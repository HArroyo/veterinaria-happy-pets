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
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
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
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private JPanel panelHubContenido;
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private JPanel panelHubVacio;
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
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
    private JTextArea lblHubProveedor;
    private JTextArea lblHubComprobante;
    private JTextArea lblHubCentroCosto;
    private JTextArea lblHubDescripcion;

    private EgresoOperativo egresoSeleccionado;

    /**
     * Constructor de VistaEgresosOperativosPanel.
     * Inicializa los componentes gráficos, configura layouts, asigna estilos
     * institucionales y carga los datos desde el repositorio central.
     */
    public VistaEgresosOperativosPanel() {
        setLayout(new BorderLayout());
        setOpaque(false);

        // Panel Superior Fijo (Cabecera + KPIs)
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
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
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel panelCentro = new JPanel(new BorderLayout());
        panelCentro.setOpaque(false);
        panelCentro.setBorder(new EmptyBorder(0, 18, 14, 18));
        panelCentro.add(crearSplitMasterDetail(), BorderLayout.CENTER);

        add(panelCentro, BorderLayout.CENTER);

        recargarDatos();
    }

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private JPanel crearCabeceraVista() {
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel cab = new JPanel(new BorderLayout());
        cab.setOpaque(false);

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
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

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
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
        cab.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        return cab;
    }

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private JPanel crearFilaKpis() {
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
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

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private JPanel crearCardKpi(String titulo, JLabel lblValor, String subtitulo, javax.swing.Icon icono) {
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel card = new JPanel(new BorderLayout(8, 0)) {
            private static final long serialVersionUID = 1L;
            @Override
            /**
             * Renderizado gráfico personalizado en Java2D.
             * Activa Antialiasing para bordes suaves y realiza el cálculo geométrico preciso.
             */
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

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
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

    private JSplitPane crearSplitMasterDetail() {
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel master = crearPanelMasterTable();
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
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

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private JPanel crearPanelMasterTable() {
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel master = new JPanel(new BorderLayout(0, 10)) {
            private static final long serialVersionUID = 1L;
            @Override
            /**
             * Renderizado gráfico personalizado en Java2D.
             * Activa Antialiasing para bordes suaves y realiza el cálculo geométrico preciso.
             */
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
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel cabFiltros = new JPanel();
        cabFiltros.setOpaque(false);
        cabFiltros.setLayout(new BoxLayout(cabFiltros, BoxLayout.Y_AXIS));

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
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
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
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
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
                JPanel cell = new JPanel(new BorderLayout(8, 0));
                cell.setOpaque(true);
                cell.setBackground(isSelected ? table.getSelectionBackground() : (row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252)));
                cell.setBorder(new EmptyBorder(0, 8, 0, 8));

                String desc = value != null ? value.toString() : "";
                String letra = !desc.isEmpty() ? desc.substring(0, 1).toUpperCase() : "E";

                JLabel avatar = new JLabel(letra, SwingConstants.CENTER) {
                    private static final long serialVersionUID = 1L;
                    @Override
            /**
             * Renderizado gráfico personalizado en Java2D.
             * Activa Antialiasing para bordes suaves y realiza el cálculo geométrico preciso.
             */
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        int s = Math.min(getWidth(), getHeight()) - 2;
                        int x = (getWidth() - s) / 2;
                        int y = (getHeight() - s) / 2;
                        g2.setColor(new Color(254, 242, 242));
                        g2.fillOval(x, y, s, s);
                        g2.setColor(new Color(254, 202, 202));
                        g2.drawOval(x, y, s, s);
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

                cell.add(avatar, BorderLayout.WEST);
                cell.add(lblTexto, BorderLayout.CENTER);
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

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private JPanel crearPanelLiveHub() {
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        panelHubContenido = new JPanel(new BorderLayout());
        panelHubContenido.setOpaque(false);

        // Panel Hub Vacío
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        panelHubVacio = new JPanel(new BorderLayout());
        panelHubVacio.setOpaque(false);
        JLabel lblVacio = new JLabel("Seleccione un egreso para consultar el Live Expense Hub", SwingConstants.CENTER);
        lblVacio.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblVacio.setForeground(Ui.TEXTO_MUTED);
        panelHubVacio.add(lblVacio, BorderLayout.CENTER);

        // Panel Detalle Activo
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        panelHubDetalle = new JPanel(new BorderLayout(0, 8)) {
            private static final long serialVersionUID = 1L;
            @Override
            /**
             * Renderizado gráfico personalizado en Java2D.
             * Activa Antialiasing para bordes suaves y realiza el cálculo geométrico preciso.
             */
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
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel cabHub = new JPanel(new BorderLayout(10, 0));
        cabHub.setOpaque(false);

        lblHubAvatar = new JLabel("E", SwingConstants.CENTER) {
            private static final long serialVersionUID = 1L;
            @Override
            /**
             * Renderizado gráfico personalizado en Java2D.
             * Activa Antialiasing para bordes suaves y realiza el cálculo geométrico preciso.
             */
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
        lblHubAvatar.setPreferredSize(new Dimension(42, 42));
        lblHubAvatar.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblHubAvatar.setForeground(Ui.COLOR_PELIGRO);
        cabHub.add(lblHubAvatar, BorderLayout.WEST);

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel titulosHub = new JPanel();
        titulosHub.setOpaque(false);
        titulosHub.setLayout(new BoxLayout(titulosHub, BoxLayout.Y_AXIS));

        lblHubTitulo = new JLabel("Descripción del Egreso");
        lblHubTitulo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblHubTitulo.setForeground(Ui.TEXTO_TITULO);

        lblHubSubtitulo = new JLabel("EGR-001 · Servicios básicos");
        lblHubSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblHubSubtitulo.setForeground(Ui.TEXTO_MUTED);

        titulosHub.add(lblHubTitulo);
        titulosHub.add(vGap(2));
        titulosHub.add(lblHubSubtitulo);
        cabHub.add(titulosHub, BorderLayout.CENTER);

        lblHubChipEstado = new JLabel("  Pagado  ");
        lblHubChipEstado.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblHubChipEstado.setForeground(Ui.COLOR_EXITO);
        lblHubChipEstado.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(187, 247, 208), 1, true),
                new EmptyBorder(3, 8, 3, 8)
        ));
        cabHub.add(lblHubChipEstado, BorderLayout.EAST);
        panelHubDetalle.add(cabHub, BorderLayout.NORTH);

        // 2. Contenido Scrollable
        ScrollablePanel centroHub = new ScrollablePanel() {
            private static final long serialVersionUID = 1L;
            @Override
            protected void addImpl(Component comp, Object constraints, int index) {
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
                if (comp instanceof javax.swing.JComponent) {
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
                    ((javax.swing.JComponent) comp).setAlignmentX(Component.LEFT_ALIGNMENT);
                }
                super.addImpl(comp, constraints, index);
            }
        };
        centroHub.setOpaque(false);
        centroHub.setLayout(new BoxLayout(centroHub, BoxLayout.Y_AXIS));

        // Telemetría 2x2
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel gridTelemetria = new JPanel(new GridLayout(2, 2, 6, 6));
        gridTelemetria.setOpaque(false);
        gridTelemetria.setMaximumSize(new Dimension(Integer.MAX_VALUE, 92));
        gridTelemetria.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblHubMontoGasto = new JLabel("S/. 0.00");
        lblHubFechaLiquidacion = new JLabel("00/00/0000");
        lblHubMetodoPago = new JLabel("Transferencia");
        lblHubEstadoGasto = new JLabel("Pagado");

        gridTelemetria.add(crearCardMiniTelemetria("MONTO DEL GASTO", lblHubMontoGasto, Ui.COLOR_PELIGRO));
        gridTelemetria.add(crearCardMiniTelemetria("FECHA LIQUIDACIÓN", lblHubFechaLiquidacion, Ui.TEXTO_TITULO));
        gridTelemetria.add(crearCardMiniTelemetria("MÉTODO DE PAGO", lblHubMetodoPago, Ui.TURQUESA_OSCURO));
        gridTelemetria.add(crearCardMiniTelemetria("ESTADO CONTABLE", lblHubEstadoGasto, Ui.COLOR_EXITO));

        centroHub.add(gridTelemetria);
        centroHub.add(vGap(8));

        // Especificaciones
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel seccionProveedor = crearSeccionCard("PROVEEDOR & DOCUMENTO FISCAL");
        lblHubProveedor = new JTextArea("-");
        configurarTextAreaItem(lblHubProveedor);
        lblHubComprobante = new JTextArea("-");
        configurarTextAreaItem(lblHubComprobante);
        seccionProveedor.add(crearItemLinea("Beneficiario:", lblHubProveedor));
        seccionProveedor.add(vGap(3));
        seccionProveedor.add(crearItemLinea("N° Operación:", lblHubComprobante));
        centroHub.add(seccionProveedor);
        centroHub.add(vGap(6));

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel seccionImputacion = crearSeccionCard("IMPUTACIÓN PRESUPUESTAL & CENTRO DE COSTO");
        lblHubCentroCosto = new JTextArea("-");
        configurarTextAreaItem(lblHubCentroCosto);
        lblHubDescripcion = new JTextArea("-");
        configurarTextAreaItem(lblHubDescripcion);
        seccionImputacion.add(crearItemLinea("Centro de Costo:", lblHubCentroCosto));
        seccionImputacion.add(vGap(3));
        seccionImputacion.add(crearItemLinea("Detalle:", lblHubDescripcion));
        centroHub.add(seccionImputacion);

        JScrollPane spHub = new JScrollPane(centroHub);
        spHub.setBorder(null);
        spHub.setOpaque(false);
        spHub.getViewport().setOpaque(false);
        spHub.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        spHub.getVerticalScrollBar().setUnitIncrement(16);
        panelHubDetalle.add(spHub, BorderLayout.CENTER);

        // 3. Footer con 4 Botones de Acción 1-click
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel gridAcciones = new JPanel(new GridLayout(2, 2, 6, 6));
        gridAcciones.setOpaque(false);
        gridAcciones.setPreferredSize(new Dimension(0, 62));
        gridAcciones.setMaximumSize(new Dimension(Integer.MAX_VALUE, 62));

        JButton btnComprobantePDF = crearBotonAccion("Comprobante PDF", Iconos.crearIconoFactura(14, Color.WHITE), true);
        btnComprobantePDF.addActionListener(e -> emitirComprobanteEgresoPDF());

        JButton btnNotificarAdmin = crearBotonAccion("Notificar Auditoría", Iconos.crearIconoMensaje(14, Ui.TURQUESA_OSCURO), false);
        btnNotificarAdmin.addActionListener(e -> notificarAuditoriaAdmin());

        JButton btnCertPresupuesto = crearBotonAccion("Certificado Gasto", Iconos.crearIconoExportar(14, Ui.TEXTO_TITULO), false);
        btnCertPresupuesto.addActionListener(e -> emitirCertificadoPresupuestalPDF());

        JButton btnEditarAnular = crearBotonAccion("Editar / Anular", Iconos.crearIconoEditar(14, Ui.COLOR_PELIGRO), false);
        btnEditarAnular.addActionListener(e -> editarOAnularEgreso());

        gridAcciones.add(btnComprobantePDF);
        gridAcciones.add(btnNotificarAdmin);
        gridAcciones.add(btnCertPresupuesto);
        gridAcciones.add(btnEditarAnular);

        panelHubDetalle.add(gridAcciones, BorderLayout.SOUTH);

        panelHubContenido.add(panelHubVacio, BorderLayout.CENTER);
        return panelHubContenido;
    }

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private JPanel crearCardMiniTelemetria(String titulo, JLabel lblValor, Color colorValor) {
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
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

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private JPanel crearSeccionCard(String titulo) {
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel sec = new JPanel() {
            private static final long serialVersionUID = 1L;
            @Override
            public Dimension getMaximumSize() {
                return new Dimension(Integer.MAX_VALUE, getPreferredSize().height);
            }
            @Override
            protected void addImpl(Component comp, Object constraints, int index) {
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
                if (comp instanceof javax.swing.JComponent) {
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
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

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
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

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private JPanel crearItemLinea(String label, Component valor) {
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
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
        l.setVerticalAlignment(SwingConstants.TOP);
        p.add(l, BorderLayout.WEST);
        p.add(valor, BorderLayout.CENTER);
        return p;
    }

    private static Component vGap(int h) {
        Component c = Box.createVerticalStrut(h);
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        if (c instanceof javax.swing.JComponent) {
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
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
            /**
             * Renderizado gráfico personalizado en Java2D.
             * Activa Antialiasing para bordes suaves y realiza el cálculo geométrico preciso.
             */
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
        btn.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(4, 6, 4, 6));
        return btn;
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

    /**
     * Aplica criterios de filtrado dinámico en memoria sobre los registros.
     * Evalúa texto de búsqueda (búsqueda predictiva) y filtros por categoría o estado.
     */
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

    /**
     * Actualiza el panel lateral derecho (Live Detail Hub) con la información del registro seleccionado.
     * Si el argumento es nulo, restablece el panel mostrando el estado de selección vacía.
     */
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

        lblHubProveedor.setText(e.getProveedor());
        lblHubComprobante.setText(e.getComprobante());
        lblHubCentroCosto.setText(e.getCategoria());
        lblHubDescripcion.setText(e.getDescripcion());

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

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private static class ScrollablePanel extends JPanel implements javax.swing.Scrollable {
        private static final long serialVersionUID = 1L;
        @Override public Dimension getPreferredScrollableViewportSize() { return getPreferredSize(); }
        @Override public int getScrollableUnitIncrement(java.awt.Rectangle visibleRect, int orientation, int direction) { return 16; }
        @Override public int getScrollableBlockIncrement(java.awt.Rectangle visibleRect, int orientation, int direction) { return 32; }
        @Override public boolean getScrollableTracksViewportWidth() { return true; }
        @Override public boolean getScrollableTracksViewportHeight() { return false; }
    }
}
