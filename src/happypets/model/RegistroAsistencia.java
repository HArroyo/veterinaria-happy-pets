package happypets.model;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Modelo para Pase de Lista Diario y Control de Asistencias.
 * Módulo 7.4: Personal y RRHH - Asistencias y Permisos.
 */
public class RegistroAsistencia implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String personalId;
    private String nombreColaborador;
    private String rol; // "Cirugía General", "Dermatología", "Auxiliar ATV", "Recepción", etc.
    private String categoria; // "Veterinario" o "Apoyo"
    private LocalDate fecha;
    private String turno; // "Mañana (08:00 - 15:00)", "Tarde (15:00 - 22:00)", "Noche (22:00 - 08:00)"
    private String horaEntradaEsperada; // "08:00"
    private String horaSalidaEsperada; // "15:00"
    private String horaEntradaReal; // "07:58", "08:14", "--:--"
    private String horaSalidaReal; // "15:02", "--:--"
    private String estado; // "Presente", "Retardo", "Ausente", "Permiso", "Pendiente"
    private int minutosRetardo; // 0 si puntual o ausente/permiso, >0 si retardo
    private String notaJustificacion;
    private boolean marcadoManual;

    public RegistroAsistencia() {
    }

    public RegistroAsistencia(String id, String personalId, String nombreColaborador,
                              String rol, String categoria, LocalDate fecha, String turno,
                              String horaEntradaEsperada, String horaSalidaEsperada,
                              String horaEntradaReal, String horaSalidaReal, String estado,
                              int minutosRetardo, String notaJustificacion, boolean marcadoManual) {
        this.id = id;
        this.personalId = personalId;
        this.nombreColaborador = nombreColaborador;
        this.rol = rol;
        this.categoria = categoria;
        this.fecha = fecha;
        this.turno = turno;
        this.horaEntradaEsperada = horaEntradaEsperada;
        this.horaSalidaEsperada = horaSalidaEsperada;
        this.horaEntradaReal = horaEntradaReal;
        this.horaSalidaReal = horaSalidaReal;
        this.estado = estado;
        this.minutosRetardo = minutosRetardo;
        this.notaJustificacion = notaJustificacion;
        this.marcadoManual = marcadoManual;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPersonalId() {
        return personalId;
    }

    public void setPersonalId(String personalId) {
        this.personalId = personalId;
    }

    public String getNombreColaborador() {
        return nombreColaborador;
    }

    public void setNombreColaborador(String nombreColaborador) {
        this.nombreColaborador = nombreColaborador;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getTurno() {
        return turno;
    }

    public void setTurno(String turno) {
        this.turno = turno;
    }

    public String getHoraEntradaEsperada() {
        return horaEntradaEsperada;
    }

    public void setHoraEntradaEsperada(String horaEntradaEsperada) {
        this.horaEntradaEsperada = horaEntradaEsperada;
    }

    public String getHoraSalidaEsperada() {
        return horaSalidaEsperada;
    }

    public void setHoraSalidaEsperada(String horaSalidaEsperada) {
        this.horaSalidaEsperada = horaSalidaEsperada;
    }

    public String getHoraEntradaReal() {
        return horaEntradaReal;
    }

    public void setHoraEntradaReal(String horaEntradaReal) {
        this.horaEntradaReal = horaEntradaReal;
    }

    public String getHoraSalidaReal() {
        return horaSalidaReal;
    }

    public void setHoraSalidaReal(String horaSalidaReal) {
        this.horaSalidaReal = horaSalidaReal;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public int getMinutosRetardo() {
        return minutosRetardo;
    }

    public void setMinutosRetardo(int minutosRetardo) {
        this.minutosRetardo = minutosRetardo;
    }

    public String getNotaJustificacion() {
        return notaJustificacion;
    }

    public void setNotaJustificacion(String notaJustificacion) {
        this.notaJustificacion = notaJustificacion;
    }

    public boolean isMarcadoManual() {
        return marcadoManual;
    }

    public void setMarcadoManual(boolean marcadoManual) {
        this.marcadoManual = marcadoManual;
    }

    @Override
    public String toString() {
        return nombreColaborador + " - " + estado + " (" + turno + ")";
    }
}
