package cr.ac.una.service;

import cr.ac.una.exception.ValidacionException;
import cr.ac.una.model.Equipo;
import cr.ac.una.model.EstadoEquipo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EquipoServiceTest {

    private EquipoRepositoryMemoria repositorio;
    private EquipoService servicio;

    @BeforeEach
    void preparar() {
        repositorio = new EquipoRepositoryMemoria();
        servicio = new EquipoService(repositorio, null);
    }

    @Test
    void registrarAceptaCamposEnSusLongitudesMaximas() {
        Equipo equipo = new Equipo(
                "A".repeat(20),
                "D".repeat(100),
                "T".repeat(50),
                EstadoEquipo.DISPONIBLE);

        servicio.registrar(equipo);

        assertEquals(1, repositorio.listar().size());
        assertEquals(
                equipo.getCodigo(),
                repositorio.buscarPorId(equipo.getCodigo())
                        .orElseThrow().getCodigo());
    }

    @Test
    void registrarAsignaDisponibleCuandoEstadoEsNulo() {
        Equipo equipo = new Equipo(
                "EQ-TEST", "Laptop de prueba", "Laptop", null);

        servicio.registrar(equipo);

        assertEquals(EstadoEquipo.DISPONIBLE, equipo.getEstado());
        assertEquals(1, repositorio.listar().size());
    }

    @ParameterizedTest
    @CsvSource({
        "codigo, 21",
        "descripcion, 101",
        "tipo, 51"
    })
    void registrarRechazaCampoDemasiadoLargo(
            String campo, int longitud) {

        Equipo equipo = equipoValido();
        String texto = "A".repeat(longitud);

        switch (campo) {
            case "codigo" -> equipo.setCodigo(texto);
            case "descripcion" -> equipo.setDescripcion(texto);
            case "tipo" -> equipo.setTipo(texto);
            default -> throw new IllegalArgumentException(
                    "Campo de prueba desconocido: " + campo);
        }

        assertThrows(ValidacionException.class, () ->
                servicio.registrar(equipo));

        assertEquals(0, repositorio.listar().size());
    }

    @Test
    void registrarRechazaCodigoDuplicado() {
        servicio.registrar(equipoValido());

        assertThrows(ValidacionException.class, () ->
                servicio.registrar(equipoValido()));

        assertEquals(1, repositorio.listar().size());
    }

    private Equipo equipoValido() {
        return new Equipo(
                "EQ-TEST",
                "Laptop de prueba",
                "Laptop",
                EstadoEquipo.DISPONIBLE);
    }
}