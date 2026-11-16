package main.data;

import main.model.Estacion;
import main.model.Conexion;
import java.util.*;
import java.io.*;

public class DataLoader {
    private static final Map<String, Estacion> estacionesMap = new HashMap<>();
    private static final List<Conexion> conexiones = new ArrayList<>();
    
    public static List<Estacion> cargarEstaciones() {
        if (!estacionesMap.isEmpty()) {
            return new ArrayList<>(estacionesMap.values());
        }
        
        // Datos hardcodeados según el enunciado
        // Línea 1 (granate)
        agregarEstacion("L1-OBS", "Observatorio", "L1", 19.3972, -99.2000);
        agregarEstacion("L1-TAC", "Tacubaya", "L1", 19.4025, -99.1875);
        agregarEstacion("L1-BAL", "Balderas", "L1", 19.4278, -99.1472);
        
        // Línea 3 (verde claro)
        agregarEstacion("L3-UNI", "Universidad", "L3", 19.3311, -99.1819);
        agregarEstacion("L3-CEM", "Centro Médico", "L3", 19.4050, -99.1556);
        agregarEstacion("L3-ZAP", "Zapata", "L3", 19.3850, -99.1639);
        agregarEstacion("L3-JUA", "Juárez", "L3", 19.4322, -99.1508);
        
        // Línea 7 (naranja)
        agregarEstacion("L7-BAR", "Barranca del Muerto", "L7", 19.3639, -99.1889);
        agregarEstacion("L7-MIX", "Mixcoac", "L7", 19.3725, -99.1858);
        agregarEstacion("L7-TAC", "Tacubaya", "L7", 19.4025, -99.1875);
        agregarEstacion("L7-POL", "Polanco", "L7", 19.4347, -99.1958);
        
        // Línea 9 (marrón)
        agregarEstacion("L9-TAC", "Tacubaya", "L9", 19.4025, -99.1875);
        agregarEstacion("L9-CEM", "Centro Médico", "L9", 19.4050, -99.1556);
        agregarEstacion("L9-LAZ", "Lázaro Cárdenas", "L9", 19.4150, -99.1400);
        
        // Línea 12 (verde oscuro)
        agregarEstacion("L12-MIX", "Mixcoac", "L12", 19.3725, -99.1858);
        agregarEstacion("L12-ZAP", "Zapata", "L12", 19.3850, -99.1639);
        agregarEstacion("L12-EJE", "Eje Central", "L12", 19.4200, -99.1350);
        
        crearConexiones();
        
        return new ArrayList<>(estacionesMap.values());
    }
    
    private static void agregarEstacion(String id, String nombre, String linea, double lat, double lon) {
        Estacion estacion = new Estacion(id, nombre, linea, lat, lon);
        estacionesMap.put(id, estacion);
    }
    
    private static void crearConexiones() {
        // Conexiones Línea 1
        crearConexion("L1-OBS", "L1-TAC", 2.5, 4);
        crearConexion("L1-TAC", "L1-BAL", 3.2, 6);
        
        // Conexiones Línea 3
        crearConexion("L3-UNI", "L3-ZAP", 2.8, 5);
        crearConexion("L3-ZAP", "L3-CEM", 1.8, 3);
        crearConexion("L3-CEM", "L3-JUA", 2.2, 4);
        
        // Conexiones Línea 7
        crearConexion("L7-BAR", "L7-MIX", 1.5, 3);
        crearConexion("L7-MIX", "L7-TAC", 2.0, 4);
        crearConexion("L7-TAC", "L7-POL", 3.5, 7);
        
        // Conexiones Línea 9
        crearConexion("L9-TAC", "L9-CEM", 2.8, 5);
        crearConexion("L9-CEM", "L9-LAZ", 1.5, 3);
        
        // Conexiones Línea 12
        crearConexion("L12-MIX", "L12-ZAP", 1.7, 3);
        crearConexion("L12-ZAP", "L12-EJE", 2.3, 5);
        
        // Transbordos (conexiones especiales)
        crearTransbordo("L7-MIX", "L12-MIX", 0.1, 2); // Mixcoac
        crearTransbordo("L3-ZAP", "L12-ZAP", 0.1, 2); // Zapata
        crearTransbordo("L1-TAC", "L7-TAC", 0.2, 3); // Tacubaya L1-L7
        crearTransbordo("L1-TAC", "L9-TAC", 0.2, 3); // Tacubaya L1-L9
        crearTransbordo("L7-TAC", "L9-TAC", 0.1, 2); // Tacubaya L7-L9
        crearTransbordo("L3-CEM", "L9-CEM", 0.1, 2); // Centro Médico
        crearTransbordo("L1-BAL", "L3-JUA", 0.3, 4); // Balderas-Juárez
    }
    
    private static void crearConexion(String idOrigen, String idDestino, double distancia, int tiempo) {
        Estacion origen = estacionesMap.get(idOrigen);
        Estacion destino = estacionesMap.get(idDestino);
        
        if (origen != null && destino != null) {
            // El metro circula en ambos sentidos: se añaden las dos aristas
            conexiones.add(new Conexion(origen, destino, distancia, tiempo));
            conexiones.add(new Conexion(destino, origen, distancia, tiempo));
        }
    }
    
    private static void crearTransbordo(String idOrigen, String idDestino, double distancia, int tiempo) {
        Estacion origen = estacionesMap.get(idOrigen);
        Estacion destino = estacionesMap.get(idDestino);
        
        if (origen != null && destino != null) {
            // Los transbordos también se pueden hacer en ambos sentidos
            Conexion ida = new Conexion(origen, destino, distancia, tiempo);
            Conexion vuelta = new Conexion(destino, origen, distancia, tiempo);
            ida.setEsTransbordo(true);
            vuelta.setEsTransbordo(true);
            conexiones.add(ida);
            conexiones.add(vuelta);
        }
    }
    
    public static List<Conexion> getConexiones() {
        if (conexiones.isEmpty()) {
            cargarEstaciones(); // Asegurar que las conexiones estén cargadas
        }
        return new ArrayList<>(conexiones);
    }
    
    public static Estacion getEstacionById(String id) {
        return estacionesMap.get(id);
    }
}