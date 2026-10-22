package cr.ac.una.service;

import cr.ac.una.exception.ValidacionException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ValidadorDatosTest {

    @ParameterizedTest
    @ValueSource(ints = {20, 30, 50, 100})
    void aceptaTextoEnLongitudMaxima(int maximo) {
        String texto = "a".repeat(maximo);

        assertDoesNotThrow(() ->
                ValidadorDatos.textoObligatorio(
                        texto, "campo de prueba", maximo));
    }

    @ParameterizedTest
    @ValueSource(ints = {20, 30, 50, 100})
    void rechazaTextoQueSuperaLongitudMaxima(int maximo) {
        String texto = "a".repeat(maximo + 1);

        assertThrows(ValidacionException.class, () ->
                ValidadorDatos.textoObligatorio(
                        texto, "campo de prueba", maximo));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t", "\n"})
    void rechazaTextoSinContenido(String texto) {
        assertThrows(ValidacionException.class, () ->
                ValidadorDatos.textoObligatorio(
                        texto, "campo de prueba", 20));
    }

    @Test
    void aceptaCorreoValido() {
        assertDoesNotThrow(() ->
                ValidadorDatos.correo("persona@example.com"));
    }

    @Test
    void rechazaCorreoSinFormatoValido() {
        assertThrows(ValidacionException.class, () ->
                ValidadorDatos.correo("persona.example.com"));
    }

    @Test
    void aceptaCorreoDe150Caracteres() {
        String correo = "usuario123@"
                + "a".repeat(63) + "."
                + "b".repeat(63) + ".ejemplo.com";

        assertDoesNotThrow(() ->
                ValidadorDatos.correo(correo));
    }

    @Test
    void rechazaCorreoDe151Caracteres() {
        String correo = "xusuario123@"
                + "a".repeat(63) + "."
                + "b".repeat(63) + ".ejemplo.com";

        assertThrows(ValidacionException.class, () ->
                ValidadorDatos.correo(correo));
    }
}