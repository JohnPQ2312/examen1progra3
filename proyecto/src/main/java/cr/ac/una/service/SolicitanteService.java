package cr.ac.una.service;

import cr.ac.una.exception.ValidacionException;
import cr.ac.una.model.Solicitante;
import cr.ac.una.model.TipoSolicitante;
import cr.ac.una.repository.PrestamoRepository;
import cr.ac.una.repository.SolicitanteRepository;
import java.util.List;
import java.util.Optional;

public class SolicitanteService {
    
    private static final int LIMITE_ESTUDIANTE = 2;

    private final SolicitanteRepository solicitanteRepository;
    private final PrestamoRepository prestamoRepository;

    public SolicitanteService(
            SolicitanteRepository solicitanteRepository,
            PrestamoRepository prestamoRepository) {
        this.solicitanteRepository = solicitanteRepository;
        this.prestamoRepository = prestamoRepository;
    }

    public void registrar(Solicitante solicitante) {
        validar(solicitante);
        if (solicitanteRepository.buscarPorId(solicitante.getIdentificacion()).isPresent()) {
            throw new ValidacionException(
                    "Ya existe un solicitante con identificacion "
                    + solicitante.getIdentificacion() + ".");
        }
        solicitanteRepository.insertar(solicitante);
    }

    public Optional<Solicitante> buscarPorId(String identificacion) {
        if (identificacion == null || identificacion.isBlank()) {
            throw new ValidacionException("La identificacion es obligatoria.");
        }
        return solicitanteRepository.buscarPorId(identificacion.trim());
    }

    public List<Solicitante> listar() {
        return solicitanteRepository.listar();
    }

    public void actualizar(Solicitante solicitante) {
        validar(solicitante);
        if (solicitanteRepository.buscarPorId(solicitante.getIdentificacion()).isEmpty()) {
            throw new ValidacionException(
                    "No existe un solicitante con identificacion "
                    + solicitante.getIdentificacion() + ".");
        }

        if (solicitante.getTipo() == TipoSolicitante.ESTUDIANTE) {
            int activos = prestamoRepository.contarActivosPorSolicitante(
                    solicitante.getIdentificacion());
            if (activos > LIMITE_ESTUDIANTE) {
                throw new ValidacionException(
                        "No se puede cambiar a ESTUDIANTE: tiene " + activos
                        + " prestamos activos y el maximo para estudiante es "
                        + LIMITE_ESTUDIANTE + ".");
            }
        }
        solicitanteRepository.actualizar(solicitante);
    }

    private void validar(Solicitante solicitante) {
        if (solicitante == null) {
            throw new ValidacionException(
                    "Los datos del solicitante son obligatorios.");
        }

        ValidadorDatos.textoObligatorio(
                solicitante.getIdentificacion(),
                "identificación del solicitante", 30);

        ValidadorDatos.textoObligatorio(
                solicitante.getNombre(),
                "nombre del solicitante", 100);

        ValidadorDatos.correo(solicitante.getCorreo());

        if (solicitante.getTipo() == null) {
            throw new ValidacionException(
                    "El tipo de solicitante es obligatorio.");
        }
    }
}
