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
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import happypets.data.RepositorioVeterinaria;
import happypets.model.Cliente;
import happypets.model.Mascota;
import happypets.model.OrdenLaboratorio;
import happypets.ui.Iconos;
import happypets.ui.Ui;

/**
 * Diálogo modal profesional para la solicitud de exámenes diagnósticos y
 * registro de resultados de laboratorio e imágenes (Submódulo 3.4).
 */
public class FormularioOrdenLaboratorioDialog extends JDialog {
    private static final long serialVersionUID = 1L;

    private static final Color COLOR_AZUL_PRIMARIO = Ui.TURQUESA;
    private static final Color COLOR_BORDE = Ui.BORDE_SUAVE;
    private static final Color COLOR_TEXTO_TITULO = Ui.TEXTO_TITULO;
    private static final Color COLOR_TEXTO_MUTED = Ui.TEXTO_MUTED;

    private final RepositorioVeterinaria repo = RepositorioVeterinaria.getInstancia();
    private final Consumer<OrdenLaboratorio> onGuardado;
    private final OrdenLaboratorio ordenExistente;

    // Tab 1: Solicitud
    private JComboBox<PacienteItem> cbPaciente;
    private JTextField txtTutor;
    private JTextField txtTelefono;
    private JComboBox<String> cbCategoria;
    private JComboBox<String> cbTipoEstudio;
    private JComboBox<String> cbPrioridad;
    private JComboBox<String> cbVeterinario;
    private JTextField txtMotivo;
    private JTextField txtFechaSolicitud;

    // Tab 2: Resultados e Informe
    private JTextField txtResponsable;
    private JTextField txtFechaResultado;
    private JComboBox<String> cbEstado;
    private JTextArea txtValores;
    private JTextArea txtInforme;
    private JTextArea txtConclusiones;

    public static class PacienteItem {
        public final Mascota mascota;
        public PacienteItem(Mascota mascota) { this.mascota = mascota; }
        @Override
        public String toString() {
            return mascota != null ? mascota.getCodigo() + " - " + mascota.getNombre() + " (" + mascota.getEspecie() + " · " + mascota.getRaza() + ")" : "Seleccione paciente...";
        }
    }

    /**
     * Constructor de FormularioOrdenLaboratorioDialog.
     * Inicializa los componentes gráficos, configura layouts, asigna estilos
     * institucionales y carga los datos desde el repositorio central.
     */
    public FormularioOrdenLaboratorioDialog(Window parent, OrdenLaboratorio ordenExistente, Mascota mascotaInicial, Consumer<OrdenLaboratorio> onGuardado) {
        super(parent, ordenExistente == null ? "Nueva Solicitud de Laboratorio / Imagen" : "Editar Estudio " + ordenExistente.getIdOrden(), ModalityType.APPLICATION_MODAL);
        this.ordenExistente = ordenExistente;
        this.onGuardado = onGuardado;

        setSize(700, 680);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        // Cabecera Médica
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(13, 148, 136)); // Turquesa laboratorio
        header.setBorder(new EmptyBorder(16, 20, 16, 20));

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel headerTxt = new JPanel();
        headerTxt.setOpaque(false);
        headerTxt.setLayout(new BoxLayout(headerTxt, BoxLayout.Y_AXIS));

        JLabel title = new JLabel(ordenExistente == null ? "Orden de Laboratorio y Diagnóstico por Imágenes" : "Informe Diagnóstico de Laboratorio / Imagen");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(Color.WHITE);
        title.setIcon(Iconos.crearIconoMicroscopio(20, Color.WHITE));
        title.setIconTextGap(8);

        JLabel sub = new JLabel(ordenExistente == null ?
                "Solicitud de analítica clínica, radiología, ecografía y reporte de parámetros." :
                "ID Orden: " + ordenExistente.getIdOrden() + " · Paciente: " + ordenExistente.getNombreMascota());
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

        tabs.addTab("1. Solicitud y Paciente", Iconos.crearIconoMicroscopio(14, COLOR_AZUL_PRIMARIO), crearTabSolicitud());
        tabs.addTab("2. Resultados Analíticos & Conclusión", Iconos.crearIconoCheck(14, new Color(22, 163, 74)), crearTabResultados());

        add(tabs, BorderLayout.CENTER);

        // Footer
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

        JButton btnGuardar = new JButton(ordenExistente == null ? "Generar Solicitud" : "Guardar Resultados");
        btnGuardar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setBackground(COLOR_AZUL_PRIMARIO);
        btnGuardar.setIcon(Iconos.crearIconoCheck(14, Color.WHITE));
        btnGuardar.setIconTextGap(6);
        btnGuardar.setFocusPainted(false);
        btnGuardar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnGuardar.setPreferredSize(new Dimension(185, 34));
        btnGuardar.addActionListener(e -> guardarOrden());

        footer.add(btnCancelar);
        footer.add(btnGuardar);
        add(footer, BorderLayout.SOUTH);

        if (ordenExistente != null) {
            cargarDatosExistentes();
        } else if (mascotaInicial != null) {
            preseleccionarMascota(mascotaInicial);
        } else {
            autocompletarDatosPaciente();
        }
    }

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private JPanel crearTabSolicitud() {
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
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

        // Categoría y Estudio
        gbc.gridx = 0; gbc.gridy = y;
        panel.add(crearEtiquetaCampo("Categoría Diagnóstica *"), gbc);
        gbc.gridx = 1; gbc.gridy = y;
        panel.add(crearEtiquetaCampo("Tipo de Estudio Requerido *"), gbc);
        y++;

        cbCategoria = new JComboBox<>(new String[]{"Laboratorio Clínico", "Diagnóstico por Imágenes"});
        cbCategoria.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cbCategoria.setBackground(Color.WHITE);
        cbCategoria.setPreferredSize(new Dimension(0, 30));
        cbCategoria.addActionListener(e -> actualizarEstudiosSugeridos());

        cbTipoEstudio = new JComboBox<>();
        cbTipoEstudio.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cbTipoEstudio.setBackground(Color.WHITE);
        cbTipoEstudio.setEditable(true);
        cbTipoEstudio.setPreferredSize(new Dimension(0, 30));

        actualizarEstudiosSugeridos();

        gbc.gridx = 0; gbc.gridy = y;
        panel.add(cbCategoria, gbc);
        gbc.gridx = 1; gbc.gridy = y;
        panel.add(cbTipoEstudio, gbc);
        y++;

        // Prioridad y Veterinario
        gbc.gridx = 0; gbc.gridy = y;
        panel.add(crearEtiquetaCampo("Prioridad Clínica *"), gbc);
        gbc.gridx = 1; gbc.gridy = y;
        panel.add(crearEtiquetaCampo("Veterinario Solicitante *"), gbc);
        y++;

        cbPrioridad = new JComboBox<>(new String[]{"Normal", "Urgente", "Emergencia"});
        cbPrioridad.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cbPrioridad.setBackground(Color.WHITE);
        cbPrioridad.setPreferredSize(new Dimension(0, 30));

        cbVeterinario = new JComboBox<>(new String[]{
                "Dra. Camila Morales (Medicina Interna)",
                "Dr. Roberto Mendoza (Cardiología)",
                "Dr. Mario Silva (Dermatología)",
                "Dra. Elena Ruiz (Medicina Felina)",
                "Dr. Carlos Méndez (Cuidados Críticos)"
        });
        cbVeterinario.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cbVeterinario.setBackground(Color.WHITE);
        cbVeterinario.setPreferredSize(new Dimension(0, 30));

        gbc.gridx = 0; gbc.gridy = y;
        panel.add(cbPrioridad, gbc);
        gbc.gridx = 1; gbc.gridy = y;
        panel.add(cbVeterinario, gbc);
        y++;

        // Motivo / Sospecha y Fecha Solicitud
        gbc.gridx = 0; gbc.gridy = y;
        panel.add(crearEtiquetaCampo("Motivo Clínico / Sospecha Diagnóstica *"), gbc);
        gbc.gridx = 1; gbc.gridy = y;
        panel.add(crearEtiquetaCampo("Fecha de Solicitud (dd/MM/yyyy) *"), gbc);
        y++;

        txtMotivo = crearCampoTexto(true);
        txtMotivo.setText("Descarte de anemia, proceso inflamatorio y chequeo prequirúrgico");

        txtFechaSolicitud = crearCampoTexto(true);
        txtFechaSolicitud.setText(LocalDate.now().format(OrdenLaboratorio.FECHA_FORMATTER));

        gbc.gridx = 0; gbc.gridy = y;
        panel.add(txtMotivo, gbc);
        gbc.gridx = 1; gbc.gridy = y;
        panel.add(txtFechaSolicitud, gbc);

        JScrollPane scroll = new JScrollPane(panel);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(12);

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.add(scroll, BorderLayout.CENTER);
        return wrap;
    }

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private JPanel crearTabResultados() {
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(14, 18, 14, 18));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 4, 4, 4);
        int y = 0;

        // Responsable y Fecha Resultado
        gbc.gridwidth = 1; gbc.weightx = 0.5;
        gbc.gridx = 0; gbc.gridy = y;
        panel.add(crearEtiquetaCampo("Especialista Responsable del Procesamiento *"), gbc);
        gbc.gridx = 1; gbc.gridy = y;
        panel.add(crearEtiquetaCampo("Fecha del Resultado (dd/MM/yyyy)"), gbc);
        y++;

        txtResponsable = crearCampoTexto(true);
        txtResponsable.setText("Lic. Marcos Céspedes (Laboratorio Central)");

        txtFechaResultado = crearCampoTexto(true);
        txtFechaResultado.setText(LocalDate.now().format(OrdenLaboratorio.FECHA_FORMATTER));

        gbc.gridx = 0; gbc.gridy = y;
        panel.add(txtResponsable, gbc);
        gbc.gridx = 1; gbc.gridy = y;
        panel.add(txtFechaResultado, gbc);
        y++;

        // Estado de la orden
        gbc.gridx = 0; gbc.gridy = y; gbc.gridwidth = 2; gbc.weightx = 1.0;
        panel.add(crearEtiquetaCampo("Estado del Estudio *"), gbc);
        y++;

        cbEstado = new JComboBox<>(new String[]{"Completado", "En Proceso", "Solicitado"});
        cbEstado.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cbEstado.setBackground(Color.WHITE);
        cbEstado.setPreferredSize(new Dimension(0, 30));

        gbc.gridx = 0; gbc.gridy = y; gbc.gridwidth = 2; gbc.weightx = 1.0;
        panel.add(cbEstado, gbc);
        y++;

        // Valores analíticos / Hallazgos Radiológicos
        gbc.gridx = 0; gbc.gridy = y; gbc.gridwidth = 2; gbc.weightx = 1.0;
        panel.add(crearEtiquetaCampo("Valores Analíticos Cuantitativos / Parámetros Obtenidos"), gbc);
        y++;

        txtValores = new JTextArea(3, 20);
        txtValores.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtValores.setLineWrap(true);
        txtValores.setWrapStyleWord(true);
        txtValores.setText("Eritrocitos: 6.8 M/uL (Ref: 5.5-8.5) · Hematocrito: 44% (Ref: 37-55) · Hemoglobina: 15.2 g/dL (Ref: 12-18) · Leucocitos: 11,200/uL (Ref: 6,000-17,000) · Plaquetas: 320,000/uL.");
        txtValores.setBorder(new EmptyBorder(6, 8, 6, 8));

        JScrollPane spVal = new JScrollPane(txtValores);
        spVal.setBorder(BorderFactory.createLineBorder(new Color(203, 213, 225), 1));
        spVal.setPreferredSize(new Dimension(0, 65));

        gbc.gridx = 0; gbc.gridy = y; gbc.gridwidth = 2; gbc.weightx = 1.0;
        panel.add(spVal, gbc);
        y++;

        // Informe Detallado
        gbc.gridx = 0; gbc.gridy = y; gbc.gridwidth = 2; gbc.weightx = 1.0;
        panel.add(crearEtiquetaCampo("Informe Descriptivo e Interpretación Médica"), gbc);
        y++;

        txtInforme = new JTextArea(3, 20);
        txtInforme.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtInforme.setLineWrap(true);
        txtInforme.setWrapStyleWord(true);
        txtInforme.setText("Línea roja normocítica y normocrómica sin signos de regeneración anómala. Serie blanca con distribución porcentual neutrófilos/linfocitos conservada. Recuento plaquetario adecuado.");
        txtInforme.setBorder(new EmptyBorder(6, 8, 6, 8));

        JScrollPane spInf = new JScrollPane(txtInforme);
        spInf.setBorder(BorderFactory.createLineBorder(new Color(203, 213, 225), 1));
        spInf.setPreferredSize(new Dimension(0, 65));

        gbc.gridx = 0; gbc.gridy = y; gbc.gridwidth = 2; gbc.weightx = 1.0;
        panel.add(spInf, gbc);
        y++;

        // Conclusiones y Recomendaciones
        gbc.gridx = 0; gbc.gridy = y; gbc.gridwidth = 2; gbc.weightx = 1.0;
        panel.add(crearEtiquetaCampo("Conclusión Diagnóstica y Recomendaciones"), gbc);
        y++;

        txtConclusiones = new JTextArea(2, 20);
        txtConclusiones.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtConclusiones.setLineWrap(true);
        txtConclusiones.setWrapStyleWord(true);
        txtConclusiones.setText("Estudio sin alteraciones patológicas significativas. Paciente apto para procedimiento quirúrgico programado.");
        txtConclusiones.setBorder(new EmptyBorder(6, 8, 6, 8));

        JScrollPane spConc = new JScrollPane(txtConclusiones);
        spConc.setBorder(BorderFactory.createLineBorder(new Color(203, 213, 225), 1));
        spConc.setPreferredSize(new Dimension(0, 55));

        gbc.gridx = 0; gbc.gridy = y; gbc.gridwidth = 2; gbc.weightx = 1.0;
        panel.add(spConc, gbc);

        JScrollPane scroll = new JScrollPane(panel);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(12);

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.add(scroll, BorderLayout.CENTER);
        return wrap;
    }

    private void actualizarEstudiosSugeridos() {
        if (cbCategoria == null || cbTipoEstudio == null) return;
        String cat = (String) cbCategoria.getSelectedItem();
        cbTipoEstudio.removeAllItems();

        if (cat != null && cat.contains("Imágenes")) {
            cbTipoEstudio.addItem("Radiografía Digital de Tórax (Lat-Lat / VD)");
            cbTipoEstudio.addItem("Radiografía Abdominal Simple y Contrastada");
            cbTipoEstudio.addItem("Ecografía Abdominal Completa de Alta Resolución");
            cbTipoEstudio.addItem("Ecocardiografía Doppler Color");
            cbTipoEstudio.addItem("Radiografía Ortopédica de Miembros");
            if (txtValores != null) txtValores.setText("Campos pulmonares con patrón vascular conservado. Silueta cardíaca dentro de rangos normales (Índice vertebral VHS = 9.8). Eje traqueal preservado.");
            if (txtInforme != null) txtInforme.setText("No se observan signos de consolidación alveolar, efusión pleural ni metástasis radiopacas. Estructura ósea torácica íntegra.");
            if (txtConclusiones != null) txtConclusiones.setText("Tórax radiológicamente compatible con la normalidad.");
        } else {
            cbTipoEstudio.addItem("Hemograma Completo + Plaquetas");
            cbTipoEstudio.addItem("Bioquímica Sanguínea (Creatinina, Urea, ALT, Fosfatasa)");
            cbTipoEstudio.addItem("Urianálisis y Sedimento Microscópico");
            cbTipoEstudio.addItem("Perfil Pre-Quirúrgico Completo");
            cbTipoEstudio.addItem("Citología por Aspiración con Aguja Fina (PAAF)");
            cbTipoEstudio.addItem("Coprológico Funcional y Flotación");
            if (txtValores != null) txtValores.setText("Eritrocitos: 6.8 M/uL (Ref: 5.5-8.5) · Hematocrito: 44% (Ref: 37-55) · Hemoglobina: 15.2 g/dL · Leucocitos: 11,200/uL · Plaquetas: 320,000/uL.");
            if (txtInforme != null) txtInforme.setText("Línea celular sanguínea conservada. Plaquetas en acúmulos normales sin agregación patológica.");
            if (txtConclusiones != null) txtConclusiones.setText("Hemograma dentro de los límites de referencia biológica.");
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
        OrdenLaboratorio o = ordenExistente;
        for (int i = 0; i < cbPaciente.getItemCount(); i++) {
            PacienteItem it = cbPaciente.getItemAt(i);
            if (it != null && it.mascota != null && it.mascota.getCodigo().equalsIgnoreCase(o.getCodigoMascota())) {
                cbPaciente.setSelectedIndex(i);
                break;
            }
        }
        cbPaciente.setEnabled(false);

        txtTutor.setText(o.getNombreTutor());
        txtTelefono.setText(o.getTelefonoTutor());

        cbCategoria.setSelectedItem(o.getCategoria());
        cbTipoEstudio.setSelectedItem(o.getTipoEstudio());
        cbPrioridad.setSelectedItem(o.getPrioridad());

        for (int i = 0; i < cbVeterinario.getItemCount(); i++) {
            if (cbVeterinario.getItemAt(i).contains(o.getVeterinarioSolicitante())) {
                cbVeterinario.setSelectedIndex(i);
                break;
            }
        }

        txtMotivo.setText(o.getMotivoSospecha());
        txtFechaSolicitud.setText(o.getFechaSolicitudFormateada());

        txtResponsable.setText(o.getResponsableProcesamiento());
        txtFechaResultado.setText(o.getFechaResultadoFormateada());
        cbEstado.setSelectedItem(o.getEstado());
        txtValores.setText(o.getResultadoValores());
        txtInforme.setText(o.getInformeDetallado());
        txtConclusiones.setText(o.getConclusionesRecomendaciones());
    }

    private void guardarOrden() {
        PacienteItem item = (PacienteItem) cbPaciente.getSelectedItem();
        if (item == null || item.mascota == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un paciente veterinario.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String est = cbTipoEstudio.getSelectedItem() != null ? cbTipoEstudio.getSelectedItem().toString().trim() : "";
        if (est.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe especificar el tipo de estudio solicitado.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        LocalDate fSol = LocalDate.now();
        try {
            fSol = LocalDate.parse(txtFechaSolicitud.getText().trim(), OrdenLaboratorio.FECHA_FORMATTER);
        } catch (Exception ignored) {}

        LocalDate fRes = null;
        try {
            if (!txtFechaResultado.getText().trim().isEmpty() && !txtFechaResultado.getText().trim().equals("-")) {
                fRes = LocalDate.parse(txtFechaResultado.getText().trim(), OrdenLaboratorio.FECHA_FORMATTER);
            }
        } catch (Exception ignored) {}

        Mascota m = item.mascota;
        String vetStr = cbVeterinario.getSelectedItem() != null ? cbVeterinario.getSelectedItem().toString() : "Dra. Camila Morales";
        String vetLimpio = vetStr.contains("(") ? vetStr.substring(0, vetStr.indexOf("(")).trim() : vetStr;

        OrdenLaboratorio ord;
        if (ordenExistente != null) {
            ord = ordenExistente;
        } else {
            ord = new OrdenLaboratorio("", (String) cbCategoria.getSelectedItem(), est,
                    m.getCodigo(), m.getNombre(), m.getEspecie() + " · " + m.getRaza(),
                    txtTutor.getText().trim(), txtTelefono.getText().trim(), fSol,
                    (String) cbPrioridad.getSelectedItem(), vetLimpio, txtMotivo.getText().trim(),
                    fRes, txtResponsable.getText().trim(), txtValores.getText().trim(),
                    txtInforme.getText().trim(), txtConclusiones.getText().trim(), (String) cbEstado.getSelectedItem());
        }

        ord.setCategoria((String) cbCategoria.getSelectedItem());
        ord.setTipoEstudio(est);
        ord.setPrioridad((String) cbPrioridad.getSelectedItem());
        ord.setVeterinarioSolicitante(vetLimpio);
        ord.setMotivoSospecha(txtMotivo.getText().trim());
        ord.setFechaSolicitud(fSol);
        ord.setFechaResultado(fRes);
        ord.setResponsableProcesamiento(txtResponsable.getText().trim());
        ord.setResultadoValores(txtValores.getText().trim());
        ord.setInformeDetallado(txtInforme.getText().trim());
        ord.setConclusionesRecomendaciones(txtConclusiones.getText().trim());
        ord.setEstado((String) cbEstado.getSelectedItem());

        repo.guardarOrdenLaboratorio(ord);

        if (onGuardado != null) {
            onGuardado.accept(ord);
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
