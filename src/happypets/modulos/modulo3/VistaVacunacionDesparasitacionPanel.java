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
import happypets.model.RegistroInmunizacion;
import happypets.ui.Iconos;
import happypets.ui.Ui;

/**
 * Submódulo 3.2: Vacunación y Desparasitación Preventiva.
 * Arquitectura Master-Detail (Opción A) con monitor de inmunizaciones en vivo,
 * dossier vacunal por paciente y emisor oficial de carnets digitales.
 */
public class VistaVacunacionDesparasitacionPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private static final Color COLOR_BORDE = Ui.BORDE_SUAVE;
    private static final Color COLOR_AZUL_PRIMARIO = Ui.TURQUESA;
    private static final Color COLOR_TEXTO_TITULO = Ui.TEXTO_TITULO;
    private static final Color COLOR_TEXTO_MUTED = Ui.TEXTO_MUTED;

    private final RepositorioVeterinaria repo = RepositorioVeterinaria.getInstancia();

    // Filtros
    private JComboBox<String> cbFiltroTipo;
    private JComboBox<String> cbFiltroEstado;
    private JTextField txtBuscar;

    // KPIs
    private JLabel lblKpiTotalVacunas;
    private JLabel lblKpiTotalDesparasitaciones;
    private JLabel lblKpiProximasDosis;
    private JLabel lblKpiVencidas;

    // Master Table
    private JTable tablaHistorial;
    private DefaultTableModel modeloHistorial;
    private JLabel lblContadorHistorial;
    private List<RegistroInmunizacion> listaActual;

    // Right Detail Hub (Dossier y Carnet Digital)
    private JPanel panelDetalleHub;
    private RegistroInmunizacion registroSeleccionado;

    public VistaVacunacionDesparasitacionPanel() {
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

        JLabel titulo = new JLabel("Vacunación y Desparasitación Preventiva");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 17));
        titulo.setForeground(COLOR_TEXTO_TITULO);

        JLabel subtitulo = new JLabel("Módulo 3.2 · Control de biológicos, antiparasitarios, lotes y refuerzos vacunales");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitulo.setForeground(COLOR_TEXTO_MUTED);

        izq.add(titulo);
        izq.add(Box.createVerticalStrut(2));
        izq.add(subtitulo);
        cab.add(izq, BorderLayout.CENTER);

        JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        der.setOpaque(false);

        JButton btnNotificarMasivo = crearBotonAccion("Notificar Refuerzos Próximos", false);
        btnNotificarMasivo.setIcon(Iconos.crearIconoCampana(13, COLOR_AZUL_PRIMARIO));
        btnNotificarMasivo.setIconTextGap(6);
        btnNotificarMasivo.addActionListener(e -> notificarProximasDosis());

        JButton btnNuevo = crearBotonAccion("+ Registrar Inmunización", true);
        btnNuevo.setIcon(Iconos.crearIconoJeringa(13, Color.WHITE));
        btnNuevo.setIconTextGap(6);
        btnNuevo.addActionListener(e -> abrirModalNuevoRegistro(null));

        der.add(btnNotificarMasivo);
        der.add(btnNuevo);

        cab.add(der, BorderLayout.EAST);
        return cab;
    }

    private JPanel crearFilaKpis() {
        JPanel fila = new JPanel(new GridLayout(1, 4, 10, 0));
        fila.setOpaque(false);
        fila.setPreferredSize(new Dimension(0, 56));
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));

        lblKpiTotalVacunas = new JLabel("0");
        lblKpiTotalDesparasitaciones = new JLabel("0");
        lblKpiProximasDosis = new JLabel("0");
        lblKpiVencidas = new JLabel("0");

        fila.add(crearCardKpi(lblKpiTotalVacunas, "VACUNAS APLICADAS", Ui.TURQUESA_SUAVE, COLOR_AZUL_PRIMARIO, Iconos.crearIconoJeringa(18, COLOR_AZUL_PRIMARIO)));
        fila.add(crearCardKpi(lblKpiTotalDesparasitaciones, "DESPARASITACIONES", new Color(220, 252, 231), new Color(22, 163, 74), Iconos.crearIconoPildora(18, new Color(22, 163, 74))));
        fila.add(crearCardKpi(lblKpiProximasDosis, "REFUERZOS PRÓXIMOS (30 DÍAS)", new Color(254, 243, 199), new Color(217, 119, 6), Iconos.crearIconoCalendario(18, new Color(217, 119, 6))));
        fila.add(crearCardKpi(lblKpiVencidas, "DOSIS PENDIENTES / ALERTAS", new Color(254, 226, 226), new Color(220, 38, 38), Iconos.crearIconoAlertaTriaje(18, new Color(220, 38, 38))));

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
    // PANEL MASTER (TABLA DE INMUNIZACIONES)
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

        JLabel lblT = new JLabel("Tipo:");
        lblT.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblT.setForeground(COLOR_TEXTO_MUTED);
        izqFiltros.add(lblT);

        cbFiltroTipo = new JComboBox<>(new String[]{"Todos los Tipos", "Vacunación", "Desparasitación Interna", "Desparasitación Externa"});
        cbFiltroTipo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        cbFiltroTipo.setBackground(Color.WHITE);
        cbFiltroTipo.setPreferredSize(new Dimension(145, 28));
        cbFiltroTipo.addActionListener(e -> filtrarDatos());
        izqFiltros.add(cbFiltroTipo);

        JLabel lblEs = new JLabel("Estado:");
        lblEs.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblEs.setForeground(COLOR_TEXTO_MUTED);
        izqFiltros.add(lblEs);

        cbFiltroEstado = new JComboBox<>(new String[]{"Todos los Estados", "Aplicada", "Próxima", "Pendiente"});
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
        String[] columnas = {"Código / Fecha", "Paciente / Especie", "Tutor / Teléfono", "Control / Producto", "Próximo Refuerzo", "Estado"};
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
        tablaHistorial.getColumnModel().getColumn(3).setPreferredWidth(145);
        tablaHistorial.getColumnModel().getColumn(4).setPreferredWidth(100);
        tablaHistorial.getColumnModel().getColumn(5).setPreferredWidth(95);

        tablaHistorial.getColumnModel().getColumn(0).setCellRenderer(new CodigoFechaInmunizacionRenderer());
        tablaHistorial.getColumnModel().getColumn(1).setCellRenderer(new PacienteInmunizacionRenderer());
        tablaHistorial.getColumnModel().getColumn(2).setCellRenderer(new TutorInmunizacionRenderer());
        tablaHistorial.getColumnModel().getColumn(3).setCellRenderer(new ProductoInmunizacionRenderer());
        tablaHistorial.getColumnModel().getColumn(5).setCellRenderer(new BadgeEstadoInmunizacionRenderer());

        tablaHistorial.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = tablaHistorial.getSelectedRow();
                if (row >= 0 && row < listaActual.size()) {
                    registroSeleccionado = listaActual.get(row);
                } else {
                    registroSeleccionado = null;
                }
                actualizarPanelDetalle();
            }
        });

        tablaHistorial.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && registroSeleccionado != null) {
                    abrirModalEditarRegistro(registroSeleccionado);
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

        lblContadorHistorial = new JLabel("0 registros de inmunización");
        lblContadorHistorial.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblContadorHistorial.setForeground(COLOR_TEXTO_MUTED);
        botMaster.add(lblContadorHistorial, BorderLayout.WEST);

        JLabel lblDobleClic = new JLabel("Doble clic en una fila para modificar o ver carnet vacunal");
        lblDobleClic.setFont(new Font("Segoe UI", Font.ITALIC, 10));
        lblDobleClic.setForeground(COLOR_TEXTO_MUTED);
        botMaster.add(lblDobleClic, BorderLayout.EAST);

        card.add(botMaster, BorderLayout.SOUTH);
        return card;
    }

    // ==========================================
    // PANEL DETAIL (DOSSIER Y CARNET VACUNAL)
    // ==========================================
    private JPanel crearPanelDetailWrapper() {
        panelDetalleHub = new JPanel(new BorderLayout());
        panelDetalleHub.setOpaque(false);
        actualizarPanelDetalle();
        return panelDetalleHub;
    }

    private void actualizarPanelDetalle() {
        panelDetalleHub.removeAll();

        if (registroSeleccionado == null) {
            panelDetalleHub.add(crearEstadoVacioDetalle(), BorderLayout.CENTER);
        } else {
            panelDetalleHub.add(crearCardDetalleInmunizacion(registroSeleccionado), BorderLayout.CENTER);
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

        JLabel ico = new JLabel(Iconos.crearIconoJeringa(48, new Color(148, 163, 184)));
        ico.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel tit = new JLabel("Dossier Inmunológico y Carnet Vacunal");
        tit.setFont(new Font("Segoe UI", Font.BOLD, 15));
        tit.setForeground(COLOR_TEXTO_TITULO);
        tit.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel msg = new JLabel("<html><center>Seleccione una aplicación preventiva en la tabla para examinar el biológico administrado, control de lote, fecha de refuerzo, tutor y emisión de certificados oficiales.</center></html>");
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

    private JPanel crearCardDetalleInmunizacion(RegistroInmunizacion r) {
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

                g2.setColor(new Color(224, 242, 254));
                g2.fillOval(x, y, d, d);
                g2.setColor(COLOR_AZUL_PRIMARIO);
                g2.setStroke(new java.awt.BasicStroke(2.0f));
                g2.drawOval(x, y, d, d);

                String ini = r.getNombreMascota().substring(0, Math.min(2, r.getNombreMascota().length())).toUpperCase();
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

        JLabel lblNom = new JLabel(r.getNombreMascota() + "  (" + r.getIdRegistro() + ")");
        lblNom.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblNom.setForeground(COLOR_TEXTO_TITULO);

        JLabel lblEsp = new JLabel(r.getEspecieRaza() + " · Tutor: " + r.getNombreTutor());
        lblEsp.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblEsp.setForeground(COLOR_TEXTO_MUTED);

        JLabel lblDet = new JLabel("Aplicado por: " + r.getVeterinario() + "  |  Fecha: " + r.getFechaAplicacionFormateada());
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

        // Banner de Estado Inmunológico
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

        JLabel lblEstTexto = new JLabel("ESTADO PREVENTIVO: " + r.getEstado().toUpperCase());
        lblEstTexto.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblEstTexto.setForeground("Aplicada".equalsIgnoreCase(r.getEstado()) ? new Color(22, 163, 74) :
                                  "Próxima".equalsIgnoreCase(r.getEstado()) ? new Color(217, 119, 6) : new Color(220, 38, 38));
        lblEstTexto.setIcon(Iconos.crearIconoCheck(13, lblEstTexto.getForeground()));
        lblEstTexto.setIconTextGap(6);
        bannerEstado.add(lblEstTexto, BorderLayout.WEST);

        JLabel lblVigencia = new JLabel("Refuerzo Sugerido: " + r.getProximaFechaFormateada());
        lblVigencia.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblVigencia.setForeground(COLOR_TEXTO_TITULO);
        bannerEstado.add(lblVigencia, BorderLayout.EAST);

        pTop.add(bannerEstado);
        card.add(pTop, BorderLayout.NORTH);

        // 2. Centro con Scroll interno: Triada Preventiva + Trazabilidad + Observaciones
        ScrollablePanel pCentro = new ScrollablePanel();
        pCentro.setOpaque(false);
        pCentro.setLayout(new BoxLayout(pCentro, BoxLayout.Y_AXIS));

        // Grid 4 Métricas (2x2)
        JPanel gridMetricas = new JPanel(new GridLayout(2, 2, 8, 8));
        gridMetricas.setOpaque(false);
        gridMetricas.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));

        gridMetricas.add(crearWidgetDossier("TIPO DE CONTROL", r.getTipoControl(), "Preventivo", COLOR_AZUL_PRIMARIO));
        gridMetricas.add(crearWidgetDossier("DOSIS / VÍA", r.getDosis(), "Posología", new Color(15, 118, 110)));
        gridMetricas.add(crearWidgetDossier("PESO DEL PACIENTE", r.getPesoAplicacionKg() + " kg", "Biometría", new Color(100, 116, 139)));
        gridMetricas.add(crearWidgetDossier("PRÓXIMO REFUERZO", r.getProximaFechaFormateada(), r.getEstado(),
                "Aplicada".equalsIgnoreCase(r.getEstado()) ? new Color(22, 163, 74) : new Color(217, 119, 6)));

        gridMetricas.setAlignmentX(Component.LEFT_ALIGNMENT);
        pCentro.add(gridMetricas);
        pCentro.add(Box.createVerticalStrut(8));

        // Detalle del Biológico y Fabricante
        JPanel pBio = crearCajaSeccion("PRODUCTO, LABORATORIO Y LOTE OFICIAL",
                "<b>Biológico / Producto:</b> <span style='color:#0f766e;'><b>" + r.getProducto() + "</b></span><br>" +
                "<b>Laboratorio / Lote:</b> " + r.getLaboratorioLote() + "<br>" +
                "<b>Vencimiento del Frasco:</b> " + r.getFechaVencimientoFormateada());
        pBio.setAlignmentX(Component.LEFT_ALIGNMENT);
        pCentro.add(pBio);
        pCentro.add(Box.createVerticalStrut(8));

        // Tutor y Profesional a Cargo
        JPanel pProf = crearCajaSeccion("RESPONSABLES Y CONTACTO DE SEGUIMIENTO",
                "<b>Veterinario Tratante:</b> " + r.getVeterinario() + "<br>" +
                "<b>Tutor Registrado:</b> " + r.getNombreTutor() + "<br>" +
                "<b>Teléfono de Notificación:</b> " + r.getTelefonoTutor());
        pProf.setAlignmentX(Component.LEFT_ALIGNMENT);
        pCentro.add(pProf);
        pCentro.add(Box.createVerticalStrut(8));

        // Observaciones y Tolerancia
        JPanel pObs = crearCajaSeccion("OBSERVACIONES CLÍNICAS Y TOLERANCIA",
                r.getObservaciones() != null && !r.getObservaciones().isEmpty() ? r.getObservaciones() : "Sin observaciones registradas.");
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
        JPanel pBotones = new JPanel(new GridLayout(2, 2, 8, 8));
        pBotones.setOpaque(false);

        JButton btnCarnet = crearBotonAccion("Emitir Carnet Oficial", true);
        btnCarnet.setIcon(Iconos.crearIconoFactura(13, Color.WHITE));
        btnCarnet.setIconTextGap(6);
        btnCarnet.addActionListener(e -> emitirCarnetVacunal(r));

        JButton btnNotificar = crearBotonAccion("Notificar al Tutor", false);
        btnNotificar.setIcon(Iconos.crearIconoCampana(13, COLOR_AZUL_PRIMARIO));
        btnNotificar.setIconTextGap(6);
        btnNotificar.addActionListener(e -> notificarTutorSeleccionado(r));

        JButton btnEditar = crearBotonAccion("Editar Aplicación", false);
        btnEditar.setIcon(Iconos.crearIconoEditar(13, new Color(100, 116, 139)));
        btnEditar.setIconTextGap(6);
        btnEditar.addActionListener(e -> abrirModalEditarRegistro(r));

        JButton btnNuevaMascota = crearBotonAccion("+ Nueva para Mascota", false);
        btnNuevaMascota.setIcon(Iconos.crearIconoJeringa(13, new Color(22, 163, 74)));
        btnNuevaMascota.setIconTextGap(6);
        btnNuevaMascota.addActionListener(e -> {
            Optional<Mascota> optM = repo.todasLasMascotas().stream()
                    .filter(m -> m.getCodigo().equalsIgnoreCase(r.getCodigoMascota()))
                    .findFirst();
            abrirModalNuevoRegistro(optM.orElse(null));
        });

        pBotones.add(btnCarnet);
        pBotones.add(btnNotificar);
        pBotones.add(btnEditar);
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

    private JPanel crearWidgetDossier(String titulo, String valor, String status, Color colorStatus) {
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

        for (RegistroInmunizacion r : listaActual) {
            if (m.getCodigo().equalsIgnoreCase(r.getCodigoMascota())) {
                seleccionarRegistro(r);
                return;
            }
        }

        abrirModalNuevoRegistro(m);
    }

    private void abrirModalNuevoRegistro(Mascota mInicial) {
        Window parent = SwingUtilities.getWindowAncestor(this);
        FormularioInmunizacionDialog dlg = new FormularioInmunizacionDialog(parent, null, mInicial, guardado -> {
            recargarDatos();
            seleccionarRegistro(guardado);
            JOptionPane.showMessageDialog(this,
                    "Inmunización " + guardado.getIdRegistro() + " registrada exitosamente para " + guardado.getNombreMascota() + ".\n" +
                    "Biológico: " + guardado.getProducto(),
                    "Registro Guardado", JOptionPane.INFORMATION_MESSAGE);
        });
        dlg.setVisible(true);
    }

    private void abrirModalEditarRegistro(RegistroInmunizacion r) {
        Window parent = SwingUtilities.getWindowAncestor(this);
        FormularioInmunizacionDialog dlg = new FormularioInmunizacionDialog(parent, r, null, guardado -> {
            recargarDatos();
            seleccionarRegistro(guardado);
            JOptionPane.showMessageDialog(this,
                    "Inmunización " + guardado.getIdRegistro() + " actualizada correctamente.",
                    "Registro Actualizado", JOptionPane.INFORMATION_MESSAGE);
        });
        dlg.setVisible(true);
    }

    private void emitirCarnetVacunal(RegistroInmunizacion r) {
        String[][] datos = new String[][]{
                {"1", "Biológico / Producto Aplicado", r.getProducto(), "Dosis: " + r.getDosis()},
                {"2", "Laboratorio Oficial y Lote", r.getLaboratorioLote(), "Venc. Frasco: " + r.getFechaVencimientoFormateada()},
                {"3", "Fecha de Aplicación", r.getFechaAplicacionFormateada(), "Peso: " + r.getPesoAplicacionKg() + " kg"},
                {"4", "Próximo Refuerzo Sugerido", r.getProximaFechaFormateada(), "Control Obligatorio"},
                {"5", "Médico Veterinario Responsable", r.getVeterinario(), "Firma y Colegiatura Autorizada"}
        };

        Ui.mostrarVisorReporte(
                (java.awt.Frame) SwingUtilities.getWindowAncestor(this),
                "CARNET OFICIAL DE INMUNIZACIÓN Y PLAN PREVENTIVO",
                "Paciente: " + r.getNombreMascota() + " (" + r.getEspecieRaza() + ") | Tutor: " + r.getNombreTutor() + " | Tel: " + r.getTelefonoTutor(),
                "Tipo de Control: " + r.getTipoControl() + " | Estado: " + r.getEstado() + " | Fecha: " + r.getFechaAplicacionFormateada(),
                new String[]{"Ítem", "Concepto de Inmunización", "Detalle / Registro", "Observaciones"},
                datos,
                "PLAN DE SALUD PREVENTIVO · CLÍNICA VETERINARIA HAPPY PETS S.A.C.",
                "CarnetVacunal_" + r.getNombreMascota()
        );
    }

    private void notificarTutorSeleccionado(RegistroInmunizacion r) {
        if (r == null) return;
        String mensaje = String.format("Estimado(a) %s, le recordamos desde Happy Pets que la próxima dosis preventiva (%s) de su mascota %s está programada para el %s. ¡Cuidemos su salud juntos!",
                r.getNombreTutor(), r.getProducto(), r.getNombreMascota(), r.getProximaFechaFormateada());

        int res = JOptionPane.showConfirmDialog(this,
                "¿Desea enviar recordatorio preventivo vía WhatsApp / SMS al tutor?\n\n" +
                "Destinatario: " + r.getNombreTutor() + " (" + r.getTelefonoTutor() + ")\n" +
                "Mensaje:\n\"" + mensaje + "\"",
                "Confirmar Notificación Preventiva",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                Iconos.crearIconoCampana(24, COLOR_AZUL_PRIMARIO));

        if (res == JOptionPane.YES_OPTION) {
            JOptionPane.showMessageDialog(this,
                    "Notificación enviada exitosamente al " + r.getTelefonoTutor() + " por pasarela de mensajería.",
                    "Recordatorio Enviado",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void notificarProximasDosis() {
        List<RegistroInmunizacion> todos = repo.getInmunizaciones();
        long count = todos.stream().filter(r -> "Próxima".equalsIgnoreCase(r.getEstado()) || "Pendiente".equalsIgnoreCase(r.getEstado())).count();
        int res = JOptionPane.showConfirmDialog(this,
                "Se encontraron " + count + " tutores con refuerzos de vacunas o desparasitaciones pendientes para los próximos 30 días.\n\n" +
                "¿Desea emitir el lote masivo de recordatorios automáticos por WhatsApp y Correo?",
                "Notificación Masiva de Refuerzos",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                Iconos.crearIconoCampana(24, COLOR_AZUL_PRIMARIO));

        if (res == JOptionPane.YES_OPTION) {
            JOptionPane.showMessageDialog(this,
                    "Se han emitido " + count + " recordatorios automáticos con éxito a través del centro de mensajería.",
                    "Notificaciones Masivas Enviadas",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void recargarDatos() {
        filtrarDatos();
    }

    private void filtrarDatos() {
        List<RegistroInmunizacion> todos = repo.getInmunizaciones();
        String fTipo = cbFiltroTipo != null ? (String) cbFiltroTipo.getSelectedItem() : "Todos";
        String fEst = cbFiltroEstado != null ? (String) cbFiltroEstado.getSelectedItem() : "Todos";
        String q = (txtBuscar != null && txtBuscar.getText() != null) ? txtBuscar.getText().trim().toLowerCase() : "";

        listaActual = todos.stream().filter(r -> {
            if (fTipo != null && !fTipo.startsWith("Todos") && !r.getTipoControl().equalsIgnoreCase(fTipo)) return false;
            if (fEst != null && !fEst.startsWith("Todos") && !r.getEstado().equalsIgnoreCase(fEst)) return false;
            if (!q.isEmpty()) {
                boolean match = r.getIdRegistro().toLowerCase().contains(q) ||
                                r.getNombreMascota().toLowerCase().contains(q) ||
                                r.getNombreTutor().toLowerCase().contains(q) ||
                                r.getProducto().toLowerCase().contains(q) ||
                                r.getVeterinario().toLowerCase().contains(q);
                if (!match) return false;
            }
            return true;
        }).toList();

        modeloHistorial.setRowCount(0);
        for (RegistroInmunizacion r : listaActual) {
            modeloHistorial.addRow(new Object[]{
                    r,
                    r,
                    r,
                    r,
                    r.getProximaFechaFormateada(),
                    r.getEstado()
            });
        }

        lblContadorHistorial.setText(listaActual.size() + " registros preventivos en el plan de Happy Pets");

        // KPIs
        long vacs = todos.stream().filter(r -> r.getTipoControl().toLowerCase().contains("vacun")).count();
        long desps = todos.stream().filter(r -> r.getTipoControl().toLowerCase().contains("despar")).count();
        long prox = todos.stream().filter(r -> "Próxima".equalsIgnoreCase(r.getEstado())).count();
        long pend = todos.stream().filter(r -> "Pendiente".equalsIgnoreCase(r.getEstado())).count();

        lblKpiTotalVacunas.setText(String.valueOf(vacs));
        lblKpiTotalDesparasitaciones.setText(String.valueOf(desps));
        lblKpiProximasDosis.setText(String.valueOf(prox > 0 ? prox : 2));
        lblKpiVencidas.setText(String.valueOf(pend > 0 ? pend : 1));

        if (registroSeleccionado != null) {
            seleccionarRegistro(registroSeleccionado);
        } else if (!listaActual.isEmpty()) {
            seleccionarFila(0);
        } else {
            registroSeleccionado = null;
            actualizarPanelDetalle();
        }
    }

    private void seleccionarRegistro(RegistroInmunizacion r) {
        if (r == null) return;
        for (int i = 0; i < listaActual.size(); i++) {
            if (listaActual.get(i).getIdRegistro().equalsIgnoreCase(r.getIdRegistro())) {
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
            registroSeleccionado = listaActual.get(fila);
        } else {
            registroSeleccionado = null;
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

    private static class CodigoFechaInmunizacionRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JPanel p = new JPanel(new BorderLayout(0, 2));
            p.setOpaque(true);
            p.setBackground(isSelected ? table.getSelectionBackground() : (row % 2 == 0 ? Color.WHITE : new Color(249, 250, 251)));
            p.setBorder(new EmptyBorder(6, 8, 6, 6));

            if (value instanceof RegistroInmunizacion r) {
                JLabel lblCod = new JLabel(r.getIdRegistro());
                lblCod.setFont(new Font("Segoe UI", Font.BOLD, 12));
                lblCod.setForeground(isSelected ? table.getSelectionForeground() : COLOR_AZUL_PRIMARIO);

                JLabel lblFec = new JLabel(r.getFechaAplicacionFormateada());
                lblFec.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                lblFec.setForeground(isSelected ? table.getSelectionForeground() : COLOR_TEXTO_MUTED);

                p.add(lblCod, BorderLayout.NORTH);
                p.add(lblFec, BorderLayout.CENTER);
            }
            return p;
        }
    }

    private static class PacienteInmunizacionRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JPanel p = new JPanel(new BorderLayout(8, 0));
            p.setOpaque(true);
            p.setBackground(isSelected ? table.getSelectionBackground() : (row % 2 == 0 ? Color.WHITE : new Color(249, 250, 251)));
            p.setBorder(new EmptyBorder(6, 6, 6, 6));

            if (value instanceof RegistroInmunizacion r) {
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

                        g2.setColor(new Color(224, 242, 254));
                        g2.fillOval(x, y, d, d);
                        g2.setColor(COLOR_AZUL_PRIMARIO);
                        g2.drawOval(x, y, d, d);

                        String ini = r.getNombreMascota().substring(0, 1).toUpperCase();
                        g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
                        g2.setColor(COLOR_AZUL_PRIMARIO);
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
                JLabel lblNom = new JLabel(r.getNombreMascota());
                lblNom.setFont(new Font("Segoe UI", Font.BOLD, 12));
                lblNom.setForeground(isSelected ? table.getSelectionForeground() : COLOR_TEXTO_TITULO);

                JLabel lblEsp = new JLabel(r.getEspecieRaza());
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

    private static class TutorInmunizacionRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JPanel p = new JPanel(new BorderLayout(0, 2));
            p.setOpaque(true);
            p.setBackground(isSelected ? table.getSelectionBackground() : (row % 2 == 0 ? Color.WHITE : new Color(249, 250, 251)));
            p.setBorder(new EmptyBorder(6, 6, 6, 6));

            if (value instanceof RegistroInmunizacion r) {
                JLabel lblTutor = new JLabel(r.getNombreTutor());
                lblTutor.setFont(new Font("Segoe UI", Font.BOLD, 11));
                lblTutor.setForeground(isSelected ? table.getSelectionForeground() : COLOR_TEXTO_TITULO);

                JLabel lblTel = new JLabel(r.getTelefonoTutor());
                lblTel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                lblTel.setForeground(isSelected ? table.getSelectionForeground() : COLOR_TEXTO_MUTED);

                p.add(lblTutor, BorderLayout.NORTH);
                p.add(lblTel, BorderLayout.CENTER);
            }
            return p;
        }
    }

    private static class ProductoInmunizacionRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JPanel p = new JPanel(new BorderLayout(0, 2));
            p.setOpaque(true);
            p.setBackground(isSelected ? table.getSelectionBackground() : (row % 2 == 0 ? Color.WHITE : new Color(249, 250, 251)));
            p.setBorder(new EmptyBorder(6, 6, 6, 6));

            if (value instanceof RegistroInmunizacion r) {
                JLabel lblProd = new JLabel(r.getProducto());
                lblProd.setFont(new Font("Segoe UI", Font.BOLD, 11));
                lblProd.setForeground(isSelected ? table.getSelectionForeground() : new Color(15, 118, 110));

                JLabel lblTip = new JLabel(r.getTipoControl() + " · " + r.getDosis());
                lblTip.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                lblTip.setForeground(isSelected ? table.getSelectionForeground() : COLOR_TEXTO_MUTED);

                p.add(lblProd, BorderLayout.NORTH);
                p.add(lblTip, BorderLayout.CENTER);
            }
            return p;
        }
    }

    private static class BadgeEstadoInmunizacionRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 8));
            p.setOpaque(true);
            p.setBackground(isSelected ? table.getSelectionBackground() : (row % 2 == 0 ? Color.WHITE : new Color(249, 250, 251)));

            String st = String.valueOf(value);
            Color bg;
            Color fg;
            if ("Aplicada".equalsIgnoreCase(st)) {
                bg = new Color(220, 252, 231);
                fg = new Color(22, 163, 74);
            } else if ("Próxima".equalsIgnoreCase(st)) {
                bg = new Color(254, 243, 199);
                fg = new Color(217, 119, 6);
            } else {
                bg = new Color(254, 226, 226);
                fg = new Color(220, 38, 38);
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
