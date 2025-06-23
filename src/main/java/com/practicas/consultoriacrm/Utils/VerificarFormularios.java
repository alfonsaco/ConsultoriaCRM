package com.practicas.consultoriacrm.Utils;

import javafx.scene.control.Alert;

// CLASE PARA PODER AGREGAR TODOS LOS MÉTODOS DE VERIFICACIÓN DE LOS FORMULARIOS
public class VerificarFormularios {
    public boolean esDNI(String DNI) {
        return DNI.matches("[0-9]{8}[a-zA-Z]");
    }

    public boolean esCIF(String CIF) {
        return CIF.matches("[a-zA-Z][0-9]{8}");
    }

    public boolean esEmail(String email) {
        return email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
    }

    // Método para crear
    public void crearAlerta(String titulo, String contenido) {
        Alert alerta = new Alert(Alert.AlertType.WARNING);
        alerta.setTitle(titulo);
        alerta.setContentText(contenido);
        alerta.showAndWait();
    }
}
