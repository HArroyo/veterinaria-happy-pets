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
import happypets.model.Cliente;
import happypets.model.Mascota;
import happypets.model.ServicioGrooming;
import happypets.ui.Iconos;
import happypets.ui.Ui;

/**
 * Submódulo 4.1: Grooming y Peluquería Canina y Felina.
 * Arquitectura Master-Detail (58% / 42%) moderna, sin scrollbars horizontales ni espacio en blanco.
 * Incluye Live Hub de spa en tiempo real, KPIs dinámicos, avance por etapas y visor de tickets.
 */
public class VistaGroomingPeluqueriaPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private final RepositorioVeterinaria repo = RepositorioVeterinaria.getInstancia();

    // KPIs
    private JLabel lblKpiTurnosHoy;
    private JLabel lblKpiEnProceso;
    private JLabel lblKpiListosEntrega;
    private JLabel lblKpiFacturacion;

    // Master Table
    private JTable tablaGrooming;
    private DefaultTableModel modeloGrooming;
    private JTextField txtBuscar;
    private String filtroEstado = "TODOS";
    private List<ServicioGrooming> listaFiltrada = new ArrayList<>();
    private ServicioGrooming servicioSeleccionado = null;

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
    private JLabel lblHubBadgeEstado;
    private JLabel lblHubHora;
    private JLabel lblHubPrecio;
    private JLabel lblHubEstilista;
    private JLabel lblHubEspecie;
    private JLabel lblHubServicio;
    private JLabel lblHubTutor;
    private JLabel lblHubTelefono;
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private JPanel panelHubChecklistBadges;
    private JLabel lblHubCosmetica;
    private JTextArea txtHubObservaciones;

    /**
     * Constructor de VistaGroomingPeluqueriaPanel.
     * Inicializa los componentes gráficos, configura layouts, asigna estilos
     * institucionales y carga los datos desde el repositorio central.
     */
    public VistaGroomingPeluqueriaPanel() {
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

        JLabel titulo = new JLabel("Grooming y Peluquería Canina / Felina");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 17));
        titulo.setForeground(Ui.TEXTO_TITULO);

        JLabel subtitulo = new JLabel("Módulo 4.1 · Spa estético, baños medicados, corte de raza, checklist dermatológico y entrega");
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

        JLabel badgeSala = new JLabel(" Sala de Estética: 3 Mesas Activas ", SwingConstants.CENTER);
        badgeSala.setFont(new Font("Segoe UI", Font.BOLD, 11));
        badgeSala.setForeground(new Color(190, 24, 93));
        badgeSala.setOpaque(true);
        badgeSala.setBackground(new Color(253, 242, 248));
        badgeSala.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(244, 114, 182), 1, true),
                new EmptyBorder(4, 10, 4, 10)
        ));
        der.add(badgeSala);

        JButton btnNuevo = crearBotonAccion("Nuevo Turno Spa", Iconos.crearIconoTijeras(14, Color.WHITE), true);
        btnNuevo.addActionListener(e -> abrirModalNuevoTurno());
        btnNuevo.setPreferredSize(new Dimension(170, 32));
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

        lblKpiTurnosHoy = new JLabel("0", SwingConstants.LEFT);
        lblKpiEnProceso = new JLabel("0", SwingConstants.LEFT);
        lblKpiListosEntrega = new JLabel("0", SwingConstants.LEFT);
        lblKpiFacturacion = new JLabel("S/. 0", SwingConstants.LEFT);

        fila.add(crearCardKpi(lblKpiTurnosHoy, "TURNOS PROGRAMADOS", Ui.TURQUESA_SUAVE, Ui.TURQUESA, Iconos.crearIconoTijeras(20, Ui.TURQUESA)));
        fila.add(crearCardKpi(lblKpiEnProceso, "EN BAÑO / SECADO", new Color(254, 243, 199), new Color(217, 119, 6), Iconos.crearIconoReloj(20, new Color(217, 119, 6))));
        fila.add(crearCardKpi(lblKpiListosEntrega, "LISTOS PARA RETIRO", new Color(220, 252, 231), new Color(22, 163, 74), Iconos.crearIconoCheck(20, new Color(22, 163, 74))));
        fila.add(crearCardKpi(lblKpiFacturacion, "FACTURACIÓN ESTIMADA", new Color(243, 232, 255), new Color(147, 51, 234), Iconos.crearIconoFactura(20, new Color(147, 51, 234))));

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
                super.paintComponent(g2);
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
                super.paintComponent(g2);
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
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(12, 14, 12, 14));

        // Barra de búsqueda y filtros
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel topFiltros = new JPanel();
        topFiltros.setOpaque(false);
        topFiltros.setLayout(new BoxLayout(topFiltros, BoxLayout.Y_AXIS));

        // Fila 1: Buscador rápido
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

        // Fila 2: Filtros en Pills/Pestañas
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel filaPills = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        filaPills.setOpaque(false);

        String[] estados = {"TODOS", "En Espera", "En Baño", "En Corte y Secado", "Listo para Entrega", "Entregado"};
        for (String est : estados) {
            JButton btnPill = crearBotonPill(est);
            filaPills.add(btnPill);
        }
        topFiltros.add(filaPills);

        panel.add(topFiltros, BorderLayout.NORTH);

        // Tabla Master
        String[] columnas = {"ID", "Turno", "Paciente", "Tutor", "Servicio", "Estilista", "Estado"};
        modeloGrooming = new DefaultTableModel(columnas, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        tablaGrooming = new JTable(modeloGrooming);
        tablaGrooming.setRowHeight(48);
        tablaGrooming.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaGrooming.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaGrooming.setShowVerticalLines(false);
        tablaGrooming.setGridColor(new Color(241, 245, 249));
        tablaGrooming.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        tablaGrooming.getTableHeader().setBackground(new Color(248, 250, 252));
        tablaGrooming.getTableHeader().setForeground(Ui.TEXTO_MUTED);

        tablaGrooming.getColumnModel().getColumn(0).setPreferredWidth(70);
        tablaGrooming.getColumnModel().getColumn(1).setPreferredWidth(55);
        tablaGrooming.getColumnModel().getColumn(2).setPreferredWidth(160);
        tablaGrooming.getColumnModel().getColumn(3).setPreferredWidth(110);
        tablaGrooming.getColumnModel().getColumn(4).setPreferredWidth(150);
        tablaGrooming.getColumnModel().getColumn(5).setPreferredWidth(100);
        tablaGrooming.getColumnModel().getColumn(6).setPreferredWidth(110);

        tablaGrooming.getColumnModel().getColumn(2).setCellRenderer(new PacienteMonogramaRenderer());
        tablaGrooming.getColumnModel().getColumn(6).setCellRenderer(new BadgeEstadoGroomingRenderer());

        tablaGrooming.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int r = tablaGrooming.getSelectedRow();
                if (r >= 0 && r < listaFiltrada.size()) {
                    mostrarDetalleEnHub(listaFiltrada.get(r));
                } else {
                    mostrarDetalleEnHub(null);
                }
            }
        });

        JScrollPane sp = new JScrollPane(tablaGrooming);
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
            // Refrescar estilo de todos los pills
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
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(10, 12, 10, 12));
        card.setPreferredSize(new Dimension(360, 0));
        card.setMinimumSize(new Dimension(320, 0));

        // Panel Vacío (cuando no hay selección)
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        panelHubVacio = new JPanel(new BorderLayout());
        panelHubVacio.setOpaque(false);
        JLabel lblVacio = new JLabel("<html><center>Seleccione un turno de la lista para ver la ficha completa de Spa,<br>el checklist de admisión sanitaria y las opciones de seguimiento.</center></html>", SwingConstants.CENTER);
        lblVacio.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblVacio.setForeground(Ui.TEXTO_MUTED);
        panelHubVacio.add(lblVacio, BorderLayout.CENTER);

        // Panel con Detalle Activo
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
                super.paintComponent(g2);
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

        lblHubNombreMascota = new JLabel("Nombre Paciente");
        lblHubNombreMascota.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblHubNombreMascota.setForeground(Ui.TEXTO_TITULO);

        lblHubSubtituloMascota = new JLabel("Especie · Raza · Código");
        lblHubSubtituloMascota.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblHubSubtituloMascota.setForeground(Ui.TEXTO_MUTED);

        centroTit.add(lblHubNombreMascota);
        centroTit.add(Box.createVerticalStrut(2));
        centroTit.add(lblHubSubtituloMascota);
        topHub.add(centroTit, BorderLayout.CENTER);

        lblHubBadgeEstado = new JLabel("En Espera", SwingConstants.CENTER);
        lblHubBadgeEstado.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblHubBadgeEstado.setOpaque(true);
        lblHubBadgeEstado.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Ui.BORDE_SUAVE, 1, true),
                new EmptyBorder(4, 10, 4, 10)
        ));
        topHub.add(lblHubBadgeEstado, BorderLayout.EAST);
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

        // 2x2 Telemetría / Métricas clave
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel gridMetricas = new JPanel(new GridLayout(2, 2, 6, 6));
        gridMetricas.setOpaque(false);
        gridMetricas.setMaximumSize(new Dimension(Integer.MAX_VALUE, 92));
        gridMetricas.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblHubHora = new JLabel("-", SwingConstants.CENTER);
        lblHubPrecio = new JLabel("-", SwingConstants.CENTER);
        lblHubEstilista = new JLabel("-", SwingConstants.CENTER);
        lblHubEspecie = new JLabel("-", SwingConstants.CENTER);

        gridMetricas.add(crearCajaMetrica("HORA DEL TURNO", lblHubHora, Ui.TURQUESA_OSCURO));
        gridMetricas.add(crearCajaMetrica("TARIFA SERVICIO", lblHubPrecio, new Color(22, 163, 74)));
        gridMetricas.add(crearCajaMetrica("GROOMER ASIGNADO", lblHubEstilista, Ui.TEXTO_TITULO));
        gridMetricas.add(crearCajaMetrica("PACIENTE / ESPECIE", lblHubEspecie, Ui.TEXTO_MUTED));
        hubBody.add(gridMetricas);
        hubBody.add(vGap(8));

        // Servicio & Tutor
        lblHubServicio = new JLabel("-");
        lblHubServicio.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblHubServicio.setForeground(Ui.TURQUESA_PROFUNDO);

        lblHubTutor = new JLabel("-");
        lblHubTutor.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblHubTutor.setForeground(Ui.TEXTO_TITULO);

        lblHubTelefono = new JLabel("-");
        lblHubTelefono.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblHubTelefono.setForeground(new Color(2, 132, 199));

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel secServicio = crearSeccionCard("SERVICIO & DATOS DEL TUTOR");
        secServicio.add(crearItemLinea("Tratamiento de Spa:", lblHubServicio));
        secServicio.add(crearItemLinea("Tutor Titular:", lblHubTutor));
        secServicio.add(crearItemLinea("Teléfono Contacto:", lblHubTelefono));
        hubBody.add(secServicio);
        hubBody.add(vGap(6));

        // Checklist Sanitario
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        panelHubChecklistBadges = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
        panelHubChecklistBadges.setOpaque(false);
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel secChecklist = crearSeccionCard("CHECKLIST SANITARIO DE ADMISIÓN");
        secChecklist.add(panelHubChecklistBadges);
        hubBody.add(secChecklist);
        hubBody.add(vGap(6));

        // Cosmética & Productos
        lblHubCosmetica = new JLabel("-");
        lblHubCosmetica.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblHubCosmetica.setForeground(Ui.TEXTO_TITULO);
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel secCosmetica = crearSeccionCard("COSMÉTICA & CHAMPÚS ESPECIALES");
        secCosmetica.add(lblHubCosmetica);
        hubBody.add(secCosmetica);
        hubBody.add(vGap(6));

        // Observaciones y Conducta
        txtHubObservaciones = new JTextArea(3, 20);
        txtHubObservaciones.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        txtHubObservaciones.setLineWrap(true);
        txtHubObservaciones.setWrapStyleWord(true);
        txtHubObservaciones.setEditable(false);
        txtHubObservaciones.setOpaque(false);
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel secObs = crearSeccionCard("OBSERVACIONES DEL GROOMER / CONDUCTA");
        secObs.add(txtHubObservaciones);
        hubBody.add(secObs);

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

        JButton btnAvanzar = crearBotonAccion("Avanzar Etapa", Iconos.crearIconoCheck(14, Color.WHITE), true);
        btnAvanzar.addActionListener(e -> avanzarEtapaServicio());

        JButton btnNotificar = crearBotonAccion("WhatsApp Listo", Iconos.crearIconoWhatsApp(14, new Color(37, 211, 102)), false);
        btnNotificar.addActionListener(e -> notificarListoWhatsApp());

        JButton btnTicket = crearBotonAccion("Ticket Retiro", Iconos.crearIconoDocumento(14, new Color(37, 99, 235)), false);
        btnTicket.addActionListener(e -> emitirTicketRetiro());

        JButton btnEditar = crearBotonAccion("Editar / Cancelar", Iconos.crearIconoLapiz(14, new Color(100, 116, 139)), false);
        btnEditar.addActionListener(e -> abrirModalEditar());

        barraAcciones.add(btnAvanzar);
        barraAcciones.add(btnNotificar);
        barraAcciones.add(btnTicket);
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

        List<ServicioGrooming> todos = repo.getServiciosGrooming();
        double totalFact = 0;
        long proc = 0;
        long listos = 0;

        for (ServicioGrooming g : todos) {
            totalFact += g.getCosto();
            if ("En Baño".equalsIgnoreCase(g.getEstado()) || "En Corte y Secado".equalsIgnoreCase(g.getEstado())) {
                proc++;
            } else if ("Listo para Entrega".equalsIgnoreCase(g.getEstado())) {
                listos++;
            }
        }

        lblKpiTurnosHoy.setText(String.valueOf(todos.size()));
        lblKpiEnProceso.setText(String.valueOf(proc));
        lblKpiListosEntrega.setText(String.valueOf(listos));
        lblKpiFacturacion.setText("S/. " + String.format("%.0f", totalFact));
    }

    /**
     * Aplica criterios de filtrado dinámico en memoria sobre los registros.
     * Evalúa texto de búsqueda (búsqueda predictiva) y filtros por categoría o estado.
     */
    private void filtrarYActualizarTabla() {
        List<ServicioGrooming> todos = repo.getServiciosGrooming();
        String txt = txtBuscar != null ? txtBuscar.getText().trim().toLowerCase() : "";

        listaFiltrada.clear();
        for (ServicioGrooming g : todos) {
            boolean coincideEstado = "TODOS".equalsIgnoreCase(filtroEstado) || g.getEstado().equalsIgnoreCase(filtroEstado);
            if (!coincideEstado) continue;

            if (!txt.isEmpty()) {
                boolean coincideTexto = g.getNombreMascota().toLowerCase().contains(txt) ||
                        g.getNombreTutor().toLowerCase().contains(txt) ||
                        g.getIdGrooming().toLowerCase().contains(txt) ||
                        g.getTipoServicio().toLowerCase().contains(txt) ||
                        g.getGroomer().toLowerCase().contains(txt);
                if (!coincideTexto) continue;
            }

            listaFiltrada.add(g);
        }

        modeloGrooming.setRowCount(0);
        for (ServicioGrooming g : listaFiltrada) {
            modeloGrooming.addRow(new Object[]{
                    g.getIdGrooming(),
                    g.getHoraTurnoFormateada(),
                    g.getNombreMascota() + " (" + g.getEspecieRaza() + ")",
                    g.getNombreTutor(),
                    g.getTipoServicio(),
                    g.getGroomer(),
                    g.getEstado()
            });
        }

        if (!listaFiltrada.isEmpty()) {
            tablaGrooming.setRowSelectionInterval(0, 0);
            mostrarDetalleEnHub(listaFiltrada.get(0));
        } else {
            mostrarDetalleEnHub(null);
        }
    }

    /**
     * Actualiza el panel lateral derecho (Live Detail Hub) con la información del registro seleccionado.
     * Si el argumento es nulo, restablece el panel mostrando el estado de selección vacía.
     */
    private void mostrarDetalleEnHub(ServicioGrooming g) {
        servicioSeleccionado = g;
        if (panelHubContenido == null) return;

        panelHubContenido.removeAll();
        if (g == null) {
            panelHubContenido.add(panelHubVacio, BorderLayout.CENTER);
            panelHubContenido.revalidate();
            panelHubContenido.repaint();
            return;
        }

        panelHubContenido.add(panelHubDetalle, BorderLayout.CENTER);

        // Cargar datos
        String inicial = !g.getNombreMascota().isEmpty() ? g.getNombreMascota().substring(0, 1).toUpperCase() : "M";
        lblHubAvatar.setText(inicial);
        lblHubNombreMascota.setText(g.getNombreMascota());
        lblHubSubtituloMascota.setText(g.getEspecieRaza() + " · Código: " + g.getCodigoMascota());

        lblHubBadgeEstado.setText(g.getEstado());
        configurarBadgeEstado(lblHubBadgeEstado, g.getEstado());

        lblHubHora.setText(g.getHoraTurnoFormateada() + " hrs");
        lblHubPrecio.setText(String.format("S/. %.2f", g.getCosto()));
        lblHubEstilista.setText(g.getGroomer());
        lblHubEspecie.setText(g.getEspecieRaza());

        lblHubServicio.setText(g.getTipoServicio());
        lblHubTutor.setText(g.getNombreTutor());
        lblHubTelefono.setText(g.getTelefonoTutor().isEmpty() ? "Sin teléfono" : g.getTelefonoTutor());

        // Chips de Checklist
        panelHubChecklistBadges.removeAll();
        agregarChipChecklist(panelHubChecklistBadges, "Ectoparásitos: " + g.getControlEctoparasitos(), g.getControlEctoparasitos().contains("Libre"));
        agregarChipChecklist(panelHubChecklistBadges, "Piel: " + g.getEstadoPiel(), g.getEstadoPiel().contains("sana"));
        agregarChipChecklist(panelHubChecklistBadges, "Oídos: Limpio", true);
        agregarChipChecklist(panelHubChecklistBadges, "Uñas: Limadas", true);
        agregarChipChecklist(panelHubChecklistBadges, "Manto: Desenredado", true);

        lblHubCosmetica.setText(g.getProductosUtilizados().isEmpty() ? "Champú estándar veterinario" : g.getProductosUtilizados());
        txtHubObservaciones.setText(g.getObservacionesConducta().isEmpty() ? "Sin observaciones especiales." : g.getObservacionesConducta());

        panelHubContenido.revalidate();
        panelHubContenido.repaint();
    }

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private void agregarChipChecklist(JPanel p, String texto, boolean positivo) {
        JLabel chip = new JLabel(texto);
        chip.setFont(new Font("Segoe UI", Font.BOLD, 10));
        chip.setOpaque(true);
        if (positivo) {
            chip.setBackground(new Color(220, 252, 231));
            chip.setForeground(new Color(22, 163, 74));
            chip.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(187, 247, 208), 1, true),
                    new EmptyBorder(2, 6, 2, 6)
            ));
        } else {
            chip.setBackground(new Color(254, 243, 199));
            chip.setForeground(new Color(180, 83, 9));
            chip.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(253, 230, 138), 1, true),
                    new EmptyBorder(2, 6, 2, 6)
            ));
        }
        p.add(chip);
    }

    private void configurarBadgeEstado(JLabel lbl, String estado) {
        if ("Listo para Entrega".equalsIgnoreCase(estado)) {
            lbl.setBackground(new Color(220, 252, 231));
            lbl.setForeground(new Color(22, 163, 74));
        } else if ("En Corte y Secado".equalsIgnoreCase(estado)) {
            lbl.setBackground(new Color(243, 232, 255));
            lbl.setForeground(new Color(147, 51, 234));
        } else if ("En Baño".equalsIgnoreCase(estado)) {
            lbl.setBackground(new Color(224, 242, 254));
            lbl.setForeground(new Color(2, 132, 199));
        } else if ("En Espera".equalsIgnoreCase(estado)) {
            lbl.setBackground(new Color(254, 243, 199));
            lbl.setForeground(new Color(217, 119, 6));
        } else {
            lbl.setBackground(new Color(241, 245, 249));
            lbl.setForeground(new Color(100, 116, 139));
        }
    }

    private void abrirModalNuevoTurno() {
        FormularioGroomingDialog dlg = new FormularioGroomingDialog(javax.swing.SwingUtilities.getWindowAncestor(this));
        dlg.setVisible(true);
        if (dlg.isGuardado()) {
            recargarDatos();
        }
    }

    private void abrirModalEditar() {
        if (servicioSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un turno para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String[] opciones = {"Editar Datos", "Cancelar Turno (Eliminar)", "Cerrar"};
        int sel = JOptionPane.showOptionDialog(this,
                "¿Qué acción desea realizar sobre el turno " + servicioSeleccionado.getIdGrooming() + " (" + servicioSeleccionado.getNombreMascota() + ")?",
                "Gestión de Turno de Grooming",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                opciones,
                opciones[0]);

        if (sel == 0) {
            FormularioGroomingDialog dlg = new FormularioGroomingDialog(javax.swing.SwingUtilities.getWindowAncestor(this), servicioSeleccionado);
            dlg.setVisible(true);
            if (dlg.isGuardado()) {
                recargarDatos();
            }
        } else if (sel == 1) {
            int conf = JOptionPane.showConfirmDialog(this,
                    "¿Está seguro de que desea cancelar y eliminar el turno " + servicioSeleccionado.getIdGrooming() + "?",
                    "Confirmar Cancelación", JOptionPane.YES_NO_OPTION);
            if (conf == JOptionPane.YES_OPTION) {
                repo.eliminarServicioGrooming(servicioSeleccionado.getIdGrooming());
                recargarDatos();
            }
        }
    }

    private void avanzarEtapaServicio() {
        if (servicioSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un servicio para avanzar su etapa.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String act = servicioSeleccionado.getEstado();
        String nuevo;

        if ("En Espera".equalsIgnoreCase(act)) nuevo = "En Baño";
        else if ("En Baño".equalsIgnoreCase(act)) nuevo = "En Corte y Secado";
        else if ("En Corte y Secado".equalsIgnoreCase(act)) nuevo = "Listo para Entrega";
        else if ("Listo para Entrega".equalsIgnoreCase(act)) nuevo = "Entregado";
        else nuevo = "Entregado";

        servicioSeleccionado.setEstado(nuevo);
        recargarDatos();

        JOptionPane.showMessageDialog(this,
                "El servicio de " + servicioSeleccionado.getNombreMascota() + " avanzó a: " + nuevo,
                "Estado Actualizado", JOptionPane.INFORMATION_MESSAGE);
    }

    private void notificarListoWhatsApp() {
        if (servicioSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un servicio para notificar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String msg = "¡Hola " + servicioSeleccionado.getNombreTutor() + "! Le informamos que " + servicioSeleccionado.getNombreMascota() +
                " ya finalizó su sesión de " + servicioSeleccionado.getTipoServicio() + " y ha quedado reluciente. Puede pasar a retirarlo por Happy Pets cuando guste.";

        JOptionPane.showMessageDialog(this,
                "Mensaje de WhatsApp preparado para " + servicioSeleccionado.getNombreTutor() + " (" + servicioSeleccionado.getTelefonoTutor() + "):\n\n\"" + msg + "\"",
                "Notificación WhatsApp", JOptionPane.INFORMATION_MESSAGE);
    }

    private void emitirTicketRetiro() {
        if (servicioSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un servicio para emitir ticket.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String[][] datos = new String[][]{
                {"1", "Servicio Principal: " + servicioSeleccionado.getTipoServicio(), "Estilista: " + servicioSeleccionado.getGroomer(), servicioSeleccionado.getHoraTurnoFormateada() + " hrs", String.format("S/ %.2f", servicioSeleccionado.getCosto())},
                {"2", "Control Sanitario y Cosmética", servicioSeleccionado.getControlEctoparasitos() + " | " + servicioSeleccionado.getEstadoPiel(), servicioSeleccionado.getProductosUtilizados(), "-"}
        };

        Ui.mostrarVisorReporte(
                (java.awt.Frame) javax.swing.SwingUtilities.getWindowAncestor(this),
                "COMPROBANTE DE SPA & GROOMING VETERINARIO",
                "Paciente: " + servicioSeleccionado.getNombreMascota() + " (" + servicioSeleccionado.getEspecieRaza() + ") | Tutor: " + servicioSeleccionado.getNombreTutor() + " | Tel: " + servicioSeleccionado.getTelefonoTutor(),
                "Código Turno: " + servicioSeleccionado.getIdGrooming() + " | Estilista: " + servicioSeleccionado.getGroomer() + " | Estado: " + servicioSeleccionado.getEstado(),
                new String[]{"N°", "Servicio / Tratamiento", "Detalle Sanitario", "Horario / Cosmética", "Importe"},
                datos,
                String.format("TOTAL: S/ %.2f · SERVICIO DE BIENESTAR Y BELLEZA", servicioSeleccionado.getCosto()),
                "TicketGrooming_" + servicioSeleccionado.getIdGrooming()
        );
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

    private static class BadgeEstadoGroomingRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            lbl.setHorizontalAlignment(SwingConstants.CENTER);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
            String st = String.valueOf(value);
            if (!isSelected) {
                if ("Listo para Entrega".equalsIgnoreCase(st)) {
                    lbl.setForeground(new Color(22, 163, 74));
                } else if ("En Corte y Secado".equalsIgnoreCase(st)) {
                    lbl.setForeground(new Color(147, 51, 234));
                } else if ("En Baño".equalsIgnoreCase(st)) {
                    lbl.setForeground(new Color(2, 132, 199));
                } else if ("En Espera".equalsIgnoreCase(st)) {
                    lbl.setForeground(new Color(217, 119, 6));
                } else {
                    lbl.setForeground(new Color(100, 116, 139));
                }
            }
            return lbl;
        }
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
