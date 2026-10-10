package happypets.modulos.modulo10;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import happypets.data.RepositorioVeterinaria;
import happypets.model.ConfiguracionModuloIA;
import happypets.model.DiagnosticoSistema;
import happypets.model.TicketSoporte;
import happypets.ui.Iconos;

/**
 * Submódulo 10.4: Módulo de IA y Soporte Técnico.
 * Basado fielmente en el wireframe 'MODULO IA.pdf'.
 */
public class VistaModuloIASoportePanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private final RepositorioVeterinaria repo = RepositorioVeterinaria.getInstancia();

    private JCheckBox chkPreTriaje;
    private JCheckBox chkDiagnostico;
    private JCheckBox chkVacunas;
    private JLabel lblModeloActual;

    private JLabel lblEstadoBD;
    private JLabel lblUltimoRespaldo;
    private JLabel lblTicketsPendientes;

    public VistaModuloIASoportePanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(248, 250, 252));
        inicializarUI();
        cargarDatos();
    }

    private void inicializarUI() {
        JPanel contenedor = new JPanel();
        contenedor.setLayout(new BoxLayout(contenedor, BoxLayout.Y_AXIS));
        contenedor.setBackground(new Color(248, 250, 252));
        contenedor.setBorder(BorderFactory.createEmptyBorder(20, 28, 28, 28));

        // 1. Tarjeta: "Módulo de IA HappyPets"
        contenedor.add(crearTarjetaModuloIA());
        contenedor.add(Box.createVerticalStrut(20));

        // 2. Tarjeta: "Consola Interactiva de Triaje y Diagnóstico IA"
        contenedor.add(crearTarjetaConsolaIA());
        contenedor.add(Box.createVerticalStrut(20));

        // 3. Tarjeta: "Soporte Técnico y Diagnóstico"
        contenedor.add(crearTarjetaSoporteTecnico());
        contenedor.add(Box.createVerticalGlue());

        JScrollPane scroll = new JScrollPane(contenedor);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(14);
        scroll.getViewport().setBackground(new Color(248, 250, 252));
        add(scroll, BorderLayout.CENTER);
    }

    private JPanel crearTarjetaModuloIA() {
        JPanel card = new JPanel(new BorderLayout(0, 16));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                BorderFactory.createEmptyBorder(20, 24, 20, 24)
        ));

        // Cabecera: Título "Módulo de IA HappyPets" y Badge "Habilitado"
        JPanel head = new JPanel(new BorderLayout());
        head.setBackground(Color.WHITE);

        JPanel izq = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        izq.setBackground(Color.WHITE);
        izq.add(new JLabel(Iconos.crearIconoRobot(24, new Color(15, 23, 42))));
        JLabel lblTit = new JLabel("Módulo de IA HappyPets");
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTit.setForeground(new Color(15, 23, 42));
        izq.add(lblTit);

        JLabel badgeHabilitado = new JLabel("Habilitado");
        badgeHabilitado.setFont(new Font("Segoe UI", Font.BOLD, 12));
        badgeHabilitado.setForeground(new Color(30, 41, 59));
        badgeHabilitado.setBackground(new Color(241, 245, 249));
        badgeHabilitado.setOpaque(true);
        badgeHabilitado.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225), 1),
                BorderFactory.createEmptyBorder(4, 12, 4, 12)
        ));

        head.add(izq, BorderLayout.WEST);
        head.add(badgeHabilitado, BorderLayout.EAST);

        // Subtítulo
        JPanel cuerpo = new JPanel();
        cuerpo.setLayout(new BoxLayout(cuerpo, BoxLayout.Y_AXIS));
        cuerpo.setBackground(Color.WHITE);

        JLabel lblSub = new JLabel("Configura los asistentes inteligentes para triaje clínico, diagnóstico sugerido y respuesta rápida al cliente.");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(new Color(100, 116, 139));
        cuerpo.add(lblSub);
        cuerpo.add(Box.createVerticalStrut(14));

        // Switches interactivos (3 items del wireframe)
        chkPreTriaje = new JCheckBox();
        chkDiagnostico = new JCheckBox();
        chkVacunas = new JCheckBox();

        cuerpo.add(crearFilaSwitch("Pre-Triaje de Mascotas con IA", "Categoriza urgencias al registrar ingreso del paciente.", chkPreTriaje));
        cuerpo.add(Box.createVerticalStrut(10));
        cuerpo.add(crearFilaSwitch("Asistente Diagnóstico Preliminar", "Sugiere posibles patologías por síntomas clínicos reportados.", chkDiagnostico));
        cuerpo.add(Box.createVerticalStrut(10));
        cuerpo.add(crearFilaSwitch("Recordatorios Predictivos de Vacunas", "Calcula fechas óptimas por raza/edad y factores de riesgo.", chkVacunas));
        cuerpo.add(Box.createVerticalStrut(16));

        // Pie de la tarjeta: Modelo actual + Botón Ajustes de Prompts
        JPanel pie = new JPanel(new BorderLayout());
        pie.setBackground(Color.WHITE);

        lblModeloActual = new JLabel("Modelo actual:  HappyPet-Core-v1.8");
        lblModeloActual.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblModeloActual.setForeground(new Color(71, 85, 105));

        JButton btnPrompts = new JButton("Ajustes de Prompts");
        btnPrompts.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnPrompts.setForeground(new Color(30, 41, 59));
        btnPrompts.setBackground(new Color(241, 245, 249));
        btnPrompts.setFocusPainted(false);
        btnPrompts.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnPrompts.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225), 1),
                BorderFactory.createEmptyBorder(6, 14, 6, 14)
        ));
        btnPrompts.addActionListener(e -> abrirAjustesPrompts());

        pie.add(lblModeloActual, BorderLayout.WEST);
        pie.add(btnPrompts, BorderLayout.EAST);
        cuerpo.add(pie);

        card.add(head, BorderLayout.NORTH);
        card.add(cuerpo, BorderLayout.CENTER);
        return card;
    }

    private JPanel crearFilaSwitch(String titulo, String subtitulo, JCheckBox chk) {
        JPanel fila = new JPanel(new BorderLayout(14, 0));
        fila.setBackground(new Color(248, 250, 252));
        fila.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                BorderFactory.createEmptyBorder(10, 16, 10, 16)
        ));

        JPanel textos = new JPanel();
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        textos.setBackground(new Color(248, 250, 252));

        JLabel lTit = new JLabel(titulo);
        lTit.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lTit.setForeground(new Color(15, 23, 42));

        JLabel lSub = new JLabel(subtitulo);
        lSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lSub.setForeground(new Color(100, 116, 139));

        textos.add(lTit);
        textos.add(Box.createVerticalStrut(2));
        textos.add(lSub);

        chk.setBackground(new Color(248, 250, 252));
        chk.setFocusPainted(false);
        chk.setCursor(new Cursor(Cursor.HAND_CURSOR));
        chk.addActionListener(e -> guardarConfiguracionIA());

        fila.add(textos, BorderLayout.CENTER);
        fila.add(chk, BorderLayout.EAST);
        return fila;
    }

    private JPanel crearTarjetaConsolaIA() {
        JPanel card = new JPanel(new BorderLayout(0, 14));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                BorderFactory.createEmptyBorder(18, 24, 18, 24)
        ));

        JLabel lblTit = new JLabel("Consola Interactiva de Triaje Clínico con IA");
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTit.setForeground(new Color(15, 23, 42));
        card.add(lblTit, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 4, 4, 4);
        g.fill = GridBagConstraints.HORIZONTAL;

        JComboBox<String> cbEspecie = new JComboBox<>(new String[]{"Canino", "Felino", "Conejo", "Ave Exótica"});
        JTextField txtRaza = new JTextField("Golden Retriever");
        JTextField txtEdad = new JTextField("48"); // 48 meses
        JTextField txtSintomas = new JTextField("Tos seca nocturna recurrente, arcadas post ejercicio, letargo moderado.");

        g.gridx = 0; g.gridy = 0; g.weightx = 0.25;
        form.add(new JLabel("Especie:"), g);
        g.gridx = 1; g.gridy = 0; g.weightx = 0.25;
        form.add(cbEspecie, g);

        g.gridx = 2; g.gridy = 0; g.weightx = 0.25;
        form.add(new JLabel("Raza:"), g);
        g.gridx = 3; g.gridy = 0; g.weightx = 0.25;
        form.add(txtRaza, g);

        g.gridx = 0; g.gridy = 1;
        form.add(new JLabel("Edad (meses):"), g);
        g.gridx = 1; g.gridy = 1;
        form.add(txtEdad, g);

        g.gridx = 2; g.gridy = 1;
        form.add(new JLabel("Signos / Síntomas:"), g);
        g.gridx = 3; g.gridy = 1;
        form.add(txtSintomas, g);

        JTextArea txtResultado = new JTextArea(7, 40);
        txtResultado.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtResultado.setBackground(new Color(248, 250, 252));
        txtResultado.setEditable(false);
        txtResultado.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        txtResultado.setText("Presione 'Ejecutar Diagnóstico IA' para procesar el caso clínico.");

        JButton btnEjecutar = new JButton("⚡  Ejecutar Diagnóstico IA");
        btnEjecutar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnEjecutar.setForeground(Color.WHITE);
        btnEjecutar.setBackground(new Color(15, 23, 42));
        btnEjecutar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnEjecutar.addActionListener(e -> {
            int edadM = 24;
            try {
                edadM = Integer.parseInt(txtEdad.getText().trim());
            } catch (Exception ex) {}

            String res = repo.simularDiagnosticoTriajeIA(
                    (String) cbEspecie.getSelectedItem(),
                    txtRaza.getText().trim(),
                    edadM,
                    "Consulta por síntomas clínicos",
                    txtSintomas.getText().trim()
            );
            txtResultado.setText(res);
            txtResultado.setCaretPosition(0);
        });

        JPanel pnlCentro = new JPanel(new BorderLayout(0, 10));
        pnlCentro.setBackground(Color.WHITE);
        pnlCentro.add(form, BorderLayout.NORTH);
        pnlCentro.add(new JScrollPane(txtResultado), BorderLayout.CENTER);
        pnlCentro.add(btnEjecutar, BorderLayout.SOUTH);

        card.add(pnlCentro, BorderLayout.CENTER);
        return card;
    }

    private JPanel crearTarjetaSoporteTecnico() {
        JPanel card = new JPanel(new BorderLayout(0, 16));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                BorderFactory.createEmptyBorder(20, 24, 20, 24)
        ));

        // Cabecera: Título con Auriculares y Estado: Normal
        JPanel head = new JPanel(new BorderLayout());
        head.setBackground(Color.WHITE);

        JPanel izq = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        izq.setBackground(Color.WHITE);
        izq.add(new JLabel(Iconos.crearIconoAuriculares(24, new Color(15, 23, 42))));
        JLabel lblTit = new JLabel("Soporte Técnico y Diagnóstico");
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTit.setForeground(new Color(15, 23, 42));
        izq.add(lblTit);

        JLabel lblEstadoNormal = new JLabel("Estado: Normal");
        lblEstadoNormal.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblEstadoNormal.setForeground(new Color(30, 41, 59));

        head.add(izq, BorderLayout.WEST);
        head.add(lblEstadoNormal, BorderLayout.EAST);
        card.add(head, BorderLayout.NORTH);

        // Grid con métricas de salud
        JPanel metricas = new JPanel(new GridLayout(3, 2, 10, 8));
        metricas.setBackground(Color.WHITE);
        metricas.setBorder(BorderFactory.createEmptyBorder(4, 4, 12, 4));

        lblEstadoBD = new JLabel("En línea (0.12 ms)");
        lblEstadoBD.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblEstadoBD.setForeground(new Color(34, 197, 94)); // Verde

        lblUltimoRespaldo = new JLabel("Hoy, 03:00 AM");
        lblUltimoRespaldo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblUltimoRespaldo.setForeground(new Color(15, 23, 42));

        lblTicketsPendientes = new JLabel("0 pendientes");
        lblTicketsPendientes.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTicketsPendientes.setForeground(new Color(15, 23, 42));

        metricas.add(crearLabelMetrica("Estado de Base de Datos:"));
        metricas.add(lblEstadoBD);
        metricas.add(crearLabelMetrica("Último Respaldo Cloud:"));
        metricas.add(lblUltimoRespaldo);
        metricas.add(crearLabelMetrica("Tickets de Soporte Abiertos:"));
        metricas.add(lblTicketsPendientes);

        card.add(metricas, BorderLayout.CENTER);

        // Botones inferiores: [Ver Logs Sistema] y [🎫 Crear Ticket]
        JPanel botones = new JPanel(new GridLayout(1, 2, 12, 0));
        botones.setBackground(Color.WHITE);

        JButton btnLogs = new JButton("Ver Logs Sistema");
        btnLogs.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnLogs.setForeground(new Color(51, 65, 85));
        btnLogs.setBackground(new Color(241, 245, 249));
        btnLogs.setFocusPainted(false);
        btnLogs.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogs.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225), 1),
                BorderFactory.createEmptyBorder(8, 14, 8, 14)
        ));
        btnLogs.addActionListener(e -> abrirLogsSistema());

        JButton btnTicket = new JButton("🎫  Crear Ticket");
        btnTicket.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnTicket.setForeground(new Color(51, 65, 85));
        btnTicket.setBackground(new Color(241, 245, 249));
        btnTicket.setFocusPainted(false);
        btnTicket.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnTicket.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225), 1),
                BorderFactory.createEmptyBorder(8, 14, 8, 14)
        ));
        btnTicket.addActionListener(e -> abrirCrearTicket());

        botones.add(btnLogs);
        botones.add(btnTicket);
        card.add(botones, BorderLayout.SOUTH);

        return card;
    }

    private JLabel crearLabelMetrica(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        l.setForeground(new Color(71, 85, 105));
        return l;
    }

    private void cargarDatos() {
        ConfiguracionModuloIA ia = repo.getConfiguracionModuloIA();
        if (ia != null) {
            chkPreTriaje.setSelected(ia.isPreTriajeMascotas());
            chkDiagnostico.setSelected(ia.isAsistenteDiagnosticoPreliminar());
            chkVacunas.setSelected(ia.isRecordatoriosPredictivosVacunas());
            lblModeloActual.setText("Modelo actual:  " + ia.getModeloActual());
        }

        DiagnosticoSistema diag = repo.getDiagnosticoSistema();
        if (diag != null) {
            lblEstadoBD.setText(diag.getEstadoBaseDatos());
            lblUltimoRespaldo.setText(diag.getUltimoRespaldoCloud());
            lblTicketsPendientes.setText(diag.getTicketsPendientes() + " pendientes");
        }
    }

    private void guardarConfiguracionIA() {
        ConfiguracionModuloIA ia = repo.getConfiguracionModuloIA();
        if (ia != null) {
            ia.setPreTriajeMascotas(chkPreTriaje.isSelected());
            ia.setAsistenteDiagnosticoPreliminar(chkDiagnostico.isSelected());
            ia.setRecordatoriosPredictivosVacunas(chkVacunas.isSelected());
            repo.guardarConfiguracionModuloIA(ia);
        }
    }

    private void abrirAjustesPrompts() {
        ConfiguracionModuloIA ia = repo.getConfiguracionModuloIA();
        JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(this), "Ajustes de Prompts de IA", JDialog.ModalityType.APPLICATION_MODAL);
        dlg.setSize(580, 420);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());

        JPanel pnl = new JPanel(new BorderLayout(0, 10));
        pnl.setBackground(Color.WHITE);
        pnl.setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));

        pnl.add(new JLabel("Prompt del Sistema para Triaje y Diagnóstico Clínico:"), BorderLayout.NORTH);

        JTextArea txtPrompt = new JTextArea(ia.getPromptSistema(), 10, 40);
        txtPrompt.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtPrompt.setLineWrap(true);
        txtPrompt.setWrapStyleWord(true);
        pnl.add(new JScrollPane(txtPrompt), BorderLayout.CENTER);

        JPanel bot = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton btnCerrar = new JButton("Cancelar");
        btnCerrar.addActionListener(e -> dlg.dispose());
        JButton btnGuardar = new JButton("Guardar Prompt");
        btnGuardar.setBackground(new Color(15, 23, 42));
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.addActionListener(e -> {
            ia.setPromptSistema(txtPrompt.getText().trim());
            repo.guardarConfiguracionModuloIA(ia);
            dlg.dispose();
            JOptionPane.showMessageDialog(this, "✓ Prompt del sistema actualizado para HappyPet-Core-v1.8.", "Prompt Guardado", JOptionPane.INFORMATION_MESSAGE);
        });
        bot.add(btnCerrar);
        bot.add(btnGuardar);

        dlg.add(pnl, BorderLayout.CENTER);
        dlg.add(bot, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    private void abrirLogsSistema() {
        DiagnosticoSistema diag = repo.getDiagnosticoSistema();
        JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(this), "Logs del Servidor y Diagnóstico", JDialog.ModalityType.APPLICATION_MODAL);
        dlg.setSize(680, 460);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());

        JPanel pnl = new JPanel(new BorderLayout(0, 10));
        pnl.setBackground(new Color(15, 23, 42));
        pnl.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JTextArea txtLogs = new JTextArea();
        txtLogs.setFont(new Font("Consolas", Font.PLAIN, 12));
        txtLogs.setBackground(new Color(15, 23, 42));
        txtLogs.setForeground(new Color(148, 163, 184));
        txtLogs.setEditable(false);

        StringBuilder sb = new StringBuilder();
        for (String log : diag.getLogsServidor()) {
            sb.append(log).append("\n");
        }
        sb.append("[INFO] ").append(java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .append(" - Telemetría de salud de hardware: CPU: 12%, RAM: ").append(diag.getMemoriaUsadaMB()).append(" MB, Disco Libre: ").append(diag.getEspacioDiscoLibreGB()).append(" GB\n");
        txtLogs.setText(sb.toString());

        pnl.add(new JScrollPane(txtLogs), BorderLayout.CENTER);

        JButton btnCerrar = new JButton("Cerrar Consola");
        btnCerrar.addActionListener(e -> dlg.dispose());
        JPanel bot = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bot.setBackground(new Color(15, 23, 42));
        bot.add(btnCerrar);

        dlg.add(pnl, BorderLayout.CENTER);
        dlg.add(bot, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    private void abrirCrearTicket() {
        JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(this), "Crear Ticket de Soporte Técnico", JDialog.ModalityType.APPLICATION_MODAL);
        dlg.setSize(480, 420);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());

        JPanel pnl = new JPanel(new GridBagLayout());
        pnl.setBackground(Color.WHITE);
        pnl.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 6, 6, 6);
        g.fill = GridBagConstraints.HORIZONTAL;

        JTextField txtAsunto = new JTextField();
        JComboBox<String> cbCat = new JComboBox<>(new String[]{"Integraciones y APIs", "Base de Datos", "Facturación Electrónica", "Hardware e Impresoras POS", "Otro"});
        JComboBox<String> cbPrio = new JComboBox<>(new String[]{"Alta", "Media", "Baja"});
        JTextArea txtDesc = new JTextArea(4, 20);
        txtDesc.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtDesc.setBorder(BorderFactory.createLineBorder(new Color(203, 213, 225)));

        g.gridx = 0; g.gridy = 0; g.weightx = 0.35;
        pnl.add(new JLabel("Asunto del Ticket:"), g);
        g.gridx = 1; g.gridy = 0; g.weightx = 0.65;
        pnl.add(txtAsunto, g);

        g.gridx = 0; g.gridy = 1;
        pnl.add(new JLabel("Categoría:"), g);
        g.gridx = 1; g.gridy = 1;
        pnl.add(cbCat, g);

        g.gridx = 0; g.gridy = 2;
        pnl.add(new JLabel("Prioridad:"), g);
        g.gridx = 1; g.gridy = 2;
        pnl.add(cbPrio, g);

        g.gridx = 0; g.gridy = 3;
        pnl.add(new JLabel("Descripción:"), g);
        g.gridx = 1; g.gridy = 3;
        pnl.add(new JScrollPane(txtDesc), g);

        JPanel bot = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton btnCan = new JButton("Cancelar");
        btnCan.addActionListener(e -> dlg.dispose());
        JButton btnCrear = new JButton("Enviar Ticket");
        btnCrear.setBackground(new Color(15, 23, 42));
        btnCrear.setForeground(Color.WHITE);
        btnCrear.addActionListener(e -> {
            String asunto = txtAsunto.getText().trim();
            if (asunto.isEmpty()) {
                JOptionPane.showMessageDialog(dlg, "Ingrese el asunto del ticket.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            TicketSoporte t = new TicketSoporte(
                    null, asunto, (String) cbCat.getSelectedItem(), (String) cbPrio.getSelectedItem(),
                    "Abierto", "admin_user", java.time.LocalDateTime.now(), txtDesc.getText().trim(),
                    "Ticket encolado en cola técnica de HappyPets L1."
            );
            repo.crearTicketSoporte(t);
            cargarDatos();
            dlg.dispose();
            JOptionPane.showMessageDialog(this, "✓ Ticket " + t.getIdTicket() + " creado con éxito. El equipo de soporte lo atenderá a la brevedad.", "Ticket Enviado", JOptionPane.INFORMATION_MESSAGE);
        });
        bot.add(btnCan);
        bot.add(btnCrear);

        dlg.add(pnl, BorderLayout.CENTER);
        dlg.add(bot, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }
}
