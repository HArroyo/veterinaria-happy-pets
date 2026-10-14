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
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import happypets.data.RepositorioVeterinaria;
import happypets.model.MovimientoCajaChica;
import happypets.ui.Iconos;
import happypets.ui.Ui;

/**
 * Diálogo modal desacoplado en dos pestañas para registro y edición de Movimientos de Caja Chica.
 */
public class FormularioMovimientoCajaChicaDialog extends JDialog {
    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final RepositorioVeterinaria repo = RepositorioVeterinaria.getInstancia();
    private final MovimientoCajaChica existente;
    private boolean confirmado = false;

    // Pestaña 1: Operación & Monto
    private JTextField txtIdMovimiento;
    private JTextField txtFecha;
    private JComboBox<String> cbTipo;
    private JTextField txtConcepto;
    private JTextField txtMonto;
    private JLabel lblSaldoProyectado;

    // Pestaña 2: Responsable & Comprobante
    private JTextField txtResponsable;
    private JTextField txtComprobante;
    private JTextArea txtObservaciones;

    /**
     * Constructor de FormularioMovimientoCajaChicaDialog.
     * Inicializa los componentes gráficos, configura layouts, asigna estilos
     * institucionales y carga los datos desde el repositorio central.
     */
    public FormularioMovimientoCajaChicaDialog(Frame parent) {
        this(parent, null);
    }

    /**
     * Constructor de FormularioMovimientoCajaChicaDialog.
     * Inicializa los componentes gráficos, configura layouts, asigna estilos
     * institucionales y carga los datos desde el repositorio central.
     */
    public FormularioMovimientoCajaChicaDialog(Frame parent, MovimientoCajaChica existente) {
        super(parent, existente == null ? "Nuevo Movimiento de Caja Chica" : "Modificación de Movimiento de Caja", true);
        this.existente = existente;
        inicializarUI();
    }

    /**
     * Construye y organiza la jerarquía visual de la pantalla en Swing.
     * Ensambla la barra superior de acciones, tarjetas de métricas (KPIs),
     * tabla maestra de datos y el panel lateral de detalle (Live Detail Hub).
     */
    private void inicializarUI() {
        setSize(600, 620);
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

        JLabel lblTit = new JLabel(existente == null ? "Registro de Caja Chica" : "Modificación de Movimiento");
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTit.setForeground(Ui.TEXTO_TITULO);

        JLabel lblSub = new JLabel("Custodia de fondos en efectivo, reposición de caja y gastos menores de guardia");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(Ui.TEXTO_MUTED);

        titulos.add(lblTit);
        titulos.add(Box.createVerticalStrut(2));
        titulos.add(lblSub);
        header.add(titulos, BorderLayout.CENTER);

        JLabel badgeIcono = new JLabel(Iconos.crearIconoCajaChica(26, Ui.TURQUESA));
        header.add(badgeIcono, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // Pestañas
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabs.setBackground(Color.WHITE);

        tabs.addTab("1. Operación & Monto", Iconos.crearIconoMonedas(15, Ui.TURQUESA), crearPestanaOperacion());
        tabs.addTab("2. Responsable & Comprobante", Iconos.crearIconoFactura(15, Ui.TURQUESA_OSCURO), crearPestanaResponsable());

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

        JButton btnGuardar = Ui.boton(existente == null ? "Registrar Movimiento" : "Actualizar Movimiento", true);
        btnGuardar.addActionListener(e -> procesarGuardado());

        footer.add(btnCancelar);
        footer.add(btnGuardar);
        add(footer, BorderLayout.SOUTH);

        if (existente != null) {
            cargarDatosExistente();
        } else {
            recalcularSaldoProyectado();
        }
    }

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private JPanel crearPestanaOperacion() {
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

        // ID Movimiento & Fecha
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.5;
        p.add(crearEtiquetaCampo("ID Movimiento *"), gbc);

        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.5;
        p.add(crearEtiquetaCampo("Fecha (dd/MM/yyyy) *"), gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        txtIdMovimiento = new JTextField(existente != null ? existente.getIdMovimiento() : "CCH-" + String.format("%03d", repo.getMovimientosCajaChica().size() + 1));
        txtIdMovimiento.setFont(new Font("Segoe UI", Font.BOLD, 12));
        txtIdMovimiento.setEditable(false);
        p.add(txtIdMovimiento, gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        txtFecha = new JTextField(LocalDate.now().format(FORMATO_FECHA));
        txtFecha.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        p.add(txtFecha, gbc);

        // Tipo & Monto
        gbc.gridx = 0; gbc.gridy = 2;
        p.add(crearEtiquetaCampo("Tipo de Movimiento *"), gbc);

        gbc.gridx = 1; gbc.gridy = 2;
        p.add(crearEtiquetaCampo("Monto en Soles S/. *"), gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        cbTipo = new JComboBox<>(new String[]{"Ingreso", "Egreso"});
        cbTipo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cbTipo.addActionListener(e -> recalcularSaldoProyectado());
        p.add(cbTipo, gbc);

        gbc.gridx = 1; gbc.gridy = 3;
        txtMonto = new JTextField("65.00");
        txtMonto.setFont(new Font("Segoe UI", Font.BOLD, 12));
        txtMonto.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { recalcularSaldoProyectado(); }
            public void removeUpdate(DocumentEvent e) { recalcularSaldoProyectado(); }
            public void changedUpdate(DocumentEvent e) { recalcularSaldoProyectado(); }
        });
        p.add(txtMonto, gbc);

        // Concepto
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        p.add(crearEtiquetaCampo("Concepto / Justificación de la Operación *"), gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        txtConcepto = new JTextField("Compra de agua purificada y descartables para recepción");
        txtConcepto.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        p.add(txtConcepto, gbc);

        // Card de Saldo Proyectado
        gbc.gridx = 0; gbc.gridy = 6;
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel panelProyeccion = new JPanel(new GridLayout(1, 2, 10, 0)) {
            private static final long serialVersionUID = 1L;
            @Override
            /**
             * Renderizado gráfico personalizado en Java2D.
             * Activa Antialiasing para bordes suaves y realiza el cálculo geométrico preciso.
             */
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(240, 253, 250));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(Ui.TURQUESA_MEDIO);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                g2.dispose();
            }
        };
        panelProyeccion.setOpaque(false);
        panelProyeccion.setBorder(new EmptyBorder(10, 14, 10, 14));

        JLabel lblTitSaldo = new JLabel("Saldo Resultante Estimado en Caja:");
        lblTitSaldo.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTitSaldo.setForeground(Ui.TURQUESA_PROFUNDO);

        lblSaldoProyectado = new JLabel("S/. 0.00", SwingConstants.RIGHT);
        lblSaldoProyectado.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblSaldoProyectado.setForeground(Ui.TURQUESA_OSCURO);

        panelProyeccion.add(lblTitSaldo);
        panelProyeccion.add(lblSaldoProyectado);
        p.add(panelProyeccion, gbc);

        return p;
    }

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private JPanel crearPestanaResponsable() {
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

        // Responsable & Comprobante
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.5;
        p.add(crearEtiquetaCampo("Responsable / Cajero *"), gbc);

        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.5;
        p.add(crearEtiquetaCampo("N° Comprobante / Vale de Caja *"), gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        txtResponsable = new JTextField("Juan Pérez Castro");
        txtResponsable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        p.add(txtResponsable, gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        txtComprobante = new JTextField("VAL-2026-042");
        txtComprobante.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        p.add(txtComprobante, gbc);

        // Observaciones
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        p.add(crearEtiquetaCampo("Observaciones Contables / Centro de Imputación"), gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        txtObservaciones = new JTextArea(4, 20);
        txtObservaciones.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtObservaciones.setLineWrap(true);
        txtObservaciones.setWrapStyleWord(true);
        txtObservaciones.setText("Gasto menor autorizado por Administración para turno diurno de recepción.");
        txtObservaciones.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Ui.BORDE_SUAVE, 1),
                new EmptyBorder(6, 8, 6, 8)
        ));
        p.add(txtObservaciones, gbc);

        return p;
    }

    private JLabel crearEtiquetaCampo(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(new Font("Segoe UI", Font.BOLD, 11));
        l.setForeground(Ui.TEXTO_MUTED);
        return l;
    }

    private void recalcularSaldoProyectado() {
        if (txtMonto == null || cbTipo == null || lblSaldoProyectado == null) return;
        try {
            double saldoActual = repo.getSaldoActualCajaChica();
            double monto = Double.parseDouble(txtMonto.getText().trim());
            String tipo = (String) cbTipo.getSelectedItem();
            double proyectado = "Ingreso".equalsIgnoreCase(tipo) ? (saldoActual + monto) : (saldoActual - monto);
            lblSaldoProyectado.setText(String.format("S/. %.2f", Math.max(0.0, proyectado)));
        } catch (NumberFormatException ignored) {
            lblSaldoProyectado.setText("S/. 0.00");
        }
    }

    private void cargarDatosExistente() {
        txtIdMovimiento.setText(existente.getIdMovimiento());
        txtFecha.setText(existente.getFecha() != null ? existente.getFecha().format(FORMATO_FECHA) : "");
        cbTipo.setSelectedItem(existente.getTipo());
        txtConcepto.setText(existente.getConcepto());
        txtMonto.setText(String.valueOf(existente.getMonto()));
        txtResponsable.setText(existente.getResponsable());
        txtComprobante.setText(existente.getComprobante());
        recalcularSaldoProyectado();
    }

    private void procesarGuardado() {
        String id = txtIdMovimiento.getText().trim();
        String concepto = txtConcepto.getText().trim();
        String responsable = txtResponsable.getText().trim();

        if (concepto.isEmpty() || responsable.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe ingresar el Concepto y el Responsable del movimiento.", "Campos Requeridos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double monto;
        try {
            monto = Double.parseDouble(txtMonto.getText().trim());
            if (monto <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Ingrese un monto válido mayor a cero.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        LocalDate f;
        try {
            f = LocalDate.parse(txtFecha.getText().trim(), FORMATO_FECHA);
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Formato de fecha inválido. Utilice dd/MM/yyyy.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String tipo = (String) cbTipo.getSelectedItem();
        String comp = txtComprobante.getText().trim();

        MovimientoCajaChica m = new MovimientoCajaChica(id, f, tipo, concepto, monto, responsable, comp);
        repo.guardarMovimientoCajaChica(m);
        confirmado = true;
        dispose();
    }

    public boolean isConfirmado() {
        return confirmado;
    }
}
