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
import javafx.scene.layout.*;
import javafx.scene.text.Text;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

public class TabTelefonia {
    @FXML
    public TextField txtNumLineas;
    @FXML
    public ComboBox<String> comboInternet;
    @FXML
    public ComboBox<String> comboTelefonia;
    // -------------------------------------------
    @FXML
    public DatePicker fechaContratoInternet;
    @FXML
    public DatePicker fechaPermanenciaInternet;
    @FXML
    public DatePicker fechaContratoTelefonia;
    @FXML
    public DatePicker fechaPermanenciaTelefonia;
    @FXML
    public TextField numeroCuenta;
    @FXML
    public TextField dirFibra;
    @FXML
    public TextArea txtObservacionesTelefonia;
    @FXML
    public TextField txtDonanteInternet;
    @FXML
    public TextField txtDonanteTlf;
    // ---------- Elementos de espacio -----------
    private GridPane gridPaneDir;
    private ScrollPane scrollPaneDir;
    private GridPane gridPaneLinea;
    private ScrollPane scrollPaneLinea;
    // ----------------- Componentes --------------
    @FXML
    private AnchorPane anchorTelefonia;
    @FXML
    private BorderPane telefoniaBorderPane;
    @FXML
    private AnchorPane anchorDirecciones;
    @FXML
    private AnchorPane anchorLineas;
    @FXML
    private Button btnMostrarHistoricoTel;
    // --------------------------------------------
    @FXML
    private Button btnMostrarHistoricoInt;
    private int idCliente;
    // --------------- Clases -------------------------
    private Conexion conexion;
    private MetodosTabs metodosTabs;
    private VerificarFormularios verificar;

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }
    // ------------------------------------------------

    public void initialize() throws SQLException {
        gridPaneDir = new GridPane();
        gridPaneDir.setHgap(10);
        gridPaneDir.setVgap(10);
        gridPaneDir.setMaxWidth(Double.MAX_VALUE);

        // Crear el ScrollPane y encapsular el GridPane
        scrollPaneDir = new ScrollPane();
        scrollPaneDir.setContent(gridPaneDir);
        scrollPaneDir.setFitToWidth(true);
        scrollPaneDir.setPadding(new Insets(5, 30, 5, 5));
        scrollPaneDir.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPaneDir.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPaneDir.setStyle("-fx-background: #FFF; -fx-border-color: #4d4d4d; -fx-border-width: 2px;");

        // ------ INSTANCIA DE LA CONEXIÓN --------
        conexion = Conexion.getInstance();
        // ----------------------------------------

        gridPaneLinea = new GridPane();
        gridPaneLinea.setHgap(10);
        gridPaneLinea.setVgap(10);
        gridPaneLinea.setMaxWidth(Double.MAX_VALUE);

        // Crear el ScrollPane y encapsular el GridPane
        scrollPaneLinea = new ScrollPane();
        scrollPaneLinea.setContent(gridPaneLinea);
        scrollPaneLinea.setFitToWidth(true);
        scrollPaneLinea.setPadding(new Insets(5, 30, 5, 5));
        scrollPaneLinea.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPaneLinea.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPaneLinea.setStyle("-fx-background: #FFF; -fx-border-color: #4d4d4d; -fx-border-width: 2px;");

        AnchorPane.setTopAnchor(this.getScrollPaneDir(), 10.0);
        AnchorPane.setLeftAnchor(this.getScrollPaneDir(), 10.0);
        AnchorPane.setRightAnchor(this.getScrollPaneDir(), 10.0);
        AnchorPane.setBottomAnchor(this.getScrollPaneDir(), 10.0);

        BorderPane.setMargin(this.getScrollPaneDir(), new Insets(0, 30, 0, 0));
        anchorDirecciones.getChildren().add(this.getScrollPaneDir());

        AnchorPane.setTopAnchor(this.getScrollPaneLinea(), 10.0);
        AnchorPane.setLeftAnchor(this.getScrollPaneLinea(), 10.0);
        AnchorPane.setRightAnchor(this.getScrollPaneLinea(), 10.0);
        AnchorPane.setBottomAnchor(this.getScrollPaneLinea(), 10.0);

        BorderPane.setMargin(this.getScrollPaneLinea(), new Insets(0, 10, 0, 10));
        anchorLineas.getChildren().add(this.getScrollPaneLinea());
        ObservableList<String> todasCompsInternet = conexion.obtenerCompaniasPorTipo("INTERNET");
        comboInternet.setItems(todasCompsInternet);

        ObservableList<String> todasCompsTelefonia = conexion.obtenerCompaniasPorTipo("TELEFONIA");
        comboTelefonia.setItems(todasCompsTelefonia);

        // BOTÓN MOSTRAR HISTÓRICO
        metodosTabs = new MetodosTabs();
        btnMostrarHistoricoTel.setOnAction(actionEvent -> {
            if (MainScreenController.hayClienteSeleccionado) {
                metodosTabs.abrirHistorico("TELEFONIA", idCliente);
            } else {
                verificar.crearAlerta("SELECCIONA UN CLIENTE", "No hay clientes seleccionados");
            }
        });
        btnMostrarHistoricoInt.setOnAction(actionEvent -> {
            if (MainScreenController.hayClienteSeleccionado) {
                metodosTabs.abrirHistorico("INTERNET", idCliente);
            } else {
                verificar.crearAlerta("SELECCIONA UN CLIENTE", "No hay clientes seleccionados");
            }
        });

        verificar = new VerificarFormularios();
    }

    public ScrollPane getScrollPaneLinea() {
        return scrollPaneLinea;
    }

    public void addDireccionesAGridPane(int numDir, int idCliente) throws Exception {
        String consulta = "SELECT DISTINCT (DIRECCION_FIBRA) FROM DIRECCION_INSTALACION_TELEFONIA WHERE ID_CLIENTE = " + idCliente;
        gridPaneDir.getChildren().clear();
        ArrayList<String> direcciones = conexion.obtenerListaDatos(consulta);

        // Asegurarnos de que numDir no sea menor que las direcciones existentes
        int numDireccionesExistentes = direcciones.size();
        if (numDir < numDireccionesExistentes) {
        }

        // Añadir direcciones existentes
        for (int i = 0; i < numDireccionesExistentes; i++) {
            String direccion = direcciones.get(i);

            HBox hBox = new HBox(0);
            hBox.getStyleClass().add("boxes-cups");

            Text text = new Text("Dirección de suministro " + (i + 1));
            text.setStyle("-fx-fill: #4d4d4d; -fx-border-color: #4d4d4d; -fx-font-weight: bold;");

            TextField textField = new TextField();
            textField.setPrefWidth(200);
            textField.getStyleClass().add("formulariosCliente");
            textField.setText(Encriptar.decrypt(direccion));
            textField.setEditable(false);
            textField.setCursor(Cursor.HAND);

            Button btnBorrar = new Button();
            btnBorrar.getStyleClass().add("botonBorrarCups");
            btnBorrar.setText("");

            try {
                Image iconoBorrar = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/deleteblanco.png")));
                ImageView imageView = new ImageView(iconoBorrar);
                imageView.setFitWidth(12);
                imageView.setFitHeight(12);
                btnBorrar.setGraphic(imageView);

            } catch (Exception e) {
                btnBorrar.setText("X");
            }

            btnBorrar.setOnMouseClicked(mouseEvent -> {
                Alert alertaEliminar = new Alert(Alert.AlertType.WARNING);
                alertaEliminar.setTitle("Eliminar Dirección");
                try {
                    alertaEliminar.setContentText("¿Seguro que quiere eliminar la dirección: " + Encriptar.decrypt(direccion) + "?");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

                ButtonType btnSi = new ButtonType("Sí");
                ButtonType btnNo = new ButtonType("No");

                alertaEliminar.getButtonTypes().setAll(btnSi, btnNo);

                Optional<ButtonType> resultado = alertaEliminar.showAndWait();
                if (resultado.isPresent() && resultado.get() == btnSi) {
                    String obtenerIdDir = "SELECT ID_DIRECCION FROM DIRECCION_INSTALACION_TELEFONIA WHERE DIRECCION_FIBRA=?";
                    int idDir;
                    try {
                        idDir = conexion.obtenerDatoIntBDD(obtenerIdDir, direccion);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    String borrarLineas = "DELETE FROM LINEAS WHERE ID_DIRECCION = " + idDir;
                    conexion.eliminarDatos(borrarLineas);
                    String borrarDireccion;
                    try {
                        borrarDireccion = "DELETE FROM DIRECCION_INSTALACION_TELEFONIA WHERE DIRECCION_FIBRA='" + direccion + "' AND ID_CLIENTE=" + idCliente;
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    conexion.eliminarDatos(borrarDireccion);
                    try {
                        addDireccionesAGridPane(numDireccionesExistentes - 1, idCliente);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    try {
                        agregarLineasAlGrid(idDir, idCliente);
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                }
            });

            // MÉTODO PARA MOSTRAR LAS LÍNEAS DE CADA DIRECCIÓN
            String consultaID = "SELECT ID_DIRECCION FROM DIRECCION_INSTALACION_TELEFONIA WHERE DIRECCION_FIBRA=?";
            int idDireccion = conexion.obtenerDatoIntBDD(consultaID, direccion);

            HBox.setMargin(text, new Insets(20, 0, 0, 10));
            HBox.setMargin(textField, new Insets(15, 0, 0, 10));
            HBox.setMargin(btnBorrar, new Insets(15, 0, 0, 0));

            HBox.setHgrow(textField, Priority.ALWAYS);
            GridPane.setHgrow(hBox, Priority.ALWAYS);

            hBox.getChildren().addAll(text, textField, btnBorrar);

            textField.setOnMouseClicked(mouseEvent -> {
                fechaPermanenciaTelefonia.getEditor().clear();
                fechaContratoTelefonia.getEditor().clear();
                fechaPermanenciaInternet.getEditor().clear();
                fechaContratoInternet.getEditor().clear();
                comboTelefonia.getSelectionModel().clearSelection();
                comboInternet.getSelectionModel().clearSelection();
                comboInternet.getEditor().setText("");
                comboTelefonia.getEditor().setText("");
                txtDonanteInternet.setText("");
                txtDonanteTlf.setText("");
                try {
                    mostrarDatos(direccion);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                MainScreenController.direccionActual = direccion;
                try {
                    agregarLineasAlGrid(idDireccion, idCliente);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            });

            gridPaneDir.add(hBox, 0, i);
        }

        // Añadir campo para nueva dirección (SIEMPRE al final)
        HBox hBoxNuevo = new HBox(0);

        Text textNuevo = new Text("NUEVA DIRECCIÓN: ");
        textNuevo.setStyle("-fx-fill: #4d4d4d; -fx-border-color: #4d4d4d; -fx-font-weight: bold;");

        TextField textFieldNuevo = new TextField();
        textFieldNuevo.setPrefWidth(200);
        textFieldNuevo.getStyleClass().add("formulariosCliente");
        textFieldNuevo.setEditable(true);

        textFieldNuevo.setOnKeyReleased(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                try {
                    insertarDireccionTelefonia(textFieldNuevo, numDireccionesExistentes);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });

        Button btnNuevo = new Button();
        btnNuevo.getStyleClass().add("botonGestionarCliente");
        btnNuevo.setStyle("-fx-border-color: #4d4d4d; -fx-border-width: 2px; -fx-border-style: solid;");

        try {
            Image iconoMas = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/mas.png")));
            ImageView imageView = new ImageView(iconoMas);
            imageView.setFitWidth(12);
            imageView.setFitHeight(12);
            btnNuevo.setGraphic(imageView);
        } catch (Exception e) {
            btnNuevo.setText("+");
        }

        btnNuevo.setOnAction(e -> insertarDireccionTelefonia(textFieldNuevo, numDireccionesExistentes));

        HBox.setMargin(textNuevo, new Insets(20, 0, 0, 10));
        HBox.setMargin(textFieldNuevo, new Insets(15, 0, 0, 10));
        HBox.setMargin(btnNuevo, new Insets(15, 0, 0, 0));

        HBox.setHgrow(textFieldNuevo, Priority.ALWAYS);
        GridPane.setHgrow(hBoxNuevo, Priority.ALWAYS);

        hBoxNuevo.getChildren().addAll(textNuevo, textFieldNuevo, btnNuevo);


        // Añadir en la última posición (numDireccionesExistentes)
        gridPaneDir.add(hBoxNuevo, 0, numDireccionesExistentes);

        obtenerCantidadLineas(idCliente);
    }

    public void insertarDireccionTelefonia(TextField textFieldNuevo, int numDireccionesExistentes) {
        if (!MainScreenController.hayClienteSeleccionado) {
            verificar.crearAlerta("SELECCIONA UN CLIENTE", "No hay un cliente seleccionado");
            return;
        }

        // Verificar datos insertados
        if (textFieldNuevo.getText().isEmpty() || textFieldNuevo.getText().isEmpty()) {
            verificar.crearAlerta("CUP VACÍO", "No puedes crear un cup vacío");
            return;
        }

        String nuevaDireccion = textFieldNuevo.getText().trim();
        if (!nuevaDireccion.isEmpty()) {
            String insertarDireccion;
            try {
                insertarDireccion = "INSERT INTO DIRECCION_INSTALACION_TELEFONIA (DIRECCION_FIBRA, ID_CLIENTE) VALUES ('" +
                        Encriptar.encrypt(nuevaDireccion) + "', " + idCliente + ")";
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
            conexion.ejecutarConsulta(insertarDireccion);
            try {
                addDireccionesAGridPane(numDireccionesExistentes + 1, idCliente);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    public void vaciarGrids() {
        gridPaneDir.getChildren().clear();
        gridPaneLinea.getChildren().clear();

        cargarPrimerCampoPorDefecto();
    }

    // Para cargar el primer input de cups al iniciar la app
    public void cargarPrimerCampoPorDefecto() {
        // Añadir campo para nueva dirección (SIEMPRE al final)
        HBox hBoxNuevo = new HBox(0);

        Text textNuevo = new Text("NUEVA DIRECCIÓN: ");
        textNuevo.setStyle("-fx-fill: #4d4d4d; -fx-border-color: #4d4d4d; -fx-font-weight: bold;");

        TextField textFieldNuevo = new TextField();
        textFieldNuevo.setPrefWidth(200);
        textFieldNuevo.getStyleClass().add("formulariosCliente");
        textFieldNuevo.setEditable(true);

        Button btnNuevo = new Button();
        btnNuevo.getStyleClass().add("botonGestionarCliente");
        btnNuevo.setStyle("-fx-border-color: #4d4d4d; -fx-border-width: 2px; -fx-border-style: solid;");

        try {
            Image iconoMas = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/mas.png")));
            ImageView imageView = new ImageView(iconoMas);
            imageView.setFitWidth(12);
            imageView.setFitHeight(12);
            btnNuevo.setGraphic(imageView);
        } catch (Exception e) {
            btnNuevo.setText("+");
        }

        btnNuevo.setOnAction(e -> {
            if (!MainScreenController.hayClienteSeleccionado) {
                verificar.crearAlerta("SELECCIONA UN CLIENTE", "No hay un cliente seleccionado");
                return;
            }

            String nuevaDireccion = textFieldNuevo.getText().trim();
            if (!nuevaDireccion.isEmpty()) {
                String insertarDireccion = "INSERT INTO DIRECCION_INSTALACION_TELEFONIA (DIRECCION_FIBRA, ID_CLIENTE) VALUES ('" +
                        nuevaDireccion + "', " + idCliente + ")";
                conexion.ejecutarConsulta(insertarDireccion);
                try {
                    addDireccionesAGridPane(1, idCliente);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        HBox.setMargin(textNuevo, new Insets(20, 0, 0, 10));
        HBox.setMargin(textFieldNuevo, new Insets(15, 0, 0, 10));
        HBox.setMargin(btnNuevo, new Insets(15, 0, 0, 0));

        HBox.setHgrow(textFieldNuevo, Priority.ALWAYS);
        GridPane.setHgrow(hBoxNuevo, Priority.ALWAYS);

        hBoxNuevo.getChildren().addAll(textNuevo, textFieldNuevo, btnNuevo);


        // Añadir en la última posición (numDireccionesExistentes)
        gridPaneDir.add(hBoxNuevo, 0, 0);
    }

    public ScrollPane getScrollPaneDir() {
        return scrollPaneDir;
    }

    public void limpiarLineas() {
        gridPaneLinea.getChildren().clear();
    }

    // Método para añadir líneas al Grid
    public void agregarLineasAlGrid(int idDireccion, int idCliente) throws Exception {
        String consultaDireccion = "SELECT LINEA FROM LINEAS WHERE ID_DIRECCION=" + idDireccion;

        ArrayList<String> lineas = conexion.obtenerListaDatos(consultaDireccion);
        int numLineas = lineas.size();

        gridPaneLinea.getChildren().clear();


        int totalFilas = (int) Math.ceil((numLineas + 1) / 4.0);
        for (int i = 0; i < numLineas; i++) {
            String encryptedLine = lineas.get(i);
            if (encryptedLine == null || encryptedLine.trim().isEmpty()) {
                continue;
            }
            int linea = Integer.parseInt(Encriptar.decrypt(encryptedLine));
            int fila = i / 4;
            int columna = i % 4;

            HBox hBox = new HBox(0);
            Text text = new Text("LÍNEA " + (i + 1));
            text.setStyle("-fx-fill: #4d4d4d;");
            TextField textField = new TextField();

            HBox.setMargin(text, new Insets(20, 0, 0, 10));
            HBox.setMargin(textField, new Insets(15, 0, 0, 0));
            textField.getStyleClass().add("formulariosCliente");
            textField.setText(String.valueOf(linea));
            textField.setPrefWidth(200);
            textField.setCursor(Cursor.HAND);

            Button btnBorrar = new Button();
            btnBorrar.getStyleClass().add("botonBorrarCups");
            btnBorrar.setText("");

            try {
                Image iconoBorrar = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/deleteblanco.png")));
                ImageView imageView = new ImageView(iconoBorrar);
                imageView.setFitWidth(12);
                imageView.setFitHeight(12);
                btnBorrar.setGraphic(imageView);

            } catch (Exception e) {
                btnBorrar.setText("X");
            }

            btnBorrar.setOnMouseClicked(mouseEvent -> {
                Alert alertaEliminar = new Alert(Alert.AlertType.WARNING);
                alertaEliminar.setTitle("Eliminar Línea");
                alertaEliminar.setContentText("¿Seguro que quiere eliminar la linea: " + linea + "?");

                ButtonType btnSi = new ButtonType("Sí");
                ButtonType btnNo = new ButtonType("No");

                alertaEliminar.getButtonTypes().setAll(btnSi, btnNo);

                Optional<ButtonType> resultado = alertaEliminar.showAndWait();
                if (resultado.isPresent() && resultado.get() == btnSi) {
                    String borrarLineas;
                    try {
                        borrarLineas = "DELETE FROM LINEAS WHERE LINEA = '" + Encriptar.encrypt(String.valueOf(linea)) + "'";
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                    conexion.eliminarDatos(borrarLineas);
                    try {
                        agregarLineasAlGrid(idDireccion, idCliente);
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                    obtenerCantidadLineas(idCliente);
                }
            });

            HBox.setMargin(text, new Insets(20, 0, 0, 10));
            HBox.setMargin(textField, new Insets(15, 0, 0, 10));
            HBox.setMargin(btnBorrar, new Insets(15, 0, 0, 0));

            HBox.setHgrow(textField, Priority.ALWAYS);
            GridPane.setHgrow(hBox, Priority.ALWAYS);

            hBox.getChildren().addAll(text, textField, btnBorrar);
            gridPaneLinea.add(hBox, columna, fila);
        }

        int ultimaFila = totalFilas - 1;
        int columnaNuevo = numLineas % 4;

        HBox hBoxNuevo = new HBox(0);
        hBoxNuevo.getStyleClass().add("boxes-cups");

        Text textNuevo = new Text(" NUEVA LÍNEA: ");
        textNuevo.setStyle("-fx-fill: #4d4d4d;");

        TextField textFieldNuevo = new TextField();
        textFieldNuevo.setPrefWidth(200);
        textFieldNuevo.getStyleClass().add("formulariosCliente");
        textFieldNuevo.setEditable(true);

        textFieldNuevo.setOnKeyReleased(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                try {
                    insertarLineas(textFieldNuevo, idDireccion, numLineas);
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

        btnNuevo.setOnAction(e -> insertarLineas(textFieldNuevo, idDireccion, numLineas));

        HBox.setMargin(textNuevo, new Insets(20, 0, 0, 10));
        HBox.setMargin(textFieldNuevo, new Insets(15, 0, 0, 10));
        HBox.setMargin(btnNuevo, new Insets(15, 0, 0, 0));

        HBox.setHgrow(textFieldNuevo, Priority.ALWAYS);
        GridPane.setHgrow(hBoxNuevo, Priority.ALWAYS);

        hBoxNuevo.getChildren().addAll(textNuevo, textFieldNuevo, btnNuevo);

        // Añadir en la posición calculada
        gridPaneLinea.add(hBoxNuevo, columnaNuevo, ultimaFila);

    }

    public void insertarLineas(TextField textFieldNuevo, int idDireccion, int numLineas) {
        try {
            int nuevaLinea = Integer.parseInt(textFieldNuevo.getText().trim());

            if (!(nuevaLinea == 0)) {
                String insertarLinea = "INSERT INTO LINEAS (ID_DIRECCION, LINEA) VALUES(" + idDireccion + ",  '" + Encriptar.encrypt(String.valueOf(nuevaLinea)) + "')";
                conexion.ejecutarConsulta(insertarLinea);
                agregarLineasAlGrid(idDireccion, idCliente);

                obtenerCantidadLineas(idCliente);
            }

        } catch (NumberFormatException i) {
            verificar.crearAlerta("LÍNEA NO VÁLIDA", "La línea debe ser un número");
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    // PARA PONERLO EN EL TXTNUMLINEAS
    public void obtenerCantidadLineas(int idCliente) {
        // AGREGAR EL NÚMERO TOTAL DE LÍNEAS POR CLIENTE
        String cantLineas = "SELECT COUNT(L.ID_LINEA) " +
                "FROM LINEAS L " +
                "JOIN DIRECCION_INSTALACION_TELEFONIA X ON X.ID_DIRECCION = L.ID_DIRECCION " +
                "JOIN CLIENTES C ON C.ID_CLIENTE = X.ID_CLIENTE " +
                "WHERE C.ID_CLIENTE = ?";

        // Obtener el número de líneas
        int numero = conexion.obtenerDatoIntBDD(cantLineas, String.valueOf(idCliente));

        // Establecer el número de líneas en el TextField
        txtNumLineas.setText(String.valueOf(numero));
    }

    public void mostrarDatos(String direccion) {
        MainScreenController.cupsEditar = false;
        MainScreenController.direccionTelefoniaInternetEditar = true;
        MainScreenController.direccionAlarmaEditar = false;
        // Consulta modificada para manejar correctamente las compañías separadas
        String consulta = "SELECT dit.*, " +
                "ci.NOMBRE_COMPANIA AS NOMBRE_COMPANIA_INTERNET, " +
                "ct.NOMBRE_COMPANIA AS NOMBRE_COMPANIA_TELEFONIA, " +
                "COMPANIA_DONANTE_INTERNET AS DONANTE_INTERNET," +
                "COMPANIA_DONANTE_TELEFONIA  AS DONANTE_TELEFONIA, " +
                "'INTERNET' AS TIPO_COMPANIA_INTERNET, " +
                "'TELEFONIA' AS TIPO_COMPANIA_TELEFONIA " +
                "FROM DIRECCION_INSTALACION_TELEFONIA dit " +
                "LEFT JOIN COMPANIAS ci ON dit.ID_COMPANIA_INTERNET = ci.ID_COMPANIA " +
                "LEFT JOIN COMPANIAS ct ON dit.ID_COMPANIA_TELEFONIA = ct.ID_COMPANIA " +
                "WHERE dit.DIRECCION_FIBRA = ?";

        try (PreparedStatement preparedStatement = conexion.getConnection().prepareStatement(consulta)) {
            preparedStatement.setString(1, direccion);
            ResultSet rs = preparedStatement.executeQuery();

            // Variables para controlar los tipos de servicios encontrados
            boolean tieneInternet = false;
            boolean tieneTelefonia = false;

            // Limpiar los campos primero
            comboInternet.getSelectionModel().clearSelection();
            comboTelefonia.getSelectionModel().clearSelection();
            txtDonanteInternet.setText("");
            txtDonanteTlf.setText("");
            fechaContratoInternet.setValue(null);
            fechaPermanenciaInternet.setValue(null);
            fechaContratoTelefonia.setValue(null);
            fechaPermanenciaTelefonia.setValue(null);
            numeroCuenta.setText("");
            txtObservacionesTelefonia.setText("");
            dirFibra.setText("");

            // Procesar todos los registros
            while (rs.next()) {
                String internetDonante = rs.getString("DONANTE_INTERNET");
                txtDonanteInternet.setText(internetDonante);
                String tlfDonante = rs.getString("DONANTE_TELEFONIA");
                txtDonanteTlf.setText(tlfDonante);
                // Procesar internet si existeID_COMPANIA_TELEFONO
                String nombreInternet = rs.getString("NOMBRE_COMPANIA_INTERNET");
                if (nombreInternet != null) {
                    tieneInternet = true;
                    comboInternet.setValue(nombreInternet);

                    java.sql.Date fechaContratoInternetSQL = rs.getDate("FECHA_ACTIVACION_INTERNET");
                    if (fechaContratoInternetSQL != null) {
                        fechaContratoInternet.setValue(fechaContratoInternetSQL.toLocalDate());
                    }

                    java.sql.Date fechaPermanenciaInternetSQL = rs.getDate("FECHA_PERMANENCIA_INTERNET");
                    if (fechaPermanenciaInternetSQL != null) {
                        fechaPermanenciaInternet.setValue(fechaPermanenciaInternetSQL.toLocalDate());
                    }

                    dirFibra.setText(Encriptar.decrypt(direccion));
                }

                // Procesar telefonía si existe
                String nombreTelefonia = rs.getString("NOMBRE_COMPANIA_TELEFONIA");
                if (nombreTelefonia != null) {
                    tieneTelefonia = true;
                    comboTelefonia.setValue(nombreTelefonia);

                    java.sql.Date fechaContratoTelefoniaSQL = rs.getDate("FECHA_ACTIVACION_TELEFONIA");
                    if (fechaContratoTelefoniaSQL != null) {
                        fechaContratoTelefonia.setValue(fechaContratoTelefoniaSQL.toLocalDate());
                    }

                    java.sql.Date fechaPermanenciaTelefoniaSQL = rs.getDate("FECHA_PERMANENCIA_TELEFONIA");
                    if (fechaPermanenciaTelefoniaSQL != null) {
                        fechaPermanenciaTelefonia.setValue(fechaPermanenciaTelefoniaSQL.toLocalDate());
                    }
                }

                // Tomar el número de cuenta y observaciones del registro
                String numCuenta = rs.getString("NUMERO_CUENTA");
                if (numCuenta != null) {
                    numeroCuenta.setText(Encriptar.decrypt(numCuenta));
                }

                String stringObservaciones = rs.getString("OBSERVACIONES");
                if (stringObservaciones != null) {
                    txtObservacionesTelefonia.setText(Encriptar.decrypt(stringObservaciones));
                }
            }

            // Si solo hay telefonía, mantener dirFibra vacío
            if (tieneTelefonia && !tieneInternet) {
                dirFibra.setText("");
            }

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

}

