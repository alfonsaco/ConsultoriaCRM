package com.practicas.consultoriacrm.Secciones;

import com.calendarfx.model.Calendar;
import com.calendarfx.model.CalendarEvent;
import com.calendarfx.model.CalendarSource;
import com.calendarfx.model.Entry;
import com.calendarfx.view.CalendarView;
import com.practicas.consultoriacrm.Utils.Conexion;
import javafx.event.Event;
import javafx.geometry.Pos;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

public class SeccionCalendario {
    private final AnchorPane calendario;
    private final Conexion conexion;
    private final int idUsuarioActual;
    private final CalendarSource myCalendarSource;
    private final Calendar calendarioReuniones;
    private final Calendar calendarioClientes;
    private CalendarView calendarView;
    private boolean cargandoEventos = false;

    public SeccionCalendario(AnchorPane calendario, int idUsuarioActual) {
        this.calendario = calendario;
        this.conexion = Conexion.getInstance();
        this.idUsuarioActual = idUsuarioActual;

        // Configurar los calendarios
        this.myCalendarSource = new CalendarSource("Mis Calendarios");

        // Crear los 3 calendarios con estilos diferentes
        this.calendarioReuniones = new Calendar("No realizado");
        calendarioReuniones.setStyle(Calendar.Style.STYLE5); // Rojito

        this.calendarioClientes = new Calendar("Realizado");
        calendarioClientes.setStyle(Calendar.Style.STYLE1); // Verde

        // Añadir los calendarios al CalendarSource

        myCalendarSource.getCalendars().addAll(calendarioReuniones, calendarioClientes);
    }

    public void configurarCalendarView(CalendarView calendarView) {
        this.calendarView = calendarView;

        // Añadir el CalendarSource a la vista
        calendarView.getCalendarSources().clear();
        calendarView.getCalendarSources().add(myCalendarSource);

        // Configurar el Entry Factory
        calendarView.setEntryFactory(param -> {
            Entry<String> newEntry = new Entry<>();
            newEntry.setTitle("Nuevo Evento");
            newEntry.setCalendar(calendarioReuniones); // Calendario por defecto

            // Configurar el intervalo de tiempo por defecto (1 hora)
            ZonedDateTime start = param.getZonedDateTime();
            ZonedDateTime end = start.plusHours(1);
            newEntry.setInterval(start, end);

            return newEntry;
        });

        // Configuración visual
        calendarView.setMinSize(0, 0);
        calendarView.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        Region container = new Region();
        container.setStyle("-fx-background-color: transparent;");
        StackPane.setAlignment(calendarView, Pos.CENTER);

        StackPane wrapper = new StackPane(container, calendarView);
        wrapper.setAlignment(Pos.CENTER);

        AnchorPane.setBottomAnchor(wrapper, 0.0);
        AnchorPane.setTopAnchor(wrapper, 0.0);
        AnchorPane.setLeftAnchor(wrapper, 0.0);
        AnchorPane.setRightAnchor(wrapper, 0.0);

        calendarView.prefWidthProperty().bind(wrapper.widthProperty());
        calendarView.prefHeightProperty().bind(wrapper.heightProperty());

        calendario.getChildren().add(wrapper);

        calendarView.setShowAddCalendarButton(false);
        calendarView.setShowPrintButton(false);
        calendario.setVisible(false);

        // Configurar listeners para todos los calendarios
        configurarListenersDeCalendario();
    }

    private void configurarListenersDeCalendario() {
        // Listeners para cambios en todos los calendarios
        calendarioReuniones.addEventHandler(this::manejarEventoCalendario);
        calendarioClientes.addEventHandler(this::manejarEventoCalendario);
    }

    private void manejarEventoCalendario(Event event) {
        if (cargandoEventos) return;
        try {
            CalendarEvent c = (CalendarEvent) event;
            Entry<?> entry = c.getEntry();

            if (entry != null) {
                if (c.isEntryAdded()) {
                    guardarEntradaEnBD(entry);
                } else if (c.isEntryRemoved()) {
                    eliminarEntradaDeBD(entry);
                } else {
                    actualizarEntradaEnBD(entry);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al manejar evento de calendario: " + e.getMessage());
        }
    }

    private void guardarEntradaEnBD(Entry<?> entry) throws SQLException {
        Connection conn = conexion.getConnection();
        PreparedStatement pstmt = null;
        ResultSet generatedKeys = null;

        try {
            pstmt = conn.prepareStatement(
                    "INSERT INTO ENTRADAS (TITULO, FECHA_INICIO, FECHA_FIN, DIA_COMPLETO, CALENDARIO, ID_USUARIO, RECURRENCIA) VALUES (?, ?, ?, ?, ?, ?, ?)",
                    PreparedStatement.RETURN_GENERATED_KEYS);

            pstmt.setString(1, entry.getTitle());
            pstmt.setObject(2, entry.getStartAsZonedDateTime());
            pstmt.setObject(3, entry.getEndAsZonedDateTime());
            pstmt.setBoolean(4, entry.isFullDay());
            pstmt.setString(5, entry.getCalendar().getName()); // Guardamos el nombre del calendario
            pstmt.setInt(6, idUsuarioActual);
            pstmt.setString(7, entry.getRecurrenceRule());

            pstmt.executeUpdate();

            generatedKeys = pstmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                entry.setId(String.valueOf(generatedKeys.getInt(1)));
            }
        } finally {
            if (generatedKeys != null) generatedKeys.close();
            if (pstmt != null) pstmt.close();
        }
    }

    private void actualizarEntradaEnBD(Entry<?> entry) throws SQLException {
        Connection conn = conexion.getConnection();

        try (PreparedStatement pstmt = conn.prepareStatement(
                "UPDATE ENTRADAS SET TITULO = ?, FECHA_INICIO = ?, FECHA_FIN = ?, DIA_COMPLETO = ?, CALENDARIO = ?, RECURRENCIA = ? WHERE ID = ?")) {

            pstmt.setString(1, entry.getTitle());
            pstmt.setObject(2, entry.getStartAsZonedDateTime());
            pstmt.setObject(3, entry.getEndAsZonedDateTime());
            pstmt.setBoolean(4, entry.isFullDay());
            pstmt.setString(5, entry.getCalendar().getName()); // Actualizamos el calendario
            pstmt.setString(6, entry.getRecurrenceRule());
            pstmt.setInt(7, Integer.parseInt(entry.getId()));

            pstmt.executeUpdate();
        }
    }

    private void eliminarEntradaDeBD(Entry<?> entry) throws SQLException {
        Connection conn = conexion.getConnection();

        try (PreparedStatement pstmt = conn.prepareStatement("DELETE FROM ENTRADAS WHERE ID = ?")) {
            pstmt.setInt(1, Integer.parseInt(entry.getId()));
            pstmt.executeUpdate();
        }
    }

    public void cargarEventosDesdeBD() throws SQLException {
        cargandoEventos = true;
        Connection conn = conexion.getConnection();

        // Limpiar los calendarios antes de cargar
        calendarioReuniones.clear();
        calendarioClientes.clear();

        PreparedStatement pstmt = conn.prepareStatement(
                "SELECT ID, TITULO, FECHA_INICIO, FECHA_FIN, DIA_COMPLETO, CALENDARIO, RECURRENCIA FROM ENTRADAS WHERE ID_USUARIO = ?");
        pstmt.setInt(1, idUsuarioActual);
        ResultSet rs = pstmt.executeQuery();

        while (rs.next()) {
            Entry<String> entry = new Entry<>(rs.getString("TITULO"));
            entry.setId(String.valueOf(rs.getInt("ID")));

            LocalDateTime start = rs.getObject("FECHA_INICIO", LocalDateTime.class);
            LocalDateTime end = rs.getObject("FECHA_FIN", LocalDateTime.class);

            entry.setInterval(start, end);
            entry.setFullDay(rs.getBoolean("DIA_COMPLETO"));

            String nombreCalendario = rs.getString("CALENDARIO");
            Calendar calendarioDestino = obtenerCalendarioPorNombre(nombreCalendario);

            if (calendarioDestino != null) {
                entry.setCalendar(calendarioDestino);
                calendarioDestino.addEntry(entry);
            }

            String rrule = rs.getString("RECURRENCIA");
            if (rrule != null && !rrule.isEmpty()) {
                entry.setRecurrenceRule(rrule);
            }
        }
        cargandoEventos = false;
    }

    private Calendar obtenerCalendarioPorNombre(String nombre) {
        return switch (nombre) {
            case "Reuniones" -> calendarioReuniones;
            case "Clientes" -> calendarioClientes;
            default -> calendarioReuniones; // Por defecto
        };
    }

}