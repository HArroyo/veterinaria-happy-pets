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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
import happypets.model.Veterinario;
import happypets.ui.Iconos;
import happypets.ui.Ui;

/**
 * Submódulo 7.1: Directorio de Especialistas Clínicos Colegiados ("Nuestros Veterinarios").
 * Arquitectura Master-Detail (Opción A):
 * - Master Table: Registro de especialistas, CMPV, especialidad, consultorio y estatus.
 * - Live Specialist Hub: Telemetría 2x2, perfil profesional, contacto y acciones 1-click.
 */
public class VistaVeterinariosPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private final RepositorioVeterinaria repo = RepositorioVeterinaria.getInstancia();

    // KPIs Superiores
    private JLabel lblKpiTotalEspecialistas;
    private JLabel lblKpiEnTurno;
    private JLabel lblKpiAtenciones;
    private JLabel lblKpiRatingPromedio;

    // Master Table (Izquierda)
    private JTable tablaVeterinarios;
    private DefaultTableModel modeloVeterinarios;
    private JTextField txtBuscar;
    private String filtroEspecialidad = "TODOS";
    private final List<Veterinario> listaVeterinariosFiltrada = new ArrayList<>();

    // Live Specialist Hub (Derecha - 360px)
    private JPanel panelHubContenido;
    private JPanel panelHubVacio;
    private JPanel panelHubDetalle;

    private JLabel lblHubAvatar;
    private JLabel lblHubNombre;
    private JLabel lblHubColegiatura;
    private JLabel lblHubChipEstado;

    // Telemetría 2x2
    private JLabel lblHubAniosExp;
    private JLabel lblHubAtenciones;
    private JLabel lblHubConsultorio;
    private JLabel lblHubProximaCita;

    // Secciones de Detalle
    private JTextArea txtHubBio;
    private JTextArea txtHubEspecialidad;
    private JTextArea txtHubContacto;
    private JTextArea txtHubHorario;

    private Veterinario veterinarioSeleccionado;

    public VistaVeterinariosPanel() {
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

        JLabel titulo = new JLabel("Directorio de Especialistas Clínicos Colegiados");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 17));
        titulo.setForeground(Ui.TEXTO_TITULO);

        JLabel subtitulo = new JLabel("Módulo 7.1 · Cuadro médico, especialidades quirúrgicas y disponibilidad en tiempo real");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitulo.setForeground(Ui.TEXTO_MUTED);

        izq.add(titulo);
        izq.add(Box.createVerticalStrut(2));
        izq.add(subtitulo);
        cab.add(izq, BorderLayout.WEST);

        JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        der.setOpaque(false);

        JButton btnNuevo = Ui.boton("+ Registrar Veterinario", true);
        btnNuevo.setIcon(Iconos.crearIconoDoctor(14, Color.WHITE));
        btnNuevo.addActionListener(e -> mostrarDialogoNuevoVeterinario());
        der.add(btnNuevo);

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

        lblKpiTotalEspecialistas = new JLabel("0");
        lblKpiEnTurno = new JLabel("0");
        lblKpiAtenciones = new JLabel("0");
        lblKpiRatingPromedio = new JLabel("5.0 ★");

        fila.add(crearCardKpi("Especialistas Colegiados", lblKpiTotalEspecialistas, "Cuadro médico activo", Iconos.crearIconoDoctor(22, Ui.TURQUESA_PROFUNDO)));
        fila.add(crearCardKpi("Médicos en Turno Activo", lblKpiEnTurno, "Disponibles y en consulta hoy", Iconos.crearIconoTurno(22, Ui.COLOR_EXITO)));
        fila.add(crearCardKpi("Atenciones del Período", lblKpiAtenciones, "Consultas y procedimientos", Iconos.crearIconoDocumento(22, Ui.TURQUESA)));
        fila.add(crearCardKpi("Calificación Asistencial", lblKpiRatingPromedio, "Satisfacción de propietarios", Iconos.crearIconoCheck(22, new Color(217, 119, 6))));

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

        // Cabecera Filtros & Buscador
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
        txtBuscar.putClientProperty("JTextField.placeholderText", "Buscar por nombre, especialidad o colegiatura CMPV...");
        txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filtrarVeterinarios(); }
            public void removeUpdate(DocumentEvent e) { filtrarVeterinarios(); }
            public void changedUpdate(DocumentEvent e) { filtrarVeterinarios(); }
        });
        barraBuscar.add(txtBuscar, BorderLayout.CENTER);
        cabFiltros.add(barraBuscar);
        cabFiltros.add(Box.createVerticalStrut(8));

        // Píldoras de Filtro
        JPanel pills = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        pills.setOpaque(false);

        String[] especialidades = {"TODOS", "Cirugía General", "Dermatología", "Medicina Felina", "Cardiología", "Animales Exóticos", "Urgencias"};
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
                filtrarVeterinarios();
            });
            pills.add(btnPill);
        }
        cabFiltros.add(pills);
        master.add(cabFiltros, BorderLayout.NORTH);

        // Tabla de Veterinarios
        String[] cols = {"Colegiatura", "Médico Especialista", "Especialidad", "Turno / Horario", "Consultorio", "Calificación", "Disponibilidad"};
        modeloVeterinarios = new DefaultTableModel(cols, 0) {
            private static final long serialVersionUID = 1L;
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tablaVeterinarios = new JTable(modeloVeterinarios);
        tablaVeterinarios.setRowHeight(48);
        tablaVeterinarios.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaVeterinarios.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaVeterinarios.setShowVerticalLines(false);
        tablaVeterinarios.setGridColor(new Color(241, 245, 249));
        tablaVeterinarios.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        tablaVeterinarios.getTableHeader().setBackground(new Color(248, 250, 252));
        tablaVeterinarios.getTableHeader().setForeground(Ui.TEXTO_MUTED);

        tablaVeterinarios.getColumnModel().getColumn(0).setPreferredWidth(85);
        tablaVeterinarios.getColumnModel().getColumn(1).setPreferredWidth(190);
        tablaVeterinarios.getColumnModel().getColumn(2).setPreferredWidth(130);
        tablaVeterinarios.getColumnModel().getColumn(3).setPreferredWidth(110);
        tablaVeterinarios.getColumnModel().getColumn(4).setPreferredWidth(100);
        tablaVeterinarios.getColumnModel().getColumn(5).setPreferredWidth(85);
        tablaVeterinarios.getColumnModel().getColumn(6).setPreferredWidth(110);

        // Monograma en columna 1 (Médico Especialista)
        tablaVeterinarios.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
            private static final long serialVersionUID = 1L;
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                JPanel cell = new JPanel(new BorderLayout(8, 0));
                cell.setOpaque(true);
                cell.setBackground(isSelected ? table.getSelectionBackground() : (row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252)));
                cell.setBorder(new EmptyBorder(0, 8, 0, 8));

                String nombre = value != null ? value.toString() : "";
                String iniciales = obtenerIniciales(nombre);

                JLabel avatar = new JLabel(iniciales, SwingConstants.CENTER) {
                    private static final long serialVersionUID = 1L;
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        int s = Math.min(getWidth(), getHeight()) - 2;
                        int x = (getWidth() - s) / 2;
                        int y = (getHeight() - s) / 2;
                        g2.setColor(new Color(224, 242, 254)); // Soft sky blue
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
                lblTexto.setFont(new Font("Segoe UI", Font.BOLD, 12));
                lblTexto.setForeground(isSelected ? table.getSelectionForeground() : Ui.TEXTO_TITULO);

                cell.add(avatar, BorderLayout.WEST);
                cell.add(lblTexto, BorderLayout.CENTER);
                return cell;
            }
        });

        // Columna 5: Calificación con icono estrella dorado
        tablaVeterinarios.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            private static final long serialVersionUID = 1L;
            private final javax.swing.Icon iconoEstrella = Iconos.crearIconoEstrella(14, new Color(245, 158, 11));

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                JPanel cell = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 13));
                cell.setOpaque(true);
                cell.setBackground(isSelected ? table.getSelectionBackground() : (row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252)));

                String calif = value != null ? value.toString().replace("★", "").trim() : "5.0";

                JLabel lblVal = new JLabel(calif);
                lblVal.setFont(new Font("Segoe UI", Font.BOLD, 12));
                lblVal.setForeground(isSelected ? table.getSelectionForeground() : new Color(180, 83, 9));

                JLabel lblIco = new JLabel(iconoEstrella);

                cell.add(lblVal);
                cell.add(lblIco);
                return cell;
            }
        });

        // Chip en columna 6 (Disponibilidad)
        tablaVeterinarios.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
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

                if (st.toLowerCase().contains("disponible")) {
                    badge.setBackground(new Color(220, 252, 231));
                    badge.setForeground(new Color(21, 128, 61));
                } else if (st.toLowerCase().contains("consulta")) {
                    badge.setBackground(new Color(224, 242, 254));
                    badge.setForeground(new Color(3, 105, 161));
                } else if (st.toLowerCase().contains("cirug")) {
                    badge.setBackground(new Color(254, 243, 199));
                    badge.setForeground(new Color(180, 83, 9));
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

        tablaVeterinarios.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int r = tablaVeterinarios.getSelectedRow();
                if (r >= 0 && r < listaVeterinariosFiltrada.size()) {
                    mostrarDetalleVeterinario(listaVeterinariosFiltrada.get(r));
                } else {
                    mostrarDetalleVeterinario(null);
                }
            }
        });

        JScrollPane scroll = new JScrollPane(tablaVeterinarios);
        scroll.setBorder(BorderFactory.createLineBorder(Ui.BORDE_SUAVE, 1));
        master.add(scroll, BorderLayout.CENTER);

        return master;
    }

    private JPanel crearPanelLiveHub() {
        panelHubContenido = new JPanel(new BorderLayout());
        panelHubContenido.setOpaque(false);

        // Panel Vacío
        panelHubVacio = new JPanel(new BorderLayout());
        panelHubVacio.setOpaque(false);
        JLabel lblVacio = new JLabel("Seleccione un especialista para consultar el Live Specialist Hub", SwingConstants.CENTER);
        lblVacio.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblVacio.setForeground(Ui.TEXTO_MUTED);
        panelHubVacio.add(lblVacio, BorderLayout.CENTER);

        // Panel Detalle Activo
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

        // 1. Hero del Veterinario
        JPanel hero = new JPanel(new BorderLayout(10, 0));
        hero.setOpaque(false);
        hero.setBorder(new EmptyBorder(0, 0, 8, 0));

        lblHubAvatar = new JLabel("DR", SwingConstants.CENTER) {
            private static final long serialVersionUID = 1L;
            @Override
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

        JPanel heroTxt = new JPanel();
        heroTxt.setOpaque(false);
        heroTxt.setLayout(new BoxLayout(heroTxt, BoxLayout.Y_AXIS));

        lblHubNombre = new JLabel("Dr. Especialista");
        lblHubNombre.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblHubNombre.setForeground(Ui.TEXTO_TITULO);

        lblHubColegiatura = new JLabel("CMPV 0000 · Cirugía");
        lblHubColegiatura.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblHubColegiatura.setForeground(Ui.TEXTO_MUTED);

        heroTxt.add(lblHubNombre);
        heroTxt.add(Box.createVerticalStrut(2));
        heroTxt.add(lblHubColegiatura);

        lblHubChipEstado = new JLabel("Disponible", SwingConstants.CENTER);
        lblHubChipEstado.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblHubChipEstado.setOpaque(true);
        lblHubChipEstado.setBackground(new Color(220, 252, 231));
        lblHubChipEstado.setForeground(new Color(21, 128, 61));
        lblHubChipEstado.setBorder(new EmptyBorder(4, 8, 4, 8));

        hero.add(lblHubAvatar, BorderLayout.WEST);
        hero.add(heroTxt, BorderLayout.CENTER);
        hero.add(lblHubChipEstado, BorderLayout.EAST);
        panelHubDetalle.add(hero, BorderLayout.NORTH);

        // 2. Scrollable Body
        ScrollablePanel body = new ScrollablePanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setOpaque(false);

        // Telemetría 2x2
        JPanel telemetria = new JPanel(new GridLayout(2, 2, 6, 6));
        telemetria.setOpaque(false);
        telemetria.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        telemetria.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblHubAniosExp = new JLabel("0 años");
        lblHubAtenciones = new JLabel("0 atenciones");
        lblHubConsultorio = new JLabel("Cons. 1");
        lblHubProximaCita = new JLabel("Inmediata");

        telemetria.add(crearCardMiniTelemetria("EXPERIENCIA", lblHubAniosExp, Ui.TURQUESA_OSCURO));
        telemetria.add(crearCardMiniTelemetria("ATENCIONES", lblHubAtenciones, Ui.TEXTO_TITULO));
        telemetria.add(crearCardMiniTelemetria("CONSULTORIO", lblHubConsultorio, Ui.TURQUESA_PROFUNDO));
        telemetria.add(crearCardMiniTelemetria("PRÓXIMA CITA", lblHubProximaCita, new Color(217, 119, 6)));

        body.add(telemetria);
        body.add(vGap(8));

        // Secciones de Detalle
        JPanel secBio = crearSeccionCard("Perfil y Especialidades Clínicas");
        txtHubEspecialidad = new JTextArea();
        configurarTextAreaItem(txtHubEspecialidad);
        txtHubBio = new JTextArea();
        configurarTextAreaItem(txtHubBio);
        secBio.add(crearItemLinea("Especialidad:", txtHubEspecialidad));
        secBio.add(vGap(4));
        secBio.add(crearItemLinea("Biografía:", txtHubBio));
        body.add(secBio);
        body.add(vGap(6));

        JPanel secContacto = crearSeccionCard("Contacto y Canales Directos");
        txtHubContacto = new JTextArea();
        configurarTextAreaItem(txtHubContacto);
        secContacto.add(crearItemLinea("Canales:", txtHubContacto));
        body.add(secContacto);
        body.add(vGap(6));

        JPanel secHorario = crearSeccionCard("Jornada de Guardia y Turnos");
        txtHubHorario = new JTextArea();
        configurarTextAreaItem(txtHubHorario);
        secHorario.add(crearItemLinea("Horario:", txtHubHorario));
        body.add(secHorario);

        JScrollPane scrollBody = new JScrollPane(body);
        scrollBody.setBorder(null);
        scrollBody.setOpaque(false);
        scrollBody.getViewport().setOpaque(false);
        panelHubDetalle.add(scrollBody, BorderLayout.CENTER);

        // 3. Grid de Acciones 1-Click (2x2)
        JPanel gridAcciones = new JPanel(new GridLayout(2, 2, 6, 6));
        gridAcciones.setOpaque(false);
        gridAcciones.setBorder(new EmptyBorder(8, 0, 0, 0));

        JButton btnFicha = crearBotonAccion("Ficha Colegiada", Iconos.crearIconoDocumento(13, Color.WHITE), true);
        btnFicha.addActionListener(e -> {
            if (veterinarioSeleccionado != null) mostrarFichaVeterinario(veterinarioSeleccionado);
        });

        JButton btnReservar = crearBotonAccion("Reservar Cita", Iconos.crearIconoCalendario(13, Ui.TURQUESA_OSCURO), false);
        btnReservar.addActionListener(e -> {
            if (veterinarioSeleccionado != null) mostrarDialogoReservarCita(veterinarioSeleccionado);
        });

        JButton btnEditar = crearBotonAccion("Editar Perfil", Iconos.crearIconoEditar(13, Ui.TEXTO_MUTED), false);
        btnEditar.addActionListener(e -> {
            if (veterinarioSeleccionado != null) mostrarDialogoEditarVeterinario(veterinarioSeleccionado);
        });

        JButton btnGuardia = crearBotonAccion("Asignar Guardia", Iconos.crearIconoTurno(13, Ui.TEXTO_MUTED), false);
        btnGuardia.addActionListener(e -> {
            if (veterinarioSeleccionado != null) {
                JOptionPane.showMessageDialog(this,
                        "Turno de guardia clínica actualizado para " + veterinarioSeleccionado.getNombreCompleto() + " en " + veterinarioSeleccionado.getConsultorioHabitual(),
                        "Programación Asistencial", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        gridAcciones.add(btnFicha);
        gridAcciones.add(btnReservar);
        gridAcciones.add(btnEditar);
        gridAcciones.add(btnGuardia);
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
        filtrarVeterinarios();
        actualizarKpis();
    }

    private void actualizarKpis() {
        List<Veterinario> total = repo.getVeterinarios();
        lblKpiTotalEspecialistas.setText(String.valueOf(total.size()));

        long enTurno = total.stream().filter(v -> v.getEstadoDisponibilidad().toLowerCase().contains("disponible") ||
                v.getEstadoDisponibilidad().toLowerCase().contains("consulta")).count();
        lblKpiEnTurno.setText(String.valueOf(enTurno));

        int atenciones = total.stream().mapToInt(Veterinario::getTotalAtencionesRealizadas).sum();
        lblKpiAtenciones.setText(String.valueOf(atenciones));

        double promRating = total.stream().mapToDouble(Veterinario::getCalificacionEstrellas).average().orElse(5.0);
        lblKpiRatingPromedio.setText(String.format("%.1f ★", promRating));
    }

    private void filtrarVeterinarios() {
        listaVeterinariosFiltrada.clear();
        modeloVeterinarios.setRowCount(0);

        List<Veterinario> base = repo.getVeterinarios();
        String busq = txtBuscar != null ? txtBuscar.getText().trim().toLowerCase() : "";

        for (Veterinario v : base) {
            boolean matchPill = filtroEspecialidad.equals("TODOS") || v.getEspecialidad().toLowerCase().contains(filtroEspecialidad.toLowerCase());
            boolean matchBusq = busq.isEmpty() ||
                    v.getNombreCompleto().toLowerCase().contains(busq) ||
                    v.getEspecialidad().toLowerCase().contains(busq) ||
                    v.getColegiaturaCMPV().toLowerCase().contains(busq);

            if (matchPill && matchBusq) {
                listaVeterinariosFiltrada.add(v);
                modeloVeterinarios.addRow(new Object[]{
                        v.getColegiaturaCMPV(),
                        v.getNombreCompleto(),
                        v.getEspecialidad(),
                        v.getHorarioTurno(),
                        v.getConsultorioHabitual(),
                        String.format("%.1f ★", v.getCalificacionEstrellas()),
                        v.getEstadoDisponibilidad()
                });
            }
        }

        if (!listaVeterinariosFiltrada.isEmpty()) {
            tablaVeterinarios.setRowSelectionInterval(0, 0);
        } else {
            mostrarDetalleVeterinario(null);
        }
    }

    private void mostrarDetalleVeterinario(Veterinario v) {
        veterinarioSeleccionado = v;
        panelHubContenido.removeAll();

        if (v == null) {
            panelHubContenido.add(panelHubVacio, BorderLayout.CENTER);
        } else {
            lblHubAvatar.setText(obtenerIniciales(v.getNombreCompleto()));
            lblHubNombre.setText(v.getNombreCompleto());
            lblHubColegiatura.setText(v.getColegiaturaCMPV() + " · " + v.getEspecialidad());

            String st = v.getEstadoDisponibilidad();
            lblHubChipEstado.setText(st);
            if (st.toLowerCase().contains("disponible")) {
                lblHubChipEstado.setBackground(new Color(220, 252, 231));
                lblHubChipEstado.setForeground(new Color(21, 128, 61));
            } else if (st.toLowerCase().contains("consulta")) {
                lblHubChipEstado.setBackground(new Color(224, 242, 254));
                lblHubChipEstado.setForeground(new Color(3, 105, 161));
            } else {
                lblHubChipEstado.setBackground(new Color(254, 243, 199));
                lblHubChipEstado.setForeground(new Color(180, 83, 9));
            }

            lblHubAniosExp.setText(v.getAniosExperiencia() + " años");
            lblHubAtenciones.setText(v.getTotalAtencionesRealizadas() + " atenciones");
            lblHubConsultorio.setText(v.getConsultorioHabitual());
            lblHubProximaCita.setText(v.getProximaCitaLibre());

            txtHubEspecialidad.setText(v.getEspecialidad() + " (Registro Sanitario Acreditado)");
            txtHubBio.setText(v.getDescripcionBio());
            txtHubContacto.setText("Tel: " + v.getTelefono() + "\nEmail: " + v.getEmail());
            txtHubHorario.setText("Días: " + v.getDiasAtencion() + "\nTurno: " + v.getHorarioTurno());

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

    private void mostrarFichaVeterinario(Veterinario v) {
        JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(this), "Ficha del Especialista: " + v.getNombreCompleto(), JDialog.ModalityType.APPLICATION_MODAL);
        dlg.setSize(620, 520);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());

        JPanel p = new JPanel(new BorderLayout(14, 14));
        p.setBackground(Color.WHITE);
        p.setBorder(new EmptyBorder(20, 24, 20, 24));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        JLabel tit = new JLabel(v.getNombreCompleto());
        tit.setFont(new Font("Segoe UI", Font.BOLD, 18));
        tit.setForeground(Ui.TEXTO_TITULO);

        JLabel sub = new JLabel(v.getColegiaturaCMPV() + " · Especialista en " + v.getEspecialidad());
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sub.setForeground(Ui.TEXTO_MUTED);

        top.add(tit, BorderLayout.NORTH);
        top.add(sub, BorderLayout.CENTER);
        p.add(top, BorderLayout.NORTH);

        JTextArea ta = new JTextArea();
        ta.setEditable(false);
        ta.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        ta.setLineWrap(true);
        ta.setWrapStyleWord(true);
        ta.setText("═══════════════════════════════════════════════════════════\n" +
                "  INFORMACIÓN COLEGIADA Y ASISTENCIAL DE ESPECIALISTA\n" +
                "═══════════════════════════════════════════════════════════\n\n" +
                "• Nombre Oficial: " + v.getNombreCompleto() + "\n" +
                "• Matrícula CMPV: " + v.getColegiaturaCMPV() + "\n" +
                "• Especialidad Primaria: " + v.getEspecialidad() + "\n" +
                "• Años de Experiencia: " + v.getAniosExperiencia() + " años\n" +
                "• Calificación Institucional: " + v.getCalificacionEstrellas() + " / 5.0 ★\n" +
                "• Atenciones Realizadas: " + v.getTotalAtencionesRealizadas() + " atenciones\n\n" +
                "• Consultorio Asignado: " + v.getConsultorioHabitual() + "\n" +
                "• Días de Guardia: " + v.getDiasAtencion() + "\n" +
                "• Turno: " + v.getHorarioTurno() + "\n" +
                "• Disponibilidad Actual: " + v.getEstadoDisponibilidad() + "\n" +
                "• Próximo Espacio Libre: " + v.getProximaCitaLibre() + "\n\n" +
                "• Canales de Contacto: " + v.getTelefono() + " | " + v.getEmail() + "\n\n" +
                "• Síntesis Curricular:\n  " + v.getDescripcionBio() + "\n");
        p.add(new JScrollPane(ta), BorderLayout.CENTER);

        JButton btnCerrar = Ui.boton("Cerrar Ficha", false);
        btnCerrar.addActionListener(e -> dlg.dispose());
        JPanel bot = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bot.setOpaque(false);
        bot.add(btnCerrar);
        p.add(bot, BorderLayout.SOUTH);

        dlg.add(p);
        dlg.setVisible(true);
    }

    private void mostrarDialogoReservarCita(Veterinario v) {
        String msg = "¿Desea derivar un paciente o agendar cita directa con " + v.getNombreCompleto() + "?\n" +
                "Próximo turno libre: " + v.getProximaCitaLibre() + " en " + v.getConsultorioHabitual();
        int opt = JOptionPane.showConfirmDialog(this, msg, "Agendar Derivación", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (opt == JOptionPane.YES_OPTION) {
            JOptionPane.showMessageDialog(this, "Cita agendada exitosamente en la agenda del especialista.", "Confirmación", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void mostrarDialogoNuevoVeterinario() {
        mostrarFormularioVeterinario(null);
    }

    private void mostrarDialogoEditarVeterinario(Veterinario v) {
        mostrarFormularioVeterinario(v);
    }

    private void mostrarFormularioVeterinario(Veterinario existente) {
        boolean esEdicion = existente != null;
        JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(this),
                esEdicion ? "Editar Especialista Médico" : "Registrar Nuevo Especialista Clínico",
                JDialog.ModalityType.APPLICATION_MODAL);
        dlg.setSize(540, 520);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(new EmptyBorder(18, 22, 18, 22));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField txtNombre = new JTextField(esEdicion ? existente.getNombreCompleto() : "");
        JTextField txtCMPV = new JTextField(esEdicion ? existente.getColegiaturaCMPV() : "CMPV-");
        JComboBox<String> cbEsp = new JComboBox<>(new String[]{"Cirugía General", "Dermatología", "Medicina Felina", "Cardiología", "Animales Exóticos", "Urgencias", "Oncología"});
        if (esEdicion) cbEsp.setSelectedItem(existente.getEspecialidad());
        JTextField txtExp = new JTextField(esEdicion ? String.valueOf(existente.getAniosExperiencia()) : "5");
        JTextField txtConsultorio = new JTextField(esEdicion ? existente.getConsultorioHabitual() : "Consultorio 1");
        JTextField txtDias = new JTextField(esEdicion ? existente.getDiasAtencion() : "Lunes a Viernes");
        JTextField txtTurno = new JTextField(esEdicion ? existente.getHorarioTurno() : "08:00 - 16:00");
        JTextField txtTel = new JTextField(esEdicion ? existente.getTelefono() : "+51 987 654 321");
        JTextField txtEmail = new JTextField(esEdicion ? existente.getEmail() : "medico@happypets.pe");

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row; form.add(new JLabel("Nombre Completo:"), gbc);
        gbc.gridx = 1; form.add(txtNombre, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; form.add(new JLabel("Colegiatura CMPV:"), gbc);
        gbc.gridx = 1; form.add(txtCMPV, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; form.add(new JLabel("Especialidad:"), gbc);
        gbc.gridx = 1; form.add(cbEsp, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; form.add(new JLabel("Años Experiencia:"), gbc);
        gbc.gridx = 1; form.add(txtExp, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; form.add(new JLabel("Consultorio:"), gbc);
        gbc.gridx = 1; form.add(txtConsultorio, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; form.add(new JLabel("Días Atención:"), gbc);
        gbc.gridx = 1; form.add(txtDias, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; form.add(new JLabel("Turno / Horario:"), gbc);
        gbc.gridx = 1; form.add(txtTurno, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; form.add(new JLabel("Teléfono Móvil:"), gbc);
        gbc.gridx = 1; form.add(txtTel, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; form.add(new JLabel("Correo Electrónico:"), gbc);
        gbc.gridx = 1; form.add(txtEmail, gbc);

        dlg.add(form, BorderLayout.CENTER);

        JPanel bot = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        bot.setBackground(new Color(248, 250, 252));
        JButton btnCancelar = Ui.boton("Cancelar", false);
        btnCancelar.addActionListener(e -> dlg.dispose());

        JButton btnGuardar = Ui.boton(esEdicion ? "Guardar Cambios" : "Registrar Especialista", true);
        btnGuardar.addActionListener(e -> {
            String nom = txtNombre.getText().trim();
            if (nom.isEmpty()) {
                JOptionPane.showMessageDialog(dlg, "El nombre del especialista es obligatorio.", "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int exp = 3;
            try { exp = Integer.parseInt(txtExp.getText().trim()); } catch (Exception ignored) {}

            if (esEdicion) {
                existente.setNombreCompleto(nom);
                existente.setColegiaturaCMPV(txtCMPV.getText().trim());
                existente.setEspecialidad((String) cbEsp.getSelectedItem());
                existente.setAniosExperiencia(exp);
                existente.setConsultorioHabitual(txtConsultorio.getText().trim());
                existente.setDiasAtencion(txtDias.getText().trim());
                existente.setHorarioTurno(txtTurno.getText().trim());
                existente.setTelefono(txtTel.getText().trim());
                existente.setEmail(txtEmail.getText().trim());
            } else {
                Veterinario nuevo = new Veterinario(
                        "VET-" + (repo.getVeterinarios().size() + 1),
                        nom,
                        txtCMPV.getText().trim(),
                        (String) cbEsp.getSelectedItem(),
                        exp,
                        "Especialista médico colegiado incorporado a la plana clínica de Veterinaria Happy Pets.",
                        txtDias.getText().trim(),
                        txtTurno.getText().trim(),
                        "Hoy 16:30",
                        "Disponible",
                        txtTel.getText().trim(),
                        txtEmail.getText().trim(),
                        5.0,
                        0,
                        txtConsultorio.getText().trim()
                );
                repo.getVeterinarios().add(nuevo);
            }
            recargarDatos();
            dlg.dispose();
            JOptionPane.showMessageDialog(this, "Especialista registrado correctamente en el cuadro médico.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        });

        bot.add(btnCancelar);
        bot.add(btnGuardar);
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
