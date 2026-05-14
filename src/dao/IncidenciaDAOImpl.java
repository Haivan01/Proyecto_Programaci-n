package dao;

import db.ConexionDB;
import dto.IncidenciaDTO;
import model.Incidencia;

import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class IncidenciaDAOImpl implements IncidenciaDAO {

    private static final DateTimeFormatter DT_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Override
    public void insertar(Incidencia i) throws SQLException {
        String sql =
                "INSERT INTO incidencias (titulo, descripcion, ubicacion, prioridad, estado, reportada_por) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, i.getTitulo());
            ps.setString(2, i.getDescripcion());
            ps.setString(3, i.getUbicacion());
            ps.setString(4, i.getPrioridad().name());
            ps.setString(5, i.getEstado().name());
            ps.setInt(6, i.getReportadaPor());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) i.setId(keys.getInt(1));
            }
        }
    }

    @Override
    public void actualizar(Incidencia i) throws SQLException {
        String sql =
                "UPDATE incidencias SET titulo = ?, descripcion = ?, ubicacion = ?, " +
                "       prioridad = ?, estado = ?, reportada_por = ? WHERE id = ?";
        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, i.getTitulo());
            ps.setString(2, i.getDescripcion());
            ps.setString(3, i.getUbicacion());
            ps.setString(4, i.getPrioridad().name());
            ps.setString(5, i.getEstado().name());
            ps.setInt(6, i.getReportadaPor());
            ps.setInt(7, i.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public List<IncidenciaDTO> listarTodos() throws SQLException {
        String sql =
                "SELECT i.id, i.titulo, i.ubicacion, i.prioridad, i.estado, i.fecha_creacion, " +
                "       u.nombre, u.apellidos, uf.departamento " +
                "FROM incidencias i " +
                "JOIN usuarios_finales uf ON i.reportada_por = uf.usuario_id " +
                "JOIN usuarios u ON uf.usuario_id = u.id " +
                "ORDER BY i.fecha_creacion DESC";
        List<IncidenciaDTO> res = new ArrayList<>();
        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String reportador = rs.getString("nombre") + " " + rs.getString("apellidos")
                                  + " [" + rs.getString("departamento") + "]";
                Timestamp ts = rs.getTimestamp("fecha_creacion");
                String fecha = ts != null ? ts.toLocalDateTime().format(DT_FMT) : "";
                res.add(new IncidenciaDTO(
                        rs.getInt("id"),
                        rs.getString("titulo"),
                        rs.getString("ubicacion"),
                        rs.getString("prioridad"),
                        rs.getString("estado"),
                        fecha,
                        reportador
                ));
            }
        }
        return res;
    }

    @Override
    public List<Incidencia> listarBasico() throws SQLException {
        String sql =
                "SELECT id, titulo, descripcion, ubicacion, prioridad, estado, " +
                "       fecha_creacion, reportada_por FROM incidencias ORDER BY id";
        List<Incidencia> res = new ArrayList<>();
        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Timestamp ts = rs.getTimestamp("fecha_creacion");
                res.add(new Incidencia(
                        rs.getInt("id"),
                        rs.getString("titulo"),
                        rs.getString("descripcion"),
                        rs.getString("ubicacion"),
                        Incidencia.Prioridad.valueOf(rs.getString("prioridad")),
                        Incidencia.Estado.valueOf(rs.getString("estado")),
                        ts != null ? ts.toLocalDateTime() : null,
                        rs.getInt("reportada_por")
                ));
            }
        }
        return res;
    }

    @Override
    public void eliminar(int id) throws SQLException {
        String sql = "DELETE FROM incidencias WHERE id = ?";
        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}
