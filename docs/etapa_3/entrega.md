# Etapa 3 — Integración, pruebas y mejora

## Integración

El sistema de préstamo de equipos mantiene el recorrido **Menú → Controller → Service → Repository → JDBC → MySQL**. `App` conecta las capas mediante sus constructores. Los menús de solicitantes y equipos permiten registrar, consultar, listar y actualizar; el de préstamos incorpora la renovación y la consulta de vencidos.

| Opción del menú | Funcionalidad | Estado |
|---|---|---|
| Principal, 1 | Comprobar la conexión con MySQL. | Implementada; conexión comprobada en la Etapa 2. |
| Solicitantes, 1–4 | Registrar, buscar, listar y actualizar solicitantes. | ✅ Registro y actualización comprobados contra MySQL; validaciones de registro también probadas con repositorios en memoria. |
| Equipos, 1–4 | Registrar, buscar, listar y actualizar equipos. | ✅ Registro y actualización comprobados contra MySQL; validaciones de registro también probadas con repositorios en memoria. |
| Préstamos, 1–2 | Registrar préstamos y devoluciones. | ✅ Regresión sobre MySQL: préstamo #6, rechazo por equipo prestado y devolución con consulta posterior de ambos efectos. |
| Préstamos, 3 | Consultar préstamos activos por solicitante. | ✅ Comprobada contra MySQL antes y después de renovar. |
| Préstamos, 4 | Renovar un préstamo. | ✅ Renovaciones de estudiante y funcionario y rechazos por repetición, vencimiento y devolución comprobados contra MySQL. |
| Préstamos, 5 | Listar préstamos activos vencidos. | ✅ Consulta contra MySQL con nombre, código de equipo y días de atraso. |

Las operaciones de la aplicación utilizan los repositorios JDBC; los repositorios en memoria se usan únicamente en las pruebas automatizadas. Las evidencias cubren los flujos indicados y las reglas de actualización; los límites de ambos tipos y la renovación con vencimiento hoy también se comprobaron en pruebas unitarias.

## Cambio de requerimiento

**Cambio solicitado por el docente:**

Permitir una única renovación de un préstamo activo que no esté vencido ni haya sido devuelto. Se agregan **3 días para estudiantes** o **7 para funcionarios**, contados desde la fecha esperada de devolución anterior. La fecha de vencimiento igual a hoy permite renovar; una fecha anterior a hoy lo impide. Los rechazos de estas reglas se comunican mediante `RenovacionNoPermitidaException`.

El modelo y la tabla de préstamos incorporan `renovaciones`, inicialmente en cero. Al renovar pasa a uno; el préstamo continúa activo y el equipo prestado. También se agrega una consulta de préstamos activos vencidos que muestra el nombre del solicitante, el código del equipo y los días de atraso.

**Clases modificadas o agregadas:**

| Clase | Capa | Cambio realizado |
|---|---|---|
| `Prestamo` | Modelo | Atributo `renovaciones`, parámetro del constructor y métodos de acceso. |
| `PrestamoVencido` | Modelo / DTO | Resultado de consulta con nombre del solicitante, código del equipo y días de atraso. |
| `RenovacionNoPermitidaException` | Excepciones | Informa las reglas que impiden renovar. |
| `PrestamoRepository` | Contrato de persistencia | Métodos `actualizarRenovacion` y `listarActivosVencidos`. |
| `PrestamoRepositoryJdbc` | Persistencia | Lee y guarda el contador; actualiza la fecha y consulta vencidos con JOIN, parámetros y cierre automático de recursos. |
| `PrestamoService` | Negocio | Valida la renovación, suma los días según el tipo y delega la persistencia; expone la consulta de vencidos. Los préstamos nuevos se construyen con cero renovaciones. |
| `PrestamoController` | Controlador | Delega renovación y consulta de vencidos al servicio. |
| `MenuPrestamos` | Presentación | Opciones 4 y 5, presentación de resultados y captura de la nueva excepción. |

En [schema.sql](../../database/schema.sql), `prestamo.renovaciones` es `INT NOT NULL DEFAULT 0`, con un `CHECK` que permite valores de cero a uno. Se adaptaron los datos iniciales para comprobar el cambio.

**Análisis:**

Las interfaces de repositorio permitieron mantener el SQL fuera del servicio y añadir las dos operaciones sin rehacer las capas. Las reglas se incorporaron a `PrestamoService`; el controlador delega y el menú solicita datos y presenta resultados. Fue necesario ampliar el constructor y el mapeo JDBC de `Prestamo`, el contrato de persistencia y el esquema. La conexión y la composición de dependencias en `App` se conservaron.

## Decisiones de diseño relevantes

- Guardar el contador de renovaciones en MySQL conserva la restricción entre ejecuciones de la aplicación.
- Calcular la ampliación desde la fecha esperada anterior aplica los días completos definidos por el requerimiento.
- Usar `LocalDate.now()` en el servicio y pasar la fecha de la computadora como parámetro a la consulta de vencidos mantiene el mismo criterio temporal en ambas operaciones.
- Utilizar `PrestamoVencido` evita reconstruir entidades completas para una consulta que solo necesita tres datos.
- Compartir `ValidadorDatos` entre los servicios de equipos y solicitantes centraliza campos obligatorios, longitudes y formato de correo. Las pruebas de esos servicios dependen de implementaciones en memoria de sus interfaces.

## Pruebas y mejoras

- Pruebas: [`pruebas.md`](pruebas.md). Se documentan 29 casos automatizados, que producen 46 ejecuciones, y 19 casos manuales contra MySQL; todos con resultado esperado. Los dos ejemplos del profesor no se cuentan como evidencia funcional propia.
- Mejoras y defectos corregidos: [`mejoras.md`](mejoras.md).

## UML final

El diseño cambió al incorporar el contador, el DTO, la excepción, los nuevos métodos y el validador compartido. Los archivos editables son [uml.drawio](../etapa_1/diagramas/uml.drawio) y [relacional.drawio](../etapa_1/diagramas/relacional.drawio).

Se contrastaron las seis vistas del UML editable con los 31 tipos Java del código integrado: atributos, constructores, métodos, relaciones, implementaciones de interfaces y herencia. Se incorporaron los métodos finales de `PrestamoService`, `PrestamoController` y `MenuPrestamos`, conservando el estilo y ajustando el espacio de las conexiones. El modelo relacional editable coincide con las tres tablas y 16 columnas del SQL, incluida `renovaciones`.

Se exportaron y revisaron el [UML final](../etapa_1/diagramas/uml_final.png) y el [modelo relacional](../etapa_1/diagramas/modelo_relacional.png). Se comprobó que las imágenes contienen las versiones actualizadas, con texto completo, conexiones y cardinalidades legibles. Los datos de diagrama incluidos en ambos PNG coinciden con sus correspondientes vistas editables. El UML final muestra la vista completa; el archivo `uml.drawio` conserva sus seis páginas.

## Aspectos pendientes

- Agregar en el [README del proyecto](../../proyecto/README.md) el enlace al video de máximo cinco minutos, con participación de los tres integrantes.
- Publicar la etiqueta `etapa-3` sobre la entrega final.

## Distribución del trabajo

Grupo de tres integrantes autorizado por el profesor. Los aportes de código se contrastaron con el historial del repositorio; las pruebas manuales de renovación, vencidos, regresión y actualizaciones fueron ejecutadas por John.

| Integrante | Aportes principales |
|---|---|
| John Perez Quesada | Integración y revisión de coherencia; validador compartido y ajustes en los servicios de equipos y solicitantes; pruebas JUnit con repositorios en memoria; pruebas automatizadas de renovación y límites en `PrestamoServiceTest`; ejecución de los 19 casos manuales documentados; preparación de la documentación y actualización, contraste y exportación de diagramas. |
| Joe Sandoval Diaz | Atributo `renovaciones` en el modelo, ajuste del constructor utilizado por el servicio, columna y datos iniciales del SQL; implementación y mapeo JDBC de renovación y vencidos; correcciones de la fecha de consulta y de la coma en el SELECT. Ejecución del script SQL. |
| Joshua Barrantes Granados | Excepción y DTO, ampliación de `PrestamoRepository`, reglas de renovación en `PrestamoService`, delegación en `PrestamoController` y opciones de renovación y vencidos en `MenuPrestamos`. |

Referencias del historial: Joe, [modelo](https://github.com/JohnPQ2312/examen1progra3/commit/4f27f588335e1c4dea0e080a994b4dd0466b92f7), [JDBC](https://github.com/JohnPQ2312/examen1progra3/commit/cba23c2e998a870b4c07c042b3d98315a12444cc) y [SQL](https://github.com/JohnPQ2312/examen1progra3/commit/d83b21760ec70e31a80399395d5d2d8d38183710); Joshua, [excepción y DTO](https://github.com/JohnPQ2312/examen1progra3/commit/474a392443ef80ffa93713d890ec6c4ddf207c88), [servicio](https://github.com/JohnPQ2312/examen1progra3/commit/5a40e3135526cc320a67434b5fc59b437b494291) y [menú](https://github.com/JohnPQ2312/examen1progra3/commit/d0507ea167325092bf5bb5a9df9354006b2cfe54).
