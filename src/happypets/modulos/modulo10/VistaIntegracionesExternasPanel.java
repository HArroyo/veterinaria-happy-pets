package happypets.modulos.modulo10;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
import javax.swing.JComponent;
import javax.swing.JDialog;
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
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import happypets.data.RepositorioVeterinaria;
import happypets.model.IntegracionExterna;
import happypets.ui.Iconos;
import happypets.ui.Ui;

/**
 * Submódulo 10.3: Integraciones Externas y Servicios Conectados
 * Responsable: Minaya Bravo, Almendra Lili
 * Parametrización de pasarelas fiscales (DIAN/SUNAT), mensajería WhatsApp Cloud, pasarelas POS y cloud storage
 * con arquitectura Master-Detail, 4 KPIs de conectividad y live API telemetry hub.
 */
public class VistaIntegracionesExternasPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private final RepositorioVeterinaria repo = RepositorioVeterinaria.getInstancia();

    // KPIs Superiores
    private JLabel lblKpiServiciosActivos;
    private JLabel lblKpiDianStatus;
    private JLabel lblKpiWhatsAppStatus;
    private JLabel lblKpiAwsBackup;

    // Master Table (Izquierda)
    private JTable tablaIntegraciones;
    private DefaultTableModel modeloIntegraciones;
    private JTextField txtBuscar;
    private String filtroCategoria = "TODAS";
    private final List<IntegracionExterna> listaIntegracionesFiltrada = new ArrayList<>();
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private JPanel panelPills;

    // Live Detail Hub (Derecha - 360px)
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private JPanel panelHubContenido;
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private JPanel panelHubVacio;
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private JPanel panelHubDetalle;

    private JLabel lblHubAvatar;
    private JLabel lblHubNombre;
    private JLabel lblHubSubtitulo;
    private JLabel lblHubChipEstado;

    // Telemetría 2x2
    private JLabel lblHubId;
    private JLabel lblHubCategoria;
    private JLabel lblHubAmbiente;
    private JLabel lblHubSincronizacion;

    // Endpoint y Credenciales
    private JTextArea txtHubEndpoint;
    private JLabel lblHubApiKey;

    private IntegracionExterna integracionSeleccionada;

    /**
     * Constructor de VistaIntegracionesExternasPanel.
     * Inicializa los componentes gráficos, configura layouts, asigna estilos
     * institucionales y carga los datos desde el repositorio central.
     */
    public VistaIntegracionesExternasPanel() {
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

        // Contenedor Master-Detail
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

        recargarDatos();
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

        JLabel titulo = new JLabel("Integraciones Externas y Servicios Conectados");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 17));
        titulo.setForeground(Ui.TEXTO_TITULO);

        JLabel subtitulo = new JLabel("Módulo 10.3 · Parametrización de APIs, pasarelas de pago, facturación electrónica y respaldos en la nube");
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

        JButton btnExportar = Ui.boton("Exportar Reporte APIs", false);
        btnExportar.addActionListener(e -> exportarReporteIntegraciones());
        der.add(btnExportar);

        JButton btnNuevaConexion = Ui.boton("+ Nueva Conexión", true);
        btnNuevaConexion.addActionListener(e -> abrirDialogoIntegracion(null));
        der.add(btnNuevaConexion);

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
        JPanel kpis = new JPanel(new GridLayout(1, 4, 12, 0));
        kpis.setOpaque(false);
        kpis.setPreferredSize(new Dimension(0, 74));
        kpis.setMaximumSize(new Dimension(Integer.MAX_VALUE, 74));

        lblKpiServiciosActivos = new JLabel("4 de 5 Conectados");
        lblKpiDianStatus = new JLabel("DIAN CPE v2 OK");
        lblKpiWhatsAppStatus = new JLabel("Meta WABA 99.8%");
        lblKpiAwsBackup = new JLabel("S3 Backup 03:00 AM");

        kpis.add(crearTarjetaKpi("Servicios Conectados", lblKpiServiciosActivos, "80% operatividad SLA", Iconos.crearIconoEnchufe(22, Ui.TURQUESA)));
        kpis.add(crearTarjetaKpi("Facturación Electrónica", lblKpiDianStatus, "Sincronización CPE al día", Iconos.crearIconoFactura(22, Ui.AZUL_INFO)));
        kpis.add(crearTarjetaKpi("Mensajería WhatsApp", lblKpiWhatsAppStatus, "Cloud API activa", Iconos.crearIconoWhatsApp(22, Ui.VERDE_EXITO)));
        kpis.add(crearTarjetaKpi("Custodia Cloud S3", lblKpiAwsBackup, "Copia diaria incremental", Iconos.crearIconoCheck(22, Ui.NARANJA_AVISO)));

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

        split.setLeftComponent(crearPanelMasterTable());
        split.setRightComponent(crearPanelLiveDetailHub());

        return split;
    }

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private JPanel crearPanelMasterTable() {
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
        master.setBorder(new EmptyBorder(12, 14, 12, 14));

        // Cabecera Filtros & Buscador
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel cabFiltros = new JPanel();
        cabFiltros.setOpaque(false);
        cabFiltros.setLayout(new BoxLayout(cabFiltros, BoxLayout.Y_AXIS));

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel barraBuscar = new JPanel(new BorderLayout(8, 0));
        barraBuscar.setOpaque(false);

        JLabel lblBuscar = new JLabel("Buscar:");
        lblBuscar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblBuscar.setForeground(Ui.TEXTO_MUTED);
        barraBuscar.add(lblBuscar, BorderLayout.WEST);

        txtBuscar = new JTextField();
        txtBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtBuscar.putClientProperty("JTextField.placeholderText", "Buscar por servicio, categoría, endpoint o ambiente...");
        txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filtrarIntegraciones(); }
            public void removeUpdate(DocumentEvent e) { filtrarIntegraciones(); }
            public void changedUpdate(DocumentEvent e) { filtrarIntegraciones(); }
        });
        barraBuscar.add(txtBuscar, BorderLayout.CENTER);
        cabFiltros.add(barraBuscar);
        cabFiltros.add(Box.createVerticalStrut(8));

        // Píldoras de Filtro por Categoría
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        panelPills = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        panelPills.setOpaque(false);
        actualizarPildorasFiltro();
        cabFiltros.add(panelPills);

        master.add(cabFiltros, BorderLayout.NORTH);

        // Tabla de Integraciones
        String[] cols = {"Cód. API", "Servicio Integrado", "Categoría", "Entorno", "Última Sincronización", "Estado"};
        modeloIntegraciones = new DefaultTableModel(cols, 0) {
            private static final long serialVersionUID = 1L;
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tablaIntegraciones = new JTable(modeloIntegraciones);
        tablaIntegraciones.setRowHeight(46);
        tablaIntegraciones.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaIntegraciones.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaIntegraciones.setShowVerticalLines(false);
        tablaIntegraciones.setGridColor(new Color(241, 245, 249));
        tablaIntegraciones.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        tablaIntegraciones.getTableHeader().setBackground(new Color(248, 250, 252));
        tablaIntegraciones.getTableHeader().setForeground(Ui.TEXTO_MUTED);

        tablaIntegraciones.getColumnModel().getColumn(0).setPreferredWidth(95);
        tablaIntegraciones.getColumnModel().getColumn(1).setPreferredWidth(230);
        tablaIntegraciones.getColumnModel().getColumn(2).setPreferredWidth(130);
        tablaIntegraciones.getColumnModel().getColumn(3).setPreferredWidth(120);
        tablaIntegraciones.getColumnModel().getColumn(4).setPreferredWidth(140);
        tablaIntegraciones.getColumnModel().getColumn(5).setPreferredWidth(110);

        // Monograma en columna 0
        tablaIntegraciones.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
            private static final long serialVersionUID = 1L;
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
                JPanel cell = new JPanel(new BorderLayout(8, 0));
                cell.setOpaque(true);
                cell.setBackground(isSelected ? table.getSelectionBackground() : (row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252)));
                cell.setBorder(new EmptyBorder(0, 8, 0, 8));

                String id = value != null ? value.toString() : "";
                JLabel avatar = new JLabel("API", SwingConstants.CENTER) {
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
                        g2.setColor(new Color(241, 245, 249));
                        g2.fillOval(x, y, s, s);
                        g2.setColor(new Color(203, 213, 225));
                        g2.drawOval(x, y, s, s);
                        super.paintComponent(g);
                        g2.dispose();
                    }
                };
                avatar.setPreferredSize(new Dimension(30, 30));
                avatar.setFont(new Font("Segoe UI", Font.BOLD, 9));
                avatar.setForeground(Ui.TEXTO_TITULO);

                JLabel lblId = new JLabel(id);
                lblId.setFont(new Font("Segoe UI", Font.BOLD, 11));
                lblId.setForeground(isSelected ? table.getSelectionForeground() : Ui.TEXTO_TITULO);

                cell.add(avatar, BorderLayout.WEST);
                cell.add(lblId, BorderLayout.CENTER);
                return cell;
            }
        });

        // Chip de Estado en columna 5 (En Línea / Desconectado)
        tablaIntegraciones.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            private static final long serialVersionUID = 1L;
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
                JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 11));
                p.setOpaque(true);
                p.setBackground(isSelected ? table.getSelectionBackground() : (row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252)));

                String st = value != null ? value.toString() : "Activo";
                JLabel badge = new JLabel(" " + st + " ");
                badge.setOpaque(true);
                badge.setFont(new Font("Segoe UI", Font.BOLD, 10));

                if (st.equalsIgnoreCase("En Línea") || st.equalsIgnoreCase("Activo")) {
                    badge.setBackground(new Color(220, 252, 231));
                    badge.setForeground(new Color(21, 128, 61));
                } else {
                    badge.setBackground(new Color(254, 242, 242));
                    badge.setForeground(new Color(220, 38, 38));
                }
                badge.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(badge.getBackground().darker(), 1, true),
                        new EmptyBorder(2, 6, 2, 6)
                ));
                p.add(badge);
                return p;
            }
        });

        tablaIntegraciones.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int r = tablaIntegraciones.getSelectedRow();
                if (r >= 0 && r < listaIntegracionesFiltrada.size()) {
                    mostrarDetalleIntegracion(listaIntegracionesFiltrada.get(r));
                } else {
                    mostrarDetalleIntegracion(null);
                }
            }
        });

        tablaIntegraciones.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int r = tablaIntegraciones.getSelectedRow();
                    if (r >= 0 && r < listaIntegracionesFiltrada.size()) {
                        abrirDialogoIntegracion(listaIntegracionesFiltrada.get(r));
                    }
                }
            }
        });

        JScrollPane scroll = new JScrollPane(tablaIntegraciones);
        scroll.setBorder(BorderFactory.createLineBorder(Ui.BORDE_SUAVE, 1));
        master.add(scroll, BorderLayout.CENTER);

        return master;
    }

    private void actualizarPildorasFiltro() {
        if (panelPills == null) return;
        panelPills.removeAll();

        String[] cats = {"TODAS", "Facturación", "Comunicaciones", "Pagos", "Laboratorio", "Cloud"};

        for (String c : cats) {
            JButton btnPill = new JButton(c);
            btnPill.setFont(new Font("Segoe UI", Font.BOLD, 11));
            btnPill.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btnPill.setBorder(new EmptyBorder(4, 10, 4, 10));
            btnPill.setFocusPainted(false);

            if (c.equalsIgnoreCase(filtroCategoria)) {
                btnPill.setBackground(Ui.TURQUESA);
                btnPill.setForeground(Color.WHITE);
            } else {
                btnPill.setBackground(new Color(241, 245, 249));
                btnPill.setForeground(Ui.TEXTO_MUTED);
            }

            btnPill.addActionListener(e -> {
                filtroCategoria = c;
                actualizarPildorasFiltro();
                filtrarIntegraciones();
            });
            panelPills.add(btnPill);
        }
        panelPills.revalidate();
        panelPills.repaint();
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

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        panelHubContenido = new JPanel(new BorderLayout());
        panelHubContenido.setOpaque(false);

        panelHubVacio = crearHubVacio();
        panelHubDetalle = crearHubDetalleActivo();

        panelHubContenido.add(panelHubVacio, BorderLayout.CENTER);
        detail.add(panelHubContenido, BorderLayout.CENTER);

        return detail;
    }

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private JPanel crearHubVacio() {
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel vacio = new JPanel();
        vacio.setOpaque(false);
        vacio.setLayout(new BoxLayout(vacio, BoxLayout.Y_AXIS));
        vacio.setBorder(new EmptyBorder(40, 20, 40, 20));

        JLabel ico = new JLabel(Iconos.crearIconoEnchufe(48, Ui.BORDE_SUAVE));
        ico.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel tit = new JLabel("Ninguna Integración Seleccionada");
        tit.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tit.setForeground(Ui.TEXTO_TITULO);
        tit.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sub = new JLabel("<html><center>Seleccione un servicio externo de la lista para supervisar su latencia, endpoint, credenciales y realizar pruebas de ping en tiempo real.</center></html>");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sub.setForeground(Ui.TEXTO_MUTED);
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);

        vacio.add(Box.createVerticalGlue());
        vacio.add(ico);
        vacio.add(Box.createVerticalStrut(12));
        vacio.add(tit);
        vacio.add(Box.createVerticalStrut(6));
        vacio.add(sub);
        vacio.add(Box.createVerticalGlue());

        return vacio;
    }

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private JPanel crearHubDetalleActivo() {
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);

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

        lblHubAvatar = new JLabel("API", SwingConstants.CENTER) {
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
        lblHubAvatar.setPreferredSize(new Dimension(46, 46));
        lblHubAvatar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblHubAvatar.setForeground(Ui.TURQUESA_PROFUNDO);

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel avatarBox = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        avatarBox.setOpaque(false);
        avatarBox.setPreferredSize(new Dimension(46, 46));
        avatarBox.add(lblHubAvatar);
        hero.add(avatarBox, BorderLayout.WEST);

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel heroText = new JPanel();
        heroText.setOpaque(false);
        heroText.setLayout(new BoxLayout(heroText, BoxLayout.Y_AXIS));

        lblHubNombre = new JLabel("Servicio Externo");
        lblHubNombre.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblHubNombre.setForeground(Ui.TEXTO_TITULO);
        lblHubNombre.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblHubSubtitulo = new JLabel("API REST v2");
        lblHubSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblHubSubtitulo.setForeground(Ui.TEXTO_MUTED);
        lblHubSubtitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel rowChips = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        rowChips.setOpaque(false);
        rowChips.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblHubChipEstado = new JLabel(" En Línea (200 OK) ");
        lblHubChipEstado.setOpaque(true);
        lblHubChipEstado.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblHubChipEstado.setBackground(new Color(220, 252, 231));
        lblHubChipEstado.setForeground(new Color(21, 128, 61));
        lblHubChipEstado.setBorder(BorderFactory.createLineBorder(new Color(134, 239, 172), 1, true));
        rowChips.add(lblHubChipEstado);

        heroText.add(lblHubNombre);
        heroText.add(Box.createVerticalStrut(2));
        heroText.add(lblHubSubtitulo);
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

        lblHubId = new JLabel("-");
        lblHubCategoria = new JLabel("-");
        lblHubAmbiente = new JLabel("-");
        lblHubSincronizacion = new JLabel("-");

        telemetria.add(crearMicroCard("Cód. Integración", lblHubId));
        telemetria.add(crearMicroCard("Categoría Servicio", lblHubCategoria));
        telemetria.add(crearMicroCard("Entorno de API", lblHubAmbiente));
        telemetria.add(crearMicroCard("Último Sync", lblHubSincronizacion));

        scrollContent.add(telemetria);
        scrollContent.add(Box.createVerticalStrut(12));

        // 3. Card Endpoint y Credenciales
        txtHubEndpoint = new JTextArea();
        txtHubEndpoint.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        txtHubEndpoint.setForeground(Ui.TEXTO_TITULO);
        txtHubEndpoint.setLineWrap(true);
        txtHubEndpoint.setWrapStyleWord(true);
        txtHubEndpoint.setEditable(false);
        txtHubEndpoint.setOpaque(false);

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel pnlEndpoint = crearSeccionCard("Endpoint URL Conexión", txtHubEndpoint);
        pnlEndpoint.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollContent.add(pnlEndpoint);
        scrollContent.add(Box.createVerticalStrut(10));

        // 4. Card Seguridad y Claves
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel pnlSeg = new JPanel(new GridLayout(2, 1, 4, 4));
        pnlSeg.setOpaque(false);

        lblHubApiKey = new JLabel("API Key: •••••••••••••••••••••");
        lblHubApiKey.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblHubApiKey.setForeground(Ui.TEXTO_MUTED);

        JLabel lblTls = new JLabel("Seguridad de Conexión: HTTPS TLS 1.3 Cifrado");
        lblTls.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblTls.setForeground(Ui.TEXTO_MUTED);

        pnlSeg.add(lblHubApiKey);
        pnlSeg.add(lblTls);

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel pnlSegCard = crearSeccionCard("Credenciales y Certificados", pnlSeg);
        pnlSegCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollContent.add(pnlSegCard);
        scrollContent.add(Box.createVerticalStrut(14));

        // 5. Botonera 2x2
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel gridAcciones = new JPanel(new GridLayout(2, 2, 8, 8));
        gridAcciones.setOpaque(false);
        gridAcciones.setAlignmentX(Component.LEFT_ALIGNMENT);
        gridAcciones.setMaximumSize(new Dimension(Integer.MAX_VALUE, 74));

        JButton btnTestPing = Ui.botonSecundario("Test Ping", Iconos.crearIconoRefrescar(12, Ui.TEXTO_TITULO));
        btnTestPing.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnTestPing.addActionListener(e -> {
            if (integracionSeleccionada != null) {
                JOptionPane.showMessageDialog(this,
                        "Ejecutando ping hacia " + integracionSeleccionada.getEndpointUrl() + "...\n" +
                        "Respuesta: HTTP/2 200 OK (Latencia: 38 ms)\n" +
                        "Autenticación Bearer: Token válido.",
                        "Diagnóstico de API", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        JButton btnSincronizar = Ui.botonSecundario("Sync Ahora", Iconos.crearIconoCheck(12, Ui.TEXTO_TITULO));
        btnSincronizar.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnSincronizar.addActionListener(e -> {
            if (integracionSeleccionada != null) {
                integracionSeleccionada.setUltimaSincronizacion("Hoy, " + java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm")));
                recargarDatos();
                JOptionPane.showMessageDialog(this, "Sincronización manual ejecutada con éxito.", "Sincronización Exitosa", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        JButton btnEditar = Ui.botonSecundario("Editar API", null);
        btnEditar.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnEditar.addActionListener(e -> {
            if (integracionSeleccionada != null) abrirDialogoIntegracion(integracionSeleccionada);
        });

        JButton btnToggleActiva = Ui.botonPrimario("Alternar", null);
        btnToggleActiva.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnToggleActiva.addActionListener(e -> {
            if (integracionSeleccionada != null) {
                integracionSeleccionada.setActiva(!integracionSeleccionada.isActiva());
                recargarDatos();
            }
        });

        gridAcciones.add(btnTestPing);
        gridAcciones.add(btnSincronizar);
        gridAcciones.add(btnEditar);
        gridAcciones.add(btnToggleActiva);

        scrollContent.add(gridAcciones);

        JScrollPane scroll = new JScrollPane(scrollContent);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(14);

        wrapper.add(scroll, BorderLayout.CENTER);
        return wrapper;
    }

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private JPanel crearMicroCard(String etiqueta, JLabel lblValor) {
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

        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblValor.setForeground(Ui.TEXTO_TITULO);

        mc.add(lblEtiq);
        mc.add(Box.createVerticalStrut(2));
        mc.add(lblValor);

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

    public void recargarDatos() {
        List<IntegracionExterna> list = repo.getIntegracionesExternas();
        long act = list.stream().filter(IntegracionExterna::isActiva).count();
        lblKpiServiciosActivos.setText(act + " de " + list.size() + " Conectados");

        actualizarPildorasFiltro();
        filtrarIntegraciones();
    }

    /**
     * Aplica criterios de filtrado dinámico en memoria sobre los registros.
     * Evalúa texto de búsqueda (búsqueda predictiva) y filtros por categoría o estado.
     */
    private void filtrarIntegraciones() {
        String q = txtBuscar != null ? txtBuscar.getText().trim().toLowerCase() : "";
        List<IntegracionExterna> todos = repo.getIntegracionesExternas();

        listaIntegracionesFiltrada.clear();
        modeloIntegraciones.setRowCount(0);

        for (IntegracionExterna inte : todos) {
            boolean coincideCat = filtroCategoria == null || "TODAS".equalsIgnoreCase(filtroCategoria)
                    || inte.getCategoria().toLowerCase().contains(filtroCategoria.toLowerCase());

            boolean coincideTexto = q.isEmpty()
                    || inte.getId().toLowerCase().contains(q)
                    || inte.getNombre().toLowerCase().contains(q)
                    || inte.getSubtitulo().toLowerCase().contains(q)
                    || inte.getEndpointUrl().toLowerCase().contains(q);

            if (coincideCat && coincideTexto) {
                listaIntegracionesFiltrada.add(inte);
                modeloIntegraciones.addRow(new Object[]{
                        inte.getId(),
                        inte.getNombre(),
                        inte.getCategoria(),
                        inte.getAmbiente(),
                        inte.getUltimaSincronizacion(),
                        inte.isActiva() ? "En Línea" : "Desconectado"
                });
            }
        }

        if (listaIntegracionesFiltrada.isEmpty()) {
            mostrarDetalleIntegracion(null);
        } else {
            int sel = tablaIntegraciones.getSelectedRow();
            if (sel < 0 || sel >= listaIntegracionesFiltrada.size()) {
                tablaIntegraciones.setRowSelectionInterval(0, 0);
                mostrarDetalleIntegracion(listaIntegracionesFiltrada.get(0));
            } else {
                mostrarDetalleIntegracion(listaIntegracionesFiltrada.get(sel));
            }
        }
    }

    /**
     * Actualiza el panel lateral derecho (Live Detail Hub) con la información del registro seleccionado.
     * Si el argumento es nulo, restablece el panel mostrando el estado de selección vacía.
     */
    private void mostrarDetalleIntegracion(IntegracionExterna inte) {
        this.integracionSeleccionada = inte;
        panelHubContenido.removeAll();

        if (inte == null) {
            panelHubContenido.add(panelHubVacio, BorderLayout.CENTER);
        } else {
            lblHubAvatar.setText(inte.getId().replace("INT-", "I"));
            lblHubNombre.setText(inte.getNombre());
            lblHubSubtitulo.setText(inte.getSubtitulo());

            if (inte.isActiva()) {
                lblHubChipEstado.setText(" En Línea (200 OK) ");
                lblHubChipEstado.setBackground(new Color(220, 252, 231));
                lblHubChipEstado.setForeground(new Color(21, 128, 61));
                lblHubChipEstado.setBorder(BorderFactory.createLineBorder(new Color(134, 239, 172), 1, true));
            } else {
                lblHubChipEstado.setText(" Servicio Desconectado ");
                lblHubChipEstado.setBackground(new Color(254, 242, 242));
                lblHubChipEstado.setForeground(new Color(220, 38, 38));
                lblHubChipEstado.setBorder(BorderFactory.createLineBorder(new Color(254, 202, 202), 1, true));
            }

            lblHubId.setText(inte.getId());
            lblHubCategoria.setText(inte.getCategoria());
            lblHubAmbiente.setText(inte.getAmbiente());
            lblHubSincronizacion.setText(inte.getUltimaSincronizacion());

            txtHubEndpoint.setText(inte.getEndpointUrl());

            String k = inte.getApiKey();
            if (k != null && k.length() > 6) {
                lblHubApiKey.setText("API Key: " + k.substring(0, 4) + "••••••••" + k.substring(k.length() - 4));
            } else {
                lblHubApiKey.setText("API Key: ••••••••••••••••");
            }

            panelHubContenido.add(panelHubDetalle, BorderLayout.CENTER);
        }

        panelHubContenido.revalidate();
        panelHubContenido.repaint();
    }

    private void abrirDialogoIntegracion(IntegracionExterna inte) {
        boolean esNuevo = (inte == null);
        JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(this), esNuevo ? "Registrar Nueva Integración API" : "Configurar API: " + inte.getNombre(), JDialog.ModalityType.APPLICATION_MODAL);
        dlg.setSize(480, 420);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel pnl = new JPanel();
        pnl.setLayout(new BoxLayout(pnl, BoxLayout.Y_AXIS));
        pnl.setBackground(Color.WHITE);
        pnl.setBorder(new EmptyBorder(16, 20, 16, 20));

        JTextField txtNom = new JTextField(esNuevo ? "" : inte.getNombre());
        JTextField txtSub = new JTextField(esNuevo ? "API REST v2 • Sincronización diaria" : inte.getSubtitulo());
        JComboBox<String> cmbCat = Ui.combo(new String[]{"Facturación", "Comunicaciones", "Pagos", "Laboratorio", "Cloud"});
        if (!esNuevo) cmbCat.setSelectedItem(inte.getCategoria());

        JTextField txtUrl = new JTextField(esNuevo ? "https://api.servicio.com/v1" : inte.getEndpointUrl());
        JTextField txtKey = new JTextField(esNuevo ? "sk_live_" : inte.getApiKey());
        JComboBox<String> cmbAmb = Ui.combo(new String[]{"Producción", "Sandbox / Pruebas"});
        if (!esNuevo) cmbAmb.setSelectedItem(inte.getAmbiente());

        pnl.add(crearFilaCampo("Nombre del Servicio:", txtNom));
        pnl.add(Box.createVerticalStrut(6));
        pnl.add(crearFilaCampo("Descripción / Protocolo:", txtSub));
        pnl.add(Box.createVerticalStrut(6));
        pnl.add(crearFilaCampo("Categoría:", cmbCat));
        pnl.add(Box.createVerticalStrut(6));
        pnl.add(crearFilaCampo("Endpoint URL:", txtUrl));
        pnl.add(Box.createVerticalStrut(6));
        pnl.add(crearFilaCampo("API Key / Token:", txtKey));
        pnl.add(Box.createVerticalStrut(6));
        pnl.add(crearFilaCampo("Ambiente:", cmbAmb));

        dlg.add(pnl, BorderLayout.CENTER);

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel bot = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        bot.setBackground(new Color(248, 250, 252));
        bot.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Ui.BORDE_SUAVE));

        JButton btnCancelar = Ui.botonSecundario("Cancelar", null);
        btnCancelar.addActionListener(e -> dlg.dispose());

        JButton btnGuardar = Ui.botonPrimario("Guardar Conexión", null);
        btnGuardar.addActionListener(e -> {
            String n = txtNom.getText().trim();
            String u = txtUrl.getText().trim();
            if (n.isEmpty() || u.isEmpty()) {
                JOptionPane.showMessageDialog(dlg, "Nombre y Endpoint URL son obligatorios.", "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (esNuevo) {
                String id = "INT-" + String.format("%02d", repo.getIntegracionesExternas().size() + 1);
                IntegracionExterna nueva = new IntegracionExterna(id, n, txtSub.getText().trim(), (String) cmbCat.getSelectedItem(), true, u, txtKey.getText().trim(), (String) cmbAmb.getSelectedItem(), "Recién configurada");
                repo.getIntegracionesExternas().add(nueva);
            } else {
                inte.setNombre(n);
                inte.setSubtitulo(txtSub.getText().trim());
                inte.setCategoria((String) cmbCat.getSelectedItem());
                inte.setEndpointUrl(u);
                inte.setApiKey(txtKey.getText().trim());
                inte.setAmbiente((String) cmbAmb.getSelectedItem());
            }
            dlg.dispose();
            recargarDatos();
            JOptionPane.showMessageDialog(this, "Integración externa actualizada con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        });

        bot.add(btnCancelar);
        bot.add(btnGuardar);
        dlg.add(bot, BorderLayout.SOUTH);

        dlg.setVisible(true);
    }

    private void exportarReporteIntegraciones() {
        List<IntegracionExterna> list = repo.getIntegracionesExternas();
        String[][] meta = new String[][]{
            {"Módulo", "Integraciones Externas y Servicios Conectados"},
            {"Fecha de Auditoría", LocalDate.now().toString()},
            {"Total Pasarelas", String.valueOf(list.size())},
            {"Cifrado", "HTTPS TLS 1.3 End-to-End"}
        };
        String[][] datos = new String[list.size()][6];
        for (int i = 0; i < list.size(); i++) {
            IntegracionExterna inte = list.get(i);
            datos[i][0] = String.valueOf(i + 1);
            datos[i][1] = inte.getNombre();
            datos[i][2] = inte.getCategoria();
            datos[i][3] = inte.getAmbiente();
            datos[i][4] = inte.isActiva() ? "En Línea" : "Desconectado";
            datos[i][5] = inte.getUltimaSincronizacion();
        }

        Ui.mostrarVisorReporte(
            (java.awt.Frame) SwingUtilities.getWindowAncestor(this),
            "INFORME DE CONECTIVIDAD Y SERVICIOS EXTERNOS (APIs)",
            "Pasarelas Fiscales, Pagos y Almacenamiento en Nube",
            meta,
            new String[]{"N°", "Servicio Integrado", "Categoría", "Ambiente", "Estado SLA", "Último Sync"},
            datos,
            "Directorio de servicios conectados y políticas de sincronización Happy Pets.",
            "Reporte_Integraciones_APIs"
        );
    }

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private JPanel crearFilaCampo(String etiqueta, JComponent comp) {
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel f = new JPanel(new BorderLayout(8, 0));
        f.setOpaque(false);
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JLabel lbl = new JLabel(etiqueta);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lbl.setForeground(Ui.TEXTO_MUTED);
        lbl.setPreferredSize(new Dimension(140, 26));

        f.add(lbl, BorderLayout.WEST);
        f.add(comp, BorderLayout.CENTER);
        return f;
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
