package com.practicas.consultoriacrm.Secciones;

import com.practicas.consultoriacrm.Utils.Conexion;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SeccionRenovaciones {
    private final Conexion conexion = Conexion.getInstance();

    // *************** BOTONES RENOVACION *****************
    private List<Button> botonesRenovacion;
    @FXML public Button btnCups;
    @FXML public Button btnTelefonia;
    @FXML public Button btnAlarma;
    @FXML public Button btnRefrescarRenovaciones;
    @FXML public Button btnInternet;

    @FXML public TableView tablaRenovaciones;
    private boolean cups = false;
    private boolean telefonia = false;
    private boolean alarma = false;
    private boolean internet = false;

    @FXML private AnchorPane containerRenovaciones;

    public void initialize() {
        cargarDatosCups();

        btnCups.setOnAction(actionEvent -> cargarDatosCups());
        btnTelefonia.setOnAction(actionEvent -> cargarDatosTelefonia());
        btnAlarma.setOnAction(actionEvent -> cargarDatosAlarma());
        btnInternet.setOnAction(actionEvent -> cargarDatosInternet());
        btnRefrescarRenovaciones.setOnAction(actionEvent -> {
            if (cups) {
                cargarDatosCups();
            }
            if (telefonia) {
                cargarDatosTelefonia();
            }
            if (alarma) {
                cargarDatosAlarma();
            }
            if (internet) {
                cargarDatosInternet();
            }
        });

        // Botones de renovación
        botonesRenovacion=new ArrayList<>();
        botonesRenovacion.add(btnCups);
        botonesRenovacion.add(btnAlarma);
        botonesRenovacion.add(btnTelefonia);
        botonesRenovacion.add(btnInternet);
        botonesRenovacion.add(btnRefrescarRenovaciones);

        btnCups.getStyleClass().add("clickado");
        for(Button boton : botonesRenovacion) {
            boton.setOnAction(actionEvent -> renovacionesButtonClick(boton));
        }
    }

    public void cargarDatosCups() {
        cups = true;
        telefonia = false;
        alarma = false;
        internet = false;

        tablaRenovaciones.getColumns().clear();
        TableColumn<String[], String> cliente = new TableColumn<>("CLIENTE");
        cliente.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()[0]));
        TableColumn<String[], String> codigoCup = new TableColumn<>("CUP");
        codigoCup.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()[1]));
        TableColumn<String[], String> tipoCup = new TableColumn<>("TIPO");
        tipoCup.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()[2]));
        TableColumn<String[], String> fechaClawback = new TableColumn<>("CLAWBACK");
        fechaClawback.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()[5]));
        TableColumn<String[], String> comercializadora = new TableColumn<>("COMERCIALIZADORA");
        comercializadora.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()[6]));

        tablaRenovaciones.getColumns().addAll(cliente, codigoCup, tipoCup, fechaClawback, comercializadora);

        ObservableList<String[]> datos = conexion.obtenerCupsClawback();

        datos.sort((a, b) -> {
            LocalDate fechaA = LocalDate.parse(a[5]);
            LocalDate fechaB = LocalDate.parse(b[5]);

            long diferenciaA = Math.abs(LocalDate.now().toEpochDay() - fechaA.toEpochDay());
            long diferenciaB = Math.abs(LocalDate.now().toEpochDay() - fechaB.toEpochDay());

            if (fechaA.isBefore(LocalDate.now()) && fechaB.isBefore(LocalDate.now())) {
                return fechaA.compareTo(fechaB);
            } else if (fechaA.isBefore(LocalDate.now())) {
                return -1;
            } else if (fechaB.isBefore(LocalDate.now())) {
                return 1;
            } else {
                return Long.compare(diferenciaA, diferenciaB);
            }
        });

        tablaRenovaciones.setItems(datos);
        configurarEstiloFilas();
    }

    public void cargarDatosTelefonia() {
        cups = false;
        telefonia = true;
        alarma = false;
        internet = false;

        tablaRenovaciones.getColumns().clear();

        TableColumn<String[], String> cliente = new TableColumn<>("CLIENTE");
        cliente.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()[0]));
        TableColumn<String[], String> direccion = new TableColumn<>("DIRECCIÓN");
        direccion.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()[1]));
        TableColumn<String[], String> compania = new TableColumn<>("COMPAÑÍA");
        compania.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()[2]));
        TableColumn<String[], String> permanencia = new TableColumn<>("PERMANENCIA");
        permanencia.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()[3]));

        tablaRenovaciones.getColumns().addAll(cliente, direccion, compania, permanencia);

        ObservableList<String[]> datos = conexion.obtenerDatosTelefonia();

        datos.sort((a, b) -> {
            LocalDate fechaA = LocalDate.parse(a[3]);
            LocalDate fechaB = LocalDate.parse(b[3]);

            long diferenciaA = Math.abs(LocalDate.now().toEpochDay() - fechaA.toEpochDay());
            long diferenciaB = Math.abs(LocalDate.now().toEpochDay() - fechaB.toEpochDay());

            if (fechaA.isBefore(LocalDate.now()) && fechaB.isBefore(LocalDate.now())) {
                return fechaA.compareTo(fechaB);
            } else if (fechaA.isBefore(LocalDate.now())) {
                return -1;
            } else if (fechaB.isBefore(LocalDate.now())) {
                return 1;
            } else {
                return Long.compare(diferenciaA, diferenciaB);
            }
        });

        tablaRenovaciones.setItems(datos);
        configurarEstiloFilas();
    }

    public void cargarDatosAlarma() {
        cups = false;
        telefonia = false;
        alarma = true;
        internet = false;

        tablaRenovaciones.getColumns().clear();

        TableColumn<String[], String> cliente = new TableColumn<>("CLIENTE");
        cliente.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()[0]));
        TableColumn<String[], String> direccion = new TableColumn<>("DIRECCIÓN");
        direccion.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()[1]));
        TableColumn<String[], String> compania = new TableColumn<>("COMPAÑÍA");
        compania.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()[2]));
        TableColumn<String[], String> permanencia = new TableColumn<>("PERMANENCIA");
        permanencia.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()[3]));

        tablaRenovaciones.getColumns().addAll(cliente, direccion, compania, permanencia);

        ObservableList<String[]> datos = conexion.obtenerDatosAlarma();

        datos.sort((a, b) -> {
            LocalDate fechaA = LocalDate.parse(a[3]);
            LocalDate fechaB = LocalDate.parse(b[3]);

            long diferenciaA = Math.abs(LocalDate.now().toEpochDay() - fechaA.toEpochDay());
            long diferenciaB = Math.abs(LocalDate.now().toEpochDay() - fechaB.toEpochDay());

            if (fechaA.isBefore(LocalDate.now()) && fechaB.isBefore(LocalDate.now())) {
                return fechaA.compareTo(fechaB);
            } else if (fechaA.isBefore(LocalDate.now())) {
                return -1;
            } else if (fechaB.isBefore(LocalDate.now())) {
                return 1;
            } else {
                return Long.compare(diferenciaA, diferenciaB);
            }
        });

        tablaRenovaciones.setItems(datos);
        configurarEstiloFilas();
    }

    public void cargarDatosInternet() {
        cups = false;
        telefonia = false;
        alarma = false;
        internet = true;

        tablaRenovaciones.getColumns().clear();
        TableColumn<String[], String> cliente = new TableColumn<>("CLIENTE");
        cliente.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()[0]));
        TableColumn<String[], String> direccion = new TableColumn<>("DIRECCIÓN");
        direccion.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()[1]));
        TableColumn<String[], String> compania = new TableColumn<>("COMPAÑÍA");
        compania.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()[2]));
        TableColumn<String[], String> permanencia = new TableColumn<>("PERMANENCIA");
        permanencia.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()[3]));

        tablaRenovaciones.getColumns().addAll(cliente, direccion, compania, permanencia);

        ObservableList<String[]> datos = conexion.obtenerDatosInternet();

        datos.sort((a, b) -> {
            LocalDate fechaA = LocalDate.parse(a[3]);
            LocalDate fechaB = LocalDate.parse(b[3]);

            long diferenciaA = Math.abs(LocalDate.now().toEpochDay() - fechaA.toEpochDay());
            long diferenciaB = Math.abs(LocalDate.now().toEpochDay() - fechaB.toEpochDay());

            if (fechaA.isBefore(LocalDate.now()) && fechaB.isBefore(LocalDate.now())) {
                return fechaA.compareTo(fechaB);
            } else if (fechaA.isBefore(LocalDate.now())) {
                return -1;
            } else if (fechaB.isBefore(LocalDate.now())) {
                return 1;
            } else {
                return Long.compare(diferenciaA, diferenciaB);
            }
        });

        tablaRenovaciones.setItems(datos);
        configurarEstiloFilas();
    }

    public void configurarEstiloFilas() {
        tablaRenovaciones.setRowFactory(tv -> new TableRow<String[]>() {
            @Override
            protected void updateItem(String[] item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setStyle("");
                    return;
                }

                LocalDate fecha = null;
                if (cups)
                    fecha = LocalDate.parse(item[5]);
                if (internet)
                    fecha = LocalDate.parse(item[3]);
                if (telefonia)
                    fecha = LocalDate.parse(item[3]);
                if (alarma)
                    fecha = LocalDate.parse(item[3]);

                if (Objects.requireNonNull(fecha).isBefore(LocalDate.now())) {
                    setStyle("-fx-background-color: #80EF80;");
                } else if (fecha.isBefore(LocalDate.now().plusMonths(2))) {
                    setStyle("-fx-background-color: #ffebcc;");
                } else {
                    setStyle("-fx-background-color: #ffcccc;");
                }
            }
        });
    }


    private void renovacionesButtonClick(Button botonClickado) {
        for(Button boton : botonesRenovacion) {
            boton.getStyleClass().remove("clickado");
        }
        botonClickado.getStyleClass().add("clickado");
    }
}
