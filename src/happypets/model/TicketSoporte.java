package happypets.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Modelo para tickets de incidencias y soporte técnico de la clínica (Módulo 10).
 */
public class TicketSoporte implements Serializable {
    private static final long serialVersionUID = 1L;

    private String idTicket;
    private String asunto;
    private String categoria;
    private String prioridad;
    private String estado; // "Abierto", "En Proceso", "Cerrado"
    private String usuarioReporta;
    private LocalDateTime fechaCreacion;
    private String descripcion;
    private String respuestaTecnica;

    public TicketSoporte() {
    }

    public TicketSoporte(String idTicket, String asunto, String categoria, String prioridad,
                         String estado, String usuarioReporta, LocalDateTime fechaCreacion,
                         String descripcion, String respuestaTecnica) {
        this.idTicket = idTicket;
        this.asunto = asunto;
        this.categoria = categoria;
        this.prioridad = prioridad;
        this.estado = estado;
        this.usuarioReporta = usuarioReporta;
        this.fechaCreacion = fechaCreacion;
        this.descripcion = descripcion;
        this.respuestaTecnica = respuestaTecnica;
    }

    public String getIdTicket() {
        return idTicket;
    }

    public void setIdTicket(String idTicket) {
        this.idTicket = idTicket;
    }

    public String getAsunto() {
        return asunto;
    }

    public void setAsunto(String asunto) {
        this.asunto = asunto;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(String prioridad) {
        this.prioridad = prioridad;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getUsuarioReporta() {
        return usuarioReporta;
    }

    public void setUsuarioReporta(String usuarioReporta) {
        this.usuarioReporta = usuarioReporta;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public String getFechaCreacionFormateada() {
        if (fechaCreacion == null) return "N/A";
        return fechaCreacion.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getRespuestaTecnica() {
        return respuestaTecnica;
    }

    public void setRespuestaTecnica(String respuestaTecnica) {
        this.respuestaTecnica = respuestaTecnica;
    }
}
