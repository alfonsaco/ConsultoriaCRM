package com.practicas.consultoriacrm.Utils;

import com.practicas.consultoriacrm.LoginController;
import com.practicas.consultoriacrm.MainScreenController;

import io.github.cdimascio.dotenv.Dotenv;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

// CLASE CON LA CONEXIÓN A LA BASE DE DATOS Y LOS MÉTODOS
public class Conexion {
    private static final String RUTA;
    private static final String USUARIO;
    private static final String CONTRA;
    private static Conexion instance;

    // CLAVES DEL ARCHIVO .ENV
    static {
        // Cargar el archivo .env
        Dotenv dotenv = Dotenv.load();

        // Obtener las variables de entorno
        RUTA = dotenv.get("DB_URL");
        USUARIO = dotenv.get("DB_USER");
        CONTRA = dotenv.get("DB_PASS");
    }

    private Connection connection;

    public Conexion() {
        VerificarFormularios verificar = new VerificarFormularios();
        try {
            connection = DriverManager.getConnection(RUTA, USUARIO, CONTRA);
            System.out.println("Conexión a la base de datos realizada con éxito");

        } catch (SQLException e) {
            System.out.println("Error al conectar a la base de datos: " + e.getMessage());
            verificar.crearAlerta("Error", "No hay conexión con la base de datos cerrando aplicación");
            System.exit(0);
        }
    }

    // Método para obtener la única instancia
    public static Conexion getInstance() {
        if (instance == null) {
            instance = new Conexion();
        }
        return instance;
    }


    // Getter
    public Connection getConnection() {
        return connection;
    }

    /*
      ------------------------------- MÉTODOS GENERALES -------------------------------
     */
    // MÉTODO PARA OBTENER UN CONJUNTO DE RESULTADOS (POR EJEMPLO, LISTAR CLIENTES)
    public ResultSet listarDatos(String consulta) {
        ResultSet rset = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            Statement statement = connection.createStatement();
            rset = statement.executeQuery(consulta);

        } catch (SQLException e) {
            e.printStackTrace();

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        return rset;
    }

    // MÉTODO PARA OBTENER UN DATO EN CUESTIÓN (POR EJEMPLO, OBTENER UN ID)
    public int obtenerDatoIntBDD(String consulta, String dato) {
        int id = 0;

        try (PreparedStatement pstmt = connection.prepareStatement(consulta)) {
            pstmt.setString(1, dato);

            try (ResultSet rset = pstmt.executeQuery()) {
                if (rset.next()) {
                    id = rset.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en verificarExistenciaDato: " + e.getMessage());
            e.printStackTrace();
        }

        return id;
    }

    public String obtenerDatoStringBDD(String consulta, String datoComprobar) {
        String dato = "";

        try (PreparedStatement pstmt = connection.prepareStatement(consulta)) {
            pstmt.setString(1, datoComprobar);

            try (ResultSet rset = pstmt.executeQuery()) {
                if (rset.next()) {
                    dato = rset.getString(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en verificarExistenciaDato: " + e.getMessage());
            e.printStackTrace();
        }


        return dato;
    }

    public void ejecutarConsulta(String consulta) {
        try {
            // Asegurarse de que el driver de MySQL esté cargado
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Crear un Statement para ejecutar la consulta
            Statement statement = connection.createStatement();

            // Ejecutar la consulta de actualización (INSERT, UPDATE, DELETE)
            statement.executeUpdate(consulta);

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    // MÉTODO PARA VERIFICAR SI EXISTE UN DATO ESPECÍFICO EN LA BASE DE DATOS
    public int verificarExistenciaDato(String consulta, String dato) {
        int numeroRegistros = 0;

        try (PreparedStatement pstmt = connection.prepareStatement(consulta)) {
            pstmt.setString(1, dato);

            try (ResultSet rset = pstmt.executeQuery()) {
                if (rset.next()) {
                    numeroRegistros = rset.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en verificarExistenciaDato: " + e.getMessage());
            e.printStackTrace();
        }

        return numeroRegistros;
    }

    // Para obtener una lista, por ejemplo de cups o de direcciones
    public ArrayList<String> obtenerListaDatos(String consulta) {
        ResultSet rset;
        ArrayList<String> listaDatos = new ArrayList<>();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            Statement statement = connection.createStatement();
            rset = statement.executeQuery(consulta);

            while (rset.next()) {
                String dato = rset.getString(1);
                listaDatos.add(dato);
            }

        } catch (SQLException e) {
            e.printStackTrace();

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        return listaDatos;
    }

    // MÉTODO PARA ELIMINAR UN DATO
    public void eliminarDatos(String consulta) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            Statement statement = connection.createStatement();
            statement.executeUpdate(consulta);

        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }
    /*
        ------------------------- FIN DE LOS MÉTODOS GENERALES ---------------------------
     */


    /*
        ------------------------- MÉTODOS PARA CUPS -------------------------
     */
    public int obtenerCupsCliente(String tipo, int idCliente) {
        String consulta = "SELECT COUNT(*) FROM CUPS WHERE ID_CLIENTE=" + idCliente + " AND TIPO='" + tipo + "'";
        int cantidadCups = 0;

        ResultSet rset;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            Statement statement = connection.createStatement();
            rset = statement.executeQuery(consulta);

            // Obtener el resultado del COUNT(*)
            if (rset.next()) {
                cantidadCups = rset.getInt(1);
            }

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();

        }

        return cantidadCups;
    }

    public int obtenerDireccionesClienteTelefono(int idCliente) {
        String consulta = "SELECT COUNT(*) FROM DIRECCION_INSTALACION_TELEFONIA WHERE ID_CLIENTE=" + idCliente;
        int cantidadDir = 0;

        ResultSet rset;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            Statement statement = connection.createStatement();
            rset = statement.executeQuery(consulta);

            // Obtener el resultado del COUNT(*)
            if (rset.next()) {
                cantidadDir = rset.getInt(1);
            }

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();

        }

        return cantidadDir;
    }

    public int obtenerDireccionesClienteAlarma(int idCliente) {
        String consulta = "SELECT COUNT(*) FROM DIRECCION_INSTALACION_ALARMAS WHERE ID_CLIENTE=" + idCliente;
        int cantidadDir = 0;

        ResultSet rset;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            Statement statement = connection.createStatement();
            rset = statement.executeQuery(consulta);

            // Obtener el resultado del COUNT(*)
            if (rset.next()) {
                cantidadDir = rset.getInt(1);
            }

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();

        }

        return cantidadDir;
    }

    public ObservableList<String> obtenerCompaniasPorTipo(String tipo) throws SQLException {
        ObservableList<String> companias = FXCollections.observableArrayList();
        String sql = "SELECT NOMBRE_COMPANIA AS NOMBRE FROM COMPANIAS WHERE TIPO = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, tipo);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                companias.add(rs.getString("NOMBRE"));
            }
        }
        return companias;
    }

    public ObservableList<String> obtenerColaboradores() throws SQLException {
        ObservableList<String> colaboradores = FXCollections.observableArrayList();
        String sql = "SELECT NOMBRE_COLABORADOR AS NOMBRE FROM COLABORADORES";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                colaboradores.add(rs.getString("NOMBRE"));
            }
        }
        return colaboradores;
    }

    public ObservableList<String> obtenerTarifasTipo(String tipo) throws SQLException {
        ObservableList<String> tarifas = FXCollections.observableArrayList();
        String sql = "SELECT TARIFA AS NOMBRE FROM TARIFAS WHERE TIPO = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, tipo);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                tarifas.add(rs.getString("NOMBRE"));
            }
        }
        return tarifas;
    }

    public ObservableList<String> obtenerClawbacks() throws SQLException {
        ObservableList<String> clawbacks = FXCollections.observableArrayList();
        String sql = "SELECT VALOR FROM CLAWBACKS";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                clawbacks.add(rs.getString("VALOR"));
            }
        }
        return clawbacks;
    }

    public int obtenerIDCompaniaPorNombreTipo(String nombre, String tipo) throws SQLException {
        int idCompania = 0;

        String sql = "SELECT ID_COMPANIA FROM COMPANIAS WHERE NOMBRE_COMPANIA = ? AND TIPO = ? ";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, nombre);
            stmt.setString(2, tipo);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                idCompania = rs.getInt("ID_COMPANIA");
            }
            return idCompania;
        }
    }

    public int obtenerIDColaborador(String nombre) throws SQLException {
        int idCompania = 0;

        String sql = "SELECT ID_COLABORADOR FROM COLABORADORES WHERE NOMBRE_COLABORADOR = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, nombre);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                idCompania = rs.getInt("ID_COLABORADOR");
            }
            return idCompania;
        }
    }

    public ObservableList<Map<String, String>> obtenerDatosClientes() {
        ObservableList<Map<String, String>> datos = FXCollections.observableArrayList();

        String query;

        if(MainScreenController.privilegiosUsuario<=2){
            query = "SELECT c.ID_CLIENTE, c.NOMBRE, c.NIF, c.DIRECCION, c.LOCALIDAD, c.PROVINCIA, " +
                    "c.OBSERVACIONES, " +
                    "COALESCE(GROUP_CONCAT(DISTINCT cl.CODIGO_CUP SEPARATOR '|'), '') AS CUPS, " +
                    "COALESCE(GROUP_CONCAT(DISTINCT cl.DIRECCION_SUMINISTRO SEPARATOR '|'), '') AS DIR_SUMINISTRO, " +
                    "COALESCE(GROUP_CONCAT(DISTINCT t.NUMERO_TELEFONO SEPARATOR '|'), '') AS TELEFONO, " +
                    "COALESCE(GROUP_CONCAT(DISTINCT e.EMAIL SEPARATOR '|'), '') AS EMAIL, " +
                    // Compañías por tipo
                    "COALESCE((SELECT GROUP_CONCAT(CO.NOMBRE_COMPANIA SEPARATOR '|') " +
                    "           FROM COMPANIAS CO " +
                    "           JOIN CUPS CC ON CO.ID_COMPANIA = CC.ID_COMPANIA " +
                    "          WHERE CC.ID_CLIENTE = c.ID_CLIENTE AND CO.TIPO = 'LUZ'), '') AS LUZ, " +
                    "COALESCE((SELECT GROUP_CONCAT(CO.NOMBRE_COMPANIA SEPARATOR '|') " +
                    "           FROM COMPANIAS CO " +
                    "           JOIN CUPS CC ON CO.ID_COMPANIA = CC.ID_COMPANIA " +
                    "          WHERE CC.ID_CLIENTE = c.ID_CLIENTE AND CO.TIPO = 'GAS'), '') AS GAS, " +
                    // Internet (modificado)
                    "COALESCE((SELECT GROUP_CONCAT(CO.NOMBRE_COMPANIA SEPARATOR '|') " +
                    "           FROM COMPANIAS CO " +
                    "           JOIN DIRECCION_INSTALACION_TELEFONIA CC ON CO.ID_COMPANIA = CC.ID_COMPANIA_INTERNET " +
                    "          WHERE CC.ID_CLIENTE = c.ID_CLIENTE AND CO.TIPO = 'INTERNET'), '') AS INTERNET, " +
                    // Telefonía (modificado)
                    "COALESCE((SELECT GROUP_CONCAT(CO.NOMBRE_COMPANIA SEPARATOR '|') " +
                    "           FROM COMPANIAS CO " +
                    "           JOIN DIRECCION_INSTALACION_TELEFONIA CC ON CO.ID_COMPANIA = CC.ID_COMPANIA_TELEFONIA " +
                    "          WHERE CC.ID_CLIENTE = c.ID_CLIENTE AND CO.TIPO = 'TELEFONIA'), '') AS TELEFONIA, " +
                    "COALESCE((SELECT GROUP_CONCAT(CO.NOMBRE_COMPANIA SEPARATOR '|') " +
                    "           FROM COMPANIAS CO " +
                    "           JOIN DIRECCION_INSTALACION_ALARMAS CC ON CO.ID_COMPANIA = CC.ID_COMPANIA " +
                    "          WHERE CC.ID_CLIENTE = c.ID_CLIENTE AND CO.TIPO = 'ALARMAS'), '') AS ALARMAS, " +
                    // Direcciones de instalación
                    "COALESCE((SELECT GROUP_CONCAT(DIRECCION_FIBRA SEPARATOR '|') " +
                    "           FROM DIRECCION_INSTALACION_TELEFONIA " +
                    "          WHERE ID_CLIENTE = c.ID_CLIENTE), '') AS DIR_FIBRA, " +
                    "COALESCE((SELECT GROUP_CONCAT(DIRECCION_ALARMA SEPARATOR '|') " +
                    "           FROM DIRECCION_INSTALACION_ALARMAS " +
                    "          WHERE ID_CLIENTE = c.ID_CLIENTE), '') AS DIR_ALARMA " +
                    "FROM CLIENTES c " +
                    "LEFT JOIN CUPS cl ON c.ID_CLIENTE = cl.ID_CLIENTE " +
                    "LEFT JOIN TELEFONOS t ON c.ID_CLIENTE = t.ID_CLIENTE " +
                    "LEFT JOIN EMAILS e ON c.ID_CLIENTE = e.ID_CLIENTE " +
                    "GROUP BY c.ID_CLIENTE, c.NOMBRE, c.NIF, c.DIRECCION, c.LOCALIDAD, c.PROVINCIA, " +
                    "c.OBSERVACIONES " +
                    "ORDER BY c.NOMBRE ASC";
        }else{
            query = "SELECT c.ID_CLIENTE, c.NOMBRE, c.NIF, c.DIRECCION, c.LOCALIDAD, c.PROVINCIA, " +
                    "c.OBSERVACIONES, " +
                    "COALESCE(GROUP_CONCAT(DISTINCT cl.CODIGO_CUP SEPARATOR '|'), '') AS CUPS, " +
                    "COALESCE(GROUP_CONCAT(DISTINCT cl.DIRECCION_SUMINISTRO SEPARATOR '|'), '') AS DIR_SUMINISTRO, " +
                    "COALESCE(GROUP_CONCAT(DISTINCT t.NUMERO_TELEFONO SEPARATOR '|'), '') AS TELEFONO, " +
                    "COALESCE(GROUP_CONCAT(DISTINCT e.EMAIL SEPARATOR '|'), '') AS EMAIL, " +
                    // Compañías por tipo
                    "COALESCE((SELECT GROUP_CONCAT(CO.NOMBRE_COMPANIA SEPARATOR '|') " +
                    "           FROM COMPANIAS CO " +
                    "           JOIN CUPS CC ON CO.ID_COMPANIA = CC.ID_COMPANIA " +
                    "          WHERE CC.ID_CLIENTE = c.ID_CLIENTE AND CO.TIPO = 'LUZ'), '') AS LUZ, " +
                    "COALESCE((SELECT GROUP_CONCAT(CO.NOMBRE_COMPANIA SEPARATOR '|') " +
                    "           FROM COMPANIAS CO " +
                    "           JOIN CUPS CC ON CO.ID_COMPANIA = CC.ID_COMPANIA " +
                    "          WHERE CC.ID_CLIENTE = c.ID_CLIENTE AND CO.TIPO = 'GAS'), '') AS GAS, " +
                    // Internet (modificado)
                    "COALESCE((SELECT GROUP_CONCAT(CO.NOMBRE_COMPANIA SEPARATOR '|') " +
                    "           FROM COMPANIAS CO " +
                    "           JOIN DIRECCION_INSTALACION_TELEFONIA CC ON CO.ID_COMPANIA = CC.ID_COMPANIA_INTERNET " +
                    "          WHERE CC.ID_CLIENTE = c.ID_CLIENTE AND CO.TIPO = 'INTERNET'), '') AS INTERNET, " +
                    // Telefonía (modificado)
                    "COALESCE((SELECT GROUP_CONCAT(CO.NOMBRE_COMPANIA SEPARATOR '|') " +
                    "           FROM COMPANIAS CO " +
                    "           JOIN DIRECCION_INSTALACION_TELEFONIA CC ON CO.ID_COMPANIA = CC.ID_COMPANIA_TELEFONIA " +
                    "          WHERE CC.ID_CLIENTE = c.ID_CLIENTE AND CO.TIPO = 'TELEFONIA'), '') AS TELEFONIA, " +
                    "COALESCE((SELECT GROUP_CONCAT(CO.NOMBRE_COMPANIA SEPARATOR '|') " +
                    "           FROM COMPANIAS CO " +
                    "           JOIN DIRECCION_INSTALACION_ALARMAS CC ON CO.ID_COMPANIA = CC.ID_COMPANIA " +
                    "          WHERE CC.ID_CLIENTE = c.ID_CLIENTE AND CO.TIPO = 'ALARMAS'), '') AS ALARMAS, " +
                    // Direcciones de instalación
                    "COALESCE((SELECT GROUP_CONCAT(DIRECCION_FIBRA SEPARATOR '|') " +
                    "           FROM DIRECCION_INSTALACION_TELEFONIA " +
                    "          WHERE ID_CLIENTE = c.ID_CLIENTE), '') AS DIR_FIBRA, " +
                    "COALESCE((SELECT GROUP_CONCAT(DIRECCION_ALARMA SEPARATOR '|') " +
                    "           FROM DIRECCION_INSTALACION_ALARMAS " +
                    "          WHERE ID_CLIENTE = c.ID_CLIENTE), '') AS DIR_ALARMA " +
                    "FROM CLIENTES c " +
                    "LEFT JOIN CUPS cl ON c.ID_CLIENTE = cl.ID_CLIENTE " +
                    "LEFT JOIN TELEFONOS t ON c.ID_CLIENTE = t.ID_CLIENTE " +
                    "LEFT JOIN EMAILS e ON c.ID_CLIENTE = e.ID_CLIENTE " +
                    "WHERE ID_USUARIO = " + LoginController.idUsuario +
                    " GROUP BY c.ID_CLIENTE, c.NOMBRE, c.NIF, c.DIRECCION, c.LOCALIDAD, c.PROVINCIA, " +
                    "c.OBSERVACIONES " +
                    "ORDER BY c.NOMBRE ASC";
        }

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Map<String, String> fila = new HashMap<>();
                // Campos básicos
                fila.put("nombre", rs.getString("NOMBRE"));
                fila.put("nif", rs.getString("NIF"));
                fila.put("direccion", rs.getString("DIRECCION"));
                fila.put("localidad", rs.getString("LOCALIDAD"));
                fila.put("provincia", rs.getString("PROVINCIA"));
                fila.put("observaciones", rs.getString("OBSERVACIONES"));

                // Campos concatenados
                fila.put("cups", rs.getString("CUPS"));
                fila.put("telefono", rs.getString("TELEFONO"));
                fila.put("email", rs.getString("EMAIL"));

                fila.put("luz", rs.getString("LUZ"));
                fila.put("gas", rs.getString("GAS"));
                fila.put("internet", rs.getString("INTERNET"));
                fila.put("telefonia", rs.getString("TELEFONIA"));
                fila.put("alarma", rs.getString("ALARMAS"));

                fila.put("dir_fibra", rs.getString("DIR_FIBRA"));
                fila.put("dir_alarma", rs.getString("DIR_ALARMA"));
                fila.put("dir_suministro", rs.getString("DIR_SUMINISTRO"));

                datos.add(fila);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener clientes: " + e.getMessage());
            e.printStackTrace();
        }

        return datos;
    }


    public ObservableList<String[]> obtenerCupsClawback() {
        ObservableList<String[]> listaCups = FXCollections.observableArrayList();
        String sql;
        int privilegios;
        try {
            privilegios = obtenerPrivilegiosUsuario(LoginController.idUsuario);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        if(privilegios<=2) {
            sql = "SELECT CU.CODIGO_CUP AS CUP, CU.TIPO AS TIPO, CU.CLAWBACK AS CLAWBACK, CU.FECHA_ACTIVACION AS ACTIVACION, CU.COMERCIALIZADORA AS COMERCIALIZADORA, C.NOMBRE AS CLIENTE FROM CUPS CU LEFT JOIN CLIENTES C ON CU.ID_CLIENTE = C.ID_CLIENTE WHERE CU.COMERCIALIZADORA IS NOT NULL AND CU.CLAWBACK IS NOT NULL AND CU.FECHA_ACTIVACION IS NOT NULL";
        }else{
            sql ="SELECT CU.CODIGO_CUP AS CUP, CU.TIPO AS TIPO, CU.CLAWBACK AS CLAWBACK, CU.FECHA_ACTIVACION AS ACTIVACION, CU.COMERCIALIZADORA AS COMERCIALIZADORA, C.NOMBRE AS CLIENTE FROM CUPS CU LEFT JOIN CLIENTES C ON CU.ID_CLIENTE = C.ID_CLIENTE WHERE CU.COMERCIALIZADORA IS NOT NULL AND CU.CLAWBACK IS NOT NULL AND CU.FECHA_ACTIVACION IS NOT NULL AND C.ID_USUARIO = "+LoginController.idUsuario;
        }
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String[] fila = new String[7];
                fila[0] = rs.getString("CLIENTE");
                try {
                    fila[1] = Encriptar.decrypt(rs.getString("CUP"));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                fila[2] = rs.getString("TIPO");
                fila[3] = rs.getString("CLAWBACK");
                fila[4] = String.valueOf(rs.getDate("ACTIVACION"));

                // Calculamos la fecha real de clawback
                LocalDate fechaActivacion = rs.getDate("ACTIVACION").toLocalDate();
                LocalDate fechaClawbackReal = calcularFechaClawback(fechaActivacion, fila[3]);
                fila[5] = fechaClawbackReal.toString(); // Guardamos la fecha calculada para ordenarla
                fila[6] = rs.getString("COMERCIALIZADORA");

                listaCups.add(fila);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return listaCups;
    }

    // Método para calcular la fecha de clawback real
    private LocalDate calcularFechaClawback(LocalDate fechaActivacion, String clawback) {
        if (clawback.contains("MESES")) {
            int meses = Integer.parseInt(clawback.split(" ")[0]);
            return fechaActivacion.plusMonths(meses);
        } else if (clawback.contains("AÑO")) {
            return fechaActivacion.plusYears(1);
        }
        throw new IllegalArgumentException("Clawback desconocido: " + clawback);
    }

    public ObservableList<String[]> obtenerDatosInternet() {
        ObservableList<String[]> listaInternet = FXCollections.observableArrayList();
        String sql = "SELECT DIT.DIRECCION_FIBRA AS DIRECCIÓN , " +
                "FECHA_PERMANENCIA_INTERNET AS PERMANENCIA, " +
                "C.NOMBRE AS CLIENTE, " +
                "CO.NOMBRE_COMPANIA AS COMPAÑÍA " +
                "FROM DIRECCION_INSTALACION_TELEFONIA DIT " +
                "LEFT JOIN CLIENTES C ON DIT.ID_CLIENTE = C.ID_CLIENTE " +
                "LEFT JOIN COMPANIAS CO ON DIT.ID_COMPANIA_INTERNET = CO.ID_COMPANIA " +
                "WHERE DIT.ID_COMPANIA_INTERNET IS NOT NULL AND FECHA_PERMANENCIA_INTERNET IS NOT NULL";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String[] fila = new String[4];
                fila[0] = rs.getString("CLIENTE");
                try {
                    fila[1] = Encriptar.decrypt(rs.getString("DIRECCIÓN"));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                fila[2] = rs.getString("COMPAÑÍA");
                fila[3] = String.valueOf(rs.getDate("PERMANENCIA"));


                listaInternet.add(fila);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return listaInternet;
    }

    public ObservableList<String[]> obtenerDatosTelefonia() {
        ObservableList<String[]> listaTelefonia = FXCollections.observableArrayList();
        String sql = "SELECT DIT.DIRECCION_FIBRA AS DIRECCIÓN , FECHA_PERMANENCIA_TELEFONIA AS PERMANENCIA, C.NOMBRE AS CLIENTE, CO.NOMBRE_COMPANIA AS COMPAÑÍA FROM DIRECCION_INSTALACION_TELEFONIA DIT LEFT JOIN CLIENTES C ON DIT.ID_CLIENTE = C.ID_CLIENTE LEFT JOIN COMPANIAS CO ON DIT.ID_COMPANIA_TELEFONIA = CO.ID_COMPANIA WHERE DIT.ID_COMPANIA_TELEFONIA IS NOT NULL AND FECHA_PERMANENCIA_TELEFONIA IS NOT NULL";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String[] fila = new String[4];
                fila[0] = rs.getString("CLIENTE");
                try {
                    fila[1] = Encriptar.decrypt(rs.getString("DIRECCIÓN"));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                fila[2] = rs.getString("COMPAÑÍA");
                fila[3] = String.valueOf(rs.getDate("PERMANENCIA"));


                listaTelefonia.add(fila);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return listaTelefonia;
    }

    public ObservableList<String[]> obtenerDatosAlarma() {
        ObservableList<String[]> listaAlarma = FXCollections.observableArrayList();
        String sql = "SELECT DIA.DIRECCION_ALARMA AS DIRECCIÓN, FECHA_PERMANENCIA AS PERMANENCIA, C.NOMBRE AS CLIENTE, CO.NOMBRE_COMPANIA AS COMPAÑÍA FROM DIRECCION_INSTALACION_ALARMAS DIA LEFT JOIN CLIENTES C ON DIA.ID_CLIENTE = C.ID_CLIENTE LEFT JOIN COMPANIAS CO ON DIA.ID_COMPANIA = CO.ID_COMPANIA WHERE DIA.ID_COMPANIA IS NOT NULL AND FECHA_PERMANENCIA IS NOT NULL";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String[] fila = new String[4];
                fila[0] = rs.getString("CLIENTE");
                try {
                    fila[1] = Encriptar.decrypt(rs.getString("DIRECCIÓN"));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                fila[2] = rs.getString("COMPAÑÍA");
                fila[3] = String.valueOf(rs.getDate("PERMANENCIA"));


                listaAlarma.add(fila);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return listaAlarma;
    }

    public int obtenerPrivilegiosUsuario(int idUsuario) throws SQLException {
        String sql = "SELECT PRIVILEGIOS FROM USUARIOS WHERE ID = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("PRIVILEGIOS");
                } else {
                    return 0;
                }
            }
        }
    }

    public void agregarNuevoUsuario(String consulta, String nombre, String contra, int privilegios) {
        try (PreparedStatement pstmt = connection.prepareStatement(consulta)) {
            pstmt.setString(1, nombre);
            pstmt.setString(2, contra);
            pstmt.setInt(3, privilegios);

            int filasInsertadas = pstmt.executeUpdate();
            if (filasInsertadas > 0) {
                System.out.println("Usuario insertado con éxito.");
            }
        } catch (SQLException e) {
            System.err.println("Error al insertar usuario: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
