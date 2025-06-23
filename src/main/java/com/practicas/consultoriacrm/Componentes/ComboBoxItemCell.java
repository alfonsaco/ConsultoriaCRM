package com.practicas.consultoriacrm.Componentes;

import com.practicas.consultoriacrm.Utils.Conexion;
import com.practicas.consultoriacrm.Utils.Encriptar;
import com.practicas.consultoriacrm.Utils.VerificarFormularios;
import javafx.scene.Cursor;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;

public class ComboBoxItemCell extends ListCell<ComboBoxItem> {

    private final ComboBox<ComboBoxItem> comboBox;
    private final VerificarFormularios verificar;
    private final Conexion conexion;

    // Constructor para aceptar el ComboBox
    public ComboBoxItemCell(ComboBox<ComboBoxItem> comboBox) {
        this.comboBox = comboBox;

        verificar = new VerificarFormularios();
        conexion = Conexion.getInstance();
    }

    @Override
    protected void updateItem(ComboBoxItem item, boolean vacio) {
        super.updateItem(item, vacio);

        if (vacio || item == null) {
            setText(null);
            setGraphic(null);
        } else {
            // Crear un contenedor HBox
            HBox hbox = new HBox();
            hbox.setSpacing(10);

            Text text = new Text(item.getTexto());
            // Crear el icono de la papelera
            ImageView iconoPapelera = item.getIcono();

            iconoPapelera.setCursor(Cursor.HAND);

            // Agregar el evento de clic sobre ell icono de Eliminar
            iconoPapelera.setOnMousePressed(event -> {
                if (comboBox != null) {
                    String texto = item.getTexto();

                    verificar.crearAlerta("Eliminar elemento", "¿Está seguro de que quiere borrar " + texto + "?");
                    comboBox.getItems().remove(item);
                    String consultaBorrar;

                    if (texto.contains("@")) {
                        try {
                            consultaBorrar = "DELETE FROM EMAILS WHERE EMAIL LIKE '" + Encriptar.encrypt(texto) + "'";
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        try {
                            consultaBorrar = "DELETE FROM TELEFONOS WHERE NUMERO_TELEFONO LIKE '" + Encriptar.encrypt(texto) + "'";
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }

                    conexion.eliminarDatos(consultaBorrar);

                    if (item.getParentList() != null) {
                        item.getParentList().remove(item);
                    }
                }
            });

            // Asegurarse de que el texto y el icono estén alineados correctamente
            HBox.setHgrow(text, Priority.ALWAYS);

            // Establecer el contenido de la celda
            hbox.getChildren().addAll(iconoPapelera, text);
            setGraphic(hbox);
        }
    }
}
