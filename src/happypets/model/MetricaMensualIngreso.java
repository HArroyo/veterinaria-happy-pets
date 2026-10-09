package happypets.model;

import java.io.Serializable;

/**
 * Modelo para métricas mensuales de ingresos, servicios clínicos y farmacia.
 * Utilizado en Tableros de Mando (Dashboards) y Reportes Financieros.
 */
public class MetricaMensualIngreso implements Serializable {
    private static final long serialVersionUID = 1L;

    private String mesEtiqueta; // Ej: "Jun 23", "Jul 23", ..., "May 24"
    private int anio;
    private int mesNumero;
    private double serviciosClinicos;
    private double farmaciaAlimentos;
    private double costesOperativos;

    public MetricaMensualIngreso() {
    }

    public MetricaMensualIngreso(String mesEtiqueta, int anio, int mesNumero,
                                 double serviciosClinicos, double farmaciaAlimentos, double costesOperativos) {
        this.mesEtiqueta = mesEtiqueta;
        this.anio = anio;
        this.mesNumero = mesNumero;
        this.serviciosClinicos = serviciosClinicos;
        this.farmaciaAlimentos = farmaciaAlimentos;
        this.costesOperativos = costesOperativos;
    }

    public String getMesEtiqueta() {
        return mesEtiqueta;
    }

    public void setMesEtiqueta(String mesEtiqueta) {
        this.mesEtiqueta = mesEtiqueta;
    }

    public int getAnio() {
        return anio;
    }

    public void setAnio(int anio) {
        this.anio = anio;
    }

    public int getMesNumero() {
        return mesNumero;
    }

    public void setMesNumero(int mesNumero) {
        this.mesNumero = mesNumero;
    }

    public double getServiciosClinicos() {
        return serviciosClinicos;
    }

    public void setServiciosClinicos(double serviciosClinicos) {
        this.serviciosClinicos = serviciosClinicos;
    }

    public double getFarmaciaAlimentos() {
        return farmaciaAlimentos;
    }

    public void setFarmaciaAlimentos(double farmaciaAlimentos) {
        this.farmaciaAlimentos = farmaciaAlimentos;
    }

    public double getCostesOperativos() {
        return costesOperativos;
    }

    public void setCostesOperativos(double costesOperativos) {
        this.costesOperativos = costesOperativos;
    }

    public double getTotalIngresos() {
        return serviciosClinicos + farmaciaAlimentos;
    }

    public double getMargenNeto() {
        return getTotalIngresos() - costesOperativos;
    }

    public double getRatioCobroGasto() {
        if (costesOperativos <= 0) return 1.0;
        return getTotalIngresos() / costesOperativos;
    }
}
