package happypets.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Modelo para las filas del historial de exportaciones de datos en el Módulo 8.
 */
public class HistorialExportacion implements Serializable {
    private static final long serialVersionUID = 1L;

    private String idExportacion; // Ej: "EXP-2024-0518"
    private String origenDatos; // Ej: "Pacientes y Fichas Clínicas"
    private LocalDateTime fechaCreacion;
    private String filtrosAplicados; // Ej: "Caninos, Felinos · Sede Central"
    private String formato; // "XLSX", "CSV", "PDF"
    private int totalFilas;
    private double tamanoMB;
    private String estado; // "Completado", "En Proceso", "Fallido"
    private String rutaArchivo;

    public HistorialExportacion() {
    }

    public HistorialExportacion(String idExportacion, String origenDatos, LocalDateTime fechaCreacion,
                                String filtrosAplicados, String formato, int totalFilas, double tamanoMB,
                                String estado, String rutaArchivo) {
        this.idExportacion = idExportacion;
        this.origenDatos = origenDatos;
        this.fechaCreacion = fechaCreacion;
        this.filtrosAplicados = filtrosAplicados;
        this.formato = formato;
        this.totalFilas = totalFilas;
        this.tamanoMB = tamanoMB;
        this.estado = estado;
        this.rutaArchivo = rutaArchivo;
    }

    public String getIdExportacion() {
        return idExportacion;
    }

    public void setIdExportacion(String idExportacion) {
        this.idExportacion = idExportacion;
    }

    public String getOrigenDatos() {
        return origenDatos;
    }

    public void setOrigenDatos(String origenDatos) {
        this.origenDatos = origenDatos;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public String getFechaCreacionFormateada() {
        if (fechaCreacion == null) return "";
        return fechaCreacion.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    public String getFiltrosAplicados() {
        return filtrosAplicados;
    }

    public void setFiltrosAplicados(String filtrosAplicados) {
        this.filtrosAplicados = filtrosAplicados;
    }

    public String getFormato() {
        return formato;
    }

    public void setFormato(String formato) {
        this.formato = formato;
    }

    public int getTotalFilas() {
        return totalFilas;
    }

    public void setTotalFilas(int totalFilas) {
        this.totalFilas = totalFilas;
    }

    public double getTamanoMB() {
        return tamanoMB;
    }

    public void setTamanoMB(double tamanoMB) {
        this.tamanoMB = tamanoMB;
    }

    public String getTamanoLegible() {
        return String.format("%.1f MB", tamanoMB);
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getRutaArchivo() {
        return rutaArchivo;
    }

    public void setRutaArchivo(String rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
    }
}
