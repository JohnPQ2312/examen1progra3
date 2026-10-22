package cr.ac.una.service;

import cr.ac.una.exception.ValidacionException;
import cr.ac.una.model.Solicitante;
import cr.ac.una.model.TipoSolicitante;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SolicitanteServiceTest {

    private SolicitanteRepositoryMemoria repositorio;
    private SolicitanteService servicio;

    @BeforeEach
    void preparar() {
        repositorio = new SolicitanteRepositoryMemoria();
        servicio = new SolicitanteService(repositorio, null);
    }

    @Test
    void registrarAceptaCamposEnSusLongitudesMaximas() {
        Solicitante solicitante = new Solicitante(
                "I".repeat(30),
                "N".repeat(100),
                correoDe150Caracteres(),
                TipoSolicitante.ESTUDIANTE);

        servicio.registrar(solicitante);

        assertEquals(1, repositorio.listar().size());
        assertEquals(
                solicitante.getIdentificacion(),
                repositorio.buscarPorId(
                        solicitante.getIdentificacion())
                        .orElseThrow().getIdentificacion());
    }

    @ParameterizedTest
    @CsvSource({
        "identificacion, 31",
        "nombre, 101"
    })
    void registrarRechazaCampoDemasiadoLargo(
            String campo, int longitud) {

        Solicitante solicitante = solicitanteValido();
        String texto = "A".repeat(longitud);

        switch (campo) {
            case "identificacion" ->
                solicitante.setIdentificacion(texto);
            case "nombre" ->
                solicitante.setNombre(texto);
            default -> throw new IllegalArgumentException(
                    "Campo de prueba desconocido: " + campo);
        }

        assertThrows(ValidacionException.class, () ->
                servicio.registrar(solicitante));

        assertEquals(0, repositorio.listar().size());
    }

    @Test
    void registrarRechazaCorreoDe151Caracteres() {
        Solicitante solicitante = solicitanteValido();
        solicitante.setCorreo("x" + correoDe150Caracteres());

        assertThrows(ValidacionException.class, () ->
                servicio.registrar(solicitante));

        assertEquals(0, repositorio.listar().size());
    }

    @Test
    void registrarRechazaCorreoInvalido() {
        Solicitante solicitante = solicitanteValido();
        solicitante.setCorreo("correo-sin-arroba");

        assertThrows(ValidacionException.class, () ->
                servicio.registrar(solicitante));

        assertEquals(0, repositorio.listar().size());
    }

    @Test
    void registrarRechazaIdentificacionDuplicada() {
        servicio.registrar(solicitanteValido());

        assertThrows(ValidacionException.class, () ->
                servicio.registrar(solicitanteValido()));

        assertEquals(1, repositorio.listar().size());
    }

    @Test
    void registrarRechazaTipoNulo() {
        Solicitante solicitante = solicitanteValido();
        solicitante.setTipo(null);

        assertThrows(ValidacionException.class, () ->
                servicio.registrar(solicitante));

        assertEquals(0, repositorio.listar().size());
    }

    private Solicitante solicitanteValido() {
        return new Solicitante(
                "ID-TEST",
                "Persona de prueba",
                "persona@example.com",
                TipoSolicitante.ESTUDIANTE);
    }

    private String correoDe150Caracteres() {
        return "usuario123@"
                + "a".repeat(63) + "."
                + "b".repeat(63) + ".ejemplo.com";
    }
}