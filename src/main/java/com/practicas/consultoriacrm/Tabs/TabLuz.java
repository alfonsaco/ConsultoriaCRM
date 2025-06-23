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

public class TabLuz {
    public Conexion conexion;
    // -------- Componentes de la Pestaña --------------
    @FXML
    public TextField txtPotenciaLuz;
    // --------------------------------------------
    @FXML
    public TextField txtConsumoLuz;
    @FXML
    public TextField txtModalidadLuz;
    @FXML
    public TextField txtNumCuentaLuz;
    @FXML
    public TextField txtDirSumLuz;
    // -------------------------------------------------
    @FXML
    public TextField txtComisionLuz;
    @FXML
    public TextField txtContratoLuz;
    @FXML
    public TextField txtRepresentanteLuz;
    @FXML
    public TextField txtLiquidacionLuz;
    @FXML
    public TextField txtPrecioLuz;
    @FXML
    public TextField txtComisionColaborador;
    @FXML
    public ComboBox<String> comboTarifaLuz;
    @FXML
    public ComboBox<String> comboComercializadoraLuz;
    @FXML
    public ComboBox<String> comboColaboradorLuz;
    @FXML
    public ComboBox<String> comboClawbackLuz;
    @FXML
    public DatePicker dateBajaLuz;
    @FXML
    public DatePicker dateContratoLuz;
    @FXML
    public DatePicker dateActivacionLuz;
    @FXML
    public TextArea txtObservacionesLuz;
    // --------- Variables para clases ------------
    private MetodosTabs metodosTabs;
    // ------------ Campos de colocación ----------------
    @FXML
    private AnchorPane anchorLuz;
    @FXML
    private BorderPane luzBorderPane;
    private GridPane gridPane;
    private ScrollPane scrollPane;
    @FXML
    private Button btnMostrarHistoricoLuz;

    // -------------------------------------------------

    private VerificarFormularios verificar;

    private int idCliente;

    private boolean cargandoDatos = false;
    private String ultimaCompania = null;
    private String ultimoColaborador = null;

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    @FXML
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


        // ######################################## COMBOBOXES ##################################
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
        // #######################################################################################

        luzBorderPane.setTop(this.getScrollPane());

        ObservableList<String> todasComps = conexion.obtenerCompaniasPorTipo("LUZ");
        ObservableList<String> colaboradores = conexion.obtenerColaboradores();
        ObservableList<String> tarifasLuz = conexion.obtenerTarifasTipo("LUZ");
        ObservableList<String> clawbacks = conexion.obtenerClawbacks();
        comboTarifaLuz.setItems(tarifasLuz);
        comboComercializadoraLuz.setItems(todasComps);
        comboColaboradorLuz.setItems(colaboradores);
        comboClawbackLuz.setItems(clawbacks);

        // BOTÓN MOSTRAR HISTÓRICO
        btnMostrarHistoricoLuz.setOnAction(actionEvent -> {
            if (MainScreenController.hayClienteSeleccionado) {
                metodosTabs.abrirHistorico("LUZ", idCliente);
            } else {
                verificar.crearAlerta("SELECCIONA UN CLIENTE", "No hay clientes seleccionados");
            }
        });

        ChangeListener<String> comisionListener = (obs, oldVal, newVal) -> {
            if (!cargandoDatos && (comboComercializadoraLuz.getValue() != null)) {
                try {
                    // Verificar si realmente cambió la compañía
                    String companiaActual = comboComercializadoraLuz.getValue();

                    if (!companiaActual.equals(ultimaCompania) ) {
                        cargarComision();
                        ultimaCompania = companiaActual;
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        };

        comboComercializadoraLuz.valueProperty().addListener(comisionListener);

    }

    public void cargarComision() throws SQLException {
        if (comboComercializadoraLuz.getValue() == null) {
            txtComisionLuz.clear();
            txtComisionColaborador.clear();
            return;
        }

        int idCompania = conexion.obtenerIDCompaniaPorNombreTipo(comboComercializadoraLuz.getValue(), "LUZ");

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
                txtComisionLuz.setText(rs.getString("COMISION"));
            } else {
                txtComisionLuz.setText("0");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            txtComisionLuz.setText("Error al cargar");
        }

    }


    // /////////////////// MÉTODO PARA AGREGAR LOS CUPS DE TIPO LUZ ///////////////////////
    public void addCupsToGridPane(int numCups, int idCliente) {
        String consulta = "SELECT CODIGO_CUP AS CUP FROM CUPS WHERE ID_CLIENTE=" + idCliente + " AND TIPO='LUZ'";
        gridPane.getChildren().clear();
        ArrayList<String> cups = conexion.obtenerListaDatos(consulta);

        // Calcular número de filas necesarias
        int totalFilas = (int) Math.ceil((numCups + 1) / 3.0);

        for (int i = 0; i < numCups; i++) {
            int fila = i / 3;
            int columna = i % 3;
            String cup = cups.get(i);

            metodosTabs.auxiliarAgregarCups(cup, "borrar", fila, columna, i, "deleteblanco.png", numCups, idCliente, "botonBorrarCups", "LUZ", gridPane, null, this);
        }

        // INPUT PARA AGREGAR NUEVO CUP - SIEMPRE AL FINAL
        int ultimaFila = totalFilas - 1;
        int columnaNuevo = numCups % 3;

        metodosTabs.auxiliarAgregarCups("", "nuevo", ultimaFila, columnaNuevo, 0, "mas.png", numCups, idCliente, "botonGestionarCliente", "LUZ", gridPane, null, this);
    }

    public void vaciarGrid() {
        gridPane.getChildren().clear();

        // INPUT PARA AGREGAR NUEVO CUP - SIEMPRE AL FINAL
        metodosTabs.auxiliarAgregarCups("", "nuevo", 0, 0, 0, "mas.png", 0, idCliente, "botonGestionarCliente", "LUZ", gridPane, null, this);
    }

    // Para cargar el primer input de cups al iniciar la app
    public void cargarPrimerCampoPorDefecto() {
        metodosTabs.auxiliarAgregarCups("", "nuevo", 0, 0, 0, "mas.png", 0, idCliente, "botonGestionarCliente", "LUZ", gridPane, null, this);
    }

    public void mostrarDatos(String consulta, String cup) {
        cargandoDatos = true;
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
                java.sql.Date fechaBaja = rs.getDate("FECHA_BAJA");
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
                txtPotenciaLuz.setText(Objects.requireNonNullElse(potencia, ""));

                txtConsumoLuz.setText(Objects.requireNonNullElse(consumo, ""));

                txtModalidadLuz.setText(Objects.requireNonNullElse(modalidad, ""));

                txtComisionLuz.setText(Double.toString(comisionEmpresa));

                txtComisionColaborador.setText(Double.toString(comision));

                if (direccionSuministro == null) {
                    txtDirSumLuz.setText("");
                } else {
                    txtDirSumLuz.setText(Encriptar.decrypt(direccionSuministro));
                }

                txtContratoLuz.setText(Objects.requireNonNullElse(tipoContrado, ""));

                txtRepresentanteLuz.setText(Objects.requireNonNullElse(datosRepresentante, ""));

                txtLiquidacionLuz.setText(Double.toString(liquidacion));

                txtPrecioLuz.setText(precio);

                if (observaciones == null) {
                    txtObservacionesLuz.setText("");
                } else {
                    try {
                        txtObservacionesLuz.setText(Encriptar.decrypt(observaciones));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }

                if (cuenta == null) {
                    txtNumCuentaLuz.setText("");
                } else {
                    try {
                        txtNumCuentaLuz.setText(Encriptar.decrypt(cuenta));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
                // -----------------------------------------


                // ----------- DATOS DE COMBO --------------
                if (tarifa == null) {
                    comboTarifaLuz.getSelectionModel().clearSelection();
                } else {
                    comboTarifaLuz.setValue(tarifa);
                }


                if (comercializadora == null) {
                    comboComercializadoraLuz.getSelectionModel().clearSelection();
                } else {
                    comboComercializadoraLuz.setValue(comercializadora);
                }

                if (colaborador == null) {
                    comboColaboradorLuz.getSelectionModel().clearSelection();
                } else {
                    comboColaboradorLuz.setValue(colaborador);
                }

                if (clawback == null) {
                    comboClawbackLuz.getSelectionModel().clearSelection();
                } else {
                    comboClawbackLuz.setValue(clawback);
                }
                // ------------------------------------------


                // ----------- DATOS DE DATE ---------------
                if (fechaActivacionSQL == null) {
                    dateActivacionLuz.setValue(null);  // Si es null, el DatePicker también debe ser null
                } else {
                    // Convertir java.sql.Date directamente a LocalDate
                    LocalDate localDate = fechaActivacionSQL.toLocalDate();
                    dateActivacionLuz.setValue(localDate);
                }

                if (fechaBaja == null) {
                    dateBajaLuz.setValue(null);
                } else {
                    LocalDate localDate = fechaBaja.toLocalDate();
                    dateBajaLuz.setValue(localDate);
                }

                if (fechaContrato == null) {
                    dateContratoLuz.setValue(null);
                } else {
                    LocalDate localDate = fechaContrato.toLocalDate();
                    dateContratoLuz.setValue(localDate);
                }


                // ------------------------------------------
                ultimaCompania = comercializadora;
                ultimoColaborador = comboColaboradorLuz.getValue();
            }


        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            cargandoDatos = false;
        }
    }


    public ScrollPane getScrollPane() {
        return scrollPane;
    }

}
