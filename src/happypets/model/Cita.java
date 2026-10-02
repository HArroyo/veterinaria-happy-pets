package happypets.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Representa una cita médica veterinaria agendada en la plataforma.
 */
public class Cita implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final DateTimeFormatter HORA_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    public static final DateTimeFormatter FECHA_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private String idCita;                 // Ej: CIT-0101
    private String codigoMascota;          // Ej: VET-0091
    private String nombreMascota;          // Ej: Rocky
    private String especieRaza;            // Ej: Canino · Golden Retriever
    private String dniCliente;             // Ej: 45892134
    private String nombreCliente;          // Ej: Carlos Eduardo Morales
    private String telefonoCliente;        // Ej: +51 984 552 110
    private LocalDate fecha;               // Fecha de la cita
    private LocalTime hora;                // Hora de la cita (Ej: 09:30)
    private int duracionMinutos;           // Duración estimada (Ej: 30, 45, 60 min)
    private String veterinario;            // Ej: Dr. Roberto Mendoza
    private String tipoServicio;           // Consulta Médica, Vacunación, Cirugía / Quirófano, Grooming / Peluquería, Control y Seguimiento
    private String estado;                 // Programada, Confirmada, En Sala de Espera, En Atención, Atendida, Cancelada
    private String motivo;                 // Motivo de consulta
    private String prioridad;              // Normal, Urgente, Emergencia
    private String observaciones;
    private double costoEstimado;

    public Cita(String idCita, String codigoMascota, String nombreMascota, String especieRaza,
                String dniCliente, String nombreCliente, String telefonoCliente,
                LocalDate fecha, LocalTime hora, int duracionMinutos, String veterinario,
                String tipoServicio, String estado, String motivo, String prioridad,
                String observaciones, double costoEstimado) {
        this.idCita = idCita;
        this.codigoMascota = codigoMascota;
        this.nombreMascota = nombreMascota;
        this.especieRaza = especieRaza;
        this.dniCliente = dniCliente;
        this.nombreCliente = nombreCliente;
        this.telefonoCliente = telefonoCliente;
        this.fecha = fecha;
        this.hora = hora;
        this.duracionMinutos = duracionMinutos;
        this.veterinario = veterinario;
        this.tipoServicio = tipoServicio;
        this.estado = estado;
        this.motivo = motivo;
        this.prioridad = prioridad;
        this.observaciones = observaciones;
        this.costoEstimado = costoEstimado;
    }

    public String getIdCita() { return idCita; }
    public void setIdCita(String idCita) { this.idCita = idCita; }

    public String getCodigoMascota() { return codigoMascota; }
    public void setCodigoMascota(String codigoMascota) { this.codigoMascota = codigoMascota; }

    public String getNombreMascota() { return nombreMascota; }
    public void setNombreMascota(String nombreMascota) { this.nombreMascota = nombreMascota; }

    public String getEspecieRaza() { return especieRaza; }
    public void setEspecieRaza(String especieRaza) { this.especieRaza = especieRaza; }

    public String getDniCliente() { return dniCliente; }
    public void setDniCliente(String dniCliente) { this.dniCliente = dniCliente; }

    public String getNombreCliente() { return nombreCliente; }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }

    public String getTelefonoCliente() { return telefonoCliente; }
    public void setTelefonoCliente(String telefonoCliente) { this.telefonoCliente = telefonoCliente; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public LocalTime getHora() { return hora; }
    public void setHora(LocalTime hora) { this.hora = hora; }

    public int getDuracionMinutos() { return duracionMinutos; }
    public void setDuracionMinutos(int duracionMinutos) { this.duracionMinutos = duracionMinutos; }

    public String getVeterinario() { return veterinario; }
    public void setVeterinario(String veterinario) { this.veterinario = veterinario; }

    public String getTipoServicio() { return tipoServicio; }
    public void setTipoServicio(String tipoServicio) { this.tipoServicio = tipoServicio; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public String getPrioridad() { return prioridad; }
    public void setPrioridad(String prioridad) { this.prioridad = prioridad; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public double getCostoEstimado() { return costoEstimado; }
    public void setCostoEstimado(double costoEstimado) { this.costoEstimado = costoEstimado; }

    public String getHoraFormateada() {
        return hora != null ? hora.format(HORA_FORMATTER) : "--:--";
    }

    public String getFechaFormateada() {
        return fecha != null ? fecha.format(FECHA_FORMATTER) : "--/--/----";
    }

    @Override
    public String toString() {
        return idCita + " - " + nombreMascota + " (" + getFechaFormateada() + " " + getHoraFormateada() + ") - " + tipoServicio;
    }
}
