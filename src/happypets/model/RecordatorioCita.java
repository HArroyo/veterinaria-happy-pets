package happypets.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Representa un recordatorio automatizado o manual para citas veterinarias.
 */
public class RecordatorioCita implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final DateTimeFormatter FORMATO_FECHA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private String idRecordatorio;        // Ej: REC-001
    private String idCita;                // Ej: CIT-0101
    private String nombreMascota;
    private String nombreCliente;
    private String contactoDestino;       // Celular / WhatsApp o Email
    private String canal;                 // WhatsApp, SMS, E-Mail
    private LocalDateTime fechaEnvio;
    private String mensaje;
    private String estado;                // Pendiente, Enviado, Confirmado, Fallido

    public RecordatorioCita(String idRecordatorio, String idCita, String nombreMascota,
                            String nombreCliente, String contactoDestino, String canal,
                            LocalDateTime fechaEnvio, String mensaje, String estado) {
        this.idRecordatorio = idRecordatorio;
        this.idCita = idCita;
        this.nombreMascota = nombreMascota;
        this.nombreCliente = nombreCliente;
        this.contactoDestino = contactoDestino;
        this.canal = canal;
        this.fechaEnvio = fechaEnvio;
        this.mensaje = mensaje;
        this.estado = estado;
    }

    public String getIdRecordatorio() { return idRecordatorio; }
    public void setIdRecordatorio(String idRecordatorio) { this.idRecordatorio = idRecordatorio; }

    public String getIdCita() { return idCita; }
    public void setIdCita(String idCita) { this.idCita = idCita; }

    public String getNombreMascota() { return nombreMascota; }
    public void setNombreMascota(String nombreMascota) { this.nombreMascota = nombreMascota; }

    public String getNombreCliente() { return nombreCliente; }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }

    public String getContactoDestino() { return contactoDestino; }
    public void setContactoDestino(String contactoDestino) { this.contactoDestino = contactoDestino; }

    public String getCanal() { return canal; }
    public void setCanal(String canal) { this.canal = canal; }

    public LocalDateTime getFechaEnvio() { return fechaEnvio; }
    public void setFechaEnvio(LocalDateTime fechaEnvio) { this.fechaEnvio = fechaEnvio; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getFechaEnvioFormateada() {
        return fechaEnvio != null ? fechaEnvio.format(FORMATO_FECHA_HORA) : "--/--/---- --:--";
    }
}
