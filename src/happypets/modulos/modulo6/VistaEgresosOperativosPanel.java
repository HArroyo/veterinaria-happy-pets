package happypets.modulos.modulo6;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import happypets.data.RepositorioVeterinaria;
import happypets.model.EgresoOperativo;
import happypets.ui.Iconos;
import happypets.ui.Ui;

/**
 * Submódulo 6.4: Control de Egresos Operativos y Gestión Presupuestaria.
 * Arquitectura Master-Detail (58% / 42%) - Opción A Recomendada:
 * - Master Table: Registro analítico de gastos operativos, proveedores, centros de costos y medios de pago.
 * - Live Expense Hub: Telemetría 2x2, imputación presupuestal, auditoría y acciones 1-click.
 */
public class VistaEgresosOperativosPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final RepositorioVeterinaria repo = RepositorioVeterinaria.getInstancia();

    // KPIs
    private JLabel lblKpiTotalEgresos;
    private JLabel lblKpiCantEgresos;
    private JLabel lblKpiMayorCategoria;
    private JLabel lblKpiPromedioEgreso;

    // Tabla Maestra (Izquierda - 58%)
    private JTable tablaEgresos;
    private DefaultTableModel modeloEgresos;
    private JTextField txtBuscar;
    private String filtroCategoriaSeleccionada = "TODOS";
    private final List<EgresoOperativo> listaEgresosFiltrada = new ArrayList<>();

    // Live Expense Hub (Derecha - 42%)
    private JPanel panelHubContenido;
    private JPanel panelHubVacio;
    private JPanel panelHubDetalle;

    private JLabel lblHubAvatar;
    private JLabel lblHubTitulo;
    private JLabel lblHubSubtitulo;
    private JLabel lblHubChipEstado;

    // Telemetría 2x2
    private JLabel lblHubMontoGasto;
    private JLabel lblHubFechaLiquidacion;
    private JLabel lblHubMetodoPago;
    private JLabel lblHubEstadoGasto;

    // Secciones de Detalle
    private JLabel lblHubProveedor;
    private JLabel lblHubComprobante;
    private JLabel lblHubCentroCosto;
    private JLabel lblHubDescripcion;

    private EgresoOperativo egresoSeleccionado;

    public VistaEgresosOperativosPanel() {
        setLayout(new BorderLayout());
        setOpaque(false);

        JPanel panelContenido = new JPanel();
        panelContenido.setOpaque(false);
        panelContenido.setLayout(new BoxLayout(panelContenido, BoxLayout.Y_AXIS));
        panelContenido.setBorder(new EmptyBorder(12, 18, 14, 18));

        // 1. Cabecera Principal
        panelContenido.add(crearCabeceraVista());
        panelContenido.add(Box.createVerticalStrut(10));

        // 2. Fila de KPIs
        panelContenido.add(crearFilaKpis());
        panelContenido.add(Box.createVerticalStrut(12));

        // 3. Contenedor Master-Detail (58% / 42%)
        panelContenido.add(crearSplitMasterDetail());

        add(panelContenido, BorderLayout.CENTER);

        recargarDatos();
    }

    private JPanel crearCabeceraVista() {
        JPanel cab = new JPanel(new BorderLayout());
        cab.setOpaque(false);

        JPanel izq = new JPanel();
        izq.setOpaque(false);
        izq.setLayout(new BoxLayout(izq, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("Gestión y Control de Egresos Operativos");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 17));
        titulo.setForeground(Ui.TEXTO_TITULO);

        JLabel subtitulo = new JLabel("Módulo 6.4 · Presupuesto de gastos fijos y variables, servicios y proveedores administrativos");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitulo.setForeground(Ui.TEXTO_MUTED);

        izq.add(titulo);
        izq.add(Box.createVerticalStrut(2));
        izq.add(subtitulo);
        cab.add(izq, BorderLayout.WEST);

        JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        der.setOpaque(false);

        JButton btnNuevoEgreso = Ui.boton("+ Registrar Egreso Operativo", true);
        btnNuevoEgreso.setIcon(Iconos.crearIconoEgresos(14, Color.WHITE));
        btnNuevoEgreso.addActionListener(e -> abrirDialogoNuevoEgreso());
        der.add(btnNuevoEgreso);

        JButton btnActualizar = Ui.boton("Actualizar", false);
        btnActualizar.addActionListener(e -> recargarDatos());
        der.add(btnActualizar);

        cab.add(der, BorderLayout.EAST);
        return cab;
    }

    private JPanel crearFilaKpis() {
        JPanel fila = new JPanel(new GridLayout(1, 4, 12, 0));
        fila.setOpaque(false);
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 74));

        lblKpiTotalEgresos = new JLabel("S/. 0.00");
        lblKpiCantEgresos = new JLabel("0");
        lblKpiMayorCategoria = new JLabel("-");
        lblKpiPromedioEgreso = new JLabel("S/. 0.00");

        fila.add(crearCardKpi("Total Egresos Presupuestados", lblKpiTotalEgresos, "Gasto acumulado del período", Iconos.crearIconoEgresos(22, Ui.COLOR_PELIGRO)));
        fila.add(crearCardKpi("Egresos Registrados", lblKpiCantEgresos, "Comprobantes procesados", Iconos.crearIconoFactura(22, Ui.TURQUESA_OSCURO)));
        fila.add(crearCardKpi("Mayor Centro de Costo", lblKpiMayorCategoria, "Categoría con mayor consumo", Iconos.crearIconoGraficoBarras(22, new Color(217, 119, 6))));
        fila.add(crearCardKpi("Gasto Promedio por Operación", lblKpiPromedioEgreso, "Media por transacción", Iconos.crearIconoMonedas(22, Ui.TURQUESA)));

        return fila;
    }

    private JPanel crearCardKpi(String titulo, JLabel lblValor, String subtitulo, javax.swing.Icon icono) {
        JPanel card = new JPanel(new BorderLayout(8, 0)) {
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
        card.setBorder(new EmptyBorder(10, 14, 10, 14));

        JPanel texto = new JPanel();
        texto.setOpaque(false);
        texto.setLayout(new BoxLayout(texto, BoxLayout.Y_AXIS));

        JLabel lblTit = new JLabel(titulo);
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblTit.setForeground(Ui.TEXTO_MUTED);

        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblValor.setForeground(Ui.TEXTO_TITULO);

        JLabel lblSub = new JLabel(subtitulo);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblSub.setForeground(Ui.TEXTO_MUTED);

        texto.add(lblTit);
        texto.add(Box.createVerticalStrut(2));
        texto.add(lblValor);
        texto.add(Box.createVerticalStrut(2));
        texto.add(lblSub);

        card.add(texto, BorderLayout.CENTER);
        if (icono != null) {
            JLabel lblIco = new JLabel(icono);
            lblIco.setVerticalAlignment(SwingConstants.TOP);
            card.add(lblIco, BorderLayout.EAST);
        }

        return card;
    }

    private JPanel crearSplitMasterDetail() {
        JPanel split = new JPanel(new GridBagLayout());
        split.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;

        // 58% Tabla Maestra Izquierda
        gbc.gridx = 0;
        gbc.weightx = 0.58;
        gbc.insets = new java.awt.Insets(0, 0, 0, 12);
        split.add(crearPanelMasterTable(), gbc);

        // 42% Live Hub Derecha
        gbc.gridx = 1;
        gbc.weightx = 0.42;
        gbc.insets = new java.awt.Insets(0, 0, 0, 0);
        split.add(crearPanelLiveHub(), gbc);

        return split;
    }

    private JPanel crearPanelMasterTable() {
        JPanel master = new JPanel(new BorderLayout(0, 10)) {
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
        master.setOpaque(false);
        master.setBorder(new EmptyBorder(12, 14, 12, 14));

        // Filtros y Buscador
        JPanel cabFiltros = new JPanel();
        cabFiltros.setOpaque(false);
        cabFiltros.setLayout(new BoxLayout(cabFiltros, BoxLayout.Y_AXIS));

        JPanel barraBuscar = new JPanel(new BorderLayout(8, 0));
        barraBuscar.setOpaque(false);

        JLabel lblBuscar = new JLabel("Buscar:");
        lblBuscar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblBuscar.setForeground(Ui.TEXTO_MUTED);
        barraBuscar.add(lblBuscar, BorderLayout.WEST);

        txtBuscar = new JTextField();
        txtBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtBuscar.putClientProperty("JTextField.placeholderText", "Buscar por descripción, proveedor o comprobante...");
        txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filtrarEgresos(); }
            public void removeUpdate(DocumentEvent e) { filtrarEgresos(); }
            public void changedUpdate(DocumentEvent e) { filtrarEgresos(); }
        });
        barraBuscar.add(txtBuscar, BorderLayout.CENTER);
        cabFiltros.add(barraBuscar);
        cabFiltros.add(Box.createVerticalStrut(8));

        // Pills de Filtro
        JPanel pills = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        pills.setOpaque(false);

        String[] categorias = {"TODOS", "Servicios", "Alquiler", "Planilla", "Suministros", "Mantenimiento"};
        for (String c : categorias) {
            JButton btnPill = new JButton(c);
            btnPill.setFont(new Font("Segoe UI", Font.BOLD, 11));
            btnPill.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btnPill.setBorder(new EmptyBorder(4, 10, 4, 10));
            btnPill.setFocusPainted(false);

            if (c.equals(filtroCategoriaSeleccionada)) {
                btnPill.setBackground(Ui.TURQUESA);
                btnPill.setForeground(Color.WHITE);
            } else {
                btnPill.setBackground(new Color(241, 245, 249));
                btnPill.setForeground(Ui.TEXTO_MUTED);
            }

            btnPill.addActionListener(e -> {
                filtroCategoriaSeleccionada = c;
                for (Component comp : pills.getComponents()) {
                    if (comp instanceof JButton) {
                        JButton b = (JButton) comp;
                        if (b.getText().equals(filtroCategoriaSeleccionada)) {
                            b.setBackground(Ui.TURQUESA);
                            b.setForeground(Color.WHITE);
                        } else {
                            b.setBackground(new Color(241, 245, 249));
                            b.setForeground(Ui.TEXTO_MUTED);
                        }
                    }
                }
                filtrarEgresos();
            });
            pills.add(btnPill);
        }
        cabFiltros.add(pills);
        master.add(cabFiltros, BorderLayout.NORTH);

        // Tabla
        String[] cols = {"ID Egreso", "Categoría / Rubro", "Descripción del Gasto", "Proveedor", "Monto S/.", "Fecha", "Método"};
        modeloEgresos = new DefaultTableModel(cols, 0) {
            private static final long serialVersionUID = 1L;
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tablaEgresos = new JTable(modeloEgresos);
        tablaEgresos.setRowHeight(48);
        tablaEgresos.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaEgresos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaEgresos.setShowVerticalLines(false);
        tablaEgresos.setGridColor(new Color(241, 245, 249));
        tablaEgresos.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        tablaEgresos.getTableHeader().setBackground(new Color(248, 250, 252));
        tablaEgresos.getTableHeader().setForeground(Ui.TEXTO_MUTED);

        tablaEgresos.getColumnModel().getColumn(0).setPreferredWidth(75);
        tablaEgresos.getColumnModel().getColumn(1).setPreferredWidth(120);
        tablaEgresos.getColumnModel().getColumn(2).setPreferredWidth(180);
        tablaEgresos.getColumnModel().getColumn(3).setPreferredWidth(120);
        tablaEgresos.getColumnModel().getColumn(4).setPreferredWidth(80);
        tablaEgresos.getColumnModel().getColumn(5).setPreferredWidth(80);
        tablaEgresos.getColumnModel().getColumn(6).setPreferredWidth(85);

        // Monograma en columna 2 (Descripción)
        tablaEgresos.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            private static final long serialVersionUID = 1L;
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                JPanel cell = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
                cell.setOpaque(true);
                cell.setBackground(isSelected ? table.getSelectionBackground() : (row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252)));

                String desc = value != null ? value.toString() : "";
                String letra = !desc.isEmpty() ? desc.substring(0, 1).toUpperCase() : "E";

                JLabel avatar = new JLabel(letra, SwingConstants.CENTER) {
                    private static final long serialVersionUID = 1L;
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(new Color(254, 242, 242));
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

                JLabel lblTexto = new JLabel(desc);
                lblTexto.setFont(new Font("Segoe UI", Font.BOLD, 11));
                lblTexto.setForeground(isSelected ? table.getSelectionForeground() : Ui.TEXTO_TITULO);

                cell.add(avatar);
                cell.add(lblTexto);
                return cell;
            }
        });

        // Render Monto S/. (Columna 4)
        tablaEgresos.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            private static final long serialVersionUID = 1L;
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                l.setHorizontalAlignment(SwingConstants.RIGHT);
                l.setFont(new Font("Segoe UI", Font.BOLD, 12));
                l.setForeground(Ui.COLOR_PELIGRO);
                return l;
            }
        });

        tablaEgresos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int r = tablaEgresos.getSelectedRow();
                if (r >= 0 && r < listaEgresosFiltrada.size()) {
                    mostrarDetalleEgreso(listaEgresosFiltrada.get(r));
                } else {
                    mostrarDetalleEgreso(null);
                }
            }
        });

        JScrollPane scroll = new JScrollPane(tablaEgresos);
        scroll.setBorder(BorderFactory.createLineBorder(Ui.BORDE_SUAVE, 1));
        master.add(scroll, BorderLayout.CENTER);

        return master;
    }

    private JPanel crearPanelLiveHub() {
        panelHubContenido = new JPanel(new BorderLayout());
        panelHubContenido.setOpaque(false);

        // Panel Hub Vacío
        panelHubVacio = new JPanel(new BorderLayout());
        panelHubVacio.setOpaque(false);
        JLabel lblVacio = new JLabel("Seleccione un egreso para consultar el Live Expense Hub", SwingConstants.CENTER);
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

        lblHubAvatar = new JLabel("E", SwingConstants.CENTER) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int diametro = Math.min(getWidth(), getHeight()) - 2;
                if (diametro > 0) {
                    g2.setColor(new Color(254, 242, 242));
                    g2.fill(new Ellipse2D.Double(1, 1, diametro, diametro));
                    g2.setColor(new Color(254, 202, 202));
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

        JPanel titulosHub = new JPanel();
        titulosHub.setOpaque(false);
        titulosHub.setLayout(new BoxLayout(titulosHub, BoxLayout.Y_AXIS));

        lblHubTitulo = new JLabel("Descripción del Egreso");
        lblHubTitulo.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblHubTitulo.setForeground(Ui.TEXTO_TITULO);

        lblHubSubtitulo = new JLabel("EGR-001 · Servicios básicos");
        lblHubSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblHubSubtitulo.setForeground(Ui.TEXTO_MUTED);

        titulosHub.add(lblHubTitulo);
        titulosHub.add(Box.createVerticalStrut(2));
        titulosHub.add(lblHubSubtitulo);
        cabHub.add(titulosHub, BorderLayout.CENTER);

        lblHubChipEstado = new JLabel("  Pagado  ");
        lblHubChipEstado.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblHubChipEstado.setForeground(Ui.COLOR_EXITO);
        lblHubChipEstado.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(187, 247, 208), 1),
                new EmptyBorder(4, 8, 4, 8)
        ));
        cabHub.add(lblHubChipEstado, BorderLayout.EAST);
        panelHubDetalle.add(cabHub, BorderLayout.NORTH);

        // 2. Centro con Telemetría 2x2 y Especificaciones
        JPanel centroHub = new JPanel();
        centroHub.setOpaque(false);
        centroHub.setLayout(new BoxLayout(centroHub, BoxLayout.Y_AXIS));

        // Telemetría 2x2
        JPanel gridTelemetria = new JPanel(new GridLayout(2, 2, 8, 8));
        gridTelemetria.setOpaque(false);
        gridTelemetria.setMaximumSize(new Dimension(Integer.MAX_VALUE, 115));

        lblHubMontoGasto = new JLabel("S/. 0.00");
        lblHubFechaLiquidacion = new JLabel("00/00/0000");
        lblHubMetodoPago = new JLabel("Transferencia");
        lblHubEstadoGasto = new JLabel("Pagado");

        gridTelemetria.add(crearCardMiniTelemetria("MONTO DEL GASTO", lblHubMontoGasto, Ui.COLOR_PELIGRO));
        gridTelemetria.add(crearCardMiniTelemetria("FECHA LIQUIDACIÓN", lblHubFechaLiquidacion, Ui.TEXTO_TITULO));
        gridTelemetria.add(crearCardMiniTelemetria("MÉTODO DE PAGO", lblHubMetodoPago, Ui.TURQUESA_OSCURO));
        gridTelemetria.add(crearCardMiniTelemetria("ESTADO CONTABLE", lblHubEstadoGasto, Ui.COLOR_EXITO));

        centroHub.add(gridTelemetria);
        centroHub.add(Box.createVerticalStrut(8));

        // Especificaciones
        centroHub.add(crearSeccionTitulo("PROVEEDOR & DOCUMENTO FISCAL"));
        lblHubProveedor = new JLabel("Proveedor / Beneficiario: -");
        lblHubProveedor.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblHubComprobante = new JLabel("N° Factura / Operación: -");
        lblHubComprobante.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        centroHub.add(lblHubProveedor);
        centroHub.add(lblHubComprobante);
        centroHub.add(Box.createVerticalStrut(8));

        centroHub.add(crearSeccionTitulo("IMPUTACIÓN PRESUPUESTAL & CENTRO DE COSTO"));
        lblHubCentroCosto = new JLabel("Rubro / Centro de Costo: -");
        lblHubCentroCosto.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblHubDescripcion = new JLabel("Detalle del Gasto: -");
        lblHubDescripcion.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        centroHub.add(lblHubCentroCosto);
        centroHub.add(lblHubDescripcion);

        panelHubDetalle.add(centroHub, BorderLayout.CENTER);

        // 3. Footer con 4 Botones de Acción 1-click
        JPanel gridAcciones = new JPanel(new GridLayout(2, 2, 8, 8));
        gridAcciones.setOpaque(false);
        gridAcciones.setMaximumSize(new Dimension(Integer.MAX_VALUE, 76));

        JButton btnComprobantePDF = Ui.boton("Comprobante PDF", true);
        btnComprobantePDF.setIcon(Iconos.crearIconoFactura(14, Color.WHITE));
        btnComprobantePDF.addActionListener(e -> emitirComprobanteEgresoPDF());

        JButton btnNotificarAdmin = Ui.boton("Notificar Auditoría", false);
        btnNotificarAdmin.setIcon(Iconos.crearIconoMensaje(14, Ui.TURQUESA_OSCURO));
        btnNotificarAdmin.addActionListener(e -> notificarAuditoriaAdmin());

        JButton btnCertPresupuesto = Ui.boton("Certificado Gasto", false);
        btnCertPresupuesto.setIcon(Iconos.crearIconoExportar(14, Ui.TEXTO_TITULO));
        btnCertPresupuesto.addActionListener(e -> emitirCertificadoPresupuestalPDF());

        JButton btnEditarAnular = Ui.boton("Editar / Anular", false);
        btnEditarAnular.setIcon(Iconos.crearIconoEditar(14, Ui.COLOR_PELIGRO));
        btnEditarAnular.addActionListener(e -> editarOAnularEgreso());

        gridAcciones.add(btnComprobantePDF);
        gridAcciones.add(btnNotificarAdmin);
        gridAcciones.add(btnCertPresupuesto);
        gridAcciones.add(btnEditarAnular);

        panelHubDetalle.add(gridAcciones, BorderLayout.SOUTH);

        panelHubContenido.add(panelHubVacio, BorderLayout.CENTER);
        return panelHubContenido;
    }

    private JPanel crearCardMiniTelemetria(String titulo, JLabel lblValor, Color colorValor) {
        JPanel c = new JPanel(new BorderLayout());
        c.setBackground(new Color(248, 250, 252));
        c.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Ui.BORDE_SUAVE, 1),
                new EmptyBorder(6, 8, 6, 8)
        ));

        JLabel t = new JLabel(titulo);
        t.setFont(new Font("Segoe UI", Font.BOLD, 9));
        t.setForeground(Ui.TEXTO_MUTED);

        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblValor.setForeground(colorValor);

        c.add(t, BorderLayout.NORTH);
        c.add(lblValor, BorderLayout.CENTER);
        return c;
    }

    private JPanel crearSeccionTitulo(String titulo) {
        JPanel s = new JPanel(new BorderLayout(6, 0));
        s.setOpaque(false);
        JLabel l = new JLabel(titulo);
        l.setFont(new Font("Segoe UI", Font.BOLD, 10));
        l.setForeground(Ui.TURQUESA_PROFUNDO);
        s.add(l, BorderLayout.WEST);
        s.add(new JSeparator(), BorderLayout.CENTER);
        s.setBorder(new EmptyBorder(4, 0, 4, 0));
        return s;
    }

    public void recargarDatos() {
        actualizarKpis();
        filtrarEgresos();
    }

    private void actualizarKpis() {
        List<EgresoOperativo> egresos = repo.getEgresosOperativos();
        double total = 0.0;
        Map<String, Double> porCat = new HashMap<>();

        for (EgresoOperativo e : egresos) {
            total += e.getMonto();
            porCat.put(e.getCategoria(), porCat.getOrDefault(e.getCategoria(), 0.0) + e.getMonto());
        }

        lblKpiTotalEgresos.setText(String.format("S/. %.2f", total));
        lblKpiCantEgresos.setText(String.valueOf(egresos.size()));

        String mayorCat = "-";
        double maxMonto = 0.0;
        for (Map.Entry<String, Double> entry : porCat.entrySet()) {
            if (entry.getValue() > maxMonto) {
                maxMonto = entry.getValue();
                mayorCat = entry.getKey();
            }
        }
        lblKpiMayorCategoria.setText(mayorCat);

        double promedio = !egresos.isEmpty() ? (total / egresos.size()) : 0.0;
        lblKpiPromedioEgreso.setText(String.format("S/. %.2f", promedio));
    }

    private void filtrarEgresos() {
        String query = txtBuscar != null ? txtBuscar.getText().trim().toLowerCase() : "";
        List<EgresoOperativo> todos = repo.getEgresosOperativos();
        listaEgresosFiltrada.clear();

        for (EgresoOperativo e : todos) {
            boolean coincideCat = true;
            if (!"TODOS".equals(filtroCategoriaSeleccionada)) {
                coincideCat = e.getCategoria().toLowerCase().contains(filtroCategoriaSeleccionada.toLowerCase());
            }
            if (!coincideCat) continue;

            if (!query.isEmpty()) {
                boolean match = e.getIdEgreso().toLowerCase().contains(query)
                        || e.getDescripcion().toLowerCase().contains(query)
                        || e.getProveedor().toLowerCase().contains(query)
                        || e.getComprobante().toLowerCase().contains(query)
                        || e.getMetodoPago().toLowerCase().contains(query);
                if (!match) continue;
            }

            listaEgresosFiltrada.add(e);
        }

        modeloEgresos.setRowCount(0);
        for (EgresoOperativo e : listaEgresosFiltrada) {
            modeloEgresos.addRow(new Object[]{
                    e.getIdEgreso(),
                    e.getCategoria(),
                    e.getDescripcion(),
                    e.getProveedor(),
                    String.format("%.2f", e.getMonto()),
                    e.getFecha() != null ? e.getFecha().format(FORMATO_FECHA) : "-",
                    e.getMetodoPago()
            });
        }

        if (!listaEgresosFiltrada.isEmpty()) {
            tablaEgresos.setRowSelectionInterval(0, 0);
            mostrarDetalleEgreso(listaEgresosFiltrada.get(0));
        } else {
            mostrarDetalleEgreso(null);
        }
    }

    private void mostrarDetalleEgreso(EgresoOperativo e) {
        this.egresoSeleccionado = e;
        panelHubContenido.removeAll();

        if (e == null) {
            panelHubContenido.add(panelHubVacio, BorderLayout.CENTER);
            panelHubContenido.revalidate();
            panelHubContenido.repaint();
            return;
        }

        lblHubAvatar.setText(e.getCategoria() != null && !e.getCategoria().isEmpty() ? e.getCategoria().substring(0, 1).toUpperCase() : "E");
        lblHubTitulo.setText(e.getDescripcion());
        lblHubSubtitulo.setText(e.getIdEgreso() + " · " + e.getCategoria());
        lblHubChipEstado.setText("  " + e.getEstado() + "  ");

        if ("Pagado".equalsIgnoreCase(e.getEstado())) {
            lblHubChipEstado.setForeground(Ui.COLOR_EXITO);
            lblHubChipEstado.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(187, 247, 208), 1),
                    new EmptyBorder(4, 8, 4, 8)
            ));
        } else if ("Programado".equalsIgnoreCase(e.getEstado())) {
            lblHubChipEstado.setForeground(new Color(217, 119, 6));
            lblHubChipEstado.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(254, 215, 170), 1),
                    new EmptyBorder(4, 8, 4, 8)
            ));
        } else {
            lblHubChipEstado.setForeground(Ui.TURQUESA_OSCURO);
            lblHubChipEstado.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(153, 246, 228), 1),
                    new EmptyBorder(4, 8, 4, 8)
            ));
        }

        lblHubMontoGasto.setText(String.format("S/. %.2f", e.getMonto()));
        lblHubFechaLiquidacion.setText(e.getFecha() != null ? e.getFecha().format(FORMATO_FECHA) : "-");
        lblHubMetodoPago.setText(e.getMetodoPago());
        lblHubEstadoGasto.setText(e.getEstado());

        lblHubProveedor.setText("Proveedor / Beneficiario: " + e.getProveedor());
        lblHubComprobante.setText("Comprobante / N° Op: " + e.getComprobante());
        lblHubCentroCosto.setText("Centro de Costo: " + e.getCategoria());
        lblHubDescripcion.setText("Detalle del Gasto: " + e.getDescripcion());

        panelHubContenido.add(panelHubDetalle, BorderLayout.CENTER);
        panelHubContenido.revalidate();
        panelHubContenido.repaint();
    }

    private void abrirDialogoNuevoEgreso() {
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        FormularioEgresoOperativoDialog dlg = new FormularioEgresoOperativoDialog(parent);
        dlg.setVisible(true);
        if (dlg.isConfirmado()) {
            recargarDatos();
        }
    }

    private void emitirComprobanteEgresoPDF() {
        if (egresoSeleccionado == null) return;

        List<Object[]> filas = new ArrayList<>();
        filas.add(new Object[]{
                egresoSeleccionado.getIdEgreso(),
                egresoSeleccionado.getCategoria(),
                egresoSeleccionado.getDescripcion(),
                egresoSeleccionado.getProveedor(),
                String.format("S/ %.2f", egresoSeleccionado.getMonto()),
                egresoSeleccionado.getMetodoPago()
        });

        Ui.mostrarVisorReporte(
                (Frame) SwingUtilities.getWindowAncestor(this),
                "Comprobante de Egreso " + egresoSeleccionado.getIdEgreso(),
                "Voucher Oficial de Desembolso Presupuestal Happy Pets",
                new String[][]{
                        {"N° Operación", egresoSeleccionado.getIdEgreso()},
                        {"Fecha de Liquidación", egresoSeleccionado.getFecha() != null ? egresoSeleccionado.getFecha().format(FORMATO_FECHA) : "-"},
                        {"Beneficiario", egresoSeleccionado.getProveedor()},
                        {"Rubro / Categoría", egresoSeleccionado.getCategoria()},
                        {"Comprobante / Factura", egresoSeleccionado.getComprobante()},
                        {"Método de Pago", egresoSeleccionado.getMetodoPago()}
                },
                new String[]{"ID Egreso", "Categoría", "Descripción", "Proveedor", "Monto Desembolsado", "Medio Pago"},
                filas,
                "MONTO LIQUIDADO: S/ " + String.format("%.2f", egresoSeleccionado.getMonto())
                        + " | Centro de Costo Imputado: " + egresoSeleccionado.getCategoria()
                        + " | Estado: " + egresoSeleccionado.getEstado(),
                "egreso_" + egresoSeleccionado.getIdEgreso()
        );
    }

    private void notificarAuditoriaAdmin() {
        if (egresoSeleccionado == null) return;
        JOptionPane.showMessageDialog(this,
                "Notificación enviada a la Oficina de Contabilidad & Auditoría:\n" +
                        "Gasto: " + egresoSeleccionado.getIdEgreso() + " (" + egresoSeleccionado.getDescripcion() + ")\n" +
                        "Monto: S/. " + String.format("%.2f", egresoSeleccionado.getMonto()) + "\n" +
                        "Proveedor: " + egresoSeleccionado.getProveedor() + "\n" +
                        "Registrado en libro diario de egresos con validación conforme.",
                "Auditoría Contable Notificada", JOptionPane.INFORMATION_MESSAGE);
    }

    private void emitirCertificadoPresupuestalPDF() {
        List<Object[]> filas = new ArrayList<>();
        for (EgresoOperativo e : repo.getEgresosOperativos()) {
            filas.add(new Object[]{
                    e.getIdEgreso(),
                    e.getFecha() != null ? e.getFecha().format(FORMATO_FECHA) : "-",
                    e.getCategoria(),
                    e.getProveedor(),
                    String.format("S/ %.2f", e.getMonto()),
                    e.getEstado()
            });
        }

        Ui.mostrarVisorReporte(
                (Frame) SwingUtilities.getWindowAncestor(this),
                "Certificado de Ejecución Presupuestal Mensual",
                "Consolidado de Egresos Operativos y Centros de Costos Happy Pets",
                new String[][]{
                        {"Fecha de Emisión", LocalDate.now().format(FORMATO_FECHA)},
                        {"Período Contable", "Octubre 2026"},
                        {"Total Desembolsado", String.format("S/ %.2f", repo.getTotalEgresosOperativos())},
                        {"N° Operaciones", String.valueOf(repo.getEgresosOperativos().size())},
                        {"Dirección Financiera", "Corrales Contreras, Joanna Andrea"}
                },
                new String[]{"ID Egreso", "Fecha", "Categoría", "Proveedor / Beneficiario", "Monto S/.", "Estado"},
                filas,
                "TOTAL PRESUPUESTO EJECUTADO: S/ " + String.format("%.2f", repo.getTotalEgresosOperativos())
                        + " | Registros Validados: " + repo.getEgresosOperativos().size(),
                "certificado_presupuesto_mensual"
        );
    }

    private void editarOAnularEgreso() {
        if (egresoSeleccionado == null) return;
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);

        String[] opciones = {"Editar Datos", "Anular Egreso", "Cancelar"};
        int sel = JOptionPane.showOptionDialog(this,
                "Seleccione la acción para el gasto " + egresoSeleccionado.getIdEgreso() + " (" + egresoSeleccionado.getDescripcion() + "):",
                "Gestión de Gasto Operativo",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, opciones, opciones[0]);

        if (sel == 0) {
            FormularioEgresoOperativoDialog dlg = new FormularioEgresoOperativoDialog(parent, egresoSeleccionado);
            dlg.setVisible(true);
            if (dlg.isConfirmado()) recargarDatos();
        } else if (sel == 1) {
            int opt = JOptionPane.showConfirmDialog(this,
                    "¿Está seguro de eliminar el registro de egreso " + egresoSeleccionado.getIdEgreso() + "?",
                    "Confirmar Anulación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (opt == JOptionPane.YES_OPTION) {
                repo.eliminarEgresoOperativo(egresoSeleccionado.getIdEgreso());
                recargarDatos();
            }
        }
    }
}
