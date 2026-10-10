package happypets.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Modelo de datos para Submódulo 6.2: Cuentas por Pagar.
 * Basado exactamente en el wireframe CUENTAS POR PAGAR.pdf.
 */
public class CuentaPorPagar {
    public static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private String idCuenta;
    private String proveedor;    // txtProveedor
    private String concepto;     // txtConcepto
    private double monto;        // txtMonto
    private double montoPagado;
    private LocalDate fechaVence;// txtVence
    private LocalDate fechaRegistro;
    private String estado;       // cboEstado: "Pendiente", "Pagado", "Vencido", "Parcial"
    private String rucProveedor;
    private String facturaProveedor;

    public CuentaPorPagar(String idCuenta, String proveedor, String concepto, double monto,
                          LocalDate fechaVence, String estado) {
        this(idCuenta, proveedor, concepto, monto, 0.0, LocalDate.now(), fechaVence, estado, "", "");
    }

    public CuentaPorPagar(String idCuenta, String proveedor, String concepto, double monto,
                          double montoPagado, LocalDate fechaRegistro, LocalDate fechaVence,
                          String estado, String rucProveedor, String facturaProveedor) {
        this.idCuenta = idCuenta;
        this.proveedor = proveedor != null ? proveedor : "";
        this.concepto = concepto != null ? concepto : "";
        this.monto = Validacion.importe(monto, "Monto", false);
        this.montoPagado = Validacion.importe(montoPagado, "Monto pagado", false);
        this.fechaRegistro = fechaRegistro != null ? fechaRegistro : LocalDate.now();
        this.fechaVence = fechaVence != null ? fechaVence : LocalDate.now().plusDays(20);
        this.rucProveedor = rucProveedor != null ? rucProveedor : "";
        this.facturaProveedor = facturaProveedor != null ? facturaProveedor : "";
        setEstado(estado);
    }

    public double getSaldo() {
        return Math.max(0.0, monto - montoPagado);
    }

    public void registrarPago(double pago) {
        Validacion.importe(pago, "Pago", true);
        if (pago > getSaldo()) throw new IllegalArgumentException("El pago supera el saldo pendiente.");
        this.montoPagado += pago;
        if (this.montoPagado >= this.monto) {
            this.estado = "Pagado";
        } else {
            this.estado = "Parcial";
        }
    }

    public String getIdCuenta() {
        return idCuenta;
    }

    public void setIdCuenta(String idCuenta) {
        this.idCuenta = idCuenta;
    }

    public String getProveedor() {
        return proveedor;
    }

    public void setProveedor(String proveedor) {
        this.proveedor = proveedor;
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

    public double getMontoPagado() {
        return montoPagado;
    }

    public void setMontoPagado(double montoPagado) {
        this.montoPagado = Validacion.importe(montoPagado, "Monto pagado", false);
    }

    public LocalDate getFechaVence() {
        return fechaVence;
    }

    public void setFechaVence(LocalDate fechaVence) {
        this.fechaVence = fechaVence;
        revisarVencimiento();
    }

    public String getFechaVenceTexto() {
        return fechaVence != null ? fechaVence.format(FORMATO_FECHA) : "";
    }

    public LocalDate getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDate fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public String getFechaRegistroTexto() {
        return fechaRegistro != null ? fechaRegistro.format(FORMATO_FECHA) : "";
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        if (estado == null || estado.isEmpty()) {
            this.estado = "Pendiente";
        } else {
            this.estado = estado;
        }
        revisarVencimiento();
    }

    private void revisarVencimiento() {
        if (!"Pagado".equalsIgnoreCase(this.estado)) {
            if (fechaVence != null && fechaVence.isBefore(LocalDate.now())) {
                this.estado = "Vencido";
            }
        }
    }

    public String getRucProveedor() {
        return rucProveedor;
    }

    public void setRucProveedor(String rucProveedor) {
        this.rucProveedor = rucProveedor;
    }

    public String getFacturaProveedor() {
        return facturaProveedor;
    }

    public void setFacturaProveedor(String facturaProveedor) {
        this.facturaProveedor = facturaProveedor;
    }
}
