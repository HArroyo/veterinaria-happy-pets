package happypets.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Modelo de datos para el Submódulo 3.3: Cirugías y Quirófano.
 * Registra programación de salas quirúrgicas, protocolo anestésico intraoperatorio y seguimiento postquirúrgico.
 */
public class RegistroCirugia {
    public static final DateTimeFormatter FECHA_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    public static final DateTimeFormatter HORA_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private String idCirugia;
    private String codigoMascota;
    private String nombreMascota;
    private String especieRaza;
    private String nombreTutor;
    private String telefonoTutor;
    private String procedimiento; // e.g. "Ovariohisterectomía", "Profilaxis Dental + Extracción", "Excisión de Tumor"
    private String quirofano; // "Quirófano 1 (Cirugía Mayor)", "Quirófano 2 (Procedimientos)"
    private String cirujanoPrincipal;
    private String personalApoyo;
    private LocalDate fecha;
    private LocalTime horaProgramada;
    private LocalTime horaInicioReal;
    private LocalTime horaFinReal;
    private String tipoAnestesia; // "Inhalatoria Isoflurano", "Intravenosa TIVA", "Sedación profunda"
    private String medicamentosInsumos;
    private String observacionesProtocolo;
    private int escalaDolorPostop; // 1 al 10
    private String estadoGeneralAlta; // "Alerta y estable", "En recuperación anestésica", "Hospitalizado"
    private String indicacionesPostop;
    private LocalDate fechaRetiroPuntos;
    private String estado; // "Programada", "En Proceso", "En Recuperación", "Alta Quirúrgica"

    public RegistroCirugia(String idCirugia, String codigoMascota, String nombreMascota, String especieRaza,
                           String nombreTutor, String telefonoTutor, String procedimiento, String quirofano,
                           String cirujanoPrincipal, String personalApoyo, LocalDate fecha,
                           LocalTime horaProgramada, LocalTime horaInicioReal, LocalTime horaFinReal,
                           String tipoAnestesia, String medicamentosInsumos, String observacionesProtocolo,
                           int escalaDolorPostop, String estadoGeneralAlta, String indicacionesPostop,
                           LocalDate fechaRetiroPuntos, String estado) {
        this.idCirugia = idCirugia;
        this.codigoMascota = codigoMascota;
        this.nombreMascota = nombreMascota;
        this.especieRaza = especieRaza;
        this.nombreTutor = nombreTutor;
        this.telefonoTutor = telefonoTutor;
        this.procedimiento = procedimiento;
        this.quirofano = quirofano;
        this.cirujanoPrincipal = cirujanoPrincipal;
        this.personalApoyo = personalApoyo;
        this.fecha = fecha != null ? fecha : LocalDate.now();
        this.horaProgramada = horaProgramada != null ? horaProgramada : LocalTime.of(9, 0);
        this.horaInicioReal = horaInicioReal;
        this.horaFinReal = horaFinReal;
        this.tipoAnestesia = tipoAnestesia;
        this.medicamentosInsumos = medicamentosInsumos;
        this.observacionesProtocolo = observacionesProtocolo;
        this.escalaDolorPostop = escalaDolorPostop;
        this.estadoGeneralAlta = estadoGeneralAlta;
        this.indicacionesPostop = indicacionesPostop;
        this.fechaRetiroPuntos = fechaRetiroPuntos;
        this.estado = estado != null ? estado : "Programada";
    }

    public String getIdCirugia() { return idCirugia; }
    public void setIdCirugia(String idCirugia) { this.idCirugia = idCirugia; }

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

    public String getProcedimiento() { return procedimiento; }
    public void setProcedimiento(String procedimiento) { this.procedimiento = procedimiento; }

    public String getQuirofano() { return quirofano; }
    public void setQuirofano(String quirofano) { this.quirofano = quirofano; }

    public String getCirujanoPrincipal() { return cirujanoPrincipal; }
    public void setCirujanoPrincipal(String cirujanoPrincipal) { this.cirujanoPrincipal = cirujanoPrincipal; }

    public String getPersonalApoyo() { return personalApoyo; }
    public void setPersonalApoyo(String personalApoyo) { this.personalApoyo = personalApoyo; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public String getFechaFormateada() { return fecha != null ? fecha.format(FECHA_FORMATTER) : "-"; }

    public LocalTime getHoraProgramada() { return horaProgramada; }
    public void setHoraProgramada(LocalTime horaProgramada) { this.horaProgramada = horaProgramada; }
    public String getHoraProgramadaFormateada() { return horaProgramada != null ? horaProgramada.format(HORA_FORMATTER) : "-"; }

    public LocalTime getHoraInicioReal() { return horaInicioReal; }
    public void setHoraInicioReal(LocalTime horaInicioReal) { this.horaInicioReal = horaInicioReal; }
    public String getHoraInicioFormateada() { return horaInicioReal != null ? horaInicioReal.format(HORA_FORMATTER) : "--:--"; }

    public LocalTime getHoraFinReal() { return horaFinReal; }
    public void setHoraFinReal(LocalTime horaFinReal) { this.horaFinReal = horaFinReal; }
    public String getHoraFinFormateada() { return horaFinReal != null ? horaFinReal.format(HORA_FORMATTER) : "--:--"; }

    public String getTipoAnestesia() { return tipoAnestesia; }
    public void setTipoAnestesia(String tipoAnestesia) { this.tipoAnestesia = tipoAnestesia; }

    public String getMedicamentosInsumos() { return medicamentosInsumos; }
    public void setMedicamentosInsumos(String medicamentosInsumos) { this.medicamentosInsumos = medicamentosInsumos; }

    public String getObservacionesProtocolo() { return observacionesProtocolo; }
    public void setObservacionesProtocolo(String observacionesProtocolo) { this.observacionesProtocolo = observacionesProtocolo; }

    public int getEscalaDolorPostop() { return escalaDolorPostop; }
    public void setEscalaDolorPostop(int escalaDolorPostop) { this.escalaDolorPostop = escalaDolorPostop; }

    public String getEstadoGeneralAlta() { return estadoGeneralAlta; }
    public void setEstadoGeneralAlta(String estadoGeneralAlta) { this.estadoGeneralAlta = estadoGeneralAlta; }

    public String getIndicacionesPostop() { return indicacionesPostop; }
    public void setIndicacionesPostop(String indicacionesPostop) { this.indicacionesPostop = indicacionesPostop; }

    public LocalDate getFechaRetiroPuntos() { return fechaRetiroPuntos; }
    public void setFechaRetiroPuntos(LocalDate fechaRetiroPuntos) { this.fechaRetiroPuntos = fechaRetiroPuntos; }
    public String getFechaRetiroPuntosFormateada() { return fechaRetiroPuntos != null ? fechaRetiroPuntos.format(FECHA_FORMATTER) : "-"; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
