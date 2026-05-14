package model;

import java.time.LocalDateTime;

/**
 * Entidad principal del dominio: incidencia técnica reportada en el IES.
 */
public class Incidencia {

    public enum Prioridad { baja, media, alta, critica }
    public enum Estado    { abierta, en_proceso, resuelta, cerrada }

    private int id;
    private String titulo;
    private String descripcion;
    private String ubicacion;
    private Prioridad prioridad;
    private Estado estado;
    private LocalDateTime fechaCreacion;
    private int reportadaPor; // FK -> usuarios_finales.usuario_id

    public Incidencia() {}

    public Incidencia(int id, String titulo, String descripcion, String ubicacion,
                      Prioridad prioridad, Estado estado,
                      LocalDateTime fechaCreacion, int reportadaPor) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.ubicacion = ubicacion;
        this.prioridad = prioridad;
        this.estado = estado;
        this.fechaCreacion = fechaCreacion;
        this.reportadaPor = reportadaPor;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }

    public Prioridad getPrioridad() { return prioridad; }
    public void setPrioridad(Prioridad prioridad) { this.prioridad = prioridad; }

    public Estado getEstado() { return estado; }
    public void setEstado(Estado estado) { this.estado = estado; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public int getReportadaPor() { return reportadaPor; }
    public void setReportadaPor(int reportadaPor) { this.reportadaPor = reportadaPor; }

    @Override
    public String toString() { return "#" + id + " - " + titulo; }
}
