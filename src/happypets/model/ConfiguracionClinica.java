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

    public ConfiguracionClinica() {
        this.razonSocial = "HappyPets Servicios Veterinarios S.A.S.";
        this.nombreComercial = "HappyPets Clínica y Hospital 24H";
        this.identificadorFiscal = "NIT 900.842.115-4";
        this.correoInstitucional = "contacto@happypets-vet.com";
        this.telefonoUrgencias = "+57 (601) 745-9988 / +57 312 000 1122";
        this.direccionSedePrincipal = "Avenida Las Mascotas # 45 - 28, Sector San Martín";
        this.monedaPrincipal = "COP ($) - Peso Colombiano";
        this.zonaHoraria = "America/Bogota (UTC -05:00)";
        this.rutaLogotipo = "assets/logo_happypets.png";
        this.sedeActiva = "Sede Norte - Principal";
    }

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
