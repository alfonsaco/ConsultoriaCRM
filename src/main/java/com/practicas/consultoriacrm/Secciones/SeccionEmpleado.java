package com.practicas.consultoriacrm.Secciones;

import com.practicas.consultoriacrm.Utils.Conexion;
import com.practicas.consultoriacrm.Utils.Encriptar;
import com.practicas.consultoriacrm.Utils.VerificarFormularios;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.io.File;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

public class SeccionEmpleado {

    @FXML public TextField txtNombreEmpleado;
    @FXML public TextField txtDNIEmpleado;
    @FXML public DatePicker datePickerFechaAltaEmpleado;
    @FXML public TextField txtEmailEmpleado;
    @FXML public TextField txtNumCuentaEmpleado;
    @FXML public TextField txtTelefonoEmpleado;
    @FXML public ComboBox<String> comboEmpresaEmpleado;
    @FXML public ListView<String> listaEmpleados;

    public int idEmpleadoSeleccionado = 0;

    private boolean empleadoSeleccionado = false;

    private Conexion conexion;

    private Map<Integer, String> mapaEmpleados;

    private VerificarFormularios v = new VerificarFormularios();

    public void initialize(){
        conexion = Conexion.getInstance();
        comboEmpresaEmpleado.getItems().addAll("Empresa 1", "Empresa 2");
        listaEmpleados.setItems(obtenerEmpleados());

        listaEmpleados.setOnMouseClicked(event -> {
            try {
                empleadoBuscadorOnClick();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void insertarEmpleado() throws Exception {

        String sql = "INSERT INTO EMPLEADOS (NOMBRE, DNI, FECHA_ALTA, NUMERO_CUENTA, EMAIL, TELEFONO, EMPRESA) VALUES (?, ?, ?, ?, ?, ?, ?)";
        String nombre = txtNombreEmpleado.getText();
        String dni = Encriptar.encrypt(txtDNIEmpleado.getText());
        if(dni.isEmpty()){
            v.crearAlerta("Error", "Por favor introduce el dni del empleado");
            return;
        }

        if(!v.esDNI(txtDNIEmpleado.getText())){
            v.crearAlerta("Error", "Por favor introduce un dni válido");
            return;
        }

        LocalDate fechaAlta = datePickerFechaAltaEmpleado.getValue();
        Date fechaAltaSQL = Date.valueOf(fechaAlta);
        String numCuenta = Encriptar.encrypt(txtNumCuentaEmpleado.getText());
        String email = Encriptar.encrypt(txtEmailEmpleado.getText());
        String telefono = Encriptar.encrypt(txtTelefonoEmpleado.getText());
        String empresa = comboEmpresaEmpleado.getValue();
        try(PreparedStatement stmt = conexion.getConnection().prepareStatement(sql)){
            stmt.setString(1, nombre);
            stmt.setString(2, dni);
            stmt.setDate(3, fechaAltaSQL);
            stmt.setString(4, numCuenta);
            stmt.setString(5, email);
            stmt.setString(6, telefono);
            stmt.setString(7, empresa);

            stmt.executeQuery();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        actualizarListaEmpleados();
        txtNombreEmpleado.setDisable(true);
        txtTelefonoEmpleado.setDisable(true);
        txtEmailEmpleado.setDisable(true);
        txtDNIEmpleado.setDisable(true);
        txtNumCuentaEmpleado.setDisable(true);
        comboEmpresaEmpleado.setDisable(true);
        datePickerFechaAltaEmpleado.setDisable(true);
        empleadoSeleccionado = false;
        v.crearAlerta("Éxito", "Empleado insertado correctamente");
    }

    public void editarEmpleado(){
        if(!empleadoSeleccionado){
            v.crearAlerta("Denegado", "Por favor selecciona primero un cliente");
            return;
        }
        txtNombreEmpleado.setDisable(false);
        txtTelefonoEmpleado.setDisable(false);
        txtEmailEmpleado.setDisable(false);
        txtDNIEmpleado.setDisable(false);
        txtNumCuentaEmpleado.setDisable(false);
        comboEmpresaEmpleado.setDisable(false);
        datePickerFechaAltaEmpleado.setDisable(false);
    }

    public void guardarEmpleado() throws Exception {
        if(!empleadoSeleccionado){
            v.crearAlerta("Denegado", "Por favor selecciona primero un cliente");
            return;
        }

        if(!v.esDNI(txtDNIEmpleado.getText())){
            v.crearAlerta("Error", "Por favor introduce un dni válido");
            return;
        }

        String nif = Encriptar.encrypt(txtDNIEmpleado.getText());
        String consultaDNI = "SELECT COUNT(NIF) FROM CLIENTES WHERE NIF = ? AND ID_CLIENTE <> "+ idEmpleadoSeleccionado;
        if (conexion.verificarExistenciaDato(consultaDNI, nif) > 0) {
            v.crearAlerta("DNI repetido", "El DNI insertado ya existe en la base de datos");
            return;
        }

        String sql = "UPDATE EMPLEADOS SET " +
                "NOMBRE = ?, " +
                "DNI = ?, " +
                "FECHA_ALTA = ?, " +
                "EMAIL = ?, " +
                "NUMERO_CUENTA = ?, " +
                "TELEFONO = ?, " +
                "EMPRESA = ? " +
                "WHERE ID_EMPLEADO = ?";

        String nombre = txtNombreEmpleado.getText();

        Date fechaAlta = null;
        if(datePickerFechaAltaEmpleado.getValue()!=null) {
            fechaAlta = Date.valueOf(datePickerFechaAltaEmpleado.getValue());
        }
        String email = Encriptar.encrypt(txtEmailEmpleado.getText());
        String numCuenta = Encriptar.encrypt(txtNumCuentaEmpleado.getText());
        String telefono = Encriptar.encrypt(txtTelefonoEmpleado.getText());
        String empresa = comboEmpresaEmpleado.getValue();

        try(PreparedStatement stmt = conexion.getConnection().prepareStatement(sql)){
            stmt.setString(1, nombre);
            stmt.setString(2, nif);
            stmt.setDate(3, fechaAlta);
            stmt.setString(4, email);
            stmt.setString(5, numCuenta);
            stmt.setString(6, telefono);
            stmt.setString(7, empresa);
            stmt.setInt(8, idEmpleadoSeleccionado);

            int filas = stmt.executeUpdate();

            if(filas>0){
                v.crearAlerta("Éxito", "El empleado se modificó satisfactoriamente.");
            }else{
                v.crearAlerta("Error", "No se modifico el empleado.");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        txtNombreEmpleado.setDisable(true);
        txtTelefonoEmpleado.setDisable(true);
        txtEmailEmpleado.setDisable(true);
        txtDNIEmpleado.setDisable(true);
        txtNumCuentaEmpleado.setDisable(true);
        comboEmpresaEmpleado.setDisable(true);
        datePickerFechaAltaEmpleado.setDisable(true);
        empleadoSeleccionado = false;
        actualizarListaEmpleados();
    }

    public void vaciarCampos(){
        txtNombreEmpleado.setDisable(false);
        txtTelefonoEmpleado.setDisable(false);
        txtEmailEmpleado.setDisable(false);
        txtDNIEmpleado.setDisable(false);
        txtNumCuentaEmpleado.setDisable(false);
        comboEmpresaEmpleado.setDisable(false);
        datePickerFechaAltaEmpleado.setDisable(false);

        txtNombreEmpleado.setText("");
        txtTelefonoEmpleado.setText("");
        txtEmailEmpleado.setText("");
        txtDNIEmpleado.setText("");
        txtNumCuentaEmpleado.setText("");
        comboEmpresaEmpleado.setValue("");
        datePickerFechaAltaEmpleado.setValue(null);
    }

    public void eliminarEmpleado(){
        if(!empleadoSeleccionado){
            v.crearAlerta("Denegado", "Por favor selecciona primero un cliente");
            return;
        }
        String sql = "DELETE FROM EMPLEADOS WHERE ID_EMPLEADO = ?";
        try {
            int filas;
            try (PreparedStatement stm = conexion.getConnection().prepareStatement(sql)) {
                stm.setInt(1, idEmpleadoSeleccionado);
                filas = stm.executeUpdate();
            }
            if(filas>0){
                v.crearAlerta("Éxito", "Empleado eliminado correctamente");
            }else{
                v.crearAlerta("Error", "El empleado no se ha eliminado");
            }
        }catch (SQLException e){
            e.printStackTrace();
        }

        txtNombreEmpleado.setDisable(true);
        txtTelefonoEmpleado.setDisable(true);
        txtEmailEmpleado.setDisable(true);
        txtDNIEmpleado.setDisable(true);
        txtNumCuentaEmpleado.setDisable(true);
        comboEmpresaEmpleado.setDisable(true);
        datePickerFechaAltaEmpleado.setDisable(true);
        empleadoSeleccionado = false;
        actualizarListaEmpleados();
    }

    public void actualizarListaEmpleados(){
        listaEmpleados.setItems(obtenerEmpleados());
    }

    public ObservableList<String> obtenerEmpleados() {
        ObservableList<String> empleados = FXCollections.observableArrayList();
        Map<Integer, String> mapaEmpleadosOrdenado = new LinkedHashMap<>();
        String consulta = "SELECT ID_EMPLEADO, UPPER(NOMBRE) AS NOMBRE FROM EMPLEADOS ORDER BY NOMBRE ASC";

        ResultSet rs = conexion.listarDatos(consulta);
        try {
            if (rs != null) {
                while (rs.next()) {
                    int id = rs.getInt("ID_EMPLEADO");
                    String nombre = rs.getString("nombre");
                    mapaEmpleadosOrdenado.put(id, nombre);
                    empleados.add(nombre);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al procesar los resultados de la consulta: " + e.getMessage());
        }
        mapaEmpleados = mapaEmpleadosOrdenado;
        return empleados;
    }

    public void empleadoBuscadorOnClick() throws Exception {
        empleadoSeleccionado = true;

        int empleadoSeleccionado = listaEmpleados.getSelectionModel().getSelectedIndex();
        if (empleadoSeleccionado == -1) return;
        Integer idEmpleado = (Integer) mapaEmpleados.keySet().toArray()[empleadoSeleccionado];

        if (idEmpleado == null) {
            return;
        }

        txtNombreEmpleado.setDisable(true);
        txtTelefonoEmpleado.setDisable(true);
        txtEmailEmpleado.setDisable(true);
        txtDNIEmpleado.setDisable(true);
        txtNumCuentaEmpleado.setDisable(true);
        comboEmpresaEmpleado.setDisable(true);
        datePickerFechaAltaEmpleado.setDisable(true);

        idEmpleadoSeleccionado = idEmpleado;
        String consulta = "SELECT * FROM EMPLEADOS WHERE ID_EMPLEADO = ?";


        try (PreparedStatement preparedStatement = conexion.getConnection().prepareStatement(consulta)) {
            preparedStatement.setInt(1, idEmpleado);
            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {
                String nombre = rs.getString("NOMBRE") != null ? rs.getString("NOMBRE") : "";
                String dni = rs.getString("DNI") != null ? rs.getString("DNI") : "";
                Date fechaAlta = rs.getDate("FECHA_ALTA");
                String email = rs.getString("EMAIL") != null ? rs.getString("EMAIL") : "";
                String numCuenta = rs.getString("NUMERO_CUENTA") != null ? rs.getString("NUMERO_CUENTA") : "";
                String telefono = rs.getString("TELEFONO") != null ? rs.getString("TELEFONO") : "";
                String empresa = rs.getString("EMPRESA") != null ? rs.getString("EMPRESA") : "";

                txtNombreEmpleado.setText(nombre);
                txtDNIEmpleado.setText(Encriptar.decrypt(dni));
                datePickerFechaAltaEmpleado.setValue(fechaAlta.toLocalDate());
                txtEmailEmpleado.setText(Encriptar.decrypt(email));
                txtNumCuentaEmpleado.setText(Encriptar.decrypt(numCuenta));
                txtTelefonoEmpleado.setText(Encriptar.decrypt(telefono));
                comboEmpresaEmpleado.setValue(empresa);

            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }


}
