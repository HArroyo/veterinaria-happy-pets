package happypets.modulos.modulo2;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import happypets.data.RepositorioVeterinaria;
import happypets.model.Cita;
import happypets.model.RecordatorioCita;
import happypets.ui.Iconos;
import happypets.ui.Ui;

/**
 * Diálogo modal profesional para redactar y enviar recordatorios de citas veterinarias
 * a través de WhatsApp, SMS y Correo Electrónico.
 */
public class FormularioEnvioRecordatorioDialog extends JDialog {
    private static final long serialVersionUID = 1L;

    private final RepositorioVeterinaria repo = RepositorioVeterinaria.getInstancia();
    private final Consumer<RecordatorioCita> onEnviado;

    private JComboBox<CitaComboItem> cbCitas;
    private JTextField txtContacto;
    private JRadioButton rbWhatsApp;
    private JRadioButton rbSms;
    private JRadioButton rbEmail;
    private JComboBox<String> cbPlantillas;
    private JTextArea txtMensaje;

    public static class CitaComboItem {
        public final Cita cita;
        public CitaComboItem(Cita cita) { this.cita = cita; }
        @Override
        public String toString() {
            if (cita == null) return "Seleccione una cita programada...";
            return cita.getIdCita() + " · " + cita.getNombreMascota() + " (" + cita.getNombreCliente() + ") - " +
                   cita.getFechaFormateada() + " " + cita.getHoraFormateada() + " hrs";
        }
    }

    /**
     * Constructor de FormularioEnvioRecordatorioDialog.
     * Inicializa los componentes gráficos, configura layouts, asigna estilos
     * institucionales y carga los datos desde el repositorio central.
     */
    public FormularioEnvioRecordatorioDialog(Window parent, Cita citaPreseleccionada, Consumer<RecordatorioCita> onEnviado) {
        super(parent, "Redactar y Emitir Recordatorio", ModalityType.APPLICATION_MODAL);
        this.onEnviado = onEnviado;

        setSize(600, 620);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        // Cabecera superior
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(22, 163, 74)); // WhatsApp Green
        header.setBorder(new EmptyBorder(14, 20, 14, 20));

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel headerTxt = new JPanel();
        headerTxt.setOpaque(false);
        headerTxt.setLayout(new BoxLayout(headerTxt, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Emisión de Notificación y Recordatorio");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(Color.WHITE);
        title.setIcon(Iconos.crearIconoWhatsApp(20, Color.WHITE));
        title.setIconTextGap(8);

        JLabel sub = new JLabel("Seleccione la cita médica, configure el canal de envío y personalice la plantilla.");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sub.setForeground(new Color(220, 252, 231));

        headerTxt.add(title);
        headerTxt.add(Box.createVerticalStrut(2));
        headerTxt.add(sub);
        header.add(headerTxt, BorderLayout.CENTER);
        add(header, BorderLayout.NORTH);

        // Cuerpo con GridBagLayout
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        form.setBorder(new EmptyBorder(16, 20, 16, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 6, 5, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Fila 0: Cita vinculada
        cbCitas = new JComboBox<>();
        cbCitas.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cbCitas.setBackground(Color.WHITE);
        cbCitas.setPreferredSize(new Dimension(360, 30));

        List<Cita> listaCitas = repo.getCitas();
        for (Cita c : listaCitas) {
            cbCitas.addItem(new CitaComboItem(c));
        }

        cbCitas.addActionListener(e -> autocompletarPorCita());

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        form.add(crearLabelCampo("Cita Médica Vinculada:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        form.add(cbCitas, gbc);

        // Fila 1: Canal de transmisión
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel pCanales = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        pCanales.setOpaque(false);

        rbWhatsApp = new JRadioButton("WhatsApp", true);
        rbWhatsApp.setFont(new Font("Segoe UI", Font.BOLD, 11));
        rbWhatsApp.setForeground(new Color(22, 163, 74));
        rbWhatsApp.setOpaque(false);

        rbSms = new JRadioButton("SMS");
        rbSms.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        rbSms.setForeground(new Color(2, 132, 199));
        rbSms.setOpaque(false);

        rbEmail = new JRadioButton("E-Mail");
        rbEmail.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        rbEmail.setForeground(new Color(147, 51, 234));
        rbEmail.setOpaque(false);

        ButtonGroup bgCanal = new ButtonGroup();
        bgCanal.add(rbWhatsApp);
        bgCanal.add(rbSms);
        bgCanal.add(rbEmail);

        pCanales.add(rbWhatsApp);
        pCanales.add(rbSms);
        pCanales.add(rbEmail);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.3;
        form.add(crearLabelCampo("Canal de Transmisión:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        form.add(pCanales, gbc);

        // Fila 2: Contacto destino
        txtContacto = Ui.campoTexto(20);
        txtContacto.setPreferredSize(new Dimension(360, 30));

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.3;
        form.add(crearLabelCampo("Teléfono / Correo Destino:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        form.add(txtContacto, gbc);

        // Fila 3: Plantilla
        cbPlantillas = new JComboBox<>(new String[]{
                "Recordatorio estándar 24 horas previas",
                "Indicaciones de ayuno prequirúrgico (8 horas)",
                "Recordatorio de vacunación anual",
                "Seguimiento posoperatorio y evolución clínica",
                "Confirmación de cita y ubicación de la clínica"
        });
        cbPlantillas.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cbPlantillas.setBackground(Color.WHITE);
        cbPlantillas.setPreferredSize(new Dimension(360, 30));
        cbPlantillas.addActionListener(e -> regenerarMensajePlantilla());

        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.3;
        form.add(crearLabelCampo("Plantilla Inteligente:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        form.add(cbPlantillas, gbc);

        // Fila 4: Mensaje
        txtMensaje = new JTextArea(5, 20);
        txtMensaje.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtMensaje.setLineWrap(true);
        txtMensaje.setWrapStyleWord(true);
        txtMensaje.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Ui.BORDE_SUAVE, 1),
                new EmptyBorder(6, 8, 6, 8)
        ));
        JScrollPane spMsg = new JScrollPane(txtMensaje);
        spMsg.setPreferredSize(new Dimension(360, 110));

        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0.3;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        form.add(crearLabelCampo("Mensaje a Transmitir:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        gbc.anchor = GridBagConstraints.CENTER;
        form.add(spMsg, gbc);

        add(form, BorderLayout.CENTER);

        // Barra de botones inferior
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        footer.setBackground(new Color(248, 250, 252));
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Ui.BORDE_SUAVE));

        JButton btnCancelar = Ui.botonSecundario("Cancelar");
        btnCancelar.addActionListener(e -> dispose());

        JButton btnEnviar = Ui.botonPrimario("Enviar Notificación");
        btnEnviar.setBackground(new Color(22, 163, 74));
        btnEnviar.setIcon(Iconos.crearIconoWhatsApp(13, Color.WHITE));
        btnEnviar.setIconTextGap(6);
        btnEnviar.addActionListener(e -> ejecutarEnvio());

        footer.add(btnCancelar);
        footer.add(btnEnviar);
        add(footer, BorderLayout.SOUTH);

        // Preselección si se proporcionó una cita inicial
        if (citaPreseleccionada != null) {
            for (int i = 0; i < cbCitas.getItemCount(); i++) {
                CitaComboItem it = cbCitas.getItemAt(i);
                if (it.cita != null && it.cita.getIdCita().equals(citaPreseleccionada.getIdCita())) {
                    cbCitas.setSelectedIndex(i);
                    break;
                }
            }
        } else if (cbCitas.getItemCount() > 0) {
            cbCitas.setSelectedIndex(0);
        }
        autocompletarPorCita();
    }

    private JLabel crearLabelCampo(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setForeground(Ui.TEXTO_TITULO);
        return l;
    }

    private void autocompletarPorCita() {
        CitaComboItem it = (CitaComboItem) cbCitas.getSelectedItem();
        if (it != null && it.cita != null) {
            Cita c = it.cita;
            txtContacto.setText(c.getTelefonoCliente() != null && !c.getTelefonoCliente().isEmpty() ?
                    c.getTelefonoCliente() : "cliente@veterinaria.com");
            regenerarMensajePlantilla();
        }
    }

    private void regenerarMensajePlantilla() {
        CitaComboItem it = (CitaComboItem) cbCitas.getSelectedItem();
        if (it == null || it.cita == null) return;
        Cita c = it.cita;
        String plantilla = (String) cbPlantillas.getSelectedItem();

        String msg;
        if (plantilla != null && plantilla.contains("ayuno")) {
            msg = "Estimado(a) " + c.getNombreCliente() + ", le recordamos que " + c.getNombreMascota() +
                  " tiene programada su cirugía de " + c.getTipoServicio() + " el " + c.getFechaFormateada() +
                  " a las " + c.getHoraFormateada() + " hrs en Quirófano con el " + c.getVeterinario() +
                  ". Recuerde que debe cumplir ayuno sólido de 8 horas. Por favor confirme su asistencia.";
        } else if (plantilla != null && plantilla.contains("vacunación")) {
            msg = "Hola " + c.getNombreCliente() + ", le recordamos el turno de vacunación de " + c.getNombreMascota() +
                  " programado para el " + c.getFechaFormateada() + " a las " + c.getHoraFormateada() +
                  " hrs en Happy Pets. Por favor traiga su carné de vacunación.";
        } else if (plantilla != null && plantilla.contains("posoperatorio")) {
            msg = "Hola " + c.getNombreCliente() + ", nos comunicamos de Happy Pets para hacer seguimiento de la recuperación de " +
                  c.getNombreMascota() + ". ¿Cómo ha pasado la noche? Recuerde administrar la medicación indicada por el " +
                  c.getVeterinario() + ". Cualquier duda estamos atentos.";
        } else if (plantilla != null && plantilla.contains("ubicación")) {
            msg = "Hola " + c.getNombreCliente() + ", le esperamos en Happy Pets Clínica Veterinaria 24H (Av. San Borja Sur 482) para la atención de " +
                  c.getNombreMascota() + " el " + c.getFechaFormateada() + " a las " + c.getHoraFormateada() + " hrs.";
        } else {
            msg = "Hola " + c.getNombreCliente() + ", te recordamos que " + c.getNombreMascota() +
                  " tiene cita de " + c.getTipoServicio() + " el " + c.getFechaFormateada() + " a las " +
                  c.getHoraFormateada() + " hrs en Happy Pets con el " + c.getVeterinario() +
                  ". Por favor confirme su asistencia respondiendo a este mensaje.";
        }
        txtMensaje.setText(msg);
    }

    private void ejecutarEnvio() {
        CitaComboItem it = (CitaComboItem) cbCitas.getSelectedItem();
        if (it == null || it.cita == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar una cita válida.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String contacto = txtContacto.getText().trim();
        if (contacto.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe ingresar un número de teléfono o correo de destino.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String contenido = txtMensaje.getText().trim();
        if (contenido.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El contenido del mensaje no puede estar vacío.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String canal = rbWhatsApp.isSelected() ? "WhatsApp" : rbSms.isSelected() ? "SMS" : "E-Mail";
        Cita c = it.cita;

        RecordatorioCita rec = new RecordatorioCita(
                null,
                c.getIdCita(),
                c.getNombreMascota(),
                c.getNombreCliente(),
                contacto,
                canal,
                LocalDateTime.now(),
                contenido,
                "Enviado"
        );

        repo.agregarRecordatorio(rec);

        if (onEnviado != null) {
            onEnviado.accept(rec);
        }

        JOptionPane.showMessageDialog(this,
                "Recordatorio enviado exitosamente a través de " + canal + " a " + c.getNombreCliente() + " (" + contacto + ").",
                "Notificación Emitida", JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }
}
