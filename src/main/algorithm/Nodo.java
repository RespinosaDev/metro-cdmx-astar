package main.algorithm;

import main.model.Estacion;

public class Nodo implements Comparable<Nodo> {
    private Estacion estacion;
    private Nodo padre;
    private double g; // costo acumulado
    private double h; // heurística
    private double f; // f = g + h
    
    public Nodo(Estacion estacion, Nodo padre, double g, double h) {
        this.estacion = estacion;
        this.padre = padre;
        this.g = g;
        this.h = h;
        this.f = g + h;
    }
    
    // Getters
    public Estacion getEstacion() { return estacion; }
    public Nodo getPadre() { return padre; }
    public double getG() { return g; }
    public double getH() { return h; }
    public double getF() { return f; }
    
    @Override
    public int compareTo(Nodo otro) {
        return Double.compare(this.f, otro.f);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Nodo nodo = (Nodo) obj;
        return estacion.equals(nodo.estacion);
    }
    
    @Override
    public int hashCode() {
        return estacion.hashCode();
    }
}