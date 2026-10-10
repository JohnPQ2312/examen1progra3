/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package cr.ac.una.repository;

import cr.ac.una.model.Prestamo;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author jp570
 */
public interface PrestamoRepository {

    Prestamo insertarConEstadoEquipo(Prestamo prestamo);

    Optional<Prestamo> buscarPorId(Integer id);

    List<Prestamo> listar();

    List<Prestamo> listarActivosPorSolicitante(String identificacion);

    int contarActivosPorSolicitante(String identificacion);

    boolean existeActivoPorEquipo(String codigo);

    void actualizarConEstadoEquipo(Prestamo prestamo);
}
