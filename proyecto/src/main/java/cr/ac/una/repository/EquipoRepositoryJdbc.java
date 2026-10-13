/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cr.ac.una.repository;

import cr.ac.una.config.ConexionBD;
import cr.ac.una.exception.DatosException;
import cr.ac.una.model.Equipo;
import cr.ac.una.model.EstadoEquipo;
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
public class EquipoRepositoryJdbc implements EquipoRepository {

    @Override
    public void insertar(Equipo equipo) {
        String sql = "INSERT INTO equipo (codigo, descripcion, tipo, estado) VALUES (?,?,?,?)";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, equipo.getCodigo());
            ps.setString(2, equipo.getDescripcion());
            ps.setString(3, equipo.getTipo());
            ps.setString(4, equipo.getEstado().name());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatosException("Error al registrar el equipo " + equipo.getCodigo(), e);
        }
    }

    @Override
    public Optional<Equipo> buscarPorId(String codigo) {
        String sql = "SELECT codigo, descripcion, tipo, estado "
                + "FROM equipo WHERE codigo = ?";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, codigo);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DatosException("Error al buscar el equipo " + codigo, e);
        }
    }

    @Override
    public List<Equipo> listar() {
        String sql = "SELECT codigo, descripcion, tipo, estado "
                + "FROM equipo ORDER BY codigo";
        List<Equipo> lista = new ArrayList<>();
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
            return lista;
        } catch (SQLException e) {
            throw new DatosException("Error al listar los equipos", e);
        }
    }

    @Override
    public void actualizar(Equipo equipo) {
         String sql = "UPDATE equipo SET descripcion = ?, tipo = ?, estado = ? "
                + "WHERE codigo = ?";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, equipo.getDescripcion());
            ps.setString(2, equipo.getTipo());
            ps.setString(3, equipo.getEstado().name());
            ps.setString(4, equipo.getCodigo());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatosException("Error al actualizar el equipo " + equipo.getCodigo(), e);
        }
    }
    
    private Equipo mapear(ResultSet rs) throws SQLException {
        return new Equipo(
                rs.getString("codigo"),
                rs.getString("descripcion"),
                rs.getString("tipo"),
                EstadoEquipo.valueOf(rs.getString("estado")));
    }    
}
