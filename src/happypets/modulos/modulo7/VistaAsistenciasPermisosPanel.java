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
import java.time.LocalTime;
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
import happypets.model.RegistroAsistencia;
import happypets.model.SolicitudPermiso;
import happypets.ui.Iconos;
import happypets.ui.Ui;

/**
 * Submódulo 7.4: Asistencias y Permisos - Pase de lista diario por turnos.
 * Arquitectura Master-Detail (Opción A):
 * - Master Table: Registro analítico de marcaciones biométricas, retardos, horas y turnos.
 * - Live Attendance Hub: Telemetría 2x2, justificaciones, permisos activos y acciones 1-click.
 */
public class VistaAsistenciasPermisosPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private final RepositorioVeterinaria repo = RepositorioVeterinaria.getInstancia();

    // KPIs Superiores
    private JLabel lblKpiPresentes;
    private JLabel lblKpiRetardos;
    private JLabel lblKpiAusentes;
    private JLabel lblKpiPermisos;

    // Master Table
    private JTable tablaAsistencias;
    private DefaultTableModel modeloAsistencias;
    private JTextField txtBuscar;
    private String filtroEstado = "TODOS";
    private final List<RegistroAsistencia> listaAsistenciasFiltrada = new ArrayList<>();

    // Live Attendance Hub (Derecha - 360px)
    private JPanel panelHubContenido;
    private JPanel panelHubVacio;
    private JPanel panelHubDetalle;

    private JLabel lblHubAvatar;
    private JLabel lblHubNombre;
    private JLabel lblHubCargo;
    private JLabel lblHubChipEstado;

    // Telemetría 2x2
    private JLabel lblHubEntrada;
    private JLabel lblHubSalida;
    private JLabel lblHubMinRetardo;
    private JLabel lblHubJornada;

    // Secciones de Detalle
    private JTextArea txtHubBiometrico;
    private JTextArea txtHubJustificacion;
    private JTextArea txtHubPermisos;

    private RegistroAsistencia asistenciaSeleccionada;

    public VistaAsistenciasPermisosPanel() {
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

        recargarDatos();
    }

    private JPanel crearCabeceraVista() {
        JPanel cab = new JPanel(new BorderLayout());
        cab.setOpaque(false);

        JPanel izq = new JPanel();
        izq.setOpaque(false);
        izq.setLayout(new BoxLayout(izq, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("Pase de Lista Diario y Control de Asistencias");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 17));
        titulo.setForeground(Ui.TEXTO_TITULO);

        LocalDate hoy = LocalDate.now();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM 'de' yyyy", new Locale("es", "ES"));
        String fechaStr = hoy.format(dtf);
        fechaStr = fechaStr.substring(0, 1).toUpperCase() + fechaStr.substring(1);

        JLabel subtitulo = new JLabel("Módulo 7.4 · " + fechaStr + " · Marcación biométrica, retardos y licencias médicas");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitulo.setForeground(Ui.TEXTO_MUTED);

        izq.add(titulo);
        izq.add(Box.createVerticalStrut(2));
        izq.add(subtitulo);
        cab.add(izq, BorderLayout.WEST);

        JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        der.setOpaque(false);

        JButton btnPase = Ui.boton("+ Pase de Lista Rápido", true);
        btnPase.setIcon(Iconos.crearIconoAsistencia(14, Color.WHITE));
        btnPase.addActionListener(e -> mostrarDialogoPaseListaRapido());
        der.add(btnPase);

        JButton btnPermisos = Ui.boton("Gestionar Permisos", false);
        btnPermisos.setIcon(Iconos.crearIconoDocumento(14, Ui.TEXTO_MUTED));
        btnPermisos.addActionListener(e -> mostrarDialogoGestionPermisos());
        der.add(btnPermisos);

        JButton btnReporte = Ui.boton("Exportar Reporte", false);
        btnReporte.addActionListener(e -> exportarParteAsistencia());
        der.add(btnReporte);

        JButton btnActualizar = Ui.boton("Actualizar", false);
        btnActualizar.addActionListener(e -> recargarDatos());
        der.add(btnActualizar);

        cab.add(der, BorderLayout.EAST);
        cab.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        return cab;
    }

    private JPanel crearFilaKpis() {
        JPanel fila = new JPanel(new GridLayout(1, 4, 12, 0));
        fila.setOpaque(false);
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 74));

        lblKpiPresentes = new JLabel("0");
        lblKpiRetardos = new JLabel("0");
        lblKpiAusentes = new JLabel("0");
        lblKpiPermisos = new JLabel("0");

        fila.add(crearCardKpi("Presentes Hoy", lblKpiPresentes, "En servicio puntual", Iconos.crearIconoCheck(22, Ui.COLOR_EXITO)));
        fila.add(crearCardKpi("Retardos / Demoras", lblKpiRetardos, "+10 min tolerancia", Iconos.crearIconoAlertaTriaje(22, new Color(217, 119, 6))));
        fila.add(crearCardKpi("Ausencias / Faltas", lblKpiAusentes, "Inasistencias registradas", Iconos.crearIconoAlertaTriaje(22, Ui.COLOR_PELIGRO)));
        fila.add(crearCardKpi("Permisos y Licencias", lblKpiPermisos, "Pases autorizados hoy", Iconos.crearIconoDocumento(22, Ui.TURQUESA_PROFUNDO)));

        return fila;
    }

    private JPanel crearCardKpi(String titulo, JLabel lblValor, String subtitulo, javax.swing.Icon icono) {
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
        card.setBorder(new EmptyBorder(10, 14, 10, 14));

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
        JPanel master = crearPanelMasterTable();
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

    private JPanel crearPanelMasterTable() {
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
        master.setBorder(new EmptyBorder(12, 14, 12, 14));

        // Filtros y Buscador
        JPanel cabFiltros = new JPanel();
        cabFiltros.setOpaque(false);
        cabFiltros.setLayout(new BoxLayout(cabFiltros, BoxLayout.Y_AXIS));

        JPanel barraBuscar = new JPanel(new BorderLayout(8, 0));
        barraBuscar.setOpaque(false);

        JLabel lblBuscar = new JLabel("Buscar:");
        lblBuscar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblBuscar.setForeground(Ui.TEXTO_MUTED);
        barraBuscar.add(lblBuscar, BorderLayout.WEST);

        txtBuscar = new JTextField();
        txtBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtBuscar.putClientProperty("JTextField.placeholderText", "Buscar colaborador, rol, turno o justificación...");
        txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filtrarAsistencias(); }
            public void removeUpdate(DocumentEvent e) { filtrarAsistencias(); }
            public void changedUpdate(DocumentEvent e) { filtrarAsistencias(); }
        });
        barraBuscar.add(txtBuscar, BorderLayout.CENTER);
        cabFiltros.add(barraBuscar);
        cabFiltros.add(Box.createVerticalStrut(8));

        // Píldoras de Filtro
        JPanel pills = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        pills.setOpaque(false);

        String[] estados = {"TODOS", "Presentes", "Retardos", "Ausentes", "Permisos"};
        for (String est : estados) {
            JButton btnPill = new JButton(est);
            btnPill.setFont(new Font("Segoe UI", Font.BOLD, 11));
            btnPill.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btnPill.setBorder(new EmptyBorder(4, 10, 4, 10));
            btnPill.setFocusPainted(false);

            if (est.equals(filtroEstado)) {
                btnPill.setBackground(Ui.TURQUESA);
                btnPill.setForeground(Color.WHITE);
            } else {
                btnPill.setBackground(new Color(241, 245, 249));
                btnPill.setForeground(Ui.TEXTO_MUTED);
            }

            btnPill.addActionListener(e -> {
                filtroEstado = est;
                for (Component comp : pills.getComponents()) {
                    if (comp instanceof JButton) {
                        JButton b = (JButton) comp;
                        if (b.getText().equals(filtroEstado)) {
                            b.setBackground(Ui.TURQUESA);
                            b.setForeground(Color.WHITE);
                        } else {
                            b.setBackground(new Color(241, 245, 249));
                            b.setForeground(Ui.TEXTO_MUTED);
                        }
                    }
                }
                filtrarAsistencias();
            });
            pills.add(btnPill);
        }
        cabFiltros.add(pills);
        master.add(cabFiltros, BorderLayout.NORTH);

        // Tabla
        String[] cols = {"Colaborador", "Rol / Cargo", "Turno", "Entrada Real", "Salida Real", "Demora", "Estado"};
        modeloAsistencias = new DefaultTableModel(cols, 0) {
            private static final long serialVersionUID = 1L;
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tablaAsistencias = new JTable(modeloAsistencias);
        tablaAsistencias.setRowHeight(48);
        tablaAsistencias.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaAsistencias.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaAsistencias.setShowVerticalLines(false);
        tablaAsistencias.setGridColor(new Color(241, 245, 249));
        tablaAsistencias.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        tablaAsistencias.getTableHeader().setBackground(new Color(248, 250, 252));
        tablaAsistencias.getTableHeader().setForeground(Ui.TEXTO_MUTED);

        tablaAsistencias.getColumnModel().getColumn(0).setPreferredWidth(170);
        tablaAsistencias.getColumnModel().getColumn(1).setPreferredWidth(140);
        tablaAsistencias.getColumnModel().getColumn(2).setPreferredWidth(120);
        tablaAsistencias.getColumnModel().getColumn(3).setPreferredWidth(90);
        tablaAsistencias.getColumnModel().getColumn(4).setPreferredWidth(90);
        tablaAsistencias.getColumnModel().getColumn(5).setPreferredWidth(80);
        tablaAsistencias.getColumnModel().getColumn(6).setPreferredWidth(100);

        // Monograma en columna 0 (Colaborador)
        tablaAsistencias.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
            private static final long serialVersionUID = 1L;
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                JPanel cell = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
                cell.setOpaque(true);
                cell.setBackground(isSelected ? table.getSelectionBackground() : (row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252)));

                String nombre = value != null ? value.toString() : "";
                String iniciales = obtenerIniciales(nombre);

                JLabel avatar = new JLabel(iniciales, SwingConstants.CENTER) {
                    private static final long serialVersionUID = 1L;
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(new Color(243, 232, 255)); // Soft lavender/purple
                        g2.fillOval(0, 0, getWidth() - 1, getHeight() - 1);
                        g2.setColor(new Color(216, 180, 254));
                        g2.drawOval(0, 0, getWidth() - 1, getHeight() - 1);
                        super.paintComponent(g);
                        g2.dispose();
                    }
                };
                avatar.setPreferredSize(new Dimension(32, 32));
                avatar.setFont(new Font("Segoe UI", Font.BOLD, 11));
                avatar.setForeground(Ui.TURQUESA_PROFUNDO);

                JLabel lblTexto = new JLabel(nombre);
                lblTexto.setFont(new Font("Segoe UI", Font.BOLD, 12));
                lblTexto.setForeground(isSelected ? table.getSelectionForeground() : Ui.TEXTO_TITULO);

                cell.add(avatar);
                cell.add(lblTexto);
                return cell;
            }
        });

        // Chip en columna 6 (Estado)
        tablaAsistencias.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            private static final long serialVersionUID = 1L;
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 11));
                p.setOpaque(true);
                p.setBackground(isSelected ? table.getSelectionBackground() : (row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252)));

                String st = value != null ? value.toString() : "";
                JLabel badge = new JLabel(" " + st + " ");
                badge.setOpaque(true);
                badge.setFont(new Font("Segoe UI", Font.BOLD, 10));

                if (st.toLowerCase().contains("presente")) {
                    badge.setBackground(new Color(220, 252, 231));
                    badge.setForeground(new Color(21, 128, 61));
                } else if (st.toLowerCase().contains("retardo")) {
                    badge.setBackground(new Color(254, 243, 199));
                    badge.setForeground(new Color(180, 83, 9));
                } else if (st.toLowerCase().contains("ausente")) {
                    badge.setBackground(new Color(254, 242, 242));
                    badge.setForeground(new Color(220, 38, 38));
                } else {
                    badge.setBackground(new Color(243, 232, 255));
                    badge.setForeground(new Color(126, 34, 206));
                }
                badge.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(badge.getBackground().darker(), 1, true),
                        new EmptyBorder(2, 6, 2, 6)
                ));
                p.add(badge);
                return p;
            }
        });

        tablaAsistencias.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int r = tablaAsistencias.getSelectedRow();
                if (r >= 0 && r < listaAsistenciasFiltrada.size()) {
                    mostrarDetalleAsistencia(listaAsistenciasFiltrada.get(r));
                } else {
                    mostrarDetalleAsistencia(null);
                }
            }
        });

        JScrollPane scroll = new JScrollPane(tablaAsistencias);
        scroll.setBorder(BorderFactory.createLineBorder(Ui.BORDE_SUAVE, 1));
        master.add(scroll, BorderLayout.CENTER);

        return master;
    }

    private JPanel crearPanelLiveHub() {
        panelHubContenido = new JPanel(new BorderLayout());
        panelHubContenido.setOpaque(false);

        panelHubVacio = new JPanel(new BorderLayout());
        panelHubVacio.setOpaque(false);
        JLabel lblVacio = new JLabel("Seleccione una marcación para consultar el Live Attendance Hub", SwingConstants.CENTER);
        lblVacio.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblVacio.setForeground(Ui.TEXTO_MUTED);
        panelHubVacio.add(lblVacio, BorderLayout.CENTER);

        panelHubDetalle = new JPanel(new BorderLayout(0, 8)) {
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
        panelHubDetalle.setOpaque(false);
        panelHubDetalle.setBorder(new EmptyBorder(12, 12, 12, 12));

        // 1. Hero del Colaborador
        JPanel hero = new JPanel(new BorderLayout(10, 0));
        hero.setOpaque(false);
        hero.setBorder(new EmptyBorder(0, 0, 8, 0));

        lblHubAvatar = new JLabel("AS", SwingConstants.CENTER) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(243, 232, 255));
                g2.fillOval(0, 0, getWidth() - 1, getHeight() - 1);
                g2.setColor(new Color(216, 180, 254));
                g2.drawOval(0, 0, getWidth() - 1, getHeight() - 1);
                super.paintComponent(g);
                g2.dispose();
            }
        };
        lblHubAvatar.setPreferredSize(new Dimension(44, 44));
        lblHubAvatar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblHubAvatar.setForeground(Ui.TURQUESA_PROFUNDO);

        JPanel heroTxt = new JPanel();
        heroTxt.setOpaque(false);
        heroTxt.setLayout(new BoxLayout(heroTxt, BoxLayout.Y_AXIS));

        lblHubNombre = new JLabel("Colaborador");
        lblHubNombre.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblHubNombre.setForeground(Ui.TEXTO_TITULO);

        lblHubCargo = new JLabel("Cargo · Turno");
        lblHubCargo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblHubCargo.setForeground(Ui.TEXTO_MUTED);

        heroTxt.add(lblHubNombre);
        heroTxt.add(Box.createVerticalStrut(2));
        heroTxt.add(lblHubCargo);

        lblHubChipEstado = new JLabel("Presente", SwingConstants.CENTER);
        lblHubChipEstado.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblHubChipEstado.setOpaque(true);
        lblHubChipEstado.setBackground(new Color(220, 252, 231));
        lblHubChipEstado.setForeground(new Color(21, 128, 61));
        lblHubChipEstado.setBorder(new EmptyBorder(4, 8, 4, 8));

        hero.add(lblHubAvatar, BorderLayout.WEST);
        hero.add(heroTxt, BorderLayout.CENTER);
        hero.add(lblHubChipEstado, BorderLayout.EAST);
        panelHubDetalle.add(hero, BorderLayout.NORTH);

        // 2. Body Scrollable
        ScrollablePanel body = new ScrollablePanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setOpaque(false);

        // Telemetría 2x2
        JPanel telemetria = new JPanel(new GridLayout(2, 2, 6, 6));
        telemetria.setOpaque(false);
        telemetria.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        telemetria.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblHubEntrada = new JLabel("08:00");
        lblHubSalida = new JLabel("15:00");
        lblHubMinRetardo = new JLabel("0 min");
        lblHubJornada = new JLabel("8 hrs");

        telemetria.add(crearCardMiniTelemetria("HORA ENTRADA", lblHubEntrada, Ui.TURQUESA_OSCURO));
        telemetria.add(crearCardMiniTelemetria("HORA SALIDA", lblHubSalida, Ui.TEXTO_TITULO));
        telemetria.add(crearCardMiniTelemetria("TOLERANCIA / RETARDO", lblHubMinRetardo, new Color(217, 119, 6)));
        telemetria.add(crearCardMiniTelemetria("JORNADA EFECTIVA", lblHubJornada, Ui.COLOR_EXITO));

        body.add(telemetria);
        body.add(vGap(8));

        // Secciones
        JPanel secBio = crearSeccionCard("Registro Biométrico y Turno");
        txtHubBiometrico = new JTextArea();
        configurarTextAreaItem(txtHubBiometrico);
        secBio.add(crearItemLinea("Marcación:", txtHubBiometrico));
        body.add(secBio);
        body.add(vGap(6));

        JPanel secJust = crearSeccionCard("Justificación y Notas de RRHH");
        txtHubJustificacion = new JTextArea();
        configurarTextAreaItem(txtHubJustificacion);
        secJust.add(crearItemLinea("Observación:", txtHubJustificacion));
        body.add(secJust);
        body.add(vGap(6));

        JPanel secPerm = crearSeccionCard("Permisos y Licencias del Día");
        txtHubPermisos = new JTextArea();
        configurarTextAreaItem(txtHubPermisos);
        secPerm.add(crearItemLinea("Estado:", txtHubPermisos));
        body.add(secPerm);

        JScrollPane scrollBody = new JScrollPane(body);
        scrollBody.setBorder(null);
        scrollBody.setOpaque(false);
        scrollBody.getViewport().setOpaque(false);
        panelHubDetalle.add(scrollBody, BorderLayout.CENTER);

        // 3. Grid de Acciones (2x2)
        JPanel gridAcciones = new JPanel(new GridLayout(2, 2, 6, 6));
        gridAcciones.setOpaque(false);
        gridAcciones.setBorder(new EmptyBorder(8, 0, 0, 0));

        JButton btnMarcar = crearBotonAccion("Marcar Salida", Iconos.crearIconoTurno(13, Color.WHITE), true);
        btnMarcar.addActionListener(e -> {
            if (asistenciaSeleccionada != null) {
                String ahora = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
                asistenciaSeleccionada.setHoraSalidaReal(ahora);
                recargarDatos();
                JOptionPane.showMessageDialog(this, "Marcación de salida registrada a las " + ahora, "Biométrico Actualizado", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        JButton btnJustificar = crearBotonAccion("Justificar Incidencia", Iconos.crearIconoEditar(13, Ui.TURQUESA_OSCURO), false);
        btnJustificar.addActionListener(e -> {
            if (asistenciaSeleccionada != null) mostrarDialogoJustificar(asistenciaSeleccionada);
        });

        JButton btnPapeleta = crearBotonAccion("Papeleta Oficial", Iconos.crearIconoDocumento(13, Ui.TEXTO_MUTED), false);
        btnPapeleta.addActionListener(e -> {
            if (asistenciaSeleccionada != null) {
                JOptionPane.showMessageDialog(this,
                        "Generando papeleta digital oficial de asistencia para " + asistenciaSeleccionada.getNombreColaborador(),
                        "Papeleta Digital", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        JButton btnPermisosSub = crearBotonAccion("Permisos Activos", Iconos.crearIconoCalendario(13, Ui.TEXTO_MUTED), false);
        btnPermisosSub.addActionListener(e -> mostrarDialogoGestionPermisos());

        gridAcciones.add(btnMarcar);
        gridAcciones.add(btnJustificar);
        gridAcciones.add(btnPapeleta);
        gridAcciones.add(btnPermisosSub);
        panelHubDetalle.add(gridAcciones, BorderLayout.SOUTH);

        panelHubContenido.add(panelHubVacio, BorderLayout.CENTER);
        return panelHubContenido;
    }

    private JPanel crearCardMiniTelemetria(String titulo, JLabel lblValor, Color colorValor) {
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

    private JPanel crearSeccionCard(String titulo) {
        JPanel sec = new JPanel() {
            private static final long serialVersionUID = 1L;
            @Override
            public Dimension getMaximumSize() {
                return new Dimension(Integer.MAX_VALUE, getPreferredSize().height);
            }
            @Override
            protected void addImpl(Component comp, Object constraints, int index) {
                if (comp instanceof javax.swing.JComponent) {
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

    private JPanel crearItemLinea(String label, Component valor) {
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
        if (c instanceof javax.swing.JComponent) {
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
        filtrarAsistencias();
        actualizarKpis();
    }

    private void actualizarKpis() {
        List<RegistroAsistencia> total = repo.getAsistencias();
        long pres = total.stream().filter(a -> a.getEstado().toLowerCase().contains("presente")).count();
        long ret = total.stream().filter(a -> a.getEstado().toLowerCase().contains("retardo")).count();
        long aus = total.stream().filter(a -> a.getEstado().toLowerCase().contains("ausente")).count();
        long perm = total.stream().filter(a -> a.getEstado().toLowerCase().contains("permiso")).count();

        lblKpiPresentes.setText(String.valueOf(pres));
        lblKpiRetardos.setText(String.valueOf(ret));
        lblKpiAusentes.setText(String.valueOf(aus));
        lblKpiPermisos.setText(String.valueOf(perm));
    }

    private void filtrarAsistencias() {
        listaAsistenciasFiltrada.clear();
        modeloAsistencias.setRowCount(0);

        List<RegistroAsistencia> base = repo.getAsistencias();
        String busq = txtBuscar != null ? txtBuscar.getText().trim().toLowerCase() : "";

        for (RegistroAsistencia a : base) {
            boolean matchPill = filtroEstado.equals("TODOS") ||
                    (filtroEstado.equals("Presentes") && a.getEstado().toLowerCase().contains("presente")) ||
                    (filtroEstado.equals("Retardos") && a.getEstado().toLowerCase().contains("retardo")) ||
                    (filtroEstado.equals("Ausentes") && a.getEstado().toLowerCase().contains("ausente")) ||
                    (filtroEstado.equals("Permisos") && a.getEstado().toLowerCase().contains("permiso"));

            boolean matchBusq = busq.isEmpty() ||
                    a.getNombreColaborador().toLowerCase().contains(busq) ||
                    a.getRol().toLowerCase().contains(busq) ||
                    a.getTurno().toLowerCase().contains(busq) ||
                    (a.getNotaJustificacion() != null && a.getNotaJustificacion().toLowerCase().contains(busq));

            if (matchPill && matchBusq) {
                listaAsistenciasFiltrada.add(a);
                modeloAsistencias.addRow(new Object[]{
                        a.getNombreColaborador(),
                        a.getRol(),
                        a.getTurno(),
                        a.getHoraEntradaReal() != null ? a.getHoraEntradaReal() : "--:--",
                        a.getHoraSalidaReal() != null ? a.getHoraSalidaReal() : "--:--",
                        a.getMinutosRetardo() > 0 ? a.getMinutosRetardo() + " min" : "Puntual",
                        a.getEstado()
                });
            }
        }

        if (!listaAsistenciasFiltrada.isEmpty()) {
            tablaAsistencias.setRowSelectionInterval(0, 0);
        } else {
            mostrarDetalleAsistencia(null);
        }
    }

    private void mostrarDetalleAsistencia(RegistroAsistencia a) {
        asistenciaSeleccionada = a;
        panelHubContenido.removeAll();

        if (a == null) {
            panelHubContenido.add(panelHubVacio, BorderLayout.CENTER);
        } else {
            lblHubAvatar.setText(obtenerIniciales(a.getNombreColaborador()));
            lblHubNombre.setText(a.getNombreColaborador());
            lblHubCargo.setText(a.getRol() + " · " + a.getTurno());

            String st = a.getEstado();
            lblHubChipEstado.setText(st);
            if (st.toLowerCase().contains("presente")) {
                lblHubChipEstado.setBackground(new Color(220, 252, 231));
                lblHubChipEstado.setForeground(new Color(21, 128, 61));
            } else if (st.toLowerCase().contains("retardo")) {
                lblHubChipEstado.setBackground(new Color(254, 243, 199));
                lblHubChipEstado.setForeground(new Color(180, 83, 9));
            } else if (st.toLowerCase().contains("ausente")) {
                lblHubChipEstado.setBackground(new Color(254, 242, 242));
                lblHubChipEstado.setForeground(new Color(220, 38, 38));
            } else {
                lblHubChipEstado.setBackground(new Color(243, 232, 255));
                lblHubChipEstado.setForeground(new Color(126, 34, 206));
            }

            lblHubEntrada.setText(a.getHoraEntradaReal() != null ? a.getHoraEntradaReal() : "--:--");
            lblHubSalida.setText(a.getHoraSalidaReal() != null ? a.getHoraSalidaReal() : "--:--");
            lblHubMinRetardo.setText(a.getMinutosRetardo() > 0 ? "+" + a.getMinutosRetardo() + " min" : "Sin demora");
            lblHubJornada.setText("8.0 hrs");

            txtHubBiometrico.setText("Esperado: " + a.getHoraEntradaEsperada() + " a " + a.getHoraSalidaEsperada() +
                    "\nReal: " + (a.getHoraEntradaReal() != null ? a.getHoraEntradaReal() : "Sin marcar") +
                    " (Marcación " + (a.isMarcadoManual() ? "Manual Supervisada" : "Biométrica Huella") + ")");

            txtHubJustificacion.setText(a.getNotaJustificacion() != null && !a.getNotaJustificacion().isEmpty() ?
                    a.getNotaJustificacion() : "Sin incidencias ni retardos reportados para la jornada de hoy.");

            List<SolicitudPermiso> permisos = repo.getSolicitudesPermisos();
            SolicitudPermiso permColab = permisos.stream()
                    .filter(sp -> sp.getNombreSolicitante().equalsIgnoreCase(a.getNombreColaborador()))
                    .findFirst().orElse(null);

            if (permColab != null) {
                txtHubPermisos.setText("Tipo: " + permColab.getTipoPermiso() + "\nEstado: " + permColab.getEstado() +
                        "\nReemplazo: " + permColab.getReemplazoPropuesto());
            } else {
                txtHubPermisos.setText("No registra solicitudes de permiso ni licencias activas en el período.");
            }

            panelHubContenido.add(panelHubDetalle, BorderLayout.CENTER);
        }

        panelHubContenido.revalidate();
        panelHubContenido.repaint();
    }

    private String obtenerIniciales(String nombre) {
        if (nombre == null || nombre.isEmpty()) return "AS";
        String limpio = nombre.replace("Dra.", "").replace("Dr.", "").trim();
        String[] p = limpio.split("\\s+");
        if (p.length >= 2) {
            return (p[0].substring(0, 1) + p[1].substring(0, 1)).toUpperCase();
        } else if (p.length == 1 && p[0].length() >= 2) {
            return p[0].substring(0, 2).toUpperCase();
        }
        return "AS";
    }

    private void exportarParteAsistencia() {
        String[] cols = new String[]{"Colaborador", "Rol / Cargo", "Turno", "Entrada Real", "Salida Real", "Estado", "Observación"};
        List<Object[]> filas = new ArrayList<>();
        for (RegistroAsistencia a : listaAsistenciasFiltrada) {
            filas.add(new Object[]{
                    a.getNombreColaborador(), a.getRol(), a.getTurno(),
                    a.getHoraEntradaReal() != null ? a.getHoraEntradaReal() : "-",
                    a.getHoraSalidaReal() != null ? a.getHoraSalidaReal() : "-",
                    a.getEstado(),
                    a.getNotaJustificacion() != null ? a.getNotaJustificacion() : "-"
            });
        }
        Ui.mostrarVisorReporte(
                SwingUtilities.getWindowAncestor(this),
                "PARTE DIARIO OFICIAL DE CONTROL DE ASISTENCIAS",
                "Fecha: " + LocalDate.now() + " | Sede Central Veterinaria Happy Pets",
                "RECURSOS HUMANOS · FISCALIZACIÓN Y ASISTENCIAS",
                cols,
                filas,
                "REGISTRO OFICIAL DE ASISTENCIAS",
                "Parte_Asistencia_Diario"
        );
    }

    private void mostrarDialogoPaseListaRapido() {
        JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(this), "Pase de Lista Rápido por Turno", JDialog.ModalityType.APPLICATION_MODAL);
        dlg.setSize(520, 360);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());

        JPanel p = new JPanel(new GridLayout(4, 1, 10, 10));
        p.setBackground(Color.WHITE);
        p.setBorder(new EmptyBorder(20, 24, 20, 24));

        JLabel lblInfo = new JLabel("<html><b>Pase de Lista Colectivo:</b> Seleccione el turno para marcar asistencia puntual masiva a todos los colaboradores programados sin marcación previa.</html>");
        lblInfo.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JComboBox<String> cbTurno = new JComboBox<>(new String[]{"Mañana (08:00 - 15:00)", "Tarde (14:30 - 22:00)", "Noche (21:30 - 07:30)"});
        JTextField txtObs = new JTextField("Pase de lista supervisado por Jefatura de RRHH.");

        p.add(lblInfo);
        p.add(cbTurno);
        p.add(new JLabel("Nota de Supervisión:"));
        p.add(txtObs);

        dlg.add(p, BorderLayout.CENTER);

        JPanel bot = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        bot.setBackground(new Color(248, 250, 252));
        JButton btnCancelar = Ui.boton("Cancelar", false);
        btnCancelar.addActionListener(e -> dlg.dispose());

        JButton btnConfirmar = Ui.boton("Confirmar Marcación Masiva", true);
        btnConfirmar.addActionListener(e -> {
            String hora = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
            for (RegistroAsistencia a : repo.getAsistencias()) {
                if (a.getHoraEntradaReal() == null || a.getHoraEntradaReal().equals("--:--")) {
                    a.setHoraEntradaReal(hora);
                    a.setEstado("Presente");
                    a.setMinutosRetardo(0);
                    a.setNotaJustificacion(txtObs.getText().trim());
                    a.setMarcadoManual(true);
                }
            }
            recargarDatos();
            dlg.dispose();
            JOptionPane.showMessageDialog(this, "Pase de lista colectivo aplicado exitosamente a las " + hora, "Confirmado", JOptionPane.INFORMATION_MESSAGE);
        });

        bot.add(btnCancelar);
        bot.add(btnConfirmar);
        dlg.add(bot, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    private void mostrarDialogoJustificar(RegistroAsistencia a) {
        String nuevaNota = JOptionPane.showInputDialog(this,
                "Ingrese la justificación de retardo, permiso o cambio para " + a.getNombreColaborador() + ":",
                a.getNotaJustificacion() != null ? a.getNotaJustificacion() : "Justificado por constancia médica / autorización de jefatura.");

        if (nuevaNota != null && !nuevaNota.trim().isEmpty()) {
            a.setNotaJustificacion(nuevaNota.trim());
            recargarDatos();
            JOptionPane.showMessageDialog(this, "Justificación guardada correctamente.", "Actualizado", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void mostrarDialogoGestionPermisos() {
        JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(this), "Solicitudes de Permisos y Licencias", JDialog.ModalityType.APPLICATION_MODAL);
        dlg.setSize(620, 420);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());

        String[] cols = {"ID", "Solicitante", "Tipo Permiso", "Reemplazo", "Estado"};
        DefaultTableModel mod = new DefaultTableModel(cols, 0) {
            private static final long serialVersionUID = 1L;
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        for (SolicitudPermiso sp : repo.getSolicitudesPermisos()) {
            mod.addRow(new Object[]{sp.getId(), sp.getNombreSolicitante(), sp.getTipoPermiso(), sp.getReemplazoPropuesto(), sp.getEstado()});
        }

        JTable tab = new JTable(mod);
        tab.setRowHeight(36);
        dlg.add(new JScrollPane(tab), BorderLayout.CENTER);

        JPanel bot = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton btnAprobar = Ui.boton("Aprobar Permiso", true);
        btnAprobar.addActionListener(e -> {
            int r = tab.getSelectedRow();
            if (r >= 0 && r < repo.getSolicitudesPermisos().size()) {
                SolicitudPermiso sp = repo.getSolicitudesPermisos().get(r);
                sp.setEstado("Aprobado");
                mod.setValueAt("Aprobado", r, 4);
                recargarDatos();
                JOptionPane.showMessageDialog(dlg, "Permiso aprobado para " + sp.getNombreSolicitante(), "Aprobado", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        JButton btnCerrar = Ui.boton("Cerrar", false);
        btnCerrar.addActionListener(e -> dlg.dispose());

        bot.add(btnAprobar);
        bot.add(btnCerrar);
        dlg.add(bot, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    private static class ScrollablePanel extends JPanel implements javax.swing.Scrollable {
        private static final long serialVersionUID = 1L;
        @Override public Dimension getPreferredScrollableViewportSize() { return getPreferredSize(); }
        @Override public int getScrollableUnitIncrement(java.awt.Rectangle visibleRect, int orientation, int direction) { return 16; }
        @Override public int getScrollableBlockIncrement(java.awt.Rectangle visibleRect, int orientation, int direction) { return 32; }
        @Override public boolean getScrollableTracksViewportWidth() { return true; }
        @Override public boolean getScrollableTracksViewportHeight() { return false; }
    }
}
