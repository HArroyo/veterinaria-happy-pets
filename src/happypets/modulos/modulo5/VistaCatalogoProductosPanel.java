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
import happypets.model.ProductoFarmacia;
import happypets.ui.Iconos;
import happypets.ui.Ui;

/**
 * Submódulo 5.1: Catálogo de Productos y Fármacos.
 * Arquitectura Master-Detail (58% / 42%) - Opción A Recomendada:
 * - Master Table: Catálogo maestro, stock en tiempo real, alertas de reposición, filtros por categoría.
 * - Live Product Hub: Telemetría 2x2, especificaciones farmacológicas, cadena de frío, trazabilidad y acciones 1-click.
 */
public class VistaCatalogoProductosPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private final RepositorioVeterinaria repo = RepositorioVeterinaria.getInstancia();

    // KPIs
    private JLabel lblKpiTotalProductos;
    private JLabel lblKpiValorInventario;
    private JLabel lblKpiBajoStock;
    private JLabel lblKpiRecetaRetenida;

    // Master Table & Filtros
    private JTextField txtBuscar;
    private String categoriaFiltroSeleccionada = "TODOS";
    private JPanel panelPillsCategoria;
    private JTable tablaProductos;
    private DefaultTableModel modeloProductos;
    private List<ProductoFarmacia> listaProductosFiltrada = new ArrayList<>();
    private ProductoFarmacia productoSeleccionado;

    // Live Product Hub Componentes
    private JPanel panelHubDetalle;
    private JPanel panelHubVacio;
    private JPanel contenedorPadre;
    private JLabel lblHubAvatar;
    private JLabel lblHubNombre;
    private JLabel lblHubCodigo;
    private JLabel lblHubBadgeCategoria;
    private JLabel lblHubBadgeEstado;

    // Telemetría 2x2
    private JLabel lblHubStockActual;
    private JLabel lblHubPrecioVenta;
    private JLabel lblHubPrecioCosto;
    private JLabel lblHubMargen;

    // Secciones descriptivas
    private JTextArea lblHubPrincipioActivo;
    private JTextArea lblHubPresentacion;
    private JLabel lblHubEspecies;
    private JLabel lblHubChipReceta;
    private JLabel lblHubChipCadenaFrio;
    private JLabel lblHubLoteVigente;
    private JLabel lblHubFechaCaducidad;
    private JLabel lblHubProveedor;
    private JLabel lblHubStockMinimo;
    private JTextArea txtHubPosologiaIndicacion;

    public VistaCatalogoProductosPanel() {
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

        JLabel titulo = new JLabel("Catálogo de Productos y Fármacos Veterinarios");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 17));
        titulo.setForeground(Ui.TEXTO_TITULO);

        JLabel subtitulo = new JLabel("Módulo 5.1 · Registro maestro de medicamentos, biológicos, insumos clínicos y control de precios");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitulo.setForeground(Ui.TEXTO_MUTED);

        izq.add(titulo);
        izq.add(Box.createVerticalStrut(2));
        izq.add(subtitulo);
        cab.add(izq, BorderLayout.CENTER);

        JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        der.setOpaque(false);

        JLabel badgeRegencia = new JLabel("Regencia Farmacéutica Activa · Happy Pets", SwingConstants.CENTER);
        badgeRegencia.setFont(new Font("Segoe UI", Font.BOLD, 11));
        badgeRegencia.setForeground(Ui.TURQUESA_PROFUNDO);
        badgeRegencia.setOpaque(true);
        badgeRegencia.setBackground(Ui.TURQUESA_SUAVE);
        badgeRegencia.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Ui.TURQUESA_MEDIO, 1, true),
                new EmptyBorder(4, 10, 4, 10)
        ));
        der.add(badgeRegencia);

        JButton btnNuevo = crearBotonAccion("Nuevo Producto", Iconos.crearIconoPildora(14, Color.WHITE), true);
        btnNuevo.addActionListener(e -> abrirModalNuevoProducto());
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

        lblKpiTotalProductos = new JLabel("0", SwingConstants.LEFT);
        lblKpiValorInventario = new JLabel("S/. 0.00", SwingConstants.LEFT);
        lblKpiBajoStock = new JLabel("0", SwingConstants.LEFT);
        lblKpiRecetaRetenida = new JLabel("0", SwingConstants.LEFT);

        fila.add(crearTarjetaKpi("Productos Registrados", lblKpiTotalProductos, "Catálogo farmacéutico activo", new Color(14, 165, 233), Iconos.crearIconoPildora(18, new Color(14, 165, 233))));
        fila.add(crearTarjetaKpi("Valorización Stock (PVP)", lblKpiValorInventario, "Inventario disponible valorizado", new Color(16, 185, 129), Iconos.crearIconoFactura(16, new Color(16, 185, 129))));
        fila.add(crearTarjetaKpi("Alerta de Reposición", lblKpiBajoStock, "Stock menor o igual al mínimo", new Color(245, 158, 11), Iconos.crearIconoAlertaMerma(16, new Color(245, 158, 11))));
        fila.add(crearTarjetaKpi("Fármacos Controlados", lblKpiRecetaRetenida, "Venta bajo receta médica", new Color(147, 51, 234), Iconos.crearIconoDocumento(16, new Color(147, 51, 234))));

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

    private JSplitPane crearSplitMasterDetail() {
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, crearPanelMaster(), crearPanelDetail());
        split.setResizeWeight(1.0);
        split.setDividerSize(6);
        split.setBorder(null);
        split.setOpaque(false);
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

        // Filtros Superiores (Búsqueda + Pills de Categoría)
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
            @Override public void insertUpdate(DocumentEvent e) { filtrarProductos(); }
            @Override public void removeUpdate(DocumentEvent e) { filtrarProductos(); }
            @Override public void changedUpdate(DocumentEvent e) { filtrarProductos(); }
        });
        filaBusqueda.add(txtBuscar, BorderLayout.CENTER);
        panelFiltros.add(filaBusqueda);
        panelFiltros.add(Box.createVerticalStrut(8));

        // Pills de Categoría
        panelPillsCategoria = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        panelPillsCategoria.setOpaque(false);
        String[] categorias = {"TODOS", "Antibiótico", "Antiparasitario", "Analgésico / AINE", "Vacuna / Biológico", "Alimento Clínico", "Material Quirúrgico"};
        for (String c : categorias) {
            JButton pill = crearBotonPill(c, c.equalsIgnoreCase(categoriaFiltroSeleccionada));
            pill.addActionListener(e -> {
                categoriaFiltroSeleccionada = c;
                actualizarPills();
                filtrarProductos();
            });
            panelPillsCategoria.add(pill);
        }
        panelFiltros.add(panelPillsCategoria);

        card.add(panelFiltros, BorderLayout.NORTH);

        // Tabla Master
        String[] columnas = {"Código", "Ítem / Nombre", "Categoría", "Stock Disp.", "PVP S/.", "Estado"};
        modeloProductos = new DefaultTableModel(columnas, 0) {
            private static final long serialVersionUID = 1L;
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };

        tablaProductos = new JTable(modeloProductos);
        tablaProductos.setRowHeight(48);
        tablaProductos.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        tablaProductos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaProductos.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        tablaProductos.getTableHeader().setBackground(new Color(248, 250, 252));
        tablaProductos.getTableHeader().setForeground(new Color(71, 85, 105));
        tablaProductos.setShowVerticalLines(false);
        tablaProductos.setGridColor(new Color(241, 245, 249));

        // Anchos de columna
        tablaProductos.getColumnModel().getColumn(0).setPreferredWidth(75);
        tablaProductos.getColumnModel().getColumn(1).setPreferredWidth(210);
        tablaProductos.getColumnModel().getColumn(2).setPreferredWidth(110);
        tablaProductos.getColumnModel().getColumn(3).setPreferredWidth(80);
        tablaProductos.getColumnModel().getColumn(4).setPreferredWidth(70);
        tablaProductos.getColumnModel().getColumn(5).setPreferredWidth(85);

        // Renderers
        tablaProductos.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
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

        tablaProductos.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
            private static final long serialVersionUID = 1L;
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                JPanel cell = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
                cell.setOpaque(true);
                cell.setBackground(isSelected ? table.getSelectionBackground() : (row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252)));

                String nombre = value != null ? value.toString() : "";
                String letra = !nombre.isEmpty() ? nombre.substring(0, 1).toUpperCase() : "P";

                JLabel avatar = new JLabel(letra, SwingConstants.CENTER) {
                    private static final long serialVersionUID = 1L;
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(new Color(224, 242, 254));
                        g2.fillOval(0, 0, getWidth() - 1, getHeight() - 1);
                        g2.setColor(new Color(186, 230, 253));
                        g2.drawOval(0, 0, getWidth() - 1, getHeight() - 1);
                        super.paintComponent(g);
                        g2.dispose();
                    }
                };
                avatar.setPreferredSize(new Dimension(30, 30));
                avatar.setFont(new Font("Segoe UI", Font.BOLD, 11));
                avatar.setForeground(new Color(2, 132, 199));

                JLabel lblNombre = new JLabel(nombre);
                lblNombre.setFont(new Font("Segoe UI", Font.BOLD, 11));
                lblNombre.setForeground(isSelected ? table.getSelectionForeground() : Ui.TEXTO_TITULO);

                cell.add(avatar);
                cell.add(lblNombre);
                return cell;
            }
        });

        tablaProductos.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            private static final long serialVersionUID = 1L;
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                l.setHorizontalAlignment(SwingConstants.CENTER);
                String estado = value != null ? value.toString() : "";
                if ("Agotado".equalsIgnoreCase(estado)) {
                    l.setForeground(Ui.COLOR_PELIGRO);
                    l.setFont(new Font("Segoe UI", Font.BOLD, 10));
                } else if ("Bajo Stock".equalsIgnoreCase(estado)) {
                    l.setForeground(Ui.COLOR_ADVERTENCIA);
                    l.setFont(new Font("Segoe UI", Font.BOLD, 10));
                } else {
                    l.setForeground(Ui.COLOR_EXITO);
                    l.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                }
                return l;
            }
        });

        tablaProductos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = tablaProductos.getSelectedRow();
                if (row >= 0 && row < listaProductosFiltrada.size()) {
                    mostrarDetalleProducto(listaProductosFiltrada.get(row));
                }
            }
        });

        JScrollPane sp = new JScrollPane(tablaProductos);
        sp.setBorder(BorderFactory.createLineBorder(Ui.BORDE_SUAVE, 1));
        sp.getViewport().setBackground(Color.WHITE);
        card.add(sp, BorderLayout.CENTER);

        return card;
    }

    private JPanel crearPanelDetail() {
        contenedorPadre = new JPanel(new BorderLayout());
        contenedorPadre.setOpaque(false);
        contenedorPadre.setPreferredSize(new Dimension(360, 0));
        contenedorPadre.setMinimumSize(new Dimension(320, 0));

        // Panel Estado Vacío
        panelHubVacio = new JPanel(new BorderLayout());
        panelHubVacio.setOpaque(false);
        JLabel lblVacio = new JLabel("<html><center>Seleccione un producto del catálogo<br>para ver su ficha técnica.</center></html>", SwingConstants.CENTER);
        lblVacio.setFont(new Font("Segoe UI", Font.PLAIN, 12));
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
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(Ui.BORDE_SUAVE);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.dispose();
            }
        };
        panelHubDetalle.setOpaque(false);
        panelHubDetalle.setBorder(new EmptyBorder(10, 12, 10, 12));

        // 1. Cabecera del Hub
        JPanel cabHub = new JPanel(new BorderLayout(8, 0));
        cabHub.setOpaque(false);

        lblHubAvatar = new JLabel("P", SwingConstants.CENTER) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int diametro = Math.min(getWidth(), getHeight()) - 2;
                if (diametro > 0) {
                    g2.setColor(new Color(224, 242, 254));
                    g2.fill(new Ellipse2D.Double(1, 1, diametro, diametro));
                    g2.setColor(Ui.TURQUESA);
                    g2.draw(new Ellipse2D.Double(1, 1, diametro, diametro));
                }
                super.paintComponent(g);
                g2.dispose();
            }
        };
        lblHubAvatar.setPreferredSize(new Dimension(42, 42));
        lblHubAvatar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblHubAvatar.setForeground(Ui.TURQUESA_PROFUNDO);
        cabHub.add(lblHubAvatar, BorderLayout.WEST);

        JPanel infoMed = new JPanel();
        infoMed.setOpaque(false);
        infoMed.setLayout(new BoxLayout(infoMed, BoxLayout.Y_AXIS));

        lblHubNombre = new JLabel("Nombre Producto");
        lblHubNombre.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblHubNombre.setForeground(Ui.TEXTO_TITULO);

        lblHubCodigo = new JLabel("PROD-000 · Categ");
        lblHubCodigo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblHubCodigo.setForeground(Ui.TEXTO_MUTED);

        infoMed.add(lblHubNombre);
        infoMed.add(Box.createVerticalStrut(2));
        infoMed.add(lblHubCodigo);
        cabHub.add(infoMed, BorderLayout.CENTER);

        lblHubBadgeEstado = new JLabel("Disponible", SwingConstants.CENTER);
        lblHubBadgeEstado.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblHubBadgeEstado.setOpaque(true);
        lblHubBadgeEstado.setBackground(Ui.TURQUESA_SUAVE);
        lblHubBadgeEstado.setForeground(Ui.TURQUESA_PROFUNDO);
        lblHubBadgeEstado.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Ui.TURQUESA_MEDIO, 1, true),
                new EmptyBorder(3, 8, 3, 8)
        ));
        cabHub.add(lblHubBadgeEstado, BorderLayout.EAST);
        panelHubDetalle.add(cabHub, BorderLayout.NORTH);

        // 2. Contenido Scrollable
        ScrollablePanel scrollContent = new ScrollablePanel() {
            private static final long serialVersionUID = 1L;
            @Override
            protected void addImpl(Component comp, Object constraints, int index) {
                if (comp instanceof javax.swing.JComponent) {
                    ((javax.swing.JComponent) comp).setAlignmentX(Component.LEFT_ALIGNMENT);
                }
                super.addImpl(comp, constraints, index);
            }
        };
        scrollContent.setLayout(new BoxLayout(scrollContent, BoxLayout.Y_AXIS));
        scrollContent.setOpaque(false);

        // Telemetría 2x2
        JPanel gridTelemetria = new JPanel(new GridLayout(2, 2, 6, 6));
        gridTelemetria.setOpaque(false);
        gridTelemetria.setMaximumSize(new Dimension(Integer.MAX_VALUE, 92));
        gridTelemetria.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblHubStockActual = new JLabel("0 un.");
        lblHubPrecioVenta = new JLabel("S/. 0.00");
        lblHubPrecioCosto = new JLabel("S/. 0.00");
        lblHubMargen = new JLabel("0.0%");

        gridTelemetria.add(crearCajaTelemetria("STOCK DISPONIBLE", lblHubStockActual, new Color(14, 165, 233)));
        gridTelemetria.add(crearCajaTelemetria("PRECIO PVP (VENTA)", lblHubPrecioVenta, new Color(16, 185, 129)));
        gridTelemetria.add(crearCajaTelemetria("PRECIO COSTO", lblHubPrecioCosto, new Color(100, 116, 139)));
        gridTelemetria.add(crearCajaTelemetria("MARGEN ESTIMADO", lblHubMargen, new Color(147, 51, 234)));

        scrollContent.add(gridTelemetria);
        scrollContent.add(vGap(8));

        // Ficha Farmacológica
        JPanel seccionFarmaco = crearSeccionCard("ESPECIFICACIONES FARMACOLÓGICAS");
        lblHubPrincipioActivo = new JTextArea("-");
        lblHubPrincipioActivo.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblHubPrincipioActivo.setForeground(Ui.TEXTO_TITULO);
        lblHubPrincipioActivo.setLineWrap(true);
        lblHubPrincipioActivo.setWrapStyleWord(true);
        lblHubPrincipioActivo.setEditable(false);
        lblHubPrincipioActivo.setOpaque(false);
        lblHubPrincipioActivo.setBorder(null);

        lblHubPresentacion = new JTextArea("-");
        lblHubPresentacion.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblHubPresentacion.setForeground(Ui.TEXTO_TITULO);
        lblHubPresentacion.setLineWrap(true);
        lblHubPresentacion.setWrapStyleWord(true);
        lblHubPresentacion.setEditable(false);
        lblHubPresentacion.setOpaque(false);
        lblHubPresentacion.setBorder(null);

        lblHubEspecies = new JLabel("-");
        lblHubEspecies.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblHubEspecies.setForeground(Ui.TEXTO_TITULO);

        seccionFarmaco.add(crearItemLinea("Principio Activo:", lblHubPrincipioActivo));
        seccionFarmaco.add(vGap(3));
        seccionFarmaco.add(crearItemLinea("Presentación / Forma:", lblHubPresentacion));
        seccionFarmaco.add(vGap(3));
        seccionFarmaco.add(crearItemLinea("Especies Destino:", lblHubEspecies));
        scrollContent.add(seccionFarmaco);
        scrollContent.add(vGap(6));

        // Requisitos Sanitarios & Conservación
        JPanel seccionSanitaria = crearSeccionCard("CONDICIONES SANITARIAS & CONSERVACIÓN");
        lblHubChipReceta = new JLabel("Venta Libre Veterinaria");
        lblHubChipReceta.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblHubChipReceta.setForeground(new Color(16, 185, 129));

        lblHubChipCadenaFrio = new JLabel("Temperatura Ambiente");
        lblHubChipCadenaFrio.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblHubChipCadenaFrio.setForeground(Ui.TEXTO_TITULO);

        lblHubStockMinimo = new JLabel("-");
        lblHubStockMinimo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblHubStockMinimo.setForeground(Ui.TEXTO_TITULO);

        seccionSanitaria.add(crearItemLinea("Dispensación:", lblHubChipReceta));
        seccionSanitaria.add(vGap(3));
        seccionSanitaria.add(crearItemLinea("Conservación:", lblHubChipCadenaFrio));
        seccionSanitaria.add(vGap(3));
        seccionSanitaria.add(crearItemLinea("Stock de Seguridad:", lblHubStockMinimo));
        scrollContent.add(seccionSanitaria);
        scrollContent.add(vGap(6));

        // Trazabilidad de Lote & Laboratorio
        JPanel seccionTrazabilidad = crearSeccionCard("TRAZABILIDAD DE LOTE & PROVEEDOR");
        lblHubLoteVigente = new JLabel("-");
        lblHubLoteVigente.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblHubLoteVigente.setForeground(Ui.TURQUESA_PROFUNDO);

        lblHubFechaCaducidad = new JLabel("-");
        lblHubFechaCaducidad.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblHubFechaCaducidad.setForeground(Ui.TEXTO_TITULO);

        lblHubProveedor = new JLabel("-");
        lblHubProveedor.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblHubProveedor.setForeground(Ui.TEXTO_TITULO);

        seccionTrazabilidad.add(crearItemLinea("Lote Vigente:", lblHubLoteVigente));
        seccionTrazabilidad.add(vGap(3));
        seccionTrazabilidad.add(crearItemLinea("Fecha de Vencimiento:", lblHubFechaCaducidad));
        seccionTrazabilidad.add(vGap(3));
        seccionTrazabilidad.add(crearItemLinea("Titular / Proveedor:", lblHubProveedor));
        scrollContent.add(seccionTrazabilidad);

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
        barraAcciones.setPreferredSize(new Dimension(0, 62));
        barraAcciones.setMaximumSize(new Dimension(Integer.MAX_VALUE, 62));

        JButton btnAjustar = crearBotonAccion("Ajustar Stock", Iconos.crearIconoCajaAlmacen(14, Color.WHITE), true);
        btnAjustar.addActionListener(e -> realizarAjusteRapidoStock());

        JButton btnFicha = crearBotonAccion("Ficha Farmacéutica", Iconos.crearIconoDocumento(14, new Color(37, 99, 235)), false);
        btnFicha.addActionListener(e -> emitirFichaTecnica());

        JButton btnEtiqueta = crearBotonAccion("Etiqueta Dispensación", Iconos.crearIconoImprimir(14, new Color(147, 51, 234)), false);
        btnEtiqueta.addActionListener(e -> imprimirEtiquetaTermica());

        JButton btnEditar = crearBotonAccion("Editar / Baja", Iconos.crearIconoLapiz(14, new Color(100, 116, 139)), false);
        btnEditar.addActionListener(e -> abrirModalEditar());

        barraAcciones.add(btnAjustar);
        barraAcciones.add(btnFicha);
        barraAcciones.add(btnEtiqueta);
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
                BorderFactory.createLineBorder(Ui.BORDE_SUAVE, 1, true),
                new EmptyBorder(4, 8, 4, 8)
        ));

        JLabel t = new JLabel(titulo);
        t.setFont(new Font("Segoe UI", Font.BOLD, 8));
        t.setForeground(Ui.TEXTO_MUTED);

        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblValor.setForeground(colorAcento);

        c.add(t);
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
        sec.add(vGap(4));
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
        filtrarProductos();
        actualizarKpis();
    }

    private void actualizarKpis() {
        List<ProductoFarmacia> todos = repo.getProductosFarmacia();
        lblKpiTotalProductos.setText(String.valueOf(todos.size()));

        double valorTotal = 0;
        int bajoStock = 0;
        int recetas = 0;

        for (ProductoFarmacia p : todos) {
            valorTotal += (p.getStockActual() * p.getPrecioVenta());
            if (p.getStockActual() <= p.getStockMinimo()) {
                bajoStock++;
            }
            if (p.isRequiereReceta()) {
                recetas++;
            }
        }

        lblKpiValorInventario.setText(String.format("S/. %.2f", valorTotal));
        lblKpiBajoStock.setText(String.valueOf(bajoStock));
        lblKpiRecetaRetenida.setText(String.valueOf(recetas));
    }

    private void filtrarProductos() {
        String query = txtBuscar != null ? txtBuscar.getText().trim().toLowerCase() : "";
        List<ProductoFarmacia> todos = repo.getProductosFarmacia();
        listaProductosFiltrada.clear();

        for (ProductoFarmacia p : todos) {
            boolean coincideCategoria = "TODOS".equalsIgnoreCase(categoriaFiltroSeleccionada)
                    || p.getCategoria().equalsIgnoreCase(categoriaFiltroSeleccionada);
            if (!coincideCategoria) continue;

            if (!query.isEmpty()) {
                boolean match = p.getCodigo().toLowerCase().contains(query)
                        || p.getNombre().toLowerCase().contains(query)
                        || p.getPrincipioActivo().toLowerCase().contains(query)
                        || p.getProveedor().toLowerCase().contains(query);
                if (!match) continue;
            }

            listaProductosFiltrada.add(p);
        }

        modeloProductos.setRowCount(0);
        for (ProductoFarmacia p : listaProductosFiltrada) {
            modeloProductos.addRow(new Object[]{
                    p.getCodigo(),
                    p.getNombre(),
                    p.getCategoria(),
                    p.getStockActual() + " / " + p.getStockMinimo(),
                    String.format("%.2f", p.getPrecioVenta()),
                    p.getEstado()
            });
        }

        if (!listaProductosFiltrada.isEmpty()) {
            tablaProductos.setRowSelectionInterval(0, 0);
            mostrarDetalleProducto(listaProductosFiltrada.get(0));
        } else {
            productoSeleccionado = null;
            contenedorPadre.removeAll();
            contenedorPadre.add(panelHubVacio, BorderLayout.CENTER);
            contenedorPadre.revalidate();
            contenedorPadre.repaint();
        }
    }

    private void mostrarDetalleProducto(ProductoFarmacia p) {
        this.productoSeleccionado = p;
        contenedorPadre.removeAll();
        contenedorPadre.add(panelHubDetalle, BorderLayout.CENTER);

        String letra = !p.getNombre().isEmpty() ? p.getNombre().substring(0, 1).toUpperCase() : "P";
        lblHubAvatar.setText(letra);

        lblHubNombre.setText(p.getNombre());
        lblHubCodigo.setText(p.getCodigo() + " · " + p.getCategoria());

        // Estado Badge
        lblHubBadgeEstado.setText(p.getEstado());
        if ("Agotado".equalsIgnoreCase(p.getEstado())) {
            lblHubBadgeEstado.setBackground(new Color(254, 226, 226));
            lblHubBadgeEstado.setForeground(Ui.COLOR_PELIGRO);
        } else if ("Bajo Stock".equalsIgnoreCase(p.getEstado())) {
            lblHubBadgeEstado.setBackground(new Color(254, 243, 199));
            lblHubBadgeEstado.setForeground(Ui.COLOR_ADVERTENCIA);
        } else {
            lblHubBadgeEstado.setBackground(Ui.TURQUESA_SUAVE);
            lblHubBadgeEstado.setForeground(Ui.TURQUESA_PROFUNDO);
        }

        // Telemetría
        lblHubStockActual.setText(p.getStockActual() + " (" + p.getPresentacion() + ")");
        lblHubStockActual.setForeground(p.getStockActual() <= p.getStockMinimo() ? Ui.COLOR_PELIGRO : new Color(14, 165, 233));
        lblHubPrecioVenta.setText(String.format("S/. %.2f", p.getPrecioVenta()));
        lblHubPrecioCosto.setText(String.format("S/. %.2f", p.getPrecioCosto()));

        double costo = p.getPrecioCosto();
        double pvp = p.getPrecioVenta();
        double margenPct = costo > 0 ? ((pvp - costo) / costo) * 100.0 : 0.0;
        lblHubMargen.setText(String.format("%.1f%% (+S/. %.2f)", margenPct, (pvp - costo)));

        // Farmacología
        lblHubPrincipioActivo.setText(p.getPrincipioActivo());
        lblHubPresentacion.setText(p.getPresentacion());
        lblHubEspecies.setText(p.getEspecieDestino());

        // Chips Sanitarios
        if (p.isRequiereReceta()) {
            lblHubChipReceta.setText("Receta Retenida Obligatoria");
            lblHubChipReceta.setForeground(new Color(126, 34, 206));
        } else {
            lblHubChipReceta.setText("Venta Libre Veterinaria");
            lblHubChipReceta.setForeground(new Color(16, 185, 129));
        }

        if (p.isCadenaFrio()) {
            lblHubChipCadenaFrio.setText("Cadena de Frío (2°C - 8°C)");
            lblHubChipCadenaFrio.setForeground(new Color(2, 132, 199));
        } else {
            lblHubChipCadenaFrio.setText("Temperatura Ambiente");
            lblHubChipCadenaFrio.setForeground(Ui.TEXTO_TITULO);
        }

        lblHubStockMinimo.setText(p.getStockMinimo() + " unidades (Punto de reposición)");
        lblHubStockMinimo.setForeground(Ui.TEXTO_TITULO);

        // Trazabilidad
        lblHubLoteVigente.setText(p.getLoteActual());

        long diasRestantes = ChronoUnit.DAYS.between(LocalDate.now(), p.getFechaVencimiento());
        String caducidadTexto = p.getFechaVencimientoFormateada() + " (" + diasRestantes + " días)";
        lblHubFechaCaducidad.setText(caducidadTexto);
        lblHubFechaCaducidad.setForeground(diasRestantes <= 60 ? Ui.COLOR_PELIGRO : Ui.TEXTO_REGULAR);

        lblHubProveedor.setText(p.getProveedor());

        contenedorPadre.revalidate();
        contenedorPadre.repaint();
    }

    private void abrirModalNuevoProducto() {
        FormularioProductoFarmaciaDialog dlg = new FormularioProductoFarmaciaDialog(
                (java.awt.Frame) javax.swing.SwingUtilities.getWindowAncestor(this)
        );
        dlg.setVisible(true);
        if (dlg.isGuardadoExitoso()) {
            recargarDatos();
        }
    }

    private void abrirModalEditar() {
        if (productoSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto para editar o eliminar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Object[] opciones = {"Editar Datos", "Dar de Baja / Eliminar", "Cancelar"};
        int res = JOptionPane.showOptionDialog(this,
                "¿Qué acción desea realizar sobre " + productoSeleccionado.getNombre() + " (" + productoSeleccionado.getCodigo() + ")?",
                "Gestión de Producto",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                opciones,
                opciones[0]);

        if (res == 0) {
            FormularioProductoFarmaciaDialog dlg = new FormularioProductoFarmaciaDialog(
                    (java.awt.Frame) javax.swing.SwingUtilities.getWindowAncestor(this),
                    productoSeleccionado
            );
            dlg.setVisible(true);
            if (dlg.isGuardadoExitoso()) {
                recargarDatos();
            }
        } else if (res == 1) {
            int conf = JOptionPane.showConfirmDialog(this,
                    "¿Está seguro de eliminar " + productoSeleccionado.getNombre() + " del catálogo?",
                    "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);
            if (conf == JOptionPane.YES_OPTION) {
                repo.eliminarProductoFarmacia(productoSeleccionado.getCodigo());
                recargarDatos();
            }
        }
    }

    private void realizarAjusteRapidoStock() {
        if (productoSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto para ajustar stock.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String cantStr = JOptionPane.showInputDialog(this,
                "Ajuste rápido de inventario para " + productoSeleccionado.getNombre() + ":\n" +
                "Stock actual: " + productoSeleccionado.getStockActual() + " " + productoSeleccionado.getPresentacion() + "\n\n" +
                "Ingrese delta de cantidad (positivo para ingreso, negativo para egreso):",
                "10");

        if (cantStr != null && !cantStr.trim().isEmpty()) {
            try {
                int delta = Integer.parseInt(cantStr.trim());
                repo.actualizarStockProducto(productoSeleccionado.getCodigo(), delta);
                recargarDatos();
                JOptionPane.showMessageDialog(this,
                        "Stock actualizado. Nuevo stock disponible: " + productoSeleccionado.getStockActual(),
                        "Ajuste Exitoso", JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Debe ingresar un número entero válido.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void emitirFichaTecnica() {
        if (productoSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto para generar la ficha técnica.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        long diasVenc = ChronoUnit.DAYS.between(LocalDate.now(), productoSeleccionado.getFechaVencimiento());

        String ficha = "FICHA TÉCNICA FARMACÉUTICA Y CONTROL DE DISPENSACIÓN\n" +
                "HAPPY PETS - SERVICIO DE FARMACIA VETERINARIA ESPECIALIZADA\n" +
                "=========================================================================\n\n" +
                "CÓDIGO INTERNO   : " + productoSeleccionado.getCodigo() + "\n" +
                "PRODUCTO         : " + productoSeleccionado.getNombre() + "\n" +
                "CATEGORÍA        : " + productoSeleccionado.getCategoria() + "\n" +
                "PRINCIPIO ACTIVO : " + productoSeleccionado.getPrincipioActivo() + "\n" +
                "PRESENTACIÓN     : " + productoSeleccionado.getPresentacion() + "\n" +
                "ESPECIE DESTINO  : " + productoSeleccionado.getEspecieDestino() + "\n\n" +
                "PARÁMETROS FINANCIEROS Y DISPONIBILIDAD:\n" +
                "• Stock Actual   : " + productoSeleccionado.getStockActual() + " unidades\n" +
                "• Stock Mínimo   : " + productoSeleccionado.getStockMinimo() + " unidades de seguridad\n" +
                "• Precio Costo   : S/. " + String.format("%.2f", productoSeleccionado.getPrecioCosto()) + "\n" +
                "• Precio Venta   : S/. " + String.format("%.2f", productoSeleccionado.getPrecioVenta()) + "\n" +
                "• Estado Actual  : " + productoSeleccionado.getEstado() + "\n\n" +
                "TRAZABILIDAD Y CADUCIDAD:\n" +
                "• Lote Activo    : " + productoSeleccionado.getLoteActual() + "\n" +
                "• Vencimiento    : " + productoSeleccionado.getFechaVencimientoFormateada() + " (" + diasVenc + " días restantes)\n" +
                "• Proveedor      : " + productoSeleccionado.getProveedor() + "\n" +
                "• Cadena de Frío : " + (productoSeleccionado.isCadenaFrio() ? "ESTRICTA 2°C - 8°C (Refrigeración)" : "Temperatura ambiente") + "\n" +
                "• Control Receta : " + (productoSeleccionado.isRequiereReceta() ? "FÁRMACO CONTROLADO - Requiere receta médica" : "Venta libre veterinaria") + "\n\n" +
                "__________________________             __________________________\n" +
                "  Regente Farmacéutico                  Dirección Médica Happy Pets\n";

        Ui.mostrarVisorReporte(
                javax.swing.SwingUtilities.getWindowAncestor(this),
                "Ficha Técnica - " + productoSeleccionado.getCodigo(),
                "FICHA TÉCNICA FARMACÉUTICA",
                ficha,
                "Ficha_Tecnica_" + productoSeleccionado.getCodigo()
        );
    }

    private void imprimirEtiquetaTermica() {
        if (productoSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto para generar etiqueta de farmacia.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String etiqueta = "========================================\n" +
                "      FARMACIA VETERINARIA HAPPY PETS   \n" +
                "   Av. Primavera 1230 - Tel: 984 552 110\n" +
                "========================================\n\n" +
                "PRODUCTO : " + productoSeleccionado.getNombre() + "\n" +
                "COMP.    : " + productoSeleccionado.getPrincipioActivo() + "\n" +
                "PRES.    : " + productoSeleccionado.getPresentacion() + "\n" +
                "LOTE     : " + productoSeleccionado.getLoteActual() + "  |  VENC: " + productoSeleccionado.getFechaVencimientoFormateada() + "\n" +
                "PVP      : S/. " + String.format("%.2f", productoSeleccionado.getPrecioVenta()) + "\n" +
                "ESPECIE  : " + productoSeleccionado.getEspecieDestino() + "\n\n" +
                "CONSERVACIÓN: " + (productoSeleccionado.isCadenaFrio() ? "Refrigerar 2°C - 8°C" : "Lugar fresco y seco") + "\n" +
                "CONDICIÓN   : " + (productoSeleccionado.isRequiereReceta() ? "Venta bajo receta retenida" : "Uso veterinario libre") + "\n\n" +
                "POSOLOGÍA: Según indicación médica veterinaria.\n" +
                "========================================\n";

        Ui.mostrarVisorReporte(
                javax.swing.SwingUtilities.getWindowAncestor(this),
                "Etiqueta Farmacia - " + productoSeleccionado.getCodigo(),
                "ETIQUETA TÉRMICA DE DISPENSACIÓN",
                etiqueta,
                "Etiqueta_" + productoSeleccionado.getCodigo()
        );
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
        for (Component c : panelPillsCategoria.getComponents()) {
            if (c instanceof JButton) {
                JButton b = (JButton) c;
                boolean act = b.getText().equalsIgnoreCase(categoriaFiltroSeleccionada);
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
