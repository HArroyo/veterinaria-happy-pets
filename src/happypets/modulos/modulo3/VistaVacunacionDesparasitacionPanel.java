package happypets.modulos.modulo3;

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
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import happypets.data.RepositorioVeterinaria;
import happypets.model.Cliente;
import happypets.model.Mascota;
import happypets.model.RegistroInmunizacion;
import happypets.ui.Iconos;
import happypets.ui.Ui;

/**
 * Submódulo 3.2: Vacunación y Desparasitación Preventiva.
 * Diseñado según el wireframe oficial (Pág. 5 Izquierda):
 * - Registro de vacunas (biológico, lote, laboratorio, dosis, vencimiento del frasco).
 * - Registro de desparasitaciones (interna/externa, peso al dosificar, principio activo).
 * - Historial completo de aplicaciones con buscador.
 * - Monitoreo de próximas dosis y alertas para notificación al tutor.
 */
public class VistaVacunacionDesparasitacionPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private static final Color COLOR_BORDE = new Color(226, 232, 240);
    private static final Color COLOR_AZUL_PRIMARIO = new Color(2, 132, 199);
    private static final Color COLOR_TEXTO_TITULO = new Color(30, 41, 59);
    private static final Color COLOR_TEXTO_MUTED = new Color(100, 116, 139);

    private final RepositorioVeterinaria repo = RepositorioVeterinaria.getInstancia();

    // KPIs
    private JLabel lblKpiTotalVacunas;
    private JLabel lblKpiTotalDesparasitaciones;
    private JLabel lblKpiProximasDosis;
    private JLabel lblKpiVencidas;

    // Formulario Vacuna
    private JComboBox<PacienteItem> cbPacienteVacuna;
    private JTextField txtTutorVacuna;
    private JComboBox<String> cbTipoVacuna;
    private JTextField txtLoteVacuna;
    private JTextField txtDosisVacuna;
    private JTextField txtVetVacuna;
    private JTextField txtProxVacuna;
    private JTextField txtObsVacuna;

    // Formulario Desparasitación
    private JComboBox<PacienteItem> cbPacienteDesp;
    private JTextField txtTutorDesp;
    private JComboBox<String> cbProductoDesp;
    private JComboBox<String> cbTipoDesp;
    private JTextField txtPesoDesp;
    private JTextField txtDosisDesp;
    private JTextField txtVetDesp;
    private JTextField txtProxDesp;

    // Tabla Historial
    private JTable tablaHistorial;
    private DefaultTableModel modeloHistorial;
    private JTextField txtBuscarHistorial;
    private JLabel lblContadorHistorial;
    private List<RegistroInmunizacion> listaActual;

    public VistaVacunacionDesparasitacionPanel() {
        setLayout(new BorderLayout());
        setOpaque(false);

        JPanel contenido = new JPanel();
        contenido.setOpaque(false);
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setBorder(new EmptyBorder(12, 18, 14, 18));

        // 1. Cabecera
        contenido.add(crearCabeceraVista());
        contenido.add(Box.createVerticalStrut(8));

        // 2. Fila de KPIs
        contenido.add(crearFilaKpis());
        contenido.add(Box.createVerticalStrut(10));

        // 3. Dos bloques de registro: Vacunas y Desparasitaciones (Fila Superior)
        contenido.add(crearFilaRegistroFormularios());
        contenido.add(Box.createVerticalStrut(12));

        // 4. Bloque Inferior: Tabla de Historial y Próximas Dosis
        contenido.add(crearCardHistorialInmunizaciones());

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(contenido, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(wrapper);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);

        recargarDatos();
    }

    private JPanel crearCabeceraVista() {
        JPanel cab = new JPanel(new BorderLayout());
        cab.setOpaque(false);

        JPanel izq = new JPanel();
        izq.setOpaque(false);
        izq.setLayout(new BoxLayout(izq, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("Vacunación y Desparasitación Preventiva");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 17));
        titulo.setForeground(COLOR_TEXTO_TITULO);

        JLabel subtitulo = new JLabel("Módulo 3.2 · Registro de biológicos, control de lotes, antiparasitarios y fechas de refuerzo");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitulo.setForeground(COLOR_TEXTO_MUTED);

        izq.add(titulo);
        izq.add(Box.createVerticalStrut(2));
        izq.add(subtitulo);
        cab.add(izq, BorderLayout.CENTER);

        JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        der.setOpaque(false);

        JButton btnNotificarMasivo = crearBotonWeb("📲 Notificar Refuerzos Próximos", false, () -> notificarProximasDosis());
        der.add(btnNotificarMasivo);

        cab.add(der, BorderLayout.EAST);
        return cab;
    }

    private JPanel crearFilaKpis() {
        JPanel fila = new JPanel(new GridLayout(1, 4, 12, 0));
        fila.setOpaque(false);

        lblKpiTotalVacunas = new JLabel("3", SwingConstants.LEFT);
        lblKpiTotalDesparasitaciones = new JLabel("3", SwingConstants.LEFT);
        lblKpiProximasDosis = new JLabel("2", SwingConstants.LEFT);
        lblKpiVencidas = new JLabel("1", SwingConstants.LEFT);

        fila.add(crearCardKpi(lblKpiTotalVacunas, "VACUNAS APLICADAS", new Color(224, 242, 254), COLOR_AZUL_PRIMARIO, Iconos.crearIconoJeringa(20, COLOR_AZUL_PRIMARIO)));
        fila.add(crearCardKpi(lblKpiTotalDesparasitaciones, "DESPARASITACIONES", new Color(220, 252, 231), new Color(22, 163, 74), Iconos.crearIconoPildora(20, new Color(22, 163, 74))));
        fila.add(crearCardKpi(lblKpiProximasDosis, "REFUERZOS PRÓXIMOS (30 DÍAS)", new Color(254, 243, 199), new Color(217, 119, 6), Iconos.crearIconoCalendario(20, new Color(217, 119, 6))));
        fila.add(crearCardKpi(lblKpiVencidas, "DOSIS PENDIENTES / VENCIDAS", new Color(254, 226, 226), new Color(220, 38, 38), Iconos.crearIconoAlertaTriaje(20, new Color(220, 38, 38))));

        return fila;
    }

    private JPanel crearCardKpi(JLabel lblValor, String etiqueta, Color colorFondoIco, Color colorIco, Icon icono) {
        JPanel card = new JPanel(new BorderLayout(10, 0)) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(COLOR_BORDE);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(8, 12, 8, 12));

        JLabel iconBox = new JLabel(icono, SwingConstants.CENTER) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(colorFondoIco);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        iconBox.setPreferredSize(new Dimension(36, 36));
        card.add(iconBox, BorderLayout.WEST);

        JPanel centro = new JPanel();
        centro.setOpaque(false);
        centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));

        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblValor.setForeground(COLOR_TEXTO_TITULO);

        JLabel lblSub = new JLabel(etiqueta);
        lblSub.setFont(new Font("Segoe UI", Font.BOLD, 9));
        lblSub.setForeground(COLOR_TEXTO_MUTED);

        centro.add(lblValor);
        centro.add(lblSub);
        card.add(centro, BorderLayout.CENTER);

        return card;
    }

    private JPanel crearFilaRegistroFormularios() {
        JPanel fila = new JPanel(new GridLayout(1, 2, 14, 0));
        fila.setOpaque(false);

        fila.add(crearCardRegistrarVacuna());
        fila.add(crearCardRegistrarDesparasitacion());

        return fila;
    }

    private JPanel crearCardRegistrarVacuna() {
        JPanel card = new JPanel(new BorderLayout()) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(COLOR_BORDE);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(12, 14, 12, 14));

        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));

        JLabel tit = new JLabel("1) Registrar Vacuna Biológica");
        tit.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tit.setForeground(COLOR_TEXTO_TITULO);
        tit.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(tit);
        form.add(Box.createVerticalStrut(8));

        // Paciente y tutor
        cbPacienteVacuna = new JComboBox<>();
        cbPacienteVacuna.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        cbPacienteVacuna.setBackground(Color.WHITE);
        cbPacienteVacuna.setPreferredSize(new Dimension(0, 26));
        cbPacienteVacuna.addActionListener(e -> autocompletarTutorVacuna());
        form.add(crearFilaCampo("Paciente (Nombre / Código) *", cbPacienteVacuna));
        form.add(Box.createVerticalStrut(4));

        txtTutorVacuna = crearCampoTexto(false);
        form.add(crearFilaCampo("Tutor Asignado", txtTutorVacuna));
        form.add(Box.createVerticalStrut(4));

        // Tipo de Vacuna
        String[] vacunas = {
                "Séxtuple Canina DHPP+L (Distemper, Parvovirus, Hepatitis, Leptospira)",
                "Triple Felina (Panleucopenia, Rinotraqueítis, Calicivirus)",
                "Vacuna Antirrábica Anual Rabisin",
                "Tos de las Perreras (KC Bronchicine)",
                "Leucemia Felina FeLV",
                "Quíntuple Canina DHPP"
        };
        cbTipoVacuna = new JComboBox<>(vacunas);
        cbTipoVacuna.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        cbTipoVacuna.setBackground(Color.WHITE);
        form.add(crearFilaCampo("Tipo de Vacuna *", cbTipoVacuna));
        form.add(Box.createVerticalStrut(4));

        JPanel filaDet = new JPanel(new GridLayout(1, 2, 8, 0));
        filaDet.setOpaque(false);
        filaDet.setAlignmentX(Component.LEFT_ALIGNMENT);
        filaDet.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        txtDosisVacuna = crearCampoTexto(true);
        txtDosisVacuna.setText("1.0 ml Subcutánea");
        txtLoteVacuna = crearCampoTexto(true);
        txtLoteVacuna.setText("Zoetis · Lote VAC-2024-9916");
        filaDet.add(crearFilaCampo("Dosis / Vía *", txtDosisVacuna));
        filaDet.add(crearFilaCampo("Laboratorio y Lote *", txtLoteVacuna));
        form.add(filaDet);
        form.add(Box.createVerticalStrut(4));

        JPanel filaVet = new JPanel(new GridLayout(1, 2, 8, 0));
        filaVet.setOpaque(false);
        filaVet.setAlignmentX(Component.LEFT_ALIGNMENT);
        filaVet.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        txtVetVacuna = crearCampoTexto(true);
        txtVetVacuna.setText("Dra. Camila Morales");
        txtProxVacuna = crearCampoTexto(true);
        txtProxVacuna.setText(LocalDate.now().plusYears(1).format(RegistroInmunizacion.FECHA_FORMATTER));
        filaVet.add(crearFilaCampo("Veterinario Responsable", txtVetVacuna));
        filaVet.add(crearFilaCampo("Próxima Dosis / Refuerzo *", txtProxVacuna));
        form.add(filaVet);
        form.add(Box.createVerticalStrut(4));

        txtObsVacuna = crearCampoTexto(true);
        txtObsVacuna.setText("Paciente afebril. Tolera biológico sin reacciones adversas inmediatas.");
        form.add(crearFilaCampo("Observaciones Clínicas", txtObsVacuna));
        form.add(Box.createVerticalStrut(10));

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        btnRow.setOpaque(false);
        btnRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        JButton btnGuardar = crearBotonWeb("💉 Guardar Vacuna", true, () -> guardarVacuna());
        btnRow.add(btnGuardar);
        form.add(btnRow);

        card.add(form, BorderLayout.NORTH);
        return card;
    }

    private JPanel crearCardRegistrarDesparasitacion() {
        JPanel card = new JPanel(new BorderLayout()) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(COLOR_BORDE);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(12, 14, 12, 14));

        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));

        JLabel tit = new JLabel("2) Registrar Desparasitación");
        tit.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tit.setForeground(COLOR_TEXTO_TITULO);
        tit.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(tit);
        form.add(Box.createVerticalStrut(8));

        // Paciente y tutor
        cbPacienteDesp = new JComboBox<>();
        cbPacienteDesp.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        cbPacienteDesp.setBackground(Color.WHITE);
        cbPacienteDesp.setPreferredSize(new Dimension(0, 26));
        cbPacienteDesp.addActionListener(e -> autocompletarTutorDesp());
        form.add(crearFilaCampo("Paciente (Nombre / Código) *", cbPacienteDesp));
        form.add(Box.createVerticalStrut(4));

        txtTutorDesp = crearCampoTexto(false);
        form.add(crearFilaCampo("Tutor Asignado", txtTutorDesp));
        form.add(Box.createVerticalStrut(4));

        // Producto y Tipo
        JPanel filaProd = new JPanel(new GridLayout(1, 2, 8, 0));
        filaProd.setOpaque(false);
        filaProd.setAlignmentX(Component.LEFT_ALIGNMENT);
        filaProd.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        String[] prods = {
                "Simparica Trio (Oral Masticable)",
                "Total F Total Plus (Tableta Oral)",
                "Bravecto Plus Spot-on (Pipeta Tópica)",
                "Nexgard Spectra (Masticable)",
                "Drontal Plus Sabor (Tableta)"
        };
        cbProductoDesp = new JComboBox<>(prods);
        cbProductoDesp.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        cbProductoDesp.setBackground(Color.WHITE);

        cbTipoDesp = new JComboBox<>(new String[]{"Desparasitación Interna", "Desparasitación Externa", "Control Integral"});
        cbTipoDesp.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        cbTipoDesp.setBackground(Color.WHITE);

        filaProd.add(crearFilaCampo("Producto Antiparasitario *", cbProductoDesp));
        filaProd.add(crearFilaCampo("Vía / Tipo *", cbTipoDesp));
        form.add(filaProd);
        form.add(Box.createVerticalStrut(4));

        JPanel filaPeso = new JPanel(new GridLayout(1, 2, 8, 0));
        filaPeso.setOpaque(false);
        filaPeso.setAlignmentX(Component.LEFT_ALIGNMENT);
        filaPeso.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        txtPesoDesp = crearCampoTexto(true);
        txtPesoDesp.setText("28.4");
        txtDosisDesp = crearCampoTexto(true);
        txtDosisDesp.setText("1 tableta (rango 20 - 40 kg)");
        filaPeso.add(crearFilaCampo("Peso Mascota (kg) *", txtPesoDesp));
        filaPeso.add(crearFilaCampo("Dosis Administrada *", txtDosisDesp));
        form.add(filaPeso);
        form.add(Box.createVerticalStrut(4));

        JPanel filaProx = new JPanel(new GridLayout(1, 2, 8, 0));
        filaProx.setOpaque(false);
        filaProx.setAlignmentX(Component.LEFT_ALIGNMENT);
        filaProx.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        txtVetDesp = crearCampoTexto(true);
        txtVetDesp.setText("Dr. Roberto Mendoza");
        txtProxDesp = crearCampoTexto(true);
        txtProxDesp.setText(LocalDate.now().plusMonths(3).format(RegistroInmunizacion.FECHA_FORMATTER));
        filaProx.add(crearFilaCampo("Veterinario Responsable", txtVetDesp));
        filaProx.add(crearFilaCampo("Próxima Aplicación *", txtProxDesp));
        form.add(filaProx);
        form.add(Box.createVerticalStrut(10));

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        btnRow.setOpaque(false);
        btnRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        JButton btnGuardar = crearBotonWeb("💊 Guardar Desparasitación", true, () -> guardarDesparasitacion());
        btnRow.add(btnGuardar);
        form.add(btnRow);

        card.add(form, BorderLayout.NORTH);
        return card;
    }

    private JPanel crearCardHistorialInmunizaciones() {
        JPanel card = new JPanel(new BorderLayout(0, 8)) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(COLOR_BORDE);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(12, 14, 12, 14));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JPanel titBox = new JPanel();
        titBox.setOpaque(false);
        titBox.setLayout(new BoxLayout(titBox, BoxLayout.Y_AXIS));

        JLabel tit = new JLabel("3) Historial de Inmunizaciones y Próximas Dosis Preventivas");
        tit.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tit.setForeground(COLOR_TEXTO_TITULO);

        lblContadorHistorial = new JLabel("Cargando registros...");
        lblContadorHistorial.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblContadorHistorial.setForeground(COLOR_TEXTO_MUTED);

        titBox.add(tit);
        titBox.add(lblContadorHistorial);
        top.add(titBox, BorderLayout.WEST);

        // Barra de búsqueda
        JPanel searchBox = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        searchBox.setOpaque(false);

        txtBuscarHistorial = new JTextField(18);
        txtBuscarHistorial.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        txtBuscarHistorial.setPreferredSize(new Dimension(200, 26));
        txtBuscarHistorial.putClientProperty("JTextField.placeholderText", "Buscar paciente o biológico...");
        txtBuscarHistorial.addActionListener(e -> recargarDatos());

        JButton btnBuscar = crearBotonWeb("Buscar", false, () -> recargarDatos());
        searchBox.add(txtBuscarHistorial);
        searchBox.add(btnBuscar);
        top.add(searchBox, BorderLayout.EAST);

        card.add(top, BorderLayout.NORTH);

        // Tabla
        String[] columnas = {"Código", "Fecha Apl.", "Paciente", "Tutor", "Tipo Control", "Producto / Vacuna", "Dosis / Vía", "Próxima Dosis", "Estado"};
        modeloHistorial = new DefaultTableModel(columnas, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        tablaHistorial = new JTable(modeloHistorial);
        Ui.formatearTabla(tablaHistorial);
        tablaHistorial.setRowHeight(30);

        tablaHistorial.getColumnModel().getColumn(0).setPreferredWidth(85);
        tablaHistorial.getColumnModel().getColumn(1).setPreferredWidth(80);
        tablaHistorial.getColumnModel().getColumn(2).setPreferredWidth(90);
        tablaHistorial.getColumnModel().getColumn(8).setCellRenderer(new BadgeEstadoInmunizacionRenderer());

        JScrollPane sp = new JScrollPane(tablaHistorial);
        sp.setBorder(BorderFactory.createLineBorder(COLOR_BORDE, 1));
        sp.setPreferredSize(new Dimension(1000, 260));
        card.add(sp, BorderLayout.CENTER);

        // Botonera
        JPanel bot = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        bot.setOpaque(false);

        JButton btnNotificarInd = crearBotonWeb("📲 Enviar Recordatorio al Tutor", false, () -> notificarTutorSeleccionado());
        JButton btnVerCert = crearBotonWeb("📄 Generar Carnet Vacunal", false, () -> generarCarnetVacunal());
        bot.add(btnNotificarInd);
        bot.add(btnVerCert);
        card.add(bot, BorderLayout.SOUTH);

        return card;
    }

    private JPanel crearFilaCampo(String etiqueta, Component campo) {
        JPanel p = new JPanel(new BorderLayout(0, 2));
        p.setOpaque(false);
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));

        JLabel lbl = new JLabel(etiqueta, SwingConstants.LEFT);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lbl.setForeground(new Color(71, 85, 105));
        lbl.setHorizontalAlignment(SwingConstants.LEFT);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        p.add(lbl, BorderLayout.NORTH);
        p.add(campo, BorderLayout.CENTER);
        return p;
    }

    private JTextField crearCampoTexto(boolean editable) {
        JTextField tf = new JTextField();
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        tf.setPreferredSize(new Dimension(0, 26));
        tf.setEditable(editable);
        tf.setBackground(editable ? Color.WHITE : new Color(241, 245, 249));
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDE, 1),
                new EmptyBorder(2, 6, 2, 6)
        ));
        return tf;
    }

    private JButton crearBotonWeb(String texto, boolean primario, Runnable accion) {
        JButton btn = new JButton(texto) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (primario) {
                    g2.setColor(getModel().isRollover() ? new Color(3, 105, 161) : COLOR_AZUL_PRIMARIO);
                } else {
                    g2.setColor(getModel().isRollover() ? new Color(241, 245, 249) : Color.WHITE);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                if (!primario) {
                    g2.setColor(COLOR_BORDE);
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 6, 6);
                }
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btn.setForeground(primario ? Color.WHITE : new Color(51, 65, 85));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(5, 12, 5, 12));
        btn.setPreferredSize(new Dimension(btn.getPreferredSize().width, 28));
        btn.addActionListener(e -> accion.run());
        return btn;
    }

    public void recargarDatos() {
        if (cbPacienteVacuna.getItemCount() == 0) {
            for (Mascota m : repo.todasLasMascotas()) {
                PacienteItem item = new PacienteItem(m);
                cbPacienteVacuna.addItem(item);
                cbPacienteDesp.addItem(item);
            }
            autocompletarTutorVacuna();
            autocompletarTutorDesp();
        }

        List<RegistroInmunizacion> todos = repo.getInmunizaciones();
        String q = txtBuscarHistorial != null ? txtBuscarHistorial.getText().trim().toLowerCase() : "";

        listaActual = todos.stream().filter(r -> {
            if (q.isEmpty()) return true;
            return r.getNombreMascota().toLowerCase().contains(q) ||
                   r.getNombreTutor().toLowerCase().contains(q) ||
                   r.getProducto().toLowerCase().contains(q) ||
                   r.getTipoControl().toLowerCase().contains(q);
        }).toList();

        modeloHistorial.setRowCount(0);
        for (RegistroInmunizacion r : listaActual) {
            modeloHistorial.addRow(new Object[]{
                    r.getIdRegistro(),
                    r.getFechaAplicacionFormateada(),
                    r.getNombreMascota(),
                    r.getNombreTutor(),
                    r.getTipoControl(),
                    r.getProducto(),
                    r.getDosis(),
                    r.getProximaFechaFormateada(),
                    r.getEstado()
            });
        }

        lblContadorHistorial.setText(listaActual.size() + " registros en el plan preventivo de Happy Pets");

        // KPIs
        long vacs = todos.stream().filter(r -> r.getTipoControl().startsWith("Vacun")).count();
        long desps = todos.stream().filter(r -> r.getTipoControl().startsWith("Despar")).count();
        long prox = todos.stream().filter(r -> "Próxima".equalsIgnoreCase(r.getEstado())).count();
        long pend = todos.stream().filter(r -> "Pendiente".equalsIgnoreCase(r.getEstado())).count();

        lblKpiTotalVacunas.setText(String.valueOf(vacs));
        lblKpiTotalDesparasitaciones.setText(String.valueOf(desps));
        lblKpiProximasDosis.setText(String.valueOf(prox));
        lblKpiVencidas.setText(String.valueOf(pend));
    }

    private void autocompletarTutorVacuna() {
        PacienteItem item = (PacienteItem) cbPacienteVacuna.getSelectedItem();
        if (item != null && item.mascota != null) {
            repo.getClienteDeMascota(item.mascota.getCodigo()).ifPresent(c -> {
                txtTutorVacuna.setText(c.getNombreCompleto() + " (" + c.getTelefonoPrincipal() + ")");
            });
        }
    }

    private void autocompletarTutorDesp() {
        PacienteItem item = (PacienteItem) cbPacienteDesp.getSelectedItem();
        if (item != null && item.mascota != null) {
            repo.getClienteDeMascota(item.mascota.getCodigo()).ifPresent(c -> {
                txtTutorDesp.setText(c.getNombreCompleto() + " (" + c.getTelefonoPrincipal() + ")");
                txtPesoDesp.setText(String.valueOf(item.mascota.getPesoActualKg()));
            });
        }
    }

    private void guardarVacuna() {
        PacienteItem pi = (PacienteItem) cbPacienteVacuna.getSelectedItem();
        if (pi == null || pi.mascota == null) return;
        Mascota m = pi.mascota;
        Optional<Cliente> optC = repo.getClienteDeMascota(m.getCodigo());

        RegistroInmunizacion reg = new RegistroInmunizacion(
                null,
                "Vacunación",
                m.getCodigo(),
                m.getNombre(),
                m.getEspecie() + " · " + m.getRaza(),
                optC.map(Cliente::getNombreCompleto).orElse("Tutor"),
                optC.map(Cliente::getTelefonoPrincipal).orElse(""),
                (String) cbTipoVacuna.getSelectedItem(),
                txtLoteVacuna.getText().trim(),
                txtDosisVacuna.getText().trim(),
                LocalDate.now(),
                LocalDate.now().plusMonths(12),
                LocalDate.now().plusYears(1),
                txtVetVacuna.getText().trim(),
                m.getPesoActualKg(),
                txtObsVacuna.getText().trim(),
                "Aplicada"
        );

        repo.guardarInmunizacion(reg);
        JOptionPane.showMessageDialog(this,
                "Vacuna registrada exitosamente para " + m.getNombre() + ".\n" +
                "Refuerzo programado para el " + reg.getProximaFechaFormateada(),
                "Vacunación Registrada", JOptionPane.INFORMATION_MESSAGE);

        recargarDatos();
    }

    private void guardarDesparasitacion() {
        PacienteItem pi = (PacienteItem) cbPacienteDesp.getSelectedItem();
        if (pi == null || pi.mascota == null) return;
        Mascota m = pi.mascota;
        Optional<Cliente> optC = repo.getClienteDeMascota(m.getCodigo());

        double peso = m.getPesoActualKg();
        try { peso = Double.parseDouble(txtPesoDesp.getText().trim()); } catch (Exception ignored) {}

        RegistroInmunizacion reg = new RegistroInmunizacion(
                null,
                (String) cbTipoDesp.getSelectedItem(),
                m.getCodigo(),
                m.getNombre(),
                m.getEspecie() + " · " + m.getRaza(),
                optC.map(Cliente::getNombreCompleto).orElse("Tutor"),
                optC.map(Cliente::getTelefonoPrincipal).orElse(""),
                (String) cbProductoDesp.getSelectedItem(),
                "Lote REG-" + LocalDate.now().getYear(),
                txtDosisDesp.getText().trim(),
                LocalDate.now(),
                LocalDate.now().plusMonths(18),
                LocalDate.now().plusMonths(3),
                txtVetDesp.getText().trim(),
                peso,
                "Control antiparasitario preventivo periódico.",
                "Aplicada"
        );

        repo.guardarInmunizacion(reg);
        JOptionPane.showMessageDialog(this,
                "Desparasitación registrada exitosamente para " + m.getNombre() + ".\n" +
                "Próxima aplicación sugerida: " + reg.getProximaFechaFormateada(),
                "Desparasitación Registrada", JOptionPane.INFORMATION_MESSAGE);

        recargarDatos();
    }

    private void notificarTutorSeleccionado() {
        int row = tablaHistorial.getSelectedRow();
        if (row < 0 || row >= listaActual.size()) {
            JOptionPane.showMessageDialog(this, "Seleccione un registro del historial para notificar al tutor.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        RegistroInmunizacion r = listaActual.get(row);

        String msg = "Hola " + r.getNombreTutor() + ", le recordamos de Veterinaria Happy Pets que la próxima fecha de " +
                     r.getTipoControl() + " (" + r.getProducto() + ") para su mascota " + r.getNombreMascota() +
                     " está programada para el " + r.getProximaFechaFormateada() + ". Responda para agendar su turno.";

        JOptionPane.showMessageDialog(this,
                "Recordatorio enviado con éxito por WhatsApp a " + r.getNombreTutor() + " (" + r.getTelefonoTutor() + "):\n\n\"" + msg + "\"",
                "Notificación de Refuerzo Enviada", JOptionPane.INFORMATION_MESSAGE);
    }

    private void notificarProximasDosis() {
        JOptionPane.showMessageDialog(this,
                "Se han procesado las notificaciones automáticas vía WhatsApp y SMS para 2 tutores con refuerzos programados en los próximos 30 días.\n" +
                "• Carlos Eduardo Morales (Toby - Antirrábica)\n• Carlos Eduardo Morales (Rocky - Simparica Trio)",
                "Notificación Masiva Preventiva", JOptionPane.INFORMATION_MESSAGE);
    }

    private void generarCarnetVacunal() {
        int row = tablaHistorial.getSelectedRow();
        if (row < 0 || row >= listaActual.size()) {
            JOptionPane.showMessageDialog(this, "Seleccione una mascota del historial para emitir su carnet sanitario.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        RegistroInmunizacion r = listaActual.get(row);

        String carnet = "╔════════════════════════════════════════════════════════════════╗\n" +
                        "║        VETERINARIA HAPPY PETS · CARNET SANITARIO               ║\n" +
                        "║           Control Oficial de Inmunizaciones                   ║\n" +
                        "╠════════════════════════════════════════════════════════════════╣\n" +
                        " Paciente: " + r.getNombreMascota() + " (" + r.getEspecieRaza() + ")\n" +
                        " Propietario: " + r.getNombreTutor() + "\n" +
                        "────────────────────────────────────────────────────────────────\n" +
                        " REGISTRO VIGENTE:\n" +
                        " • " + r.getTipoControl() + ": " + r.getProducto() + "\n" +
                        " • Fecha Aplicación: " + r.getFechaAplicacionFormateada() + "    Dosis: " + r.getDosis() + "\n" +
                        " • Lote / Laboratorio: " + r.getLaboratorioLote() + "\n" +
                        " • Médico Veterinario: " + r.getVeterinario() + "\n" +
                        " • PRÓXIMO REFUERZO: " + r.getProximaFechaFormateada() + "\n" +
                        "╚════════════════════════════════════════════════════════════════╝";

        JOptionPane.showMessageDialog(this, carnet, "Carnet Vacunal Digital - Happy Pets", JOptionPane.INFORMATION_MESSAGE);
    }

    private static class PacienteItem {
        final Mascota mascota;
        PacienteItem(Mascota mascota) { this.mascota = mascota; }
        @Override
        public String toString() {
            return mascota != null ? mascota.getCodigo() + " - " + mascota.getNombre() + " (" + mascota.getEspecie() + " · " + mascota.getRaza() + ")" : "-";
        }
    }

    private static class BadgeEstadoInmunizacionRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            lbl.setHorizontalAlignment(SwingConstants.CENTER);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
            String st = String.valueOf(value);
            if (!isSelected) {
                if ("Aplicada".equalsIgnoreCase(st)) {
                    lbl.setForeground(new Color(22, 163, 74));
                } else if ("Próxima".equalsIgnoreCase(st)) {
                    lbl.setForeground(new Color(217, 119, 6));
                } else if ("Pendiente".equalsIgnoreCase(st)) {
                    lbl.setForeground(new Color(220, 38, 38));
                }
            }
            return lbl;
        }
    }
}
