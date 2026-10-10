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
import java.time.LocalDateTime;
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
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import happypets.data.RepositorioVeterinaria;
import happypets.model.Cita;
import happypets.model.RecordatorioCita;
import happypets.ui.Iconos;
import happypets.ui.Ui;

/**
 * Submódulo 2.3: Gestión de Recordatorios de Citas y Tratamientos (WhatsApp, SMS, Email).
 */
public class VistaRecordatoriosPanel extends happypets.ui.AssetsModulo {
    private static final long serialVersionUID = 1L;

    private static final Color COLOR_BORDE = new Color(226, 232, 240);
    private static final Color COLOR_AZUL_PRIMARIO = new Color(2, 132, 199);
    private static final Color COLOR_TEXTO_TITULO = new Color(30, 41, 59);
    private static final Color COLOR_TEXTO_MUTED = new Color(100, 116, 139);

    private final RepositorioVeterinaria repo = RepositorioVeterinaria.getInstancia();

    // Filtros
    private JComboBox<String> cbFiltroCanal;
    private JComboBox<String> cbFiltroEstado;
    private JTextField txtBuscar;

    // KPIs
    private JLabel lblKpiTotalEnviados;
    private JLabel lblKpiConfirmados;
    private JLabel lblKpiTasaRespuesta;
    private JLabel lblKpiPendientes;

    // Redactor
    private JComboBox<CitaItem> cbCitaVinculada;
    private JTextField txtContactoDestino;
    private JRadioButton rbWhatsApp;
    private JRadioButton rbSms;
    private JRadioButton rbEmail;
    private JComboBox<String> cbPlantilla;
    private JTextArea txtMensaje;

    // Tabla
    private JTable tablaRecordatorios;
    private DefaultTableModel modeloRecordatorios;
    private JLabel lblContador;
    private List<RecordatorioCita> listaActual;

    public VistaRecordatoriosPanel() {
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

        // 4. Doble columna: Redactor a la izquierda, Historial a la derecha
        contenido.add(crearDobleColumna());

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(contenido, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(wrapper);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
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

        JLabel lblTit = new JLabel("Gestión y Envío de Recordatorios");
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblTit.setForeground(COLOR_TEXTO_TITULO);

        JLabel lblSub = new JLabel("Módulo 2.3 · Notificaciones automatizadas y confirmación de asistencia vía WhatsApp, SMS y Email");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblSub.setForeground(COLOR_TEXTO_MUTED);

        izq.add(lblTit);
        izq.add(Box.createVerticalStrut(2));
        izq.add(lblSub);
        cab.add(izq, BorderLayout.CENTER);

        JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 2));
        der.setOpaque(false);

        JButton btnMasivo = crearBotonAccion("Enviar Notificaciones Masivas", false);
        btnMasivo.setIcon(Iconos.crearIconoWhatsApp(12, new Color(22, 163, 74)));
        btnMasivo.setIconTextGap(4);
        btnMasivo.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                    "Se enviaron 5 recordatorios pendientes a los clientes con citas programadas para las próximas 24 horas.",
                    "Envío Masivo Completado", JOptionPane.INFORMATION_MESSAGE);
            recargarDatos();
        });

        der.add(btnMasivo);
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

        JLabel lblCanal = new JLabel("Canal:");
        lblCanal.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblCanal.setForeground(COLOR_TEXTO_MUTED);
        izq.add(lblCanal);

        cbFiltroCanal = new JComboBox<>(new String[]{"Todos los canales", "WhatsApp", "SMS", "E-Mail"});
        cbFiltroCanal.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        cbFiltroCanal.setPreferredSize(new Dimension(130, 26));
        cbFiltroCanal.setBackground(Color.WHITE);
        cbFiltroCanal.addActionListener(e -> recargarDatos());
        izq.add(cbFiltroCanal);

        JLabel lblEst = new JLabel("Estado:");
        lblEst.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblEst.setForeground(COLOR_TEXTO_MUTED);
        izq.add(lblEst);

        cbFiltroEstado = new JComboBox<>(new String[]{"Todos los estados", "Confirmado", "Enviado", "Pendiente"});
        cbFiltroEstado.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        cbFiltroEstado.setPreferredSize(new Dimension(130, 26));
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
        txtBuscar.setPreferredSize(new Dimension(140, 26));
        txtBuscar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225), 1),
                new EmptyBorder(2, 6, 2, 6)
        ));
        txtBuscar.setToolTipText("Buscar paciente o propietario...");
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

        lblKpiTotalEnviados = new JLabel("0");
        fila.add(crearCardKpi(lblKpiTotalEnviados, "RECORDATORIOS EMITIDOS", new Color(224, 242, 254), Iconos.crearIconoMensaje(20, COLOR_AZUL_PRIMARIO)));

        lblKpiConfirmados = new JLabel("0");
        fila.add(crearCardKpi(lblKpiConfirmados, "CONFIRMADOS POR CLIENTES", new Color(220, 252, 231), Iconos.crearIconoWhatsApp(20, new Color(22, 163, 74))));

        lblKpiTasaRespuesta = new JLabel("0%");
        fila.add(crearCardKpi(lblKpiTasaRespuesta, "TASA DE EFECTIVIDAD", new Color(243, 232, 255), Iconos.crearIconoCheck(20, new Color(147, 51, 234))));

        lblKpiPendientes = new JLabel("0");
        fila.add(crearCardKpi(lblKpiPendientes, "RECORDATORIOS PENDIENTES", new Color(254, 243, 199), Iconos.crearIconoReloj(20, new Color(217, 119, 6))));

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

        grid.add(crearTarjetaRedactor());
        grid.add(crearTarjetaHistorial());

        return grid;
    }

    private JPanel crearTarjetaRedactor() {
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

        JLabel lblTit = new JLabel("Redactor de Notificación y Recordatorio");
        lblTit.setIcon(Iconos.crearIconoWhatsApp(15, new Color(22, 163, 74)));
        lblTit.setIconTextGap(6);
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTit.setForeground(COLOR_TEXTO_TITULO);
        card.add(lblTit, BorderLayout.NORTH);

        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));

        // 1. Selector de cita vinculada
        cbCitaVinculada = new JComboBox<>();
        cbCitaVinculada.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        cbCitaVinculada.setBackground(Color.WHITE);
        cbCitaVinculada.setPreferredSize(new Dimension(0, 26));
        cbCitaVinculada.addActionListener(e -> autocompletarDatosCita());
        form.add(crearFilaCampo("Cita Médica Vinculada *", cbCitaVinculada));
        form.add(Box.createVerticalStrut(6));

        // 2. Destinatario y Canal
        txtContactoDestino = crearCampoTextoCompacto();

        JPanel pCanales = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        pCanales.setOpaque(false);

        rbWhatsApp = new JRadioButton("WhatsApp", true);
        rbWhatsApp.setFont(new Font("Segoe UI", Font.BOLD, 11));
        rbWhatsApp.setForeground(new Color(22, 163, 74));
        rbWhatsApp.setOpaque(false);

        rbSms = new JRadioButton("SMS");
        rbSms.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        rbSms.setForeground(new Color(2, 132, 199));
        rbSms.setOpaque(false);

        rbEmail = new JRadioButton("E-Mail");
        rbEmail.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        rbEmail.setForeground(new Color(147, 51, 234));
        rbEmail.setOpaque(false);

        ButtonGroup bg = new ButtonGroup();
        bg.add(rbWhatsApp);
        bg.add(rbSms);
        bg.add(rbEmail);

        pCanales.add(rbWhatsApp);
        pCanales.add(rbSms);
        pCanales.add(rbEmail);

        JPanel fila2 = new JPanel(new GridLayout(1, 2, 8, 0));
        fila2.setOpaque(false);
        fila2.setAlignmentX(Component.LEFT_ALIGNMENT);
        fila2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        fila2.add(crearFilaCampo("Teléfono / Correo Destino *", txtContactoDestino));
        fila2.add(crearFilaCampo("Canal de Transmisión *", pCanales));
        form.add(fila2);
        form.add(Box.createVerticalStrut(6));

        // 3. Plantilla
        cbPlantilla = new JComboBox<>(new String[]{
                "Recordatorio 24 horas previas",
                "Indicaciones de ayuno prequirúrgico (8 horas)",
                "Recordatorio de vacuna anual",
                "Seguimiento posoperatorio y evolución"
        });
        cbPlantilla.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        cbPlantilla.setBackground(Color.WHITE);
        cbPlantilla.addActionListener(e -> regenerarMensajePlantilla());
        form.add(crearFilaCampo("Plantilla de Mensaje Inteligente", cbPlantilla));
        form.add(Box.createVerticalStrut(6));

        // 4. Área de texto
        txtMensaje = new JTextArea(4, 20);
        txtMensaje.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        txtMensaje.setLineWrap(true);
        txtMensaje.setWrapStyleWord(true);
        txtMensaje.setBorder(new EmptyBorder(4, 6, 4, 6));

        JScrollPane spMsg = new JScrollPane(txtMensaje);
        spMsg.setPreferredSize(new Dimension(0, 80));
        spMsg.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        spMsg.setBorder(BorderFactory.createLineBorder(new Color(203, 213, 225), 1));
        form.add(crearFilaCampo("Contenido del Mensaje a Enviar *", spMsg));

        card.add(form, BorderLayout.CENTER);

        // Botones inferiores
        JPanel bot = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        bot.setOpaque(false);

        JButton btnLimpiar = crearBotonAccion("Restablecer", false);
        btnLimpiar.addActionListener(e -> regenerarMensajePlantilla());

        JButton btnEnviar = crearBotonAccion("Enviar Notificación", true);
        btnEnviar.setIcon(Iconos.crearIconoWhatsApp(12, Color.WHITE));
        btnEnviar.setIconTextGap(4);
        btnEnviar.addActionListener(e -> ejecutarEnvioRecordatorio());

        bot.add(btnLimpiar);
        bot.add(btnEnviar);
        card.add(bot, BorderLayout.SOUTH);

        return card;
    }

    private JPanel crearTarjetaHistorial() {
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

        JLabel lblTit = new JLabel("Historial de Recordatorios Enviados");
        lblTit.setIcon(Iconos.crearIconoHistorial(15, COLOR_AZUL_PRIMARIO));
        lblTit.setIconTextGap(6);
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTit.setForeground(COLOR_TEXTO_TITULO);
        top.add(lblTit, BorderLayout.WEST);

        lblContador = new JLabel("0 registros");
        lblContador.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblContador.setForeground(COLOR_TEXTO_MUTED);
        top.add(lblContador, BorderLayout.EAST);
        card.add(top, BorderLayout.NORTH);

        // Tabla
        String[] columnas = {"Canal", "Cita", "Paciente", "Destinatario", "Fecha y Hora", "Estado"};
        modeloRecordatorios = new DefaultTableModel(columnas, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        tablaRecordatorios = new JTable(modeloRecordatorios);
        Ui.formatearTabla(tablaRecordatorios);
        tablaRecordatorios.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        tablaRecordatorios.setRowHeight(26);

        tablaRecordatorios.getColumnModel().getColumn(5).setCellRenderer(new BadgeEstadoRecRenderer());

        tablaRecordatorios.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    verDetalleMensajeSeleccionado();
                }
            }
        });

        JScrollPane sp = new JScrollPane(tablaRecordatorios);
        sp.setPreferredSize(new Dimension(540, 240));
        sp.setBorder(BorderFactory.createLineBorder(COLOR_BORDE, 1));
        card.add(sp, BorderLayout.CENTER);

        // Acciones inferiores
        JPanel bot = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 4));
        bot.setOpaque(false);

        JButton btnReenviar = crearBotonAccion("Reenviar", false);
        btnReenviar.setIcon(Iconos.crearIconoWhatsApp(12, new Color(22, 163, 74)));
        btnReenviar.setIconTextGap(4);
        btnReenviar.addActionListener(e -> reenviarSeleccionado());

        JButton btnConfirmar = crearBotonAccion("Confirmar Asistencia", true);
        btnConfirmar.addActionListener(e -> marcarConfirmadoSeleccionado());

        bot.add(btnReenviar);
        bot.add(btnConfirmar);
        card.add(bot, BorderLayout.SOUTH);

        return card;
    }

    private JPanel crearFilaCampo(String label, Component componente) {
        JPanel p = new JPanel(new BorderLayout(0, 2));
        p.setOpaque(false);
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        int altura = (componente instanceof JScrollPane) ? 104 : 44;
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, altura));

        JLabel lbl = new JLabel(label, SwingConstants.LEFT);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lbl.setForeground(new Color(100, 116, 139));
        lbl.setHorizontalAlignment(SwingConstants.LEFT);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        p.add(lbl, BorderLayout.NORTH);
        happypets.ui.Ui.ajustarAlturaCampo(componente);
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
        JButton btn = new happypets.ui.BotonAsset(texto) {
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

    public void prepararParaCita(Cita cita) {
        if (cita == null) return;
        recargarDatos();
        for (int i = 0; i < cbCitaVinculada.getItemCount(); i++) {
            CitaItem item = cbCitaVinculada.getItemAt(i);
            if (item != null && item.cita != null && item.cita.getIdCita().equals(cita.getIdCita())) {
                cbCitaVinculada.setSelectedIndex(i);
                autocompletarDatosCita();
                break;
            }
        }
    }

    public void recargarDatos() {
        // Cargar citas en combo si está vacío
        if (cbCitaVinculada.getItemCount() == 0) {
            for (Cita c : repo.getCitas()) {
                cbCitaVinculada.addItem(new CitaItem(c));
            }
            autocompletarDatosCita();
        }

        List<RecordatorioCita> todos = repo.getRecordatorios();
        String fCanal = (String) cbFiltroCanal.getSelectedItem();
        String fEstado = (String) cbFiltroEstado.getSelectedItem();
        String q = txtBuscar.getText().trim().toLowerCase();

        listaActual = todos.stream().filter(r -> {
            if (fCanal != null && !fCanal.startsWith("Todos") && !r.getCanal().equalsIgnoreCase(fCanal)) return false;
            if (fEstado != null && !fEstado.startsWith("Todos") && !r.getEstado().equalsIgnoreCase(fEstado)) return false;
            if (!q.isEmpty()) {
                boolean m = r.getNombreMascota().toLowerCase().contains(q) ||
                            r.getNombreCliente().toLowerCase().contains(q) ||
                            r.getIdCita().toLowerCase().contains(q) ||
                            r.getMensaje().toLowerCase().contains(q);
                if (!m) return false;
            }
            return true;
        }).toList();

        modeloRecordatorios.setRowCount(0);
        for (RecordatorioCita r : listaActual) {
            modeloRecordatorios.addRow(new Object[]{
                    r.getCanal(),
                    r.getIdCita(),
                    r.getNombreMascota(),
                    r.getContactoDestino(),
                    r.getFechaEnvioFormateada(),
                    r.getEstado()
            });
        }
        lblContador.setText(listaActual.size() + " registros");

        // KPIs
        long total = todos.size();
        long confirmados = todos.stream().filter(r -> "Confirmado".equalsIgnoreCase(r.getEstado())).count();
        long pendientes = todos.stream().filter(r -> "Pendiente".equalsIgnoreCase(r.getEstado())).count();

        lblKpiTotalEnviados.setText(String.valueOf(total));
        lblKpiConfirmados.setText(String.valueOf(confirmados));
        lblKpiTasaRespuesta.setText((total > 0 ? (confirmados * 100 / total) : 0) + "%");
        lblKpiPendientes.setText(String.valueOf(pendientes));
    }

    private void autocompletarDatosCita() {
        CitaItem ci = (CitaItem) cbCitaVinculada.getSelectedItem();
        if (ci != null && ci.cita != null) {
            txtContactoDestino.setText(ci.cita.getTelefonoCliente());
            regenerarMensajePlantilla();
        }
    }

    private void regenerarMensajePlantilla() {
        CitaItem ci = (CitaItem) cbCitaVinculada.getSelectedItem();
        if (ci == null || ci.cita == null) return;
        Cita c = ci.cita;
        String plantilla = (String) cbPlantilla.getSelectedItem();

        if (plantilla != null && plantilla.contains("ayuno")) {
            txtMensaje.setText("Hola " + c.getNombreCliente() + ", le recordamos que " + c.getNombreMascota() + " tiene programada su cita de " + c.getTipoServicio() + " el " + c.getFechaFormateada() + " a las " + c.getHoraFormateada() + " hrs. Es fundamental que cumpla con 8 horas de ayuno estricto. Responda 'CONFIRMAR' para ratificar su turno.");
        } else if (plantilla != null && plantilla.contains("vacuna")) {
            txtMensaje.setText("¡Hola " + c.getNombreCliente() + "! En Happy Pets te recordamos que le toca el plan vacunal anual a " + c.getNombreMascota() + " el " + c.getFechaFormateada() + " a las " + c.getHoraFormateada() + " hrs con el " + c.getVeterinario() + ". Por favor confirme si asistirá.");
        } else if (plantilla != null && plantilla.contains("posoperatorio")) {
            txtMensaje.setText("Estimado/a " + c.getNombreCliente() + ", le escribimos de Veterinaria Happy Pets para consultar por la evolución de " + c.getNombreMascota() + " y confirmar su cita de control el " + c.getFechaFormateada() + " a las " + c.getHoraFormateada() + " hrs.");
        } else {
            txtMensaje.setText("Hola " + c.getNombreCliente() + ", te recordamos que " + c.getNombreMascota() + " tiene cita de " + c.getTipoServicio() + " el " + c.getFechaFormateada() + " a las " + c.getHoraFormateada() + " hrs en Happy Pets con el " + c.getVeterinario() + ". Por favor confirme su asistencia.");
        }
    }

    private void ejecutarEnvioRecordatorio() {
        CitaItem ci = (CitaItem) cbCitaVinculada.getSelectedItem();
        String destino = txtContactoDestino.getText().trim();
        String mensaje = txtMensaje.getText().trim();

        if (ci == null || destino.isEmpty() || mensaje.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Complete todos los campos del recordatorio antes de enviar.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String canal = rbWhatsApp.isSelected() ? "WhatsApp" : rbSms.isSelected() ? "SMS" : "E-Mail";

        RecordatorioCita rec = new RecordatorioCita(
                null,
                ci.cita.getIdCita(),
                ci.cita.getNombreMascota(),
                ci.cita.getNombreCliente(),
                destino,
                canal,
                LocalDateTime.now(),
                mensaje,
                "Enviado"
        );
        repo.agregarRecordatorio(rec);

        JOptionPane.showMessageDialog(this,
                "Recordatorio transmitido exitosamente por " + canal + " a " + ci.cita.getNombreCliente() + " (" + destino + ").",
                "Notificación Enviada", JOptionPane.INFORMATION_MESSAGE);

        recargarDatos();
    }

    private void reenviarSeleccionado() {
        int row = tablaRecordatorios.getSelectedRow();
        if (row < 0 || row >= listaActual.size()) {
            JOptionPane.showMessageDialog(this, "Seleccione un recordatorio de la tabla para reenviar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        RecordatorioCita r = listaActual.get(row);
        r.setFechaEnvio(LocalDateTime.now());
        r.setEstado("Enviado");
        recargarDatos();
        JOptionPane.showMessageDialog(this, "Recordatorio reenviado a " + r.getNombreCliente() + " por " + r.getCanal() + ".", "Reenvío Exitoso", JOptionPane.INFORMATION_MESSAGE);
    }

    private void marcarConfirmadoSeleccionado() {
        int row = tablaRecordatorios.getSelectedRow();
        if (row < 0 || row >= listaActual.size()) {
            JOptionPane.showMessageDialog(this, "Seleccione un recordatorio de la tabla para confirmar asistencia.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        RecordatorioCita r = listaActual.get(row);
        r.setEstado("Confirmado");
        repo.actualizarEstadoCita(r.getIdCita(), "Confirmada");
        recargarDatos();
        JOptionPane.showMessageDialog(this, "La asistencia para la cita " + r.getIdCita() + " de " + r.getNombreMascota() + " ha sido confirmada por el cliente.", "Asistencia Confirmada", JOptionPane.INFORMATION_MESSAGE);
    }

    private void verDetalleMensajeSeleccionado() {
        int row = tablaRecordatorios.getSelectedRow();
        if (row < 0 || row >= listaActual.size()) return;
        RecordatorioCita r = listaActual.get(row);

        String msg = "DETALLES DEL RECORDATORIO\n\n" +
                     "ID: " + r.getIdRecordatorio() + " | Cita: " + r.getIdCita() + "\n" +
                     "Paciente: " + r.getNombreMascota() + "\n" +
                     "Propietario: " + r.getNombreCliente() + "\n" +
                     "Canal: " + r.getCanal() + "\n" +
                     "Destinatario: " + r.getContactoDestino() + "\n" +
                     "Fecha y Hora de Envío: " + r.getFechaEnvioFormateada() + "\n" +
                     "Estado: " + r.getEstado() + "\n\n" +
                     "CONTENIDO DEL MENSAJE:\n" + r.getMensaje();

        JOptionPane.showMessageDialog(this, msg, "Notificación - " + r.getIdRecordatorio(), JOptionPane.INFORMATION_MESSAGE);
    }

    private static class CitaItem {
        final Cita cita;
        CitaItem(Cita cita) { this.cita = cita; }
        @Override
        public String toString() {
            return cita != null ? cita.getIdCita() + " - " + cita.getNombreMascota() + " (" + cita.getFechaFormateada() + " " + cita.getHoraFormateada() + " - " + cita.getTipoServicio() + ")" : "-";
        }
    }

    private static class BadgeEstadoRecRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            lbl.setHorizontalAlignment(SwingConstants.CENTER);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
            String st = String.valueOf(value);
            if (!isSelected) {
                if ("Confirmado".equalsIgnoreCase(st)) {
                    lbl.setForeground(new Color(22, 163, 74));
                } else if ("Enviado".equalsIgnoreCase(st)) {
                    lbl.setForeground(new Color(2, 132, 199));
                } else {
                    lbl.setForeground(new Color(217, 119, 6));
                }
            }
            return lbl;
        }
    }
}
