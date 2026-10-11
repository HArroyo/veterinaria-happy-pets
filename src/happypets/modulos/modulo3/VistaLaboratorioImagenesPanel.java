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
import happypets.model.Cliente;
import happypets.model.Mascota;
import happypets.model.OrdenLaboratorio;
import happypets.ui.Iconos;
import happypets.ui.Ui;

/**
 * Submódulo 3.4: Laboratorio Clínico y Diagnóstico por Imágenes.
 * Arquitectura Master-Detail (Opción A) con catálogo analítico en vivo,
 * workstation de informes radiológicos y emisión de certificados diagnósticos.
 */
public class VistaLaboratorioImagenesPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private static final Color COLOR_BORDE = Ui.BORDE_SUAVE;
    private static final Color COLOR_AZUL_PRIMARIO = Ui.TURQUESA;
    private static final Color COLOR_TEXTO_TITULO = Ui.TEXTO_TITULO;
    private static final Color COLOR_TEXTO_MUTED = Ui.TEXTO_MUTED;

    private final RepositorioVeterinaria repo = RepositorioVeterinaria.getInstancia();

    // Filtros
    private JComboBox<String> cbFiltroCategoria;
    private JComboBox<String> cbFiltroEstado;
    private JTextField txtBuscar;

    // KPIs
    private JLabel lblKpiTotalOrdenes;
    private JLabel lblKpiLaboratorio;
    private JLabel lblKpiImagenes;
    private JLabel lblKpiCompletados;

    // Master Table
    private JTable tablaHistorial;
    private DefaultTableModel modeloHistorial;
    private JLabel lblContadorHistorial;
    private List<OrdenLaboratorio> listaActual;

    // Right Detail Hub (Workstation Diagnóstica)
    private JPanel panelDetalleHub;
    private OrdenLaboratorio ordenSeleccionada;

    public VistaLaboratorioImagenesPanel() {
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

        JLabel titulo = new JLabel("Laboratorio Clínico y Diagnóstico por Imágenes");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 17));
        titulo.setForeground(COLOR_TEXTO_TITULO);

        JLabel subtitulo = new JLabel("Módulo 3.4 · Órdenes diagnósticas, procesamiento de muestras, informes radiológicos e imágenes PACS");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitulo.setForeground(COLOR_TEXTO_MUTED);

        izq.add(titulo);
        izq.add(Box.createVerticalStrut(2));
        izq.add(subtitulo);
        cab.add(izq, BorderLayout.CENTER);

        JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        der.setOpaque(false);

        JLabel badgePACS = new JLabel("Visor Digital PACS / LIS: Conectado") {
            private static final long serialVersionUID = 1L;
            @Override
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
        badgePACS.setFont(new Font("Segoe UI", Font.BOLD, 11));
        badgePACS.setForeground(new Color(15, 118, 110));
        badgePACS.setBorder(new EmptyBorder(5, 10, 5, 10));

        JButton btnNuevo = crearBotonAccion("+ Nueva Orden Diagnóstica", true);
        btnNuevo.setIcon(Iconos.crearIconoMicroscopio(13, Color.WHITE));
        btnNuevo.setIconTextGap(6);
        btnNuevo.addActionListener(e -> abrirModalNuevaOrden(null));

        der.add(badgePACS);
        der.add(btnNuevo);

        cab.add(der, BorderLayout.EAST);
        return cab;
    }

    private JPanel crearFilaKpis() {
        JPanel fila = new JPanel(new GridLayout(1, 4, 10, 0));
        fila.setOpaque(false);
        fila.setPreferredSize(new Dimension(0, 56));
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));

        lblKpiTotalOrdenes = new JLabel("0");
        lblKpiLaboratorio = new JLabel("0");
        lblKpiImagenes = new JLabel("0");
        lblKpiCompletados = new JLabel("0");

        fila.add(crearCardKpi(lblKpiTotalOrdenes, "ESTUDIOS TOTALES", Ui.TURQUESA_SUAVE, COLOR_AZUL_PRIMARIO, Iconos.crearIconoMicroscopio(18, COLOR_AZUL_PRIMARIO)));
        fila.add(crearCardKpi(lblKpiLaboratorio, "LABORATORIO CLÍNICO", new Color(224, 242, 254), new Color(2, 132, 199), Iconos.crearIconoJeringa(18, new Color(2, 132, 199))));
        fila.add(crearCardKpi(lblKpiImagenes, "RAYOS X / ECOGRAFÍA", new Color(243, 232, 255), new Color(147, 51, 234), Iconos.crearIconoHistorial(18, new Color(147, 51, 234))));
        fila.add(crearCardKpi(lblKpiCompletados, "INFORMES COMPLETADOS", new Color(220, 252, 231), new Color(22, 163, 74), Iconos.crearIconoCheck(18, new Color(22, 163, 74))));

        return fila;
    }

    private JPanel crearCardKpi(JLabel lblVal, String etiqueta, Color bgIco, Color fgIco, Icon icono) {
        JPanel card = new JPanel(new BorderLayout(8, 0)) {
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
    // PANEL MASTER (TABLA DE ÓRDENES)
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

        // Barra de filtros
        JPanel topFiltros = new JPanel(new BorderLayout(8, 0));
        topFiltros.setOpaque(false);

        JPanel izqFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        izqFiltros.setOpaque(false);

        JLabel lblCat = new JLabel("Categoría:");
        lblCat.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblCat.setForeground(COLOR_TEXTO_MUTED);
        izqFiltros.add(lblCat);

        cbFiltroCategoria = new JComboBox<>(new String[]{"Todas las Categorías", "Laboratorio Clínico", "Diagnóstico por Imágenes"});
        cbFiltroCategoria.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        cbFiltroCategoria.setBackground(Color.WHITE);
        cbFiltroCategoria.setPreferredSize(new Dimension(150, 28));
        cbFiltroCategoria.addActionListener(e -> filtrarDatos());
        izqFiltros.add(cbFiltroCategoria);

        JLabel lblEs = new JLabel("Estado:");
        lblEs.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblEs.setForeground(COLOR_TEXTO_MUTED);
        izqFiltros.add(lblEs);

        cbFiltroEstado = new JComboBox<>(new String[]{"Todos los Estados", "Completado", "En Proceso", "Solicitado"});
        cbFiltroEstado.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        cbFiltroEstado.setBackground(Color.WHITE);
        cbFiltroEstado.setPreferredSize(new Dimension(130, 28));
        cbFiltroEstado.addActionListener(e -> filtrarDatos());
        izqFiltros.add(cbFiltroEstado);

        topFiltros.add(izqFiltros, BorderLayout.WEST);

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
        String[] columnas = {"Código / Fecha", "Paciente / Especie", "Tutor / Teléfono", "Estudio Solicitado", "Prioridad", "Estado"};
        modeloHistorial = new DefaultTableModel(columnas, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        tablaHistorial = new JTable(modeloHistorial);
        tablaHistorial.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        Ui.formatearTabla(tablaHistorial, new int[]{0, 4, 5}, new int[]{});
        tablaHistorial.setRowHeight(48);
        tablaHistorial.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        tablaHistorial.getColumnModel().getColumn(0).setPreferredWidth(100);
        tablaHistorial.getColumnModel().getColumn(1).setPreferredWidth(125);
        tablaHistorial.getColumnModel().getColumn(2).setPreferredWidth(110);
        tablaHistorial.getColumnModel().getColumn(3).setPreferredWidth(155);
        tablaHistorial.getColumnModel().getColumn(4).setPreferredWidth(90);
        tablaHistorial.getColumnModel().getColumn(5).setPreferredWidth(95);

        tablaHistorial.getColumnModel().getColumn(0).setCellRenderer(new CodigoFechaOrdenRenderer());
        tablaHistorial.getColumnModel().getColumn(1).setCellRenderer(new PacienteOrdenRenderer());
        tablaHistorial.getColumnModel().getColumn(2).setCellRenderer(new TutorOrdenRenderer());
        tablaHistorial.getColumnModel().getColumn(3).setCellRenderer(new EstudioOrdenRenderer());
        tablaHistorial.getColumnModel().getColumn(4).setCellRenderer(new BadgePrioridadRenderer());
        tablaHistorial.getColumnModel().getColumn(5).setCellRenderer(new BadgeEstadoOrdenRenderer());

        tablaHistorial.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = tablaHistorial.getSelectedRow();
                if (row >= 0 && row < listaActual.size()) {
                    ordenSeleccionada = listaActual.get(row);
                } else {
                    ordenSeleccionada = null;
                }
                actualizarPanelDetalle();
            }
        });

        tablaHistorial.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && ordenSeleccionada != null) {
                    abrirModalEditarOrden(ordenSeleccionada);
                }
            }
        });

        JScrollPane sp = new JScrollPane(tablaHistorial);
        sp.setBorder(BorderFactory.createLineBorder(COLOR_BORDE, 1));
        sp.getViewport().setBackground(Color.WHITE);
        card.add(sp, BorderLayout.CENTER);

        // Barra inferior del Master
        JPanel botMaster = new JPanel(new BorderLayout());
        botMaster.setOpaque(false);

        lblContadorHistorial = new JLabel("0 estudios registrados");
        lblContadorHistorial.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblContadorHistorial.setForeground(COLOR_TEXTO_MUTED);
        botMaster.add(lblContadorHistorial, BorderLayout.WEST);

        JLabel lblDobleClic = new JLabel("Doble clic en una fila para modificar o ingresar resultados");
        lblDobleClic.setFont(new Font("Segoe UI", Font.ITALIC, 10));
        lblDobleClic.setForeground(COLOR_TEXTO_MUTED);
        botMaster.add(lblDobleClic, BorderLayout.EAST);

        card.add(botMaster, BorderLayout.SOUTH);
        return card;
    }

    // ==========================================
    // PANEL DETAIL (WORKSTATION DIAGNÓSTICA)
    // ==========================================
    private JPanel crearPanelDetailWrapper() {
        panelDetalleHub = new JPanel(new BorderLayout());
        panelDetalleHub.setOpaque(false);
        actualizarPanelDetalle();
        return panelDetalleHub;
    }

    private void actualizarPanelDetalle() {
        panelDetalleHub.removeAll();

        if (ordenSeleccionada == null) {
            panelDetalleHub.add(crearEstadoVacioDetalle(), BorderLayout.CENTER);
        } else {
            panelDetalleHub.add(crearCardDetalleOrden(ordenSeleccionada), BorderLayout.CENTER);
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

        JLabel ico = new JLabel(Iconos.crearIconoMicroscopio(48, new Color(148, 163, 184)));
        ico.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel tit = new JLabel("Workstation de Laboratorio e Imágenes");
        tit.setFont(new Font("Segoe UI", Font.BOLD, 15));
        tit.setForeground(COLOR_TEXTO_TITULO);
        tit.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel msg = new JLabel("<html><center>Seleccione una orden en la tabla para examinar los valores analíticos, visor radiográfico/ecográfico, interpretación patológica y emisión del informe oficial.</center></html>");
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

    private JPanel crearCardDetalleOrden(OrdenLaboratorio o) {
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

        // 1. Cabecera con Avatar Circular, Datos y Banner de Estado
        JPanel pTop = new JPanel();
        pTop.setOpaque(false);
        pTop.setLayout(new BoxLayout(pTop, BoxLayout.Y_AXIS));

        JPanel pFilaAvatar = new JPanel(new BorderLayout(12, 0));
        pFilaAvatar.setOpaque(false);

        // Circular Avatar seguro
        boolean esImagen = o.getCategoria().contains("Imágenes");
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

                g2.setColor(esImagen ? new Color(243, 232, 255) : new Color(224, 242, 254));
                g2.fillOval(x, y, d, d);
                g2.setColor(esImagen ? new Color(147, 51, 234) : COLOR_AZUL_PRIMARIO);
                g2.setStroke(new java.awt.BasicStroke(2.0f));
                g2.drawOval(x, y, d, d);

                String ini = o.getNombreMascota().substring(0, Math.min(2, o.getNombreMascota().length())).toUpperCase();
                g2.setFont(new Font("Segoe UI", Font.BOLD, 18));
                g2.setColor(esImagen ? new Color(109, 40, 217) : new Color(15, 118, 110));
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

        JLabel lblNom = new JLabel(o.getNombreMascota() + "  (" + o.getIdOrden() + ")");
        lblNom.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblNom.setForeground(COLOR_TEXTO_TITULO);

        JLabel lblEsp = new JLabel(o.getEspecieRaza() + " · Tutor: " + o.getNombreTutor());
        lblEsp.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblEsp.setForeground(COLOR_TEXTO_MUTED);

        JLabel lblDet = new JLabel(o.getCategoria() + "  |  " + o.getTipoEstudio());
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

        // Banner de Estado Diagnóstico
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

        JLabel lblEstTexto = new JLabel("ESTADO: " + o.getEstado().toUpperCase());
        lblEstTexto.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblEstTexto.setForeground("Completado".equalsIgnoreCase(o.getEstado()) ? new Color(22, 163, 74) :
                                  "En Proceso".equalsIgnoreCase(o.getEstado()) ? new Color(2, 132, 199) : new Color(217, 119, 6));
        lblEstTexto.setIcon(Iconos.crearIconoCheck(13, lblEstTexto.getForeground()));
        lblEstTexto.setIconTextGap(6);
        bannerEstado.add(lblEstTexto, BorderLayout.WEST);

        JLabel lblPrio = new JLabel("Prioridad: " + o.getPrioridad().toUpperCase() + "  |  Fecha: " + o.getFechaSolicitudFormateada());
        lblPrio.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblPrio.setForeground("Urgente".equalsIgnoreCase(o.getPrioridad()) || "Emergencia".equalsIgnoreCase(o.getPrioridad()) ? new Color(220, 38, 38) : COLOR_TEXTO_TITULO);
        bannerEstado.add(lblPrio, BorderLayout.EAST);

        pTop.add(bannerEstado);
        card.add(pTop, BorderLayout.NORTH);

        // 2. Centro con Scroll interno: Telemetría + Visor/Resultados + Conclusiones
        ScrollablePanel pCentro = new ScrollablePanel();
        pCentro.setOpaque(false);
        pCentro.setLayout(new BoxLayout(pCentro, BoxLayout.Y_AXIS));

        // Grid 4 Métricas (2x2)
        JPanel gridMetricas = new JPanel(new GridLayout(2, 2, 8, 8));
        gridMetricas.setOpaque(false);
        gridMetricas.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));

        gridMetricas.add(crearWidgetLab("CATEGORÍA", o.getCategoria().contains("Imágenes") ? "Imágenes PACS" : "Lab. Clínico", "Especialidad",
                o.getCategoria().contains("Imágenes") ? new Color(147, 51, 234) : COLOR_AZUL_PRIMARIO));
        gridMetricas.add(crearWidgetLab("VETERINARIO SOLICITANTE", o.getVeterinarioSolicitante(), "Médico Titular", new Color(15, 118, 110)));
        gridMetricas.add(crearWidgetLab("ESPECIALISTA RESPONSABLE", o.getResponsableProcesamiento(), "Procesamiento", new Color(100, 116, 139)));
        gridMetricas.add(crearWidgetLab("FECHA RESULTADO", o.getFechaResultadoFormateada(), o.getEstado(),
                "Completado".equalsIgnoreCase(o.getEstado()) ? new Color(22, 163, 74) : new Color(217, 119, 6)));

        gridMetricas.setAlignmentX(Component.LEFT_ALIGNMENT);
        pCentro.add(gridMetricas);
        pCentro.add(Box.createVerticalStrut(8));

        // Cuadro de Estudio y Sospecha Clínica
        JPanel pEstudio = crearCajaSeccion("ESTUDIO DIAGNÓSTICO Y MOTIVO DE CONSULTA",
                "<b>Estudio:</b> <span style='color:#0f766e;'><b>" + o.getTipoEstudio() + "</b></span><br>" +
                "<b>Motivo / Sospecha Clínica:</b> " + o.getMotivoSospecha());
        pEstudio.setAlignmentX(Component.LEFT_ALIGNMENT);
        pCentro.add(pEstudio);
        pCentro.add(Box.createVerticalStrut(8));

        // Si es imagen, mostramos simulación del visor PACS oscuro
        if (esImagen) {
            JPanel pVisorRx = crearVisorDigitalRx(o);
            pVisorRx.setAlignmentX(Component.LEFT_ALIGNMENT);
            pCentro.add(pVisorRx);
            pCentro.add(Box.createVerticalStrut(8));
        }

        // Valores analíticos cuantitativos
        JPanel pValores = crearCajaSeccion("HALLAZGOS CUANTITATIVOS Y PARÁMETROS",
                o.getResultadoValores() != null && !o.getResultadoValores().isEmpty() ? o.getResultadoValores() : "En espera de procesamiento en laboratorio.");
        pValores.setAlignmentX(Component.LEFT_ALIGNMENT);
        pCentro.add(pValores);
        pCentro.add(Box.createVerticalStrut(8));

        // Informe e interpretación
        JPanel pInforme = crearCajaSeccion("INFORME MÉDICO E INTERPRETACIÓN CLÍNICA",
                "<b>Informe Detallado:</b><br>" + o.getInformeDetallado() + "<br><br>" +
                "<b>Conclusión Diagnóstica:</b> <span style='color:#0f766e;'><b>" + o.getConclusionesRecomendaciones() + "</b></span>");
        pInforme.setAlignmentX(Component.LEFT_ALIGNMENT);
        pCentro.add(pInforme);
        pCentro.add(Box.createVerticalGlue());

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

        JButton btnReporte = crearBotonAccion("Emitir Informe Oficial", true);
        btnReporte.setIcon(Iconos.crearIconoFactura(13, Color.WHITE));
        btnReporte.setIconTextGap(6);
        btnReporte.addActionListener(e -> emitirInformeOficial(o));

        JButton btnEditar = crearBotonAccion("Cargar / Editar Resultados", false);
        btnEditar.setIcon(Iconos.crearIconoEditar(13, COLOR_AZUL_PRIMARIO));
        btnEditar.setIconTextGap(6);
        btnEditar.addActionListener(e -> abrirModalEditarOrden(o));

        JButton btnEstado = crearBotonAccion("Cambiar Estado", false);
        btnEstado.setIcon(Iconos.crearIconoCheck(13, new Color(22, 163, 74)));
        btnEstado.setIconTextGap(6);
        btnEstado.addActionListener(e -> cambiarEstadoOrden(o));

        JButton btnNuevaMascota = crearBotonAccion("+ Nueva para Mascota", false);
        btnNuevaMascota.setIcon(Iconos.crearIconoMicroscopio(13, new Color(100, 116, 139)));
        btnNuevaMascota.setIconTextGap(6);
        btnNuevaMascota.addActionListener(e -> {
            Optional<Mascota> optM = repo.todasLasMascotas().stream()
                    .filter(m -> m.getCodigo().equalsIgnoreCase(o.getCodigoMascota()))
                    .findFirst();
            abrirModalNuevaOrden(optM.orElse(null));
        });

        pBotones.add(btnReporte);
        pBotones.add(btnEditar);
        pBotones.add(btnEstado);
        pBotones.add(btnNuevaMascota);

        card.add(pBotones, BorderLayout.SOUTH);
        return card;
    }

    private JPanel crearVisorDigitalRx(OrdenLaboratorio o) {
        JPanel pVisor = new JPanel(new BorderLayout(0, 6)) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Fondo oscuro radiológico médico
                g2.setColor(new Color(15, 23, 42));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(new Color(51, 65, 85));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);

                // Cuadrícula y silueta médica
                g2.setColor(new Color(30, 41, 59));
                for (int x = 20; x < getWidth(); x += 30) g2.drawLine(x, 10, x, getHeight() - 10);
                for (int y = 20; y < getHeight(); y += 25) g2.drawLine(10, y, getWidth() - 10, y);

                g2.dispose();
            }
        };
        pVisor.setPreferredSize(new Dimension(0, 125));
        pVisor.setMaximumSize(new Dimension(Integer.MAX_VALUE, 125));
        pVisor.setBorder(new EmptyBorder(8, 12, 8, 12));

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);

        JLabel lblDicom = new JLabel("VISOR DIGITAL PACS · " + o.getTipoEstudio().toUpperCase());
        lblDicom.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblDicom.setForeground(new Color(148, 163, 184));
        topBar.add(lblDicom, BorderLayout.WEST);

        JLabel lblProy = new JLabel("Proyección: Lat-Lat / VD · Calibración 100%");
        lblProy.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblProy.setForeground(new Color(45, 212, 191));
        topBar.add(lblProy, BorderLayout.EAST);
        pVisor.add(topBar, BorderLayout.NORTH);

        JPanel centerInfo = new JPanel();
        centerInfo.setOpaque(false);
        centerInfo.setLayout(new BoxLayout(centerInfo, BoxLayout.Y_AXIS));

        JLabel lblEstudio = new JLabel(o.getNombreMascota() + " · " + o.getEspecieRaza() + " (" + o.getIdOrden() + ")");
        lblEstudio.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblEstudio.setForeground(Color.WHITE);

        JLabel lblInfo = new JLabel("<html><span style='color:#94a3b8;'>Estudio radiográfico digitalizado de alta resolución. Contraste y densidad ósea/tejidos blandos en rango óptimo.</span></html>");
        lblInfo.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblInfo.setForeground(new Color(203, 213, 225));

        centerInfo.add(Box.createVerticalGlue());
        centerInfo.add(lblEstudio);
        centerInfo.add(Box.createVerticalStrut(4));
        centerInfo.add(lblInfo);
        centerInfo.add(Box.createVerticalGlue());
        pVisor.add(centerInfo, BorderLayout.CENTER);

        return pVisor;
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

    private JPanel crearWidgetLab(String titulo, String valor, String status, Color colorStatus) {
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

        for (OrdenLaboratorio o : listaActual) {
            if (m.getCodigo().equalsIgnoreCase(o.getCodigoMascota())) {
                seleccionarOrden(o);
                return;
            }
        }

        abrirModalNuevaOrden(m);
    }

    private void abrirModalNuevaOrden(Mascota mInicial) {
        Window parent = SwingUtilities.getWindowAncestor(this);
        FormularioOrdenLaboratorioDialog dlg = new FormularioOrdenLaboratorioDialog(parent, null, mInicial, guardada -> {
            recargarDatos();
            seleccionarOrden(guardada);
            JOptionPane.showMessageDialog(this,
                    "Orden " + guardada.getIdOrden() + " generada con éxito para " + guardada.getNombreMascota() + ".\n" +
                    "Estudio: " + guardada.getTipoEstudio(),
                    "Solicitud Creada", JOptionPane.INFORMATION_MESSAGE);
        });
        dlg.setVisible(true);
    }

    private void abrirModalEditarOrden(OrdenLaboratorio o) {
        Window parent = SwingUtilities.getWindowAncestor(this);
        FormularioOrdenLaboratorioDialog dlg = new FormularioOrdenLaboratorioDialog(parent, o, null, guardada -> {
            recargarDatos();
            seleccionarOrden(guardada);
            JOptionPane.showMessageDialog(this,
                    "Resultados e informe de " + guardada.getIdOrden() + " actualizados con éxito.",
                    "Estudio Actualizado", JOptionPane.INFORMATION_MESSAGE);
        });
        dlg.setVisible(true);
    }

    private void cambiarEstadoOrden(OrdenLaboratorio o) {
        String[] opciones = {"Completado", "En Proceso", "Solicitado"};
        int sel = JOptionPane.showOptionDialog(
                this,
                "Seleccione el estado de procesamiento para el estudio " + o.getIdOrden() + ":",
                "Actualizar Estado de Estudio",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                Iconos.crearIconoMicroscopio(24, COLOR_AZUL_PRIMARIO),
                opciones,
                o.getEstado()
        );

        if (sel >= 0 && sel < opciones.length) {
            String nuevoEstado = opciones[sel];
            o.setEstado(nuevoEstado);
            if ("Completado".equalsIgnoreCase(nuevoEstado) && o.getFechaResultado() == null) {
                o.setFechaResultado(LocalDate.now());
            }
            repo.guardarOrdenLaboratorio(o);
            recargarDatos();
            seleccionarOrden(o);
            JOptionPane.showMessageDialog(this,
                    "Estado actualizado a: " + nuevoEstado,
                    "Estado Modificado", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void emitirInformeOficial(OrdenLaboratorio o) {
        String[][] datos = new String[][]{
                {"1", "Estudio Diagnóstico Solicitado", o.getTipoEstudio(), "Categoría: " + o.getCategoria() + " · Prioridad: " + o.getPrioridad()},
                {"2", "Veterinario Tratante Solicitante", o.getVeterinarioSolicitante(), "Motivo / Sospecha: " + o.getMotivoSospecha()},
                {"3", "Valores Cuantitativos / Parámetros", o.getResultadoValores(), "Procesado por: " + o.getResponsableProcesamiento()},
                {"4", "Informe Descriptivo e Interpretación", o.getInformeDetallado(), "Fecha Entrega: " + o.getFechaResultadoFormateada()},
                {"5", "Conclusión y Diagnóstico Definitivo", o.getConclusionesRecomendaciones(), "Dictamen médico validado"}
        };

        Ui.mostrarVisorReporte(
                (java.awt.Frame) SwingUtilities.getWindowAncestor(this),
                "INFORME OFICIAL DE LABORATORIO E IMÁGENES DIAGNÓSTICAS",
                "Paciente: " + o.getNombreMascota() + " (" + o.getEspecieRaza() + ") | Tutor: " + o.getNombreTutor() + " | Fecha Solicitud: " + o.getFechaSolicitudFormateada(),
                "Estudio: " + o.getTipoEstudio() + " | Estado: " + o.getEstado() + " | Responsable: " + o.getResponsableProcesamiento(),
                new String[]{"Ítem", "Parámetro / Concepto", "Detalle de Resultados", "Validación"},
                datos,
                "CENTRO DIAGNÓSTICO INTEGRAL · HAPPY PETS HOSPITAL VETERINARIO",
                "InformeDiagnostico_" + o.getNombreMascota()
        );
    }

    public void recargarDatos() {
        filtrarDatos();
    }

    private void filtrarDatos() {
        List<OrdenLaboratorio> todas = repo.getOrdenesLaboratorio();
        String fCat = cbFiltroCategoria != null ? (String) cbFiltroCategoria.getSelectedItem() : "Todas";
        String fEst = cbFiltroEstado != null ? (String) cbFiltroEstado.getSelectedItem() : "Todos";
        String q = (txtBuscar != null && txtBuscar.getText() != null) ? txtBuscar.getText().trim().toLowerCase() : "";

        listaActual = todas.stream().filter(o -> {
            if (fCat != null && !fCat.startsWith("Todas") && !o.getCategoria().equalsIgnoreCase(fCat)) return false;
            if (fEst != null && !fEst.startsWith("Todos") && !o.getEstado().equalsIgnoreCase(fEst)) return false;
            if (!q.isEmpty()) {
                boolean match = o.getIdOrden().toLowerCase().contains(q) ||
                                o.getNombreMascota().toLowerCase().contains(q) ||
                                o.getNombreTutor().toLowerCase().contains(q) ||
                                o.getTipoEstudio().toLowerCase().contains(q) ||
                                o.getVeterinarioSolicitante().toLowerCase().contains(q);
                if (!match) return false;
            }
            return true;
        }).toList();

        modeloHistorial.setRowCount(0);
        for (OrdenLaboratorio o : listaActual) {
            modeloHistorial.addRow(new Object[]{
                    o,
                    o,
                    o,
                    o,
                    o.getPrioridad(),
                    o.getEstado()
            });
        }

        lblContadorHistorial.setText(listaActual.size() + " estudios registrados en la base diagnóstica");

        // KPIs
        long labs = todas.stream().filter(o -> o.getCategoria().toLowerCase().contains("laboratorio")).count();
        long imgs = todas.stream().filter(o -> o.getCategoria().toLowerCase().contains("imágenes")).count();
        long compl = todas.stream().filter(o -> "Completado".equalsIgnoreCase(o.getEstado())).count();

        lblKpiTotalOrdenes.setText(String.valueOf(todas.size()));
        lblKpiLaboratorio.setText(String.valueOf(labs));
        lblKpiImagenes.setText(String.valueOf(imgs));
        lblKpiCompletados.setText(String.valueOf(compl));

        if (ordenSeleccionada != null) {
            seleccionarOrden(ordenSeleccionada);
        } else if (!listaActual.isEmpty()) {
            seleccionarFila(0);
        } else {
            ordenSeleccionada = null;
            actualizarPanelDetalle();
        }
    }

    private void seleccionarOrden(OrdenLaboratorio o) {
        if (o == null) return;
        for (int i = 0; i < listaActual.size(); i++) {
            if (listaActual.get(i).getIdOrden().equalsIgnoreCase(o.getIdOrden())) {
                seleccionarFila(i);
                return;
            }
        }
        actualizarPanelDetalle();
    }

    private void seleccionarFila(int fila) {
        if (fila >= 0 && fila < tablaHistorial.getRowCount()) {
            tablaHistorial.setRowSelectionInterval(fila, fila);
            tablaHistorial.scrollRectToVisible(tablaHistorial.getCellRect(fila, 0, true));
            ordenSeleccionada = listaActual.get(fila);
        } else {
            ordenSeleccionada = null;
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
    // INNER CLASSES Y RENDERIZADORES
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

    private static class CodigoFechaOrdenRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JPanel p = new JPanel(new BorderLayout(0, 2));
            p.setOpaque(true);
            p.setBackground(isSelected ? table.getSelectionBackground() : (row % 2 == 0 ? Color.WHITE : new Color(249, 250, 251)));
            p.setBorder(new EmptyBorder(6, 8, 6, 6));

            if (value instanceof OrdenLaboratorio o) {
                JLabel lblCod = new JLabel(o.getIdOrden());
                lblCod.setFont(new Font("Segoe UI", Font.BOLD, 12));
                lblCod.setForeground(isSelected ? table.getSelectionForeground() : COLOR_AZUL_PRIMARIO);

                JLabel lblFec = new JLabel(o.getFechaSolicitudFormateada());
                lblFec.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                lblFec.setForeground(isSelected ? table.getSelectionForeground() : COLOR_TEXTO_MUTED);

                p.add(lblCod, BorderLayout.NORTH);
                p.add(lblFec, BorderLayout.CENTER);
            }
            return p;
        }
    }

    private static class PacienteOrdenRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JPanel p = new JPanel(new BorderLayout(8, 0));
            p.setOpaque(true);
            p.setBackground(isSelected ? table.getSelectionBackground() : (row % 2 == 0 ? Color.WHITE : new Color(249, 250, 251)));
            p.setBorder(new EmptyBorder(6, 6, 6, 6));

            if (value instanceof OrdenLaboratorio o) {
                boolean esImg = o.getCategoria().contains("Imágenes");
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
                        int x = (w - d) / 2;
                        int y = (h - d) / 2;

                        g2.setColor(esImg ? new Color(243, 232, 255) : new Color(224, 242, 254));
                        g2.fillOval(x, y, d, d);
                        g2.setColor(esImg ? new Color(147, 51, 234) : COLOR_AZUL_PRIMARIO);
                        g2.drawOval(x, y, d, d);

                        String ini = o.getNombreMascota().substring(0, 1).toUpperCase();
                        g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
                        g2.setColor(esImg ? new Color(147, 51, 234) : COLOR_AZUL_PRIMARIO);
                        java.awt.FontMetrics fm = g2.getFontMetrics();
                        int tx = (w - fm.stringWidth(ini)) / 2;
                        int ty = (h + fm.getAscent() - fm.getDescent()) / 2;
                        g2.drawString(ini, tx, ty);
                        g2.dispose();
                    }
                };
                lblAv.setPreferredSize(new Dimension(30, 30));
                pAvWrap.add(lblAv);

                JPanel pDat = new JPanel(new BorderLayout(0, 2));
                pDat.setOpaque(false);
                JLabel lblNom = new JLabel(o.getNombreMascota());
                lblNom.setFont(new Font("Segoe UI", Font.BOLD, 12));
                lblNom.setForeground(isSelected ? table.getSelectionForeground() : COLOR_TEXTO_TITULO);

                JLabel lblEsp = new JLabel(o.getEspecieRaza());
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

    private static class TutorOrdenRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JPanel p = new JPanel(new BorderLayout(0, 2));
            p.setOpaque(true);
            p.setBackground(isSelected ? table.getSelectionBackground() : (row % 2 == 0 ? Color.WHITE : new Color(249, 250, 251)));
            p.setBorder(new EmptyBorder(6, 6, 6, 6));

            if (value instanceof OrdenLaboratorio o) {
                JLabel lblTutor = new JLabel(o.getNombreTutor());
                lblTutor.setFont(new Font("Segoe UI", Font.BOLD, 11));
                lblTutor.setForeground(isSelected ? table.getSelectionForeground() : COLOR_TEXTO_TITULO);

                JLabel lblTel = new JLabel(o.getTelefonoTutor());
                lblTel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                lblTel.setForeground(isSelected ? table.getSelectionForeground() : COLOR_TEXTO_MUTED);

                p.add(lblTutor, BorderLayout.NORTH);
                p.add(lblTel, BorderLayout.CENTER);
            }
            return p;
        }
    }

    private static class EstudioOrdenRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JPanel p = new JPanel(new BorderLayout(0, 2));
            p.setOpaque(true);
            p.setBackground(isSelected ? table.getSelectionBackground() : (row % 2 == 0 ? Color.WHITE : new Color(249, 250, 251)));
            p.setBorder(new EmptyBorder(6, 6, 6, 6));

            if (value instanceof OrdenLaboratorio o) {
                JLabel lblEst = new JLabel(o.getTipoEstudio());
                lblEst.setFont(new Font("Segoe UI", Font.BOLD, 11));
                lblEst.setForeground(isSelected ? table.getSelectionForeground() : new Color(15, 118, 110));

                JLabel lblCat = new JLabel(o.getCategoria() + " · " + o.getVeterinarioSolicitante());
                lblCat.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                lblCat.setForeground(isSelected ? table.getSelectionForeground() : COLOR_TEXTO_MUTED);

                p.add(lblEst, BorderLayout.NORTH);
                p.add(lblCat, BorderLayout.CENTER);
            }
            return p;
        }
    }

    private static class BadgePrioridadRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 8));
            p.setOpaque(true);
            p.setBackground(isSelected ? table.getSelectionBackground() : (row % 2 == 0 ? Color.WHITE : new Color(249, 250, 251)));

            String st = String.valueOf(value);
            Color bg;
            Color fg;
            if ("Urgente".equalsIgnoreCase(st) || "Emergencia".equalsIgnoreCase(st)) {
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
            badge.setBorder(new EmptyBorder(3, 8, 3, 8));

            p.add(badge);
            return p;
        }
    }

    private static class BadgeEstadoOrdenRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 8));
            p.setOpaque(true);
            p.setBackground(isSelected ? table.getSelectionBackground() : (row % 2 == 0 ? Color.WHITE : new Color(249, 250, 251)));

            String st = String.valueOf(value);
            Color bg;
            Color fg;
            if ("Completado".equalsIgnoreCase(st)) {
                bg = new Color(220, 252, 231);
                fg = new Color(22, 163, 74);
            } else if ("En Proceso".equalsIgnoreCase(st)) {
                bg = new Color(224, 242, 254);
                fg = new Color(2, 132, 199);
            } else {
                bg = new Color(254, 243, 199);
                fg = new Color(217, 119, 6);
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
            badge.setBorder(new EmptyBorder(3, 8, 3, 8));

            p.add(badge);
            return p;
        }
    }
}
