package main.model;

public class Estacion {
    private String id;
    private String nombre;
    private String linea;
    private double latitud;
    private double longitud;
    private boolean tieneAscensor;
    private boolean tieneEscaleras;
    
    public Estacion(String id, String nombre, String linea, double latitud, double longitud) {
        this.id = id;
        this.nombre = nombre;
        this.linea = linea;
        this.latitud = latitud;
        this.longitud = longitud;
    }
    
    // Getters y Setters
    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public String getLinea() { return linea; }
    public double getLatitud() { return latitud; }
    public double getLongitud() { return longitud; }
    public boolean isTieneAscensor() { return tieneAscensor; }
    public void setTieneAscensor(boolean tieneAscensor) { this.tieneAscensor = tieneAscensor; }
    public boolean isTieneEscaleras() { return tieneEscaleras; }
    public void setTieneEscaleras(boolean tieneEscaleras) { this.tieneEscaleras = tieneEscaleras; }
    
    @Override
    public String toString() {
        return nombre + " (" + linea + ")";
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Estacion estacion = (Estacion) obj;
        return id.equals(estacion.id);
    }
    
    @Override
    public int hashCode() {
        return id.hashCode();
    }
}