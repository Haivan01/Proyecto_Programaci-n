package dao;

import dto.UsuarioDTO;
import model.Tecnico;
import model.Usuario;
import model.UsuarioFinal;

import java.sql.SQLException;
import java.util.List;

public interface UsuarioDAO {

    /** Valida credenciales y devuelve el usuario o null. */
    Usuario validar(String username, String password) throws SQLException;

    /** Registra un técnico (transacción atómica: usuarios + tecnicos). */
    void registrarTecnico(Tecnico t) throws SQLException;

    /** Registra un usuario final (transacción atómica: usuarios + usuarios_finales). */
    void registrarUsuarioFinal(UsuarioFinal uf) throws SQLException;

    /** Lista todos los usuarios con info de las tablas hijas (JOIN). */
    List<UsuarioDTO> listarTodos() throws SQLException;

    /** Devuelve solo técnicos (para combos de asignación). */
    List<Tecnico> listarTecnicos() throws SQLException;

    /** Devuelve solo usuarios finales (para combos de reportador). */
    List<UsuarioFinal> listarUsuariosFinales() throws SQLException;

    /** Actualiza campos básicos del usuario. */
    void actualizar(Usuario u) throws SQLException;

    /** Cambia la contraseña (validando la antigua). */
    boolean cambiarPassword(int idUsuario, String passwordActual, String passwordNueva) throws SQLException;

    /** Elimina (ON DELETE CASCADE se encarga de las tablas hijas y asignaciones). */
    void eliminar(int id) throws SQLException;

    /** Comprueba existencia (para validaciones de registro). */
    boolean existeUsername(String username) throws SQLException;
}
