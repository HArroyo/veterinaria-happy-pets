package happypets.data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import happypets.model.Certificado;
import happypets.model.Cliente;
import happypets.model.ConsultaClinica;
import happypets.model.DocumentoMascota;
import happypets.model.Mascota;

/**
 * Almacén en memoria centralizado para clientes, mascotas, consultas y documentos.
 * Inicializado con los datos exactamente representados en los wireframes del proyecto.
 */
public class RepositorioVeterinaria {
    private static RepositorioVeterinaria instancia;

    private final List<Cliente> clientes = new ArrayList<>();
    private final List<ConsultaClinica> consultas = new ArrayList<>();
    private final List<DocumentoMascota> documentos = new ArrayList<>();

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
}
