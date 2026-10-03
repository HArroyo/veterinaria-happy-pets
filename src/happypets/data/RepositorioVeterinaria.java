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
}
