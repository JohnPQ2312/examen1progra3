# Pruebas documentadas

## Tipo de pruebas

- [x] Automatizadas con JUnit 5.
- [x] Pruebas manuales de integración con MySQL: registros, préstamo y devolución, renovación, vencidos, límite de estudiante y actualizaciones.

Clases ejecutadas:

- `ValidadorDatosTest`
- `EquipoServiceTest`
- `SolicitanteServiceTest`
- `PrestamoServiceTest`

Las pruebas automatizadas cubren registro y validación de equipos y solicitantes, reglas de préstamos y delegación del controlador; utilizan repositorios de prueba y no ejecutan consultas contra MySQL. Los casos P-18 a P-36 se ejecutaron manualmente desde el menú sobre MySQL, con los cambios locales de validación y los cambios publicados integrados hasta `d0507ea`.

## Casos de prueba

En los casos automatizados de rechazo, el resultado correcto es lanzar la excepción prevista: `ValidacionException`, `LimitePrestamosException` o `RenovacionNoPermitidaException`, según la regla. Al renovar, los identificadores inválidos o inexistentes producen `ValidacionException`; un préstamo devuelto, vencido o ya renovado produce `RenovacionNoPermitidaException`. El menú presenta el mensaje de rechazo. Que una entrada sea rechazada no significa que la prueba haya fallado.

| ID | Tipo | Descripción | Datos de entrada | Resultado esperado | Resultado obtenido | Estado |
|---|---|---|---|---|---|---|
| P-01 | Éxito | Aceptar textos en el máximo permitido. | Longitudes 20, 30, 50 y 100; cada una con su máximo correspondiente. | Aceptación sin excepción. | Se aceptaron los cuatro casos. | ✅ |
| P-02 | Error | Rechazar textos que superan su máximo. | Longitudes 21, 31, 51 y 101 frente a máximos 20, 30, 50 y 100. | `ValidacionException`. | Se rechazaron los cuatro casos. | ✅ |
| P-03 | Error | Rechazar textos sin contenido. | `null`, cadena vacía, espacio, tabulación y salto de línea. | `ValidacionException`. | Se rechazaron los cinco casos. | ✅ |
| P-04 | Éxito | Aceptar correo con formato válido. | `persona@example.com`. | Aceptación sin excepción. | Correo aceptado. | ✅ |
| P-05 | Error | Rechazar correo sin arroba. | `persona.example.com`. | `ValidacionException`. | Correo rechazado. | ✅ |
| P-06 | Éxito | Aceptar correo en su longitud máxima. | Correo de 150 caracteres construido en la prueba. | Aceptación sin excepción. | Correo aceptado. | ✅ |
| P-07 | Error | Rechazar correo demasiado largo. | Correo de 151 caracteres construido en la prueba. | `ValidacionException`. | Correo rechazado. | ✅ |
| P-08 | Éxito | Registrar equipo con campos en sus máximos. | Código de 20, descripción de 100 y tipo de 50 caracteres; estado `DISPONIBLE`. | Equipo registrado en memoria. | Un equipo registrado y recuperado por su código. | ✅ |
| P-09 | Éxito | Asignar estado inicial al registrar. | Equipo válido con estado `null`. | Registro con estado `DISPONIBLE`. | Estado asignado y un equipo registrado. | ✅ |
| P-10 | Error | Rechazar campos de equipo demasiado largos. | Código de 21, descripción de 101 o tipo de 51 caracteres, en casos separados. | `ValidacionException` sin insertar. | Los tres casos se rechazaron; repositorio vacío. | ✅ |
| P-11 | Error | Rechazar código de equipo duplicado. | Dos registros con código `EQ-TEST`. | Rechazar el segundo registro. | Excepción y un único equipo almacenado. | ✅ |
| P-12 | Éxito | Registrar solicitante con campos en sus máximos. | Identificación de 30, nombre de 100 y correo de 150 caracteres; tipo `ESTUDIANTE`. | Solicitante registrado en memoria. | Un solicitante registrado y recuperado por identificación. | ✅ |
| P-13 | Error | Rechazar identificación o nombre demasiado largos. | Identificación de 31 o nombre de 101 caracteres, en casos separados. | `ValidacionException` sin insertar. | Ambos casos rechazados; repositorio vacío. | ✅ |
| P-14 | Error | Rechazar registro con correo demasiado largo. | Solicitante con correo de 151 caracteres. | `ValidacionException` sin insertar. | Registro rechazado; repositorio vacío. | ✅ |
| P-15 | Error | Rechazar registro con correo inválido. | Correo `correo-sin-arroba`. | `ValidacionException` sin insertar. | Registro rechazado; repositorio vacío. | ✅ |
| P-16 | Error | Rechazar identificación duplicada. | Dos registros con identificación `ID-TEST`. | Rechazar el segundo registro. | Excepción y un único solicitante almacenado. | ✅ |
| P-17 | Error | Rechazar solicitante sin tipo. | Solicitante válido con tipo `null`. | `ValidacionException` sin insertar. | Registro rechazado; repositorio vacío. | ✅ |
| P-18 | Éxito | Renovar un préstamo de estudiante y volver a consultarlo. | Préstamo #4, Carlos Vega Rojas, equipo EQ-07; fecha anterior 2026-12-15. | Ampliación de 3 días: 2026-12-18. | El menú confirmó la renovación y la consulta posterior recuperó 2026-12-18. | ✅ |
| P-19 | Error | Intentar una segunda renovación. | Préstamo #4 después de P-18. | Rechazo por renovación previa. | Se informó que el préstamo ya fue renovado una vez. | ✅ |
| P-20 | Éxito | Renovar un préstamo de funcionario y volver a consultarlo. | Préstamo #5, Laura Chaves Brenes, equipo EQ-08; fecha anterior 2026-12-15. | Ampliación de 7 días: 2026-12-22. | El menú confirmó la renovación y la consulta posterior recuperó 2026-12-22. | ✅ |
| P-21 | Éxito | Consultar préstamos activos vencidos. | Opción 5; fecha de la computadora 2026-10-22. EQ-05 vence el 20 y EQ-06 el 21. | Ana Mora Solís, EQ-05 con 2 días y EQ-06 con 1 día de atraso. | Se mostraron las dos filas con esos datos. | ✅ |
| P-22 | Error | Renovar un préstamo vencido. | Préstamo #1, fecha esperada 2026-10-20, consultado el 2026-10-22. | Rechazo por vencimiento. | Se informó que está vencido desde 2026-10-20 y no puede renovarse. | ✅ |
| P-23 | Error | Renovar un préstamo devuelto. | Préstamo #3, estado DEVUELTO. | Rechazo por devolución previa. | Se informó que ya fue devuelto y no puede renovarse. | ✅ |
| P-24 | Éxito | Registrar préstamo y recuperar el estado del equipo. | Daniela, 3-3333-3333; EQ-01 disponible; devolución 2026-10-29. | Préstamo activo y equipo PRESTADO. | Préstamo #6 registrado el 2026-10-22; consulta de EQ-01 confirmó PRESTADO. | ✅ |
| P-25 | Error | Intentar prestar de nuevo un equipo prestado. | Mismos solicitante, EQ-01 y fecha de P-24. | Rechazo sin otro préstamo activo. | Rechazo por PRESTADO; consulta posterior mostró únicamente #6. | ✅ |
| P-26 | Éxito | Devolver el préstamo y consultar ambos efectos. | Préstamo #6. | Devolución registrada; equipo DISPONIBLE y Daniela sin activos. | Fecha real 2026-10-22; consultas confirmaron ambos efectos. | ✅ |
| P-27 | Error | Superar el límite de un estudiante. | Ana, 1-1111-1111, con dos activos; EQ-01 disponible; devolución 2026-10-29. | Rechazo por máximo de dos, sin cambiar el equipo. | Rechazo por límite de ESTUDIANTE; EQ-01 siguió DISPONIBLE. | ✅ |
| P-28 | Error | Renovar con identificador inválido desde el menú. | Número de préstamo 0. | Rechazo por número inválido. | El numero de prestamo no es valido. | ✅ |
| P-29 | Error | Renovar un préstamo inexistente desde el menú. | Número 999999. | Rechazo por inexistencia. | No existe un prestamo con numero 999999. | ✅ |
| P-30 | Éxito | Registrar el solicitante para las pruebas de actualización. | EV3-1022, Prueba Etapa 3, prueba.ev3.1022@example.com, ESTUDIANTE. | Registro guardado. | Registro confirmado; al abrir la actualización se recuperaron esos datos. | ✅ |
| P-31 | Éxito | Actualizar nombre, correo y tipo del solicitante. | EV3-1022; Prueba Etapa 3 Actualizada; prueba.ev3.1022.actualizada@example.com; FUNCIONARIO. | Guardar los tres cambios. | Consulta por identificación recuperó el nombre, correo y tipo nuevos. | ✅ |
| P-32 | Error | Actualizar solicitante con correo inválido. | EV3-1022; correo-sin-arroba; conservar nombre y tipo. | Rechazo y conservación de los datos previos. | Rechazo por formato; consulta posterior conservó el correo válido, nombre y FUNCIONARIO. | ✅ |
| P-33 | Éxito | Registrar equipo para las pruebas de actualización. | EV3-EQ-1022; Laptop de prueba Etapa 3; Laptop. | Registro DISPONIBLE. | Registro confirmado; la actualización recuperó esos datos y estado. | ✅ |
| P-34 | Éxito | Actualizar descripción, tipo y estado del equipo. | EV3-EQ-1022; Equipo actualizado Etapa 3; Portatil; EN_REPARACION. | Guardar los tres cambios. | Consulta por código recuperó los tres valores nuevos. | ✅ |
| P-35 | Error | Prestar un equipo en reparación. | Solicitante EV3-1022; EV3-EQ-1022; devolución 2026-10-29. | Rechazo por EN_REPARACION. | El menú rechazó el préstamo por el estado del equipo. | ✅ |
| P-36 | Error | Cambiar a DISPONIBLE un equipo con préstamo activo. | EQ-05, asociado al préstamo #1; conservar descripción y tipo. | Rechazo y conservación de PRESTADO. | Rechazo: registre primero la devolución; consulta posterior confirmó PRESTADO. | ✅ |
| P-37 | Éxito | Renovación de estudiante desde la fecha esperada. | Vencimiento a diez días de hoy, renovaciones 0. | Sumar tres días y conservar ACTIVO y PRESTADO. | Fecha ampliada, contador 1, fecha real nula y una llamada de persistencia. | ✅ |
| P-38 | Éxito | Renovación de funcionario desde la fecha esperada. | Vencimiento a diez días de hoy, renovaciones 0. | Sumar siete días y conservar los estados. | Fecha ampliada, contador 1 y una llamada de persistencia. | ✅ |
| P-39 | Éxito | Renovar cuando el vencimiento es hoy. | Estudiante, préstamo ACTIVO sin renovaciones, vencimiento LocalDate.now(). | Permitir renovación hasta hoy más tres días. | Renovación aceptada; contador 1 y estados conservados. | ✅ |
| P-40 | Error | Renovar un préstamo vencido desde ayer. | Préstamo ACTIVO, fecha de ayer. | RenovacionNoPermitidaException sin actualizar. | Excepción esperada, fecha y contador intactos, cero actualizaciones. | ✅ |
| P-41 | Error | Renovar un préstamo DEVUELTO. | Estado DEVUELTO y fecha real de devolución. | RenovacionNoPermitidaException sin actualizar. | Excepción esperada y cero actualizaciones. | ✅ |
| P-42 | Error | Repetir una renovación en el servicio. | Renovar una vez y volver a renovar el mismo préstamo. | Rechazar el segundo intento, sin segunda ampliación. | Fecha conservada, contador 1 y una única actualización. | ✅ |
| P-43 | Error | Validar identificadores de renovación en el servicio. | null, 0 y -1, en tres ejecuciones. | ValidacionException sin actualizar. | Los tres valores se rechazaron; cero actualizaciones. | ✅ |
| P-44 | Error | Renovar un préstamo inexistente en el servicio. | Identificador 999, repositorio sin ese préstamo. | ValidacionException sin actualizar. | Excepción esperada y cero actualizaciones. | ✅ |
| P-45 | Éxito | Delegar la renovación desde el controlador. | Controlador conectado al servicio con préstamo válido de estudiante. | Aplicar la regla y guardar una vez. | Fecha ampliada tres días y una actualización. | ✅ |
| P-46 | Éxito | Delegar la consulta de vencidos desde el controlador. | Repositorio de prueba con un DTO de vencido. | Devolver la lista suministrada por el repositorio. | El controlador recuperó la misma lista a través del servicio. | ✅ |
| P-47 | Éxito | Registrar el último préstamo permitido para cada tipo. | ESTUDIANTE con 1 activo; FUNCIONARIO con 3, en dos ejecuciones. | Aceptar el segundo o cuarto préstamo. | Una inserción, préstamo ACTIVO, equipo PRESTADO y renovaciones 0 en ambos casos. | ✅ |
| P-48 | Error | Superar el máximo de activos de cada tipo. | ESTUDIANTE con 2 activos; FUNCIONARIO con 4, en dos ejecuciones. | LimitePrestamosException sin insertar ni cambiar equipo. | Ambos rechazados; cero inserciones y equipo DISPONIBLE. | ✅ |

## Evidencias

### Ejecución inicial de pruebas automatizadas, 21 de octubre

Ejecución conjunta desde NetBeans mediante Maven Surefire y JUnit Platform, realizada el 21 de octubre de 2026 a las 20:21, hora de Costa Rica.

Extracto de la salida:

```text
Running cr.ac.una.EjemploPruebaTest
Tests run: 2, Failures: 0, Errors: 0, Skipped: 0

Running cr.ac.una.service.EquipoServiceTest
Tests run: 6, Failures: 0, Errors: 0, Skipped: 0

Running cr.ac.una.service.SolicitanteServiceTest
Tests run: 7, Failures: 0, Errors: 0, Skipped: 0

Running cr.ac.una.service.ValidadorDatosTest
Tests run: 17, Failures: 0, Errors: 0, Skipped: 0

Results:
Tests run: 32, Failures: 0, Errors: 0, Skipped: 0

BUILD SUCCESS
```

### Pruebas manuales con MySQL

John ejecutó estos casos durante la sesión del 21–22 de octubre de 2026, hora de Costa Rica. Los bloques siguientes son extractos de las salidas compartidas; las imágenes enlazadas son capturas originales. Las fechas iniciales corresponden a los datos de prueba preparados por Joe.

**P-18. Estudiante: renovación y consulta posterior.** La [captura previa](evidencias/p18-estudiante-antes.png) muestra el préstamo #4 con fecha 2026-12-15.

```text
Opcion: 4
Numero de prestamo a renovar: 4
  Prestamo #4 renovado. Nueva fecha de devolucion: 2026-12-18.

Opcion: 3
Identificacion del solicitante: 2-2222-2222
  #4    | EQ-07    Parlante JBL Charge 5        | prestado 2026-10-15 | devolver 2026-12-18
```

**P-19. Segunda renovación rechazada.** [Captura del resultado](evidencias/p19-segunda-renovacion-rechazada.png).

```text
Opcion: 4
Numero de prestamo a renovar: 4
  Rechazado: El prestamo 4 ya fue renovado una vez; solo se permite una renovacion.
```

**P-20. Funcionario: renovación y consulta posterior.** [Consulta previa](evidencias/p20-funcionario-antes.png) y [consulta posterior](evidencias/p20-funcionario-despues.png).

```text
Opcion: 4
Numero de prestamo a renovar: 5
  Prestamo #5 renovado. Nueva fecha de devolucion: 2026-12-22.

Opcion: 3
Identificacion del solicitante: 5-5555-5555
  #5    | EQ-08    Proyector BenQ MS560         | prestado 2026-10-15 | devolver 2026-12-22
```

Las consultas posteriores de P-18 y P-20 recuperaron las nuevas fechas desde MySQL. El rechazo de P-19 evidencia que la aplicación reconoce la renovación previa; no se incluye una inspección directa del contador en SQL.

**P-21. Vencidos al 22 de octubre de 2026.** [Captura del listado](evidencias/p21-prestamos-vencidos.png).

```text
Opcion: 5
  Ana Mora Solís                | equipo EQ-05 | 2 día(s) de atraso
  Ana Mora Solís                | equipo EQ-06 | 1 día(s) de atraso
```

La consulta de activos de `1-1111-1111` confirmó que el préstamo #1 vence el 2026-10-20 y el #2 el 2026-10-21, lo que corresponde a dos y un día de atraso respectivamente.

**P-22. Renovación de préstamo vencido.** Salida compartida en texto:

```text
Opcion: 4
Numero de prestamo a renovar: 1
  Rechazado: El prestamo 1 esta vencido desde el 2026-10-20; no se puede renovar.
```

**P-23. Renovación de préstamo devuelto.** Salida compartida en texto:

```text
Opcion: 4
Numero de prestamo a renovar: 3
  Rechazado: El prestamo 3 ya fue devuelto; no se puede renovar.
```

Las renovaciones modificaron los préstamos #4 y #5. Repetir las mismas renovaciones exitosas sobre esos registros producirá un rechazo. Los días de atraso también cambian con la fecha de ejecución; los resultados anteriores corresponden a la fecha documentada.

### Regresión y actualizaciones con MySQL, 22 de octubre

John ejecutó P-24 a P-36 desde los menús de la versión integrada y compartió sus salidas en texto. Los extractos se conservaron en [regresion-2026-10-22.txt](evidencias/regresion-2026-10-22.txt). Las consultas posteriores verificaron el estado del equipo, los préstamos activos y los datos actualizados, incluidos los intentos rechazados.

El préstamo #6 quedó devuelto, `EQ-01` disponible y Daniela sin préstamos activos. El solicitante `EV3-1022` quedó como funcionario con nombre y correo actualizados; el equipo `EV3-EQ-1022` quedó en reparación. Los registros #4 y #5 corresponden a las renovaciones anteriores, no al préstamo #6 de esta regresión.

### Ampliación de las pruebas automatizadas, 22 de octubre

Se agregó `PrestamoServiceTest`, con 16 ejecuciones para P-37 a P-48. El contador de activos se controla en el repositorio de prueba para verificar el último préstamo permitido y el rechazo al alcanzar el máximo de ambos tipos. Las pruebas de renovación comprueban fecha, contador y llamadas de persistencia; el vencimiento igual a hoy se construye en memoria.

La suite completa se ejecutó en una copia aislada del código y las pruebas actuales, sin recursos de conexión ni acceso a MySQL. [Resumen obtenido de los informes de Surefire](evidencias/junit-2026-10-22.txt):

```text
EquipoServiceTest: 6 aprobadas
SolicitanteServiceTest: 7 aprobadas
ValidadorDatosTest: 17 aprobadas
PrestamoServiceTest: 16 aprobadas
EjemploPruebaTest: 2 aprobadas
Tests run: 48, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

## Resumen

| Modalidad | Casos documentados | Ejecuciones | Aprobadas | Fallidas |
|---|---:|---:|---:|---:|
| JUnit 5, P-01 a P-17 y P-37 a P-48 | 29 | 46 | 46 | 0 |
| Manual con MySQL, P-18 a P-36 | 19 | 19 | 19 | 0 |
| Total | 48 | 65 | 65 | 0 |

Se documentan 21 casos de aceptación y 27 de rechazo esperado. La parametrización produce varias ejecuciones por caso. Las dos pruebas de ejemplo del profesor están en la salida completa, pero se excluyen de los totales funcionales de esta tabla. Un rechazo esperado cuenta como prueba aprobada.

## Alcance de la comprobación

Se completaron los puntos de cobertura que estaban pendientes: regresión de préstamo y devolución sobre MySQL, actualizaciones válidas y rechazadas, límites de ambos tipos, renovación con vencimiento hoy e identificadores inválidos o inexistentes. Los límites de funcionario y el vencimiento hoy se comprobaron con repositorios de prueba; no se presentan como ejecuciones sobre MySQL. Estos resultados no certifican todos los caminos posibles, fallos de conexión ni acceso simultáneo de varios usuarios.
