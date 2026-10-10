package happypets.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Modelo de datos para Submódulo 6.4: Control de Egresos Operativos.
 * Basado exactamente en el wireframe CONTROL DE EGRESOS OPERATIVOS.pdf.
 */
public class EgresoOperativo {
    public static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private String idEgreso;
    private LocalDate fecha;        // txtFecha
    private String categoria;       // cboCategoria: "Alquiler", "Servicios básicos", "Planilla y Honorarios", etc.
    private String descripcion;     // txtDescripcion
    private String proveedor;       // txtProveedor
    private double monto;           // txtMonto
    private String metodoPago;      // cboMetodoPago: "Efectivo", "Transferencia", "Yape / Plin", "Tarjeta", "Cheque"
    private String comprobante;     // Comprobante / N.° Factura / Operación
    private String estado;          // "Pagado", "Programado", "Auditado"

    public EgresoOperativo(String idEgreso, LocalDate fecha, String categoria, String descripcion,
                           String proveedor, double monto, String metodoPago) {
        this(idEgreso, fecha, categoria, descripcion, proveedor, monto, metodoPago, "-", "Pagado");
    }

    public EgresoOperativo(String idEgreso, LocalDate fecha, String categoria, String descripcion,
                           String proveedor, double monto, String metodoPago, String comprobante,
                           String estado) {
        this.idEgreso = idEgreso;
        this.fecha = fecha != null ? fecha : LocalDate.now();
        this.categoria = categoria != null ? categoria : "Otros Gastos";
        this.descripcion = descripcion != null ? descripcion : "";
        this.proveedor = proveedor != null ? proveedor : "-";
        this.monto = Validacion.importe(monto, "Monto", false);
        this.metodoPago = metodoPago != null ? metodoPago : "Efectivo";
        this.comprobante = comprobante != null ? comprobante : "-";
        this.estado = estado != null ? estado : "Pagado";
    }

    public String getIdEgreso() {
        return idEgreso;
    }

    public void setIdEgreso(String idEgreso) {
        this.idEgreso = idEgreso;
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

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getProveedor() {
        return proveedor;
    }

    public void setProveedor(String proveedor) {
        this.proveedor = proveedor;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = Validacion.importe(monto, "Monto", false);
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public String getComprobante() {
        return comprobante;
    }

    public void setComprobante(String comprobante) {
        this.comprobante = comprobante;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
