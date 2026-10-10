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
import happypets.model.MovimientoCajaChica;
import happypets.ui.Iconos;
import happypets.ui.Ui;

/**
 * Submódulo 6.3: Control de Caja Chica.
 * Basado estrictamente en el wireframe oficial CONTROL DE CAJA CHICA.pdf y mejorado:
 * - Formulario: Fecha, Tipo (Ingreso/Egreso), Concepto, Monto, Responsable, Comprobante.
 * - Botones oficiales: Agregar, Modificar, Eliminar, Limpiar.
 * - Tabla interactiva con badges de tipo y comprobante.
 * - Totales inferiores: Total ingresos, Total egresos, Saldo actual.
 * - Mejoras: KPIs de fondo, Diálogo de Arqueo Físico (billetes/monedas) y Reporte de Cierre.
 */
public class VistaControlCajaChicaPanel extends happypets.ui.AssetsModulo {
    private static final long serialVersionUID = 1L;

    private static final Color COLOR_BORDE = new Color(226, 232, 240);
    private static final Color COLOR_PRIMARIO = new Color(249, 115, 22);
    private static final Color COLOR_VERDE = new Color(16, 185, 129);
    private static final Color COLOR_ROJO = new Color(239, 68, 68);
    private static final Color COLOR_AZUL = new Color(14, 165, 233);
    private static final Color COLOR_TEXTO_TITULO = new Color(30, 41, 59);
    private static final Color COLOR_TEXTO_MUTED = new Color(100, 116, 139);

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final RepositorioVeterinaria repo = RepositorioVeterinaria.getInstancia();

    // KPIs Superiores
    private JLabel lblKpiIngresos;
    private JLabel lblKpiEgresos;
    private JLabel lblKpiSaldo;
    private JLabel lblKpiOperaciones;

    // Componentes del Formulario del Wireframe
    private JTextField txtFecha;         // txtFecha
    private JComboBox<String> cboTipo;   // cboTipo: Ingreso
    private JTextField txtConcepto;      // txtConcepto
    private JTextField txtMonto;         // txtMonto
    private JTextField txtResponsable;   // txtResponsable
    private JTextField txtComprobante;   // txtComprobante

    // Tabla
    private JTable tablaCaja;
    private DefaultTableModel modeloCaja;
    private String idSeleccionado = null;

    // Totales del Footer del Wireframe
    private JLabel lblTotalIngresos;
    private JLabel lblTotalEgresos;
    private JLabel lblSaldoActual;

    public VistaControlCajaChicaPanel() {
        setLayout(new BorderLayout());
        setOpaque(false);

        JPanel contenido = new JPanel();
        contenido.setOpaque(false);
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setBorder(new EmptyBorder(12, 18, 14, 18));

        // 1. Cabecera institucional
        contenido.add(crearCabeceraVista());
        contenido.add(Box.createVerticalStrut(10));

        // 2. Fila de KPIs
        contenido.add(crearFilaKpis());
        contenido.add(Box.createVerticalStrut(12));

        // 3. Formulario oficial del Wireframe
        contenido.add(crearFormularioCaja());
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

        JLabel ico = new JLabel(Iconos.crearIconoCajaChica(22, COLOR_PRIMARIO));
        izq.add(ico);

        JPanel titulos = new JPanel();
        titulos.setOpaque(false);
        titulos.setLayout(new BoxLayout(titulos, BoxLayout.Y_AXIS));

        JLabel lblTit = new JLabel("Submódulo 6.3: Control de Caja Chica");
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblTit.setForeground(COLOR_TEXTO_TITULO);

        JLabel lblSub = new JLabel("Administración de fondo fijo para gastos menores, reposiciones y arqueos en efectivo");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(COLOR_TEXTO_MUTED);

        titulos.add(lblTit);
        titulos.add(lblSub);
        izq.add(titulos);
        cab.add(izq, BorderLayout.WEST);

        // Botón superior de Arqueo
        JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        der.setOpaque(false);

        JButton btnArqueo = Ui.boton("Realizar Arqueo Físico", true);
        btnArqueo.setBackground(COLOR_AZUL);
        btnArqueo.setIcon(Iconos.crearIconoMonedas(14, Color.WHITE));
        btnArqueo.addActionListener(e -> abrirModalArqueoCaja());

        JButton btnReporte = Ui.boton("Reporte de Cuadre", false);
        btnReporte.setIcon(Iconos.crearIconoImprimir(14, COLOR_TEXTO_TITULO));
        btnReporte.addActionListener(e -> generarReporteCuadre());

        der.add(btnArqueo);
        der.add(btnReporte);
        cab.add(der, BorderLayout.EAST);

        return cab;
    }

    private JPanel crearFilaKpis() {
        JPanel fila = new JPanel(new GridLayout(1, 4, 12, 0));
        fila.setOpaque(false);
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 74));

        lblKpiIngresos = new JLabel("S/ 0.00");
        lblKpiEgresos = new JLabel("S/ 0.00");
        lblKpiSaldo = new JLabel("S/ 0.00");
        lblKpiOperaciones = new JLabel("0 movs");

        fila.add(crearCardKpi("Total Ingresos / Fondos", lblKpiIngresos, COLOR_VERDE, "Apertura y reposiciones"));
        fila.add(crearCardKpi("Total Egresos Menores", lblKpiEgresos, COLOR_ROJO, "Gastos corrientes sustentados"));
        fila.add(crearCardKpi("Saldo Disponible Actual", lblKpiSaldo, COLOR_PRIMARIO, "Efectivo físico disponible"));
        fila.add(crearCardKpi("Movimientos Registrados", lblKpiOperaciones, COLOR_AZUL, "Transacciones en el periodo"));

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
     * Formulario que replica exactamente el wireframe CONTROL DE CAJA CHICA.pdf:
     * Fecha: txtFecha           Tipo: cboTipo: Ingreso
     * Concepto: txtConcepto     Monto: txtMonto
     * Responsable: txtResponsable Comprobante: txtComprobante
     * [Agregar] [Modificar] [Eliminar] [Limpiar]
     */
    private JPanel crearFormularioCaja() {
        JPanel boxForm = new JPanel(new BorderLayout(0, 10));
        boxForm.setBackground(Color.WHITE);
        boxForm.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDE, 1),
                new EmptyBorder(12, 18, 14, 18)
        ));

        JPanel gridCampos = new JPanel(new GridLayout(3, 2, 22, 8));
        gridCampos.setOpaque(false);

        // Fila 1: Fecha y Tipo
        JPanel fFec = new JPanel(new BorderLayout(6, 0));
        fFec.setOpaque(false);
        JLabel lFec = new JLabel("Fecha:");
        lFec.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lFec.setPreferredSize(new Dimension(95, 24));
        txtFecha = new JTextField(LocalDate.now().format(FORMATO_FECHA));
        fFec.add(lFec, BorderLayout.WEST);
        fFec.add(txtFecha, BorderLayout.CENTER);

        JPanel fTip = new JPanel(new BorderLayout(6, 0));
        fTip.setOpaque(false);
        JLabel lTip = new JLabel("Tipo:");
        lTip.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lTip.setPreferredSize(new Dimension(95, 24));
        cboTipo = new JComboBox<>(new String[]{"Ingreso", "Egreso"});
        fTip.add(lTip, BorderLayout.WEST);
        fTip.add(cboTipo, BorderLayout.CENTER);

        gridCampos.add(fFec);
        gridCampos.add(fTip);

        // Fila 2: Concepto y Monto
        JPanel fCon = new JPanel(new BorderLayout(6, 0));
        fCon.setOpaque(false);
        JLabel lCon = new JLabel("Concepto:");
        lCon.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lCon.setPreferredSize(new Dimension(95, 24));
        txtConcepto = new JTextField();
        fCon.add(lCon, BorderLayout.WEST);
        fCon.add(txtConcepto, BorderLayout.CENTER);

        JPanel fMon = new JPanel(new BorderLayout(6, 0));
        fMon.setOpaque(false);
        JLabel lMon = new JLabel("Monto S/:");
        lMon.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lMon.setPreferredSize(new Dimension(95, 24));
        txtMonto = new JTextField();
        fMon.add(lMon, BorderLayout.WEST);
        fMon.add(txtMonto, BorderLayout.CENTER);

        gridCampos.add(fCon);
        gridCampos.add(fMon);

        // Fila 3: Responsable y Comprobante
        JPanel fResp = new JPanel(new BorderLayout(6, 0));
        fResp.setOpaque(false);
        JLabel lResp = new JLabel("Responsable:");
        lResp.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lResp.setPreferredSize(new Dimension(95, 24));
        txtResponsable = new JTextField("Joanna Corrales");
        fResp.add(lResp, BorderLayout.WEST);
        fResp.add(txtResponsable, BorderLayout.CENTER);

        JPanel fComp = new JPanel(new BorderLayout(6, 0));
        fComp.setOpaque(false);
        JLabel lComp = new JLabel("Comprobante:");
        lComp.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lComp.setPreferredSize(new Dimension(95, 24));
        txtComprobante = new JTextField();
        fComp.add(lComp, BorderLayout.WEST);
        fComp.add(txtComprobante, BorderLayout.CENTER);

        gridCampos.add(fResp);
        gridCampos.add(fComp);

        boxForm.add(gridCampos, BorderLayout.CENTER);

        // Botones de acción exactos: [Agregar] [Modificar] [Eliminar] [Limpiar]
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 4));
        panelBotones.setOpaque(false);

        JButton btnAgregar = Ui.boton("Agregar", true);
        btnAgregar.setPreferredSize(new Dimension(115, 34));
        btnAgregar.addActionListener(e -> agregarMovimiento());

        JButton btnModificar = Ui.boton("Modificar", false);
        btnModificar.setPreferredSize(new Dimension(115, 34));
        btnModificar.addActionListener(e -> modificarMovimiento());

        JButton btnEliminar = Ui.boton("Eliminar", false);
        btnEliminar.setPreferredSize(new Dimension(115, 34));
        btnEliminar.setForeground(COLOR_ROJO);
        btnEliminar.addActionListener(e -> eliminarMovimiento());

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
     * Tabla y Footer idénticos al wireframe:
     * Tabla: Fecha | Tipo | Concepto | Monto | Responsable
     * Footer: Total ingresos: S/ 0.00    Total egresos: S/ 0.00    Saldo actual: S/ 0.00
     */
    private JPanel crearSeccionTablaYFooter() {
        JPanel sec = new JPanel(new BorderLayout(0, 8));
        sec.setOpaque(false);

        String[] cols = {"Fecha", "Tipo", "Concepto", "Monto (S/)", "Responsable", "Comprobante"};
        modeloCaja = new DefaultTableModel(cols, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        tablaCaja = new JTable(modeloCaja);
        Ui.formatearTabla(tablaCaja);
        tablaCaja.getColumnModel().getColumn(0).setPreferredWidth(85);
        tablaCaja.getColumnModel().getColumn(1).setPreferredWidth(75);
        tablaCaja.getColumnModel().getColumn(2).setPreferredWidth(230);
        tablaCaja.getColumnModel().getColumn(3).setPreferredWidth(85);
        tablaCaja.getColumnModel().getColumn(4).setPreferredWidth(120);
        tablaCaja.getColumnModel().getColumn(5).setPreferredWidth(100);

        tablaCaja.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                seleccionarFilaCaja();
            }
        });

        // Renderer de colores para Tipo (Ingreso verde, Egreso rojo)
        tablaCaja.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
            private static final long serialVersionUID = 1L;
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.CENTER);
                setFont(new Font("Segoe UI", Font.BOLD, 11));
                String tipo = value != null ? value.toString() : "";
                if ("Ingreso".equalsIgnoreCase(tipo)) {
                    setForeground(new Color(22, 101, 52));
                } else {
                    setForeground(new Color(185, 28, 28));
                }
                return c;
            }
        });

        JScrollPane scroll = new JScrollPane(tablaCaja);
        scroll.setBorder(BorderFactory.createLineBorder(COLOR_BORDE, 1));
        sec.add(scroll, BorderLayout.CENTER);

        // Footer exacto del Wireframe:
        // Total ingresos: S/ 0.00 | Total egresos: S/ 0.00 | Saldo actual: S/ 0.00
        JPanel footer = new JPanel(new GridLayout(1, 3, 20, 0));
        footer.setBackground(Color.WHITE);
        footer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDE, 1),
                new EmptyBorder(8, 18, 8, 18)
        ));

        lblTotalIngresos = new JLabel("Total ingresos: S/ 0.00", SwingConstants.LEFT);
        lblTotalIngresos.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTotalIngresos.setForeground(COLOR_VERDE);

        lblTotalEgresos = new JLabel("Total egresos: S/ 0.00", SwingConstants.CENTER);
        lblTotalEgresos.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTotalEgresos.setForeground(COLOR_ROJO);

        lblSaldoActual = new JLabel("Saldo actual: S/ 0.00", SwingConstants.RIGHT);
        lblSaldoActual.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblSaldoActual.setForeground(COLOR_PRIMARIO);

        footer.add(lblTotalIngresos);
        footer.add(lblTotalEgresos);
        footer.add(lblSaldoActual);

        sec.add(footer, BorderLayout.SOUTH);
        return sec;
    }

    // =========================================================================
    // LÓGICA DE NEGOCIO Y OPERACIONES DE CAJA CHICA
    // =========================================================================
    public void recargarDatos() {
        modeloCaja.setRowCount(0);
        List<MovimientoCajaChica> movimientos = repo.getMovimientosCajaChica();

        double totalIng = 0.0;
        double totalEg = 0.0;

        for (MovimientoCajaChica m : movimientos) {
            modeloCaja.addRow(new Object[]{
                    m.getFechaTexto(),
                    m.getTipo(),
                    m.getConcepto(),
                    String.format("%.2f", m.getMonto()),
                    m.getResponsable(),
                    m.getComprobante()
            });

            if (m.esIngreso()) {
                totalIng += m.getMonto();
            } else {
                totalEg += m.getMonto();
            }
        }

        double saldoActual = Math.max(0.0, totalIng - totalEg);

        // Actualizar Footer
        lblTotalIngresos.setText("Total ingresos: S/ " + String.format("%.2f", totalIng));
        lblTotalEgresos.setText("Total egresos: S/ " + String.format("%.2f", totalEg));
        lblSaldoActual.setText("Saldo actual: S/ " + String.format("%.2f", saldoActual));

        // Actualizar KPIs
        lblKpiIngresos.setText("S/ " + String.format("%.2f", totalIng));
        lblKpiEgresos.setText("S/ " + String.format("%.2f", totalEg));
        lblKpiSaldo.setText("S/ " + String.format("%.2f", saldoActual));
        lblKpiOperaciones.setText(movimientos.size() + " movimientos");
    }

    private void seleccionarFilaCaja() {
        int r = tablaCaja.getSelectedRow();
        if (r >= 0 && r < repo.getMovimientosCajaChica().size()) {
            MovimientoCajaChica m = repo.getMovimientosCajaChica().get(r);
            idSeleccionado = m.getIdMovimiento();
            txtFecha.setText(m.getFechaTexto());
            cboTipo.setSelectedItem(m.getTipo());
            txtConcepto.setText(m.getConcepto());
            txtMonto.setText(String.format("%.2f", m.getMonto()));
            txtResponsable.setText(m.getResponsable());
            txtComprobante.setText(m.getComprobante());
        }
    }

    private void agregarMovimiento() {
        try {
            String fecStr = txtFecha.getText().trim();
            String tip = (String) cboTipo.getSelectedItem();
            String con = txtConcepto.getText().trim();
            String monStr = txtMonto.getText().trim();
            String resp = txtResponsable.getText().trim();
            String comp = txtComprobante.getText().trim();

            if (fecStr.isEmpty() || con.isEmpty() || monStr.isEmpty() || resp.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Complete todos los campos obligatorios del formulario.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            double mon = Double.parseDouble(monStr);
            if (mon <= 0) {
                JOptionPane.showMessageDialog(this, "El monto debe ser mayor a 0.00.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Validar que un egreso no exceda el saldo disponible
            double saldoActual = repo.getSaldoActualCajaChica();
            if ("Egreso".equalsIgnoreCase(tip) && mon > saldoActual) {
                int conf = JOptionPane.showConfirmDialog(this,
                        "El egreso (S/ " + String.format("%.2f", mon) + ") supera el saldo actual en caja (S/ " + String.format("%.2f", saldoActual) + ").\n¿Desea registrarlo de todas formas?",
                        "Advertencia de Saldo Insuficiente", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (conf != JOptionPane.YES_OPTION) return;
            }

            LocalDate fec = LocalDate.parse(fecStr, FORMATO_FECHA);
            MovimientoCajaChica mov = new MovimientoCajaChica(null, fec, tip, con, mon, resp, comp);
            repo.guardarMovimientoCajaChica(mov);

            recargarDatos();
            limpiarFormulario();
            JOptionPane.showMessageDialog(this, "Movimiento de caja chica registrado con éxito.", "Registro Conforme", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Datos inválidos: verifique formato de monto o fecha (dd/MM/yyyy).", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void modificarMovimiento() {
        if (idSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un movimiento de la tabla para modificar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            LocalDate fec = LocalDate.parse(txtFecha.getText().trim(), FORMATO_FECHA);
            String tip = (String) cboTipo.getSelectedItem();
            String con = txtConcepto.getText().trim();
            double mon = Double.parseDouble(txtMonto.getText().trim());
            String resp = txtResponsable.getText().trim();
            String comp = txtComprobante.getText().trim();

            for (MovimientoCajaChica m : repo.getMovimientosCajaChica()) {
                if (m.getIdMovimiento().equalsIgnoreCase(idSeleccionado)) {
                    m.setFecha(fec);
                    m.setTipo(tip);
                    m.setConcepto(con);
                    m.setMonto(mon);
                    m.setResponsable(resp);
                    m.setComprobante(comp);
                    break;
                }
            }
            recargarDatos();
            JOptionPane.showMessageDialog(this, "Movimiento modificado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al modificar movimiento.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarMovimiento() {
        if (idSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un movimiento de la tabla para eliminar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int r = JOptionPane.showConfirmDialog(this, "¿Está seguro de eliminar este movimiento de caja chica?", "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);
        if (r == JOptionPane.YES_OPTION) {
            repo.eliminarMovimientoCajaChica(idSeleccionado);
            recargarDatos();
            limpiarFormulario();
        }
    }

    private void limpiarFormulario() {
        idSeleccionado = null;
        txtFecha.setText(LocalDate.now().format(FORMATO_FECHA));
        cboTipo.setSelectedIndex(0);
        txtConcepto.setText("");
        txtMonto.setText("");
        txtResponsable.setText("Joanna Corrales");
        txtComprobante.setText("");
        tablaCaja.clearSelection();
    }

    /**
     * Modal interactivo para arqueo físico de efectivo (billetes y monedas).
     */
    private void abrirModalArqueoCaja() {
        JDialog dlg = new JDialog((java.awt.Frame) SwingUtilities.getWindowAncestor(this),
                "Arqueo Físico y Cuadre de Caja Chica", true);
        dlg.setSize(520, 560);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());

        JPanel p = new JPanel(new BorderLayout(0, 12));
        p.setBackground(Color.WHITE);
        p.setBorder(new EmptyBorder(14, 18, 14, 18));

        // Cabecera del diálogo
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        JLabel tit = new JLabel("Conteo Físico de Efectivo en Bóveda");
        tit.setFont(new Font("Segoe UI", Font.BOLD, 15));
        tit.setForeground(COLOR_TEXTO_TITULO);
        top.add(tit, BorderLayout.NORTH);

        double saldoSistema = repo.getSaldoActualCajaChica();
        JLabel sub = new JLabel("Saldo según sistema: S/ " + String.format("%.2f", saldoSistema));
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sub.setForeground(COLOR_AZUL);
        top.add(sub, BorderLayout.SOUTH);
        p.add(top, BorderLayout.NORTH);

        // Denominaciones peruanas oficiales
        double[] denomValores = {100.0, 50.0, 20.0, 10.0, 5.0, 2.0, 1.0, 0.50};
        String[] denomNombres = {"Billetes de S/ 100", "Billetes de S/ 50", "Billetes de S/ 20", "Billetes de S/ 10",
                                 "Monedas de S/ 5", "Monedas de S/ 2", "Monedas de S/ 1", "Monedas de S/ 0.50"};
        JSpinner[] spinners = new JSpinner[denomValores.length];

        JPanel gridDenom = new JPanel(new GridLayout(denomValores.length, 3, 10, 4));
        gridDenom.setOpaque(false);

        JLabel lblTotalConteo = new JLabel("S/ 0.00", SwingConstants.RIGHT);
        lblTotalConteo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTotalConteo.setForeground(COLOR_PRIMARIO);

        JLabel lblDiferencia = new JLabel("Diferencia: S/ 0.00", SwingConstants.RIGHT);
        lblDiferencia.setFont(new Font("Segoe UI", Font.BOLD, 13));

        Runnable recalcularConteo = () -> {
            double totalFisico = 0.0;
            for (int i = 0; i < denomValores.length; i++) {
                int cant = ((Number) spinners[i].getValue()).intValue();
                totalFisico += cant * denomValores[i];
            }
            lblTotalConteo.setText("S/ " + String.format("%.2f", totalFisico));
            double dif = totalFisico - saldoSistema;
            if (Math.abs(dif) < 0.01) {
                lblDiferencia.setText("Caja Cuadrada Exacta (0.00)");
                lblDiferencia.setForeground(COLOR_VERDE);
            } else if (dif > 0) {
                lblDiferencia.setText("Sobrante: + S/ " + String.format("%.2f", dif));
                lblDiferencia.setForeground(COLOR_AZUL);
            } else {
                lblDiferencia.setText("Faltante: - S/ " + String.format("%.2f", Math.abs(dif)));
                lblDiferencia.setForeground(COLOR_ROJO);
            }
        };

        for (int i = 0; i < denomValores.length; i++) {
            JLabel lblD = new JLabel(denomNombres[i]);
            lblD.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            spinners[i] = new JSpinner(new SpinnerNumberModel(0, 0, 500, 1));
            spinners[i].addChangeListener(e -> recalcularConteo.run());

            gridDenom.add(lblD);
            gridDenom.add(spinners[i]);
            JLabel subTotalDenom = new JLabel("x S/ " + String.format("%.2f", denomValores[i]));
            subTotalDenom.setFont(new Font("Segoe UI", Font.ITALIC, 11));
            subTotalDenom.setForeground(COLOR_TEXTO_MUTED);
            gridDenom.add(subTotalDenom);
        }

        p.add(gridDenom, BorderLayout.CENTER);

        // Bloque inferior con totales y botón guardar acta de arqueo
        JPanel bot = new JPanel(new BorderLayout(0, 8));
        bot.setOpaque(false);

        JPanel pTotales = new JPanel(new GridLayout(2, 2, 8, 4));
        pTotales.setBackground(Color.WHITE);
        pTotales.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDE, 1),
                new EmptyBorder(8, 12, 8, 12)
        ));

        JLabel l1 = new JLabel("Total Efectivo Contado:");
        l1.setFont(new Font("Segoe UI", Font.BOLD, 12));
        pTotales.add(l1);
        pTotales.add(lblTotalConteo);

        JLabel l2 = new JLabel("Estado del Cuadre:");
        l2.setFont(new Font("Segoe UI", Font.BOLD, 12));
        pTotales.add(l2);
        pTotales.add(lblDiferencia);

        bot.add(pTotales, BorderLayout.NORTH);

        JPanel pBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        pBtns.setOpaque(false);

        JButton btnGuardarActa = Ui.boton("Guardar Acta de Arqueo", true);
        btnGuardarActa.addActionListener(e -> {
            JOptionPane.showMessageDialog(dlg,
                    "Acta de Arqueo registrada satisfactoriamente para la sesión actual.\nResponsable: Joanna Corrales.",
                    "Arqueo Conforme", JOptionPane.INFORMATION_MESSAGE);
            dlg.dispose();
        });

        JButton btnCerrar = Ui.boton("Cerrar", false);
        btnCerrar.addActionListener(e -> dlg.dispose());

        pBtns.add(btnGuardarActa);
        pBtns.add(btnCerrar);
        bot.add(pBtns, BorderLayout.SOUTH);

        p.add(bot, BorderLayout.SOUTH);

        dlg.add(p);
        dlg.setVisible(true);
    }

    private void generarReporteCuadre() {
        JDialog dlg = new JDialog((java.awt.Frame) SwingUtilities.getWindowAncestor(this),
                "Reporte Oficial de Movimientos de Caja Chica", true);
        dlg.setSize(540, 600);
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
        sb.append("         VETERINARIA HAPPY PETS S.A.C.             \n");
        sb.append("       REPORTE DE CIERRE Y MOVIMIENTOS CAJA CHICA   \n");
        sb.append("====================================================\n");
        sb.append("Fecha de Emisión: ").append(LocalDate.now().format(FORMATO_FECHA)).append("\n");
        sb.append("Responsable     : Joanna Corrales / Harry Arroyo\n");
        sb.append("Estado          : Conciliado\n");
        sb.append("----------------------------------------------------\n");
        sb.append(String.format("%-10s %-8s %-20s %10s\n", "FECHA", "TIPO", "CONCEPTO", "MONTO"));
        sb.append("----------------------------------------------------\n");

        for (MovimientoCajaChica m : repo.getMovimientosCajaChica()) {
            String con = m.getConcepto().length() > 20 ? m.getConcepto().substring(0, 18) + ".." : m.getConcepto();
            sb.append(String.format("%-10s %-8s %-20s S/ %7.2f\n",
                    m.getFechaTexto(), m.getTipo(), con, m.getMonto()));
        }

        sb.append("----------------------------------------------------\n");
        sb.append(String.format("%-30s S/ %10.2f\n", "TOTAL INGRESOS REGISTRADOS:", repo.getTotalIngresosCajaChica()));
        sb.append(String.format("%-30s S/ %10.2f\n", "TOTAL EGRESOS EJECUTADOS:", repo.getTotalEgresosCajaChica()));
        sb.append(String.format("%-30s S/ %10.2f\n", "SALDO DISPONIBLE EN EFECTIVO:", repo.getSaldoActualCajaChica()));
        sb.append("====================================================\n");
        sb.append("\n  Firma Responsable de Caja: ____________________\n");
        sb.append("  Firma Administración     : ____________________\n");

        txt.setText(sb.toString());

        p.add(new JScrollPane(txt), BorderLayout.CENTER);

        JPanel bot = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        bot.setOpaque(false);
        JButton btnImp = Ui.boton("Imprimir Acta", true);
        btnImp.setIcon(Iconos.crearIconoImprimir(14, Color.WHITE));
        btnImp.addActionListener(e -> {
            JOptionPane.showMessageDialog(dlg, "Reporte enviado a imprimir con éxito.", "Impresión", JOptionPane.INFORMATION_MESSAGE);
            dlg.dispose();
        });
        JButton btnCerrar = Ui.boton("Cerrar", false);
        btnCerrar.addActionListener(e -> dlg.dispose());
        bot.add(btnImp);
        bot.add(btnCerrar);
        p.add(bot, BorderLayout.SOUTH);

        dlg.add(p);
        dlg.setVisible(true);
    }
}
