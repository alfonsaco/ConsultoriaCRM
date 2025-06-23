package com.practicas.consultoriacrm.Tabs;

import com.practicas.consultoriacrm.Componentes.ComboBoxItem;
import com.practicas.consultoriacrm.Componentes.ComboBoxItemCell;
import com.practicas.consultoriacrm.LoginController;
import com.practicas.consultoriacrm.MainScreenController;
import com.practicas.consultoriacrm.Secciones.SeccionClientes;
import com.practicas.consultoriacrm.Utils.Conexion;
import com.practicas.consultoriacrm.Utils.Encriptar;
import com.practicas.consultoriacrm.Utils.VerificarFormularios;

import javafx.event.Event;
import javafx.event.EventDispatchChain;
import javafx.event.EventDispatcher;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

// CLASE PARA LA PESTAÑA DE "DATOS"
public class TabDatos {

    // ---------- Clases -------------------------------------------------
    public final VerificarFormularios verificar = new VerificarFormularios();
    // -------- Componenetes ----------------------
    @FXML
    public TextField txtNombre;
    @FXML
    public TextField txtNombreComercial;
    @FXML
    public TextField txtCodPostal;
    @FXML
    public TextField txtNIF;
    @FXML
    public TextField txtDireccion;
    @FXML
    public TextField txtLocalidad;
    @FXML
    public TextField txtProvincia;
    @FXML
    public TextField clawbackLuz;
    @FXML
    public TextField clawbackGas;
    @FXML
    public TextField clawbackInternet;
    @FXML
    public TextField clawbackTelefonia;
    @FXML
    public TextField clawbackAlarmas;

    @FXML
    public TextArea txtObservaciones;

    @FXML
    public DatePicker fechaCreacion;
    @FXML
    public DatePicker fechaLuz;
    @FXML
    public DatePicker fechaGas;
    @FXML
    public DatePicker fechaInternet;
    @FXML
    public DatePicker fechaTelefonia;
    @FXML
    public DatePicker fechaAlarmas;

    @FXML
    public DatePicker fechaProxContacto;
    // --------------------------------------------


    // -------- Combos a rellenar automaticamente --------
    @FXML
    public ComboBox<String> comboLuz;
    @FXML
    public ComboBox<String> comboGas;
    @FXML
    public ComboBox<String> comboInternet;
    @FXML
    public ComboBox<String> comboCompTel;
    @FXML
    public ComboBox<String> comboAlarma;
    @FXML
    public ComboBox<String> comboTecnico;
    @FXML
    public ComboBox<String> cupsLuz;
    @FXML
    public ComboBox<String> cupsGas;
    @FXML
    public ComboBox<String> direccionesInternet;
    @FXML
    public ComboBox<String> direccionesTelefonia;
    @FXML
    public ComboBox<String> direccionesAlarmas;
    // ---------------------------------------------------


    // ---------- Emails y teléfonos -----------------
    public ArrayList<ComboBoxItem> emails;
    public ArrayList<ComboBoxItem> telefonos;
    @FXML
    public ComboBox comboTelefono;
    @FXML
    public ComboBox comboEmail;
    // -----------------------------------------------

    @FXML
    public CheckBox checkPermanenciaLuz;
    @FXML
    public CheckBox checkPermanenciaGas;
    @FXML
    public CheckBox checkPermanenciaInternet;
    @FXML
    public CheckBox checkPermanenciaTel;
    @FXML
    public CheckBox checkPermanenciaAlarm;
    public Conexion conexion;

    private int idUsuario = LoginController.idUsuario;

    // -------------- Arrays para la función de habilitar/deshabilitar -----------------
    public ArrayList<TextField> arrayDeTextFieldsAVaciar;
    // -------------------------------------------------------------------
    public ArrayList<ComboBox> arrayDeComboLimpiar;
    public ArrayList<ComboBox> arrayDeComboVaciar;
    public ArrayList<DatePicker> arrayDeFechasAVaciar;
    public ArrayList<TextArea> arrayDeObservacionesAVaciar;
    private SeccionClientes seccionClientes;
    // ----------------------------------------------------------------------------------

    public void initialize() {
        checkPermanenciaInternet.setDisable(true);
        checkPermanenciaGas.setDisable(true);
        checkPermanenciaTel.setDisable(true);
        checkPermanenciaLuz.setDisable(true);
        checkPermanenciaAlarm.setDisable(true);
        // ########################## INSTANCIA DE LA CONEXIÓN ##########################
        conexion = Conexion.getInstance();
        // ##############################################################################
        seccionClientes = SeccionClientes.getInstance();

        // ######################## MÉTODOS PARA VERIFICAR EL FORMATO CORRECTO ########################
        // Evitar letras en el código postal
        txtCodPostal.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                txtCodPostal.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
        // Permitir solo números y espacios en el teléfono
        comboTelefono.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[\\d ]*")) {
                comboTelefono.getEditor().setText(newValue.replaceAll("[^\\d ]", ""));
            }
        });
        // #########################################################################################


        // ######################################## COMBOBOXES ##################################
        ArrayList<ComboBox> combosArray = new ArrayList<>();
        combosArray.add(comboTelefono);
        combosArray.add(comboEmail);
        combosArray.add(comboLuz);
        combosArray.add(comboGas);
        combosArray.add(comboInternet);
        combosArray.add(comboCompTel);
        combosArray.add(comboAlarma);
        combosArray.add(comboTecnico);

        emails = new ArrayList<>();
        telefonos = new ArrayList<>();
        comboTelefono.setCellFactory(listView -> new ComboBoxItemCell(comboTelefono));
        comboEmail.setCellFactory(listView -> new ComboBoxItemCell(comboEmail));
        agregarElementoCombo(comboEmail, emails);
        agregarElementoCombo(comboTelefono, telefonos);

        for (ComboBox combo : combosArray) {
            combo.setEditable(true);
            combo.getEditor().requestFocus();
        }

        deshabilitarControles(fechaLuz, fechaGas, fechaInternet, fechaTelefonia, fechaAlarmas, comboLuz, comboGas, comboInternet, comboCompTel, comboAlarma, cupsLuz, cupsGas, direccionesInternet, direccionesTelefonia, direccionesAlarmas, clawbackLuz, clawbackGas, clawbackInternet, clawbackTelefonia, clawbackAlarmas);

        cargarDatosEnCombo(comboTecnico, "SELECT NOMBRE_COLABORADOR FROM COLABORADORES", "NOMBRE_COLABORADOR");
        // #########################################################################################
    }


    // //////////////////////////////////////////////////////////////////////////////////
    @FXML
    public void obtenerDatosParaGuardarCliente() throws Exception {
        String nombre = txtNombre.getText();
        String nombreComercial = txtNombreComercial.getText();
        String codPostal = txtCodPostal.getText();
        String direccion = txtDireccion.getText();
        String localidad = txtLocalidad.getText();
        String provincia = txtProvincia.getText();
        String observaciones = txtObservaciones.getText();
        String NIF = txtNIF.getText();
        int codPostalNumerico = 0;

        // Combos
        String tecnico = comboTecnico.getSelectionModel().getSelectedItem();

        // Fechas
        Date fechaCreacionSQL;
        Date proxContacto;

        if (fechaCreacion.getValue() != null && !fechaCreacion.getValue().equals("")) {
            fechaCreacionSQL = Date.valueOf(fechaCreacion.getValue());
        } else {
            fechaCreacionSQL = new Date(System.currentTimeMillis());
        }

        if (fechaProxContacto.getValue() != null && !fechaProxContacto.getValue().equals("")) {
            proxContacto = Date.valueOf(fechaProxContacto.getValue());
        } else {
            proxContacto = null;
        }

        // Lista de emails y teléfonos
        ArrayList<ComboBoxItem> listaEmails = new ArrayList(comboEmail.getItems());
        ArrayList<ComboBoxItem> listaTelefonos = new ArrayList(comboTelefono.getItems());

        // Verificar que los campos de nombre y DNI no estén vacíos
        if (nombre.trim().isEmpty() || NIF.trim().isEmpty()) {
            verificar.crearAlerta("Campos vacíos", "Los campos de \"Nombre\" y \"DNI/CIF\" no pueden estar vacíos");
            return;
        }

        // Verificaciones de datos
        if (!verificar.esCIF(NIF) && !verificar.esDNI(NIF)) {
            verificar.crearAlerta("DNI/CIF Inválido", "El DNI/CIF insertado no es válido");
            return;
        }

        // Verificar que el DNI no esté repetido
        String consultaDNI = "SELECT COUNT(NIF) FROM CLIENTES WHERE NIF = ?";
        if (conexion.verificarExistenciaDato(consultaDNI, Encriptar.encrypt(NIF)) > 0) {
            verificar.crearAlerta("DNI/CIF repetido", "El DNI/CIF insertado ya existe en la base de datos");
            return;
        }

        // Verificar que el usuario guarde los cambios de Email y teléfono
        if (comboTelefono.getValue() != " " && comboTelefono.getValue() != "" && comboTelefono.getValue()!=null) {
            verificar.crearAlerta("CAMPOS SIN GUARDAR", "Pulsa \"ENTER\" en el combo de teléfonos para guardar el teléfono");
            return;
        }
        if (comboEmail.getValue() != "" && comboEmail.getValue() != " " && comboEmail.getValue()!=null) {
            verificar.crearAlerta("CAMPOS SIN GUARDAR", "Pulsa \"ENTER\" en el combo de Emails para guardar el email");
            return;
        }

        // Intentamos obtener el código postal. Si no hay, sale 0
        if (!codPostal.trim().isEmpty()) {
            try {
                codPostalNumerico = Integer.parseInt(codPostal);

            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        insertarNuevoCliente(nombre, nombreComercial, NIF, codPostalNumerico, direccion, localidad, provincia, observaciones, listaEmails, listaTelefonos, fechaCreacionSQL, proxContacto, tecnico);

        // LIMPIAMOS CAMPOS
        vaciarCamposFormulario(arrayDeTextFieldsAVaciar, arrayDeComboLimpiar, arrayDeComboVaciar, arrayDeFechasAVaciar, arrayDeObservacionesAVaciar);
        verificar.crearAlerta("Cliente insertado", "Cliente \"" + nombre.toUpperCase() + "\" ha sido insertado");
    }
    // //////////////////////////////////////////////////////////////////////////////////

    // Método para insertar el cliente en la baes de datos. Se le llama desde el métood guardarCliente()
    private void insertarNuevoCliente(String nombre, String nombreComercial, String nif, int codPostal, String direccion, String localidad, String provincia, String observaciones, ArrayList<ComboBoxItem> listaEmails, ArrayList<ComboBoxItem> listaTelefonos, Date fechaCreacion, Date proxContacto, String tecnico) throws Exception {
        // INSERTAMOS EL CLIENTE
        String consulta = "INSERT INTO CLIENTES " +
                "(NOMBRE, NOMBRE_COMERCIAL, NIF, DIRECCION, COD_POSTAL, LOCALIDAD, PROVINCIA, OBSERVACIONES, FECHA_CREACION, TECNICO, PROX_CONTACTO, ID_USUARIO) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = conexion.getConnection().prepareStatement(consulta)) {
            preparedStatement.setString(1, nombre);
            preparedStatement.setString(2, nombreComercial);
            preparedStatement.setString(3, Encriptar.encrypt(nif));
            preparedStatement.setString(4, Encriptar.encrypt(direccion));
            preparedStatement.setInt(5, codPostal);
            preparedStatement.setString(6, localidad);
            preparedStatement.setString(7, provincia);
            preparedStatement.setString(8, Encriptar.encrypt(observaciones));
            preparedStatement.setDate(9, fechaCreacion);
            preparedStatement.setString(10, tecnico);
            preparedStatement.setDate(11, proxContacto);
            preparedStatement.setInt(12, idUsuario);

            preparedStatement.executeUpdate();
            // Una vez añadido todo, se actualiza
            seccionClientes.actualizarListaClientes();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // OBTENEMOS LA ID DEL CLIENTE SELECCIONADO
        String consultaObtenerId;
        if(MainScreenController.privilegiosUsuario<=2) {
            consultaObtenerId = "SELECT ID_CLIENTE FROM CLIENTES WHERE NIF=?";
        }else{
            consultaObtenerId = "SELECT ID_CLIENTE FROM CLIENTES WHERE NIF = ? AND ID_USUARIO = "+idUsuario;
        }
        int ID = conexion.obtenerDatoIntBDD(consultaObtenerId, Encriptar.encrypt(nif));

        // AÑADIMOS LOS EMAILS DEL CLIENTE
        insertarEmails(listaEmails, ID);

        // AÑADIMOS LOS TELÉFONOS DEL CLIENTE
        insertarTelefonos(listaTelefonos, ID);

    }

    public void insertarEmails(ArrayList<ComboBoxItem> listaEmails, int ID) {
        String consultaExiste = "SELECT COUNT(*) FROM EMAILS WHERE ID_CLIENTE = ? AND EMAIL = ?";
        String consultaInsertar = "INSERT INTO EMAILS (ID_CLIENTE, EMAIL) VALUES (?, ?)";

        for (ComboBoxItem email : listaEmails) {
            try {
                String emailEncriptado = Encriptar.encrypt(email.getTexto());

                try (PreparedStatement checkStmt = conexion.getConnection().prepareStatement(consultaExiste)) {
                    checkStmt.setInt(1, ID);
                    checkStmt.setString(2, emailEncriptado);
                    ResultSet rs = checkStmt.executeQuery();
                    rs.next();
                    int count = rs.getInt(1);

                    if (count == 0) {
                        try (PreparedStatement insertStmt = conexion.getConnection().prepareStatement(consultaInsertar)) {
                            insertStmt.setInt(1, ID);
                            insertStmt.setString(2, emailEncriptado);
                            insertStmt.executeUpdate();
                        }
                    }
                }

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }


    public void insertarTelefonos(ArrayList<ComboBoxItem> listaTelefonos, int ID) {
        String consultaExiste = "SELECT COUNT(*) FROM TELEFONOS WHERE ID_CLIENTE = ? AND NUMERO_TELEFONO = ?";
        String consultaInsertar = "INSERT INTO TELEFONOS (ID_CLIENTE, NUMERO_TELEFONO) VALUES (?, ?)";

        for (ComboBoxItem telefono : listaTelefonos) {
            try {
                String telefonoLimpio = telefono.getTexto().replaceAll("\\s+", "");
                String telefonoEncriptado = Encriptar.encrypt(telefonoLimpio);

                try (PreparedStatement checkStmt = conexion.getConnection().prepareStatement(consultaExiste)) {
                    checkStmt.setInt(1, ID);
                    checkStmt.setString(2, telefonoEncriptado);
                    ResultSet rs = checkStmt.executeQuery();
                    rs.next();
                    int count = rs.getInt(1);

                    if (count == 0) {
                        try (PreparedStatement insertStmt = conexion.getConnection().prepareStatement(consultaInsertar)) {
                            insertStmt.setInt(1, ID);
                            insertStmt.setString(2, telefonoEncriptado);
                            insertStmt.executeUpdate();
                        }
                    }
                }

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }


    // Método para eliminar el cliente de la base de datos
    public void eliminarCliente() {
        String nombre = txtNombre.getText();
        String nif = txtNIF.getText();

        if (!nombre.isEmpty()) {
            Alert alertaEliminar = new Alert(Alert.AlertType.CONFIRMATION);
            alertaEliminar.setTitle("Eliminar cliente");
            alertaEliminar.setContentText("Está seguro de que quiere eliminar al cliente " + nombre);

            ButtonType btnSi = new ButtonType("Sí");
            ButtonType btnNo = new ButtonType("No");
            alertaEliminar.getButtonTypes().setAll(btnSi, btnNo);

            alertaEliminar.showAndWait().ifPresent(r -> {
                if (r == btnSi) {
                    // Consultas para eliminar en cascada
                    try {
                        // ELIMINAR CLIENTE
                        String consulta = "DELETE FROM CLIENTES WHERE NIF = '" + Encriptar.encrypt(nif) + "'";
                        conexion.eliminarDatos(consulta);

                        verificar.crearAlerta("Cliente eliminado", "El cliente " + nombre + " ha sido eliminado correctamente");

                        seccionClientes.actualizarListaClientes();
                        this.vaciarCamposFormulario(arrayDeTextFieldsAVaciar, arrayDeComboVaciar, arrayDeComboLimpiar, arrayDeFechasAVaciar, arrayDeObservacionesAVaciar);

                    } catch (Exception e) {
                        e.printStackTrace();
                        verificar.crearAlerta("Error", "Hubo un error al eliminar el cliente: " + e.getMessage());
                    }
                }
            });
        }
    }

    // Meter datos de un Array a un ComboBox
    public void cargarDatosEnCombo(ComboBox combo, String consulta, String columna) {
        ResultSet rs = conexion.listarDatos(consulta);
        try {
            while (rs.next()) {
                String campo = rs.getString(columna);
                combo.getItems().add(campo);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Método para verificar que los elementos sean emails correctos
    private void agregarElementoCombo(ComboBox combo, ArrayList<ComboBoxItem> listaElementos) {
        combo.getEditor().setEventDispatcher(new EventDispatcher() {
            private final EventDispatcher originalDispatcher = combo.getEditor().getEventDispatcher();

            @Override
            public Event dispatchEvent(Event event, EventDispatchChain tail) {
                if (event instanceof KeyEvent keyEvent) {
                    if (keyEvent.getCode() == KeyCode.ENTER) {
                        String nuevoElemento = combo.getEditor().getText().trim();

                        if (!nuevoElemento.isEmpty()) {
                            // Verificar si el elemento ya existe en el ComboBox
                            boolean existe = combo.getItems().stream()
                                    .anyMatch(item -> item.toString().equals(nuevoElemento));

                            if (!existe) {
                                if (combo.equals(comboEmail)) {
                                    if (!verificar.esEmail(nuevoElemento)) {
                                        verificar.crearAlerta("Email Incorrecto", "El email introducido es inválido.");
                                        return null;
                                    }
                                }
                                // Crear un nuevo ComboBoxItem
                                ComboBoxItem newItem = new ComboBoxItem(nuevoElemento);
                                newItem.setParentList(listaElementos);

                                listaElementos.add(newItem);
                                combo.getItems().add(newItem);
                                combo.getSelectionModel().select(newItem);

                                combo.getEditor().clear();
                                combo.requestFocus();
                            }
                        }

                        // Consumir el evento para evitar que otros componentes lo capturen
                        event.consume();
                        return null;
                    }
                }

                // Dejar que el dispatcher original maneje otros eventos
                return originalDispatcher.dispatchEvent(event, tail);
            }
        });
    }

    public void deshabilitarControles(Control... controles) {
        for (Control control : controles) {
            control.getStyleClass().add("deshabilitar-edicion");

            switch (control) {
                case DatePicker datePicker -> {
                    datePicker.setEditable(false);
                    datePicker.setDisable(true);
                }
                case ComboBox<?> combo -> {
                    combo.setEditable(false);
                    Node arrow = combo.lookup(".arrow");
                    if (arrow != null) {
                        arrow.setStyle("-fx-background-color: #4d4d4d;");
                    }
                }
                case TextField textField -> textField.setEditable(false);
                default -> {
                }
            }
        }
    }


    // //////////////////////////////// MÉTODOS GENERALES ////////////////////////////////////
    // Método para agregar nuevo cliente en MainScreenController
    public void vaciarCamposFormulario(ArrayList<TextField> arrayTextFields, ArrayList<ComboBox> arrayCombosLimpiar, ArrayList<ComboBox> arrayCombosVaciar, ArrayList<DatePicker> arrayDates, ArrayList<TextArea> arrayObservacionesVaciar) {
        habilitarEdicion(arrayTextFields, arrayCombosLimpiar, arrayCombosVaciar, arrayDates, arrayObservacionesVaciar);

        deshabilitarControles(fechaLuz, fechaGas, fechaInternet, fechaTelefonia, fechaAlarmas, comboLuz, comboGas, comboInternet, comboCompTel, comboAlarma, cupsLuz, cupsGas, direccionesInternet, direccionesTelefonia, direccionesAlarmas, clawbackLuz, clawbackGas, clawbackInternet, clawbackTelefonia, clawbackAlarmas);
        // Vaciar textFields
        for (TextField field : arrayTextFields) {
            field.setText("");
        }

        // Vaciar DatePickers
        for (DatePicker date : arrayDates) {
            date.setValue(null);
        }

        // Vaciar combos
        for (ComboBox combo : arrayCombosLimpiar) {
            combo.getSelectionModel().clearSelection();
            combo.setValue(null);
        }

        // Vaciar combos
        for (ComboBox combo : arrayCombosVaciar) {
            combo.getSelectionModel().clearSelection();
            combo.setValue(null);
        }

        for (TextArea txtObservaciones : arrayObservacionesVaciar) {
            txtObservaciones.setText("");
        }

    }

    // Método para habilitar la edición de los campos
    public void habilitarEdicion(ArrayList<TextField> arrayTextFields, ArrayList<ComboBox> arrayCombosLimpiar, ArrayList<ComboBox> arrayCombosVaciar, ArrayList<DatePicker> arrayDates, ArrayList<TextArea> arrayObservacionesVaciar) {
        // Cambiar estilos TextFields
        for (TextField field : arrayTextFields) {
            field.setEditable(true);
            field.getStyleClass().remove("deshabilitar-edicion");
        }

        // Combos
        for (ComboBox combo : arrayCombosLimpiar) {
            combo.setEditable(true);
            combo.getStyleClass().remove("deshabilitar-edicion");
            combo.lookup(".arrow").setStyle("-fx-background-color: #1b3557;");
            combo.setDisable(false);
        }
        for (ComboBox combo : arrayCombosVaciar) {
            combo.setEditable(true);
            combo.getStyleClass().remove("deshabilitar-edicion");
            combo.lookup(".arrow").setStyle("-fx-background-color: #1b3557;");
            combo.setDisable(false);
        }

        // Dates
        for (DatePicker date : arrayDates) {
            date.setEditable(true);
            date.getStyleClass().remove("deshabilitar-edicion");
            date.setDisable(false);
        }

        for (TextArea textArea : arrayObservacionesVaciar) {
            textArea.getStyleClass().remove("deshabilitar-edicion");

        }
    }

    // Método para deshabilitar la edición de los campos
    public void deshabilitarEdicion(ArrayList<TextField> arrayTextFields, ArrayList<ComboBox> arrayCombosLimpiar, ArrayList<ComboBox> arrayCombosVaciar, ArrayList<DatePicker> arrayDates, ArrayList<TextArea> arrayObservaciones) {
        for (TextField field : arrayTextFields) {
            field.setEditable(false);
            field.getStyleClass().add("deshabilitar-edicion");
        }

        for (ComboBox combo : arrayCombosLimpiar) {
            combo.setEditable(false);
            combo.getStyleClass().add("deshabilitar-edicion");
            combo.lookup(".arrow").setStyle("-fx-background-color: #4d4d4d;");
            combo.setDisable(true);
        }
        for (ComboBox combo : arrayCombosVaciar) {
            combo.setEditable(false);
            combo.getStyleClass().add("deshabilitar-edicion");
            combo.lookup(".arrow").setStyle("-fx-background-color: #4d4d4d;");
            combo.setDisable(true);
        }

        for (DatePicker date : arrayDates) {
            date.setEditable(false);
            date.getStyleClass().add("deshabilitar-edicion");
            date.setDisable(true);
        }

        for (TextArea textArea : arrayObservaciones) {
            textArea.getStyleClass().add("deshabilitar-edicion");
        }
    }
    // ////////////////////////////////////////////////////////////////////////////////////////////////
}
