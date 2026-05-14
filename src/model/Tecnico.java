package model;

/**
 * Tabla hija 'tecnicos'. Extiende Usuario añadiendo
 * especialidad y teléfono.
 */
public class Tecnico extends Usuario {

    private String especialidad;
    private String telefono;

    public Tecnico() { super(); }

    public Tecnico(Usuario u, String especialidad, String telefono) {
        super(u.getId(), u.getUsername(), u.getPassword(), u.getEmail(),
              u.getNombre(), u.getApellidos(), u.getDni(), u.getRol());
        this.especialidad = especialidad;
        this.telefono = telefono;
    }

    public String getEspecialidad() { return especialidad; }
    public void setEspecialidad(String especialidad) { this.especialidad = especialidad; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
}
