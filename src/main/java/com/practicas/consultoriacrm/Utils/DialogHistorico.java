package com.practicas.consultoriacrm.Utils;

import com.practicas.consultoriacrm.CRMApp;
import com.practicas.consultoriacrm.LoginController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class DialogHistorico extends Application {

    private Stage stage;

    @Override
    public void start(Stage primaryStage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(CRMApp.class.getResource("dialog-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 900, 600);

        // Icono de la APP
        try {
            stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResource("/images/icono.png")).toExternalForm()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        String css = Objects.requireNonNull(this.getClass().getResource("estilos/historico.css")).toExternalForm();
        scene.getStylesheets().add(css);

        LoginController controller = fxmlLoader.getController();
        controller.setScene(scene);

        stage.setScene(scene);
        stage.setTitle("CRM - Historico");
        stage.setResizable(false);
        stage.show();
    }
}
