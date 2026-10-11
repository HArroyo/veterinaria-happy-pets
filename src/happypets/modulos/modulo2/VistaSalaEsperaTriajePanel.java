package happypets.modulos.modulo2;

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
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.Duration;
import java.time.LocalTime;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import happypets.data.RepositorioVeterinaria;
import happypets.model.Cita;
import happypets.model.PacienteTriaje;
import happypets.ui.Iconos;
import happypets.ui.Ui;

/**
 * Submódulo 2.4: Monitor de Sala de Espera y Triaje Clínico de Urgencias.
 * Arquitectura Master-Detail (Opción A) con monitor de constantes vitales en vivo,
 * escala Manchester visual y modal de admisión.
 */
public class VistaSalaEsperaTriajePanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private static final Color COLOR_AZUL_PRIMARIO = Ui.TURQUESA;
    private static final Color COLOR_BORDE = Ui.BORDE_SUAVE;
    private static final Color COLOR_TEXTO_TITULO = Ui.TEXTO_TITULO;
    private static final Color COLOR_TEXTO_MUTED = Ui.TEXTO_MUTED;
    private static final Color COLOR_BG_PANEL = new Color(248, 250, 252);

    private final RepositorioVeterinaria repo = RepositorioVeterinaria.getInstancia();

    // Filtros
    private JTextField txtBuscar;
    private JComboBox<String> cbFiltroPrioridad;
    private JComboBox<String> cbFiltroEstado;

    // KPIs
    private JLabel lblKpiEnEspera;
    private JLabel lblKpiEnConsulta;
    private JLabel lblKpiTiempoMedio;
    private JLabel lblKpiCriticos;

    // Master Table
    private JTable tablaMonitor;
    private DefaultTableModel modeloMonitor;
    private JLabel lblContadorLista;
    private List<PacienteTriaje> listaActual;

    // Right Detail Hub (Monitor Clínico)
    private JPanel panelDetalleHub;
    private PacienteTriaje pacienteSeleccionado;

    public VistaSalaEsperaTriajePanel() {
        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(new EmptyBorder(12, 16, 14, 16));

        // 1. Cabecera superior
        JPanel panelNorte = new JPanel();
        panelNorte.setOpaque(false);
        panelNorte.setLayout(new BoxLayout(panelNorte, BoxLayout.Y_AXIS));

        panelNorte.add(crearCabeceraVista());
        panelNorte.add(Box.createVerticalStrut(10));
        panelNorte.add(crearFilaKpi());
        panelNorte.add(Box.createVerticalStrut(10));

        add(panelNorte, BorderLayout.NORTH);

        // 2. Split Master-Detail Central (58% Master / 42% Detail)
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, crearPanelMaster(), crearPanelDetailWrapper());
        splitPane.setOpaque(false);
        splitPane.setBorder(null);
        splitPane.setContinuousLayout(true);
        splitPane.setDividerSize(8);
        splitPane.setResizeWeight(0.58);
        splitPane.setDividerLocation(690);

        add(splitPane, BorderLayout.CENTER);

        recargarDatos();
    }

    private JPanel crearCabeceraVista() {
        JPanel cab = new JPanel(new BorderLayout());
        cab.setOpaque(false);
        cab.setPreferredSize(new Dimension(0, 42));
        cab.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

        JPanel izq = new JPanel();
        izq.setOpaque(false);
        izq.setLayout(new BoxLayout(izq, BoxLayout.Y_AXIS));

        JLabel lblTit = new JLabel("Sala de Espera y Triaje de Urgencias");
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTit.setForeground(COLOR_TEXTO_TITULO);

        JLabel lblSub = new JLabel("Módulo 2.4 · Monitor en vivo de pacientes en sala, toma de constantes vitales y clasificación Manchester");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblSub.setForeground(COLOR_TEXTO_MUTED);

        izq.add(lblTit);
        izq.add(Box.createVerticalStrut(2));
        izq.add(lblSub);
        cab.add(izq, BorderLayout.CENTER);

        JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        der.setOpaque(false);

        JButton btnRefrescar = crearBotonAccion("Actualizar", false);
        btnRefrescar.setIcon(Iconos.crearIconoRefrescar(13, COLOR_AZUL_PRIMARIO));
        btnRefrescar.setIconTextGap(5);
        btnRefrescar.addActionListener(e -> recargarDatos());

        JButton btnLlamarSiguiente = crearBotonAccion("Llamar Siguiente Turno", false);
        btnLlamarSiguiente.setIcon(Iconos.crearIconoReloj(13, new Color(217, 119, 6)));
        btnLlamarSiguiente.setIconTextGap(5);
        btnLlamarSiguiente.addActionListener(e -> llamarSiguienteTurno());

        JButton btnNuevoIngreso = crearBotonAccion("+ Ingresar Paciente a Triaje", true);
        btnNuevoIngreso.setIcon(Iconos.crearIconoAlertaTriaje(13, Color.WHITE));
        btnNuevoIngreso.setIconTextGap(5);
        btnNuevoIngreso.addActionListener(e -> abrirModalIngreso(null, null));

        der.add(btnRefrescar);
        der.add(btnLlamarSiguiente);
        der.add(btnNuevoIngreso);
        cab.add(der, BorderLayout.EAST);

        return cab;
    }

    private JPanel crearFilaKpi() {
        JPanel fila = new JPanel(new GridLayout(1, 4, 10, 0));
        fila.setOpaque(false);
        fila.setPreferredSize(new Dimension(0, 52));
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));

        lblKpiEnEspera = new JLabel("0");
        fila.add(crearCardKpi(lblKpiEnEspera, "PACIENTES EN ESPERA", new Color(254, 243, 199), Iconos.crearIconoReloj(20, new Color(217, 119, 6))));

        lblKpiEnConsulta = new JLabel("0");
        fila.add(crearCardKpi(lblKpiEnConsulta, "EN CONSULTA / TÓPICO", Ui.TURQUESA_SUAVE, Iconos.crearIconoEstetoscopio(20, COLOR_AZUL_PRIMARIO)));

        lblKpiTiempoMedio = new JLabel("11 min");
        fila.add(crearCardKpi(lblKpiTiempoMedio, "TIEMPO PROMEDIO ESPERA", new Color(220, 252, 231), Iconos.crearIconoCheck(20, new Color(22, 163, 74))));

        lblKpiCriticos = new JLabel("0");
        fila.add(crearCardKpi(lblKpiCriticos, "CASOS CRÍTICOS (ROJO)", new Color(254, 226, 226), Iconos.crearIconoAlertaTriaje(20, new Color(220, 38, 38))));

        return fila;
    }

    private JPanel crearCardKpi(JLabel lblVal, String etiqueta, Color bgIco, Icon icono) {
        JPanel card = new JPanel(new BorderLayout(10, 0)) {
            private static final long serialVersionUID = 1L;
            @Override
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
    // PANEL MASTER (TABLA DE TRIAJE Y MONITOR)
    // ==========================================
    private JPanel crearPanelMaster() {
        JPanel card = new JPanel(new BorderLayout(0, 8)) {
            private static final long serialVersionUID = 1L;
            @Override
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

        // Barra superior de filtros
        JPanel topFiltros = new JPanel(new BorderLayout(8, 0));
        topFiltros.setOpaque(false);

        JPanel izqFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        izqFiltros.setOpaque(false);

        JLabel lblPr = new JLabel("Prioridad:");
        lblPr.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblPr.setForeground(COLOR_TEXTO_MUTED);
        izqFiltros.add(lblPr);

        cbFiltroPrioridad = new JComboBox<>(new String[]{"Todas las Prioridades", "ROJO (Emergencia)", "AMARILLO (Urgencia)", "VERDE (Normal)"});
        cbFiltroPrioridad.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        cbFiltroPrioridad.setBackground(Color.WHITE);
        cbFiltroPrioridad.setPreferredSize(new Dimension(150, 28));
        cbFiltroPrioridad.addActionListener(e -> filtrarDatos());
        izqFiltros.add(cbFiltroPrioridad);

        JLabel lblEs = new JLabel("Estado:");
        lblEs.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblEs.setForeground(COLOR_TEXTO_MUTED);
        izqFiltros.add(lblEs);

        cbFiltroEstado = new JComboBox<>(new String[]{"Todos en Sala", "En Espera", "En Consulta", "Atendidos Hoy"});
        cbFiltroEstado.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        cbFiltroEstado.setBackground(Color.WHITE);
        cbFiltroEstado.setPreferredSize(new Dimension(120, 28));
        cbFiltroEstado.addActionListener(e -> filtrarDatos());
        izqFiltros.add(cbFiltroEstado);

        topFiltros.add(izqFiltros, BorderLayout.WEST);

        JPanel derFiltros = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        derFiltros.setOpaque(false);

        txtBuscar = new JTextField(12);
        txtBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        txtBuscar.setPreferredSize(new Dimension(140, 28));
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

        // Tabla Master Monitor
        String[] columnas = {"Ticket", "Paciente / Especie", "Tutor / Teléfono", "Triaje Manchester", "Destino", "Estado"};
        modeloMonitor = new DefaultTableModel(columnas, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        tablaMonitor = new JTable(modeloMonitor);
        tablaMonitor.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        Ui.formatearTabla(tablaMonitor, new int[]{0, 3, 4, 5}, new int[]{});
        tablaMonitor.setRowHeight(48);
        tablaMonitor.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        tablaMonitor.getColumnModel().getColumn(0).setPreferredWidth(75);
        tablaMonitor.getColumnModel().getColumn(1).setPreferredWidth(170);
        tablaMonitor.getColumnModel().getColumn(2).setPreferredWidth(150);
        tablaMonitor.getColumnModel().getColumn(3).setPreferredWidth(130);
        tablaMonitor.getColumnModel().getColumn(4).setPreferredWidth(110);
        tablaMonitor.getColumnModel().getColumn(5).setPreferredWidth(95);

        tablaMonitor.getColumnModel().getColumn(0).setCellRenderer(new TicketCellRenderer());
        tablaMonitor.getColumnModel().getColumn(1).setCellRenderer(new PacienteCellRenderer());
        tablaMonitor.getColumnModel().getColumn(2).setCellRenderer(new TutorCellRenderer());
        tablaMonitor.getColumnModel().getColumn(3).setCellRenderer(new BadgeTriajeCellRenderer());
        tablaMonitor.getColumnModel().getColumn(5).setCellRenderer(new BadgeEstadoTriajeRenderer());

        tablaMonitor.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = tablaMonitor.getSelectedRow();
                if (row >= 0 && row < listaActual.size()) {
                    pacienteSeleccionado = listaActual.get(row);
                } else {
                    pacienteSeleccionado = null;
                }
                actualizarPanelDetalle();
            }
        });

        tablaMonitor.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && pacienteSeleccionado != null) {
                    abrirModalIngreso(pacienteSeleccionado, null);
                }
            }
        });

        JScrollPane sp = new JScrollPane(tablaMonitor);
        sp.setBorder(BorderFactory.createLineBorder(COLOR_BORDE, 1));
        sp.getViewport().setBackground(Color.WHITE);
        card.add(sp, BorderLayout.CENTER);

        // Barra inferior del Master
        JPanel botMaster = new JPanel(new BorderLayout());
        botMaster.setOpaque(false);

        lblContadorLista = new JLabel("0 pacientes en sala de espera");
        lblContadorLista.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblContadorLista.setForeground(COLOR_TEXTO_MUTED);
        botMaster.add(lblContadorLista, BorderLayout.WEST);

        JLabel lblDobleClic = new JLabel("Doble clic en un paciente para editar constantes vitales");
        lblDobleClic.setFont(new Font("Segoe UI", Font.ITALIC, 10));
        lblDobleClic.setForeground(COLOR_TEXTO_MUTED);
        botMaster.add(lblDobleClic, BorderLayout.EAST);

        card.add(botMaster, BorderLayout.SOUTH);

        return card;
    }

    // ==========================================
    // PANEL DETAIL (CENTRO DE MANDO DE TRIAJE)
    // ==========================================
    private JPanel crearPanelDetailWrapper() {
        panelDetalleHub = new JPanel(new BorderLayout());
        panelDetalleHub.setOpaque(false);
        actualizarPanelDetalle();
        return panelDetalleHub;
    }

    private void actualizarPanelDetalle() {
        panelDetalleHub.removeAll();

        if (pacienteSeleccionado == null) {
            panelDetalleHub.add(crearEstadoVacioDetalle(), BorderLayout.CENTER);
        } else {
            panelDetalleHub.add(crearCardDetallePaciente(pacienteSeleccionado), BorderLayout.CENTER);
        }

        panelDetalleHub.revalidate();
        panelDetalleHub.repaint();
    }

    private JPanel crearEstadoVacioDetalle() {
        JPanel card = new JPanel(new BorderLayout()) {
            private static final long serialVersionUID = 1L;
            @Override
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

        JPanel centro = new JPanel();
        centro.setOpaque(false);
        centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));

        JLabel ico = new JLabel(Iconos.crearIconoAlertaTriaje(48, new Color(148, 163, 184)));
        ico.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel tit = new JLabel("Monitor Clínico de Triaje");
        tit.setFont(new Font("Segoe UI", Font.BOLD, 15));
        tit.setForeground(COLOR_TEXTO_TITULO);
        tit.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel msg = new JLabel("<html><center>Seleccione un paciente en la lista de espera para supervisar sus signos vitales, nivel de urgencia Manchester y ejecutar acciones clínicas rápidas.</center></html>");
        msg.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        msg.setForeground(COLOR_TEXTO_MUTED);
        msg.setAlignmentX(Component.CENTER_ALIGNMENT);
        msg.setMaximumSize(new Dimension(320, 70));

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

    private JPanel crearCardDetallePaciente(PacienteTriaje p) {
        JPanel card = new JPanel(new BorderLayout(0, 10)) {
            private static final long serialVersionUID = 1L;
            @Override
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
        card.setBorder(new EmptyBorder(14, 16, 14, 16));

        // Determinar colores Manchester
        String nt = p.getNivelTriaje().toUpperCase();
        Color colorTriaje;
        Color bgTriaje;
        String descTriaje;
        if (nt.contains("ROJO")) {
            colorTriaje = new Color(220, 38, 38);
            bgTriaje = new Color(254, 242, 242);
            descTriaje = "PRIORIDAD ROJA · EMERGENCIA CRÍTICA";
        } else if (nt.contains("AMARILLO")) {
            colorTriaje = new Color(217, 119, 6);
            bgTriaje = new Color(254, 252, 232);
            descTriaje = "PRIORIDAD AMARILLA · URGENCIA (< 30 MIN)";
        } else {
            colorTriaje = new Color(22, 163, 74);
            bgTriaje = new Color(240, 253, 244);
            descTriaje = "PRIORIDAD VERDE · NORMAL / NO URGENTE";
        }

        // 1. Header con Avatar Circular, Datos y Banner Manchester
        JPanel pTop = new JPanel();
        pTop.setOpaque(false);
        pTop.setLayout(new BoxLayout(pTop, BoxLayout.Y_AXIS));

        JPanel pFilaAvatar = new JPanel(new BorderLayout(12, 0));
        pFilaAvatar.setOpaque(false);

        // Circular Avatar seguro
        JPanel pAvatarWrap = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        pAvatarWrap.setOpaque(false);
        JLabel lblAvatar = new JLabel() {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth();
                int h = getHeight();
                int d = Math.min(w, h) - 2;
                int x = (w - d) / 2;
                int y = (h - d) / 2;

                g2.setColor(bgTriaje);
                g2.fillOval(x, y, d, d);
                g2.setColor(colorTriaje);
                g2.setStroke(new java.awt.BasicStroke(2.0f));
                g2.drawOval(x, y, d, d);

                // Letras iniciales
                String ini = p.getNombreMascota().substring(0, Math.min(2, p.getNombreMascota().length())).toUpperCase();
                g2.setFont(new Font("Segoe UI", Font.BOLD, 18));
                g2.setColor(colorTriaje);
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

        // Datos del paciente al lado del avatar
        JPanel pInfoPaciente = new JPanel();
        pInfoPaciente.setOpaque(false);
        pInfoPaciente.setLayout(new BoxLayout(pInfoPaciente, BoxLayout.Y_AXIS));

        JLabel lblNom = new JLabel(p.getNombreMascota() + "  (" + p.getIdTicket() + ")");
        lblNom.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblNom.setForeground(COLOR_TEXTO_TITULO);

        JLabel lblEsp = new JLabel(p.getEspecieRaza() + " · Tutor: " + p.getNombrePropietario());
        lblEsp.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblEsp.setForeground(COLOR_TEXTO_MUTED);

        JLabel lblTel = new JLabel("Teléfono: " + p.getTelefonoPropietario() + "  |  Llegada: " + p.getHoraLlegadaFormateada() + " hrs");
        lblTel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblTel.setForeground(COLOR_TEXTO_MUTED);

        pInfoPaciente.add(lblNom);
        pInfoPaciente.add(Box.createVerticalStrut(2));
        pInfoPaciente.add(lblEsp);
        pInfoPaciente.add(Box.createVerticalStrut(2));
        pInfoPaciente.add(lblTel);
        pFilaAvatar.add(pInfoPaciente, BorderLayout.CENTER);

        pTop.add(pFilaAvatar);
        pTop.add(Box.createVerticalStrut(10));

        // Banner Manchester Triage
        JPanel bannerManchester = new JPanel(new BorderLayout(8, 0)) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bgTriaje);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(colorTriaje);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        bannerManchester.setOpaque(false);
        bannerManchester.setBorder(new EmptyBorder(8, 12, 8, 12));

        JLabel lblManchester = new JLabel(descTriaje);
        lblManchester.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblManchester.setForeground(colorTriaje);
        lblManchester.setIcon(Iconos.crearIconoAlertaTriaje(16, colorTriaje));
        lblManchester.setIconTextGap(8);
        bannerManchester.add(lblManchester, BorderLayout.WEST);

        JLabel lblEstadoPill = new JLabel("Estado: " + p.getEstado());
        lblEstadoPill.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblEstadoPill.setForeground(colorTriaje);
        bannerManchester.add(lblEstadoPill, BorderLayout.EAST);

        pTop.add(bannerManchester);
        card.add(pTop, BorderLayout.NORTH);

        // 2. Centro: Constantes vitales + Motivo clínico
        JPanel pCentro = new JPanel();
        pCentro.setOpaque(false);
        pCentro.setLayout(new BoxLayout(pCentro, BoxLayout.Y_AXIS));

        // Grid de 4 constantes vitales
        JPanel gridConstantes = new JPanel(new GridLayout(2, 2, 8, 8));
        gridConstantes.setOpaque(false);

        // Temperatura
        String evalTemp = (p.getTemperaturaC() > 39.2) ? "Febril" : (p.getTemperaturaC() < 38.0) ? "Hipotermia" : "Normal";
        Color colTemp = evalTemp.equals("Normal") ? new Color(22, 163, 74) : new Color(220, 38, 38);
        gridConstantes.add(crearWidgetSignoVital("TEMPERATURA CORPORAL", p.getTemperaturaC() + " °C", evalTemp, colTemp));

        // Frecuencia Cardíaca
        String evalFc = (p.getFrecuenciaCardiacaPpm() > 140) ? "Taquicardia" : (p.getFrecuenciaCardiacaPpm() < 60) ? "Bradicardia" : "Normal";
        Color colFc = evalFc.equals("Normal") ? new Color(22, 163, 74) : new Color(217, 119, 6);
        gridConstantes.add(crearWidgetSignoVital("FRECUENCIA CARDÍACA", p.getFrecuenciaCardiacaPpm() + " ppm", evalFc, colFc));

        // Peso Corporal
        gridConstantes.add(crearWidgetSignoVital("PESO DEL PACIENTE", p.getPesoKg() + " kg", "Biometría", COLOR_AZUL_PRIMARIO));

        // Tiempo en Sala
        long minutosEspera = 0;
        if (p.getHoraLlegada() != null) {
            minutosEspera = Math.max(0, Duration.between(p.getHoraLlegada(), LocalTime.now()).toMinutes());
        }
        gridConstantes.add(crearWidgetSignoVital("TIEMPO EN SALA", minutosEspera + " minutos", "Llegó " + p.getHoraLlegadaFormateada(), new Color(100, 116, 139)));

        pCentro.add(gridConstantes);
        pCentro.add(Box.createVerticalStrut(10));

        // Motivo y Destino
        JPanel pMotivo = new JPanel(new BorderLayout(0, 4)) {
            private static final long serialVersionUID = 1L;
            @Override
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
        pMotivo.setOpaque(false);
        pMotivo.setBorder(new EmptyBorder(10, 12, 10, 12));

        JLabel lblTitM = new JLabel("SIGNOS CLÍNICOS / MOTIVO DE CONSULTA");
        lblTitM.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblTitM.setForeground(COLOR_TEXTO_MUTED);

        JLabel lblDescM = new JLabel("<html>" + p.getMotivo() + "</html>");
        lblDescM.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblDescM.setForeground(COLOR_TEXTO_TITULO);

        JLabel lblDestino = new JLabel("Consultorio Destino: " + p.getConsultorioAsignado());
        lblDestino.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblDestino.setForeground(COLOR_AZUL_PRIMARIO);

        pMotivo.add(lblTitM, BorderLayout.NORTH);
        pMotivo.add(lblDescM, BorderLayout.CENTER);
        pMotivo.add(lblDestino, BorderLayout.SOUTH);

        pCentro.add(pMotivo);
        card.add(pCentro, BorderLayout.CENTER);

        // 3. Footer: Dock de Acciones Rápidas (1-click triage operations)
        JPanel pBotones = new JPanel(new GridLayout(2, 2, 8, 8));
        pBotones.setOpaque(false);

        JButton btnLlamar = crearBotonAccion("Llamar a Consultorio", true);
        btnLlamar.setIcon(Iconos.crearIconoEstetoscopio(13, Color.WHITE));
        btnLlamar.setIconTextGap(6);
        btnLlamar.addActionListener(e -> pasarAConsulta(p));

        JButton btnReclasificar = crearBotonAccion("Reclasificar Triaje", false);
        btnReclasificar.setIcon(Iconos.crearIconoAlertaTriaje(13, COLOR_AZUL_PRIMARIO));
        btnReclasificar.setIconTextGap(6);
        btnReclasificar.addActionListener(e -> reclasificarPrioridad(p));

        JButton btnAtendido = crearBotonAccion("Finalizar Atención", false);
        btnAtendido.setIcon(Iconos.crearIconoCheck(13, new Color(22, 163, 74)));
        btnAtendido.setIconTextGap(6);
        btnAtendido.addActionListener(e -> finalizarAtencion(p));

        JButton btnEditar = crearBotonAccion("Editar Constantes", false);
        btnEditar.setIcon(Iconos.crearIconoEditar(13, new Color(100, 116, 139)));
        btnEditar.setIconTextGap(6);
        btnEditar.addActionListener(e -> abrirModalIngreso(p, null));

        pBotones.add(btnLlamar);
        pBotones.add(btnReclasificar);
        pBotones.add(btnAtendido);
        pBotones.add(btnEditar);

        card.add(pBotones, BorderLayout.SOUTH);

        return card;
    }

    private JPanel crearWidgetSignoVital(String titulo, String valor, String status, Color colorStatus) {
        JPanel w = new JPanel(new BorderLayout(4, 2)) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(250, 250, 250));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(COLOR_BORDE);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        w.setOpaque(false);
        w.setBorder(new EmptyBorder(6, 10, 6, 10));

        JLabel lblT = new JLabel(titulo);
        lblT.setFont(new Font("Segoe UI", Font.BOLD, 9));
        lblT.setForeground(COLOR_TEXTO_MUTED);

        JLabel lblV = new JLabel(valor);
        lblV.setFont(new Font("Segoe UI", Font.BOLD, 14));
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
    // OPERACIONES Y LÓGICA DE NEGOCIO
    // ==========================================
    public void registrarLlegadaDesdeCita(Cita cita) {
        if (cita == null) return;
        recargarDatos();

        // Buscar si ya está en triaje
        for (PacienteTriaje p : listaActual) {
            if (cita.getIdCita().equalsIgnoreCase(p.getIdCita())) {
                seleccionarPaciente(p);
                return;
            }
        }

        // Si no está en triaje, abrir diálogo para admisión directa
        abrirModalIngreso(null, cita);
    }

    private void abrirModalIngreso(PacienteTriaje pExistente, Cita citaPrevia) {
        Window parent = SwingUtilities.getWindowAncestor(this);
        FormularioIngresoTriajeDialog dlg = new FormularioIngresoTriajeDialog(parent, pExistente, citaPrevia, guardado -> {
            recargarDatos();
            seleccionarPaciente(guardado);
            JOptionPane.showMessageDialog(this,
                    "Paciente " + guardado.getNombreMascota() + " registrado con ticket " + guardado.getIdTicket() + ".\n" +
                    "Nivel de Triaje: " + guardado.getNivelTriaje() + "\n" +
                    "Destino: " + guardado.getConsultorioAsignado(),
                    "Ficha de Triaje Registrada", JOptionPane.INFORMATION_MESSAGE);
        });
        dlg.setVisible(true);
    }

    private void pasarAConsulta(PacienteTriaje p) {
        p.setEstado("En Consulta");
        repo.actualizarEstadoTriaje(p.getIdTicket(), "En Consulta");
        recargarDatos();
        seleccionarPaciente(p);
        JOptionPane.showMessageDialog(this,
                "LLAMADO A CONSULTA:\n\n" +
                "Ticket: " + p.getIdTicket() + "\n" +
                "Paciente: " + p.getNombreMascota() + " (" + p.getEspecieRaza() + ")\n" +
                "Propietario: " + p.getNombrePropietario() + "\n" +
                "Destino: " + p.getConsultorioAsignado() + "\n\n" +
                "El paciente ha ingresado a atención clínica.",
                "Llamado a Consulta", JOptionPane.INFORMATION_MESSAGE);
    }

    private void reclasificarPrioridad(PacienteTriaje p) {
        String[] opciones = {"VERDE (Normal / Rutinario)", "AMARILLO (Urgencia < 30 min)", "ROJO (Emergencia Crítica)"};
        int sel = JOptionPane.showOptionDialog(
                this,
                "Seleccione el nuevo nivel de clasificación Manchester para " + p.getNombreMascota() + ":",
                "Reclasificación de Triaje - " + p.getIdTicket(),
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                Iconos.crearIconoAlertaTriaje(24, COLOR_AZUL_PRIMARIO),
                opciones,
                opciones[0]
        );

        if (sel >= 0 && sel < opciones.length) {
            String nuevoNivel = opciones[sel];
            p.setNivelTriaje(nuevoNivel);
            repo.guardarPacienteTriaje(p);
            recargarDatos();
            seleccionarPaciente(p);
            JOptionPane.showMessageDialog(this,
                    "Prioridad actualizada para " + p.getNombreMascota() + ":\n" + nuevoNivel,
                    "Prioridad Modificada", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void finalizarAtencion(PacienteTriaje p) {
        p.setEstado("Atendido");
        repo.actualizarEstadoTriaje(p.getIdTicket(), "Atendido");
        recargarDatos();
        seleccionarPaciente(p);
        JOptionPane.showMessageDialog(this,
                "Atención clínica finalizada para el paciente " + p.getNombreMascota() + ".",
                "Atención Concluida", JOptionPane.INFORMATION_MESSAGE);
    }

    private void llamarSiguienteTurno() {
        List<PacienteTriaje> enEspera = repo.getPacientesTriaje().stream()
                .filter(p -> "En Espera".equalsIgnoreCase(p.getEstado()))
                .sorted((a, b) -> {
                    // Orden de prioridad Manchester: ROJO primero, luego AMARILLO, luego VERDE
                    int pa = a.getNivelTriaje().toUpperCase().contains("ROJO") ? 0 : a.getNivelTriaje().toUpperCase().contains("AMARILLO") ? 1 : 2;
                    int pb = b.getNivelTriaje().toUpperCase().contains("ROJO") ? 0 : b.getNivelTriaje().toUpperCase().contains("AMARILLO") ? 1 : 2;
                    if (pa != pb) return Integer.compare(pa, pb);
                    if (a.getHoraLlegada() == null) return 1;
                    if (b.getHoraLlegada() == null) return -1;
                    return a.getHoraLlegada().compareTo(b.getHoraLlegada());
                }).toList();

        if (enEspera.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No hay pacientes pendientes en la sala de espera actualmente.",
                    "Sala de Espera Despejada", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        PacienteTriaje sig = enEspera.get(0);
        pasarAConsulta(sig);
    }

    public void recargarDatos() {
        filtrarDatos();
    }

    private void filtrarDatos() {
        List<PacienteTriaje> todos = repo.getPacientesTriaje();
        String fPrio = cbFiltroPrioridad != null ? (String) cbFiltroPrioridad.getSelectedItem() : "Todas";
        String fEst = cbFiltroEstado != null ? (String) cbFiltroEstado.getSelectedItem() : "Todos";
        String q = (txtBuscar != null && txtBuscar.getText() != null) ? txtBuscar.getText().trim().toLowerCase() : "";

        listaActual = todos.stream().filter(p -> {
            if (fPrio != null && !fPrio.startsWith("Todas")) {
                if (fPrio.startsWith("ROJO") && !p.getNivelTriaje().toUpperCase().contains("ROJO")) return false;
                if (fPrio.startsWith("AMARILLO") && !p.getNivelTriaje().toUpperCase().contains("AMARILLO")) return false;
                if (fPrio.startsWith("VERDE") && !p.getNivelTriaje().toUpperCase().contains("VERDE")) return false;
            }
            if (fEst != null && !fEst.startsWith("Todos")) {
                if (fEst.startsWith("Atendidos") && !"Atendido".equalsIgnoreCase(p.getEstado())) return false;
                if (!fEst.startsWith("Atendidos") && !p.getEstado().equalsIgnoreCase(fEst)) return false;
            }
            if (!q.isEmpty()) {
                boolean match = p.getNombreMascota().toLowerCase().contains(q) ||
                                p.getNombrePropietario().toLowerCase().contains(q) ||
                                p.getIdTicket().toLowerCase().contains(q) ||
                                p.getMotivo().toLowerCase().contains(q) ||
                                p.getConsultorioAsignado().toLowerCase().contains(q);
                if (!match) return false;
            }
            return true;
        }).toList();

        modeloMonitor.setRowCount(0);
        for (PacienteTriaje p : listaActual) {
            modeloMonitor.addRow(new Object[]{
                    p,
                    p,
                    p,
                    p.getNivelTriaje(),
                    p.getConsultorioAsignado(),
                    p.getEstado()
            });
        }

        lblContadorLista.setText(listaActual.size() + " pacientes monitoreados");

        // KPIs
        long enEspera = todos.stream().filter(p -> "En Espera".equalsIgnoreCase(p.getEstado())).count();
        long enConsulta = todos.stream().filter(p -> "En Consulta".equalsIgnoreCase(p.getEstado())).count();
        long criticos = todos.stream().filter(p -> p.getNivelTriaje().toUpperCase().contains("ROJO")).count();

        lblKpiEnEspera.setText(String.valueOf(enEspera));
        lblKpiEnConsulta.setText(String.valueOf(enConsulta));
        lblKpiCriticos.setText(String.valueOf(criticos));

        // Seleccionar paciente previo si aún existe en la lista filtrada
        if (pacienteSeleccionado != null) {
            seleccionarPaciente(pacienteSeleccionado);
        } else if (!listaActual.isEmpty()) {
            seleccionarFila(0);
        } else {
            pacienteSeleccionado = null;
            actualizarPanelDetalle();
        }
    }

    private void seleccionarPaciente(PacienteTriaje p) {
        if (p == null) return;
        for (int i = 0; i < listaActual.size(); i++) {
            if (listaActual.get(i).getIdTicket().equalsIgnoreCase(p.getIdTicket())) {
                seleccionarFila(i);
                return;
            }
        }
        actualizarPanelDetalle();
    }

    private void seleccionarFila(int fila) {
        if (fila >= 0 && fila < tablaMonitor.getRowCount()) {
            tablaMonitor.setRowSelectionInterval(fila, fila);
            tablaMonitor.scrollRectToVisible(tablaMonitor.getCellRect(fila, 0, true));
            pacienteSeleccionado = listaActual.get(fila);
        } else {
            pacienteSeleccionado = null;
        }
        actualizarPanelDetalle();
    }

    private JButton crearBotonAccion(String texto, boolean primario) {
        JButton btn = new JButton(texto) {
            private static final long serialVersionUID = 1L;
            @Override
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
    // RENDERIZADORES DE LA TABLA MASTER
    // ==========================================
    private static class TicketCellRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JPanel p = new JPanel(new BorderLayout(0, 2));
            p.setOpaque(true);
            p.setBackground(isSelected ? table.getSelectionBackground() : (row % 2 == 0 ? Color.WHITE : new Color(249, 250, 251)));
            p.setBorder(new EmptyBorder(6, 8, 6, 6));

            if (value instanceof PacienteTriaje pt) {
                JLabel lblTicket = new JLabel(pt.getIdTicket());
                lblTicket.setFont(new Font("Segoe UI", Font.BOLD, 12));
                lblTicket.setForeground(isSelected ? table.getSelectionForeground() : COLOR_AZUL_PRIMARIO);

                JLabel lblHora = new JLabel(pt.getHoraLlegadaFormateada() + " hrs");
                lblHora.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                lblHora.setForeground(isSelected ? table.getSelectionForeground() : COLOR_TEXTO_MUTED);

                p.add(lblTicket, BorderLayout.NORTH);
                p.add(lblHora, BorderLayout.CENTER);
            }
            return p;
        }
    }

    private static class PacienteCellRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JPanel p = new JPanel(new BorderLayout(8, 0));
            p.setOpaque(true);
            p.setBackground(isSelected ? table.getSelectionBackground() : (row % 2 == 0 ? Color.WHITE : new Color(249, 250, 251)));
            p.setBorder(new EmptyBorder(6, 6, 6, 6));

            if (value instanceof PacienteTriaje pt) {
                // Avatar circular miniatura
                JPanel pAvWrap = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 2));
                pAvWrap.setOpaque(false);
                JLabel lblAv = new JLabel() {
                    private static final long serialVersionUID = 1L;
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        int w = getWidth();
                        int h = getHeight();
                        int d = Math.min(w, h) - 2;
                        g2.setColor(new Color(224, 242, 254));
                        g2.fillOval(1, 1, d, d);
                        g2.setColor(new Color(2, 132, 199));
                        g2.drawOval(1, 1, d, d);
                        String ini = pt.getNombreMascota().substring(0, 1).toUpperCase();
                        g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
                        java.awt.FontMetrics fm = g2.getFontMetrics();
                        g2.drawString(ini, (w - fm.stringWidth(ini)) / 2, (h + fm.getAscent() - fm.getDescent()) / 2);
                        g2.dispose();
                    }
                };
                lblAv.setPreferredSize(new Dimension(28, 28));
                pAvWrap.add(lblAv);
                p.add(pAvWrap, BorderLayout.WEST);

                JPanel pTxt = new JPanel();
                pTxt.setOpaque(false);
                pTxt.setLayout(new BoxLayout(pTxt, BoxLayout.Y_AXIS));

                JLabel lblNom = new JLabel(pt.getNombreMascota());
                lblNom.setFont(new Font("Segoe UI", Font.BOLD, 12));
                lblNom.setForeground(isSelected ? table.getSelectionForeground() : COLOR_TEXTO_TITULO);

                JLabel lblEsp = new JLabel(pt.getEspecieRaza());
                lblEsp.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                lblEsp.setForeground(isSelected ? table.getSelectionForeground() : COLOR_TEXTO_MUTED);

                pTxt.add(lblNom);
                pTxt.add(lblEsp);
                p.add(pTxt, BorderLayout.CENTER);
            }
            return p;
        }
    }

    private static class TutorCellRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JPanel p = new JPanel(new BorderLayout(0, 2));
            p.setOpaque(true);
            p.setBackground(isSelected ? table.getSelectionBackground() : (row % 2 == 0 ? Color.WHITE : new Color(249, 250, 251)));
            p.setBorder(new EmptyBorder(6, 6, 6, 6));

            if (value instanceof PacienteTriaje pt) {
                JLabel lblTutor = new JLabel(pt.getNombrePropietario());
                lblTutor.setFont(new Font("Segoe UI", Font.BOLD, 11));
                lblTutor.setForeground(isSelected ? table.getSelectionForeground() : COLOR_TEXTO_TITULO);

                JLabel lblTel = new JLabel(pt.getTelefonoPropietario());
                lblTel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                lblTel.setForeground(isSelected ? table.getSelectionForeground() : COLOR_TEXTO_MUTED);

                p.add(lblTutor, BorderLayout.NORTH);
                p.add(lblTel, BorderLayout.CENTER);
            }
            return p;
        }
    }

    private static class BadgeTriajeCellRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 8));
            p.setOpaque(true);
            p.setBackground(isSelected ? table.getSelectionBackground() : (row % 2 == 0 ? Color.WHITE : new Color(249, 250, 251)));

            String text = String.valueOf(value);
            String nt = text.toUpperCase();

            Color bg;
            Color fg;
            String label;
            if (nt.contains("ROJO")) {
                bg = new Color(254, 226, 226);
                fg = new Color(185, 28, 28);
                label = "ROJO (Crítico)";
            } else if (nt.contains("AMARILLO")) {
                bg = new Color(254, 243, 199);
                fg = new Color(180, 83, 9);
                label = "AMARILLO (Urgente)";
            } else {
                bg = new Color(220, 252, 231);
                fg = new Color(21, 128, 61);
                label = "VERDE (Normal)";
            }

            JLabel badge = new JLabel(label, SwingConstants.CENTER) {
                private static final long serialVersionUID = 1L;
                @Override
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
            badge.setBorder(new EmptyBorder(3, 10, 3, 10));

            p.add(badge);
            return p;
        }
    }

    private static class BadgeEstadoTriajeRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 8));
            p.setOpaque(true);
            p.setBackground(isSelected ? table.getSelectionBackground() : (row % 2 == 0 ? Color.WHITE : new Color(249, 250, 251)));

            String st = String.valueOf(value);
            Color bg;
            Color fg;
            if ("En Consulta".equalsIgnoreCase(st)) {
                bg = Ui.TURQUESA_SUAVE;
                fg = new Color(15, 118, 110);
            } else if ("Atendido".equalsIgnoreCase(st)) {
                bg = new Color(241, 245, 249);
                fg = new Color(100, 116, 139);
            } else {
                bg = new Color(254, 243, 199);
                fg = new Color(180, 83, 9);
            }

            JLabel badge = new JLabel(st, SwingConstants.CENTER) {
                private static final long serialVersionUID = 1L;
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(bg);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                    super.paintComponent(g2);
                    g2.dispose();
                }
            };
            badge.setOpaque(false);
            badge.setFont(new Font("Segoe UI", Font.BOLD, 10));
            badge.setForeground(fg);
            badge.setBorder(new EmptyBorder(3, 10, 3, 10));

            p.add(badge);
            return p;
        }
    }
}
