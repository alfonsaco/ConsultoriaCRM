package com.practicas.consultoriacrm.Tabs;

import com.practicas.consultoriacrm.MainScreenController;
import com.practicas.consultoriacrm.Utils.Conexion;
import com.practicas.consultoriacrm.Utils.Encriptar;
import com.practicas.consultoriacrm.Utils.MetodosTabs;
import com.practicas.consultoriacrm.Utils.VerificarFormularios;

import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Objects;

public class TabGas {
    private final boolean cargandoDatos = false;
    public Conexion conexion;
    // --------------- COMPONENTES ----------------
    @FXML
    public ComboBox<String> comboTarifaGas;
    // --------------------------------------------
    @FXML
    public ComboBox<String> comboComercializadoraGas;
    @FXML
    public ComboBox<String> comboColaboradoresGas;
    @FXML
    public ComboBox<String> comboClawbackGas;
    @FXML
    public TextField txtPotenciaGas;
    // --------------------------------------------
    @FXML
    public TextField txtConsumoGas;
    @FXML
    public TextField txtModalidadGas;
    @FXML
    public TextField txtNumCuentaGas;
    @FXML
    public TextField txtDirSumGas;
    @FXML
    public TextField txtLiquidacionGas;
    @FXML
    public TextField txtContratoGas;
    @FXML
    public TextField txtComisionGas;
    @FXML
    public TextField txtPrecioGas;
    @FXML
    public TextField txtRepresentanteGas;
    @FXML
    public TextArea txtObservacionesGas;
    @FXML
    public DatePicker dateContratoGas;
    @FXML
    public DatePicker dateActivacionGas;
    @FXML
    public DatePicker dateBajaGas;
    @FXML
    public Button btnHistoricoGas;
    @FXML
    public TextField txtComisionColaborador;
    // --------- Variables para clases ------------
    private MetodosTabs metodosTabs;
    private VerificarFormularios verificar;
    // ------------ Espacio -----------------------
    private GridPane gridPane;
    private ScrollPane scrollPane;
    @FXML
    private AnchorPane anchorGas;
    // -------------------------------------------
    @FXML
    private BorderPane gasBorderPane;
    private int idCliente;
    private String ultimaCompania = null;
    private String ultimoColaborador = null;


    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }


    public void initialize() throws SQLException {
        // ------ INSTANCIA DE LA CONEXIÓN --------
        conexion = Conexion.getInstance();
        // ----------------------------------------

        metodosTabs = new MetodosTabs();
        verificar = new VerificarFormularios();

        // ##################################### GRIDPANE #######################################
        gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setMaxWidth(Double.MAX_VALUE);
        // ######################################################################################


        // ###################################### COMBOBOXES ####################################
        // Crear el ScrollPane y encapsular el GridPane
        scrollPane = new ScrollPane();
        scrollPane.setContent(gridPane);
        scrollPane.setFitToWidth(true);
        // Margenes
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setPrefHeight(200);
        scrollPane.setStyle("-fx-background: #FFF; -fx-border-color: #4d4d4d; -fx-border-width: 2px;");
        BorderPane.setMargin(this.getScrollPane(), new Insets(0, 30, 0, 0));
        // ######################################################################################

        gasBorderPane.setTop(this.getScrollPane());

        // BOTÓN MOSTRAR HISTÓRICO
        btnHistoricoGas.setOnAction(actionEvent -> {
            if (MainScreenController.hayClienteSeleccionado) {
                metodosTabs.abrirHistorico("GAS", idCliente);
            } else {
                verificar.crearAlerta("SELECCIONA UN CLIENTE", "No hay clientes seleccionados");
            }
        });


        ObservableList<String> todasComps = conexion.obtenerCompaniasPorTipo("GAS");
        ObservableList<String> colaboradores = conexion.obtenerColaboradores();
        ObservableList<String> tarifasGas = conexion.obtenerTarifasTipo("GAS");
        ObservableList<String> clawbacks = conexion.obtenerClawbacks();
        comboComercializadoraGas.setItems(todasComps);
        comboColaboradoresGas.setItems(colaboradores);
        comboTarifaGas.setItems(tarifasGas);
        comboClawbackGas.setItems(clawbacks);

        ChangeListener<String> comisionListener = (obs, oldVal, newVal) -> {
            if (!cargandoDatos && (comboComercializadoraGas.getValue() != null)) {
                try {
                    // Verificar si realmente cambió la compañía o colaborador
                    String companiaActual = comboComercializadoraGas.getValue();

                    if (!companiaActual.equals(ultimaCompania)) {
                        cargarComision();
                        ultimaCompania = companiaActual;
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        };

        comboComercializadoraGas.valueProperty().addListener(comisionListener);
    }

    public void cargarComision() throws SQLException {
        if (comboComercializadoraGas.getValue() == null) {
            txtComisionGas.clear();
            txtComisionColaborador.clear();
            return;
        }

        int idCompania = conexion.obtenerIDCompaniaPorNombreTipo(comboComercializadoraGas.getValue(), "GAS");

        try {
            String query = "SELECT COMISION FROM COMISIONES " +
                    "WHERE ID_COMPANIA = ? " +
                    "ORDER BY ID_COMISION DESC ";

            PreparedStatement stmt = conexion.getConnection().prepareStatement(query);
            stmt.setInt(1, idCompania);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                txtComisionColaborador.setText(rs.getString("COMISION"));
            } else {
                txtComisionColaborador.setText("0");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            txtComisionColaborador.setText("Error al cargar");
        }

        try {
            String query = "SELECT COMISION FROM COMISION_EMPRESA " +
                    "WHERE ID_COMPANIA = ? " +
                    "ORDER BY ID_COMISION DESC ";

            PreparedStatement stmt = conexion.getConnection().prepareStatement(query);
            stmt.setInt(1, idCompania);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                txtComisionGas.setText(rs.getString("COMISION"));
            } else {
                txtComisionGas.setText("0");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            txtComisionGas.setText("Error al cargar");
        }

    }

    // /////////////////// MÉTODO PARA AGREGAR LOS CUPS DE TIPO GAS ///////////////////////
    public void addCupsToGridPane(int numCups, int idCliente) {
        String consulta = "SELECT CODIGO_CUP AS CUP FROM CUPS WHERE ID_CLIENTE=" + idCliente + " AND TIPO='GAS'";
        gridPane.getChildren().clear();
        ArrayList<String> cups = conexion.obtenerListaDatos(consulta);

        // Calcular número de filas necesarias
        int totalFilas = (int) Math.ceil((numCups + 1) / 3.0);

        for (int i = 0; i < numCups; i++) {
            int fila = i / 3;
            int columna = i % 3;
            String cup = cups.get(i);

            // CUPS DEL CLIENTE CON BOTÓN DE BORRAR
            metodosTabs.auxiliarAgregarCups(cup, "borrar", fila, columna, i, "deleteblanco.png", numCups, idCliente, "botonBorrarCups", "GAS", gridPane, this, null);
        }

        // INPUT PARA AGREGAR NUEVO CUP - SIEMPRE AL FINAL
        int ultimaFila = totalFilas - 1;
        int columnaNuevo = numCups % 3;

        metodosTabs.auxiliarAgregarCups("", "nuevo", ultimaFila, columnaNuevo, 0, "mas.png", numCups, idCliente, "botonGestionarCliente", "GAS", gridPane, this, null);
    }

    public void vaciarGrid() {
        gridPane.getChildren().clear();
        cargarPrimerCampoPorDefecto();
    }

    // Para cargar el primer input de cups al iniciar la app
    public void cargarPrimerCampoPorDefecto() {
        metodosTabs.auxiliarAgregarCups("", "nuevo", 0, 0, 0, "mas.png", 0, idCliente, "botonGestionarCliente", "LUZ", gridPane, this, null);
    }

    public void mostrarDatos(String consulta, String cup) {
        MainScreenController.cupsEditar = true;
        MainScreenController.direccionTelefoniaInternetEditar = false;
        MainScreenController.direccionAlarmaEditar = false;
        try (PreparedStatement preparedStatement = conexion.getConnection().prepareStatement(consulta)) {
            preparedStatement.setString(1, cup);
            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {
                String tarifa = rs.getString("TARIFA");
                String potencia = rs.getString("POTENCIA");

                java.sql.Date fechaActivacionSQL = rs.getDate("FECHA_ACTIVACION");
                java.sql.Date fechaBajaSQL = rs.getDate("FECHA_BAJA");
                java.sql.Date fechaContrato = rs.getDate("FECHA_CONTRATO");

                String consumo = rs.getString("CONSUMO");
                String modalidad = rs.getString("MODALIDAD");
                String comercializadora = rs.getString("COMERCIALIZADORA");
                String colaborador = rs.getString("COLABORADOR");
                double comision = rs.getDouble("COMISION");
                double comisionEmpresa = rs.getDouble("COMISION_EMPRESA");
                String direccionSuministro = rs.getString("DIRECCION_SUMINISTRO");
                String tipoContrado = rs.getString("TIPO_CONTRATO");
                String datosRepresentante = rs.getString("DATOS_REPRESENTANTE");
                double liquidacion = rs.getDouble("LIQUIDACION");
                String precio = rs.getString("PRECIO");
                String observaciones = rs.getString("OBSERVACIONES");
                String cuenta = rs.getString("NUMERO_CUENTA");
                String clawback = rs.getString("CLAWBACK");

                // ----------- DATOS DE TEXTO --------------
                txtPotenciaGas.setText(Objects.requireNonNullElse(potencia, ""));

                txtConsumoGas.setText(Objects.requireNonNullElse(consumo, ""));

                txtComisionColaborador.setText(Double.toString(comision));

                txtModalidadGas.setText(Objects.requireNonNullElse(modalidad, ""));

                if (direccionSuministro == null) {
                    txtDirSumGas.setText("");
                } else {
                    txtDirSumGas.setText(Encriptar.decrypt(direccionSuministro));
                }

                txtContratoGas.setText(Objects.requireNonNullElse(tipoContrado, ""));

                txtRepresentanteGas.setText(Objects.requireNonNullElse(datosRepresentante, ""));

                txtLiquidacionGas.setText(Double.toString(liquidacion));

                txtPrecioGas.setText(precio);

                if (observaciones == null) {
                    txtObservacionesGas.setText("");
                } else {
                    try {
                        txtObservacionesGas.setText(Encriptar.decrypt(observaciones));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }

                if (cuenta == null) {
                    txtNumCuentaGas.setText("");
                } else {
                    try {
                        txtNumCuentaGas.setText(Encriptar.decrypt(cuenta));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
                // -----------------------------------------


                // ----------- DATOS DE COMBO --------------
                if (tarifa == null) {
                    comboTarifaGas.getSelectionModel().clearSelection();
                } else {
                    comboTarifaGas.setValue(tarifa);
                }

                if (comercializadora == null) {
                    comboComercializadoraGas.getSelectionModel().clearSelection();
                } else {
                    comboComercializadoraGas.setValue(comercializadora);
                }

                if (colaborador == null) {
                    comboColaboradoresGas.getSelectionModel().clearSelection();
                } else {
                    comboColaboradoresGas.setValue(colaborador);
                }

                txtComisionGas.setText(Double.toString(comisionEmpresa));

                if (clawback == null) {
                    comboClawbackGas.getSelectionModel().clearSelection();
                } else {
                    comboClawbackGas.setValue(clawback);
                }

                // -----------------------------------------


                // ----------- DATOS DE DATE ---------------
                if (fechaActivacionSQL == null) {
                    dateActivacionGas.setValue(null);
                } else {
                    LocalDate localDate = fechaActivacionSQL.toLocalDate();
                    dateActivacionGas.setValue(localDate);
                }

                if (fechaBajaSQL == null) {
                    dateBajaGas.setValue(null);
                } else {
                    LocalDate localDate = fechaBajaSQL.toLocalDate();
                    dateBajaGas.setValue(localDate);
                }

                if (fechaContrato == null) {
                    dateContratoGas.setValue(null);
                } else {
                    LocalDate localDate = fechaContrato.toLocalDate();
                    dateContratoGas.setValue(localDate);
                }

                // -----------------------------------------
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ScrollPane getScrollPane() {
        return scrollPane;
    }
}
