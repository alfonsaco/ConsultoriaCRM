package com.practicas.consultoriacrm.Utils;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.MapValueFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DialogController {
    private String tipo;

    private int idCliente;
    private Conexion conexion;

    @FXML
    private TableView<Map> tabla;
    @FXML
    private TableColumn<Map, String> colCompania;
    @FXML
    private TableColumn<Map, String> colFechaContrato;
    @FXML
    private TableColumn<Map, String> colFechaPermanencia;
    @FXML
    private TableColumn<Map, String> colCup;

    @FXML
    private void initialize() {
        // Configurar las columnas para usar las claves del Map
        colCompania.setCellValueFactory(new MapValueFactory<>("compania"));
        colFechaContrato.setCellValueFactory(new MapValueFactory<>("fechaContrato"));
        colFechaPermanencia.setCellValueFactory(new MapValueFactory<>("fechaPermanencia"));
        colCup.setCellValueFactory(new MapValueFactory<>("cup"));

        conexion = Conexion.getInstance();
    }

    // Setters
    public void setScene(Scene scene) {
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;

        cargarDatosEnTabla();
    }

    private void cargarDatosEnTabla() {
        String consultaCompanias = "SELECT X.NOMBRE_COMPANIA FROM CLIENTES C " +
                "JOIN CUPS CC ON CC.ID_CLIENTE=C.ID_CLIENTE " +
                "JOIN COMPANIAS X ON X.ID_COMPANIA=CC.ID_COMPANIA " +
                "WHERE C.ID_CLIENTE=" + idCliente + " AND X.TIPO='" + tipo + "'";
        String consultaContrato = "SELECT CC.FECHA_ACTIVACION FROM CLIENTES C " +
                "JOIN CUPS CC ON CC.ID_CLIENTE=C.ID_CLIENTE " +
                "JOIN COMPANIAS X ON X.ID_COMPANIA=CC.ID_COMPANIA " +
                "WHERE C.ID_CLIENTE=" + idCliente + " AND X.TIPO='" + tipo + "'";
        String consultaBaja = "SELECT CC.FECHA_BAJA FROM CLIENTES C " +
                "JOIN CUPS CC ON CC.ID_CLIENTE=C.ID_CLIENTE " +
                "JOIN COMPANIAS X ON X.ID_COMPANIA=CC.ID_COMPANIA " +
                "WHERE C.ID_CLIENTE=" + idCliente + " AND X.TIPO='" + tipo + "'";
        String consultaDireccion = "SELECT CC.CODIGO_CUP FROM CLIENTES C " +
                "JOIN CUPS CC ON CC.ID_CLIENTE=C.ID_CLIENTE " +
                "JOIN COMPANIAS X ON X.ID_COMPANIA=CC.ID_COMPANIA " +
                "WHERE C.ID_CLIENTE=" + idCliente + " AND X.TIPO='" + tipo + "'";

        switch (tipo) {
            case "ALARMAS" -> {
                consultaCompanias = "SELECT X.NOMBRE_COMPANIA FROM CLIENTES C " +
                        "JOIN DIRECCION_INSTALACION_ALARMAS CC ON CC.ID_CLIENTE=C.ID_CLIENTE " +
                        "JOIN COMPANIAS X ON X.ID_COMPANIA=CC.ID_COMPANIA " +
                        "WHERE C.ID_CLIENTE=" + idCliente + " AND X.TIPO='ALARMAS'";
                consultaContrato = "SELECT CC.FECHA_ACTIVACION FROM CLIENTES C " +
                        "JOIN DIRECCION_INSTALACION_ALARMAS CC ON CC.ID_CLIENTE=C.ID_CLIENTE " +
                        "JOIN COMPANIAS X ON X.ID_COMPANIA=CC.ID_COMPANIA " +
                        "WHERE C.ID_CLIENTE=" + idCliente + " AND X.TIPO='ALARMAS'";
                consultaBaja = "SELECT CC.FECHA_BAJA FROM CLIENTES C " +
                        "JOIN DIRECCION_INSTALACION_ALARMAS CC ON CC.ID_CLIENTE=C.ID_CLIENTE " +
                        "JOIN COMPANIAS X ON X.ID_COMPANIA=CC.ID_COMPANIA " +
                        "WHERE C.ID_CLIENTE=" + idCliente + " AND X.TIPO='ALARMAS'";
                consultaDireccion = "SELECT F.DIRECCION_ALARMA FROM CLIENTES C " +
                        "JOIN DIRECCION_INSTALACION_ALARMAS F ON F.ID_CLIENTE=C.ID_CLIENTE " +
                        "JOIN COMPANIAS X ON X.ID_COMPANIA=F.ID_COMPANIA " +
                        "WHERE C.ID_CLIENTE=" + idCliente + " AND X.TIPO='ALARMAS'";

                colCup.setText("DIRECCIÓN");
            }
            case "TELEFONIA" -> {
                // Prober primero con compañís de telefonia
                consultaCompanias = "SELECT X.NOMBRE_COMPANIA FROM CLIENTES C " +
                        "JOIN DIRECCION_INSTALACION_TELEFONIA CC ON CC.ID_CLIENTE=C.ID_CLIENTE " +
                        "JOIN COMPANIAS X ON X.ID_COMPANIA=CC.ID_COMPANIA_TELEFONIA " +
                        "WHERE C.ID_CLIENTE=" + idCliente + " AND X.TIPO='TELEFONIA'";
                consultaContrato = "SELECT CC.FECHA_ACTIVACION_TELEFONIA FROM CLIENTES C " +
                        "JOIN DIRECCION_INSTALACION_TELEFONIA CC ON CC.ID_CLIENTE=C.ID_CLIENTE " +
                        "JOIN COMPANIAS X ON X.ID_COMPANIA=CC.ID_COMPANIA_TELEFONIA " +
                        "WHERE C.ID_CLIENTE=" + idCliente + " AND X.TIPO='TELEFONIA'";
                consultaBaja = "SELECT CC.FECHA_PERMANENCIA_TELEFONIA FROM CLIENTES C " +
                        "JOIN DIRECCION_INSTALACION_TELEFONIA CC ON CC.ID_CLIENTE=C.ID_CLIENTE " +
                        "JOIN COMPANIAS X ON X.ID_COMPANIA=CC.ID_COMPANIA_TELEFONIA " +
                        "WHERE C.ID_CLIENTE=" + idCliente + " AND X.TIPO='TELEFONIA'";
                consultaDireccion = "SELECT F.DIRECCION_FIBRA FROM CLIENTES C " +
                        "JOIN DIRECCION_INSTALACION_TELEFONIA F ON F.ID_CLIENTE=C.ID_CLIENTE " +
                        "JOIN COMPANIAS X ON X.ID_COMPANIA=F.ID_COMPANIA_TELEFONIA " +
                        "WHERE C.ID_CLIENTE=" + idCliente + " AND X.TIPO='TELEFONIA'";

                // Los telefonos no tienen fecha baja, asi que cambiamos el texto de la columna
                colFechaPermanencia.setText("FECHA PERMANENCIA");
                colCup.setText("DIRECCIÓN");
            }
            case "INTERNET" -> {
                // Prober primero con compañís de telefonia
                consultaCompanias = "SELECT X.NOMBRE_COMPANIA FROM CLIENTES C " +
                        "JOIN DIRECCION_INSTALACION_TELEFONIA CC ON CC.ID_CLIENTE=C.ID_CLIENTE " +
                        "JOIN COMPANIAS X ON X.ID_COMPANIA=CC.ID_COMPANIA_INTERNET " +
                        "WHERE C.ID_CLIENTE=" + idCliente + " AND X.TIPO='INTERNET'";
                consultaContrato = "SELECT CC.FECHA_ACTIVACION_INTERNET FROM CLIENTES C " +
                        "JOIN DIRECCION_INSTALACION_TELEFONIA CC ON CC.ID_CLIENTE=C.ID_CLIENTE " +
                        "JOIN COMPANIAS X ON X.ID_COMPANIA=CC.ID_COMPANIA_INTERNET " +
                        "WHERE C.ID_CLIENTE=" + idCliente + " AND X.TIPO='INTERNET'";
                consultaBaja = "SELECT CC.FECHA_PERMANENCIA_INTERNET FROM CLIENTES C " +
                        "JOIN DIRECCION_INSTALACION_TELEFONIA CC ON CC.ID_CLIENTE=C.ID_CLIENTE " +
                        "JOIN COMPANIAS X ON X.ID_COMPANIA=CC.ID_COMPANIA_INTERNET " +
                        "WHERE C.ID_CLIENTE=" + idCliente + " AND X.TIPO='INTERNET'";
                consultaDireccion = "SELECT F.DIRECCION_FIBRA FROM CLIENTES C " +
                        "JOIN DIRECCION_INSTALACION_TELEFONIA F ON F.ID_CLIENTE=C.ID_CLIENTE " +
                        "JOIN COMPANIAS X ON X.ID_COMPANIA=F.ID_COMPANIA_INTERNET " +
                        "WHERE C.ID_CLIENTE=" + idCliente + " AND X.TIPO='INTERNET'";

                // Los telefonos no tienen fecha baja, asi que cambiamos el texto de la columna
                colFechaPermanencia.setText("FECHA PERMANENCIA");
                colCup.setText("DIRECCIÓN");
            }
        }


        ArrayList<String> resultadosCompanias = conexion.obtenerListaDatos(consultaCompanias);
        ArrayList<String> resultadosContrato = conexion.obtenerListaDatos(consultaContrato);
        ArrayList<String> resultadosPermanencia = conexion.obtenerListaDatos(consultaBaja);
        ArrayList<String> resultadoCup = conexion.obtenerListaDatos(consultaDireccion);

        ObservableList<Map> datosTabla = FXCollections.observableArrayList();

        for (int i = 0; i < resultadosCompanias.size(); i++) {
            Map<String, String> item = new HashMap<>();
            item.put("compania", resultadosCompanias.get(i));
            item.put("fechaContrato", resultadosContrato.get(i));
            item.put("fechaPermanencia", resultadosPermanencia.get(i));

            String cup = "";
            if (i < resultadoCup.size() && resultadoCup.get(i) != null && !resultadoCup.get(i).isEmpty()) {
                cup = resultadoCup.get(i);
            }
            try {
                item.put("cup", Encriptar.decrypt(cup));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            datosTabla.add(item);
        }

        tabla.setItems(datosTabla);
    }
}