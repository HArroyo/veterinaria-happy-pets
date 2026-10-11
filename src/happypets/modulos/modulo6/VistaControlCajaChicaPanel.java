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
import javax.swing.JTable;
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
    private JLabel lblHubResponsable;
    private JLabel lblHubComprobante;
    private JLabel lblHubConcepto;
    private JLabel lblHubSaldoActualCaja;

    private MovimientoCajaChica movimientoSeleccionado;

    public VistaControlCajaChicaPanel() {
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
                JPanel cell = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
                cell.setOpaque(true);
                cell.setBackground(isSelected ? table.getSelectionBackground() : (row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252)));

                String concepto = value != null ? value.toString() : "";
                String letra = !concepto.isEmpty() ? concepto.substring(0, 1).toUpperCase() : "M";

                JLabel avatar = new JLabel(letra, SwingConstants.CENTER) {
                    private static final long serialVersionUID = 1L;
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(new Color(240, 253, 250));
                        g2.fillOval(0, 0, getWidth() - 1, getHeight() - 1);
                        g2.setColor(new Color(153, 246, 228));
                        g2.drawOval(0, 0, getWidth() - 1, getHeight() - 1);
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

                cell.add(avatar);
                cell.add(lblTexto);
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
        panelHubDetalle.setBorder(new EmptyBorder(12, 14, 12, 14));

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
        lblHubAvatar.setPreferredSize(new Dimension(46, 46));
        lblHubAvatar.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblHubAvatar.setForeground(Ui.TURQUESA_PROFUNDO);
        cabHub.add(lblHubAvatar, BorderLayout.WEST);

        JPanel titulosHub = new JPanel();
        titulosHub.setOpaque(false);
        titulosHub.setLayout(new BoxLayout(titulosHub, BoxLayout.Y_AXIS));

        lblHubTitulo = new JLabel("Concepto de la Operación");
        lblHubTitulo.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblHubTitulo.setForeground(Ui.TEXTO_TITULO);

        lblHubSubtitulo = new JLabel("CCH-001 · Responsable: Juan Pérez");
        lblHubSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblHubSubtitulo.setForeground(Ui.TEXTO_MUTED);

        titulosHub.add(lblHubTitulo);
        titulosHub.add(Box.createVerticalStrut(2));
        titulosHub.add(lblHubSubtitulo);
        cabHub.add(titulosHub, BorderLayout.CENTER);

        lblHubChipTipo = new JLabel("  Ingreso  ");
        lblHubChipTipo.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblHubChipTipo.setForeground(Ui.COLOR_EXITO);
        lblHubChipTipo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(187, 247, 208), 1),
                new EmptyBorder(4, 8, 4, 8)
        ));
        cabHub.add(lblHubChipTipo, BorderLayout.EAST);
        panelHubDetalle.add(cabHub, BorderLayout.NORTH);

        // 2. Centro con Telemetría 2x2 y Especificaciones
        JPanel centroHub = new JPanel();
        centroHub.setOpaque(false);
        centroHub.setLayout(new BoxLayout(centroHub, BoxLayout.Y_AXIS));

        // Telemetría 2x2
        JPanel gridTelemetria = new JPanel(new GridLayout(2, 2, 8, 8));
        gridTelemetria.setOpaque(false);
        gridTelemetria.setMaximumSize(new Dimension(Integer.MAX_VALUE, 115));

        lblHubMontoOperado = new JLabel("S/. 0.00");
        lblHubSaldoResultante = new JLabel("S/. 0.00");
        lblHubFechaOperacion = new JLabel("00/00/0000");
        lblHubTipoTransaccion = new JLabel("Ingreso");

        gridTelemetria.add(crearCardMiniTelemetria("MONTO OPERADO", lblHubMontoOperado, Ui.TURQUESA_OSCURO));
        gridTelemetria.add(crearCardMiniTelemetria("SALDO RESULTANTE", lblHubSaldoResultante, Ui.COLOR_EXITO));
        gridTelemetria.add(crearCardMiniTelemetria("FECHA OPERACIÓN", lblHubFechaOperacion, Ui.TEXTO_TITULO));
        gridTelemetria.add(crearCardMiniTelemetria("TIPO TRANSACCIÓN", lblHubTipoTransaccion, Ui.TEXTO_MUTED));

        centroHub.add(gridTelemetria);
        centroHub.add(Box.createVerticalStrut(8));

        // Especificaciones
        centroHub.add(crearSeccionTitulo("AUDITORÍA DE CAJA & RESPONSABLE"));
        lblHubResponsable = new JLabel("Responsable de Turno: -");
        lblHubResponsable.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblHubComprobante = new JLabel("Comprobante de Respaldo: -");
        lblHubComprobante.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        centroHub.add(lblHubResponsable);
        centroHub.add(lblHubComprobante);
        centroHub.add(Box.createVerticalStrut(8));

        centroHub.add(crearSeccionTitulo("CONCILIACIÓN & JUSTIFICACIÓN CONTABLE"));
        lblHubConcepto = new JLabel("Detalle: -");
        lblHubConcepto.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblHubSaldoActualCaja = new JLabel("Saldo Total de Caja: S/. 0.00");
        lblHubSaldoActualCaja.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        centroHub.add(lblHubConcepto);
        centroHub.add(lblHubSaldoActualCaja);

        panelHubDetalle.add(centroHub, BorderLayout.CENTER);

        // 3. Footer con 4 Botones de Acción 1-click
        JPanel gridAcciones = new JPanel(new GridLayout(2, 2, 8, 8));
        gridAcciones.setOpaque(false);
        gridAcciones.setMaximumSize(new Dimension(Integer.MAX_VALUE, 76));

        JButton btnValePDF = Ui.boton("Vale de Caja PDF", true);
        btnValePDF.setIcon(Iconos.crearIconoFactura(14, Color.WHITE));
        btnValePDF.addActionListener(e -> emitirValePDF());

        JButton btnArqueoModal = Ui.boton("Arqueo Físico", false);
        btnArqueoModal.setIcon(Iconos.crearIconoCajaChica(14, Ui.TEXTO_TITULO));
        btnArqueoModal.addActionListener(e -> realizarArqueoFisico());

        JButton btnCierreTurno = Ui.boton("Cierre de Turno", false);
        btnCierreTurno.setIcon(Iconos.crearIconoExportar(14, Ui.TURQUESA_OSCURO));
        btnCierreTurno.addActionListener(e -> emitirCierreTurnoPDF());

        JButton btnAnular = Ui.boton("Anular Movimiento", false);
        btnAnular.setIcon(Iconos.crearIconoAlertaMerma(14, Ui.COLOR_PELIGRO));
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

        lblHubResponsable.setText("Responsable: " + m.getResponsable());
        lblHubComprobante.setText("Comprobante: " + m.getComprobante());
        lblHubConcepto.setText("Detalle: " + m.getConcepto());
        lblHubSaldoActualCaja.setText(String.format("Saldo Total en Custodia: S/. %.2f", repo.getSaldoActualCajaChica()));

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
}
