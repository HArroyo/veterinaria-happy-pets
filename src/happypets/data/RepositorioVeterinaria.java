package happypets.data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import happypets.model.AtencionMedica;
import happypets.model.Certificado;
import happypets.model.Cita;
import happypets.model.Cliente;
import happypets.model.ConsultaClinica;
import happypets.model.DocumentoMascota;
import happypets.model.Mascota;
import happypets.model.OrdenLaboratorio;
import happypets.model.PacienteTriaje;
import happypets.model.RecordatorioCita;
import happypets.model.RegistroCirugia;
import happypets.model.RegistroInmunizacion;
import happypets.model.VentaPOS;
import happypets.model.ItemVentaPOS;
import happypets.model.CuentaPorCobrar;
import happypets.model.CuentaPorPagar;
import happypets.model.MovimientoCajaChica;
import happypets.model.EgresoOperativo;
import happypets.model.Veterinario;
import happypets.model.PersonalApoyo;
import happypets.model.TurnoSemanal;
import happypets.model.RegistroAsistencia;
import happypets.model.SolicitudPermiso;
import happypets.model.MetricaMensualIngreso;
import happypets.model.ReporteClinicoDetalle;
import happypets.model.DesgloseFinancieroPrestacion;
import happypets.model.HistorialExportacion;
import happypets.model.NotificacionSistema;
import happypets.model.ConfiguracionCanalNotificacion;
import happypets.model.PreferenciaNotificacionEventos;
import happypets.model.DocumentoRepositorio;
import happypets.model.LogAuditoria;
import happypets.model.Usuario;
import happypets.model.ConfiguracionClinica;
import happypets.model.RolPermiso;
import happypets.model.IntegracionExterna;
import happypets.model.ConfiguracionModuloIA;
import happypets.model.TicketSoporte;
import happypets.model.DiagnosticoSistema;

/**
 * Almacén en memoria centralizado para clientes, mascotas, consultas, documentos,
 * citas, recordatorios, triaje, atenciones médicas, inmunizaciones, cirugías y laboratorio.
 */
public class RepositorioVeterinaria {
    private static RepositorioVeterinaria instancia;

    private final List<Cliente> clientes = new ArrayList<>();
    private final List<ConsultaClinica> consultas = new ArrayList<>();
    private final List<DocumentoMascota> documentos = new ArrayList<>();
    private final List<Cita> citas = new ArrayList<>();
    private final List<RecordatorioCita> recordatorios = new ArrayList<>();
    private final List<PacienteTriaje> pacientesTriaje = new ArrayList<>();

    // Módulo 3: Servicios Médicos y Quirúrgicos
    private final List<AtencionMedica> atencionesMedicas = new ArrayList<>();
    private final List<RegistroInmunizacion> inmunizaciones = new ArrayList<>();
    private final List<RegistroCirugia> cirugias = new ArrayList<>();
    private final List<OrdenLaboratorio> ordenesLaboratorio = new ArrayList<>();

    // Módulo 4: Servicios Estéticos y Hospedaje
    private final List<happypets.model.ServicioGrooming> serviciosGrooming = new ArrayList<>();
    private final List<happypets.model.InternamientoHospitalario> internamientos = new ArrayList<>();
    private final List<happypets.model.ReservaHospedaje> reservasHospedaje = new ArrayList<>();
    private final List<happypets.model.MascotaAdopcion> mascotasAdopcion = new ArrayList<>();

    // Módulo 5: Inventario y Farmacia
    private final List<happypets.model.ProductoFarmacia> productosFarmacia = new ArrayList<>();
    private final List<happypets.model.LoteMovimientoStock> movimientosStock = new ArrayList<>();
    private final List<happypets.model.ProveedorFarmacia> proveedoresFarmacia = new ArrayList<>();
    private final List<happypets.model.OrdenCompra> ordenesCompra = new ArrayList<>();
    private final List<happypets.model.AjusteMerma> ajustesMermas = new ArrayList<>();

    // Módulo 6: Finanzas y Ventas
    private final List<VentaPOS> ventasPOS = new ArrayList<>();
    private final List<CuentaPorCobrar> cuentasPorCobrar = new ArrayList<>();
    private final List<CuentaPorPagar> cuentasPorPagar = new ArrayList<>();
    private final List<MovimientoCajaChica> movimientosCajaChica = new ArrayList<>();
    private final List<EgresoOperativo> egresosOperativos = new ArrayList<>();

    // Módulo 7: Personal y Recursos Humanos
    private final List<Veterinario> veterinarios = new ArrayList<>();
    private final List<PersonalApoyo> personalApoyo = new ArrayList<>();
    private final List<TurnoSemanal> cuadranteTurnos = new ArrayList<>();
    private final List<RegistroAsistencia> asistencias = new ArrayList<>();
    private final List<SolicitudPermiso> solicitudesPermisos = new ArrayList<>();

    // Módulo 8: Inteligencia de Negocios y Reportes
    private final List<MetricaMensualIngreso> metricasMensuales = new ArrayList<>();
    private final List<ReporteClinicoDetalle> reportesClinicos = new ArrayList<>();
    private final List<DesgloseFinancieroPrestacion> desglosesFinancieros = new ArrayList<>();
    private final List<HistorialExportacion> historialExportaciones = new ArrayList<>();

    // Módulo 9: Notificaciones, Documentos y Auditoría
    private final List<NotificacionSistema> notificaciones = new ArrayList<>();
    private final List<ConfiguracionCanalNotificacion> canalesNotificacion = new ArrayList<>();
    private PreferenciaNotificacionEventos preferenciasEventos = new PreferenciaNotificacionEventos();
    private final List<DocumentoRepositorio> documentosRepositorio = new ArrayList<>();
    private final List<LogAuditoria> logsAuditoria = new ArrayList<>();

    // Módulo 10: Configuración, Integraciones y Soporte
    private ConfiguracionClinica configuracionClinica = new ConfiguracionClinica();
    private final List<Usuario> usuariosSistema = new ArrayList<>();
    private final List<RolPermiso> rolesPermisos = new ArrayList<>();
    private final List<IntegracionExterna> integracionesExternas = new ArrayList<>();
    private ConfiguracionModuloIA configuracionModuloIA = new ConfiguracionModuloIA();
    private final List<TicketSoporte> ticketsSoporte = new ArrayList<>();
    private DiagnosticoSistema diagnosticoSistema = new DiagnosticoSistema();

    private RepositorioVeterinaria() {
        inicializarDatos();
    }

    public static synchronized RepositorioVeterinaria getInstancia() {
        if (instancia == null) {
            instancia = new RepositorioVeterinaria();
        }
        return instancia;
    }

    private void inicializarDatos() {
        // Cliente principal del Wireframe
        Cliente c1 = new Cliente(
                "CLI-001",
                "Carlos Eduardo",
                "Morales Soto",
                "DNI",
                "45892134",
                "+51 984 552 110",
                "+51 01 432 9980",
                "carlos.morales@gmail.com",
                "Av. San Borja Sur 482, Dpto 301 - Lima",
                "San Borja, Lima",
                "Preferencia de contacto en las tardes. Notificar recordatorios de desparasitación y vacunas por mensajería móvil."
        );

        // Mascotas del cliente según Wireframe
        Mascota m1 = new Mascota(
                "VET-0091", "CLI-001", "Rocky", "Canino", "Golden Retriever",
                "3 años 2 m.", "Macho", 28.4, "Al día", "Polen de gramíneas", true
        );
        Mascota m2 = new Mascota(
                "VET-0144", "CLI-001", "Luna", "Felino", "Siamés",
                "1 año 8 m.", "Hembra", 4.2, "Al día", "Ninguna conocida", true
        );
        Mascota m3 = new Mascota(
                "VET-0238", "CLI-001", "Toby", "Canino", "Pug",
                "5 años", "Macho", 8.5, "Al día", "Polen de gramíneas", true
        );

        c1.agregarMascota(m1);
        c1.agregarMascota(m2);
        c1.agregarMascota(m3);
        clientes.add(c1);

        // Segundo cliente para pruebas de búsqueda y listado
        Cliente c2 = new Cliente(
                "CLI-002",
                "Ana María",
                "Rojas Paredes",
                "DNI",
                "72451980",
                "+51 971 223 344",
                "",
                "ana.rojas@outlook.com",
                "Calle Las Camelias 230 - Miraflores",
                "Miraflores, Lima",
                "Llamar solo por las mañanas."
        );
        Mascota m4 = new Mascota(
                "VET-0305", "CLI-002", "Max", "Canino", "Schnauzer",
                "2 años", "Macho", 7.8, "Al día", "Alérgica al pollo", true
        );
        c2.agregarMascota(m4);
        clientes.add(c2);

        // Consultas clínicas de Rocky (VET-0091) según Wireframe pág. 2
        consultas.add(new ConsultaClinica(
                "HC-001", "VET-0091", LocalDate.of(2024, 6, 14),
                "Control rutinario", "Sin síntomas patológicos evidentes.",
                "Paciente estable", "Continuar plan vacunal", "Dr. R. Mendoza",
                28.4, 38.5, "Revisión general en orden. Próxima desparasitación en 3 meses.", "Cerrada"
        ));
        consultas.add(new ConsultaClinica(
                "HC-002", "VET-0091", LocalDate.of(2024, 5, 10),
                "Vacunación", "Control de inmunizaciones anual.",
                "Apto para vacunación", "Vacuna séxtuple", "Dr. R. Mendoza",
                28.2, 38.6, "Aplicación sin reacciones adversas registradas.", "Cerrada"
        ));
        consultas.add(new ConsultaClinica(
                "HC-003", "VET-0091", LocalDate.of(2024, 3, 12),
                "Control", "Prurito leve en extremidades por posible alergia estacional.",
                "Paciente estable", "Seguimiento", "Dr. R. Mendoza",
                27.9, 38.4, "Seguimiento de respuesta dérmica favorable.", "Cerrada"
        ));

        // Consultas de Max (VET-0305)
        consultas.add(new ConsultaClinica(
                "HC-004", "VET-0305", LocalDate.of(2024, 7, 20),
                "Consulta dermatológica", "Enrojecimiento cutáneo por cambio de pienso.",
                "Alergia alimentaria", "Dieta hipoalergénica y antihistamínico", "Dr. R. Mendoza",
                7.8, 38.7, "Control en 15 días para evaluar piel.", "Cerrada"
        ));

        // Documentos de Rocky (VET-0091) según Wireframe pág. 3
        documentos.add(new DocumentoMascota(
                "DOC-001", "VET-0091", "Tarjeta de vacunación",
                "Vacunas aplicadas y próximas dosis", LocalDate.of(2024, 5, 10), true
        ));
        documentos.add(new DocumentoMascota(
                "DOC-002", "VET-0091", "Historial de recetas médicas",
                "Recetas asociadas a consultas anteriores", LocalDate.of(2024, 6, 14), true
        ));
        documentos.add(new DocumentoMascota(
                "DOC-003", "VET-0091", "Historial clínico",
                "Resumen de consultas y tratamientos", LocalDate.of(2024, 6, 14), true
        ));
        documentos.add(new Certificado(
                "DOC-004", "VET-0091", "Certificado de vacunación",
                "Certificado vigente de vacunas", LocalDate.of(2024, 5, 10), true,
                "CERT-VAC-2024-0091", LocalDate.of(2024, 5, 10), "Viajes / Residencia", "Vigente"
        ));

        // Documentos de Luna (VET-0144)
        documentos.add(new DocumentoMascota(
                "DOC-005", "VET-0144", "Tarjeta de vacunación",
                "Vacunación felina triple y antirrábica", LocalDate.of(2024, 4, 18), true
        ));

        // ==========================================
        // MÓDULO 2: AGENDA Y CITAS (DATOS INICIALES)
        // ==========================================
        LocalDate hoy = LocalDate.now();

        // 1. Citas del día y próximas
        citas.add(new Cita(
                "CIT-0101", "VET-0091", "Rocky", "Canino · Golden Retriever",
                "45892134", "Carlos Eduardo Morales", "+51 984 552 110",
                hoy, LocalTime.of(9, 0), 30, "Dr. Roberto Mendoza",
                "Consulta Médica", "En Sala de Espera", "Control y desparasitación trimestral",
                "Normal", "Paciente puntual en sala", 75.00
        ));
        citas.add(new Cita(
                "CIT-0102", "VET-0144", "Luna", "Felino · Siamés",
                "45892134", "Carlos Eduardo Morales", "+51 984 552 110",
                hoy, LocalTime.of(10, 0), 30, "Dra. Laura Morales",
                "Vacunación", "Confirmada", "Vacuna triple felina anual y revisión",
                "Normal", "Confirmado por WhatsApp", 65.00
        ));
        citas.add(new Cita(
                "CIT-0103", "VET-0305", "Max", "Canino · Schnauzer",
                "72451980", "Ana María Rojas", "+51 971 223 344",
                hoy, LocalTime.of(11, 30), 60, "Dr. Roberto Mendoza",
                "Cirugía / Quirófano", "Programada", "Profilaxis dental y limpieza por ultrasonido",
                "Urgente", "Ayuno de 8 horas indicado", 180.00
        ));
        citas.add(new Cita(
                "CIT-0104", "VET-0238", "Toby", "Canino · Pug",
                "45892134", "Carlos Eduardo Morales", "+51 984 552 110",
                hoy, LocalTime.of(14, 0), 45, "Dra. Laura Morales",
                "Grooming / Peluquería", "Confirmada", "Baño medicado hipoalergénico y corte higiénico",
                "Normal", "Piel sensible a champús aromáticos", 50.00
        ));
        citas.add(new Cita(
                "CIT-0105", "VET-0412", "Thor", "Canino · Pastor Alemán",
                "41290887", "Jorge Valdivia Ramos", "+51 945 882 113",
                hoy, LocalTime.of(15, 30), 30, "Dr. Carlos Silva",
                "Consulta Médica", "Programada", "Cojera en pata trasera derecha tras salto",
                "Urgente", "Requiere evaluación traumatológica", 85.00
        ));
        citas.add(new Cita(
                "CIT-0106", "VET-0520", "Bella", "Felino · Angora",
                "10882341", "Patricia Benítez", "+51 993 441 022",
                hoy, LocalTime.of(16, 30), 20, "Dr. Roberto Mendoza",
                "Control y Seguimiento", "Confirmada", "Revisión de herida quirúrgica y retiro de puntos",
                "Normal", "Evolución favorable", 40.00
        ));
        citas.add(new Cita(
                "CIT-0107", "VET-0618", "Simón", "Canino · Beagle",
                "46610992", "Elena Castro Peña", "+51 912 345 678",
                hoy, LocalTime.of(17, 30), 40, "Dra. Laura Morales",
                "Consulta Médica", "Programada", "Vómito recurrente y deshidratación leve",
                "Emergencia", "Prioridad en triaje", 95.00
        ));
        citas.add(new Cita(
                "CIT-0108", "VET-0091", "Rocky", "Canino · Golden Retriever",
                "45892134", "Carlos Eduardo Morales", "+51 984 552 110",
                hoy.plusDays(1), LocalTime.of(10, 0), 30, "Dr. Roberto Mendoza",
                "Control y Seguimiento", "Programada", "Evaluación de dieta y peso corporal",
                "Normal", "Control rutinario", 45.00
        ));
        citas.add(new Cita(
                "CIT-0109", "VET-0305", "Max", "Canino · Schnauzer",
                "72451980", "Ana María Rojas", "+51 971 223 344",
                hoy.plusDays(2), LocalTime.of(16, 0), 30, "Dra. Laura Morales",
                "Vacunación", "Programada", "Refuerzo anual antirrábica",
                "Normal", "Traer carné de vacunación", 55.00
        ));

        // 2. Recordatorios automáticos vinculados a citas
        LocalDateTime ahora = LocalDateTime.now();
        recordatorios.add(new RecordatorioCita(
                "REC-001", "CIT-0101", "Rocky", "Carlos Eduardo Morales", "+51 984 552 110",
                "WhatsApp", ahora.minusHours(2),
                "Hola Carlos, te recordamos que Rocky tiene cita de Consulta Médica hoy a las 09:00 AM en Happy Pets con el Dr. Roberto Mendoza. Por favor confirma tu asistencia.",
                "Confirmado"
        ));
        recordatorios.add(new RecordatorioCita(
                "REC-002", "CIT-0102", "Luna", "Carlos Eduardo Morales", "+51 984 552 110",
                "WhatsApp", ahora.minusHours(1),
                "Hola Carlos, te recordamos que Luna tiene cita de Vacunación hoy a las 10:00 AM con la Dra. Laura Morales en Happy Pets.",
                "Confirmado"
        ));
        recordatorios.add(new RecordatorioCita(
                "REC-003", "CIT-0103", "Max", "Ana María Rojas", "+51 971 223 344",
                "SMS", ahora.minusMinutes(45),
                "HappyPets: Max tiene programada Cirugía hoy a las 11:30 AM. Recuerde que el paciente debe estar en ayuno estricto de 8 horas.",
                "Enviado"
        ));
        recordatorios.add(new RecordatorioCita(
                "REC-004", "CIT-0105", "Thor", "Jorge Valdivia Ramos", "jorge.valdivia@gmail.com",
                "E-Mail", ahora.minusMinutes(20),
                "Estimado Jorge, confirmamos la cita para Thor hoy a las 15:30 PM para Consulta Médica traumatológica en Happy Pets.",
                "Pendiente"
        ));

        // 3. Sala de Espera y Triaje de Urgencias
        LocalTime horaRef = LocalTime.now();
        pacientesTriaje.add(new PacienteTriaje(
                "TR-01", "CIT-0101", "VET-0091", "Rocky", "Canino · Golden Retriever",
                "Carlos Eduardo Morales", "+51 984 552 110", horaRef.minusMinutes(18),
                28.4, 38.6, 92, "VERDE (Normal)", "Control rutinario y desparasitación",
                "Consultorio 1 - Dr. Mendoza", "En Espera"
        ));
        pacientesTriaje.add(new PacienteTriaje(
                "TR-02", null, "VET-0740", "Bimba", "Felino · Persa",
                "Maribel Soto Cruz", "+51 991 445 612", horaRef.minusMinutes(10),
                3.9, 39.8, 140, "AMARILLO (Urgencia)", "Fiebre persistente, decaimiento e inapetencia",
                "Consultorio 2 - Dra. Morales", "En Espera"
        ));
        pacientesTriaje.add(new PacienteTriaje(
                "TR-03", null, "VET-0810", "Zeus", "Canino · Rottweiler",
                "Daniel Paredes Ríos", "+51 988 331 209", horaRef.minusMinutes(4),
                42.1, 37.2, 165, "ROJO (Emergencia crítica)", "Intoxicación por sospecha de ingesta accidental de veneno",
                "Tópico de Emergencias", "En Consulta"
        ));

        // 4. Módulo 3.1: Consultas Médicas
        atencionesMedicas.add(new AtencionMedica(
                "CM-2024-001", "VET-0091", "Rocky", "Canino · Golden Retriever",
                "45892134", "Carlos Eduardo Morales", "+51 984 552 110",
                LocalDate.now(), LocalTime.of(10, 15), "Dra. Camila Morales",
                "Decaimiento general, inapetencia y vómito recurrente desde hace 24 horas",
                28.4, 39.2, 110,
                "Paciente letárgico, mucosas ligeramente pálidas, dolor a la palpación abdominal media. Sonidos pulmonares limpios. Deshidratación estimada en 5%.",
                "Gastroenteritis aguda / Sospecha de cuerpo extraño",
                "Gastroenteritis aguda de origen dietario",
                "Fluidoterapia con Ringer Lactato IV 500ml, Maropitant 1mg/kg SC, Ranitidina 2mg/kg IV lento.",
                "1. Cerenia (Maropitant) 24mg comp: 1/2 comp c/24h x 3 días\n2. Sucralfato susp. 1g/5ml: 3ml c/8h antes de alimento x 5 días\n3. Probiótico Canino: 1 sobre c/24h x 7 días",
                "Ayuno estricto de sólidos por 12 horas. Ofrecer agua en pequeños sorbos. Dieta blanda (pollo hervido y arroz blanco en 4 tomas al día). Reposo en ambiente templado.",
                "Control presencial obligatorio en 48 horas (revisión de hidratación y evolución).",
                "Completada", 120.0
        ));
        atencionesMedicas.add(new AtencionMedica(
                "CM-2024-002", "VET-0144", "Luna", "Felino · Siamés",
                "45892134", "Carlos Eduardo Morales", "+51 984 552 110",
                LocalDate.now().minusDays(3), LocalTime.of(16, 30), "Dr. Roberto Mendoza",
                "Revisión médica y control previo a vacunación anual",
                4.2, 38.5, 145,
                "Paciente activa, atenta al entorno. Mucosas rosadas normocoloreadas. Ganglios linfáticos normales. Frecuencia respiratoria adecuada.",
                "Paciente clínicamente sano",
                "Paciente clínicamente sano / Apto para inmunización",
                "Examen físico completo sin hallazgos patológicos.",
                "No requiere farmacoterapia en este momento.",
                "Mantener esquema de nutrición felina balanceada y agua fresca disponible.",
                "Cita programada para refuerzo de desparasitación en 3 meses.",
                "Completada", 75.0
        ));
        atencionesMedicas.add(new AtencionMedica(
                "CM-2024-003", "VET-0238", "Toby", "Canino · Pug",
                "45892134", "Carlos Eduardo Morales", "+51 984 552 110",
                LocalDate.now().minusDays(1), LocalTime.of(11, 0), "Dra. Ana Silva",
                "Halitosis severa, dificultad para masticar alimento seco",
                8.5, 38.7, 120,
                "Presencia de sarro dental moderado a severo en premolares y molares superiores. Gingivitis marginal grado II con sangrado leve al tacto.",
                "Enfermedad periodontal grado II",
                "Periodontitis canina / Indicación de destartraje",
                "Profilaxis dental por ultrasonido bajo sedación y extracción de piezas comprometidas.",
                "1. Clindamicina 75mg: 1 cápsula c/12h x 7 días iniciando previo a cirugía\n2. Meloxicam 0.5mg: 1 dosis diaria x 3 días postoperatorio",
                "Alimentación exclusivamente húmeda por 4 días postquirúrgicos. Higiene bucal con enjuague de clorhexidina al 0.12%.",
                "Procedimiento quirúrgico programado en Quirófano 2.",
                "Derivada a Quirófano", 90.0
        ));

        // 5. Módulo 3.2: Vacunación y Desparasitación
        inmunizaciones.add(new RegistroInmunizacion(
                "VAC-2024-081", "Vacunación", "VET-0144", "Luna", "Felino · Siamés",
                "Carlos Eduardo Morales", "+51 984 552 110",
                "Triple Felina (Panleucopenia, Rinotraqueítis, Calicivirus)",
                "Zoetis · Lote TF-2024-881", "1.0 ml Subcutánea",
                LocalDate.now().minusDays(3), LocalDate.now().plusMonths(11),
                LocalDate.now().plusYears(1), "Dr. Roberto Mendoza", 4.2,
                "Buena tolerancia al biológico. Sin signos de anafilaxia o fiebre.", "Aplicada"
        ));
        inmunizaciones.add(new RegistroInmunizacion(
                "VAC-2024-082", "Vacunación", "VET-0091", "Rocky", "Canino · Golden Retriever",
                "Carlos Eduardo Morales", "+51 984 552 110",
                "Séxtuple Canina DHPP+L (Distemper, Hepatitis, Parvovirus, Parainfluenza, Leptospirosis)",
                "Boehringer Ingelheim · Lote SX-9012", "1.0 ml Subcutánea",
                LocalDate.now().minusMonths(6), LocalDate.now().plusMonths(8),
                LocalDate.now().plusMonths(6), "Dra. Camila Morales", 28.0,
                "Refuerzo anual administrado conforme al calendario preventivo.", "Aplicada"
        ));
        inmunizaciones.add(new RegistroInmunizacion(
                "VAC-2024-083", "Vacunación", "VET-0238", "Toby", "Canino · Pug",
                "Carlos Eduardo Morales", "+51 984 552 110",
                "Vacuna Antirrábica Rabisin", "Merial · Lote RB-4410", "1.0 ml Subcutánea",
                LocalDate.now().minusMonths(11), LocalDate.now().plusMonths(5),
                LocalDate.now().plusDays(15), "Dra. Ana Silva", 8.4,
                "Próximo refuerzo obligatorio programado para este mes.", "Próxima"
        ));
        inmunizaciones.add(new RegistroInmunizacion(
                "DES-2024-041", "Desparasitación Interna", "VET-0091", "Rocky", "Canino · Golden Retriever",
                "Carlos Eduardo Morales", "+51 984 552 110",
                "Total F Total Plus / Drontal Plus", "Bayer · Lote DP-119",
                "3 tabletas orales (según 28.4 kg)",
                LocalDate.now().minusMonths(2), LocalDate.now().plusMonths(18),
                LocalDate.now().plusDays(25), "Dra. Camila Morales", 28.4,
                "Control trimestral de nematodos y cestodos.", "Aplicada"
        ));
        inmunizaciones.add(new RegistroInmunizacion(
                "DES-2024-042", "Desparasitación Externa", "VET-0144", "Luna", "Felino · Siamés",
                "Carlos Eduardo Morales", "+51 984 552 110",
                "Bravecto Plus Spot-on Felino (2.8 - 6.25 kg)", "MSD Salud Animal · Lote BV-774",
                "1 pipeta tópica dorsal",
                LocalDate.now().minusMonths(1), LocalDate.now().plusMonths(14),
                LocalDate.now().plusMonths(2), "Dr. Roberto Mendoza", 4.2,
                "Protección extendida contra pulgas, garrapatas y ácaros por 12 semanas.", "Aplicada"
        ));
        inmunizaciones.add(new RegistroInmunizacion(
                "DES-2024-043", "Desparasitación Externa", "VET-0238", "Toby", "Canino · Pug",
                "Carlos Eduardo Morales", "+51 984 552 110",
                "Simparica Trio 10-20kg", "Zoetis · Lote ST-303", "1 tableta masticable",
                LocalDate.now().minusMonths(3), LocalDate.now().plusMonths(12),
                LocalDate.now().plusDays(4), "Dra. Ana Silva", 8.5,
                "Dosis vencida. Requiere renovación inmediata de protección.", "Pendiente"
        ));

        // 6. Módulo 3.3: Cirugías y Quirófano
        cirugias.add(new RegistroCirugia(
                "QX-2024-01", "VET-0238", "Toby", "Canino · Pug",
                "Carlos Eduardo Morales", "+51 984 552 110",
                "Profilaxis Dental por Ultrasonido + Extracción de 2 piezas (208 y 209)",
                "Quirófano 2 (Procedimientos)", "Dra. Ana Silva", "Lic. Marta Ferrer (Anestesista)",
                LocalDate.now(), LocalTime.of(10, 0), LocalTime.of(10, 15), LocalTime.of(11, 40),
                "Inhalatoria Isoflurano + Inducción Propofol",
                "Propofol 2mg/kg IV, Isoflurano 1.5%, Meloxicam 0.2mg/kg SC, Sutura absorbable 3-0",
                "Procedimiento sin complicaciones. Extracción atraumática de premolares con periodontitis avanzada. Constantes estables durante todo el acto quirúrgico.",
                2, "En recuperación anestésica",
                "Reposo absoluto por 3 días. Limpieza de herida con clorhexidina diluida. Uso obligatorio de collar isabelino. Dieta húmeda blanda.",
                LocalDate.now().plusDays(10), "En Recuperación"
        ));
        cirugias.add(new RegistroCirugia(
                "QX-2024-02", "VET-0144", "Luna", "Felino · Siamés",
                "Carlos Eduardo Morales", "+51 984 552 110",
                "Ovariohisterectomía preventiva (Esterilización)",
                "Quirófano 1 (Cirugía Mayor)", "Dr. Carlos Méndez", "Dra. Camila Morales",
                LocalDate.now().minusDays(5), LocalTime.of(8, 30), LocalTime.of(8, 45), LocalTime.of(9, 50),
                "Inhalatoria Isoflurano + Sedación Midazolam/Ketamina",
                "Midazolam 0.2mg/kg, Ketamina 5mg/kg, Cefalexina 20mg/kg, Meloxicam 0.1mg/kg",
                "Cirugía exitosa por línea media. Ligaduras hemostáticas seguras con PDS 3-0. Cierre dérmico intradérmico estético.",
                1, "Alerta y estable",
                "Collar isabelino permanente por 10 días. Curación con solución antiséptica 2 veces al día. Cefalexina 100mg c/12h x 7 días.",
                LocalDate.now().plusDays(5), "Alta Quirúrgica"
        ));
        cirugias.add(new RegistroCirugia(
                "QX-2024-03", "VET-0091", "Rocky", "Canino · Golden Retriever",
                "Carlos Eduardo Morales", "+51 984 552 110",
                "Excisión de Nódulo Cutáneo / Tumor subcutáneo en flanco izquierdo",
                "Quirófano 1 (Cirugía Mayor)", "Dr. Carlos Méndez", "Lic. Marta Ferrer (Anestesista)",
                LocalDate.now().plusDays(2), LocalTime.of(9, 0), null, null,
                "Inhalatoria Isoflurano con intubación endotraqueal",
                "Protocolo preanestésico con Acepromacina y Morfina. Biopsia programada para patología.",
                "Programación electiva. Paciente con exámenes prequirúrgicos completos y aptos.",
                0, "Pendiente de ingreso",
                "Ayuno estricto de 12 horas previo al horario quirúrgico. Baño medicado el día anterior.",
                LocalDate.now().plusDays(14), "Programada"
        ));

        // 7. Módulo 3.4: Laboratorio e Imágenes
        ordenesLaboratorio.add(new OrdenLaboratorio(
                "LAB-2024-01", "Laboratorio Clínico", "Hemograma Completo Automatizado",
                "VET-0091", "Rocky", "Canino · Golden Retriever",
                "Carlos Eduardo Morales", "+51 984 552 110",
                LocalDate.now(), "Urgente", "Dra. Camila Morales",
                "Decaimiento, vómitos recurrentes y fiebre. Descartar proceso infeccioso activo o pancreatitis.",
                LocalDate.now(), "Lab. Veterinario Central - Lic. P. Torres",
                "Leucocitos: 18.2 mil/uL (Ref: 6.0-17.0) [ELEVADO]\nHematocrito: 42% (Ref: 37-55%)\nPlaquetas: 245 mil/uL (Ref: 200-500 mil)\nNeutrófilos en banda: 4% (Ref: 0-3%)",
                "Leucocitosis leve con desviación a la izquierda compatible con foco inflamatorio/infeccioso agudo gastrointestinal. Serie roja y plaquetaria dentro de los rangos fisiológicos.",
                "Correlacionar con ecografía abdominal y bioquímica sanguínea (amilasa/lipasa). Iniciar antibioticoterapia y protector gástrico.",
                "Completado"
        ));
        ordenesLaboratorio.add(new OrdenLaboratorio(
                "IMG-2024-02", "Diagnóstico por Imágenes", "Radiografía de Tórax (Proyecciones LL y VD)",
                "VET-0144", "Luna", "Felino · Siamés",
                "Carlos Eduardo Morales", "+51 984 552 110",
                LocalDate.now().minusDays(2), "Normal", "Dr. Andrés Restrepo",
                "Tos esporádica nocturna, descartar asma felina o bronquitis crónica.",
                LocalDate.now().minusDays(2), "Dr. Andrés Restrepo (Especialista en Imagenología)",
                "Silueta cardíaca conservada de tamaño normal (VHS: 7.4v). Patrón bronquial leve difuso en campos caudales. Sin evidencia de consolidación alveolar, neumotórax ni derrame pleural.",
                "Estudio radiográfico torácico que evidencia bronquiopatía inflamatoria leve compatible con bronquitis alérgica felina incipiente.",
                "Se sugiere prueba terapéutica broncodilatadora/antiinflamatoria y control radiológico evolutivo en 30 días si persisten síntomas.",
                "Completado"
        ));
        ordenesLaboratorio.add(new OrdenLaboratorio(
                "IMG-2024-03", "Diagnóstico por Imágenes", "Ecografía Abdominal Completa",
                "VET-0091", "Rocky", "Canino · Golden Retriever",
                "Carlos Eduardo Morales", "+51 984 552 110",
                LocalDate.now(), "Urgente", "Dra. Camila Morales",
                "Dolor a la palpación epigástrica y vómitos. Descartar cuerpo extraño en estómago o duodeno.",
                LocalDate.now(), "Dr. Andrés Restrepo (Imagenología)",
                "Estómago con moderado contenido líquido y gas; pared gástrica engrosada (4.8mm) con conservación de capas. Duodeno y yeyuno con peristaltismo activo sin evidencia de efecto masa intraluminal ni dilatación anómala. Hígado, bazo y riñones ecográficamente normales.",
                "Gastroduodenitis aguda reactiva. Se descarta obstrucción mecánica por cuerpo extraño radiodenso o radiotransparente en el tracto superior.",
                "Continuar con tratamiento médico conservador para gastroenteritis y monitorear tolerancia oral.",
                "Completado"
        ));
        ordenesLaboratorio.add(new OrdenLaboratorio(
                "LAB-2024-04", "Laboratorio Clínico", "Perfil Bioquímico Hepato-Renal + Glucosa",
                "VET-0238", "Toby", "Canino · Pug",
                "Carlos Eduardo Morales", "+51 984 552 110",
                LocalDate.now().minusDays(1), "Normal", "Dra. Ana Silva",
                "Evaluación prequirúrgica obligatoria para profilaxis dental bajo anestesia general.",
                LocalDate.now().minusDays(1), "Lab. Central Veterinario",
                "ALT/GPT: 38 U/L (Ref: 10-100)\nFosfatasa Alcalina: 65 U/L (Ref: 23-212)\nCreatinina: 0.9 mg/dL (Ref: 0.5-1.5)\nUrea: 32 mg/dL (Ref: 15-40)\nGlucosa: 94 mg/dL (Ref: 70-115)",
                "Función renal y hepática en rangos óptimos. Glucemia normal. Sin contraindicaciones metabólicas para sedación.",
                "Apto para procedimiento quirúrgico odontológico con protocolo anestésico estándar.",
                "Completado"
        ));

        // 8. Módulo 4.1: Grooming y Peluquería
        serviciosGrooming.add(new happypets.model.ServicioGrooming(
                "GR-2024-01", "VET-0144", "Luna", "Felino · Siamés",
                "Carlos Eduardo Morales", "+51 984 552 110",
                LocalDate.now(), LocalTime.of(10, 30), "Ana Martínez (Groomer)",
                "Spa Completo Felino (Baño cosmético + desenredado + corte de uñas)",
                "Libre de ectoparásitos", "Piel sana y pelaje sedoso",
                "Champú Avena Suave, Acondicionador Desenredante, Colonia Baby Cat",
                "Paciente muy dócil y tranquila durante el cepillado.", 65.0, "En Corte y Secado"
        ));
        serviciosGrooming.add(new happypets.model.ServicioGrooming(
                "GR-2024-02", "VET-0091", "Rocky", "Canino · Golden Retriever",
                "Carlos Eduardo Morales", "+51 984 552 110",
                LocalDate.now(), LocalTime.of(11, 45), "Carlos Mendoza (Estilista Canino)",
                "Baño Medicado Dermatológico + Deslanado Profundo",
                "Sin ectoparásitos visibles", "Dermatitis alérgica leve en flanco",
                "Champú Clorhexidina 3% con Ketoconazol, Acondicionador Hidratante",
                "Disfruta del agua tibia. Requiere secado con turbina de bajo ruido.", 95.0, "En Baño"
        ));
        serviciosGrooming.add(new happypets.model.ServicioGrooming(
                "GR-2024-03", "VET-0238", "Toby", "Canino · Pug",
                "Carlos Eduardo Morales", "+51 984 552 110",
                LocalDate.now(), LocalTime.of(9, 15), "Luis Peña (Groomer)",
                "Baño Hipoalergénico + Limpieza de Pliegues Faciales + Vaciado de Glándulas",
                "Libre de pulgas", "Pliegues nasales limpios sin eritema",
                "Champú Hipoalergénico hipoalergénico, Solución antiséptica para pliegues",
                "Excelente comportamiento. Pliegues faciales secados minuciosamente.", 55.0, "Listo para Entrega"
        ));

        // 9. Módulo 4.2: Hospitalización
        internamientos.add(new happypets.model.InternamientoHospitalario(
                "HOSP-2024-01", "Box 01", "UCI / Cuidados Intensivos",
                "VET-0810", "Zeus", "Canino · Rottweiler",
                "Daniel Paredes Ríos", "+51 988 331 209",
                "Intoxicación severa por sospecha de ingesta de rodenticida anticoagulante",
                "Dr. Roberto Mendoza", LocalDate.now().minusDays(1), LocalTime.of(14, 0),
                LocalDate.now().plusDays(3), 42.1, 37.8, 130,
                "Ringer Lactato IV a 85 ml/h continuo + Bomba de infusión",
                "Fitomenadiona (Vitamina K1) 2.5mg/kg SC c/12h, Omeprazol 1mg/kg IV, Carbón activado",
                "Paciente en monitoreo continuo. Mucosas normocoloreadas. Reflejos presentes. Diuresis positiva.",
                "CRÍTICO", 150.0, "Internado / En Tratamiento"
        ));
        internamientos.add(new happypets.model.InternamientoHospitalario(
                "HOSP-2024-02", "Box 02", "Hospitalización General",
                "VET-0238", "Toby", "Canino · Pug",
                "Carlos Eduardo Morales", "+51 984 552 110",
                "Postoperatorio inmediato por destartraje periodontal y exodoncia",
                "Dra. Ana Silva", LocalDate.now(), LocalTime.of(11, 45),
                LocalDate.now().plusDays(1), 8.5, 38.4, 115,
                "Cloruro de Sodio 0.9% IV a 20 ml/h",
                "Clindamicina 11mg/kg IV c/12h, Meloxicam 0.1mg/kg SC c/24h",
                "Recuperación anestésica satisfactoria. Alerta y respondiendo a estímulos sonoros.",
                "ESTABLE", 85.0, "Internado / En Tratamiento"
        ));
        internamientos.add(new happypets.model.InternamientoHospitalario(
                "HOSP-2024-03", "Box 05", "Aislamiento Infeccioso",
                "VET-0740", "Bimba", "Felino · Persa",
                "Maribel Soto Cruz", "+51 991 445 612",
                "Gastroenteritis aguda con deshidratación moderada (7%)",
                "Dra. Camila Morales", LocalDate.now().minusDays(2), LocalTime.of(16, 20),
                LocalDate.now().plusDays(1), 3.9, 38.9, 150,
                "Normosol-R IV con suplementación de KCl a 15 ml/h",
                "Maropitant 1mg/kg SC c/24h, Ranitidina 2mg/kg IV c/12h",
                "Sin episodios eméticos en las últimas 18 horas. Inicio de dieta líquida recovery tolerada.",
                "OBSERVACIÓN", 110.0, "Internado / En Tratamiento"
        ));

        // 10. Módulo 4.3: Hotel y Guardería
        reservasHospedaje.add(new happypets.model.ReservaHospedaje(
                "HOT-2024-01", "Suite 01 Canina (Jardín)", "VET-0091", "Rocky",
                "Canino · Golden Retriever", "Carlos Eduardo Morales", "+51 984 552 110",
                "+51 01 432 9980", LocalDate.now().minusDays(1), LocalDate.now().plusDays(2),
                3, "Pro Plan Adulto provisto por el dueño (2 raciones de 250g diarias)",
                "3 paseos diarios en zona de césped con pelota. Muy sociable.",
                true, "Glucosamina 1 tableta diaria en desayuno", 70.0, 210.0, "En Estadía / Hospedado"
        ));
        reservasHospedaje.add(new happypets.model.ReservaHospedaje(
                "HOT-2024-02", "Suite 03 Felina (Rascador)", "VET-0144", "Luna",
                "Felino · Siamés", "Carlos Eduardo Morales", "+51 984 552 110",
                "+51 01 432 9980", LocalDate.now().minusDays(4), LocalDate.now().minusDays(1),
                3, "Royal Canin Fit 32 + pouch húmedo matutino",
                "Juego interactivo con plumas en área cerrada de enriquecimiento ambiental",
                false, "Ninguna", 55.0, 165.0, "Finalizada / Check-out"
        ));
        reservasHospedaje.add(new happypets.model.ReservaHospedaje(
                "HOT-2024-03", "Suite 02 Canina", "VET-0180", "Max",
                "Canino · Pastor Alemán", "Roberto Solano Vega", "+51 982 110 445",
                "+51 982 110 440", LocalDate.now().plusDays(2), LocalDate.now().plusDays(6),
                4, "Hills Science Diet Large Breed (300g c/12h)",
                "Paseos individuales exclusivos con correa. Nivel alto de energía.",
                true, "Omega 3 en cápsula nocturna", 75.0, 300.0, "Confirmada"
        ));

        // 11. Módulo 4.4: Adopciones y Rescates
        mascotasAdopcion.add(new happypets.model.MascotaAdopcion(
                "ADOP-01", "Pelusa", "Felino", "Mestizo Europeo",
                "8 meses", "Hembra", "Pequeño", "Extremadamente cariñosa, ronronea y convive con perros",
                "Rescatada en San Borja en estado de vulnerabilidad. Completamente sana, esterilizada y desparasitada.",
                true, true, true, null, null, null, null, null, 50.0, "Disponible"
        ));
        mascotasAdopcion.add(new happypets.model.MascotaAdopcion(
                "ADOP-02", "Duque", "Canino", "Cruza Golden / Mestizo",
                "1 año 2 m.", "Macho", "Mediano", "Muy sociable, obediente, enérgico y juguetón con niños",
                "Rescatado de la vía pública con herida cicatrizada. Rehabilitado en Happy Pets. Sabe pasear con correa.",
                true, true, true, "Familia Huamán Pérez", "44102938", "+51 987 334 112",
                "Av. Aviación 2840, San Borja", LocalDate.now().minusDays(2), 80.0, "En Evaluación"
        ));
        mascotasAdopcion.add(new happypets.model.MascotaAdopcion(
                "ADOP-03", "Chispita", "Canino", "Mestizo Poodle",
                "2 años", "Hembra", "Pequeño", "Tranquila, faldera, ideal para departamento o adultos mayores",
                "Entregada por tutores de la tercera edad que no podían atenderla. Muy educada para hacer sus necesidades afuera.",
                true, true, true, null, null, null, null, null, 60.0, "Disponible"
        ));
        mascotasAdopcion.add(new happypets.model.MascotaAdopcion(
                "ADOP-04", "Milo", "Felino", "Criollo Atigrado",
                "1 año", "Macho", "Mediano", "Curioso, independiente y juguetón",
                "Rescatado de una obra en construcción. Vacunado con triple felina y antirrábica.",
                true, true, true, "Andrea Corrales", "47281902", "+51 993 445 120",
                "Calle Las Camelias 412, Surco", LocalDate.now().minusMonths(1), 50.0, "Adoptado con Éxito"
        ));

        // 12. Módulo 5.1: Catálogo de Productos y Fármacos
        productosFarmacia.add(new happypets.model.ProductoFarmacia(
                "PROD-001", "NexGard Spectra (7.5 a 15 kg)", "Antiparasitario",
                "Afoxolaner 37.5 mg + Milbemicina 7.5 mg", "Caja x 3 comp. masticables",
                "Canino", false, false, 52.0, 85.0, 24, 10,
                "LOT-2024-88A", LocalDate.now().plusMonths(18),
                "Boehringer Ingelheim Animal Health Perú", "Disponible"
        ));
        productosFarmacia.add(new happypets.model.ProductoFarmacia(
                "PROD-002", "Amoxicilina + Clavulánico Vet 500 mg", "Antibiótico",
                "Amoxicilina 400 mg + Ác. Clavulánico 100 mg", "Caja x 20 comprimidos",
                "Mixto Canino/Felino", true, false, 28.5, 45.0, 18, 8,
                "LOT-2024-12C", LocalDate.now().plusMonths(14),
                "Laboratorios Zoetis Perú S.A.C.", "Disponible"
        ));
        productosFarmacia.add(new happypets.model.ProductoFarmacia(
                "PROD-003", "Meloxicam Gotas 1.5 mg/ml", "Analgésico / AINE",
                "Meloxicam 1.5 mg/ml", "Frasco gotero 10 ml",
                "Mixto Canino/Felino", true, false, 22.0, 38.0, 6, 8,
                "LOT-2023-99F", LocalDate.now().plusMonths(2),
                "Laboratorios Drag Pharma", "Bajo Stock"
        ));
        productosFarmacia.add(new happypets.model.ProductoFarmacia(
                "PROD-004", "Vacuna Séxtuple Canina Nobivac DHPPi+L", "Vacuna / Biológico",
                "Virus vivo modificado + Bacterina Leptospira", "Vial x 1 dosis + diluyente",
                "Canino", true, true, 35.0, 60.0, 30, 15,
                "LOT-2024-05V", LocalDate.now().plusMonths(10),
                "MSD Salud Animal Perú", "Disponible"
        ));
        productosFarmacia.add(new happypets.model.ProductoFarmacia(
                "PROD-005", "Royal Canin Gastrointestinal Dog", "Alimento Clínico",
                "Fórmula alta digestibilidad y electrolitos", "Bolsa 2 kg",
                "Canino", false, false, 65.0, 98.0, 12, 5,
                "RC-2024-41", LocalDate.now().plusMonths(11),
                "Distribuidora Veterinaria Santa Anita S.A.C.", "Disponible"
        ));
        productosFarmacia.add(new happypets.model.ProductoFarmacia(
                "PROD-006", "Ketamina 10% Inyectable Vet", "Anestésico / Controlado",
                "Ketamina Clorhidrato 100 mg/ml", "Frasco ampolla 50 ml",
                "Mixto Canino/Felino", true, false, 75.0, 120.0, 4, 3,
                "LOT-2024-K2", LocalDate.now().plusMonths(16),
                "Laboratorios Agrovet Market", "Disponible"
        ));
        productosFarmacia.add(new happypets.model.ProductoFarmacia(
                "PROD-007", "Cloruro de Sodio 0.9% 500 ml", "Material Quirúrgico / Insumo",
                "Solución Salina Fisiológica Estéril", "Frasco infusión 500 ml",
                "Mixto Canino/Felino", false, false, 6.5, 15.0, 40, 15,
                "LOT-2024-CL", LocalDate.now().plusMonths(24),
                "Laboratorios Medifarma", "Disponible"
        ));

        // 13. Módulo 5.2: Control de Stock y Lotes
        movimientosStock.add(new happypets.model.LoteMovimientoStock(
                "MOV-2024-01", "PROD-001", "NexGard Spectra (7.5 a 15 kg)", "LOT-2024-88A",
                "Ingreso por Compra (OC-2024-01)", 20, 4, 24,
                LocalDate.now().minusDays(3), LocalDate.now().plusMonths(18),
                "Dra. Elena Ruiz (Regente Farmacéutico)", "Recepción de pedido Zoetis conforme a factura."
        ));
        movimientosStock.add(new happypets.model.LoteMovimientoStock(
                "MOV-2024-02", "PROD-002", "Amoxicilina + Clavulánico Vet 500 mg", "LOT-2024-12C",
                "Salida por Consulta (HC-002)", 2, 20, 18,
                LocalDate.now().minusDays(1), LocalDate.now().plusMonths(14),
                "Dr. Roberto Mendoza", "Dispensación para tratamiento de Rocky Morales."
        ));
        movimientosStock.add(new happypets.model.LoteMovimientoStock(
                "MOV-2024-03", "PROD-003", "Meloxicam Gotas 1.5 mg/ml", "LOT-2023-99F",
                "Salida por Cirugía (QX-2024-02)", 1, 7, 6,
                LocalDate.now(), LocalDate.now().plusMonths(2),
                "Dra. Ana Silva", "Analgesia postoperatoria para Toby Morales."
        ));

        // 14. Módulo 5.3: Proveedores y Órdenes de Compra
        proveedoresFarmacia.add(new happypets.model.ProveedorFarmacia(
                "20123456789", "Laboratorios Zoetis Perú S.A.C.", "Zoetis Animal Health",
                "+51 01 614 7800", "pedidos.peru@zoetis.com",
                "Av. República de Panamá 3591, San Isidro", "Ing. Roberto Calderón (+51 977 441 230)",
                "Crédito 30 días", "Homologado / Activo"
        ));
        proveedoresFarmacia.add(new happypets.model.ProveedorFarmacia(
                "20501234567", "Boehringer Ingelheim Animal Health Perú", "Boehringer Ingelheim",
                "+51 01 411 5000", "veterinaria@boehringer.com",
                "Av. Canaval y Moreyra 480, San Isidro", "Lic. Vanessa Prado (+51 998 120 445)",
                "Crédito 30 días", "Homologado / Activo"
        ));
        proveedoresFarmacia.add(new happypets.model.ProveedorFarmacia(
                "20345678901", "Distribuidora Veterinaria Santa Anita S.A.C.", "Disvet Santa Anita",
                "+51 01 362 8900", "ventas@disvetsantaanita.com",
                "Av. Nicolás Ayllón 2450, Ate", "Sr. Carlos Fuentes (+51 984 551 099)",
                "Contado Factura", "Homologado / Activo"
        ));
        proveedoresFarmacia.add(new happypets.model.ProveedorFarmacia(
                "20456789012", "MSD Salud Animal Perú", "MSD Animal Health",
                "+51 01 411 9000", "contacto@msd-animal-health.pe",
                "Av. El Derby 055, Surco", "Dr. Fernando Rivas (+51 991 332 556)",
                "Crédito 15 días", "Homologado / Activo"
        ));

        ordenesCompra.add(new happypets.model.OrdenCompra(
                "OC-2024-01", "20501234567", "Boehringer Ingelheim Animal Health Perú",
                LocalDate.now().minusDays(5), LocalDate.now().minusDays(3),
                "20x NexGard Spectra (7.5-15kg)", 1040.0, 187.2, 1227.2,
                "Recibida en Almacén", "Dra. Elena Ruiz"
        ));
        ordenesCompra.add(new happypets.model.OrdenCompra(
                "OC-2024-02", "20123456789", "Laboratorios Zoetis Perú S.A.C.",
                LocalDate.now().minusDays(2), LocalDate.now().plusDays(3),
                "30x Amoxicilina Vet, 15x Convenia 10ml", 1850.0, 333.0, 2183.0,
                "Enviada a Proveedor", "Administración Happy Pets"
        ));
        ordenesCompra.add(new happypets.model.OrdenCompra(
                "OC-2024-03", "20456789012", "MSD Salud Animal Perú",
                LocalDate.now(), LocalDate.now().plusDays(4),
                "50x Vacuna Séxtuple Nobivac, 30x Nobivac Rabies", 2150.0, 387.0, 2537.0,
                "Borrador", "Dra. Elena Ruiz"
        ));

        // 15. Módulo 5.4: Ajustes y Mermas
        ajustesMermas.add(new happypets.model.AjusteMerma(
                "AJU-2024-01", LocalDate.now().minusDays(4), "PROD-003",
                "Meloxicam Gotas 1.5 mg/ml", "LOT-2023-80A", "Vencimiento de Lote",
                2, 22.0, 44.0, "Lote caducado retirado de estante de farmacia para disposición final.",
                "Dr. Carlos Vargas (Director Médico)", "Aprobado y Descargado"
        ));
        ajustesMermas.add(new happypets.model.AjusteMerma(
                "AJU-2024-02", LocalDate.now().minusDays(2), "PROD-007",
                "Cloruro de Sodio 0.9% 500 ml", "LOT-2024-CL", "Merma por Rotura / Deterioro",
                1, 6.5, 6.5, "Frasco fisurado por caída accidental durante recepción de almacén.",
                "Dra. Elena Ruiz", "Aprobado y Descargado"
        ));
        ajustesMermas.add(new happypets.model.AjusteMerma(
                "AJU-2024-03", LocalDate.now().minusDays(1), "PROD-001",
                "NexGard Spectra (7.5 a 15 kg)", "LOT-2024-88A", "Ajuste Físico Positivo",
                1, 52.0, 52.0, "Sobrante de conteo físico mensual verificado contra kardex.",
                "Administración Happy Pets", "Aprobado y Descargado"
        ));

        // 16. Módulo 6.1: Punto de Venta (POS)
        List<ItemVentaPOS> itemsV1 = new ArrayList<>();
        itemsV1.add(new ItemVentaPOS("SERV-001", "Consulta Médica General", "Consultas", 1, 50.0));
        itemsV1.add(new ItemVentaPOS("PROD-001", "NexGard Spectra (7.5 a 15 kg)", "Farmacia", 1, 85.0));
        ventasPOS.add(new VentaPOS(
                "VTA-2024-001", "B001-000412", "Boleta Electrónica",
                LocalDateTime.now().minusHours(5), "Carlos Eduardo Morales Soto", "45892134",
                "Rocky", itemsV1, 5.0, "Tarjeta Débito / Crédito", 128.25, "Pagada", "Joanna Corrales"
        ));

        List<ItemVentaPOS> itemsV2 = new ArrayList<>();
        itemsV2.add(new ItemVentaPOS("SERV-003", "Vacuna Antirrábica Felina + Cartilla", "Vacunas", 1, 45.0));
        itemsV2.add(new ItemVentaPOS("PROD-005", "Royal Canin Gastrointestinal Dog", "Alimentos", 1, 98.0));
        ventasPOS.add(new VentaPOS(
                "VTA-2024-002", "B001-000413", "Boleta Electrónica",
                LocalDateTime.now().minusHours(3), "Carlos Eduardo Morales Soto", "45892134",
                "Luna", itemsV2, 0.0, "Yape / Plin", 143.0, "Pagada", "Joanna Corrales"
        ));

        List<ItemVentaPOS> itemsV3 = new ArrayList<>();
        itemsV3.add(new ItemVentaPOS("SERV-004", "Baño y Corte de Raza (Grooming)", "Otros", 1, 45.0));
        itemsV3.add(new ItemVentaPOS("PROD-002", "Amoxicilina + Clavulánico Vet 500 mg", "Farmacia", 1, 45.0));
        ventasPOS.add(new VentaPOS(
                "VTA-2024-003", "TK-001205", "Ticket POS",
                LocalDateTime.now().minusHours(1), "Mariana Paredes Torres", "72198421",
                "Max", itemsV3, 10.0, "Efectivo", 100.0, "Pagada", "Harry Arroyo"
        ));

        // 17. Módulo 6.2: Cuentas por Cobrar
        cuentasPorCobrar.add(new CuentaPorCobrar(
                "CXC-001", "Carlos Eduardo Morales Soto",
                "Hospitalización y fluidoterapia de urgencia Rocky", 450.0,
                250.0, LocalDate.now().minusDays(10), LocalDate.now().plusDays(5),
                "Parcial", "+51 984 552 110", "B001-000388"
        ));
        cuentasPorCobrar.add(new CuentaPorCobrar(
                "CXC-002", "Patricia Salazar Alva",
                "Cirugía traumatológica compleja y pines óseos Coco", 920.0,
                0.0, LocalDate.now().minusDays(20), LocalDate.now().minusDays(5),
                "Vencido", "+51 977 123 456", "F001-000155"
        ));
        cuentasPorCobrar.add(new CuentaPorCobrar(
                "CXC-003", "Luis Miguel Ramos",
                "Paquete anual preventivo de vacunas x 3 pacientes", 380.0,
                100.0, LocalDate.now().minusDays(3), LocalDate.now().plusDays(12),
                "Pendiente", "+51 982 341 552", "TK-001150"
        ));
        cuentasPorCobrar.add(new CuentaPorCobrar(
                "CXC-004", "Mariana Paredes Torres",
                "Estancia Hotel & Guardería Canina (10 días) Max", 400.0,
                400.0, LocalDate.now().minusDays(15), LocalDate.now().minusDays(1),
                "Pagado", "+51 993 445 120", "B001-000399"
        ));
        cuentasPorCobrar.add(new CuentaPorCobrar(
                "CXC-005", "Roberto Dávila Wong",
                "Tratamiento dermatológico y biopsia cutánea Lucas", 260.0,
                0.0, LocalDate.now().minusDays(2), LocalDate.now().plusDays(15),
                "Pendiente", "+51 965 412 889", "B001-000420"
        ));

        // 18. Módulo 6.2: Cuentas por Pagar
        cuentasPorPagar.add(new CuentaPorPagar(
                "CXP-001", "Laboratorios Zoetis Perú S.A.C.",
                "Lote antibióticos Convenia y vacunas Vanguard Plus", 1850.0,
                0.0, LocalDate.now().minusDays(15), LocalDate.now().plusDays(15),
                "Pendiente", "20123456789", "F002-004812"
        ));
        cuentasPorPagar.add(new CuentaPorPagar(
                "CXP-002", "Boehringer Ingelheim Animal Health Perú",
                "Factura quincenal antiparasitarios NexGard Spectra", 1227.2,
                1227.2, LocalDate.now().minusDays(25), LocalDate.now().minusDays(5),
                "Pagado", "20501234567", "F001-009941"
        ));
        cuentasPorPagar.add(new CuentaPorPagar(
                "CXP-003", "Distribuidora Veterinaria Santa Anita S.A.C.",
                "Suministro alimentos clínicos Royal Canin y Hills", 1450.0,
                600.0, LocalDate.now().minusDays(8), LocalDate.now().plusDays(7),
                "Parcial", "20345678901", "F003-001248"
        ));
        cuentasPorPagar.add(new CuentaPorPagar(
                "CXP-004", "Laboratorios Medifarma S.A.",
                "Lote fluidoterapia sueros y catéteres endovenosos", 680.0,
                0.0, LocalDate.now().minusDays(35), LocalDate.now().minusDays(5),
                "Vencido", "20100088899", "F005-000812"
        ));
        cuentasPorPagar.add(new CuentaPorPagar(
                "CXP-005", "BioClean Residuos Hospitalarios SAC",
                "Servicio mensual recojo residuos biocontaminados", 320.0,
                0.0, LocalDate.now().minusDays(4), LocalDate.now().plusDays(10),
                "Pendiente", "20489912345", "F001-003411"
        ));

        // 19. Módulo 6.3: Control de Caja Chica
        movimientosCajaChica.add(new MovimientoCajaChica(
                "CCH-001", LocalDate.now().minusDays(5), "Ingreso",
                "Apertura de fondo fijo mensual de caja chica", 800.0,
                "Joanna Corrales", "Recibo Interno #001", 800.0
        ));
        movimientosCajaChica.add(new MovimientoCajaChica(
                "CCH-002", LocalDate.now().minusDays(4), "Egreso",
                "Compra urgente de desinfectante y material de aseo", 64.50,
                "Joanna Corrales", "Boleta B003-4512", 735.50
        ));
        movimientosCajaChica.add(new MovimientoCajaChica(
                "CCH-003", LocalDate.now().minusDays(3), "Egreso",
                "Pago movilidad / taxi envío muestras de biopsia a laboratorio", 25.00,
                "Harry Arroyo", "Vale de Caja #012", 710.50
        ));
        movimientosCajaChica.add(new MovimientoCajaChica(
                "CCH-004", LocalDate.now().minusDays(2), "Egreso",
                "Recarga de 3 botellones de agua purificada San Mateo", 45.00,
                "Joanna Corrales", "Ticket TK-8812", 665.50
        ));
        movimientosCajaChica.add(new MovimientoCajaChica(
                "CCH-005", LocalDate.now().minusDays(1), "Egreso",
                "Compra insumos de papelería, clips y rollos térmicos POS", 58.00,
                "Harry Arroyo", "Boleta B012-9981", 607.50
        ));
        movimientosCajaChica.add(new MovimientoCajaChica(
                "CCH-006", LocalDate.now(), "Ingreso",
                "Reembolso de reposición de caja chica por administración", 192.50,
                "Administración Happy Pets", "Recibo Interno #002", 800.00
        ));
        movimientosCajaChica.add(new MovimientoCajaChica(
                "CCH-007", LocalDate.now(), "Egreso",
                "Café, refrigerio e insumos para guardia médica nocturna", 32.00,
                "Dra. Patricia Silva", "Ticket TK-4410", 768.00
        ));

        // 20. Módulo 6.4: Control de Egresos Operativos
        egresosOperativos.add(new EgresoOperativo(
                "EGR-001", LocalDate.now().minusDays(8), "Alquiler de Local Clínico",
                "Merced conductiva mensual sede principal Happy Pets",
                "Inmobiliaria Santa Anita SAC", 3800.0, "Transferencia", "Factura F001-008219", "Pagado"
        ));
        egresosOperativos.add(new EgresoOperativo(
                "EGR-002", LocalDate.now().minusDays(6), "Servicios Básicos (Luz/Agua/Net)",
                "Recibo de energía eléctrica consultorios y quirófano",
                "Enel Distribución Perú S.A.A.", 640.0, "Transferencia", "Recibo Luz #498214", "Pagado"
        ));
        egresosOperativos.add(new EgresoOperativo(
                "EGR-003", LocalDate.now().minusDays(5), "Servicios Básicos (Luz/Agua/Net)",
                "Consumo de agua potable y saneamiento clínica",
                "Sedapal", 215.0, "Transferencia", "Recibo Sedapal #1128941", "Pagado"
        ));
        egresosOperativos.add(new EgresoOperativo(
                "EGR-004", LocalDate.now().minusDays(4), "Servicios Básicos (Luz/Agua/Net)",
                "Internet corporativo fibra óptica 500 Mbps + línea fija",
                "Telefónica del Perú (Movistar)", 249.90, "Tarjeta", "Factura F018-994120", "Pagado"
        ));
        egresosOperativos.add(new EgresoOperativo(
                "EGR-005", LocalDate.now().minusDays(3), "Mantenimiento de Equipos Médicos",
                "Calibración semestral de equipo de Rayos X y autoclave",
                "Servicios BioMédicos Perú SAC", 750.0, "Transferencia", "Factura F002-001844", "Pagado"
        ));
        egresosOperativos.add(new EgresoOperativo(
                "EGR-006", LocalDate.now().minusDays(2), "Marketing y Publicidad Digital",
                "Campañas de captación en redes sociales y Google Maps",
                "Meta Platforms / Google Ads", 380.0, "Tarjeta", "Invoice META-88412", "Pagado"
        ));
        egresosOperativos.add(new EgresoOperativo(
                "EGR-007", LocalDate.now().minusDays(1), "Gestión de Residuos Biológicos",
                "Retiro y tratamiento seguro de residuos biosanitarios",
                "BioClean Residuos Hospitalarios SAC", 320.0, "Cheque", "Factura F001-003411", "Pagado"
        ));
        egresosOperativos.add(new EgresoOperativo(
                "EGR-008", LocalDate.now(), "Planilla y Honorarios Médicos",
                "Honorarios profesionales turno noche médico veterinario",
                "Dra. Patricia Silva Ruiz", 850.0, "Yape / Plin", "Recibo Honorarios E001-44", "Pagado"
        ));

        // Inicializar datos del Módulo 7: Personal y Recursos Humanos
        inicializarModulo7RRHH();

        // Inicializar datos del Módulo 8: Inteligencia de Negocios y Reportes
        inicializarModulo8ReportesBI();

        // Inicializar datos del Módulo 9: Notificaciones, Documentos y Auditoría
        inicializarModulo9NotificacionesAuditoria();

        // Inicializar datos del Módulo 10: Configuración, Integraciones y Soporte
        inicializarModulo10ConfiguracionSoporte();
    }

    public List<Cliente> getClientes() {
        return new ArrayList<>(clientes);
    }

    public Optional<Cliente> buscarClientePorDniOApellido(String termino) {
        if (termino == null || termino.trim().isEmpty()) return Optional.empty();
        String q = termino.trim().toLowerCase();
        return clientes.stream().filter(c ->
                (c.getNumeroDocumento() != null && c.getNumeroDocumento().toLowerCase().contains(q)) ||
                (c.getApellidos() != null && c.getApellidos().toLowerCase().contains(q)) ||
                (c.getNombres() != null && c.getNombres().toLowerCase().contains(q)) ||
                (c.getNombreCompleto().toLowerCase().contains(q))
        ).findFirst();
    }

    public List<Cliente> buscarClientes(String termino) {
        if (termino == null || termino.trim().isEmpty()) return getClientes();
        String q = termino.trim().toLowerCase();
        return clientes.stream().filter(c ->
                (c.getNumeroDocumento() != null && c.getNumeroDocumento().toLowerCase().contains(q)) ||
                (c.getApellidos() != null && c.getApellidos().toLowerCase().contains(q)) ||
                (c.getNombres() != null && c.getNombres().toLowerCase().contains(q)) ||
                (c.getNombreCompleto().toLowerCase().contains(q))
        ).collect(Collectors.toList());
    }

    public Optional<Cliente> getClientePorCodigo(String codigo) {
        return clientes.stream().filter(c -> c.getCodigo().equalsIgnoreCase(codigo)).findFirst();
    }

    public Optional<Cliente> getClienteDeMascota(String codigoMascota) {
        for (Cliente c : clientes) {
            for (Mascota m : c.getMascotas()) {
                if (m.getCodigo().equalsIgnoreCase(codigoMascota)) {
                    return Optional.of(c);
                }
            }
        }
        return Optional.empty();
    }

    public Optional<Mascota> buscarMascotaPorCodigoONombre(String termino) {
        if (termino == null || termino.trim().isEmpty()) return Optional.empty();
        String q = termino.trim().toLowerCase();
        for (Cliente c : clientes) {
            for (Mascota m : c.getMascotas()) {
                if (m.getCodigo().toLowerCase().contains(q) || m.getNombre().toLowerCase().contains(q)) {
                    return Optional.of(m);
                }
            }
        }
        return Optional.empty();
    }

    public List<Mascota> todasLasMascotas() {
        List<Mascota> result = new ArrayList<>();
        for (Cliente c : clientes) {
            result.addAll(c.getMascotas());
        }
        return result;
    }

    public void guardarCliente(Cliente cliente) {
        if (cliente == null) return;
        boolean existe = false;
        for (int i = 0; i < clientes.size(); i++) {
            if (clientes.get(i).getCodigo().equalsIgnoreCase(cliente.getCodigo())) {
                clientes.set(i, cliente);
                existe = true;
                break;
            }
        }
        if (!existe) {
            if (cliente.getCodigo() == null || cliente.getCodigo().isEmpty()) {
                cliente.setCodigo("CLI-" + String.format("%03d", clientes.size() + 1));
            }
            clientes.add(cliente);
        }
    }

    public void eliminarCliente(String codigo) {
        clientes.removeIf(c -> c.getCodigo().equalsIgnoreCase(codigo));
    }

    public List<ConsultaClinica> getConsultasPorMascota(String codigoMascota) {
        return consultas.stream()
                .filter(cc -> cc.getCodigoMascota().equalsIgnoreCase(codigoMascota))
                .sorted((a, b) -> b.getFecha().compareTo(a.getFecha()))
                .collect(Collectors.toList());
    }

    public void agregarConsulta(ConsultaClinica consulta) {
        if (consulta != null) {
            consultas.add(consulta);
        }
    }

    public List<DocumentoMascota> getDocumentosPorMascota(String codigoMascota) {
        return documentos.stream()
                .filter(d -> d.getCodigoMascota().equalsIgnoreCase(codigoMascota))
                .collect(Collectors.toList());
    }

    public void agregarDocumento(DocumentoMascota documento) {
        if (documento != null) {
            documentos.add(documento);
        }
    }

    // ==========================================
    // MÉTODOS DE NEGOCIO: AGENDA Y CITAS
    // ==========================================

    public List<Cita> getCitas() {
        return new ArrayList<>(citas);
    }

    public List<Cita> getCitasPorFecha(LocalDate fecha) {
        if (fecha == null) return getCitas();
        return citas.stream()
                .filter(c -> c.getFecha().equals(fecha))
                .sorted((a, b) -> a.getHora().compareTo(b.getHora()))
                .collect(Collectors.toList());
    }

    public List<Cita> getCitasHoy() {
        return getCitasPorFecha(LocalDate.now());
    }

    public List<Cita> buscarCitas(String query) {
        if (query == null || query.trim().isEmpty()) return getCitas();
        String q = query.trim().toLowerCase();
        return citas.stream().filter(c ->
                c.getIdCita().toLowerCase().contains(q) ||
                c.getNombreMascota().toLowerCase().contains(q) ||
                c.getNombreCliente().toLowerCase().contains(q) ||
                c.getDniCliente().toLowerCase().contains(q) ||
                c.getVeterinario().toLowerCase().contains(q) ||
                c.getTipoServicio().toLowerCase().contains(q) ||
                c.getEstado().toLowerCase().contains(q)
        ).sorted((a, b) -> a.getHora().compareTo(b.getHora())).collect(Collectors.toList());
    }

    public void guardarCita(Cita cita) {
        if (cita == null) return;
        boolean existe = false;
        for (int i = 0; i < citas.size(); i++) {
            if (citas.get(i).getIdCita().equalsIgnoreCase(cita.getIdCita())) {
                citas.set(i, cita);
                existe = true;
                break;
            }
        }
        if (!existe) {
            if (cita.getIdCita() == null || cita.getIdCita().isEmpty()) {
                cita.setIdCita("CIT-" + String.format("%04d", citas.size() + 101));
            }
            citas.add(cita);
        }
    }

    public void actualizarEstadoCita(String idCita, String nuevoEstado) {
        for (Cita c : citas) {
            if (c.getIdCita().equalsIgnoreCase(idCita)) {
                c.setEstado(nuevoEstado);
                break;
            }
        }
    }

    public void eliminarCita(String idCita) {
        citas.removeIf(c -> c.getIdCita().equalsIgnoreCase(idCita));
    }

    public List<RecordatorioCita> getRecordatorios() {
        return new ArrayList<>(recordatorios);
    }

    public void agregarRecordatorio(RecordatorioCita rec) {
        if (rec != null) {
            if (rec.getIdRecordatorio() == null || rec.getIdRecordatorio().isEmpty()) {
                rec.setIdRecordatorio("REC-" + String.format("%03d", recordatorios.size() + 1));
            }
            recordatorios.add(0, rec);
        }
    }

    public void actualizarEstadoRecordatorio(String idRecordatorio, String nuevoEstado) {
        for (RecordatorioCita r : recordatorios) {
            if (r.getIdRecordatorio().equalsIgnoreCase(idRecordatorio)) {
                r.setEstado(nuevoEstado);
                break;
            }
        }
    }

    public List<PacienteTriaje> getPacientesTriaje() {
        return new ArrayList<>(pacientesTriaje);
    }

    public void agregarPacienteTriaje(PacienteTriaje p) {
        if (p != null) {
            if (p.getIdTicket() == null || p.getIdTicket().isEmpty()) {
                p.setIdTicket("TR-" + String.format("%02d", pacientesTriaje.size() + 1));
            }
            pacientesTriaje.add(p);
        }
    }

    public void actualizarEstadoTriaje(String idTicket, String nuevoEstado) {
        for (PacienteTriaje p : pacientesTriaje) {
            if (p.getIdTicket().equalsIgnoreCase(idTicket)) {
                p.setEstado(nuevoEstado);
                break;
            }
        }
    }

    public void eliminarPacienteTriaje(String idTicket) {
        pacientesTriaje.removeIf(p -> p.getIdTicket().equalsIgnoreCase(idTicket));
    }

    // ==========================================
    // MÉTODOS DEL MÓDULO 3: SERVICIOS MÉDICOS
    // ==========================================

    // 1. Consultas Médicas
    public List<AtencionMedica> getAtencionesMedicas() {
        return new ArrayList<>(atencionesMedicas);
    }

    public void guardarAtencionMedica(AtencionMedica am) {
        if (am == null) return;
        boolean existe = false;
        for (int i = 0; i < atencionesMedicas.size(); i++) {
            if (atencionesMedicas.get(i).getIdConsulta().equalsIgnoreCase(am.getIdConsulta())) {
                atencionesMedicas.set(i, am);
                existe = true;
                break;
            }
        }
        if (!existe) {
            if (am.getIdConsulta() == null || am.getIdConsulta().isEmpty()) {
                am.setIdConsulta("CM-2024-" + String.format("%03d", atencionesMedicas.size() + 1));
            }
            atencionesMedicas.add(0, am);
        }
    }

    // 2. Inmunizaciones (Vacunas y Desparasitaciones)
    public List<RegistroInmunizacion> getInmunizaciones() {
        return new ArrayList<>(inmunizaciones);
    }

    public void guardarInmunizacion(RegistroInmunizacion reg) {
        if (reg == null) return;
        boolean existe = false;
        for (int i = 0; i < inmunizaciones.size(); i++) {
            if (inmunizaciones.get(i).getIdRegistro().equalsIgnoreCase(reg.getIdRegistro())) {
                inmunizaciones.set(i, reg);
                existe = true;
                break;
            }
        }
        if (!existe) {
            if (reg.getIdRegistro() == null || reg.getIdRegistro().isEmpty()) {
                String prefix = reg.getTipoControl().startsWith("Vacun") ? "VAC-" : "DES-";
                reg.setIdRegistro(prefix + "2024-" + String.format("%03d", inmunizaciones.size() + 50));
            }
            inmunizaciones.add(0, reg);
        }
    }

    // 3. Cirugías y Quirófano
    public List<RegistroCirugia> getCirugias() {
        return new ArrayList<>(cirugias);
    }

    public void guardarCirugia(RegistroCirugia cirugia) {
        if (cirugia == null) return;
        boolean existe = false;
        for (int i = 0; i < cirugias.size(); i++) {
            if (cirugias.get(i).getIdCirugia().equalsIgnoreCase(cirugia.getIdCirugia())) {
                cirugias.set(i, cirugia);
                existe = true;
                break;
            }
        }
        if (!existe) {
            if (cirugia.getIdCirugia() == null || cirugia.getIdCirugia().isEmpty()) {
                cirugia.setIdCirugia("QX-2024-" + String.format("%02d", cirugias.size() + 1));
            }
            cirugias.add(0, cirugia);
        }
    }

    public void actualizarEstadoCirugia(String idCirugia, String nuevoEstado) {
        for (RegistroCirugia c : cirugias) {
            if (c.getIdCirugia().equalsIgnoreCase(idCirugia)) {
                c.setEstado(nuevoEstado);
                break;
            }
        }
    }

    // 4. Laboratorio e Imágenes
    public List<OrdenLaboratorio> getOrdenesLaboratorio() {
        return new ArrayList<>(ordenesLaboratorio);
    }

    public void guardarOrdenLaboratorio(OrdenLaboratorio orden) {
        if (orden == null) return;
        boolean existe = false;
        for (int i = 0; i < ordenesLaboratorio.size(); i++) {
            if (ordenesLaboratorio.get(i).getIdOrden().equalsIgnoreCase(orden.getIdOrden())) {
                ordenesLaboratorio.set(i, orden);
                existe = true;
                break;
            }
        }
        if (!existe) {
            if (orden.getIdOrden() == null || orden.getIdOrden().isEmpty()) {
                String prefix = orden.getCategoria().contains("Imágenes") ? "IMG-" : "LAB-";
                orden.setIdOrden(prefix + "2024-" + String.format("%02d", ordenesLaboratorio.size() + 1));
            }
            ordenesLaboratorio.add(0, orden);
        }
    }

    public void actualizarEstadoOrden(String idOrden, String nuevoEstado) {
        for (OrdenLaboratorio o : ordenesLaboratorio) {
            if (o.getIdOrden().equalsIgnoreCase(idOrden)) {
                o.setEstado(nuevoEstado);
                break;
            }
        }
    }

    // ==========================================
    // MÉTODOS DEL MÓDULO 4: ESTÉTICA Y HOSPEDAJE
    // ==========================================

    // 1. Grooming y Peluquería
    public List<happypets.model.ServicioGrooming> getServiciosGrooming() {
        return new ArrayList<>(serviciosGrooming);
    }

    public void guardarServicioGrooming(happypets.model.ServicioGrooming g) {
        if (g == null) return;
        boolean existe = false;
        for (int i = 0; i < serviciosGrooming.size(); i++) {
            if (serviciosGrooming.get(i).getIdGrooming().equalsIgnoreCase(g.getIdGrooming())) {
                serviciosGrooming.set(i, g);
                existe = true;
                break;
            }
        }
        if (!existe) {
            if (g.getIdGrooming() == null || g.getIdGrooming().isEmpty()) {
                g.setIdGrooming("GR-2024-" + String.format("%02d", serviciosGrooming.size() + 1));
            }
            serviciosGrooming.add(0, g);
        }
    }

    public void actualizarEstadoGrooming(String idGrooming, String nuevoEstado) {
        for (happypets.model.ServicioGrooming g : serviciosGrooming) {
            if (g.getIdGrooming().equalsIgnoreCase(idGrooming)) {
                g.setEstado(nuevoEstado);
                break;
            }
        }
    }

    // 2. Hospitalización
    public List<happypets.model.InternamientoHospitalario> getInternamientos() {
        return new ArrayList<>(internamientos);
    }

    public void guardarInternamiento(happypets.model.InternamientoHospitalario h) {
        if (h == null) return;
        boolean existe = false;
        for (int i = 0; i < internamientos.size(); i++) {
            if (internamientos.get(i).getIdInternamiento().equalsIgnoreCase(h.getIdInternamiento())) {
                internamientos.set(i, h);
                existe = true;
                break;
            }
        }
        if (!existe) {
            if (h.getIdInternamiento() == null || h.getIdInternamiento().isEmpty()) {
                h.setIdInternamiento("HOSP-2024-" + String.format("%02d", internamientos.size() + 1));
            }
            internamientos.add(0, h);
        }
    }

    public void actualizarEstadoInternamiento(String idInternamiento, String nuevoEstado) {
        for (happypets.model.InternamientoHospitalario h : internamientos) {
            if (h.getIdInternamiento().equalsIgnoreCase(idInternamiento)) {
                h.setEstado(nuevoEstado);
                break;
            }
        }
    }

    // 3. Hotel y Guardería
    public List<happypets.model.ReservaHospedaje> getReservasHospedaje() {
        return new ArrayList<>(reservasHospedaje);
    }

    public void guardarReservaHospedaje(happypets.model.ReservaHospedaje r) {
        if (r == null) return;
        boolean existe = false;
        for (int i = 0; i < reservasHospedaje.size(); i++) {
            if (reservasHospedaje.get(i).getIdReserva().equalsIgnoreCase(r.getIdReserva())) {
                reservasHospedaje.set(i, r);
                existe = true;
                break;
            }
        }
        if (!existe) {
            if (r.getIdReserva() == null || r.getIdReserva().isEmpty()) {
                r.setIdReserva("HOT-2024-" + String.format("%02d", reservasHospedaje.size() + 1));
            }
            reservasHospedaje.add(0, r);
        }
    }

    public void actualizarEstadoHospedaje(String idReserva, String nuevoEstado) {
        for (happypets.model.ReservaHospedaje r : reservasHospedaje) {
            if (r.getIdReserva().equalsIgnoreCase(idReserva)) {
                r.setEstado(nuevoEstado);
                break;
            }
        }
    }

    // 4. Adopciones y Rescates
    public List<happypets.model.MascotaAdopcion> getMascotasAdopcion() {
        return new ArrayList<>(mascotasAdopcion);
    }

    public void guardarMascotaAdopcion(happypets.model.MascotaAdopcion ma) {
        if (ma == null) return;
        boolean existe = false;
        for (int i = 0; i < mascotasAdopcion.size(); i++) {
            if (mascotasAdopcion.get(i).getIdMascotaAdopcion().equalsIgnoreCase(ma.getIdMascotaAdopcion())) {
                mascotasAdopcion.set(i, ma);
                existe = true;
                break;
            }
        }
        if (!existe) {
            if (ma.getIdMascotaAdopcion() == null || ma.getIdMascotaAdopcion().isEmpty()) {
                ma.setIdMascotaAdopcion("ADOP-" + String.format("%02d", mascotasAdopcion.size() + 1));
            }
            mascotasAdopcion.add(0, ma);
        }
    }

    public void actualizarEstadoAdopcion(String idMascotaAdopcion, String nuevoEstado, String adoptante, String dni, String tel, String dir) {
        for (happypets.model.MascotaAdopcion ma : mascotasAdopcion) {
            if (ma.getIdMascotaAdopcion().equalsIgnoreCase(idMascotaAdopcion)) {
                ma.setEstado(nuevoEstado);
                if (adoptante != null) ma.setAdoptanteNombre(adoptante);
                if (dni != null) ma.setAdoptanteDni(dni);
                if (tel != null) ma.setAdoptanteTelefono(tel);
                if (dir != null) ma.setAdoptanteDireccion(dir);
                break;
            }
        }
    }

    // ==========================================
    // MÉTODOS DEL MÓDULO 5: INVENTARIO Y FARMACIA
    // ==========================================

    // 1. Catálogo de Productos y Fármacos
    public List<happypets.model.ProductoFarmacia> getProductosFarmacia() {
        return new ArrayList<>(productosFarmacia);
    }

    public void guardarProductoFarmacia(happypets.model.ProductoFarmacia p) {
        if (p == null) return;
        boolean existe = false;
        for (int i = 0; i < productosFarmacia.size(); i++) {
            if (productosFarmacia.get(i).getCodigo().equalsIgnoreCase(p.getCodigo())) {
                productosFarmacia.set(i, p);
                existe = true;
                break;
            }
        }
        if (!existe) {
            if (p.getCodigo() == null || p.getCodigo().isEmpty()) {
                p.setCodigo("PROD-" + String.format("%03d", productosFarmacia.size() + 1));
            }
            productosFarmacia.add(0, p);
        }
    }

    public void eliminarProductoFarmacia(String codigo) {
        productosFarmacia.removeIf(p -> p.getCodigo().equalsIgnoreCase(codigo));
    }

    public void actualizarStockProducto(String codigo, int deltaCantidad) {
        for (happypets.model.ProductoFarmacia p : productosFarmacia) {
            if (p.getCodigo().equalsIgnoreCase(codigo)) {
                int nuevo = p.getStockActual() + deltaCantidad;
                if (nuevo < 0) nuevo = 0;
                p.setStockActual(nuevo);
                break;
            }
        }
    }

    // 2. Control de Stock y Lotes (Movimientos)
    public List<happypets.model.LoteMovimientoStock> getMovimientosStock() {
        return new ArrayList<>(movimientosStock);
    }

    public void registrarMovimientoStock(happypets.model.LoteMovimientoStock m) {
        if (m == null) return;
        if (m.getIdMovimiento() == null || m.getIdMovimiento().isEmpty()) {
            m.setIdMovimiento("MOV-2024-" + String.format("%02d", movimientosStock.size() + 1));
        }
        movimientosStock.add(0, m);

        // Actualizar stock del producto vinculado
        int delta = m.getTipoMovimiento().toLowerCase().contains("ingreso") ? m.getCantidad() : -m.getCantidad();
        actualizarStockProducto(m.getCodigoProducto(), delta);
    }

    // 3. Proveedores y Órdenes de Compra
    public List<happypets.model.ProveedorFarmacia> getProveedoresFarmacia() {
        return new ArrayList<>(proveedoresFarmacia);
    }

    public void guardarProveedorFarmacia(happypets.model.ProveedorFarmacia pr) {
        if (pr == null) return;
        boolean existe = false;
        for (int i = 0; i < proveedoresFarmacia.size(); i++) {
            if (proveedoresFarmacia.get(i).getRuc().equalsIgnoreCase(pr.getRuc())) {
                proveedoresFarmacia.set(i, pr);
                existe = true;
                break;
            }
        }
        if (!existe) {
            proveedoresFarmacia.add(0, pr);
        }
    }

    public void eliminarProveedorFarmacia(String ruc) {
        proveedoresFarmacia.removeIf(pr -> pr.getRuc().equalsIgnoreCase(ruc));
    }

    public List<happypets.model.OrdenCompra> getOrdenesCompra() {
        return new ArrayList<>(ordenesCompra);
    }

    public void guardarOrdenCompra(happypets.model.OrdenCompra oc) {
        if (oc == null) return;
        boolean existe = false;
        for (int i = 0; i < ordenesCompra.size(); i++) {
            if (ordenesCompra.get(i).getIdOrden().equalsIgnoreCase(oc.getIdOrden())) {
                ordenesCompra.set(i, oc);
                existe = true;
                break;
            }
        }
        if (!existe) {
            if (oc.getIdOrden() == null || oc.getIdOrden().isEmpty()) {
                oc.setIdOrden("OC-2024-" + String.format("%02d", ordenesCompra.size() + 1));
            }
            ordenesCompra.add(0, oc);
        }
    }

    public void actualizarEstadoOrdenCompra(String idOrden, String nuevoEstado) {
        for (happypets.model.OrdenCompra oc : ordenesCompra) {
            if (oc.getIdOrden().equalsIgnoreCase(idOrden)) {
                oc.setEstado(nuevoEstado);
                break;
            }
        }
    }

    // 4. Ajustes y Mermas
    public List<happypets.model.AjusteMerma> getAjustesMermas() {
        return new ArrayList<>(ajustesMermas);
    }

    public void guardarAjusteMerma(happypets.model.AjusteMerma a) {
        if (a == null) return;
        if (a.getIdAjuste() == null || a.getIdAjuste().isEmpty()) {
            a.setIdAjuste("AJU-2024-" + String.format("%02d", ajustesMermas.size() + 1));
        }
        ajustesMermas.add(0, a);

        // Descontar o regularizar stock físico
        int delta = a.getTipo().contains("Positivo") ? a.getCantidad() : -a.getCantidad();
        actualizarStockProducto(a.getCodigoProducto(), delta);
    }

    // ==========================================
    // MÓDULO 6: FINANZAS Y VENTAS
    // ==========================================

    // 1. Punto de Venta (POS)
    public List<VentaPOS> getVentasPOS() {
        return new ArrayList<>(ventasPOS);
    }

    public void guardarVentaPOS(VentaPOS venta) {
        if (venta == null) return;
        if (venta.getIdVenta() == null || venta.getIdVenta().isEmpty()) {
            venta.setIdVenta("VTA-2024-" + String.format("%03d", ventasPOS.size() + 1));
        }
        if (venta.getNumeroComprobante() == null || venta.getNumeroComprobante().isEmpty()) {
            String pref = "Boleta Electrónica".equals(venta.getTipoComprobante()) ? "B001-" :
                          "Factura Electrónica".equals(venta.getTipoComprobante()) ? "F001-" : "TK-";
            venta.setNumeroComprobante(pref + String.format("%06d", ventasPOS.size() + 414));
        }
        ventasPOS.add(0, venta);

        // Descontar stock para productos físicos dispensados si la venta no es solo cotización
        if (!"Cotización".equalsIgnoreCase(venta.getEstado())) {
            for (ItemVentaPOS item : venta.getItems()) {
                actualizarStockProducto(item.getCodigo(), -item.getCantidad());
            }
        }
    }

    public void anularVentaPOS(String idVenta) {
        for (VentaPOS v : ventasPOS) {
            if (v.getIdVenta().equalsIgnoreCase(idVenta)) {
                v.setEstado("Anulada");
                // Reintegrar stock si no era cotización
                for (ItemVentaPOS item : v.getItems()) {
                    actualizarStockProducto(item.getCodigo(), item.getCantidad());
                }
                break;
            }
        }
    }

    // 2. Cuentas por Cobrar
    public List<CuentaPorCobrar> getCuentasPorCobrar() {
        return new ArrayList<>(cuentasPorCobrar);
    }

    public void guardarCuentaPorCobrar(CuentaPorCobrar c) {
        if (c == null) return;
        boolean existe = false;
        for (int i = 0; i < cuentasPorCobrar.size(); i++) {
            if (cuentasPorCobrar.get(i).getIdCuenta().equalsIgnoreCase(c.getIdCuenta())) {
                cuentasPorCobrar.set(i, c);
                existe = true;
                break;
            }
        }
        if (!existe) {
            if (c.getIdCuenta() == null || c.getIdCuenta().isEmpty()) {
                c.setIdCuenta("CXC-" + String.format("%03d", cuentasPorCobrar.size() + 1));
            }
            cuentasPorCobrar.add(0, c);
        }
    }

    public void eliminarCuentaPorCobrar(String idCuenta) {
        cuentasPorCobrar.removeIf(c -> c.getIdCuenta().equalsIgnoreCase(idCuenta));
    }

    public void registrarAbonoCuentaPorCobrar(String idCuenta, double abono) {
        for (CuentaPorCobrar c : cuentasPorCobrar) {
            if (c.getIdCuenta().equalsIgnoreCase(idCuenta)) {
                c.registrarAbono(abono);
                break;
            }
        }
    }

    // 3. Cuentas por Pagar
    public List<CuentaPorPagar> getCuentasPorPagar() {
        return new ArrayList<>(cuentasPorPagar);
    }

    public void guardarCuentaPorPagar(CuentaPorPagar c) {
        if (c == null) return;
        boolean existe = false;
        for (int i = 0; i < cuentasPorPagar.size(); i++) {
            if (cuentasPorPagar.get(i).getIdCuenta().equalsIgnoreCase(c.getIdCuenta())) {
                cuentasPorPagar.set(i, c);
                existe = true;
                break;
            }
        }
        if (!existe) {
            if (c.getIdCuenta() == null || c.getIdCuenta().isEmpty()) {
                c.setIdCuenta("CXP-" + String.format("%03d", cuentasPorPagar.size() + 1));
            }
            cuentasPorPagar.add(0, c);
        }
    }

    public void eliminarCuentaPorPagar(String idCuenta) {
        cuentasPorPagar.removeIf(c -> c.getIdCuenta().equalsIgnoreCase(idCuenta));
    }

    public void registrarPagoCuentaPorPagar(String idCuenta, double pago) {
        for (CuentaPorPagar c : cuentasPorPagar) {
            if (c.getIdCuenta().equalsIgnoreCase(idCuenta)) {
                c.registrarPago(pago);
                break;
            }
        }
    }

    // 4. Control de Caja Chica
    public List<MovimientoCajaChica> getMovimientosCajaChica() {
        return new ArrayList<>(movimientosCajaChica);
    }

    public void guardarMovimientoCajaChica(MovimientoCajaChica m) {
        if (m == null) return;
        boolean existe = false;
        for (int i = 0; i < movimientosCajaChica.size(); i++) {
            if (movimientosCajaChica.get(i).getIdMovimiento().equalsIgnoreCase(m.getIdMovimiento())) {
                movimientosCajaChica.set(i, m);
                existe = true;
                break;
            }
        }
        if (!existe) {
            if (m.getIdMovimiento() == null || m.getIdMovimiento().isEmpty()) {
                m.setIdMovimiento("CCH-" + String.format("%03d", movimientosCajaChica.size() + 1));
            }
            double saldoActual = getSaldoActualCajaChica();
            if (m.esIngreso()) {
                m.setSaldoResultante(saldoActual + m.getMonto());
            } else {
                m.setSaldoResultante(saldoActual - m.getMonto());
            }
            movimientosCajaChica.add(m);
        }
    }

    public void eliminarMovimientoCajaChica(String idMovimiento) {
        movimientosCajaChica.removeIf(m -> m.getIdMovimiento().equalsIgnoreCase(idMovimiento));
    }

    public double getSaldoActualCajaChica() {
        double saldo = 0.0;
        for (MovimientoCajaChica m : movimientosCajaChica) {
            if (m.esIngreso()) {
                saldo += m.getMonto();
            } else {
                saldo -= m.getMonto();
            }
        }
        return Math.max(0.0, saldo);
    }

    public double getTotalIngresosCajaChica() {
        double total = 0.0;
        for (MovimientoCajaChica m : movimientosCajaChica) {
            if (m.esIngreso()) {
                total += m.getMonto();
            }
        }
        return total;
    }

    public double getTotalEgresosCajaChica() {
        double total = 0.0;
        for (MovimientoCajaChica m : movimientosCajaChica) {
            if (m.esEgreso()) {
                total += m.getMonto();
            }
        }
        return total;
    }

    // 5. Control de Egresos Operativos
    public List<EgresoOperativo> getEgresosOperativos() {
        return new ArrayList<>(egresosOperativos);
    }

    public void guardarEgresoOperativo(EgresoOperativo e) {
        if (e == null) return;
        boolean existe = false;
        for (int i = 0; i < egresosOperativos.size(); i++) {
            if (egresosOperativos.get(i).getIdEgreso().equalsIgnoreCase(e.getIdEgreso())) {
                egresosOperativos.set(i, e);
                existe = true;
                break;
            }
        }
        if (!existe) {
            if (e.getIdEgreso() == null || e.getIdEgreso().isEmpty()) {
                e.setIdEgreso("EGR-" + String.format("%03d", egresosOperativos.size() + 1));
            }
            egresosOperativos.add(0, e);
        }
    }

    public void eliminarEgresoOperativo(String idEgreso) {
        egresosOperativos.removeIf(e -> e.getIdEgreso().equalsIgnoreCase(idEgreso));
    }

    public double getTotalEgresosOperativos() {
        double t = 0.0;
        for (EgresoOperativo e : egresosOperativos) {
            t += e.getMonto();
        }
        return t;
    }

    // =========================================================================
    // MÓDULO 7: PERSONAL Y RECURSOS HUMANOS (Loli Espinoza, Víctor Manuel)
    // =========================================================================

    private void inicializarModulo7RRHH() {
        // 1. Veterinarios Especialistas Clínicos
        veterinarios.add(new Veterinario(
                "VET-01", "Dra. Laura Morales Ruiz", "Col. N° 28/4512",
                "Cirugía General y Tejidos Blandos", 12,
                "Especialista en cirugía mínimamente invasiva, laparoscopia y traumatología canina y felina. Más de 12 años liderando equipos quirúrgicos.",
                "Lun · Mié · Vie", "08:00 - 15:00", "Hoy, 16:30 hrs",
                "En consulta", "+51 984 112 334", "laura.morales@happypets.pe",
                4.9, 520, "Quirófano A / Cons. 1"
        ));
        veterinarios.add(new Veterinario(
                "VET-02", "Dr. Mario Silva Paredes", "Col. N° 24/8910",
                "Dermatología y Alergias", 9,
                "Experto en citología cutánea, atopias severas, otitis crónica y tratamientos biológicos para mascotas alérgicas.",
                "Mar · Jue · Sáb", "08:00 - 15:00", "Mañana, 09:30 hrs",
                "Disponible hoy", "+51 991 445 667", "mario.silva@happypets.pe",
                4.8, 380, "Consultorio 2"
        ));
        veterinarios.add(new Veterinario(
                "VET-03", "Dra. Carmen Vega Hurtado", "Col. N° 31/1245",
                "Animales Exóticos y Aves", 7,
                "Atención especializada de conejos, hurones, aves psitácidas y reptiles. Manejo etológico y medicina preventiva no convencional.",
                "Lun · Mar · Jue", "15:00 - 22:00", "Hoy, 18:00 hrs",
                "Turno Tarde", "+51 977 882 119", "carmen.vega@happypets.pe",
                4.9, 290, "Consultorio 3"
        ));
        veterinarios.add(new Veterinario(
                "VET-04", "Dr. Roberto Mendoza Chávez", "Col. N° 19/6721",
                "Cardiología y Ecocardiografía", 14,
                "Diplomado en ecocardiografía Doppler color. Diagnóstico de soplos, cardiopatías congénitas y monitorización prequirúrgica.",
                "Mié · Vie · Sáb", "08:00 - 15:00", "Viernes, 11:00 hrs",
                "Disponible hoy", "+51 942 331 889", "roberto.mendoza@happypets.pe",
                5.0, 610, "Consultorio 4"
        ));
        veterinarios.add(new Veterinario(
                "VET-05", "Dra. Elena Ruiz Salazar", "Col. N° 29/3310",
                "Medicina Felina y Comportamiento", 8,
                "Certificación Cat Friendly Practice. Especialista en nefrología felina, estrés ambiental y geriatría en gatos.",
                "Lun · Mié · Jue", "08:00 - 15:00", "Hoy, 17:00 hrs",
                "En cirugía", "+51 965 221 443", "elena.ruiz@happypets.pe",
                4.9, 440, "Consultorio Felino CatFriendly"
        ));
        veterinarios.add(new Veterinario(
                "VET-06", "Dr. Fernando Ortiz Bravo", "Col. N° 22/5567",
                "Oftalmología Veterinaria", 11,
                "Microcirugía corneal, corrección de úlceras complejas, catarata, glaucoma y retinopatías avanzadas.",
                "Mar · Vie", "15:00 - 22:00", "Sábado, 10:00 hrs",
                "Turno Tarde", "+51 955 776 221", "fernando.ortiz@happypets.pe",
                4.7, 340, "Consultorio Oftalmológico"
        ));
        veterinarios.add(new Veterinario(
                "VET-07", "Dr. Carlos Méndez Soto", "Col. N° 30/7891",
                "Urgencias y Cuidados Críticos", 6,
                "Médico de guardia intensivista y emergencias 24h. Manejo de politraumatismos, toxicología y soporte vital avanzado.",
                "Jue · Vie · Sáb · Dom", "22:00 - 08:00", "Hoy, 22:00 hrs",
                "Guardia nocturna", "+51 988 334 112", "carlos.mendez@happypets.pe",
                4.8, 310, "Área Shock y Urgencias 24h"
        ));
        veterinarios.add(new Veterinario(
                "VET-08", "Dra. Sofía Valdivia León", "Col. N° 33/4421",
                "Oncología y Quimioterapia", 10,
                "Protocolos de quimioterapia metronómica, biopsias tumorales guiadas y cuidados paliativos oncológicos.",
                "Lun · Mié · Vie", "15:00 - 22:00", "Lunes, 15:30 hrs",
                "Disponible hoy", "+51 971 665 990", "sofia.valdivia@happypets.pe",
                4.9, 275, "Consultorio 5"
        ));

        // 2. Personal de Apoyo (Total 24 en equipo / 10 principales representados)
        personalApoyo.add(new PersonalApoyo(
                "APO-01", "Marta Sánchez Gómez", "Auxiliar Técnico Veterinario (ATV)", "Auxiliares",
                "Hospitalización y Quirófano B", "Ext. 204", "Mañana (07:00 - 15:00)",
                "En turno", "+51 981 223 114", "marta.sanchez@happypets.pe",
                "RCP Veterinario, Soporte Anestésico Avanzado"
        ));
        personalApoyo.add(new PersonalApoyo(
                "APO-02", "Carlos Vega Ruiz", "Coordinador de Recepción y Triaje", "Recepción",
                "Mostrador Principal / Caja", "Ext. 101", "Mañana (07:30 - 16:00)",
                "En turno", "+51 992 441 552", "carlos.vega@happypets.pe",
                "Atención al Cliente PetCare, Facturación Electrónica"
        ));
        personalApoyo.add(new PersonalApoyo(
                "APO-03", "Lucía Domínguez Peña", "Estilista Canina y Felina Senior", "Peluquería",
                "Grooming Spa Canino", "Ext. 305", "Mañana (08:30 - 17:30)",
                "En turno", "+51 973 881 220", "lucia.dominguez@happypets.pe",
                "Cortes Asiáticos, Stripping y Baños Medicados"
        ));
        personalApoyo.add(new PersonalApoyo(
                "APO-04", "Antonio Delgado Flores", "Supervisor de Mantenimiento e Higiene", "Mantenimiento",
                "Esterilización y Desinfección", "Ext. 401", "Mañana (06:30 - 15:00)",
                "En turno", "+51 964 112 778", "antonio.delgado@happypets.pe",
                "Bioseguridad Grado Hospitalario y Gestión Residuos"
        ));
        personalApoyo.add(new PersonalApoyo(
                "APO-05", "Valeria Quispe Morales", "Auxiliar Técnico Veterinario (ATV)", "Auxiliares",
                "Sala de Triaje y Vacunatorio", "Ext. 206", "Tarde (14:30 - 22:00)",
                "Turno Tarde", "+51 955 667 334", "valeria.quispe@happypets.pe",
                "Toma de Muestras y Canalización Venosa"
        ));
        personalApoyo.add(new PersonalApoyo(
                "APO-06", "Diego Farfán Tello", "Recepcionista y Agendamiento Call Center", "Recepción",
                "Atención Telefónica y WhatsApp", "Ext. 102", "Tarde (14:00 - 22:00)",
                "Turno Tarde", "+51 988 554 991", "diego.farfan@happypets.pe",
                "Resolución de Quejas y Manejo de Agenda Médica"
        ));
        personalApoyo.add(new PersonalApoyo(
                "APO-07", "Patricia Alarcón Salazar", "Asistente Contable y RRHH", "Administración",
                "Oficina de Administración", "Ext. 502", "Mañana (08:00 - 17:00)",
                "En turno", "+51 941 229 883", "patricia.alarcon@happypets.pe",
                "Gestión de Planillas, Tesorería y RRHH"
        ));
        personalApoyo.add(new PersonalApoyo(
                "APO-08", "Jorge Benavides Ramos", "Auxiliar de Grooming y Baños", "Peluquería",
                "Área de Baños y Secado", "Ext. 306", "Mañana (09:00 - 18:00)",
                "En turno", "+51 972 331 449", "jorge.benavides@happypets.pe",
                "Dermatocosmética e Higiene Spa"
        ));
        personalApoyo.add(new PersonalApoyo(
                "APO-09", "Marcos Céspedes Ramos", "Técnico de Laboratorio e Imágenes", "Auxiliares",
                "Laboratorio Clínico y Rayos X", "Ext. 208", "Mañana (08:00 - 16:30)",
                "En turno", "+51 983 445 119", "marcos.cespedes@happypets.pe",
                "Hemogramas, Bioquímica y Radiología Digital"
        ));
        personalApoyo.add(new PersonalApoyo(
                "APO-10", "Rosa Villegas Díaz", "Auxiliar Nocturna de Hospitalizados", "Auxiliares",
                "Área UCI y Hospitalización 24h", "Ext. 205", "Noche (21:30 - 07:30)",
                "Descanso", "+51 991 776 220", "rosa.villegas@happypets.pe",
                "Monitoreo Vital Nocturno y Fluidoterapia"
        ));

        // 3. Cuadrante Semanal de Guardias y Turnos Médicos (Lun a Dom)
        cuadranteTurnos.add(new TurnoSemanal(
                "TUR-01", "VET-01", "Dra. Laura Morales Ruiz", "Col. N° 28/4512",
                "Cirugía General", "Quirófano A / Cons. 1",
                new String[]{
                        "08:00 - 15:00 · Quirófano A", "Descanso", "08:00 - 15:00 · Cons. 1",
                        "Descanso", "08:00 - 15:00 · Quirófano A", "08:00 - 14:00 · Cons. 1", "Descanso"
                },
                new String[]{"Mañana", "Descanso", "Mañana", "Descanso", "Mañana", "Mañana", "Descanso"}
        ));
        cuadranteTurnos.add(new TurnoSemanal(
                "TUR-02", "VET-02", "Dr. Mario Silva Paredes", "Col. N° 24/8910",
                "Dermatología", "Consultorio 2",
                new String[]{
                        "Descanso", "08:00 - 15:00 · Cons. 2", "Descanso",
                        "08:00 - 15:00 · Cons. 2", "Descanso", "08:00 - 15:00 · Cons. 2", "Descanso"
                },
                new String[]{"Descanso", "Mañana", "Descanso", "Mañana", "Descanso", "Mañana", "Descanso"}
        ));
        cuadranteTurnos.add(new TurnoSemanal(
                "TUR-03", "VET-03", "Dra. Carmen Vega Hurtado", "Col. N° 31/1245",
                "Animales Exóticos", "Consultorio 3",
                new String[]{
                        "15:00 - 22:00 · Cons. 3", "15:00 - 22:00 · Cons. 3", "Descanso",
                        "15:00 - 22:00 · Cons. 3", "Descanso", "14:00 - 20:00 · Cons. 3", "Descanso"
                },
                new String[]{"Tarde", "Tarde", "Descanso", "Tarde", "Descanso", "Tarde", "Descanso"}
        ));
        cuadranteTurnos.add(new TurnoSemanal(
                "TUR-04", "VET-04", "Dr. Roberto Mendoza Chávez", "Col. N° 19/6721",
                "Cardiología", "Consultorio 4",
                new String[]{
                        "Descanso", "Descanso", "08:00 - 15:00 · Cons. 4",
                        "Descanso", "08:00 - 15:00 · Cons. 4", "08:00 - 15:00 · Cons. 4", "Descanso"
                },
                new String[]{"Descanso", "Descanso", "Mañana", "Descanso", "Mañana", "Mañana", "Descanso"}
        ));
        cuadranteTurnos.add(new TurnoSemanal(
                "TUR-05", "VET-05", "Dra. Elena Ruiz Salazar", "Col. N° 29/3310",
                "Medicina Felina", "Consultorio Felino",
                new String[]{
                        "08:00 - 15:00 · Cons. Felino", "Descanso", "08:00 - 15:00 · Cons. Felino",
                        "08:00 - 15:00 · Quirófano B", "Descanso", "09:00 - 14:00 · Cons. Felino", "Descanso"
                },
                new String[]{"Mañana", "Descanso", "Mañana", "Mañana", "Descanso", "Mañana", "Descanso"}
        ));
        cuadranteTurnos.add(new TurnoSemanal(
                "TUR-06", "VET-06", "Dr. Fernando Ortiz Bravo", "Col. N° 22/5567",
                "Oftalmología", "Cons. Oftalmológico",
                new String[]{
                        "Descanso", "15:00 - 22:00 · Oftalmología", "Descanso",
                        "Descanso", "15:00 - 22:00 · Oftalmología", "10:00 - 16:00 · Oftalmología", "Descanso"
                },
                new String[]{"Descanso", "Tarde", "Descanso", "Descanso", "Tarde", "Tarde", "Descanso"}
        ));
        cuadranteTurnos.add(new TurnoSemanal(
                "TUR-07", "VET-07", "Dr. Carlos Méndez Soto", "Col. N° 30/7891",
                "Urgencias 24h", "Shock & UCI",
                new String[]{
                        "Descanso", "Descanso", "Descanso",
                        "22:00 - 08:00 · Urgencias 24h", "22:00 - 08:00 · Urgencias 24h",
                        "22:00 - 08:00 · Urgencias 24h", "22:00 - 08:00 · Urgencias 24h"
                },
                new String[]{"Descanso", "Descanso", "Descanso", "Guardia", "Guardia", "Guardia", "Guardia"}
        ));
        cuadranteTurnos.add(new TurnoSemanal(
                "TUR-08", "VET-08", "Dra. Sofía Valdivia León", "Col. N° 33/4421",
                "Oncología", "Consultorio 5",
                new String[]{
                        "15:00 - 22:00 · Cons. 5", "Descanso", "15:00 - 22:00 · Cons. 5",
                        "Descanso", "15:00 - 22:00 · Cons. 5", "Descanso", "Descanso"
                },
                new String[]{"Tarde", "Descanso", "Tarde", "Descanso", "Tarde", "Descanso", "Descanso"}
        ));

        // 4. Asistencias del Día de Hoy (24 colaboradores registrados según Wireframe)
        LocalDate hoy = LocalDate.now();
        asistencias.add(new RegistroAsistencia(
                "ASI-01", "VET-05", "Dra. Elena Ruiz Salazar", "Medicina Felina", "Veterinario",
                hoy, "Mañana (08:00 - 15:00)", "08:00", "15:00",
                "07:55", "--:--", "Presente", 0, "Puntual · En consulta felina", false
        ));
        asistencias.add(new RegistroAsistencia(
                "ASI-02", "VET-02", "Dr. Mario Silva Paredes", "Dermatología", "Veterinario",
                hoy, "Mañana (08:00 - 15:00)", "08:00", "15:00",
                "08:14", "--:--", "Retardo", 14, "Retardo +14 min (Tráfico Av. Javier Prado)", false
        ));
        asistencias.add(new RegistroAsistencia(
                "ASI-03", "APO-01", "Marta Sánchez Gómez", "Auxiliar ATV", "Apoyo",
                hoy, "Mañana (07:00 - 15:00)", "07:00", "15:00",
                "06:58", "15:02", "Presente", 0, "Puntual · Turno completado", false
        ));
        asistencias.add(new RegistroAsistencia(
                "ASI-04", "APO-02", "Carlos Vega Ruiz", "Recepción / Triaje", "Apoyo",
                hoy, "Mañana (07:30 - 16:00)", "07:30", "16:00",
                "07:28", "--:--", "Presente", 0, "Puntual · Mostrador activo", false
        ));
        asistencias.add(new RegistroAsistencia(
                "ASI-05", "APO-03", "Lucía Domínguez Peña", "Peluquería / Grooming", "Apoyo",
                hoy, "Mañana (08:30 - 17:30)", "08:30", "17:30",
                "08:42", "--:--", "Retardo", 12, "Retardo +12 min", false
        ));
        asistencias.add(new RegistroAsistencia(
                "ASI-06", "VET-04", "Dr. Roberto Mendoza Chávez", "Cardiología", "Veterinario",
                hoy, "Mañana (08:00 - 15:00)", "08:00", "15:00",
                "07:50", "--:--", "Presente", 0, "Puntual · En ecocardiografías", false
        ));
        asistencias.add(new RegistroAsistencia(
                "ASI-07", "VET-01", "Dra. Laura Morales Ruiz", "Cirugía General", "Veterinario",
                hoy, "Mañana (08:00 - 15:00)", "08:00", "15:00",
                "08:00", "--:--", "Presente", 0, "Puntual · En quirófano central", false
        ));
        asistencias.add(new RegistroAsistencia(
                "ASI-08", "APO-04", "Antonio Delgado Flores", "Mantenimiento e Higiene", "Apoyo",
                hoy, "Mañana (06:30 - 15:00)", "06:30", "15:00",
                "06:25", "15:05", "Presente", 0, "Puntual · Esterilización lista", false
        ));
        asistencias.add(new RegistroAsistencia(
                "ASI-09", "VET-06", "Dra. Silvia Peña Mendoza", "Oftalmología", "Veterinario",
                hoy, "Mañana (08:00 - 15:00)", "08:00", "15:00",
                "--:--", "--:--", "Permiso", 0, "Permiso médico P-184 en trámite", false
        ));
        asistencias.add(new RegistroAsistencia(
                "ASI-10", "APO-08", "Jorge Benavides Ramos", "Peluquería", "Apoyo",
                hoy, "Mañana (09:00 - 18:00)", "09:00", "18:00",
                "--:--", "--:--", "Ausente", 0, "Sin marcación registrada", false
        ));
        asistencias.add(new RegistroAsistencia(
                "ASI-11", "APO-09", "Marcos Céspedes Ramos", "Técnico Laboratorio", "Apoyo",
                hoy, "Mañana (08:00 - 16:30)", "08:00", "16:30",
                "07:54", "--:--", "Presente", 0, "Puntual · Procesando hemogramas", false
        ));
        asistencias.add(new RegistroAsistencia(
                "ASI-12", "APO-07", "Patricia Alarcón Salazar", "Administración / RRHH", "Apoyo",
                hoy, "Mañana (08:00 - 17:00)", "08:00", "17:00",
                "07:58", "--:--", "Presente", 0, "Puntual", false
        ));
        asistencias.add(new RegistroAsistencia(
                "ASI-13", "APO-05", "Valeria Quispe Morales", "Auxiliar ATV", "Apoyo",
                hoy, "Tarde (14:30 - 22:00)", "14:30", "22:00",
                "14:48", "--:--", "Retardo", 18, "Retardo +18 min por transporte", false
        ));
        asistencias.add(new RegistroAsistencia(
                "ASI-14", "APO-06", "Diego Farfán Tello", "Recepción Tarde", "Apoyo",
                hoy, "Tarde (14:00 - 22:00)", "14:00", "22:00",
                "13:55", "--:--", "Presente", 0, "Puntual · En línea telefónica", false
        ));
        asistencias.add(new RegistroAsistencia(
                "ASI-15", "VET-03", "Dra. Carmen Vega Hurtado", "Animales Exóticos", "Veterinario",
                hoy, "Tarde (15:00 - 22:00)", "15:00", "22:00",
                "14:52", "--:--", "Presente", 0, "Puntual", false
        ));
        asistencias.add(new RegistroAsistencia(
                "ASI-16", "VET-08", "Dra. Sofía Valdivia León", "Oncología", "Veterinario",
                hoy, "Tarde (15:00 - 22:00)", "15:00", "22:00",
                "14:58", "--:--", "Presente", 0, "Puntual", false
        ));
        asistencias.add(new RegistroAsistencia(
                "ASI-17", "APO-10", "Rosa Villegas Díaz", "Auxiliar Hospitalización UCI", "Apoyo",
                hoy, "Noche (21:30 - 07:30)", "21:30", "07:30",
                "--:--", "--:--", "Pendiente", 0, "Turno noche por iniciar", false
        ));
        asistencias.add(new RegistroAsistencia(
                "ASI-18", "VET-07", "Dr. Carlos Méndez Soto", "Urgencias 24h", "Veterinario",
                hoy, "Noche (22:00 - 08:00)", "22:00", "08:00",
                "--:--", "--:--", "Pendiente", 0, "Guardia nocturna por ingresar", false
        ));
        asistencias.add(new RegistroAsistencia(
                "ASI-19", "APO-11", "Brenda Castro Núñez", "Auxiliar Farmacia", "Apoyo",
                hoy, "Mañana (08:00 - 16:00)", "08:00", "16:00",
                "07:59", "--:--", "Presente", 0, "Puntual", false
        ));
        asistencias.add(new RegistroAsistencia(
                "ASI-20", "APO-12", "César Augusto Rivas", "Chofer Ambulancia PetCare", "Apoyo",
                hoy, "Mañana (08:00 - 17:00)", "08:00", "17:00",
                "07:51", "--:--", "Presente", 0, "Puntual · Unidad disponible", false
        ));
        asistencias.add(new RegistroAsistencia(
                "ASI-21", "APO-13", "Miriam Loyola Sánchez", "Auxiliar Limpieza Sede", "Apoyo",
                hoy, "Mañana (06:30 - 14:30)", "06:30", "14:30",
                "06:28", "14:35", "Presente", 0, "Puntual · Jornada completada", false
        ));
        asistencias.add(new RegistroAsistencia(
                "ASI-22", "APO-14", "Joaquín Estévez Peña", "Seguridad y Accesos", "Apoyo",
                hoy, "Mañana (07:00 - 19:00)", "07:00", "19:00",
                "06:45", "--:--", "Presente", 0, "Puntual", false
        ));
        asistencias.add(new RegistroAsistencia(
                "ASI-23", "APO-15", "Gabriela Tapia Luna", "Cajera Turno Tarde", "Apoyo",
                hoy, "Tarde (14:00 - 22:00)", "14:00", "22:00",
                "13:58", "--:--", "Presente", 0, "Puntual", false
        ));
        asistencias.add(new RegistroAsistencia(
                "ASI-24", "APO-16", "Gonzalo Barrientos Cruz", "Practicante Medicina Veterinaria", "Apoyo",
                hoy, "Mañana (08:00 - 14:00)", "08:00", "14:00",
                "--:--", "--:--", "Ausente", 0, "Falta injustificada", false
        ));

        // 5. Solicitudes de Permisos
        solicitudesPermisos.add(new SolicitudPermiso(
                "P-184", "VET-06", "Dra. Silvia Peña Mendoza", "Médico Oftalmólogo",
                "Permiso médico", hoy, "Tarde (15:00 - 22:00)",
                "Dr. Carlos Méndez", "Cita médica con especialista y reposo 24 hrs", "Pendiente"
        ));
        solicitudesPermisos.add(new SolicitudPermiso(
                "P-185", "APO-03", "Lucía Domínguez Peña", "Estilista Canina",
                "Capacitación / Congreso", hoy.plusDays(2), "Mañana (08:30 - 17:30)",
                "Jorge Benavides", "Seminario Internacional de Estética Canina y Spa", "Aprobado"
        ));
        solicitudesPermisos.add(new SolicitudPermiso(
                "P-186", "VET-02", "Dr. Mario Silva Paredes", "Dermatología",
                "Compensación de guardia", hoy.plusDays(5), "Sábado (08:00 - 15:00)",
                "Dra. Laura Morales", "Compensación de guardia extraordinaria de fin de semana", "Pendiente"
        ));
    }

    // --- Métodos de Submódulo 7.1: Veterinarios ---
    public List<Veterinario> getVeterinarios() {
        return new ArrayList<>(veterinarios);
    }

    public void guardarVeterinario(Veterinario v) {
        if (v == null) return;
        boolean existe = false;
        for (int i = 0; i < veterinarios.size(); i++) {
            if (veterinarios.get(i).getId().equalsIgnoreCase(v.getId())) {
                veterinarios.set(i, v);
                existe = true;
                break;
            }
        }
        if (!existe) {
            if (v.getId() == null || v.getId().isEmpty()) {
                v.setId("VET-" + String.format("%02d", veterinarios.size() + 1));
            }
            veterinarios.add(v);
        }
    }

    public void eliminarVeterinario(String id) {
        veterinarios.removeIf(v -> v.getId().equalsIgnoreCase(id));
    }

    public void actualizarEstadoVeterinario(String id, String nuevoEstado) {
        for (Veterinario v : veterinarios) {
            if (v.getId().equalsIgnoreCase(id)) {
                v.setEstadoDisponibilidad(nuevoEstado);
                break;
            }
        }
    }

    // --- Métodos de Submódulo 7.2: Personal de Apoyo ---
    public List<PersonalApoyo> getPersonalApoyo() {
        return new ArrayList<>(personalApoyo);
    }

    public void guardarPersonalApoyo(PersonalApoyo p) {
        if (p == null) return;
        boolean existe = false;
        for (int i = 0; i < personalApoyo.size(); i++) {
            if (personalApoyo.get(i).getId().equalsIgnoreCase(p.getId())) {
                personalApoyo.set(i, p);
                existe = true;
                break;
            }
        }
        if (!existe) {
            if (p.getId() == null || p.getId().isEmpty()) {
                p.setId("APO-" + String.format("%02d", personalApoyo.size() + 1));
            }
            personalApoyo.add(p);
        }
    }

    public void eliminarPersonalApoyo(String id) {
        personalApoyo.removeIf(p -> p.getId().equalsIgnoreCase(id));
    }

    public void actualizarEstadoPersonalApoyo(String id, String nuevoEstado) {
        for (PersonalApoyo p : personalApoyo) {
            if (p.getId().equalsIgnoreCase(id)) {
                p.setEstado(nuevoEstado);
                break;
            }
        }
    }

    // --- Métodos de Submódulo 7.3: Horarios y Turnos Médicos ---
    public List<TurnoSemanal> getCuadranteTurnos() {
        return new ArrayList<>(cuadranteTurnos);
    }

    public void guardarTurnoSemanal(TurnoSemanal t) {
        if (t == null) return;
        boolean existe = false;
        for (int i = 0; i < cuadranteTurnos.size(); i++) {
            if (cuadranteTurnos.get(i).getId().equalsIgnoreCase(t.getId())) {
                cuadranteTurnos.set(i, t);
                existe = true;
                break;
            }
        }
        if (!existe) {
            if (t.getId() == null || t.getId().isEmpty()) {
                t.setId("TUR-" + String.format("%02d", cuadranteTurnos.size() + 1));
            }
            cuadranteTurnos.add(t);
        }
    }

    public void actualizarTurnoDia(String profesionalId, int diaIndex, String nuevoHorario, String tipoTurno) {
        for (TurnoSemanal ts : cuadranteTurnos) {
            if (ts.getProfesionalId().equalsIgnoreCase(profesionalId)) {
                ts.setHorarioDia(diaIndex, nuevoHorario, tipoTurno);
                break;
            }
        }
    }

    // --- Métodos de Submódulo 7.4: Asistencias y Permisos ---
    public List<RegistroAsistencia> getAsistencias() {
        return new ArrayList<>(asistencias);
    }

    public void guardarAsistencia(RegistroAsistencia a) {
        if (a == null) return;
        boolean existe = false;
        for (int i = 0; i < asistencias.size(); i++) {
            if (asistencias.get(i).getId().equalsIgnoreCase(a.getId())) {
                asistencias.set(i, a);
                existe = true;
                break;
            }
        }
        if (!existe) {
            if (a.getId() == null || a.getId().isEmpty()) {
                a.setId("ASI-" + String.format("%02d", asistencias.size() + 1));
            }
            asistencias.add(a);
        }
    }

    public void marcarEntrada(String idAsistencia, String hora, String estado, int retardoMin, String nota) {
        for (RegistroAsistencia a : asistencias) {
            if (a.getId().equalsIgnoreCase(idAsistencia)) {
                a.setHoraEntradaReal(hora);
                a.setEstado(estado);
                a.setMinutosRetardo(retardoMin);
                if (nota != null && !nota.trim().isEmpty()) {
                    a.setNotaJustificacion(nota);
                }
                a.setMarcadoManual(true);
                break;
            }
        }
    }

    public void marcarSalida(String idAsistencia, String hora) {
        for (RegistroAsistencia a : asistencias) {
            if (a.getId().equalsIgnoreCase(idAsistencia)) {
                a.setHoraSalidaReal(hora);
                a.setMarcadoManual(true);
                break;
            }
        }
    }

    public void actualizarEstadoAsistencia(String idAsistencia, String nuevoEstado, String nota) {
        for (RegistroAsistencia a : asistencias) {
            if (a.getId().equalsIgnoreCase(idAsistencia)) {
                a.setEstado(nuevoEstado);
                if (nota != null) a.setNotaJustificacion(nota);
                break;
            }
        }
    }

    public int contarPresentesHoy() {
        int c = 0;
        for (RegistroAsistencia a : asistencias) {
            if ("Presente".equalsIgnoreCase(a.getEstado())) c++;
        }
        return c;
    }

    public int contarRetardosHoy() {
        int c = 0;
        for (RegistroAsistencia a : asistencias) {
            if ("Retardo".equalsIgnoreCase(a.getEstado())) c++;
        }
        return c;
    }

    public int contarAusentesHoy() {
        int c = 0;
        for (RegistroAsistencia a : asistencias) {
            if ("Ausente".equalsIgnoreCase(a.getEstado())) c++;
        }
        return c;
    }

    public int contarPermisosHoy() {
        int c = 0;
        for (RegistroAsistencia a : asistencias) {
            if ("Permiso".equalsIgnoreCase(a.getEstado())) c++;
        }
        return c;
    }

    public List<SolicitudPermiso> getSolicitudesPermisos() {
        return new ArrayList<>(solicitudesPermisos);
    }

    public void guardarSolicitudPermiso(SolicitudPermiso sp) {
        if (sp == null) return;
        boolean existe = false;
        for (int i = 0; i < solicitudesPermisos.size(); i++) {
            if (solicitudesPermisos.get(i).getId().equalsIgnoreCase(sp.getId())) {
                solicitudesPermisos.set(i, sp);
                existe = true;
                break;
            }
        }
        if (!existe) {
            if (sp.getId() == null || sp.getId().isEmpty()) {
                sp.setId("P-" + (180 + solicitudesPermisos.size() + 1));
            }
            solicitudesPermisos.add(0, sp);
        }
    }

    public void aprobarPermiso(String idPermiso) {
        for (SolicitudPermiso sp : solicitudesPermisos) {
            if (sp.getId().equalsIgnoreCase(idPermiso)) {
                sp.setEstado("Aprobado");
                // Reflejar en asistencia si aplica
                for (RegistroAsistencia a : asistencias) {
                    if (a.getPersonalId() != null && a.getPersonalId().equalsIgnoreCase(sp.getPersonalId())) {
                        a.setEstado("Permiso");
                        a.setNotaJustificacion("Permiso " + sp.getId() + " aprobado");
                    }
                }
                break;
            }
        }
    }

    public void rechazarPermiso(String idPermiso, String motivo) {
        for (SolicitudPermiso sp : solicitudesPermisos) {
            if (sp.getId().equalsIgnoreCase(idPermiso)) {
                sp.setEstado("Rechazado");
                if (motivo != null && !motivo.isEmpty()) {
                    sp.setMotivoDetalle(sp.getMotivoDetalle() + " [Rechazado: " + motivo + "]");
                }
                break;
            }
        }
    }

    // =========================================================================
    // MÓDULO 8: INTELIGENCIA DE NEGOCIOS Y REPORTES (Arroyo Preciado, Harry Martin)
    // =========================================================================

    private void inicializarModulo8ReportesBI() {
        // 1. Métricas Mensuales (12 meses: Jun 2023 a Mayo 2024)
        metricasMensuales.add(new MetricaMensualIngreso("Jun 23", 2023, 6, 68400.0, 36200.0, 24800.0));
        metricasMensuales.add(new MetricaMensualIngreso("Jul 23", 2023, 7, 72100.0, 38900.0, 25200.0));
        metricasMensuales.add(new MetricaMensualIngreso("Ago 23", 2023, 8, 75300.0, 41200.0, 25800.0));
        metricasMensuales.add(new MetricaMensualIngreso("Set 23", 2023, 9, 78900.0, 43800.0, 26100.0));
        metricasMensuales.add(new MetricaMensualIngreso("Oct 23", 2023, 10, 83200.0, 45900.0, 26500.0));
        metricasMensuales.add(new MetricaMensualIngreso("Nov 23", 2023, 11, 86400.0, 48100.0, 26900.0));
        metricasMensuales.add(new MetricaMensualIngreso("Dic 23", 2023, 12, 95200.0, 54100.0, 28400.0));
        metricasMensuales.add(new MetricaMensualIngreso("Ene 24", 2024, 1, 82400.0, 45100.0, 26100.0));
        metricasMensuales.add(new MetricaMensualIngreso("Feb 24", 2024, 2, 85300.0, 47200.0, 26800.0));
        metricasMensuales.add(new MetricaMensualIngreso("Mar 24", 2024, 3, 89100.0, 49200.0, 27200.0));
        metricasMensuales.add(new MetricaMensualIngreso("Abr 24", 2024, 4, 91200.0, 50900.0, 27450.0));
        metricasMensuales.add(new MetricaMensualIngreso("May 24", 2024, 5, 95150.0, 53100.0, 27700.0));

        // 2. Reportes Clínicos Detallados (Top casos según wireframe)
        reportesClinicos.add(new ReporteClinicoDetalle(
                "RC-2023-0941", LocalDate.now().minusDays(1), "Max", "Canino · Golden Retriever",
                "Carlos Mendoza Soto", "+51 984 552 110", "Dra. Elena Ruiz Salazar",
                "Gastroenteritis Aguda Infecciosa", "Moderada", 35, true,
                "Fluidoterapia Ringer Lactato 500ml + Metronidazol 250mg c/12h x 5 días + Probióticos",
                "Paciente con deshidratación leve del 6%. Se estabilizó con fluidos y tolera dieta blanda."
        ));
        reportesClinicos.add(new ReporteClinicoDetalle(
                "RC-2023-0942", LocalDate.now().minusDays(1), "Misi", "Felino · Siamés",
                "Lucía Torres Alva", "+51 977 123 456", "Dr. Marcos León Bravo",
                "Dermatitis Alérgica por Pulgas (DAPP)", "Leve", 25, false,
                "Apoquel 3.6mg media tableta c/24h x 7 días + Pipeta Antipulgas Fipronil",
                "Alopecia focal en región lumbosacra con prurito intenso. Ausencia de ectoparásitos visibles hoy."
        ));
        reportesClinicos.add(new ReporteClinicoDetalle(
                "RC-2023-0943", LocalDate.now().minusDays(2), "Thor", "Canino · Pastor Alemán",
                "Fernando Castillo P.", "+51 991 445 667", "Dra. Clara Vega Hurtado",
                "Otitis Externa Eritematosa Bilateral", "Moderada", 30, true,
                "Lavado ótico con Solución Tris-EDTA + Otomax 4 gotas en cada conducto c/12h x 10d",
                "Citología con abundantes levaduras Malassezia spp. Revisión de control en 7 días."
        ));
        reportesClinicos.add(new ReporteClinicoDetalle(
                "RC-2023-0944", LocalDate.now().minusDays(2), "Kira", "Canino · Pug",
                "Ana María Rojas Paredes", "+51 971 223 344", "Dr. Andrés Pardo Soto",
                "Síndrome Braquicefálico Estadio I", "Leve", 40, true,
                "Reposo térmico, evitar collares de ahorque, nebulizaciones salinas x 3 días",
                "Estenosis moderada de narinas y elongación leve de paladar blando. Sugerir rinoplastia futura."
        ));
        reportesClinicos.add(new ReporteClinicoDetalle(
                "RC-2023-0945", LocalDate.now().minusDays(3), "Simba", "Felino · Persa",
                "Jorge Valdivia León", "+51 942 331 889", "Dra. Sofía Mora Castro",
                "Gingivoestomatitis Crónica Felina", "Grave", 45, true,
                "Meloxicam 0.1mg/kg suspensión oral + Clorhexidina gel 0.12% + Programar exodoncia",
                "Dolor marcado al masticar croquetas secas. Requiere biopsia gingival y perfil renal previo."
        ));
        reportesClinicos.add(new ReporteClinicoDetalle(
                "RC-2023-0946", LocalDate.now().minusDays(4), "Rocky", "Canino · Bulldog Francés",
                "Roberto Dávila Wong", "+51 965 412 889", "Dr. Marcos León Bravo",
                "Traumatismo y Contusión Miembro Posterior", "Grave", 50, true,
                "Vendaje Robert Jones + Tramadol 50mg inyectable + Placa Radiográfica de fémur",
                "Cojera grado IV sin apoyo. Descartada fractura completa por rayos X. Reposo estricto en jaula."
        ));
        reportesClinicos.add(new ReporteClinicoDetalle(
                "RC-2023-0947", LocalDate.now().minusDays(5), "Luna", "Felino · Común Europeo",
                "Mariana Paredes Torres", "+51 993 445 120", "Dra. Elena Ruiz Salazar",
                "Enfermedad Renal Crónica IRIS II", "Grave", 38, true,
                "Dieta Renal Húmeda + Quelante de Fósforo Ipakitine 1g/5kg c/12h con comida",
                "Creatinina 2.4 mg/dL, SDMA 18 ug/dL. Buen apetito y actitud alerta. Monitoreo mensual."
        ));
        reportesClinicos.add(new ReporteClinicoDetalle(
                "RC-2023-0948", LocalDate.now().minusDays(6), "Toby", "Canino · Pug",
                "Carlos Eduardo Morales", "+51 984 552 110", "Dra. Clara Vega Hurtado",
                "Profilaxis Dental y Periodontitis Grado II", "Moderada", 35, false,
                "Destartraje ultrasónico bajo sedación + Clindamicina 75mg x 7 días",
                "Retiro de placa bacteriana y sarro supra/subgingival. Pulido con pasta fluorada exitoso."
        ));

        // 3. Desglose Financiero por Prestación / Departamento
        desglosesFinancieros.add(new DesgloseFinancieroPrestacion(
                "Consultas Generales y Especialidades", "Médico Clínico",
                2450, 85750.0, 25725.0, 70.0, "Óptimo"
        ));
        desglosesFinancieros.add(new DesgloseFinancieroPrestacion(
                "Cirugías Mayores y Quirófano", "Quirófano Central",
                185, 78400.0, 27440.0, 65.0, "Excelente"
        ));
        desglosesFinancieros.add(new DesgloseFinancieroPrestacion(
                "Diagnóstico por Imagen y Laboratorio", "Lab & Diagnóstico",
                620, 62472.0, 21865.0, 65.0, "Excelente"
        ));
        desglosesFinancieros.add(new DesgloseFinancieroPrestacion(
                "Farmacia y Medicación Hospitalaria", "Farmacia Veterinaria",
                1140, 68400.0, 47880.0, 30.0, "Margen Normal"
        ));
        desglosesFinancieros.add(new DesgloseFinancieroPrestacion(
                "Pet Shop, Alimentos y Accesorios", "Venta Comercial",
                580, 34800.0, 26100.0, 25.0, "Estable"
        ));
        desglosesFinancieros.add(new DesgloseFinancieroPrestacion(
                "Estética, Baños y Peluquería Canina", "Área Grooming",
                335, 18828.0, 13410.0, 28.8, "A Revisar"
        ));

        // 4. Historial de Exportaciones Recientes
        historialExportaciones.add(new HistorialExportacion(
                "EXP-2024-0518", "Pacientes y Fichas Clínicas", LocalDateTime.now().minusHours(3),
                "Caninos, Felinos · Sede Central", "XLSX", 3120, 2.4, "Completado",
                "exports/pacientes_clinica_20240518.xlsx"
        ));
        historialExportaciones.add(new HistorialExportacion(
                "EXP-2024-0512", "Finanzas y Facturación", LocalDateTime.now().minusDays(6),
                "Tickets > S/ 50 · Período Q1", "CSV", 14210, 8.7, "Completado",
                "exports/tickets_ventas_q1_2024.csv"
        ));
        historialExportaciones.add(new HistorialExportacion(
                "EXP-2024-0504", "Inventario y Farmacia", LocalDateTime.now().minusDays(14),
                "Stock Crítico · Todas las sedes", "XLSX", 842, 0.6, "Completado",
                "exports/inventario_stock_20240504.xlsx"
        ));
        historialExportaciones.add(new HistorialExportacion(
                "EXP-2024-0428", "Pacientes y Fichas Clínicas", LocalDateTime.now().minusDays(20),
                "Especie Canino · Vacunas al día", "PDF", 1480, 3.1, "Completado",
                "exports/reporte_pacientes_vacunados.pdf"
        ));
        historialExportaciones.add(new HistorialExportacion(
                "EXP-2024-0415", "Finanzas y Facturación", LocalDateTime.now().minusDays(33),
                "Cierre Mensual Marzo 2024", "XLSX", 5310, 4.2, "Completado",
                "exports/cierre_contable_marzo_2024.xlsx"
        ));
    }

    private DesgloseFinancieroPrestacion DesgresoPrestacionSeguro(String linea, String centro,
                                                                 int trans, double ing, double coste,
                                                                 double rent, String est) {
        return new DesgloseFinancieroPrestacion(linea, centro, trans, ing, coste, rent, est);
    }

    public List<MetricaMensualIngreso> getMetricasMensuales() {
        return new ArrayList<>(metricasMensuales);
    }

    public List<ReporteClinicoDetalle> getReportesClinicos() {
        return new ArrayList<>(reportesClinicos);
    }

    public List<ReporteClinicoDetalle> buscarReportesClinicos(String termino, String veterinario, String diagnostico) {
        List<ReporteClinicoDetalle> lista = new ArrayList<>();
        String q = termino != null ? termino.trim().toLowerCase() : "";
        for (ReporteClinicoDetalle r : reportesClinicos) {
            boolean coincideTermino = q.isEmpty() ||
                    r.getCodigoReporte().toLowerCase().contains(q) ||
                    r.getNombrePaciente().toLowerCase().contains(q) ||
                    r.getNombrePropietario().toLowerCase().contains(q) ||
                    r.getDiagnosticoConfirmado().toLowerCase().contains(q);

            boolean coincideVet = veterinario == null || veterinario.isEmpty() ||
                    "Todos los Veterinarios".equalsIgnoreCase(veterinario) ||
                    r.getVeterinarioTratante().toLowerCase().contains(veterinario.toLowerCase());

            boolean coincideDiag = diagnostico == null || diagnostico.isEmpty() ||
                    "Todos los Diagnósticos".equalsIgnoreCase(diagnostico) ||
                    r.getDiagnosticoConfirmado().toLowerCase().contains(diagnostico.toLowerCase());

            if (coincideTermino && coincideVet && coincideDiag) {
                lista.add(r);
            }
        }
        return lista;
    }

    public void guardarReporteClinico(ReporteClinicoDetalle rep) {
        if (rep == null) return;
        boolean existe = false;
        for (int i = 0; i < reportesClinicos.size(); i++) {
            if (reportesClinicos.get(i).getCodigoReporte().equalsIgnoreCase(rep.getCodigoReporte())) {
                reportesClinicos.set(i, rep);
                existe = true;
                break;
            }
        }
        if (!existe) {
            if (rep.getCodigoReporte() == null || rep.getCodigoReporte().isEmpty()) {
                rep.setCodigoReporte("RC-2024-" + String.format("%04d", 950 + reportesClinicos.size()));
            }
            reportesClinicos.add(0, rep);
        }
    }

    public List<DesgloseFinancieroPrestacion> getDesglosesFinancieros() {
        return new ArrayList<>(desglosesFinancieros);
    }

    public List<HistorialExportacion> getHistorialExportaciones() {
        return new ArrayList<>(historialExportaciones);
    }

    public void registrarExportacion(HistorialExportacion exp) {
        if (exp != null) {
            historialExportaciones.add(0, exp);
        }
    }

    public HistorialExportacion generarExportacion(String origen, String formato, String filtros, List<String> camposAdicionales) {
        int filas;
        double tamano;
        String extension;
        String fmt = formato != null ? formato.toUpperCase() : "XLSX";

        if ("Inventario y Farmacia".equalsIgnoreCase(origen)) {
            filas = 842;
            tamano = fmt.contains("CSV") ? 0.3 : (fmt.contains("PDF") ? 1.2 : 0.6);
            extension = fmt.contains("CSV") ? ".csv" : (fmt.contains("PDF") ? ".pdf" : ".xlsx");
        } else if ("Finanzas y Facturación".equalsIgnoreCase(origen)) {
            filas = 14210;
            tamano = fmt.contains("CSV") ? 4.5 : (fmt.contains("PDF") ? 9.8 : 8.7);
            extension = fmt.contains("CSV") ? ".csv" : (fmt.contains("PDF") ? ".pdf" : ".xlsx");
        } else {
            filas = 3120;
            tamano = fmt.contains("CSV") ? 1.1 : (fmt.contains("PDF") ? 4.6 : 2.4);
            extension = fmt.contains("CSV") ? ".csv" : (fmt.contains("PDF") ? ".pdf" : ".xlsx");
        }

        String id = "EXP-" + LocalDate.now().getYear() + "-" + String.format("%04d", 520 + historialExportaciones.size());
        String ruta = "exports/export_" + id.toLowerCase().replace('-', '_') + extension;

        HistorialExportacion exp = new HistorialExportacion(
                id, origen, LocalDateTime.now(), filtros != null && !filtros.isEmpty() ? filtros : "Filtros predeterminados",
                fmt, filas, tamano, "Completado", ruta
        );
        historialExportaciones.add(0, exp);
        return exp;
    }

    // =========================================================================
    // MÓDULO 9: NOTIFICACIONES, DOCUMENTOS Y AUDITORÍA (Vera Aguilar, Carlos Edgardo)
    // =========================================================================

    private void inicializarModulo9NotificacionesAuditoria() {
        // 1. Centro de Notificaciones (Alertas, Mensajes y Eventos según wireframe 1)
        notificaciones.add(new NotificacionSistema(
                "NOTIF-001", "NUEVA ALERTA", "Alerta",
                "Alerta de Seguridad", "Intento de acceso no autorizado detectado en el sistema.",
                LocalDateTime.now().minusMinutes(5), "Hace 5 min", false, "Alta", "Seguridad"
        ));
        notificaciones.add(new NotificacionSistema(
                "NOTIF-002", "MENSAJE IMPORTANTE", "Mensaje",
                "Mantenimiento Programado", "Actualización programada del servidor para este fin de semana.",
                LocalDateTime.now().minusHours(2), "Hace 2 horas", false, "Media", "Servidor"
        ));
        notificaciones.add(new NotificacionSistema(
                "NOTIF-003", "ALERTA DE SISTEMA", "Alerta",
                "Capacidad Crítica", "El almacenamiento del Repositorio Documental supera el 80%.",
                LocalDateTime.now().minusDays(1), "Ayer", false, "Alta", "Almacenamiento"
        ));
        notificaciones.add(new NotificacionSistema(
                "NOTIF-004", "NUEVO MENSAJE", "Mensaje",
                "Solicitud de Privilegios", "El usuario J. Pérez ha solicitado permisos de auditoria.",
                LocalDateTime.now().minusDays(2), "Hace 2 días", false, "Baja", "Auditoría"
        ));
        notificaciones.add(new NotificacionSistema(
                "NOTIF-005", "EVENTO CLÍNICO", "Evento",
                "Control Quirúrgico", "Cierre de lote quirúrgico y esterilización completada con éxito.",
                LocalDateTime.now().minusDays(3), "Hace 3 días", true, "Media", "Quirófano"
        ));
        notificaciones.add(new NotificacionSistema(
                "NOTIF-006", "EVENTO DE AUDITORÍA", "Evento",
                "Copia de Seguridad", "Backup automático semanal de base de datos generado satisfactoriamente.",
                LocalDateTime.now().minusDays(4), "Hace 4 días", true, "Baja", "Sistema"
        ));

        // 2. Configuración de Canales (según wireframe 2)
        canalesNotificacion.add(new ConfiguracionCanalNotificacion(
                "Email", true, "usuario@empresa.com", "Inmediata"
        ));
        canalesNotificacion.add(new ConfiguracionCanalNotificacion(
                "SMS", true, "+51 987 654 321", "Resumen Diario"
        ));
        canalesNotificacion.add(new ConfiguracionCanalNotificacion(
                "Notificaciones Push", false, "Dispositivo Móvil / Navegador Web", "Inmediata"
        ));

        preferenciasEventos = new PreferenciaNotificacionEventos(true, true, true, false);

        // 3. Repositorio Documental (según wireframe 3)
        documentosRepositorio.add(new DocumentoRepositorio(
                "DOC-001", "PDF", "Factura.pdf", "Facturación",
                2.4, LocalDateTime.of(2026, 3, 24, 9, 30), "admin_user", "docs/Factura.pdf"
        ));
        documentosRepositorio.add(new DocumentoRepositorio(
                "DOC-002", "DOCX", "Informe.docx", "Auditoría",
                1.1, LocalDateTime.of(2026, 3, 23, 15, 45), "jgarcia", "docs/Informe.docx"
        ));
        documentosRepositorio.add(new DocumentoRepositorio(
                "DOC-003", "PDF", "Contrato.pdf", "Legal / RRHH",
                3.8, LocalDateTime.of(2026, 3, 20, 11, 15), "admin_user", "docs/Contrato.pdf"
        ));
        documentosRepositorio.add(new DocumentoRepositorio(
                "DOC-004", "XLSX", "Reporte_Anual.xlsx", "Contabilidad",
                5.2, LocalDateTime.of(2026, 3, 18, 16, 20), "mlopez", "docs/Reporte_Anual.xlsx"
        ));
        documentosRepositorio.add(new DocumentoRepositorio(
                "DOC-005", "PDF", "Guia_Farmacologica_2026.pdf", "Clínico",
                4.1, LocalDateTime.of(2026, 3, 15, 10, 00), "admin_user", "docs/Guia_Farmacologica_2026.pdf"
        ));
        documentosRepositorio.add(new DocumentoRepositorio(
                "DOC-006", "DOCX", "Protocolo_Bioseguridad.docx", "Auditoría",
                1.8, LocalDateTime.of(2026, 3, 12, 14, 10), "jgarcia", "docs/Protocolo_Bioseguridad.docx"
        ));
        documentosRepositorio.add(new DocumentoRepositorio(
                "DOC-007", "XLSX", "Inventario_Kardex_Q1.xlsx", "Inventario",
                3.3, LocalDateTime.of(2026, 3, 10, 17, 30), "mlopez", "docs/Inventario_Kardex_Q1.xlsx"
        ));
        documentosRepositorio.add(new DocumentoRepositorio(
                "DOC-008", "PDF", "Reglamento_Interno_Trabajo.pdf", "Legal / RRHH",
                2.9, LocalDateTime.of(2026, 3, 5, 8, 45), "admin_user", "docs/Reglamento_Interno_Trabajo.pdf"
        ));

        // 4. Logs y Trazabilidad (según wireframe 4)
        logsAuditoria.add(new LogAuditoria(
                "#EV-1042", "Modificación de Canales", "admin_user",
                LocalDateTime.of(2026, 3, 24, 10, 15, 22), "192.168.1.10", "ÉXITO",
                "Actualización de frecuencia de notificaciones por email a 'Inmediata'"
        ));
        logsAuditoria.add(new LogAuditoria(
                "#EV-1041", "Descarga de Contrato.pdf", "jgarcia",
                LocalDateTime.of(2026, 3, 24, 9, 40, 11), "192.168.1.25", "ÉXITO",
                "Descarga de documento firmado desde el Repositorio Documental"
        ));
        logsAuditoria.add(new LogAuditoria(
                "#EV-1040", "Lectura de Notificación", "mlopez",
                LocalDateTime.of(2026, 3, 23, 18, 5, 0), "192.168.1.18", "ÉXITO",
                "Revisión de aviso de mantenimiento programado de servidor"
        ));
        logsAuditoria.add(new LogAuditoria(
                "#EV-1039", "Intento de Acceso Fallido", "desconocido",
                LocalDateTime.of(2026, 3, 23, 14, 22, 10), "192.168.1.145", "FALLIDO",
                "Credenciales erróneas usuario 'root'. Bloqueo temporal activado por firewall."
        ));
        logsAuditoria.add(new LogAuditoria(
                "#EV-1038", "Carga de Factura.pdf", "admin_user",
                LocalDateTime.of(2026, 3, 23, 9, 12, 5), "192.168.1.10", "ÉXITO",
                "Subida de comprobante electrónico firmado al almacenamiento central"
        ));
        logsAuditoria.add(new LogAuditoria(
                "#EV-1037", "Exportación de Reporte Financiero", "mlopez",
                LocalDateTime.of(2026, 3, 22, 16, 45, 12), "192.168.1.18", "ÉXITO",
                "Generación de archivo XLSX con balance mensual y caja chica"
        ));
        logsAuditoria.add(new LogAuditoria(
                "#EV-1036", "Eliminación de Registro Temporal", "admin_user",
                LocalDateTime.of(2026, 3, 22, 11, 20, 0), "192.168.1.10", "ÉXITO",
                "Depuración de registros transitorios y caché de sincronización"
        ));
        logsAuditoria.add(new LogAuditoria(
                "#EV-1035", "Autenticación de Usuario", "jgarcia",
                LocalDateTime.of(2026, 3, 22, 8, 30, 45), "192.168.1.25", "ÉXITO",
                "Inicio de sesión exitoso en el módulo de personal y asistencias"
        ));
    }

    public List<NotificacionSistema> getNotificaciones() {
        return new ArrayList<>(notificaciones);
    }

    public List<NotificacionSistema> getNotificacionesPorCategoria(String cat) {
        if (cat == null || cat.isEmpty() || "Todas".equalsIgnoreCase(cat)) {
            return new ArrayList<>(notificaciones);
        }
        return notificaciones.stream()
                .filter(n -> n.getCategoria().equalsIgnoreCase(cat))
                .collect(Collectors.toList());
    }

    public void marcarNotificacionComoLeida(String idNotif) {
        if (idNotif == null) return;
        for (NotificacionSistema n : notificaciones) {
            if (n.getId().equalsIgnoreCase(idNotif)) {
                n.setLeida(true);
                registrarLogAuditoria(new LogAuditoria(
                        "#EV-" + (1043 + logsAuditoria.size()), "Lectura de Notificación",
                        "admin_user", LocalDateTime.now(), "127.0.0.1", "ÉXITO",
                        "Notificación " + idNotif + " marcada como leída"
                ));
                break;
            }
        }
    }

    public void limpiarAlertasLeidas() {
        notificaciones.removeIf(NotificacionSistema::isLeida);
    }

    public int contarNotificacionesPorCategoria(String cat) {
        int c = 0;
        for (NotificacionSistema n : notificaciones) {
            if (cat == null || cat.isEmpty() || "Todas".equalsIgnoreCase(cat) || n.getCategoria().equalsIgnoreCase(cat)) {
                c++;
            }
        }
        return c;
    }

    public List<ConfiguracionCanalNotificacion> getCanalesNotificacion() {
        return new ArrayList<>(canalesNotificacion);
    }

    public PreferenciaNotificacionEventos getPreferenciasEventos() {
        return preferenciasEventos;
    }

    public void guardarPreferenciasCanales(List<ConfiguracionCanalNotificacion> nuevosCanales, PreferenciaNotificacionEventos nuevasPref) {
        if (nuevosCanales != null) {
            canalesNotificacion.clear();
            canalesNotificacion.addAll(nuevosCanales);
        }
        if (nuevasPref != null) {
            preferenciasEventos = nuevasPref;
        }
        registrarLogAuditoria(new LogAuditoria(
                "#EV-" + (1043 + logsAuditoria.size()), "Modificación de Canales",
                "admin_user", LocalDateTime.now(), "127.0.0.1", "ÉXITO",
                "Canales de notificación y eventos actualizados por el administrador"
        ));
    }

    public List<DocumentoRepositorio> getDocumentosRepositorio() {
        return new ArrayList<>(documentosRepositorio);
    }

    public List<DocumentoRepositorio> buscarDocumentosRepositorio(String termino, String tipoFiltro) {
        List<DocumentoRepositorio> res = new ArrayList<>();
        String q = termino != null ? termino.trim().toLowerCase() : "";
        for (DocumentoRepositorio d : documentosRepositorio) {
            boolean coincideTermino = q.isEmpty() ||
                    d.getNombreArchivo().toLowerCase().contains(q) ||
                    d.getCategoria().toLowerCase().contains(q) ||
                    d.getTipoExtension().toLowerCase().contains(q);

            boolean coincideTipo = tipoFiltro == null || tipoFiltro.isEmpty() ||
                    "Todos".equalsIgnoreCase(tipoFiltro) || "Filtrar: Todos".equalsIgnoreCase(tipoFiltro) ||
                    d.getTipoExtension().equalsIgnoreCase(tipoFiltro);

            if (coincideTermino && coincideTipo) {
                res.add(d);
            }
        }
        return res;
    }

    public void agregarDocumentoRepositorio(DocumentoRepositorio doc) {
        if (doc != null) {
            if (doc.getId() == null || doc.getId().isEmpty()) {
                doc.setId("DOC-" + String.format("%03d", documentosRepositorio.size() + 1));
            }
            documentosRepositorio.add(0, doc);
            registrarLogAuditoria(new LogAuditoria(
                    "#EV-" + (1043 + logsAuditoria.size()), "Carga de " + doc.getNombreArchivo(),
                    doc.getUsuarioCarga() != null ? doc.getUsuarioCarga() : "admin_user",
                    LocalDateTime.now(), "127.0.0.1", "ÉXITO",
                    "Carga exitosa de archivo al Repositorio Documental (" + doc.getTamanoLegible() + ")"
            ));
        }
    }

    public List<LogAuditoria> getLogsAuditoria() {
        return new ArrayList<>(logsAuditoria);
    }

    public List<LogAuditoria> filtrarLogsAuditoria(LocalDate desde, LocalDate hasta, String tipoEvento, String usuario) {
        List<LogAuditoria> res = new ArrayList<>();
        String u = usuario != null ? usuario.trim().toLowerCase() : "";
        for (LogAuditoria l : logsAuditoria) {
            LocalDate f = l.getFechaHora() != null ? l.getFechaHora().toLocalDate() : LocalDate.now();

            boolean fechaValida = (desde == null || !f.isBefore(desde)) && (hasta == null || !f.isAfter(hasta));
            boolean eventoValido = tipoEvento == null || tipoEvento.isEmpty() ||
                    "Todos".equalsIgnoreCase(tipoEvento) || "Evento: Todos".equalsIgnoreCase(tipoEvento) ||
                    l.getTipoEvento().toLowerCase().contains(tipoEvento.toLowerCase());
            boolean usuarioValido = u.isEmpty() || l.getUsuario().toLowerCase().contains(u);

            if (fechaValida && eventoValido && usuarioValido) {
                res.add(l);
            }
        }
        return res;
    }

    public void registrarLogAuditoria(LogAuditoria log) {
        if (log != null) {
            logsAuditoria.add(0, log);
        }
    }

    // =========================================================================
    // MÓDULO 10: CONFIGURACIÓN, INTEGRACIONES Y SOPORTE (Minaya Bravo, Almendra Lili)
    // =========================================================================

    private void inicializarModulo10ConfiguracionSoporte() {
        // 1. Configuración Institucional de la Clínica
        configuracionClinica = new ConfiguracionClinica(
                "HappyPets Servicios Veterinarios S.A.S.",
                "HappyPets Clínica y Hospital 24H",
                "NIT 900.842.115-4",
                "contacto@happypets-vet.com",
                "+57 (601) 745-9988 / +57 312 000 1122",
                "Avenida Las Mascotas # 45 - 28, Sector San Martín",
                "COP ($) - Peso Colombiano",
                "America/Bogota (UTC -05:00)",
                "assets/logo_happypets.png",
                "Sede Norte - Principal"
        );

        // 2. Usuarios Activos y Asignación de Roles (14 colaboradores según wireframe USUARIO.pdf)
        usuariosSistema.add(new Usuario("rtorres", "admin123", "Dr. Roberto Torres", "Administrador", "rtorres@happypets.com", "Dirección Médica", "Activo"));
        usuariosSistema.add(new Usuario("cmorales", "vet123", "Dra. Camila Morales", "Veterinario Titular", "cmorales@happypets.com", "Cirugía & Quirófano", "Activo"));
        usuariosSistema.add(new Usuario("jperez", "rec123", "Juan Pérez Castro", "Recepcionista", "jperez@happypets.com", "Atención al Cliente", "Activo"));
        usuariosSistema.add(new Usuario("lgomez", "aux123", "Lucía Gómez", "Auxiliar Veterinario", "lgomez@happypets.com", "Hospitalización", "Inactivo"));
        usuariosSistema.add(new Usuario("lmorales", "vet123", "Dra. Laura Morales Ruiz", "Veterinario Titular", "lmorales@happypets.com", "Cirugía General", "Activo"));
        usuariosSistema.add(new Usuario("msilva", "vet123", "Dr. Mario Silva Paredes", "Veterinario Titular", "msilva@happypets.com", "Dermatología", "Activo"));
        usuariosSistema.add(new Usuario("cvega", "vet123", "Dra. Carmen Vega Hurtado", "Veterinario Especialista", "cvega@happypets.com", "Animales Exóticos", "Activo"));
        usuariosSistema.add(new Usuario("rmendoza", "admin", "Dr. Roberto Mendoza Chávez", "Administrador", "rmendoza@happypets.com", "Cardiología y Ecografía", "Activo"));
        usuariosSistema.add(new Usuario("eruiz", "vet123", "Dra. Elena Ruiz Salazar", "Veterinario Titular", "eruiz@happypets.com", "Medicina Felina", "Activo"));
        usuariosSistema.add(new Usuario("msanchez", "aux123", "Marta Sánchez Gómez", "Auxiliar Veterinario", "msanchez@happypets.com", "UCI y Quirófano B", "Activo"));
        usuariosSistema.add(new Usuario("cvegar", "rec123", "Carlos Vega Ruiz", "Recepcionista", "cvegar@happypets.com", "Caja Principal y Triaje", "Activo"));
        usuariosSistema.add(new Usuario("ldominguez", "aux123", "Lucía Domínguez Peña", "Auxiliar Veterinario", "ldominguez@happypets.com", "Grooming Spa Canino", "Activo"));
        usuariosSistema.add(new Usuario("palarcon", "admin123", "Patricia Alarcón Salazar", "Contador / Auditor", "palarcon@happypets.com", "Administración & RRHH", "Activo"));
        usuariosSistema.add(new Usuario("rvillegas", "aux123", "Rosa Villegas Díaz", "Auxiliar Veterinario", "rvillegas@happypets.com", "Hospitalización Nocturna", "Activo"));

        // 3. Matriz de Roles y Permisos
        RolPermiso rAdmin = new RolPermiso("ROL-01", "Administrador", "Acceso irrestricto a todos los módulos y ajustes del sistema", "Total", true);
        rAdmin.asignarPermiso("Pacientes e Historias", true);
        rAdmin.asignarPermiso("Agenda y Citas", true);
        rAdmin.asignarPermiso("Servicios Médicos", true);
        rAdmin.asignarPermiso("Estética y Hospedaje", true);
        rAdmin.asignarPermiso("Farmacia e Inventario", true);
        rAdmin.asignarPermiso("Finanzas y Ventas", true);
        rAdmin.asignarPermiso("Personal y RRHH", true);
        rAdmin.asignarPermiso("Reportes y BI", true);
        rAdmin.asignarPermiso("Notificaciones y Auditoría", true);
        rAdmin.asignarPermiso("Configuración y Soporte", true);
        rolesPermisos.add(rAdmin);

        RolPermiso rVet = new RolPermiso("ROL-02", "Veterinario Titular", "Atención médica, emisión de diagnósticos y recetas", "Médico", true);
        rVet.asignarPermiso("Pacientes e Historias", true);
        rVet.asignarPermiso("Agenda y Citas", true);
        rVet.asignarPermiso("Servicios Médicos", true);
        rVet.asignarPermiso("Estética y Hospedaje", true);
        rVet.asignarPermiso("Farmacia e Inventario", true);
        rVet.asignarPermiso("Reportes y BI", true);
        rolesPermisos.add(rVet);

        RolPermiso rRec = new RolPermiso("ROL-03", "Recepcionista", "Atención al cliente, agendamiento de citas y cobros POS", "Operativo", true);
        rRec.asignarPermiso("Pacientes e Historias", true);
        rRec.asignarPermiso("Agenda y Citas", true);
        rRec.asignarPermiso("Finanzas y Ventas", true);
        rRec.asignarPermiso("Notificaciones y Auditoría", false);
        rolesPermisos.add(rRec);

        RolPermiso rAux = new RolPermiso("ROL-04", "Auxiliar Veterinario", "Soporte en hospitalización, grooming y suministro de dosis", "Operativo", true);
        rAux.asignarPermiso("Pacientes e Historias", true);
        rAux.asignarPermiso("Estética y Hospedaje", true);
        rAux.asignarPermiso("Farmacia e Inventario", false);
        rolesPermisos.add(rAux);

        RolPermiso rCont = new RolPermiso("ROL-05", "Contador / Auditor", "Auditoría financiera, facturación, egresos y trazabilidad", "Financiero", true);
        rCont.asignarPermiso("Finanzas y Ventas", true);
        rCont.asignarPermiso("Reportes y BI", true);
        rCont.asignarPermiso("Notificaciones y Auditoría", true);
        rolesPermisos.add(rCont);

        // 4. Integraciones Externas (según wireframe Integraciones externas.pdf)
        integracionesExternas.add(new IntegracionExterna(
                "INT-01", "DIAN Factura Electrónica", "API REST v2 • Sincronización diaria",
                "Facturación", true, "https://api.dian.gov.co/v2/cpe/sync", "sk_live_9f83a2184c2",
                "Producción", "Hoy, 14:15"
        ));
        integracionesExternas.add(new IntegracionExterna(
                "INT-02", "WhatsApp Business Cloud", "Envío automático de citas y fórmulas",
                "Comunicaciones", true, "https://graph.facebook.com/v19.0/waba/messages", "waba_meta_88203b",
                "Producción", "Hoy, 13:50"
        ));
        integracionesExternas.add(new IntegracionExterna(
                "INT-03", "Pasarela de Pagos (Wompi)", "Cobros en línea y datáfono integrado",
                "Pagos", true, "https://api.wompi.co/v1/transactions", "pub_prod_77192a",
                "Producción", "Hoy, 12:30"
        ));
        integracionesExternas.add(new IntegracionExterna(
                "INT-04", "Laboratorio Clínico IDEXX", "Recepción automática de hemogramas",
                "Laboratorio", false, "tcp://192.168.1.180:5000", "idexx_token_00",
                "Sandbox / Pruebas", "No conectado"
        ));
        integracionesExternas.add(new IntegracionExterna(
                "INT-05", "Copia de Respaldo Cloud (AWS S3)", "Backup automático diario a las 03:00 AM",
                "Cloud", true, "s3://happypets-backups-bucket/daily", "aws_s3_key_live_4492",
                "Producción", "Hoy, 03:00 AM"
        ));

        // 5. Módulo de IA (según wireframe MODULO IA.pdf)
        configuracionModuloIA = new ConfiguracionModuloIA(
                true, true, true, false, "HappyPet-Core-v1.8",
                "Eres un asistente inteligente para triaje clínico, diagnóstico preliminar sugerido y respuesta rápida al cliente de HappyPets."
        );

        // 6. Tickets de Soporte
        ticketsSoporte.add(new TicketSoporte(
                "#TCK-501", "Calibración de integración IDEXX", "Integración", "Media", "Cerrado",
                "rtorres@happypets.com", LocalDateTime.now().minusDays(5),
                "Verificación de puerto serial y recepción de hemogramas en red local.",
                "Equipo conectado a switch dedicado y puerto 5000 abierto en firewall."
        ));
        ticketsSoporte.add(new TicketSoporte(
                "#TCK-502", "Renovación Certificado Digital DIAN", "Facturación", "Alta", "Cerrado",
                "palarcon@happypets.com", LocalDateTime.now().minusDays(2),
                "Actualización del archivo .p12 con validez hasta 2028.",
                "Certificado cargado satisfactoriamente en almacén encriptado."
        ));

        // 7. Diagnóstico Técnico del Sistema
        diagnosticoSistema = new DiagnosticoSistema();
    }

    public ConfiguracionClinica getConfiguracionClinica() {
        return configuracionClinica;
    }

    public void guardarConfiguracionClinica(ConfiguracionClinica cfg) {
        if (cfg != null) {
            this.configuracionClinica = cfg;
            registrarLogAuditoria(new LogAuditoria(
                    "#EV-" + (1043 + logsAuditoria.size()), "Actualización de Configuración",
                    "admin_user", LocalDateTime.now(), "127.0.0.1", "ÉXITO",
                    "Parámetros institucionales y de clínica actualizados correctamente"
            ));
        }
    }

    public List<Usuario> getUsuariosSistema() {
        return new ArrayList<>(usuariosSistema);
    }

    public List<Usuario> buscarUsuariosSistema(String termino, String filtroRol, String filtroEstado) {
        List<Usuario> res = new ArrayList<>();
        String q = termino != null ? termino.trim().toLowerCase() : "";
        for (Usuario u : usuariosSistema) {
            boolean coincideTermino = q.isEmpty() ||
                    u.getNombreCompleto().toLowerCase().contains(q) ||
                    u.getUsername().toLowerCase().contains(q) ||
                    u.getCorreo().toLowerCase().contains(q) ||
                    u.getEspecialidadArea().toLowerCase().contains(q);

            boolean coincideRol = filtroRol == null || filtroRol.isEmpty() ||
                    "Todos".equalsIgnoreCase(filtroRol) || "Rol: Todos".equalsIgnoreCase(filtroRol) ||
                    u.getRol().equalsIgnoreCase(filtroRol);

            boolean coincideEstado = filtroEstado == null || filtroEstado.isEmpty() ||
                    "Todos".equalsIgnoreCase(filtroEstado) || "Estado: Todos".equalsIgnoreCase(filtroEstado) ||
                    u.getEstado().equalsIgnoreCase(filtroEstado);

            if (coincideTermino && coincideRol && coincideEstado) {
                res.add(u);
            }
        }
        return res;
    }

    public void guardarUsuarioSistema(Usuario u) {
        if (u == null) return;
        boolean encontrado = false;
        for (int i = 0; i < usuariosSistema.size(); i++) {
            if (usuariosSistema.get(i).getUsername().equalsIgnoreCase(u.getUsername())) {
                usuariosSistema.set(i, u);
                encontrado = true;
                break;
            }
        }
        if (!encontrado) {
            usuariosSistema.add(u);
        }
        registrarLogAuditoria(new LogAuditoria(
                "#EV-" + (1043 + logsAuditoria.size()), "Gestión de Usuario " + u.getUsername(),
                "admin_user", LocalDateTime.now(), "127.0.0.1", "ÉXITO",
                (encontrado ? "Modificación" : "Creación") + " de usuario en el sistema con rol " + u.getRol()
        ));
    }

    public void eliminarUsuarioSistema(String username) {
        if (username != null) {
            usuariosSistema.removeIf(u -> u.getUsername().equalsIgnoreCase(username));
            registrarLogAuditoria(new LogAuditoria(
                    "#EV-" + (1043 + logsAuditoria.size()), "Baja de Usuario " + username,
                    "admin_user", LocalDateTime.now(), "127.0.0.1", "ÉXITO",
                    "Usuario dado de baja en la nómina del sistema"
            ));
        }
    }

    public List<RolPermiso> getRolesPermisos() {
        return new ArrayList<>(rolesPermisos);
    }

    public void guardarRolPermiso(RolPermiso rol) {
        if (rol == null) return;
        boolean encontrado = false;
        for (int i = 0; i < rolesPermisos.size(); i++) {
            if (rolesPermisos.get(i).getIdRol().equalsIgnoreCase(rol.getIdRol())) {
                rolesPermisos.set(i, rol);
                encontrado = true;
                break;
            }
        }
        if (!encontrado) {
            rolesPermisos.add(rol);
        }
    }

    public List<IntegracionExterna> getIntegracionesExternas() {
        return new ArrayList<>(integracionesExternas);
    }

    public void guardarIntegracionExterna(IntegracionExterna inte) {
        if (inte == null) return;
        for (int i = 0; i < integracionesExternas.size(); i++) {
            if (integracionesExternas.get(i).getId().equalsIgnoreCase(inte.getId())) {
                integracionesExternas.set(i, inte);
                break;
            }
        }
        registrarLogAuditoria(new LogAuditoria(
                "#EV-" + (1043 + logsAuditoria.size()), "Configuración de Integración " + inte.getNombre(),
                "admin_user", LocalDateTime.now(), "127.0.0.1", "ÉXITO",
                "Actualización de credenciales y estado para " + inte.getNombre()
        ));
    }

    public void conmutarEstadoIntegracion(String idIntegracion) {
        if (idIntegracion == null) return;
        for (IntegracionExterna inte : integracionesExternas) {
            if (inte.getId().equalsIgnoreCase(idIntegracion)) {
                inte.setActiva(!inte.isActiva());
                inte.setUltimaSincronizacion(inte.isActiva() ? "Hoy, recién sincronizado" : "Desconectado");
                registrarLogAuditoria(new LogAuditoria(
                        "#EV-" + (1043 + logsAuditoria.size()), "Cambio de Estado de Integración",
                        "admin_user", LocalDateTime.now(), "127.0.0.1", "ÉXITO",
                        inte.getNombre() + " " + (inte.isActiva() ? "Activada" : "Desactivada")
                ));
                break;
            }
        }
    }

    public ConfiguracionModuloIA getConfiguracionModuloIA() {
        return configuracionModuloIA;
    }

    public void guardarConfiguracionModuloIA(ConfiguracionModuloIA ia) {
        if (ia != null) {
            this.configuracionModuloIA = ia;
            registrarLogAuditoria(new LogAuditoria(
                    "#EV-" + (1043 + logsAuditoria.size()), "Ajuste de Módulo IA",
                    "admin_user", LocalDateTime.now(), "127.0.0.1", "ÉXITO",
                    "Toggles y prompts de IA clínica actualizados (" + ia.getModeloActual() + ")"
            ));
        }
    }

    public String simularDiagnosticoTriajeIA(String especie, String raza, int edadMeses, String motivo, String sintomas) {
        String sintLower = (sintomas != null ? sintomas.toLowerCase() : "") + " " + (motivo != null ? motivo.toLowerCase() : "");
        String nivelUrgencia;
        String colorCodigo;
        String sugerencias;

        if (sintLower.contains("convulsi") || sintLower.contains("desmay") || sintLower.contains("hemorragia") ||
                sintLower.contains("asfixia") || sintLower.contains("atropell") || sintLower.contains("intoxic")) {
            nivelUrgencia = "CÓDIGO ROJO - EMERGENCIA VITAL";
            colorCodigo = "Atención inmediata (0 min espera)";
            sugerencias = "1. Estabilización cardiorrespiratoria de urgencia y oxigenoterapia.\n"
                    + "2. Acceso venoso permeable con catéter 20G/22G y fluidoterapia de rescate.\n"
                    + "3. Monitorización continua de ECG, SpO2 y presión arterial no invasiva (PANI).\n"
                    + "4. Toma inmediata de panel electrolítico y hematocrito de urgencia.";
        } else if (sintLower.contains("vomit") || sintLower.contains("diarrea") || sintLower.contains("dolor") ||
                sintLower.contains("fiebre") || sintLower.contains("tos") || sintLower.contains("letargo") || sintLower.contains("renguea")) {
            nivelUrgencia = "CÓDIGO AMARILLO - URGENCIA CLÍNICA PRIORITARIA";
            colorCodigo = "Atención en menos de 20-30 minutos";
            sugerencias = "1. Triage físico completo: temperatura rectal, palpación abdominal y auscultación.\n"
                    + "2. Evaluación de estado de hidratación (tiempo de llenado capilar y pliegue cutáneo).\n"
                    + "3. Exámenes sugeridos: Hemograma automatizado IDEXX + Bioquímica hepatorrenal.\n"
                    + "4. Considerar ecografía FAST abdominal según grado de molestia a la palpación.";
        } else {
            nivelUrgencia = "CÓDIGO VERDE - CONSULTA PROGRAMADA / ESTABLE";
            colorCodigo = "Atención estándar por orden de llegada";
            sugerencias = "1. Revisión preventiva integral y control de signos vitales.\n"
                    + "2. Verificación de cartilla de vacunación y esquema antiparasitario.\n"
                    + "3. Asesoramiento nutricional y recomendaciones de manejo preventivo.";
        }

        return "═══════════════════════════════════════════════════════════════════\n"
                + "  ANALIZADOR CLÍNICO INTELIGENTE · " + configuracionModuloIA.getModeloActual() + "\n"
                + "═══════════════════════════════════════════════════════════════════\n"
                + "• Paciente: " + (especie != null ? especie : "Canino") + " (" + (raza != null ? raza : "Mestizo") + ") · " + edadMeses + " meses\n"
                + "• Clasificación de Triaje: " + nivelUrgencia + "\n"
                + "• Protocolo de Tiempo: " + colorCodigo + "\n\n"
                + "DIAGNÓSTICOS DIFERENCIALES SUGERIDOS:\n"
                + (sintLower.contains("tos") ? " - Traqueobronquitis infecciosa canina / Tos de las perreras\n - Colapso traqueal o cardiomegalia incipiente\n" : "")
                + (sintLower.contains("vomit") ? " - Gastroenteritis aguda dietaria o infecciosa\n - Ingestión de cuerpo extraño gastrointestinal\n" : "")
                + (sintLower.contains("diarrea") ? " - Parasitosis intestinal / Giardiasis / Parvovirosis\n" : "")
                + " - Cuadro inespecífico en fase inicial a correlacionar con analítica\n\n"
                + "PLAN DE ACCIÓN SUGERIDO POR IA:\n" + sugerencias + "\n"
                + "═══════════════════════════════════════════════════════════════════";
    }

    public List<TicketSoporte> getTicketsSoporte() {
        return new ArrayList<>(ticketsSoporte);
    }

    public void crearTicketSoporte(TicketSoporte ticket) {
        if (ticket != null) {
            if (ticket.getIdTicket() == null || ticket.getIdTicket().isEmpty()) {
                ticket.setIdTicket("#TCK-" + (501 + ticketsSoporte.size()));
            }
            ticketsSoporte.add(0, ticket);
            diagnosticoSistema.setTicketsPendientes((int) ticketsSoporte.stream().filter(t -> !"Cerrado".equalsIgnoreCase(t.getEstado())).count());
            registrarLogAuditoria(new LogAuditoria(
                    "#EV-" + (1043 + logsAuditoria.size()), "Ticket de Soporte " + ticket.getIdTicket(),
                    ticket.getUsuarioReporta() != null ? ticket.getUsuarioReporta() : "admin_user",
                    LocalDateTime.now(), "127.0.0.1", "ÉXITO",
                    "Registro de ticket por incidencia en categoría " + ticket.getCategoria()
            ));
        }
    }

    public DiagnosticoSistema getDiagnosticoSistema() {
        return diagnosticoSistema;
    }
}


