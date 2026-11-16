package main.model;

public class Conexion {
    private Estacion origen;
    private Estacion destino;
    private double distancia;
    private int tiempoMinutos;
    private boolean esTransbordo;
    
    public Conexion(Estacion origen, Estacion destino, double distancia, int tiempoMinutos) {
        this.origen = origen;
        this.destino = destino;
        this.distancia = distancia;
        this.tiempoMinutos = tiempoMinutos;
        this.esTransbordo = false;
    }
    
    // Getters
    public Estacion getOrigen() { return origen; }
    public Estacion getDestino() { return destino; }
    public double getDistancia() { return distancia; }
    public int getTiempoMinutos() { return tiempoMinutos; }
    public boolean isEsTransbordo() { return esTransbordo; }
    public void setEsTransbordo(boolean esTransbordo) { this.esTransbordo = esTransbordo; }
}