package happypets.modulos.modulo2;

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
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import happypets.data.RepositorioVeterinaria;
import happypets.model.Cita;
import happypets.model.PacienteTriaje;
import happypets.model.RecordatorioCita;
import happypets.ui.Iconos;
import happypets.ui.Ui;

/**
 * Submódulo 2.3: Gestión y Envío de Recordatorios de Citas y Tratamientos.
 * Arquitectura Opción A: Master-Detail con tabla de alta densidad, avatares circulares matemáticamente perfectos,
 * simulador interactivo de mensajería móvil (WhatsApp / SMS Chat Bubble), KPIs en vivo y redactor modal.
 */
public class VistaRecordatoriosPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private static final Color COLOR_BORDE = Ui.BORDE_SUAVE;
    private static final Color COLOR_AZUL_PRIMARIO = Ui.TURQUESA;
    private static final Color COLOR_AZUL_OSCURO = Ui.TURQUESA_OSCURO;
    private static final Color COLOR_AZUL_SUAVE = Ui.TURQUESA_SUAVE;
    private static final Color COLOR_TEXTO_TITULO = Ui.TEXTO_TITULO;
    private static final Color COLOR_TEXTO_MUTED = Ui.TEXTO_MUTED;

    private final RepositorioVeterinaria repo = RepositorioVeterinaria.getInstancia();

    // Filtros y Búsqueda
    private JComboBox<String> cbFiltroCanal;
    private JComboBox<String> cbFiltroEstado;
    private JTextField txtBuscar;
    private String tabActiva = "EMITIDOS"; // "EMITIDOS" o "PENDIENTES"
    private JButton btnTabEmitidos;
    private JButton btnTabPendientes;

    // KPIs
    private JLabel lblKpiTotalEnviados;
    private JLabel lblKpiConfirmados;
    private JLabel lblKpiTasaRespuesta;
    private JLabel lblKpiPendientes;

    // Tabla Master
    private JTable tablaRecordatorios;
    private DefaultTableModel modeloRecordatorios;
    private JLabel lblContadorRegistros;
    private List<RecordatorioCita> listaRecordatoriosActual = new ArrayList<>();
    private List<Cita> listaCitasPendientesAviso = new ArrayList<>();
    private RecordatorioCita recordatorioSeleccionado = null;

    // Panel Detalle / Simulador Móvil
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private JPanel panelDetalleSimulador;
    private JLabel lblSimuladorContacto;
    private JLabel lblSimuladorSub;
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private JPanel headerSimulador;
    private JTextArea txtSimuladorBurbuja;
    private JLabel lblSimuladorHoraCheck;
    private JLabel lblFichaPaciente;
    private JLabel lblFichaTutor;
    private JLabel lblBadgeCanal;
    private JLabel lblBadgeEstado;
    private JLabel lblBadgeCita;

    /**
     * Constructor de VistaRecordatoriosPanel.
     * Inicializa los componentes gráficos, configura layouts, asigna estilos
     * institucionales y carga los datos desde el repositorio central.
     */
    public VistaRecordatoriosPanel() {
        setLayout(new BorderLayout());
        setOpaque(false);

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel contenido = new JPanel();
        contenido.setOpaque(false);
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setBorder(new EmptyBorder(12, 18, 14, 18));

        // 1. Cabecera principal
        contenido.add(crearCabeceraVista());
        contenido.add(Box.createVerticalStrut(8));

        // 2. Fila superior de 4 KPIs
        contenido.add(crearFilaKpis());
        contenido.add(Box.createVerticalStrut(10));

        // 3. Contenedor Master-Detail (Tabla 58% + Simulador Móvil 42%)
        contenido.add(crearContenedorMasterDetail());

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(contenido, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(wrapper);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);

        recargarDatos();
    }

    // =========================================================================
    // 1. CABECERA Y KPIS
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
        cab.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel izq = new JPanel();
        izq.setOpaque(false);
        izq.setLayout(new BoxLayout(izq, BoxLayout.Y_AXIS));

        JLabel lblTit = new JLabel("Gestión y Envío de Recordatorios");
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblTit.setForeground(COLOR_TEXTO_TITULO);

        JLabel lblSub = new JLabel("Módulo 2.3 · Notificaciones automatizadas y confirmación de asistencia vía WhatsApp, SMS y Email");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblSub.setForeground(COLOR_TEXTO_MUTED);

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

        JButton btnMasivo = crearBotonAccion("Envío Masivo (Próximas 24h)", false);
        btnMasivo.setIcon(Iconos.crearIconoWhatsApp(13, new Color(22, 163, 74)));
        btnMasivo.setIconTextGap(6);
        btnMasivo.addActionListener(e -> ejecutarEnvioMasivo());

        JButton btnActualizar = crearBotonAccion("Actualizar", false);
        btnActualizar.setIcon(Iconos.crearIconoCheck(13, new Color(22, 163, 74)));
        btnActualizar.setIconTextGap(6);
        btnActualizar.addActionListener(e -> recargarDatos());

        JButton btnNuevo = crearBotonAccion("+ Redactar Recordatorio", true);
        btnNuevo.setIcon(Iconos.crearIconoWhatsApp(13, Color.WHITE));
        btnNuevo.setIconTextGap(6);
        btnNuevo.addActionListener(e -> abrirDialogoNuevoRecordatorio(null));

        der.add(btnMasivo);
        der.add(btnActualizar);
        der.add(btnNuevo);
        cab.add(der, BorderLayout.EAST);

        return cab;
    }

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private JPanel crearFilaKpis() {
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel fila = new JPanel(new GridLayout(1, 4, 10, 0));
        fila.setOpaque(false);
        fila.setPreferredSize(new Dimension(0, 56));
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));

        lblKpiTotalEnviados = new JLabel("0");
        lblKpiConfirmados = new JLabel("0");
        lblKpiTasaRespuesta = new JLabel("0%");
        lblKpiPendientes = new JLabel("0");

        fila.add(crearCardKpi(lblKpiTotalEnviados, "RECORDATORIOS EMITIDOS", new JLabel("Enviados por todos los canales"), new Color(224, 242, 254), Iconos.crearIconoMensaje(18, COLOR_AZUL_PRIMARIO)));
        fila.add(crearCardKpi(lblKpiConfirmados, "CONFIRMADOS POR CLIENTES", new JLabel("Asistencia ratificada"), new Color(220, 252, 231), Iconos.crearIconoWhatsApp(18, new Color(22, 163, 74))));
        fila.add(crearCardKpi(lblKpiTasaRespuesta, "TASA DE EFECTIVIDAD", new JLabel("Ratio de respuesta positiva"), new Color(243, 232, 255), Iconos.crearIconoCheck(18, new Color(147, 51, 234))));
        fila.add(crearCardKpi(lblKpiPendientes, "PENDIENTES POR CONFIRMAR", new JLabel("En espera de respuesta"), new Color(254, 243, 199), Iconos.crearIconoReloj(18, new Color(217, 119, 6))));

        return fila;
    }

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private JPanel crearCardKpi(JLabel lblVal, String etiqueta, JLabel lblSub, Color bgIco, Icon icono) {
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel card = new JPanel(new BorderLayout(10, 0)) {
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
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(COLOR_BORDE);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(8, 12, 8, 12));

        JLabel badge = new JLabel(icono, SwingConstants.CENTER) {
            private static final long serialVersionUID = 1L;
            @Override
            /**
             * Renderizado gráfico personalizado en Java2D.
             * Activa Antialiasing para bordes suaves y realiza el cálculo geométrico preciso.
             */
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bgIco);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        badge.setPreferredSize(new Dimension(36, 36));
        card.add(badge, BorderLayout.WEST);

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel pText = new JPanel();
        pText.setOpaque(false);
        pText.setLayout(new BoxLayout(pText, BoxLayout.Y_AXIS));

        JLabel lblEtq = new JLabel(etiqueta);
        lblEtq.setFont(new Font("Segoe UI", Font.BOLD, 9));
        lblEtq.setForeground(COLOR_TEXTO_MUTED);

        lblVal.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblVal.setForeground(COLOR_TEXTO_TITULO);

        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblSub.setForeground(COLOR_TEXTO_MUTED);

        pText.add(lblEtq);
        pText.add(Box.createVerticalStrut(1));
        pText.add(lblVal);
        pText.add(Box.createVerticalStrut(1));
        pText.add(lblSub);

        card.add(pText, BorderLayout.CENTER);
        return card;
    }

    // =========================================================================
    // 2. MASTER-DETAIL: TABLA HISTÓRICA Y SIMULADOR SMARTPHONE
    // =========================================================================

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private JPanel crearContenedorMasterDetail() {
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel split = new JPanel(new BorderLayout(14, 0));
        split.setOpaque(false);

        // Columna Izquierda (Master: Tabla y Filtros ~ 62%)
        split.add(crearTarjetaMaster(), BorderLayout.CENTER);

        // Columna Derecha (Detail: Simulador Móvil y Acciones ~ 38%)
        split.add(crearTarjetaSimuladorMovil(), BorderLayout.EAST);

        return split;
    }

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private JPanel crearTarjetaMaster() {
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
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(COLOR_BORDE);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(12, 14, 12, 14));

        // Barra superior de herramientas con selector de vista (Tabs)
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel pTop = new JPanel(new BorderLayout(8, 6));
        pTop.setOpaque(false);

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel pTabs = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        pTabs.setOpaque(false);

        btnTabEmitidos = crearBotonPill("Historial Emitido", true);
        btnTabPendientes = crearBotonPill("Citas por Notificar", false);

        btnTabEmitidos.addActionListener(e -> setTabActiva("EMITIDOS"));
        btnTabPendientes.addActionListener(e -> setTabActiva("PENDIENTES"));

        pTabs.add(btnTabEmitidos);
        pTabs.add(btnTabPendientes);
        pTop.add(pTabs, BorderLayout.WEST);

        lblContadorRegistros = new JLabel("0 registros");
        lblContadorRegistros.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblContadorRegistros.setForeground(COLOR_TEXTO_MUTED);
        pTop.add(lblContadorRegistros, BorderLayout.EAST);

        // Barra de Filtros secundarios
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel pFiltros = new JPanel(new BorderLayout(8, 0));
        pFiltros.setOpaque(false);

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel pCombos = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        pCombos.setOpaque(false);

        cbFiltroCanal = new JComboBox<>(new String[]{"Todos los canales", "WhatsApp", "SMS", "E-Mail"});
        cbFiltroCanal.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        cbFiltroCanal.setPreferredSize(new Dimension(130, 26));
        cbFiltroCanal.setBackground(Color.WHITE);
        cbFiltroCanal.addActionListener(e -> filtrarDatos());
        pCombos.add(cbFiltroCanal);

        cbFiltroEstado = new JComboBox<>(new String[]{"Todos los estados", "Confirmado", "Enviado", "Pendiente"});
        cbFiltroEstado.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        cbFiltroEstado.setPreferredSize(new Dimension(130, 26));
        cbFiltroEstado.setBackground(Color.WHITE);
        cbFiltroEstado.addActionListener(e -> filtrarDatos());
        pCombos.add(cbFiltroEstado);

        pFiltros.add(pCombos, BorderLayout.WEST);

        // Buscador reactivo
        txtBuscar = Ui.campoTexto(14);
        txtBuscar.setPreferredSize(new Dimension(160, 26));
        txtBuscar.setToolTipText("Buscar paciente, dueño, contacto...");
        txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { filtrarDatos(); }
            @Override public void removeUpdate(DocumentEvent e) { filtrarDatos(); }
            @Override public void changedUpdate(DocumentEvent e) { filtrarDatos(); }
        });

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel pBusq = new JPanel(new BorderLayout(4, 0));
        pBusq.setOpaque(false);
        JLabel lblLupa = new JLabel(Iconos.crearIconoBuscar(13, COLOR_TEXTO_MUTED));
        pBusq.add(lblLupa, BorderLayout.WEST);
        pBusq.add(txtBuscar, BorderLayout.CENTER);
        pFiltros.add(pBusq, BorderLayout.EAST);

        pTop.add(pFiltros, BorderLayout.SOUTH);
        card.add(pTop, BorderLayout.NORTH);

        // Tabla Master con columnas ricas
        String[] columnas = {"Canal", "Paciente & Tutor", "Cita", "Contacto", "Fecha Emisión", "Estado"};
        modeloRecordatorios = new DefaultTableModel(columnas, 0) {
            private static final long serialVersionUID = 1L;
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tablaRecordatorios = new JTable(modeloRecordatorios);
        tablaRecordatorios.setRowHeight(44);
        tablaRecordatorios.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        tablaRecordatorios.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        tablaRecordatorios.getTableHeader().setBackground(new Color(241, 245, 249));
        tablaRecordatorios.getTableHeader().setForeground(COLOR_TEXTO_TITULO);
        tablaRecordatorios.setSelectionBackground(COLOR_AZUL_SUAVE);
        tablaRecordatorios.setSelectionForeground(COLOR_TEXTO_TITULO);

        // Renderers especializados
        tablaRecordatorios.getColumnModel().getColumn(0).setPreferredWidth(90);
        tablaRecordatorios.getColumnModel().getColumn(0).setCellRenderer(new CanalBadgeCellRenderer());

        tablaRecordatorios.getColumnModel().getColumn(1).setPreferredWidth(170);
        tablaRecordatorios.getColumnModel().getColumn(1).setCellRenderer(new PacienteTutorCellRenderer());

        tablaRecordatorios.getColumnModel().getColumn(2).setPreferredWidth(95);
        tablaRecordatorios.getColumnModel().getColumn(2).setCellRenderer(new CenteredCellRenderer());

        tablaRecordatorios.getColumnModel().getColumn(3).setPreferredWidth(130);
        tablaRecordatorios.getColumnModel().getColumn(4).setPreferredWidth(110);
        tablaRecordatorios.getColumnModel().getColumn(4).setCellRenderer(new CenteredCellRenderer());

        tablaRecordatorios.getColumnModel().getColumn(5).setPreferredWidth(95);
        tablaRecordatorios.getColumnModel().getColumn(5).setCellRenderer(new EstadoBadgeCellRenderer());

        tablaRecordatorios.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = tablaRecordatorios.getSelectedRow();
                if (row >= 0 && row < listaRecordatoriosActual.size()) {
                    seleccionarRecordatorio(listaRecordatoriosActual.get(row));
                }
            }
        });

        JScrollPane sp = new JScrollPane(tablaRecordatorios);
        sp.setPreferredSize(new Dimension(620, 380));
        sp.setBorder(BorderFactory.createLineBorder(COLOR_BORDE, 1));
        sp.getVerticalScrollBar().setUnitIncrement(16);
        card.add(sp, BorderLayout.CENTER);

        // Barra inferior de acciones master
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel bot = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 2));
        bot.setOpaque(false);

        JButton btnCopiar = crearBotonAccion("Copiar Mensaje", false);
        btnCopiar.addActionListener(e -> copiarMensajePortapapeles());

        JButton btnReenviar = crearBotonAccion("Reenviar", false);
        btnReenviar.setIcon(Iconos.crearIconoWhatsApp(12, new Color(22, 163, 74)));
        btnReenviar.setIconTextGap(4);
        btnReenviar.addActionListener(e -> reenviarSeleccionado());

        JButton btnConfirmar = crearBotonAccion("Confirmar Asistencia", true);
        btnConfirmar.addActionListener(e -> confirmarAsistenciaSeleccionada());

        bot.add(btnCopiar);
        bot.add(btnReenviar);
        bot.add(btnConfirmar);
        card.add(bot, BorderLayout.SOUTH);

        return card;
    }

    // =========================================================================
    // 3. DETAIL: SIMULADOR SMARTPHONE Y FICHA EN VIVO
    // =========================================================================

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private JPanel crearTarjetaSimuladorMovil() {
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        panelDetalleSimulador = new JPanel(new BorderLayout(0, 8)) {
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
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(COLOR_BORDE);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        panelDetalleSimulador.setOpaque(false);
        panelDetalleSimulador.setBorder(new EmptyBorder(12, 14, 12, 14));
        panelDetalleSimulador.setPreferredSize(new Dimension(380, 0));

        // 1. Cabecera ejecutiva del contacto
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JLabel lblTit = new JLabel("Vista Previa y Control en Vivo");
        lblTit.setIcon(Iconos.crearIconoReloj(14, COLOR_AZUL_PRIMARIO));
        lblTit.setIconTextGap(6);
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTit.setForeground(COLOR_TEXTO_TITULO);
        top.add(lblTit, BorderLayout.WEST);

        panelDetalleSimulador.add(top, BorderLayout.NORTH);

        // 2. Ficha del Paciente y Cita + Teléfono Inteligente
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel centro = new JPanel();
        centro.setOpaque(false);
        centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));

        // Ficha ejecutiva
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel cardFicha = new JPanel(new BorderLayout(10, 0));
        cardFicha.setBackground(new Color(248, 250, 252));
        cardFicha.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDE, 1),
                new EmptyBorder(8, 10, 8, 10)
        ));
        cardFicha.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));

        lblFichaPaciente = new JLabel("Paciente: --");
        lblFichaPaciente.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblFichaPaciente.setForeground(COLOR_TEXTO_TITULO);

        lblFichaTutor = new JLabel("Tutor: -- | Tel: --");
        lblFichaTutor.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblFichaTutor.setForeground(COLOR_TEXTO_MUTED);

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel pFichaText = new JPanel();
        pFichaText.setOpaque(false);
        pFichaText.setLayout(new BoxLayout(pFichaText, BoxLayout.Y_AXIS));
        pFichaText.add(lblFichaPaciente);
        pFichaText.add(Box.createVerticalStrut(2));
        pFichaText.add(lblFichaTutor);

        cardFicha.add(pFichaText, BorderLayout.CENTER);

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel pFichaBadges = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
        pFichaBadges.setOpaque(false);
        lblBadgeCanal = crearBadgePastilla("WhatsApp", new Color(220, 252, 231), new Color(22, 163, 74));
        lblBadgeEstado = crearBadgePastilla("Enviado", new Color(224, 242, 254), new Color(2, 132, 199));
        pFichaBadges.add(lblBadgeCanal);
        pFichaBadges.add(lblBadgeEstado);
        cardFicha.add(pFichaBadges, BorderLayout.EAST);

        centro.add(cardFicha);
        centro.add(Box.createVerticalStrut(8));

        // Smartphone Frame Simulator
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel phoneFrame = new JPanel(new BorderLayout()) {
            private static final long serialVersionUID = 1L;
            @Override
            /**
             * Renderizado gráfico personalizado en Java2D.
             * Activa Antialiasing para bordes suaves y realiza el cálculo geométrico preciso.
             */
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(239, 234, 226)); // WhatsApp Chat BG
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(new Color(203, 213, 225));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        phoneFrame.setOpaque(false);
        phoneFrame.setPreferredSize(new Dimension(350, 240));

        // Phone Header
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        headerSimulador = new JPanel(new BorderLayout(8, 0)) {
            private static final long serialVersionUID = 1L;
            @Override
            /**
             * Renderizado gráfico personalizado en Java2D.
             * Activa Antialiasing para bordes suaves y realiza el cálculo geométrico preciso.
             */
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = (Color) getClientProperty("bgColor");
                if (bg == null) bg = new Color(7, 94, 84);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.fillRect(0, getHeight() - 6, getWidth(), 6);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        headerSimulador.setOpaque(false);
        headerSimulador.setBorder(new EmptyBorder(8, 10, 8, 10));
        headerSimulador.setPreferredSize(new Dimension(0, 42));

        JLabel avatarSim = crearAvatarCircular("HP", 28, Color.WHITE, new Color(7, 94, 84));
        headerSimulador.add(avatarSim, BorderLayout.WEST);

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel pHeaderTxt = new JPanel();
        pHeaderTxt.setOpaque(false);
        pHeaderTxt.setLayout(new BoxLayout(pHeaderTxt, BoxLayout.Y_AXIS));

        lblSimuladorContacto = new JLabel("Cliente Destino");
        lblSimuladorContacto.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblSimuladorContacto.setForeground(Color.WHITE);

        lblSimuladorSub = new JLabel("En línea  ·  Notificaciones HappyPets");
        lblSimuladorSub.setFont(new Font("Segoe UI", Font.PLAIN, 9));
        lblSimuladorSub.setForeground(new Color(220, 252, 231));

        pHeaderTxt.add(lblSimuladorContacto);
        pHeaderTxt.add(lblSimuladorSub);
        headerSimulador.add(pHeaderTxt, BorderLayout.CENTER);

        phoneFrame.add(headerSimulador, BorderLayout.NORTH);

        // Chat Body con burbuja saliente
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel chatBody = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 14));
        chatBody.setOpaque(false);

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel bubble = new JPanel(new BorderLayout(4, 4)) {
            private static final long serialVersionUID = 1L;
            @Override
            /**
             * Renderizado gráfico personalizado en Java2D.
             * Activa Antialiasing para bordes suaves y realiza el cálculo geométrico preciso.
             */
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(220, 248, 198)); // WhatsApp outgoing bubble
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(new Color(187, 247, 208));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        bubble.setOpaque(false);
        bubble.setBorder(new EmptyBorder(8, 10, 6, 10));
        bubble.setPreferredSize(new Dimension(320, 140));

        txtSimuladorBurbuja = new JTextArea();
        txtSimuladorBurbuja.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        txtSimuladorBurbuja.setForeground(new Color(15, 23, 42));
        txtSimuladorBurbuja.setLineWrap(true);
        txtSimuladorBurbuja.setWrapStyleWord(true);
        txtSimuladorBurbuja.setEditable(false);
        txtSimuladorBurbuja.setOpaque(false);
        bubble.add(txtSimuladorBurbuja, BorderLayout.CENTER);

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel pBubbleBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
        pBubbleBottom.setOpaque(false);

        lblSimuladorHoraCheck = new JLabel("12:00 hrs · Entregado");
        lblSimuladorHoraCheck.setFont(new Font("Segoe UI", Font.BOLD, 9));
        lblSimuladorHoraCheck.setForeground(new Color(2, 132, 199));
        pBubbleBottom.add(lblSimuladorHoraCheck);
        bubble.add(pBubbleBottom, BorderLayout.SOUTH);

        chatBody.add(bubble);
        phoneFrame.add(chatBody, BorderLayout.CENTER);

        centro.add(phoneFrame);
        panelDetalleSimulador.add(centro, BorderLayout.CENTER);

        // 3. Barra de Acciones Operativas Inmediatas
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel bot = new JPanel(new GridLayout(2, 2, 6, 6));
        bot.setOpaque(false);
        bot.setBorder(new EmptyBorder(6, 0, 0, 0));

        JButton btnConfirmarAsist = crearBotonAccion("Confirmar Asistencia", true);
        btnConfirmarAsist.setIcon(Iconos.crearIconoCheck(12, Color.WHITE));
        btnConfirmarAsist.addActionListener(e -> confirmarAsistenciaSeleccionada());

        JButton btnReenviarMsg = crearBotonAccion("Reenviar Notificación", false);
        btnReenviarMsg.setIcon(Iconos.crearIconoWhatsApp(12, new Color(22, 163, 74)));
        btnReenviarMsg.addActionListener(e -> reenviarSeleccionado());

        JButton btnEditarReenviar = crearBotonAccion("Editar y Reenviar", false);
        btnEditarReenviar.setIcon(Iconos.crearIconoCalendario(12, COLOR_AZUL_PRIMARIO));
        btnEditarReenviar.addActionListener(e -> editarYReenviar());

        JButton btnTriaje = crearBotonAccion("Pasar a Sala de Espera", false);
        btnTriaje.setIcon(Iconos.crearIconoReloj(12, new Color(217, 119, 6)));
        btnTriaje.addActionListener(e -> pasarASalaEspera());

        bot.add(btnConfirmarAsist);
        bot.add(btnReenviarMsg);
        bot.add(btnEditarReenviar);
        bot.add(btnTriaje);

        panelDetalleSimulador.add(bot, BorderLayout.SOUTH);

        return panelDetalleSimulador;
    }

    public void prepararParaCita(Cita cita) {
        if (cita == null) return;
        List<RecordatorioCita> todos = repo.getRecordatorios();
        RecordatorioCita existente = todos.stream()
                .filter(r -> r.getIdCita() != null && r.getIdCita().equalsIgnoreCase(cita.getIdCita()))
                .findFirst().orElse(null);

        if (existente != null) {
            setTabActiva("EMITIDOS");
            seleccionarRecordatorio(existente);
            for (int i = 0; i < listaRecordatoriosActual.size(); i++) {
                if (listaRecordatoriosActual.get(i).getIdRecordatorio().equals(existente.getIdRecordatorio())) {
                    tablaRecordatorios.setRowSelectionInterval(i, i);
                    break;
                }
            }
        } else {
            abrirDialogoNuevoRecordatorio(cita);
        }
    }

    public void recargarDatos() {
        actualizarKpis();
        filtrarDatos();
    }

    private void actualizarKpis() {
        List<RecordatorioCita> todos = repo.getRecordatorios();
        int total = todos.size();
        long confirmados = todos.stream().filter(r -> "Confirmado".equalsIgnoreCase(r.getEstado())).count();
        long pendientes = todos.stream().filter(r -> "Pendiente".equalsIgnoreCase(r.getEstado()) || "Enviado".equalsIgnoreCase(r.getEstado())).count();
        int tasa = total > 0 ? (int) ((confirmados * 100) / total) : 0;

        lblKpiTotalEnviados.setText(total + (total == 1 ? " emitido" : " emitidos"));
        lblKpiConfirmados.setText(confirmados + (confirmados == 1 ? " confirmado" : " confirmados"));
        lblKpiTasaRespuesta.setText(tasa + "%");
        lblKpiPendientes.setText(pendientes + (pendientes == 1 ? " pendiente" : " pendientes"));
    }

    private void setTabActiva(String tab) {
        this.tabActiva = tab;
        btnTabEmitidos.putClientProperty("activo", "EMITIDOS".equals(tab));
        btnTabPendientes.putClientProperty("activo", "PENDIENTES".equals(tab));
        btnTabEmitidos.repaint();
        btnTabPendientes.repaint();
        filtrarDatos();
    }

    /**
     * Aplica criterios de filtrado dinámico en memoria sobre los registros.
     * Evalúa texto de búsqueda (búsqueda predictiva) y filtros por categoría o estado.
     */
    private void filtrarDatos() {
        modeloRecordatorios.setRowCount(0);
        listaRecordatoriosActual.clear();

        String fCanal = cbFiltroCanal != null ? (String) cbFiltroCanal.getSelectedItem() : "Todos";
        String fEstado = cbFiltroEstado != null ? (String) cbFiltroEstado.getSelectedItem() : "Todos";
        String q = txtBuscar != null ? txtBuscar.getText().trim().toLowerCase() : "";

        if ("EMITIDOS".equals(tabActiva)) {
            List<RecordatorioCita> todos = repo.getRecordatorios();
            for (RecordatorioCita r : todos) {
                if (fCanal != null && !fCanal.startsWith("Todos") && !r.getCanal().equalsIgnoreCase(fCanal)) continue;
                if (fEstado != null && !fEstado.startsWith("Todos") && !r.getEstado().equalsIgnoreCase(fEstado)) continue;

                if (!q.isEmpty()) {
                    boolean match = (r.getNombreMascota() != null && r.getNombreMascota().toLowerCase().contains(q)) ||
                                    (r.getNombreCliente() != null && r.getNombreCliente().toLowerCase().contains(q)) ||
                                    (r.getContactoDestino() != null && r.getContactoDestino().toLowerCase().contains(q)) ||
                                    (r.getIdCita() != null && r.getIdCita().toLowerCase().contains(q)) ||
                                    (r.getMensaje() != null && r.getMensaje().toLowerCase().contains(q));
                    if (!match) continue;
                }

                listaRecordatoriosActual.add(r);
                modeloRecordatorios.addRow(new Object[]{
                        r.getCanal(),
                        new Object[]{r.getNombreMascota(), r.getNombreCliente()},
                        r.getIdCita(),
                        r.getContactoDestino(),
                        r.getFechaEnvio().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                        r.getEstado()
                });
            }
            lblContadorRegistros.setText(listaRecordatoriosActual.size() + " recordatorios emitidos");
        } else {
            // Bandeja de citas por notificar
            List<Cita> citas = repo.getCitas();
            List<RecordatorioCita> todosRec = repo.getRecordatorios();

            for (Cita c : citas) {
                boolean yaTieneRec = todosRec.stream().anyMatch(r -> r.getIdCita().equalsIgnoreCase(c.getIdCita()));
                if (yaTieneRec) continue; // Ya fue notificada

                if (!q.isEmpty()) {
                    boolean match = (c.getNombreMascota() != null && c.getNombreMascota().toLowerCase().contains(q)) ||
                                    (c.getNombreCliente() != null && c.getNombreCliente().toLowerCase().contains(q)) ||
                                    (c.getTelefonoCliente() != null && c.getTelefonoCliente().toLowerCase().contains(q)) ||
                                    (c.getIdCita() != null && c.getIdCita().toLowerCase().contains(q));
                    if (!match) continue;
                }

                // Generar recordatorio temporal para visualización
                RecordatorioCita rTemp = new RecordatorioCita(
                        "PEND",
                        c.getIdCita(),
                        c.getNombreMascota(),
                        c.getNombreCliente(),
                        c.getTelefonoCliente(),
                        "WhatsApp",
                        LocalDateTime.now(),
                        "Hola " + c.getNombreCliente() + ", recordamos cita de " + c.getNombreMascota() + " el " + c.getFechaFormateada() + " a las " + c.getHoraFormateada() + " hrs.",
                        "Pendiente"
                );

                listaRecordatoriosActual.add(rTemp);
                modeloRecordatorios.addRow(new Object[]{
                        "WhatsApp",
                        new Object[]{c.getNombreMascota(), c.getNombreCliente()},
                        c.getIdCita(),
                        c.getTelefonoCliente(),
                        c.getFechaFormateada() + " " + c.getHoraFormateada(),
                        "Pendiente"
                });
            }
            lblContadorRegistros.setText(listaRecordatoriosActual.size() + " citas pendientes de aviso");
        }

        // Auto-seleccionar primer elemento
        if (!listaRecordatoriosActual.isEmpty()) {
            tablaRecordatorios.setRowSelectionInterval(0, 0);
            seleccionarRecordatorio(listaRecordatoriosActual.get(0));
        } else {
            limpiarDetalleSimulador();
        }
    }

    private void seleccionarRecordatorio(RecordatorioCita r) {
        this.recordatorioSeleccionado = r;
        if (r == null) return;

        lblFichaPaciente.setText("Paciente: " + r.getNombreMascota());
        lblFichaTutor.setText("Tutor: " + r.getNombreCliente() + "  |  " + r.getContactoDestino());
        lblBadgeCanal.setText(r.getCanal());
        lblBadgeEstado.setText(r.getEstado());

        lblSimuladorContacto.setText(r.getNombreCliente() + " (" + r.getNombreMascota() + ")");
        lblSimuladorSub.setText("En línea  ·  " + r.getContactoDestino());
        txtSimuladorBurbuja.setText(r.getMensaje());

        String sufijoCheck = "Confirmado".equalsIgnoreCase(r.getEstado()) ? " · Leído" :
                             "Enviado".equalsIgnoreCase(r.getEstado()) ? " · Entregado" : " · Pendiente";
        lblSimuladorHoraCheck.setText(r.getFechaEnvio().format(DateTimeFormatter.ofPattern("HH:mm")) + " hrs" + sufijoCheck);
        lblSimuladorHoraCheck.setForeground("Confirmado".equalsIgnoreCase(r.getEstado()) ? new Color(2, 132, 199) : new Color(100, 116, 139));

        Color colHeader = "SMS".equalsIgnoreCase(r.getCanal()) ? new Color(2, 132, 199) :
                          "E-Mail".equalsIgnoreCase(r.getCanal()) ? new Color(126, 34, 206) : new Color(7, 94, 84);
        headerSimulador.putClientProperty("bgColor", colHeader);

        panelDetalleSimulador.revalidate();
        panelDetalleSimulador.repaint();
    }

    private void limpiarDetalleSimulador() {
        this.recordatorioSeleccionado = null;
        lblFichaPaciente.setText("Paciente: --");
        lblFichaTutor.setText("Tutor: --");
        txtSimuladorBurbuja.setText("Seleccione un recordatorio en la tabla para ver su vista previa interactiva.");
    }

    // =========================================================================
    // 5. ACCIONES OPERATIVAS
    // =========================================================================

    private void abrirDialogoNuevoRecordatorio(Cita c) {
        Window parent = SwingUtilities.getWindowAncestor(this);
        FormularioEnvioRecordatorioDialog dlg = new FormularioEnvioRecordatorioDialog(parent, c, rec -> {
            recargarDatos();
            seleccionarRecordatorio(rec);
        });
        dlg.setVisible(true);
    }

    private void confirmarAsistenciaSeleccionada() {
        if (recordatorioSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un recordatorio primero.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        recordatorioSeleccionado.setEstado("Confirmado");
        repo.actualizarEstadoRecordatorio(recordatorioSeleccionado.getIdRecordatorio(), "Confirmado");
        repo.actualizarEstadoCita(recordatorioSeleccionado.getIdCita(), "Confirmada");

        JOptionPane.showMessageDialog(this,
                "Asistencia confirmada para " + recordatorioSeleccionado.getNombreMascota() + " (" + recordatorioSeleccionado.getNombreCliente() + ").\nLa cita médica " + recordatorioSeleccionado.getIdCita() + " ha sido actualizada a 'Confirmada'.",
                "Confirmación Registrada", JOptionPane.INFORMATION_MESSAGE);

        recargarDatos();
    }

    private void reenviarSeleccionado() {
        if (recordatorioSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un recordatorio primero.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        recordatorioSeleccionado.setFechaEnvio(LocalDateTime.now());
        recordatorioSeleccionado.setEstado("Enviado");
        repo.actualizarEstadoRecordatorio(recordatorioSeleccionado.getIdRecordatorio(), "Enviado");

        JOptionPane.showMessageDialog(this,
                "Notificación reenviada vía " + recordatorioSeleccionado.getCanal() + " a " + recordatorioSeleccionado.getNombreCliente() + " (" + recordatorioSeleccionado.getContactoDestino() + ").",
                "Reenvío Exitoso", JOptionPane.INFORMATION_MESSAGE);

        recargarDatos();
    }

    private void editarYReenviar() {
        if (recordatorioSeleccionado == null) return;
        List<Cita> citas = repo.getCitas();
        Cita c = citas.stream().filter(it -> it.getIdCita().equalsIgnoreCase(recordatorioSeleccionado.getIdCita())).findFirst().orElse(null);
        abrirDialogoNuevoRecordatorio(c);
    }

    private void pasarASalaEspera() {
        if (recordatorioSeleccionado == null) return;
        List<Cita> citas = repo.getCitas();
        Cita c = citas.stream().filter(it -> it.getIdCita().equalsIgnoreCase(recordatorioSeleccionado.getIdCita())).findFirst().orElse(null);

        if (c != null) {
            c.setEstado("En Sala de Espera");
            repo.guardarCita(c);

            PacienteTriaje pt = new PacienteTriaje(
                    null, c.getIdCita(), c.getCodigoMascota(), c.getNombreMascota(),
                    c.getEspecieRaza(), c.getNombreCliente(), c.getTelefonoCliente(),
                    java.time.LocalTime.now(), 20.0, 38.5, 90,
                    "VERDE (Normal)", c.getMotivo(),
                    "Consultorio 1 - Dr. Mendoza", "En Espera"
            );
            repo.agregarPacienteTriaje(pt);

            JOptionPane.showMessageDialog(this,
                    "El paciente " + c.getNombreMascota() + " ha sido transferido a Sala de Espera (Ticket " + pt.getIdTicket() + ").",
                    "Ingreso a Triaje", JOptionPane.INFORMATION_MESSAGE);

            recargarDatos();
        }
    }

    private void ejecutarEnvioMasivo() {
        List<Cita> citas = repo.getCitas();
        int enviados = 0;
        for (Cita c : citas) {
            if ("Programada".equalsIgnoreCase(c.getEstado())) {
                RecordatorioCita rec = new RecordatorioCita(
                        null,
                        c.getIdCita(),
                        c.getNombreMascota(),
                        c.getNombreCliente(),
                        c.getTelefonoCliente(),
                        "WhatsApp",
                        LocalDateTime.now(),
                        "Hola " + c.getNombreCliente() + ", te recordamos tu cita en Happy Pets para " + c.getNombreMascota() + " programada para mañana a las " + c.getHoraFormateada() + " hrs. Por favor confirma tu asistencia.",
                        "Enviado"
                );
                repo.agregarRecordatorio(rec);
                enviados++;
            }
        }

        JOptionPane.showMessageDialog(this,
                "Envío masivo completado: Se han generado y transmitido " + enviados + " recordatorios automáticos por WhatsApp para las citas programadas.",
                "Automatización Ejecutada", JOptionPane.INFORMATION_MESSAGE);

        recargarDatos();
    }

    private void copiarMensajePortapapeles() {
        if (recordatorioSeleccionado != null && recordatorioSeleccionado.getMensaje() != null) {
            java.awt.datatransfer.StringSelection sel = new java.awt.datatransfer.StringSelection(recordatorioSeleccionado.getMensaje());
            java.awt.Toolkit.getDefaultToolkit().getSystemClipboard().setContents(sel, null);
            JOptionPane.showMessageDialog(this, "Mensaje copiado al portapapeles.", "Copiado", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // =========================================================================
    // 6. RENDERERS ESPECIALIZADOS Y HELPERS
    // =========================================================================

    public static String extraerIniciales(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) return "HP";
        String[] partes = nombre.trim().split("\\s+");
        if (partes.length >= 2) {
            return ("" + partes[0].charAt(0) + partes[1].charAt(0)).toUpperCase();
        } else if (nombre.length() >= 2) {
            return nombre.substring(0, 2).toUpperCase();
        }
        return nombre.toUpperCase();
    }

    public static JLabel crearAvatarCircular(String texto, int tamano, Color bg, Color fg) {
        JLabel lbl = new JLabel(texto, SwingConstants.CENTER) {
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
                g2.setColor(bg);
                g2.fillOval(x, y, d, d);
                g2.setColor(new Color(fg.getRed(), fg.getGreen(), fg.getBlue(), 80));
                g2.drawOval(x, y, d, d);
                g2.setColor(fg);
                g2.setFont(new Font("Segoe UI", Font.BOLD, tamano >= 36 ? 13 : 10));
                FontMetrics fm = g2.getFontMetrics();
                String t = getText();
                int tx = x + (d - fm.stringWidth(t)) / 2;
                int ty = y + (d - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(t, tx, ty);
                g2.dispose();
            }
        };
        lbl.setPreferredSize(new Dimension(tamano, tamano));
        lbl.setMinimumSize(new Dimension(tamano, tamano));
        lbl.setMaximumSize(new Dimension(tamano, tamano));
        lbl.setOpaque(false);
        return lbl;
    }

    private JLabel crearBadgePastilla(String texto, Color bg, Color fg) {
        JLabel l = new JLabel(texto, SwingConstants.CENTER) {
            private static final long serialVersionUID = 1L;
            @Override
            /**
             * Renderizado gráfico personalizado en Java2D.
             * Activa Antialiasing para bordes suaves y realiza el cálculo geométrico preciso.
             */
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        l.setFont(new Font("Segoe UI", Font.BOLD, 10));
        l.setForeground(fg);
        l.setBorder(new EmptyBorder(2, 8, 2, 8));
        return l;
    }

    private class PacienteTutorCellRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
            JPanel p = new JPanel(new BorderLayout(8, 0));
            p.setOpaque(true);
            p.setBackground(isSelected ? table.getSelectionBackground() : (row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252)));
            p.setBorder(new EmptyBorder(4, 8, 4, 8));

            if (value instanceof Object[]) {
                Object[] arr = (Object[]) value;
                String pet = arr[0] != null ? arr[0].toString() : "";
                String tutor = arr[1] != null ? arr[1].toString() : "";

                JLabel av = crearAvatarCircular(extraerIniciales(pet), 28, COLOR_AZUL_SUAVE, COLOR_AZUL_OSCURO);
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
                JPanel avWrap = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 4));
                avWrap.setOpaque(false);
                avWrap.add(av);
                p.add(avWrap, BorderLayout.WEST);

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
                JPanel pTxt = new JPanel();
                pTxt.setOpaque(false);
                pTxt.setLayout(new BoxLayout(pTxt, BoxLayout.Y_AXIS));

                JLabel lblPet = new JLabel(pet);
                lblPet.setFont(new Font("Segoe UI", Font.BOLD, 11));
                lblPet.setForeground(COLOR_TEXTO_TITULO);

                JLabel lblTut = new JLabel(tutor);
                lblTut.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                lblTut.setForeground(COLOR_TEXTO_MUTED);

                pTxt.add(lblPet);
                pTxt.add(Box.createVerticalStrut(1));
                pTxt.add(lblTut);
                p.add(pTxt, BorderLayout.CENTER);
            }
            return p;
        }
    }

    private class CanalBadgeCellRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            String canal = value != null ? value.toString() : "";
            Color bg = new Color(220, 252, 231);
            Color fg = new Color(22, 163, 74);
            if ("SMS".equalsIgnoreCase(canal)) {
                bg = new Color(224, 242, 254);
                fg = new Color(2, 132, 199);
            } else if ("E-Mail".equalsIgnoreCase(canal)) {
                bg = new Color(243, 232, 255);
                fg = new Color(147, 51, 234);
            }

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
            JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 8));
            p.setOpaque(true);
            p.setBackground(isSelected ? table.getSelectionBackground() : (row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252)));

            JLabel b = crearBadgePastilla(canal, bg, fg);
            p.add(b);
            return p;
        }
    }

    private class EstadoBadgeCellRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            String est = value != null ? value.toString() : "";
            Color bg = new Color(220, 252, 231);
            Color fg = new Color(22, 163, 74);
            if ("Pendiente".equalsIgnoreCase(est)) {
                bg = new Color(254, 243, 199);
                fg = new Color(217, 119, 6);
            } else if ("Enviado".equalsIgnoreCase(est)) {
                bg = new Color(224, 242, 254);
                fg = new Color(2, 132, 199);
            }

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
            JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 8));
            p.setOpaque(true);
            p.setBackground(isSelected ? table.getSelectionBackground() : (row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252)));

            JLabel b = crearBadgePastilla(est, bg, fg);
            p.add(b);
            return p;
        }
    }

    private static class CenteredCellRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;
        public CenteredCellRenderer() {
            setHorizontalAlignment(SwingConstants.CENTER);
        }
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (!isSelected) {
                c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252));
            }
            return c;
        }
    }

    private JButton crearBotonPill(String texto, boolean activo) {
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
                boolean act = Boolean.TRUE.equals(getClientProperty("activo"));
                if (act) {
                    g2.setColor(COLOR_AZUL_PRIMARIO);
                } else if (getModel().isRollover()) {
                    g2.setColor(new Color(241, 245, 249));
                } else {
                    g2.setColor(Color.WHITE);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g2.setColor(act ? COLOR_AZUL_PRIMARIO : COLOR_BORDE);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 6, 6);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        btn.putClientProperty("activo", activo);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 10));
        btn.setForeground(activo ? Color.WHITE : new Color(71, 85, 105));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(140, 26));
        return btn;
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
                boolean esPrimario = Boolean.TRUE.equals(getClientProperty("primario"));
                if (esPrimario) {
                    g2.setColor(getModel().isRollover() ? COLOR_AZUL_OSCURO : COLOR_AZUL_PRIMARIO);
                } else {
                    g2.setColor(getModel().isRollover() ? new Color(241, 245, 249) : Color.WHITE);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                if (!esPrimario) {
                    g2.setColor(COLOR_BORDE);
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 6, 6);
                }
                super.paintComponent(g2);
                g2.dispose();
            }

            @Override
            public Dimension getPreferredSize() {
                Dimension d = super.getPreferredSize();
                return new Dimension(d.width + 12, 28);
            }
        };
        btn.putClientProperty("primario", primario);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btn.setForeground(primario ? Color.WHITE : new Color(51, 65, 85));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(4, 10, 4, 10));
        return btn;
    }
}
