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
import happypets.model.AtencionMedica;
import happypets.model.Cliente;
import happypets.model.Mascota;
import happypets.ui.Iconos;
import happypets.ui.Ui;

/**
 * Submódulo 3.1: Consultas Médicas y Atenciones Veterinarias.
 * Arquitectura Master-Detail (Opción A) con monitor de constantes vitales en vivo,
 * expediente clínico evolutivo y visor oficial de recetas médicas.
 */
public class VistaConsultasMedicasPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private static final Color COLOR_AZUL_PRIMARIO = Ui.TURQUESA;
    private static final Color COLOR_BORDE = Ui.BORDE_SUAVE;
    private static final Color COLOR_TEXTO_TITULO = Ui.TEXTO_TITULO;
    private static final Color COLOR_TEXTO_MUTED = Ui.TEXTO_MUTED;

    private final RepositorioVeterinaria repo = RepositorioVeterinaria.getInstancia();

    // Filtros
    private JTextField txtBuscar;
    private JComboBox<String> cbFiltroEstado;
    private JComboBox<String> cbFiltroVeterinario;

    // KPIs
    private JLabel lblKpiConsultasHoy;
    private JLabel lblKpiUrgencias;
    private JLabel lblKpiRecetas;
    private JLabel lblKpiEnSeguimiento;

    // Master Table
    private JTable tablaConsultas;
    private DefaultTableModel modeloConsultas;
    private JLabel lblContadorLista;
    private List<AtencionMedica> listaActual;

    // Right Detail Hub (Expediente Clínico de Atención)
    private JPanel panelDetalleHub;
    private AtencionMedica atencionSeleccionada;

    public VistaConsultasMedicasPanel() {
        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(new EmptyBorder(12, 16, 14, 16));

        // 1. Cabecera superior y KPIs
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

    private JPanel crearCabeceraVista() {
        JPanel cab = new JPanel(new BorderLayout());
        cab.setOpaque(false);
        cab.setPreferredSize(new Dimension(0, 42));
        cab.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

        JPanel izq = new JPanel();
        izq.setOpaque(false);
        izq.setLayout(new BoxLayout(izq, BoxLayout.Y_AXIS));

        JLabel lblTit = new JLabel("Consultas Médicas y Atenciones Veterinarias");
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTit.setForeground(COLOR_TEXTO_TITULO);

        JLabel lblSub = new JLabel("Módulo 3.1 · Historial clínico, anamnesis, exploración física, constantes, diagnósticos y prescripciones");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblSub.setForeground(COLOR_TEXTO_MUTED);

        izq.add(lblTit);
        izq.add(Box.createVerticalStrut(2));
        izq.add(lblSub);
        cab.add(izq, BorderLayout.CENTER);

        JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        der.setOpaque(false);

        JLabel boxBadge = new JLabel(" Box Activo: Consultorio 1 · Dr. Roberto Mendoza ", SwingConstants.CENTER);
        boxBadge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        boxBadge.setForeground(new Color(15, 118, 110));
        boxBadge.setOpaque(true);
        boxBadge.setBackground(Ui.TURQUESA_SUAVE);
        boxBadge.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(153, 246, 228), 1, true),
                new EmptyBorder(4, 10, 4, 10)
        ));
        der.add(boxBadge);

        JButton btnRefrescar = crearBotonAccion("Actualizar", false);
        btnRefrescar.setIcon(Iconos.crearIconoRefrescar(13, COLOR_AZUL_PRIMARIO));
        btnRefrescar.setIconTextGap(5);
        btnRefrescar.addActionListener(e -> recargarDatos());
        der.add(btnRefrescar);

        JButton btnNueva = crearBotonAccion("+ Registrar Nueva Consulta", true);
        btnNueva.setIcon(Iconos.crearIconoEstetoscopio(13, Color.WHITE));
        btnNueva.setIconTextGap(5);
        btnNueva.addActionListener(e -> abrirModalNuevaConsulta(null));
        der.add(btnNueva);

        cab.add(der, BorderLayout.EAST);
        return cab;
    }

    private JPanel crearFilaKpis() {
        JPanel fila = new JPanel(new GridLayout(1, 4, 10, 0));
        fila.setOpaque(false);
        fila.setPreferredSize(new Dimension(0, 52));
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));

        lblKpiConsultasHoy = new JLabel("0");
        fila.add(crearCardKpi(lblKpiConsultasHoy, "CONSULTAS REGISTRADAS", Ui.TURQUESA_SUAVE, Iconos.crearIconoEstetoscopio(20, COLOR_AZUL_PRIMARIO)));

        lblKpiUrgencias = new JLabel("0");
        fila.add(crearCardKpi(lblKpiUrgencias, "CASOS DE URGENCIA", new Color(254, 226, 226), Iconos.crearIconoAlertaTriaje(20, new Color(220, 38, 38))));

        lblKpiRecetas = new JLabel("0");
        fila.add(crearCardKpi(lblKpiRecetas, "RECETAS PRESCRITAS", new Color(243, 232, 255), Iconos.crearIconoFactura(20, new Color(147, 51, 234))));

        lblKpiEnSeguimiento = new JLabel("0");
        fila.add(crearCardKpi(lblKpiEnSeguimiento, "CONTROLES PENDIENTES", new Color(254, 243, 199), Iconos.crearIconoReloj(20, new Color(217, 119, 6))));

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
    // PANEL MASTER (TABLA DE CONSULTAS)
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

        JLabel lblEs = new JLabel("Estado:");
        lblEs.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblEs.setForeground(COLOR_TEXTO_MUTED);
        izqFiltros.add(lblEs);

        cbFiltroEstado = new JComboBox<>(new String[]{"Todos los Estados", "Completada", "En Evaluación", "Derivada a Quirófano", "Hospitalizada"});
        cbFiltroEstado.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        cbFiltroEstado.setBackground(Color.WHITE);
        cbFiltroEstado.setPreferredSize(new Dimension(140, 28));
        cbFiltroEstado.addActionListener(e -> filtrarDatos());
        izqFiltros.add(cbFiltroEstado);

        JLabel lblVet = new JLabel("Veterinario:");
        lblVet.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblVet.setForeground(COLOR_TEXTO_MUTED);
        izqFiltros.add(lblVet);

        cbFiltroVeterinario = new JComboBox<>(new String[]{"Todos los Veterinarios", "Dra. Camila Morales", "Dr. Roberto Mendoza", "Dra. Ana Silva", "Dr. Mario Silva"});
        cbFiltroVeterinario.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        cbFiltroVeterinario.setBackground(Color.WHITE);
        cbFiltroVeterinario.setPreferredSize(new Dimension(150, 28));
        cbFiltroVeterinario.addActionListener(e -> filtrarDatos());
        izqFiltros.add(cbFiltroVeterinario);

        topFiltros.add(izqFiltros, BorderLayout.WEST);

        JPanel derFiltros = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        derFiltros.setOpaque(false);

        txtBuscar = new JTextField(12);
        txtBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        txtBuscar.setPreferredSize(new Dimension(130, 28));
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

        // Tabla Master de Consultas
        String[] columnas = {"Código / Fecha", "Paciente / Especie", "Tutor / Teléfono", "Diagnóstico y Motivo", "Veterinario", "Estado"};
        modeloConsultas = new DefaultTableModel(columnas, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        tablaConsultas = new JTable(modeloConsultas);
        tablaConsultas.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        Ui.formatearTabla(tablaConsultas, new int[]{0, 4, 5}, new int[]{});
        tablaConsultas.setRowHeight(48);
        tablaConsultas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        tablaConsultas.getColumnModel().getColumn(0).setPreferredWidth(100);
        tablaConsultas.getColumnModel().getColumn(1).setPreferredWidth(120);
        tablaConsultas.getColumnModel().getColumn(2).setPreferredWidth(110);
        tablaConsultas.getColumnModel().getColumn(3).setPreferredWidth(130);
        tablaConsultas.getColumnModel().getColumn(4).setPreferredWidth(105);
        tablaConsultas.getColumnModel().getColumn(5).setPreferredWidth(120);

        tablaConsultas.getColumnModel().getColumn(0).setCellRenderer(new CodigoFechaCellRenderer());
        tablaConsultas.getColumnModel().getColumn(1).setCellRenderer(new PacienteConsultaCellRenderer());
        tablaConsultas.getColumnModel().getColumn(2).setCellRenderer(new TutorConsultaCellRenderer());
        tablaConsultas.getColumnModel().getColumn(3).setCellRenderer(new DiagnosticoConsultaCellRenderer());
        tablaConsultas.getColumnModel().getColumn(5).setCellRenderer(new BadgeEstadoAtencionRenderer());

        tablaConsultas.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = tablaConsultas.getSelectedRow();
                if (row >= 0 && row < listaActual.size()) {
                    atencionSeleccionada = listaActual.get(row);
                } else {
                    atencionSeleccionada = null;
                }
                actualizarPanelDetalle();
            }
        });

        tablaConsultas.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && atencionSeleccionada != null) {
                    abrirModalEditarConsulta(atencionSeleccionada);
                }
            }
        });

        JScrollPane sp = new JScrollPane(tablaConsultas);
        sp.setBorder(BorderFactory.createLineBorder(COLOR_BORDE, 1));
        sp.getViewport().setBackground(Color.WHITE);
        card.add(sp, BorderLayout.CENTER);

        // Barra inferior del Master
        JPanel botMaster = new JPanel(new BorderLayout());
        botMaster.setOpaque(false);

        lblContadorLista = new JLabel("0 atenciones registradas");
        lblContadorLista.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblContadorLista.setForeground(COLOR_TEXTO_MUTED);
        botMaster.add(lblContadorLista, BorderLayout.WEST);

        JLabel lblDobleClic = new JLabel("Doble clic en una fila para modificar o ver diagnóstico completo");
        lblDobleClic.setFont(new Font("Segoe UI", Font.ITALIC, 10));
        lblDobleClic.setForeground(COLOR_TEXTO_MUTED);
        botMaster.add(lblDobleClic, BorderLayout.EAST);

        card.add(botMaster, BorderLayout.SOUTH);
        return card;
    }

    // ==========================================
    // PANEL DETAIL (EXPEDIENTE CLÍNICO EN VIVO)
    // ==========================================
    private JPanel crearPanelDetailWrapper() {
        panelDetalleHub = new JPanel(new BorderLayout());
        panelDetalleHub.setOpaque(false);
        actualizarPanelDetalle();
        return panelDetalleHub;
    }

    private void actualizarPanelDetalle() {
        panelDetalleHub.removeAll();

        if (atencionSeleccionada == null) {
            panelDetalleHub.add(crearEstadoVacioDetalle(), BorderLayout.CENTER);
        } else {
            panelDetalleHub.add(crearCardDetalleConsulta(atencionSeleccionada), BorderLayout.CENTER);
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

        JLabel ico = new JLabel(Iconos.crearIconoEstetoscopio(48, new Color(148, 163, 184)));
        ico.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel tit = new JLabel("Expediente Clínico de Atención");
        tit.setFont(new Font("Segoe UI", Font.BOLD, 15));
        tit.setForeground(COLOR_TEXTO_TITULO);
        tit.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel msg = new JLabel("<html><center>Seleccione una atención veterinaria en la tabla para visualizar la anamnesis, constantes fisiológicas, diagnóstico, prescripción médica y emisión de recetas oficiales.</center></html>");
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

    private JPanel crearCardDetalleConsulta(AtencionMedica a) {
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
        card.setBorder(new EmptyBorder(12, 16, 12, 16));

        // 1. Cabecera con Avatar Circular, Datos y Badge de Estado
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

                g2.setColor(Ui.TURQUESA_SUAVE);
                g2.fillOval(x, y, d, d);
                g2.setColor(COLOR_AZUL_PRIMARIO);
                g2.setStroke(new java.awt.BasicStroke(2.0f));
                g2.drawOval(x, y, d, d);

                String ini = a.getNombreMascota().substring(0, Math.min(2, a.getNombreMascota().length())).toUpperCase();
                g2.setFont(new Font("Segoe UI", Font.BOLD, 18));
                g2.setColor(new Color(15, 118, 110));
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
        JPanel pInfoPaciente = new JPanel();
        pInfoPaciente.setOpaque(false);
        pInfoPaciente.setLayout(new BoxLayout(pInfoPaciente, BoxLayout.Y_AXIS));

        JLabel lblNom = new JLabel(a.getNombreMascota() + "  (" + a.getIdConsulta() + ")");
        lblNom.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblNom.setForeground(COLOR_TEXTO_TITULO);

        JLabel lblEsp = new JLabel(a.getEspecieRaza() + " · Tutor: " + a.getNombreTutor());
        lblEsp.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblEsp.setForeground(COLOR_TEXTO_MUTED);

        JLabel lblDet = new JLabel("Atendido por: " + a.getVeterinario() + "  |  " + a.getFechaFormateada() + " " + a.getHoraFormateada() + " hrs");
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

        // Banner de Estado Clínico
        JPanel bannerEstado = new JPanel(new BorderLayout(8, 0)) {
            private static final long serialVersionUID = 1L;
            @Override
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

        JLabel lblEstTexto = new JLabel("ESTADO CLÍNICO: " + a.getEstado().toUpperCase());
        lblEstTexto.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblEstTexto.setForeground(a.getEstado().contains("Completada") ? new Color(22, 163, 74) :
                                  a.getEstado().contains("Quirófano") ? new Color(147, 51, 234) :
                                  a.getEstado().contains("Hospital") ? new Color(220, 38, 38) : new Color(2, 132, 199));
        lblEstTexto.setIcon(Iconos.crearIconoCheck(13, lblEstTexto.getForeground()));
        lblEstTexto.setIconTextGap(6);
        bannerEstado.add(lblEstTexto, BorderLayout.WEST);

        JLabel lblCostoBadge = new JLabel(String.format("Honorarios: S/. %.2f", a.getCosto()));
        lblCostoBadge.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblCostoBadge.setForeground(COLOR_TEXTO_TITULO);
        bannerEstado.add(lblCostoBadge, BorderLayout.EAST);

        pTop.add(bannerEstado);
        card.add(pTop, BorderLayout.NORTH);

        // 2. Centro con Scroll interno: Triada Clínica + Diagnóstico + Tratamiento + Receta
        ScrollablePanel pCentro = new ScrollablePanel();
        pCentro.setOpaque(false);
        pCentro.setLayout(new BoxLayout(pCentro, BoxLayout.Y_AXIS));

        // Grid 4 Constantes Vitales (2x2)
        JPanel gridConstantes = new JPanel(new GridLayout(2, 2, 8, 8));
        gridConstantes.setOpaque(false);
        gridConstantes.setMaximumSize(new Dimension(Integer.MAX_VALUE, 105));

        String evalT = (a.getTemperaturaC() > 39.2) ? "Febril" : (a.getTemperaturaC() < 38.0) ? "Hipotermia" : "Normal";
        Color colT = evalT.equals("Normal") ? new Color(22, 163, 74) : new Color(220, 38, 38);
        gridConstantes.add(crearWidgetSignoVital("TEMPERATURA CORPORAL", a.getTemperaturaC() + " °C", evalT, colT));

        String evalFc = (a.getFrecuenciaCardiacaLpm() > 140) ? "Taquicardia" : (a.getFrecuenciaCardiacaLpm() < 60) ? "Bradicardia" : "Normal";
        Color colFc = evalFc.equals("Normal") ? new Color(22, 163, 74) : new Color(217, 119, 6);
        gridConstantes.add(crearWidgetSignoVital("FRECUENCIA CARDÍACA", a.getFrecuenciaCardiacaLpm() + " lpm", evalFc, colFc));

        gridConstantes.add(crearWidgetSignoVital("PESO DEL PACIENTE", a.getPesoKg() + " kg", "Biometría", COLOR_AZUL_PRIMARIO));

        gridConstantes.add(crearWidgetSignoVital("PRÓXIMO CONTROL", a.getSeguimiento(), "Seguimiento", new Color(100, 116, 139)));

        pCentro.add(gridConstantes);
        pCentro.add(Box.createVerticalStrut(8));

        // Diagnóstico y Anamnesis
        JPanel pDiag = crearCajaSeccion("DIAGNÓSTICO CLÍNICO Y MOTIVO DE CONSULTA",
                "<b>Motivo:</b> " + a.getMotivoConsulta() + "<br>" +
                "<b>Diagnóstico Definitivo:</b> <span style='color:#0f766e;'><b>" +
                (a.getDiagnosticoDefinitivo().isEmpty() ? a.getDiagnosticoPresuntivo() : a.getDiagnosticoDefinitivo()) + "</b></span><br>" +
                "<b>Evaluación Física:</b> " + a.getEvaluacionClinica());
        pCentro.add(pDiag);
        pCentro.add(Box.createVerticalStrut(8));

        // Tratamiento aplicado en clínica
        JPanel pTrat = crearCajaSeccion("TRATAMIENTO APLICADO EN CLÍNICA", a.getTratamientoIndicado());
        pCentro.add(pTrat);
        pCentro.add(Box.createVerticalStrut(8));

        // Receta médica y prescripción para casa
        JPanel pReceta = crearCajaSeccion("RECETA MÉDICA Y POSOLOGÍA PARA CASA",
                "<b>Medicamentos prescritos:</b><br>" + a.getRecetaMedicamentos().replace("\n", "<br>") + "<br><br>" +
                "<b>Indicaciones para el Tutor:</b> " + a.getIndicacionesTutor());
        pCentro.add(pReceta);

        JScrollPane spCentro = new JScrollPane(pCentro);
        spCentro.setBorder(null);
        spCentro.setOpaque(false);
        spCentro.getViewport().setOpaque(false);
        spCentro.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        spCentro.getVerticalScrollBar().setUnitIncrement(12);
        card.add(spCentro, BorderLayout.CENTER);

        // 3. Footer: Dock de Acciones Rápidas (1-clic)
        JPanel pBotones = new JPanel(new GridLayout(2, 2, 8, 8));
        pBotones.setOpaque(false);

        JButton btnImprimir = crearBotonAccion("Imprimir Receta Médica", true);
        btnImprimir.setIcon(Iconos.crearIconoFactura(13, Color.WHITE));
        btnImprimir.setIconTextGap(6);
        btnImprimir.addActionListener(e -> imprimirRecetaOficial(a));

        JButton btnEditar = crearBotonAccion("Editar Atención", false);
        btnEditar.setIcon(Iconos.crearIconoEditar(13, COLOR_AZUL_PRIMARIO));
        btnEditar.setIconTextGap(6);
        btnEditar.addActionListener(e -> abrirModalEditarConsulta(a));

        JButton btnEstado = crearBotonAccion("Cambiar Estado", false);
        btnEstado.setIcon(Iconos.crearIconoCheck(13, new Color(22, 163, 74)));
        btnEstado.setIconTextGap(6);
        btnEstado.addActionListener(e -> cambiarEstadoAtencion(a));

        JButton btnNuevaMascota = crearBotonAccion("+ Nueva para Mascota", false);
        btnNuevaMascota.setIcon(Iconos.crearIconoEstetoscopio(13, new Color(100, 116, 139)));
        btnNuevaMascota.setIconTextGap(6);
        btnNuevaMascota.addActionListener(e -> {
            Optional<Mascota> optM = repo.todasLasMascotas().stream()
                    .filter(m -> m.getCodigo().equalsIgnoreCase(a.getCodigoMascota()))
                    .findFirst();
            abrirModalNuevaConsulta(optM.orElse(null));
        });

        pBotones.add(btnImprimir);
        pBotones.add(btnEditar);
        pBotones.add(btnEstado);
        pBotones.add(btnNuevaMascota);

        card.add(pBotones, BorderLayout.SOUTH);
        return card;
    }

    private JPanel crearCajaSeccion(String titulo, String contenidoHtml) {
        JPanel p = new JPanel(new BorderLayout(0, 4)) {
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

    private JPanel crearWidgetSignoVital(String titulo, String valor, String status, Color colorStatus) {
        JPanel w = new JPanel(new BorderLayout(2, 2)) {
            private static final long serialVersionUID = 1L;
            @Override
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
        lblV.setFont(new Font("Segoe UI", Font.BOLD, 13));
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
    public void cargarMascota(Mascota m) {
        if (m == null) return;
        recargarDatos();

        // Buscar si existe una atención previa para esta mascota
        for (AtencionMedica a : listaActual) {
            if (m.getCodigo().equalsIgnoreCase(a.getCodigoMascota())) {
                seleccionarAtencion(a);
                return;
            }
        }

        // Si no tiene consultas previas, abrir diálogo de registro preseleccionando la mascota
        abrirModalNuevaConsulta(m);
    }

    private void abrirModalNuevaConsulta(Mascota mInicial) {
        Window parent = SwingUtilities.getWindowAncestor(this);
        FormularioConsultaMedicaDialog dlg = new FormularioConsultaMedicaDialog(parent, null, mInicial, guardada -> {
            recargarDatos();
            seleccionarAtencion(guardada);
            JOptionPane.showMessageDialog(this,
                    "Consulta Médica " + guardada.getIdConsulta() + " registrada exitosamente para " + guardada.getNombreMascota() + ".\n" +
                    "Diagnóstico: " + guardada.getDiagnosticoDefinitivo(),
                    "Consulta Guardada", JOptionPane.INFORMATION_MESSAGE);
        });
        dlg.setVisible(true);
    }

    private void abrirModalEditarConsulta(AtencionMedica a) {
        Window parent = SwingUtilities.getWindowAncestor(this);
        FormularioConsultaMedicaDialog dlg = new FormularioConsultaMedicaDialog(parent, a, null, guardada -> {
            recargarDatos();
            seleccionarAtencion(guardada);
            JOptionPane.showMessageDialog(this,
                    "Expediente " + guardada.getIdConsulta() + " actualizado correctamente.",
                    "Atención Actualizada", JOptionPane.INFORMATION_MESSAGE);
        });
        dlg.setVisible(true);
    }

    private void cambiarEstadoAtencion(AtencionMedica a) {
        String[] opciones = {"Completada", "En Evaluación", "Derivada a Quirófano", "Hospitalizada"};
        int sel = JOptionPane.showOptionDialog(
                this,
                "Seleccione el nuevo estado clínico para " + a.getNombreMascota() + ":",
                "Actualizar Estado Clínico - " + a.getIdConsulta(),
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                Iconos.crearIconoEstetoscopio(24, COLOR_AZUL_PRIMARIO),
                opciones,
                a.getEstado()
        );

        if (sel >= 0 && sel < opciones.length) {
            String nuevoEstado = opciones[sel];
            a.setEstado(nuevoEstado);
            repo.guardarAtencionMedica(a);
            recargarDatos();
            seleccionarAtencion(a);
            JOptionPane.showMessageDialog(this,
                    "Estado actualizado a: " + nuevoEstado,
                    "Estado Modificado", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void imprimirRecetaOficial(AtencionMedica a) {
        String receta = a.getRecetaMedicamentos().trim();
        String indic = a.getIndicacionesTutor().trim();

        if (receta.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Esta consulta no tiene medicamentos prescritos para emitir receta.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String[][] datos = new String[][]{
                {"1", "Prescripción Farmacológica", receta, "Administrar conforme a posología"},
                {"2", "Cuidados Generales en Casa", indic.isEmpty() ? "Reposo e hidratación constante" : indic, "Cuidados post-consulta"},
                {"3", "Próximo Control Clínico", a.getSeguimiento(), "Revisión programada"},
                {"4", "Médico Veterinario Colegiado", a.getVeterinario(), "Firma y sello médico autorizado"}
        };

        Ui.mostrarVisorReporte(
                (java.awt.Frame) SwingUtilities.getWindowAncestor(this),
                "RECETA MÉDICA VETERINARIA OFICIAL",
                "Paciente: " + a.getNombreMascota() + " (" + a.getEspecieRaza() + ") | Tutor: " + a.getNombreTutor() + " | Fecha: " + a.getFechaFormateada(),
                "Veterinario Tratante: " + a.getVeterinario() + " | Diagnóstico: " + a.getDiagnosticoDefinitivo(),
                new String[]{"Ítem", "Concepto Clínico", "Detalle / Posología", "Observaciones"},
                datos,
                "RECETA OFICIAL VÁLIDA · CLÍNICA VETERINARIA HAPPY PETS S.A.C.",
                "RecetaMedica_" + a.getNombreMascota()
        );
    }

    public void recargarDatos() {
        filtrarDatos();
    }

    private void filtrarDatos() {
        List<AtencionMedica> todas = repo.getAtencionesMedicas();
        String fEst = cbFiltroEstado != null ? (String) cbFiltroEstado.getSelectedItem() : "Todos";
        String fVet = cbFiltroVeterinario != null ? (String) cbFiltroVeterinario.getSelectedItem() : "Todos";
        String q = (txtBuscar != null && txtBuscar.getText() != null) ? txtBuscar.getText().trim().toLowerCase() : "";

        listaActual = todas.stream().filter(a -> {
            if (fEst != null && !fEst.startsWith("Todos") && !a.getEstado().equalsIgnoreCase(fEst)) return false;
            if (fVet != null && !fVet.startsWith("Todos") && !a.getVeterinario().contains(fVet)) return false;
            if (!q.isEmpty()) {
                boolean match = a.getIdConsulta().toLowerCase().contains(q) ||
                                a.getNombreMascota().toLowerCase().contains(q) ||
                                a.getNombreTutor().toLowerCase().contains(q) ||
                                a.getDiagnosticoDefinitivo().toLowerCase().contains(q) ||
                                a.getDiagnosticoPresuntivo().toLowerCase().contains(q) ||
                                a.getMotivoConsulta().toLowerCase().contains(q) ||
                                a.getVeterinario().toLowerCase().contains(q);
                if (!match) return false;
            }
            return true;
        }).toList();

        modeloConsultas.setRowCount(0);
        for (AtencionMedica a : listaActual) {
            modeloConsultas.addRow(new Object[]{
                    a,
                    a,
                    a,
                    a,
                    a.getVeterinario(),
                    a.getEstado()
            });
        }

        lblContadorLista.setText(listaActual.size() + " atenciones registradas en el sistema");

        // KPIs
        long hoy = todas.stream().filter(a -> a.getFecha().equals(LocalDate.now())).count();
        long urg = todas.stream().filter(a -> a.getMotivoConsulta().toLowerCase().contains("vómito") ||
                                              a.getMotivoConsulta().toLowerCase().contains("urgencia") ||
                                              a.getMotivoConsulta().toLowerCase().contains("veneno")).count();
        long rec = todas.stream().filter(a -> a.getRecetaMedicamentos() != null && !a.getRecetaMedicamentos().isEmpty()).count();

        lblKpiConsultasHoy.setText(String.valueOf(hoy > 0 ? hoy : todas.size()));
        lblKpiUrgencias.setText(String.valueOf(urg));
        lblKpiRecetas.setText(String.valueOf(rec));
        lblKpiEnSeguimiento.setText("2");

        if (atencionSeleccionada != null) {
            seleccionarAtencion(atencionSeleccionada);
        } else if (!listaActual.isEmpty()) {
            seleccionarFila(0);
        } else {
            atencionSeleccionada = null;
            actualizarPanelDetalle();
        }
    }

    private void seleccionarAtencion(AtencionMedica a) {
        if (a == null) return;
        for (int i = 0; i < listaActual.size(); i++) {
            if (listaActual.get(i).getIdConsulta().equalsIgnoreCase(a.getIdConsulta())) {
                seleccionarFila(i);
                return;
            }
        }
        actualizarPanelDetalle();
    }

    private void seleccionarFila(int fila) {
        if (fila >= 0 && fila < tablaConsultas.getRowCount()) {
            tablaConsultas.setRowSelectionInterval(fila, fila);
            tablaConsultas.scrollRectToVisible(tablaConsultas.getCellRect(fila, 0, true));
            atencionSeleccionada = listaActual.get(fila);
        } else {
            atencionSeleccionada = null;
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
    private static class CodigoFechaCellRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JPanel p = new JPanel(new BorderLayout(0, 2));
            p.setOpaque(true);
            p.setBackground(isSelected ? table.getSelectionBackground() : (row % 2 == 0 ? Color.WHITE : new Color(249, 250, 251)));
            p.setBorder(new EmptyBorder(6, 8, 6, 6));

            if (value instanceof AtencionMedica a) {
                JLabel lblCod = new JLabel(a.getIdConsulta());
                lblCod.setFont(new Font("Segoe UI", Font.BOLD, 12));
                lblCod.setForeground(isSelected ? table.getSelectionForeground() : COLOR_AZUL_PRIMARIO);

                JLabel lblFec = new JLabel(a.getFechaFormateada() + " " + a.getHoraFormateada() + "h");
                lblFec.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                lblFec.setForeground(isSelected ? table.getSelectionForeground() : COLOR_TEXTO_MUTED);

                p.add(lblCod, BorderLayout.NORTH);
                p.add(lblFec, BorderLayout.CENTER);
            }
            return p;
        }
    }

    private static class PacienteConsultaCellRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JPanel p = new JPanel(new BorderLayout(8, 0));
            p.setOpaque(true);
            p.setBackground(isSelected ? table.getSelectionBackground() : (row % 2 == 0 ? Color.WHITE : new Color(249, 250, 251)));
            p.setBorder(new EmptyBorder(6, 6, 6, 6));

            if (value instanceof AtencionMedica a) {
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
                        String ini = a.getNombreMascota().substring(0, 1).toUpperCase();
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

                JLabel lblNom = new JLabel(a.getNombreMascota());
                lblNom.setFont(new Font("Segoe UI", Font.BOLD, 12));
                lblNom.setForeground(isSelected ? table.getSelectionForeground() : COLOR_TEXTO_TITULO);

                JLabel lblEsp = new JLabel(a.getEspecieRaza());
                lblEsp.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                lblEsp.setForeground(isSelected ? table.getSelectionForeground() : COLOR_TEXTO_MUTED);

                pTxt.add(lblNom);
                pTxt.add(lblEsp);
                p.add(pTxt, BorderLayout.CENTER);
            }
            return p;
        }
    }

    private static class TutorConsultaCellRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JPanel p = new JPanel(new BorderLayout(0, 2));
            p.setOpaque(true);
            p.setBackground(isSelected ? table.getSelectionBackground() : (row % 2 == 0 ? Color.WHITE : new Color(249, 250, 251)));
            p.setBorder(new EmptyBorder(6, 6, 6, 6));

            if (value instanceof AtencionMedica a) {
                JLabel lblTutor = new JLabel(a.getNombreTutor());
                lblTutor.setFont(new Font("Segoe UI", Font.BOLD, 11));
                lblTutor.setForeground(isSelected ? table.getSelectionForeground() : COLOR_TEXTO_TITULO);

                JLabel lblTel = new JLabel(a.getTelefonoTutor());
                lblTel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                lblTel.setForeground(isSelected ? table.getSelectionForeground() : COLOR_TEXTO_MUTED);

                p.add(lblTutor, BorderLayout.NORTH);
                p.add(lblTel, BorderLayout.CENTER);
            }
            return p;
        }
    }

    private static class DiagnosticoConsultaCellRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JPanel p = new JPanel(new BorderLayout(0, 2));
            p.setOpaque(true);
            p.setBackground(isSelected ? table.getSelectionBackground() : (row % 2 == 0 ? Color.WHITE : new Color(249, 250, 251)));
            p.setBorder(new EmptyBorder(6, 6, 6, 6));

            if (value instanceof AtencionMedica a) {
                String diag = a.getDiagnosticoDefinitivo().isEmpty() ? a.getDiagnosticoPresuntivo() : a.getDiagnosticoDefinitivo();
                JLabel lblDiag = new JLabel(diag);
                lblDiag.setFont(new Font("Segoe UI", Font.BOLD, 11));
                lblDiag.setForeground(isSelected ? table.getSelectionForeground() : new Color(15, 118, 110));

                JLabel lblMot = new JLabel(a.getMotivoConsulta());
                lblMot.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                lblMot.setForeground(isSelected ? table.getSelectionForeground() : COLOR_TEXTO_MUTED);

                p.add(lblDiag, BorderLayout.NORTH);
                p.add(lblMot, BorderLayout.CENTER);
            }
            return p;
        }
    }

    private static class BadgeEstadoAtencionRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 8));
            p.setOpaque(true);
            p.setBackground(isSelected ? table.getSelectionBackground() : (row % 2 == 0 ? Color.WHITE : new Color(249, 250, 251)));

            String st = String.valueOf(value);
            Color bg;
            Color fg;
            if ("Completada".equalsIgnoreCase(st)) {
                bg = new Color(220, 252, 231);
                fg = new Color(22, 163, 74);
            } else if ("En Evaluación".equalsIgnoreCase(st)) {
                bg = Ui.TURQUESA_SUAVE;
                fg = new Color(15, 118, 110);
            } else if ("Derivada a Quirófano".equalsIgnoreCase(st)) {
                bg = new Color(243, 232, 255);
                fg = new Color(147, 51, 234);
            } else if ("Hospitalizada".equalsIgnoreCase(st)) {
                bg = new Color(254, 226, 226);
                fg = new Color(220, 38, 38);
            } else {
                bg = new Color(241, 245, 249);
                fg = new Color(100, 116, 139);
            }

            JLabel badge = new JLabel(st, SwingConstants.CENTER) {
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
            badge.setBorder(new EmptyBorder(3, 7, 3, 7));

            p.add(badge);
            return p;
        }
    }
}
