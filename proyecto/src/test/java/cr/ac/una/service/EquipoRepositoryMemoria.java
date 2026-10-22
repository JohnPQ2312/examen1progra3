package cr.ac.una.service;

import cr.ac.una.model.Equipo;
import cr.ac.una.repository.EquipoRepository;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

class EquipoRepositoryMemoria implements EquipoRepository {

    private final Map<String, Equipo> equipos = new LinkedHashMap<>();

    @Override
    public void insertar(Equipo equipo) {
        equipos.put(equipo.getCodigo(), equipo);
    }

    @Override
    public Optional<Equipo> buscarPorId(String codigo) {
        return Optional.ofNullable(equipos.get(codigo));
    }

    @Override
    public List<Equipo> listar() {
        return new ArrayList<>(equipos.values());
    }

    @Override
    public void actualizar(Equipo equipo) {
        equipos.put(equipo.getCodigo(), equipo);
    }
}