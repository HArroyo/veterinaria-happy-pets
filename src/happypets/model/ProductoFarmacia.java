package happypets.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Modelo de datos para el Submódulo 5.1: Catálogo de Productos y Fármacos.
 * Administra medicamentos veterinarios, biológicos, alimentos clínicos y material médico.
 */
public class ProductoFarmacia {
    public static final DateTimeFormatter FECHA_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private String codigo;
    private String nombre;
    private String categoria; // "Antiparasitario", "Antibiótico", "Analgésico / AINE", "Vacuna / Biológico", "Alimento Clínico", "Material Quirúrgico"
    private String principioActivo;
    private String presentacion; // "Caja x 3 comp.", "Frasco 100 ml", "Bolsa 3 kg", "Ampolla 2 ml"
    private String especieDestino; // "Canino", "Felino", "Mixto Canino/Felino"
    private boolean requiereReceta;
    private boolean cadenaFrio; // 2°C - 8°C
    private double precioCosto;
    private double precioVenta;
    private int stockActual;
    private int stockMinimo;
    private String loteActual;
    private LocalDate fechaVencimiento;
    private String proveedor;
    private String estado; // "Disponible", "Bajo Stock", "Agotado"

    public ProductoFarmacia(String codigo, String nombre, String categoria, String principioActivo,
                            String presentacion, String especieDestino, boolean requiereReceta,
                            boolean cadenaFrio, double precioCosto, double precioVenta,
                            int stockActual, int stockMinimo, String loteActual,
                            LocalDate fechaVencimiento, String proveedor, String estado) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.categoria = categoria != null ? categoria : "Medicamento";
        this.principioActivo = principioActivo != null ? principioActivo : "-";
        this.presentacion = presentacion != null ? presentacion : "Unidad";
        this.especieDestino = especieDestino != null ? especieDestino : "Mixto Canino/Felino";
        this.requiereReceta = requiereReceta;
        this.cadenaFrio = cadenaFrio;
        this.precioCosto = precioCosto;
        this.precioVenta = precioVenta;
        this.stockActual = stockActual;
        this.stockMinimo = stockMinimo > 0 ? stockMinimo : 5;
        this.loteActual = loteActual != null ? loteActual : "LOT-ACT";
        this.fechaVencimiento = fechaVencimiento != null ? fechaVencimiento : LocalDate.now().plusMonths(12);
        this.proveedor = proveedor != null ? proveedor : "Laboratorio Veterinario";
        actualizarEstadoCalculado();
    }

    public void actualizarEstadoCalculado() {
        if (stockActual <= 0) {
            this.estado = "Agotado";
        } else if (stockActual <= stockMinimo) {
            this.estado = "Bajo Stock";
        } else {
            this.estado = "Disponible";
        }
    }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public String getPrincipioActivo() { return principioActivo; }
    public void setPrincipioActivo(String principioActivo) { this.principioActivo = principioActivo; }

    public String getPresentacion() { return presentacion; }
    public void setPresentacion(String presentacion) { this.presentacion = presentacion; }

    public String getEspecieDestino() { return especieDestino; }
    public void setEspecieDestino(String especieDestino) { this.especieDestino = especieDestino; }

    public boolean isRequiereReceta() { return requiereReceta; }
    public void setRequiereReceta(boolean requiereReceta) { this.requiereReceta = requiereReceta; }

    public boolean isCadenaFrio() { return cadenaFrio; }
    public void setCadenaFrio(boolean cadenaFrio) { this.cadenaFrio = cadenaFrio; }

    public double getPrecioCosto() { return precioCosto; }
    public void setPrecioCosto(double precioCosto) { this.precioCosto = precioCosto; }

    public double getPrecioVenta() { return precioVenta; }
    public void setPrecioVenta(double precioVenta) { this.precioVenta = precioVenta; }

    public int getStockActual() { return stockActual; }
    public void setStockActual(int stockActual) {
        this.stockActual = stockActual;
        actualizarEstadoCalculado();
    }

    public int getStockMinimo() { return stockMinimo; }
    public void setStockMinimo(int stockMinimo) {
        this.stockMinimo = stockMinimo;
        actualizarEstadoCalculado();
    }

    public String getLoteActual() { return loteActual; }
    public void setLoteActual(String loteActual) { this.loteActual = loteActual; }

    public LocalDate getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(LocalDate fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }
    public String getFechaVencimientoFormateada() {
        return fechaVencimiento != null ? fechaVencimiento.format(FECHA_FORMATTER) : "-";
    }

    public String getProveedor() { return proveedor; }
    public void setProveedor(String proveedor) { this.proveedor = proveedor; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
