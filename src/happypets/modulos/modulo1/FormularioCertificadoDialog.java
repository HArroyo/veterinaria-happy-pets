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
import happypets.model.Certificado;
import happypets.model.DocumentoMascota;
import happypets.model.Mascota;
import happypets.ui.Iconos;
import happypets.ui.Ui;

/**
 * Diálogo modal profesional para la emisión de constancias y certificados médicos veterinarios oficiales.
 */
public class FormularioCertificadoDialog extends JDialog {
    private static final long serialVersionUID = 1L;

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final Mascota mascota;
    private final DocumentoMascota documentoExistente;

    private final JComboBox<String> cbTipoDocumento;
    private final JTextField txtNumeroCertificado;
    private final JComboBox<String> cbVeterinario;
    private final JTextField txtMotivoDestino;
    private final JTextField txtFechaEmision;
    private final JComboBox<String> cbVigencia;
    private final JTextArea txtDictamen;
    private final JTextArea txtObservaciones;

    private boolean guardado = false;
    private DocumentoMascota documentoResultado;

    public FormularioCertificadoDialog(Window parent, Mascota mascota, DocumentoMascota documentoExistente, Consumer<DocumentoMascota> onGuardar) {
        super(parent, documentoExistente == null ? "Emitir Certificado Médico Veterinario" : "Modificar Documento Oficial", ModalityType.APPLICATION_MODAL);
        this.mascota = mascota;
        this.documentoExistente = documentoExistente;

        setSize(650, 700);
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

        JLabel title = new JLabel(documentoExistente == null ? "Emisión de Certificado Médico Oficial" : "Modificar Documento " + documentoExistente.getCodigo());
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(Color.WHITE);
        title.setIcon(Iconos.crearIconoCertificado(20, Color.WHITE));
        title.setIconTextGap(8);

        String petInfo = mascota != null ? "Paciente: " + mascota.getNombre() + " (" + mascota.getEspecie() + " · " + mascota.getRaza() + " · " + mascota.getCodigo() + ")" : "Registro de certificación veterinaria";
        JLabel sub = new JLabel(petInfo);
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

        // Fila 0: Tipo de documento
        cbTipoDocumento = new JComboBox<>(new String[]{
                "Certificado de Salud General",
                "Certificado de Vacunación",
                "Certificado de Salud para Viajes",
                "Constancia de Desparasitación",
                "Autorización Quirúrgica",
                "Certificado de Microchip"
        });
        cbTipoDocumento.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cbTipoDocumento.setPreferredSize(new Dimension(280, 30));
        cbTipoDocumento.setBackground(Color.WHITE);

        // Fila 1: N° Certificado oficial
        txtNumeroCertificado = Ui.campoTexto(16);
        int totalDocs = mascota != null ? RepositorioVeterinaria.getInstancia().getDocumentosPorMascota(mascota.getCodigo()).size() : 0;
        txtNumeroCertificado.setText("CERT-2024-" + String.format("%04d", (totalDocs + 1) * 10 + 1));

        // Fila 2: Veterinario colegiado
        cbVeterinario = new JComboBox<>(new String[]{
                "Dr. Roberto Mendoza Chávez (CMVP 8492)",
                "Dra. Laura Morales Ruiz (CMVP 4512)",
                "Dr. Mario Silva Paredes (CMVP 8910)",
                "Dra. Carmen Vega Hurtado (CMVP 1245)",
                "Dra. Patricia Flores (CMVP 9134)"
        });
        cbVeterinario.setEditable(true);
        cbVeterinario.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cbVeterinario.setPreferredSize(new Dimension(280, 30));
        cbVeterinario.setBackground(Color.WHITE);

        // Fila 3: Motivo / Destino
        txtMotivoDestino = Ui.campoTexto(20);
        txtMotivoDestino.setText("Control Sanitario y Aptitud General");

        // Fila 4: Fecha de emisión y vigencia
        txtFechaEmision = Ui.campoTexto(10);
        txtFechaEmision.setText(LocalDate.now().format(FORMATO_FECHA));
        txtFechaEmision.setToolTipText("dd/MM/yyyy");

        cbVigencia = new JComboBox<>(new String[]{
                "30 días calendario",
                "60 días calendario",
                "90 días calendario",
                "1 año calendario"
        });
        cbVigencia.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cbVigencia.setPreferredSize(new Dimension(140, 30));
        cbVigencia.setBackground(Color.WHITE);

        JPanel panelFechaVig = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        panelFechaVig.setOpaque(false);
        panelFechaVig.add(txtFechaEmision);
        JLabel lblVig = new JLabel("Vigencia:");
        lblVig.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblVig.setForeground(Ui.TEXTO_TITULO);
        panelFechaVig.add(lblVig);
        panelFechaVig.add(cbVigencia);

        // Fila 5: Dictamen Clínico Sanitario
        txtDictamen = crearAreaTexto(4);
        actualizarDictamenPorDefecto((String) cbTipoDocumento.getSelectedItem());
        cbTipoDocumento.addActionListener(e -> actualizarDictamenPorDefecto((String) cbTipoDocumento.getSelectedItem()));
        JScrollPane spDictamen = new JScrollPane(txtDictamen);
        spDictamen.setPreferredSize(new Dimension(380, 75));

        // Fila 6: Observaciones complementarias
        txtObservaciones = crearAreaTexto(3);
        txtObservaciones.setText("Paciente en condiciones óptimas de nutrición e hidratación. Sin signos clínicos de patologías transmisibles.");
        JScrollPane spObs = new JScrollPane(txtObservaciones);
        spObs.setPreferredSize(new Dimension(380, 60));

        // Cargar valores existentes si estamos editando
        if (documentoExistente != null) {
            cbTipoDocumento.setSelectedItem(documentoExistente.getTipo());
            txtFechaEmision.setText(documentoExistente.getFechaActualizacionFormateada());
            txtDictamen.setText(documentoExistente.getDescripcion());
            if (documentoExistente instanceof Certificado cert) {
                if (cert.getNumeroCertificado() != null) txtNumeroCertificado.setText(cert.getNumeroCertificado());
                if (cert.getMotivoDestino() != null) txtMotivoDestino.setText(cert.getMotivoDestino());
            }
            if (documentoExistente.getVeterinarioEmisor() != null) {
                cbVeterinario.setSelectedItem(documentoExistente.getVeterinarioEmisor());
            }
        }

        // Armar el layout
        int fila = 0;
        agregarCampo(form, gbc, fila++, "Tipo de Certificado *", cbTipoDocumento);
        agregarCampo(form, gbc, fila++, "N.º Certificado Oficial *", txtNumeroCertificado);
        agregarCampo(form, gbc, fila++, "Médico Colegiado (CMVP) *", cbVeterinario);
        agregarCampo(form, gbc, fila++, "Motivo / Destino *", txtMotivoDestino);
        agregarCampo(form, gbc, fila++, "Fecha y Vigencia *", panelFechaVig);
        agregarCampo(form, gbc, fila++, "Dictamen Sanitario Oficial *", spDictamen);
        agregarCampo(form, gbc, fila++, "Observaciones y Notas", spObs);

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

        JButton btnEmitir = crearBotonModal("Emitir Certificado", true);
        btnEmitir.addActionListener(e -> {
            String tipo = (String) cbTipoDocumento.getSelectedItem();
            String num = txtNumeroCertificado.getText().trim();
            String vet = (String) cbVeterinario.getSelectedItem();
            String motivo = txtMotivoDestino.getText().trim();
            String dictamen = txtDictamen.getText().trim();

            if (num.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Debe ingresar el número oficial de certificado.", "Validación", JOptionPane.WARNING_MESSAGE);
                txtNumeroCertificado.requestFocus();
                return;
            }
            if (motivo.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Debe especificar el motivo o destino del documento.", "Validación", JOptionPane.WARNING_MESSAGE);
                txtMotivoDestino.requestFocus();
                return;
            }
            if (dictamen.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Debe ingresar el dictamen o certificación médica.", "Validación", JOptionPane.WARNING_MESSAGE);
                txtDictamen.requestFocus();
                return;
            }

            LocalDate fecha;
            try {
                fecha = LocalDate.parse(txtFechaEmision.getText().trim(), FORMATO_FECHA);
            } catch (Exception ex) {
                fecha = LocalDate.now();
            }

            RepositorioVeterinaria repo = RepositorioVeterinaria.getInstancia();
            DocumentoMascota doc = documentoExistente;
            if (doc == null) {
                int total = mascota != null ? repo.getDocumentosPorMascota(mascota.getCodigo()).size() : 0;
                String nuevoCod = "DOC-" + String.format("%03d", total + 1);
                Certificado cert = new Certificado();
                cert.setCodigo(nuevoCod);
                cert.setCodigoMascota(mascota != null ? mascota.getCodigo() : "VET-0000");
                cert.setNumeroCertificado(num);
                cert.setFechaEmision(fecha);
                cert.setMotivoDestino(motivo);
                cert.setEstado("Vigente");
                doc = cert;
            } else if (doc instanceof Certificado cert) {
                cert.setNumeroCertificado(num);
                cert.setFechaEmision(fecha);
                cert.setMotivoDestino(motivo);
            }

            doc.setTipo(tipo);
            doc.setDescripcion(dictamen);
            doc.setFechaActualizacion(fecha);
            doc.setVeterinarioEmisor(vet);
            doc.setDisponible(true);

            // Calcular vigencia
            String vigTexto = (String) cbVigencia.getSelectedItem();
            if (vigTexto != null && vigTexto.contains("1 año")) {
                doc.setFechaVencimiento(fecha.plusYears(1));
            } else if (vigTexto != null && vigTexto.contains("90")) {
                doc.setFechaVencimiento(fecha.plusDays(90));
            } else if (vigTexto != null && vigTexto.contains("60")) {
                doc.setFechaVencimiento(fecha.plusDays(60));
            } else {
                doc.setFechaVencimiento(fecha.plusDays(30));
            }

            if (documentoExistente == null) {
                repo.agregarDocumento(doc);
            }

            this.documentoResultado = doc;
            this.guardado = true;
            if (onGuardar != null) {
                onGuardar.accept(doc);
            }
            dispose();
        });

        footer.add(btnCancelar);
        footer.add(btnEmitir);
        add(footer, BorderLayout.SOUTH);
    }

    private void actualizarDictamenPorDefecto(String tipo) {
        if (tipo == null) return;
        String petName = mascota != null ? mascota.getNombre() : "el paciente";
        if (tipo.contains("Salud para Viajes")) {
            txtDictamen.setText("El médico veterinario colegiado certifica que " + petName + " se encuentra clínicamente sano y libre de patologías infectocontagiosas o parasitarias, considerándose APTO para viajar por vía aérea o terrestre dentro del territorio nacional o internacional.");
            txtMotivoDestino.setText("Trámite de Viaje Aéreo / Aerolínea");
        } else if (tipo.contains("Vacunación")) {
            txtDictamen.setText("Se certifica que el ejemplar " + petName + " cuenta con el esquema oficial de inmunizaciones al día (Vacuna Séxtuple canina, Antirrábica y KC), sin registrarse contraindicaciones biológicas.");
            txtMotivoDestino.setText("Control Sanitario e Inmunizaciones");
        } else if (tipo.contains("Desparasitación")) {
            txtDictamen.setText("Se certifica la administración de antiparasitarios internos de amplio espectro y tratamiento ectoparasiticida vigente, encontrándose el paciente libre de parásitos.");
            txtMotivoDestino.setText("Control Sanitario Trimestral");
        } else if (tipo.contains("Quirúrgica")) {
            txtDictamen.setText("Se aprueba la aptitud prequirúrgica del paciente tras evaluación física general y triaje clínico, con autorización del propietario bajo consentimiento informado.");
            txtMotivoDestino.setText("Procedimiento Quirúrgico Electivo");
        } else if (tipo.contains("Microchip")) {
            txtDictamen.setText("Se certifica la implantación y verificación de funcionamiento del dispositivo microchip identificatorio estándar internacional ISO 11784/11785 en " + petName + ".");
            txtMotivoDestino.setText("Registro e Identificación Animal");
        } else {
            txtDictamen.setText("Habiendo realizado el examen físico y evaluación clínica de " + petName + ", se certifica que se encuentra en ÓPTIMO ESTADO DE SALUD, buen estado nutricional y libre de signos de enfermedades zoonóticas.");
            txtMotivoDestino.setText("Constancia de Salud General");
        }
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

    public boolean isGuardado() {
        return guardado;
    }

    public DocumentoMascota getDocumentoResultado() {
        return documentoResultado;
    }
}
