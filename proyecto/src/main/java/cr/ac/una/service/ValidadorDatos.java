package cr.ac.una.service;

import cr.ac.una.exception.ValidacionException;
import java.util.regex.Pattern;

public final class ValidadorDatos {

    private static final Pattern FORMATO_CORREO =
            Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$");

    private ValidadorDatos() {
    }

    public static void textoObligatorio(
            String valor, String campo, int longitudMaxima) {

        if (valor == null || valor.isBlank()) {
            throw new ValidacionException(
                    "El campo " + campo + " es obligatorio.");
        }

        int longitud = valor.codePointCount(0, valor.length());

        if (longitud > longitudMaxima) {
            throw new ValidacionException(
                    "El campo " + campo + " admite como máximo "
                    + longitudMaxima + " caracteres.");
        }
    }

    public static void correo(String valor) {
        textoObligatorio(valor, "correo", 150);

        if (!FORMATO_CORREO.matcher(valor).matches()) {
            throw new ValidacionException(
                    "El correo no tiene un formato válido.");
        }
    }
}