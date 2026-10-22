# Sistema de préstamo de equipos tecnológicos

## Curso

EIF206 – Programación III. Examen práctico integrador, Caso 1.

## Integrantes

| Nombre | Usuario de GitHub |
|---|---|
| John Perez Quesada | [JohnPQ2312](https://github.com/JohnPQ2312) |
| Joe Sandoval Diaz | [JoeSandoval22](https://github.com/JoeSandoval22) |
| Joshua Barrantes Granados | [andreybarra38-netizen](https://github.com/andreybarra38-netizen) |

Grupo de tres integrantes autorizado por el profesor. Los aportes están descritos en la [entrega de la Etapa 3](../docs/etapa_3/entrega.md#distribución-del-trabajo).

## Caso asignado

**Caso 1. Préstamo de equipos tecnológicos.**

## Descripción del sistema

Aplicación de consola para registrar solicitantes y equipos, controlar la disponibilidad y administrar préstamos, devoluciones y renovaciones. La información se almacena en MySQL mediante JDBC.

## Funcionalidades implementadas

| Menú | Opciones |
|---|---|
| Principal | 1: probar conexión; 2: solicitantes; 3: equipos; 4: préstamos; 0: salir. |
| Solicitantes | 1: registrar; 2: buscar por identificación; 3: listar; 4: actualizar nombre, correo y tipo; 0: volver. |
| Equipos | 1: registrar; 2: buscar por código; 3: listar; 4: actualizar descripción, tipo y estado; 0: volver. |
| Préstamos | 1: registrar; 2: devolver; 3: consultar activos de un solicitante; 4: renovar; 5: listar activos vencidos; 0: volver. |

Las consultas de préstamos relacionan solicitantes y equipos mediante JOIN. El listado de vencidos muestra nombre del solicitante, código del equipo y días de atraso. Las fechas de entrada usan `AAAA-MM-DD`; el programa toma la fecha actual de la computadora.

## Reglas de negocio implementadas

Se conservan RN-01 a RN-08 de las etapas anteriores y se agregan las reglas de renovación.

| ID | Regla | Dónde se valida |
|---|---|---|
| RN-01 | Solo se presta un equipo DISPONIBLE. | `PrestamoService.registrarPrestamo`. |
| RN-02 | Máximo de dos préstamos activos para estudiantes y cuatro para funcionarios. | `PrestamoService.registrarPrestamo`. |
| RN-03 | La devolución guarda la fecha real, deja el préstamo DEVUELTO y libera el equipo. | `PrestamoService.registrarDevolucion` y transacción JDBC. |
| RN-04 | Identificaciones de solicitante y códigos de equipo únicos. | Registro en ambos servicios y claves primarias de MySQL. |
| RN-05 | Solicitante y equipo existentes antes de prestar. | `PrestamoService.registrarPrestamo`. |
| RN-06 | La fecha esperada de un préstamo nuevo debe ser posterior a hoy. | `PrestamoService.registrarPrestamo`. |
| RN-07 | El préstamo nuevo queda ACTIVO y el equipo PRESTADO; ambas escrituras se confirman juntas. | `PrestamoService.registrarPrestamo` y transacción JDBC. |
| RN-08 | Solo puede devolverse un préstamo existente y activo. | `PrestamoService.registrarDevolucion`. |
| RN-09 | Solo una renovación por préstamo, si está activo y su fecha esperada no es anterior a hoy. | `PrestamoService.renovarPrestamo`; contador persistido y restricción SQL de cero a uno. |
| RN-10 | Renovar suma tres días para estudiantes o siete para funcionarios desde la fecha esperada anterior. | `PrestamoService.renovarPrestamo`. |

`ValidadorDatos` comprueba campos obligatorios, formato de correo y longitudes máximas: identificación 30, nombre 100, correo 150, código de equipo 20, descripción 100 y tipo de equipo 50. Se utiliza en el registro y la actualización de solicitantes y equipos.

Los servicios también impiden cambiar a estudiante a una persona con más de dos préstamos activos y asignar al equipo un estado incompatible con sus préstamos. MySQL aplica las claves, referencias, estados permitidos y unicidad del correo. Los menús presentan las excepciones de validación y de datos y permiten continuar.

## Arquitectura

```text
App y menús → Controller → Service → Repository (interfaz) → RepositoryJdbc → MySQL
```

`App` construye las dependencias. Los menús leen y presentan datos; los controladores delegan; los servicios aplican reglas y reciben interfaces por constructor. Las implementaciones JDBC ejecutan SQL con `PreparedStatement`, mapean resultados y cierran recursos con `try-with-resources`. Registro de préstamo y devolución coordinan los cambios de préstamo y equipo en una misma transacción.

`PrestamoVencido` transporta los tres datos del listado de vencidos. Los repositorios en memoria y demás implementaciones de prueba se encuentran en `src/test`; la aplicación utiliza los repositorios JDBC.

## Estructura del proyecto

```text
proyecto/
├── pom.xml
└── src/
    ├── main/
    │   ├── java/cr/ac/una/
    │   │   ├── app/
    │   │   ├── config/
    │   │   ├── controller/
    │   │   ├── exception/
    │   │   ├── model/
    │   │   ├── repository/
    │   │   └── service/
    │   └── resources/db.properties.example
    └── test/java/cr/ac/una/
```

El [SQL](../database/schema.sql), los [diagramas editables](../docs/etapa_1/diagramas/) y la [documentación de la Etapa 3](../docs/etapa_3/entrega.md) están en la raíz del repositorio, fuera de la carpeta `proyecto`.

## Configuración de la base de datos

1. Para una instalación desde cero, importar [database/schema.sql](../database/schema.sql) en una instancia MySQL donde se disponga de permisos para crear bases. **El script elimina y recrea `examen_progra3`, incluidos sus datos anteriores.** Crea tres tablas e inserta cinco solicitantes, ocho equipos y cinco préstamos de prueba, con el contador de renovaciones en cero.
2. Copiar `src/main/resources/db.properties.example` como `db.properties` en la misma carpeta. Completar las tres propiedades con los datos de la instancia utilizada:

   ```properties
   db.url=jdbc:mysql://localhost:3306/examen_progra3
   db.usuario=USUARIO_LOCAL
   db.clave=CONTRASENA_LOCAL
   ```

3. Para usar la base ya preparada del profesor, configurar en `db.url` su host y el nombre `gamabasis_ex1p3g5`, junto con las credenciales asignadas al grupo. Las tablas de esa base ya incluyen `renovaciones`; el script de recreación no es un paso para volver a conectarse a ella.
4. Guardar la configuración y reiniciar la aplicación si estaba abierta. `db.properties` está excluido por `.gitignore`; las credenciales reales se conservan únicamente en el archivo local.

Los datos iniciales contienen fechas fijas. Su vencimiento depende del día de ejecución; los resultados documentados corresponden a las fechas indicadas en [pruebas.md](../docs/etapa_3/pruebas.md).

## Pasos para ejecutar

**NetBeans:**

1. Clonar o abrir el [repositorio del grupo](https://github.com/JohnPQ2312/examen1progra3) y completar la configuración anterior.
2. En **File → Open Project**, seleccionar la carpeta `proyecto`, que contiene `pom.xml`.
3. Usar un JDK 21 o superior. El proyecto compila con `release 21`, definido en el POM.
4. Ejecutar el proyecto con **Run**. Si NetBeans solicita la clase principal, seleccionar `cr.ac.una.app.App`.
5. Introducir las opciones en la pestaña **Output**. La opción 1 del menú principal comprueba la conexión con MySQL.

**Alternativa por terminal**, desde la raíz del repositorio y con Maven y JDK disponibles:

```shell
mvn -f proyecto/pom.xml compile exec:java
```

## Ejecutar las pruebas

En NetBeans, hacer clic derecho sobre el proyecto y seleccionar **Test**. Como alternativa, desde la raíz del repositorio:

```shell
mvn -f proyecto/pom.xml test
```

Las pruebas JUnit utilizan repositorios de prueba y no requieren MySQL ni `db.properties`. Cubren validaciones de registro, renovación, límites de préstamos y delegación del controlador. Las comprobaciones manuales de integración sí requieren la conexión configurada.

Última ejecución documentada: **48 pruebas JUnit aprobadas**, incluidas dos de ejemplo del profesor. El informe distingue las 46 ejecuciones funcionales automatizadas de los 19 casos manuales sobre MySQL: [casos, resultados y evidencias](../docs/etapa_3/pruebas.md).

## Video de demostración

**Pendiente de grabación y enlace.** Duración máxima de cinco minutos, con participación de los tres integrantes. Debe mostrar la preparación de la base, la ejecución, un préstamo aceptado y otro rechazado, una regla del servicio, un método JDBC y el cambio de requerimiento funcionando.

## Tecnologías utilizadas

| Tecnología | Versión |
|---|---|
| Java | Compilación con release 21; requiere JDK 21 o superior. Verificación automatizada con JDK 26.0.2. |
| Maven | 3.9.16 en la verificación automatizada. |
| MySQL | 8.0.32 en la conexión comprobada del grupo. |
| MySQL Connector/J | 9.1.0, declarado en el POM. |
| JUnit Jupiter | 5.11.3, declarado en el POM. |
| Maven Compiler Plugin | 3.13.0. |
| Maven Surefire Plugin | 3.5.2. |
| Exec Maven Plugin | 3.5.0. |

NetBeans se utiliza como IDE para abrir y ejecutar el proyecto Maven.
