package cr.ac.una.controller;

import cr.ac.una.model.Solicitante;
import cr.ac.una.service.SolicitanteService;
import java.util.List;
import java.util.Optional;

public class SolicitanteController {

    private final SolicitanteService solicitanteService;

    public SolicitanteController(SolicitanteService solicitanteService) {
        this.solicitanteService = solicitanteService;
    }

    public void registrar(Solicitante solicitante) {
        solicitanteService.registrar(solicitante);
    }

    public Optional<Solicitante> buscarPorId(String identificacion) {
        return solicitanteService.buscarPorId(identificacion);
    }

    public List<Solicitante> listar() {
        return solicitanteService.listar();
    }

    public void actualizar(Solicitante solicitante) {
        solicitanteService.actualizar(solicitante);
    }
}