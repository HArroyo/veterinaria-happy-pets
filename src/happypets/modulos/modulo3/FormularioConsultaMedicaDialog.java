package happypets.modulos.modulo3;

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
import java.awt.Window;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

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
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import happypets.data.RepositorioVeterinaria;
import happypets.model.AtencionMedica;
import happypets.model.Cliente;
import happypets.model.Mascota;
import happypets.ui.Iconos;
import happypets.ui.Ui;

/**
 * Diálogo modal profesional para el registro y edición de consultas médicas
 * y atenciones clínicas veterinarias (Submódulo 3.1).
 */
public class FormularioConsultaMedicaDialog extends JDialog {
    private static final long serialVersionUID = 1L;

    private static final Color COLOR_AZUL_PRIMARIO = Ui.TURQUESA;
    private static final Color COLOR_BORDE = Ui.BORDE_SUAVE;
    private static final Color COLOR_TEXTO_TITULO = Ui.TEXTO_TITULO;
    private static final Color COLOR_TEXTO_MUTED = Ui.TEXTO_MUTED;

    private final RepositorioVeterinaria repo = RepositorioVeterinaria.getInstancia();
    private final Consumer<AtencionMedica> onGuardado;
    private final AtencionMedica atencionExistente;

    // Campos Pestaña 1
    private JComboBox<PacienteItem> cbPaciente;
    private JTextField txtTutor;
    private JTextField txtTelefono;
    private JComboBox<String> cbVeterinario;
    private JTextField txtMotivo;
    private JTextField txtPeso;
    private JTextField txtTemperatura;
    private JTextField txtFC;
    private JTextArea txtEvaluacion;
    private JTextField txtAlergias;

    // Campos Pestaña 2
    private JTextField txtDiagPresuntivo;
    private JTextField txtDiagDefinitivo;
    private JTextArea txtTratamiento;
    private JTextArea txtReceta;
    private JTextArea txtIndicaciones;
    private JTextField txtSeguimiento;
    private JTextField txtCosto;
    private JComboBox<String> cbEstado;

    public static class PacienteItem {
        public final Mascota mascota;
        public PacienteItem(Mascota mascota) { this.mascota = mascota; }
        @Override
        public String toString() {
            return mascota != null ? mascota.getCodigo() + " - " + mascota.getNombre() + " (" + mascota.getEspecie() + " · " + mascota.getRaza() + ")" : "Seleccione paciente...";
        }
    }

    public FormularioConsultaMedicaDialog(Window parent, AtencionMedica atencionExistente, Mascota mascotaInicial, Consumer<AtencionMedica> onGuardado) {
        super(parent, atencionExistente == null ? "Nueva Consulta Médica" : "Editar Consulta " + atencionExistente.getIdConsulta(), ModalityType.APPLICATION_MODAL);
        this.atencionExistente = atencionExistente;
        this.onGuardado = onGuardado;

        setSize(680, 720);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        // Cabecera superior
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(13, 148, 136)); // Teal
        header.setBorder(new EmptyBorder(14, 20, 14, 20));

        JPanel headerTxt = new JPanel();
        headerTxt.setOpaque(false);
        headerTxt.setLayout(new BoxLayout(headerTxt, BoxLayout.Y_AXIS));

        JLabel title = new JLabel(atencionExistente == null ? "Registro de Atención Médica Veterinaria" : "Modificación de Expediente Clínico");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(Color.WHITE);
        title.setIcon(Iconos.crearIconoEstetoscopio(20, Color.WHITE));
        title.setIconTextGap(8);

        JLabel sub = new JLabel(atencionExistente == null ?
                "Anamnesis clínica, constantes vitales, exploración física y prescripción farmacológica." :
                "ID Consulta: " + atencionExistente.getIdConsulta() + " · Paciente: " + atencionExistente.getNombreMascota());
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sub.setForeground(new Color(204, 251, 241));

        headerTxt.add(title);
        headerTxt.add(Box.createVerticalStrut(2));
        headerTxt.add(sub);
        header.add(headerTxt, BorderLayout.CENTER);
        add(header, BorderLayout.NORTH);

        // Tabs de organización clínica
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabs.setBackground(Color.WHITE);

        tabs.addTab("1. Anamnesis y Exploración Física", Iconos.crearIconoHistorial(14, COLOR_AZUL_PRIMARIO), crearTabAnamnesis());
        tabs.addTab("2. Diagnóstico y Prescripción", Iconos.crearIconoPildora(14, new Color(147, 51, 234)), crearTabDiagnostico());

        add(tabs, BorderLayout.CENTER);

        // Barra inferior de botones
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        footer.setBackground(new Color(248, 250, 252));
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, COLOR_BORDE));

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnCancelar.setForeground(new Color(100, 116, 139));
        btnCancelar.setBackground(Color.WHITE);
        btnCancelar.setFocusPainted(false);
        btnCancelar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnCancelar.setPreferredSize(new Dimension(95, 34));
        btnCancelar.addActionListener(e -> dispose());

        JButton btnGuardar = new JButton(atencionExistente == null ? "Guardar Consulta" : "Actualizar Consulta");
        btnGuardar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setBackground(COLOR_AZUL_PRIMARIO);
        btnGuardar.setIcon(Iconos.crearIconoCheck(14, Color.WHITE));
        btnGuardar.setIconTextGap(6);
        btnGuardar.setFocusPainted(false);
        btnGuardar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnGuardar.setPreferredSize(new Dimension(170, 34));
        btnGuardar.addActionListener(e -> guardarConsulta());

        footer.add(btnCancelar);
        footer.add(btnGuardar);
        add(footer, BorderLayout.SOUTH);

        // Cargar datos si aplica
        if (atencionExistente != null) {
            cargarDatosExistentes();
        } else if (mascotaInicial != null) {
            preseleccionarMascota(mascotaInicial);
        } else {
            autocompletarDatosPaciente();
        }
    }

    private JPanel crearTabAnamnesis() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(14, 18, 14, 18));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 4, 4, 4);
        int y = 0;

        // Paciente
        gbc.gridx = 0; gbc.gridy = y; gbc.gridwidth = 2; gbc.weightx = 1.0;
        panel.add(crearEtiquetaCampo("Paciente Veterinario *"), gbc);
        y++;

        cbPaciente = new JComboBox<>();
        cbPaciente.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cbPaciente.setBackground(Color.WHITE);
        cbPaciente.setPreferredSize(new Dimension(0, 30));
        for (Mascota m : repo.todasLasMascotas()) {
            cbPaciente.addItem(new PacienteItem(m));
        }
        cbPaciente.addActionListener(e -> autocompletarDatosPaciente());
        gbc.gridx = 0; gbc.gridy = y; gbc.gridwidth = 2; gbc.weightx = 1.0;
        panel.add(cbPaciente, gbc);
        y++;

        // Tutor y Teléfono
        gbc.gridwidth = 1; gbc.weightx = 0.5;
        gbc.gridx = 0; gbc.gridy = y;
        panel.add(crearEtiquetaCampo("Propietario / Tutor"), gbc);
        gbc.gridx = 1; gbc.gridy = y;
        panel.add(crearEtiquetaCampo("Teléfono de Contacto Móvil"), gbc);
        y++;

        txtTutor = crearCampoTexto(false);
        txtTelefono = crearCampoTexto(false);
        gbc.gridx = 0; gbc.gridy = y;
        panel.add(txtTutor, gbc);
        gbc.gridx = 1; gbc.gridy = y;
        panel.add(txtTelefono, gbc);
        y++;

        // Veterinario y Motivo
        gbc.gridx = 0; gbc.gridy = y;
        panel.add(crearEtiquetaCampo("Veterinario Tratante *"), gbc);
        gbc.gridx = 1; gbc.gridy = y;
        panel.add(crearEtiquetaCampo("Motivo de Consulta *"), gbc);
        y++;

        cbVeterinario = new JComboBox<>(new String[]{
                "Dra. Camila Morales (Cirugía y Traumatología)",
                "Dr. Roberto Mendoza (Cardiología y Ecocardiografía)",
                "Dr. Mario Silva (Dermatología y Alergias)",
                "Dra. Elena Ruiz (Medicina Felina)",
                "Dr. Carlos Méndez (Urgencias y Cuidados Críticos)"
        });
        cbVeterinario.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cbVeterinario.setBackground(Color.WHITE);
        cbVeterinario.setPreferredSize(new Dimension(0, 30));

        txtMotivo = crearCampoTexto(true);
        txtMotivo.setText("Decaimiento general y revisión médica preventiva");
        gbc.gridx = 0; gbc.gridy = y;
        panel.add(cbVeterinario, gbc);
        gbc.gridx = 1; gbc.gridy = y;
        panel.add(txtMotivo, gbc);
        y++;

        // Constantes Vitales (Triada clínica)
        gbc.gridx = 0; gbc.gridy = y; gbc.gridwidth = 2;
        panel.add(crearEtiquetaCampo("Triada Clínica y Constantes Fisiológicas *"), gbc);
        y++;

        JPanel panelSignos = new JPanel(new GridLayout(1, 3, 10, 0));
        panelSignos.setOpaque(false);

        txtPeso = crearCampoTexto(true);
        txtPeso.setText("15.0");
        JPanel pPeso = new JPanel(new BorderLayout(0, 2));
        pPeso.setOpaque(false);
        JLabel lp = new JLabel("Peso (kg)");
        lp.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lp.setForeground(COLOR_TEXTO_MUTED);
        pPeso.add(lp, BorderLayout.NORTH);
        pPeso.add(txtPeso, BorderLayout.CENTER);

        txtTemperatura = crearCampoTexto(true);
        txtTemperatura.setText("38.5");
        JPanel pTemp = new JPanel(new BorderLayout(0, 2));
        pTemp.setOpaque(false);
        JLabel lt = new JLabel("Temperatura (°C)");
        lt.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lt.setForeground(COLOR_TEXTO_MUTED);
        pTemp.add(lt, BorderLayout.NORTH);
        pTemp.add(txtTemperatura, BorderLayout.CENTER);

        txtFC = crearCampoTexto(true);
        txtFC.setText("95");
        JPanel pFc = new JPanel(new BorderLayout(0, 2));
        pFc.setOpaque(false);
        JLabel lfc = new JLabel("Frec. Cardíaca (lpm)");
        lfc.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lfc.setForeground(COLOR_TEXTO_MUTED);
        pFc.add(lfc, BorderLayout.NORTH);
        pFc.add(txtFC, BorderLayout.CENTER);

        panelSignos.add(pPeso);
        panelSignos.add(pTemp);
        panelSignos.add(pFc);

        gbc.gridx = 0; gbc.gridy = y; gbc.gridwidth = 2;
        panel.add(panelSignos, gbc);
        y++;

        // Evaluación Física
        gbc.gridx = 0; gbc.gridy = y; gbc.gridwidth = 2;
        panel.add(crearEtiquetaCampo("Exploración Física / Hallazgos Clínicos *"), gbc);
        y++;

        txtEvaluacion = new JTextArea(4, 20);
        txtEvaluacion.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtEvaluacion.setLineWrap(true);
        txtEvaluacion.setWrapStyleWord(true);
        txtEvaluacion.setText("Paciente atento al entorno. Mucosas rosadas normocoloreadas. Hidratación adecuada sin pliegue cutáneo persistente. Campos pulmonares limpios bilateralmente.");
        txtEvaluacion.setBorder(new EmptyBorder(6, 8, 6, 8));

        JScrollPane spEval = new JScrollPane(txtEvaluacion);
        spEval.setBorder(BorderFactory.createLineBorder(new Color(203, 213, 225), 1));
        spEval.setPreferredSize(new Dimension(0, 75));
        gbc.gridx = 0; gbc.gridy = y; gbc.gridwidth = 2;
        panel.add(spEval, gbc);
        y++;

        // Alergias
        gbc.gridx = 0; gbc.gridy = y; gbc.gridwidth = 2;
        panel.add(crearEtiquetaCampo("Alertas de Alergias y Sensibilidades Clínicas"), gbc);
        y++;

        txtAlergias = crearCampoTexto(true);
        txtAlergias.setForeground(new Color(185, 28, 28));
        txtAlergias.setText("Sin alergias conocidas");
        gbc.gridx = 0; gbc.gridy = y; gbc.gridwidth = 2;
        panel.add(txtAlergias, gbc);

        JScrollPane scroll = new JScrollPane(panel);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(12);

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.add(scroll, BorderLayout.CENTER);
        return wrap;
    }

    private JPanel crearTabDiagnostico() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(14, 18, 14, 18));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 4, 4, 4);
        int y = 0;

        // Diagnóstico Presuntivo y Definitivo
        gbc.gridwidth = 1; gbc.weightx = 0.5;
        gbc.gridx = 0; gbc.gridy = y;
        panel.add(crearEtiquetaCampo("Diagnóstico Presuntivo"), gbc);
        gbc.gridx = 1;
        panel.add(crearEtiquetaCampo("Diagnóstico Definitivo *"), gbc);
        y++;

        txtDiagPresuntivo = crearCampoTexto(true);
        txtDiagDefinitivo = crearCampoTexto(true);
        txtDiagDefinitivo.setText("Paciente clínicamente sano / Chequeo de rutina");
        gbc.gridx = 0; gbc.gridy = y;
        panel.add(txtDiagPresuntivo, gbc);
        gbc.gridx = 1;
        panel.add(txtDiagDefinitivo, gbc);
        y++;

        // Tratamiento aplicado en clínica
        gbc.gridx = 0; gbc.gridy = y; gbc.gridwidth = 2; gbc.weightx = 1.0;
        panel.add(crearEtiquetaCampo("Tratamiento Aplicado en Clínica / Procedimiento Inmediato"), gbc);
        y++;

        txtTratamiento = new JTextArea(3, 20);
        txtTratamiento.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtTratamiento.setLineWrap(true);
        txtTratamiento.setWrapStyleWord(true);
        txtTratamiento.setText("Examen físico completo y desparasitación preventiva oral con comprimido antiparasitario de amplio espectro.");
        txtTratamiento.setBorder(new EmptyBorder(6, 8, 6, 8));

        JScrollPane spTrat = new JScrollPane(txtTratamiento);
        spTrat.setBorder(BorderFactory.createLineBorder(new Color(203, 213, 225), 1));
        spTrat.setPreferredSize(new Dimension(0, 65));
        gbc.gridx = 0; gbc.gridy = y; gbc.gridwidth = 2; gbc.weightx = 1.0;
        panel.add(spTrat, gbc);
        y++;

        // Prescripción de Receta para casa
        gbc.gridx = 0; gbc.gridy = y; gbc.gridwidth = 2; gbc.weightx = 1.0;
        panel.add(crearEtiquetaCampo("Prescripción de Medicamentos y Posología para Casa"), gbc);
        y++;

        txtReceta = new JTextArea(3, 20);
        txtReceta.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtReceta.setLineWrap(true);
        txtReceta.setWrapStyleWord(true);
        txtReceta.setText("1. Probiótico Digestivo Canino/Felino: 1 cápsula cada 24 horas con el alimento por 7 días.\n2. Complejo Multivitamínico Vet: 2.5 ml vía oral al día por 15 días.");
        txtReceta.setBorder(new EmptyBorder(6, 8, 6, 8));

        JScrollPane spRec = new JScrollPane(txtReceta);
        spRec.setBorder(BorderFactory.createLineBorder(new Color(203, 213, 225), 1));
        spRec.setPreferredSize(new Dimension(0, 65));
        gbc.gridx = 0; gbc.gridy = y; gbc.gridwidth = 2; gbc.weightx = 1.0;
        panel.add(spRec, gbc);
        y++;

        // Indicaciones para el tutor
        gbc.gridx = 0; gbc.gridy = y; gbc.gridwidth = 2; gbc.weightx = 1.0;
        panel.add(crearEtiquetaCampo("Indicaciones de Cuidado en Casa para el Tutor"), gbc);
        y++;

        txtIndicaciones = new JTextArea(2, 20);
        txtIndicaciones.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtIndicaciones.setLineWrap(true);
        txtIndicaciones.setWrapStyleWord(true);
        txtIndicaciones.setText("Mantener libre acceso a agua fresca y limpia. Ofrecer alimento balanceado de alta gama. Evitar esfuerzos excesivos las próximas 24 horas.");
        txtIndicaciones.setBorder(new EmptyBorder(6, 8, 6, 8));

        JScrollPane spInd = new JScrollPane(txtIndicaciones);
        spInd.setBorder(BorderFactory.createLineBorder(new Color(203, 213, 225), 1));
        spInd.setPreferredSize(new Dimension(0, 55));
        gbc.gridx = 0; gbc.gridy = y; gbc.gridwidth = 2; gbc.weightx = 1.0;
        panel.add(spInd, gbc);
        y++;

        // Próximo control, Costo y Estado
        gbc.gridx = 0; gbc.gridy = y; gbc.gridwidth = 2; gbc.weightx = 1.0;
        JPanel fFinal = new JPanel(new GridLayout(1, 3, 10, 0));
        fFinal.setOpaque(false);

        txtSeguimiento = crearCampoTexto(true);
        txtSeguimiento.setText("Control en 3 meses");
        JPanel pSeg = new JPanel(new BorderLayout(0, 2));
        pSeg.setOpaque(false);
        JLabel ls = new JLabel("Próximo Control");
        ls.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        ls.setForeground(COLOR_TEXTO_MUTED);
        pSeg.add(ls, BorderLayout.NORTH);
        pSeg.add(txtSeguimiento, BorderLayout.CENTER);

        txtCosto = crearCampoTexto(true);
        txtCosto.setText("85.00");
        JPanel pCos = new JPanel(new BorderLayout(0, 2));
        pCos.setOpaque(false);
        JLabel lc = new JLabel("Costo Total (S/.)");
        lc.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lc.setForeground(COLOR_TEXTO_MUTED);
        pCos.add(lc, BorderLayout.NORTH);
        pCos.add(txtCosto, BorderLayout.CENTER);

        cbEstado = new JComboBox<>(new String[]{"Completada", "En Evaluación", "Derivada a Quirófano", "Hospitalizada"});
        cbEstado.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cbEstado.setBackground(Color.WHITE);
        cbEstado.setPreferredSize(new Dimension(0, 30));
        JPanel pEst = new JPanel(new BorderLayout(0, 2));
        pEst.setOpaque(false);
        JLabel le = new JLabel("Estado Final");
        le.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        le.setForeground(COLOR_TEXTO_MUTED);
        pEst.add(le, BorderLayout.NORTH);
        pEst.add(cbEstado, BorderLayout.CENTER);

        fFinal.add(pSeg);
        fFinal.add(pCos);
        fFinal.add(pEst);

        panel.add(fFinal, gbc);

        JScrollPane scroll = new JScrollPane(panel);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(12);

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.add(scroll, BorderLayout.CENTER);
        return wrap;
    }

    private void autocompletarDatosPaciente() {
        PacienteItem item = (PacienteItem) cbPaciente.getSelectedItem();
        if (item != null && item.mascota != null) {
            Mascota m = item.mascota;
            Optional<Cliente> optC = repo.getClienteDeMascota(m.getCodigo());
            optC.ifPresent(c -> {
                txtTutor.setText(c.getNombreCompleto());
                txtTelefono.setText(c.getTelefonoPrincipal());
            });
            if (atencionExistente == null) {
                txtPeso.setText(String.valueOf(m.getPesoActualKg()));
            }
            if (!m.getAlergias().equalsIgnoreCase("Ninguna conocida")) {
                txtAlergias.setText("ALERTA: " + m.getAlergias());
                txtAlergias.setForeground(new Color(220, 38, 38));
            } else {
                txtAlergias.setText("Sin alergias conocidas");
                txtAlergias.setForeground(new Color(22, 163, 74));
            }
        }
    }

    private void preseleccionarMascota(Mascota m) {
        if (m == null) return;
        for (int i = 0; i < cbPaciente.getItemCount(); i++) {
            PacienteItem item = cbPaciente.getItemAt(i);
            if (item != null && item.mascota != null && item.mascota.getCodigo().equalsIgnoreCase(m.getCodigo())) {
                cbPaciente.setSelectedIndex(i);
                autocompletarDatosPaciente();
                break;
            }
        }
    }

    private void cargarDatosExistentes() {
        // Seleccionar paciente
        for (int i = 0; i < cbPaciente.getItemCount(); i++) {
            PacienteItem item = cbPaciente.getItemAt(i);
            if (item != null && item.mascota != null && item.mascota.getCodigo().equalsIgnoreCase(atencionExistente.getCodigoMascota())) {
                cbPaciente.setSelectedIndex(i);
                break;
            }
        }
        cbPaciente.setEnabled(false);

        txtTutor.setText(atencionExistente.getNombreTutor());
        txtTelefono.setText(atencionExistente.getTelefonoTutor());
        txtMotivo.setText(atencionExistente.getMotivoConsulta());
        txtPeso.setText(String.valueOf(atencionExistente.getPesoKg()));
        txtTemperatura.setText(String.valueOf(atencionExistente.getTemperaturaC()));
        txtFC.setText(String.valueOf(atencionExistente.getFrecuenciaCardiacaLpm()));
        txtEvaluacion.setText(atencionExistente.getEvaluacionClinica());
        txtDiagPresuntivo.setText(atencionExistente.getDiagnosticoPresuntivo());
        txtDiagDefinitivo.setText(atencionExistente.getDiagnosticoDefinitivo());
        txtTratamiento.setText(atencionExistente.getTratamientoIndicado());
        txtReceta.setText(atencionExistente.getRecetaMedicamentos());
        txtIndicaciones.setText(atencionExistente.getIndicacionesTutor());
        txtSeguimiento.setText(atencionExistente.getSeguimiento());
        txtCosto.setText(String.format("%.2f", atencionExistente.getCosto()));
        cbEstado.setSelectedItem(atencionExistente.getEstado());

        String vet = atencionExistente.getVeterinario();
        for (int i = 0; i < cbVeterinario.getItemCount(); i++) {
            if (cbVeterinario.getItemAt(i).contains(vet)) {
                cbVeterinario.setSelectedIndex(i);
                break;
            }
        }
    }

    private void guardarConsulta() {
        PacienteItem item = (PacienteItem) cbPaciente.getSelectedItem();
        if (item == null || item.mascota == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un paciente veterinario.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Mascota m = item.mascota;
        String motivo = txtMotivo.getText().trim();
        if (motivo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El motivo de la consulta es obligatorio.", "Validación", JOptionPane.WARNING_MESSAGE);
            txtMotivo.requestFocus();
            return;
        }

        String diagDef = txtDiagDefinitivo.getText().trim();
        if (diagDef.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El diagnóstico definitivo o clínico es obligatorio.", "Validación", JOptionPane.WARNING_MESSAGE);
            txtDiagDefinitivo.requestFocus();
            return;
        }

        double peso = 10.0;
        double temp = 38.5;
        int fc = 100;
        double costo = 80.0;

        try {
            peso = Double.parseDouble(txtPeso.getText().trim());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "El peso ingresado no es válido (ej: 14.5).", "Formato Inválido", JOptionPane.WARNING_MESSAGE);
            txtPeso.requestFocus();
            return;
        }

        try {
            temp = Double.parseDouble(txtTemperatura.getText().trim());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "La temperatura ingresada no es válida (ej: 38.5).", "Formato Inválido", JOptionPane.WARNING_MESSAGE);
            txtTemperatura.requestFocus();
            return;
        }

        try {
            fc = Integer.parseInt(txtFC.getText().trim());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "La frecuencia cardíaca debe ser un número entero (ej: 95).", "Formato Inválido", JOptionPane.WARNING_MESSAGE);
            txtFC.requestFocus();
            return;
        }

        try {
            costo = Double.parseDouble(txtCosto.getText().trim());
        } catch (Exception ex) {
            costo = 80.0;
        }

        String vetSel = (String) cbVeterinario.getSelectedItem();
        String vetNombre = (vetSel != null && vetSel.contains("(")) ? vetSel.substring(0, vetSel.indexOf("(")).trim() : vetSel;

        AtencionMedica atencion;
        if (atencionExistente != null) {
            atencion = atencionExistente;
            atencion.setVeterinario(vetNombre);
            atencion.setMotivoConsulta(motivo);
            atencion.setPesoKg(peso);
            atencion.setTemperaturaC(temp);
            atencion.setFrecuenciaCardiacaLpm(fc);
            atencion.setEvaluacionClinica(txtEvaluacion.getText().trim());
            atencion.setDiagnosticoPresuntivo(txtDiagPresuntivo.getText().trim());
            atencion.setDiagnosticoDefinitivo(diagDef);
            atencion.setTratamientoIndicado(txtTratamiento.getText().trim());
            atencion.setRecetaMedicamentos(txtReceta.getText().trim());
            atencion.setIndicacionesTutor(txtIndicaciones.getText().trim());
            atencion.setSeguimiento(txtSeguimiento.getText().trim());
            atencion.setEstado((String) cbEstado.getSelectedItem());
            atencion.setCosto(costo);
            repo.guardarAtencionMedica(atencion);
        } else {
            String idNueva = "CM-2024-" + String.format("%03d", repo.getAtencionesMedicas().size() + 1);
            atencion = new AtencionMedica(
                    idNueva,
                    m.getCodigo(),
                    m.getNombre(),
                    m.getEspecie() + " · " + m.getRaza(),
                    "",
                    txtTutor.getText().trim(),
                    txtTelefono.getText().trim(),
                    LocalDate.now(),
                    LocalTime.now(),
                    vetNombre,
                    motivo,
                    peso,
                    temp,
                    fc,
                    txtEvaluacion.getText().trim(),
                    txtDiagPresuntivo.getText().trim(),
                    diagDef,
                    txtTratamiento.getText().trim(),
                    txtReceta.getText().trim(),
                    txtIndicaciones.getText().trim(),
                    txtSeguimiento.getText().trim(),
                    (String) cbEstado.getSelectedItem(),
                    costo
            );
            repo.guardarAtencionMedica(atencion);
        }

        // Actualizar el peso actual de la mascota en el repositorio si cambió
        m.setPesoActualKg(peso);

        if (onGuardado != null) {
            onGuardado.accept(atencion);
        }

        dispose();
    }

    private JLabel crearEtiquetaCampo(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lbl.setForeground(COLOR_TEXTO_TITULO);
        lbl.setBorder(new EmptyBorder(3, 0, 2, 0));
        return lbl;
    }

    private JTextField crearCampoTexto(boolean editable) {
        JTextField txt = new JTextField();
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txt.setPreferredSize(new Dimension(0, 30));
        txt.setEditable(editable);
        txt.setBackground(editable ? Color.WHITE : new Color(248, 250, 252));
        txt.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225), 1),
                new EmptyBorder(4, 8, 4, 8)
        ));
        return txt;
    }
}
