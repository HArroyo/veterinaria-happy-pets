package happypets.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Modelo de datos para el Submódulo 4.1: Grooming y Peluquería Canina y Felina.
 * Registra turnos de estética, checklist sanitario de entrada, productos utilizados y estado del servicio.
 */
public class ServicioGrooming {
    public static final DateTimeFormatter FECHA_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    public static final DateTimeFormatter HORA_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private String idGrooming;
    private String codigoMascota;
    private String nombreMascota;
    private String especieRaza;
    private String nombreTutor;
    private String telefonoTutor;
    private LocalDate fecha;
    private LocalTime horaTurno;
    private String groomer; // Estilista asignado
    private String tipoServicio; // "Spa Completo", "Baño Medicado Dermatológico", "Corte de Raza Estándar", etc.
    private String controlEctoparasitos; // "Libre de ectoparásitos", "Presencia leve de pulgas", etc.
    private String estadoPiel; // "Sana sin lesiones", "Dermatitis leve / Eritema", "Nudos graves"
    private String productosUtilizados; // Champús, acondicionadores, etc.
    private String observacionesConducta; // "Tranquilo", "Nervioso en secado", etc.
    private double costo;
    private String estado; // "En Espera", "En Baño", "En Corte y Secado", "Listo para Entrega", "Entregado"

    public ServicioGrooming(String idGrooming, String codigoMascota, String nombreMascota, String especieRaza,
                            String nombreTutor, String telefonoTutor, LocalDate fecha, LocalTime horaTurno,
                            String groomer, String tipoServicio, String controlEctoparasitos, String estadoPiel,
                            String productosUtilizados, String observacionesConducta, double costo, String estado) {
        this.idGrooming = idGrooming;
        this.codigoMascota = codigoMascota;
        this.nombreMascota = nombreMascota;
        this.especieRaza = especieRaza;
        this.nombreTutor = nombreTutor;
        this.telefonoTutor = telefonoTutor;
        this.fecha = fecha != null ? fecha : LocalDate.now();
        this.horaTurno = horaTurno != null ? horaTurno : LocalTime.now();
        this.groomer = groomer;
        this.tipoServicio = tipoServicio;
        this.controlEctoparasitos = controlEctoparasitos;
        this.estadoPiel = estadoPiel;
        this.productosUtilizados = productosUtilizados;
        this.observacionesConducta = observacionesConducta;
        this.costo = costo;
        this.estado = estado != null ? estado : "En Espera";
    }

    public String getIdGrooming() { return idGrooming; }
    public void setIdGrooming(String idGrooming) { this.idGrooming = idGrooming; }

    public String getCodigoMascota() { return codigoMascota; }
    public void setCodigoMascota(String codigoMascota) { this.codigoMascota = codigoMascota; }

    public String getNombreMascota() { return nombreMascota; }
    public void setNombreMascota(String nombreMascota) { this.nombreMascota = nombreMascota; }

    public String getEspecieRaza() { return especieRaza; }
    public void setEspecieRaza(String especieRaza) { this.especieRaza = especieRaza; }

    public String getNombreTutor() { return nombreTutor; }
    public void setNombreTutor(String nombreTutor) { this.nombreTutor = nombreTutor; }

    public String getTelefonoTutor() { return telefonoTutor; }
    public void setTelefonoTutor(String telefonoTutor) { this.telefonoTutor = telefonoTutor; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public String getFechaFormateada() { return fecha != null ? fecha.format(FECHA_FORMATTER) : "-"; }

    public LocalTime getHoraTurno() { return horaTurno; }
    public void setHoraTurno(LocalTime horaTurno) { this.horaTurno = horaTurno; }
    public String getHoraTurnoFormateada() { return horaTurno != null ? horaTurno.format(HORA_FORMATTER) : "-"; }

    public String getGroomer() { return groomer; }
    public void setGroomer(String groomer) { this.groomer = groomer; }

    public String getTipoServicio() { return tipoServicio; }
    public void setTipoServicio(String tipoServicio) { this.tipoServicio = tipoServicio; }

    public String getControlEctoparasitos() { return controlEctoparasitos; }
    public void setControlEctoparasitos(String controlEctoparasitos) { this.controlEctoparasitos = controlEctoparasitos; }

    public String getEstadoPiel() { return estadoPiel; }
    public void setEstadoPiel(String estadoPiel) { this.estadoPiel = estadoPiel; }

    public String getProductosUtilizados() { return productosUtilizados; }
    public void setProductosUtilizados(String productosUtilizados) { this.productosUtilizados = productosUtilizados; }

    public String getObservacionesConducta() { return observacionesConducta; }
    public void setObservacionesConducta(String observacionesConducta) { this.observacionesConducta = observacionesConducta; }

    public double getCosto() { return costo; }
    public void setCosto(double costo) { this.costo = costo; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
