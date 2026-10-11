package happypets.modulos.modulo6;

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
import java.time.LocalDateTime;
import java.util.ArrayList;
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
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
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
 * Diálogo modal desacoplado en dos pestañas para emisión interactiva de Ventas y Comprobantes POS.
 * Pestaña 1: Catálogo & Carrito de Productos/Servicios.
 * Pestaña 2: Cliente, Facturación SUNAT & Medio de Pago.
 */
public class FormularioVentaPOSDialog extends JDialog {
    private static final long serialVersionUID = 1L;

    private final RepositorioVeterinaria repo = RepositorioVeterinaria.getInstancia();
    private boolean confirmado = false;

    // Estado del carrito
    private final List<ItemVentaPOS> itemsCarrito = new ArrayList<>();
    private final List<CatalogoOpcion> opcionesCatalogo = new ArrayList<>();

    // Pestaña 1: Componentes
    private JComboBox<String> cbProductoServicio;
    private JSpinner spinCantidad;
    private JLabel lblPrecioUnitario;
    private JTable tablaItems;
    private DefaultTableModel modeloItems;
    private JLabel lblSubtotalBruto;
    private JSpinner spinDescuentoPorc;
    private JLabel lblMontoDescuento;
    private JLabel lblTotalVenta;

    // Pestaña 2: Componentes
    private JComboBox<String> cbTipoComprobante;
    private JTextField txtNumeroComprobante;
    private JComboBox<String> cbCliente;
    private JTextField txtDniRuc;
    private JComboBox<String> cbMascota;
    private JComboBox<String> cbMetodoPago;
    private JTextField txtMontoRecibido;
    private JLabel lblVueltoCalculado;
    private JTextField txtCajero;

    public FormularioVentaPOSDialog(Frame parent) {
        super(parent, "Nueva Venta POS / Emisión de Comprobante", true);
        cargarOpcionesCatalogo();
        inicializarUI();
    }

    private void cargarOpcionesCatalogo() {
        opcionesCatalogo.clear();

        // 1. Servicios Clínicos y Procedimientos
        opcionesCatalogo.add(new CatalogoOpcion("SERV-001", "Consulta Médica General", "Consultas", 50.0));
        opcionesCatalogo.add(new CatalogoOpcion("SERV-002", "Consulta Especializada / Dermatología", "Consultas", 80.0));
        opcionesCatalogo.add(new CatalogoOpcion("SERV-003", "Vacuna Antirrábica + Cartilla Oficial", "Vacunas", 45.0));
        opcionesCatalogo.add(new CatalogoOpcion("SERV-004", "Vacuna Séxtuple Canina Nobivac", "Vacunas", 60.0));
        opcionesCatalogo.add(new CatalogoOpcion("SERV-005", "Triple Felina Nobivac Tricat", "Vacunas", 55.0));
        opcionesCatalogo.add(new CatalogoOpcion("SERV-006", "Baño y Corte de Raza (Grooming)", "Estética", 45.0));
        opcionesCatalogo.add(new CatalogoOpcion("SERV-007", "Baño Medicado Dermatológico", "Estética", 55.0));
        opcionesCatalogo.add(new CatalogoOpcion("SERV-008", "Profilaxis Dental Ultrasonido", "Cirugía", 120.0));
        opcionesCatalogo.add(new CatalogoOpcion("SERV-009", "Hemograma Automatizado 18 Parámetros", "Laboratorio", 65.0));
        opcionesCatalogo.add(new CatalogoOpcion("SERV-010", "Día de Guardería / Hospedaje Canino", "Hospedaje", 40.0));

        // 2. Fármacos y Biológicos del Repositorio Oficial
        for (ProductoFarmacia p : repo.getProductosFarmacia()) {
            opcionesCatalogo.add(new CatalogoOpcion(p.getCodigo(), p.getNombre(), p.getCategoria(), p.getPrecioVenta()));
        }
    }

    private void inicializarUI() {
        setSize(780, 700);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(248, 250, 252));

        // 1. Header institucional
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Ui.BORDE_SUAVE),
                new EmptyBorder(14, 20, 14, 20)
        ));

        JPanel titulos = new JPanel();
        titulos.setOpaque(false);
        titulos.setLayout(new BoxLayout(titulos, BoxLayout.Y_AXIS));

        JLabel lblTit = new JLabel("Terminal de Venta POS & Facturación Electrónica");
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTit.setForeground(Ui.TEXTO_TITULO);

        JLabel lblSub = new JLabel("Emisión directa de tickets, boletas y facturas con sincronización inmediata de Kardex");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(Ui.TEXTO_MUTED);

        titulos.add(lblTit);
        titulos.add(Box.createVerticalStrut(2));
        titulos.add(lblSub);
        header.add(titulos, BorderLayout.CENTER);

        JLabel badgeIcono = new JLabel(Iconos.crearIconoPOS(26, Ui.TURQUESA));
        header.add(badgeIcono, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // 2. Pestañas
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabs.setBackground(Color.WHITE);

        tabs.addTab("1. Catálogo & Carrito de Compras", Iconos.crearIconoCajaAlmacen(15, Ui.TURQUESA), crearPestanaCarrito());
        tabs.addTab("2. Cliente, Facturación SUNAT & Pago", Iconos.crearIconoFactura(15, Ui.TURQUESA_OSCURO), crearPestanaFacturacion());

        add(tabs, BorderLayout.CENTER);

        // 3. Footer con Botones
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        footer.setBackground(Color.WHITE);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Ui.BORDE_SUAVE));

        JButton btnCancelar = Ui.boton("Cancelar", false);
        btnCancelar.addActionListener(e -> dispose());

        JButton btnProcesar = Ui.boton("Emitir Comprobante / Cobrar", true);
        btnProcesar.setIcon(Iconos.crearIconoCheck(14, Color.WHITE));
        btnProcesar.addActionListener(e -> procesarEmisionVenta());

        footer.add(btnCancelar);
        footer.add(btnProcesar);
        add(footer, BorderLayout.SOUTH);

        // Inicializar primer ítem de demostración para facilidad de uso
        if (!opcionesCatalogo.isEmpty()) {
            CatalogoOpcion opt = opcionesCatalogo.get(0);
            itemsCarrito.add(new ItemVentaPOS(opt.codigo, opt.nombre, opt.categoria, 1, opt.precio));
            actualizarTablaCarrito();
        }
    }

    private JPanel crearPestanaCarrito() {
        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(14, 18, 14, 18));

        // Selector superior de Producto / Servicio
        JPanel panelSelect = new JPanel(new GridBagLayout());
        panelSelect.setBackground(Color.WHITE);
        panelSelect.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Ui.BORDE_SUAVE, 1),
                new EmptyBorder(10, 12, 10, 12)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.55;
        JLabel lblProd = new JLabel("Producto Clínico o Servicio Veterinario *");
        lblProd.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblProd.setForeground(Ui.TEXTO_MUTED);
        panelSelect.add(lblProd, gbc);

        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.15;
        JLabel lblCant = new JLabel("Cantidad *");
        lblCant.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblCant.setForeground(Ui.TEXTO_MUTED);
        panelSelect.add(lblCant, gbc);

        gbc.gridx = 2; gbc.gridy = 0; gbc.weightx = 0.15;
        JLabel lblPvp = new JLabel("Precio Unit.");
        lblPvp.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblPvp.setForeground(Ui.TEXTO_MUTED);
        panelSelect.add(lblPvp, gbc);

        gbc.gridx = 3; gbc.gridy = 0; gbc.weightx = 0.15;
        panelSelect.add(new JLabel(""), gbc);

        // Fila de controles
        gbc.gridx = 0; gbc.gridy = 1;
        cbProductoServicio = new JComboBox<>();
        cbProductoServicio.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        for (CatalogoOpcion opt : opcionesCatalogo) {
            cbProductoServicio.addItem(opt.codigo + " - " + opt.nombre + " (S/. " + String.format("%.2f", opt.precio) + ")");
        }
        cbProductoServicio.addActionListener(e -> actualizarPrecioUnitarioSeleccionado());
        panelSelect.add(cbProductoServicio, gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        spinCantidad = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
        spinCantidad.setFont(new Font("Segoe UI", Font.BOLD, 12));
        panelSelect.add(spinCantidad, gbc);

        gbc.gridx = 2; gbc.gridy = 1;
        lblPrecioUnitario = new JLabel("S/. 0.00", SwingConstants.CENTER);
        lblPrecioUnitario.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblPrecioUnitario.setForeground(Ui.TURQUESA_OSCURO);
        panelSelect.add(lblPrecioUnitario, gbc);

        gbc.gridx = 3; gbc.gridy = 1;
        JButton btnAgregar = Ui.boton("+ Agregar", true);
        btnAgregar.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnAgregar.addActionListener(e -> agregarItemAlCarrito());
        panelSelect.add(btnAgregar, gbc);

        p.add(panelSelect, BorderLayout.NORTH);

        // Tabla de ítems en carrito
        String[] cols = {"Código", "Descripción del Ítem", "Categoría", "Cant.", "Precio Unit.", "Subtotal"};
        modeloItems = new DefaultTableModel(cols, 0) {
            private static final long serialVersionUID = 1L;
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        tablaItems = new JTable(modeloItems);
        Ui.formatearTabla(tablaItems, new int[]{0, 2, 3}, new int[]{4, 5});
        tablaItems.setRowHeight(32);

        JScrollPane scrollTabla = new JScrollPane(tablaItems);
        scrollTabla.setBorder(BorderFactory.createLineBorder(Ui.BORDE_SUAVE, 1));
        p.add(scrollTabla, BorderLayout.CENTER);

        // Panel inferior del carrito con Descuento y Totales
        JPanel panelTotales = new JPanel(new BorderLayout(10, 0));
        panelTotales.setBackground(Color.WHITE);
        panelTotales.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Ui.BORDE_SUAVE, 1),
                new EmptyBorder(10, 14, 10, 14)
        ));

        // Acciones sobre la tabla
        JButton btnQuitar = Ui.boton("Quitar Seleccionado", false);
        btnQuitar.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btnQuitar.addActionListener(e -> quitarItemSeleccionado());
        panelTotales.add(btnQuitar, BorderLayout.WEST);

        // Resumen monetario
        JPanel panelResumen = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 0));
        panelResumen.setOpaque(false);

        lblSubtotalBruto = new JLabel("Subtotal: S/. 0.00");
        lblSubtotalBruto.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubtotalBruto.setForeground(Ui.TEXTO_MUTED);

        JLabel lblDesc = new JLabel("Desc. %:");
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblDesc.setForeground(Ui.TEXTO_MUTED);

        spinDescuentoPorc = new JSpinner(new SpinnerNumberModel(0, 0, 100, 5));
        spinDescuentoPorc.setFont(new Font("Segoe UI", Font.BOLD, 11));
        spinDescuentoPorc.addChangeListener(e -> recalcularTotalesCarrito());

        lblMontoDescuento = new JLabel("(-S/. 0.00)");
        lblMontoDescuento.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblMontoDescuento.setForeground(Ui.COLOR_PELIGRO);

        lblTotalVenta = new JLabel("TOTAL: S/. 0.00");
        lblTotalVenta.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTotalVenta.setForeground(Ui.TURQUESA_OSCURO);

        panelResumen.add(lblSubtotalBruto);
        panelResumen.add(lblDesc);
        panelResumen.add(spinDescuentoPorc);
        panelResumen.add(lblMontoDescuento);
        panelResumen.add(new JLabel("|"));
        panelResumen.add(lblTotalVenta);

        panelTotales.add(panelResumen, BorderLayout.EAST);
        p.add(panelTotales, BorderLayout.SOUTH);

        actualizarPrecioUnitarioSeleccionado();
        return p;
    }

    private JPanel crearPestanaFacturacion() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(16, 22, 16, 22));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 1. Tipo de Comprobante & N° Comprobante
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.5;
        p.add(crearEtiquetaCampo("Tipo de Comprobante *"), gbc);

        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.5;
        p.add(crearEtiquetaCampo("Número de Serie / Comprobante"), gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        cbTipoComprobante = new JComboBox<>(new String[]{
                "Boleta Electrónica",
                "Factura Electrónica",
                "Ticket POS",
                "Cotización"
        });
        cbTipoComprobante.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cbTipoComprobante.addActionListener(e -> generarNumeroComprobanteSugerido());
        p.add(cbTipoComprobante, gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        txtNumeroComprobante = new JTextField("B001-" + String.format("%06d", repo.getVentasPOS().size() + 415));
        txtNumeroComprobante.setFont(new Font("Segoe UI", Font.BOLD, 12));
        txtNumeroComprobante.setEditable(false);
        p.add(txtNumeroComprobante, gbc);

        // 2. Cliente & DNI/RUC
        gbc.gridx = 0; gbc.gridy = 2;
        p.add(crearEtiquetaCampo("Cliente / Razón Social *"), gbc);

        gbc.gridx = 1; gbc.gridy = 2;
        p.add(crearEtiquetaCampo("DNI / RUC del Cliente *"), gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        cbCliente = new JComboBox<>();
        cbCliente.setEditable(true);
        cbCliente.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cbCliente.addItem("Carlos Mendoza Silva");
        for (Cliente c : repo.getClientes()) {
            cbCliente.addItem(c.getNombreCompleto());
        }
        cbCliente.addActionListener(e -> actualizarDniCliente());
        p.add(cbCliente, gbc);

        gbc.gridx = 1; gbc.gridy = 3;
        txtDniRuc = new JTextField("45892014");
        txtDniRuc.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        p.add(txtDniRuc, gbc);

        // 3. Mascota / Paciente & Cajero
        gbc.gridx = 0; gbc.gridy = 4;
        p.add(crearEtiquetaCampo("Mascota / Paciente Atendido"), gbc);

        gbc.gridx = 1; gbc.gridy = 4;
        p.add(crearEtiquetaCampo("Cajero Responsable"), gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        cbMascota = new JComboBox<>();
        cbMascota.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cbMascota.addItem("Max (Golden Retriever)");
        for (Cliente c : repo.getClientes()) {
            for (Mascota m : c.getMascotas()) {
                cbMascota.addItem(m.getNombre() + " (" + m.getEspecie() + ")");
            }
        }
        p.add(cbMascota, gbc);

        gbc.gridx = 1; gbc.gridy = 5;
        txtCajero = new JTextField("Juan Pérez Castro (Caja Central)");
        txtCajero.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        p.add(txtCajero, gbc);

        // 4. Método de Pago & Monto Recibido
        gbc.gridx = 0; gbc.gridy = 6;
        p.add(crearEtiquetaCampo("Método de Pago *"), gbc);

        gbc.gridx = 1; gbc.gridy = 6;
        p.add(crearEtiquetaCampo("Importe Recibido (Efectivo) S/."), gbc);

        gbc.gridx = 0; gbc.gridy = 7;
        cbMetodoPago = new JComboBox<>(new String[]{
                "Efectivo",
                "Yape / Plin",
                "Tarjeta Débito / Crédito",
                "Transferencia Bancaria",
                "Crédito (Cuenta por Cobrar)"
        });
        cbMetodoPago.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cbMetodoPago.addActionListener(e -> verificarMetodoPago());
        p.add(cbMetodoPago, gbc);

        gbc.gridx = 1; gbc.gridy = 7;
        txtMontoRecibido = new JTextField("100.00");
        txtMontoRecibido.setFont(new Font("Segoe UI", Font.BOLD, 12));
        txtMontoRecibido.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { recalcularVuelto(); }
            public void removeUpdate(DocumentEvent e) { recalcularVuelto(); }
            public void changedUpdate(DocumentEvent e) { recalcularVuelto(); }
        });
        p.add(txtMontoRecibido, gbc);

        // 5. Card resumen de Vuelto / Liquidación
        gbc.gridx = 0; gbc.gridy = 8; gbc.gridwidth = 2;
        JPanel panelVuelto = new JPanel(new GridLayout(1, 2, 12, 0)) {
            private static final long serialVersionUID = 1L;
            @Override
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
        panelVuelto.setOpaque(false);
        panelVuelto.setBorder(new EmptyBorder(12, 16, 12, 16));

        JLabel lblLeyendaVuelto = new JLabel("Vuelto a Entregar al Cliente:");
        lblLeyendaVuelto.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblLeyendaVuelto.setForeground(Ui.TURQUESA_PROFUNDO);

        lblVueltoCalculado = new JLabel("S/. 0.00", SwingConstants.RIGHT);
        lblVueltoCalculado.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblVueltoCalculado.setForeground(Ui.COLOR_EXITO);

        panelVuelto.add(lblLeyendaVuelto);
        panelVuelto.add(lblVueltoCalculado);
        p.add(panelVuelto, gbc);

        return p;
    }

    private JLabel crearEtiquetaCampo(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(new Font("Segoe UI", Font.BOLD, 11));
        l.setForeground(Ui.TEXTO_MUTED);
        return l;
    }

    private void actualizarPrecioUnitarioSeleccionado() {
        int idx = cbProductoServicio.getSelectedIndex();
        if (idx >= 0 && idx < opcionesCatalogo.size()) {
            CatalogoOpcion opt = opcionesCatalogo.get(idx);
            lblPrecioUnitario.setText(String.format("S/. %.2f", opt.precio));
        }
    }

    private void agregarItemAlCarrito() {
        int idx = cbProductoServicio.getSelectedIndex();
        if (idx < 0 || idx >= opcionesCatalogo.size()) return;

        CatalogoOpcion opt = opcionesCatalogo.get(idx);
        int cant = (Integer) spinCantidad.getValue();

        // Buscar si ya existe en el carrito
        boolean existe = false;
        for (ItemVentaPOS item : itemsCarrito) {
            if (item.getCodigo().equalsIgnoreCase(opt.codigo)) {
                item.incrementarCantidad(cant);
                existe = true;
                break;
            }
        }

        if (!existe) {
            itemsCarrito.add(new ItemVentaPOS(opt.codigo, opt.nombre, opt.categoria, cant, opt.precio));
        }

        spinCantidad.setValue(1);
        actualizarTablaCarrito();
    }

    private void quitarItemSeleccionado() {
        int row = tablaItems.getSelectedRow();
        if (row >= 0 && row < itemsCarrito.size()) {
            itemsCarrito.remove(row);
            actualizarTablaCarrito();
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione un ítem del carrito para quitar.", "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void actualizarTablaCarrito() {
        modeloItems.setRowCount(0);
        for (ItemVentaPOS it : itemsCarrito) {
            modeloItems.addRow(new Object[]{
                    it.getCodigo(),
                    it.getDescripcion(),
                    it.getCategoria(),
                    it.getCantidad(),
                    String.format("%.2f", it.getPrecioUnitario()),
                    String.format("%.2f", it.getSubtotal())
            });
        }
        recalcularTotalesCarrito();
    }

    private void recalcularTotalesCarrito() {
        double subtotal = 0.0;
        for (ItemVentaPOS it : itemsCarrito) {
            subtotal += it.getSubtotal();
        }

        int descPorc = (Integer) spinDescuentoPorc.getValue();
        double montoDesc = subtotal * (descPorc / 100.0);
        double total = Math.max(0.0, subtotal - montoDesc);

        lblSubtotalBruto.setText(String.format("Subtotal: S/. %.2f", subtotal));
        lblMontoDescuento.setText(String.format("(-S/. %.2f)", montoDesc));
        lblTotalVenta.setText(String.format("TOTAL: S/. %.2f", total));

        recalcularVuelto();
    }

    private void recalcularVuelto() {
        if (txtMontoRecibido == null || lblVueltoCalculado == null) return;
        try {
            double recibido = Double.parseDouble(txtMontoRecibido.getText().trim());
            double subtotal = 0.0;
            for (ItemVentaPOS it : itemsCarrito) {
                subtotal += it.getSubtotal();
            }
            int descPorc = (Integer) spinDescuentoPorc.getValue();
            double total = Math.max(0.0, subtotal - (subtotal * (descPorc / 100.0)));

            double vuelto = Math.max(0.0, recibido - total);
            lblVueltoCalculado.setText(String.format("S/. %.2f", vuelto));
        } catch (NumberFormatException ignored) {
            lblVueltoCalculado.setText("S/. 0.00");
        }
    }

    private void verificarMetodoPago() {
        String metodo = (String) cbMetodoPago.getSelectedItem();
        boolean esEfectivo = "Efectivo".equalsIgnoreCase(metodo);
        txtMontoRecibido.setEnabled(esEfectivo);
        if (!esEfectivo) {
            txtMontoRecibido.setText("0.00");
            lblVueltoCalculado.setText("S/. 0.00");
        }
    }

    private void generarNumeroComprobanteSugerido() {
        String tipo = (String) cbTipoComprobante.getSelectedItem();
        int seq = repo.getVentasPOS().size() + 415;
        if ("Factura Electrónica".equals(tipo)) {
            txtNumeroComprobante.setText("F001-" + String.format("%06d", seq));
        } else if ("Ticket POS".equals(tipo)) {
            txtNumeroComprobante.setText("TK-" + String.format("%06d", seq));
        } else if ("Cotización".equals(tipo)) {
            txtNumeroComprobante.setText("COT-" + String.format("%06d", seq));
        } else {
            txtNumeroComprobante.setText("B001-" + String.format("%06d", seq));
        }
    }

    private void actualizarDniCliente() {
        String clienteStr = (String) cbCliente.getSelectedItem();
        if (clienteStr == null) return;
        for (Cliente c : repo.getClientes()) {
            if (c.getNombreCompleto().equalsIgnoreCase(clienteStr)) {
                txtDniRuc.setText(c.getNumeroDocumento());
                return;
            }
        }
    }

    private void procesarEmisionVenta() {
        if (itemsCarrito.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Debe agregar al menos un producto o servicio al carrito de venta.",
                    "Carrito Vacío", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String cliente = cbCliente.getSelectedItem() != null ? cbCliente.getSelectedItem().toString().trim() : "Cliente Varios";
        String dni = txtDniRuc.getText().trim();
        String mascota = cbMascota.getSelectedItem() != null ? cbMascota.getSelectedItem().toString() : "-";
        String tipoComp = (String) cbTipoComprobante.getSelectedItem();
        String numComp = txtNumeroComprobante.getText().trim();
        String metodo = (String) cbMetodoPago.getSelectedItem();
        String cajero = txtCajero.getText().trim();
        int descPorc = (Integer) spinDescuentoPorc.getValue();

        double montoRecibido = 0.0;
        if ("Efectivo".equalsIgnoreCase(metodo)) {
            try {
                montoRecibido = Double.parseDouble(txtMontoRecibido.getText().trim());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Ingrese un importe recibido válido.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        String estado = "Cotización".equals(tipoComp) ? "Cotización" : "Pagada";

        VentaPOS nuevaVenta = new VentaPOS(
                "VTA-2026-" + String.format("%03d", repo.getVentasPOS().size() + 1),
                numComp,
                tipoComp,
                LocalDateTime.now(),
                cliente,
                dni,
                mascota,
                itemsCarrito,
                descPorc,
                metodo,
                montoRecibido,
                estado,
                cajero
        );

        repo.guardarVentaPOS(nuevaVenta);
        confirmado = true;

        JOptionPane.showMessageDialog(this,
                "Comprobante " + numComp + " emitido exitosamente por un total de S/. " + String.format("%.2f", nuevaVenta.getTotal()),
                "Venta Procesada", JOptionPane.INFORMATION_MESSAGE);

        dispose();
    }

    public boolean isConfirmado() {
        return confirmado;
    }

    private static class CatalogoOpcion {
        final String codigo;
        final String nombre;
        final String categoria;
        final double precio;

        CatalogoOpcion(String codigo, String nombre, String categoria, double precio) {
            this.codigo = codigo;
            this.nombre = nombre;
            this.categoria = categoria;
            this.precio = precio;
        }
    }
}
