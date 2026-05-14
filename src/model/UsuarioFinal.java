package model;

/**
 * Tabla hija 'usuarios_finales'. Personal del IES que reporta incidencias.
 */
public class UsuarioFinal extends Usuario {

    private String departamento;
    private String ubicacion;

    public UsuarioFinal() { super(); }

    public UsuarioFinal(Usuario u, String departamento, String ubicacion) {
        super(u.getId(), u.getUsername(), u.getPassword(), u.getEmail(),
              u.getNombre(), u.getApellidos(), u.getDni(), u.getRol());
        this.departamento = departamento;
        this.ubicacion = ubicacion;
    }

    public String getDepartamento() { return departamento; }
    public void setDepartamento(String departamento) { this.departamento = departamento; }

    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }
}
