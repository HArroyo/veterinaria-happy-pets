package happypets.modulos.modulo4;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Window;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

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
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import happypets.data.RepositorioVeterinaria;
import happypets.model.Cliente;
import happypets.model.Mascota;
import happypets.model.ServicioGrooming;
import happypets.ui.Iconos;
import happypets.ui.Ui;

/**
 * Diálogo modal profesional para registrar o editar un turno de Grooming y Spa Canino/Felino.
 * Organizado en dos pestañas clínicas:
 *  1. Servicio & Asignación
 *  2. Checklist Sanitario & Cosmética
 */
public class FormularioGroomingDialog extends JDialog {
    private static final long serialVersionUID = 1L;

    private final RepositorioVeterinaria repo = RepositorioVeterinaria.getInstancia();
    private boolean guardado = false;
    private ServicioGrooming servicioResultante = null;
    private final ServicioGrooming servicioOriginal;

    // Componentes - Tab 1
    private JComboBox<PacienteItem> cbPaciente;
    private JTextField txtTutor;
    private JTextField txtTelefono;
    private JComboBox<String> cbGroomer;
    private JComboBox<String> cbTipoServicio;
    private JTextField txtHoraTurno;
    private JTextField txtCosto;
    private JComboBox<String> cbEstado;

    // Componentes - Tab 2
    private JCheckBox chkEctoparasitos;
    private JCheckBox chkRevisionPiel;
    private JCheckBox chkMantoNudos;
    private JCheckBox chkLimpiezaOidos;
    private JCheckBox chkCorteUnas;
    private JCheckBox chkVaciadoGlandulas;
    private JTextField txtCosmetica;
    private JTextArea txtObservaciones;

    public FormularioGroomingDialog(Window owner) {
        this(owner, null);
    }

    public FormularioGroomingDialog(Window owner, ServicioGrooming edicion) {
        super(owner, edicion == null ? "Registrar Nuevo Turno de Spa & Grooming" : "Editar Ficha de Grooming: " + edicion.getIdGrooming(), ModalityType.APPLICATION_MODAL);
        this.servicioOriginal = edicion;

        setSize(640, 560);
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

        JLabel lblTit = new JLabel(servicioOriginal == null ? "Nuevo Turno de Estética y Peluquería" : "Modificar Turno de Spa · " + servicioOriginal.getIdGrooming());
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTit.setForeground(Ui.TEXTO_TITULO);

        JLabel lblSub = new JLabel("Admisión sanitaria, selección de servicios de bienestar y especificaciones estéticas");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(Ui.TEXTO_MUTED);

        titBox.add(lblTit);
        titBox.add(Box.createVerticalStrut(3));
        titBox.add(lblSub);

        JLabel iconLabel = new JLabel(Iconos.crearIconoTijeras(28, Ui.TURQUESA));
        cabecera.add(titBox, BorderLayout.CENTER);
        cabecera.add(iconLabel, BorderLayout.EAST);
        add(cabecera, BorderLayout.NORTH);

        // Pestañas
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabs.setBackground(Color.WHITE);

        tabs.addTab("1. Servicio & Asignación", Iconos.crearIconoTijeras(14, Ui.TURQUESA), crearTabServicio());
        tabs.addTab("2. Checklist & Cosmética", Iconos.crearIconoCheck(14, Ui.COLOR_EXITO), crearTabChecklist());
        add(tabs, BorderLayout.CENTER);

        // Barra inferior de botones
        JPanel barraBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        barraBotones.setBackground(Color.WHITE);
        barraBotones.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Ui.BORDE_SUAVE));

        JButton btnCancelar = crearBotonAccion("Cancelar", false);
        btnCancelar.addActionListener(e -> dispose());
        JButton btnGuardar = crearBotonAccion("Guardar Turno de Spa", true);
        btnGuardar.addActionListener(e -> guardarTurno());

        barraBotones.add(btnCancelar);
        barraBotones.add(btnGuardar);
        add(barraBotones, BorderLayout.SOUTH);
    }

    private JPanel crearTabServicio() {
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
        p.add(crearFilaCampo("Paciente (Nombre / Especie / Código) *", cbPaciente));
        p.add(Box.createVerticalStrut(8));

        // Tutor y Teléfono
        JPanel filaTutor = new JPanel(new GridLayout(1, 2, 10, 0));
        filaTutor.setOpaque(false);
        filaTutor.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        txtTutor = crearTextField(false);
        txtTelefono = crearTextField(true);
        filaTutor.add(crearFilaCampo("Propietario / Tutor Titular", txtTutor));
        filaTutor.add(crearFilaCampo("Teléfono / Contacto WhatsApp *", txtTelefono));
        p.add(filaTutor);
        p.add(Box.createVerticalStrut(8));

        // Groomer y Servicio
        JPanel filaGroomer = new JPanel(new GridLayout(1, 2, 10, 0));
        filaGroomer.setOpaque(false);
        filaGroomer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        cbGroomer = new JComboBox<>(new String[]{
                "Ana Martínez (Groomer)",
                "Carlos Mendoza (Estilista Canino)",
                "Luis Peña (Groomer)",
                "Lucía Domínguez Peña (Estilista Senior)"
        });
        cbGroomer.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cbGroomer.setBackground(Color.WHITE);

        cbTipoServicio = new JComboBox<>(new String[]{
                "Spa Completo (Baño + Corte + Uñas + Glándulas)",
                "Baño Medicado Dermatológico (Clorhexidina)",
                "Corte de Raza Estándar y Acabado a Tijera",
                "Corte Higiénico y Deslanado Profundo",
                "Baño Cosmético e Hidratación de Manto"
        });
        cbTipoServicio.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cbTipoServicio.setBackground(Color.WHITE);

        filaGroomer.add(crearFilaCampo("Groomer / Estilista Responsable *", cbGroomer));
        filaGroomer.add(crearFilaCampo("Tipo de Servicio de Spa *", cbTipoServicio));
        p.add(filaGroomer);
        p.add(Box.createVerticalStrut(8));

        // Hora, Costo y Estado
        JPanel filaDet = new JPanel(new GridLayout(1, 3, 10, 0));
        filaDet.setOpaque(false);
        filaDet.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        txtHoraTurno = crearTextField(true);
        txtHoraTurno.setText(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
        txtCosto = crearTextField(true);
        txtCosto.setText("70.00");

        cbEstado = new JComboBox<>(new String[]{
                "En Espera",
                "En Baño",
                "En Corte y Secado",
                "Listo para Entrega",
                "Entregado"
        });
        cbEstado.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cbEstado.setBackground(Color.WHITE);

        filaDet.add(crearFilaCampo("Hora Turno (HH:mm) *", txtHoraTurno));
        filaDet.add(crearFilaCampo("Tarifa Servicio S/. *", txtCosto));
        filaDet.add(crearFilaCampo("Estado Inicial *", cbEstado));
        p.add(filaDet);

        autocompletarTutor();
        return p;
    }

    private JPanel crearTabChecklist() {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(new EmptyBorder(16, 20, 16, 20));

        JLabel lblCheck = new JLabel("Checklist de Admisión Sanitaria (Página 3 Wireframe):");
        lblCheck.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblCheck.setForeground(Ui.TEXTO_TITULO);
        p.add(lblCheck);
        p.add(Box.createVerticalStrut(6));

        JPanel gridChecks = new JPanel(new GridLayout(2, 3, 8, 8));
        gridChecks.setOpaque(false);
        gridChecks.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        chkEctoparasitos = new JCheckBox("Control ectoparásitos (pulgas/garrapatas)");
        chkEctoparasitos.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        chkEctoparasitos.setOpaque(false);

        chkRevisionPiel = new JCheckBox("Revisión dérmica sana");
        chkRevisionPiel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        chkRevisionPiel.setSelected(true);
        chkRevisionPiel.setOpaque(false);

        chkMantoNudos = new JCheckBox("Manto libre de nudos graves");
        chkMantoNudos.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        chkMantoNudos.setSelected(true);
        chkMantoNudos.setOpaque(false);

        chkLimpiezaOidos = new JCheckBox("Limpieza de conducto auditivo");
        chkLimpiezaOidos.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        chkLimpiezaOidos.setSelected(true);
        chkLimpiezaOidos.setOpaque(false);

        chkCorteUnas = new JCheckBox("Corte y limado de uñas");
        chkCorteUnas.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        chkCorteUnas.setSelected(true);
        chkCorteUnas.setOpaque(false);

        chkVaciadoGlandulas = new JCheckBox("Vaciado de glándulas anales");
        chkVaciadoGlandulas.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        chkVaciadoGlandulas.setOpaque(false);

        gridChecks.add(chkEctoparasitos);
        gridChecks.add(chkRevisionPiel);
        gridChecks.add(chkMantoNudos);
        gridChecks.add(chkLimpiezaOidos);
        gridChecks.add(chkCorteUnas);
        gridChecks.add(chkVaciadoGlandulas);
        p.add(gridChecks);
        p.add(Box.createVerticalStrut(10));

        txtCosmetica = crearTextField(true);
        txtCosmetica.setText("Champú Avena Suave, Acondicionador Desenredante, Colonia Baby");
        p.add(crearFilaCampo("Cosmética & Champús Especiales Aplicados", txtCosmetica));
        p.add(Box.createVerticalStrut(10));

        txtObservaciones = new JTextArea(3, 20);
        txtObservaciones.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtObservaciones.setLineWrap(true);
        txtObservaciones.setWrapStyleWord(true);
        txtObservaciones.setText("Paciente tranquilo y sociable. Acabado de raza estándar.");
        txtObservaciones.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Ui.BORDE_SUAVE, 1),
                new EmptyBorder(6, 8, 6, 8)
        ));

        JScrollPane sp = new JScrollPane(txtObservaciones);
        sp.setPreferredSize(new Dimension(0, 80));
        sp.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        sp.setBorder(null);

        p.add(crearFilaCampo("Observaciones de Grooming / Conducta", sp));
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
        }
    }

    private void cargarDatosExistentes(ServicioGrooming g) {
        // Seleccionar paciente
        for (int i = 0; i < cbPaciente.getItemCount(); i++) {
            PacienteItem pi = cbPaciente.getItemAt(i);
            if (pi.mascota != null && pi.mascota.getCodigo().equalsIgnoreCase(g.getCodigoMascota())) {
                cbPaciente.setSelectedIndex(i);
                break;
            }
        }
        txtTutor.setText(g.getNombreTutor());
        txtTelefono.setText(g.getTelefonoTutor());
        cbGroomer.setSelectedItem(g.getGroomer());
        cbTipoServicio.setSelectedItem(g.getTipoServicio());
        txtHoraTurno.setText(g.getHoraTurnoFormateada());
        txtCosto.setText(String.format("%.2f", g.getCosto()));
        cbEstado.setSelectedItem(g.getEstado());

        chkEctoparasitos.setSelected(g.getControlEctoparasitos() != null && g.getControlEctoparasitos().contains("Presencia"));
        chkRevisionPiel.setSelected(g.getEstadoPiel() != null && g.getEstadoPiel().contains("sana"));
        txtCosmetica.setText(g.getProductosUtilizados() != null ? g.getProductosUtilizados() : "");
        txtObservaciones.setText(g.getObservacionesConducta() != null ? g.getObservacionesConducta() : "");
    }

    private void guardarTurno() {
        PacienteItem pi = (PacienteItem) cbPaciente.getSelectedItem();
        if (pi == null || pi.mascota == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un paciente válido.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        LocalTime hora = LocalTime.now();
        try {
            String[] p = txtHoraTurno.getText().trim().split(":");
            hora = LocalTime.of(Integer.parseInt(p[0].trim()), Integer.parseInt(p[1].trim()));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Hora inválida. Use el formato HH:mm (ej. 14:30).", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double costo = 70.0;
        try {
            costo = Double.parseDouble(txtCosto.getText().trim().replace(",", "."));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Precio inválido.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String ecto = chkEctoparasitos.isSelected() ? "Presencia leve - Requiere champú antiparasitario" : "Libre de ectoparásitos";
        String piel = chkRevisionPiel.isSelected() ? "Piel sana sin lesiones" : "Sensibilidad dérmica observada";

        String id = servicioOriginal != null ? servicioOriginal.getIdGrooming() : null;

        servicioResultante = new ServicioGrooming(
                id,
                pi.mascota.getCodigo(),
                pi.mascota.getNombre(),
                pi.mascota.getEspecie() + " · " + pi.mascota.getRaza(),
                txtTutor.getText().trim(),
                txtTelefono.getText().trim(),
                servicioOriginal != null ? servicioOriginal.getFecha() : LocalDate.now(),
                hora,
                (String) cbGroomer.getSelectedItem(),
                (String) cbTipoServicio.getSelectedItem(),
                ecto,
                piel,
                txtCosmetica.getText().trim(),
                txtObservaciones.getText().trim(),
                costo,
                (String) cbEstado.getSelectedItem()
        );

        repo.guardarServicioGrooming(servicioResultante);
        guardado = true;
        dispose();
    }

    public boolean isGuardado() {
        return guardado;
    }

    public ServicioGrooming getServicioResultante() {
        return servicioResultante;
    }

    private JButton crearBotonAccion(String texto, boolean primario) {
        JButton btn = new JButton(texto) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(java.awt.Graphics g) {
                java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
                g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
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
        btn.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
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
