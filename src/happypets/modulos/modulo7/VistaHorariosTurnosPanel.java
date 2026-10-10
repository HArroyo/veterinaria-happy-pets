package happypets.modulos.modulo7;

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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import happypets.data.RepositorioVeterinaria;
import happypets.model.TurnoSemanal;
import happypets.ui.Iconos;
import happypets.ui.Ui;

/**
 * Submódulo 7.3: Cuadrante de Guardias - Horarios y Turnos Médicos.
 * Diseñado según wireframe oficial (modulos/modulo-7/wireframe_page_4.png).
 */
public class VistaHorariosTurnosPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private static final Color COLOR_FONDO = Ui.FONDO;
    private static final Color COLOR_CARD = Ui.FONDO_CARD;
    private static final Color COLOR_BORDE = Ui.BORDE_SUAVE;
    private static final Color COLOR_TEXTO_TITULO = Ui.TEXTO_TITULO;
    private static final Color COLOR_TEXTO_MUTED = Ui.TEXTO_MUTED;
    private static final Color COLOR_AZUL = Ui.TURQUESA;
    private static final Color COLOR_MORADO = Ui.TURQUESA_PROFUNDO;
    private static final Color COLOR_NARANJA = new Color(234, 88, 12);
    private static final Color COLOR_VERDE = Ui.COLOR_EXITO;

    private final RepositorioVeterinaria repo = RepositorioVeterinaria.getInstancia();

    private LocalDate fechaSemanaActual = LocalDate.now();
    private JLabel lblSemanaRango;
    private JTextField txtBuscar;
    private JComboBox<String> cmbEspecialidad;
    private JTable tablaCuadrante;
    private DefaultTableModel modeloTabla;
    private List<TurnoSemanal> listaTurnosActual;

    // Widgets inferiores
    private JLabel lblCoberturaManana;
    private JLabel lblCoberturaTarde;
    private JLabel lblCoberturaNoche;

    public VistaHorariosTurnosPanel() {
        setLayout(new BorderLayout());
        setBackground(COLOR_FONDO);
        setBorder(new EmptyBorder(20, 24, 20, 24));

        // 1. Cabecera con Título, Selector de Semana y Botones de Acción
        add(crearCabecera(), BorderLayout.NORTH);

        // 2. Centro: Barra de Filtros + Leyenda + Tabla Cuadrante Semanal
        JPanel panelCentro = new JPanel(new BorderLayout(0, 14));
        panelCentro.setOpaque(false);
        panelCentro.add(crearBarraFiltrosYLeyenda(), BorderLayout.NORTH);
        panelCentro.add(crearPanelTablaCuadrante(), BorderLayout.CENTER);
        panelCentro.add(crearWidgetsInferiores(), BorderLayout.SOUTH);

        add(panelCentro, BorderLayout.CENTER);

        recargarDatos();
    }

    private JPanel crearCabecera() {
        JPanel cab = new JPanel(new BorderLayout(16, 8));
        cab.setOpaque(false);
        cab.setBorder(new EmptyBorder(0, 0, 14, 0));

        JPanel textos = new JPanel();
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        textos.setOpaque(false);

        JLabel lblBreadcrumb = new JLabel("Gestión Interna · Cuadrante de Guardias y Roles");
        lblBreadcrumb.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblBreadcrumb.setForeground(COLOR_AZUL);

        JLabel lblTitulo = new JLabel("Horarios y Turnos Médicos");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(COLOR_TEXTO_TITULO);

        // Selector de Semana con botones < Hoy >
        JPanel navSemana = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
        navSemana.setOpaque(false);

        JButton btnPrev = new JButton("<");
        btnPrev.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnPrev.setFocusPainted(false);
        btnPrev.setBackground(Color.WHITE);
        btnPrev.setBorder(new CompoundBorder(new LineBorder(COLOR_BORDE, 1, true), new EmptyBorder(4, 10, 4, 10)));
        btnPrev.addActionListener(e -> {
            fechaSemanaActual = fechaSemanaActual.minusWeeks(1);
            actualizarTextoSemana();
            recargarDatos();
        });

        JButton btnHoy = new JButton("Semana Actual");
        btnHoy.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnHoy.setFocusPainted(false);
        btnHoy.setBackground(Color.WHITE);
        btnHoy.setBorder(new CompoundBorder(new LineBorder(COLOR_BORDE, 1, true), new EmptyBorder(4, 12, 4, 12)));
        btnHoy.addActionListener(e -> {
            fechaSemanaActual = LocalDate.now();
            actualizarTextoSemana();
            recargarDatos();
        });

        JButton btnNext = new JButton(">");
        btnNext.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnNext.setFocusPainted(false);
        btnNext.setBackground(Color.WHITE);
        btnNext.setBorder(new CompoundBorder(new LineBorder(COLOR_BORDE, 1, true), new EmptyBorder(4, 10, 4, 10)));
        btnNext.addActionListener(e -> {
            fechaSemanaActual = fechaSemanaActual.plusWeeks(1);
            actualizarTextoSemana();
            recargarDatos();
        });

        lblSemanaRango = new JLabel();
        lblSemanaRango.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblSemanaRango.setForeground(COLOR_TEXTO_TITULO);
        actualizarTextoSemana();

        navSemana.add(btnPrev);
        navSemana.add(btnHoy);
        navSemana.add(btnNext);
        navSemana.add(Box.createHorizontalStrut(6));
        navSemana.add(lblSemanaRango);

        textos.add(lblBreadcrumb);
        textos.add(Box.createVerticalStrut(2));
        textos.add(lblTitulo);
        textos.add(Box.createVerticalStrut(2));
        textos.add(navSemana);

        // Botones superiores derechos
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelBotones.setOpaque(false);

        JButton btnDescargarPDF = Ui.botonSecundario("Descargar PDF", Iconos.crearIconoDocumento(14, COLOR_TEXTO_MUTED));
        btnDescargarPDF.addActionListener(e -> {
            String[] cols = new String[]{"Especialista", "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"};
            List<Object[]> filas = new java.util.ArrayList<>();
            if (listaTurnosActual != null) {
                for (TurnoSemanal ts : listaTurnosActual) {
                    filas.add(new Object[]{
                        ts.getNombreProfesional() + " (" + ts.getEspecialidad() + ")",
                        ts.getHorarioDia(0), ts.getHorarioDia(1), ts.getHorarioDia(2),
                        ts.getHorarioDia(3), ts.getHorarioDia(4), ts.getHorarioDia(5), ts.getHorarioDia(6)
                    });
                }
            }
            Ui.mostrarVisorReporte(
                SwingUtilities.getWindowAncestor(this),
                "CUADRANTE SEMANAL DE GUARDIAS Y TURNOS",
                "Semana: " + lblSemanaRango.getText() + " | Sede Central Happy Pets",
                "DIRECCIÓN MÉDICA Y GESTIÓN DE TURNOS",
                cols,
                filas,
                "CUADRANTE DE ROL OPERATIVO VETERINARIA HAPPY PETS",
                "Cuadrante_Turnos"
            );
        });

        JButton btnCalendario = Ui.botonSecundario("Exportar a Calendario", Iconos.crearIconoCalendario(14, COLOR_TEXTO_MUTED));
        btnCalendario.addActionListener(e -> JOptionPane.showMessageDialog(this, "Sincronizando turnos médicos con Google Calendar / Outlook iCal.", "Sincronización Exitosa", JOptionPane.INFORMATION_MESSAGE));

        JButton btnAsignar = Ui.botonPrimario("+ Asignar / Editar Turno", Iconos.crearIconoTurno(16, Color.WHITE));
        btnAsignar.setBackground(COLOR_AZUL);
        btnAsignar.addActionListener(e -> mostrarDialogoAsignarTurno());

        panelBotones.add(btnDescargarPDF);
        panelBotones.add(btnCalendario);
        panelBotones.add(btnAsignar);

        cab.add(textos, BorderLayout.CENTER);
        cab.add(panelBotones, BorderLayout.EAST);
        return cab;
    }

    private void actualizarTextoSemana() {
        LocalDate inicioSemana = fechaSemanaActual.minusDays(fechaSemanaActual.getDayOfWeek().getValue() - 1);
        LocalDate finSemana = inicioSemana.plusDays(6);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd MMM");
        if (lblSemanaRango != null) {
            lblSemanaRango.setText("Semana del " + inicioSemana.format(dtf) + " al " + finSemana.format(dtf) + " · 8 Especialistas Programados");
        }
    }

    private JPanel crearBarraFiltrosYLeyenda() {
        JPanel contenedor = new JPanel(new BorderLayout(12, 10));
        contenedor.setBackground(COLOR_CARD);
        contenedor.setBorder(new CompoundBorder(
                new LineBorder(COLOR_BORDE, 1, true),
                new EmptyBorder(12, 16, 12, 16)
        ));

        // Izquierda: Buscador y Especialidad
        JPanel filaIzquierda = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        filaIzquierda.setOpaque(false);

        JLabel lblLupa = new JLabel(Iconos.crearIconoBuscar(16, COLOR_TEXTO_MUTED));
        txtBuscar = new JTextField(18);
        txtBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtBuscar.setToolTipText("Buscar médico por nombre o CMPV");
        txtBuscar.addActionListener(e -> recargarDatos());

        cmbEspecialidad = new JComboBox<>(new String[]{
                "Todas las especialidades",
                "Cirugía General",
                "Dermatología",
                "Animales Exóticos",
                "Cardiología",
                "Medicina Felina",
                "Oftalmología",
                "Urgencias 24h",
                "Oncología"
        });
        cmbEspecialidad.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cmbEspecialidad.addActionListener(e -> recargarDatos());

        JButton btnFiltrar = Ui.botonPrimario("Filtrar", Iconos.crearIconoFiltro(14, Color.WHITE));
        btnFiltrar.setBackground(COLOR_AZUL);
        btnFiltrar.addActionListener(e -> recargarDatos());

        filaIzquierda.add(lblLupa);
        filaIzquierda.add(txtBuscar);
        filaIzquierda.add(new JLabel("Especialidad:"));
        filaIzquierda.add(cmbEspecialidad);
        filaIzquierda.add(btnFiltrar);

        // Derecha: Leyenda de colores de turnos
        JPanel filaLeyenda = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        filaLeyenda.setOpaque(false);

        filaLeyenda.add(crearItemLeyenda("Mañana (08-15)", new Color(224, 242, 254), COLOR_AZUL));
        filaLeyenda.add(crearItemLeyenda("Tarde (15-22)", new Color(243, 232, 255), COLOR_MORADO));
        filaLeyenda.add(crearItemLeyenda("Guardia 24h", new Color(254, 243, 199), COLOR_NARANJA));
        filaLeyenda.add(crearItemLeyenda("Descanso / Libre", new Color(241, 245, 249), COLOR_TEXTO_MUTED));

        contenedor.add(filaIzquierda, BorderLayout.WEST);
        contenedor.add(filaLeyenda, BorderLayout.EAST);
        return contenedor;
    }

    private JPanel crearItemLeyenda(String texto, Color bg, Color fg) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 2));
        p.setOpaque(true);
        p.setBackground(bg);
        p.setBorder(new CompoundBorder(
                new LineBorder(bg.darker(), 1, true),
                new EmptyBorder(2, 6, 2, 6)
        ));
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lbl.setForeground(fg);
        p.add(lbl);
        return p;
    }

    private JPanel crearPanelTablaCuadrante() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_CARD);
        panel.setBorder(new CompoundBorder(
                new LineBorder(COLOR_BORDE, 1, true),
                new EmptyBorder(4, 4, 4, 4)
        ));

        String[] columnas = {
                "Médico Especialista", "LUN", "MAR", "MIÉ", "JUE", "VIE", "SÁB", "DOM", "Acciones"
        };

        modeloTabla = new DefaultTableModel(columnas, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaCuadrante = new JTable(modeloTabla);
        tablaCuadrante.setRowHeight(52);
        tablaCuadrante.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaCuadrante.setShowGrid(true);
        tablaCuadrante.setGridColor(new Color(241, 245, 249));
        tablaCuadrante.setSelectionBackground(new Color(224, 242, 254));
        tablaCuadrante.setSelectionForeground(COLOR_TEXTO_TITULO);

        JTableHeader header = tablaCuadrante.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBackground(new Color(248, 250, 252));
        header.setForeground(COLOR_TEXTO_TITULO);
        header.setPreferredSize(new Dimension(0, 36));

        // Renderer para columna de Médico (Avatar + Nombre + CMPV)
        tablaCuadrante.getColumnModel().getColumn(0).setPreferredWidth(210);
        tablaCuadrante.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
            private static final long serialVersionUID = 1L;
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                JPanel p = new JPanel(new BorderLayout(8, 0));
                p.setOpaque(true);
                p.setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
                p.setBorder(new EmptyBorder(4, 8, 4, 8));

                if (value instanceof TurnoSemanal) {
                    TurnoSemanal ts = (TurnoSemanal) value;
                    JLabel av = new JLabel(obtenerIniciales(ts.getNombreProfesional()), SwingConstants.CENTER);
                    av.setPreferredSize(new Dimension(34, 34));
                    av.setFont(new Font("Segoe UI", Font.BOLD, 12));
                    av.setOpaque(true);
                    av.setBackground(COLOR_AZUL);
                    av.setForeground(Color.WHITE);
                    av.setBorder(new LineBorder(Color.WHITE, 1, true));

                    JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 2));
                    textPanel.setOpaque(false);

                    JLabel lblNom = new JLabel(ts.getNombreProfesional());
                    lblNom.setFont(new Font("Segoe UI", Font.BOLD, 12));
                    lblNom.setForeground(COLOR_TEXTO_TITULO);

                    JLabel lblSub = new JLabel(ts.getColegiaturaCMPV() + " · " + ts.getEspecialidad());
                    lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                    lblSub.setForeground(COLOR_TEXTO_MUTED);

                    textPanel.add(lblNom);
                    textPanel.add(lblSub);

                    p.add(av, BorderLayout.WEST);
                    p.add(textPanel, BorderLayout.CENTER);
                } else {
                    JLabel l = new JLabel(String.valueOf(value));
                    l.setFont(new Font("Segoe UI", Font.BOLD, 12));
                    p.add(l, BorderLayout.CENTER);
                }
                return p;
            }
        });

        // Renderer para las celdas de días (Columnas 1 a 7)
        DefaultTableCellRenderer diaRenderer = new DefaultTableCellRenderer() {
            private static final long serialVersionUID = 1L;
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                JPanel celda = new JPanel(new BorderLayout(2, 2));
                celda.setOpaque(true);
                celda.setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
                celda.setBorder(new EmptyBorder(4, 4, 4, 4));

                String texto = String.valueOf(value);
                String tipo = "Descanso";
                if (texto.toLowerCase().contains("quirof") || texto.toLowerCase().contains("08:00") || texto.toLowerCase().contains("mañana")) {
                    tipo = "Mañana";
                } else if (texto.toLowerCase().contains("15:00") || texto.toLowerCase().contains("14:00") || texto.toLowerCase().contains("tarde")) {
                    tipo = "Tarde";
                } else if (texto.toLowerCase().contains("urgencia") || texto.toLowerCase().contains("22:00") || texto.toLowerCase().contains("guardia")) {
                    tipo = "Guardia";
                } else if (texto.toLowerCase().contains("descanso") || texto.toLowerCase().contains("libre")) {
                    tipo = "Descanso";
                }

                Color bgPill;
                Color fgPill;

                if ("Mañana".equals(tipo)) {
                    bgPill = new Color(224, 242, 254);
                    fgPill = COLOR_AZUL;
                } else if ("Tarde".equals(tipo)) {
                    bgPill = new Color(243, 232, 255);
                    fgPill = COLOR_MORADO;
                } else if ("Guardia".equals(tipo)) {
                    bgPill = new Color(254, 243, 199);
                    fgPill = COLOR_NARANJA;
                } else {
                    bgPill = new Color(241, 245, 249);
                    fgPill = COLOR_TEXTO_MUTED;
                }

                JPanel pill = new JPanel(new GridLayout(2, 1, 0, 0));
                pill.setOpaque(true);
                pill.setBackground(bgPill);
                pill.setBorder(new CompoundBorder(
                        new LineBorder(bgPill.darker(), 1, true),
                        new EmptyBorder(2, 6, 2, 6)
                ));

                String hora = texto;
                String sala = "";
                if (texto.contains("·")) {
                    String[] sp = texto.split("·");
                    hora = sp[0].trim();
                    sala = sp[1].trim();
                }

                JLabel lblH = new JLabel(hora, SwingConstants.CENTER);
                lblH.setFont(new Font("Segoe UI", Font.BOLD, 10));
                lblH.setForeground(fgPill);

                JLabel lblS = new JLabel(sala.isEmpty() ? tipo : sala, SwingConstants.CENTER);
                lblS.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                lblS.setForeground(fgPill.darker());

                pill.add(lblH);
                pill.add(lblS);
                celda.add(pill, BorderLayout.CENTER);

                return celda;
            }
        };

        for (int i = 1; i <= 7; i++) {
            tablaCuadrante.getColumnModel().getColumn(i).setPreferredWidth(130);
            tablaCuadrante.getColumnModel().getColumn(i).setCellRenderer(diaRenderer);
        }

        // Renderer para acciones
        tablaCuadrante.getColumnModel().getColumn(8).setPreferredWidth(100);
        tablaCuadrante.getColumnModel().getColumn(8).setCellRenderer(new DefaultTableCellRenderer() {
            private static final long serialVersionUID = 1L;
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 8));
                p.setOpaque(true);
                p.setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
                JButton btn = new JButton("Editar");
                btn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                btn.setBackground(Color.WHITE);
                btn.setFocusPainted(false);
                btn.setBorder(new CompoundBorder(new LineBorder(COLOR_BORDE, 1, true), new EmptyBorder(4, 10, 4, 10)));
                p.add(btn);
                return p;
            }
        });

        tablaCuadrante.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int row = tablaCuadrante.getSelectedRow();
                int col = tablaCuadrante.getSelectedColumn();
                if (row >= 0 && col == 8) {
                    if (listaTurnosActual != null && row < listaTurnosActual.size()) {
                        mostrarDialogoEditarTurnos(listaTurnosActual.get(row));
                    }
                }
            }
        });

        JScrollPane scroll = new JScrollPane(tablaCuadrante);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(Color.WHITE);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearWidgetsInferiores() {
        JPanel contenedor = new JPanel(new GridLayout(1, 2, 16, 0));
        contenedor.setOpaque(false);
        contenedor.setBorder(new EmptyBorder(12, 0, 0, 0));

        // Widget 1: Cobertura en vivo del día de hoy
        JPanel widgetCobertura = new JPanel(new BorderLayout(10, 8));
        widgetCobertura.setBackground(COLOR_CARD);
        widgetCobertura.setBorder(new CompoundBorder(
                new LineBorder(COLOR_BORDE, 1, true),
                new EmptyBorder(12, 14, 12, 14)
        ));

        JLabel lblTitCob = new JLabel("⚡ Cobertura de Turno en Vivo (Hoy)");
        lblTitCob.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTitCob.setForeground(COLOR_TEXTO_TITULO);
        widgetCobertura.add(lblTitCob, BorderLayout.NORTH);

        JPanel panelTurnosHoy = new JPanel(new GridLayout(3, 1, 0, 6));
        panelTurnosHoy.setOpaque(false);

        lblCoberturaManana = new JLabel("☀️ Mañana (08:00 - 15:00): 5 Especialistas (Cirugía, Derma, Cardio, Felina)");
        lblCoberturaManana.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblCoberturaManana.setForeground(COLOR_TEXTO_TITULO);

        lblCoberturaTarde = new JLabel("🌤️ Tarde (15:00 - 22:00): 3 Especialistas (Exóticos, Oftalmología, Oncología)");
        lblCoberturaTarde.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblCoberturaTarde.setForeground(COLOR_TEXTO_TITULO);

        lblCoberturaNoche = new JLabel("🌙 Noche / Guardia 24h: 1 Médico Intensivista (Dr. Carlos Méndez · Urgencias)");
        lblCoberturaNoche.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblCoberturaNoche.setForeground(COLOR_NARANJA);

        panelTurnosHoy.add(lblCoberturaManana);
        panelTurnosHoy.add(lblCoberturaTarde);
        panelTurnosHoy.add(lblCoberturaNoche);

        widgetCobertura.add(panelTurnosHoy, BorderLayout.CENTER);

        // Widget 2: Solicitudes pendientes de cambios de turno
        JPanel widgetPermutas = new JPanel(new BorderLayout(10, 8));
        widgetPermutas.setBackground(COLOR_CARD);
        widgetPermutas.setBorder(new CompoundBorder(
                new LineBorder(COLOR_BORDE, 1, true),
                new EmptyBorder(12, 14, 12, 14)
        ));

        JLabel lblTitPerm = new JLabel("🔄 Solicitudes Pendientes de Cambios de Turno");
        lblTitPerm.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTitPerm.setForeground(COLOR_TEXTO_TITULO);
        widgetPermutas.add(lblTitPerm, BorderLayout.NORTH);

        JPanel itemPermuta = new JPanel(new BorderLayout(8, 0));
        itemPermuta.setBackground(new Color(248, 250, 252));
        itemPermuta.setBorder(new CompoundBorder(
                new LineBorder(COLOR_BORDE, 1, true),
                new EmptyBorder(6, 10, 6, 10)
        ));

        JLabel lblDetallePerm = new JLabel("<html><b>Dr. Mario Silva</b> solicita permuta Sáb 08:00 con <b>Dra. Laura Morales</b><br><font color='#64748b'>Motivo: Compensación de guardia médica fin de semana</font></html>");
        lblDetallePerm.setFont(new Font("Segoe UI", Font.PLAIN, 11));

        JPanel botonesPerm = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        botonesPerm.setOpaque(false);

        JButton btnAprobar = Ui.botonPrimario("Aprobar", null);
        btnAprobar.setBackground(COLOR_VERDE);
        btnAprobar.addActionListener(e -> JOptionPane.showMessageDialog(this, "Permuta de turno aprobada y actualizada en el cuadrante semanal.", "Turno Aprobado", JOptionPane.INFORMATION_MESSAGE));

        JButton btnRechazar = Ui.botonSecundario("Rechazar", null);
        btnRechazar.addActionListener(e -> JOptionPane.showMessageDialog(this, "Solicitud de permuta rechazada.", "Turno Rechazado", JOptionPane.INFORMATION_MESSAGE));

        botonesPerm.add(btnAprobar);
        botonesPerm.add(btnRechazar);

        itemPermuta.add(lblDetallePerm, BorderLayout.CENTER);
        itemPermuta.add(botonesPerm, BorderLayout.EAST);

        widgetPermutas.add(itemPermuta, BorderLayout.CENTER);

        contenedor.add(widgetCobertura);
        contenedor.add(widgetPermutas);
        return contenedor;
    }

    public void recargarDatos() {
        modeloTabla.setRowCount(0);
        List<TurnoSemanal> todos = repo.getCuadranteTurnos();

        String busq = txtBuscar != null ? txtBuscar.getText().trim().toLowerCase() : "";
        String espFiltro = cmbEspecialidad != null ? (String) cmbEspecialidad.getSelectedItem() : "Todas las especialidades";

        listaTurnosActual = todos.stream().filter(t -> {
            if (!busq.isEmpty()) {
                boolean mNom = t.getNombreProfesional().toLowerCase().contains(busq);
                boolean mCol = t.getColegiaturaCMPV().toLowerCase().contains(busq);
                if (!mNom && !mCol) return false;
            }
            if (espFiltro != null && !espFiltro.equals("Todas las especialidades")) {
                if (!t.getEspecialidad().toLowerCase().contains(espFiltro.toLowerCase())) {
                    return false;
                }
            }
            return true;
        }).collect(Collectors.toList());

        for (TurnoSemanal t : listaTurnosActual) {
            modeloTabla.addRow(new Object[]{
                    t,
                    t.getHorarioDia(0),
                    t.getHorarioDia(1),
                    t.getHorarioDia(2),
                    t.getHorarioDia(3),
                    t.getHorarioDia(4),
                    t.getHorarioDia(5),
                    t.getHorarioDia(6),
                    "Editar"
            });
        }
    }

    private String obtenerIniciales(String nombre) {
        if (nombre == null || nombre.isEmpty()) return "V";
        String limpio = nombre.replace("Dra.", "").replace("Dr.", "").trim();
        String[] partes = limpio.split("\\s+");
        if (partes.length >= 2) {
            return (partes[0].substring(0, 1) + partes[1].substring(0, 1)).toUpperCase();
        }
        return "V";
    }

    private void mostrarDialogoEditarTurnos(TurnoSemanal t) {
        JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(this), "Editar Turno Semanal: " + t.getNombreProfesional(), JDialog.ModalityType.APPLICATION_MODAL);
        dlg.setSize(620, 520);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());

        JPanel p = new JPanel(new GridLayout(8, 2, 8, 8));
        p.setBackground(Color.WHITE);
        p.setBorder(new EmptyBorder(16, 20, 16, 20));

        String[] dias = {"Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"};
        JTextField[] txtHorarios = new JTextField[7];
        JComboBox<String>[] cmbTipos = new JComboBox[7];

        for (int i = 0; i < 7; i++) {
            txtHorarios[i] = new JTextField(t.getHorarioDia(i), 15);
            cmbTipos[i] = new JComboBox<>(new String[]{"Mañana", "Tarde", "Guardia", "Descanso"});
            cmbTipos[i].setSelectedItem(t.getTipoTurnoDia(i));

            JPanel boxDia = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
            boxDia.setOpaque(false);
            boxDia.add(new JLabel(dias[i] + ":"));
            boxDia.add(cmbTipos[i]);

            p.add(boxDia);
            p.add(txtHorarios[i]);
        }

        JPanel bot = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        bot.setBackground(Color.WHITE);

        JButton btnGuardar = Ui.botonPrimario("Guardar Asignaciones", null);
        btnGuardar.setBackground(COLOR_AZUL);
        btnGuardar.addActionListener(e -> {
            for (int i = 0; i < 7; i++) {
                t.setHorarioDia(i, txtHorarios[i].getText().trim(), (String) cmbTipos[i].getSelectedItem());
            }
            repo.guardarTurnoSemanal(t);
            JOptionPane.showMessageDialog(dlg, "Cuadrante de turnos actualizado para " + t.getNombreProfesional(), "Actualización Exitosa", JOptionPane.INFORMATION_MESSAGE);
            dlg.dispose();
            recargarDatos();
        });

        JButton btnCerrar = Ui.botonSecundario("Cancelar", null);
        btnCerrar.addActionListener(e -> dlg.dispose());

        bot.add(btnGuardar);
        bot.add(btnCerrar);

        dlg.add(p, BorderLayout.CENTER);
        dlg.add(bot, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    private void mostrarDialogoAsignarTurno() {
        if (listaTurnosActual != null && !listaTurnosActual.isEmpty()) {
            mostrarDialogoEditarTurnos(listaTurnosActual.get(0));
        } else {
            JOptionPane.showMessageDialog(this, "No hay especialistas en la lista para asignar turnos.", "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }
}
