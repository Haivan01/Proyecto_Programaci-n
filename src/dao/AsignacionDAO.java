package dao;

import dto.AsignacionDTO;
import model.Asignacion;

import java.sql.SQLException;
import java.util.List;

public interface AsignacionDAO {

    void insertar(Asignacion a) throws SQLException;

    /** Listado con JOIN técnico+incidencia. */
    List<AsignacionDTO> listarTodos() throws SQLException;

    void actualizarEstado(int id, Asignacion.EstadoAsignacion nuevoEstado) throws SQLException;

    void eliminar(int id) throws SQLException;
}
