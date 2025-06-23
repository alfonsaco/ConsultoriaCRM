package com.practicas.consultoriacrm.Tabs;

import com.practicas.consultoriacrm.MainScreenController;
import com.practicas.consultoriacrm.Utils.Conexion;
import com.practicas.consultoriacrm.Utils.Encriptar;
import com.practicas.consultoriacrm.Utils.MetodosTabs;
import com.practicas.consultoriacrm.Utils.VerificarFormularios;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

public class TabAlarmas {
    @FXML public BorderPane alarmasBorderPane;
    @FXML public ComboBox<String> comboColaboradorAlarma;
    @FXML public DatePicker dateActivacion;
    @FXML public TextField txtNumCuentaAlarma;
    @FXML public DatePicker dateBajaAlarma;
    @FXML public DatePicker datePermanenciaAlarma;
    @FXML public TextField txtDirSumAlarma;
    @FXML public TextField txtComision;
    @FXML public TextField txtContratoAlarma;
    @FXML public TextArea txtObservacionesAlarma;
    @FXML public TextField txtPrecio;
    @FXML public TextField txtLiquidacion;
    @FXML public ComboBox<String> comboCompania;

    private GridPane gridPaneDir;
    private ScrollPane scrollPaneDir;

    @FXML private Button btnMostrarHistoricoAlarma;
    private Conexion conexion;
    private MetodosTabs metodosTabs;
    private VerificarFormularios verificar;

    private int idCliente;

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public void initialize() throws SQLException {
        // ------ INSTANCIA DE LA CONEXIÓN --------
        try {
            conexion = Conexion.getInstance();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        // ----------------------------------------
        gridPaneDir = new GridPane();
        gridPaneDir.setHgap(10);
        gridPaneDir.setVgap(10);
        gridPaneDir.setMaxWidth(Double.MAX_VALUE);

        // Crear el ScrollPane y encapsular el GridPane
        scrollPaneDir = new ScrollPane();
        scrollPaneDir.setContent(gridPaneDir);
        scrollPaneDir.setFitToWidth(true); // Se ajusta al ancho disponible
        scrollPaneDir.setPadding(new Insets(5, 30, 5, 5));
        scrollPaneDir.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPaneDir.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPaneDir.setPrefHeight(200);
        scrollPaneDir.setStyle("-fx-background: #FFF; -fx-border-color: #4d4d4d; -fx-border-width: 2px;");
        alarmasBorderPane.setTop(this.getScrollPaneDir());
        BorderPane.setMargin(this.getScrollPaneDir(), new Insets(0, 10, 0, 10));

        ObservableList<String> todasComps = conexion.obtenerCompaniasPorTipo("ALARMAS");
        comboCompania.setItems(todasComps);

        ObservableList<String> todosColabs = conexion.obtenerColaboradores();
        comboColaboradorAlarma.setItems(todosColabs);

        // BOTÓN MOSTRAR HISTÓRICO
        metodosTabs = new MetodosTabs();
        btnMostrarHistoricoAlarma.setOnAction(actionEvent -> {
            if (MainScreenController.hayClienteSeleccionado) {
                metodosTabs.abrirHistorico("ALARMAS", idCliente);
            } else {
                verificar.crearAlerta("SELECCIONA UN CLIENTE", "No hay clientes seleccionados");
            }
        });


        verificar = new VerificarFormularios();
    }



    public void addDireccionesAGridPane(int numDir, int idCliente) {
        String sql = "SELECT DIRECCION_ALARMA FROM DIRECCION_INSTALACION_ALARMAS WHERE ID_CLIENTE = " + idCliente;
        gridPaneDir.getChildren().clear();
        int totalFilas = (int) (double) (numDir + 1);
        ArrayList<String> direcciones = conexion.obtenerListaDatos(sql);

        for (int i = 0; i < numDir; i++) {
            String direccion = direcciones.get(i);
            HBox hBox = new HBox(0);
            Text text = new Text("Dirección de suministro  " + (i + 1));
            TextField textField = new TextField();

            HBox.setMargin(text, new Insets(20, 0, 0, 10));
            HBox.setMargin(textField, new Insets(15, 0, 0, 0));
            textField.getStyleClass().add("formulariosCliente");
            text.setStyle("-fx-fill: #4d4d4d; -fx-font-weight: bold;");
            textField.setPrefWidth(200);
            try {
                textField.setText(Encriptar.decrypt(direccion));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            textField.setCursor(Cursor.HAND);

            Button btnBorrar = new Button();
            btnBorrar.getStyleClass().add("botonBorrarCups");
            btnBorrar.setText("");

            try {
                Image iconoMas = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/deleteblanco.png")));
                ImageView imageView = new ImageView(iconoMas);
                imageView.setFitWidth(12);
                imageView.setFitHeight(12);
                btnBorrar.setGraphic(imageView);

            } catch (Exception e) {
                btnBorrar.setText("+");
            }

            btnBorrar.setOnMouseClicked(mouseEvent -> {
                // ALERTA PARA VERIFICAR SI BORRAR CUP O NO
                Alert alertaEliminar = new Alert(Alert.AlertType.WARNING);
                alertaEliminar.setTitle("Eliminar Dirección");
                try {
                    alertaEliminar.setContentText("¿Seguro que quiere eliminar " + Encriptar.decrypt(direccion) + "?");
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
                        String borrarCUPS = "DELETE FROM DIRECCION_INSTALACION_ALARMAS WHERE DIRECCION_ALARMA='" + direccion + "'";
                        conexion.eliminarDatos(borrarCUPS);
                        addDireccionesAGridPane(numDir - 1, idCliente);
                    }
                }
            });

            HBox.setMargin(text, new Insets(20, 0, 0, 10));
            HBox.setMargin(textField, new Insets(15, 0, 0, 10));
            HBox.setMargin(btnBorrar, new Insets(15, 0, 0, 0));

            textField.setOnMouseClicked(mouseEvent -> {
                mostrarDatos("SELECT * FROM DIRECCION_INSTALACION_ALARMAS WHERE DIRECCION_ALARMA = ?", direccion);
                MainScreenController.direccionAlarmaActual = direccion;
            });

            HBox.setHgrow(textField, Priority.ALWAYS);
            GridPane.setHgrow(hBox, Priority.ALWAYS);

            hBox.getChildren().addAll(text, textField, btnBorrar);
            gridPaneDir.add(hBox, 0, i);

        }

        HBox hBoxNuevo = new HBox(0);
        hBoxNuevo.getStyleClass().add("boxes-cups");

        Text textNuevo = new Text("NUEVA DIRECCIÓN: ");
        textNuevo.setStyle("-fx-fill: #4d4d4d; -fx-font-weight: bold;");

        TextField textFieldNuevo = new TextField();
        textFieldNuevo.setPrefWidth(200);
        textFieldNuevo.getStyleClass().add("formulariosCliente");
        textFieldNuevo.setEditable(true);

        textFieldNuevo.setOnKeyReleased(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                try {
                    insertarDirAlarma(textFieldNuevo, numDir);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });

        Button btnNuevo = new Button();
        btnNuevo.getStyleClass().add("botonGestionarCliente");
        btnNuevo.setStyle("-fx-border-color: #4d4d4d; -fx-border-width: 2px; -fx-border-style: solid;");
        btnNuevo.setText("");

        try {
            Image iconoMas = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/mas.png")));
            ImageView imageView = new ImageView(iconoMas);
            imageView.setFitWidth(12);
            imageView.setFitHeight(12);
            btnNuevo.setGraphic(imageView);
        } catch (Exception e) {
            btnNuevo.setText("+");
        }

        // Configurar acción del botón
        btnNuevo.setOnAction(e -> insertarDirAlarma(textFieldNuevo, numDir));

        HBox.setMargin(textNuevo, new Insets(20, 0, 0, 10));
        HBox.setMargin(textFieldNuevo, new Insets(15, 0, 0, 10));
        HBox.setMargin(btnNuevo, new Insets(15, 0, 0, 0));

        HBox.setHgrow(textFieldNuevo, Priority.ALWAYS);
        GridPane.setHgrow(hBoxNuevo, Priority.ALWAYS);

        hBoxNuevo.getChildren().addAll(textNuevo, textFieldNuevo, btnNuevo);

        // Añadir en la posición calculada
        gridPaneDir.add(hBoxNuevo, 0, totalFilas - 1);

    }

    public void insertarDirAlarma(TextField textFieldNuevo, int numDir) {
        if (!MainScreenController.hayClienteSeleccionado) {
            verificar.crearAlerta("SELECCIONA UN CLIENTE", "No hay un cliente seleccionado");
            return;
        }

        // Verificar datos insertados
        if (textFieldNuevo.getText().isEmpty() || textFieldNuevo.getText().isEmpty()) {
            verificar.crearAlerta("CUP VACÍO", "No puedes crear un cup vacío");
            return;
        }

        // Lógica para agregar nuevo CUPS
        String nuevaDireccion;
        try {
            nuevaDireccion = Encriptar.encrypt(textFieldNuevo.getText().trim());
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        if (!nuevaDireccion.isEmpty()) {
            // Agregar el nuevo CUPS a la base de datos y actualizar el GridPane
            String insertarDir = "INSERT INTO DIRECCION_INSTALACION_ALARMAS (DIRECCION_ALARMA, ID_CLIENTE) VALUES ('" + nuevaDireccion + "', " + idCliente + ")";
            conexion.ejecutarConsulta(insertarDir);
            addDireccionesAGridPane(numDir + 1, idCliente); // Actualizar el GridPane
        }
    }

    public void vaciarGrid() {
        gridPaneDir.getChildren().clear();
        cargarPrimerCampoPorDefecto();
    }

    // Para cargar el primer input de cups al iniciar la app
    public void cargarPrimerCampoPorDefecto() {
        HBox hBoxNuevo = new HBox(0);
        hBoxNuevo.getStyleClass().add("boxes-cups");

        Text textNuevo = new Text("NUEVA DIRECCIÓN: ");
        textNuevo.setStyle("-fx-fill: #4d4d4d; -fx-font-weight: bold;");

        TextField textFieldNuevo = new TextField();
        textFieldNuevo.setPrefWidth(200);
        textFieldNuevo.getStyleClass().add("formulariosCliente");
        textFieldNuevo.setEditable(true);

        Button btnNuevo = new Button();
        btnNuevo.getStyleClass().add("botonGestionarCliente");
        btnNuevo.setStyle("-fx-border-color: #4d4d4d; -fx-border-width: 2px; -fx-border-style: solid;");
        btnNuevo.setText("");

        try {
            Image iconoMas = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/mas.png")));
            ImageView imageView = new ImageView(iconoMas);
            imageView.setFitWidth(12);
            imageView.setFitHeight(12);
            btnNuevo.setGraphic(imageView);
        } catch (Exception e) {
            btnNuevo.setText("+");
        }

        // Configurar acción del botón
        btnNuevo.setOnAction(e -> {
            if (!MainScreenController.hayClienteSeleccionado) {
                verificar.crearAlerta("SELECCIONA UN CLIENTE", "No hay un cliente seleccionado");
                return;
            }


            // Lógica para agregar nuevo CUPS
            String nuevaDireccion = textFieldNuevo.getText().trim();
            if (!nuevaDireccion.isEmpty()) {
                // Agregar el nuevo CUPS a la base de datos y actualizar el GridPane
                String insertarDir = "INSERT INTO DIRECCION_INSTALACION_ALARMAS (DIRECCION_ALARMA, ID_CLIENTE) VALUES ('" + nuevaDireccion + "', " + idCliente + ")";
                conexion.ejecutarConsulta(insertarDir);
                addDireccionesAGridPane(1, idCliente); // Actualizar el GridPane
            }
        });

        HBox.setMargin(textNuevo, new Insets(20, 0, 0, 10));
        HBox.setMargin(textFieldNuevo, new Insets(15, 0, 0, 10));
        HBox.setMargin(btnNuevo, new Insets(15, 0, 0, 0));

        HBox.setHgrow(textFieldNuevo, Priority.ALWAYS);
        GridPane.setHgrow(hBoxNuevo, Priority.ALWAYS);

        hBoxNuevo.getChildren().addAll(textNuevo, textFieldNuevo, btnNuevo);

        // Añadir en la posición calculada
        gridPaneDir.add(hBoxNuevo, 0, 0);
    }

    public void mostrarDatos(String consulta, String direccion) {
        MainScreenController.cupsEditar = false;
        MainScreenController.direccionTelefoniaInternetEditar = false;
        MainScreenController.direccionAlarmaEditar = true;
        try (PreparedStatement preparedStatement = conexion.getConnection().prepareStatement(consulta)) {
            preparedStatement.setString(1, direccion);
            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {
                String sql = "SELECT CO.NOMBRE_COMPANIA FROM COMPANIAS CO JOIN DIRECCION_INSTALACION_ALARMAS D ON CO.ID_COMPANIA = D.ID_COMPANIA JOIN CLIENTES C ON C.ID_CLIENTE = D.ID_CLIENTE WHERE D.DIRECCION_ALARMA = ?";
                String compania = conexion.obtenerDatoStringBDD(sql, direccion);

                java.sql.Date fechaActivacionSQL = rs.getDate("FECHA_ACTIVACION");
                java.sql.Date fechaBajaSQL = rs.getDate("FECHA_BAJA");
                java.sql.Date fechaPermanencia = rs.getDate("FECHA_PERMANENCIA");

                double comision = rs.getDouble("COMISION");
                String direccionSuministro = rs.getString("DIRECCION_ALARMA");
                String tipoContrado = rs.getString("TIPO_CONTRATO");
                String numCuenta = rs.getString("NUMERO_CUENTA");
                double liquidacion = rs.getDouble("LIQUIDACION");
                double precio = rs.getDouble("PRECIO");
                String observaciones = rs.getString("OBSERVACIONES");


                if (compania == null) {
                    comboCompania.getSelectionModel().clearSelection();
                } else {
                    comboCompania.setValue(compania);
                }

                if (fechaActivacionSQL == null) {
                    dateActivacion.setValue(null);
                } else {
                    LocalDate localDate = fechaActivacionSQL.toLocalDate();
                    dateActivacion.setValue(localDate);
                }
                if (fechaBajaSQL == null) {
                    dateBajaAlarma.setValue(null);
                } else {
                    LocalDate localDate = fechaBajaSQL.toLocalDate();
                    dateBajaAlarma.setValue(localDate);
                }

                if (fechaPermanencia == null) {
                    datePermanenciaAlarma.setValue(null);
                } else {
                    LocalDate localDate = fechaPermanencia.toLocalDate();
                    datePermanenciaAlarma.setValue(localDate);
                }

                txtComision.setText(Double.toString(comision));

                if (direccionSuministro == null) {
                    txtDirSumAlarma.setText("");
                } else {
                    try {
                        txtDirSumAlarma.setText(Encriptar.decrypt(direccionSuministro));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }

                txtContratoAlarma.setText(Objects.requireNonNullElse(tipoContrado, ""));

                if (numCuenta == null) {
                    txtNumCuentaAlarma.setText("");
                } else {
                    try {
                        txtNumCuentaAlarma.setText(Encriptar.decrypt(numCuenta));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }

                txtLiquidacion.setText(Double.toString(liquidacion));

                txtPrecio.setText(Double.toString(precio));

                if (observaciones == null) {
                    txtObservacionesAlarma.setText("");
                } else {
                    try {
                        txtObservacionesAlarma.setText(Encriptar.decrypt(observaciones));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }

            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ScrollPane getScrollPaneDir() {
        return scrollPaneDir;
    }


}
