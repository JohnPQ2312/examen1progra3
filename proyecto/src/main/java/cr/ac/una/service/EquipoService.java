package cr.ac.una.service;

import cr.ac.una.model.Equipo;
import cr.ac.una.repository.EquipoRepository;
import cr.ac.una.repository.PrestamoRepository;
import java.util.List;
import java.util.Optional;

public class EquipoService {

    private final EquipoRepository equipoRepository;
    private final PrestamoRepository prestamoRepository;

    public EquipoService(
            EquipoRepository equipoRepository,
            PrestamoRepository prestamoRepository) {
        this.equipoRepository = equipoRepository;
        this.prestamoRepository = prestamoRepository;
    }

    public void registrar(Equipo equipo) {
        throw new UnsupportedOperationException(
                "Registro de equipos pendiente para la Etapa 2.");
    }

    public Optional<Equipo> buscarPorId(String codigo) {
        throw new UnsupportedOperationException(
                "Consulta de equipos pendiente para la Etapa 2.");
    }

    public List<Equipo> listar() {
        throw new UnsupportedOperationException(
                "Listado de equipos pendiente para la Etapa 2.");
    }

    public void actualizar(Equipo equipo) {
        throw new UnsupportedOperationException(
                "Actualización de equipos pendiente para la Etapa 2.");
    }
}