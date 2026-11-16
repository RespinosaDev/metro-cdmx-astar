package main.algorithm;

import main.data.DataLoader;
import main.model.Conexion;
import main.model.Estacion;

/**
 * Heurística h(n) para A*: tiempo mínimo (en minutos) que tardaría el metro
 * en recorrer en línea recta la distancia entre dos estaciones.
 *
 * Se expresa en minutos para que esté en la misma escala que el coste real g(n).
 * La velocidad usada es la máxima "en línea recta" observada en la red
 * (distancia geográfica / tiempo de cada tramo), así que h(n) nunca sobreestima
 * el coste real: la heurística es admisible y consistente.
 */
public class Heuristica {
    private static final double RADIO_TIERRA_KM = 6371.0;

    private static double velocidadMaximaKmPorMin = -1;

    // Distancia geográfica (fórmula de haversine) en kilómetros
    public static double distanciaKm(Estacion a, Estacion b) {
        double lat1 = Math.toRadians(a.getLatitud());
        double lat2 = Math.toRadians(b.getLatitud());
        double dLat = lat2 - lat1;
        double dLon = Math.toRadians(b.getLongitud() - a.getLongitud());

        double h = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                 + Math.cos(lat1) * Math.cos(lat2) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return 2 * RADIO_TIERRA_KM * Math.asin(Math.sqrt(h));
    }

    // Velocidad máxima de la red (km/min), calculada una sola vez a partir de los tramos
    static double getVelocidadMaxima() {
        if (velocidadMaximaKmPorMin < 0) {
            double max = 0;
            for (Conexion c : DataLoader.getConexiones()) {
                if (c.getTiempoMinutos() > 0) {
                    max = Math.max(max, distanciaKm(c.getOrigen(), c.getDestino()) / c.getTiempoMinutos());
                }
            }
            velocidadMaximaKmPorMin = max > 0 ? max : 1.0;
        }
        return velocidadMaximaKmPorMin;
    }

    // h(n): minutos estimados (por debajo del real) hasta el objetivo
    public static double calcularHeuristica(Estacion actual, Estacion objetivo) {
        return distanciaKm(actual, objetivo) / getVelocidadMaxima();
    }
}
