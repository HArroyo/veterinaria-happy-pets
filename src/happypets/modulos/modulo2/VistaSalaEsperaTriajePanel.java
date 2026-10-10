package happypets.modulos.modulo2;

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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalTime;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import happypets.data.RepositorioVeterinaria;
import happypets.model.Cita;
import happypets.model.Cliente;
import happypets.model.Mascota;
import happypets.model.PacienteTriaje;
import happypets.ui.Iconos;
import happypets.ui.Ui;

/**
 * Submódulo 2.4: Monitor de Sala de Espera y Triaje Clínico de Urgencias.
 */
public class VistaSalaEsperaTriajePanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private static final Color COLOR_BORDE = Ui.BORDE_SUAVE;
    private static final Color COLOR_AZUL_PRIMARIO = Ui.TURQUESA;
    private static final Color COLOR_TEXTO_TITULO = Ui.TEXTO_TITULO;
    private static final Color COLOR_TEXTO_MUTED = Ui.TEXTO_MUTED;

    private final RepositorioVeterinaria repo = RepositorioVeterinaria.getInstancia();

    // Filtros
    private JComboBox<String> cbFiltroTriaje;
    private JComboBox<String> cbFiltroEstado;
    private JTextField txtBuscar;

    // KPIs
    private JLabel lblKpiEnEspera;
    private JLabel lblKpiEnConsulta;
    private JLabel lblKpiTiempoMedio;
    private JLabel lblKpiCriticos;

    // Formulario de Triaje
    private JComboBox<CitaOTurnoItem> cbPacienteCita;
    private JTextField txtNombreMascota;
    private JTextField txtEspecieRaza;
    private JTextField txtPropietario;
    private JTextField txtTelefono;
    private JTextField txtPeso;
    private JTextField txtTemperatura;
    private JTextField txtFrecuenciaCardiaca;
    private JRadioButton rbVerde;
    private JRadioButton rbAmarillo;
    private JRadioButton rbRojo;
    private JComboBox<String> cbConsultorio;
    private JTextField txtMotivoTriaje;

    // Tabla Monitor
    private JTable tablaMonitor;
    private DefaultTableModel modeloMonitor;
    private JLabel lblContador;
    private List<PacienteTriaje> listaActual;

    public VistaSalaEsperaTriajePanel() {
        setLayout(new BorderLayout());
        setOpaque(false);

        JPanel contenido = new JPanel();
        contenido.setOpaque(false);
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setBorder(new EmptyBorder(12, 18, 14, 18));

        // 1. Cabecera
        contenido.add(crearCabeceraVista());
        contenido.add(Box.createVerticalStrut(8));

        // 2. Barra de filtros
        contenido.add(crearBarraFiltros());
        contenido.add(Box.createVerticalStrut(10));

        // 3. Fila de 4 KPIs
        contenido.add(crearFilaKpi());
        contenido.add(Box.createVerticalStrut(10));

        // 4. Doble columna: Formulario de Triaje y Monitor en Vivo
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
        cab.setPreferredSize(new Dimension(0, 40));
        cab.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JPanel izq = new JPanel();
        izq.setOpaque(false);
        izq.setLayout(new BoxLayout(izq, BoxLayout.Y_AXIS));

        JLabel lblTit = new JLabel("Sala de Espera y Triaje de Urgencias");
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblTit.setForeground(COLOR_TEXTO_TITULO);

        JLabel lblSub = new JLabel("Módulo 2.4 · Monitor en vivo de pacientes en sala, toma de signos vitales y clasificación Manchester");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblSub.setForeground(COLOR_TEXTO_MUTED);

        izq.add(lblTit);
        izq.add(Box.createVerticalStrut(2));
        izq.add(lblSub);
        cab.add(izq, BorderLayout.CENTER);

        JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 2));
        der.setOpaque(false);

        JButton btnLlamarSiguiente = crearBotonAccion("Llamar Siguiente a Consulta", true);
        btnLlamarSiguiente.setIcon(Iconos.crearIconoReloj(12, Color.WHITE));
        btnLlamarSiguiente.setIconTextGap(4);
        btnLlamarSiguiente.addActionListener(e -> llamarSiguienteTurno());

        der.add(btnLlamarSiguiente);
        cab.add(der, BorderLayout.EAST);

        return cab;
    }

    private JPanel crearBarraFiltros() {
        JPanel barra = new JPanel(new BorderLayout(10, 0)) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(COLOR_BORDE);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        barra.setOpaque(false);
        barra.setBorder(new EmptyBorder(3, 10, 3, 10));
        barra.setPreferredSize(new Dimension(0, 38));
        barra.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

        JPanel izq = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
        izq.setOpaque(false);

        JLabel lblTriaje = new JLabel("Nivel Triaje:");
        lblTriaje.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblTriaje.setForeground(COLOR_TEXTO_MUTED);
        izq.add(lblTriaje);

        cbFiltroTriaje = new JComboBox<>(new String[]{"Todos los niveles", "VERDE (Normal)", "AMARILLO (Urgencia)", "ROJO (Emergencia crítica)"});
        cbFiltroTriaje.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        cbFiltroTriaje.setPreferredSize(new Dimension(170, 26));
        cbFiltroTriaje.setBackground(Color.WHITE);
        cbFiltroTriaje.addActionListener(e -> recargarDatos());
        izq.add(cbFiltroTriaje);

        JLabel lblEst = new JLabel("Estado:");
        lblEst.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblEst.setForeground(COLOR_TEXTO_MUTED);
        izq.add(lblEst);

        cbFiltroEstado = new JComboBox<>(new String[]{"Todos", "En Espera", "En Consulta", "Atendido"});
        cbFiltroEstado.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        cbFiltroEstado.setPreferredSize(new Dimension(110, 26));
        cbFiltroEstado.setBackground(Color.WHITE);
        cbFiltroEstado.addActionListener(e -> recargarDatos());
        izq.add(cbFiltroEstado);

        barra.add(izq, BorderLayout.WEST);

        JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 2));
        der.setOpaque(false);

        JLabel lblB = new JLabel("Buscar:");
        lblB.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblB.setForeground(COLOR_TEXTO_MUTED);
        der.add(lblB);

        txtBuscar = new JTextField(12);
        txtBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        txtBuscar.setPreferredSize(new Dimension(130, 26));
        txtBuscar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225), 1),
                new EmptyBorder(2, 6, 2, 6)
        ));
        java.awt.event.ActionListener accionB = e -> recargarDatos();
        txtBuscar.addActionListener(accionB);
        der.add(txtBuscar);

        JButton btnB = crearBotonAccion("Buscar", false);
        btnB.setIcon(Iconos.crearIconoBuscar(12, COLOR_AZUL_PRIMARIO));
        btnB.setIconTextGap(4);
        btnB.addActionListener(accionB);
        der.add(btnB);

        barra.add(der, BorderLayout.EAST);
        return barra;
    }

    private JPanel crearFilaKpi() {
        JPanel fila = new JPanel(new GridLayout(1, 4, 10, 0));
        fila.setOpaque(false);

        lblKpiEnEspera = new JLabel("0");
        fila.add(crearCardKpi(lblKpiEnEspera, "PACIENTES EN SALA", new Color(254, 243, 199), Iconos.crearIconoReloj(20, new Color(217, 119, 6))));

        lblKpiEnConsulta = new JLabel("0");
        fila.add(crearCardKpi(lblKpiEnConsulta, "EN CONSULTORIO", Ui.TURQUESA_SUAVE, Iconos.crearIconoEstetoscopio(20, COLOR_AZUL_PRIMARIO)));

        lblKpiTiempoMedio = new JLabel("11 min");
        fila.add(crearCardKpi(lblKpiTiempoMedio, "TIEMPO PROMEDIO ESPERA", new Color(220, 252, 231), Iconos.crearIconoCheck(20, new Color(22, 163, 74))));

        lblKpiCriticos = new JLabel("0");
        fila.add(crearCardKpi(lblKpiCriticos, "CASOS CRÍTICOS / ROJO", new Color(254, 226, 226), Iconos.crearIconoAlertaTriaje(20, new Color(220, 38, 38))));

        return fila;
    }

    private JPanel crearCardKpi(JLabel lblVal, String etiqueta, Color bgIco, Icon icono) {
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

        JLabel badge = new JLabel(icono, SwingConstants.CENTER) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bgIco);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        badge.setPreferredSize(new Dimension(36, 36));
        card.add(badge, BorderLayout.WEST);

        JPanel pText = new JPanel();
        pText.setOpaque(false);
        pText.setLayout(new BoxLayout(pText, BoxLayout.Y_AXIS));

        JLabel lblEtq = new JLabel(etiqueta);
        lblEtq.setFont(new Font("Segoe UI", Font.BOLD, 9));
        lblEtq.setForeground(COLOR_TEXTO_MUTED);

        lblVal.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblVal.setForeground(COLOR_TEXTO_TITULO);

        pText.add(lblEtq);
        pText.add(Box.createVerticalStrut(2));
        pText.add(lblVal);
        card.add(pText, BorderLayout.CENTER);

        return card;
    }

    private JPanel crearDobleColumna() {
        JPanel grid = new JPanel(new GridLayout(1, 2, 12, 0));
        grid.setOpaque(false);

        grid.add(crearTarjetaFormularioTriaje());
        grid.add(crearTarjetaMonitorSala());

        return grid;
    }

    private JPanel crearTarjetaFormularioTriaje() {
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

        JLabel lblTit = new JLabel("Recepción y Clasificación de Triaje");
        lblTit.setIcon(Iconos.crearIconoAlertaTriaje(15, COLOR_AZUL_PRIMARIO));
        lblTit.setIconTextGap(6);
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTit.setForeground(COLOR_TEXTO_TITULO);
        card.add(lblTit, BorderLayout.NORTH);

        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));

        // 1. Selector de cita previa o ingreso directo
        cbPacienteCita = new JComboBox<>();
        cbPacienteCita.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        cbPacienteCita.setBackground(Color.WHITE);
        cbPacienteCita.setPreferredSize(new Dimension(0, 26));
        cbPacienteCita.addActionListener(e -> autollenarDatosDesdeCita());
        form.add(crearFilaCampo("Cita Previa Programada (Opcional)", cbPacienteCita));
        form.add(Box.createVerticalStrut(6));

        // 2. Datos de Paciente y Dueño
        txtNombreMascota = crearCampoTextoCompacto();
        txtEspecieRaza = crearCampoTextoCompacto();

        JPanel f1 = new JPanel(new GridLayout(1, 2, 8, 0));
        f1.setOpaque(false);
        f1.setAlignmentX(Component.LEFT_ALIGNMENT);
        f1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        f1.add(crearFilaCampo("Nombre de la Mascota *", txtNombreMascota));
        f1.add(crearFilaCampo("Especie y Raza *", txtEspecieRaza));
        form.add(f1);
        form.add(Box.createVerticalStrut(6));

        txtPropietario = crearCampoTextoCompacto();
        txtTelefono = crearCampoTextoCompacto();

        JPanel f2 = new JPanel(new GridLayout(1, 2, 8, 0));
        f2.setOpaque(false);
        f2.setAlignmentX(Component.LEFT_ALIGNMENT);
        f2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        f2.add(crearFilaCampo("Propietario Responsable *", txtPropietario));
        f2.add(crearFilaCampo("Teléfono de Contacto", txtTelefono));
        form.add(f2);
        form.add(Box.createVerticalStrut(6));

        // 3. Constantes Vitales
        txtPeso = crearCampoTextoCompacto();
        txtPeso.setText("15.0");
        txtTemperatura = crearCampoTextoCompacto();
        txtTemperatura.setText("38.5");
        txtFrecuenciaCardiaca = crearCampoTextoCompacto();
        txtFrecuenciaCardiaca.setText("95");

        JPanel fConstantes = new JPanel(new GridLayout(1, 3, 8, 0));
        fConstantes.setOpaque(false);
        fConstantes.setAlignmentX(Component.LEFT_ALIGNMENT);
        fConstantes.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        fConstantes.add(crearFilaCampo("Peso (kg) *", txtPeso));
        fConstantes.add(crearFilaCampo("Temp (°C) *", txtTemperatura));
        fConstantes.add(crearFilaCampo("FC (ppm) *", txtFrecuenciaCardiaca));
        form.add(fConstantes);
        form.add(Box.createVerticalStrut(6));

        // 4. Clasificación Manchester
        JPanel pTriajeRadios = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        pTriajeRadios.setOpaque(false);

        rbVerde = new JRadioButton("Verde (Normal)", true);
        rbVerde.setFont(new Font("Segoe UI", Font.BOLD, 10));
        rbVerde.setForeground(new Color(22, 163, 74));
        rbVerde.setOpaque(false);

        rbAmarillo = new JRadioButton("Amarillo (Urgente)");
        rbAmarillo.setFont(new Font("Segoe UI", Font.BOLD, 10));
        rbAmarillo.setForeground(new Color(217, 119, 6));
        rbAmarillo.setOpaque(false);

        rbRojo = new JRadioButton("Rojo (Emergencia)");
        rbRojo.setFont(new Font("Segoe UI", Font.BOLD, 10));
        rbRojo.setForeground(new Color(220, 38, 38));
        rbRojo.setOpaque(false);

        ButtonGroup bgT = new ButtonGroup();
        bgT.add(rbVerde);
        bgT.add(rbAmarillo);
        bgT.add(rbRojo);

        pTriajeRadios.add(rbVerde);
        pTriajeRadios.add(rbAmarillo);
        pTriajeRadios.add(rbRojo);

        form.add(crearFilaCampo("Clasificación de Prioridad Clínica (Triaje Manchester) *", pTriajeRadios));
        form.add(Box.createVerticalStrut(6));

        // 5. Consultorio y Motivo
        cbConsultorio = new JComboBox<>(new String[]{
                "Consultorio 1 - Dr. Mendoza",
                "Consultorio 2 - Dra. Morales",
                "Quirófano Principal",
                "Tópico de Emergencias",
                "Área de Grooming"
        });
        cbConsultorio.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        cbConsultorio.setBackground(Color.WHITE);

        form.add(crearFilaCampo("Consultorio o Espacio Asignado *", cbConsultorio));
        form.add(Box.createVerticalStrut(6));

        txtMotivoTriaje = crearCampoTextoCompacto();
        form.add(crearFilaCampo("Signos Clínicos / Motivo de Urgencia *", txtMotivoTriaje));

        card.add(form, BorderLayout.CENTER);

        // Botones inferiores
        JPanel bot = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        bot.setOpaque(false);

        JButton btnLimpiar = crearBotonAccion("Limpiar", false);
        btnLimpiar.addActionListener(e -> limpiarFormulario());

        JButton btnIngresar = crearBotonAccion("Ingresar a Sala", true);
        btnIngresar.setIcon(Iconos.crearIconoReloj(12, Color.WHITE));
        btnIngresar.setIconTextGap(4);
        btnIngresar.addActionListener(e -> ingresarPacienteASala());

        bot.add(btnLimpiar);
        bot.add(btnIngresar);
        card.add(bot, BorderLayout.SOUTH);

        return card;
    }

    private JPanel crearTarjetaMonitorSala() {
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

        JLabel lblTit = new JLabel("Monitor de Pacientes en Sala de Espera");
        lblTit.setIcon(Iconos.crearIconoHistorial(15, COLOR_AZUL_PRIMARIO));
        lblTit.setIconTextGap(6);
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTit.setForeground(COLOR_TEXTO_TITULO);
        top.add(lblTit, BorderLayout.WEST);

        lblContador = new JLabel("0 en espera");
        lblContador.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblContador.setForeground(COLOR_TEXTO_MUTED);
        top.add(lblContador, BorderLayout.EAST);
        card.add(top, BorderLayout.NORTH);

        // Tabla
        String[] columnas = {"Ticket", "Hora", "Paciente", "Prioridad Triaje", "Motivo", "Consultorio", "Estado"};
        modeloMonitor = new DefaultTableModel(columnas, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        tablaMonitor = new JTable(modeloMonitor);
        Ui.formatearTabla(tablaMonitor, new int[]{0, 1, 3, 5, 6}, new int[]{});

        tablaMonitor.getColumnModel().getColumn(3).setCellRenderer(new BadgeTriajeRenderer());
        tablaMonitor.getColumnModel().getColumn(6).setCellRenderer(new BadgeEstadoTriajeRenderer());

        tablaMonitor.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    mostrarDetalleTriajeSeleccionado();
                }
            }
        });

        JScrollPane sp = new JScrollPane(tablaMonitor);
        sp.setPreferredSize(new Dimension(540, 240));
        sp.setBorder(BorderFactory.createLineBorder(COLOR_BORDE, 1));
        card.add(sp, BorderLayout.CENTER);

        // Acciones inferiores
        JPanel bot = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 4));
        bot.setOpaque(false);

        JButton btnLlamar = crearBotonAccion("Llamar a Consultorio", false);
        btnLlamar.setIcon(Iconos.crearIconoEstetoscopio(12, COLOR_AZUL_PRIMARIO));
        btnLlamar.setIconTextGap(4);
        btnLlamar.addActionListener(e -> llamarPacienteSeleccionado());

        JButton btnFinalizar = crearBotonAccion("Finalizar Atención", true);
        btnFinalizar.addActionListener(e -> finalizarAtencionSeleccionada());

        bot.add(btnLlamar);
        bot.add(btnFinalizar);
        card.add(bot, BorderLayout.SOUTH);

        return card;
    }

    private JPanel crearFilaCampo(String label, Component componente) {
        JPanel p = new JPanel(new BorderLayout(0, 2));
        p.setOpaque(false);
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));

        JLabel lbl = new JLabel(label, SwingConstants.LEFT);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lbl.setForeground(new Color(100, 116, 139));
        lbl.setHorizontalAlignment(SwingConstants.LEFT);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        p.add(lbl, BorderLayout.NORTH);
        p.add(componente, BorderLayout.CENTER);
        return p;
    }

    private JTextField crearCampoTextoCompacto() {
        JTextField txt = new JTextField();
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        txt.setPreferredSize(new Dimension(0, 26));
        txt.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));
        txt.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225), 1),
                new EmptyBorder(2, 6, 2, 6)
        ));
        return txt;
    }

    private JButton crearBotonAccion(String texto, boolean primario) {
        JButton btn = new JButton(texto) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                boolean esPrimario = Boolean.TRUE.equals(getClientProperty("primario"));
                if (esPrimario) {
                    g2.setColor(getModel().isRollover() ? Ui.TURQUESA_OSCURO : COLOR_AZUL_PRIMARIO);
                } else {
                    g2.setColor(getModel().isRollover() ? new Color(241, 245, 249) : Color.WHITE);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                if (!esPrimario) {
                    g2.setColor(COLOR_BORDE);
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 6, 6);
                }
                super.paintComponent(g2);
                g2.dispose();
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
        btn.setPreferredSize(new Dimension(btn.getPreferredSize().width, 28));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        return btn;
    }

    public void registrarLlegadaDesdeCita(Cita cita) {
        if (cita == null) return;
        recargarDatos();
        for (int i = 0; i < cbPacienteCita.getItemCount(); i++) {
            CitaOTurnoItem item = cbPacienteCita.getItemAt(i);
            if (item != null && item.cita != null && item.cita.getIdCita().equals(cita.getIdCita())) {
                cbPacienteCita.setSelectedIndex(i);
                autollenarDatosDesdeCita();
                break;
            }
        }
    }

    public void recargarDatos() {
        // Cargar citas en combo
        if (cbPacienteCita.getItemCount() == 0) {
            cbPacienteCita.addItem(new CitaOTurnoItem(null, "--- Ingreso Directo por Urgencia (Sin cita) ---"));
            for (Cita c : repo.getCitas()) {
                cbPacienteCita.addItem(new CitaOTurnoItem(c, c.getIdCita() + " - " + c.getNombreMascota() + " (" + c.getNombreCliente() + ")"));
            }
        }

        List<PacienteTriaje> todos = repo.getPacientesTriaje();
        String fTriaje = (String) cbFiltroTriaje.getSelectedItem();
        String fEstado = (String) cbFiltroEstado.getSelectedItem();
        String q = txtBuscar.getText().trim().toLowerCase();

        listaActual = todos.stream().filter(p -> {
            if (fTriaje != null && !fTriaje.startsWith("Todos") && !p.getNivelTriaje().startsWith(fTriaje.substring(0, 4))) return false;
            if (fEstado != null && !fEstado.startsWith("Todos") && !p.getEstado().equalsIgnoreCase(fEstado)) return false;
            if (!q.isEmpty()) {
                boolean match = p.getNombreMascota().toLowerCase().contains(q) ||
                                p.getNombrePropietario().toLowerCase().contains(q) ||
                                p.getIdTicket().toLowerCase().contains(q) ||
                                p.getMotivo().toLowerCase().contains(q);
                if (!match) return false;
            }
            return true;
        }).toList();

        modeloMonitor.setRowCount(0);
        for (PacienteTriaje p : listaActual) {
            modeloMonitor.addRow(new Object[]{
                    p.getIdTicket(),
                    p.getHoraLlegadaFormateada(),
                    p.getNombreMascota(),
                    p.getNivelTriaje(),
                    p.getMotivo(),
                    p.getConsultorioAsignado(),
                    p.getEstado()
            });
        }
        lblContador.setText(listaActual.size() + " en sala");

        // KPIs
        long enEspera = todos.stream().filter(p -> "En Espera".equalsIgnoreCase(p.getEstado())).count();
        long enConsulta = todos.stream().filter(p -> "En Consulta".equalsIgnoreCase(p.getEstado())).count();
        long criticos = todos.stream().filter(p -> p.getNivelTriaje().toUpperCase().contains("ROJO")).count();

        lblKpiEnEspera.setText(String.valueOf(enEspera));
        lblKpiEnConsulta.setText(String.valueOf(enConsulta));
        lblKpiCriticos.setText(String.valueOf(criticos));
    }

    private void autollenarDatosDesdeCita() {
        CitaOTurnoItem item = (CitaOTurnoItem) cbPacienteCita.getSelectedItem();
        if (item != null && item.cita != null) {
            txtNombreMascota.setText(item.cita.getNombreMascota());
            txtEspecieRaza.setText(item.cita.getEspecieRaza());
            txtPropietario.setText(item.cita.getNombreCliente());
            txtTelefono.setText(item.cita.getTelefonoCliente());
            txtMotivoTriaje.setText(item.cita.getMotivo());
            if (item.cita.getVeterinario().contains("Mendoza")) {
                cbConsultorio.setSelectedItem("Consultorio 1 - Dr. Mendoza");
            } else {
                cbConsultorio.setSelectedItem("Consultorio 2 - Dra. Morales");
            }
            if ("Emergencia".equalsIgnoreCase(item.cita.getPrioridad())) {
                rbRojo.setSelected(true);
            } else if ("Urgente".equalsIgnoreCase(item.cita.getPrioridad())) {
                rbAmarillo.setSelected(true);
            } else {
                rbVerde.setSelected(true);
            }
        }
    }

    private void ingresarPacienteASala() {
        String mascota = txtNombreMascota.getText().trim();
        String especie = txtEspecieRaza.getText().trim();
        String prop = txtPropietario.getText().trim();
        String motivo = txtMotivoTriaje.getText().trim();

        if (mascota.isEmpty() || especie.isEmpty() || prop.isEmpty() || motivo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Complete los datos obligatorios del paciente para ingresar a triaje.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double peso = 10.0;
        double temp = 38.5;
        int fc = 90;
        try {
            peso = Double.parseDouble(txtPeso.getText().trim());
            temp = Double.parseDouble(txtTemperatura.getText().trim());
            fc = Integer.parseInt(txtFrecuenciaCardiaca.getText().trim());
        } catch (Exception ignored) { }

        String nivel = rbRojo.isSelected() ? "ROJO (Emergencia crítica)" :
                       rbAmarillo.isSelected() ? "AMARILLO (Urgencia)" : "VERDE (Normal)";

        CitaOTurnoItem cItem = (CitaOTurnoItem) cbPacienteCita.getSelectedItem();
        String idCita = (cItem != null && cItem.cita != null) ? cItem.cita.getIdCita() : null;

        PacienteTriaje pt = new PacienteTriaje(
                null,
                idCita,
                "VET-" + (100 + (int)(Math.random() * 800)),
                mascota,
                especie,
                prop,
                txtTelefono.getText().trim(),
                LocalTime.now(),
                peso,
                temp,
                fc,
                nivel,
                motivo,
                (String) cbConsultorio.getSelectedItem(),
                "En Espera"
        );
        repo.agregarPacienteTriaje(pt);

        JOptionPane.showMessageDialog(this,
                "Ticket " + pt.getIdTicket() + " generado para " + pt.getNombreMascota() + ".\nClasificación: " + pt.getNivelTriaje() + "\nConsultorio: " + pt.getConsultorioAsignado(),
                "Ingreso a Sala Registrado", JOptionPane.INFORMATION_MESSAGE);

        limpiarFormulario();
        recargarDatos();
    }

    private void limpiarFormulario() {
        txtNombreMascota.setText("");
        txtEspecieRaza.setText("");
        txtPropietario.setText("");
        txtTelefono.setText("");
        txtMotivoTriaje.setText("");
        rbVerde.setSelected(true);
        cbPacienteCita.setSelectedIndex(0);
    }

    private void llamarSiguienteTurno() {
        List<PacienteTriaje> enEspera = repo.getPacientesTriaje().stream()
                .filter(p -> "En Espera".equalsIgnoreCase(p.getEstado()))
                .sorted((a, b) -> {
                    // Orden de prioridad: ROJO primero, luego AMARILLO, luego VERDE
                    int pa = a.getNivelTriaje().startsWith("ROJO") ? 0 : a.getNivelTriaje().startsWith("AMARILLO") ? 1 : 2;
                    int pb = b.getNivelTriaje().startsWith("ROJO") ? 0 : b.getNivelTriaje().startsWith("AMARILLO") ? 1 : 2;
                    if (pa != pb) return Integer.compare(pa, pb);
                    return a.getHoraLlegada().compareTo(b.getHoraLlegada());
                }).toList();

        if (enEspera.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay pacientes pendientes en la sala de espera actualmente.", "Sala Vacía", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        PacienteTriaje sig = enEspera.get(0);
        sig.setEstado("En Consulta");
        recargarDatos();

        JOptionPane.showMessageDialog(this,
                "LLAMANDO A CONSULTA:\n\n" +
                "Ticket: " + sig.getIdTicket() + "\n" +
                "Paciente: " + sig.getNombreMascota() + " (" + sig.getEspecieRaza() + ")\n" +
                "Propietario: " + sig.getNombrePropietario() + "\n" +
                "Triaje: " + sig.getNivelTriaje() + "\n" +
                "Destino: " + sig.getConsultorioAsignado(),
                "Llamado a Consulta", JOptionPane.INFORMATION_MESSAGE);
    }

    private void llamarPacienteSeleccionado() {
        int row = tablaMonitor.getSelectedRow();
        if (row < 0 || row >= listaActual.size()) {
            JOptionPane.showMessageDialog(this, "Seleccione un paciente de la tabla para llamar a consultorio.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        PacienteTriaje p = listaActual.get(row);
        p.setEstado("En Consulta");
        recargarDatos();
        JOptionPane.showMessageDialog(this, "Paciente " + p.getNombreMascota() + " llamado al " + p.getConsultorioAsignado() + ".", "Paciente Llamado", JOptionPane.INFORMATION_MESSAGE);
    }

    private void finalizarAtencionSeleccionada() {
        int row = tablaMonitor.getSelectedRow();
        if (row < 0 || row >= listaActual.size()) {
            JOptionPane.showMessageDialog(this, "Seleccione un paciente de la tabla para finalizar atención.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        PacienteTriaje p = listaActual.get(row);
        p.setEstado("Atendido");
        recargarDatos();
        JOptionPane.showMessageDialog(this, "Atención clínica finalizada para el paciente " + p.getNombreMascota() + ".", "Atención Concluida", JOptionPane.INFORMATION_MESSAGE);
    }

    private void mostrarDetalleTriajeSeleccionado() {
        int row = tablaMonitor.getSelectedRow();
        if (row < 0 || row >= listaActual.size()) return;
        PacienteTriaje p = listaActual.get(row);

        String info = "HOJA DE TRIAJE CLÍNICO\n\n" +
                      "Ticket de Sala: " + p.getIdTicket() + " (Llegada: " + p.getHoraLlegadaFormateada() + " hrs)\n" +
                      "Paciente: " + p.getNombreMascota() + " (" + p.getEspecieRaza() + ")\n" +
                      "Propietario: " + p.getNombrePropietario() + " | Contacto: " + p.getTelefonoPropietario() + "\n\n" +
                      "SIGNOS VITALES REGISTRADOS:\n" +
                      "• Peso: " + p.getPesoKg() + " kg\n" +
                      "• Temperatura: " + p.getTemperaturaC() + " °C\n" +
                      "• Frecuencia Cardíaca: " + p.getFrecuenciaCardiacaPpm() + " ppm\n\n" +
                      "CLASIFICACIÓN DE TRIAJE:\n" +
                      "• Prioridad: " + p.getNivelTriaje() + "\n" +
                      "• Espacio: " + p.getConsultorioAsignado() + "\n" +
                      "• Estado en Sala: " + p.getEstado() + "\n\n" +
                      "MOTIVO DE CONSULTA:\n" + p.getMotivo();

        JOptionPane.showMessageDialog(this, info, "Ficha de Triaje - " + p.getIdTicket(), JOptionPane.INFORMATION_MESSAGE);
    }

    private static class CitaOTurnoItem {
        final Cita cita;
        final String etiqueta;
        CitaOTurnoItem(Cita cita, String etiqueta) {
            this.cita = cita;
            this.etiqueta = etiqueta;
        }
        @Override
        public String toString() { return etiqueta; }
    }

    private static class BadgeTriajeRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            lbl.setHorizontalAlignment(SwingConstants.CENTER);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
            String t = String.valueOf(value).toUpperCase();
            if (!isSelected) {
                if (t.contains("ROJO")) {
                    lbl.setForeground(new Color(220, 38, 38));
                } else if (t.contains("AMARILLO")) {
                    lbl.setForeground(new Color(217, 119, 6));
                } else {
                    lbl.setForeground(new Color(22, 163, 74));
                }
            }
            return lbl;
        }
    }

    private static class BadgeEstadoTriajeRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            lbl.setHorizontalAlignment(SwingConstants.CENTER);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
            String st = String.valueOf(value);
            if (!isSelected) {
                if ("En Consulta".equalsIgnoreCase(st)) {
                    lbl.setForeground(new Color(2, 132, 199));
                } else if ("Atendido".equalsIgnoreCase(st)) {
                    lbl.setForeground(new Color(100, 116, 139));
                } else {
                    lbl.setForeground(new Color(217, 119, 6));
                }
            }
            return lbl;
        }
    }
}
