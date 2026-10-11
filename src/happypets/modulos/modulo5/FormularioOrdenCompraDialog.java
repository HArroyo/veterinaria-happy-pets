package happypets.modulos.modulo5;

import java.awt.BorderLayout;
import java.awt.Color;
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
import java.awt.Insets;
import java.awt.RenderingHints;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import happypets.data.RepositorioVeterinaria;
import happypets.model.OrdenCompra;
import happypets.model.ProveedorFarmacia;
import happypets.ui.Iconos;
import happypets.ui.Ui;

/**
 * Modal desacoplado en dos pestañas para emisión y edición de Órdenes de Compra a proveedores.
 */
public class FormularioOrdenCompraDialog extends JDialog {
    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final RepositorioVeterinaria repo = RepositorioVeterinaria.getInstancia();
    private final OrdenCompra ordenExistente;
    private boolean guardadoExitoso = false;

    // Pestaña 1
    private JTextField txtIdOrden;
    private JComboBox<ProveedorItem> cbProveedor;
    private JTextField txtRuc;
    private JTextField txtFechaEmision;
    private JTextField txtFechaEntrega;
    private JTextField txtSolicitante;

    // Pestaña 2
    private JTextArea txtItemsResumen;
    private JTextField txtSubtotal;
    private JLabel lblIgvCalculado;
    private JLabel lblTotalCalculado;
    private JComboBox<String> cbEstado;

    public static class ProveedorItem {
        private final ProveedorFarmacia proveedor;
        public ProveedorItem(ProveedorFarmacia p) { this.proveedor = p; }
        public ProveedorFarmacia getProveedor() { return proveedor; }
        @Override public String toString() {
            return proveedor.getRazonSocial() + " (" + proveedor.getRuc() + ")";
        }
    }

    public FormularioOrdenCompraDialog(Frame parent) {
        this(parent, null);
    }

    public FormularioOrdenCompraDialog(Frame parent, OrdenCompra existente) {
        super(parent, existente == null ? "Nueva Orden de Compra de Fármacos" : "Editar Orden - " + existente.getIdOrden(), true);
        this.ordenExistente = existente;

        setSize(650, 560);
        setLocationRelativeTo(parent);
        setResizable(false);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Ui.FONDO);

        add(crearCabecera(), BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabs.setBackground(Color.WHITE);
        tabs.addTab("1. Proveedor & Términos", Iconos.crearIconoCamionProveedor(16, Ui.TURQUESA_PROFUNDO), crearPestanaProveedor());
        tabs.addTab("2. Ítems Solicitados & Montos", Iconos.crearIconoFactura(16, Ui.COLOR_EXITO), crearPestanaItemsMontos());
        add(tabs, BorderLayout.CENTER);

        add(crearBotoneraInferior(), BorderLayout.SOUTH);

        if (ordenExistente != null) {
            cargarDatosExistentes();
        } else {
            inicializarValoresPorDefecto();
        }
    }

    private JPanel crearCabecera() {
        JPanel cab = new JPanel(new BorderLayout());
        cab.setBackground(Color.WHITE);
        cab.setBorder(new EmptyBorder(14, 20, 14, 20));

        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel(ordenExistente == null ? "Emisión de Orden de Compra Farmacéutica" : "Modificación de Orden de Compra");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titulo.setForeground(Ui.TEXTO_TITULO);

        JLabel subtitulo = new JLabel("Adquisición de medicamentos veterinarios, biológicos y material descartable");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitulo.setForeground(Ui.TEXTO_MUTED);

        textos.add(titulo);
        textos.add(Box.createVerticalStrut(2));
        textos.add(subtitulo);
        cab.add(textos, BorderLayout.CENTER);

        JLabel lblIcono = new JLabel(Iconos.crearIconoDocumento(28, Ui.TURQUESA));
        cab.add(lblIcono, BorderLayout.EAST);

        return cab;
    }

    private JPanel crearPestanaProveedor() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(14, 18, 14, 18));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 6, 5, 6);

        // Fila 0: ID Orden y RUC
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.5;
        p.add(crearEtiquetaCampo("ID Orden de Compra *"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.5;
        p.add(crearEtiquetaCampo("RUC Proveedor *"), gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        txtIdOrden = crearTextField();
        p.add(txtIdOrden, gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        txtRuc = crearTextField();
        txtRuc.setEditable(false);
        txtRuc.setBackground(new Color(241, 245, 249));
        p.add(txtRuc, gbc);

        // Fila 2: Proveedor
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        p.add(crearEtiquetaCampo("Proveedor / Laboratorio Homologado *"), gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        cbProveedor = new JComboBox<>();
        cbProveedor.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cbProveedor.setBackground(Color.WHITE);
        cbProveedor.setPreferredSize(new Dimension(100, 30));
        cbProveedor.addActionListener(e -> autocompletarRucProveedor());
        p.add(cbProveedor, gbc);

        // Fila 4: Fechas
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 4;
        p.add(crearEtiquetaCampo("Fecha de Emisión *"), gbc);
        gbc.gridx = 1; gbc.gridy = 4;
        p.add(crearEtiquetaCampo("Fecha Estimada de Entrega *"), gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        txtFechaEmision = crearTextField();
        p.add(txtFechaEmision, gbc);
        gbc.gridx = 1; gbc.gridy = 5;
        txtFechaEntrega = crearTextField();
        p.add(txtFechaEntrega, gbc);

        // Fila 6: Solicitante
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        p.add(crearEtiquetaCampo("Área / Profesional Solicitante *"), gbc);

        gbc.gridx = 0; gbc.gridy = 7;
        txtSolicitante = crearTextField();
        p.add(txtSolicitante, gbc);

        return p;
    }

    private JPanel crearPestanaItemsMontos() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(14, 18, 14, 18));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 6, 5, 6);

        // Fila 0: Resumen de Ítems
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        p.add(crearEtiquetaCampo("Detalle de Fármacos, Insumos y Cantidades Solicitadas *"), gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        txtItemsResumen = new JTextArea(4, 30);
        txtItemsResumen.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtItemsResumen.setLineWrap(true);
        txtItemsResumen.setWrapStyleWord(true);
        txtItemsResumen.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Ui.BORDE_INPUT, 1, true),
                new EmptyBorder(6, 8, 6, 8)
        ));
        JScrollPane sp = new JScrollPane(txtItemsResumen);
        sp.setPreferredSize(new Dimension(100, 80));
        p.add(sp, gbc);

        // Fila 2: Subtotal y Estado
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 2;
        p.add(crearEtiquetaCampo("Subtotal sin IGV (S/.) *"), gbc);
        gbc.gridx = 1; gbc.gridy = 2;
        p.add(crearEtiquetaCampo("Estado del Pedido *"), gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        txtSubtotal = crearTextField();
        txtSubtotal.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override public void keyReleased(java.awt.event.KeyEvent e) { calcularTotales(); }
        });
        p.add(txtSubtotal, gbc);

        gbc.gridx = 1; gbc.gridy = 3;
        cbEstado = new JComboBox<>(new String[]{"Borrador", "Enviada a Proveedor", "Recibida en Almacén", "Cancelada"});
        cbEstado.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cbEstado.setBackground(Color.WHITE);
        cbEstado.setPreferredSize(new Dimension(100, 30));
        p.add(cbEstado, gbc);

        // Fila 4: IGV y Total
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        JPanel panelTotales = new JPanel(new GridLayout(1, 2, 10, 0));
        panelTotales.setOpaque(false);
        panelTotales.setBorder(new EmptyBorder(8, 0, 4, 0));

        lblIgvCalculado = new JLabel("IGV (18%): S/. 0.00", SwingConstants.CENTER);
        lblIgvCalculado.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblIgvCalculado.setForeground(Ui.TEXTO_MUTED);
        lblIgvCalculado.setOpaque(true);
        lblIgvCalculado.setBackground(new Color(241, 245, 249));
        lblIgvCalculado.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Ui.BORDE_SUAVE, 1, true),
                new EmptyBorder(8, 8, 8, 8)
        ));

        lblTotalCalculado = new JLabel("TOTAL: S/. 0.00", SwingConstants.CENTER);
        lblTotalCalculado.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTotalCalculado.setForeground(new Color(16, 185, 129));
        lblTotalCalculado.setOpaque(true);
        lblTotalCalculado.setBackground(new Color(220, 252, 231));
        lblTotalCalculado.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(187, 247, 208), 1, true),
                new EmptyBorder(8, 8, 8, 8)
        ));

        panelTotales.add(lblIgvCalculado);
        panelTotales.add(lblTotalCalculado);
        p.add(panelTotales, gbc);

        return p;
    }

    private void calcularTotales() {
        try {
            double sub = Double.parseDouble(txtSubtotal.getText().trim().replace(",", "."));
            double igv = sub * 0.18;
            double tot = sub + igv;
            lblIgvCalculado.setText(String.format("IGV (18%%): S/. %.2f", igv));
            lblTotalCalculado.setText(String.format("TOTAL: S/. %.2f", tot));
        } catch (Exception ignored) {
            lblIgvCalculado.setText("IGV (18%): S/. 0.00");
            lblTotalCalculado.setText("TOTAL: S/. 0.00");
        }
    }

    private void autocompletarRucProveedor() {
        ProveedorItem item = (ProveedorItem) cbProveedor.getSelectedItem();
        if (item != null) {
            txtRuc.setText(item.getProveedor().getRuc());
        }
    }

    private JPanel crearBotoneraInferior() {
        JPanel barra = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        barra.setBackground(Color.WHITE);
        barra.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Ui.BORDE_SUAVE));

        JButton btnCancelar = crearBotonAccion("Cancelar", false);
        btnCancelar.addActionListener(e -> dispose());

        JButton btnGuardar = crearBotonAccion(ordenExistente == null ? "Emitir Orden de Compra" : "Actualizar Orden", true);
        btnGuardar.addActionListener(e -> validarYGuardar());

        barra.add(btnCancelar);
        barra.add(btnGuardar);
        return barra;
    }

    private void cargarDatosExistentes() {
        txtIdOrden.setText(ordenExistente.getIdOrden());
        txtIdOrden.setEditable(false);
        txtIdOrden.setBackground(new Color(241, 245, 249));

        txtRuc.setText(ordenExistente.getRucProveedor());
        txtFechaEmision.setText(ordenExistente.getFechaEmisionFormateada());
        txtFechaEntrega.setText(ordenExistente.getFechaEntregaEstimadaFormateada());
        txtSolicitante.setText(ordenExistente.getSolicitante());
        txtItemsResumen.setText(ordenExistente.getItemsResumen());
        txtSubtotal.setText(String.format("%.2f", ordenExistente.getSubtotal()).replace(",", "."));
        cbEstado.setSelectedItem(ordenExistente.getEstado());

        calcularTotales();
    }

    private void inicializarValoresPorDefecto() {
        List<ProveedorFarmacia> provs = repo.getProveedoresFarmacia();
        cbProveedor.removeAllItems();
        for (ProveedorFarmacia p : provs) {
            cbProveedor.addItem(new ProveedorItem(p));
        }
        if (!provs.isEmpty()) {
            autocompletarRucProveedor();
        }

        txtIdOrden.setText("OC-2024-" + String.format("%02d", repo.getOrdenesCompra().size() + 1));
        txtFechaEmision.setText(LocalDate.now().format(FORMATO_FECHA));
        txtFechaEntrega.setText(LocalDate.now().plusDays(5).format(FORMATO_FECHA));
        txtSolicitante.setText("Administración & Farmacia Happy Pets");
        txtItemsResumen.setText("Amoxicilina + Ác. Clavulánico x 15 cajas, Meloxicam 1 mg x 20 cajas, Alcohol 70° x 10 galones");
        txtSubtotal.setText("850.00");
        calcularTotales();
    }

    private void validarYGuardar() {
        String id = txtIdOrden.getText().trim();
        String ruc = txtRuc.getText().trim();
        ProveedorItem provItem = (ProveedorItem) cbProveedor.getSelectedItem();
        String nomProv = provItem != null ? provItem.getProveedor().getRazonSocial() : "";

        if (id.isEmpty()) {
            mostrarError("El ID de la orden es obligatorio.");
            return;
        }
        if (ruc.isEmpty() || nomProv.isEmpty()) {
            mostrarError("Debe seleccionar un proveedor homologado.");
            return;
        }

        LocalDate fEmis, fEntr;
        try {
            fEmis = LocalDate.parse(txtFechaEmision.getText().trim(), FORMATO_FECHA);
            fEntr = LocalDate.parse(txtFechaEntrega.getText().trim(), FORMATO_FECHA);
        } catch (DateTimeParseException ex) {
            mostrarError("Las fechas deben tener el formato válido dd/MM/yyyy.");
            return;
        }

        String items = txtItemsResumen.getText().trim();
        if (items.isEmpty()) {
            mostrarError("Debe especificar los ítems y cantidades solicitadas.");
            return;
        }

        double sub;
        try {
            sub = Double.parseDouble(txtSubtotal.getText().trim().replace(",", "."));
            if (sub <= 0) throw new NumberFormatException();
        } catch (Exception ex) {
            mostrarError("El subtotal debe ser un valor numérico mayor a 0.");
            return;
        }

        double igv = sub * 0.18;
        double tot = sub + igv;
        String est = (String) cbEstado.getSelectedItem();
        String sol = txtSolicitante.getText().trim();

        if (ordenExistente != null) {
            ordenExistente.setRucProveedor(ruc);
            ordenExistente.setNombreProveedor(nomProv);
            ordenExistente.setFechaEmision(fEmis);
            ordenExistente.setFechaEntregaEstimada(fEntr);
            ordenExistente.setItemsResumen(items);
            ordenExistente.setSubtotal(sub);
            ordenExistente.setIgv(igv);
            ordenExistente.setTotal(tot);
            ordenExistente.setEstado(est);
            ordenExistente.setSolicitante(sol.isEmpty() ? "Administración Happy Pets" : sol);
            repo.guardarOrdenCompra(ordenExistente);
        } else {
            OrdenCompra oc = new OrdenCompra(
                    id, ruc, nomProv, fEmis, fEntr, items, sub, igv, tot, est, sol.isEmpty() ? "Administración Happy Pets" : sol
            );
            repo.guardarOrdenCompra(oc);
        }

        guardadoExitoso = true;
        JOptionPane.showMessageDialog(this,
                "Orden de Compra procesada con éxito:\n" + id + " para " + nomProv + " (Total: S/. " + String.format("%.2f", tot) + ")",
                "Orden Registrada", JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }

    public boolean isGuardadoExitoso() {
        return guardadoExitoso;
    }

    private void mostrarError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Datos Incompletos / Inválidos", JOptionPane.WARNING_MESSAGE);
    }

    private JLabel crearEtiquetaCampo(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(new Font("Segoe UI", Font.BOLD, 11));
        l.setForeground(new Color(51, 65, 85));
        return l;
    }

    private JTextField crearTextField() {
        JTextField tf = new JTextField();
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tf.setPreferredSize(new Dimension(100, 30));
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Ui.BORDE_INPUT, 1, true),
                new EmptyBorder(4, 8, 4, 8)
        ));
        return tf;
    }

    private JButton crearBotonAccion(String texto, boolean primario) {
        JButton btn = new JButton(texto) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                boolean esPrimario = Boolean.TRUE.equals(getClientProperty("primario"));
                if (esPrimario) {
                    g2.setColor(getModel().isRollover() ? Ui.TURQUESA_OSCURO : Ui.TURQUESA);
                } else {
                    g2.setColor(getModel().isRollover() ? new Color(241, 245, 249) : Color.WHITE);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                if (!esPrimario) {
                    g2.setColor(Ui.BORDE_SUAVE);
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 6, 6);
                }
                super.paintComponent(g);
                g2.dispose();
            }

            @Override
            public Dimension getPreferredSize() {
                Dimension d = super.getPreferredSize();
                return new Dimension(d.width + 16, 32);
            }
        };
        btn.putClientProperty("primario", primario);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setForeground(primario ? Color.WHITE : new Color(51, 65, 85));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(6, 12, 6, 12));
        return btn;
    }
}
