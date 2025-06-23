package com.practicas.consultoriacrm.Utils;

import com.practicas.consultoriacrm.MainScreenController;
import com.practicas.consultoriacrm.Tabs.TabGas;
import com.practicas.consultoriacrm.Tabs.TabLuz;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

public class MetodosTabs {
    private final Conexion conexion;
    private final VerificarFormularios verificar;

    public MetodosTabs() {
        this.conexion = Conexion.getInstance();
        verificar = new VerificarFormularios();
    }

    // ---------------------- MÉTODOS CUPS PARA LAS CLASES TABLUZ Y TABGAS -----------------------------
    public void auxiliarAgregarCups(String textoInputCupDecriptado, String tipoBoton, int fila, int columna, int posicion, String icono, int numCups, int idCliente, String styleClass, String tipoTabla, GridPane gridPane, TabGas tabGas, TabLuz tabLuz) {
        HBox hBox = new HBox(0);

        Text text;
        if (tipoBoton.equals("borrar")) {
            text = new Text("CUPS " + (posicion + 1));
        } else {
            text = new Text("NUEVO: ");
        }

        text.setStyle("-fx-fill: #4d4d4d; -fx-font-weight: bold;");
        TextField textField = new TextField();
        textField.setPrefWidth(200);
        textField.getStyleClass().add("formulariosCliente");
        try {
            textField.setText(Encriptar.decrypt(textoInputCupDecriptado).toUpperCase());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        Button boton = new Button();
        boton.getStyleClass().add(styleClass);
        boton.setText("");

        // PONER ICONOS
        try {
            Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/" + icono)));
            ImageView imageView = new ImageView(icon);
            imageView.setFitWidth(12);
            imageView.setFitHeight(12);
            boton.setGraphic(imageView);

        } catch (Exception e) {
            boton.setText("+");
        }

        // Caso de botón de borrar
        if (tipoBoton.equals("borrar")) {
            textField.setEditable(false);
            textField.setCursor(Cursor.HAND);

            boton.setOnMouseClicked(mouseEvent -> {
                // ALERTA PARA VERIFICAR SI BORRAR CUP O NO
                Alert alertaEliminar = new Alert(Alert.AlertType.WARNING);
                alertaEliminar.setTitle("Eliminar CUP");
                try {
                    alertaEliminar.setContentText("¿Seguro que quiere eliminar el " + Encriptar.decrypt(textoInputCupDecriptado) + "?");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

                ButtonType btnSi = new ButtonType("Sí");
                ButtonType btnNo = new ButtonType("No");

                alertaEliminar.getButtonTypes().setAll(btnSi, btnNo);

                Optional<ButtonType> resultado = alertaEliminar.showAndWait();
                if (resultado.isPresent()) {
                    if (resultado.get() == btnNo) {

                    } else if (resultado.get() == btnSi) {
                        String borrarCUPS;
                        try {
                            borrarCUPS = "DELETE FROM CUPS WHERE CODIGO_CUP='" + textoInputCupDecriptado + "'";
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                        conexion.eliminarDatos(borrarCUPS);
                        if (tipoTabla.equals("GAS")) {
                            try {
                                tabGas.addCupsToGridPane(numCups - 1, idCliente);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        } else {
                            tabLuz.addCupsToGridPane(numCups - 1, idCliente);
                        }
                    }
                }
            });


            // Caso de botón de nuevo
        } else if (tipoBoton.equals("nuevo")) {
            textField.setOnKeyReleased(keyEvent -> {
                if (keyEvent.getCode() == KeyCode.ENTER) {
                    try {
                        insertarCup(textField, tipoTabla, tabGas, tabLuz, numCups, idCliente);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            boton.setStyle("-fx-border-color: #4d4d4d; -fx-border-width: 2px; -fx-border-style: solid; -fx-font-weight: bold;");
            boton.setOnAction(e -> insertarCup(textField, tipoTabla, tabGas, tabLuz, numCups, idCliente));
        }

        HBox.setMargin(text, new Insets(20, 0, 0, 10));
        HBox.setMargin(textField, new Insets(15, 0, 0, 10));
        HBox.setMargin(boton, new Insets(15, 0, 0, 0));
        hBox.setPadding(new Insets(10, 20, 10, 0));

        String consultaCups = "SELECT * FROM CUPS WHERE CODIGO_CUP=?";
        textField.setOnMouseClicked(actionEvent -> {
            MainScreenController.cupsActual = textoInputCupDecriptado;
            MainScreenController.tipoActual = tipoTabla;

            if (tipoTabla.equals("GAS")) {
                tabGas.mostrarDatos(consultaCups, textoInputCupDecriptado);
            } else {
                tabLuz.mostrarDatos(consultaCups, textoInputCupDecriptado);
            }
        });

        HBox.setHgrow(textField, Priority.ALWAYS);
        GridPane.setHgrow(hBox, Priority.ALWAYS);

        hBox.getChildren().addAll(text, textField, boton);
        gridPane.add(hBox, columna, fila);
    }
    // -------------------------------------------------------------------------------------------------

    public void insertarCup(TextField textField, String tipoTabla, TabGas tabGas, TabLuz tabLuz, int numCups, int idCliente) {
        // Verificar cliente seleccionado
        if (!MainScreenController.hayClienteSeleccionado) {
            verificar.crearAlerta("SELECCIONA UN CLIENTE", "No hay un cliente seleccionado");
            return;
        }

        // Verificar datos insertados
        if (textField.getText().isEmpty() || textField.getText().isEmpty()) {
            verificar.crearAlerta("CUP VACÍO", "No puedes crear un cup vacío");
            return;
        }

        // Lógica para agregar nuevo CUPS
        String nuevoCups;
        try {
            nuevoCups = Encriptar.encrypt(textField.getText().trim());
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        if (!nuevoCups.isEmpty()) {

            // Verificar que el CUP no esté repetido en la BDD
            String consultaRepetido = "SELECT COUNT(*) FROM CUPS WHERE CODIGO_CUP = ?";
            int registros = conexion.verificarExistenciaDato(consultaRepetido, nuevoCups);
            if (registros > 0) {
                verificar.crearAlerta("CUP repetido", "El CUP introducido ya existe en la Base de Datos");
                return;
            }

            // Agregar el nuevo CUPS a la base de datos y actualizar el GridPane
            String insertarCups = "INSERT INTO CUPS (CODIGO_CUP, ID_CLIENTE, TIPO) VALUES ('" + nuevoCups + "', " + idCliente + ", '" + tipoTabla + "')";
            if (tipoTabla.equals("GAS")) {
                tabGas.conexion.ejecutarConsulta(insertarCups);
                try {
                    tabGas.addCupsToGridPane(numCups + 1, idCliente);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            } else {
                tabLuz.conexion.ejecutarConsulta(insertarCups);
                tabLuz.addCupsToGridPane(numCups + 1, idCliente);
            }

        }
    }

    // ------------------------- MÉTODO ABRIR HISTÓRICO ----------------------
    public void abrirHistorico(String tipo, int idCliente) {
        try {
            // Cargar FXML con ruta absoluta desde resources
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/practicas/consultoriacrm/dialog-view.fxml")
            );

            // Crear nuevo Stage para el diálogo
            Stage dialogStage = new Stage();
            Scene scene = new Scene(loader.load(), 800, 600);

            // Configurar el controlador
            DialogController controller = loader.getController();
            controller.setIdCliente(idCliente);
            controller.setTipo(tipo);

            // Configurar CSS
            scene.getStylesheets().add(
                    Objects.requireNonNull(getClass().getResource("/com/practicas/consultoriacrm/estilos/historico.css")).toExternalForm()
            );

            // Configurar ícono
            dialogStage.getIcons().add(
                    new Image(Objects.requireNonNull(getClass().getResource("/images/icono.png")).toExternalForm())
            );

            // Configurar ventana
            dialogStage.setScene(scene);
            dialogStage.setTitle("Histórico de " + tipo + " con ID " + idCliente);
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setResizable(false);
            dialogStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
