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

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
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
import javax.swing.border.EmptyBorder;

import happypets.data.RepositorioVeterinaria;
import happypets.model.ProductoFarmacia;
import happypets.ui.Iconos;
import happypets.ui.Ui;

/**
 * Modal desacoplado para creación y edición de productos farmacéuticos y medicamentos.
 * Estructurado en dos pestañas ergonómicas para evitar recargar la interfaz.
 */
public class FormularioProductoFarmaciaDialog extends JDialog {
    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final RepositorioVeterinaria repo = RepositorioVeterinaria.getInstancia();
    private final ProductoFarmacia productoExistente;
    private boolean guardadoExitoso = false;

    // Campos Pestaña 1: Clasificación & Farmacología
    private JTextField txtCodigo;
    private JTextField txtNombre;
    private JComboBox<String> cbCategoria;
    private JTextField txtPrincipioActivo;
    private JTextField txtPresentacion;
    private JComboBox<String> cbEspecie;
    private JTextField txtProveedor;
    private JCheckBox chkReceta;
    private JCheckBox chkCadenaFrio;

    // Campos Pestaña 2: Precios, Stock & Trazabilidad
    private JTextField txtPrecioCosto;
    private JTextField txtPrecioVenta;
    private JLabel lblMargenCalculado;
    private JTextField txtStockActual;
    private JTextField txtStockMinimo;
    private JTextField txtLoteActual;
    private JTextField txtFechaVencimiento;

    /**
     * Constructor de FormularioProductoFarmaciaDialog.
     * Inicializa los componentes gráficos, configura layouts, asigna estilos
     * institucionales y carga los datos desde el repositorio central.
     */
    public FormularioProductoFarmaciaDialog(Frame parent) {
        this(parent, null);
    }

    /**
     * Constructor de FormularioProductoFarmaciaDialog.
     * Inicializa los componentes gráficos, configura layouts, asigna estilos
     * institucionales y carga los datos desde el repositorio central.
     */
    public FormularioProductoFarmaciaDialog(Frame parent, ProductoFarmacia existente) {
        super(parent, existente == null ? "Nuevo Fármaco / Producto Veterinario" : "Editar Producto - " + existente.getCodigo(), true);
        this.productoExistente = existente;

        setSize(650, 560);
        setLocationRelativeTo(parent);
        setResizable(false);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Ui.FONDO);

        add(crearCabecera(), BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabs.setBackground(Color.WHITE);
        tabs.addTab("1. Información Básica & Farmacología", Iconos.crearIconoPildora(16, Ui.TURQUESA_OSCURO), crearPestanaBasica());
        tabs.addTab("2. Precios, Stock & Lote", Iconos.crearIconoCajaAlmacen(16, Ui.COLOR_EXITO), crearPestanaPreciosStock());
        add(tabs, BorderLayout.CENTER);

        add(crearBotoneraInferior(), BorderLayout.SOUTH);

        if (productoExistente != null) {
            cargarDatosExistentes();
        } else {
            inicializarValoresPorDefecto();
        }
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

        JLabel titulo = new JLabel(productoExistente == null ? "Registro de Fármaco / Producto Veterinario" : "Modificación de Ficha Farmacéutica");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titulo.setForeground(Ui.TEXTO_TITULO);

        JLabel subtitulo = new JLabel("Catálogo Oficial de Medicamentos, Biológicos, Insumos Clínicos y Dietas de Soporte");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitulo.setForeground(Ui.TEXTO_MUTED);

        textos.add(titulo);
        textos.add(Box.createVerticalStrut(2));
        textos.add(subtitulo);
        cab.add(textos, BorderLayout.CENTER);

        JLabel lblIcono = new JLabel(Iconos.crearIconoPildora(28, Ui.TURQUESA));
        cab.add(lblIcono, BorderLayout.EAST);

        return cab;
    }

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private JPanel crearPestanaBasica() {
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
        gbc.insets = new Insets(5, 6, 5, 6);

        // Fila 0: Código y Nombre
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.35;
        p.add(crearEtiquetaCampo("Código de Producto *"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.65;
        p.add(crearEtiquetaCampo("Nombre Comercial *"), gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        txtCodigo = crearTextField();
        p.add(txtCodigo, gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        txtNombre = crearTextField();
        p.add(txtNombre, gbc);

        // Fila 2: Categoría y Especie Destino
        gbc.gridx = 0; gbc.gridy = 2;
        p.add(crearEtiquetaCampo("Categoría Farmacéutica *"), gbc);
        gbc.gridx = 1; gbc.gridy = 2;
        p.add(crearEtiquetaCampo("Especie Destino *"), gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        cbCategoria = new JComboBox<>(new String[]{
                "Antibiótico", "Antiparasitario", "Analgésico / AINE", "Vacuna / Biológico",
                "Alimento Clínico", "Material Quirúrgico", "Vitamínico / Suplemento", "Dermatológico", "Oftalmológico", "Otro"
        });
        estilizarCombo(cbCategoria);
        p.add(cbCategoria, gbc);

        gbc.gridx = 1; gbc.gridy = 3;
        cbEspecie = new JComboBox<>(new String[]{"Canino", "Felino", "Mixto Canino/Felino", "Aves / Exóticos"});
        estilizarCombo(cbEspecie);
        p.add(cbEspecie, gbc);

        // Fila 4: Principio Activo y Presentación
        gbc.gridx = 0; gbc.gridy = 4;
        p.add(crearEtiquetaCampo("Principio Activo *"), gbc);
        gbc.gridx = 1; gbc.gridy = 4;
        p.add(crearEtiquetaCampo("Presentación / Concentración *"), gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        txtPrincipioActivo = crearTextField();
        p.add(txtPrincipioActivo, gbc);

        gbc.gridx = 1; gbc.gridy = 5;
        txtPresentacion = crearTextField();
        p.add(txtPresentacion, gbc);

        // Fila 6: Proveedor habitual
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        p.add(crearEtiquetaCampo("Laboratorio / Proveedor Habitual *"), gbc);

        gbc.gridx = 0; gbc.gridy = 7;
        txtProveedor = crearTextField();
        p.add(txtProveedor, gbc);

        // Fila 8: Checkboxes de requisitos sanitarios
        gbc.gridx = 0; gbc.gridy = 8; gbc.gridwidth = 2;
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel panelChecks = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 6));
        panelChecks.setOpaque(false);

        chkReceta = new JCheckBox("Requiere Receta Médica Retenida (Fármaco Controlado)");
        chkReceta.setFont(new Font("Segoe UI", Font.BOLD, 11));
        chkReceta.setForeground(new Color(126, 34, 206));
        chkReceta.setOpaque(false);

        chkCadenaFrio = new JCheckBox("Cadena de Frío Estricta (Refrigeración 2°C - 8°C)");
        chkCadenaFrio.setFont(new Font("Segoe UI", Font.BOLD, 11));
        chkCadenaFrio.setForeground(new Color(2, 132, 199));
        chkCadenaFrio.setOpaque(false);

        panelChecks.add(chkReceta);
        panelChecks.add(chkCadenaFrio);
        p.add(panelChecks, gbc);

        return p;
    }

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private JPanel crearPestanaPreciosStock() {
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

        // Fila 0: Precios
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.5;
        p.add(crearEtiquetaCampo("Precio de Costo S/. *"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.5;
        p.add(crearEtiquetaCampo("Precio de Venta PVP S/. *"), gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        txtPrecioCosto = crearTextField();
        p.add(txtPrecioCosto, gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        txtPrecioVenta = crearTextField();
        p.add(txtPrecioVenta, gbc);

        // Fila 2: Margen calculado
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        lblMargenCalculado = new JLabel("Margen Comercial Estimado: 0.0% (S/. 0.00)");
        lblMargenCalculado.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblMargenCalculado.setForeground(Ui.TURQUESA_PROFUNDO);
        lblMargenCalculado.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Ui.TURQUESA_MEDIO, 1, true),
                new EmptyBorder(4, 8, 4, 8)
        ));
        p.add(lblMargenCalculado, gbc);

        txtPrecioCosto.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override public void keyReleased(java.awt.event.KeyEvent e) { actualizarMargen(); }
        });
        txtPrecioVenta.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override public void keyReleased(java.awt.event.KeyEvent e) { actualizarMargen(); }
        });

        // Fila 3: Stock actual y Stock mínimo
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 1;
        p.add(crearEtiquetaCampo("Stock Actual Inicial *"), gbc);
        gbc.gridx = 1; gbc.gridy = 3;
        p.add(crearEtiquetaCampo("Stock Mínimo de Alerta *"), gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        txtStockActual = crearTextField();
        p.add(txtStockActual, gbc);

        gbc.gridx = 1; gbc.gridy = 4;
        txtStockMinimo = crearTextField();
        p.add(txtStockMinimo, gbc);

        // Fila 5: Lote actual y Fecha vencimiento
        gbc.gridx = 0; gbc.gridy = 5;
        p.add(crearEtiquetaCampo("Número de Lote Vigente *"), gbc);
        gbc.gridx = 1; gbc.gridy = 5;
        p.add(crearEtiquetaCampo("Fecha de Vencimiento (dd/MM/yyyy) *"), gbc);

        gbc.gridx = 0; gbc.gridy = 6;
        txtLoteActual = crearTextField();
        p.add(txtLoteActual, gbc);

        gbc.gridx = 1; gbc.gridy = 6;
        txtFechaVencimiento = crearTextField();
        p.add(txtFechaVencimiento, gbc);

        return p;
    }

    private void actualizarMargen() {
        try {
            double c = Double.parseDouble(txtPrecioCosto.getText().trim());
            double v = Double.parseDouble(txtPrecioVenta.getText().trim());
            if (c > 0) {
                double ganancia = v - c;
                double pct = (ganancia / c) * 100.0;
                lblMargenCalculado.setText(String.format("Margen Comercial Estimado: %.1f%% (+S/. %.2f)", pct, ganancia));
                lblMargenCalculado.setForeground(ganancia >= 0 ? new Color(16, 185, 129) : Ui.COLOR_PELIGRO);
            }
        } catch (Exception ignored) {
            lblMargenCalculado.setText("Margen Comercial Estimado: Ingrese costo y PVP válidos");
            lblMargenCalculado.setForeground(Ui.TEXTO_MUTED);
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

        JButton btnGuardar = crearBotonAccion(productoExistente == null ? "Guardar Producto en Catálogo" : "Actualizar Producto", true);
        btnGuardar.addActionListener(e -> validarYGuardar());

        barra.add(btnCancelar);
        barra.add(btnGuardar);
        return barra;
    }

    private void cargarDatosExistentes() {
        txtCodigo.setText(productoExistente.getCodigo());
        txtCodigo.setEditable(false);
        txtCodigo.setBackground(new Color(241, 245, 249));

        txtNombre.setText(productoExistente.getNombre());
        cbCategoria.setSelectedItem(productoExistente.getCategoria());
        txtPrincipioActivo.setText(productoExistente.getPrincipioActivo());
        txtPresentacion.setText(productoExistente.getPresentacion());
        cbEspecie.setSelectedItem(productoExistente.getEspecieDestino());
        txtProveedor.setText(productoExistente.getProveedor());
        chkReceta.setSelected(productoExistente.isRequiereReceta());
        chkCadenaFrio.setSelected(productoExistente.isCadenaFrio());

        txtPrecioCosto.setText(String.format("%.2f", productoExistente.getPrecioCosto()).replace(",", "."));
        txtPrecioVenta.setText(String.format("%.2f", productoExistente.getPrecioVenta()).replace(",", "."));
        txtStockActual.setText(String.valueOf(productoExistente.getStockActual()));
        txtStockMinimo.setText(String.valueOf(productoExistente.getStockMinimo()));
        txtLoteActual.setText(productoExistente.getLoteActual());
        txtFechaVencimiento.setText(productoExistente.getFechaVencimientoFormateada());

        actualizarMargen();
    }

    private void inicializarValoresPorDefecto() {
        txtCodigo.setText("PROD-" + String.format("%03d", repo.getProductosFarmacia().size() + 1));
        txtStockMinimo.setText("5");
        txtStockActual.setText("10");
        txtPrecioCosto.setText("15.00");
        txtPrecioVenta.setText("25.00");
        txtLoteActual.setText("LOT-" + LocalDate.now().getYear() + "-01");
        txtFechaVencimiento.setText(LocalDate.now().plusMonths(18).format(FORMATO_FECHA));
        txtProveedor.setText("Distribuidora Veterinaria Perú S.A.C.");
        actualizarMargen();
    }

    private void validarYGuardar() {
        String cod = txtCodigo.getText().trim();
        String nom = txtNombre.getText().trim();
        String princ = txtPrincipioActivo.getText().trim();
        String pres = txtPresentacion.getText().trim();
        String prov = txtProveedor.getText().trim();
        String lote = txtLoteActual.getText().trim();
        String fVencStr = txtFechaVencimiento.getText().trim();

        if (nom.isEmpty()) {
            mostrarError("El nombre comercial del producto es obligatorio.");
            return;
        }
        if (princ.isEmpty()) {
            mostrarError("El principio activo es obligatorio.");
            return;
        }
        if (pres.isEmpty()) {
            mostrarError("La presentación comercial es obligatoria.");
            return;
        }
        if (lote.isEmpty()) {
            mostrarError("Debe indicar el número de lote de fabricación.");
            return;
        }

        double costo, venta;
        try {
            costo = Double.parseDouble(txtPrecioCosto.getText().trim().replace(",", "."));
            venta = Double.parseDouble(txtPrecioVenta.getText().trim().replace(",", "."));
            if (costo < 0 || venta < 0) throw new NumberFormatException();
        } catch (Exception ex) {
            mostrarError("Los precios de costo y venta deben ser valores numéricos mayores o iguales a 0.");
            return;
        }

        int stockAct, stockMin;
        try {
            stockAct = Integer.parseInt(txtStockActual.getText().trim());
            stockMin = Integer.parseInt(txtStockMinimo.getText().trim());
            if (stockAct < 0 || stockMin < 0) throw new NumberFormatException();
        } catch (Exception ex) {
            mostrarError("El stock actual y mínimo deben ser enteros positivos.");
            return;
        }

        LocalDate fVenc;
        try {
            fVenc = LocalDate.parse(fVencStr, FORMATO_FECHA);
        } catch (DateTimeParseException ex) {
            mostrarError("La fecha de vencimiento no tiene un formato válido (dd/MM/yyyy).");
            return;
        }

        String cat = (String) cbCategoria.getSelectedItem();
        String esp = (String) cbEspecie.getSelectedItem();
        boolean rec = chkReceta.isSelected();
        boolean frio = chkCadenaFrio.isSelected();

        String estado;
        if (stockAct <= 0) estado = "Agotado";
        else if (stockAct <= stockMin) estado = "Bajo Stock";
        else estado = "Disponible";

        if (productoExistente != null) {
            productoExistente.setNombre(nom);
            productoExistente.setCategoria(cat);
            productoExistente.setPrincipioActivo(princ);
            productoExistente.setPresentacion(pres);
            productoExistente.setEspecieDestino(esp);
            productoExistente.setProveedor(prov);
            productoExistente.setRequiereReceta(rec);
            productoExistente.setCadenaFrio(frio);
            productoExistente.setPrecioCosto(costo);
            productoExistente.setPrecioVenta(venta);
            productoExistente.setStockActual(stockAct);
            productoExistente.setStockMinimo(stockMin);
            productoExistente.setLoteActual(lote);
            productoExistente.setFechaVencimiento(fVenc);
            productoExistente.setEstado(estado);
            repo.guardarProductoFarmacia(productoExistente);
        } else {
            ProductoFarmacia nuevo = new ProductoFarmacia(
                    cod, nom, cat, princ, pres, esp, rec, frio,
                    costo, venta, stockAct, stockMin, lote, fVenc, prov, estado
            );
            repo.guardarProductoFarmacia(nuevo);
        }

        guardadoExitoso = true;
        JOptionPane.showMessageDialog(this,
                "Producto guardado exitosamente en el catálogo farmacéutico:\n" + nom + " (" + cod + ")",
                "Operación Exitosa", JOptionPane.INFORMATION_MESSAGE);
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

    private void estilizarCombo(JComboBox<?> cb) {
        cb.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cb.setBackground(Color.WHITE);
        cb.setPreferredSize(new Dimension(100, 30));
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
