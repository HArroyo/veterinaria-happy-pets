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
import happypets.model.RegistroInmunizacion;
import happypets.ui.Iconos;
import happypets.ui.Ui;

/**
 * Diálogo modal profesional para el registro y edición de inmunizaciones,
 * vacunas biológicas y desparasitaciones (Submódulo 3.2).
 */
public class FormularioInmunizacionDialog extends JDialog {
    private static final long serialVersionUID = 1L;

    private static final Color COLOR_AZUL_PRIMARIO = Ui.TURQUESA;
    private static final Color COLOR_BORDE = Ui.BORDE_SUAVE;
    private static final Color COLOR_TEXTO_TITULO = Ui.TEXTO_TITULO;
    private static final Color COLOR_TEXTO_MUTED = Ui.TEXTO_MUTED;

    private final RepositorioVeterinaria repo = RepositorioVeterinaria.getInstancia();
    private final Consumer<RegistroInmunizacion> onGuardado;
    private final RegistroInmunizacion registroExistente;

    // Campos Tab 1: Aplicación y Paciente
    private JComboBox<PacienteItem> cbPaciente;
    private JTextField txtTutor;
    private JTextField txtTelefono;
    private JComboBox<String> cbTipoControl;
    private JComboBox<String> cbProducto;
    private JTextField txtDosis;
    private JTextField txtLaboratorioLote;
    private JTextField txtPeso;

    // Campos Tab 2: Trazabilidad y Refuerzo
    private JTextField txtFechaAplicacion;
    private JTextField txtFechaVencimiento;
    private JTextField txtProximaFecha;
    private JComboBox<String> cbVeterinario;
    private JComboBox<String> cbEstado;
    private JTextArea txtObservaciones;

    public static class PacienteItem {
        public final Mascota mascota;
        public PacienteItem(Mascota mascota) { this.mascota = mascota; }
        @Override
        public String toString() {
            return mascota != null ? mascota.getCodigo() + " - " + mascota.getNombre() + " (" + mascota.getEspecie() + " · " + mascota.getRaza() + ")" : "Seleccione paciente...";
        }
    }

    public FormularioInmunizacionDialog(Window parent, RegistroInmunizacion registroExistente, Mascota mascotaInicial, Consumer<RegistroInmunizacion> onGuardado) {
        super(parent, registroExistente == null ? "Nuevo Registro de Inmunización" : "Editar Inmunización " + registroExistente.getIdRegistro(), ModalityType.APPLICATION_MODAL);
        this.registroExistente = registroExistente;
        this.onGuardado = onGuardado;

        setSize(680, 640);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        // Cabecera Médica
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(COLOR_AZUL_PRIMARIO);
        header.setBorder(new EmptyBorder(16, 20, 16, 20));

        JPanel headerTxt = new JPanel();
        headerTxt.setOpaque(false);
        headerTxt.setLayout(new BoxLayout(headerTxt, BoxLayout.Y_AXIS));

        JLabel title = new JLabel(registroExistente == null ? "Registro Preventivo de Inmunización" : "Modificación de Inmunización");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(Color.WHITE);
        title.setIcon(Iconos.crearIconoJeringa(20, Color.WHITE));
        title.setIconTextGap(8);

        JLabel sub = new JLabel(registroExistente == null ?
                "Control de biológicos, antiparasitarios, lote oficial y fecha de refuerzo." :
                "ID: " + registroExistente.getIdRegistro() + " · Paciente: " + registroExistente.getNombreMascota());
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

        tabs.addTab("1. Aplicación y Biológico", Iconos.crearIconoJeringa(14, COLOR_AZUL_PRIMARIO), crearTabAplicacion());
        tabs.addTab("2. Trazabilidad y Refuerzo", Iconos.crearIconoCalendario(14, new Color(217, 119, 6)), crearTabTrazabilidad());

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

        JButton btnGuardar = new JButton(registroExistente == null ? "Guardar Aplicación" : "Actualizar Inmunización");
        btnGuardar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setBackground(COLOR_AZUL_PRIMARIO);
        btnGuardar.setIcon(Iconos.crearIconoCheck(14, Color.WHITE));
        btnGuardar.setIconTextGap(6);
        btnGuardar.setFocusPainted(false);
        btnGuardar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnGuardar.setPreferredSize(new Dimension(185, 34));
        btnGuardar.addActionListener(e -> guardarInmunizacion());

        footer.add(btnCancelar);
        footer.add(btnGuardar);
        add(footer, BorderLayout.SOUTH);

        if (registroExistente != null) {
            cargarDatosExistentes();
        } else if (mascotaInicial != null) {
            preseleccionarMascota(mascotaInicial);
        } else {
            autocompletarDatosPaciente();
        }
    }

    private JPanel crearTabAplicacion() {
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
        panel.add(crearEtiquetaCampo("Teléfono de Contacto"), gbc);
        y++;

        txtTutor = crearCampoTexto(false);
        txtTelefono = crearCampoTexto(false);
        gbc.gridx = 0; gbc.gridy = y;
        panel.add(txtTutor, gbc);
        gbc.gridx = 1; gbc.gridy = y;
        panel.add(txtTelefono, gbc);
        y++;

        // Tipo de Control y Biológico / Producto
        gbc.gridx = 0; gbc.gridy = y;
        panel.add(crearEtiquetaCampo("Tipo de Control Preventivo *"), gbc);
        gbc.gridx = 1; gbc.gridy = y;
        panel.add(crearEtiquetaCampo("Biológico / Producto Administrado *"), gbc);
        y++;

        cbTipoControl = new JComboBox<>(new String[]{"Vacunación", "Desparasitación Interna", "Desparasitación Externa", "Control Antiparasitario Integral"});
        cbTipoControl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cbTipoControl.setBackground(Color.WHITE);
        cbTipoControl.setPreferredSize(new Dimension(0, 30));
        cbTipoControl.addActionListener(e -> actualizarListaProductosSugeridos());

        cbProducto = new JComboBox<>();
        cbProducto.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cbProducto.setBackground(Color.WHITE);
        cbProducto.setEditable(true);
        cbProducto.setPreferredSize(new Dimension(0, 30));

        actualizarListaProductosSugeridos();

        gbc.gridx = 0; gbc.gridy = y;
        panel.add(cbTipoControl, gbc);
        gbc.gridx = 1; gbc.gridy = y;
        panel.add(cbProducto, gbc);
        y++;

        // Dosis / Vía y Laboratorio / Lote
        gbc.gridx = 0; gbc.gridy = y;
        panel.add(crearEtiquetaCampo("Dosis y Vía de Administración *"), gbc);
        gbc.gridx = 1; gbc.gridy = y;
        panel.add(crearEtiquetaCampo("Laboratorio Fabricante y Lote *"), gbc);
        y++;

        txtDosis = crearCampoTexto(true);
        txtDosis.setText("1.0 ml Subcutánea");

        txtLaboratorioLote = crearCampoTexto(true);
        txtLaboratorioLote.setText("Zoetis · Lote VAC-2024-9916");

        gbc.gridx = 0; gbc.gridy = y;
        panel.add(txtDosis, gbc);
        gbc.gridx = 1; gbc.gridy = y;
        panel.add(txtLaboratorioLote, gbc);
        y++;

        // Peso al dosificar
        gbc.gridx = 0; gbc.gridy = y; gbc.gridwidth = 2; gbc.weightx = 1.0;
        panel.add(crearEtiquetaCampo("Peso del Paciente al Dosificar (kg) *"), gbc);
        y++;

        txtPeso = crearCampoTexto(true);
        txtPeso.setText("15.0");
        gbc.gridx = 0; gbc.gridy = y; gbc.gridwidth = 2; gbc.weightx = 1.0;
        panel.add(txtPeso, gbc);

        JScrollPane scroll = new JScrollPane(panel);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(12);

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.add(scroll, BorderLayout.CENTER);
        return wrap;
    }

    private JPanel crearTabTrazabilidad() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(14, 18, 14, 18));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 4, 4, 4);
        int y = 0;

        // Fechas: Aplicación y Vencimiento Frasco
        gbc.gridwidth = 1; gbc.weightx = 0.5;
        gbc.gridx = 0; gbc.gridy = y;
        panel.add(crearEtiquetaCampo("Fecha de Aplicación (dd/MM/yyyy) *"), gbc);
        gbc.gridx = 1; gbc.gridy = y;
        panel.add(crearEtiquetaCampo("Vencimiento del Frasco / Lote"), gbc);
        y++;

        txtFechaAplicacion = crearCampoTexto(true);
        txtFechaAplicacion.setText(LocalDate.now().format(RegistroInmunizacion.FECHA_FORMATTER));

        txtFechaVencimiento = crearCampoTexto(true);
        txtFechaVencimiento.setText(LocalDate.now().plusMonths(18).format(RegistroInmunizacion.FECHA_FORMATTER));

        gbc.gridx = 0; gbc.gridy = y;
        panel.add(txtFechaAplicacion, gbc);
        gbc.gridx = 1; gbc.gridy = y;
        panel.add(txtFechaVencimiento, gbc);
        y++;

        // Próxima Fecha y Estado
        gbc.gridx = 0; gbc.gridy = y;
        panel.add(crearEtiquetaCampo("Próxima Dosis / Refuerzo Sugerido *"), gbc);
        gbc.gridx = 1; gbc.gridy = y;
        panel.add(crearEtiquetaCampo("Estado del Registro *"), gbc);
        y++;

        txtProximaFecha = crearCampoTexto(true);
        txtProximaFecha.setText(LocalDate.now().plusYears(1).format(RegistroInmunizacion.FECHA_FORMATTER));

        cbEstado = new JComboBox<>(new String[]{"Aplicada", "Próxima", "Pendiente"});
        cbEstado.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cbEstado.setBackground(Color.WHITE);
        cbEstado.setPreferredSize(new Dimension(0, 30));

        gbc.gridx = 0; gbc.gridy = y;
        panel.add(txtProximaFecha, gbc);
        gbc.gridx = 1; gbc.gridy = y;
        panel.add(cbEstado, gbc);
        y++;

        // Veterinario
        gbc.gridx = 0; gbc.gridy = y; gbc.gridwidth = 2; gbc.weightx = 1.0;
        panel.add(crearEtiquetaCampo("Veterinario Tratante Responsable *"), gbc);
        y++;

        cbVeterinario = new JComboBox<>(new String[]{
                "Dra. Camila Morales (Medicina Preventiva y Cirugía)",
                "Dr. Roberto Mendoza (Cardiología y Clínica General)",
                "Dr. Mario Silva (Dermatología y Vacunatorio)",
                "Dra. Elena Ruiz (Medicina Felina)",
                "Dra. Carmen Vega (Exóticos y Pequeños Animales)"
        });
        cbVeterinario.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cbVeterinario.setBackground(Color.WHITE);
        cbVeterinario.setPreferredSize(new Dimension(0, 30));

        gbc.gridx = 0; gbc.gridy = y; gbc.gridwidth = 2; gbc.weightx = 1.0;
        panel.add(cbVeterinario, gbc);
        y++;

        // Observaciones
        gbc.gridx = 0; gbc.gridy = y; gbc.gridwidth = 2; gbc.weightx = 1.0;
        panel.add(crearEtiquetaCampo("Observaciones Clínicas y Reacciones Inmediatas"), gbc);
        y++;

        txtObservaciones = new JTextArea(3, 20);
        txtObservaciones.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtObservaciones.setLineWrap(true);
        txtObservaciones.setWrapStyleWord(true);
        txtObservaciones.setText("Paciente normotérmico, examen clínico favorable. Tolera biológico sin reacciones adversas inmediatas.");
        txtObservaciones.setBorder(new EmptyBorder(6, 8, 6, 8));

        JScrollPane spObs = new JScrollPane(txtObservaciones);
        spObs.setBorder(BorderFactory.createLineBorder(new Color(203, 213, 225), 1));
        spObs.setPreferredSize(new Dimension(0, 75));

        gbc.gridx = 0; gbc.gridy = y; gbc.gridwidth = 2; gbc.weightx = 1.0;
        panel.add(spObs, gbc);

        JScrollPane scroll = new JScrollPane(panel);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(12);

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.add(scroll, BorderLayout.CENTER);
        return wrap;
    }

    private void actualizarListaProductosSugeridos() {
        if (cbTipoControl == null || cbProducto == null) return;
        String tipo = (String) cbTipoControl.getSelectedItem();
        cbProducto.removeAllItems();

        if (tipo != null && tipo.startsWith("Vacun")) {
            cbProducto.addItem("Séxtuple Canina DHPP+L (Distemper, Parvo, Hepatitis, Lepto)");
            cbProducto.addItem("Triple Felina (Panleucopenia, Rinotraqueítis, Calicivirus)");
            cbProducto.addItem("Vacuna Antirrábica Rabisin Anual");
            cbProducto.addItem("Tos de las Perreras (KC Bronchicine)");
            cbProducto.addItem("Leucemia Felina FeLV Recombinante");
            cbProducto.addItem("Quíntuple Canina DHPP");
            if (txtDosis != null) txtDosis.setText("1.0 ml Subcutánea");
            if (txtLaboratorioLote != null) txtLaboratorioLote.setText("Zoetis · Lote VAC-2024-9916");
            if (txtProximaFecha != null) txtProximaFecha.setText(LocalDate.now().plusYears(1).format(RegistroInmunizacion.FECHA_FORMATTER));
        } else {
            cbProducto.addItem("Simparica Trio Masticable (Sarolaner/Moxidectina/Pirantel)");
            cbProducto.addItem("Bravecto Plus Spot-on Pipeta Tópica");
            cbProducto.addItem("Nexgard Spectra Masticable");
            cbProducto.addItem("Total F Total Plus Comprimido Oral");
            cbProducto.addItem("Drontal Plus Sabor (Praziquantel/Pirantel)");
            if (txtDosis != null) txtDosis.setText("1 tableta / dosis según peso");
            if (txtLaboratorioLote != null) txtLaboratorioLote.setText("Elanco · Lote DESP-2024-118");
            if (txtProximaFecha != null) txtProximaFecha.setText(LocalDate.now().plusMonths(3).format(RegistroInmunizacion.FECHA_FORMATTER));
        }
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
            if (txtPeso != null && m.getPesoActualKg() > 0) {
                txtPeso.setText(String.format("%.1f", m.getPesoActualKg()).replace(",", "."));
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
        RegistroInmunizacion r = registroExistente;
        for (int i = 0; i < cbPaciente.getItemCount(); i++) {
            PacienteItem it = cbPaciente.getItemAt(i);
            if (it != null && it.mascota != null && it.mascota.getCodigo().equalsIgnoreCase(r.getCodigoMascota())) {
                cbPaciente.setSelectedIndex(i);
                break;
            }
        }
        cbPaciente.setEnabled(false);

        txtTutor.setText(r.getNombreTutor());
        txtTelefono.setText(r.getTelefonoTutor());

        for (int i = 0; i < cbTipoControl.getItemCount(); i++) {
            if (cbTipoControl.getItemAt(i).equalsIgnoreCase(r.getTipoControl())) {
                cbTipoControl.setSelectedIndex(i);
                break;
            }
        }

        cbProducto.setSelectedItem(r.getProducto());
        txtDosis.setText(r.getDosis());
        txtLaboratorioLote.setText(r.getLaboratorioLote());
        txtPeso.setText(String.format("%.1f", r.getPesoAplicacionKg()).replace(",", "."));

        txtFechaAplicacion.setText(r.getFechaAplicacionFormateada());
        txtFechaVencimiento.setText(r.getFechaVencimientoFormateada());
        txtProximaFecha.setText(r.getProximaFechaFormateada());

        for (int i = 0; i < cbVeterinario.getItemCount(); i++) {
            if (cbVeterinario.getItemAt(i).contains(r.getVeterinario())) {
                cbVeterinario.setSelectedIndex(i);
                break;
            }
        }

        cbEstado.setSelectedItem(r.getEstado());
        txtObservaciones.setText(r.getObservaciones());
    }

    private void guardarInmunizacion() {
        PacienteItem item = (PacienteItem) cbPaciente.getSelectedItem();
        if (item == null || item.mascota == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un paciente veterinario.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String prod = cbProducto.getSelectedItem() != null ? cbProducto.getSelectedItem().toString().trim() : "";
        if (prod.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe especificar el biológico o producto administrado.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double pesoVal = 10.0;
        try {
            pesoVal = Double.parseDouble(txtPeso.getText().trim().replace(",", "."));
        } catch (Exception ignored) {}

        LocalDate fechaApl = LocalDate.now();
        try {
            fechaApl = LocalDate.parse(txtFechaAplicacion.getText().trim(), RegistroInmunizacion.FECHA_FORMATTER);
        } catch (Exception ignored) {}

        LocalDate fechaVenc = null;
        try {
            if (!txtFechaVencimiento.getText().trim().isEmpty() && !txtFechaVencimiento.getText().trim().equals("-")) {
                fechaVenc = LocalDate.parse(txtFechaVencimiento.getText().trim(), RegistroInmunizacion.FECHA_FORMATTER);
            }
        } catch (Exception ignored) {}

        LocalDate fechaProx = null;
        try {
            if (!txtProximaFecha.getText().trim().isEmpty() && !txtProximaFecha.getText().trim().equals("-")) {
                fechaProx = LocalDate.parse(txtProximaFecha.getText().trim(), RegistroInmunizacion.FECHA_FORMATTER);
            }
        } catch (Exception ignored) {}

        Mascota m = item.mascota;
        String vetStr = cbVeterinario.getSelectedItem() != null ? cbVeterinario.getSelectedItem().toString() : "Dra. Camila Morales";
        String vetLimpio = vetStr.contains("(") ? vetStr.substring(0, vetStr.indexOf("(")).trim() : vetStr;

        RegistroInmunizacion reg;
        if (registroExistente != null) {
            reg = registroExistente;
        } else {
            reg = new RegistroInmunizacion("", (String) cbTipoControl.getSelectedItem(), m.getCodigo(), m.getNombre(),
                    m.getEspecie() + " · " + m.getRaza(), txtTutor.getText().trim(), txtTelefono.getText().trim(),
                    prod, txtLaboratorioLote.getText().trim(), txtDosis.getText().trim(), fechaApl, fechaVenc,
                    fechaProx, vetLimpio, pesoVal, txtObservaciones.getText().trim(), (String) cbEstado.getSelectedItem());
        }

        reg.setTipoControl((String) cbTipoControl.getSelectedItem());
        reg.setProducto(prod);
        reg.setLaboratorioLote(txtLaboratorioLote.getText().trim());
        reg.setDosis(txtDosis.getText().trim());
        reg.setFechaAplicacion(fechaApl);
        reg.setFechaVencimientoFrasco(fechaVenc);
        reg.setProximaFechaSugerida(fechaProx);
        reg.setVeterinario(vetLimpio);
        reg.setPesoAplicacionKg(pesoVal);
        reg.setObservaciones(txtObservaciones.getText().trim());
        reg.setEstado((String) cbEstado.getSelectedItem());

        repo.guardarInmunizacion(reg);

        if (onGuardado != null) {
            onGuardado.accept(reg);
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
