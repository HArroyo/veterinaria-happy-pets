package happypets.model;

import java.time.LocalDate;
import java.time.Period;

/**
 * Paciente veterinario asociado a un cliente responsable.
 */
public class Mascota {
    private String codigo;
    private String codigoCliente;
    private String nombre;
    private String especie;
    private String raza;
    private String edadTexto;
    private String sexo;
    private LocalDate fechaNacimiento;
    private boolean esterilizado;
    private LocalDate fechaIngreso;
    private double pesoActualKg;
    private String microchip;
    private String planVacunal;
    private String alergias;
    private boolean activo = true;

    public Mascota() {
    }

    public Mascota(String codigo, String codigoCliente, String nombre, String especie,
                   String raza, String edadTexto, String sexo, double pesoActualKg,
                   String planVacunal, String alergias, boolean activo) {
        this.codigo = codigo;
        this.codigoCliente = codigoCliente;
        this.nombre = nombre;
        this.especie = especie;
        this.raza = raza;
        this.edadTexto = edadTexto;
        this.sexo = sexo;
        this.pesoActualKg = pesoActualKg;
        this.planVacunal = planVacunal;
        this.alergias = alergias;
        this.activo = activo;
    }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getCodigoCliente() { return codigoCliente; }
    public void setCodigoCliente(String codigoCliente) { this.codigoCliente = codigoCliente; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEspecie() { return especie; }
    public void setEspecie(String especie) { this.especie = especie; }

    public String getRaza() { return raza; }
    public void setRaza(String raza) { this.raza = raza; }

    public String getEspecieRaza() {
        return (especie != null ? especie : "") + " / " + (raza != null ? raza : "");
    }

    public String getEdadTexto() {
        if (edadTexto != null && !edadTexto.trim().isEmpty()) {
            return edadTexto;
        }
        if (fechaNacimiento != null) {
            Period p = Period.between(fechaNacimiento, LocalDate.now());
            if (p.getYears() > 0) {
                return p.getYears() + " año" + (p.getYears() > 1 ? "s" : "") +
                        (p.getMonths() > 0 ? " " + p.getMonths() + " m." : "");
            } else {
                return p.getMonths() + " meses";
            }
        }
        return "-";
    }
    public void setEdadTexto(String edadTexto) { this.edadTexto = edadTexto; }

    public String getSexo() { return sexo; }
    public void setSexo(String sexo) { this.sexo = sexo; }

    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }

    public boolean isEsterilizado() { return esterilizado; }
    public void setEsterilizado(boolean esterilizado) { this.esterilizado = esterilizado; }

    public LocalDate getFechaIngreso() { return fechaIngreso; }
    public void setFechaIngreso(LocalDate fechaIngreso) { this.fechaIngreso = fechaIngreso; }

    public double getPesoActualKg() { return pesoActualKg; }
    public void setPesoActualKg(double pesoActualKg) { this.pesoActualKg = pesoActualKg; }

    public String getMicrochip() { return microchip; }
    public void setMicrochip(String microchip) { this.microchip = microchip; }

    public String getPlanVacunal() { return planVacunal; }
    public void setPlanVacunal(String planVacunal) { this.planVacunal = planVacunal; }

    public String getAlergias() { return alergias; }
    public void setAlergias(String alergias) { this.alergias = alergias; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public String getEstadoTexto() {
        return activo ? "Activo" : "Inactivo";
    }

    @Override
    public String toString() {
        return nombre + " (" + codigo + ")";
    }
}
