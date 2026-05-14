package dao;

import dto.IncidenciaDTO;
import model.Incidencia;

import java.sql.SQLException;
import java.util.List;

public interface IncidenciaDAO {

    void insertar(Incidencia i) throws SQLException;

    void actualizar(Incidencia i) throws SQLException;

    /** Lista con JOIN para incluir el nombre del reportador. */
    List<IncidenciaDTO> listarTodos() throws SQLException;

    /** Lista cruda (sin JOIN) para combos. */
    List<Incidencia> listarBasico() throws SQLException;

    void eliminar(int id) throws SQLException;
}
