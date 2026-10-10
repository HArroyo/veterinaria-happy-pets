package happypets.modulos.modulo1;

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
import java.util.function.Consumer;

import javax.swing.BorderFactory;
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
import happypets.model.Cliente;
import happypets.ui.Iconos;
import happypets.ui.Ui;

/**
 * Dialogo modal profesional para registrar o editar datos de un cliente / propietario.
 */
public class FormularioClienteDialog extends JDialog {
    private static final long serialVersionUID = 1L;

    private final JTextField txtNombres;
    private final JTextField txtApellidos;
    private final JComboBox<String> cbTipoDoc;
    private final JTextField txtDocumento;
    private final JTextField txtTelefonoPrincipal;
    private final JTextField txtTelefonoSecundario;
    private final JTextField txtCorreo;
    private final JTextField txtDireccion;
    private final JTextField txtDistritoCiudad;
    private final JTextArea txtNotas;

    private boolean guardado = false;
    private Cliente clienteResultado;

    public FormularioClienteDialog(Window parent, Cliente clienteExistente, Consumer<Cliente> onGuardar) {
        super(parent, clienteExistente == null ? "Nuevo Propietario" : "Modificar Propietario", ModalityType.APPLICATION_MODAL);
        setSize(560, 640);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        // Cabecera estilizada
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Ui.TURQUESA);
        header.setBorder(new EmptyBorder(14, 20, 14, 20));

        JPanel headerTxt = new JPanel();
        headerTxt.setOpaque(false);
        headerTxt.setLayout(new javax.swing.BoxLayout(headerTxt, javax.swing.BoxLayout.Y_AXIS));

        JLabel title = new JLabel(clienteExistente == null ? "Registro de Nuevo Propietario" : "Modificar Ficha del Propietario");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(Color.WHITE);
        title.setIcon(Iconos.crearIconoUsuario(20, Color.WHITE));
        title.setIconTextGap(8);

        JLabel sub = new JLabel("Complete la información de contacto y localización del titular");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        sub.setForeground(new Color(224, 242, 254));

        headerTxt.add(title);
        headerTxt.add(javax.swing.Box.createVerticalStrut(2));
        headerTxt.add(sub);
        header.add(headerTxt, BorderLayout.CENTER);
        add(header, BorderLayout.NORTH);

        // Formulario central
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        form.setBorder(new EmptyBorder(16, 22, 16, 22));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtNombres = Ui.campoTexto(14);
        txtApellidos = Ui.campoTexto(14);
        cbTipoDoc = new JComboBox<>(new String[]{"DNI", "Carne Extranjeria", "Pasaporte", "RUC"});
        cbTipoDoc.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtDocumento = Ui.campoTexto(14);
        txtTelefonoPrincipal = Ui.campoTexto(14);
        txtTelefonoSecundario = Ui.campoTexto(14);
        txtCorreo = Ui.campoTexto(14);
        txtDireccion = Ui.campoTexto(14);
        txtDistritoCiudad = Ui.campoTexto(14);

        txtNotas = new JTextArea(3, 20);
        txtNotas.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtNotas.setLineWrap(true);
        txtNotas.setWrapStyleWord(true);
        txtNotas.setBorder(new EmptyBorder(4, 6, 4, 6));

        JScrollPane spNotas = new JScrollPane(txtNotas);
        spNotas.setPreferredSize(new Dimension(0, 60));
        spNotas.setBorder(BorderFactory.createLineBorder(Ui.BORDE_SUAVE, 1));

        int r = 0;
        agregarCampo(form, gbc, r++, "Nombres *:", txtNombres);
        agregarCampo(form, gbc, r++, "Apellidos *:", txtApellidos);
        agregarCampo(form, gbc, r++, "Tipo de Documento:", cbTipoDoc);
        agregarCampo(form, gbc, r++, "Nro. Documento *:", txtDocumento);
        agregarCampo(form, gbc, r++, "Telefono Principal *:", txtTelefonoPrincipal);
        agregarCampo(form, gbc, r++, "Telefono Secundario:", txtTelefonoSecundario);
        agregarCampo(form, gbc, r++, "Correo Electronico:", txtCorreo);
        agregarCampo(form, gbc, r++, "Direccion Domiciliaria:", txtDireccion);
        agregarCampo(form, gbc, r++, "Distrito / Ciudad:", txtDistritoCiudad);
        agregarCampo(form, gbc, r++, "Notas / Observaciones:", spNotas);

        // Prellenar si edita
        if (clienteExistente != null) {
            txtNombres.setText(clienteExistente.getNombres());
            txtApellidos.setText(clienteExistente.getApellidos());
            if (clienteExistente.getTipoDocumento() != null) {
                cbTipoDoc.setSelectedItem(clienteExistente.getTipoDocumento());
            }
            txtDocumento.setText(clienteExistente.getNumeroDocumento());
            txtTelefonoPrincipal.setText(clienteExistente.getTelefonoPrincipal());
            txtTelefonoSecundario.setText(clienteExistente.getTelefonoSecundario());
            txtCorreo.setText(clienteExistente.getCorreo());
            txtDireccion.setText(clienteExistente.getDireccion());
            txtDistritoCiudad.setText(clienteExistente.getDistritoCiudad());
            txtNotas.setText(clienteExistente.getNotasContacto());
        } else {
            cbTipoDoc.setSelectedItem("DNI");
            txtDistritoCiudad.setText("Lima");
        }

        JScrollPane scrollForm = new JScrollPane(form);
        scrollForm.setBorder(null);
        scrollForm.getViewport().setOpaque(false);
        scrollForm.setOpaque(false);
        add(scrollForm, BorderLayout.CENTER);

        // Pie de diálogo
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        footer.setBackground(Ui.FONDO);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Ui.BORDE_SUAVE));

        JButton btnCancelar = crearBotonModal("Cancelar", false);
        JButton btnGuardar = crearBotonModal(clienteExistente == null ? "Registrar Cliente" : "Guardar Cambios", true);

        btnCancelar.addActionListener(e -> dispose());
        btnGuardar.addActionListener(e -> {
            String nom = txtNombres.getText().trim();
            String ape = txtApellidos.getText().trim();
            String doc = txtDocumento.getText().trim();
            String tel = txtTelefonoPrincipal.getText().trim();

            if (nom.isEmpty() || ape.isEmpty() || doc.isEmpty() || tel.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Por favor complete los campos obligatorios (*): Nombres, Apellidos, Nro. Documento y Teléfono Principal.",
                        "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Cliente c = (clienteExistente != null) ? clienteExistente : new Cliente();
            if (c.getCodigo() == null || c.getCodigo().isEmpty()) {
                c.setCodigo("CLI-" + String.format("%03d", RepositorioVeterinaria.getInstancia().getClientes().size() + 1));
            }
            c.setNombres(nom);
            c.setApellidos(ape);
            c.setTipoDocumento((String) cbTipoDoc.getSelectedItem());
            c.setNumeroDocumento(doc);
            c.setTelefonoPrincipal(tel);
            c.setTelefonoSecundario(txtTelefonoSecundario.getText().trim());
            c.setCorreo(txtCorreo.getText().trim());
            c.setDireccion(txtDireccion.getText().trim());
            c.setDistritoCiudad(txtDistritoCiudad.getText().trim());
            c.setNotasContacto(txtNotas.getText().trim());

            this.clienteResultado = c;
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

    private void agregarCampo(JPanel panel, GridBagConstraints gbc, int row, String etiqueta, JComponent comp) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.32;
        gbc.anchor = GridBagConstraints.WEST;
        JLabel lbl = new JLabel(etiqueta, SwingConstants.LEFT);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lbl.setForeground(Ui.TEXTO_TITULO);
        panel.add(lbl, gbc);

        gbc.gridx = 1;
        gbc.gridy = row;
        gbc.weightx = 0.68;
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

    public Cliente getClienteResultado() {
        return clienteResultado;
    }
}
