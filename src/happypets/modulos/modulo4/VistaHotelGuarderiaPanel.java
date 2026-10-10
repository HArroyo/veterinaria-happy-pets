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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import happypets.data.RepositorioVeterinaria;
import happypets.model.Cliente;
import happypets.model.Mascota;
import happypets.model.ReservaHospedaje;
import happypets.ui.Iconos;
import happypets.ui.Ui;

/**
 * Submódulo 4.3: Hotel y Guardería Canina y Felina.
 * Diseñado según el wireframe oficial y especificaciones ERP Happy Pets:
 * - Control de ocupación de suites individuales climatizadas.
 * - Registro de estadías, noches automáticas y check-in / check-out.
 * - Plan de alimentación personalizado y cronograma de paseos recreativos.
 * - Reportes diarios a tutores vía WhatsApp y generación de contrato de hospedaje.
 */
public class VistaHotelGuarderiaPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private static final Color COLOR_BORDE = Ui.BORDE_SUAVE;
    private static final Color COLOR_AZUL_PRIMARIO = Ui.TURQUESA;
    private static final Color COLOR_TEXTO_TITULO = new Color(30, 41, 59);
    private static final Color COLOR_TEXTO_MUTED = new Color(100, 116, 139);

    private final RepositorioVeterinaria repo = RepositorioVeterinaria.getInstancia();

    // KPIs
    private JLabel lblKpiOcupadas;
    private JLabel lblKpiCheckinHoy;
    private JLabel lblKpiCheckoutHoy;
    private JLabel lblKpiFacturacionNoches;

    // Formulario de Check-in
    private JComboBox<PacienteItem> cbPaciente;
    private JTextField txtTutor;
    private JTextField txtEmergencia;
    private JComboBox<String> cbSuite;
    private JTextField txtFechaIngreso;
    private JTextField txtFechaSalida;
    private JTextField txtCostoNoche;
    private JTextField txtDieta;
    private JComboBox<String> cbPaseos;
    private JCheckBox chkBanoSalida;
    private JTextField txtMedicacion;

    // Tabla de Huéspedes
    private JTable tablaHospedaje;
    private DefaultTableModel modeloHospedaje;
    private JLabel lblContadorHospedaje;
    private JComboBox<String> cbFiltroEstado;
    private List<ReservaHospedaje> listaActual;

    public VistaHotelGuarderiaPanel() {
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

        // 3. Doble Columna: Formulario de Hospedaje y Monitor de Huéspedes
        contenido.add(crearDobleColumna());

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

        JLabel titulo = new JLabel("Hotel & Guardería Canina / Felina");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 17));
        titulo.setForeground(COLOR_TEXTO_TITULO);

        JLabel subtitulo = new JLabel("Módulo 4.3 · Suites climatizadas, estancias prolongadas, nutrición personalizada, paseos y check-out");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitulo.setForeground(COLOR_TEXTO_MUTED);

        izq.add(titulo);
        izq.add(Box.createVerticalStrut(2));
        izq.add(subtitulo);
        cab.add(izq, BorderLayout.CENTER);

        JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        der.setOpaque(false);

        JLabel badgeCapacidad = new JLabel(" Capacidad Total: 8 Suites Climatizadas ", SwingConstants.CENTER);
        badgeCapacidad.setFont(new Font("Segoe UI", Font.BOLD, 11));
        badgeCapacidad.setForeground(Ui.TURQUESA_PROFUNDO);
        badgeCapacidad.setOpaque(true);
        badgeCapacidad.setBackground(Ui.TURQUESA_SUAVE);
        badgeCapacidad.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Ui.TURQUESA_MEDIO, 1, true),
                new EmptyBorder(4, 10, 4, 10)
        ));
        der.add(badgeCapacidad);

        cab.add(der, BorderLayout.EAST);
        return cab;
    }

    private JPanel crearFilaKpis() {
        JPanel kpis = new JPanel(new GridLayout(1, 4, 10, 0));
        kpis.setOpaque(false);

        lblKpiOcupadas = new JLabel("0", SwingConstants.LEFT);
        lblKpiCheckinHoy = new JLabel("0", SwingConstants.LEFT);
        lblKpiCheckoutHoy = new JLabel("0", SwingConstants.LEFT);
        lblKpiFacturacionNoches = new JLabel("S/. 0", SwingConstants.LEFT);

        kpis.add(crearTarjetaKpi("Suites Ocupadas Hoy", lblKpiOcupadas, "Disponibles: 8 - Total", new Color(14, 165, 233), Iconos.crearIconoCasaMascota(18, new Color(14, 165, 233))));
        kpis.add(crearTarjetaKpi("Check-ins Agendados", lblKpiCheckinHoy, "Ingresos registrados", new Color(16, 185, 129), Iconos.crearIconoCheck(16, new Color(16, 185, 129))));
        kpis.add(crearTarjetaKpi("Check-outs Previstos", lblKpiCheckoutHoy, "Retiros hoy con tutor", new Color(245, 158, 11), Iconos.crearIconoReloj(16, new Color(245, 158, 11))));
        kpis.add(crearTarjetaKpi("Facturación Estimada", lblKpiFacturacionNoches, "Estancias en curso", new Color(99, 102, 241), Iconos.crearIconoDocumento(16, new Color(99, 102, 241))));

        return kpis;
    }

    private JPanel crearTarjetaKpi(String titulo, JLabel lblValor, String subtitulo, Color colorAcento, Icon icono) {
        JPanel card = new JPanel(new BorderLayout(8, 4)) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(COLOR_BORDE);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(8, 12, 8, 12));

        JPanel izq = new JPanel();
        izq.setOpaque(false);
        izq.setLayout(new BoxLayout(izq, BoxLayout.Y_AXIS));

        JLabel t = new JLabel(titulo);
        t.setFont(new Font("Segoe UI", Font.BOLD, 10));
        t.setForeground(COLOR_TEXTO_MUTED);

        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblValor.setForeground(COLOR_TEXTO_TITULO);

        JLabel sub = new JLabel(subtitulo);
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        sub.setForeground(COLOR_TEXTO_MUTED);

        izq.add(t);
        izq.add(Box.createVerticalStrut(2));
        izq.add(lblValor);
        izq.add(Box.createVerticalStrut(1));
        izq.add(sub);

        card.add(izq, BorderLayout.CENTER);

        if (icono != null) {
            JLabel lblIcono = new JLabel(icono);
            lblIcono.setVerticalAlignment(SwingConstants.TOP);
            card.add(lblIcono, BorderLayout.EAST);
        }

        return card;
    }

    private JPanel crearDobleColumna() {
        JPanel fila = new JPanel(new BorderLayout(14, 0));
        fila.setOpaque(false);

        // Columna Izquierda: Formulario Check-in (ancho 410px aprox)
        JPanel colIzquierda = crearPanelFormularioHospedaje();
        colIzquierda.setPreferredSize(new Dimension(420, 560));
        fila.add(colIzquierda, BorderLayout.WEST);

        // Columna Derecha: Monitor de Huéspedes
        JPanel colDerecha = crearPanelMonitorHuespedes();
        fila.add(colDerecha, BorderLayout.CENTER);

        return fila;
    }

    private JPanel crearPanelFormularioHospedaje() {
        JPanel card = new JPanel(new BorderLayout()) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(COLOR_BORDE);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(12, 14, 12, 14));

        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));

        // Título del formulario
        JLabel lblTitulo = new JLabel("Registrar Check-in / Nueva Reserva");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTitulo.setForeground(COLOR_TEXTO_TITULO);
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(lblTitulo);

        JLabel lblSub = new JLabel("Asignación de suite individual, dieta y plan de cuidados");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblSub.setForeground(COLOR_TEXTO_MUTED);
        lblSub.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(lblSub);
        form.add(Box.createVerticalStrut(10));

        // 1. Huésped / Paciente
        form.add(crearEtiquetaCampo("Paciente Huésped:"));
        cbPaciente = new JComboBox<>();
        estilizarControl(cbPaciente);
        cbPaciente.addActionListener(e -> autocompletarTutor());
        form.add(cbPaciente);
        form.add(Box.createVerticalStrut(6));

        // 2. Tutor & Teléfono Emergencia (en grid)
        JPanel gridTutor = new JPanel(new GridLayout(1, 2, 8, 0));
        gridTutor.setOpaque(false);
        gridTutor.setAlignmentX(Component.LEFT_ALIGNMENT);
        gridTutor.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));

        JPanel pTutor = new JPanel();
        pTutor.setOpaque(false);
        pTutor.setAlignmentX(Component.LEFT_ALIGNMENT);
        pTutor.setLayout(new BoxLayout(pTutor, BoxLayout.Y_AXIS));
        pTutor.add(crearEtiquetaCampo("Tutor Responsable:"));
        txtTutor = new JTextField();
        estilizarControl(txtTutor);
        txtTutor.setEditable(false);
        pTutor.add(txtTutor);
        gridTutor.add(pTutor);

        JPanel pEmerg = new JPanel();
        pEmerg.setOpaque(false);
        pEmerg.setAlignmentX(Component.LEFT_ALIGNMENT);
        pEmerg.setLayout(new BoxLayout(pEmerg, BoxLayout.Y_AXIS));
        pEmerg.add(crearEtiquetaCampo("Tel. Emergencia:"));
        txtEmergencia = new JTextField("+51 984 552 110");
        estilizarControl(txtEmergencia);
        pEmerg.add(txtEmergencia);
        gridTutor.add(pEmerg);

        form.add(gridTutor);
        form.add(Box.createVerticalStrut(6));

        // 3. Suite Asignada
        form.add(crearEtiquetaCampo("Suite Climatizada:"));
        cbSuite = new JComboBox<>(new String[]{
                "Suite 01 Canina (Grande - Jardín)",
                "Suite 02 Canina (Grande - Climatizada)",
                "Suite 03 Canina (Mediana - Interior)",
                "Suite 04 Canina (Mediana - Interior)",
                "Suite 05 Felina (Torre Rascador A)",
                "Suite 06 Felina (Torre Rascador B)",
                "Suite 07 Felina (Suite Tranquila)",
                "Suite 08 VIP Mixta (Cámaras 24h)"
        });
        estilizarControl(cbSuite);
        form.add(cbSuite);
        form.add(Box.createVerticalStrut(6));

        // 4. Fechas Ingreso / Salida y Costo por Noche
        JPanel gridFechas = new JPanel(new GridLayout(1, 3, 6, 0));
        gridFechas.setOpaque(false);
        gridFechas.setAlignmentX(Component.LEFT_ALIGNMENT);
        gridFechas.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));

        JPanel pIn = new JPanel();
        pIn.setOpaque(false);
        pIn.setAlignmentX(Component.LEFT_ALIGNMENT);
        pIn.setLayout(new BoxLayout(pIn, BoxLayout.Y_AXIS));
        pIn.add(crearEtiquetaCampo("Check-In:"));
        txtFechaIngreso = new JTextField(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        estilizarControl(txtFechaIngreso);
        pIn.add(txtFechaIngreso);
        gridFechas.add(pIn);

        JPanel pOut = new JPanel();
        pOut.setOpaque(false);
        pOut.setAlignmentX(Component.LEFT_ALIGNMENT);
        pOut.setLayout(new BoxLayout(pOut, BoxLayout.Y_AXIS));
        pOut.add(crearEtiquetaCampo("Check-Out:"));
        txtFechaSalida = new JTextField(LocalDate.now().plusDays(2).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        estilizarControl(txtFechaSalida);
        pOut.add(txtFechaSalida);
        gridFechas.add(pOut);

        JPanel pCosto = new JPanel();
        pCosto.setOpaque(false);
        pCosto.setAlignmentX(Component.LEFT_ALIGNMENT);
        pCosto.setLayout(new BoxLayout(pCosto, BoxLayout.Y_AXIS));
        pCosto.add(crearEtiquetaCampo("Tarifa Noche S/.:"));
        txtCostoNoche = new JTextField("55.00");
        estilizarControl(txtCostoNoche);
        pCosto.add(txtCostoNoche);
        gridFechas.add(pCosto);

        form.add(gridFechas);
        form.add(Box.createVerticalStrut(6));

        // 5. Alimentación y Paseos
        form.add(crearEtiquetaCampo("Dieta / Alimentación Específica:"));
        txtDieta = new JTextField("Ración ProPlan Adulto 150g 2v/día (Tutor trae alimento)");
        estilizarControl(txtDieta);
        form.add(txtDieta);
        form.add(Box.createVerticalStrut(6));

        form.add(crearEtiquetaCampo("Plan de Paseos y Socialización:"));
        cbPaseos = new JComboBox<>(new String[]{
                "3 Paseos al día · Parque interno y socialización",
                "2 Paseos al día · Paseo individual con correa",
                "Guardería Diurna · Juegos libres supervisados",
                "Zona Felina · Estimulación con juguetes y rascador"
        });
        estilizarControl(cbPaseos);
        form.add(cbPaseos);
        form.add(Box.createVerticalStrut(6));

        // 6. Medicación y Baño de Salida
        form.add(crearEtiquetaCampo("Medicación Especial / Cuidados:"));
        txtMedicacion = new JTextField("Ninguna medicación requerida");
        estilizarControl(txtMedicacion);
        form.add(txtMedicacion);
        form.add(Box.createVerticalStrut(6));

        chkBanoSalida = new JCheckBox("Incluir Baño y Deslanado de Salida (+ S/. 35.00)");
        chkBanoSalida.setFont(new Font("Segoe UI", Font.BOLD, 10));
        chkBanoSalida.setForeground(Ui.TURQUESA_OSCURO);
        chkBanoSalida.setOpaque(false);
        chkBanoSalida.setSelected(true);
        chkBanoSalida.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(chkBanoSalida);
        form.add(Box.createVerticalStrut(12));

        // Botones de acción
        JPanel pBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        pBotones.setOpaque(false);
        pBotones.setAlignmentX(Component.LEFT_ALIGNMENT);
        pBotones.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        JButton btnLimpiar = crearBoton("Limpiar", false, this::limpiarFormulario);
        JButton btnRegistrar = crearBoton("Registrar Estadía", true, this::registrarEstadia);

        pBotones.add(btnLimpiar);
        pBotones.add(btnRegistrar);
        form.add(pBotones);

        card.add(form, BorderLayout.CENTER);
        return card;
    }

    private JPanel crearPanelMonitorHuespedes() {
        JPanel card = new JPanel(new BorderLayout(0, 8)) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(COLOR_BORDE);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(12, 14, 12, 14));

        // Cabecera de la tabla con filtro
        JPanel top = new JPanel(new BorderLayout(8, 0));
        top.setOpaque(false);

        JPanel izq = new JPanel();
        izq.setOpaque(false);
        izq.setLayout(new BoxLayout(izq, BoxLayout.Y_AXIS));

        JLabel lblTit = new JLabel("Huéspedes en Hospedaje & Control de Estadías");
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTit.setForeground(COLOR_TEXTO_TITULO);

        lblContadorHospedaje = new JLabel("0 reservas activas");
        lblContadorHospedaje.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblContadorHospedaje.setForeground(COLOR_TEXTO_MUTED);

        izq.add(lblTit);
        izq.add(Box.createVerticalStrut(1));
        izq.add(lblContadorHospedaje);
        top.add(izq, BorderLayout.CENTER);

        JPanel derFiltro = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        derFiltro.setOpaque(false);

        JLabel lblFiltro = new JLabel("Estado:");
        lblFiltro.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblFiltro.setForeground(COLOR_TEXTO_MUTED);
        derFiltro.add(lblFiltro);

        cbFiltroEstado = new JComboBox<>(new String[]{"Todos", "En Estadía / Hospedado", "Confirmada", "Finalizada / Check-out"});
        estilizarControl(cbFiltroEstado);
        cbFiltroEstado.addActionListener(e -> filtrarTabla());
        derFiltro.add(cbFiltroEstado);

        top.add(derFiltro, BorderLayout.EAST);
        card.add(top, BorderLayout.NORTH);

        // Tabla
        String[] columnas = {"ID", "Suite", "Huésped", "Tutor", "Ingreso", "Salida", "Noches", "Costo Total", "Estado"};
        modeloHospedaje = new DefaultTableModel(columnas, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        tablaHospedaje = new JTable(modeloHospedaje);
        Ui.formatearTabla(tablaHospedaje, new int[]{0, 4, 5, 6, 8}, new int[]{7});

        tablaHospedaje.getColumnModel().getColumn(0).setPreferredWidth(65);
        tablaHospedaje.getColumnModel().getColumn(1).setPreferredWidth(100);
        tablaHospedaje.getColumnModel().getColumn(2).setPreferredWidth(90);
        tablaHospedaje.getColumnModel().getColumn(3).setPreferredWidth(110);
        tablaHospedaje.getColumnModel().getColumn(4).setPreferredWidth(75);
        tablaHospedaje.getColumnModel().getColumn(5).setPreferredWidth(75);
        tablaHospedaje.getColumnModel().getColumn(6).setPreferredWidth(45);
        tablaHospedaje.getColumnModel().getColumn(7).setPreferredWidth(75);
        tablaHospedaje.getColumnModel().getColumn(8).setPreferredWidth(125);

        // Renderizador de Estado con Badges
        tablaHospedaje.getColumnModel().getColumn(8).setCellRenderer(new DefaultTableCellRenderer() {
            private static final long serialVersionUID = 1L;
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                l.setHorizontalAlignment(SwingConstants.CENTER);
                String st = value != null ? value.toString() : "";
                if (st.contains("En Estadía") || st.contains("Hospedado")) {
                    l.setForeground(Ui.TURQUESA_PROFUNDO);
                    l.setBackground(Ui.TURQUESA_SUAVE);
                } else if (st.contains("Confirmada")) {
                    l.setForeground(new Color(245, 158, 11));
                    l.setBackground(new Color(254, 243, 199));
                } else {
                    l.setForeground(Ui.COLOR_EXITO);
                    l.setBackground(new Color(209, 250, 229));
                }
                l.setOpaque(true);
                return l;
            }
        });

        JScrollPane scrollTabla = new JScrollPane(tablaHospedaje);
        scrollTabla.setBorder(BorderFactory.createLineBorder(COLOR_BORDE, 1));
        scrollTabla.getViewport().setBackground(Color.WHITE);
        card.add(scrollTabla, BorderLayout.CENTER);

        // Barra inferior de acciones rápidas
        JPanel pAcciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        pAcciones.setOpaque(false);

        JButton btnWhatsapp = crearBoton("Reporte Diario WhatsApp", false, this::enviarReporteWhatsapp);
        btnWhatsapp.setIcon(Iconos.crearIconoMensaje(14, new Color(16, 185, 129)));

        JButton btnContrato = crearBoton("Contrato Hospedaje", false, this::mostrarContratoHospedaje);
        btnContrato.setIcon(Iconos.crearIconoDocumento(14, COLOR_AZUL_PRIMARIO));

        JButton btnCheckout = crearBoton("Registrar Check-Out", true, this::realizarCheckout);
        btnCheckout.setIcon(Iconos.crearIconoCheck(14, Color.WHITE));

        pAcciones.add(btnWhatsapp);
        pAcciones.add(btnContrato);
        pAcciones.add(btnCheckout);

        card.add(pAcciones, BorderLayout.SOUTH);
        return card;
    }

    private JLabel crearEtiquetaCampo(String texto) {
        JLabel l = new JLabel(texto, SwingConstants.LEFT);
        l.setFont(new Font("Segoe UI", Font.BOLD, 10));
        l.setForeground(new Color(71, 85, 105));
        l.setBorder(new EmptyBorder(0, 0, 2, 0));
        l.setHorizontalAlignment(SwingConstants.LEFT);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        l.setMaximumSize(new Dimension(Integer.MAX_VALUE, 18));
        return l;
    }

    private void estilizarControl(Component c) {
        c.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        c.setPreferredSize(new Dimension(c.getPreferredSize().width, 27));
        c.setMaximumSize(new Dimension(Integer.MAX_VALUE, 27));
        if (c instanceof javax.swing.JComponent jc) {
            jc.setAlignmentX(Component.LEFT_ALIGNMENT);
        }
        if (c instanceof JTextField) {
            ((JTextField) c).setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(COLOR_BORDE, 1, true),
                    new EmptyBorder(2, 6, 2, 6)
            ));
        } else if (c instanceof JComboBox) {
            c.setBackground(Color.WHITE);
        }
    }

    private JButton crearBoton(String texto, boolean primario, Runnable accion) {
        JButton btn = new JButton(texto) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (primario) {
                    g2.setColor(getModel().isRollover() ? Ui.TURQUESA_OSCURO : COLOR_AZUL_PRIMARIO);
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

            @Override
            public Dimension getPreferredSize() {
                Dimension d = super.getPreferredSize();
                return new Dimension(d.width + 16, 28);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btn.setForeground(primario ? Color.WHITE : new Color(51, 65, 85));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(5, 12, 5, 12));
        btn.addActionListener(e -> accion.run());
        return btn;
    }

    public void recargarDatos() {
        if (cbPaciente.getItemCount() == 0) {
            for (Mascota m : repo.todasLasMascotas()) {
                cbPaciente.addItem(new PacienteItem(m));
            }
            autocompletarTutor();
        }

        listaActual = repo.getReservasHospedaje();
        filtrarTabla();

        // KPIs
        long ocupadas = listaActual.stream().filter(r -> r.getEstado().contains("En Estadía") || r.getEstado().contains("Hospedado")).count();
        long inHoy = listaActual.stream().filter(r -> r.getFechaCheckIn().equals(LocalDate.now())).count();
        long outHoy = listaActual.stream().filter(r -> r.getFechaCheckOut().equals(LocalDate.now())).count();
        double facturacion = listaActual.stream().mapToDouble(ReservaHospedaje::getCostoTotal).sum();

        lblKpiOcupadas.setText(String.valueOf(ocupadas));
        lblKpiCheckinHoy.setText(String.valueOf(inHoy));
        lblKpiCheckoutHoy.setText(String.valueOf(outHoy));
        lblKpiFacturacionNoches.setText("S/. " + String.format("%.0f", facturacion));
    }

    private void filtrarTabla() {
        if (listaActual == null) return;
        modeloHospedaje.setRowCount(0);
        String filtro = cbFiltroEstado != null ? (String) cbFiltroEstado.getSelectedItem() : "Todos";

        int cont = 0;
        for (ReservaHospedaje r : listaActual) {
            if (!"Todos".equalsIgnoreCase(filtro)) {
                if (!r.getEstado().equalsIgnoreCase(filtro)) continue;
            }
            cont++;
            modeloHospedaje.addRow(new Object[]{
                    r.getIdReserva(),
                    r.getNumeroSuite(),
                    r.getNombreMascota(),
                    r.getNombreTutor(),
                    r.getFechaCheckInFormateada(),
                    r.getFechaCheckOutFormateada(),
                    r.getNumeroNoches(),
                    "S/. " + String.format("%.2f", r.getCostoTotal()),
                    r.getEstado()
            });
        }
        lblContadorHospedaje.setText(cont + " reservas mostradas");
    }

    private void autocompletarTutor() {
        PacienteItem item = (PacienteItem) cbPaciente.getSelectedItem();
        if (item != null && item.mascota != null) {
            repo.getClienteDeMascota(item.mascota.getCodigo()).ifPresent(c -> {
                txtTutor.setText(c.getNombreCompleto());
                txtEmergencia.setText(c.getTelefonoPrincipal());
            });
        }
    }

    private void registrarEstadia() {
        PacienteItem pi = (PacienteItem) cbPaciente.getSelectedItem();
        if (pi == null || pi.mascota == null) {
            JOptionPane.showMessageDialog(this, "Seleccione una mascota para el hospedaje.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        LocalDate fechaIn = LocalDate.now();
        LocalDate fechaOut = LocalDate.now().plusDays(2);
        try {
            fechaIn = LocalDate.parse(txtFechaIngreso.getText().trim(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            fechaOut = LocalDate.parse(txtFechaSalida.getText().trim(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Formato de fechas incorrecto. Use dd/MM/yyyy.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int noches = (int) ChronoUnit.DAYS.between(fechaIn, fechaOut);
        if (noches <= 0) noches = 1;

        double tarifaNoche = 55.0;
        try {
            tarifaNoche = Double.parseDouble(txtCostoNoche.getText().trim());
        } catch (Exception ignored) {}

        double costoTotal = (noches * tarifaNoche) + (chkBanoSalida.isSelected() ? 35.0 : 0.0);

        ReservaHospedaje r = new ReservaHospedaje(
                null,
                (String) cbSuite.getSelectedItem(),
                pi.mascota.getCodigo(),
                pi.mascota.getNombre(),
                pi.mascota.getEspecie() + " · " + pi.mascota.getRaza(),
                txtTutor.getText().trim(),
                txtEmergencia.getText().trim(),
                txtEmergencia.getText().trim(),
                fechaIn,
                fechaOut,
                noches,
                txtDieta.getText().trim(),
                (String) cbPaseos.getSelectedItem(),
                chkBanoSalida.isSelected(),
                txtMedicacion.getText().trim(),
                tarifaNoche,
                costoTotal,
                "En Estadía / Hospedado"
        );

        repo.guardarReservaHospedaje(r);
        recargarDatos();

        JOptionPane.showMessageDialog(this,
                "¡Check-in realizado con éxito!\n\n" +
                        "Suite asignada: " + r.getNumeroSuite() + "\n" +
                        "Huésped: " + r.getNombreMascota() + "\n" +
                        "Estadía: " + r.getNumeroNoches() + " noches (" + r.getFechaCheckInFormateada() + " al " + r.getFechaCheckOutFormateada() + ")\n" +
                        "Total Estimado: S/. " + String.format("%.2f", r.getCostoTotal()),
                "Check-in Confirmado", JOptionPane.INFORMATION_MESSAGE);
    }

    private void realizarCheckout() {
        int fila = tablaHospedaje.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un huésped de la tabla para registrar su Check-out.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String idReserva = (String) modeloHospedaje.getValueAt(fila, 0);
        Optional<ReservaHospedaje> opt = repo.getReservasHospedaje().stream().filter(r -> r.getIdReserva().equalsIgnoreCase(idReserva)).findFirst();
        if (!opt.isPresent()) return;

        ReservaHospedaje r = opt.get();
        if ("Finalizada / Check-out".equalsIgnoreCase(r.getEstado())) {
            JOptionPane.showMessageDialog(this, "Esta reserva ya fue finalizada anteriormente.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int conf = JOptionPane.showConfirmDialog(this,
                "¿Desea confirmar el Check-out de " + r.getNombreMascota() + "?\n\n" +
                        "Suite: " + r.getNumeroSuite() + "\n" +
                        "Tutor: " + r.getNombreTutor() + "\n" +
                        "Total a liquidar: S/. " + String.format("%.2f", r.getCostoTotal()) + "\n" +
                        (r.isBanoSalida() ? "✓ Baño de salida completado e incluido" : "Sin baño de salida"),
                "Confirmar Check-out", JOptionPane.YES_NO_OPTION);

        if (conf == JOptionPane.YES_OPTION) {
            repo.actualizarEstadoHospedaje(r.getIdReserva(), "Finalizada / Check-out");
            recargarDatos();
            JOptionPane.showMessageDialog(this, "Check-out completado. La suite " + r.getNumeroSuite() + " ahora está disponible para desinfección y nuevos ingresos.", "Check-out Exitoso", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void enviarReporteWhatsapp() {
        int fila = tablaHospedaje.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un huésped para generar el reporte de bienestar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String idReserva = (String) modeloHospedaje.getValueAt(fila, 0);
        Optional<ReservaHospedaje> opt = repo.getReservasHospedaje().stream().filter(r -> r.getIdReserva().equalsIgnoreCase(idReserva)).findFirst();
        if (!opt.isPresent()) return;

        ReservaHospedaje r = opt.get();
        String reporte = "🐾 *HAPPY PETS HOTEL & RESORT* 🏨\n" +
                "====================================\n" +
                "¡Hola *" + r.getNombreTutor() + "*! Te enviamos el reporte diario de tu consentido:\n\n" +
                "🐶 *Huésped:* " + r.getNombreMascota() + " (" + r.getEspecieRaza() + ")\n" +
                "🏡 *Suite:* " + r.getNumeroSuite() + "\n" +
                "🥗 *Alimentación:* " + r.getTipoAlimentacion() + " (Ración completa consumida ✓)\n" +
                "🎾 *Recreación:* " + r.getPlanPaseos() + " (Comportamiento sociable y alegre)\n" +
                "💊 *Atención Médica:* " + r.getMedicacionEspecial() + "\n" +
                "📅 *Estadía:* " + r.getFechaCheckInFormateada() + " al " + r.getFechaCheckOutFormateada() + "\n\n" +
                "✨ *Estado:* Se encuentra tranquilo, muy bien adaptado y recibiendo mucho cariño de nuestro equipo.\n" +
                "📸 *Fotos y videos:* Enviados adjuntos al chat.\n" +
                "====================================\n" +
                "Clínica & Hotel Happy Pets · Vigilancia y Cuidados 24/7";

        JTextArea ta = new JTextArea(reporte);
        ta.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        ta.setEditable(false);
        ta.setLineWrap(true);
        ta.setWrapStyleWord(true);
        JScrollPane sp = new JScrollPane(ta);
        sp.setPreferredSize(new Dimension(460, 260));

        JOptionPane.showMessageDialog(this, sp, "Reporte Diario de Estadía para WhatsApp", JOptionPane.INFORMATION_MESSAGE);
    }

    private void mostrarContratoHospedaje() {
        int fila = tablaHospedaje.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un huésped para visualizar su Contrato de Hospedaje.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String idReserva = (String) modeloHospedaje.getValueAt(fila, 0);
        Optional<ReservaHospedaje> opt = repo.getReservasHospedaje().stream().filter(r -> r.getIdReserva().equalsIgnoreCase(idReserva)).findFirst();
        if (!opt.isPresent()) return;

        ReservaHospedaje r = opt.get();
        String contrato = "CONTRATO DE PRESTACIÓN DE SERVICIOS DE HOSPEDAJE Y CUIDADO INTEGRAL\n" +
                "CLÍNICA VETERINARIA & HOTEL HAPPY PETS S.A.C.\n" +
                "=========================================================================\n\n" +
                "CÓDIGO DE RESERVA: " + r.getIdReserva() + "\n" +
                "FECHA DE INGRESO : " + r.getFechaCheckInFormateada() + "  |  FECHA DE SALIDA PREVISTA: " + r.getFechaCheckOutFormateada() + "\n" +
                "SUITE ASIGNADA   : " + r.getNumeroSuite() + "\n" +
                "HUÉSPED          : " + r.getNombreMascota() + " (" + r.getEspecieRaza() + ")\n" +
                "TUTOR TITULAR    : " + r.getNombreTutor() + "  |  TEL. CONTACTO: " + r.getTelefonoTutor() + "\n" +
                "TEL. EMERGENCIA  : " + r.getTelefonoEmergencia() + "\n\n" +
                "CLÁUSULAS GENERALES:\n" +
                "1. ALIMENTACIÓN Y SALUD: El establecimiento se compromete a suministrar la dieta indicada (" + r.getTipoAlimentacion() + ").\n" +
                "2. PASEOS: Se ejecutará el cronograma (" + r.getPlanPaseos() + ") en áreas cercadas y seguras.\n" +
                "3. AUTORIZACIÓN MÉDICA: En caso de urgencia o descompensación, el tutor autoriza al equipo de guardia veterinaria a prestar atención médica inmediata.\n" +
                "4. BAÑO DE SALIDA: " + (r.isBanoSalida() ? "INCLUIDO Y PROGRAMADO PREVIO A ENTREGA." : "NO SOLICITADO POR EL TUTOR.") + "\n" +
                "5. TARIFA: S/. " + String.format("%.2f", r.getCostoNoche()) + " por noche. COSTO TOTAL ACORDADO: S/. " + String.format("%.2f", r.getCostoTotal()) + ".\n\n" +
                "__________________________             __________________________\n" +
                "     Firma del Tutor                        Administración Happy Pets\n" +
                "  DNI: _________________                     Hotel Canino & Felino\n";

        Ui.mostrarVisorReporte(
                javax.swing.SwingUtilities.getWindowAncestor(this),
                "Contrato de Hospedaje - " + r.getIdReserva(),
                "CONTRATO DE PRESTACIÓN DE SERVICIOS DE HOSPEDAJE",
                contrato,
                "Contrato_Hospedaje_" + r.getIdReserva()
        );
    }

    private void limpiarFormulario() {
        txtDieta.setText("Ración balanceada 2v/día");
        txtMedicacion.setText("Ninguna");
        chkBanoSalida.setSelected(false);
        txtFechaIngreso.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        txtFechaSalida.setText(LocalDate.now().plusDays(2).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
    }

    // Helper para Combo de Paciente
    private static class PacienteItem {
        final Mascota mascota;

        PacienteItem(Mascota m) {
            this.mascota = m;
        }

        @Override
        public String toString() {
            if (mascota == null) return "Seleccione...";
            return mascota.getCodigo() + " - " + mascota.getNombre() + " (" + mascota.getEspecie() + " · " + mascota.getRaza() + ")";
        }
    }
}
