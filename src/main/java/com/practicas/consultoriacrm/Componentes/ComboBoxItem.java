package com.practicas.consultoriacrm.Componentes;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.ArrayList;
import java.util.Objects;

public class ComboBoxItem {
    private final String texto;
    private final ImageView icono;
    private ArrayList<ComboBoxItem> parentList;

    public ComboBoxItem(String texto) {
        this.texto = texto;
        this.icono = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("/images/combodelete.png")).toExternalForm()));
        this.icono.setFitHeight(17);
        this.icono.setFitWidth(17);
    }

    public String getTexto() {
        return texto;
    }

    public ImageView getIcono() {
        return icono;
    }

    public ArrayList<ComboBoxItem> getParentList() {
        return parentList;
    }

    public void setParentList(ArrayList<ComboBoxItem> parentList) {
        this.parentList = parentList;
    }

    @Override
    public String toString() {
        return texto;
    }
}
