package cr.ac.una.repository;

import cr.ac.una.config.ConexionBD;
import cr.ac.una.exception.DatosException;
import cr.ac.una.model.Equipo;
import cr.ac.una.model.EstadoEquipo;
import cr.ac.una.model.EstadoPrestamo;
import cr.ac.una.model.Prestamo;
import cr.ac.una.model.Solicitante;
import cr.ac.una.model.TipoSolicitante;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PrestamoRepositoryJdbc implements PrestamoRepository {

 
    private static final String SELECT_BASE =
            "SELECT p.id, p.fecha_prestamo, p.fecha_devolucion_esperada, "
            + "p.fecha_devolucion_real, p.estado AS estado_prestamo, "
            + "s.identificacion, s.nombre, s.correo, s.tipo AS tipo_solicitante, "
            + "e.codigo, e.descripcion, e.tipo AS tipo_equipo, e.estado AS estado_equipo "
            + "FROM prestamo p "
            + "JOIN solicitante s ON s.identificacion = p.solicitante_id "
            + "JOIN equipo e ON e.codigo = p.equipo_codigo ";


    @Override
    public Optional<Prestamo> buscarPorId(Integer id) {
        String sql = SELECT_BASE + "WHERE p.id = ?";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DatosException("Error al buscar el prestamo " + id, e);
        }
    }

    @Override
    public List<Prestamo> listar() {
        String sql = SELECT_BASE + "ORDER BY p.id";
        List<Prestamo> lista = new ArrayList<>();
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
            return lista;
        } catch (SQLException e) {
            throw new DatosException("Error al listar los prestamos", e);
        }
    }
    
    @Override
    public List<Prestamo> listarActivosPorSolicitante(String identificacion) {
        String sql = SELECT_BASE
                + "WHERE p.solicitante_id = ? AND p.estado = 'ACTIVO' "
                + "ORDER BY p.fecha_prestamo";
        List<Prestamo> lista = new ArrayList<>();
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, identificacion);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
            return lista;
        } catch (SQLException e) {
            throw new DatosException("Error al listar los prestamos activos de " + identificacion, e);
        }
    }

    @Override
    public int contarActivosPorSolicitante(String identificacion) {
        String sql = "SELECT COUNT(*) FROM prestamo WHERE solicitante_id = ? AND estado = 'ACTIVO'";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, identificacion);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new DatosException("Error al contar los prestamos activos de " + identificacion, e);
        }
    }

    @Override
    public boolean existeActivoPorEquipo(String codigo) {
        String sql = "SELECT COUNT(*) FROM prestamo WHERE equipo_codigo = ? AND estado = 'ACTIVO'";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, codigo);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new DatosException("Error al verificar prestamos activos del equipo " + codigo, e);
        }
    }

    @Override
    public Prestamo insertarConEstadoEquipo(Prestamo prestamo) {
        String sqlPrestamo = "INSERT INTO prestamo (solicitante_id, equipo_codigo, fecha_prestamo, "
                + "fecha_devolucion_esperada, fecha_devolucion_real, estado) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        String sqlEquipo = "UPDATE equipo SET estado = ? WHERE codigo = ?";

        try (Connection con = ConexionBD.obtenerConexion()) {
            con.setAutoCommit(false);
            try (PreparedStatement psPrestamo = con.prepareStatement(sqlPrestamo,
                         Statement.RETURN_GENERATED_KEYS);
                 PreparedStatement psEquipo = con.prepareStatement(sqlEquipo)) {

                psPrestamo.setString(1, prestamo.getSolicitante().getIdentificacion());
                psPrestamo.setString(2, prestamo.getEquipo().getCodigo());
                psPrestamo.setDate(3, Date.valueOf(prestamo.getFechaPrestamo()));
                psPrestamo.setDate(4, Date.valueOf(prestamo.getFechaDevolucionEsperada()));
                psPrestamo.setDate(5, null);
                psPrestamo.setString(6, prestamo.getEstado().name());
                psPrestamo.executeUpdate();

                try (ResultSet claves = psPrestamo.getGeneratedKeys()) {
                    if (claves.next()) {
                        prestamo.setId(claves.getInt(1));
                    }
                }

                psEquipo.setString(1, prestamo.getEquipo().getEstado().name());
                psEquipo.setString(2, prestamo.getEquipo().getCodigo());
                psEquipo.executeUpdate();

                con.commit();
                return prestamo;
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new DatosException("Error al registrar el prestamo; no se guardo ningun cambio", e);
        }
    }

    @Override
    public void actualizarConEstadoEquipo(Prestamo prestamo) {
        String sqlPrestamo = "UPDATE prestamo SET fecha_devolucion_real = ?, estado = ? WHERE id = ?";
        String sqlEquipo = "UPDATE equipo SET estado = ? WHERE codigo = ?";

        try (Connection con = ConexionBD.obtenerConexion()) {
            con.setAutoCommit(false);
            try (PreparedStatement psPrestamo = con.prepareStatement(sqlPrestamo);
                 PreparedStatement psEquipo = con.prepareStatement(sqlEquipo)) {

                if (prestamo.getFechaDevolucionReal() == null) {
                    psPrestamo.setDate(1, null);
                } else {
                    psPrestamo.setDate(1, Date.valueOf(prestamo.getFechaDevolucionReal()));
                }
                psPrestamo.setString(2, prestamo.getEstado().name());
                psPrestamo.setInt(3, prestamo.getId());
                psPrestamo.executeUpdate();

                psEquipo.setString(1, prestamo.getEquipo().getEstado().name());
                psEquipo.setString(2, prestamo.getEquipo().getCodigo());
                psEquipo.executeUpdate();

                con.commit();
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new DatosException("Error al registrar la devolucion; no se guardo ningun cambio", e);
        }
    }

    private Prestamo mapear(ResultSet rs) throws SQLException {
        Solicitante solicitante = new Solicitante(
                rs.getString("identificacion"),
                rs.getString("nombre"),
                rs.getString("correo"),
                TipoSolicitante.valueOf(rs.getString("tipo_solicitante")));

        Equipo equipo = new Equipo(
                rs.getString("codigo"),
                rs.getString("descripcion"),
                rs.getString("tipo_equipo"),
                EstadoEquipo.valueOf(rs.getString("estado_equipo")));

        Date real = rs.getDate("fecha_devolucion_real");

        return new Prestamo(
                rs.getInt("id"),
                solicitante,
                equipo,
                rs.getDate("fecha_prestamo").toLocalDate(),
                rs.getDate("fecha_devolucion_esperada").toLocalDate(),
                real == null ? null : real.toLocalDate(),
                EstadoPrestamo.valueOf(rs.getString("estado_prestamo")));
    }
}