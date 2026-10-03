package happypets.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Modelo de datos para el Submódulo 4.3: Hotel y Guardería Canina y Felina.
 * Registra estadías en suites individuales, planes de paseos, dieta personalizada y control de check-in / check-out.
 */
public class ReservaHospedaje {
    public static final DateTimeFormatter FECHA_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private String idReserva;
    private String numeroSuite; // e.g. "Suite 01 Canina", "Suite 03 Felina"
    private String codigoMascota;
    private String nombreMascota;
    private String especieRaza;
    private String nombreTutor;
    private String telefonoTutor;
    private String telefonoEmergencia;
    private LocalDate fechaCheckIn;
    private LocalDate fechaCheckOut;
    private int numeroNoches;
    private String tipoAlimentacion; // Dieta propia, raciones diarias y horarios
    private String planPaseos; // Cantidad de paseos y nivel de sociabilidad
    private boolean banoSalida; // Servicio de baño cosmético antes de salida
    private String medicacionEspecial;
    private double costoNoche;
    private double costoTotal;
    private String estado; // "Confirmada", "En Estadía / Hospedado", "Finalizada / Check-out", "Cancelada"

    public ReservaHospedaje(String idReserva, String numeroSuite, String codigoMascota, String nombreMascota,
                            String especieRaza, String nombreTutor, String telefonoTutor, String telefonoEmergencia,
                            LocalDate fechaCheckIn, LocalDate fechaCheckOut, int numeroNoches,
                            String tipoAlimentacion, String planPaseos, boolean banoSalida,
                            String medicacionEspecial, double costoNoche, double costoTotal, String estado) {
        this.idReserva = idReserva;
        this.numeroSuite = numeroSuite;
        this.codigoMascota = codigoMascota;
        this.nombreMascota = nombreMascota;
        this.especieRaza = especieRaza;
        this.nombreTutor = nombreTutor;
        this.telefonoTutor = telefonoTutor;
        this.telefonoEmergencia = telefonoEmergencia;
        this.fechaCheckIn = fechaCheckIn != null ? fechaCheckIn : LocalDate.now();
        this.fechaCheckOut = fechaCheckOut != null ? fechaCheckOut : LocalDate.now().plusDays(1);
        this.numeroNoches = numeroNoches > 0 ? numeroNoches : 1;
        this.tipoAlimentacion = tipoAlimentacion;
        this.planPaseos = planPaseos;
        this.banoSalida = banoSalida;
        this.medicacionEspecial = medicacionEspecial;
        this.costoNoche = costoNoche;
        this.costoTotal = costoTotal;
        this.estado = estado != null ? estado : "Confirmada";
    }

    public String getIdReserva() { return idReserva; }
    public void setIdReserva(String idReserva) { this.idReserva = idReserva; }

    public String getNumeroSuite() { return numeroSuite; }
    public void setNumeroSuite(String numeroSuite) { this.numeroSuite = numeroSuite; }

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

    public String getTelefonoEmergencia() { return telefonoEmergencia; }
    public void setTelefonoEmergencia(String telefonoEmergencia) { this.telefonoEmergencia = telefonoEmergencia; }

    public LocalDate getFechaCheckIn() { return fechaCheckIn; }
    public void setFechaCheckIn(LocalDate fechaCheckIn) { this.fechaCheckIn = fechaCheckIn; }
    public String getFechaCheckInFormateada() { return fechaCheckIn != null ? fechaCheckIn.format(FECHA_FORMATTER) : "-"; }

    public LocalDate getFechaCheckOut() { return fechaCheckOut; }
    public void setFechaCheckOut(LocalDate fechaCheckOut) { this.fechaCheckOut = fechaCheckOut; }
    public String getFechaCheckOutFormateada() { return fechaCheckOut != null ? fechaCheckOut.format(FECHA_FORMATTER) : "-"; }

    public int getNumeroNoches() { return numeroNoches; }
    public void setNumeroNoches(int numeroNoches) { this.numeroNoches = numeroNoches; }

    public String getTipoAlimentacion() { return tipoAlimentacion; }
    public void setTipoAlimentacion(String tipoAlimentacion) { this.tipoAlimentacion = tipoAlimentacion; }

    public String getPlanPaseos() { return planPaseos; }
    public void setPlanPaseos(String planPaseos) { this.planPaseos = planPaseos; }

    public boolean isBanoSalida() { return banoSalida; }
    public void setBanoSalida(boolean banoSalida) { this.banoSalida = banoSalida; }

    public String getMedicacionEspecial() { return medicacionEspecial; }
    public void setMedicacionEspecial(String medicacionEspecial) { this.medicacionEspecial = medicacionEspecial; }

    public double getCostoNoche() { return costoNoche; }
    public void setCostoNoche(double costoNoche) { this.costoNoche = costoNoche; }

    public double getCostoTotal() { return costoTotal; }
    public void setCostoTotal(double costoTotal) { this.costoTotal = costoTotal; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
