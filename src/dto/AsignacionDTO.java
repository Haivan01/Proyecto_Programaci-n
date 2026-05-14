package dto;

/**
 * DTO usado al listar asignaciones con JOIN a técnicos, usuarios e incidencias.
 */
public class AsignacionDTO {

    private int id;
    private String tecnico;        // nombre completo + especialidad
    private String incidencia;     // título de la incidencia
    private String fechaAsignacion;
    private String observaciones;
    private String estado;

    public AsignacionDTO(int id, String tecnico, String incidencia,
                         String fechaAsignacion, String observaciones, String estado) {
        this.id = id;
        this.tecnico = tecnico;
        this.incidencia = incidencia;
        this.fechaAsignacion = fechaAsignacion;
        this.observaciones = observaciones;
        this.estado = estado;
    }

    public int getId() { return id; }
    public String getTecnico() { return tecnico; }
    public String getIncidencia() { return incidencia; }
    public String getFechaAsignacion() { return fechaAsignacion; }
    public String getObservaciones() { return observaciones; }
    public String getEstado() { return estado; }
}
