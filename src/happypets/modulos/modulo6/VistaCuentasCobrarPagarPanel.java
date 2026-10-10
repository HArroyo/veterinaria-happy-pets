package happypets.modulos.modulo6;

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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import happypets.data.RepositorioVeterinaria;
import happypets.model.CuentaPorCobrar;
import happypets.model.CuentaPorPagar;
import happypets.ui.Iconos;
import happypets.ui.Ui;

/**
 * Submódulo 6.2: Cuentas por Cobrar y Cuentas por Pagar.
 * Basado exactamente en los dos wireframes oficiales:
 * - CUENTAS POR COBRAR.pdf (Clientes, Concepto, Monto, Vence, Estado, Botones Agregar/Modificar/Eliminar/Limpiar, Tabla y Total por cobrar)
 * - CUENTAS POR PAGAR.pdf (Proveedores, Concepto, Monto, Vence, Estado, Botones Agregar/Modificar/Eliminar/Limpiar, Tabla y Total pendiente)
 * Incluye KPIs de liquidez, registro rápido de amortizaciones y enlace contable con Caja Chica y Egresos.
 */
public class VistaCuentasCobrarPagarPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private static final Color COLOR_BORDE = Ui.BORDE_SUAVE;
    private static final Color COLOR_PRIMARIO = Ui.TURQUESA; // Turquesa Clínico Original
    private static final Color COLOR_AZUL = Ui.TURQUESA_OSCURO;
    private static final Color COLOR_VERDE = Ui.COLOR_EXITO;
    private static final Color COLOR_ROJO = Ui.COLOR_PELIGRO;
    private static final Color COLOR_TEXTO_TITULO = Ui.TEXTO_TITULO;
    private static final Color COLOR_TEXTO_MUTED = Ui.TEXTO_MUTED;

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final RepositorioVeterinaria repo = RepositorioVeterinaria.getInstancia();

    // KPIs Generales
    private JLabel lblKpiCobrar;
    private JLabel lblKpiPagar;
    private JLabel lblKpiVencidas;
    private JLabel lblKpiBalance;

    // Componentes: Cuentas por Cobrar
    private JTextField txtClienteCobrar;
    private JTextField txtConceptoCobrar;
    private JTextField txtMontoCobrar;
    private JTextField txtVenceCobrar;
    private JComboBox<String> cboEstadoCobrar;
    private JTable tablaCobrar;
    private DefaultTableModel modeloCobrar;
    private JLabel lblTotalPorCobrar;
    private String idSeleccionadoCobrar = null;

    // Componentes: Cuentas por Pagar
    private JTextField txtProveedorPagar;
    private JTextField txtConceptoPagar;
    private JTextField txtMontoPagar;
    private JTextField txtVencePagar;
    private JComboBox<String> cboEstadoPagar;
    private JTable tablaPagar;
    private DefaultTableModel modeloPagar;
    private JLabel lblTotalPorPagar;
    private String idSeleccionadoPagar = null;

    private JTabbedPane tabSubmodulos;

    public VistaCuentasCobrarPagarPanel() {
        setLayout(new BorderLayout());
        setOpaque(false);

        JPanel contenido = new JPanel();
        contenido.setOpaque(false);
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setBorder(new EmptyBorder(12, 18, 14, 18));

        // 1. Cabecera institucional
        contenido.add(crearCabeceraVista());
        contenido.add(Box.createVerticalStrut(10));

        // 2. Fila de KPIs de cartera y vencimientos
        contenido.add(crearFilaKpis());
        contenido.add(Box.createVerticalStrut(12));

        // 3. Pestañas para Cuentas por Cobrar y Cuentas por Pagar (según los 2 Wireframes)
        tabSubmodulos = new JTabbedPane();
        tabSubmodulos.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabSubmodulos.setBackground(Color.WHITE);

        tabSubmodulos.addTab(" Cuentas por Cobrar (Clientes)",
                Iconos.crearIconoPOS(16, COLOR_VERDE),
                crearPanelCuentasPorCobrar());

        tabSubmodulos.addTab(" Cuentas por Pagar (Proveedores)",
                Iconos.crearIconoFactura(16, COLOR_ROJO),
                crearPanelCuentasPorPagar());

        contenido.add(tabSubmodulos);

        add(contenido, BorderLayout.CENTER);

        recargarDatos();
    }

    private JPanel crearCabeceraVista() {
        JPanel cab = new JPanel(new BorderLayout());
        cab.setOpaque(false);

        JPanel izq = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        izq.setOpaque(false);

        JLabel ico = new JLabel(Iconos.crearIconoCuentas(22, COLOR_PRIMARIO));
        izq.add(ico);

        JPanel titulos = new JPanel();
        titulos.setOpaque(false);
        titulos.setLayout(new BoxLayout(titulos, BoxLayout.Y_AXIS));

        JLabel lblTit = new JLabel("Submódulo 6.2: Cuentas por Cobrar y Pagar");
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblTit.setForeground(COLOR_TEXTO_TITULO);

        JLabel lblSub = new JLabel("Control de créditos a propietarios, obligaciones con proveedores de farmacia y amortizaciones");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(COLOR_TEXTO_MUTED);

        titulos.add(lblTit);
        titulos.add(lblSub);
        izq.add(titulos);
        cab.add(izq, BorderLayout.WEST);

        return cab;
    }

    private JPanel crearFilaKpis() {
        JPanel fila = new JPanel(new GridLayout(1, 4, 12, 0));
        fila.setOpaque(false);
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 74));

        lblKpiCobrar = new JLabel("S/ 0.00");
        lblKpiPagar = new JLabel("S/ 0.00");
        lblKpiVencidas = new JLabel("0 cuentas");
        lblKpiBalance = new JLabel("S/ 0.00");

        fila.add(crearCardKpi("Por Cobrar (Activo)", lblKpiCobrar, COLOR_VERDE, "Créditos otorgados a clientes"));
        fila.add(crearCardKpi("Por Pagar (Pasivo)", lblKpiPagar, COLOR_ROJO, "Deudas con distribuidores"));
        fila.add(crearCardKpi("Cartera Vencida", lblKpiVencidas, new Color(245, 158, 11), "Expiradas fuera de plazo"));
        fila.add(crearCardKpi("Balance Neto Cartera", lblKpiBalance, COLOR_AZUL, "Diferencial de liquidez"));

        return fila;
    }

    private JPanel crearCardKpi(String titulo, JLabel lblValor, Color colorAcento, String subtitulo) {
        JPanel card = new JPanel(new BorderLayout(4, 4)) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(COLOR_BORDE);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.setColor(colorAcento);
                g2.fillRect(0, 0, 4, getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(8, 12, 8, 12));

        JLabel lblT = new JLabel(titulo.toUpperCase());
        lblT.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblT.setForeground(COLOR_TEXTO_MUTED);

        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblValor.setForeground(colorAcento);

        JLabel lblS = new JLabel(subtitulo);
        lblS.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblS.setForeground(COLOR_TEXTO_MUTED);

        card.add(lblT, BorderLayout.NORTH);
        card.add(lblValor, BorderLayout.CENTER);
        card.add(lblS, BorderLayout.SOUTH);

        return card;
    }

    // =========================================================================
    // PANEL 1: CUENTAS POR COBRAR (Fiel al wireframe CUENTAS POR COBRAR.pdf)
    // =========================================================================
    private JPanel crearPanelCuentasPorCobrar() {
        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Formulario del Wireframe:
        // Cliente: txtCliente | Monto: txtMonto
        // Concepto: txtConcepto | Vence: txtVence
        //                         Estado: cboEstado: Pendiente
        JPanel boxForm = new JPanel(new BorderLayout(0, 8));
        boxForm.setBackground(Color.WHITE);
        boxForm.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDE, 1),
                new EmptyBorder(10, 16, 12, 16)
        ));

        JPanel gridCampos = new JPanel(new GridLayout(3, 2, 18, 8));
        gridCampos.setOpaque(false);

        // Fila 1: Cliente y Monto
        JPanel fCli = new JPanel(new BorderLayout(6, 0));
        fCli.setOpaque(false);
        JLabel lCli = new JLabel("Cliente:");
        lCli.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lCli.setPreferredSize(new Dimension(80, 24));
        txtClienteCobrar = new JTextField();
        fCli.add(lCli, BorderLayout.WEST);
        fCli.add(txtClienteCobrar, BorderLayout.CENTER);

        JPanel fMon = new JPanel(new BorderLayout(6, 0));
        fMon.setOpaque(false);
        JLabel lMon = new JLabel("Monto S/:");
        lMon.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lMon.setPreferredSize(new Dimension(80, 24));
        txtMontoCobrar = new JTextField();
        fMon.add(lMon, BorderLayout.WEST);
        fMon.add(txtMontoCobrar, BorderLayout.CENTER);

        gridCampos.add(fCli);
        gridCampos.add(fMon);

        // Fila 2: Concepto y Vence
        JPanel fCon = new JPanel(new BorderLayout(6, 0));
        fCon.setOpaque(false);
        JLabel lCon = new JLabel("Concepto:");
        lCon.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lCon.setPreferredSize(new Dimension(80, 24));
        txtConceptoCobrar = new JTextField();
        fCon.add(lCon, BorderLayout.WEST);
        fCon.add(txtConceptoCobrar, BorderLayout.CENTER);

        JPanel fVen = new JPanel(new BorderLayout(6, 0));
        fVen.setOpaque(false);
        JLabel lVen = new JLabel("Vence:");
        lVen.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lVen.setPreferredSize(new Dimension(80, 24));
        txtVenceCobrar = new JTextField(LocalDate.now().plusDays(15).format(FORMATO_FECHA));
        fVen.add(lVen, BorderLayout.WEST);
        fVen.add(txtVenceCobrar, BorderLayout.CENTER);

        gridCampos.add(fCon);
        gridCampos.add(fVen);

        // Fila 3: Espacio vacío y Estado
        JPanel fEspacio = new JPanel();
        fEspacio.setOpaque(false);

        JPanel fEst = new JPanel(new BorderLayout(6, 0));
        fEst.setOpaque(false);
        JLabel lEst = new JLabel("Estado:");
        lEst.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lEst.setPreferredSize(new Dimension(80, 24));
        cboEstadoCobrar = new JComboBox<>(new String[]{"Pendiente", "Pagado", "Vencido", "Parcial"});
        fEst.add(lEst, BorderLayout.WEST);
        fEst.add(cboEstadoCobrar, BorderLayout.CENTER);

        gridCampos.add(fEspacio);
        gridCampos.add(fEst);

        boxForm.add(gridCampos, BorderLayout.CENTER);

        // Botones exactos del wireframe: [Agregar] [Modificar] [Eliminar] [Limpiar]
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 6));
        panelBotones.setOpaque(false);

        JButton btnAgregar = Ui.boton("Agregar", true);
        btnAgregar.setPreferredSize(new Dimension(105, 32));
        btnAgregar.addActionListener(e -> agregarCuentaCobrar());

        JButton btnModificar = Ui.boton("Modificar", false);
        btnModificar.setPreferredSize(new Dimension(105, 32));
        btnModificar.addActionListener(e -> modificarCuentaCobrar());

        JButton btnEliminar = Ui.boton("Eliminar", false);
        btnEliminar.setPreferredSize(new Dimension(105, 32));
        btnEliminar.setForeground(COLOR_ROJO);
        btnEliminar.addActionListener(e -> eliminarCuentaCobrar());

        JButton btnLimpiar = Ui.boton("Limpiar", false);
        btnLimpiar.setPreferredSize(new Dimension(105, 32));
        btnLimpiar.addActionListener(e -> limpiarFormCobrar());

        JButton btnAbono = Ui.boton("Registrar Cobro / Abono", true);
        btnAbono.setBackground(COLOR_VERDE);
        btnAbono.setPreferredSize(new Dimension(180, 32));
        btnAbono.addActionListener(e -> abrirDialogoAbonoCobrar());

        panelBotones.add(btnAgregar);
        panelBotones.add(btnModificar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnLimpiar);
        panelBotones.add(btnAbono);

        boxForm.add(panelBotones, BorderLayout.SOUTH);
        p.add(boxForm, BorderLayout.NORTH);

        // Tabla del Wireframe: Cliente | Concepto | Monto | Vencimiento | Estado
        String[] cols = {"Cliente", "Concepto", "Monto", "Vencimiento", "Estado"};
        modeloCobrar = new DefaultTableModel(cols, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        tablaCobrar = new JTable(modeloCobrar);
        Ui.formatearTabla(tablaCobrar, new int[]{3, 4}, new int[]{2});

        tablaCobrar.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                seleccionarFilaCobrar();
            }
        });

        // Renderer de colores para la columna Estado
        tablaCobrar.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            private static final long serialVersionUID = 1L;
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.CENTER);
                setFont(new Font("Segoe UI", Font.BOLD, 11));
                String est = value != null ? value.toString() : "";
                if ("Pagado".equalsIgnoreCase(est)) {
                    setForeground(new Color(22, 101, 52));
                } else if ("Vencido".equalsIgnoreCase(est)) {
                    setForeground(new Color(185, 28, 28));
                } else if ("Parcial".equalsIgnoreCase(est)) {
                    setForeground(new Color(3, 105, 161));
                } else {
                    setForeground(new Color(180, 83, 9));
                }
                return c;
            }
        });

        JScrollPane scrollTabla = new JScrollPane(tablaCobrar);
        scrollTabla.setBorder(BorderFactory.createLineBorder(COLOR_BORDE, 1));
        p.add(scrollTabla, BorderLayout.CENTER);

        // Footer exacto del Wireframe: "Total por cobrar: S/ 0.00"
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 6));
        footer.setBackground(Color.WHITE);
        footer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDE, 1),
                new EmptyBorder(6, 12, 6, 12)
        ));

        lblTotalPorCobrar = new JLabel("Total por cobrar: S/ 0.00");
        lblTotalPorCobrar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTotalPorCobrar.setForeground(COLOR_PRIMARIO);
        footer.add(lblTotalPorCobrar);

        p.add(footer, BorderLayout.SOUTH);

        return p;
    }

    // =========================================================================
    // PANEL 2: CUENTAS POR PAGAR (Fiel al wireframe CUENTAS POR PAGAR.pdf)
    // =========================================================================
    private JPanel crearPanelCuentasPorPagar() {
        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Formulario del Wireframe:
        // Proveedor: txtProveedor | Monto: txtMonto
        // Concepto: txtConcepto   | Vence: txtVence
        //                           Estado: cboEstado: Pendiente
        JPanel boxForm = new JPanel(new BorderLayout(0, 8));
        boxForm.setBackground(Color.WHITE);
        boxForm.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDE, 1),
                new EmptyBorder(10, 16, 12, 16)
        ));

        JPanel gridCampos = new JPanel(new GridLayout(3, 2, 18, 8));
        gridCampos.setOpaque(false);

        // Fila 1: Proveedor y Monto
        JPanel fProv = new JPanel(new BorderLayout(6, 0));
        fProv.setOpaque(false);
        JLabel lProv = new JLabel("Proveedor:");
        lProv.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lProv.setPreferredSize(new Dimension(80, 24));
        txtProveedorPagar = new JTextField();
        fProv.add(lProv, BorderLayout.WEST);
        fProv.add(txtProveedorPagar, BorderLayout.CENTER);

        JPanel fMon = new JPanel(new BorderLayout(6, 0));
        fMon.setOpaque(false);
        JLabel lMon = new JLabel("Monto S/:");
        lMon.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lMon.setPreferredSize(new Dimension(80, 24));
        txtMontoPagar = new JTextField();
        fMon.add(lMon, BorderLayout.WEST);
        fMon.add(txtMontoPagar, BorderLayout.CENTER);

        gridCampos.add(fProv);
        gridCampos.add(fMon);

        // Fila 2: Concepto y Vence
        JPanel fCon = new JPanel(new BorderLayout(6, 0));
        fCon.setOpaque(false);
        JLabel lCon = new JLabel("Concepto:");
        lCon.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lCon.setPreferredSize(new Dimension(80, 24));
        txtConceptoPagar = new JTextField();
        fCon.add(lCon, BorderLayout.WEST);
        fCon.add(txtConceptoPagar, BorderLayout.CENTER);

        JPanel fVen = new JPanel(new BorderLayout(6, 0));
        fVen.setOpaque(false);
        JLabel lVen = new JLabel("Vence:");
        lVen.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lVen.setPreferredSize(new Dimension(80, 24));
        txtVencePagar = new JTextField(LocalDate.now().plusDays(20).format(FORMATO_FECHA));
        fVen.add(lVen, BorderLayout.WEST);
        fVen.add(txtVencePagar, BorderLayout.CENTER);

        gridCampos.add(fCon);
        gridCampos.add(fVen);

        // Fila 3: Espacio vacío y Estado
        JPanel fEspacio = new JPanel();
        fEspacio.setOpaque(false);

        JPanel fEst = new JPanel(new BorderLayout(6, 0));
        fEst.setOpaque(false);
        JLabel lEst = new JLabel("Estado:");
        lEst.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lEst.setPreferredSize(new Dimension(80, 24));
        cboEstadoPagar = new JComboBox<>(new String[]{"Pendiente", "Pagado", "Vencido", "Parcial"});
        fEst.add(lEst, BorderLayout.WEST);
        fEst.add(cboEstadoPagar, BorderLayout.CENTER);

        gridCampos.add(fEspacio);
        gridCampos.add(fEst);

        boxForm.add(gridCampos, BorderLayout.CENTER);

        // Botones exactos del wireframe: [Agregar] [Modificar] [Eliminar] [Limpiar]
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 6));
        panelBotones.setOpaque(false);

        JButton btnAgregar = Ui.boton("Agregar", true);
        btnAgregar.setPreferredSize(new Dimension(105, 32));
        btnAgregar.addActionListener(e -> agregarCuentaPagar());

        JButton btnModificar = Ui.boton("Modificar", false);
        btnModificar.setPreferredSize(new Dimension(105, 32));
        btnModificar.addActionListener(e -> modificarCuentaPagar());

        JButton btnEliminar = Ui.boton("Eliminar", false);
        btnEliminar.setPreferredSize(new Dimension(105, 32));
        btnEliminar.setForeground(COLOR_ROJO);
        btnEliminar.addActionListener(e -> eliminarCuentaPagar());

        JButton btnLimpiar = Ui.boton("Limpiar", false);
        btnLimpiar.setPreferredSize(new Dimension(105, 32));
        btnLimpiar.addActionListener(e -> limpiarFormPagar());

        JButton btnPagar = Ui.boton("Registrar Pago a Proveedor", true);
        btnPagar.setBackground(new Color(2, 132, 199));
        btnPagar.setPreferredSize(new Dimension(195, 32));
        btnPagar.addActionListener(e -> abrirDialogoPagoPagar());

        panelBotones.add(btnAgregar);
        panelBotones.add(btnModificar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnLimpiar);
        panelBotones.add(btnPagar);

        boxForm.add(panelBotones, BorderLayout.SOUTH);
        p.add(boxForm, BorderLayout.NORTH);

        // Tabla del Wireframe: Proveedor | Concepto | Monto | Vencimiento | Estado
        String[] cols = {"Proveedor", "Concepto", "Monto", "Vencimiento", "Estado"};
        modeloPagar = new DefaultTableModel(cols, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        tablaPagar = new JTable(modeloPagar);
        Ui.formatearTabla(tablaPagar, new int[]{3, 4}, new int[]{2});

        tablaPagar.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                seleccionarFilaPagar();
            }
        });

        tablaPagar.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            private static final long serialVersionUID = 1L;
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.CENTER);
                setFont(new Font("Segoe UI", Font.BOLD, 11));
                String est = value != null ? value.toString() : "";
                if ("Pagado".equalsIgnoreCase(est)) {
                    setForeground(new Color(22, 101, 52));
                } else if ("Vencido".equalsIgnoreCase(est)) {
                    setForeground(new Color(185, 28, 28));
                } else if ("Parcial".equalsIgnoreCase(est)) {
                    setForeground(new Color(3, 105, 161));
                } else {
                    setForeground(new Color(180, 83, 9));
                }
                return c;
            }
        });

        JScrollPane scrollTabla = new JScrollPane(tablaPagar);
        scrollTabla.setBorder(BorderFactory.createLineBorder(COLOR_BORDE, 1));
        p.add(scrollTabla, BorderLayout.CENTER);

        // Footer exacto del Wireframe: "Total pendiente: S/ 0.00"
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 6));
        footer.setBackground(Color.WHITE);
        footer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDE, 1),
                new EmptyBorder(6, 12, 6, 12)
        ));

        lblTotalPorPagar = new JLabel("Total pendiente: S/ 0.00");
        lblTotalPorPagar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTotalPorPagar.setForeground(COLOR_ROJO);
        footer.add(lblTotalPorPagar);

        p.add(footer, BorderLayout.SOUTH);

        return p;
    }

    // =========================================================================
    // LÓGICA DE NEGOCIO: CARGA Y CRUD CUENTAS POR COBRAR
    // =========================================================================
    public void recargarDatos() {
        // 1. Recargar Cuentas por Cobrar
        modeloCobrar.setRowCount(0);
        List<CuentaPorCobrar> listaCobrar = repo.getCuentasPorCobrar();
        double totalCobrarPendiente = 0.0;
        int vencidas = 0;

        for (CuentaPorCobrar c : listaCobrar) {
            modeloCobrar.addRow(new Object[]{
                    c.getCliente(),
                    c.getConcepto(),
                    String.format("%.2f", c.getMonto()),
                    c.getFechaVenceTexto(),
                    c.getEstado()
            });
            if (!"Pagado".equalsIgnoreCase(c.getEstado())) {
                totalCobrarPendiente += c.getSaldo();
            }
            if ("Vencido".equalsIgnoreCase(c.getEstado())) {
                vencidas++;
            }
        }
        lblTotalPorCobrar.setText("Total por cobrar: S/ " + String.format("%.2f", totalCobrarPendiente));

        // 2. Recargar Cuentas por Pagar
        modeloPagar.setRowCount(0);
        List<CuentaPorPagar> listaPagar = repo.getCuentasPorPagar();
        double totalPagarPendiente = 0.0;

        for (CuentaPorPagar p : listaPagar) {
            modeloPagar.addRow(new Object[]{
                    p.getProveedor(),
                    p.getConcepto(),
                    String.format("%.2f", p.getMonto()),
                    p.getFechaVenceTexto(),
                    p.getEstado()
            });
            if (!"Pagado".equalsIgnoreCase(p.getEstado())) {
                totalPagarPendiente += p.getSaldo();
            }
            if ("Vencido".equalsIgnoreCase(p.getEstado())) {
                vencidas++;
            }
        }
        lblTotalPorPagar.setText("Total pendiente: S/ " + String.format("%.2f", totalPagarPendiente));

        // 3. Actualizar KPIs superiores
        lblKpiCobrar.setText("S/ " + String.format("%.2f", totalCobrarPendiente));
        lblKpiPagar.setText("S/ " + String.format("%.2f", totalPagarPendiente));
        lblKpiVencidas.setText(vencidas + " vencidas");
        double balance = totalCobrarPendiente - totalPagarPendiente;
        lblKpiBalance.setText((balance >= 0 ? "+ S/ " : "- S/ ") + String.format("%.2f", Math.abs(balance)));
        lblKpiBalance.setForeground(balance >= 0 ? COLOR_VERDE : COLOR_ROJO);
    }

    private void seleccionarFilaCobrar() {
        int r = tablaCobrar.getSelectedRow();
        if (r >= 0 && r < repo.getCuentasPorCobrar().size()) {
            CuentaPorCobrar c = repo.getCuentasPorCobrar().get(r);
            idSeleccionadoCobrar = c.getIdCuenta();
            txtClienteCobrar.setText(c.getCliente());
            txtConceptoCobrar.setText(c.getConcepto());
            txtMontoCobrar.setText(String.format("%.2f", c.getMonto()));
            txtVenceCobrar.setText(c.getFechaVenceTexto());
            cboEstadoCobrar.setSelectedItem(c.getEstado());
        }
    }

    private void agregarCuentaCobrar() {
        try {
            String cli = txtClienteCobrar.getText().trim();
            String con = txtConceptoCobrar.getText().trim();
            String monStr = txtMontoCobrar.getText().trim();
            String venStr = txtVenceCobrar.getText().trim();
            String est = (String) cboEstadoCobrar.getSelectedItem();

            if (cli.isEmpty() || con.isEmpty() || monStr.isEmpty() || venStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Complete todos los campos del formulario.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            double mon = Double.parseDouble(monStr);
            LocalDate ven = LocalDate.parse(venStr, FORMATO_FECHA);

            CuentaPorCobrar nueva = new CuentaPorCobrar(null, cli, con, mon, ven, est);
            repo.guardarCuentaPorCobrar(nueva);
            recargarDatos();
            limpiarFormCobrar();
            JOptionPane.showMessageDialog(this, "Cuenta por cobrar registrada correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Datos inválidos: verifique formato de monto o fecha (dd/MM/yyyy).", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void modificarCuentaCobrar() {
        if (idSeleccionadoCobrar == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un registro de la tabla para modificar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            String cli = txtClienteCobrar.getText().trim();
            String con = txtConceptoCobrar.getText().trim();
            double mon = Double.parseDouble(txtMontoCobrar.getText().trim());
            LocalDate ven = LocalDate.parse(txtVenceCobrar.getText().trim(), FORMATO_FECHA);
            String est = (String) cboEstadoCobrar.getSelectedItem();

            for (CuentaPorCobrar c : repo.getCuentasPorCobrar()) {
                if (c.getIdCuenta().equalsIgnoreCase(idSeleccionadoCobrar)) {
                    c.setCliente(cli);
                    c.setConcepto(con);
                    c.setMonto(mon);
                    c.setFechaVence(ven);
                    c.setEstado(est);
                    break;
                }
            }
            recargarDatos();
            JOptionPane.showMessageDialog(this, "Cuenta modificada correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al modificar: verifique los datos ingresados.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarCuentaCobrar() {
        if (idSeleccionadoCobrar == null) {
            JOptionPane.showMessageDialog(this, "Seleccione una cuenta para eliminar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int r = JOptionPane.showConfirmDialog(this, "¿Está seguro de eliminar esta cuenta por cobrar?", "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);
        if (r == JOptionPane.YES_OPTION) {
            repo.eliminarCuentaPorCobrar(idSeleccionadoCobrar);
            recargarDatos();
            limpiarFormCobrar();
        }
    }

    private void limpiarFormCobrar() {
        idSeleccionadoCobrar = null;
        txtClienteCobrar.setText("");
        txtConceptoCobrar.setText("");
        txtMontoCobrar.setText("");
        txtVenceCobrar.setText(LocalDate.now().plusDays(15).format(FORMATO_FECHA));
        cboEstadoCobrar.setSelectedIndex(0);
        tablaCobrar.clearSelection();
    }

    private void abrirDialogoAbonoCobrar() {
        int r = tablaCobrar.getSelectedRow();
        if (r < 0 || r >= repo.getCuentasPorCobrar().size()) {
            JOptionPane.showMessageDialog(this, "Seleccione la cuenta del cliente a la que desea registrar el cobro.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        CuentaPorCobrar cuenta = repo.getCuentasPorCobrar().get(r);
        if ("Pagado".equalsIgnoreCase(cuenta.getEstado())) {
            JOptionPane.showMessageDialog(this, "Esta cuenta ya se encuentra totalmente saldada.", "Información", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String input = JOptionPane.showInputDialog(this,
                "<html><body><b>Registrar Cobro a Cliente:</b> " + cuenta.getCliente() + "<br>" +
                "<b>Concepto:</b> " + cuenta.getConcepto() + "<br>" +
                "<b>Monto Total:</b> S/ " + String.format("%.2f", cuenta.getMonto()) + "<br>" +
                "<b>Saldo Pendiente:</b> S/ " + String.format("%.2f", cuenta.getSaldo()) + "<br><br>" +
                "Ingrese el importe a cobrar (S/):</body></html>",
                String.format("%.2f", cuenta.getSaldo()));

        if (input != null && !input.trim().isEmpty()) {
            try {
                double abono = Double.parseDouble(input.trim());
                if (abono <= 0) return;
                repo.registrarAbonoCuentaPorCobrar(cuenta.getIdCuenta(), abono);

                // Opcional: registrar automáticamente en caja chica
                repo.guardarMovimientoCajaChica(new happypets.model.MovimientoCajaChica(
                        null, LocalDate.now(), "Ingreso",
                        "Cobro CXC " + cuenta.getIdCuenta() + " - " + cuenta.getCliente(),
                        abono, "Joanna Corrales", "Recibo Cobro #0" + cuenta.getIdCuenta()
                ));

                recargarDatos();
                JOptionPane.showMessageDialog(this, "Abono de S/ " + String.format("%.2f", abono) + " registrado con éxito.", "Cobro Registrado", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Monto inválido.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // =========================================================================
    // LÓGICA DE NEGOCIO: CARGA Y CRUD CUENTAS POR PAGAR
    // =========================================================================
    private void seleccionarFilaPagar() {
        int r = tablaPagar.getSelectedRow();
        if (r >= 0 && r < repo.getCuentasPorPagar().size()) {
            CuentaPorPagar p = repo.getCuentasPorPagar().get(r);
            idSeleccionadoPagar = p.getIdCuenta();
            txtProveedorPagar.setText(p.getProveedor());
            txtConceptoPagar.setText(p.getConcepto());
            txtMontoPagar.setText(String.format("%.2f", p.getMonto()));
            txtVencePagar.setText(p.getFechaVenceTexto());
            cboEstadoPagar.setSelectedItem(p.getEstado());
        }
    }

    private void agregarCuentaPagar() {
        try {
            String prov = txtProveedorPagar.getText().trim();
            String con = txtConceptoPagar.getText().trim();
            String monStr = txtMontoPagar.getText().trim();
            String venStr = txtVencePagar.getText().trim();
            String est = (String) cboEstadoPagar.getSelectedItem();

            if (prov.isEmpty() || con.isEmpty() || monStr.isEmpty() || venStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Complete todos los campos del formulario.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            double mon = Double.parseDouble(monStr);
            LocalDate ven = LocalDate.parse(venStr, FORMATO_FECHA);

            CuentaPorPagar nueva = new CuentaPorPagar(null, prov, con, mon, ven, est);
            repo.guardarCuentaPorPagar(nueva);
            recargarDatos();
            limpiarFormPagar();
            JOptionPane.showMessageDialog(this, "Cuenta por pagar registrada correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Datos inválidos: verifique formato de monto o fecha (dd/MM/yyyy).", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void modificarCuentaPagar() {
        if (idSeleccionadoPagar == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un registro de la tabla para modificar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            String prov = txtProveedorPagar.getText().trim();
            String con = txtConceptoPagar.getText().trim();
            double mon = Double.parseDouble(txtMontoPagar.getText().trim());
            LocalDate ven = LocalDate.parse(txtVencePagar.getText().trim(), FORMATO_FECHA);
            String est = (String) cboEstadoPagar.getSelectedItem();

            for (CuentaPorPagar p : repo.getCuentasPorPagar()) {
                if (p.getIdCuenta().equalsIgnoreCase(idSeleccionadoPagar)) {
                    p.setProveedor(prov);
                    p.setConcepto(con);
                    p.setMonto(mon);
                    p.setFechaVence(ven);
                    p.setEstado(est);
                    break;
                }
            }
            recargarDatos();
            JOptionPane.showMessageDialog(this, "Cuenta modificada correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al modificar: verifique los datos ingresados.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarCuentaPagar() {
        if (idSeleccionadoPagar == null) {
            JOptionPane.showMessageDialog(this, "Seleccione una cuenta para eliminar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int r = JOptionPane.showConfirmDialog(this, "¿Está seguro de eliminar esta cuenta por pagar?", "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);
        if (r == JOptionPane.YES_OPTION) {
            repo.eliminarCuentaPorPagar(idSeleccionadoPagar);
            recargarDatos();
            limpiarFormPagar();
        }
    }

    private void limpiarFormPagar() {
        idSeleccionadoPagar = null;
        txtProveedorPagar.setText("");
        txtConceptoPagar.setText("");
        txtMontoPagar.setText("");
        txtVencePagar.setText(LocalDate.now().plusDays(20).format(FORMATO_FECHA));
        cboEstadoPagar.setSelectedIndex(0);
        tablaPagar.clearSelection();
    }

    private void abrirDialogoPagoPagar() {
        int r = tablaPagar.getSelectedRow();
        if (r < 0 || r >= repo.getCuentasPorPagar().size()) {
            JOptionPane.showMessageDialog(this, "Seleccione la cuenta por pagar a liquidar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        CuentaPorPagar cuenta = repo.getCuentasPorPagar().get(r);
        if ("Pagado".equalsIgnoreCase(cuenta.getEstado())) {
            JOptionPane.showMessageDialog(this, "Esta cuenta ya se encuentra totalmente cancelada.", "Información", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String input = JOptionPane.showInputDialog(this,
                "<html><body><b>Registrar Pago a Proveedor:</b> " + cuenta.getProveedor() + "<br>" +
                "<b>Concepto:</b> " + cuenta.getConcepto() + "<br>" +
                "<b>Monto Total:</b> S/ " + String.format("%.2f", cuenta.getMonto()) + "<br>" +
                "<b>Saldo Pendiente:</b> S/ " + String.format("%.2f", cuenta.getSaldo()) + "<br><br>" +
                "Ingrese el importe a pagar (S/):</body></html>",
                String.format("%.2f", cuenta.getSaldo()));

        if (input != null && !input.trim().isEmpty()) {
            try {
                double pago = Double.parseDouble(input.trim());
                if (pago <= 0) return;
                repo.registrarPagoCuentaPorPagar(cuenta.getIdCuenta(), pago);

                // Opcional: registrar en egresos operativos
                repo.guardarEgresoOperativo(new happypets.model.EgresoOperativo(
                        null, LocalDate.now(), "Suministros y Mercadería",
                        "Pago a proveedor " + cuenta.getProveedor() + " (" + cuenta.getConcepto() + ")",
                        cuenta.getProveedor(), pago, "Transferencia", "OP-BCP-9812", "Pagado"
                ));

                recargarDatos();
                JOptionPane.showMessageDialog(this, "Pago de S/ " + String.format("%.2f", pago) + " registrado con éxito.", "Pago Registrado", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Monto inválido.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
