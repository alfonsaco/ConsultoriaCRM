package com.practicas.consultoriacrm;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class CRMApp extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(CRMApp.class.getResource("/com/practicas/consultoriacrm/main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 640);
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResource("/images/icono.png")).toExternalForm()));

        // Conectar con el CSS
        String css = Objects.requireNonNull(this.getClass().getResource("estilos/estilos.css")).toExternalForm();
        scene.getStylesheets().add(css);

        // Obtener el controlador y pasarle la referencia de la Scene
        MainScreenController controller = fxmlLoader.getController();
        controller.setScene(scene);

        stage.setScene(scene);
        stage.setTitle("CRM - Inicio");
        stage.show();
    }
}