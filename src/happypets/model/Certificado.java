package happypets.model;

import java.time.LocalDate;

/**
 * Datos específicos de certificados oficiales emitidos a la mascota.
 */
public class Certificado extends DocumentoMascota {
    private String numeroCertificado;
    private LocalDate fechaEmision;
    private String motivoDestino;
    private String estado;

    public Certificado() {
        super();
    }

    public Certificado(String codigo, String codigoMascota, String tipo, String descripcion,
                       LocalDate fechaActualizacion, boolean disponible, String numeroCertificado,
                       LocalDate fechaEmision, String motivoDestino, String estado) {
        super(codigo, codigoMascota, tipo, descripcion, fechaActualizacion, disponible);
        this.numeroCertificado = numeroCertificado;
        this.fechaEmision = fechaEmision;
        this.motivoDestino = motivoDestino;
        this.estado = estado;
    }

    public String getNumeroCertificado() { return numeroCertificado; }
    public void setNumeroCertificado(String numeroCertificado) { this.numeroCertificado = numeroCertificado; }

    public LocalDate getFechaEmision() { return fechaEmision; }
    public void setFechaEmision(LocalDate fechaEmision) { this.fechaEmision = fechaEmision; }

    public String getMotivoDestino() { return motivoDestino; }
    public void setMotivoDestino(String motivoDestino) { this.motivoDestino = motivoDestino; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
