package dao;

import db.ConexionDB;
import dto.AsignacionDTO;
import model.Asignacion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AsignacionDAOImpl implements AsignacionDAO {

    @Override
    public void insertar(Asignacion a) throws SQLException {
        String sql =
                "INSERT INTO asignaciones (tecnico_id, incidencia_id, fecha_asignacion, " +
                "       observaciones, estado_asignacion) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, a.getTecnicoId());
            ps.setInt(2, a.getIncidenciaId());
            ps.setDate(3, Date.valueOf(a.getFechaAsignacion()));
            ps.setString(4, a.getObservaciones());
            ps.setString(5, a.getEstado().name());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) a.setId(keys.getInt(1));
            }
        }
    }

    @Override
    public List<AsignacionDTO> listarTodos() throws SQLException {
        String sql =
                "SELECT a.id, " +
                "       u.nombre AS tec_nombre, u.apellidos AS tec_apellidos, t.especialidad, " +
                "       i.titulo AS inc_titulo, " +
                "       a.fecha_asignacion, a.observaciones, a.estado_asignacion " +
                "FROM asignaciones a " +
                "JOIN tecnicos t       ON a.tecnico_id    = t.usuario_id " +
                "JOIN usuarios u       ON t.usuario_id    = u.id " +
                "JOIN incidencias i    ON a.incidencia_id = i.id " +
                "ORDER BY a.fecha_asignacion DESC";
        List<AsignacionDTO> res = new ArrayList<>();
        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String tec = rs.getString("tec_nombre") + " " + rs.getString("tec_apellidos")
                           + " (" + rs.getString("especialidad") + ")";
                res.add(new AsignacionDTO(
                        rs.getInt("id"),
                        tec,
                        rs.getString("inc_titulo"),
                        rs.getDate("fecha_asignacion").toString(),
                        rs.getString("observaciones"),
                        rs.getString("estado_asignacion")
                ));
            }
        }
        return res;
    }

    @Override
    public void actualizarEstado(int id, Asignacion.EstadoAsignacion nuevoEstado) throws SQLException {
        String sql = "UPDATE asignaciones SET estado_asignacion = ? WHERE id = ?";
        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nuevoEstado.name());
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    @Override
    public void eliminar(int id) throws SQLException {
        String sql = "DELETE FROM asignaciones WHERE id = ?";
        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}
