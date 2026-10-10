/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package cr.ac.una.repository;

import cr.ac.una.model.Equipo;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author jp570
 */

public interface EquipoRepository {

    void insertar(Equipo equipo);

    Optional<Equipo> buscarPorId(String codigo);

    List<Equipo> listar();

    void actualizar(Equipo equipo);
}