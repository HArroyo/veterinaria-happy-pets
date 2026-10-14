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
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDateTime;
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
import happypets.model.NotificacionSistema;
import happypets.ui.Iconos;
import happypets.ui.Ui;

/**
 * Submódulo 9.1: Centro de Notificaciones y Alertas
 * Responsable: Vera Aguilar, Carlos Edgardo
 * Bandeja unificada de alertas críticas, incidentes y eventos con arquitectura Master-Detail,
 * 4 KPIs ejecutivos de telemetría y dock de supervisión en tiempo real.
 */
public class VistaCentroNotificacionesPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private final RepositorioVeterinaria repo = RepositorioVeterinaria.getInstancia();

    // KPIs Superiores
    private JLabel lblKpiCriticas;
    private JLabel lblKpiMensajes;
    private JLabel lblKpiEventos;
    private JLabel lblKpiNoLeidas;

    // Master Table (Izquierda)
    private JTable tablaNotificaciones;
    private DefaultTableModel modeloNotificaciones;
    private JTextField txtBuscar;
    private String categoriaSeleccionada = "Todas";
    private final List<NotificacionSistema> listaNotificacionesFiltrada = new ArrayList<>();
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
    private JLabel lblHubTitulo;
    private JLabel lblHubTipo;
    private JLabel lblHubChipEstado;

    // Telemetría 2x2
    private JLabel lblHubId;
    private JLabel lblHubFecha;
    private JLabel lblHubCategoria;
    private JLabel lblHubOrigen;

    // Secciones de Detalle
    private JTextArea txtHubMensaje;
    private JLabel lblHubPrioridad;
    private JLabel lblHubTiempoRelativo;

    private NotificacionSistema notificacionSeleccionada;

    /**
     * Constructor de VistaCentroNotificacionesPanel.
     * Inicializa los componentes gráficos, configura layouts, asigna estilos
     * institucionales y carga los datos desde el repositorio central.
     */
    public VistaCentroNotificacionesPanel() {
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

        JLabel titulo = new JLabel("Centro de Notificaciones y Alertas");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 17));
        titulo.setForeground(Ui.TEXTO_TITULO);

        JLabel subtitulo = new JLabel("Módulo 9.1 · Monitoreo en vivo de incidentes de seguridad, alertas de capacidad y mensajes del sistema");
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

        JButton btnActualizar = Ui.boton("Actualizar", false);
        btnActualizar.addActionListener(e -> recargarDatos());
        der.add(btnActualizar);

        JButton btnNuevaAlerta = Ui.boton("+ Nueva Alerta", true);
        btnNuevaAlerta.addActionListener(e -> abrirDialogoNuevaAlerta());
        der.add(btnNuevaAlerta);

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

        lblKpiCriticas = new JLabel("0");
        lblKpiMensajes = new JLabel("0");
        lblKpiEventos = new JLabel("0");
        lblKpiNoLeidas = new JLabel("0");

        kpis.add(crearTarjetaKpi("Alertas Críticas", lblKpiCriticas, "Incidentes y seguridad", Iconos.crearIconoAlertaTriaje(22, Ui.ROJO_ERROR)));
        kpis.add(crearTarjetaKpi("Mensajes & Citas", lblKpiMensajes, "Canales y recordatorios", Iconos.crearIconoWhatsApp(22, Ui.TURQUESA)));
        kpis.add(crearTarjetaKpi("Eventos de Sistema", lblKpiEventos, "Transacciones y logs", Iconos.crearIconoCampana(22, Ui.AZUL_INFO)));
        kpis.add(crearTarjetaKpi("Pendientes Lectura", lblKpiNoLeidas, "Bandeja activa", Iconos.crearIconoCheck(22, Ui.NARANJA_AVISO)));

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
        txtBuscar.putClientProperty("JTextField.placeholderText", "Buscar por título, contenido, tipo u origen...");
        txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filtrarNotificaciones(); }
            public void removeUpdate(DocumentEvent e) { filtrarNotificaciones(); }
            public void changedUpdate(DocumentEvent e) { filtrarNotificaciones(); }
        });
        barraBuscar.add(txtBuscar, BorderLayout.CENTER);
        cabFiltros.add(barraBuscar);
        cabFiltros.add(Box.createVerticalStrut(8));

        // Píldoras de Filtro
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

        // Tabla de Notificaciones
        String[] cols = {"Cód. Alerta", "Tipo / Nivel", "Asunto Notificación", "Categoría", "Módulo Origen", "Emisión", "Estado"};
        modeloNotificaciones = new DefaultTableModel(cols, 0) {
            private static final long serialVersionUID = 1L;
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tablaNotificaciones = new JTable(modeloNotificaciones);
        tablaNotificaciones.setRowHeight(46);
        tablaNotificaciones.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaNotificaciones.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaNotificaciones.setShowVerticalLines(false);
        tablaNotificaciones.setGridColor(new Color(241, 245, 249));
        tablaNotificaciones.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        tablaNotificaciones.getTableHeader().setBackground(new Color(248, 250, 252));
        tablaNotificaciones.getTableHeader().setForeground(Ui.TEXTO_MUTED);

        tablaNotificaciones.getColumnModel().getColumn(0).setPreferredWidth(95);
        tablaNotificaciones.getColumnModel().getColumn(1).setPreferredWidth(140);
        tablaNotificaciones.getColumnModel().getColumn(2).setPreferredWidth(260);
        tablaNotificaciones.getColumnModel().getColumn(3).setPreferredWidth(95);
        tablaNotificaciones.getColumnModel().getColumn(4).setPreferredWidth(110);
        tablaNotificaciones.getColumnModel().getColumn(5).setPreferredWidth(110);
        tablaNotificaciones.getColumnModel().getColumn(6).setPreferredWidth(100);

        // Monograma en columna 0
        tablaNotificaciones.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
            private static final long serialVersionUID = 1L;
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
                JPanel cell = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 7));
                cell.setOpaque(true);
                cell.setBackground(isSelected ? table.getSelectionBackground() : (row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252)));

                String id = value != null ? value.toString() : "";
                JLabel avatar = new JLabel(id.replace("NOTIF-", "N"), SwingConstants.CENTER) {
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
                        g2.fillOval(0, 0, getWidth(), getHeight());
                        g2.setColor(new Color(203, 213, 225));
                        g2.drawOval(0, 0, getWidth() - 1, getHeight() - 1);
                        super.paintComponent(g);
                        g2.dispose();
                    }
                };
                avatar.setPreferredSize(new Dimension(30, 30));
                avatar.setFont(new Font("Segoe UI", Font.BOLD, 10));
                avatar.setForeground(Ui.TEXTO_TITULO);

                JLabel lblId = new JLabel(id);
                lblId.setFont(new Font("Segoe UI", Font.BOLD, 11));
                lblId.setForeground(isSelected ? table.getSelectionForeground() : Ui.TEXTO_TITULO);

                cell.add(avatar);
                cell.add(lblId);
                return cell;
            }
        });

        // Chip de Tipo en Columna 1
        tablaNotificaciones.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
            private static final long serialVersionUID = 1L;
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
                JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 11));
                p.setOpaque(true);
                p.setBackground(isSelected ? table.getSelectionBackground() : (row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252)));

                String st = value != null ? value.toString() : "";
                JLabel badge = new JLabel(" " + st + " ");
                badge.setOpaque(true);
                badge.setFont(new Font("Segoe UI", Font.BOLD, 10));

                Color fondo = new Color(241, 245, 249);
                Color texto = new Color(71, 85, 105);

                if (st.toUpperCase().contains("ALERTA") || st.toUpperCase().contains("CRITIC")) {
                    fondo = new Color(254, 226, 226);
                    texto = new Color(220, 38, 38);
                } else if (st.toUpperCase().contains("MENSAJE") || st.toUpperCase().contains("IMPORTANTE")) {
                    fondo = new Color(224, 242, 254);
                    texto = new Color(3, 105, 161);
                } else if (st.toUpperCase().contains("CLÍNICO") || st.toUpperCase().contains("ALTA")) {
                    fondo = new Color(220, 252, 231);
                    texto = new Color(21, 128, 61);
                } else {
                    fondo = Ui.TURQUESA_SUAVE;
                    texto = Ui.TURQUESA_PROFUNDO;
                }

                badge.setBackground(fondo);
                badge.setForeground(texto);
                badge.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(fondo.darker(), 1, true),
                        new EmptyBorder(2, 6, 2, 6)
                ));
                p.add(badge);
                return p;
            }
        });

        // Chip en columna 6 (Estado: Leída / No leída)
        tablaNotificaciones.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
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

                String st = value != null ? value.toString() : "";
                JLabel badge = new JLabel(" " + st + " ");
                badge.setOpaque(true);
                badge.setFont(new Font("Segoe UI", Font.BOLD, 10));

                if (st.equalsIgnoreCase("Leída")) {
                    badge.setBackground(new Color(241, 245, 249));
                    badge.setForeground(new Color(100, 116, 139));
                } else {
                    badge.setBackground(new Color(254, 243, 199));
                    badge.setForeground(new Color(180, 83, 9));
                }
                badge.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(badge.getBackground().darker(), 1, true),
                        new EmptyBorder(2, 6, 2, 6)
                ));
                p.add(badge);
                return p;
            }
        });

        tablaNotificaciones.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int r = tablaNotificaciones.getSelectedRow();
                if (r >= 0 && r < listaNotificacionesFiltrada.size()) {
                    mostrarDetalleNotificacion(listaNotificacionesFiltrada.get(r));
                } else {
                    mostrarDetalleNotificacion(null);
                }
            }
        });

        tablaNotificaciones.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int r = tablaNotificaciones.getSelectedRow();
                    if (r >= 0 && r < listaNotificacionesFiltrada.size()) {
                        abrirVisorReporteNotificacion(listaNotificacionesFiltrada.get(r));
                    }
                }
            }
        });

        JScrollPane scroll = new JScrollPane(tablaNotificaciones);
        scroll.setBorder(BorderFactory.createLineBorder(Ui.BORDE_SUAVE, 1));
        master.add(scroll, BorderLayout.CENTER);

        return master;
    }

    private void actualizarPildorasFiltro() {
        if (panelPills == null) return;
        panelPills.removeAll();

        int tot = repo.getNotificaciones().size();
        int alert = repo.contarNotificacionesPorCategoria("Alerta");
        int msgs = repo.contarNotificacionesPorCategoria("Mensaje");
        int evts = repo.contarNotificacionesPorCategoria("Evento");

        String[] pildoras = {
            "Todas (" + tot + ")",
            "Alerta (" + alert + ")",
            "Mensaje (" + msgs + ")",
            "Evento (" + evts + ")"
        };
        String[] valores = {"Todas", "Alerta", "Mensaje", "Evento"};

        for (int i = 0; i < pildoras.length; i++) {
            final String cat = valores[i];
            JButton btnPill = new JButton(pildoras[i]);
            btnPill.setFont(new Font("Segoe UI", Font.BOLD, 11));
            btnPill.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btnPill.setBorder(new EmptyBorder(4, 10, 4, 10));
            btnPill.setFocusPainted(false);

            if (cat.equalsIgnoreCase(categoriaSeleccionada)) {
                btnPill.setBackground(Ui.TURQUESA);
                btnPill.setForeground(Color.WHITE);
            } else {
                btnPill.setBackground(new Color(241, 245, 249));
                btnPill.setForeground(Ui.TEXTO_MUTED);
            }

            btnPill.addActionListener(e -> {
                categoriaSeleccionada = cat;
                actualizarPildorasFiltro();
                filtrarNotificaciones();
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

        JLabel ico = new JLabel(Iconos.crearIconoCampana(48, Ui.BORDE_SUAVE));
        ico.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel tit = new JLabel("Ninguna Alerta Seleccionada");
        tit.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tit.setForeground(Ui.TEXTO_TITULO);
        tit.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sub = new JLabel("<html><center>Seleccione un registro de la lista de auditoría para inspeccionar su trazabilidad, severidad y mensaje completo.</center></html>");
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

        lblHubAvatar = new JLabel("AL", SwingConstants.CENTER) {
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
                g2.setColor(new Color(254, 242, 242));
                g2.fillOval(x, y, s, s);
                g2.setColor(new Color(254, 202, 202));
                g2.drawOval(x, y, s, s);
                super.paintComponent(g);
                g2.dispose();
            }
        };
        lblHubAvatar.setPreferredSize(new Dimension(46, 46));
        lblHubAvatar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblHubAvatar.setForeground(Ui.ROJO_ERROR);

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

        lblHubTitulo = new JLabel("Título Notificación");
        lblHubTitulo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblHubTitulo.setForeground(Ui.TEXTO_TITULO);
        lblHubTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblHubTipo = new JLabel("ALERTA CRÍTICA");
        lblHubTipo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblHubTipo.setForeground(Ui.ROJO_ERROR);
        lblHubTipo.setAlignmentX(Component.LEFT_ALIGNMENT);

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel rowChips = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        rowChips.setOpaque(false);
        rowChips.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblHubChipEstado = new JLabel(" No leída ");
        lblHubChipEstado.setOpaque(true);
        lblHubChipEstado.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblHubChipEstado.setBackground(new Color(254, 243, 199));
        lblHubChipEstado.setForeground(new Color(180, 83, 9));
        lblHubChipEstado.setBorder(BorderFactory.createLineBorder(new Color(252, 211, 77), 1, true));
        rowChips.add(lblHubChipEstado);

        heroText.add(lblHubTitulo);
        heroText.add(Box.createVerticalStrut(2));
        heroText.add(lblHubTipo);
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
        lblHubFecha = new JLabel("-");
        lblHubCategoria = new JLabel("-");
        lblHubOrigen = new JLabel("-");

        telemetria.add(crearMicroCard("Cód. Identificador", lblHubId));
        telemetria.add(crearMicroCard("Emisión Registro", lblHubFecha));
        telemetria.add(crearMicroCard("Categoría ERP", lblHubCategoria));
        telemetria.add(crearMicroCard("Módulo Emisor", lblHubOrigen));

        scrollContent.add(telemetria);
        scrollContent.add(Box.createVerticalStrut(12));

        // 3. Card Mensaje y Detalle
        txtHubMensaje = new JTextArea();
        txtHubMensaje.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtHubMensaje.setForeground(Ui.TEXTO_TITULO);
        txtHubMensaje.setLineWrap(true);
        txtHubMensaje.setWrapStyleWord(true);
        txtHubMensaje.setEditable(false);
        txtHubMensaje.setOpaque(false);

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel pnlMensaje = crearSeccionCard("Contenido Completo de la Alerta", txtHubMensaje);
        pnlMensaje.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollContent.add(pnlMensaje);
        scrollContent.add(Box.createVerticalStrut(10));

        // 4. Card Metadatos de Auditoría
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel pnlMeta = new JPanel(new GridLayout(2, 1, 4, 4));
        pnlMeta.setOpaque(false);

        lblHubPrioridad = new JLabel("Prioridad de Despacho: Alta");
        lblHubPrioridad.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblHubPrioridad.setForeground(Ui.TEXTO_MUTED);

        lblHubTiempoRelativo = new JLabel("Tiempo Transcurrido: Reciente");
        lblHubTiempoRelativo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblHubTiempoRelativo.setForeground(Ui.TEXTO_MUTED);

        pnlMeta.add(lblHubPrioridad);
        pnlMeta.add(lblHubTiempoRelativo);

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel pnlMetaCard = crearSeccionCard("Parámetros de Auditoría", pnlMeta);
        pnlMetaCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollContent.add(pnlMetaCard);
        scrollContent.add(Box.createVerticalStrut(14));

        // 5. Botonera 2x2 de Acciones
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel gridAcciones = new JPanel(new GridLayout(2, 2, 8, 8));
        gridAcciones.setOpaque(false);
        gridAcciones.setAlignmentX(Component.LEFT_ALIGNMENT);
        gridAcciones.setMaximumSize(new Dimension(Integer.MAX_VALUE, 74));

        JButton btnAlternarLeida = Ui.botonSecundario("Alternar Lectura", Iconos.crearIconoCheck(12, Ui.TEXTO_TITULO));
        btnAlternarLeida.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnAlternarLeida.addActionListener(e -> alternarEstadoLectura());

        JButton btnVisorPDF = Ui.botonSecundario("Descargar PDF", Iconos.crearIconoDescargar(12, Ui.TEXTO_TITULO));
        btnVisorPDF.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnVisorPDF.addActionListener(e -> abrirVisorReporteNotificacion(notificacionSeleccionada));

        JButton btnNuevaAlertaDock = Ui.botonPrimario("+ Alerta", null);
        btnNuevaAlertaDock.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnNuevaAlertaDock.addActionListener(e -> abrirDialogoNuevaAlerta());

        JButton btnLimpiar = Ui.botonSecundario("Limpiar Leídas", null);
        btnLimpiar.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnLimpiar.addActionListener(e -> {
            repo.limpiarAlertasLeidas();
            recargarDatos();
            JOptionPane.showMessageDialog(this,
                    "Se han depurado las notificaciones marcadas como leídas.",
                    "Limpieza de Alertas", JOptionPane.INFORMATION_MESSAGE);
        });

        gridAcciones.add(btnAlternarLeida);
        gridAcciones.add(btnVisorPDF);
        gridAcciones.add(btnNuevaAlertaDock);
        gridAcciones.add(btnLimpiar);

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
        // Actualizar KPIs
        int criticas = repo.contarNotificacionesPorCategoria("Alerta");
        int mensajes = repo.contarNotificacionesPorCategoria("Mensaje");
        int eventos = repo.contarNotificacionesPorCategoria("Evento");
        int noLeidas = 0;
        for (NotificacionSistema n : repo.getNotificaciones()) {
            if (!n.isLeida()) noLeidas++;
        }

        lblKpiCriticas.setText(String.valueOf(criticas));
        lblKpiMensajes.setText(String.valueOf(mensajes));
        lblKpiEventos.setText(String.valueOf(eventos));
        lblKpiNoLeidas.setText(String.valueOf(noLeidas));

        actualizarPildorasFiltro();
        filtrarNotificaciones();
    }

    /**
     * Aplica criterios de filtrado dinámico en memoria sobre los registros.
     * Evalúa texto de búsqueda (búsqueda predictiva) y filtros por categoría o estado.
     */
    private void filtrarNotificaciones() {
        String q = txtBuscar != null ? txtBuscar.getText().trim().toLowerCase() : "";
        List<NotificacionSistema> todas = repo.getNotificacionesPorCategoria(categoriaSeleccionada);

        listaNotificacionesFiltrada.clear();
        modeloNotificaciones.setRowCount(0);

        for (NotificacionSistema n : todas) {
            boolean coincideTexto = q.isEmpty()
                    || n.getId().toLowerCase().contains(q)
                    || n.getTitulo().toLowerCase().contains(q)
                    || n.getMensaje().toLowerCase().contains(q)
                    || n.getTipo().toLowerCase().contains(q)
                    || n.getOrigen().toLowerCase().contains(q);

            if (coincideTexto) {
                listaNotificacionesFiltrada.add(n);
                modeloNotificaciones.addRow(new Object[]{
                        n.getId(),
                        n.getTipo(),
                        n.getTitulo(),
                        n.getCategoria(),
                        n.getOrigen(),
                        n.getTiempoRelativo() != null && !n.getTiempoRelativo().isEmpty() ? n.getTiempoRelativo() : n.getFechaFormateada(),
                        n.isLeida() ? "Leída" : "No leída"
                });
            }
        }

        if (listaNotificacionesFiltrada.isEmpty()) {
            mostrarDetalleNotificacion(null);
        } else {
            // Seleccionar primer registro si no hay selección
            int sel = tablaNotificaciones.getSelectedRow();
            if (sel < 0 || sel >= listaNotificacionesFiltrada.size()) {
                tablaNotificaciones.setRowSelectionInterval(0, 0);
                mostrarDetalleNotificacion(listaNotificacionesFiltrada.get(0));
            } else {
                mostrarDetalleNotificacion(listaNotificacionesFiltrada.get(sel));
            }
        }
    }

    /**
     * Actualiza el panel lateral derecho (Live Detail Hub) con la información del registro seleccionado.
     * Si el argumento es nulo, restablece el panel mostrando el estado de selección vacía.
     */
    private void mostrarDetalleNotificacion(NotificacionSistema notif) {
        this.notificacionSeleccionada = notif;
        panelHubContenido.removeAll();

        if (notif == null) {
            panelHubContenido.add(panelHubVacio, BorderLayout.CENTER);
        } else {
            String tipo = notif.getTipo() != null ? notif.getTipo().toUpperCase() : "AVISO";
            lblHubAvatar.setText(notif.getId().replace("NOTIF-", "N"));
            lblHubTitulo.setText(notif.getTitulo());
            lblHubTipo.setText(tipo);

            if (tipo.contains("ALERTA") || tipo.contains("CRITIC")) {
                lblHubTipo.setForeground(Ui.ROJO_ERROR);
            } else if (tipo.contains("MENSAJE")) {
                lblHubTipo.setForeground(Ui.TURQUESA_PROFUNDO);
            } else {
                lblHubTipo.setForeground(Ui.AZUL_INFO);
            }

            if (notif.isLeida()) {
                lblHubChipEstado.setText(" Leída ");
                lblHubChipEstado.setBackground(new Color(241, 245, 249));
                lblHubChipEstado.setForeground(new Color(100, 116, 139));
                lblHubChipEstado.setBorder(BorderFactory.createLineBorder(new Color(203, 213, 225), 1, true));
            } else {
                lblHubChipEstado.setText(" No leída ");
                lblHubChipEstado.setBackground(new Color(254, 243, 199));
                lblHubChipEstado.setForeground(new Color(180, 83, 9));
                lblHubChipEstado.setBorder(BorderFactory.createLineBorder(new Color(252, 211, 77), 1, true));
            }

            lblHubId.setText(notif.getId());
            lblHubFecha.setText(notif.getFechaFormateada());
            lblHubCategoria.setText(notif.getCategoria());
            lblHubOrigen.setText(notif.getOrigen());

            txtHubMensaje.setText(notif.getMensaje());
            lblHubPrioridad.setText("Prioridad de Despacho: " + (notif.getPrioridad() != null ? notif.getPrioridad() : "Media"));
            lblHubTiempoRelativo.setText("Tiempo Transcurrido: " + (notif.getTiempoRelativo() != null ? notif.getTiempoRelativo() : "Reciente"));

            panelHubContenido.add(panelHubDetalle, BorderLayout.CENTER);
        }

        panelHubContenido.revalidate();
        panelHubContenido.repaint();
    }

    private void alternarEstadoLectura() {
        if (notificacionSeleccionada == null) return;
        boolean nuevoEstado = !notificacionSeleccionada.isLeida();
        notificacionSeleccionada.setLeida(nuevoEstado);
        recargarDatos();
    }

    private void abrirVisorReporteNotificacion(NotificacionSistema notif) {
        if (notif == null) return;
        String[][] meta = new String[][]{
            {"Identificador", notif.getId()},
            {"Tipo / Severidad", notif.getTipo()},
            {"Categoría", notif.getCategoria()},
            {"Módulo Emisor", notif.getOrigen()},
            {"Fecha y Hora", notif.getFechaFormateada()},
            {"Estado de Lectura", notif.isLeida() ? "Leída" : "No leída"}
        };
        String[][] datos = new String[][]{
            {"1", notif.getId(), notif.getTipo(), notif.getOrigen(), notif.getMensaje()}
        };
        Ui.mostrarVisorReporte(
            (java.awt.Frame) SwingUtilities.getWindowAncestor(this),
            "REPORTE OFICIAL DE ALERTA O MENSAJE ERP",
            notif.getTitulo(),
            meta,
            new String[]{"N°", "Cód. Notificación", "Tipo", "Origen", "Detalle / Contenido"},
            datos,
            "Pista de auditoría interna y notificación de sistema Happy Pets.",
            "Notificacion_" + notif.getId()
        );
    }

    private void abrirDialogoNuevaAlerta() {
        JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(this), "Emitir Nueva Alerta o Notificación de Sistema", JDialog.ModalityType.APPLICATION_MODAL);
        dlg.setSize(480, 440);
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

        JLabel lblTit = new JLabel("Configurar Emisión de Notificación");
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTit.setForeground(Ui.TEXTO_TITULO);
        pnl.add(lblTit);
        pnl.add(Box.createVerticalStrut(12));

        JTextField txtTitulo = new JTextField();
        txtTitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        JComboBox<String> cmbCategoria = Ui.combo(new String[]{"Alerta", "Mensaje", "Evento"});
        JComboBox<String> cmbTipo = Ui.combo(new String[]{"NUEVA ALERTA CRÍTICA", "ALERTA DE SEGURIDAD", "MENSAJE IMPORTANTE", "EVENTO DEL SISTEMA", "AVISO OPERATIVO"});
        JComboBox<String> cmbOrigen = Ui.combo(new String[]{"Seguridad", "Servidor", "Almacenamiento", "Auditoría", "Farmacia", "Quirófano"});
        JComboBox<String> cmbPrioridad = Ui.combo(new String[]{"Alta", "Media", "Baja"});
        JTextArea txtMsg = new JTextArea(4, 20);
        txtMsg.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtMsg.setLineWrap(true);
        txtMsg.setWrapStyleWord(true);
        JScrollPane scMsg = new JScrollPane(txtMsg);

        pnl.add(crearFilaCampo("Título / Asunto:", txtTitulo));
        pnl.add(Box.createVerticalStrut(6));
        pnl.add(crearFilaCampo("Categoría:", cmbCategoria));
        pnl.add(Box.createVerticalStrut(6));
        pnl.add(crearFilaCampo("Etiqueta / Tipo:", cmbTipo));
        pnl.add(Box.createVerticalStrut(6));
        pnl.add(crearFilaCampo("Módulo de Origen:", cmbOrigen));
        pnl.add(Box.createVerticalStrut(6));
        pnl.add(crearFilaCampo("Prioridad:", cmbPrioridad));
        pnl.add(Box.createVerticalStrut(6));

        JLabel lblM = new JLabel("Mensaje / Detalle del Incidente:");
        lblM.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblM.setForeground(Ui.TEXTO_MUTED);
        pnl.add(lblM);
        pnl.add(Box.createVerticalStrut(4));
        pnl.add(scMsg);

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

        JButton btnGuardar = Ui.botonPrimario("Emitir Alerta", null);
        btnGuardar.addActionListener(e -> {
            String t = txtTitulo.getText().trim();
            String m = txtMsg.getText().trim();
            if (t.isEmpty() || m.isEmpty()) {
                JOptionPane.showMessageDialog(dlg, "Por favor ingrese el título y el contenido del mensaje.", "Campos requeridos", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String id = "NOTIF-" + String.format("%03d", repo.getNotificaciones().size() + 1);
            NotificacionSistema notifNueva = new NotificacionSistema(
                    id,
                    (String) cmbTipo.getSelectedItem(),
                    (String) cmbCategoria.getSelectedItem(),
                    t,
                    m,
                    LocalDateTime.now(),
                    "Ahora mismo",
                    false,
                    (String) cmbPrioridad.getSelectedItem(),
                    (String) cmbOrigen.getSelectedItem()
            );
            repo.agregarNotificacion(notifNueva);
            dlg.dispose();
            recargarDatos();
            JOptionPane.showMessageDialog(this, "Notificación emitida con éxito al sistema.", "Emisión Exitosa", JOptionPane.INFORMATION_MESSAGE);
        });

        bot.add(btnCancelar);
        bot.add(btnGuardar);
        dlg.add(bot, BorderLayout.SOUTH);

        dlg.setVisible(true);
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
        lbl.setPreferredSize(new Dimension(130, 26));

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
