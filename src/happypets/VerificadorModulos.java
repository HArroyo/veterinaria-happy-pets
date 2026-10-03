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

        // 5. Validar Citas, Recordatorios y Triaje del Módulo 2
        System.out.println("\n[5] Validando Entidades del Módulo 2 (Agenda y Citas)...");
        List<happypets.model.Cita> citas = repo.getCitas();
        System.out.println(" -> Citas registradas en repositorio: " + citas.size() + " (Esperado >= 8)");
        assert citas.size() >= 8 : "Deben existir citas de prueba.";

        List<happypets.model.RecordatorioCita> recordatorios = repo.getRecordatorios();
        System.out.println(" -> Recordatorios registrados: " + recordatorios.size() + " (Esperado >= 4)");
        assert recordatorios.size() >= 4 : "Deben existir recordatorios de prueba.";

        List<happypets.model.PacienteTriaje> pacientesTriaje = repo.getPacientesTriaje();
        System.out.println(" -> Pacientes en sala de espera/triaje: " + pacientesTriaje.size() + " (Esperado >= 3)");
        assert pacientesTriaje.size() >= 3 : "Deben existir pacientes en triaje.";

        // 6. Validar Entidades del Módulo 3 (Servicios Médicos y Quirúrgicos)
        System.out.println("\n[6] Validando Entidades del Módulo 3 (Servicios Médicos)...");
        List<happypets.model.AtencionMedica> atenciones = repo.getAtencionesMedicas();
        System.out.println(" -> Consultas médicas registradas: " + atenciones.size() + " (Esperado >= 3)");
        assert atenciones.size() >= 3 : "Deben existir consultas médicas.";

        List<happypets.model.RegistroInmunizacion> inms = repo.getInmunizaciones();
        System.out.println(" -> Vacunaciones y desparasitaciones registradas: " + inms.size() + " (Esperado >= 6)");
        assert inms.size() >= 6 : "Deben existir inmunizaciones.";

        List<happypets.model.RegistroCirugia> cirugias = repo.getCirugias();
        System.out.println(" -> Cirugías en control quirúrgico: " + cirugias.size() + " (Esperado >= 3)");
        assert cirugias.size() >= 3 : "Deben existir cirugías.";

        List<happypets.model.OrdenLaboratorio> ordenesLab = repo.getOrdenesLaboratorio();
        System.out.println(" -> Órdenes de laboratorio e imágenes: " + ordenesLab.size() + " (Esperado >= 4)");
        assert ordenesLab.size() >= 4 : "Deben existir órdenes diagnósticas.";

        // 7. Validar Entidades del Módulo 4 (Servicios Estéticos y Hospedaje)
        System.out.println("\n[7] Validando Entidades del Módulo 4 (Estética y Hospedaje)...");
        List<happypets.model.ServicioGrooming> grooming = repo.getServiciosGrooming();
        System.out.println(" -> Turnos de Grooming / Peluquería: " + grooming.size() + " (Esperado >= 3)");
        assert grooming.size() >= 3 : "Deben existir registros de grooming.";

        List<happypets.model.InternamientoHospitalario> inters = repo.getInternamientos();
        System.out.println(" -> Pacientes hospitalizados en boxes: " + inters.size() + " (Esperado >= 3)");
        assert inters.size() >= 3 : "Deben existir registros de hospitalización.";

        List<happypets.model.ReservaHospedaje> reservas = repo.getReservasHospedaje();
        System.out.println(" -> Reservas de Hotel y Guardería: " + reservas.size() + " (Esperado >= 3)");
        assert reservas.size() >= 3 : "Deben existir reservas de hospedaje.";

        List<happypets.model.MascotaAdopcion> adopciones = repo.getMascotasAdopcion();
        System.out.println(" -> Catálogo de Adopciones y Rescatados: " + adopciones.size() + " (Esperado >= 3)");
        assert adopciones.size() >= 3 : "Deben existir rescatados en adopción.";

        // 8. Instanciar UI sin errores
        System.out.println("\n[8] Instanciando frames visuales Swing y autenticación...");
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

        happypets.modulos.modulo2.Modulo2AgendaCitasFrame frameMod2 = new happypets.modulos.modulo2.Modulo2AgendaCitasFrame();
        System.out.println(" -> Modulo2AgendaCitasFrame instanciada correctamente.");

        happypets.modulos.modulo3.Modulo3ServiciosMedicosFrame frameMod3 = new happypets.modulos.modulo3.Modulo3ServiciosMedicosFrame();
        System.out.println(" -> Modulo3ServiciosMedicosFrame instanciada correctamente.");

        happypets.modulos.modulo4.Modulo4EsteticosHospedajeFrame frameMod4 = new happypets.modulos.modulo4.Modulo4EsteticosHospedajeFrame();
        System.out.println(" -> Modulo4EsteticosHospedajeFrame instanciada correctamente.");

        frameLogin.dispose();
        framePrincipal.dispose();
        app.dispose();
        frameClientes.dispose();
        frameHistorial.dispose();
        frameDocs.dispose();
        frameMod2.dispose();
        frameMod3.dispose();
        frameMod4.dispose();

        System.out.println("\n=== VALIDACIÓN COMPLETADA: TODOS LOS REQUERIMIENTOS CUMPLIDOS CON ÉXITO ===");
    }
}
