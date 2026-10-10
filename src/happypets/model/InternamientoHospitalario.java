package happypets.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Modelo de datos para el Submódulo 4.2: Hospitalización y Cuidados Intensivos.
 * Registra ocupación de boxes/caniles, fluidoterapia, medicación intrahospitalaria y notas de evolución.
 */
public class InternamientoHospitalario {
    public static final DateTimeFormatter FECHA_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    public static final DateTimeFormatter HORA_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private String idInternamiento;
    private String numeroBox; // e.g. "Box 01", "Box 02", etc.
    private String tipoBox; // "UCI / Cuidados Intensivos", "Hospitalización General", "Aislamiento Infeccioso"
    private String codigoMascota;
    private String nombreMascota;
    private String especieRaza;
    private String nombreTutor;
    private String telefonoTutor;
    private String diagnosticoIngreso;
    private String veterinarioTratante;
    private LocalDate fechaIngreso;
    private LocalTime horaIngreso;
    private LocalDate fechaAltaEstimada;
    private double pesoActualKg;
    private double temperaturaC;
    private int frecuenciaCardiaca;
    private String fluidoterapia; // e.g. "Ringer Lactato a 40 ml/h continuo"
    private String medicacionActual; // antibióticos, analgesia, protectores
    private String evolucionNotas; // notas de enfermería veterinaria
    private String nivelAlerta; // "ESTABLE", "OBSERVACIÓN", "CRÍTICO"
    private double costoDia;
    private String estado; // "Internado / En Tratamiento", "Alta Médica", "Derivado a UCI"

    public InternamientoHospitalario(String idInternamiento, String numeroBox, String tipoBox, String codigoMascota,
                                     String nombreMascota, String especieRaza, String nombreTutor, String telefonoTutor,
                                     String diagnosticoIngreso, String veterinarioTratante, LocalDate fechaIngreso,
                                     LocalTime horaIngreso, LocalDate fechaAltaEstimada, double pesoActualKg,
                                     double temperaturaC, int frecuenciaCardiaca, String fluidoterapia,
                                     String medicacionActual, String evolucionNotas, String nivelAlerta,
                                     double costoDia, String estado) {
        this.idInternamiento = idInternamiento;
        this.numeroBox = numeroBox;
        this.tipoBox = tipoBox;
        this.codigoMascota = codigoMascota;
        this.nombreMascota = nombreMascota;
        this.especieRaza = especieRaza;
        this.nombreTutor = nombreTutor;
        this.telefonoTutor = telefonoTutor;
        this.diagnosticoIngreso = diagnosticoIngreso;
        this.veterinarioTratante = veterinarioTratante;
        this.fechaIngreso = fechaIngreso != null ? fechaIngreso : LocalDate.now();
        this.horaIngreso = horaIngreso != null ? horaIngreso : LocalTime.now();
        this.fechaAltaEstimada = fechaAltaEstimada;
        this.pesoActualKg = pesoActualKg;
        this.temperaturaC = temperaturaC;
        this.frecuenciaCardiaca = frecuenciaCardiaca;
        this.fluidoterapia = fluidoterapia;
        this.medicacionActual = medicacionActual;
        this.evolucionNotas = evolucionNotas;
        this.nivelAlerta = nivelAlerta != null ? nivelAlerta : "ESTABLE";
        this.costoDia = costoDia;
        this.estado = estado != null ? estado : "Internado / En Tratamiento";
    }

    public String getIdInternamiento() { return idInternamiento; }
    public void setIdInternamiento(String idInternamiento) { this.idInternamiento = idInternamiento; }

    public String getNumeroBox() { return numeroBox; }
    public void setNumeroBox(String numeroBox) { this.numeroBox = numeroBox; }

    public String getTipoBox() { return tipoBox; }
    public void setTipoBox(String tipoBox) { this.tipoBox = tipoBox; }

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

    public String getDiagnosticoIngreso() { return diagnosticoIngreso; }
    public void setDiagnosticoIngreso(String diagnosticoIngreso) { this.diagnosticoIngreso = diagnosticoIngreso; }

    public String getVeterinarioTratante() { return veterinarioTratante; }
    public void setVeterinarioTratante(String veterinarioTratante) { this.veterinarioTratante = veterinarioTratante; }

    public LocalDate getFechaIngreso() { return fechaIngreso; }
    public void setFechaIngreso(LocalDate fechaIngreso) { this.fechaIngreso = fechaIngreso; }
    public String getFechaIngresoFormateada() { return fechaIngreso != null ? fechaIngreso.format(FECHA_FORMATTER) : "-"; }

    public LocalTime getHoraIngreso() { return horaIngreso; }
    public void setHoraIngreso(LocalTime horaIngreso) { this.horaIngreso = horaIngreso; }
    public String getHoraIngresoFormateada() { return horaIngreso != null ? horaIngreso.format(HORA_FORMATTER) : "-"; }

    public LocalDate getFechaAltaEstimada() { return fechaAltaEstimada; }
    public void setFechaAltaEstimada(LocalDate fechaAltaEstimada) { this.fechaAltaEstimada = fechaAltaEstimada; }
    public String getFechaAltaEstimadaFormateada() { return fechaAltaEstimada != null ? fechaAltaEstimada.format(FECHA_FORMATTER) : "A determinar"; }

    public double getPesoActualKg() { return pesoActualKg; }
    public void setPesoActualKg(double pesoActualKg) { this.pesoActualKg = pesoActualKg; }

    public double getTemperaturaC() { return temperaturaC; }
    public void setTemperaturaC(double temperaturaC) { this.temperaturaC = temperaturaC; }

    public int getFrecuenciaCardiaca() { return frecuenciaCardiaca; }
    public void setFrecuenciaCardiaca(int frecuenciaCardiaca) { this.frecuenciaCardiaca = frecuenciaCardiaca; }

    public String getFluidoterapia() { return fluidoterapia; }
    public void setFluidoterapia(String fluidoterapia) { this.fluidoterapia = fluidoterapia; }

    public String getMedicacionActual() { return medicacionActual; }
    public void setMedicacionActual(String medicacionActual) { this.medicacionActual = medicacionActual; }

    public String getEvolucionNotas() { return evolucionNotas; }
    public void setEvolucionNotas(String evolucionNotas) { this.evolucionNotas = evolucionNotas; }

    public String getNivelAlerta() { return nivelAlerta; }
    public void setNivelAlerta(String nivelAlerta) { this.nivelAlerta = nivelAlerta; }

    public double getCostoDia() { return costoDia; }
    public void setCostoDia(double costoDia) { this.costoDia = costoDia; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
