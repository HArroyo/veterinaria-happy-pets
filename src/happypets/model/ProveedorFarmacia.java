package happypets.model;

/**
 * Modelo de datos para el Submódulo 5.3: Proveedores Farmacéuticos.
 * Gestiona laboratorios veterinarios homologados, distribuidores y canales de contacto comercial.
 */
public class ProveedorFarmacia {
    private String ruc;
    private String razonSocial;
    private String nombreComercial;
    private String telefono;
    private String correo;
    private String direccion;
    private String contactoAsesor;
    private String condicionPago; // "Crédito 30 días", "Contado Factura", "Crédito 15 días"
    private String estado; // "Homologado / Activo", "En Evaluación", "Inactivo"

    public ProveedorFarmacia(String ruc, String razonSocial, String nombreComercial, String telefono,
                             String correo, String direccion, String contactoAsesor,
                             String condicionPago, String estado) {
        this.ruc = ruc;
        this.razonSocial = razonSocial;
        this.nombreComercial = nombreComercial != null ? nombreComercial : razonSocial;
        this.telefono = telefono;
        this.correo = correo;
        this.direccion = direccion;
        this.contactoAsesor = contactoAsesor != null ? contactoAsesor : "-";
        this.condicionPago = condicionPago != null ? condicionPago : "Contado Factura";
        this.estado = estado != null ? estado : "Homologado / Activo";
    }

    public String getRuc() { return ruc; }
    public void setRuc(String ruc) { this.ruc = ruc; }

    public String getRazonSocial() { return razonSocial; }
    public void setRazonSocial(String razonSocial) { this.razonSocial = razonSocial; }

    public String getNombreComercial() { return nombreComercial; }
    public void setNombreComercial(String nombreComercial) { this.nombreComercial = nombreComercial; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getContactoAsesor() { return contactoAsesor; }
    public void setContactoAsesor(String contactoAsesor) { this.contactoAsesor = contactoAsesor; }

    public String getCondicionPago() { return condicionPago; }
    public void setCondicionPago(String condicionPago) { this.condicionPago = condicionPago; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
