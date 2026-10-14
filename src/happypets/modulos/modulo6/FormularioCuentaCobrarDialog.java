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
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import happypets.data.RepositorioVeterinaria;
import happypets.model.Cliente;
import happypets.model.CuentaPorCobrar;
import happypets.ui.Iconos;
import happypets.ui.Ui;

/**
 * Diálogo modal desacoplado en dos pestañas para registro y edición de Cuentas por Cobrar (Créditos a Clientes).
 */
public class FormularioCuentaCobrarDialog extends JDialog {
    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final RepositorioVeterinaria repo = RepositorioVeterinaria.getInstancia();
    private final CuentaPorCobrar existente;
    private boolean confirmado = false;

    // Pestaña 1: Deudor & Concepto
    private JTextField txtIdCuenta;
    private JComboBox<String> cbCliente;
    private JTextField txtTelefono;
    private JTextField txtConcepto;
    private JTextField txtComprobanteRef;

    // Pestaña 2: Importe & Plazos
    private JTextField txtMontoTotal;
    private JTextField txtMontoAbonado;
    private JLabel lblSaldoCalculado;
    private JTextField txtFechaRegistro;
    private JTextField txtFechaVence;
    private JComboBox<String> cbEstado;

    /**
     * Constructor de FormularioCuentaCobrarDialog.
     * Inicializa los componentes gráficos, configura layouts, asigna estilos
     * institucionales y carga los datos desde el repositorio central.
     */
    public FormularioCuentaCobrarDialog(Frame parent) {
        this(parent, null);
    }

    /**
     * Constructor de FormularioCuentaCobrarDialog.
     * Inicializa los componentes gráficos, configura layouts, asigna estilos
     * institucionales y carga los datos desde el repositorio central.
     */
    public FormularioCuentaCobrarDialog(Frame parent, CuentaPorCobrar existente) {
        super(parent, existente == null ? "Nueva Cuenta por Cobrar (Crédito a Cliente)" : "Modificación de Cuenta por Cobrar", true);
        this.existente = existente;
        inicializarUI();
    }

    /**
     * Construye y organiza la jerarquía visual de la pantalla en Swing.
     * Ensambla la barra superior de acciones, tarjetas de métricas (KPIs),
     * tabla maestra de datos y el panel lateral de detalle (Live Detail Hub).
     */
    private void inicializarUI() {
        setSize(620, 640);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(248, 250, 252));

        // Header
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Ui.BORDE_SUAVE),
                new EmptyBorder(14, 20, 14, 20)
        ));

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel titulos = new JPanel();
        titulos.setOpaque(false);
        titulos.setLayout(new BoxLayout(titulos, BoxLayout.Y_AXIS));

        JLabel lblTit = new JLabel(existente == null ? "Registro de Cuenta por Cobrar" : "Modificación de Crédito Comercial");
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTit.setForeground(Ui.TEXTO_TITULO);

        JLabel lblSub = new JLabel("Control de cartera de clientes, pagos en cuotas y convenios veterinarios");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(Ui.TEXTO_MUTED);

        titulos.add(lblTit);
        titulos.add(Box.createVerticalStrut(2));
        titulos.add(lblSub);
        header.add(titulos, BorderLayout.CENTER);

        JLabel badgeIcono = new JLabel(Iconos.crearIconoCuentas(26, Ui.TURQUESA));
        header.add(badgeIcono, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // Pestañas
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabs.setBackground(Color.WHITE);

        tabs.addTab("1. Deudor & Concepto Clínico", Iconos.crearIconoUsuario(15, Ui.TURQUESA), crearPestanaDeudor());
        tabs.addTab("2. Importe, Vencimiento & Plazos", Iconos.crearIconoMonedas(15, Ui.TURQUESA_OSCURO), crearPestanaImporte());

        add(tabs, BorderLayout.CENTER);

        // Footer
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        footer.setBackground(Color.WHITE);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Ui.BORDE_SUAVE));

        JButton btnCancelar = Ui.boton("Cancelar", false);
        btnCancelar.addActionListener(e -> dispose());

        JButton btnGuardar = Ui.boton(existente == null ? "Registrar Cuenta" : "Actualizar Cuenta", true);
        btnGuardar.addActionListener(e -> procesarGuardado());

        footer.add(btnCancelar);
        footer.add(btnGuardar);
        add(footer, BorderLayout.SOUTH);

        // Cargar datos si es edición
        if (existente != null) {
            cargarDatosExistente();
        }
    }

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private JPanel crearPestanaDeudor() {
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
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
        p.add(crearEtiquetaCampo("Teléfono de Contacto"), gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        txtIdCuenta = new JTextField(existente != null ? existente.getIdCuenta() : "CXC-" + String.format("%03d", repo.getCuentasPorCobrar().size() + 1));
        txtIdCuenta.setFont(new Font("Segoe UI", Font.BOLD, 12));
        txtIdCuenta.setEditable(false);
        p.add(txtIdCuenta, gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        txtTelefono = new JTextField("+51 987 654 321");
        txtTelefono.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        p.add(txtTelefono, gbc);

        // Cliente
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        p.add(crearEtiquetaCampo("Cliente / Titular del Crédito *"), gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        cbCliente = new JComboBox<>();
        cbCliente.setEditable(true);
        cbCliente.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        for (Cliente c : repo.getClientes()) {
            cbCliente.addItem(c.getNombreCompleto());
        }
        p.add(cbCliente, gbc);

        // Concepto de la deuda
        gbc.gridx = 0; gbc.gridy = 4;
        p.add(crearEtiquetaCampo("Concepto Clínico / Motivo de la Deuda *"), gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        txtConcepto = new JTextField("Cirugía de TPLO y 3 días de hospitalización");
        txtConcepto.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        p.add(txtConcepto, gbc);

        // Comprobante de Referencia
        gbc.gridx = 0; gbc.gridy = 6;
        p.add(crearEtiquetaCampo("Comprobante / N° Orden de Referencia"), gbc);

        gbc.gridx = 0; gbc.gridy = 7;
        txtComprobanteRef = new JTextField("B001-000412");
        txtComprobanteRef.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        p.add(txtComprobanteRef, gbc);

        return p;
    }

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private JPanel crearPestanaImporte() {
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(16, 22, 16, 22));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Monto Total & Monto Abonado
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.5;
        p.add(crearEtiquetaCampo("Monto Total S/. *"), gbc);

        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.5;
        p.add(crearEtiquetaCampo("Monto Inicial Abonado S/."), gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        txtMontoTotal = new JTextField("450.00");
        txtMontoTotal.setFont(new Font("Segoe UI", Font.BOLD, 12));
        txtMontoTotal.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { recalcularSaldo(); }
            public void removeUpdate(DocumentEvent e) { recalcularSaldo(); }
            public void changedUpdate(DocumentEvent e) { recalcularSaldo(); }
        });
        p.add(txtMontoTotal, gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        txtMontoAbonado = new JTextField("150.00");
        txtMontoAbonado.setFont(new Font("Segoe UI", Font.BOLD, 12));
        txtMontoAbonado.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { recalcularSaldo(); }
            public void removeUpdate(DocumentEvent e) { recalcularSaldo(); }
            public void changedUpdate(DocumentEvent e) { recalcularSaldo(); }
        });
        p.add(txtMontoAbonado, gbc);

        // Card de Saldo Resultante
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel panelSaldo = new JPanel(new GridLayout(1, 2, 10, 0)) {
            private static final long serialVersionUID = 1L;
            @Override
            /**
             * Renderizado gráfico personalizado en Java2D.
             * Activa Antialiasing para bordes suaves y realiza el cálculo geométrico preciso.
             */
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

        JLabel lblTitSaldo = new JLabel("Saldo Pendiente por Cobrar:");
        lblTitSaldo.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTitSaldo.setForeground(Ui.COLOR_PELIGRO);

        lblSaldoCalculado = new JLabel("S/. 300.00", SwingConstants.RIGHT);
        lblSaldoCalculado.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblSaldoCalculado.setForeground(Ui.COLOR_PELIGRO);

        panelSaldo.add(lblTitSaldo);
        panelSaldo.add(lblSaldoCalculado);
        p.add(panelSaldo, gbc);

        // Fechas de Registro y Vencimiento
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 1;
        p.add(crearEtiquetaCampo("Fecha de Emisión (dd/MM/yyyy) *"), gbc);

        gbc.gridx = 1; gbc.gridy = 3;
        p.add(crearEtiquetaCampo("Fecha de Vencimiento (dd/MM/yyyy) *"), gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        txtFechaRegistro = new JTextField(LocalDate.now().format(FORMATO_FECHA));
        txtFechaRegistro.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        p.add(txtFechaRegistro, gbc);

        gbc.gridx = 1; gbc.gridy = 4;
        txtFechaVence = new JTextField(LocalDate.now().plusDays(20).format(FORMATO_FECHA));
        txtFechaVence.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        p.add(txtFechaVence, gbc);

        // Estado
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        p.add(crearEtiquetaCampo("Estado Inicial de la Cuenta"), gbc);

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
        if (txtMontoTotal == null || txtMontoAbonado == null || lblSaldoCalculado == null) return;
        try {
            double total = Double.parseDouble(txtMontoTotal.getText().trim());
            double abonado = Double.parseDouble(txtMontoAbonado.getText().trim());
            double saldo = Math.max(0.0, total - abonado);
            lblSaldoCalculado.setText(String.format("S/. %.2f", saldo));
        } catch (NumberFormatException ignored) {
            lblSaldoCalculado.setText("S/. 0.00");
        }
    }

    private void cargarDatosExistente() {
        txtIdCuenta.setText(existente.getIdCuenta());
        cbCliente.setSelectedItem(existente.getCliente());
        txtTelefono.setText(existente.getTelefonoCliente());
        txtConcepto.setText(existente.getConcepto());
        txtComprobanteRef.setText(existente.getComprobanteReferencia());

        txtMontoTotal.setText(String.valueOf(existente.getMonto()));
        txtMontoAbonado.setText(String.valueOf(existente.getMontoPagado()));
        txtFechaRegistro.setText(existente.getFechaRegistro() != null ? existente.getFechaRegistro().format(FORMATO_FECHA) : "");
        txtFechaVence.setText(existente.getFechaVence() != null ? existente.getFechaVence().format(FORMATO_FECHA) : "");
        cbEstado.setSelectedItem(existente.getEstado());
        recalcularSaldo();
    }

    private void procesarGuardado() {
        String id = txtIdCuenta.getText().trim();
        String cliente = cbCliente.getSelectedItem() != null ? cbCliente.getSelectedItem().toString().trim() : "";
        String concepto = txtConcepto.getText().trim();

        if (cliente.isEmpty() || concepto.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe ingresar el Cliente y el Concepto de la deuda.", "Campos Requeridos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double total;
        double abonado;
        try {
            total = Double.parseDouble(txtMontoTotal.getText().trim());
            abonado = Double.parseDouble(txtMontoAbonado.getText().trim());
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
        String tel = txtTelefono.getText().trim();
        String ref = txtComprobanteRef.getText().trim();

        CuentaPorCobrar c = new CuentaPorCobrar(id, cliente, concepto, total, abonado, fReg, fVence, estado, tel, ref);
        repo.guardarCuentaPorCobrar(c);
        confirmado = true;
        dispose();
    }

    public boolean isConfirmado() {
        return confirmado;
    }
}
