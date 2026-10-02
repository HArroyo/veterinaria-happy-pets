package happypets.model;

import java.io.Serializable;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Representa a un paciente en la sala de espera y triaje de urgencias veterinarias.
 */
public class PacienteTriaje implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final DateTimeFormatter HORA_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private String idTicket;              // Ej: TR-01, TR-02
    private String idCita;                // Puede ser null si es urgencia sin cita
    private String codigoMascota;
    private String nombreMascota;
    private String especieRaza;
    private String nombrePropietario;
    private String telefonoPropietario;
    private LocalTime horaLlegada;
    private double pesoKg;
    private double temperaturaC;
    private int frecuenciaCardiacaPpm;
    private String nivelTriaje;           // VERDE (Normal), AMARILLO (Urgencia), ROJO (Emergencia crítica)
    private String motivo;
    private String consultorioAsignado;   // Consultorio 1, Consultorio 2, Quirófano, Tópico
    private String estado;                // En Espera, Llamado, En Consulta, Atendido

    public PacienteTriaje(String idTicket, String idCita, String codigoMascota,
                          String nombreMascota, String especieRaza, String nombrePropietario,
                          String telefonoPropietario, LocalTime horaLlegada, double pesoKg,
                          double temperaturaC, int frecuenciaCardiacaPpm, String nivelTriaje,
                          String motivo, String consultorioAsignado, String estado) {
        this.idTicket = idTicket;
        this.idCita = idCita;
        this.codigoMascota = codigoMascota;
        this.nombreMascota = nombreMascota;
        this.especieRaza = especieRaza;
        this.nombrePropietario = nombrePropietario;
        this.telefonoPropietario = telefonoPropietario;
        this.horaLlegada = horaLlegada;
        this.pesoKg = pesoKg;
        this.temperaturaC = temperaturaC;
        this.frecuenciaCardiacaPpm = frecuenciaCardiacaPpm;
        this.nivelTriaje = nivelTriaje;
        this.motivo = motivo;
        this.consultorioAsignado = consultorioAsignado;
        this.estado = estado;
    }

    public String getIdTicket() { return idTicket; }
    public void setIdTicket(String idTicket) { this.idTicket = idTicket; }

    public String getIdCita() { return idCita; }
    public void setIdCita(String idCita) { this.idCita = idCita; }

    public String getCodigoMascota() { return codigoMascota; }
    public void setCodigoMascota(String codigoMascota) { this.codigoMascota = codigoMascota; }

    public String getNombreMascota() { return nombreMascota; }
    public void setNombreMascota(String nombreMascota) { this.nombreMascota = nombreMascota; }

    public String getEspecieRaza() { return especieRaza; }
    public void setEspecieRaza(String especieRaza) { this.especieRaza = especieRaza; }

    public String getNombrePropietario() { return nombrePropietario; }
    public void setNombrePropietario(String nombrePropietario) { this.nombrePropietario = nombrePropietario; }

    public String getTelefonoPropietario() { return telefonoPropietario; }
    public void setTelefonoPropietario(String telefonoPropietario) { this.telefonoPropietario = telefonoPropietario; }

    public LocalTime getHoraLlegada() { return horaLlegada; }
    public void setHoraLlegada(LocalTime horaLlegada) { this.horaLlegada = horaLlegada; }

    public double getPesoKg() { return pesoKg; }
    public void setPesoKg(double pesoKg) { this.pesoKg = pesoKg; }

    public double getTemperaturaC() { return temperaturaC; }
    public void setTemperaturaC(double temperaturaC) { this.temperaturaC = temperaturaC; }

    public int getFrecuenciaCardiacaPpm() { return frecuenciaCardiacaPpm; }
    public void setFrecuenciaCardiacaPpm(int frecuenciaCardiacaPpm) { this.frecuenciaCardiacaPpm = frecuenciaCardiacaPpm; }

    public String getNivelTriaje() { return nivelTriaje; }
    public void setNivelTriaje(String nivelTriaje) { this.nivelTriaje = nivelTriaje; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public String getConsultorioAsignado() { return consultorioAsignado; }
    public void setConsultorioAsignado(String consultorioAsignado) { this.consultorioAsignado = consultorioAsignado; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getHoraLlegadaFormateada() {
        return horaLlegada != null ? horaLlegada.format(HORA_FORMATTER) : "--:--";
    }
}
