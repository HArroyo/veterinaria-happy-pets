package happypets.auth;

import java.util.Optional;
import happypets.data.RepositorioVeterinaria;

import happypets.model.Usuario;

/**
 * Servicio centralizado de autenticación para el sistema Happy Pets.
 * Por defecto provee las credenciales admin / admin solicitadas.
 */
public class ServicioAutenticacion {
    private static ServicioAutenticacion instancia;
    private Usuario sesionActual;

    private ServicioAutenticacion() {
    }

    public static synchronized ServicioAutenticacion getInstancia() {
        if (instancia == null) {
            instancia = new ServicioAutenticacion();
        }
        return instancia;
    }

    public Optional<Usuario> autenticar(String username, String password) {
        sesionActual = null;
        if (username == null || password == null) return Optional.empty();
        String u = username.trim();
        final String solicitado = u;
        boolean existe = RepositorioVeterinaria.getInstancia().getUsuariosSistema().stream()
                .anyMatch(user -> user.getUsername().equalsIgnoreCase(solicitado));
        if (!existe && u.equalsIgnoreCase("admin")) u = "rmendoza";
        if (!existe && u.equalsIgnoreCase("veterinario")) u = "cmorales";
        for (Usuario user : RepositorioVeterinaria.getInstancia().getUsuariosSistema()) {
            if (user.isActivo() && user.getUsername().equalsIgnoreCase(u) && user.validarPassword(password)) {
                this.sesionActual = user;
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    public Usuario getSesionActual() {
        if (sesionActual != null) {
            sesionActual = RepositorioVeterinaria.getInstancia().getUsuariosSistema().stream()
                    .filter(u -> u.getUsername().equalsIgnoreCase(sesionActual.getUsername()) && u.isActivo())
                    .findFirst().orElse(null);
        }
        return sesionActual;
    }

    public void cerrarSesion() {
        this.sesionActual = null;
    }

    public static final String[] MODULOS = {"Pacientes e Historias", "Agenda y Citas", "Servicios Médicos",
            "Estética y Hospedaje", "Farmacia e Inventario", "Finanzas y Ventas", "Personal y RRHH",
            "Reportes y BI", "Notificaciones y Auditoría", "Configuración y Soporte"};

    public boolean puedeAccederModulo(int numero) {
        Usuario usuario = getSesionActual();
        if (usuario == null || numero < 1 || numero > MODULOS.length) return false;
        String rol = "Veterinario Especialista".equals(usuario.getRol()) ? "Veterinario Titular" : usuario.getRol();
        return RepositorioVeterinaria.getInstancia().getRolesPermisos().stream()
                .anyMatch(r -> r.isActivo() && r.getNombreRol().equalsIgnoreCase(rol) && r.tienePermiso(MODULOS[numero - 1]));
    }
}
