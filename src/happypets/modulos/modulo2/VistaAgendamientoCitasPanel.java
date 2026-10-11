package happypets.modulos.modulo2;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import happypets.data.RepositorioVeterinaria;
import happypets.model.Cita;
import happypets.model.PacienteTriaje;
import happypets.ui.Iconos;
import happypets.ui.Ui;

/**
 * Submódulo 2.1: Agendamiento y Control Operativo de Citas Médicas Veterinarias.
 * Arquitectura Master-Detail con Agenda Cronológica y Panel de Control de Turno en Vivo.
 */
public class VistaAgendamientoCitasPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private static final Color COLOR_BORDE = Ui.BORDE_SUAVE;
    private static final Color COLOR_TURQUESA = Ui.TURQUESA;
    private static final Color COLOR_TURQUESA_OSCURO = Ui.TURQUESA_OSCURO;
    private static final Color COLOR_TURQUESA_SUAVE = Ui.TURQUESA_SUAVE;
    private static final Color COLOR_TITULO = Ui.TEXTO_TITULO;
    private static final Color COLOR_MUTED = Ui.TEXTO_MUTED;

    private final RepositorioVeterinaria repo = RepositorioVeterinaria.getInstancia();

    // Estado del modelo
    private List<Cita> listaCitasCompleta = new ArrayList<>();
    private List<Cita> listaCitasFiltrada = new ArrayList<>();
    private Cita citaSeleccionada;
    private String filtroFechaActual = "HOY";

    // Componentes de Cabecera y KPIs
    private JLabel lblKpiCitasHoyVal;
    private JLabel lblKpiEnEsperaVal;
    private JLabel lblKpiConfirmadasVal;
    private JLabel lblKpiUrgenciasVal;

    // Componentes del Panel Izquierdo (Master)
    private JButton btnFiltroHoy;
    private JButton btnFiltroManana;
    private JButton btnFiltroTodas;
    private JComboBox<String> cbFiltroDoctor;
    private JTextField txtBuscar;
    private JLabel lblContadorCitas;
    private DefaultListModel<Cita> modeloListaCitas;
    private JList<Cita> listaCitas;

    // Componentes del Panel Derecho (Detail - Ficha de Turno)
    private JPanel cardControlTurno;
    private JPanel panelAvatarPet;
    private JLabel lblPetNombre;
    private JLabel lblPetCodigoBadge;
    private JLabel lblPetEspecieRaza;
    private JLabel lblPetDuenoInfo;

    // Micro-KPIs del Turno
    private JLabel lblKpiHorarioVal;
    private JLabel lblKpiEstadoVal;
    private JLabel lblKpiConsultorioVal;
    private JLabel lblKpiCostoVal;

    // Bloques informativos del turno
    private JLabel lblDetalleServicio;
    private JLabel lblDetalleMotivo;
    private JLabel lblDetallePrioridad;
    private JLabel lblDetalleObservaciones;
    private JLabel lblDetalleVeterinario;

    private JPanel panelSinCitas;

    // Interfaz de navegación inter-módulos
    public interface AccionNavegacionAgendaListener {
        void irASalaEspera(Cita cita);
        void irACalendario(LocalDate fecha);
        void irARecordatorios(Cita cita);
    }

    private AccionNavegacionAgendaListener navegacionListener;

    public void setNavegacionListener(AccionNavegacionAgendaListener listener) {
        this.navegacionListener = listener;
    }

    public VistaAgendamientoCitasPanel() {
        setLayout(new BorderLayout(0, 10));
        setOpaque(false);
        setBorder(new EmptyBorder(12, 16, 14, 16));

        // 1. Cabecera superior
        add(crearCabeceraVista(), BorderLayout.NORTH);

        // 2. Contenedor central (KPIs + Split Master-Detail)
        JPanel centralWrapper = new JPanel(new BorderLayout(0, 10));
        centralWrapper.setOpaque(false);

        // Fila de 4 KPIs superiores
        centralWrapper.add(crearFilaKpiAgenda(), BorderLayout.NORTH);

        // Split Layout (Master 360px / Detail 64%)
        JPanel centralSplit = new JPanel(new BorderLayout(14, 0));
        centralSplit.setOpaque(false);

        centralSplit.add(crearPanelIzquierdoMaster(), BorderLayout.WEST);
        centralSplit.add(crearPanelDerechoDetail(), BorderLayout.CENTER);

        centralWrapper.add(centralSplit, BorderLayout.CENTER);
        add(centralWrapper, BorderLayout.CENTER);

        recargarDatos();
    }

    // =========================================================================
    // 1. CABECERA DE LA VISTA
    // =========================================================================
    private JPanel crearCabeceraVista() {
        JPanel cab = new JPanel(new BorderLayout());
        cab.setOpaque(false);
        cab.setPreferredSize(new Dimension(0, 42));

        JPanel izq = new JPanel();
        izq.setOpaque(false);
        izq.setLayout(new BoxLayout(izq, BoxLayout.Y_AXIS));

        JLabel lblTit = new JLabel("Agendamiento y Registro de Citas");
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblTit.setForeground(COLOR_TITULO);

        JLabel lblSub = new JLabel("Módulo 2.1 · Reserva, reprogramación, control operativo y gestión cronológica de turnos veterinarios");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblSub.setForeground(COLOR_MUTED);

        izq.add(lblTit);
        izq.add(Box.createVerticalStrut(2));
        izq.add(lblSub);
        cab.add(izq, BorderLayout.CENTER);

        JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 2));
        der.setOpaque(false);

        JButton btnExportar = crearBotonAccion("Exportar Agenda", false);
        btnExportar.setIcon(Iconos.crearIconoImprimir(13, COLOR_TURQUESA));
        btnExportar.setIconTextGap(6);
        btnExportar.addActionListener(e -> exportarAgendaReporte());

        JButton btnNuevaCita = crearBotonAccion("+ Nueva Cita", true);
        btnNuevaCita.setIcon(Iconos.crearIconoCalendario(14, Color.WHITE));
        btnNuevaCita.setIconTextGap(6);
        btnNuevaCita.addActionListener(e -> abrirFormularioNuevaCita());

        der.add(btnExportar);
        der.add(btnNuevaCita);
        cab.add(der, BorderLayout.EAST);

        return cab;
    }

    private JPanel crearFilaKpiAgenda() {
        JPanel fila = new JPanel(new GridLayout(1, 4, 10, 0));
        fila.setOpaque(false);
        fila.setPreferredSize(new Dimension(0, 56));

        lblKpiCitasHoyVal = new JLabel("0");
        lblKpiEnEsperaVal = new JLabel("0");
        lblKpiConfirmadasVal = new JLabel("0%");
        lblKpiUrgenciasVal = new JLabel("0");

        fila.add(crearCardKpi(lblKpiCitasHoyVal, "CITAS PROGRAMADAS HOY", new Color(224, 242, 254), Iconos.crearIconoCalendario(20, COLOR_TURQUESA)));
        fila.add(crearCardKpi(lblKpiEnEsperaVal, "EN SALA DE ESPERA", new Color(254, 243, 199), Iconos.crearIconoReloj(20, new Color(217, 119, 6))));
        fila.add(crearCardKpi(lblKpiConfirmadasVal, "CONFIRMADAS POR WHATSAPP", new Color(220, 252, 231), Iconos.crearIconoCheck(20, new Color(22, 163, 74))));
        fila.add(crearCardKpi(lblKpiUrgenciasVal, "URGENCIAS CLÍNICAS", new Color(254, 226, 226), Iconos.crearIconoAlertaTriaje(20, new Color(220, 38, 38))));

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
        lblEtq.setForeground(COLOR_MUTED);

        lblVal.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblVal.setForeground(COLOR_TITULO);

        pText.add(lblEtq);
        pText.add(Box.createVerticalStrut(1));
        pText.add(lblVal);
        card.add(pText, BorderLayout.CENTER);

        return card;
    }

    // =========================================================================
    // 2. PANEL IZQUIERDO (MASTER): AGENDA DE TURNOS DEL DÍA
    // =========================================================================
    private JPanel crearPanelIzquierdoMaster() {
        JPanel master = new JPanel(new BorderLayout(0, 8)) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(COLOR_BORDE);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        master.setOpaque(false);
        master.setPreferredSize(new Dimension(380, 0));
        master.setBorder(new EmptyBorder(12, 12, 12, 12));

        // Sub-panel superior de filtros
        JPanel panelFiltros = new JPanel();
        panelFiltros.setOpaque(false);
        panelFiltros.setLayout(new BoxLayout(panelFiltros, BoxLayout.Y_AXIS));

        // Pills de fecha: Hoy / Mañana / Todas (Segmented Control 1 fila)
        JPanel filaPills = new JPanel(new GridLayout(1, 3, 6, 0));
        filaPills.setOpaque(false);
        filaPills.setAlignmentX(Component.LEFT_ALIGNMENT);
        filaPills.setPreferredSize(new Dimension(356, 26));
        filaPills.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));

        btnFiltroHoy = crearBotonPill("Citas de Hoy", true);
        btnFiltroManana = crearBotonPill("Mañana", false);
        btnFiltroTodas = crearBotonPill("Todas", false);

        btnFiltroHoy.addActionListener(e -> {
            filtroFechaActual = "HOY";
            actualizarPillsFiltro(btnFiltroHoy, btnFiltroManana, btnFiltroTodas);
            recargarDatos();
        });
        btnFiltroManana.addActionListener(e -> {
            filtroFechaActual = "MANANA";
            actualizarPillsFiltro(btnFiltroManana, btnFiltroHoy, btnFiltroTodas);
            recargarDatos();
        });
        btnFiltroTodas.addActionListener(e -> {
            filtroFechaActual = "TODAS";
            actualizarPillsFiltro(btnFiltroTodas, btnFiltroHoy, btnFiltroManana);
            recargarDatos();
        });

        filaPills.add(btnFiltroHoy);
        filaPills.add(btnFiltroManana);
        filaPills.add(btnFiltroTodas);
        panelFiltros.add(filaPills);
        panelFiltros.add(Box.createVerticalStrut(8));

        // Filtro por doctor
        cbFiltroDoctor = new JComboBox<>(new String[]{
                "Todos los veterinarios",
                "Dr. Roberto Mendoza",
                "Dra. Laura Morales",
                "Dr. Carlos Silva",
                "Dra. Carmen Vega"
        });
        cbFiltroDoctor.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        cbFiltroDoctor.setPreferredSize(new Dimension(336, 28));
        cbFiltroDoctor.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        cbFiltroDoctor.setBackground(Color.WHITE);
        cbFiltroDoctor.addActionListener(e -> filtrarCitas());
        panelFiltros.add(cbFiltroDoctor);
        panelFiltros.add(Box.createVerticalStrut(6));

        // Buscador reactivo
        txtBuscar = new JTextField();
        txtBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        txtBuscar.setPreferredSize(new Dimension(336, 28));
        txtBuscar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        txtBuscar.setToolTipText("Buscar por mascota, propietario o turno...");
        txtBuscar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDE, 1),
                new EmptyBorder(2, 6, 2, 6)
        ));
        txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { filtrarCitas(); }
            @Override public void removeUpdate(DocumentEvent e) { filtrarCitas(); }
            @Override public void changedUpdate(DocumentEvent e) { filtrarCitas(); }
        });
        panelFiltros.add(txtBuscar);
        panelFiltros.add(Box.createVerticalStrut(8));

        // Cabecera contador de citas
        JPanel topLista = new JPanel(new BorderLayout());
        topLista.setOpaque(false);
        topLista.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblTitLista = new JLabel("TURNOS Y CITAS PROGRAMADAS");
        lblTitLista.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblTitLista.setForeground(COLOR_MUTED);
        topLista.add(lblTitLista, BorderLayout.WEST);

        lblContadorCitas = new JLabel("0 citas");
        lblContadorCitas.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblContadorCitas.setForeground(COLOR_MUTED);
        topLista.add(lblContadorCitas, BorderLayout.EAST);
        panelFiltros.add(topLista);
        panelFiltros.add(Box.createVerticalStrut(4));

        master.add(panelFiltros, BorderLayout.NORTH);

        // Lista interactiva de citas (Timeline de turnos)
        modeloListaCitas = new DefaultListModel<>();
        listaCitas = new JList<>(modeloListaCitas);
        listaCitas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaCitas.setCellRenderer(new CitaCellRenderer());
        listaCitas.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Cita c = listaCitas.getSelectedValue();
                if (c != null) {
                    mostrarDetalleTurnoEnVivo(c);
                }
            }
        });

        JScrollPane scrollCitas = new JScrollPane(listaCitas);
        scrollCitas.setBorder(BorderFactory.createLineBorder(COLOR_BORDE, 1));
        scrollCitas.getVerticalScrollBar().setUnitIncrement(14);
        master.add(scrollCitas, BorderLayout.CENTER);

        return master;
    }

    private JButton crearBotonPill(String texto, boolean activo) {
        JButton btn = new JButton(texto) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                boolean esActivo = Boolean.TRUE.equals(getClientProperty("activo"));
                if (esActivo) {
                    g2.setColor(COLOR_TURQUESA);
                } else {
                    g2.setColor(getModel().isRollover() ? new Color(241, 245, 249) : Color.WHITE);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                if (!esActivo) {
                    g2.setColor(COLOR_BORDE);
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                }
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        btn.putClientProperty("activo", activo);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 10));
        btn.setForeground(activo ? Color.WHITE : COLOR_TITULO);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(btn.getPreferredSize().width + 12, 24));
        btn.setBorder(new EmptyBorder(2, 8, 2, 8));
        return btn;
    }

    private void actualizarPillsFiltro(JButton sel, JButton b2, JButton b3) {
        sel.putClientProperty("activo", true);
        sel.setForeground(Color.WHITE);
        b2.putClientProperty("activo", false);
        b2.setForeground(COLOR_TITULO);
        b3.putClientProperty("activo", false);
        b3.setForeground(COLOR_TITULO);
        sel.repaint();
        b2.repaint();
        b3.repaint();
    }

    // =========================================================================
    // 3. PANEL DERECHO (DETAIL): PANEL DE CONTROL OPERATIVO DE TURNO EN VIVO
    // =========================================================================
    private JPanel crearPanelDerechoDetail() {
        cardControlTurno = new JPanel(new BorderLayout(0, 10)) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(COLOR_BORDE);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        cardControlTurno.setOpaque(false);
        cardControlTurno.setBorder(new EmptyBorder(14, 16, 14, 16));

        // Inicializar labels
        lblPetNombre = new JLabel("Paciente");
        lblPetCodigoBadge = crearBadge("CIT-0000", new Color(241, 245, 249), COLOR_TITULO);
        lblPetEspecieRaza = crearBadge("Canino", COLOR_TURQUESA_SUAVE, COLOR_TURQUESA_OSCURO);
        lblPetDuenoInfo = new JLabel("Propietario Responsable: -");

        lblKpiHorarioVal = new JLabel("09:00");
        lblKpiEstadoVal = new JLabel("Programada");
        lblKpiConsultorioVal = new JLabel("Consultorio 1");
        lblKpiCostoVal = new JLabel("S/. 75.00");

        lblDetalleServicio = new JLabel("-");
        lblDetalleMotivo = new JLabel("-");
        lblDetallePrioridad = new JLabel("-");
        lblDetalleObservaciones = new JLabel("-");
        lblDetalleVeterinario = new JLabel("-");

        // Estado vacío si no hay citas
        panelSinCitas = new JPanel(new BorderLayout());
        panelSinCitas.setOpaque(false);

        JPanel centroVacio = new JPanel();
        centroVacio.setOpaque(false);
        centroVacio.setLayout(new BoxLayout(centroVacio, BoxLayout.Y_AXIS));

        JLabel lblIcoVacio = new JLabel(Iconos.crearIconoCalendario(42, COLOR_MUTED));
        lblIcoVacio.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblTitVacio = new JLabel("No hay citas registradas para este filtro");
        lblTitVacio.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitVacio.setForeground(COLOR_TITULO);
        lblTitVacio.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSubVacio = new JLabel("Seleccione otro rango de fechas o programe una nueva cita médica.");
        lblSubVacio.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubVacio.setForeground(COLOR_MUTED);
        lblSubVacio.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btnAgendarPrimer = crearBotonAccion("+ Agendar Nueva Cita", true);
        btnAgendarPrimer.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnAgendarPrimer.addActionListener(e -> abrirFormularioNuevaCita());

        centroVacio.add(Box.createVerticalGlue());
        centroVacio.add(lblIcoVacio);
        centroVacio.add(Box.createVerticalStrut(8));
        centroVacio.add(lblTitVacio);
        centroVacio.add(Box.createVerticalStrut(4));
        centroVacio.add(lblSubVacio);
        centroVacio.add(Box.createVerticalStrut(12));
        centroVacio.add(btnAgendarPrimer);
        centroVacio.add(Box.createVerticalGlue());

        panelSinCitas.add(centroVacio, BorderLayout.CENTER);

        return cardControlTurno;
    }

    private void mostrarDetalleTurnoEnVivo(Cita c) {
        this.citaSeleccionada = c;
        if (c == null) {
            mostrarEstadoSinCitas();
            return;
        }

        cardControlTurno.removeAll();
        cardControlTurno.setLayout(new BorderLayout(0, 10));

        // 1. Tarjeta Superior: Ficha Ejecutiva del Paciente y Titular
        JPanel cardTop = new JPanel(new BorderLayout(0, 8));
        cardTop.setOpaque(false);

        JPanel headerInfo = new JPanel(new BorderLayout(14, 0));
        headerInfo.setOpaque(false);

        String inicial = c.getNombreMascota() != null && !c.getNombreMascota().isEmpty() ? c.getNombreMascota().substring(0, 1).toUpperCase() : "P";
        panelAvatarPet = crearAvatarCircular(50, inicial, COLOR_TURQUESA_SUAVE, COLOR_TURQUESA_OSCURO);
        JPanel avatarWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        avatarWrapper.setOpaque(false);
        avatarWrapper.add(panelAvatarPet);
        headerInfo.add(avatarWrapper, BorderLayout.WEST);

        JPanel datosTxt = new JPanel();
        datosTxt.setOpaque(false);
        datosTxt.setLayout(new BoxLayout(datosTxt, BoxLayout.Y_AXIS));

        JPanel filaTitulo = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filaTitulo.setOpaque(false);
        filaTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblPetNombre.setText(c.getNombreMascota());
        lblPetNombre.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblPetNombre.setForeground(COLOR_TITULO);
        filaTitulo.add(lblPetNombre);

        lblPetCodigoBadge.setText(c.getIdCita());
        filaTitulo.add(lblPetCodigoBadge);

        lblPetEspecieRaza.setText(c.getEspecieRaza());
        filaTitulo.add(lblPetEspecieRaza);

        datosTxt.add(filaTitulo);
        datosTxt.add(Box.createVerticalStrut(4));

        lblPetDuenoInfo.setText("Propietario Responsable: " + c.getNombreCliente() + " (DNI: " + c.getDniCliente() + " · Cel: " + c.getTelefonoCliente() + ")");
        lblPetDuenoInfo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblPetDuenoInfo.setForeground(new Color(71, 85, 105));
        lblPetDuenoInfo.setAlignmentX(Component.LEFT_ALIGNMENT);
        datosTxt.add(lblPetDuenoInfo);

        headerInfo.add(datosTxt, BorderLayout.CENTER);
        cardTop.add(headerInfo, BorderLayout.NORTH);

        // Fila de 4 Micro-KPIs del Turno
        JPanel filaKpis = new JPanel(new GridLayout(1, 4, 10, 0));
        filaKpis.setOpaque(false);
        filaKpis.setBorder(new EmptyBorder(6, 0, 0, 0));

        lblKpiHorarioVal.setText(c.getHoraFormateada() + " hrs");
        lblKpiEstadoVal.setText(c.getEstado());
        lblKpiConsultorioVal.setText(c.getVeterinario().contains("Mendoza") ? "Consultorio 1" : "Consultorio 2");
        lblKpiCostoVal.setText(String.format(java.util.Locale.US, "S/. %.2f", c.getCostoEstimado()));

        filaKpis.add(crearItemKpiTurno("HORARIO ASIGNADO", lblKpiHorarioVal, "Duración: " + c.getDuracionMinutos() + " min", new Color(240, 253, 250), COLOR_TURQUESA));
        filaKpis.add(crearItemKpiTurno("ESTADO OPERATIVO", lblKpiEstadoVal, c.getFechaFormateada(), new Color(240, 253, 244), new Color(22, 163, 74)));
        filaKpis.add(crearItemKpiTurno("CONSULTORIO", lblKpiConsultorioVal, c.getVeterinario(), new Color(238, 242, 255), new Color(79, 70, 229)));
        filaKpis.add(crearItemKpiTurno("COSTO ESTIMADO", lblKpiCostoVal, "Tarifa de servicio", new Color(254, 243, 199), new Color(217, 119, 6)));

        cardTop.add(filaKpis, BorderLayout.CENTER);
        cardControlTurno.add(cardTop, BorderLayout.NORTH);

        // 2. Tarjeta Central: Detalle Clínico, Trazabilidad e Instrucciones
        JPanel cuerpo = new JPanel();
        cuerpo.setOpaque(false);
        cuerpo.setLayout(new BoxLayout(cuerpo, BoxLayout.Y_AXIS));
        cuerpo.setBorder(new EmptyBorder(10, 2, 10, 2));

        // Bloque 1: Servicio y Motivo
        lblDetalleServicio.setText(c.getTipoServicio());
        lblDetalleServicio.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblDetalleServicio.setForeground(COLOR_TITULO);

        lblDetalleMotivo.setText("Motivo / Sintomatología: " + c.getMotivo());
        lblDetalleMotivo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblDetalleMotivo.setForeground(new Color(51, 65, 85));

        cuerpo.add(crearBloqueTurno("SERVICIO SOLICITADO Y MOTIVO DE CONSULTA", lblDetalleServicio, lblDetalleMotivo, new Color(248, 250, 252), COLOR_BORDE));
        cuerpo.add(Box.createVerticalStrut(10));

        // Bloque 2: Prioridad y Clasificación
        lblDetallePrioridad.setText("Prioridad: " + c.getPrioridad().toUpperCase() + "   ·   " +
                (c.getPrioridad().equalsIgnoreCase("Emergencia") ? "Atención inmediata requerida en Quirófano/Box Crítico" :
                 c.getPrioridad().equalsIgnoreCase("Urgente") ? "Triaje preferencial - tiempo máximo espera 15 min" : "Atención por orden de llegada"));
        lblDetallePrioridad.setFont(new Font("Segoe UI", Font.BOLD, 12));

        Color bgPrio = new Color(240, 253, 244);
        Color borderPrio = new Color(187, 247, 208);
        Color txtPrio = new Color(22, 163, 74);

        if ("Emergencia".equalsIgnoreCase(c.getPrioridad())) {
            bgPrio = new Color(254, 242, 242);
            borderPrio = new Color(254, 202, 202);
            txtPrio = new Color(220, 38, 38);
        } else if ("Urgente".equalsIgnoreCase(c.getPrioridad())) {
            bgPrio = new Color(254, 243, 199);
            borderPrio = new Color(253, 230, 138);
            txtPrio = new Color(217, 119, 6);
        }
        lblDetallePrioridad.setForeground(txtPrio);

        cuerpo.add(crearBloqueTurno("CLASIFICACIÓN CLÍNICA Y TRIAJE", lblDetallePrioridad, null, bgPrio, borderPrio));
        cuerpo.add(Box.createVerticalStrut(10));

        // Bloque 3: Instrucciones Previas
        String obs = c.getObservaciones() != null && !c.getObservaciones().trim().isEmpty() ? c.getObservaciones() : "Sin instrucciones previas específicas registradas.";
        lblDetalleObservaciones.setText(obs);
        lblDetalleObservaciones.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblDetalleObservaciones.setForeground(COLOR_TITULO);

        cuerpo.add(crearBloqueTurno("INSTRUCCIONES PREVIAS / RECOMENDACIONES", lblDetalleObservaciones, null, Color.WHITE, COLOR_BORDE));
        cuerpo.add(Box.createVerticalStrut(10));

        // Bloque 4: Médico Responsable
        lblDetalleVeterinario.setText(c.getVeterinario() + "   ·   Especialista responsable del turno médico");
        lblDetalleVeterinario.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblDetalleVeterinario.setForeground(COLOR_MUTED);

        cuerpo.add(crearBloqueTurno("FACULTATIVO VETERINARIO A CARGO", lblDetalleVeterinario, null, new Color(248, 250, 252), COLOR_BORDE));

        JScrollPane scrollCuerpo = new JScrollPane(cuerpo);
        scrollCuerpo.setBorder(null);
        scrollCuerpo.setOpaque(false);
        scrollCuerpo.getViewport().setOpaque(false);
        scrollCuerpo.getVerticalScrollBar().setUnitIncrement(14);
        cardControlTurno.add(scrollCuerpo, BorderLayout.CENTER);

        // 3. Barra de Acciones Operativas Inmediatas
        JPanel barraAcciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        barraAcciones.setOpaque(false);
        barraAcciones.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, COLOR_BORDE));

        JButton btnPasarEspera = crearBotonAccion("Pasar a Sala de Espera", false);
        btnPasarEspera.setIcon(Iconos.crearIconoReloj(12, new Color(217, 119, 6)));
        btnPasarEspera.setIconTextGap(4);
        btnPasarEspera.addActionListener(e -> transferirASalaEspera(c));

        JButton btnNotificar = crearBotonAccion("Notificar WhatsApp", false);
        btnNotificar.setIcon(Iconos.crearIconoWhatsApp(12, new Color(22, 163, 74)));
        btnNotificar.setIconTextGap(4);
        btnNotificar.addActionListener(e -> enviarRecordatorioWhatsApp(c));

        JButton btnReprogramar = crearBotonAccion("Reprogramar", false);
        btnReprogramar.setIcon(Iconos.crearIconoCalendario(12, COLOR_TURQUESA));
        btnReprogramar.setIconTextGap(4);
        btnReprogramar.addActionListener(e -> reprogramarCitaActual(c));

        JButton btnAtendida = crearBotonAccion("Marcar Atendida", true);
        btnAtendida.addActionListener(e -> marcarCitaAtendida(c));

        barraAcciones.add(btnPasarEspera);
        barraAcciones.add(btnNotificar);
        barraAcciones.add(btnReprogramar);
        barraAcciones.add(btnAtendida);

        cardControlTurno.add(barraAcciones, BorderLayout.SOUTH);

        cardControlTurno.revalidate();
        cardControlTurno.repaint();
    }

    private JPanel crearItemKpiTurno(String etiqueta, JLabel lblValor, String sub, Color fondo, Color acento) {
        JPanel kpi = new JPanel(new BorderLayout(4, 2)) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(fondo);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(new Color(acento.getRed(), acento.getGreen(), acento.getBlue(), 60));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        kpi.setOpaque(false);
        kpi.setBorder(new EmptyBorder(6, 10, 6, 10));

        JPanel txt = new JPanel();
        txt.setOpaque(false);
        txt.setLayout(new BoxLayout(txt, BoxLayout.Y_AXIS));

        JLabel lblE = new JLabel(etiqueta);
        lblE.setFont(new Font("Segoe UI", Font.BOLD, 9));
        lblE.setForeground(COLOR_MUTED);

        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblValor.setForeground(COLOR_TITULO);

        JLabel lblS = new JLabel(sub);
        lblS.setFont(new Font("Segoe UI", Font.PLAIN, 9));
        lblS.setForeground(COLOR_MUTED);

        txt.add(lblE);
        txt.add(Box.createVerticalStrut(1));
        txt.add(lblValor);
        txt.add(Box.createVerticalStrut(1));
        txt.add(lblS);
        kpi.add(txt, BorderLayout.CENTER);

        return kpi;
    }

    private JPanel crearBloqueTurno(String titulo, JLabel principal, JLabel secundario, Color fondo, Color borde) {
        JPanel bloque = new JPanel(new BorderLayout(0, 4)) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(fondo);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(borde);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        bloque.setOpaque(false);
        bloque.setBorder(new EmptyBorder(8, 12, 8, 12));
        bloque.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        JLabel lblT = new JLabel(titulo);
        lblT.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblT.setForeground(COLOR_MUTED);
        bloque.add(lblT, BorderLayout.NORTH);

        JPanel cuerpo = new JPanel();
        cuerpo.setOpaque(false);
        cuerpo.setLayout(new BoxLayout(cuerpo, BoxLayout.Y_AXIS));

        if (principal != null) cuerpo.add(principal);
        if (secundario != null) {
            cuerpo.add(Box.createVerticalStrut(2));
            cuerpo.add(secundario);
        }

        bloque.add(cuerpo, BorderLayout.CENTER);
        return bloque;
    }

    private void mostrarEstadoSinCitas() {
        this.citaSeleccionada = null;
        cardControlTurno.removeAll();
        cardControlTurno.setLayout(new BorderLayout());
        cardControlTurno.add(panelSinCitas, BorderLayout.CENTER);
        cardControlTurno.revalidate();
        cardControlTurno.repaint();
    }

    // =========================================================================
    // 4. LÓGICA DE DATOS Y FILTRADO
    // =========================================================================

    public void recargarDatos() {
        listaCitasCompleta = repo.getCitas();
        filtrarCitas();
    }

    private void filtrarCitas() {
        LocalDate hoy = LocalDate.now();
        LocalDate fechaLimite = null;

        if ("HOY".equals(filtroFechaActual)) {
            fechaLimite = hoy;
        } else if ("MANANA".equals(filtroFechaActual)) {
            fechaLimite = hoy.plusDays(1);
        }

        String docSel = cbFiltroDoctor != null ? (String) cbFiltroDoctor.getSelectedItem() : "Todos los veterinarios";
        String txtQ = txtBuscar != null ? txtBuscar.getText().trim().toLowerCase() : "";

        LocalDate finalFechaLimite = fechaLimite;
        listaCitasFiltrada = listaCitasCompleta.stream().filter(c -> {
            if (finalFechaLimite != null && !c.getFecha().equals(finalFechaLimite)) return false;
            if (docSel != null && !docSel.startsWith("Todos") && !c.getVeterinario().contains(docSel.replace("Dr. ", "").replace("Dra. ", ""))) return false;
            if (!txtQ.isEmpty()) {
                boolean match = c.getNombreMascota().toLowerCase().contains(txtQ) ||
                                c.getNombreCliente().toLowerCase().contains(txtQ) ||
                                c.getIdCita().toLowerCase().contains(txtQ) ||
                                c.getVeterinario().toLowerCase().contains(txtQ) ||
                                c.getTipoServicio().toLowerCase().contains(txtQ);
                if (!match) return false;
            }
            return true;
        }).sorted((a, b) -> a.getHora().compareTo(b.getHora())).collect(Collectors.toList());

        if (modeloListaCitas != null) {
            modeloListaCitas.clear();
            for (Cita c : listaCitasFiltrada) {
                modeloListaCitas.addElement(c);
            }
            lblContadorCitas.setText(listaCitasFiltrada.size() + " citas registradas");
        }

        // Actualizar KPIs
        long citasHoy = listaCitasCompleta.stream().filter(c -> c.getFecha().equals(hoy)).count();
        long enEspera = listaCitasCompleta.stream().filter(c -> c.getFecha().equals(hoy) && "En Sala de Espera".equalsIgnoreCase(c.getEstado())).count();
        long confirmadas = listaCitasCompleta.stream().filter(c -> c.getFecha().equals(hoy) && "Confirmada".equalsIgnoreCase(c.getEstado())).count();
        long urgencias = listaCitasCompleta.stream().filter(c -> c.getFecha().equals(hoy) && ("Urgente".equalsIgnoreCase(c.getPrioridad()) || "Emergencia".equalsIgnoreCase(c.getPrioridad()))).count();

        lblKpiCitasHoyVal.setText(String.valueOf(citasHoy));
        lblKpiEnEsperaVal.setText(String.valueOf(enEspera));
        lblKpiConfirmadasVal.setText((citasHoy > 0 ? (confirmadas * 100 / citasHoy) : 0) + "%");
        lblKpiUrgenciasVal.setText(String.valueOf(urgencias));

        if (!listaCitasFiltrada.isEmpty()) {
            listaCitas.setSelectedIndex(0);
            mostrarDetalleTurnoEnVivo(listaCitasFiltrada.get(0));
        } else {
            mostrarEstadoSinCitas();
        }
    }

    // =========================================================================
    // 5. ACCIONES Y OPERACIONES DE CITA
    // =========================================================================

    private void abrirFormularioNuevaCita() {
        FormularioReservaCitaDialog dlg = new FormularioReservaCitaDialog(
                SwingUtilities.getWindowAncestor(this),
                null,
                nuevaCita -> {
                    recargarDatos();
                    for (int i = 0; i < modeloListaCitas.size(); i++) {
                        if (modeloListaCitas.get(i).getIdCita().equals(nuevaCita.getIdCita())) {
                            listaCitas.setSelectedIndex(i);
                            listaCitas.ensureIndexIsVisible(i);
                            break;
                        }
                    }
                    JOptionPane.showMessageDialog(this,
                            "Cita " + nuevaCita.getIdCita() + " agendada exitosamente para " + nuevaCita.getNombreMascota() + "\nFecha: " + nuevaCita.getFechaFormateada() + " a las " + nuevaCita.getHoraFormateada() + " hrs.",
                            "Cita Agendada", JOptionPane.INFORMATION_MESSAGE);
                }
        );
        dlg.setVisible(true);
    }

    private void reprogramarCitaActual(Cita c) {
        if (c == null) return;
        FormularioReservaCitaDialog dlg = new FormularioReservaCitaDialog(
                SwingUtilities.getWindowAncestor(this),
                c,
                citaModificada -> {
                    recargarDatos();
                    mostrarDetalleTurnoEnVivo(citaModificada);
                    JOptionPane.showMessageDialog(this,
                            "Cita " + citaModificada.getIdCita() + " reprogramada correctamente.",
                            "Turno Actualizado", JOptionPane.INFORMATION_MESSAGE);
                }
        );
        dlg.setVisible(true);
    }

    private void transferirASalaEspera(Cita cita) {
        if (cita == null) return;
        cita.setEstado("En Sala de Espera");

        PacienteTriaje pt = new PacienteTriaje(
                null, cita.getIdCita(), cita.getCodigoMascota(), cita.getNombreMascota(),
                cita.getEspecieRaza(), cita.getNombreCliente(), cita.getTelefonoCliente(),
                LocalTime.now(), 20.0, 38.5, 90,
                cita.getPrioridad().equalsIgnoreCase("Emergencia") ? "ROJO (Emergencia crítica)" :
                cita.getPrioridad().equalsIgnoreCase("Urgente") ? "AMARILLO (Urgencia)" : "VERDE (Normal)",
                cita.getMotivo(),
                cita.getVeterinario().contains("Mendoza") ? "Consultorio 1 - Dr. Mendoza" : "Consultorio 2 - Dra. Morales",
                "En Espera"
        );
        repo.agregarPacienteTriaje(pt);

        JOptionPane.showMessageDialog(this,
                "El paciente " + cita.getNombreMascota() + " ha ingresado a la Sala de Espera con ticket " + pt.getIdTicket() + ".\nPuede monitorizar sus constantes en el submódulo 'Sala de Espera y Triaje'.",
                "Ingreso a Sala de Espera", JOptionPane.INFORMATION_MESSAGE);

        recargarDatos();

        if (navegacionListener != null) {
            int op = JOptionPane.showConfirmDialog(this, "¿Desea abrir la pantalla de Sala de Espera y Triaje ahora?", "Navegación", JOptionPane.YES_NO_OPTION);
            if (op == JOptionPane.YES_OPTION) {
                navegacionListener.irASalaEspera(cita);
            }
        }
    }

    private void enviarRecordatorioWhatsApp(Cita c) {
        if (c == null) return;
        c.setEstado("Confirmada");
        recargarDatos();

        JOptionPane.showMessageDialog(this,
                "Recordatorio enviado exitosamente vía WhatsApp a " + c.getNombreCliente() + " (" + c.getTelefonoCliente() + "):\n\n" +
                "\"Hola " + c.getNombreCliente() + ", te recordamos que " + c.getNombreMascota() + " tiene cita de " + c.getTipoServicio() + " el " + c.getFechaFormateada() + " a las " + c.getHoraFormateada() + " hrs con el " + c.getVeterinario() + " en Happy Pets.\"",
                "Recordatorio WhatsApp Enviado", JOptionPane.INFORMATION_MESSAGE);
    }

    private void marcarCitaAtendida(Cita c) {
        if (c == null) return;
        c.setEstado("Atendida");
        recargarDatos();
        JOptionPane.showMessageDialog(this, "La cita de " + c.getNombreMascota() + " ha sido finalizada y marcada como Atendida.", "Cita Atendida", JOptionPane.INFORMATION_MESSAGE);
    }

    private void exportarAgendaReporte() {
        int rows = listaCitasFiltrada.size();
        String[][] datos = new String[rows][6];
        for (int i = 0; i < rows; i++) {
            Cita c = listaCitasFiltrada.get(i);
            datos[i][0] = c.getHoraFormateada();
            datos[i][1] = c.getIdCita();
            datos[i][2] = c.getNombreMascota();
            datos[i][3] = c.getNombreCliente();
            datos[i][4] = c.getTipoServicio();
            datos[i][5] = c.getEstado();
        }
        Ui.mostrarVisorReporte(
                (java.awt.Frame) SwingUtilities.getWindowAncestor(this),
                "AGENDA DE CITAS VETERINARIAS",
                "Programación de Turnos · Fecha: " + LocalDate.now(),
                "Total Citas Programadas: " + rows + " | Estado Operativo Activo",
                new String[]{"Hora", "ID Cita", "Paciente", "Propietario", "Servicio", "Estado"},
                datos,
                "PROGRAMACIÓN CLÍNICA HAPPY PETS",
                "Agenda_Citas_" + LocalDate.now()
        );
    }

    // =========================================================================
    // 6. COMPONENTES GRÁFICOS PERSONALIZADOS (AVATARES Y RENDERERS)
    // =========================================================================

    private JPanel crearAvatarCircular(int diametro, String texto, Color fondo, Color textoColor) {
        JPanel avatar = new JPanel() {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int d = Math.min(getWidth(), getHeight()) - 2;
                int x = (getWidth() - d) / 2;
                int y = (getHeight() - d) / 2;

                Color bg = (Color) getClientProperty("fondo");
                if (bg == null) bg = fondo;
                Color fg = (Color) getClientProperty("textoColor");
                if (fg == null) fg = textoColor;
                String txt = (String) getClientProperty("texto");
                if (txt == null) txt = texto;

                g2.setColor(bg);
                g2.fillOval(x, y, d, d);
                g2.setColor(new Color(bg.getRed(), bg.getGreen(), bg.getBlue(), 60));
                g2.drawOval(x, y, d, d);
                g2.setColor(fg);
                g2.setFont(new Font("Segoe UI", Font.BOLD, diametro >= 40 ? 18 : 13));
                FontMetrics fm = g2.getFontMetrics();
                int tx = x + (d - fm.stringWidth(txt)) / 2;
                int ty = y + (d - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(txt, tx, ty);
                g2.dispose();
            }
        };
        avatar.setOpaque(false);
        avatar.setPreferredSize(new Dimension(diametro, diametro));
        avatar.setMinimumSize(new Dimension(diametro, diametro));
        avatar.setMaximumSize(new Dimension(diametro, diametro));
        avatar.putClientProperty("texto", texto);
        avatar.putClientProperty("fondo", fondo);
        avatar.putClientProperty("textoColor", textoColor);
        return avatar;
    }

    private JLabel crearBadge(String texto, Color fondo, Color textoColor) {
        JLabel badge = new JLabel(texto) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        badge.setFont(new Font("Segoe UI", Font.BOLD, 10));
        badge.setForeground(textoColor);
        badge.setBackground(fondo);
        badge.setOpaque(false);
        badge.setBorder(new EmptyBorder(3, 8, 3, 8));
        return badge;
    }

    private JButton crearBotonAccion(String texto, boolean primario) {
        JButton btn = new JButton(texto) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (primario) {
                    g2.setColor(getModel().isRollover() ? COLOR_TURQUESA_OSCURO : COLOR_TURQUESA);
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
        btn.setForeground(primario ? Color.WHITE : COLOR_TITULO);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(btn.getPreferredSize().width + 16, 28));
        btn.setBorder(new EmptyBorder(4, 10, 4, 10));
        return btn;
    }

    // =========================================================================
    // 7. RENDERER DE LISTA (TARJETA DE TURNO / CITA)
    // =========================================================================

    private class CitaCellRenderer extends JPanel implements ListCellRenderer<Cita> {
        private static final long serialVersionUID = 1L;
        private final JLabel lblHora;
        private final JLabel lblAvatar;
        private final JLabel lblNombreMascota;
        private final JLabel lblDetalleDueno;
        private final JLabel lblServicioDoctor;
        private final JLabel lblBadgeEstado;
        private final JLabel lblBadgePrioridad;

        public CitaCellRenderer() {
            setLayout(new BorderLayout(8, 2));
            setBorder(new EmptyBorder(8, 8, 8, 8));
            setOpaque(true);

            // Hora a la izquierda con badge
            JPanel izq = new JPanel(new BorderLayout());
            izq.setOpaque(false);

            lblHora = new JLabel("", SwingConstants.CENTER) {
                private static final long serialVersionUID = 1L;
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(241, 245, 249));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                    g2.setColor(COLOR_BORDE);
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 6, 6);
                    super.paintComponent(g2);
                    g2.dispose();
                }
            };
            lblHora.setFont(new Font("Segoe UI", Font.BOLD, 11));
            lblHora.setForeground(COLOR_TITULO);
            lblHora.setPreferredSize(new Dimension(54, 26));
            izq.add(lblHora, BorderLayout.NORTH);

            // Avatar circular de la mascota
            lblAvatar = new JLabel("", SwingConstants.CENTER) {
                private static final long serialVersionUID = 1L;
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    int d = Math.min(getWidth(), getHeight()) - 2;
                    int x = (getWidth() - d) / 2;
                    int y = (getHeight() - d) / 2;
                    g2.setColor(COLOR_TURQUESA_SUAVE);
                    g2.fillOval(x, y, d, d);
                    g2.setColor(COLOR_TURQUESA_OSCURO);
                    g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
                    FontMetrics fm = g2.getFontMetrics();
                    String t = getText();
                    int tx = x + (d - fm.stringWidth(t)) / 2;
                    int ty = y + (d - fm.getHeight()) / 2 + fm.getAscent();
                    g2.drawString(t, tx, ty);
                    g2.dispose();
                }
            };
            lblAvatar.setPreferredSize(new Dimension(32, 32));
            lblAvatar.setOpaque(false);

            JPanel avatarWrap = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 4));
            avatarWrap.setOpaque(false);
            avatarWrap.add(lblAvatar);
            izq.add(avatarWrap, BorderLayout.CENTER);

            add(izq, BorderLayout.WEST);

            // Textos centrales
            JPanel center = new JPanel();
            center.setOpaque(false);
            center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));

            lblNombreMascota = new JLabel();
            lblNombreMascota.setFont(new Font("Segoe UI", Font.BOLD, 12));
            lblNombreMascota.setForeground(COLOR_TITULO);

            lblDetalleDueno = new JLabel();
            lblDetalleDueno.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            lblDetalleDueno.setForeground(new Color(100, 116, 139));

            lblServicioDoctor = new JLabel();
            lblServicioDoctor.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            lblServicioDoctor.setForeground(COLOR_MUTED);

            center.add(lblNombreMascota);
            center.add(Box.createVerticalStrut(1));
            center.add(lblDetalleDueno);
            center.add(Box.createVerticalStrut(1));
            center.add(lblServicioDoctor);
            add(center, BorderLayout.CENTER);

            // Badges a la derecha
            JPanel der = new JPanel(new GridLayout(2, 1, 0, 4));
            der.setOpaque(false);
            der.setPreferredSize(new Dimension(90, 46));

            lblBadgeEstado = crearBadge("Programada", new Color(238, 242, 255), new Color(79, 70, 229));
            lblBadgePrioridad = crearBadge("Normal", new Color(241, 245, 249), COLOR_MUTED);
            lblBadgeEstado.setHorizontalAlignment(SwingConstants.CENTER);
            lblBadgePrioridad.setHorizontalAlignment(SwingConstants.CENTER);

            der.add(lblBadgeEstado);
            der.add(lblBadgePrioridad);
            add(der, BorderLayout.EAST);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends Cita> list, Cita c, int index, boolean isSelected, boolean cellHasFocus) {
            if (c != null) {
                lblHora.setText(c.getHoraFormateada());
                String ini = c.getNombreMascota() != null && !c.getNombreMascota().isEmpty() ? c.getNombreMascota().substring(0, 1).toUpperCase() : "P";
                lblAvatar.setText(ini);

                lblNombreMascota.setText(c.getNombreMascota() + " (" + c.getIdCita() + ")");
                lblDetalleDueno.setText(c.getEspecieRaza() + " · Dueño: " + c.getNombreCliente());
                lblServicioDoctor.setText(c.getTipoServicio() + " · " + c.getVeterinario());

                // Estado
                lblBadgeEstado.setText(c.getEstado());
                if ("Confirmada".equalsIgnoreCase(c.getEstado())) {
                    lblBadgeEstado.setBackground(new Color(220, 252, 231));
                    lblBadgeEstado.setForeground(new Color(22, 163, 74));
                } else if ("En Sala de Espera".equalsIgnoreCase(c.getEstado())) {
                    lblBadgeEstado.setBackground(new Color(254, 243, 199));
                    lblBadgeEstado.setForeground(new Color(217, 119, 6));
                } else if ("Atendida".equalsIgnoreCase(c.getEstado())) {
                    lblBadgeEstado.setBackground(new Color(241, 245, 249));
                    lblBadgeEstado.setForeground(COLOR_MUTED);
                } else {
                    lblBadgeEstado.setBackground(new Color(238, 242, 255));
                    lblBadgeEstado.setForeground(new Color(79, 70, 229));
                }

                // Prioridad
                lblBadgePrioridad.setText(c.getPrioridad());
                if ("Emergencia".equalsIgnoreCase(c.getPrioridad())) {
                    lblBadgePrioridad.setBackground(new Color(254, 226, 226));
                    lblBadgePrioridad.setForeground(new Color(220, 38, 38));
                } else if ("Urgente".equalsIgnoreCase(c.getPrioridad())) {
                    lblBadgePrioridad.setBackground(new Color(254, 243, 199));
                    lblBadgePrioridad.setForeground(new Color(217, 119, 6));
                } else {
                    lblBadgePrioridad.setBackground(new Color(241, 245, 249));
                    lblBadgePrioridad.setForeground(COLOR_MUTED);
                }
            }

            if (isSelected) {
                setBackground(COLOR_TURQUESA_SUAVE);
                setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(COLOR_TURQUESA, 1),
                        new EmptyBorder(7, 7, 7, 7)
                ));
            } else {
                setBackground(Color.WHITE);
                setBorder(new EmptyBorder(8, 8, 8, 8));
            }
            return this;
        }
    }
}
