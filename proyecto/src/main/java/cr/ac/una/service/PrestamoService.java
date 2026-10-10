package cr.ac.una.service;

import cr.ac.una.model.Prestamo;
import cr.ac.una.repository.EquipoRepository;
import cr.ac.una.repository.PrestamoRepository;
import cr.ac.una.repository.SolicitanteRepository;
import java.time.LocalDate;
import java.util.List;

public class PrestamoService {

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
        throw new UnsupportedOperationException(
                "Registro de préstamos pendiente para la Etapa 2.");
    }

    public List<Prestamo> listarActivosPorSolicitante(
            String identificacion) {
        throw new UnsupportedOperationException(
                "Consulta de préstamos activos pendiente para la Etapa 2.");
    }

    public Prestamo registrarDevolucion(Integer idPrestamo) {
        throw new UnsupportedOperationException(
                "Registro de devoluciones pendiente para la Etapa 2.");
    }
}