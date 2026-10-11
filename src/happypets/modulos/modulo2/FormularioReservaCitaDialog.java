package happypets.modulos.modulo2;

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
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Window;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import happypets.data.RepositorioVeterinaria;
import happypets.model.Cita;
import happypets.model.Cliente;
import happypets.model.Mascota;
import happypets.ui.Iconos;
import happypets.ui.Ui;

/**
 * Diálogo modal profesional para registrar o reprogramar citas médicas veterinarias.
 */
public class FormularioReservaCitaDialog extends JDialog {
    private static final long serialVersionUID = 1L;

    private final RepositorioVeterinaria repo = RepositorioVeterinaria.getInstancia();
    private final Cita citaExistente;

    private final JComboBox<ClienteItem> cbCliente;
    private final JComboBox<MascotaItem> cbMascota;
    private final JTextField txtFecha;
    private final JComboBox<String> cbHora;
    private final JComboBox<String> cbDoctor;
    private final JComboBox<String> cbServicio;
    private final JComboBox<String> cbPrioridad;
    private final JTextField txtCosto;
    private final JTextField txtMotivo;
    private final JTextArea txtObservaciones;

    private boolean guardado = false;
    private Cita citaResultado;

    public static class ClienteItem {
        public final Cliente cliente;
        public ClienteItem(Cliente cliente) { this.cliente = cliente; }
        @Override
        public String toString() {
            return cliente != null ? cliente.getNombreCompleto() + " (" + cliente.getNumeroDocumento() + ")" : "Seleccione propietario";
        }
    }

    public static class MascotaItem {
        public final Mascota mascota;
        public MascotaItem(Mascota mascota) { this.mascota = mascota; }
        @Override
        public String toString() {
            return mascota != null ? mascota.getNombre() + " (" + mascota.getEspecie() + " · " + mascota.getRaza() + ")" : "Seleccione mascota";
        }
    }

    public FormularioReservaCitaDialog(Window parent, Cita citaExistente, Consumer<Cita> onGuardar) {
        super(parent, citaExistente == null ? "Agendar Nueva Cita Médica" : "Reprogramar Cita " + citaExistente.getIdCita(), ModalityType.APPLICATION_MODAL);
        this.citaExistente = citaExistente;

        setSize(650, 720);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        // 1. Cabecera superior estilizada
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Ui.TURQUESA);
        header.setBorder(new EmptyBorder(14, 20, 14, 20));

        JPanel headerTxt = new JPanel();
        headerTxt.setOpaque(false);
        headerTxt.setLayout(new BoxLayout(headerTxt, BoxLayout.Y_AXIS));

        JLabel title = new JLabel(citaExistente == null ? "Reserva y Agendamiento de Cita" : "Reprogramación de Cita " + citaExistente.getIdCita());
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(Color.WHITE);
        title.setIcon(Iconos.crearIconoCalendario(20, Color.WHITE));
        title.setIconTextGap(8);

        String subText = citaExistente == null ? "Complete los datos del paciente, propietario y turno de atención clínica" : "Modifique el horario, médico asignado o motivo del turno veterinario";
        JLabel sub = new JLabel(subText);
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sub.setForeground(new Color(224, 242, 254));

        headerTxt.add(title);
        headerTxt.add(Box.createVerticalStrut(2));
        headerTxt.add(sub);
        header.add(headerTxt, BorderLayout.CENTER);
        add(header, BorderLayout.NORTH);

        // 2. Formulario central
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        form.setBorder(new EmptyBorder(14, 20, 14, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Fila 0: Propietario
        cbCliente = new JComboBox<>();
        cbCliente.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cbCliente.setBackground(Color.WHITE);
        cbCliente.setPreferredSize(new Dimension(360, 30));
        for (Cliente c : repo.getClientes()) {
            cbCliente.addItem(new ClienteItem(c));
        }
        cbCliente.addActionListener(e -> sincronizarMascotasDeCliente());

        // Fila 1: Paciente (Mascota)
        cbMascota = new JComboBox<>();
        cbMascota.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cbMascota.setBackground(Color.WHITE);
        cbMascota.setPreferredSize(new Dimension(360, 30));
        sincronizarMascotasDeCliente();

        // Fila 2: Fecha y Hora
        txtFecha = Ui.campoTexto(10);
        txtFecha.setText(LocalDate.now().format(Cita.FECHA_FORMATTER));
        txtFecha.setToolTipText("dd/MM/yyyy");

        cbHora = new JComboBox<>(new String[]{
                "08:30", "09:00", "09:30", "10:00", "10:30", "11:00", "11:30",
                "12:00", "14:00", "14:30", "15:00", "15:30", "16:00", "16:30",
                "17:00", "17:30", "18:00", "18:30"
        });
        cbHora.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cbHora.setBackground(Color.WHITE);
        cbHora.setPreferredSize(new Dimension(130, 30));

        JPanel panelFechaHora = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        panelFechaHora.setOpaque(false);
        panelFechaHora.add(txtFecha);
        JLabel lblHoraEtq = new JLabel("Horario:");
        lblHoraEtq.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblHoraEtq.setForeground(Ui.TEXTO_TITULO);
        panelFechaHora.add(lblHoraEtq);
        panelFechaHora.add(cbHora);

        // Fila 3: Veterinario
        cbDoctor = new JComboBox<>(new String[]{
                "Dr. Roberto Mendoza Chávez (CMVP 8492)",
                "Dra. Laura Morales Ruiz (CMVP 4512)",
                "Dr. Mario Silva Paredes (CMVP 8910)",
                "Dra. Carmen Vega Hurtado (CMVP 1245)",
                "Dr. Carlos Silva (Traumatología)"
        });
        cbDoctor.setEditable(true);
        cbDoctor.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cbDoctor.setBackground(Color.WHITE);
        cbDoctor.setPreferredSize(new Dimension(360, 30));

        // Fila 4: Servicio y Prioridad
        cbServicio = new JComboBox<>(new String[]{
                "Consulta Médica",
                "Vacunación",
                "Cirugía / Quirófano",
                "Grooming / Peluquería",
                "Control y Seguimiento",
                "Odontología / Profilaxis"
        });
        cbServicio.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cbServicio.setBackground(Color.WHITE);
        cbServicio.setPreferredSize(new Dimension(170, 30));

        cbPrioridad = new JComboBox<>(new String[]{"Normal", "Urgente", "Emergencia"});
        cbPrioridad.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cbPrioridad.setBackground(Color.WHITE);
        cbPrioridad.setPreferredSize(new Dimension(120, 30));

        JPanel panelServPrio = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        panelServPrio.setOpaque(false);
        panelServPrio.add(cbServicio);
        JLabel lblPrioEtq = new JLabel("Prioridad:");
        lblPrioEtq.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblPrioEtq.setForeground(Ui.TEXTO_TITULO);
        panelServPrio.add(lblPrioEtq);
        panelServPrio.add(cbPrioridad);

        // Fila 5: Costo Estimado
        txtCosto = Ui.campoTexto(8);
        txtCosto.setText("75.00");

        JPanel panelCosto = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        panelCosto.setOpaque(false);
        JLabel lblMoneda = new JLabel("S/.");
        lblMoneda.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblMoneda.setForeground(Ui.TEXTO_TITULO);
        panelCosto.add(lblMoneda);
        panelCosto.add(txtCosto);

        // Fila 6: Motivo
        txtMotivo = Ui.campoTexto(25);

        // Fila 7: Observaciones
        txtObservaciones = new JTextArea(3, 20);
        txtObservaciones.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtObservaciones.setLineWrap(true);
        txtObservaciones.setWrapStyleWord(true);
        txtObservaciones.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Ui.BORDE_SUAVE, 1),
                new EmptyBorder(6, 8, 6, 8)
        ));
        JScrollPane spObs = new JScrollPane(txtObservaciones);
        spObs.setPreferredSize(new Dimension(360, 65));

        // Cargar datos si editamos
        if (citaExistente != null) {
            txtFecha.setText(citaExistente.getFechaFormateada());
            cbHora.setSelectedItem(citaExistente.getHoraFormateada());
            cbServicio.setSelectedItem(citaExistente.getTipoServicio());
            cbPrioridad.setSelectedItem(citaExistente.getPrioridad());
            txtCosto.setText(String.format(java.util.Locale.US, "%.2f", citaExistente.getCostoEstimado()));
            txtMotivo.setText(citaExistente.getMotivo());
            txtObservaciones.setText(citaExistente.getObservaciones());

            // Seleccionar cliente y mascota correspondientes
            for (int i = 0; i < cbCliente.getItemCount(); i++) {
                ClienteItem ci = cbCliente.getItemAt(i);
                if (ci.cliente != null && ci.cliente.getNumeroDocumento().equals(citaExistente.getDniCliente())) {
                    cbCliente.setSelectedIndex(i);
                    sincronizarMascotasDeCliente();
                    break;
                }
            }
            for (int j = 0; j < cbMascota.getItemCount(); j++) {
                MascotaItem mi = cbMascota.getItemAt(j);
                if (mi.mascota != null && mi.mascota.getCodigo().equalsIgnoreCase(citaExistente.getCodigoMascota())) {
                    cbMascota.setSelectedIndex(j);
                    break;
                }
            }
        }

        // Armar layout
        int fila = 0;
        agregarCampo(form, gbc, fila++, "Propietario Responsable *", cbCliente);
        agregarCampo(form, gbc, fila++, "Paciente (Mascota) *", cbMascota);
        agregarCampo(form, gbc, fila++, "Fecha y Horario *", panelFechaHora);
        agregarCampo(form, gbc, fila++, "Veterinario Tratante *", cbDoctor);
        agregarCampo(form, gbc, fila++, "Servicio y Prioridad *", panelServPrio);
        agregarCampo(form, gbc, fila++, "Costo Estimado *", panelCosto);
        agregarCampo(form, gbc, fila++, "Motivo de Consulta *", txtMotivo);
        agregarCampo(form, gbc, fila++, "Instrucciones Previas", spObs);

        JScrollPane scrollForm = new JScrollPane(form);
        scrollForm.setBorder(null);
        scrollForm.getVerticalScrollBar().setUnitIncrement(12);
        add(scrollForm, BorderLayout.CENTER);

        // 3. Pie con botones
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        footer.setBackground(Ui.FONDO);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Ui.BORDE_SUAVE));

        JButton btnCancelar = crearBotonModal("Cancelar", false);
        btnCancelar.addActionListener(e -> dispose());

        JButton btnGuardar = crearBotonModal(citaExistente == null ? "Agendar Cita" : "Guardar Cambios", true);
        btnGuardar.addActionListener(e -> {
            ClienteItem cItem = (ClienteItem) cbCliente.getSelectedItem();
            MascotaItem mItem = (MascotaItem) cbMascota.getSelectedItem();
            String motivo = txtMotivo.getText().trim();
            String strFecha = txtFecha.getText().trim();
            String strHora = (String) cbHora.getSelectedItem();

            if (cItem == null || cItem.cliente == null) {
                JOptionPane.showMessageDialog(this, "Debe seleccionar un propietario responsable.", "Validación", JOptionPane.WARNING_MESSAGE);
                cbCliente.requestFocus();
                return;
            }
            if (mItem == null || mItem.mascota == null) {
                JOptionPane.showMessageDialog(this, "Debe seleccionar una mascota para la cita.", "Validación", JOptionPane.WARNING_MESSAGE);
                cbMascota.requestFocus();
                return;
            }
            if (motivo.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Debe ingresar el motivo de la consulta.", "Validación", JOptionPane.WARNING_MESSAGE);
                txtMotivo.requestFocus();
                return;
            }

            LocalDate fecha;
            try {
                fecha = LocalDate.parse(strFecha, Cita.FECHA_FORMATTER);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Formato de fecha inválido. Ingrese dd/MM/yyyy.", "Validación", JOptionPane.WARNING_MESSAGE);
                txtFecha.requestFocus();
                return;
            }

            LocalTime hora;
            try {
                hora = LocalTime.parse(strHora, Cita.HORA_FORMATTER);
            } catch (Exception ex) {
                hora = LocalTime.of(9, 0);
            }

            double costo = 75.0;
            try {
                costo = Double.parseDouble(txtCosto.getText().trim().replace(',', '.'));
            } catch (Exception ignored) { }

            String docCompleto = (String) cbDoctor.getSelectedItem();
            String docNombre = docCompleto != null ? docCompleto.split("\\(")[0].trim() : "Dr. Roberto Mendoza";

            Cita cita = citaExistente;
            if (cita == null) {
                cita = new Cita(
                        null,
                        mItem.mascota.getCodigo(),
                        mItem.mascota.getNombre(),
                        mItem.mascota.getEspecie() + " · " + mItem.mascota.getRaza(),
                        cItem.cliente.getNumeroDocumento(),
                        cItem.cliente.getNombreCompleto(),
                        cItem.cliente.getTelefonoPrincipal(),
                        fecha,
                        hora,
                        30,
                        docNombre,
                        (String) cbServicio.getSelectedItem(),
                        "Programada",
                        motivo,
                        (String) cbPrioridad.getSelectedItem(),
                        txtObservaciones.getText().trim(),
                        costo
                );
            } else {
                cita.setCodigoMascota(mItem.mascota.getCodigo());
                cita.setNombreMascota(mItem.mascota.getNombre());
                cita.setEspecieRaza(mItem.mascota.getEspecie() + " · " + mItem.mascota.getRaza());
                cita.setDniCliente(cItem.cliente.getNumeroDocumento());
                cita.setNombreCliente(cItem.cliente.getNombreCompleto());
                cita.setTelefonoCliente(cItem.cliente.getTelefonoPrincipal());
                cita.setFecha(fecha);
                cita.setHora(hora);
                cita.setVeterinario(docNombre);
                cita.setTipoServicio((String) cbServicio.getSelectedItem());
                cita.setPrioridad((String) cbPrioridad.getSelectedItem());
                cita.setCostoEstimado(costo);
                cita.setMotivo(motivo);
                cita.setObservaciones(txtObservaciones.getText().trim());
            }

            repo.guardarCita(cita);
            this.citaResultado = cita;
            this.guardado = true;
            if (onGuardar != null) {
                onGuardar.accept(cita);
            }
            dispose();
        });

        footer.add(btnCancelar);
        footer.add(btnGuardar);
        add(footer, BorderLayout.SOUTH);
    }

    private void sincronizarMascotasDeCliente() {
        cbMascota.removeAllItems();
        ClienteItem ci = (ClienteItem) cbCliente.getSelectedItem();
        if (ci != null && ci.cliente != null) {
            for (Mascota m : ci.cliente.getMascotas()) {
                cbMascota.addItem(new MascotaItem(m));
            }
        }
    }

    private void agregarCampo(JPanel panel, GridBagConstraints gbc, int row, String etiqueta, JComponent comp) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.28;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        JLabel lbl = new JLabel(etiqueta, SwingConstants.LEFT);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lbl.setForeground(Ui.TEXTO_TITULO);
        panel.add(lbl, gbc);

        gbc.gridx = 1;
        gbc.gridy = row;
        gbc.weightx = 0.72;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(comp, gbc);
    }

    private JButton crearBotonModal(String texto, boolean primario) {
        JButton btn = new JButton(texto) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (primario) {
                    g2.setColor(getModel().isRollover() ? Ui.TURQUESA_OSCURO : Ui.TURQUESA);
                } else {
                    g2.setColor(getModel().isRollover() ? new Color(241, 245, 249) : Color.WHITE);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                if (!primario) {
                    g2.setColor(Ui.BORDE_SUAVE);
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 6, 6);
                }
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setForeground(primario ? Color.WHITE : Ui.TEXTO_TITULO);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(7, 16, 7, 16));
        return btn;
    }

    public void preseleccionarDatos(LocalDate fecha, String hora, String espacioODoctor) {
        if (fecha != null && txtFecha != null) {
            txtFecha.setText(fecha.format(Cita.FECHA_FORMATTER));
        }
        if (hora != null && cbHora != null) {
            for (int i = 0; i < cbHora.getItemCount(); i++) {
                if (cbHora.getItemAt(i).startsWith(hora)) {
                    cbHora.setSelectedIndex(i);
                    break;
                }
            }
        }
        if (espacioODoctor != null && cbDoctor != null) {
            for (int i = 0; i < cbDoctor.getItemCount(); i++) {
                String item = cbDoctor.getItemAt(i);
                if (espacioODoctor.contains("Mendoza") && item.contains("Mendoza")) {
                    cbDoctor.setSelectedIndex(i);
                    break;
                } else if (espacioODoctor.contains("Morales") && item.contains("Morales")) {
                    cbDoctor.setSelectedIndex(i);
                    break;
                }
            }
        }
        if (espacioODoctor != null && cbServicio != null) {
            if (espacioODoctor.contains("Quirófano") || espacioODoctor.contains("Cirugía")) {
                cbServicio.setSelectedItem("Cirugía / Quirófano");
            } else if (espacioODoctor.contains("Grooming") || espacioODoctor.contains("Peluquería")) {
                cbServicio.setSelectedItem("Grooming / Peluquería");
            }
        }
    }

    public boolean isGuardado() {
        return guardado;
    }

    public Cita getCitaResultado() {
        return citaResultado;
    }
}
