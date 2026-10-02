package happypets.model;

import java.time.LocalDate;

/** Atención ya registrada; esta interfaz solamente consulta sus datos. */
public class ConsultaClinica {
    private String codigo;
    private String codigoMascota;
    private LocalDate fecha;
    private String motivo;
    private String sintomas;
    private String diagnostico;
    private String tratamiento;
    private String veterinario;
    private double pesoKg;
    private double temperaturaC;
    private String observaciones;
    private String estado;

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getCodigoMascota() { return codigoMascota; }
    public void setCodigoMascota(String codigoMascota) { this.codigoMascota = codigoMascota; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
    public String getSintomas() { return sintomas; }
    public void setSintomas(String sintomas) { this.sintomas = sintomas; }
    public String getDiagnostico() { return diagnostico; }
    public void setDiagnostico(String diagnostico) { this.diagnostico = diagnostico; }
    public String getTratamiento() { return tratamiento; }
    public void setTratamiento(String tratamiento) { this.tratamiento = tratamiento; }
    public String getVeterinario() { return veterinario; }
    public void setVeterinario(String veterinario) { this.veterinario = veterinario; }
    public double getPesoKg() { return pesoKg; }
    public void setPesoKg(double pesoKg) { this.pesoKg = pesoKg; }
    public double getTemperaturaC() { return temperaturaC; }
    public void setTemperaturaC(double temperaturaC) { this.temperaturaC = temperaturaC; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
