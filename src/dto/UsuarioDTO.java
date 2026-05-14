package dto;

/**
 * DTO para listar usuarios uniendo info de las tablas hijas (técnicos / usuarios_finales).
 */
public class UsuarioDTO {

    private int id;
    private String username;
    private String nombreCompleto;
    private String email;
    private String dni;
    private String rol;
    private String infoExtra;  // especialidad (tec) o departamento+ubicación (uf)

    public UsuarioDTO(int id, String username, String nombreCompleto, String email,
                      String dni, String rol, String infoExtra) {
        this.id = id;
        this.username = username;
        this.nombreCompleto = nombreCompleto;
        this.email = email;
        this.dni = dni;
        this.rol = rol;
        this.infoExtra = infoExtra;
    }

    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getNombreCompleto() { return nombreCompleto; }
    public String getEmail() { return email; }
    public String getDni() { return dni; }
    public String getRol() { return rol; }
    public String getInfoExtra() { return infoExtra; }
}
