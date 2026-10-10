package cr.ac.una.controller;

import cr.ac.una.model.Equipo;
import cr.ac.una.service.EquipoService;
import java.util.List;
import java.util.Optional;

public class EquipoController {

    private final EquipoService equipoService;

    public EquipoController(EquipoService equipoService) {
        this.equipoService = equipoService;
    }

    public void registrar(Equipo equipo) {
        equipoService.registrar(equipo);
    }

    public Optional<Equipo> buscarPorId(String codigo) {
        return equipoService.buscarPorId(codigo);
    }

    public List<Equipo> listar() {
        return equipoService.listar();
    }

    public void actualizar(Equipo equipo) {
        equipoService.actualizar(equipo);
    }
}