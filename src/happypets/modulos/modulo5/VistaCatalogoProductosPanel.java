package happypets.modulos.modulo5;

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
import java.util.List;
import java.util.Optional;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import happypets.data.RepositorioVeterinaria;
import happypets.model.ProductoFarmacia;
import happypets.ui.Iconos;

/**
 * Submódulo 5.1: Catálogo de Productos y Fármacos.
 * Basado en el wireframe oficial (Pág. 1) y especificaciones ERP Happy Pets:
 * - Registro maestro de medicamentos, biológicos, alimentos clínicos y material quirúrgico.
 * - Validación de venta bajo receta médica retenida y cadena de frío (2°C - 8°C).
 * - Control de márgenes comerciales (precio de compra vs venta).
 * - Emisión de fichas técnicas oficiales y etiquetas de dispensación farmacéutica.
 */
public class VistaCatalogoProductosPanel extends happypets.ui.AssetsModulo {
    private static final long serialVersionUID = 1L;

    private static final Color COLOR_BORDE = new Color(226, 232, 240);
    private static final Color COLOR_AZUL_PRIMARIO = new Color(2, 132, 199);
    private static final Color COLOR_TEXTO_TITULO = new Color(30, 41, 59);
    private static final Color COLOR_TEXTO_MUTED = new Color(100, 116, 139);

    private final RepositorioVeterinaria repo = RepositorioVeterinaria.getInstancia();

    // KPIs
    private JLabel lblKpiTotalProductos;
    private JLabel lblKpiValorInventario;
    private JLabel lblKpiBajoStock;
    private JLabel lblKpiRecetaRetenida;

    // Formulario de Producto
    private JTextField txtCodigo;
    private JTextField txtNombre;
    private JComboBox<String> cbCategoria;
    private JTextField txtPrincipioActivo;
    private JTextField txtPresentacion;
    private JComboBox<String> cbEspecie;
    private JTextField txtPrecioCosto;
    private JTextField txtPrecioVenta;
    private JTextField txtStockActual;
    private JTextField txtStockMinimo;
    private JCheckBox chkReceta;
    private JCheckBox chkCadenaFrio;
    private JTextField txtProveedor;

    // Tabla y Filtros
    private JTextField txtBuscar;
    private JComboBox<String> cbFiltroCategoria;
    private JTable tablaProductos;
    private DefaultTableModel modeloProductos;
    private JLabel lblContadorProductos;
    private List<ProductoFarmacia> listaActual;

    public VistaCatalogoProductosPanel() {
        setLayout(new BorderLayout());
        setOpaque(false);

        JPanel contenido = new JPanel();
        contenido.setOpaque(false);
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setBorder(new EmptyBorder(12, 18, 14, 18));

        // 1. Cabecera
        contenido.add(crearCabeceraVista());
        contenido.add(Box.createVerticalStrut(8));

        // 2. Fila de KPIs
        contenido.add(crearFilaKpis());
        contenido.add(Box.createVerticalStrut(10));

        // 3. Doble Columna: Formulario y Catálogo
        contenido.add(crearDobleColumna());

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(contenido, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(wrapper);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);

        recargarDatos();
    }

    private JPanel crearCabeceraVista() {
        JPanel cab = new JPanel(new BorderLayout());
        cab.setOpaque(false);

        JPanel izq = new JPanel();
        izq.setOpaque(false);
        izq.setLayout(new BoxLayout(izq, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("Catálogo de Productos y Fármacos Veterinarios");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 17));
        titulo.setForeground(COLOR_TEXTO_TITULO);

        JLabel subtitulo = new JLabel("Módulo 5.1 · Registro maestro de medicamentos, biológicos, alimentos clínicos, insumos y control de precios");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitulo.setForeground(COLOR_TEXTO_MUTED);

        izq.add(titulo);
        izq.add(Box.createVerticalStrut(2));
        izq.add(subtitulo);
        cab.add(izq, BorderLayout.CENTER);

        JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        der.setOpaque(false);

        JLabel badgeFarmacia = new JLabel(" Regencia Farmacéutica Activa · Happy Pets ", SwingConstants.CENTER);
        badgeFarmacia.setFont(new Font("Segoe UI", Font.BOLD, 11));
        badgeFarmacia.setForeground(new Color(2, 132, 199));
        badgeFarmacia.setOpaque(true);
        badgeFarmacia.setBackground(new Color(224, 242, 254));
        badgeFarmacia.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(186, 230, 253), 1, true),
                new EmptyBorder(4, 10, 4, 10)
        ));
        der.add(badgeFarmacia);

        cab.add(der, BorderLayout.EAST);
        return cab;
    }

    private JPanel crearFilaKpis() {
        JPanel kpis = new JPanel(new GridLayout(1, 4, 10, 0));
        kpis.setOpaque(false);

        lblKpiTotalProductos = new JLabel("0", SwingConstants.LEFT);
        lblKpiValorInventario = new JLabel("S/. 0", SwingConstants.LEFT);
        lblKpiBajoStock = new JLabel("0", SwingConstants.LEFT);
        lblKpiRecetaRetenida = new JLabel("0", SwingConstants.LEFT);

        kpis.add(crearTarjetaKpi("Productos Registrados", lblKpiTotalProductos, "Catálogo farmacéutico activo", new Color(14, 165, 233), Iconos.crearIconoPildora(18, new Color(14, 165, 233))));
        kpis.add(crearTarjetaKpi("Valorización Stock (PVP)", lblKpiValorInventario, "Total inventario disponible", new Color(16, 185, 129), Iconos.crearIconoFactura(16, new Color(16, 185, 129))));
        kpis.add(crearTarjetaKpi("Alerta de Reposición", lblKpiBajoStock, "Stock menor al mínimo", new Color(245, 158, 11), Iconos.crearIconoAlertaMerma(16, new Color(245, 158, 11))));
        kpis.add(crearTarjetaKpi("Fármacos Controlados", lblKpiRecetaRetenida, "Venta bajo receta médica", new Color(147, 51, 234), Iconos.crearIconoDocumento(16, new Color(147, 51, 234))));

        return kpis;
    }

    private JPanel crearTarjetaKpi(String titulo, JLabel lblValor, String subtitulo, Color colorAcento, Icon icono) {
        JPanel card = new JPanel(new BorderLayout(8, 4)) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(COLOR_BORDE);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(8, 12, 8, 12));

        JPanel izq = new JPanel();
        izq.setOpaque(false);
        izq.setLayout(new BoxLayout(izq, BoxLayout.Y_AXIS));

        JLabel t = new JLabel(titulo);
        t.setFont(new Font("Segoe UI", Font.BOLD, 10));
        t.setForeground(COLOR_TEXTO_MUTED);

        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblValor.setForeground(COLOR_TEXTO_TITULO);

        JLabel sub = new JLabel(subtitulo);
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        sub.setForeground(COLOR_TEXTO_MUTED);

        izq.add(t);
        izq.add(Box.createVerticalStrut(2));
        izq.add(lblValor);
        izq.add(Box.createVerticalStrut(1));
        izq.add(sub);

        card.add(izq, BorderLayout.CENTER);

        if (icono != null) {
            JLabel lblIcono = new JLabel(icono);
            lblIcono.setVerticalAlignment(SwingConstants.TOP);
            card.add(lblIcono, BorderLayout.EAST);
        }

        return card;
    }

    private JPanel crearDobleColumna() {
        JPanel fila = new JPanel(new BorderLayout(14, 0));
        fila.setOpaque(false);

        // Columna Izquierda: Formulario
        JPanel colIzquierda = crearPanelFormularioProducto();
        colIzquierda.setPreferredSize(new Dimension(420, 560));
        fila.add(colIzquierda, BorderLayout.WEST);

        // Columna Derecha: Catálogo y Búsqueda
        JPanel colDerecha = crearPanelCatalogoTabla();
        fila.add(colDerecha, BorderLayout.CENTER);

        return fila;
    }

    private JPanel crearPanelFormularioProducto() {
        JPanel card = new JPanel(new BorderLayout()) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(COLOR_BORDE);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(12, 14, 12, 14));

        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));

        // Título del formulario
        JLabel lblTitulo = new JLabel("Formulario de Producto / Fármaco");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTitulo.setForeground(COLOR_TEXTO_TITULO);
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(lblTitulo);

        JLabel lblSub = new JLabel("Definición de atributos clínicos, presentación y valores comerciales");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblSub.setForeground(COLOR_TEXTO_MUTED);
        lblSub.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(lblSub);
        form.add(Box.createVerticalStrut(10));

        // Código y Categoría en Grid
        JPanel gridCodCat = new JPanel(new GridLayout(1, 2, 8, 0));
        gridCodCat.setOpaque(false);
        gridCodCat.setAlignmentX(Component.LEFT_ALIGNMENT);
        gridCodCat.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));

        JPanel pCod = new JPanel();
        pCod.setOpaque(false);
        pCod.setAlignmentX(Component.LEFT_ALIGNMENT);
        pCod.setLayout(new BoxLayout(pCod, BoxLayout.Y_AXIS));
        pCod.add(crearEtiquetaCampo("Código Producto:"));
        txtCodigo = new JTextField();
        estilizarControl(txtCodigo);
        pCod.add(txtCodigo);
        gridCodCat.add(pCod);

        JPanel pCat = new JPanel();
        pCat.setOpaque(false);
        pCat.setAlignmentX(Component.LEFT_ALIGNMENT);
        pCat.setLayout(new BoxLayout(pCat, BoxLayout.Y_AXIS));
        pCat.add(crearEtiquetaCampo("Categoría:"));
        cbCategoria = new JComboBox<>(new String[]{
                "Antiparasitario",
                "Antibiótico",
                "Analgésico / AINE",
                "Vacuna / Biológico",
                "Alimento Clínico",
                "Anestésico / Controlado",
                "Material Quirúrgico / Insumo"
        });
        estilizarControl(cbCategoria);
        pCat.add(cbCategoria);
        gridCodCat.add(pCat);

        form.add(gridCodCat);
        form.add(Box.createVerticalStrut(6));

        // Nombre Comercial
        form.add(crearEtiquetaCampo("Nombre Comercial del Producto:"));
        txtNombre = new JTextField();
        estilizarControl(txtNombre);
        form.add(txtNombre);
        form.add(Box.createVerticalStrut(6));

        // Principio Activo y Presentación
        JPanel gridPrincPres = new JPanel(new GridLayout(1, 2, 8, 0));
        gridPrincPres.setOpaque(false);
        gridPrincPres.setAlignmentX(Component.LEFT_ALIGNMENT);
        gridPrincPres.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));

        JPanel pPrinc = new JPanel();
        pPrinc.setOpaque(false);
        pPrinc.setAlignmentX(Component.LEFT_ALIGNMENT);
        pPrinc.setLayout(new BoxLayout(pPrinc, BoxLayout.Y_AXIS));
        pPrinc.add(crearEtiquetaCampo("Principio Activo:"));
        txtPrincipioActivo = new JTextField();
        estilizarControl(txtPrincipioActivo);
        pPrinc.add(txtPrincipioActivo);
        gridPrincPres.add(pPrinc);

        JPanel pPres = new JPanel();
        pPres.setOpaque(false);
        pPres.setAlignmentX(Component.LEFT_ALIGNMENT);
        pPres.setLayout(new BoxLayout(pPres, BoxLayout.Y_AXIS));
        pPres.add(crearEtiquetaCampo("Presentación / Envase:"));
        txtPresentacion = new JTextField();
        estilizarControl(txtPresentacion);
        pPres.add(txtPresentacion);
        gridPrincPres.add(pPres);

        form.add(gridPrincPres);
        form.add(Box.createVerticalStrut(6));

        // Especie Destino y Proveedor
        JPanel gridEspProv = new JPanel(new GridLayout(1, 2, 8, 0));
        gridEspProv.setOpaque(false);
        gridEspProv.setAlignmentX(Component.LEFT_ALIGNMENT);
        gridEspProv.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));

        JPanel pEsp = new JPanel();
        pEsp.setOpaque(false);
        pEsp.setAlignmentX(Component.LEFT_ALIGNMENT);
        pEsp.setLayout(new BoxLayout(pEsp, BoxLayout.Y_AXIS));
        pEsp.add(crearEtiquetaCampo("Especie Destino:"));
        cbEspecie = new JComboBox<>(new String[]{"Canino", "Felino", "Mixto Canino/Felino"});
        estilizarControl(cbEspecie);
        pEsp.add(cbEspecie);
        gridEspProv.add(pEsp);

        JPanel pProv = new JPanel();
        pProv.setOpaque(false);
        pProv.setAlignmentX(Component.LEFT_ALIGNMENT);
        pProv.setLayout(new BoxLayout(pProv, BoxLayout.Y_AXIS));
        pProv.add(crearEtiquetaCampo("Laboratorio / Proveedor:"));
        txtProveedor = new JTextField("Laboratorios Zoetis Perú S.A.C.");
        estilizarControl(txtProveedor);
        pProv.add(txtProveedor);
        gridEspProv.add(pProv);

        form.add(gridEspProv);
        form.add(Box.createVerticalStrut(6));

        // Precios y Stock (Grid 4 columnas)
        JPanel gridPrecios = new JPanel(new GridLayout(1, 4, 6, 0));
        gridPrecios.setOpaque(false);
        gridPrecios.setAlignmentX(Component.LEFT_ALIGNMENT);
        gridPrecios.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));

        JPanel pCosto = new JPanel();
        pCosto.setOpaque(false);
        pCosto.setAlignmentX(Component.LEFT_ALIGNMENT);
        pCosto.setLayout(new BoxLayout(pCosto, BoxLayout.Y_AXIS));
        pCosto.add(crearEtiquetaCampo("Costo S/.:"));
        txtPrecioCosto = new JTextField("30.00");
        estilizarControl(txtPrecioCosto);
        pCosto.add(txtPrecioCosto);
        gridPrecios.add(pCosto);

        JPanel pVenta = new JPanel();
        pVenta.setOpaque(false);
        pVenta.setAlignmentX(Component.LEFT_ALIGNMENT);
        pVenta.setLayout(new BoxLayout(pVenta, BoxLayout.Y_AXIS));
        pVenta.add(crearEtiquetaCampo("Venta S/.:"));
        txtPrecioVenta = new JTextField("50.00");
        estilizarControl(txtPrecioVenta);
        pVenta.add(txtPrecioVenta);
        gridPrecios.add(pVenta);

        JPanel pStock = new JPanel();
        pStock.setOpaque(false);
        pStock.setAlignmentX(Component.LEFT_ALIGNMENT);
        pStock.setLayout(new BoxLayout(pStock, BoxLayout.Y_AXIS));
        pStock.add(crearEtiquetaCampo("Stock Actual:"));
        txtStockActual = new JTextField("20");
        estilizarControl(txtStockActual);
        pStock.add(txtStockActual);
        gridPrecios.add(pStock);

        JPanel pMin = new JPanel();
        pMin.setOpaque(false);
        pMin.setAlignmentX(Component.LEFT_ALIGNMENT);
        pMin.setLayout(new BoxLayout(pMin, BoxLayout.Y_AXIS));
        pMin.add(crearEtiquetaCampo("Stock Mín.:"));
        txtStockMinimo = new JTextField("5");
        estilizarControl(txtStockMinimo);
        pMin.add(txtStockMinimo);
        gridPrecios.add(pMin);

        form.add(gridPrecios);
        form.add(Box.createVerticalStrut(8));

        // Checkboxes Regulatorios
        JPanel pCheckboxes = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        pCheckboxes.setOpaque(false);
        pCheckboxes.setAlignmentX(Component.LEFT_ALIGNMENT);
        pCheckboxes.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));

        chkReceta = new JCheckBox("Venta Bajo Receta Retenida");
        chkReceta.setFont(new Font("Segoe UI", Font.BOLD, 10));
        chkReceta.setForeground(new Color(126, 34, 206));
        chkReceta.setOpaque(false);

        chkCadenaFrio = new JCheckBox("Cadena de Frío (2°C - 8°C)");
        chkCadenaFrio.setFont(new Font("Segoe UI", Font.BOLD, 10));
        chkCadenaFrio.setForeground(new Color(3, 105, 161));
        chkCadenaFrio.setOpaque(false);

        pCheckboxes.add(chkReceta);
        pCheckboxes.add(chkCadenaFrio);
        form.add(pCheckboxes);
        form.add(Box.createVerticalStrut(12));

        // Botones de acción del formulario
        JPanel pBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        pBotones.setOpaque(false);
        pBotones.setAlignmentX(Component.LEFT_ALIGNMENT);
        pBotones.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        JButton btnLimpiar = crearBoton("Nuevo / Limpiar", false, this::limpiarFormulario);
        JButton btnGuardar = crearBoton("Guardar Producto", true, this::guardarProducto);

        pBotones.add(btnLimpiar);
        pBotones.add(btnGuardar);
        form.add(pBotones);

        card.add(form, BorderLayout.CENTER);
        return card;
    }

    private JPanel crearPanelCatalogoTabla() {
        JPanel card = new JPanel(new BorderLayout(0, 8)) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(COLOR_BORDE);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(12, 14, 12, 14));

        // Cabecera superior con buscador y filtro
        JPanel top = new JPanel(new BorderLayout(8, 0));
        top.setOpaque(false);

        JPanel izq = new JPanel();
        izq.setOpaque(false);
        izq.setLayout(new BoxLayout(izq, BoxLayout.Y_AXIS));

        JLabel lblTit = new JLabel("Inventario Maestro de Fármacos e Insumos");
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTit.setForeground(COLOR_TEXTO_TITULO);

        lblContadorProductos = new JLabel("0 productos listados");
        lblContadorProductos.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblContadorProductos.setForeground(COLOR_TEXTO_MUTED);

        izq.add(lblTit);
        izq.add(Box.createVerticalStrut(1));
        izq.add(lblContadorProductos);
        top.add(izq, BorderLayout.WEST);

        // Barra de búsqueda y categoría
        JPanel derFiltros = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        derFiltros.setOpaque(false);

        txtBuscar = new JTextField(12);
        txtBuscar.setText("Buscar producto...");
        estilizarControl(txtBuscar);
        txtBuscar.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override public void focusGained(java.awt.event.FocusEvent e) { if ("Buscar producto...".equals(txtBuscar.getText())) txtBuscar.setText(""); }
            @Override public void focusLost(java.awt.event.FocusEvent e) { if (txtBuscar.getText().trim().isEmpty()) txtBuscar.setText("Buscar producto..."); }
        });
        txtBuscar.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override public void keyReleased(java.awt.event.KeyEvent e) { filtrarTabla(); }
        });
        derFiltros.add(txtBuscar);

        cbFiltroCategoria = new JComboBox<>(new String[]{
                "Todas las Categorías",
                "Antiparasitario",
                "Antibiótico",
                "Analgésico / AINE",
                "Vacuna / Biológico",
                "Alimento Clínico",
                "Anestésico / Controlado",
                "Material Quirúrgico / Insumo"
        });
        estilizarControl(cbFiltroCategoria);
        cbFiltroCategoria.addActionListener(e -> filtrarTabla());
        derFiltros.add(cbFiltroCategoria);

        top.add(derFiltros, BorderLayout.EAST);
        card.add(top, BorderLayout.NORTH);

        // Tabla de Productos
        String[] columnas = {"Código", "Producto / Fármaco", "Categoría", "Especie", "Stock", "P. Venta", "Control", "Estado"};
        modeloProductos = new DefaultTableModel(columnas, 0) {
            private static final long serialVersionUID = 1L;
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tablaProductos = new JTable(modeloProductos);
        tablaProductos.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        tablaProductos.setRowHeight(26);
        tablaProductos.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        tablaProductos.getTableHeader().setBackground(Color.WHITE);
        tablaProductos.getTableHeader().setForeground(new Color(71, 85, 105));
        tablaProductos.setSelectionBackground(new Color(224, 242, 254));
        tablaProductos.setSelectionForeground(new Color(3, 105, 161));
        tablaProductos.setShowGrid(false);
        tablaProductos.setIntercellSpacing(new Dimension(0, 0));

        tablaProductos.getColumnModel().getColumn(0).setPreferredWidth(65);
        tablaProductos.getColumnModel().getColumn(1).setPreferredWidth(170);
        tablaProductos.getColumnModel().getColumn(2).setPreferredWidth(100);
        tablaProductos.getColumnModel().getColumn(3).setPreferredWidth(80);
        tablaProductos.getColumnModel().getColumn(4).setPreferredWidth(50);
        tablaProductos.getColumnModel().getColumn(5).setPreferredWidth(65);
        tablaProductos.getColumnModel().getColumn(6).setPreferredWidth(85);
        tablaProductos.getColumnModel().getColumn(7).setPreferredWidth(85);

        // Renderizador de Estado
        tablaProductos.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
            private static final long serialVersionUID = 1L;
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                l.setHorizontalAlignment(SwingConstants.CENTER);
                String st = value != null ? value.toString() : "";
                if ("Disponible".equalsIgnoreCase(st)) {
                    l.setForeground(new Color(16, 185, 129));
                    l.setBackground(new Color(209, 250, 229));
                } else if ("Bajo Stock".equalsIgnoreCase(st)) {
                    l.setForeground(new Color(245, 158, 11));
                    l.setBackground(new Color(254, 243, 199));
                } else {
                    l.setForeground(new Color(220, 38, 38));
                    l.setBackground(new Color(254, 226, 226));
                }
                l.setOpaque(true);
                return l;
            }
        });

        JScrollPane scrollTabla = new JScrollPane(tablaProductos);
        scrollTabla.setBorder(BorderFactory.createLineBorder(COLOR_BORDE, 1));
        scrollTabla.getViewport().setBackground(Color.WHITE);
        card.add(scrollTabla, BorderLayout.CENTER);

        // Barra inferior de acciones
        JPanel pAcciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        pAcciones.setOpaque(false);

        JButton btnEditar = crearBoton("Editar Datos", false, this::cargarParaEdicion);
        btnEditar.setIcon(Iconos.crearIconoLapiz(14, COLOR_AZUL_PRIMARIO));

        JButton btnProspecto = crearBoton("Ficha Técnica / Prospecto", false, this::mostrarProspectoClinico);
        btnProspecto.setIcon(Iconos.crearIconoDocumento(14, new Color(99, 102, 241)));

        JButton btnEtiqueta = crearBoton("Etiqueta Dispensación", false, this::generarEtiquetaDispensacion);
        btnEtiqueta.setIcon(Iconos.crearIconoImprimir(14, new Color(16, 185, 129)));

        JButton btnEliminar = crearBoton("Eliminar", false, this::eliminarProducto);
        btnEliminar.setIcon(Iconos.crearIconoCruzMedica(14, new Color(220, 38, 38)));

        pAcciones.add(btnEditar);
        pAcciones.add(btnProspecto);
        pAcciones.add(btnEtiqueta);
        pAcciones.add(btnEliminar);

        card.add(pAcciones, BorderLayout.SOUTH);
        return card;
    }

    private JLabel crearEtiquetaCampo(String texto) {
        JLabel l = new JLabel(texto, SwingConstants.LEFT);
        l.setFont(new Font("Segoe UI", Font.BOLD, 10));
        l.setForeground(new Color(71, 85, 105));
        l.setBorder(new EmptyBorder(0, 0, 2, 0));
        l.setHorizontalAlignment(SwingConstants.LEFT);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        l.setMaximumSize(new Dimension(Integer.MAX_VALUE, 18));
        return l;
    }

    private void estilizarControl(Component c) {
        c.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        c.setPreferredSize(new Dimension(c.getPreferredSize().width, 27));
        c.setMaximumSize(new Dimension(Integer.MAX_VALUE, 27));
        if (c instanceof javax.swing.JComponent jc) {
            jc.setAlignmentX(Component.LEFT_ALIGNMENT);
        }
        if (c instanceof JTextField) {
            ((JTextField) c).setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(COLOR_BORDE, 1, true),
                    new EmptyBorder(2, 6, 2, 6)
            ));
        } else if (c instanceof JComboBox) {
            c.setBackground(Color.WHITE);
        }
    }

    private JButton crearBoton(String texto, boolean primario, Runnable accion) {
        JButton btn = new happypets.ui.BotonAsset(texto) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (primario) {
                    g2.setColor(getModel().isRollover() ? new Color(3, 105, 161) : COLOR_AZUL_PRIMARIO);
                } else {
                    g2.setColor(getModel().isRollover() ? new Color(241, 245, 249) : Color.WHITE);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                if (!primario) {
                    g2.setColor(COLOR_BORDE);
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 6, 6);
                }
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btn.setForeground(primario ? Color.WHITE : new Color(51, 65, 85));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(5, 12, 5, 12));
        btn.setPreferredSize(new Dimension(btn.getPreferredSize().width, 28));
        btn.addActionListener(e -> accion.run());
        return btn;
    }

    public void recargarDatos() {
        listaActual = repo.getProductosFarmacia();
        filtrarTabla();

        // KPIs
        long bajo = listaActual.stream().filter(p -> p.getStockActual() <= p.getStockMinimo()).count();
        long receta = listaActual.stream().filter(ProductoFarmacia::isRequiereReceta).count();
        double valorStock = listaActual.stream().mapToDouble(p -> p.getStockActual() * p.getPrecioVenta()).sum();

        lblKpiTotalProductos.setText(String.valueOf(listaActual.size()));
        lblKpiValorInventario.setText("S/. " + String.format("%.0f", valorStock));
        lblKpiBajoStock.setText(String.valueOf(bajo));
        lblKpiRecetaRetenida.setText(String.valueOf(receta));

        if (txtCodigo.getText().trim().isEmpty()) {
            txtCodigo.setText("PROD-" + String.format("%03d", listaActual.size() + 1));
        }
    }

    private void filtrarTabla() {
        if (listaActual == null) return;
        modeloProductos.setRowCount(0);

        String texto = txtBuscar != null && !"Buscar producto...".equals(txtBuscar.getText()) ? txtBuscar.getText().trim().toLowerCase() : "";
        String catFiltro = cbFiltroCategoria != null ? (String) cbFiltroCategoria.getSelectedItem() : "Todas las Categorías";

        int cont = 0;
        for (ProductoFarmacia p : listaActual) {
            if (!"Todas las Categorías".equalsIgnoreCase(catFiltro)) {
                if (!p.getCategoria().equalsIgnoreCase(catFiltro)) continue;
            }
            if (!texto.isEmpty()) {
                boolean match = p.getCodigo().toLowerCase().contains(texto) ||
                        p.getNombre().toLowerCase().contains(texto) ||
                        p.getPrincipioActivo().toLowerCase().contains(texto) ||
                        p.getCategoria().toLowerCase().contains(texto);
                if (!match) continue;
            }

            cont++;
            String control = (p.isRequiereReceta() ? "Receta" : "Libre") + (p.isCadenaFrio() ? " · Frío" : "");

            modeloProductos.addRow(new Object[]{
                    p.getCodigo(),
                    p.getNombre(),
                    p.getCategoria(),
                    p.getEspecieDestino(),
                    p.getStockActual() + " un.",
                    "S/. " + String.format("%.2f", p.getPrecioVenta()),
                    control,
                    p.getEstado()
            });
        }
        lblContadorProductos.setText(cont + " productos coincidentes");
    }

    private void guardarProducto() {
        String cod = txtCodigo.getText().trim();
        String nom = txtNombre.getText().trim();
        if (cod.isEmpty() || nom.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Complete el código y nombre comercial del producto.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double costo = 20.0;
        double venta = 35.0;
        int stock = 10;
        int min = 5;

        try { costo = Double.parseDouble(txtPrecioCosto.getText().trim()); } catch (Exception ignored) {}
        try { venta = Double.parseDouble(txtPrecioVenta.getText().trim()); } catch (Exception ignored) {}
        try { stock = Integer.parseInt(txtStockActual.getText().trim()); } catch (Exception ignored) {}
        try { min = Integer.parseInt(txtStockMinimo.getText().trim()); } catch (Exception ignored) {}

        ProductoFarmacia p = new ProductoFarmacia(
                cod,
                nom,
                (String) cbCategoria.getSelectedItem(),
                txtPrincipioActivo.getText().trim(),
                txtPresentacion.getText().trim(),
                (String) cbEspecie.getSelectedItem(),
                chkReceta.isSelected(),
                chkCadenaFrio.isSelected(),
                costo,
                venta,
                stock,
                min,
                "LOT-2024-NVO",
                null,
                txtProveedor.getText().trim(),
                "Disponible"
        );

        repo.guardarProductoFarmacia(p);
        recargarDatos();
        limpiarFormulario();

        JOptionPane.showMessageDialog(this, "Producto " + nom + " guardado con éxito en el catálogo.", "Catálogo Actualizado", JOptionPane.INFORMATION_MESSAGE);
    }

    private void cargarParaEdicion() {
        int fila = tablaProductos.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto de la tabla para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String cod = (String) modeloProductos.getValueAt(fila, 0);
        Optional<ProductoFarmacia> opt = repo.getProductosFarmacia().stream().filter(p -> p.getCodigo().equalsIgnoreCase(cod)).findFirst();
        if (!opt.isPresent()) return;

        ProductoFarmacia p = opt.get();
        txtCodigo.setText(p.getCodigo());
        txtNombre.setText(p.getNombre());
        cbCategoria.setSelectedItem(p.getCategoria());
        txtPrincipioActivo.setText(p.getPrincipioActivo());
        txtPresentacion.setText(p.getPresentacion());
        cbEspecie.setSelectedItem(p.getEspecieDestino());
        txtPrecioCosto.setText(String.format("%.2f", p.getPrecioCosto()));
        txtPrecioVenta.setText(String.format("%.2f", p.getPrecioVenta()));
        txtStockActual.setText(String.valueOf(p.getStockActual()));
        txtStockMinimo.setText(String.valueOf(p.getStockMinimo()));
        chkReceta.setSelected(p.isRequiereReceta());
        chkCadenaFrio.setSelected(p.isCadenaFrio());
        txtProveedor.setText(p.getProveedor());
    }

    private void eliminarProducto() {
        int fila = tablaProductos.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto para dar de baja.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String cod = (String) modeloProductos.getValueAt(fila, 0);
        int r = JOptionPane.showConfirmDialog(this, "¿Está seguro de eliminar del catálogo el producto " + cod + "?", "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);
        if (r == JOptionPane.YES_OPTION) {
            repo.eliminarProductoFarmacia(cod);
            recargarDatos();
            limpiarFormulario();
        }
    }

    private void mostrarProspectoClinico() {
        int fila = tablaProductos.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un fármaco para consultar su prospecto y ficha técnica.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String cod = (String) modeloProductos.getValueAt(fila, 0);
        Optional<ProductoFarmacia> opt = repo.getProductosFarmacia().stream().filter(p -> p.getCodigo().equalsIgnoreCase(cod)).findFirst();
        if (!opt.isPresent()) return;

        ProductoFarmacia p = opt.get();
        String prospecto = "FICHA TÉCNICA Y FARMACOVIGILANCIA VETERINARIA\n" +
                "HAPPY PETS - SERVICIO DE FARMACIA Y DISPENSACIÓN REGULADA\n" +
                "=========================================================================\n\n" +
                "CÓDIGO INTERNO   : " + p.getCodigo() + "\n" +
                "PRODUCTO / MARCA : " + p.getNombre() + "\n" +
                "CATEGORÍA        : " + p.getCategoria() + "\n" +
                "PRINCIPIO ACTIVO : " + p.getPrincipioActivo() + "\n" +
                "PRESENTACIÓN     : " + p.getPresentacion() + "\n" +
                "ESPECIE DESTINO  : " + p.getEspecieDestino() + "\n\n" +
                "CONDICIONES DE CONSERVACIÓN Y VENTA:\n" +
                "  • Dispensación : " + (p.isRequiereReceta() ? "VENTA EXCLUSIVA BAJO RECETA RETENIDA" : "VENTA LIBRE MOSTRADOR") + "\n" +
                "  • Temperatura  : " + (p.isCadenaFrio() ? "REFRIGERACIÓN ESTRICTA (2°C A 8°C - NO CONGELAR)" : "AMBIENTE (MENOR A 25°C, LUGAR SECO Y FRESCO)") + "\n" +
                "  • Laboratorio  : " + p.getProveedor() + "\n\n" +
                "INDICACIONES TERAPÉUTICAS:\n" +
                "Indicado en la prevención y tratamiento según prescripción del Médico Veterinario Colegiado.\n" +
                "Mantener fuera del alcance de los niños y de animales domésticos sin indicación médica.\n" +
                "=========================================================================\n" +
                "Dirección Técnica Farmacéutica · Happy Pets Clínicas Veterinarias";

        JTextArea ta = new JTextArea(prospecto);
        ta.setFont(new Font("Consolas", Font.PLAIN, 11));
        ta.setEditable(false);
        JScrollPane sp = new JScrollPane(ta);
        sp.setPreferredSize(new Dimension(540, 320));

        JOptionPane.showMessageDialog(this, sp, "Ficha Técnica Farmacéutica", JOptionPane.INFORMATION_MESSAGE);
    }

    private void generarEtiquetaDispensacion() {
        int fila = tablaProductos.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto para generar la etiqueta de dispensación clínica.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String cod = (String) modeloProductos.getValueAt(fila, 0);
        Optional<ProductoFarmacia> opt = repo.getProductosFarmacia().stream().filter(p -> p.getCodigo().equalsIgnoreCase(cod)).findFirst();
        if (!opt.isPresent()) return;

        ProductoFarmacia p = opt.get();
        String etiqueta = "╔═══════════════════════════════════════════════════════╗\n" +
                "║             VETERINARIA HAPPY PETS S.A.C.            ║\n" +
                "║        Av. Primavera 1230, Surco - Tel. 984 552 110   ║\n" +
                "╠═══════════════════════════════════════════════════════╣\n" +
                "║ PACIENTE: ____________________  TUTOR: ______________ ║\n" +
                "║ FECHA   : " + java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")) + "        MÉDICO: Dr(a). Veterinario/a ║\n" +
                "╠═══════════════════════════════════════════════════════╣\n" +
                "║ MEDICAMENTO: " + String.format("%-39s", p.getNombre()) + "║\n" +
                "║ DOSIS: _______________________ FRECUENCIA: __________ ║\n" +
                "║ VÍA  : [ ] Oral  [ ] Tópica  [ ] Subcutánea  DURACIÓN: ║\n" +
                "╠═══════════════════════════════════════════════════════╣\n" +
                "║ CONSERVACIÓN: " + (p.isCadenaFrio() ? "MANTENER REFRIGERADO (2°C - 8°C)       " : "CONSERVAR EN LUGAR SECO Y FRESCO        ") + "║\n" +
                "╚═══════════════════════════════════════════════════════╝";

        JTextArea ta = new JTextArea(etiqueta);
        ta.setFont(new Font("Consolas", Font.BOLD, 12));
        ta.setEditable(false);
        JScrollPane sp = new JScrollPane(ta);
        sp.setPreferredSize(new Dimension(500, 220));

        JOptionPane.showMessageDialog(this, sp, "Etiqueta Térmica de Dispensación al Paciente", JOptionPane.INFORMATION_MESSAGE);
    }

    private void limpiarFormulario() {
        txtCodigo.setText("PROD-" + String.format("%03d", repo.getProductosFarmacia().size() + 1));
        txtNombre.setText("");
        txtPrincipioActivo.setText("");
        txtPresentacion.setText("");
        txtPrecioCosto.setText("20.00");
        txtPrecioVenta.setText("35.00");
        txtStockActual.setText("10");
        txtStockMinimo.setText("5");
        chkReceta.setSelected(false);
        chkCadenaFrio.setSelected(false);
        tablaProductos.clearSelection();
    }
}
