package happypets.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Modelo de datos para el Submódulo 5.2: Control de Stock y Lotes (Kardex).
 * Registra entradas por compra, salidas por dispensación clínica, transferencias y control de vencimientos.
 */
public class LoteMovimientoStock {
    public static final DateTimeFormatter FECHA_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private String idMovimiento;
    private String codigoProducto;
    private String nombreProducto;
    private String numeroLote;
    private String tipoMovimiento; // "Ingreso por Compra", "Salida por Consulta", "Salida por Cirugía", "Ajuste por Merma"
    private int cantidad;
    private int stockPrevio;
    private int stockPosterior;
    private LocalDate fechaMovimiento;
    private LocalDate fechaVencimientoLote;
    private String responsable;
    private String observacion;

    public LoteMovimientoStock(String idMovimiento, String codigoProducto, String nombreProducto,
                               String numeroLote, String tipoMovimiento, int cantidad,
                               int stockPrevio, int stockPosterior, LocalDate fechaMovimiento,
                               LocalDate fechaVencimientoLote, String responsable, String observacion) {
        this.idMovimiento = idMovimiento;
        this.codigoProducto = codigoProducto;
        this.nombreProducto = nombreProducto;
        this.numeroLote = numeroLote;
        this.tipoMovimiento = tipoMovimiento != null ? tipoMovimiento : "Ingreso";
        this.cantidad = cantidad;
        this.stockPrevio = stockPrevio;
        this.stockPosterior = stockPosterior;
        this.fechaMovimiento = fechaMovimiento != null ? fechaMovimiento : LocalDate.now();
        this.fechaVencimientoLote = fechaVencimientoLote != null ? fechaVencimientoLote : LocalDate.now().plusMonths(12);
        this.responsable = responsable != null ? responsable : "Regente Farmacéutico";
        this.observacion = observacion != null ? observacion : "-";
    }

    public String getIdMovimiento() { return idMovimiento; }
    public void setIdMovimiento(String idMovimiento) { this.idMovimiento = idMovimiento; }

    public String getCodigoProducto() { return codigoProducto; }
    public void setCodigoProducto(String codigoProducto) { this.codigoProducto = codigoProducto; }

    public String getNombreProducto() { return nombreProducto; }
    public void setNombreProducto(String nombreProducto) { this.nombreProducto = nombreProducto; }

    public String getNumeroLote() { return numeroLote; }
    public void setNumeroLote(String numeroLote) { this.numeroLote = numeroLote; }

    public String getTipoMovimiento() { return tipoMovimiento; }
    public void setTipoMovimiento(String tipoMovimiento) { this.tipoMovimiento = tipoMovimiento; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    public int getStockPrevio() { return stockPrevio; }
    public void setStockPrevio(int stockPrevio) { this.stockPrevio = stockPrevio; }

    public int getStockPosterior() { return stockPosterior; }
    public void setStockPosterior(int stockPosterior) { this.stockPosterior = stockPosterior; }

    public LocalDate getFechaMovimiento() { return fechaMovimiento; }
    public void setFechaMovimiento(LocalDate fechaMovimiento) { this.fechaMovimiento = fechaMovimiento; }
    public String getFechaMovimientoFormateada() {
        return fechaMovimiento != null ? fechaMovimiento.format(FECHA_FORMATTER) : "-";
    }

    public LocalDate getFechaVencimientoLote() { return fechaVencimientoLote; }
    public void setFechaVencimientoLote(LocalDate fechaVencimientoLote) { this.fechaVencimientoLote = fechaVencimientoLote; }
    public String getFechaVencimientoLoteFormateada() {
        return fechaVencimientoLote != null ? fechaVencimientoLote.format(FECHA_FORMATTER) : "-";
    }

    public String getResponsable() { return responsable; }
    public void setResponsable(String responsable) { this.responsable = responsable; }

    public String getObservacion() { return observacion; }
    public void setObservacion(String observacion) { this.observacion = observacion; }
}
