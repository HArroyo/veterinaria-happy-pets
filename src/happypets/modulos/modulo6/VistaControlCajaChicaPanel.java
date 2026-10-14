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
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import happypets.data.RepositorioVeterinaria;
import happypets.model.MovimientoCajaChica;
import happypets.ui.Iconos;
import happypets.ui.Ui;

/**
 * Submódulo 6.3: Control de Caja Chica y Arqueos Diarios.
 * Arquitectura Master-Detail (58% / 42%) - Opción A Recomendada:
 * - Master Table: Registro cronológico de ingresos y egresos menores con saldos dinámicos.
 * - Live Cash Hub: Telemetría 2x2, conciliación de fondos, arqueos físicos y acciones 1-click.
 */
public class VistaControlCajaChicaPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final RepositorioVeterinaria repo = RepositorioVeterinaria.getInstancia();

    // KPIs
    private JLabel lblKpiSaldoActual;
    private JLabel lblKpiTotalIngresos;
    private JLabel lblKpiTotalEgresos;
    private JLabel lblKpiOperaciones;

    // Tabla Maestra (Izquierda - 58%)
    private JTable tablaCaja;
    private DefaultTableModel modeloCaja;
    private JTextField txtBuscar;
    private String filtroTipoSeleccionado = "TODOS";
    private final List<MovimientoCajaChica> listaCajaFiltrada = new ArrayList<>();

    // Live Cash Hub (Derecha - 42%)
    private JPanel panelHubContenido;
    private JPanel panelHubVacio;
    private JPanel panelHubDetalle;

    private JLabel lblHubAvatar;
    private JLabel lblHubTitulo;
    private JLabel lblHubSubtitulo;
    private JLabel lblHubChipTipo;

    // Telemetría 2x2
    private JLabel lblHubMontoOperado;
    private JLabel lblHubSaldoResultante;
    private JLabel lblHubFechaOperacion;
    private JLabel lblHubTipoTransaccion;

    // Secciones de Detalle
    private JTextArea lblHubResponsable;
    private JTextArea lblHubComprobante;
    private JTextArea lblHubConcepto;
    private JTextArea lblHubSaldoActualCaja;

    private MovimientoCajaChica movimientoSeleccionado;

    public VistaControlCajaChicaPanel() {
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

        JLabel titulo = new JLabel("Control de Caja Chica & Arqueos Diarios");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 17));
        titulo.setForeground(Ui.TEXTO_TITULO);

        JLabel subtitulo = new JLabel("Módulo 6.3 · Custodia de fondos en efectivo, vales de caja y arqueo por denominación");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitulo.setForeground(Ui.TEXTO_MUTED);

        izq.add(titulo);
        izq.add(Box.createVerticalStrut(2));
        izq.add(subtitulo);
        cab.add(izq, BorderLayout.WEST);

        JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        der.setOpaque(false);

        JButton btnNuevoMov = Ui.boton("+ Nuevo Movimiento", true);
        btnNuevoMov.setIcon(Iconos.crearIconoMonedas(14, Color.WHITE));
        btnNuevoMov.addActionListener(e -> abrirDialogoNuevoMovimiento());
        der.add(btnNuevoMov);

        JButton btnArqueo = Ui.boton("Arqueo Físico de Caja", false);
        btnArqueo.setIcon(Iconos.crearIconoCajaChica(14, Ui.TEXTO_TITULO));
        btnArqueo.addActionListener(e -> realizarArqueoFisico());
        der.add(btnArqueo);

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

        lblKpiSaldoActual = new JLabel("S/. 0.00");
        lblKpiTotalIngresos = new JLabel("S/. 0.00");
        lblKpiTotalEgresos = new JLabel("S/. 0.00");
        lblKpiOperaciones = new JLabel("0");

        fila.add(crearCardKpi("Saldo Actual en Custodia", lblKpiSaldoActual, "Efectivo disponible en caja", Iconos.crearIconoMonedas(22, Ui.COLOR_EXITO)));
        fila.add(crearCardKpi("Ingresos del Turno", lblKpiTotalIngresos, "Reposición y ventas efectivo", Iconos.crearIconoPOS(22, Ui.TURQUESA)));
        fila.add(crearCardKpi("Egresos del Turno", lblKpiTotalEgresos, "Vales y gastos menores", Iconos.crearIconoAlertaMerma(22, Ui.COLOR_PELIGRO)));
        fila.add(crearCardKpi("Operaciones Registradas", lblKpiOperaciones, "Movimientos auditados", Iconos.crearIconoCajaChica(22, Ui.TURQUESA_OSCURO)));

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
        txtBuscar.putClientProperty("JTextField.placeholderText", "Buscar por concepto, responsable o comprobante...");
        txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filtrarCaja(); }
            public void removeUpdate(DocumentEvent e) { filtrarCaja(); }
            public void changedUpdate(DocumentEvent e) { filtrarCaja(); }
        });
        barraBuscar.add(txtBuscar, BorderLayout.CENTER);
        cabFiltros.add(barraBuscar);
        cabFiltros.add(Box.createVerticalStrut(8));

        // Pills de Filtro
        JPanel pills = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        pills.setOpaque(false);

        String[] tipos = {"TODOS", "Ingresos", "Egresos", "Con Comprobante", "Hoy"};
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
                filtrarCaja();
            });
            pills.add(btnPill);
        }
        cabFiltros.add(pills);
        master.add(cabFiltros, BorderLayout.NORTH);

        // Tabla
        String[] cols = {"ID Mov.", "Tipo", "Concepto de la Operación", "Monto S/.", "Saldo Res. S/.", "Responsable", "Fecha"};
        modeloCaja = new DefaultTableModel(cols, 0) {
            private static final long serialVersionUID = 1L;
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tablaCaja = new JTable(modeloCaja);
        tablaCaja.setRowHeight(48);
        tablaCaja.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaCaja.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaCaja.setShowVerticalLines(false);
        tablaCaja.setGridColor(new Color(241, 245, 249));
        tablaCaja.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        tablaCaja.getTableHeader().setBackground(new Color(248, 250, 252));
        tablaCaja.getTableHeader().setForeground(Ui.TEXTO_MUTED);

        tablaCaja.getColumnModel().getColumn(0).setPreferredWidth(75);
        tablaCaja.getColumnModel().getColumn(1).setPreferredWidth(80);
        tablaCaja.getColumnModel().getColumn(2).setPreferredWidth(210);
        tablaCaja.getColumnModel().getColumn(3).setPreferredWidth(80);
        tablaCaja.getColumnModel().getColumn(4).setPreferredWidth(85);
        tablaCaja.getColumnModel().getColumn(5).setPreferredWidth(110);
        tablaCaja.getColumnModel().getColumn(6).setPreferredWidth(80);

        // Monograma en columna 2 (Concepto)
        tablaCaja.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            private static final long serialVersionUID = 1L;
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                JPanel cell = new JPanel(new BorderLayout(8, 0));
                cell.setOpaque(true);
                cell.setBackground(isSelected ? table.getSelectionBackground() : (row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252)));
                cell.setBorder(new EmptyBorder(0, 8, 0, 8));

                String concepto = value != null ? value.toString() : "";
                String letra = !concepto.isEmpty() ? concepto.substring(0, 1).toUpperCase() : "M";

                JLabel avatar = new JLabel(letra, SwingConstants.CENTER) {
                    private static final long serialVersionUID = 1L;
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        int s = Math.min(getWidth(), getHeight()) - 2;
                        int x = (getWidth() - s) / 2;
                        int y = (getHeight() - s) / 2;
                        g2.setColor(new Color(240, 253, 250));
                        g2.fillOval(x, y, s, s);
                        g2.setColor(new Color(153, 246, 228));
                        g2.drawOval(x, y, s, s);
                        super.paintComponent(g);
                        g2.dispose();
                    }
                };
                avatar.setPreferredSize(new Dimension(30, 30));
                avatar.setFont(new Font("Segoe UI", Font.BOLD, 11));
                avatar.setForeground(Ui.TURQUESA_PROFUNDO);

                JLabel lblTexto = new JLabel(concepto);
                lblTexto.setFont(new Font("Segoe UI", Font.BOLD, 11));
                lblTexto.setForeground(isSelected ? table.getSelectionForeground() : Ui.TEXTO_TITULO);

                cell.add(avatar, BorderLayout.WEST);
                cell.add(lblTexto, BorderLayout.CENTER);
                return cell;
            }
        });

        // Render de Tipo (Columna 1)
        tablaCaja.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
            private static final long serialVersionUID = 1L;
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                l.setHorizontalAlignment(SwingConstants.CENTER);
                String tipo = value != null ? value.toString() : "";
                if ("Ingreso".equalsIgnoreCase(tipo)) {
                    l.setForeground(Ui.COLOR_EXITO);
                    l.setFont(new Font("Segoe UI", Font.BOLD, 11));
                } else {
                    l.setForeground(Ui.COLOR_PELIGRO);
                    l.setFont(new Font("Segoe UI", Font.BOLD, 11));
                }
                return l;
            }
        });

        tablaCaja.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int r = tablaCaja.getSelectedRow();
                if (r >= 0 && r < listaCajaFiltrada.size()) {
                    mostrarDetalleMovimiento(listaCajaFiltrada.get(r));
                } else {
                    mostrarDetalleMovimiento(null);
                }
            }
        });

        JScrollPane scroll = new JScrollPane(tablaCaja);
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
        JLabel lblVacio = new JLabel("Seleccione un movimiento para consultar el Live Cash Hub", SwingConstants.CENTER);
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

        lblHubAvatar = new JLabel("$", SwingConstants.CENTER) {
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

        lblHubTitulo = new JLabel("Concepto de la Operación");
        lblHubTitulo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblHubTitulo.setForeground(Ui.TEXTO_TITULO);

        lblHubSubtitulo = new JLabel("CCH-001 · Responsable: Juan Pérez");
        lblHubSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblHubSubtitulo.setForeground(Ui.TEXTO_MUTED);

        titulosHub.add(lblHubTitulo);
        titulosHub.add(vGap(2));
        titulosHub.add(lblHubSubtitulo);
        cabHub.add(titulosHub, BorderLayout.CENTER);

        lblHubChipTipo = new JLabel("  Ingreso  ");
        lblHubChipTipo.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblHubChipTipo.setForeground(Ui.COLOR_EXITO);
        lblHubChipTipo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(187, 247, 208), 1, true),
                new EmptyBorder(3, 8, 3, 8)
        ));
        cabHub.add(lblHubChipTipo, BorderLayout.EAST);
        panelHubDetalle.add(cabHub, BorderLayout.NORTH);

        // 2. Contenido Scrollable
        ScrollablePanel centroHub = new ScrollablePanel() {
            private static final long serialVersionUID = 1L;
            @Override
            protected void addImpl(Component comp, Object constraints, int index) {
                if (comp instanceof javax.swing.JComponent) {
                    ((javax.swing.JComponent) comp).setAlignmentX(Component.LEFT_ALIGNMENT);
                }
                super.addImpl(comp, constraints, index);
            }
        };
        centroHub.setOpaque(false);
        centroHub.setLayout(new BoxLayout(centroHub, BoxLayout.Y_AXIS));

        // Telemetría 2x2
        JPanel gridTelemetria = new JPanel(new GridLayout(2, 2, 6, 6));
        gridTelemetria.setOpaque(false);
        gridTelemetria.setMaximumSize(new Dimension(Integer.MAX_VALUE, 92));
        gridTelemetria.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblHubMontoOperado = new JLabel("S/. 0.00");
        lblHubSaldoResultante = new JLabel("S/. 0.00");
        lblHubFechaOperacion = new JLabel("00/00/0000");
        lblHubTipoTransaccion = new JLabel("Ingreso");

        gridTelemetria.add(crearCardMiniTelemetria("MONTO OPERADO", lblHubMontoOperado, Ui.TURQUESA_OSCURO));
        gridTelemetria.add(crearCardMiniTelemetria("SALDO RESULTANTE", lblHubSaldoResultante, Ui.COLOR_EXITO));
        gridTelemetria.add(crearCardMiniTelemetria("FECHA OPERACIÓN", lblHubFechaOperacion, Ui.TEXTO_TITULO));
        gridTelemetria.add(crearCardMiniTelemetria("TIPO TRANSACCIÓN", lblHubTipoTransaccion, Ui.TEXTO_MUTED));

        centroHub.add(gridTelemetria);
        centroHub.add(vGap(8));

        // Especificaciones
        JPanel seccionAuditoria = crearSeccionCard("AUDITORÍA DE CAJA & RESPONSABLE");
        lblHubResponsable = new JTextArea("-");
        configurarTextAreaItem(lblHubResponsable);
        lblHubComprobante = new JTextArea("-");
        configurarTextAreaItem(lblHubComprobante);
        seccionAuditoria.add(crearItemLinea("Responsable:", lblHubResponsable));
        seccionAuditoria.add(vGap(3));
        seccionAuditoria.add(crearItemLinea("Comprobante:", lblHubComprobante));
        centroHub.add(seccionAuditoria);
        centroHub.add(vGap(6));

        JPanel seccionConciliacion = crearSeccionCard("CONCILIACIÓN & JUSTIFICACIÓN CONTABLE");
        lblHubConcepto = new JTextArea("-");
        configurarTextAreaItem(lblHubConcepto);
        lblHubSaldoActualCaja = new JTextArea("-");
        configurarTextAreaItem(lblHubSaldoActualCaja);
        seccionConciliacion.add(crearItemLinea("Detalle:", lblHubConcepto));
        seccionConciliacion.add(vGap(3));
        seccionConciliacion.add(crearItemLinea("Saldo en Caja:", lblHubSaldoActualCaja));
        centroHub.add(seccionConciliacion);

        JScrollPane spHub = new JScrollPane(centroHub);
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

        JButton btnValePDF = crearBotonAccion("Vale de Caja PDF", Iconos.crearIconoFactura(14, Color.WHITE), true);
        btnValePDF.addActionListener(e -> emitirValePDF());

        JButton btnArqueoModal = crearBotonAccion("Arqueo Físico", Iconos.crearIconoCajaChica(14, Ui.TEXTO_TITULO), false);
        btnArqueoModal.addActionListener(e -> realizarArqueoFisico());

        JButton btnCierreTurno = crearBotonAccion("Cierre de Turno", Iconos.crearIconoExportar(14, Ui.TURQUESA_OSCURO), false);
        btnCierreTurno.addActionListener(e -> emitirCierreTurnoPDF());

        JButton btnAnular = crearBotonAccion("Anular Movimiento", Iconos.crearIconoAlertaMerma(14, Ui.COLOR_PELIGRO), false);
        btnAnular.addActionListener(e -> anularMovimientoSeleccionado());

        gridAcciones.add(btnValePDF);
        gridAcciones.add(btnArqueoModal);
        gridAcciones.add(btnCierreTurno);
        gridAcciones.add(btnAnular);

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
        l.setVerticalAlignment(SwingConstants.TOP);
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
        btn.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(4, 6, 4, 6));
        return btn;
    }

    public void recargarDatos() {
        actualizarKpis();
        filtrarCaja();
    }

    private void actualizarKpis() {
        lblKpiSaldoActual.setText(String.format("S/. %.2f", repo.getSaldoActualCajaChica()));
        lblKpiTotalIngresos.setText(String.format("S/. %.2f", repo.getTotalIngresosCajaChica()));
        lblKpiTotalEgresos.setText(String.format("S/. %.2f", repo.getTotalEgresosCajaChica()));
        lblKpiOperaciones.setText(String.valueOf(repo.getMovimientosCajaChica().size()));
    }

    private void filtrarCaja() {
        String query = txtBuscar != null ? txtBuscar.getText().trim().toLowerCase() : "";
        List<MovimientoCajaChica> todos = repo.getMovimientosCajaChica();
        listaCajaFiltrada.clear();

        for (MovimientoCajaChica m : todos) {
            boolean coincideTipo = true;
            if ("Ingresos".equals(filtroTipoSeleccionado)) {
                coincideTipo = m.esIngreso();
            } else if ("Egresos".equals(filtroTipoSeleccionado)) {
                coincideTipo = m.esEgreso();
            } else if ("Con Comprobante".equals(filtroTipoSeleccionado)) {
                coincideTipo = m.getComprobante() != null && !m.getComprobante().equals("-") && !m.getComprobante().trim().isEmpty();
            } else if ("Hoy".equals(filtroTipoSeleccionado)) {
                coincideTipo = m.getFecha() != null && m.getFecha().isEqual(LocalDate.now());
            }
            if (!coincideTipo) continue;

            if (!query.isEmpty()) {
                boolean match = m.getIdMovimiento().toLowerCase().contains(query)
                        || m.getConcepto().toLowerCase().contains(query)
                        || m.getResponsable().toLowerCase().contains(query)
                        || m.getComprobante().toLowerCase().contains(query);
                if (!match) continue;
            }

            listaCajaFiltrada.add(m);
        }

        modeloCaja.setRowCount(0);
        for (MovimientoCajaChica m : listaCajaFiltrada) {
            modeloCaja.addRow(new Object[]{
                    m.getIdMovimiento(),
                    m.getTipo(),
                    m.getConcepto(),
                    String.format("%.2f", m.getMonto()),
                    String.format("%.2f", m.getSaldoResultante()),
                    m.getResponsable(),
                    m.getFecha() != null ? m.getFecha().format(FORMATO_FECHA) : "-"
            });
        }

        if (!listaCajaFiltrada.isEmpty()) {
            tablaCaja.setRowSelectionInterval(0, 0);
            mostrarDetalleMovimiento(listaCajaFiltrada.get(0));
        } else {
            mostrarDetalleMovimiento(null);
        }
    }

    private void mostrarDetalleMovimiento(MovimientoCajaChica m) {
        this.movimientoSeleccionado = m;
        panelHubContenido.removeAll();

        if (m == null) {
            panelHubContenido.add(panelHubVacio, BorderLayout.CENTER);
            panelHubContenido.revalidate();
            panelHubContenido.repaint();
            return;
        }

        lblHubAvatar.setText(m.esIngreso() ? "+" : "-");
        lblHubTitulo.setText(m.getConcepto());
        lblHubSubtitulo.setText(m.getIdMovimiento() + " · " + m.getResponsable());
        lblHubChipTipo.setText("  " + m.getTipo() + "  ");

        if (m.esIngreso()) {
            lblHubChipTipo.setForeground(Ui.COLOR_EXITO);
            lblHubChipTipo.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(187, 247, 208), 1),
                    new EmptyBorder(4, 8, 4, 8)
            ));
        } else {
            lblHubChipTipo.setForeground(Ui.COLOR_PELIGRO);
            lblHubChipTipo.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(254, 202, 202), 1),
                    new EmptyBorder(4, 8, 4, 8)
            ));
        }

        lblHubMontoOperado.setText(String.format("S/. %.2f", m.getMonto()));
        lblHubSaldoResultante.setText(String.format("S/. %.2f", m.getSaldoResultante()));
        lblHubFechaOperacion.setText(m.getFecha() != null ? m.getFecha().format(FORMATO_FECHA) : "-");
        lblHubTipoTransaccion.setText(m.getTipo());

        lblHubResponsable.setText(m.getResponsable());
        lblHubComprobante.setText(m.getComprobante());
        lblHubConcepto.setText(m.getConcepto());
        lblHubSaldoActualCaja.setText(String.format("S/. %.2f", repo.getSaldoActualCajaChica()));

        panelHubContenido.add(panelHubDetalle, BorderLayout.CENTER);
        panelHubContenido.revalidate();
        panelHubContenido.repaint();
    }

    private void abrirDialogoNuevoMovimiento() {
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        FormularioMovimientoCajaChicaDialog dlg = new FormularioMovimientoCajaChicaDialog(parent);
        dlg.setVisible(true);
        if (dlg.isConfirmado()) {
            recargarDatos();
        }
    }

    private void realizarArqueoFisico() {
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        JDialog dlg = new JDialog(parent, "Arqueo Físico de Billetes y Monedas", true);
        dlg.setSize(520, 500);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());

        JPanel p = new JPanel(new BorderLayout(0, 12));
        p.setBackground(Color.WHITE);
        p.setBorder(new EmptyBorder(16, 20, 16, 20));

        JLabel tit = new JLabel("Conteo por Denominación en Caja Chica");
        tit.setFont(new Font("Segoe UI", Font.BOLD, 15));
        tit.setForeground(Ui.TEXTO_TITULO);
        p.add(tit, BorderLayout.NORTH);

        JPanel gridDenom = new JPanel(new GridLayout(6, 2, 10, 8));
        gridDenom.setOpaque(false);

        int[] denominaciones = {100, 50, 20, 10, 5, 1};
        JSpinner[] spinners = new JSpinner[denominaciones.length];

        for (int i = 0; i < denominaciones.length; i++) {
            gridDenom.add(new JLabel("Denominación S/. " + denominaciones[i] + ":"));
            spinners[i] = new JSpinner(new SpinnerNumberModel(0, 0, 500, 1));
            gridDenom.add(spinners[i]);
        }
        p.add(gridDenom, BorderLayout.CENTER);

        JPanel bot = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        bot.setOpaque(false);

        JButton btnCalcular = Ui.boton("Conciliar Arqueo", true);
        btnCalcular.addActionListener(e -> {
            double conteoTotal = 0.0;
            for (int i = 0; i < denominaciones.length; i++) {
                int cant = (Integer) spinners[i].getValue();
                conteoTotal += (cant * denominaciones[i]);
            }
            double saldoSistema = repo.getSaldoActualCajaChica();
            double diferencia = conteoTotal - saldoSistema;

            String resultado = String.format("Conteo Físico Total: S/. %.2f\nSaldo en Sistema: S/. %.2f\n", conteoTotal, saldoSistema);
            if (Math.abs(diferencia) < 0.01) {
                resultado += "Estado: [CONFORME] Arqueo cuadrado exactamente sin diferencias.";
            } else if (diferencia > 0) {
                resultado += String.format("Estado: [SOBRANTE] Sobrante en caja de S/. %.2f.", diferencia);
            } else {
                resultado += String.format("Estado: [FALTANTE] Faltante en caja de S/. %.2f.", Math.abs(diferencia));
            }

            JOptionPane.showMessageDialog(dlg, resultado, "Resultado del Arqueo", JOptionPane.INFORMATION_MESSAGE);
            dlg.dispose();
        });

        JButton btnCerrar = Ui.boton("Cerrar", false);
        btnCerrar.addActionListener(e -> dlg.dispose());

        bot.add(btnCerrar);
        bot.add(btnCalcular);
        p.add(bot, BorderLayout.SOUTH);

        dlg.add(p);
        dlg.setVisible(true);
    }

    private void emitirValePDF() {
        if (movimientoSeleccionado == null) return;

        List<Object[]> filas = new ArrayList<>();
        filas.add(new Object[]{
                movimientoSeleccionado.getIdMovimiento(),
                movimientoSeleccionado.getConcepto(),
                movimientoSeleccionado.getTipo(),
                String.format("S/ %.2f", movimientoSeleccionado.getMonto()),
                String.format("S/ %.2f", movimientoSeleccionado.getSaldoResultante()),
                movimientoSeleccionado.getComprobante()
        });

        Ui.mostrarVisorReporte(
                (Frame) SwingUtilities.getWindowAncestor(this),
                "Vale de Caja " + movimientoSeleccionado.getIdMovimiento(),
                "Comprobante Oficial de Movimiento en Caja Chica",
                new String[][]{
                        {"N° Operación", movimientoSeleccionado.getIdMovimiento()},
                        {"Fecha", movimientoSeleccionado.getFecha() != null ? movimientoSeleccionado.getFecha().format(FORMATO_FECHA) : "-"},
                        {"Tipo", movimientoSeleccionado.getTipo()},
                        {"Responsable", movimientoSeleccionado.getResponsable()},
                        {"Comprobante / Vale", movimientoSeleccionado.getComprobante()}
                },
                new String[]{"ID Mov.", "Concepto", "Tipo", "Monto Operado", "Saldo Resultante", "Comprobante"},
                filas,
                "MONTO: S/ " + String.format("%.2f", movimientoSeleccionado.getMonto())
                        + " | Saldo Resultante en Caja: S/ " + String.format("%.2f", movimientoSeleccionado.getSaldoResultante()),
                "vale_caja_" + movimientoSeleccionado.getIdMovimiento()
        );
    }

    private void emitirCierreTurnoPDF() {
        List<Object[]> filas = new ArrayList<>();
        for (MovimientoCajaChica m : repo.getMovimientosCajaChica()) {
            filas.add(new Object[]{
                    m.getIdMovimiento(),
                    m.getFecha() != null ? m.getFecha().format(FORMATO_FECHA) : "-",
                    m.getTipo(),
                    m.getConcepto(),
                    String.format("S/ %.2f", m.getMonto()),
                    m.getResponsable()
            });
        }

        Ui.mostrarVisorReporte(
                (Frame) SwingUtilities.getWindowAncestor(this),
                "Cierre de Turno de Caja Chica",
                "Acta de Cuadre y Conciliación Oficial de Efectivo",
                new String[][]{
                        {"Fecha de Cierre", LocalDate.now().format(FORMATO_FECHA)},
                        {"Cajero Responsable", "Juan Pérez Castro (Caja Central)"},
                        {"Ingresos del Turno", String.format("S/ %.2f", repo.getTotalIngresosCajaChica())},
                        {"Egresos del Turno", String.format("S/ %.2f", repo.getTotalEgresosCajaChica())},
                        {"Saldo Final en Custodia", String.format("S/ %.2f", repo.getSaldoActualCajaChica())}
                },
                new String[]{"ID Mov.", "Fecha", "Tipo", "Concepto", "Monto", "Responsable"},
                filas,
                "Ingresos: S/ " + String.format("%.2f", repo.getTotalIngresosCajaChica())
                        + " | Egresos: S/ " + String.format("%.2f", repo.getTotalEgresosCajaChica())
                        + " | SALDO FINAL EN CUSTODIA: S/ " + String.format("%.2f", repo.getSaldoActualCajaChica()),
                "cierre_turno_caja"
        );
    }

    private void anularMovimientoSeleccionado() {
        if (movimientoSeleccionado == null) return;

        int opt = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de anular el movimiento " + movimientoSeleccionado.getIdMovimiento() + "?\n" +
                        "Esta acción recalculará los saldos de caja y registrará la auditoría correspondiente.",
                "Confirmar Anulación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (opt == JOptionPane.YES_OPTION) {
            repo.eliminarMovimientoCajaChica(movimientoSeleccionado.getIdMovimiento());
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
