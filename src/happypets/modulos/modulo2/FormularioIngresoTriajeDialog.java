package happypets.modulos.modulo2;

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
import java.time.LocalTime;
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
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import happypets.data.RepositorioVeterinaria;
import happypets.model.Cita;
import happypets.model.PacienteTriaje;
import happypets.ui.Iconos;
import happypets.ui.Ui;

/**
 * Diálogo modal para recepción de pacientes, toma de constantes vitales
 * y clasificación de prioridad clínica en Sala de Espera y Triaje Manchester.
 */
public class FormularioIngresoTriajeDialog extends JDialog {
    private static final long serialVersionUID = 1L;

    private static final Color COLOR_AZUL_PRIMARIO = Ui.TURQUESA;
    private static final Color COLOR_BORDE = Ui.BORDE_SUAVE;
    private static final Color COLOR_TEXTO_TITULO = Ui.TEXTO_TITULO;
    private static final Color COLOR_TEXTO_MUTED = Ui.TEXTO_MUTED;

    private final RepositorioVeterinaria repo = RepositorioVeterinaria.getInstancia();
    private final Consumer<PacienteTriaje> onGuardado;
    private final PacienteTriaje pacienteExistente;

    private JComboBox<CitaItem> cbCitaPrevia;
    private JTextField txtNombreMascota;
    private JTextField txtEspecieRaza;
    private JTextField txtPropietario;
    private JTextField txtTelefono;
    private JTextField txtPeso;
    private JTextField txtTemperatura;
    private JTextField txtFrecuenciaCardiaca;
    private JRadioButton rbVerde;
    private JRadioButton rbAmarillo;
    private JRadioButton rbRojo;
    private JComboBox<String> cbConsultorio;
    private JTextArea txtMotivo;

    public static class CitaItem {
        public final Cita cita;
        public final String etiqueta;
        public CitaItem(Cita cita, String etiqueta) {
            this.cita = cita;
            this.etiqueta = etiqueta;
        }
        @Override
        public String toString() { return etiqueta; }
    }

    /**
     * Constructor de FormularioIngresoTriajeDialog.
     * Inicializa los componentes gráficos, configura layouts, asigna estilos
     * institucionales y carga los datos desde el repositorio central.
     */
    public FormularioIngresoTriajeDialog(Window parent, PacienteTriaje pacienteExistente, Cita citaPrevia, Consumer<PacienteTriaje> onGuardado) {
        super(parent, pacienteExistente == null ? "Ingreso y Triaje de Paciente" : "Modificar Ficha de Triaje", ModalityType.APPLICATION_MODAL);
        this.pacienteExistente = pacienteExistente;
        this.onGuardado = onGuardado;

        setSize(650, 700);
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
        header.setBackground(new Color(13, 148, 136)); // Teal
        header.setBorder(new EmptyBorder(14, 20, 14, 20));

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel headerTxt = new JPanel();
        headerTxt.setOpaque(false);
        headerTxt.setLayout(new BoxLayout(headerTxt, BoxLayout.Y_AXIS));

        JLabel title = new JLabel(pacienteExistente == null ? "Recepción e Ingreso a Triaje Clínico" : "Actualización de Constantes y Triaje");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(Color.WHITE);
        title.setIcon(Iconos.crearIconoAlertaTriaje(20, Color.WHITE));
        title.setIconTextGap(8);

        JLabel sub = new JLabel(pacienteExistente == null ?
                "Registro de llegada, constantes vitales y asignación según escala Manchester." :
                "Ticket " + pacienteExistente.getIdTicket() + " · " + pacienteExistente.getNombreMascota() + " (" + pacienteExistente.getEspecieRaza() + ")");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sub.setForeground(new Color(204, 251, 241));

        headerTxt.add(title);
        headerTxt.add(Box.createVerticalStrut(2));
        headerTxt.add(sub);
        header.add(headerTxt, BorderLayout.CENTER);
        add(header, BorderLayout.NORTH);

        // Cuerpo del formulario con Scroll
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel panelCuerpo = new JPanel(new GridBagLayout());
        panelCuerpo.setBackground(Color.WHITE);
        panelCuerpo.setBorder(new EmptyBorder(16, 22, 16, 22));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 4, 5, 4);
        int gridY = 0;

        // 1. Cita programada opcional
        gbc.gridx = 0; gbc.gridy = gridY; gbc.gridwidth = 2; gbc.weightx = 1.0;
        panelCuerpo.add(crearEtiquetaCampo("Cita Previa Programada en Agenda (Opcional)"), gbc);
        gridY++;

        cbCitaPrevia = new JComboBox<>();
        cbCitaPrevia.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cbCitaPrevia.setBackground(Color.WHITE);
        cbCitaPrevia.setPreferredSize(new Dimension(0, 32));
        cargarCitasDisponibles(citaPrevia);
        cbCitaPrevia.addActionListener(e -> autollenarDesdeCitaSeleccionada());
        if (pacienteExistente != null) {
            cbCitaPrevia.setEnabled(false);
        }

        gbc.gridx = 0; gbc.gridy = gridY; gbc.gridwidth = 2;
        panelCuerpo.add(cbCitaPrevia, gbc);
        gridY++;

        // 2. Nombre Mascota y Especie/Raza
        gbc.gridwidth = 1; gbc.weightx = 0.5;
        gbc.gridx = 0; gbc.gridy = gridY;
        panelCuerpo.add(crearEtiquetaCampo("Nombre de la Mascota *"), gbc);
        gbc.gridx = 1;
        panelCuerpo.add(crearEtiquetaCampo("Especie y Raza *"), gbc);
        gridY++;

        txtNombreMascota = crearCampoTexto();
        txtEspecieRaza = crearCampoTexto();
        gbc.gridx = 0; gbc.gridy = gridY;
        panelCuerpo.add(txtNombreMascota, gbc);
        gbc.gridx = 1;
        panelCuerpo.add(txtEspecieRaza, gbc);
        gridY++;

        // 3. Propietario y Teléfono
        gbc.gridx = 0; gbc.gridy = gridY;
        panelCuerpo.add(crearEtiquetaCampo("Tutor / Propietario Responsable *"), gbc);
        gbc.gridx = 1;
        panelCuerpo.add(crearEtiquetaCampo("Teléfono de Contacto Móvil *"), gbc);
        gridY++;

        txtPropietario = crearCampoTexto();
        txtTelefono = crearCampoTexto();
        gbc.gridx = 0; gbc.gridy = gridY;
        panelCuerpo.add(txtPropietario, gbc);
        gbc.gridx = 1;
        panelCuerpo.add(txtTelefono, gbc);
        gridY++;

        // 4. Constantes Vitales (3 columnas agrupadas)
        gbc.gridx = 0; gbc.gridy = gridY; gbc.gridwidth = 2;
        panelCuerpo.add(crearEtiquetaCampo("Constantes Fisiológicas / Signos Vitales al Ingreso"), gbc);
        gridY++;

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel panelSignos = new JPanel(new GridLayout(1, 3, 10, 0));
        panelSignos.setOpaque(false);

        txtPeso = crearCampoTexto();
        txtPeso.setText("12.5");
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel pPeso = new JPanel(new BorderLayout(0, 3));
        pPeso.setOpaque(false);
        JLabel lblP = new JLabel("Peso (kg) *");
        lblP.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblP.setForeground(COLOR_TEXTO_MUTED);
        pPeso.add(lblP, BorderLayout.NORTH);
        pPeso.add(txtPeso, BorderLayout.CENTER);

        txtTemperatura = crearCampoTexto();
        txtTemperatura.setText("38.5");
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel pTemp = new JPanel(new BorderLayout(0, 3));
        pTemp.setOpaque(false);
        JLabel lblT = new JLabel("Temperatura (°C) *");
        lblT.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblT.setForeground(COLOR_TEXTO_MUTED);
        pTemp.add(lblT, BorderLayout.NORTH);
        pTemp.add(txtTemperatura, BorderLayout.CENTER);

        txtFrecuenciaCardiaca = crearCampoTexto();
        txtFrecuenciaCardiaca.setText("95");
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel pFc = new JPanel(new BorderLayout(0, 3));
        pFc.setOpaque(false);
        JLabel lblFc = new JLabel("Frec. Cardíaca (ppm) *");
        lblFc.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblFc.setForeground(COLOR_TEXTO_MUTED);
        pFc.add(lblFc, BorderLayout.NORTH);
        pFc.add(txtFrecuenciaCardiaca, BorderLayout.CENTER);

        panelSignos.add(pPeso);
        panelSignos.add(pTemp);
        panelSignos.add(pFc);

        gbc.gridx = 0; gbc.gridy = gridY; gbc.gridwidth = 2;
        panelCuerpo.add(panelSignos, gbc);
        gridY++;

        // 5. Escala de Triaje Manchester
        gbc.gridx = 0; gbc.gridy = gridY; gbc.gridwidth = 2;
        panelCuerpo.add(crearEtiquetaCampo("Nivel de Prioridad Clínica (Escala Manchester de Triaje) *"), gbc);
        gridY++;

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel panelTriaje = new JPanel(new GridLayout(1, 3, 8, 0));
        panelTriaje.setOpaque(false);

        rbVerde = new JRadioButton("<html><b>VERDE</b><br><small style='color:#15803d'>Normal · Rutinario</small></html>", true);
        rbVerde.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        rbVerde.setBackground(new Color(240, 253, 244));
        rbVerde.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(187, 247, 208), 1),
                new EmptyBorder(6, 8, 6, 8)
        ));
        rbVerde.setBorderPainted(true);

        rbAmarillo = new JRadioButton("<html><b>AMARILLO</b><br><small style='color:#b45309'>Urgencia · < 30 min</small></html>");
        rbAmarillo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        rbAmarillo.setBackground(new Color(254, 252, 232));
        rbAmarillo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(254, 240, 138), 1),
                new EmptyBorder(6, 8, 6, 8)
        ));
        rbAmarillo.setBorderPainted(true);

        rbRojo = new JRadioButton("<html><b>ROJO</b><br><small style='color:#b91c1c'>Emergencia Crítica</small></html>");
        rbRojo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        rbRojo.setBackground(new Color(254, 242, 242));
        rbRojo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(254, 202, 202), 1),
                new EmptyBorder(6, 8, 6, 8)
        ));
        rbRojo.setBorderPainted(true);

        ButtonGroup bgT = new ButtonGroup();
        bgT.add(rbVerde);
        bgT.add(rbAmarillo);
        bgT.add(rbRojo);

        panelTriaje.add(rbVerde);
        panelTriaje.add(rbAmarillo);
        panelTriaje.add(rbRojo);

        gbc.gridx = 0; gbc.gridy = gridY; gbc.gridwidth = 2;
        panelCuerpo.add(panelTriaje, gbc);
        gridY++;

        // 6. Consultorio o Destino
        gbc.gridx = 0; gbc.gridy = gridY; gbc.gridwidth = 2;
        panelCuerpo.add(crearEtiquetaCampo("Consultorio / Espacio Médico Asignado *"), gbc);
        gridY++;

        cbConsultorio = new JComboBox<>(new String[]{
                "Consultorio 1 - Dr. Roberto Mendoza (Cardiología / Medicina)",
                "Consultorio 2 - Dra. Laura Morales (Cirugía / Felinos)",
                "Tópico de Emergencias (Cuidados Inmediatos)",
                "Quirófano Principal (Procedimiento de Urgencia)",
                "Área de Grooming y Estética"
        });
        cbConsultorio.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cbConsultorio.setBackground(Color.WHITE);
        cbConsultorio.setPreferredSize(new Dimension(0, 32));

        gbc.gridx = 0; gbc.gridy = gridY; gbc.gridwidth = 2;
        panelCuerpo.add(cbConsultorio, gbc);
        gridY++;

        // 7. Motivo / Signos de Urgencia
        gbc.gridx = 0; gbc.gridy = gridY; gbc.gridwidth = 2;
        panelCuerpo.add(crearEtiquetaCampo("Motivo de Ingreso / Signos Clínicos Observados *"), gbc);
        gridY++;

        txtMotivo = new JTextArea(3, 20);
        txtMotivo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtMotivo.setLineWrap(true);
        txtMotivo.setWrapStyleWord(true);
        txtMotivo.setBorder(new EmptyBorder(6, 8, 6, 8));

        JScrollPane spMotivo = new JScrollPane(txtMotivo);
        spMotivo.setBorder(BorderFactory.createLineBorder(new Color(203, 213, 225), 1));
        spMotivo.setPreferredSize(new Dimension(0, 68));

        gbc.gridx = 0; gbc.gridy = gridY; gbc.gridwidth = 2;
        panelCuerpo.add(spMotivo, gbc);

        JScrollPane spGlobal = new JScrollPane(panelCuerpo);
        spGlobal.setBorder(null);
        spGlobal.getVerticalScrollBar().setUnitIncrement(12);
        add(spGlobal, BorderLayout.CENTER);

        // Barra inferior de botones
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
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

        JButton btnGuardar = new JButton(pacienteExistente == null ? "Ingresar a Sala" : "Actualizar Triaje");
        btnGuardar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setBackground(COLOR_AZUL_PRIMARIO);
        btnGuardar.setIcon(Iconos.crearIconoCheck(14, Color.WHITE));
        btnGuardar.setIconTextGap(6);
        btnGuardar.setFocusPainted(false);
        btnGuardar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnGuardar.setPreferredSize(new Dimension(160, 34));
        btnGuardar.addActionListener(e -> guardarTriaje());

        footer.add(btnCancelar);
        footer.add(btnGuardar);
        add(footer, BorderLayout.SOUTH);

        // Cargar datos existentes si aplica
        if (pacienteExistente != null) {
            cargarDatosExistentes();
        } else if (citaPrevia != null) {
            autollenarDesdeCita(citaPrevia);
        }
    }

    private void cargarCitasDisponibles(Cita citaPreseleccionada) {
        cbCitaPrevia.removeAllItems();
        cbCitaPrevia.addItem(new CitaItem(null, "--- Ingreso Directo por Urgencia (Sin Cita Previa) ---"));
        List<Cita> citas = repo.getCitas();
        int idxSeleccion = 0;
        int i = 1;
        for (Cita c : citas) {
            String label = c.getIdCita() + " · " + c.getNombreMascota() + " (" + c.getNombreCliente() + ") - " + c.getTipoServicio() + " " + c.getHoraFormateada() + "h";
            CitaItem item = new CitaItem(c, label);
            cbCitaPrevia.addItem(item);
            if (citaPreseleccionada != null && c.getIdCita().equalsIgnoreCase(citaPreseleccionada.getIdCita())) {
                idxSeleccion = i;
            }
            i++;
        }
        cbCitaPrevia.setSelectedIndex(idxSeleccion);
    }

    private void autollenarDesdeCitaSeleccionada() {
        CitaItem item = (CitaItem) cbCitaPrevia.getSelectedItem();
        if (item != null && item.cita != null) {
            autollenarDesdeCita(item.cita);
        }
    }

    private void autollenarDesdeCita(Cita c) {
        txtNombreMascota.setText(c.getNombreMascota());
        txtEspecieRaza.setText(c.getEspecieRaza());
        txtPropietario.setText(c.getNombreCliente());
        txtTelefono.setText(c.getTelefonoCliente());
        txtMotivo.setText(c.getMotivo());

        if (c.getVeterinario() != null && c.getVeterinario().contains("Mendoza")) {
            cbConsultorio.setSelectedIndex(0);
        } else if (c.getVeterinario() != null && c.getVeterinario().contains("Morales")) {
            cbConsultorio.setSelectedIndex(1);
        }

        if ("Emergencia".equalsIgnoreCase(c.getPrioridad())) {
            rbRojo.setSelected(true);
        } else if ("Urgente".equalsIgnoreCase(c.getPrioridad())) {
            rbAmarillo.setSelected(true);
        } else {
            rbVerde.setSelected(true);
        }
    }

    private void cargarDatosExistentes() {
        txtNombreMascota.setText(pacienteExistente.getNombreMascota());
        txtEspecieRaza.setText(pacienteExistente.getEspecieRaza());
        txtPropietario.setText(pacienteExistente.getNombrePropietario());
        txtTelefono.setText(pacienteExistente.getTelefonoPropietario());
        txtPeso.setText(String.valueOf(pacienteExistente.getPesoKg()));
        txtTemperatura.setText(String.valueOf(pacienteExistente.getTemperaturaC()));
        txtFrecuenciaCardiaca.setText(String.valueOf(pacienteExistente.getFrecuenciaCardiacaPpm()));
        txtMotivo.setText(pacienteExistente.getMotivo());

        String nt = pacienteExistente.getNivelTriaje().toUpperCase();
        if (nt.contains("ROJO")) {
            rbRojo.setSelected(true);
        } else if (nt.contains("AMARILLO")) {
            rbAmarillo.setSelected(true);
        } else {
            rbVerde.setSelected(true);
        }

        String consultorio = pacienteExistente.getConsultorioAsignado();
        for (int i = 0; i < cbConsultorio.getItemCount(); i++) {
            if (cbConsultorio.getItemAt(i).startsWith(consultorio)) {
                cbConsultorio.setSelectedIndex(i);
                break;
            }
        }
    }

    private void guardarTriaje() {
        String mascota = txtNombreMascota.getText().trim();
        String especie = txtEspecieRaza.getText().trim();
        String prop = txtPropietario.getText().trim();
        String tel = txtTelefono.getText().trim();
        String motivo = txtMotivo.getText().trim();

        if (mascota.isEmpty() || especie.isEmpty() || prop.isEmpty() || motivo.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Por favor complete todos los campos obligatorios del paciente (*).",
                    "Validación Requerida", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double peso = 12.0;
        double temp = 38.5;
        int fc = 90;
        try {
            peso = Double.parseDouble(txtPeso.getText().trim());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "El peso ingresado no es un número válido (ej: 14.5).", "Formato Inválido", JOptionPane.WARNING_MESSAGE);
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
            fc = Integer.parseInt(txtFrecuenciaCardiaca.getText().trim());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "La frecuencia cardíaca debe ser un número entero (ej: 95).", "Formato Inválido", JOptionPane.WARNING_MESSAGE);
            txtFrecuenciaCardiaca.requestFocus();
            return;
        }

        String nivel = rbRojo.isSelected() ? "ROJO (Emergencia crítica)" :
                       rbAmarillo.isSelected() ? "AMARILLO (Urgencia)" : "VERDE (Normal)";

        String consultorioSel = (String) cbConsultorio.getSelectedItem();
        String consultorioCorto = consultorioSel;
        if (consultorioSel != null && consultorioSel.contains("-")) {
            consultorioCorto = consultorioSel.split("-")[0].trim();
        }

        CitaItem cItem = (CitaItem) cbCitaPrevia.getSelectedItem();
        String idCita = (cItem != null && cItem.cita != null) ? cItem.cita.getIdCita() : null;

        PacienteTriaje p;
        if (pacienteExistente != null) {
            p = pacienteExistente;
            p.setNombreMascota(mascota);
            p.setEspecieRaza(especie);
            p.setNombrePropietario(prop);
            p.setTelefonoPropietario(tel);
            p.setPesoKg(peso);
            p.setTemperaturaC(temp);
            p.setFrecuenciaCardiacaPpm(fc);
            p.setNivelTriaje(nivel);
            p.setConsultorioAsignado(consultorioCorto);
            p.setMotivo(motivo);
            repo.guardarPacienteTriaje(p);
        } else {
            String ticket = "TR-" + String.format("%02d", repo.getPacientesTriaje().size() + 1);
            p = new PacienteTriaje(
                    ticket,
                    idCita,
                    "VET-" + (100 + (int)(Math.random() * 800)),
                    mascota,
                    especie,
                    prop,
                    tel,
                    LocalTime.now(),
                    peso,
                    temp,
                    fc,
                    nivel,
                    motivo,
                    consultorioCorto,
                    "En Espera"
            );
            repo.agregarPacienteTriaje(p);
        }

        if (onGuardado != null) {
            onGuardado.accept(p);
        }

        dispose();
    }

    private JLabel crearEtiquetaCampo(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lbl.setForeground(COLOR_TEXTO_TITULO);
        lbl.setBorder(new EmptyBorder(4, 0, 2, 0));
        return lbl;
    }

    private JTextField crearCampoTexto() {
        JTextField txt = new JTextField();
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txt.setPreferredSize(new Dimension(0, 32));
        txt.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225), 1),
                new EmptyBorder(4, 8, 4, 8)
        ));
        return txt;
    }
}
