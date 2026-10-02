package happypets.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Responsable de una o varias mascotas. */
public class Cliente {
    private String codigo;
    private String nombres;
    private String apellidos;
    private String tipoDocumento;
    private String numeroDocumento;
    private String telefonoPrincipal;
    private String telefonoSecundario;
    private String correo;
    private String direccion;
    private String distrito;
    private String ciudad;
    private String notasContacto;
    private final List<Mascota> mascotas = new ArrayList<>();

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    
    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }
    
    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }
    
    public String getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(String tipoDocumento) { this.tipoDocumento = tipoDocumento; }
    
    public String getNumeroDocumento() { return numeroDocumento; }
    public void setNumeroDocumento(String numeroDocumento) { this.numeroDocumento = numeroDocumento; }
    
    public String getTelefonoPrincipal() { return telefonoPrincipal; }
    public void setTelefonoPrincipal(String telefonoPrincipal) { this.telefonoPrincipal = telefonoPrincipal; }
    
    public String getTelefonoSecundario() { return telefonoSecundario; }
    public void setTelefonoSecundario(String telefonoSecundario) { this.telefonoSecundario = telefonoSecundario; }
    
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    
    public String getDistrito() { return distrito; }
    public void setDistrito(String distrito) { this.distrito = distrito; }
    
    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }
    
    public String getNotasContacto() { return notasContacto; }
    public void setNotasContacto(String notasContacto) { this.notasContacto = notasContacto; }

    public String getNombreCompleto() {
        return (nombres == null ? "" : nombres) + " " + (apellidos == null ? "" : apellidos);
    }

    public List<Mascota> getMascotas() { return Collections.unmodifiableList(mascotas); }

    public void agregarMascota(Mascota mascota) {
        if (mascota == null) throw new IllegalArgumentException("La mascota es obligatoria");
        mascota.setCodigoCliente(codigo);
        mascotas.add(mascota);
    }
}
