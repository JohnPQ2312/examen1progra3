package cr.ac.una.service;

import cr.ac.una.controller.PrestamoController;
import cr.ac.una.exception.RenovacionNoPermitidaException;
import cr.ac.una.exception.ValidacionException;
import cr.ac.una.exception.LimitePrestamosException;
import cr.ac.una.model.*;
import cr.ac.una.repository.PrestamoRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;

/** Pruebas de reglas de préstamos con repositorios de prueba, sin conexión a MySQL. */
class PrestamoServiceTest {
    private static class RepositorioPrestamosPrueba implements PrestamoRepository {
        Prestamo prestamo;
        int actualizaciones;
        int activos;
        int inserciones;
        final List<PrestamoVencido> vencidos = List.of(new PrestamoVencido("Persona", "EQ-1", 2));
        RepositorioPrestamosPrueba(Prestamo p) { prestamo = p; }
        public Optional<Prestamo> buscarPorId(Integer id) {
            return prestamo != null && prestamo.getId().equals(id) ? Optional.of(prestamo) : Optional.empty();
        }
        public void actualizarRenovacion(Prestamo p) { actualizaciones++; prestamo = p; }
        public List<PrestamoVencido> listarActivosVencidos() { return vencidos; }
        public Prestamo insertarConEstadoEquipo(Prestamo p) {
            inserciones++;
            p.setId(10);
            prestamo = p;
            return p;
        }
        public List<Prestamo> listar() { throw new UnsupportedOperationException(); }
        public List<Prestamo> listarActivosPorSolicitante(String id) { throw new UnsupportedOperationException(); }
        public int contarActivosPorSolicitante(String id) { return activos; }
        public boolean existeActivoPorEquipo(String codigo) { throw new UnsupportedOperationException(); }
        public void actualizarConEstadoEquipo(Prestamo p) { throw new UnsupportedOperationException(); }
    }
    private Prestamo prestamo(TipoSolicitante tipo, LocalDate vencimiento) {
        Solicitante s = new Solicitante("ID-1", "Persona", "persona@example.com", tipo);
        Equipo e = new Equipo("EQ-1", "Equipo", "Laptop", EstadoEquipo.PRESTADO);
        return new Prestamo(4, s, e, LocalDate.now().minusDays(5), vencimiento, null, EstadoPrestamo.ACTIVO, 0);
    }
    private PrestamoService servicio(RepositorioPrestamosPrueba r) { return new PrestamoService(null, null, r); }
    private void comprobarRenovacion(TipoSolicitante tipo, int dias, LocalDate fecha) {
        Prestamo p = prestamo(tipo, fecha);
        RepositorioPrestamosPrueba r = new RepositorioPrestamosPrueba(p);
        Prestamo resultado = servicio(r).renovarPrestamo(4);
        assertAll(
            () -> assertSame(p, resultado),
            () -> assertEquals(fecha.plusDays(dias), resultado.getFechaDevolucionEsperada()),
            () -> assertEquals(1, resultado.getRenovaciones()),
            () -> assertEquals(EstadoPrestamo.ACTIVO, resultado.getEstado()),
            () -> assertEquals(EstadoEquipo.PRESTADO, resultado.getEquipo().getEstado()),
            () -> assertNull(resultado.getFechaDevolucionReal()),
            () -> assertEquals(1, r.actualizaciones)
        );
    }
    private void comprobarRechazo(Prestamo p) {
        RepositorioPrestamosPrueba r = new RepositorioPrestamosPrueba(p);
        LocalDate fecha = p.getFechaDevolucionEsperada();
        int renovaciones = p.getRenovaciones();
        assertThrows(RenovacionNoPermitidaException.class, () -> servicio(r).renovarPrestamo(4));
        assertAll(
            () -> assertEquals(fecha, p.getFechaDevolucionEsperada()),
            () -> assertEquals(renovaciones, p.getRenovaciones()),
            () -> assertEquals(0, r.actualizaciones)
        );
    }
    @Test void estudianteSumaTresDiasDesdeFechaEsperada() {
        comprobarRenovacion(TipoSolicitante.ESTUDIANTE, 3, LocalDate.now().plusDays(10));
    }
    @Test void funcionarioSumaSieteDiasDesdeFechaEsperada() {
        comprobarRenovacion(TipoSolicitante.FUNCIONARIO, 7, LocalDate.now().plusDays(10));
    }
    @Test void permiteRenovarCuandoVenceHoy() {
        comprobarRenovacion(TipoSolicitante.ESTUDIANTE, 3, LocalDate.now());
    }
    @Test void rechazaVencidoDesdeAyer() {
        comprobarRechazo(prestamo(TipoSolicitante.ESTUDIANTE, LocalDate.now().minusDays(1)));
    }
    @Test void rechazaDevuelto() {
        Prestamo p = prestamo(TipoSolicitante.ESTUDIANTE, LocalDate.now().plusDays(10));
        p.setEstado(EstadoPrestamo.DEVUELTO); p.setFechaDevolucionReal(LocalDate.now());
        p.getEquipo().setEstado(EstadoEquipo.DISPONIBLE);
        comprobarRechazo(p);
    }
    @Test void rechazaSegundaRenovacion() {
        Prestamo p = prestamo(TipoSolicitante.ESTUDIANTE, LocalDate.now().plusDays(10));
        RepositorioPrestamosPrueba r = new RepositorioPrestamosPrueba(p);
        PrestamoService s = servicio(r); s.renovarPrestamo(4);
        LocalDate fecha = p.getFechaDevolucionEsperada();
        assertThrows(RenovacionNoPermitidaException.class, () -> s.renovarPrestamo(4));
        assertEquals(fecha, p.getFechaDevolucionEsperada());
        assertEquals(1, p.getRenovaciones()); assertEquals(1, r.actualizaciones);
    }
    @ParameterizedTest @NullSource @ValueSource(ints={0,-1})
    void rechazaIdentificadorInvalido(Integer id) {
        RepositorioPrestamosPrueba r = new RepositorioPrestamosPrueba(null);
        assertThrows(ValidacionException.class, () -> servicio(r).renovarPrestamo(id));
        assertEquals(0, r.actualizaciones);
    }
    @Test void rechazaPrestamoInexistente() {
        RepositorioPrestamosPrueba r = new RepositorioPrestamosPrueba(null);
        assertThrows(ValidacionException.class, () -> servicio(r).renovarPrestamo(999));
        assertEquals(0, r.actualizaciones);
    }
    @Test void controladorDelegaRenovacion() {
        LocalDate fecha = LocalDate.now().plusDays(10);
        RepositorioPrestamosPrueba r = new RepositorioPrestamosPrueba(prestamo(TipoSolicitante.ESTUDIANTE, fecha));
        Prestamo resultado = new PrestamoController(servicio(r)).renovarPrestamo(4);
        assertEquals(fecha.plusDays(3), resultado.getFechaDevolucionEsperada());
        assertEquals(1, r.actualizaciones);
    }
    @Test void controladorDelegaConsultaDeVencidos() {
        RepositorioPrestamosPrueba r = new RepositorioPrestamosPrueba(null);
        assertSame(r.vencidos, new PrestamoController(servicio(r)).listarActivosVencidos());
    }

    @ParameterizedTest
    @CsvSource({"ESTUDIANTE, 1", "FUNCIONARIO, 3"})
    void registrarAceptaUltimoPrestamoPermitido(TipoSolicitante tipo, int activos) {
        RepositorioPrestamosPrueba r = new RepositorioPrestamosPrueba(null);
        r.activos = activos;
        EquipoRepositoryMemoria equipos = new EquipoRepositoryMemoria();
        SolicitanteRepositoryMemoria solicitantes = new SolicitanteRepositoryMemoria();
        solicitantes.insertar(new Solicitante("ID-LIMITE", "Persona de prueba", "limite@example.com", tipo));
        Equipo equipo = new Equipo("EQ-LIMITE", "Equipo de prueba", "Laptop", EstadoEquipo.DISPONIBLE);
        equipos.insertar(equipo);
        PrestamoService s = new PrestamoService(solicitantes, equipos, r);

        Prestamo resultado = s.registrarPrestamo("ID-LIMITE", "EQ-LIMITE", LocalDate.now().plusDays(7));

        assertAll(
            () -> assertEquals(1, r.inserciones),
            () -> assertEquals(EstadoPrestamo.ACTIVO, resultado.getEstado()),
            () -> assertEquals(EstadoEquipo.PRESTADO, equipo.getEstado()),
            () -> assertEquals(0, resultado.getRenovaciones())
        );
    }

    @ParameterizedTest
    @CsvSource({"ESTUDIANTE, 2", "FUNCIONARIO, 4"})
    void registrarRechazaCuandoAlcanzaLimite(TipoSolicitante tipo, int activos) {
        RepositorioPrestamosPrueba r = new RepositorioPrestamosPrueba(null);
        r.activos = activos;
        EquipoRepositoryMemoria equipos = new EquipoRepositoryMemoria();
        SolicitanteRepositoryMemoria solicitantes = new SolicitanteRepositoryMemoria();
        solicitantes.insertar(new Solicitante("ID-LIMITE", "Persona de prueba", "limite@example.com", tipo));
        Equipo equipo = new Equipo("EQ-LIMITE", "Equipo de prueba", "Laptop", EstadoEquipo.DISPONIBLE);
        equipos.insertar(equipo);
        PrestamoService s = new PrestamoService(solicitantes, equipos, r);

        assertThrows(LimitePrestamosException.class, () ->
                s.registrarPrestamo("ID-LIMITE", "EQ-LIMITE", LocalDate.now().plusDays(7)));
        assertAll(
            () -> assertEquals(0, r.inserciones),
            () -> assertNull(r.prestamo),
            () -> assertEquals(EstadoEquipo.DISPONIBLE, equipo.getEstado())
        );
    }
}
