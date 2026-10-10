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
import java.time.temporal.ChronoUnit;
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
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import happypets.data.RepositorioVeterinaria;
import happypets.model.LoteMovimientoStock;
import happypets.model.ProductoFarmacia;
import happypets.ui.Iconos;
import happypets.ui.Ui;

/**
 * Submódulo 5.2: Control de Stock y Lotes (Kardex).
 * Diseñado según el wireframe oficial (Pág. 1) y especificaciones Happy Pets:
 * - Trazabilidad de lotes de fabricación y control FEFO (First Expired, First Out).
 * - Registro de ingresos por compras y salidas por consultas/cirugías.
 * - Alerta preventiva de vencimiento de fármacos (lotes a menos de 60 días).
 * - Generación de hojas de inventario físico y kardex valorizado.
 */
public class VistaControlStockLotesPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private static final Color COLOR_BORDE = Ui.BORDE_SUAVE;
    private static final Color COLOR_AZUL_PRIMARIO = Ui.TURQUESA;
    private static final Color COLOR_TEXTO_TITULO = new Color(30, 41, 59);
    private static final Color COLOR_TEXTO_MUTED = new Color(100, 116, 139);

    private final RepositorioVeterinaria repo = RepositorioVeterinaria.getInstancia();

    // KPIs
    private JLabel lblKpiLotesTotales;
    private JLabel lblKpiPorVencer;
    private JLabel lblKpiStockCritico;
    private JLabel lblKpiMovimientosHoy;

    // Formulario de Movimientos de Stock
    private JComboBox<ProductoItem> cbProducto;
    private JTextField txtLote;
    private JComboBox<String> cbTipoMovimiento;
    private JTextField txtCantidad;
    private JTextField txtFechaMovimiento;
    private JTextField txtFechaVencimiento;
    private JTextField txtResponsable;
    private JTextField txtObservacion;

    // Tabla de Movimientos
    private JTextField txtBuscar;
    private JComboBox<String> cbFiltroTipo;
    private JTable tablaMovimientos;
    private DefaultTableModel modeloMovimientos;
    private JLabel lblContadorMovimientos;
    private List<LoteMovimientoStock> listaActual;

    public VistaControlStockLotesPanel() {
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

        // 3. Doble Columna: Formulario y Kardex
        contenido.add(crearDobleColumna());

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(contenido, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(wrapper);
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

        JLabel titulo = new JLabel("Control de Stock y Lotes (Kardex de Farmacia)");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 17));
        titulo.setForeground(COLOR_TEXTO_TITULO);

        JLabel subtitulo = new JLabel("Módulo 5.2 · Trazabilidad de lotes clínicos, control FEFO de caducidad, movimientos de almacén y reposición");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitulo.setForeground(COLOR_TEXTO_MUTED);

        izq.add(titulo);
        izq.add(Box.createVerticalStrut(2));
        izq.add(subtitulo);
        cab.add(izq, BorderLayout.CENTER);

        JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        der.setOpaque(false);

        JLabel badgeFefo = new JLabel(" Política FEFO: Primero en Vencer, Primero en Salir ", SwingConstants.CENTER);
        badgeFefo.setFont(new Font("Segoe UI", Font.BOLD, 11));
        badgeFefo.setForeground(Ui.TURQUESA_PROFUNDO);
        badgeFefo.setOpaque(true);
        badgeFefo.setBackground(Ui.TURQUESA_SUAVE);
        badgeFefo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Ui.TURQUESA_MEDIO, 1, true),
                new EmptyBorder(4, 10, 4, 10)
        ));
        der.add(badgeFefo);

        cab.add(der, BorderLayout.EAST);
        return cab;
    }

    private JPanel crearFilaKpis() {
        JPanel kpis = new JPanel(new GridLayout(1, 4, 10, 0));
        kpis.setOpaque(false);

        lblKpiLotesTotales = new JLabel("0", SwingConstants.LEFT);
        lblKpiPorVencer = new JLabel("0", SwingConstants.LEFT);
        lblKpiStockCritico = new JLabel("0", SwingConstants.LEFT);
        lblKpiMovimientosHoy = new JLabel("0", SwingConstants.LEFT);

        kpis.add(crearTarjetaKpi("Lotes en Almacén", lblKpiLotesTotales, "Lotes con stock activo", new Color(14, 165, 233), Iconos.crearIconoCajaAlmacen(18, new Color(14, 165, 233))));
        kpis.add(crearTarjetaKpi("Próximos a Vencer (<60d)", lblKpiPorVencer, "Atención prioritaria", new Color(220, 38, 38), Iconos.crearIconoAlertaMerma(16, new Color(220, 38, 38))));
        kpis.add(crearTarjetaKpi("Productos en Quiebre", lblKpiStockCritico, "Stock menor al mínimo", new Color(245, 158, 11), Iconos.crearIconoReloj(16, new Color(245, 158, 11))));
        kpis.add(crearTarjetaKpi("Movimientos Registrados", lblKpiMovimientosHoy, "Kardex del período", new Color(16, 185, 129), Iconos.crearIconoCheck(16, new Color(16, 185, 129))));

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
        JPanel colIzquierda = crearPanelFormularioMovimiento();
        colIzquierda.setPreferredSize(new Dimension(420, 560));
        fila.add(colIzquierda, BorderLayout.WEST);

        // Columna Derecha: Kardex de Movimientos
        JPanel colDerecha = crearPanelKardexTabla();
        fila.add(colDerecha, BorderLayout.CENTER);

        return fila;
    }

    private JPanel crearPanelFormularioMovimiento() {
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
        JLabel lblTitulo = new JLabel("Registro de Movimientos de Stock");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTitulo.setForeground(COLOR_TEXTO_TITULO);
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(lblTitulo);

        JLabel lblSub = new JLabel("Entrada por compras o salida clínica con actualización automática de Kardex");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblSub.setForeground(COLOR_TEXTO_MUTED);
        lblSub.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(lblSub);
        form.add(Box.createVerticalStrut(10));

        // Producto
        form.add(crearEtiquetaCampo("Producto / Fármaco:"));
        cbProducto = new JComboBox<>();
        estilizarControl(cbProducto);
        form.add(cbProducto);
        form.add(Box.createVerticalStrut(6));

        // Tipo de Movimiento
        form.add(crearEtiquetaCampo("Tipo de Movimiento:"));
        cbTipoMovimiento = new JComboBox<>(new String[]{
                "Ingreso por Compra / Proveedor",
                "Salida por Consulta Médica",
                "Salida por Cirugía / Quirófano",
                "Salida por Dispensación Externa",
                "Ajuste Positivo (+ Sobrante)",
                "Ajuste Negativo (- Faltante)"
        });
        estilizarControl(cbTipoMovimiento);
        form.add(cbTipoMovimiento);
        form.add(Box.createVerticalStrut(6));

        // Lote y Cantidad en Grid
        JPanel gridLoteCant = new JPanel(new GridLayout(1, 2, 8, 0));
        gridLoteCant.setOpaque(false);
        gridLoteCant.setAlignmentX(Component.LEFT_ALIGNMENT);
        gridLoteCant.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));

        JPanel pLote = new JPanel();
        pLote.setOpaque(false);
        pLote.setAlignmentX(Component.LEFT_ALIGNMENT);
        pLote.setLayout(new BoxLayout(pLote, BoxLayout.Y_AXIS));
        pLote.add(crearEtiquetaCampo("Número de Lote:"));
        txtLote = new JTextField("LOT-2024-90B");
        estilizarControl(txtLote);
        pLote.add(txtLote);
        gridLoteCant.add(pLote);

        JPanel pCant = new JPanel();
        pCant.setOpaque(false);
        pCant.setAlignmentX(Component.LEFT_ALIGNMENT);
        pCant.setLayout(new BoxLayout(pCant, BoxLayout.Y_AXIS));
        pCant.add(crearEtiquetaCampo("Cantidad (Unidades):"));
        txtCantidad = new JTextField("10");
        estilizarControl(txtCantidad);
        pCant.add(txtCantidad);
        gridLoteCant.add(pCant);

        form.add(gridLoteCant);
        form.add(Box.createVerticalStrut(6));

        // Fechas de Movimiento y Vencimiento del Lote
        JPanel gridFechas = new JPanel(new GridLayout(1, 2, 8, 0));
        gridFechas.setOpaque(false);
        gridFechas.setAlignmentX(Component.LEFT_ALIGNMENT);
        gridFechas.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));

        JPanel pMov = new JPanel();
        pMov.setOpaque(false);
        pMov.setAlignmentX(Component.LEFT_ALIGNMENT);
        pMov.setLayout(new BoxLayout(pMov, BoxLayout.Y_AXIS));
        pMov.add(crearEtiquetaCampo("Fecha de Operación:"));
        txtFechaMovimiento = new JTextField(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        estilizarControl(txtFechaMovimiento);
        pMov.add(txtFechaMovimiento);
        gridFechas.add(pMov);

        JPanel pVenc = new JPanel();
        pVenc.setOpaque(false);
        pVenc.setAlignmentX(Component.LEFT_ALIGNMENT);
        pVenc.setLayout(new BoxLayout(pVenc, BoxLayout.Y_AXIS));
        pVenc.add(crearEtiquetaCampo("Vencimiento del Lote:"));
        txtFechaVencimiento = new JTextField(LocalDate.now().plusMonths(12).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        estilizarControl(txtFechaVencimiento);
        pVenc.add(txtFechaVencimiento);
        gridFechas.add(pVenc);

        form.add(gridFechas);
        form.add(Box.createVerticalStrut(6));

        // Responsable
        form.add(crearEtiquetaCampo("Responsable de Farmacia / Almacén:"));
        txtResponsable = new JTextField("Dra. Elena Ruiz (Regente Farmacéutico)");
        estilizarControl(txtResponsable);
        form.add(txtResponsable);
        form.add(Box.createVerticalStrut(6));

        // Observaciones / Motivo
        form.add(crearEtiquetaCampo("Motivo / Referencia del Movimiento:"));
        txtObservacion = new JTextField("Ingreso conforme con Guía de Remisión.");
        estilizarControl(txtObservacion);
        form.add(txtObservacion);
        form.add(Box.createVerticalStrut(14));

        // Botones de acción
        JPanel pBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        pBotones.setOpaque(false);
        pBotones.setAlignmentX(Component.LEFT_ALIGNMENT);
        pBotones.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        JButton btnLimpiar = crearBoton("Limpiar", false, this::limpiarFormulario);
        JButton btnGuardar = crearBoton("Registrar Movimiento", true, this::guardarMovimiento);

        pBotones.add(btnLimpiar);
        pBotones.add(btnGuardar);
        form.add(pBotones);

        card.add(form, BorderLayout.CENTER);
        return card;
    }

    private JPanel crearPanelKardexTabla() {
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

        // Cabecera superior
        JPanel top = new JPanel(new BorderLayout(8, 0));
        top.setOpaque(false);

        JPanel izq = new JPanel();
        izq.setOpaque(false);
        izq.setLayout(new BoxLayout(izq, BoxLayout.Y_AXIS));

        JLabel lblTit = new JLabel("Historial de Movimientos de Stock (Kardex)");
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTit.setForeground(COLOR_TEXTO_TITULO);

        lblContadorMovimientos = new JLabel("0 transacciones registradas");
        lblContadorMovimientos.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblContadorMovimientos.setForeground(COLOR_TEXTO_MUTED);

        izq.add(lblTit);
        izq.add(Box.createVerticalStrut(1));
        izq.add(lblContadorMovimientos);
        top.add(izq, BorderLayout.WEST);

        // Filtro y buscador
        JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        der.setOpaque(false);

        txtBuscar = new JTextField(12);
        txtBuscar.setText("Buscar en kardex...");
        estilizarControl(txtBuscar);
        txtBuscar.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override public void focusGained(java.awt.event.FocusEvent e) { if ("Buscar en kardex...".equals(txtBuscar.getText())) txtBuscar.setText(""); }
            @Override public void focusLost(java.awt.event.FocusEvent e) { if (txtBuscar.getText().trim().isEmpty()) txtBuscar.setText("Buscar en kardex..."); }
        });
        txtBuscar.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override public void keyReleased(java.awt.event.KeyEvent e) { filtrarTabla(); }
        });
        der.add(txtBuscar);

        cbFiltroTipo = new JComboBox<>(new String[]{"Todos", "Ingresos", "Salidas"});
        estilizarControl(cbFiltroTipo);
        cbFiltroTipo.addActionListener(e -> filtrarTabla());
        der.add(cbFiltroTipo);

        top.add(der, BorderLayout.EAST);
        card.add(top, BorderLayout.NORTH);

        // Tabla
        String[] columnas = {"ID", "Producto", "Lote", "Tipo Operación", "Cant.", "Stock Res.", "Fecha", "Venc. Lote", "Responsable"};
        modeloMovimientos = new DefaultTableModel(columnas, 0) {
            private static final long serialVersionUID = 1L;
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tablaMovimientos = new JTable(modeloMovimientos);
        Ui.formatearTabla(tablaMovimientos, new int[]{0, 2, 3, 6, 7}, new int[]{4, 5});

        tablaMovimientos.getColumnModel().getColumn(0).setPreferredWidth(65);
        tablaMovimientos.getColumnModel().getColumn(1).setPreferredWidth(140);
        tablaMovimientos.getColumnModel().getColumn(2).setPreferredWidth(85);
        tablaMovimientos.getColumnModel().getColumn(3).setPreferredWidth(125);
        tablaMovimientos.getColumnModel().getColumn(4).setPreferredWidth(45);
        tablaMovimientos.getColumnModel().getColumn(5).setPreferredWidth(60);
        tablaMovimientos.getColumnModel().getColumn(6).setPreferredWidth(70);
        tablaMovimientos.getColumnModel().getColumn(7).setPreferredWidth(75);
        tablaMovimientos.getColumnModel().getColumn(8).setPreferredWidth(120);

        // Renderizador para Tipo Operación
        tablaMovimientos.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            private static final long serialVersionUID = 1L;
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String st = value != null ? value.toString() : "";
                if (st.toLowerCase().contains("ingreso") || st.contains("+")) {
                    l.setForeground(Ui.COLOR_EXITO);
                } else {
                    l.setForeground(Ui.TURQUESA_OSCURO);
                }
                return l;
            }
        });

        JScrollPane scrollTabla = new JScrollPane(tablaMovimientos);
        scrollTabla.setBorder(BorderFactory.createLineBorder(COLOR_BORDE, 1));
        scrollTabla.getViewport().setBackground(Color.WHITE);
        card.add(scrollTabla, BorderLayout.CENTER);

        // Barra inferior de acciones
        JPanel pAcciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        pAcciones.setOpaque(false);

        JButton btnStockActual = crearBoton("Consolidado de Stock", false, this::mostrarConsolidadoStock);
        btnStockActual.setIcon(Iconos.crearIconoCajaAlmacen(14, COLOR_AZUL_PRIMARIO));

        JButton btnKardexImprimir = crearBoton("Reporte de Kardex Físico", true, this::imprimirKardexFisico);
        btnKardexImprimir.setIcon(Iconos.crearIconoImprimir(14, Color.WHITE));

        pAcciones.add(btnStockActual);
        pAcciones.add(btnKardexImprimir);

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
        JButton btn = new JButton(texto) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (primario) {
                    g2.setColor(getModel().isRollover() ? Ui.TURQUESA_OSCURO : COLOR_AZUL_PRIMARIO);
                } else {
                    g2.setColor(getModel().isRollover() ? new Color(241, 245, 249) : Color.WHITE);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                if (!primario) {
                    g2.setColor(Ui.BORDE_SUAVE);
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 6, 6);
                }
                super.paintComponent(g2);
                g2.dispose();
            }

            @Override
            public Dimension getPreferredSize() {
                Dimension d = super.getPreferredSize();
                return new Dimension(d.width + 16, 28);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btn.setForeground(primario ? Color.WHITE : new Color(51, 65, 85));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(5, 12, 5, 12));
        btn.addActionListener(e -> accion.run());
        return btn;
    }

    public void recargarDatos() {
        if (cbProducto.getItemCount() == 0) {
            for (ProductoFarmacia p : repo.getProductosFarmacia()) {
                cbProducto.addItem(new ProductoItem(p));
            }
        }

        listaActual = repo.getMovimientosStock();
        filtrarTabla();

        // KPIs
        List<ProductoFarmacia> prods = repo.getProductosFarmacia();
        long porVencer = prods.stream().filter(p -> p.getFechaVencimiento() != null && ChronoUnit.DAYS.between(LocalDate.now(), p.getFechaVencimiento()) <= 60).count();
        long bajoStock = prods.stream().filter(p -> p.getStockActual() <= p.getStockMinimo()).count();

        lblKpiLotesTotales.setText(String.valueOf(prods.size()));
        lblKpiPorVencer.setText(String.valueOf(porVencer));
        lblKpiStockCritico.setText(String.valueOf(bajoStock));
        lblKpiMovimientosHoy.setText(String.valueOf(listaActual.size()));
    }

    private void filtrarTabla() {
        if (listaActual == null) return;
        modeloMovimientos.setRowCount(0);

        String texto = txtBuscar != null && !"Buscar en kardex...".equals(txtBuscar.getText()) ? txtBuscar.getText().trim().toLowerCase() : "";
        String tipoFiltro = cbFiltroTipo != null ? (String) cbFiltroTipo.getSelectedItem() : "Todos";

        int cont = 0;
        for (LoteMovimientoStock m : listaActual) {
            if ("Ingresos".equalsIgnoreCase(tipoFiltro)) {
                if (!m.getTipoMovimiento().toLowerCase().contains("ingreso") && !m.getTipoMovimiento().contains("+")) continue;
            } else if ("Salidas".equalsIgnoreCase(tipoFiltro)) {
                if (!m.getTipoMovimiento().toLowerCase().contains("salida") && !m.getTipoMovimiento().contains("-")) continue;
            }

            if (!texto.isEmpty()) {
                boolean match = m.getIdMovimiento().toLowerCase().contains(texto) ||
                        m.getNombreProducto().toLowerCase().contains(texto) ||
                        m.getNumeroLote().toLowerCase().contains(texto) ||
                        m.getResponsable().toLowerCase().contains(texto);
                if (!match) continue;
            }

            cont++;
            modeloMovimientos.addRow(new Object[]{
                    m.getIdMovimiento(),
                    m.getNombreProducto(),
                    m.getNumeroLote(),
                    m.getTipoMovimiento(),
                    m.getCantidad(),
                    m.getStockPosterior(),
                    m.getFechaMovimientoFormateada(),
                    m.getFechaVencimientoLoteFormateada(),
                    m.getResponsable()
            });
        }
        lblContadorMovimientos.setText(cont + " movimientos visualizados");
    }

    private void guardarMovimiento() {
        ProductoItem item = (ProductoItem) cbProducto.getSelectedItem();
        if (item == null || item.producto == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto para el movimiento.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int cant = 1;
        try {
            cant = Integer.parseInt(txtCantidad.getText().trim());
            if (cant <= 0) cant = 1;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Ingrese una cantidad válida mayor a 0.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        LocalDate fechaVenc = LocalDate.now().plusMonths(12);
        try {
            fechaVenc = LocalDate.parse(txtFechaVencimiento.getText().trim(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } catch (Exception ignored) {}

        String tipo = (String) cbTipoMovimiento.getSelectedItem();
        int stockPrev = item.producto.getStockActual();
        int stockPost = tipo.toLowerCase().contains("ingreso") || tipo.contains("+") ? (stockPrev + cant) : (stockPrev - cant);
        if (stockPost < 0) {
            JOptionPane.showMessageDialog(this, "Stock insuficiente en almacén. Stock actual: " + stockPrev + " unidades.", "Stock Insuficiente", JOptionPane.WARNING_MESSAGE);
            return;
        }

        LoteMovimientoStock mov = new LoteMovimientoStock(
                null,
                item.producto.getCodigo(),
                item.producto.getNombre(),
                txtLote.getText().trim(),
                tipo,
                cant,
                stockPrev,
                stockPost,
                LocalDate.now(),
                fechaVenc,
                txtResponsable.getText().trim(),
                txtObservacion.getText().trim()
        );

        repo.registrarMovimientoStock(mov);
        recargarDatos();
        limpiarFormulario();

        JOptionPane.showMessageDialog(this,
                "Movimiento registrado en Kardex con éxito.\n\n" +
                        "Producto: " + item.producto.getNombre() + "\n" +
                        "Tipo: " + tipo + "\n" +
                        "Cantidad: " + cant + " un.\n" +
                        "Stock previo: " + stockPrev + " -> Nuevo stock: " + stockPost + " un.",
                "Kardex Actualizado", JOptionPane.INFORMATION_MESSAGE);
    }

    private void mostrarConsolidadoStock() {
        StringBuilder sb = new StringBuilder();
        sb.append("CONSOLIDADO DE STOCK DE FARMACIA VETERINARIA\n");
        sb.append("CLÍNICA HAPPY PETS · INVENTARIO AL DÍA\n");
        sb.append("=========================================================================\n");
        sb.append(String.format("%-10s %-32s %-12s %-10s %-10s %-12s\n", "CÓDIGO", "PRODUCTO", "CATEGORÍA", "STOCK", "MÍNIMO", "ESTADO"));
        sb.append("-------------------------------------------------------------------------\n");

        for (ProductoFarmacia p : repo.getProductosFarmacia()) {
            sb.append(String.format("%-10s %-32s %-12s %-10s %-10s %-12s\n",
                    p.getCodigo(),
                    p.getNombre().length() > 30 ? p.getNombre().substring(0, 30) : p.getNombre(),
                    p.getCategoria().length() > 10 ? p.getCategoria().substring(0, 10) : p.getCategoria(),
                    p.getStockActual() + " un.",
                    p.getStockMinimo() + " un.",
                    p.getEstado()
            ));
        }

        Ui.mostrarVisorReporte(
                javax.swing.SwingUtilities.getWindowAncestor(this),
                "Consolidado de Stock de Farmacia",
                "CLÍNICA HAPPY PETS · INVENTARIO Y SALDOS AL DÍA",
                sb.toString(),
                "Consolidado_Stock"
        );
    }

    private void imprimirKardexFisico() {
        StringBuilder sb = new StringBuilder();
        sb.append("HOJA DE CONTROL Y AUDITORÍA DE KARDEX FÍSICO\n");
        sb.append("HAPPY PETS VETERINARIA · SERVICIO FARMACÉUTICO\n");
        sb.append("FECHA DE EMISIÓN: ").append(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).append("\n");
        sb.append("=========================================================================\n");
        sb.append(String.format("%-12s %-10s %-25s %-22s %-6s %-10s\n", "ID OPERACIÓN", "FECHA", "PRODUCTO", "OPERACIÓN", "CANT.", "STOCK RES."));
        sb.append("-------------------------------------------------------------------------\n");

        for (LoteMovimientoStock m : repo.getMovimientosStock()) {
            sb.append(String.format("%-12s %-10s %-25s %-22s %-6s %-10s\n",
                    m.getIdMovimiento(),
                    m.getFechaMovimientoFormateada(),
                    m.getNombreProducto().length() > 23 ? m.getNombreProducto().substring(0, 23) : m.getNombreProducto(),
                    m.getTipoMovimiento().length() > 20 ? m.getTipoMovimiento().substring(0, 20) : m.getTipoMovimiento(),
                    m.getCantidad(),
                    m.getStockPosterior()
            ));
        }

        sb.append("=========================================================================\n\n");
        sb.append("_________________________                 _________________________\n");
        sb.append(" Dra. Elena Ruiz (Regente)                  Director Médico Happy Pets\n");
        sb.append(" CQVP Matrícula Nº 4120                     Colegio Médico Veterinario\n");

        Ui.mostrarVisorReporte(
                javax.swing.SwingUtilities.getWindowAncestor(this),
                "Kardex Físico y Movimientos de Lotes",
                "HAPPY PETS VETERINARIA · SERVICIO FARMACÉUTICO",
                sb.toString(),
                "Kardex_Lotes"
        );
    }

    private void limpiarFormulario() {
        txtLote.setText("LOT-2024-" + String.format("%02d", repo.getMovimientosStock().size() + 1));
        txtCantidad.setText("10");
        txtObservacion.setText("Operación conforme.");
    }

    private static class ProductoItem {
        final ProductoFarmacia producto;
        ProductoItem(ProductoFarmacia p) { this.producto = p; }
        @Override public String toString() {
            if (producto == null) return "Seleccione...";
            return producto.getCodigo() + " - " + producto.getNombre() + " (Stock: " + producto.getStockActual() + ")";
        }
    }
}
