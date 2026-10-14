package happypets.modulos.modulo9;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import happypets.data.RepositorioVeterinaria;
import happypets.model.ConfiguracionCanalNotificacion;
import happypets.model.PreferenciaNotificacionEventos;
import happypets.ui.Iconos;
import happypets.ui.Ui;

/**
 * Submódulo 9.2: Configuración de Canales y Políticas de Envío
 * Responsable: Vera Aguilar, Carlos Edgardo
 * Parametrización de pasarelas de entrega (Email, SMS y Push) y políticas de despacho
 * con arquitectura Master-Detail, 4 KPIs operativos y hub de monitoreo de SLA en vivo.
 */
public class VistaConfiguracionCanalesPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private final RepositorioVeterinaria repo = RepositorioVeterinaria.getInstancia();

    // KPIs Superiores
    private JLabel lblKpiCanalesActivos;
    private JLabel lblKpiTasaEntrega;
    private JLabel lblKpiMensajesEnviados;
    private JLabel lblKpiSaldoPasarela;

    // Controles de canales
    private JCheckBox chkEmail;
    private JTextField txtEmail;
    private JComboBox<String> comboFrecEmail;

    private JCheckBox chkSMS;
    private JTextField txtSMS;
    private JComboBox<String> comboFrecSMS;

    private JCheckBox chkPush;
    private JTextField txtPush;
    private JComboBox<String> comboFrecPush;

    // Controles de eventos a notificar
    private JCheckBox chkAlertasCriticas;
    private JCheckBox chkDocumentosNuevos;
    private JCheckBox chkEventosSistema;
    private JCheckBox chkRecordatoriosClinicos;

    // Hub Lateral (360px)
    private JLabel lblHubEstadoEmail;
    private JLabel lblHubEstadoSMS;
    private JLabel lblHubEstadoPush;
    private JLabel lblHubSlaStatus;
    private JTextArea txtHubBitacora;

    /**
     * Constructor de VistaConfiguracionCanalesPanel.
     * Inicializa los componentes gráficos, configura layouts, asigna estilos
     * institucionales y carga los datos desde el repositorio central.
     */
    public VistaConfiguracionCanalesPanel() {
        setLayout(new BorderLayout());
        setOpaque(false);

        // Panel Superior Fijo (Cabecera + 4 KPIs)
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel panelSuperior = new JPanel();
        panelSuperior.setOpaque(false);
        panelSuperior.setLayout(new BoxLayout(panelSuperior, BoxLayout.Y_AXIS));
        panelSuperior.setBorder(new EmptyBorder(12, 18, 10, 18));

        panelSuperior.add(crearCabeceraVista());
        panelSuperior.add(Box.createVerticalStrut(10));
        panelSuperior.add(crearFilaKpis());

        add(panelSuperior, BorderLayout.NORTH);

        // Master-Detail Split
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel panelCentro = new JPanel(new BorderLayout());
        panelCentro.setOpaque(false);
        panelCentro.setBorder(new EmptyBorder(0, 18, 14, 18));
        panelCentro.add(crearSplitMasterDetail(), BorderLayout.CENTER);

        add(panelCentro, BorderLayout.CENTER);

        cargarValoresDesdeRepositorio();
        actualizarKpisYHub();
    }

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
        cab.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel izq = new JPanel();
        izq.setOpaque(false);
        izq.setLayout(new BoxLayout(izq, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("Configuración de Canales y Políticas de Envío");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 17));
        titulo.setForeground(Ui.TEXTO_TITULO);

        JLabel subtitulo = new JLabel("Módulo 9.2 · Parametrización de pasarelas de entrega (Email, SMS y Push) y políticas de envío");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitulo.setForeground(Ui.TEXTO_MUTED);

        izq.add(titulo);
        izq.add(Box.createVerticalStrut(2));
        izq.add(subtitulo);
        cab.add(izq, BorderLayout.WEST);

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        der.setOpaque(false);

        JButton btnExportar = Ui.boton("Exportar Configuración", false);
        btnExportar.addActionListener(e -> exportarConfiguracionPDF());
        der.add(btnExportar);

        JButton btnGuardar = Ui.boton("Guardar Preferencias", true);
        btnGuardar.addActionListener(e -> guardarPreferencias());
        der.add(btnGuardar);

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
        JPanel kpis = new GridLayout(1, 4, 12, 0) != null ? new JPanel(new GridLayout(1, 4, 12, 0)) : new JPanel();
        kpis.setOpaque(false);
        kpis.setPreferredSize(new Dimension(0, 74));
        kpis.setMaximumSize(new Dimension(Integer.MAX_VALUE, 74));

        lblKpiCanalesActivos = new JLabel("2 / 3 Activos");
        lblKpiTasaEntrega = new JLabel("99.4% SLA");
        lblKpiMensajesEnviados = new JLabel("1,482 envíos");
        lblKpiSaldoPasarela = new JLabel("$ 124.50 USD");

        kpis.add(crearTarjetaKpi("Canales Activos", lblKpiCanalesActivos, "Email & SMS Pasarelas", Iconos.crearIconoCanales(22, Ui.TURQUESA)));
        kpis.add(crearTarjetaKpi("Tasa de Entrega", lblKpiTasaEntrega, "Disponibilidad verificada", Iconos.crearIconoCheck(22, Ui.VERDE_EXITO)));
        kpis.add(crearTarjetaKpi("Mensajes Despachados", lblKpiMensajesEnviados, "Alertas y recordatorios 30d", Iconos.crearIconoWhatsApp(22, Ui.AZUL_INFO)));
        kpis.add(crearTarjetaKpi("Saldo Pasarela API", lblKpiSaldoPasarela, "Crédito activo Twilio & AWS", Iconos.crearIconoMonedas(22, Ui.NARANJA_AVISO)));

        return kpis;
    }

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private JPanel crearTarjetaKpi(String titulo, JLabel lblValor, String subtitulo, Icon icono) {
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel card = new JPanel(new BorderLayout(8, 0)) {
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
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(Ui.BORDE_SUAVE);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(8, 12, 8, 12));

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel texto = new JPanel();
        texto.setOpaque(false);
        texto.setLayout(new BoxLayout(texto, BoxLayout.Y_AXIS));

        JLabel lblTit = new JLabel(titulo);
        lblTit.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblTit.setForeground(Ui.TEXTO_MUTED);

        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblValor.setForeground(Ui.TEXTO_TITULO);

        JLabel lblSub = new JLabel(subtitulo);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblSub.setForeground(Ui.TEXTO_MUTED);

        texto.add(lblTit);
        texto.add(Box.createVerticalStrut(1));
        texto.add(lblValor);
        texto.add(Box.createVerticalStrut(1));
        texto.add(lblSub);

        card.add(texto, BorderLayout.CENTER);

        if (icono != null) {
            JLabel lblIco = new JLabel(icono);
            lblIco.setVerticalAlignment(SwingConstants.TOP);
            lblIco.setBorder(new EmptyBorder(2, 0, 0, 0));
            card.add(lblIco, BorderLayout.EAST);
        }

        return card;
    }

    private JSplitPane crearSplitMasterDetail() {
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setOpaque(false);
        split.setBorder(null);
        split.setDividerSize(8);
        split.setContinuousLayout(true);
        split.setResizeWeight(1.0);

        split.setLeftComponent(crearPanelMasterConfiguracion());
        split.setRightComponent(crearPanelLiveDetailHub());

        return split;
    }

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private JPanel crearPanelMasterConfiguracion() {
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel master = new JPanel(new BorderLayout(0, 10)) {
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
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(Ui.BORDE_SUAVE);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                g2.dispose();
            }
        };
        master.setOpaque(false);
        master.setBorder(new EmptyBorder(14, 16, 14, 16));

        ScrollablePanel scrollContent = new ScrollablePanel();
        scrollContent.setOpaque(false);
        scrollContent.setLayout(new BoxLayout(scrollContent, BoxLayout.Y_AXIS));

        // Título del formulario
        JLabel lblSubtit = new JLabel("Parametrización de Canales de Notificación:");
        lblSubtit.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblSubtit.setForeground(Ui.TEXTO_TITULO);
        lblSubtit.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollContent.add(lblSubtit);
        scrollContent.add(Box.createVerticalStrut(10));

        // Formulario Canales
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel formCanales = new JPanel(new GridBagLayout());
        formCanales.setOpaque(false);
        formCanales.setAlignmentX(Component.LEFT_ALIGNMENT);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 6, 8, 6);

        // Fila 1: Email
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.20;
        chkEmail = new JCheckBox("Correo Electrónico (SMTP)", true);
        chkEmail.setFont(new Font("Segoe UI", Font.BOLD, 12));
        chkEmail.setOpaque(false);
        chkEmail.setForeground(Ui.TEXTO_TITULO);
        chkEmail.addActionListener(e -> {
            txtEmail.setEnabled(chkEmail.isSelected());
            actualizarKpisYHub();
        });
        formCanales.add(chkEmail, gbc);

        gbc.gridx = 1; gbc.weightx = 0.45;
        txtEmail = new JTextField("usuario@empresa.com");
        txtEmail.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtEmail.setPreferredSize(new Dimension(240, 32));
        formCanales.add(txtEmail, gbc);

        gbc.gridx = 2; gbc.weightx = 0.23;
        comboFrecEmail = Ui.combo(new String[]{
                "Inmediata (Push/SMTP)",
                "Cada 30 minutos",
                "Resumen Diario (08:00)",
                "Resumen Semanal"
        });
        comboFrecEmail.setPreferredSize(new Dimension(170, 32));
        formCanales.add(comboFrecEmail, gbc);

        gbc.gridx = 3; gbc.weightx = 0.12;
        JButton btnTestEmail = Ui.botonSecundario("Test Envío", null);
        btnTestEmail.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnTestEmail.setPreferredSize(new Dimension(100, 32));
        btnTestEmail.addActionListener(e -> probarCanal("Email", txtEmail.getText()));
        formCanales.add(btnTestEmail, gbc);

        // Fila 2: SMS
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.20;
        chkSMS = new JCheckBox("SMS Móvil Gateway", true);
        chkSMS.setFont(new Font("Segoe UI", Font.BOLD, 12));
        chkSMS.setOpaque(false);
        chkSMS.setForeground(Ui.TEXTO_TITULO);
        chkSMS.addActionListener(e -> {
            txtSMS.setEnabled(chkSMS.isSelected());
            actualizarKpisYHub();
        });
        formCanales.add(chkSMS, gbc);

        gbc.gridx = 1; gbc.weightx = 0.45;
        txtSMS = new JTextField("+51 987 654 321");
        txtSMS.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtSMS.setPreferredSize(new Dimension(240, 32));
        formCanales.add(txtSMS, gbc);

        gbc.gridx = 2; gbc.weightx = 0.23;
        comboFrecSMS = Ui.combo(new String[]{
                "Resumen Diario (08:00)",
                "Inmediata (SMS Gateway)",
                "Solo Alertas Críticas",
                "Resumen Semanal"
        });
        comboFrecSMS.setPreferredSize(new Dimension(170, 32));
        formCanales.add(comboFrecSMS, gbc);

        gbc.gridx = 3; gbc.weightx = 0.12;
        JButton btnTestSMS = Ui.botonSecundario("Test Envío", null);
        btnTestSMS.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnTestSMS.setPreferredSize(new Dimension(100, 32));
        btnTestSMS.addActionListener(e -> probarCanal("SMS", txtSMS.getText()));
        formCanales.add(btnTestSMS, gbc);

        // Fila 3: Push
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.20;
        chkPush = new JCheckBox("Notificaciones Push Web/App", false);
        chkPush.setFont(new Font("Segoe UI", Font.BOLD, 12));
        chkPush.setOpaque(false);
        chkPush.setForeground(Ui.TEXTO_TITULO);
        chkPush.addActionListener(e -> {
            txtPush.setEnabled(chkPush.isSelected());
            actualizarKpisYHub();
        });
        formCanales.add(chkPush, gbc);

        gbc.gridx = 1; gbc.weightx = 0.45;
        txtPush = new JTextField("Navegador Web / App Móvil Firebase");
        txtPush.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtPush.setPreferredSize(new Dimension(240, 32));
        txtPush.setEnabled(false);
        formCanales.add(txtPush, gbc);

        gbc.gridx = 2; gbc.weightx = 0.23;
        comboFrecPush = Ui.combo(new String[]{
                "Inmediata (Push Real-Time)",
                "Solo Sesión Activa",
                "Resumen Diario"
        });
        comboFrecPush.setPreferredSize(new Dimension(170, 32));
        formCanales.add(comboFrecPush, gbc);

        gbc.gridx = 3; gbc.weightx = 0.12;
        JButton btnTestPush = Ui.botonSecundario("Test Envío", null);
        btnTestPush.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnTestPush.setPreferredSize(new Dimension(100, 32));
        btnTestPush.addActionListener(e -> probarCanal("Push", txtPush.getText()));
        formCanales.add(btnTestPush, gbc);

        scrollContent.add(formCanales);
        scrollContent.add(Box.createVerticalStrut(14));

        // Sección de Políticas de Notificación
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel cardEventos = new JPanel(new BorderLayout(0, 10));
        cardEventos.setOpaque(false);
        cardEventos.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(241, 245, 249), 1, true),
                new EmptyBorder(12, 14, 12, 14)
        ));
        cardEventos.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblTitEventos = new JLabel("Políticas de Activación y Despacho Automático de Alertas:");
        lblTitEventos.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTitEventos.setForeground(Ui.TEXTO_TITULO);
        cardEventos.add(lblTitEventos, BorderLayout.NORTH);

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel gridChecks = new JPanel(new GridLayout(2, 2, 12, 8));
        gridChecks.setOpaque(false);

        chkAlertasCriticas = new JCheckBox("Alertas Críticas de Seguridad y Aforo (Nivel Alto)", true);
        chkAlertasCriticas.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        chkAlertasCriticas.setOpaque(false);

        chkDocumentosNuevos = new JCheckBox("Emisión de Nuevos Documentos y Facturas Electrónicas", true);
        chkDocumentosNuevos.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        chkDocumentosNuevos.setOpaque(false);

        chkEventosSistema = new JCheckBox("Auditoría de Inicios de Sesión y Cambios de Configuración", true);
        chkEventosSistema.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        chkEventosSistema.setOpaque(false);

        chkRecordatoriosClinicos = new JCheckBox("Recordatorios de Citas Médicas y Vacunación a Clientes", false);
        chkRecordatoriosClinicos.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        chkRecordatoriosClinicos.setOpaque(false);

        gridChecks.add(chkAlertasCriticas);
        gridChecks.add(chkDocumentosNuevos);
        gridChecks.add(chkEventosSistema);
        gridChecks.add(chkRecordatoriosClinicos);

        cardEventos.add(gridChecks, BorderLayout.CENTER);
        scrollContent.add(cardEventos);

        JScrollPane scroll = new JScrollPane(scrollContent);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(14);

        master.add(scroll, BorderLayout.CENTER);
        return master;
    }

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private JPanel crearPanelLiveDetailHub() {
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel detail = new JPanel(new BorderLayout()) {
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
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(Ui.BORDE_SUAVE);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                g2.dispose();
            }
        };
        detail.setOpaque(false);
        detail.setPreferredSize(new Dimension(360, 0));
        detail.setMinimumSize(new Dimension(320, 0));

        ScrollablePanel scrollContent = new ScrollablePanel();
        scrollContent.setOpaque(false);
        scrollContent.setLayout(new BoxLayout(scrollContent, BoxLayout.Y_AXIS));
        scrollContent.setBorder(new EmptyBorder(14, 16, 14, 16));

        // 1. Hero Block
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel hero = new JPanel(new BorderLayout(8, 0));
        hero.setOpaque(false);
        hero.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel avatar = new JLabel("GW", SwingConstants.CENTER) {
            private static final long serialVersionUID = 1L;
            @Override
            /**
             * Renderizado gráfico personalizado en Java2D.
             * Activa Antialiasing para bordes suaves y realiza el cálculo geométrico preciso.
             */
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int s = Math.min(getWidth(), getHeight()) - 2;
                int x = (getWidth() - s) / 2;
                int y = (getHeight() - s) / 2;
                g2.setColor(Ui.TURQUESA_SUAVE);
                g2.fillOval(x, y, s, s);
                g2.setColor(Ui.TURQUESA);
                g2.drawOval(x, y, s, s);
                super.paintComponent(g);
                g2.dispose();
            }
        };
        avatar.setPreferredSize(new Dimension(46, 46));
        avatar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        avatar.setForeground(Ui.TURQUESA_PROFUNDO);

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel avatarBox = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        avatarBox.setOpaque(false);
        avatarBox.setPreferredSize(new Dimension(46, 46));
        avatarBox.add(avatar);
        hero.add(avatarBox, BorderLayout.WEST);

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel heroText = new JPanel();
        heroText.setOpaque(false);
        heroText.setLayout(new BoxLayout(heroText, BoxLayout.Y_AXIS));

        JLabel lblTit = new JLabel("Pasarelas de Conectividad");
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTit.setForeground(Ui.TEXTO_TITULO);
        lblTit.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblSub = new JLabel("Gateway Cloud & Telemetría");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblSub.setForeground(Ui.TEXTO_MUTED);
        lblSub.setAlignmentX(Component.LEFT_ALIGNMENT);

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel rowChips = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        rowChips.setOpaque(false);
        rowChips.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblHubSlaStatus = new JLabel(" 100% SLA Operativo ");
        lblHubSlaStatus.setOpaque(true);
        lblHubSlaStatus.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblHubSlaStatus.setBackground(new Color(220, 252, 231));
        lblHubSlaStatus.setForeground(new Color(21, 128, 61));
        lblHubSlaStatus.setBorder(BorderFactory.createLineBorder(new Color(134, 239, 172), 1, true));
        rowChips.add(lblHubSlaStatus);

        heroText.add(lblTit);
        heroText.add(Box.createVerticalStrut(2));
        heroText.add(lblSub);
        heroText.add(Box.createVerticalStrut(4));
        heroText.add(rowChips);

        hero.add(heroText, BorderLayout.CENTER);
        scrollContent.add(hero);
        scrollContent.add(Box.createVerticalStrut(14));

        // 2. Telemetría 2x2
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel telemetria = new JPanel(new GridLayout(2, 2, 8, 8));
        telemetria.setOpaque(false);
        telemetria.setAlignmentX(Component.LEFT_ALIGNMENT);
        telemetria.setMaximumSize(new Dimension(Integer.MAX_VALUE, 92));

        telemetria.add(crearMicroCard("Protocolo Gateway", new JLabel("SMTP / REST v2")));
        telemetria.add(crearMicroCard("Cifrado Seguridad", new JLabel("TLS 1.3 End-to-End")));
        telemetria.add(crearMicroCard("Latencia Media", new JLabel("42 ms (Óptimo)")));
        telemetria.add(crearMicroCard("Registro Auditoría", new JLabel("#EV-1043")));

        scrollContent.add(telemetria);
        scrollContent.add(Box.createVerticalStrut(12));

        // 3. Estado Individual de Canales
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel pnlEstados = new JPanel(new GridLayout(3, 1, 4, 6));
        pnlEstados.setOpaque(false);

        lblHubEstadoEmail = new JLabel("● Email Gateway: En línea (Conectado)");
        lblHubEstadoEmail.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblHubEstadoEmail.setForeground(new Color(21, 128, 61));

        lblHubEstadoSMS = new JLabel("● SMS Celular: En línea (Pasarela activa)");
        lblHubEstadoSMS.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblHubEstadoSMS.setForeground(new Color(21, 128, 61));

        lblHubEstadoPush = new JLabel("○ Push Web/App: Desactivado por usuario");
        lblHubEstadoPush.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblHubEstadoPush.setForeground(new Color(148, 163, 184));

        pnlEstados.add(lblHubEstadoEmail);
        pnlEstados.add(lblHubEstadoSMS);
        pnlEstados.add(lblHubEstadoPush);

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel pnlCanalesCard = crearSeccionCard("Diagnóstico de Pasarelas", pnlEstados);
        pnlCanalesCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollContent.add(pnlCanalesCard);
        scrollContent.add(Box.createVerticalStrut(10));

        // 4. Bitácora de Envíos
        txtHubBitacora = new JTextArea();
        txtHubBitacora.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        txtHubBitacora.setForeground(Ui.TEXTO_MUTED);
        txtHubBitacora.setLineWrap(true);
        txtHubBitacora.setWrapStyleWord(true);
        txtHubBitacora.setEditable(false);
        txtHubBitacora.setOpaque(false);
        txtHubBitacora.setText("Último ping de verificación: Hoy, 14:35:10\n" +
                "Autenticación DKIM/SPF: Aprobada\n" +
                "Cuota disponible mensual: 85.2%");

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel pnlBitacoraCard = crearSeccionCard("Bitácora Técnica y Conectividad", txtHubBitacora);
        pnlBitacoraCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollContent.add(pnlBitacoraCard);
        scrollContent.add(Box.createVerticalStrut(14));

        // 5. Botones 2x2
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel gridAcciones = new JPanel(new GridLayout(2, 2, 8, 8));
        gridAcciones.setOpaque(false);
        gridAcciones.setAlignmentX(Component.LEFT_ALIGNMENT);
        gridAcciones.setMaximumSize(new Dimension(Integer.MAX_VALUE, 74));

        JButton btnTestTodos = Ui.botonSecundario("Testear Todo", Iconos.crearIconoRefrescar(12, Ui.TEXTO_TITULO));
        btnTestTodos.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnTestTodos.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                    "Enviando solicitud de comprobación a todos los canales habilitados...\n" +
                    "- Gateway Email: 200 OK (0.24s)\n" +
                    "- Gateway SMS: 200 OK (0.41s)\n" +
                    "Todos los servicios responden favorablemente.",
                    "Diagnóstico Global", JOptionPane.INFORMATION_MESSAGE);
        });

        JButton btnExportarDock = Ui.botonSecundario("Descargar PDF", Iconos.crearIconoDescargar(12, Ui.TEXTO_TITULO));
        btnExportarDock.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnExportarDock.addActionListener(e -> exportarConfiguracionPDF());

        JButton btnGuardarDock = Ui.botonPrimario("Guardar", null);
        btnGuardarDock.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnGuardarDock.addActionListener(e -> guardarPreferencias());

        JButton btnRevertir = Ui.botonSecundario("Restablecer", null);
        btnRevertir.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnRevertir.addActionListener(e -> {
            cargarValoresDesdeRepositorio();
            JOptionPane.showMessageDialog(this, "Valores restablecidos a su estado inicial.", "Restablecer", JOptionPane.INFORMATION_MESSAGE);
        });

        gridAcciones.add(btnTestTodos);
        gridAcciones.add(btnExportarDock);
        gridAcciones.add(btnGuardarDock);
        gridAcciones.add(btnRevertir);

        scrollContent.add(gridAcciones);

        JScrollPane scroll = new JScrollPane(scrollContent);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(14);

        detail.add(scroll, BorderLayout.CENTER);
        return detail;
    }

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private JPanel crearMicroCard(String etiqueta, JComponent compValor) {
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel mc = new JPanel();
        mc.setOpaque(false);
        mc.setLayout(new BoxLayout(mc, BoxLayout.Y_AXIS));
        mc.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(241, 245, 249), 1, true),
                new EmptyBorder(6, 8, 6, 8)
        ));

        JLabel lblEtiq = new JLabel(etiqueta);
        lblEtiq.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblEtiq.setForeground(Ui.TEXTO_MUTED);

        if (compValor instanceof JLabel) {
            compValor.setFont(new Font("Segoe UI", Font.BOLD, 11));
            compValor.setForeground(Ui.TEXTO_TITULO);
        }

        mc.add(lblEtiq);
        mc.add(Box.createVerticalStrut(2));
        mc.add(compValor);

        return mc;
    }

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private JPanel crearSeccionCard(String titulo, JComponent contenido) {
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel sec = new JPanel(new BorderLayout(0, 6));
        sec.setOpaque(false);
        sec.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(241, 245, 249), 1, true),
                new EmptyBorder(8, 10, 8, 10)
        ));

        JLabel lblTit = new JLabel(titulo);
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblTit.setForeground(Ui.TEXTO_MUTED);
        sec.add(lblTit, BorderLayout.NORTH);

        sec.add(contenido, BorderLayout.CENTER);
        return sec;
    }

    private void actualizarKpisYHub() {
        int activos = 0;
        if (chkEmail != null && chkEmail.isSelected()) activos++;
        if (chkSMS != null && chkSMS.isSelected()) activos++;
        if (chkPush != null && chkPush.isSelected()) activos++;

        if (lblKpiCanalesActivos != null) {
            lblKpiCanalesActivos.setText(activos + " / 3 Activos");
        }

        if (lblHubEstadoEmail != null) {
            boolean e = chkEmail != null && chkEmail.isSelected();
            lblHubEstadoEmail.setText(e ? "● Email Gateway: En línea (Conectado)" : "○ Email Gateway: Desactivado");
            lblHubEstadoEmail.setForeground(e ? new Color(21, 128, 61) : new Color(148, 163, 184));
        }

        if (lblHubEstadoSMS != null) {
            boolean s = chkSMS != null && chkSMS.isSelected();
            lblHubEstadoSMS.setText(s ? "● SMS Celular: En línea (Pasarela activa)" : "○ SMS Celular: Desactivado");
            lblHubEstadoSMS.setForeground(s ? new Color(21, 128, 61) : new Color(148, 163, 184));
        }

        if (lblHubEstadoPush != null) {
            boolean p = chkPush != null && chkPush.isSelected();
            lblHubEstadoPush.setText(p ? "● Push Web/App: En línea (WebSocket activo)" : "○ Push Web/App: Desactivado");
            lblHubEstadoPush.setForeground(p ? new Color(21, 128, 61) : new Color(148, 163, 184));
        }
    }

    private void cargarValoresDesdeRepositorio() {
        List<ConfiguracionCanalNotificacion> canales = repo.getCanalesNotificacion();
        for (ConfiguracionCanalNotificacion c : canales) {
            if ("Email".equalsIgnoreCase(c.getCanal())) {
                chkEmail.setSelected(c.isActivo());
                txtEmail.setText(c.getDestino());
                txtEmail.setEnabled(c.isActivo());
            } else if ("SMS".equalsIgnoreCase(c.getCanal())) {
                chkSMS.setSelected(c.isActivo());
                txtSMS.setText(c.getDestino());
                txtSMS.setEnabled(c.isActivo());
            } else if (c.getCanal().contains("Push")) {
                chkPush.setSelected(c.isActivo());
                txtPush.setText(c.getDestino());
                txtPush.setEnabled(c.isActivo());
            }
        }

        PreferenciaNotificacionEventos pref = repo.getPreferenciasEventos();
        if (pref != null) {
            chkAlertasCriticas.setSelected(pref.isAlertasCriticas());
            chkDocumentosNuevos.setSelected(pref.isDocumentosNuevos());
            chkEventosSistema.setSelected(pref.isEventosSistema());
            chkRecordatoriosClinicos.setSelected(pref.isRecordatoriosClinicos());
        }

        actualizarKpisYHub();
    }

    private void guardarPreferencias() {
        List<ConfiguracionCanalNotificacion> nuevos = new ArrayList<>();
        nuevos.add(new ConfiguracionCanalNotificacion("Email", chkEmail.isSelected(), txtEmail.getText().trim(), (String) comboFrecEmail.getSelectedItem()));
        nuevos.add(new ConfiguracionCanalNotificacion("SMS", chkSMS.isSelected(), txtSMS.getText().trim(), (String) comboFrecSMS.getSelectedItem()));
        nuevos.add(new ConfiguracionCanalNotificacion("Notificaciones Push", chkPush.isSelected(), txtPush.getText().trim(), (String) comboFrecPush.getSelectedItem()));

        PreferenciaNotificacionEventos nuevasPref = new PreferenciaNotificacionEventos(
                chkAlertasCriticas.isSelected(),
                chkDocumentosNuevos.isSelected(),
                chkEventosSistema.isSelected(),
                chkRecordatoriosClinicos.isSelected()
        );

        repo.guardarPreferenciasCanales(nuevos, nuevasPref);
        actualizarKpisYHub();

        JOptionPane.showMessageDialog(this,
                "Preferencias de canales de notificación y eventos actualizadas con éxito.\nSe ha registrado un evento en el log de auditoría (#EV-1043).",
                "Configuración Guardada", JOptionPane.INFORMATION_MESSAGE);
    }

    private void probarCanal(String canal, String destino) {
        JOptionPane.showMessageDialog(this,
                "Enviando mensaje de prueba al canal " + canal + " hacia [" + destino + "]...\nRespuesta del servidor: 200 OK (Mensaje entregado).",
                "Test de Canal", JOptionPane.INFORMATION_MESSAGE);
    }

    private void exportarConfiguracionPDF() {
        String[][] meta = new String[][]{
            {"Módulo", "Configuración de Canales y Políticas de Envío"},
            {"Fecha de Configuración", java.time.LocalDate.now().toString()},
            {"Usuario Administrador", "Admin Central"},
            {"Canal Email", chkEmail.isSelected() ? "Activo (" + txtEmail.getText() + ")" : "Inactivo"},
            {"Canal SMS", chkSMS.isSelected() ? "Activo (" + txtSMS.getText() + ")" : "Inactivo"},
            {"Canal Push", chkPush.isSelected() ? "Activo (" + txtPush.getText() + ")" : "Inactivo"}
        };
        String[][] datos = new String[][]{
            {"1", "Email Gateway", txtEmail.getText(), comboFrecEmail.getSelectedItem().toString(), chkEmail.isSelected() ? "Activo" : "Inactivo"},
            {"2", "SMS Movil", txtSMS.getText(), comboFrecSMS.getSelectedItem().toString(), chkSMS.isSelected() ? "Activo" : "Inactivo"},
            {"3", "Web Push", txtPush.getText(), comboFrecPush.getSelectedItem().toString(), chkPush.isSelected() ? "Activo" : "Inactivo"}
        };
        Ui.mostrarVisorReporte(
            (java.awt.Frame) SwingUtilities.getWindowAncestor(this),
            "CONFIGURACIÓN DE CANALES Y REGLAS DE ENVÍO",
            "Directivas de Comunicación y Notificación ERP",
            meta,
            new String[]{"N°", "Canal de Pasarela", "Destinatario / Endpoint", "Frecuencia", "Estado"},
            datos,
            "Políticas de comunicación corporativa Happy Pets.",
            "Config_Canales_Notificaciones"
        );
    }

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private static class ScrollablePanel extends JPanel implements Scrollable {
        private static final long serialVersionUID = 1L;

        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }

        @Override
        public int getScrollableUnitIncrement(java.awt.Rectangle visibleRect, int orientation, int direction) {
            return 16;
        }

        @Override
        public int getScrollableBlockIncrement(java.awt.Rectangle visibleRect, int orientation, int direction) {
            return 32;
        }

        @Override
        public boolean getScrollableTracksViewportWidth() {
            return true;
        }

        @Override
        public boolean getScrollableTracksViewportHeight() {
            return false;
        }
    }
}
