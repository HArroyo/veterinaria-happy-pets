package happypets.model;

import java.io.Serializable;

/**
 * Modelo para la Matriz del Cuadrante Semanal de Guardias y Turnos Médicos.
 * Módulo 7.3: Personal y RRHH - Horarios y Turnos Médicos.
 */
public class TurnoSemanal implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String profesionalId;
    private String nombreProfesional;
    private String colegiaturaCMPV;
    private String especialidad;
    private String consultorioHabitual;

    // Asignaciones por día de la semana (0: Lun, 1: Mar, 2: Mié, 3: Jue, 4: Vie, 5: Sáb, 6: Dom)
    private String[] horariosDias; // ej: ["08:00 - 15:00 · Cons. 1", ...]
    private String[] tiposTurnoDias; // ej: ["Mañana", "Tarde", "Guardia", "Descanso"]

    public TurnoSemanal() {
        this.horariosDias = new String[7];
        this.tiposTurnoDias = new String[7];
        for (int i = 0; i < 7; i++) {
            this.horariosDias[i] = "Descanso";
            this.tiposTurnoDias[i] = "Descanso";
        }
    }

    public TurnoSemanal(String id, String profesionalId, String nombreProfesional,
                        String colegiaturaCMPV, String especialidad, String consultorioHabitual,
                        String[] horariosDias, String[] tiposTurnoDias) {
        this.id = id;
        this.profesionalId = profesionalId;
        this.nombreProfesional = nombreProfesional;
        this.colegiaturaCMPV = colegiaturaCMPV;
        this.especialidad = especialidad;
        this.consultorioHabitual = consultorioHabitual;
        this.horariosDias = horariosDias != null ? horariosDias : new String[7];
        this.tiposTurnoDias = tiposTurnoDias != null ? tiposTurnoDias : new String[7];
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProfesionalId() {
        return profesionalId;
    }

    public void setProfesionalId(String profesionalId) {
        this.profesionalId = profesionalId;
    }

    public String getNombreProfesional() {
        return nombreProfesional;
    }

    public void setNombreProfesional(String nombreProfesional) {
        this.nombreProfesional = nombreProfesional;
    }

    public String getColegiaturaCMPV() {
        return colegiaturaCMPV;
    }

    public void setColegiaturaCMPV(String colegiaturaCMPV) {
        this.colegiaturaCMPV = colegiaturaCMPV;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public String getConsultorioHabitual() {
        return consultorioHabitual;
    }

    public void setConsultorioHabitual(String consultorioHabitual) {
        this.consultorioHabitual = consultorioHabitual;
    }

    public String[] getHorariosDias() {
        return horariosDias;
    }

    public void setHorariosDias(String[] horariosDias) {
        this.horariosDias = horariosDias;
    }

    public String[] getTiposTurnoDias() {
        return tiposTurnoDias;
    }

    public void setTiposTurnoDias(String[] tiposTurnoDias) {
        this.tiposTurnoDias = tiposTurnoDias;
    }

    public String getHorarioDia(int diaIndex) {
        if (diaIndex >= 0 && diaIndex < horariosDias.length) {
            return horariosDias[diaIndex];
        }
        return "Descanso";
    }

    public void setHorarioDia(int diaIndex, String horario, String tipoTurno) {
        validarHorario(horario, tipoTurno);
        if (diaIndex >= 0 && diaIndex < 7) {
            horariosDias[diaIndex] = horario;
            tiposTurnoDias[diaIndex] = tipoTurno;
        }
    }

    public String getTipoTurnoDia(int diaIndex) {
        if (diaIndex >= 0 && diaIndex < tiposTurnoDias.length) {
            return tiposTurnoDias[diaIndex];
        }
        return "Descanso";
    }

    public static void validarHorario(String horario, String tipo) {
        if (horario == null || horario.isBlank()) throw new IllegalArgumentException("Ingrese el horario o indique Descanso.");
        if (horario.toLowerCase().contains("descanso") || horario.toLowerCase().contains("libre")) return;
        var matcher = java.util.regex.Pattern.compile("(\\d{2}:\\d{2})\\s*-\\s*(\\d{2}:\\d{2})").matcher(horario);
        if (!matcher.find()) throw new IllegalArgumentException("Horario inválido. Use HH:mm - HH:mm.");
        try {
            var inicio = java.time.LocalTime.parse(matcher.group(1));
            var fin = java.time.LocalTime.parse(matcher.group(2));
            if (inicio.equals(fin) || (fin.isBefore(inicio) && (tipo == null || !tipo.toLowerCase().contains("guardia"))))
                throw new IllegalArgumentException("El final del turno debe ser posterior al inicio; las guardias pueden cruzar medianoche.");
        } catch (java.time.DateTimeException ex) { throw new IllegalArgumentException("La hora del turno no es válida."); }
    }

    @Override
    public String toString() {
        return nombreProfesional + " - " + especialidad;
    }
}
