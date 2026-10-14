package happypets.modulos.modulo4;

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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
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
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import happypets.data.RepositorioVeterinaria;
import happypets.model.ReservaHospedaje;
import happypets.ui.Iconos;
import happypets.ui.Ui;

/**
 * Submódulo 4.3: Hotel y Guardería Canina y Felina.
 * Arquitectura Master-Detail (58% / 42%) moderna, sin scrollbars horizontales ni espacio en blanco.
 * Incluye Live Hub de suites climatizadas, gestión de nutrición, contratos de hospedaje y control de check-out.
 */
public class VistaHotelGuarderiaPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private final RepositorioVeterinaria repo = RepositorioVeterinaria.getInstancia();

    // KPIs
    private JLabel lblKpiOcupadas;
    private JLabel lblKpiCheckinHoy;
    private JLabel lblKpiCheckoutHoy;
    private JLabel lblKpiFacturacionNoches;

    // Master Table
    private JTable tablaHospedaje;
    private DefaultTableModel modeloHospedaje;
    private JTextField txtBuscar;
    private String filtroEstado = "TODOS";
    private List<ReservaHospedaje> listaFiltrada = new ArrayList<>();
    private ReservaHospedaje reservaSeleccionada = null;

    // Live Detail Hub
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
    private JLabel lblHubNombreMascota;
    private JLabel lblHubSubtituloMascota;
    private JLabel lblHubBadgeSuite;
    private JLabel lblHubNoches;
    private JLabel lblHubTarifaNoche;
    private JLabel lblHubTotal;
    private JLabel lblHubBanoSalida;
    private JLabel lblHubTutor;
    private JLabel lblHubEmergencia;
    private JLabel lblHubFechasEstadia;
    private JLabel lblHubDieta;
    private JLabel lblHubPaseos;
    private JTextArea txtHubMedicacion;

    /**
     * Constructor de VistaHotelGuarderiaPanel.
     * Inicializa los componentes gráficos, configura layouts, asigna estilos
     * institucionales y carga los datos desde el repositorio central.
     */
    public VistaHotelGuarderiaPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(248, 250, 252));

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel panelSuperior = new JPanel();
        panelSuperior.setOpaque(false);
        panelSuperior.setLayout(new BoxLayout(panelSuperior, BoxLayout.Y_AXIS));
        panelSuperior.setBorder(new EmptyBorder(12, 16, 6, 16));

        panelSuperior.add(crearCabeceraVista());
        panelSuperior.add(Box.createVerticalStrut(10));
        panelSuperior.add(crearFilaKpis());
        panelSuperior.add(Box.createVerticalStrut(8));

        add(panelSuperior, BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, crearPanelMaster(), crearPanelDetail());
        splitPane.setResizeWeight(1.0);
        splitPane.setDividerSize(6);
        splitPane.setBorder(new EmptyBorder(0, 16, 12, 16));
        splitPane.setOpaque(false);

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

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel izq = new JPanel();
        izq.setOpaque(false);
        izq.setLayout(new BoxLayout(izq, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("Hotel & Guardería Canina / Felina");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 17));
        titulo.setForeground(Ui.TEXTO_TITULO);

        JLabel subtitulo = new JLabel("Módulo 4.3 · Suites climatizadas, estancias prolongadas, nutrición personalizada, paseos y check-out");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitulo.setForeground(Ui.TEXTO_MUTED);

        izq.add(titulo);
        izq.add(Box.createVerticalStrut(2));
        izq.add(subtitulo);
        cab.add(izq, BorderLayout.CENTER);

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        der.setOpaque(false);

        JLabel badgeCapacidad = new JLabel(" Capacidad: 8 Suites Climatizadas ", SwingConstants.CENTER);
        badgeCapacidad.setFont(new Font("Segoe UI", Font.BOLD, 11));
        badgeCapacidad.setForeground(Ui.TURQUESA_PROFUNDO);
        badgeCapacidad.setOpaque(true);
        badgeCapacidad.setBackground(Ui.TURQUESA_SUAVE);
        badgeCapacidad.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Ui.TURQUESA_MEDIO, 1, true),
                new EmptyBorder(4, 10, 4, 10)
        ));
        der.add(badgeCapacidad);

        JButton btnNuevo = crearBotonAccion("Registrar Check-In", Iconos.crearIconoCasaMascota(14, Color.WHITE), true);
        btnNuevo.addActionListener(e -> abrirModalNuevoCheckin());
        btnNuevo.setPreferredSize(new Dimension(175, 32));
        der.add(btnNuevo);

        JButton btnRefrescar = crearBotonAccion("Actualizar", false);
        btnRefrescar.addActionListener(e -> recargarDatos());
        btnRefrescar.setPreferredSize(new Dimension(100, 32));
        der.add(btnRefrescar);

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
        JPanel fila = new JPanel(new GridLayout(1, 4, 12, 0));
        fila.setOpaque(false);

        lblKpiOcupadas = new JLabel("0", SwingConstants.LEFT);
        lblKpiCheckinHoy = new JLabel("0", SwingConstants.LEFT);
        lblKpiCheckoutHoy = new JLabel("0", SwingConstants.LEFT);
        lblKpiFacturacionNoches = new JLabel("S/. 0", SwingConstants.LEFT);

        fila.add(crearCardKpi(lblKpiOcupadas, "SUITES OCUPADAS HOY", Ui.TURQUESA_SUAVE, Ui.TURQUESA, Iconos.crearIconoCasaMascota(20, Ui.TURQUESA)));
        fila.add(crearCardKpi(lblKpiCheckinHoy, "CHECK-INS AGENDADOS", new Color(220, 252, 231), Ui.COLOR_EXITO, Iconos.crearIconoCheck(20, Ui.COLOR_EXITO)));
        fila.add(crearCardKpi(lblKpiCheckoutHoy, "CHECK-OUTS PREVISTOS", new Color(254, 243, 199), new Color(217, 119, 6), Iconos.crearIconoReloj(20, new Color(217, 119, 6))));
        fila.add(crearCardKpi(lblKpiFacturacionNoches, "FACTURACIÓN ESTANCIAS", new Color(243, 232, 255), new Color(147, 51, 234), Iconos.crearIconoFactura(20, new Color(147, 51, 234))));

        return fila;
    }

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private JPanel crearCardKpi(JLabel lblValor, String etiqueta, Color colorFondoIco, Color colorIco, Icon icono) {
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
                g2.setColor(Ui.BORDE_SUAVE);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                super.paintComponent(g);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(8, 12, 8, 12));

        JLabel iconBox = new JLabel(icono, SwingConstants.CENTER) {
            private static final long serialVersionUID = 1L;
            @Override
            /**
             * Renderizado gráfico personalizado en Java2D.
             * Activa Antialiasing para bordes suaves y realiza el cálculo geométrico preciso.
             */
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(colorFondoIco);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                super.paintComponent(g);
                g2.dispose();
            }
        };
        iconBox.setPreferredSize(new Dimension(36, 36));
        card.add(iconBox, BorderLayout.WEST);

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel centro = new JPanel();
        centro.setOpaque(false);
        centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));

        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblValor.setForeground(Ui.TEXTO_TITULO);

        JLabel lblSub = new JLabel(etiqueta);
        lblSub.setFont(new Font("Segoe UI", Font.BOLD, 9));
        lblSub.setForeground(Ui.TEXTO_MUTED);

        centro.add(lblValor);
        centro.add(lblSub);
        card.add(centro, BorderLayout.CENTER);

        return card;
    }

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
        JPanel panel = new JPanel(new BorderLayout(0, 8)) {
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
                g2.setColor(Ui.BORDE_SUAVE);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                super.paintComponent(g);
                g2.dispose();
            }
        };
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(12, 14, 12, 14));

        // Buscador y Filtros Pills
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel topFiltros = new JPanel();
        topFiltros.setOpaque(false);
        topFiltros.setLayout(new BoxLayout(topFiltros, BoxLayout.Y_AXIS));

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel filaBuscar = new JPanel(new BorderLayout(8, 0));
        filaBuscar.setOpaque(false);

        JLabel lblLupa = new JLabel("Buscar:");
        lblLupa.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblLupa.setForeground(Ui.TEXTO_MUTED);
        filaBuscar.add(lblLupa, BorderLayout.WEST);

        txtBuscar = new JTextField();
        txtBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtBuscar.setPreferredSize(new Dimension(0, 28));
        txtBuscar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Ui.BORDE_SUAVE, 1, true),
                new EmptyBorder(2, 8, 2, 8)
        ));
        txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filtrarYActualizarTabla(); }
            public void removeUpdate(DocumentEvent e) { filtrarYActualizarTabla(); }
            public void changedUpdate(DocumentEvent e) { filtrarYActualizarTabla(); }
        });
        filaBuscar.add(txtBuscar, BorderLayout.CENTER);
        topFiltros.add(filaBuscar);
        topFiltros.add(Box.createVerticalStrut(8));

        // Pills de Estado
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel filaPills = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        filaPills.setOpaque(false);

        String[] estados = {"TODOS", "En Estadía / Hospedado", "Confirmada", "Finalizada / Check-out"};
        for (String est : estados) {
            JButton btnPill = crearBotonPill(est);
            filaPills.add(btnPill);
        }
        topFiltros.add(filaPills);
        panel.add(topFiltros, BorderLayout.NORTH);

        // Tabla Master
        String[] columnas = {"ID", "Suite", "Huésped", "Tutor", "Ingreso", "Salida", "Noches", "Total S/.", "Estado"};
        modeloHospedaje = new DefaultTableModel(columnas, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        tablaHospedaje = new JTable(modeloHospedaje);
        tablaHospedaje.setRowHeight(48);
        tablaHospedaje.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaHospedaje.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaHospedaje.setShowVerticalLines(false);
        tablaHospedaje.setGridColor(new Color(241, 245, 249));
        tablaHospedaje.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        tablaHospedaje.getTableHeader().setBackground(new Color(248, 250, 252));
        tablaHospedaje.getTableHeader().setForeground(Ui.TEXTO_MUTED);

        tablaHospedaje.getColumnModel().getColumn(0).setPreferredWidth(65);
        tablaHospedaje.getColumnModel().getColumn(1).setPreferredWidth(125);
        tablaHospedaje.getColumnModel().getColumn(2).setPreferredWidth(150);
        tablaHospedaje.getColumnModel().getColumn(3).setPreferredWidth(95);
        tablaHospedaje.getColumnModel().getColumn(4).setPreferredWidth(70);
        tablaHospedaje.getColumnModel().getColumn(5).setPreferredWidth(70);
        tablaHospedaje.getColumnModel().getColumn(6).setPreferredWidth(45);
        tablaHospedaje.getColumnModel().getColumn(7).setPreferredWidth(70);
        tablaHospedaje.getColumnModel().getColumn(8).setPreferredWidth(115);

        tablaHospedaje.getColumnModel().getColumn(2).setCellRenderer(new PacienteMonogramaRenderer());
        tablaHospedaje.getColumnModel().getColumn(8).setCellRenderer(new BadgeEstadoHospedajeRenderer());

        tablaHospedaje.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int r = tablaHospedaje.getSelectedRow();
                if (r >= 0 && r < listaFiltrada.size()) {
                    mostrarDetalleEnHub(listaFiltrada.get(r));
                } else {
                    mostrarDetalleEnHub(null);
                }
            }
        });

        JScrollPane sp = new JScrollPane(tablaHospedaje);
        sp.setBorder(BorderFactory.createLineBorder(Ui.BORDE_SUAVE, 1));
        sp.getViewport().setBackground(Color.WHITE);
        panel.add(sp, BorderLayout.CENTER);

        return panel;
    }

    private JButton crearBotonPill(String estado) {
        JButton btn = new JButton(estado);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 10));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(4, 8, 4, 8));

        actualizarEstiloPill(btn, estado.equalsIgnoreCase(filtroEstado));

        btn.addActionListener(e -> {
            filtroEstado = estado;
            Component[] comps = btn.getParent().getComponents();
            for (Component c : comps) {
                if (c instanceof JButton b) {
                    actualizarEstiloPill(b, b.getText().equalsIgnoreCase(filtroEstado));
                }
            }
            filtrarYActualizarTabla();
        });

        return btn;
    }

    private void actualizarEstiloPill(JButton btn, boolean seleccionado) {
        if (seleccionado) {
            btn.setBackground(Ui.TURQUESA);
            btn.setForeground(Color.WHITE);
            btn.setOpaque(true);
        } else {
            btn.setBackground(new Color(241, 245, 249));
            btn.setForeground(new Color(71, 85, 105));
            btn.setOpaque(true);
        }
    }

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private JPanel crearPanelDetail() {
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
                g2.setColor(Ui.BORDE_SUAVE);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                super.paintComponent(g);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(10, 12, 10, 12));
        card.setPreferredSize(new Dimension(360, 0));
        card.setMinimumSize(new Dimension(320, 0));

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        panelHubVacio = new JPanel(new BorderLayout());
        panelHubVacio.setOpaque(false);
        JLabel lblVacio = new JLabel("<html><center>Seleccione un huésped para ver los detalles de su suite climatizada,<br>su dieta asignada, cronograma de recreación y emitir contrato.</center></html>", SwingConstants.CENTER);
        lblVacio.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblVacio.setForeground(Ui.TEXTO_MUTED);
        panelHubVacio.add(lblVacio, BorderLayout.CENTER);

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        panelHubDetalle = new JPanel(new BorderLayout(0, 8));
        panelHubDetalle.setOpaque(false);

        // 1. Cabecera del Hub
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel topHub = new JPanel(new BorderLayout(8, 0));
        topHub.setOpaque(false);

        lblHubAvatar = new JLabel() {
            private static final long serialVersionUID = 1L;
            @Override
            /**
             * Renderizado gráfico personalizado en Java2D.
             * Activa Antialiasing para bordes suaves y realiza el cálculo geométrico preciso.
             */
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int size = Math.min(getWidth(), getHeight()) - 2;
                g2.setColor(Ui.TURQUESA_SUAVE);
                g2.fillOval(1, 1, size, size);
                g2.setColor(Ui.TURQUESA);
                g2.drawOval(1, 1, size, size);
                super.paintComponent(g);
                g2.dispose();
            }
        };
        lblHubAvatar.setPreferredSize(new Dimension(42, 42));
        lblHubAvatar.setHorizontalAlignment(SwingConstants.CENTER);
        lblHubAvatar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblHubAvatar.setForeground(Ui.TURQUESA);
        topHub.add(lblHubAvatar, BorderLayout.WEST);

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel centroTit = new JPanel();
        centroTit.setOpaque(false);
        centroTit.setLayout(new BoxLayout(centroTit, BoxLayout.Y_AXIS));

        lblHubNombreMascota = new JLabel("Nombre Huésped");
        lblHubNombreMascota.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblHubNombreMascota.setForeground(Ui.TEXTO_TITULO);

        lblHubSubtituloMascota = new JLabel("Especie · Raza · Código");
        lblHubSubtituloMascota.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblHubSubtituloMascota.setForeground(Ui.TEXTO_MUTED);

        centroTit.add(lblHubNombreMascota);
        centroTit.add(Box.createVerticalStrut(2));
        centroTit.add(lblHubSubtituloMascota);
        topHub.add(centroTit, BorderLayout.CENTER);

        lblHubBadgeSuite = new JLabel("SUITE 01", SwingConstants.CENTER);
        lblHubBadgeSuite.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblHubBadgeSuite.setOpaque(true);
        lblHubBadgeSuite.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Ui.BORDE_SUAVE, 1, true),
                new EmptyBorder(4, 10, 4, 10)
        ));
        topHub.add(lblHubBadgeSuite, BorderLayout.EAST);
        panelHubDetalle.add(topHub, BorderLayout.NORTH);

        // 2. Contenido Central Estructurado
        ScrollablePanel hubBody = new ScrollablePanel() {
            private static final long serialVersionUID = 1L;
            @Override
            protected void addImpl(Component comp, Object constraints, int index) {
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
                if (comp instanceof javax.swing.JComponent) {
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
                    ((javax.swing.JComponent) comp).setAlignmentX(Component.LEFT_ALIGNMENT);
                }
                super.addImpl(comp, constraints, index);
            }
        };
        hubBody.setLayout(new BoxLayout(hubBody, BoxLayout.Y_AXIS));
        hubBody.setOpaque(false);

        // 2x2 Telemetría de Estadía
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel gridMetricas = new JPanel(new GridLayout(2, 2, 6, 6));
        gridMetricas.setOpaque(false);
        gridMetricas.setMaximumSize(new Dimension(Integer.MAX_VALUE, 92));
        gridMetricas.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblHubNoches = new JLabel("-", SwingConstants.CENTER);
        lblHubTarifaNoche = new JLabel("-", SwingConstants.CENTER);
        lblHubTotal = new JLabel("-", SwingConstants.CENTER);
        lblHubBanoSalida = new JLabel("-", SwingConstants.CENTER);

        gridMetricas.add(crearCajaMetrica("NOCHES DE ESTADÍA", lblHubNoches, Ui.TURQUESA_OSCURO));
        gridMetricas.add(crearCajaMetrica("TARIFA POR NOCHE", lblHubTarifaNoche, Ui.TEXTO_TITULO));
        gridMetricas.add(crearCajaMetrica("TOTAL LIQUIDACIÓN", lblHubTotal, new Color(22, 163, 74)));
        gridMetricas.add(crearCajaMetrica("BAÑO DE SALIDA", lblHubBanoSalida, new Color(147, 51, 234)));
        hubBody.add(gridMetricas);
        hubBody.add(vGap(8));

        // Tutor & Cronograma
        lblHubTutor = new JLabel("-");
        lblHubTutor.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblHubTutor.setForeground(Ui.TEXTO_TITULO);

        lblHubEmergencia = new JLabel("-");
        lblHubEmergencia.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblHubEmergencia.setForeground(new Color(2, 132, 199));

        lblHubFechasEstadia = new JLabel("-");
        lblHubFechasEstadia.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblHubFechasEstadia.setForeground(Ui.TURQUESA_PROFUNDO);

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel secTutor = crearSeccionCard("TUTOR & CRONOGRAMA DE ESTANCIA");
        secTutor.add(crearItemLinea("Tutor Responsable:", lblHubTutor));
        secTutor.add(crearItemLinea("Teléfono Emergencia 24H:", lblHubEmergencia));
        secTutor.add(crearItemLinea("Fechas Check-In / Out:", lblHubFechasEstadia));
        hubBody.add(secTutor);
        hubBody.add(vGap(6));

        // Nutrición & Dieta
        lblHubDieta = new JLabel("-");
        lblHubDieta.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblHubDieta.setForeground(Ui.TEXTO_TITULO);
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel secDieta = crearSeccionCard("PLAN DE NUTRICIÓN & DIETA SUPERVISADA");
        secDieta.add(lblHubDieta);
        hubBody.add(secDieta);
        hubBody.add(vGap(6));

        // Recreación y Paseos
        lblHubPaseos = new JLabel("-");
        lblHubPaseos.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblHubPaseos.setForeground(Ui.TEXTO_TITULO);
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel secPaseos = crearSeccionCard("CRONOGRAMA DE PASEOS & RECREACIÓN");
        secPaseos.add(lblHubPaseos);
        hubBody.add(secPaseos);
        hubBody.add(vGap(6));

        // Medicación & Cuidados Particulares
        txtHubMedicacion = new JTextArea(2, 20);
        txtHubMedicacion.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        txtHubMedicacion.setLineWrap(true);
        txtHubMedicacion.setWrapStyleWord(true);
        txtHubMedicacion.setEditable(false);
        txtHubMedicacion.setOpaque(false);
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel secMed = crearSeccionCard("MEDICACIÓN ESPECIAL & CUIDADOS");
        secMed.add(txtHubMedicacion);
        hubBody.add(secMed);

        JScrollPane spHub = new JScrollPane(hubBody);
        spHub.setBorder(null);
        spHub.setOpaque(false);
        spHub.getViewport().setOpaque(false);
        spHub.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        spHub.getVerticalScrollBar().setUnitIncrement(16);
        panelHubDetalle.add(spHub, BorderLayout.CENTER);

        // 3. Barra Inferior de Acciones Rápidas
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel barraAcciones = new JPanel(new GridLayout(2, 2, 6, 6));
        barraAcciones.setOpaque(false);
        barraAcciones.setPreferredSize(new Dimension(0, 62));
        barraAcciones.setMaximumSize(new Dimension(Integer.MAX_VALUE, 62));

        JButton btnCheckout = crearBotonAccion("Registrar Check-Out", Iconos.crearIconoCheck(14, Color.WHITE), true);
        btnCheckout.addActionListener(e -> realizarCheckout());

        JButton btnContrato = crearBotonAccion("Contrato Hospedaje", Iconos.crearIconoDocumento(14, new Color(37, 99, 235)), false);
        btnContrato.addActionListener(e -> mostrarContratoHospedaje());

        JButton btnWhatsapp = crearBotonAccion("WhatsApp Reporte", Iconos.crearIconoWhatsApp(14, new Color(37, 211, 102)), false);
        btnWhatsapp.addActionListener(e -> enviarReporteWhatsapp());

        JButton btnEditar = crearBotonAccion("Editar Estadía", Iconos.crearIconoLapiz(14, new Color(100, 116, 139)), false);
        btnEditar.addActionListener(e -> abrirModalEditar());

        barraAcciones.add(btnCheckout);
        barraAcciones.add(btnContrato);
        barraAcciones.add(btnWhatsapp);
        barraAcciones.add(btnEditar);

        panelHubDetalle.add(barraAcciones, BorderLayout.SOUTH);

        panelHubContenido = card;
        panelHubContenido.add(panelHubVacio, BorderLayout.CENTER);
        return card;
    }

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private JPanel crearCajaMetrica(String etiqueta, JLabel lblValor, Color colorValor) {
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel c = new JPanel();
        c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));
        c.setBackground(new Color(248, 250, 252));
        c.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Ui.BORDE_SUAVE, 1, true),
                new EmptyBorder(4, 8, 4, 8)
        ));
        JLabel lblEt = new JLabel(etiqueta);
        lblEt.setFont(new Font("Segoe UI", Font.BOLD, 8));
        lblEt.setForeground(Ui.TEXTO_MUTED);

        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblValor.setForeground(colorValor);

        c.add(lblEt);
        c.add(Box.createVerticalStrut(2));
        c.add(lblValor);
        return c;
    }

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private JPanel crearSeccionCard(String titulo) {
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel sec = new JPanel() {
            private static final long serialVersionUID = 1L;
            @Override
            public Dimension getMaximumSize() {
                return new Dimension(Integer.MAX_VALUE, getPreferredSize().height);
            }
            @Override
            protected void addImpl(Component comp, Object constraints, int index) {
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
                if (comp instanceof javax.swing.JComponent) {
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
                    ((javax.swing.JComponent) comp).setAlignmentX(Component.LEFT_ALIGNMENT);
                }
                super.addImpl(comp, constraints, index);
            }
        };
        sec.setOpaque(false);
        sec.setLayout(new BoxLayout(sec, BoxLayout.Y_AXIS));
        sec.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Ui.BORDE_SUAVE, 1, true),
                new EmptyBorder(6, 10, 6, 10)
        ));
        sec.setBackground(Color.WHITE);
        sec.setAlignmentX(Component.LEFT_ALIGNMENT);

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        header.setOpaque(false);
        header.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel t = new JLabel(titulo);
        t.setFont(new Font("Segoe UI", Font.BOLD, 10));
        t.setForeground(Ui.TURQUESA_OSCURO);
        header.add(t);

        sec.add(header);
        sec.add(Box.createVerticalStrut(4));
        return sec;
    }

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private JPanel crearItemLinea(String label, Component valor) {
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel p = new JPanel(new BorderLayout(6, 0)) {
            private static final long serialVersionUID = 1L;
            @Override
            public Dimension getMaximumSize() {
                return new Dimension(Integer.MAX_VALUE, getPreferredSize().height);
            }
        };
        p.setOpaque(false);
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel l = new JLabel(label);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        l.setForeground(Ui.TEXTO_MUTED);
        p.add(l, BorderLayout.WEST);
        p.add(valor, BorderLayout.CENTER);
        return p;
    }

    private static Component vGap(int h) {
        Component c = Box.createVerticalStrut(h);
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        if (c instanceof javax.swing.JComponent) {
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
            ((javax.swing.JComponent) c).setAlignmentX(Component.LEFT_ALIGNMENT);
        }
        return c;
    }

    public void recargarDatos() {
        filtrarYActualizarTabla();

        List<ReservaHospedaje> todas = repo.getReservasHospedaje();
        long ocupadas = todas.stream().filter(r -> r.getEstado().contains("En Estadía") || r.getEstado().contains("Hospedado")).count();
        long inHoy = todas.stream().filter(r -> r.getFechaCheckIn().equals(LocalDate.now())).count();
        long outHoy = todas.stream().filter(r -> r.getFechaCheckOut().equals(LocalDate.now())).count();
        double facturacion = todas.stream().mapToDouble(ReservaHospedaje::getCostoTotal).sum();

        lblKpiOcupadas.setText(String.valueOf(ocupadas));
        lblKpiCheckinHoy.setText(String.valueOf(inHoy));
        lblKpiCheckoutHoy.setText(String.valueOf(outHoy));
        lblKpiFacturacionNoches.setText("S/. " + String.format("%.0f", facturacion));
    }

    /**
     * Aplica criterios de filtrado dinámico en memoria sobre los registros.
     * Evalúa texto de búsqueda (búsqueda predictiva) y filtros por categoría o estado.
     */
    private void filtrarYActualizarTabla() {
        List<ReservaHospedaje> todas = repo.getReservasHospedaje();
        String txt = txtBuscar != null ? txtBuscar.getText().trim().toLowerCase() : "";

        listaFiltrada.clear();
        for (ReservaHospedaje r : todas) {
            boolean coincideEstado = "TODOS".equalsIgnoreCase(filtroEstado) || r.getEstado().equalsIgnoreCase(filtroEstado);
            if (!coincideEstado) continue;

            if (!txt.isEmpty()) {
                boolean coincideTexto = r.getNombreMascota().toLowerCase().contains(txt) ||
                        r.getNumeroSuite().toLowerCase().contains(txt) ||
                        r.getNombreTutor().toLowerCase().contains(txt) ||
                        r.getIdReserva().toLowerCase().contains(txt);
                if (!coincideTexto) continue;
            }

            listaFiltrada.add(r);
        }

        modeloHospedaje.setRowCount(0);
        for (ReservaHospedaje r : listaFiltrada) {
            modeloHospedaje.addRow(new Object[]{
                    r.getIdReserva(),
                    r.getNumeroSuite(),
                    r.getNombreMascota() + " (" + r.getEspecieRaza() + ")",
                    r.getNombreTutor(),
                    r.getFechaCheckInFormateada(),
                    r.getFechaCheckOutFormateada(),
                    r.getNumeroNoches(),
                    String.format("%.2f", r.getCostoTotal()),
                    r.getEstado()
            });
        }

        if (!listaFiltrada.isEmpty()) {
            tablaHospedaje.setRowSelectionInterval(0, 0);
            mostrarDetalleEnHub(listaFiltrada.get(0));
        } else {
            mostrarDetalleEnHub(null);
        }
    }

    /**
     * Actualiza el panel lateral derecho (Live Detail Hub) con la información del registro seleccionado.
     * Si el argumento es nulo, restablece el panel mostrando el estado de selección vacía.
     */
    private void mostrarDetalleEnHub(ReservaHospedaje r) {
        reservaSeleccionada = r;
        if (panelHubContenido == null) return;

        panelHubContenido.removeAll();
        if (r == null) {
            panelHubContenido.add(panelHubVacio, BorderLayout.CENTER);
            panelHubContenido.revalidate();
            panelHubContenido.repaint();
            return;
        }

        panelHubContenido.add(panelHubDetalle, BorderLayout.CENTER);

        String inicial = !r.getNombreMascota().isEmpty() ? r.getNombreMascota().substring(0, 1).toUpperCase() : "M";
        lblHubAvatar.setText(inicial);
        lblHubNombreMascota.setText(r.getNombreMascota());
        lblHubSubtituloMascota.setText(r.getEspecieRaza() + " · Código: " + r.getCodigoMascota());

        lblHubBadgeSuite.setText(r.getNumeroSuite());
        configurarBadgeSuite(lblHubBadgeSuite, r.getEstado());

        lblHubNoches.setText(r.getNumeroNoches() + " noches");
        lblHubTarifaNoche.setText(String.format("S/. %.2f", r.getCostoNoche()));
        lblHubTotal.setText(String.format("S/. %.2f", r.getCostoTotal()));
        lblHubBanoSalida.setText(r.isBanoSalida() ? "INCLUIDO (SI)" : "NO");

        lblHubTutor.setText(r.getNombreTutor());
        lblHubEmergencia.setText(r.getTelefonoEmergencia());
        lblHubFechasEstadia.setText(r.getFechaCheckInFormateada() + "  hasta  " + r.getFechaCheckOutFormateada());

        lblHubDieta.setText(r.getTipoAlimentacion().isEmpty() ? "Ración estándar balanceada" : r.getTipoAlimentacion());
        lblHubPaseos.setText(r.getPlanPaseos().isEmpty() ? "Recreación supervisada" : r.getPlanPaseos());
        txtHubMedicacion.setText(r.getMedicacionEspecial().isEmpty() ? "Ninguna medicación requerida." : r.getMedicacionEspecial());

        panelHubContenido.revalidate();
        panelHubContenido.repaint();
    }

    private void configurarBadgeSuite(JLabel lbl, String estado) {
        if (estado.contains("En Estadía") || estado.contains("Hospedado")) {
            lbl.setBackground(Ui.TURQUESA_SUAVE);
            lbl.setForeground(Ui.TURQUESA_PROFUNDO);
        } else if (estado.contains("Confirmada")) {
            lbl.setBackground(new Color(254, 243, 199));
            lbl.setForeground(new Color(217, 119, 6));
        } else {
            lbl.setBackground(new Color(220, 252, 231));
            lbl.setForeground(new Color(22, 163, 74));
        }
    }

    private void abrirModalNuevoCheckin() {
        FormularioHotelHospedajeDialog dlg = new FormularioHotelHospedajeDialog(javax.swing.SwingUtilities.getWindowAncestor(this));
        dlg.setVisible(true);
        if (dlg.isGuardado()) {
            recargarDatos();
        }
    }

    private void abrirModalEditar() {
        if (reservaSeleccionada == null) {
            JOptionPane.showMessageDialog(this, "Seleccione una reserva para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String[] opciones = {"Editar Estadía", "Cancelar Reserva (Eliminar)", "Cerrar"};
        int sel = JOptionPane.showOptionDialog(this,
                "¿Qué acción desea realizar sobre la reserva de " + reservaSeleccionada.getNombreMascota() + " (" + reservaSeleccionada.getNumeroSuite() + ")?",
                "Gestión de Reserva de Hotel",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                opciones,
                opciones[0]);

        if (sel == 0) {
            FormularioHotelHospedajeDialog dlg = new FormularioHotelHospedajeDialog(javax.swing.SwingUtilities.getWindowAncestor(this), reservaSeleccionada);
            dlg.setVisible(true);
            if (dlg.isGuardado()) {
                recargarDatos();
            }
        } else if (sel == 1) {
            int conf = JOptionPane.showConfirmDialog(this,
                    "¿Desea cancelar y eliminar la reserva " + reservaSeleccionada.getIdReserva() + "?",
                    "Confirmar Cancelación", JOptionPane.YES_NO_OPTION);
            if (conf == JOptionPane.YES_OPTION) {
                repo.eliminarReservaHospedaje(reservaSeleccionada.getIdReserva());
                recargarDatos();
            }
        }
    }

    private void realizarCheckout() {
        if (reservaSeleccionada == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un huésped para registrar Check-Out.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if ("Finalizada / Check-out".equalsIgnoreCase(reservaSeleccionada.getEstado())) {
            JOptionPane.showMessageDialog(this, "Esta reserva ya fue finalizada anteriormente.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int conf = JOptionPane.showConfirmDialog(this,
                "¿Desea confirmar el Check-out de " + reservaSeleccionada.getNombreMascota() + "?\n\n" +
                "Suite: " + reservaSeleccionada.getNumeroSuite() + "\n" +
                "Tutor: " + reservaSeleccionada.getNombreTutor() + "\n" +
                "Total liquidación: S/. " + String.format("%.2f", reservaSeleccionada.getCostoTotal()) + "\n" +
                (reservaSeleccionada.isBanoSalida() ? "[OK] Baño de salida completado e incluido" : "Sin baño de salida"),
                "Confirmar Check-Out", JOptionPane.YES_NO_OPTION);

        if (conf == JOptionPane.YES_OPTION) {
            repo.actualizarEstadoHospedaje(reservaSeleccionada.getIdReserva(), "Finalizada / Check-out");
            recargarDatos();
            JOptionPane.showMessageDialog(this,
                    "Check-out completado con éxito.\nLa " + reservaSeleccionada.getNumeroSuite() + " ahora está disponible para desinfección y nuevos ingresos.",
                    "Check-Out Exitoso", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void mostrarContratoHospedaje() {
        if (reservaSeleccionada == null) {
            JOptionPane.showMessageDialog(this, "Seleccione una reserva para visualizar el contrato.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String contrato = "CONTRATO DE PRESTACIÓN DE SERVICIOS DE HOSPEDAJE Y CUIDADO INTEGRAL\n" +
                "CLÍNICA VETERINARIA & HOTEL HAPPY PETS S.A.C.\n" +
                "=========================================================================\n\n" +
                "CÓDIGO DE RESERVA: " + reservaSeleccionada.getIdReserva() + "\n" +
                "FECHA DE INGRESO : " + reservaSeleccionada.getFechaCheckInFormateada() + "  |  FECHA DE SALIDA: " + reservaSeleccionada.getFechaCheckOutFormateada() + "\n" +
                "SUITE ASIGNADA   : " + reservaSeleccionada.getNumeroSuite() + "\n" +
                "HUÉSPED          : " + reservaSeleccionada.getNombreMascota() + " (" + reservaSeleccionada.getEspecieRaza() + ")\n" +
                "TUTOR TITULAR    : " + reservaSeleccionada.getNombreTutor() + "  |  TEL. CONTACTO: " + reservaSeleccionada.getTelefonoTutor() + "\n" +
                "TEL. EMERGENCIA  : " + reservaSeleccionada.getTelefonoEmergencia() + "\n\n" +
                "CLÁUSULAS GENERALES:\n" +
                "1. ALIMENTACIÓN Y SALUD: Se suministrará la dieta (" + reservaSeleccionada.getTipoAlimentacion() + ").\n" +
                "2. PASEOS: Se ejecutará el cronograma (" + reservaSeleccionada.getPlanPaseos() + ") en áreas cercadas y seguras.\n" +
                "3. AUTORIZACIÓN MÉDICA: En caso de urgencia, el tutor autoriza la atención médica veterinaria inmediata.\n" +
                "4. BAÑO DE SALIDA: " + (reservaSeleccionada.isBanoSalida() ? "INCLUIDO Y PROGRAMADO PREVIO A ENTREGA." : "NO SOLICITADO POR EL TUTOR.") + "\n" +
                "5. TARIFA: S/. " + String.format("%.2f", reservaSeleccionada.getCostoNoche()) + " por noche. TOTAL ACORDADO: S/. " + String.format("%.2f", reservaSeleccionada.getCostoTotal()) + ".\n\n" +
                "__________________________             __________________________\n" +
                "     Firma del Tutor                        Administración Happy Pets\n" +
                "  DNI: _________________                     Hotel Canino & Felino\n";

        Ui.mostrarVisorReporte(
                javax.swing.SwingUtilities.getWindowAncestor(this),
                "Contrato de Hospedaje - " + reservaSeleccionada.getIdReserva(),
                "CONTRATO DE PRESTACIÓN DE SERVICIOS DE HOSPEDAJE",
                contrato,
                "Contrato_Hospedaje_" + reservaSeleccionada.getIdReserva()
        );
    }

    private void enviarReporteWhatsapp() {
        if (reservaSeleccionada == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un huésped para generar reporte de bienestar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String reporte = "HAPPY PETS HOTEL & RESORT\n" +
                "====================================\n" +
                "¡Hola " + reservaSeleccionada.getNombreTutor() + "! Le enviamos el reporte diario de su consentido:\n\n" +
                "Huésped: " + reservaSeleccionada.getNombreMascota() + " (" + reservaSeleccionada.getEspecieRaza() + ")\n" +
                "Suite: " + reservaSeleccionada.getNumeroSuite() + "\n" +
                "Alimentación: " + reservaSeleccionada.getTipoAlimentacion() + " (Ración completa consumida)\n" +
                "Recreación: " + reservaSeleccionada.getPlanPaseos() + " (Comportamiento sociable y alegre)\n" +
                "Atención Médica: " + reservaSeleccionada.getMedicacionEspecial() + "\n" +
                "Estadía: " + reservaSeleccionada.getFechaCheckInFormateada() + " al " + reservaSeleccionada.getFechaCheckOutFormateada() + "\n\n" +
                "Estado: Se encuentra tranquilo, muy bien adaptado y recibiendo mucho cariño de nuestro equipo.\n" +
                "====================================\n" +
                "Clínica & Hotel Happy Pets · Vigilancia y Cuidados 24/7";

        JTextArea ta = new JTextArea(reporte);
        ta.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        ta.setEditable(false);
        ta.setLineWrap(true);
        ta.setWrapStyleWord(true);
        JScrollPane sp = new JScrollPane(ta);
        sp.setPreferredSize(new Dimension(460, 260));

        JOptionPane.showMessageDialog(this, sp, "Reporte Diario de Estadía para WhatsApp", JOptionPane.INFORMATION_MESSAGE);
    }

    private JButton crearBotonAccion(String texto, boolean primario) {
        return crearBotonAccion(texto, null, primario);
    }

    private JButton crearBotonAccion(String texto, Icon icono, boolean primario) {
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
                    g2.setColor(getModel().isRollover() ? Ui.TURQUESA_OSCURO : Ui.TURQUESA);
                } else {
                    g2.setColor(getModel().isRollover() ? new Color(241, 245, 249) : Color.WHITE);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                if (!esPrimario) {
                    g2.setColor(Ui.BORDE_SUAVE);
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 6, 6);
                }
                super.paintComponent(g);
                g2.dispose();
            }

            @Override
            public Dimension getPreferredSize() {
                Dimension d = super.getPreferredSize();
                return new Dimension(d.width + 12, 30);
            }
        };
        if (icono != null) {
            btn.setIcon(icono);
            btn.setIconTextGap(6);
        }
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

    private static class PacienteMonogramaRenderer extends DefaultTableCellRenderer {
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
            p.setBorder(new EmptyBorder(0, 8, 0, 8));

            String text = value != null ? value.toString() : "";
            String inicial = !text.isEmpty() ? text.substring(0, 1).toUpperCase() : "M";

            JLabel avatar = new JLabel(inicial, SwingConstants.CENTER) {
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
                    super.paintComponent(g2);
                    g2.dispose();
                }
            };
            avatar.setPreferredSize(new Dimension(28, 28));
            avatar.setFont(new Font("Segoe UI", Font.BOLD, 12));
            avatar.setForeground(Ui.TURQUESA);

            JLabel lblNombre = new JLabel(text);
            lblNombre.setFont(new Font("Segoe UI", Font.BOLD, 12));
            lblNombre.setForeground(isSelected ? table.getSelectionForeground() : Ui.TEXTO_TITULO);

            p.add(avatar, BorderLayout.WEST);
            p.add(lblNombre, BorderLayout.CENTER);
            return p;
        }
    }

    private static class BadgeEstadoHospedajeRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            lbl.setHorizontalAlignment(SwingConstants.CENTER);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
            String st = String.valueOf(value);
            if (!isSelected) {
                if (st.contains("En Estadía") || st.contains("Hospedado")) {
                    lbl.setForeground(Ui.TURQUESA_PROFUNDO);
                } else if (st.contains("Confirmada")) {
                    lbl.setForeground(new Color(217, 119, 6));
                } else {
                    lbl.setForeground(new Color(22, 163, 74));
                }
            }
            return lbl;
        }
    }

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private static class ScrollablePanel extends JPanel implements javax.swing.Scrollable {
        private static final long serialVersionUID = 1L;
        @Override public Dimension getPreferredScrollableViewportSize() { return getPreferredSize(); }
        @Override public int getScrollableUnitIncrement(java.awt.Rectangle visibleRect, int orientation, int direction) { return 16; }
        @Override public int getScrollableBlockIncrement(java.awt.Rectangle visibleRect, int orientation, int direction) { return 32; }
        @Override public boolean getScrollableTracksViewportWidth() { return true; }
        @Override public boolean getScrollableTracksViewportHeight() { return false; }
    }
}
