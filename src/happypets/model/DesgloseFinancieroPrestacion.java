package happypets.model;

import java.io.Serializable;

/**
 * Modelo para las líneas de servicio/producto en los reportes financieros por departamento.
 */
public class DesgloseFinancieroPrestacion implements Serializable {
    private static final long serialVersionUID = 1L;

    private String lineaServicio;
    private String centroOperativo;
    private int transacciones;
    private double ingresosBrutos;
    private double costeDirecto;
    private double porcentajeRentabilidad;
    private String estadoFinanciero; // "Óptimo", "Excelente", "Margen Normal", "Estable", "A Revisar"

    public DesgloseFinancieroPrestacion() {
    }

    public DesgloseFinancieroPrestacion(String lineaServicio, String centroOperativo, int transacciones,
                                        double ingresosBrutos, double costeDirecto,
                                        double porcentajeRentabilidad, String estadoFinanciero) {
        this.lineaServicio = lineaServicio;
        this.centroOperativo = centroOperativo;
        this.transacciones = transacciones;
        this.ingresosBrutos = ingresosBrutos;
        this.costeDirecto = costeDirecto;
        this.porcentajeRentabilidad = porcentajeRentabilidad;
        this.estadoFinanciero = estadoFinanciero;
    }

    public String getLineaServicio() {
        return lineaServicio;
    }

    public void setLineaServicio(String lineaServicio) {
        this.lineaServicio = lineaServicio;
    }

    public String getCentroOperativo() {
        return centroOperativo;
    }

    public void setCentroOperativo(String centroOperativo) {
        this.centroOperativo = centroOperativo;
    }

    public int getTransacciones() {
        return transacciones;
    }

    public void setTransacciones(int transacciones) {
        this.transacciones = transacciones;
    }

    public double getIngresosBrutos() {
        return ingresosBrutos;
    }

    public void setIngresosBrutos(double ingresosBrutos) {
        this.ingresosBrutos = ingresosBrutos;
    }

    public double getCosteDirecto() {
        return costeDirecto;
    }

    public void setCosteDirecto(double costeDirecto) {
        this.costeDirecto = costeDirecto;
    }

    public double getMargenBruto() {
        return ingresosBrutos - costeDirecto;
    }

    public double getPorcentajeRentabilidad() {
        return porcentajeRentabilidad;
    }

    public void setPorcentajeRentabilidad(double porcentajeRentabilidad) {
        this.porcentajeRentabilidad = porcentajeRentabilidad;
    }

    public String getEstadoFinanciero() {
        return estadoFinanciero;
    }

    public void setEstadoFinanciero(String estadoFinanciero) {
        this.estadoFinanciero = estadoFinanciero;
    }
}
