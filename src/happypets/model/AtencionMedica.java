package happypets.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Modelo de datos para el Submódulo 3.1: Consultas Médicas.
 * Registra anamnesis, exploración física, constantes vitales, diagnósticos y prescripciones.
 */
public class AtencionMedica {
    public static final DateTimeFormatter FECHA_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    public static final DateTimeFormatter HORA_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private String idConsulta;
    private String codigoMascota;
    private String nombreMascota;
    private String especieRaza;
    private String dniTutor;
    private String nombreTutor;
    private String telefonoTutor;
    private LocalDate fecha;
    private LocalTime hora;
    private String veterinario;
    private String motivoConsulta;
    private double pesoKg;
    private double temperaturaC;
    private int frecuenciaCardiacaLpm;
    private String evaluacionClinica;
    private String diagnosticoPresuntivo;
    private String diagnosticoDefinitivo;
    private String tratamientoIndicado;
    private String recetaMedicamentos;
    private String indicacionesTutor;
    private String seguimiento;
    private String estado;
    private double costo;

    public AtencionMedica(String idConsulta, String codigoMascota, String nombreMascota, String especieRaza,
                          String dniTutor, String nombreTutor, String telefonoTutor, LocalDate fecha, LocalTime hora,
                          String veterinario, String motivoConsulta, double pesoKg, double temperaturaC,
                          int frecuenciaCardiacaLpm, String evaluacionClinica, String diagnosticoPresuntivo,
                          String diagnosticoDefinitivo, String tratamientoIndicado, String recetaMedicamentos,
                          String indicacionesTutor, String seguimiento, String estado, double costo) {
        this.idConsulta = idConsulta;
        this.codigoMascota = codigoMascota;
        this.nombreMascota = nombreMascota;
        this.especieRaza = especieRaza;
        this.dniTutor = dniTutor;
        this.nombreTutor = nombreTutor;
        this.telefonoTutor = telefonoTutor;
        this.fecha = fecha != null ? fecha : LocalDate.now();
        this.hora = hora != null ? hora : LocalTime.now();
        this.veterinario = veterinario;
        this.motivoConsulta = motivoConsulta;
        this.pesoKg = pesoKg;
        this.temperaturaC = temperaturaC;
        this.frecuenciaCardiacaLpm = frecuenciaCardiacaLpm;
        this.evaluacionClinica = evaluacionClinica;
        this.diagnosticoPresuntivo = diagnosticoPresuntivo;
        this.diagnosticoDefinitivo = diagnosticoDefinitivo;
        this.tratamientoIndicado = tratamientoIndicado;
        this.recetaMedicamentos = recetaMedicamentos;
        this.indicacionesTutor = indicacionesTutor;
        this.seguimiento = seguimiento;
        this.estado = estado != null ? estado : "Completada";
        this.costo = costo;
    }

    public String getIdConsulta() { return idConsulta; }
    public void setIdConsulta(String idConsulta) { this.idConsulta = idConsulta; }

    public String getCodigoMascota() { return codigoMascota; }
    public void setCodigoMascota(String codigoMascota) { this.codigoMascota = codigoMascota; }

    public String getNombreMascota() { return nombreMascota; }
    public void setNombreMascota(String nombreMascota) { this.nombreMascota = nombreMascota; }

    public String getEspecieRaza() { return especieRaza; }
    public void setEspecieRaza(String especieRaza) { this.especieRaza = especieRaza; }

    public String getDniTutor() { return dniTutor; }
    public void setDniTutor(String dniTutor) { this.dniTutor = dniTutor; }

    public String getNombreTutor() { return nombreTutor; }
    public void setNombreTutor(String nombreTutor) { this.nombreTutor = nombreTutor; }

    public String getTelefonoTutor() { return telefonoTutor; }
    public void setTelefonoTutor(String telefonoTutor) { this.telefonoTutor = telefonoTutor; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public String getFechaFormateada() { return fecha != null ? fecha.format(FECHA_FORMATTER) : "-"; }

    public LocalTime getHora() { return hora; }
    public void setHora(LocalTime hora) { this.hora = hora; }
    public String getHoraFormateada() { return hora != null ? hora.format(HORA_FORMATTER) : "-"; }

    public String getVeterinario() { return veterinario; }
    public void setVeterinario(String veterinario) { this.veterinario = veterinario; }

    public String getMotivoConsulta() { return motivoConsulta; }
    public void setMotivoConsulta(String motivoConsulta) { this.motivoConsulta = motivoConsulta; }

    public double getPesoKg() { return pesoKg; }
    public void setPesoKg(double pesoKg) { this.pesoKg = pesoKg; }

    public double getTemperaturaC() { return temperaturaC; }
    public void setTemperaturaC(double temperaturaC) { this.temperaturaC = temperaturaC; }

    public int getFrecuenciaCardiacaLpm() { return frecuenciaCardiacaLpm; }
    public void setFrecuenciaCardiacaLpm(int frecuenciaCardiacaLpm) { this.frecuenciaCardiacaLpm = frecuenciaCardiacaLpm; }

    public String getEvaluacionClinica() { return evaluacionClinica; }
    public void setEvaluacionClinica(String evaluacionClinica) { this.evaluacionClinica = evaluacionClinica; }

    public String getDiagnosticoPresuntivo() { return diagnosticoPresuntivo; }
    public void setDiagnosticoPresuntivo(String diagnosticoPresuntivo) { this.diagnosticoPresuntivo = diagnosticoPresuntivo; }

    public String getDiagnosticoDefinitivo() { return diagnosticoDefinitivo; }
    public void setDiagnosticoDefinitivo(String diagnosticoDefinitivo) { this.diagnosticoDefinitivo = diagnosticoDefinitivo; }

    public String getTratamientoIndicado() { return tratamientoIndicado; }
    public void setTratamientoIndicado(String tratamientoIndicado) { this.tratamientoIndicado = tratamientoIndicado; }

    public String getRecetaMedicamentos() { return recetaMedicamentos; }
    public void setRecetaMedicamentos(String recetaMedicamentos) { this.recetaMedicamentos = recetaMedicamentos; }

    public String getIndicacionesTutor() { return indicacionesTutor; }
    public void setIndicacionesTutor(String indicacionesTutor) { this.indicacionesTutor = indicacionesTutor; }

    public String getSeguimiento() { return seguimiento; }
    public void setSeguimiento(String seguimiento) { this.seguimiento = seguimiento; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public double getCosto() { return costo; }
    public void setCosto(double costo) { this.costo = costo; }
}
