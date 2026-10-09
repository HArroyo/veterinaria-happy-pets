package happypets.model;

/**
 * Representa al usuario que inicia sesión en la plataforma veterinaria.
 */
public class Usuario {
    private String username;
    private String password;
    private String nombreCompleto;
    private String rol;
    private String correo;
    private String especialidadArea;
    private String estado; // "Activo", "Inactivo"

    public Usuario(String username, String password, String nombreCompleto, String rol, String correo) {
        this(username, password, nombreCompleto, rol, correo, "General", "Activo");
    }

    public Usuario(String username, String password, String nombreCompleto, String rol, String correo,
                   String especialidadArea, String estado) {
        this.username = username;
        this.password = password;
        this.nombreCompleto = nombreCompleto;
        this.rol = rol;
        this.correo = correo;
        this.especialidadArea = especialidadArea != null ? especialidadArea : "General";
        this.estado = estado != null ? estado : "Activo";
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getEspecialidadArea() { return especialidadArea; }
    public void setEspecialidadArea(String especialidadArea) { this.especialidadArea = especialidadArea; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public boolean isActivo() {
        return "Activo".equalsIgnoreCase(this.estado);
    }

    public boolean validarPassword(String pass) {
        return this.password != null && this.password.equals(pass);
    }
}

