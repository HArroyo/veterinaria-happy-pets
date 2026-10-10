package happypets.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Modelo de datos para una venta en el Punto de Venta (POS).
 * Submódulo 6.1: Punto de Venta (POS).
 */
public class VentaPOS {
    public static final DateTimeFormatter FORMATO_FECHA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    public static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private String idVenta;
    private String numeroComprobante; // Ej: B001-000421 o TK-00812
    private String tipoComprobante;   // "Boleta Electrónica", "Factura Electrónica", "Ticket POS", "Cotización"
    private LocalDateTime fechaHora;
    private String clienteNombre;
    private String clienteDocumento;
    private String mascotaNombre;
    private List<ItemVentaPOS> items;
    private double subtotal;
    private double porcentajeDescuento;
    private double montoDescuento;
    private double igv;
    private double total;
    private String metodoPago;        // "Efectivo", "Yape / Plin", "Tarjeta Débito / Crédito", "Transferencia"
    private double montoRecibido;
    private double vuelto;
    private String estado;            // "Pagada", "Cotización", "Anulada"
    private String cajero;

    public VentaPOS() {
        this.items = new ArrayList<>();
        this.fechaHora = LocalDateTime.now();
        this.estado = "Pagada";
    }

    public VentaPOS(String idVenta, String numeroComprobante, String tipoComprobante,
                    LocalDateTime fechaHora, String clienteNombre, String clienteDocumento,
                    String mascotaNombre, List<ItemVentaPOS> items, double porcentajeDescuento,
                    String metodoPago, double montoRecibido, String estado, String cajero) {
        this.idVenta = idVenta;
        this.numeroComprobante = numeroComprobante;
        this.tipoComprobante = tipoComprobante != null ? tipoComprobante : "Boleta Electrónica";
        this.fechaHora = fechaHora != null ? fechaHora : LocalDateTime.now();
        this.clienteNombre = clienteNombre != null ? clienteNombre : "Cliente Varios";
        this.clienteDocumento = clienteDocumento != null ? clienteDocumento : "-";
        this.mascotaNombre = mascotaNombre != null ? mascotaNombre : "-";
        this.items = copiarItems(items);
        this.porcentajeDescuento = porcentajeDescuento;
        this.metodoPago = metodoPago != null ? metodoPago : "Efectivo";
        this.montoRecibido = montoRecibido;
        this.estado = estado != null ? estado : "Pagada";
        this.cajero = cajero != null ? cajero : "Caja Central";
        recalcularTotales();
    }

    public void recalcularTotales() {
        double sub = 0.0;
        for (ItemVentaPOS item : items) {
            sub += item.getSubtotal();
        }
        this.subtotal = sub;
        this.montoDescuento = this.subtotal * (this.porcentajeDescuento / 100.0);
        double baseImponible = Math.max(0.0, this.subtotal - this.montoDescuento);
        this.igv = baseImponible * 18.0 / 118.0; // Porción del IGV incluida en el PVP
        this.total = baseImponible; // En comercio retail con IGV ya incluido en PVP
        if (this.montoRecibido >= this.total) {
            this.vuelto = this.montoRecibido - this.total;
        } else {
            this.vuelto = 0.0;
        }
    }

    public String getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(String idVenta) {
        this.idVenta = idVenta;
    }

    public String getNumeroComprobante() {
        return numeroComprobante;
    }

    public void setNumeroComprobante(String numeroComprobante) {
        this.numeroComprobante = numeroComprobante;
    }

    public String getTipoComprobante() {
        return tipoComprobante;
    }

    public void setTipoComprobante(String tipoComprobante) {
        this.tipoComprobante = tipoComprobante;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getFechaHoraTexto() {
        return fechaHora != null ? fechaHora.format(FORMATO_FECHA_HORA) : "";
    }

    public String getFechaTexto() {
        return fechaHora != null ? fechaHora.format(FORMATO_FECHA) : "";
    }

    public String getClienteNombre() {
        return clienteNombre;
    }

    public void setClienteNombre(String clienteNombre) {
        this.clienteNombre = clienteNombre;
    }

    public String getClienteDocumento() {
        return clienteDocumento;
    }

    public void setClienteDocumento(String clienteDocumento) {
        this.clienteDocumento = clienteDocumento;
    }

    public String getMascotaNombre() {
        return mascotaNombre;
    }

    public void setMascotaNombre(String mascotaNombre) {
        this.mascotaNombre = mascotaNombre;
    }

    public List<ItemVentaPOS> getItems() {
        return copiarItems(items);
    }

    public void setItems(List<ItemVentaPOS> items) {
        this.items = copiarItems(items);
        recalcularTotales();
    }

    public void agregarItem(ItemVentaPOS item) {
        // Si el item ya existe, sumamos la cantidad
        for (ItemVentaPOS exist : items) {
            if (exist.getCodigo().equalsIgnoreCase(item.getCodigo())) {
                exist.incrementarCantidad(item.getCantidad());
                recalcularTotales();
                return;
            }
        }
        items.add(new ItemVentaPOS(item.getCodigo(), item.getDescripcion(), item.getCategoria(), item.getCantidad(), item.getPrecioUnitario()));
        recalcularTotales();
    }

    public void eliminarItem(int index) {
        if (index >= 0 && index < items.size()) {
            items.remove(index);
            recalcularTotales();
        }
    }

    public void limpiarItems() {
        items.clear();
        recalcularTotales();
    }

    public double getSubtotal() {
        return subtotal;
    }

    public double getPorcentajeDescuento() {
        return porcentajeDescuento;
    }

    public void setPorcentajeDescuento(double porcentajeDescuento) {
        this.porcentajeDescuento = Math.max(0.0, Math.min(100.0, porcentajeDescuento));
        recalcularTotales();
    }

    public double getMontoDescuento() {
        return montoDescuento;
    }

    public double getIgv() {
        return igv;
    }

    public double getTotal() {
        return total;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public double getMontoRecibido() {
        return montoRecibido;
    }

    public void setMontoRecibido(double montoRecibido) {
        this.montoRecibido = montoRecibido;
        recalcularTotales();
    }

    public double getVuelto() {
        return vuelto;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getCajero() {
        return cajero;
    }

    public void setCajero(String cajero) {
        this.cajero = cajero;
    }

    public int getCantidadTotalItems() {
        int c = 0;
        for (ItemVentaPOS item : items) {
            c += item.getCantidad();
        }
        return c;
    }

    private static List<ItemVentaPOS> copiarItems(List<ItemVentaPOS> origen) {
        List<ItemVentaPOS> copia = new ArrayList<>();
        if (origen != null) for (ItemVentaPOS item : origen)
            copia.add(new ItemVentaPOS(item.getCodigo(), item.getDescripcion(), item.getCategoria(), item.getCantidad(), item.getPrecioUnitario()));
        return copia;
    }
}
