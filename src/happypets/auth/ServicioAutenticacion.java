package happypets.auth;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import happypets.model.Usuario;

/**
 * Servicio centralizado de autenticación para el sistema Happy Pets.
 * Por defecto provee las credenciales admin / admin solicitadas.
 */
public class ServicioAutenticacion {
    private static ServicioAutenticacion instancia;
    private final List<Usuario> usuarios = new ArrayList<>();
    private Usuario sesionActual;

    private ServicioAutenticacion() {
        // Usuario administrador por defecto
        usuarios.add(new Usuario("admin", "admin", "Dr. R. Mendoza", "Administrador", "dr.mendoza@happypets.com"));
        // Usuario veterinario secundario
        usuarios.add(new Usuario("veterinario", "vet123", "Dra. V. Morales", "Veterinario", "dra.morales@happypets.com"));
    }

    public static synchronized ServicioAutenticacion getInstancia() {
        if (instancia == null) {
            instancia = new ServicioAutenticacion();
        }
        return instancia;
    }

    public Optional<Usuario> autenticar(String username, String password) {
        if (username == null || password == null) return Optional.empty();
        String u = username.trim();
        for (Usuario user : usuarios) {
            if (user.getUsername().equalsIgnoreCase(u) && user.validarPassword(password)) {
                this.sesionActual = user;
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    public Usuario getSesionActual() {
        if (sesionActual == null) {
            sesionActual = usuarios.get(0); // Admin por defecto si no ha iniciado
        }
        return sesionActual;
    }

    public void cerrarSesion() {
        this.sesionActual = null;
    }
}
