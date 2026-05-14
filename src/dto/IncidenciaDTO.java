package dto;

/**
 * DTO usado al listar incidencias con JOIN a usuarios_finales/usuarios.
 * Resuelve la relación FK con el nombre legible del reportador.
 */
public class IncidenciaDTO {

    private int id;
    private String titulo;
    private String ubicacion;
    private String prioridad;
    private String estado;
    private String fechaCreacion;
    private String reportador;   // nombre completo + departamento

    public IncidenciaDTO(int id, String titulo, String ubicacion, String prioridad,
                         String estado, String fechaCreacion, String reportador) {
        this.id = id;
        this.titulo = titulo;
        this.ubicacion = ubicacion;
        this.prioridad = prioridad;
        this.estado = estado;
        this.fechaCreacion = fechaCreacion;
        this.reportador = reportador;
    }

    public int getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getUbicacion() { return ubicacion; }
    public String getPrioridad() { return prioridad; }
    public String getEstado() { return estado; }
    public String getFechaCreacion() { return fechaCreacion; }
    public String getReportador() { return reportador; }
}
