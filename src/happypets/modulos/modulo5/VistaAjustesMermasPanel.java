package happypets.modulos.modulo5;

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
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
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
import happypets.model.AjusteMerma;
import happypets.ui.Iconos;
import happypets.ui.Ui;

/**
 * Submódulo 5.4: Ajustes de Inventario y Registro de Mermas.
 * Arquitectura Master-Detail (58% / 42%) - Opción A Recomendada:
 * - Master Table: Registro de bajas por caducidad, roturas, fallas de cadena de frío y ajustes físicos.
 * - Live Merma Hub: Telemetría de pérdida financiera, trazabilidad de lote, causas sanitarias y Acta Oficial de Destrucción.
 */
public class VistaAjustesMermasPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private final RepositorioVeterinaria repo = RepositorioVeterinaria.getInstancia();

    // KPIs
    private JLabel lblKpiTotalAjustes;
    private JLabel lblKpiPerdidaTotal;
    private JLabel lblKpiLotesCaducados;
    private JLabel lblKpiRegularizaciones;

    // Master Table & Filtros
    private JTextField txtBuscar;
    private String tipoFiltroSeleccionado = "TODOS";
    private JPanel panelPillsTipo;
    private JTable tablaAjustes;
    private DefaultTableModel modeloAjustes;
    private List<AjusteMerma> listaAjustesFiltrada = new ArrayList<>();
    private AjusteMerma ajusteSeleccionado;

    // Live Merma Hub Componentes
    private JPanel panelHubDetalle;
    private JPanel panelHubVacio;
    private JPanel contenedorPadre;
    private JLabel lblHubAvatar;
    private JLabel lblHubProducto;
    private JLabel lblHubIdAjuste;
    private JLabel lblHubBadgeTipo;

    // Telemetría 2x2
    private JLabel lblHubPerdida;
    private JLabel lblHubCantidad;
    private JLabel lblHubCostoUnitario;
    private JLabel lblHubFecha;

    // Secciones descriptivas
    private JLabel lblHubCodigoProd;
    private JLabel lblHubLote;
    private JLabel lblHubAutorizadoPor;
    private JLabel lblHubEstado;
    private JTextArea txtHubMotivo;

    public VistaAjustesMermasPanel() {
        setLayout(new BorderLayout());
        setOpaque(false);

        // Panel Superior Fijo (Cabecera + KPIs)
        JPanel panelSuperior = new JPanel();
        panelSuperior.setOpaque(false);
        panelSuperior.setLayout(new BoxLayout(panelSuperior, BoxLayout.Y_AXIS));
        panelSuperior.setBorder(new EmptyBorder(12, 18, 10, 18));

        // 1. Cabecera Principal
        panelSuperior.add(crearCabeceraVista());
        panelSuperior.add(Box.createVerticalStrut(10));

        // 2. Fila de KPIs
        panelSuperior.add(crearFilaKpis());

        add(panelSuperior, BorderLayout.NORTH);

        // 3. Contenedor Master-Detail (58% / 42%)
        JPanel panelCentro = new JPanel(new BorderLayout());
        panelCentro.setOpaque(false);
        panelCentro.setBorder(new EmptyBorder(0, 18, 14, 18));
        panelCentro.add(crearSplitMasterDetail(), BorderLayout.CENTER);

        add(panelCentro, BorderLayout.CENTER);

        recargarDatos();
    }

    private JPanel crearCabeceraVista() {
        JPanel cab = new JPanel(new BorderLayout());
        cab.setOpaque(false);

        JPanel izq = new JPanel();
        izq.setOpaque(false);
        izq.setLayout(new BoxLayout(izq, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("Ajustes de Inventario y Registro de Mermas");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 17));
        titulo.setForeground(Ui.TEXTO_TITULO);

        JLabel subtitulo = new JLabel("Módulo 5.4 · Registro de bajas por caducidad, roturas accidentales, auditoría física y actas de descarte");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitulo.setForeground(Ui.TEXTO_MUTED);

        izq.add(titulo);
        izq.add(Box.createVerticalStrut(2));
        izq.add(subtitulo);
        cab.add(izq, BorderLayout.CENTER);

        JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        der.setOpaque(false);

        JLabel badgeMerma = new JLabel("Control Sanitario Activo · Happy Pets", SwingConstants.CENTER);
        badgeMerma.setFont(new Font("Segoe UI", Font.BOLD, 11));
        badgeMerma.setForeground(new Color(185, 28, 28));
        badgeMerma.setOpaque(true);
        badgeMerma.setBackground(new Color(254, 226, 226));
        badgeMerma.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(254, 202, 202), 1, true),
                new EmptyBorder(4, 10, 4, 10)
        ));
        der.add(badgeMerma);

        JButton btnNuevo = crearBotonAccion("Registrar Merma", Iconos.crearIconoAlertaMerma(14, Color.WHITE), true);
        btnNuevo.addActionListener(e -> abrirModalNuevaMerma());
        btnNuevo.setPreferredSize(new Dimension(165, 32));
        der.add(btnNuevo);

        JButton btnRefrescar = crearBotonAccion("Actualizar", false);
        btnRefrescar.addActionListener(e -> recargarDatos());
        btnRefrescar.setPreferredSize(new Dimension(100, 32));
        der.add(btnRefrescar);

        cab.add(der, BorderLayout.EAST);
        cab.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        return cab;
    }

    private JPanel crearFilaKpis() {
        JPanel fila = new JPanel(new GridLayout(1, 4, 12, 0));
        fila.setOpaque(false);

        lblKpiTotalAjustes = new JLabel("0", SwingConstants.LEFT);
        lblKpiPerdidaTotal = new JLabel("S/. 0.00", SwingConstants.LEFT);
        lblKpiLotesCaducados = new JLabel("0", SwingConstants.LEFT);
        lblKpiRegularizaciones = new JLabel("0", SwingConstants.LEFT);

        fila.add(crearTarjetaKpi("Mermas Registradas", lblKpiTotalAjustes, "Historial de bajas y descartes", new Color(14, 165, 233), Iconos.crearIconoDocumento(18, new Color(14, 165, 233))));
        fila.add(crearTarjetaKpi("Pérdida Financiera", lblKpiPerdidaTotal, "Valorización total de pérdidas", new Color(239, 68, 68), Iconos.crearIconoAlertaMerma(16, new Color(239, 68, 68))));
        fila.add(crearTarjetaKpi("Bajas por Vencimiento", lblKpiLotesCaducados, "Fármacos caducados retirados", new Color(245, 158, 11), Iconos.crearIconoCampana(16, new Color(245, 158, 11))));
        fila.add(crearTarjetaKpi("Ajustes Físicos", lblKpiRegularizaciones, "Regularización de inventario", new Color(147, 51, 234), Iconos.crearIconoCajaAlmacen(16, new Color(147, 51, 234))));

        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 74));
        return fila;
    }

    private JPanel crearTarjetaKpi(String titulo, JLabel lblValor, String subtitulo, Color acento, Icon icono) {
        JPanel card = new JPanel(new BorderLayout(8, 4)) {
            private static final long serialVersionUID = 1L;
            @Override
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

        JPanel izq = new JPanel();
        izq.setOpaque(false);
        izq.setLayout(new BoxLayout(izq, BoxLayout.Y_AXIS));

        JLabel t = new JLabel(titulo);
        t.setFont(new Font("Segoe UI", Font.BOLD, 10));
        t.setForeground(Ui.TEXTO_MUTED);

        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblValor.setForeground(Ui.TEXTO_TITULO);

        JLabel sub = new JLabel(subtitulo);
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        sub.setForeground(Ui.TEXTO_MUTED);

        izq.add(t);
        izq.add(Box.createVerticalStrut(2));
        izq.add(lblValor);
        izq.add(Box.createVerticalStrut(1));
        izq.add(sub);

        card.add(izq, BorderLayout.CENTER);

        if (icono != null) {
            JLabel lblIcono = new JLabel(icono);
            lblIcono.setVerticalAlignment(SwingConstants.TOP);
            card.add(lblIcono, BorderLayout.EAST);
        }

        return card;
    }

    private JPanel crearSplitMasterDetail() {
        JPanel split = new JPanel(new GridBagLayout());
        split.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;

        // Master Panel (54%)
        gbc.gridx = 0;
        gbc.weightx = 0.54;
        gbc.insets = new Insets(0, 0, 0, 12);
        split.add(crearPanelMaster(), gbc);

        // Detail Live Hub (46%)
        gbc.gridx = 1;
        gbc.weightx = 0.46;
        gbc.insets = new Insets(0, 0, 0, 0);
        JPanel panelDetail = crearPanelDetail();
        panelDetail.setPreferredSize(new Dimension(490, 560));
        panelDetail.setMinimumSize(new Dimension(450, 500));
        split.add(panelDetail, gbc);

        return split;
    }

    private JPanel crearPanelMaster() {
        JPanel card = new JPanel(new BorderLayout(0, 8)) {
            private static final long serialVersionUID = 1L;
            @Override
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
        card.setBorder(new EmptyBorder(10, 12, 10, 12));

        // Filtros Superiores
        JPanel panelFiltros = new JPanel();
        panelFiltros.setOpaque(false);
        panelFiltros.setLayout(new BoxLayout(panelFiltros, BoxLayout.Y_AXIS));

        JPanel filaBusqueda = new JPanel(new BorderLayout(8, 0));
        filaBusqueda.setOpaque(false);

        JLabel lblBuscar = new JLabel("Buscar:");
        lblBuscar.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblBuscar.setForeground(Ui.TEXTO_MUTED);
        filaBusqueda.add(lblBuscar, BorderLayout.WEST);

        txtBuscar = new JTextField();
        txtBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtBuscar.setPreferredSize(new Dimension(200, 28));
        txtBuscar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Ui.BORDE_INPUT, 1, true),
                new EmptyBorder(2, 8, 2, 8)
        ));
        txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { filtrarAjustes(); }
            @Override public void removeUpdate(DocumentEvent e) { filtrarAjustes(); }
            @Override public void changedUpdate(DocumentEvent e) { filtrarAjustes(); }
        });
        filaBusqueda.add(txtBuscar, BorderLayout.CENTER);
        panelFiltros.add(filaBusqueda);
        panelFiltros.add(Box.createVerticalStrut(8));

        // Pills de Tipo
        panelPillsTipo = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        panelPillsTipo.setOpaque(false);
        String[] tipos = {"TODOS", "Vencimiento", "Rotura", "Cadena de Frío", "Ajuste"};
        for (String t : tipos) {
            JButton pill = crearBotonPill(t, t.equalsIgnoreCase(tipoFiltroSeleccionado));
            pill.addActionListener(e -> {
                tipoFiltroSeleccionado = t;
                actualizarPills();
                filtrarAjustes();
            });
            panelPillsTipo.add(pill);
        }
        panelFiltros.add(panelPillsTipo);

        card.add(panelFiltros, BorderLayout.NORTH);

        // Tabla Master
        String[] columnas = {"ID Ajuste", "Fármaco / Producto", "Causal de Baja", "Cantidad", "Pérdida S/.", "Lote", "Fecha"};
        modeloAjustes = new DefaultTableModel(columnas, 0) {
            private static final long serialVersionUID = 1L;
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };

        tablaAjustes = new JTable(modeloAjustes);
        tablaAjustes.setRowHeight(48);
        tablaAjustes.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        tablaAjustes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaAjustes.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        tablaAjustes.getTableHeader().setBackground(new Color(248, 250, 252));
        tablaAjustes.getTableHeader().setForeground(new Color(71, 85, 105));
        tablaAjustes.setShowVerticalLines(false);
        tablaAjustes.setGridColor(new Color(241, 245, 249));

        // Anchos de columna
        tablaAjustes.getColumnModel().getColumn(0).setPreferredWidth(85);
        tablaAjustes.getColumnModel().getColumn(1).setPreferredWidth(175);
        tablaAjustes.getColumnModel().getColumn(2).setPreferredWidth(140);
        tablaAjustes.getColumnModel().getColumn(3).setPreferredWidth(60);
        tablaAjustes.getColumnModel().getColumn(4).setPreferredWidth(75);
        tablaAjustes.getColumnModel().getColumn(5).setPreferredWidth(80);
        tablaAjustes.getColumnModel().getColumn(6).setPreferredWidth(75);

        // Renderers
        tablaAjustes.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
            private static final long serialVersionUID = 1L;
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                l.setFont(new Font("Segoe UI", Font.BOLD, 11));
                l.setForeground(isSelected ? table.getSelectionForeground() : Ui.TURQUESA_PROFUNDO);
                l.setHorizontalAlignment(SwingConstants.CENTER);
                return l;
            }
        });

        tablaAjustes.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
            private static final long serialVersionUID = 1L;
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                JPanel cell = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
                cell.setOpaque(true);
                cell.setBackground(isSelected ? table.getSelectionBackground() : (row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252)));

                String nombre = value != null ? value.toString() : "";
                String letra = !nombre.isEmpty() ? nombre.substring(0, 1).toUpperCase() : "A";

                JLabel avatar = new JLabel(letra, SwingConstants.CENTER) {
                    private static final long serialVersionUID = 1L;
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(new Color(254, 226, 226));
                        g2.fillOval(0, 0, getWidth() - 1, getHeight() - 1);
                        g2.setColor(new Color(254, 202, 202));
                        g2.drawOval(0, 0, getWidth() - 1, getHeight() - 1);
                        super.paintComponent(g);
                        g2.dispose();
                    }
                };
                avatar.setPreferredSize(new Dimension(30, 30));
                avatar.setFont(new Font("Segoe UI", Font.BOLD, 11));
                avatar.setForeground(Ui.COLOR_PELIGRO);

                JLabel lblNombre = new JLabel(nombre);
                lblNombre.setFont(new Font("Segoe UI", Font.BOLD, 11));
                lblNombre.setForeground(isSelected ? table.getSelectionForeground() : Ui.TEXTO_TITULO);

                cell.add(avatar);
                cell.add(lblNombre);
                return cell;
            }
        });

        tablaAjustes.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            private static final long serialVersionUID = 1L;
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                String tipo = value != null ? value.toString() : "";
                if (tipo.contains("Vencimiento") || tipo.contains("Frío")) {
                    l.setForeground(Ui.COLOR_PELIGRO);
                    l.setFont(new Font("Segoe UI", Font.BOLD, 10));
                } else if (tipo.contains("Rotura")) {
                    l.setForeground(Ui.COLOR_ADVERTENCIA);
                    l.setFont(new Font("Segoe UI", Font.BOLD, 10));
                } else {
                    l.setForeground(new Color(147, 51, 234));
                    l.setFont(new Font("Segoe UI", Font.BOLD, 10));
                }
                return l;
            }
        });

        tablaAjustes.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = tablaAjustes.getSelectedRow();
                if (row >= 0 && row < listaAjustesFiltrada.size()) {
                    mostrarDetalleAjuste(listaAjustesFiltrada.get(row));
                }
            }
        });

        JScrollPane sp = new JScrollPane(tablaAjustes);
        sp.setBorder(BorderFactory.createLineBorder(Ui.BORDE_SUAVE, 1));
        sp.getViewport().setBackground(Color.WHITE);
        card.add(sp, BorderLayout.CENTER);

        return card;
    }

    private JPanel crearPanelDetail() {
        contenedorPadre = new JPanel(new BorderLayout());
        contenedorPadre.setOpaque(false);

        // Panel Estado Vacío
        panelHubVacio = new JPanel(new BorderLayout());
        panelHubVacio.setOpaque(false);
        JLabel lblVacio = new JLabel("Seleccione un registro de merma para visualizar la causal y el acta de baja", SwingConstants.CENTER);
        lblVacio.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblVacio.setForeground(Ui.TEXTO_MUTED);
        panelHubVacio.add(lblVacio, BorderLayout.CENTER);

        // Panel Detalle Activo
        panelHubDetalle = new JPanel(new BorderLayout(0, 8)) {
            private static final long serialVersionUID = 1L;
            @Override
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
        panelHubDetalle.setOpaque(false);
        panelHubDetalle.setBorder(new EmptyBorder(12, 14, 12, 14));

        // 1. Cabecera del Hub
        JPanel cabHub = new JPanel(new BorderLayout(10, 0));
        cabHub.setOpaque(false);

        lblHubAvatar = new JLabel("A", SwingConstants.CENTER) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int diametro = Math.min(getWidth(), getHeight()) - 2;
                if (diametro > 0) {
                    g2.setColor(new Color(254, 226, 226));
                    g2.fill(new Ellipse2D.Double(1, 1, diametro, diametro));
                    g2.setColor(Ui.COLOR_PELIGRO);
                    g2.draw(new Ellipse2D.Double(1, 1, diametro, diametro));
                }
                super.paintComponent(g);
                g2.dispose();
            }
        };
        lblHubAvatar.setPreferredSize(new Dimension(46, 46));
        lblHubAvatar.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblHubAvatar.setForeground(Ui.COLOR_PELIGRO);
        cabHub.add(lblHubAvatar, BorderLayout.WEST);

        JPanel infoMed = new JPanel();
        infoMed.setOpaque(false);
        infoMed.setLayout(new BoxLayout(infoMed, BoxLayout.Y_AXIS));

        lblHubProducto = new JLabel("Producto");
        lblHubProducto.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblHubProducto.setForeground(Ui.TEXTO_TITULO);

        lblHubIdAjuste = new JLabel("AJU-2024-00 · Lote");
        lblHubIdAjuste.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblHubIdAjuste.setForeground(Ui.TEXTO_MUTED);

        infoMed.add(lblHubProducto);
        infoMed.add(Box.createVerticalStrut(2));
        infoMed.add(lblHubIdAjuste);
        cabHub.add(infoMed, BorderLayout.CENTER);

        lblHubBadgeTipo = new JLabel("Merma", SwingConstants.CENTER);
        lblHubBadgeTipo.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblHubBadgeTipo.setOpaque(true);
        lblHubBadgeTipo.setBackground(new Color(254, 226, 226));
        lblHubBadgeTipo.setForeground(Ui.COLOR_PELIGRO);
        lblHubBadgeTipo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(254, 202, 202), 1, true),
                new EmptyBorder(3, 8, 3, 8)
        ));
        cabHub.add(lblHubBadgeTipo, BorderLayout.EAST);
        panelHubDetalle.add(cabHub, BorderLayout.NORTH);

        // 2. Contenido Scrollable
        ScrollablePanel scrollContent = new ScrollablePanel();
        scrollContent.setLayout(new BoxLayout(scrollContent, BoxLayout.Y_AXIS));
        scrollContent.setOpaque(false);

        // Telemetría 2x2
        JPanel gridTelemetria = new JPanel(new GridLayout(2, 2, 8, 8));
        gridTelemetria.setOpaque(false);
        gridTelemetria.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        lblHubPerdida = new JLabel("S/. 0.00");
        lblHubCantidad = new JLabel("0 un.");
        lblHubCostoUnitario = new JLabel("S/. 0.00");
        lblHubFecha = new JLabel("-");

        gridTelemetria.add(crearCajaTelemetria("PÉRDIDA VALORIZADA", lblHubPerdida, Ui.COLOR_PELIGRO));
        gridTelemetria.add(crearCajaTelemetria("CANTIDAD DESCARGADA", lblHubCantidad, new Color(14, 165, 233)));
        gridTelemetria.add(crearCajaTelemetria("COSTO ADQUISICIÓN", lblHubCostoUnitario, new Color(100, 116, 139)));
        gridTelemetria.add(crearCajaTelemetria("FECHA DE BAJA", lblHubFecha, Ui.TURQUESA_PROFUNDO));

        scrollContent.add(gridTelemetria);
        scrollContent.add(Box.createVerticalStrut(8));

        // Causal Sanitaria
        JPanel seccionMotivo = crearSeccionDetalle("CAUSAL SANITARIA & DICTAMEN MÉDICO");
        txtHubMotivo = new JTextArea("-");
        txtHubMotivo.setFont(new Font("Segoe UI", Font.BOLD, 11));
        txtHubMotivo.setForeground(Ui.TEXTO_TITULO);
        txtHubMotivo.setLineWrap(true);
        txtHubMotivo.setWrapStyleWord(true);
        txtHubMotivo.setRows(2);
        txtHubMotivo.setPreferredSize(new Dimension(380, 34));
        txtHubMotivo.setOpaque(false);
        txtHubMotivo.setEditable(false);
        txtHubMotivo.setFocusable(false);
        seccionMotivo.add(txtHubMotivo);
        scrollContent.add(seccionMotivo);
        scrollContent.add(Box.createVerticalStrut(8));

        // Trazabilidad de Lote
        JPanel seccionLote = crearSeccionDetalle("TRAZABILIDAD DE LOTE & ALMACÉN");
        lblHubCodigoProd = new JLabel("-");
        lblHubLote = new JLabel("-");

        seccionLote.add(crearFilaDato("Código de Producto:", lblHubCodigoProd));
        seccionLote.add(Box.createVerticalStrut(3));
        seccionLote.add(crearFilaDato("Número de Lote:", lblHubLote));
        scrollContent.add(seccionLote);
        scrollContent.add(Box.createVerticalStrut(8));

        // Autorización
        JPanel seccionAutor = crearSeccionDetalle("AUTORIZACIÓN & AUDITORÍA");
        lblHubAutorizadoPor = new JLabel("-");
        lblHubEstado = new JLabel("-");

        seccionAutor.add(crearFilaDato("Autorizado Por:", lblHubAutorizadoPor));
        seccionAutor.add(Box.createVerticalStrut(3));
        seccionAutor.add(crearFilaDato("Estado del Ajuste:", lblHubEstado));
        scrollContent.add(seccionAutor);
        scrollContent.add(Box.createVerticalGlue());

        JScrollPane spHub = new JScrollPane(scrollContent);
        spHub.setBorder(null);
        spHub.setOpaque(false);
        spHub.getViewport().setOpaque(false);
        spHub.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        spHub.getVerticalScrollBar().setUnitIncrement(16);
        panelHubDetalle.add(spHub, BorderLayout.CENTER);

        // 3. Barra de Acciones Rápidas (1-Click)
        JPanel barraAcciones = new JPanel(new GridLayout(2, 2, 6, 6));
        barraAcciones.setOpaque(false);
        barraAcciones.setPreferredSize(new Dimension(0, 72));
        barraAcciones.setMaximumSize(new Dimension(Integer.MAX_VALUE, 72));

        JButton btnActa = crearBotonAccion("Acta Destrucción", Iconos.crearIconoDocumento(14, Color.WHITE), true);
        btnActa.addActionListener(e -> emitirActaDestruccion());

        JButton btnCertificado = crearBotonAccion("Certificado Baja", Iconos.crearIconoCertificado(14, new Color(37, 99, 235)), false);
        btnCertificado.addActionListener(e -> emitirCertificadoMerma());

        JButton btnAuditoria = crearBotonAccion("Notificar Auditoría", Iconos.crearIconoCampana(14, Ui.COLOR_ADVERTENCIA), false);
        btnAuditoria.addActionListener(e -> notificarAuditoria());

        JButton btnAnular = crearBotonAccion("Anular Registro", Iconos.crearIconoLapiz(14, new Color(100, 116, 139)), false);
        btnAnular.addActionListener(e -> anularMerma());

        barraAcciones.add(btnActa);
        barraAcciones.add(btnCertificado);
        barraAcciones.add(btnAuditoria);
        barraAcciones.add(btnAnular);

        panelHubDetalle.add(barraAcciones, BorderLayout.SOUTH);

        contenedorPadre.add(panelHubVacio, BorderLayout.CENTER);
        return contenedorPadre;
    }

    private JPanel crearCajaTelemetria(String titulo, JLabel lblValor, Color colorAcento) {
        JPanel c = new JPanel();
        c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));
        c.setBackground(new Color(248, 250, 252));
        c.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(6, 10, 6, 10)
        ));

        JLabel t = new JLabel(titulo);
        t.setFont(new Font("Segoe UI", Font.BOLD, 9));
        t.setForeground(Ui.TEXTO_MUTED);

        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblValor.setForeground(colorAcento);

        c.add(t);
        c.add(Box.createVerticalStrut(2));
        c.add(lblValor);
        return c;
    }

    private JPanel crearSeccionDetalle(String titulo) {
        JPanel sec = new JPanel();
        sec.setLayout(new BoxLayout(sec, BoxLayout.Y_AXIS));
        sec.setBackground(new Color(248, 250, 252));
        sec.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(8, 12, 8, 12)
        ));
        sec.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel t = new JLabel(titulo);
        t.setFont(new Font("Segoe UI", Font.BOLD, 10));
        t.setForeground(Ui.TURQUESA_PROFUNDO);
        t.setAlignmentX(Component.LEFT_ALIGNMENT);

        sec.add(t);
        sec.add(Box.createVerticalStrut(5));
        return sec;
    }

    private JPanel crearFilaDato(String etiqueta, JLabel lblValor) {
        JPanel fila = new JPanel(new BorderLayout(8, 0));
        fila.setOpaque(false);
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
        fila.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblE = new JLabel(etiqueta);
        lblE.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblE.setForeground(Ui.TEXTO_MUTED);
        lblE.setPreferredSize(new Dimension(130, 20));

        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblValor.setForeground(Ui.TEXTO_TITULO);

        fila.add(lblE, BorderLayout.WEST);
        fila.add(lblValor, BorderLayout.CENTER);
        return fila;
    }

    public void recargarDatos() {
        filtrarAjustes();
        actualizarKpis();
    }

    private void actualizarKpis() {
        List<AjusteMerma> todos = repo.getAjustesMermas();
        lblKpiTotalAjustes.setText(String.valueOf(todos.size()));

        double perdida = 0;
        int vencidos = 0;
        int reg = 0;

        for (AjusteMerma a : todos) {
            perdida += a.getPerdidaValorizada();
            if (a.getTipo().contains("Vencimiento")) vencidos++;
            if (a.getTipo().contains("Ajuste")) reg++;
        }

        lblKpiPerdidaTotal.setText(String.format("S/. %.2f", perdida));
        lblKpiLotesCaducados.setText(String.valueOf(vencidos));
        lblKpiRegularizaciones.setText(String.valueOf(reg));
    }

    private void filtrarAjustes() {
        String query = txtBuscar != null ? txtBuscar.getText().trim().toLowerCase() : "";
        List<AjusteMerma> todos = repo.getAjustesMermas();
        listaAjustesFiltrada.clear();

        for (AjusteMerma a : todos) {
            boolean coincideTipo = "TODOS".equalsIgnoreCase(tipoFiltroSeleccionado)
                    || a.getTipo().toLowerCase().contains(tipoFiltroSeleccionado.toLowerCase());
            if (!coincideTipo) continue;

            if (!query.isEmpty()) {
                boolean match = a.getIdAjuste().toLowerCase().contains(query)
                        || a.getNombreProducto().toLowerCase().contains(query)
                        || a.getNumeroLote().toLowerCase().contains(query)
                        || a.getMotivoDetallado().toLowerCase().contains(query)
                        || a.getAutorizadoPor().toLowerCase().contains(query);
                if (!match) continue;
            }

            listaAjustesFiltrada.add(a);
        }

        modeloAjustes.setRowCount(0);
        for (AjusteMerma a : listaAjustesFiltrada) {
            modeloAjustes.addRow(new Object[]{
                    a.getIdAjuste(),
                    a.getNombreProducto(),
                    a.getTipo(),
                    a.getCantidad() + " un.",
                    String.format("%.2f", a.getPerdidaValorizada()),
                    a.getNumeroLote(),
                    a.getFechaFormateada()
            });
        }

        if (!listaAjustesFiltrada.isEmpty()) {
            tablaAjustes.setRowSelectionInterval(0, 0);
            mostrarDetalleAjuste(listaAjustesFiltrada.get(0));
        } else {
            ajusteSeleccionado = null;
            contenedorPadre.removeAll();
            contenedorPadre.add(panelHubVacio, BorderLayout.CENTER);
            contenedorPadre.revalidate();
            contenedorPadre.repaint();
        }
    }

    private void mostrarDetalleAjuste(AjusteMerma a) {
        this.ajusteSeleccionado = a;
        contenedorPadre.removeAll();
        contenedorPadre.add(panelHubDetalle, BorderLayout.CENTER);

        String letra = !a.getNombreProducto().isEmpty() ? a.getNombreProducto().substring(0, 1).toUpperCase() : "A";
        lblHubAvatar.setText(letra);

        lblHubProducto.setText(a.getNombreProducto());
        lblHubIdAjuste.setText(a.getIdAjuste() + " · Lote: " + a.getNumeroLote());

        // Badge Tipo
        lblHubBadgeTipo.setText(a.getTipo());
        if (a.getTipo().contains("Vencimiento") || a.getTipo().contains("Frío")) {
            lblHubBadgeTipo.setBackground(new Color(254, 226, 226));
            lblHubBadgeTipo.setForeground(Ui.COLOR_PELIGRO);
        } else if (a.getTipo().contains("Rotura")) {
            lblHubBadgeTipo.setBackground(new Color(254, 243, 199));
            lblHubBadgeTipo.setForeground(new Color(217, 119, 6));
        } else {
            lblHubBadgeTipo.setBackground(new Color(243, 232, 255));
            lblHubBadgeTipo.setForeground(new Color(126, 34, 206));
        }

        // Telemetría
        lblHubPerdida.setText(String.format("S/. %.2f", a.getPerdidaValorizada()));
        lblHubCantidad.setText(a.getCantidad() + " unidades");
        lblHubCostoUnitario.setText(String.format("S/. %.2f", a.getCostoUnitario()));
        lblHubFecha.setText(a.getFechaFormateada());

        // Secciones
        txtHubMotivo.setText(a.getMotivoDetallado());
        lblHubCodigoProd.setText(a.getCodigoProducto());
        lblHubLote.setText(a.getNumeroLote());
        lblHubAutorizadoPor.setText(a.getAutorizadoPor());
        lblHubEstado.setText(a.getEstado());

        contenedorPadre.revalidate();
        contenedorPadre.repaint();
    }

    private void abrirModalNuevaMerma() {
        FormularioAjusteMermaDialog dlg = new FormularioAjusteMermaDialog(
                (java.awt.Frame) javax.swing.SwingUtilities.getWindowAncestor(this)
        );
        dlg.setVisible(true);
        if (dlg.isGuardadoExitoso()) {
            recargarDatos();
        }
    }

    private void emitirActaDestruccion() {
        if (ajusteSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un registro de merma para emitir el acta.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String acta = "ACTA OFICIAL DE DESTRUCCIÓN SANITARIA Y BAJA DE MEDICAMENTOS VETERINARIOS\n" +
                "CLÍNICA & HOSPITAL VETERINARIO HAPPY PETS S.A.C.\n" +
                "=========================================================================\n\n" +
                "CÓDIGO DE ACTA   : " + ajusteSeleccionado.getIdAjuste() + "\n" +
                "FECHA DE BAJA    : " + ajusteSeleccionado.getFechaFormateada() + "\n" +
                "CAUSAL SANITARIA : " + ajusteSeleccionado.getTipo() + "\n\n" +
                "DATOS DEL PRODUCTO DADO DE BAJA:\n" +
                "• Nombre Fármaco : " + ajusteSeleccionado.getNombreProducto() + "\n" +
                "• Código Interno : " + ajusteSeleccionado.getCodigoProducto() + "\n" +
                "• Lote de Fab.   : " + ajusteSeleccionado.getNumeroLote() + "\n" +
                "• Cantidad Total : " + ajusteSeleccionado.getCantidad() + " unidades físicas\n" +
                "• Costo Unitario : S/. " + String.format("%.2f", ajusteSeleccionado.getCostoUnitario()) + "\n" +
                "• PÉRDIDA VALOR. : S/. " + String.format("%.2f", ajusteSeleccionado.getPerdidaValorizada()) + "\n\n" +
                "CIRCUNSTANCIAS Y MOTIVO:\n" +
                ajusteSeleccionado.getMotivoDetallado() + "\n\n" +
                "DICTAMEN ÉTICO Y SANITARIO:\n" +
                "En cumplimiento con las normativas sanitarias vigentes y las buenas prácticas de almacenamiento (BPA),\n" +
                "se procede con la desnaturalización e incineración controlada de los fármacos inhabilitados.\n\n" +
                "__________________________             __________________________\n" +
                "  Regente Farmacéutico                  Dirección Médica Happy Pets\n" +
                "  Colegiatura N°: ________               " + ajusteSeleccionado.getAutorizadoPor() + "\n";

        Ui.mostrarVisorReporte(
                javax.swing.SwingUtilities.getWindowAncestor(this),
                "Acta Destrucción - " + ajusteSeleccionado.getIdAjuste(),
                "ACTA OFICIAL DE DESTRUCCIÓN SANITARIA",
                acta,
                "Acta_Destruccion_" + ajusteSeleccionado.getIdAjuste()
        );
    }

    private void emitirCertificadoMerma() {
        if (ajusteSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un registro para generar certificado de baja contable.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String certificado = "CERTIFICADO DE DESCARGO Y BAJA CONTABLE DE INVENTARIO\n" +
                "HAPPY PETS - AUDITORÍA Y CONTROL FINANCIERO\n" +
                "=========================================================================\n\n" +
                "REFERENCIA REGISTRO : " + ajusteSeleccionado.getIdAjuste() + "\n" +
                "FECHA CONTABLE      : " + ajusteSeleccionado.getFechaFormateada() + "\n" +
                "PRODUCTO AFECTADO   : " + ajusteSeleccionado.getNombreProducto() + " (" + ajusteSeleccionado.getCodigoProducto() + ")\n" +
                "LOTE DE REGISTRO    : " + ajusteSeleccionado.getNumeroLote() + "\n" +
                "CANTIDAD AJUSTADA   : " + ajusteSeleccionado.getCantidad() + " unidades\n" +
                "PÉRDIDA PATRIMONIAL : S/. " + String.format("%.2f", ajusteSeleccionado.getPerdidaValorizada()) + "\n" +
                "ESTADO PROCESO      : " + ajusteSeleccionado.getEstado() + "\n\n" +
                "RESPONSABLE DE APROBACIÓN:\n" +
                ajusteSeleccionado.getAutorizadoPor() + "\n\n" +
                "__________________________\n" +
                "   Auditoría Financiera\n";

        Ui.mostrarVisorReporte(
                javax.swing.SwingUtilities.getWindowAncestor(this),
                "Certificado Baja - " + ajusteSeleccionado.getIdAjuste(),
                "CERTIFICADO DE BAJA PATRIMONIAL",
                certificado,
                "Certificado_Baja_" + ajusteSeleccionado.getIdAjuste()
        );
    }

    private void notificarAuditoria() {
        if (ajusteSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un registro para notificar a auditoría.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String msg = "Notificación de Merma emitida para Auditoría Interna:\n" +
                "ID: " + ajusteSeleccionado.getIdAjuste() + " | Producto: " + ajusteSeleccionado.getNombreProducto() +
                "\nPérdida: S/. " + String.format("%.2f", ajusteSeleccionado.getPerdidaValorizada()) +
                " (" + ajusteSeleccionado.getCantidad() + " un.)\nCausal: " + ajusteSeleccionado.getTipo();

        JOptionPane.showMessageDialog(this, msg, "Notificación a Auditoría Enviada", JOptionPane.INFORMATION_MESSAGE);
    }

    private void anularMerma() {
        if (ajusteSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un registro para anular.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int conf = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de anular el registro de merma " + ajusteSeleccionado.getIdAjuste() + "?\n" +
                "Producto: " + ajusteSeleccionado.getNombreProducto() + "\nPérdida: S/. " + String.format("%.2f", ajusteSeleccionado.getPerdidaValorizada()),
                "Confirmar Anulación", JOptionPane.YES_NO_OPTION);

        if (conf == JOptionPane.YES_OPTION) {
            repo.eliminarAjusteMerma(ajusteSeleccionado.getIdAjuste());
            recargarDatos();
            JOptionPane.showMessageDialog(this, "Registro de merma anulado exitosamente.", "Anulación Exitosa", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private JButton crearBotonPill(String texto, boolean activo) {
        JButton btn = new JButton(texto) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                boolean act = Boolean.TRUE.equals(getClientProperty("activo"));
                if (act) {
                    g2.setColor(Ui.TURQUESA);
                } else {
                    g2.setColor(getModel().isRollover() ? new Color(241, 245, 249) : Color.WHITE);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(act ? Ui.TURQUESA_OSCURO : Ui.BORDE_SUAVE);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                super.paintComponent(g);
                g2.dispose();
            }
        };
        btn.putClientProperty("activo", activo);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 10));
        btn.setForeground(activo ? Color.WHITE : new Color(71, 85, 105));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(3, 10, 3, 10));
        return btn;
    }

    private void actualizarPills() {
        for (Component c : panelPillsTipo.getComponents()) {
            if (c instanceof JButton) {
                JButton b = (JButton) c;
                boolean act = b.getText().equalsIgnoreCase(tipoFiltroSeleccionado);
                b.putClientProperty("activo", act);
                b.setForeground(act ? Color.WHITE : new Color(71, 85, 105));
                b.repaint();
            }
        }
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

    private static class ScrollablePanel extends JPanel implements javax.swing.Scrollable {
        private static final long serialVersionUID = 1L;
        @Override public Dimension getPreferredScrollableViewportSize() { return getPreferredSize(); }
        @Override public int getScrollableUnitIncrement(java.awt.Rectangle visibleRect, int orientation, int direction) { return 16; }
        @Override public int getScrollableBlockIncrement(java.awt.Rectangle visibleRect, int orientation, int direction) { return 32; }
        @Override public boolean getScrollableTracksViewportWidth() { return true; }
        @Override public boolean getScrollableTracksViewportHeight() { return false; }
    }
}
