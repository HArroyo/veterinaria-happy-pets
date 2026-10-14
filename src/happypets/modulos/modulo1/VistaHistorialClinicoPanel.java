package happypets.modulos.modulo1;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import happypets.data.RepositorioVeterinaria;
import happypets.model.Cliente;
import happypets.model.ConsultaClinica;
import happypets.model.Mascota;
import happypets.ui.Iconos;
import happypets.ui.Ui;

/**
 * Submódulo 1.2: Historial Clínico de Mascotas.
 * Arquitectura Master-Detail Clínico con Timeline y Expediente Evolutivo en Vivo.
 * - Panel Izquierdo: Directorio de pacientes con búsqueda reactiva + Timeline cronológico de consultas.
 * - Panel Derecho: Ficha ejecutiva con micro-KPIs biológicos + Visor completo de expediente clínico en tiempo real.
 */
public class VistaHistorialClinicoPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private static final Color COLOR_BORDE = Ui.BORDE_SUAVE;
    private static final Color COLOR_TURQUESA = Ui.TURQUESA;
    private static final Color COLOR_TURQUESA_OSCURO = Ui.TURQUESA_OSCURO;
    private static final Color COLOR_TURQUESA_SUAVE = Ui.TURQUESA_SUAVE;
    private static final Color COLOR_TITULO = Ui.TEXTO_TITULO;
    private static final Color COLOR_MUTED = Ui.TEXTO_MUTED;

    private final RepositorioVeterinaria repo = RepositorioVeterinaria.getInstancia();

    // Estado del modelo
    private List<Mascota> listaMascotasCompleta = new ArrayList<>();
    private List<Mascota> listaMascotasFiltrada = new ArrayList<>();
    private Mascota mascotaSeleccionada;
    private Cliente clienteSeleccionado;

    private List<ConsultaClinica> listaConsultasMascota = new ArrayList<>();
    private List<ConsultaClinica> listaConsultasFiltradas = new ArrayList<>();
    private ConsultaClinica consultaSeleccionada;

    // Componentes del Panel Izquierdo (Master)
    private JTextField txtBuscarMascota;
    private JComboBox<String> cbFiltroEspecie;
    private JLabel lblContadorMascotas;
    private DefaultListModel<Mascota> modeloListaMascotas;
    private JList<Mascota> listaMascotas;

    private JLabel lblContadorConsultas;
    private JComboBox<String> cbPeriodo;
    private DefaultListModel<ConsultaClinica> modeloListaConsultas;
    private JList<ConsultaClinica> listaConsultas;

    // Componentes del Panel Derecho (Detail - Ficha Ejecutiva)
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private JPanel panelAvatarMascota;
    private JLabel lblPetNombre;
    private JLabel lblPetCodigoBadge;
    private JLabel lblPetEspecieRaza;
    private JLabel lblPetEdadSexo;
    private JLabel lblPetDuenoInfo;

    // Micro-KPIs Biológicos
    private JLabel lblKpiPesoVal;
    private JLabel lblKpiTempVal;
    private JLabel lblKpiVacunasVal;
    private JLabel lblKpiAlergiasVal;

    // Componentes del Expediente Clínico de la Consulta
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private JPanel cardExpediente;
    private JLabel lblExpedienteCodigo;
    private JLabel lblExpedienteFecha;
    private JLabel lblExpedienteVeterinario;
    private JLabel lblExpedienteEstadoBadge;
    private JButton btnEditarConsulta;

    // Bloques del expediente
    private JLabel lblTriajePeso;
    private JLabel lblTriajeTemp;
    private JLabel lblExpMotivo;
    private JLabel lblExpSintomas;
    private JLabel lblExpDiagnostico;
    private JLabel lblExpTratamiento;
    private JLabel lblExpObservaciones;

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private JPanel panelSinConsulta;

    /**
     * Constructor de VistaHistorialClinicoPanel.
     * Inicializa los componentes gráficos, configura layouts, asigna estilos
     * institucionales y carga los datos desde el repositorio central.
     */
    public VistaHistorialClinicoPanel() {
        setLayout(new BorderLayout(0, 10));
        setOpaque(false);
        setBorder(new EmptyBorder(12, 16, 14, 16));

        // 1. Cabecera superior
        add(crearCabeceraVista(), BorderLayout.NORTH);

        // 2. Panel central dividido (Master-Detail 350px / resto)
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel centralSplit = new JPanel(new BorderLayout(14, 0));
        centralSplit.setOpaque(false);

        // Columna Izquierda: Directorio de Pacientes + Timeline de Consultas
        centralSplit.add(crearPanelIzquierdoMaster(), BorderLayout.WEST);

        // Columna Derecha: Expediente Clínico Evolutivo en Vivo
        centralSplit.add(crearPanelDerechoDossier(), BorderLayout.CENTER);

        add(centralSplit, BorderLayout.CENTER);

        // Cargar datos iniciales
        recargarMascotas();
        Optional<Mascota> optRocky = repo.buscarMascotaPorCodigoONombre("VET-0091");
        if (optRocky.isPresent()) {
            cargarMascota(optRocky.get());
        } else if (!listaMascotasCompleta.isEmpty()) {
            cargarMascota(listaMascotasCompleta.get(0));
        }
    }

    // =========================================================================
    // 1. CABECERA DE LA VISTA
    // =========================================================================
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private JPanel crearCabeceraVista() {
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel cab = new JPanel(new BorderLayout());
        cab.setOpaque(false);
        cab.setPreferredSize(new Dimension(0, 42));

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel izq = new JPanel();
        izq.setOpaque(false);
        izq.setLayout(new BoxLayout(izq, BoxLayout.Y_AXIS));

        JLabel lblTit = new JLabel("Historial Clínico de Mascotas");
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblTit.setForeground(COLOR_TITULO);

        JLabel lblSub = new JLabel("Módulo 1.2 · Expediente evolutivo de consultas, diagnósticos, constantes vitales y tratamientos veterinarios");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblSub.setForeground(COLOR_MUTED);

        izq.add(lblTit);
        izq.add(Box.createVerticalStrut(2));
        izq.add(lblSub);
        cab.add(izq, BorderLayout.CENTER);

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 2));
        der.setOpaque(false);

        JButton btnImprimir = crearBotonAccion("Imprimir Historial", false);
        btnImprimir.setIcon(Iconos.crearIconoImprimir(13, COLOR_TURQUESA));
        btnImprimir.setIconTextGap(6);
        btnImprimir.addActionListener(e -> exportarHistorialDocumento());

        JButton btnExportarPdf = crearBotonAccion("Exportar Reporte", false);
        btnExportarPdf.setIcon(Iconos.crearIconoDescargar(13, COLOR_TURQUESA));
        btnExportarPdf.setIconTextGap(6);
        btnExportarPdf.addActionListener(e -> exportarHistorialDocumento());

        JButton btnNuevaConsulta = crearBotonAccion("+ Nueva Consulta", true);
        btnNuevaConsulta.setIcon(Iconos.crearIconoEstetoscopio(14, Color.WHITE));
        btnNuevaConsulta.setIconTextGap(6);
        btnNuevaConsulta.addActionListener(e -> abrirNuevaConsultaDialog());

        der.add(btnImprimir);
        der.add(btnExportarPdf);
        der.add(btnNuevaConsulta);
        cab.add(der, BorderLayout.EAST);

        return cab;
    }

    // =========================================================================
    // 2. PANEL IZQUIERDO (MASTER): DIRECTORIO + TIMELINE
    // =========================================================================
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private JPanel crearPanelIzquierdoMaster() {
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel master = new JPanel(new BorderLayout(0, 8)) {
            private static final long serialVersionUID = 1L;
            @Override
            /**
             * Renderizado gráfico personalizado en Java2D.
             * Activa Antialiasing para bordes suaves y realiza el cálculo geométrico preciso.
             */
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(COLOR_BORDE);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        master.setOpaque(false);
        master.setPreferredSize(new Dimension(350, 0));
        master.setBorder(new EmptyBorder(12, 12, 12, 12));

        // Sub-panel superior: Directorio de Pacientes
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel panelPacientes = new JPanel(new BorderLayout(0, 6));
        panelPacientes.setOpaque(false);

        // Cabecera del directorio
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel topDirectorio = new JPanel(new BorderLayout());
        topDirectorio.setOpaque(false);

        JLabel lblTitPac = new JLabel("DIRECTORIO DE PACIENTES");
        lblTitPac.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblTitPac.setForeground(COLOR_MUTED);
        topDirectorio.add(lblTitPac, BorderLayout.WEST);

        lblContadorMascotas = new JLabel("0 pacientes");
        lblContadorMascotas.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblContadorMascotas.setForeground(COLOR_MUTED);
        topDirectorio.add(lblContadorMascotas, BorderLayout.EAST);
        panelPacientes.add(topDirectorio, BorderLayout.NORTH);

        // Barra de búsqueda y filtro por especie
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel panelFiltros = new JPanel(new BorderLayout(6, 0));
        panelFiltros.setOpaque(false);

        txtBuscarMascota = new JTextField();
        txtBuscarMascota.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        txtBuscarMascota.setPreferredSize(new Dimension(190, 28));
        txtBuscarMascota.setToolTipText("Buscar por nombre, código o raza...");
        txtBuscarMascota.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDE, 1),
                new EmptyBorder(2, 6, 2, 6)
        ));
        txtBuscarMascota.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { filtrarMascotas(); }
            @Override public void removeUpdate(DocumentEvent e) { filtrarMascotas(); }
            @Override public void changedUpdate(DocumentEvent e) { filtrarMascotas(); }
        });
        panelFiltros.add(txtBuscarMascota, BorderLayout.CENTER);

        cbFiltroEspecie = new JComboBox<>(new String[]{"Todas", "Caninos", "Felinos"});
        cbFiltroEspecie.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        cbFiltroEspecie.setPreferredSize(new Dimension(86, 28));
        cbFiltroEspecie.setBackground(Color.WHITE);
        cbFiltroEspecie.addActionListener(e -> filtrarMascotas());
        panelFiltros.add(cbFiltroEspecie, BorderLayout.EAST);
        panelPacientes.add(panelFiltros, BorderLayout.CENTER);

        // Lista de pacientes
        modeloListaMascotas = new DefaultListModel<>();
        listaMascotas = new JList<>(modeloListaMascotas);
        listaMascotas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaMascotas.setCellRenderer(new MascotaCellRenderer());
        listaMascotas.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Mascota m = listaMascotas.getSelectedValue();
                if (m != null && (mascotaSeleccionada == null || !m.getCodigo().equals(mascotaSeleccionada.getCodigo()))) {
                    seleccionarMascotaInterno(m);
                }
            }
        });

        JScrollPane scrollPacientes = new JScrollPane(listaMascotas);
        scrollPacientes.setPreferredSize(new Dimension(320, 180));
        scrollPacientes.setBorder(BorderFactory.createLineBorder(COLOR_BORDE, 1));
        scrollPacientes.getVerticalScrollBar().setUnitIncrement(10);
        panelPacientes.add(scrollPacientes, BorderLayout.SOUTH);

        master.add(panelPacientes, BorderLayout.NORTH);

        // Sub-panel central: Timeline de Consultas del paciente
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel panelTimeline = new JPanel(new BorderLayout(0, 6));
        panelTimeline.setOpaque(false);
        panelTimeline.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, COLOR_BORDE));

        // Cabecera del timeline
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel topTimeline = new JPanel(new BorderLayout());
        topTimeline.setOpaque(false);
        topTimeline.setBorder(new EmptyBorder(8, 0, 2, 0));

        JLabel lblTitTime = new JLabel("LÍNEA DE TIEMPO · VISITAS");
        lblTitTime.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblTitTime.setForeground(COLOR_MUTED);
        topTimeline.add(lblTitTime, BorderLayout.WEST);

        lblContadorConsultas = new JLabel("0 visitas");
        lblContadorConsultas.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblContadorConsultas.setForeground(COLOR_MUTED);
        topTimeline.add(lblContadorConsultas, BorderLayout.EAST);

        // Filtro por período
        cbPeriodo = new JComboBox<>(new String[]{
                "Todas las fechas",
                "Último mes",
                "Últimos 3 meses",
                "Últimos 6 meses",
                "Último año"
        });
        cbPeriodo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        cbPeriodo.setPreferredSize(new Dimension(320, 26));
        cbPeriodo.setBackground(Color.WHITE);
        cbPeriodo.addActionListener(e -> filtrarConsultasTimeline());

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel headerTimeline = new JPanel();
        headerTimeline.setOpaque(false);
        headerTimeline.setLayout(new BoxLayout(headerTimeline, BoxLayout.Y_AXIS));
        headerTimeline.add(topTimeline);
        headerTimeline.add(Box.createVerticalStrut(4));
        headerTimeline.add(cbPeriodo);
        panelTimeline.add(headerTimeline, BorderLayout.NORTH);

        // Lista de consultas (Timeline)
        modeloListaConsultas = new DefaultListModel<>();
        listaConsultas = new JList<>(modeloListaConsultas);
        listaConsultas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaConsultas.setCellRenderer(new ConsultaTimelineCellRenderer());
        listaConsultas.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                ConsultaClinica c = listaConsultas.getSelectedValue();
                if (c != null) {
                    mostrarDetalleConsultaEnVivo(c);
                }
            }
        });

        JScrollPane scrollTimeline = new JScrollPane(listaConsultas);
        scrollTimeline.setBorder(BorderFactory.createLineBorder(COLOR_BORDE, 1));
        scrollTimeline.getVerticalScrollBar().setUnitIncrement(12);
        panelTimeline.add(scrollTimeline, BorderLayout.CENTER);

        master.add(panelTimeline, BorderLayout.CENTER);

        return master;
    }

    // =========================================================================
    // 3. PANEL DERECHO (DETAIL): DOSSIER CLÍNICO INTEGRAL EN VIVO
    // =========================================================================
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private JPanel crearPanelDerechoDossier() {
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel dossier = new JPanel(new BorderLayout(0, 10));
        dossier.setOpaque(false);

        // Tarjeta 1 (Norte): Ficha Ejecutiva del Paciente & Micro-KPIs Biológicos
        dossier.add(crearFichaEjecutivaPaciente(), BorderLayout.NORTH);

        // Tarjeta 2 (Centro): Expediente Clínico de la Consulta Seleccionada
        dossier.add(crearCardExpedienteConsulta(), BorderLayout.CENTER);

        return dossier;
    }

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private JPanel crearFichaEjecutivaPaciente() {
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel card = new JPanel(new BorderLayout(0, 8)) {
            private static final long serialVersionUID = 1L;
            @Override
            /**
             * Renderizado gráfico personalizado en Java2D.
             * Activa Antialiasing para bordes suaves y realiza el cálculo geométrico preciso.
             */
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(COLOR_BORDE);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(12, 16, 12, 16));

        // Parte Superior de la Ficha: Avatar circular + Datos Identificatorios
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel headerInfo = new JPanel(new BorderLayout(14, 0));
        headerInfo.setOpaque(false);

        // Avatar circular grande (50px)
        panelAvatarMascota = crearAvatarCircular(50, "P", COLOR_TURQUESA_SUAVE, COLOR_TURQUESA_OSCURO);
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel avatarWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        avatarWrapper.setOpaque(false);
        avatarWrapper.add(panelAvatarMascota);
        headerInfo.add(avatarWrapper, BorderLayout.WEST);

        // Textos del paciente
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel datosTxt = new JPanel();
        datosTxt.setOpaque(false);
        datosTxt.setLayout(new BoxLayout(datosTxt, BoxLayout.Y_AXIS));

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel filaTitulo = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filaTitulo.setOpaque(false);
        filaTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblPetNombre = new JLabel("Nombre Mascota");
        lblPetNombre.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblPetNombre.setForeground(COLOR_TITULO);
        filaTitulo.add(lblPetNombre);

        lblPetCodigoBadge = crearBadge("VET-0000", new Color(241, 245, 249), COLOR_TITULO);
        filaTitulo.add(lblPetCodigoBadge);

        lblPetEspecieRaza = crearBadge("Especie · Raza", COLOR_TURQUESA_SUAVE, COLOR_TURQUESA_OSCURO);
        filaTitulo.add(lblPetEspecieRaza);

        lblPetEdadSexo = new JLabel("3 años · Macho");
        lblPetEdadSexo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblPetEdadSexo.setForeground(COLOR_MUTED);
        filaTitulo.add(lblPetEdadSexo);

        lblPetDuenoInfo = new JLabel("Propietario Responsable: - · Tel: -");
        lblPetDuenoInfo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblPetDuenoInfo.setForeground(new Color(71, 85, 105));
        lblPetDuenoInfo.setAlignmentX(Component.LEFT_ALIGNMENT);

        datosTxt.add(filaTitulo);
        datosTxt.add(Box.createVerticalStrut(4));
        datosTxt.add(lblPetDuenoInfo);
        headerInfo.add(datosTxt, BorderLayout.CENTER);

        card.add(headerInfo, BorderLayout.NORTH);

        // Parte Inferior de la Ficha: 4 Micro-KPIs Biológicos
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel filaKpis = new JPanel(new GridLayout(1, 4, 10, 0));
        filaKpis.setOpaque(false);
        filaKpis.setBorder(new EmptyBorder(8, 0, 0, 0));

        lblKpiPesoVal = new JLabel("0.0 kg");
        lblKpiTempVal = new JLabel("38.5 °C");
        lblKpiVacunasVal = new JLabel("Al día");
        lblKpiAlergiasVal = new JLabel("Ninguna");

        filaKpis.add(crearItemKpi("PESO REGISTRADO", lblKpiPesoVal, "Último control clínico", new Color(240, 253, 250), COLOR_TURQUESA));
        filaKpis.add(crearItemKpi("TEMP. CORPORAL", lblKpiTempVal, "Rango fisiológico normal", new Color(238, 242, 255), new Color(79, 70, 229)));
        filaKpis.add(crearItemKpi("PLAN VACUNAL", lblKpiVacunasVal, "Inmunización preventiva", new Color(240, 253, 244), new Color(22, 163, 74)));
        filaKpis.add(crearItemKpi("ALERGIAS / ALERTA", lblKpiAlergiasVal, "Condición clínica", new Color(254, 242, 242), new Color(220, 38, 38)));

        card.add(filaKpis, BorderLayout.CENTER);

        return card;
    }

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private JPanel crearItemKpi(String etiqueta, JLabel lblValor, String sub, Color fondo, Color acento) {
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel kpi = new JPanel(new BorderLayout(4, 2)) {
            private static final long serialVersionUID = 1L;
            @Override
            /**
             * Renderizado gráfico personalizado en Java2D.
             * Activa Antialiasing para bordes suaves y realiza el cálculo geométrico preciso.
             */
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(fondo);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(new Color(acento.getRed(), acento.getGreen(), acento.getBlue(), 60));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        kpi.setOpaque(false);
        kpi.setBorder(new EmptyBorder(6, 10, 6, 10));

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel txt = new JPanel();
        txt.setOpaque(false);
        txt.setLayout(new BoxLayout(txt, BoxLayout.Y_AXIS));

        JLabel lblE = new JLabel(etiqueta);
        lblE.setFont(new Font("Segoe UI", Font.BOLD, 9));
        lblE.setForeground(COLOR_MUTED);

        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblValor.setForeground(COLOR_TITULO);

        JLabel lblS = new JLabel(sub);
        lblS.setFont(new Font("Segoe UI", Font.PLAIN, 9));
        lblS.setForeground(COLOR_MUTED);

        txt.add(lblE);
        txt.add(Box.createVerticalStrut(1));
        txt.add(lblValor);
        txt.add(Box.createVerticalStrut(1));
        txt.add(lblS);
        kpi.add(txt, BorderLayout.CENTER);

        return kpi;
    }

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private JPanel crearCardExpedienteConsulta() {
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        cardExpediente = new JPanel(new BorderLayout(0, 10)) {
            private static final long serialVersionUID = 1L;
            @Override
            /**
             * Renderizado gráfico personalizado en Java2D.
             * Activa Antialiasing para bordes suaves y realiza el cálculo geométrico preciso.
             */
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(COLOR_BORDE);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        cardExpediente.setOpaque(false);
        cardExpediente.setBorder(new EmptyBorder(14, 16, 14, 16));

        // Cabecera del expediente
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel headerExp = new JPanel(new BorderLayout());
        headerExp.setOpaque(false);
        headerExp.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_BORDE));
        headerExp.setPreferredSize(new Dimension(0, 44));

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel izq = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        izq.setOpaque(false);

        JLabel lblIco = new JLabel(Iconos.crearIconoHistorial(18, COLOR_TURQUESA));
        izq.add(lblIco);

        lblExpedienteCodigo = new JLabel("Atención Médica HC-000");
        lblExpedienteCodigo.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblExpedienteCodigo.setForeground(COLOR_TITULO);
        izq.add(lblExpedienteCodigo);

        lblExpedienteFecha = new JLabel("· 10/01/2024");
        lblExpedienteFecha.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblExpedienteFecha.setForeground(COLOR_MUTED);
        izq.add(lblExpedienteFecha);

        lblExpedienteVeterinario = new JLabel("· Dr. R. Mendoza");
        lblExpedienteVeterinario.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblExpedienteVeterinario.setForeground(COLOR_MUTED);
        izq.add(lblExpedienteVeterinario);

        lblExpedienteEstadoBadge = crearBadge("Cerrada", new Color(220, 252, 231), new Color(22, 163, 74));
        izq.add(lblExpedienteEstadoBadge);

        headerExp.add(izq, BorderLayout.CENTER);

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 2));
        der.setOpaque(false);

        btnEditarConsulta = crearBotonAccion("Editar Atención", false);
        btnEditarConsulta.setIcon(Iconos.crearIconoEditar(12, COLOR_TURQUESA));
        btnEditarConsulta.setIconTextGap(4);
        btnEditarConsulta.addActionListener(e -> editarConsultaActual());
        der.add(btnEditarConsulta);

        JButton btnImprimirReceta = crearBotonAccion("Imprimir Receta", false);
        btnImprimirReceta.setIcon(Iconos.crearIconoImprimir(12, COLOR_TURQUESA));
        btnImprimirReceta.setIconTextGap(4);
        btnImprimirReceta.addActionListener(e -> exportarRecetaActual());
        der.add(btnImprimirReceta);

        headerExp.add(der, BorderLayout.EAST);
        cardExpediente.add(headerExp, BorderLayout.NORTH);

        // Cuerpo del expediente con scroll
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel cuerpo = new JPanel();
        cuerpo.setOpaque(false);
        cuerpo.setLayout(new BoxLayout(cuerpo, BoxLayout.Y_AXIS));
        cuerpo.setBorder(new EmptyBorder(10, 4, 10, 4));

        // Bloque 1: Triaje y Signos Vitales en la cita
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel filaTriaje = new JPanel(new GridLayout(1, 2, 12, 0));
        filaTriaje.setOpaque(false);
        filaTriaje.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        lblTriajePeso = new JLabel("28.4 kg");
        lblTriajeTemp = new JLabel("38.5 °C");
        filaTriaje.add(crearCajaDatoSimple("PESO EN CONSULTA", lblTriajePeso));
        filaTriaje.add(crearCajaDatoSimple("TEMPERATURA CORPORAL", lblTriajeTemp));
        cuerpo.add(filaTriaje);
        cuerpo.add(Box.createVerticalStrut(10));

        // Bloque 2: Motivo y Síntomas
        lblExpMotivo = new JLabel("-");
        lblExpMotivo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblExpMotivo.setForeground(COLOR_TITULO);

        lblExpSintomas = new JLabel("-");
        lblExpSintomas.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblExpSintomas.setForeground(new Color(51, 65, 85));

        cuerpo.add(crearBloqueClinico("MOTIVO DE LA ATENCIÓN Y SÍNTOMAS REPORTADOS", lblExpMotivo, lblExpSintomas, new Color(248, 250, 252), COLOR_BORDE));
        cuerpo.add(Box.createVerticalStrut(10));

        // Bloque 3: Diagnóstico Clínico
        lblExpDiagnostico = new JLabel("-");
        lblExpDiagnostico.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblExpDiagnostico.setForeground(new Color(15, 118, 110));

        cuerpo.add(crearBloqueClinico("DIAGNÓSTICO CLÍNICO VETERINARIO", lblExpDiagnostico, null, new Color(240, 253, 250), new Color(153, 246, 228)));
        cuerpo.add(Box.createVerticalStrut(10));

        // Bloque 4: Tratamiento y Prescripción Farmacológica
        lblExpTratamiento = new JLabel("-");
        lblExpTratamiento.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblExpTratamiento.setForeground(COLOR_TITULO);

        cuerpo.add(crearBloqueClinico("PLAN DE TRATAMIENTO Y PRESCRIPCIÓN FARMACOLÓGICA", lblExpTratamiento, null, new Color(254, 252, 232), new Color(254, 240, 138)));
        cuerpo.add(Box.createVerticalStrut(10));

        // Bloque 5: Observaciones y Evolución
        lblExpObservaciones = new JLabel("-");
        lblExpObservaciones.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblExpObservaciones.setForeground(COLOR_MUTED);

        cuerpo.add(crearBloqueClinico("OBSERVACIONES Y EVOLUCIÓN DEL PACIENTE", lblExpObservaciones, null, Color.WHITE, COLOR_BORDE));

        JScrollPane scrollCuerpo = new JScrollPane(cuerpo);
        scrollCuerpo.setBorder(null);
        scrollCuerpo.setOpaque(false);
        scrollCuerpo.getViewport().setOpaque(false);
        scrollCuerpo.getVerticalScrollBar().setUnitIncrement(14);
        cardExpediente.add(scrollCuerpo, BorderLayout.CENTER);

        // Panel alternativo: Estado vacío sin consulta
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        panelSinConsulta = new JPanel(new BorderLayout());
        panelSinConsulta.setOpaque(false);

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel centroVacio = new JPanel();
        centroVacio.setOpaque(false);
        centroVacio.setLayout(new BoxLayout(centroVacio, BoxLayout.Y_AXIS));

        JLabel lblIcoVacio = new JLabel(Iconos.crearIconoHistorial(40, COLOR_MUTED));
        lblIcoVacio.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblTitVacio = new JLabel("No hay consultas seleccionadas o registradas");
        lblTitVacio.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitVacio.setForeground(COLOR_TITULO);
        lblTitVacio.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSubVacio = new JLabel("Seleccione una visita en el panel izquierdo o registre una nueva consulta para este paciente.");
        lblSubVacio.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubVacio.setForeground(COLOR_MUTED);
        lblSubVacio.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btnAgregarPrimera = crearBotonAccion("+ Registrar Primera Consulta", true);
        btnAgregarPrimera.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnAgregarPrimera.addActionListener(e -> abrirNuevaConsultaDialog());

        centroVacio.add(Box.createVerticalGlue());
        centroVacio.add(lblIcoVacio);
        centroVacio.add(Box.createVerticalStrut(8));
        centroVacio.add(lblTitVacio);
        centroVacio.add(Box.createVerticalStrut(4));
        centroVacio.add(lblSubVacio);
        centroVacio.add(Box.createVerticalStrut(12));
        centroVacio.add(btnAgregarPrimera);
        centroVacio.add(Box.createVerticalGlue());

        panelSinConsulta.add(centroVacio, BorderLayout.CENTER);

        return cardExpediente;
    }

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private JPanel crearCajaDatoSimple(String titulo, JLabel valor) {
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel caja = new JPanel(new BorderLayout(4, 2)) {
            private static final long serialVersionUID = 1L;
            @Override
            /**
             * Renderizado gráfico personalizado en Java2D.
             * Activa Antialiasing para bordes suaves y realiza el cálculo geométrico preciso.
             */
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(248, 250, 252));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(COLOR_BORDE);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        caja.setOpaque(false);
        caja.setBorder(new EmptyBorder(6, 12, 6, 12));

        JLabel lblT = new JLabel(titulo);
        lblT.setFont(new Font("Segoe UI", Font.BOLD, 9));
        lblT.setForeground(COLOR_MUTED);

        valor.setFont(new Font("Segoe UI", Font.BOLD, 13));
        valor.setForeground(COLOR_TITULO);

        caja.add(lblT, BorderLayout.NORTH);
        caja.add(valor, BorderLayout.CENTER);
        return caja;
    }

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private JPanel crearBloqueClinico(String titulo, JLabel principal, JLabel secundario, Color fondo, Color borde) {
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel bloque = new JPanel(new BorderLayout(0, 4)) {
            private static final long serialVersionUID = 1L;
            @Override
            /**
             * Renderizado gráfico personalizado en Java2D.
             * Activa Antialiasing para bordes suaves y realiza el cálculo geométrico preciso.
             */
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(fondo);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(borde);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        bloque.setOpaque(false);
        bloque.setBorder(new EmptyBorder(8, 12, 8, 12));
        bloque.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        JLabel lblT = new JLabel(titulo);
        lblT.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblT.setForeground(COLOR_MUTED);
        bloque.add(lblT, BorderLayout.NORTH);

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel cuerpo = new JPanel();
        cuerpo.setOpaque(false);
        cuerpo.setLayout(new BoxLayout(cuerpo, BoxLayout.Y_AXIS));

        if (principal != null) {
            cuerpo.add(principal);
        }
        if (secundario != null) {
            cuerpo.add(Box.createVerticalStrut(2));
            cuerpo.add(secundario);
        }

        bloque.add(cuerpo, BorderLayout.CENTER);
        return bloque;
    }

    // =========================================================================
    // 4. LÓGICA DE DATOS Y CARGA
    // =========================================================================

    public void cargarMascota(Mascota m) {
        if (m == null) return;
        recargarMascotas();
        for (int i = 0; i < modeloListaMascotas.size(); i++) {
            if (modeloListaMascotas.get(i).getCodigo().equalsIgnoreCase(m.getCodigo())) {
                listaMascotas.setSelectedIndex(i);
                listaMascotas.ensureIndexIsVisible(i);
                return;
            }
        }
        seleccionarMascotaInterno(m);
    }

    private void recargarMascotas() {
        listaMascotasCompleta = repo.todasLasMascotas();
        filtrarMascotas();
    }

    /**
     * Aplica criterios de filtrado dinámico en memoria sobre los registros.
     * Evalúa texto de búsqueda (búsqueda predictiva) y filtros por categoría o estado.
     */
    private void filtrarMascotas() {
        String query = txtBuscarMascota != null ? txtBuscarMascota.getText().trim().toLowerCase() : "";
        String especie = cbFiltroEspecie != null ? (String) cbFiltroEspecie.getSelectedItem() : "Todas";

        listaMascotasFiltrada = listaMascotasCompleta.stream().filter(m -> {
            boolean coincideTexto = query.isEmpty()
                    || m.getNombre().toLowerCase().contains(query)
                    || m.getCodigo().toLowerCase().contains(query)
                    || (m.getRaza() != null && m.getRaza().toLowerCase().contains(query));

            boolean coincideEspecie = "Todas".equals(especie)
                    || ("Caninos".equals(especie) && "Canino".equalsIgnoreCase(m.getEspecie()))
                    || ("Felinos".equals(especie) && "Felino".equalsIgnoreCase(m.getEspecie()));

            return coincideTexto && coincideEspecie;
        }).collect(Collectors.toList());

        if (modeloListaMascotas != null) {
            modeloListaMascotas.clear();
            for (Mascota m : listaMascotasFiltrada) {
                modeloListaMascotas.addElement(m);
            }
            lblContadorMascotas.setText(listaMascotasFiltrada.size() + " pacientes");
        }
    }

    private void seleccionarMascotaInterno(Mascota m) {
        this.mascotaSeleccionada = m;
        this.clienteSeleccionado = repo.getClienteDeMascota(m.getCodigo()).orElse(null);

        // Actualizar datos de la Ficha Ejecutiva
        lblPetNombre.setText(m.getNombre());
        lblPetCodigoBadge.setText(m.getCodigo());
        lblPetEspecieRaza.setText(m.getEspecie() + " · " + m.getRaza());
        lblPetEdadSexo.setText(m.getEdadTexto() + " · " + m.getSexo());

        if (clienteSeleccionado != null) {
            lblPetDuenoInfo.setText("Propietario Responsable: " + clienteSeleccionado.getNombreCompleto()
                    + " (DNI: " + clienteSeleccionado.getNumeroDocumento() + " · Cel: " + clienteSeleccionado.getTelefonoPrincipal() + ")");
        } else {
            lblPetDuenoInfo.setText("Propietario Responsable: Sin titular registrado");
        }

        // Actualizar Avatar circular grande
        String inicial = m.getNombre() != null && !m.getNombre().isEmpty() ? m.getNombre().substring(0, 1).toUpperCase() : "P";
        actualizarAvatarCircular(panelAvatarMascota, inicial, COLOR_TURQUESA_SUAVE, COLOR_TURQUESA_OSCURO);

        // Cargar lista de consultas de la mascota
        listaConsultasMascota = repo.getConsultasPorMascota(m.getCodigo());

        // Actualizar Micro-KPIs biológicos
        lblKpiPesoVal.setText(String.format(java.util.Locale.US, "%.1f kg", m.getPesoActualKg()));
        lblKpiVacunasVal.setText(m.getPlanVacunal() != null ? m.getPlanVacunal() : "Al día");
        lblKpiAlergiasVal.setText(m.getAlergias() != null && !m.getAlergias().trim().isEmpty() ? m.getAlergias() : "Ninguna");

        if (!listaConsultasMascota.isEmpty()) {
            ConsultaClinica ultima = listaConsultasMascota.get(0);
            lblKpiTempVal.setText(String.format(java.util.Locale.US, "%.1f °C", ultima.getTemperaturaC()));
        } else {
            lblKpiTempVal.setText("38.5 °C");
        }

        filtrarConsultasTimeline();
    }

    /**
     * Aplica criterios de filtrado dinámico en memoria sobre los registros.
     * Evalúa texto de búsqueda (búsqueda predictiva) y filtros por categoría o estado.
     */
    private void filtrarConsultasTimeline() {
        modeloListaConsultas.clear();
        if (mascotaSeleccionada == null || listaConsultasMascota == null) {
            mostrarEstadoSinConsultas();
            return;
        }

        String periodo = cbPeriodo != null ? (String) cbPeriodo.getSelectedItem() : "Todas las fechas";
        LocalDate limite = null;
        LocalDate hoy = LocalDate.now();

        if ("Último mes".equals(periodo)) limite = hoy.minusMonths(1);
        else if ("Últimos 3 meses".equals(periodo)) limite = hoy.minusMonths(3);
        else if ("Últimos 6 meses".equals(periodo)) limite = hoy.minusMonths(6);
        else if ("Último año".equals(periodo)) limite = hoy.minusYears(1);

        listaConsultasFiltradas = new ArrayList<>();
        for (ConsultaClinica c : listaConsultasMascota) {
            if (limite == null || !c.getFecha().isBefore(limite)) {
                listaConsultasFiltradas.add(c);
                modeloListaConsultas.addElement(c);
            }
        }

        lblContadorConsultas.setText(listaConsultasFiltradas.size() + " visitas");

        if (!listaConsultasFiltradas.isEmpty()) {
            listaConsultas.setSelectedIndex(0);
            mostrarDetalleConsultaEnVivo(listaConsultasFiltradas.get(0));
        } else {
            mostrarEstadoSinConsultas();
        }
    }

    /**
     * Actualiza el panel lateral derecho (Live Detail Hub) con la información del registro seleccionado.
     * Si el argumento es nulo, restablece el panel mostrando el estado de selección vacía.
     */
    private void mostrarDetalleConsultaEnVivo(ConsultaClinica c) {
        this.consultaSeleccionada = c;
        if (c == null) {
            mostrarEstadoSinConsultas();
            return;
        }

        lblExpedienteCodigo.setText("Atención Médica " + c.getCodigo());
        lblExpedienteFecha.setText("· " + c.getFechaFormateada());
        lblExpedienteVeterinario.setText("· " + c.getVeterinario());
        lblExpedienteEstadoBadge.setText(c.getEstado());

        if ("Cerrada".equalsIgnoreCase(c.getEstado())) {
            lblExpedienteEstadoBadge.setBackground(new Color(220, 252, 231));
            lblExpedienteEstadoBadge.setForeground(new Color(22, 163, 74));
        } else if ("En Tratamiento".equalsIgnoreCase(c.getEstado())) {
            lblExpedienteEstadoBadge.setBackground(new Color(254, 243, 199));
            lblExpedienteEstadoBadge.setForeground(new Color(217, 119, 6));
        } else {
            lblExpedienteEstadoBadge.setBackground(new Color(238, 242, 255));
            lblExpedienteEstadoBadge.setForeground(new Color(79, 70, 229));
        }

        lblTriajePeso.setText(String.format(java.util.Locale.US, "%.1f kg", c.getPesoKg()));
        lblTriajeTemp.setText(String.format(java.util.Locale.US, "%.1f °C", c.getTemperaturaC()));

        lblExpMotivo.setText("Motivo: " + c.getMotivo());
        lblExpSintomas.setText("Síntomas observados: " + (c.getSintomas() != null && !c.getSintomas().isEmpty() ? c.getSintomas() : "Sin sintomatología reportada"));

        lblExpDiagnostico.setText(c.getDiagnostico() != null ? c.getDiagnostico() : "Diagnóstico en evaluación");
        lblExpTratamiento.setText(c.getTratamiento() != null ? c.getTratamiento() : "Sin tratamiento registrado");
        lblExpObservaciones.setText(c.getObservaciones() != null ? c.getObservaciones() : "Sin observaciones adicionales");

        cardExpediente.removeAll();
        cardExpediente.setLayout(new BorderLayout(0, 10));

        // Reconstruir
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel headerExp = new JPanel(new BorderLayout());
        headerExp.setOpaque(false);
        headerExp.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_BORDE));
        headerExp.setPreferredSize(new Dimension(0, 44));

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel izq = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        izq.setOpaque(false);
        izq.add(new JLabel(Iconos.crearIconoHistorial(18, COLOR_TURQUESA)));
        izq.add(lblExpedienteCodigo);
        izq.add(lblExpedienteFecha);
        izq.add(lblExpedienteVeterinario);
        izq.add(lblExpedienteEstadoBadge);
        headerExp.add(izq, BorderLayout.CENTER);

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 2));
        der.setOpaque(false);
        der.add(btnEditarConsulta);
        JButton btnImprimirReceta = crearBotonAccion("Imprimir Receta", false);
        btnImprimirReceta.setIcon(Iconos.crearIconoImprimir(12, COLOR_TURQUESA));
        btnImprimirReceta.setIconTextGap(4);
        btnImprimirReceta.addActionListener(e -> exportarRecetaActual());
        der.add(btnImprimirReceta);
        headerExp.add(der, BorderLayout.EAST);

        cardExpediente.add(headerExp, BorderLayout.NORTH);

        // Cuerpo con los bloques
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel cuerpo = new JPanel();
        cuerpo.setOpaque(false);
        cuerpo.setLayout(new BoxLayout(cuerpo, BoxLayout.Y_AXIS));
        cuerpo.setBorder(new EmptyBorder(10, 4, 10, 4));

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel filaTriaje = new JPanel(new GridLayout(1, 2, 12, 0));
        filaTriaje.setOpaque(false);
        filaTriaje.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        filaTriaje.add(crearCajaDatoSimple("PESO EN CONSULTA", lblTriajePeso));
        filaTriaje.add(crearCajaDatoSimple("TEMPERATURA CORPORAL", lblTriajeTemp));
        cuerpo.add(filaTriaje);
        cuerpo.add(Box.createVerticalStrut(10));

        cuerpo.add(crearBloqueClinico("MOTIVO DE LA ATENCIÓN Y SÍNTOMAS REPORTADOS", lblExpMotivo, lblExpSintomas, new Color(248, 250, 252), COLOR_BORDE));
        cuerpo.add(Box.createVerticalStrut(10));

        cuerpo.add(crearBloqueClinico("DIAGNÓSTICO CLÍNICO VETERINARIO", lblExpDiagnostico, null, new Color(240, 253, 250), new Color(153, 246, 228)));
        cuerpo.add(Box.createVerticalStrut(10));

        cuerpo.add(crearBloqueClinico("PLAN DE TRATAMIENTO Y PRESCRIPCIÓN FARMACOLÓGICA", lblExpTratamiento, null, new Color(254, 252, 232), new Color(254, 240, 138)));
        cuerpo.add(Box.createVerticalStrut(10));

        cuerpo.add(crearBloqueClinico("OBSERVACIONES Y EVOLUCIÓN DEL PACIENTE", lblExpObservaciones, null, Color.WHITE, COLOR_BORDE));

        JScrollPane scrollCuerpo = new JScrollPane(cuerpo);
        scrollCuerpo.setBorder(null);
        scrollCuerpo.setOpaque(false);
        scrollCuerpo.getViewport().setOpaque(false);
        scrollCuerpo.getVerticalScrollBar().setUnitIncrement(14);
        cardExpediente.add(scrollCuerpo, BorderLayout.CENTER);

        cardExpediente.revalidate();
        cardExpediente.repaint();
    }

    private void mostrarEstadoSinConsultas() {
        this.consultaSeleccionada = null;
        cardExpediente.removeAll();
        cardExpediente.setLayout(new BorderLayout());
        cardExpediente.add(panelSinConsulta, BorderLayout.CENTER);
        cardExpediente.revalidate();
        cardExpediente.repaint();
    }

    // =========================================================================
    // 5. ACCIONES Y DIÁLOGOS
    // =========================================================================

    private void abrirNuevaConsultaDialog() {
        if (mascotaSeleccionada == null) {
            JOptionPane.showMessageDialog(this, "Seleccione primero un paciente en el directorio.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        FormularioConsultaDialog dlg = new FormularioConsultaDialog(
                SwingUtilities.getWindowAncestor(this),
                mascotaSeleccionada,
                null,
                nuevaConsulta -> {
                    // Refrescar datos y seleccionar la nueva consulta
                    seleccionarMascotaInterno(mascotaSeleccionada);
                    for (int i = 0; i < modeloListaConsultas.size(); i++) {
                        if (modeloListaConsultas.get(i).getCodigo().equals(nuevaConsulta.getCodigo())) {
                            listaConsultas.setSelectedIndex(i);
                            listaConsultas.ensureIndexIsVisible(i);
                            break;
                        }
                    }
                    JOptionPane.showMessageDialog(this,
                            "Consulta " + nuevaConsulta.getCodigo() + " registrada exitosamente en el expediente.",
                            "Consulta Registrada", JOptionPane.INFORMATION_MESSAGE);
                }
        );
        dlg.setVisible(true);
    }

    private void editarConsultaActual() {
        if (consultaSeleccionada == null || mascotaSeleccionada == null) return;

        FormularioConsultaDialog dlg = new FormularioConsultaDialog(
                SwingUtilities.getWindowAncestor(this),
                mascotaSeleccionada,
                consultaSeleccionada,
                consultaActualizada -> {
                    seleccionarMascotaInterno(mascotaSeleccionada);
                    mostrarDetalleConsultaEnVivo(consultaActualizada);
                    JOptionPane.showMessageDialog(this,
                            "Consulta " + consultaActualizada.getCodigo() + " actualizada correctamente.",
                            "Atención Modificada", JOptionPane.INFORMATION_MESSAGE);
                }
        );
        dlg.setVisible(true);
    }

    private void exportarHistorialDocumento() {
        if (mascotaSeleccionada == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un paciente para generar el reporte de historial clínico.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<ConsultaClinica> consultas = repo.getConsultasPorMascota(mascotaSeleccionada.getCodigo());
        String[][] datos = new String[consultas.size()][5];
        for (int i = 0; i < consultas.size(); i++) {
            ConsultaClinica c = consultas.get(i);
            datos[i][0] = c.getCodigo();
            datos[i][1] = c.getFechaFormateada();
            datos[i][2] = c.getMotivo();
            datos[i][3] = c.getDiagnostico();
            datos[i][4] = c.getVeterinario();
        }

        Ui.mostrarVisorReporte(
                (java.awt.Frame) SwingUtilities.getWindowAncestor(this),
                "EXPEDIENTE CLÍNICO INTEGRAL VETERINARIO",
                "Paciente: " + mascotaSeleccionada.getNombre() + " (" + mascotaSeleccionada.getEspecieRaza() + ") | Código: " + mascotaSeleccionada.getCodigo(),
                "Propietario: " + (clienteSeleccionado != null ? clienteSeleccionado.getNombreCompleto() : "N/D") + " | Total Consultas: " + consultas.size(),
                new String[]{"Código", "Fecha", "Motivo", "Diagnóstico", "Veterinario"},
                datos,
                "ESTADO: PACIENTE CONTROLADO · HISTORIAL COMPLETO",
                "HistorialClinico_" + mascotaSeleccionada.getNombre()
        );
    }

    private void exportarRecetaActual() {
        if (consultaSeleccionada == null || mascotaSeleccionada == null) {
            JOptionPane.showMessageDialog(this, "Seleccione una consulta médica para imprimir su receta.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String[][] datos = new String[][]{
                {consultaSeleccionada.getCodigo(), consultaSeleccionada.getFechaFormateada(), consultaSeleccionada.getDiagnostico(), consultaSeleccionada.getTratamiento(), consultaSeleccionada.getVeterinario()}
        };

        Ui.mostrarVisorReporte(
                (java.awt.Frame) SwingUtilities.getWindowAncestor(this),
                "RECETA MÉDICA Y PLAN TERAPÉUTICO",
                "Paciente: " + mascotaSeleccionada.getNombre() + " | Propietario: " + (clienteSeleccionado != null ? clienteSeleccionado.getNombreCompleto() : "N/D"),
                "Fecha de emisión: " + consultaSeleccionada.getFechaFormateada() + " | Médico Tratante: " + consultaSeleccionada.getVeterinario(),
                new String[]{"Código", "Fecha", "Diagnóstico Clínico", "Prescripción / Medicamentos", "Veterinario"},
                datos,
                "INDICACIONES: CUMPLIR EL HORARIO DE DOSIS PRESCRITO · CONTROL EN 7 DÍAS",
                "Receta_" + consultaSeleccionada.getCodigo()
        );
    }

    // =========================================================================
    // 6. COMPONENTES GRÁFICOS PERSONALIZADOS (AVATARES Y RENDERERS)
    // =========================================================================

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private JPanel crearAvatarCircular(int diametro, String texto, Color fondo, Color textoColor) {
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel avatar = new JPanel() {
            private static final long serialVersionUID = 1L;
            @Override
            /**
             * Renderizado gráfico personalizado en Java2D.
             * Activa Antialiasing para bordes suaves y realiza el cálculo geométrico preciso.
             */
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int d = Math.min(getWidth(), getHeight()) - 2;
                int x = (getWidth() - d) / 2;
                int y = (getHeight() - d) / 2;
                Color bg = (Color) getClientProperty("fondo");
                if (bg == null) bg = fondo;
                Color fg = (Color) getClientProperty("textoColor");
                if (fg == null) fg = textoColor;
                String txt = (String) getClientProperty("texto");
                if (txt == null) txt = texto;

                g2.setColor(bg);
                g2.fillOval(x, y, d, d);
                g2.setColor(new Color(bg.getRed(), bg.getGreen(), bg.getBlue(), 60));
                g2.drawOval(x, y, d, d);
                g2.setColor(fg);
                g2.setFont(new Font("Segoe UI", Font.BOLD, diametro >= 40 ? 18 : 13));
                FontMetrics fm = g2.getFontMetrics();
                int tx = x + (d - fm.stringWidth(txt)) / 2;
                int ty = y + (d - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(txt, tx, ty);
                g2.dispose();
            }
        };
        avatar.setOpaque(false);
        avatar.setPreferredSize(new Dimension(diametro, diametro));
        avatar.setMinimumSize(new Dimension(diametro, diametro));
        avatar.setMaximumSize(new Dimension(diametro, diametro));
        avatar.putClientProperty("texto", texto);
        avatar.putClientProperty("fondo", fondo);
        avatar.putClientProperty("textoColor", textoColor);
        return avatar;
    }

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private void actualizarAvatarCircular(JPanel avatar, String texto, Color fondo, Color textoColor) {
        avatar.putClientProperty("texto", texto);
        avatar.putClientProperty("fondo", fondo);
        avatar.putClientProperty("textoColor", textoColor);
        avatar.repaint();
    }

    private JLabel crearBadge(String texto, Color fondo, Color textoColor) {
        JLabel badge = new JLabel(texto) {
            private static final long serialVersionUID = 1L;
            @Override
            /**
             * Renderizado gráfico personalizado en Java2D.
             * Activa Antialiasing para bordes suaves y realiza el cálculo geométrico preciso.
             */
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        badge.setFont(new Font("Segoe UI", Font.BOLD, 10));
        badge.setForeground(textoColor);
        badge.setBackground(fondo);
        badge.setOpaque(false);
        badge.setBorder(new EmptyBorder(3, 8, 3, 8));
        return badge;
    }

    private JButton crearBotonAccion(String texto, boolean primario) {
        JButton btn = new JButton(texto) {
            private static final long serialVersionUID = 1L;
            @Override
            /**
             * Renderizado gráfico personalizado en Java2D.
             * Activa Antialiasing para bordes suaves y realiza el cálculo geométrico preciso.
             */
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (primario) {
                    g2.setColor(getModel().isRollover() ? COLOR_TURQUESA_OSCURO : COLOR_TURQUESA);
                } else {
                    g2.setColor(getModel().isRollover() ? new Color(241, 245, 249) : Color.WHITE);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                if (!primario) {
                    g2.setColor(COLOR_BORDE);
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 6, 6);
                }
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btn.setForeground(primario ? Color.WHITE : COLOR_TITULO);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(btn.getPreferredSize().width + 16, 28));
        btn.setBorder(new EmptyBorder(4, 10, 4, 10));
        return btn;
    }

    // =========================================================================
    // 7. RENDERERS DE LISTA (MASCOTA & TIMELINE)
    // =========================================================================

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private class MascotaCellRenderer extends JPanel implements ListCellRenderer<Mascota> {
        private static final long serialVersionUID = 1L;
        private final JLabel lblAvatar;
        private final JLabel lblNombre;
        private final JLabel lblDetalle;
        private final JLabel lblDueno;

        public MascotaCellRenderer() {
            setLayout(new BorderLayout(8, 0));
            setBorder(new EmptyBorder(6, 8, 6, 8));
            setOpaque(true);

            lblAvatar = new JLabel("", SwingConstants.CENTER) {
                private static final long serialVersionUID = 1L;
                @Override
            /**
             * Renderizado gráfico personalizado en Java2D.
             * Activa Antialiasing para bordes suaves y realiza el cálculo geométrico preciso.
             */
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    int d = Math.min(getWidth(), getHeight()) - 2;
                    int x = (getWidth() - d) / 2;
                    int y = (getHeight() - d) / 2;
                    g2.setColor(COLOR_TURQUESA_SUAVE);
                    g2.fillOval(x, y, d, d);
                    g2.setColor(COLOR_TURQUESA_OSCURO);
                    g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
                    FontMetrics fm = g2.getFontMetrics();
                    String t = getText();
                    int tx = x + (d - fm.stringWidth(t)) / 2;
                    int ty = y + (d - fm.getHeight()) / 2 + fm.getAscent();
                    g2.drawString(t, tx, ty);
                    g2.dispose();
                }
            };
            lblAvatar.setPreferredSize(new Dimension(32, 32));
            lblAvatar.setMinimumSize(new Dimension(32, 32));
            lblAvatar.setMaximumSize(new Dimension(32, 32));
            lblAvatar.setOpaque(false);

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
            JPanel avatarWrap = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 2));
            avatarWrap.setOpaque(false);
            avatarWrap.add(lblAvatar);
            add(avatarWrap, BorderLayout.WEST);

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
            JPanel txt = new JPanel();
            txt.setOpaque(false);
            txt.setLayout(new BoxLayout(txt, BoxLayout.Y_AXIS));

            lblNombre = new JLabel();
            lblNombre.setFont(new Font("Segoe UI", Font.BOLD, 12));
            lblNombre.setForeground(COLOR_TITULO);

            lblDetalle = new JLabel();
            lblDetalle.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            lblDetalle.setForeground(COLOR_MUTED);

            lblDueno = new JLabel();
            lblDueno.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            lblDueno.setForeground(new Color(100, 116, 139));

            txt.add(lblNombre);
            txt.add(lblDetalle);
            txt.add(lblDueno);
            add(txt, BorderLayout.CENTER);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends Mascota> list, Mascota m, int index, boolean isSelected, boolean cellHasFocus) {
            if (m != null) {
                String ini = m.getNombre() != null && !m.getNombre().isEmpty() ? m.getNombre().substring(0, 1).toUpperCase() : "P";
                lblAvatar.setText(ini);
                lblNombre.setText(m.getNombre() + " (" + m.getCodigo() + ")");
                lblDetalle.setText(m.getEspecie() + " · " + m.getRaza());

                Cliente cl = repo.getClienteDeMascota(m.getCodigo()).orElse(null);
                lblDueno.setText("Dueño: " + (cl != null ? cl.getNombreCompleto() : "Sin asignar"));
            }

            if (isSelected) {
                setBackground(COLOR_TURQUESA_SUAVE);
                setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(COLOR_TURQUESA, 1),
                        new EmptyBorder(5, 7, 5, 7)
                ));
            } else {
                setBackground(Color.WHITE);
                setBorder(new EmptyBorder(6, 8, 6, 8));
            }
            return this;
        }
    }

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private class ConsultaTimelineCellRenderer extends JPanel implements ListCellRenderer<ConsultaClinica> {
        private static final long serialVersionUID = 1L;
        private final JLabel lblFecha;
        private final JLabel lblMotivo;
        private final JLabel lblVet;
        private final JLabel lblBadgeEstado;

        public ConsultaTimelineCellRenderer() {
            setLayout(new BorderLayout(8, 2));
            setBorder(new EmptyBorder(8, 10, 8, 10));
            setOpaque(true);

            // Icono nodo circular de fecha
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
            JPanel izq = new JPanel(new BorderLayout());
            izq.setOpaque(false);

            lblFecha = new JLabel("", SwingConstants.CENTER) {
                private static final long serialVersionUID = 1L;
                @Override
            /**
             * Renderizado gráfico personalizado en Java2D.
             * Activa Antialiasing para bordes suaves y realiza el cálculo geométrico preciso.
             */
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(241, 245, 249));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                    g2.setColor(COLOR_BORDE);
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 6, 6);
                    super.paintComponent(g2);
                    g2.dispose();
                }
            };
            lblFecha.setFont(new Font("Segoe UI", Font.BOLD, 10));
            lblFecha.setForeground(COLOR_TITULO);
            lblFecha.setPreferredSize(new Dimension(74, 26));
            lblFecha.setOpaque(false);
            izq.add(lblFecha, BorderLayout.NORTH);
            add(izq, BorderLayout.WEST);

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
            JPanel center = new JPanel();
            center.setOpaque(false);
            center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));

            lblMotivo = new JLabel();
            lblMotivo.setFont(new Font("Segoe UI", Font.BOLD, 12));
            lblMotivo.setForeground(COLOR_TITULO);

            lblVet = new JLabel();
            lblVet.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            lblVet.setForeground(COLOR_MUTED);

            center.add(lblMotivo);
            center.add(Box.createVerticalStrut(1));
            center.add(lblVet);
            add(center, BorderLayout.CENTER);

            lblBadgeEstado = crearBadge("Cerrada", new Color(220, 252, 231), new Color(22, 163, 74));
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
            JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 4));
            der.setOpaque(false);
            der.add(lblBadgeEstado);
            add(der, BorderLayout.EAST);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends ConsultaClinica> list, ConsultaClinica c, int index, boolean isSelected, boolean cellHasFocus) {
            if (c != null) {
                lblFecha.setText(c.getFechaFormateada());
                lblMotivo.setText(c.getCodigo() + " · " + c.getMotivo());
                lblVet.setText(c.getVeterinario());
                lblBadgeEstado.setText(c.getEstado());

                if ("Cerrada".equalsIgnoreCase(c.getEstado())) {
                    lblBadgeEstado.setBackground(new Color(220, 252, 231));
                    lblBadgeEstado.setForeground(new Color(22, 163, 74));
                } else if ("En Tratamiento".equalsIgnoreCase(c.getEstado())) {
                    lblBadgeEstado.setBackground(new Color(254, 243, 199));
                    lblBadgeEstado.setForeground(new Color(217, 119, 6));
                } else {
                    lblBadgeEstado.setBackground(new Color(238, 242, 255));
                    lblBadgeEstado.setForeground(new Color(79, 70, 229));
                }
            }

            if (isSelected) {
                setBackground(COLOR_TURQUESA_SUAVE);
                setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(COLOR_TURQUESA, 1),
                        new EmptyBorder(7, 9, 7, 9)
                ));
            } else {
                setBackground(Color.WHITE);
                setBorder(new EmptyBorder(8, 10, 8, 10));
            }
            return this;
        }
    }
}
