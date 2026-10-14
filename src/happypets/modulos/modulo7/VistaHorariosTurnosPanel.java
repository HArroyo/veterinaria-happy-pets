package happypets.modulos.modulo7;

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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import happypets.data.RepositorioVeterinaria;
import happypets.model.TurnoSemanal;
import happypets.ui.Iconos;
import happypets.ui.Ui;

/**
 * Submódulo 7.3: Cuadrante de Guardias - Horarios y Turnos Médicos.
 * Arquitectura Master-Detail (Opción A):
 * - Master Matrix: Cuadrante semanal con días de atención, turnos rotativos y leyenda.
 * - Live Shift Control Dock (360px): Telemetría 2x2, desglose por día, reemplazos y acciones 1-click.
 */
public class VistaHorariosTurnosPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private final RepositorioVeterinaria repo = RepositorioVeterinaria.getInstancia();
    private LocalDate fechaSemanaActual = LocalDate.now();

    // KPIs
    private JLabel lblKpiCobertura;
    private JLabel lblKpiManana;
    private JLabel lblKpiTarde;
    private JLabel lblKpiNoche;

    // Navegación Semana
    private JLabel lblSemanaRango;

    // Master Table
    private JTable tablaCuadrante;
    private DefaultTableModel modeloTabla;
    private JTextField txtBuscar;
    private String filtroEspecialidad = "TODOS";
    private final List<TurnoSemanal> listaTurnosFiltrada = new ArrayList<>();

    // Live Shift Control Dock (Derecha - 360px)
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private JPanel panelHubContenido;
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private JPanel panelHubVacio;
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private JPanel panelHubDetalle;

    private JLabel lblHubAvatar;
    private JLabel lblHubNombre;
    private JLabel lblHubEspecialidad;
    private JLabel lblHubChipCobertura;

    // Telemetría 2x2
    private JLabel lblHubHorasSemanales;
    private JLabel lblHubDiasActivos;
    private JLabel lblHubConsultorio;
    private JLabel lblHubGuardiasNoche;

    // Secciones de Detalle
    private JTextArea txtHubDesgloseSemanal;
    private JTextArea txtHubRespaldo;
    private JTextArea txtHubObservaciones;

    private TurnoSemanal turnoSeleccionado;

    /**
     * Constructor de VistaHorariosTurnosPanel.
     * Inicializa los componentes gráficos, configura layouts, asigna estilos
     * institucionales y carga los datos desde el repositorio central.
     */
    public VistaHorariosTurnosPanel() {
        setLayout(new BorderLayout());
        setOpaque(false);

        // Panel Superior Fijo (Cabecera + Navegación + 4 KPIs)
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel panelSuperior = new JPanel();
        panelSuperior.setOpaque(false);
        panelSuperior.setLayout(new BoxLayout(panelSuperior, BoxLayout.Y_AXIS));
        panelSuperior.setBorder(new EmptyBorder(12, 18, 10, 18));

        panelSuperior.add(crearCabeceraVista());
        panelSuperior.add(Box.createVerticalStrut(8));
        panelSuperior.add(crearBarraSemana());
        panelSuperior.add(Box.createVerticalStrut(10));
        panelSuperior.add(crearFilaKpis());

        add(panelSuperior, BorderLayout.NORTH);

        // Contenedor Master-Detail
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel panelCentro = new JPanel(new BorderLayout());
        panelCentro.setOpaque(false);
        panelCentro.setBorder(new EmptyBorder(0, 18, 14, 18));
        panelCentro.add(crearSplitMasterDetail(), BorderLayout.CENTER);

        add(panelCentro, BorderLayout.CENTER);

        recargarDatos();
    }

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

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel izq = new JPanel();
        izq.setOpaque(false);
        izq.setLayout(new BoxLayout(izq, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("Cuadrante Semanal de Guardias y Turnos Médicos");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 17));
        titulo.setForeground(Ui.TEXTO_TITULO);

        JLabel subtitulo = new JLabel("Módulo 7.3 · Distribución horaria, roles asistenciales, asignación de consultorios y urgencias");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitulo.setForeground(Ui.TEXTO_MUTED);

        izq.add(titulo);
        izq.add(Box.createVerticalStrut(2));
        izq.add(subtitulo);
        cab.add(izq, BorderLayout.WEST);

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        der.setOpaque(false);

        JButton btnAsignar = Ui.boton("+ Asignar Turno", true);
        btnAsignar.setIcon(Iconos.crearIconoTurno(14, Color.WHITE));
        btnAsignar.addActionListener(e -> mostrarDialogoAsignarTurno());
        der.add(btnAsignar);

        JButton btnPDF = Ui.boton("Descargar PDF", false);
        btnPDF.setIcon(Iconos.crearIconoDocumento(14, Ui.TEXTO_MUTED));
        btnPDF.addActionListener(e -> exportarCuadrantePDF());
        der.add(btnPDF);

        JButton btnActualizar = Ui.boton("Actualizar", false);
        btnActualizar.addActionListener(e -> recargarDatos());
        der.add(btnActualizar);

        cab.add(der, BorderLayout.EAST);
        cab.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        return cab;
    }

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private JPanel crearBarraSemana() {
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        barra.setOpaque(false);
        barra.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));

        JButton btnAnt = new JButton("<");
        btnAnt.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnAnt.setFocusPainted(false);
        btnAnt.setBackground(Color.WHITE);
        btnAnt.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Ui.BORDE_SUAVE, 1, true),
                new EmptyBorder(3, 10, 3, 10)
        ));
        btnAnt.addActionListener(e -> {
            fechaSemanaActual = fechaSemanaActual.minusWeeks(1);
            actualizarTextoSemana();
            recargarDatos();
        });

        JButton btnHoy = new JButton("Semana Actual");
        btnHoy.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnHoy.setFocusPainted(false);
        btnHoy.setBackground(Color.WHITE);
        btnHoy.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Ui.BORDE_SUAVE, 1, true),
                new EmptyBorder(3, 12, 3, 12)
        ));
        btnHoy.addActionListener(e -> {
            fechaSemanaActual = LocalDate.now();
            actualizarTextoSemana();
            recargarDatos();
        });

        JButton btnSig = new JButton(">");
        btnSig.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnSig.setFocusPainted(false);
        btnSig.setBackground(Color.WHITE);
        btnSig.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Ui.BORDE_SUAVE, 1, true),
                new EmptyBorder(3, 10, 3, 10)
        ));
        btnSig.addActionListener(e -> {
            fechaSemanaActual = fechaSemanaActual.plusWeeks(1);
            actualizarTextoSemana();
            recargarDatos();
        });

        lblSemanaRango = new JLabel();
        lblSemanaRango.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblSemanaRango.setForeground(Ui.TURQUESA_PROFUNDO);
        actualizarTextoSemana();

        barra.add(btnAnt);
        barra.add(btnHoy);
        barra.add(btnSig);
        barra.add(Box.createHorizontalStrut(6));
        barra.add(lblSemanaRango);

        return barra;
    }

    private void actualizarTextoSemana() {
        LocalDate lunes = fechaSemanaActual.with(java.time.DayOfWeek.MONDAY);
        LocalDate domingo = lunes.plusDays(6);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("d 'de' MMMM", new Locale("es", "ES"));
        lblSemanaRango.setText("Rol Operativo: " + lunes.format(dtf) + " al " + domingo.format(dtf) + " (" + lunes.getYear() + ")");
    }

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private JPanel crearFilaKpis() {
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel fila = new JPanel(new GridLayout(1, 4, 12, 0));
        fila.setOpaque(false);
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 74));

        lblKpiCobertura = new JLabel("98.5%");
        lblKpiManana = new JLabel("6 médicos");
        lblKpiTarde = new JLabel("5 médicos");
        lblKpiNoche = new JLabel("2 en guardia");

        fila.add(crearCardKpi("Cobertura Semanal", lblKpiCobertura, "Disponibilidad de consultorios", Iconos.crearIconoCheck(22, Ui.COLOR_EXITO)));
        fila.add(crearCardKpi("Turno Mañana (08:00 - 15:00)", lblKpiManana, "Plazas asistenciales cubiertas", Iconos.crearIconoTurno(22, Ui.TURQUESA)));
        fila.add(crearCardKpi("Turno Tarde (14:30 - 22:00)", lblKpiTarde, "Consultas y procedimientos", Iconos.crearIconoTurno(22, Ui.TURQUESA_OSCURO)));
        fila.add(crearCardKpi("Guardia Urgencias (21:30 - 07:30)", lblKpiNoche, "Atención continua 24 horas", Iconos.crearIconoAlertaTriaje(22, new Color(217, 119, 6))));

        return fila;
    }

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private JPanel crearCardKpi(String titulo, JLabel lblValor, String subtitulo, javax.swing.Icon icono) {
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel card = new JPanel(new BorderLayout(8, 0)) {
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
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(Ui.BORDE_SUAVE);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(10, 14, 10, 14));

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel texto = new JPanel();
        texto.setOpaque(false);
        texto.setLayout(new BoxLayout(texto, BoxLayout.Y_AXIS));

        JLabel lblTit = new JLabel(titulo);
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblTit.setForeground(Ui.TEXTO_MUTED);

        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblValor.setForeground(Ui.TEXTO_TITULO);

        JLabel lblSub = new JLabel(subtitulo);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblSub.setForeground(Ui.TEXTO_MUTED);

        texto.add(lblTit);
        texto.add(Box.createVerticalStrut(2));
        texto.add(lblValor);
        texto.add(Box.createVerticalStrut(2));
        texto.add(lblSub);

        card.add(texto, BorderLayout.CENTER);
        if (icono != null) {
            JLabel lblIco = new JLabel(icono);
            lblIco.setVerticalAlignment(SwingConstants.TOP);
            card.add(lblIco, BorderLayout.EAST);
        }

        return card;
    }

    private JSplitPane crearSplitMasterDetail() {
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel master = crearPanelMasterTable();
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel detail = crearPanelLiveHub();
        detail.setPreferredSize(new Dimension(360, 0));
        detail.setMinimumSize(new Dimension(320, 0));

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, master, detail);
        split.setResizeWeight(1.0);
        split.setDividerSize(6);
        split.setBorder(null);
        split.setOpaque(false);
        return split;
    }

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private JPanel crearPanelMasterTable() {
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel master = new JPanel(new BorderLayout(0, 10)) {
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
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(Ui.BORDE_SUAVE);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                g2.dispose();
            }
        };
        master.setOpaque(false);
        master.setBorder(new EmptyBorder(12, 14, 12, 14));

        // Cabecera Filtros
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel cabFiltros = new JPanel();
        cabFiltros.setOpaque(false);
        cabFiltros.setLayout(new BoxLayout(cabFiltros, BoxLayout.Y_AXIS));

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel barraBuscar = new JPanel(new BorderLayout(8, 0));
        barraBuscar.setOpaque(false);

        JLabel lblBuscar = new JLabel("Buscar:");
        lblBuscar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblBuscar.setForeground(Ui.TEXTO_MUTED);
        barraBuscar.add(lblBuscar, BorderLayout.WEST);

        txtBuscar = new JTextField();
        txtBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtBuscar.putClientProperty("JTextField.placeholderText", "Buscar especialista, especialidad o colegiatura en el cuadrante...");
        txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filtrarTurnos(); }
            public void removeUpdate(DocumentEvent e) { filtrarTurnos(); }
            public void changedUpdate(DocumentEvent e) { filtrarTurnos(); }
        });
        barraBuscar.add(txtBuscar, BorderLayout.CENTER);
        cabFiltros.add(barraBuscar);
        cabFiltros.add(Box.createVerticalStrut(8));

        // Píldoras de Filtro
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel pills = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        pills.setOpaque(false);

        String[] especialidades = {"TODOS", "Cirugía General", "Dermatología", "Medicina Felina", "Cardiología", "Urgencias"};
        for (String esp : especialidades) {
            JButton btnPill = new JButton(esp);
            btnPill.setFont(new Font("Segoe UI", Font.BOLD, 11));
            btnPill.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btnPill.setBorder(new EmptyBorder(4, 10, 4, 10));
            btnPill.setFocusPainted(false);

            if (esp.equals(filtroEspecialidad)) {
                btnPill.setBackground(Ui.TURQUESA);
                btnPill.setForeground(Color.WHITE);
            } else {
                btnPill.setBackground(new Color(241, 245, 249));
                btnPill.setForeground(Ui.TEXTO_MUTED);
            }

            btnPill.addActionListener(e -> {
                filtroEspecialidad = esp;
                for (Component comp : pills.getComponents()) {
                    if (comp instanceof JButton) {
                        JButton b = (JButton) comp;
                        if (b.getText().equals(filtroEspecialidad)) {
                            b.setBackground(Ui.TURQUESA);
                            b.setForeground(Color.WHITE);
                        } else {
                            b.setBackground(new Color(241, 245, 249));
                            b.setForeground(Ui.TEXTO_MUTED);
                        }
                    }
                }
                filtrarTurnos();
            });
            pills.add(btnPill);
        }
        cabFiltros.add(pills);
        master.add(cabFiltros, BorderLayout.NORTH);

        // Tabla Cuadrante Matriz
        String[] cols = {"Especialista", "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"};
        modeloTabla = new DefaultTableModel(cols, 0) {
            private static final long serialVersionUID = 1L;
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tablaCuadrante = new JTable(modeloTabla);
        tablaCuadrante.setRowHeight(48);
        tablaCuadrante.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        tablaCuadrante.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaCuadrante.setShowVerticalLines(false);
        tablaCuadrante.setGridColor(new Color(241, 245, 249));
        tablaCuadrante.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        tablaCuadrante.getTableHeader().setBackground(new Color(248, 250, 252));
        tablaCuadrante.getTableHeader().setForeground(Ui.TEXTO_MUTED);

        tablaCuadrante.getColumnModel().getColumn(0).setPreferredWidth(190);
        for (int i = 1; i <= 7; i++) {
            tablaCuadrante.getColumnModel().getColumn(i).setPreferredWidth(105);
        }

        // Monograma en columna 0 (Especialista)
        tablaCuadrante.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
            private static final long serialVersionUID = 1L;
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
                JPanel cell = new JPanel(new BorderLayout(8, 0));
                cell.setOpaque(true);
                cell.setBackground(isSelected ? table.getSelectionBackground() : (row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252)));
                cell.setBorder(new EmptyBorder(0, 8, 0, 8));

                String nombre = value != null ? value.toString() : "";
                String iniciales = obtenerIniciales(nombre);

                JLabel avatar = new JLabel(iniciales, SwingConstants.CENTER) {
                    private static final long serialVersionUID = 1L;
                    @Override
            /**
             * Renderizado gráfico personalizado en Java2D.
             * Activa Antialiasing para bordes suaves y realiza el cálculo geométrico preciso.
             */
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        int s = Math.min(getWidth(), getHeight()) - 2;
                        int x = (getWidth() - s) / 2;
                        int y = (getHeight() - s) / 2;
                        g2.setColor(new Color(224, 242, 254));
                        g2.fillOval(x, y, s, s);
                        g2.setColor(new Color(186, 230, 253));
                        g2.drawOval(x, y, s, s);
                        super.paintComponent(g);
                        g2.dispose();
                    }
                };
                avatar.setPreferredSize(new Dimension(32, 32));
                avatar.setFont(new Font("Segoe UI", Font.BOLD, 11));
                avatar.setForeground(Ui.TURQUESA_PROFUNDO);

                JLabel lblTexto = new JLabel(nombre);
                lblTexto.setFont(new Font("Segoe UI", Font.BOLD, 11));
                lblTexto.setForeground(isSelected ? table.getSelectionForeground() : Ui.TEXTO_TITULO);

                cell.add(avatar, BorderLayout.WEST);
                cell.add(lblTexto, BorderLayout.CENTER);
                return cell;
            }
        });

        // Renderer de celdas de días (Columnas 1 a 7)
        DefaultTableCellRenderer diaRenderer = new DefaultTableCellRenderer() {
            private static final long serialVersionUID = 1L;
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
                JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 11));
                p.setOpaque(true);
                p.setBackground(isSelected ? table.getSelectionBackground() : (row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252)));

                String val = value != null ? value.toString() : "Descanso";
                JLabel badge = new JLabel(" " + val + " ");
                badge.setOpaque(true);
                badge.setFont(new Font("Segoe UI", Font.BOLD, 10));

                if (val.toLowerCase().contains("mañana") || val.contains("08:00")) {
                    badge.setBackground(new Color(220, 252, 231));
                    badge.setForeground(new Color(21, 128, 61));
                } else if (val.toLowerCase().contains("tarde") || val.contains("14:30") || val.contains("15:00")) {
                    badge.setBackground(new Color(224, 242, 254));
                    badge.setForeground(new Color(3, 105, 161));
                } else if (val.toLowerCase().contains("noche") || val.toLowerCase().contains("guardia") || val.contains("21:30")) {
                    badge.setBackground(new Color(254, 243, 199));
                    badge.setForeground(new Color(180, 83, 9));
                } else {
                    badge.setBackground(new Color(241, 245, 249));
                    badge.setForeground(new Color(148, 163, 184));
                }
                badge.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(badge.getBackground().darker(), 1, true),
                        new EmptyBorder(2, 6, 2, 6)
                ));
                p.add(badge);
                return p;
            }
        };

        for (int i = 1; i <= 7; i++) {
            tablaCuadrante.getColumnModel().getColumn(i).setCellRenderer(diaRenderer);
        }

        tablaCuadrante.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int r = tablaCuadrante.getSelectedRow();
                if (r >= 0 && r < listaTurnosFiltrada.size()) {
                    mostrarDetalleTurno(listaTurnosFiltrada.get(r));
                } else {
                    mostrarDetalleTurno(null);
                }
            }
        });

        JScrollPane scroll = new JScrollPane(tablaCuadrante);
        scroll.setBorder(BorderFactory.createLineBorder(Ui.BORDE_SUAVE, 1));
        master.add(scroll, BorderLayout.CENTER);

        return master;
    }

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private JPanel crearPanelLiveHub() {
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        panelHubContenido = new JPanel(new BorderLayout());
        panelHubContenido.setOpaque(false);

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        panelHubVacio = new JPanel(new BorderLayout());
        panelHubVacio.setOpaque(false);
        JLabel lblVacio = new JLabel("Seleccione un médico del cuadrante para consultar el Live Shift Control Dock", SwingConstants.CENTER);
        lblVacio.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblVacio.setForeground(Ui.TEXTO_MUTED);
        panelHubVacio.add(lblVacio, BorderLayout.CENTER);

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        panelHubDetalle = new JPanel(new BorderLayout(0, 8)) {
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
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(Ui.BORDE_SUAVE);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                g2.dispose();
            }
        };
        panelHubDetalle.setOpaque(false);
        panelHubDetalle.setBorder(new EmptyBorder(12, 12, 12, 12));

        // 1. Hero del Turno
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel hero = new JPanel(new BorderLayout(10, 0));
        hero.setOpaque(false);
        hero.setBorder(new EmptyBorder(0, 0, 8, 0));

        lblHubAvatar = new JLabel("DR", SwingConstants.CENTER) {
            private static final long serialVersionUID = 1L;
            @Override
            /**
             * Renderizado gráfico personalizado en Java2D.
             * Activa Antialiasing para bordes suaves y realiza el cálculo geométrico preciso.
             */
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int s = Math.min(getWidth(), getHeight()) - 2;
                int x = (getWidth() - s) / 2;
                int y = (getHeight() - s) / 2;
                g2.setColor(new Color(224, 242, 254));
                g2.fillOval(x, y, s, s);
                g2.setColor(new Color(186, 230, 253));
                g2.drawOval(x, y, s, s);
                super.paintComponent(g);
                g2.dispose();
            }
        };
        lblHubAvatar.setPreferredSize(new Dimension(44, 44));
        lblHubAvatar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblHubAvatar.setForeground(Ui.TURQUESA_PROFUNDO);

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel heroTxt = new JPanel();
        heroTxt.setOpaque(false);
        heroTxt.setLayout(new BoxLayout(heroTxt, BoxLayout.Y_AXIS));

        lblHubNombre = new JLabel("Especialista");
        lblHubNombre.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblHubNombre.setForeground(Ui.TEXTO_TITULO);

        lblHubEspecialidad = new JLabel("CMPV 0000 · Especialidad");
        lblHubEspecialidad.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblHubEspecialidad.setForeground(Ui.TEXTO_MUTED);

        heroTxt.add(lblHubNombre);
        heroTxt.add(Box.createVerticalStrut(2));
        heroTxt.add(lblHubEspecialidad);

        lblHubChipCobertura = new JLabel("40 hrs / sem", SwingConstants.CENTER);
        lblHubChipCobertura.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblHubChipCobertura.setOpaque(true);
        lblHubChipCobertura.setBackground(new Color(220, 252, 231));
        lblHubChipCobertura.setForeground(new Color(21, 128, 61));
        lblHubChipCobertura.setBorder(new EmptyBorder(4, 8, 4, 8));

        hero.add(lblHubAvatar, BorderLayout.WEST);
        hero.add(heroTxt, BorderLayout.CENTER);
        hero.add(lblHubChipCobertura, BorderLayout.EAST);
        panelHubDetalle.add(hero, BorderLayout.NORTH);

        // 2. Body Scrollable
        ScrollablePanel body = new ScrollablePanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setOpaque(false);

        // Telemetría 2x2
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel telemetria = new JPanel(new GridLayout(2, 2, 6, 6));
        telemetria.setOpaque(false);
        telemetria.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        telemetria.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblHubHorasSemanales = new JLabel("40 hrs");
        lblHubDiasActivos = new JLabel("5 días");
        lblHubConsultorio = new JLabel("Cons. 1");
        lblHubGuardiasNoche = new JLabel("1 guardia");

        telemetria.add(crearCardMiniTelemetria("HORAS TOTALES", lblHubHorasSemanales, Ui.TURQUESA_OSCURO));
        telemetria.add(crearCardMiniTelemetria("DÍAS EN SEDE", lblHubDiasActivos, Ui.TEXTO_TITULO));
        telemetria.add(crearCardMiniTelemetria("CONSULTORIO", lblHubConsultorio, Ui.TURQUESA_PROFUNDO));
        telemetria.add(crearCardMiniTelemetria("GUARDIAS NOCHE", lblHubGuardiasNoche, new Color(217, 119, 6)));

        body.add(telemetria);
        body.add(vGap(8));

        // Secciones
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel secDesglose = crearSeccionCard("Desglose Diario de Guardias (Lun - Dom)");
        txtHubDesgloseSemanal = new JTextArea();
        configurarTextAreaItem(txtHubDesgloseSemanal);
        secDesglose.add(crearItemLinea("Semana:", txtHubDesgloseSemanal));
        body.add(secDesglose);
        body.add(vGap(6));

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel secRespaldo = crearSeccionCard("Especialista de Respaldo y Derivación");
        txtHubRespaldo = new JTextArea();
        configurarTextAreaItem(txtHubRespaldo);
        secRespaldo.add(crearItemLinea("Suplencia:", txtHubRespaldo));
        body.add(secRespaldo);
        body.add(vGap(6));

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel secObs = crearSeccionCard("Observaciones y Cobertura Asistencial");
        txtHubObservaciones = new JTextArea();
        configurarTextAreaItem(txtHubObservaciones);
        secObs.add(crearItemLinea("Notas:", txtHubObservaciones));
        body.add(secObs);

        JScrollPane scrollBody = new JScrollPane(body);
        scrollBody.setBorder(null);
        scrollBody.setOpaque(false);
        scrollBody.getViewport().setOpaque(false);
        panelHubDetalle.add(scrollBody, BorderLayout.CENTER);

        // 3. Grid de Acciones (2x2)
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel gridAcciones = new JPanel(new GridLayout(2, 2, 6, 6));
        gridAcciones.setOpaque(false);
        gridAcciones.setBorder(new EmptyBorder(8, 0, 0, 0));

        JButton btnModificar = crearBotonAccion("Modificar Turno", Iconos.crearIconoTurno(13, Color.WHITE), true);
        btnModificar.addActionListener(e -> {
            if (turnoSeleccionado != null) mostrarDialogoEditarTurno(turnoSeleccionado);
        });

        JButton btnReemplazo = crearBotonAccion("Asignar Reemplazo", Iconos.crearIconoUsuario(13, Ui.TURQUESA_OSCURO), false);
        btnReemplazo.addActionListener(e -> {
            if (turnoSeleccionado != null) {
                JOptionPane.showMessageDialog(this,
                        "Reemplazo asistencial asignado temporalmente para " + turnoSeleccionado.getNombreProfesional(),
                        "Reemplazo Confirmado", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        JButton btnWhatsApp = crearBotonAccion("Notificar WhatsApp", Iconos.crearIconoWhatsApp(13, Ui.TEXTO_MUTED), false);
        btnWhatsApp.addActionListener(e -> {
            if (turnoSeleccionado != null) {
                JOptionPane.showMessageDialog(this,
                        "Rol semanal enviado al móvil del especialista " + turnoSeleccionado.getNombreProfesional() + " vía WhatsApp Business.",
                        "Notificación Enviada", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        JButton btnReporte = crearBotonAccion("Ficha de Turno", Iconos.crearIconoDocumento(13, Ui.TEXTO_MUTED), false);
        btnReporte.addActionListener(e -> exportarCuadrantePDF());

        gridAcciones.add(btnModificar);
        gridAcciones.add(btnReemplazo);
        gridAcciones.add(btnWhatsApp);
        gridAcciones.add(btnReporte);
        panelHubDetalle.add(gridAcciones, BorderLayout.SOUTH);

        panelHubContenido.add(panelHubVacio, BorderLayout.CENTER);
        return panelHubContenido;
    }

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private JPanel crearCardMiniTelemetria(String titulo, JLabel lblValor, Color colorValor) {
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel c = new JPanel();
        c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));
        c.setBackground(new Color(248, 250, 252));
        c.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Ui.BORDE_SUAVE, 1, true),
                new EmptyBorder(4, 8, 4, 8)
        ));

        JLabel t = new JLabel(titulo);
        t.setFont(new Font("Segoe UI", Font.BOLD, 8));
        t.setForeground(Ui.TEXTO_MUTED);

        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblValor.setForeground(colorValor);

        c.add(t);
        c.add(vGap(2));
        c.add(lblValor);
        return c;
    }

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private JPanel crearSeccionCard(String titulo) {
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel sec = new JPanel() {
            private static final long serialVersionUID = 1L;
            @Override
            public Dimension getMaximumSize() {
                return new Dimension(Integer.MAX_VALUE, getPreferredSize().height);
            }
            @Override
            protected void addImpl(Component comp, Object constraints, int index) {
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
                if (comp instanceof javax.swing.JComponent) {
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
                    ((javax.swing.JComponent) comp).setAlignmentX(Component.LEFT_ALIGNMENT);
                }
                super.addImpl(comp, constraints, index);
            }
        };
        sec.setOpaque(false);
        sec.setLayout(new BoxLayout(sec, BoxLayout.Y_AXIS));
        sec.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Ui.BORDE_SUAVE, 1, true),
                new EmptyBorder(6, 10, 6, 10)
        ));
        sec.setBackground(Color.WHITE);
        sec.setAlignmentX(Component.LEFT_ALIGNMENT);

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        header.setOpaque(false);
        header.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel t = new JLabel(titulo);
        t.setFont(new Font("Segoe UI", Font.BOLD, 10));
        t.setForeground(Ui.TURQUESA_OSCURO);
        header.add(t);

        sec.add(header);
        sec.add(vGap(4));
        return sec;
    }

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private JPanel crearItemLinea(String label, Component valor) {
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel p = new JPanel(new BorderLayout(6, 0)) {
            private static final long serialVersionUID = 1L;
            @Override
            public Dimension getMaximumSize() {
                return new Dimension(Integer.MAX_VALUE, getPreferredSize().height);
            }
        };
        p.setOpaque(false);
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel l = new JLabel(label);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        l.setForeground(Ui.TEXTO_MUTED);
        l.setVerticalAlignment(SwingConstants.TOP);
        p.add(l, BorderLayout.WEST);
        p.add(valor, BorderLayout.CENTER);
        return p;
    }

    private static Component vGap(int h) {
        Component c = Box.createVerticalStrut(h);
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        if (c instanceof javax.swing.JComponent) {
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
            ((javax.swing.JComponent) c).setAlignmentX(Component.LEFT_ALIGNMENT);
        }
        return c;
    }

    private static void configurarTextAreaItem(JTextArea ta) {
        ta.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        ta.setForeground(Ui.TEXTO_TITULO);
        ta.setLineWrap(true);
        ta.setWrapStyleWord(true);
        ta.setEditable(false);
        ta.setOpaque(false);
        ta.setBorder(null);
        ta.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    private JButton crearBotonAccion(String texto, javax.swing.Icon icono, boolean primario) {
        JButton btn = new JButton(texto, icono) {
            private static final long serialVersionUID = 1L;
            @Override
            /**
             * Renderizado gráfico personalizado en Java2D.
             * Activa Antialiasing para bordes suaves y realiza el cálculo geométrico preciso.
             */
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (primario) {
                    g2.setColor(getModel().isPressed() ? Ui.TURQUESA_OSCURO : Ui.TURQUESA);
                } else {
                    g2.setColor(getModel().isRollover() ? new Color(241, 245, 249) : Color.WHITE);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                if (!primario) {
                    g2.setColor(Ui.BORDE_SUAVE);
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                }
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 10));
        btn.setForeground(primario ? Color.WHITE : new Color(51, 65, 85));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(4, 6, 4, 6));
        return btn;
    }

    public void recargarDatos() {
        filtrarTurnos();
        actualizarKpis();
    }

    private void actualizarKpis() {
        List<TurnoSemanal> total = repo.getCuadranteTurnos();
        int man = 0;
        int tar = 0;
        int noc = 0;

        for (TurnoSemanal ts : total) {
            for (String h : ts.getHorariosDias()) {
                if (h.toLowerCase().contains("08:00") || h.toLowerCase().contains("mañana")) man++;
                if (h.toLowerCase().contains("14:30") || h.toLowerCase().contains("15:00") || h.toLowerCase().contains("tarde")) tar++;
                if (h.toLowerCase().contains("21:30") || h.toLowerCase().contains("noche") || h.toLowerCase().contains("guardia")) noc++;
            }
        }

        lblKpiCobertura.setText("98.5%");
        lblKpiManana.setText((man / 7 + 3) + " médicos");
        lblKpiTarde.setText((tar / 7 + 2) + " médicos");
        lblKpiNoche.setText((noc > 0 ? noc / 7 + 2 : 2) + " en guardia");
    }

    /**
     * Aplica criterios de filtrado dinámico en memoria sobre los registros.
     * Evalúa texto de búsqueda (búsqueda predictiva) y filtros por categoría o estado.
     */
    private void filtrarTurnos() {
        listaTurnosFiltrada.clear();
        modeloTabla.setRowCount(0);

        List<TurnoSemanal> base = repo.getCuadranteTurnos();
        String busq = txtBuscar != null ? txtBuscar.getText().trim().toLowerCase() : "";

        for (TurnoSemanal ts : base) {
            boolean matchPill = filtroEspecialidad.equals("TODOS") ||
                    ts.getEspecialidad().toLowerCase().contains(filtroEspecialidad.toLowerCase());

            boolean matchBusq = busq.isEmpty() ||
                    ts.getNombreProfesional().toLowerCase().contains(busq) ||
                    ts.getEspecialidad().toLowerCase().contains(busq) ||
                    ts.getColegiaturaCMPV().toLowerCase().contains(busq);

            if (matchPill && matchBusq) {
                listaTurnosFiltrada.add(ts);
                Object[] fila = new Object[8];
                fila[0] = ts.getNombreProfesional() + " (" + ts.getEspecialidad() + ")";
                for (int d = 0; d < 7; d++) {
                    fila[d + 1] = ts.getHorarioDia(d);
                }
                modeloTabla.addRow(fila);
            }
        }

        if (!listaTurnosFiltrada.isEmpty()) {
            tablaCuadrante.setRowSelectionInterval(0, 0);
        } else {
            mostrarDetalleTurno(null);
        }
    }

    /**
     * Actualiza el panel lateral derecho (Live Detail Hub) con la información del registro seleccionado.
     * Si el argumento es nulo, restablece el panel mostrando el estado de selección vacía.
     */
    private void mostrarDetalleTurno(TurnoSemanal ts) {
        turnoSeleccionado = ts;
        panelHubContenido.removeAll();

        if (ts == null) {
            panelHubContenido.add(panelHubVacio, BorderLayout.CENTER);
        } else {
            lblHubAvatar.setText(obtenerIniciales(ts.getNombreProfesional()));
            lblHubNombre.setText(ts.getNombreProfesional());
            lblHubEspecialidad.setText(ts.getColegiaturaCMPV() + " · " + ts.getEspecialidad());

            int diasActivos = 0;
            StringBuilder desglose = new StringBuilder();
            String[] nombresDias = {"Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"};
            int guardiasNoc = 0;

            for (int i = 0; i < 7; i++) {
                String h = ts.getHorarioDia(i);
                if (!h.equalsIgnoreCase("descanso")) {
                    diasActivos++;
                    if (h.toLowerCase().contains("noche") || h.toLowerCase().contains("guardia")) guardiasNoc++;
                }
                desglose.append("• ").append(nombresDias[i]).append(": ").append(h).append("\n");
            }

            int horasTotales = diasActivos * 8;
            lblHubHorasSemanales.setText(horasTotales + " hrs");
            lblHubDiasActivos.setText(diasActivos + " días");
            lblHubConsultorio.setText(ts.getConsultorioHabitual());
            lblHubGuardiasNoche.setText(guardiasNoc + " guardia(s)");

            txtHubDesgloseSemanal.setText(desglose.toString().trim());
            txtHubRespaldo.setText("Suplente Oficial: Dr. Respaldo Clínico de Guardia\nConsultorio Asignado: " + ts.getConsultorioHabitual());
            txtHubObservaciones.setText("Cumplimiento estricto de descansos post-guardia. Tolerancia biométrica de ingreso: 10 minutos.");

            panelHubContenido.add(panelHubDetalle, BorderLayout.CENTER);
        }

        panelHubContenido.revalidate();
        panelHubContenido.repaint();
    }

    private String obtenerIniciales(String nombre) {
        if (nombre == null || nombre.isEmpty()) return "DR";
        String limpio = nombre.replace("Dra.", "").replace("Dr.", "").trim();
        String[] p = limpio.split("\\s+");
        if (p.length >= 2) {
            return (p[0].substring(0, 1) + p[1].substring(0, 1)).toUpperCase();
        } else if (p.length == 1 && p[0].length() >= 2) {
            return p[0].substring(0, 2).toUpperCase();
        }
        return "DR";
    }

    private void exportarCuadrantePDF() {
        String[] cols = new String[]{"Especialista", "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"};
        List<Object[]> filas = new ArrayList<>();
        for (TurnoSemanal ts : listaTurnosFiltrada) {
            filas.add(new Object[]{
                    ts.getNombreProfesional() + " (" + ts.getEspecialidad() + ")",
                    ts.getHorarioDia(0), ts.getHorarioDia(1), ts.getHorarioDia(2),
                    ts.getHorarioDia(3), ts.getHorarioDia(4), ts.getHorarioDia(5), ts.getHorarioDia(6)
            });
        }
        Ui.mostrarVisorReporte(
                SwingUtilities.getWindowAncestor(this),
                "CUADRANTE SEMANAL DE GUARDIAS Y TURNOS MÉDICOS",
                lblSemanaRango.getText() + " | Sede Central Veterinaria Happy Pets",
                "DIRECCIÓN MÉDICA Y GESTIÓN DE ROLES ASISTENCIALES",
                cols,
                filas,
                "CUADRANTE DE ROL OPERATIVO VETERINARIA HAPPY PETS",
                "Cuadrante_Turnos"
        );
    }

    private void mostrarDialogoAsignarTurno() {
        mostrarFormularioTurno(null);
    }

    private void mostrarDialogoEditarTurno(TurnoSemanal ts) {
        mostrarFormularioTurno(ts);
    }

    private void mostrarFormularioTurno(TurnoSemanal existente) {
        boolean esEdicion = existente != null;
        JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(this),
                esEdicion ? "Modificar Turnos Semanales" : "Asignar Nuevo Turno Semanal",
                JDialog.ModalityType.APPLICATION_MODAL);
        dlg.setSize(560, 520);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(new EmptyBorder(18, 22, 18, 22));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField txtNombre = new JTextField(esEdicion ? existente.getNombreProfesional() : "Dr. Especialista");
        JTextField txtCMPV = new JTextField(esEdicion ? existente.getColegiaturaCMPV() : "CMPV-0000");
        JComboBox<String> cbEsp = new JComboBox<>(new String[]{"Cirugía General", "Dermatología", "Medicina Felina", "Cardiología", "Animales Exóticos", "Urgencias"});
        if (esEdicion) cbEsp.setSelectedItem(existente.getEspecialidad());
        JTextField txtCons = new JTextField(esEdicion ? existente.getConsultorioHabitual() : "Consultorio 1");

        String[] opcionesTurnos = {"08:00 - 15:00 (Mañana)", "14:30 - 22:00 (Tarde)", "21:30 - 07:30 (Guardia Noche)", "Descanso"};
        JComboBox<String>[] combosDias = new JComboBox[7];
        String[] nombresDias = {"Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"};

        for (int i = 0; i < 7; i++) {
            combosDias[i] = new JComboBox<>(opcionesTurnos);
            if (esEdicion) {
                String act = existente.getHorarioDia(i);
                for (String op : opcionesTurnos) {
                    if (act.toLowerCase().contains(op.substring(0, 5).toLowerCase()) || act.equalsIgnoreCase(op)) {
                        combosDias[i].setSelectedItem(op);
                    }
                }
            } else {
                if (i >= 5) combosDias[i].setSelectedItem("Descanso");
            }
        }

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row; form.add(new JLabel("Profesional:"), gbc);
        gbc.gridx = 1; form.add(txtNombre, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; form.add(new JLabel("CMPV / Especialidad:"), gbc);
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel pMid = new JPanel(new GridLayout(1, 2, 6, 0));
        pMid.setOpaque(false);
        pMid.add(txtCMPV);
        pMid.add(cbEsp);
        gbc.gridx = 1; form.add(pMid, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; form.add(new JLabel("Consultorio:"), gbc);
        gbc.gridx = 1; form.add(txtCons, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        JLabel lblSub = new JLabel("Asignación de Turnos Diarios:");
        lblSub.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblSub.setForeground(Ui.TURQUESA_PROFUNDO);
        form.add(lblSub, gbc);
        gbc.gridwidth = 1;

        for (int i = 0; i < 7; i++) {
            row++;
            gbc.gridx = 0; gbc.gridy = row; form.add(new JLabel(nombresDias[i] + ":"), gbc);
            gbc.gridx = 1; form.add(combosDias[i], gbc);
        }

        dlg.add(new JScrollPane(form), BorderLayout.CENTER);

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel bot = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        bot.setBackground(new Color(248, 250, 252));
        JButton btnCancelar = Ui.boton("Cancelar", false);
        btnCancelar.addActionListener(e -> dlg.dispose());

        JButton btnGuardar = Ui.boton(esEdicion ? "Guardar Cuadrante" : "Asignar Horario", true);
        btnGuardar.addActionListener(e -> {
            String[] hDias = new String[7];
            String[] tDias = new String[7];
            for (int i = 0; i < 7; i++) {
                hDias[i] = (String) combosDias[i].getSelectedItem();
                tDias[i] = hDias[i].contains("Mañana") ? "Mañana" : (hDias[i].contains("Tarde") ? "Tarde" : (hDias[i].contains("Guardia") ? "Guardia" : "Descanso"));
            }

            if (esEdicion) {
                existente.setNombreProfesional(txtNombre.getText().trim());
                existente.setColegiaturaCMPV(txtCMPV.getText().trim());
                existente.setEspecialidad((String) cbEsp.getSelectedItem());
                existente.setConsultorioHabitual(txtCons.getText().trim());
                existente.setHorariosDias(hDias);
                existente.setTiposTurnoDias(tDias);
            } else {
                TurnoSemanal nuevo = new TurnoSemanal(
                        "TS-" + (repo.getCuadranteTurnos().size() + 1),
                        "MED-" + (repo.getCuadranteTurnos().size() + 1),
                        txtNombre.getText().trim(),
                        txtCMPV.getText().trim(),
                        (String) cbEsp.getSelectedItem(),
                        txtCons.getText().trim(),
                        hDias,
                        tDias
                );
                repo.getCuadranteTurnos().add(nuevo);
            }
            recargarDatos();
            dlg.dispose();
            JOptionPane.showMessageDialog(this, "Cuadrante de turnos actualizado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        });

        bot.add(btnCancelar);
        bot.add(btnGuardar);
        dlg.add(bot, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private static class ScrollablePanel extends JPanel implements javax.swing.Scrollable {
        private static final long serialVersionUID = 1L;
        @Override public Dimension getPreferredScrollableViewportSize() { return getPreferredSize(); }
        @Override public int getScrollableUnitIncrement(java.awt.Rectangle visibleRect, int orientation, int direction) { return 16; }
        @Override public int getScrollableBlockIncrement(java.awt.Rectangle visibleRect, int orientation, int direction) { return 32; }
        @Override public boolean getScrollableTracksViewportWidth() { return true; }
        @Override public boolean getScrollableTracksViewportHeight() { return false; }
    }
}
