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
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
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
 * Submódulo 2.1: Agendamiento y Registro de Citas Médicas Veterinarias.
 */
public class VistaAgendamientoCitasPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private static final Color COLOR_BORDE = new Color(226, 232, 240);
    private static final Color COLOR_AZUL_PRIMARIO = new Color(2, 132, 199);
    private static final Color COLOR_TEXTO_TITULO = new Color(30, 41, 59);
    private static final Color COLOR_TEXTO_MUTED = new Color(100, 116, 139);

    private final RepositorioVeterinaria repo = RepositorioVeterinaria.getInstancia();

    // Filtros
    private JTextField txtBusqueda;
    private JComboBox<String> cbFiltroDoctor;
    private JComboBox<String> cbFiltroServicio;
    private String filtroFechaActual = "HOY";

    // KPIs
    private JLabel lblKpiCitasHoyVal;
    private JLabel lblKpiEnEsperaVal;
    private JLabel lblKpiConfirmadasVal;
    private JLabel lblKpiUrgenciasVal;

    // Formulario de Reserva
    private JComboBox<ClienteItem> cbCliente;
    private JComboBox<MascotaItem> cbMascota;
    private JTextField txtFecha;
    private JComboBox<String> cbHora;
    private JComboBox<String> cbDoctor;
    private JComboBox<String> cbServicio;
    private JComboBox<String> cbPrioridad;
    private JTextField txtMotivo;
    private JTextField txtCosto;
    private JTextField txtObservaciones;

    // Tabla
    private JTable tablaCitas;
    private DefaultTableModel modeloCitas;
    private JLabel lblContadorCitas;
    private List<Cita> listaCitasActual;

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
        setLayout(new BorderLayout());
        setOpaque(false);

        JPanel contenido = new JPanel();
        contenido.setOpaque(false);
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setBorder(new EmptyBorder(12, 18, 14, 18));

        // 1. Cabecera
        contenido.add(crearCabeceraVista());
        contenido.add(Box.createVerticalStrut(8));

        // 2. Barra de filtros de fecha y búsqueda
        contenido.add(crearBarraFiltros());
        contenido.add(Box.createVerticalStrut(10));

        // 3. Fila de 4 KPIs
        contenido.add(crearFilaKpiAgenda());
        contenido.add(Box.createVerticalStrut(10));

        // 4. Doble columna: Formulario de reserva y Tabla de citas
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

        JLabel lblTit = new JLabel("Agendamiento y Registro de Citas");
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblTit.setForeground(COLOR_TEXTO_TITULO);

        JLabel lblSub = new JLabel("Módulo 2.1 · Reserva, reprogramación y gestión cronológica de citas clínicas veterinarias");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblSub.setForeground(COLOR_TEXTO_MUTED);

        izq.add(lblTit);
        izq.add(Box.createVerticalStrut(2));
        izq.add(lblSub);
        cab.add(izq, BorderLayout.CENTER);

        JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 2));
        der.setOpaque(false);

        JButton btnExportar = crearBotonAccion("Exportar Agenda", false);
        btnExportar.setIcon(Iconos.crearIconoImprimir(12, COLOR_AZUL_PRIMARIO));
        btnExportar.setIconTextGap(4);
        btnExportar.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "La agenda de citas fue exportada satisfactoriamente a formato Excel / PDF.",
                "Exportación Exitosa", JOptionPane.INFORMATION_MESSAGE));

        JButton btnNuevaCita = crearBotonAccion("+ Nueva Cita", true);
        btnNuevaCita.addActionListener(e -> {
            limpiarFormulario();
            cbCliente.requestFocus();
        });

        der.add(btnExportar);
        der.add(btnNuevaCita);
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

        JPanel izq = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
        izq.setOpaque(false);

        JButton btnHoy = crearBotonAccion("Citas de Hoy", true);
        JButton btnManana = crearBotonAccion("Mañana", false);
        JButton btnTodas = crearBotonAccion("Todas", false);

        btnHoy.addActionListener(e -> {
            filtroFechaActual = "HOY";
            seleccionarBotonFiltro(btnHoy, btnManana, btnTodas);
            recargarDatos();
        });
        btnManana.addActionListener(e -> {
            filtroFechaActual = "MANANA";
            seleccionarBotonFiltro(btnManana, btnHoy, btnTodas);
            recargarDatos();
        });
        btnTodas.addActionListener(e -> {
            filtroFechaActual = "TODAS";
            seleccionarBotonFiltro(btnTodas, btnHoy, btnManana);
            recargarDatos();
        });

        izq.add(btnHoy);
        izq.add(btnManana);
        izq.add(btnTodas);

        izq.add(Box.createHorizontalStrut(10));

        JLabel lblDoc = new JLabel("Médico:");
        lblDoc.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblDoc.setForeground(COLOR_TEXTO_MUTED);
        izq.add(lblDoc);

        cbFiltroDoctor = new JComboBox<>(new String[]{
                "Todos los veterinarios",
                "Dr. Roberto Mendoza",
                "Dra. Laura Morales",
                "Dr. Carlos Silva"
        });
        cbFiltroDoctor.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        cbFiltroDoctor.setPreferredSize(new Dimension(150, 26));
        cbFiltroDoctor.setBackground(Color.WHITE);
        cbFiltroDoctor.addActionListener(e -> recargarDatos());
        izq.add(cbFiltroDoctor);

        JLabel lblServ = new JLabel("Servicio:");
        lblServ.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblServ.setForeground(COLOR_TEXTO_MUTED);
        izq.add(lblServ);

        cbFiltroServicio = new JComboBox<>(new String[]{
                "Todos los servicios",
                "Consulta Médica",
                "Vacunación",
                "Cirugía / Quirófano",
                "Grooming / Peluquería",
                "Control y Seguimiento"
        });
        cbFiltroServicio.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        cbFiltroServicio.setPreferredSize(new Dimension(140, 26));
        cbFiltroServicio.setBackground(Color.WHITE);
        cbFiltroServicio.addActionListener(e -> recargarDatos());
        izq.add(cbFiltroServicio);

        barra.add(izq, BorderLayout.WEST);

        // Derecha: Buscador
        JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 2));
        der.setOpaque(false);

        JLabel lblBuscar = new JLabel("Buscar:");
        lblBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblBuscar.setForeground(COLOR_TEXTO_MUTED);
        der.add(lblBuscar);

        txtBusqueda = new JTextField(12);
        txtBusqueda.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        txtBusqueda.setPreferredSize(new Dimension(140, 26));
        txtBusqueda.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225), 1),
                new EmptyBorder(2, 6, 2, 6)
        ));
        txtBusqueda.setToolTipText("Buscar por mascota, propietario, veterinario...");
        der.add(txtBusqueda);

        JButton btnBuscar = crearBotonAccion("Buscar", false);
        btnBuscar.setIcon(Iconos.crearIconoBuscar(12, COLOR_AZUL_PRIMARIO));
        btnBuscar.setIconTextGap(4);
        java.awt.event.ActionListener accionBuscar = e -> recargarDatos();
        btnBuscar.addActionListener(accionBuscar);
        txtBusqueda.addActionListener(accionBuscar);
        der.add(btnBuscar);

        barra.add(der, BorderLayout.EAST);
        return barra;
    }

    private void seleccionarBotonFiltro(JButton sel, JButton b2, JButton b3) {
        sel.putClientProperty("primario", true);
        b2.putClientProperty("primario", false);
        b3.putClientProperty("primario", false);
        sel.repaint();
        b2.repaint();
        b3.repaint();
    }

    private JPanel crearFilaKpiAgenda() {
        JPanel fila = new JPanel(new GridLayout(1, 4, 10, 0));
        fila.setOpaque(false);

        lblKpiCitasHoyVal = new JLabel("0");
        fila.add(crearCardKpi(lblKpiCitasHoyVal, "CITAS PROGRAMADAS HOY", new Color(224, 242, 254), Iconos.crearIconoCalendario(20, COLOR_AZUL_PRIMARIO)));

        lblKpiEnEsperaVal = new JLabel("0");
        fila.add(crearCardKpi(lblKpiEnEsperaVal, "EN SALA DE ESPERA", new Color(254, 243, 199), Iconos.crearIconoReloj(20, new Color(217, 119, 6))));

        lblKpiConfirmadasVal = new JLabel("0%");
        fila.add(crearCardKpi(lblKpiConfirmadasVal, "CONFIRMADAS POR WHATSAPP", new Color(220, 252, 231), Iconos.crearIconoCheck(20, new Color(22, 163, 74))));

        lblKpiUrgenciasVal = new JLabel("0");
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

        grid.add(crearTarjetaFormularioReserva());
        grid.add(crearTarjetaTablaCitas());

        return grid;
    }

    private JPanel crearTarjetaFormularioReserva() {
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

        // Cabecera de la tarjeta
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JLabel lblTit = new JLabel("Formulario de Reserva de Cita");
        lblTit.setIcon(Iconos.crearIconoCalendario(15, COLOR_AZUL_PRIMARIO));
        lblTit.setIconTextGap(6);
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTit.setForeground(COLOR_TEXTO_TITULO);
        top.add(lblTit, BorderLayout.WEST);

        JLabel lblReq = new JLabel("(*) Campos obligatorios");
        lblReq.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblReq.setForeground(new Color(239, 68, 68));
        top.add(lblReq, BorderLayout.EAST);
        card.add(top, BorderLayout.NORTH);

        // Formulario
        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));

        // 1. Cliente
        cbCliente = new JComboBox<>();
        cbCliente.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        cbCliente.setBackground(Color.WHITE);
        cbCliente.setPreferredSize(new Dimension(0, 26));
        cbCliente.addActionListener(e -> sincronizarMascotasDeCliente());

        // 2. Mascota
        cbMascota = new JComboBox<>();
        cbMascota.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        cbMascota.setBackground(Color.WHITE);
        cbMascota.setPreferredSize(new Dimension(0, 26));

        JPanel fila1 = new JPanel(new GridLayout(1, 2, 8, 0));
        fila1.setOpaque(false);
        fila1.setAlignmentX(Component.LEFT_ALIGNMENT);
        fila1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        fila1.add(crearFilaCampo("Propietario Responsable *", cbCliente));
        fila1.add(crearFilaCampo("Paciente (Mascota) *", cbMascota));
        form.add(fila1);
        form.add(Box.createVerticalStrut(6));

        // 3. Fecha y Hora
        txtFecha = crearCampoTextoCompacto();
        txtFecha.setText(LocalDate.now().format(Cita.FECHA_FORMATTER));

        cbHora = new JComboBox<>(new String[]{
                "08:30", "09:00", "09:30", "10:00", "10:30", "11:00", "11:30",
                "12:00", "14:00", "14:30", "15:00", "15:30", "16:00", "16:30",
                "17:00", "17:30", "18:00", "18:30"
        });
        cbHora.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        cbHora.setBackground(Color.WHITE);

        JPanel fila2 = new JPanel(new GridLayout(1, 2, 8, 0));
        fila2.setOpaque(false);
        fila2.setAlignmentX(Component.LEFT_ALIGNMENT);
        fila2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        fila2.add(crearFilaCampo("Fecha de la Cita (dd/MM/yyyy) *", txtFecha));
        fila2.add(crearFilaCampo("Horario de Turno *", cbHora));
        form.add(fila2);
        form.add(Box.createVerticalStrut(6));

        // 4. Veterinario y Servicio
        cbDoctor = new JComboBox<>(new String[]{
                "Dr. Roberto Mendoza (Medicina General / Cirugía)",
                "Dra. Laura Morales (Dermatología / Vacunas)",
                "Dr. Carlos Silva (Traumatología)"
        });
        cbDoctor.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        cbDoctor.setBackground(Color.WHITE);

        cbServicio = new JComboBox<>(new String[]{
                "Consulta Médica",
                "Vacunación",
                "Cirugía / Quirófano",
                "Grooming / Peluquería",
                "Control y Seguimiento"
        });
        cbServicio.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        cbServicio.setBackground(Color.WHITE);

        JPanel fila3 = new JPanel(new GridLayout(1, 2, 8, 0));
        fila3.setOpaque(false);
        fila3.setAlignmentX(Component.LEFT_ALIGNMENT);
        fila3.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        fila3.add(crearFilaCampo("Veterinario Tratante *", cbDoctor));
        fila3.add(crearFilaCampo("Tipo de Servicio *", cbServicio));
        form.add(fila3);
        form.add(Box.createVerticalStrut(6));

        // 5. Prioridad y Costo
        cbPrioridad = new JComboBox<>(new String[]{"Normal", "Urgente", "Emergencia"});
        cbPrioridad.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        cbPrioridad.setBackground(Color.WHITE);

        txtCosto = crearCampoTextoCompacto();
        txtCosto.setText("75.00");

        JPanel fila4 = new JPanel(new GridLayout(1, 2, 8, 0));
        fila4.setOpaque(false);
        fila4.setAlignmentX(Component.LEFT_ALIGNMENT);
        fila4.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        fila4.add(crearFilaCampo("Nivel de Prioridad *", cbPrioridad));
        fila4.add(crearFilaCampo("Costo Estimado (S/.)", txtCosto));
        form.add(fila4);
        form.add(Box.createVerticalStrut(6));

        // 6. Motivo
        txtMotivo = crearCampoTextoCompacto();
        form.add(crearFilaCampo("Motivo de Consulta / Síntomas *", txtMotivo));
        form.add(Box.createVerticalStrut(6));

        // 7. Observaciones
        txtObservaciones = crearCampoTextoCompacto();
        form.add(crearFilaCampo("Instrucciones Previas / Observaciones", txtObservaciones));

        card.add(form, BorderLayout.CENTER);

        // Botones inferiores
        JPanel bot = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        bot.setOpaque(false);

        JButton btnLimpiar = crearBotonAccion("Limpiar", false);
        btnLimpiar.addActionListener(e -> limpiarFormulario());

        JButton btnAgendar = crearBotonAccion("Agendar Cita", true);
        btnAgendar.addActionListener(e -> registrarNuevaCita());

        bot.add(btnLimpiar);
        bot.add(btnAgendar);
        card.add(bot, BorderLayout.SOUTH);

        return card;
    }

    private JPanel crearTarjetaTablaCitas() {
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

        // Cabecera tabla
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JLabel lblTit = new JLabel("Citas Programadas");
        lblTit.setIcon(Iconos.crearIconoHistorial(15, COLOR_AZUL_PRIMARIO));
        lblTit.setIconTextGap(6);
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTit.setForeground(COLOR_TEXTO_TITULO);
        top.add(lblTit, BorderLayout.WEST);

        lblContadorCitas = new JLabel("0 citas registradas");
        lblContadorCitas.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblContadorCitas.setForeground(COLOR_TEXTO_MUTED);
        top.add(lblContadorCitas, BorderLayout.EAST);
        card.add(top, BorderLayout.NORTH);

        // Tabla
        String[] columnas = {"Hora", "ID", "Paciente", "Propietario", "Servicio", "Veterinario", "Estado", "Prioridad"};
        modeloCitas = new DefaultTableModel(columnas, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        tablaCitas = new JTable(modeloCitas);
        Ui.formatearTabla(tablaCitas);
        tablaCitas.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        tablaCitas.setRowHeight(26);

        // Renderizador de Estado y Prioridad con colores
        tablaCitas.getColumnModel().getColumn(6).setCellRenderer(new BadgeEstadoRenderer());
        tablaCitas.getColumnModel().getColumn(7).setCellRenderer(new BadgePrioridadRenderer());

        tablaCitas.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    mostrarDetalleCitaSeleccionada();
                }
            }
        });

        JScrollPane sp = new JScrollPane(tablaCitas);
        sp.setPreferredSize(new Dimension(540, 240));
        sp.setBorder(BorderFactory.createLineBorder(COLOR_BORDE, 1));
        card.add(sp, BorderLayout.CENTER);

        // Pie de tabla con acciones de flujo
        JPanel bot = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 4));
        bot.setOpaque(false);

        JButton btnPasarEspera = crearBotonAccion("Pasar a Sala de Espera", false);
        btnPasarEspera.setIcon(Iconos.crearIconoReloj(12, new Color(217, 119, 6)));
        btnPasarEspera.setIconTextGap(4);
        btnPasarEspera.addActionListener(e -> transferirASalaEspera());

        JButton btnRecordatorio = crearBotonAccion("Notificar WhatsApp", false);
        btnRecordatorio.setIcon(Iconos.crearIconoWhatsApp(12, new Color(22, 163, 74)));
        btnRecordatorio.setIconTextGap(4);
        btnRecordatorio.addActionListener(e -> enviarRecordatorioSeleccionado());

        JButton btnAtendida = crearBotonAccion("Marcar Atendida", true);
        btnAtendida.addActionListener(e -> marcarCitaAtendida());

        bot.add(btnPasarEspera);
        bot.add(btnRecordatorio);
        bot.add(btnAtendida);
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

    private void sincronizarMascotasDeCliente() {
        cbMascota.removeAllItems();
        ClienteItem ci = (ClienteItem) cbCliente.getSelectedItem();
        if (ci != null && ci.cliente != null) {
            for (Mascota m : ci.cliente.getMascotas()) {
                cbMascota.addItem(new MascotaItem(m));
            }
        }
    }

    public void recargarDatos() {
        // Cargar clientes en combo si está vacío
        if (cbCliente.getItemCount() == 0) {
            for (Cliente c : repo.getClientes()) {
                cbCliente.addItem(new ClienteItem(c));
            }
            sincronizarMascotasDeCliente();
        }

        LocalDate hoy = LocalDate.now();
        List<Cita> todas = repo.getCitas();

        // Filtrar por fecha
        LocalDate fFiltro = null;
        if ("HOY".equals(filtroFechaActual)) {
            fFiltro = hoy;
        } else if ("MANANA".equals(filtroFechaActual)) {
            fFiltro = hoy.plusDays(1);
        }

        String doctorSel = (String) cbFiltroDoctor.getSelectedItem();
        String servicioSel = (String) cbFiltroServicio.getSelectedItem();
        String txtQ = txtBusqueda != null ? txtBusqueda.getText().trim().toLowerCase() : "";

        LocalDate finalFFiltro = fFiltro;
        listaCitasActual = todas.stream().filter(c -> {
            if (finalFFiltro != null && !c.getFecha().equals(finalFFiltro)) return false;
            if (doctorSel != null && !doctorSel.startsWith("Todos") && !c.getVeterinario().contains(doctorSel.replace("Dr. ", "").replace("Dra. ", ""))) return false;
            if (servicioSel != null && !servicioSel.startsWith("Todos") && !c.getTipoServicio().equalsIgnoreCase(servicioSel)) return false;
            if (!txtQ.isEmpty()) {
                boolean match = c.getNombreMascota().toLowerCase().contains(txtQ) ||
                                c.getNombreCliente().toLowerCase().contains(txtQ) ||
                                c.getIdCita().toLowerCase().contains(txtQ) ||
                                c.getVeterinario().toLowerCase().contains(txtQ);
                if (!match) return false;
            }
            return true;
        }).sorted((a, b) -> a.getHora().compareTo(b.getHora())).toList();

        // Llenar tabla
        modeloCitas.setRowCount(0);
        for (Cita c : listaCitasActual) {
            modeloCitas.addRow(new Object[]{
                    c.getHoraFormateada(),
                    c.getIdCita(),
                    c.getNombreMascota(),
                    c.getNombreCliente(),
                    c.getTipoServicio(),
                    c.getVeterinario(),
                    c.getEstado(),
                    c.getPrioridad()
            });
        }
        lblContadorCitas.setText(listaCitasActual.size() + " citas registradas");

        // Actualizar KPIs
        long citasHoy = todas.stream().filter(c -> c.getFecha().equals(hoy)).count();
        long enEspera = todas.stream().filter(c -> c.getFecha().equals(hoy) && "En Sala de Espera".equalsIgnoreCase(c.getEstado())).count();
        long confirmadas = todas.stream().filter(c -> c.getFecha().equals(hoy) && "Confirmada".equalsIgnoreCase(c.getEstado())).count();
        long urgencias = todas.stream().filter(c -> c.getFecha().equals(hoy) && ("Urgente".equalsIgnoreCase(c.getPrioridad()) || "Emergencia".equalsIgnoreCase(c.getPrioridad()))).count();

        lblKpiCitasHoyVal.setText(String.valueOf(citasHoy));
        lblKpiEnEsperaVal.setText(String.valueOf(enEspera));
        lblKpiConfirmadasVal.setText((citasHoy > 0 ? (confirmadas * 100 / citasHoy) : 0) + "%");
        lblKpiUrgenciasVal.setText(String.valueOf(urgencias));
    }

    private void registrarNuevaCita() {
        ClienteItem cItem = (ClienteItem) cbCliente.getSelectedItem();
        MascotaItem mItem = (MascotaItem) cbMascota.getSelectedItem();
        String strFecha = txtFecha.getText().trim();
        String strHora = (String) cbHora.getSelectedItem();
        String motivo = txtMotivo.getText().trim();

        if (cItem == null || mItem == null || strFecha.isEmpty() || motivo.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Por favor complete los campos obligatorios: Propietario, Mascota, Fecha y Motivo de Consulta.",
                    "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        LocalDate fechaCita;
        try {
            fechaCita = LocalDate.parse(strFecha, Cita.FECHA_FORMATTER);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Formato de fecha inválido. Ingrese la fecha en formato dd/MM/yyyy (ej: 02/10/2026).",
                    "Error de Fecha", JOptionPane.ERROR_MESSAGE);
            return;
        }

        LocalTime horaCita = LocalTime.parse(strHora, Cita.HORA_FORMATTER);
        String docCompleto = (String) cbDoctor.getSelectedItem();
        String docNombre = docCompleto != null ? docCompleto.split("\\(")[0].trim() : "Dr. Roberto Mendoza";
        String serv = (String) cbServicio.getSelectedItem();
        String prio = (String) cbPrioridad.getSelectedItem();
        double costo = 75.0;
        try {
            costo = Double.parseDouble(txtCosto.getText().trim());
        } catch (Exception ignored) { }

        Cita nueva = new Cita(
                null,
                mItem.mascota.getCodigo(),
                mItem.mascota.getNombre(),
                mItem.mascota.getEspecie() + " · " + mItem.mascota.getRaza(),
                cItem.cliente.getNumeroDocumento(),
                cItem.cliente.getNombreCompleto(),
                cItem.cliente.getTelefonoPrincipal(),
                fechaCita,
                horaCita,
                30,
                docNombre,
                serv,
                "Programada",
                motivo,
                prio,
                txtObservaciones.getText().trim(),
                costo
        );

        repo.guardarCita(nueva);
        JOptionPane.showMessageDialog(this,
                "Cita " + nueva.getIdCita() + " agendada exitosamente para " + nueva.getNombreMascota() + "\nFecha: " + nueva.getFechaFormateada() + " a las " + nueva.getHoraFormateada() + " hrs.",
                "Cita Agendada", JOptionPane.INFORMATION_MESSAGE);

        limpiarFormulario();
        recargarDatos();
    }

    private void limpiarFormulario() {
        txtMotivo.setText("");
        txtObservaciones.setText("");
        txtFecha.setText(LocalDate.now().format(Cita.FECHA_FORMATTER));
        cbHora.setSelectedIndex(0);
        cbServicio.setSelectedIndex(0);
        cbPrioridad.setSelectedIndex(0);
        txtCosto.setText("75.00");
    }

    private void transferirASalaEspera() {
        int row = tablaCitas.getSelectedRow();
        if (row < 0 || row >= listaCitasActual.size()) {
            JOptionPane.showMessageDialog(this, "Seleccione una cita de la tabla para pasar a Sala de Espera.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Cita cita = listaCitasActual.get(row);
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
                "El paciente " + cita.getNombreMascota() + " ha ingresado a la Sala de Espera con ticket " + pt.getIdTicket() + ".\nPuede monitorizar sus constantes y triaje en el submódulo 'Sala de Espera y Triaje'.",
                "Ingreso a Sala de Espera", JOptionPane.INFORMATION_MESSAGE);

        recargarDatos();

        if (navegacionListener != null) {
            int op = JOptionPane.showConfirmDialog(this, "¿Desea abrir la pantalla de Sala de Espera y Triaje ahora?", "Navegación", JOptionPane.YES_NO_OPTION);
            if (op == JOptionPane.YES_OPTION) {
                navegacionListener.irASalaEspera(cita);
            }
        }
    }

    private void enviarRecordatorioSeleccionado() {
        int row = tablaCitas.getSelectedRow();
        if (row < 0 || row >= listaCitasActual.size()) {
            JOptionPane.showMessageDialog(this, "Seleccione una cita de la tabla para enviar recordatorio.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Cita c = listaCitasActual.get(row);
        c.setEstado("Confirmada");
        recargarDatos();

        JOptionPane.showMessageDialog(this,
                "Recordatorio enviado exitosamente vía WhatsApp a " + c.getNombreCliente() + " (" + c.getTelefonoCliente() + "):\n\n" +
                "\"Hola " + c.getNombreCliente() + ", te recordamos que " + c.getNombreMascota() + " tiene cita de " + c.getTipoServicio() + " el " + c.getFechaFormateada() + " a las " + c.getHoraFormateada() + " hrs con el " + c.getVeterinario() + " en Happy Pets.\"",
                "Recordatorio WhatsApp Enviado", JOptionPane.INFORMATION_MESSAGE);
    }

    private void marcarCitaAtendida() {
        int row = tablaCitas.getSelectedRow();
        if (row < 0 || row >= listaCitasActual.size()) {
            JOptionPane.showMessageDialog(this, "Seleccione una cita de la tabla para marcarla como atendida.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Cita c = listaCitasActual.get(row);
        c.setEstado("Atendida");
        recargarDatos();
        JOptionPane.showMessageDialog(this, "La cita de " + c.getNombreMascota() + " ha sido finalizada y marcada como Atendida.", "Cita Atendida", JOptionPane.INFORMATION_MESSAGE);
    }

    private void mostrarDetalleCitaSeleccionada() {
        int row = tablaCitas.getSelectedRow();
        if (row < 0 || row >= listaCitasActual.size()) return;
        Cita c = listaCitasActual.get(row);

        String msg = "DETALLES DE LA CITA MÉDICA\n\n" +
                     "Código: " + c.getIdCita() + "\n" +
                     "Fecha y Hora: " + c.getFechaFormateada() + " " + c.getHoraFormateada() + " hrs\n" +
                     "Paciente: " + c.getNombreMascota() + " (" + c.getEspecieRaza() + ")\n" +
                     "Propietario: " + c.getNombreCliente() + " (DNI: " + c.getDniCliente() + ")\n" +
                     "Teléfono: " + c.getTelefonoCliente() + "\n" +
                     "Veterinario: " + c.getVeterinario() + "\n" +
                     "Servicio: " + c.getTipoServicio() + "\n" +
                     "Estado: " + c.getEstado() + "\n" +
                     "Prioridad: " + c.getPrioridad() + "\n" +
                     "Motivo: " + c.getMotivo() + "\n" +
                     "Costo Estimado: S/. " + String.format("%.2f", c.getCostoEstimado()) + "\n" +
                     "Observaciones: " + (c.getObservaciones().isEmpty() ? "Ninguna" : c.getObservaciones());

        JOptionPane.showMessageDialog(this, msg, "Información de Cita - " + c.getIdCita(), JOptionPane.INFORMATION_MESSAGE);
    }

    // Helper classes para comboboxes
    private static class ClienteItem {
        final Cliente cliente;
        ClienteItem(Cliente cliente) { this.cliente = cliente; }
        @Override
        public String toString() {
            return cliente != null ? cliente.getNombreCompleto() + " (" + cliente.getNumeroDocumento() + ")" : "-";
        }
    }

    private static class MascotaItem {
        final Mascota mascota;
        MascotaItem(Mascota mascota) { this.mascota = mascota; }
        @Override
        public String toString() {
            return mascota != null ? mascota.getNombre() + " (" + mascota.getEspecie() + " · " + mascota.getRaza() + ")" : "-";
        }
    }

    // Renderizadores visuales para tablas
    private static class BadgeEstadoRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            lbl.setHorizontalAlignment(SwingConstants.CENTER);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
            String st = String.valueOf(value);
            if (!isSelected) {
                if ("Confirmada".equalsIgnoreCase(st)) {
                    lbl.setForeground(new Color(22, 163, 74));
                } else if ("En Sala de Espera".equalsIgnoreCase(st)) {
                    lbl.setForeground(new Color(2, 132, 199));
                } else if ("Atendida".equalsIgnoreCase(st)) {
                    lbl.setForeground(new Color(79, 70, 229));
                } else if ("Cancelada".equalsIgnoreCase(st)) {
                    lbl.setForeground(new Color(220, 38, 38));
                } else {
                    lbl.setForeground(new Color(100, 116, 139));
                }
            }
            return lbl;
        }
    }

    private static class BadgePrioridadRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            lbl.setHorizontalAlignment(SwingConstants.CENTER);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
            String pr = String.valueOf(value);
            if (!isSelected) {
                if ("Emergencia".equalsIgnoreCase(pr)) {
                    lbl.setForeground(new Color(220, 38, 38));
                } else if ("Urgente".equalsIgnoreCase(pr)) {
                    lbl.setForeground(new Color(217, 119, 6));
                } else {
                    lbl.setForeground(new Color(100, 116, 139));
                }
            }
            return lbl;
        }
    }
}
