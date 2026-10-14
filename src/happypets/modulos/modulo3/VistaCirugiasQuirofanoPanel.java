package happypets.modulos.modulo3;

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
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import happypets.data.RepositorioVeterinaria;
import happypets.model.Cliente;
import happypets.model.Mascota;
import happypets.model.RegistroCirugia;
import happypets.ui.Iconos;
import happypets.ui.Ui;

/**
 * Submódulo 3.3: Cirugías y Quirófano Veterinario.
 * Arquitectura Master-Detail (Opción A) con monitor de salas quirúrgicas en vivo,
 * protocolo anestésico intraoperatorio y emisión de actas quirúrgicas oficiales.
 */
public class VistaCirugiasQuirofanoPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private static final Color COLOR_BORDE = Ui.BORDE_SUAVE;
    private static final Color COLOR_AZUL_PRIMARIO = Ui.TURQUESA;
    private static final Color COLOR_TEXTO_TITULO = Ui.TEXTO_TITULO;
    private static final Color COLOR_TEXTO_MUTED = Ui.TEXTO_MUTED;

    private final RepositorioVeterinaria repo = RepositorioVeterinaria.getInstancia();

    // Filtros
    private JComboBox<String> cbFiltroQuirofano;
    private JComboBox<String> cbFiltroEstado;
    private JTextField txtBuscar;

    // KPIs
    private JLabel lblKpiQuirofanosActivos;
    private JLabel lblKpiCirugiasHoy;
    private JLabel lblKpiEnProcedimiento;
    private JLabel lblKpiEnRecuperacion;

    // Master Table
    private JTable tablaCirugias;
    private DefaultTableModel modeloCirugias;
    private JLabel lblContadorCirugias;
    private List<RegistroCirugia> listaActual;

    // Right Detail Hub (Acta Quirúrgica y Telemetría Postop)
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private JPanel panelDetalleHub;
    private RegistroCirugia cirugiaSeleccionada;

    /**
     * Constructor de VistaCirugiasQuirofanoPanel.
     * Inicializa los componentes gráficos, configura layouts, asigna estilos
     * institucionales y carga los datos desde el repositorio central.
     */
    public VistaCirugiasQuirofanoPanel() {
        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(new EmptyBorder(12, 16, 14, 16));

        // 1. Cabecera superior y KPIs
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel panelNorte = new JPanel();
        panelNorte.setOpaque(false);
        panelNorte.setLayout(new BoxLayout(panelNorte, BoxLayout.Y_AXIS));

        panelNorte.add(crearCabeceraVista());
        panelNorte.add(Box.createVerticalStrut(10));
        panelNorte.add(crearFilaKpis());
        panelNorte.add(Box.createVerticalStrut(10));

        add(panelNorte, BorderLayout.NORTH);

        // 2. Split Master-Detail Central (58% Master / 42% Detail)
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, crearPanelMaster(), crearPanelDetailWrapper());
        splitPane.setOpaque(false);
        splitPane.setBorder(null);
        splitPane.setContinuousLayout(true);
        splitPane.setDividerSize(8);
        splitPane.setResizeWeight(0.58);
        splitPane.setDividerLocation(675);

        add(splitPane, BorderLayout.CENTER);

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

        JLabel titulo = new JLabel("Cirugías y Quirófano Veterinario");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 17));
        titulo.setForeground(COLOR_TEXTO_TITULO);

        JLabel subtitulo = new JLabel("Módulo 3.3 · Programación quirúrgica, protocolo intraoperatorio, anestesia y seguimiento postquirúrgico");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitulo.setForeground(COLOR_TEXTO_MUTED);

        izq.add(titulo);
        izq.add(Box.createVerticalStrut(2));
        izq.add(subtitulo);
        cab.add(izq, BorderLayout.CENTER);

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        der.setOpaque(false);

        JLabel badgeBox = new JLabel("Quirófano 1: Listo · Quirófano 2: Disponible") {
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
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g2.setColor(new Color(153, 246, 228));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 6, 6);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        badgeBox.setFont(new Font("Segoe UI", Font.BOLD, 11));
        badgeBox.setForeground(new Color(15, 118, 110));
        badgeBox.setBorder(new EmptyBorder(5, 10, 5, 10));

        JButton btnNuevo = crearBotonAccion("+ Programar Cirugía", true);
        btnNuevo.setIcon(Iconos.crearIconoBisturi(13, Color.WHITE));
        btnNuevo.setIconTextGap(6);
        btnNuevo.addActionListener(e -> abrirModalNuevaCirugia(null));

        der.add(badgeBox);
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

        lblKpiQuirofanosActivos = new JLabel("2 / 2");
        lblKpiCirugiasHoy = new JLabel("0");
        lblKpiEnProcedimiento = new JLabel("0");
        lblKpiEnRecuperacion = new JLabel("0");

        fila.add(crearCardKpi(lblKpiQuirofanosActivos, "QUIRÓFANOS DISPONIBLES", Ui.TURQUESA_SUAVE, COLOR_AZUL_PRIMARIO, Iconos.crearIconoBisturi(18, COLOR_AZUL_PRIMARIO)));
        fila.add(crearCardKpi(lblKpiCirugiasHoy, "INTERVENCIONES TOTALES", new Color(224, 242, 254), new Color(2, 132, 199), Iconos.crearIconoCalendario(18, new Color(2, 132, 199))));
        fila.add(crearCardKpi(lblKpiEnProcedimiento, "EN PROCEDIMIENTO ACTIVO", new Color(254, 226, 226), new Color(220, 38, 38), Iconos.crearIconoAlertaTriaje(18, new Color(220, 38, 38))));
        fila.add(crearCardKpi(lblKpiEnRecuperacion, "EN RECUPERACIÓN / ALTA", new Color(220, 252, 231), new Color(22, 163, 74), Iconos.crearIconoCheck(18, new Color(22, 163, 74))));

        return fila;
    }

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private JPanel crearCardKpi(JLabel lblVal, String etiqueta, Color bgIco, Color fgIco, Icon icono) {
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
                g2.setColor(COLOR_BORDE);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(6, 12, 6, 12));

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
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
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

        lblVal.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblVal.setForeground(COLOR_TEXTO_TITULO);

        pText.add(lblEtq);
        pText.add(Box.createVerticalStrut(2));
        pText.add(lblVal);
        card.add(pText, BorderLayout.CENTER);

        return card;
    }

    // ==========================================
    // PANEL MASTER (TABLA DE CIRUGÍAS)
    // ==========================================
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private JPanel crearPanelMaster() {
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
        card.setBorder(new EmptyBorder(10, 12, 10, 12));

        // Barra de filtros
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel topFiltros = new JPanel(new BorderLayout(8, 0));
        topFiltros.setOpaque(false);

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel izqFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        izqFiltros.setOpaque(false);

        JLabel lblQ = new JLabel("Quirófano:");
        lblQ.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblQ.setForeground(COLOR_TEXTO_MUTED);
        izqFiltros.add(lblQ);

        cbFiltroQuirofano = new JComboBox<>(new String[]{"Todos los Quirófanos", "Quirófano 1", "Quirófano 2"});
        cbFiltroQuirofano.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        cbFiltroQuirofano.setBackground(Color.WHITE);
        cbFiltroQuirofano.setPreferredSize(new Dimension(140, 28));
        cbFiltroQuirofano.addActionListener(e -> filtrarDatos());
        izqFiltros.add(cbFiltroQuirofano);

        JLabel lblEs = new JLabel("Estado:");
        lblEs.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblEs.setForeground(COLOR_TEXTO_MUTED);
        izqFiltros.add(lblEs);

        cbFiltroEstado = new JComboBox<>(new String[]{"Todos los Estados", "Programada", "En Proceso", "En Recuperación", "Alta Quirúrgica"});
        cbFiltroEstado.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        cbFiltroEstado.setBackground(Color.WHITE);
        cbFiltroEstado.setPreferredSize(new Dimension(135, 28));
        cbFiltroEstado.addActionListener(e -> filtrarDatos());
        izqFiltros.add(cbFiltroEstado);

        topFiltros.add(izqFiltros, BorderLayout.WEST);

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel derFiltros = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        derFiltros.setOpaque(false);

        txtBuscar = new JTextField(12);
        txtBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        txtBuscar.setPreferredSize(new Dimension(125, 28));
        txtBuscar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225), 1),
                new EmptyBorder(2, 6, 2, 6)
        ));
        txtBuscar.addActionListener(e -> filtrarDatos());
        derFiltros.add(txtBuscar);

        JButton btnB = crearBotonAccion("Buscar", false);
        btnB.setIcon(Iconos.crearIconoBuscar(12, COLOR_AZUL_PRIMARIO));
        btnB.setIconTextGap(4);
        btnB.setPreferredSize(new Dimension(84, 28));
        btnB.addActionListener(e -> filtrarDatos());
        derFiltros.add(btnB);

        topFiltros.add(derFiltros, BorderLayout.EAST);
        card.add(topFiltros, BorderLayout.NORTH);

        // Tabla Master
        String[] columnas = {"Código / Hora", "Paciente / Especie", "Tutor / Teléfono", "Procedimiento Quirúrgico", "Cirujano / Quirófano", "Estado"};
        modeloCirugias = new DefaultTableModel(columnas, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        tablaCirugias = new JTable(modeloCirugias);
        tablaCirugias.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        Ui.formatearTabla(tablaCirugias, new int[]{0, 4, 5}, new int[]{});
        tablaCirugias.setRowHeight(48);
        tablaCirugias.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        tablaCirugias.getColumnModel().getColumn(0).setPreferredWidth(100);
        tablaCirugias.getColumnModel().getColumn(1).setPreferredWidth(120);
        tablaCirugias.getColumnModel().getColumn(2).setPreferredWidth(110);
        tablaCirugias.getColumnModel().getColumn(3).setPreferredWidth(145);
        tablaCirugias.getColumnModel().getColumn(4).setPreferredWidth(105);
        tablaCirugias.getColumnModel().getColumn(5).setPreferredWidth(105);

        tablaCirugias.getColumnModel().getColumn(0).setCellRenderer(new CodigoHoraCirugiaRenderer());
        tablaCirugias.getColumnModel().getColumn(1).setCellRenderer(new PacienteCirugiaRenderer());
        tablaCirugias.getColumnModel().getColumn(2).setCellRenderer(new TutorCirugiaRenderer());
        tablaCirugias.getColumnModel().getColumn(3).setCellRenderer(new ProcedimientoCirugiaRenderer());
        tablaCirugias.getColumnModel().getColumn(5).setCellRenderer(new BadgeEstadoCirugiaRenderer());

        tablaCirugias.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = tablaCirugias.getSelectedRow();
                if (row >= 0 && row < listaActual.size()) {
                    cirugiaSeleccionada = listaActual.get(row);
                } else {
                    cirugiaSeleccionada = null;
                }
                actualizarPanelDetalle();
            }
        });

        tablaCirugias.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && cirugiaSeleccionada != null) {
                    abrirModalEditarCirugia(cirugiaSeleccionada);
                }
            }
        });

        JScrollPane sp = new JScrollPane(tablaCirugias);
        sp.setBorder(BorderFactory.createLineBorder(COLOR_BORDE, 1));
        sp.getViewport().setBackground(Color.WHITE);
        card.add(sp, BorderLayout.CENTER);

        // Barra inferior del Master
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel botMaster = new JPanel(new BorderLayout());
        botMaster.setOpaque(false);

        lblContadorCirugias = new JLabel("0 cirugías registradas");
        lblContadorCirugias.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblContadorCirugias.setForeground(COLOR_TEXTO_MUTED);
        botMaster.add(lblContadorCirugias, BorderLayout.WEST);

        JLabel lblDobleClic = new JLabel("Doble clic en una fila para modificar protocolo quirúrgico");
        lblDobleClic.setFont(new Font("Segoe UI", Font.ITALIC, 10));
        lblDobleClic.setForeground(COLOR_TEXTO_MUTED);
        botMaster.add(lblDobleClic, BorderLayout.EAST);

        card.add(botMaster, BorderLayout.SOUTH);
        return card;
    }

    // ==========================================
    // PANEL DETAIL (ACTA QUIRÚRGICA EN VIVO)
    // ==========================================
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private JPanel crearPanelDetailWrapper() {
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        panelDetalleHub = new JPanel(new BorderLayout());
        panelDetalleHub.setOpaque(false);
        actualizarPanelDetalle();
        return panelDetalleHub;
    }

    private void actualizarPanelDetalle() {
        panelDetalleHub.removeAll();

        if (cirugiaSeleccionada == null) {
            panelDetalleHub.add(crearEstadoVacioDetalle(), BorderLayout.CENTER);
        } else {
            panelDetalleHub.add(crearCardDetalleCirugia(cirugiaSeleccionada), BorderLayout.CENTER);
        }

        panelDetalleHub.revalidate();
        panelDetalleHub.repaint();
    }

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private JPanel crearEstadoVacioDetalle() {
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel card = new JPanel(new BorderLayout()) {
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
        card.setBorder(new EmptyBorder(30, 20, 30, 20));

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel centro = new JPanel();
        centro.setOpaque(false);
        centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));

        JLabel ico = new JLabel(Iconos.crearIconoBisturi(48, new Color(148, 163, 184)));
        ico.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel tit = new JLabel("Acta Quirúrgica y Telemetría Intraoperatoria");
        tit.setFont(new Font("Segoe UI", Font.BOLD, 15));
        tit.setForeground(COLOR_TEXTO_TITULO);
        tit.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel msg = new JLabel("<html><center>Seleccione una intervención en la tabla para supervisar el procedimiento, protocolo anestésico, tiempos quirúrgicos, analgesia y emisión del acta médica.</center></html>");
        msg.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        msg.setForeground(COLOR_TEXTO_MUTED);
        msg.setAlignmentX(Component.CENTER_ALIGNMENT);
        msg.setMaximumSize(new Dimension(320, 80));

        centro.add(Box.createVerticalGlue());
        centro.add(ico);
        centro.add(Box.createVerticalStrut(12));
        centro.add(tit);
        centro.add(Box.createVerticalStrut(6));
        centro.add(msg);
        centro.add(Box.createVerticalGlue());

        card.add(centro, BorderLayout.CENTER);
        return card;
    }

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private JPanel crearCardDetalleCirugia(RegistroCirugia c) {
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
        card.setBorder(new EmptyBorder(12, 16, 12, 16));

        // 1. Cabecera con Avatar Circular, Datos y Banner de Estado
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel pTop = new JPanel();
        pTop.setOpaque(false);
        pTop.setLayout(new BoxLayout(pTop, BoxLayout.Y_AXIS));

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel pFilaAvatar = new JPanel(new BorderLayout(12, 0));
        pFilaAvatar.setOpaque(false);

        // Circular Avatar seguro
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel pAvatarWrap = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        pAvatarWrap.setOpaque(false);
        JLabel lblAvatar = new JLabel() {
            private static final long serialVersionUID = 1L;
            @Override
            /**
             * Renderizado gráfico personalizado en Java2D.
             * Activa Antialiasing para bordes suaves y realiza el cálculo geométrico preciso.
             */
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth();
                int h = getHeight();
                int d = Math.min(w, h) - 2;
                int x = (w - d) / 2;
                int y = (h - d) / 2;

                g2.setColor(new Color(254, 226, 226));
                g2.fillOval(x, y, d, d);
                g2.setColor(new Color(220, 38, 38));
                g2.setStroke(new java.awt.BasicStroke(2.0f));
                g2.drawOval(x, y, d, d);

                String ini = c.getNombreMascota().substring(0, Math.min(2, c.getNombreMascota().length())).toUpperCase();
                g2.setFont(new Font("Segoe UI", Font.BOLD, 18));
                g2.setColor(new Color(185, 28, 28));
                java.awt.FontMetrics fm = g2.getFontMetrics();
                int tx = (w - fm.stringWidth(ini)) / 2;
                int ty = (h + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(ini, tx, ty);
                g2.dispose();
            }
        };
        lblAvatar.setPreferredSize(new Dimension(54, 54));
        pAvatarWrap.add(lblAvatar);
        pFilaAvatar.add(pAvatarWrap, BorderLayout.WEST);

        // Datos del paciente
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel pInfoPaciente = new JPanel();
        pInfoPaciente.setOpaque(false);
        pInfoPaciente.setLayout(new BoxLayout(pInfoPaciente, BoxLayout.Y_AXIS));

        JLabel lblNom = new JLabel(c.getNombreMascota() + "  (" + c.getIdCirugia() + ")");
        lblNom.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblNom.setForeground(COLOR_TEXTO_TITULO);

        JLabel lblEsp = new JLabel(c.getEspecieRaza() + " · Tutor: " + c.getNombreTutor());
        lblEsp.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblEsp.setForeground(COLOR_TEXTO_MUTED);

        JLabel lblDet = new JLabel("Cirujano: " + c.getCirujanoPrincipal() + "  |  " + c.getQuirofano());
        lblDet.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblDet.setForeground(COLOR_TEXTO_MUTED);

        pInfoPaciente.add(lblNom);
        pInfoPaciente.add(Box.createVerticalStrut(2));
        pInfoPaciente.add(lblEsp);
        pInfoPaciente.add(Box.createVerticalStrut(2));
        pInfoPaciente.add(lblDet);
        pFilaAvatar.add(pInfoPaciente, BorderLayout.CENTER);

        pTop.add(pFilaAvatar);
        pTop.add(Box.createVerticalStrut(8));

        // Banner de Estado Quirúrgico
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel bannerEstado = new JPanel(new BorderLayout(8, 0)) {
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
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(COLOR_BORDE);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        bannerEstado.setOpaque(false);
        bannerEstado.setBorder(new EmptyBorder(6, 12, 6, 12));

        JLabel lblEstTexto = new JLabel("FASE: " + c.getEstado().toUpperCase());
        lblEstTexto.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblEstTexto.setForeground("En Proceso".equalsIgnoreCase(c.getEstado()) ? new Color(220, 38, 38) :
                                  "En Recuperación".equalsIgnoreCase(c.getEstado()) ? new Color(217, 119, 6) :
                                  "Alta Quirúrgica".equalsIgnoreCase(c.getEstado()) ? new Color(22, 163, 74) : new Color(2, 132, 199));
        lblEstTexto.setIcon(Iconos.crearIconoCheck(13, lblEstTexto.getForeground()));
        lblEstTexto.setIconTextGap(6);
        bannerEstado.add(lblEstTexto, BorderLayout.WEST);

        JLabel lblTiempos = new JLabel("Horario: " + c.getHoraProgramadaFormateada() + " (" + c.getHoraInicioFormateada() + " - " + c.getHoraFinFormateada() + ")");
        lblTiempos.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblTiempos.setForeground(COLOR_TEXTO_TITULO);
        bannerEstado.add(lblTiempos, BorderLayout.EAST);

        pTop.add(bannerEstado);
        card.add(pTop, BorderLayout.NORTH);

        // 2. Centro con Scroll interno: Triada Quirúrgica + Medicación + Protocolo
        ScrollablePanel pCentro = new ScrollablePanel();
        pCentro.setOpaque(false);
        pCentro.setLayout(new BoxLayout(pCentro, BoxLayout.Y_AXIS));

        // Grid 4 Métricas Quirúrgicas (2x2)
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel gridMetricas = new JPanel(new GridLayout(2, 2, 8, 8));
        gridMetricas.setOpaque(false);
        gridMetricas.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));

        gridMetricas.add(crearWidgetCirugia("SALA QUIRÚRGICA", c.getQuirofano().contains("Quirófano 1") ? "Quirófano 1 (Mayor)" : "Quirófano 2 (Menor)", "Esterilidad A1", COLOR_AZUL_PRIMARIO));
        gridMetricas.add(crearWidgetCirugia("PROTOCOLO ANESTÉSICO", c.getTipoAnestesia(), "Monitoreo", new Color(147, 51, 234)));
        gridMetricas.add(crearWidgetCirugia("ESCALA DE DOLOR POSTOP", c.getEscalaDolorPostop() + " / 10", c.getEscalaDolorPostop() <= 2 ? "Dolor Leve" : "Moderado",
                c.getEscalaDolorPostop() <= 2 ? new Color(22, 163, 74) : new Color(217, 119, 6)));
        gridMetricas.add(crearWidgetCirugia("RETIRO DE PUNTOS", c.getFechaRetiroPuntosFormateada(), "Seguimiento", new Color(100, 116, 139)));

        gridMetricas.setAlignmentX(Component.LEFT_ALIGNMENT);
        pCentro.add(gridMetricas);
        pCentro.add(Box.createVerticalStrut(8));

        // Procedimiento y Equipo
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel pProc = crearCajaSeccion("PROCEDIMIENTO Y EQUIPO MÉDICO",
                "<b>Intervención:</b> <span style='color:#0f766e;'><b>" + c.getProcedimiento() + "</b></span><br>" +
                "<b>Cirujano Principal:</b> " + c.getCirujanoPrincipal() + "<br>" +
                "<b>Personal de Apoyo / Asistente:</b> " + c.getPersonalApoyo());
        pProc.setAlignmentX(Component.LEFT_ALIGNMENT);
        pCentro.add(pProc);
        pCentro.add(Box.createVerticalStrut(8));

        // Fármacos Intraquirúrgicos
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel pMed = crearCajaSeccion("MEDICAMENTOS ADMINISTRADOS E INSUMOS",
                c.getMedicamentosInsumos() != null && !c.getMedicamentosInsumos().isEmpty() ? c.getMedicamentosInsumos() : "No especificados");
        pMed.setAlignmentX(Component.LEFT_ALIGNMENT);
        pCentro.add(pMed);
        pCentro.add(Box.createVerticalStrut(8));

        // Hallazgos y Cuidados Postoperatorios
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel pObs = crearCajaSeccion("HALLAZGOS Y PLAN DE CUIDADO POSTOPERATORIO",
                "<b>Hallazgos:</b> " + c.getObservacionesProtocolo() + "<br><br>" +
                "<b>Cuidados en Casa para el Tutor:</b> " + c.getIndicacionesPostop());
        pObs.setAlignmentX(Component.LEFT_ALIGNMENT);
        pCentro.add(pObs);
        pCentro.add(Box.createVerticalGlue());

        JScrollPane spCentro = new JScrollPane(pCentro);
        spCentro.setBorder(null);
        spCentro.setOpaque(false);
        spCentro.getViewport().setOpaque(false);
        spCentro.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        spCentro.getVerticalScrollBar().setUnitIncrement(12);
        card.add(spCentro, BorderLayout.CENTER);

        // 3. Footer: Dock de Acciones Rápidas (1-clic)
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel pBotones = new JPanel(new GridLayout(2, 2, 8, 8));
        pBotones.setOpaque(false);

        JButton btnActa = crearBotonAccion("Emitir Acta Quirúrgica", true);
        btnActa.setIcon(Iconos.crearIconoFactura(13, Color.WHITE));
        btnActa.setIconTextGap(6);
        btnActa.addActionListener(e -> emitirActaQuirurgica(c));

        JButton btnFase = crearBotonAccion("Cambiar Fase / Estado", false);
        btnFase.setIcon(Iconos.crearIconoCheck(13, new Color(22, 163, 74)));
        btnFase.setIconTextGap(6);
        btnFase.addActionListener(e -> cambiarFaseCirugia(c));

        JButton btnEditar = crearBotonAccion("Editar Protocolo", false);
        btnEditar.setIcon(Iconos.crearIconoEditar(13, COLOR_AZUL_PRIMARIO));
        btnEditar.setIconTextGap(6);
        btnEditar.addActionListener(e -> abrirModalEditarCirugia(c));

        JButton btnNuevaMascota = crearBotonAccion("+ Nueva para Mascota", false);
        btnNuevaMascota.setIcon(Iconos.crearIconoBisturi(13, new Color(100, 116, 139)));
        btnNuevaMascota.setIconTextGap(6);
        btnNuevaMascota.addActionListener(e -> {
            Optional<Mascota> optM = repo.todasLasMascotas().stream()
                    .filter(m -> m.getCodigo().equalsIgnoreCase(c.getCodigoMascota()))
                    .findFirst();
            abrirModalNuevaCirugia(optM.orElse(null));
        });

        pBotones.add(btnActa);
        pBotones.add(btnFase);
        pBotones.add(btnEditar);
        pBotones.add(btnNuevaMascota);

        card.add(pBotones, BorderLayout.SOUTH);
        return card;
    }

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private JPanel crearCajaSeccion(String titulo, String contenidoHtml) {
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel p = new JPanel(new BorderLayout(0, 4)) {
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
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(8, 10, 8, 10));

        JLabel lblTit = new JLabel(titulo);
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblTit.setForeground(COLOR_TEXTO_MUTED);

        JLabel lblCont = new JLabel("<html><body style='width: 310px;'>" + contenidoHtml + "</body></html>");
        lblCont.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblCont.setForeground(COLOR_TEXTO_TITULO);

        p.add(lblTit, BorderLayout.NORTH);
        p.add(lblCont, BorderLayout.CENTER);
        return p;
    }

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private JPanel crearWidgetCirugia(String titulo, String valor, String status, Color colorStatus) {
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel w = new JPanel(new BorderLayout(2, 2)) {
            private static final long serialVersionUID = 1L;
            @Override
            /**
             * Renderizado gráfico personalizado en Java2D.
             * Activa Antialiasing para bordes suaves y realiza el cálculo geométrico preciso.
             */
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(250, 250, 250));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g2.setColor(COLOR_BORDE);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 6, 6);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        w.setOpaque(false);
        w.setBorder(new EmptyBorder(5, 8, 5, 8));

        JLabel lblT = new JLabel(titulo);
        lblT.setFont(new Font("Segoe UI", Font.BOLD, 9));
        lblT.setForeground(COLOR_TEXTO_MUTED);

        JLabel lblV = new JLabel(valor);
        lblV.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblV.setForeground(COLOR_TEXTO_TITULO);

        JLabel lblS = new JLabel(status);
        lblS.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblS.setForeground(colorStatus);

        w.add(lblT, BorderLayout.NORTH);
        w.add(lblV, BorderLayout.CENTER);
        w.add(lblS, BorderLayout.SOUTH);

        return w;
    }

    // ==========================================
    // OPERACIONES Y NEGOCIO
    // ==========================================
    public void cargarMascota(Mascota m) {
        if (m == null) return;
        recargarDatos();

        for (RegistroCirugia c : listaActual) {
            if (m.getCodigo().equalsIgnoreCase(c.getCodigoMascota())) {
                seleccionarCirugia(c);
                return;
            }
        }

        abrirModalNuevaCirugia(m);
    }

    private void abrirModalNuevaCirugia(Mascota mInicial) {
        Window parent = SwingUtilities.getWindowAncestor(this);
        FormularioCirugiaDialog dlg = new FormularioCirugiaDialog(parent, null, mInicial, guardada -> {
            recargarDatos();
            seleccionarCirugia(guardada);
            JOptionPane.showMessageDialog(this,
                    "Cirugía " + guardada.getIdCirugia() + " programada con éxito para " + guardada.getNombreMascota() + ".\n" +
                    "Procedimiento: " + guardada.getProcedimiento(),
                    "Cirugía Registrada", JOptionPane.INFORMATION_MESSAGE);
        });
        dlg.setVisible(true);
    }

    private void abrirModalEditarCirugia(RegistroCirugia c) {
        Window parent = SwingUtilities.getWindowAncestor(this);
        FormularioCirugiaDialog dlg = new FormularioCirugiaDialog(parent, c, null, guardada -> {
            recargarDatos();
            seleccionarCirugia(guardada);
            JOptionPane.showMessageDialog(this,
                    "Protocolo de cirugía " + guardada.getIdCirugia() + " actualizado correctamente.",
                    "Intervención Actualizada", JOptionPane.INFORMATION_MESSAGE);
        });
        dlg.setVisible(true);
    }

    private void cambiarFaseCirugia(RegistroCirugia c) {
        String[] opciones = {"Programada", "En Proceso", "En Recuperación", "Alta Quirúrgica"};
        int sel = JOptionPane.showOptionDialog(
                this,
                "Seleccione la fase quirúrgica actual para " + c.getNombreMascota() + ":",
                "Fase Quirúrgica - " + c.getIdCirugia(),
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                Iconos.crearIconoBisturi(24, COLOR_AZUL_PRIMARIO),
                opciones,
                c.getEstado()
        );

        if (sel >= 0 && sel < opciones.length) {
            String nuevoEstado = opciones[sel];
            c.setEstado(nuevoEstado);
            repo.actualizarEstadoCirugia(c.getIdCirugia(), nuevoEstado);
            recargarDatos();
            seleccionarCirugia(c);
            JOptionPane.showMessageDialog(this,
                    "Fase quirúrgica actualizada a: " + nuevoEstado,
                    "Estado Quirúrgico Modificado", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void emitirActaQuirurgica(RegistroCirugia c) {
        String[][] datos = new String[][]{
                {"1", "Procedimiento Quirúrgico", c.getProcedimiento(), c.getQuirofano()},
                {"2", "Cirujano y Equipo Quirúrgico", c.getCirujanoPrincipal(), "Asistente: " + c.getPersonalApoyo()},
                {"3", "Protocolo Anestésico y Tiempos", c.getTipoAnestesia(), "Inicio: " + c.getHoraInicioFormateada() + " | Fin: " + c.getHoraFinFormateada()},
                {"4", "Medicamentos Administrados", c.getMedicamentosInsumos(), "Terapia analgésica y profilaxis antibiótica"},
                {"5", "Hallazgos y Observaciones", c.getObservacionesProtocolo(), "Técnica sin complicaciones"},
                {"6", "Control Postoperatorio y Retiro Puntos", c.getFechaRetiroPuntosFormateada(), "Escala de Dolor: " + c.getEscalaDolorPostop() + "/10 · " + c.getEstadoGeneralAlta()}
        };

        Ui.mostrarVisorReporte(
                (java.awt.Frame) SwingUtilities.getWindowAncestor(this),
                "ACTA QUIRÚRGICA Y PROTOCOLO OPERATORIO OFICIAL",
                "Paciente: " + c.getNombreMascota() + " (" + c.getEspecieRaza() + ") | Tutor: " + c.getNombreTutor() + " | Fecha: " + c.getFechaFormateada(),
                "Cirujano Tratante: " + c.getCirujanoPrincipal() + " | Sala: " + c.getQuirofano() + " | Fase: " + c.getEstado(),
                new String[]{"Ítem", "Fase / Concepto", "Detalle Quirúrgico", "Observaciones"},
                datos,
                "CENTRO QUIRÚRGICO VETERINARIO · HAPPY PETS HOSPITAL 24H",
                "ActaQuirurgica_" + c.getNombreMascota()
        );
    }

    public void recargarDatos() {
        filtrarDatos();
    }

    /**
     * Aplica criterios de filtrado dinámico en memoria sobre los registros.
     * Evalúa texto de búsqueda (búsqueda predictiva) y filtros por categoría o estado.
     */
    private void filtrarDatos() {
        List<RegistroCirugia> todas = repo.getCirugias();
        String fQ = cbFiltroQuirofano != null ? (String) cbFiltroQuirofano.getSelectedItem() : "Todos";
        String fEst = cbFiltroEstado != null ? (String) cbFiltroEstado.getSelectedItem() : "Todos";
        String q = (txtBuscar != null && txtBuscar.getText() != null) ? txtBuscar.getText().trim().toLowerCase() : "";

        listaActual = todas.stream().filter(c -> {
            if (fQ != null && !fQ.startsWith("Todos") && !c.getQuirofano().contains(fQ)) return false;
            if (fEst != null && !fEst.startsWith("Todos") && !c.getEstado().equalsIgnoreCase(fEst)) return false;
            if (!q.isEmpty()) {
                boolean match = c.getIdCirugia().toLowerCase().contains(q) ||
                                c.getNombreMascota().toLowerCase().contains(q) ||
                                c.getNombreTutor().toLowerCase().contains(q) ||
                                c.getProcedimiento().toLowerCase().contains(q) ||
                                c.getCirujanoPrincipal().toLowerCase().contains(q);
                if (!match) return false;
            }
            return true;
        }).toList();

        modeloCirugias.setRowCount(0);
        for (RegistroCirugia c : listaActual) {
            modeloCirugias.addRow(new Object[]{
                    c,
                    c,
                    c,
                    c,
                    c.getCirujanoPrincipal() + " (" + (c.getQuirofano().contains("1") ? "Q-1" : "Q-2") + ")",
                    c.getEstado()
            });
        }

        lblContadorCirugias.setText(listaActual.size() + " cirugías registradas en quirófano");

        // KPIs
        long hoy = todas.stream().filter(c -> c.getFecha().equals(LocalDate.now())).count();
        long proc = todas.stream().filter(c -> "En Proceso".equalsIgnoreCase(c.getEstado())).count();
        long rec = todas.stream().filter(c -> "En Recuperación".equalsIgnoreCase(c.getEstado()) || "Alta Quirúrgica".equalsIgnoreCase(c.getEstado())).count();

        lblKpiCirugiasHoy.setText(String.valueOf(hoy > 0 ? hoy : todas.size()));
        lblKpiEnProcedimiento.setText(String.valueOf(proc));
        lblKpiEnRecuperacion.setText(String.valueOf(rec));

        if (cirugiaSeleccionada != null) {
            seleccionarCirugia(cirugiaSeleccionada);
        } else if (!listaActual.isEmpty()) {
            seleccionarFila(0);
        } else {
            cirugiaSeleccionada = null;
            actualizarPanelDetalle();
        }
    }

    private void seleccionarCirugia(RegistroCirugia c) {
        if (c == null) return;
        for (int i = 0; i < listaActual.size(); i++) {
            if (listaActual.get(i).getIdCirugia().equalsIgnoreCase(c.getIdCirugia())) {
                seleccionarFila(i);
                return;
            }
        }
        actualizarPanelDetalle();
    }

    private void seleccionarFila(int fila) {
        if (fila >= 0 && fila < tablaCirugias.getRowCount()) {
            tablaCirugias.setRowSelectionInterval(fila, fila);
            tablaCirugias.scrollRectToVisible(tablaCirugias.getCellRect(fila, 0, true));
            cirugiaSeleccionada = listaActual.get(fila);
        } else {
            cirugiaSeleccionada = null;
        }
        actualizarPanelDetalle();
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
                    g2.setColor(getModel().isRollover() ? Ui.TURQUESA_OSCURO : COLOR_AZUL_PRIMARIO);
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
                return new Dimension(d.width + 12, 30);
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

    // ==========================================
    // INNER CLASSES Y RENDERIZADORES
    // ==========================================
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private static class ScrollablePanel extends JPanel implements Scrollable {
        private static final long serialVersionUID = 1L;
        public ScrollablePanel() { super(); }
        public ScrollablePanel(java.awt.LayoutManager layout) { super(layout); }
        @Override public Dimension getPreferredScrollableViewportSize() { return getPreferredSize(); }
        @Override public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) { return 16; }
        @Override public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) { return 48; }
        @Override public boolean getScrollableTracksViewportWidth() { return true; }
        @Override public boolean getScrollableTracksViewportHeight() { return false; }
    }

    private static class CodigoHoraCirugiaRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
            JPanel p = new JPanel(new BorderLayout(0, 2));
            p.setOpaque(true);
            p.setBackground(isSelected ? table.getSelectionBackground() : (row % 2 == 0 ? Color.WHITE : new Color(249, 250, 251)));
            p.setBorder(new EmptyBorder(6, 8, 6, 6));

            if (value instanceof RegistroCirugia c) {
                JLabel lblCod = new JLabel(c.getIdCirugia());
                lblCod.setFont(new Font("Segoe UI", Font.BOLD, 12));
                lblCod.setForeground(isSelected ? table.getSelectionForeground() : COLOR_AZUL_PRIMARIO);

                JLabel lblHor = new JLabel(c.getHoraProgramadaFormateada() + " hrs · " + c.getFechaFormateada());
                lblHor.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                lblHor.setForeground(isSelected ? table.getSelectionForeground() : COLOR_TEXTO_MUTED);

                p.add(lblCod, BorderLayout.NORTH);
                p.add(lblHor, BorderLayout.CENTER);
            }
            return p;
        }
    }

    private static class PacienteCirugiaRenderer extends DefaultTableCellRenderer {
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
            p.setBackground(isSelected ? table.getSelectionBackground() : (row % 2 == 0 ? Color.WHITE : new Color(249, 250, 251)));
            p.setBorder(new EmptyBorder(6, 6, 6, 6));

            if (value instanceof RegistroCirugia c) {
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
                JPanel pAvWrap = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 2));
                pAvWrap.setOpaque(false);
                JLabel lblAv = new JLabel() {
                    private static final long serialVersionUID = 1L;
                    @Override
            /**
             * Renderizado gráfico personalizado en Java2D.
             * Activa Antialiasing para bordes suaves y realiza el cálculo geométrico preciso.
             */
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        int w = getWidth();
                        int h = getHeight();
                        int d = Math.min(w, h) - 2;
                        int x = (w - d) / 2;
                        int y = (h - d) / 2;

                        g2.setColor(new Color(254, 226, 226));
                        g2.fillOval(x, y, d, d);
                        g2.setColor(new Color(220, 38, 38));
                        g2.drawOval(x, y, d, d);

                        String ini = c.getNombreMascota().substring(0, 1).toUpperCase();
                        g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
                        g2.setColor(new Color(185, 28, 28));
                        java.awt.FontMetrics fm = g2.getFontMetrics();
                        int tx = (w - fm.stringWidth(ini)) / 2;
                        int ty = (h + fm.getAscent() - fm.getDescent()) / 2;
                        g2.drawString(ini, tx, ty);
                        g2.dispose();
                    }
                };
                lblAv.setPreferredSize(new Dimension(30, 30));
                pAvWrap.add(lblAv);

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
                JPanel pDat = new JPanel(new BorderLayout(0, 2));
                pDat.setOpaque(false);
                JLabel lblNom = new JLabel(c.getNombreMascota());
                lblNom.setFont(new Font("Segoe UI", Font.BOLD, 12));
                lblNom.setForeground(isSelected ? table.getSelectionForeground() : COLOR_TEXTO_TITULO);

                JLabel lblEsp = new JLabel(c.getEspecieRaza());
                lblEsp.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                lblEsp.setForeground(isSelected ? table.getSelectionForeground() : COLOR_TEXTO_MUTED);

                pDat.add(lblNom, BorderLayout.NORTH);
                pDat.add(lblEsp, BorderLayout.CENTER);

                p.add(pAvWrap, BorderLayout.WEST);
                p.add(pDat, BorderLayout.CENTER);
            }
            return p;
        }
    }

    private static class TutorCirugiaRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
            JPanel p = new JPanel(new BorderLayout(0, 2));
            p.setOpaque(true);
            p.setBackground(isSelected ? table.getSelectionBackground() : (row % 2 == 0 ? Color.WHITE : new Color(249, 250, 251)));
            p.setBorder(new EmptyBorder(6, 6, 6, 6));

            if (value instanceof RegistroCirugia c) {
                JLabel lblTutor = new JLabel(c.getNombreTutor());
                lblTutor.setFont(new Font("Segoe UI", Font.BOLD, 11));
                lblTutor.setForeground(isSelected ? table.getSelectionForeground() : COLOR_TEXTO_TITULO);

                JLabel lblTel = new JLabel(c.getTelefonoTutor());
                lblTel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                lblTel.setForeground(isSelected ? table.getSelectionForeground() : COLOR_TEXTO_MUTED);

                p.add(lblTutor, BorderLayout.NORTH);
                p.add(lblTel, BorderLayout.CENTER);
            }
            return p;
        }
    }

    private static class ProcedimientoCirugiaRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
            JPanel p = new JPanel(new BorderLayout(0, 2));
            p.setOpaque(true);
            p.setBackground(isSelected ? table.getSelectionBackground() : (row % 2 == 0 ? Color.WHITE : new Color(249, 250, 251)));
            p.setBorder(new EmptyBorder(6, 6, 6, 6));

            if (value instanceof RegistroCirugia c) {
                JLabel lblProc = new JLabel(c.getProcedimiento());
                lblProc.setFont(new Font("Segoe UI", Font.BOLD, 11));
                lblProc.setForeground(isSelected ? table.getSelectionForeground() : new Color(15, 118, 110));

                JLabel lblAn = new JLabel(c.getTipoAnestesia());
                lblAn.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                lblAn.setForeground(isSelected ? table.getSelectionForeground() : COLOR_TEXTO_MUTED);

                p.add(lblProc, BorderLayout.NORTH);
                p.add(lblAn, BorderLayout.CENTER);
            }
            return p;
        }
    }

    private static class BadgeEstadoCirugiaRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
            JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 8));
            p.setOpaque(true);
            p.setBackground(isSelected ? table.getSelectionBackground() : (row % 2 == 0 ? Color.WHITE : new Color(249, 250, 251)));

            String st = String.valueOf(value);
            Color bg;
            Color fg;
            if ("Alta Quirúrgica".equalsIgnoreCase(st)) {
                bg = new Color(220, 252, 231);
                fg = new Color(22, 163, 74);
            } else if ("En Proceso".equalsIgnoreCase(st)) {
                bg = new Color(254, 226, 226);
                fg = new Color(220, 38, 38);
            } else if ("En Recuperación".equalsIgnoreCase(st)) {
                bg = new Color(254, 243, 199);
                fg = new Color(217, 119, 6);
            } else {
                bg = new Color(224, 242, 254);
                fg = new Color(2, 132, 199);
            }

            JLabel badge = new JLabel(st, SwingConstants.CENTER) {
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
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                    g2.setColor(fg);
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                    super.paintComponent(g2);
                    g2.dispose();
                }
            };
            badge.setOpaque(false);
            badge.setFont(new Font("Segoe UI", Font.BOLD, 10));
            badge.setForeground(fg);
            badge.setBorder(new EmptyBorder(3, 8, 3, 8));

            p.add(badge);
            return p;
        }
    }
}
