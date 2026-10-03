package happypets.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Modelo de datos para el Submódulo 3.2: Vacunación y Desparasitación.
 * Registra aplicaciones preventivas de biológicos y antiparasitarios con control de lotes y refuerzos.
 */
public class RegistroInmunizacion {
    public static final DateTimeFormatter FECHA_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private String idRegistro;
    private String tipoControl; // "Vacunación", "Desparasitación Interna", "Desparasitación Externa"
    private String codigoMascota;
    private String nombreMascota;
    private String especieRaza;
    private String nombreTutor;
    private String telefonoTutor;
    private String producto; // e.g. "Sextuple Canina DHPP+L", "Simparica Trio"
    private String laboratorioLote;
    private String dosis; // e.g. "1.0 ml Subcutánea", "1 tableta masticable"
    private LocalDate fechaAplicacion;
    private LocalDate fechaVencimientoFrasco;
    private LocalDate proximaFechaSugerida;
    private String veterinario;
    private double pesoAplicacionKg;
    private String observaciones;
    private String estado; // "Aplicada", "Próxima", "Pendiente"

    public RegistroInmunizacion(String idRegistro, String tipoControl, String codigoMascota, String nombreMascota,
                                 String especieRaza, String nombreTutor, String telefonoTutor, String producto,
                                 String laboratorioLote, String dosis, LocalDate fechaAplicacion,
                                 LocalDate fechaVencimientoFrasco, LocalDate proximaFechaSugerida,
                                 String veterinario, double pesoAplicacionKg, String observaciones, String estado) {
        this.idRegistro = idRegistro;
        this.tipoControl = tipoControl;
        this.codigoMascota = codigoMascota;
        this.nombreMascota = nombreMascota;
        this.especieRaza = especieRaza;
        this.nombreTutor = nombreTutor;
        this.telefonoTutor = telefonoTutor;
        this.producto = producto;
        this.laboratorioLote = laboratorioLote;
        this.dosis = dosis;
        this.fechaAplicacion = fechaAplicacion != null ? fechaAplicacion : LocalDate.now();
        this.fechaVencimientoFrasco = fechaVencimientoFrasco;
        this.proximaFechaSugerida = proximaFechaSugerida;
        this.veterinario = veterinario;
        this.pesoAplicacionKg = pesoAplicacionKg;
        this.observaciones = observaciones;
        this.estado = estado != null ? estado : "Aplicada";
    }

    public String getIdRegistro() { return idRegistro; }
    public void setIdRegistro(String idRegistro) { this.idRegistro = idRegistro; }

    public String getTipoControl() { return tipoControl; }
    public void setTipoControl(String tipoControl) { this.tipoControl = tipoControl; }

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

    public String getProducto() { return producto; }
    public void setProducto(String producto) { this.producto = producto; }

    public String getLaboratorioLote() { return laboratorioLote; }
    public void setLaboratorioLote(String laboratorioLote) { this.laboratorioLote = laboratorioLote; }

    public String getDosis() { return dosis; }
    public void setDosis(String dosis) { this.dosis = dosis; }

    public LocalDate getFechaAplicacion() { return fechaAplicacion; }
    public void setFechaAplicacion(LocalDate fechaAplicacion) { this.fechaAplicacion = fechaAplicacion; }
    public String getFechaAplicacionFormateada() { return fechaAplicacion != null ? fechaAplicacion.format(FECHA_FORMATTER) : "-"; }

    public LocalDate getFechaVencimientoFrasco() { return fechaVencimientoFrasco; }
    public void setFechaVencimientoFrasco(LocalDate fechaVencimientoFrasco) { this.fechaVencimientoFrasco = fechaVencimientoFrasco; }
    public String getFechaVencimientoFormateada() { return fechaVencimientoFrasco != null ? fechaVencimientoFrasco.format(FECHA_FORMATTER) : "-"; }

    public LocalDate getProximaFechaSugerida() { return proximaFechaSugerida; }
    public void setProximaFechaSugerida(LocalDate proximaFechaSugerida) { this.proximaFechaSugerida = proximaFechaSugerida; }
    public String getProximaFechaFormateada() { return proximaFechaSugerida != null ? proximaFechaSugerida.format(FECHA_FORMATTER) : "-"; }

    public String getVeterinario() { return veterinario; }
    public void setVeterinario(String veterinario) { this.veterinario = veterinario; }

    public double getPesoAplicacionKg() { return pesoAplicacionKg; }
    public void setPesoAplicacionKg(double pesoAplicacionKg) { this.pesoAplicacionKg = pesoAplicacionKg; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
