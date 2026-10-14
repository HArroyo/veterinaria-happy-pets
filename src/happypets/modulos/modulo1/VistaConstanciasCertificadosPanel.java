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
import javax.swing.JButton;
import javax.swing.JComboBox;
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
import happypets.model.Certificado;
import happypets.model.Cliente;
import happypets.model.DocumentoMascota;
import happypets.model.Mascota;
import happypets.ui.Iconos;
import happypets.ui.Ui;

/**
 * Submódulo 1.3: Constancias y Certificados Médicos Oficiales.
 * Arquitectura Master-Detail con Catálogo Documental y Visor Oficial Digital en Vivo.
 * - Panel Izquierdo: Directorio de pacientes con búsqueda reactiva + Catálogo de documentos del paciente.
 * - Panel Derecho: Ficha ejecutiva con micro-KPIs documentales + Visor oficial en vivo de hoja membretada con firma y sello.
 */
public class VistaConstanciasCertificadosPanel extends JPanel {
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

    private List<DocumentoMascota> listaDocumentosMascota = new ArrayList<>();
    private List<DocumentoMascota> listaDocumentosFiltrados = new ArrayList<>();
    private DocumentoMascota documentoSeleccionado;

    // Componentes del Panel Izquierdo (Master)
    private JTextField txtBuscarMascota;
    private JComboBox<String> cbFiltroEspecie;
    private JLabel lblContadorMascotas;
    private DefaultListModel<Mascota> modeloListaMascotas;
    private JList<Mascota> listaMascotas;

    private JLabel lblContadorDocumentos;
    private JComboBox<String> cbTipoDocumento;
    private DefaultListModel<DocumentoMascota> modeloListaDocumentos;
    private JList<DocumentoMascota> listaDocumentos;

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

    // Micro-KPIs Documentales
    private JLabel lblKpiTotalVal;
    private JLabel lblKpiEstadoVal;
    private JLabel lblKpiUltimaFechaVal;
    private JLabel lblKpiFacultativoVal;

    // Componentes del Visor Oficial Membretado
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private JPanel cardVisorDocumento;
    private JLabel lblCertTituloOficial;
    private JLabel lblCertNumeroRegistro;
    private JLabel lblCertFechaEmision;
    private JLabel lblCertEstadoBadge;

    // Filiación del paciente y propietario en el certificado
    private JLabel lblFiliacionPaciente;
    private JLabel lblFiliacionDetallePet;
    private JLabel lblFiliacionPropietario;
    private JLabel lblFiliacionDetalleDni;

    // Dictamen clínico y vigencia
    private JLabel lblCertDictamenTexto;
    private JLabel lblCertMotivoDestino;
    private JLabel lblCertVigencia;
    private JLabel lblCertVeterinarioFirma;
    private JLabel lblCertColegiaturaFirma;

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private JPanel panelSinDocumento;

    /**
     * Constructor de VistaConstanciasCertificadosPanel.
     * Inicializa los componentes gráficos, configura layouts, asigna estilos
     * institucionales y carga los datos desde el repositorio central.
     */
    public VistaConstanciasCertificadosPanel() {
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

        // Columna Izquierda: Directorio de Pacientes + Catálogo Documental
        centralSplit.add(crearPanelIzquierdoMaster(), BorderLayout.WEST);

        // Columna Derecha: Visor de Hoja Membretada Oficial en Vivo
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

        JLabel lblTit = new JLabel("Constancias y Certificados Médicos");
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblTit.setForeground(COLOR_TITULO);

        JLabel lblSub = new JLabel("Módulo 1.3 · Emisión, consulta, validación sanitaria y descarga de certificados veterinarios oficiales");
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

        JButton btnImprimir = crearBotonAccion("Imprimir Listado", false);
        btnImprimir.setIcon(Iconos.crearIconoImprimir(13, COLOR_TURQUESA));
        btnImprimir.setIconTextGap(6);
        btnImprimir.addActionListener(e -> exportarListadoDocumentos());

        JButton btnExportar = crearBotonAccion("Exportar Reporte", false);
        btnExportar.setIcon(Iconos.crearIconoDescargar(13, COLOR_TURQUESA));
        btnExportar.setIconTextGap(6);
        btnExportar.addActionListener(e -> exportarListadoDocumentos());

        JButton btnEmitir = crearBotonAccion("+ Emitir Nuevo Certificado", true);
        btnEmitir.setIcon(Iconos.crearIconoCertificado(14, Color.WHITE));
        btnEmitir.setIconTextGap(6);
        btnEmitir.addActionListener(e -> abrirEmitirCertificadoDialog());

        der.add(btnImprimir);
        der.add(btnExportar);
        der.add(btnEmitir);
        cab.add(der, BorderLayout.EAST);

        return cab;
    }

    // =========================================================================
    // 2. PANEL IZQUIERDO (MASTER): DIRECTORIO + CATÁLOGO DOCUMENTAL
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

        // Sub-panel central: Catálogo de Documentos Emitidos
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel panelDocumentos = new JPanel(new BorderLayout(0, 6));
        panelDocumentos.setOpaque(false);
        panelDocumentos.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, COLOR_BORDE));

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel topDocs = new JPanel(new BorderLayout());
        topDocs.setOpaque(false);
        topDocs.setBorder(new EmptyBorder(8, 0, 2, 0));

        JLabel lblTitDocs = new JLabel("DOCUMENTOS Y CERTIFICADOS EMITIDOS");
        lblTitDocs.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblTitDocs.setForeground(COLOR_MUTED);
        topDocs.add(lblTitDocs, BorderLayout.WEST);

        lblContadorDocumentos = new JLabel("0 constancias");
        lblContadorDocumentos.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblContadorDocumentos.setForeground(COLOR_MUTED);
        topDocs.add(lblContadorDocumentos, BorderLayout.EAST);

        // Filtro por tipo de documento
        cbTipoDocumento = new JComboBox<>(new String[]{
                "Todos los documentos",
                "Tarjeta de vacunación",
                "Certificado de vacunación",
                "Certificado de salud",
                "Historial clínico",
                "Historial de recetas médicas",
                "Autorización quirúrgica"
        });
        cbTipoDocumento.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        cbTipoDocumento.setPreferredSize(new Dimension(320, 26));
        cbTipoDocumento.setBackground(Color.WHITE);
        cbTipoDocumento.addActionListener(e -> filtrarDocumentosCatalogo());

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel headerDocs = new JPanel();
        headerDocs.setOpaque(false);
        headerDocs.setLayout(new BoxLayout(headerDocs, BoxLayout.Y_AXIS));
        headerDocs.add(topDocs);
        headerDocs.add(Box.createVerticalStrut(4));
        headerDocs.add(cbTipoDocumento);
        panelDocumentos.add(headerDocs, BorderLayout.NORTH);

        // Lista de documentos
        modeloListaDocumentos = new DefaultListModel<>();
        listaDocumentos = new JList<>(modeloListaDocumentos);
        listaDocumentos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaDocumentos.setCellRenderer(new DocumentoCellRenderer());
        listaDocumentos.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                DocumentoMascota doc = listaDocumentos.getSelectedValue();
                if (doc != null) {
                    mostrarDetalleDocumentoEnVivo(doc);
                }
            }
        });

        JScrollPane scrollDocs = new JScrollPane(listaDocumentos);
        scrollDocs.setBorder(BorderFactory.createLineBorder(COLOR_BORDE, 1));
        scrollDocs.getVerticalScrollBar().setUnitIncrement(12);
        panelDocumentos.add(scrollDocs, BorderLayout.CENTER);

        master.add(panelDocumentos, BorderLayout.CENTER);

        return master;
    }

    // =========================================================================
    // 3. PANEL DERECHO (DETAIL): FICHA EJECUTIVA + VISOR OFICIAL EN VIVO
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

        // Tarjeta 1 (Norte): Ficha Ejecutiva del Paciente & Micro-KPIs Documentales
        dossier.add(crearFichaEjecutivaPaciente(), BorderLayout.NORTH);

        // Tarjeta 2 (Centro): Hoja Membretada Oficial del Certificado
        dossier.add(crearCardVisorDocumentoOficial(), BorderLayout.CENTER);

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

        // Parte Inferior de la Ficha: 4 Micro-KPIs Documentales
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel filaKpis = new JPanel(new GridLayout(1, 4, 10, 0));
        filaKpis.setOpaque(false);
        filaKpis.setBorder(new EmptyBorder(8, 0, 0, 0));

        lblKpiTotalVal = new JLabel("4 docs");
        lblKpiEstadoVal = new JLabel("VIGENTE");
        lblKpiUltimaFechaVal = new JLabel("10/05/2024");
        lblKpiFacultativoVal = new JLabel("Dr. R. Mendoza");

        filaKpis.add(crearItemKpi("EXPEDIENTE DIGITAL", lblKpiTotalVal, "Constancias registradas", new Color(240, 253, 250), COLOR_TURQUESA));
        filaKpis.add(crearItemKpi("ESTADO SANITARIO", lblKpiEstadoVal, "Plan vacunal al día", new Color(240, 253, 244), new Color(22, 163, 74)));
        filaKpis.add(crearItemKpi("ÚLTIMA CERTIFICACIÓN", lblKpiUltimaFechaVal, "Fecha de emisión oficial", new Color(238, 242, 255), new Color(79, 70, 229)));
        filaKpis.add(crearItemKpi("MÉDICO VETERINARIO", lblKpiFacultativoVal, "Colegiatura CMVP 8492", new Color(254, 243, 199), new Color(217, 119, 6)));

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
    private JPanel crearCardVisorDocumentoOficial() {
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        cardVisorDocumento = new JPanel(new BorderLayout(0, 10)) {
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
        cardVisorDocumento.setOpaque(false);
        cardVisorDocumento.setBorder(new EmptyBorder(14, 16, 14, 16));

        // Inicializar labels del visor
        lblCertTituloOficial = new JLabel("CERTIFICADO MÉDICO VETERINARIO OFICIAL");
        lblCertNumeroRegistro = new JLabel("N.º DE REGISTRO: CERT-2024-0001");
        lblCertFechaEmision = new JLabel("10/05/2024");
        lblCertEstadoBadge = crearBadge("OFICIAL · VIGENTE", new Color(220, 252, 231), new Color(22, 163, 74));

        lblFiliacionPaciente = new JLabel("-");
        lblFiliacionDetallePet = new JLabel("-");
        lblFiliacionPropietario = new JLabel("-");
        lblFiliacionDetalleDni = new JLabel("-");

        lblCertDictamenTexto = new JLabel("-");
        lblCertMotivoDestino = new JLabel("-");
        lblCertVigencia = new JLabel("-");
        lblCertVeterinarioFirma = new JLabel("Dr. Roberto Mendoza Chávez");
        lblCertColegiaturaFirma = new JLabel("Médico Veterinario Colegiado · CMVP 8492");

        // Panel alternativo: Estado vacío sin documento
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        panelSinDocumento = new JPanel(new BorderLayout());
        panelSinDocumento.setOpaque(false);

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel centroVacio = new JPanel();
        centroVacio.setOpaque(false);
        centroVacio.setLayout(new BoxLayout(centroVacio, BoxLayout.Y_AXIS));

        JLabel lblIcoVacio = new JLabel(Iconos.crearIconoCertificado(40, COLOR_MUTED));
        lblIcoVacio.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblTitVacio = new JLabel("No hay certificados o constancias seleccionadas");
        lblTitVacio.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitVacio.setForeground(COLOR_TITULO);
        lblTitVacio.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSubVacio = new JLabel("Seleccione un documento en el catálogo izquierdo o emita un nuevo certificado para este paciente.");
        lblSubVacio.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubVacio.setForeground(COLOR_MUTED);
        lblSubVacio.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btnEmitirPrimer = crearBotonAccion("+ Emitir Primer Certificado", true);
        btnEmitirPrimer.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnEmitirPrimer.addActionListener(e -> abrirEmitirCertificadoDialog());

        centroVacio.add(Box.createVerticalGlue());
        centroVacio.add(lblIcoVacio);
        centroVacio.add(Box.createVerticalStrut(8));
        centroVacio.add(lblTitVacio);
        centroVacio.add(Box.createVerticalStrut(4));
        centroVacio.add(lblSubVacio);
        centroVacio.add(Box.createVerticalStrut(12));
        centroVacio.add(btnEmitirPrimer);
        centroVacio.add(Box.createVerticalGlue());

        panelSinDocumento.add(centroVacio, BorderLayout.CENTER);

        return cardVisorDocumento;
    }

    /**
     * Actualiza el panel lateral derecho (Live Detail Hub) con la información del registro seleccionado.
     * Si el argumento es nulo, restablece el panel mostrando el estado de selección vacía.
     */
    private void mostrarDetalleDocumentoEnVivo(DocumentoMascota doc) {
        this.documentoSeleccionado = doc;
        if (doc == null) {
            mostrarEstadoSinDocumentos();
            return;
        }

        cardVisorDocumento.removeAll();
        cardVisorDocumento.setLayout(new BorderLayout(0, 10));

        // 1. Cabecera del visor (Membrete de la clínica + Botones de impresión)
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel headerMembrete = new JPanel(new BorderLayout());
        headerMembrete.setOpaque(false);
        headerMembrete.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_BORDE));
        headerMembrete.setPreferredSize(new Dimension(0, 48));

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel membreteTxt = new JPanel();
        membreteTxt.setOpaque(false);
        membreteTxt.setLayout(new BoxLayout(membreteTxt, BoxLayout.Y_AXIS));

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel logoRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        logoRow.setOpaque(false);
        logoRow.add(new JLabel(Iconos.crearIconoCertificado(18, COLOR_TURQUESA)));
        JLabel lblClinica = new JLabel("CLÍNICA VETERINARIA HAPPY PETS S.A.C.");
        lblClinica.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblClinica.setForeground(COLOR_TURQUESA_OSCURO);
        logoRow.add(lblClinica);
        logoRow.add(Box.createHorizontalStrut(6));
        logoRow.add(crearBadge("RUC: 20601234567", new Color(241, 245, 249), COLOR_MUTED));

        JLabel lblLegal = new JLabel("Registro Sanitario SENASA / CMVP #8492 · Av. San Borja Sur 482 - Lima · Tel: (01) 432-9980");
        lblLegal.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblLegal.setForeground(COLOR_MUTED);

        membreteTxt.add(logoRow);
        membreteTxt.add(Box.createVerticalStrut(2));
        membreteTxt.add(lblLegal);
        headerMembrete.add(membreteTxt, BorderLayout.CENTER);

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel derBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 4));
        derBtns.setOpaque(false);

        JButton btnImprimirCert = crearBotonAccion("Imprimir Certificado", true);
        btnImprimirCert.setIcon(Iconos.crearIconoImprimir(12, Color.WHITE));
        btnImprimirCert.setIconTextGap(6);
        btnImprimirCert.addActionListener(e -> imprimirCertificadoSeleccionado());
        derBtns.add(btnImprimirCert);

        JButton btnExportarPdf = crearBotonAccion("Descargar PDF", false);
        btnExportarPdf.setIcon(Iconos.crearIconoDescargar(12, COLOR_TURQUESA));
        btnExportarPdf.setIconTextGap(6);
        btnExportarPdf.addActionListener(e -> imprimirCertificadoSeleccionado());
        derBtns.add(btnExportarPdf);

        headerMembrete.add(derBtns, BorderLayout.EAST);
        cardVisorDocumento.add(headerMembrete, BorderLayout.NORTH);

        // 2. Cuerpo de la Hoja Oficial del Certificado (Scrollable)
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel hoja = new JPanel();
        hoja.setOpaque(false);
        hoja.setLayout(new BoxLayout(hoja, BoxLayout.Y_AXIS));
        hoja.setBorder(new EmptyBorder(10, 16, 10, 16));

        // Título del Certificado
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel bannerTitulo = new JPanel(new BorderLayout());
        bannerTitulo.setOpaque(false);
        bannerTitulo.setBorder(new EmptyBorder(4, 0, 12, 0));

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel bannerCentro = new JPanel();
        bannerCentro.setOpaque(false);
        bannerCentro.setLayout(new BoxLayout(bannerCentro, BoxLayout.Y_AXIS));

        String tituloCert = doc.getTipo().toUpperCase();
        if (!tituloCert.contains("CERTIFICADO") && !tituloCert.contains("CONSTANCIA")) {
            tituloCert = "CERTIFICADO OFICIAL: " + tituloCert;
        }

        lblCertTituloOficial.setText(tituloCert);
        lblCertTituloOficial.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblCertTituloOficial.setForeground(COLOR_TITULO);
        lblCertTituloOficial.setAlignmentX(Component.CENTER_ALIGNMENT);

        String numCert = "DOC-0001";
        String motivoDest = "Control Sanitario Periódico";
        String vetEmisor = doc.getVeterinarioEmisor() != null ? doc.getVeterinarioEmisor() : "Dr. Roberto Mendoza Chávez (CMVP 8492)";

        if (doc instanceof Certificado cert) {
            if (cert.getNumeroCertificado() != null) numCert = cert.getNumeroCertificado();
            if (cert.getMotivoDestino() != null) motivoDest = cert.getMotivoDestino();
        } else if (doc.getCodigo() != null) {
            numCert = doc.getCodigo();
        }

        lblCertNumeroRegistro.setText("CÓDIGO DE REGISTRO DOCUMENTAL: " + numCert + "   ·   EMISIÓN: " + doc.getFechaActualizacionFormateada());
        lblCertNumeroRegistro.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblCertNumeroRegistro.setForeground(COLOR_MUTED);
        lblCertNumeroRegistro.setAlignmentX(Component.CENTER_ALIGNMENT);

        bannerCentro.add(lblCertTituloOficial);
        bannerCentro.add(Box.createVerticalStrut(3));
        bannerCentro.add(lblCertNumeroRegistro);
        bannerTitulo.add(bannerCentro, BorderLayout.CENTER);

        hoja.add(bannerTitulo);

        // Cuadro 1: Filiación del Paciente y Propietario (2 Columnas)
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel filaFiliacion = new JPanel(new GridLayout(1, 2, 12, 0));
        filaFiliacion.setOpaque(false);
        filaFiliacion.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        String petTxt = mascotaSeleccionada != null ? mascotaSeleccionada.getNombre() + " (" + mascotaSeleccionada.getCodigo() + ")" : "Paciente N/D";
        String petSub = mascotaSeleccionada != null ? mascotaSeleccionada.getEspecie() + " · " + mascotaSeleccionada.getRaza() + " · " + mascotaSeleccionada.getSexo() + " (" + mascotaSeleccionada.getEdadTexto() + ")" : "-";
        filaFiliacion.add(crearCajaFiliacion("DATOS DEL PACIENTE VETERINARIO", petTxt, petSub));

        String cliTxt = clienteSeleccionado != null ? clienteSeleccionado.getNombreCompleto() : "Titular N/D";
        String cliSub = clienteSeleccionado != null ? "DNI: " + clienteSeleccionado.getNumeroDocumento() + " · Tel: " + clienteSeleccionado.getTelefonoPrincipal() : "-";
        filaFiliacion.add(crearCajaFiliacion("PROPIETARIO / TITULAR RESPONSABLE", cliTxt, cliSub));

        hoja.add(filaFiliacion);
        hoja.add(Box.createVerticalStrut(10));

        // Cuadro 2: Dictamen Sanitario Oficial
        String dictamen = doc.getDescripcion();
        if (dictamen == null || dictamen.trim().isEmpty()) {
            dictamen = "Se certifica que el ejemplar ha sido examinado clínicamente, encontrándose en óptimas condiciones de salud e higiene.";
        }
        hoja.add(crearBloqueDictamenSanitario("CERTIFICACIÓN CLÍNICA Y DICTAMEN FACULTATIVO", dictamen));
        hoja.add(Box.createVerticalStrut(10));

        // Cuadro 3: Vigencia, Motivo y Cláusulas
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel filaVigencia = new JPanel(new GridLayout(1, 2, 12, 0));
        filaVigencia.setOpaque(false);
        filaVigencia.setMaximumSize(new Dimension(Integer.MAX_VALUE, 65));

        filaVigencia.add(crearCajaFiliacion("MOTIVO / DESTINO OFICIAL", motivoDest, "Válido ante autoridades sanitarias, aerolíneas y entidades municipales"));
        String vigenciaStr = doc.getFechaVencimiento() != null ? "Válido hasta: " + doc.getFechaVencimiento().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "Válido por 30 días calendario";
        filaVigencia.add(crearCajaFiliacion("VIGENCIA DEL DOCUMENTO", vigenciaStr, "Certificación formal respaldada por Historia Clínica"));
        hoja.add(filaVigencia);
        hoja.add(Box.createVerticalStrut(12));

        // Cuadro 4: Sello y Firma Digital del Médico Colegiado
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel panelFirma = new JPanel(new BorderLayout());
        panelFirma.setOpaque(false);
        panelFirma.setBorder(new EmptyBorder(8, 0, 4, 0));

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel selloCentro = new JPanel();
        selloCentro.setOpaque(false);
        selloCentro.setLayout(new BoxLayout(selloCentro, BoxLayout.Y_AXIS));

        JLabel lblLineaFirma = new JLabel("________________________________________________");
        lblLineaFirma.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblLineaFirma.setForeground(new Color(148, 163, 184));
        lblLineaFirma.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblCertVeterinarioFirma.setText(vetEmisor);
        lblCertVeterinarioFirma.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblCertVeterinarioFirma.setForeground(COLOR_TITULO);
        lblCertVeterinarioFirma.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblCertColegiaturaFirma.setText("Médico Veterinario Colegiado · CMVP #8492");
        lblCertColegiaturaFirma.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblCertColegiaturaFirma.setForeground(COLOR_MUTED);
        lblCertColegiaturaFirma.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblValidezBadge = crearBadge("DOCUMENTO OFICIAL DIGITALIZADO · VÁLIDO Y VIGENTE", new Color(220, 252, 231), new Color(22, 163, 74));
        lblValidezBadge.setAlignmentX(Component.CENTER_ALIGNMENT);

        selloCentro.add(lblLineaFirma);
        selloCentro.add(Box.createVerticalStrut(3));
        selloCentro.add(lblCertVeterinarioFirma);
        selloCentro.add(Box.createVerticalStrut(1));
        selloCentro.add(lblCertColegiaturaFirma);
        selloCentro.add(Box.createVerticalStrut(8));
        selloCentro.add(lblValidezBadge);

        panelFirma.add(selloCentro, BorderLayout.CENTER);
        hoja.add(panelFirma);

        JScrollPane scrollHoja = new JScrollPane(hoja);
        scrollHoja.setBorder(null);
        scrollHoja.setOpaque(false);
        scrollHoja.getViewport().setOpaque(false);
        scrollHoja.getVerticalScrollBar().setUnitIncrement(14);
        cardVisorDocumento.add(scrollHoja, BorderLayout.CENTER);

        cardVisorDocumento.revalidate();
        cardVisorDocumento.repaint();
    }

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private JPanel crearCajaFiliacion(String titulo, String principal, String secundario) {
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
        caja.setBorder(new EmptyBorder(8, 12, 8, 12));

        JLabel lblT = new JLabel(titulo);
        lblT.setFont(new Font("Segoe UI", Font.BOLD, 9));
        lblT.setForeground(COLOR_MUTED);

        JLabel lblP = new JLabel(principal);
        lblP.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblP.setForeground(COLOR_TITULO);

        JLabel lblS = new JLabel(secundario);
        lblS.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblS.setForeground(new Color(71, 85, 105));

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel cuerpo = new JPanel();
        cuerpo.setOpaque(false);
        cuerpo.setLayout(new BoxLayout(cuerpo, BoxLayout.Y_AXIS));
        cuerpo.add(lblP);
        cuerpo.add(Box.createVerticalStrut(2));
        cuerpo.add(lblS);

        caja.add(lblT, BorderLayout.NORTH);
        caja.add(cuerpo, BorderLayout.CENTER);
        return caja;
    }

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private JPanel crearBloqueDictamenSanitario(String titulo, String texto) {
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel bloque = new JPanel(new BorderLayout(0, 6)) {
            private static final long serialVersionUID = 1L;
            @Override
            /**
             * Renderizado gráfico personalizado en Java2D.
             * Activa Antialiasing para bordes suaves y realiza el cálculo geométrico preciso.
             */
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(240, 253, 250));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(new Color(153, 246, 228));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        bloque.setOpaque(false);
        bloque.setBorder(new EmptyBorder(10, 14, 10, 14));
        bloque.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        JLabel lblT = new JLabel(titulo);
        lblT.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblT.setForeground(new Color(15, 118, 110));
        bloque.add(lblT, BorderLayout.NORTH);

        JLabel lblTexto = new JLabel("<html><body style='width: 580px; font-family: Segoe UI; font-size: 11px; color: #0f172a; line-height: 1.4;'>" + texto + "</body></html>");
        bloque.add(lblTexto, BorderLayout.CENTER);

        return bloque;
    }

    private void mostrarEstadoSinDocumentos() {
        this.documentoSeleccionado = null;
        cardVisorDocumento.removeAll();
        cardVisorDocumento.setLayout(new BorderLayout());
        cardVisorDocumento.add(panelSinDocumento, BorderLayout.CENTER);
        cardVisorDocumento.revalidate();
        cardVisorDocumento.repaint();
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

        // Actualizar Avatar circular grande dinámico
        String inicial = m.getNombre() != null && !m.getNombre().isEmpty() ? m.getNombre().substring(0, 1).toUpperCase() : "P";
        actualizarAvatarCircular(panelAvatarMascota, inicial, COLOR_TURQUESA_SUAVE, COLOR_TURQUESA_OSCURO);

        // Cargar lista de documentos de la mascota
        listaDocumentosMascota = repo.getDocumentosPorMascota(m.getCodigo());

        // Actualizar Micro-KPIs documentales
        lblKpiTotalVal.setText(listaDocumentosMascota.size() + " constancias");
        lblKpiEstadoVal.setText("VIGENTE");

        if (!listaDocumentosMascota.isEmpty()) {
            DocumentoMascota ultimo = listaDocumentosMascota.get(listaDocumentosMascota.size() - 1);
            lblKpiUltimaFechaVal.setText(ultimo.getFechaActualizacionFormateada());
            if (ultimo.getVeterinarioEmisor() != null) {
                lblKpiFacultativoVal.setText(ultimo.getVeterinarioEmisor());
            } else {
                lblKpiFacultativoVal.setText("Dr. R. Mendoza");
            }
        } else {
            lblKpiUltimaFechaVal.setText("-");
            lblKpiFacultativoVal.setText("-");
        }

        filtrarDocumentosCatalogo();
    }

    /**
     * Aplica criterios de filtrado dinámico en memoria sobre los registros.
     * Evalúa texto de búsqueda (búsqueda predictiva) y filtros por categoría o estado.
     */
    private void filtrarDocumentosCatalogo() {
        modeloListaDocumentos.clear();
        if (mascotaSeleccionada == null || listaDocumentosMascota == null) {
            mostrarEstadoSinDocumentos();
            return;
        }

        String filtroTipo = cbTipoDocumento != null ? (String) cbTipoDocumento.getSelectedItem() : "Todos los documentos";

        listaDocumentosFiltrados = new ArrayList<>();
        for (DocumentoMascota d : listaDocumentosMascota) {
            if ("Todos los documentos".equals(filtroTipo) || d.getTipo().equalsIgnoreCase(filtroTipo)) {
                listaDocumentosFiltrados.add(d);
                modeloListaDocumentos.addElement(d);
            }
        }

        lblContadorDocumentos.setText(listaDocumentosFiltrados.size() + " constancias");

        if (!listaDocumentosFiltrados.isEmpty()) {
            listaDocumentos.setSelectedIndex(0);
            mostrarDetalleDocumentoEnVivo(listaDocumentosFiltrados.get(0));
        } else {
            mostrarEstadoSinDocumentos();
        }
    }

    // =========================================================================
    // 5. ACCIONES Y DIÁLOGOS
    // =========================================================================

    private void abrirEmitirCertificadoDialog() {
        if (mascotaSeleccionada == null) {
            JOptionPane.showMessageDialog(this, "Seleccione primero un paciente en el directorio.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        FormularioCertificadoDialog dlg = new FormularioCertificadoDialog(
                SwingUtilities.getWindowAncestor(this),
                mascotaSeleccionada,
                null,
                nuevoDoc -> {
                    seleccionarMascotaInterno(mascotaSeleccionada);
                    for (int i = 0; i < modeloListaDocumentos.size(); i++) {
                        if (modeloListaDocumentos.get(i).getCodigo().equals(nuevoDoc.getCodigo())) {
                            listaDocumentos.setSelectedIndex(i);
                            listaDocumentos.ensureIndexIsVisible(i);
                            break;
                        }
                    }
                    JOptionPane.showMessageDialog(this,
                            "Documento '" + nuevoDoc.getTipo() + "' emitido exitosamente.",
                            "Certificado Emitido", JOptionPane.INFORMATION_MESSAGE);
                }
        );
        dlg.setVisible(true);
    }

    private void imprimirCertificadoSeleccionado() {
        if (documentoSeleccionado == null || mascotaSeleccionada == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un documento para visualizar e imprimir.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String tipo = documentoSeleccionado.getTipo().toUpperCase();
        String[][] datos = new String[][]{
                {"1", tipo, mascotaSeleccionada.getNombre() + " (" + mascotaSeleccionada.getCodigo() + ")", clienteSeleccionado != null ? clienteSeleccionado.getNombreCompleto() : "N/D", documentoSeleccionado.getFechaActualizacionFormateada()},
                {"2", "Dictamen Facultativo", documentoSeleccionado.getDescripcion(), documentoSeleccionado.getVeterinarioEmisor() != null ? documentoSeleccionado.getVeterinarioEmisor() : "Dr. R. Mendoza (CMVP 8492)", "Vigente"}
        };

        Ui.mostrarVisorReporte(
                (java.awt.Frame) SwingUtilities.getWindowAncestor(this),
                tipo,
                "Paciente: " + mascotaSeleccionada.getNombre() + " (" + mascotaSeleccionada.getEspecieRaza() + ") | Propietario: " + (clienteSeleccionado != null ? clienteSeleccionado.getNombreCompleto() : "N/D"),
                "Fecha Emisión: " + documentoSeleccionado.getFechaActualizacionFormateada() + " | Registro: " + documentoSeleccionado.getCodigo(),
                new String[]{"N°", "Concepto", "Paciente / Detalle", "Médico Veterinario", "Estado"},
                datos,
                "DOCUMENTO OFICIAL DIGITAL VETERINARIA HAPPY PETS S.A.C.",
                "Certificado_" + mascotaSeleccionada.getNombre() + "_" + documentoSeleccionado.getCodigo()
        );
    }

    private void exportarListadoDocumentos() {
        if (mascotaSeleccionada == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un paciente para exportar su listado de certificados.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<DocumentoMascota> docs = repo.getDocumentosPorMascota(mascotaSeleccionada.getCodigo());
        String[][] datos = new String[docs.size()][4];
        for (int i = 0; i < docs.size(); i++) {
            DocumentoMascota d = docs.get(i);
            datos[i][0] = d.getCodigo();
            datos[i][1] = d.getTipo();
            datos[i][2] = d.getFechaActualizacionFormateada();
            datos[i][3] = d.isDisponible() ? "Oficial · Vigente" : "Archivado";
        }

        Ui.mostrarVisorReporte(
                (java.awt.Frame) SwingUtilities.getWindowAncestor(this),
                "RELACIÓN DE CONSTANCIAS Y CERTIFICADOS EMITIDOS",
                "Paciente: " + mascotaSeleccionada.getNombre() + " (" + mascotaSeleccionada.getEspecieRaza() + ") | Código: " + mascotaSeleccionada.getCodigo(),
                "Propietario: " + (clienteSeleccionado != null ? clienteSeleccionado.getNombreCompleto() : "N/D") + " | Total Documentos: " + docs.size(),
                new String[]{"Código", "Tipo de Documento", "Fecha Emisión", "Estado"},
                datos,
                "EXPEDIENTE DOCUMENTAL SANITARIO COMPLETO",
                "Certificados_" + mascotaSeleccionada.getNombre()
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
    // 7. RENDERERS DE LISTA (MASCOTA & DOCUMENTO)
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
    private class DocumentoCellRenderer extends JPanel implements ListCellRenderer<DocumentoMascota> {
        private static final long serialVersionUID = 1L;
        private final JLabel lblIconoDoc;
        private final JLabel lblNombreDoc;
        private final JLabel lblDetalleDoc;
        private final JLabel lblBadgeVigencia;

        public DocumentoCellRenderer() {
            setLayout(new BorderLayout(8, 2));
            setBorder(new EmptyBorder(8, 10, 8, 10));
            setOpaque(true);

            lblIconoDoc = new JLabel(Iconos.crearIconoCertificado(20, COLOR_TURQUESA));
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
            JPanel icoWrap = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 4));
            icoWrap.setOpaque(false);
            icoWrap.add(lblIconoDoc);
            add(icoWrap, BorderLayout.WEST);

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
            JPanel center = new JPanel();
            center.setOpaque(false);
            center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));

            lblNombreDoc = new JLabel();
            lblNombreDoc.setFont(new Font("Segoe UI", Font.BOLD, 12));
            lblNombreDoc.setForeground(COLOR_TITULO);

            lblDetalleDoc = new JLabel();
            lblDetalleDoc.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            lblDetalleDoc.setForeground(COLOR_MUTED);

            center.add(lblNombreDoc);
            center.add(Box.createVerticalStrut(1));
            center.add(lblDetalleDoc);
            add(center, BorderLayout.CENTER);

            lblBadgeVigencia = crearBadge("Oficial", new Color(220, 252, 231), new Color(22, 163, 74));
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
            JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 4));
            der.setOpaque(false);
            der.add(lblBadgeVigencia);
            add(der, BorderLayout.EAST);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends DocumentoMascota> list, DocumentoMascota doc, int index, boolean isSelected, boolean cellHasFocus) {
            if (doc != null) {
                lblNombreDoc.setText(doc.getTipo());
                String code = doc instanceof Certificado cert && cert.getNumeroCertificado() != null ? cert.getNumeroCertificado() : doc.getCodigo();
                lblDetalleDoc.setText(code + " · " + doc.getFechaActualizacionFormateada());
                lblBadgeVigencia.setText(doc.isDisponible() ? "Vigente" : "Archivado");

                if (doc.isDisponible()) {
                    lblBadgeVigencia.setBackground(new Color(220, 252, 231));
                    lblBadgeVigencia.setForeground(new Color(22, 163, 74));
                } else {
                    lblBadgeVigencia.setBackground(new Color(241, 245, 249));
                    lblBadgeVigencia.setForeground(COLOR_MUTED);
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
