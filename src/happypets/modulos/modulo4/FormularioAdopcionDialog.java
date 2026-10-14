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

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
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
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import happypets.data.RepositorioVeterinaria;
import happypets.model.MascotaAdopcion;
import happypets.ui.Iconos;
import happypets.ui.Ui;

/**
 * Diálogo modal profesional para registrar o editar un expediente de Adopción y Rescate Responsable.
 * Organizado en dos pestañas:
 *  1. Perfil Sanitario & Ficha del Rescatado
 *  2. Datos del Adoptante / Postulación
 */
public class FormularioAdopcionDialog extends JDialog {
    private static final long serialVersionUID = 1L;

    private final RepositorioVeterinaria repo = RepositorioVeterinaria.getInstancia();
    private boolean guardado = false;
    private MascotaAdopcion mascotaResultante = null;
    private final MascotaAdopcion mascotaOriginal;

    // Componentes - Tab 1
    private JTextField txtNombre;
    private JComboBox<String> cbEspecie;
    private JTextField txtRaza;
    private JTextField txtEdad;
    private JComboBox<String> cbSexo;
    private JComboBox<String> cbTamano;
    private JCheckBox chkEsterilizado;
    private JCheckBox chkVacunado;
    private JCheckBox chkDesparasitado;
    private JTextField txtTemperamento;
    private JTextArea txtHistoriaRescate;
    private JTextField txtDonacion;
    private JComboBox<String> cbEstado;

    // Componentes - Tab 2
    private JTextField txtAdoptanteNombre;
    private JTextField txtAdoptanteDni;
    private JTextField txtAdoptanteTelefono;
    private JTextField txtAdoptanteDireccion;

    /**
     * Constructor de FormularioAdopcionDialog.
     * Inicializa los componentes gráficos, configura layouts, asigna estilos
     * institucionales y carga los datos desde el repositorio central.
     */
    public FormularioAdopcionDialog(Window owner) {
        this(owner, null);
    }

    /**
     * Constructor de FormularioAdopcionDialog.
     * Inicializa los componentes gráficos, configura layouts, asigna estilos
     * institucionales y carga los datos desde el repositorio central.
     */
    public FormularioAdopcionDialog(Window owner, MascotaAdopcion edicion) {
        super(owner, edicion == null ? "Registrar Nuevo Animal Rescatado" : "Editar Ficha de Rescatado: " + edicion.getIdMascotaAdopcion(), ModalityType.APPLICATION_MODAL);
        this.mascotaOriginal = edicion;

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
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel cabecera = new JPanel(new BorderLayout());
        cabecera.setBackground(Color.WHITE);
        cabecera.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Ui.BORDE_SUAVE),
                new EmptyBorder(16, 20, 16, 20)
        ));

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel titBox = new JPanel();
        titBox.setOpaque(false);
        titBox.setLayout(new BoxLayout(titBox, BoxLayout.Y_AXIS));

        JLabel lblTit = new JLabel(mascotaOriginal == null ? "Nuevo Expediente de Rescate & Adopción" : "Modificar Ficha · " + mascotaOriginal.getIdMascotaAdopcion());
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTit.setForeground(Ui.TEXTO_TITULO);

        JLabel lblSub = new JLabel("Catálogo de rescatados, perfil sanitario obligatorio y evaluación ética de adoptantes");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(Ui.TEXTO_MUTED);

        titBox.add(lblTit);
        titBox.add(Box.createVerticalStrut(3));
        titBox.add(lblSub);

        JLabel iconLabel = new JLabel(Iconos.crearIconoCorazonMascota(28, Ui.TURQUESA_PROFUNDO));
        cabecera.add(titBox, BorderLayout.CENTER);
        cabecera.add(iconLabel, BorderLayout.EAST);
        add(cabecera, BorderLayout.NORTH);

        // Pestañas
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabs.setBackground(Color.WHITE);

        tabs.addTab("1. Perfil del Rescatado", Iconos.crearIconoCorazonMascota(14, Ui.TURQUESA), crearTabRescatado());
        tabs.addTab("2. Postulante / Adoptante", Iconos.crearIconoDocumento(14, Ui.COLOR_EXITO), crearTabAdoptante());
        add(tabs, BorderLayout.CENTER);

        // Barra inferior de botones
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel barraBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        barraBotones.setBackground(Color.WHITE);
        barraBotones.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Ui.BORDE_SUAVE));

        JButton btnCancelar = crearBotonAccion("Cancelar", false);
        btnCancelar.addActionListener(e -> dispose());

        JButton btnGuardar = crearBotonAccion("Guardar Ficha de Adopción", true);
        btnGuardar.addActionListener(e -> guardarAdopcion());

        barraBotones.add(btnCancelar);
        barraBotones.add(btnGuardar);
        add(barraBotones, BorderLayout.SOUTH);
    }

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private JPanel crearTabRescatado() {
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(new EmptyBorder(16, 20, 16, 20));

        // Nombre y Especie
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel filaNom = new JPanel(new GridLayout(1, 2, 10, 0));
        filaNom.setOpaque(false);
        filaNom.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        txtNombre = crearTextField(true);
        cbEspecie = new JComboBox<>(new String[]{"Canino", "Felino"});
        cbEspecie.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cbEspecie.setBackground(Color.WHITE);

        filaNom.add(crearFilaCampo("Nombre del Rescatado *", txtNombre));
        filaNom.add(crearFilaCampo("Especie *", cbEspecie));
        p.add(filaNom);
        p.add(Box.createVerticalStrut(8));

        // Raza, Edad, Sexo y Tamaño
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel filaDet = new JPanel(new GridLayout(1, 4, 8, 0));
        filaDet.setOpaque(false);
        filaDet.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        txtRaza = crearTextField(true);
        txtRaza.setText("Mestizo");

        txtEdad = crearTextField(true);
        txtEdad.setText("1 año y medio");

        cbSexo = new JComboBox<>(new String[]{"Macho", "Hembra"});
        cbSexo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cbSexo.setBackground(Color.WHITE);

        cbTamano = new JComboBox<>(new String[]{"Pequeño", "Mediano", "Grande"});
        cbTamano.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cbTamano.setBackground(Color.WHITE);
        cbTamano.setSelectedItem("Mediano");

        filaDet.add(crearFilaCampo("Raza / Tipo *", txtRaza));
        filaDet.add(crearFilaCampo("Edad Estimada *", txtEdad));
        filaDet.add(crearFilaCampo("Sexo *", cbSexo));
        filaDet.add(crearFilaCampo("Porte / Tamaño *", cbTamano));
        p.add(filaDet);
        p.add(Box.createVerticalStrut(8));

        // Checklist Sanitario
        JLabel lblSan = new JLabel("Protocolo Sanitario Oficial (Ley 30407):");
        lblSan.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblSan.setForeground(Ui.TEXTO_TITULO);
        p.add(lblSan);
        p.add(Box.createVerticalStrut(4));

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel panelChecks = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        panelChecks.setOpaque(false);
        panelChecks.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));

        chkEsterilizado = new JCheckBox("Esterilizado/a Quirúrgico");
        chkEsterilizado.setFont(new Font("Segoe UI", Font.BOLD, 11));
        chkEsterilizado.setForeground(Ui.COLOR_EXITO);
        chkEsterilizado.setOpaque(false);
        chkEsterilizado.setSelected(true);

        chkVacunado = new JCheckBox("Vacunación Completa");
        chkVacunado.setFont(new Font("Segoe UI", Font.BOLD, 11));
        chkVacunado.setForeground(Ui.COLOR_EXITO);
        chkVacunado.setOpaque(false);
        chkVacunado.setSelected(true);

        chkDesparasitado = new JCheckBox("Desparasitación al Día");
        chkDesparasitado.setFont(new Font("Segoe UI", Font.BOLD, 11));
        chkDesparasitado.setForeground(Ui.COLOR_EXITO);
        chkDesparasitado.setOpaque(false);
        chkDesparasitado.setSelected(true);

        panelChecks.add(chkEsterilizado);
        panelChecks.add(chkVacunado);
        panelChecks.add(chkDesparasitado);
        p.add(panelChecks);
        p.add(Box.createVerticalStrut(8));

        // Temperamento
        txtTemperamento = crearTextField(true);
        txtTemperamento.setText("Sociable con otros perros y niños. Juguetón y dócil.");
        p.add(crearFilaCampo("Temperamento & Nivel de Convivencia", txtTemperamento));
        p.add(Box.createVerticalStrut(8));

        // Historia de Rescate
        txtHistoriaRescate = new JTextArea(2, 20);
        txtHistoriaRescate.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtHistoriaRescate.setLineWrap(true);
        txtHistoriaRescate.setWrapStyleWord(true);
        txtHistoriaRescate.setText("Rescatado en operativo urbano. Rehabilitado clínicamente y listo para adopción.");
        txtHistoriaRescate.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Ui.BORDE_SUAVE, 1),
                new EmptyBorder(4, 6, 4, 6)
        ));
        JScrollPane spHis = new JScrollPane(txtHistoriaRescate);
        spHis.setPreferredSize(new Dimension(0, 48));
        spHis.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));
        spHis.setBorder(null);
        p.add(crearFilaCampo("Historia del Rescate / Antecedentes", spHis));
        p.add(Box.createVerticalStrut(8));

        // Donación y Estado
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel filaCierre = new JPanel(new GridLayout(1, 2, 10, 0));
        filaCierre.setOpaque(false);
        filaCierre.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        txtDonacion = crearTextField(true);
        txtDonacion.setText("50.00");

        cbEstado = new JComboBox<>(new String[]{
                "Disponible",
                "En Evaluación",
                "En Adaptación (Prueba)",
                "Adoptado con Éxito"
        });
        cbEstado.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cbEstado.setBackground(Color.WHITE);

        filaCierre.add(crearFilaCampo("Cuota Solidaria Donación S/.", txtDonacion));
        filaCierre.add(crearFilaCampo("Estado de Adopción *", cbEstado));
        p.add(filaCierre);

        return p;
    }

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private JPanel crearTabAdoptante() {
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(new EmptyBorder(16, 20, 16, 20));

        JLabel lblInfo = new JLabel("Datos del Postulante / Futuro Tutor Responsable:");
        lblInfo.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblInfo.setForeground(Ui.TEXTO_TITULO);
        p.add(lblInfo);
        p.add(Box.createVerticalStrut(8));

        // Nombre y DNI
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel filaPost = new JPanel(new GridLayout(1, 2, 10, 0));
        filaPost.setOpaque(false);
        filaPost.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        txtAdoptanteNombre = crearTextField(true);
        txtAdoptanteDni = crearTextField(true);

        filaPost.add(crearFilaCampo("Nombre Completo del Adoptante", txtAdoptanteNombre));
        filaPost.add(crearFilaCampo("DNI / Documento de Identidad", txtAdoptanteDni));
        p.add(filaPost);
        p.add(Box.createVerticalStrut(10));

        // Teléfono
        txtAdoptanteTelefono = crearTextField(true);
        p.add(crearFilaCampo("Teléfono Celular / WhatsApp", txtAdoptanteTelefono));
        p.add(Box.createVerticalStrut(10));

        // Domicilio y tipo de vivienda
        txtAdoptanteDireccion = crearTextField(true);
        txtAdoptanteDireccion.setText("Casa propia con jardín cercado");
        p.add(crearFilaCampo("Dirección de Domicilio & Tipo de Vivienda", txtAdoptanteDireccion));

        return p;
    }

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private JPanel crearFilaCampo(String etiqueta, Component campo) {
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
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

    private void cargarDatosExistentes(MascotaAdopcion m) {
        txtNombre.setText(m.getNombre());
        cbEspecie.setSelectedItem(m.getEspecie());
        txtRaza.setText(m.getRaza());
        txtEdad.setText(m.getEdadEstimada());
        cbSexo.setSelectedItem(m.getSexo());
        cbTamano.setSelectedItem(m.getTamano());
        chkEsterilizado.setSelected(m.isEsterilizado());
        chkVacunado.setSelected(m.isVacunado());
        chkDesparasitado.setSelected(m.isDesparasitado());
        txtTemperamento.setText(m.getTemperamento());
        txtHistoriaRescate.setText(m.getHistoriaRescate());
        txtDonacion.setText(String.format("%.2f", m.getCuotaDonacion()));
        cbEstado.setSelectedItem(m.getEstado());

        if (m.getAdoptanteNombre() != null) txtAdoptanteNombre.setText(m.getAdoptanteNombre());
        if (m.getAdoptanteDni() != null) txtAdoptanteDni.setText(m.getAdoptanteDni());
        if (m.getAdoptanteTelefono() != null) txtAdoptanteTelefono.setText(m.getAdoptanteTelefono());
        if (m.getAdoptanteDireccion() != null) txtAdoptanteDireccion.setText(m.getAdoptanteDireccion());
    }

    private void guardarAdopcion() {
        String nom = txtNombre.getText().trim();
        if (nom.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe ingresar el nombre del animal rescatado.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double donacion = 50.0;
        try {
            donacion = Double.parseDouble(txtDonacion.getText().trim().replace(",", "."));
        } catch (Exception ignored) {}

        String id = mascotaOriginal != null ? mascotaOriginal.getIdMascotaAdopcion() : null;

        mascotaResultante = new MascotaAdopcion(
                id,
                nom,
                (String) cbEspecie.getSelectedItem(),
                txtRaza.getText().trim(),
                txtEdad.getText().trim(),
                (String) cbSexo.getSelectedItem(),
                (String) cbTamano.getSelectedItem(),
                txtTemperamento.getText().trim(),
                txtHistoriaRescate.getText().trim(),
                chkEsterilizado.isSelected(),
                chkVacunado.isSelected(),
                chkDesparasitado.isSelected(),
                txtAdoptanteNombre.getText().trim(),
                txtAdoptanteDni.getText().trim(),
                txtAdoptanteTelefono.getText().trim(),
                txtAdoptanteDireccion.getText().trim(),
                mascotaOriginal != null ? mascotaOriginal.getFechaSolicitud() : LocalDate.now(),
                donacion,
                (String) cbEstado.getSelectedItem()
        );

        repo.guardarMascotaAdopcion(mascotaResultante);
        guardado = true;
        dispose();
    }

    public boolean isGuardado() {
        return guardado;
    }

    public MascotaAdopcion getMascotaResultante() {
        return mascotaResultante;
    }

    private JButton crearBotonAccion(String texto, boolean primario) {
        JButton btn = new JButton(texto) {
            private static final long serialVersionUID = 1L;
            @Override
            /**
             * Renderizado gráfico personalizado en Java2D.
             * Activa Antialiasing para bordes suaves y realiza el cálculo geométrico preciso.
             */
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
}
