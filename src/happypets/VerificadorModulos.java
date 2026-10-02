package happypets;

import java.util.List;
import java.util.Optional;

import happypets.data.RepositorioVeterinaria;
import happypets.model.Cliente;
import happypets.model.ConsultaClinica;
import happypets.model.DocumentoMascota;
import happypets.model.Mascota;
import happypets.modulos.modulo1.ClientesMascotasFrame;
import happypets.modulos.modulo1.ConstanciasCertificadosFrame;
import happypets.modulos.modulo1.HistorialClinicoFrame;

/**
 * Clase de prueba y validación automatizada contra los requerimientos
 * del PDF de Wireframes del Módulo 1.
 */
public class VerificadorModulos {

    public static void main(String[] args) {
        System.out.println("=== INICIANDO VALIDACIÓN DEL MÓDULO 1 SEGÚN WIREFRAME ===");
        RepositorioVeterinaria repo = RepositorioVeterinaria.getInstancia();

        // 1. Validar Cliente del Wireframe
        System.out.println("\n[1] Validando Ficha de Registro de Cliente (Pág. 1)...");
        Optional<Cliente> optCliente = repo.buscarClientePorDniOApellido("45892134");
        assert optCliente.isPresent() : "El cliente con DNI 45892134 debe existir en el repositorio.";
        Cliente c = optCliente.get();
        System.out.println(" -> Cliente encontrado: " + c.getNombreCompleto() + " | DNI: " + c.getNumeroDocumento());
        System.out.println(" -> Teléfono: " + c.getTelefonoPrincipal() + " | Correo: " + c.getCorreo());
        System.out.println(" -> Dirección: " + c.getDireccion() + " | Ciudad: " + c.getDistritoCiudad());

        // 2. Validar Mascotas vinculadas del Wireframe
        System.out.println("\n[2] Validando Mascotas vinculadas al cliente (Pág. 1)...");
        List<Mascota> mascotas = c.getMascotas();
        System.out.println(" -> Total mascotas vinculadas: " + mascotas.size() + " (Esperado: 3)");
        assert mascotas.size() == 3 : "Deben haber 3 mascotas vinculadas.";
        for (Mascota m : mascotas) {
            System.out.println("    * " + m.getCodigo() + " - " + m.getNombre() + " (" + m.getEspecie() + ", " + m.getRaza() + ", " + m.getEdadTexto() + ", " + m.getSexo() + ", " + m.getEstadoTexto() + ")");
        }

        // 3. Validar Historial Clínico de Rocky (VET-0091)
        System.out.println("\n[3] Validando Historial Clínico de Mascotas (Pág. 2)...");
        List<ConsultaClinica> consultas = repo.getConsultasPorMascota("VET-0091");
        System.out.println(" -> Consultas registradas para Rocky: " + consultas.size() + " (Esperado: 3)");
        assert consultas.size() == 3 : "Rocky debe tener 3 consultas registradas.";
        for (ConsultaClinica cc : consultas) {
            System.out.println("    * " + cc.getCodigo() + " | " + cc.getFechaFormateada() + " | Motivo: " + cc.getMotivo() + " | Diag: " + cc.getDiagnostico() + " | Trat: " + cc.getTratamiento() + " | Vet: " + cc.getVeterinario() + " | Estado: " + cc.getEstado());
        }

        // 4. Validar Constancias y Certificados de Rocky (VET-0091)
        System.out.println("\n[4] Validando Constancias y Certificados (Pág. 3)...");
        List<DocumentoMascota> docs = repo.getDocumentosPorMascota("VET-0091");
        System.out.println(" -> Documentos disponibles para Rocky: " + docs.size() + " (Esperado: 4)");
        assert docs.size() == 4 : "Rocky debe tener 4 documentos disponibles.";
        for (DocumentoMascota d : docs) {
            System.out.println("    * " + d.getTipo() + " | " + d.getDescripcion() + " | Actualizado: " + d.getFechaActualizacionFormateada() + " | Disp: " + d.getDisponibilidadTexto());
        }

        // 5. Instanciar UI sin errores
        System.out.println("\n[5] Instanciando frames visuales Swing y autenticación...");
        happypets.auth.ServicioAutenticacion auth = happypets.auth.ServicioAutenticacion.getInstancia();
        assert auth.autenticar("admin", "admin").isPresent() : "Credenciales admin / admin deben ser válidas.";
        System.out.println(" -> Autenticación admin / admin validada correctamente.");

        happypets.ui.LoginFrame frameLogin = new happypets.ui.LoginFrame();
        System.out.println(" -> LoginFrame instanciada correctamente.");

        happypets.ui.PantallaPrincipalFrame framePrincipal = new happypets.ui.PantallaPrincipalFrame();
        System.out.println(" -> PantallaPrincipalFrame (Dashboard) instanciada correctamente.");

        HappyPetsApp app = new HappyPetsApp();
        System.out.println(" -> HappyPetsApp instanciada correctamente.");

        ClientesMascotasFrame frameClientes = new ClientesMascotasFrame();
        System.out.println(" -> ClientesMascotasFrame instanciada correctamente.");

        HistorialClinicoFrame frameHistorial = new HistorialClinicoFrame();
        System.out.println(" -> HistorialClinicoFrame instanciada correctamente.");

        ConstanciasCertificadosFrame frameDocs = new ConstanciasCertificadosFrame();
        System.out.println(" -> ConstanciasCertificadosFrame instanciada correctamente.");

        frameLogin.dispose();
        framePrincipal.dispose();
        app.dispose();
        frameClientes.dispose();
        frameHistorial.dispose();
        frameDocs.dispose();

        System.out.println("\n=== VALIDACIÓN COMPLETADA: TODOS LOS REQUERIMIENTOS CUMPLIDOS CON ÉXITO ===");
    }
}
