package happypets.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Modelo de datos para el Submódulo 5.4: Ajustes y Mermas.
 * Registra bajas por vencimiento, roturas, descarte biológico y regularizaciones de inventario físico.
 */
public class AjusteMerma {
    public static final DateTimeFormatter FECHA_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private String idAjuste;
    private LocalDate fecha;
    private String codigoProducto;
    private String nombreProducto;
    private String numeroLote;
    private String tipo; // "Merma por Rotura / Deterioro", "Vencimiento de Lote", "Falla Cadena de Frío", "Ajuste Físico Negativo", "Ajuste Físico Positivo"
    private int cantidad;
    private double costoUnitario;
    private double perdidaValorizada;
    private String motivoDetallado;
    private String autorizadoPor;
    private String estado; // "Aprobado y Descargado", "Pendiente de Visto Bueno"

    public AjusteMerma(String idAjuste, LocalDate fecha, String codigoProducto, String nombreProducto,
                       String numeroLote, String tipo, int cantidad, double costoUnitario,
                       double perdidaValorizada, String motivoDetallado, String autorizadoPor, String estado) {
        this.idAjuste = idAjuste;
        this.fecha = fecha != null ? fecha : LocalDate.now();
        this.codigoProducto = codigoProducto;
        this.nombreProducto = nombreProducto;
        this.numeroLote = numeroLote != null ? numeroLote : "-";
        this.tipo = tipo != null ? tipo : "Merma";
        this.cantidad = cantidad;
        this.costoUnitario = costoUnitario;
        this.perdidaValorizada = perdidaValorizada > 0 ? perdidaValorizada : (cantidad * costoUnitario);
        this.motivoDetallado = motivoDetallado != null ? motivoDetallado : "-";
        this.autorizadoPor = autorizadoPor != null ? autorizadoPor : "Dirección Médica";
        this.estado = estado != null ? estado : "Aprobado y Descargado";
    }

    public String getIdAjuste() { return idAjuste; }
    public void setIdAjuste(String idAjuste) { this.idAjuste = idAjuste; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public String getFechaFormateada() {
        return fecha != null ? fecha.format(FECHA_FORMATTER) : "-";
    }

    public String getCodigoProducto() { return codigoProducto; }
    public void setCodigoProducto(String codigoProducto) { this.codigoProducto = codigoProducto; }

    public String getNombreProducto() { return nombreProducto; }
    public void setNombreProducto(String nombreProducto) { this.nombreProducto = nombreProducto; }

    public String getNumeroLote() { return numeroLote; }
    public void setNumeroLote(String numeroLote) { this.numeroLote = numeroLote; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    public double getCostoUnitario() { return costoUnitario; }
    public void setCostoUnitario(double costoUnitario) { this.costoUnitario = costoUnitario; }

    public double getPerdidaValorizada() { return perdidaValorizada; }
    public void setPerdidaValorizada(double perdidaValorizada) { this.perdidaValorizada = perdidaValorizada; }

    public String getMotivoDetallado() { return motivoDetallado; }
    public void setMotivoDetallado(String motivoDetallado) { this.motivoDetallado = motivoDetallado; }

    public String getAutorizadoPor() { return autorizadoPor; }
    public void setAutorizadoPor(String autorizadoPor) { this.autorizadoPor = autorizadoPor; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
