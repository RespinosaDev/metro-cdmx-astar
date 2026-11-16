module METRO_CDMX {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.base;
	requires javafx.graphics;
    
    // Exportar los paquetes que JavaFX necesita acceder
    exports main.gui to javafx.graphics;
    exports main to javafx.graphics;
    
    // Abrir el paquete para reflexión
    opens main.gui to javafx.fxml;
}