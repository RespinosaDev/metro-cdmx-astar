package main.gui;

import javafx.scene.layout.Pane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import main.model.Estacion;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class MapaPanel extends Pane {
    private List<Estacion> estaciones;
    private List<Estacion> ruta;
    private Map<String, Color> coloresLineas;
    private ImageView imageView;
    
    // Elementos dibujados para la ruta actual (se borran al calcular otra)
    private final List<javafx.scene.Node> elementosRuta = new java.util.ArrayList<>();
    
    // Coordenadas fijas para las estaciones en el mapa
    private Map<String, double[]> coordenadasEstaciones;
    
    public MapaPanel() {
        setPrefSize(600, 400);
        setStyle("-fx-background-color: #f0f0f0;");
        
        inicializarColoresLineas();
        inicializarCoordenadas();
        
        // Cargar imagen del mapa (si no existe, mostrar mapa básico)
        cargarImagenMapa();
        
        // Cargar estaciones
        cargarEstaciones();
    }
    
    private void inicializarColoresLineas() {
        coloresLineas = new HashMap<>();
        coloresLineas.put("L1", Color.web("#8B4513"));  // Granate
        coloresLineas.put("L3", Color.web("#90EE90"));  // Verde claro
        coloresLineas.put("L7", Color.web("#FFA500"));  // Naranja
        coloresLineas.put("L9", Color.web("#A52A2A"));  // Marrón
        coloresLineas.put("L12", Color.web("#006400")); // Verde oscuro
    }
    
    private void inicializarCoordenadas() {
        coordenadasEstaciones = new HashMap<>();
        
        // Coordenadas aproximadas para posicionar las estaciones en el mapa
        // Línea 1
        coordenadasEstaciones.put("Observatorio", new double[]{100, 80});
        coordenadasEstaciones.put("Tacubaya", new double[]{300, 80});
        coordenadasEstaciones.put("Balderas", new double[]{500, 80});
        
        // Línea 3
        coordenadasEstaciones.put("Universidad", new double[]{100, 300});
        coordenadasEstaciones.put("Zapata", new double[]{100, 200});
        coordenadasEstaciones.put("Centro Médico", new double[]{100, 150});
        coordenadasEstaciones.put("Juárez", new double[]{100, 100});
        
        // Línea 7
        coordenadasEstaciones.put("Barranca del Muerto", new double[]{200, 350});
        coordenadasEstaciones.put("Mixcoac", new double[]{250, 250});
        coordenadasEstaciones.put("Polanco", new double[]{450, 120});
        
        // Línea 9
        coordenadasEstaciones.put("Lázaro Cárdenas", new double[]{400, 150});
        
        // Línea 12
        coordenadasEstaciones.put("Eje Central", new double[]{550, 200});
    }
    
    private void cargarImagenMapa() {
        try {
            // Intentar cargar imagen del mapa desde recursos
            Image mapaImage = new Image(getClass().getResourceAsStream("/images/mapa_metro.png"));
            imageView = new ImageView(mapaImage);
            imageView.setFitWidth(600);
            imageView.setFitHeight(400);
            imageView.setPreserveRatio(false);
            getChildren().add(imageView);
        } catch (Exception e) {
            // Si no hay imagen, mostrar fondo blanco simple
            System.out.println("No se encontró imagen del mapa. Mostrando mapa básico.");
            setStyle("-fx-background-color: white; -fx-border-color: #ccc; -fx-border-width: 1;");
            
            // Dibujar un mapa básico
            dibujarMapaBasico();
        }
    }
    
    private void dibujarMapaBasico() {
        // Dibujar un rectángulo simple como mapa
        Text titulo = new Text(200, 30, "Mapa del Metro CDMX");
        titulo.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        getChildren().add(titulo);
        
        // Dibujar un marco
        Line marco1 = new Line(50, 50, 550, 50);
        Line marco2 = new Line(550, 50, 550, 350);
        Line marco3 = new Line(550, 350, 50, 350);
        Line marco4 = new Line(50, 350, 50, 50);
        
        marco1.setStroke(Color.GRAY);
        marco2.setStroke(Color.GRAY);
        marco3.setStroke(Color.GRAY);
        marco4.setStroke(Color.GRAY);
        
        getChildren().addAll(marco1, marco2, marco3, marco4);
    }
    
    private void cargarEstaciones() {
        try {
            // Las estaciones se cargarán desde MainWindow
        } catch (Exception e) {
            System.err.println("Error cargando estaciones: " + e.getMessage());
        }
    }
    
    public void setEstaciones(List<Estacion> estaciones) {
        this.estaciones = estaciones;
        dibujarEstacionesYLíneas();
    }
    
    public void setRuta(List<Estacion> ruta) {
        this.ruta = ruta;
        dibujarRuta();
    }
    
    private void dibujarEstacionesYLíneas() {
        if (estaciones == null || estaciones.isEmpty()) {
            return;
        }
        
        // Dibujar líneas del metro
        dibujarLineasMetro();
        
        // Dibujar estaciones
        dibujarEstaciones();
    }
    
    private void dibujarLineasMetro() {
        // Línea 1
        dibujarLinea("Observatorio", "Tacubaya", "L1");
        dibujarLinea("Tacubaya", "Balderas", "L1");
        
        // Línea 3
        dibujarLinea("Universidad", "Zapata", "L3");
        dibujarLinea("Zapata", "Centro Médico", "L3");
        dibujarLinea("Centro Médico", "Juárez", "L3");
        
        // Línea 7
        dibujarLinea("Barranca del Muerto", "Mixcoac", "L7");
        dibujarLinea("Mixcoac", "Tacubaya", "L7");
        dibujarLinea("Tacubaya", "Polanco", "L7");
        
        // Línea 9
        dibujarLinea("Tacubaya", "Centro Médico", "L9");
        dibujarLinea("Centro Médico", "Lázaro Cárdenas", "L9");
        
        // Línea 12
        dibujarLinea("Mixcoac", "Zapata", "L12");
        dibujarLinea("Zapata", "Eje Central", "L12");
    }
    
    private void dibujarLinea(String estacion1, String estacion2, String linea) {
        double[] coord1 = coordenadasEstaciones.get(estacion1);
        double[] coord2 = coordenadasEstaciones.get(estacion2);
        
        if (coord1 != null && coord2 != null) {
            Line line = new Line(coord1[0], coord1[1], coord2[0], coord2[1]);
            Color color = coloresLineas.get(linea);
            line.setStroke(color);
            line.setStrokeWidth(3);
            getChildren().add(line);
        }
    }
    
    private void dibujarEstaciones() {
        for (Estacion estacion : estaciones) {
            double[] coord = coordenadasEstaciones.get(estacion.getNombre());
            if (coord != null) {
                double x = coord[0];
                double y = coord[1];
                
                // Círculo de la estación
                Circle circle = new Circle(x, y, 5);
                Color color = coloresLineas.getOrDefault(estacion.getLinea(), Color.BLACK);
                circle.setFill(color);
                circle.setStroke(Color.BLACK);
                circle.setStrokeWidth(1);
                
                // Texto con el nombre (solo para estaciones principales)
                if (esEstacionPrincipal(estacion.getNombre())) {
                    Text text = new Text(x + 8, y - 8, estacion.getNombre());
                    text.setStyle("-fx-font-size: 9px; -fx-fill: #333;");
                    getChildren().add(text);
                }
                
                getChildren().add(circle);
            }
        }
    }
    
    private void dibujarRuta() {
        getChildren().removeAll(elementosRuta);
        elementosRuta.clear();
        
        if (ruta == null || ruta.size() < 2) return;
        
        // Dibujar línea de la ruta
        for (int i = 0; i < ruta.size() - 1; i++) {
            Estacion est1 = ruta.get(i);
            Estacion est2 = ruta.get(i + 1);
            
            double[] coord1 = coordenadasEstaciones.get(est1.getNombre());
            double[] coord2 = coordenadasEstaciones.get(est2.getNombre());
            
            if (coord1 != null && coord2 != null) {
                Line routeLine = new Line(coord1[0], coord1[1], coord2[0], coord2[1]);
                routeLine.setStroke(Color.RED);
                routeLine.setStrokeWidth(4);
                elementosRuta.add(routeLine);
            }
        }
        
        // Resaltar estaciones de la ruta
        for (Estacion estacion : ruta) {
            double[] coord = coordenadasEstaciones.get(estacion.getNombre());
            if (coord != null) {
                double x = coord[0];
                double y = coord[1];
                
                Circle highlightCircle = new Circle(x, y, 8);
                highlightCircle.setFill(Color.TRANSPARENT);
                highlightCircle.setStroke(Color.RED);
                highlightCircle.setStrokeWidth(2);
                
                elementosRuta.add(highlightCircle);
                
                // Mostrar nombre de todas las estaciones de la ruta
                Text text = new Text(x + 10, y + 5, estacion.getNombre());
                text.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-fill: red;");
                elementosRuta.add(text);
            }
        }
        
        getChildren().addAll(elementosRuta);
    }
    
    private boolean esEstacionPrincipal(String nombre) {
        String[] principales = {"Tacubaya", "Mixcoac", "Balderas", "Centro Médico", "Observatorio"};
        for (String principal : principales) {
            if (nombre.equals(principal)) {
                return true;
            }
        }
        return false;
    }
}