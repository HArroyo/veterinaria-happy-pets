package happypets.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Modelo para los eventos de trazabilidad y logs de auditoría (Módulo 9).
 */
public class LogAuditoria implements Serializable {
    private static final long serialVersionUID = 1L;

    private String idEvento;     // "#EV-1042"
    private String tipoEvento;   // "Modificación de Canales", "Descarga de Contrato.pdf", etc.
    private String usuario;      // "admin_user", "jgarcia", "mlopez", "desconocido"
    private LocalDateTime fechaHora;
    private String ipOrigen;     // "192.168.1.10"
    private String estado;       // "ÉXITO", "FALLIDO"
    private String detalles;

    public LogAuditoria() {
    }

    public LogAuditoria(String idEvento, String tipoEvento, String usuario,
                        LocalDateTime fechaHora, String ipOrigen, String estado, String detalles) {
        this.idEvento = idEvento;
        this.tipoEvento = tipoEvento;
        this.usuario = usuario;
        this.fechaHora = fechaHora;
        this.ipOrigen = ipOrigen;
        this.estado = estado;
        this.detalles = detalles;
    }

    public String getIdEvento() {
        return idEvento;
    }

    public void setIdEvento(String idEvento) {
        this.idEvento = idEvento;
    }

    public String getTipoEvento() {
        return tipoEvento;
    }

    public void setTipoEvento(String tipoEvento) {
        this.tipoEvento = tipoEvento;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getFechaHoraFormateada() {
        if (fechaHora == null) return "";
        return fechaHora.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public String getIpOrigen() {
        return ipOrigen;
    }

    public void setIpOrigen(String ipOrigen) {
        this.ipOrigen = ipOrigen;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getDetalles() {
        return detalles;
    }

    public void setDetalles(String detalles) {
        this.detalles = detalles;
    }
}
