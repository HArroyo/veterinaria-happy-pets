package happypets.model;

/**
 * Detalle o línea de producto/servicio en una transacción de Punto de Venta (POS).
 */
public class ItemVentaPOS {
    private String codigo;
    private String descripcion;
    private String categoria;
    private int cantidad;
    private double precioUnitario;

    public ItemVentaPOS(String codigo, String descripcion, String categoria, int cantidad, double precioUnitario) {
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.categoria = categoria;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getNombre() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public void incrementarCantidad(int delta) {
        this.cantidad = Math.max(1, this.cantidad + delta);
    }

    public double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public double getSubtotal() {
        return cantidad * precioUnitario;
    }

    @Override
    public String toString() {
        return descripcion + " (x" + cantidad + ") - S/ " + String.format("%.2f", getSubtotal());
    }
}
