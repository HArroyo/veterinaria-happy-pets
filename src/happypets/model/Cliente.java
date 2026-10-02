package happypets.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Representa al cliente / propietario responsable de una o más mascotas.
 */
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
    private String distritoCiudad;
    private String notasContacto;
    private final List<Mascota> mascotas = new ArrayList<>();

    public Cliente() {
    }

    public Cliente(String codigo, String nombres, String apellidos, String tipoDocumento,
                   String numeroDocumento, String telefonoPrincipal, String telefonoSecundario,
                   String correo, String direccion, String distritoCiudad, String notasContacto) {
        this.codigo = codigo;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.tipoDocumento = tipoDocumento;
        this.numeroDocumento = numeroDocumento;
        this.telefonoPrincipal = telefonoPrincipal;
        this.telefonoSecundario = telefonoSecundario;
        this.correo = correo;
        this.direccion = direccion;
        this.distritoCiudad = distritoCiudad;
        this.notasContacto = notasContacto;
    }

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

    public String getDistritoCiudad() { return distritoCiudad; }
    public void setDistritoCiudad(String distritoCiudad) { this.distritoCiudad = distritoCiudad; }

    public String getNotasContacto() { return notasContacto; }
    public void setNotasContacto(String notasContacto) { this.notasContacto = notasContacto; }

    public String getNombreCompleto() {
        String n = nombres != null ? nombres.trim() : "";
        String a = apellidos != null ? apellidos.trim() : "";
        if (n.isEmpty()) return a;
        if (a.isEmpty()) return n;
        return n + " " + a;
    }

    public List<Mascota> getMascotas() {
        return Collections.unmodifiableList(mascotas);
    }

    public void agregarMascota(Mascota mascota) {
        if (mascota == null) throw new IllegalArgumentException("La mascota no puede ser nula");
        mascota.setCodigoCliente(codigo);
        mascotas.add(mascota);
    }

    public void eliminarMascota(String codigoMascota) {
        mascotas.removeIf(m -> m.getCodigo().equalsIgnoreCase(codigoMascota));
    }
}
