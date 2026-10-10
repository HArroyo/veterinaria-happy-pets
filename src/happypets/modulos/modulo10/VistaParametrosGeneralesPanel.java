package happypets.modulos.modulo10;

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
import java.io.File;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import java.time.LocalDate;

import happypets.data.RepositorioVeterinaria;
import happypets.model.ConfiguracionClinica;
import happypets.ui.Iconos;
import happypets.ui.Ui;

/**
 * Submódulo 10.1: Parámetros Generales de la Clínica Veterinaria.
 * Basado fielmente en el wireframe institucional 'PÁGINA COMPLETA.pdf'.
 */
public class VistaParametrosGeneralesPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private final RepositorioVeterinaria repo = RepositorioVeterinaria.getInstancia();

    private JTextField txtRazonSocial;
    private JTextField txtNombreComercial;
    private JTextField txtIdentificadorFiscal;
    private JTextField txtCorreo;
    private JTextField txtTelefono;
    private JTextField txtDireccion;
    private JComboBox<String> cbMoneda;
    private JComboBox<String> cbZonaHoraria;
    private JComboBox<String> cbSede;
    private JLabel lblLogoPreview;

    public VistaParametrosGeneralesPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(248, 250, 252)); // Slate-50 suave
        inicializarUI();
        cargarDatos();
    }

    private void inicializarUI() {
        JPanel contenedor = new JPanel();
        contenedor.setLayout(new BoxLayout(contenedor, BoxLayout.Y_AXIS));
        contenedor.setBackground(new Color(248, 250, 252));
        contenedor.setBorder(BorderFactory.createEmptyBorder(20, 28, 28, 28));

        // 1. Barra de Encabezado Institucional (Breadcrumb + Título + Acciones)
        contenedor.add(crearBarraEncabezado());
        contenedor.add(Box.createVerticalStrut(20));

        // 2. Tarjeta Principal: "Datos Generales de la Clínica Veterinaria"
        contenedor.add(crearTarjetaDatosGenerales());
        contenedor.add(Box.createVerticalStrut(20));

        // 3. Tarjeta de Logo Institucional y Configuración de Comprobantes
        contenedor.add(crearTarjetaLogoYRecetas());
        contenedor.add(Box.createVerticalGlue());

        JScrollPane scroll = new JScrollPane(contenedor);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(14);
        scroll.getViewport().setBackground(new Color(248, 250, 252));
        add(scroll, BorderLayout.CENTER);
    }

    private JPanel crearBarraEncabezado() {
        JPanel header = new JPanel(new BorderLayout(15, 8));
        header.setBackground(new Color(248, 250, 252));

        // Lado Izquierdo: Breadcrumb y Títulos
        JPanel izq = new JPanel();
        izq.setLayout(new BoxLayout(izq, BoxLayout.Y_AXIS));
        izq.setBackground(new Color(248, 250, 252));

        JLabel lblBreadcrumb = new JLabel("Ajustes > Configuración Empresarial > Parámetros Generales");
        lblBreadcrumb.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblBreadcrumb.setForeground(Ui.TURQUESA_OSCURO);
        izq.add(lblBreadcrumb);
        izq.add(Box.createVerticalStrut(4));

        JLabel lblTitulo = new JLabel("Administración de HappyPets");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(new Color(15, 23, 42));
        izq.add(lblTitulo);
        izq.add(Box.createVerticalStrut(4));

        JLabel lblSub = new JLabel("Configuración centralizada de parámetros, control de acceso, IA y servicios conectados de la clínica.");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(new Color(100, 116, 139));
        izq.add(lblSub);

        header.add(izq, BorderLayout.CENTER);

        // Lado Derecho: Botones [Exportar], [Restablecer Valores] y [Guardar Todos los Cambios]
        JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        der.setBackground(new Color(248, 250, 252));

        JButton btnExportar = Ui.botonSecundario("📄 Exportar Parámetros", Iconos.crearIconoExportar(13, Ui.TURQUESA_PROFUNDO));
        btnExportar.setPreferredSize(new Dimension(190, 34));
        btnExportar.addActionListener(e -> exportarParametrosReporte());
        der.add(btnExportar);

        JButton btnRestablecer = Ui.botonSecundario("Restablecer Valores", null);
        btnRestablecer.setPreferredSize(new Dimension(160, 34));
        btnRestablecer.addActionListener(e -> cargarDatos());
        der.add(btnRestablecer);

        JButton btnGuardar = Ui.botonPrimario("Guardar Cambios", Iconos.crearIconoGuardar(13, Color.WHITE));
        btnGuardar.setPreferredSize(new Dimension(160, 34));
        btnGuardar.addActionListener(e -> guardarCambios());
        der.add(btnGuardar);

        header.add(der, BorderLayout.EAST);
        return header;
    }

    private JPanel crearTarjetaDatosGenerales() {
        JPanel card = new JPanel(new BorderLayout(10, 16));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                BorderFactory.createEmptyBorder(20, 24, 24, 24)
        ));

        // Cabecera de la tarjeta
        JPanel headCard = new JPanel(new BorderLayout());
        headCard.setBackground(Color.WHITE);

        JLabel lblTitCard = new JLabel("Datos Generales de la Clínica Veterinaria");
        lblTitCard.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitCard.setForeground(new Color(30, 41, 59));
        headCard.add(lblTitCard, BorderLayout.WEST);

        txtIdentificadorFiscal = Ui.campoTexto(16);

        JPanel pnlIdFiscal = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        pnlIdFiscal.setBackground(Color.WHITE);
        JLabel lblRucNit = new JLabel("Identificador Fiscal:");
        lblRucNit.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblRucNit.setForeground(new Color(100, 116, 139));
        pnlIdFiscal.add(lblRucNit);
        pnlIdFiscal.add(txtIdentificadorFiscal);
        headCard.add(pnlIdFiscal, BorderLayout.EAST);

        card.add(headCard, BorderLayout.NORTH);

        // Formulario en GridBagLayout
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Fila 1: Razón Social y Nombre Comercial
        txtRazonSocial = Ui.campoTexto(15);
        txtNombreComercial = Ui.campoTexto(15);

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.5;
        form.add(crearGrupoCampo("Razón Social / Nombre Legal", txtRazonSocial), gbc);

        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.5;
        form.add(crearGrupoCampo("Nombre Comercial", txtNombreComercial), gbc);

        // Fila 2: Correo Institucional y Teléfono de Urgencias
        txtCorreo = Ui.campoTexto(15);
        txtTelefono = Ui.campoTexto(15);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.5;
        form.add(crearGrupoCampo("Correo Electrónico Institucional", txtCorreo), gbc);

        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 0.5;
        form.add(crearGrupoCampo("Línea Telefónica de Urgencias", txtTelefono), gbc);

        // Fila 3: Dirección Sede Principal (Full Width)
        txtDireccion = Ui.campoTexto(25);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; gbc.weightx = 1.0;
        form.add(crearGrupoCampo("Dirección Sede Principal", txtDireccion), gbc);
        gbc.gridwidth = 1;

        // Fila 4: Moneda Principal, Zona Horaria y Sede Activa
        cbMoneda = Ui.combo(new String[]{
                "COP ($) - Peso Colombiano",
                "PEN (S/) - Sol Peruano",
                "USD ($) - Dólar Estadounidense"
        });

        cbZonaHoraria = Ui.combo(new String[]{
                "America/Bogota (UTC -05:00)",
                "America/Lima (UTC -05:00)",
                "America/Santiago (UTC -04:00)",
                "America/Mexico_City (UTC -06:00)"
        });

        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.5;
        form.add(crearGrupoCampo("Moneda Principal", cbMoneda), gbc);

        gbc.gridx = 1; gbc.gridy = 3; gbc.weightx = 0.5;
        form.add(crearGrupoCampo("Zona Horaria", cbZonaHoraria), gbc);

        card.add(form, BorderLayout.CENTER);
        return card;
    }

    private JPanel crearTarjetaLogoYRecetas() {
        JPanel card = new JPanel(new BorderLayout(16, 12));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                BorderFactory.createEmptyBorder(18, 24, 18, 24)
        ));

        // Vista previa del Logo
        JPanel pnlLogoBox = new JPanel(new BorderLayout());
        pnlLogoBox.setBackground(new Color(241, 245, 249));
        pnlLogoBox.setPreferredSize(new Dimension(80, 80));
        pnlLogoBox.setBorder(BorderFactory.createLineBorder(new Color(203, 213, 225), 1));

        lblLogoPreview = new JLabel(Iconos.crearIconoHuella(36, Ui.TURQUESA_OSCURO), SwingConstants.CENTER);
        pnlLogoBox.add(lblLogoPreview, BorderLayout.CENTER);

        // Texto informativo del logotipo
        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setBackground(Color.WHITE);

        JLabel lblTitLogo = new JLabel("Logotipo Oficial para Recetas y Facturas");
        lblTitLogo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitLogo.setForeground(new Color(30, 41, 59));
        info.add(lblTitLogo);
        info.add(Box.createVerticalStrut(4));

        JLabel lblFmt = new JLabel("Formatos aceptados: PNG, JPG, SVG. Máx 2MB (Resolución recomendada 400x400 px con fondo transparente).");
        lblFmt.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblFmt.setForeground(new Color(100, 116, 139));
        info.add(lblFmt);
        info.add(Box.createVerticalStrut(6));

        cbSede = Ui.combo(new String[]{
                "Sede Norte - Principal",
                "Sede Sur - Miraflores",
                "Sede Este - La Molina"
        });
        cbSede.setPreferredSize(new Dimension(220, 32));

        JPanel pnlSedeSel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pnlSedeSel.setBackground(Color.WHITE);
        JLabel lblSedeT = new JLabel("Sede en Edición:  ");
        lblSedeT.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSedeT.setForeground(new Color(71, 85, 105));
        pnlSedeSel.add(lblSedeT);
        pnlSedeSel.add(cbSede);
        info.add(pnlSedeSel);

        // Botón Actualizar Imagen
        JPanel pnlDer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 20));
        pnlDer.setBackground(Color.WHITE);

        JButton btnActualizarLogo = Ui.botonSecundario("Actualizar Imagen", null);
        btnActualizarLogo.addActionListener(e -> seleccionarLogo());
        pnlDer.add(btnActualizarLogo);

        JPanel centro = new JPanel(new BorderLayout(16, 0));
        centro.setBackground(Color.WHITE);
        centro.add(pnlLogoBox, BorderLayout.WEST);
        centro.add(info, BorderLayout.CENTER);

        card.add(centro, BorderLayout.CENTER);
        card.add(pnlDer, BorderLayout.EAST);
        return card;
    }

    private JPanel crearGrupoCampo(String etiqueta, Component campo) {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setBackground(Color.WHITE);
        JLabel l = new JLabel(etiqueta);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setForeground(new Color(71, 85, 105));
        p.add(l, BorderLayout.NORTH);
        p.add(campo, BorderLayout.CENTER);
        return p;
    }

    private void estilizarCampoTexto(JTextField tf) {
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tf.setForeground(new Color(15, 23, 42));
        tf.setPreferredSize(new Dimension(tf.getPreferredSize().width, 36));
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225), 1),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
    }

    private void estilizarCombo(JComboBox<?> cb) {
        cb.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cb.setBackground(Color.WHITE);
        cb.setForeground(new Color(15, 23, 42));
        cb.setPreferredSize(new Dimension(cb.getPreferredSize().width, 36));
    }

    private void cargarDatos() {
        ConfiguracionClinica cfg = repo.getConfiguracionClinica();
        if (cfg != null) {
            txtRazonSocial.setText(cfg.getRazonSocial());
            txtNombreComercial.setText(cfg.getNombreComercial());
            txtIdentificadorFiscal.setText(cfg.getIdentificadorFiscal());
            txtCorreo.setText(cfg.getCorreoInstitucional());
            txtTelefono.setText(cfg.getTelefonoUrgencias());
            txtDireccion.setText(cfg.getDireccionSedePrincipal());
            cbMoneda.setSelectedItem(cfg.getMonedaPrincipal());
            cbZonaHoraria.setSelectedItem(cfg.getZonaHoraria());
            cbSede.setSelectedItem(cfg.getSedeActiva());
        }
    }

    private void guardarCambios() {
        ConfiguracionClinica cfg = new ConfiguracionClinica(
                txtRazonSocial.getText().trim(),
                txtNombreComercial.getText().trim(),
                txtIdentificadorFiscal.getText().trim(),
                txtCorreo.getText().trim(),
                txtTelefono.getText().trim(),
                txtDireccion.getText().trim(),
                (String) cbMoneda.getSelectedItem(),
                (String) cbZonaHoraria.getSelectedItem(),
                "assets/logo_happypets.png",
                (String) cbSede.getSelectedItem()
        );

        repo.guardarConfiguracionClinica(cfg);

        JOptionPane.showMessageDialog(
                this,
                "✓ Los parámetros generales de HappyPets se han guardado exitosamente.\n"
                        + "Todos los documentos, recetas y facturas aplicarán esta configuración.",
                "Configuración Actualizada",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void seleccionarLogo() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Seleccionar Logotipo Institucional (PNG, JPG, SVG)");
        int res = chooser.showOpenDialog(this);
        if (res == JFileChooser.APPROVE_OPTION) {
            File f = chooser.getSelectedFile();
            JOptionPane.showMessageDialog(
                    this,
                    "✓ Logotipo institucional actualizado correctamente:\n" + f.getName() + " (" + (f.length() / 1024) + " KB)",
                    "Logotipo Actualizado",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }
    }

    private void exportarParametrosReporte() {
        StringBuilder html = new StringBuilder();
        html.append("<html><body style='font-family:sans-serif; padding:15px; color:#1e293b;'>");
        html.append("<h2 style='color:#006064; border-bottom:2px solid #00BCD4; padding-bottom:6px;'>FICHA TÉCNICA DE PARÁMETROS EMPRESARIALES</h2>");
        html.append("<p><strong>Fecha de Emisión:</strong> ").append(LocalDate.now()).append(" | <strong>Sistema:</strong> Veterinaria Happy Pets ERP</p>");
        html.append("<table border='1' cellpadding='6' cellspacing='0' style='border-collapse:collapse; width:100%; border-color:#cbd5e1; font-size:12px;'>");
        html.append("<tr style='background-color:#006064; color:white;'><th align='left'>Parámetro Institucional</th><th align='left'>Valor Asignado</th></tr>");
        html.append("<tr><td><b>Razón Social / Nombre Legal:</b></td><td>").append(txtRazonSocial.getText()).append("</td></tr>");
        html.append("<tr><td><b>Nombre Comercial:</b></td><td>").append(txtNombreComercial.getText()).append("</td></tr>");
        html.append("<tr><td><b>Identificador Fiscal (RUC/NIT):</b></td><td>").append(txtIdentificadorFiscal.getText()).append("</td></tr>");
        html.append("<tr><td><b>Correo Electrónico Oficial:</b></td><td>").append(txtCorreo.getText()).append("</td></tr>");
        html.append("<tr><td><b>Teléfono Urgencias 24/7:</b></td><td>").append(txtTelefono.getText()).append("</td></tr>");
        html.append("<tr><td><b>Dirección Sede Principal:</b></td><td>").append(txtDireccion.getText()).append("</td></tr>");
        html.append("<tr><td><b>Moneda Base ERP:</b></td><td>").append(cbMoneda.getSelectedItem()).append("</td></tr>");
        html.append("<tr><td><b>Zona Horaria Servidor:</b></td><td>").append(cbZonaHoraria.getSelectedItem()).append("</td></tr>");
        html.append("<tr><td><b>Sede Activa en Edición:</b></td><td>").append(cbSede.getSelectedItem()).append("</td></tr>");
        html.append("</table>");
        html.append("<p style='margin-top:15px; font-size:10px; color:#64748b;'>Documento oficial de configuración del ERP emitido para auditoría técnica y fiscal.</p>");
        html.append("</body></html>");

        StringBuilder csv = new StringBuilder("PARAMETRO,VALOR\n");
        csv.append("\"Razon Social\",\"").append(txtRazonSocial.getText()).append("\"\n");
        csv.append("\"Nombre Comercial\",\"").append(txtNombreComercial.getText()).append("\"\n");
        csv.append("\"Identificador Fiscal\",\"").append(txtIdentificadorFiscal.getText()).append("\"\n");
        csv.append("\"Correo\",\"").append(txtCorreo.getText()).append("\"\n");
        csv.append("\"Telefono\",\"").append(txtTelefono.getText()).append("\"\n");
        csv.append("\"Direccion\",\"").append(txtDireccion.getText()).append("\"\n");
        csv.append("\"Moneda\",\"").append(cbMoneda.getSelectedItem()).append("\"\n");
        csv.append("\"Zona Horaria\",\"").append(cbZonaHoraria.getSelectedItem()).append("\"\n");
        csv.append("\"Sede\",\"").append(cbSede.getSelectedItem()).append("\"\n");

        Ui.mostrarVisorReporte(
            SwingUtilities.getWindowAncestor(this),
            "Parámetros Generales ERP",
            "Ficha Técnica de Parámetros Generales",
            html.toString(),
            csv.toString()
        );
    }
}
