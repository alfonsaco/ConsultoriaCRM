package com.practicas.consultoriacrm;

import com.calendarfx.view.CalendarView;

import com.practicas.consultoriacrm.Componentes.ComboBoxItem;
import com.practicas.consultoriacrm.Secciones.SeccionCalendario;
import com.practicas.consultoriacrm.Secciones.SeccionClientes;
import com.practicas.consultoriacrm.Tabs.*;
import com.practicas.consultoriacrm.Utils.Conexion;
import com.practicas.consultoriacrm.Utils.Encriptar;
import com.practicas.consultoriacrm.Utils.Exportar;
import com.practicas.consultoriacrm.Utils.VerificarFormularios;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;

import java.io.IOException;
import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;

public class MainScreenController {
    // --- VARIABLE PARA CUANDO SE HAGA CLICK EN UN PRIMER CLIENTE---
    public static boolean hayClienteSeleccionado = false;
    public static boolean cupsEditar = false;

    // -------- Variables del Buscador ---------
    public static boolean direccionTelefoniaInternetEditar = false;
    public static boolean direccionAlarmaEditar = false;
    public static String cupsActual;
    public static String tipoActual;
    public static String direccionActual;
    public static String direccionAlarmaActual;
    public static int privilegiosUsuario;
    final int[] ultimoTab = {0};
    private final int idUsuario = com.practicas.consultoriacrm.LoginController.idUsuario;
    @FXML public ListView<String> lista;
    public static int idClienteSeleccionado = 0;
    // ----------------------------------

    public static String nombreExportar = "";

    // ----------------------------------
    // -------- Botones sección ---------
    @FXML private Button clientesButton;
    @FXML private Button listButton;
    @FXML private Button calendarButton;
    @FXML private Button renovacionesButton;
    @FXML private Button adminButton;
    @FXML private Button empleadoButton;

    @FXML private AnchorPane renovacionesPane;
    @FXML private BorderPane clientesPane;
    @FXML private AnchorPane adminPane;
    @FXML private AnchorPane empleadoPane;

    @FXML public AnchorPane calendarioPane;
    private List<Button> botonesSeccion;


    // -------- Botones generales ---------
    @FXML private Button btnGuardarNuevoCliente;
    @FXML private Button btnModificarCliente;
    @FXML private Button btnEliminarCliente;
    @FXML private Button btnLimpiarCampos;
    @FXML private Button btnGuardarCambios;
    // --------------------------------------


    @FXML private Button btnRefrescar;
    // -----------------------------------------
    @FXML private Button btnVaciarCampoBusqueda;
    // --------------------------------------
    @FXML private Button btnCarpeta;


    // -------- Variables de Clases ---------
    private Conexion conexion;
    private VerificarFormularios verificar;
    private SeccionClientes seccionClientes;


    // -------- Variables del Buscador ---------
    @FXML private TextField buscador;
    private ObservableList<String> NIFS;
    @FXML private Scene scene;
    //IMPORTANTE SE DECLARA EL TABPANE COMO ELEMENTO PARA AÑADIRLO A LA PESTAÑA CORRESPONDIENTE
    @FXML private TabPane tabPane;
    //IMPORTANTE SE DECLARA EL LUZTABCONTAINER QUE ES POR ASI DECIRLO EL XML.
    @FXML private AnchorPane tabLuzContainer;
    //IMPORTANTE SE DECLARA EL LUZTABCONTROLLER QUE ES LO QUE NOS VA A PERMITIR EJECUTAR LOS MÉTODOS DE LA CLASE LUZTAB.
    @FXML private TabLuz tabLuzController;

    // VARIABLES DE CLASES
    @FXML private TabDatos tabDatosController;
    @FXML private AnchorPane tabDatosContainer;
    @FXML private TabGas tabGasController;
    @FXML private AnchorPane tabGasContainer;
    @FXML private TabTelefonia tabTelefoniaController;
    @FXML private AnchorPane tabTelefoniaContainer;
    @FXML private TabAlarmas tabAlarmasController;
    @FXML private AnchorPane tabAlarmasContainer;
    @FXML private AnchorPane listado;
    @FXML private TableView<Map<String, String>> tablaClientes;
    @FXML private TableColumn<Map<String, String>, String> colNombre;
    @FXML private TableColumn<Map<String, String>, String> colNif;
    @FXML private TableColumn<Map<String, String>, String> colDireccion;
    @FXML private TableColumn<Map<String, String>, String> colLocalidad;
    @FXML private TableColumn<Map<String, String>, String> colProvincia;
    @FXML private TableColumn<Map<String, String>, String> colCUPS;
    @FXML private TableColumn<Map<String, String>, String> colObservaciones;
    @FXML private TableColumn<Map<String, String>, String> colEmail;
    @FXML private TableColumn<Map<String, String>, String> colTelefono;
    @FXML private TableColumn<Map<String, String>, String> colLuz;
    @FXML private TableColumn<Map<String, String>, String> colGas;
    @FXML private TableColumn<Map<String, String>, String> colInternet;
    @FXML private TableColumn<Map<String, String>, String> colTelefonia;
    @FXML private TableColumn<Map<String, String>, String> colAlarma;
    @FXML private TableColumn<Map<String, String>, String> colDirFibra;
    @FXML private TableColumn<Map<String, String>, String> colDirAlarma;
    @FXML private TableColumn<Map<String, String>, String> colDirSuministro;
    private ObservableList<Map<String, String>> datosClientes = FXCollections.observableArrayList();
    public FilteredList<Map<String, String>> datosFiltrados = new FilteredList<>(datosClientes);
    @FXML private TextField buscarNombre;
    @FXML private TextField buscarNif;
    @FXML private TextField buscarDireccion;
    @FXML private TextField buscarLocalidad;
    @FXML private TextField buscarProvincia;
    @FXML private TextField buscarCUPS;
    @FXML private TextField buscarObservaciones;
    @FXML private TextField buscarEmail;
    @FXML private TextField buscarTelefono;
    @FXML private TextField buscarLuz;
    @FXML private TextField buscarGas;
    @FXML private TextField buscarInternet;
    @FXML private TextField buscarTelefonia;
    @FXML private TextField buscarAlarma;


    // --------------------------------------
    @FXML private TextField buscarDirFibra;
    @FXML private TextField buscarDirAlarma;
    @FXML private TextField buscarDirSuministro;


    // ---------- Botones listado -----------
    @FXML private Button btnExportarLista;
    @FXML private Button btnRecargar;
    private Exportar e;
    // --------------------------------------------------------------


    private boolean estaModificando = false;
    private boolean haPulsadoModificar = false;

    private ChangeListener<String> luzListener;
    private ChangeListener<String> gasListener;
    private ChangeListener<String> internetListener;
    private ChangeListener<String> telefoniaListener;
    private ChangeListener<String> alarmasListener;

    private ChangeListener<String> fechaLuzListener;
    private ChangeListener<String> fechaGasListener;
    private ChangeListener<String> fechaInternetListener;
    private ChangeListener<String> fechaTelefoniaListener;
    private ChangeListener<String> fechaAlarmasListener;


    @FXML private void initialize() throws IOException, SQLException {
        // ------ INSTANCIA DE LA CONEXIÓN --------
        conexion = Conexion.getInstance();
        // ----------------------------------------

        privilegiosUsuario = conexion.obtenerPrivilegiosUsuario(idUsuario);

        try {
            migrarDatosParaBusqueda();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        // Botones de Sección
        botonesSeccion = new ArrayList<>();
        botonesSeccion.add(clientesButton);
        botonesSeccion.add(listButton);
        botonesSeccion.add(calendarButton);
        botonesSeccion.add(renovacionesButton);
        botonesSeccion.add(adminButton);
        botonesSeccion.add(empleadoButton);

        clientesButton.getStyleClass().add("clickado");

        // SI INICIA SESIÓN UN USUARIO QUE NO SEA ADMIN, SE OCULTARÁ LA SECCIÓN DE ADMIN
        if(privilegiosUsuario > 1) {
            adminButton.setManaged(false);
        }


        verificar = new VerificarFormularios();
        seccionClientes = SeccionClientes.getInstance();
        e = new Exportar(this);
        SeccionCalendario c = new SeccionCalendario(calendarioPane, idUsuario);

        //Inicialización y configuración de CalendarView.
        CalendarView calendarView = new CalendarView();
        c.configurarCalendarView(calendarView);


        //Inicialización de listado.
        configurarTablaClientes();
        cargarDatosClientes();
        listado.setVisible(false);

        // Listener para búsqueda en tiempo real
        configurarListenersBusqueda();

        //Nueva VentanaDatos.
        cargarTabDatos();

        ObservableList<String> clientes = seccionClientes.obtenerClientesDesdeBD();
        NIFS = seccionClientes.obtenerNIF();
        lista.setItems(clientes);
        seccionClientes.setLista(lista);

        // LISTENERS
        buscador.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                seccionClientes.buscarClientes(newValue);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });

        lista.setOnMouseClicked(event -> clienteBuscadorOnClick());

        configurarBotonesDeAccion();

        cargarTabLuz();
        cargarTabGas();
        cargarTabTelefonia();
        cargarTabAlarmas();
        c.cargarEventosDesdeBD();

        // CARGAR PRIMER INPUT DE CUPS POR DEFECTO
        tabGasController.cargarPrimerCampoPorDefecto();
        tabLuzController.cargarPrimerCampoPorDefecto();
        tabTelefoniaController.cargarPrimerCampoPorDefecto();
        tabAlarmasController.cargarPrimerCampoPorDefecto();

        // BOOLEAN PARA EVITAR QUE SE SALGA DE LA TAB SIN GUARDAR CAMBIOS
        final BooleanProperty ignoreListener = new SimpleBooleanProperty(false);
        // Evento de cambio de tab
        tabPane.getSelectionModel().selectedIndexProperty().addListener((obs, oldIndex, newIndex) -> {
            if (oldIndex == null || newIndex == null) return;

            if(oldIndex.intValue()!=0){
                tipoActual = null;
                direccionActual = null;
                direccionAlarmaActual = null;
                cupsEditar = false;
                direccionTelefoniaInternetEditar = false;
                direccionAlarmaEditar = false;
            }
            if (ignoreListener.get()) {
                return;
            }

            if (estaModificando) {
                // Mostrar alerta de confirmación
                Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
                alerta.setTitle("Cambios sin guardar");
                alerta.setHeaderText("¿Seguro que quieres cambiar de pestaña sin guardar?");
                alerta.setContentText("Se perderán los cambios no guardados.");

                ButtonType botonSi = new ButtonType("Descartar cambios", ButtonBar.ButtonData.YES);
                ButtonType botonNo = new ButtonType("Volver", ButtonBar.ButtonData.NO);
                alerta.getButtonTypes().setAll(botonSi, botonNo);

                Optional<ButtonType> resultado = alerta.showAndWait();

                if (resultado.isPresent() && resultado.get() == botonNo) {
                    // Para evitar que el listener se vuelva a ejecutar, y así no se forma el
                    // bucle de diálogos sin poder cerrarlo
                    ignoreListener.set(true);
                    tabPane.getSelectionModel().select(oldIndex.intValue());
                    ignoreListener.set(false);

                } else {
                    // Aceptar el cambio
                    estaModificando = false;
                    ultimoTab[0] = newIndex.intValue();
                    tabDatosController.deshabilitarEdicion(tabDatosController.arrayDeTextFieldsAVaciar, tabDatosController.arrayDeComboLimpiar, tabDatosController.arrayDeComboVaciar, tabDatosController.arrayDeFechasAVaciar, tabDatosController.arrayDeObservacionesAVaciar);
                }
            } else {
                ultimoTab[0] = newIndex.intValue();
            }
        });
    }

    public void migrarDatosParaBusqueda() throws Exception {
        Connection conn;
        // Obtener la conexión una sola vez
        conn = conexion.getConnection();

        // 1. Migrar NIFs de clientes
        migrarNIFsClientes(conn);

        // 2. Migrar números de teléfono
        migrarNumerosTelefono(conn);

        // 3. Migrar códigos CUPS
        migrarCodigosCUPS(conn);
    }

    private void migrarNIFsClientes(Connection conn) throws Exception {
        String selectQuery;
        String updateQuery;
        if(privilegiosUsuario<=2) {
            selectQuery = "SELECT ID_CLIENTE, NIF FROM CLIENTES WHERE NIF IS NOT NULL";
            updateQuery = "UPDATE CLIENTES SET NIF_HASH = ? WHERE ID_CLIENTE = ?";
        }else{
            selectQuery = "SELECT ID_CLIENTE, NIF FROM CLIENTES WHERE NIF IS NOT NULL AND ID_USUARIO = "+idUsuario;
            updateQuery = "UPDATE CLIENTES SET NIF_HASH = ? WHERE ID_CLIENTE = ? AND ID_USUARIO = "+idUsuario;
        }

        try (PreparedStatement selectStmt = conn.prepareStatement(selectQuery);
             ResultSet rs = selectStmt.executeQuery()) {

            while (rs.next()) {
                String id = rs.getString("ID_CLIENTE");
                String nif = Encriptar.decrypt(rs.getString("NIF"));

                String nifHash = Encriptar.generateSearchHash(nif);

                try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                    updateStmt.setString(1, nifHash);
                    updateStmt.setString(2, id);
                    updateStmt.executeUpdate();
                }
            }
        }
    }


    private void migrarNumerosTelefono(Connection conn) throws Exception {
        String selectQuery;
        String updateQuery;
        if(privilegiosUsuario<=2) {
            selectQuery = "SELECT ID_TELEFONO, NUMERO_TELEFONO FROM TELEFONOS WHERE NUMERO_TELEFONO IS NOT NULL";
            updateQuery = "UPDATE TELEFONOS SET NUMERO_TELEFONO_HASH = ? WHERE ID_TELEFONO = ?";
        }else{
            selectQuery = "SELECT T.ID_TELEFONO, T.NUMERO_TELEFONO FROM TELEFONOS T LEFT JOIN CLIENTES C ON T.ID_CLIENTE = C.ID_CLIENTE WHERE T.NUMERO_TELEFONO IS NOT NULL AND C.ID_USUARIO = "+idUsuario;
            updateQuery = "UPDATE TELEFONOS T LEFT JOIN CLIENTES C ON T.ID_CLIENTE = C.ID_CLIENTE SET T.NUMERO_TELEFONO_HASH = ? WHERE T.ID_TELEFONO = ? AND C.ID_USUARIO = "+idUsuario;
        }

        try (PreparedStatement selectStmt = conn.prepareStatement(selectQuery);
             ResultSet rs = selectStmt.executeQuery()) {

            while (rs.next()) {
                String id = rs.getString("ID_TELEFONO");
                String telefono = Encriptar.decrypt(rs.getString("NUMERO_TELEFONO").replace(" ", ""));

                String telefonoHash = Encriptar.generateSearchHash(telefono);

                try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                    updateStmt.setString(1, telefonoHash);
                    updateStmt.setString(2, id);
                    updateStmt.executeUpdate();
                }
            }
        }
    }

    private void migrarCodigosCUPS(Connection conn) throws Exception {
        String selectQuery;
        String updateQuery;
        if(privilegiosUsuario<=2) {
            selectQuery = "SELECT ID_CUP, CODIGO_CUP FROM CUPS WHERE CODIGO_CUP IS NOT NULL";
            updateQuery = "UPDATE CUPS SET CODIGO_CUP_HASH = ? WHERE ID_CUP = ?";
        }else{
            selectQuery = "SELECT CU.ID_CUP, CU.CODIGO_CUP FROM CUPS CU LEFT JOIN CLIENTES C ON CU.ID_CLIENTE = C.ID_CLIENTE WHERE CODIGO_CUP IS NOT NULL AND C.ID_USUARIO = "+idUsuario;
            updateQuery = "UPDATE CUPS CU LEFT JOIN CLIENTES C ON CU.ID_CLIENTE = C.ID_CLIENTE SET CODIGO_CUP_HASH = ? WHERE ID_CUP = ? AND C.ID_USUARIO = "+idUsuario;
        }

        try (PreparedStatement selectStmt = conn.prepareStatement(selectQuery);
             ResultSet rs = selectStmt.executeQuery()) {

            while (rs.next()) {
                String id = rs.getString("ID_CUP");
                String codigoCup = Encriptar.decrypt(rs.getString("CODIGO_CUP"));

                String cupHash = Encriptar.generateSearchHash(codigoCup);

                try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                    updateStmt.setString(1, cupHash);
                    updateStmt.setString(2, id);
                    updateStmt.executeUpdate();
                }
            }
        }
    }


    private void configurarBotonesDeAccion() {
        // ------------- BOTONES DE ACCIÓN PARA EL USUARIO-------------------------------------------------------------
        btnGuardarNuevoCliente.setOnAction(actionEvent -> {
            try {
                tabDatosController.obtenerDatosParaGuardarCliente();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
            try {
                migrarDatosParaBusqueda();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
            tabDatosController.comboTelefono.getSelectionModel().clearSelection();
            tabDatosController.comboEmail.getSelectionModel().clearSelection();
            actualizarTablaClientes();
        });
        btnGuardarNuevoCliente.setTooltip(new Tooltip("Añadir nuevo cliente"));

        btnModificarCliente.setOnAction(actionEvent -> {
            estaModificando=true;

            tabDatosController.habilitarEdicion(tabDatosController.arrayDeTextFieldsAVaciar, tabDatosController.arrayDeComboLimpiar, tabDatosController.arrayDeComboVaciar, tabDatosController.arrayDeFechasAVaciar, tabDatosController.arrayDeObservacionesAVaciar);
            tabDatosController.deshabilitarControles(
                    tabDatosController.fechaLuz,
                    tabDatosController.fechaGas,
                    tabDatosController.fechaInternet,
                    tabDatosController.fechaTelefonia,
                    tabDatosController.fechaAlarmas,
                    tabDatosController.comboLuz,
                    tabDatosController.comboGas,
                    tabDatosController.comboInternet,
                    tabDatosController.comboCompTel,
                    tabDatosController.comboAlarma,
                    tabDatosController.cupsLuz,
                    tabDatosController.cupsGas,
                    tabDatosController.direccionesInternet,
                    tabDatosController.direccionesTelefonia,
                    tabDatosController.direccionesAlarmas,
                    tabDatosController.clawbackLuz,
                    tabDatosController.clawbackGas,
                    tabDatosController.clawbackInternet,
                    tabDatosController.clawbackTelefonia,
                    tabDatosController.clawbackAlarmas,
                    tabTelefoniaController.txtNumLineas
            );

            actualizarTablaClientes();

            tabDatosController.comboEmail.setValue("");
            tabDatosController.comboTelefono.setValue("");
            haPulsadoModificar = true;
        });
        btnModificarCliente.setTooltip(new Tooltip("Modificar datos"));


        btnEliminarCliente.setOnAction(actionEvent -> {
            tabDatosController.eliminarCliente();
            actualizarTablaClientes();

            tabGasController.vaciarGrid();
            tabGasController.cargarPrimerCampoPorDefecto();

            tabLuzController.vaciarGrid();
            tabLuzController.cargarPrimerCampoPorDefecto();

            tabAlarmasController.vaciarGrid();
            tabAlarmasController.cargarPrimerCampoPorDefecto();

            tabTelefoniaController.vaciarGrids();
            tabTelefoniaController.cargarPrimerCampoPorDefecto();
        });
        btnEliminarCliente.setTooltip(new Tooltip("Eliminar cliente"));

        btnLimpiarCampos.setOnAction(actionEvent -> {
            tabDatosController.vaciarCamposFormulario(tabDatosController.arrayDeTextFieldsAVaciar, tabDatosController.arrayDeComboLimpiar, tabDatosController.arrayDeComboVaciar, tabDatosController.arrayDeFechasAVaciar, tabDatosController.arrayDeObservacionesAVaciar);
            tabDatosController.comboTelefono.getSelectionModel().clearSelection();
            tabDatosController.comboEmail.getSelectionModel().clearSelection();
            tabDatosController.comboEmail.getItems().clear();
            tabDatosController.comboTelefono.getItems().clear();


            hayClienteSeleccionado = false;
            estaModificando = false;
            haPulsadoModificar = false;

            tabLuzController.vaciarGrid();
            tabGasController.vaciarGrid();
            tabTelefoniaController.vaciarGrids();
            tabAlarmasController.vaciarGrid();
        });
        btnLimpiarCampos.setTooltip(new Tooltip("Vaciar campos"));

        btnVaciarCampoBusqueda.setOnAction(actionEvent -> vaciarBusqueda());
        btnVaciarCampoBusqueda.setTooltip(new Tooltip("Vaciar búsqueda"));

        btnRefrescar.setOnAction(mouseEvent -> {
            seccionClientes.actualizarListaClientes();
            actualizarTablaClientes();
            hayClienteSeleccionado = false;
        });
        btnRefrescar.setTooltip(new Tooltip("Refrescar"));

        btnGuardarCambios.setOnAction(actionEvent -> {
            actualizarCliente();
            try {
                migrarDatosParaBusqueda();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
            estaModificando = false;
            haPulsadoModificar = false;
        });
        btnGuardarCambios.setTooltip(new Tooltip("Guardar cambios"));

        btnCarpeta.setOnAction(mouseEvent ->{
            e.onCrearCarpetaYDocumentacion();
        });
        btnCarpeta.setTooltip(new Tooltip("Crear DOCS"));

        btnRecargar.setOnAction(actionEvent -> {
            seccionClientes.actualizarListaClientes();
            actualizarTablaClientes();
            hayClienteSeleccionado = false;
        });
        btnRecargar.setTooltip(new Tooltip("Refrescar lista"));

        btnExportarLista.setOnAction(actionEvent -> e.exportarListadoClientes());
        btnExportarLista.setTooltip(new Tooltip("Exportar la lista a Excel"));
        //-----------------------------------------------------------------------------------------------------------
    }


    private void cargarTabDatos() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("TabDatos.fxml"));
        tabDatosContainer = loader.load();
        tabDatosController = loader.getController();
        tabPane.getTabs().getFirst().setContent(tabDatosContainer);
    }

    private void cargarTabLuz() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("TabLuz.fxml"));
        tabLuzContainer = loader.load();
        tabLuzController = loader.getController();
        //AÑADIMOS EL CONTAINER A LA PESTAÑA CORRESPONDIENTE DEL TABPANE.
        tabPane.getTabs().get(1).setContent(tabLuzContainer);
    }

    private void cargarTabGas() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("TabGas.fxml"));
        tabGasContainer = loader.load();
        tabGasController = loader.getController();
        //AÑADIMOS EL CONTAINER A LA PESTAÑA CORRESPONDIENTE DEL TABPANE.
        tabPane.getTabs().get(2).setContent(tabGasContainer);
    }

    private void cargarTabTelefonia() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("TabTelefonia.fxml"));
        tabTelefoniaContainer = loader.load();
        tabTelefoniaController = loader.getController();
        //AÑADIMOS EL CONTAINER A LA PESTAÑA CORRESPONDIENTE DEL TABPANE.
        tabPane.getTabs().get(3).setContent(tabTelefoniaContainer);
    }

    private void cargarTabAlarmas() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("TabAlarmas.fxml"));
        tabAlarmasContainer = loader.load();
        tabAlarmasController = loader.getController();
        //AÑADIMOS EL CONTAINER A LA PESTAÑA CORRESPONDIENTE DEL TABPANE.
        tabPane.getTabs().get(4).setContent(tabAlarmasContainer);
    }

    private void configurarListenersBusqueda() {
        List<TextField> camposBusqueda = Arrays.asList(
                buscarNombre, buscarNif, buscarDireccion, buscarLocalidad,
                buscarProvincia, buscarCUPS, buscarObservaciones,
                buscarEmail, buscarTelefono, buscarLuz, buscarGas, buscarInternet, buscarTelefonia, buscarAlarma, buscarDirFibra, buscarDirAlarma, buscarDirSuministro
        );

        camposBusqueda.forEach(field ->
                field.textProperty().addListener((obs, oldVal, newVal) -> filtrarClientes())
        );

    }

    private String decryptMultipleValues(String encryptedValues) {
        if (encryptedValues == null || encryptedValues.trim().isEmpty()) {
            return "";
        }

        try {
            // CORRECCIÓN: Usar Pattern.quote para escapar el separador
            String[] values = encryptedValues.split(Pattern.quote("|"));
            StringBuilder result = new StringBuilder();

            for (String value : values) {
                if (!value.trim().isEmpty()) {
                    if (!result.isEmpty()) {
                        result.append("\n");
                    }
                    try {
                        result.append(Encriptar.decrypt(value.trim()));
                    } catch (Exception e) {
                        result.append("[VALOR INVÁLIDO]");
                        System.err.println("Error desencriptando valor individual: " + value);
                    }
                }
            }

            return result.toString();
        } catch (Exception ex) {
            System.err.println("Error procesando valores múltiples: " + encryptedValues);
            return "[ERROR DESENCRIPTANDO]";
        }
    }

    private void configurarTablaClientes() {
        colCUPS.setMinWidth(250);
        colEmail.setMinWidth(250);
        colDirSuministro.setMinWidth(300);
        colDirAlarma.setMinWidth(300);
        colDirFibra.setMinWidth(300);
        colDireccion.setMinWidth(300);
        // Configurar cómo obtener los datos de cada columna
        colNombre.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get("nombre")));
        colNif.setCellValueFactory(data -> {
            try {
                return new SimpleStringProperty(Encriptar.decrypt(data.getValue().get("nif")));
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
        colDireccion.setCellValueFactory(data -> {
            try {
                return new SimpleStringProperty(Encriptar.decrypt(data.getValue().get("direccion")));
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
        colLocalidad.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get("localidad")));
        colProvincia.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get("provincia")));
        colCUPS.setCellValueFactory(data -> new SimpleStringProperty(decryptMultipleValues(data.getValue().get("cups"))));
        colDirSuministro.setCellValueFactory(data -> new SimpleStringProperty(decryptMultipleValues(data.getValue().get("dir_suministro"))));
        colObservaciones.setCellValueFactory(data -> {
            try {
                return new SimpleStringProperty(Encriptar.decrypt(data.getValue().get("observaciones")));
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
        colEmail.setCellValueFactory(data -> new SimpleStringProperty(decryptMultipleValues(data.getValue().get("email"))));
        colTelefono.setCellValueFactory(data -> new SimpleStringProperty(decryptMultipleValues(data.getValue().get("telefono"))));
        colLuz.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get("luz")));
        colGas.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get("gas")));
        colInternet.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get("internet")));
        colTelefonia.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get("telefonia")));
        colAlarma.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get("alarma")));

        colDirFibra.setCellValueFactory(data -> new SimpleStringProperty(decryptMultipleValues(data.getValue().get("dir_fibra"))));
        colDirAlarma.setCellValueFactory(data -> new SimpleStringProperty(decryptMultipleValues(data.getValue().get("dir_alarma"))));


        // Configurar celda para CUPS (apilado vertical)
        colCUPS.setCellFactory(tc -> crearCeldaApilada(colCUPS));

        // Configurar celda para Teléfonos (apilado vertical)
        colTelefono.setCellFactory(tc -> crearCeldaApilada(colTelefono));

        // Configurar celda para Emails (apilado vertical)
        colEmail.setCellFactory(tc -> crearCeldaApilada(colEmail));

        colLuz.setCellFactory(tc -> crearCeldaApilada(colLuz));
        colGas.setCellFactory(tc -> crearCeldaApilada(colGas));
        colInternet.setCellFactory(tc -> crearCeldaApilada(colInternet));
        colTelefonia.setCellFactory(tc -> crearCeldaApilada(colTelefonia));
        colAlarma.setCellFactory(tc -> crearCeldaApilada(colAlarma));
        colDirFibra.setCellFactory(tc -> crearCeldaApilada(colDirFibra));
        colDirAlarma.setCellFactory(tc -> crearCeldaApilada(colDirAlarma));
        colDirSuministro.setCellFactory(tc -> crearCeldaApilada(colDirSuministro));
        // Configurar el resto de la tabla...
        tablaClientes.setItems(datosFiltrados);

        tablaClientes.setRowFactory(tv -> {
            TableRow<Map<String, String>> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    hayClienteSeleccionado = true;

                    Map<String, String> clienteSeleccionado = row.getItem();
                    String nifCliente = clienteSeleccionado.get("nif");

                    // Cargar datos del cliente
                    cargarDatosClienteDirectamente(nifCliente);

                    // Cambiar a la vista de cliente
                    clientsButtonClicked();
                    seccionButtonClick(clientesButton);
                    tabDatosController.deshabilitarEdicion(tabDatosController.arrayDeTextFieldsAVaciar, tabDatosController.arrayDeComboLimpiar, tabDatosController.arrayDeComboVaciar, tabDatosController.arrayDeFechasAVaciar, tabDatosController.arrayDeObservacionesAVaciar);

                    for (int i = 0; i < NIFS.size(); i++) {
                        if (NIFS.get(i).equalsIgnoreCase(nifCliente)) {
                            lista.getSelectionModel().select(i);
                            lista.scrollTo(i);
                            break;
                        }
                    }
                }
            });

            return row;
        });
    }

    // Método auxiliar para crear celdas con texto apilado
    private TableCell<Map<String, String>, String> crearCeldaApilada(TableColumn<Map<String, String>, String> columna) {
        return new TableCell<>() {
            private final Text text = new Text();
            private final Tooltip tooltip = new Tooltip();

            {
                // Configuración inicial del texto
                text.wrappingWidthProperty().bind(columna.widthProperty().subtract(10));
                setGraphic(text);
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || item.isEmpty()) {
                    text.setText("");
                    setTooltip(null);
                } else {
                    // Reemplazar el separador | por saltos de línea
                    String displayText = item.replace("|", "\n");
                    text.setText(displayText);

                    // Configurar tooltip
                    tooltip.setText(displayText);
                    Tooltip.install(this, tooltip);
                }
            }
        };
    }

    private void cargarDatosClientes() {
        datosClientes = conexion.obtenerDatosClientes();

        datosFiltrados = new FilteredList<>(datosClientes);
        tablaClientes.setItems(datosFiltrados);
    }

    public void actualizarTablaClientes() {
        datosClientes = conexion.obtenerDatosClientes();
        datosFiltrados = new FilteredList<>(datosClientes);
        tablaClientes.setItems(datosFiltrados);

        // Reaplicar el filtro si hay alguno activo
        filtrarClientes();
    }

    private void cargarDatosClienteDirectamente(String nifCliente) {
        String query;
        if(privilegiosUsuario<=2) {
            query = "SELECT * FROM CLIENTES WHERE NIF = ?";
        }else{
            query = "SELECT * FROM CLIENTES WHERE NIF = ? AND ID_USUARIO = "+idUsuario;
        }

        try (PreparedStatement stmt = conexion.getConnection().prepareStatement(query)) {
            stmt.setString(1, nifCliente);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Obtener todos los campos
                String nombre = rs.getString("NOMBRE");
                String nombreComercial = rs.getString("NOMBRE_COMERCIAL");
                String direccion = rs.getString("DIRECCION");
                int codPostal = rs.getInt("COD_POSTAL");
                String localidad = rs.getString("LOCALIDAD");
                String provincia = rs.getString("PROVINCIA");
                Date fechaCreacion = rs.getDate("FECHA_CREACION");
                String observaciones = rs.getString("OBSERVACIONES");
                Date proxContacto = rs.getDate("PROX_CONTACTO");
                String tecnico = rs.getString("TECNICO");

                // Manejo de campos con posibles valores nulos
                // Nombre
                tabDatosController.txtNombre.setText(Objects.requireNonNullElse(nombre, ""));

                // NIF (ya lo tenemos en el parámetro)
                tabDatosController.txtNIF.setText(Encriptar.decrypt(nifCliente));

                // Nombre comercial
                tabDatosController.txtNombreComercial.setText(Objects.requireNonNullElse(nombreComercial, ""));

                // Dirección (desencriptar)
                if (direccion != null) {
                    tabDatosController.txtDireccion.setText(Encriptar.decrypt(direccion));
                } else {
                    tabDatosController.txtDireccion.setText("");
                }

                // Código postal
                if (codPostal != 0) {
                    tabDatosController.txtCodPostal.setText(String.valueOf(codPostal));
                } else {
                    tabDatosController.txtCodPostal.setText("");
                }

                // Localidad
                tabDatosController.txtLocalidad.setText(Objects.requireNonNullElse(localidad, ""));

                // Provincia
                tabDatosController.txtProvincia.setText(Objects.requireNonNullElse(provincia, ""));

                // Fecha creación
                if (fechaCreacion != null) {
                    tabDatosController.fechaCreacion.setValue(fechaCreacion.toLocalDate());
                } else {
                    tabDatosController.fechaCreacion.setValue(null);
                }

                // Observaciones (desencriptar)
                if (observaciones != null) {
                    tabDatosController.txtObservaciones.setText(Encriptar.decrypt(observaciones));
                } else {
                    tabDatosController.txtObservaciones.setText("");
                }

                // Próximo contacto
                if (proxContacto != null) {
                    tabDatosController.fechaProxContacto.setValue(proxContacto.toLocalDate());
                } else {
                    tabDatosController.fechaProxContacto.setValue(null);
                }

                // Técnico
                if (tecnico != null) {
                    tabDatosController.comboTecnico.setDisable(false);

                    tabDatosController.comboTecnico.setValue(tecnico);

                } else {
                    tabDatosController.comboTecnico.setValue(null);
                }

                // Cargar datos adicionales (emails y teléfonos)
                int idCliente = rs.getInt("ID_CLIENTE");
                cargarDatosAdicionales(idCliente);
            }
        } catch (SQLException e) {
            System.err.println("Error al cargar cliente con NIF " + nifCliente);
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Error inesperado al cargar cliente");
            e.printStackTrace();
        }
    }

    private void cargarDatosAdicionales(int idCliente) throws Exception {
        // Luz
        int cantCupsLuz = conexion.obtenerCupsCliente("LUZ", idCliente);
        tabLuzController.addCupsToGridPane(cantCupsLuz, idCliente);

        // Gas
        int cantCupsGas = conexion.obtenerCupsCliente("GAS", idCliente);
        tabGasController.addCupsToGridPane(cantCupsGas, idCliente);

        // Telefonía
        int cantDirTel = conexion.obtenerDireccionesClienteTelefono(idCliente);
        tabTelefoniaController.addDireccionesAGridPane(cantDirTel, idCliente);

        // Alarmas
        int cantDirAlarma = conexion.obtenerDireccionesClienteAlarma(idCliente);
        tabAlarmasController.addDireccionesAGridPane(cantDirAlarma, idCliente);

        obtenerEmails(idCliente);
        obtenerTelefonos(idCliente);

        obtenerCompLuz(idCliente);
        obtenerCompGas(idCliente);
        obtenerCompTelefonia(idCliente);
        obtenerCompInternet(idCliente);
        obtenerCompAlarma(idCliente);

    }

    private void filtrarClientes() {
        datosFiltrados.setPredicate(cliente -> {
            if (cliente == null) return false;

            try {
                // Obtener valores de búsqueda (convertir a minúsculas y trim)
                String nombre = buscarNombre.getText().trim().toLowerCase();
                String nifBusqueda = buscarNif.getText().trim().toLowerCase();
                String direccion = buscarDireccion.getText().trim().toLowerCase();
                String localidad = buscarLocalidad.getText().trim().toLowerCase();
                String provincia = buscarProvincia.getText().trim().toLowerCase();
                String cups = buscarCUPS.getText().trim().toLowerCase();
                String observaciones = buscarObservaciones.getText().trim().toLowerCase();
                String email = buscarEmail.getText().trim().toLowerCase();
                String telefono = buscarTelefono.getText().trim().toLowerCase();
                String luz = buscarLuz.getText().trim().toLowerCase();
                String gas = buscarGas.getText().trim().toLowerCase();
                String internet = buscarInternet.getText().trim().toLowerCase();
                String telefonia = buscarTelefonia.getText().trim().toLowerCase();
                String alarma = buscarAlarma.getText().trim().toLowerCase();
                String dirFibra = buscarDirFibra.getText().trim().toLowerCase();
                String dirAlarma = buscarDirAlarma.getText().trim().toLowerCase();
                String dirSuministro = buscarDirSuministro.getText().trim().toLowerCase();

                // Obtener valores del cliente con manejo de nulos
                String clienteNombre = cliente.get("nombre") != null ? cliente.get("nombre").toLowerCase() : "";
                String clienteNif = cliente.get("nif") != null ? Encriptar.decrypt(cliente.get("nif")).toLowerCase() : "";
                String clienteDireccion = cliente.get("direccion") != null ? Encriptar.decrypt(cliente.get("direccion")).toLowerCase() : "";
                String clienteLocalidad = cliente.get("localidad") != null ? cliente.get("localidad").toLowerCase() : "";
                String clienteProvincia = cliente.get("provincia") != null ? cliente.get("provincia").toLowerCase() : "";

                // Campos con múltiples valores
                String clienteCups = cliente.get("cups") != null ?
                        decryptMultipleValues(cliente.get("cups")).toLowerCase() : "";
                String clienteObservaciones = cliente.get("observaciones") != null ?
                        decryptMultipleValues(cliente.get("observaciones")).toLowerCase() : "";
                String clienteEmail = cliente.get("email") != null ?
                        decryptMultipleValues(cliente.get("email")).toLowerCase() : "";
                String clienteTelefono = cliente.get("telefono") != null ?
                        decryptMultipleValues(cliente.get("telefono")).toLowerCase() : "";

                String clienteLuz = cliente.get("luz") != null ? cliente.get("luz").toLowerCase() : "";
                String clienteGas = cliente.get("gas") != null ? cliente.get("gas").toLowerCase() : "";
                String clienteInternet = cliente.get("internet") != null ? cliente.get("internet").toLowerCase() : "";
                String clienteTelefonia = cliente.get("telefonia") != null ? cliente.get("telefonia").toLowerCase() : "";
                String clienteAlarma = cliente.get("alarma") != null ? cliente.get("alarma").toLowerCase() : "";
                String clienteDirFibra = cliente.get("dir_fibra") != null ?
                        decryptMultipleValues(cliente.get("dir_fibra")).toLowerCase() : "";
                String clienteDirAlarma = cliente.get("dir_alarma") != null ?
                        decryptMultipleValues(cliente.get("dir_alarma")).toLowerCase() : "";
                String clienteDirSuministro = cliente.get("dir_suministro") != null ?
                        decryptMultipleValues(cliente.get("dir_suministro")).toLowerCase() : "";

                // Aplicar filtros (si el campo de búsqueda está vacío, no filtrar por él)
                boolean coincideNombre = nombre.isEmpty() || clienteNombre.contains(nombre);
                boolean coincideNif = nifBusqueda.isEmpty() || clienteNif.contains(nifBusqueda);
                boolean coincideDireccion = direccion.isEmpty() || clienteDireccion.contains(direccion);
                boolean coincideLocalidad = localidad.isEmpty() || clienteLocalidad.contains(localidad);
                boolean coincideProvincia = provincia.isEmpty() || clienteProvincia.contains(provincia);
                boolean coincideCUPS = cups.isEmpty() || clienteCups.contains(cups);
                boolean coincideObservaciones = observaciones.isEmpty() || clienteObservaciones.contains(observaciones);
                boolean coincideEmail = email.isEmpty() || clienteEmail.contains(email);
                boolean coincideTelefono = telefono.isEmpty() || clienteTelefono.contains(telefono);
                boolean coincideLuz = luz.isEmpty() || clienteLuz.contains(luz);
                boolean coincideGas = gas.isEmpty() || clienteGas.contains(gas);
                boolean coincideInternet = internet.isEmpty() || clienteInternet.contains(internet);
                boolean coincideTelefonia = telefonia.isEmpty() || clienteTelefonia.contains(telefonia);
                boolean coincideAlarma = alarma.isEmpty() || clienteAlarma.contains(alarma);
                boolean coincideDirFibra = dirFibra.isEmpty() || clienteDirFibra.contains(dirFibra);
                boolean coincideDirAlarma = dirAlarma.isEmpty() || clienteDirAlarma.contains(dirAlarma);
                boolean coincideDirSuministro = dirSuministro.isEmpty() || clienteDirSuministro.contains(dirSuministro);

                return coincideNombre && coincideNif && coincideDireccion && coincideLocalidad
                        && coincideProvincia && coincideCUPS
                        && coincideObservaciones && coincideEmail && coincideTelefono
                        && coincideLuz && coincideGas && coincideInternet && coincideTelefonia
                        && coincideAlarma && coincideDirFibra && coincideDirAlarma && coincideDirSuministro;
            } catch (Exception ex) {
                ex.printStackTrace();
                return false;
            }
        });
    }

    @FXML
    private void limpiarBusqueda() {
        buscarNombre.clear();
        buscarNif.clear();
        buscarDireccion.clear();
        buscarLocalidad.clear();
        buscarProvincia.clear();
        buscarCUPS.clear();
        buscarObservaciones.clear();
        buscarEmail.clear();
        buscarTelefono.clear();
        buscarLuz.clear();
        buscarGas.clear();
        buscarInternet.clear();
        buscarTelefonia.clear();
        buscarAlarma.clear();
        buscarDirFibra.clear();
        buscarDirAlarma.clear();
        buscarDirSuministro.clear();
    }

    // SETTERS
    public void setScene(Scene scene) {
        this.scene = scene;
        Platform.runLater(() -> {
            tabDatosController.arrayDeTextFieldsAVaciar = obtenerNodos(".formulariosCliente", TextField.class);
            tabDatosController.arrayDeComboLimpiar = obtenerNodos(".comboLimpiar", ComboBox.class);
            tabDatosController.arrayDeComboVaciar = obtenerNodos(".comboVaciar", ComboBox.class);
            tabDatosController.arrayDeFechasAVaciar = obtenerNodos("DatePicker", DatePicker.class);
            tabDatosController.arrayDeObservacionesAVaciar = obtenerNodos("TextArea", TextArea.class);
        });
    }

    // Método para obtener todos los datos de X nodo y meterlos en un ArrayList
    public <T extends Node> ArrayList<T> obtenerNodos(String claseEstilo, Class<T> tipoNodo) {
        // Obtener la raíz de la escena
        Parent root = scene.getRoot();

        // Buscar todos los nodos con la clase proporcionada desde la raíz
        Set<Node> nodos = root.lookupAll(claseEstilo);
        ArrayList<T> datos = new ArrayList<>();

        for (Node node : nodos) {
            if (tipoNodo.isInstance(node)) {
                T nodo = tipoNodo.cast(node);
                datos.add(nodo);
            }
        }
        return datos;
    }

    // Al pulsar el botón, se vacía el TextField de búsqueda
    private void vaciarBusqueda() {
        buscador.setText("");
    }

    @FXML
    private void clienteBuscadorOnClick() {
        if (estaModificando) {
            Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
            alerta.setTitle("Cliente modificándose");
            alerta.setHeaderText("¿Seguro que quieres salir sin guardar los cambios?");
            alerta.setContentText("El cliente tiene cambios sin guardar.");

            ButtonType botonSi = new ButtonType("Sí", ButtonBar.ButtonData.YES);
            ButtonType botonNo = new ButtonType("No", ButtonBar.ButtonData.NO);

            alerta.getButtonTypes().setAll(botonSi, botonNo);

            Optional<ButtonType> resultado = alerta.showAndWait();
            if (resultado.isPresent() && resultado.get() == botonSi) {
                estaModificando = false;
            }

            if (resultado.isPresent() && resultado.get() == botonNo) {
                estaModificando = true;
                return;
            }
        }

        // Con este click, ya se pueden hacer cosas con los clientes
        hayClienteSeleccionado = true;
        tabDatosController.vaciarCamposFormulario(tabDatosController.arrayDeTextFieldsAVaciar, tabDatosController.arrayDeComboLimpiar, tabDatosController.arrayDeComboVaciar, tabDatosController.arrayDeFechasAVaciar, tabDatosController.arrayDeObservacionesAVaciar);
        tabDatosController.deshabilitarEdicion(tabDatosController.arrayDeTextFieldsAVaciar, tabDatosController.arrayDeComboLimpiar, tabDatosController.arrayDeComboVaciar, tabDatosController.arrayDeFechasAVaciar, tabDatosController.arrayDeObservacionesAVaciar);
        tabDatosController.checkPermanenciaLuz.setSelected(false);
        tabDatosController.checkPermanenciaGas.setSelected(false);
        tabDatosController.checkPermanenciaInternet.setSelected(false);
        tabDatosController.checkPermanenciaTel.setSelected(false);
        tabDatosController.checkPermanenciaGas.setSelected(false);
        int clienteSeleccionado = lista.getSelectionModel().getSelectedIndex();
        if (clienteSeleccionado == -1) return; // No hay selección

        // Obtén el ID del cliente usando el índice seleccionado

        Integer idCliente = (Integer) seccionClientes.clientesMap.keySet().toArray()[clienteSeleccionado];

        if (idCliente == null) {
            return;
        }

        String consulta;
        if(privilegiosUsuario<=2){
            consulta = "SELECT * FROM CLIENTES WHERE ID_CLIENTE = ?";
        }else{
            consulta = "SELECT * FROM CLIENTES WHERE ID_CLIENTE = ? AND ID_USUARIO = "+idUsuario;
        }

        try (PreparedStatement preparedStatement = conexion.getConnection().prepareStatement(consulta)) {
            preparedStatement.setInt(1, idCliente);
            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {
                idClienteSeleccionado = idCliente;
                tabAlarmasController.setIdCliente(idCliente);
                tabTelefoniaController.setIdCliente(idCliente);
                tabGasController.setIdCliente(idCliente);
                tabLuzController.setIdCliente(idCliente);

                String nombre = rs.getString("NOMBRE");
                nombreExportar = nombre;

                String nombreComercial = rs.getString("NOMBRE_COMERCIAL");
                String nif = rs.getString("NIF");
                String direccion = rs.getString("DIRECCION");
                int codPostal = rs.getInt("COD_POSTAL");
                String localidad = rs.getString("LOCALIDAD");
                String provincia = rs.getString("PROVINCIA");
                Date fechaCreacion = rs.getDate("FECHA_CREACION");
                String observaciones = rs.getString("OBSERVACIONES");
                Date proxContacto = rs.getDate("PROX_CONTACTO");
                String tecnico = rs.getString("TECNICO");

                if (nombre == null) {
                } else {
                    tabDatosController.txtNombre.setText(nombre);
                }
                if (nombreComercial == null) {
                } else {
                    tabDatosController.txtNombreComercial.setText(nombreComercial);
                }
                if (nif == null) {
                } else {
                    tabDatosController.txtNIF.setText(Encriptar.decrypt(nif));
                }
                if (direccion == null) {
                } else {
                    tabDatosController.txtDireccion.setText(Encriptar.decrypt(direccion));
                }
                if (codPostal != 0) {
                    tabDatosController.txtCodPostal.setText(String.valueOf(codPostal));
                }

                if (localidad == null) {
                } else {
                    tabDatosController.txtLocalidad.setText(localidad);
                }
                if (provincia == null) {
                } else {
                    tabDatosController.txtProvincia.setText(provincia);
                }
                if (fechaCreacion == null) {
                } else {
                    LocalDate fecha = fechaCreacion.toLocalDate();
                    tabDatosController.fechaCreacion.setValue(fecha);
                }
                if (observaciones == null) {
                } else {
                    tabDatosController.txtObservaciones.setText(Encriptar.decrypt(observaciones));
                }
                if (proxContacto == null) {
                } else {
                    LocalDate fecha = proxContacto.toLocalDate();
                    tabDatosController.fechaProxContacto.setValue(fecha);
                }
                if (tecnico != null) {
                    tabDatosController.comboTecnico.setDisable(false);

                    tabDatosController.comboTecnico.setValue(tecnico);

                }
                obtenerEmails(idCliente);
                obtenerTelefonos(idCliente);

                tabDatosController.comboLuz.getItems().clear();
                tabDatosController.comboGas.getItems().clear();
                tabDatosController.comboInternet.getItems().clear();
                tabDatosController.comboCompTel.getItems().clear();
                tabDatosController.comboAlarma.getItems().clear();

                obtenerCompLuz(idCliente);
                obtenerCompGas(idCliente);
                obtenerCompInternet(idCliente);
                obtenerCompTelefonia(idCliente);
                obtenerCompAlarma(idCliente);

                // CUPS
                //IMPORTANTE SE UTILIZA LUZTABCONTROLLER PARA LOS MÉTODOS.
                int cantCupsLuz = conexion.obtenerCupsCliente("LUZ", idCliente);
                tabLuzController.addCupsToGridPane(cantCupsLuz, idCliente);

                int cantCupsGas = conexion.obtenerCupsCliente("GAS", idCliente);
                tabGasController.addCupsToGridPane(cantCupsGas, idCliente);

                int cantDirTel = conexion.obtenerDireccionesClienteTelefono(idCliente);
                tabTelefoniaController.addDireccionesAGridPane(cantDirTel, idCliente);
                tabTelefoniaController.limpiarLineas();

                int cantDirAlarma = conexion.obtenerDireccionesClienteAlarma(idCliente);
                tabAlarmasController.addDireccionesAGridPane(cantDirAlarma, idCliente);

            }

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

    }

    public void obtenerEmails(int idCliente) throws SQLException {
        String consultaEmails = "SELECT E.EMAIL FROM CLIENTES C " +
                "LEFT JOIN EMAILS E ON C.ID_CLIENTE = E.ID_CLIENTE " +
                "WHERE C.ID_CLIENTE = " + idCliente;

        ResultSet rsEmails = conexion.listarDatos(consultaEmails);

        tabDatosController.comboEmail.getItems().clear();
        tabDatosController.comboEmail.setDisable(false);

        while (rsEmails.next()) {
            String emailEncrypted = rsEmails.getString("EMAIL");
            if (!rsEmails.wasNull()) { // Verifica si el valor no es NULL en la BD
                try {
                    String email = Encriptar.decrypt(emailEncrypted);
                    if (!email.isEmpty()) {
                        ComboBoxItem itemMail = new ComboBoxItem(email);
                        tabDatosController.comboEmail.getItems().add(itemMail);
                    }
                } catch (Exception ex) {
                    // Maneja el error de desencriptación sin romper el flujo
                    System.err.println("Error al desencriptar email: " + ex.getMessage());
                }
            }
        }

        // Solo selecciona el primer item si hay elementos
        if (!tabDatosController.comboEmail.getItems().isEmpty()) {
            tabDatosController.comboEmail.getSelectionModel().select(0);
        }
    }

    public void obtenerTelefonos(int idCliente) throws SQLException {
        String consultaTlfs = "SELECT T.NUMERO_TELEFONO FROM CLIENTES C " +
                "LEFT JOIN TELEFONOS T ON C.ID_CLIENTE = T.ID_CLIENTE " +
                "WHERE C.ID_CLIENTE = " + idCliente;

        ResultSet rsTlfs = conexion.listarDatos(consultaTlfs);

        tabDatosController.comboTelefono.getItems().clear();
        tabDatosController.comboTelefono.setDisable(false);

        while (rsTlfs.next()) {
            String telefonoEncrypted = rsTlfs.getString("NUMERO_TELEFONO");
            if (!rsTlfs.wasNull()) { // Verifica si el valor no es NULL en la BD
                try {
                    String telefono = Encriptar.decrypt(telefonoEncrypted);
                    if (!telefono.isEmpty()) {
                        ComboBoxItem itemFono = new ComboBoxItem(telefono);
                        tabDatosController.comboTelefono.getItems().add(itemFono);
                    }
                } catch (Exception ex) {
                    // Maneja el error de desencriptación sin romper el flujo
                    System.err.println("Error al desencriptar teléfono: " + ex.getMessage());
                }
            }
        }

        // Solo selecciona el primer item si hay elementos
        if (!tabDatosController.comboTelefono.getItems().isEmpty()) {
            tabDatosController.comboTelefono.getSelectionModel().select(0);
        }
    }


    public void obtenerCompLuz(int idCliente) throws SQLException {
        // Verificar componentes UI
        if (tabDatosController == null || tabDatosController.comboLuz == null ||
                tabDatosController.cupsLuz == null) {
            return;
        }

        // Limpiar combobox en hilo UI
        Platform.runLater(() -> {
            tabDatosController.comboLuz.setDisable(false);
            tabDatosController.cupsLuz.setDisable(false);
            tabDatosController.comboLuz.getItems().clear();
            tabDatosController.cupsLuz.getItems().clear();
        });

        // Eliminar listeners antiguos
        removeLuzListeners();

        // Obtener compañías de luz
        String consultaCompanias = "SELECT DISTINCT CO.ID_COMPANIA, CO.NOMBRE_COMPANIA AS NOMBRE " +
                "FROM COMPANIAS CO " +
                "JOIN CUPS CC ON CO.ID_COMPANIA = CC.ID_COMPANIA " +
                "JOIN CLIENTES C ON CC.ID_CLIENTE = C.ID_CLIENTE " +
                "WHERE C.ID_CLIENTE = ? AND CO.TIPO = 'LUZ' " +
                "ORDER BY CO.NOMBRE_COMPANIA";

        try (PreparedStatement stmt = conexion.getConnection().prepareStatement(consultaCompanias)) {
            stmt.setInt(1, idCliente);
            try (ResultSet rsCompanias = stmt.executeQuery()) {
                List<String> companias = new ArrayList<>();
                String primeraCompaniaNombre = null;

                while (rsCompanias.next()) {
                    String nombreCompania = rsCompanias.getString("NOMBRE");
                    companias.add(nombreCompania);
                    if (primeraCompaniaNombre == null) {
                        primeraCompaniaNombre = nombreCompania;
                    }
                }

                String finalPrimeraCompaniaNombre = primeraCompaniaNombre;
                Platform.runLater(() -> {
                    tabDatosController.comboLuz.getItems().addAll(companias);
                    if (finalPrimeraCompaniaNombre != null) {
                        tabDatosController.comboLuz.setValue(finalPrimeraCompaniaNombre);
                    }
                });

                if (primeraCompaniaNombre != null) {
                    cargarCupsPorCompaniaLuz(idCliente, primeraCompaniaNombre);
                }
            }
        }

        // Configurar listeners
        configurarListenersLuz(idCliente);
    }

    private void removeLuzListeners() {
        if (luzListener != null && tabDatosController.comboLuz != null) {
            tabDatosController.comboLuz.getSelectionModel().selectedItemProperty().removeListener(luzListener);
        }
        if (fechaLuzListener != null && tabDatosController.cupsLuz != null) {
            tabDatosController.cupsLuz.getSelectionModel().selectedItemProperty().removeListener(fechaLuzListener);
        }
    }

    private void cargarCupsPorCompaniaLuz(int idCliente, String nombreCompania) throws SQLException {
        Platform.runLater(() -> tabDatosController.cupsLuz.getItems().clear());

        // Remover listener temporalmente
        if (fechaLuzListener != null && tabDatosController.cupsLuz != null) {
            Platform.runLater(() ->
                    tabDatosController.cupsLuz.getSelectionModel()
                            .selectedItemProperty().removeListener(fechaLuzListener));
        }

        String sqlCups = "SELECT CC.CODIGO_CUP AS CUPS, CC.FECHA_ACTIVACION AS CONTRATO, CC.CLAWBACK AS CLAWBACK " +
                "FROM CUPS CC " +
                "JOIN COMPANIAS CO ON CC.ID_COMPANIA = CO.ID_COMPANIA " +
                "WHERE CC.ID_CLIENTE = ? AND CO.NOMBRE_COMPANIA = ? AND CC.TIPO = 'LUZ'" +
                "ORDER BY CC.FECHA_ACTIVACION";

        try (PreparedStatement stmt = conexion.getConnection().prepareStatement(sqlCups)) {
            stmt.setInt(1, idCliente);
            stmt.setString(2, nombreCompania);
            try (ResultSet rsCups = stmt.executeQuery()) {
                List<String> cupsList = new ArrayList<>();
                LocalDate primeraFecha = null;
                String primerCup = null;
                LocalDate primeraFechaClawback = null;
                String clawbackFormatted = null;

                while (rsCups.next()) {
                    String codCups = rsCups.getString("CUPS");
                    try {
                        cupsList.add(Encriptar.decrypt(codCups));
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }

                    if (primerCup == null) {
                        primerCup = codCups;
                        Date fechaContrato = rsCups.getDate("CONTRATO");
                        if (fechaContrato != null) {
                            primeraFecha = fechaContrato.toLocalDate();
                        } else {
                            primeraFecha = LocalDate.now();
                        }
                        String clawbackStr = rsCups.getString("CLAWBACK");
                        if (clawbackStr != null) {
                            tabDatosController.checkPermanenciaLuz.setSelected(true);
                            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                            if (clawbackStr.startsWith("1")) {
                                primeraFechaClawback = primeraFecha.plusYears(1);
                            }
                            if (clawbackStr.startsWith("2")) {
                                primeraFechaClawback = primeraFecha.plusMonths(2);
                            }
                            if (clawbackStr.startsWith("3")) {
                                primeraFechaClawback = primeraFecha.plusMonths(3);
                            }
                            if (clawbackStr.startsWith("4")) {
                                primeraFechaClawback = primeraFecha.plusMonths(4);
                            }
                            if (primeraFechaClawback != null)
                                clawbackFormatted = primeraFechaClawback.format(dtf);
                        }
                    }
                }

                String finalPrimerCup = primerCup;
                LocalDate finalPrimeraFecha = primeraFecha;
                String finalClawbackFormatted = clawbackFormatted;
                Platform.runLater(() -> {
                    tabDatosController.cupsLuz.getItems().addAll(cupsList);
                    if (finalPrimerCup != null) {
                        try {
                            tabDatosController.cupsLuz.setValue(Encriptar.decrypt(finalPrimerCup));
                        } catch (Exception ex) {
                            throw new RuntimeException(ex);
                        }
                        tabDatosController.fechaLuz.setValue(finalPrimeraFecha);
                        tabDatosController.clawbackLuz.setText(Objects.requireNonNullElse(finalClawbackFormatted, ""));
                    }
                });
            }
        }

        // Volver a agregar listener
        if (fechaLuzListener != null) {
            Platform.runLater(() ->
                    tabDatosController.cupsLuz.getSelectionModel()
                            .selectedItemProperty().addListener(fechaLuzListener));
        }
    }

    private void configurarListenersLuz(int idCliente) {
        // Limpiar listeners antiguos
        removeLuzListeners();

        // Listener para fecha de Luz
        fechaLuzListener = (obs, old, nuevoCup) -> {
            if (nuevoCup != null && !nuevoCup.isEmpty()) {
                try {
                    String sql = "SELECT FECHA_ACTIVACION, CLAWBACK FROM CUPS WHERE CODIGO_CUP = ?";
                    try (PreparedStatement stmt = conexion.getConnection().prepareStatement(sql)) {
                        try {
                            stmt.setString(1, Encriptar.encrypt(nuevoCup));
                        } catch (Exception ex) {
                            throw new RuntimeException(ex);
                        }
                        try (ResultSet rs = stmt.executeQuery()) {
                            if (rs.next()) {
                                Date fecha = rs.getDate(1);
                                String clawbackStr = rs.getString(2);
                                LocalDate fechaClawback = null;
                                String clawbackFormatted = null;
                                if (fecha != null) {
                                    if (clawbackStr != null) {
                                        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                                        if (clawbackStr.startsWith("1")) {
                                            fechaClawback = fecha.toLocalDate().plusYears(1);
                                        }
                                        if (clawbackStr.startsWith("2")) {
                                            fechaClawback = fecha.toLocalDate().plusMonths(2);
                                        }
                                        if (clawbackStr.startsWith("3")) {
                                            fechaClawback = fecha.toLocalDate().plusMonths(3);
                                        }
                                        if (clawbackStr.startsWith("4")) {
                                            fechaClawback = fecha.toLocalDate().plusMonths(4);
                                        }
                                        clawbackFormatted = Objects.requireNonNull(fechaClawback).format(dtf);
                                    }
                                }
                                String finalClawbackFormatted = clawbackFormatted;
                                Platform.runLater(() -> {
                                    if (fecha != null) {
                                        tabDatosController.fechaLuz.setValue(fecha.toLocalDate());
                                        if (finalClawbackFormatted != null) {
                                            tabDatosController.clawbackLuz.setText(finalClawbackFormatted);
                                        }
                                    } else {
                                        tabDatosController.fechaLuz.setValue(null);
                                        tabDatosController.clawbackLuz.setText("");
                                    }
                                });
                            }
                        }
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        };

        // Listener para compañía de Luz
        luzListener = (obs, old, nuevaCompania) -> {
            if (nuevaCompania != null && !nuevaCompania.isEmpty()) {
                try {
                    cargarCupsPorCompaniaLuz(idCliente, nuevaCompania);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        };

        // Agregar listeners
        Platform.runLater(() -> {
            tabDatosController.cupsLuz.getSelectionModel()
                    .selectedItemProperty().addListener(fechaLuzListener);
            tabDatosController.comboLuz.getSelectionModel()
                    .selectedItemProperty().addListener(luzListener);
        });
    }

    public void obtenerCompGas(int idCliente) throws SQLException {
        // Verificar componentes UI
        if (tabDatosController == null || tabDatosController.comboGas == null ||
                tabDatosController.cupsGas == null) {
            return;
        }

        // Limpiar combobox en hilo UI
        Platform.runLater(() -> {
            tabDatosController.cupsGas.setDisable(false);
            tabDatosController.comboGas.setDisable(false);
            tabDatosController.comboGas.getItems().clear();
            tabDatosController.cupsGas.getItems().clear();
        });

        // Eliminar listeners antiguos
        removeGasListeners();

        // Obtener compañías de gas
        String consultaCompanias = "SELECT DISTINCT CO.ID_COMPANIA, CO.NOMBRE_COMPANIA AS NOMBRE " +
                "FROM COMPANIAS CO " +
                "JOIN CUPS CC ON CO.ID_COMPANIA = CC.ID_COMPANIA " +
                "JOIN CLIENTES C ON CC.ID_CLIENTE = C.ID_CLIENTE " +
                "WHERE C.ID_CLIENTE = ? AND CO.TIPO = 'GAS' " +
                "ORDER BY CO.NOMBRE_COMPANIA";

        try (PreparedStatement stmt = conexion.getConnection().prepareStatement(consultaCompanias)) {
            stmt.setInt(1, idCliente);
            try (ResultSet rsCompanias = stmt.executeQuery()) {
                List<String> companias = new ArrayList<>();
                String primeraCompaniaNombre = null;

                while (rsCompanias.next()) {
                    String nombreCompania = rsCompanias.getString("NOMBRE");
                    companias.add(nombreCompania);
                    if (primeraCompaniaNombre == null) {
                        primeraCompaniaNombre = nombreCompania;
                    }
                }

                String finalPrimeraCompaniaNombre = primeraCompaniaNombre;
                Platform.runLater(() -> {
                    tabDatosController.comboGas.getItems().addAll(companias);
                    if (finalPrimeraCompaniaNombre != null) {
                        tabDatosController.comboGas.setValue(finalPrimeraCompaniaNombre);
                    }
                });

                if (primeraCompaniaNombre != null) {
                    cargarCupsPorCompaniaGas(idCliente, primeraCompaniaNombre);
                }
            }
        }

        // Configurar listeners
        configurarListenersGas(idCliente);
    }

    private void removeGasListeners() {
        if (gasListener != null && tabDatosController.comboGas != null) {
            tabDatosController.comboGas.getSelectionModel().selectedItemProperty().removeListener(gasListener);
        }
        if (fechaGasListener != null && tabDatosController.cupsGas != null) {
            tabDatosController.cupsGas.getSelectionModel().selectedItemProperty().removeListener(fechaGasListener);
        }
    }

    private void cargarCupsPorCompaniaGas(int idCliente, String nombreCompania) throws SQLException {
        Platform.runLater(() -> tabDatosController.cupsGas.getItems().clear());

        // Remover listener temporalmente
        if (fechaGasListener != null && tabDatosController.cupsGas != null) {
            Platform.runLater(() ->
                    tabDatosController.cupsGas.getSelectionModel()
                            .selectedItemProperty().removeListener(fechaGasListener));
        }

        String sqlCups = "SELECT CC.CODIGO_CUP AS CUPS, CC.FECHA_ACTIVACION AS CONTRATO, CC.CLAWBACK AS CLAWBACK " +
                "FROM CUPS CC " +
                "JOIN COMPANIAS CO ON CC.ID_COMPANIA = CO.ID_COMPANIA " +
                "WHERE CC.ID_CLIENTE = ? AND CO.NOMBRE_COMPANIA = ? AND CC.TIPO='GAS'" +
                "ORDER BY CC.FECHA_ACTIVACION";

        try (PreparedStatement stmt = conexion.getConnection().prepareStatement(sqlCups)) {
            stmt.setInt(1, idCliente);
            stmt.setString(2, nombreCompania);
            try (ResultSet rsCups = stmt.executeQuery()) {
                List<String> cupsList = new ArrayList<>();
                LocalDate primeraFecha = null;
                String primerCup = null;
                LocalDate primeraFechaClawback = null;
                String clawbackFormatted = null;

                while (rsCups.next()) {
                    String codCups = rsCups.getString("CUPS");
                    try {
                        cupsList.add(Encriptar.decrypt(codCups));
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }

                    if (primerCup == null) {
                        primerCup = codCups;
                        Date fechaContrato = rsCups.getDate("CONTRATO");
                        if (fechaContrato != null) {
                            primeraFecha = fechaContrato.toLocalDate();
                        } else {
                            primeraFecha = LocalDate.now();
                        }
                        String clawbackStr = rsCups.getString("CLAWBACK");
                        tabDatosController.checkPermanenciaGas.setSelected(true);
                        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                        if (clawbackStr.startsWith("1")) {
                            primeraFechaClawback = primeraFecha.plusYears(1);
                        }
                        if (clawbackStr.startsWith("2")) {
                            primeraFechaClawback = primeraFecha.plusMonths(2);
                        }
                        if (clawbackStr.startsWith("3")) {
                            primeraFechaClawback = primeraFecha.plusMonths(3);
                        }
                        if (clawbackStr.startsWith("4")) {
                            primeraFechaClawback = primeraFecha.plusMonths(4);
                        }
                        if (primeraFechaClawback != null)
                            clawbackFormatted = primeraFechaClawback.format(dtf);
                    }
                }

                String finalPrimerCup = primerCup;
                LocalDate finalPrimeraFecha = primeraFecha;
                String finalClawbackFormatted = clawbackFormatted;
                Platform.runLater(() -> {
                    tabDatosController.cupsGas.getItems().addAll(cupsList);
                    if (finalPrimerCup != null) {
                        try {
                            tabDatosController.cupsGas.setValue(Encriptar.decrypt(finalPrimerCup));
                        } catch (Exception ex) {
                            throw new RuntimeException(ex);
                        }
                        tabDatosController.fechaGas.setValue(finalPrimeraFecha);
                        tabDatosController.clawbackGas.setText(Objects.requireNonNullElse(finalClawbackFormatted, ""));
                    }
                });
            }
        }

        // Volver a agregar listener
        if (fechaGasListener != null) {
            Platform.runLater(() ->
                    tabDatosController.cupsGas.getSelectionModel()
                            .selectedItemProperty().addListener(fechaGasListener));
        }
    }

    private void configurarListenersGas(int idCliente) {
        // Limpiar listeners antiguos
        removeGasListeners();

        // Listener para fecha de Gas
        fechaGasListener = (obs, old, nuevoCup) -> {
            if (nuevoCup != null && !nuevoCup.isEmpty()) {
                try {
                    String sql = "SELECT FECHA_ACTIVACION, CLAWBACK FROM CUPS WHERE CODIGO_CUP = ?";
                    try (PreparedStatement stmt = conexion.getConnection().prepareStatement(sql)) {
                        try {
                            stmt.setString(1, Encriptar.encrypt(nuevoCup));
                        } catch (Exception ex) {
                            throw new RuntimeException(ex);
                        }
                        try (ResultSet rs = stmt.executeQuery()) {
                            if (rs.next()) {
                                Date fecha = rs.getDate(1);
                                String clawbackStr = rs.getString(2);
                                LocalDate fechaClawback = null;
                                String clawbackFormatted = null;
                                if (fecha != null) {
                                    if (clawbackStr != null) {
                                        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                                        if (clawbackStr.startsWith("1")) {
                                            fechaClawback = fecha.toLocalDate().plusYears(1);
                                        }
                                        if (clawbackStr.startsWith("2")) {
                                            fechaClawback = fecha.toLocalDate().plusMonths(2);
                                        }
                                        if (clawbackStr.startsWith("3")) {
                                            fechaClawback = fecha.toLocalDate().plusMonths(3);
                                        }
                                        if (clawbackStr.startsWith("4")) {
                                            fechaClawback = fecha.toLocalDate().plusMonths(4);
                                        }
                                        clawbackFormatted = Objects.requireNonNull(fechaClawback).format(dtf);
                                    }
                                }
                                String finalClawbackFormatted = clawbackFormatted;
                                Platform.runLater(() -> {
                                    if (fecha != null) {
                                        tabDatosController.fechaGas.setValue(fecha.toLocalDate());
                                        if (finalClawbackFormatted != null) {
                                            tabDatosController.clawbackGas.setText(finalClawbackFormatted);
                                        }
                                    } else {
                                        tabDatosController.fechaGas.setValue(null);
                                        tabDatosController.clawbackGas.setText("");
                                    }
                                });
                            }
                        }
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        };

        // Listener para compañía de Gas
        gasListener = (obs, old, nuevaCompania) -> {
            if (nuevaCompania != null && !nuevaCompania.isEmpty()) {
                try {
                    cargarCupsPorCompaniaGas(idCliente, nuevaCompania);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        };

        // Agregar listeners
        Platform.runLater(() -> {
            tabDatosController.cupsGas.getSelectionModel()
                    .selectedItemProperty().addListener(fechaGasListener);
            tabDatosController.comboGas.getSelectionModel()
                    .selectedItemProperty().addListener(gasListener);
        });
    }

    public void obtenerCompInternet(int idCliente) throws SQLException {
        // Verificar que los componentes UI están inicializados
        if (tabDatosController == null || tabDatosController.comboInternet == null ||
                tabDatosController.direccionesInternet == null) {
            return; // Mejor que lanzar excepción
        }

        // Limpiar combobox - ejecutar en hilo UI
        Platform.runLater(() -> {
            tabDatosController.direccionesInternet.setDisable(false);
            tabDatosController.comboInternet.setDisable(false);
            tabDatosController.comboInternet.getItems().clear();
            tabDatosController.direccionesInternet.getItems().clear();
        });

        // Eliminar listeners antiguos si existen
        removeInternetListeners();

        // Obtener compañías de internet
        String consultaCompanias = "SELECT DISTINCT CO.ID_COMPANIA, CO.NOMBRE_COMPANIA AS NOMBRE " +
                "FROM COMPANIAS CO " +
                "JOIN DIRECCION_INSTALACION_TELEFONIA CC ON CO.ID_COMPANIA = CC.ID_COMPANIA_INTERNET " +
                "JOIN CLIENTES C ON CC.ID_CLIENTE = C.ID_CLIENTE " +
                "WHERE C.ID_CLIENTE = ? AND CO.TIPO = 'INTERNET' " +
                "ORDER BY CO.NOMBRE_COMPANIA";

        try (PreparedStatement stmt = conexion.getConnection().prepareStatement(consultaCompanias)) {
            stmt.setInt(1, idCliente);
            try (ResultSet rsCompanias = stmt.executeQuery()) {
                List<String> companias = new ArrayList<>();
                String primeraCompaniaNombre = null;

                while (rsCompanias.next()) {
                    String nombreCompania = rsCompanias.getString("NOMBRE");
                    companias.add(nombreCompania);
                    if (primeraCompaniaNombre == null) {
                        primeraCompaniaNombre = nombreCompania;
                    }
                }

                // Actualizar UI en hilo principal
                String finalPrimeraCompaniaNombre = primeraCompaniaNombre;
                Platform.runLater(() -> {
                    tabDatosController.comboInternet.getItems().addAll(companias);
                    if (finalPrimeraCompaniaNombre != null) {
                        tabDatosController.comboInternet.setValue(finalPrimeraCompaniaNombre);
                    }
                });

                if (primeraCompaniaNombre != null) {
                    cargarDirPorCompaniaInternet(idCliente, primeraCompaniaNombre);
                }
            }
        }

        // Configurar listeners
        configurarListenersInternet(idCliente);
    }

    private void removeInternetListeners() {
        if (internetListener != null && tabDatosController.comboInternet != null) {
            tabDatosController.comboInternet.getSelectionModel().selectedItemProperty().removeListener(internetListener);
        }
        if (fechaInternetListener != null && tabDatosController.direccionesInternet != null) {
            tabDatosController.direccionesInternet.getSelectionModel().selectedItemProperty().removeListener(fechaInternetListener);
        }
    }

    private void cargarDirPorCompaniaInternet(int idCliente, String nombreCompania) throws SQLException {
        // Limpiar combobox en hilo UI
        Platform.runLater(() -> tabDatosController.direccionesInternet.getItems().clear());

        // Remover listener temporalmente
        if (fechaInternetListener != null && tabDatosController.direccionesInternet != null) {
            Platform.runLater(() ->
                    tabDatosController.direccionesInternet.getSelectionModel()
                            .selectedItemProperty().removeListener(fechaInternetListener));
        }

        String sqlDirecciones = "SELECT CC.DIRECCION_FIBRA AS DIR, CC.FECHA_ACTIVACION_INTERNET AS CONTRATO, CC.FECHA_PERMANENCIA_INTERNET AS PERMANENCIA " +
                "FROM DIRECCION_INSTALACION_TELEFONIA CC " +
                "JOIN COMPANIAS CO ON CC.ID_COMPANIA_INTERNET = CO.ID_COMPANIA " +
                "WHERE CC.ID_CLIENTE = ? AND CO.NOMBRE_COMPANIA = ? " +
                "ORDER BY CC.FECHA_ACTIVACION_INTERNET";

        try (PreparedStatement stmt = conexion.getConnection().prepareStatement(sqlDirecciones)) {
            stmt.setInt(1, idCliente);
            stmt.setString(2, nombreCompania);
            try (ResultSet rsDirecciones = stmt.executeQuery()) {
                List<String> direcciones = new ArrayList<>();
                LocalDate primeraFecha = null;
                LocalDate primeraFechaPermanencia = null;
                String primeraDireccion = null;

                while (rsDirecciones.next()) {
                    String direccion = rsDirecciones.getString("DIR");
                    try {
                        direcciones.add(Encriptar.decrypt(direccion));
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }

                    if (primeraDireccion == null) {
                        primeraDireccion = direccion;
                        Date fechaContrato = rsDirecciones.getDate("CONTRATO");
                        if (fechaContrato != null) {
                            primeraFecha = fechaContrato.toLocalDate();
                        }
                        Date fechaPermanencia = rsDirecciones.getDate("PERMANENCIA");
                        if (fechaPermanencia != null) {
                            primeraFechaPermanencia = fechaPermanencia.toLocalDate();
                            tabDatosController.checkPermanenciaInternet.setSelected(true);
                        }
                    }
                }

                // Actualizar UI en hilo principal
                String finalPrimeraDireccion = primeraDireccion;
                LocalDate finalPrimeraFecha = primeraFecha;
                LocalDate finalPrimeraFechaPermanencia = primeraFechaPermanencia;
                Platform.runLater(() -> {
                    tabDatosController.direccionesInternet.getItems().addAll(direcciones);
                    if (finalPrimeraDireccion != null) {
                        try {
                            tabDatosController.direccionesInternet.setValue(Encriptar.decrypt(finalPrimeraDireccion));
                        } catch (Exception ex) {
                            throw new RuntimeException(ex);
                        }
                        tabDatosController.fechaInternet.setValue(finalPrimeraFecha);
                        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                        if (finalPrimeraFechaPermanencia != null)
                            tabDatosController.clawbackInternet.setText(finalPrimeraFechaPermanencia.format(dtf));
                    }
                });
            }
        }

        // Volver a agregar listener
        if (fechaInternetListener != null) {
            Platform.runLater(() ->
                    tabDatosController.direccionesInternet.getSelectionModel()
                            .selectedItemProperty().addListener(fechaInternetListener));
        }
    }

    private void configurarListenersInternet(int idCliente) {
        // Limpiar listeners antiguos
        removeInternetListeners();

        // Listener para fecha de Internet
        fechaInternetListener = (obs, old, nuevaDireccion) -> {
            if (nuevaDireccion != null && !nuevaDireccion.isEmpty()) {
                try {
                    String sql = "SELECT FECHA_ACTIVACION_INTERNET, FECHA_PERMANENCIA_INTERNET FROM DIRECCION_INSTALACION_TELEFONIA WHERE DIRECCION_FIBRA = ?";
                    try (PreparedStatement stmt = conexion.getConnection().prepareStatement(sql)) {
                        try {
                            stmt.setString(1, Encriptar.encrypt(nuevaDireccion));
                        } catch (Exception ex) {
                            throw new RuntimeException(ex);
                        }
                        try (ResultSet rs = stmt.executeQuery()) {
                            if (rs.next()) {
                                Date fecha = rs.getDate(1);
                                Date fechaPermanencia = rs.getDate(2);
                                Platform.runLater(() -> {
                                    if (fecha != null) {
                                        tabDatosController.fechaInternet.setValue(fecha.toLocalDate());
                                    } else {
                                        tabDatosController.fechaInternet.setValue(null);
                                    }
                                    if (fechaPermanencia != null) {
                                        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                                        tabDatosController.clawbackInternet.setText(fechaPermanencia.toLocalDate().format(dtf));
                                    } else {
                                        tabDatosController.clawbackInternet.setText("");
                                    }
                                });
                            }
                        }
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        };

        // Listener para compañía de Internet
        internetListener = (obs, old, nuevaCompania) -> {
            if (nuevaCompania != null && !nuevaCompania.isEmpty()) {
                try {
                    cargarDirPorCompaniaInternet(idCliente, nuevaCompania);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        };

        // Agregar listeners
        Platform.runLater(() -> {
            tabDatosController.direccionesInternet.getSelectionModel()
                    .selectedItemProperty().addListener(fechaInternetListener);
            tabDatosController.comboInternet.getSelectionModel()
                    .selectedItemProperty().addListener(internetListener);
        });
    }

    public void obtenerCompTelefonia(int idCliente) throws SQLException {
        // Verificar que los componentes UI están inicializados
        if (tabDatosController == null || tabDatosController.comboCompTel == null ||
                tabDatosController.direccionesTelefonia == null) {
            return; // Mejor que lanzar excepción
        }

        // Limpiar combobox - ejecutar en hilo UI
        Platform.runLater(() -> {
            tabDatosController.direccionesTelefonia.setDisable(false);
            tabDatosController.comboCompTel.setDisable(false);
            tabDatosController.comboCompTel.getItems().clear();
            tabDatosController.direccionesTelefonia.getItems().clear();
        });

        // Eliminar listeners antiguos si existen
        removeTelefoniaListeners();

        // Obtener compañías de internet
        String consultaCompanias = "SELECT DISTINCT CO.ID_COMPANIA, CO.NOMBRE_COMPANIA AS NOMBRE " +
                "FROM COMPANIAS CO " +
                "JOIN DIRECCION_INSTALACION_TELEFONIA CC ON CO.ID_COMPANIA = CC.ID_COMPANIA_TELEFONIA " +
                "JOIN CLIENTES C ON CC.ID_CLIENTE = C.ID_CLIENTE " +
                "WHERE C.ID_CLIENTE = ? AND CO.TIPO = 'TELEFONIA' " +
                "ORDER BY CO.NOMBRE_COMPANIA";

        try (PreparedStatement stmt = conexion.getConnection().prepareStatement(consultaCompanias)) {
            stmt.setInt(1, idCliente);
            try (ResultSet rsCompanias = stmt.executeQuery()) {
                List<String> companias = new ArrayList<>();
                String primeraCompaniaNombre = null;

                while (rsCompanias.next()) {
                    String nombreCompania = rsCompanias.getString("NOMBRE");
                    companias.add(nombreCompania);
                    if (primeraCompaniaNombre == null) {
                        primeraCompaniaNombre = nombreCompania;
                    }
                }

                // Actualizar UI en hilo principal
                String finalPrimeraCompaniaNombre = primeraCompaniaNombre;
                Platform.runLater(() -> {
                    tabDatosController.comboCompTel.getItems().addAll(companias);
                    if (finalPrimeraCompaniaNombre != null) {
                        tabDatosController.comboCompTel.setValue(finalPrimeraCompaniaNombre);
                    }
                });

                if (primeraCompaniaNombre != null) {
                    cargarDirPorCompTelefonia(idCliente, primeraCompaniaNombre);
                }
            }
        }

        // Configurar listeners
        configurarListenersTelefonia(idCliente);
    }

    private void removeTelefoniaListeners() {
        if (telefoniaListener != null && tabDatosController.comboCompTel != null) {
            tabDatosController.comboCompTel.getSelectionModel().selectedItemProperty().removeListener(telefoniaListener);
        }
        if (fechaTelefoniaListener != null && tabDatosController.direccionesTelefonia != null) {
            tabDatosController.direccionesTelefonia.getSelectionModel().selectedItemProperty().removeListener(fechaTelefoniaListener);
        }
    }

    private void cargarDirPorCompTelefonia(int idCliente, String nombreCompania) throws SQLException {
        // Limpiar combobox en hilo UI
        Platform.runLater(() -> tabDatosController.direccionesTelefonia.getItems().clear());

        // Remover listener temporalmente
        if (fechaTelefoniaListener != null && tabDatosController.direccionesTelefonia != null) {
            Platform.runLater(() ->
                    tabDatosController.direccionesTelefonia.getSelectionModel()
                            .selectedItemProperty().removeListener(fechaTelefoniaListener));
        }

        String sqlDirecciones = "SELECT CC.DIRECCION_FIBRA AS DIR, CC.FECHA_ACTIVACION_TELEFONIA AS CONTRATO, CC.FECHA_PERMANENCIA_TELEFONIA AS PERMANENCIA " +
                "FROM DIRECCION_INSTALACION_TELEFONIA CC " +
                "JOIN COMPANIAS CO ON CC.ID_COMPANIA_TELEFONIA = CO.ID_COMPANIA " +
                "WHERE CC.ID_CLIENTE = ? AND CO.NOMBRE_COMPANIA = ? " +
                "ORDER BY CC.FECHA_ACTIVACION_TELEFONIA";

        try (PreparedStatement stmt = conexion.getConnection().prepareStatement(sqlDirecciones)) {
            stmt.setInt(1, idCliente);
            stmt.setString(2, nombreCompania);
            try (ResultSet rsDirecciones = stmt.executeQuery()) {
                List<String> direcciones = new ArrayList<>();
                LocalDate primeraFecha = null;
                String primeraDireccion = null;
                LocalDate primeraFechaPermanencia = null;

                while (rsDirecciones.next()) {
                    String direccion = rsDirecciones.getString("DIR");
                    try {
                        direcciones.add(Encriptar.decrypt(direccion));
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }

                    if (primeraDireccion == null) {
                        primeraDireccion = direccion;
                        Date fechaContrato = rsDirecciones.getDate("CONTRATO");
                        if (fechaContrato != null) {
                            primeraFecha = fechaContrato.toLocalDate();
                        }
                        Date fechaPermanencia = rsDirecciones.getDate("PERMANENCIA");
                        if (fechaPermanencia != null) {
                            primeraFechaPermanencia = fechaPermanencia.toLocalDate();
                            tabDatosController.checkPermanenciaTel.setSelected(true);
                        }
                    }
                }

                // Actualizar UI en hilo principal
                String finalPrimeraDireccion = primeraDireccion;
                LocalDate finalPrimeraFecha = primeraFecha;
                LocalDate finalPrimeraFechaPermanencia = primeraFechaPermanencia;
                Platform.runLater(() -> {
                    tabDatosController.direccionesTelefonia.getItems().addAll(direcciones);
                    if (finalPrimeraDireccion != null) {
                        try {
                            tabDatosController.direccionesTelefonia.setValue(Encriptar.decrypt(finalPrimeraDireccion));
                        } catch (Exception ex) {
                            throw new RuntimeException(ex);
                        }
                        tabDatosController.fechaTelefonia.setValue(finalPrimeraFecha);
                        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                        if (finalPrimeraFechaPermanencia != null)
                            tabDatosController.clawbackTelefonia.setText(finalPrimeraFechaPermanencia.format(dtf));
                    }
                });
            }
        }

        // Volver a agregar listener
        if (fechaTelefoniaListener != null) {
            Platform.runLater(() ->
                    tabDatosController.direccionesTelefonia.getSelectionModel()
                            .selectedItemProperty().addListener(fechaTelefoniaListener));
        }
    }

    private void configurarListenersTelefonia(int idCliente) {
        // Limpiar listeners antiguos
        removeTelefoniaListeners();

        // Listener para fecha de Internet
        fechaTelefoniaListener = (obs, old, nuevaDireccion) -> {
            if (nuevaDireccion != null && !nuevaDireccion.isEmpty()) {
                try {
                    String sql = "SELECT FECHA_ACTIVACION_TELEFONIA, FECHA_PERMANENCIA_TELEFONIA FROM DIRECCION_INSTALACION_TELEFONIA WHERE DIRECCION_FIBRA = ?";
                    try (PreparedStatement stmt = conexion.getConnection().prepareStatement(sql)) {
                        try {
                            stmt.setString(1, Encriptar.encrypt(nuevaDireccion));
                        } catch (Exception ex) {
                            throw new RuntimeException(ex);
                        }
                        try (ResultSet rs = stmt.executeQuery()) {
                            if (rs.next()) {
                                Date fecha = rs.getDate(1);
                                Date fechaPermanencia = rs.getDate(2);
                                Platform.runLater(() -> {
                                    if (fecha != null) {
                                        tabDatosController.fechaTelefonia.setValue(fecha.toLocalDate());
                                    } else {
                                        tabDatosController.fechaTelefonia.setValue(null);
                                    }

                                    if (fechaPermanencia != null) {
                                        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                                        tabDatosController.clawbackTelefonia.setText(fechaPermanencia.toLocalDate().format(dtf));
                                    } else {
                                        tabDatosController.clawbackTelefonia.setText("");
                                    }

                                });
                            }
                        }
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        };

        // Listener para compañía de Internet
        telefoniaListener = (obs, old, nuevaCompania) -> {
            if (nuevaCompania != null && !nuevaCompania.isEmpty()) {
                try {
                    cargarDirPorCompTelefonia(idCliente, nuevaCompania);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        };

        // Agregar listeners
        Platform.runLater(() -> {
            tabDatosController.direccionesTelefonia.getSelectionModel()
                    .selectedItemProperty().addListener(fechaTelefoniaListener);
            tabDatosController.comboCompTel.getSelectionModel()
                    .selectedItemProperty().addListener(telefoniaListener);
        });
    }

    public void obtenerCompAlarma(int idCliente) throws SQLException {
        // Verificar que los componentes UI están inicializados
        if (tabDatosController == null || tabDatosController.comboAlarma == null ||
                tabDatosController.direccionesAlarmas == null) {
            return; // Mejor que lanzar excepción
        }

        // Limpiar combobox - ejecutar en hilo UI
        Platform.runLater(() -> {
            tabDatosController.direccionesAlarmas.setDisable(false);
            tabDatosController.comboAlarma.setDisable(false);
            tabDatosController.comboAlarma.getItems().clear();
            tabDatosController.direccionesAlarmas.getItems().clear();
        });

        // Eliminar listeners antiguos si existen
        removeAlarmaListeners();

        // Obtener compañías de internet
        String consultaCompanias = "SELECT DISTINCT CO.ID_COMPANIA, CO.NOMBRE_COMPANIA AS NOMBRE " +
                "FROM COMPANIAS CO " +
                "JOIN DIRECCION_INSTALACION_ALARMAS CC ON CO.ID_COMPANIA = CC.ID_COMPANIA " +
                "JOIN CLIENTES C ON CC.ID_CLIENTE = C.ID_CLIENTE " +
                "WHERE C.ID_CLIENTE = ? AND CO.TIPO = 'ALARMAS' " +
                "ORDER BY CO.NOMBRE_COMPANIA";

        try (PreparedStatement stmt = conexion.getConnection().prepareStatement(consultaCompanias)) {
            stmt.setInt(1, idCliente);
            try (ResultSet rsCompanias = stmt.executeQuery()) {
                List<String> companias = new ArrayList<>();
                String primeraCompaniaNombre = null;

                while (rsCompanias.next()) {
                    String nombreCompania = rsCompanias.getString("NOMBRE");
                    companias.add(nombreCompania);
                    if (primeraCompaniaNombre == null) {
                        primeraCompaniaNombre = nombreCompania;
                    }
                }

                // Actualizar UI en hilo principal
                String finalPrimeraCompaniaNombre = primeraCompaniaNombre;
                Platform.runLater(() -> {
                    tabDatosController.comboAlarma.getItems().addAll(companias);
                    if (finalPrimeraCompaniaNombre != null) {
                        tabDatosController.comboAlarma.setValue(finalPrimeraCompaniaNombre);
                    }
                });

                if (primeraCompaniaNombre != null) {
                    cargarDirPorCompAlarma(idCliente, primeraCompaniaNombre);
                }
            }
        }

        // Configurar listeners
        configurarListenersAlarma(idCliente);
    }

    private void removeAlarmaListeners() {
        if (alarmasListener != null && tabDatosController.comboAlarma != null) {
            tabDatosController.comboAlarma.getSelectionModel().selectedItemProperty().removeListener(alarmasListener);
        }
        if (fechaAlarmasListener != null && tabDatosController.direccionesAlarmas != null) {
            tabDatosController.direccionesAlarmas.getSelectionModel().selectedItemProperty().removeListener(fechaAlarmasListener);
        }
    }

    private void cargarDirPorCompAlarma(int idCliente, String nombreCompania) throws SQLException {
        // Limpiar combobox en hilo UI
        Platform.runLater(() -> tabDatosController.direccionesAlarmas.getItems().clear());

        // Remover listener temporalmente
        if (fechaAlarmasListener != null && tabDatosController.direccionesAlarmas != null) {
            Platform.runLater(() ->
                    tabDatosController.direccionesAlarmas.getSelectionModel()
                            .selectedItemProperty().removeListener(fechaAlarmasListener));
        }

        String sqlDirecciones = "SELECT CC.DIRECCION_ALARMA AS DIR, CC.FECHA_ACTIVACION AS CONTRATO, CC.FECHA_PERMANENCIA AS PERMANENCIA " +
                "FROM DIRECCION_INSTALACION_ALARMAS CC " +
                "JOIN COMPANIAS CO ON CC.ID_COMPANIA = CO.ID_COMPANIA " +
                "WHERE CC.ID_CLIENTE = ? AND CO.NOMBRE_COMPANIA = ? " +
                "ORDER BY CC.FECHA_ACTIVACION";

        try (PreparedStatement stmt = conexion.getConnection().prepareStatement(sqlDirecciones)) {
            stmt.setInt(1, idCliente);
            stmt.setString(2, nombreCompania);
            try (ResultSet rsDirecciones = stmt.executeQuery()) {
                List<String> direcciones = new ArrayList<>();
                LocalDate primeraFecha = null;
                LocalDate primeraFechaPermanencia = null;
                String primeraDireccion = null;

                while (rsDirecciones.next()) {
                    String direccion = rsDirecciones.getString("DIR");
                    try {
                        direcciones.add(Encriptar.decrypt(direccion));
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }

                    if (primeraDireccion == null) {
                        primeraDireccion = direccion;
                        Date fechaContrato = rsDirecciones.getDate("CONTRATO");
                        if (fechaContrato != null) {
                            primeraFecha = fechaContrato.toLocalDate();
                        }
                        Date fechaPermanencia = rsDirecciones.getDate("PERMANENCIA");
                        if (fechaPermanencia != null) {
                            primeraFechaPermanencia = fechaPermanencia.toLocalDate();
                            tabDatosController.checkPermanenciaAlarm.setSelected(true);
                        }
                    }
                }

                // Actualizar UI en hilo principal
                String finalPrimeraDireccion = primeraDireccion;
                LocalDate finalPrimeraFecha = primeraFecha;
                LocalDate finalPrimeraFechaPermanencia = primeraFechaPermanencia;
                Platform.runLater(() -> {
                    tabDatosController.direccionesAlarmas.getItems().addAll(direcciones);
                    if (finalPrimeraDireccion != null) {
                        try {
                            tabDatosController.direccionesAlarmas.setValue(Encriptar.decrypt(finalPrimeraDireccion));
                        } catch (Exception ex) {
                            throw new RuntimeException(ex);
                        }
                        tabDatosController.fechaAlarmas.setValue(finalPrimeraFecha);
                        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                        if (finalPrimeraFechaPermanencia != null)
                            tabDatosController.clawbackAlarmas.setText(finalPrimeraFechaPermanencia.format(dtf));
                    }
                });
            }
        }

        // Volver a agregar listener
        if (fechaAlarmasListener != null) {
            Platform.runLater(() ->
                    tabDatosController.direccionesAlarmas.getSelectionModel()
                            .selectedItemProperty().addListener(fechaAlarmasListener));
        }
    }

    private void configurarListenersAlarma(int idCliente) {
        // Limpiar listeners antiguos
        removeAlarmaListeners();

        // Listener para fecha de Internet
        fechaAlarmasListener = (obs, old, nuevaDireccion) -> {
            if (nuevaDireccion != null && !nuevaDireccion.isEmpty()) {
                try {
                    String sql = "SELECT FECHA_ACTIVACION, FECHA_PERMANENCIA FROM DIRECCION_INSTALACION_ALARMAS WHERE DIRECCION_ALARMA = ?";
                    try (PreparedStatement stmt = conexion.getConnection().prepareStatement(sql)) {
                        try {
                            stmt.setString(1, Encriptar.encrypt(nuevaDireccion));
                        } catch (Exception ex) {
                            throw new RuntimeException(ex);
                        }
                        try (ResultSet rs = stmt.executeQuery()) {
                            if (rs.next()) {
                                Date fecha = rs.getDate(1);
                                Date fechaPermanencia = rs.getDate(2);
                                Platform.runLater(() -> {
                                    if (fecha != null) {
                                        tabDatosController.fechaAlarmas.setValue(fecha.toLocalDate());
                                    } else {
                                        tabDatosController.fechaAlarmas.setValue(null);
                                    }
                                    if (fechaPermanencia != null) {
                                        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                                        tabDatosController.clawbackAlarmas.setText(fechaPermanencia.toLocalDate().format(dtf));
                                    } else {
                                        tabDatosController.clawbackAlarmas.setText("");
                                    }
                                });
                            }
                        }
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        };

        // Listener para compañía de Internet
        alarmasListener = (obs, old, nuevaCompania) -> {
            if (nuevaCompania != null && !nuevaCompania.isEmpty()) {
                try {
                    cargarDirPorCompAlarma(idCliente, nuevaCompania);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        };

        // Agregar listeners
        Platform.runLater(() -> {
            tabDatosController.direccionesAlarmas.getSelectionModel()
                    .selectedItemProperty().addListener(fechaAlarmasListener);
            tabDatosController.comboAlarma.getSelectionModel()
                    .selectedItemProperty().addListener(alarmasListener);
        });
    }


    @FXML
    private void actualizarCliente() {
        if (!hayClienteSeleccionado || idClienteSeleccionado == 0) {
            verificar.crearAlerta("No hay cliente seleccionado", "No se puede modificar, selecciona un cliente primero.");
            return;
        }

        try {
            // Obtener los valores de los campos
            String nombre = tabDatosController.txtNombre.getText();
            String nombreComercial = tabDatosController.txtNombreComercial.getText();
            String nif;

            // Verificar que el usuario guarde los cambios de Email y teléfono
            if (tabDatosController.comboTelefono.getValue() != " " && tabDatosController.comboTelefono.getValue() != "" && tabDatosController.comboTelefono.getValue() != null) {
                verificar.crearAlerta("CAMPOS SIN GUARDAR", "Pulsa \"ENTER\" en el combo de teléfonos para guardar el teléfono");
                return;
            }
            if (tabDatosController.comboEmail.getValue() != "" && tabDatosController.comboEmail.getValue() != " " && tabDatosController.comboEmail.getValue() != null) {
                verificar.crearAlerta("CAMPOS SIN GUARDAR", "Pulsa \"ENTER\" en el combo de Emails para guardar el email");
                return;
            }

            try {
                nif = Encriptar.encrypt(tabDatosController.txtNIF.getText());
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
            // Verificaciones de datos
            try {
                if (!verificar.esCIF(Encriptar.decrypt(nif)) && !verificar.esDNI(Encriptar.decrypt(nif))) {
                    verificar.crearAlerta("DNI/CIF Inválido", "El DNI/CIF insertado no es válido");
                    return;
                }
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }

            // Verificar que el DNI no esté repetido
            String consultaDNI = "SELECT COUNT(NIF) FROM CLIENTES WHERE NIF = ? AND ID_CLIENTE <> "+idClienteSeleccionado;
            if (conexion.verificarExistenciaDato(consultaDNI, nif) > 0) {
                verificar.crearAlerta("DNI/CIF repetido", "El DNI/CIF insertado ya existe en la base de datos");
                return;
            }


            String direccion;
            try {
                direccion = Encriptar.encrypt(tabDatosController.txtDireccion.getText());
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
            int codPostal;
            try {
                codPostal = Integer.parseInt(tabDatosController.txtCodPostal.getText());

            } catch (NumberFormatException io) {
                codPostal = 0;
            }

            String localidad = tabDatosController.txtLocalidad.getText();
            String provincia = tabDatosController.txtProvincia.getText();
            LocalDate fechaCreacion = tabDatosController.fechaCreacion.getValue();
            String observaciones;
            try {
                observaciones = Encriptar.encrypt(tabDatosController.txtObservaciones.getText());
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
            LocalDate proxContacto = tabDatosController.fechaProxContacto.getValue();
            String tecnico = tabDatosController.comboTecnico.getValue();

            // Consulta SQL parametrizada
            String sql = "UPDATE CLIENTES SET " +
                    "NOMBRE = ?, " +
                    "NOMBRE_COMERCIAL = ?, " +
                    "NIF = ?, " +
                    "DIRECCION = ?, " +
                    "COD_POSTAL = ?, " +
                    "LOCALIDAD = ?, " +
                    "PROVINCIA = ?, " +
                    "FECHA_CREACION = ?, " +
                    "OBSERVACIONES = ?, " +
                    "PROX_CONTACTO = ?, " +
                    "TECNICO = ? " +
                    "WHERE ID_CLIENTE = ?";

            try (PreparedStatement stmt = conexion.getConnection().prepareStatement(sql)) {
                // Establecer parámetros
                stmt.setString(1, nombre != null ? nombre : null);
                stmt.setString(2, nombreComercial != null ? nombreComercial : null);
                stmt.setString(3, nif != null ? nif : null);
                stmt.setString(4, direccion != null ? direccion : null);
                stmt.setInt(5, codPostal);
                stmt.setString(6, localidad != null ? localidad : null);
                stmt.setString(7, provincia != null ? provincia : null);
                stmt.setDate(8, fechaCreacion != null ? Date.valueOf(fechaCreacion) : null);
                stmt.setString(9, observaciones != null ? observaciones : null);
                stmt.setDate(10, proxContacto != null ? Date.valueOf(proxContacto) : null);
                stmt.setString(11, tecnico != null ? tecnico : null);
                stmt.setInt(12, idClienteSeleccionado);

                // Ejecutar actualización
                int filasAfectadas = stmt.executeUpdate();

                ArrayList<ComboBoxItem> listaEmails = new ArrayList(tabDatosController.comboEmail.getItems());
                ArrayList<ComboBoxItem> listaTelefonos = new ArrayList(tabDatosController.comboTelefono.getItems());

                tabDatosController.insertarEmails(listaEmails, idClienteSeleccionado);
                tabDatosController.insertarTelefonos(listaTelefonos, idClienteSeleccionado);

                boolean algunaActualizacion = false;

                boolean seIntentoEditar = cupsEditar || direccionAlarmaEditar || direccionTelefoniaInternetEditar;

                if (tipoActual != null && cupsEditar) {
                    if (actualizarCups(tipoActual)) {
                        obtenerCompLuz(idClienteSeleccionado);
                        obtenerCompGas(idClienteSeleccionado);
                        algunaActualizacion = true;
                    }
                }
                if (direccionActual != null && direccionTelefoniaInternetEditar) {
                    if (actualizarDirecciones()) {
                        obtenerCompInternet(idClienteSeleccionado);
                        obtenerCompTelefonia(idClienteSeleccionado);
                        algunaActualizacion = true;
                    }
                }

                if (direccionAlarmaActual != null && direccionAlarmaEditar) {
                    if (actualizarDirAlarmas()) {
                        obtenerCompAlarma(idClienteSeleccionado);
                        algunaActualizacion = true;
                    }
                }


                if (filasAfectadas > 0) {
                    if (!seIntentoEditar || algunaActualizacion) {
                        verificar.crearAlerta("Éxito", "Cliente modificado correctamente");
                    }
                    tabDatosController.deshabilitarEdicion(tabDatosController.arrayDeTextFieldsAVaciar, tabDatosController.arrayDeComboLimpiar, tabDatosController.arrayDeComboVaciar, tabDatosController.arrayDeFechasAVaciar, tabDatosController.arrayDeObservacionesAVaciar);
                    tabDatosController.comboLuz.setDisable(false);
                    tabDatosController.cupsLuz.setDisable(false);
                    tabDatosController.comboGas.setDisable(false);
                    tabDatosController.cupsGas.setDisable(false);
                    tabDatosController.comboInternet.setDisable(false);
                    tabDatosController.direccionesInternet.setDisable(false);
                    tabDatosController.comboCompTel.setDisable(false);
                    tabDatosController.direccionesTelefonia.setDisable(false);
                    tabDatosController.comboAlarma.setDisable(false);
                    tabDatosController.direccionesAlarmas.setDisable(false);
                    estaModificando = false;
                    // Actualizar la lista de clientes si es necesario
                    seccionClientes.actualizarListaClientes();
                } else {
                    verificar.crearAlerta("Error", "No se pudo actualizar el cliente");
                }
            }
        } catch (NumberFormatException e) {
            verificar.crearAlerta("Error", "El código postal debe ser un número");
        } catch (SQLException e) {
            verificar.crearAlerta("Error", "Error al actualizar el cliente: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public boolean actualizarDirecciones() throws SQLException {
        if (direccionActual == null || direccionActual.trim().isEmpty()) {
            return false;
        }

        // Verificar que al menos hay una compañía seleccionada
        try {
            if (tabTelefoniaController.comboTelefonia.getValue() == null &&
                    tabTelefoniaController.comboInternet.getValue() == null) {
                throw new InterruptedException("No has introducido una compañía");
            }
        } catch (InterruptedException ex) {
            verificar.crearAlerta("Error", "No se ha introducido una compañía");
            System.out.println(ex.getMessage());
            return false;
        }

        boolean exito;
        Connection conn = null;

        try {
            conn = conexion.getConnection();
            conn.setAutoCommit(false);

            // Obtener todos los valores posibles
            String nombreTelefonia = tabTelefoniaController.comboTelefonia.getValue();
            String nombreInternet = tabTelefoniaController.comboInternet.getValue();
            String donanteTelefonia = tabTelefoniaController.txtDonanteTlf.getText();
            String donanteInternet = tabTelefoniaController.txtDonanteInternet.getText();
            String numCuenta;
            try {
                numCuenta = Encriptar.encrypt(tabTelefoniaController.numeroCuenta.getText());
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
            String observaciones;
            try {
                observaciones = Encriptar.encrypt(tabTelefoniaController.txtObservacionesTelefonia.getText());
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }

            // Obtener fechas
            LocalDate fechaContratoTelefonia = tabTelefoniaController.fechaContratoTelefonia.getValue();
            LocalDate fechaPermanenciaTelefonia = tabTelefoniaController.fechaPermanenciaTelefonia.getValue();
            LocalDate fechaContratoInternet = tabTelefoniaController.fechaContratoInternet.getValue();
            LocalDate fechaPermanenciaInternet = tabTelefoniaController.fechaPermanenciaInternet.getValue();

            // Obtener IDs de compañías (manejando nulos)
            Integer idCompaniaTelefonia = (nombreTelefonia != null) ?
                    conexion.obtenerIDCompaniaPorNombreTipo(nombreTelefonia, "TELEFONIA") : null;
            Integer idCompaniaInternet = (nombreInternet != null) ?
                    conexion.obtenerIDCompaniaPorNombreTipo(nombreInternet, "INTERNET") : null;


            // Consulta única de UPDATE
            String sqlUpdate = "UPDATE DIRECCION_INSTALACION_TELEFONIA SET " +
                    "NUMERO_CUENTA = ?, " +
                    "OBSERVACIONES = ?, " +
                    "ID_COMPANIA_TELEFONIA = ?, " +
                    "COMPANIA_DONANTE_TELEFONIA = ?, " +
                    "FECHA_ACTIVACION_TELEFONIA = ?, " +
                    "FECHA_PERMANENCIA_TELEFONIA = ?, " +
                    "ID_COMPANIA_INTERNET = ?, " +
                    "COMPANIA_DONANTE_INTERNET = ?, " +
                    "FECHA_ACTIVACION_INTERNET = ?, " +
                    "FECHA_PERMANENCIA_INTERNET = ? " +
                    "WHERE DIRECCION_FIBRA = ? AND ID_CLIENTE = ?";

            try (PreparedStatement stmt = conn.prepareStatement(sqlUpdate)) {
                // Campos comunes
                stmt.setString(1, numCuenta != null ? numCuenta : "");
                stmt.setString(2, observaciones != null ? observaciones : "");

                // Campos de telefonía
                if (idCompaniaTelefonia != null) {
                    stmt.setInt(3, idCompaniaTelefonia);
                } else {
                    stmt.setNull(3, Types.INTEGER);
                }

                stmt.setString(4, donanteTelefonia != null ? donanteTelefonia : "");

                stmt.setDate(5, fechaContratoTelefonia != null ? Date.valueOf(fechaContratoTelefonia) : null);
                stmt.setDate(6, fechaPermanenciaTelefonia != null ? Date.valueOf(fechaPermanenciaTelefonia) : null);

                // Campos de internet
                if (idCompaniaInternet != null) {
                    stmt.setInt(7, idCompaniaInternet);
                } else {
                    stmt.setNull(7, Types.INTEGER);
                }

                stmt.setString(8, donanteInternet != null ? donanteInternet : "");

                stmt.setDate(9, fechaContratoInternet != null ? Date.valueOf(fechaContratoInternet) : null);
                stmt.setDate(10, fechaPermanenciaInternet != null ? Date.valueOf(fechaPermanenciaInternet) : null);

                // Condiciones WHERE
                stmt.setString(11, direccionActual);
                stmt.setInt(12, idClienteSeleccionado);

                exito = stmt.executeUpdate() > 0;
            }

            if (exito) {
                conn.commit();
            } else {
                conn.rollback();
            }

            return exito;
        } catch (SQLException ex) {
            conn.rollback();
            throw ex;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException ex) {
                    System.err.println("Error al cerrar conexión: " + ex.getMessage());
                }
            }
        }
    }


    public boolean actualizarCups(String tipo) throws SQLException {
        // Validar que el CUPS no esté vacío
        if (cupsActual == null || cupsActual.trim().isEmpty()) {
            verificar.crearAlerta("Error", "El CUPS no puede estar vacío");
            return false;
        }

        String sql = "UPDATE CUPS SET " +
                "ID_COMPANIA = ?, " +
                "TARIFA = ?, " +
                "POTENCIA = ?, " +
                "FECHA_ACTIVACION = ?, " +
                "FECHA_BAJA = ?, " +
                "CONSUMO = ?, " +
                "MODALIDAD = ?, " +
                "COMERCIALIZADORA = ?, " +
                "COLABORADOR = ?, " +
                "COMISION = ?, " +
                "COMISION_EMPRESA = ?," +
                "DIRECCION_SUMINISTRO = ?, " +
                "TIPO_CONTRATO = ?, " +
                "DATOS_REPRESENTANTE = ?, " +
                "LIQUIDACION = ?, " +
                "PRECIO = ?, " +
                "OBSERVACIONES = ?, " +
                "NUMERO_CUENTA = ?, " +
                "CLAWBACK = ?, " +
                "TIPO = ?, " +
                "FECHA_CONTRATO = ? " +
                "WHERE CODIGO_CUP = ? AND ID_CLIENTE = ?";

        try (PreparedStatement stmt = conexion.getConnection().prepareStatement(sql)) {
            String tarifa, potencia, consumo, modalidad, comercializadora, comisionStr;
            String direccionSuministro, tipoContrato, datosRepresentante, liquidacionStr;
            String precioStr, observaciones, numeroCuenta, colaborador, comisionEmpresa, clawback;
            LocalDate fechaActivacion, fechaBaja, fechaContrato;
            // Obtener valores de los campos de la interfaz
            if (tipo.equals("LUZ")) {
                tarifa = tabLuzController.comboTarifaLuz.getValue();
                potencia = tabLuzController.txtPotenciaLuz.getText();

                fechaActivacion = tabLuzController.dateActivacionLuz.getValue();
                fechaContrato = tabLuzController.dateContratoLuz.getValue();
                fechaBaja = tabLuzController.dateBajaLuz.getValue();
                consumo = tabLuzController.txtConsumoLuz.getText();
                modalidad = tabLuzController.txtModalidadLuz.getText();
                comercializadora = tabLuzController.comboComercializadoraLuz.getValue();
                colaborador = tabLuzController.comboColaboradorLuz.getValue();
                comisionStr = tabLuzController.txtComisionColaborador.getText();
                comisionEmpresa = tabLuzController.txtComisionLuz.getText();
                try {
                    direccionSuministro = Encriptar.encrypt(tabLuzController.txtDirSumLuz.getText());
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
                tipoContrato = tabLuzController.txtContratoLuz.getText();
                datosRepresentante = tabLuzController.txtRepresentanteLuz.getText();
                liquidacionStr = tabLuzController.txtLiquidacionLuz.getText();
                precioStr = tabLuzController.txtPrecioLuz.getText();
                try {
                    observaciones = Encriptar.encrypt(tabLuzController.txtObservacionesLuz.getText());
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
                try {
                    numeroCuenta = Encriptar.encrypt(tabLuzController.txtNumCuentaLuz.getText());
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }

                clawback = tabLuzController.comboClawbackLuz.getValue();


            } else {
                tarifa = tabGasController.comboTarifaGas.getValue();
                potencia = tabGasController.txtPotenciaGas.getText();

                fechaActivacion = tabGasController.dateActivacionGas.getValue();
                fechaContrato = tabGasController.dateContratoGas.getValue();
                fechaBaja = tabGasController.dateBajaGas.getValue();
                consumo = tabGasController.txtConsumoGas.getText();
                modalidad = tabGasController.txtModalidadGas.getText();
                comercializadora = tabGasController.comboComercializadoraGas.getValue();
                colaborador = tabGasController.comboColaboradoresGas.getValue();
                comisionStr = tabGasController.txtComisionColaborador.getText();
                comisionEmpresa = tabGasController.txtComisionGas.getText();
                try {
                    direccionSuministro = Encriptar.encrypt(tabGasController.txtDirSumGas.getText());
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
                tipoContrato = tabGasController.txtContratoGas.getText();
                datosRepresentante = tabGasController.txtRepresentanteGas.getText();
                liquidacionStr = tabGasController.txtLiquidacionGas.getText();
                precioStr = tabGasController.txtPrecioGas.getText();
                try {
                    observaciones = Encriptar.encrypt(tabGasController.txtObservacionesGas.getText());
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
                try {
                    numeroCuenta = Encriptar.encrypt(tabGasController.txtNumCuentaGas.getText());
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
                clawback = tabGasController.comboClawbackGas.getValue();

            }

            // VERIFICAR QUE SE HAYA SELECCIONADO UNA COMPAÑÍA
            if (comercializadora == null || comercializadora.trim().isEmpty()) {
                verificar.crearAlerta("SELECCIONA COMPAÑÍA", "Debes seleccionar una compañía para el cup de " + tipo);
                return false;
            }

            Double comision = comisionStr.isEmpty() ? null : Double.parseDouble(comisionStr);
            Double liquidacion = liquidacionStr.isEmpty() ? null : Double.parseDouble(liquidacionStr);

            int idCompania = conexion.obtenerIDCompaniaPorNombreTipo(comercializadora, tipo);

            // Establecer parámetros en el PreparedStatement
            int paramIndex = 1;
            stmt.setInt(paramIndex++, idCompania);
            stmt.setString(paramIndex++, tarifa);
            stmt.setString(paramIndex++, potencia.isEmpty() ? null : potencia);
            stmt.setDate(paramIndex++, fechaActivacion != null ? Date.valueOf(fechaActivacion) : null);
            stmt.setDate(paramIndex++, fechaBaja != null ? Date.valueOf(fechaBaja) : null);
            stmt.setString(paramIndex++, consumo.isEmpty() ? null : consumo);
            stmt.setString(paramIndex++, modalidad.isEmpty() ? null : modalidad);
            stmt.setString(paramIndex++, comercializadora);
            stmt.setString(paramIndex++, colaborador);
            stmt.setObject(paramIndex++, comision, Types.DOUBLE);
            stmt.setObject(paramIndex++, comisionEmpresa, Types.DOUBLE);
            stmt.setString(paramIndex++, direccionSuministro.isEmpty() ? null : direccionSuministro);
            stmt.setString(paramIndex++, tipoContrato.isEmpty() ? null : tipoContrato);
            stmt.setString(paramIndex++, datosRepresentante.isEmpty() ? null : datosRepresentante);
            stmt.setObject(paramIndex++, liquidacion, Types.DOUBLE);
            stmt.setString(paramIndex++, precioStr.isEmpty() ? null: precioStr);
            stmt.setString(paramIndex++, observaciones.isEmpty() ? null : observaciones);
            stmt.setString(paramIndex++, numeroCuenta.isEmpty() ? null : numeroCuenta);
            stmt.setString(paramIndex++, clawback != null ? clawback : "");
            stmt.setString(paramIndex++, tipo);
            stmt.setDate(paramIndex++, fechaContrato != null ? Date.valueOf(fechaContrato) : Date.valueOf(LocalDate.now()));
            stmt.setString(paramIndex++, cupsActual);
            stmt.setInt(paramIndex++, idClienteSeleccionado);

            // Ejecutar la actualización
            int filasActualizadas = stmt.executeUpdate();

            if (filasActualizadas == 0) {
                verificar.crearAlerta("Error", "No se actualizó ningún registro. Verifique el CUPS y el ID del cliente.");
                return false;
            }

            return true;

        } catch (NumberFormatException e) {
            verificar.crearAlerta("Error", "Formato incorrecto en campos numéricos: " + e.getMessage());
            return false;
        }
    }

    public boolean actualizarDirAlarmas() throws SQLException {
        if (direccionAlarmaActual == null || direccionAlarmaActual.trim().isEmpty()) {
            verificar.crearAlerta("Error", "La dirección no puede estar vacía");
            return false;
        } else {
            String sql = "UPDATE DIRECCION_INSTALACION_ALARMAS SET " +
                    "ID_COMPANIA = ?, " +
                    "NUMERO_CUENTA = ?, " +
                    "LIQUIDACION = ?, " +
                    "PRECIO = ?, " +
                    "OBSERVACIONES = ?, " +
                    "FECHA_ACTIVACION = ?, " +
                    "FECHA_BAJA = ?, " +
                    "COMISION = ?, " +
                    "TIPO_CONTRATO = ?, " +
                    "FECHA_PERMANENCIA = ? " +
                    "WHERE DIRECCION_ALARMA = ?";

            try (PreparedStatement stmt = conexion.getConnection().prepareStatement(sql)) {
                String nombreCompania = tabAlarmasController.comboCompania.getValue();
                if (nombreCompania.isEmpty()) {
                    verificar.crearAlerta("Error", "No se ha introducido una compañía");
                    throw new RuntimeException("Sin compañía");
                }
                int idCompania = conexion.obtenerIDCompaniaPorNombreTipo(nombreCompania, "ALARMAS");
                String numeroCuenta;
                try {
                    numeroCuenta = Encriptar.encrypt(tabAlarmasController.txtNumCuentaAlarma.getText());
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
                Double liquidacion = Double.valueOf(tabAlarmasController.txtLiquidacion.getText());
                String precio = tabAlarmasController.txtPrecio.getText();
                String observaciones;
                try {
                    observaciones = Encriptar.encrypt(tabAlarmasController.txtObservacionesAlarma.getText());
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
                LocalDate fechaActivacion = tabAlarmasController.dateActivacion.getValue();
                LocalDate fechaBaja = tabAlarmasController.dateBajaAlarma.getValue();
                LocalDate fechaPermanencia = tabAlarmasController.datePermanenciaAlarma.getValue();
                Double comision = Double.valueOf(tabAlarmasController.txtComision.getText());
                String tipoContrato = tabAlarmasController.txtContratoAlarma.getText();

                int paramIndex = 1;
                stmt.setInt(paramIndex++, idCompania);
                stmt.setString(paramIndex++, numeroCuenta != null && !numeroCuenta.isEmpty() ? numeroCuenta : null);
                stmt.setObject(paramIndex++, liquidacion, Types.DOUBLE);  // Usamos setObject para permitir null
                stmt.setString(paramIndex++, precio != null && !precio.isEmpty() ? precio : null);       // Usamos setObject para permitir null
                stmt.setString(paramIndex++, observaciones != null && !observaciones.isEmpty() ? observaciones : null);
                stmt.setDate(paramIndex++, fechaActivacion != null ? Date.valueOf(fechaActivacion) : null);
                stmt.setDate(paramIndex++, fechaBaja != null ? Date.valueOf(fechaBaja) : null);
                stmt.setObject(paramIndex++, comision, Types.DOUBLE);     // Usamos setObject para permitir null
                stmt.setString(paramIndex++, tipoContrato != null && !tipoContrato.isEmpty() ? tipoContrato : null);
                stmt.setDate(paramIndex++, fechaPermanencia != null ? Date.valueOf(fechaPermanencia) : null);
                stmt.setString(paramIndex++, direccionAlarmaActual != null && !direccionAlarmaActual.isEmpty() ? direccionAlarmaActual : null);

                int filasActualizadas = stmt.executeUpdate();

                if (filasActualizadas == 0) {
                    verificar.crearAlerta("Error", "No se actualizó ningún registro. Verifique la dirección de la alarma y el ID del cliente.");
                    return false;
                }

                return true;

            } catch (NumberFormatException e) {
                verificar.crearAlerta("Error", "Formato incorrecto en campos numéricos: " + e.getMessage());
                return false;
            } catch (RuntimeException e) {
                System.out.println(e.getMessage());
                return false;
            }

        }
    }


    /*
        ---------- MÉTODOS PARA LOS BOTONES DE SECCIÓN ----------
     */
    @FXML
    protected void clientsButtonClicked() {
        clientesPane.setVisible(true);
        calendarioPane.setVisible(false);
        listado.setVisible(false);
        renovacionesPane.setVisible(false);
        adminPane.setVisible(false);
        empleadoPane.setVisible(false);

        seccionButtonClick(clientesButton);
    }

    @FXML
    protected void listButtonClicked() {
        clientesPane.setVisible(false);
        calendarioPane.setVisible(false);
        listado.setVisible(true);
        renovacionesPane.setVisible(false);
        adminPane.setVisible(false);
        empleadoPane.setVisible(false);

        seccionButtonClick(listButton);
    }

    @FXML
    protected void calendarButtonClicked() {
        clientesPane.setVisible(false);
        calendarioPane.setVisible(true);
        listado.setVisible(false);
        renovacionesPane.setVisible(false);
        adminPane.setVisible(false);
        empleadoPane.setVisible(false);

        seccionButtonClick(calendarButton);
    }

    @FXML
    protected void renovationsButtonClicked() {
        clientesPane.setVisible(false);
        calendarioPane.setVisible(false);
        listado.setVisible(false);
        renovacionesPane.setVisible(true);
        adminPane.setVisible(false);
        empleadoPane.setVisible(false);

        seccionButtonClick(renovacionesButton);
    }

    @FXML
    protected void adminButtonClicked() {
        clientesPane.setVisible(false);
        calendarioPane.setVisible(false);
        listado.setVisible(false);
        renovacionesPane.setVisible(false);
        adminPane.setVisible(true);
        empleadoPane.setVisible(false);

        seccionButtonClick(adminButton);
    }

    @FXML
    protected void empleadoButtonClicked() {
        clientesPane.setVisible(false);
        calendarioPane.setVisible(false);
        listado.setVisible(false);
        renovacionesPane.setVisible(false);
        adminPane.setVisible(false);
        empleadoPane.setVisible(true);

        seccionButtonClick(empleadoButton);
    }

    // Colorear de verde el fondo del botón al hacer CLick
    private void seccionButtonClick(Button botonClickado) {
        for (Button boton : botonesSeccion) {
            boton.getStyleClass().remove("clickado");
        }
        botonClickado.getStyleClass().add("clickado");
    }
}