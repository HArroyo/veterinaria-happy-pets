package happypets.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Modelo para la definición de roles y matriz de permisos en el sistema (Módulo 10).
 */
public class RolPermiso implements Serializable {
    private static final long serialVersionUID = 1L;

    private String idRol;
    private String nombreRol;
    private String descripcion;
    private String nivelAcceso; // "Total", "Médico", "Operativo", "Financiero", "Consulta"
    private boolean activo;
    private Map<String, Boolean> permisosModulos; // Clave: Módulo, Valor: Permiso habilitado

    public RolPermiso() {
        this.permisosModulos = new HashMap<>();
    }

    public RolPermiso(String idRol, String nombreRol, String descripcion, String nivelAcceso, boolean activo) {
        this.idRol = idRol;
        this.nombreRol = nombreRol;
        this.descripcion = descripcion;
        this.nivelAcceso = nivelAcceso;
        this.activo = activo;
        this.permisosModulos = new HashMap<>();
    }

    public String getIdRol() {
        return idRol;
    }

    public void setIdRol(String idRol) {
        this.idRol = idRol;
    }

    public String getNombreRol() {
        return nombreRol;
    }

    public void setNombreRol(String nombreRol) {
        this.nombreRol = nombreRol;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getNivelAcceso() {
        return nivelAcceso;
    }

    public void setNivelAcceso(String nivelAcceso) {
        this.nivelAcceso = nivelAcceso;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public Map<String, Boolean> getPermisosModulos() {
        return permisosModulos;
    }

    public void setPermisosModulos(Map<String, Boolean> permisosModulos) {
        this.permisosModulos = permisosModulos;
    }

    public void asignarPermiso(String modulo, boolean concedido) {
        this.permisosModulos.put(modulo, concedido);
    }

    public boolean tienePermiso(String modulo) {
        return this.permisosModulos.getOrDefault(modulo, false);
    }
}
