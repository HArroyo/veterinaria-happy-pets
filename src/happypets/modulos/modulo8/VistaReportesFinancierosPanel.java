package happypets.modulos.modulo8;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import happypets.data.RepositorioVeterinaria;
import happypets.model.DesgloseFinancieroPrestacion;
import happypets.ui.Iconos;
import happypets.ui.Ui;

/**
 * Submódulo 8.3: Reportes Financieros
 * Responsable: Arroyo Preciado, Harry Martin
 * Presenta el balance consolidado de ingresos brutos, costes operativos, margen EBITDA,
 * gráfico Donut de canales (Servicios vs Productos), barras comparativas semestrales y
 * la tabla departamental con rentabilidad y estados financieros.
 */
public class VistaReportesFinancierosPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private final RepositorioVeterinaria repo = RepositorioVeterinaria.getInstancia();

    private JComboBox<String> comboPeriodo;
    private JComboBox<String> comboDivisa;
    private JComboBox<String> comboSede;
    private JComboBox<String> comboMetodoPago;
    private JTextField txtRangoFechas;

    private JTable tablaDesglose;
    private DefaultTableModel modeloDesglose;
    private Runnable alSolicitarExportador;

    private GraficoDonutCanalesPanel graficoDonut;
    private GraficoIngresosGastosPanel graficoBarrasFinancieras;

    public VistaReportesFinancierosPanel() {
        this(null);
    }

    public VistaReportesFinancierosPanel(Runnable alSolicitarExportador) {
        this.alSolicitarExportador = alSolicitarExportador;
        setLayout(new BorderLayout(0, 16));
        setBackground(new Color(248, 250, 252));
        setBorder(BorderFactory.createEmptyBorder(20, 24, 24, 24));

        add(crearCabeceraSuperior(), BorderLayout.NORTH);

        JPanel panelCuerpo = new JPanel();
        panelCuerpo.setLayout(new BoxLayout(panelCuerpo, BoxLayout.Y_AXIS));
        panelCuerpo.setBackground(new Color(248, 250, 252));

        panelCuerpo.add(crearPanelKPIs());
        panelCuerpo.add(Box.createVerticalStrut(16));
        panelCuerpo.add(crearBarraFiltros());
        panelCuerpo.add(Box.createVerticalStrut(16));
        panelCuerpo.add(crearPanelGraficosFinancieros());
        panelCuerpo.add(Box.createVerticalStrut(16));
        panelCuerpo.add(crearPanelTablaDepartamental());
        panelCuerpo.add(Box.createVerticalStrut(16));
        panelCuerpo.add(crearBannerAccesoExportador());

        JScrollPane scrollGeneral = new JScrollPane(panelCuerpo);
        scrollGeneral.setBorder(null);
        scrollGeneral.getVerticalScrollBar().setUnitIncrement(16);
        scrollGeneral.setBackground(new Color(248, 250, 252));
        add(scrollGeneral, BorderLayout.CENTER);

        cargarDatosTabla();
    }

    public void setAlSolicitarExportador(Runnable r) {
        this.alSolicitarExportador = r;
    }

    private JPanel crearCabeceraSuperior() {
        JPanel cab = new JPanel(new BorderLayout(16, 8));
        cab.setOpaque(false);

        JPanel pnlTit = new JPanel();
        pnlTit.setLayout(new BoxLayout(pnlTit, BoxLayout.Y_AXIS));
        pnlTit.setOpaque(false);

        JLabel lblBreadcrumb = new JLabel("Módulos Financieros / Inteligencia de Negocio");
        lblBreadcrumb.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblBreadcrumb.setForeground(new Color(13, 148, 136));
        pnlTit.add(lblBreadcrumb);
        pnlTit.add(Box.createVerticalStrut(2));

        JLabel lblTitulo = new JLabel("Reportes Financieros y Rendimiento Operativo");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(new Color(15, 23, 42));
        pnlTit.add(lblTitulo);
        pnlTit.add(Box.createVerticalStrut(3));

        JLabel lblSub = new JLabel("Balance consolidado de ingresos, costes asistenciales y rentabilidad por centro de coste.");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(new Color(100, 116, 139));
        pnlTit.add(lblSub);

        cab.add(pnlTit, BorderLayout.WEST);

        JPanel pnlControles = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlControles.setOpaque(false);

        comboPeriodo = Ui.combo(new String[]{
                "2024 (Q1 - Q2 Acumulado)",
                "Mayo 2024 (Mes en Curso)",
                "Primer Trimestre 2024 (Q1)",
                "Ejercicio Fiscal Completo 2023"
        });
        comboPeriodo.setPreferredSize(new Dimension(200, 34));

        comboDivisa = Ui.combo(new String[]{"PEN (S/)", "EUR (€)", "USD ($)"});
        comboDivisa.setPreferredSize(new Dimension(95, 34));

        JButton btnDescargar = Ui.botonPrimario("Descargar Resumen", Iconos.crearIconoDescargar(14, Color.WHITE));
        btnDescargar.setPreferredSize(new Dimension(175, 34));
        btnDescargar.addActionListener(e -> generarReporteFinanciero());

        pnlControles.add(comboPeriodo);
        pnlControles.add(comboDivisa);
        pnlControles.add(btnDescargar);

        cab.add(pnlControles, BorderLayout.EAST);
        return cab;
    }

    private JPanel crearPanelKPIs() {
        JPanel panelKPIs = new JPanel(new GridLayout(1, 4, 14, 0));
        panelKPIs.setOpaque(false);
        panelKPIs.setMaximumSize(new Dimension(Integer.MAX_VALUE, 115));

        // KPI 1: Ingresos Brutos Totales
        panelKPIs.add(crearTarjetaKPI(
                "Ingresos Brutos Totales",
                "S/ 348,650.00",
                "+14.2% vs periodo anterior",
                new Color(16, 185, 129),
                "Meta período: S/ 330,000 (105% cumplido)",
                new Color(236, 253, 245),
                Iconos.crearIconoPOS(22, new Color(13, 148, 136))
        ));

        // KPI 2: Costes Operativos
        panelKPIs.add(crearTarjetaKPI(
                "Costes y Gastos Operativos",
                "S/ 162,420.00",
                "-2.8% eficiencia",
                new Color(16, 185, 129),
                "Representa el 46.5% de la facturación",
                new Color(254, 243, 199),
                Iconos.crearIconoEgreso(22, new Color(217, 119, 6))
        ));

        // KPI 3: Margen Operativo Neto (EBITDA)
        panelKPIs.add(crearTarjetaKPI(
                "Margen Operativo Neto (EBITDA)",
                "S/ 186,230.00",
                "53.4% Rendimiento Neto",
                new Color(2, 132, 199),
                "+4.1 pts porcentuales vs ejercicio 2023",
                new Color(240, 249, 255),
                Iconos.crearIconoReportes(22, new Color(2, 132, 199))
        ));

        // KPI 4: Ticket Medio
        panelKPIs.add(crearTarjetaKPI(
                "Ticket Medio por Paciente",
                "S/ 76.40",
                "4,563 Visitas totales",
                new Color(100, 116, 139),
                "+S/ 5.80 vs media base histórica",
                new Color(241, 245, 249),
                Iconos.crearIconoFactura(22, new Color(79, 70, 229))
        ));

        return panelKPIs;
    }

    private JPanel crearTarjetaKPI(String titulo, String valor, String badgeTexto, Color badgeColor,
                                   String pieTexto, Color iconoBg, javax.swing.Icon icono) {
        JPanel card = new JPanel(new BorderLayout(8, 6));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                BorderFactory.createEmptyBorder(12, 14, 12, 14)
        ));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JLabel lblTit = new JLabel(titulo);
        lblTit.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblTit.setForeground(new Color(100, 116, 139));
        top.add(lblTit, BorderLayout.WEST);

        JLabel lblIcono = new JLabel(icono);
        lblIcono.setOpaque(true);
        lblIcono.setBackground(iconoBg);
        lblIcono.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        top.add(lblIcono, BorderLayout.EAST);
        card.add(top, BorderLayout.NORTH);

        JPanel centro = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
        centro.setOpaque(false);

        JLabel lblVal = new JLabel(valor);
        lblVal.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblVal.setForeground(new Color(15, 23, 42));
        centro.add(lblVal);

        JLabel badge = new JLabel("  " + badgeTexto + "  ");
        badge.setFont(new Font("Segoe UI", Font.BOLD, 10));
        badge.setOpaque(true);
        badge.setBackground(new Color(badgeColor.getRed(), badgeColor.getGreen(), badgeColor.getBlue(), 25));
        badge.setForeground(badgeColor);
        badge.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));
        centro.add(badge);

        card.add(centro, BorderLayout.CENTER);

        JLabel lblPie = new JLabel(pieTexto);
        lblPie.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblPie.setForeground(new Color(148, 163, 184));
        card.add(lblPie, BorderLayout.SOUTH);

        return card;
    }

    private JPanel crearBarraFiltros() {
        JPanel card = new JPanel(new BorderLayout(14, 0));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                BorderFactory.createEmptyBorder(10, 16, 10, 16)
        ));

        JPanel fila = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
        fila.setOpaque(false);

        // Rango fechas
        JPanel pnlFec = new JPanel(new BorderLayout(4, 2));
        pnlFec.setOpaque(false);
        JLabel lblF = new JLabel("Rango Contable");
        lblF.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblF.setForeground(new Color(100, 116, 139));
        pnlFec.add(lblF, BorderLayout.NORTH);
        txtRangoFechas = new JTextField("01/01/2024 - 30/06/2024", 14);
        txtRangoFechas.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtRangoFechas.setPreferredSize(new Dimension(170, 30));
        pnlFec.add(txtRangoFechas, BorderLayout.CENTER);
        fila.add(pnlFec);

        // Sede
        JPanel pnlSede = new JPanel(new BorderLayout(4, 2));
        pnlSede.setOpaque(false);
        JLabel lblS = new JLabel("Centro Operativo / Sede");
        lblS.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblS.setForeground(new Color(100, 116, 139));
        pnlSede.add(lblS, BorderLayout.NORTH);
        comboSede = new JComboBox<>(new String[]{"Todas las Sedes y Filiales", "Sede Central (Miraflores)", "Sede Filial (San Borja)"});
        comboSede.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        comboSede.setPreferredSize(new Dimension(190, 30));
        comboSede.setBackground(Color.WHITE);
        pnlSede.add(comboSede, BorderLayout.CENTER);
        fila.add(pnlSede);

        // Tipo Pago
        JPanel pnlPago = new JPanel(new BorderLayout(4, 2));
        pnlPago.setOpaque(false);
        JLabel lblP = new JLabel("Canal de Cobro");
        lblP.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblP.setForeground(new Color(100, 116, 139));
        pnlPago.add(lblP, BorderLayout.NORTH);
        comboMetodoPago = new JComboBox<>(new String[]{"Todos los Canales", "POS / Tarjeta", "Transferencia / Yape", "Efectivo"});
        comboMetodoPago.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        comboMetodoPago.setPreferredSize(new Dimension(160, 30));
        comboMetodoPago.setBackground(Color.WHITE);
        pnlPago.add(comboMetodoPago, BorderLayout.CENTER);
        fila.add(pnlPago);

        card.add(fila, BorderLayout.CENTER);

        // Botones de filtro
        JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        der.setOpaque(false);

        JButton btnAplicar = Ui.botonPrimario("Aplicar Filtro", Iconos.crearIconoRefrescar(12, Color.WHITE));
        btnAplicar.setPreferredSize(new Dimension(125, 32));
        btnAplicar.addActionListener(e -> {
            if (graficoDonut != null) graficoDonut.repaint();
            if (graficoBarrasFinancieras != null) graficoBarrasFinancieras.repaint();
            JOptionPane.showMessageDialog(this, "Balance financiero consolidado recalculado con éxito.", "Filtros Financieros", JOptionPane.INFORMATION_MESSAGE);
        });

        JButton btnRestablecer = Ui.botonSecundario("Restablecer", null);
        btnRestablecer.setPreferredSize(new Dimension(100, 32));
        btnRestablecer.addActionListener(e -> {
            comboSede.setSelectedIndex(0);
            comboMetodoPago.setSelectedIndex(0);
            if (graficoDonut != null) graficoDonut.repaint();
            if (graficoBarrasFinancieras != null) graficoBarrasFinancieras.repaint();
        });

        der.add(btnAplicar);
        der.add(btnRestablecer);
        card.add(der, BorderLayout.EAST);

        return card;
    }

    private JPanel crearPanelGraficosFinancieros() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;

        // Gráfico Izquierdo (Donut Canales: Servicios vs Productos)
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.45;
        gbc.insets = new Insets(0, 0, 0, 8);

        JPanel cardDonut = new JPanel(new BorderLayout(0, 8));
        cardDonut.setBackground(Color.WHITE);
        cardDonut.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                BorderFactory.createEmptyBorder(14, 16, 14, 16)
        ));

        JPanel titDonut = new JPanel(new BorderLayout());
        titDonut.setOpaque(false);

        JLabel lblTD = new JLabel("Fuentes de Ingresos: Servicios vs Productos");
        lblTD.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTD.setForeground(new Color(15, 23, 42));
        titDonut.add(lblTD, BorderLayout.WEST);

        JLabel lblSD = new JLabel("Distribución porcentual por línea");
        lblSD.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblSD.setForeground(new Color(100, 116, 139));
        titDonut.add(lblSD, BorderLayout.EAST);
        cardDonut.add(titDonut, BorderLayout.NORTH);

        graficoDonut = new GraficoDonutCanalesPanel();
        graficoDonut.setPreferredSize(new Dimension(380, 210));
        cardDonut.add(graficoDonut, BorderLayout.CENTER);

        // Barra inferior del donut: 65% Servicios vs 35% Productos
        JPanel pnlResumenDonut = new JPanel(new GridLayout(1, 2, 8, 0));
        pnlResumenDonut.setBackground(new Color(248, 250, 252));
        pnlResumenDonut.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(241, 245, 249), 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));

        JLabel lblServ = new JLabel("● Servicios Clínicos: S/ 226,622 (65%)");
        lblServ.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblServ.setForeground(new Color(15, 76, 129));
        pnlResumenDonut.add(lblServ);

        JLabel lblProd = new JLabel("● Productos & Farmacia: S/ 122,028 (35%)");
        lblProd.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblProd.setForeground(new Color(13, 148, 136));
        pnlResumenDonut.add(lblProd);

        cardDonut.add(pnlResumenDonut, BorderLayout.SOUTH);
        panel.add(cardDonut, gbc);

        // Gráfico Derecho (Barras Ingresos vs Gastos Mensuales Ene-Jun)
        gbc.gridx = 1;
        gbc.weightx = 0.55;
        gbc.insets = new Insets(0, 8, 0, 0);

        JPanel cardBarras = new JPanel(new BorderLayout(0, 8));
        cardBarras.setBackground(Color.WHITE);
        cardBarras.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                BorderFactory.createEmptyBorder(14, 16, 14, 16)
        ));

        JPanel titBarras = new JPanel(new BorderLayout());
        titBarras.setOpaque(false);

        JLabel lblTB = new JLabel("Ingresos vs Gastos Operativos Mensuales (2024)");
        lblTB.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTB.setForeground(new Color(15, 23, 42));
        titBarras.add(lblTB, BorderLayout.WEST);

        JPanel pnlBadgeMayor = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        pnlBadgeMayor.setOpaque(false);
        JLabel badgeMayo = new JLabel("  ★ Mayo: Mayor Margen (S/ 36,100)  ");
        badgeMayo.setFont(new Font("Segoe UI", Font.BOLD, 10));
        badgeMayo.setOpaque(true);
        badgeMayo.setBackground(new Color(236, 253, 245));
        badgeMayo.setForeground(new Color(16, 185, 129));
        pnlBadgeMayor.add(badgeMayo);
        titBarras.add(pnlBadgeMayor, BorderLayout.EAST);
        cardBarras.add(titBarras, BorderLayout.NORTH);

        graficoBarrasFinancieras = new GraficoIngresosGastosPanel();
        graficoBarrasFinancieras.setPreferredSize(new Dimension(460, 210));
        cardBarras.add(graficoBarrasFinancieras, BorderLayout.CENTER);

        // Barra inferior: Ratio cobro/gasto y costo medio
        JPanel pnlResumenBarras = new JPanel(new GridLayout(1, 2, 8, 0));
        pnlResumenBarras.setBackground(new Color(248, 250, 252));
        pnlResumenBarras.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(241, 245, 249), 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));

        JLabel lblCosteMedio = new JLabel("• Coste operativo medio: S/ 27,070 / mes");
        lblCosteMedio.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblCosteMedio.setForeground(new Color(71, 85, 105));
        pnlResumenBarras.add(lblCosteMedio);

        JLabel lblRatio = new JLabel("• Ratio cobro/gasto: 2.15x (Saludable)");
        lblRatio.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblRatio.setForeground(new Color(16, 185, 129));
        pnlResumenBarras.add(lblRatio);

        cardBarras.add(pnlResumenBarras, BorderLayout.SOUTH);
        panel.add(cardBarras, gbc);

        return panel;
    }

    private JPanel crearPanelTablaDepartamental() {
        JPanel card = new JPanel(new BorderLayout(0, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                BorderFactory.createEmptyBorder(14, 16, 14, 16)
        ));

        // Título de la tabla
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JLabel lblTit = new JLabel("Resumen Financiero por Departamento y Prestación");
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTit.setForeground(new Color(15, 23, 42));
        top.add(lblTit, BorderLayout.WEST);

        JLabel lblDet = new JLabel("Datos semestrales consolidados (Enero - Junio 2024)");
        lblDet.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblDet.setForeground(new Color(100, 116, 139));
        top.add(lblDet, BorderLayout.EAST);
        card.add(top, BorderLayout.NORTH);

        // Tabla Swing
        String[] columnas = {
                "Línea de Servicio / Producto", "Centro Operativo", "N° Transacciones",
                "Ingresos Brutos", "Coste Directo", "Margen Bruto", "% Rentabilidad", "Estado Financiero"
        };
        modeloDesglose = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };

        tablaDesglose = new JTable(modeloDesglose);
        Ui.formatearTabla(tablaDesglose, new int[]{6, 7}, new int[]{2, 3, 4, 5});
        tablaDesglose.getColumnModel().getColumn(0).setPreferredWidth(220);
        tablaDesglose.getColumnModel().getColumn(1).setPreferredWidth(140);
        tablaDesglose.getColumnModel().getColumn(2).setPreferredWidth(100);
        tablaDesglose.getColumnModel().getColumn(3).setPreferredWidth(110);
        tablaDesglose.getColumnModel().getColumn(4).setPreferredWidth(100);
        tablaDesglose.getColumnModel().getColumn(5).setPreferredWidth(110);
        tablaDesglose.getColumnModel().getColumn(6).setPreferredWidth(95);
        tablaDesglose.getColumnModel().getColumn(7).setPreferredWidth(110);

        // Render de Estado Financiero
        tablaDesglose.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                l.setHorizontalAlignment(SwingConstants.CENTER);
                String est = value != null ? value.toString() : "";
                l.setFont(new Font("Segoe UI", Font.BOLD, 11));
                l.setOpaque(true);
                if ("Óptimo".equalsIgnoreCase(est)) {
                    l.setBackground(Ui.TURQUESA_SUAVE);
                    l.setForeground(Ui.TURQUESA_PROFUNDO);
                } else if ("Excelente".equalsIgnoreCase(est)) {
                    l.setBackground(new Color(236, 253, 245));
                    l.setForeground(new Color(16, 185, 129));
                } else if ("Margen Normal".equalsIgnoreCase(est)) {
                    l.setBackground(Ui.TURQUESA_SUAVE);
                    l.setForeground(Ui.TURQUESA_OSCURO);
                } else if ("A Revisar".equalsIgnoreCase(est)) {
                    l.setBackground(new Color(254, 243, 199));
                    l.setForeground(new Color(180, 83, 9));
                } else {
                    l.setBackground(new Color(241, 245, 249));
                    l.setForeground(new Color(71, 85, 105));
                }
                return l;
            }
        });

        JScrollPane scroll = new JScrollPane(tablaDesglose);
        scroll.setPreferredSize(new Dimension(800, 230));
        scroll.setBorder(BorderFactory.createLineBorder(Ui.BORDE_SUAVE, 1));
        card.add(scroll, BorderLayout.CENTER);

        // Fila Totalizadora Destacada
        JPanel pnlTotales = new JPanel(new GridLayout(1, 4, 16, 0));
        pnlTotales.setBackground(new Color(241, 245, 249));
        pnlTotales.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)
        ));

        pnlTotales.add(new JLabel("Totales Consolidados: 5,310 Ops"));
        pnlTotales.add(new JLabel("Ingresos: S/ 348,650.00"));
        pnlTotales.add(new JLabel("Costes: S/ 162,420.00"));
        JLabel lblMargenTot = new JLabel("Margen EBITDA: S/ 186,230.00 (53.4%)");
        lblMargenTot.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblMargenTot.setForeground(new Color(16, 185, 129));
        pnlTotales.add(lblMargenTot);

        card.add(pnlTotales, BorderLayout.SOUTH);
        return card;
    }

    private JPanel crearBannerAccesoExportador() {
        JPanel banner = new JPanel(new BorderLayout(14, 0));
        banner.setBackground(new Color(240, 253, 250));
        banner.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(204, 251, 241), 1),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));

        JPanel izq = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        izq.setOpaque(false);
        izq.add(new JLabel(Iconos.crearIconoReportes(24, new Color(13, 148, 136))));

        JPanel txts = new JPanel();
        txts.setLayout(new BoxLayout(txts, BoxLayout.Y_AXIS));
        txts.setOpaque(false);

        JLabel t = new JLabel("¿Requiere exportar este balance contable para auditoría o SUNAT?");
        t.setFont(new Font("Segoe UI", Font.BOLD, 12));
        t.setForeground(new Color(19, 78, 74));
        txts.add(t);

        JLabel d = new JLabel("Utilice el Exportador de Datos para generar un libro mayor en Microsoft Excel (.xlsx) o archivo plano .CSV con segmentación avanzada.");
        d.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        d.setForeground(new Color(15, 118, 110));
        txts.add(d);
        izq.add(txts);

        banner.add(izq, BorderLayout.CENTER);

        JButton btnIrExportador = Ui.botonPrimario("Ir al Exportador de Datos", Iconos.crearIconoDocumento(13, Color.WHITE));
        btnIrExportador.setPreferredSize(new Dimension(205, 34));
        btnIrExportador.addActionListener(e -> {
            if (alSolicitarExportador != null) {
                alSolicitarExportador.run();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Accediendo a la pestaña del Exportador de Datos...",
                        "Exportador de Datos", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        banner.add(btnIrExportador, BorderLayout.EAST);

        return banner;
    }

    private void cargarDatosTabla() {
        List<DesgloseFinancieroPrestacion> lista = repo.getDesglosesFinancieros();
        modeloDesglose.setRowCount(0);

        for (DesgloseFinancieroPrestacion d : lista) {
            modeloDesglose.addRow(new Object[]{
                    d.getLineaServicio(),
                    d.getCentroOperativo(),
                    String.format("%,d", d.getTransacciones()),
                    String.format("S/ %,.2f", d.getIngresosBrutos()),
                    String.format("S/ %,.2f", d.getCosteDirecto()),
                    String.format("S/ %,.2f", d.getMargenBruto()),
                    String.format("%.1f%%", d.getPorcentajeRentabilidad()),
                    d.getEstadoFinanciero()
            });
        }
    }

    private void generarReporteFinanciero() {
        List<DesgloseFinancieroPrestacion> lista = repo.getDesglosesFinancieros();
        String[][] datos = new String[lista.size()][7];
        for (int i = 0; i < lista.size(); i++) {
            DesgloseFinancieroPrestacion d = lista.get(i);
            datos[i] = new String[]{
                String.valueOf(i + 1),
                d.getLineaServicio(),
                d.getCentroOperativo(),
                String.format("%,d", d.getTransacciones()),
                String.format("S/ %,.2f", d.getIngresosBrutos()),
                String.format("S/ %,.2f", d.getCosteDirecto()),
                String.format("S/ %,.2f (%.1f%%)", d.getMargenBruto(), d.getPorcentajeRentabilidad())
            };
        }

        String[][] meta = new String[][]{
            {"Ejercicio Contable", comboPeriodo != null ? comboPeriodo.getSelectedItem().toString() : "2024"},
            {"Moneda", comboDivisa != null ? comboDivisa.getSelectedItem().toString() : "PEN (S/)"},
            {"Total Operaciones", "5,310 transacciones"},
            {"Ingresos Totales", "S/ 348,650.00"},
            {"Costes Operativos", "S/ 162,420.00"},
            {"Margen EBITDA Global", "S/ 186,230.00 (53.4%)"}
        };

        Ui.mostrarVisorReporte(
            (java.awt.Frame) SwingUtilities.getWindowAncestor(this),
            "BALANCE FINANCIERO Y RENDIMIENTO OPERATIVO",
            "Consolidado Departamental y Margen de Contribución",
            meta,
            new String[]{"N°", "Línea de Servicio / Producto", "Centro Operativo", "Transacciones", "Ingresos Brutos", "Costes", "Margen Bruto"},
            datos,
            "Resumen financiero oficial generado 100% en memoria para auditoría interna.",
            "Balance_Financiero_Ejecutivo"
        );
    }

    /**
     * Gráfico Donut de 6 canales en Graphics2D con centro blanco.
     */
    private static class GraficoDonutCanalesPanel extends JPanel {
        private static final long serialVersionUID = 1L;

        GraficoDonutCanalesPanel() {
            setBackground(Color.WHITE);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            int size = Math.min(w / 2 - 20, h - 30);
            int cx = 20 + size / 2;
            int cy = h / 2;

            double[] porcs = {0.246, 0.225, 0.196, 0.179, 0.100, 0.054};
            Color[] colores = {
                    new Color(15, 76, 129),   // Consultas (Azul oscuro)
                    new Color(2, 132, 199),   // Cirugías (Azul cielo)
                    new Color(13, 148, 136),  // Farmacia (Teal)
                    new Color(99, 102, 241),  // Diagnóstico (Índigo)
                    new Color(217, 119, 6),   // Pet Shop (Ámbar)
                    new Color(168, 85, 247)   // Grooming (Púrpura)
            };
            String[] nombres = {
                    "Consultas (24.6%)",
                    "Cirugías (22.5%)",
                    "Farmacia (19.6%)",
                    "Diagnóstico (17.9%)",
                    "Pet Shop (10.0%)",
                    "Grooming (5.4%)"
            };

            double anguloInicio = 90.0;
            for (int i = 0; i < porcs.length; i++) {
                double anguloExt = porcs[i] * 360.0;
                g2.setColor(colores[i]);
                g2.fill(new Arc2D.Double(cx - size / 2.0, cy - size / 2.0, size, size, anguloInicio, -anguloExt, Arc2D.PIE));
                anguloInicio -= anguloExt;
            }

            // Centro blanco para efecto Donut
            int innerSize = (int) (size * 0.58);
            g2.setColor(Color.WHITE);
            g2.fill(new Ellipse2D.Double(cx - innerSize / 2.0, cy - innerSize / 2.0, innerSize, innerSize));

            // Texto central
            g2.setColor(new Color(15, 23, 42));
            g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
            String totalTxt = "S/ 348.6K";
            int tw = g2.getFontMetrics().stringWidth(totalTxt);
            g2.drawString(totalTxt, cx - tw / 2, cy);

            g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            g2.setColor(new Color(100, 116, 139));
            String subTxt = "Total Bruto";
            int stw = g2.getFontMetrics().stringWidth(subTxt);
            g2.drawString(subTxt, cx - stw / 2, cy + 14);

            // Leyenda a la derecha
            int legX = cx + size / 2 + 20;
            int legY = 25;
            int itemH = 26;

            for (int i = 0; i < nombres.length; i++) {
                int y = legY + i * itemH;
                g2.setColor(colores[i]);
                g2.fillRect(legX, y - 9, 10, 10);

                g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                g2.setColor(new Color(30, 41, 59));
                g2.drawString(nombres[i], legX + 16, y);
            }

            g2.dispose();
        }
    }

    /**
     * Gráfico de Barras Dobles: Ingresos vs Gastos Mensuales (Ene a Jun).
     */
    private static class GraficoIngresosGastosPanel extends JPanel {
        private static final long serialVersionUID = 1L;

        GraficoIngresosGastosPanel() {
            setBackground(Color.WHITE);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            int padLeft = 45;
            int padRight = 20;
            int padTop = 20;
            int padBottom = 35;

            int graphW = w - padLeft - padRight;
            int graphH = h - padTop - padBottom;

            if (graphW <= 0 || graphH <= 0) {
                g2.dispose();
                return;
            }

            double maxVal = 70000.0;
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));

            for (int i = 0; i <= 3; i++) {
                double val = i * 20000.0;
                int y = padTop + graphH - (int) ((val / maxVal) * graphH);

                g2.setColor(new Color(241, 245, 249));
                g2.drawLine(padLeft, y, w - padRight, y);

                g2.setColor(new Color(148, 163, 184));
                String tag = String.format("S/%dK", (int)(val / 1000));
                g2.drawString(tag, 8, y + 4);
            }

            String[] meses = {"Ene", "Feb", "Mar", "Abr", "May", "Jun"};
            double[] ingresos = {52400, 54800, 58100, 60350, 63800, 59200};
            double[] gastos =   {26100, 26800, 27200, 27450, 27700, 27170};

            int n = meses.length;
            int slotW = graphW / n;
            int barW = Math.max(10, (slotW - 16) / 2);

            Color colorIngreso = new Color(16, 185, 129);  // Verde esmeralda
            Color colorGasto = new Color(249, 115, 22);    // Naranja quemado

            for (int i = 0; i < n; i++) {
                int slotX = padLeft + i * slotW;

                // Barra Ingreso
                int hIng = (int) ((ingresos[i] / maxVal) * graphH);
                int xIng = slotX + (slotW - 2 * barW - 4) / 2;
                int yIng = padTop + graphH - hIng;

                g2.setColor(colorIngreso);
                g2.fillRoundRect(xIng, yIng, barW, hIng, 4, 4);

                // Barra Gasto
                int hGas = (int) ((gastos[i] / maxVal) * graphH);
                int xGas = xIng + barW + 4;
                int yGas = padTop + graphH - hGas;

                g2.setColor(colorGasto);
                g2.fillRoundRect(xGas, yGas, barW, hGas, 4, 4);

                // Etiqueta del mes
                g2.setColor(new Color(100, 116, 139));
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                int txtW = g2.getFontMetrics().stringWidth(meses[i]);
                g2.drawString(meses[i], slotX + (slotW - txtW) / 2, h - 12);
            }

            g2.setColor(new Color(203, 213, 225));
            g2.drawLine(padLeft, padTop + graphH, w - padRight, padTop + graphH);

            g2.dispose();
        }
    }
}
