package main.algorithm;

import main.model.Estacion;
import main.model.Conexion;
import main.data.DataLoader;
import java.util.*;

public class AStar {
    private List<Conexion> conexiones;
    
    public AStar() {
        this.conexiones = DataLoader.getConexiones();
    }
    
    public List<Estacion> encontrarRuta(Estacion origen, Estacion destino, 
                                      boolean evitarEscaleras, boolean minimoTransbordos) {
        // Si origen y destino son la misma estación
        if (origen.equals(destino)) {
            return Arrays.asList(origen);
        }
        
        // Priority queue para nodos abiertos, ordenados por f(n) = g(n) + h(n)
        PriorityQueue<Nodo> abiertos = new PriorityQueue<>();
        
        // Map para nodos cerrados y para registrar costos
        Map<Estacion, Nodo> cerrados = new HashMap<>();
        Map<Estacion, Double> mejoresCostos = new HashMap<>();
        
        // Inicializar con el nodo origen
        Nodo nodoInicial = new Nodo(origen, null, 0, Heuristica.calcularHeuristica(origen, destino));
        abiertos.add(nodoInicial);
        mejoresCostos.put(origen, 0.0);
        
        while (!abiertos.isEmpty()) {
            // Obtener el nodo con menor f(n)
            Nodo actual = abiertos.poll();
            
            // Si llegamos al destino, reconstruir la ruta
            if (actual.getEstacion().equals(destino)) {
                return reconstruirRuta(actual);
            }
            
            // Mover a cerrados
            cerrados.put(actual.getEstacion(), actual);
            
            // Expandir nodos vecinos
            for (Conexion conexion : obtenerConexiones(actual.getEstacion())) {
                Estacion vecino = conexion.getDestino();
                
                // Saltar si ya está en cerrados
                if (cerrados.containsKey(vecino)) {
                    continue;
                }
                
                // Calcular costo g(n) para este vecino
                double costoNuevo = actual.getG() + calcularCostoConexion(conexion, evitarEscaleras, minimoTransbordos);
                
                // Si es un camino mejor o no hemos visitado este nodo
                if (!mejoresCostos.containsKey(vecino) || costoNuevo < mejoresCostos.get(vecino)) {
                    double heuristica = Heuristica.calcularHeuristica(vecino, destino);
                    Nodo nodoVecino = new Nodo(vecino, actual, costoNuevo, heuristica);
                    
                    // Actualizar mejores costos y agregar a abiertos
                    mejoresCostos.put(vecino, costoNuevo);
                    abiertos.add(nodoVecino);
                }
            }
        }
        
        // No se encontró ruta
        return null;
    }
    
    private List<Estacion> reconstruirRuta(Nodo nodoFinal) {
        List<Estacion> ruta = new ArrayList<>();
        Nodo actual = nodoFinal;
        
        // Reconstruir desde el final hasta el inicio
        while (actual != null) {
            ruta.add(0, actual.getEstacion()); // Insertar al inicio
            actual = actual.getPadre();
        }
        
        return ruta;
    }
    
    private List<Conexion> obtenerConexiones(Estacion estacion) {
        List<Conexion> conexionesEstacion = new ArrayList<>();
        
        for (Conexion conexion : conexiones) {
            if (conexion.getOrigen().equals(estacion)) {
                conexionesEstacion.add(conexion);
            }
        }
        
        return conexionesEstacion;
    }
    
    private double calcularCostoConexion(Conexion conexion, boolean evitarEscaleras, boolean minimoTransbordos) {
        double costo = conexion.getTiempoMinutos();
        
        // Penalizar transbordos si se quiere minimizarlos
        if (minimoTransbordos && conexion.isEsTransbordo()) {
            costo += 10; // Penalización por transbordo
        }
        
        // Considerar accesibilidad
        if (evitarEscaleras) {
            Estacion destino = conexion.getDestino();
            if (destino.isTieneEscaleras() && !destino.isTieneAscensor()) {
                costo += 15; // Alta penalización si no hay alternativa accesible
            }
        }
        
        return costo;
    }
    
    /**
     * Tiempo real de la ruta en minutos: suma del tiempo de cada tramo
     * (incluidos los transbordos), sin las penalizaciones de preferencia.
     */
    public int calcularTiempoRuta(List<Estacion> ruta) {
        if (ruta == null || ruta.size() < 2) {
            return 0;
        }
        int total = 0;
        for (int i = 0; i < ruta.size() - 1; i++) {
            Estacion actual = ruta.get(i);
            Estacion siguiente = ruta.get(i + 1);
            for (Conexion conexion : obtenerConexiones(actual)) {
                if (conexion.getDestino().equals(siguiente)) {
                    total += conexion.getTiempoMinutos();
                    break;
                }
            }
        }
        return total;
    }
    
    // Método alternativo para debugging
    public void imprimirRuta(List<Estacion> ruta) {
        if (ruta == null) {
            System.out.println("No se encontró ruta");
            return;
        }
        
        System.out.println("Ruta encontrada (" + ruta.size() + " estaciones):");
        for (int i = 0; i < ruta.size(); i++) {
            Estacion estacion = ruta.get(i);
            System.out.printf("%2d. %-20s (%s)%n", i + 1, estacion.getNombre(), estacion.getLinea());
        }
    }
}