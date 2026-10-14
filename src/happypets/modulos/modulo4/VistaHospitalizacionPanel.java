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
import happypets.model.InternamientoHospitalario;
import happypets.ui.Iconos;
import happypets.ui.Ui;

/**
 * Submódulo 4.2: Hospitalización y Cuidados Críticos.
 * Arquitectura Master-Detail (58% / 42%) moderna, sin scrollbars horizontales ni espacio en blanco.
 * Incluye mapa interactivo de 9 caniles/boxes, monitor UCI, telemetría vital y generación de epicrisis.
 */
public class VistaHospitalizacionPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private final RepositorioVeterinaria repo = RepositorioVeterinaria.getInstancia();

    // KPIs
    private JLabel lblKpiOcupados;
    private JLabel lblKpiCriticos;
    private JLabel lblKpiDisponibles;
    private JLabel lblKpiTasaOcupacion;

    // Mini Mapa Interactivo de Boxes (9 boxes)
    private final JButton[] botonesBoxes = new JButton[9];
    private final String[] nombresBoxes = {
            "Box 01", "Box 02", "Box 03",
            "Box 04", "Box 05", "Box 06",
            "Box 07", "Box 08", "Box 09"
    };

    // Master Table
    private JTable tablaHospitalizados;
    private DefaultTableModel modeloHospitalizados;
    private JTextField txtBuscar;
    private String filtroAlerta = "TODOS";
    private List<InternamientoHospitalario> listaFiltrada = new ArrayList<>();
    private InternamientoHospitalario internamientoSeleccionado = null;

    // Live Detail Hub
    private JPanel panelHubContenido;
    private JPanel panelHubVacio;
    private JPanel panelHubDetalle;
    private JLabel lblHubAvatar;
    private JLabel lblHubNombreMascota;
    private JLabel lblHubSubtituloMascota;
    private JLabel lblHubBadgeBox;
    private JLabel lblHubPeso;
    private JLabel lblHubTemp;
    private JLabel lblHubFC;
    private JLabel lblHubTarifa;
    private JTextArea lblHubDiagnostico;
    private JLabel lblHubVeterinario;
    private JLabel lblHubTutor;
    private JLabel lblHubTelefono;
    private JTextArea txtHubFluidoterapia;
    private JTextArea txtHubMedicacion;
    private JTextArea txtHubEvolucion;

    public VistaHospitalizacionPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(248, 250, 252));

        JPanel panelSuperior = new JPanel();
        panelSuperior.setOpaque(false);
        panelSuperior.setLayout(new BoxLayout(panelSuperior, BoxLayout.Y_AXIS));
        panelSuperior.setBorder(new EmptyBorder(12, 16, 6, 16));

        panelSuperior.add(crearCabeceraVista());
        panelSuperior.add(Box.createVerticalStrut(10));
        panelSuperior.add(crearFilaKpis());
        panelSuperior.add(Box.createVerticalStrut(8));
        panelSuperior.add(crearBarraMiniMapaBoxes());
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

    private JPanel crearCabeceraVista() {
        JPanel cab = new JPanel(new BorderLayout());
        cab.setOpaque(false);

        JPanel izq = new JPanel();
        izq.setOpaque(false);
        izq.setLayout(new BoxLayout(izq, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("Hospitalización y Cuidados Críticos");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 17));
        titulo.setForeground(Ui.TEXTO_TITULO);

        JLabel subtitulo = new JLabel("Módulo 4.2 · Mapa de caniles climatizados, fluidoterapia, monitorización UCI 24/7 y hoja de evolución");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitulo.setForeground(Ui.TEXTO_MUTED);

        izq.add(titulo);
        izq.add(Box.createVerticalStrut(2));
        izq.add(subtitulo);
        cab.add(izq, BorderLayout.CENTER);

        JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        der.setOpaque(false);

        JButton btnNuevo = crearBotonAccion("Registrar Ingreso UCI", Iconos.crearIconoCamaHospital(14, Color.WHITE), true);
        btnNuevo.addActionListener(e -> abrirModalNuevoIngreso());
        btnNuevo.setPreferredSize(new Dimension(190, 32));
        der.add(btnNuevo);

        JButton btnRefrescar = crearBotonAccion("Actualizar", false);
        btnRefrescar.addActionListener(e -> recargarDatos());
        btnRefrescar.setPreferredSize(new Dimension(100, 32));
        der.add(btnRefrescar);

        cab.add(der, BorderLayout.EAST);
        return cab;
    }

    private JPanel crearFilaKpis() {
        JPanel fila = new JPanel(new GridLayout(1, 4, 12, 0));
        fila.setOpaque(false);

        lblKpiOcupados = new JLabel("0 / 9", SwingConstants.LEFT);
        lblKpiCriticos = new JLabel("0", SwingConstants.LEFT);
        lblKpiDisponibles = new JLabel("9", SwingConstants.LEFT);
        lblKpiTasaOcupacion = new JLabel("0%", SwingConstants.LEFT);

        fila.add(crearCardKpi(lblKpiOcupados, "CANÍLES OCUPADOS", Ui.TURQUESA_SUAVE, Ui.TURQUESA, Iconos.crearIconoCamaHospital(20, Ui.TURQUESA)));
        fila.add(crearCardKpi(lblKpiCriticos, "PACIENTES CRÍTICOS (UCI)", new Color(254, 226, 226), Ui.COLOR_PELIGRO, Iconos.crearIconoAlertaTriaje(20, Ui.COLOR_PELIGRO)));
        fila.add(crearCardKpi(lblKpiDisponibles, "CANÍLES DISPONIBLES", new Color(220, 252, 231), Ui.COLOR_EXITO, Iconos.crearIconoCheck(20, Ui.COLOR_EXITO)));
        fila.add(crearCardKpi(lblKpiTasaOcupacion, "OCUPACIÓN CLÍNICA", new Color(243, 232, 255), new Color(147, 51, 234), Iconos.crearIconoReportes(20, new Color(147, 51, 234))));

        return fila;
    }

    private JPanel crearCardKpi(JLabel lblValor, String etiqueta, Color colorFondoIco, Color colorIco, Icon icono) {
        JPanel card = new JPanel(new BorderLayout(10, 0)) {
            private static final long serialVersionUID = 1L;
            @Override
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

    private JPanel crearBarraMiniMapaBoxes() {
        JPanel panel = new JPanel(new BorderLayout(8, 0)) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(Ui.BORDE_SUAVE);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(6, 12, 6, 12));

        JLabel lblTit = new JLabel("Mapa Caniles Clínicos:");
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblTit.setForeground(Ui.TEXTO_TITULO);
        panel.add(lblTit, BorderLayout.WEST);

        JPanel grilla = new JPanel(new GridLayout(1, 9, 6, 0));
        grilla.setOpaque(false);

        for (int i = 0; i < 9; i++) {
            final String boxName = nombresBoxes[i];
            JButton btn = new JButton(boxName) {
                private static final long serialVersionUID = 1L;
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    Color bg = (Color) getClientProperty("bgColor");
                    if (bg == null) bg = new Color(241, 245, 249);
                    g2.setColor(bg);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                    Color border = (Color) getClientProperty("borderColor");
                    if (border != null) {
                        g2.setColor(border);
                        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 6, 6);
                    }
                    super.paintComponent(g);
                    g2.dispose();
                }
            };
            btn.setFont(new Font("Segoe UI", Font.BOLD, 10));
            btn.setFocusPainted(false);
            btn.setContentAreaFilled(false);
            btn.setOpaque(false);
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btn.setPreferredSize(new Dimension(0, 28));
            btn.addActionListener(e -> seleccionarPorBox(boxName));

            botonesBoxes[i] = btn;
            grilla.add(btn);
        }

        panel.add(grilla, BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPanelMaster() {
        JPanel panel = new JPanel(new BorderLayout(0, 8)) {
            private static final long serialVersionUID = 1L;
            @Override
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
        JPanel topFiltros = new JPanel();
        topFiltros.setOpaque(false);
        topFiltros.setLayout(new BoxLayout(topFiltros, BoxLayout.Y_AXIS));

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

        // Pills de Filtro de Gravedad
        JPanel filaPills = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        filaPills.setOpaque(false);

        String[] alertas = {"TODOS", "CRÍTICO", "OBSERVACIÓN", "ESTABLE", "Alta Médica"};
        for (String al : alertas) {
            JButton btnPill = crearBotonPill(al);
            filaPills.add(btnPill);
        }
        topFiltros.add(filaPills);
        panel.add(topFiltros, BorderLayout.NORTH);

        // Tabla Master
        String[] columnas = {"Box", "Paciente", "Diagnóstico", "Tutor", "Veterinario", "Alerta", "Estado"};
        modeloHospitalizados = new DefaultTableModel(columnas, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        tablaHospitalizados = new JTable(modeloHospitalizados);
        tablaHospitalizados.setRowHeight(48);
        tablaHospitalizados.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaHospitalizados.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaHospitalizados.setShowVerticalLines(false);
        tablaHospitalizados.setGridColor(new Color(241, 245, 249));
        tablaHospitalizados.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        tablaHospitalizados.getTableHeader().setBackground(new Color(248, 250, 252));
        tablaHospitalizados.getTableHeader().setForeground(Ui.TEXTO_MUTED);

        tablaHospitalizados.getColumnModel().getColumn(0).setPreferredWidth(65);
        tablaHospitalizados.getColumnModel().getColumn(1).setPreferredWidth(125);
        tablaHospitalizados.getColumnModel().getColumn(2).setPreferredWidth(140);
        tablaHospitalizados.getColumnModel().getColumn(3).setPreferredWidth(100);
        tablaHospitalizados.getColumnModel().getColumn(4).setPreferredWidth(110);
        tablaHospitalizados.getColumnModel().getColumn(5).setPreferredWidth(85);
        tablaHospitalizados.getColumnModel().getColumn(6).setPreferredWidth(100);

        tablaHospitalizados.getColumnModel().getColumn(0).setCellRenderer(new BoxBadgeRenderer());
        tablaHospitalizados.getColumnModel().getColumn(1).setCellRenderer(new PacienteMonogramaRenderer());
        tablaHospitalizados.getColumnModel().getColumn(5).setCellRenderer(new BadgeAlertaRenderer());

        tablaHospitalizados.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int r = tablaHospitalizados.getSelectedRow();
                if (r >= 0 && r < listaFiltrada.size()) {
                    mostrarDetalleEnHub(listaFiltrada.get(r));
                } else {
                    mostrarDetalleEnHub(null);
                }
            }
        });

        JScrollPane sp = new JScrollPane(tablaHospitalizados);
        sp.setBorder(BorderFactory.createLineBorder(Ui.BORDE_SUAVE, 1));
        sp.getViewport().setBackground(Color.WHITE);
        panel.add(sp, BorderLayout.CENTER);

        return panel;
    }

    private JButton crearBotonPill(String alerta) {
        JButton btn = new JButton(alerta);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 10));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(4, 8, 4, 8));

        actualizarEstiloPill(btn, alerta.equalsIgnoreCase(filtroAlerta));

        btn.addActionListener(e -> {
            filtroAlerta = alerta;
            Component[] comps = btn.getParent().getComponents();
            for (Component c : comps) {
                if (c instanceof JButton b) {
                    actualizarEstiloPill(b, b.getText().equalsIgnoreCase(filtroAlerta));
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

    private JPanel crearPanelDetail() {
        JPanel card = new JPanel(new BorderLayout()) {
            private static final long serialVersionUID = 1L;
            @Override
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

        panelHubVacio = new JPanel(new BorderLayout());
        panelHubVacio.setOpaque(false);
        JLabel lblVacio = new JLabel("<html><center>Seleccione un paciente hospitalizado para visualizar sus constantes vitales,<br>esquema de fluidoterapia, medicación activa y notas de enfermería.</center></html>", SwingConstants.CENTER);
        lblVacio.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblVacio.setForeground(Ui.TEXTO_MUTED);
        panelHubVacio.add(lblVacio, BorderLayout.CENTER);

        panelHubDetalle = new JPanel(new BorderLayout(0, 8));
        panelHubDetalle.setOpaque(false);

        // 1. Cabecera del Hub
        JPanel topHub = new JPanel(new BorderLayout(8, 0));
        topHub.setOpaque(false);

        lblHubAvatar = new JLabel() {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int size = Math.min(getWidth(), getHeight()) - 2;
                g2.setColor(new Color(254, 226, 226));
                g2.fillOval(1, 1, size, size);
                g2.setColor(Ui.COLOR_PELIGRO);
                g2.drawOval(1, 1, size, size);
                super.paintComponent(g);
                g2.dispose();
            }
        };
        lblHubAvatar.setPreferredSize(new Dimension(42, 42));
        lblHubAvatar.setHorizontalAlignment(SwingConstants.CENTER);
        lblHubAvatar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblHubAvatar.setForeground(Ui.COLOR_PELIGRO);
        topHub.add(lblHubAvatar, BorderLayout.WEST);

        JPanel centroTit = new JPanel();
        centroTit.setOpaque(false);
        centroTit.setLayout(new BoxLayout(centroTit, BoxLayout.Y_AXIS));

        lblHubNombreMascota = new JLabel("Nombre Paciente");
        lblHubNombreMascota.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblHubNombreMascota.setForeground(Ui.TEXTO_TITULO);

        lblHubSubtituloMascota = new JLabel("Especie · Raza · Código");
        lblHubSubtituloMascota.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblHubSubtituloMascota.setForeground(Ui.TEXTO_MUTED);

        centroTit.add(lblHubNombreMascota);
        centroTit.add(Box.createVerticalStrut(2));
        centroTit.add(lblHubSubtituloMascota);
        topHub.add(centroTit, BorderLayout.CENTER);

        lblHubBadgeBox = new JLabel("BOX 01 · UCI", SwingConstants.CENTER);
        lblHubBadgeBox.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblHubBadgeBox.setOpaque(true);
        lblHubBadgeBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Ui.BORDE_SUAVE, 1, true),
                new EmptyBorder(3, 8, 3, 8)
        ));
        topHub.add(lblHubBadgeBox, BorderLayout.EAST);
        panelHubDetalle.add(topHub, BorderLayout.NORTH);

        // 2. Contenido Central Estructurado
        ScrollablePanel hubBody = new ScrollablePanel() {
            private static final long serialVersionUID = 1L;
            @Override
            protected void addImpl(Component comp, Object constraints, int index) {
                if (comp instanceof javax.swing.JComponent) {
                    ((javax.swing.JComponent) comp).setAlignmentX(Component.LEFT_ALIGNMENT);
                }
                super.addImpl(comp, constraints, index);
            }
        };
        hubBody.setLayout(new BoxLayout(hubBody, BoxLayout.Y_AXIS));
        hubBody.setOpaque(false);

        // 2x2 Constantes Vitales
        JPanel gridMetricas = new JPanel(new GridLayout(2, 2, 6, 6));
        gridMetricas.setOpaque(false);
        gridMetricas.setMaximumSize(new Dimension(Integer.MAX_VALUE, 92));
        gridMetricas.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblHubPeso = new JLabel("-", SwingConstants.CENTER);
        lblHubTemp = new JLabel("-", SwingConstants.CENTER);
        lblHubFC = new JLabel("-", SwingConstants.CENTER);
        lblHubTarifa = new JLabel("-", SwingConstants.CENTER);

        gridMetricas.add(crearCajaMetrica("PESO CORPORAL", lblHubPeso, Ui.TURQUESA_OSCURO));
        gridMetricas.add(crearCajaMetrica("TEMPERATURA RECTAL", lblHubTemp, Ui.COLOR_PELIGRO));
        gridMetricas.add(crearCajaMetrica("FRECUENCIA CARDÍACA", lblHubFC, new Color(217, 119, 6)));
        gridMetricas.add(crearCajaMetrica("TARIFA DIARIA S/.", lblHubTarifa, new Color(22, 163, 74)));
        hubBody.add(gridMetricas);
        hubBody.add(vGap(8));

        // Diagnóstico & Médico
        lblHubDiagnostico = new JTextArea("-");
        lblHubDiagnostico.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblHubDiagnostico.setForeground(Ui.COLOR_PELIGRO);
        lblHubDiagnostico.setLineWrap(true);
        lblHubDiagnostico.setWrapStyleWord(true);
        lblHubDiagnostico.setEditable(false);
        lblHubDiagnostico.setOpaque(false);
        lblHubDiagnostico.setBorder(null);

        lblHubVeterinario = new JLabel("-");
        lblHubVeterinario.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblHubVeterinario.setForeground(Ui.TEXTO_TITULO);

        lblHubTutor = new JLabel("-");
        lblHubTutor.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblHubTutor.setForeground(Ui.TEXTO_TITULO);

        lblHubTelefono = new JLabel("-");
        lblHubTelefono.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblHubTelefono.setForeground(new Color(2, 132, 199));

        JPanel secDiag = crearSeccionCard("DIAGNÓSTICO & EQUIPO MÉDICO");
        secDiag.add(crearItemLinea("Diagnóstico Ingreso:", lblHubDiagnostico));
        secDiag.add(crearItemLinea("Médico Tratante:", lblHubVeterinario));
        secDiag.add(crearItemLinea("Tutor Titular:", lblHubTutor));
        secDiag.add(crearItemLinea("Teléfono Emergencia:", lblHubTelefono));
        hubBody.add(secDiag);
        hubBody.add(vGap(6));

        // Fluidoterapia & Vía
        txtHubFluidoterapia = new JTextArea(2, 20);
        txtHubFluidoterapia.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        txtHubFluidoterapia.setLineWrap(true);
        txtHubFluidoterapia.setWrapStyleWord(true);
        txtHubFluidoterapia.setEditable(false);
        txtHubFluidoterapia.setOpaque(false);
        txtHubFluidoterapia.setAlignmentX(Component.LEFT_ALIGNMENT);
        JPanel secFlu = crearSeccionCard("FLUIDOTERAPIA & VÍA DE INFUSIÓN");
        secFlu.add(txtHubFluidoterapia);
        hubBody.add(secFlu);
        hubBody.add(vGap(6));

        // Medicación Activa
        txtHubMedicacion = new JTextArea(2, 20);
        txtHubMedicacion.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        txtHubMedicacion.setLineWrap(true);
        txtHubMedicacion.setWrapStyleWord(true);
        txtHubMedicacion.setEditable(false);
        txtHubMedicacion.setOpaque(false);
        txtHubMedicacion.setAlignmentX(Component.LEFT_ALIGNMENT);
        JPanel secMed = crearSeccionCard("MEDICAMENTOS INTRAHOSPITALARIOS ACTIVOS");
        secMed.add(txtHubMedicacion);
        hubBody.add(secMed);
        hubBody.add(vGap(6));

        // Evolución Diaria
        txtHubEvolucion = new JTextArea(3, 20);
        txtHubEvolucion.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        txtHubEvolucion.setLineWrap(true);
        txtHubEvolucion.setWrapStyleWord(true);
        txtHubEvolucion.setEditable(false);
        txtHubEvolucion.setOpaque(false);
        txtHubEvolucion.setAlignmentX(Component.LEFT_ALIGNMENT);
        JPanel secEvo = crearSeccionCard("EVOLUCIÓN & NOTAS DE ENFERMERÍA 24/7");
        secEvo.add(txtHubEvolucion);
        hubBody.add(secEvo);

        JScrollPane spHub = new JScrollPane(hubBody);
        spHub.setBorder(null);
        spHub.setOpaque(false);
        spHub.getViewport().setOpaque(false);
        spHub.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        spHub.getVerticalScrollBar().setUnitIncrement(16);
        panelHubDetalle.add(spHub, BorderLayout.CENTER);

        // 3. Barra Inferior de Acciones Rápidas
        JPanel barraAcciones = new JPanel(new GridLayout(2, 2, 6, 6));
        barraAcciones.setOpaque(false);
        barraAcciones.setPreferredSize(new Dimension(0, 62));
        barraAcciones.setMaximumSize(new Dimension(Integer.MAX_VALUE, 62));

        JButton btnAlta = crearBotonAccion("Dar Alta Médica", Iconos.crearIconoCheck(14, Color.WHITE), true);
        btnAlta.addActionListener(e -> darAltaMedicaSeleccionada());

        JButton btnEpicrisis = crearBotonAccion("Resumen Epicrisis", Iconos.crearIconoDocumento(14, new Color(37, 99, 235)), false);
        btnEpicrisis.addActionListener(e -> emitirEpicrisisHospitalaria());

        JButton btnNotificar = crearBotonAccion("Notificar Tutor", Iconos.crearIconoCampana(14, Ui.COLOR_ADVERTENCIA), false);
        btnNotificar.addActionListener(e -> notificarTutorHospitalizado());

        JButton btnEditar = crearBotonAccion("Editar / Modificar", Iconos.crearIconoLapiz(14, new Color(100, 116, 139)), false);
        btnEditar.addActionListener(e -> abrirModalEditar());

        barraAcciones.add(btnAlta);
        barraAcciones.add(btnEpicrisis);
        barraAcciones.add(btnNotificar);
        barraAcciones.add(btnEditar);

        panelHubDetalle.add(barraAcciones, BorderLayout.SOUTH);

        panelHubContenido = card;
        panelHubContenido.add(panelHubVacio, BorderLayout.CENTER);
        return card;
    }

    private JPanel crearCajaMetrica(String etiqueta, JLabel lblValor, Color colorValor) {
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

    private JPanel crearSeccionCard(String titulo) {
        JPanel sec = new JPanel() {
            private static final long serialVersionUID = 1L;
            @Override
            public Dimension getMaximumSize() {
                return new Dimension(Integer.MAX_VALUE, getPreferredSize().height);
            }
            @Override
            protected void addImpl(Component comp, Object constraints, int index) {
                if (comp instanceof javax.swing.JComponent) {
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

    private JPanel crearItemLinea(String label, Component valor) {
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
        if (c instanceof javax.swing.JComponent) {
            ((javax.swing.JComponent) c).setAlignmentX(Component.LEFT_ALIGNMENT);
        }
        return c;
    }

    public void recargarDatos() {
        filtrarYActualizarTabla();

        List<InternamientoHospitalario> todos = repo.getInternamientos();
        long activos = 0;
        long criticos = 0;

        for (int i = 0; i < 9; i++) {
            String bName = nombresBoxes[i];
            Optional<InternamientoHospitalario> optH = todos.stream()
                    .filter(h -> h.getNumeroBox().equalsIgnoreCase(bName) && !"Alta Médica".equalsIgnoreCase(h.getEstado()))
                    .findFirst();

            JButton btn = botonesBoxes[i];
            if (optH.isPresent()) {
                InternamientoHospitalario h = optH.get();
                btn.setText("<html><center><b>" + bName + "</b><br><font size='1'>" + h.getNombreMascota() + "</font></center></html>");
                if ("CRÍTICO".equalsIgnoreCase(h.getNivelAlerta())) {
                    btn.putClientProperty("bgColor", new Color(254, 226, 226));
                    btn.putClientProperty("borderColor", new Color(239, 68, 68));
                    btn.setForeground(new Color(185, 28, 28));
                } else if (h.getTipoBox().contains("Aislamiento")) {
                    btn.putClientProperty("bgColor", new Color(254, 243, 199));
                    btn.putClientProperty("borderColor", new Color(245, 158, 11));
                    btn.setForeground(new Color(180, 83, 9));
                } else {
                    btn.putClientProperty("bgColor", new Color(224, 242, 254));
                    btn.putClientProperty("borderColor", new Color(2, 132, 199));
                    btn.setForeground(Ui.TURQUESA);
                }
            } else {
                btn.setText("<html><center><b>" + bName + "</b><br><font size='1' color='#16a34a'>Libre</font></center></html>");
                btn.putClientProperty("bgColor", new Color(240, 253, 244));
                btn.putClientProperty("borderColor", new Color(187, 247, 208));
                btn.setForeground(new Color(22, 163, 74));
            }
            btn.repaint();
        }

        for (InternamientoHospitalario h : todos) {
            if (!"Alta Médica".equalsIgnoreCase(h.getEstado())) {
                activos++;
                if ("CRÍTICO".equalsIgnoreCase(h.getNivelAlerta())) criticos++;
            }
        }

        lblKpiOcupados.setText(activos + " / 9");
        lblKpiCriticos.setText(String.valueOf(criticos));
        lblKpiDisponibles.setText(String.valueOf(9 - activos));
        int porcentaje = (int) Math.round((activos / 9.0) * 100);
        lblKpiTasaOcupacion.setText(porcentaje + "%");
    }

    private void filtrarYActualizarTabla() {
        List<InternamientoHospitalario> todos = repo.getInternamientos();
        String txt = txtBuscar != null ? txtBuscar.getText().trim().toLowerCase() : "";

        listaFiltrada.clear();
        for (InternamientoHospitalario h : todos) {
            boolean coincideAlerta = "TODOS".equalsIgnoreCase(filtroAlerta) ||
                    ("Alta Médica".equalsIgnoreCase(filtroAlerta) && "Alta Médica".equalsIgnoreCase(h.getEstado())) ||
                    (filtroAlerta.equalsIgnoreCase(h.getNivelAlerta()) && !"Alta Médica".equalsIgnoreCase(h.getEstado()));
            if (!coincideAlerta) continue;

            if (!txt.isEmpty()) {
                boolean coincideTexto = h.getNombreMascota().toLowerCase().contains(txt) ||
                        h.getNumeroBox().toLowerCase().contains(txt) ||
                        h.getDiagnosticoIngreso().toLowerCase().contains(txt) ||
                        h.getNombreTutor().toLowerCase().contains(txt) ||
                        h.getVeterinarioTratante().toLowerCase().contains(txt);
                if (!coincideTexto) continue;
            }

            listaFiltrada.add(h);
        }

        modeloHospitalizados.setRowCount(0);
        for (InternamientoHospitalario h : listaFiltrada) {
            modeloHospitalizados.addRow(new Object[]{
                    h.getNumeroBox(),
                    h.getNombreMascota() + " (" + h.getEspecieRaza() + ")",
                    h.getDiagnosticoIngreso(),
                    h.getNombreTutor(),
                    h.getVeterinarioTratante(),
                    h.getNivelAlerta(),
                    h.getEstado()
            });
        }

        if (!listaFiltrada.isEmpty()) {
            tablaHospitalizados.setRowSelectionInterval(0, 0);
            mostrarDetalleEnHub(listaFiltrada.get(0));
        } else {
            mostrarDetalleEnHub(null);
        }
    }

    private void seleccionarPorBox(String boxName) {
        for (int i = 0; i < listaFiltrada.size(); i++) {
            if (listaFiltrada.get(i).getNumeroBox().equalsIgnoreCase(boxName)) {
                tablaHospitalizados.setRowSelectionInterval(i, i);
                mostrarDetalleEnHub(listaFiltrada.get(i));
                return;
            }
        }
        // Si no está en la lista filtrada, buscamos en todos
        Optional<InternamientoHospitalario> opt = repo.getInternamientos().stream()
                .filter(h -> h.getNumeroBox().equalsIgnoreCase(boxName) && !"Alta Médica".equalsIgnoreCase(h.getEstado()))
                .findFirst();
        if (opt.isPresent()) {
            filtroAlerta = "TODOS";
            filtrarYActualizarTabla();
            seleccionarPorBox(boxName);
        } else {
            JOptionPane.showMessageDialog(this, "El " + boxName + " se encuentra actualmente libre y desinfectado.", "Canil Disponible", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void mostrarDetalleEnHub(InternamientoHospitalario h) {
        internamientoSeleccionado = h;
        if (panelHubContenido == null) return;

        panelHubContenido.removeAll();
        if (h == null) {
            panelHubContenido.add(panelHubVacio, BorderLayout.CENTER);
            panelHubContenido.revalidate();
            panelHubContenido.repaint();
            return;
        }

        panelHubContenido.add(panelHubDetalle, BorderLayout.CENTER);

        String inicial = !h.getNombreMascota().isEmpty() ? h.getNombreMascota().substring(0, 1).toUpperCase() : "M";
        lblHubAvatar.setText(inicial);
        lblHubNombreMascota.setText(h.getNombreMascota());
        lblHubSubtituloMascota.setText(h.getEspecieRaza() + " · Código: " + h.getCodigoMascota());

        lblHubBadgeBox.setText(h.getNumeroBox() + " · " + h.getNivelAlerta());
        configurarBadgeBox(lblHubBadgeBox, h.getNivelAlerta());

        lblHubPeso.setText(h.getPesoActualKg() + " kg");
        lblHubTemp.setText(h.getTemperaturaC() + " °C");
        lblHubFC.setText(h.getFrecuenciaCardiaca() + " lpm");
        lblHubTarifa.setText(String.format("S/. %.2f", h.getCostoDia()));

        lblHubDiagnostico.setText(h.getDiagnosticoIngreso());
        lblHubVeterinario.setText(h.getVeterinarioTratante());
        lblHubTutor.setText(h.getNombreTutor());
        lblHubTelefono.setText(h.getTelefonoTutor().isEmpty() ? "Sin contacto" : h.getTelefonoTutor());

        txtHubFluidoterapia.setText(h.getFluidoterapia());
        txtHubMedicacion.setText(h.getMedicacionActual());
        txtHubEvolucion.setText(h.getEvolucionNotas());

        panelHubContenido.revalidate();
        panelHubContenido.repaint();
    }

    private void configurarBadgeBox(JLabel lbl, String alerta) {
        if ("CRÍTICO".equalsIgnoreCase(alerta)) {
            lbl.setBackground(new Color(254, 226, 226));
            lbl.setForeground(new Color(185, 28, 28));
        } else if ("OBSERVACIÓN".equalsIgnoreCase(alerta)) {
            lbl.setBackground(new Color(254, 243, 199));
            lbl.setForeground(new Color(180, 83, 9));
        } else {
            lbl.setBackground(new Color(220, 252, 231));
            lbl.setForeground(new Color(22, 163, 74));
        }
    }

    private void abrirModalNuevoIngreso() {
        FormularioHospitalizacionDialog dlg = new FormularioHospitalizacionDialog(javax.swing.SwingUtilities.getWindowAncestor(this));
        dlg.setVisible(true);
        if (dlg.isGuardado()) {
            recargarDatos();
        }
    }

    private void abrirModalEditar() {
        if (internamientoSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un paciente para editar su expediente.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String[] opciones = {"Editar Ficha", "Eliminar Registro", "Cerrar"};
        int sel = JOptionPane.showOptionDialog(this,
                "¿Qué acción desea realizar sobre el internamiento de " + internamientoSeleccionado.getNombreMascota() + " (" + internamientoSeleccionado.getNumeroBox() + ")?",
                "Gestión de Expediente Clínico",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                opciones,
                opciones[0]);

        if (sel == 0) {
            FormularioHospitalizacionDialog dlg = new FormularioHospitalizacionDialog(javax.swing.SwingUtilities.getWindowAncestor(this), internamientoSeleccionado);
            dlg.setVisible(true);
            if (dlg.isGuardado()) {
                recargarDatos();
            }
        } else if (sel == 1) {
            int conf = JOptionPane.showConfirmDialog(this,
                    "¿Desea eliminar el registro hospitalario " + internamientoSeleccionado.getIdInternamiento() + "?",
                    "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);
            if (conf == JOptionPane.YES_OPTION) {
                repo.eliminarInternamiento(internamientoSeleccionado.getIdInternamiento());
                recargarDatos();
            }
        }
    }

    private void darAltaMedicaSeleccionada() {
        if (internamientoSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un paciente para dar de alta.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if ("Alta Médica".equalsIgnoreCase(internamientoSeleccionado.getEstado())) {
            JOptionPane.showMessageDialog(this, "Este paciente ya tiene otorgada el Alta Médica.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int conf = JOptionPane.showConfirmDialog(this,
                "¿Confirmar el Alta Médica para " + internamientoSeleccionado.getNombreMascota() + "?\n" +
                "El canil " + internamientoSeleccionado.getNumeroBox() + " quedará libre.",
                "Confirmar Alta", JOptionPane.YES_NO_OPTION);

        if (conf == JOptionPane.YES_OPTION) {
            internamientoSeleccionado.setEstado("Alta Médica");
            recargarDatos();
            JOptionPane.showMessageDialog(this,
                    "Alta Médica otorgada con éxito para " + internamientoSeleccionado.getNombreMascota() + ".\n" +
                    "Canil " + internamientoSeleccionado.getNumeroBox() + " disponible para sanitización.",
                    "Alta Concluida", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void emitirEpicrisisHospitalaria() {
        if (internamientoSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un paciente para emitir epicrisis.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String[][] datos = new String[][]{
                {"1", "Ingreso y Diagnóstico", internamientoSeleccionado.getDiagnosticoIngreso(), "Fecha: " + internamientoSeleccionado.getFechaIngresoFormateada() + " " + internamientoSeleccionado.getHoraIngresoFormateada(), "Ingresado"},
                {"2", "Terapia y Medicación", internamientoSeleccionado.getFluidoterapia() + " | " + internamientoSeleccionado.getMedicacionActual(), "Veterinario: " + internamientoSeleccionado.getVeterinarioTratante(), internamientoSeleccionado.getNivelAlerta()},
                {"3", "Evolución y Estado", internamientoSeleccionado.getEvolucionNotas(), "Canil Box: " + internamientoSeleccionado.getNumeroBox(), internamientoSeleccionado.getEstado()}
        };

        Ui.mostrarVisorReporte(
                (java.awt.Frame) javax.swing.SwingUtilities.getWindowAncestor(this),
                "EPICRISIS Y REPORTE DE HOSPITALIZACIÓN",
                "Paciente: " + internamientoSeleccionado.getNombreMascota() + " (" + internamientoSeleccionado.getEspecieRaza() + ") | Tutor: " + internamientoSeleccionado.getNombreTutor() + " | Tel: " + internamientoSeleccionado.getTelefonoTutor(),
                "Código Internamiento: " + internamientoSeleccionado.getIdInternamiento() + " | Box: " + internamientoSeleccionado.getNumeroBox() + " | Estado: " + internamientoSeleccionado.getEstado(),
                new String[]{"Fase", "Etapa Clínica", "Protocolo / Medicación", "Responsable / Ubicación", "Estado"},
                datos,
                "UNIDAD DE CUIDADOS INTENSIVOS Y HOSPITALIZACIÓN · HAPPY PETS",
                "Epicrisis_" + internamientoSeleccionado.getIdInternamiento()
        );
    }

    private void notificarTutorHospitalizado() {
        if (internamientoSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un paciente para notificar al tutor.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String msg = "Estimado/a " + internamientoSeleccionado.getNombreTutor() + ", le informamos que " + internamientoSeleccionado.getNombreMascota() +
                " en " + internamientoSeleccionado.getNumeroBox() + " se encuentra actualmente " + internamientoSeleccionado.getNivelAlerta().toLowerCase() +
                ". Su evolución médica: \"" + internamientoSeleccionado.getEvolucionNotas() + "\". El equipo veterinario sigue a su entera disposición.";

        JOptionPane.showMessageDialog(this,
                "Reporte de evolución médica para " + internamientoSeleccionado.getNombreTutor() + " (" + internamientoSeleccionado.getTelefonoTutor() + "):\n\n\"" + msg + "\"",
                "Notificación Clínica al Tutor", JOptionPane.INFORMATION_MESSAGE);
    }

    private JButton crearBotonAccion(String texto, boolean primario) {
        return crearBotonAccion(texto, null, primario);
    }

    private JButton crearBotonAccion(String texto, Icon icono, boolean primario) {
        JButton btn = new JButton(texto) {
            private static final long serialVersionUID = 1L;
            @Override
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

    private static class BoxBadgeRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            lbl.setHorizontalAlignment(SwingConstants.CENTER);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
            String val = String.valueOf(value);
            if (!isSelected) {
                if (val.contains("01")) {
                    lbl.setForeground(new Color(185, 28, 28));
                } else if (val.contains("05")) {
                    lbl.setForeground(new Color(180, 83, 9));
                } else {
                    lbl.setForeground(Ui.TURQUESA);
                }
            }
            return lbl;
        }
    }

    private static class PacienteMonogramaRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JPanel p = new JPanel(new BorderLayout(8, 0));
            p.setOpaque(true);
            p.setBackground(isSelected ? table.getSelectionBackground() : (row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252)));
            p.setBorder(new EmptyBorder(0, 8, 0, 8));

            String text = value != null ? value.toString() : "";
            String inicial = !text.isEmpty() ? text.substring(0, 1).toUpperCase() : "M";

            JLabel avatar = new JLabel(inicial, SwingConstants.CENTER) {
                private static final long serialVersionUID = 1L;
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    int s = Math.min(getWidth(), getHeight()) - 2;
                    int x = (getWidth() - s) / 2;
                    int y = (getHeight() - s) / 2;
                    g2.setColor(new Color(254, 226, 226));
                    g2.fillOval(x, y, s, s);
                    g2.setColor(Ui.COLOR_PELIGRO);
                    g2.drawOval(x, y, s, s);
                    super.paintComponent(g2);
                    g2.dispose();
                }
            };
            avatar.setPreferredSize(new Dimension(28, 28));
            avatar.setFont(new Font("Segoe UI", Font.BOLD, 12));
            avatar.setForeground(Ui.COLOR_PELIGRO);

            JLabel lblNombre = new JLabel(text);
            lblNombre.setFont(new Font("Segoe UI", Font.BOLD, 12));
            lblNombre.setForeground(isSelected ? table.getSelectionForeground() : Ui.TEXTO_TITULO);

            p.add(avatar, BorderLayout.WEST);
            p.add(lblNombre, BorderLayout.CENTER);
            return p;
        }
    }

    private static class BadgeAlertaRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            lbl.setHorizontalAlignment(SwingConstants.CENTER);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
            String al = String.valueOf(value);
            if (!isSelected) {
                if ("CRÍTICO".equalsIgnoreCase(al)) {
                    lbl.setForeground(new Color(220, 38, 38));
                } else if ("OBSERVACIÓN".equalsIgnoreCase(al)) {
                    lbl.setForeground(new Color(217, 119, 6));
                } else {
                    lbl.setForeground(new Color(22, 163, 74));
                }
            }
            return lbl;
        }
    }

    private static class ScrollablePanel extends JPanel implements javax.swing.Scrollable {
        private static final long serialVersionUID = 1L;
        @Override public Dimension getPreferredScrollableViewportSize() { return getPreferredSize(); }
        @Override public int getScrollableUnitIncrement(java.awt.Rectangle visibleRect, int orientation, int direction) { return 16; }
        @Override public int getScrollableBlockIncrement(java.awt.Rectangle visibleRect, int orientation, int direction) { return 32; }
        @Override public boolean getScrollableTracksViewportWidth() { return true; }
        @Override public boolean getScrollableTracksViewportHeight() { return false; }
    }
}
