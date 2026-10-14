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
import java.time.format.DateTimeFormatter;
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
import happypets.model.MascotaAdopcion;
import happypets.ui.Iconos;
import happypets.ui.Ui;

/**
 * Submódulo 4.4: Adopciones y Rescates Responsables.
 * Arquitectura Master-Detail (58% / 42%) moderna, sin scrollbars horizontales ni espacio en blanco.
 * Incluye Live Hub de bienestar animal, actas legales de compromiso y difusión en redes/WhatsApp.
 */
public class VistaAdopcionesPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private final RepositorioVeterinaria repo = RepositorioVeterinaria.getInstancia();

    // KPIs
    private JLabel lblKpiDisponibles;
    private JLabel lblKpiEvaluacion;
    private JLabel lblKpiAdaptacion;
    private JLabel lblKpiAdoptados;

    // Master Table
    private JTable tablaAdopciones;
    private DefaultTableModel modeloAdopciones;
    private JTextField txtBuscar;
    private String filtroEstado = "TODOS";
    private List<MascotaAdopcion> listaFiltrada = new ArrayList<>();
    private MascotaAdopcion mascotaSeleccionada = null;

    // Live Detail Hub
    private JPanel panelHubContenido;
    private JPanel panelHubVacio;
    private JPanel panelHubDetalle;
    private JLabel lblHubAvatar;
    private JLabel lblHubNombreMascota;
    private JLabel lblHubSubtituloMascota;
    private JLabel lblHubBadgeEstado;
    private JLabel lblHubEspecieSexo;
    private JLabel lblHubEdad;
    private JLabel lblHubDonacion;
    private JLabel lblHubTamano;
    private JPanel panelHubSanidadBadges;
    private JLabel lblHubTemperamento;
    private JTextArea txtHubHistoria;
    private JLabel lblHubAdoptante;
    private JLabel lblHubAdoptanteDni;
    private JLabel lblHubAdoptanteTel;
    private JLabel lblHubAdoptanteDir;

    public VistaAdopcionesPanel() {
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

        JLabel titulo = new JLabel("Adopciones y Rescates Responsables");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 17));
        titulo.setForeground(Ui.TEXTO_TITULO);

        JLabel subtitulo = new JLabel("Módulo 4.4 · Catálogo de rescatados, perfil sanitario, evaluación de postulantes y actas de compromiso");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitulo.setForeground(Ui.TEXTO_MUTED);

        izq.add(titulo);
        izq.add(Box.createVerticalStrut(2));
        izq.add(subtitulo);
        cab.add(izq, BorderLayout.CENTER);

        JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        der.setOpaque(false);

        JLabel badgeRefugio = new JLabel(" Programa de Tenencia Responsable · Ley 30407 ", SwingConstants.CENTER);
        badgeRefugio.setFont(new Font("Segoe UI", Font.BOLD, 11));
        badgeRefugio.setForeground(Ui.TURQUESA_PROFUNDO);
        badgeRefugio.setOpaque(true);
        badgeRefugio.setBackground(Ui.TURQUESA_SUAVE);
        badgeRefugio.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Ui.TURQUESA_MEDIO, 1, true),
                new EmptyBorder(4, 10, 4, 10)
        ));
        der.add(badgeRefugio);

        JButton btnNuevo = crearBotonAccion("Ingresar Rescatado", Iconos.crearIconoCorazonMascota(14, Color.WHITE), true);
        btnNuevo.addActionListener(e -> abrirModalNuevoRescatado());
        btnNuevo.setPreferredSize(new Dimension(180, 32));
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

        lblKpiDisponibles = new JLabel("0", SwingConstants.LEFT);
        lblKpiEvaluacion = new JLabel("0", SwingConstants.LEFT);
        lblKpiAdaptacion = new JLabel("0", SwingConstants.LEFT);
        lblKpiAdoptados = new JLabel("0", SwingConstants.LEFT);

        fila.add(crearCardKpi(lblKpiDisponibles, "DISPONIBLES ADOPCIÓN", Ui.TURQUESA_SUAVE, Ui.TURQUESA, Iconos.crearIconoCorazonMascota(20, Ui.TURQUESA)));
        fila.add(crearCardKpi(lblKpiEvaluacion, "POSTULACIONES EN REVISIÓN", new Color(254, 243, 199), new Color(217, 119, 6), Iconos.crearIconoDocumento(20, new Color(217, 119, 6))));
        fila.add(crearCardKpi(lblKpiAdaptacion, "PERÍODO DE ADAPTACIÓN", new Color(243, 232, 255), new Color(147, 51, 234), Iconos.crearIconoReloj(20, new Color(147, 51, 234))));
        fila.add(crearCardKpi(lblKpiAdoptados, "ADOPCIONES EXITOSAS", new Color(220, 252, 231), Ui.COLOR_EXITO, Iconos.crearIconoCheck(20, Ui.COLOR_EXITO)));

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
                super.paintComponent(g);
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
                super.paintComponent(g);
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
                super.paintComponent(g);
                g2.dispose();
            }
        };
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(12, 14, 12, 14));

        // Buscador y Pills
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

        // Pills de Estado
        JPanel filaPills = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        filaPills.setOpaque(false);

        String[] estados = {"TODOS", "Disponible", "En Evaluación", "En Adaptación (Prueba)", "Adoptado con Éxito"};
        for (String est : estados) {
            JButton btnPill = crearBotonPill(est);
            filaPills.add(btnPill);
        }
        topFiltros.add(filaPills);
        panel.add(topFiltros, BorderLayout.NORTH);

        // Tabla Master
        String[] columnas = {"ID", "Nombre", "Especie / Raza", "Edad / Sexo", "Sanidad", "Postulante", "Estado"};
        modeloAdopciones = new DefaultTableModel(columnas, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        tablaAdopciones = new JTable(modeloAdopciones);
        tablaAdopciones.setRowHeight(48);
        tablaAdopciones.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaAdopciones.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaAdopciones.setShowVerticalLines(false);
        tablaAdopciones.setGridColor(new Color(241, 245, 249));
        tablaAdopciones.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        tablaAdopciones.getTableHeader().setBackground(new Color(248, 250, 252));
        tablaAdopciones.getTableHeader().setForeground(Ui.TEXTO_MUTED);

        tablaAdopciones.getColumnModel().getColumn(0).setPreferredWidth(65);
        tablaAdopciones.getColumnModel().getColumn(1).setPreferredWidth(125);
        tablaAdopciones.getColumnModel().getColumn(2).setPreferredWidth(115);
        tablaAdopciones.getColumnModel().getColumn(3).setPreferredWidth(95);
        tablaAdopciones.getColumnModel().getColumn(4).setPreferredWidth(105);
        tablaAdopciones.getColumnModel().getColumn(5).setPreferredWidth(100);
        tablaAdopciones.getColumnModel().getColumn(6).setPreferredWidth(115);

        tablaAdopciones.getColumnModel().getColumn(1).setCellRenderer(new PacienteMonogramaRenderer());
        tablaAdopciones.getColumnModel().getColumn(6).setCellRenderer(new BadgeEstadoAdopcionRenderer());

        tablaAdopciones.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int r = tablaAdopciones.getSelectedRow();
                if (r >= 0 && r < listaFiltrada.size()) {
                    mostrarDetalleEnHub(listaFiltrada.get(r));
                } else {
                    mostrarDetalleEnHub(null);
                }
            }
        });

        JScrollPane sp = new JScrollPane(tablaAdopciones);
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
                super.paintComponent(g);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(10, 12, 10, 12));
        card.setPreferredSize(new Dimension(360, 0));
        card.setMinimumSize(new Dimension(320, 0));

        panelHubVacio = new JPanel(new BorderLayout());
        panelHubVacio.setOpaque(false);
        JLabel lblVacio = new JLabel("<html><center>Seleccione un rescatado para consultar su perfil sanitario completo,<br>historia de recuperación, datos del postulante y acta de adopción.</center></html>", SwingConstants.CENTER);
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
                g2.setColor(Ui.TURQUESA_SUAVE);
                g2.fillOval(1, 1, size, size);
                g2.setColor(Ui.TURQUESA_PROFUNDO);
                g2.drawOval(1, 1, size, size);
                super.paintComponent(g);
                g2.dispose();
            }
        };
        lblHubAvatar.setPreferredSize(new Dimension(42, 42));
        lblHubAvatar.setHorizontalAlignment(SwingConstants.CENTER);
        lblHubAvatar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblHubAvatar.setForeground(Ui.TURQUESA_PROFUNDO);
        topHub.add(lblHubAvatar, BorderLayout.WEST);

        JPanel centroTit = new JPanel();
        centroTit.setOpaque(false);
        centroTit.setLayout(new BoxLayout(centroTit, BoxLayout.Y_AXIS));

        lblHubNombreMascota = new JLabel("Nombre Rescatado");
        lblHubNombreMascota.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblHubNombreMascota.setForeground(Ui.TEXTO_TITULO);

        lblHubSubtituloMascota = new JLabel("Especie · Raza · Código");
        lblHubSubtituloMascota.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblHubSubtituloMascota.setForeground(Ui.TEXTO_MUTED);

        centroTit.add(lblHubNombreMascota);
        centroTit.add(Box.createVerticalStrut(2));
        centroTit.add(lblHubSubtituloMascota);
        topHub.add(centroTit, BorderLayout.CENTER);

        lblHubBadgeEstado = new JLabel("DISPONIBLE", SwingConstants.CENTER);
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
                if (comp instanceof javax.swing.JComponent) {
                    ((javax.swing.JComponent) comp).setAlignmentX(Component.LEFT_ALIGNMENT);
                }
                super.addImpl(comp, constraints, index);
            }
        };
        hubBody.setLayout(new BoxLayout(hubBody, BoxLayout.Y_AXIS));
        hubBody.setOpaque(false);

        // 2x2 Telemetría / Perfil
        JPanel gridMetricas = new JPanel(new GridLayout(2, 2, 6, 6));
        gridMetricas.setOpaque(false);
        gridMetricas.setMaximumSize(new Dimension(Integer.MAX_VALUE, 92));
        gridMetricas.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblHubEspecieSexo = new JLabel("-", SwingConstants.CENTER);
        lblHubEdad = new JLabel("-", SwingConstants.CENTER);
        lblHubDonacion = new JLabel("-", SwingConstants.CENTER);
        lblHubTamano = new JLabel("-", SwingConstants.CENTER);

        gridMetricas.add(crearCajaMetrica("ESPECIE / SEXO", lblHubEspecieSexo, Ui.TURQUESA_OSCURO));
        gridMetricas.add(crearCajaMetrica("EDAD ESTIMADA", lblHubEdad, Ui.TEXTO_TITULO));
        gridMetricas.add(crearCajaMetrica("CUOTA SOLIDARIA S/.", lblHubDonacion, new Color(22, 163, 74)));
        gridMetricas.add(crearCajaMetrica("PORTE / TAMAÑO", lblHubTamano, new Color(147, 51, 234)));
        hubBody.add(gridMetricas);
        hubBody.add(vGap(8));

        // Protocolo Sanitario Oficial
        panelHubSanidadBadges = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
        panelHubSanidadBadges.setOpaque(false);
        JPanel secSan = crearSeccionCard("PROTOCOLO SANITARIO OFICIAL (LEY 30407)");
        secSan.add(panelHubSanidadBadges);
        hubBody.add(secSan);
        hubBody.add(vGap(6));

        // Temperamento & Conducta
        lblHubTemperamento = new JLabel("-");
        lblHubTemperamento.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblHubTemperamento.setForeground(Ui.TEXTO_TITULO);
        JPanel secTemp = crearSeccionCard("TEMPERAMENTO & CONVIVENCIA");
        secTemp.add(lblHubTemperamento);
        hubBody.add(secTemp);
        hubBody.add(vGap(6));

        // Historia de Rescate
        txtHubHistoria = new JTextArea(2, 20);
        txtHubHistoria.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        txtHubHistoria.setLineWrap(true);
        txtHubHistoria.setWrapStyleWord(true);
        txtHubHistoria.setEditable(false);
        txtHubHistoria.setOpaque(false);
        JPanel secHis = crearSeccionCard("HISTORIA DE RESCATE & REHABILITACIÓN");
        secHis.add(txtHubHistoria);
        hubBody.add(secHis);
        hubBody.add(vGap(6));

        // Postulante / Adoptante
        lblHubAdoptante = new JLabel("Sin postulante registrado");
        lblHubAdoptante.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblHubAdoptante.setForeground(Ui.TURQUESA_PROFUNDO);

        lblHubAdoptanteDni = new JLabel("-");
        lblHubAdoptanteDni.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblHubAdoptanteDni.setForeground(Ui.TEXTO_MUTED);

        lblHubAdoptanteTel = new JLabel("-");
        lblHubAdoptanteTel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblHubAdoptanteTel.setForeground(new Color(2, 132, 199));

        lblHubAdoptanteDir = new JLabel("-");
        lblHubAdoptanteDir.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblHubAdoptanteDir.setForeground(Ui.TEXTO_TITULO);

        JPanel secPost = crearSeccionCard("DATOS DEL POSTULANTE / ADOPTANTE");
        secPost.add(crearItemLinea("Adoptante:", lblHubAdoptante));
        secPost.add(crearItemLinea("DNI / Doc:", lblHubAdoptanteDni));
        secPost.add(crearItemLinea("Teléfono WhatsApp:", lblHubAdoptanteTel));
        secPost.add(crearItemLinea("Domicilio / Vivienda:", lblHubAdoptanteDir));
        hubBody.add(secPost);

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

        JButton btnEvaluar = crearBotonAccion("Evaluar / Ficha", Iconos.crearIconoLapiz(14, Color.WHITE), true);
        btnEvaluar.addActionListener(e -> abrirModalEditar());

        JButton btnAdaptacion = crearBotonAccion("Pasar a Adaptación", Iconos.crearIconoHuella(14, Ui.TURQUESA_OSCURO), false);
        btnAdaptacion.addActionListener(e -> pasarAAdaptacion());

        JButton btnActa = crearBotonAccion("Emitir Acta Legal", Iconos.crearIconoCertificado(14, new Color(37, 99, 235)), false);
        btnActa.addActionListener(e -> finalizarActaAdopcion());

        JButton btnDifusion = crearBotonAccion("Difusión Redes", Iconos.crearIconoMensaje(14, new Color(147, 51, 234)), false);
        btnDifusion.addActionListener(e -> generarDifusionWhatsapp());

        barraAcciones.add(btnEvaluar);
        barraAcciones.add(btnAdaptacion);
        barraAcciones.add(btnActa);
        barraAcciones.add(btnDifusion);

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

        List<MascotaAdopcion> todas = repo.getMascotasAdopcion();
        long disp = todas.stream().filter(m -> "Disponible".equalsIgnoreCase(m.getEstado())).count();
        long eval = todas.stream().filter(m -> "En Evaluación".equalsIgnoreCase(m.getEstado())).count();
        long adap = todas.stream().filter(m -> m.getEstado().contains("Adaptación")).count();
        long adop = todas.stream().filter(m -> m.getEstado().contains("Adoptado")).count();

        lblKpiDisponibles.setText(String.valueOf(disp));
        lblKpiEvaluacion.setText(String.valueOf(eval));
        lblKpiAdaptacion.setText(String.valueOf(adap));
        lblKpiAdoptados.setText(String.valueOf(adop));
    }

    private void filtrarYActualizarTabla() {
        List<MascotaAdopcion> todas = repo.getMascotasAdopcion();
        String txt = txtBuscar != null ? txtBuscar.getText().trim().toLowerCase() : "";

        listaFiltrada.clear();
        for (MascotaAdopcion m : todas) {
            boolean coincideEstado = "TODOS".equalsIgnoreCase(filtroEstado) || m.getEstado().equalsIgnoreCase(filtroEstado);
            if (!coincideEstado) continue;

            if (!txt.isEmpty()) {
                boolean coincideTexto = m.getNombre().toLowerCase().contains(txt) ||
                        m.getRaza().toLowerCase().contains(txt) ||
                        m.getIdMascotaAdopcion().toLowerCase().contains(txt) ||
                        (m.getAdoptanteNombre() != null && m.getAdoptanteNombre().toLowerCase().contains(txt));
                if (!coincideTexto) continue;
            }

            listaFiltrada.add(m);
        }

        modeloAdopciones.setRowCount(0);
        for (MascotaAdopcion m : listaFiltrada) {
            String sanidad = (m.isEsterilizado() ? "Est: SI " : "") +
                             (m.isVacunado() ? "Vac: SI " : "") +
                             (m.isDesparasitado() ? "Desp: SI" : "");

            modeloAdopciones.addRow(new Object[]{
                    m.getIdMascotaAdopcion(),
                    m.getNombre(),
                    m.getEspecie() + " · " + m.getRaza(),
                    m.getEdadEstimada() + " (" + m.getSexo() + ")",
                    sanidad.trim().isEmpty() ? "En revisión" : sanidad.trim(),
                    m.getAdoptanteNombre() != null && !m.getAdoptanteNombre().isEmpty() ? m.getAdoptanteNombre() : "Sin postulante",
                    m.getEstado()
            });
        }

        if (!listaFiltrada.isEmpty()) {
            tablaAdopciones.setRowSelectionInterval(0, 0);
            mostrarDetalleEnHub(listaFiltrada.get(0));
        } else {
            mostrarDetalleEnHub(null);
        }
    }

    private void mostrarDetalleEnHub(MascotaAdopcion m) {
        mascotaSeleccionada = m;
        if (panelHubContenido == null) return;

        panelHubContenido.removeAll();
        if (m == null) {
            panelHubContenido.add(panelHubVacio, BorderLayout.CENTER);
            panelHubContenido.revalidate();
            panelHubContenido.repaint();
            return;
        }

        panelHubContenido.add(panelHubDetalle, BorderLayout.CENTER);

        String inicial = !m.getNombre().isEmpty() ? m.getNombre().substring(0, 1).toUpperCase() : "R";
        lblHubAvatar.setText(inicial);
        lblHubNombreMascota.setText(m.getNombre());
        lblHubSubtituloMascota.setText(m.getEspecie() + " · " + m.getRaza() + " · " + m.getIdMascotaAdopcion());

        lblHubBadgeEstado.setText(m.getEstado());
        configurarBadgeEstado(lblHubBadgeEstado, m.getEstado());

        lblHubEspecieSexo.setText(m.getEspecie() + " / " + m.getSexo());
        lblHubEdad.setText(m.getEdadEstimada());
        lblHubDonacion.setText(String.format("S/. %.2f", m.getCuotaDonacion()));
        lblHubTamano.setText(m.getTamano());

        // Chips Sanitarios
        panelHubSanidadBadges.removeAll();
        agregarChipSanitario(panelHubSanidadBadges, "Esterilizado/a", m.isEsterilizado());
        agregarChipSanitario(panelHubSanidadBadges, "Vacunas al Día", m.isVacunado());
        agregarChipSanitario(panelHubSanidadBadges, "Desparasitado/a", m.isDesparasitado());

        lblHubTemperamento.setText(m.getTemperamento().isEmpty() ? "Sociable y cariñoso." : m.getTemperamento());
        txtHubHistoria.setText(m.getHistoriaRescate().isEmpty() ? "Rescatado en buen estado." : m.getHistoriaRescate());

        if (m.getAdoptanteNombre() != null && !m.getAdoptanteNombre().isEmpty()) {
            lblHubAdoptante.setText(m.getAdoptanteNombre());
            lblHubAdoptanteDni.setText(m.getAdoptanteDni().isEmpty() ? "DNI no registrado" : m.getAdoptanteDni());
            lblHubAdoptanteTel.setText(m.getAdoptanteTelefono().isEmpty() ? "Sin celular" : m.getAdoptanteTelefono());
            lblHubAdoptanteDir.setText(m.getAdoptanteDireccion().isEmpty() ? "Dirección no especificada" : m.getAdoptanteDireccion());
        } else {
            lblHubAdoptante.setText("Sin postulante registrado");
            lblHubAdoptanteDni.setText("-");
            lblHubAdoptanteTel.setText("-");
            lblHubAdoptanteDir.setText("-");
        }

        panelHubContenido.revalidate();
        panelHubContenido.repaint();
    }

    private void agregarChipSanitario(JPanel p, String texto, boolean activo) {
        JLabel chip = new JLabel((activo ? "[OK] " : "[-] ") + texto);
        chip.setFont(new Font("Segoe UI", Font.BOLD, 10));
        chip.setOpaque(true);
        if (activo) {
            chip.setBackground(new Color(220, 252, 231));
            chip.setForeground(new Color(22, 163, 74));
            chip.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(187, 247, 208), 1, true),
                    new EmptyBorder(2, 6, 2, 6)
            ));
        } else {
            chip.setBackground(new Color(254, 226, 226));
            chip.setForeground(new Color(220, 38, 38));
            chip.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(254, 202, 202), 1, true),
                    new EmptyBorder(2, 6, 2, 6)
            ));
        }
        p.add(chip);
    }

    private void configurarBadgeEstado(JLabel lbl, String estado) {
        if ("Disponible".equalsIgnoreCase(estado)) {
            lbl.setBackground(Ui.TURQUESA_SUAVE);
            lbl.setForeground(Ui.TURQUESA_PROFUNDO);
        } else if ("En Evaluación".equalsIgnoreCase(estado)) {
            lbl.setBackground(new Color(254, 243, 199));
            lbl.setForeground(new Color(217, 119, 6));
        } else if (estado.contains("Adaptación")) {
            lbl.setBackground(new Color(243, 232, 255));
            lbl.setForeground(new Color(147, 51, 234));
        } else {
            lbl.setBackground(new Color(220, 252, 231));
            lbl.setForeground(new Color(22, 163, 74));
        }
    }

    private void abrirModalNuevoRescatado() {
        FormularioAdopcionDialog dlg = new FormularioAdopcionDialog(javax.swing.SwingUtilities.getWindowAncestor(this));
        dlg.setVisible(true);
        if (dlg.isGuardado()) {
            recargarDatos();
        }
    }

    private void abrirModalEditar() {
        if (mascotaSeleccionada == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un animal para evaluar o editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String[] opciones = {"Editar Ficha / Postulante", "Eliminar del Catálogo", "Cerrar"};
        int sel = JOptionPane.showOptionDialog(this,
                "¿Qué acción desea realizar sobre el rescatado " + mascotaSeleccionada.getNombre() + " (" + mascotaSeleccionada.getIdMascotaAdopcion() + ")?",
                "Gestión de Rescate y Adopción",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                opciones,
                opciones[0]);

        if (sel == 0) {
            FormularioAdopcionDialog dlg = new FormularioAdopcionDialog(javax.swing.SwingUtilities.getWindowAncestor(this), mascotaSeleccionada);
            dlg.setVisible(true);
            if (dlg.isGuardado()) {
                recargarDatos();
            }
        } else if (sel == 1) {
            int conf = JOptionPane.showConfirmDialog(this,
                    "¿Desea eliminar del catálogo a " + mascotaSeleccionada.getNombre() + "?",
                    "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);
            if (conf == JOptionPane.YES_OPTION) {
                repo.eliminarMascotaAdopcion(mascotaSeleccionada.getIdMascotaAdopcion());
                recargarDatos();
            }
        }
    }

    private void pasarAAdaptacion() {
        if (mascotaSeleccionada == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un rescatado para iniciar el período de prueba.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (mascotaSeleccionada.getAdoptanteNombre() == null || mascotaSeleccionada.getAdoptanteNombre().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe registrar un postulante en la ficha antes de pasar a la etapa de adaptación.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        repo.actualizarEstadoAdopcion(mascotaSeleccionada.getIdMascotaAdopcion(), "En Adaptación (Prueba)", null, null, null, null);
        recargarDatos();

        JOptionPane.showMessageDialog(this,
                "¡Período de adaptación de 15 días iniciado con éxito!\n\n" +
                "Mascota: " + mascotaSeleccionada.getNombre() + "\n" +
                "Tutor Temporal: " + mascotaSeleccionada.getAdoptanteNombre() + " (" + mascotaSeleccionada.getAdoptanteTelefono() + ")\n" +
                "Se ha programado una visita y control de seguimiento a los 7 días.",
                "Período de Prueba Iniciado", JOptionPane.INFORMATION_MESSAGE);
    }

    private void finalizarActaAdopcion() {
        if (mascotaSeleccionada == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un adoptado para emitir el Acta de Compromiso Legal.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (mascotaSeleccionada.getAdoptanteNombre() == null || mascotaSeleccionada.getAdoptanteNombre().isEmpty()) {
            JOptionPane.showMessageDialog(this, "No se puede emitir el acta sin un adoptante registrado.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        repo.actualizarEstadoAdopcion(mascotaSeleccionada.getIdMascotaAdopcion(), "Adoptado con Éxito", null, null, null, null);
        recargarDatos();

        String acta = "ACTA OFICIAL DE ADOPCIÓN RESPONSABLE Y COMPROMISO ÉTICO-LEGAL\n" +
                "CLÍNICA VETERINARIA HAPPY PETS · PROGRAMA RESCATE & VIDA\n" +
                "=========================================================================\n\n" +
                "NÚMERO DE EXPEDIENTE: " + mascotaSeleccionada.getIdMascotaAdopcion() + "\n" +
                "FECHA DE ADOPCIÓN   : " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + "\n\n" +
                "DATOS DEL ANIMAL ADOPTADO:\n" +
                "  • Nombre Asignado : " + mascotaSeleccionada.getNombre() + "\n" +
                "  • Especie / Raza  : " + mascotaSeleccionada.getEspecie() + " · " + mascotaSeleccionada.getRaza() + "\n" +
                "  • Edad Estimada   : " + mascotaSeleccionada.getEdadEstimada() + "  |  Sexo: " + mascotaSeleccionada.getSexo() + "\n" +
                "  • Estado Sanitario: " + (mascotaSeleccionada.isEsterilizado() ? "Esterilizado/a (SI)" : "Pendiente") + " | " +
                (mascotaSeleccionada.isVacunado() ? "Vacunado/a (SI)" : "Pendiente") + " | " + (mascotaSeleccionada.isDesparasitado() ? "Desparasitado/a (SI)" : "Pendiente") + "\n\n" +
                "DATOS DEL TUTOR ADOPTANTE:\n" +
                "  • Nombre Completo : " + mascotaSeleccionada.getAdoptanteNombre() + "\n" +
                "  • DNI / Documento : " + mascotaSeleccionada.getAdoptanteDni() + "\n" +
                "  • Teléfono Móvil  : " + mascotaSeleccionada.getAdoptanteTelefono() + "\n" +
                "  • Domicilio       : " + mascotaSeleccionada.getAdoptanteDireccion() + "\n" +
                "  • Aporte al Fondo : S/. " + String.format("%.2f", mascotaSeleccionada.getCuotaDonacion()) + " (Para rescate de otros animales)\n\n" +
                "COMPROMISOS DE TENENCIA RESPONSABLE (LEY 30407):\n" +
                "1. Proporcionar alimento de calidad, agua fresca, abrigo y atención médica preventiva periódica.\n" +
                "2. NO mantener al animal atado, confinado ni en azoteas desprotegidas.\n" +
                "3. En caso fortuito de no poder conservarlo, comunicarlo inmediatamente a Happy Pets sin abandonarlo.\n" +
                "4. Permitir visitas inopinadas de seguimiento durante los primeros 6 meses.\n\n" +
                "__________________________             __________________________\n" +
                "     Firma del Adoptante                    Dirección Médica Happy Pets\n" +
                "  DNI: " + mascotaSeleccionada.getAdoptanteDni() + "                     Programa de Adopciones\n";

        Ui.mostrarVisorReporte(
                javax.swing.SwingUtilities.getWindowAncestor(this),
                "Acta de Adopción Responsable - " + mascotaSeleccionada.getIdMascotaAdopcion(),
                "ACTA OFICIAL DE ADOPCIÓN RESPONSABLE Y COMPROMISO ÉTICO-LEGAL",
                acta,
                "Acta_Adopcion_" + mascotaSeleccionada.getIdMascotaAdopcion()
        );
    }

    private void generarDifusionWhatsapp() {
        if (mascotaSeleccionada == null) {
            JOptionPane.showMessageDialog(this, "Seleccione una mascota para generar el mensaje de difusión.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String mensaje = "HAPPY PETS - CAMPAÑA DE ADOPCIÓN RESPONSABLE\n" +
                "====================================================\n\n" +
                "Mi nombre es *" + mascotaSeleccionada.getNombre() + "* y busco un hogar definitivo lleno de amor.\n\n" +
                "Datos sobre mí:\n" +
                "• Especie / Raza: " + mascotaSeleccionada.getEspecie() + " · " + mascotaSeleccionada.getRaza() + "\n" +
                "• Edad: " + mascotaSeleccionada.getEdadEstimada() + " | Sexo: " + mascotaSeleccionada.getSexo() + "\n" +
                "• Personalidad: " + mascotaSeleccionada.getTemperamento() + "\n" +
                "• Salud: " + (mascotaSeleccionada.isEsterilizado() ? "Esterilizado/a (SI) " : "") + (mascotaSeleccionada.isVacunado() ? "Vacunas al día (SI) " : "") + (mascotaSeleccionada.isDesparasitado() ? "Desparasitado/a (SI)" : "") + "\n\n" +
                "Si quieres darme una oportunidad y abrirme las puertas de tu corazón y tu familia, contáctanos hoy mismo.\n" +
                "Teléfono / WhatsApp Adopciones Happy Pets: +51 984 552 110\n" +
                "Av. Primavera 1230, Santiago de Surco\n" +
                "====================================================\n" +
                "¡Por favor comparte esta publicación y ayúdame a encontrar a mi familia soñada!";

        JTextArea ta = new JTextArea(mensaje);
        ta.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        ta.setEditable(false);
        ta.setLineWrap(true);
        ta.setWrapStyleWord(true);
        JScrollPane sp = new JScrollPane(ta);
        sp.setPreferredSize(new Dimension(460, 260));

        JOptionPane.showMessageDialog(this, sp, "Mensaje de Difusión para Redes / WhatsApp", JOptionPane.INFORMATION_MESSAGE);
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

    private static class PacienteMonogramaRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JPanel p = new JPanel(new BorderLayout(8, 0));
            p.setOpaque(true);
            p.setBackground(isSelected ? table.getSelectionBackground() : (row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252)));
            p.setBorder(new EmptyBorder(0, 8, 0, 8));

            String text = value != null ? value.toString() : "";
            String inicial = !text.isEmpty() ? text.substring(0, 1).toUpperCase() : "R";

            JLabel avatar = new JLabel(inicial, SwingConstants.CENTER) {
                private static final long serialVersionUID = 1L;
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    int s = Math.min(getWidth(), getHeight()) - 2;
                    int x = (getWidth() - s) / 2;
                    int y = (getHeight() - s) / 2;
                    g2.setColor(Ui.TURQUESA_SUAVE);
                    g2.fillOval(x, y, s, s);
                    g2.setColor(Ui.TURQUESA_PROFUNDO);
                    g2.drawOval(x, y, s, s);
                    super.paintComponent(g);
                    g2.dispose();
                }
            };
            avatar.setPreferredSize(new Dimension(28, 28));
            avatar.setFont(new Font("Segoe UI", Font.BOLD, 12));
            avatar.setForeground(Ui.TURQUESA_PROFUNDO);

            JLabel lblNombre = new JLabel(text);
            lblNombre.setFont(new Font("Segoe UI", Font.BOLD, 12));
            lblNombre.setForeground(isSelected ? table.getSelectionForeground() : Ui.TEXTO_TITULO);

            p.add(avatar, BorderLayout.WEST);
            p.add(lblNombre, BorderLayout.CENTER);
            return p;
        }
    }

    private static class BadgeEstadoAdopcionRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            lbl.setHorizontalAlignment(SwingConstants.CENTER);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
            String st = String.valueOf(value);
            if (!isSelected) {
                if ("Disponible".equalsIgnoreCase(st)) {
                    lbl.setForeground(Ui.TURQUESA_PROFUNDO);
                } else if ("En Evaluación".equalsIgnoreCase(st)) {
                    lbl.setForeground(new Color(217, 119, 6));
                } else if (st.contains("Adaptación")) {
                    lbl.setForeground(new Color(147, 51, 234));
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
