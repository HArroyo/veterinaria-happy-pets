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
import javax.swing.border.EmptyBorder;

import happypets.data.RepositorioVeterinaria;
import happypets.model.AjusteMerma;
import happypets.model.ProductoFarmacia;
import happypets.ui.Iconos;
import happypets.ui.Ui;

/**
 * Modal desacoplado en dos pestañas para el registro de ajustes y mermas en farmacia veterinaria.
 */
public class FormularioAjusteMermaDialog extends JDialog {
    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final RepositorioVeterinaria repo = RepositorioVeterinaria.getInstancia();
    private boolean guardadoExitoso = false;

    // Pestaña 1
    private JComboBox<ProductoItem> cbProducto;
    private JComboBox<String> cbTipo;
    private JTextField txtCantidad;
    private JTextField txtCostoUnitario;
    private JLabel lblPerdidaCalculada;

    // Pestaña 2
    private JTextField txtLote;
    private JTextField txtFecha;
    private JTextField txtAutorizadoPor;
    private JComboBox<String> cbEstado;
    private JTextArea txtMotivo;

    public static class ProductoItem {
        private final ProductoFarmacia producto;
        public ProductoItem(ProductoFarmacia p) { this.producto = p; }
        public ProductoFarmacia getProducto() { return producto; }
        @Override public String toString() {
            return producto.getCodigo() + " - " + producto.getNombre() + " (Stock: " + producto.getStockActual() + ")";
        }
    }

    public FormularioAjusteMermaDialog(Frame parent) {
        super(parent, "Registro de Merma / Ajuste Sanitario de Fármacos", true);
        setSize(640, 530);
        setLocationRelativeTo(parent);
        setResizable(false);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Ui.FONDO);

        add(crearCabecera(), BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabs.setBackground(Color.WHITE);
        tabs.addTab("1. Producto & Valorización", Iconos.crearIconoAlertaMerma(16, Ui.COLOR_PELIGRO), crearPestanaProducto());
        tabs.addTab("2. Causal Sanitaria & Autorización", Iconos.crearIconoDocumento(16, Ui.TURQUESA_PROFUNDO), crearPestanaCausal());
        add(tabs, BorderLayout.CENTER);

        add(crearBotoneraInferior(), BorderLayout.SOUTH);

        inicializarDatos();
    }

    private JPanel crearCabecera() {
        JPanel cab = new JPanel(new BorderLayout());
        cab.setBackground(Color.WHITE);
        cab.setBorder(new EmptyBorder(14, 20, 14, 20));

        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("Baja por Merma / Ajuste de Inventario");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titulo.setForeground(Ui.TEXTO_TITULO);

        JLabel subtitulo = new JLabel("Descarte biológico por vencimiento, roturas accidentales o regularizaciones físicas");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitulo.setForeground(Ui.TEXTO_MUTED);

        textos.add(titulo);
        textos.add(Box.createVerticalStrut(2));
        textos.add(subtitulo);
        cab.add(textos, BorderLayout.CENTER);

        JLabel lblIcono = new JLabel(Iconos.crearIconoAlertaMerma(28, Ui.COLOR_PELIGRO));
        cab.add(lblIcono, BorderLayout.EAST);

        return cab;
    }

    private JPanel crearPestanaProducto() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(14, 18, 14, 18));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 6, 5, 6);

        // Fila 0: Producto
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        p.add(crearEtiquetaCampo("Fármaco / Producto Afectado *"), gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        cbProducto = new JComboBox<>();
        cbProducto.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cbProducto.setBackground(Color.WHITE);
        cbProducto.setPreferredSize(new Dimension(100, 30));
        cbProducto.addActionListener(e -> autocompletarProducto());
        p.add(cbProducto, gbc);

        // Fila 2: Tipo de Ajuste
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        p.add(crearEtiquetaCampo("Causal / Tipo de Merma *"), gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        cbTipo = new JComboBox<>(new String[]{
                "Vencimiento de Lote",
                "Merma por Rotura / Deterioro",
                "Falla Cadena de Frío",
                "Ajuste Físico Negativo",
                "Ajuste Físico Positivo"
        });
        cbTipo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cbTipo.setBackground(Color.WHITE);
        cbTipo.setPreferredSize(new Dimension(100, 30));
        p.add(cbTipo, gbc);

        // Fila 4: Cantidad y Costo Unitario
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0.5;
        p.add(crearEtiquetaCampo("Cantidad Afectada (Unidades) *"), gbc);
        gbc.gridx = 1; gbc.gridy = 4; gbc.weightx = 0.5;
        p.add(crearEtiquetaCampo("Costo Unitario de Adquisición S/. *"), gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        txtCantidad = crearTextField();
        txtCantidad.setText("2");
        txtCantidad.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override public void keyReleased(java.awt.event.KeyEvent e) { recalcularPerdida(); }
        });
        p.add(txtCantidad, gbc);

        gbc.gridx = 1; gbc.gridy = 5;
        txtCostoUnitario = crearTextField();
        txtCostoUnitario.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override public void keyReleased(java.awt.event.KeyEvent e) { recalcularPerdida(); }
        });
        p.add(txtCostoUnitario, gbc);

        // Fila 6: Pérdida Valorizada Calculada
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        lblPerdidaCalculada = new JLabel("Pérdida Valorizada: S/. 0.00", JLabel.CENTER);
        lblPerdidaCalculada.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblPerdidaCalculada.setForeground(Ui.COLOR_PELIGRO);
        lblPerdidaCalculada.setOpaque(true);
        lblPerdidaCalculada.setBackground(new Color(254, 242, 242));
        lblPerdidaCalculada.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(254, 202, 202), 1, true),
                new EmptyBorder(8, 12, 8, 12)
        ));
        p.add(lblPerdidaCalculada, gbc);

        return p;
    }

    private JPanel crearPestanaCausal() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(14, 18, 14, 18));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 6, 5, 6);

        // Fila 0: Lote y Fecha
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.5;
        p.add(crearEtiquetaCampo("Número de Lote Afectado *"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.5;
        p.add(crearEtiquetaCampo("Fecha del Descarte (dd/MM/yyyy) *"), gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        txtLote = crearTextField();
        p.add(txtLote, gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        txtFecha = crearTextField();
        txtFecha.setText(LocalDate.now().format(FORMATO_FECHA));
        p.add(txtFecha, gbc);

        // Fila 2: Autorizado Por y Estado
        gbc.gridx = 0; gbc.gridy = 2;
        p.add(crearEtiquetaCampo("Profesional que Autoriza la Baja *"), gbc);
        gbc.gridx = 1; gbc.gridy = 2;
        p.add(crearEtiquetaCampo("Estado del Registro *"), gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        txtAutorizadoPor = crearTextField();
        txtAutorizadoPor.setText("Dra. Camila Morales (Dirección Médica)");
        p.add(txtAutorizadoPor, gbc);

        gbc.gridx = 1; gbc.gridy = 3;
        cbEstado = new JComboBox<>(new String[]{"Aprobado y Descargado", "Pendiente de Visto Bueno"});
        cbEstado.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cbEstado.setBackground(Color.WHITE);
        cbEstado.setPreferredSize(new Dimension(100, 30));
        p.add(cbEstado, gbc);

        // Fila 4: Motivo Detallado
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        p.add(crearEtiquetaCampo("Motivo Circunstanciado / Dictamen Sanitario *"), gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        txtMotivo = new JTextArea(3, 20);
        txtMotivo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtMotivo.setLineWrap(true);
        txtMotivo.setWrapStyleWord(true);
        txtMotivo.setText("Frasco roto accidentalmente durante el proceso de reposición en estantería.");
        txtMotivo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Ui.BORDE_INPUT, 1, true),
                new EmptyBorder(6, 8, 6, 8)
        ));
        JScrollPane sp = new JScrollPane(txtMotivo);
        sp.setPreferredSize(new Dimension(100, 70));
        p.add(sp, gbc);

        return p;
    }

    private void inicializarDatos() {
        List<ProductoFarmacia> prods = repo.getProductosFarmacia();
        cbProducto.removeAllItems();
        for (ProductoFarmacia prod : prods) {
            cbProducto.addItem(new ProductoItem(prod));
        }
        if (!prods.isEmpty()) {
            autocompletarProducto();
        }
    }

    private void autocompletarProducto() {
        ProductoItem item = (ProductoItem) cbProducto.getSelectedItem();
        if (item != null) {
            ProductoFarmacia p = item.getProducto();
            txtCostoUnitario.setText(String.format("%.2f", p.getPrecioCosto()).replace(",", "."));
            txtLote.setText(p.getLoteActual());
            recalcularPerdida();
        }
    }

    private void recalcularPerdida() {
        try {
            int cant = Integer.parseInt(txtCantidad.getText().trim());
            double costo = Double.parseDouble(txtCostoUnitario.getText().trim().replace(",", "."));
            double tot = cant * costo;
            lblPerdidaCalculada.setText(String.format("Pérdida Valorizada: S/. %.2f", tot));
        } catch (Exception ignored) {
            lblPerdidaCalculada.setText("Pérdida Valorizada: Ingrese cantidad y costo válidos");
        }
    }

    private JPanel crearBotoneraInferior() {
        JPanel barra = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        barra.setBackground(Color.WHITE);
        barra.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Ui.BORDE_SUAVE));

        JButton btnCancelar = crearBotonAccion("Cancelar", false);
        btnCancelar.addActionListener(e -> dispose());

        JButton btnGuardar = crearBotonAccion("Guardar Registro de Merma", true);
        btnGuardar.addActionListener(e -> validarYGuardar());

        barra.add(btnCancelar);
        barra.add(btnGuardar);
        return barra;
    }

    private void validarYGuardar() {
        ProductoItem item = (ProductoItem) cbProducto.getSelectedItem();
        if (item == null) {
            mostrarError("Debe seleccionar un producto farmacéutico.");
            return;
        }

        int cant;
        try {
            cant = Integer.parseInt(txtCantidad.getText().trim());
            if (cant <= 0) throw new NumberFormatException();
        } catch (Exception ex) {
            mostrarError("La cantidad afectada debe ser un número entero mayor a 0.");
            return;
        }

        double costo;
        try {
            costo = Double.parseDouble(txtCostoUnitario.getText().trim().replace(",", "."));
            if (costo < 0) throw new NumberFormatException();
        } catch (Exception ex) {
            mostrarError("El costo unitario debe ser mayor o igual a 0.");
            return;
        }

        LocalDate fecha;
        try {
            fecha = LocalDate.parse(txtFecha.getText().trim(), FORMATO_FECHA);
        } catch (DateTimeParseException ex) {
            mostrarError("La fecha de descarte debe tener formato dd/MM/yyyy.");
            return;
        }

        String lote = txtLote.getText().trim();
        String motivo = txtMotivo.getText().trim();
        String autor = txtAutorizadoPor.getText().trim();

        if (lote.isEmpty()) {
            mostrarError("El número de lote es obligatorio.");
            return;
        }
        if (motivo.isEmpty()) {
            mostrarError("Debe ingresar el motivo detallado de la merma.");
            return;
        }
        if (autor.isEmpty()) {
            mostrarError("Debe ingresar quién autoriza la baja.");
            return;
        }

        double perdida = cant * costo;
        String tipo = (String) cbTipo.getSelectedItem();
        String est = (String) cbEstado.getSelectedItem();

        AjusteMerma ajuste = new AjusteMerma(
                "", fecha, item.getProducto().getCodigo(), item.getProducto().getNombre(),
                lote, tipo, cant, costo, perdida, motivo, autor, est
        );

        repo.guardarAjusteMerma(ajuste);
        guardadoExitoso = true;

        JOptionPane.showMessageDialog(this,
                "Registro de merma guardado exitosamente:\n" + tipo + " de " + cant + " un. (" + item.getProducto().getNombre() + ")\nPérdida: S/. " + String.format("%.2f", perdida),
                "Merma Procesada", JOptionPane.INFORMATION_MESSAGE);
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
