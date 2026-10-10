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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import happypets.data.RepositorioVeterinaria;
import happypets.model.ReporteClinicoDetalle;
import happypets.ui.Iconos;
import happypets.ui.Ui;

/**
 * Submódulo 8.2: Reportes Clínicos
 * Responsable: Arroyo Preciado, Harry Martin
 * Muestra métricas de consultas médicas, gráficos de prevalencia patológica
 * (Top 5) y carga asistencial por especialista médico, junto a la tabla analítica
 * de diagnósticos confirmados y modal interactivo de ficha clínica completa.
 */
public class VistaReportesClinicosPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private final RepositorioVeterinaria repo = RepositorioVeterinaria.getInstancia();

    // Filtros
    private JTextField txtRangoFechas;
    private JComboBox<String> comboVeterinario;
    private JComboBox<String> comboDiagnostico;
    private JTextField txtBuscarTabla;

    // Componentes de datos
    private JTable tablaReportes;
    private DefaultTableModel modeloReportes;
    private JLabel lblContadorRegistros;
    private List<ReporteClinicoDetalle> reportesActuales;

    private GraficoTopDiagnosticosPanel graficoDiagnosticos;
    private GraficoCargaVeterinariosPanel graficoVeterinarios;

    public VistaReportesClinicosPanel() {
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
        panelCuerpo.add(crearPanelGraficosComparativos());
        panelCuerpo.add(Box.createVerticalStrut(16));
        panelCuerpo.add(crearPanelTablaReportes());

        JScrollPane scrollGeneral = new JScrollPane(panelCuerpo);
        scrollGeneral.setBorder(null);
        scrollGeneral.getVerticalScrollBar().setUnitIncrement(16);
        scrollGeneral.setBackground(new Color(248, 250, 252));
        add(scrollGeneral, BorderLayout.CENTER);

        cargarDatosReportes();
    }

    private JPanel crearCabeceraSuperior() {
        JPanel cab = new JPanel(new BorderLayout(16, 8));
        cab.setOpaque(false);

        // Título y Breadcrumb
        JPanel pnlTit = new JPanel();
        pnlTit.setLayout(new BoxLayout(pnlTit, BoxLayout.Y_AXIS));
        pnlTit.setOpaque(false);

        JLabel lblBreadcrumb = new JLabel("Módulo Analítico / Diagnósticos & Atención");
        lblBreadcrumb.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblBreadcrumb.setForeground(new Color(2, 132, 199));
        pnlTit.add(lblBreadcrumb);
        pnlTit.add(Box.createVerticalStrut(2));

        JLabel lblTitulo = new JLabel("Reportes Clínicos");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(new Color(15, 23, 42));
        pnlTit.add(lblTitulo);
        pnlTit.add(Box.createVerticalStrut(3));

        JLabel lblSub = new JLabel("Historial integral de consultas médicas, prevalencia patológica y carga asistencial.");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(new Color(100, 116, 139));
        pnlTit.add(lblSub);

        cab.add(pnlTit, BorderLayout.WEST);

        // Botones a la derecha
        JPanel pnlBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlBotones.setOpaque(false);

        JButton btnActualizar = Ui.botonSecundario("Actualizar Datos", Iconos.crearIconoRefrescar(13, new Color(15, 23, 42)));
        btnActualizar.setPreferredSize(new Dimension(150, 34));
        btnActualizar.addActionListener(e -> {
            cargarDatosReportes();
            if (graficoDiagnosticos != null) graficoDiagnosticos.repaint();
            if (graficoVeterinarios != null) graficoVeterinarios.repaint();
            JOptionPane.showMessageDialog(this,
                    "Datos clínicos consolidados sincronizados exitosamente.",
                    "Actualización", JOptionPane.INFORMATION_MESSAGE);
        });

        JButton btnDescargarPDF = Ui.botonPrimario("Descargar Resumen PDF", Iconos.crearIconoDescargar(14, Color.WHITE));
        btnDescargarPDF.setPreferredSize(new Dimension(205, 34));
        btnDescargarPDF.addActionListener(e -> generarResumenReportesPDF());

        pnlBotones.add(btnActualizar);
        pnlBotones.add(btnDescargarPDF);

        cab.add(pnlBotones, BorderLayout.EAST);
        return cab;
    }

    private JPanel crearPanelKPIs() {
        JPanel panelKPIs = new JPanel(new GridLayout(1, 4, 14, 0));
        panelKPIs.setOpaque(false);
        panelKPIs.setMaximumSize(new Dimension(Integer.MAX_VALUE, 115));

        // KPI 1: Total Consultas
        panelKPIs.add(crearTarjetaKPI(
                "Total Consultas Registradas",
                "1,482",
                "+12.4% vs mes anterior",
                new Color(16, 185, 129),
                "894 pacientes únicos atendidos",
                new Color(238, 242, 255),
                Iconos.crearIconoDoctor(22, new Color(79, 70, 229))
        ));

        // KPI 2: Pacientes Únicos
        panelKPIs.add(crearTarjetaKPI(
                "Pacientes Únicos Atendidos",
                "894",
                "68% Caninos / 32% Felinos",
                new Color(2, 132, 199),
                "Ratio 1.6 consultas / paciente",
                new Color(240, 249, 255),
                Iconos.crearIconoMascota(22, new Color(2, 132, 199))
        ));

        // KPI 3: Casos con Seguimiento
        panelKPIs.add(crearTarjetaKPI(
                "Casos con Seguimiento Activo",
                "156",
                "42 citas programadas hoy",
                new Color(217, 119, 6),
                "Controles postoperatorios y crónicos",
                new Color(254, 243, 199),
                Iconos.crearIconoCalendario(22, new Color(217, 119, 6))
        ));

        // KPI 4: Tiempo Medio
        panelKPIs.add(crearTarjetaKPI(
                "Tiempo Medio por Consulta",
                "32 min",
                "Objetivo: 30 min",
                new Color(100, 116, 139),
                "Tiempo medio de triaje: 6.2 min",
                new Color(241, 245, 249),
                Iconos.crearIconoTurno(22, new Color(15, 118, 110))
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

        JPanel filaCampos = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
        filaCampos.setOpaque(false);

        // Rango de fechas
        JPanel pnlFec = new JPanel(new BorderLayout(4, 2));
        pnlFec.setOpaque(false);
        JLabel lblF = new JLabel("Rango de Fechas");
        lblF.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblF.setForeground(new Color(100, 116, 139));
        pnlFec.add(lblF, BorderLayout.NORTH);

        txtRangoFechas = new JTextField("01/10/2023 - 31/10/2023", 14);
        txtRangoFechas.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtRangoFechas.setPreferredSize(new Dimension(170, 30));
        pnlFec.add(txtRangoFechas, BorderLayout.CENTER);
        filaCampos.add(pnlFec);

        // Veterinario
        JPanel pnlVet = new JPanel(new BorderLayout(4, 2));
        pnlVet.setOpaque(false);
        JLabel lblV = new JLabel("Veterinario Responsable");
        lblV.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblV.setForeground(new Color(100, 116, 139));
        pnlVet.add(lblV, BorderLayout.NORTH);

        comboVeterinario = new JComboBox<>(new String[]{
                "Todos los Veterinarios",
                "Dra. Elena Ruiz Salazar",
                "Dr. Marcos León Bravo",
                "Dra. Clara Vega Hurtado",
                "Dr. Andrés Pardo Soto",
                "Dra. Sofía Mora Castro"
        });
        comboVeterinario.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        comboVeterinario.setPreferredSize(new Dimension(190, 30));
        comboVeterinario.setBackground(Color.WHITE);
        pnlVet.add(comboVeterinario, BorderLayout.CENTER);
        filaCampos.add(pnlVet);

        // Diagnóstico Primario
        JPanel pnlDiag = new JPanel(new BorderLayout(4, 2));
        pnlDiag.setOpaque(false);
        JLabel lblD = new JLabel("Diagnóstico Primario");
        lblD.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblD.setForeground(new Color(100, 116, 139));
        pnlDiag.add(lblD, BorderLayout.NORTH);

        comboDiagnostico = new JComboBox<>(new String[]{
                "Todos los Diagnósticos",
                "Gastroenteritis",
                "Dermatitis",
                "Otitis",
                "Profilaxis Dental",
                "Traumatismo",
                "Enfermedad Renal"
        });
        comboDiagnostico.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        comboDiagnostico.setPreferredSize(new Dimension(180, 30));
        comboDiagnostico.setBackground(Color.WHITE);
        pnlDiag.add(comboDiagnostico, BorderLayout.CENTER);
        filaCampos.add(pnlDiag);

        card.add(filaCampos, BorderLayout.CENTER);

        // Botones de acción de filtros
        JPanel pnlAcciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        pnlAcciones.setOpaque(false);

        JButton btnAplicar = Ui.botonPrimario("Aplicar Filtros", Iconos.crearIconoRefrescar(12, Color.WHITE));
        btnAplicar.setPreferredSize(new Dimension(135, 32));
        btnAplicar.addActionListener(e -> cargarDatosReportes());

        JButton btnLimpiar = Ui.botonSecundario("Limpiar", null);
        btnLimpiar.setPreferredSize(new Dimension(85, 32));
        btnLimpiar.addActionListener(e -> {
            comboVeterinario.setSelectedIndex(0);
            comboDiagnostico.setSelectedIndex(0);
            txtBuscarTabla.setText("");
            cargarDatosReportes();
        });

        pnlAcciones.add(btnAplicar);
        pnlAcciones.add(btnLimpiar);
        card.add(pnlAcciones, BorderLayout.EAST);

        return card;
    }

    private JPanel crearPanelGraficosComparativos() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;

        // Gráfico 1: Top 5 Diagnósticos Más Comunes
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.5;
        gbc.insets = new Insets(0, 0, 0, 8);

        JPanel cardDiag = new JPanel(new BorderLayout(0, 8));
        cardDiag.setBackground(Color.WHITE);
        cardDiag.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                BorderFactory.createEmptyBorder(14, 16, 14, 16)
        ));

        JPanel titDiag = new JPanel(new BorderLayout());
        titDiag.setOpaque(false);

        JLabel lblT1 = new JLabel("Diagnósticos Más Comunes (Top 5)");
        lblT1.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblT1.setForeground(new Color(15, 23, 42));
        titDiag.add(lblT1, BorderLayout.WEST);

        JLabel lblS1 = new JLabel("Distribución por casuística atendida");
        lblS1.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblS1.setForeground(new Color(100, 116, 139));
        titDiag.add(lblS1, BorderLayout.EAST);
        cardDiag.add(titDiag, BorderLayout.NORTH);

        graficoDiagnosticos = new GraficoTopDiagnosticosPanel();
        graficoDiagnosticos.setPreferredSize(new Dimension(420, 200));
        cardDiag.add(graficoDiagnosticos, BorderLayout.CENTER);

        panel.add(cardDiag, gbc);

        // Gráfico 2: Carga Asistencial por Especialista
        gbc.gridx = 1;
        gbc.weightx = 0.5;
        gbc.insets = new Insets(0, 8, 0, 0);

        JPanel cardVet = new JPanel(new BorderLayout(0, 8));
        cardVet.setBackground(Color.WHITE);
        cardVet.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                BorderFactory.createEmptyBorder(14, 16, 14, 16)
        ));

        JPanel titVet = new JPanel(new BorderLayout());
        titVet.setOpaque(false);

        JLabel lblT2 = new JLabel("Pacientes por Veterinario (Carga Asistencial)");
        lblT2.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblT2.setForeground(new Color(15, 23, 42));
        titVet.add(lblT2, BorderLayout.WEST);

        JLabel lblTotalVet = new JLabel("Total: 1,482 atenciones");
        lblTotalVet.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblTotalVet.setForeground(new Color(2, 132, 199));
        titVet.add(lblTotalVet, BorderLayout.EAST);
        cardVet.add(titVet, BorderLayout.NORTH);

        graficoVeterinarios = new GraficoCargaVeterinariosPanel();
        graficoVeterinarios.setPreferredSize(new Dimension(420, 200));
        cardVet.add(graficoVeterinarios, BorderLayout.CENTER);

        panel.add(cardVet, gbc);
        return panel;
    }

    private JPanel crearPanelTablaReportes() {
        JPanel card = new JPanel(new BorderLayout(0, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                BorderFactory.createEmptyBorder(14, 16, 14, 16)
        ));

        // Cabecera de la tabla
        JPanel cab = new JPanel(new BorderLayout(12, 0));
        cab.setOpaque(false);

        JPanel titIzq = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        titIzq.setOpaque(false);

        JLabel lblTit = new JLabel("Listado Detallado de Reportes Clínicos");
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTit.setForeground(new Color(15, 23, 42));
        titIzq.add(lblTit);

        lblContadorRegistros = new JLabel("  8 casos listados  ");
        lblContadorRegistros.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblContadorRegistros.setOpaque(true);
        lblContadorRegistros.setBackground(new Color(241, 245, 249));
        lblContadorRegistros.setForeground(new Color(71, 85, 105));
        lblContadorRegistros.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));
        titIzq.add(lblContadorRegistros);
        cab.add(titIzq, BorderLayout.WEST);

        JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        der.setOpaque(false);

        txtBuscarTabla = new JTextField(16);
        txtBuscarTabla.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtBuscarTabla.setPreferredSize(new Dimension(210, 30));
        txtBuscarTabla.setToolTipText("Buscar paciente, propietario o diagnóstico...");
        txtBuscarTabla.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { cargarDatosReportes(); }
            @Override public void removeUpdate(DocumentEvent e) { cargarDatosReportes(); }
            @Override public void changedUpdate(DocumentEvent e) { cargarDatosReportes(); }
        });
        der.add(new JLabel(Iconos.crearIconoLupa(14, new Color(100, 116, 139))));
        der.add(txtBuscarTabla);

        cab.add(der, BorderLayout.EAST);
        card.add(cab, BorderLayout.NORTH);

        // Modelo de tabla
        String[] columnas = {"Cód. Reporte", "Fecha", "Paciente", "Propietario / Tutor", "Veterinario Tratante", "Diagnóstico Confirmado", "Severidad", "Acción"};
        modeloReportes = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };

        tablaReportes = new JTable(modeloReportes);
        Ui.formatearTabla(tablaReportes, new int[]{0, 1, 6, 7}, new int[]{});
        tablaReportes.getColumnModel().getColumn(0).setPreferredWidth(100);
        tablaReportes.getColumnModel().getColumn(1).setPreferredWidth(85);
        tablaReportes.getColumnModel().getColumn(2).setPreferredWidth(110);
        tablaReportes.getColumnModel().getColumn(3).setPreferredWidth(150);
        tablaReportes.getColumnModel().getColumn(4).setPreferredWidth(150);
        tablaReportes.getColumnModel().getColumn(5).setPreferredWidth(210);
        tablaReportes.getColumnModel().getColumn(6).setPreferredWidth(95);
        tablaReportes.getColumnModel().getColumn(7).setPreferredWidth(85);

        // Render de Severidad
        tablaReportes.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                l.setHorizontalAlignment(SwingConstants.CENTER);
                String sev = value != null ? value.toString() : "";
                l.setFont(new Font("Segoe UI", Font.BOLD, 11));
                l.setOpaque(true);
                if ("Grave".equalsIgnoreCase(sev)) {
                    l.setBackground(new Color(254, 226, 226));
                    l.setForeground(new Color(220, 38, 38));
                } else if ("Moderada".equalsIgnoreCase(sev)) {
                    l.setBackground(new Color(254, 243, 199));
                    l.setForeground(new Color(180, 83, 9));
                } else {
                    l.setBackground(Ui.TURQUESA_SUAVE);
                    l.setForeground(Ui.TURQUESA_PROFUNDO);
                }
                return l;
            }
        });

        // Render de Acción
        tablaReportes.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(table, "Ver Ficha", isSelected, hasFocus, row, col);
                l.setHorizontalAlignment(SwingConstants.CENTER);
                l.setFont(new Font("Segoe UI", Font.BOLD, 11));
                l.setForeground(Ui.TURQUESA_PROFUNDO);
                l.setCursor(new Cursor(Cursor.HAND_CURSOR));
                return l;
            }
        });

        tablaReportes.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int col = tablaReportes.columnAtPoint(e.getPoint());
                int fila = tablaReportes.rowAtPoint(e.getPoint());
                if (fila >= 0 && col == 7) {
                    abrirFichaClinicaCompleta(fila);
                }
            }
        });

        JScrollPane scroll = new JScrollPane(tablaReportes);
        scroll.setPreferredSize(new Dimension(800, 240));
        scroll.setBorder(BorderFactory.createLineBorder(Ui.BORDE_SUAVE, 1));
        card.add(scroll, BorderLayout.CENTER);

        return card;
    }

    private void cargarDatosReportes() {
        String termino = txtBuscarTabla != null ? txtBuscarTabla.getText().trim() : "";
        String vet = comboVeterinario != null ? (String) comboVeterinario.getSelectedItem() : "";
        String diag = comboDiagnostico != null ? (String) comboDiagnostico.getSelectedItem() : "";

        reportesActuales = repo.buscarReportesClinicos(termino, vet, diag);

        modeloReportes.setRowCount(0);
        for (ReporteClinicoDetalle r : reportesActuales) {
            modeloReportes.addRow(new Object[]{
                    r.getCodigoReporte(),
                    r.getFechaFormateada(),
                    r.getNombrePaciente() + " (" + r.getEspecieRaza() + ")",
                    r.getNombrePropietario(),
                    r.getVeterinarioTratante(),
                    r.getDiagnosticoConfirmado(),
                    r.getSeveridad(),
                    "Ver Ficha"
            });
        }

        if (lblContadorRegistros != null) {
            lblContadorRegistros.setText("  " + reportesActuales.size() + " casos listados  ");
        }
    }

    private void abrirFichaClinicaCompleta(int fila) {
        if (fila < 0 || fila >= reportesActuales.size()) return;
        ReporteClinicoDetalle r = reportesActuales.get(fila);

        JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(this), "Ficha de Reporte Clínico Detallado - " + r.getCodigoReporte(), JDialog.ModalityType.APPLICATION_MODAL);
        dlg.setSize(620, 560);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());

        JPanel pnl = new JPanel();
        pnl.setLayout(new BoxLayout(pnl, BoxLayout.Y_AXIS));
        pnl.setBackground(Color.WHITE);
        pnl.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        // Header del modal
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        JLabel lblCod = new JLabel(r.getCodigoReporte() + " · " + r.getDiagnosticoConfirmado());
        lblCod.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblCod.setForeground(new Color(15, 23, 42));
        top.add(lblCod, BorderLayout.WEST);

        JLabel badgeSev = new JLabel("  Severidad: " + r.getSeveridad() + "  ");
        badgeSev.setFont(new Font("Segoe UI", Font.BOLD, 11));
        badgeSev.setOpaque(true);
        if ("Grave".equalsIgnoreCase(r.getSeveridad())) {
            badgeSev.setBackground(new Color(254, 226, 226));
            badgeSev.setForeground(new Color(220, 38, 38));
        } else {
            badgeSev.setBackground(new Color(254, 243, 199));
            badgeSev.setForeground(new Color(180, 83, 9));
        }
        top.add(badgeSev, BorderLayout.EAST);
        pnl.add(top);
        pnl.add(Box.createVerticalStrut(14));

        // Cuadrícula de datos informativos
        JPanel grid = new JPanel(new GridLayout(4, 2, 12, 10));
        grid.setOpaque(false);
        grid.add(crearItemDetalle("Fecha de Atención:", r.getFechaFormateada()));
        grid.add(crearItemDetalle("Duración de Consulta:", r.getDuracionMinutos() + " minutos"));
        grid.add(crearItemDetalle("Paciente:", r.getNombrePaciente() + " (" + r.getEspecieRaza() + ")"));
        grid.add(crearItemDetalle("Propietario / Tutor:", r.getNombrePropietario() + " (" + r.getTelefonoPropietario() + ")"));
        grid.add(crearItemDetalle("Médico Veterinario:", r.getVeterinarioTratante()));
        grid.add(crearItemDetalle("Seguimiento Activo:", r.isSeguimientoActivo() ? "Sí (Programado)" : "No requerido"));
        pnl.add(grid);
        pnl.add(Box.createVerticalStrut(16));

        // Tratamiento
        JLabel lblTrat = new JLabel("Tratamiento Prescrito y Medicación:");
        lblTrat.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTrat.setForeground(new Color(30, 41, 59));
        lblTrat.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnl.add(lblTrat);
        pnl.add(Box.createVerticalStrut(4));

        JTextArea txtTrat = new JTextArea(r.getTratamiento());
        txtTrat.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtTrat.setLineWrap(true);
        txtTrat.setWrapStyleWord(true);
        txtTrat.setEditable(false);
        txtTrat.setBackground(new Color(248, 250, 252));
        txtTrat.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        pnl.add(txtTrat);
        pnl.add(Box.createVerticalStrut(12));

        // Observaciones
        JLabel lblObs = new JLabel("Observaciones de Evolución y Anamnesis:");
        lblObs.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblObs.setForeground(new Color(30, 41, 59));
        lblObs.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnl.add(lblObs);
        pnl.add(Box.createVerticalStrut(4));

        JTextArea txtObs = new JTextArea(r.getObservaciones());
        txtObs.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtObs.setLineWrap(true);
        txtObs.setWrapStyleWord(true);
        txtObs.setEditable(false);
        txtObs.setBackground(new Color(248, 250, 252));
        txtObs.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        pnl.add(txtObs);

        dlg.add(new JScrollPane(pnl), BorderLayout.CENTER);

        // Barra inferior
        JPanel bot = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        bot.setBackground(new Color(248, 250, 252));
        bot.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(226, 232, 240)));

        JButton btnImprimir = Ui.botonSecundario("Imprimir / Exportar", Iconos.crearIconoDescargar(13, Ui.TEXTO_TITULO));
        btnImprimir.addActionListener(e -> {
            String[][] datosFicha = new String[][]{
                {"1", r.getCodigoReporte(), r.getFechaFormateada(), r.getNombrePaciente(), r.getEspecieRaza(), r.getNombrePropietario(), r.getVeterinarioTratante(), r.getDiagnosticoConfirmado(), r.getSeveridad()}
            };
            Ui.mostrarVisorReporte(
                dlg,
                "FICHA CLÍNICA DETALLADA",
                "Expediente Caso Médico: " + r.getCodigoReporte(),
                "Paciente: " + r.getNombrePaciente() + " | Tutor: " + r.getNombrePropietario(),
                new String[]{"N°", "Código", "Fecha", "Paciente", "Raza", "Propietario", "Médico", "Diagnóstico", "Severidad"},
                datosFicha,
                "Tratamiento sugerido: " + r.getTratamiento(),
                "Ficha_Clinica_" + r.getCodigoReporte()
            );
        });

        JButton btnCerrar = Ui.botonPrimario("Cerrar", null);
        btnCerrar.addActionListener(e -> dlg.dispose());

        bot.add(btnImprimir);
        bot.add(btnCerrar);
        dlg.add(bot, BorderLayout.SOUTH);

        dlg.setVisible(true);
    }

    private void generarResumenReportesPDF() {
        if (reportesActuales == null || reportesActuales.isEmpty()) {
            cargarDatosReportes();
        }
        String[][] datos = new String[reportesActuales.size()][7];
        for (int i = 0; i < reportesActuales.size(); i++) {
            ReporteClinicoDetalle r = reportesActuales.get(i);
            datos[i] = new String[]{
                String.valueOf(i + 1),
                r.getCodigoReporte(),
                r.getFechaFormateada(),
                r.getNombrePaciente() + " (" + r.getEspecieRaza() + ")",
                r.getVeterinarioTratante(),
                r.getDiagnosticoConfirmado(),
                r.getSeveridad()
            };
        }

        String[][] meta = new String[][]{
            {"Módulo", "Reportes Clínicos y Epidemiología"},
            {"Filtro Veterinario", comboVeterinario != null ? comboVeterinario.getSelectedItem().toString() : "Todos"},
            {"Filtro Diagnóstico", comboDiagnostico != null ? comboDiagnostico.getSelectedItem().toString() : "Todos"},
            {"Total Casos", String.valueOf(reportesActuales.size())},
            {"Institución", "Clínica Veterinaria Happy Pets 24H"},
            {"Estado", "Auditoría Médica Conforme"}
        };

        Ui.mostrarVisorReporte(
            (java.awt.Frame) javax.swing.SwingUtilities.getWindowAncestor(this),
            "INFORME EPIDEMIOLÓGICO Y REPORTES CLÍNICOS",
            "Consolidado de Morbilidad y Casuística Asistencial",
            meta,
            new String[]{"N°", "Cód. Caso", "Fecha", "Paciente", "Médico Veterinario", "Diagnóstico", "Severidad"},
            datos,
            "Resumen estadístico de atenciones médicas veterinarias generadas en memoria.",
            "Reporte_Clinico_Consolidado"
        );
    }

    private JPanel crearItemDetalle(String campo, String valor) {
        JPanel p = new JPanel(new BorderLayout(4, 2));
        p.setOpaque(false);
        JLabel c = new JLabel(campo);
        c.setFont(new Font("Segoe UI", Font.BOLD, 11));
        c.setForeground(new Color(100, 116, 139));
        p.add(c, BorderLayout.NORTH);

        JLabel v = new JLabel(valor);
        v.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        v.setForeground(new Color(15, 23, 42));
        p.add(v, BorderLayout.CENTER);
        return p;
    }

    /**
     * Gráfico nativo Graphics2D: Top 5 Diagnósticos Más Comunes
     */
    private static class GraficoTopDiagnosticosPanel extends JPanel {
        private static final long serialVersionUID = 1L;

        GraficoTopDiagnosticosPanel() {
            setBackground(Color.WHITE);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            String[] diagNombres = {
                    "Gastroenteritis Aguda",
                    "Dermatitis Alérgica",
                    "Otitis Externa",
                    "Profilaxis Dental",
                    "Traumatismo / Heridas"
            };
            int[] valores = {330, 290, 200, 170, 110};
            int maxVal = 350;

            int w = getWidth();
            int h = getHeight();
            int topPad = 15;
            int botPad = 15;
            int totalH = h - topPad - botPad;
            int n = diagNombres.length;
            int rowH = totalH / n;

            int labelW = 140;
            int rightPad = 60;
            int barMaxW = w - labelW - rightPad - 20;

            for (int i = 0; i < n; i++) {
                int y = topPad + i * rowH;

                // Etiqueta
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                g2.setColor(new Color(30, 41, 59));
                g2.drawString(diagNombres[i], 10, y + rowH / 2 + 4);

                // Barra
                int barW = (int) (((double) valores[i] / maxVal) * barMaxW);
                int barY = y + (rowH - 14) / 2;

                // Fondo barra
                g2.setColor(new Color(241, 245, 249));
                g2.fillRoundRect(labelW, barY, barMaxW, 14, 6, 6);

                // Barra progreso
                g2.setColor(new Color(2, 132, 199)); // Azul cielo vibrante
                g2.fillRoundRect(labelW, barY, Math.max(10, barW), 14, 6, 6);

                // Valor numérico y porcentaje
                g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
                g2.setColor(new Color(15, 23, 42));
                String cantStr = valores[i] + " (" + String.format("%.1f%%", (valores[i] * 100.0 / 1100.0)) + ")";
                g2.drawString(cantStr, labelW + barMaxW + 8, y + rowH / 2 + 4);
            }

            g2.dispose();
        }
    }

    /**
     * Gráfico nativo Graphics2D: Carga Asistencial por Especialista Médico
     */
    private static class GraficoCargaVeterinariosPanel extends JPanel {
        private static final long serialVersionUID = 1L;

        GraficoCargaVeterinariosPanel() {
            setBackground(Color.WHITE);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            String[] vets = {
                    "Dra. Elena Ruiz",
                    "Dr. Marcos León",
                    "Dra. Clara Vega",
                    "Dr. Andrés Pardo",
                    "Dra. Sofía Mora"
            };
            int[] valores = {410, 380, 310, 220, 150};
            int maxVal = 450;
            Color[] colores = {
                    new Color(15, 76, 129),
                    new Color(13, 148, 136),
                    new Color(99, 102, 241),
                    new Color(217, 119, 6),
                    new Color(147, 51, 234)
            };

            int w = getWidth();
            int h = getHeight();
            int topPad = 15;
            int botPad = 15;
            int totalH = h - topPad - botPad;
            int n = vets.length;
            int rowH = totalH / n;

            int labelW = 120;
            int rightPad = 60;
            int barMaxW = w - labelW - rightPad - 20;

            for (int i = 0; i < n; i++) {
                int y = topPad + i * rowH;

                // Etiqueta
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                g2.setColor(new Color(30, 41, 59));
                g2.drawString(vets[i], 10, y + rowH / 2 + 4);

                // Barra
                int barW = (int) (((double) valores[i] / maxVal) * barMaxW);
                int barY = y + (rowH - 14) / 2;

                // Fondo barra
                g2.setColor(new Color(241, 245, 249));
                g2.fillRoundRect(labelW, barY, barMaxW, 14, 6, 6);

                // Barra progreso
                g2.setColor(colores[i % colores.length]);
                g2.fillRoundRect(labelW, barY, Math.max(10, barW), 14, 6, 6);

                // Valor numérico
                g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
                g2.setColor(new Color(15, 23, 42));
                String cantStr = valores[i] + " cons.";
                g2.drawString(cantStr, labelW + barMaxW + 8, y + rowH / 2 + 4);
            }

            g2.dispose();
        }
    }
}
