package happypets.modulos.modulo2;

import java.awt.BorderLayout;
import java.awt.Color;
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
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import happypets.data.RepositorioVeterinaria;
import happypets.model.Cita;
import happypets.ui.Iconos;

/**
 * Submódulo 2.2: Calendario Global de Citas y Turnos de la Clínica.
 */
public class VistaCalendarioGlobalPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private static final Color COLOR_BORDE = new Color(226, 232, 240);
    private static final Color COLOR_AZUL_PRIMARIO = new Color(2, 132, 199);
    private static final Color COLOR_TEXTO_TITULO = new Color(30, 41, 59);
    private static final Color COLOR_TEXTO_MUTED = new Color(100, 116, 139);

    private final RepositorioVeterinaria repo = RepositorioVeterinaria.getInstancia();

    private LocalDate fechaSeleccionada = LocalDate.now();
    private YearMonth mesVisualizado = YearMonth.now();

    // Componentes del mini-calendario
    private JLabel lblMesAnio;
    private JPanel gridDiasMes;
    private JLabel lblInfoDiaSeleccionado;
    private JLabel lblTotalCitasDia;

    // Selector de recursos
    private JComboBox<String> cbRecurso;
    private JPanel panelAgendaHorarios;

    private final String[] HORAS_SLOTS = {
            "08:00", "09:00", "10:00", "11:00", "12:00",
            "14:00", "15:00", "16:00", "17:00", "18:00", "19:00"
    };

    private final String[] RECURSOS_COLUMNAS = {
            "Consultorio 1 (Dr. Mendoza)",
            "Consultorio 2 (Dra. Morales)",
            "Quirófano Principal",
            "Área de Grooming"
    };

    public VistaCalendarioGlobalPanel() {
        setLayout(new BorderLayout());
        setOpaque(false);

        JPanel contenido = new JPanel();
        contenido.setOpaque(false);
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setBorder(new EmptyBorder(12, 18, 14, 18));

        // 1. Cabecera
        contenido.add(crearCabeceraVista());
        contenido.add(Box.createVerticalStrut(8));

        // 2. Barra de navegación y filtro
        contenido.add(crearBarraNavegacion());
        contenido.add(Box.createVerticalStrut(10));

        // 3. Contenedor principal: Mini calendario a la izquierda + Agenda visual a la derecha
        contenido.add(crearContenedorCalendario());

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(contenido, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(wrapper);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);

        actualizarMiniCalendario();
        actualizarMatrizHorarios();
    }

    private JPanel crearCabeceraVista() {
        JPanel cab = new JPanel(new BorderLayout());
        cab.setOpaque(false);
        cab.setPreferredSize(new Dimension(0, 40));
        cab.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JPanel izq = new JPanel();
        izq.setOpaque(false);
        izq.setLayout(new BoxLayout(izq, BoxLayout.Y_AXIS));

        JLabel lblTit = new JLabel("Calendario Global de Turnos y Espacios");
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblTit.setForeground(COLOR_TEXTO_TITULO);

        JLabel lblSub = new JLabel("Módulo 2.2 · Monitoreo y programación de consultorios, quirófanos y áreas especializadas");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblSub.setForeground(COLOR_TEXTO_MUTED);

        izq.add(lblTit);
        izq.add(Box.createVerticalStrut(2));
        izq.add(lblSub);
        cab.add(izq, BorderLayout.CENTER);

        JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 2));
        der.setOpaque(false);

        JButton btnDia = crearBotonAccion("Día", true);
        JButton btnSemana = crearBotonAccion("Semana", false);
        JButton btnMes = crearBotonAccion("Mes", false);

        btnDia.addActionListener(e -> {
            btnDia.putClientProperty("primario", true);
            btnSemana.putClientProperty("primario", false);
            btnMes.putClientProperty("primario", false);
            btnDia.repaint(); btnSemana.repaint(); btnMes.repaint();
            actualizarMatrizHorarios();
        });

        btnSemana.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Vista semanal generada. Visualizando turnos programados para la semana actual.",
                "Vista Semanal", JOptionPane.INFORMATION_MESSAGE));

        btnMes.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Vista mensual consolidada. Puede navegar días específicos con el panel lateral.",
                "Vista Mensual", JOptionPane.INFORMATION_MESSAGE));

        der.add(btnDia);
        der.add(btnSemana);
        der.add(btnMes);
        cab.add(der, BorderLayout.EAST);

        return cab;
    }

    private JPanel crearBarraNavegacion() {
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

        // Izquierda: Controles mes < >
        JPanel izq = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
        izq.setOpaque(false);

        JButton btnAnt = crearBotonAccion("< Anterior", false);
        lblMesAnio = new JLabel("", SwingConstants.CENTER);
        lblMesAnio.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblMesAnio.setForeground(COLOR_TEXTO_TITULO);
        lblMesAnio.setPreferredSize(new Dimension(140, 26));

        JButton btnSig = crearBotonAccion("Siguiente >", false);

        btnAnt.addActionListener(e -> {
            mesVisualizado = mesVisualizado.minusMonths(1);
            actualizarMiniCalendario();
        });

        btnSig.addActionListener(e -> {
            mesVisualizado = mesVisualizado.plusMonths(1);
            actualizarMiniCalendario();
        });

        JButton btnHoy = crearBotonAccion("Ir a Hoy", false);
        btnHoy.addActionListener(e -> {
            fechaSeleccionada = LocalDate.now();
            mesVisualizado = YearMonth.now();
            actualizarMiniCalendario();
            actualizarMatrizHorarios();
        });

        izq.add(btnAnt);
        izq.add(lblMesAnio);
        izq.add(btnSig);
        izq.add(btnHoy);
        barra.add(izq, BorderLayout.WEST);

        // Derecha: Selector de recurso
        JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 2));
        der.setOpaque(false);

        JLabel lblRec = new JLabel("Filtrar Espacio:");
        lblRec.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblRec.setForeground(COLOR_TEXTO_MUTED);
        der.add(lblRec);

        cbRecurso = new JComboBox<>(new String[]{
                "Todos los espacios (4 áreas)",
                "Consultorio 1 (Dr. Mendoza)",
                "Consultorio 2 (Dra. Morales)",
                "Quirófano Principal",
                "Área de Grooming"
        });
        cbRecurso.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        cbRecurso.setPreferredSize(new Dimension(190, 26));
        cbRecurso.setBackground(Color.WHITE);
        cbRecurso.addActionListener(e -> actualizarMatrizHorarios());
        der.add(cbRecurso);

        barra.add(der, BorderLayout.EAST);
        return barra;
    }

    private JPanel crearContenedorCalendario() {
        JPanel split = new JPanel(new BorderLayout(12, 0));
        split.setOpaque(false);

        // Columna Izquierda: Tarjeta Mini Calendario + Leyenda + Resumen
        JPanel colIzq = new JPanel();
        colIzq.setOpaque(false);
        colIzq.setLayout(new BoxLayout(colIzq, BoxLayout.Y_AXIS));
        colIzq.setPreferredSize(new Dimension(250, 0));

        colIzq.add(crearTarjetaMiniCalendario());
        colIzq.add(Box.createVerticalStrut(10));
        colIzq.add(crearTarjetaLeyendaEspacios());

        split.add(colIzq, BorderLayout.WEST);

        // Columna Derecha: Tarjeta con la Matriz de Agenda y Slots
        split.add(crearTarjetaMatrizAgenda(), BorderLayout.CENTER);

        return split;
    }

    private JPanel crearTarjetaMiniCalendario() {
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
        card.setBorder(new EmptyBorder(10, 12, 10, 12));

        JLabel lblTit = new JLabel("Selector de Fecha");
        lblTit.setIcon(Iconos.crearIconoCalendario(14, COLOR_AZUL_PRIMARIO));
        lblTit.setIconTextGap(6);
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTit.setForeground(COLOR_TEXTO_TITULO);
        card.add(lblTit, BorderLayout.NORTH);

        JPanel pnlCal = new JPanel(new BorderLayout(0, 4));
        pnlCal.setOpaque(false);

        // Cabecera días semana
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

        // Cuadrícula 7x6 de días
        gridDiasMes = new JPanel(new GridLayout(6, 7, 2, 2));
        gridDiasMes.setOpaque(false);
        pnlCal.add(gridDiasMes, BorderLayout.CENTER);

        card.add(pnlCal, BorderLayout.CENTER);

        // Resumen inferior del día seleccionado
        JPanel bot = new JPanel();
        bot.setOpaque(false);
        bot.setLayout(new BoxLayout(bot, BoxLayout.Y_AXIS));
        bot.setBorder(new EmptyBorder(6, 0, 0, 0));

        lblInfoDiaSeleccionado = new JLabel("Fecha: " + fechaSeleccionada.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        lblInfoDiaSeleccionado.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblInfoDiaSeleccionado.setForeground(COLOR_TEXTO_TITULO);

        lblTotalCitasDia = new JLabel("0 turnos programados");
        lblTotalCitasDia.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblTotalCitasDia.setForeground(COLOR_TEXTO_MUTED);

        bot.add(lblInfoDiaSeleccionado);
        bot.add(Box.createVerticalStrut(2));
        bot.add(lblTotalCitasDia);
        card.add(bot, BorderLayout.SOUTH);

        return card;
    }

    private JPanel crearTarjetaLeyendaEspacios() {
        JPanel card = new JPanel(new BorderLayout(0, 6)) {
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
        card.setBorder(new EmptyBorder(10, 12, 10, 12));

        JLabel lblTit = new JLabel("Leyenda de Espacios");
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTit.setForeground(COLOR_TEXTO_TITULO);
        card.add(lblTit, BorderLayout.NORTH);

        JPanel pnlItems = new JPanel();
        pnlItems.setOpaque(false);
        pnlItems.setLayout(new BoxLayout(pnlItems, BoxLayout.Y_AXIS));

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

    private JPanel crearFilaLeyenda(Color bg, Color text, String etiqueta) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        p.setOpaque(false);

        JLabel dot = new JLabel() {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fillOval(0, 0, 12, 12);
                g2.setColor(text);
                g2.drawOval(0, 0, 11, 11);
                g2.dispose();
            }
        };
        dot.setPreferredSize(new Dimension(12, 12));

        JLabel lbl = new JLabel(etiqueta);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lbl.setForeground(new Color(51, 65, 85));

        p.add(dot);
        p.add(lbl);
        return p;
    }

    private JPanel crearTarjetaMatrizAgenda() {
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

        // Cabecera
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JLabel lblTit = new JLabel("Matriz de Ocupación y Turnos Horarios");
        lblTit.setIcon(Iconos.crearIconoReloj(15, COLOR_AZUL_PRIMARIO));
        lblTit.setIconTextGap(6);
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTit.setForeground(COLOR_TEXTO_TITULO);
        top.add(lblTit, BorderLayout.WEST);

        JLabel lblTip = new JLabel("Haga clic sobre cualquier cita para ver su detalle clínico");
        lblTip.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblTip.setForeground(COLOR_TEXTO_MUTED);
        top.add(lblTip, BorderLayout.EAST);
        card.add(top, BorderLayout.NORTH);

        // Panel de agenda dinámica
        panelAgendaHorarios = new JPanel();
        panelAgendaHorarios.setOpaque(false);
        panelAgendaHorarios.setLayout(new BoxLayout(panelAgendaHorarios, BoxLayout.Y_AXIS));

        JScrollPane sp = new JScrollPane(panelAgendaHorarios);
        sp.setBorder(BorderFactory.createLineBorder(COLOR_BORDE, 1));
        sp.setPreferredSize(new Dimension(600, 360));
        sp.getVerticalScrollBar().setUnitIncrement(16);
        card.add(sp, BorderLayout.CENTER);

        return card;
    }

    private void actualizarMiniCalendario() {
        gridDiasMes.removeAll();
        lblMesAnio.setText(mesVisualizado.format(DateTimeFormatter.ofPattern("MMMM yyyy")));

        LocalDate primeroMes = mesVisualizado.atDay(1);
        int diaSemanaPrimero = primeroMes.getDayOfWeek().getValue(); // 1 = Lunes, 7 = Domingo
        int diasEnMes = mesVisualizado.lengthOfMonth();

        // Celdas vacías antes del primer día
        for (int i = 1; i < diaSemanaPrimero; i++) {
            gridDiasMes.add(new JLabel(""));
        }

        List<Cita> citasDelRepo = repo.getCitas();

        for (int d = 1; d <= diasEnMes; d++) {
            LocalDate f = mesVisualizado.atDay(d);
            boolean esSeleccionado = f.equals(fechaSeleccionada);
            boolean esHoy = f.equals(LocalDate.now());
            long citasEnEsteDia = citasDelRepo.stream().filter(c -> c.getFecha().equals(f)).count();

            JLabel lblDia = new JLabel(String.valueOf(d), SwingConstants.CENTER) {
                private static final long serialVersionUID = 1L;
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    if (esSeleccionado) {
                        g2.setColor(COLOR_AZUL_PRIMARIO);
                        g2.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 6, 6);
                    } else if (esHoy) {
                        g2.setColor(new Color(224, 242, 254));
                        g2.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 6, 6);
                    } else if (citasEnEsteDia > 0) {
                        g2.setColor(new Color(241, 245, 249));
                        g2.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 6, 6);
                    }
                    super.paintComponent(g);
                    if (citasEnEsteDia > 0 && !esSeleccionado) {
                        g2.setColor(new Color(2, 132, 199));
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
                    actualizarMiniCalendario();
                    actualizarMatrizHorarios();
                }
            });

            gridDiasMes.add(lblDia);
        }

        // Rellenar resto de celdas
        int totalCeldas = (diaSemanaPrimero - 1) + diasEnMes;
        int restantes = (7 - (totalCeldas % 7)) % 7;
        for (int i = 0; i < restantes; i++) {
            gridDiasMes.add(new JLabel(""));
        }

        gridDiasMes.revalidate();
        gridDiasMes.repaint();

        // Actualizar etiqueta del día
        lblInfoDiaSeleccionado.setText("Fecha: " + fechaSeleccionada.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        long citasDia = citasDelRepo.stream().filter(c -> c.getFecha().equals(fechaSeleccionada)).count();
        lblTotalCitasDia.setText(citasDia + " turnos programados");
    }

    private void actualizarMatrizHorarios() {
        panelAgendaHorarios.removeAll();

        List<Cita> citasDia = repo.getCitasPorFecha(fechaSeleccionada);
        String filtroRecurso = cbRecurso != null ? (String) cbRecurso.getSelectedItem() : "Todos";

        // Determinar qué columnas mostrar
        List<String> columnasAMostrar = new ArrayList<>();
        if (filtroRecurso == null || filtroRecurso.startsWith("Todos")) {
            for (String col : RECURSOS_COLUMNAS) columnasAMostrar.add(col);
        } else {
            columnasAMostrar.add(filtroRecurso);
        }

        // 1. Fila Cabecera con nombres de consultorios
        JPanel filaCab = new JPanel(new GridLayout(1, columnasAMostrar.size() + 1, 4, 0));
        filaCab.setBackground(new Color(241, 245, 249));
        filaCab.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        filaCab.setBorder(new EmptyBorder(4, 8, 4, 8));

        JLabel lblH = new JLabel("Hora", SwingConstants.CENTER);
        lblH.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblH.setForeground(COLOR_TEXTO_MUTED);
        filaCab.add(lblH);

        for (String col : columnasAMostrar) {
            JLabel lblC = new JLabel(col, SwingConstants.CENTER);
            lblC.setFont(new Font("Segoe UI", Font.BOLD, 10));
            lblC.setForeground(COLOR_TEXTO_TITULO);
            filaCab.add(lblC);
        }
        panelAgendaHorarios.add(filaCab);
        panelAgendaHorarios.add(Box.createVerticalStrut(2));

        // 2. Filas de horarios (Slots)
        for (String slotHora : HORAS_SLOTS) {
            JPanel filaSlot = new JPanel(new GridLayout(1, columnasAMostrar.size() + 1, 4, 0));
            filaSlot.setOpaque(false);
            filaSlot.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
            filaSlot.setBorder(new EmptyBorder(2, 6, 2, 6));

            JLabel lblHora = new JLabel(slotHora, SwingConstants.CENTER);
            lblHora.setFont(new Font("Segoe UI", Font.BOLD, 10));
            lblHora.setForeground(new Color(71, 85, 105));
            filaSlot.add(lblHora);

            for (String rec : columnasAMostrar) {
                Cita citaEnSlot = buscarCitaEnSlot(citasDia, slotHora, rec);
                filaSlot.add(crearCeldaSlot(slotHora, rec, citaEnSlot));
            }

            panelAgendaHorarios.add(filaSlot);
            panelAgendaHorarios.add(Box.createVerticalStrut(2));
        }

        panelAgendaHorarios.revalidate();
        panelAgendaHorarios.repaint();
    }

    private Cita buscarCitaEnSlot(List<Cita> citas, String slotHora, String recurso) {
        int horaSlot = Integer.parseInt(slotHora.split(":")[0]);
        for (Cita c : citas) {
            if (c.getHora().getHour() == horaSlot) {
                if (recurso.contains("Dr. Mendoza") && c.getVeterinario().contains("Mendoza")) return c;
                if (recurso.contains("Dra. Morales") && c.getVeterinario().contains("Morales")) return c;
                if (recurso.contains("Quirófano") && c.getTipoServicio().contains("Cirugía")) return c;
                if (recurso.contains("Grooming") && c.getTipoServicio().contains("Grooming")) return c;
            }
        }
        return null;
    }

    private JPanel crearCeldaSlot(String hora, String recurso, Cita cita) {
        if (cita == null) {
            // Slot Libre
            JPanel libre = new JPanel(new BorderLayout()) {
                private static final long serialVersionUID = 1L;
                @Override
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

            JLabel lblDisp = new JLabel("Disponible", SwingConstants.CENTER);
            lblDisp.setFont(new Font("Segoe UI", Font.PLAIN, 9));
            lblDisp.setForeground(new Color(148, 163, 184));
            libre.add(lblDisp, BorderLayout.CENTER);

            libre.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    JOptionPane.showMessageDialog(VistaCalendarioGlobalPanel.this,
                            "Turno disponible a las " + hora + " en " + recurso + " para el " + fechaSeleccionada.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + ".\nPuede reservar este turno desde la pestaña 'Agendamiento de Citas'.",
                            "Turno Disponible", JOptionPane.INFORMATION_MESSAGE);
                }
            });
            return libre;
        }

        // Slot Ocupado
        Color bgCard;
        Color borderCard;
        if (cita.getTipoServicio().contains("Cirugía")) {
            bgCard = new Color(243, 232, 255);
            borderCard = new Color(147, 51, 234);
        } else if (cita.getTipoServicio().contains("Grooming")) {
            bgCard = new Color(254, 243, 199);
            borderCard = new Color(217, 119, 6);
        } else if (cita.getTipoServicio().contains("Vacuna")) {
            bgCard = new Color(220, 252, 231);
            borderCard = new Color(22, 163, 74);
        } else {
            bgCard = new Color(224, 242, 254);
            borderCard = new Color(2, 132, 199);
        }

        JPanel ocupado = new JPanel(new BorderLayout(2, 0)) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bgCard);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g2.setColor(borderCard);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 6, 6);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        ocupado.setOpaque(false);
        ocupado.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        ocupado.setBorder(new EmptyBorder(3, 6, 3, 6));

        JPanel pInfo = new JPanel();
        pInfo.setOpaque(false);
        pInfo.setLayout(new BoxLayout(pInfo, BoxLayout.Y_AXIS));

        JLabel lblNom = new JLabel(cita.getNombreMascota() + " · " + cita.getTipoServicio());
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
                String info = "CITA PROGRAMADA: " + cita.getIdCita() + "\n\n" +
                              "Paciente: " + cita.getNombreMascota() + " (" + cita.getEspecieRaza() + ")\n" +
                              "Propietario: " + cita.getNombreCliente() + " | Tel: " + cita.getTelefonoCliente() + "\n" +
                              "Fecha y Hora: " + cita.getFechaFormateada() + " " + cita.getHoraFormateada() + " hrs\n" +
                              "Espacio Asignado: " + recurso + "\n" +
                              "Veterinario: " + cita.getVeterinario() + "\n" +
                              "Servicio: " + cita.getTipoServicio() + "\n" +
                              "Estado: " + cita.getEstado() + "\n" +
                              "Motivo: " + cita.getMotivo();

                JOptionPane.showMessageDialog(VistaCalendarioGlobalPanel.this, info, "Información de Cita", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        return ocupado;
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
                    g2.setColor(getModel().isRollover() ? new Color(3, 105, 161) : COLOR_AZUL_PRIMARIO);
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
}
