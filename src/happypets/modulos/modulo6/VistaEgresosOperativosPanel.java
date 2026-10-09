package happypets.modulos.modulo6;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import happypets.data.RepositorioVeterinaria;
import happypets.model.EgresoOperativo;
import happypets.ui.Iconos;
import happypets.ui.Ui;

/**
 * Submódulo 6.4: Control de Egresos Operativos.
 * Basado estrictamente en el wireframe oficial CONTROL DE EGRESOS OPERATIVOS.pdf y mejorado:
 * - Formulario del wireframe: Fecha, Categoría, Descripción, Monto, Proveedor, Método de pago.
 * - Botones del wireframe: Agregar, Modificar, Eliminar, Limpiar.
 * - Tabla del wireframe: Fecha, Categoría, Descripción, Proveedor, Monto.
 * - Footer del wireframe: N.° de egresos: X | Total egresos: S/ 0.00.
 * - Mejoras: KPIs de control presupuestal, mayor categoría de gasto, filtros y voucher de egreso.
 */
public class VistaEgresosOperativosPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private static final Color COLOR_BORDE = new Color(226, 232, 240);
    private static final Color COLOR_PRIMARIO = new Color(249, 115, 22);
    private static final Color COLOR_ROJO = new Color(239, 68, 68);
    private static final Color COLOR_AZUL = new Color(14, 165, 233);
    private static final Color COLOR_VERDE = new Color(16, 185, 129);
    private static final Color COLOR_TEXTO_TITULO = new Color(30, 41, 59);
    private static final Color COLOR_TEXTO_MUTED = new Color(100, 116, 139);

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final RepositorioVeterinaria repo = RepositorioVeterinaria.getInstancia();

    // KPIs Superiores
    private JLabel lblKpiTotalEgresos;
    private JLabel lblKpiCantEgresos;
    private JLabel lblKpiMayorCategoria;
    private JLabel lblKpiPromedioEgreso;

    // Componentes del Formulario del Wireframe
    private JTextField txtFecha;            // txtFecha
    private JComboBox<String> cboCategoria; // cboCategoria
    private JTextField txtDescripcion;      // txtDescripcion
    private JTextField txtMonto;            // txtMonto
    private JTextField txtProveedor;        // txtProveedor
    private JComboBox<String> cboMetodoPago;// cboMetodoPago

    // Tabla
    private JTable tablaEgresos;
    private DefaultTableModel modeloEgresos;
    private String idSeleccionado = null;

    // Footer del Wireframe
    private JLabel lblNumEgresos;           // N.° de egresos: 0
    private JLabel lblTotalEgresos;         // Total egresos: S/ 0.00

    public VistaEgresosOperativosPanel() {
        setLayout(new BorderLayout());
        setOpaque(false);

        JPanel contenido = new JPanel();
        contenido.setOpaque(false);
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setBorder(new EmptyBorder(12, 18, 14, 18));

        // 1. Cabecera institucional
        contenido.add(crearCabeceraVista());
        contenido.add(Box.createVerticalStrut(10));

        // 2. Fila de KPIs presupuestales
        contenido.add(crearFilaKpis());
        contenido.add(Box.createVerticalStrut(12));

        // 3. Formulario oficial del Wireframe
        contenido.add(crearFormularioEgresos());
        contenido.add(Box.createVerticalStrut(12));

        // 4. Tabla y Footer
        contenido.add(crearSeccionTablaYFooter());

        add(contenido, BorderLayout.CENTER);

        recargarDatos();
    }

    private JPanel crearCabeceraVista() {
        JPanel cab = new JPanel(new BorderLayout());
        cab.setOpaque(false);

        JPanel izq = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        izq.setOpaque(false);

        JLabel ico = new JLabel(Iconos.crearIconoEgresos(22, COLOR_ROJO));
        izq.add(ico);

        JPanel titulos = new JPanel();
        titulos.setOpaque(false);
        titulos.setLayout(new BoxLayout(titulos, BoxLayout.Y_AXIS));

        JLabel lblTit = new JLabel("Submódulo 6.4: Gestión de Egresos Operativos");
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblTit.setForeground(COLOR_TEXTO_TITULO);

        JLabel lblSub = new JLabel("Registro y clasificación de gastos fijos, suministros, servicios y obligaciones operacionales");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(COLOR_TEXTO_MUTED);

        titulos.add(lblTit);
        titulos.add(lblSub);
        izq.add(titulos);
        cab.add(izq, BorderLayout.WEST);

        // Botón derecho: Voucher Contable
        JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        der.setOpaque(false);

        JButton btnVoucher = Ui.boton("Emitir Voucher Contable", true);
        btnVoucher.setBackground(COLOR_PRIMARIO);
        btnVoucher.setIcon(Iconos.crearIconoImprimir(14, Color.WHITE));
        btnVoucher.addActionListener(e -> generarVoucherEgreso());

        der.add(btnVoucher);
        cab.add(der, BorderLayout.EAST);

        return cab;
    }

    private JPanel crearFilaKpis() {
        JPanel fila = new JPanel(new GridLayout(1, 4, 12, 0));
        fila.setOpaque(false);
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 74));

        lblKpiTotalEgresos = new JLabel("S/ 0.00");
        lblKpiCantEgresos = new JLabel("0 registros");
        lblKpiMayorCategoria = new JLabel("-");
        lblKpiPromedioEgreso = new JLabel("S/ 0.00");

        fila.add(crearCardKpi("Gasto Total del Periodo", lblKpiTotalEgresos, COLOR_ROJO, "Suma global de egresos"));
        fila.add(crearCardKpi("Egresos Contabilizados", lblKpiCantEgresos, COLOR_AZUL, "Cantidad de operaciones"));
        fila.add(crearCardKpi("Categoría Principal", lblKpiMayorCategoria, COLOR_PRIMARIO, "Mayor peso presupuestal"));
        fila.add(crearCardKpi("Ticket Promedio por Gasto", lblKpiPromedioEgreso, COLOR_VERDE, "Distribución unitaria"));

        return fila;
    }

    private JPanel crearCardKpi(String titulo, JLabel lblValor, Color colorAcento, String subtitulo) {
        JPanel card = new JPanel(new BorderLayout(4, 4)) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(COLOR_BORDE);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.setColor(colorAcento);
                g2.fillRect(0, 0, 4, getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(8, 12, 8, 12));

        JLabel lblT = new JLabel(titulo.toUpperCase());
        lblT.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblT.setForeground(COLOR_TEXTO_MUTED);

        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblValor.setForeground(colorAcento);

        JLabel lblS = new JLabel(subtitulo);
        lblS.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblS.setForeground(COLOR_TEXTO_MUTED);

        card.add(lblT, BorderLayout.NORTH);
        card.add(lblValor, BorderLayout.CENTER);
        card.add(lblS, BorderLayout.SOUTH);

        return card;
    }

    /**
     * Formulario exacto del wireframe CONTROL DE EGRESOS OPERATIVOS.pdf:
     * Fecha: txtFecha           Categoría: cboCategoria
     * Descripción: txtDescripcion Monto: txtMonto
     * Proveedor: txtProveedor   Método de pago: cboMetodoPago
     * [Agregar] [Modificar] [Eliminar] [Limpiar]
     */
    private JPanel crearFormularioEgresos() {
        JPanel boxForm = new JPanel(new BorderLayout(0, 10));
        boxForm.setBackground(Color.WHITE);
        boxForm.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDE, 1),
                new EmptyBorder(12, 18, 14, 18)
        ));

        JPanel gridCampos = new JPanel(new GridLayout(3, 2, 22, 8));
        gridCampos.setOpaque(false);

        // Fila 1: Fecha y Categoría
        JPanel fFec = new JPanel(new BorderLayout(6, 0));
        fFec.setOpaque(false);
        JLabel lFec = new JLabel("Fecha:");
        lFec.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lFec.setPreferredSize(new Dimension(105, 24));
        txtFecha = new JTextField(LocalDate.now().format(FORMATO_FECHA));
        fFec.add(lFec, BorderLayout.WEST);
        fFec.add(txtFecha, BorderLayout.CENTER);

        JPanel fCat = new JPanel(new BorderLayout(6, 0));
        fCat.setOpaque(false);
        JLabel lCat = new JLabel("Categoría:");
        lCat.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lCat.setPreferredSize(new Dimension(105, 24));
        String[] categorias = {
                "Alquiler de Local Clínico",
                "Servicios Básicos (Luz/Agua/Net)",
                "Planilla y Honorarios Médicos",
                "Mantenimiento de Equipos Médicos",
                "Suministros y Material de Limpieza",
                "Marketing y Publicidad Digital",
                "Gestión de Residuos Biológicos",
                "Otros Gastos Operativos"
        };
        cboCategoria = new JComboBox<>(categorias);
        fCat.add(lCat, BorderLayout.WEST);
        fCat.add(cboCategoria, BorderLayout.CENTER);

        gridCampos.add(fFec);
        gridCampos.add(fCat);

        // Fila 2: Descripción y Monto
        JPanel fDes = new JPanel(new BorderLayout(6, 0));
        fDes.setOpaque(false);
        JLabel lDes = new JLabel("Descripción:");
        lDes.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lDes.setPreferredSize(new Dimension(105, 24));
        txtDescripcion = new JTextField();
        fDes.add(lDes, BorderLayout.WEST);
        fDes.add(txtDescripcion, BorderLayout.CENTER);

        JPanel fMon = new JPanel(new BorderLayout(6, 0));
        fMon.setOpaque(false);
        JLabel lMon = new JLabel("Monto S/:");
        lMon.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lMon.setPreferredSize(new Dimension(105, 24));
        txtMonto = new JTextField();
        fMon.add(lMon, BorderLayout.WEST);
        fMon.add(txtMonto, BorderLayout.CENTER);

        gridCampos.add(fDes);
        gridCampos.add(fMon);

        // Fila 3: Proveedor y Método de pago
        JPanel fPro = new JPanel(new BorderLayout(6, 0));
        fPro.setOpaque(false);
        JLabel lPro = new JLabel("Proveedor:");
        lPro.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lPro.setPreferredSize(new Dimension(105, 24));
        txtProveedor = new JTextField();
        fPro.add(lPro, BorderLayout.WEST);
        fPro.add(txtProveedor, BorderLayout.CENTER);

        JPanel fMet = new JPanel(new BorderLayout(6, 0));
        fMet.setOpaque(false);
        JLabel lMet = new JLabel("Método de pago:");
        lMet.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lMet.setPreferredSize(new Dimension(105, 24));
        cboMetodoPago = new JComboBox<>(new String[]{"Transferencia", "Efectivo", "Yape / Plin", "Tarjeta", "Cheque"});
        fMet.add(lMet, BorderLayout.WEST);
        fMet.add(cboMetodoPago, BorderLayout.CENTER);

        gridCampos.add(fPro);
        gridCampos.add(fMet);

        boxForm.add(gridCampos, BorderLayout.CENTER);

        // Botones oficiales: [Agregar] [Modificar] [Eliminar] [Limpiar]
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 4));
        panelBotones.setOpaque(false);

        JButton btnAgregar = Ui.boton("Agregar", true);
        btnAgregar.setPreferredSize(new Dimension(115, 34));
        btnAgregar.addActionListener(e -> agregarEgreso());

        JButton btnModificar = Ui.boton("Modificar", false);
        btnModificar.setPreferredSize(new Dimension(115, 34));
        btnModificar.addActionListener(e -> modificarEgreso());

        JButton btnEliminar = Ui.boton("Eliminar", false);
        btnEliminar.setPreferredSize(new Dimension(115, 34));
        btnEliminar.setForeground(COLOR_ROJO);
        btnEliminar.addActionListener(e -> eliminarEgreso());

        JButton btnLimpiar = Ui.boton("Limpiar", false);
        btnLimpiar.setPreferredSize(new Dimension(115, 34));
        btnLimpiar.addActionListener(e -> limpiarFormulario());

        panelBotones.add(btnAgregar);
        panelBotones.add(btnModificar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnLimpiar);

        boxForm.add(panelBotones, BorderLayout.SOUTH);
        return boxForm;
    }

    /**
     * Tabla y Footer exactos del wireframe:
     * Tabla: Fecha | Categoría | Descripción | Proveedor | Monto
     * Footer: N.° de egresos: 0 | Total egresos: S/ 0.00
     */
    private JPanel crearSeccionTablaYFooter() {
        JPanel sec = new JPanel(new BorderLayout(0, 8));
        sec.setOpaque(false);

        String[] cols = {"Fecha", "Categoría", "Descripción", "Proveedor", "Monto (S/)", "Método Pago"};
        modeloEgresos = new DefaultTableModel(cols, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        tablaEgresos = new JTable(modeloEgresos);
        Ui.formatearTabla(tablaEgresos);
        tablaEgresos.getColumnModel().getColumn(0).setPreferredWidth(85);
        tablaEgresos.getColumnModel().getColumn(1).setPreferredWidth(170);
        tablaEgresos.getColumnModel().getColumn(2).setPreferredWidth(240);
        tablaEgresos.getColumnModel().getColumn(3).setPreferredWidth(160);
        tablaEgresos.getColumnModel().getColumn(4).setPreferredWidth(85);
        tablaEgresos.getColumnModel().getColumn(5).setPreferredWidth(95);

        tablaEgresos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                seleccionarFilaEgreso();
            }
        });

        JScrollPane scroll = new JScrollPane(tablaEgresos);
        scroll.setBorder(BorderFactory.createLineBorder(COLOR_BORDE, 1));
        sec.add(scroll, BorderLayout.CENTER);

        // Footer exacto del Wireframe:
        // N.° de egresos: 0 | Total egresos: S/ 0.00
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(Color.WHITE);
        footer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDE, 1),
                new EmptyBorder(8, 18, 8, 18)
        ));

        lblNumEgresos = new JLabel("N.° de egresos: 0", SwingConstants.LEFT);
        lblNumEgresos.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblNumEgresos.setForeground(COLOR_TEXTO_TITULO);

        lblTotalEgresos = new JLabel("Total egresos: S/ 0.00", SwingConstants.RIGHT);
        lblTotalEgresos.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTotalEgresos.setForeground(COLOR_ROJO);

        footer.add(lblNumEgresos, BorderLayout.WEST);
        footer.add(lblTotalEgresos, BorderLayout.EAST);

        sec.add(footer, BorderLayout.SOUTH);
        return sec;
    }

    // =========================================================================
    // LÓGICA DE NEGOCIO Y OPERACIONES DE EGRESOS OPERATIVOS
    // =========================================================================
    public void recargarDatos() {
        modeloEgresos.setRowCount(0);
        List<EgresoOperativo> egresos = repo.getEgresosOperativos();

        double total = 0.0;
        Map<String, Double> mapCategorias = new HashMap<>();

        for (EgresoOperativo e : egresos) {
            modeloEgresos.addRow(new Object[]{
                    e.getFechaTexto(),
                    e.getCategoria(),
                    e.getDescripcion(),
                    e.getProveedor(),
                    String.format("%.2f", e.getMonto()),
                    e.getMetodoPago()
            });

            total += e.getMonto();
            mapCategorias.put(e.getCategoria(), mapCategorias.getOrDefault(e.getCategoria(), 0.0) + e.getMonto());
        }

        // Actualizar Footer
        lblNumEgresos.setText("N.° de egresos: " + egresos.size());
        lblTotalEgresos.setText("Total egresos: S/ " + String.format("%.2f", total));

        // Actualizar KPIs
        lblKpiTotalEgresos.setText("S/ " + String.format("%.2f", total));
        lblKpiCantEgresos.setText(egresos.size() + " gastos");

        double prom = egresos.isEmpty() ? 0.0 : total / egresos.size();
        lblKpiPromedioEgreso.setText("S/ " + String.format("%.2f", prom));

        String mayorCat = "-";
        double maxMonto = 0.0;
        for (Map.Entry<String, Double> entry : mapCategorias.entrySet()) {
            if (entry.getValue() > maxMonto) {
                maxMonto = entry.getValue();
                mayorCat = entry.getKey();
            }
        }
        if (mayorCat.length() > 20) {
            mayorCat = mayorCat.substring(0, 18) + "..";
        }
        lblKpiMayorCategoria.setText(mayorCat);
    }

    private void seleccionarFilaEgreso() {
        int r = tablaEgresos.getSelectedRow();
        if (r >= 0 && r < repo.getEgresosOperativos().size()) {
            EgresoOperativo e = repo.getEgresosOperativos().get(r);
            idSeleccionado = e.getIdEgreso();
            txtFecha.setText(e.getFechaTexto());
            cboCategoria.setSelectedItem(e.getCategoria());
            txtDescripcion.setText(e.getDescripcion());
            txtMonto.setText(String.format("%.2f", e.getMonto()));
            txtProveedor.setText(e.getProveedor());
            cboMetodoPago.setSelectedItem(e.getMetodoPago());
        }
    }

    private void agregarEgreso() {
        try {
            String fecStr = txtFecha.getText().trim();
            String cat = (String) cboCategoria.getSelectedItem();
            String des = txtDescripcion.getText().trim();
            String monStr = txtMonto.getText().trim();
            String pro = txtProveedor.getText().trim();
            String met = (String) cboMetodoPago.getSelectedItem();

            if (fecStr.isEmpty() || des.isEmpty() || monStr.isEmpty() || pro.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Complete todos los campos del formulario.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            double mon = Double.parseDouble(monStr);
            if (mon <= 0) {
                JOptionPane.showMessageDialog(this, "El monto debe ser mayor a 0.00.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            LocalDate fec = LocalDate.parse(fecStr, FORMATO_FECHA);
            EgresoOperativo egr = new EgresoOperativo(null, fec, cat, des, pro, mon, met);
            repo.guardarEgresoOperativo(egr);

            recargarDatos();
            limpiarFormulario();
            JOptionPane.showMessageDialog(this, "Egreso operativo registrado con éxito.", "Registro Conforme", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Datos inválidos: verifique formato de monto o fecha (dd/MM/yyyy).", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void modificarEgreso() {
        if (idSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un egreso de la tabla para modificar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            LocalDate fec = LocalDate.parse(txtFecha.getText().trim(), FORMATO_FECHA);
            String cat = (String) cboCategoria.getSelectedItem();
            String des = txtDescripcion.getText().trim();
            double mon = Double.parseDouble(txtMonto.getText().trim());
            String pro = txtProveedor.getText().trim();
            String met = (String) cboMetodoPago.getSelectedItem();

            for (EgresoOperativo e : repo.getEgresosOperativos()) {
                if (e.getIdEgreso().equalsIgnoreCase(idSeleccionado)) {
                    e.setFecha(fec);
                    e.setCategoria(cat);
                    e.setDescripcion(des);
                    e.setMonto(mon);
                    e.setProveedor(pro);
                    e.setMetodoPago(met);
                    break;
                }
            }
            recargarDatos();
            JOptionPane.showMessageDialog(this, "Egreso modificado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al modificar egreso.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarEgreso() {
        if (idSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un egreso de la tabla para eliminar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int r = JOptionPane.showConfirmDialog(this, "¿Está seguro de eliminar este registro de egreso?", "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);
        if (r == JOptionPane.YES_OPTION) {
            repo.eliminarEgresoOperativo(idSeleccionado);
            recargarDatos();
            limpiarFormulario();
        }
    }

    private void limpiarFormulario() {
        idSeleccionado = null;
        txtFecha.setText(LocalDate.now().format(FORMATO_FECHA));
        cboCategoria.setSelectedIndex(0);
        txtDescripcion.setText("");
        txtMonto.setText("");
        txtProveedor.setText("");
        cboMetodoPago.setSelectedIndex(0);
        tablaEgresos.clearSelection();
    }

    private void generarVoucherEgreso() {
        int r = tablaEgresos.getSelectedRow();
        if (r < 0 || r >= repo.getEgresosOperativos().size()) {
            JOptionPane.showMessageDialog(this, "Seleccione un egreso de la tabla para emitir su voucher contable.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        EgresoOperativo e = repo.getEgresosOperativos().get(r);

        JDialog dlg = new JDialog((java.awt.Frame) SwingUtilities.getWindowAncestor(this),
                "Voucher de Egreso Contable - Happy Pets", true);
        dlg.setSize(500, 560);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());

        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setBackground(Color.WHITE);
        p.setBorder(new EmptyBorder(14, 18, 14, 18));

        JTextArea txt = new JTextArea();
        txt.setFont(new Font("Consolas", Font.PLAIN, 12));
        txt.setEditable(false);

        StringBuilder sb = new StringBuilder();
        sb.append("====================================================\n");
        sb.append("            VETERINARIA HAPPY PETS S.A.C.           \n");
        sb.append("           RUC: 20608912345 · LIMA - PERÚ           \n");
        sb.append("            VOUCHER DE EGRESO OPERACIONAL           \n");
        sb.append("====================================================\n");
        sb.append("CÓDIGO INTERNO   : ").append(e.getIdEgreso()).append("\n");
        sb.append("FECHA DE PAGO    : ").append(e.getFechaTexto()).append("\n");
        sb.append("CATEGORÍA        : ").append(e.getCategoria()).append("\n");
        sb.append("MÉTODO DE PAGO   : ").append(e.getMetodoPago()).append("\n");
        sb.append("COMPROBANTE/REF  : ").append(e.getComprobante()).append("\n");
        sb.append("----------------------------------------------------\n");
        sb.append("BENEFICIARIO/PROV: ").append(e.getProveedor()).append("\n");
        sb.append("CONCEPTO/DETALLE : ").append(e.getDescripcion()).append("\n");
        sb.append("----------------------------------------------------\n");
        sb.append(String.format("IMPORTE TOTAL PAGADO : S/ %10.2f\n", e.getMonto()));
        sb.append("====================================================\n");
        sb.append("SON: ").append(String.format("%.2f", e.getMonto())).append(" SOLES PERUANOS\n\n");
        sb.append("  Elaborado por: Joanna Corrales (Finanzas)\n");
        sb.append("  Aprobado por : Harry Arroyo (Administración)\n\n");
        sb.append("  Firma Beneficiario: _________________________\n");
        sb.append("  DNI / RUC         : _________________________\n");

        txt.setText(sb.toString());

        p.add(new JScrollPane(txt), BorderLayout.CENTER);

        JPanel bot = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        bot.setOpaque(false);
        JButton btnImp = Ui.boton("Imprimir Voucher", true);
        btnImp.setIcon(Iconos.crearIconoImprimir(14, Color.WHITE));
        btnImp.addActionListener(ev -> {
            JOptionPane.showMessageDialog(dlg, "Voucher de egreso impreso correctamente.", "Impresión", JOptionPane.INFORMATION_MESSAGE);
            dlg.dispose();
        });
        JButton btnCerrar = Ui.boton("Cerrar", false);
        btnCerrar.addActionListener(ev -> dlg.dispose());
        bot.add(btnImp);
        bot.add(btnCerrar);
        p.add(bot, BorderLayout.SOUTH);

        dlg.add(p);
        dlg.setVisible(true);
    }
}
