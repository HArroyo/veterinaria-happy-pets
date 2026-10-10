package happypets.model;

import java.io.Serializable;

/**
 * Modelo para las opciones y estado del Módulo de Inteligencia Artificial (Módulo 10).
 */
public class ConfiguracionModuloIA implements Serializable {
    private static final long serialVersionUID = 1L;

    private boolean habilitado;
    private boolean preTriajeMascotas;
    private boolean asistenteDiagnosticoPreliminar;
    private boolean recordatoriosPredictivosVacunas;
    private String modeloActual;
    private String promptSistema;
    private double temperatura;
    private int maxTokens;

    public ConfiguracionModuloIA() {
        this.habilitado = true;
        this.preTriajeMascotas = true;
        this.asistenteDiagnosticoPreliminar = true;
        this.recordatoriosPredictivosVacunas = false;
        this.modeloActual = "HappyPet-Core-v1.8";
        this.promptSistema = "Eres un asistente de soporte veterinario experto y triaje para la Clínica HappyPets. "
                + "Evalúa los signos clínicos reportados, determina el nivel de gravedad (Código Rojo, Amarillo o Verde) "
                + "y formula un resumen de posibles diagnósticos diferenciales y exámenes complementarios prioritarios.";
        this.temperatura = 0.3;
        this.maxTokens = 1024;
    }

    public ConfiguracionModuloIA(boolean habilitado, boolean preTriajeMascotas,
                                 boolean asistenteDiagnosticoPreliminar, boolean recordatoriosPredictivosVacunas,
                                 String modeloActual, String promptSistema) {
        this.habilitado = habilitado;
        this.preTriajeMascotas = preTriajeMascotas;
        this.asistenteDiagnosticoPreliminar = asistenteDiagnosticoPreliminar;
        this.recordatoriosPredictivosVacunas = recordatoriosPredictivosVacunas;
        this.modeloActual = modeloActual;
        this.promptSistema = promptSistema;
        this.temperatura = 0.3;
        this.maxTokens = 1024;
    }

    public boolean isHabilitado() {
        return habilitado;
    }

    public void setHabilitado(boolean habilitado) {
        this.habilitado = habilitado;
    }

    public boolean isPreTriajeMascotas() {
        return preTriajeMascotas;
    }

    public void setPreTriajeMascotas(boolean preTriajeMascotas) {
        this.preTriajeMascotas = preTriajeMascotas;
    }

    public boolean isAsistenteDiagnosticoPreliminar() {
        return asistenteDiagnosticoPreliminar;
    }

    public void setAsistenteDiagnosticoPreliminar(boolean asistenteDiagnosticoPreliminar) {
        this.asistenteDiagnosticoPreliminar = asistenteDiagnosticoPreliminar;
    }

    public boolean isRecordatoriosPredictivosVacunas() {
        return recordatoriosPredictivosVacunas;
    }

    public void setRecordatoriosPredictivosVacunas(boolean recordatoriosPredictivosVacunas) {
        this.recordatoriosPredictivosVacunas = recordatoriosPredictivosVacunas;
    }

    public String getModeloActual() {
        return modeloActual;
    }

    public void setModeloActual(String modeloActual) {
        this.modeloActual = modeloActual;
    }

    public String getPromptSistema() {
        return promptSistema;
    }

    public void setPromptSistema(String promptSistema) {
        this.promptSistema = promptSistema;
    }

    public double getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(double temperatura) {
        this.temperatura = temperatura;
    }

    public int getMaxTokens() {
        return maxTokens;
    }

    public void setMaxTokens(int maxTokens) {
        this.maxTokens = maxTokens;
    }
}
