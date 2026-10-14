package happypets.model;

import java.io.Serializable;

/**
 * Modelo para los parámetros generales y datos institucionales de la clínica veterinaria (Módulo 10).
 */
public class ConfiguracionClinica implements Serializable {
    private static final long serialVersionUID = 1L;

    private String razonSocial;
    private String nombreComercial;
    private String identificadorFiscal; // NIT / RUC
    private String correoInstitucional;
    private String telefonoUrgencias;
    private String direccionSedePrincipal;
    private String monedaPrincipal;     // Ej: "COP ($ - Peso Colombiano)" o "PEN (S/ - Sol Peruano)"
    private String zonaHoraria;         // Ej: "America/Bogota (UTC -05:00)" o "America/Lima (UTC -05:00)"
    private String rutaLogotipo;
    private String sedeActiva;

    /**
     * Constructor de ConfiguracionClinica.
     * Inicializa los componentes gráficos, configura layouts, asigna estilos
     * institucionales y carga los datos desde el repositorio central.
     */
    public ConfiguracionClinica() {
        this.razonSocial = "HappyPets Servicios Veterinarios S.A.C.";
        this.nombreComercial = "HappyPets Clínica y Hospital Veterinario 24H";
        this.identificadorFiscal = "RUC 20608421154";
        this.correoInstitucional = "contacto@happypets.pe";
        this.telefonoUrgencias = "+51 (01) 432-9980 / +51 984 552 110";
        this.direccionSedePrincipal = "Av. Angamos Oeste 840, Miraflores, Lima - Perú";
        this.monedaPrincipal = "PEN (S/) - Sol Peruano";
        this.zonaHoraria = "America/Lima (UTC -05:00)";
        this.rutaLogotipo = "assets/logo_happypets.png";
        this.sedeActiva = "Sede Principal - Miraflores";
    }

    /**
     * Constructor de ConfiguracionClinica.
     * Inicializa los componentes gráficos, configura layouts, asigna estilos
     * institucionales y carga los datos desde el repositorio central.
     */
    public ConfiguracionClinica(String razonSocial, String nombreComercial, String identificadorFiscal,
                                String correoInstitucional, String telefonoUrgencias, String direccionSedePrincipal,
                                String monedaPrincipal, String zonaHoraria, String rutaLogotipo, String sedeActiva) {
        this.razonSocial = razonSocial;
        this.nombreComercial = nombreComercial;
        this.identificadorFiscal = identificadorFiscal;
        this.correoInstitucional = correoInstitucional;
        this.telefonoUrgencias = telefonoUrgencias;
        this.direccionSedePrincipal = direccionSedePrincipal;
        this.monedaPrincipal = monedaPrincipal;
        this.zonaHoraria = zonaHoraria;
        this.rutaLogotipo = rutaLogotipo;
        this.sedeActiva = sedeActiva;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public String getNombreComercial() {
        return nombreComercial;
    }

    public void setNombreComercial(String nombreComercial) {
        this.nombreComercial = nombreComercial;
    }

    public String getIdentificadorFiscal() {
        return identificadorFiscal;
    }

    public void setIdentificadorFiscal(String identificadorFiscal) {
        this.identificadorFiscal = identificadorFiscal;
    }

    public String getCorreoInstitucional() {
        return correoInstitucional;
    }

    public void setCorreoInstitucional(String correoInstitucional) {
        this.correoInstitucional = correoInstitucional;
    }

    public String getTelefonoUrgencias() {
        return telefonoUrgencias;
    }

    public void setTelefonoUrgencias(String telefonoUrgencias) {
        this.telefonoUrgencias = telefonoUrgencias;
    }

    public String getDireccionSedePrincipal() {
        return direccionSedePrincipal;
    }

    public void setDireccionSedePrincipal(String direccionSedePrincipal) {
        this.direccionSedePrincipal = direccionSedePrincipal;
    }

    public String getMonedaPrincipal() {
        return monedaPrincipal;
    }

    public void setMonedaPrincipal(String monedaPrincipal) {
        this.monedaPrincipal = monedaPrincipal;
    }

    public String getZonaHoraria() {
        return zonaHoraria;
    }

    public void setZonaHoraria(String zonaHoraria) {
        this.zonaHoraria = zonaHoraria;
    }

    public String getRutaLogotipo() {
        return rutaLogotipo;
    }

    public void setRutaLogotipo(String rutaLogotipo) {
        this.rutaLogotipo = rutaLogotipo;
    }

    public String getSedeActiva() {
        return sedeActiva;
    }

    public void setSedeActiva(String sedeActiva) {
        this.sedeActiva = sedeActiva;
    }
}
