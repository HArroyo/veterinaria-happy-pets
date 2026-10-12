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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
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
import happypets.model.ItemVentaPOS;
import happypets.model.VentaPOS;
import happypets.ui.Iconos;
import happypets.ui.Ui;

/**
 * Submódulo 6.1: Punto de Venta (POS) y Comprobantes Electrónicos.
 * Arquitectura Master-Detail (58% / 42%) - Opción A Recomendada:
 * - Master Table: Registro maestro de ventas del día, comprobantes electrónicos, métodos de pago y estados.
 * - Live Sale Hub: Telemetría 2x2, desglose de ítems, datos fiscales SUNAT y acciones 1-click.
 */
public class VistaPuntoVentaPOSPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private final RepositorioVeterinaria repo = RepositorioVeterinaria.getInstancia();

    // KPIs
    private JLabel lblKpiRecaudadoHoy;
    private JLabel lblKpiComprobantes;
    private JLabel lblKpiTicketPromedio;
    private JLabel lblKpiDigitales;

    // Tabla Maestra (Izquierda - 58%)
    private JTable tablaVentas;
    private DefaultTableModel modeloVentas;
    private JTextField txtBuscar;
    private String filtroTipoSeleccionado = "TODAS";
    private final List<VentaPOS> listaVentasFiltrada = new ArrayList<>();

    // Live Sale Hub (Derecha - 42%)
    private JPanel panelHubContenido;
    private JPanel panelHubVacio;
    private JPanel panelHubDetalle;

    private JLabel lblHubAvatar;
    private JLabel lblHubTitulo;
    private JLabel lblHubSubtitulo;
    private JLabel lblHubChipEstado;

    // Telemetría 2x2
    private JLabel lblHubTotalCobrado;
    private JLabel lblHubSubtotalNeto;
    private JLabel lblHubIgvFiscal;
    private JLabel lblHubDescuento;

    // Secciones del Hub
    private JTextArea txtHubItemsDetalle;
    private JTextArea lblHubClienteDni;
    private JTextArea lblHubMascota;
    private JTextArea lblHubMetodoPago;
    private JTextArea lblHubCajeroFecha;

    private VentaPOS ventaSeleccionada;

    public VistaPuntoVentaPOSPanel() {
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

        JLabel titulo = new JLabel("Punto de Venta (POS) & Comprobantes Electrónicos");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 17));
        titulo.setForeground(Ui.TEXTO_TITULO);

        JLabel subtitulo = new JLabel("Módulo 6.1 · Emisión de comprobantes, cobros en caja, servicios y conciliación retail");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitulo.setForeground(Ui.TEXTO_MUTED);

        izq.add(titulo);
        izq.add(Box.createVerticalStrut(2));
        izq.add(subtitulo);
        cab.add(izq, BorderLayout.WEST);

        JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        der.setOpaque(false);

        JLabel chipCaja = new JLabel("  Caja Central · En Línea  ") {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(240, 253, 250));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.setColor(Ui.TURQUESA_MEDIO);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        chipCaja.setFont(new Font("Segoe UI", Font.BOLD, 11));
        chipCaja.setForeground(Ui.TURQUESA_PROFUNDO);
        chipCaja.setBorder(new EmptyBorder(4, 10, 4, 10));
        der.add(chipCaja);

        JButton btnNuevaVenta = Ui.boton("+ Nueva Venta POS / Cobro", true);
        btnNuevaVenta.setIcon(Iconos.crearIconoPOS(14, Color.WHITE));
        btnNuevaVenta.addActionListener(e -> abrirDialogoNuevaVenta());
        der.add(btnNuevaVenta);

        JButton btnActualizar = Ui.boton("Actualizar", false);
        btnActualizar.addActionListener(e -> recargarDatos());
        der.add(btnActualizar);

        cab.add(der, BorderLayout.EAST);
        cab.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        return cab;
    }

    private JPanel crearFilaKpis() {
        JPanel fila = new JPanel(new GridLayout(1, 4, 12, 0));
        fila.setOpaque(false);
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 74));

        lblKpiRecaudadoHoy = new JLabel("S/. 0.00");
        lblKpiComprobantes = new JLabel("0");
        lblKpiTicketPromedio = new JLabel("S/. 0.00");
        lblKpiDigitales = new JLabel("0%");

        fila.add(crearCardKpi("Recaudación Hoy (PVP)", lblKpiRecaudadoHoy, "Ingresos netos en caja", Iconos.crearIconoMonedas(22, Ui.TURQUESA)));
        fila.add(crearCardKpi("Comprobantes Emitidos", lblKpiComprobantes, "Boletas, facturas y tickets", Iconos.crearIconoFactura(22, Ui.TURQUESA_OSCURO)));
        fila.add(crearCardKpi("Ticket Promedio", lblKpiTicketPromedio, "Valor medio por operación", Iconos.crearIconoGraficoBarras(22, Ui.COLOR_EXITO)));
        fila.add(crearCardKpi("Ventas Digitales", lblKpiDigitales, "Yape / Tarjeta / Transferencia", Iconos.crearIconoPOS(22, new Color(139, 92, 246))));

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

        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 17));
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

    private JSplitPane crearSplitMasterDetail() {
        JPanel master = crearPanelMasterTable();
        JPanel detail = crearPanelLiveHub();
        detail.setPreferredSize(new Dimension(360, 0));
        detail.setMinimumSize(new Dimension(320, 0));

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, master, detail);
        split.setResizeWeight(1.0);
        split.setDividerSize(6);
        split.setBorder(null);
        split.setOpaque(false);
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
        txtBuscar.putClientProperty("JTextField.placeholderText", "Buscar por N° comprobante, cliente, mascota o método de pago...");
        txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filtrarVentas(); }
            public void removeUpdate(DocumentEvent e) { filtrarVentas(); }
            public void changedUpdate(DocumentEvent e) { filtrarVentas(); }
        });
        barraBuscar.add(txtBuscar, BorderLayout.CENTER);
        cabFiltros.add(barraBuscar);
        cabFiltros.add(Box.createVerticalStrut(8));

        // Pills de Filtro
        JPanel pills = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        pills.setOpaque(false);

        String[] tipos = {"TODAS", "Boleta", "Factura", "Ticket", "Cotización", "Pagada", "Anulada"};
        for (String t : tipos) {
            JButton btnPill = new JButton(t);
            btnPill.setFont(new Font("Segoe UI", Font.BOLD, 11));
            btnPill.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btnPill.setBorder(new EmptyBorder(4, 10, 4, 10));
            btnPill.setFocusPainted(false);

            if (t.equals(filtroTipoSeleccionado)) {
                btnPill.setBackground(Ui.TURQUESA);
                btnPill.setForeground(Color.WHITE);
            } else {
                btnPill.setBackground(new Color(241, 245, 249));
                btnPill.setForeground(Ui.TEXTO_MUTED);
            }

            btnPill.addActionListener(e -> {
                filtroTipoSeleccionado = t;
                for (Component c : pills.getComponents()) {
                    if (c instanceof JButton) {
                        JButton b = (JButton) c;
                        if (b.getText().equals(filtroTipoSeleccionado)) {
                            b.setBackground(Ui.TURQUESA);
                            b.setForeground(Color.WHITE);
                        } else {
                            b.setBackground(new Color(241, 245, 249));
                            b.setForeground(Ui.TEXTO_MUTED);
                        }
                    }
                }
                filtrarVentas();
            });
            pills.add(btnPill);
        }
        cabFiltros.add(pills);
        master.add(cabFiltros, BorderLayout.NORTH);

        // Tabla
        String[] cols = {"N° Comprobante", "Tipo", "Cliente / Paciente", "Método", "Total (S/.)", "Estado"};
        modeloVentas = new DefaultTableModel(cols, 0) {
            private static final long serialVersionUID = 1L;
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tablaVentas = new JTable(modeloVentas);
        tablaVentas.setRowHeight(48);
        tablaVentas.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaVentas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaVentas.setShowVerticalLines(false);
        tablaVentas.setGridColor(new Color(241, 245, 249));
        tablaVentas.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        tablaVentas.getTableHeader().setBackground(new Color(248, 250, 252));
        tablaVentas.getTableHeader().setForeground(Ui.TEXTO_MUTED);

        tablaVentas.getColumnModel().getColumn(0).setPreferredWidth(110);
        tablaVentas.getColumnModel().getColumn(1).setPreferredWidth(100);
        tablaVentas.getColumnModel().getColumn(2).setPreferredWidth(180);
        tablaVentas.getColumnModel().getColumn(3).setPreferredWidth(90);
        tablaVentas.getColumnModel().getColumn(4).setPreferredWidth(85);
        tablaVentas.getColumnModel().getColumn(5).setPreferredWidth(90);

        // Monograma en columna 0
        tablaVentas.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
            private static final long serialVersionUID = 1L;
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                JPanel cell = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
                cell.setOpaque(true);
                cell.setBackground(isSelected ? table.getSelectionBackground() : (row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252)));

                String num = value != null ? value.toString() : "";
                String letra = num.startsWith("B") ? "B" : num.startsWith("F") ? "F" : "T";

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

                JLabel lblNum = new JLabel(num);
                lblNum.setFont(new Font("Segoe UI", Font.BOLD, 11));
                lblNum.setForeground(isSelected ? table.getSelectionForeground() : Ui.TEXTO_TITULO);

                cell.add(avatar);
                cell.add(lblNum);
                return cell;
            }
        });

        // Alineación y Render de Estado (Columna 5)
        tablaVentas.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            private static final long serialVersionUID = 1L;
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                l.setHorizontalAlignment(SwingConstants.CENTER);
                String estado = value != null ? value.toString() : "";
                if ("Pagada".equalsIgnoreCase(estado)) {
                    l.setForeground(Ui.COLOR_EXITO);
                    l.setFont(new Font("Segoe UI", Font.BOLD, 11));
                } else if ("Cotización".equalsIgnoreCase(estado)) {
                    l.setForeground(new Color(217, 119, 6));
                    l.setFont(new Font("Segoe UI", Font.BOLD, 11));
                } else {
                    l.setForeground(Ui.COLOR_PELIGRO);
                    l.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                }
                return l;
            }
        });

        tablaVentas.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int r = tablaVentas.getSelectedRow();
                if (r >= 0 && r < listaVentasFiltrada.size()) {
                    mostrarDetalleVenta(listaVentasFiltrada.get(r));
                } else {
                    mostrarDetalleVenta(null);
                }
            }
        });

        JScrollPane scroll = new JScrollPane(tablaVentas);
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
        JLabel lblVacio = new JLabel("Seleccione una venta del registro para consultar el Live Sale Hub", SwingConstants.CENTER);
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
        panelHubDetalle.setBorder(new EmptyBorder(12, 12, 12, 12));

        // 1. Cabecera del Hub
        JPanel cabHub = new JPanel(new BorderLayout(10, 0));
        cabHub.setOpaque(false);

        lblHubAvatar = new JLabel("V", SwingConstants.CENTER) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int diametro = Math.min(getWidth(), getHeight()) - 2;
                if (diametro > 0) {
                    g2.setColor(new Color(240, 253, 250));
                    g2.fill(new Ellipse2D.Double(1, 1, diametro, diametro));
                    g2.setColor(Ui.TURQUESA);
                    g2.draw(new Ellipse2D.Double(1, 1, diametro, diametro));
                }
                super.paintComponent(g);
                g2.dispose();
            }
        };
        lblHubAvatar.setPreferredSize(new Dimension(42, 42));
        lblHubAvatar.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblHubAvatar.setForeground(Ui.TURQUESA_PROFUNDO);
        cabHub.add(lblHubAvatar, BorderLayout.WEST);

        JPanel titulosHub = new JPanel();
        titulosHub.setOpaque(false);
        titulosHub.setLayout(new BoxLayout(titulosHub, BoxLayout.Y_AXIS));

        lblHubTitulo = new JLabel("B001-000414");
        lblHubTitulo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblHubTitulo.setForeground(Ui.TEXTO_TITULO);

        lblHubSubtitulo = new JLabel("Boleta Electrónica · Carlos Mendoza");
        lblHubSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblHubSubtitulo.setForeground(Ui.TEXTO_MUTED);

        titulosHub.add(lblHubTitulo);
        titulosHub.add(vGap(2));
        titulosHub.add(lblHubSubtitulo);
        cabHub.add(titulosHub, BorderLayout.CENTER);

        lblHubChipEstado = new JLabel("  Pagada  ");
        lblHubChipEstado.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblHubChipEstado.setForeground(Ui.COLOR_EXITO);
        lblHubChipEstado.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(187, 247, 208), 1, true),
                new EmptyBorder(3, 8, 3, 8)
        ));
        cabHub.add(lblHubChipEstado, BorderLayout.EAST);
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

        lblHubTotalCobrado = new JLabel("S/. 0.00");
        lblHubSubtotalNeto = new JLabel("S/. 0.00");
        lblHubIgvFiscal = new JLabel("S/. 0.00");
        lblHubDescuento = new JLabel("0%");

        gridTelemetria.add(crearCardMiniTelemetria("TOTAL COBRADO", lblHubTotalCobrado, Ui.TURQUESA_OSCURO));
        gridTelemetria.add(crearCardMiniTelemetria("SUBTOTAL NETO", lblHubSubtotalNeto, Ui.TEXTO_TITULO));
        gridTelemetria.add(crearCardMiniTelemetria("IGV FISCAL (18%)", lblHubIgvFiscal, Ui.TEXTO_MUTED));
        gridTelemetria.add(crearCardMiniTelemetria("DESCUENTO %", lblHubDescuento, Ui.COLOR_PELIGRO));

        scrollContent.add(gridTelemetria);
        scrollContent.add(vGap(8));

        // Secciones Detalladas
        JPanel seccionItems = crearSeccionCard("DESGLOSE DE ÍTEMS ADQUIRIDOS");
        txtHubItemsDetalle = new JTextArea(4, 20);
        txtHubItemsDetalle.setFont(new Font("Consolas", Font.PLAIN, 11));
        txtHubItemsDetalle.setEditable(false);
        txtHubItemsDetalle.setLineWrap(true);
        txtHubItemsDetalle.setWrapStyleWord(true);
        txtHubItemsDetalle.setOpaque(false);
        txtHubItemsDetalle.setBorder(null);
        txtHubItemsDetalle.setAlignmentX(Component.LEFT_ALIGNMENT);
        seccionItems.add(txtHubItemsDetalle);
        scrollContent.add(seccionItems);
        scrollContent.add(vGap(6));

        JPanel seccionFiscal = crearSeccionCard("DATOS FISCALES & PACIENTE");
        lblHubClienteDni = new JTextArea("-");
        configurarTextAreaItem(lblHubClienteDni);

        lblHubMascota = new JTextArea("-");
        configurarTextAreaItem(lblHubMascota);

        lblHubMetodoPago = new JTextArea("-");
        configurarTextAreaItem(lblHubMetodoPago);

        lblHubCajeroFecha = new JTextArea("-");
        configurarTextAreaItem(lblHubCajeroFecha);

        seccionFiscal.add(crearItemLinea("Cliente / Doc:", lblHubClienteDni));
        seccionFiscal.add(vGap(3));
        seccionFiscal.add(crearItemLinea("Paciente:", lblHubMascota));
        seccionFiscal.add(vGap(3));
        seccionFiscal.add(crearItemLinea("Medio de Pago:", lblHubMetodoPago));
        seccionFiscal.add(vGap(3));
        seccionFiscal.add(crearItemLinea("Cajero / Emisión:", lblHubCajeroFecha));
        scrollContent.add(seccionFiscal);

        JScrollPane spHub = new JScrollPane(scrollContent);
        spHub.setBorder(null);
        spHub.setOpaque(false);
        spHub.getViewport().setOpaque(false);
        spHub.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        spHub.getVerticalScrollBar().setUnitIncrement(16);
        panelHubDetalle.add(spHub, BorderLayout.CENTER);

        // 3. Footer con 4 Botones de Acción 1-click
        JPanel gridAcciones = new JPanel(new GridLayout(2, 2, 6, 6));
        gridAcciones.setOpaque(false);
        gridAcciones.setPreferredSize(new Dimension(0, 62));
        gridAcciones.setMaximumSize(new Dimension(Integer.MAX_VALUE, 62));

        JButton btnImprimirTicket = crearBotonAccion("Ticket (80mm)", Iconos.crearIconoImprimir(14, Color.WHITE), true);
        btnImprimirTicket.addActionListener(e -> imprimirTicketTermico());

        JButton btnReporteOficial = crearBotonAccion("Comprobante PDF", Iconos.crearIconoFactura(14, Ui.TEXTO_TITULO), false);
        btnReporteOficial.addActionListener(e -> emitirReporteOficialPDF());

        JButton btnEnviarWhatsApp = crearBotonAccion("Enviar WhatsApp", Iconos.crearIconoMensaje(14, Ui.COLOR_EXITO), false);
        btnEnviarWhatsApp.addActionListener(e -> enviarWhatsAppCliente());

        JButton btnAnularVenta = crearBotonAccion("Anular Venta", Iconos.crearIconoAlertaMerma(14, Ui.COLOR_PELIGRO), false);
        btnAnularVenta.addActionListener(e -> anularVentaSeleccionada());

        gridAcciones.add(btnImprimirTicket);
        gridAcciones.add(btnReporteOficial);
        gridAcciones.add(btnEnviarWhatsApp);
        gridAcciones.add(btnAnularVenta);

        panelHubDetalle.add(gridAcciones, BorderLayout.SOUTH);

        panelHubContenido.add(panelHubVacio, BorderLayout.CENTER);
        return panelHubContenido;
    }

    private JPanel crearCardMiniTelemetria(String titulo, JLabel lblValor, Color colorValor) {
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
        lblValor.setForeground(colorValor);

        c.add(t);
        c.add(vGap(2));
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

    private static void configurarTextAreaItem(JTextArea ta) {
        ta.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        ta.setForeground(Ui.TEXTO_TITULO);
        ta.setLineWrap(true);
        ta.setWrapStyleWord(true);
        ta.setEditable(false);
        ta.setOpaque(false);
        ta.setBorder(null);
        ta.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    private JButton crearBotonAccion(String texto, javax.swing.Icon icono, boolean primario) {
        JButton btn = new JButton(texto, icono) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (primario) {
                    g2.setColor(getModel().isPressed() ? Ui.TURQUESA_OSCURO : Ui.TURQUESA);
                } else {
                    g2.setColor(getModel().isRollover() ? new Color(241, 245, 249) : Color.WHITE);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                if (!primario) {
                    g2.setColor(Ui.BORDE_SUAVE);
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                }
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 10));
        btn.setForeground(primario ? Color.WHITE : new Color(51, 65, 85));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(4, 6, 4, 6));
        return btn;
    }

    public void recargarDatos() {
        actualizarKpis();
        filtrarVentas();
    }

    private void actualizarKpis() {
        List<VentaPOS> ventas = repo.getVentasPOS();
        double totalRecaudado = 0.0;
        int cantidadValidas = 0;
        int ventasDigitales = 0;

        for (VentaPOS v : ventas) {
            if (!"Anulada".equalsIgnoreCase(v.getEstado())) {
                totalRecaudado += v.getTotal();
                cantidadValidas++;
                if (!"Efectivo".equalsIgnoreCase(v.getMetodoPago())) {
                    ventasDigitales++;
                }
            }
        }

        lblKpiRecaudadoHoy.setText(String.format("S/. %.2f", totalRecaudado));
        lblKpiComprobantes.setText(String.valueOf(ventas.size()));

        double promedio = cantidadValidas > 0 ? (totalRecaudado / cantidadValidas) : 0.0;
        lblKpiTicketPromedio.setText(String.format("S/. %.2f", promedio));

        double pctDigital = cantidadValidas > 0 ? ((ventasDigitales * 100.0) / cantidadValidas) : 0.0;
        lblKpiDigitales.setText(String.format("%.0f%%", pctDigital));
    }

    private void filtrarVentas() {
        String query = txtBuscar != null ? txtBuscar.getText().trim().toLowerCase() : "";
        List<VentaPOS> todas = repo.getVentasPOS();
        listaVentasFiltrada.clear();

        for (VentaPOS v : todas) {
            boolean coincideTipo = true;
            if (!"TODAS".equals(filtroTipoSeleccionado)) {
                if ("Boleta".equals(filtroTipoSeleccionado)) {
                    coincideTipo = v.getTipoComprobante().toLowerCase().contains("boleta");
                } else if ("Factura".equals(filtroTipoSeleccionado)) {
                    coincideTipo = v.getTipoComprobante().toLowerCase().contains("factura");
                } else if ("Ticket".equals(filtroTipoSeleccionado)) {
                    coincideTipo = v.getTipoComprobante().toLowerCase().contains("ticket");
                } else if ("Cotización".equals(filtroTipoSeleccionado)) {
                    coincideTipo = "Cotización".equalsIgnoreCase(v.getEstado()) || v.getTipoComprobante().toLowerCase().contains("cotiz");
                } else if ("Pagada".equals(filtroTipoSeleccionado)) {
                    coincideTipo = "Pagada".equalsIgnoreCase(v.getEstado());
                } else if ("Anulada".equals(filtroTipoSeleccionado)) {
                    coincideTipo = "Anulada".equalsIgnoreCase(v.getEstado());
                }
            }
            if (!coincideTipo) continue;

            if (!query.isEmpty()) {
                boolean match = v.getNumeroComprobante().toLowerCase().contains(query)
                        || v.getClienteNombre().toLowerCase().contains(query)
                        || v.getMascotaNombre().toLowerCase().contains(query)
                        || v.getMetodoPago().toLowerCase().contains(query);
                if (!match) continue;
            }

            listaVentasFiltrada.add(v);
        }

        modeloVentas.setRowCount(0);
        for (VentaPOS v : listaVentasFiltrada) {
            modeloVentas.addRow(new Object[]{
                    v.getNumeroComprobante(),
                    v.getTipoComprobante(),
                    v.getClienteNombre() + " (" + v.getMascotaNombre() + ")",
                    v.getMetodoPago(),
                    String.format("%.2f", v.getTotal()),
                    v.getEstado()
            });
        }

        if (!listaVentasFiltrada.isEmpty()) {
            tablaVentas.setRowSelectionInterval(0, 0);
            mostrarDetalleVenta(listaVentasFiltrada.get(0));
        } else {
            mostrarDetalleVenta(null);
        }
    }

    private void mostrarDetalleVenta(VentaPOS v) {
        this.ventaSeleccionada = v;
        panelHubContenido.removeAll();

        if (v == null) {
            panelHubContenido.add(panelHubVacio, BorderLayout.CENTER);
            panelHubContenido.revalidate();
            panelHubContenido.repaint();
            return;
        }

        // Configurar datos en Hub
        String num = v.getNumeroComprobante();
        lblHubAvatar.setText(num.startsWith("B") ? "B" : num.startsWith("F") ? "F" : "T");
        lblHubTitulo.setText(num);
        lblHubSubtitulo.setText(v.getTipoComprobante() + " · " + v.getClienteNombre());
        lblHubChipEstado.setText("  " + v.getEstado() + "  ");

        if ("Pagada".equalsIgnoreCase(v.getEstado())) {
            lblHubChipEstado.setForeground(Ui.COLOR_EXITO);
            lblHubChipEstado.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(187, 247, 208), 1),
                    new EmptyBorder(4, 8, 4, 8)
            ));
        } else if ("Cotización".equalsIgnoreCase(v.getEstado())) {
            lblHubChipEstado.setForeground(new Color(217, 119, 6));
            lblHubChipEstado.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(254, 215, 170), 1),
                    new EmptyBorder(4, 8, 4, 8)
            ));
        } else {
            lblHubChipEstado.setForeground(Ui.COLOR_PELIGRO);
            lblHubChipEstado.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(254, 202, 202), 1),
                    new EmptyBorder(4, 8, 4, 8)
            ));
        }

        lblHubTotalCobrado.setText(String.format("S/. %.2f", v.getTotal()));
        lblHubSubtotalNeto.setText(String.format("S/. %.2f", v.getSubtotal()));
        lblHubIgvFiscal.setText(String.format("S/. %.2f", v.getIgv()));
        lblHubDescuento.setText(String.format("%.0f%%", v.getPorcentajeDescuento()));

        StringBuilder sbItems = new StringBuilder();
        for (ItemVentaPOS it : v.getItems()) {
            sbItems.append(String.format("%-2d x %-26s S/ %6.2f\n", it.getCantidad(), it.getDescripcion(), it.getSubtotal()));
        }
        txtHubItemsDetalle.setText(sbItems.toString());

        lblHubClienteDni.setText(v.getClienteNombre() + " (Doc: " + v.getClienteDocumento() + ")");
        lblHubMascota.setText(v.getMascotaNombre());
        lblHubMetodoPago.setText(v.getMetodoPago() + (v.getVuelto() > 0 ? " (Vuelto: S/ " + String.format("%.2f", v.getVuelto()) + ")" : ""));
        lblHubCajeroFecha.setText(v.getCajero() + " | Fecha: " + v.getFechaHoraTexto());

        panelHubContenido.add(panelHubDetalle, BorderLayout.CENTER);
        panelHubContenido.revalidate();
        panelHubContenido.repaint();
    }

    private void abrirDialogoNuevaVenta() {
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        FormularioVentaPOSDialog dlg = new FormularioVentaPOSDialog(parent);
        dlg.setVisible(true);
        if (dlg.isConfirmado()) {
            recargarDatos();
        }
    }

    private void imprimirTicketTermico() {
        if (ventaSeleccionada == null) return;
        JOptionPane.showMessageDialog(this,
                "Enviando documento térmico a impresora POS de 80mm...\n" +
                        "Comprobante: " + ventaSeleccionada.getNumeroComprobante() + "\n" +
                        "Total: S/. " + String.format("%.2f", ventaSeleccionada.getTotal()) + "\n" +
                        "Corte automático activado y ticket emitido correctamente.",
                "Impresión Exitosa", JOptionPane.INFORMATION_MESSAGE);
    }

    private void emitirReporteOficialPDF() {
        if (ventaSeleccionada == null) return;

        List<Object[]> filas = new ArrayList<>();
        for (ItemVentaPOS it : ventaSeleccionada.getItems()) {
            filas.add(new Object[]{
                    it.getCodigo(),
                    it.getDescripcion(),
                    it.getCategoria(),
                    it.getCantidad(),
                    String.format("S/ %.2f", it.getPrecioUnitario()),
                    String.format("S/ %.2f", it.getSubtotal())
            });
        }

        Ui.mostrarVisorReporte(
                (Frame) SwingUtilities.getWindowAncestor(this),
                ventaSeleccionada.getTipoComprobante() + " " + ventaSeleccionada.getNumeroComprobante(),
                "Comprobante Electrónico Oficial Happy Pets ERP",
                new String[][]{
                        {"Comprobante", ventaSeleccionada.getNumeroComprobante()},
                        {"Tipo", ventaSeleccionada.getTipoComprobante()},
                        {"Cliente", ventaSeleccionada.getClienteNombre()},
                        {"DNI / RUC", ventaSeleccionada.getClienteDocumento()},
                        {"Paciente", ventaSeleccionada.getMascotaNombre()},
                        {"Método de Pago", ventaSeleccionada.getMetodoPago()},
                        {"Fecha de Emisión", ventaSeleccionada.getFechaHoraTexto()},
                        {"Cajero / Turno", ventaSeleccionada.getCajero()}
                },
                new String[]{"Código", "Descripción del Ítem", "Categoría", "Cant.", "P. Unit.", "Subtotal"},
                filas,
                "Subtotal: S/ " + String.format("%.2f", ventaSeleccionada.getSubtotal())
                        + " | Descuento: S/ " + String.format("%.2f", ventaSeleccionada.getMontoDescuento())
                        + " | IGV (18%): S/ " + String.format("%.2f", ventaSeleccionada.getIgv())
                        + " | TOTAL: S/ " + String.format("%.2f", ventaSeleccionada.getTotal()),
                "comprobante_" + ventaSeleccionada.getNumeroComprobante()
        );
    }

    private void enviarWhatsAppCliente() {
        if (ventaSeleccionada == null) return;
        JOptionPane.showMessageDialog(this,
                "Enviando comprobante electrónico por WhatsApp API:\n" +
                        "Destinatario: " + ventaSeleccionada.getClienteNombre() + "\n" +
                        "Mensaje: ¡Hola! Su comprobante " + ventaSeleccionada.getNumeroComprobante() +
                        " por S/. " + String.format("%.2f", ventaSeleccionada.getTotal()) +
                        " está listo para descarga digital.",
                "WhatsApp API - Notificación Enviada", JOptionPane.INFORMATION_MESSAGE);
    }

    private void anularVentaSeleccionada() {
        if (ventaSeleccionada == null) return;
        if ("Anulada".equalsIgnoreCase(ventaSeleccionada.getEstado())) {
            JOptionPane.showMessageDialog(this, "Esta venta ya se encuentra anulada.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int opt = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de anular el comprobante " + ventaSeleccionada.getNumeroComprobante() + "?\n" +
                        "Esta acción restituirá las unidades al stock en Kardex y registrará el evento de auditoría.",
                "Confirmar Anulación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (opt == JOptionPane.YES_OPTION) {
            repo.anularVentaPOS(ventaSeleccionada.getIdVenta());
            recargarDatos();
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
