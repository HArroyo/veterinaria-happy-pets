package happypets.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Documento consultable o descargable de una mascota.
 */
public class DocumentoMascota {
    private String codigo;
    private String codigoMascota;
    private String tipo;
    private String descripcion;
    private LocalDate fechaActualizacion;
    private LocalDate fechaVencimiento;
    private String veterinarioEmisor;
    private String rutaArchivo;
    private boolean disponible = true;

    public DocumentoMascota() {
    }

    public DocumentoMascota(String codigo, String codigoMascota, String tipo, String descripcion,
                            LocalDate fechaActualizacion, boolean disponible) {
        this.codigo = codigo;
        this.codigoMascota = codigoMascota;
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.fechaActualizacion = fechaActualizacion;
        this.disponible = disponible;
    }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getCodigoMascota() { return codigoMascota; }
    public void setCodigoMascota(String codigoMascota) { this.codigoMascota = codigoMascota; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public LocalDate getFechaActualizacion() { return fechaActualizacion; }
    public void setFechaActualizacion(LocalDate fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }

    public String getFechaActualizacionFormateada() {
        return fechaActualizacion != null ? fechaActualizacion.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "";
    }

    public LocalDate getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(LocalDate fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }

    public String getVeterinarioEmisor() { return veterinarioEmisor; }
    public void setVeterinarioEmisor(String veterinarioEmisor) { this.veterinarioEmisor = veterinarioEmisor; }

    public String getRutaArchivo() { return rutaArchivo; }
    public void setRutaArchivo(String rutaArchivo) { this.rutaArchivo = rutaArchivo; }

    public boolean isDisponible() { return disponible; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }

    public String getDisponibilidadTexto() {
        return disponible ? "Disponible" : "No disponible";
    }
}
