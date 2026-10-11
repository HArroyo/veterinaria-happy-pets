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
import java.time.temporal.ChronoUnit;
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
import happypets.model.CuentaPorCobrar;
import happypets.model.CuentaPorPagar;
import happypets.ui.Iconos;
import happypets.ui.Ui;

/**
 * Submódulo 6.2: Cuentas por Cobrar y Cuentas por Pagar (Gestión de Liquidez).
 * Arquitectura Master-Detail (58% / 42%) - Opción A Recomendada:
 * - Master Table: Cartera integral unificada de créditos a clientes y pasivos con laboratorios/proveedores.
 * - Live Credit/Debt Hub: Telemetría 2x2, plazos de mora, amortizaciones y acciones 1-click.
 */
public class VistaCuentasCobrarPagarPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final RepositorioVeterinaria repo = RepositorioVeterinaria.getInstancia();

    // KPIs
    private JLabel lblKpiCobrar;
    private JLabel lblKpiPagar;
    private JLabel lblKpiVencidas;
    private JLabel lblKpiBalance;

    // Tabla Maestra (Izquierda - 58%)
    private JTable tablaCartera;
    private DefaultTableModel modeloCartera;
    private JTextField txtBuscar;
    private String filtroTipoSeleccionado = "TODAS";
    private final List<FilaCartera> listaCarteraFiltrada = new ArrayList<>();

    // Live Hub (Derecha - 42%)
    private JPanel panelHubContenido;
    private JPanel panelHubVacio;
    private JPanel panelHubDetalle;

    private JLabel lblHubAvatar;
    private JLabel lblHubTitulo;
    private JLabel lblHubSubtitulo;
    private JLabel lblHubChipEstado;

    // Telemetría 2x2
    private JLabel lblHubMontoOriginal;
    private JLabel lblHubSaldoPendiente;
    private JLabel lblHubTotalAbonado;
    private JLabel lblHubDiasVence;

    // Secciones de Detalle
    private JLabel lblHubTitularTipo;
    private JLabel lblHubDocumentoRef;
    private JLabel lblHubConcepto;
    private JLabel lblHubFechaEmisionVence;

    private FilaCartera itemSeleccionado;

    public VistaCuentasCobrarPagarPanel() {
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

        JLabel titulo = new JLabel("Cuentas por Cobrar & Cuentas por Pagar");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 17));
        titulo.setForeground(Ui.TEXTO_TITULO);

        JLabel subtitulo = new JLabel("Módulo 6.2 · Control de cartera de crédito, vencimientos y obligaciones con proveedores");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitulo.setForeground(Ui.TEXTO_MUTED);

        izq.add(titulo);
        izq.add(Box.createVerticalStrut(2));
        izq.add(subtitulo);
        cab.add(izq, BorderLayout.WEST);

        JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        der.setOpaque(false);

        JButton btnNuevaCxC = Ui.boton("+ Por Cobrar (Cliente)", true);
        btnNuevaCxC.setIcon(Iconos.crearIconoUsuario(14, Color.WHITE));
        btnNuevaCxC.addActionListener(e -> abrirDialogoNuevaCxC());
        der.add(btnNuevaCxC);

        JButton btnNuevaCxP = Ui.boton("+ Por Pagar (Proveedor)", false);
        btnNuevaCxP.setIcon(Iconos.crearIconoCamionProveedor(14, Ui.TEXTO_TITULO));
        btnNuevaCxP.addActionListener(e -> abrirDialogoNuevaCxP());
        der.add(btnNuevaCxP);

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

        lblKpiCobrar = new JLabel("S/. 0.00");
        lblKpiPagar = new JLabel("S/. 0.00");
        lblKpiVencidas = new JLabel("0");
        lblKpiBalance = new JLabel("S/. 0.00");

        fila.add(crearCardKpi("Total por Cobrar (Clientes)", lblKpiCobrar, "Activo corriente exigible", Iconos.crearIconoCuentas(22, Ui.TURQUESA)));
        fila.add(crearCardKpi("Total por Pagar (Proveedores)", lblKpiPagar, "Pasivo a corto plazo", Iconos.crearIconoCamionProveedor(22, Ui.TURQUESA_OSCURO)));
        fila.add(crearCardKpi("Obligaciones Vencidas", lblKpiVencidas, "Alertas críticas de cobro/pago", Iconos.crearIconoAlertaMerma(22, Ui.COLOR_PELIGRO)));
        fila.add(crearCardKpi("Balance Neto de Cartera", lblKpiBalance, "Por Cobrar - Por Pagar", Iconos.crearIconoMonedas(22, Ui.COLOR_EXITO)));

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
        txtBuscar.putClientProperty("JTextField.placeholderText", "Buscar por cliente, proveedor, concepto o comprobante...");
        txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filtrarCartera(); }
            public void removeUpdate(DocumentEvent e) { filtrarCartera(); }
            public void changedUpdate(DocumentEvent e) { filtrarCartera(); }
        });
        barraBuscar.add(txtBuscar, BorderLayout.CENTER);
        cabFiltros.add(barraBuscar);
        cabFiltros.add(Box.createVerticalStrut(8));

        // Pills de Filtro
        JPanel pills = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        pills.setOpaque(false);

        String[] tipos = {"TODAS", "Por Cobrar", "Por Pagar", "Pendientes", "Parciales", "Pagadas", "Vencidas"};
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
                filtrarCartera();
            });
            pills.add(btnPill);
        }
        cabFiltros.add(pills);
        master.add(cabFiltros, BorderLayout.NORTH);

        // Tabla
        String[] cols = {"ID", "Tipo", "Titular", "Concepto", "Monto Orig.", "Saldo Pend.", "Vence", "Estado"};
        modeloCartera = new DefaultTableModel(cols, 0) {
            private static final long serialVersionUID = 1L;
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tablaCartera = new JTable(modeloCartera);
        tablaCartera.setRowHeight(48);
        tablaCartera.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaCartera.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaCartera.setShowVerticalLines(false);
        tablaCartera.setGridColor(new Color(241, 245, 249));
        tablaCartera.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        tablaCartera.getTableHeader().setBackground(new Color(248, 250, 252));
        tablaCartera.getTableHeader().setForeground(Ui.TEXTO_MUTED);

        tablaCartera.getColumnModel().getColumn(0).setPreferredWidth(70);
        tablaCartera.getColumnModel().getColumn(1).setPreferredWidth(75);
        tablaCartera.getColumnModel().getColumn(2).setPreferredWidth(140);
        tablaCartera.getColumnModel().getColumn(3).setPreferredWidth(150);
        tablaCartera.getColumnModel().getColumn(4).setPreferredWidth(75);
        tablaCartera.getColumnModel().getColumn(5).setPreferredWidth(75);
        tablaCartera.getColumnModel().getColumn(6).setPreferredWidth(80);
        tablaCartera.getColumnModel().getColumn(7).setPreferredWidth(80);

        // Monograma en columna 2 (Titular)
        tablaCartera.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            private static final long serialVersionUID = 1L;
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                JPanel cell = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
                cell.setOpaque(true);
                cell.setBackground(isSelected ? table.getSelectionBackground() : (row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252)));

                String nombre = value != null ? value.toString() : "";
                String letra = !nombre.isEmpty() ? nombre.substring(0, 1).toUpperCase() : "C";

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

                JLabel lblNombre = new JLabel(nombre);
                lblNombre.setFont(new Font("Segoe UI", Font.BOLD, 11));
                lblNombre.setForeground(isSelected ? table.getSelectionForeground() : Ui.TEXTO_TITULO);

                cell.add(avatar);
                cell.add(lblNombre);
                return cell;
            }
        });

        // Render de Estado (Columna 7)
        tablaCartera.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
            private static final long serialVersionUID = 1L;
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                l.setHorizontalAlignment(SwingConstants.CENTER);
                String estado = value != null ? value.toString() : "";
                if ("Pagado".equalsIgnoreCase(estado)) {
                    l.setForeground(Ui.COLOR_EXITO);
                    l.setFont(new Font("Segoe UI", Font.BOLD, 11));
                } else if ("Parcial".equalsIgnoreCase(estado)) {
                    l.setForeground(Ui.TURQUESA_OSCURO);
                    l.setFont(new Font("Segoe UI", Font.BOLD, 11));
                } else if ("Vencido".equalsIgnoreCase(estado)) {
                    l.setForeground(Ui.COLOR_PELIGRO);
                    l.setFont(new Font("Segoe UI", Font.BOLD, 11));
                } else {
                    l.setForeground(new Color(217, 119, 6));
                    l.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                }
                return l;
            }
        });

        tablaCartera.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int r = tablaCartera.getSelectedRow();
                if (r >= 0 && r < listaCarteraFiltrada.size()) {
                    mostrarDetalleItem(listaCarteraFiltrada.get(r));
                } else {
                    mostrarDetalleItem(null);
                }
            }
        });

        JScrollPane scroll = new JScrollPane(tablaCartera);
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
        JLabel lblVacio = new JLabel("Seleccione una cuenta para consultar el Live Credit Hub", SwingConstants.CENTER);
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

        lblHubAvatar = new JLabel("C", SwingConstants.CENTER) {
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

        lblHubTitulo = new JLabel("Titular de la Cuenta");
        lblHubTitulo.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblHubTitulo.setForeground(Ui.TEXTO_TITULO);

        lblHubSubtitulo = new JLabel("CXC-001 · Cuenta por Cobrar");
        lblHubSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblHubSubtitulo.setForeground(Ui.TEXTO_MUTED);

        titulosHub.add(lblHubTitulo);
        titulosHub.add(Box.createVerticalStrut(2));
        titulosHub.add(lblHubSubtitulo);
        cabHub.add(titulosHub, BorderLayout.CENTER);

        lblHubChipEstado = new JLabel("  Pendiente  ");
        lblHubChipEstado.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblHubChipEstado.setForeground(new Color(217, 119, 6));
        lblHubChipEstado.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(254, 215, 170), 1),
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

        lblHubMontoOriginal = new JLabel("S/. 0.00");
        lblHubSaldoPendiente = new JLabel("S/. 0.00");
        lblHubTotalAbonado = new JLabel("S/. 0.00");
        lblHubDiasVence = new JLabel("0 días");

        gridTelemetria.add(crearCardMiniTelemetria("MONTO TOTAL", lblHubMontoOriginal, Ui.TEXTO_TITULO));
        gridTelemetria.add(crearCardMiniTelemetria("SALDO PENDIENTE", lblHubSaldoPendiente, Ui.COLOR_PELIGRO));
        gridTelemetria.add(crearCardMiniTelemetria("TOTAL AMORTIZADO", lblHubTotalAbonado, Ui.COLOR_EXITO));
        gridTelemetria.add(crearCardMiniTelemetria("PLAZO / VENCIMIENTO", lblHubDiasVence, Ui.TURQUESA_OSCURO));

        centroHub.add(gridTelemetria);
        centroHub.add(Box.createVerticalStrut(8));

        // Especificaciones
        centroHub.add(crearSeccionTitulo("INFORMACIÓN DEL TITULAR & CONTACTO"));
        lblHubTitularTipo = new JLabel("Titular: -");
        lblHubTitularTipo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblHubDocumentoRef = new JLabel("Referencia / Teléfono: -");
        lblHubDocumentoRef.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        centroHub.add(lblHubTitularTipo);
        centroHub.add(lblHubDocumentoRef);
        centroHub.add(Box.createVerticalStrut(8));

        centroHub.add(crearSeccionTitulo("DETALLE DE LA PRESTACIÓN O COMPRA"));
        lblHubConcepto = new JLabel("Concepto: -");
        lblHubConcepto.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblHubFechaEmisionVence = new JLabel("Emisión: - | Vence: -");
        lblHubFechaEmisionVence.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        centroHub.add(lblHubConcepto);
        centroHub.add(lblHubFechaEmisionVence);

        panelHubDetalle.add(centroHub, BorderLayout.CENTER);

        // 3. Footer con 4 Botones de Acción 1-click
        JPanel gridAcciones = new JPanel(new GridLayout(2, 2, 8, 8));
        gridAcciones.setOpaque(false);
        gridAcciones.setMaximumSize(new Dimension(Integer.MAX_VALUE, 76));

        JButton btnRegistrarPago = Ui.boton("Registrar Abono / Pago", true);
        btnRegistrarPago.setIcon(Iconos.crearIconoMonedas(14, Color.WHITE));
        btnRegistrarPago.addActionListener(e -> registrarAbonoPago());

        JButton btnEstadoCuenta = Ui.boton("Estado de Cuenta PDF", false);
        btnEstadoCuenta.setIcon(Iconos.crearIconoFactura(14, Ui.TEXTO_TITULO));
        btnEstadoCuenta.addActionListener(e -> emitirEstadoCuentaPDF());

        JButton btnWhatsApp = Ui.boton("Notificar WhatsApp", false);
        btnWhatsApp.setIcon(Iconos.crearIconoMensaje(14, Ui.COLOR_EXITO));
        btnWhatsApp.addActionListener(e -> notificarWhatsApp());

        JButton btnModificar = Ui.boton("Editar / Anular", false);
        btnModificar.setIcon(Iconos.crearIconoEditar(14, Ui.TEXTO_TITULO));
        btnModificar.addActionListener(e -> editarOAnularCuenta());

        gridAcciones.add(btnRegistrarPago);
        gridAcciones.add(btnEstadoCuenta);
        gridAcciones.add(btnWhatsApp);
        gridAcciones.add(btnModificar);

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
        filtrarCartera();
    }

    private void actualizarKpis() {
        List<CuentaPorCobrar> cxcList = repo.getCuentasPorCobrar();
        List<CuentaPorPagar> cxpList = repo.getCuentasPorPagar();

        double totalCobrar = 0.0;
        int vencidas = 0;
        for (CuentaPorCobrar c : cxcList) {
            totalCobrar += c.getSaldo();
            if ("Vencido".equalsIgnoreCase(c.getEstado()) || (c.getFechaVence() != null && c.getFechaVence().isBefore(LocalDate.now()) && !"Pagado".equalsIgnoreCase(c.getEstado()))) {
                vencidas++;
            }
        }

        double totalPagar = 0.0;
        for (CuentaPorPagar p : cxpList) {
            totalPagar += p.getSaldo();
            if ("Vencido".equalsIgnoreCase(p.getEstado()) || (p.getFechaVence() != null && p.getFechaVence().isBefore(LocalDate.now()) && !"Pagado".equalsIgnoreCase(p.getEstado()))) {
                vencidas++;
            }
        }

        double balance = totalCobrar - totalPagar;

        lblKpiCobrar.setText(String.format("S/. %.2f", totalCobrar));
        lblKpiPagar.setText(String.format("S/. %.2f", totalPagar));
        lblKpiVencidas.setText(String.valueOf(vencidas));
        lblKpiBalance.setText(String.format("S/. %.2f", balance));
    }

    private void filtrarCartera() {
        String query = txtBuscar != null ? txtBuscar.getText().trim().toLowerCase() : "";
        listaCarteraFiltrada.clear();

        // 1. Cuentas por Cobrar
        if (!"Por Pagar".equals(filtroTipoSeleccionado)) {
            for (CuentaPorCobrar c : repo.getCuentasPorCobrar()) {
                if (!coincideFiltroEstado(c.getEstado(), c.getFechaVence())) continue;

                if (!query.isEmpty()) {
                    boolean match = c.getIdCuenta().toLowerCase().contains(query)
                            || c.getCliente().toLowerCase().contains(query)
                            || c.getConcepto().toLowerCase().contains(query)
                            || c.getComprobanteReferencia().toLowerCase().contains(query);
                    if (!match) continue;
                }
                listaCarteraFiltrada.add(new FilaCartera(c));
            }
        }

        // 2. Cuentas por Pagar
        if (!"Por Cobrar".equals(filtroTipoSeleccionado)) {
            for (CuentaPorPagar p : repo.getCuentasPorPagar()) {
                if (!coincideFiltroEstado(p.getEstado(), p.getFechaVence())) continue;

                if (!query.isEmpty()) {
                    boolean match = p.getIdCuenta().toLowerCase().contains(query)
                            || p.getProveedor().toLowerCase().contains(query)
                            || p.getConcepto().toLowerCase().contains(query)
                            || p.getFacturaProveedor().toLowerCase().contains(query);
                    if (!match) continue;
                }
                listaCarteraFiltrada.add(new FilaCartera(p));
            }
        }

        modeloCartera.setRowCount(0);
        for (FilaCartera f : listaCarteraFiltrada) {
            modeloCartera.addRow(new Object[]{
                    f.id,
                    f.esCobrar ? "Por Cobrar" : "Por Pagar",
                    f.titular,
                    f.concepto,
                    String.format("%.2f", f.montoTotal),
                    String.format("%.2f", f.saldoPendiente),
                    f.fechaVence != null ? f.fechaVence.format(FORMATO_FECHA) : "-",
                    f.estado
            });
        }

        if (!listaCarteraFiltrada.isEmpty()) {
            tablaCartera.setRowSelectionInterval(0, 0);
            mostrarDetalleItem(listaCarteraFiltrada.get(0));
        } else {
            mostrarDetalleItem(null);
        }
    }

    private boolean coincideFiltroEstado(String estado, LocalDate fechaVence) {
        if ("TODAS".equals(filtroTipoSeleccionado) || "Por Cobrar".equals(filtroTipoSeleccionado) || "Por Pagar".equals(filtroTipoSeleccionado)) {
            return true;
        }
        if ("Pendientes".equals(filtroTipoSeleccionado)) {
            return "Pendiente".equalsIgnoreCase(estado);
        }
        if ("Parciales".equals(filtroTipoSeleccionado)) {
            return "Parcial".equalsIgnoreCase(estado);
        }
        if ("Pagadas".equals(filtroTipoSeleccionado)) {
            return "Pagado".equalsIgnoreCase(estado);
        }
        if ("Vencidas".equals(filtroTipoSeleccionado)) {
            return "Vencido".equalsIgnoreCase(estado) || (fechaVence != null && fechaVence.isBefore(LocalDate.now()) && !"Pagado".equalsIgnoreCase(estado));
        }
        return true;
    }

    private void mostrarDetalleItem(FilaCartera f) {
        this.itemSeleccionado = f;
        panelHubContenido.removeAll();

        if (f == null) {
            panelHubContenido.add(panelHubVacio, BorderLayout.CENTER);
            panelHubContenido.revalidate();
            panelHubContenido.repaint();
            return;
        }

        lblHubAvatar.setText(f.titular != null && !f.titular.isEmpty() ? f.titular.substring(0, 1).toUpperCase() : "C");
        lblHubTitulo.setText(f.titular);
        lblHubSubtitulo.setText(f.id + " · " + (f.esCobrar ? "Cuenta por Cobrar (Cliente)" : "Cuenta por Pagar (Proveedor)"));
        lblHubChipEstado.setText("  " + f.estado + "  ");

        if ("Pagado".equalsIgnoreCase(f.estado)) {
            lblHubChipEstado.setForeground(Ui.COLOR_EXITO);
            lblHubChipEstado.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(187, 247, 208), 1),
                    new EmptyBorder(4, 8, 4, 8)
            ));
        } else if ("Parcial".equalsIgnoreCase(f.estado)) {
            lblHubChipEstado.setForeground(Ui.TURQUESA_OSCURO);
            lblHubChipEstado.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(153, 246, 228), 1),
                    new EmptyBorder(4, 8, 4, 8)
            ));
        } else if ("Vencido".equalsIgnoreCase(f.estado) || (f.fechaVence != null && f.fechaVence.isBefore(LocalDate.now()))) {
            lblHubChipEstado.setForeground(Ui.COLOR_PELIGRO);
            lblHubChipEstado.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(254, 202, 202), 1),
                    new EmptyBorder(4, 8, 4, 8)
            ));
        } else {
            lblHubChipEstado.setForeground(new Color(217, 119, 6));
            lblHubChipEstado.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(254, 215, 170), 1),
                    new EmptyBorder(4, 8, 4, 8)
            ));
        }

        lblHubMontoOriginal.setText(String.format("S/. %.2f", f.montoTotal));
        lblHubSaldoPendiente.setText(String.format("S/. %.2f", f.saldoPendiente));
        lblHubTotalAbonado.setText(String.format("S/. %.2f", f.montoAbonado));

        long dias = f.fechaVence != null ? ChronoUnit.DAYS.between(LocalDate.now(), f.fechaVence) : 0;
        if (dias < 0) {
            lblHubDiasVence.setText("Vencido (" + Math.abs(dias) + " d)");
            lblHubDiasVence.setForeground(Ui.COLOR_PELIGRO);
        } else {
            lblHubDiasVence.setText(dias + " días vigentes");
            lblHubDiasVence.setForeground(Ui.TURQUESA_OSCURO);
        }

        lblHubTitularTipo.setText("Titular: " + f.titular + " (" + (f.esCobrar ? "Cliente" : "Laboratorio/Proveedor") + ")");
        lblHubDocumentoRef.setText("Ref / Teléfono: " + (f.referencia != null && !f.referencia.isEmpty() ? f.referencia : "-"));
        lblHubConcepto.setText("Concepto: " + f.concepto);
        lblHubFechaEmisionVence.setText("Emisión: " + (f.fechaRegistro != null ? f.fechaRegistro.format(FORMATO_FECHA) : "-")
                + " | Vence: " + (f.fechaVence != null ? f.fechaVence.format(FORMATO_FECHA) : "-"));

        panelHubContenido.add(panelHubDetalle, BorderLayout.CENTER);
        panelHubContenido.revalidate();
        panelHubContenido.repaint();
    }

    private void abrirDialogoNuevaCxC() {
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        FormularioCuentaCobrarDialog dlg = new FormularioCuentaCobrarDialog(parent);
        dlg.setVisible(true);
        if (dlg.isConfirmado()) {
            recargarDatos();
        }
    }

    private void abrirDialogoNuevaCxP() {
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        FormularioCuentaPagarDialog dlg = new FormularioCuentaPagarDialog(parent);
        dlg.setVisible(true);
        if (dlg.isConfirmado()) {
            recargarDatos();
        }
    }

    private void registrarAbonoPago() {
        if (itemSeleccionado == null) return;
        if (itemSeleccionado.saldoPendiente <= 0) {
            JOptionPane.showMessageDialog(this, "Esta cuenta ya se encuentra totalmente saldada.", "Cuenta Pagada", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String input = JOptionPane.showInputDialog(this,
                "Ingrese el importe a amortizar para " + itemSeleccionado.titular + " (" + itemSeleccionado.id + "):\n" +
                        "Saldo actual pendiente: S/. " + String.format("%.2f", itemSeleccionado.saldoPendiente),
                String.format("%.2f", itemSeleccionado.saldoPendiente));

        if (input != null && !input.trim().isEmpty()) {
            try {
                double abono = Double.parseDouble(input.trim());
                if (abono <= 0) throw new NumberFormatException();

                if (itemSeleccionado.esCobrar) {
                    repo.registrarAbonoCuentaPorCobrar(itemSeleccionado.id, abono);
                } else {
                    repo.registrarPagoCuentaPorPagar(itemSeleccionado.id, abono);
                }

                JOptionPane.showMessageDialog(this,
                        "Abono de S/. " + String.format("%.2f", abono) + " aplicado exitosamente.",
                        "Operación Exitosa", JOptionPane.INFORMATION_MESSAGE);
                recargarDatos();
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Ingrese un monto numérico válido mayor a cero.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void emitirEstadoCuentaPDF() {
        if (itemSeleccionado == null) return;

        List<Object[]> filas = new ArrayList<>();
        filas.add(new Object[]{
                itemSeleccionado.id,
                itemSeleccionado.concepto,
                String.format("S/ %.2f", itemSeleccionado.montoTotal),
                String.format("S/ %.2f", itemSeleccionado.montoAbonado),
                String.format("S/ %.2f", itemSeleccionado.saldoPendiente),
                itemSeleccionado.estado
        });

        Ui.mostrarVisorReporte(
                (Frame) SwingUtilities.getWindowAncestor(this),
                "Estado de Cuenta " + itemSeleccionado.id,
                "Certificado Oficial de Cartera & Liquidación Happy Pets",
                new String[][]{
                        {"Titular", itemSeleccionado.titular},
                        {"Tipo de Obligación", itemSeleccionado.esCobrar ? "Cuenta por Cobrar (Cliente)" : "Cuenta por Pagar (Proveedor)"},
                        {"Documento / Referencia", itemSeleccionado.referencia},
                        {"Fecha de Vencimiento", itemSeleccionado.fechaVence != null ? itemSeleccionado.fechaVence.format(FORMATO_FECHA) : "-"},
                        {"Estado de Cuenta", itemSeleccionado.estado}
                },
                new String[]{"ID Cuenta", "Concepto", "Monto Orig.", "Amortizado", "Saldo Pend.", "Estado"},
                filas,
                "Total Facturado: S/ " + String.format("%.2f", itemSeleccionado.montoTotal)
                        + " | Amortizaciones: S/ " + String.format("%.2f", itemSeleccionado.montoAbonado)
                        + " | SALDO EXIGIBLE: S/ " + String.format("%.2f", itemSeleccionado.saldoPendiente),
                "estado_cuenta_" + itemSeleccionado.id
        );
    }

    private void notificarWhatsApp() {
        if (itemSeleccionado == null) return;
        JOptionPane.showMessageDialog(this,
                "Enviando notificación por WhatsApp:\n" +
                        "Destinatario: " + itemSeleccionado.titular + "\n" +
                        "Mensaje: Estimado(a), le recordamos que la cuenta " + itemSeleccionado.id +
                        " por S/. " + String.format("%.2f", itemSeleccionado.saldoPendiente) +
                        " vence el " + (itemSeleccionado.fechaVence != null ? itemSeleccionado.fechaVence.format(FORMATO_FECHA) : "-") +
                        ". Gracias por su confianza en Happy Pets.",
                "WhatsApp API - Notificación Enviada", JOptionPane.INFORMATION_MESSAGE);
    }

    private void editarOAnularCuenta() {
        if (itemSeleccionado == null) return;
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);

        if (itemSeleccionado.esCobrar) {
            CuentaPorCobrar c = repo.getCuentasPorCobrar().stream()
                    .filter(x -> x.getIdCuenta().equalsIgnoreCase(itemSeleccionado.id))
                    .findFirst().orElse(null);
            if (c != null) {
                FormularioCuentaCobrarDialog dlg = new FormularioCuentaCobrarDialog(parent, c);
                dlg.setVisible(true);
                if (dlg.isConfirmado()) recargarDatos();
            }
        } else {
            CuentaPorPagar p = repo.getCuentasPorPagar().stream()
                    .filter(x -> x.getIdCuenta().equalsIgnoreCase(itemSeleccionado.id))
                    .findFirst().orElse(null);
            if (p != null) {
                FormularioCuentaPagarDialog dlg = new FormularioCuentaPagarDialog(parent, p);
                dlg.setVisible(true);
                if (dlg.isConfirmado()) recargarDatos();
            }
        }
    }

    private static class FilaCartera {
        final String id;
        final boolean esCobrar;
        final String titular;
        final String concepto;
        final double montoTotal;
        final double montoAbonado;
        final double saldoPendiente;
        final LocalDate fechaRegistro;
        final LocalDate fechaVence;
        final String estado;
        final String referencia;

        FilaCartera(CuentaPorCobrar c) {
            this.id = c.getIdCuenta();
            this.esCobrar = true;
            this.titular = c.getCliente();
            this.concepto = c.getConcepto();
            this.montoTotal = c.getMonto();
            this.montoAbonado = c.getMontoPagado();
            this.saldoPendiente = c.getSaldo();
            this.fechaRegistro = c.getFechaRegistro();
            this.fechaVence = c.getFechaVence();
            this.estado = c.getEstado();
            this.referencia = c.getComprobanteReferencia() != null && !c.getComprobanteReferencia().isEmpty()
                    ? c.getComprobanteReferencia() : c.getTelefonoCliente();
        }

        FilaCartera(CuentaPorPagar p) {
            this.id = p.getIdCuenta();
            this.esCobrar = false;
            this.titular = p.getProveedor();
            this.concepto = p.getConcepto();
            this.montoTotal = p.getMonto();
            this.montoAbonado = p.getMontoPagado();
            this.saldoPendiente = p.getSaldo();
            this.fechaRegistro = p.getFechaRegistro();
            this.fechaVence = p.getFechaVence();
            this.estado = p.getEstado();
            this.referencia = p.getFacturaProveedor() != null && !p.getFacturaProveedor().isEmpty()
                    ? p.getFacturaProveedor() : p.getRucProveedor();
        }
    }
}
