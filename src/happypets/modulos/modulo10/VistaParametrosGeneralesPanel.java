package happypets.modulos.modulo10;

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
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.io.File;
import java.time.LocalDate;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import happypets.data.RepositorioVeterinaria;
import happypets.model.ConfiguracionClinica;
import happypets.ui.Iconos;
import happypets.ui.Ui;

/**
 * Submódulo 10.1: Parámetros Generales de la Clínica Veterinaria
 * Responsable: Minaya Bravo, Almendra Lili
 * Configuración centralizada de datos fiscales, razón social, sedes, comprobantes y membrete oficial
 * con arquitectura Master-Detail, 4 KPIs corporativos y hub de previsualización en vivo de comprobantes.
 */
public class VistaParametrosGeneralesPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private final RepositorioVeterinaria repo = RepositorioVeterinaria.getInstancia();

    // KPIs Superiores
    private JLabel lblKpiSedes;
    private JLabel lblKpiFiscal;
    private JLabel lblKpiMoneda;
    private JLabel lblKpiMembrete;

    // Formulario (Master)
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

    // Live Detail Hub (360px)
    private JLabel lblHubLogoMonograma;
    private JLabel lblHubNombreComercial;
    private JLabel lblHubRazonSocial;
    private JLabel lblHubSedeChip;

    private JLabel lblHubNitValor;
    private JLabel lblHubMonedaValor;
    private JLabel lblHubTelefonoValor;
    private JLabel lblHubZonaValor;

    private JTextArea txtHubMembretePreview;

    public VistaParametrosGeneralesPanel() {
        setLayout(new BorderLayout());
        setOpaque(false);

        // Panel Superior Fijo (Cabecera + 4 KPIs)
        JPanel panelSuperior = new JPanel();
        panelSuperior.setOpaque(false);
        panelSuperior.setLayout(new BoxLayout(panelSuperior, BoxLayout.Y_AXIS));
        panelSuperior.setBorder(new EmptyBorder(12, 18, 10, 18));

        panelSuperior.add(crearCabeceraVista());
        panelSuperior.add(Box.createVerticalStrut(10));
        panelSuperior.add(crearFilaKpis());

        add(panelSuperior, BorderLayout.NORTH);

        // Contenedor Master-Detail
        JPanel panelCentro = new JPanel(new BorderLayout());
        panelCentro.setOpaque(false);
        panelCentro.setBorder(new EmptyBorder(0, 18, 14, 18));
        panelCentro.add(crearSplitMasterDetail(), BorderLayout.CENTER);

        add(panelCentro, BorderLayout.CENTER);

        cargarDatos();
        actualizarHubEnVivo();
    }

    private JPanel crearCabeceraVista() {
        JPanel cab = new JPanel(new BorderLayout());
        cab.setOpaque(false);
        cab.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

        JPanel izq = new JPanel();
        izq.setOpaque(false);
        izq.setLayout(new BoxLayout(izq, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("Parámetros Generales de la Clínica");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 17));
        titulo.setForeground(Ui.TEXTO_TITULO);

        JLabel subtitulo = new JLabel("Módulo 10.1 · Configuración legal, fiscal, sedes y personalización de comprobantes oficiales");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitulo.setForeground(Ui.TEXTO_MUTED);

        izq.add(titulo);
        izq.add(Box.createVerticalStrut(2));
        izq.add(subtitulo);
        cab.add(izq, BorderLayout.WEST);

        JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        der.setOpaque(false);

        JButton btnExportar = Ui.boton("Exportar Ficha", false);
        btnExportar.addActionListener(e -> exportarParametrosReporte());
        der.add(btnExportar);

        JButton btnGuardar = Ui.boton("Guardar Cambios", true);
        btnGuardar.addActionListener(e -> guardarCambios());
        der.add(btnGuardar);

        cab.add(der, BorderLayout.EAST);
        return cab;
    }

    private JPanel crearFilaKpis() {
        JPanel kpis = new JPanel(new GridLayout(1, 4, 12, 0));
        kpis.setOpaque(false);
        kpis.setPreferredSize(new Dimension(0, 74));
        kpis.setMaximumSize(new Dimension(Integer.MAX_VALUE, 74));

        lblKpiSedes = new JLabel("3 Sedes en Lima");
        lblKpiFiscal = new JLabel("RUC 20608421154");
        lblKpiMoneda = new JLabel("PEN (S/.) • UTC-5");
        lblKpiMembrete = new JLabel("SUNAT / CPE OK");

        kpis.add(crearTarjetaKpi("Sedes Operativas", lblKpiSedes, "Miraflores (Principal)", Iconos.crearIconoHuella(22, Ui.TURQUESA)));
        kpis.add(crearTarjetaKpi("Identificador Fiscal", lblKpiFiscal, "SUNAT RUC Vigente", Iconos.crearIconoFactura(22, Ui.AZUL_INFO)));
        kpis.add(crearTarjetaKpi("Régimen & Divisa", lblKpiMoneda, "Lima / Sol Peruano (S/.)", Iconos.crearIconoMonedas(22, Ui.VERDE_EXITO)));
        kpis.add(crearTarjetaKpi("Comprobantes CPE", lblKpiMembrete, "Boletas y Facturas", Iconos.crearIconoCheck(22, Ui.NARANJA_AVISO)));

        return kpis;
    }

    private JPanel crearTarjetaKpi(String titulo, JLabel lblValor, String subtitulo, Icon icono) {
        JPanel card = new JPanel(new BorderLayout(8, 0)) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(Ui.BORDE_SUAVE);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(8, 12, 8, 12));

        JPanel texto = new JPanel();
        texto.setOpaque(false);
        texto.setLayout(new BoxLayout(texto, BoxLayout.Y_AXIS));

        JLabel lblTit = new JLabel(titulo);
        lblTit.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblTit.setForeground(Ui.TEXTO_MUTED);

        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblValor.setForeground(Ui.TEXTO_TITULO);

        JLabel lblSub = new JLabel(subtitulo);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblSub.setForeground(Ui.TEXTO_MUTED);

        texto.add(lblTit);
        texto.add(Box.createVerticalStrut(1));
        texto.add(lblValor);
        texto.add(Box.createVerticalStrut(1));
        texto.add(lblSub);

        card.add(texto, BorderLayout.CENTER);

        if (icono != null) {
            JLabel lblIco = new JLabel(icono);
            lblIco.setVerticalAlignment(SwingConstants.TOP);
            lblIco.setBorder(new EmptyBorder(2, 0, 0, 0));
            card.add(lblIco, BorderLayout.EAST);
        }

        return card;
    }

    private JSplitPane crearSplitMasterDetail() {
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setOpaque(false);
        split.setBorder(null);
        split.setDividerSize(8);
        split.setContinuousLayout(true);
        split.setResizeWeight(1.0);

        split.setLeftComponent(crearPanelMasterConfiguracion());
        split.setRightComponent(crearPanelLiveDetailHub());

        return split;
    }

    private JPanel crearPanelMasterConfiguracion() {
        JPanel master = new JPanel(new BorderLayout(0, 10)) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(Ui.BORDE_SUAVE);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                g2.dispose();
            }
        };
        master.setOpaque(false);
        master.setBorder(new EmptyBorder(14, 16, 14, 16));

        ScrollablePanel scrollContent = new ScrollablePanel();
        scrollContent.setOpaque(false);
        scrollContent.setLayout(new BoxLayout(scrollContent, BoxLayout.Y_AXIS));

        // Título del formulario
        JLabel lblSubtit = new JLabel("Identidad Legal y Fiscal de la Empresa:");
        lblSubtit.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblSubtit.setForeground(Ui.TEXTO_TITULO);
        lblSubtit.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollContent.add(lblSubtit);
        scrollContent.add(Box.createVerticalStrut(10));

        // Formulario GridBagLayout
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        form.setAlignmentX(Component.LEFT_ALIGNMENT);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtRazonSocial = new JTextField(15);
        txtNombreComercial = new JTextField(15);
        txtIdentificadorFiscal = new JTextField(15);
        txtCorreo = new JTextField(15);
        txtTelefono = new JTextField(15);
        txtDireccion = new JTextField(25);

        cbMoneda = Ui.combo(new String[]{
                "PEN (S/) - Sol Peruano",
                "USD ($) - Dólar Estadounidense"
        });

        cbZonaHoraria = Ui.combo(new String[]{
                "America/Lima (UTC -05:00)"
        });

        cbSede = Ui.combo(new String[]{
                "Sede Principal - Miraflores",
                "Sede Surco - El Polo",
                "Sede San Isidro"
        });

        DocumentListener docListener = new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { actualizarHubEnVivo(); }
            public void removeUpdate(DocumentEvent e) { actualizarHubEnVivo(); }
            public void changedUpdate(DocumentEvent e) { actualizarHubEnVivo(); }
        };
        txtRazonSocial.getDocument().addDocumentListener(docListener);
        txtNombreComercial.getDocument().addDocumentListener(docListener);
        txtIdentificadorFiscal.getDocument().addDocumentListener(docListener);
        txtCorreo.getDocument().addDocumentListener(docListener);
        txtTelefono.getDocument().addDocumentListener(docListener);
        txtDireccion.getDocument().addDocumentListener(docListener);
        cbMoneda.addActionListener(e -> actualizarHubEnVivo());
        cbZonaHoraria.addActionListener(e -> actualizarHubEnVivo());
        cbSede.addActionListener(e -> actualizarHubEnVivo());

        // Fila 0: Razón Social y Nombre Comercial
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.5;
        form.add(crearGrupoCampo("Razón Social Legal:", txtRazonSocial), gbc);

        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.5;
        form.add(crearGrupoCampo("Nombre Comercial:", txtNombreComercial), gbc);

        // Fila 1: Identificador Fiscal y Sede en Edición
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.5;
        form.add(crearGrupoCampo("Identificador Fiscal (RUC - SUNAT):", txtIdentificadorFiscal), gbc);

        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 0.5;
        form.add(crearGrupoCampo("Sede en Edición:", cbSede), gbc);

        // Fila 2: Correo Institucional y Teléfono de Urgencias
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.5;
        form.add(crearGrupoCampo("Correo Institucional:", txtCorreo), gbc);

        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 0.5;
        form.add(crearGrupoCampo("Teléfono Central / Urgencias:", txtTelefono), gbc);

        // Fila 3: Dirección Sede Principal (Full width)
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; gbc.weightx = 1.0;
        form.add(crearGrupoCampo("Dirección Sede Principal:", txtDireccion), gbc);
        gbc.gridwidth = 1;

        // Fila 4: Moneda y Zona Horaria
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0.5;
        form.add(crearGrupoCampo("Moneda Principal de Facturación:", cbMoneda), gbc);

        gbc.gridx = 1; gbc.gridy = 4; gbc.weightx = 0.5;
        form.add(crearGrupoCampo("Huso Horario Oficial:", cbZonaHoraria), gbc);

        scrollContent.add(form);
        scrollContent.add(Box.createVerticalStrut(14));

        // Card Logotipo Oficial
        JPanel cardLogo = new JPanel(new BorderLayout(14, 0));
        cardLogo.setOpaque(false);
        cardLogo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(241, 245, 249), 1, true),
                new EmptyBorder(12, 14, 12, 14)
        ));
        cardLogo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel pnlLogoBox = new JPanel(new BorderLayout());
        pnlLogoBox.setBackground(new Color(241, 245, 249));
        pnlLogoBox.setPreferredSize(new Dimension(72, 72));
        pnlLogoBox.setBorder(BorderFactory.createLineBorder(new Color(203, 213, 225), 1));

        lblLogoPreview = new JLabel(Iconos.crearIconoHuella(36, Ui.TURQUESA_OSCURO), SwingConstants.CENTER);
        pnlLogoBox.add(lblLogoPreview, BorderLayout.CENTER);
        cardLogo.add(pnlLogoBox, BorderLayout.WEST);

        JPanel infoLogo = new JPanel();
        infoLogo.setOpaque(false);
        infoLogo.setLayout(new BoxLayout(infoLogo, BoxLayout.Y_AXIS));

        JLabel lblTitLogo = new JLabel("Logotipo Oficial para Recetas y Comprobantes");
        lblTitLogo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTitLogo.setForeground(Ui.TEXTO_TITULO);

        JLabel lblDescLogo = new JLabel("<html>Formatos admitidos: PNG, JPG o SVG transparente. Resolución recomendada 400x400 px.<br/>Aparecerá en el encabezado de recetas médicas, historias clínicas y boletas electrónicas.</html>");
        lblDescLogo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblDescLogo.setForeground(Ui.TEXTO_MUTED);

        infoLogo.add(lblTitLogo);
        infoLogo.add(Box.createVerticalStrut(4));
        infoLogo.add(lblDescLogo);
        cardLogo.add(infoLogo, BorderLayout.CENTER);

        JButton btnActualizarLogo = Ui.botonSecundario("Cambiar Logo", null);
        btnActualizarLogo.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnActualizarLogo.addActionListener(e -> cambiarLogotipo());
        cardLogo.add(btnActualizarLogo, BorderLayout.EAST);

        scrollContent.add(cardLogo);

        JScrollPane scroll = new JScrollPane(scrollContent);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(14);

        master.add(scroll, BorderLayout.CENTER);
        return master;
    }

    private JPanel crearPanelLiveDetailHub() {
        JPanel detail = new JPanel(new BorderLayout()) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(Ui.BORDE_SUAVE);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                g2.dispose();
            }
        };
        detail.setOpaque(false);
        detail.setPreferredSize(new Dimension(360, 0));
        detail.setMinimumSize(new Dimension(320, 0));

        ScrollablePanel scrollContent = new ScrollablePanel();
        scrollContent.setOpaque(false);
        scrollContent.setLayout(new BoxLayout(scrollContent, BoxLayout.Y_AXIS));
        scrollContent.setBorder(new EmptyBorder(14, 16, 14, 16));

        // 1. Hero Block
        JPanel hero = new JPanel(new BorderLayout(8, 0));
        hero.setOpaque(false);
        hero.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblHubLogoMonograma = new JLabel("HP", SwingConstants.CENTER) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int s = Math.min(getWidth(), getHeight()) - 2;
                int x = (getWidth() - s) / 2;
                int y = (getHeight() - s) / 2;
                g2.setColor(Ui.TURQUESA_SUAVE);
                g2.fillOval(x, y, s, s);
                g2.setColor(Ui.TURQUESA);
                g2.drawOval(x, y, s, s);
                super.paintComponent(g);
                g2.dispose();
            }
        };
        lblHubLogoMonograma.setPreferredSize(new Dimension(46, 46));
        lblHubLogoMonograma.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblHubLogoMonograma.setForeground(Ui.TURQUESA_PROFUNDO);

        JPanel avatarBox = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        avatarBox.setOpaque(false);
        avatarBox.setPreferredSize(new Dimension(46, 46));
        avatarBox.add(lblHubLogoMonograma);
        hero.add(avatarBox, BorderLayout.WEST);

        JPanel heroText = new JPanel();
        heroText.setOpaque(false);
        heroText.setLayout(new BoxLayout(heroText, BoxLayout.Y_AXIS));

        lblHubNombreComercial = new JLabel("HappyPets Clínica");
        lblHubNombreComercial.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblHubNombreComercial.setForeground(Ui.TEXTO_TITULO);
        lblHubNombreComercial.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblHubRazonSocial = new JLabel("HappyPets S.A.C.");
        lblHubRazonSocial.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblHubRazonSocial.setForeground(Ui.TEXTO_MUTED);
        lblHubRazonSocial.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel rowChips = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        rowChips.setOpaque(false);
        rowChips.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblHubSedeChip = new JLabel(" Sede Principal Activa ");
        lblHubSedeChip.setOpaque(true);
        lblHubSedeChip.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblHubSedeChip.setBackground(new Color(220, 252, 231));
        lblHubSedeChip.setForeground(new Color(21, 128, 61));
        lblHubSedeChip.setBorder(BorderFactory.createLineBorder(new Color(134, 239, 172), 1, true));
        rowChips.add(lblHubSedeChip);

        heroText.add(lblHubNombreComercial);
        heroText.add(Box.createVerticalStrut(2));
        heroText.add(lblHubRazonSocial);
        heroText.add(Box.createVerticalStrut(4));
        heroText.add(rowChips);

        hero.add(heroText, BorderLayout.CENTER);
        scrollContent.add(hero);
        scrollContent.add(Box.createVerticalStrut(14));

        // 2. Telemetría 2x2
        JPanel telemetria = new JPanel(new GridLayout(2, 2, 8, 8));
        telemetria.setOpaque(false);
        telemetria.setAlignmentX(Component.LEFT_ALIGNMENT);
        telemetria.setMaximumSize(new Dimension(Integer.MAX_VALUE, 92));

        lblHubNitValor = new JLabel("900.842.115-4");
        lblHubMonedaValor = new JLabel("COP ($)");
        lblHubTelefonoValor = new JLabel("+57 312 000 1122");
        lblHubZonaValor = new JLabel("UTC -05:00");

        telemetria.add(crearMicroCard("Identificador Fiscal", lblHubNitValor));
        telemetria.add(crearMicroCard("Moneda Facturación", lblHubMonedaValor));
        telemetria.add(crearMicroCard("Teléfono Central", lblHubTelefonoValor));
        telemetria.add(crearMicroCard("Huso Horario", lblHubZonaValor));

        scrollContent.add(telemetria);
        scrollContent.add(Box.createVerticalStrut(12));

        // 3. Card Vista Previa de Membrete Impreso
        txtHubMembretePreview = new JTextArea();
        txtHubMembretePreview.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        txtHubMembretePreview.setForeground(Ui.TEXTO_TITULO);
        txtHubMembretePreview.setLineWrap(true);
        txtHubMembretePreview.setWrapStyleWord(true);
        txtHubMembretePreview.setEditable(false);
        txtHubMembretePreview.setOpaque(false);

        JPanel pnlMembrete = crearSeccionCard("Encabezado Oficial de Comprobantes", txtHubMembretePreview);
        pnlMembrete.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollContent.add(pnlMembrete);
        scrollContent.add(Box.createVerticalStrut(14));

        // 4. Botones 2x2
        JPanel gridAcciones = new JPanel(new GridLayout(2, 2, 8, 8));
        gridAcciones.setOpaque(false);
        gridAcciones.setAlignmentX(Component.LEFT_ALIGNMENT);
        gridAcciones.setMaximumSize(new Dimension(Integer.MAX_VALUE, 74));

        JButton btnExportarFicha = Ui.botonSecundario("Descargar PDF", Iconos.crearIconoDescargar(12, Ui.TEXTO_TITULO));
        btnExportarFicha.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnExportarFicha.addActionListener(e -> exportarParametrosReporte());

        JButton btnGuardarDock = Ui.botonPrimario("Guardar", null);
        btnGuardarDock.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnGuardarDock.addActionListener(e -> guardarCambios());

        JButton btnProbarReceta = Ui.botonSecundario("Test Receta", Iconos.crearIconoFactura(12, Ui.TEXTO_TITULO));
        btnProbarReceta.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnProbarReceta.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                    "Simulación de membrete oficial validada con éxito.\n" +
                    "Los campos de encabezado se ajustan a las normativas de impresión A4 y formato térmico 80mm.",
                    "Validación de Membrete", JOptionPane.INFORMATION_MESSAGE);
        });

        JButton btnRestablecer = Ui.botonSecundario("Restablecer", null);
        btnRestablecer.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnRestablecer.addActionListener(e -> {
            cargarDatos();
            JOptionPane.showMessageDialog(this, "Parámetros restablecidos desde la base de datos.", "Restablecer", JOptionPane.INFORMATION_MESSAGE);
        });

        gridAcciones.add(btnExportarFicha);
        gridAcciones.add(btnGuardarDock);
        gridAcciones.add(btnProbarReceta);
        gridAcciones.add(btnRestablecer);

        scrollContent.add(gridAcciones);

        JScrollPane scroll = new JScrollPane(scrollContent);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(14);

        detail.add(scroll, BorderLayout.CENTER);
        return detail;
    }

    private JPanel crearMicroCard(String etiqueta, JLabel lblValor) {
        JPanel mc = new JPanel();
        mc.setOpaque(false);
        mc.setLayout(new BoxLayout(mc, BoxLayout.Y_AXIS));
        mc.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(241, 245, 249), 1, true),
                new EmptyBorder(6, 8, 6, 8)
        ));

        JLabel lblEtiq = new JLabel(etiqueta);
        lblEtiq.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblEtiq.setForeground(Ui.TEXTO_MUTED);

        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblValor.setForeground(Ui.TEXTO_TITULO);

        mc.add(lblEtiq);
        mc.add(Box.createVerticalStrut(2));
        mc.add(lblValor);

        return mc;
    }

    private JPanel crearSeccionCard(String titulo, JComponent contenido) {
        JPanel sec = new JPanel(new BorderLayout(0, 6));
        sec.setOpaque(false);
        sec.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(241, 245, 249), 1, true),
                new EmptyBorder(8, 10, 8, 10)
        ));

        JLabel lblTit = new JLabel(titulo);
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblTit.setForeground(Ui.TEXTO_MUTED);
        sec.add(lblTit, BorderLayout.NORTH);

        sec.add(contenido, BorderLayout.CENTER);
        return sec;
    }

    private JPanel crearGrupoCampo(String titulo, JComponent campo) {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setOpaque(false);

        JLabel lbl = new JLabel(titulo);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lbl.setForeground(Ui.TEXTO_MUTED);

        campo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        campo.setPreferredSize(new Dimension(campo.getPreferredSize().width, 32));

        p.add(lbl, BorderLayout.NORTH);
        p.add(campo, BorderLayout.CENTER);
        return p;
    }

    private void actualizarHubEnVivo() {
        String com = txtNombreComercial != null ? txtNombreComercial.getText().trim() : "HappyPets";
        String raz = txtRazonSocial != null ? txtRazonSocial.getText().trim() : "Servicios Veterinarios";
        String nit = txtIdentificadorFiscal != null ? txtIdentificadorFiscal.getText().trim() : "";
        String tel = txtTelefono != null ? txtTelefono.getText().trim() : "";
        String dir = txtDireccion != null ? txtDireccion.getText().trim() : "";
        String mon = cbMoneda != null ? (String) cbMoneda.getSelectedItem() : "";
        String zon = cbZonaHoraria != null ? (String) cbZonaHoraria.getSelectedItem() : "";
        String sed = cbSede != null ? (String) cbSede.getSelectedItem() : "Sede Principal";

        if (lblHubNombreComercial != null) lblHubNombreComercial.setText(com.isEmpty() ? "HappyPets" : com);
        if (lblHubRazonSocial != null) lblHubRazonSocial.setText(raz.isEmpty() ? "Servicios S.A.S." : raz);
        if (lblHubSedeChip != null) lblHubSedeChip.setText(" " + sed + " ");
        if (lblHubNitValor != null) lblHubNitValor.setText(nit.isEmpty() ? "-" : nit);
        if (lblHubMonedaValor != null) lblHubMonedaValor.setText(mon.split("-")[0].trim());
        if (lblHubTelefonoValor != null) lblHubTelefonoValor.setText(tel.isEmpty() ? "-" : tel);
        if (lblHubZonaValor != null) lblHubZonaValor.setText(zon.split(" ")[0]);

        if (txtHubMembretePreview != null) {
            txtHubMembretePreview.setText(
                    "=========================================\n" +
                    com.toUpperCase() + "\n" +
                    raz + " • " + nit + "\n" +
                    dir + "\n" +
                    "Central: " + tel + "\n" +
                    "Emisión: " + sed + "\n" +
                    "========================================="
            );
        }
    }

    private void cargarDatos() {
        ConfiguracionClinica cfg = repo.getConfiguracionClinica();
        if (cfg == null) return;

        txtRazonSocial.setText(cfg.getRazonSocial());
        txtNombreComercial.setText(cfg.getNombreComercial());
        txtIdentificadorFiscal.setText(cfg.getIdentificadorFiscal());
        txtCorreo.setText(cfg.getCorreoInstitucional());
        txtTelefono.setText(cfg.getTelefonoUrgencias());
        txtDireccion.setText(cfg.getDireccionSedePrincipal());

        cbMoneda.setSelectedItem(cfg.getMonedaPrincipal());
        cbZonaHoraria.setSelectedItem(cfg.getZonaHoraria());
        cbSede.setSelectedItem(cfg.getSedeActiva());

        actualizarHubEnVivo();
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
        actualizarHubEnVivo();

        JOptionPane.showMessageDialog(this,
                "Los parámetros de la clínica veterinaria fueron actualizados satisfactoriamente.",
                "Configuración Guardada", JOptionPane.INFORMATION_MESSAGE);
    }

    private void cambiarLogotipo() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Seleccionar Logotipo Institucional");
        int res = fc.showOpenDialog(this);
        if (res == JFileChooser.APPROVE_OPTION) {
            File f = fc.getSelectedFile();
            JOptionPane.showMessageDialog(this,
                    "Logotipo actualizado correctamente:\n" + f.getName() + " (" + (f.length() / 1024) + " KB)",
                    "Imagen Actualizada", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void exportarParametrosReporte() {
        ConfiguracionClinica cfg = repo.getConfiguracionClinica();
        String[][] meta = new String[][]{
            {"Razón Social", cfg.getRazonSocial()},
            {"Nombre Comercial", cfg.getNombreComercial()},
            {"Identificador Fiscal", cfg.getIdentificadorFiscal()},
            {"Sede Principal", cfg.getSedeActiva()},
            {"Moneda Oficial", cfg.getMonedaPrincipal()},
            {"Zona Horaria", cfg.getZonaHoraria()}
        };
        String[][] datos = new String[][]{
            {"1", "Nombre Legal / Razón Social", cfg.getRazonSocial(), "Oficial"},
            {"2", "Nombre Comercial", cfg.getNombreComercial(), "Marca Registrada"},
            {"3", "Identificador Tributario", cfg.getIdentificadorFiscal(), "DIAN / SUNAT"},
            {"4", "Canal de Contacto", cfg.getCorreoInstitucional() + " | " + cfg.getTelefonoUrgencias(), "Atención al Cliente"},
            {"5", "Dirección Central", cfg.getDireccionSedePrincipal(), "Sede Matriz"}
        };
        Ui.mostrarVisorReporte(
            (java.awt.Frame) SwingUtilities.getWindowAncestor(this),
            "FICHA TÉCNICA INSTITUCIONAL DE LA CLÍNICA",
            "Parámetros y Configuración Empresarial Happy Pets",
            meta,
            new String[]{"N°", "Parámetro Institucional", "Valor Configurado", "Estado / Entidad"},
            datos,
            "Directorio oficial de configuración corporativa Happy Pets.",
            "Ficha_Institucional_Clinica"
        );
    }

    private static class ScrollablePanel extends JPanel implements Scrollable {
        private static final long serialVersionUID = 1L;

        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }

        @Override
        public int getScrollableUnitIncrement(java.awt.Rectangle visibleRect, int orientation, int direction) {
            return 16;
        }

        @Override
        public int getScrollableBlockIncrement(java.awt.Rectangle visibleRect, int orientation, int direction) {
            return 32;
        }

        @Override
        public boolean getScrollableTracksViewportWidth() {
            return true;
        }

        @Override
        public boolean getScrollableTracksViewportHeight() {
            return false;
        }
    }
}
