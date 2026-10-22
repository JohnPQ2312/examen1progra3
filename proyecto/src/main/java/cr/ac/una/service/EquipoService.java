package cr.ac.una.service;

import cr.ac.una.exception.ValidacionException;
import cr.ac.una.model.Equipo;
import cr.ac.una.model.EstadoEquipo;
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
        validar(equipo);
        if (equipo.getEstado() == null) {
            equipo.setEstado(EstadoEquipo.DISPONIBLE);
        }

        if (equipo.getEstado() == EstadoEquipo.PRESTADO) {
            throw new ValidacionException(
                    "Un equipo nuevo no puede registrarse como PRESTADO.");
        }
        if (equipoRepository.buscarPorId(equipo.getCodigo()).isPresent()) {
            throw new ValidacionException(
                    "Ya existe un equipo con codigo " + equipo.getCodigo() + ".");
        }
        equipoRepository.insertar(equipo);
    }

    public Optional<Equipo> buscarPorId(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            throw new ValidacionException("El codigo del equipo es obligatorio.");
        }
        return equipoRepository.buscarPorId(codigo.trim());
    }

    public List<Equipo> listar() {
        return equipoRepository.listar();
    }

    public void actualizar(Equipo equipo) {
        validar(equipo);
        if (equipo.getEstado() == null) {
            throw new ValidacionException("El estado del equipo es obligatorio.");
        }
        if (equipoRepository.buscarPorId(equipo.getCodigo()).isEmpty()) {
            throw new ValidacionException(
                    "No existe un equipo con codigo " + equipo.getCodigo() + ".");
        }
        boolean tienePrestamoActivo = prestamoRepository.existeActivoPorEquipo(equipo.getCodigo());
        if (tienePrestamoActivo && equipo.getEstado() != EstadoEquipo.PRESTADO) {
            throw new ValidacionException(
                    "El equipo " + equipo.getCodigo()
                    + " tiene un prestamo activo; registre primero la devolucion.");
        }
        if (!tienePrestamoActivo && equipo.getEstado() == EstadoEquipo.PRESTADO) {
            throw new ValidacionException(
                    "Un equipo solo pasa a prestado al registrar un prestamo.");
        }
        equipoRepository.actualizar(equipo);
    }

    private void validar(Equipo equipo) {
        if (equipo == null) {
            throw new ValidacionException(
                    "Los datos del equipo son obligatorios.");
        }

        ValidadorDatos.textoObligatorio(
                equipo.getCodigo(), "código del equipo", 20);

        ValidadorDatos.textoObligatorio(
                equipo.getDescripcion(), "descripción del equipo", 100);

        ValidadorDatos.textoObligatorio(
                equipo.getTipo(), "tipo de equipo", 50);
    }
}
