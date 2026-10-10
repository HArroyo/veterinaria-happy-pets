package happypets.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Modelo de datos para las Órdenes de Compra del Submódulo 5.3.
 * Controla pedidos a proveedores, costos valorizados, impuestos y estado de recepción.
 */
public class OrdenCompra {
    public static final DateTimeFormatter FECHA_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private String idOrden;
    private String rucProveedor;
    private String nombreProveedor;
    private LocalDate fechaEmision;
    private LocalDate fechaEntregaEstimada;
    private String itemsResumen; // Resumen legible de productos y cantidades
    private double subtotal;
    private double igv; // 18%
    private double total;
    private String estado; // "Borrador", "Enviada a Proveedor", "Recibida en Almacén", "Cancelada"
    private String solicitante;

    public OrdenCompra(String idOrden, String rucProveedor, String nombreProveedor,
                       LocalDate fechaEmision, LocalDate fechaEntregaEstimada,
                       String itemsResumen, double subtotal, double igv, double total,
                       String estado, String solicitante) {
        this.idOrden = idOrden;
        this.rucProveedor = rucProveedor;
        this.nombreProveedor = nombreProveedor;
        this.fechaEmision = fechaEmision != null ? fechaEmision : LocalDate.now();
        this.fechaEntregaEstimada = fechaEntregaEstimada != null ? fechaEntregaEstimada : LocalDate.now().plusDays(5);
        this.itemsResumen = itemsResumen != null ? itemsResumen : "-";
        this.subtotal = subtotal;
        this.igv = igv > 0 ? igv : subtotal * 0.18;
        this.total = total > 0 ? total : this.subtotal + this.igv;
        this.estado = estado != null ? estado : "Borrador";
        this.solicitante = solicitante != null ? solicitante : "Administración Happy Pets";
    }

    public String getIdOrden() { return idOrden; }
    public void setIdOrden(String idOrden) { this.idOrden = idOrden; }

    public String getRucProveedor() { return rucProveedor; }
    public void setRucProveedor(String rucProveedor) { this.rucProveedor = rucProveedor; }

    public String getNombreProveedor() { return nombreProveedor; }
    public void setNombreProveedor(String nombreProveedor) { this.nombreProveedor = nombreProveedor; }

    public LocalDate getFechaEmision() { return fechaEmision; }
    public void setFechaEmision(LocalDate fechaEmision) { this.fechaEmision = fechaEmision; }
    public String getFechaEmisionFormateada() {
        return fechaEmision != null ? fechaEmision.format(FECHA_FORMATTER) : "-";
    }

    public LocalDate getFechaEntregaEstimada() { return fechaEntregaEstimada; }
    public void setFechaEntregaEstimada(LocalDate fechaEntregaEstimada) { this.fechaEntregaEstimada = fechaEntregaEstimada; }
    public String getFechaEntregaEstimadaFormateada() {
        return fechaEntregaEstimada != null ? fechaEntregaEstimada.format(FECHA_FORMATTER) : "-";
    }

    public String getItemsResumen() { return itemsResumen; }
    public void setItemsResumen(String itemsResumen) { this.itemsResumen = itemsResumen; }

    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }

    public double getIgv() { return igv; }
    public void setIgv(double igv) { this.igv = igv; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getSolicitante() { return solicitante; }
    public void setSolicitante(String solicitante) { this.solicitante = solicitante; }
}
