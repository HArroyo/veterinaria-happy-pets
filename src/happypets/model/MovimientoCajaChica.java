package happypets.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Modelo de datos para Submódulo 6.3: Control de Caja Chica.
 * Basado exactamente en el wireframe CONTROL DE CAJA CHICA.pdf.
 */
public class MovimientoCajaChica {
    public static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private String idMovimiento;
    private LocalDate fecha;       // txtFecha
    private String tipo;           // cboTipo: "Ingreso" o "Egreso"
    private String concepto;       // txtConcepto
    private double monto;          // txtMonto
    private String responsable;    // txtResponsable
    private String comprobante;    // txtComprobante (ej. Recibo #042, Boleta B001-102, Vale #15)
    private double saldoResultante;

    public MovimientoCajaChica(String idMovimiento, LocalDate fecha, String tipo,
                               String concepto, double monto, String responsable,
                               String comprobante) {
        this(idMovimiento, fecha, tipo, concepto, monto, responsable, comprobante, 0.0);
    }

    public MovimientoCajaChica(String idMovimiento, LocalDate fecha, String tipo,
                               String concepto, double monto, String responsable,
                               String comprobante, double saldoResultante) {
        this.idMovimiento = idMovimiento;
        this.fecha = fecha != null ? fecha : LocalDate.now();
        this.tipo = (tipo != null && tipo.toLowerCase().contains("egreso")) ? "Egreso" : "Ingreso";
        this.concepto = concepto != null ? concepto : "";
        this.monto = Validacion.importe(monto, "Monto", false);
        this.responsable = responsable != null ? responsable : "Cajero de Turno";
        this.comprobante = comprobante != null ? comprobante : "-";
        this.saldoResultante = saldoResultante;
    }

    public boolean esIngreso() {
        return "Ingreso".equalsIgnoreCase(tipo);
    }

    public boolean esEgreso() {
        return "Egreso".equalsIgnoreCase(tipo);
    }

    public String getIdMovimiento() {
        return idMovimiento;
    }

    public void setIdMovimiento(String idMovimiento) {
        this.idMovimiento = idMovimiento;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getFechaTexto() {
        return fecha != null ? fecha.format(FORMATO_FECHA) : "";
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = (tipo != null && tipo.toLowerCase().contains("egreso")) ? "Egreso" : "Ingreso";
    }

    public String getConcepto() {
        return concepto;
    }

    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = Validacion.importe(monto, "Monto", false);
    }

    public String getResponsable() {
        return responsable;
    }

    public void setResponsable(String responsable) {
        this.responsable = responsable;
    }

    public String getComprobante() {
        return comprobante;
    }

    public void setComprobante(String comprobante) {
        this.comprobante = comprobante;
    }

    public double getSaldoResultante() {
        return saldoResultante;
    }

    public void setSaldoResultante(double saldoResultante) {
        this.saldoResultante = saldoResultante;
    }
}
