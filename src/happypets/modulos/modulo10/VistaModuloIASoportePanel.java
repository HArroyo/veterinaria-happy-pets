package happypets.modulos.modulo10;

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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import happypets.data.RepositorioVeterinaria;
import happypets.model.ConfiguracionModuloIA;
import happypets.model.DiagnosticoSistema;
import happypets.model.TicketSoporte;
import happypets.ui.Iconos;
import happypets.ui.Ui;

/**
 * Submódulo 10.4: Módulo de IA y Soporte Técnico
 * Responsable: Minaya Bravo, Almendra Lili
 * Asistentes inteligentes para pre-triaje clínico, sugerencias de diagnóstico, recordatorios predictivos
 * y monitoreo de salud del sistema con arquitectura Master-Detail, 4 KPIs y hub de telemetría de IA.
 */
public class VistaModuloIASoportePanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private final RepositorioVeterinaria repo = RepositorioVeterinaria.getInstancia();

    // KPIs Superiores
    private JLabel lblKpiMotorIA;
    private JLabel lblKpiTriajesMes;
    private JLabel lblKpiSaludBD;
    private JLabel lblKpiTickets;

    // Consola IA (Master)
    private JComboBox<String> cbEspecie;
    private JTextField txtRaza;
    private JTextField txtEdad;
    private JTextField txtSintomas;
    private JTextArea txtResultadoIA;

    // Switches IA
    private JCheckBox chkPreTriaje;
    private JCheckBox chkDiagnostico;
    private JCheckBox chkVacunas;

    // Tabla de Tickets
    private JTable tablaTickets;
    private DefaultTableModel modeloTickets;

    // Live Detail Hub (Derecha - 360px)
    private JTextArea txtHubPromptPreview;
    private JLabel lblHubLatencia;
    private JLabel lblHubModelo;

    public VistaModuloIASoportePanel() {
        setLayout(new BorderLayout());
        setOpaque(false);

        // Panel Superior Fijo (Cabecera + 4 KPIs)
        JPanel panelSuperior = new JPanel();
        panelSuperior.setOpaque(false);
        panelSuperior.setLayout(new BoxLayout(panelSuperior, BoxLayout.Y_AXIS));
        panelSuperior.setBorder(new EmptyBorder(12, 18, 10, 18));

        panelSuperior.add(crearCabeceraVista());
        panelSuperior.add(Box.createVerticalStrut(10));
        panelSuperior.add(crearFilaKpis());

        add(panelSuperior, BorderLayout.NORTH);

        // Contenedor Master-Detail
        JPanel panelCentro = new JPanel(new BorderLayout());
        panelCentro.setOpaque(false);
        panelCentro.setBorder(new EmptyBorder(0, 18, 14, 18));
        panelCentro.add(crearSplitMasterDetail(), BorderLayout.CENTER);

        add(panelCentro, BorderLayout.CENTER);

        cargarDatos();
    }

    private JPanel crearCabeceraVista() {
        JPanel cab = new JPanel(new BorderLayout());
        cab.setOpaque(false);
        cab.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

        JPanel izq = new JPanel();
        izq.setOpaque(false);
        izq.setLayout(new BoxLayout(izq, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("Módulo de IA y Soporte Técnico");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 17));
        titulo.setForeground(Ui.TEXTO_TITULO);

        JLabel subtitulo = new JLabel("Módulo 10.4 · Asistentes predictivos, triaje inteligente HappyPet-Core-v1.8 y monitoreo de salud del sistema");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitulo.setForeground(Ui.TEXTO_MUTED);

        izq.add(titulo);
        izq.add(Box.createVerticalStrut(2));
        izq.add(subtitulo);
        cab.add(izq, BorderLayout.WEST);

        JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        der.setOpaque(false);

        JButton btnPrompts = Ui.boton("Ajustes de Prompts", false);
        btnPrompts.addActionListener(e -> abrirAjustesPrompts());
        der.add(btnPrompts);

        JButton btnNuevoTicket = Ui.boton("+ Nuevo Ticket", true);
        btnNuevoTicket.addActionListener(e -> abrirDialogoNuevoTicket());
        der.add(btnNuevoTicket);

        cab.add(der, BorderLayout.EAST);
        return cab;
    }

    private JPanel crearFilaKpis() {
        JPanel kpis = new JPanel(new GridLayout(1, 4, 12, 0));
        kpis.setOpaque(false);
        kpis.setPreferredSize(new Dimension(0, 74));
        kpis.setMaximumSize(new Dimension(Integer.MAX_VALUE, 74));

        lblKpiMotorIA = new JLabel("HappyPet-Core-v1.8");
        lblKpiTriajesMes = new JLabel("312 Casos");
        lblKpiSaludBD = new JLabel("100% Saludable");
        lblKpiTickets = new JLabel("0 Pendientes");

        kpis.add(crearTarjetaKpi("Motor IA Activo", lblKpiMotorIA, "Inferencia clínica de triaje", Iconos.crearIconoRobot(22, Ui.TURQUESA)));
        kpis.add(crearTarjetaKpi("Triajes Procesados", lblKpiTriajesMes, "Tiempo respuesta < 1.2s", Iconos.crearIconoEstetoscopio(22, Ui.AZUL_INFO)));
        kpis.add(crearTarjetaKpi("Estado de Base Datos", lblKpiSaludBD, "Integridad transaccional OK", Iconos.crearIconoCheck(22, Ui.VERDE_EXITO)));
        kpis.add(crearTarjetaKpi("Tickets de Soporte", lblKpiTickets, "2 resueltos con éxito", Iconos.crearIconoAlertaTriaje(22, Ui.NARANJA_AVISO)));

        return kpis;
    }

    private JPanel crearTarjetaKpi(String titulo, JLabel lblValor, String subtitulo, Icon icono) {
        JPanel card = new JPanel(new BorderLayout(8, 0)) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(Ui.BORDE_SUAVE);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(8, 12, 8, 12));

        JPanel texto = new JPanel();
        texto.setOpaque(false);
        texto.setLayout(new BoxLayout(texto, BoxLayout.Y_AXIS));

        JLabel lblTit = new JLabel(titulo);
        lblTit.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblTit.setForeground(Ui.TEXTO_MUTED);

        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblValor.setForeground(Ui.TEXTO_TITULO);

        JLabel lblSub = new JLabel(subtitulo);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblSub.setForeground(Ui.TEXTO_MUTED);

        texto.add(lblTit);
        texto.add(Box.createVerticalStrut(1));
        texto.add(lblValor);
        texto.add(Box.createVerticalStrut(1));
        texto.add(lblSub);

        card.add(texto, BorderLayout.CENTER);

        if (icono != null) {
            JLabel lblIco = new JLabel(icono);
            lblIco.setVerticalAlignment(SwingConstants.TOP);
            lblIco.setBorder(new EmptyBorder(2, 0, 0, 0));
            card.add(lblIco, BorderLayout.EAST);
        }

        return card;
    }

    private JSplitPane crearSplitMasterDetail() {
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setOpaque(false);
        split.setBorder(null);
        split.setDividerSize(8);
        split.setContinuousLayout(true);
        split.setResizeWeight(1.0);

        split.setLeftComponent(crearPanelMasterIA());
        split.setRightComponent(crearPanelLiveDetailHub());

        return split;
    }

    private JPanel crearPanelMasterIA() {
        JPanel master = new JPanel(new BorderLayout(0, 10)) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(Ui.BORDE_SUAVE);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                g2.dispose();
            }
        };
        master.setOpaque(false);
        master.setBorder(new EmptyBorder(14, 16, 14, 16));

        ScrollablePanel scrollContent = new ScrollablePanel();
        scrollContent.setOpaque(false);
        scrollContent.setLayout(new BoxLayout(scrollContent, BoxLayout.Y_AXIS));

        // 1. Consola Interactiva de Triaje Clínico
        JPanel cardConsola = new JPanel(new BorderLayout(0, 10));
        cardConsola.setOpaque(false);
        cardConsola.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(241, 245, 249), 1, true),
                new EmptyBorder(12, 14, 12, 14)
        ));
        cardConsola.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblTitConsola = new JLabel("Consola Interactiva de Triaje Clínico con IA:");
        lblTitConsola.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTitConsola.setForeground(Ui.TEXTO_TITULO);
        cardConsola.add(lblTitConsola, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 4, 4, 4);
        g.fill = GridBagConstraints.HORIZONTAL;

        cbEspecie = Ui.combo(new String[]{"Canino", "Felino", "Conejo", "Ave Exótica"});
        txtRaza = new JTextField("Golden Retriever", 14);
        txtRaza.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtEdad = new JTextField("48", 6);
        txtEdad.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtSintomas = new JTextField("Tos seca nocturna recurrente, arcadas post ejercicio, letargo moderado.", 30);
        txtSintomas.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        g.gridx = 0; g.gridy = 0; g.weightx = 0.15;
        form.add(crearLabelForm("Especie:"), g);
        g.gridx = 1; g.gridy = 0; g.weightx = 0.35;
        form.add(cbEspecie, g);

        g.gridx = 2; g.gridy = 0; g.weightx = 0.15;
        form.add(crearLabelForm("Raza:"), g);
        g.gridx = 3; g.gridy = 0; g.weightx = 0.35;
        form.add(txtRaza, g);

        g.gridx = 0; g.gridy = 1; g.weightx = 0.15;
        form.add(crearLabelForm("Edad (meses):"), g);
        g.gridx = 1; g.gridy = 1; g.weightx = 0.35;
        form.add(txtEdad, g);

        g.gridx = 2; g.gridy = 1; g.weightx = 0.15;
        form.add(crearLabelForm("Signos Clínicos:"), g);
        g.gridx = 3; g.gridy = 1; g.weightx = 0.35;
        form.add(txtSintomas, g);

        txtResultadoIA = new JTextArea(5, 40);
        txtResultadoIA.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtResultadoIA.setBackground(new Color(248, 250, 252));
        txtResultadoIA.setForeground(Ui.TEXTO_TITULO);
        txtResultadoIA.setEditable(false);
        txtResultadoIA.setLineWrap(true);
        txtResultadoIA.setWrapStyleWord(true);
        txtResultadoIA.setText("Haga clic en 'Ejecutar Diagnóstico IA' para procesar el cuadro clínico con HappyPet-Core-v1.8.");
        txtResultadoIA.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Ui.BORDE_SUAVE, 1),
                new EmptyBorder(8, 10, 8, 10)
        ));

        JPanel pnlBotonesConsola = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        pnlBotonesConsola.setOpaque(false);

        JButton btnExportarFicha = Ui.botonSecundario("Exportar Ficha IA", null);
        btnExportarFicha.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnExportarFicha.addActionListener(e -> exportarFichaDiagnostica());

        JButton btnEjecutar = Ui.botonPrimario("⚡ Ejecutar Diagnóstico IA", null);
        btnEjecutar.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnEjecutar.addActionListener(e -> ejecutarDiagnosticoIA());

        pnlBotonesConsola.add(btnExportarFicha);
        pnlBotonesConsola.add(btnEjecutar);

        JPanel pnlCentroConsola = new JPanel(new BorderLayout(0, 8));
        pnlCentroConsola.setOpaque(false);
        pnlCentroConsola.add(form, BorderLayout.NORTH);
        pnlCentroConsola.add(new JScrollPane(txtResultadoIA), BorderLayout.CENTER);
        pnlCentroConsola.add(pnlBotonesConsola, BorderLayout.SOUTH);

        cardConsola.add(pnlCentroConsola, BorderLayout.CENTER);
        scrollContent.add(cardConsola);
        scrollContent.add(Box.createVerticalStrut(12));

        // 2. Switches de Configuración de Asistentes IA
        JPanel cardSwitches = new JPanel(new BorderLayout(0, 8));
        cardSwitches.setOpaque(false);
        cardSwitches.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(241, 245, 249), 1, true),
                new EmptyBorder(10, 14, 10, 14)
        ));
        cardSwitches.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblTitSwitches = new JLabel("Asistentes Inteligentes Configurados:");
        lblTitSwitches.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTitSwitches.setForeground(Ui.TEXTO_TITULO);
        cardSwitches.add(lblTitSwitches, BorderLayout.NORTH);

        JPanel pnlListSwitches = new JPanel(new GridLayout(3, 1, 0, 6));
        pnlListSwitches.setOpaque(false);

        chkPreTriaje = new JCheckBox("Pre-Triaje de Pacientes con IA — Categoriza el nivel de urgencia al registrar ingreso", true);
        chkPreTriaje.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        chkPreTriaje.setOpaque(false);
        chkPreTriaje.addActionListener(e -> guardarConfiguracionIA());

        chkDiagnostico = new JCheckBox("Asistente Diagnóstico Preliminar — Sugiere correlación patológica por síntomas clínicos", true);
        chkDiagnostico.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        chkDiagnostico.setOpaque(false);
        chkDiagnostico.addActionListener(e -> guardarConfiguracionIA());

        chkVacunas = new JCheckBox("Recordatorios Predictivos de Vacunas — Calcula fechas óptimas según raza, edad y perfil", true);
        chkVacunas.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        chkVacunas.setOpaque(false);
        chkVacunas.addActionListener(e -> guardarConfiguracionIA());

        pnlListSwitches.add(chkPreTriaje);
        pnlListSwitches.add(chkDiagnostico);
        pnlListSwitches.add(chkVacunas);

        cardSwitches.add(pnlListSwitches, BorderLayout.CENTER);
        scrollContent.add(cardSwitches);
        scrollContent.add(Box.createVerticalStrut(12));

        // 3. Tabla de Tickets de Soporte
        JPanel cardTickets = new JPanel(new BorderLayout(0, 8));
        cardTickets.setOpaque(false);
        cardTickets.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(241, 245, 249), 1, true),
                new EmptyBorder(10, 14, 10, 14)
        ));
        cardTickets.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblTitTickets = new JLabel("Tickets de Soporte Técnico y Diagnóstico del Sistema:");
        lblTitTickets.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTitTickets.setForeground(Ui.TEXTO_TITULO);
        cardTickets.add(lblTitTickets, BorderLayout.NORTH);

        String[] cols = {"Cód. Ticket", "Asunto del Incidente", "Categoría", "Prioridad", "Fecha", "Estado"};
        modeloTickets = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tablaTickets = new JTable(modeloTickets);
        tablaTickets.setRowHeight(36);
        tablaTickets.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaTickets.setShowVerticalLines(false);
        tablaTickets.setGridColor(new Color(241, 245, 249));
        tablaTickets.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        tablaTickets.getTableHeader().setBackground(new Color(248, 250, 252));

        tablaTickets.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 7));
                p.setOpaque(true);
                p.setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);

                String st = value != null ? value.toString() : "Cerrado";
                JLabel badge = new JLabel(" " + st + " ");
                badge.setOpaque(true);
                badge.setFont(new Font("Segoe UI", Font.BOLD, 10));

                if (st.equalsIgnoreCase("Cerrado") || st.equalsIgnoreCase("Resuelto")) {
                    badge.setBackground(new Color(220, 252, 231));
                    badge.setForeground(new Color(21, 128, 61));
                } else {
                    badge.setBackground(new Color(254, 243, 199));
                    badge.setForeground(new Color(180, 83, 9));
                }
                badge.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(badge.getBackground().darker(), 1, true),
                        new EmptyBorder(2, 6, 2, 6)
                ));
                p.add(badge);
                return p;
            }
        });

        JScrollPane scTickets = new JScrollPane(tablaTickets);
        scTickets.setPreferredSize(new Dimension(0, 110));
        scTickets.setBorder(BorderFactory.createLineBorder(Ui.BORDE_SUAVE, 1));
        cardTickets.add(scTickets, BorderLayout.CENTER);

        scrollContent.add(cardTickets);

        JScrollPane scroll = new JScrollPane(scrollContent);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(14);

        master.add(scroll, BorderLayout.CENTER);
        return master;
    }

    private JPanel crearPanelLiveDetailHub() {
        JPanel detail = new JPanel(new BorderLayout()) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(Ui.BORDE_SUAVE);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                g2.dispose();
            }
        };
        detail.setOpaque(false);
        detail.setPreferredSize(new Dimension(360, 0));
        detail.setMinimumSize(new Dimension(320, 0));

        ScrollablePanel scrollContent = new ScrollablePanel();
        scrollContent.setOpaque(false);
        scrollContent.setLayout(new BoxLayout(scrollContent, BoxLayout.Y_AXIS));
        scrollContent.setBorder(new EmptyBorder(14, 16, 14, 16));

        // 1. Hero Block
        JPanel hero = new JPanel(new BorderLayout(8, 0));
        hero.setOpaque(false);
        hero.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel avatar = new JLabel(Iconos.crearIconoRobot(26, Ui.TURQUESA_PROFUNDO), SwingConstants.CENTER) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int s = Math.min(getWidth(), getHeight()) - 2;
                int x = (getWidth() - s) / 2;
                int y = (getHeight() - s) / 2;
                g2.setColor(Ui.TURQUESA_SUAVE);
                g2.fillOval(x, y, s, s);
                g2.setColor(Ui.TURQUESA);
                g2.drawOval(x, y, s, s);
                super.paintComponent(g);
                g2.dispose();
            }
        };
        avatar.setPreferredSize(new Dimension(46, 46));

        JPanel avatarBox = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        avatarBox.setOpaque(false);
        avatarBox.setPreferredSize(new Dimension(46, 46));
        avatarBox.add(avatar);
        hero.add(avatarBox, BorderLayout.WEST);

        JPanel heroText = new JPanel();
        heroText.setOpaque(false);
        heroText.setLayout(new BoxLayout(heroText, BoxLayout.Y_AXIS));

        lblHubModelo = new JLabel("HappyPet-Core-v1.8");
        lblHubModelo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblHubModelo.setForeground(Ui.TEXTO_TITULO);
        lblHubModelo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblSub = new JLabel("Motor de Inferencia Clínica");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblSub.setForeground(Ui.TEXTO_MUTED);
        lblSub.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel rowChips = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        rowChips.setOpaque(false);
        rowChips.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel chipEstado = new JLabel(" Asistente Operativo ");
        chipEstado.setOpaque(true);
        chipEstado.setFont(new Font("Segoe UI", Font.BOLD, 10));
        chipEstado.setBackground(new Color(220, 252, 231));
        chipEstado.setForeground(new Color(21, 128, 61));
        chipEstado.setBorder(BorderFactory.createLineBorder(new Color(134, 239, 172), 1, true));
        rowChips.add(chipEstado);

        heroText.add(lblHubModelo);
        heroText.add(Box.createVerticalStrut(2));
        heroText.add(lblSub);
        heroText.add(Box.createVerticalStrut(4));
        heroText.add(rowChips);

        hero.add(heroText, BorderLayout.CENTER);
        scrollContent.add(hero);
        scrollContent.add(Box.createVerticalStrut(14));

        // 2. Telemetría 2x2
        JPanel telemetria = new JPanel(new GridLayout(2, 2, 8, 8));
        telemetria.setOpaque(false);
        telemetria.setAlignmentX(Component.LEFT_ALIGNMENT);
        telemetria.setMaximumSize(new Dimension(Integer.MAX_VALUE, 92));

        lblHubLatencia = new JLabel("34 ms (Óptimo)");
        telemetria.add(crearMicroCard("Latencia Inferencia", lblHubLatencia));
        telemetria.add(crearMicroCard("Precisión Diagnóstica", new JLabel("98.5% Validación")));
        telemetria.add(crearMicroCard("Copia de Seguridad", new JLabel("Hoy, 03:00 AM")));
        telemetria.add(crearMicroCard("Motor de Base Datos", new JLabel("PostgreSQL ACID")));

        scrollContent.add(telemetria);
        scrollContent.add(Box.createVerticalStrut(12));

        // 3. Card Prompt Base y Directivas
        txtHubPromptPreview = new JTextArea();
        txtHubPromptPreview.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        txtHubPromptPreview.setForeground(Ui.TEXTO_TITULO);
        txtHubPromptPreview.setLineWrap(true);
        txtHubPromptPreview.setWrapStyleWord(true);
        txtHubPromptPreview.setEditable(false);
        txtHubPromptPreview.setOpaque(false);

        JPanel pnlPrompt = crearSeccionCard("Directivas del Asistente (System Prompt)", txtHubPromptPreview);
        pnlPrompt.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollContent.add(pnlPrompt);
        scrollContent.add(Box.createVerticalStrut(14));

        // 4. Botones 2x2
        JPanel gridAcciones = new JPanel(new GridLayout(2, 2, 8, 8));
        gridAcciones.setOpaque(false);
        gridAcciones.setAlignmentX(Component.LEFT_ALIGNMENT);
        gridAcciones.setMaximumSize(new Dimension(Integer.MAX_VALUE, 74));

        JButton btnTestIA = Ui.botonSecundario("Test Inferencia", Iconos.crearIconoRobot(12, Ui.TEXTO_TITULO));
        btnTestIA.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnTestIA.addActionListener(e -> ejecutarDiagnosticoIA());

        JButton btnExportar = Ui.botonSecundario("Ficha A4 PDF", Iconos.crearIconoDescargar(12, Ui.TEXTO_TITULO));
        btnExportar.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnExportar.addActionListener(e -> exportarFichaDiagnostica());

        JButton btnAjustarPrompt = Ui.botonSecundario("Ajustar Prompts", null);
        btnAjustarPrompt.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnAjustarPrompt.addActionListener(e -> abrirAjustesPrompts());

        JButton btnCrearTicket = Ui.botonPrimario("+ Ticket", null);
        btnCrearTicket.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnCrearTicket.addActionListener(e -> abrirDialogoNuevoTicket());

        gridAcciones.add(btnTestIA);
        gridAcciones.add(btnExportar);
        gridAcciones.add(btnAjustarPrompt);
        gridAcciones.add(btnCrearTicket);

        scrollContent.add(gridAcciones);

        JScrollPane scroll = new JScrollPane(scrollContent);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(14);

        detail.add(scroll, BorderLayout.CENTER);
        return detail;
    }

    private JPanel crearMicroCard(String etiqueta, JLabel lblValor) {
        JPanel mc = new JPanel();
        mc.setOpaque(false);
        mc.setLayout(new BoxLayout(mc, BoxLayout.Y_AXIS));
        mc.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(241, 245, 249), 1, true),
                new EmptyBorder(6, 8, 6, 8)
        ));

        JLabel lblEtiq = new JLabel(etiqueta);
        lblEtiq.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblEtiq.setForeground(Ui.TEXTO_MUTED);

        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblValor.setForeground(Ui.TEXTO_TITULO);

        mc.add(lblEtiq);
        mc.add(Box.createVerticalStrut(2));
        mc.add(lblValor);

        return mc;
    }

    private JPanel crearSeccionCard(String titulo, JComponent contenido) {
        JPanel sec = new JPanel(new BorderLayout(0, 6));
        sec.setOpaque(false);
        sec.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(241, 245, 249), 1, true),
                new EmptyBorder(8, 10, 8, 10)
        ));

        JLabel lblTit = new JLabel(titulo);
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblTit.setForeground(Ui.TEXTO_MUTED);
        sec.add(lblTit, BorderLayout.NORTH);

        sec.add(contenido, BorderLayout.CENTER);
        return sec;
    }

    private JLabel crearLabelForm(String txt) {
        JLabel l = new JLabel(txt);
        l.setFont(new Font("Segoe UI", Font.BOLD, 11));
        l.setForeground(Ui.TEXTO_MUTED);
        return l;
    }

    private void cargarDatos() {
        ConfiguracionModuloIA cfg = repo.getConfiguracionModuloIA();
        if (cfg != null) {
            chkPreTriaje.setSelected(cfg.isPreTriajeMascotas());
            chkDiagnostico.setSelected(cfg.isAsistenteDiagnosticoPreliminar());
            chkVacunas.setSelected(cfg.isRecordatoriosPredictivosVacunas());
            lblHubModelo.setText(cfg.getModeloActual());
            lblKpiMotorIA.setText(cfg.getModeloActual());
            txtHubPromptPreview.setText(cfg.getPromptSistema());
        }

        // Cargar Tickets
        modeloTickets.setRowCount(0);
        List<TicketSoporte> tickets = repo.getTicketsSoporte();
        for (TicketSoporte t : tickets) {
            modeloTickets.addRow(new Object[]{
                    t.getIdTicket(),
                    t.getAsunto(),
                    t.getCategoria(),
                    t.getPrioridad(),
                    t.getFechaCreacionFormateada(),
                    t.getEstado()
            });
        }
        lblKpiTickets.setText("0 Pendientes");
    }

    private void guardarConfiguracionIA() {
        ConfiguracionModuloIA cfg = repo.getConfiguracionModuloIA();
        if (cfg != null) {
            cfg.setPreTriajeMascotas(chkPreTriaje.isSelected());
            cfg.setAsistenteDiagnosticoPreliminar(chkDiagnostico.isSelected());
            cfg.setRecordatoriosPredictivosVacunas(chkVacunas.isSelected());
        }
    }

    private void ejecutarDiagnosticoIA() {
        int edadM = 24;
        try {
            edadM = Integer.parseInt(txtEdad.getText().trim());
        } catch (Exception ignored) { }

        String res = repo.simularDiagnosticoTriajeIA(
                (String) cbEspecie.getSelectedItem(),
                txtRaza.getText().trim(),
                edadM,
                "Consulta por síntomas clínicos",
                txtSintomas.getText().trim()
        );
        txtResultadoIA.setText(res);
        txtResultadoIA.setCaretPosition(0);
    }

    private void abrirAjustesPrompts() {
        ConfiguracionModuloIA cfg = repo.getConfiguracionModuloIA();
        JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(this), "Ajustes de Prompts y Directivas IA", JDialog.ModalityType.APPLICATION_MODAL);
        dlg.setSize(520, 380);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());

        JPanel pnl = new JPanel(new BorderLayout(8, 8));
        pnl.setBackground(Color.WHITE);
        pnl.setBorder(new EmptyBorder(16, 20, 16, 20));

        JLabel lblTit = new JLabel("Directiva Maestra (System Prompt):");
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTit.setForeground(Ui.TEXTO_TITULO);
        pnl.add(lblTit, BorderLayout.NORTH);

        JTextArea txtP = new JTextArea(cfg != null ? cfg.getPromptSistema() : "");
        txtP.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtP.setLineWrap(true);
        txtP.setWrapStyleWord(true);
        pnl.add(new JScrollPane(txtP), BorderLayout.CENTER);

        dlg.add(pnl, BorderLayout.CENTER);

        JPanel bot = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        bot.setBackground(new Color(248, 250, 252));
        bot.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Ui.BORDE_SUAVE));

        JButton btnCancelar = Ui.botonSecundario("Cancelar", null);
        btnCancelar.addActionListener(e -> dlg.dispose());

        JButton btnGuardar = Ui.botonPrimario("Guardar Prompt", null);
        btnGuardar.addActionListener(e -> {
            if (cfg != null) {
                cfg.setPromptSistema(txtP.getText().trim());
                txtHubPromptPreview.setText(cfg.getPromptSistema());
            }
            dlg.dispose();
            JOptionPane.showMessageDialog(this, "Directivas de IA actualizadas satisfactoriamente.", "Configuración Guardada", JOptionPane.INFORMATION_MESSAGE);
        });

        bot.add(btnCancelar);
        bot.add(btnGuardar);
        dlg.add(bot, BorderLayout.SOUTH);

        dlg.setVisible(true);
    }

    private void abrirDialogoNuevoTicket() {
        JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(this), "Crear Ticket de Soporte Técnico", JDialog.ModalityType.APPLICATION_MODAL);
        dlg.setSize(480, 400);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());

        JPanel pnl = new JPanel();
        pnl.setLayout(new BoxLayout(pnl, BoxLayout.Y_AXIS));
        pnl.setBackground(Color.WHITE);
        pnl.setBorder(new EmptyBorder(16, 20, 16, 20));

        JTextField txtAsunto = new JTextField();
        JComboBox<String> cmbCat = Ui.combo(new String[]{"Integración", "Facturación", "Base de Datos", "Hardware / Red", "Módulo IA"});
        JComboBox<String> cmbPrioridad = Ui.combo(new String[]{"Alta", "Media", "Baja"});
        JTextArea txtDesc = new JTextArea(4, 20);
        txtDesc.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtDesc.setLineWrap(true);
        txtDesc.setWrapStyleWord(true);

        pnl.add(crearFilaCampo("Asunto del Ticket:", txtAsunto));
        pnl.add(Box.createVerticalStrut(6));
        pnl.add(crearFilaCampo("Categoría:", cmbCat));
        pnl.add(Box.createVerticalStrut(6));
        pnl.add(crearFilaCampo("Prioridad:", cmbPrioridad));
        pnl.add(Box.createVerticalStrut(6));

        JLabel lDesc = new JLabel("Descripción Detallada del Incidente:");
        lDesc.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lDesc.setForeground(Ui.TEXTO_MUTED);
        pnl.add(lDesc);
        pnl.add(Box.createVerticalStrut(4));
        pnl.add(new JScrollPane(txtDesc));

        dlg.add(pnl, BorderLayout.CENTER);

        JPanel bot = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        bot.setBackground(new Color(248, 250, 252));
        bot.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Ui.BORDE_SUAVE));

        JButton btnCancelar = Ui.botonSecundario("Cancelar", null);
        btnCancelar.addActionListener(e -> dlg.dispose());

        JButton btnGuardar = Ui.botonPrimario("Registrar Ticket", null);
        btnGuardar.addActionListener(e -> {
            String as = txtAsunto.getText().trim();
            if (as.isEmpty()) {
                JOptionPane.showMessageDialog(dlg, "El asunto es requerido.", "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String id = "#TCK-" + (500 + repo.getTicketsSoporte().size() + 1);
            TicketSoporte nuevo = new TicketSoporte(
                    id, as, (String) cmbCat.getSelectedItem(), (String) cmbPrioridad.getSelectedItem(),
                    "Abierto", "admin@happypets.com", LocalDateTime.now(), txtDesc.getText().trim(), "En proceso de revisión técnica"
            );
            repo.getTicketsSoporte().add(0, nuevo);
            dlg.dispose();
            cargarDatos();
            JOptionPane.showMessageDialog(this, "Ticket de soporte técnico registrado (" + id + ").", "Ticket Creado", JOptionPane.INFORMATION_MESSAGE);
        });

        bot.add(btnCancelar);
        bot.add(btnGuardar);
        dlg.add(bot, BorderLayout.SOUTH);

        dlg.setVisible(true);
    }

    private void exportarFichaDiagnostica() {
        String esp = (String) cbEspecie.getSelectedItem();
        String raz = txtRaza.getText().trim();
        String edad = txtEdad.getText().trim();
        String sint = txtSintomas.getText().trim();
        String res = txtResultadoIA.getText().trim();

        String[][] meta = new String[][]{
            {"Paciente Evaluado", esp + " - " + raz},
            {"Edad Cronológica", edad + " meses"},
            {"Modelo de Inferencia", "HappyPet-Core-v1.8"},
            {"Fecha de Evaluación", LocalDate.now().toString()},
            {"Tipo de Documento", "Pre-Triaje y Orientación Diagnóstica Asistida por IA"}
        };
        String[][] datos = new String[][]{
            {"1", "Especie Animal", esp, "Protocolo biológico validado"},
            {"2", "Raza Declarada", raz, "Ponderación de factores de riesgo"},
            {"3", "Edad en Meses", edad + " meses", "Segmentación etaria calculada"},
            {"4", "Sintomatología", sint, "Indexación de patologías clínicas"},
            {"5", "Dictamen Predictivo IA", res.replace("\n", " "), "Sugerencia orientativa para médico"}
        };

        Ui.mostrarVisorReporte(
            (java.awt.Frame) SwingUtilities.getWindowAncestor(this),
            "FICHA CLÍNICA DE TRIAJE Y DIAGNÓSTICO PREDICTIVO IA",
            "Evaluación de Asistencia Clínica HappyPet-Core-v1.8",
            meta,
            new String[]{"N°", "Parámetro Evaluado", "Valor Reportado", "Criterio de Inferencia IA"},
            datos,
            "Documento confidencial de apoyo diagnóstico veterinario Happy Pets.",
            "Ficha_Triaje_IA_" + esp
        );
    }

    private JPanel crearFilaCampo(String etiqueta, JComponent comp) {
        JPanel f = new JPanel(new BorderLayout(8, 0));
        f.setOpaque(false);
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JLabel lbl = new JLabel(etiqueta);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lbl.setForeground(Ui.TEXTO_MUTED);
        lbl.setPreferredSize(new Dimension(140, 26));

        f.add(lbl, BorderLayout.WEST);
        f.add(comp, BorderLayout.CENTER);
        return f;
    }

    private static class ScrollablePanel extends JPanel implements Scrollable {
        private static final long serialVersionUID = 1L;

        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }

        @Override
        public int getScrollableUnitIncrement(java.awt.Rectangle visibleRect, int orientation, int direction) {
            return 16;
        }

        @Override
        public int getScrollableBlockIncrement(java.awt.Rectangle visibleRect, int orientation, int direction) {
            return 32;
        }

        @Override
        public boolean getScrollableTracksViewportWidth() {
            return true;
        }

        @Override
        public boolean getScrollableTracksViewportHeight() {
            return false;
        }
    }
}
