package happypets.model;

import java.io.Serializable;

/**
 * Modelo para la configuración de canales de notificación (Email, SMS, Push).
 */
public class ConfiguracionCanalNotificacion implements Serializable {
    private static final long serialVersionUID = 1L;

    private String canal;       // "Email", "SMS", "Notificaciones Push"
    private boolean activo;
    private String destino;     // "usuario@empresa.com", "+51 987 654 321", "Dispositivo Móvil / Navegador Web"
    private String frecuencia;  // "Inmediata", "Cada hora", "Resumen Diario", "Semanal"

    public ConfiguracionCanalNotificacion() {
    }

    public ConfiguracionCanalNotificacion(String canal, boolean activo, String destino, String frecuencia) {
        this.canal = canal;
        this.activo = activo;
        this.destino = destino;
        this.frecuencia = frecuencia;
    }

    public String getCanal() {
        return canal;
    }

    public void setCanal(String canal) {
        this.canal = canal;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public String getFrecuencia() {
        return frecuencia;
    }

    public void setFrecuencia(String frecuencia) {
        this.frecuencia = frecuencia;
    }
}
