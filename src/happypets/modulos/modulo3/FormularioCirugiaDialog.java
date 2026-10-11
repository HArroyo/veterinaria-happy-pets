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
import javax.swing.border.EmptyBorder;

import happypets.data.RepositorioVeterinaria;
import happypets.model.Cliente;
import happypets.model.Mascota;
import happypets.model.RegistroCirugia;
import happypets.ui.Iconos;
import happypets.ui.Ui;

/**
 * Diálogo modal profesional para la programación de cirugías, registro del acta
 * intraoperatoria y seguimiento postquirúrgico (Submódulo 3.3).
 */
public class FormularioCirugiaDialog extends JDialog {
    private static final long serialVersionUID = 1L;

    private static final Color COLOR_AZUL_PRIMARIO = Ui.TURQUESA;
    private static final Color COLOR_BORDE = Ui.BORDE_SUAVE;
    private static final Color COLOR_TEXTO_TITULO = Ui.TEXTO_TITULO;
    private static final Color COLOR_TEXTO_MUTED = Ui.TEXTO_MUTED;

    private final RepositorioVeterinaria repo = RepositorioVeterinaria.getInstancia();
    private final Consumer<RegistroCirugia> onGuardado;
    private final RegistroCirugia cirugiaExistente;

    // Tab 1: Programación
    private JComboBox<PacienteItem> cbPaciente;
    private JTextField txtTutor;
    private JTextField txtTelefono;
    private JComboBox<String> cbProcedimiento;
    private JComboBox<String> cbQuirofano;
    private JComboBox<String> cbCirujano;
    private JTextField txtPersonalApoyo;
    private JTextField txtFecha;
    private JTextField txtHora;

    // Tab 2: Intraoperatorio & Anestesia
    private JComboBox<String> cbTipoAnestesia;
    private JTextField txtHoraInicio;
    private JTextField txtHoraFin;
    private JTextArea txtMedicamentos;
    private JTextArea txtObservaciones;

    // Tab 3: Postoperatorio & Alta
    private JComboBox<String> cbEstado;
    private JComboBox<String> cbEscalaDolor;
    private JComboBox<String> cbEstadoGeneral;
    private JTextField txtFechaPuntos;
    private JTextArea txtIndicaciones;

    public static class PacienteItem {
        public final Mascota mascota;
        public PacienteItem(Mascota mascota) { this.mascota = mascota; }
        @Override
        public String toString() {
            return mascota != null ? mascota.getCodigo() + " - " + mascota.getNombre() + " (" + mascota.getEspecie() + " · " + mascota.getRaza() + ")" : "Seleccione paciente...";
        }
    }

    public FormularioCirugiaDialog(Window parent, RegistroCirugia cirugiaExistente, Mascota mascotaInicial, Consumer<RegistroCirugia> onGuardado) {
        super(parent, cirugiaExistente == null ? "Programar Intervención Quirúrgica" : "Editar Cirugía " + cirugiaExistente.getIdCirugia(), ModalityType.APPLICATION_MODAL);
        this.cirugiaExistente = cirugiaExistente;
        this.onGuardado = onGuardado;

        setSize(700, 680);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        // Cabecera Quirúrgica
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(15, 118, 110)); // Turquesa profundo quirúrgico
        header.setBorder(new EmptyBorder(16, 20, 16, 20));

        JPanel headerTxt = new JPanel();
        headerTxt.setOpaque(false);
        headerTxt.setLayout(new BoxLayout(headerTxt, BoxLayout.Y_AXIS));

        JLabel title = new JLabel(cirugiaExistente == null ? "Programación y Protocolo Quirúrgico" : "Modificación de Intervención Quirúrgica");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(Color.WHITE);
        title.setIcon(Iconos.crearIconoBisturi(20, Color.WHITE));
        title.setIconTextGap(8);

        JLabel sub = new JLabel(cirugiaExistente == null ?
                "Control de quirófano, protocolo anestésico, medicamentos y postoperatorio." :
                "ID Cirugía: " + cirugiaExistente.getIdCirugia() + " · Paciente: " + cirugiaExistente.getNombreMascota());
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sub.setForeground(new Color(204, 251, 241));

        headerTxt.add(title);
        headerTxt.add(Box.createVerticalStrut(2));
        headerTxt.add(sub);
        header.add(headerTxt, BorderLayout.CENTER);
        add(header, BorderLayout.NORTH);

        // Tabs
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabs.setBackground(Color.WHITE);

        tabs.addTab("1. Programación y Quirófano", Iconos.crearIconoCalendario(14, COLOR_AZUL_PRIMARIO), crearTabProgramacion());
        tabs.addTab("2. Protocolo Intraoperatorio", Iconos.crearIconoBisturi(14, new Color(220, 38, 38)), crearTabIntraoperatorio());
        tabs.addTab("3. Postoperatorio y Alta", Iconos.crearIconoCheck(14, new Color(22, 163, 74)), crearTabPostoperatorio());

        add(tabs, BorderLayout.CENTER);

        // Footer
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

        JButton btnGuardar = new JButton(cirugiaExistente == null ? "Guardar Cirugía" : "Actualizar Intervención");
        btnGuardar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setBackground(COLOR_AZUL_PRIMARIO);
        btnGuardar.setIcon(Iconos.crearIconoCheck(14, Color.WHITE));
        btnGuardar.setIconTextGap(6);
        btnGuardar.setFocusPainted(false);
        btnGuardar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnGuardar.setPreferredSize(new Dimension(195, 34));
        btnGuardar.addActionListener(e -> guardarCirugia());

        footer.add(btnCancelar);
        footer.add(btnGuardar);
        add(footer, BorderLayout.SOUTH);

        if (cirugiaExistente != null) {
            cargarDatosExistentes();
        } else if (mascotaInicial != null) {
            preseleccionarMascota(mascotaInicial);
        } else {
            autocompletarDatosPaciente();
        }
    }

    private JPanel crearTabProgramacion() {
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

        // Procedimiento y Quirófano
        gbc.gridx = 0; gbc.gridy = y;
        panel.add(crearEtiquetaCampo("Procedimiento Quirúrgico *"), gbc);
        gbc.gridx = 1; gbc.gridy = y;
        panel.add(crearEtiquetaCampo("Quirófano Asignado *"), gbc);
        y++;

        String[] procs = {
                "Ovariohisterectomía Canina / Felina (Esterilización)",
                "Castración / Orquiectomía",
                "Profilaxis Dental + Extracciones Quirúrgicas",
                "Excisión de Neoplasia / Tumor Subcutáneo",
                "Osteosíntesis Traumatológica",
                "Cesárea de Urgencia",
                "Enterotomía por Cuerpo Extraño",
                "Hernioplastia Umbilical / Inguinal"
        };
        cbProcedimiento = new JComboBox<>(procs);
        cbProcedimiento.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cbProcedimiento.setBackground(Color.WHITE);
        cbProcedimiento.setEditable(true);
        cbProcedimiento.setPreferredSize(new Dimension(0, 30));

        cbQuirofano = new JComboBox<>(new String[]{"Quirófano 1 (Cirugía Mayor)", "Quirófano 2 (Procedimientos)"});
        cbQuirofano.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cbQuirofano.setBackground(Color.WHITE);
        cbQuirofano.setPreferredSize(new Dimension(0, 30));

        gbc.gridx = 0; gbc.gridy = y;
        panel.add(cbProcedimiento, gbc);
        gbc.gridx = 1; gbc.gridy = y;
        panel.add(cbQuirofano, gbc);
        y++;

        // Cirujano y Personal de Apoyo
        gbc.gridx = 0; gbc.gridy = y;
        panel.add(crearEtiquetaCampo("Cirujano Principal Responsable *"), gbc);
        gbc.gridx = 1; gbc.gridy = y;
        panel.add(crearEtiquetaCampo("Personal de Apoyo / Instrumentista"), gbc);
        y++;

        cbCirujano = new JComboBox<>(new String[]{
                "Dra. Camila Morales (Cirugía General y Especial)",
                "Dr. Roberto Mendoza (Cardiocirugía y Torácica)",
                "Dra. Elena Ruiz (Cirugía de Pequeños Animales)",
                "Dr. Carlos Méndez (Cirugía Traumatológica)"
        });
        cbCirujano.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cbCirujano.setBackground(Color.WHITE);
        cbCirujano.setPreferredSize(new Dimension(0, 30));

        txtPersonalApoyo = crearCampoTexto(true);
        txtPersonalApoyo.setText("Marta Sánchez (ATV Quirófano)");

        gbc.gridx = 0; gbc.gridy = y;
        panel.add(cbCirujano, gbc);
        gbc.gridx = 1; gbc.gridy = y;
        panel.add(txtPersonalApoyo, gbc);
        y++;

        // Fecha y Hora
        gbc.gridx = 0; gbc.gridy = y;
        panel.add(crearEtiquetaCampo("Fecha Programada (dd/MM/yyyy) *"), gbc);
        gbc.gridx = 1; gbc.gridy = y;
        panel.add(crearEtiquetaCampo("Hora Programada (HH:mm) *"), gbc);
        y++;

        txtFecha = crearCampoTexto(true);
        txtFecha.setText(LocalDate.now().format(RegistroCirugia.FECHA_FORMATTER));

        txtHora = crearCampoTexto(true);
        txtHora.setText("09:00");

        gbc.gridx = 0; gbc.gridy = y;
        panel.add(txtFecha, gbc);
        gbc.gridx = 1; gbc.gridy = y;
        panel.add(txtHora, gbc);

        JScrollPane scroll = new JScrollPane(panel);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(12);

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.add(scroll, BorderLayout.CENTER);
        return wrap;
    }

    private JPanel crearTabIntraoperatorio() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(14, 18, 14, 18));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 4, 4, 4);
        int y = 0;

        // Anestesia y Tiempos
        gbc.gridx = 0; gbc.gridy = y; gbc.gridwidth = 2; gbc.weightx = 1.0;
        panel.add(crearEtiquetaCampo("Protocolo Anestésico Empleado *"), gbc);
        y++;

        cbTipoAnestesia = new JComboBox<>(new String[]{
                "Inhalatoria Isoflurano + Premedicación Midazolam/Morfina",
                "Intravenosa Total TIVA (Propofol + Fentanilo)",
                "Sedación Profunda + Bloqueo Local Infiltrativo",
                "Epidural L-S + Sedación ligera"
        });
        cbTipoAnestesia.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cbTipoAnestesia.setBackground(Color.WHITE);
        cbTipoAnestesia.setPreferredSize(new Dimension(0, 30));

        gbc.gridx = 0; gbc.gridy = y; gbc.gridwidth = 2; gbc.weightx = 1.0;
        panel.add(cbTipoAnestesia, gbc);
        y++;

        // Horarios Reales
        gbc.gridwidth = 1; gbc.weightx = 0.5;
        gbc.gridx = 0; gbc.gridy = y;
        panel.add(crearEtiquetaCampo("Hora Inicio Real (HH:mm)"), gbc);
        gbc.gridx = 1; gbc.gridy = y;
        panel.add(crearEtiquetaCampo("Hora Fin Real (HH:mm)"), gbc);
        y++;

        txtHoraInicio = crearCampoTexto(true);
        txtHoraInicio.setText("09:15");

        txtHoraFin = crearCampoTexto(true);
        txtHoraFin.setText("10:30");

        gbc.gridx = 0; gbc.gridy = y;
        panel.add(txtHoraInicio, gbc);
        gbc.gridx = 1; gbc.gridy = y;
        panel.add(txtHoraFin, gbc);
        y++;

        // Medicamentos e Insumos
        gbc.gridx = 0; gbc.gridy = y; gbc.gridwidth = 2; gbc.weightx = 1.0;
        panel.add(crearEtiquetaCampo("Medicamentos e Insumos Administrados en Quirófano"), gbc);
        y++;

        txtMedicamentos = new JTextArea(3, 20);
        txtMedicamentos.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtMedicamentos.setLineWrap(true);
        txtMedicamentos.setWrapStyleWord(true);
        txtMedicamentos.setText("Cefazolina 22mg/kg IV pre-incisión, Meloxicam 0.2mg/kg SC, Tramadol 2mg/kg IV lento, Ringer Lactato a 5ml/kg/h.");
        txtMedicamentos.setBorder(new EmptyBorder(6, 8, 6, 8));

        JScrollPane spMed = new JScrollPane(txtMedicamentos);
        spMed.setBorder(BorderFactory.createLineBorder(new Color(203, 213, 225), 1));
        spMed.setPreferredSize(new Dimension(0, 65));

        gbc.gridx = 0; gbc.gridy = y; gbc.gridwidth = 2; gbc.weightx = 1.0;
        panel.add(spMed, gbc);
        y++;

        // Observaciones y Hallazgos
        gbc.gridx = 0; gbc.gridy = y; gbc.gridwidth = 2; gbc.weightx = 1.0;
        panel.add(crearEtiquetaCampo("Observaciones Intraquirúrgicas y Hallazgos"), gbc);
        y++;

        txtObservaciones = new JTextArea(3, 20);
        txtObservaciones.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtObservaciones.setLineWrap(true);
        txtObservaciones.setWrapStyleWord(true);
        txtObservaciones.setText("Hemostasia rigurosa. Parámetros hemodinámicos estables (PAM > 70 mmHg, SpO2 99%). Cierre por planos con sutura monofilamento absorbible.");
        txtObservaciones.setBorder(new EmptyBorder(6, 8, 6, 8));

        JScrollPane spObs = new JScrollPane(txtObservaciones);
        spObs.setBorder(BorderFactory.createLineBorder(new Color(203, 213, 225), 1));
        spObs.setPreferredSize(new Dimension(0, 65));

        gbc.gridx = 0; gbc.gridy = y; gbc.gridwidth = 2; gbc.weightx = 1.0;
        panel.add(spObs, gbc);

        JScrollPane scroll = new JScrollPane(panel);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(12);

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.add(scroll, BorderLayout.CENTER);
        return wrap;
    }

    private JPanel crearTabPostoperatorio() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(14, 18, 14, 18));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 4, 4, 4);
        int y = 0;

        // Estado y Escala de Dolor
        gbc.gridwidth = 1; gbc.weightx = 0.5;
        gbc.gridx = 0; gbc.gridy = y;
        panel.add(crearEtiquetaCampo("Fase / Estado Quirúrgico *"), gbc);
        gbc.gridx = 1; gbc.gridy = y;
        panel.add(crearEtiquetaCampo("Escala de Dolor Postoperatorio (1 a 10)"), gbc);
        y++;

        cbEstado = new JComboBox<>(new String[]{"Programada", "En Proceso", "En Recuperación", "Alta Quirúrgica"});
        cbEstado.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cbEstado.setBackground(Color.WHITE);
        cbEstado.setPreferredSize(new Dimension(0, 30));

        cbEscalaDolor = new JComboBox<>(new String[]{
                "1 - Dolor Ausente / Mínimo",
                "2 - Dolor Leve Controlado",
                "3 - Dolor Moderado Leve",
                "4 - Dolor Moderado",
                "5 - Dolor Significativo",
                "6 - Dolor Intenso Requiere Rescate"
        });
        cbEscalaDolor.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cbEscalaDolor.setBackground(Color.WHITE);
        cbEscalaDolor.setPreferredSize(new Dimension(0, 30));

        gbc.gridx = 0; gbc.gridy = y;
        panel.add(cbEstado, gbc);
        gbc.gridx = 1; gbc.gridy = y;
        panel.add(cbEscalaDolor, gbc);
        y++;

        // Estado General y Retiro de Puntos
        gbc.gridx = 0; gbc.gridy = y;
        panel.add(crearEtiquetaCampo("Estado General del Paciente al Alta"), gbc);
        gbc.gridx = 1; gbc.gridy = y;
        panel.add(crearEtiquetaCampo("Fecha Retiro de Puntos / Control (dd/MM/yyyy)"), gbc);
        y++;

        cbEstadoGeneral = new JComboBox<>(new String[]{
                "Alerta, reflejos activos y normotérmico",
                "En recuperación anestésica en Box",
                "Hospitalizado para monitorización continua 24h"
        });
        cbEstadoGeneral.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cbEstadoGeneral.setBackground(Color.WHITE);
        cbEstadoGeneral.setPreferredSize(new Dimension(0, 30));

        txtFechaPuntos = crearCampoTexto(true);
        txtFechaPuntos.setText(LocalDate.now().plusDays(10).format(RegistroCirugia.FECHA_FORMATTER));

        gbc.gridx = 0; gbc.gridy = y;
        panel.add(cbEstadoGeneral, gbc);
        gbc.gridx = 1; gbc.gridy = y;
        panel.add(txtFechaPuntos, gbc);
        y++;

        // Indicaciones Postop para el Tutor
        gbc.gridx = 0; gbc.gridy = y; gbc.gridwidth = 2; gbc.weightx = 1.0;
        panel.add(crearEtiquetaCampo("Indicaciones Postoperatorias y Cuidados Domiciliarios"), gbc);
        y++;

        txtIndicaciones = new JTextArea(3, 20);
        txtIndicaciones.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtIndicaciones.setLineWrap(true);
        txtIndicaciones.setWrapStyleWord(true);
        txtIndicaciones.setText("Uso obligatorio de collar isabelino por 10 días. Reposo absoluto sin saltos ni juegos bruscos. Administrar antibiótico y analgésico según receta médica adjunta.");
        txtIndicaciones.setBorder(new EmptyBorder(6, 8, 6, 8));

        JScrollPane spInd = new JScrollPane(txtIndicaciones);
        spInd.setBorder(BorderFactory.createLineBorder(new Color(203, 213, 225), 1));
        spInd.setPreferredSize(new Dimension(0, 75));

        gbc.gridx = 0; gbc.gridy = y; gbc.gridwidth = 2; gbc.weightx = 1.0;
        panel.add(spInd, gbc);

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
            if (optC.isPresent()) {
                Cliente c = optC.get();
                txtTutor.setText(c.getNombreCompleto());
                txtTelefono.setText(c.getTelefonoPrincipal());
            } else {
                txtTutor.setText("Tutor no registrado");
                txtTelefono.setText("-");
            }
        }
    }

    private void preseleccionarMascota(Mascota mInicial) {
        for (int i = 0; i < cbPaciente.getItemCount(); i++) {
            PacienteItem it = cbPaciente.getItemAt(i);
            if (it != null && it.mascota != null && it.mascota.getCodigo().equalsIgnoreCase(mInicial.getCodigo())) {
                cbPaciente.setSelectedIndex(i);
                break;
            }
        }
    }

    private void cargarDatosExistentes() {
        RegistroCirugia c = cirugiaExistente;
        for (int i = 0; i < cbPaciente.getItemCount(); i++) {
            PacienteItem it = cbPaciente.getItemAt(i);
            if (it != null && it.mascota != null && it.mascota.getCodigo().equalsIgnoreCase(c.getCodigoMascota())) {
                cbPaciente.setSelectedIndex(i);
                break;
            }
        }
        cbPaciente.setEnabled(false);

        txtTutor.setText(c.getNombreTutor());
        txtTelefono.setText(c.getTelefonoTutor());

        cbProcedimiento.setSelectedItem(c.getProcedimiento());
        cbQuirofano.setSelectedItem(c.getQuirofano());

        for (int i = 0; i < cbCirujano.getItemCount(); i++) {
            if (cbCirujano.getItemAt(i).contains(c.getCirujanoPrincipal())) {
                cbCirujano.setSelectedIndex(i);
                break;
            }
        }

        txtPersonalApoyo.setText(c.getPersonalApoyo());
        txtFecha.setText(c.getFechaFormateada());
        txtHora.setText(c.getHoraProgramadaFormateada());

        cbTipoAnestesia.setSelectedItem(c.getTipoAnestesia());
        txtHoraInicio.setText(c.getHoraInicioFormateada());
        txtHoraFin.setText(c.getHoraFinFormateada());
        txtMedicamentos.setText(c.getMedicamentosInsumos());
        txtObservaciones.setText(c.getObservacionesProtocolo());

        cbEstado.setSelectedItem(c.getEstado());
        cbEstadoGeneral.setSelectedItem(c.getEstadoGeneralAlta());
        txtFechaPuntos.setText(c.getFechaRetiroPuntosFormateada());
        txtIndicaciones.setText(c.getIndicacionesPostop());
    }

    private void guardarCirugia() {
        PacienteItem item = (PacienteItem) cbPaciente.getSelectedItem();
        if (item == null || item.mascota == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un paciente veterinario.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String proc = cbProcedimiento.getSelectedItem() != null ? cbProcedimiento.getSelectedItem().toString().trim() : "";
        if (proc.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe especificar el procedimiento quirúrgico.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        LocalDate fProg = LocalDate.now();
        try {
            fProg = LocalDate.parse(txtFecha.getText().trim(), RegistroCirugia.FECHA_FORMATTER);
        } catch (Exception ignored) {}

        LocalTime hProg = LocalTime.of(9, 0);
        try {
            hProg = LocalTime.parse(txtHora.getText().trim(), RegistroCirugia.HORA_FORMATTER);
        } catch (Exception ignored) {}

        LocalTime hIni = null;
        try {
            if (!txtHoraInicio.getText().trim().isEmpty() && !txtHoraInicio.getText().trim().equals("--:--")) {
                hIni = LocalTime.parse(txtHoraInicio.getText().trim(), RegistroCirugia.HORA_FORMATTER);
            }
        } catch (Exception ignored) {}

        LocalTime hFin = null;
        try {
            if (!txtHoraFin.getText().trim().isEmpty() && !txtHoraFin.getText().trim().equals("--:--")) {
                hFin = LocalTime.parse(txtHoraFin.getText().trim(), RegistroCirugia.HORA_FORMATTER);
            }
        } catch (Exception ignored) {}

        LocalDate fPuntos = null;
        try {
            if (!txtFechaPuntos.getText().trim().isEmpty() && !txtFechaPuntos.getText().trim().equals("-")) {
                fPuntos = LocalDate.parse(txtFechaPuntos.getText().trim(), RegistroCirugia.FECHA_FORMATTER);
            }
        } catch (Exception ignored) {}

        int escalaDolor = 2;
        try {
            String selEsc = (String) cbEscalaDolor.getSelectedItem();
            if (selEsc != null && selEsc.contains("-")) {
                escalaDolor = Integer.parseInt(selEsc.substring(0, selEsc.indexOf("-")).trim());
            }
        } catch (Exception ignored) {}

        Mascota m = item.mascota;
        String cirStr = cbCirujano.getSelectedItem() != null ? cbCirujano.getSelectedItem().toString() : "Dra. Camila Morales";
        String cirLimpio = cirStr.contains("(") ? cirStr.substring(0, cirStr.indexOf("(")).trim() : cirStr;

        RegistroCirugia rc;
        if (cirugiaExistente != null) {
            rc = cirugiaExistente;
        } else {
            rc = new RegistroCirugia("", m.getCodigo(), m.getNombre(), m.getEspecie() + " · " + m.getRaza(),
                    txtTutor.getText().trim(), txtTelefono.getText().trim(), proc,
                    (String) cbQuirofano.getSelectedItem(), cirLimpio, txtPersonalApoyo.getText().trim(),
                    fProg, hProg, hIni, hFin, (String) cbTipoAnestesia.getSelectedItem(),
                    txtMedicamentos.getText().trim(), txtObservaciones.getText().trim(),
                    escalaDolor, (String) cbEstadoGeneral.getSelectedItem(), txtIndicaciones.getText().trim(),
                    fPuntos, (String) cbEstado.getSelectedItem());
        }

        rc.setProcedimiento(proc);
        rc.setQuirofano((String) cbQuirofano.getSelectedItem());
        rc.setCirujanoPrincipal(cirLimpio);
        rc.setPersonalApoyo(txtPersonalApoyo.getText().trim());
        rc.setFecha(fProg);
        rc.setHoraProgramada(hProg);
        rc.setHoraInicioReal(hIni);
        rc.setHoraFinReal(hFin);
        rc.setTipoAnestesia((String) cbTipoAnestesia.getSelectedItem());
        rc.setMedicamentosInsumos(txtMedicamentos.getText().trim());
        rc.setObservacionesProtocolo(txtObservaciones.getText().trim());
        rc.setEscalaDolorPostop(escalaDolor);
        rc.setEstadoGeneralAlta((String) cbEstadoGeneral.getSelectedItem());
        rc.setIndicacionesPostop(txtIndicaciones.getText().trim());
        rc.setFechaRetiroPuntos(fPuntos);
        rc.setEstado((String) cbEstado.getSelectedItem());

        repo.guardarCirugia(rc);

        if (onGuardado != null) {
            onGuardado.accept(rc);
        }

        dispose();
    }

    private JLabel crearEtiquetaCampo(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(new Font("Segoe UI", Font.BOLD, 11));
        l.setForeground(new Color(51, 65, 85));
        return l;
    }

    private JTextField crearCampoTexto(boolean editable) {
        JTextField tf = new JTextField();
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tf.setPreferredSize(new Dimension(0, 30));
        tf.setEditable(editable);
        tf.setBackground(editable ? Color.WHITE : new Color(248, 250, 252));
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225), 1),
                new EmptyBorder(4, 8, 4, 8)
        ));
        return tf;
    }
}
