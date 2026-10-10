/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package cr.ac.una.repository;

import cr.ac.una.model.Solicitante;
import java.util.List;
import java.util.Optional;


/**
 *
 * @author jp570
 */
public interface SolicitanteRepository {

    void insertar(Solicitante solicitante);

    Optional<Solicitante> buscarPorId(String identificacion);

    List<Solicitante> listar();

    void actualizar(Solicitante solicitante);
}
