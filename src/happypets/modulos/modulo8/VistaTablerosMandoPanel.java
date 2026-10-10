package happypets.modulos.modulo8;

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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import happypets.data.RepositorioVeterinaria;
import happypets.model.MetricaMensualIngreso;
import happypets.ui.Iconos;
import happypets.ui.Ui;

/**
 * Submódulo 8.1: Tableros de Mando (Dashboards)
 * Responsable: Arroyo Preciado, Harry Martin
 * Muestra KPIs ejecutivos, comparativo mensual de ingresos en Graphics2D nativo,
 * métricas de eficiencia operativa y la tabla de citas del día con búsqueda y paginación.
 */
public class VistaTablerosMandoPanel extends happypets.ui.AssetsModulo {
    private static final long serialVersionUID = 1L;

    private final RepositorioVeterinaria repo = RepositorioVeterinaria.getInstancia();

    // Filtros
    private JComboBox<String> comboPeriodo;
    private JComboBox<String> comboEspecie;
    private JTextField txtBuscarCita;

    // Componentes de datos
    private JTable tablaCitas;
    private DefaultTableModel modeloCitas;
    private JLabel lblTotalCitasHoy;
    private GraficoBarrasMensualPanel graficoMensual;

    // Datos de citas de hoy simuladas/cargadas del repositorio
    private static class FilaCitaHoy {
        String hora;
        String paciente;
        String especieRaza;
        String propietario;
        String veterinario;
        String motivo;
        String estado;

        FilaCitaHoy(String hora, String paciente, String especieRaza, String propietario,
                     String veterinario, String motivo, String estado) {
            this.hora = hora;
            this.paciente = paciente;
            this.especieRaza = especieRaza;
            this.propietario = propietario;
            this.veterinario = veterinario;
            this.motivo = motivo;
            this.estado = estado;
        }
    }

    private final List<FilaCitaHoy> listaCitasHoy = new ArrayList<>();
    private final List<FilaCitaHoy> citasFiltradas = new ArrayList<>();
    private int paginaActual = 1;
    private final int tamanoPagina = 5;

    public VistaTablerosMandoPanel() {
        setLayout(new BorderLayout(0, 16));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 24, 24, 24));

        inicializarDatosCitas();

        add(crearCabeceraSuperior(), BorderLayout.NORTH);

        // Contenedor principal con scroll por si la resolución es menor
        JPanel panelCuerpo = new JPanel();
        panelCuerpo.setLayout(new BoxLayout(panelCuerpo, BoxLayout.Y_AXIS));
        panelCuerpo.setBackground(Color.WHITE);
        panelCuerpo.setOpaque(false);

        panelCuerpo.add(crearPanelKPIs());
        panelCuerpo.add(Box.createVerticalStrut(18));
        panelCuerpo.add(crearPanelGraficoYEficiencia());
        panelCuerpo.add(Box.createVerticalStrut(18));
        panelCuerpo.add(crearPanelCitasHoy());

        JScrollPane scrollGeneral = new JScrollPane(panelCuerpo);
        scrollGeneral.setOpaque(false);
        scrollGeneral.getViewport().setOpaque(false);
        scrollGeneral.setBorder(null);
        scrollGeneral.getVerticalScrollBar().setUnitIncrement(16);
        scrollGeneral.setBackground(Color.WHITE);
        add(scrollGeneral, BorderLayout.CENTER);

        filtrarCitas();
    }

    private void inicializarDatosCitas() {
        listaCitasHoy.clear();
        listaCitasHoy.add(new FilaCitaHoy("08:30 AM", "Max", "Canino · Golden Retriever", "Carlos Mendoza Soto", "Dra. Elena Ruiz", "Revisión Post-Cirugía Traumatológica", "En Espera"));
        listaCitasHoy.add(new FilaCitaHoy("09:15 AM", "Misi", "Felino · Siamés", "Lucía Torres Alva", "Dr. Marcos León", "Vacunación Anual Triple Felina", "En Atención"));
        listaCitasHoy.add(new FilaCitaHoy("10:00 AM", "Thor", "Canino · Pastor Alemán", "Fernando Castillo", "Dra. Clara Vega", "Otitis Externa Bilateral Crónica", "Confirmada"));
        listaCitasHoy.add(new FilaCitaHoy("10:45 AM", "Kira", "Canino · Pug", "Ana María Rojas", "Dr. Andrés Pardo", "Control Dermatológico y Alergia", "Confirmada"));
        listaCitasHoy.add(new FilaCitaHoy("11:30 AM", "Simba", "Felino · Persa", "Jorge Valdivia", "Dra. Sofía Mora", "Profilaxis Dental y Halitosis", "Confirmada"));
        listaCitasHoy.add(new FilaCitaHoy("12:15 PM", "Luna", "Felino · Común Europeo", "Mariana Paredes", "Dra. Elena Ruiz", "Chequeo Preventivo Geriátrico", "Confirmada"));
        listaCitasHoy.add(new FilaCitaHoy("02:00 PM", "Rocky", "Canino · Bulldog Francés", "Roberto Dávila", "Dr. Marcos León", "Dificultad Respiratoria / Braquicéfalo", "Programada"));
        listaCitasHoy.add(new FilaCitaHoy("02:45 PM", "Bella", "Canino · Beagle", "Carmen Morales", "Dra. Clara Vega", "Desparasitación y Pipeta Antipulgas", "Programada"));
        listaCitasHoy.add(new FilaCitaHoy("03:30 PM", "Coco", "Canino · Poodle", "Sofía Alarcón", "Dr. Andrés Pardo", "Corte de Uñas y Limpieza de Oídos", "Programada"));
        listaCitasHoy.add(new FilaCitaHoy("04:15 PM", "Nala", "Felino · Angora", "Diego Navarro", "Dra. Sofía Mora", "Control de Peso y Nutrición Renal", "Programada"));
        listaCitasHoy.add(new FilaCitaHoy("05:00 PM", "Toby", "Canino · Pug", "Carlos Eduardo Morales", "Dra. Clara Vega", "Ecografía Abdominal Preventiva", "Programada"));
        listaCitasHoy.add(new FilaCitaHoy("05:45 PM", "Bruno", "Canino · Rottweiler", "Héctor Palacios", "Dr. Andrés Pardo", "Vacuna Antirrábica Obligatoria", "Programada"));
    }

    private JPanel crearCabeceraSuperior() {
        JPanel cabecera = new JPanel(new BorderLayout(16, 8));
        cabecera.setOpaque(false);

        // Título y píldora sede
        JPanel pnlTitulos = new JPanel();
        pnlTitulos.setLayout(new BoxLayout(pnlTitulos, BoxLayout.Y_AXIS));
        pnlTitulos.setOpaque(false);

        JPanel filaTitulo = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filaTitulo.setOpaque(false);

        JLabel lblTitulo = new JLabel("Tableros de Mando");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(new Color(15, 23, 42));
        filaTitulo.add(lblTitulo);

        JLabel badgeSede = new JLabel("  Sede Central y Filiales  ");
        badgeSede.setFont(new Font("Segoe UI", Font.BOLD, 11));
        badgeSede.setOpaque(true);
        badgeSede.setBackground(new Color(224, 242, 254));
        badgeSede.setForeground(new Color(3, 105, 161));
        badgeSede.setBorder(BorderFactory.createEmptyBorder(3, 6, 3, 6));
        filaTitulo.add(badgeSede);

        pnlTitulos.add(filaTitulo);
        pnlTitulos.add(Box.createVerticalStrut(4));

        JLabel lblSub = new JLabel("Monitoreo ejecutivo de indicadores de rendimiento, citas e ingresos en tiempo real.");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(new Color(100, 116, 139));
        pnlTitulos.add(lblSub);

        cabecera.add(pnlTitulos, BorderLayout.WEST);

        // Controles de fecha y filtro a la derecha
        JPanel pnlFiltros = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlFiltros.setOpaque(false);

        comboPeriodo = new JComboBox<>(new String[]{
                "01 May 2024 - 31 May 2024",
                "Abril 2024 (Mes Completo)",
                "Primer Trimestre 2024 (Q1)",
                "Últimos 12 Meses"
        });
        comboPeriodo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        comboPeriodo.setPreferredSize(new Dimension(210, 34));
        comboPeriodo.setBackground(Color.WHITE);

        comboEspecie = new JComboBox<>(new String[]{
                "Todas las Especies",
                "Sólo Caninos",
                "Sólo Felinos",
                "Animales Exóticos"
        });
        comboEspecie.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        comboEspecie.setPreferredSize(new Dimension(150, 34));
        comboEspecie.setBackground(Color.WHITE);

        JButton btnActualizar = Ui.botonPrimario("Actualizar Indicadores", Iconos.crearIconoRefrescar(14, Color.WHITE));
        btnActualizar.setPreferredSize(new Dimension(175, 34));
        btnActualizar.addActionListener(e -> {
            graficoMensual.repaint();
            filtrarCitas();
            JOptionPane.showMessageDialog(this,
                    "Indicadores ejecutivos y métricas de rendimiento actualizados con éxito.",
                    "Sincronización BI", JOptionPane.INFORMATION_MESSAGE);
        });

        pnlFiltros.add(comboPeriodo);
        pnlFiltros.add(comboEspecie);
        pnlFiltros.add(btnActualizar);

        cabecera.add(pnlFiltros, BorderLayout.EAST);
        return cabecera;
    }

    private JPanel crearPanelKPIs() {
        JPanel panelKPIs = new JPanel(new GridLayout(1, 4, 14, 0));
        panelKPIs.setOpaque(false);
        panelKPIs.setMaximumSize(new Dimension(Integer.MAX_VALUE, 115));

        // KPI 1: Ingresos Totales
        panelKPIs.add(crearTarjetaKPI(
                "Ingresos Totales (Total Revenue)",
                "S/ 148,250.00",
                "+12.4% vs abril",
                new Color(16, 185, 129),
                "Meta mensual alcanzada: 94.2%",
                new Color(236, 253, 245),
                Iconos.crearIconoPOS(22, new Color(13, 148, 136))
        ));

        // KPI 2: Citas Totales
        panelKPIs.add(crearTarjetaKPI(
                "Citas Totales (Appointments)",
                "1,420",
                "+5.8% mensual",
                new Color(16, 185, 129),
                "Promedio diario: 47.3 consultas",
                new Color(238, 242, 255),
                Iconos.crearIconoCalendario(22, new Color(79, 70, 229))
        ));

        // KPI 3: Pacientes Activos
        panelKPIs.add(crearTarjetaKPI(
                "Pacientes Activos (Active Patients)",
                "3,892",
                "+184 nuevos",
                new Color(2, 132, 199),
                "Tasa de retención clínica: 88.7%",
                new Color(240, 249, 255),
                Iconos.crearIconoMascota(22, new Color(2, 132, 199))
        ));

        // KPI 4: Ticket Promedio
        panelKPIs.add(crearTarjetaKPI(
                "Ticket Promedio por Paciente",
                "S/ 104.40",
                "+3.1% medio",
                new Color(16, 185, 129),
                "Combinados con farmacia: 64%",
                new Color(254, 243, 199),
                Iconos.crearIconoReportes(22, new Color(217, 119, 6))
        ));

        return panelKPIs;
    }

    private JPanel crearTarjetaKPI(String titulo, String valor, String badgeTexto, Color badgeColor,
                                   String pieTexto, Color iconoBg, javax.swing.Icon icono) {
        JPanel card = new JPanel(new BorderLayout(8, 6));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                BorderFactory.createEmptyBorder(12, 14, 12, 14)
        ));

        // Fila Superior: Título + Icono circular
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JLabel lblTit = new JLabel(titulo);
        lblTit.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblTit.setForeground(new Color(100, 116, 139));
        top.add(lblTit, BorderLayout.WEST);

        JLabel lblIcono = new JLabel(icono);
        lblIcono.setOpaque(true);
        lblIcono.setBackground(iconoBg);
        lblIcono.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        top.add(lblIcono, BorderLayout.EAST);
        card.add(top, BorderLayout.NORTH);

        // Centro: Valor + Badge
        JPanel centro = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
        centro.setOpaque(false);

        JLabel lblVal = new JLabel(valor);
        lblVal.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblVal.setForeground(new Color(15, 23, 42));
        centro.add(lblVal);

        JLabel badge = new JLabel("  " + badgeTexto + "  ");
        badge.setFont(new Font("Segoe UI", Font.BOLD, 10));
        badge.setOpaque(true);
        badge.setBackground(new Color(badgeColor.getRed(), badgeColor.getGreen(), badgeColor.getBlue(), 25));
        badge.setForeground(badgeColor);
        badge.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));
        centro.add(badge);

        card.add(centro, BorderLayout.CENTER);

        // Pie: Texto complementario
        JLabel lblPie = new JLabel(pieTexto);
        lblPie.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblPie.setForeground(new Color(148, 163, 184));
        card.add(lblPie, BorderLayout.SOUTH);

        return card;
    }

    private JPanel crearPanelGraficoYEficiencia() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;

        // Gráfico Izquierdo (Tendencia Mensual de Ingresos)
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.65;
        gbc.insets = new Insets(0, 0, 0, 14);

        JPanel panelGraficoCard = new JPanel(new BorderLayout(0, 10));
        panelGraficoCard.setBackground(Color.WHITE);
        panelGraficoCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                BorderFactory.createEmptyBorder(14, 16, 14, 16)
        ));

        // Cabecera del gráfico
        JPanel cabGrafico = new JPanel(new BorderLayout());
        cabGrafico.setOpaque(false);

        JPanel titGrafico = new JPanel();
        titGrafico.setLayout(new BoxLayout(titGrafico, BoxLayout.Y_AXIS));
        titGrafico.setOpaque(false);

        JLabel lblTitG = new JLabel("Tendencia Mensual de Ingresos (Monthly Revenue)");
        lblTitG.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitG.setForeground(new Color(15, 23, 42));
        titGrafico.add(lblTitG);

        JLabel lblSubG = new JLabel("Comparativo de los últimos 12 meses (Jun 23 - May 24) en miles de Soles (PEN)");
        lblSubG.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblSubG.setForeground(new Color(100, 116, 139));
        titGrafico.add(lblSubG);
        cabGrafico.add(titGrafico, BorderLayout.WEST);

        // Leyenda
        JPanel leyenda = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        leyenda.setOpaque(false);

        JLabel dot1 = new JLabel("■ Servicios Clínicos");
        dot1.setFont(new Font("Segoe UI", Font.BOLD, 11));
        dot1.setForeground(new Color(15, 76, 129));
        leyenda.add(dot1);

        JLabel dot2 = new JLabel("■ Farmacia y Alimentos");
        dot2.setFont(new Font("Segoe UI", Font.BOLD, 11));
        dot2.setForeground(new Color(56, 189, 248));
        leyenda.add(dot2);

        cabGrafico.add(leyenda, BorderLayout.EAST);
        panelGraficoCard.add(cabGrafico, BorderLayout.NORTH);

        graficoMensual = new GraficoBarrasMensualPanel(repo.getMetricasMensuales());
        graficoMensual.setPreferredSize(new Dimension(600, 240));
        panelGraficoCard.add(graficoMensual, BorderLayout.CENTER);

        panel.add(panelGraficoCard, gbc);

        // Tarjeta Derecha (Métricas de Eficiencia Mayo 2024)
        gbc.gridx = 1;
        gbc.weightx = 0.35;
        gbc.insets = new Insets(0, 0, 0, 0);

        JPanel panelEficienciaCard = new JPanel();
        panelEficienciaCard.setLayout(new BoxLayout(panelEficienciaCard, BoxLayout.Y_AXIS));
        panelEficienciaCard.setBackground(Color.WHITE);
        panelEficienciaCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                BorderFactory.createEmptyBorder(14, 16, 14, 16)
        ));

        JLabel lblTitEf = new JLabel("Métricas de Eficiencia (Mayo 2024)");
        lblTitEf.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblEficienciaEstilo(panelEficienciaCard, lblTitEf);

        JLabel lblSubEf = new JLabel("Rendimiento asistencial y optimización de recursos");
        lblSubEf.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblSubEf.setForeground(new Color(100, 116, 139));
        panelEficienciaCard.add(lblSubEf);
        panelEficienciaCard.add(Box.createVerticalStrut(14));

        // Métrica 1
        panelEficienciaCard.add(crearBarraEficiencia(
                "Ocupación de Quirófanos", "78%", 78,
                "28 cirugías realizadas esta semana", new Color(14, 165, 233)
        ));
        panelEficienciaCard.add(Box.createVerticalStrut(12));

        // Métrica 2
        panelEficienciaCard.add(crearBarraEficiencia(
                "Puntualidad en Consultas", "92%", 92,
                "Tiempo medio de espera: 8.4 min", new Color(16, 185, 129)
        ));
        panelEficienciaCard.add(Box.createVerticalStrut(12));

        // Métrica 3
        panelEficienciaCard.add(crearBarraEficiencia(
                "Pacientes Recurrentes (Check-ups)", "65%", 65,
                "412 vacunas y desparasitaciones", new Color(99, 102, 241)
        ));
        panelEficienciaCard.add(Box.createVerticalStrut(14));

        // Auditoría / Estado óptimo
        JPanel pnlAuditoria = new JPanel(new BorderLayout(8, 4));
        pnlAuditoria.setBackground(new Color(241, 245, 249));
        pnlAuditoria.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));

        JLabel lblAudTit = new JLabel("● Auditoría de Procesos: Óptimo");
        lblAudTit.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblAudTit.setForeground(new Color(15, 118, 110));
        pnlAuditoria.add(lblAudTit, BorderLayout.NORTH);

        JLabel lblAudDesc = new JLabel("Cumplimiento normativo y protocolos clínicos: 98.2% conformidad.");
        lblAudDesc.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblAudDesc.setForeground(new Color(71, 85, 105));
        pnlAuditoria.add(lblAudDesc, BorderLayout.CENTER);

        panelEficienciaCard.add(pnlAuditoria);

        panel.add(panelEficienciaCard, gbc);
        return panel;
    }

    private void lblEficienciaEstilo(JPanel parent, JLabel lbl) {
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        lbl.setForeground(new Color(15, 23, 42));
        parent.add(lbl);
    }

    private JPanel crearBarraEficiencia(String titulo, String porcentajeStr, int porcentajeVal,
                                        String detalle, Color barColor) {
        JPanel pnl = new JPanel();
        pnl.setLayout(new BoxLayout(pnl, BoxLayout.Y_AXIS));
        pnl.setOpaque(false);
        pnl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JLabel lblT = new JLabel(titulo);
        lblT.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblT.setForeground(new Color(30, 41, 59));
        top.add(lblT, BorderLayout.WEST);

        JLabel lblP = new JLabel(porcentajeStr);
        lblP.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblP.setForeground(barColor);
        top.add(lblP, BorderLayout.EAST);
        pnl.add(top);

        pnl.add(Box.createVerticalStrut(4));

        JProgressBar bar = new JProgressBar(0, 100);
        bar.setValue(porcentajeVal);
        bar.setPreferredSize(new Dimension(100, 7));
        bar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 7));
        bar.setForeground(barColor);
        bar.setBackground(new Color(241, 245, 249));
        bar.setBorderPainted(false);
        pnl.add(bar);

        pnl.add(Box.createVerticalStrut(3));

        JLabel lblD = new JLabel(detalle);
        lblD.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblD.setForeground(new Color(100, 116, 139));
        pnl.add(lblD);

        return pnl;
    }

    private JPanel crearPanelCitasHoy() {
        JPanel card = new JPanel(new BorderLayout(0, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                BorderFactory.createEmptyBorder(14, 16, 14, 16)
        ));

        // Cabecera de la tabla
        JPanel cabTabla = new JPanel(new BorderLayout(12, 0));
        cabTabla.setOpaque(false);

        JPanel titIzq = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        titIzq.setOpaque(false);

        JLabel lblTitC = new JLabel("Citas de Hoy (Today's Appointments)");
        lblTitC.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitC.setForeground(new Color(15, 23, 42));
        titIzq.add(lblTitC);

        lblTotalCitasHoy = new JLabel("  18 programadas hoy  ");
        lblTotalCitasHoy.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblTotalCitasHoy.setOpaque(true);
        lblTotalCitasHoy.setBackground(new Color(241, 245, 249));
        lblTotalCitasHoy.setForeground(new Color(71, 85, 105));
        lblTotalCitasHoy.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));
        titIzq.add(lblTotalCitasHoy);
        cabTabla.add(titIzq, BorderLayout.WEST);

        JPanel derControles = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        derControles.setOpaque(false);

        txtBuscarCita = new JTextField(15);
        txtBuscarCita.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtBuscarCita.setPreferredSize(new Dimension(180, 30));
        txtBuscarCita.setToolTipText("Buscar paciente, propietario o motivo...");
        txtBuscarCita.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { filtrarCitas(); }
            @Override public void removeUpdate(DocumentEvent e) { filtrarCitas(); }
            @Override public void changedUpdate(DocumentEvent e) { filtrarCitas(); }
        });
        derControles.add(new JLabel(Iconos.crearIconoLupa(14, new Color(100, 116, 139))));
        derControles.add(txtBuscarCita);

        JButton btnVerAgenda = Ui.botonSecundario("Ver Agenda Completa", Iconos.crearIconoCalendario(13, new Color(15, 23, 42)));
        btnVerAgenda.setPreferredSize(new Dimension(160, 30));
        btnVerAgenda.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                    "Redirigiendo a la agenda completa de citas médicas (Módulo 2)...",
                    "Agenda de Citas", JOptionPane.INFORMATION_MESSAGE);
        });
        derControles.add(btnVerAgenda);

        cabTabla.add(derControles, BorderLayout.EAST);
        card.add(cabTabla, BorderLayout.NORTH);

        // Tabla de Citas
        String[] columnas = {"Hora", "Paciente (Especie / Raza)", "Propietario", "Veterinario Asignado", "Motivo de Consulta", "Estado", "Acción"};
        modeloCitas = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };

        tablaCitas = new JTable(modeloCitas);
        tablaCitas.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaCitas.setRowHeight(38);
        tablaCitas.setGridColor(new Color(241, 245, 249));
        tablaCitas.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tablaCitas.getTableHeader().setBackground(Color.WHITE);
        tablaCitas.getTableHeader().setForeground(new Color(71, 85, 105));
        tablaCitas.setSelectionBackground(new Color(240, 249, 255));
        tablaCitas.setSelectionForeground(new Color(15, 23, 42));

        // Centrado de horas y estados
        DefaultTableCellRenderer centroRenderer = new DefaultTableCellRenderer();
        centroRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        tablaCitas.getColumnModel().getColumn(0).setCellRenderer(centroRenderer);
        tablaCitas.getColumnModel().getColumn(0).setPreferredWidth(90);
        tablaCitas.getColumnModel().getColumn(1).setPreferredWidth(170);
        tablaCitas.getColumnModel().getColumn(2).setPreferredWidth(150);
        tablaCitas.getColumnModel().getColumn(3).setPreferredWidth(140);
        tablaCitas.getColumnModel().getColumn(4).setPreferredWidth(210);
        tablaCitas.getColumnModel().getColumn(5).setPreferredWidth(110);
        tablaCitas.getColumnModel().getColumn(6).setPreferredWidth(90);

        // Render de Estado con badges de color
        tablaCitas.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                l.setHorizontalAlignment(SwingConstants.CENTER);
                String st = value != null ? value.toString() : "";
                l.setFont(new Font("Segoe UI", Font.BOLD, 11));
                l.setOpaque(true);
                if ("En Atención".equalsIgnoreCase(st)) {
                    l.setBackground(new Color(224, 242, 254));
                    l.setForeground(new Color(3, 105, 161));
                } else if ("En Espera".equalsIgnoreCase(st)) {
                    l.setBackground(new Color(254, 243, 199));
                    l.setForeground(new Color(180, 83, 9));
                } else if ("Confirmada".equalsIgnoreCase(st)) {
                    l.setBackground(new Color(236, 253, 245));
                    l.setForeground(new Color(16, 185, 129));
                } else {
                    l.setBackground(new Color(241, 245, 249));
                    l.setForeground(new Color(100, 116, 139));
                }
                return l;
            }
        });

        // Render de Acción
        tablaCitas.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(table, "Ver Ficha", isSelected, hasFocus, row, col);
                l.setHorizontalAlignment(SwingConstants.CENTER);
                l.setFont(new Font("Segoe UI", Font.BOLD, 11));
                l.setForeground(new Color(2, 132, 199));
                l.setCursor(new Cursor(Cursor.HAND_CURSOR));
                return l;
            }
        });

        tablaCitas.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int col = tablaCitas.columnAtPoint(e.getPoint());
                int fila = tablaCitas.rowAtPoint(e.getPoint());
                if (fila >= 0 && col == 6) {
                    mostrarDetalleCita(fila);
                }
            }
        });

        JScrollPane scrollTabla = new JScrollPane(tablaCitas);
        scrollTabla.setPreferredSize(new Dimension(800, 215));
        scrollTabla.setBorder(BorderFactory.createLineBorder(new Color(241, 245, 249), 1));
        card.add(scrollTabla, BorderLayout.CENTER);

        // Pie de paginación
        JPanel piePaginacion = new JPanel(new BorderLayout());
        piePaginacion.setOpaque(false);

        JLabel lblInfoPags = new JLabel("Mostrando 5 de 18 citas programadas");
        lblInfoPags.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblInfoPags.setForeground(new Color(100, 116, 139));
        piePaginacion.add(lblInfoPags, BorderLayout.WEST);

        JPanel pnlBotonesPag = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
        pnlBotonesPag.setOpaque(false);

        JButton btnAnt = new happypets.ui.BotonAsset("< Anterior");
        btnAnt.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btnAnt.setBackground(Color.WHITE);
        btnAnt.addActionListener(e -> {
            if (paginaActual > 1) {
                paginaActual--;
                actualizarTablaPaginada();
            }
        });

        JButton btnP1 = new happypets.ui.BotonAsset("1");
        btnP1.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnP1.setBackground(new Color(2, 132, 199));
        btnP1.setForeground(Color.WHITE);

        JButton btnP2 = new happypets.ui.BotonAsset("2");
        btnP2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btnP2.setBackground(Color.WHITE);
        btnP2.addActionListener(e -> {
            paginaActual = 2;
            actualizarTablaPaginada();
        });

        JButton btnP3 = new happypets.ui.BotonAsset("3");
        btnP3.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btnP3.setBackground(Color.WHITE);
        btnP3.addActionListener(e -> {
            paginaActual = 3;
            actualizarTablaPaginada();
        });

        JButton btnSig = new happypets.ui.BotonAsset("Siguiente >");
        btnSig.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btnSig.setBackground(Color.WHITE);
        btnSig.addActionListener(e -> {
            int maxPags = (int) Math.ceil((double) citasFiltradas.size() / tamanoPagina);
            if (paginaActual < maxPags) {
                paginaActual++;
                actualizarTablaPaginada();
            }
        });

        pnlBotonesPag.add(btnAnt);
        pnlBotonesPag.add(btnP1);
        pnlBotonesPag.add(btnP2);
        pnlBotonesPag.add(btnP3);
        pnlBotonesPag.add(btnSig);
        piePaginacion.add(pnlBotonesPag, BorderLayout.EAST);

        card.add(piePaginacion, BorderLayout.SOUTH);
        return card;
    }

    private void filtrarCitas() {
        String q = txtBuscarCita != null ? txtBuscarCita.getText().trim().toLowerCase() : "";
        citasFiltradas.clear();
        for (FilaCitaHoy c : listaCitasHoy) {
            if (q.isEmpty() || c.paciente.toLowerCase().contains(q)
                    || c.propietario.toLowerCase().contains(q)
                    || c.veterinario.toLowerCase().contains(q)
                    || c.motivo.toLowerCase().contains(q)) {
                citasFiltradas.add(c);
            }
        }
        paginaActual = 1;
        actualizarTablaPaginada();
    }

    private void actualizarTablaPaginada() {
        modeloCitas.setRowCount(0);
        int inicio = (paginaActual - 1) * tamanoPagina;
        int fin = Math.min(inicio + tamanoPagina, citasFiltradas.size());

        for (int i = inicio; i < fin; i++) {
            FilaCitaHoy c = citasFiltradas.get(i);
            modeloCitas.addRow(new Object[]{
                    c.hora,
                    c.paciente + " (" + c.especieRaza + ")",
                    c.propietario,
                    c.veterinario,
                    c.motivo,
                    c.estado,
                    "Ver Ficha"
            });
        }

        if (lblTotalCitasHoy != null) {
            lblTotalCitasHoy.setText("  " + citasFiltradas.size() + " citas listadas  ");
        }
    }

    private void mostrarDetalleCita(int fila) {
        int idx = (paginaActual - 1) * tamanoPagina + fila;
        if (idx < 0 || idx >= citasFiltradas.size()) return;
        FilaCitaHoy c = citasFiltradas.get(idx);

        String msg = "FICHA RÁPIDA DE CITA CLÍNICA\n\n"
                + "• Paciente: " + c.paciente + " (" + c.especieRaza + ")\n"
                + "• Propietario Tutor: " + c.propietario + "\n"
                + "• Médico Veterinario: " + c.veterinario + "\n"
                + "• Hora Programada: " + c.hora + "\n"
                + "• Motivo de Consulta: " + c.motivo + "\n"
                + "• Estado de Atención: " + c.estado + "\n\n"
                + "¿Desea iniciar la atención o derivar a triaje?";

        int opt = JOptionPane.showOptionDialog(this, msg, "Detalle de Cita - " + c.paciente,
                JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE,
                null, new String[]{"Atender Paciente", "Cerrar"}, "Atender Paciente");

        if (opt == 0) {
            c.estado = "En Atención";
            actualizarTablaPaginada();
            JOptionPane.showMessageDialog(this,
                    "El estado del paciente " + c.paciente + " ha cambiado a 'En Atención'.",
                    "Paciente en Consulta", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Gráfico nativo con Graphics2D para comparar la tendencia mensual de ingresos
     * (Servicios Clínicos vs Farmacia y Alimentos) en 12 meses.
     */
    private static class GraficoBarrasMensualPanel extends JPanel {
        private static final long serialVersionUID = 1L;
        private final List<MetricaMensualIngreso> metricas;

        GraficoBarrasMensualPanel(List<MetricaMensualIngreso> metricas) {
            this.metricas = metricas;
            setBackground(Color.WHITE);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            int padLeft = 50;
            int padRight = 20;
            int padTop = 20;
            int padBottom = 35;

            int graphW = w - padLeft - padRight;
            int graphH = h - padTop - padBottom;

            if (graphW <= 0 || graphH <= 0) {
                g2.dispose();
                return;
            }

            // Líneas de guía y valores Y (0, 50k, 100k, 150k)
            double maxValor = 160000.0;
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));

            for (int i = 0; i <= 4; i++) {
                double val = i * 40000.0;
                int y = padTop + graphH - (int) ((val / maxValor) * graphH);

                g2.setColor(new Color(241, 245, 249));
                g2.drawLine(padLeft, y, w - padRight, y);

                g2.setColor(new Color(148, 163, 184));
                String tag = String.format("S/%dK", (int)(val / 1000));
                g2.drawString(tag, 10, y + 4);
            }

            if (metricas == null || metricas.isEmpty()) {
                g2.dispose();
                return;
            }

            int n = metricas.size();
            int slotW = graphW / n;
            int barW = Math.max(6, (slotW - 12) / 2);

            Color colorServicios = new Color(15, 76, 129); // Azul oscuro
            Color colorFarmacia = new Color(56, 189, 248);  // Celeste brillante

            for (int i = 0; i < n; i++) {
                MetricaMensualIngreso m = metricas.get(i);
                int slotX = padLeft + i * slotW;

                // Barra 1: Servicios Clínicos
                int h1 = (int) ((m.getServiciosClinicos() / maxValor) * graphH);
                int x1 = slotX + (slotW - 2 * barW - 4) / 2;
                int y1 = padTop + graphH - h1;

                g2.setColor(colorServicios);
                g2.fillRoundRect(x1, y1, barW, h1, 4, 4);

                // Barra 2: Farmacia y Alimentos
                int h2 = (int) ((m.getFarmaciaAlimentos() / maxValor) * graphH);
                int x2 = x1 + barW + 4;
                int y2 = padTop + graphH - h2;

                g2.setColor(colorFarmacia);
                g2.fillRoundRect(x2, y2, barW, h2, 4, 4);

                // Etiqueta del mes
                g2.setColor(new Color(100, 116, 139));
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                String mesTxt = m.getMesEtiqueta() != null ? m.getMesEtiqueta().split(" ")[0] : ("M" + (i + 1));
                int txtW = g2.getFontMetrics().stringWidth(mesTxt);
                g2.drawString(mesTxt, slotX + (slotW - txtW) / 2, h - 12);
            }

            // Eje X base
            g2.setColor(new Color(203, 213, 225));
            g2.drawLine(padLeft, padTop + graphH, w - padRight, padTop + graphH);

            g2.dispose();
        }
    }
}
