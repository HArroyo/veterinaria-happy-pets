package happypets.model;

import java.io.Serializable;

/**
 * Modelo para servicios y pasarelas de conexión externa (Módulo 10).
 */
public class IntegracionExterna implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String nombre;
    private String subtitulo;
    private String categoria;
    private boolean activa;
    private String endpointUrl;
    private String apiKey;
    private String ambiente; // "Producción", "Sandbox / Pruebas"
    private String ultimaSincronizacion;

    public IntegracionExterna() {
    }

    public IntegracionExterna(String id, String nombre, String subtitulo, String categoria,
                              boolean activa, String endpointUrl, String apiKey,
                              String ambiente, String ultimaSincronizacion) {
        this.id = id;
        this.nombre = nombre;
        this.subtitulo = subtitulo;
        this.categoria = categoria;
        this.activa = activa;
        this.endpointUrl = endpointUrl;
        this.apiKey = apiKey;
        this.ambiente = ambiente;
        this.ultimaSincronizacion = ultimaSincronizacion;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getSubtitulo() {
        return subtitulo;
    }

    public void setSubtitulo(String subtitulo) {
        this.subtitulo = subtitulo;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public boolean isActiva() {
        return activa;
    }

    public void setActiva(boolean activa) {
        this.activa = activa;
    }

    public String getEndpointUrl() {
        return endpointUrl;
    }

    public void setEndpointUrl(String endpointUrl) {
        this.endpointUrl = endpointUrl;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getAmbiente() {
        return ambiente;
    }

    public void setAmbiente(String ambiente) {
        this.ambiente = ambiente;
    }

    public String getUltimaSincronizacion() {
        return ultimaSincronizacion;
    }

    public void setUltimaSincronizacion(String ultimaSincronizacion) {
        this.ultimaSincronizacion = ultimaSincronizacion;
    }
}
