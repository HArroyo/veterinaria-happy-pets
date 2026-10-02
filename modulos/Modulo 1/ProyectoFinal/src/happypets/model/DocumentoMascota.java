package happypets.model;

import java.time.LocalDate;

/** Documento consultable de una mascota, incluido cualquier certificado. */
public class DocumentoMascota {
    private String codigo;
    private String codigoMascota;
    private String tipo;
    private String descripcion;
    private LocalDate fechaActualizacion;
    private LocalDate fechaVencimiento;
    private String veterinarioEmisor;
    private String rutaArchivo;
    private boolean disponible;

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
    public LocalDate getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(LocalDate fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }
    public String getVeterinarioEmisor() { return veterinarioEmisor; }
    public void setVeterinarioEmisor(String veterinarioEmisor) { this.veterinarioEmisor = veterinarioEmisor; }
    public String getRutaArchivo() { return rutaArchivo; }
    public void setRutaArchivo(String rutaArchivo) { this.rutaArchivo = rutaArchivo; }
    public boolean isDisponible() { return disponible; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }
}
