package happypets.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Modelo para los archivos del Repositorio Documental centralizado (Módulo 9).
 */
public class DocumentoRepositorio implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String tipoExtension; // "PDF", "DOCX", "XLSX", "PNG", etc.
    private String nombreArchivo; // "Factura.pdf", "Informe.docx", etc.
    private String categoria;     // "Facturación", "Auditoría", "Legal / RRHH", "Contabilidad", "Clínico"
    private double tamanoMB;
    private LocalDateTime fechaCarga;
    private String usuarioCarga;  // "admin_user"
    private String rutaArchivo;

    public DocumentoRepositorio() {
    }

    public DocumentoRepositorio(String id, String tipoExtension, String nombreArchivo,
                                String categoria, double tamanoMB, LocalDateTime fechaCarga,
                                String usuarioCarga, String rutaArchivo) {
        this.id = id;
        this.tipoExtension = tipoExtension;
        this.nombreArchivo = nombreArchivo;
        this.categoria = categoria;
        this.tamanoMB = tamanoMB;
        this.fechaCarga = fechaCarga;
        this.usuarioCarga = usuarioCarga;
        this.rutaArchivo = rutaArchivo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTipoExtension() {
        return tipoExtension;
    }

    public void setTipoExtension(String tipoExtension) {
        this.tipoExtension = tipoExtension;
    }

    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
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

    public LocalDateTime getFechaCarga() {
        return fechaCarga;
    }

    public void setFechaCarga(LocalDateTime fechaCarga) {
        this.fechaCarga = fechaCarga;
    }

    public String getFechaCargaFormateada() {
        if (fechaCarga == null) return "";
        return fechaCarga.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    public String getUsuarioCarga() {
        return usuarioCarga;
    }

    public void setUsuarioCarga(String usuarioCarga) {
        this.usuarioCarga = usuarioCarga;
    }

    public String getRutaArchivo() {
        return rutaArchivo;
    }

    public void setRutaArchivo(String rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
    }
}
