package happypets.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Modelo de datos para el Submódulo 3.4: Laboratorio e Imágenes.
 * Gestiona órdenes diagnósticas, procesamiento de muestras, informes radiológicos/ecográficos e histórico.
 */
public class OrdenLaboratorio {
    public static final DateTimeFormatter FECHA_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private String idOrden;
    private String categoria; // "Laboratorio Clínico", "Diagnóstico por Imágenes"
    private String tipoEstudio; // "Hemograma completo", "Bioquímica sanguínea", "Radiografía de tórax", "Ecografía abdominal", "Citología"
    private String codigoMascota;
    private String nombreMascota;
    private String especieRaza;
    private String nombreTutor;
    private String telefonoTutor;
    private LocalDate fechaSolicitud;
    private String prioridad; // "Normal", "Urgente", "Emergencia"
    private String veterinarioSolicitante;
    private String motivoSospecha;
    private LocalDate fechaResultado;
    private String responsableProcesamiento;
    private String resultadoValores;
    private String informeDetallado;
    private String conclusionesRecomendaciones;
    private String estado; // "Solicitado", "En Proceso", "Completado", "Muestra insuficiente"

    public OrdenLaboratorio(String idOrden, String categoria, String tipoEstudio, String codigoMascota,
                            String nombreMascota, String especieRaza, String nombreTutor, String telefonoTutor,
                            LocalDate fechaSolicitud, String prioridad, String veterinarioSolicitante,
                            String motivoSospecha, LocalDate fechaResultado, String responsableProcesamiento,
                            String resultadoValores, String informeDetallado, String conclusionesRecomendaciones,
                            String estado) {
        this.idOrden = idOrden;
        this.categoria = categoria;
        this.tipoEstudio = tipoEstudio;
        this.codigoMascota = codigoMascota;
        this.nombreMascota = nombreMascota;
        this.especieRaza = especieRaza;
        this.nombreTutor = nombreTutor;
        this.telefonoTutor = telefonoTutor;
        this.fechaSolicitud = fechaSolicitud != null ? fechaSolicitud : LocalDate.now();
        this.prioridad = prioridad != null ? prioridad : "Normal";
        this.veterinarioSolicitante = veterinarioSolicitante;
        this.motivoSospecha = motivoSospecha;
        this.fechaResultado = fechaResultado;
        this.responsableProcesamiento = responsableProcesamiento;
        this.resultadoValores = resultadoValores;
        this.informeDetallado = informeDetallado;
        this.conclusionesRecomendaciones = conclusionesRecomendaciones;
        this.estado = estado != null ? estado : "Solicitado";
    }

    public String getIdOrden() { return idOrden; }
    public void setIdOrden(String idOrden) { this.idOrden = idOrden; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public String getTipoEstudio() { return tipoEstudio; }
    public void setTipoEstudio(String tipoEstudio) { this.tipoEstudio = tipoEstudio; }

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

    public LocalDate getFechaSolicitud() { return fechaSolicitud; }
    public void setFechaSolicitud(LocalDate fechaSolicitud) { this.fechaSolicitud = fechaSolicitud; }
    public String getFechaSolicitudFormateada() { return fechaSolicitud != null ? fechaSolicitud.format(FECHA_FORMATTER) : "-"; }

    public String getPrioridad() { return prioridad; }
    public void setPrioridad(String prioridad) { this.prioridad = prioridad; }

    public String getVeterinarioSolicitante() { return veterinarioSolicitante; }
    public void setVeterinarioSolicitante(String veterinarioSolicitante) { this.veterinarioSolicitante = veterinarioSolicitante; }

    public String getMotivoSospecha() { return motivoSospecha; }
    public void setMotivoSospecha(String motivoSospecha) { this.motivoSospecha = motivoSospecha; }

    public LocalDate getFechaResultado() { return fechaResultado; }
    public void setFechaResultado(LocalDate fechaResultado) { this.fechaResultado = fechaResultado; }
    public String getFechaResultadoFormateada() { return fechaResultado != null ? fechaResultado.format(FECHA_FORMATTER) : "-"; }

    public String getResponsableProcesamiento() { return responsableProcesamiento; }
    public void setResponsableProcesamiento(String responsableProcesamiento) { this.responsableProcesamiento = responsableProcesamiento; }

    public String getResultadoValores() { return resultadoValores; }
    public void setResultadoValores(String resultadoValores) { this.resultadoValores = resultadoValores; }

    public String getInformeDetallado() { return informeDetallado; }
    public void setInformeDetallado(String informeDetallado) { this.informeDetallado = informeDetallado; }

    public String getConclusionesRecomendaciones() { return conclusionesRecomendaciones; }
    public void setConclusionesRecomendaciones(String conclusionesRecomendaciones) { this.conclusionesRecomendaciones = conclusionesRecomendaciones; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
