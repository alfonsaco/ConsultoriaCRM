package com.practicas.consultoriacrm;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class Login extends Application {

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/practicas/consultoriacrm/login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 900, 600);

        // Icono de la APP
        try {
            stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResource("/images/icono.png")).toExternalForm()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Estilos
        String css = Objects.requireNonNull(this.getClass().getResource("estilos/login.css")).toExternalForm();
        scene.getStylesheets().add(css);

        LoginController controller = fxmlLoader.getController();
        controller.setScene(scene);

        stage.setScene(scene);
        stage.setTitle("CRM - Login");
        stage.setResizable(false);
        stage.show();
    }
}
