package happypets.modulos.modulo1;

import java.awt.BorderLayout;
import java.awt.Color;
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
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
import javax.swing.JComponent;
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
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import happypets.data.RepositorioVeterinaria;
import happypets.model.ConsultaClinica;
import happypets.model.Mascota;
import happypets.ui.Iconos;
import happypets.ui.Ui;

/**
 * Diálogo modal profesional para registrar o editar consultas clínicas veterinarias.
 * Incluye validación de signos vitales, anamnesis, diagnóstico y plan farmacológico.
 */
public class FormularioConsultaDialog extends JDialog {
    private static final long serialVersionUID = 1L;

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final Mascota mascota;
    private final ConsultaClinica consultaExistente;

    private final JComboBox<String> cbVeterinario;
    private final JTextField txtFecha;
    private final JComboBox<String> cbEstado;
    private final JTextField txtMotivo;
    private final JTextField txtPesoKg;
    private final JTextField txtTemperaturaC;
    private final JTextArea txtSintomas;
    private final JTextArea txtDiagnostico;
    private final JTextArea txtTratamiento;
    private final JTextArea txtObservaciones;

    private boolean guardado = false;
    private ConsultaClinica consultaResultado;

    /**
     * Constructor de FormularioConsultaDialog.
     * Inicializa los componentes gráficos, configura layouts, asigna estilos
     * institucionales y carga los datos desde el repositorio central.
     */
    public FormularioConsultaDialog(Window parent, Mascota mascota, ConsultaClinica consultaExistente, Consumer<ConsultaClinica> onGuardar) {
        super(parent, consultaExistente == null ? "Registrar Consulta Clínica" : "Modificar Consulta Clínica", ModalityType.APPLICATION_MODAL);
        this.mascota = mascota;
        this.consultaExistente = consultaExistente;

        setSize(640, 720);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        // 1. Cabecera superior estilizada
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Ui.TURQUESA);
        header.setBorder(new EmptyBorder(14, 20, 14, 20));

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel headerTxt = new JPanel();
        headerTxt.setOpaque(false);
        headerTxt.setLayout(new BoxLayout(headerTxt, BoxLayout.Y_AXIS));

        JLabel title = new JLabel(consultaExistente == null ? "Nueva Consulta Clínica Veterinaria" : "Modificar Atención Médica " + consultaExistente.getCodigo());
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(Color.WHITE);
        title.setIcon(Iconos.crearIconoEstetoscopio(20, Color.WHITE));
        title.setIconTextGap(8);

        String petInfo = mascota != null ? "Paciente: " + mascota.getNombre() + " (" + mascota.getEspecie() + " · " + mascota.getRaza() + " · " + mascota.getCodigo() + ")" : "Registro médico veterinario";
        JLabel sub = new JLabel(petInfo);
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sub.setForeground(new Color(224, 242, 254));

        headerTxt.add(title);
        headerTxt.add(Box.createVerticalStrut(2));
        headerTxt.add(sub);
        header.add(headerTxt, BorderLayout.CENTER);
        add(header, BorderLayout.NORTH);

        // 2. Formulario central
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        form.setBorder(new EmptyBorder(14, 20, 14, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Fila 0: Veterinario tratante
        cbVeterinario = new JComboBox<>(new String[]{
                "Dr. Roberto Mendoza",
                "Dra. Patricia Flores",
                "Dr. Alejandro Valdivia",
                "Dra. Lucía Castro",
                "Dr. Carlos Silva"
        });
        cbVeterinario.setEditable(true);
        cbVeterinario.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cbVeterinario.setPreferredSize(new Dimension(240, 30));
        cbVeterinario.setBackground(Color.WHITE);

        // Fila 1: Fecha y Estado
        txtFecha = Ui.campoTexto(10);
        txtFecha.setText(LocalDate.now().format(FORMATO_FECHA));
        txtFecha.setToolTipText("Formato: dd/MM/yyyy");

        cbEstado = new JComboBox<>(new String[]{"Cerrada", "En Tratamiento", "Control Programado"});
        cbEstado.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cbEstado.setPreferredSize(new Dimension(140, 30));
        cbEstado.setBackground(Color.WHITE);

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel panelFechaEstado = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        panelFechaEstado.setOpaque(false);
        panelFechaEstado.add(txtFecha);
        JLabel lblEst = new JLabel("Estado:");
        lblEst.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblEst.setForeground(Ui.TEXTO_TITULO);
        panelFechaEstado.add(lblEst);
        panelFechaEstado.add(cbEstado);

        // Fila 2: Motivo
        txtMotivo = Ui.campoTexto(20);

        // Fila 3: Signos Vitales (Peso y Temperatura)
        txtPesoKg = Ui.campoTexto(6);
        if (mascota != null && mascota.getPesoActualKg() > 0) {
            txtPesoKg.setText(String.format(java.util.Locale.US, "%.1f", mascota.getPesoActualKg()));
        } else {
            txtPesoKg.setText("0.0");
        }

        txtTemperaturaC = Ui.campoTexto(6);
        txtTemperaturaC.setText("38.5");

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel panelSignos = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        panelSignos.setOpaque(false);
        JLabel lblKg = new JLabel("kg");
        lblKg.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        panelSignos.add(txtPesoKg);
        panelSignos.add(lblKg);
        panelSignos.add(Box.createHorizontalStrut(14));
        JLabel lblTemp = new JLabel("Temp. corporal (°C):");
        lblTemp.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblTemp.setForeground(Ui.TEXTO_TITULO);
        panelSignos.add(lblTemp);
        panelSignos.add(txtTemperaturaC);

        // Fila 4: Síntomas reportados
        txtSintomas = crearAreaTexto(3);
        JScrollPane spSintomas = new JScrollPane(txtSintomas);
        spSintomas.setPreferredSize(new Dimension(380, 55));

        // Fila 5: Diagnóstico
        txtDiagnostico = crearAreaTexto(3);
        JScrollPane spDiag = new JScrollPane(txtDiagnostico);
        spDiag.setPreferredSize(new Dimension(380, 55));

        // Fila 6: Tratamiento
        txtTratamiento = crearAreaTexto(3);
        JScrollPane spTrat = new JScrollPane(txtTratamiento);
        spTrat.setPreferredSize(new Dimension(380, 55));

        // Fila 7: Observaciones
        txtObservaciones = crearAreaTexto(2);
        JScrollPane spObs = new JScrollPane(txtObservaciones);
        spObs.setPreferredSize(new Dimension(380, 45));

        // Rellenar campos si editamos
        if (consultaExistente != null) {
            cbVeterinario.setSelectedItem(consultaExistente.getVeterinario());
            txtFecha.setText(consultaExistente.getFechaFormateada());
            cbEstado.setSelectedItem(consultaExistente.getEstado());
            txtMotivo.setText(consultaExistente.getMotivo());
            txtPesoKg.setText(String.format(java.util.Locale.US, "%.1f", consultaExistente.getPesoKg()));
            txtTemperaturaC.setText(String.format(java.util.Locale.US, "%.1f", consultaExistente.getTemperaturaC()));
            txtSintomas.setText(consultaExistente.getSintomas());
            txtDiagnostico.setText(consultaExistente.getDiagnostico());
            txtTratamiento.setText(consultaExistente.getTratamiento());
            txtObservaciones.setText(consultaExistente.getObservaciones());
        }

        // Construir layout
        int fila = 0;
        agregarCampo(form, gbc, fila++, "Veterinario Tratante *", cbVeterinario);
        agregarCampo(form, gbc, fila++, "Fecha y Estado *", panelFechaEstado);
        agregarCampo(form, gbc, fila++, "Motivo de Consulta *", txtMotivo);
        agregarCampo(form, gbc, fila++, "Signos Vitales (Peso/Temp) *", panelSignos);
        agregarCampo(form, gbc, fila++, "Síntomas Reportados", spSintomas);
        agregarCampo(form, gbc, fila++, "Diagnóstico Clínico *", spDiag);
        agregarCampo(form, gbc, fila++, "Tratamiento / Receta *", spTrat);
        agregarCampo(form, gbc, fila++, "Observaciones y Evolución", spObs);

        JScrollPane scrollForm = new JScrollPane(form);
        scrollForm.setBorder(null);
        scrollForm.getVerticalScrollBar().setUnitIncrement(12);
        add(scrollForm, BorderLayout.CENTER);

        // 3. Pie con botones de acción
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        footer.setBackground(Ui.FONDO);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Ui.BORDE_SUAVE));

        JButton btnCancelar = crearBotonModal("Cancelar", false);
        btnCancelar.addActionListener(e -> dispose());

        JButton btnGuardar = crearBotonModal("Guardar Consulta", true);
        btnGuardar.addActionListener(e -> {
            String motivo = txtMotivo.getText().trim();
            String diag = txtDiagnostico.getText().trim();
            String trat = txtTratamiento.getText().trim();
            String vet = (String) cbVeterinario.getSelectedItem();
            if (vet == null || vet.trim().isEmpty()) vet = "Dr. Roberto Mendoza";

            if (motivo.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Debe ingresar el motivo de la consulta.", "Validación", JOptionPane.WARNING_MESSAGE);
                txtMotivo.requestFocus();
                return;
            }
            if (diag.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Debe ingresar el diagnóstico clínico preliminar o definitivo.", "Validación", JOptionPane.WARNING_MESSAGE);
                txtDiagnostico.requestFocus();
                return;
            }
            if (trat.isEmpty()) {
                trat = "Sin tratamiento farmacológico prescrito";
            }

            double peso = 0.0;
            try {
                peso = Double.parseDouble(txtPesoKg.getText().trim().replace(',', '.'));
                if (peso <= 0) throw new NumberFormatException();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Ingrese un peso válido en kilogramos (número mayor a cero).", "Validación", JOptionPane.WARNING_MESSAGE);
                txtPesoKg.requestFocus();
                return;
            }

            double temp = 38.5;
            try {
                temp = Double.parseDouble(txtTemperaturaC.getText().trim().replace(',', '.'));
                if (temp < 30 || temp > 45) throw new NumberFormatException();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Ingrese una temperatura corporal válida (ejemplo: 38.5).", "Validación", JOptionPane.WARNING_MESSAGE);
                txtTemperaturaC.requestFocus();
                return;
            }

            LocalDate fecha;
            try {
                fecha = LocalDate.parse(txtFecha.getText().trim(), FORMATO_FECHA);
            } catch (Exception ex) {
                fecha = LocalDate.now();
            }

            RepositorioVeterinaria repo = RepositorioVeterinaria.getInstancia();
            ConsultaClinica c = consultaExistente;
            if (c == null) {
                int totalConsultas = 0;
                if (mascota != null) {
                    totalConsultas = repo.getConsultasPorMascota(mascota.getCodigo()).size();
                }
                String nuevoCod = "HC-" + String.format("%03d", totalConsultas + 1);
                c = new ConsultaClinica();
                c.setCodigo(nuevoCod);
                c.setCodigoMascota(mascota != null ? mascota.getCodigo() : "VET-0000");
            }

            c.setFecha(fecha);
            c.setVeterinario(vet.trim());
            c.setMotivo(motivo);
            c.setPesoKg(peso);
            c.setTemperaturaC(temp);
            c.setSintomas(txtSintomas.getText().trim().isEmpty() ? "Sin síntomas adicionales referidos" : txtSintomas.getText().trim());
            c.setDiagnostico(diag);
            c.setTratamiento(trat);
            c.setObservaciones(txtObservaciones.getText().trim().isEmpty() ? "Paciente clínicamente estable" : txtObservaciones.getText().trim());
            c.setEstado((String) cbEstado.getSelectedItem());

            // Actualizar peso de la mascota si es válido
            if (mascota != null && peso > 0) {
                mascota.setPesoActualKg(peso);
            }

            if (consultaExistente == null) {
                repo.agregarConsulta(c);
            }

            this.consultaResultado = c;
            this.guardado = true;
            if (onGuardar != null) {
                onGuardar.accept(c);
            }
            dispose();
        });

        footer.add(btnCancelar);
        footer.add(btnGuardar);
        add(footer, BorderLayout.SOUTH);
    }

    private JTextArea crearAreaTexto(int filas) {
        JTextArea area = new JTextArea(filas, 20);
        area.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Ui.BORDE_SUAVE, 1),
                new EmptyBorder(6, 8, 6, 8)
        ));
        return area;
    }

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
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
            /**
             * Renderizado gráfico personalizado en Java2D.
             * Activa Antialiasing para bordes suaves y realiza el cálculo geométrico preciso.
             */
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

    public boolean isGuardado() {
        return guardado;
    }

    public ConsultaClinica getConsultaResultado() {
        return consultaResultado;
    }
}
