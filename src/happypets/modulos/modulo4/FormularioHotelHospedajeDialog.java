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
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
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
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import happypets.data.RepositorioVeterinaria;
import happypets.model.Cliente;
import happypets.model.Mascota;
import happypets.model.ReservaHospedaje;
import happypets.ui.Iconos;
import happypets.ui.Ui;

/**
 * Diálogo modal profesional para registrar o editar una reserva de Hotel y Guardería.
 * Organizado en dos pestañas:
 *  1. Estancia & Asignación de Suite Climatizada
 *  2. Nutrición, Paseos & Servicios Adicionales
 */
public class FormularioHotelHospedajeDialog extends JDialog {
    private static final long serialVersionUID = 1L;

    private final RepositorioVeterinaria repo = RepositorioVeterinaria.getInstancia();
    private boolean guardado = false;
    private ReservaHospedaje reservaResultante = null;
    private final ReservaHospedaje reservaOriginal;

    // Componentes - Tab 1
    private JComboBox<PacienteItem> cbPaciente;
    private JTextField txtTutor;
    private JTextField txtEmergencia;
    private JComboBox<String> cbSuite;
    private JTextField txtFechaIngreso;
    private JTextField txtFechaSalida;
    private JTextField txtNoches;
    private JTextField txtCostoNoche;
    private JTextField txtCostoTotal;
    private JComboBox<String> cbEstado;

    // Componentes - Tab 2
    private JTextField txtDieta;
    private JComboBox<String> cbPaseos;
    private JCheckBox chkBanoSalida;
    private JTextField txtMedicacion;

    public FormularioHotelHospedajeDialog(Window owner) {
        this(owner, null);
    }

    public FormularioHotelHospedajeDialog(Window owner, ReservaHospedaje edicion) {
        super(owner, edicion == null ? "Registrar Check-In / Nueva Reserva de Hospedaje" : "Editar Reserva de Hotel: " + edicion.getIdReserva(), ModalityType.APPLICATION_MODAL);
        this.reservaOriginal = edicion;

        setSize(650, 560);
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

        JLabel lblTit = new JLabel(reservaOriginal == null ? "Nueva Estadía · Hotel & Guardería Canina / Felina" : "Modificar Reserva · " + reservaOriginal.getIdReserva());
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTit.setForeground(Ui.TEXTO_TITULO);

        JLabel lblSub = new JLabel("Asignación de suites individuales climatizadas, cronograma de recreación y dieta supervisada");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(Ui.TEXTO_MUTED);

        titBox.add(lblTit);
        titBox.add(Box.createVerticalStrut(3));
        titBox.add(lblSub);

        JLabel iconLabel = new JLabel(Iconos.crearIconoCasaMascota(28, Ui.TURQUESA_OSCURO));
        cabecera.add(titBox, BorderLayout.CENTER);
        cabecera.add(iconLabel, BorderLayout.EAST);
        add(cabecera, BorderLayout.NORTH);

        // Pestañas
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabs.setBackground(Color.WHITE);

        tabs.addTab("1. Estancia & Suite", Iconos.crearIconoCasaMascota(14, Ui.TURQUESA), crearTabEstancia());
        tabs.addTab("2. Nutrición & Servicios", Iconos.crearIconoCheck(14, Ui.COLOR_EXITO), crearTabServicios());
        add(tabs, BorderLayout.CENTER);

        // Barra inferior de botones
        JPanel barraBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        barraBotones.setBackground(Color.WHITE);
        barraBotones.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Ui.BORDE_SUAVE));

        JButton btnCancelar = crearBotonAccion("Cancelar", false);
        btnCancelar.addActionListener(e -> dispose());

        JButton btnGuardar = crearBotonAccion("Guardar Reserva de Hospedaje", true);
        btnGuardar.addActionListener(e -> guardarReserva());

        barraBotones.add(btnCancelar);
        barraBotones.add(btnGuardar);
        add(barraBotones, BorderLayout.SOUTH);
    }

    private JPanel crearTabEstancia() {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(new EmptyBorder(16, 20, 16, 20));

        // Huésped
        cbPaciente = new JComboBox<>();
        cbPaciente.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cbPaciente.setBackground(Color.WHITE);
        cbPaciente.setPreferredSize(new Dimension(0, 30));
        for (Mascota m : repo.todasLasMascotas()) {
            cbPaciente.addItem(new PacienteItem(m));
        }
        cbPaciente.addActionListener(e -> autocompletarTutor());
        p.add(crearFilaCampo("Paciente Huésped *", cbPaciente));
        p.add(Box.createVerticalStrut(8));

        // Tutor y Emergencia
        JPanel filaTutor = new JPanel(new GridLayout(1, 2, 10, 0));
        filaTutor.setOpaque(false);
        filaTutor.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        txtTutor = crearTextField(false);
        txtEmergencia = crearTextField(true);
        filaTutor.add(crearFilaCampo("Tutor Responsable Titular", txtTutor));
        filaTutor.add(crearFilaCampo("Teléfono de Emergencia 24H *", txtEmergencia));
        p.add(filaTutor);
        p.add(Box.createVerticalStrut(8));

        // Suite Climatizada
        cbSuite = new JComboBox<>(new String[]{
                "Suite 01 Canina (Grande - Jardín)",
                "Suite 02 Canina (Grande - Climatizada)",
                "Suite 03 Canina (Mediana - Interior)",
                "Suite 04 Canina (Mediana - Interior)",
                "Suite 05 Felina (Torre Rascador A)",
                "Suite 06 Felina (Torre Rascador B)",
                "Suite 07 Felina (Suite Tranquila)",
                "Suite 08 VIP Mixta (Cámaras 24h)"
        });
        cbSuite.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cbSuite.setBackground(Color.WHITE);
        p.add(crearFilaCampo("Suite Climatizada Asignada *", cbSuite));
        p.add(Box.createVerticalStrut(8));

        // Fechas Check-in, Check-out y Noches
        JPanel filaFechas = new JPanel(new GridLayout(1, 3, 10, 0));
        filaFechas.setOpaque(false);
        filaFechas.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        txtFechaIngreso = crearTextField(true);
        txtFechaIngreso.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        txtFechaSalida = crearTextField(true);
        txtFechaSalida.setText(LocalDate.now().plusDays(2).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        txtNoches = crearTextField(true);
        txtNoches.setText("2");

        filaFechas.add(crearFilaCampo("Check-In (dd/MM/yyyy) *", txtFechaIngreso));
        filaFechas.add(crearFilaCampo("Check-Out (dd/MM/yyyy) *", txtFechaSalida));
        filaFechas.add(crearFilaCampo("N° de Noches *", txtNoches));
        p.add(filaFechas);
        p.add(Box.createVerticalStrut(8));

        // Tarifa por noche, Total y Estado
        JPanel filaCostos = new JPanel(new GridLayout(1, 3, 10, 0));
        filaCostos.setOpaque(false);
        filaCostos.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        txtCostoNoche = crearTextField(true);
        txtCostoNoche.setText("55.00");

        txtCostoTotal = crearTextField(true);
        txtCostoTotal.setText("110.00");

        cbEstado = new JComboBox<>(new String[]{
                "En Estadía / Hospedado",
                "Confirmada",
                "Finalizada / Check-out",
                "Cancelada"
        });
        cbEstado.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cbEstado.setBackground(Color.WHITE);

        filaCostos.add(crearFilaCampo("Tarifa Noche S/. *", txtCostoNoche));
        filaCostos.add(crearFilaCampo("Costo Total S/. *", txtCostoTotal));
        filaCostos.add(crearFilaCampo("Estado de Reserva *", cbEstado));
        p.add(filaCostos);

        autocompletarTutor();
        return p;
    }

    private JPanel crearTabServicios() {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(new EmptyBorder(16, 20, 16, 20));

        txtDieta = crearTextField(true);
        txtDieta.setText("Ración balanceada ProPlan 150g 2v/día (Tutor suministra alimento)");
        p.add(crearFilaCampo("Dieta / Alimentación Específica *", txtDieta));
        p.add(Box.createVerticalStrut(10));

        cbPaseos = new JComboBox<>(new String[]{
                "3 Paseos al día · Parque interno y socialización",
                "2 Paseos al día · Paseo individual con correa",
                "Guardería Diurna · Juegos libres supervisados",
                "Zona Felina · Estimulación con juguetes y rascador"
        });
        cbPaseos.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cbPaseos.setBackground(Color.WHITE);
        p.add(crearFilaCampo("Plan de Paseos y Socialización *", cbPaseos));
        p.add(Box.createVerticalStrut(10));

        txtMedicacion = crearTextField(true);
        txtMedicacion.setText("Ninguna medicación requerida");
        p.add(crearFilaCampo("Medicación Especial / Cuidados Particulares", txtMedicacion));
        p.add(Box.createVerticalStrut(12));

        chkBanoSalida = new JCheckBox("Incluir Baño y Deslanado de Salida (+ S/. 35.00)");
        chkBanoSalida.setFont(new Font("Segoe UI", Font.BOLD, 12));
        chkBanoSalida.setForeground(Ui.TURQUESA_OSCURO);
        chkBanoSalida.setOpaque(false);
        chkBanoSalida.setSelected(true);
        chkBanoSalida.addActionListener(e -> recalcularCostoTotal());
        p.add(chkBanoSalida);

        return p;
    }

    private void recalcularCostoTotal() {
        try {
            int noches = Integer.parseInt(txtNoches.getText().trim());
            double tarifa = Double.parseDouble(txtCostoNoche.getText().trim().replace(",", "."));
            double extra = chkBanoSalida.isSelected() ? 35.0 : 0.0;
            txtCostoTotal.setText(String.format("%.2f", (noches * tarifa) + extra));
        } catch (Exception ignored) {}
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
                txtEmergencia.setText(c.getTelefonoPrincipal());
            } else {
                txtTutor.setText("Tutor Titular");
                txtEmergencia.setText("");
            }
        }
    }

    private void cargarDatosExistentes(ReservaHospedaje r) {
        for (int i = 0; i < cbPaciente.getItemCount(); i++) {
            PacienteItem pi = cbPaciente.getItemAt(i);
            if (pi.mascota != null && pi.mascota.getCodigo().equalsIgnoreCase(r.getCodigoMascota())) {
                cbPaciente.setSelectedIndex(i);
                break;
            }
        }
        txtTutor.setText(r.getNombreTutor());
        txtEmergencia.setText(r.getTelefonoEmergencia());
        cbSuite.setSelectedItem(r.getNumeroSuite());
        txtFechaIngreso.setText(r.getFechaCheckInFormateada());
        txtFechaSalida.setText(r.getFechaCheckOutFormateada());
        txtNoches.setText(String.valueOf(r.getNumeroNoches()));
        txtCostoNoche.setText(String.format("%.2f", r.getCostoNoche()));
        txtCostoTotal.setText(String.format("%.2f", r.getCostoTotal()));
        cbEstado.setSelectedItem(r.getEstado());

        txtDieta.setText(r.getTipoAlimentacion());
        cbPaseos.setSelectedItem(r.getPlanPaseos());
        chkBanoSalida.setSelected(r.isBanoSalida());
        txtMedicacion.setText(r.getMedicacionEspecial());
    }

    private void guardarReserva() {
        PacienteItem pi = (PacienteItem) cbPaciente.getSelectedItem();
        if (pi == null || pi.mascota == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un huésped válido.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        LocalDate in = LocalDate.now();
        LocalDate out = LocalDate.now().plusDays(2);
        try {
            in = LocalDate.parse(txtFechaIngreso.getText().trim(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            out = LocalDate.parse(txtFechaSalida.getText().trim(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Formato de fechas incorrecto. Use dd/MM/yyyy.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int noches = (int) ChronoUnit.DAYS.between(in, out);
        if (noches <= 0) noches = 1;

        double tarifa = 55.0;
        double total = 110.0;
        try {
            tarifa = Double.parseDouble(txtCostoNoche.getText().trim().replace(",", "."));
            total = Double.parseDouble(txtCostoTotal.getText().trim().replace(",", "."));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Verifique los montos numéricos de tarifa o costo total.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String id = reservaOriginal != null ? reservaOriginal.getIdReserva() : null;

        reservaResultante = new ReservaHospedaje(
                id,
                (String) cbSuite.getSelectedItem(),
                pi.mascota.getCodigo(),
                pi.mascota.getNombre(),
                pi.mascota.getEspecie() + " · " + pi.mascota.getRaza(),
                txtTutor.getText().trim(),
                txtEmergencia.getText().trim(),
                txtEmergencia.getText().trim(),
                in,
                out,
                noches,
                txtDieta.getText().trim(),
                (String) cbPaseos.getSelectedItem(),
                chkBanoSalida.isSelected(),
                txtMedicacion.getText().trim(),
                tarifa,
                total,
                (String) cbEstado.getSelectedItem()
        );

        repo.guardarReservaHospedaje(reservaResultante);
        guardado = true;
        dispose();
    }

    public boolean isGuardado() {
        return guardado;
    }

    public ReservaHospedaje getReservaResultante() {
        return reservaResultante;
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
