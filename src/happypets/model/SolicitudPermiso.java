package happypets.model;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Modelo para Solicitudes de Permisos, Licencias y Coberturas.
 * Módulo 7.4: Personal y RRHH - Asistencias y Permisos.
 */
public class SolicitudPermiso implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id; // ej. "P-184"
    private String personalId;
    private String nombreSolicitante;
    private String rol;
    private String tipoPermiso; // "Permiso médico", "Capacitación / Congreso", "Asunto personal", "Compensación de guardia"
    private LocalDate fecha;
    private String turnoAfectado; // ej. "Tarde (15:00 - 22:00)"
    private String reemplazoPropuesto; // ej. "Dr. Carlos Méndez"
    private String motivoDetalle;
    private String estado; // "Pendiente", "Aprobado", "Rechazado"

    public SolicitudPermiso() {
    }

    public SolicitudPermiso(String id, String personalId, String nombreSolicitante, String rol,
                            String tipoPermiso, LocalDate fecha, String turnoAfectado,
                            String reemplazoPropuesto, String motivoDetalle, String estado) {
        this.id = id;
        this.personalId = personalId;
        this.nombreSolicitante = nombreSolicitante;
        this.rol = rol;
        this.tipoPermiso = tipoPermiso;
        this.fecha = fecha;
        this.turnoAfectado = turnoAfectado;
        this.reemplazoPropuesto = reemplazoPropuesto;
        this.motivoDetalle = motivoDetalle;
        this.estado = estado;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPersonalId() {
        return personalId;
    }

    public void setPersonalId(String personalId) {
        this.personalId = personalId;
    }

    public String getNombreSolicitante() {
        return nombreSolicitante;
    }

    public void setNombreSolicitante(String nombreSolicitante) {
        this.nombreSolicitante = nombreSolicitante;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getTipoPermiso() {
        return tipoPermiso;
    }

    public void setTipoPermiso(String tipoPermiso) {
        this.tipoPermiso = tipoPermiso;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getTurnoAfectado() {
        return turnoAfectado;
    }

    public void setTurnoAfectado(String turnoAfectado) {
        this.turnoAfectado = turnoAfectado;
    }

    public String getReemplazoPropuesto() {
        return reemplazoPropuesto;
    }

    public void setReemplazoPropuesto(String reemplazoPropuesto) {
        this.reemplazoPropuesto = reemplazoPropuesto;
    }

    public String getMotivoDetalle() {
        return motivoDetalle;
    }

    public void setMotivoDetalle(String motivoDetalle) {
        this.motivoDetalle = motivoDetalle;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return id + " · " + nombreSolicitante + " (" + tipoPermiso + ")";
    }
}
