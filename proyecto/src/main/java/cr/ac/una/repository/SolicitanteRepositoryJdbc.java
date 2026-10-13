/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cr.ac.una.repository;

import cr.ac.una.config.ConexionBD;
import cr.ac.una.exception.DatosException;
import cr.ac.una.model.Solicitante;
import cr.ac.una.model.TipoSolicitante;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author User
 */
public class SolicitanteRepositoryJdbc implements SolicitanteRepository {

    @Override
    public void insertar(Solicitante solicitante) {
        String sql = "INSERT INTO solicitante (identificacion, nombre, correo, tipo) "
                + "VALUES (?, ?, ?, ?)";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, solicitante.getIdentificacion());
            ps.setString(2, solicitante.getNombre());
            ps.setString(3, solicitante.getCorreo());
            ps.setString(4, solicitante.getTipo().name());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatosException("Error al registrar el solicitante "
                    + solicitante.getIdentificacion(), e);
        }
    }

    @Override
    public Optional<Solicitante> buscarPorId(String identificacion) {
        String sql = "SELECT identificacion, nombre, correo, tipo "
                + "FROM solicitante WHERE identificacion = ?";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, identificacion);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DatosException("Error al buscar el solicitante " + identificacion, e);
        }
    }

    @Override
    public List<Solicitante> listar() {
        String sql = "SELECT identificacion, nombre, correo, tipo "
                + "FROM solicitante ORDER BY nombre";
        List<Solicitante> lista = new ArrayList<>();
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
            return lista;
        } catch (SQLException e) {
            throw new DatosException("Error al listar los solicitantes", e);
        }
    }

    @Override
    public void actualizar(Solicitante solicitante) {
        String sql = "UPDATE solicitante SET nombre = ?, correo = ?, tipo = ? "
                + "WHERE identificacion = ?";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, solicitante.getNombre());
            ps.setString(2, solicitante.getCorreo());
            ps.setString(3, solicitante.getTipo().name());
            ps.setString(4, solicitante.getIdentificacion());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatosException("Error al actualizar el solicitante "
                    + solicitante.getIdentificacion(), e);
        }
    }
    
    private Solicitante mapear(ResultSet rs) throws SQLException {
        return new Solicitante(
                rs.getString("identificacion"),
                rs.getString("nombre"),
                rs.getString("correo"),
                TipoSolicitante.valueOf(rs.getString("tipo")));
    }
}
