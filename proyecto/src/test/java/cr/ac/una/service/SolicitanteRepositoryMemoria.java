package cr.ac.una.service;

import cr.ac.una.model.Solicitante;
import cr.ac.una.repository.SolicitanteRepository;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

class SolicitanteRepositoryMemoria
        implements SolicitanteRepository {

    private final Map<String, Solicitante> solicitantes =
            new LinkedHashMap<>();

    @Override
    public void insertar(Solicitante solicitante) {
        solicitantes.put(
                solicitante.getIdentificacion(), solicitante);
    }

    @Override
    public Optional<Solicitante> buscarPorId(String identificacion) {
        return Optional.ofNullable(
                solicitantes.get(identificacion));
    }

    @Override
    public List<Solicitante> listar() {
        return new ArrayList<>(solicitantes.values());
    }

    @Override
    public void actualizar(Solicitante solicitante) {
        solicitantes.put(
                solicitante.getIdentificacion(), solicitante);
    }
}