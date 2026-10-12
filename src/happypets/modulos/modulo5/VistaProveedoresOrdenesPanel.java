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
import java.awt.GridLayout;
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
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import happypets.data.RepositorioVeterinaria;
import happypets.model.OrdenCompra;
import happypets.model.ProveedorFarmacia;
import happypets.ui.Iconos;
import happypets.ui.Ui;

/**
 * Submódulo 5.3: Proveedores y Órdenes de Compra.
 * Arquitectura Master-Detail (58% / 42%) - Opción A Recomendada:
 * - Master Table: Pipeline de órdenes de compra, control presupuestario y directorio de proveedores homologados.
 * - Live Order Hub: Telemetría 2x2, términos comerciales, desglose de fármacos solicitados y recepción en almacén.
 */
public class VistaProveedoresOrdenesPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private final RepositorioVeterinaria repo = RepositorioVeterinaria.getInstancia();

    // KPIs
    private JLabel lblKpiTotalOrdenes;
    private JLabel lblKpiTotalInversion;
    private JLabel lblKpiOrdenesTransito;
    private JLabel lblKpiProveedoresActivos;

    // Master Table & Filtros
    private JTextField txtBuscar;
    private String estadoFiltroSeleccionado = "TODAS";
    private JPanel panelPillsEstado;
    private JTable tablaOrdenes;
    private DefaultTableModel modeloOrdenes;
    private List<OrdenCompra> listaOrdenesFiltrada = new ArrayList<>();
    private OrdenCompra ordenSeleccionada;

    // Live Hub Componentes
    private JPanel panelHubDetalle;
    private JPanel panelHubVacio;
    private JPanel contenedorPadre;
    private JLabel lblHubAvatar;
    private JLabel lblHubProveedor;
    private JLabel lblHubIdOrden;
    private JLabel lblHubBadgeEstado;

    // Telemetría 2x2
    private JLabel lblHubTotal;
    private JLabel lblHubSubtotal;
    private JLabel lblHubIgv;
    private JLabel lblHubFechaEntrega;

    // Secciones descriptivas
    private JLabel lblHubRuc;
    private JLabel lblHubContactoAsesor;
    private JLabel lblHubTelefonoProv;
    private JLabel lblHubFechaEmision;
    private JLabel lblHubSolicitante;
    private JLabel lblHubItemsResumen;

    public VistaProveedoresOrdenesPanel() {
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

        JLabel titulo = new JLabel("Proveedores y Órdenes de Compra Farmacéuticas");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 17));
        titulo.setForeground(Ui.TEXTO_TITULO);

        JLabel subtitulo = new JLabel("Módulo 5.3 · Homologación de laboratorios, emisión de órdenes con IGV y recepción en almacén");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitulo.setForeground(Ui.TEXTO_MUTED);

        izq.add(titulo);
        izq.add(Box.createVerticalStrut(2));
        izq.add(subtitulo);
        cab.add(izq, BorderLayout.CENTER);

        JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        der.setOpaque(false);

        JButton btnNuevoProv = crearBotonAccion("+ Homologar Proveedor", Iconos.crearIconoUsuario(14, Ui.TURQUESA_PROFUNDO), false);
        btnNuevoProv.addActionListener(e -> abrirModalNuevoProveedor());
        btnNuevoProv.setPreferredSize(new Dimension(175, 32));
        der.add(btnNuevoProv);

        JButton btnNuevaOrden = crearBotonAccion("Nueva Orden de Compra", Iconos.crearIconoCamionProveedor(14, Color.WHITE), true);
        btnNuevaOrden.addActionListener(e -> abrirModalNuevaOrden());
        btnNuevaOrden.setPreferredSize(new Dimension(185, 32));
        der.add(btnNuevaOrden);

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

        lblKpiTotalOrdenes = new JLabel("0", SwingConstants.LEFT);
        lblKpiTotalInversion = new JLabel("S/. 0.00", SwingConstants.LEFT);
        lblKpiOrdenesTransito = new JLabel("0", SwingConstants.LEFT);
        lblKpiProveedoresActivos = new JLabel("0", SwingConstants.LEFT);

        fila.add(crearTarjetaKpi("Órdenes de Compra", lblKpiTotalOrdenes, "Historial de pedidos emitidos", new Color(14, 165, 233), Iconos.crearIconoDocumento(18, new Color(14, 165, 233))));
        fila.add(crearTarjetaKpi("Inversión en Farmacia", lblKpiTotalInversion, "Gasto total acumulado (Total S/.)", new Color(16, 185, 129), Iconos.crearIconoFactura(16, new Color(16, 185, 129))));
        fila.add(crearTarjetaKpi("Órdenes en Tránsito", lblKpiOrdenesTransito, "Pendientes de recepción", new Color(245, 158, 11), Iconos.crearIconoCamionProveedor(16, new Color(245, 158, 11))));
        fila.add(crearTarjetaKpi("Proveedores Homologados", lblKpiProveedoresActivos, "Laboratorios y distribuidores", new Color(147, 51, 234), Iconos.crearIconoClientes(16, new Color(147, 51, 234))));

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
        JPanel split = new JPanel(new BorderLayout(14, 0));
        split.setOpaque(false);

        JPanel panelMaster = crearPanelMaster();
        split.add(panelMaster, BorderLayout.CENTER);

        JPanel panelDetail = crearPanelDetail();
        panelDetail.setPreferredSize(new Dimension(460, 560));
        split.add(panelDetail, BorderLayout.EAST);

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
            @Override public void insertUpdate(DocumentEvent e) { filtrarOrdenes(); }
            @Override public void removeUpdate(DocumentEvent e) { filtrarOrdenes(); }
            @Override public void changedUpdate(DocumentEvent e) { filtrarOrdenes(); }
        });
        filaBusqueda.add(txtBuscar, BorderLayout.CENTER);
        panelFiltros.add(filaBusqueda);
        panelFiltros.add(Box.createVerticalStrut(8));

        // Pills de Estado
        panelPillsEstado = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        panelPillsEstado.setOpaque(false);
        String[] estados = {"TODAS", "Borrador", "Enviada a Proveedor", "Recibida en Almacén", "Cancelada"};
        for (String est : estados) {
            JButton pill = crearBotonPill(est, est.equalsIgnoreCase(estadoFiltroSeleccionado));
            pill.addActionListener(e -> {
                estadoFiltroSeleccionado = est;
                actualizarPills();
                filtrarOrdenes();
            });
            panelPillsEstado.add(pill);
        }
        panelFiltros.add(panelPillsEstado);

        card.add(panelFiltros, BorderLayout.NORTH);

        // Tabla Master
        String[] columnas = {"N° Orden", "Proveedor / Laboratorio", "Ítems Resumen", "Entrega Est.", "Total S/.", "Estado"};
        modeloOrdenes = new DefaultTableModel(columnas, 0) {
            private static final long serialVersionUID = 1L;
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };

        tablaOrdenes = new JTable(modeloOrdenes);
        tablaOrdenes.setRowHeight(48);
        tablaOrdenes.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        tablaOrdenes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaOrdenes.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        tablaOrdenes.getTableHeader().setBackground(new Color(248, 250, 252));
        tablaOrdenes.getTableHeader().setForeground(new Color(71, 85, 105));
        tablaOrdenes.setShowVerticalLines(false);
        tablaOrdenes.setGridColor(new Color(241, 245, 249));

        // Anchos de columna
        tablaOrdenes.getColumnModel().getColumn(0).setPreferredWidth(85);
        tablaOrdenes.getColumnModel().getColumn(1).setPreferredWidth(170);
        tablaOrdenes.getColumnModel().getColumn(2).setPreferredWidth(180);
        tablaOrdenes.getColumnModel().getColumn(3).setPreferredWidth(85);
        tablaOrdenes.getColumnModel().getColumn(4).setPreferredWidth(80);
        tablaOrdenes.getColumnModel().getColumn(5).setPreferredWidth(110);

        // Renderers
        tablaOrdenes.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
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

        tablaOrdenes.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
            private static final long serialVersionUID = 1L;
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                JPanel cell = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
                cell.setOpaque(true);
                cell.setBackground(isSelected ? table.getSelectionBackground() : (row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252)));

                String nombre = value != null ? value.toString() : "";
                String letra = !nombre.isEmpty() ? nombre.substring(0, 1).toUpperCase() : "O";

                JLabel avatar = new JLabel(letra, SwingConstants.CENTER) {
                    private static final long serialVersionUID = 1L;
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(new Color(224, 231, 255));
                        g2.fillOval(0, 0, getWidth() - 1, getHeight() - 1);
                        g2.setColor(new Color(199, 210, 254));
                        g2.drawOval(0, 0, getWidth() - 1, getHeight() - 1);
                        super.paintComponent(g);
                        g2.dispose();
                    }
                };
                avatar.setPreferredSize(new Dimension(30, 30));
                avatar.setFont(new Font("Segoe UI", Font.BOLD, 11));
                avatar.setForeground(new Color(79, 70, 229));

                JLabel lblNombre = new JLabel(nombre);
                lblNombre.setFont(new Font("Segoe UI", Font.BOLD, 11));
                lblNombre.setForeground(isSelected ? table.getSelectionForeground() : Ui.TEXTO_TITULO);

                cell.add(avatar);
                cell.add(lblNombre);
                return cell;
            }
        });

        tablaOrdenes.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            private static final long serialVersionUID = 1L;
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                l.setHorizontalAlignment(SwingConstants.CENTER);
                String est = value != null ? value.toString() : "";
                if (est.toLowerCase().contains("recibida")) {
                    l.setForeground(new Color(22, 163, 74));
                    l.setFont(new Font("Segoe UI", Font.BOLD, 10));
                } else if (est.toLowerCase().contains("enviada")) {
                    l.setForeground(new Color(217, 119, 6));
                    l.setFont(new Font("Segoe UI", Font.BOLD, 10));
                } else if (est.toLowerCase().contains("cancelada")) {
                    l.setForeground(Ui.COLOR_PELIGRO);
                    l.setFont(new Font("Segoe UI", Font.BOLD, 10));
                } else {
                    l.setForeground(new Color(100, 116, 139));
                    l.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                }
                return l;
            }
        });

        tablaOrdenes.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = tablaOrdenes.getSelectedRow();
                if (row >= 0 && row < listaOrdenesFiltrada.size()) {
                    mostrarDetalleOrden(listaOrdenesFiltrada.get(row));
                }
            }
        });

        JScrollPane sp = new JScrollPane(tablaOrdenes);
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
        JLabel lblVacio = new JLabel("Seleccione una orden de compra para visualizar su desglose", SwingConstants.CENTER);
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

        lblHubAvatar = new JLabel("P", SwingConstants.CENTER) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int diametro = Math.min(getWidth(), getHeight()) - 2;
                if (diametro > 0) {
                    g2.setColor(new Color(224, 231, 255));
                    g2.fill(new Ellipse2D.Double(1, 1, diametro, diametro));
                    g2.setColor(new Color(99, 102, 241));
                    g2.draw(new Ellipse2D.Double(1, 1, diametro, diametro));
                }
                super.paintComponent(g);
                g2.dispose();
            }
        };
        lblHubAvatar.setPreferredSize(new Dimension(46, 46));
        lblHubAvatar.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblHubAvatar.setForeground(new Color(79, 70, 229));
        cabHub.add(lblHubAvatar, BorderLayout.WEST);

        JPanel infoMed = new JPanel();
        infoMed.setOpaque(false);
        infoMed.setLayout(new BoxLayout(infoMed, BoxLayout.Y_AXIS));

        lblHubProveedor = new JLabel("Proveedor");
        lblHubProveedor.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblHubProveedor.setForeground(Ui.TEXTO_TITULO);

        lblHubIdOrden = new JLabel("OC-2024-00 · RUC");
        lblHubIdOrden.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblHubIdOrden.setForeground(Ui.TEXTO_MUTED);

        infoMed.add(lblHubProveedor);
        infoMed.add(Box.createVerticalStrut(2));
        infoMed.add(lblHubIdOrden);
        cabHub.add(infoMed, BorderLayout.CENTER);

        lblHubBadgeEstado = new JLabel("Enviada", SwingConstants.CENTER);
        lblHubBadgeEstado.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblHubBadgeEstado.setOpaque(true);
        lblHubBadgeEstado.setBackground(new Color(254, 243, 199));
        lblHubBadgeEstado.setForeground(new Color(217, 119, 6));
        lblHubBadgeEstado.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(253, 230, 138), 1, true),
                new EmptyBorder(3, 8, 3, 8)
        ));
        cabHub.add(lblHubBadgeEstado, BorderLayout.EAST);
        panelHubDetalle.add(cabHub, BorderLayout.NORTH);

        // 2. Contenido Scrollable
        ScrollablePanel scrollContent = new ScrollablePanel();
        scrollContent.setLayout(new BoxLayout(scrollContent, BoxLayout.Y_AXIS));
        scrollContent.setOpaque(false);

        // Telemetría 2x2
        JPanel gridTelemetria = new JPanel(new GridLayout(2, 2, 8, 8));
        gridTelemetria.setOpaque(false);

        lblHubTotal = new JLabel("S/. 0.00");
        lblHubSubtotal = new JLabel("S/. 0.00");
        lblHubIgv = new JLabel("S/. 0.00");
        lblHubFechaEntrega = new JLabel("-");

        gridTelemetria.add(crearCajaTelemetria("TOTAL LIQUIDACIÓN", lblHubTotal, new Color(16, 185, 129)));
        gridTelemetria.add(crearCajaTelemetria("SUBTOTAL (SIN IGV)", lblHubSubtotal, new Color(14, 165, 233)));
        gridTelemetria.add(crearCajaTelemetria("IGV FISCAL (18%)", lblHubIgv, new Color(100, 116, 139)));
        gridTelemetria.add(crearCajaTelemetria("FECHA ENTREGA", lblHubFechaEntrega, Ui.TURQUESA_PROFUNDO));

        scrollContent.add(gridTelemetria);
        scrollContent.add(Box.createVerticalStrut(10));

        // Sección Proveedor
        JPanel seccionProv = crearSeccionDetalle("DATOS FISCALES & ASESOR COMERCIAL");
        lblHubRuc = new JLabel("-");
        lblHubContactoAsesor = new JLabel("-");
        lblHubTelefonoProv = new JLabel("-");

        seccionProv.add(crearFilaDato("RUC Proveedor:", lblHubRuc));
        seccionProv.add(crearFilaDato("Asesor de Cuenta:", lblHubContactoAsesor));
        seccionProv.add(crearFilaDato("Teléfono Comercial:", lblHubTelefonoProv));
        scrollContent.add(seccionProv);
        scrollContent.add(Box.createVerticalStrut(8));

        // Sección Ítems Solicitados
        JPanel seccionItems = crearSeccionDetalle("FÁRMACOS & INSUMOS SOLICITADOS");
        lblHubItemsResumen = new JLabel("-");
        lblHubItemsResumen.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblHubItemsResumen.setForeground(Ui.TEXTO_REGULAR);
        seccionItems.add(lblHubItemsResumen);
        scrollContent.add(seccionItems);
        scrollContent.add(Box.createVerticalStrut(8));

        // Sección Emisión y Solicitante
        JPanel seccionEmision = crearSeccionDetalle("TÉRMINOS DE COMPRA & DESPACHO");
        lblHubFechaEmision = new JLabel("-");
        lblHubSolicitante = new JLabel("-");

        seccionEmision.add(crearFilaDato("Fecha de Emisión:", lblHubFechaEmision));
        seccionEmision.add(crearFilaDato("Área Solicitante:", lblHubSolicitante));
        scrollContent.add(seccionEmision);

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

        JButton btnRecepcionar = crearBotonAccion("Recepcionar Almacén", Iconos.crearIconoCheck(14, Color.WHITE), true);
        btnRecepcionar.addActionListener(e -> recepcionarOrdenAlmacen());

        JButton btnOrdenDoc = crearBotonAccion("Orden Oficial PDF", Iconos.crearIconoDocumento(14, new Color(37, 99, 235)), false);
        btnOrdenDoc.addActionListener(e -> emitirOrdenCompraOficial());

        JButton btnWhatsapp = crearBotonAccion("Enviar WhatsApp", Iconos.crearIconoWhatsApp(14, new Color(37, 211, 102)), false);
        btnWhatsapp.addActionListener(e -> enviarOrdenWhatsApp());

        JButton btnEditar = crearBotonAccion("Editar / Estado", Iconos.crearIconoLapiz(14, new Color(100, 116, 139)), false);
        btnEditar.addActionListener(e -> abrirModalEditar());

        barraAcciones.add(btnRecepcionar);
        barraAcciones.add(btnOrdenDoc);
        barraAcciones.add(btnWhatsapp);
        barraAcciones.add(btnEditar);

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
                new EmptyBorder(6, 8, 6, 8)
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
        sec.setOpaque(false);
        sec.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(6, 10, 6, 10)
        ));

        JLabel t = new JLabel(titulo);
        t.setFont(new Font("Segoe UI", Font.BOLD, 10));
        t.setForeground(Ui.TURQUESA_PROFUNDO);
        t.setAlignmentX(Component.LEFT_ALIGNMENT);

        sec.add(t);
        sec.add(Box.createVerticalStrut(4));
        return sec;
    }

    private JPanel crearFilaDato(String etiqueta, JLabel lblValor) {
        JPanel fila = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 1));
        fila.setOpaque(false);
        fila.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblE = new JLabel(etiqueta);
        lblE.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblE.setForeground(Ui.TEXTO_MUTED);

        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblValor.setForeground(Ui.TEXTO_REGULAR);

        fila.add(lblE);
        fila.add(lblValor);
        return fila;
    }

    public void recargarDatos() {
        filtrarOrdenes();
        actualizarKpis();
    }

    private void actualizarKpis() {
        List<OrdenCompra> todas = repo.getOrdenesCompra();
        lblKpiTotalOrdenes.setText(String.valueOf(todas.size()));

        double inversion = 0;
        int transito = 0;
        for (OrdenCompra oc : todas) {
            inversion += oc.getTotal();
            if ("Enviada a Proveedor".equalsIgnoreCase(oc.getEstado()) || "Borrador".equalsIgnoreCase(oc.getEstado())) {
                transito++;
            }
        }
        lblKpiTotalInversion.setText(String.format("S/. %.2f", inversion));
        lblKpiOrdenesTransito.setText(String.valueOf(transito));
        lblKpiProveedoresActivos.setText(String.valueOf(repo.getProveedoresFarmacia().size()));
    }

    private void filtrarOrdenes() {
        String query = txtBuscar != null ? txtBuscar.getText().trim().toLowerCase() : "";
        List<OrdenCompra> todas = repo.getOrdenesCompra();
        listaOrdenesFiltrada.clear();

        for (OrdenCompra oc : todas) {
            boolean coincideEstado = "TODAS".equalsIgnoreCase(estadoFiltroSeleccionado)
                    || oc.getEstado().equalsIgnoreCase(estadoFiltroSeleccionado);
            if (!coincideEstado) continue;

            if (!query.isEmpty()) {
                boolean match = oc.getIdOrden().toLowerCase().contains(query)
                        || oc.getNombreProveedor().toLowerCase().contains(query)
                        || oc.getRucProveedor().toLowerCase().contains(query)
                        || oc.getSolicitante().toLowerCase().contains(query);
                if (!match) continue;
            }

            listaOrdenesFiltrada.add(oc);
        }

        modeloOrdenes.setRowCount(0);
        for (OrdenCompra oc : listaOrdenesFiltrada) {
            modeloOrdenes.addRow(new Object[]{
                    oc.getIdOrden(),
                    oc.getNombreProveedor(),
                    oc.getItemsResumen(),
                    oc.getFechaEntregaEstimadaFormateada(),
                    String.format("%.2f", oc.getTotal()),
                    oc.getEstado()
            });
        }

        if (!listaOrdenesFiltrada.isEmpty()) {
            tablaOrdenes.setRowSelectionInterval(0, 0);
            mostrarDetalleOrden(listaOrdenesFiltrada.get(0));
        } else {
            ordenSeleccionada = null;
            contenedorPadre.removeAll();
            contenedorPadre.add(panelHubVacio, BorderLayout.CENTER);
            contenedorPadre.revalidate();
            contenedorPadre.repaint();
        }
    }

    private void mostrarDetalleOrden(OrdenCompra oc) {
        this.ordenSeleccionada = oc;
        contenedorPadre.removeAll();
        contenedorPadre.add(panelHubDetalle, BorderLayout.CENTER);

        String letra = !oc.getNombreProveedor().isEmpty() ? oc.getNombreProveedor().substring(0, 1).toUpperCase() : "O";
        lblHubAvatar.setText(letra);

        lblHubProveedor.setText(oc.getNombreProveedor());
        lblHubIdOrden.setText(oc.getIdOrden() + " · RUC: " + oc.getRucProveedor());

        // Badge Estado
        lblHubBadgeEstado.setText(oc.getEstado());
        if (oc.getEstado().toLowerCase().contains("recibida")) {
            lblHubBadgeEstado.setBackground(new Color(220, 252, 231));
            lblHubBadgeEstado.setForeground(new Color(22, 163, 74));
        } else if (oc.getEstado().toLowerCase().contains("enviada")) {
            lblHubBadgeEstado.setBackground(new Color(254, 243, 199));
            lblHubBadgeEstado.setForeground(new Color(217, 119, 6));
        } else if (oc.getEstado().toLowerCase().contains("cancelada")) {
            lblHubBadgeEstado.setBackground(new Color(254, 226, 226));
            lblHubBadgeEstado.setForeground(Ui.COLOR_PELIGRO);
        } else {
            lblHubBadgeEstado.setBackground(new Color(241, 245, 249));
            lblHubBadgeEstado.setForeground(new Color(100, 116, 139));
        }

        // Telemetría
        lblHubTotal.setText(String.format("S/. %.2f", oc.getTotal()));
        lblHubSubtotal.setText(String.format("S/. %.2f", oc.getSubtotal()));
        lblHubIgv.setText(String.format("S/. %.2f", oc.getIgv()));
        lblHubFechaEntrega.setText(oc.getFechaEntregaEstimadaFormateada());

        // Secciones
        lblHubRuc.setText(oc.getRucProveedor());

        ProveedorFarmacia prov = null;
        for (ProveedorFarmacia p : repo.getProveedoresFarmacia()) {
            if (p.getRuc().equalsIgnoreCase(oc.getRucProveedor())) {
                prov = p;
                break;
            }
        }

        lblHubContactoAsesor.setText(prov != null ? prov.getContactoAsesor() : "Asesor Comercial");
        lblHubTelefonoProv.setText(prov != null ? prov.getTelefono() : "-");
        lblHubFechaEmision.setText(oc.getFechaEmisionFormateada());
        lblHubSolicitante.setText(oc.getSolicitante());
        lblHubItemsResumen.setText("<html>" + oc.getItemsResumen().replace("\n", "<br>") + "</html>");

        contenedorPadre.revalidate();
        contenedorPadre.repaint();
    }

    private void abrirModalNuevaOrden() {
        FormularioOrdenCompraDialog dlg = new FormularioOrdenCompraDialog(
                (java.awt.Frame) javax.swing.SwingUtilities.getWindowAncestor(this)
        );
        dlg.setVisible(true);
        if (dlg.isGuardadoExitoso()) {
            recargarDatos();
        }
    }

    private void abrirModalNuevoProveedor() {
        FormularioProveedorDialog dlg = new FormularioProveedorDialog(
                (java.awt.Frame) javax.swing.SwingUtilities.getWindowAncestor(this)
        );
        dlg.setVisible(true);
        if (dlg.isGuardadoExitoso()) {
            recargarDatos();
        }
    }

    private void abrirModalEditar() {
        if (ordenSeleccionada == null) {
            JOptionPane.showMessageDialog(this, "Seleccione una orden para editar o modificar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Object[] opciones = {"Editar Datos", "Cancelar Orden", "Eliminar Registro", "Volver"};
        int res = JOptionPane.showOptionDialog(this,
                "¿Qué acción desea ejecutar sobre la Orden " + ordenSeleccionada.getIdOrden() + "?",
                "Gestión de Orden de Compra",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                opciones,
                opciones[0]);

        if (res == 0) {
            FormularioOrdenCompraDialog dlg = new FormularioOrdenCompraDialog(
                    (java.awt.Frame) javax.swing.SwingUtilities.getWindowAncestor(this),
                    ordenSeleccionada
            );
            dlg.setVisible(true);
            if (dlg.isGuardadoExitoso()) {
                recargarDatos();
            }
        } else if (res == 1) {
            repo.actualizarEstadoOrdenCompra(ordenSeleccionada.getIdOrden(), "Cancelada");
            recargarDatos();
            JOptionPane.showMessageDialog(this, "Estado actualizado a: Cancelada", "Orden Cancelada", JOptionPane.INFORMATION_MESSAGE);
        } else if (res == 2) {
            int conf = JOptionPane.showConfirmDialog(this,
                    "¿Desea eliminar definitivamente la orden " + ordenSeleccionada.getIdOrden() + "?",
                    "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);
            if (conf == JOptionPane.YES_OPTION) {
                repo.eliminarOrdenCompra(ordenSeleccionada.getIdOrden());
                recargarDatos();
            }
        }
    }

    private void recepcionarOrdenAlmacen() {
        if (ordenSeleccionada == null) {
            JOptionPane.showMessageDialog(this, "Seleccione una orden de compra para recepcionar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if ("Recibida en Almacén".equalsIgnoreCase(ordenSeleccionada.getEstado())) {
            JOptionPane.showMessageDialog(this, "Esta orden ya fue recepcionada e ingresada previamente a inventario.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int conf = JOptionPane.showConfirmDialog(this,
                "¿Confirmar la recepción formal de la Orden " + ordenSeleccionada.getIdOrden() + " en almacén?\n" +
                "Proveedor: " + ordenSeleccionada.getNombreProveedor() + "\n" +
                "Total: S/. " + String.format("%.2f", ordenSeleccionada.getTotal()),
                "Recepción de Mercadería", JOptionPane.YES_NO_OPTION);

        if (conf == JOptionPane.YES_OPTION) {
            repo.actualizarEstadoOrdenCompra(ordenSeleccionada.getIdOrden(), "Recibida en Almacén");
            recargarDatos();
            JOptionPane.showMessageDialog(this,
                    "Orden marcada como 'Recibida en Almacén'.\nSe ha completado el ciclo de abastecimiento de mercadería.",
                    "Recepción Completada", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void emitirOrdenCompraOficial() {
        if (ordenSeleccionada == null) {
            JOptionPane.showMessageDialog(this, "Seleccione una orden para emitir el documento oficial.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String reporte = "ORDEN DE COMPRA OFICIAL DE MEDICAMENTOS E INSUMOS CLÍNICOS\n" +
                "CLÍNICA VETERINARIA & HOSPITAL 24H HAPPY PETS S.A.C. - RUC: 20608945123\n" +
                "=========================================================================\n\n" +
                "ORDEN DE COMPRA N° : " + ordenSeleccionada.getIdOrden() + "\n" +
                "FECHA DE EMISIÓN   : " + ordenSeleccionada.getFechaEmisionFormateada() + "\n" +
                "FECHA DE ENTREGA   : " + ordenSeleccionada.getFechaEntregaEstimadaFormateada() + "\n" +
                "SOLICITANTE        : " + ordenSeleccionada.getSolicitante() + "\n\n" +
                "DATOS DEL PROVEEDOR:\n" +
                "• Razón Social     : " + ordenSeleccionada.getNombreProveedor() + "\n" +
                "• RUC Proveedor    : " + ordenSeleccionada.getRucProveedor() + "\n\n" +
                "DETALLE DE PRODUCTOS Y PRESENTACIONES PEDIDAS:\n" +
                ordenSeleccionada.getItemsResumen() + "\n\n" +
                "LIQUIDACIÓN FINANCIERA (EN MONEDA NACIONAL SOLES):\n" +
                "• Subtotal Valor Compra : S/. " + String.format("%10.2f", ordenSeleccionada.getSubtotal()) + "\n" +
                "• Impuesto IGV (18%)    : S/. " + String.format("%10.2f", ordenSeleccionada.getIgv()) + "\n" +
                "• TOTAL ORDEN DE COMPRA : S/. " + String.format("%10.2f", ordenSeleccionada.getTotal()) + "\n\n" +
                "CONDICIONES DE RECEPCIÓN:\n" +
                "1. Fármacos con cadena de frío deben entregarse con data logger térmico activo (2°C - 8°C).\n" +
                "2. Vigencia mínima de lote al momento de la entrega: mayor a 12 meses de caducidad.\n" +
                "3. Adjuntar Factura Electrónica y Guía de Remisión Remitente.\n\n" +
                "__________________________             __________________________\n" +
                "  Administración Happy Pets              Firma y Sello de Proveedor\n" +
                "     Área de Adquisiciones                  Conformidad de Pedido\n";

        Ui.mostrarVisorReporte(
                javax.swing.SwingUtilities.getWindowAncestor(this),
                "Orden de Compra - " + ordenSeleccionada.getIdOrden(),
                "ORDEN DE COMPRA OFICIAL",
                reporte,
                "Orden_Compra_" + ordenSeleccionada.getIdOrden()
        );
    }

    private void enviarOrdenWhatsApp() {
        if (ordenSeleccionada == null) {
            JOptionPane.showMessageDialog(this, "Seleccione una orden para preparar el pedido por WhatsApp.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String msg = "¡Hola estimado proveedor " + ordenSeleccionada.getNombreProveedor() + "! Desde Happy Pets Clínica Veterinaria le remitimos la Orden de Compra N° " +
                ordenSeleccionada.getIdOrden() + " por un total de S/. " + String.format("%.2f", ordenSeleccionada.getTotal()) +
                ". Solicitamos entrega estimada para el " + ordenSeleccionada.getFechaEntregaEstimadaFormateada() +
                ". Detalle de ítems: " + ordenSeleccionada.getItemsResumen();

        JOptionPane.showMessageDialog(this,
                "Mensaje de WhatsApp preparado para el asesor comercial:\n\n\"" + msg + "\"",
                "Envío de Pedido por WhatsApp", JOptionPane.INFORMATION_MESSAGE);
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
        for (Component c : panelPillsEstado.getComponents()) {
            if (c instanceof JButton) {
                JButton b = (JButton) c;
                boolean act = b.getText().equalsIgnoreCase(estadoFiltroSeleccionado);
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
