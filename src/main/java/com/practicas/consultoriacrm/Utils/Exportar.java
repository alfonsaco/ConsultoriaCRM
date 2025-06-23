package com.practicas.consultoriacrm.Utils;

import com.practicas.consultoriacrm.MainScreenController;
import javafx.scene.control.Alert;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.regex.Pattern;

public class Exportar {

    private final MainScreenController controller;

    public Exportar(MainScreenController controller) {
        this.controller = controller;
    }

    public void exportarListadoClientes() {

        try {
            // Obtener la fecha actual para el nombre del archivo
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy-HH-mm-ss");
            String fechaActual = dateFormat.format(new java.util.Date());

            // Crear el nombre del archivo
            String nombreArchivo = fechaActual + "_ListadoClientes.xlsx";

            // Obtener la carpeta de documentos del usuario
            String documentosPath = System.getProperty("user.home") + File.separator + "Documents";
            String carpetaClientes = documentosPath + File.separator + "Clientes";

            // Crear la carpeta si no existe
            if (!Files.exists(Paths.get(carpetaClientes))) {
                Files.createDirectories(Paths.get(carpetaClientes));
            }

            // Ruta completa del archivo
            String rutaCompleta = carpetaClientes + File.separator + nombreArchivo;

            // Verificar si el archivo ya existe
            File archivo = new File(rutaCompleta);
            int contador = 1;
            while (archivo.exists()) {
                nombreArchivo = fechaActual + "_ListadoClientes(" + contador + ").xlsx";
                rutaCompleta = carpetaClientes + File.separator + nombreArchivo;
                archivo = new File(rutaCompleta);
                contador++;
            }

            // Crear el libro de Excel
            try (Workbook workbook = new XSSFWorkbook()) {
                Sheet sheet = workbook.createSheet("Clientes");

                // Crear estilo para celdas con texto multilínea
                CellStyle multilineStyle = workbook.createCellStyle();
                multilineStyle.setWrapText(true); // Habilitar ajuste de texto

                // Crear fila de encabezados
                Row headerRow = sheet.createRow(0);
                String[] headers = {"Nombre", "NIF", "Dirección", "Localidad", "Provincia",
                        "CUPS", "Direcciones suministro", "Observaciones",
                        "Email", "Teléfono", "Luz", "Gas", "Internet", "Telefonía", "Alarma", "Direcciones fibra", "Direcciones alarma"};

                // Estilo para encabezados
                CellStyle headerStyle = workbook.createCellStyle();
                Font headerFont = workbook.createFont();
                headerFont.setBold(true);
                headerStyle.setFont(headerFont);

                // Llenar encabezados
                for (int i = 0; i < headers.length; i++) {
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(headers[i]);
                    cell.setCellStyle(headerStyle);
                }

                // Llenar datos
                int rowNum = 1;
                for (Map<String, String> cliente : controller.datosFiltrados) {
                    Row row = sheet.createRow(rowNum++);

                    // Crear celdas normales
                    row.createCell(0).setCellValue(getSafeValue(cliente, "nombre"));
                    row.createCell(1).setCellValue(Encriptar.decrypt(getSafeValue(cliente, "nif")));
                    row.createCell(2).setCellValue(Encriptar.decrypt(getSafeValue(cliente, "direccion")));
                    row.createCell(3).setCellValue(getSafeValue(cliente, "localidad"));
                    row.createCell(4).setCellValue(getSafeValue(cliente, "provincia"));
                    row.createCell(7).setCellValue(Encriptar.decrypt(getSafeValue(cliente, "observaciones")));

                    // Celdas con contenido multilínea (CUPS, Email, Teléfono)
                    Cell cupsCell = row.createCell(5);
                    cupsCell.setCellValue(decryptMultipleValues(getSafeValue(cliente, "cups")).replace("|", "\n"));
                    cupsCell.setCellStyle(multilineStyle);

                    Cell dirSuministroCell = row.createCell(6);
                    dirSuministroCell.setCellValue(decryptMultipleValues(getSafeValue(cliente, "dir_suministro")).replace("|", "\n"));
                    dirSuministroCell.setCellStyle(multilineStyle);

                    Cell emailCell = row.createCell(8);
                    emailCell.setCellValue(decryptMultipleValues(getSafeValue(cliente, "email")).replace("|", "\n"));
                    emailCell.setCellStyle(multilineStyle);

                    Cell telefonoCell = row.createCell(9);
                    telefonoCell.setCellValue(decryptMultipleValues(getSafeValue(cliente, "telefono")).replace("|", "\n"));
                    telefonoCell.setCellStyle(multilineStyle);

                    Cell luzCell = row.createCell(10);
                    luzCell.setCellValue(getSafeValue(cliente, "luz").replace("|", "\n"));
                    luzCell.setCellStyle(multilineStyle);

                    Cell gasCell = row.createCell(11);
                    gasCell.setCellValue(getSafeValue(cliente, "gas").replace("|", "\n"));
                    gasCell.setCellStyle(multilineStyle);

                    Cell internetCell = row.createCell(12);
                    internetCell.setCellValue(getSafeValue(cliente, "internet").replace("|", "\n"));
                    internetCell.setCellStyle(multilineStyle);

                    Cell telefoniaCell = row.createCell(13);
                    telefoniaCell.setCellValue(getSafeValue(cliente, "telefonia").replace("|", "\n"));
                    telefoniaCell.setCellStyle(multilineStyle);

                    Cell alarmaCell = row.createCell(14);
                    alarmaCell.setCellValue(getSafeValue(cliente, "alarma").replace("|", "\n"));
                    alarmaCell.setCellStyle(multilineStyle);

                    Cell dirFibraCell = row.createCell(15);
                    dirFibraCell.setCellValue(decryptMultipleValues(getSafeValue(cliente, "dir_fibra")).replace("|", "\n"));
                    dirFibraCell.setCellStyle(multilineStyle);

                    Cell dirAlarmaCell = row.createCell(16);
                    dirAlarmaCell.setCellValue(decryptMultipleValues(getSafeValue(cliente, "dir_alarma")).replace("|", "\n"));
                    dirAlarmaCell.setCellStyle(multilineStyle);
                }

                // Autoajustar columnas
                for (int i = 0; i < headers.length; i++) {
                    sheet.autoSizeColumn(i);
                }

                // Guardar archivo
                try (FileOutputStream outputStream = new FileOutputStream(archivo)) {
                    workbook.write(outputStream);

                    // Mostrar mensaje de éxito
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Exportación exitosa");
                    alert.setHeaderText(null);
                    alert.setContentText("El listado de clientes se ha exportado correctamente a:\n" + rutaCompleta);
                    alert.showAndWait();
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } catch (IOException e) {
            System.err.println("Error al exportar a Excel");
            e.printStackTrace();

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error en exportación");
            alert.setHeaderText(null);
            alert.setContentText("Ocurrió un error al exportar el listado de clientes.");
            alert.showAndWait();
        }
    }

    // Método auxiliar para manejar valores nulos
    private String getSafeValue(Map<String, String> map, String key) {
        return map.containsKey(key) && map.get(key) != null ? map.get(key) : "";
    }

    public boolean crearCarpeta() {
        VerificarFormularios verificar = new VerificarFormularios();
        if (!MainScreenController.hayClienteSeleccionado) {
            verificar.crearAlerta("Selecciona cliente", "Selecciona un cliente antes de utilizar esta función");
            return false;
        }

        String ruta = System.getProperty("user.home") + "/Documents";
        File carpeta = new File(ruta, "Clientes");

        if (!carpeta.exists()) {
            if (!carpeta.mkdir()) {
                verificar.crearAlerta("Error", "No se pudo crear la carpeta Clientes");
                return false;
            }
            verificar.crearAlerta("Carpeta creada", "Carpeta clientes creada");
        }

        File carpetaCliente = new File(carpeta, controller.idClienteSeleccionado + " - " + controller.nombreExportar.toUpperCase());
        if (carpetaCliente.exists()) {
            verificar.crearAlerta("Carpeta ya existe", "Ya existe una carpeta para el cliente " + controller.idClienteSeleccionado);
            return true; // Devuelve true aunque ya exista
        }

        if (carpetaCliente.mkdir()) {
            verificar.crearAlerta("Carpeta creada", "Carpeta del cliente con ID " + controller.idClienteSeleccionado + " creada");
            return true;
        }

        return false;
    }

    public void onCrearCarpetaYDocumentacion() {

    }

    private String decryptMultipleValues(String encryptedValues) {
        if (encryptedValues == null || encryptedValues.trim().isEmpty()) {
            return "";
        }

        try {
            // CORRECCIÓN: Usar Pattern.quote para escapar el separador
            String[] values = encryptedValues.split(Pattern.quote("|"));
            StringBuilder result = new StringBuilder();

            for (String value : values) {
                if (!value.trim().isEmpty()) {
                    if (!result.isEmpty()) {
                        result.append("\n");
                    }
                    try {
                        result.append(Encriptar.decrypt(value.trim()));
                    } catch (Exception e) {
                        result.append("[VALOR INVÁLIDO]");
                        System.err.println("Error desencriptando valor individual: " + value);
                    }
                }
            }

            return result.toString();
        } catch (Exception ex) {
            System.err.println("Error procesando valores múltiples: " + encryptedValues);
            return "[ERROR DESENCRIPTANDO]";
        }
    }
}
