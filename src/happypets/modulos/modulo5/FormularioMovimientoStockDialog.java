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

import happypets.data.RepositorioVeterinaria;
import happypets.model.LoteMovimientoStock;
import happypets.model.ProductoFarmacia;
import happypets.ui.Iconos;
import happypets.ui.Ui;

/**
 * Modal desacoplado para registrar movimientos de stock en Kardex y trazabilidad de lotes.
 */
public class FormularioMovimientoStockDialog extends JDialog {
    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final RepositorioVeterinaria repo = RepositorioVeterinaria.getInstancia();
    private boolean guardadoExitoso = false;

    // Pestaña 1
    private JComboBox<ProductoFarmaciaItem> cbProducto;
    private JComboBox<String> cbTipoMovimiento;
    private JTextField txtCantidad;
    private JLabel lblStockPrevio;
    private JLabel lblStockPosteriorCalculado;

    // Pestaña 2
    private JTextField txtNumeroLote;
    private JTextField txtFechaVencimiento;
    private JTextField txtFechaMovimiento;
    private JTextField txtResponsable;
    private JTextField txtObservacion;

    public static class ProductoFarmaciaItem {
        private final ProductoFarmacia producto;
        public ProductoFarmaciaItem(ProductoFarmacia p) { this.producto = p; }
        public ProductoFarmacia getProducto() { return producto; }
        @Override public String toString() {
            return producto.getCodigo() + " - " + producto.getNombre() + " (Stock actual: " + producto.getStockActual() + ")";
        }
    }

    /**
     * Constructor de FormularioMovimientoStockDialog.
     * Inicializa los componentes gráficos, configura layouts, asigna estilos
     * institucionales y carga los datos desde el repositorio central.
     */
    public FormularioMovimientoStockDialog(Frame parent) {
        super(parent, "Registrar Movimiento de Kardex / Trazabilidad de Lotes", true);
        setSize(640, 520);
        setLocationRelativeTo(parent);
        setResizable(false);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Ui.FONDO);

        add(crearCabecera(), BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabs.setBackground(Color.WHITE);
        tabs.addTab("1. Operación & Cantidades", Iconos.crearIconoCajaAlmacen(16, Ui.TURQUESA_OSCURO), crearPestanaOperacion());
        tabs.addTab("2. Lote & Trazabilidad Sanitaria", Iconos.crearIconoPildora(16, Ui.COLOR_EXITO), crearPestanaTrazabilidad());
        add(tabs, BorderLayout.CENTER);

        add(crearBotoneraInferior(), BorderLayout.SOUTH);

        inicializarDatos();
    }

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private JPanel crearCabecera() {
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel cab = new JPanel(new BorderLayout());
        cab.setBackground(Color.WHITE);
        cab.setBorder(new EmptyBorder(14, 20, 14, 20));

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("Nuevo Movimiento de Kardex / Almacén");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titulo.setForeground(Ui.TEXTO_TITULO);

        JLabel subtitulo = new JLabel("Control de ingresos, salidas clínicas, despachos quirúrgicos y lotes vigentes");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitulo.setForeground(Ui.TEXTO_MUTED);

        textos.add(titulo);
        textos.add(Box.createVerticalStrut(2));
        textos.add(subtitulo);
        cab.add(textos, BorderLayout.CENTER);

        JLabel lblIcono = new JLabel(Iconos.crearIconoCajaAlmacen(28, Ui.TURQUESA));
        cab.add(lblIcono, BorderLayout.EAST);

        return cab;
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
        p.setBorder(new EmptyBorder(14, 18, 14, 18));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 6, 6, 6);

        // Fila 0: Producto
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        p.add(crearEtiquetaCampo("Fármaco / Producto Involucrado *"), gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        cbProducto = new JComboBox<>();
        cbProducto.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cbProducto.setBackground(Color.WHITE);
        cbProducto.setPreferredSize(new Dimension(100, 32));
        cbProducto.addActionListener(e -> autocompletarProducto());
        p.add(cbProducto, gbc);

        // Fila 2: Tipo de Movimiento y Cantidad
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.6;
        p.add(crearEtiquetaCampo("Tipo de Movimiento Kardex *"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 0.4;
        p.add(crearEtiquetaCampo("Cantidad Operada *"), gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        cbTipoMovimiento = new JComboBox<>(new String[]{
                "Ingreso por Compra",
                "Salida por Consulta",
                "Salida por Cirugía",
                "Salida por Dispensación Externa",
                "Ajuste por Conteo Físico"
        });
        cbTipoMovimiento.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cbTipoMovimiento.setBackground(Color.WHITE);
        cbTipoMovimiento.setPreferredSize(new Dimension(100, 30));
        cbTipoMovimiento.addActionListener(e -> recalcularStockPosterior());
        p.add(cbTipoMovimiento, gbc);

        gbc.gridx = 1; gbc.gridy = 3;
        txtCantidad = crearTextField();
        txtCantidad.setText("5");
        txtCantidad.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override public void keyReleased(java.awt.event.KeyEvent e) { recalcularStockPosterior(); }
        });
        p.add(txtCantidad, gbc);

        // Fila 4: Resumen de Stock
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel panelResumenStock = new JPanel(new GridLayout(1, 2, 10, 0));
        panelResumenStock.setOpaque(false);
        panelResumenStock.setBorder(new EmptyBorder(8, 0, 4, 0));

        lblStockPrevio = new JLabel("Stock Previo: 0 un.", SwingConstants.CENTER);
        lblStockPrevio.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblStockPrevio.setForeground(Ui.TEXTO_MUTED);
        lblStockPrevio.setOpaque(true);
        lblStockPrevio.setBackground(new Color(241, 245, 249));
        lblStockPrevio.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Ui.BORDE_SUAVE, 1, true),
                new EmptyBorder(8, 8, 8, 8)
        ));

        lblStockPosteriorCalculado = new JLabel("Stock Posterior: 0 un.", SwingConstants.CENTER);
        lblStockPosteriorCalculado.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblStockPosteriorCalculado.setForeground(Ui.TURQUESA_PROFUNDO);
        lblStockPosteriorCalculado.setOpaque(true);
        lblStockPosteriorCalculado.setBackground(Ui.TURQUESA_SUAVE);
        lblStockPosteriorCalculado.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Ui.TURQUESA_MEDIO, 1, true),
                new EmptyBorder(8, 8, 8, 8)
        ));

        panelResumenStock.add(lblStockPrevio);
        panelResumenStock.add(lblStockPosteriorCalculado);
        p.add(panelResumenStock, gbc);

        return p;
    }

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private JPanel crearPestanaTrazabilidad() {
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(14, 18, 14, 18));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 6, 6, 6);

        // Fila 0: Lote y Vencimiento
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.5;
        p.add(crearEtiquetaCampo("Número de Lote *"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.5;
        p.add(crearEtiquetaCampo("Fecha de Vencimiento Lote *"), gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        txtNumeroLote = crearTextField();
        p.add(txtNumeroLote, gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        txtFechaVencimiento = crearTextField();
        p.add(txtFechaVencimiento, gbc);

        // Fila 2: Fecha Movimiento y Responsable
        gbc.gridx = 0; gbc.gridy = 2;
        p.add(crearEtiquetaCampo("Fecha de Movimiento *"), gbc);
        gbc.gridx = 1; gbc.gridy = 2;
        p.add(crearEtiquetaCampo("Responsable / Regente *"), gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        txtFechaMovimiento = crearTextField();
        txtFechaMovimiento.setText(LocalDate.now().format(FORMATO_FECHA));
        p.add(txtFechaMovimiento, gbc);

        gbc.gridx = 1; gbc.gridy = 3;
        txtResponsable = crearTextField();
        txtResponsable.setText("Dr. Roberto Torres (Regente)");
        p.add(txtResponsable, gbc);

        // Fila 4: Observación / Referencia
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        p.add(crearEtiquetaCampo("Observación / Documento de Referencia (Factura, Receta o Motivo) *"), gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        txtObservacion = crearTextField();
        txtObservacion.setText("Ingreso regular de mercadería según guía de remisión");
        p.add(txtObservacion, gbc);

        return p;
    }

    private void inicializarDatos() {
        List<ProductoFarmacia> prods = repo.getProductosFarmacia();
        cbProducto.removeAllItems();
        for (ProductoFarmacia prod : prods) {
            cbProducto.addItem(new ProductoFarmaciaItem(prod));
        }
        if (!prods.isEmpty()) {
            autocompletarProducto();
        }
    }

    private void autocompletarProducto() {
        ProductoFarmaciaItem item = (ProductoFarmaciaItem) cbProducto.getSelectedItem();
        if (item != null) {
            ProductoFarmacia p = item.getProducto();
            lblStockPrevio.setText("Stock Previo: " + p.getStockActual() + " un.");
            txtNumeroLote.setText(p.getLoteActual());
            txtFechaVencimiento.setText(p.getFechaVencimientoFormateada());
            recalcularStockPosterior();
        }
    }

    private void recalcularStockPosterior() {
        ProductoFarmaciaItem item = (ProductoFarmaciaItem) cbProducto.getSelectedItem();
        if (item == null) return;

        int stockPrev = item.getProducto().getStockActual();
        try {
            int cant = Integer.parseInt(txtCantidad.getText().trim());
            String tipo = (String) cbTipoMovimiento.getSelectedItem();
            boolean esIngreso = tipo != null && tipo.toLowerCase().contains("ingreso");
            int stockFinal = esIngreso ? (stockPrev + cant) : (stockPrev - cant);
            if (stockFinal < 0) stockFinal = 0;

            lblStockPosteriorCalculado.setText("Stock Posterior: " + stockFinal + " un.");
            lblStockPosteriorCalculado.setForeground(stockFinal <= item.getProducto().getStockMinimo() ? Ui.COLOR_PELIGRO : Ui.TURQUESA_PROFUNDO);
        } catch (Exception ignored) {
            lblStockPosteriorCalculado.setText("Stock Posterior: Cantidad inválida");
        }
    }

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private JPanel crearBotoneraInferior() {
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel barra = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        barra.setBackground(Color.WHITE);
        barra.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Ui.BORDE_SUAVE));

        JButton btnCancelar = crearBotonAccion("Cancelar", false);
        btnCancelar.addActionListener(e -> dispose());

        JButton btnGuardar = crearBotonAccion("Registrar en Kardex", true);
        btnGuardar.addActionListener(e -> validarYGuardar());

        barra.add(btnCancelar);
        barra.add(btnGuardar);
        return barra;
    }

    private void validarYGuardar() {
        ProductoFarmaciaItem item = (ProductoFarmaciaItem) cbProducto.getSelectedItem();
        if (item == null) {
            mostrarError("Debe seleccionar un producto farmacéutico.");
            return;
        }

        int cantidad;
        try {
            cantidad = Integer.parseInt(txtCantidad.getText().trim());
            if (cantidad <= 0) throw new NumberFormatException();
        } catch (Exception ex) {
            mostrarError("La cantidad operada debe ser un número entero mayor a 0.");
            return;
        }

        String lote = txtNumeroLote.getText().trim();
        if (lote.isEmpty()) {
            mostrarError("El número de lote es obligatorio.");
            return;
        }

        LocalDate fVenc;
        try {
            fVenc = LocalDate.parse(txtFechaVencimiento.getText().trim(), FORMATO_FECHA);
        } catch (DateTimeParseException ex) {
            mostrarError("La fecha de vencimiento no tiene un formato válido (dd/MM/yyyy).");
            return;
        }

        LocalDate fMov;
        try {
            fMov = LocalDate.parse(txtFechaMovimiento.getText().trim(), FORMATO_FECHA);
        } catch (DateTimeParseException ex) {
            mostrarError("La fecha de movimiento no tiene un formato válido (dd/MM/yyyy).");
            return;
        }

        String resp = txtResponsable.getText().trim();
        String obs = txtObservacion.getText().trim();
        if (resp.isEmpty()) {
            mostrarError("Debe indicar el profesional o regente responsable.");
            return;
        }

        ProductoFarmacia p = item.getProducto();
        int stockPrev = p.getStockActual();
        String tipo = (String) cbTipoMovimiento.getSelectedItem();
        boolean esIngreso = tipo != null && tipo.toLowerCase().contains("ingreso");
        int stockPost = esIngreso ? (stockPrev + cantidad) : Math.max(0, stockPrev - cantidad);

        LoteMovimientoStock mov = new LoteMovimientoStock(
                "",
                p.getCodigo(),
                p.getNombre(),
                lote,
                tipo,
                cantidad,
                stockPrev,
                stockPost,
                fMov,
                fVenc,
                resp,
                obs.isEmpty() ? "Sin observación adicional" : obs
        );

        repo.registrarMovimientoStock(mov);
        guardadoExitoso = true;

        JOptionPane.showMessageDialog(this,
                "Movimiento registrado en Kardex exitosamente:\n" + tipo + " de " + cantidad + " un. para " + p.getNombre(),
                "Kardex Actualizado", JOptionPane.INFORMATION_MESSAGE);
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
            /**
             * Renderizado gráfico personalizado en Java2D.
             * Activa Antialiasing para bordes suaves y realiza el cálculo geométrico preciso.
             */
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
