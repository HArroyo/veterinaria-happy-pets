package happypets.modulos.modulo6;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
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
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import happypets.data.RepositorioVeterinaria;
import happypets.model.EgresoOperativo;
import happypets.ui.Iconos;
import happypets.ui.Ui;

/**
 * Diálogo modal desacoplado en dos pestañas para registro y edición de Egresos Operativos.
 */
public class FormularioEgresoOperativoDialog extends JDialog {
    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final RepositorioVeterinaria repo = RepositorioVeterinaria.getInstancia();
    private final EgresoOperativo existente;
    private boolean confirmado = false;

    // Pestaña 1: Concepto & Centro de Costo
    private JTextField txtIdEgreso;
    private JTextField txtFecha;
    private JComboBox<String> cbCategoria;
    private JTextArea txtDescripcion;

    // Pestaña 2: Proveedor, Monto & Pago
    private JTextField txtProveedor;
    private JTextField txtMonto;
    private JComboBox<String> cbMetodoPago;
    private JTextField txtComprobante;
    private JComboBox<String> cbEstado;

    public FormularioEgresoOperativoDialog(Frame parent) {
        this(parent, null);
    }

    public FormularioEgresoOperativoDialog(Frame parent, EgresoOperativo existente) {
        super(parent, existente == null ? "Nuevo Egreso Operativo Presupuestal" : "Modificación de Egreso Operativo", true);
        this.existente = existente;
        inicializarUI();
    }

    private void inicializarUI() {
        setSize(600, 620);
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

        JLabel lblTit = new JLabel(existente == null ? "Registro de Egreso Operativo" : "Modificación de Gasto Operativo");
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTit.setForeground(Ui.TEXTO_TITULO);

        JLabel lblSub = new JLabel("Control presupuestario de servicios, alquiler, suministros y costos administrativos");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(Ui.TEXTO_MUTED);

        titulos.add(lblTit);
        titulos.add(Box.createVerticalStrut(2));
        titulos.add(lblSub);
        header.add(titulos, BorderLayout.CENTER);

        JLabel badgeIcono = new JLabel(Iconos.crearIconoEgresos(26, Ui.TURQUESA_OSCURO));
        header.add(badgeIcono, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // Pestañas
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabs.setBackground(Color.WHITE);

        tabs.addTab("1. Concepto & Centro de Costo", Iconos.crearIconoDocumento(15, Ui.TURQUESA), crearPestanaConcepto());
        tabs.addTab("2. Proveedor, Monto & Pago", Iconos.crearIconoMonedas(15, Ui.TURQUESA_OSCURO), crearPestanaPago());

        add(tabs, BorderLayout.CENTER);

        // Footer
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        footer.setBackground(Color.WHITE);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Ui.BORDE_SUAVE));

        JButton btnCancelar = Ui.boton("Cancelar", false);
        btnCancelar.addActionListener(e -> dispose());

        JButton btnGuardar = Ui.boton(existente == null ? "Registrar Egreso" : "Actualizar Egreso", true);
        btnGuardar.addActionListener(e -> procesarGuardado());

        footer.add(btnCancelar);
        footer.add(btnGuardar);
        add(footer, BorderLayout.SOUTH);

        if (existente != null) {
            cargarDatosExistente();
        }
    }

    private JPanel crearPestanaConcepto() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(16, 22, 16, 22));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // ID Egreso & Fecha
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.5;
        p.add(crearEtiquetaCampo("ID Egreso *"), gbc);

        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.5;
        p.add(crearEtiquetaCampo("Fecha de Liquidación (dd/MM/yyyy) *"), gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        txtIdEgreso = new JTextField(existente != null ? existente.getIdEgreso() : "EGR-" + String.format("%03d", repo.getEgresosOperativos().size() + 1));
        txtIdEgreso.setFont(new Font("Segoe UI", Font.BOLD, 12));
        txtIdEgreso.setEditable(false);
        p.add(txtIdEgreso, gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        txtFecha = new JTextField(LocalDate.now().format(FORMATO_FECHA));
        txtFecha.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        p.add(txtFecha, gbc);

        // Categoría Presupuestal
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        p.add(crearEtiquetaCampo("Centro de Costo / Categoría del Gasto *"), gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        cbCategoria = new JComboBox<>(new String[]{
                "Servicios básicos",
                "Alquiler de Local",
                "Planilla y Honorarios",
                "Suministros Médicos",
                "Mantenimiento e Higiene",
                "Marketing y Publicidad",
                "Impuestos y Tributos",
                "Otros Gastos Generales"
        });
        cbCategoria.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        p.add(cbCategoria, gbc);

        // Descripción detallada
        gbc.gridx = 0; gbc.gridy = 4;
        p.add(crearEtiquetaCampo("Descripción Detallada del Gasto *"), gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        txtDescripcion = new JTextArea(4, 20);
        txtDescripcion.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);
        txtDescripcion.setText("Pago de servicio de suministro eléctrico (Luz del Sur) sede clínica.");
        txtDescripcion.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Ui.BORDE_SUAVE, 1),
                new EmptyBorder(6, 8, 6, 8)
        ));
        p.add(txtDescripcion, gbc);

        return p;
    }

    private JPanel crearPestanaPago() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(16, 22, 16, 22));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Proveedor & Monto
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.5;
        p.add(crearEtiquetaCampo("Proveedor / Beneficiario *"), gbc);

        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.5;
        p.add(crearEtiquetaCampo("Monto del Egreso S/. *"), gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        txtProveedor = new JTextField("Luz del Sur S.A.A.");
        txtProveedor.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        p.add(txtProveedor, gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        txtMonto = new JTextField("480.00");
        txtMonto.setFont(new Font("Segoe UI", Font.BOLD, 12));
        p.add(txtMonto, gbc);

        // Método de Pago & N° Comprobante
        gbc.gridx = 0; gbc.gridy = 2;
        p.add(crearEtiquetaCampo("Método de Pago *"), gbc);

        gbc.gridx = 1; gbc.gridy = 2;
        p.add(crearEtiquetaCampo("N° Factura / Comprobante de Respaldo *"), gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        cbMetodoPago = new JComboBox<>(new String[]{
                "Transferencia",
                "Efectivo",
                "Yape / Plin",
                "Tarjeta Débito",
                "Cheque Gerencia"
        });
        cbMetodoPago.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        p.add(cbMetodoPago, gbc);

        gbc.gridx = 1; gbc.gridy = 3;
        txtComprobante = new JTextField("REC-884021");
        txtComprobante.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        p.add(txtComprobante, gbc);

        // Estado
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        p.add(crearEtiquetaCampo("Estado del Gasto"), gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        cbEstado = new JComboBox<>(new String[]{"Pagado", "Programado", "Auditado"});
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

    private void cargarDatosExistente() {
        txtIdEgreso.setText(existente.getIdEgreso());
        txtFecha.setText(existente.getFecha() != null ? existente.getFecha().format(FORMATO_FECHA) : "");
        cbCategoria.setSelectedItem(existente.getCategoria());
        txtDescripcion.setText(existente.getDescripcion());
        txtProveedor.setText(existente.getProveedor());
        txtMonto.setText(String.valueOf(existente.getMonto()));
        cbMetodoPago.setSelectedItem(existente.getMetodoPago());
        txtComprobante.setText(existente.getComprobante());
        cbEstado.setSelectedItem(existente.getEstado());
    }

    private void procesarGuardado() {
        String id = txtIdEgreso.getText().trim();
        String desc = txtDescripcion.getText().trim();
        String prov = txtProveedor.getText().trim();

        if (desc.isEmpty() || prov.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe ingresar la Descripción y el Proveedor del gasto.", "Campos Requeridos", JOptionPane.WARNING_MESSAGE);
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

        String cat = (String) cbCategoria.getSelectedItem();
        String metodo = (String) cbMetodoPago.getSelectedItem();
        String comp = txtComprobante.getText().trim();
        String estado = (String) cbEstado.getSelectedItem();

        EgresoOperativo e = new EgresoOperativo(id, f, cat, desc, prov, monto, metodo, comp, estado);
        repo.guardarEgresoOperativo(e);
        confirmado = true;
        dispose();
    }

    public boolean isConfirmado() {
        return confirmado;
    }
}
