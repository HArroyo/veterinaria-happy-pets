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
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
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
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import happypets.data.RepositorioVeterinaria;
import happypets.model.LoteMovimientoStock;
import happypets.model.ProductoFarmacia;
import happypets.ui.Iconos;
import happypets.ui.Ui;

/**
 * Submódulo 5.2: Control de Stock y Lotes (Kardex).
 * Arquitectura Master-Detail (58% / 42%) - Opción A Recomendada:
 * - Master Table: Movimientos históricos, entradas, salidas clínicas, despachos quirúrgicos y lotes.
 * - Live Kardex Hub: Telemetría 2x2, días de caducidad, auditoría de almacén, protocolo FEFO y acciones rápidas.
 */
public class VistaControlStockLotesPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private final RepositorioVeterinaria repo = RepositorioVeterinaria.getInstancia();

    // KPIs
    private JLabel lblKpiTotalMovimientos;
    private JLabel lblKpiLotesPorVencer;
    private JLabel lblKpiStockCritico;
    private JLabel lblKpiMovimientosHoy;

    // Master Table & Filtros
    private JTextField txtBuscar;
    private String tipoFiltroSeleccionado = "TODOS";
    private JPanel panelPillsTipo;
    private JTable tablaMovimientos;
    private DefaultTableModel modeloMovimientos;
    private List<LoteMovimientoStock> listaMovimientosFiltrada = new ArrayList<>();
    private LoteMovimientoStock movimientoSeleccionado;

    // Live Kardex Hub Componentes
    private JPanel panelHubDetalle;
    private JPanel panelHubVacio;
    private JPanel contenedorPadre;
    private JLabel lblHubAvatar;
    private JLabel lblHubProducto;
    private JLabel lblHubIdMovimiento;
    private JLabel lblHubBadgeTipo;

    // Telemetría 2x2
    private JLabel lblHubCantidad;
    private JLabel lblHubStockPosterior;
    private JLabel lblHubDiasCaducidad;
    private JLabel lblHubFechaMovimiento;

    // Secciones descriptivas
    private JLabel lblHubCodigoProd;
    private JLabel lblHubLote;
    private JLabel lblHubFechaVenc;
    private JLabel lblHubResponsable;
    private JLabel lblHubStockPrevio;
    private JLabel lblHubObservacion;
    private JLabel lblHubProtocoloFefo;

    public VistaControlStockLotesPanel() {
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

        JLabel titulo = new JLabel("Control de Stock, Lotes y Trazabilidad (Kardex)");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 17));
        titulo.setForeground(Ui.TEXTO_TITULO);

        JLabel subtitulo = new JLabel("Módulo 5.2 · Registro de entradas, salidas clínicas, despachos quirúrgicos y monitoreo preventivo FEFO");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitulo.setForeground(Ui.TEXTO_MUTED);

        izq.add(titulo);
        izq.add(Box.createVerticalStrut(2));
        izq.add(subtitulo);
        cab.add(izq, BorderLayout.CENTER);

        JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        der.setOpaque(false);

        JLabel badgeFefo = new JLabel("Protocolo FEFO Activo · Farmacia Happy Pets", SwingConstants.CENTER);
        badgeFefo.setFont(new Font("Segoe UI", Font.BOLD, 11));
        badgeFefo.setForeground(Ui.TURQUESA_PROFUNDO);
        badgeFefo.setOpaque(true);
        badgeFefo.setBackground(Ui.TURQUESA_SUAVE);
        badgeFefo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Ui.TURQUESA_MEDIO, 1, true),
                new EmptyBorder(4, 10, 4, 10)
        ));
        der.add(badgeFefo);

        JButton btnNuevo = crearBotonAccion("Registrar Movimiento", Iconos.crearIconoCajaAlmacen(14, Color.WHITE), true);
        btnNuevo.addActionListener(e -> abrirModalNuevoMovimiento());
        btnNuevo.setPreferredSize(new Dimension(180, 32));
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

        lblKpiTotalMovimientos = new JLabel("0", SwingConstants.LEFT);
        lblKpiLotesPorVencer = new JLabel("0", SwingConstants.LEFT);
        lblKpiStockCritico = new JLabel("0", SwingConstants.LEFT);
        lblKpiMovimientosHoy = new JLabel("0", SwingConstants.LEFT);

        fila.add(crearTarjetaKpi("Movimientos Kardex", lblKpiTotalMovimientos, "Historial de entradas y salidas", new Color(14, 165, 233), Iconos.crearIconoCajaAlmacen(18, new Color(14, 165, 233))));
        fila.add(crearTarjetaKpi("Lotes por Vencer (<60d)", lblKpiLotesPorVencer, "Monitoreo preventivo de caducidad", new Color(245, 158, 11), Iconos.crearIconoCampana(16, new Color(245, 158, 11))));
        fila.add(crearTarjetaKpi("Fármacos con Quiebre", lblKpiStockCritico, "Stock menor o igual al mínimo", new Color(239, 68, 68), Iconos.crearIconoAlertaMerma(16, new Color(239, 68, 68))));
        fila.add(crearTarjetaKpi("Operaciones Recientes", lblKpiMovimientosHoy, "Actividad en el almacén", new Color(16, 185, 129), Iconos.crearIconoFactura(16, new Color(16, 185, 129))));

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
            @Override public void insertUpdate(DocumentEvent e) { filtrarMovimientos(); }
            @Override public void removeUpdate(DocumentEvent e) { filtrarMovimientos(); }
            @Override public void changedUpdate(DocumentEvent e) { filtrarMovimientos(); }
        });
        filaBusqueda.add(txtBuscar, BorderLayout.CENTER);
        panelFiltros.add(filaBusqueda);
        panelFiltros.add(Box.createVerticalStrut(8));

        // Pills de Tipo
        panelPillsTipo = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        panelPillsTipo.setOpaque(false);
        String[] tipos = {"TODOS", "Ingreso", "Salida", "Ajuste"};
        for (String t : tipos) {
            JButton pill = crearBotonPill(t, t.equalsIgnoreCase(tipoFiltroSeleccionado));
            pill.addActionListener(e -> {
                tipoFiltroSeleccionado = t;
                actualizarPills();
                filtrarMovimientos();
            });
            panelPillsTipo.add(pill);
        }
        panelFiltros.add(panelPillsTipo);

        card.add(panelFiltros, BorderLayout.NORTH);

        // Tabla Master
        String[] columnas = {"ID Mov.", "Fármaco / Producto", "Tipo Movimiento", "Lote", "Cantidad", "Stock Final", "Fecha"};
        modeloMovimientos = new DefaultTableModel(columnas, 0) {
            private static final long serialVersionUID = 1L;
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };

        tablaMovimientos = new JTable(modeloMovimientos);
        tablaMovimientos.setRowHeight(48);
        tablaMovimientos.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        tablaMovimientos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaMovimientos.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        tablaMovimientos.getTableHeader().setBackground(new Color(248, 250, 252));
        tablaMovimientos.getTableHeader().setForeground(new Color(71, 85, 105));
        tablaMovimientos.setShowVerticalLines(false);
        tablaMovimientos.setGridColor(new Color(241, 245, 249));

        // Anchos de columna
        tablaMovimientos.getColumnModel().getColumn(0).setPreferredWidth(85);
        tablaMovimientos.getColumnModel().getColumn(1).setPreferredWidth(180);
        tablaMovimientos.getColumnModel().getColumn(2).setPreferredWidth(125);
        tablaMovimientos.getColumnModel().getColumn(3).setPreferredWidth(85);
        tablaMovimientos.getColumnModel().getColumn(4).setPreferredWidth(65);
        tablaMovimientos.getColumnModel().getColumn(5).setPreferredWidth(75);
        tablaMovimientos.getColumnModel().getColumn(6).setPreferredWidth(80);

        // Renderers
        tablaMovimientos.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
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

        tablaMovimientos.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
            private static final long serialVersionUID = 1L;
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                JPanel cell = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
                cell.setOpaque(true);
                cell.setBackground(isSelected ? table.getSelectionBackground() : (row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252)));

                String nombre = value != null ? value.toString() : "";
                String letra = !nombre.isEmpty() ? nombre.substring(0, 1).toUpperCase() : "M";

                JLabel avatar = new JLabel(letra, SwingConstants.CENTER) {
                    private static final long serialVersionUID = 1L;
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(new Color(241, 245, 249));
                        g2.fillOval(0, 0, getWidth() - 1, getHeight() - 1);
                        g2.setColor(new Color(203, 213, 225));
                        g2.drawOval(0, 0, getWidth() - 1, getHeight() - 1);
                        super.paintComponent(g);
                        g2.dispose();
                    }
                };
                avatar.setPreferredSize(new Dimension(30, 30));
                avatar.setFont(new Font("Segoe UI", Font.BOLD, 11));
                avatar.setForeground(Ui.TURQUESA_PROFUNDO);

                JLabel lblNombre = new JLabel(nombre);
                lblNombre.setFont(new Font("Segoe UI", Font.BOLD, 11));
                lblNombre.setForeground(isSelected ? table.getSelectionForeground() : Ui.TEXTO_TITULO);

                cell.add(avatar);
                cell.add(lblNombre);
                return cell;
            }
        });

        tablaMovimientos.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            private static final long serialVersionUID = 1L;
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                String tipo = value != null ? value.toString() : "";
                if (tipo.toLowerCase().contains("ingreso")) {
                    l.setForeground(new Color(16, 185, 129));
                    l.setFont(new Font("Segoe UI", Font.BOLD, 10));
                } else if (tipo.toLowerCase().contains("salida")) {
                    l.setForeground(new Color(37, 99, 235));
                    l.setFont(new Font("Segoe UI", Font.BOLD, 10));
                } else {
                    l.setForeground(new Color(147, 51, 234));
                    l.setFont(new Font("Segoe UI", Font.BOLD, 10));
                }
                return l;
            }
        });

        tablaMovimientos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = tablaMovimientos.getSelectedRow();
                if (row >= 0 && row < listaMovimientosFiltrada.size()) {
                    mostrarDetalleMovimiento(listaMovimientosFiltrada.get(row));
                }
            }
        });

        JScrollPane sp = new JScrollPane(tablaMovimientos);
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
        JLabel lblVacio = new JLabel("Seleccione un registro del Kardex para inspeccionar su trazabilidad", SwingConstants.CENTER);
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

        lblHubAvatar = new JLabel("M", SwingConstants.CENTER) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int diametro = Math.min(getWidth(), getHeight()) - 2;
                if (diametro > 0) {
                    g2.setColor(new Color(241, 245, 249));
                    g2.fill(new Ellipse2D.Double(1, 1, diametro, diametro));
                    g2.setColor(Ui.TURQUESA);
                    g2.draw(new Ellipse2D.Double(1, 1, diametro, diametro));
                }
                super.paintComponent(g);
                g2.dispose();
            }
        };
        lblHubAvatar.setPreferredSize(new Dimension(46, 46));
        lblHubAvatar.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblHubAvatar.setForeground(Ui.TURQUESA_PROFUNDO);
        cabHub.add(lblHubAvatar, BorderLayout.WEST);

        JPanel infoMed = new JPanel();
        infoMed.setOpaque(false);
        infoMed.setLayout(new BoxLayout(infoMed, BoxLayout.Y_AXIS));

        lblHubProducto = new JLabel("Producto");
        lblHubProducto.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblHubProducto.setForeground(Ui.TEXTO_TITULO);

        lblHubIdMovimiento = new JLabel("MOV-2024-00 · Lote");
        lblHubIdMovimiento.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblHubIdMovimiento.setForeground(Ui.TEXTO_MUTED);

        infoMed.add(lblHubProducto);
        infoMed.add(Box.createVerticalStrut(2));
        infoMed.add(lblHubIdMovimiento);
        cabHub.add(infoMed, BorderLayout.CENTER);

        lblHubBadgeTipo = new JLabel("Ingreso", SwingConstants.CENTER);
        lblHubBadgeTipo.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblHubBadgeTipo.setOpaque(true);
        lblHubBadgeTipo.setBackground(new Color(220, 252, 231));
        lblHubBadgeTipo.setForeground(new Color(22, 163, 74));
        lblHubBadgeTipo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(187, 247, 208), 1, true),
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

        lblHubCantidad = new JLabel("+0 un.");
        lblHubStockPosterior = new JLabel("0 un.");
        lblHubDiasCaducidad = new JLabel("0 días");
        lblHubFechaMovimiento = new JLabel("-");

        gridTelemetria.add(crearCajaTelemetria("CANTIDAD OPERADA", lblHubCantidad, new Color(14, 165, 233)));
        gridTelemetria.add(crearCajaTelemetria("STOCK RESULTANTE", lblHubStockPosterior, new Color(16, 185, 129)));
        gridTelemetria.add(crearCajaTelemetria("DÍAS CADUCIDAD LOTE", lblHubDiasCaducidad, new Color(245, 158, 11)));
        gridTelemetria.add(crearCajaTelemetria("FECHA MOVIMIENTO", lblHubFechaMovimiento, Ui.TURQUESA_PROFUNDO));

        scrollContent.add(gridTelemetria);
        scrollContent.add(Box.createVerticalStrut(8));

        // Trazabilidad
        JPanel seccionLote = crearSeccionDetalle("TRAZABILIDAD DE LOTE & FARMACOPEA");
        lblHubCodigoProd = new JLabel("-");
        lblHubLote = new JLabel("-");
        lblHubFechaVenc = new JLabel("-");

        seccionLote.add(crearFilaDato("Código Producto:", lblHubCodigoProd));
        seccionLote.add(Box.createVerticalStrut(3));
        seccionLote.add(crearFilaDato("Número de Lote:", lblHubLote));
        seccionLote.add(Box.createVerticalStrut(3));
        seccionLote.add(crearFilaDato("Fecha de Vencimiento:", lblHubFechaVenc));
        scrollContent.add(seccionLote);
        scrollContent.add(Box.createVerticalStrut(8));

        // Auditoría
        JPanel seccionAuditoria = crearSeccionDetalle("AUDITORÍA DE MOVIMIENTO & ALMACÉN");
        lblHubResponsable = new JLabel("-");
        lblHubStockPrevio = new JLabel("-");
        lblHubObservacion = new JLabel("-");

        seccionAuditoria.add(crearFilaDato("Responsable / Regente:", lblHubResponsable));
        seccionAuditoria.add(Box.createVerticalStrut(3));
        seccionAuditoria.add(crearFilaDato("Stock Previo Operación:", lblHubStockPrevio));
        seccionAuditoria.add(Box.createVerticalStrut(3));
        seccionAuditoria.add(crearFilaDato("Referencia / Doc.:", lblHubObservacion));
        scrollContent.add(seccionAuditoria);
        scrollContent.add(Box.createVerticalStrut(8));

        // Protocolo Sanitario
        JPanel seccionFefo = crearSeccionDetalle("DISPENSACIÓN FEFO (FIRST EXPIRED, FIRST OUT)");
        lblHubProtocoloFefo = new JLabel("Lote validado bajo política FEFO. Cumple normativa sanitaria.");
        lblHubProtocoloFefo.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblHubProtocoloFefo.setForeground(new Color(22, 163, 74));
        seccionFefo.add(lblHubProtocoloFefo);
        scrollContent.add(seccionFefo);
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

        JButton btnComprobante = crearBotonAccion("Nota de Almacén", Iconos.crearIconoDocumento(14, Color.WHITE), true);
        btnComprobante.addActionListener(e -> emitirNotaAlmacen());

        JButton btnKardex = crearBotonAccion("Kardex Producto", Iconos.crearIconoFactura(14, new Color(37, 99, 235)), false);
        btnKardex.addActionListener(e -> emitirKardexProducto());

        JButton btnAlerta = crearBotonAccion("Alertas Caducidad", Iconos.crearIconoCampana(14, Ui.COLOR_ADVERTENCIA), false);
        btnAlerta.addActionListener(e -> verAlertasCaducidad());

        JButton btnAnular = crearBotonAccion("Anular Registro", Iconos.crearIconoLapiz(14, new Color(100, 116, 139)), false);
        btnAnular.addActionListener(e -> anularMovimiento());

        barraAcciones.add(btnComprobante);
        barraAcciones.add(btnKardex);
        barraAcciones.add(btnAlerta);
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
        filtrarMovimientos();
        actualizarKpis();
    }

    private void actualizarKpis() {
        List<LoteMovimientoStock> todos = repo.getMovimientosStock();
        lblKpiTotalMovimientos.setText(String.valueOf(todos.size()));

        int porVencer = 0;
        for (LoteMovimientoStock m : todos) {
            long dias = ChronoUnit.DAYS.between(LocalDate.now(), m.getFechaVencimientoLote());
            if (dias <= 60) porVencer++;
        }
        lblKpiLotesPorVencer.setText(String.valueOf(porVencer));

        int criticos = 0;
        for (ProductoFarmacia p : repo.getProductosFarmacia()) {
            if (p.getStockActual() <= p.getStockMinimo()) criticos++;
        }
        lblKpiStockCritico.setText(String.valueOf(criticos));
        lblKpiMovimientosHoy.setText(String.valueOf(Math.min(todos.size(), 4)));
    }

    private void filtrarMovimientos() {
        String query = txtBuscar != null ? txtBuscar.getText().trim().toLowerCase() : "";
        List<LoteMovimientoStock> todos = repo.getMovimientosStock();
        listaMovimientosFiltrada.clear();

        for (LoteMovimientoStock m : todos) {
            boolean coincideTipo = "TODOS".equalsIgnoreCase(tipoFiltroSeleccionado)
                    || m.getTipoMovimiento().toLowerCase().contains(tipoFiltroSeleccionado.toLowerCase());
            if (!coincideTipo) continue;

            if (!query.isEmpty()) {
                boolean match = m.getIdMovimiento().toLowerCase().contains(query)
                        || m.getNombreProducto().toLowerCase().contains(query)
                        || m.getNumeroLote().toLowerCase().contains(query)
                        || m.getResponsable().toLowerCase().contains(query);
                if (!match) continue;
            }

            listaMovimientosFiltrada.add(m);
        }

        modeloMovimientos.setRowCount(0);
        for (LoteMovimientoStock m : listaMovimientosFiltrada) {
            modeloMovimientos.addRow(new Object[]{
                    m.getIdMovimiento(),
                    m.getNombreProducto(),
                    m.getTipoMovimiento(),
                    m.getNumeroLote(),
                    (m.getTipoMovimiento().toLowerCase().contains("ingreso") ? "+" : "-") + m.getCantidad(),
                    m.getStockPosterior() + " un.",
                    m.getFechaMovimientoFormateada()
            });
        }

        if (!listaMovimientosFiltrada.isEmpty()) {
            tablaMovimientos.setRowSelectionInterval(0, 0);
            mostrarDetalleMovimiento(listaMovimientosFiltrada.get(0));
        } else {
            movimientoSeleccionado = null;
            contenedorPadre.removeAll();
            contenedorPadre.add(panelHubVacio, BorderLayout.CENTER);
            contenedorPadre.revalidate();
            contenedorPadre.repaint();
        }
    }

    private void mostrarDetalleMovimiento(LoteMovimientoStock m) {
        this.movimientoSeleccionado = m;
        contenedorPadre.removeAll();
        contenedorPadre.add(panelHubDetalle, BorderLayout.CENTER);

        String letra = !m.getNombreProducto().isEmpty() ? m.getNombreProducto().substring(0, 1).toUpperCase() : "M";
        lblHubAvatar.setText(letra);

        lblHubProducto.setText(m.getNombreProducto());
        lblHubIdMovimiento.setText(m.getIdMovimiento() + " · Lote: " + m.getNumeroLote());

        // Badge Tipo
        boolean esIngreso = m.getTipoMovimiento().toLowerCase().contains("ingreso");
        lblHubBadgeTipo.setText(m.getTipoMovimiento());
        if (esIngreso) {
            lblHubBadgeTipo.setBackground(new Color(220, 252, 231));
            lblHubBadgeTipo.setForeground(new Color(22, 163, 74));
        } else if (m.getTipoMovimiento().toLowerCase().contains("salida")) {
            lblHubBadgeTipo.setBackground(new Color(224, 242, 254));
            lblHubBadgeTipo.setForeground(new Color(2, 132, 199));
        } else {
            lblHubBadgeTipo.setBackground(new Color(243, 232, 255));
            lblHubBadgeTipo.setForeground(new Color(126, 34, 206));
        }

        // Telemetría
        lblHubCantidad.setText((esIngreso ? "+" : "-") + m.getCantidad() + " un.");
        lblHubStockPosterior.setText(m.getStockPosterior() + " un.");

        long diasVenc = ChronoUnit.DAYS.between(LocalDate.now(), m.getFechaVencimientoLote());
        lblHubDiasCaducidad.setText(diasVenc + " días");
        lblHubDiasCaducidad.setForeground(diasVenc <= 60 ? Ui.COLOR_PELIGRO : new Color(245, 158, 11));

        lblHubFechaMovimiento.setText(m.getFechaMovimientoFormateada());

        // Secciones
        lblHubCodigoProd.setText(m.getCodigoProducto());
        lblHubLote.setText(m.getNumeroLote());
        lblHubFechaVenc.setText(m.getFechaVencimientoLoteFormateada());

        lblHubResponsable.setText(m.getResponsable());
        lblHubStockPrevio.setText(m.getStockPrevio() + " un.");
        lblHubObservacion.setText(m.getObservacion());

        if (diasVenc < 0) {
            lblHubProtocoloFefo.setText("ALERTA CRÍTICA: Lote caducado. Retirar de dispensación inmediatamente.");
            lblHubProtocoloFefo.setForeground(Ui.COLOR_PELIGRO);
        } else if (diasVenc <= 60) {
            lblHubProtocoloFefo.setText("ALERTA PREVENTIVA: Próximo a vencer. Priorizar dispensación FEFO.");
            lblHubProtocoloFefo.setForeground(Ui.COLOR_ADVERTENCIA);
        } else {
            lblHubProtocoloFefo.setText("Lote en regla. Almacenamiento seguro bajo política FEFO.");
            lblHubProtocoloFefo.setForeground(new Color(22, 163, 74));
        }

        contenedorPadre.revalidate();
        contenedorPadre.repaint();
    }

    private void abrirModalNuevoMovimiento() {
        FormularioMovimientoStockDialog dlg = new FormularioMovimientoStockDialog(
                (java.awt.Frame) javax.swing.SwingUtilities.getWindowAncestor(this)
        );
        dlg.setVisible(true);
        if (dlg.isGuardadoExitoso()) {
            recargarDatos();
        }
    }

    private void emitirNotaAlmacen() {
        if (movimientoSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un movimiento para emitir nota de almacén.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String nota = "COMPROBANTE DE MOVIMIENTO EN KARDEX Y CONTROL DE ALMACÉN\n" +
                "HAPPY PETS - GESTIÓN FARMACÉUTICA Y LOGÍSTICA VETERINARIA\n" +
                "=========================================================================\n\n" +
                "OPERACIÓN ID     : " + movimientoSeleccionado.getIdMovimiento() + "\n" +
                "FECHA REGISTRO   : " + movimientoSeleccionado.getFechaMovimientoFormateada() + "\n" +
                "TIPO MOVIMIENTO  : " + movimientoSeleccionado.getTipoMovimiento() + "\n\n" +
                "PRODUCTO         : " + movimientoSeleccionado.getNombreProducto() + " (" + movimientoSeleccionado.getCodigoProducto() + ")\n" +
                "NÚMERO DE LOTE   : " + movimientoSeleccionado.getNumeroLote() + "  |  VENCIMIENTO: " + movimientoSeleccionado.getFechaVencimientoLoteFormateada() + "\n\n" +
                "BALANCE DE STOCK:\n" +
                "• Stock Previo   : " + movimientoSeleccionado.getStockPrevio() + " unidades\n" +
                "• Cantidad Movida: " + (movimientoSeleccionado.getTipoMovimiento().toLowerCase().contains("ingreso") ? "+" : "-") + movimientoSeleccionado.getCantidad() + " unidades\n" +
                "• Stock Posterior: " + movimientoSeleccionado.getStockPosterior() + " unidades resultantes\n\n" +
                "AUDITORÍA Y JUSTIFICACIÓN:\n" +
                "• Responsable    : " + movimientoSeleccionado.getResponsable() + "\n" +
                "• Referencia     : " + movimientoSeleccionado.getObservacion() + "\n\n" +
                "__________________________             __________________________\n" +
                "  Regente Farmacéutico                  Recepción / Auditor Almacén\n";

        Ui.mostrarVisorReporte(
                javax.swing.SwingUtilities.getWindowAncestor(this),
                "Nota de Almacén - " + movimientoSeleccionado.getIdMovimiento(),
                "NOTA DE MOVIMIENTO DE KARDEX",
                nota,
                "Nota_Kardex_" + movimientoSeleccionado.getIdMovimiento()
        );
    }

    private void emitirKardexProducto() {
        if (movimientoSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un movimiento para consultar el kardex.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("HOJA DE KARDEX FÍSICO VALORIZADO POR PRODUCTO\n");
        sb.append("PRODUCTO: ").append(movimientoSeleccionado.getNombreProducto()).append(" (").append(movimientoSeleccionado.getCodigoProducto()).append(")\n");
        sb.append("=========================================================================\n\n");
        sb.append(String.format("%-14s %-12s %-25s %-10s %-12s\n", "ID MOV.", "FECHA", "OPERACIÓN", "CANT.", "STOCK FINAL"));
        sb.append("-------------------------------------------------------------------------\n");

        for (LoteMovimientoStock m : repo.getMovimientosStock()) {
            if (m.getCodigoProducto().equalsIgnoreCase(movimientoSeleccionado.getCodigoProducto())
                    || m.getNombreProducto().equalsIgnoreCase(movimientoSeleccionado.getNombreProducto())) {
                sb.append(String.format("%-14s %-12s %-25s %-10s %-12s\n",
                        m.getIdMovimiento(),
                        m.getFechaMovimientoFormateada(),
                        m.getTipoMovimiento(),
                        (m.getTipoMovimiento().toLowerCase().contains("ingreso") ? "+" : "-") + m.getCantidad(),
                        m.getStockPosterior() + " un."
                ));
            }
        }
        sb.append("-------------------------------------------------------------------------\n");
        sb.append("Fin del reporte de movimientos para el producto consultado.\n");

        Ui.mostrarVisorReporte(
                javax.swing.SwingUtilities.getWindowAncestor(this),
                "Kardex Histórico - " + movimientoSeleccionado.getCodigoProducto(),
                "KARDEX FÍSICO VALORIZADO",
                sb.toString(),
                "Kardex_" + movimientoSeleccionado.getCodigoProducto()
        );
    }

    private void verAlertasCaducidad() {
        StringBuilder sb = new StringBuilder();
        sb.append("REPORTE PREVENTIVO DE VENCIMIENTOS DE LOTES (FEFO)\n");
        sb.append("FARMACIA HAPPY PETS - POLÍTICA SANITARIA DE DISPENSACIÓN\n");
        sb.append("=========================================================================\n\n");

        int count = 0;
        for (LoteMovimientoStock m : repo.getMovimientosStock()) {
            long dias = ChronoUnit.DAYS.between(LocalDate.now(), m.getFechaVencimientoLote());
            if (dias <= 60) {
                count++;
                sb.append(String.format("• [%s] %s | Lote: %s | Vence: %s (%d días restantes)\n",
                        dias < 0 ? "CADUCADO" : "ALERTA",
                        m.getNombreProducto(),
                        m.getNumeroLote(),
                        m.getFechaVencimientoLoteFormateada(),
                        dias
                ));
            }
        }

        if (count == 0) {
            sb.append("Todos los lotes en almacén cuentan con más de 60 días de vigencia sanitaria.\n");
        } else {
            sb.append("\nSe recomienda priorizar la rotación o retiro según corresponda.\n");
        }

        Ui.mostrarVisorReporte(
                javax.swing.SwingUtilities.getWindowAncestor(this),
                "Reporte de Caducidades FEFO",
                "MONITOREO DE VENCIMIENTOS DE LOTES",
                sb.toString(),
                "Reporte_Vencimientos_FEFO"
        );
    }

    private void anularMovimiento() {
        if (movimientoSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un movimiento para anular.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int conf = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de anular el movimiento " + movimientoSeleccionado.getIdMovimiento() + "?\n" +
                "Producto: " + movimientoSeleccionado.getNombreProducto() + " (" + movimientoSeleccionado.getCantidad() + " un.)",
                "Confirmar Anulación", JOptionPane.YES_NO_OPTION);

        if (conf == JOptionPane.YES_OPTION) {
            repo.eliminarMovimientoStock(movimientoSeleccionado.getIdMovimiento());
            recargarDatos();
            JOptionPane.showMessageDialog(this, "Movimiento anulado del registro de Kardex.", "Anulación Exitosa", JOptionPane.INFORMATION_MESSAGE);
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
