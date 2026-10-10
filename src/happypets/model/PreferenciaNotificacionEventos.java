package happypets.model;

import java.io.Serializable;

/**
 * Modelo para las preferencias de tipos de eventos a notificar en el sistema.
 */
public class PreferenciaNotificacionEventos implements Serializable {
    private static final long serialVersionUID = 1L;

    private boolean alertasCriticas;
    private boolean documentosNuevos;
    private boolean eventosSistema;
    private boolean recordatoriosClinicos;

    public PreferenciaNotificacionEventos() {
        this.alertasCriticas = true;
        this.documentosNuevos = true;
        this.eventosSistema = true;
        this.recordatoriosClinicos = true;
    }

    public PreferenciaNotificacionEventos(boolean alertasCriticas, boolean documentosNuevos,
                                          boolean eventosSistema, boolean recordatoriosClinicos) {
        this.alertasCriticas = alertasCriticas;
        this.documentosNuevos = documentosNuevos;
        this.eventosSistema = eventosSistema;
        this.recordatoriosClinicos = recordatoriosClinicos;
    }

    public boolean isAlertasCriticas() {
        return alertasCriticas;
    }

    public void setAlertasCriticas(boolean alertasCriticas) {
        this.alertasCriticas = alertasCriticas;
    }

    public boolean isDocumentosNuevos() {
        return documentosNuevos;
    }

    public void setDocumentosNuevos(boolean documentosNuevos) {
        this.documentosNuevos = documentosNuevos;
    }

    public boolean isEventosSistema() {
        return eventosSistema;
    }

    public void setEventosSistema(boolean eventosSistema) {
        this.eventosSistema = eventosSistema;
    }

    public boolean isRecordatoriosClinicos() {
        return recordatoriosClinicos;
    }

    public void setRecordatoriosClinicos(boolean recordatoriosClinicos) {
        this.recordatoriosClinicos = recordatoriosClinicos;
    }
}
