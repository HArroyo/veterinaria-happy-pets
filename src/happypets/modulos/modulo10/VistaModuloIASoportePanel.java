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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
import happypets.ui.Ui;

/**
 * Submódulo 10.4: Módulo de IA y Soporte Técnico.
 * Estandarizado con la paleta institucional Turquesa Clínico Original y visor de reportes A4.
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
        setBackground(Ui.FONDO);
        inicializarUI();
        cargarDatos();
    }

    private void inicializarUI() {
        JPanel contenedor = new JPanel();
        contenedor.setLayout(new BoxLayout(contenedor, BoxLayout.Y_AXIS));
        contenedor.setBackground(Ui.FONDO);
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
        scroll.getViewport().setBackground(Ui.FONDO);
        add(scroll, BorderLayout.CENTER);
    }

    private JPanel crearTarjetaModuloIA() {
        JPanel card = new JPanel(new BorderLayout(0, 16));
        card.setBackground(Ui.FONDO_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Ui.BORDE_SUAVE, 1),
                BorderFactory.createEmptyBorder(20, 24, 20, 24)
        ));

        // Cabecera: Título "Módulo de IA HappyPets" y Badge "Habilitado"
        JPanel head = new JPanel(new BorderLayout());
        head.setBackground(Ui.FONDO_CARD);

        JPanel izq = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        izq.setBackground(Ui.FONDO_CARD);
        izq.add(new JLabel(Iconos.crearIconoRobot(24, Ui.TURQUESA_PROFUNDO)));
        JLabel lblTit = new JLabel("Módulo de IA HappyPets");
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTit.setForeground(Ui.TEXTO_TITULO);
        izq.add(lblTit);

        JLabel badgeHabilitado = new JLabel("Habilitado");
        badgeHabilitado.setFont(new Font("Segoe UI", Font.BOLD, 12));
        badgeHabilitado.setForeground(Ui.TURQUESA_PROFUNDO);
        badgeHabilitado.setBackground(Ui.TURQUESA_SUAVE);
        badgeHabilitado.setOpaque(true);
        badgeHabilitado.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Ui.TURQUESA_MEDIO, 1),
                BorderFactory.createEmptyBorder(4, 12, 4, 12)
        ));

        head.add(izq, BorderLayout.WEST);
        head.add(badgeHabilitado, BorderLayout.EAST);

        // Subtítulo
        JPanel cuerpo = new JPanel();
        cuerpo.setLayout(new BoxLayout(cuerpo, BoxLayout.Y_AXIS));
        cuerpo.setBackground(Ui.FONDO_CARD);

        JLabel lblSub = new JLabel("Configura los asistentes inteligentes para triaje clínico, diagnóstico sugerido y respuesta rápida al cliente.");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(Ui.TEXTO_MUTED);
        cuerpo.add(lblSub);
        cuerpo.add(Box.createVerticalStrut(14));

        // Switches interactivos
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
        pie.setBackground(Ui.FONDO_CARD);

        lblModeloActual = new JLabel("Modelo actual:  HappyPet-Core-v1.8");
        lblModeloActual.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblModeloActual.setForeground(Ui.TURQUESA_OSCURO);

        JButton btnPrompts = Ui.botonSecundario("Ajustes de Prompts", null);
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
        fila.setBackground(Ui.FONDO);
        fila.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Ui.BORDE_SUAVE, 1),
                BorderFactory.createEmptyBorder(10, 16, 10, 16)
        ));

        JPanel textos = new JPanel();
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        textos.setBackground(Ui.FONDO);

        JLabel lTit = new JLabel(titulo);
        lTit.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lTit.setForeground(Ui.TEXTO_TITULO);

        JLabel lSub = new JLabel(subtitulo);
        lSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lSub.setForeground(Ui.TEXTO_MUTED);

        textos.add(lTit);
        textos.add(Box.createVerticalStrut(2));
        textos.add(lSub);

        chk.setBackground(Ui.FONDO);
        chk.setFocusPainted(false);
        chk.setCursor(new Cursor(Cursor.HAND_CURSOR));
        chk.addActionListener(e -> guardarConfiguracionIA());

        fila.add(textos, BorderLayout.CENTER);
        fila.add(chk, BorderLayout.EAST);
        return fila;
    }

    private JPanel crearTarjetaConsolaIA() {
        JPanel card = new JPanel(new BorderLayout(0, 14));
        card.setBackground(Ui.FONDO_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Ui.BORDE_SUAVE, 1),
                BorderFactory.createEmptyBorder(18, 24, 18, 24)
        ));

        JPanel head = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        head.setBackground(Ui.FONDO_CARD);
        head.add(new JLabel(Iconos.crearIconoRobot(22, Ui.TURQUESA_OSCURO)));
        JLabel lblTit = new JLabel("Consola Interactiva de Triaje Clínico con IA");
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTit.setForeground(Ui.TEXTO_TITULO);
        head.add(lblTit);
        card.add(head, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Ui.FONDO_CARD);
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 6, 6, 6);
        g.fill = GridBagConstraints.HORIZONTAL;

        JComboBox<String> cbEspecie = Ui.combo(new String[]{"Canino", "Felino", "Conejo", "Ave Exótica"});
        JTextField txtRaza = Ui.campoTexto("Golden Retriever", 14);
        JTextField txtEdad = Ui.campoTexto("48", 6); // 48 meses
        JTextField txtSintomas = Ui.campoTexto("Tos seca nocturna recurrente, arcadas post ejercicio, letargo moderado.", 30);

        g.gridx = 0; g.gridy = 0; g.weightx = 0.15;
        form.add(Ui.crearLabelFormulario("Especie:"), g);
        g.gridx = 1; g.gridy = 0; g.weightx = 0.35;
        form.add(cbEspecie, g);

        g.gridx = 2; g.gridy = 0; g.weightx = 0.15;
        form.add(Ui.crearLabelFormulario("Raza:"), g);
        g.gridx = 3; g.gridy = 0; g.weightx = 0.35;
        form.add(txtRaza, g);

        g.gridx = 0; g.gridy = 1; g.weightx = 0.15;
        form.add(Ui.crearLabelFormulario("Edad (meses):"), g);
        g.gridx = 1; g.gridy = 1; g.weightx = 0.35;
        form.add(txtEdad, g);

        g.gridx = 2; g.gridy = 1; g.weightx = 0.15;
        form.add(Ui.crearLabelFormulario("Signos / Síntomas:"), g);
        g.gridx = 3; g.gridy = 1; g.weightx = 0.35;
        form.add(txtSintomas, g);

        JTextArea txtResultado = new JTextArea(7, 40);
        txtResultado.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtResultado.setBackground(Ui.FONDO);
        txtResultado.setForeground(Ui.TEXTO_REGULAR);
        txtResultado.setEditable(false);
        txtResultado.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Ui.BORDE_SUAVE, 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        txtResultado.setText("Presione 'Ejecutar Diagnóstico IA' para procesar el caso clínico.");

        JButton btnEjecutar = Ui.botonPrimario("⚡ Ejecutar Diagnóstico IA", null);
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

        JButton btnExportar = Ui.botonSecundario("📄 Exportar Ficha IA", null);
        btnExportar.addActionListener(e -> {
            exportarFichaDiagnostica(
                    (String) cbEspecie.getSelectedItem(),
                    txtRaza.getText().trim(),
                    txtEdad.getText().trim(),
                    txtSintomas.getText().trim(),
                    txtResultado.getText().trim()
            );
        });

        JPanel pnlAcciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlAcciones.setBackground(Ui.FONDO_CARD);
        pnlAcciones.add(btnExportar);
        pnlAcciones.add(btnEjecutar);

        JPanel pnlCentro = new JPanel(new BorderLayout(0, 10));
        pnlCentro.setBackground(Ui.FONDO_CARD);
        pnlCentro.add(form, BorderLayout.NORTH);
        pnlCentro.add(new JScrollPane(txtResultado), BorderLayout.CENTER);
        pnlCentro.add(pnlAcciones, BorderLayout.SOUTH);

        card.add(pnlCentro, BorderLayout.CENTER);
        return card;
    }

    private void exportarFichaDiagnostica(String especie, String raza, String edad, String sintomas, String resultado) {
        StringBuilder html = new StringBuilder();
        html.append("<html><body style='font-family:sans-serif; padding:15px; color:#1e293b;'>");
        html.append("<h2 style='color:#006064; border-bottom:2px solid #00BCD4; padding-bottom:6px;'>FICHA CLÍNICA DE TRIAJE Y DIAGNÓSTICO PREDICTIVO IA</h2>");
        html.append("<p><strong>Fecha de Evaluación:</strong> ").append(LocalDate.now()).append(" | <strong>Modelo IA:</strong> HappyPet-Core-v1.8</p>");
        html.append("<table border='1' cellpadding='6' cellspacing='0' style='border-collapse:collapse; width:100%; border-color:#cbd5e1; font-size:12px;'>");
        html.append("<tr style='background-color:#006064; color:white;'><th align='left'>Parámetro Clínico</th><th align='left'>Dato Evaluado</th><th align='left'>Sugerencia Asistente IA</th></tr>");
        html.append("<tr><td><b>Especie Animal:</b></td><td>").append(especie).append("</td><td>Protocolo biológico validado</td></tr>");
        html.append("<tr><td><b>Raza Declarada:</b></td><td>").append(raza.isEmpty() ? "No especificada" : raza).append("</td><td>Ponderación biométrica de factores de riesgo</td></tr>");
        html.append("<tr><td><b>Edad Cronológica:</b></td><td>").append(edad).append(" meses</td><td>Segmentación etaria pediátrica/adulta calculada</td></tr>");
        html.append("<tr><td><b>Sintomatología Reportada:</b></td><td>").append(sintomas.isEmpty() ? "Sin síntomas" : sintomas).append("</td><td>Indexación y correlación de patologías clínicas</td></tr>");
        html.append("<tr><td><b>Dictamen Predictivo IA:</b></td><td colspan='2' style='background-color:#E0F7FA; color:#006064;'><b>").append(resultado.isEmpty() ? "Pendiente" : resultado.replace("\n", "<br/>")).append("</b></td></tr>");
        html.append("</table>");
        html.append("<p style='margin-top:15px; font-size:10px; color:#64748b;'>Documento de pre-triaje asistido por IA para soporte clínico del médico veterinario oficial.</p>");
        html.append("</body></html>");

        StringBuilder csv = new StringBuilder("PARAMETRO,VALOR,EVALUACION_IA\n");
        csv.append("\"Especie Animal\",\"").append(especie).append("\",\"Protocolo validado\"\n");
        csv.append("\"Raza\",\"").append(raza).append("\",\"Ponderacion biometrica\"\n");
        csv.append("\"Edad Cronologica\",\"").append(edad).append(" meses\",\"Segmentacion etaria\"\n");
        csv.append("\"Sintomatologia\",\"").append(sintomas.replace("\"", "'")).append("\",\"Correlacion clinica\"\n");
        csv.append("\"Dictamen Preliminar\",\"").append(resultado.replace("\"", "'").replace("\n", " | ")).append("\",\"Dictamen asistido\"\n");

        Ui.mostrarVisorReporte(
                SwingUtilities.getWindowAncestor(this),
                "Ficha Diagnóstica y Triaje IA",
                "Ficha Clínica de Triaje y Diagnóstico Predictivo IA",
                html.toString(),
                csv.toString()
        );
    }

    private JPanel crearTarjetaSoporteTecnico() {
        JPanel card = new JPanel(new BorderLayout(0, 16));
        card.setBackground(Ui.FONDO_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Ui.BORDE_SUAVE, 1),
                BorderFactory.createEmptyBorder(20, 24, 20, 24)
        ));

        // Cabecera: Título con Auriculares y Estado: Normal
        JPanel head = new JPanel(new BorderLayout());
        head.setBackground(Ui.FONDO_CARD);

        JPanel izq = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        izq.setBackground(Ui.FONDO_CARD);
        izq.add(new JLabel(Iconos.crearIconoAuriculares(24, Ui.TURQUESA_PROFUNDO)));
        JLabel lblTit = new JLabel("Soporte Técnico y Diagnóstico");
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTit.setForeground(Ui.TEXTO_TITULO);
        izq.add(lblTit);

        JLabel lblEstadoNormal = new JLabel("Estado: Normal");
        lblEstadoNormal.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblEstadoNormal.setForeground(Ui.TURQUESA_PROFUNDO);
        lblEstadoNormal.setBackground(Ui.TURQUESA_SUAVE);
        lblEstadoNormal.setOpaque(true);
        lblEstadoNormal.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Ui.TURQUESA_MEDIO, 1),
                BorderFactory.createEmptyBorder(4, 12, 4, 12)
        ));

        head.add(izq, BorderLayout.WEST);
        head.add(lblEstadoNormal, BorderLayout.EAST);
        card.add(head, BorderLayout.NORTH);

        // Grid con métricas de salud
        JPanel metricas = new JPanel(new GridLayout(3, 2, 10, 8));
        metricas.setBackground(Ui.FONDO_CARD);
        metricas.setBorder(BorderFactory.createEmptyBorder(4, 4, 12, 4));

        lblEstadoBD = new JLabel("En línea (0.12 ms)");
        lblEstadoBD.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblEstadoBD.setForeground(Ui.TURQUESA_OSCURO);

        lblUltimoRespaldo = new JLabel("Hoy, 03:00 AM");
        lblUltimoRespaldo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblUltimoRespaldo.setForeground(Ui.TEXTO_TITULO);

        lblTicketsPendientes = new JLabel("0 pendientes");
        lblTicketsPendientes.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTicketsPendientes.setForeground(Ui.TEXTO_TITULO);

        metricas.add(crearLabelMetrica("Estado de Base de Datos:"));
        metricas.add(lblEstadoBD);
        metricas.add(crearLabelMetrica("Último Respaldo Cloud:"));
        metricas.add(lblUltimoRespaldo);
        metricas.add(crearLabelMetrica("Tickets de Soporte Abiertos:"));
        metricas.add(lblTicketsPendientes);

        card.add(metricas, BorderLayout.CENTER);

        // Botones inferiores: [Ver Logs Sistema] y [🎫 Crear Ticket]
        JPanel botones = new JPanel(new GridLayout(1, 2, 12, 0));
        botones.setBackground(Ui.FONDO_CARD);

        JButton btnLogs = Ui.botonSecundario("Ver Logs Sistema", null);
        btnLogs.addActionListener(e -> abrirLogsSistema());

        JButton btnTicket = Ui.botonPrimario("🎫 Crear Ticket", null);
        btnTicket.addActionListener(e -> abrirCrearTicket());

        botones.add(btnLogs);
        botones.add(btnTicket);
        card.add(botones, BorderLayout.SOUTH);

        return card;
    }

    private JLabel crearLabelMetrica(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        l.setForeground(Ui.TEXTO_MUTED);
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
        dlg.setSize(620, 440);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());

        JPanel pnl = new JPanel(new BorderLayout(0, 10));
        pnl.setBackground(Ui.FONDO_CARD);
        pnl.setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));

        JLabel lblTit = Ui.crearLabelFormulario("Prompt del Sistema para Triaje y Diagnóstico Clínico:");
        pnl.add(lblTit, BorderLayout.NORTH);

        JTextArea txtPrompt = new JTextArea(ia.getPromptSistema(), 10, 40);
        txtPrompt.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtPrompt.setLineWrap(true);
        txtPrompt.setWrapStyleWord(true);
        txtPrompt.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Ui.BORDE_INPUT, 1),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        pnl.add(new JScrollPane(txtPrompt), BorderLayout.CENTER);

        JPanel bot = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        bot.setBackground(Ui.FONDO_CARD);
        JButton btnCerrar = Ui.botonSecundario("Cancelar", null);
        btnCerrar.addActionListener(e -> dlg.dispose());
        JButton btnGuardar = Ui.botonPrimario("Guardar Prompt", null);
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
        dlg.setSize(720, 480);
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
        sb.append("[INFO] ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .append(" - Telemetría de salud de hardware: CPU: 12%, RAM: ").append(diag.getMemoriaUsadaMB()).append(" MB, Disco Libre: ").append(diag.getEspacioDiscoLibreGB()).append(" GB\n");
        txtLogs.setText(sb.toString());

        pnl.add(new JScrollPane(txtLogs), BorderLayout.CENTER);

        JButton btnExportar = Ui.botonSecundario("📄 Exportar Telemetría", null);
        btnExportar.addActionListener(e -> {
            StringBuilder html = new StringBuilder();
            html.append("<html><body style='font-family:sans-serif; padding:15px; color:#1e293b;'>");
            html.append("<h2 style='color:#006064; border-bottom:2px solid #00BCD4; padding-bottom:6px;'>REPORTE TÉCNICO DE TELEMETRÍA Y ESTADO DEL SISTEMA</h2>");
            html.append("<p><strong>Fecha de Emisión:</strong> ").append(LocalDate.now()).append(" | <strong>Entorno:</strong> Servidor Local / Cloud Híbrido</p>");
            html.append("<table border='1' cellpadding='6' cellspacing='0' style='border-collapse:collapse; width:100%; border-color:#cbd5e1; font-size:12px;'>");
            html.append("<tr style='background-color:#006064; color:white;'><th align='left'>Métrica de Infraestructura</th><th align='left'>Valor Actual</th><th align='left'>Diagnóstico Operativo</th></tr>");
            html.append("<tr><td><b>Base de Datos Local:</b></td><td>").append(diag.getEstadoBaseDatos()).append("</td><td>Conexión estable e íntegra</td></tr>");
            html.append("<tr><td><b>Último Respaldo Cloud:</b></td><td>").append(diag.getUltimoRespaldoCloud()).append("</td><td>Snapshot sincronizado</td></tr>");
            html.append("<tr><td><b>Memoria JVM en Uso:</b></td><td>").append(diag.getMemoriaUsadaMB()).append(" MB</td><td>Consumo eficiente dentro del umbral</td></tr>");
            html.append("<tr><td><b>Espacio en Disco Libre:</b></td><td>").append(diag.getEspacioDiscoLibreGB()).append(" GB</td><td>Capacidad adecuada</td></tr>");
            html.append("<tr><td><b>Tickets de Soporte Abiertos:</b></td><td>").append(diag.getTicketsPendientes()).append(" pendientes</td><td>Mesa de ayuda activa</td></tr>");
            html.append("</table>");
            html.append("<p style='margin-top:15px; font-size:10px; color:#64748b;'>Reporte técnico generado por el subsistema de diagnóstico y telemetría de Happy Pets ERP.</p>");
            html.append("</body></html>");

            StringBuilder csv = new StringBuilder("METRICA,VALOR,DIAGNOSTICO\n");
            csv.append("\"Base de Datos\",\"").append(diag.getEstadoBaseDatos()).append("\",\"Conexion estable\"\n");
            csv.append("\"Ultimo Respaldo\",\"").append(diag.getUltimoRespaldoCloud()).append("\",\"Snapshot sincronizado\"\n");
            csv.append("\"Memoria JVM\",\"").append(diag.getMemoriaUsadaMB()).append(" MB\",\"Consumo normal\"\n");
            csv.append("\"Espacio en Disco\",\"").append(diag.getEspacioDiscoLibreGB()).append(" GB\",\"Capacidad adecuada\"\n");
            csv.append("\"Tickets Pendientes\",").append(diag.getTicketsPendientes()).append(",\"Mesa de ayuda\"\n");

            Ui.mostrarVisorReporte(
                    dlg,
                    "Telemetría y Diagnóstico del Servidor",
                    "Reporte Técnico de Telemetría y Salud del Sistema",
                    html.toString(),
                    csv.toString()
            );
        });

        JButton btnCerrar = Ui.botonSecundario("Cerrar Consola", null);
        btnCerrar.addActionListener(e -> dlg.dispose());

        JPanel bot = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        bot.setBackground(new Color(15, 23, 42));
        bot.add(btnExportar);
        bot.add(btnCerrar);

        dlg.add(pnl, BorderLayout.CENTER);
        dlg.add(bot, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    private void abrirCrearTicket() {
        JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(this), "Crear Ticket de Soporte Técnico", JDialog.ModalityType.APPLICATION_MODAL);
        dlg.setSize(520, 440);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());

        JPanel pnl = new JPanel(new GridBagLayout());
        pnl.setBackground(Ui.FONDO_CARD);
        pnl.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 6, 6, 6);
        g.fill = GridBagConstraints.HORIZONTAL;

        JTextField txtAsunto = Ui.campoTexto("", 20);
        JComboBox<String> cbCat = Ui.combo(new String[]{"Integraciones y APIs", "Base de Datos", "Facturación Electrónica", "Hardware e Impresoras POS", "Otro"});
        JComboBox<String> cbPrio = Ui.combo(new String[]{"Alta", "Media", "Baja"});
        JTextArea txtDesc = new JTextArea(4, 20);
        txtDesc.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtDesc.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Ui.BORDE_INPUT, 1),
                BorderFactory.createEmptyBorder(6, 6, 6, 6)
        ));

        g.gridx = 0; g.gridy = 0; g.weightx = 0.35;
        pnl.add(Ui.crearLabelFormulario("Asunto del Ticket:"), g);
        g.gridx = 1; g.gridy = 0; g.weightx = 0.65;
        pnl.add(txtAsunto, g);

        g.gridx = 0; g.gridy = 1;
        pnl.add(Ui.crearLabelFormulario("Categoría:"), g);
        g.gridx = 1; g.gridy = 1;
        pnl.add(cbCat, g);

        g.gridx = 0; g.gridy = 2;
        pnl.add(Ui.crearLabelFormulario("Prioridad:"), g);
        g.gridx = 1; g.gridy = 2;
        pnl.add(cbPrio, g);

        g.gridx = 0; g.gridy = 3;
        pnl.add(Ui.crearLabelFormulario("Descripción:"), g);
        g.gridx = 1; g.gridy = 3;
        pnl.add(new JScrollPane(txtDesc), g);

        JPanel bot = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        bot.setBackground(Ui.FONDO_CARD);
        JButton btnCan = Ui.botonSecundario("Cancelar", null);
        btnCan.addActionListener(e -> dlg.dispose());
        JButton btnCrear = Ui.botonPrimario("Enviar Ticket", null);
        btnCrear.addActionListener(e -> {
            String asunto = txtAsunto.getText().trim();
            if (asunto.isEmpty()) {
                JOptionPane.showMessageDialog(dlg, "Ingrese el asunto del ticket.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            TicketSoporte t = new TicketSoporte(
                    null, asunto, (String) cbCat.getSelectedItem(), (String) cbPrio.getSelectedItem(),
                    "Abierto", "admin_user", LocalDateTime.now(), txtDesc.getText().trim(),
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
