package happypets.model;

import java.time.LocalDate;

/** Datos adicionales de un certificado veterinario consultable. */
public class Certificado extends DocumentoMascota {
    private String numeroCertificado;
    private LocalDate fechaEmision;
    private String motivoDestino;
    private String estado;

    public String getNumeroCertificado() { return numeroCertificado; }
    public void setNumeroCertificado(String numeroCertificado) { this.numeroCertificado = numeroCertificado; }
    public LocalDate getFechaEmision() { return fechaEmision; }
    public void setFechaEmision(LocalDate fechaEmision) { this.fechaEmision = fechaEmision; }
    public String getMotivoDestino() { return motivoDestino; }
    public void setMotivoDestino(String motivoDestino) { this.motivoDestino = motivoDestino; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
