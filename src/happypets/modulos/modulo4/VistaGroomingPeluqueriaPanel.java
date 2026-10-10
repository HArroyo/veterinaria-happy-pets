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
import java.time.LocalTime;
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
import happypets.model.ServicioGrooming;
import happypets.ui.Iconos;
import happypets.ui.Ui;

/**
 * Submódulo 4.1: Grooming y Peluquería Canina y Felina.
 * Diseñado según el wireframe oficial (Pág. 3):
 * - Agenda y control de turnos de spa en tiempo real.
 * - Checklist sanitario de recepción (ectoparásitos, revisión dérmica y nudos).
 * - Selección de cosmética y tratamientos dermatológicos.
 * - Notificaciones de retiro al tutor por WhatsApp.
 */
public class VistaGroomingPeluqueriaPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private static final Color COLOR_BORDE = Ui.BORDE_SUAVE;
    private static final Color COLOR_AZUL_PRIMARIO = Ui.TURQUESA;
    private static final Color COLOR_TEXTO_TITULO = Ui.TEXTO_TITULO;
    private static final Color COLOR_TEXTO_MUTED = Ui.TEXTO_MUTED;

    private final RepositorioVeterinaria repo = RepositorioVeterinaria.getInstancia();

    // KPIs
    private JLabel lblKpiTurnosHoy;
    private JLabel lblKpiEnProceso;
    private JLabel lblKpiListosEntrega;
    private JLabel lblKpiFacturacion;

    // Formulario de Check-in de Spa
    private JComboBox<PacienteItem> cbPaciente;
    private JTextField txtTutor;
    private JComboBox<String> cbGroomer;
    private JComboBox<String> cbTipoServicio;
    private JCheckBox chkPulgas;
    private JCheckBox chkHeridas;
    private JCheckBox chkNudos;
    private JCheckBox chkLimpiezaOidos;
    private JCheckBox chkCorteUnas;
    private JCheckBox chkVaciadoGlandulas;
    private JTextField txtCosmetica;
    private JTextArea txtObservaciones;
    private JTextField txtHoraTurno;
    private JTextField txtCosto;

    // Tabla de Turnos
    private JTable tablaGrooming;
    private DefaultTableModel modeloGrooming;
    private JLabel lblContadorTurnos;
    private List<ServicioGrooming> listaActual;

    public VistaGroomingPeluqueriaPanel() {
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

        // 3. Doble Columna: Checklist / Formulario y Monitor de Turnos
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

        JLabel titulo = new JLabel("Grooming y Peluquería Canina / Felina");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 17));
        titulo.setForeground(COLOR_TEXTO_TITULO);

        JLabel subtitulo = new JLabel("Módulo 4.1 · Spa estético, baños medicados, corte de raza, checklist dermatológico y entrega");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitulo.setForeground(COLOR_TEXTO_MUTED);

        izq.add(titulo);
        izq.add(Box.createVerticalStrut(2));
        izq.add(subtitulo);
        cab.add(izq, BorderLayout.CENTER);

        JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        der.setOpaque(false);

        JLabel badgeSala = new JLabel(" Sala de Estética: 3 Mesas Activas ", SwingConstants.CENTER);
        badgeSala.setFont(new Font("Segoe UI", Font.BOLD, 11));
        badgeSala.setForeground(new Color(230, 80, 130));
        badgeSala.setOpaque(true);
        badgeSala.setBackground(new Color(253, 242, 248));
        badgeSala.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(251, 207, 232), 1, true),
                new EmptyBorder(4, 10, 4, 10)
        ));
        der.add(badgeSala);

        JButton btnRefrescar = crearBotonWeb("↻ Actualizar", false, () -> recargarDatos());
        der.add(btnRefrescar);

        cab.add(der, BorderLayout.EAST);
        return cab;
    }

    private JPanel crearFilaKpis() {
        JPanel fila = new JPanel(new GridLayout(1, 4, 12, 0));
        fila.setOpaque(false);

        lblKpiTurnosHoy = new JLabel("3", SwingConstants.LEFT);
        lblKpiEnProceso = new JLabel("2", SwingConstants.LEFT);
        lblKpiListosEntrega = new JLabel("1", SwingConstants.LEFT);
        lblKpiFacturacion = new JLabel("S/. 215", SwingConstants.LEFT);

        fila.add(crearCardKpi(lblKpiTurnosHoy, "TURNOS PROGRAMADOS", Ui.TURQUESA_SUAVE, COLOR_AZUL_PRIMARIO, Iconos.crearIconoTijeras(20, COLOR_AZUL_PRIMARIO)));
        fila.add(crearCardKpi(lblKpiEnProceso, "EN BAÑO / SECADO", new Color(254, 243, 199), new Color(217, 119, 6), Iconos.crearIconoReloj(20, new Color(217, 119, 6))));
        fila.add(crearCardKpi(lblKpiListosEntrega, "LISTOS PARA RETIRO", new Color(220, 252, 231), new Color(22, 163, 74), Iconos.crearIconoCheck(20, new Color(22, 163, 74))));
        fila.add(crearCardKpi(lblKpiFacturacion, "FACTURACIÓN ESTIMADA", new Color(243, 232, 255), new Color(147, 51, 234), Iconos.crearIconoFactura(20, new Color(147, 51, 234))));

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

    private JPanel crearDobleColumna() {
        JPanel panel = new JPanel(new BorderLayout(14, 0));
        panel.setOpaque(false);

        panel.add(crearCardFormularioCheckin(), BorderLayout.WEST);
        panel.add(crearCardTablaAgenda(), BorderLayout.CENTER);

        return panel;
    }

    private JPanel crearCardFormularioCheckin() {
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
        card.setPreferredSize(new Dimension(510, 0));
        card.setBorder(new EmptyBorder(12, 14, 12, 14));

        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));

        JLabel tit = new JLabel("1) Ficha de Admisión y Checklist de Entrada");
        tit.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tit.setForeground(COLOR_TEXTO_TITULO);
        tit.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(tit);
        form.add(Box.createVerticalStrut(8));

        cbPaciente = new JComboBox<>();
        cbPaciente.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        cbPaciente.setBackground(Color.WHITE);
        cbPaciente.setPreferredSize(new Dimension(0, 26));
        cbPaciente.addActionListener(e -> autocompletarTutor());
        form.add(crearFilaCampo("Paciente (Nombre / Código) *", cbPaciente));
        form.add(Box.createVerticalStrut(4));

        txtTutor = crearCampoTexto(false);
        form.add(crearFilaCampo("Propietario / Teléfono Contacto", txtTutor));
        form.add(Box.createVerticalStrut(4));

        JPanel filaEst = new JPanel(new GridLayout(1, 2, 8, 0));
        filaEst.setOpaque(false);
        filaEst.setAlignmentX(Component.LEFT_ALIGNMENT);
        filaEst.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        String[] groomers = {"Ana Martínez (Groomer)", "Carlos Mendoza (Estilista Canino)", "Luis Peña (Groomer)"};
        cbGroomer = new JComboBox<>(groomers);
        cbGroomer.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        cbGroomer.setBackground(Color.WHITE);

        String[] servs = {
                "Spa Completo (Baño + Corte + Uñas + Glándulas)",
                "Baño Medicado Dermatológico (Clorhexidina)",
                "Corte de Raza Estándar y Acabado a Tijera",
                "Corte Higiénico y Deslanado Profundo",
                "Baño Cosmético e Hidratación de Manto"
        };
        cbTipoServicio = new JComboBox<>(servs);
        cbTipoServicio.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        cbTipoServicio.setBackground(Color.WHITE);

        filaEst.add(crearFilaCampo("Groomer Asignado *", cbGroomer));
        filaEst.add(crearFilaCampo("Servicio de Spa *", cbTipoServicio));
        form.add(filaEst);
        form.add(Box.createVerticalStrut(6));

        // Checklist de Entrada (Wireframe Página 3)
        JLabel lblCheck = new JLabel("Checklist Sanitario de Admisión:", SwingConstants.LEFT);
        lblCheck.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblCheck.setForeground(COLOR_TEXTO_TITULO);
        lblCheck.setHorizontalAlignment(SwingConstants.LEFT);
        lblCheck.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(lblCheck);
        form.add(Box.createVerticalStrut(3));

        JPanel panelChecks = new JPanel(new GridLayout(2, 3, 4, 4));
        panelChecks.setOpaque(false);
        panelChecks.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelChecks.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        chkPulgas = new JCheckBox("Control ectoparásitos");
        chkPulgas.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        chkPulgas.setOpaque(false);
        chkHeridas = new JCheckBox("Revisión dérmica sana");
        chkHeridas.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        chkHeridas.setSelected(true);
        chkHeridas.setOpaque(false);
        chkNudos = new JCheckBox("Manto libre de nudos");
        chkNudos.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        chkNudos.setSelected(true);
        chkNudos.setOpaque(false);

        chkLimpiezaOidos = new JCheckBox("Limpieza de oídos");
        chkLimpiezaOidos.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        chkLimpiezaOidos.setSelected(true);
        chkLimpiezaOidos.setOpaque(false);
        chkCorteUnas = new JCheckBox("Corte y limado uñas");
        chkCorteUnas.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        chkCorteUnas.setSelected(true);
        chkCorteUnas.setOpaque(false);
        chkVaciadoGlandulas = new JCheckBox("Vaciado glándulas");
        chkVaciadoGlandulas.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        chkVaciadoGlandulas.setOpaque(false);

        panelChecks.add(chkPulgas);
        panelChecks.add(chkHeridas);
        panelChecks.add(chkNudos);
        panelChecks.add(chkLimpiezaOidos);
        panelChecks.add(chkCorteUnas);
        panelChecks.add(chkVaciadoGlandulas);
        form.add(panelChecks);
        form.add(Box.createVerticalStrut(4));

        txtCosmetica = crearCampoTexto(true);
        txtCosmetica.setText("Champú Avena Suave, Acondicionador Desenredante, Colonia Baby");
        form.add(crearFilaCampo("Cosmética y Champús Especiales", txtCosmetica));
        form.add(Box.createVerticalStrut(4));

        txtObservaciones = new JTextArea(2, 20);
        txtObservaciones.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        txtObservaciones.setLineWrap(true);
        txtObservaciones.setWrapStyleWord(true);
        txtObservaciones.setText("Paciente tranquilo y sociable. Solicita acabado redondeado en cara y patas.");
        txtObservaciones.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDE, 1),
                new EmptyBorder(3, 6, 3, 6)
        ));
        form.add(crearFilaArea("Observaciones del Groomer / Conducta", txtObservaciones, 44));
        form.add(Box.createVerticalStrut(4));

        JPanel filaCierre = new JPanel(new GridLayout(1, 2, 8, 0));
        filaCierre.setOpaque(false);
        filaCierre.setAlignmentX(Component.LEFT_ALIGNMENT);
        filaCierre.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        txtHoraTurno = crearCampoTexto(true);
        txtHoraTurno.setText("11:30");
        txtCosto = crearCampoTexto(true);
        txtCosto.setText("70.00");
        filaCierre.add(crearFilaCampo("Hora del Turno", txtHoraTurno));
        filaCierre.add(crearFilaCampo("Precio Total (S/.)", txtCosto));
        form.add(filaCierre);
        form.add(Box.createVerticalStrut(10));

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        btnRow.setOpaque(false);
        btnRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        JButton btnRegistrar = crearBotonWeb("✂ Registrar Turno de Spa", true, () -> agendarNuevoTurno());
        btnRow.add(btnRegistrar);
        form.add(btnRow);

        card.add(form, BorderLayout.NORTH);
        return card;
    }

    private JPanel crearCardTablaAgenda() {
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

        JLabel tit = new JLabel("2) Monitor y Agenda de Servicios de Estética (Hoy)");
        tit.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tit.setForeground(COLOR_TEXTO_TITULO);
        top.add(tit, BorderLayout.WEST);

        lblContadorTurnos = new JLabel("Cargando turnos...");
        lblContadorTurnos.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblContadorTurnos.setForeground(COLOR_TEXTO_MUTED);
        top.add(lblContadorTurnos, BorderLayout.EAST);

        card.add(top, BorderLayout.NORTH);

        String[] columnas = {"ID", "Hora", "Paciente", "Tutor", "Servicio", "Estilista", "Estado"};
        modeloGrooming = new DefaultTableModel(columnas, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        tablaGrooming = new JTable(modeloGrooming);
        Ui.formatearTabla(tablaGrooming, new int[]{0, 1, 6}, new int[]{});

        tablaGrooming.getColumnModel().getColumn(0).setPreferredWidth(70);
        tablaGrooming.getColumnModel().getColumn(1).setPreferredWidth(60);
        tablaGrooming.getColumnModel().getColumn(2).setPreferredWidth(85);
        tablaGrooming.getColumnModel().getColumn(4).setPreferredWidth(170);
        tablaGrooming.getColumnModel().getColumn(6).setCellRenderer(new BadgeEstadoGroomingRenderer());

        JScrollPane sp = new JScrollPane(tablaGrooming);
        sp.setBorder(BorderFactory.createLineBorder(COLOR_BORDE, 1));
        sp.setPreferredSize(new Dimension(650, 360));
        card.add(sp, BorderLayout.CENTER);

        JPanel bot = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        bot.setOpaque(false);

        JButton btnAvanzar = crearBotonWeb("▶ Avanzar Etapa", false, () -> avanzarEtapaServicio());
        JButton btnNotificar = crearBotonWeb("📲 Notificar Listo (WhatsApp)", false, () -> notificarListoWhatsApp());
        JButton btnTicket = crearBotonWeb("🧾 Ticket de Retiro", false, () -> emitirTicketRetiro());

        bot.add(btnAvanzar);
        bot.add(btnNotificar);
        bot.add(btnTicket);

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

    private JPanel crearFilaArea(String etiqueta, JTextArea area, int altura) {
        JPanel p = new JPanel(new BorderLayout(0, 2));
        p.setOpaque(false);
        p.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lbl = new JLabel(etiqueta, SwingConstants.LEFT);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lbl.setForeground(new Color(71, 85, 105));
        lbl.setHorizontalAlignment(SwingConstants.LEFT);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        p.add(lbl, BorderLayout.NORTH);

        JScrollPane sp = new JScrollPane(area);
        sp.setPreferredSize(new Dimension(0, altura));
        sp.setBorder(null);
        p.add(sp, BorderLayout.CENTER);
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
                    g2.setColor(getModel().isRollover() ? Ui.TURQUESA_OSCURO : COLOR_AZUL_PRIMARIO);
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
        if (cbPaciente.getItemCount() == 0) {
            for (Mascota m : repo.todasLasMascotas()) {
                cbPaciente.addItem(new PacienteItem(m));
            }
            autocompletarTutor();
        }

        listaActual = repo.getServiciosGrooming();
        modeloGrooming.setRowCount(0);

        double totalFact = 0;
        for (ServicioGrooming g : listaActual) {
            totalFact += g.getCosto();
            modeloGrooming.addRow(new Object[]{
                    g.getIdGrooming(),
                    g.getHoraTurnoFormateada(),
                    g.getNombreMascota(),
                    g.getNombreTutor(),
                    g.getTipoServicio(),
                    g.getGroomer(),
                    g.getEstado()
            });
        }

        lblContadorTurnos.setText(listaActual.size() + " servicios agendados hoy");

        // KPIs
        long proc = listaActual.stream().filter(g -> "En Baño".equalsIgnoreCase(g.getEstado()) || "En Corte y Secado".equalsIgnoreCase(g.getEstado())).count();
        long listos = listaActual.stream().filter(g -> "Listo para Entrega".equalsIgnoreCase(g.getEstado())).count();

        lblKpiTurnosHoy.setText(String.valueOf(listaActual.size()));
        lblKpiEnProceso.setText(String.valueOf(proc));
        lblKpiListosEntrega.setText(String.valueOf(listos));
        lblKpiFacturacion.setText("S/. " + String.format("%.0f", totalFact));
    }

    private void autocompletarTutor() {
        PacienteItem item = (PacienteItem) cbPaciente.getSelectedItem();
        if (item != null && item.mascota != null) {
            repo.getClienteDeMascota(item.mascota.getCodigo()).ifPresent(c -> {
                txtTutor.setText(c.getNombreCompleto() + " (" + c.getTelefonoPrincipal() + ")");
            });
        }
    }

    private void agendarNuevoTurno() {
        PacienteItem pi = (PacienteItem) cbPaciente.getSelectedItem();
        if (pi == null || pi.mascota == null) return;
        Mascota m = pi.mascota;
        Optional<Cliente> optC = repo.getClienteDeMascota(m.getCodigo());

        LocalTime hora = LocalTime.now();
        try {
            String[] p = txtHoraTurno.getText().trim().split(":");
            hora = LocalTime.of(Integer.parseInt(p[0].trim()), Integer.parseInt(p[1].trim()));
        } catch (Exception ignored) {}

        double costo = 70.0;
        try { costo = Double.parseDouble(txtCosto.getText().trim()); } catch (Exception ignored) {}

        String ecto = chkPulgas.isSelected() ? "Presencia leve - Requiere champú antiparasitario" : "Libre de ectoparásitos";
        String piel = chkHeridas.isSelected() ? "Piel sana sin lesiones" : "Sensibilidad dérmica observada";

        ServicioGrooming g = new ServicioGrooming(
                null,
                m.getCodigo(),
                m.getNombre(),
                m.getEspecie() + " · " + m.getRaza(),
                optC.map(Cliente::getNombreCompleto).orElse("Tutor"),
                optC.map(Cliente::getTelefonoPrincipal).orElse(""),
                LocalDate.now(),
                hora,
                (String) cbGroomer.getSelectedItem(),
                (String) cbTipoServicio.getSelectedItem(),
                ecto,
                piel,
                txtCosmetica.getText().trim(),
                txtObservaciones.getText().trim(),
                costo,
                "En Espera"
        );

        repo.guardarServicioGrooming(g);
        JOptionPane.showMessageDialog(this,
                "Turno " + g.getIdGrooming() + " registrado con éxito para " + m.getNombre() + " (" + g.getTipoServicio() + ").",
                "Grooming Agendado", JOptionPane.INFORMATION_MESSAGE);

        recargarDatos();
    }

    private void avanzarEtapaServicio() {
        int row = tablaGrooming.getSelectedRow();
        if (row < 0 || row >= listaActual.size()) {
            JOptionPane.showMessageDialog(this, "Seleccione un servicio de la tabla para avanzar su etapa.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        ServicioGrooming g = listaActual.get(row);
        String act = g.getEstado();
        String nuevo = "En Espera";

        if ("En Espera".equalsIgnoreCase(act)) nuevo = "En Baño";
        else if ("En Baño".equalsIgnoreCase(act)) nuevo = "En Corte y Secado";
        else if ("En Corte y Secado".equalsIgnoreCase(act)) nuevo = "Listo para Entrega";
        else if ("Listo para Entrega".equalsIgnoreCase(act)) nuevo = "Entregado";
        else nuevo = "Entregado";

        g.setEstado(nuevo);
        recargarDatos();

        JOptionPane.showMessageDialog(this,
                "El servicio de " + g.getNombreMascota() + " avanzó a: " + nuevo,
                "Estado de Spa Actualizado", JOptionPane.INFORMATION_MESSAGE);
    }

    private void notificarListoWhatsApp() {
        int row = tablaGrooming.getSelectedRow();
        if (row < 0 || row >= listaActual.size()) {
            JOptionPane.showMessageDialog(this, "Seleccione un servicio para notificar al tutor.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        ServicioGrooming g = listaActual.get(row);

        String msg = "¡Hola " + g.getNombreTutor() + "! Le informamos que " + g.getNombreMascota() +
                     " ya finalizó su sesión de " + g.getTipoServicio() + " y ha quedado reluciente. Puede pasar a retirarlo por Happy Pets cuando guste.";

        JOptionPane.showMessageDialog(this,
                "Mensaje de WhatsApp enviado a " + g.getNombreTutor() + " (" + g.getTelefonoTutor() + "):\n\n\"" + msg + "\"",
                "Notificación Enviada al Tutor", JOptionPane.INFORMATION_MESSAGE);
    }

    private void emitirTicketRetiro() {
        int row = tablaGrooming.getSelectedRow();
        if (row < 0 || row >= listaActual.size()) {
            JOptionPane.showMessageDialog(this, "Seleccione un servicio para emitir ticket.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        ServicioGrooming g = listaActual.get(row);

        String[][] datos = new String[][]{
            {"1", "Servicio Principal: " + g.getTipoServicio(), "Estilista: " + g.getGroomer(), g.getHoraTurnoFormateada() + " hrs", String.format("S/ %.2f", g.getCosto())},
            {"2", "Control Sanitario y Cosmética", g.getControlEctoparasitos() + " | " + g.getEstadoPiel(), g.getProductosUtilizados(), "-"}
        };

        Ui.mostrarVisorReporte(
            (java.awt.Frame) javax.swing.SwingUtilities.getWindowAncestor(this),
            "COMPROBANTE DE SPA & GROOMING VETERINARIO",
            "Paciente: " + g.getNombreMascota() + " (" + g.getEspecieRaza() + ") | Tutor: " + g.getNombreTutor() + " | Tel: " + g.getTelefonoTutor(),
            "Código Turno: " + g.getIdGrooming() + " | Estilista: " + g.getGroomer() + " | Estado: " + g.getEstado(),
            new String[]{"N°", "Servicio / Tratamiento", "Detalle Sanitario", "Horario / Cosmética", "Importe"},
            datos,
            String.format("TOTAL: S/ %.2f · SERVICIO DE BIENESTAR Y BELLEZA", g.getCosto()),
            "TicketGrooming_" + g.getIdGrooming()
        );
    }

    private static class PacienteItem {
        final Mascota mascota;
        PacienteItem(Mascota mascota) { this.mascota = mascota; }
        @Override
        public String toString() {
            return mascota != null ? mascota.getCodigo() + " - " + mascota.getNombre() + " (" + mascota.getEspecie() + " · " + mascota.getRaza() + ")" : "-";
        }
    }

    private static class BadgeEstadoGroomingRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            lbl.setHorizontalAlignment(SwingConstants.CENTER);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
            String st = String.valueOf(value);
            if (!isSelected) {
                if ("Listo para Entrega".equalsIgnoreCase(st)) {
                    lbl.setForeground(new Color(22, 163, 74));
                } else if ("En Corte y Secado".equalsIgnoreCase(st)) {
                    lbl.setForeground(new Color(147, 51, 234));
                } else if ("En Baño".equalsIgnoreCase(st)) {
                    lbl.setForeground(new Color(2, 132, 199));
                } else if ("En Espera".equalsIgnoreCase(st)) {
                    lbl.setForeground(new Color(217, 119, 6));
                } else if ("Entregado".equalsIgnoreCase(st)) {
                    lbl.setForeground(new Color(100, 116, 139));
                }
            }
            return lbl;
        }
    }
}
