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

        // 8. Validar Entidades del Módulo 5 (Inventario y Farmacia)
        System.out.println("\n[8] Validando Entidades del Módulo 5 (Inventario y Farmacia)...");
        List<happypets.model.ProductoFarmacia> prods = repo.getProductosFarmacia();
        System.out.println(" -> Productos farmacéuticos registrados: " + prods.size() + " (Esperado >= 5)");
        assert prods.size() >= 5 : "Deben existir productos farmacéuticos en catálogo.";

        List<happypets.model.LoteMovimientoStock> movs = repo.getMovimientosStock();
        System.out.println(" -> Movimientos de stock en Kardex: " + movs.size() + " (Esperado >= 3)");
        assert movs.size() >= 3 : "Deben existir movimientos de Kardex.";

        List<happypets.model.ProveedorFarmacia> provs = repo.getProveedoresFarmacia();
        System.out.println(" -> Proveedores y laboratorios homologados: " + provs.size() + " (Esperado >= 3)");
        assert provs.size() >= 3 : "Deben existir proveedores registrados.";

        List<happypets.model.OrdenCompra> ordenes = repo.getOrdenesCompra();
        System.out.println(" -> Órdenes de compra registradas: " + ordenes.size() + " (Esperado >= 3)");
        assert ordenes.size() >= 3 : "Deben existir órdenes de compra.";

        List<happypets.model.AjusteMerma> mermas = repo.getAjustesMermas();
        System.out.println(" -> Ajustes y mermas registradas: " + mermas.size() + " (Esperado >= 3)");
        assert mermas.size() >= 3 : "Deben existir ajustes y mermas.";

        // 9. Validar Entidades del Módulo 6 (Finanzas y Ventas)
        System.out.println("\n[9] Validando Entidades del Módulo 6 (Finanzas y Ventas)...");
        List<happypets.model.VentaPOS> ventasPOS = repo.getVentasPOS();
        System.out.println(" -> Ventas POS emitidas: " + ventasPOS.size() + " (Esperado >= 3)");
        assert ventasPOS.size() >= 3 : "Deben existir ventas POS.";

        List<happypets.model.CuentaPorCobrar> cxc = repo.getCuentasPorCobrar();
        System.out.println(" -> Cuentas por Cobrar: " + cxc.size() + " (Esperado >= 3)");
        assert cxc.size() >= 3 : "Deben existir cuentas por cobrar.";

        List<happypets.model.CuentaPorPagar> cxp = repo.getCuentasPorPagar();
        System.out.println(" -> Cuentas por Pagar: " + cxp.size() + " (Esperado >= 3)");
        assert cxp.size() >= 3 : "Deben existir cuentas por pagar.";

        List<happypets.model.MovimientoCajaChica> cch = repo.getMovimientosCajaChica();
        System.out.println(" -> Movimientos Caja Chica: " + cch.size() + " (Esperado >= 5)");
        assert cch.size() >= 5 : "Deben existir movimientos de caja chica.";

        List<happypets.model.EgresoOperativo> egr = repo.getEgresosOperativos();
        System.out.println(" -> Egresos Operativos presupuestados: " + egr.size() + " (Esperado >= 5)");
        assert egr.size() >= 5 : "Deben existir egresos operativos.";

        // 10. Validar Entidades del Módulo 7 (Personal y Recursos Humanos)
        System.out.println("\n[10] Validando Entidades del Módulo 7 (Personal y RRHH)...");
        List<happypets.model.Veterinario> vets = repo.getVeterinarios();
        System.out.println(" -> Veterinarios especialistas colegiados: " + vets.size() + " (Esperado >= 8)");
        assert vets.size() >= 8 : "Deben existir especialistas veterinarios colegiados.";
        for (happypets.model.Veterinario v : vets) {
            System.out.println("    * " + v.getId() + " | " + v.getNombreCompleto() + " (" + v.getColegiaturaCMPV() + ") · " + v.getEspecialidad() + " · " + v.getEstadoDisponibilidad());
        }

        List<happypets.model.PersonalApoyo> apoyo = repo.getPersonalApoyo();
        System.out.println(" -> Personal de apoyo registrado: " + apoyo.size() + " (Esperado >= 10)");
        assert apoyo.size() >= 10 : "Debe existir personal de apoyo.";
        for (happypets.model.PersonalApoyo pa : apoyo) {
            System.out.println("    * " + pa.getId() + " | " + pa.getNombreCompleto() + " · " + pa.getCargo() + " (" + pa.getAreaAsignada() + ") · " + pa.getEstado());
        }

        List<happypets.model.TurnoSemanal> cuadrante = repo.getCuadranteTurnos();
        System.out.println(" -> Roles semanales en cuadrante de turnos: " + cuadrante.size() + " (Esperado >= 8)");
        assert cuadrante.size() >= 8 : "Deben existir turnos médicos en el cuadrante.";

        List<happypets.model.RegistroAsistencia> asistencias = repo.getAsistencias();
        System.out.println(" -> Registros de pase de lista diario: " + asistencias.size() + " (Esperado = 24)");
        assert asistencias.size() == 24 : "Deben existir 24 colaboradores en pase de lista.";
        System.out.println("    - Presentes hoy: " + repo.contarPresentesHoy());
        System.out.println("    - Retardos (+10 min tolerancia): " + repo.contarRetardosHoy());
        System.out.println("    - Ausentes: " + repo.contarAusentesHoy());
        System.out.println("    - Permisos hoy: " + repo.contarPermisosHoy());

        List<happypets.model.SolicitudPermiso> permisos = repo.getSolicitudesPermisos();
        System.out.println(" -> Solicitudes de permisos y licencias: " + permisos.size() + " (Esperado >= 3)");
        assert permisos.size() >= 3 : "Deben existir solicitudes de permisos.";

        // 11. Validar Entidades del Módulo 8 (Inteligencia de Negocios y Reportes)
        System.out.println("\n[11] Validando Entidades del Módulo 8 (Inteligencia y Reportes)...");
        List<happypets.model.MetricaMensualIngreso> metricas = repo.getMetricasMensuales();
        System.out.println(" -> Métricas mensuales registradas: " + metricas.size() + " (Esperado: 12 meses)");
        assert metricas.size() >= 12 : "Deben existir al menos 12 meses de métricas históricas.";

        List<happypets.model.ReporteClinicoDetalle> reportes = repo.getReportesClinicos();
        System.out.println(" -> Casos clínicos detallados: " + reportes.size() + " (Esperado >= 8)");
        assert reportes.size() >= 8 : "Deben existir reportes clínicos detallados.";

        List<happypets.model.DesgloseFinancieroPrestacion> desgloses = repo.getDesglosesFinancieros();
        System.out.println(" -> Desgloses financieros departamentales: " + desgloses.size() + " (Esperado >= 6)");
        assert desgloses.size() >= 6 : "Deben existir desgloses departamentales.";

        List<happypets.model.HistorialExportacion> historial = repo.getHistorialExportaciones();
        System.out.println(" -> Exportaciones en historial previo: " + historial.size() + " (Esperado >= 5)");
        assert historial.size() >= 5 : "Deben existir exportaciones en el historial.";

        happypets.model.HistorialExportacion expNueva = repo.generarExportacion("Pacientes y Fichas Clínicas", "XLSX", "Sede Central", List.of("Chip", "Tutor"));
        assert expNueva != null && expNueva.getIdExportacion().startsWith("EXP-") : "La exportación generada debe ser válida.";
        System.out.println(" -> Exportación interactiva generada con éxito: " + expNueva.getIdExportacion() + " (" + expNueva.getFormato() + ", " + expNueva.getTamanoLegible() + ")");

        // 12. Validar Entidades del Módulo 9 (Notificaciones, Documentos y Auditoría)
        System.out.println("\n[12] Validando Entidades del Módulo 9 (Notificaciones, Documentos y Auditoría)...");
        List<happypets.model.NotificacionSistema> notifs = repo.getNotificaciones();
        System.out.println(" -> Notificaciones registradas: " + notifs.size() + " (Esperado >= 6)");
        assert notifs.size() >= 6 : "Deben existir notificaciones del sistema.";
        for (happypets.model.NotificacionSistema n : notifs) {
            System.out.println("    * " + n.getId() + " | [" + n.getCategoria() + "] " + n.getTitulo() + " · " + (n.isLeida() ? "LEÍDA" : "NO LEÍDA"));
        }
        
        int totalAlertas = repo.contarNotificacionesPorCategoria("Alerta");
        System.out.println(" -> Notificaciones de tipo Alerta: " + totalAlertas + " (Esperado >= 2)");
        assert totalAlertas >= 2 : "Deben existir al menos 2 alertas registradas.";
        repo.marcarNotificacionComoLeida("NOTIF-001");
        assert repo.getNotificaciones().stream().filter(n -> n.getId().equals("NOTIF-001")).findFirst().get().isLeida() : "NOTIF-001 debe estar leída.";

        List<happypets.model.ConfiguracionCanalNotificacion> canales = repo.getCanalesNotificacion();
        System.out.println(" -> Canales de notificación configurados: " + canales.size() + " (Email, SMS, Push)");
        assert canales.size() == 3 : "Deben existir 3 canales configurados (Email, SMS, Push).";

        List<happypets.model.DocumentoRepositorio> docsRepo = repo.getDocumentosRepositorio();
        System.out.println(" -> Documentos en repositorio digital: " + docsRepo.size() + " (Esperado >= 8)");
        assert docsRepo.size() >= 8 : "Deben existir documentos en el repositorio digital.";

        happypets.model.DocumentoRepositorio nuevoDoc = new happypets.model.DocumentoRepositorio(
                "DOC-TEST-99", "PDF", "Protocolo Emergencias Felinas.pdf", "Clínico", 1.25,
                java.time.LocalDateTime.now(), "Dr. Gabriel Arana", "docs/protocolo_felinos.pdf");
        repo.agregarDocumentoRepositorio(nuevoDoc);
        assert repo.buscarDocumentosRepositorio("Emergencias", "Todos").size() >= 1 : "Debe encontrarse el nuevo documento.";

        List<happypets.model.LogAuditoria> logs = repo.getLogsAuditoria();
        System.out.println(" -> Logs de auditoría registrados: " + logs.size() + " (Esperado >= 8)");
        assert logs.size() >= 8 : "Deben existir eventos de auditoría inmutables.";
        for (happypets.model.LogAuditoria l : logs) {
            System.out.println("    * " + l.getIdEvento() + " | " + l.getFechaHoraFormateada() + " | " + l.getUsuario() + " | " + l.getTipoEvento() + " | " + l.getEstado());
        }

        repo.registrarLogAuditoria(new happypets.model.LogAuditoria(
                "#EV-9999", "Prueba Verificador", "Admin",
                java.time.LocalDateTime.now(), "127.0.0.1", "ÉXITO", "Test automático de auditoría"
        ));
        assert repo.filtrarLogsAuditoria(null, null, "Todos", "Admin").size() >= 1 : "El log registrado debe ser recuperado.";

        // 13. Validar Entidades del Módulo 10 (Configuración, Integraciones y Soporte)
        System.out.println("\n[13] Validando Entidades del Módulo 10 (Configuración, Integraciones y Soporte)...");
        happypets.model.ConfiguracionClinica cfg = repo.getConfiguracionClinica();
        assert cfg != null : "La configuración institucional debe existir.";
        System.out.println(" -> Configuración de la Clínica: " + cfg.getNombreComercial() + " (" + cfg.getIdentificadorFiscal() + ")");
        assert cfg.getIdentificadorFiscal().contains("900.842.115-4") : "El identificador fiscal debe coincidir con el wireframe.";

        List<happypets.model.Usuario> users = repo.getUsuariosSistema();
        System.out.println(" -> Colaboradores en nómina del sistema: " + users.size() + " (Esperado: 14 según wireframe)");
        assert users.size() == 14 : "Deben existir exactamente 14 usuarios como en el wireframe USUARIO.pdf.";
        for (happypets.model.Usuario u : users) {
            System.out.println("    * " + u.getUsername() + " | " + u.getNombreCompleto() + " (" + u.getRol() + ") · " + u.getEspecialidadArea() + " · " + u.getEstado());
        }

        List<happypets.model.RolPermiso> roles = repo.getRolesPermisos();
        System.out.println(" -> Roles con matriz de permisos: " + roles.size() + " (Esperado >= 5)");
        assert roles.size() >= 5 : "Deben existir roles con matriz de privilegios.";

        List<happypets.model.IntegracionExterna> integraciones = repo.getIntegracionesExternas();
        System.out.println(" -> Integraciones externas configuradas: " + integraciones.size() + " (Esperado = 5)");
        assert integraciones.size() == 5 : "Deben existir 5 integraciones externas.";
        long act = integraciones.stream().filter(happypets.model.IntegracionExterna::isActiva).count();
        System.out.println("    - Activas por defecto: " + act + " de " + integraciones.size() + " (Esperado: 3 de 5 según wireframe)");
        assert act >= 3 : "Al menos 3 integraciones deben estar activas por defecto.";

        // Test de conmutación de integración
        repo.conmutarEstadoIntegracion("INT-04");
        assert integraciones.stream().filter(i -> i.getId().equals("INT-04")).findFirst().get().isActiva() : "INT-04 debe activarse.";
        repo.conmutarEstadoIntegracion("INT-04"); // Restaurar a inactiva

        happypets.model.ConfiguracionModuloIA ia = repo.getConfiguracionModuloIA();
        assert ia != null && ia.isHabilitado() : "El módulo de IA debe estar habilitado.";
        System.out.println(" -> Módulo de IA: " + ia.getModeloActual() + " (Pre-triaje: " + ia.isPreTriajeMascotas() + ", Diagnóstico: " + ia.isAsistenteDiagnosticoPreliminar() + ")");

        String diagIA = repo.simularDiagnosticoTriajeIA("Canino", "Golden Retriever", 48, "Tos seca y fatiga", "tos continua post ejercicio");
        assert diagIA != null && diagIA.contains("ANALIZADOR CLÍNICO INTELIGENTE") : "La respuesta de la IA debe ser válida.";
        System.out.println(" -> Simulación de Triaje con IA ejecutada satisfactoriamente.");

        happypets.model.DiagnosticoSistema salud = repo.getDiagnosticoSistema();
        assert salud != null && salud.getEstadoBaseDatos().contains("En línea") : "El estado de base de datos debe estar en línea.";
        System.out.println(" -> Diagnóstico del servidor: " + salud.getEstadoBaseDatos() + " | Respaldo: " + salud.getUltimoRespaldoCloud());

        List<happypets.model.TicketSoporte> tickets = repo.getTicketsSoporte();
        System.out.println(" -> Tickets de soporte registrados: " + tickets.size() + " (Esperado >= 2)");
        assert tickets.size() >= 2 : "Deben existir tickets de soporte.";

        // 14. Instanciar UI sin errores
        System.out.println("\n[14] Instanciando frames visuales Swing y autenticación...");
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

        happypets.modulos.modulo5.Modulo5InventarioFarmaciaFrame frameMod5 = new happypets.modulos.modulo5.Modulo5InventarioFarmaciaFrame();
        System.out.println(" -> Modulo5InventarioFarmaciaFrame instanciada correctamente.");

        happypets.modulos.modulo6.Modulo6FinanzasVentasFrame frameMod6 = new happypets.modulos.modulo6.Modulo6FinanzasVentasFrame();
        System.out.println(" -> Modulo6FinanzasVentasFrame instanciada correctamente.");

        happypets.modulos.modulo7.Modulo7PersonalRRHHFrame frameMod7 = new happypets.modulos.modulo7.Modulo7PersonalRRHHFrame();
        System.out.println(" -> Modulo7PersonalRRHHFrame instanciada correctamente.");

        happypets.modulos.modulo8.Modulo8ReportesBIFrame frameMod8 = new happypets.modulos.modulo8.Modulo8ReportesBIFrame();
        System.out.println(" -> Modulo8ReportesBIFrame instanciada correctamente con sus 4 submódulos.");

        happypets.modulos.modulo9.Modulo9NotificacionesAuditoriaFrame frameMod9 = new happypets.modulos.modulo9.Modulo9NotificacionesAuditoriaFrame();
        System.out.println(" -> Modulo9NotificacionesAuditoriaFrame instanciada correctamente con sus 4 submódulos.");

        happypets.modulos.modulo10.Modulo10ConfiguracionSoporteFrame frameMod10 = new happypets.modulos.modulo10.Modulo10ConfiguracionSoporteFrame();
        System.out.println(" -> Modulo10ConfiguracionSoporteFrame instanciada correctamente con su Dashboard y sus 4 submódulos.");

        frameLogin.dispose();
        framePrincipal.dispose();
        app.dispose();
        frameClientes.dispose();
        frameHistorial.dispose();
        frameDocs.dispose();
        frameMod2.dispose();
        frameMod3.dispose();
        frameMod4.dispose();
        frameMod5.dispose();
        frameMod6.dispose();
        frameMod7.dispose();
        frameMod8.dispose();
        frameMod9.dispose();
        frameMod10.dispose();

        System.out.println("\n=== VALIDACIÓN COMPLETADA: TODOS LOS REQUERIMIENTOS CUMPLIDOS CON ÉXITO ===");
    }
}
