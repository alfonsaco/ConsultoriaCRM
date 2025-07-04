# 📊 CRM

**CRM** desarrollado en Java con JavaFX y MySQL, realizado junto con un compañeros durante las prácticas de DAM. Permite gestionar usuarios, clientes y tareas con una interfaz sencilla y funcional. El proyecto es educativo y demostrativo, sin referencias a empresas reales, y muestra las habilidades adquiridas en programación y bases de datos.

<br>

## ⚙️ Funcionalidades

El CRM permite:
- Agregar clientes, junto con sus CUPS y direcciones de sumistro de Gas, Luz, Telefonía, Internet y Alarmas. Se permiten agregar varias direcciones por clientes.
- Gestión de usuarios con diferentes roles y permisos.
- Calendario para crear tareas para los ténicos
- Pestaña que muestra las renovaciones de los clientes
- Listado general con todos los clientes y sus datos, para mostrarlo de una forma más ordenada
- Sección de admin para añadir nuevos colaboradores y usuarios
- Sección para añadir nuevos empleados

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

> ⚠ **ADDONS NO AGUANTA MÁS DE 5 CONEXIONES A LA VEZ** ⚠  
> Cuando pasas más de unos minutos sin realizar ninguna acción en la aplicación, la base de datos se cierra automáticamente y hay que reiniciar la App. Esto se debe a que se ha utilizado **Addons Clever Cloud** para realizarla, la cual tiene esta limitación.


<br>

## 🙍‍♂️ Usuarios por defecto

El sistema incluye dos usuarios preconfigurados para facilitar las pruebas y el acceso inicial:

| Usuario | Contraseña | Rol      |
|---------|------------|----------|
| `user`  | `user`     | Técnico  |
| `admin` | `admin`    | Administrador |

<br>

## 🖼 Capturas
<img src="https://github.com/alfonsaco/ConsultoriaCRM/blob/main/src/main/resources/images/CAP1%20(1).PNG" alt="Stronger" width="250px">
<img src="https://github.com/alfonsaco/ConsultoriaCRM/blob/main/src/main/resources/images/CAP1%20(2).PNG" alt="Stronger" width="250px">
<img src="https://github.com/alfonsaco/ConsultoriaCRM/blob/main/src/main/resources/images/CAP1%20(3).PNG" alt="Stronger" width="250px">
<img src="https://github.com/alfonsaco/ConsultoriaCRM/blob/main/src/main/resources/images/CAP1%20(4).PNG" alt="Stronger" width="250px">
<img src="https://github.com/alfonsaco/ConsultoriaCRM/blob/main/src/main/resources/images/CAP1%20(5).PNG" alt="Stronger" width="250px">

<br>

## 🛠 Tecnologías utilizadas

- Java 23
- JavaFX 23
- MySQL
- Maven
- Dotenv para manejo de variables de entorno
- Addons Clever Cloud (Base de datos en la nube)

<br>

## 📄 Licencia

Este proyecto está protegido bajo una licencia personalizada para uso exclusivamente educativo y demostrativo. Puedes revisar los detalles en el archivo [LICENSE](LICENSE).

<br>

## 📬 Contacto

Para dudas, sugerencias o colaboraciones, puedes contactarme en:

- Email: alfonso.rincondev@gmail.com
- GitHub: [alfonsaco](https://github.com/alfonsaco)
