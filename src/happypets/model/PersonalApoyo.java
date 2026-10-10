package happypets.model;

import java.io.Serializable;

/**
 * Modelo para Personal de Apoyo y Colaboradores Asistenciales/Operativos.
 * Módulo 7.2: Personal y RRHH - Personal de Apoyo.
 * (ATV Auxiliares Técnicos, Recepción, Peluquería / Estética, Administración, Mantenimiento).
 */
public class PersonalApoyo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String nombreCompleto;
    private String cargo; // "Auxiliar Técnico Veterinario (ATV)", "Coordinador de Recepción", "Estilista Canino/Felino", etc.
    private String categoria; // "Auxiliares", "Recepción", "Peluquería", "Administración", "Mantenimiento"
    private String areaAsignada; // ej. "Hospitalización y Quirófano B", "Mostrador Principal", "Grooming Spa"
    private String extensionInterna; // ej. "Ext. 204"
    private String turno; // "Mañana (07:00 - 15:00)", "Tarde (14:30 - 22:00)", "Noche (21:30 - 07:30)"
    private String estado; // "En turno", "Turno Tarde", "Descanso", "Permiso"
    private String telefono;
    private String email;
    private String certificaciones;

    public PersonalApoyo() {
    }

    public PersonalApoyo(String id, String nombreCompleto, String cargo, String categoria,
                         String areaAsignada, String extensionInterna, String turno,
                         String estado, String telefono, String email, String certificaciones) {
        this.id = id;
        this.nombreCompleto = nombreCompleto;
        this.cargo = cargo;
        this.categoria = categoria;
        this.areaAsignada = areaAsignada;
        this.extensionInterna = extensionInterna;
        this.turno = turno;
        this.estado = estado;
        this.telefono = telefono;
        this.email = email;
        this.certificaciones = certificaciones;
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

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getAreaAsignada() {
        return areaAsignada;
    }

    public void setAreaAsignada(String areaAsignada) {
        this.areaAsignada = areaAsignada;
    }

    public String getExtensionInterna() {
        return extensionInterna;
    }

    public void setExtensionInterna(String extensionInterna) {
        this.extensionInterna = extensionInterna;
    }

    public String getTurno() {
        return turno;
    }

    public void setTurno(String turno) {
        this.turno = turno;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
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

    public String getCertificaciones() {
        return certificaciones;
    }

    public void setCertificaciones(String certificaciones) {
        this.certificaciones = certificaciones;
    }

    @Override
    public String toString() {
        return nombreCompleto + " (" + cargo + ")";
    }
}
