package happypets.modulos.modulo6;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import happypets.data.RepositorioVeterinaria;
import happypets.model.CuentaPorPagar;
import happypets.model.ProveedorFarmacia;
import happypets.ui.Iconos;
import happypets.ui.Ui;

/**
 * Diálogo modal desacoplado en dos pestañas para registro y edición de Cuentas por Pagar (Obligaciones con Proveedores).
 */
public class FormularioCuentaPagarDialog extends JDialog {
    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final RepositorioVeterinaria repo = RepositorioVeterinaria.getInstancia();
    private final CuentaPorPagar existente;
    private boolean confirmado = false;

    // Pestaña 1: Proveedor & Factura
    private JTextField txtIdCuenta;
    private JComboBox<String> cbProveedor;
    private JTextField txtRuc;
    private JTextField txtConcepto;
    private JTextField txtFacturaProveedor;

    // Pestaña 2: Importe & Plazos
    private JTextField txtMontoTotal;
    private JTextField txtMontoPagado;
    private JLabel lblSaldoCalculado;
    private JTextField txtFechaRegistro;
    private JTextField txtFechaVence;
    private JComboBox<String> cbEstado;

    public FormularioCuentaPagarDialog(Frame parent) {
        this(parent, null);
    }

    public FormularioCuentaPagarDialog(Frame parent, CuentaPorPagar existente) {
        super(parent, existente == null ? "Nueva Cuenta por Pagar (Obligación con Proveedor)" : "Modificación de Cuenta por Pagar", true);
        this.existente = existente;
        inicializarUI();
    }

    private void inicializarUI() {
        setSize(620, 640);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(248, 250, 252));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Ui.BORDE_SUAVE),
                new EmptyBorder(14, 20, 14, 20)
        ));

        JPanel titulos = new JPanel();
        titulos.setOpaque(false);
        titulos.setLayout(new BoxLayout(titulos, BoxLayout.Y_AXIS));

        JLabel lblTit = new JLabel(existente == null ? "Registro de Cuenta por Pagar" : "Modificación de Pasivo Comercial");
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTit.setForeground(Ui.TEXTO_TITULO);

        JLabel lblSub = new JLabel("Control de compras a crédito, facturas de laboratorios y proveedores de insumos");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(Ui.TEXTO_MUTED);

        titulos.add(lblTit);
        titulos.add(Box.createVerticalStrut(2));
        titulos.add(lblSub);
        header.add(titulos, BorderLayout.CENTER);

        JLabel badgeIcono = new JLabel(Iconos.crearIconoCamionProveedor(26, Ui.TURQUESA_OSCURO));
        header.add(badgeIcono, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // Pestañas
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabs.setBackground(Color.WHITE);

        tabs.addTab("1. Proveedor & Factura Comercial", Iconos.crearIconoCamionProveedor(15, Ui.TURQUESA), crearPestanaProveedor());
        tabs.addTab("2. Importe, Términos & Plazos", Iconos.crearIconoMonedas(15, Ui.TURQUESA_OSCURO), crearPestanaImporte());

        add(tabs, BorderLayout.CENTER);

        // Footer
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        footer.setBackground(Color.WHITE);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Ui.BORDE_SUAVE));

        JButton btnCancelar = Ui.boton("Cancelar", false);
        btnCancelar.addActionListener(e -> dispose());

        JButton btnGuardar = Ui.boton(existente == null ? "Registrar Pasivo" : "Actualizar Pasivo", true);
        btnGuardar.addActionListener(e -> procesarGuardado());

        footer.add(btnCancelar);
        footer.add(btnGuardar);
        add(footer, BorderLayout.SOUTH);

        if (existente != null) {
            cargarDatosExistente();
        }
    }

    private JPanel crearPestanaProveedor() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(16, 22, 16, 22));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // ID Cuenta
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.5;
        p.add(crearEtiquetaCampo("ID Cuenta *"), gbc);

        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.5;
        p.add(crearEtiquetaCampo("RUC Proveedor *"), gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        txtIdCuenta = new JTextField(existente != null ? existente.getIdCuenta() : "CXP-" + String.format("%03d", repo.getCuentasPorPagar().size() + 1));
        txtIdCuenta.setFont(new Font("Segoe UI", Font.BOLD, 12));
        txtIdCuenta.setEditable(false);
        p.add(txtIdCuenta, gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        txtRuc = new JTextField("20501234567");
        txtRuc.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        p.add(txtRuc, gbc);

        // Proveedor
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        p.add(crearEtiquetaCampo("Proveedor / Laboratorio Acreedor *"), gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        cbProveedor = new JComboBox<>();
        cbProveedor.setEditable(true);
        cbProveedor.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        for (ProveedorFarmacia prov : repo.getProveedoresFarmacia()) {
            cbProveedor.addItem(prov.getRazonSocial());
        }
        cbProveedor.addActionListener(e -> {
            String provStr = (String) cbProveedor.getSelectedItem();
            if (provStr == null) return;
            for (ProveedorFarmacia prov : repo.getProveedoresFarmacia()) {
                if (prov.getRazonSocial().equalsIgnoreCase(provStr)) {
                    txtRuc.setText(prov.getRuc());
                    return;
                }
            }
        });
        p.add(cbProveedor, gbc);

        // Factura Comercial Proveedor
        gbc.gridx = 0; gbc.gridy = 4;
        p.add(crearEtiquetaCampo("N° Factura Comercial del Proveedor *"), gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        txtFacturaProveedor = new JTextField("F001-0008892");
        txtFacturaProveedor.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        p.add(txtFacturaProveedor, gbc);

        // Concepto del Gasto
        gbc.gridx = 0; gbc.gridy = 6;
        p.add(crearEtiquetaCampo("Concepto / Insumos Adquiridos a Crédito *"), gbc);

        gbc.gridx = 0; gbc.gridy = 7;
        txtConcepto = new JTextField("Adquisición de 40 viales de Vacuna Séxtuple");
        txtConcepto.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        p.add(txtConcepto, gbc);

        return p;
    }

    private JPanel crearPestanaImporte() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(16, 22, 16, 22));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Monto Total & Monto Pagado
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.5;
        p.add(crearEtiquetaCampo("Monto Total Facturado S/. *"), gbc);

        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.5;
        p.add(crearEtiquetaCampo("Monto Abonado a la Fecha S/."), gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        txtMontoTotal = new JTextField("1200.00");
        txtMontoTotal.setFont(new Font("Segoe UI", Font.BOLD, 12));
        txtMontoTotal.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { recalcularSaldo(); }
            public void removeUpdate(DocumentEvent e) { recalcularSaldo(); }
            public void changedUpdate(DocumentEvent e) { recalcularSaldo(); }
        });
        p.add(txtMontoTotal, gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        txtMontoPagado = new JTextField("400.00");
        txtMontoPagado.setFont(new Font("Segoe UI", Font.BOLD, 12));
        txtMontoPagado.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { recalcularSaldo(); }
            public void removeUpdate(DocumentEvent e) { recalcularSaldo(); }
            public void changedUpdate(DocumentEvent e) { recalcularSaldo(); }
        });
        p.add(txtMontoPagado, gbc);

        // Card de Saldo Resultante
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        JPanel panelSaldo = new JPanel(new GridLayout(1, 2, 10, 0)) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(254, 242, 242));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(new Color(254, 202, 202));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                g2.dispose();
            }
        };
        panelSaldo.setOpaque(false);
        panelSaldo.setBorder(new EmptyBorder(10, 14, 10, 14));

        JLabel lblTitSaldo = new JLabel("Saldo Pendiente por Pagar al Proveedor:");
        lblTitSaldo.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTitSaldo.setForeground(Ui.COLOR_PELIGRO);

        lblSaldoCalculado = new JLabel("S/. 800.00", SwingConstants.RIGHT);
        lblSaldoCalculado.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblSaldoCalculado.setForeground(Ui.COLOR_PELIGRO);

        panelSaldo.add(lblTitSaldo);
        panelSaldo.add(lblSaldoCalculado);
        p.add(panelSaldo, gbc);

        // Fechas de Emisión y Vencimiento
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 1;
        p.add(crearEtiquetaCampo("Fecha de Emisión (dd/MM/yyyy) *"), gbc);

        gbc.gridx = 1; gbc.gridy = 3;
        p.add(crearEtiquetaCampo("Fecha de Vencimiento (dd/MM/yyyy) *"), gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        txtFechaRegistro = new JTextField(LocalDate.now().format(FORMATO_FECHA));
        txtFechaRegistro.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        p.add(txtFechaRegistro, gbc);

        gbc.gridx = 1; gbc.gridy = 4;
        txtFechaVence = new JTextField(LocalDate.now().plusDays(30).format(FORMATO_FECHA));
        txtFechaVence.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        p.add(txtFechaVence, gbc);

        // Estado
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        p.add(crearEtiquetaCampo("Estado Inicial de la Obligación"), gbc);

        gbc.gridx = 0; gbc.gridy = 6;
        cbEstado = new JComboBox<>(new String[]{"Pendiente", "Parcial", "Pagado", "Vencido"});
        cbEstado.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        p.add(cbEstado, gbc);

        return p;
    }

    private JLabel crearEtiquetaCampo(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(new Font("Segoe UI", Font.BOLD, 11));
        l.setForeground(Ui.TEXTO_MUTED);
        return l;
    }

    private void recalcularSaldo() {
        if (txtMontoTotal == null || txtMontoPagado == null || lblSaldoCalculado == null) return;
        try {
            double total = Double.parseDouble(txtMontoTotal.getText().trim());
            double pagado = Double.parseDouble(txtMontoPagado.getText().trim());
            double saldo = Math.max(0.0, total - pagado);
            lblSaldoCalculado.setText(String.format("S/. %.2f", saldo));
        } catch (NumberFormatException ignored) {
            lblSaldoCalculado.setText("S/. 0.00");
        }
    }

    private void cargarDatosExistente() {
        txtIdCuenta.setText(existente.getIdCuenta());
        cbProveedor.setSelectedItem(existente.getProveedor());
        txtRuc.setText(existente.getRucProveedor());
        txtFacturaProveedor.setText(existente.getFacturaProveedor());
        txtConcepto.setText(existente.getConcepto());

        txtMontoTotal.setText(String.valueOf(existente.getMonto()));
        txtMontoPagado.setText(String.valueOf(existente.getMontoPagado()));
        txtFechaRegistro.setText(existente.getFechaRegistro() != null ? existente.getFechaRegistro().format(FORMATO_FECHA) : "");
        txtFechaVence.setText(existente.getFechaVence() != null ? existente.getFechaVence().format(FORMATO_FECHA) : "");
        cbEstado.setSelectedItem(existente.getEstado());
        recalcularSaldo();
    }

    private void procesarGuardado() {
        String id = txtIdCuenta.getText().trim();
        String proveedor = cbProveedor.getSelectedItem() != null ? cbProveedor.getSelectedItem().toString().trim() : "";
        String concepto = txtConcepto.getText().trim();

        if (proveedor.isEmpty() || concepto.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe ingresar el Proveedor y el Concepto de la obligación.", "Campos Requeridos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double total;
        double pagado;
        try {
            total = Double.parseDouble(txtMontoTotal.getText().trim());
            pagado = Double.parseDouble(txtMontoPagado.getText().trim());
            if (total <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Ingrese montos válidos mayores a cero.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        LocalDate fReg, fVence;
        try {
            fReg = LocalDate.parse(txtFechaRegistro.getText().trim(), FORMATO_FECHA);
            fVence = LocalDate.parse(txtFechaVence.getText().trim(), FORMATO_FECHA);
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Formato de fecha inválido. Utilice dd/MM/yyyy.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String estado = (String) cbEstado.getSelectedItem();
        String ruc = txtRuc.getText().trim();
        String fact = txtFacturaProveedor.getText().trim();

        CuentaPorPagar c = new CuentaPorPagar(id, proveedor, concepto, total, pagado, fReg, fVence, estado, ruc, fact);
        repo.guardarCuentaPorPagar(c);
        confirmado = true;
        dispose();
    }

    public boolean isConfirmado() {
        return confirmado;
    }
}
