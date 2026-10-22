# Informe de mejoras

## Mejora de calidad implementada

**Tipo de mejora:** reutilización y centralización de validaciones.

**Problema identificado:**

`EquipoService` y `SolicitanteService` realizaban comprobaciones similares de campos obligatorios. Además, sus validaciones no comprobaban todas las longitudes máximas definidas en el SQL.

Se creó `ValidadorDatos` para compartir la validación de textos obligatorios, longitudes máximas y formato del correo. Los servicios conservan sus comprobaciones específicas de negocio.

**Antes:**

```java
if (e.getCodigo() == null || e.getCodigo().isBlank()) {
    throw new ValidacionException(
            "El codigo del equipo es obligatorio.");
}
```

La comprobación exigía contenido, pero no verificaba el máximo de 20 caracteres.

**Después:**

```java
ValidadorDatos.textoObligatorio(
        equipo.getCodigo(), "código del equipo", 20);
```

Para el correo:

```java
ValidadorDatos.correo(solicitante.getCorreo());
```

**Beneficio obtenido:**

- Las comprobaciones comunes se mantienen en una clase reutilizada por ambos servicios.
- Registro y actualización llaman a sus respectivos métodos privados `validar`, que utilizan el validador compartido.
- Los campos demasiado largos se rechazan mediante `ValidacionException` antes de enviarlos al repositorio.
- El patrón del correo queda definido en un solo lugar.

**Verificación realizada:**

Las pruebas del validador y del registro de equipos y solicitantes completaron 30 ejecuciones satisfactorias. La ampliación con `PrestamoServiceTest` agregó 16 ejecuciones aprobadas de renovación, límites y delegación; la actualización de solicitantes y equipos también se comprobó desde los menús sobre MySQL. Los casos y la evidencia están documentados en [pruebas.md](pruebas.md).

**Commit:** [02bfa93](https://github.com/JohnPQ2312/examen1progra3/commit/02bfa93bb58a56ff2bb1a27358fa31d2aa4a9bb4).

## Defectos encontrados y corregidos

| ID | Defecto | Cómo se detectó | Corrección | Commit |
|---|---|---|---|---|
| D-01 | Faltaban comprobaciones de longitud en los campos de solicitantes y equipos. | Revisión de las validaciones frente a los tamaños del SQL. Después de corregirlo, se probaron los máximos permitidos y los valores que los superan. | Incorporación de los límites en los servicios mediante `ValidadorDatos`, con pruebas automatizadas. | [02bfa93](https://github.com/JohnPQ2312/examen1progra3/commit/02bfa93bb58a56ff2bb1a27358fa31d2aa4a9bb4) |
| D-02 | Faltaba una coma después de `p.renovaciones` en el SELECT base de préstamos. | Revisión de la consulta concatenada durante la integración; la falta de separación hacía inválida la selección de columnas. | Se agregó la coma. Posteriormente, las consultas y renovaciones documentadas en P-18 a P-23 se ejecutaron desde Java contra MySQL. | [d84d194](https://github.com/JohnPQ2312/examen1progra3/commit/d84d1941b0135cea915a3c188aed31f0b37bc95f), Joe Sandoval. |
