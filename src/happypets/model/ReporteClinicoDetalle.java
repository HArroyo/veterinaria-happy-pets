package happypets.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Modelo para las filas del listado de reportes clínicos del Módulo 8.
 */
public class ReporteClinicoDetalle implements Serializable {
    private static final long serialVersionUID = 1L;

    private String codigoReporte; // Ej: "RC-2023-0941"
    private LocalDate fecha;
    private String nombrePaciente;
    private String especieRaza;
    private String nombrePropietario;
    private String telefonoPropietario;
    private String veterinarioTratante;
    private String diagnosticoConfirmado;
    private String severidad; // "Leve", "Moderada", "Grave"
    private int duracionMinutos;
    private boolean seguimientoActivo;
    private String tratamiento;
    private String observaciones;

    public ReporteClinicoDetalle() {
    }

    public ReporteClinicoDetalle(String codigoReporte, LocalDate fecha, String nombrePaciente,
                                 String especieRaza, String nombrePropietario, String telefonoPropietario,
                                 String veterinarioTratante, String diagnosticoConfirmado, String severidad,
                                 int duracionMinutos, boolean seguimientoActivo, String tratamiento,
                                 String observaciones) {
        this.codigoReporte = codigoReporte;
        this.fecha = fecha;
        this.nombrePaciente = nombrePaciente;
        this.especieRaza = especieRaza;
        this.nombrePropietario = nombrePropietario;
        this.telefonoPropietario = telefonoPropietario;
        this.veterinarioTratante = veterinarioTratante;
        this.diagnosticoConfirmado = diagnosticoConfirmado;
        this.severidad = severidad;
        this.duracionMinutos = duracionMinutos;
        this.seguimientoActivo = seguimientoActivo;
        this.tratamiento = tratamiento;
        this.observaciones = observaciones;
    }

    public String getCodigoReporte() {
        return codigoReporte;
    }

    public void setCodigoReporte(String codigoReporte) {
        this.codigoReporte = codigoReporte;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getFechaFormateada() {
        if (fecha == null) return "";
        return fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    public String getNombrePaciente() {
        return nombrePaciente;
    }

    public void setNombrePaciente(String nombrePaciente) {
        this.nombrePaciente = nombrePaciente;
    }

    public String getEspecieRaza() {
        return especieRaza;
    }

    public void setEspecieRaza(String especieRaza) {
        this.especieRaza = especieRaza;
    }

    public String getNombrePropietario() {
        return nombrePropietario;
    }

    public void setNombrePropietario(String nombrePropietario) {
        this.nombrePropietario = nombrePropietario;
    }

    public String getTelefonoPropietario() {
        return telefonoPropietario;
    }

    public void setTelefonoPropietario(String telefonoPropietario) {
        this.telefonoPropietario = telefonoPropietario;
    }

    public String getVeterinarioTratante() {
        return veterinarioTratante;
    }

    public void setVeterinarioTratante(String veterinarioTratante) {
        this.veterinarioTratante = veterinarioTratante;
    }

    public String getDiagnosticoConfirmado() {
        return diagnosticoConfirmado;
    }

    public void setDiagnosticoConfirmado(String diagnosticoConfirmado) {
        this.diagnosticoConfirmado = diagnosticoConfirmado;
    }

    public String getSeveridad() {
        return severidad;
    }

    public void setSeveridad(String severidad) {
        this.severidad = severidad;
    }

    public int getDuracionMinutos() {
        return duracionMinutos;
    }

    public void setDuracionMinutos(int duracionMinutos) {
        this.duracionMinutos = duracionMinutos;
    }

    public boolean isSeguimientoActivo() {
        return seguimientoActivo;
    }

    public void setSeguimientoActivo(boolean seguimientoActivo) {
        this.seguimientoActivo = seguimientoActivo;
    }

    public String getTratamiento() {
        return tratamiento;
    }

    public void setTratamiento(String tratamiento) {
        this.tratamiento = tratamiento;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
}
