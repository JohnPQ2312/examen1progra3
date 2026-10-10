package cr.ac.una.service;

import cr.ac.una.model.Solicitante;
import cr.ac.una.repository.PrestamoRepository;
import cr.ac.una.repository.SolicitanteRepository;
import java.util.List;
import java.util.Optional;

public class SolicitanteService {

    private final SolicitanteRepository solicitanteRepository;
    private final PrestamoRepository prestamoRepository;

    public SolicitanteService(
            SolicitanteRepository solicitanteRepository,
            PrestamoRepository prestamoRepository) {
        this.solicitanteRepository = solicitanteRepository;
        this.prestamoRepository = prestamoRepository;
    }

    public void registrar(Solicitante solicitante) {
        throw new UnsupportedOperationException(
                "Registro de solicitantes pendiente para la Etapa 2.");
    }

    public Optional<Solicitante> buscarPorId(String identificacion) {
        throw new UnsupportedOperationException(
                "Consulta de solicitantes pendiente para la Etapa 2.");
    }

    public List<Solicitante> listar() {
        throw new UnsupportedOperationException(
                "Listado de solicitantes pendiente para la Etapa 2.");
    }

    public void actualizar(Solicitante solicitante) {
        throw new UnsupportedOperationException(
                "Actualización de solicitantes pendiente para la Etapa 2.");
    }
}