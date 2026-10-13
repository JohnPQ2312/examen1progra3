package cr.ac.una.service;

import cr.ac.una.exception.EquipoNoDisponibleException;
import cr.ac.una.exception.LimitePrestamosException;
import cr.ac.una.exception.ValidacionException;
import cr.ac.una.model.Equipo;
import cr.ac.una.model.EstadoEquipo;
import cr.ac.una.model.EstadoPrestamo;
import cr.ac.una.model.Prestamo;
import cr.ac.una.model.Solicitante;
import cr.ac.una.model.TipoSolicitante;
import cr.ac.una.repository.EquipoRepository;
import cr.ac.una.repository.PrestamoRepository;
import cr.ac.una.repository.SolicitanteRepository;
import java.time.LocalDate;
import java.util.List;

public class PrestamoService {

    private static final int LIMITE_ESTUDIANTE = 2;
    private static final int LIMITE_FUNCIONARIO = 4;

    private final SolicitanteRepository solicitanteRepository;
    private final EquipoRepository equipoRepository;
    private final PrestamoRepository prestamoRepository;

    public PrestamoService(
            SolicitanteRepository solicitanteRepository,
            EquipoRepository equipoRepository,
            PrestamoRepository prestamoRepository) {
        this.solicitanteRepository = solicitanteRepository;
        this.equipoRepository = equipoRepository;
        this.prestamoRepository = prestamoRepository;
    }

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

    public List<Prestamo> listarActivosPorSolicitante(String identificacion) {
        validarTexto(identificacion, "La identificacion del solicitante es obligatoria.");
        if (solicitanteRepository.buscarPorId(identificacion).isEmpty()) {
            throw new ValidacionException(
                    "No existe un solicitante con identificacion " + identificacion + ".");
        }
        return prestamoRepository.listarActivosPorSolicitante(identificacion);
    }

    public Prestamo registrarDevolucion(Integer idPrestamo) {
        if (idPrestamo == null || idPrestamo <= 0) {
            throw new ValidacionException("El numero de prestamo no es valido.");
        }

        Prestamo prestamo = prestamoRepository.buscarPorId(idPrestamo)
                .orElseThrow(() -> new ValidacionException(
                        "No existe un prestamo con nemero " + idPrestamo + "."));
        if (prestamo.getEstado() != EstadoPrestamo.ACTIVO) {
            throw new ValidacionException(
                    "El prestamo " + idPrestamo + " ya fue devuelto el "
                    + prestamo.getFechaDevolucionReal() + ".");
        }

        prestamo.setFechaDevolucionReal(LocalDate.now());
        prestamo.setEstado(EstadoPrestamo.DEVUELTO);
        prestamo.getEquipo().setEstado(EstadoEquipo.DISPONIBLE);
        prestamoRepository.actualizarConEstadoEquipo(prestamo);
        return prestamo;
    }

    private void validarTexto(String valor, String mensaje) {
        if (valor == null || valor.isBlank()) {
            throw new ValidacionException(mensaje);
        }
    }
}