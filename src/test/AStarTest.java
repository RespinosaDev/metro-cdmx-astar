package test;

import main.algorithm.AStar;
import main.algorithm.Heuristica;
import main.model.Conexion;
import main.model.Estacion;
import main.data.DataLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

public class AStarTest {
    private List<Estacion> estaciones;
    private AStar aStar;
    
    @BeforeEach
    public void setUp() {
        estaciones = DataLoader.cargarEstaciones();
        aStar = new AStar();
    }
    
    @Test
    public void testRutaDirectaMismaLinea() {
        Estacion origen = findEstacionByNombre("Observatorio");
        Estacion destino = findEstacionByNombre("Tacubaya");
        
        assertNotNull(origen, "Estación origen no encontrada");
        assertNotNull(destino, "Estación destino no encontrada");
        
        List<Estacion> ruta = aStar.encontrarRuta(origen, destino, false, false);
        
        assertNotNull(ruta, "La ruta no debería ser null");
        assertFalse(ruta.isEmpty(), "La ruta no debería estar vacía");
        assertTrue(ruta.contains(origen), "La ruta debe contener la estación origen");
        assertTrue(ruta.contains(destino), "La ruta debe contener la estación destino");
    }
    
    @Test
    public void testRutaConTransbordo() {
        Estacion origen = findEstacionByNombre("Observatorio");
        Estacion destino = findEstacionByNombre("Mixcoac");
        
        List<Estacion> ruta = aStar.encontrarRuta(origen, destino, false, false);
        
        assertNotNull(ruta, "Debería encontrar ruta con transbordo");
        assertTrue(ruta.size() >= 3, "La ruta debe tener al menos 3 estaciones");
    }
    
    @Test
    public void testRutaMismasEstaciones() {
        Estacion estacion = findEstacionByNombre("Tacubaya");
        
        List<Estacion> ruta = aStar.encontrarRuta(estacion, estacion, false, false);
        
        assertNotNull(ruta, "La ruta no debería ser null");
        assertEquals(1, ruta.size(), "La ruta debería tener solo una estación");
        assertEquals(estacion, ruta.get(0), "La ruta debería contener la misma estación");
    }
    
    @Test
    public void testRutaNoEncontrada() {
        // Crear una estación que no está conectada
        Estacion estacionAislada = new Estacion("AISLADA", "Estación Aislada", "L99", 0, 0);
        Estacion destino = findEstacionByNombre("Observatorio");
        
        List<Estacion> ruta = aStar.encontrarRuta(estacionAislada, destino, false, false);
        
        assertNull(ruta, "La ruta debería ser null cuando no hay conexión");
    }
    
    @Test
    public void testRutaSentidoContrario() {
        // Las vías son de doble sentido: la vuelta también debe encontrarse
        Estacion origen = DataLoader.getEstacionById("L7-MIX");
        Estacion destino = DataLoader.getEstacionById("L1-OBS");

        List<Estacion> ruta = aStar.encontrarRuta(origen, destino, false, false);

        assertNotNull(ruta, "Debería encontrar ruta de vuelta Mixcoac -> Observatorio");
        assertEquals(origen, ruta.get(0));
        assertEquals(destino, ruta.get(ruta.size() - 1));
    }

    @Test
    public void testTiempoRutaDirecta() {
        Estacion origen = DataLoader.getEstacionById("L1-OBS");
        Estacion destino = DataLoader.getEstacionById("L1-TAC");

        List<Estacion> ruta = aStar.encontrarRuta(origen, destino, false, false);

        assertEquals(2, ruta.size());
        assertEquals(4, aStar.calcularTiempoRuta(ruta), "Observatorio -> Tacubaya son 4 minutos");
    }

    @Test
    public void testHeuristicaConsistente() {
        // h(n) <= coste(n, n') + h(n') para todo tramo y todo objetivo
        for (Estacion objetivo : estaciones) {
            for (Conexion c : DataLoader.getConexiones()) {
                double hOrigen = Heuristica.calcularHeuristica(c.getOrigen(), objetivo);
                double hDestino = Heuristica.calcularHeuristica(c.getDestino(), objetivo);
                assertTrue(hOrigen <= c.getTiempoMinutos() + hDestino + 1e-9,
                        "Heurística no consistente en " + c.getOrigen() + " -> " + c.getDestino());
            }
        }
    }

    @Test
    public void testRutaOptimaIgualQueDijkstra() {
        // Sin preferencias, A* debe dar el mismo tiempo mínimo que Dijkstra en todos los pares
        for (Estacion origen : estaciones) {
            Map<Estacion, Integer> distancias = dijkstra(origen);
            for (Estacion destino : estaciones) {
                List<Estacion> ruta = aStar.encontrarRuta(origen, destino, false, false);
                assertNotNull(ruta, "Sin ruta de " + origen + " a " + destino);
                assertEquals(distancias.get(destino).intValue(), aStar.calcularTiempoRuta(ruta),
                        "Ruta no óptima de " + origen + " a " + destino);
            }
        }
    }

    @Test
    public void testMinimizarTransbordos() {
        Estacion origen = DataLoader.getEstacionById("L3-UNI");
        Estacion destino = DataLoader.getEstacionById("L1-OBS");

        List<Estacion> normal = aStar.encontrarRuta(origen, destino, false, false);
        List<Estacion> pocosTransbordos = aStar.encontrarRuta(origen, destino, false, true);

        assertNotNull(normal);
        assertNotNull(pocosTransbordos);
        assertTrue(contarTransbordos(pocosTransbordos) <= contarTransbordos(normal));
    }

    private int contarTransbordos(List<Estacion> ruta) {
        int n = 0;
        for (int i = 1; i < ruta.size(); i++) {
            if (!ruta.get(i).getLinea().equals(ruta.get(i - 1).getLinea())) n++;
        }
        return n;
    }

    private Map<Estacion, Integer> dijkstra(Estacion origen) {
        Map<Estacion, Integer> dist = new HashMap<>();
        PriorityQueue<Map.Entry<Estacion, Integer>> cola =
                new PriorityQueue<>(Map.Entry.comparingByValue());
        dist.put(origen, 0);
        cola.add(new AbstractMap.SimpleEntry<>(origen, 0));
        while (!cola.isEmpty()) {
            Map.Entry<Estacion, Integer> actual = cola.poll();
            if (actual.getValue() > dist.get(actual.getKey())) continue;
            for (Conexion c : DataLoader.getConexiones()) {
                if (!c.getOrigen().equals(actual.getKey())) continue;
                int nd = actual.getValue() + c.getTiempoMinutos();
                if (nd < dist.getOrDefault(c.getDestino(), Integer.MAX_VALUE)) {
                    dist.put(c.getDestino(), nd);
                    cola.add(new AbstractMap.SimpleEntry<>(c.getDestino(), nd));
                }
            }
        }
        return dist;
    }
    
    private Estacion findEstacionByNombre(String nombre) {
        return estaciones.stream()
                .filter(e -> e.getNombre().equals(nombre))
                .findFirst()
                .orElse(null);
    }
}