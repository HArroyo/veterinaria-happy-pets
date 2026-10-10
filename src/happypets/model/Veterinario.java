package happypets.model;

import java.io.Serializable;

/**
 * Modelo para Especialistas Veterinarios Clínicos Colegiados.
 * Módulo 7.1: Personal y RRHH - Nuestros Veterinarios.
 */
public class Veterinario implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String nombreCompleto;
    private String colegiaturaCMPV;
    private String especialidad;
    private int aniosExperiencia;
    private String descripcionBio;
    private String diasAtencion;
    private String horarioTurno;
    private String proximaCitaLibre;
    private String estadoDisponibilidad; // "Disponible hoy", "En consulta", "En cirugía", "Turno Tarde", "Descanso"
    private String telefono;
    private String email;
    private double calificacionEstrellas;
    private int totalAtencionesRealizadas;
    private String consultorioHabitual;

    public Veterinario() {
    }

    public Veterinario(String id, String nombreCompleto, String colegiaturaCMPV, String especialidad,
                       int aniosExperiencia, String descripcionBio, String diasAtencion,
                       String horarioTurno, String proximaCitaLibre, String estadoDisponibilidad,
                       String telefono, String email, double calificacionEstrellas,
                       int totalAtencionesRealizadas, String consultorioHabitual) {
        this.id = id;
        this.nombreCompleto = nombreCompleto;
        this.colegiaturaCMPV = colegiaturaCMPV;
        this.especialidad = especialidad;
        this.aniosExperiencia = aniosExperiencia;
        this.descripcionBio = descripcionBio;
        this.diasAtencion = diasAtencion;
        this.horarioTurno = horarioTurno;
        this.proximaCitaLibre = proximaCitaLibre;
        this.estadoDisponibilidad = estadoDisponibilidad;
        this.telefono = telefono;
        this.email = email;
        this.calificacionEstrellas = calificacionEstrellas;
        this.totalAtencionesRealizadas = totalAtencionesRealizadas;
        this.consultorioHabitual = consultorioHabitual;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getColegiaturaCMPV() {
        return colegiaturaCMPV;
    }

    public void setColegiaturaCMPV(String colegiaturaCMPV) {
        this.colegiaturaCMPV = colegiaturaCMPV;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public int getAniosExperiencia() {
        return aniosExperiencia;
    }

    public void setAniosExperiencia(int aniosExperiencia) {
        this.aniosExperiencia = aniosExperiencia;
    }

    public String getDescripcionBio() {
        return descripcionBio;
    }

    public void setDescripcionBio(String descripcionBio) {
        this.descripcionBio = descripcionBio;
    }

    public String getDiasAtencion() {
        return diasAtencion;
    }

    public void setDiasAtencion(String diasAtencion) {
        this.diasAtencion = diasAtencion;
    }

    public String getHorarioTurno() {
        return horarioTurno;
    }

    public void setHorarioTurno(String horarioTurno) {
        this.horarioTurno = horarioTurno;
    }

    public String getProximaCitaLibre() {
        return proximaCitaLibre;
    }

    public void setProximaCitaLibre(String proximaCitaLibre) {
        this.proximaCitaLibre = proximaCitaLibre;
    }

    public String getEstadoDisponibilidad() {
        return estadoDisponibilidad;
    }

    public void setEstadoDisponibilidad(String estadoDisponibilidad) {
        this.estadoDisponibilidad = estadoDisponibilidad;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public double getCalificacionEstrellas() {
        return calificacionEstrellas;
    }

    public void setCalificacionEstrellas(double calificacionEstrellas) {
        this.calificacionEstrellas = calificacionEstrellas;
    }

    public int getTotalAtencionesRealizadas() {
        return totalAtencionesRealizadas;
    }

    public void setTotalAtencionesRealizadas(int totalAtencionesRealizadas) {
        this.totalAtencionesRealizadas = totalAtencionesRealizadas;
    }

    public String getConsultorioHabitual() {
        return consultorioHabitual;
    }

    public void setConsultorioHabitual(String consultorioHabitual) {
        this.consultorioHabitual = consultorioHabitual;
    }

    @Override
    public String toString() {
        return nombreCompleto + " (" + especialidad + ")";
    }
}
