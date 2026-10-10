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
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import happypets.data.RepositorioVeterinaria;
import happypets.model.AjusteMerma;
import happypets.model.ProductoFarmacia;
import happypets.ui.Iconos;
import happypets.ui.Ui;

/**
 * Submódulo 5.4: Ajustes y Mermas.
 * Basado en el wireframe oficial (Pág. 2) y normativas sanitarias veterinarias:
 * - Registro de bajas por caducidad de lotes, roturas accidentales y fallas de cadena de frío.
 * - Descuento automático de stock en inventario y Kardex.
 * - Valorización financiera de pérdidas por mermas en farmacia.
 * - Generación de Acta Oficial de Destrucción Sanitaria y Baja de Fármacos.
 */
public class VistaAjustesMermasPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private static final Color COLOR_BORDE = Ui.BORDE_SUAVE;
    private static final Color COLOR_AZUL_PRIMARIO = Ui.TURQUESA;
    private static final Color COLOR_TEXTO_TITULO = new Color(30, 41, 59);
    private static final Color COLOR_TEXTO_MUTED = new Color(100, 116, 139);

    private final RepositorioVeterinaria repo = RepositorioVeterinaria.getInstancia();

    // KPIs
    private JLabel lblKpiTotalAjustes;
    private JLabel lblKpiPerdidaTotal;
    private JLabel lblKpiLotesCaducados;
    private JLabel lblKpiRegularizaciones;

    // Formulario de Ajuste
    private JComboBox<ProductoItem> cbProducto;
    private JTextField txtLote;
    private JComboBox<String> cbTipo;
    private JTextField txtCantidad;
    private JTextField txtCostoUnitario;
    private JTextField txtPerdidaCalculada;
    private JTextArea txtMotivo;
    private JTextField txtAutorizadoPor;

    // Tabla de Ajustes
    private JComboBox<String> cbFiltroTipo;
    private JTable tablaAjustes;
    private DefaultTableModel modeloAjustes;
    private JLabel lblContadorAjustes;
    private List<AjusteMerma> listaActual;

    public VistaAjustesMermasPanel() {
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

        // 3. Doble Columna: Formulario y Tabla de Historial
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

        JLabel titulo = new JLabel("Ajustes de Inventario y Control de Mermas");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 17));
        titulo.setForeground(COLOR_TEXTO_TITULO);

        JLabel subtitulo = new JLabel("Módulo 5.4 · Registro de bajas por caducidad, roturas, descarte biológico y regularización de inventario físico");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitulo.setForeground(COLOR_TEXTO_MUTED);

        izq.add(titulo);
        izq.add(Box.createVerticalStrut(2));
        izq.add(subtitulo);
        cab.add(izq, BorderLayout.CENTER);

        JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        der.setOpaque(false);

        JLabel badgeAuditoria = new JLabel(" Protocolo de Descarte Sanitario & Farmacovigilancia ", SwingConstants.CENTER);
        badgeAuditoria.setFont(new Font("Segoe UI", Font.BOLD, 11));
        badgeAuditoria.setForeground(Ui.TURQUESA_PROFUNDO);
        badgeAuditoria.setOpaque(true);
        badgeAuditoria.setBackground(Ui.TURQUESA_SUAVE);
        badgeAuditoria.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Ui.TURQUESA_MEDIO, 1, true),
                new EmptyBorder(4, 10, 4, 10)
        ));
        der.add(badgeAuditoria);

        cab.add(der, BorderLayout.EAST);
        return cab;
    }

    private JPanel crearFilaKpis() {
        JPanel kpis = new JPanel(new GridLayout(1, 4, 10, 0));
        kpis.setOpaque(false);

        lblKpiTotalAjustes = new JLabel("0", SwingConstants.LEFT);
        lblKpiPerdidaTotal = new JLabel("S/. 0", SwingConstants.LEFT);
        lblKpiLotesCaducados = new JLabel("0", SwingConstants.LEFT);
        lblKpiRegularizaciones = new JLabel("0", SwingConstants.LEFT);

        kpis.add(crearTarjetaKpi("Ajustes Registrados", lblKpiTotalAjustes, "Transacciones de ajuste", new Color(14, 165, 233), Iconos.crearIconoAlertaMerma(18, new Color(14, 165, 233))));
        kpis.add(crearTarjetaKpi("Pérdida Valorizada (Mes)", lblKpiPerdidaTotal, "Impacto económico neto", new Color(220, 38, 38), Iconos.crearIconoFactura(16, new Color(220, 38, 38))));
        kpis.add(crearTarjetaKpi("Lotes Caducados", lblKpiLotesCaducados, "Bajas por vencimiento", new Color(245, 158, 11), Iconos.crearIconoReloj(16, new Color(245, 158, 11))));
        kpis.add(crearTarjetaKpi("Conteo Físico / Regulariz.", lblKpiRegularizaciones, "Diferencias de stock", new Color(16, 185, 129), Iconos.crearIconoCheck(16, new Color(16, 185, 129))));

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

        // Columna Izquierda: Formulario (420px)
        JPanel colIzquierda = crearPanelFormularioAjuste();
        colIzquierda.setPreferredSize(new Dimension(420, 560));
        fila.add(colIzquierda, BorderLayout.WEST);

        // Columna Derecha: Historial de Ajustes
        JPanel colDerecha = crearPanelHistorialTabla();
        fila.add(colDerecha, BorderLayout.CENTER);

        return fila;
    }

    private JPanel crearPanelFormularioAjuste() {
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
        JLabel lblTitulo = new JLabel("Registro de Ajuste / Merma");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTitulo.setForeground(COLOR_TEXTO_TITULO);
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(lblTitulo);

        JLabel lblSub = new JLabel("Baja formal de medicamentos con afectación de stock y cálculo de pérdida");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblSub.setForeground(COLOR_TEXTO_MUTED);
        lblSub.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(lblSub);
        form.add(Box.createVerticalStrut(10));

        // Producto
        form.add(crearEtiquetaCampo("Producto a Ajustar:"));
        cbProducto = new JComboBox<>();
        estilizarControl(cbProducto);
        cbProducto.addActionListener(e -> autocompletarDatosProducto());
        form.add(cbProducto);
        form.add(Box.createVerticalStrut(6));

        // Lote y Tipo en Grid
        JPanel gridLoteTipo = new JPanel(new GridLayout(1, 2, 8, 0));
        gridLoteTipo.setOpaque(false);
        gridLoteTipo.setAlignmentX(Component.LEFT_ALIGNMENT);
        gridLoteTipo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));

        JPanel pLote = new JPanel();
        pLote.setOpaque(false);
        pLote.setAlignmentX(Component.LEFT_ALIGNMENT);
        pLote.setLayout(new BoxLayout(pLote, BoxLayout.Y_AXIS));
        pLote.add(crearEtiquetaCampo("Número de Lote:"));
        txtLote = new JTextField("LOT-2024-88A");
        estilizarControl(txtLote);
        pLote.add(txtLote);
        gridLoteTipo.add(pLote);

        JPanel pTipo = new JPanel();
        pTipo.setOpaque(false);
        pTipo.setAlignmentX(Component.LEFT_ALIGNMENT);
        pTipo.setLayout(new BoxLayout(pTipo, BoxLayout.Y_AXIS));
        pTipo.add(crearEtiquetaCampo("Tipo de Ajuste:"));
        cbTipo = new JComboBox<>(new String[]{
                "Merma por Rotura / Deterioro",
                "Vencimiento de Lote",
                "Falla Cadena de Frío",
                "Ajuste Físico Negativo (- Faltante)",
                "Ajuste Físico Positivo (+ Sobrante)"
        });
        estilizarControl(cbTipo);
        pTipo.add(cbTipo);
        gridLoteTipo.add(pTipo);

        form.add(gridLoteTipo);
        form.add(Box.createVerticalStrut(6));

        // Cantidad, Costo Unitario y Pérdida
        JPanel gridValores = new JPanel(new GridLayout(1, 3, 6, 0));
        gridValores.setOpaque(false);
        gridValores.setAlignmentX(Component.LEFT_ALIGNMENT);
        gridValores.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));

        JPanel pCant = new JPanel();
        pCant.setOpaque(false);
        pCant.setAlignmentX(Component.LEFT_ALIGNMENT);
        pCant.setLayout(new BoxLayout(pCant, BoxLayout.Y_AXIS));
        pCant.add(crearEtiquetaCampo("Cantidad:"));
        txtCantidad = new JTextField("2");
        estilizarControl(txtCantidad);
        txtCantidad.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override public void keyReleased(java.awt.event.KeyEvent e) { recalcularPerdida(); }
        });
        pCant.add(txtCantidad);
        gridValores.add(pCant);

        JPanel pCosto = new JPanel();
        pCosto.setOpaque(false);
        pCosto.setAlignmentX(Component.LEFT_ALIGNMENT);
        pCosto.setLayout(new BoxLayout(pCosto, BoxLayout.Y_AXIS));
        pCosto.add(crearEtiquetaCampo("Costo Unit. S/.:"));
        txtCostoUnitario = new JTextField("25.00");
        estilizarControl(txtCostoUnitario);
        txtCostoUnitario.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override public void keyReleased(java.awt.event.KeyEvent e) { recalcularPerdida(); }
        });
        pCosto.add(txtCostoUnitario);
        gridValores.add(pCosto);

        JPanel pPerd = new JPanel();
        pPerd.setOpaque(false);
        pPerd.setAlignmentX(Component.LEFT_ALIGNMENT);
        pPerd.setLayout(new BoxLayout(pPerd, BoxLayout.Y_AXIS));
        pPerd.add(crearEtiquetaCampo("Pérdida S/.:"));
        txtPerdidaCalculada = new JTextField("50.00");
        estilizarControl(txtPerdidaCalculada);
        txtPerdidaCalculada.setEditable(false);
        pPerd.add(txtPerdidaCalculada);
        gridValores.add(pPerd);

        form.add(gridValores);
        form.add(Box.createVerticalStrut(6));

        // Motivo Detallado
        form.add(crearEtiquetaCampo("Motivo Detallado de la Merma:"));
        txtMotivo = new JTextArea(3, 20);
        txtMotivo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        txtMotivo.setLineWrap(true);
        txtMotivo.setWrapStyleWord(true);
        txtMotivo.setText("Lote caducado retirado de estantes de farmacia para descarte seguro.");
        JScrollPane spMotivo = new JScrollPane(txtMotivo);
        spMotivo.setBorder(BorderFactory.createLineBorder(COLOR_BORDE, 1));
        spMotivo.setPreferredSize(new Dimension(spMotivo.getPreferredSize().width, 60));
        spMotivo.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(spMotivo);
        form.add(Box.createVerticalStrut(6));

        // Autorizado Por
        form.add(crearEtiquetaCampo("Autorizado por (Director Médico / Regente):"));
        txtAutorizadoPor = new JTextField("Dr. Carlos Vargas (Director Médico)");
        estilizarControl(txtAutorizadoPor);
        form.add(txtAutorizadoPor);
        form.add(Box.createVerticalStrut(14));

        // Botones de acción
        JPanel pBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        pBotones.setOpaque(false);
        pBotones.setAlignmentX(Component.LEFT_ALIGNMENT);
        pBotones.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        JButton btnLimpiar = crearBoton("Limpiar", false, this::limpiarFormulario);
        JButton btnGuardar = crearBoton("Registrar Ajuste / Baja", true, this::guardarAjuste);

        pBotones.add(btnLimpiar);
        pBotones.add(btnGuardar);
        form.add(pBotones);

        card.add(form, BorderLayout.CENTER);
        return card;
    }

    private JPanel crearPanelHistorialTabla() {
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

        JLabel lblTit = new JLabel("Historial de Bajas, Mermas y Descarte Sanitario");
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTit.setForeground(COLOR_TEXTO_TITULO);

        lblContadorAjustes = new JLabel("0 registros de ajuste");
        lblContadorAjustes.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblContadorAjustes.setForeground(COLOR_TEXTO_MUTED);

        izq.add(lblTit);
        izq.add(Box.createVerticalStrut(1));
        izq.add(lblContadorAjustes);
        top.add(izq, BorderLayout.WEST);

        // Filtro por tipo
        JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        der.setOpaque(false);

        JLabel lblFiltro = new JLabel("Tipo:");
        lblFiltro.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblFiltro.setForeground(COLOR_TEXTO_MUTED);
        der.add(lblFiltro);

        cbFiltroTipo = new JComboBox<>(new String[]{"Todos", "Vencimiento", "Rotura", "Ajuste Físico"});
        estilizarControl(cbFiltroTipo);
        cbFiltroTipo.addActionListener(e -> filtrarTabla());
        der.add(cbFiltroTipo);

        top.add(der, BorderLayout.EAST);
        card.add(top, BorderLayout.NORTH);

        // Tabla
        String[] columnas = {"ID", "Fecha", "Producto", "Lote", "Tipo Merma", "Cant.", "Pérdida S/.", "Autorizado Por", "Estado"};
        modeloAjustes = new DefaultTableModel(columnas, 0) {
            private static final long serialVersionUID = 1L;
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tablaAjustes = new JTable(modeloAjustes);
        Ui.formatearTabla(tablaAjustes, new int[]{0, 1, 3, 4, 8}, new int[]{5, 6});

        tablaAjustes.getColumnModel().getColumn(0).setPreferredWidth(65);
        tablaAjustes.getColumnModel().getColumn(1).setPreferredWidth(70);
        tablaAjustes.getColumnModel().getColumn(2).setPreferredWidth(140);
        tablaAjustes.getColumnModel().getColumn(3).setPreferredWidth(85);
        tablaAjustes.getColumnModel().getColumn(4).setPreferredWidth(140);
        tablaAjustes.getColumnModel().getColumn(5).setPreferredWidth(45);
        tablaAjustes.getColumnModel().getColumn(6).setPreferredWidth(75);
        tablaAjustes.getColumnModel().getColumn(7).setPreferredWidth(120);
        tablaAjustes.getColumnModel().getColumn(8).setPreferredWidth(110);

        // Renderizador de Estado
        tablaAjustes.getColumnModel().getColumn(8).setCellRenderer(new DefaultTableCellRenderer() {
            private static final long serialVersionUID = 1L;
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                l.setHorizontalAlignment(SwingConstants.CENTER);
                l.setForeground(Ui.COLOR_EXITO);
                l.setBackground(new Color(209, 250, 229));
                l.setOpaque(true);
                return l;
            }
        });

        JScrollPane scroll = new JScrollPane(tablaAjustes);
        scroll.setBorder(BorderFactory.createLineBorder(COLOR_BORDE, 1));
        scroll.getViewport().setBackground(Color.WHITE);
        card.add(scroll, BorderLayout.CENTER);

        // Barra inferior de acciones
        JPanel pAcciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        pAcciones.setOpaque(false);

        JButton btnActaBaja = crearBoton("Acta de Destrucción Sanitaria", true, this::imprimirActaDestruccion);
        btnActaBaja.setIcon(Iconos.crearIconoDocumento(14, Color.WHITE));
        pAcciones.add(btnActaBaja);

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
            autocompletarDatosProducto();
        }

        listaActual = repo.getAjustesMermas();
        filtrarTabla();

        // KPIs
        double perdidaTotal = listaActual.stream().filter(a -> !a.getTipo().contains("Positivo")).mapToDouble(AjusteMerma::getPerdidaValorizada).sum();
        long caducados = listaActual.stream().filter(a -> a.getTipo().contains("Vencimiento")).count();
        long regulariz = listaActual.stream().filter(a -> a.getTipo().contains("Físico")).count();

        lblKpiTotalAjustes.setText(String.valueOf(listaActual.size()));
        lblKpiPerdidaTotal.setText("S/. " + String.format("%.2f", perdidaTotal));
        lblKpiLotesCaducados.setText(String.valueOf(caducados));
        lblKpiRegularizaciones.setText(String.valueOf(regulariz));
    }

    private void autocompletarDatosProducto() {
        ProductoItem item = (ProductoItem) cbProducto.getSelectedItem();
        if (item != null && item.producto != null) {
            txtLote.setText(item.producto.getLoteActual());
            txtCostoUnitario.setText(String.format("%.2f", item.producto.getPrecioCosto()));
            recalcularPerdida();
        }
    }

    private void recalcularPerdida() {
        try {
            int c = Integer.parseInt(txtCantidad.getText().trim());
            double u = Double.parseDouble(txtCostoUnitario.getText().trim());
            txtPerdidaCalculada.setText(String.format("%.2f", c * u));
        } catch (Exception ignored) {}
    }

    private void filtrarTabla() {
        if (listaActual == null) return;
        modeloAjustes.setRowCount(0);
        String filtro = cbFiltroTipo != null ? (String) cbFiltroTipo.getSelectedItem() : "Todos";

        int cont = 0;
        for (AjusteMerma a : listaActual) {
            if (!"Todos".equalsIgnoreCase(filtro)) {
                if (!a.getTipo().toLowerCase().contains(filtro.toLowerCase())) continue;
            }

            cont++;
            modeloAjustes.addRow(new Object[]{
                    a.getIdAjuste(),
                    a.getFechaFormateada(),
                    a.getNombreProducto(),
                    a.getNumeroLote(),
                    a.getTipo(),
                    a.getCantidad(),
                    "S/. " + String.format("%.2f", a.getPerdidaValorizada()),
                    a.getAutorizadoPor(),
                    a.getEstado()
            });
        }
        lblContadorAjustes.setText(cont + " ajustes mostrados");
    }

    private void guardarAjuste() {
        ProductoItem pi = (ProductoItem) cbProducto.getSelectedItem();
        if (pi == null || pi.producto == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto para registrar el ajuste.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int cant = 1;
        double costo = 20.0;
        try { cant = Integer.parseInt(txtCantidad.getText().trim()); } catch (Exception ignored) {}
        try { costo = Double.parseDouble(txtCostoUnitario.getText().trim()); } catch (Exception ignored) {}

        String tipo = (String) cbTipo.getSelectedItem();
        if (!tipo.contains("Positivo") && pi.producto.getStockActual() < cant) {
            JOptionPane.showMessageDialog(this, "La cantidad a dar de baja (" + cant + ") supera el stock actual disponible (" + pi.producto.getStockActual() + ").", "Stock Insuficiente", JOptionPane.WARNING_MESSAGE);
            return;
        }

        AjusteMerma a = new AjusteMerma(
                null,
                LocalDate.now(),
                pi.producto.getCodigo(),
                pi.producto.getNombre(),
                txtLote.getText().trim(),
                tipo,
                cant,
                costo,
                cant * costo,
                txtMotivo.getText().trim(),
                txtAutorizadoPor.getText().trim(),
                "Aprobado y Descargado"
        );

        repo.guardarAjusteMerma(a);
        recargarDatos();
        limpiarFormulario();

        JOptionPane.showMessageDialog(this,
                "Ajuste de inventario registrado con éxito.\n\n" +
                        "Producto: " + a.getNombreProducto() + "\n" +
                        "Tipo: " + a.getTipo() + " (" + a.getCantidad() + " un.)\n" +
                        "Impacto Económico: S/. " + String.format("%.2f", a.getPerdidaValorizada()) + "\n" +
                        "Stock de almacén actualizado automáticamente.",
                "Ajuste Procesado", JOptionPane.INFORMATION_MESSAGE);
    }

    private void imprimirActaDestruccion() {
        int fila = tablaAjustes.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un registro de ajuste para emitir el Acta de Destrucción.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String idAjuste = (String) modeloAjustes.getValueAt(fila, 0);
        Optional<AjusteMerma> opt = repo.getAjustesMermas().stream().filter(a -> a.getIdAjuste().equalsIgnoreCase(idAjuste)).findFirst();
        if (!opt.isPresent()) return;

        AjusteMerma a = opt.get();
        String acta = "ACTA OFICIAL DE DESTRUCCIÓN SANITARIA Y BAJA DE FÁRMACOS VETERINARIOS\n" +
                "CLÍNICA VETERINARIA HAPPY PETS S.A.C. · DIRECCIÓN TÉCNICA\n" +
                "=========================================================================\n\n" +
                "EXPEDIENTE DE MERMA: " + a.getIdAjuste() + "\n" +
                "FECHA DE ACTA      : " + a.getFechaFormateada() + "\n" +
                "AUTORIZADO POR     : " + a.getAutorizadoPor() + "\n\n" +
                "DATOS DEL PRODUCTO DADO DE BAJA:\n" +
                "  • Código Producto: " + a.getCodigoProducto() + "\n" +
                "  • Nombre Fármaco : " + a.getNombreProducto() + "\n" +
                "  • Lote Afectado  : " + a.getNumeroLote() + "\n" +
                "  • Causal de Baja : " + a.getTipo() + "\n" +
                "  • Cantidad Dada  : " + a.getCantidad() + " unidades físicas\n" +
                "  • Costo Unitario : S/. " + String.format("%.2f", a.getCostoUnitario()) + "\n" +
                "  • PÉRDIDA TOTAL  : S/. " + String.format("%.2f", a.getPerdidaValorizada()) + "\n\n" +
                "DESCRIPCIÓN DEL MOTIVO TÉCNICO:\n" +
                a.getMotivoDetallado() + "\n\n" +
                "PROCEDIMIENTO DE DISPOSICIÓN FINAL:\n" +
                "Se procede a la inactivación física y entrega a empresa operadora de residuos biocontaminados,\n" +
                "en estricto cumplimiento de las normas de farmacovigilancia y sanidad animal.\n\n" +
                "__________________________             __________________________\n" +
                "    Regente Farmacéutico                    Director Médico Veterinario\n" +
                "  CQVP Matrícula Nº 4120                     Colegio Médico Veterinario\n";

        Ui.mostrarVisorReporte(
                javax.swing.SwingUtilities.getWindowAncestor(this),
                "Acta de Destrucción Sanitaria - " + a.getIdAjuste(),
                "BAJA DE FÁRMACOS Y MERMAS · " + a.getNombreProducto(),
                acta,
                "Acta_Destruccion_" + a.getIdAjuste()
        );
    }

    private void limpiarFormulario() {
        txtCantidad.setText("1");
        txtMotivo.setText("Baja regularizada por auditoría.");
    }

    private static class ProductoItem {
        final ProductoFarmacia producto;
        ProductoItem(ProductoFarmacia p) { this.producto = p; }
        @Override public String toString() {
            if (producto == null) return "Seleccione...";
            return producto.getCodigo() + " - " + producto.getNombre();
        }
    }
}
