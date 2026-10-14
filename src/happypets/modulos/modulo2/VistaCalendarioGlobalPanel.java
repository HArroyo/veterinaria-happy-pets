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
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.Scrollable;
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
 * Submódulo 2.2: Calendario Global de Citas y Turnos de la Clínica.
 * Arquitectura Opción A: Matriz fluida de ocupación de espacios (100% sin scroll horizontal),
 * control de turno en vivo interactivo, KPIs de ocupación diaria y avatares perfectamente circulares.
 */
public class VistaCalendarioGlobalPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private static final Color COLOR_BORDE = Ui.BORDE_SUAVE;
    private static final Color COLOR_AZUL_PRIMARIO = Ui.TURQUESA;
    private static final Color COLOR_AZUL_OSCURO = Ui.TURQUESA_OSCURO;
    private static final Color COLOR_AZUL_SUAVE = Ui.TURQUESA_SUAVE;
    private static final Color COLOR_TEXTO_TITULO = Ui.TEXTO_TITULO;
    private static final Color COLOR_TEXTO_MUTED = Ui.TEXTO_MUTED;

    private final RepositorioVeterinaria repo = RepositorioVeterinaria.getInstancia();

    private LocalDate fechaSeleccionada = LocalDate.now();
    private YearMonth mesVisualizado = YearMonth.now();
    private String filtroJornada = "TODO"; // "TODO", "MANANA", "TARDE"
    private String filtroTexto = "";
    private Cita citaSeleccionada = null;

    // Componentes de KPIs superiores
    private JLabel lblKpiOcupacionVal;
    private JLabel lblKpiOcupacionSub;
    private JLabel lblKpiTurnosVal;
    private JLabel lblKpiEspaciosVal;
    private JLabel lblKpiLibresVal;

    // Componentes del mini-calendario y panel lateral
    private JLabel lblMesAnio;
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private JPanel gridDiasMes;
    private JLabel lblInfoDiaSeleccionado;
    private JLabel lblTotalCitasDia;
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private JPanel panelMedidoresOcupacion;
    private JButton btnFiltroTodo;
    private JButton btnFiltroManana;
    private JButton btnFiltroTarde;

    // Matriz de horarios y control
    private JLabel lblTituloFechaMatriz;
    private JComboBox<String> cbRecurso;
    private JTextField txtBuscarMatriz;
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private JPanel panelAgendaHorarios;
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private JPanel cardControlTurno;

    private final String[] HORAS_TODAS = {
            "08:00", "09:00", "10:00", "11:00", "12:00",
            "14:00", "15:00", "16:00", "17:00", "18:00", "19:00"
    };
    private final String[] HORAS_MANANA = {
            "08:00", "09:00", "10:00", "11:00", "12:00"
    };
    private final String[] HORAS_TARDE = {
            "14:00", "15:00", "16:00", "17:00", "18:00", "19:00"
    };

    public static final String[] RECURSOS_COLUMNAS = {
            "Consultorio 1 (Dr. Mendoza)",
            "Consultorio 2 (Dra. Morales)",
            "Quirófano Principal",
            "Área de Grooming"
    };

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private static class ScrollablePanel extends JPanel implements Scrollable {
        private static final long serialVersionUID = 1L;
        @Override public Dimension getPreferredScrollableViewportSize() { return getPreferredSize(); }
        @Override public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) { return 16; }
        @Override public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) { return 48; }
        @Override public boolean getScrollableTracksViewportWidth() { return true; }
        @Override public boolean getScrollableTracksViewportHeight() { return false; }
    }

    /**
     * Constructor de VistaCalendarioGlobalPanel.
     * Inicializa los componentes gráficos, configura layouts, asigna estilos
     * institucionales y carga los datos desde el repositorio central.
     */
    public VistaCalendarioGlobalPanel() {
        setLayout(new BorderLayout());
        setOpaque(false);

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel contenido = new JPanel();
        contenido.setOpaque(false);
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setBorder(new EmptyBorder(12, 18, 14, 18));

        // 1. Cabecera principal con botones de acción rápida
        contenido.add(crearCabeceraVista());
        contenido.add(Box.createVerticalStrut(8));

        // 2. Fila superior de 4 KPIs en vivo
        contenido.add(crearFilaKpisCalendario());
        contenido.add(Box.createVerticalStrut(10));

        // 3. Contenedor principal: Panel lateral inteligente (290px) + Matriz y Dock de turno (Centro)
        contenido.add(crearContenedorPrincipal());

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(contenido, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(wrapper);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);

        // Carga inicial sincronizada
        recargarDatos();
    }

    // =========================================================================
    // 1. CABECERA Y KPIS SUPERIORES
    // =========================================================================

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private JPanel crearCabeceraVista() {
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel cab = new JPanel(new BorderLayout());
        cab.setOpaque(false);
        cab.setPreferredSize(new Dimension(0, 42));
        cab.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel izq = new JPanel();
        izq.setOpaque(false);
        izq.setLayout(new BoxLayout(izq, BoxLayout.Y_AXIS));

        JLabel lblTit = new JLabel("Calendario Global de Turnos y Espacios");
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblTit.setForeground(COLOR_TEXTO_TITULO);

        JLabel lblSub = new JLabel("Módulo 2.2 · Monitoreo y ocupación en tiempo real de consultorios, quirófano y grooming");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblSub.setForeground(COLOR_TEXTO_MUTED);

        izq.add(lblTit);
        izq.add(Box.createVerticalStrut(2));
        izq.add(lblSub);
        cab.add(izq, BorderLayout.CENTER);

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 2));
        der.setOpaque(false);

        JButton btnHoy = crearBotonAccion("Ir a Hoy", false);
        btnHoy.setIcon(Iconos.crearIconoCalendario(13, COLOR_AZUL_PRIMARIO));
        btnHoy.setIconTextGap(6);
        btnHoy.addActionListener(e -> {
            fechaSeleccionada = LocalDate.now();
            mesVisualizado = YearMonth.now();
            recargarDatos();
        });

        JButton btnActualizar = crearBotonAccion("Actualizar", false);
        btnActualizar.setIcon(Iconos.crearIconoCheck(13, new Color(22, 163, 74)));
        btnActualizar.setIconTextGap(6);
        btnActualizar.addActionListener(e -> recargarDatos());

        JButton btnNuevoTurno = crearBotonAccion("+ Reservar Turno", true);
        btnNuevoTurno.setIcon(Iconos.crearIconoCalendario(14, Color.WHITE));
        btnNuevoTurno.setIconTextGap(6);
        btnNuevoTurno.addActionListener(e -> abrirFormularioReserva(null, null));

        der.add(btnHoy);
        der.add(btnActualizar);
        der.add(btnNuevoTurno);
        cab.add(der, BorderLayout.EAST);

        return cab;
    }

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private JPanel crearFilaKpisCalendario() {
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel fila = new JPanel(new GridLayout(1, 4, 10, 0));
        fila.setOpaque(false);
        fila.setPreferredSize(new Dimension(0, 56));
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));

        lblKpiOcupacionVal = new JLabel("0%");
        lblKpiOcupacionSub = new JLabel("0 de 0 turnos");
        lblKpiTurnosVal = new JLabel("0 citas");
        lblKpiEspaciosVal = new JLabel("4 Áreas");
        lblKpiLibresVal = new JLabel("0 libres");

        fila.add(crearCardKpi(lblKpiOcupacionVal, "OCUPACIÓN GLOBAL DIARIA", lblKpiOcupacionSub, new Color(224, 242, 254), Iconos.crearIconoReloj(18, COLOR_AZUL_PRIMARIO)));
        fila.add(crearCardKpi(lblKpiTurnosVal, "CITAS AGENDADAS EN FECHA", new JLabel("En fecha seleccionada"), new Color(220, 252, 231), Iconos.crearIconoCalendario(18, new Color(22, 163, 74))));
        fila.add(crearCardKpi(lblKpiEspaciosVal, "ESPACIOS OPERATIVOS", new JLabel("4 de 4 salas activas"), new Color(254, 243, 199), Iconos.crearIconoCheck(18, new Color(217, 119, 6))));
        fila.add(crearCardKpi(lblKpiLibresVal, "HORARIOS DISPONIBLES", new JLabel("Franjas listas para agendar"), new Color(243, 232, 255), Iconos.crearIconoAlertaTriaje(18, new Color(147, 51, 234))));

        return fila;
    }

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private JPanel crearCardKpi(JLabel lblVal, String etiqueta, JLabel lblSub, Color bgIco, Icon icono) {
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel card = new JPanel(new BorderLayout(10, 0)) {
            private static final long serialVersionUID = 1L;
            @Override
            /**
             * Renderizado gráfico personalizado en Java2D.
             * Activa Antialiasing para bordes suaves y realiza el cálculo geométrico preciso.
             */
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
            /**
             * Renderizado gráfico personalizado en Java2D.
             * Activa Antialiasing para bordes suaves y realiza el cálculo geométrico preciso.
             */
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

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel pText = new JPanel();
        pText.setOpaque(false);
        pText.setLayout(new BoxLayout(pText, BoxLayout.Y_AXIS));

        JLabel lblEtq = new JLabel(etiqueta);
        lblEtq.setFont(new Font("Segoe UI", Font.BOLD, 9));
        lblEtq.setForeground(COLOR_TEXTO_MUTED);

        lblVal.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblVal.setForeground(COLOR_TEXTO_TITULO);

        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblSub.setForeground(COLOR_TEXTO_MUTED);

        pText.add(lblEtq);
        pText.add(Box.createVerticalStrut(1));
        pText.add(lblVal);
        pText.add(Box.createVerticalStrut(1));
        pText.add(lblSub);

        card.add(pText, BorderLayout.CENTER);
        return card;
    }

    // =========================================================================
    // 2. CONTENEDOR PRINCIPAL (SPLIT IZQUIERDO Y DERECHO)
    // =========================================================================

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private JPanel crearContenedorPrincipal() {
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel split = new JPanel(new BorderLayout(14, 0));
        split.setOpaque(false);

        // Panel Lateral Izquierdo (290px)
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel colIzq = new JPanel();
        colIzq.setOpaque(false);
        colIzq.setLayout(new BoxLayout(colIzq, BoxLayout.Y_AXIS));
        colIzq.setPreferredSize(new Dimension(290, 0));

        colIzq.add(crearTarjetaMiniCalendario());
        colIzq.add(Box.createVerticalStrut(10));
        colIzq.add(crearTarjetaMedidoresOcupacion());
        colIzq.add(Box.createVerticalStrut(10));
        colIzq.add(crearTarjetaFiltrosYLeyenda());

        split.add(colIzq, BorderLayout.WEST);

        // Panel Central / Derecho: Matriz fluida + Dock de Control de Turno
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel colDer = new JPanel(new BorderLayout(0, 10));
        colDer.setOpaque(false);

        colDer.add(crearTarjetaMatrizAgenda(), BorderLayout.CENTER);
        colDer.add(crearTarjetaDockControlTurno(), BorderLayout.SOUTH);

        split.add(colDer, BorderLayout.CENTER);

        return split;
    }

    // =========================================================================
    // 3. PANEL LATERAL: MINI CALENDARIO, MEDIDORES Y LEYENDA
    // =========================================================================

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private JPanel crearTarjetaMiniCalendario() {
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel card = new JPanel(new BorderLayout(0, 8)) {
            private static final long serialVersionUID = 1L;
            @Override
            /**
             * Renderizado gráfico personalizado en Java2D.
             * Activa Antialiasing para bordes suaves y realiza el cálculo geométrico preciso.
             */
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
        card.setBorder(new EmptyBorder(10, 12, 10, 12));

        // Cabecera del mes con botones < >
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel navMes = new JPanel(new BorderLayout(4, 0));
        navMes.setOpaque(false);

        JButton btnAnt = crearBotonMini("<");
        btnAnt.addActionListener(e -> {
            mesVisualizado = mesVisualizado.minusMonths(1);
            actualizarMiniCalendario();
        });

        lblMesAnio = new JLabel("", SwingConstants.CENTER);
        lblMesAnio.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblMesAnio.setForeground(COLOR_TEXTO_TITULO);

        JButton btnSig = crearBotonMini(">");
        btnSig.addActionListener(e -> {
            mesVisualizado = mesVisualizado.plusMonths(1);
            actualizarMiniCalendario();
        });

        navMes.add(btnAnt, BorderLayout.WEST);
        navMes.add(lblMesAnio, BorderLayout.CENTER);
        navMes.add(btnSig, BorderLayout.EAST);
        card.add(navMes, BorderLayout.NORTH);

        // Cuadrícula del mes
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel pnlCal = new JPanel(new BorderLayout(0, 4));
        pnlCal.setOpaque(false);

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel pnlSemana = new JPanel(new GridLayout(1, 7, 2, 2));
        pnlSemana.setOpaque(false);
        String[] diasCortos = {"Lu", "Ma", "Mi", "Ju", "Vi", "Sá", "Do"};
        for (String d : diasCortos) {
            JLabel l = new JLabel(d, SwingConstants.CENTER);
            l.setFont(new Font("Segoe UI", Font.BOLD, 10));
            l.setForeground(COLOR_TEXTO_MUTED);
            pnlSemana.add(l);
        }
        pnlCal.add(pnlSemana, BorderLayout.NORTH);

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        gridDiasMes = new JPanel(new GridLayout(6, 7, 2, 2));
        gridDiasMes.setOpaque(false);
        pnlCal.add(gridDiasMes, BorderLayout.CENTER);

        card.add(pnlCal, BorderLayout.CENTER);

        // Resumen inferior
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel bot = new JPanel(new BorderLayout());
        bot.setOpaque(false);
        bot.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, COLOR_BORDE),
                new EmptyBorder(6, 2, 2, 2)
        ));

        lblInfoDiaSeleccionado = new JLabel("Fecha: " + fechaSeleccionada.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        lblInfoDiaSeleccionado.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblInfoDiaSeleccionado.setForeground(COLOR_TEXTO_TITULO);

        lblTotalCitasDia = new JLabel("0 turnos", SwingConstants.RIGHT);
        lblTotalCitasDia.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblTotalCitasDia.setForeground(COLOR_AZUL_OSCURO);

        bot.add(lblInfoDiaSeleccionado, BorderLayout.WEST);
        bot.add(lblTotalCitasDia, BorderLayout.EAST);
        card.add(bot, BorderLayout.SOUTH);

        return card;
    }

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private JPanel crearTarjetaMedidoresOcupacion() {
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel card = new JPanel(new BorderLayout(0, 6)) {
            private static final long serialVersionUID = 1L;
            @Override
            /**
             * Renderizado gráfico personalizado en Java2D.
             * Activa Antialiasing para bordes suaves y realiza el cálculo geométrico preciso.
             */
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
        card.setBorder(new EmptyBorder(10, 12, 10, 12));

        JLabel lblTit = new JLabel("Ocupación por Espacio");
        lblTit.setIcon(Iconos.crearIconoReloj(13, COLOR_AZUL_PRIMARIO));
        lblTit.setIconTextGap(6);
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblTit.setForeground(COLOR_TEXTO_TITULO);
        card.add(lblTit, BorderLayout.NORTH);

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        panelMedidoresOcupacion = new JPanel();
        panelMedidoresOcupacion.setOpaque(false);
        panelMedidoresOcupacion.setLayout(new BoxLayout(panelMedidoresOcupacion, BoxLayout.Y_AXIS));

        card.add(panelMedidoresOcupacion, BorderLayout.CENTER);
        return card;
    }

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private JPanel crearTarjetaFiltrosYLeyenda() {
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel card = new JPanel(new BorderLayout(0, 8)) {
            private static final long serialVersionUID = 1L;
            @Override
            /**
             * Renderizado gráfico personalizado en Java2D.
             * Activa Antialiasing para bordes suaves y realiza el cálculo geométrico preciso.
             */
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
        card.setBorder(new EmptyBorder(10, 12, 10, 12));

        // Segmented Control de Jornada
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel pFiltro = new JPanel(new BorderLayout(0, 4));
        pFiltro.setOpaque(false);

        JLabel lblJor = new JLabel("Filtro de Franja Horaria:");
        lblJor.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblJor.setForeground(COLOR_TEXTO_MUTED);
        pFiltro.add(lblJor, BorderLayout.NORTH);

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel pPills = new JPanel(new GridLayout(1, 3, 4, 0));
        pPills.setOpaque(false);

        btnFiltroTodo = crearBotonPill("Todo", true);
        btnFiltroManana = crearBotonPill("Mañana", false);
        btnFiltroTarde = crearBotonPill("Tarde", false);

        btnFiltroTodo.addActionListener(e -> setFiltroJornada("TODO"));
        btnFiltroManana.addActionListener(e -> setFiltroJornada("MANANA"));
        btnFiltroTarde.addActionListener(e -> setFiltroJornada("TARDE"));

        pPills.add(btnFiltroTodo);
        pPills.add(btnFiltroManana);
        pPills.add(btnFiltroTarde);
        pFiltro.add(pPills, BorderLayout.CENTER);

        card.add(pFiltro, BorderLayout.NORTH);

        // Leyenda de Espacios
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel pnlItems = new JPanel();
        pnlItems.setOpaque(false);
        pnlItems.setLayout(new BoxLayout(pnlItems, BoxLayout.Y_AXIS));
        pnlItems.setBorder(new EmptyBorder(4, 0, 0, 0));

        pnlItems.add(crearFilaLeyenda(new Color(224, 242, 254), new Color(2, 132, 199), "Consultorio 1 · Dr. Mendoza"));
        pnlItems.add(Box.createVerticalStrut(4));
        pnlItems.add(crearFilaLeyenda(new Color(220, 252, 231), new Color(22, 163, 74), "Consultorio 2 · Dra. Morales"));
        pnlItems.add(Box.createVerticalStrut(4));
        pnlItems.add(crearFilaLeyenda(new Color(243, 232, 255), new Color(147, 51, 234), "Quirófano · Cirugías"));
        pnlItems.add(Box.createVerticalStrut(4));
        pnlItems.add(crearFilaLeyenda(new Color(254, 243, 199), new Color(217, 119, 6), "Grooming · Baño y Corte"));

        card.add(pnlItems, BorderLayout.CENTER);
        return card;
    }

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private JPanel crearFilaLeyenda(Color bg, Color text, String etiqueta) {
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        p.setOpaque(false);

        JLabel dot = new JLabel() {
            private static final long serialVersionUID = 1L;
            @Override
            /**
             * Renderizado gráfico personalizado en Java2D.
             * Activa Antialiasing para bordes suaves y realiza el cálculo geométrico preciso.
             */
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fillOval(0, 0, 10, 10);
                g2.setColor(text);
                g2.drawOval(0, 0, 9, 9);
                g2.dispose();
            }
        };
        dot.setPreferredSize(new Dimension(10, 10));

        JLabel lbl = new JLabel(etiqueta);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lbl.setForeground(new Color(71, 85, 105));

        p.add(dot);
        p.add(lbl);
        return p;
    }

    // =========================================================================
    // 4. MATRIZ DE ESPACIOS FLUIDA Y HERRAMIENTAS (SIN SCROLL HORIZONTAL)
    // =========================================================================

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private JPanel crearTarjetaMatrizAgenda() {
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel card = new JPanel(new BorderLayout(0, 8)) {
            private static final long serialVersionUID = 1L;
            @Override
            /**
             * Renderizado gráfico personalizado en Java2D.
             * Activa Antialiasing para bordes suaves y realiza el cálculo geométrico preciso.
             */
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
        card.setBorder(new EmptyBorder(10, 12, 10, 12));

        // Barra de Herramientas superior de la matriz
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel top = new JPanel(new BorderLayout(10, 0));
        top.setOpaque(false);
        top.setPreferredSize(new Dimension(0, 36));

        lblTituloFechaMatriz = new JLabel();
        lblTituloFechaMatriz.setIcon(Iconos.crearIconoCalendario(15, COLOR_AZUL_PRIMARIO));
        lblTituloFechaMatriz.setIconTextGap(6);
        lblTituloFechaMatriz.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTituloFechaMatriz.setForeground(COLOR_TEXTO_TITULO);
        top.add(lblTituloFechaMatriz, BorderLayout.WEST);

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 2));
        der.setOpaque(false);

        // Buscador reactivo
        txtBuscarMatriz = Ui.campoTexto(16);
        txtBuscarMatriz.setPreferredSize(new Dimension(180, 28));
        txtBuscarMatriz.setToolTipText("Buscar paciente, dueño, veterinario o servicio...");
        txtBuscarMatriz.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { filtrarPorTexto(); }
            @Override public void removeUpdate(DocumentEvent e) { filtrarPorTexto(); }
            @Override public void changedUpdate(DocumentEvent e) { filtrarPorTexto(); }
        });

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel pnlBusqueda = new JPanel(new BorderLayout(4, 0));
        pnlBusqueda.setOpaque(false);
        JLabel lblLupa = new JLabel(Iconos.crearIconoBuscar(14, COLOR_TEXTO_MUTED));
        pnlBusqueda.add(lblLupa, BorderLayout.WEST);
        pnlBusqueda.add(txtBuscarMatriz, BorderLayout.CENTER);
        der.add(pnlBusqueda);

        // Selector de recurso
        cbRecurso = new JComboBox<>(new String[]{
                "Todos los espacios (4 áreas)",
                "Consultorio 1 (Dr. Mendoza)",
                "Consultorio 2 (Dra. Morales)",
                "Quirófano Principal",
                "Área de Grooming"
        });
        cbRecurso.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        cbRecurso.setPreferredSize(new Dimension(190, 28));
        cbRecurso.setBackground(Color.WHITE);
        cbRecurso.addActionListener(e -> actualizarMatrizHorarios());
        der.add(cbRecurso);

        top.add(der, BorderLayout.EAST);
        card.add(top, BorderLayout.NORTH);

        // Panel de agenda dinámica con Scrollable que fuerza el ancho del viewport (0 scroll horizontal)
        panelAgendaHorarios = new ScrollablePanel();
        panelAgendaHorarios.setOpaque(false);
        panelAgendaHorarios.setLayout(new BoxLayout(panelAgendaHorarios, BoxLayout.Y_AXIS));

        JScrollPane sp = new JScrollPane(panelAgendaHorarios);
        sp.setBorder(BorderFactory.createLineBorder(COLOR_BORDE, 1));
        sp.setPreferredSize(new Dimension(600, 360));
        sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        sp.getVerticalScrollBar().setUnitIncrement(16);
        card.add(sp, BorderLayout.CENTER);

        return card;
    }

    /**
     * Aplica criterios de filtrado dinámico en memoria sobre los registros.
     * Evalúa texto de búsqueda (búsqueda predictiva) y filtros por categoría o estado.
     */
    private void filtrarPorTexto() {
        filtroTexto = txtBuscarMatriz.getText();
        actualizarMatrizHorarios();
    }

    // =========================================================================
    // 5. DOCK DE CONTROL DE TURNO EN VIVO (PANEL INFERIOR)
    // =========================================================================

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private JPanel crearTarjetaDockControlTurno() {
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        cardControlTurno = new JPanel(new BorderLayout(0, 6)) {
            private static final long serialVersionUID = 1L;
            @Override
            /**
             * Renderizado gráfico personalizado en Java2D.
             * Activa Antialiasing para bordes suaves y realiza el cálculo geométrico preciso.
             */
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
        cardControlTurno.setOpaque(false);
        cardControlTurno.setBorder(new EmptyBorder(10, 14, 10, 14));
        cardControlTurno.setPreferredSize(new Dimension(0, 130));

        actualizarDockControlTurno();
        return cardControlTurno;
    }

    private void actualizarDockControlTurno() {
        cardControlTurno.removeAll();

        if (citaSeleccionada == null) {
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
            JPanel vacio = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 36));
            vacio.setOpaque(false);

            JLabel lblIco = new JLabel(Iconos.crearIconoReloj(20, COLOR_AZUL_PRIMARIO));
            JLabel lblTxt = new JLabel("Haga clic sobre cualquier cita en la matriz para abrir el Centro de Control de Turno en Vivo o en '+ Disponible' para agendar.");
            lblTxt.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            lblTxt.setForeground(COLOR_TEXTO_MUTED);

            vacio.add(lblIco);
            vacio.add(lblTxt);
            cardControlTurno.add(vacio, BorderLayout.CENTER);
        } else {
            Cita c = citaSeleccionada;
            String rec = resolverRecursoCita(c);

            // Fila Superior: Avatar circular + Datos Paciente + Badges Micro-KPI
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
            JPanel top = new JPanel(new BorderLayout(10, 0));
            top.setOpaque(false);

            // Avatar circular paciente
            Color bgAv = getColorAvatarBgRecurso(rec);
            Color fgAv = getColorAvatarTextoRecurso(rec);
            JLabel avatar = crearAvatarCircular(extraerIniciales(c.getNombreMascota()), 42, bgAv, fgAv);

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
            JPanel avWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
            avWrapper.setOpaque(false);
            avWrapper.add(avatar);
            top.add(avWrapper, BorderLayout.WEST);

            // Textos paciente
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
            JPanel infoPac = new JPanel();
            infoPac.setOpaque(false);
            infoPac.setLayout(new BoxLayout(infoPac, BoxLayout.Y_AXIS));

            JLabel lblNom = new JLabel(c.getNombreMascota() + " (" + c.getEspecieRaza() + ")");
            lblNom.setFont(new Font("Segoe UI", Font.BOLD, 13));
            lblNom.setForeground(COLOR_TEXTO_TITULO);

            JLabel lblSub = new JLabel("Tutor: " + c.getNombreCliente() + "  ·  Tel: " + c.getTelefonoCliente() + "  ·  Dr(a): " + c.getVeterinario());
            lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            lblSub.setForeground(COLOR_TEXTO_MUTED);

            infoPac.add(lblNom);
            infoPac.add(Box.createVerticalStrut(2));
            infoPac.add(lblSub);
            top.add(infoPac, BorderLayout.CENTER);

            // Badges a la derecha
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
            JPanel pBadges = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 2));
            pBadges.setOpaque(false);

            JLabel bHora = crearBadgePastilla(c.getHoraFormateada() + " hrs", new Color(241, 245, 249), COLOR_TEXTO_TITULO);
            JLabel bEspacio = crearBadgePastilla(rec, getColorFondoRecurso(rec), getColorBordeRecurso(rec));
            JLabel bEstado = crearBadgePastilla(c.getEstado(), getFondoEstado(c.getEstado()), getTextoEstado(c.getEstado()));

            pBadges.add(bHora);
            pBadges.add(bEspacio);
            pBadges.add(bEstado);
            top.add(pBadges, BorderLayout.EAST);

            cardControlTurno.add(top, BorderLayout.NORTH);

            // Fila Media: Motivo y prioridad
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
            JPanel mid = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 4));
            mid.setOpaque(false);

            JLabel lblMotivo = new JLabel("Motivo Clínico: " + c.getMotivo() + "  |  Prioridad: " + c.getPrioridad());
            lblMotivo.setFont(new Font("Segoe UI", Font.ITALIC, 11));
            lblMotivo.setForeground(new Color(71, 85, 105));
            mid.add(lblMotivo);
            cardControlTurno.add(mid, BorderLayout.CENTER);

            // Fila Inferior: Acciones Operativas Inmediatas
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
            JPanel bot = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
            bot.setOpaque(false);

            JButton btnEspera = crearBotonAccion("Pasar a Sala de Espera", false);
            btnEspera.setIcon(Iconos.crearIconoReloj(12, new Color(217, 119, 6)));
            btnEspera.setIconTextGap(4);
            btnEspera.addActionListener(e -> transferirASalaEspera(c));

            JButton btnWhatsapp = crearBotonAccion("Notificar WhatsApp", false);
            btnWhatsapp.setIcon(Iconos.crearIconoWhatsApp(12, new Color(22, 163, 74)));
            btnWhatsapp.setIconTextGap(4);
            btnWhatsapp.addActionListener(e -> notificarWhatsApp(c));

            JButton btnReprog = crearBotonAccion("Reprogramar", false);
            btnReprog.setIcon(Iconos.crearIconoCalendario(12, COLOR_AZUL_PRIMARIO));
            btnReprog.setIconTextGap(4);
            btnReprog.addActionListener(e -> reprogramarTurno(c));

            JButton btnCancelar = crearBotonAccion("Cancelar Turno", false);
            btnCancelar.setIcon(Iconos.crearIconoAlertaTriaje(12, new Color(220, 38, 38)));
            btnCancelar.setIconTextGap(4);
            btnCancelar.addActionListener(e -> cancelarTurno(c));

            bot.add(btnEspera);
            bot.add(btnWhatsapp);
            bot.add(btnReprog);
            bot.add(btnCancelar);

            cardControlTurno.add(bot, BorderLayout.SOUTH);
        }

        cardControlTurno.revalidate();
        cardControlTurno.repaint();
    }

    // =========================================================================
    // 6. ACCIONES OPERATIVAS Y NEGOCIO
    // =========================================================================

    private void transferirASalaEspera(Cita cita) {
        if (cita == null) return;
        cita.setEstado("En Sala de Espera");
        repo.guardarCita(cita);

        PacienteTriaje pt = new PacienteTriaje(
                null, cita.getIdCita(), cita.getCodigoMascota(), cita.getNombreMascota(),
                cita.getEspecieRaza(), cita.getNombreCliente(), cita.getTelefonoCliente(),
                LocalTime.now(), 20.0, 38.5, 90,
                cita.getPrioridad().equalsIgnoreCase("Emergencia") ? "ROJO (Emergencia crítica)" :
                cita.getPrioridad().equalsIgnoreCase("Urgente") ? "AMARILLO (Urgencia)" : "VERDE (Normal)",
                cita.getMotivo(),
                resolverRecursoCita(cita),
                "En Espera"
        );
        repo.agregarPacienteTriaje(pt);

        JOptionPane.showMessageDialog(this,
                "El paciente " + cita.getNombreMascota() + " ha sido ingresado a Sala de Espera con ticket " + pt.getIdTicket() + ".\nPuede monitorear sus constantes en el submódulo 'Sala de Espera y Triaje'.",
                "Paciente en Triaje", JOptionPane.INFORMATION_MESSAGE);

        recargarDatos();
    }

    private void notificarWhatsApp(Cita cita) {
        if (cita == null) return;
        cita.setEstado("Confirmada");
        repo.guardarCita(cita);

        JOptionPane.showMessageDialog(this,
                "Mensaje de WhatsApp enviado a " + cita.getNombreCliente() + " (" + cita.getTelefonoCliente() + "):\n\n" +
                "\"Hola " + cita.getNombreCliente() + ", te recordamos el turno de " + cita.getNombreMascota() +
                " programado para el " + cita.getFechaFormateada() + " a las " + cita.getHoraFormateada() + " hrs en " +
                resolverRecursoCita(cita) + ".\"",
                "Notificación WhatsApp", JOptionPane.INFORMATION_MESSAGE);

        recargarDatos();
    }

    private void reprogramarTurno(Cita cita) {
        if (cita == null) return;
        Window parent = SwingUtilities.getWindowAncestor(this);
        FormularioReservaCitaDialog dlg = new FormularioReservaCitaDialog(parent, cita, citaModificada -> {
            recargarDatos();
            seleccionarCita(citaModificada);
            JOptionPane.showMessageDialog(this,
                    "Turno " + citaModificada.getIdCita() + " reprogramado exitosamente.",
                    "Turno Actualizado", JOptionPane.INFORMATION_MESSAGE);
        });
        dlg.setVisible(true);
    }

    private void cancelarTurno(Cita cita) {
        if (cita == null) return;
        int opt = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de que desea cancelar el turno " + cita.getIdCita() + " de " + cita.getNombreMascota() + "?\nEsta acción liberará el espacio horario.",
                "Confirmar Cancelación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (opt == JOptionPane.YES_OPTION) {
            cita.setEstado("Cancelada");
            repo.guardarCita(cita);
            citaSeleccionada = null;
            recargarDatos();
            JOptionPane.showMessageDialog(this, "El turno ha sido cancelado y el espacio queda disponible.", "Turno Cancelado", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void abrirFormularioReserva(String hora, String espacio) {
        Window parent = SwingUtilities.getWindowAncestor(this);
        FormularioReservaCitaDialog dlg = new FormularioReservaCitaDialog(parent, null, nuevaCita -> {
            recargarDatos();
            seleccionarCita(nuevaCita);
            JOptionPane.showMessageDialog(this,
                    "Turno agendado exitosamente para " + nuevaCita.getNombreMascota() + ".",
                    "Turno Confirmado", JOptionPane.INFORMATION_MESSAGE);
        });
        if (hora != null || espacio != null) {
            dlg.preseleccionarDatos(fechaSeleccionada, hora, espacio);
        }
        dlg.setVisible(true);
    }

    public void seleccionarCita(Cita c) {
        this.citaSeleccionada = c;
        actualizarDockControlTurno();
        panelAgendaHorarios.repaint();
    }

    // =========================================================================
    // 7. ACTUALIZACIÓN REACTIVA DE DATOS
    // =========================================================================

    public void recargarDatos() {
        actualizarKpis();
        actualizarMiniCalendario();
        actualizarMedidoresOcupacion();
        actualizarMatrizHorarios();

        // Auto-seleccionar primer turno si no hay uno seleccionado para poblar el dock
        List<Cita> citasDia = repo.getCitasPorFecha(fechaSeleccionada).stream()
                .filter(c -> !"Cancelada".equalsIgnoreCase(c.getEstado()))
                .collect(Collectors.toList());
        if (citaSeleccionada == null && !citasDia.isEmpty()) {
            citaSeleccionada = citasDia.get(0);
        } else if (citaSeleccionada != null && !citasDia.contains(citaSeleccionada)) {
            citaSeleccionada = citasDia.isEmpty() ? null : citasDia.get(0);
        }
        actualizarDockControlTurno();
    }

    private void actualizarKpis() {
        List<Cita> citasDia = repo.getCitasPorFecha(fechaSeleccionada).stream()
                .filter(c -> !"Cancelada".equalsIgnoreCase(c.getEstado()))
                .collect(Collectors.toList());

        String[] slotsHorarios = "MANANA".equals(filtroJornada) ? HORAS_MANANA :
                                 "TARDE".equals(filtroJornada) ? HORAS_TARDE : HORAS_TODAS;

        int totalEspacios = RECURSOS_COLUMNAS.length;
        int totalSlots = totalEspacios * slotsHorarios.length;
        int citasOcupadas = Math.min(totalSlots, citasDia.size());
        int porcentaje = totalSlots > 0 ? (citasOcupadas * 100) / totalSlots : 0;
        int libres = Math.max(0, totalSlots - citasOcupadas);

        lblKpiOcupacionVal.setText(porcentaje + "%");
        lblKpiOcupacionSub.setText(citasOcupadas + " de " + totalSlots + " turnos");
        lblKpiTurnosVal.setText(citasDia.size() + (citasDia.size() == 1 ? " cita" : " citas"));
        lblKpiEspaciosVal.setText("4 Áreas");
        lblKpiLibresVal.setText(libres + " libres");
    }

    private void actualizarMiniCalendario() {
        gridDiasMes.removeAll();
        lblMesAnio.setText(formatearMesAnio(mesVisualizado));

        LocalDate primeroMes = mesVisualizado.atDay(1);
        int diaSemanaPrimero = primeroMes.getDayOfWeek().getValue(); // 1 = Lunes, 7 = Domingo
        int diasEnMes = mesVisualizado.lengthOfMonth();

        for (int i = 1; i < diaSemanaPrimero; i++) {
            gridDiasMes.add(new JLabel(""));
        }

        List<Cita> citasDelRepo = repo.getCitas();

        for (int d = 1; d <= diasEnMes; d++) {
            LocalDate f = mesVisualizado.atDay(d);
            boolean esSeleccionado = f.equals(fechaSeleccionada);
            boolean esHoy = f.equals(LocalDate.now());
            long citasEnEsteDia = citasDelRepo.stream()
                    .filter(c -> c.getFecha().equals(f) && !"Cancelada".equalsIgnoreCase(c.getEstado()))
                    .count();

            JLabel lblDia = new JLabel(String.valueOf(d), SwingConstants.CENTER) {
                private static final long serialVersionUID = 1L;
                @Override
            /**
             * Renderizado gráfico personalizado en Java2D.
             * Activa Antialiasing para bordes suaves y realiza el cálculo geométrico preciso.
             */
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    if (esSeleccionado) {
                        g2.setColor(COLOR_AZUL_PRIMARIO);
                        g2.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 6, 6);
                    } else if (esHoy) {
                        g2.setColor(COLOR_AZUL_SUAVE);
                        g2.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 6, 6);
                        g2.setColor(COLOR_AZUL_PRIMARIO);
                        g2.drawRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 6, 6);
                    } else if (citasEnEsteDia > 0) {
                        g2.setColor(new Color(241, 245, 249));
                        g2.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 6, 6);
                    }
                    super.paintComponent(g);
                    if (citasEnEsteDia > 0 && !esSeleccionado) {
                        g2.setColor(COLOR_AZUL_PRIMARIO);
                        g2.fillOval(getWidth() / 2 - 2, getHeight() - 5, 4, 4);
                    }
                    g2.dispose();
                }
            };
            lblDia.setFont(new Font("Segoe UI", esSeleccionado ? Font.BOLD : Font.PLAIN, 10));
            lblDia.setForeground(esSeleccionado ? Color.WHITE : COLOR_TEXTO_TITULO);
            lblDia.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            lblDia.setPreferredSize(new Dimension(26, 24));

            lblDia.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    fechaSeleccionada = f;
                    citaSeleccionada = null;
                    recargarDatos();
                }
            });

            gridDiasMes.add(lblDia);
        }

        int totalCeldas = (diaSemanaPrimero - 1) + diasEnMes;
        int restantes = (7 - (totalCeldas % 7)) % 7;
        for (int i = 0; i < restantes; i++) {
            gridDiasMes.add(new JLabel(""));
        }

        gridDiasMes.revalidate();
        gridDiasMes.repaint();

        lblInfoDiaSeleccionado.setText("Fecha: " + fechaSeleccionada.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        long citasDia = citasDelRepo.stream()
                .filter(c -> c.getFecha().equals(fechaSeleccionada) && !"Cancelada".equalsIgnoreCase(c.getEstado()))
                .count();
        lblTotalCitasDia.setText(citasDia + (citasDia == 1 ? " turno" : " turnos"));
    }

    private void actualizarMedidoresOcupacion() {
        panelMedidoresOcupacion.removeAll();

        List<Cita> citasDia = repo.getCitasPorFecha(fechaSeleccionada).stream()
                .filter(c -> !"Cancelada".equalsIgnoreCase(c.getEstado()))
                .collect(Collectors.toList());

        String[] slots = "MANANA".equals(filtroJornada) ? HORAS_MANANA :
                         "TARDE".equals(filtroJornada) ? HORAS_TARDE : HORAS_TODAS;
        int totalSlotsPorEspacio = slots.length;

        for (String rec : RECURSOS_COLUMNAS) {
            long citasRec = citasDia.stream().filter(c -> resolverRecursoCita(c).equalsIgnoreCase(rec)).count();
            int pct = totalSlotsPorEspacio > 0 ? (int) ((citasRec * 100) / totalSlotsPorEspacio) : 0;
            Color colorBarra = getColorBordeRecurso(rec);

            panelMedidoresOcupacion.add(crearBarraProgresoEspacio(rec, (int) citasRec, totalSlotsPorEspacio, pct, colorBarra));
            panelMedidoresOcupacion.add(Box.createVerticalStrut(4));
        }

        panelMedidoresOcupacion.revalidate();
        panelMedidoresOcupacion.repaint();
    }

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private JPanel crearBarraProgresoEspacio(String nombre, int ocupados, int total, int porcentaje, Color colorBarra) {
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel p = new JPanel(new BorderLayout(0, 2));
        p.setOpaque(false);

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel pLabels = new JPanel(new BorderLayout());
        pLabels.setOpaque(false);

        String nombreCorto = nombre.split("·")[0].split("\\(")[0].trim();
        JLabel lblNom = new JLabel(nombreCorto);
        lblNom.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblNom.setForeground(new Color(51, 65, 85));

        JLabel lblVal = new JLabel(ocupados + "/" + total + " (" + porcentaje + "%)");
        lblVal.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblVal.setForeground(colorBarra);

        pLabels.add(lblNom, BorderLayout.WEST);
        pLabels.add(lblVal, BorderLayout.EAST);
        p.add(pLabels, BorderLayout.NORTH);

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JComponent progressBar = new JComponent() {
            private static final long serialVersionUID = 1L;
            @Override
            /**
             * Renderizado gráfico personalizado en Java2D.
             * Activa Antialiasing para bordes suaves y realiza el cálculo geométrico preciso.
             */
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(241, 245, 249));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 4, 4);
                if (porcentaje > 0) {
                    int anchoLleno = Math.max(4, (getWidth() * porcentaje) / 100);
                    g2.setColor(colorBarra);
                    g2.fillRoundRect(0, 0, anchoLleno, getHeight(), 4, 4);
                }
                g2.dispose();
            }
        };
        progressBar.setPreferredSize(new Dimension(0, 5));
        p.add(progressBar, BorderLayout.CENTER);

        return p;
    }

    private void actualizarMatrizHorarios() {
        panelAgendaHorarios.removeAll();

        lblTituloFechaMatriz.setText("Turnos del " + formatearFechaCompleta(fechaSeleccionada));

        List<Cita> citasDia = repo.getCitasPorFecha(fechaSeleccionada).stream()
                .filter(c -> !"Cancelada".equalsIgnoreCase(c.getEstado()))
                .collect(Collectors.toList());

        String filtroRecurso = cbRecurso != null ? (String) cbRecurso.getSelectedItem() : "Todos";

        List<String> columnasAMostrar = new ArrayList<>();
        if (filtroRecurso == null || filtroRecurso.startsWith("Todos")) {
            for (String col : RECURSOS_COLUMNAS) columnasAMostrar.add(col);
        } else {
            columnasAMostrar.add(filtroRecurso);
        }

        String[] slotsHorarios = "MANANA".equals(filtroJornada) ? HORAS_MANANA :
                                 "TARDE".equals(filtroJornada) ? HORAS_TARDE : HORAS_TODAS;

        // 1. Fila Cabecera con nombres de salas
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel filaCab = new JPanel(new BorderLayout(8, 0));
        filaCab.setBackground(new Color(241, 245, 249));
        filaCab.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        filaCab.setBorder(new EmptyBorder(4, 8, 4, 8));

        JLabel lblH = new JLabel("Hora", SwingConstants.CENTER);
        lblH.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblH.setForeground(COLOR_TEXTO_MUTED);
        lblH.setPreferredSize(new Dimension(65, 24));
        filaCab.add(lblH, BorderLayout.WEST);

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel pCols = new JPanel(new GridLayout(1, columnasAMostrar.size(), 6, 0));
        pCols.setOpaque(false);
        for (String col : columnasAMostrar) {
            JLabel lblC = new JLabel(col, SwingConstants.CENTER);
            lblC.setFont(new Font("Segoe UI", Font.BOLD, 11));
            lblC.setForeground(COLOR_TEXTO_TITULO);
            pCols.add(lblC);
        }
        filaCab.add(pCols, BorderLayout.CENTER);
        panelAgendaHorarios.add(filaCab);
        panelAgendaHorarios.add(Box.createVerticalStrut(4));

        // 2. Filas de horarios
        for (String slotHora : slotsHorarios) {
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
            JPanel filaSlot = new JPanel(new BorderLayout(8, 0));
            filaSlot.setOpaque(false);
            filaSlot.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
            filaSlot.setBorder(new EmptyBorder(2, 6, 2, 6));

            JLabel lblHora = new JLabel(slotHora, SwingConstants.CENTER);
            lblHora.setFont(new Font("Segoe UI", Font.BOLD, 11));
            lblHora.setForeground(new Color(71, 85, 105));
            lblHora.setPreferredSize(new Dimension(65, 38));
            filaSlot.add(lblHora, BorderLayout.WEST);

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
            JPanel pCeldas = new JPanel(new GridLayout(1, columnasAMostrar.size(), 6, 0));
            pCeldas.setOpaque(false);
            for (String rec : columnasAMostrar) {
                Cita citaEnSlot = buscarCitaEnSlot(citasDia, slotHora, rec, filtroTexto);
                pCeldas.add(crearCeldaSlot(slotHora, rec, citaEnSlot));
            }
            filaSlot.add(pCeldas, BorderLayout.CENTER);

            panelAgendaHorarios.add(filaSlot);
            panelAgendaHorarios.add(Box.createVerticalStrut(3));
        }

        panelAgendaHorarios.revalidate();
        panelAgendaHorarios.repaint();
    }

    private Cita buscarCitaEnSlot(List<Cita> citas, String slotHora, String recurso, String query) {
        int horaSlot = Integer.parseInt(slotHora.split(":")[0]);
        for (Cita c : citas) {
            if (c.getHora().getHour() == horaSlot) {
                String recCita = resolverRecursoCita(c);
                if (recCita.equalsIgnoreCase(recurso)) {
                    if (query == null || query.trim().isEmpty()) {
                        return c;
                    }
                    String q = query.trim().toLowerCase();
                    boolean match = (c.getNombreMascota() != null && c.getNombreMascota().toLowerCase().contains(q)) ||
                                    (c.getNombreCliente() != null && c.getNombreCliente().toLowerCase().contains(q)) ||
                                    (c.getVeterinario() != null && c.getVeterinario().toLowerCase().contains(q)) ||
                                    (c.getTipoServicio() != null && c.getTipoServicio().toLowerCase().contains(q)) ||
                                    (c.getMotivo() != null && c.getMotivo().toLowerCase().contains(q));
                    if (match) return c;
                }
            }
        }
        return null;
    }

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private JPanel crearCeldaSlot(String hora, String recurso, Cita cita) {
        if (cita == null) {
            // Slot Disponible
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
            JPanel libre = new JPanel(new BorderLayout()) {
                private static final long serialVersionUID = 1L;
                @Override
            /**
             * Renderizado gráfico personalizado en Java2D.
             * Activa Antialiasing para bordes suaves y realiza el cálculo geométrico preciso.
             */
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(248, 250, 252));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                    g2.setColor(new Color(226, 232, 240));
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 6, 6);
                    super.paintComponent(g2);
                    g2.dispose();
                }
            };
            libre.setOpaque(false);
            libre.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            libre.setBorder(new EmptyBorder(4, 6, 4, 6));

            JLabel lblDisp = new JLabel("+ Disponible", SwingConstants.CENTER);
            lblDisp.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            lblDisp.setForeground(new Color(148, 163, 184));
            libre.add(lblDisp, BorderLayout.CENTER);

            libre.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    abrirFormularioReserva(hora, recurso);
                }
            });
            return libre;
        }

        // Slot Ocupado
        boolean esSeleccionada = (citaSeleccionada != null && citaSeleccionada.getIdCita().equals(cita.getIdCita()));
        Color bgCard = getColorFondoRecurso(recurso);
        Color borderCard = getColorBordeRecurso(recurso);
        Color bgAvatar = getColorAvatarBgRecurso(recurso);
        Color fgAvatar = getColorAvatarTextoRecurso(recurso);

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel ocupado = new JPanel(new BorderLayout(6, 0)) {
            private static final long serialVersionUID = 1L;
            @Override
            /**
             * Renderizado gráfico personalizado en Java2D.
             * Activa Antialiasing para bordes suaves y realiza el cálculo geométrico preciso.
             */
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bgCard);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);

                if (esSeleccionada) {
                    g2.setColor(COLOR_AZUL_PRIMARIO);
                    g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 6, 6);
                    g2.setColor(borderCard);
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 6, 6);
                } else {
                    g2.setColor(borderCard);
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 6, 6);
                }
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        ocupado.setOpaque(false);
        ocupado.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        ocupado.setBorder(new EmptyBorder(3, 6, 3, 6));

        // Avatar perfectamente circular con panel contenedor para evitar estiramiento
        JLabel av = crearAvatarCircular(extraerIniciales(cita.getNombreMascota()), 26, bgAvatar, fgAvatar);
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel avWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 4));
        avWrapper.setOpaque(false);
        avWrapper.add(av);
        ocupado.add(avWrapper, BorderLayout.WEST);

        // Textos del turno
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel pInfo = new JPanel();
        pInfo.setOpaque(false);
        pInfo.setLayout(new BoxLayout(pInfo, BoxLayout.Y_AXIS));

        JLabel lblNom = new JLabel(cita.getNombreMascota() + " · " + abreviarServicio(cita.getTipoServicio()));
        lblNom.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblNom.setForeground(COLOR_TEXTO_TITULO);

        JLabel lblDuenio = new JLabel(cita.getNombreCliente() + " (" + cita.getEstado() + ")");
        lblDuenio.setFont(new Font("Segoe UI", Font.PLAIN, 9));
        lblDuenio.setForeground(COLOR_TEXTO_MUTED);

        pInfo.add(lblNom);
        pInfo.add(lblDuenio);
        ocupado.add(pInfo, BorderLayout.CENTER);

        ocupado.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                seleccionarCita(cita);
            }
        });

        return ocupado;
    }

    private void setFiltroJornada(String jornada) {
        this.filtroJornada = jornada;
        btnFiltroTodo.putClientProperty("activo", "TODO".equals(jornada));
        btnFiltroManana.putClientProperty("activo", "MANANA".equals(jornada));
        btnFiltroTarde.putClientProperty("activo", "TARDE".equals(jornada));
        btnFiltroTodo.repaint();
        btnFiltroManana.repaint();
        btnFiltroTarde.repaint();
        recargarDatos();
    }

    // =========================================================================
    // 8. HELPERS ESTÉTICOS Y FORMATEO
    // =========================================================================

    public static String resolverRecursoCita(Cita c) {
        if (c == null) return "Consultorio 1 (Dr. Mendoza)";
        String s = c.getTipoServicio() != null ? c.getTipoServicio().toLowerCase() : "";
        String v = c.getVeterinario() != null ? c.getVeterinario().toLowerCase() : "";
        if (s.contains("cirug") || s.contains("quir")) return "Quirófano Principal";
        if (s.contains("groom") || s.contains("peluq") || s.contains("baño")) return "Área de Grooming";
        if (v.contains("morales") || v.contains("silva") || s.contains("vacun")) return "Consultorio 2 (Dra. Morales)";
        return "Consultorio 1 (Dr. Mendoza)";
    }

    public static Color getColorFondoRecurso(String recurso) {
        if (recurso.contains("Quirófano")) return new Color(243, 232, 255);
        if (recurso.contains("Grooming")) return new Color(254, 243, 199);
        if (recurso.contains("Consultorio 2")) return new Color(220, 252, 231);
        return new Color(224, 242, 254);
    }

    public static Color getColorBordeRecurso(String recurso) {
        if (recurso.contains("Quirófano")) return new Color(147, 51, 234);
        if (recurso.contains("Grooming")) return new Color(217, 119, 6);
        if (recurso.contains("Consultorio 2")) return new Color(22, 163, 74);
        return new Color(2, 132, 199);
    }

    public static Color getColorAvatarBgRecurso(String recurso) {
        if (recurso.contains("Quirófano")) return new Color(233, 213, 255);
        if (recurso.contains("Grooming")) return new Color(253, 230, 138);
        if (recurso.contains("Consultorio 2")) return new Color(187, 247, 208);
        return new Color(186, 230, 253);
    }

    public static Color getColorAvatarTextoRecurso(String recurso) {
        if (recurso.contains("Quirófano")) return new Color(126, 34, 206);
        if (recurso.contains("Grooming")) return new Color(180, 83, 9);
        if (recurso.contains("Consultorio 2")) return new Color(21, 128, 61);
        return new Color(3, 105, 161);
    }

    public static String extraerIniciales(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) return "HP";
        String[] partes = nombre.trim().split("\\s+");
        if (partes.length >= 2) {
            return ("" + partes[0].charAt(0) + partes[1].charAt(0)).toUpperCase();
        } else if (nombre.length() >= 2) {
            return nombre.substring(0, 2).toUpperCase();
        }
        return nombre.toUpperCase();
    }

    public static JLabel crearAvatarCircular(String texto, int tamano, Color bg, Color fg) {
        JLabel lbl = new JLabel(texto, SwingConstants.CENTER) {
            private static final long serialVersionUID = 1L;
            @Override
            /**
             * Renderizado gráfico personalizado en Java2D.
             * Activa Antialiasing para bordes suaves y realiza el cálculo geométrico preciso.
             */
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int d = Math.min(getWidth(), getHeight()) - 2;
                int x = (getWidth() - d) / 2;
                int y = (getHeight() - d) / 2;
                g2.setColor(bg);
                g2.fillOval(x, y, d, d);
                g2.setColor(new Color(fg.getRed(), fg.getGreen(), fg.getBlue(), 80));
                g2.drawOval(x, y, d, d);
                g2.setColor(fg);
                g2.setFont(new Font("Segoe UI", Font.BOLD, tamano >= 40 ? 15 : 10));
                FontMetrics fm = g2.getFontMetrics();
                String t = getText();
                int tx = x + (d - fm.stringWidth(t)) / 2;
                int ty = y + (d - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(t, tx, ty);
                g2.dispose();
            }
        };
        lbl.setPreferredSize(new Dimension(tamano, tamano));
        lbl.setMinimumSize(new Dimension(tamano, tamano));
        lbl.setMaximumSize(new Dimension(tamano, tamano));
        lbl.setOpaque(false);
        return lbl;
    }

    private JLabel crearBadgePastilla(String texto, Color bg, Color fg) {
        JLabel l = new JLabel(texto, SwingConstants.CENTER) {
            private static final long serialVersionUID = 1L;
            @Override
            /**
             * Renderizado gráfico personalizado en Java2D.
             * Activa Antialiasing para bordes suaves y realiza el cálculo geométrico preciso.
             */
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        l.setFont(new Font("Segoe UI", Font.BOLD, 10));
        l.setForeground(fg);
        l.setBorder(new EmptyBorder(2, 8, 2, 8));
        return l;
    }

    private Color getFondoEstado(String estado) {
        if ("Confirmada".equalsIgnoreCase(estado)) return new Color(220, 252, 231);
        if ("En Sala de Espera".equalsIgnoreCase(estado)) return new Color(254, 243, 199);
        if ("Atendida".equalsIgnoreCase(estado)) return new Color(241, 245, 249);
        if ("Cancelada".equalsIgnoreCase(estado)) return new Color(254, 226, 226);
        return new Color(224, 242, 254);
    }

    private Color getTextoEstado(String estado) {
        if ("Confirmada".equalsIgnoreCase(estado)) return new Color(22, 163, 74);
        if ("En Sala de Espera".equalsIgnoreCase(estado)) return new Color(217, 119, 6);
        if ("Atendida".equalsIgnoreCase(estado)) return new Color(100, 116, 139);
        if ("Cancelada".equalsIgnoreCase(estado)) return new Color(220, 38, 38);
        return new Color(2, 132, 199);
    }

    private String abreviarServicio(String serv) {
        if (serv == null) return "Consulta";
        if (serv.contains("Cirugía")) return "Cirugía";
        if (serv.contains("Grooming")) return "Grooming";
        if (serv.contains("Vacuna")) return "Vacuna";
        if (serv.contains("Control")) return "Control";
        return serv;
    }

    private String formatearMesAnio(YearMonth ym) {
        String[] meses = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
                          "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
        return meses[ym.getMonthValue() - 1] + " " + ym.getYear();
    }

    private String formatearFechaCompleta(LocalDate d) {
        String[] dias = {"Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"};
        String[] meses = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
                          "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
        String nomDia = dias[d.getDayOfWeek().getValue() - 1];
        String nomMes = meses[d.getMonthValue() - 1];
        return nomDia + ", " + d.getDayOfMonth() + " de " + nomMes + " de " + d.getYear();
    }

    private JButton crearBotonMini(String texto) {
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
                g2.setColor(getModel().isRollover() ? new Color(241, 245, 249) : Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g2.setColor(COLOR_BORDE);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 6, 6);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setForeground(new Color(51, 65, 85));
        btn.setPreferredSize(new Dimension(32, 24));
        btn.setMargin(new java.awt.Insets(0, 0, 0, 0));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(1, 4, 1, 4));
        return btn;
    }

    private JButton crearBotonPill(String texto, boolean activo) {
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
                boolean act = Boolean.TRUE.equals(getClientProperty("activo"));
                if (act) {
                    g2.setColor(COLOR_AZUL_PRIMARIO);
                } else if (getModel().isRollover()) {
                    g2.setColor(new Color(241, 245, 249));
                } else {
                    g2.setColor(Color.WHITE);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g2.setColor(act ? COLOR_AZUL_PRIMARIO : COLOR_BORDE);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 6, 6);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        btn.putClientProperty("activo", activo);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 10));
        btn.setForeground(activo ? Color.WHITE : new Color(71, 85, 105));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(0, 24));
        return btn;
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
                    g2.setColor(getModel().isRollover() ? COLOR_AZUL_OSCURO : COLOR_AZUL_PRIMARIO);
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

            @Override
            public Dimension getPreferredSize() {
                Dimension d = super.getPreferredSize();
                return new Dimension(d.width + 14, 28);
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
        return btn;
    }
}
