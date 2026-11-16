package test;

import main.data.DataLoader;
import main.model.Estacion;
import main.model.Conexion;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class DataLoaderTest {
    
    @Test
    public void testCargarEstaciones() {
        List<Estacion> estaciones = DataLoader.cargarEstaciones();
        
        assertNotNull(estaciones, "La lista de estaciones no debería ser null");
        assertFalse(estaciones.isEmpty(), "La lista de estaciones no debería estar vacía");
        
        // Verificar que existen estaciones clave
        assertTrue(estaciones.stream().anyMatch(e -> e.getNombre().equals("Tacubaya")),
                "Debería existir la estación Tacubaya");
        assertTrue(estaciones.stream().anyMatch(e -> e.getNombre().equals("Mixcoac")),
                "Debería existir la estación Mixcoac");
    }
    
    @Test
    public void testCargarConexiones() {
        List<Conexion> conexiones = DataLoader.getConexiones();
        
        assertNotNull(conexiones, "La lista de conexiones no debería ser null");
        assertFalse(conexiones.isEmpty(), "La lista de conexiones no debería estar vacía");
        
        // Verificar que existen conexiones y transbordos
        long transbordos = conexiones.stream().filter(Conexion::isEsTransbordo).count();
        assertTrue(transbordos > 0, "Debería haber transbordos definidos");
    }
    
    @Test
    public void testGetEstacionById() {
        Estacion estacion = DataLoader.getEstacionById("L1-OBS");
        
        assertNotNull(estacion, "Debería encontrar estación por ID");
        assertEquals("Observatorio", estacion.getNombre(), "El nombre debería coincidir");
        assertEquals("L1", estacion.getLinea(), "La línea debería coincidir");
    }
}