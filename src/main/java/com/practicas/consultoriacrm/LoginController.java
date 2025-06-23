package com.practicas.consultoriacrm;

import com.practicas.consultoriacrm.Utils.Conexion;
import com.practicas.consultoriacrm.Utils.Encriptar;
import com.practicas.consultoriacrm.Utils.VerificarFormularios;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class LoginController {

    public static int idUsuario;
    private VerificarFormularios verificar;
    private Conexion conexion;

    // Componentes
    @FXML private TextField etxtUsuario;
    @FXML private PasswordField etxtContra;
    @FXML private Button btnIniciarSesion;

    @FXML private void initialize() {
        verificar = new VerificarFormularios();
        conexion = Conexion.getInstance();

        btnIniciarSesion.setOnMouseClicked(mouseEvent -> {
            try {
                iniciarSesion();

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        etxtContra.setOnKeyReleased(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                try {
                    iniciarSesion();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });

        etxtUsuario.setOnKeyReleased(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                try {
                    iniciarSesion();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });

    }

    // SETTERS
    public void setScene(Scene scene) {
    }

    // ///////////////////// MÉTODOS DE INICIO DE SESIÓN ///////////////////////
    private void iniciarSesion() throws Exception {
        String usuario = etxtUsuario.getText().toUpperCase();
        String contra = etxtContra.getText();

        if (usuario.isEmpty() || contra.isEmpty()) {
            verificar.crearAlerta("Campos vacíos", "No pueden haber campos vacíos");
            return;
        }

        // Creamos los textos encriptados
        String usuarioEncriptado = Encriptar.encrypt(usuario.toLowerCase());
        String contraEncriptada = Encriptar.encrypt(contra);

        // Verificar existencia del usuario
        String consultaExiste = "SELECT COUNT(*) FROM USUARIOS WHERE UPPER(NOMBRE)=?";
        int hayUsuariosConEseNombre = conexion.verificarExistenciaDato(consultaExiste, usuarioEncriptado);

        if (hayUsuariosConEseNombre <= 0) {
            verificar.crearAlerta("Usuario inexistente", "El usuario introducido no existe en la base de datos");
            return;
        }

        // Verificar que la contraseña es correcta
        String consultaContra = "SELECT CONTRASENA FROM USUARIOS WHERE NOMBRE=?";
        String contrasenaBDD = conexion.obtenerDatoStringBDD(consultaContra, usuarioEncriptado);

        if (!contraEncriptada.equals(contrasenaBDD)) {
            verificar.crearAlerta("Contraseña incorrecta", "La contraseña introducida es incorrecta");
            return;
        }

        String consultaIDUsuario = "SELECT ID FROM USUARIOS WHERE NOMBRE=?";
        idUsuario = conexion.obtenerDatoIntBDD(consultaIDUsuario, usuarioEncriptado);

        abrirCRM();
    }

    // Método para abrir el CRM
    private void abrirCRM() {
        try {
            FXMLLoader loader = new FXMLLoader(CRMApp.class.getResource("main-view.fxml"));
            Scene scene = new Scene(loader.load(), 1200, 640);

            Stage stage = (Stage) btnIniciarSesion.getScene().getWindow();
            stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResource("/images/icono.png")).toExternalForm()));

            // Conectar con el CSS
            String css = Objects.requireNonNull(this.getClass().getResource("estilos/estilos.css")).toExternalForm();
            scene.getStylesheets().add(css);

            // Obtener el controlador y pasarle la referencia de la Scene
            MainScreenController controller = loader.getController();
            controller.setScene(scene);

            stage.setScene(scene);
            stage.setTitle("CRM - Inicio");
            stage.setMaximized(true);
            stage.setResizable(true);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // /////////////////////////////////////////////////////////////////////////
}
