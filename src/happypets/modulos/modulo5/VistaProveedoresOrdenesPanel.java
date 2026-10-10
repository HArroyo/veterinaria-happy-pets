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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import happypets.data.RepositorioVeterinaria;
import happypets.model.OrdenCompra;
import happypets.model.ProveedorFarmacia;
import happypets.ui.Iconos;

/**
 * Submódulo 5.3: Proveedores y Órdenes de Compra.
 * Basado en el wireframe oficial (Pág. 2) y especificaciones ERP Happy Pets:
 * - Directorio maestro de laboratorios farmacéuticos y distribuidoras veterinarias.
 * - Creación y emisión de Órdenes de Compra con cálculo de IGV (18%).
 * - Pipeline de estados (Borrador -> Enviada a Proveedor -> Recibida en Almacén).
 * - Recepción de mercadería con incremento directo en inventario.
 */
public class VistaProveedoresOrdenesPanel extends happypets.ui.AssetsModulo {
    private static final long serialVersionUID = 1L;

    private static final Color COLOR_BORDE = new Color(226, 232, 240);
    private static final Color COLOR_AZUL_PRIMARIO = new Color(2, 132, 199);
    private static final Color COLOR_TEXTO_TITULO = new Color(30, 41, 59);
    private static final Color COLOR_TEXTO_MUTED = new Color(100, 116, 139);

    private final RepositorioVeterinaria repo = RepositorioVeterinaria.getInstancia();

    // KPIs
    private JLabel lblKpiProveedoresActivos;
    private JLabel lblKpiOrdenesTransito;
    private JLabel lblKpiOrdenesRecibidas;
    private JLabel lblKpiGastoTotalCompras;

    // Formulario Proveedor
    private JTextField txtRuc;
    private JTextField txtRazonSocial;
    private JTextField txtNombreComercial;
    private JTextField txtTelefono;
    private JTextField txtCorreo;
    private JTextField txtDireccion;
    private JTextField txtAsesor;
    private JComboBox<String> cbCondicionPago;

    // Formulario Orden de Compra
    private JComboBox<ProveedorItem> cbProveedorOrden;
    private JTextField txtItemsOrden;
    private JTextField txtSubtotalOrden;
    private JTextField txtIgvOrden;
    private JTextField txtTotalOrden;
    private JTextField txtFechaEntrega;
    private JTextField txtSolicitante;

    // Tablas
    private JTabbedPane tabTablas;
    private JTable tablaProveedores;
    private DefaultTableModel modeloProveedores;
    private JTable tablaOrdenes;
    private DefaultTableModel modeloOrdenes;

    public VistaProveedoresOrdenesPanel() {
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

        // 3. Doble Columna: Formularios en Pestañas y Tablas de Gestión
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

        JLabel titulo = new JLabel("Proveedores y Órdenes de Compra");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 17));
        titulo.setForeground(COLOR_TEXTO_TITULO);

        JLabel subtitulo = new JLabel("Módulo 5.3 · Homologación de laboratorios, compras valorizadas, control de créditos y recepción de stock");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitulo.setForeground(COLOR_TEXTO_MUTED);

        izq.add(titulo);
        izq.add(Box.createVerticalStrut(2));
        izq.add(subtitulo);
        cab.add(izq, BorderLayout.CENTER);

        JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        der.setOpaque(false);

        JLabel badgeCompras = new JLabel(" Abastecimiento & Compras Farmacéuticas ", SwingConstants.CENTER);
        badgeCompras.setFont(new Font("Segoe UI", Font.BOLD, 11));
        badgeCompras.setForeground(new Color(2, 132, 199));
        badgeCompras.setOpaque(true);
        badgeCompras.setBackground(new Color(224, 242, 254));
        badgeCompras.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(186, 230, 253), 1, true),
                new EmptyBorder(4, 10, 4, 10)
        ));
        der.add(badgeCompras);

        cab.add(der, BorderLayout.EAST);
        return cab;
    }

    private JPanel crearFilaKpis() {
        JPanel kpis = new JPanel(new GridLayout(1, 4, 10, 0));
        kpis.setOpaque(false);

        lblKpiProveedoresActivos = new JLabel("0", SwingConstants.LEFT);
        lblKpiOrdenesTransito = new JLabel("0", SwingConstants.LEFT);
        lblKpiOrdenesRecibidas = new JLabel("0", SwingConstants.LEFT);
        lblKpiGastoTotalCompras = new JLabel("S/. 0", SwingConstants.LEFT);

        kpis.add(crearTarjetaKpi("Proveedores Homologados", lblKpiProveedoresActivos, "Laboratorios y distribuidoras", new Color(14, 165, 233), Iconos.crearIconoCamionProveedor(18, new Color(14, 165, 233))));
        kpis.add(crearTarjetaKpi("Órdenes en Tránsito", lblKpiOrdenesTransito, "Enviadas / Por recibir", new Color(245, 158, 11), Iconos.crearIconoReloj(16, new Color(245, 158, 11))));
        kpis.add(crearTarjetaKpi("Órdenes Ingresadas", lblKpiOrdenesRecibidas, "Recibidas en almacén", new Color(16, 185, 129), Iconos.crearIconoCheck(16, new Color(16, 185, 129))));
        kpis.add(crearTarjetaKpi("Inversión en Compras", lblKpiGastoTotalCompras, "Monto valorizado (Mes)", new Color(99, 102, 241), Iconos.crearIconoFactura(16, new Color(99, 102, 241))));

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

        // Columna Izquierda: Pestañas de Formularios (420px)
        JPanel colIzquierda = crearPanelFormulariosPestanas();
        colIzquierda.setPreferredSize(new Dimension(420, 560));
        fila.add(colIzquierda, BorderLayout.WEST);

        // Columna Derecha: Tablas de Proveedores y Órdenes
        JPanel colDerecha = crearPanelTablasGestion();
        fila.add(colDerecha, BorderLayout.CENTER);

        return fila;
    }

    private JPanel crearPanelFormulariosPestanas() {
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
        card.setBorder(new EmptyBorder(10, 10, 10, 10));

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 11));

        // Pestaña 1: Formulario Proveedor
        tabs.addTab("Nuevo Proveedor", crearFormularioProveedor());

        // Pestaña 2: Formulario Orden de Compra
        tabs.addTab("Generar Orden de Compra", crearFormularioOrdenCompra());

        card.add(tabs, BorderLayout.CENTER);
        return card;
    }

    private JPanel crearFormularioProveedor() {
        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(new EmptyBorder(8, 8, 8, 8));

        // RUC y Razón Social
        JPanel gridRucNom = new JPanel(new GridLayout(1, 2, 8, 0));
        gridRucNom.setOpaque(false);
        gridRucNom.setAlignmentX(Component.LEFT_ALIGNMENT);
        gridRucNom.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));

        JPanel pRuc = new JPanel();
        pRuc.setOpaque(false);
        pRuc.setAlignmentX(Component.LEFT_ALIGNMENT);
        pRuc.setLayout(new BoxLayout(pRuc, BoxLayout.Y_AXIS));
        pRuc.add(crearEtiquetaCampo("RUC Proveedor:"));
        txtRuc = new JTextField();
        estilizarControl(txtRuc);
        pRuc.add(txtRuc);
        gridRucNom.add(pRuc);

        JPanel pTel = new JPanel();
        pTel.setOpaque(false);
        pTel.setAlignmentX(Component.LEFT_ALIGNMENT);
        pTel.setLayout(new BoxLayout(pTel, BoxLayout.Y_AXIS));
        pTel.add(crearEtiquetaCampo("Teléfono:"));
        txtTelefono = new JTextField();
        estilizarControl(txtTelefono);
        pTel.add(txtTelefono);
        gridRucNom.add(pTel);

        form.add(gridRucNom);
        form.add(Box.createVerticalStrut(6));

        form.add(crearEtiquetaCampo("Razón Social:"));
        txtRazonSocial = new JTextField();
        estilizarControl(txtRazonSocial);
        form.add(txtRazonSocial);
        form.add(Box.createVerticalStrut(6));

        form.add(crearEtiquetaCampo("Nombre Comercial / Marca:"));
        txtNombreComercial = new JTextField();
        estilizarControl(txtNombreComercial);
        form.add(txtNombreComercial);
        form.add(Box.createVerticalStrut(6));

        form.add(crearEtiquetaCampo("Correo Electrónico de Pedidos:"));
        txtCorreo = new JTextField();
        estilizarControl(txtCorreo);
        form.add(txtCorreo);
        form.add(Box.createVerticalStrut(6));

        form.add(crearEtiquetaCampo("Asesor Comercial / Contacto Directo:"));
        txtAsesor = new JTextField();
        estilizarControl(txtAsesor);
        form.add(txtAsesor);
        form.add(Box.createVerticalStrut(6));

        form.add(crearEtiquetaCampo("Condición de Pago Comercial:"));
        cbCondicionPago = new JComboBox<>(new String[]{"Crédito 30 días", "Contado Factura", "Crédito 15 días", "Contraentrega"});
        estilizarControl(cbCondicionPago);
        form.add(cbCondicionPago);
        form.add(Box.createVerticalStrut(14));

        JPanel pBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        pBotones.setOpaque(false);
        pBotones.setAlignmentX(Component.LEFT_ALIGNMENT);
        pBotones.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        JButton btnLimpiar = crearBoton("Limpiar", false, this::limpiarFormProveedor);
        JButton btnGuardar = crearBoton("Guardar Proveedor", true, this::guardarProveedor);

        pBotones.add(btnLimpiar);
        pBotones.add(btnGuardar);
        form.add(pBotones);

        return form;
    }

    private JPanel crearFormularioOrdenCompra() {
        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(new EmptyBorder(8, 8, 8, 8));

        form.add(crearEtiquetaCampo("Laboratorio / Proveedor:"));
        cbProveedorOrden = new JComboBox<>();
        estilizarControl(cbProveedorOrden);
        form.add(cbProveedorOrden);
        form.add(Box.createVerticalStrut(6));

        form.add(crearEtiquetaCampo("Detalle de Productos y Cantidades Solicitadas:"));
        txtItemsOrden = new JTextField("30x Amoxicilina Vet, 20x NexGard Spectra");
        estilizarControl(txtItemsOrden);
        form.add(txtItemsOrden);
        form.add(Box.createVerticalStrut(6));

        // Subtotal, IGV y Total
        JPanel gridValores = new JPanel(new GridLayout(1, 3, 6, 0));
        gridValores.setOpaque(false);
        gridValores.setAlignmentX(Component.LEFT_ALIGNMENT);
        gridValores.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));

        JPanel pSub = new JPanel();
        pSub.setOpaque(false);
        pSub.setAlignmentX(Component.LEFT_ALIGNMENT);
        pSub.setLayout(new BoxLayout(pSub, BoxLayout.Y_AXIS));
        pSub.add(crearEtiquetaCampo("Subtotal S/.:"));
        txtSubtotalOrden = new JTextField("1200.00");
        estilizarControl(txtSubtotalOrden);
        txtSubtotalOrden.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override public void keyReleased(java.awt.event.KeyEvent e) { calcularTotalesOrden(); }
        });
        pSub.add(txtSubtotalOrden);
        gridValores.add(pSub);

        JPanel pIgv = new JPanel();
        pIgv.setOpaque(false);
        pIgv.setAlignmentX(Component.LEFT_ALIGNMENT);
        pIgv.setLayout(new BoxLayout(pIgv, BoxLayout.Y_AXIS));
        pIgv.add(crearEtiquetaCampo("IGV (18%):"));
        txtIgvOrden = new JTextField("216.00");
        estilizarControl(txtIgvOrden);
        txtIgvOrden.setEditable(false);
        pIgv.add(txtIgvOrden);
        gridValores.add(pIgv);

        JPanel pTot = new JPanel();
        pTot.setOpaque(false);
        pTot.setAlignmentX(Component.LEFT_ALIGNMENT);
        pTot.setLayout(new BoxLayout(pTot, BoxLayout.Y_AXIS));
        pTot.add(crearEtiquetaCampo("Total S/.:"));
        txtTotalOrden = new JTextField("1416.00");
        estilizarControl(txtTotalOrden);
        txtTotalOrden.setEditable(false);
        pTot.add(txtTotalOrden);
        gridValores.add(pTot);

        form.add(gridValores);
        form.add(Box.createVerticalStrut(6));

        form.add(crearEtiquetaCampo("Fecha de Entrega Estimada:"));
        txtFechaEntrega = new JTextField(LocalDate.now().plusDays(4).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        estilizarControl(txtFechaEntrega);
        form.add(txtFechaEntrega);
        form.add(Box.createVerticalStrut(6));

        form.add(crearEtiquetaCampo("Solicitado por:"));
        txtSolicitante = new JTextField("Dra. Elena Ruiz (Farmacia)");
        estilizarControl(txtSolicitante);
        form.add(txtSolicitante);
        form.add(Box.createVerticalStrut(14));

        JPanel pBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        pBotones.setOpaque(false);
        pBotones.setAlignmentX(Component.LEFT_ALIGNMENT);
        pBotones.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        JButton btnEmitir = crearBoton("Emitir Orden de Compra", true, this::guardarOrdenCompra);
        pBotones.add(btnEmitir);
        form.add(pBotones);

        return form;
    }

    private JPanel crearPanelTablasGestion() {
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

        tabTablas = new JTabbedPane();
        tabTablas.setFont(new Font("Segoe UI", Font.BOLD, 11));

        // Subpestaña 1: Proveedores
        tabTablas.addTab("Directorio de Proveedores", crearPanelTablaProveedores());

        // Subpestaña 2: Órdenes de Compra
        tabTablas.addTab("Órdenes de Compra Emitidas", crearPanelTablaOrdenes());

        card.add(tabTablas, BorderLayout.CENTER);
        return card;
    }

    private JPanel crearPanelTablaProveedores() {
        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.setOpaque(false);

        String[] cols = {"RUC", "Razón Social", "Nombre Comercial", "Teléfono", "Contacto Asesor", "Condición Pago"};
        modeloProveedores = new DefaultTableModel(cols, 0) {
            private static final long serialVersionUID = 1L;
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tablaProveedores = new JTable(modeloProveedores);
        tablaProveedores.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        tablaProveedores.setRowHeight(26);
        tablaProveedores.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        tablaProveedores.getTableHeader().setBackground(Color.WHITE);
        tablaProveedores.getTableHeader().setForeground(new Color(71, 85, 105));
        tablaProveedores.setSelectionBackground(new Color(224, 242, 254));
        tablaProveedores.setSelectionForeground(new Color(3, 105, 161));
        tablaProveedores.setShowGrid(false);
        tablaProveedores.setIntercellSpacing(new Dimension(0, 0));

        tablaProveedores.getColumnModel().getColumn(0).setPreferredWidth(90);
        tablaProveedores.getColumnModel().getColumn(1).setPreferredWidth(170);
        tablaProveedores.getColumnModel().getColumn(2).setPreferredWidth(120);
        tablaProveedores.getColumnModel().getColumn(3).setPreferredWidth(85);
        tablaProveedores.getColumnModel().getColumn(4).setPreferredWidth(130);
        tablaProveedores.getColumnModel().getColumn(5).setPreferredWidth(100);

        JScrollPane scroll = new JScrollPane(tablaProveedores);
        scroll.setBorder(BorderFactory.createLineBorder(COLOR_BORDE, 1));
        scroll.getViewport().setBackground(Color.WHITE);
        p.add(scroll, BorderLayout.CENTER);

        JPanel pAcciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        pAcciones.setOpaque(false);

        JButton btnEliminar = crearBoton("Eliminar Proveedor", false, this::eliminarProveedor);
        btnEliminar.setIcon(Iconos.crearIconoCruzMedica(14, new Color(220, 38, 38)));
        pAcciones.add(btnEliminar);

        p.add(pAcciones, BorderLayout.SOUTH);
        return p;
    }

    private JPanel crearPanelTablaOrdenes() {
        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.setOpaque(false);

        String[] cols = {"ID Orden", "Proveedor", "Emisión", "Entrega Est.", "Items Solicitados", "Total S/.", "Estado"};
        modeloOrdenes = new DefaultTableModel(cols, 0) {
            private static final long serialVersionUID = 1L;
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tablaOrdenes = new JTable(modeloOrdenes);
        tablaOrdenes.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        tablaOrdenes.setRowHeight(26);
        tablaOrdenes.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        tablaOrdenes.getTableHeader().setBackground(Color.WHITE);
        tablaOrdenes.getTableHeader().setForeground(new Color(71, 85, 105));
        tablaOrdenes.setSelectionBackground(new Color(224, 242, 254));
        tablaOrdenes.setSelectionForeground(new Color(3, 105, 161));
        tablaOrdenes.setShowGrid(false);
        tablaOrdenes.setIntercellSpacing(new Dimension(0, 0));

        tablaOrdenes.getColumnModel().getColumn(0).setPreferredWidth(75);
        tablaOrdenes.getColumnModel().getColumn(1).setPreferredWidth(160);
        tablaOrdenes.getColumnModel().getColumn(2).setPreferredWidth(75);
        tablaOrdenes.getColumnModel().getColumn(3).setPreferredWidth(75);
        tablaOrdenes.getColumnModel().getColumn(4).setPreferredWidth(160);
        tablaOrdenes.getColumnModel().getColumn(5).setPreferredWidth(75);
        tablaOrdenes.getColumnModel().getColumn(6).setPreferredWidth(125);

        // Renderizador de Estado
        tablaOrdenes.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            private static final long serialVersionUID = 1L;
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                l.setHorizontalAlignment(SwingConstants.CENTER);
                String st = value != null ? value.toString() : "";
                if (st.contains("Recibida")) {
                    l.setForeground(new Color(16, 185, 129));
                    l.setBackground(new Color(209, 250, 229));
                } else if (st.contains("Enviada")) {
                    l.setForeground(new Color(2, 132, 199));
                    l.setBackground(new Color(224, 242, 254));
                } else {
                    l.setForeground(new Color(245, 158, 11));
                    l.setBackground(new Color(254, 243, 199));
                }
                l.setOpaque(true);
                return l;
            }
        });

        JScrollPane scroll = new JScrollPane(tablaOrdenes);
        scroll.setBorder(BorderFactory.createLineBorder(COLOR_BORDE, 1));
        scroll.getViewport().setBackground(Color.WHITE);
        p.add(scroll, BorderLayout.CENTER);

        JPanel pAcciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        pAcciones.setOpaque(false);

        JButton btnImprimir = crearBoton("Imprimir Orden Oficial", false, this::imprimirOrdenOficial);
        btnImprimir.setIcon(Iconos.crearIconoImprimir(14, COLOR_AZUL_PRIMARIO));

        JButton btnRecepcion = crearBoton("Recepción de Mercadería (Ingreso Almacén)", true, this::recepcionarMercaderia);
        btnRecepcion.setIcon(Iconos.crearIconoCheck(14, Color.WHITE));

        pAcciones.add(btnImprimir);
        pAcciones.add(btnRecepcion);

        p.add(pAcciones, BorderLayout.SOUTH);
        return p;
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
        // Cargar proveedores
        List<ProveedorFarmacia> provs = repo.getProveedoresFarmacia();
        modeloProveedores.setRowCount(0);
        cbProveedorOrden.removeAllItems();

        for (ProveedorFarmacia pr : provs) {
            cbProveedorOrden.addItem(new ProveedorItem(pr));
            modeloProveedores.addRow(new Object[]{
                    pr.getRuc(),
                    pr.getRazonSocial(),
                    pr.getNombreComercial(),
                    pr.getTelefono(),
                    pr.getContactoAsesor(),
                    pr.getCondicionPago()
            });
        }

        // Cargar órdenes
        List<OrdenCompra> ordenes = repo.getOrdenesCompra();
        modeloOrdenes.setRowCount(0);
        double totalGasto = 0;
        long transito = 0;
        long recibidas = 0;

        for (OrdenCompra oc : ordenes) {
            totalGasto += oc.getTotal();
            if ("Enviada a Proveedor".equalsIgnoreCase(oc.getEstado())) transito++;
            if ("Recibida en Almacén".equalsIgnoreCase(oc.getEstado())) recibidas++;

            modeloOrdenes.addRow(new Object[]{
                    oc.getIdOrden(),
                    oc.getNombreProveedor(),
                    oc.getFechaEmisionFormateada(),
                    oc.getFechaEntregaEstimadaFormateada(),
                    oc.getItemsResumen(),
                    "S/. " + String.format("%.2f", oc.getTotal()),
                    oc.getEstado()
            });
        }

        lblKpiProveedoresActivos.setText(String.valueOf(provs.size()));
        lblKpiOrdenesTransito.setText(String.valueOf(transito));
        lblKpiOrdenesRecibidas.setText(String.valueOf(recibidas));
        lblKpiGastoTotalCompras.setText("S/. " + String.format("%.0f", totalGasto));
    }

    private void calcularTotalesOrden() {
        try {
            double sub = Double.parseDouble(txtSubtotalOrden.getText().trim());
            double igv = sub * 0.18;
            double tot = sub + igv;
            txtIgvOrden.setText(String.format("%.2f", igv));
            txtTotalOrden.setText(String.format("%.2f", tot));
        } catch (Exception ignored) {}
    }

    private void guardarProveedor() {
        String ruc = txtRuc.getText().trim();
        String razon = txtRazonSocial.getText().trim();
        if (ruc.isEmpty() || razon.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Complete el RUC y Razón Social del proveedor.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        ProveedorFarmacia p = new ProveedorFarmacia(
                ruc,
                razon,
                txtNombreComercial.getText().trim(),
                txtTelefono.getText().trim(),
                txtCorreo.getText().trim(),
                "San Isidro, Lima",
                txtAsesor.getText().trim(),
                (String) cbCondicionPago.getSelectedItem(),
                "Homologado / Activo"
        );

        repo.guardarProveedorFarmacia(p);
        recargarDatos();
        limpiarFormProveedor();
        JOptionPane.showMessageDialog(this, "Proveedor " + razon + " registrado con éxito.", "Registro Homologado", JOptionPane.INFORMATION_MESSAGE);
    }

    private void eliminarProveedor() {
        int fila = tablaProveedores.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un proveedor para eliminar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String ruc = (String) modeloProveedores.getValueAt(fila, 0);
        int r = JOptionPane.showConfirmDialog(this, "¿Está seguro de eliminar el proveedor con RUC " + ruc + "?", "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);
        if (r == JOptionPane.YES_OPTION) {
            repo.eliminarProveedorFarmacia(ruc);
            recargarDatos();
        }
    }

    private void guardarOrdenCompra() {
        ProveedorItem pi = (ProveedorItem) cbProveedorOrden.getSelectedItem();
        if (pi == null || pi.proveedor == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un proveedor para la orden de compra.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double sub = 1000.0;
        try { sub = Double.parseDouble(txtSubtotalOrden.getText().trim()); } catch (Exception ignored) {}
        double igv = sub * 0.18;
        double tot = sub + igv;

        LocalDate entrega = LocalDate.now().plusDays(4);
        try {
            entrega = LocalDate.parse(txtFechaEntrega.getText().trim(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } catch (Exception ignored) {}

        OrdenCompra oc = new OrdenCompra(
                null,
                pi.proveedor.getRuc(),
                pi.proveedor.getRazonSocial(),
                LocalDate.now(),
                entrega,
                txtItemsOrden.getText().trim(),
                sub,
                igv,
                tot,
                "Enviada a Proveedor",
                txtSolicitante.getText().trim()
        );

        repo.guardarOrdenCompra(oc);
        recargarDatos();
        tabTablas.setSelectedIndex(1);

        JOptionPane.showMessageDialog(this,
                "¡Orden de Compra emitida exitosamente!\n\n" +
                        "Código: " + oc.getIdOrden() + "\n" +
                        "Proveedor: " + oc.getNombreProveedor() + "\n" +
                        "Subtotal: S/. " + String.format("%.2f", sub) + " | IGV: S/. " + String.format("%.2f", igv) + "\n" +
                        "TOTAL ORDEN: S/. " + String.format("%.2f", tot) + "\n" +
                        "Estado: Enviada a Proveedor",
                "Orden de Compra Generada", JOptionPane.INFORMATION_MESSAGE);
    }

    private void recepcionarMercaderia() {
        int fila = tablaOrdenes.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una orden de compra para recepcionar en almacén.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String idOrden = (String) modeloOrdenes.getValueAt(fila, 0);
        Optional<OrdenCompra> opt = repo.getOrdenesCompra().stream().filter(o -> o.getIdOrden().equalsIgnoreCase(idOrden)).findFirst();
        if (!opt.isPresent()) return;

        OrdenCompra oc = opt.get();
        if ("Recibida en Almacén".equalsIgnoreCase(oc.getEstado())) {
            JOptionPane.showMessageDialog(this, "Esta orden de compra ya fue ingresada y descargada en almacén.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int r = JOptionPane.showConfirmDialog(this,
                "¿Confirma la recepción física y conformidad de la orden " + oc.getIdOrden() + "?\n\n" +
                        "Proveedor: " + oc.getNombreProveedor() + "\n" +
                        "Items: " + oc.getItemsResumen() + "\n" +
                        "Total Factura: S/. " + String.format("%.2f", oc.getTotal()) + "\n\n" +
                        "Se registrará automáticamente el ingreso al Kardex de Farmacia.",
                "Confirmar Recepción de Mercadería", JOptionPane.YES_NO_OPTION);

        if (r == JOptionPane.YES_OPTION) {
            repo.actualizarEstadoOrdenCompra(oc.getIdOrden(), "Recibida en Almacén");
            recargarDatos();
            JOptionPane.showMessageDialog(this, "Recepción completada. Stock de fármacos incrementado en el Kardex de Happy Pets.", "Recepción Conforme", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void imprimirOrdenOficial() {
        int fila = tablaOrdenes.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una orden para emitir el documento oficial.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String idOrden = (String) modeloOrdenes.getValueAt(fila, 0);
        Optional<OrdenCompra> opt = repo.getOrdenesCompra().stream().filter(o -> o.getIdOrden().equalsIgnoreCase(idOrden)).findFirst();
        if (!opt.isPresent()) return;

        OrdenCompra oc = opt.get();
        String doc = "ORDEN DE COMPRA OFICIAL - CLÍNICA VETERINARIA HAPPY PETS S.A.C.\n" +
                "RUC: 20608912345 · AV. PRIMAVERA 1230, SURCO, LIMA\n" +
                "=========================================================================\n\n" +
                "NÚMERO DE ORDEN  : " + oc.getIdOrden() + "\n" +
                "FECHA DE EMISIÓN : " + oc.getFechaEmisionFormateada() + "  |  ENTREGA ESTIMADA: " + oc.getFechaEntregaEstimadaFormateada() + "\n" +
                "PROVEEDOR        : " + oc.getNombreProveedor() + " (RUC: " + oc.getRucProveedor() + ")\n" +
                "SOLICITADO POR   : " + oc.getSolicitante() + "\n" +
                "ESTADO ACTUAL    : " + oc.getEstado() + "\n\n" +
                "DETALLE DE REQUERIMIENTOS:\n" +
                "  • " + oc.getItemsResumen() + "\n\n" +
                "VALORIZACIÓN ECONÓMICA:\n" +
                "  SUBTOTAL AFECTO : S/. " + String.format("%10.2f", oc.getSubtotal()) + "\n" +
                "  I.G.V. (18%)    : S/. " + String.format("%10.2f", oc.getIgv()) + "\n" +
                "  TOTAL A FACTURAR: S/. " + String.format("%10.2f", oc.getTotal()) + "\n\n" +
                "CONDICIONES DE ENTREGA:\n" +
                "1. Adjuntar Guía de Remisión y Factura Electrónica indicando número de orden.\n" +
                "2. Fármacos con cadena de frío deben entregarse con termómetro digital activo (2°C-8°C).\n" +
                "3. Los lotes entregados deben tener una vigencia mínima de 12 meses.\n\n" +
                "__________________________             __________________________\n" +
                "   Responsable de Compras                  Administración Central\n" +
                "     Happy Pets Farmacia                     Firma Autorizada\n";

        JTextArea ta = new JTextArea(doc);
        ta.setFont(new Font("Consolas", Font.PLAIN, 11));
        ta.setEditable(false);
        JScrollPane sp = new JScrollPane(ta);
        sp.setPreferredSize(new Dimension(560, 360));

        JOptionPane.showMessageDialog(this, sp, "Orden de Compra Oficial (Impresión)", JOptionPane.INFORMATION_MESSAGE);
    }

    private void limpiarFormProveedor() {
        txtRuc.setText("");
        txtRazonSocial.setText("");
        txtNombreComercial.setText("");
        txtTelefono.setText("");
        txtCorreo.setText("");
        txtAsesor.setText("");
    }

    private static class ProveedorItem {
        final ProveedorFarmacia proveedor;
        ProveedorItem(ProveedorFarmacia p) { this.proveedor = p; }
        @Override public String toString() {
            if (proveedor == null) return "Seleccione...";
            return proveedor.getNombreComercial() + " (RUC: " + proveedor.getRuc() + ")";
        }
    }
}
