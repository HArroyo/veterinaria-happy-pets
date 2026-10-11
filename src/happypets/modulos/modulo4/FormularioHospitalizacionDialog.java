package happypets.modulos.modulo4;

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
import java.awt.Window;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

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
import happypets.model.Cliente;
import happypets.model.InternamientoHospitalario;
import happypets.model.Mascota;
import happypets.ui.Iconos;
import happypets.ui.Ui;

/**
 * Diálogo modal profesional para registrar o editar un expediente hospitalario.
 * Organizado en dos pestañas clínicas:
 *  1. Ingreso & Ubicación de Canil / Box
 *  2. Constantes Vitales & Terapia Intensiva
 */
public class FormularioHospitalizacionDialog extends JDialog {
    private static final long serialVersionUID = 1L;

    private final RepositorioVeterinaria repo = RepositorioVeterinaria.getInstancia();
    private boolean guardado = false;
    private InternamientoHospitalario internamientoResultante = null;
    private final InternamientoHospitalario internamientoOriginal;

    // Componentes - Tab 1
    private JComboBox<PacienteItem> cbPaciente;
    private JTextField txtTutor;
    private JTextField txtTelefono;
    private JComboBox<String> cbBoxAsignado;
    private JTextField txtDiagnostico;
    private JTextField txtVeterinario;
    private JComboBox<String> cbNivelAlerta;
    private JTextField txtCostoDia;
    private JComboBox<String> cbEstado;

    // Componentes - Tab 2
    private JTextField txtPeso;
    private JTextField txtTemperatura;
    private JTextField txtFC;
    private JTextArea txtFluidoterapia;
    private JTextArea txtMedicacion;
    private JTextArea txtEvolucion;

    private static final String[] BOXES = {
            "Box 01", "Box 02", "Box 03",
            "Box 04", "Box 05", "Box 06",
            "Box 07", "Box 08", "Box 09"
    };

    public FormularioHospitalizacionDialog(Window owner) {
        this(owner, null);
    }

    public FormularioHospitalizacionDialog(Window owner, InternamientoHospitalario edicion) {
        super(owner, edicion == null ? "Registrar Ingreso a Hospitalización / UCI" : "Editar Expediente Hospitalario: " + edicion.getIdInternamiento(), ModalityType.APPLICATION_MODAL);
        this.internamientoOriginal = edicion;

        setSize(650, 580);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(248, 250, 252));

        construirUi();
        if (edicion != null) {
            cargarDatosExistentes(edicion);
        }
    }

    private void construirUi() {
        // Cabecera estilizada
        JPanel cabecera = new JPanel(new BorderLayout());
        cabecera.setBackground(Color.WHITE);
        cabecera.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Ui.BORDE_SUAVE),
                new EmptyBorder(16, 20, 16, 20)
        ));

        JPanel titBox = new JPanel();
        titBox.setOpaque(false);
        titBox.setLayout(new BoxLayout(titBox, BoxLayout.Y_AXIS));

        JLabel lblTit = new JLabel(internamientoOriginal == null ? "Ingreso Hospitalario & Cuidados Críticos" : "Modificar Expediente · " + internamientoOriginal.getIdInternamiento());
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTit.setForeground(Ui.TEXTO_TITULO);

        JLabel lblSub = new JLabel("Asignación de caniles climatizados, fluidoterapia, monitorización y protocolos de soporte");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(Ui.TEXTO_MUTED);

        titBox.add(lblTit);
        titBox.add(Box.createVerticalStrut(3));
        titBox.add(lblSub);

        JLabel iconLabel = new JLabel(Iconos.crearIconoCamaHospital(28, Ui.COLOR_PELIGRO));
        cabecera.add(titBox, BorderLayout.CENTER);
        cabecera.add(iconLabel, BorderLayout.EAST);
        add(cabecera, BorderLayout.NORTH);

        // Pestañas
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabs.setBackground(Color.WHITE);

        tabs.addTab("1. Ingreso & Asignación Box", Iconos.crearIconoCamaHospital(14, Ui.TURQUESA), crearTabIngreso());
        tabs.addTab("2. Constantes & Terapia UCI", Iconos.crearIconoAlertaTriaje(14, Ui.COLOR_PELIGRO), crearTabTerapia());
        add(tabs, BorderLayout.CENTER);

        // Barra inferior de botones
        JPanel barraBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        barraBotones.setBackground(Color.WHITE);
        barraBotones.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Ui.BORDE_SUAVE));

        JButton btnCancelar = crearBotonAccion("Cancelar", false);
        btnCancelar.addActionListener(e -> dispose());

        JButton btnGuardar = crearBotonAccion("Guardar Ingreso Hospitalario", true);
        btnGuardar.addActionListener(e -> guardarIngreso());

        barraBotones.add(btnCancelar);
        barraBotones.add(btnGuardar);
        add(barraBotones, BorderLayout.SOUTH);
    }

    private JPanel crearTabIngreso() {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(new EmptyBorder(16, 20, 16, 20));

        // Selector de Paciente
        cbPaciente = new JComboBox<>();
        cbPaciente.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cbPaciente.setBackground(Color.WHITE);
        cbPaciente.setPreferredSize(new Dimension(0, 30));
        for (Mascota m : repo.todasLasMascotas()) {
            cbPaciente.addItem(new PacienteItem(m));
        }
        cbPaciente.addActionListener(e -> autocompletarTutor());
        p.add(crearFilaCampo("Paciente a Internar (Código / Nombre / Raza) *", cbPaciente));
        p.add(Box.createVerticalStrut(8));

        // Tutor y Teléfono
        JPanel filaTutor = new JPanel(new GridLayout(1, 2, 10, 0));
        filaTutor.setOpaque(false);
        filaTutor.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        txtTutor = crearTextField(false);
        txtTelefono = crearTextField(true);
        filaTutor.add(crearFilaCampo("Propietario / Contacto de Emergencia", txtTutor));
        filaTutor.add(crearFilaCampo("Teléfono Contacto 24/7 *", txtTelefono));
        p.add(filaTutor);
        p.add(Box.createVerticalStrut(8));

        // Box Asignado y Nivel de Alerta
        JPanel filaBox = new JPanel(new GridLayout(1, 2, 10, 0));
        filaBox.setOpaque(false);
        filaBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        cbBoxAsignado = new JComboBox<>(BOXES);
        cbBoxAsignado.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cbBoxAsignado.setBackground(Color.WHITE);

        cbNivelAlerta = new JComboBox<>(new String[]{"ESTABLE", "OBSERVACIÓN", "CRÍTICO"});
        cbNivelAlerta.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cbNivelAlerta.setBackground(Color.WHITE);

        filaBox.add(crearFilaCampo("Box Clínico Asignado *", cbBoxAsignado));
        filaBox.add(crearFilaCampo("Nivel de Alerta y Gravedad *", cbNivelAlerta));
        p.add(filaBox);
        p.add(Box.createVerticalStrut(8));

        // Diagnóstico de Ingreso
        txtDiagnostico = crearTextField(true);
        txtDiagnostico.setText("Gastroenteritis infecciosa severa / Deshidratación moderada");
        p.add(crearFilaCampo("Diagnóstico Clínico de Ingreso *", txtDiagnostico));
        p.add(Box.createVerticalStrut(8));

        // Veterinario, Costo y Estado
        JPanel filaVet = new JPanel(new GridLayout(1, 3, 10, 0));
        filaVet.setOpaque(false);
        filaVet.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        txtVeterinario = crearTextField(true);
        txtVeterinario.setText("Dr. Roberto Mendoza");

        txtCostoDia = crearTextField(true);
        txtCostoDia.setText("110.00");

        cbEstado = new JComboBox<>(new String[]{
                "Internado / En Tratamiento",
                "Alta Médica",
                "Derivado a UCI"
        });
        cbEstado.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cbEstado.setBackground(Color.WHITE);

        filaVet.add(crearFilaCampo("Veterinario Tratante *", txtVeterinario));
        filaVet.add(crearFilaCampo("Tarifa Diaria S/. *", txtCostoDia));
        filaVet.add(crearFilaCampo("Estado Clínico *", cbEstado));
        p.add(filaVet);

        autocompletarTutor();
        return p;
    }

    private JPanel crearTabTerapia() {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(new EmptyBorder(16, 20, 16, 20));

        // Constantes Vitales
        JPanel filaConst = new JPanel(new GridLayout(1, 3, 10, 0));
        filaConst.setOpaque(false);
        filaConst.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        txtPeso = crearTextField(true);
        txtPeso.setText("12.5");
        txtTemperatura = crearTextField(true);
        txtTemperatura.setText("38.6");
        txtFC = crearTextField(true);
        txtFC.setText("115");

        filaConst.add(crearFilaCampo("Peso Actual (kg) *", txtPeso));
        filaConst.add(crearFilaCampo("Temperatura (°C) *", txtTemperatura));
        filaConst.add(crearFilaCampo("Frec. Cardíaca (lpm) *", txtFC));
        p.add(filaConst);
        p.add(Box.createVerticalStrut(10));

        // Fluidoterapia
        txtFluidoterapia = new JTextArea(2, 20);
        txtFluidoterapia.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtFluidoterapia.setLineWrap(true);
        txtFluidoterapia.setWrapStyleWord(true);
        txtFluidoterapia.setText("Ringer Lactato IV a 45 ml/h continuo en bomba de infusión.");
        txtFluidoterapia.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Ui.BORDE_SUAVE, 1),
                new EmptyBorder(4, 6, 4, 6)
        ));
        JScrollPane spFlu = new JScrollPane(txtFluidoterapia);
        spFlu.setPreferredSize(new Dimension(0, 52));
        spFlu.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        spFlu.setBorder(null);
        p.add(crearFilaCampo("Fluidoterapia & Vía de Infusión", spFlu));
        p.add(Box.createVerticalStrut(8));

        // Medicación Activa
        txtMedicacion = new JTextArea(2, 20);
        txtMedicacion.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtMedicacion.setLineWrap(true);
        txtMedicacion.setWrapStyleWord(true);
        txtMedicacion.setText("Cefazolina 25mg/kg c/8h IV, Tramadol 2mg/kg c/8h SC, Omeprazol 1mg/kg c/24h IV.");
        txtMedicacion.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Ui.BORDE_SUAVE, 1),
                new EmptyBorder(4, 6, 4, 6)
        ));
        JScrollPane spMed = new JScrollPane(txtMedicacion);
        spMed.setPreferredSize(new Dimension(0, 52));
        spMed.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        spMed.setBorder(null);
        p.add(crearFilaCampo("Medicamentos Intrahospitalarios Activos", spMed));
        p.add(Box.createVerticalStrut(8));

        // Hoja de Evolución Diaria
        txtEvolucion = new JTextArea(3, 20);
        txtEvolucion.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtEvolucion.setLineWrap(true);
        txtEvolucion.setWrapStyleWord(true);
        txtEvolucion.setText("Paciente estable. Apetito moderado en dieta blanda. Diuresis y motilidad controlada.");
        txtEvolucion.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Ui.BORDE_SUAVE, 1),
                new EmptyBorder(4, 6, 4, 6)
        ));
        JScrollPane spEvo = new JScrollPane(txtEvolucion);
        spEvo.setPreferredSize(new Dimension(0, 70));
        spEvo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        spEvo.setBorder(null);
        p.add(crearFilaCampo("Evolución y Cuidados Diarios de Enfermería", spEvo));

        return p;
    }

    private JPanel crearFilaCampo(String etiqueta, Component campo) {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setOpaque(false);
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 54));

        JLabel lbl = new JLabel(etiqueta, SwingConstants.LEFT);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lbl.setForeground(new Color(71, 85, 105));

        p.add(lbl, BorderLayout.NORTH);
        p.add(campo, BorderLayout.CENTER);
        return p;
    }

    private JTextField crearTextField(boolean editable) {
        JTextField tf = new JTextField();
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tf.setPreferredSize(new Dimension(0, 30));
        tf.setEditable(editable);
        tf.setBackground(editable ? Color.WHITE : new Color(241, 245, 249));
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Ui.BORDE_SUAVE, 1),
                new EmptyBorder(4, 8, 4, 8)
        ));
        return tf;
    }

    private void autocompletarTutor() {
        PacienteItem item = (PacienteItem) cbPaciente.getSelectedItem();
        if (item != null && item.mascota != null) {
            Optional<Cliente> opt = repo.getClienteDeMascota(item.mascota.getCodigo());
            if (opt.isPresent()) {
                Cliente c = opt.get();
                txtTutor.setText(c.getNombreCompleto());
                txtTelefono.setText(c.getTelefonoPrincipal());
            } else {
                txtTutor.setText("Tutor Particular");
                txtTelefono.setText("");
            }
            if (txtPeso != null) {
                txtPeso.setText(String.valueOf(item.mascota.getPesoActualKg()));
            }
        }
    }

    private void cargarDatosExistentes(InternamientoHospitalario h) {
        for (int i = 0; i < cbPaciente.getItemCount(); i++) {
            PacienteItem pi = cbPaciente.getItemAt(i);
            if (pi.mascota != null && pi.mascota.getCodigo().equalsIgnoreCase(h.getCodigoMascota())) {
                cbPaciente.setSelectedIndex(i);
                break;
            }
        }
        txtTutor.setText(h.getNombreTutor());
        txtTelefono.setText(h.getTelefonoTutor());
        cbBoxAsignado.setSelectedItem(h.getNumeroBox());
        cbNivelAlerta.setSelectedItem(h.getNivelAlerta());
        txtDiagnostico.setText(h.getDiagnosticoIngreso());
        txtVeterinario.setText(h.getVeterinarioTratante());
        txtCostoDia.setText(String.format("%.2f", h.getCostoDia()));
        cbEstado.setSelectedItem(h.getEstado());

        txtPeso.setText(String.valueOf(h.getPesoActualKg()));
        txtTemperatura.setText(String.valueOf(h.getTemperaturaC()));
        txtFC.setText(String.valueOf(h.getFrecuenciaCardiaca()));
        txtFluidoterapia.setText(h.getFluidoterapia());
        txtMedicacion.setText(h.getMedicacionActual());
        txtEvolucion.setText(h.getEvolucionNotas());
    }

    private void guardarIngreso() {
        PacienteItem pi = (PacienteItem) cbPaciente.getSelectedItem();
        if (pi == null || pi.mascota == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un paciente válido.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double peso = 10.0;
        double temp = 38.5;
        int fc = 110;
        double costo = 110.0;
        try {
            peso = Double.parseDouble(txtPeso.getText().trim().replace(",", "."));
            temp = Double.parseDouble(txtTemperatura.getText().trim().replace(",", "."));
            fc = Integer.parseInt(txtFC.getText().trim());
            costo = Double.parseDouble(txtCostoDia.getText().trim().replace(",", "."));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Verifique los valores numéricos (Peso, Temp, FC o Tarifa).", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String boxSel = (String) cbBoxAsignado.getSelectedItem();
        String tipoBox = (boxSel != null && boxSel.contains("01")) ? "UCI / Cuidados Intensivos" :
                         (boxSel != null && boxSel.contains("05")) ? "Aislamiento Infeccioso" : "Hospitalización General";

        String id = internamientoOriginal != null ? internamientoOriginal.getIdInternamiento() : null;

        internamientoResultante = new InternamientoHospitalario(
                id,
                boxSel,
                tipoBox,
                pi.mascota.getCodigo(),
                pi.mascota.getNombre(),
                pi.mascota.getEspecie() + " · " + pi.mascota.getRaza(),
                txtTutor.getText().trim(),
                txtTelefono.getText().trim(),
                txtDiagnostico.getText().trim(),
                txtVeterinario.getText().trim(),
                internamientoOriginal != null ? internamientoOriginal.getFechaIngreso() : LocalDate.now(),
                internamientoOriginal != null ? internamientoOriginal.getHoraIngreso() : LocalTime.now(),
                LocalDate.now().plusDays(2),
                peso,
                temp,
                fc,
                txtFluidoterapia.getText().trim(),
                txtMedicacion.getText().trim(),
                txtEvolucion.getText().trim(),
                (String) cbNivelAlerta.getSelectedItem(),
                costo,
                (String) cbEstado.getSelectedItem()
        );

        repo.guardarInternamiento(internamientoResultante);
        guardado = true;
        dispose();
    }

    public boolean isGuardado() {
        return guardado;
    }

    public InternamientoHospitalario getInternamientoResultante() {
        return internamientoResultante;
    }

    private JButton crearBotonAccion(String texto, boolean primario) {
        JButton btn = new JButton(texto) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                boolean esPrimario = Boolean.TRUE.equals(getClientProperty("primario"));
                if (esPrimario) {
                    g2.setColor(getModel().isRollover() ? Ui.TURQUESA_OSCURO : Ui.TURQUESA);
                } else {
                    g2.setColor(getModel().isRollover() ? new Color(241, 245, 249) : Color.WHITE);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                if (!esPrimario) {
                    g2.setColor(Ui.BORDE_SUAVE);
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 6, 6);
                }
                super.paintComponent(g);
                g2.dispose();
            }

            @Override
            public Dimension getPreferredSize() {
                Dimension d = super.getPreferredSize();
                return new Dimension(d.width + 12, 30);
            }
        };
        btn.putClientProperty("primario", primario);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btn.setForeground(primario ? Color.WHITE : new Color(51, 65, 85));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(4, 10, 4, 10));
        return btn;
    }

    public static class PacienteItem {
        public final Mascota mascota;
        public PacienteItem(Mascota mascota) { this.mascota = mascota; }
        @Override
        public String toString() {
            return mascota != null ? mascota.getCodigo() + " - " + mascota.getNombre() + " (" + mascota.getEspecie() + " · " + mascota.getRaza() + ")" : "-";
        }
    }
}
