package happypets.modulos.modulo5;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;

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
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import happypets.data.RepositorioVeterinaria;
import happypets.model.ProveedorFarmacia;
import happypets.ui.Iconos;
import happypets.ui.Ui;

/**
 * Modal para homologación y registro de laboratorios y proveedores farmacéuticos.
 */
public class FormularioProveedorDialog extends JDialog {
    private static final long serialVersionUID = 1L;

    private final RepositorioVeterinaria repo = RepositorioVeterinaria.getInstancia();
    private final ProveedorFarmacia proveedorExistente;
    private boolean guardadoExitoso = false;

    private JTextField txtRuc;
    private JTextField txtRazonSocial;
    private JTextField txtNombreComercial;
    private JTextField txtTelefono;
    private JTextField txtCorreo;
    private JTextField txtDireccion;
    private JTextField txtContactoAsesor;
    private JComboBox<String> cbCondicionPago;
    private JComboBox<String> cbEstado;

    /**
     * Constructor de FormularioProveedorDialog.
     * Inicializa los componentes gráficos, configura layouts, asigna estilos
     * institucionales y carga los datos desde el repositorio central.
     */
    public FormularioProveedorDialog(Frame parent) {
        this(parent, null);
    }

    /**
     * Constructor de FormularioProveedorDialog.
     * Inicializa los componentes gráficos, configura layouts, asigna estilos
     * institucionales y carga los datos desde el repositorio central.
     */
    public FormularioProveedorDialog(Frame parent, ProveedorFarmacia existente) {
        super(parent, existente == null ? "Nuevo Laboratorio / Proveedor Farmacéutico" : "Editar Proveedor - " + existente.getRuc(), true);
        this.proveedorExistente = existente;

        setSize(580, 520);
        setLocationRelativeTo(parent);
        setResizable(false);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Ui.FONDO);

        add(crearCabecera(), BorderLayout.NORTH);
        add(crearCuerpoFormulario(), BorderLayout.CENTER);
        add(crearBotoneraInferior(), BorderLayout.SOUTH);

        if (proveedorExistente != null) {
            cargarDatosExistentes();
        } else {
            inicializarValoresPorDefecto();
        }
    }

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private JPanel crearCabecera() {
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel cab = new JPanel(new BorderLayout());
        cab.setBackground(Color.WHITE);
        cab.setBorder(new EmptyBorder(14, 20, 14, 20));

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel(proveedorExistente == null ? "Homologación de Proveedor Farmacéutico" : "Edición de Proveedor Homologado");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titulo.setForeground(Ui.TEXTO_TITULO);

        JLabel subtitulo = new JLabel("Directorio de laboratorios veterinarios, distribuidoras y canales de abastecimiento");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitulo.setForeground(Ui.TEXTO_MUTED);

        textos.add(titulo);
        textos.add(Box.createVerticalStrut(2));
        textos.add(subtitulo);
        cab.add(textos, BorderLayout.CENTER);

        JLabel lblIcono = new JLabel(Iconos.crearIconoCamionProveedor(28, Ui.TURQUESA_PROFUNDO));
        cab.add(lblIcono, BorderLayout.EAST);

        return cab;
    }

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private JPanel crearCuerpoFormulario() {
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(14, 18, 14, 18));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 6, 5, 6);

        // Fila 0: RUC y Teléfono
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.5;
        p.add(crearEtiquetaCampo("RUC (11 dígitos) *"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.5;
        p.add(crearEtiquetaCampo("Teléfono Comercial *"), gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        txtRuc = crearTextField();
        p.add(txtRuc, gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        txtTelefono = crearTextField();
        p.add(txtTelefono, gbc);

        // Fila 2: Razón Social
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        p.add(crearEtiquetaCampo("Razón Social Legal *"), gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        txtRazonSocial = crearTextField();
        p.add(txtRazonSocial, gbc);

        // Fila 4: Nombre Comercial y Contacto Asesor
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 4;
        p.add(crearEtiquetaCampo("Nombre Comercial / Marca *"), gbc);
        gbc.gridx = 1; gbc.gridy = 4;
        p.add(crearEtiquetaCampo("Asesor / Ejecutivo de Cuenta *"), gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        txtNombreComercial = crearTextField();
        p.add(txtNombreComercial, gbc);
        gbc.gridx = 1; gbc.gridy = 5;
        txtContactoAsesor = crearTextField();
        p.add(txtContactoAsesor, gbc);

        // Fila 6: Correo y Dirección
        gbc.gridx = 0; gbc.gridy = 6;
        p.add(crearEtiquetaCampo("Correo Electrónico *"), gbc);
        gbc.gridx = 1; gbc.gridy = 6;
        p.add(crearEtiquetaCampo("Dirección Fiscal / Sede *"), gbc);

        gbc.gridx = 0; gbc.gridy = 7;
        txtCorreo = crearTextField();
        p.add(txtCorreo, gbc);
        gbc.gridx = 1; gbc.gridy = 7;
        txtDireccion = crearTextField();
        p.add(txtDireccion, gbc);

        // Fila 8: Condición de Pago y Estado
        gbc.gridx = 0; gbc.gridy = 8;
        p.add(crearEtiquetaCampo("Condición de Pago Comercial *"), gbc);
        gbc.gridx = 1; gbc.gridy = 8;
        p.add(crearEtiquetaCampo("Estado de Homologación *"), gbc);

        gbc.gridx = 0; gbc.gridy = 9;
        cbCondicionPago = new JComboBox<>(new String[]{"Contado Factura", "Crédito 15 días", "Crédito 30 días", "Crédito 45 días"});
        cbCondicionPago.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cbCondicionPago.setBackground(Color.WHITE);
        cbCondicionPago.setPreferredSize(new Dimension(100, 30));
        p.add(cbCondicionPago, gbc);

        gbc.gridx = 1; gbc.gridy = 9;
        cbEstado = new JComboBox<>(new String[]{"Homologado / Activo", "En Evaluación", "Inactivo"});
        cbEstado.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cbEstado.setBackground(Color.WHITE);
        cbEstado.setPreferredSize(new Dimension(100, 30));
        p.add(cbEstado, gbc);

        return p;
    }

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private JPanel crearBotoneraInferior() {
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel barra = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        barra.setBackground(Color.WHITE);
        barra.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Ui.BORDE_SUAVE));

        JButton btnCancelar = crearBotonAccion("Cancelar", false);
        btnCancelar.addActionListener(e -> dispose());

        JButton btnGuardar = crearBotonAccion(proveedorExistente == null ? "Homologar Proveedor" : "Actualizar Proveedor", true);
        btnGuardar.addActionListener(e -> validarYGuardar());

        barra.add(btnCancelar);
        barra.add(btnGuardar);
        return barra;
    }

    private void cargarDatosExistentes() {
        txtRuc.setText(proveedorExistente.getRuc());
        txtRuc.setEditable(false);
        txtRuc.setBackground(new Color(241, 245, 249));

        txtRazonSocial.setText(proveedorExistente.getRazonSocial());
        txtNombreComercial.setText(proveedorExistente.getNombreComercial());
        txtTelefono.setText(proveedorExistente.getTelefono());
        txtCorreo.setText(proveedorExistente.getCorreo());
        txtDireccion.setText(proveedorExistente.getDireccion());
        txtContactoAsesor.setText(proveedorExistente.getContactoAsesor());
        cbCondicionPago.setSelectedItem(proveedorExistente.getCondicionPago());
        cbEstado.setSelectedItem(proveedorExistente.getEstado());
    }

    private void inicializarValoresPorDefecto() {
        txtRuc.setText("20608945123");
        txtRazonSocial.setText("Distribuidora Farmacéutica Veterinaria S.A.C.");
        txtNombreComercial.setText("FarmaVet Perú");
        txtTelefono.setText("+51 987 654 321");
        txtCorreo.setText("ventas@farmavetperu.com");
        txtDireccion.setText("Av. Los Frutales 450, Ate, Lima");
        txtContactoAsesor.setText("Lic. Miguel Ángel Ramos");
    }

    private void validarYGuardar() {
        String ruc = txtRuc.getText().trim();
        String razon = txtRazonSocial.getText().trim();
        String nombreCom = txtNombreComercial.getText().trim();
        String tel = txtTelefono.getText().trim();
        String correo = txtCorreo.getText().trim();
        String dir = txtDireccion.getText().trim();
        String asesor = txtContactoAsesor.getText().trim();

        if (ruc.length() != 11 || !ruc.matches("\\d+")) {
            mostrarError("El RUC debe tener exactamente 11 dígitos numéricos.");
            return;
        }
        if (razon.isEmpty()) {
            mostrarError("La Razón Social es obligatoria.");
            return;
        }
        if (tel.isEmpty()) {
            mostrarError("El teléfono de contacto es obligatorio.");
            return;
        }
        if (correo.isEmpty() || !correo.contains("@")) {
            mostrarError("Debe ingresar un correo electrónico válido.");
            return;
        }

        String cond = (String) cbCondicionPago.getSelectedItem();
        String est = (String) cbEstado.getSelectedItem();

        ProveedorFarmacia p = new ProveedorFarmacia(
                ruc, razon, nombreCom.isEmpty() ? razon : nombreCom, tel, correo, dir, asesor, cond, est
        );

        repo.guardarProveedorFarmacia(p);
        guardadoExitoso = true;

        JOptionPane.showMessageDialog(this,
                "Proveedor homologado exitosamente:\n" + razon + " (RUC: " + ruc + ")",
                "Registro Exitoso", JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }

    public boolean isGuardadoExitoso() {
        return guardadoExitoso;
    }

    private void mostrarError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Datos Incompletos", JOptionPane.WARNING_MESSAGE);
    }

    private JLabel crearEtiquetaCampo(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(new Font("Segoe UI", Font.BOLD, 11));
        l.setForeground(new Color(51, 65, 85));
        return l;
    }

    private JTextField crearTextField() {
        JTextField tf = new JTextField();
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tf.setPreferredSize(new Dimension(100, 30));
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Ui.BORDE_INPUT, 1, true),
                new EmptyBorder(4, 8, 4, 8)
        ));
        return tf;
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
                return new Dimension(d.width + 16, 32);
            }
        };
        btn.putClientProperty("primario", primario);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setForeground(primario ? Color.WHITE : new Color(51, 65, 85));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(6, 12, 6, 12));
        return btn;
    }
}
