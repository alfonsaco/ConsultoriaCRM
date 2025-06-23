package com.practicas.consultoriacrm.Secciones;

import com.practicas.consultoriacrm.Utils.Conexion;
import com.practicas.consultoriacrm.Utils.Encriptar;
import com.practicas.consultoriacrm.Utils.VerificarFormularios;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class SeccionAdmin {
    @FXML private TextField texFieldUsuario;
    @FXML private TextField textFieldContra;
    @FXML private Button btnNuevoUsuario;

    @FXML private ComboBox comboNuevoTecnico;
    @FXML private ComboBox comboPrivilegios;

    private VerificarFormularios verificar;

    public void initialize() {
        verificar=new VerificarFormularios();

        btnNuevoUsuario.setOnAction(actionEvent -> {
            insertarCliente();
        });

        comboPrivilegios.getItems().add(1);
        comboPrivilegios.getItems().add(2);
        comboPrivilegios.getItems().add(3);
    }

    private void insertarCliente() {
        String nombre=texFieldUsuario.getText();
        String contra=textFieldContra.getText();
        int privilegios=Integer.parseInt(String.valueOf(comboPrivilegios.getValue()));

        // Verificar que no haya campos vacíos
        if(nombre.isEmpty() || contra.isEmpty() || nombre == null || contra == null) {
            verificar.crearAlerta("Campos vacíos", "No puede haber ningún campo vacío");
            return;
        }

        // Verificar usuario repetido
        String consuta="SELECT COUNT(NOMBRE) FROM USUARIOS WHERE NOMBRE=?";
        int repetido= 0;
        try {
            repetido = Conexion.getInstance().verificarExistenciaDato(consuta, Encriptar.encrypt(nombre));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if(repetido > 0) {
            verificar.crearAlerta("Usuario repetido", "Ya existe un usuario con ese nombre");
            return;
        }

        String consultaInsertar="INSERT INTO USUARIOS (NOMBRE, CONTRASENA, PRIVILEGIOS) VALUES (?, ?, ?)";
        try {
            Conexion.getInstance().agregarNuevoUsuario(consultaInsertar, Encriptar.encrypt(nombre), Encriptar.encrypt(contra), privilegios);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        verificar.crearAlerta("Usuario añadido", "Usuario "+nombre+" añadido");
    }


}
