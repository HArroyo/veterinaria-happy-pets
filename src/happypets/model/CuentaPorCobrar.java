package happypets.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Modelo de datos para Submódulo 6.2: Cuentas por Cobrar.
 * Basado exactamente en el wireframe CUENTAS POR COBRAR.pdf.
 */
public class CuentaPorCobrar {
    public static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private String idCuenta;
    private String cliente;      // txtCliente
    private String concepto;     // txtConcepto
    private double monto;        // txtMonto
    private double montoPagado;
    private LocalDate fechaVence;// txtVence
    private LocalDate fechaRegistro;
    private String estado;       // cboEstado: "Pendiente", "Pagado", "Vencido", "Parcial"
    private String telefonoCliente;
    private String comprobanteReferencia;

    public CuentaPorCobrar(String idCuenta, String cliente, String concepto, double monto,
                           LocalDate fechaVence, String estado) {
        this(idCuenta, cliente, concepto, monto, 0.0, LocalDate.now(), fechaVence, estado, "", "");
    }

    public CuentaPorCobrar(String idCuenta, String cliente, String concepto, double monto,
                           double montoPagado, LocalDate fechaRegistro, LocalDate fechaVence,
                           String estado, String telefonoCliente, String comprobanteReferencia) {
        this.idCuenta = idCuenta;
        this.cliente = cliente != null ? cliente : "";
        this.concepto = concepto != null ? concepto : "";
        this.monto = Validacion.importe(monto, "Monto", false);
        this.montoPagado = Validacion.importe(montoPagado, "Monto pagado", false);
        this.fechaRegistro = fechaRegistro != null ? fechaRegistro : LocalDate.now();
        this.fechaVence = fechaVence != null ? fechaVence : LocalDate.now().plusDays(15);
        this.telefonoCliente = telefonoCliente != null ? telefonoCliente : "";
        this.comprobanteReferencia = comprobanteReferencia != null ? comprobanteReferencia : "";
        setEstado(estado);
    }

    public double getSaldo() {
        return Math.max(0.0, monto - montoPagado);
    }

    public void registrarAbono(double abono) {
        Validacion.importe(abono, "Abono", true);
        if (abono > getSaldo()) throw new IllegalArgumentException("El abono supera el saldo pendiente.");
        this.montoPagado += abono;
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

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
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

    public String getTelefonoCliente() {
        return telefonoCliente;
    }

    public void setTelefonoCliente(String telefonoCliente) {
        this.telefonoCliente = telefonoCliente;
    }

    public String getComprobanteReferencia() {
        return comprobanteReferencia;
    }

    public void setComprobanteReferencia(String comprobanteReferencia) {
        this.comprobanteReferencia = comprobanteReferencia;
    }
}
