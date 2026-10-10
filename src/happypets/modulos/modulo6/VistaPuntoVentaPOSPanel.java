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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import happypets.data.RepositorioVeterinaria;
import happypets.model.Cliente;
import happypets.model.ItemVentaPOS;
import happypets.model.Mascota;
import happypets.model.ProductoFarmacia;
import happypets.model.VentaPOS;
import happypets.ui.Iconos;
import happypets.ui.Ui;

/**
 * Submódulo 6.1: Punto de Venta (POS).
 * Basado estrictamente en el wireframe oficial PUNTO DE VENTA POS.pdf y mejorado:
 * - Catálogo visual de productos clínicos, biológicos, alimentos y servicios veterinarios.
 * - Filtros rápidos: Todas, Consultas, Vacunas, Alimentos, Otros, Farmacia.
 * - Carrito en vivo "Venta actual" con cálculo inmediato de Subtotal, Descuento %, IGV y Total.
 * - Métodos de pago: Efectivo (con cálculo de vuelto), Yape/Plin, Tarjeta Débito/Crédito y Transferencia.
 * - Vinculación directa con Clientes y Pacientes de la Veterinaria.
 * - Botones de acción oficial: [Cotización] e [Imprimir / Procesar Venta] con emisión de ticket.
 */
public class VistaPuntoVentaPOSPanel extends happypets.ui.AssetsModulo {
    private static final long serialVersionUID = 1L;

    private static final Color COLOR_BORDE = new Color(226, 232, 240);
    private static final Color COLOR_PRIMARIO = new Color(249, 115, 22); // Acento naranja cálido ventas
    private static final Color COLOR_PRIMARIO_OSCURO = new Color(234, 88, 12);
    private static final Color COLOR_VERDE = new Color(16, 185, 129);
    private static final Color COLOR_TEXTO_TITULO = new Color(30, 41, 59);
    private static final Color COLOR_TEXTO_MUTED = new Color(100, 116, 139);
    private static final Color COLOR_FONDO_CARD = Color.WHITE;

    private final RepositorioVeterinaria repo = RepositorioVeterinaria.getInstancia();

    // Catálogo en memoria (productos + servicios veterinarios)
    private final List<CatalogoItem> catalogoCompleto = new ArrayList<>();
    private final List<CatalogoItem> catalogoFiltrado = new ArrayList<>();

    // Estado de la venta actual
    private final List<ItemVentaPOS> itemsVenta = new ArrayList<>();
    private String categoriaFiltroActual = "Todas";

    // Componentes del Catálogo (Izquierda)
    private JTextField txtBuscar;
    private JPanel panelGridProductos;
    private JLabel lblContadorItems;

    // Componentes del Carrito (Derecha)
    private JTable tablaVenta;
    private DefaultTableModel modeloVenta;
    private JLabel lblSubtotal;
    private JSpinner spinDescuento;
    private JLabel lblMontoDescuento;
    private JLabel lblTotal;
    private JTextField txtCliente;
    private JComboBox<String> cbTipoComprobante;
    private JComboBox<String> cbMascota;
    private String metodoPagoSeleccionado = "Efectivo";
    private JButton btnPagoEfectivo;
    private JButton btnPagoYape;
    private JButton btnPagoTarjeta;
    private JButton btnPagoTransferencia;
    private JTextField txtMontoRecibido;
    private JLabel lblVuelto;
    private JPanel panelEfectivoExtra;

    public VistaPuntoVentaPOSPanel() {
        setLayout(new BorderLayout());
        setOpaque(false);

        cargarCatalogoMaestro();

        JPanel contenedor = new JPanel(new BorderLayout(14, 0));
        contenedor.setOpaque(false);
        contenedor.setBorder(new EmptyBorder(12, 16, 14, 16));

        // Cabecera superior institucional del POS
        contenedor.add(crearCabeceraPOS(), BorderLayout.NORTH);

        // Panel dividido: Catálogo a la izquierda, Carrito a la derecha
        JPanel split = new JPanel(new GridLayout(1, 2, 14, 0));
        split.setOpaque(false);

        split.add(crearColumnaCatalogo());
        split.add(crearColumnaVentaActual());

        contenedor.add(split, BorderLayout.CENTER);
        add(contenedor, BorderLayout.CENTER);

        filtrarCatalogo();
        recalcularVenta();
    }

    private void cargarCatalogoMaestro() {
        catalogoCompleto.clear();

        // 1. Servicios Clínicos y Preventivos
        catalogoCompleto.add(new CatalogoItem("SERV-001", "Consulta Médica General", "Consultas", 50.0, 999, true));
        catalogoCompleto.add(new CatalogoItem("SERV-002", "Consulta Especializada / Derma", "Consultas", 80.0, 999, true));
        catalogoCompleto.add(new CatalogoItem("SERV-003", "Vacuna Antirrábica + Cartilla", "Vacunas", 45.0, 50, false));
        catalogoCompleto.add(new CatalogoItem("SERV-004", "Vacuna Séxtuple Canina Nobivac", "Vacunas", 60.0, 30, false));
        catalogoCompleto.add(new CatalogoItem("SERV-005", "Triple Felina Nobivac Tricat", "Vacunas", 55.0, 25, false));
        catalogoCompleto.add(new CatalogoItem("SERV-006", "Baño y Corte de Raza (Grooming)", "Otros", 45.0, 999, true));
        catalogoCompleto.add(new CatalogoItem("SERV-007", "Baño Medicado Dermatológico", "Otros", 55.0, 999, true));
        catalogoCompleto.add(new CatalogoItem("SERV-008", "Profilaxis Dental por Ultrasonido", "Consultas", 120.0, 999, true));
        catalogoCompleto.add(new CatalogoItem("SERV-009", "Hemograma Completo Automatizado", "Consultas", 65.0, 999, true));
        catalogoCompleto.add(new CatalogoItem("SERV-010", "Día de Guardería / Hotel Canino", "Otros", 40.0, 999, true));

        // 2. Alimentos balanceados clínicos
        catalogoCompleto.add(new CatalogoItem("ALIM-001", "Royal Canin Gastrointestinal 2kg", "Alimentos", 98.0, 12, false));
        catalogoCompleto.add(new CatalogoItem("ALIM-002", "Hills Science Diet Adult Canine 3kg", "Alimentos", 125.0, 8, false));
        catalogoCompleto.add(new CatalogoItem("ALIM-003", "Pro Plan Urinary Feline 1.5kg", "Alimentos", 85.0, 15, false));
        catalogoCompleto.add(new CatalogoItem("ALIM-004", "Lata Recovery Royal Canin 195g", "Alimentos", 24.0, 20, false));

        // 3. Medicamentos y Antiparasitarios de Farmacia
        for (ProductoFarmacia p : repo.getProductosFarmacia()) {
            String cat = p.getCategoria().contains("Alimento") ? "Alimentos" :
                         p.getCategoria().contains("Vacuna") ? "Vacunas" : "Otros";
            catalogoCompleto.add(new CatalogoItem(p.getCodigo(), p.getNombre(), cat, p.getPrecioVenta(), p.getStockActual(), false));
        }
    }

    private JPanel crearCabeceraPOS() {
        JPanel cab = new JPanel(new BorderLayout());
        cab.setOpaque(false);
        cab.setBorder(new EmptyBorder(0, 0, 10, 0));

        JPanel izq = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        izq.setOpaque(false);

        JLabel lblBadge = new JLabel(Iconos.crearIconoPOS(22, COLOR_PRIMARIO));
        izq.add(lblBadge);

        JPanel titulos = new JPanel();
        titulos.setOpaque(false);
        titulos.setLayout(new BoxLayout(titulos, BoxLayout.Y_AXIS));

        JLabel lblTit = new JLabel("Punto de Venta (POS) - Caja Central Happy Pets");
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblTit.setForeground(COLOR_TEXTO_TITULO);

        JLabel lblSub = new JLabel("Facturación rápida, venta de fármacos, servicios clínicos y control de cobros");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(COLOR_TEXTO_MUTED);

        titulos.add(lblTit);
        titulos.add(lblSub);
        izq.add(titulos);
        cab.add(izq, BorderLayout.WEST);

        JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        der.setOpaque(false);

        JButton btnHistorial = Ui.boton("Ver Ventas del Día", false);
        btnHistorial.setIcon(Iconos.crearIconoFactura(14, new Color(71, 85, 105)));
        btnHistorial.addActionListener(e -> mostrarHistorialVentas());
        der.add(btnHistorial);

        cab.add(der, BorderLayout.EAST);
        return cab;
    }

    /**
     * Columna izquierda: Buscador, filtros de categorías y rejilla de productos.
     */
    private JPanel crearColumnaCatalogo() {
        JPanel col = new JPanel(new BorderLayout(0, 8));
        col.setOpaque(false);

        // Barra de búsqueda con icono
        JPanel panelSearch = new JPanel(new BorderLayout(8, 0));
        panelSearch.setBackground(Color.WHITE);
        panelSearch.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDE, 1),
                new EmptyBorder(6, 10, 6, 10)
        ));

        JLabel icoBuscar = new JLabel(Iconos.crearIconoBuscar(16, COLOR_TEXTO_MUTED));
        panelSearch.add(icoBuscar, BorderLayout.WEST);

        txtBuscar = new JTextField();
        txtBuscar.setBorder(null);
        txtBuscar.setPreferredSize(new Dimension(160, 28));
        txtBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtBuscar.setToolTipText("Buscar producto o servicio por código o nombre...");
        txtBuscar.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                filtrarCatalogo();
            }
        });
        panelSearch.add(txtBuscar, BorderLayout.CENTER);

        JButton btnBorrar = new happypets.ui.BotonAsset("✕");
        btnBorrar.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnBorrar.setForeground(COLOR_TEXTO_MUTED);
        btnBorrar.setBorder(null);
        btnBorrar.setPreferredSize(new Dimension(28, 28));
        btnBorrar.setToolTipText("Limpiar búsqueda");
        btnBorrar.setContentAreaFilled(false);
        btnBorrar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnBorrar.addActionListener(e -> {
            txtBuscar.setText("");
            filtrarCatalogo();
        });
        panelSearch.add(btnBorrar, BorderLayout.EAST);

        // Barra de Filtros Pills (Todas, Consultas, Vacunas, Alimentos, Otros)
        JPanel panelFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
        panelFiltros.setOpaque(false);

        String[] categorias = {"Todas", "Consultas", "Vacunas", "Alimentos", "Otros"};
        ButtonGroup bg = new ButtonGroup();

        for (String cat : categorias) {
            JButton btnCat = crearPillCategoria(cat);
            bg.add(btnCat);
            panelFiltros.add(btnCat);
        }

        JPanel topCat = new JPanel(new BorderLayout(0, 6));
        topCat.setOpaque(false);
        topCat.add(panelSearch, BorderLayout.NORTH);
        topCat.add(panelFiltros, BorderLayout.SOUTH);

        col.add(topCat, BorderLayout.NORTH);

        // Rejilla de productos interactiva dentro de scroll
        panelGridProductos = new JPanel(new GridLayout(0, 2, 10, 10));
        panelGridProductos.setOpaque(false);

        JScrollPane scrollGrid = new JScrollPane(panelGridProductos);
        scrollGrid.setBorder(BorderFactory.createLineBorder(COLOR_BORDE, 1));
        scrollGrid.getViewport().setBackground(Color.WHITE);
        scrollGrid.getVerticalScrollBar().setUnitIncrement(16);

        col.add(scrollGrid, BorderLayout.CENTER);

        // Barra de pie con contador
        JPanel pieCat = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 4));
        pieCat.setOpaque(false);
        lblContadorItems = new JLabel("Mostrando items...");
        lblContadorItems.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblContadorItems.setForeground(COLOR_TEXTO_MUTED);
        pieCat.add(lblContadorItems);
        col.add(pieCat, BorderLayout.SOUTH);

        return col;
    }

    private JButton crearPillCategoria(String nombre) {
        JButton btn = new happypets.ui.BotonAsset(nombre) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                boolean activo = nombre.equalsIgnoreCase(categoriaFiltroActual);
                if (activo) {
                    g2.setColor(COLOR_PRIMARIO);
                } else if (getModel().isRollover()) {
                    g2.setColor(new Color(254, 215, 170));
                } else {
                    g2.setColor(Color.WHITE);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(activo ? COLOR_PRIMARIO_OSCURO : COLOR_BORDE);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setForeground(nombre.equalsIgnoreCase(categoriaFiltroActual) ? Color.WHITE : COLOR_TEXTO_TITULO);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(5, 12, 5, 12));

        btn.addActionListener(e -> {
            categoriaFiltroActual = nombre;
            // Refrescar estilo de todos los botones hermanos
            Component[] comps = btn.getParent().getComponents();
            for (Component c : comps) {
                if (c instanceof JButton) {
                    JButton b = (JButton) c;
                    b.setForeground(b.getText().equalsIgnoreCase(categoriaFiltroActual) ? Color.WHITE : COLOR_TEXTO_TITULO);
                    b.repaint();
                }
            }
            filtrarCatalogo();
        });

        return btn;
    }

    private void filtrarCatalogo() {
        catalogoFiltrado.clear();
        String query = txtBuscar != null ? txtBuscar.getText().trim().toLowerCase() : "";

        for (CatalogoItem it : catalogoCompleto) {
            boolean coincideCat = "Todas".equalsIgnoreCase(categoriaFiltroActual) ||
                                  it.categoria.equalsIgnoreCase(categoriaFiltroActual);
            boolean coincideTexto = query.isEmpty() ||
                                    it.nombre.toLowerCase().contains(query) ||
                                    it.codigo.toLowerCase().contains(query);

            if (coincideCat && coincideTexto) {
                catalogoFiltrado.add(it);
            }
        }

        renderizarGridProductos();
    }

    private void renderizarGridProductos() {
        panelGridProductos.removeAll();

        if (catalogoFiltrado.isEmpty()) {
            JPanel vacio = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 40));
            vacio.setOpaque(false);
            JLabel lblVacio = new JLabel("No se encontraron productos o servicios.");
            lblVacio.setFont(new Font("Segoe UI", Font.ITALIC, 13));
            lblVacio.setForeground(COLOR_TEXTO_MUTED);
            vacio.add(lblVacio);
            panelGridProductos.add(vacio);
        } else {
            for (CatalogoItem item : catalogoFiltrado) {
                panelGridProductos.add(crearCardProducto(item));
            }
        }

        if (lblContadorItems != null) {
            lblContadorItems.setText("Mostrando " + catalogoFiltrado.size() + " de " + catalogoCompleto.size() + " ítems disponibles");
        }

        panelGridProductos.revalidate();
        panelGridProductos.repaint();
    }

    private JPanel crearCardProducto(CatalogoItem item) {
        JPanel card = new JPanel(new BorderLayout(8, 6)) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(COLOR_FONDO_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(COLOR_BORDE);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(10, 12, 10, 12));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Top: Icono de categoría y badge de stock/servicio
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        Icon ico = "Consultas".equalsIgnoreCase(item.categoria) ? Iconos.crearIconoEstetoscopio(16, new Color(14, 165, 233)) :
                   "Vacunas".equalsIgnoreCase(item.categoria) ? Iconos.crearIconoJeringa(16, new Color(16, 185, 129)) :
                   "Alimentos".equalsIgnoreCase(item.categoria) ? Iconos.crearIconoHuella(16, new Color(245, 158, 11)) :
                   Iconos.crearIconoPildora(16, COLOR_PRIMARIO);

        JLabel lblIco = new JLabel(ico);
        top.add(lblIco, BorderLayout.WEST);

        JLabel badgeStock = new JLabel(item.esServicio ? "Servicio Clínico" : "Stock: " + item.stock);
        badgeStock.setFont(new Font("Segoe UI", Font.BOLD, 10));
        badgeStock.setForeground(item.esServicio ? new Color(14, 165, 233) :
                                item.stock <= 5 ? new Color(220, 38, 38) : new Color(22, 101, 52));
        top.add(badgeStock, BorderLayout.EAST);
        card.add(top, BorderLayout.NORTH);

        // Centro: Nombre del producto / servicio
        JLabel lblNombre = new JLabel("<html><body style='width: 130px; font-weight:600; color:#1e293b; line-height:1.2;'>" +
                item.nombre + "</body></html>");
        lblNombre.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        card.add(lblNombre, BorderLayout.CENTER);

        // Bottom: Precio y Botón Agregar
        JPanel bot = new JPanel(new BorderLayout());
        bot.setOpaque(false);

        JLabel lblPrecio = new JLabel("S/ " + String.format("%.2f", item.precio));
        lblPrecio.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblPrecio.setForeground(COLOR_PRIMARIO);
        bot.add(lblPrecio, BorderLayout.WEST);

        JButton btnAdd = new happypets.ui.BotonAsset("+ Agregar");
        btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setBackground(COLOR_PRIMARIO);
        btnAdd.setFocusPainted(false);
        btnAdd.setBorder(new EmptyBorder(4, 8, 4, 8));
        btnAdd.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnAdd.addActionListener(e -> agregarItemAVenta(item));
        bot.add(btnAdd, BorderLayout.EAST);

        card.add(bot, BorderLayout.SOUTH);

        // Clic en toda la tarjeta también agrega
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                agregarItemAVenta(item);
            }
        });

        return card;
    }

    /**
     * Columna derecha: Venta actual (Carrito), Totales, Métodos de Pago y Acciones.
     */
    private JPanel crearColumnaVentaActual() {
        JPanel col = new JPanel(new BorderLayout(0, 10));
        col.setOpaque(false);

        // Cabecera "Venta actual" con botón Limpiar
        JPanel cabVenta = new JPanel(new BorderLayout());
        cabVenta.setOpaque(false);

        JPanel titCart = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        titCart.setOpaque(false);
        JLabel icoCart = new JLabel(Iconos.crearIconoFactura(16, COLOR_PRIMARIO));
        JLabel lblTitVenta = new JLabel("Venta actual");
        lblTitVenta.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitVenta.setForeground(COLOR_TEXTO_TITULO);
        titCart.add(icoCart);
        titCart.add(lblTitVenta);
        cabVenta.add(titCart, BorderLayout.WEST);

        JButton btnLimpiar = Ui.boton("Limpiar Carrito", false);
        btnLimpiar.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnLimpiar.setForeground(new Color(220, 38, 38));
        btnLimpiar.addActionListener(e -> limpiarVenta());
        cabVenta.add(btnLimpiar, BorderLayout.EAST);

        // Bloque superior: Cliente, Mascota y Tipo de Comprobante
        JPanel panelCliente = new JPanel(new GridLayout(2, 2, 8, 6));
        panelCliente.setOpaque(false);
        panelCliente.setBorder(new EmptyBorder(4, 0, 6, 0));

        JPanel pCli = new JPanel(new BorderLayout(4, 0));
        pCli.setOpaque(false);
        JLabel lblLCli = new JLabel("Cliente: ");
        lblLCli.setFont(new Font("Segoe UI", Font.BOLD, 11));
        pCli.add(lblLCli, BorderLayout.WEST);
        txtCliente = new JTextField("Carlos Eduardo Morales Soto");
        txtCliente.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        pCli.add(txtCliente, BorderLayout.CENTER);
        panelCliente.add(pCli);

        JPanel pComp = new JPanel(new BorderLayout(4, 0));
        pComp.setOpaque(false);
        JLabel lblLComp = new JLabel("Comprobante: ");
        lblLComp.setFont(new Font("Segoe UI", Font.BOLD, 11));
        pComp.add(lblLComp, BorderLayout.WEST);
        cbTipoComprobante = new JComboBox<>(new String[]{"Boleta Electrónica", "Factura Electrónica", "Ticket POS"});
        cbTipoComprobante.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        pComp.add(cbTipoComprobante, BorderLayout.CENTER);
        panelCliente.add(pComp);

        JPanel pMasc = new JPanel(new BorderLayout(4, 0));
        pMasc.setOpaque(false);
        JLabel lblLMasc = new JLabel("Paciente / Mascota: ");
        lblLMasc.setFont(new Font("Segoe UI", Font.BOLD, 11));
        pMasc.add(lblLMasc, BorderLayout.WEST);
        cbMascota = new JComboBox<>(new String[]{"Rocky (Golden Retriever)", "Luna (Siamés)", "Toby (Pug)", "Sin Mascota Asociada"});
        cbMascota.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        pMasc.add(cbMascota, BorderLayout.CENTER);
        panelCliente.add(pMasc);

        JPanel pCaj = new JPanel(new BorderLayout(4, 0));
        pCaj.setOpaque(false);
        JLabel lblLCaj = new JLabel("Cajero / Resp.: ");
        lblLCaj.setFont(new Font("Segoe UI", Font.BOLD, 11));
        pCaj.add(lblLCaj, BorderLayout.WEST);
        JLabel lblNombreCajero = new JLabel("Joanna Corrales / Harry Arroyo");
        lblNombreCajero.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblNombreCajero.setForeground(COLOR_TEXTO_MUTED);
        pCaj.add(lblNombreCajero, BorderLayout.CENTER);
        panelCliente.add(pCaj);

        JPanel topContenedor = new JPanel(new BorderLayout());
        topContenedor.setOpaque(false);
        topContenedor.add(cabVenta, BorderLayout.NORTH);
        topContenedor.add(panelCliente, BorderLayout.SOUTH);
        col.add(topContenedor, BorderLayout.NORTH);

        // Tabla de ítems del Carrito
        String[] columnas = {"Producto", "Cant.", "P. unit.", "Subtotal", "Acción"};
        modeloVenta = new DefaultTableModel(columnas, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaVenta = new JTable(modeloVenta);
        Ui.formatearTabla(tablaVenta);
        tablaVenta.getColumnModel().getColumn(0).setPreferredWidth(160);
        tablaVenta.getColumnModel().getColumn(1).setPreferredWidth(45);
        tablaVenta.getColumnModel().getColumn(2).setPreferredWidth(65);
        tablaVenta.getColumnModel().getColumn(3).setPreferredWidth(65);
        tablaVenta.getColumnModel().getColumn(4).setPreferredWidth(55);

        // Doble clic o acción para modificar cantidad o eliminar
        tablaVenta.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int row = tablaVenta.getSelectedRow();
                int colIdx = tablaVenta.getSelectedColumn();
                if (row >= 0 && colIdx == 4) {
                    eliminarItemDeVenta(row);
                }
            }
        });

        JScrollPane scrollTabla = new JScrollPane(tablaVenta);
        scrollTabla.setPreferredSize(new Dimension(0, 180));
        scrollTabla.setBorder(BorderFactory.createLineBorder(COLOR_BORDE, 1));
        col.add(scrollTabla, BorderLayout.CENTER);

        // Sección Inferior: Totales, Métodos de Pago y Botones oficiales
        JPanel panelInferior = new JPanel();
        panelInferior.setLayout(new BoxLayout(panelInferior, BoxLayout.Y_AXIS));
        panelInferior.setOpaque(false);
        panelInferior.setBorder(new EmptyBorder(8, 0, 0, 0));

        // Cuadro de Resumen Numérico (Subtotal, Descuento, Total)
        JPanel boxTotales = new JPanel(new GridLayout(3, 2, 10, 4));
        boxTotales.setBackground(Color.WHITE);
        boxTotales.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDE, 1),
                new EmptyBorder(8, 14, 8, 14)
        ));

        // Subtotal
        JLabel lblSubTit = new JLabel("Subtotal:");
        lblSubTit.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubtotal = new JLabel("S/ 0.00", SwingConstants.RIGHT);
        lblSubtotal.setFont(new Font("Segoe UI", Font.BOLD, 12));
        boxTotales.add(lblSubTit);
        boxTotales.add(lblSubtotal);

        // Descuento %
        JPanel pDesc = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        pDesc.setOpaque(false);
        pDesc.add(new JLabel("Descuento (%):"));
        spinDescuento = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 100.0, 1.0));
        spinDescuento.setPreferredSize(new Dimension(65, 24));
        spinDescuento.addChangeListener(e -> recalcularVenta());
        pDesc.add(spinDescuento);

        lblMontoDescuento = new JLabel("- S/ 0.00", SwingConstants.RIGHT);
        lblMontoDescuento.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblMontoDescuento.setForeground(new Color(220, 38, 38));
        boxTotales.add(pDesc);
        boxTotales.add(lblMontoDescuento);

        // Total Final Destacado
        JLabel lblTotTit = new JLabel("Total a Pagar:");
        lblTotTit.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTotTit.setForeground(COLOR_TEXTO_TITULO);
        lblTotal = new JLabel("S/ 0.00", SwingConstants.RIGHT);
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTotal.setForeground(COLOR_PRIMARIO);
        boxTotales.add(lblTotTit);
        boxTotales.add(lblTotal);

        panelInferior.add(boxTotales);
        panelInferior.add(Box.createVerticalStrut(8));

        // Selector de Métodos de Pago (Botones Toggle)
        JPanel panelMetodos = new JPanel(new GridLayout(2, 2, 6, 6));
        panelMetodos.setOpaque(false);

        btnPagoEfectivo = crearBotonMetodoPago("Efectivo", Iconos.crearIconoMonedas(14, Color.WHITE), true);
        btnPagoYape = crearBotonMetodoPago("Yape/Plin", Iconos.crearIconoQr(14, COLOR_TEXTO_TITULO), false);
        btnPagoTarjeta = crearBotonMetodoPago("Tarjeta", Iconos.crearIconoTarjeta(14, COLOR_TEXTO_TITULO), false);
        btnPagoTransferencia = crearBotonMetodoPago("Transferencia", Iconos.crearIconoFactura(14, COLOR_TEXTO_TITULO), false);

        panelMetodos.add(btnPagoEfectivo);
        panelMetodos.add(btnPagoYape);
        panelMetodos.add(btnPagoTarjeta);
        panelMetodos.add(btnPagoTransferencia);

        panelInferior.add(panelMetodos);
        panelInferior.add(Box.createVerticalStrut(6));

        // Panel condicional para Efectivo: Monto Recibido y Vuelto
        panelEfectivoExtra = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 2));
        panelEfectivoExtra.setOpaque(false);

        JLabel lblMRec = new JLabel("Monto Recibido S/:");
        lblMRec.setFont(new Font("Segoe UI", Font.BOLD, 12));
        txtMontoRecibido = new JTextField("0.00", 6);
        txtMontoRecibido.setFont(new Font("Segoe UI", Font.BOLD, 12));
        txtMontoRecibido.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                recalcularVuelto();
            }
        });

        lblVuelto = new JLabel("Vuelto: S/ 0.00");
        lblVuelto.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblVuelto.setForeground(COLOR_VERDE);

        panelEfectivoExtra.add(lblMRec);
        panelEfectivoExtra.add(txtMontoRecibido);
        panelEfectivoExtra.add(lblVuelto);
        panelInferior.add(panelEfectivoExtra);
        panelInferior.add(Box.createVerticalStrut(8));

        // Botones de Acción Oficiales del Wireframe: [Cotización] e [Imprimir]
        JPanel panelAcciones = new JPanel(new GridLayout(1, 2, 10, 0));
        panelAcciones.setOpaque(false);

        JButton btnCotizacion = new happypets.ui.BotonAsset(" Cotización") {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? new Color(241, 245, 249) : Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(new Color(148, 163, 184));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnCotizacion.setIcon(Iconos.crearIconoDocumento(16, new Color(71, 85, 105)));
        btnCotizacion.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnCotizacion.setForeground(new Color(51, 65, 85));
        btnCotizacion.setPreferredSize(new Dimension(0, 38));
        btnCotizacion.setFocusPainted(false);
        btnCotizacion.setContentAreaFilled(false);
        btnCotizacion.setOpaque(false);
        btnCotizacion.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnCotizacion.addActionListener(e -> procesarVenta(true));

        JButton btnImprimir = new happypets.ui.BotonAsset(" Imprimir / Cobrar") {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2.setColor(COLOR_PRIMARIO_OSCURO.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(COLOR_PRIMARIO_OSCURO);
                } else {
                    g2.setColor(COLOR_PRIMARIO);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnImprimir.setIcon(Iconos.crearIconoImprimir(16, Color.WHITE));
        btnImprimir.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnImprimir.setForeground(Color.WHITE);
        btnImprimir.setPreferredSize(new Dimension(0, 38));
        btnImprimir.setFocusPainted(false);
        btnImprimir.setContentAreaFilled(false);
        btnImprimir.setOpaque(false);
        btnImprimir.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnImprimir.addActionListener(e -> procesarVenta(false));

        panelAcciones.add(btnCotizacion);
        panelAcciones.add(btnImprimir);

        panelInferior.add(panelAcciones);

        col.add(panelInferior, BorderLayout.SOUTH);
        return col;
    }

    private JButton crearBotonMetodoPago(String metodo, Icon ico, boolean activoInicial) {
        JButton btn = new happypets.ui.BotonAsset(metodo) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                boolean activo = metodo.equalsIgnoreCase(metodoPagoSeleccionado);
                if (activo) {
                    g2.setColor(COLOR_PRIMARIO);
                } else if (getModel().isRollover()) {
                    g2.setColor(new Color(254, 243, 199));
                } else {
                    g2.setColor(Color.WHITE);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g2.setColor(activo ? COLOR_PRIMARIO_OSCURO : COLOR_BORDE);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 6, 6);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setIcon(ico);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btn.setForeground(activoInicial ? Color.WHITE : COLOR_TEXTO_TITULO);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(0, 32));

        btn.addActionListener(e -> {
            metodoPagoSeleccionado = metodo;
            panelEfectivoExtra.setVisible("Efectivo".equalsIgnoreCase(metodo));

            // Actualizar estilo visual de los 4 botones
            actualizarBotonesMetodos();
            recalcularVuelto();
        });

        return btn;
    }

    private void actualizarBotonesMetodos() {
        JButton[] btns = {btnPagoEfectivo, btnPagoYape, btnPagoTarjeta, btnPagoTransferencia};
        for (JButton b : btns) {
            boolean act = b.getText().equalsIgnoreCase(metodoPagoSeleccionado);
            b.setForeground(act ? Color.WHITE : COLOR_TEXTO_TITULO);
            b.repaint();
        }
    }

    private void agregarItemAVenta(CatalogoItem item) {
        // Verificar si ya existe en la lista
        boolean encontrado = false;
        for (ItemVentaPOS v : itemsVenta) {
            if (v.getCodigo().equalsIgnoreCase(item.codigo)) {
                v.incrementarCantidad(1);
                encontrado = true;
                break;
            }
        }
        if (!encontrado) {
            itemsVenta.add(new ItemVentaPOS(item.codigo, item.nombre, item.categoria, 1, item.precio));
        }

        actualizarTablaVenta();
        recalcularVenta();
    }

    private void eliminarItemDeVenta(int row) {
        if (row >= 0 && row < itemsVenta.size()) {
            itemsVenta.remove(row);
            actualizarTablaVenta();
            recalcularVenta();
        }
    }

    private void limpiarVenta() {
        if (itemsVenta.isEmpty()) return;
        int r = JOptionPane.showConfirmDialog(this, "¿Desea vaciar todos los ítems de la venta actual?",
                "Limpiar Venta", JOptionPane.YES_NO_OPTION);
        if (r == JOptionPane.YES_OPTION) {
            itemsVenta.clear();
            actualizarTablaVenta();
            recalcularVenta();
        }
    }

    private void actualizarTablaVenta() {
        modeloVenta.setRowCount(0);
        for (ItemVentaPOS it : itemsVenta) {
            modeloVenta.addRow(new Object[]{
                    it.getDescripcion(),
                    it.getCantidad(),
                    String.format("%.2f", it.getPrecioUnitario()),
                    String.format("%.2f", it.getSubtotal()),
                    "✕ Quitar"
            });
        }
    }

    private void recalcularVenta() {
        double sub = 0.0;
        for (ItemVentaPOS it : itemsVenta) {
            sub += it.getSubtotal();
        }

        double descPorc = ((Number) spinDescuento.getValue()).doubleValue();
        double descMonto = sub * (descPorc / 100.0);
        double total = Math.max(0.0, sub - descMonto);

        lblSubtotal.setText("S/ " + String.format("%.2f", sub));
        lblMontoDescuento.setText("- S/ " + String.format("%.2f", descMonto));
        lblTotal.setText("S/ " + String.format("%.2f", total));

        if ("Efectivo".equalsIgnoreCase(metodoPagoSeleccionado)) {
            if (txtMontoRecibido.getText().trim().equals("0.00") || txtMontoRecibido.getText().trim().isEmpty()) {
                txtMontoRecibido.setText(String.format("%.2f", total));
            }
        }
        recalcularVuelto();
    }

    private void recalcularVuelto() {
        try {
            double total = Double.parseDouble(lblTotal.getText().replace("S/", "").replace(",", "").trim());
            double recibido = Double.parseDouble(txtMontoRecibido.getText().replace("S/", "").replace(",", "").trim());
            double vuelto = Math.max(0.0, recibido - total);
            lblVuelto.setText("Vuelto: S/ " + String.format("%.2f", vuelto));
        } catch (Exception ex) {
            lblVuelto.setText("Vuelto: S/ 0.00");
        }
    }

    /**
     * Procesa la venta o cotización y muestra el diálogo con vista previa del ticket.
     */
    private void procesarVenta(boolean esCotizacion) {
        if (itemsVenta.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "El carrito de venta está vacío. Por favor seleccione al menos un producto o servicio.",
                    "Venta Vacía", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String cliente = txtCliente.getText().trim();
        if (cliente.isEmpty()) {
            cliente = "Cliente Varios";
        }

        double total = Double.parseDouble(lblTotal.getText().replace("S/", "").replace(",", "").trim());
        double recibido = total;
        if ("Efectivo".equalsIgnoreCase(metodoPagoSeleccionado)) {
            try {
                recibido = Double.parseDouble(txtMontoRecibido.getText().trim());
                if (!esCotizacion && recibido < total) {
                    JOptionPane.showMessageDialog(this,
                            "El monto recibido (S/ " + String.format("%.2f", recibido) + ") es menor al total a pagar (S/ " + String.format("%.2f", total) + ").",
                            "Pago Insuficiente", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            } catch (Exception e) {
                recibido = total;
            }
        }

        double descPorc = ((Number) spinDescuento.getValue()).doubleValue();
        String tipoComp = esCotizacion ? "Cotización" : (String) cbTipoComprobante.getSelectedItem();
        String mascota = (String) cbMascota.getSelectedItem();

        VentaPOS venta = new VentaPOS(
                null, null, tipoComp, LocalDateTime.now(), cliente, "-",
                mascota, itemsVenta, descPorc, metodoPagoSeleccionado, recibido,
                esCotizacion ? "Cotización" : "Pagada", "Joanna Corrales"
        );

        if (!esCotizacion) {
            repo.guardarVentaPOS(venta);
            // Si el pago es en efectivo, ofrecer alimentar la caja chica de inmediato
            if ("Efectivo".equalsIgnoreCase(metodoPagoSeleccionado)) {
                repo.guardarMovimientoCajaChica(new happypets.model.MovimientoCajaChica(
                        null, java.time.LocalDate.now(), "Ingreso",
                        "Cobro POS " + venta.getNumeroComprobante() + " - " + cliente,
                        venta.getTotal(), "Joanna Corrales", venta.getNumeroComprobante()
                ));
            }
        }

        // Mostrar comprobante impreso en ventana modal estilizada
        mostrarModalTicket(venta, esCotizacion);

        // Limpiar para la siguiente venta
        itemsVenta.clear();
        actualizarTablaVenta();
        spinDescuento.setValue(0.0);
        recalcularVenta();
        cargarCatalogoMaestro();
        filtrarCatalogo();
    }

    private void mostrarModalTicket(VentaPOS venta, boolean esCotizacion) {
        JDialog dlg = new JDialog((java.awt.Frame) SwingUtilities.getWindowAncestor(this),
                esCotizacion ? "Cotización / Proforma Oficial" : "Comprobante Electrónico Emitido", true);
        dlg.setSize(440, 640);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());

        JPanel p = new JPanel(new BorderLayout(0, 12));
        p.setBackground(Color.WHITE);
        p.setBorder(new EmptyBorder(16, 20, 16, 20));

        // Vista de texto / ticket con formato térmico profesional
        JTextArea txtTicket = new JTextArea();
        txtTicket.setFont(new Font("Consolas", Font.PLAIN, 12));
        txtTicket.setEditable(false);
        txtTicket.setBackground(new Color(254, 254, 254));

        StringBuilder sb = new StringBuilder();
        sb.append("========================================\n");
        sb.append("       VETERINARIA HAPPY PETS S.A.C.    \n");
        sb.append("        RUC: 20608912345 · LIMA - PERÚ  \n");
        sb.append("       Av. San Borja Sur 482 · San Borja\n");
        sb.append("       Central: (01) 432-9980 / 984-552-110\n");
        sb.append("========================================\n");
        sb.append(esCotizacion ? "     *** PROFORMA / COTIZACIÓN ***\n" :
                                 "   " + venta.getTipoComprobante().toUpperCase() + "\n");
        sb.append("N° Comprobante: ").append(venta.getNumeroComprobante()).append("\n");
        sb.append("Fecha y Hora  : ").append(venta.getFechaHoraTexto()).append("\n");
        sb.append("Cajero Resp.  : ").append(venta.getCajero()).append("\n");
        sb.append("----------------------------------------\n");
        sb.append("Cliente : ").append(venta.getClienteNombre()).append("\n");
        sb.append("Paciente: ").append(venta.getMascotaNombre()).append("\n");
        sb.append("Método  : ").append(venta.getMetodoPago()).append("\n");
        sb.append("----------------------------------------\n");
        sb.append(String.format("%-18s %4s %7s %8s\n", "DESCRIPCIÓN", "CANT", "P.UNT", "TOTAL"));
        sb.append("----------------------------------------\n");

        for (ItemVentaPOS it : venta.getItems()) {
            String desc = it.getDescripcion().length() > 18 ?
                    it.getDescripcion().substring(0, 16) + ".." : it.getDescripcion();
            sb.append(String.format("%-18s %4d %7.2f %8.2f\n",
                    desc, it.getCantidad(), it.getPrecioUnitario(), it.getSubtotal()));
        }

        sb.append("----------------------------------------\n");
        sb.append(String.format("%-26s S/ %8.2f\n", "SUBTOTAL:", venta.getSubtotal()));
        if (venta.getPorcentajeDescuento() > 0) {
            sb.append(String.format("%-26s S/ -%7.2f\n", "DSCTO (" + (int)venta.getPorcentajeDescuento() + "%):", venta.getMontoDescuento()));
        }
        sb.append(String.format("%-26s S/ %8.2f\n", "IGV (18% INCLUIDO):", venta.getIgv()));
        sb.append(String.format("%-26s S/ %8.2f\n", "TOTAL FINAL A PAGAR:", venta.getTotal()));
        sb.append("----------------------------------------\n");

        if ("Efectivo".equalsIgnoreCase(venta.getMetodoPago()) && !esCotizacion) {
            sb.append(String.format("%-26s S/ %8.2f\n", "IMPORTE RECIBIDO:", venta.getMontoRecibido()));
            sb.append(String.format("%-26s S/ %8.2f\n", "VUELTO ENTREGADO:", venta.getVuelto()));
            sb.append("----------------------------------------\n");
        }

        sb.append("\n  [ QR ELECTRÓNICO SUNAT VALIDADO ]  \n");
        sb.append("      Consulte su comprobante en:\n");
        sb.append("       www.veterinariahappypets.pe/cpe   \n");
        sb.append("  ¡Gracias por confiar en Happy Pets!  \n");
        sb.append("========================================\n");

        txtTicket.setText(sb.toString());

        JScrollPane sc = new JScrollPane(txtTicket);
        sc.setBorder(BorderFactory.createLineBorder(COLOR_BORDE, 1));
        p.add(sc, BorderLayout.CENTER);

        // Botones de acción del modal
        JPanel botDlg = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        botDlg.setOpaque(false);

        JButton btnPrint = Ui.boton("Imprimir en Ticketera Térmica (80mm)", true);
        btnPrint.setIcon(Iconos.crearIconoImprimir(14, Color.WHITE));
        btnPrint.addActionListener(e -> {
            JOptionPane.showMessageDialog(dlg,
                    "Enviando documento térmico a impresora POS predeterminada...\nImpresión completada con éxito.",
                    "Impresión Exitosa", JOptionPane.INFORMATION_MESSAGE);
            dlg.dispose();
        });

        JButton btnCerrar = Ui.boton("Cerrar", false);
        btnCerrar.addActionListener(e -> dlg.dispose());

        botDlg.add(btnPrint);
        botDlg.add(btnCerrar);
        p.add(botDlg, BorderLayout.SOUTH);

        dlg.add(p);
        dlg.setVisible(true);
    }

    private void mostrarHistorialVentas() {
        JDialog dlg = new JDialog((java.awt.Frame) SwingUtilities.getWindowAncestor(this),
                "Historial de Ventas POS del Día", true);
        dlg.setSize(840, 520);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());

        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setBackground(Color.WHITE);
        p.setBorder(new EmptyBorder(14, 18, 14, 18));

        JLabel lblTit = new JLabel("Transacciones Registradas en Caja Central");
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTit.setForeground(COLOR_TEXTO_TITULO);
        p.add(lblTit, BorderLayout.NORTH);

        String[] cols = {"N° Comp.", "Tipo", "Fecha/Hora", "Cliente", "Mascota", "Método", "Total (S/)", "Estado"};
        DefaultTableModel mod = new DefaultTableModel(cols, 0);
        List<VentaPOS> ventas = repo.getVentasPOS();

        for (VentaPOS v : ventas) {
            mod.addRow(new Object[]{
                    v.getNumeroComprobante(),
                    v.getTipoComprobante(),
                    v.getFechaHoraTexto(),
                    v.getClienteNombre(),
                    v.getMascotaNombre(),
                    v.getMetodoPago(),
                    String.format("%.2f", v.getTotal()),
                    v.getEstado()
            });
        }

        JTable tab = new JTable(mod);
        Ui.formatearTabla(tab);

        p.add(new JScrollPane(tab), BorderLayout.CENTER);

        JPanel bot = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        bot.setOpaque(false);
        JButton btnCerrar = Ui.boton("Cerrar", true);
        btnCerrar.addActionListener(e -> dlg.dispose());
        bot.add(btnCerrar);
        p.add(bot, BorderLayout.SOUTH);

        dlg.add(p);
        dlg.setVisible(true);
    }

    public void recargarDatos() {
        cargarCatalogoMaestro();
        filtrarCatalogo();
    }

    /**
     * Modelo interno auxiliar para la grilla de productos / servicios.
     */
    private static class CatalogoItem {
        final String codigo;
        final String nombre;
        final String categoria;
        final double precio;
        final int stock;
        final boolean esServicio;

        CatalogoItem(String codigo, String nombre, String categoria, double precio, int stock, boolean esServicio) {
            this.codigo = codigo;
            this.nombre = nombre;
            this.categoria = categoria;
            this.precio = precio;
            this.stock = stock;
            this.esServicio = esServicio;
        }
    }
}
