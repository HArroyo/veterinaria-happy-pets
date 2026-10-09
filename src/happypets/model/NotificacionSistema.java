package happypets.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Modelo para las notificaciones, alertas y mensajes del Centro de Notificaciones (Módulo 9).
 */
public class NotificacionSistema implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String tipo;       // Ej: "NUEVA ALERTA", "MENSAJE IMPORTANTE", "ALERTA DE SISTEMA", "NUEVO MENSAJE"
    private String categoria;  // "Alerta", "Mensaje", "Evento"
    private String titulo;
    private String mensaje;
    private LocalDateTime fecha;
    private String tiempoRelativo; // Ej: "Hace 5 min", "Hace 2 horas", "Ayer", "Hace 2 días"
    private boolean leida;
    private String prioridad;  // "Alta", "Media", "Baja"
    private String origen;     // "Seguridad", "Servidor", "Almacenamiento", "Auditoría"

    public NotificacionSistema() {
    }

    public NotificacionSistema(String id, String tipo, String categoria, String titulo, String mensaje,
                               LocalDateTime fecha, String tiempoRelativo, boolean leida,
                               String prioridad, String origen) {
        this.id = id;
        this.tipo = tipo;
        this.categoria = categoria;
        this.titulo = titulo;
        this.mensaje = mensaje;
        this.fecha = fecha;
        this.tiempoRelativo = tiempoRelativo;
        this.leida = leida;
        this.prioridad = prioridad;
        this.origen = origen;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public String getFechaFormateada() {
        if (fecha == null) return "";
        return fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    public String getTiempoRelativo() {
        return tiempoRelativo;
    }

    public void setTiempoRelativo(String tiempoRelativo) {
        this.tiempoRelativo = tiempoRelativo;
    }

    public boolean isLeida() {
        return leida;
    }

    public void setLeida(boolean leida) {
        this.leida = leida;
    }

    public String getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(String prioridad) {
        this.prioridad = prioridad;
    }

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }
}
