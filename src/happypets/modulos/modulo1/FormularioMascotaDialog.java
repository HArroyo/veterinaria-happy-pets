package happypets.modulos.modulo1;

import happypets.ui.Ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import happypets.model.Mascota;

/**
 * Diálogo modal para registrar o modificar datos de una mascota vinculada al cliente.
 */
public class FormularioMascotaDialog extends JDialog {
    private static final long serialVersionUID = 1L;

    private final JTextField txtCodigo;
    private final JTextField txtNombre;
    private final JComboBox<String> cbEspecie;
    private final JTextField txtRaza;
    private final JTextField txtEdad;
    private final JComboBox<String> cbSexo;
    private final JTextField txtPeso;
    private final JTextField txtPlanVacunal;
    private final JTextField txtAlergias;
    private final JComboBox<String> cbEstado;

    private boolean guardado = false;
    private Mascota mascotaResultado;

    public FormularioMascotaDialog(JFrame parent, Mascota mascotaExistente, Consumer<Mascota> onGuardar) {
        super(parent, mascotaExistente == null ? "Nueva Mascota" : "Modificar Mascota", true);
        setSize(520, 560);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Ui.TURQUESA);
        header.setBorder(new EmptyBorder(14, 20, 14, 20));

        JLabel title = new JLabel(mascotaExistente == null ? "Registro de Nueva Mascota" : "Modificación de Mascota");
        title.setFont(new Font("Segoe UI", Font.BOLD, 17));
        title.setForeground(Color.WHITE);
        header.add(title, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(new EmptyBorder(16, 20, 16, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtCodigo = Ui.campoTexto(12);
        txtNombre = Ui.campoTexto(12);
        cbEspecie = new JComboBox<>(new String[] {"Canino", "Felino", "Ave", "Roedor", "Otro"});
        txtRaza = Ui.campoTexto(12);
        txtEdad = Ui.campoTexto(12);
        cbSexo = new JComboBox<>(new String[] {"Macho", "Hembra"});
        txtPeso = Ui.campoTexto(12);
        txtPlanVacunal = Ui.campoTexto(12);
        txtAlergias = Ui.campoTexto(12);
        cbEstado = new JComboBox<>(new String[] {"Activo", "Inactivo"});

        int r = 0;
        agregarCampo(form, gbc, r++, "Código Mascota*:", txtCodigo);
        agregarCampo(form, gbc, r++, "Nombre Mascota*:", txtNombre);
        agregarCampo(form, gbc, r++, "Especie*:", cbEspecie);
        agregarCampo(form, gbc, r++, "Raza*:", txtRaza);
        agregarCampo(form, gbc, r++, "Edad (ej. 3 años 2 m.)*:", txtEdad);
        agregarCampo(form, gbc, r++, "Sexo*:", cbSexo);
        agregarCampo(form, gbc, r++, "Peso actual (kg):", txtPeso);
        agregarCampo(form, gbc, r++, "Plan vacunal:", txtPlanVacunal);
        agregarCampo(form, gbc, r++, "Alergias:", txtAlergias);
        agregarCampo(form, gbc, r++, "Estado:", cbEstado);

        if (mascotaExistente != null) {
            txtCodigo.setText(mascotaExistente.getCodigo());
            txtCodigo.setEditable(false);
            txtNombre.setText(mascotaExistente.getNombre());
            cbEspecie.setSelectedItem(mascotaExistente.getEspecie());
            txtRaza.setText(mascotaExistente.getRaza());
            txtEdad.setText(mascotaExistente.getEdadTexto());
            cbSexo.setSelectedItem(mascotaExistente.getSexo());
            txtPeso.setText(String.valueOf(mascotaExistente.getPesoActualKg()));
            txtPlanVacunal.setText(mascotaExistente.getPlanVacunal());
            txtAlergias.setText(mascotaExistente.getAlergias());
            cbEstado.setSelectedItem(mascotaExistente.isActivo() ? "Activo" : "Inactivo");
        } else {
            txtCodigo.setText("VET-" + String.format("%04d", (int)(Math.random() * 9000 + 1000)));
            txtPlanVacunal.setText("Al día");
            txtAlergias.setText("Ninguna conocida");
        }

        add(form, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        footer.setBackground(Ui.FONDO);

        JButton btnGuardar = Ui.boton("Guardar", true);
        JButton btnCancelar = Ui.boton("Cancelar", false);

        btnGuardar.addActionListener(e -> {
            if (txtNombre.getText().trim().isEmpty() || txtRaza.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor complete los campos obligatorios.", "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }
            double peso = 0.0;
            try {
                if (!txtPeso.getText().trim().isEmpty()) {
                    peso = Double.parseDouble(txtPeso.getText().trim());
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "El peso debe ser un número válido.", "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Mascota m = mascotaExistente != null ? mascotaExistente : new Mascota();
            m.setCodigo(txtCodigo.getText().trim());
            m.setNombre(txtNombre.getText().trim());
            m.setEspecie((String) cbEspecie.getSelectedItem());
            m.setRaza(txtRaza.getText().trim());
            m.setEdadTexto(txtEdad.getText().trim());
            m.setSexo((String) cbSexo.getSelectedItem());
            m.setPesoActualKg(peso);
            m.setPlanVacunal(txtPlanVacunal.getText().trim());
            m.setAlergias(txtAlergias.getText().trim());
            m.setActivo("Activo".equalsIgnoreCase((String) cbEstado.getSelectedItem()));

            this.mascotaResultado = m;
            this.guardado = true;
            if (onGuardar != null) {
                onGuardar.accept(m);
            }
            dispose();
        });

        btnCancelar.addActionListener(e -> dispose());

        footer.add(btnGuardar);
        footer.add(btnCancelar);
        add(footer, BorderLayout.SOUTH);
    }

    private void agregarCampo(JPanel panel, GridBagConstraints gbc, int row, String etiqueta, javax.swing.JComponent comp) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.35;
        JLabel lbl = new JLabel(etiqueta);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        panel.add(lbl, gbc);

        gbc.gridx = 1;
        gbc.gridy = row;
        gbc.weightx = 0.65;
        panel.add(comp, gbc);
    }

    public boolean isGuardado() { return guardado; }
    public Mascota getMascotaResultado() { return mascotaResultado; }
}
