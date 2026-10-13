# Etapa 2 — Implementación funcional y persistencia

## Identificación

```text
Caso asignado: Caso 1. Préstamo de equipos tecnológicos
Integrante 1: John Perez Quesada
Integrante 2: Joe Sandoval Diaz
Integrante 3: Joshua Barrantes Granados
URL del repositorio: https://github.com/JohnPQ2312/examen1progra3
```

Grupo de tres integrantes autorizado por el profesor. Esta entrega continúa el [análisis y diseño de la Etapa 1](../etapa_1/entrega.md).

## Cambios al diseño

Se implementaron las clases JDBC previstas, las validaciones de los servicios y los menús `MenuSolicitantes`, `MenuEquipos` y `MenuPrestamos`. `App.main` conecta los repositorios, servicios, controladores y menús mediante sus constructores.

Las operaciones del caso siguen el recorrido **Menú → Controller → Service → interfaz Repository → JDBC → MySQL**. Los servicios reciben interfaces de repositorio por constructor y no incluyen SQL ni entrada/salida de consola. El registro de préstamos y las devoluciones coordinan las escrituras de préstamo y equipo en una misma transacción.

Se actualizó el [UML editable](../etapa_1/diagramas/uml.drawio) y su [PNG](../etapa_1/diagramas/uml.png) para representar las clases implementadas y sus conexiones. El [modelo relacional](../etapa_1/diagramas/relacional.drawio) y su [PNG](../etapa_1/diagramas/modelo_relacional.png) conservan la estructura de las tablas `solicitante`, `equipo` y `prestamo`. Las relaciones permiten múltiples préstamos históricos; los límites por solicitante se aplican solo a los activos.

El [script SQL](../../database/schema.sql) incluye la creación desde cero, llaves, restricciones y datos de prueba: cinco solicitantes, ocho equipos y tres préstamos.

## Configuración y ejecución

**Requisitos:** JDK 21 o superior, Maven y acceso a MySQL. MySQL Connector/J 9.1.0 está declarado en `proyecto/pom.xml`.

1. **Preparar la base.** Para instalar desde cero, importar `database/schema.sql` en MySQL con permisos de creación de bases. El script elimina y recrea `examen_progra3`, borrando sus datos anteriores. Para el servidor del profesor se utiliza `gamabasis_ex1p3g5`, cuyas tablas ya habían creado los compañeros; no se ejecutó de nuevo el script durante las pruebas documentadas.
2. **Configurar la conexión.** Copiar [db.properties.example](../../proyecto/src/main/resources/db.properties.example) como `db.properties` en la misma carpeta. Para el servidor utilizado:

   ```properties
   db.url=jdbc:mysql://mysql.us.cloudlogin.co:3306/gamabasis_ex1p3g5
   db.usuario=gamabasis_ex1p3g5
   db.clave=REEMPLAZAR_CON_LA_CONTRASENA_LOCAL
   ```

   Sustituir el texto de la contraseña solo en el archivo local, excluido por `.gitignore`. Para una instalación local con el script, usar `jdbc:mysql://localhost:3306/examen_progra3` y las credenciales de esa instancia. Reiniciar el programa si se cambia la configuración.
3. **Compilar y ejecutar en NetBeans (opción principal).** Seleccionar **File → Open Project** y abrir la carpeta `proyecto`, que contiene `pom.xml`. NetBeans reconoce el proyecto Maven y gestiona sus dependencias. En **Properties → Build → Compile**, seleccionar un JDK 21 o superior y conservar **Target Release: 21**. Hacer clic derecho sobre **Examen Práctico Integrador - Programación III** y seleccionar **Run**. Si solicita la clase principal, elegir `cr.ac.una.app.App`; también puede indicarse en **Properties → Run → Main Class**. El menú aparece en la pestaña **Output**, donde se introducen las opciones y los datos de consola.

   **Alternativa desde una terminal:** con Maven disponible, ejecutar desde la raíz del repositorio:

   ```shell
   mvn -f proyecto/pom.xml compile exec:java
   ```

   Ambas opciones ejecutan la misma aplicación y requieren el archivo local `db.properties` configurado en el paso anterior.

| Menú | Opciones |
|---|---|
| Principal | 1: probar conexión; 2: solicitantes; 3: equipos; 4: préstamos; 0: salir. |
| Solicitantes | 1: registrar; 2: consultar por identificación; 3: listar; 4: actualizar; 0: volver. |
| Equipos | 1: registrar; 2: consultar por código; 3: listar; 4: actualizar; 0: volver. |
| Préstamos | 1: registrar préstamo; 2: registrar devolución; 3: consultar préstamos activos; 0: volver. |

Las fechas se ingresan como `AAAA-MM-DD`. En las actualizaciones, Enter conserva los valores que el menú así indique. El registro de equipos asigna `DISPONIBLE`; el cambio a `PRESTADO` corresponde al registro de un préstamo.

## Operaciones implementadas

| Operación | Tipo | Controller | Service | Repository |
|---|---|---|---|---|
| RF-01. Registrar solicitante. | Registro | `SolicitanteController.registrar` | `SolicitanteService.registrar` | `SolicitanteRepositoryJdbc.insertar` |
| RF-02. Consultar solicitante por identificación. | Consulta | `SolicitanteController.buscarPorId` | `SolicitanteService.buscarPorId` | `SolicitanteRepositoryJdbc.buscarPorId` |
| RF-03. Listar solicitantes. | Consulta | `SolicitanteController.listar` | `SolicitanteService.listar` | `SolicitanteRepositoryJdbc.listar` |
| RF-04. Actualizar nombre, correo y tipo del solicitante. | Actualización | `SolicitanteController.actualizar` | `SolicitanteService.actualizar` | `SolicitanteRepositoryJdbc.actualizar` |
| RF-05. Registrar equipo. | Registro | `EquipoController.registrar` | `EquipoService.registrar` | `EquipoRepositoryJdbc.insertar` |
| RF-06. Consultar equipo por código. | Consulta | `EquipoController.buscarPorId` | `EquipoService.buscarPorId` | `EquipoRepositoryJdbc.buscarPorId` |
| RF-07. Listar equipos. | Consulta | `EquipoController.listar` | `EquipoService.listar` | `EquipoRepositoryJdbc.listar` |
| RF-08. Actualizar descripción, tipo y estado del equipo. | Actualización | `EquipoController.actualizar` | `EquipoService.actualizar` | `EquipoRepositoryJdbc.actualizar`; `PrestamoRepositoryJdbc.existeActivoPorEquipo` para comprobar coherencia. |
| RF-09. Registrar préstamo y cambiar el estado del equipo. | Negocio | `PrestamoController.registrarPrestamo` | `PrestamoService.registrarPrestamo` | `PrestamoRepositoryJdbc.insertarConEstadoEquipo`, precedido por consultas de existencia y conteo. |
| RF-10. Consultar préstamos activos de un solicitante mediante filtro y JOIN. | Consulta | `PrestamoController.listarActivosPorSolicitante` | `PrestamoService.listarActivosPorSolicitante` | `PrestamoRepositoryJdbc.listarActivosPorSolicitante` |
| RF-11. Registrar devolución y liberar el equipo. | Negocio / actualización | `PrestamoController.registrarDevolucion` | `PrestamoService.registrarDevolucion` | `PrestamoRepositoryJdbc.buscarPorId` y `actualizarConEstadoEquipo` |

## Reglas de negocio implementadas

Se conservan los identificadores definidos en la Etapa 1.

| ID | Regla | Clase / método del Service | Excepción que se lanza |
|---|---|---|---|
| RN-01 | Solo se presta un equipo `DISPONIBLE`. | `PrestamoService.registrarPrestamo` | `EquipoNoDisponibleException` |
| RN-02 | Máximo de 2 préstamos activos para estudiantes y 4 para funcionarios. | `PrestamoService.registrarPrestamo` | `LimitePrestamosException` |
| RN-03 | La devolución registra la fecha real, cambia el préstamo a `DEVUELTO` y el equipo a `DISPONIBLE`; ambas escrituras se confirman juntas. | `PrestamoService.registrarDevolucion`, con persistencia en `PrestamoRepositoryJdbc.actualizarConEstadoEquipo` | `DatosException` desde el repositorio ante un fallo SQL. |
| RN-04 | No se permiten identificaciones de solicitantes ni códigos de equipos duplicados. | `SolicitanteService.registrar`; `EquipoService.registrar` | `ValidacionException` en la comprobación previa. |
| RN-05 | El solicitante y el equipo deben existir antes del préstamo. | `PrestamoService.registrarPrestamo` | `ValidacionException` |
| RN-06 | La fecha esperada de devolución debe ser posterior a la fecha actual. | `PrestamoService.registrarPrestamo` | `ValidacionException` |
| RN-07 | El préstamo se registra `ACTIVO` y el equipo pasa a `PRESTADO` en una misma transacción. | `PrestamoService.registrarPrestamo`, con persistencia en `PrestamoRepositoryJdbc.insertarConEstadoEquipo` | `DatosException` desde el repositorio ante un fallo SQL. |
| RN-08 | Solo puede devolverse un préstamo existente y `ACTIVO`; no se devuelve dos veces. | `PrestamoService.registrarDevolucion` | `ValidacionException` |

También se validan campos obligatorios, formato del correo e identificadores de préstamo positivos. No se permite convertir en estudiante a un solicitante con más de dos préstamos activos, ni modificar el estado de un equipo de forma incompatible con sus préstamos activos.

El script refuerza la integridad con `PRIMARY KEY`, `FOREIGN KEY`, `UNIQUE`, `NOT NULL` y `CHECK`. La unicidad del correo se comprueba en SQL y los conflictos se informan como `DatosException`.

## JDBC

Fragmento de [SolicitanteRepositoryJdbc.java](../../proyecto/src/main/java/cr/ac/una/repository/SolicitanteRepositoryJdbc.java):

```java
    @Override
    public Optional<Solicitante> buscarPorId(String identificacion) {
        String sql = "SELECT identificacion, nombre, correo, tipo "
                + "FROM solicitante WHERE identificacion = ?";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, identificacion);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DatosException("Error al buscar el solicitante " + identificacion, e);
        }
    }
```

La identificación se asigna mediante `setString`, sin concatenarla al SQL. `try-with-resources` cierra los recursos y `mapear` convierte la fila en un objeto del modelo. Si no hay coincidencia, se devuelve `Optional.empty()`; las `SQLException` se envuelven en `DatosException` conservando la causa.

**Consulta con filtro y JOIN:** `PrestamoRepositoryJdbc.listarActivosPorSolicitante` une `prestamo`, `solicitante` y `equipo`, filtra por identificación mediante `?` y por estado `ACTIVO`, y ordena por fecha de préstamo. El mapeo construye el préstamo y sus objetos asociados, convirtiendo las fechas a `LocalDate`.

**Transacciones:** `insertarConEstadoEquipo` inserta el préstamo y actualiza el equipo; `actualizarConEstadoEquipo` actualiza la devolución y el equipo. Cada método usa una misma conexión con `setAutoCommit(false)`, confirma con `commit` e intenta `rollback` ante una `SQLException`. El registro recupera el identificador generado. Esto permite coordinar las dos escrituras de cada operación.

## Service

Operación principal de [PrestamoService.java](../../proyecto/src/main/java/cr/ac/una/service/PrestamoService.java):

```java
    public Prestamo registrarPrestamo(
            String identificacion,
            String codigoEquipo,
            LocalDate fechaDevolucionEsperada) {

        validarTexto(identificacion, "La identificación del solicitante es obligatoria.");
        validarTexto(codigoEquipo, "El codigo del equipo es obligatorio.");
        if (fechaDevolucionEsperada == null) {
            throw new ValidacionException("La fecha esperada de devolución es obligatoria.");
        }

        LocalDate hoy = LocalDate.now();
        if (!fechaDevolucionEsperada.isAfter(hoy)) {
            throw new ValidacionException(
                    "La fecha esperada de devolucion debe ser posterior a hoy (" + hoy + ").");
        }

        Solicitante solicitante = solicitanteRepository.buscarPorId(identificacion)
                .orElseThrow(() -> new ValidacionException(
                        "No existe un solicitante con identificacion " + identificacion + "."));
        Equipo equipo = equipoRepository.buscarPorId(codigoEquipo)
                .orElseThrow(() -> new ValidacionException(
                        "No existe un equipo con codigo " + codigoEquipo + "."));

        if (equipo.getEstado() != EstadoEquipo.DISPONIBLE) {
            throw new EquipoNoDisponibleException(
                    "El equipo " + codigoEquipo + " no está disponible (estado: "
                    + equipo.getEstado() + ").");
        }

        int limite = solicitante.getTipo() == TipoSolicitante.ESTUDIANTE
                ? LIMITE_ESTUDIANTE
                : LIMITE_FUNCIONARIO;
        int activos = prestamoRepository.contarActivosPorSolicitante(identificacion);
        if (activos >= limite) {
            throw new LimitePrestamosException(
                    solicitante.getNombre() + " ya tiene " + activos
                    + " prestamos activos; el maximo para " + solicitante.getTipo()
                    + " es " + limite + ".");
        }

        equipo.setEstado(EstadoEquipo.PRESTADO);
        Prestamo prestamo = new Prestamo(
                null, solicitante, equipo, hoy, fechaDevolucionEsperada,
                null, EstadoPrestamo.ACTIVO);
        return prestamoRepository.insertarConEstadoEquipo(prestamo);
    }
```

`validarTexto` rechaza valores nulos o vacíos. Las constantes `LIMITE_ESTUDIANTE` y `LIMITE_FUNCIONARIO` valen 2 y 4. El servicio prepara los cambios únicamente después de validar y comprobar existencia, disponibilidad y límite de préstamos.

Para la devolución, `registrarDevolucion` exige un préstamo existente y activo, registra la fecha real actual, cambia el préstamo a `DEVUELTO` y el equipo a `DISPONIBLE`, y delega la persistencia conjunta en `actualizarConEstadoEquipo`.

## Excepciones

| Excepción | Uso |
|---|---|
| `ValidacionException` | Entradas inválidas, duplicados comprobados, registros inexistentes y devolución repetida. |
| `EquipoNoDisponibleException` | Intento de prestar un equipo no disponible. |
| `LimitePrestamosException` | Registro de préstamo que excede el límite de activos. |
| `DatosException` | Fallo de persistencia, conservando la causa SQL. |

Clase de [DatosException.java](../../proyecto/src/main/java/cr/ac/una/exception/DatosException.java):

```java
public class DatosException extends RuntimeException {

    public DatosException(String mensaje) {
        super(mensaje);
    }

    public DatosException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
```

Captura dentro del ciclo de `mostrar()` en [MenuPrestamos.java](../../proyecto/src/main/java/cr/ac/una/app/MenuPrestamos.java):

```java
            } catch (ValidacionException | EquipoNoDisponibleException
                     | LimitePrestamosException e) {
                System.out.println("  Rechazado: " + e.getMessage());
            } catch (DatosException e) {
                System.out.println("  Error de base de datos: " + e.getMessage()
                        + (e.getCause() != null ? " (" + e.getCause().getMessage().lines().findFirst().orElse("") + ")" : ""));
            }
```

Los menús presentan el mensaje y permiten elegir otra opción después del rechazo o error. La consola vuelve a solicitar las entradas numéricas y fechas cuando no puede convertirlas.

## Evidencias de ejecución

Salidas reales compartidas por John al ejecutar la aplicación sobre `gamabasis_ex1p3g5`. La prueba del préstamo #5 se realizó el 13 de octubre de 2026.

### Conexión

```text
Conexión exitosa: MySQL 8.0.32 ? base de datos: gamabasis_ex1p3g5
```

### Registro y consulta de solicitante

```text
Opcion: 1
Identificacion: EV2-1013-X7
Nombre: Evidencia Etapa 2 X7
Correo: evidencia.ev2.x7@example.com
Tipo (1 = ESTUDIANTE, 2 = FUNCIONARIO): 1
  Solicitante registrado.
```

```text
Opcion: 2
Identificacion: EV2-1013-X7
  EV2-1013-X7  | Evidencia Etapa 2 X7      | evidencia.ev2.x7@example.com   | ESTUDIANTE
```

### Listado

```text
Opcion: 3
  1-1111-1111  | Ana Mora Solís            | ana.mora@est.una.ac.cr         | ESTUDIANTE
  2-2222-2222  | Carlos Vega Rojas         | carlos.vega@est.una.ac.cr      | ESTUDIANTE
  3-3333-3333  | Daniela Ruiz Castro       | daniela.ruiz@est.una.ac.cr     | ESTUDIANTE
  5-5555-5555  | Laura Chaves Brenes       | laura.chaves@una.ac.cr         | FUNCIONARIO
  4-4444-4444  | Mario Jiménez Alfaro      | mario.jimenez@una.ac.cr        | FUNCIONARIO
```

### Actualización y consulta posterior

Se actualizó el nombre del solicitante `PRUEBA-G5-001`, registrado previamente, conservando su correo y tipo:

```text
Opcion: 4
Identificación del solicitante a actualizar: PRUEBA-G5-001
  PRUEBA-G5-001 | Prueba Grupo 5            | prueba.g5@example.com          | ESTUDIANTE
  (Presione Enter para conservar el valor actual)
Nuevo nombre: Prueba Grupo 5 Actualizada
Nuevo correo:
¿Cambiar tipo? (s/n): n
  Solicitante actualizado.
```

```text
Opcion: 2
Identificacion: PRUEBA-G5-001
  PRUEBA-G5-001 | Prueba Grupo 5 Actualizada | prueba.g5@example.com          | ESTUDIANTE
```

### Registro y consulta de equipo

```text
Opcion: 1
Codigo: EV2-EQ-1013-X7
Descripcion: Laptop de evidencia Etapa 2 X7
Tipo (Laptop, Proyector, ...): Laptop
  Equipo registrado como disponible.
```

```text
Opcion: 2
Codigo: EV2-EQ-1013-X7
  EV2-EQ-1013-X7 | Laptop de evidencia Etapa 2 X7 | Laptop     | DISPONIBLE
```

### Préstamo exitoso y consulta con JOIN

```text
Opcion: 1
Identificación del solicitante: EV2-1013-X7
Codigo del equipo: EV2-EQ-1013-X7
Fecha esperada de devolución (AAAA-MM-DD): 2026-10-20
  Prestamo #5 registrado. El equipo EV2-EQ-1013-X7 quedo PRESTADO.
```

```text
Opcion: 3
Identificacion del solicitante: EV2-1013-X7
  #5    | EV2-EQ-1013-X7 Laptop de evidencia Etapa 2 X7 | prestado 2026-10-13 | devolver 2026-10-20
```

### Rechazo por RN-01 y consulta posterior

Se intentó prestar nuevamente el mismo equipo:

```text
Opcion: 1
Identificación del solicitante: EV2-1013-X7
Codigo del equipo: EV2-EQ-1013-X7
Fecha esperada de devolución (AAAA-MM-DD): 2026-10-20
  Rechazado: El equipo EV2-EQ-1013-X7 no está disponible (estado: PRESTADO).
```

La consulta posterior siguió mostrando únicamente el préstamo #5:

```text
Opcion: 3
Identificacion del solicitante: EV2-1013-X7
  #5    | EV2-EQ-1013-X7 Laptop de evidencia Etapa 2 X7 | prestado 2026-10-13 | devolver 2026-10-20
```

### Devolución y comprobación del resultado

```text
Opcion: 2
Numero de prestamo: 5
  Devolucion registrada el 2026-10-13. El equipo EV2-EQ-1013-X7 quedo DISPONIBLE.
```

```text
Opcion: 2
Codigo: EV2-EQ-1013-X7
  EV2-EQ-1013-X7 | Laptop de evidencia Etapa 2 X7 | Laptop     | DISPONIBLE
```

```text
Opcion: 3
Identificacion del solicitante: EV2-1013-X7
  El solicitante no tiene prestamos activos.
```

El equipo terminó `DISPONIBLE` y el solicitante sin préstamos activos. Estas salidas demuestran los casos descritos; no certifican una prueba de todas las reglas ni del rollback por fallo SQL.

## Distribución del trabajo

Los aportes de código se contrastaron con los commits publicados. La cuenta `andreybarra38-netizen` corresponde a Joshua.

| Integrante | Aportes principales |
|---|---|
| John Perez Quesada | Revisión e integración local; corrección de la preparación del SQL desde cero; actualización del UML; configuración y ejecución de las pruebas documentadas; preparación de la entrega y revisión de coherencia con la Etapa 1, con asistencia de IA. |
| Joe Sandoval Diaz | SQL con datos de prueba; repositorios JDBC de equipos y solicitantes; estructura inicial del repositorio de préstamos; integración de menús en `App` y pruebas del programa. |
| Joshua Barrantes Granados | Repositorio JDBC de préstamos, JOIN y transacciones; servicios y validaciones; creación e implementación de los tres menús y ajustes relacionados. |
