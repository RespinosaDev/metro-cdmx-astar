package main.gui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import main.model.Estacion;
import main.algorithm.AStar;
import main.data.DataLoader;
import java.util.List;

public class MainWindow extends Application {
    private ComboBox<String> cbOrigen;
    private ComboBox<String> cbDestino;
    private Button btnCalcular;
    private TextArea taResultado;
    private CheckBox chkEvitarEscaleras;
    private CheckBox chkMinimoTransbordos;
    private List<Estacion> estaciones;
    private MapaPanel mapaPanel;
    
    @Override
    public void start(Stage primaryStage) {
        try {
            initComponents();
            setupLayout(primaryStage);
            setupListeners();
            loadEstaciones();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error inicializando la aplicación: " + e.getMessage());
        }
    }
    
    private void initComponents() {
        cbOrigen = new ComboBox<>();
        cbDestino = new ComboBox<>();
        btnCalcular = new Button("Calcular Ruta Óptima");
        taResultado = new TextArea();
        taResultado.setEditable(false);
        taResultado.setStyle("-fx-font-family: 'Consolas', 'Monospaced'; -fx-font-size: 12px;");
        taResultado.setPrefRowCount(10);
        
        chkEvitarEscaleras = new CheckBox("Evitar escaleras (accesibilidad)");
        chkMinimoTransbordos = new CheckBox("Minimizar transbordos");
        
        mapaPanel = new MapaPanel();
    }
    
    private void setupLayout(Stage primaryStage) {
        primaryStage.setTitle("Metro CDMX - Calculador de Rutas Óptimas");
        
        // Panel principal dividido
        SplitPane splitPane = new SplitPane();
        splitPane.setDividerPositions(0.4); // 40% controles, 60% mapa
        
        // Panel izquierdo - Controles
        VBox panelControles = new VBox(15);
        panelControles.setPadding(new Insets(20));
        panelControles.setStyle("-fx-background-color: #f5f5f5;");
        panelControles.setPrefWidth(350);
        
        // Título
        Label titulo = new Label("Sistema de Navegación Metro CDMX");
        titulo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        // Panel de controles
        GridPane gridPanel = new GridPane();
        gridPanel.setHgap(15);
        gridPanel.setVgap(10);
        gridPanel.setPadding(new Insets(15));
        gridPanel.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-border-radius: 5;");
        
        gridPanel.add(new Label("Estación Origen:"), 0, 0);
        gridPanel.add(cbOrigen, 1, 0);
        gridPanel.add(new Label("Estación Destino:"), 0, 1);
        gridPanel.add(cbDestino, 1, 1);
        gridPanel.add(chkEvitarEscaleras, 0, 2);
        gridPanel.add(chkMinimoTransbordos, 1, 2);
        
        cbOrigen.setPrefWidth(200);
        cbDestino.setPrefWidth(200);
        
        // Área de resultados
        VBox resultBox = new VBox(5);
        Label resultadoLabel = new Label("Ruta Calculada:");
        resultadoLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        taResultado.setPrefHeight(200);
        taResultado.setStyle("-fx-control-inner-background: #f8f9fa; -fx-border-color: #ddd;");
        
        resultBox.getChildren().addAll(resultadoLabel, taResultado);
        VBox.setVgrow(resultBox, Priority.ALWAYS);
        
        // Botón
        btnCalcular.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;");
        btnCalcular.setOnMouseEntered(e -> btnCalcular.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;"));
        btnCalcular.setOnMouseExited(e -> btnCalcular.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;"));
        
        HBox buttonBox = new HBox(btnCalcular);
        buttonBox.setAlignment(javafx.geometry.Pos.CENTER);
        
        panelControles.getChildren().addAll(titulo, gridPanel, resultBox, buttonBox);
        
        // Panel derecho - Mapa
        VBox panelMapa = new VBox();
        Label mapaLabel = new Label("Mapa del Metro CDMX");
        mapaLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 10;");
        
        VBox.setVgrow(mapaPanel, Priority.ALWAYS);
        panelMapa.getChildren().addAll(mapaLabel, mapaPanel);
        
        // Configurar split pane
        splitPane.getItems().addAll(panelControles, panelMapa);
        
        Scene scene = new Scene(splitPane, 1000, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    private void setupListeners() {
        btnCalcular.setOnAction(e -> calcularRuta());
    }
    
    private void loadEstaciones() {
        try {
            estaciones = DataLoader.cargarEstaciones();
            System.out.println("Estaciones cargadas: " + estaciones.size());
            
            for (Estacion estacion : estaciones) {
                String display = estacion.getNombre() + " (" + estacion.getLinea() + ")";
                cbOrigen.getItems().add(display);
                cbDestino.getItems().add(display);
            }
            
            if (!estaciones.isEmpty()) {
                cbOrigen.getSelectionModel().select(0);
                if (estaciones.size() > 1) {
                    cbDestino.getSelectionModel().select(1);
                }
            }
            
            // Pasar las estaciones al mapa
            if (mapaPanel != null) {
                mapaPanel.setEstaciones(estaciones);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error cargando estaciones: " + e.getMessage());
        }
    }
    
    private void calcularRuta() {
        try {
            String origenSel = cbOrigen.getValue();
            String destinoSel = cbDestino.getValue();
            
            if (origenSel == null || destinoSel == null) {
                showError("Seleccione estación origen y destino");
                return;
            }
            
            Estacion origen = findEstacionByDisplayName(origenSel);
            Estacion destino = findEstacionByDisplayName(destinoSel);
            
            if (origen == null || destino == null) {
                showError("No se pudieron encontrar las estaciones");
                return;
            }
            
            if (origen.equals(destino)) {
                showError("Las estaciones origen y destino son iguales");
                return;
            }
            
            boolean evitarEscaleras = chkEvitarEscaleras.isSelected();
            boolean minimoTransbordos = chkMinimoTransbordos.isSelected();
            
            AStar aStar = new AStar();
            List<Estacion> ruta = aStar.encontrarRuta(origen, destino, evitarEscaleras, minimoTransbordos);
            
            mostrarResultado(ruta, origen, destino, aStar.calcularTiempoRuta(ruta));
            
            // Actualizar el mapa con la ruta
            if (mapaPanel != null) {
                mapaPanel.setRuta(ruta);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error calculando ruta: " + e.getMessage());
        }
    }
    
    private void mostrarResultado(List<Estacion> ruta, Estacion origen, Estacion destino, int tiempoMinutos) {
        if (ruta == null || ruta.isEmpty()) {
            taResultado.setText("No se encontró ruta entre " + origen.getNombre() + " y " + destino.getNombre());
            return;
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("RUTA ÓPTIMA ENCONTRADA:\n");
        sb.append("=======================\n\n");
        sb.append("Origen: ").append(origen.getNombre()).append(" (").append(origen.getLinea()).append(")\n");
        sb.append("Destino: ").append(destino.getNombre()).append(" (").append(destino.getLinea()).append(")\n");
        sb.append("Total estaciones: ").append(ruta.size()).append("\n\n");
        sb.append("DETALLE DE LA RUTA:\n");
        sb.append("-------------------\n");
        
        String lineaActual = ruta.get(0).getLinea();
        int transbordos = 0;
        
        for (int i = 0; i < ruta.size(); i++) {
            Estacion estacion = ruta.get(i);
            
            if (i > 0 && !estacion.getLinea().equals(lineaActual)) {
                sb.append("  └── TRANSBORDO a Línea ").append(estacion.getLinea()).append("\n");
                lineaActual = estacion.getLinea();
                transbordos++;
            }
            
            sb.append(String.format("%2d. %s (%s)\n", i + 1, estacion.getNombre(), estacion.getLinea()));
        }
        
        sb.append("\nRESUMEN:\n");
        sb.append("--------\n");
        sb.append("• Estaciones totales: ").append(ruta.size()).append("\n");
        sb.append("• Transbordos: ").append(transbordos).append("\n");
        sb.append("• Tiempo estimado: ").append(tiempoMinutos).append(" minutos\n");
        
        taResultado.setText(sb.toString());
    }
    
    private Estacion findEstacionByDisplayName(String displayName) {
        for (Estacion estacion : estaciones) {
            String estacionDisplay = estacion.getNombre() + " (" + estacion.getLinea() + ")";
            if (estacionDisplay.equals(displayName)) {
                return estacion;
            }
        }
        return null;
    }
    
    private void showError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}