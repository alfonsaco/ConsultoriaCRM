# 📊 CRM

**CRM** desarrollado en Java con JavaFX y MySQL, realizado en grupo durante las prácticas de DAM. Permite gestionar usuarios, clientes y tareas con una interfaz sencilla y funcional. El proyecto es educativo y demostrativo, sin referencias a empresas reales, y muestra las habilidades adquiridas en programación y bases de datos.

<br>

## ⚙️ Funcionalidades

El CRM permite:

- Gestión de usuarios con diferentes roles y permisos.

<br>

## 🚀 Instalación

### Requisitos
- IntelliJ o cualquier programa para ejecutar y crear aplicaciones Java
- Java
- MySQL

### Pasos
1. Clonar el repositorio:
```
git clone https://github.com/alfonsaco/ConsultoriaCRM.git
```

2. Crea la base de datos en tu servidor MySQL.
3. Importa la estructura de la base de datos. La encontrarás en un .txt en la carpeta DB dentro del proyecto (database.txt).
4. Configura el archivo .env con las credenciales y parámetros de conexión a tu base de datos. El archivo debe contener variables como:
```
DB_URL=jdbc:mysql://tu_servidor:puerto/tu_base_de_datos
DB_USER=tu_usuario
DB_PASS=tu_contraseña
CLAVE_ENCRIPT=1234567891234567
```
La clave del encriptado debe ser **1234567891234567**.

5. Abre el proyecto en tu IDE (Ej: IntelliJ) y ejecuta la aplicación.

<br>

## 🙍‍♂️ Usuarios por defecto

El sistema incluye dos usuarios preconfigurados para facilitar las pruebas y el acceso inicial:

| Usuario | Contraseña | Rol      |
|---------|------------|----------|
| `user`  | `user`     | Técnico  |
| `admin` | `admin`    | Administrador |

<br>

## 🛠 Tecnologías utilizadas

- Java 23
- JavaFX 23
- MySQL
- Maven
- Dotenv para manejo de variables de entorno

<br>

## 📄 Licencia

Este proyecto está protegido bajo una licencia personalizada para uso exclusivamente educativo y demostrativo. Puedes revisar los detalles en el archivo [LICENSE](LICENSE).

<br>

## 📬 Contacto

Para dudas, sugerencias o colaboraciones, puedes contactarme en:

- Email: alfonso.rincondev@gmail.com
- GitHub: [alfonsaco](https://github.com/alfonsaco)
