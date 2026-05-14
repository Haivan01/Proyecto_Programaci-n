package model;

import java.time.LocalDate;

/**
 * Tabla N:M entre técnicos e incidencias.
 * Incluye campos extra: fecha, observaciones y estado de la asignación.
 */
public class Asignacion {

    public enum EstadoAsignacion { pendiente, en_curso, completada }

    private int id;
    private int tecnicoId;
    private int incidenciaId;
    private LocalDate fechaAsignacion;
    private String observaciones;
    private EstadoAsignacion estado;

    public Asignacion() {}

    public Asignacion(int id, int tecnicoId, int incidenciaId,
                      LocalDate fechaAsignacion, String observaciones,
                      EstadoAsignacion estado) {
        this.id = id;
        this.tecnicoId = tecnicoId;
        this.incidenciaId = incidenciaId;
        this.fechaAsignacion = fechaAsignacion;
        this.observaciones = observaciones;
        this.estado = estado;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getTecnicoId() { return tecnicoId; }
    public void setTecnicoId(int tecnicoId) { this.tecnicoId = tecnicoId; }

    public int getIncidenciaId() { return incidenciaId; }
    public void setIncidenciaId(int incidenciaId) { this.incidenciaId = incidenciaId; }

    public LocalDate getFechaAsignacion() { return fechaAsignacion; }
    public void setFechaAsignacion(LocalDate fechaAsignacion) { this.fechaAsignacion = fechaAsignacion; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public EstadoAsignacion getEstado() { return estado; }
    public void setEstado(EstadoAsignacion estado) { this.estado = estado; }
}
