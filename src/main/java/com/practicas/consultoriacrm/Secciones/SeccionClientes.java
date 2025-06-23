package com.practicas.consultoriacrm.Secciones;

import com.practicas.consultoriacrm.LoginController;
import com.practicas.consultoriacrm.MainScreenController;
import com.practicas.consultoriacrm.Utils.Conexion;
import com.practicas.consultoriacrm.Utils.Encriptar;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

public class SeccionClientes {
    private static SeccionClientes instance;
    private final Conexion conexion;
    public Map<Integer, String> clientesMap = new LinkedHashMap<>();
    private ListView<String> lista;

    // Constructor
    private SeccionClientes() {
        conexion = Conexion.getInstance();
    }

    public static SeccionClientes getInstance() {
        if (instance == null) {
            instance = new SeccionClientes();
        }
        return instance;
    }

    public void setLista(ListView<String> lista) {
        this.lista = lista;
    }

    // Método para actualizar la lista de Clientes del buscador
    public void actualizarListaClientes() {
        ObservableList<String> clientes = obtenerClientesDesdeBD();
        lista.setItems(clientes);

        lista.getSelectionModel().clearSelection();
    }

    public ObservableList<String> obtenerClientesDesdeBD() {
        ObservableList<String> clientes = FXCollections.observableArrayList();
        Map<Integer, String> orderedClientesMap = new LinkedHashMap<>();
        String consulta;
        if(MainScreenController.privilegiosUsuario<=2) {
            consulta = "SELECT ID_CLIENTE, UPPER(NOMBRE) AS NOMBRE FROM CLIENTES ORDER BY NOMBRE ASC";
        }else{
            consulta = "SELECT ID_CLIENTE, UPPER(NOMBRE) AS NOMBRE FROM CLIENTES WHERE ID_USUARIO = "+LoginController.idUsuario +" ORDER BY NOMBRE ASC";
        }

        ResultSet rs = conexion.listarDatos(consulta);
        try {
            if (rs != null) {
                while (rs.next()) {
                    int id = rs.getInt("ID_CLIENTE");
                    String nombre = rs.getString("nombre");
                    orderedClientesMap.put(id, nombre);
                    clientes.add(nombre);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al procesar los resultados de la consulta: " + e.getMessage());
        }
        clientesMap = orderedClientesMap;
        return clientes;
    }

    public ObservableList<String> obtenerNIF() {
        ObservableList<String> clientes = FXCollections.observableArrayList();
        String consulta;
        if(MainScreenController.privilegiosUsuario<=2) {
            consulta = "SELECT NOMBRE, NIF FROM CLIENTES ORDER BY NOMBRE ASC";
        }else{
            consulta = "SELECT NOMBRE, NIF FROM CLIENTES WHERE ID_USUARIO = "+LoginController.idUsuario+" ORDER BY NOMBRE ASC";
        }

        ResultSet rs = conexion.listarDatos(consulta);
        try {
            if (rs != null) {
                while (rs.next()) {
                    String nif = rs.getString("NIF");
                    clientes.add(nif);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al procesar los resultados de la consulta: " + e.getMessage());
        }
        return clientes;
    }

    // Método para buscar el cliente en el buscador

    public void buscarClientes(String textoBusqueda) throws Exception {
        String textoBusquedaOriginal = textoBusqueda;
        textoBusqueda = textoBusqueda.trim().replaceAll("\\s+", "").toUpperCase();
        String searchHash = textoBusqueda.isEmpty() ? "" : Encriptar.generateSearchHash(textoBusqueda);

        String consulta;

        if(MainScreenController.privilegiosUsuario<=2){
            consulta = "SELECT C.ID_CLIENTE, UPPER(C.NOMBRE) AS NOMBRE " +
                    "FROM CLIENTES C " +
                    "LEFT JOIN TELEFONOS T ON C.ID_CLIENTE = T.ID_CLIENTE " +
                    "LEFT JOIN CUPS X ON X.ID_CLIENTE = C.ID_CLIENTE " +
                    "WHERE C.NOMBRE LIKE ? " +  // Búsqueda parcial por nombre
                    "OR C.NIF_HASH = ? " +     // Hash completo si se detecta NIF
                    "OR T.NUMERO_TELEFONO_HASH = ? " +
                    "OR X.CODIGO_CUP_HASH = ? " +
                    "GROUP BY C.ID_CLIENTE, C.NOMBRE " +
                    "ORDER BY C.NOMBRE ASC";
        }else{
            consulta = "SELECT C.ID_CLIENTE, UPPER(C.NOMBRE) AS NOMBRE " +
                    "FROM CLIENTES C " +
                    "LEFT JOIN TELEFONOS T ON C.ID_CLIENTE = T.ID_CLIENTE " +
                    "LEFT JOIN CUPS X ON X.ID_CLIENTE = C.ID_CLIENTE " +
                    "WHERE (C.NOMBRE LIKE ? " +  // Búsqueda parcial por nombre
                    "OR C.NIF_HASH = ? " +     // Hash completo si se detecta NIF
                    "OR T.NUMERO_TELEFONO_HASH = ? " +
                    "OR X.CODIGO_CUP_HASH = ?) AND C.ID_USUARIO = "+ LoginController.idUsuario +
                    " GROUP BY C.ID_CLIENTE, C.NOMBRE " +
                    "ORDER BY C.NOMBRE ASC";
        }


        try (PreparedStatement preparedStatement = conexion.getConnection().prepareStatement(consulta)) {
            preparedStatement.setString(1, textoBusquedaOriginal + "%");  // Búsqueda parcial por nombre
            preparedStatement.setString(2, searchHash);           // Comparación exacta con hash
            preparedStatement.setString(3, searchHash);
            preparedStatement.setString(4, searchHash);

            ResultSet rs = preparedStatement.executeQuery();
            ObservableList<String> resultados = FXCollections.observableArrayList();
            clientesMap.clear();
            while (rs.next()) {
                int id = rs.getInt("ID_CLIENTE");
                String nombre = rs.getString("NOMBRE");
                resultados.add(nombre);
                clientesMap.put(id, nombre);
            }
            lista.setItems(resultados);
        } catch (SQLException e) {
            System.out.println("Error al buscar clientes: " + e.getMessage());
        }
    }

}
