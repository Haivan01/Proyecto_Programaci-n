package dao;

import db.ConexionDB;
import dto.UsuarioDTO;
import model.Tecnico;
import model.Usuario;
import model.UsuarioFinal;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAOImpl implements UsuarioDAO {

    @Override
    public Usuario validar(String username, String password) throws SQLException {
        String sql = "SELECT id, username, password, email, nombre, apellidos, dni, rol " +
                     "FROM usuarios WHERE username = ? AND password = ?";
        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Usuario(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("email"),
                            rs.getString("nombre"),
                            rs.getString("apellidos"),
                            rs.getString("dni"),
                            Usuario.Rol.valueOf(rs.getString("rol"))
                    );
                }
                return null;
            }
        }
    }

    @Override
    public void registrarTecnico(Tecnico t) throws SQLException {
        String sqlUser =
                "INSERT INTO usuarios (username, password, email, nombre, apellidos, dni, rol) " +
                "VALUES (?, ?, ?, ?, ?, ?, 'tecnico')";
        String sqlTec =
                "INSERT INTO tecnicos (usuario_id, especialidad, telefono) VALUES (?, ?, ?)";

        Connection con = null;
        try {
            con = ConexionDB.conectar();
            con.setAutoCommit(false);

            int newId;
            try (PreparedStatement ps = con.prepareStatement(sqlUser, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, t.getUsername());
                ps.setString(2, t.getPassword());
                ps.setString(3, t.getEmail());
                ps.setString(4, t.getNombre());
                ps.setString(5, t.getApellidos());
                ps.setString(6, t.getDni());
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (!keys.next()) throw new SQLException("No se generó ID de usuario");
                    newId = keys.getInt(1);
                }
            }
            try (PreparedStatement ps = con.prepareStatement(sqlTec)) {
                ps.setInt(1, newId);
                ps.setString(2, t.getEspecialidad());
                ps.setString(3, t.getTelefono());
                ps.executeUpdate();
            }
            con.commit();
            t.setId(newId);
        } catch (SQLException e) {
            if (con != null) con.rollback();
            throw e;
        } finally {
            if (con != null) { con.setAutoCommit(true); con.close(); }
        }
    }

    @Override
    public void registrarUsuarioFinal(UsuarioFinal uf) throws SQLException {
        String sqlUser =
                "INSERT INTO usuarios (username, password, email, nombre, apellidos, dni, rol) " +
                "VALUES (?, ?, ?, ?, ?, ?, 'usuario_final')";
        String sqlUF =
                "INSERT INTO usuarios_finales (usuario_id, departamento, ubicacion) VALUES (?, ?, ?)";

        Connection con = null;
        try {
            con = ConexionDB.conectar();
            con.setAutoCommit(false);

            int newId;
            try (PreparedStatement ps = con.prepareStatement(sqlUser, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, uf.getUsername());
                ps.setString(2, uf.getPassword());
                ps.setString(3, uf.getEmail());
                ps.setString(4, uf.getNombre());
                ps.setString(5, uf.getApellidos());
                ps.setString(6, uf.getDni());
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (!keys.next()) throw new SQLException("No se generó ID de usuario");
                    newId = keys.getInt(1);
                }
            }
            try (PreparedStatement ps = con.prepareStatement(sqlUF)) {
                ps.setInt(1, newId);
                ps.setString(2, uf.getDepartamento());
                ps.setString(3, uf.getUbicacion());
                ps.executeUpdate();
            }
            con.commit();
            uf.setId(newId);
        } catch (SQLException e) {
            if (con != null) con.rollback();
            throw e;
        } finally {
            if (con != null) { con.setAutoCommit(true); con.close(); }
        }
    }

    @Override
    public List<UsuarioDTO> listarTodos() throws SQLException {
        String sql =
                "SELECT u.id, u.username, u.nombre, u.apellidos, u.email, u.dni, u.rol, " +
                "       t.especialidad, uf.departamento, uf.ubicacion " +
                "FROM usuarios u " +
                "LEFT JOIN tecnicos t ON u.id = t.usuario_id " +
                "LEFT JOIN usuarios_finales uf ON u.id = uf.usuario_id " +
                "ORDER BY u.id";
        List<UsuarioDTO> res = new ArrayList<>();
        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String rol = rs.getString("rol");
                String extra;
                if ("tecnico".equals(rol)) {
                    extra = "Especialidad: " + rs.getString("especialidad");
                } else if ("usuario_final".equals(rol)) {
                    extra = rs.getString("departamento") + " - " + rs.getString("ubicacion");
                } else {
                    extra = "—";
                }
                res.add(new UsuarioDTO(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("nombre") + " " + rs.getString("apellidos"),
                        rs.getString("email"),
                        rs.getString("dni"),
                        rol,
                        extra
                ));
            }
        }
        return res;
    }

    @Override
    public List<Tecnico> listarTecnicos() throws SQLException {
        String sql =
                "SELECT u.id, u.username, u.password, u.email, u.nombre, u.apellidos, " +
                "       u.dni, u.rol, t.especialidad, t.telefono " +
                "FROM usuarios u JOIN tecnicos t ON u.id = t.usuario_id ORDER BY u.apellidos";
        List<Tecnico> res = new ArrayList<>();
        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Usuario u = new Usuario(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("email"),
                        rs.getString("nombre"),
                        rs.getString("apellidos"),
                        rs.getString("dni"),
                        Usuario.Rol.tecnico
                );
                res.add(new Tecnico(u, rs.getString("especialidad"), rs.getString("telefono")));
            }
        }
        return res;
    }

    @Override
    public List<UsuarioFinal> listarUsuariosFinales() throws SQLException {
        String sql =
                "SELECT u.id, u.username, u.password, u.email, u.nombre, u.apellidos, " +
                "       u.dni, u.rol, uf.departamento, uf.ubicacion " +
                "FROM usuarios u JOIN usuarios_finales uf ON u.id = uf.usuario_id ORDER BY u.apellidos";
        List<UsuarioFinal> res = new ArrayList<>();
        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Usuario u = new Usuario(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("email"),
                        rs.getString("nombre"),
                        rs.getString("apellidos"),
                        rs.getString("dni"),
                        Usuario.Rol.usuario_final
                );
                res.add(new UsuarioFinal(u, rs.getString("departamento"), rs.getString("ubicacion")));
            }
        }
        return res;
    }

    @Override
    public void actualizar(Usuario u) throws SQLException {
        String sql =
                "UPDATE usuarios SET email = ?, nombre = ?, apellidos = ?, dni = ? WHERE id = ?";
        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, u.getEmail());
            ps.setString(2, u.getNombre());
            ps.setString(3, u.getApellidos());
            ps.setString(4, u.getDni());
            ps.setInt(5, u.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public boolean cambiarPassword(int idUsuario, String passwordActual, String passwordNueva) throws SQLException {
        String check = "SELECT id FROM usuarios WHERE id = ? AND password = ?";
        String upd = "UPDATE usuarios SET password = ? WHERE id = ?";
        try (Connection con = ConexionDB.conectar()) {
            try (PreparedStatement ps = con.prepareStatement(check)) {
                ps.setInt(1, idUsuario);
                ps.setString(2, passwordActual);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) return false;
                }
            }
            try (PreparedStatement ps = con.prepareStatement(upd)) {
                ps.setString(1, passwordNueva);
                ps.setInt(2, idUsuario);
                return ps.executeUpdate() == 1;
            }
        }
    }

    @Override
    public void eliminar(int id) throws SQLException {
        String sql = "DELETE FROM usuarios WHERE id = ?";
        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public boolean existeUsername(String username) throws SQLException {
        String sql = "SELECT 1 FROM usuarios WHERE username = ?";
        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }
}