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
import happypets.model.InternamientoHospitalario;
import happypets.model.Mascota;
import happypets.ui.Iconos;
import happypets.ui.Ui;

/**
 * Submódulo 4.2: Hospitalización y Cuidados Intensivos.
 * Diseñado según el wireframe oficial (Pág. 4):
 * - Mapa gráfico e interactivo de caniles / boxes de internamiento (Box 01 a Box 09).
 * - Monitor de pacientes críticos y alertas clínicas en tiempo real.
 * - Expediente hospitalario (fluidoterapia, medicamentos, nutrición y evolución diaria).
 * - Control de altas médicas y costos de hospitalización.
 */
public class VistaHospitalizacionPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private static final Color COLOR_BORDE = Ui.BORDE_SUAVE;
    private static final Color COLOR_AZUL_PRIMARIO = Ui.TURQUESA;
    private static final Color COLOR_TEXTO_TITULO = Ui.TEXTO_TITULO;
    private static final Color COLOR_TEXTO_MUTED = Ui.TEXTO_MUTED;

    private final RepositorioVeterinaria repo = RepositorioVeterinaria.getInstancia();

    // KPIs
    private JLabel lblKpiOcupados;
    private JLabel lblKpiCriticos;
    private JLabel lblKpiDisponibles;
    private JLabel lblKpiTasaOcupacion;

    // Mapa de Caniles (9 boxes)
    private final JButton[] botonesBoxes = new JButton[9];
    private final String[] nombresBoxes = {
            "Box 01", "Box 02", "Box 03",
            "Box 04", "Box 05", "Box 06",
            "Box 07", "Box 08", "Box 09"
    };

    // Expediente de Hospitalización
    private JComboBox<PacienteItem> cbPaciente;
    private JTextField txtTutor;
    private JComboBox<String> cbBoxAsignado;
    private JTextField txtDiagnostico;
    private JTextField txtVeterinario;
    private JTextField txtPeso;
    private JTextField txtTemperatura;
    private JTextField txtFC;
    private JComboBox<String> cbNivelAlerta;
    private JTextArea txtFluidoterapia;
    private JTextArea txtMedicacion;
    private JTextArea txtEvolucion;
    private JTextField txtCostoDia;

    // Tabla de Hospitalizados
    private JTable tablaHospitalizados;
    private DefaultTableModel modeloHospitalizados;
    private JLabel lblContadorHosp;
    private List<InternamientoHospitalario> listaActual;

    public VistaHospitalizacionPanel() {
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

        // 3. Fila Superior: Mapa de Ocupación de Boxes y Monitor de Alertas
        contenido.add(crearFilaMapaYAlertas());
        contenido.add(Box.createVerticalStrut(12));

        // 4. Fila Inferior: Expediente de Hospitalización y Tabla de Pacientes
        contenido.add(crearFilaExpedienteYTabla());

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

        JLabel titulo = new JLabel("Hospitalización y Cuidados Críticos");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 17));
        titulo.setForeground(COLOR_TEXTO_TITULO);

        JLabel subtitulo = new JLabel("Módulo 4.2 · Mapa de caniles climatizados, fluidoterapia, monitoreo 24/7 y hoja de evolución");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitulo.setForeground(COLOR_TEXTO_MUTED);

        izq.add(titulo);
        izq.add(Box.createVerticalStrut(2));
        izq.add(subtitulo);
        cab.add(izq, BorderLayout.CENTER);

        JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        der.setOpaque(false);

        JButton btnRefrescar = crearBotonWeb("↻ Actualizar Estado", false, () -> recargarDatos());
        der.add(btnRefrescar);

        cab.add(der, BorderLayout.EAST);
        return cab;
    }

    private JPanel crearFilaKpis() {
        JPanel fila = new JPanel(new GridLayout(1, 4, 12, 0));
        fila.setOpaque(false);

        lblKpiOcupados = new JLabel("3 / 9", SwingConstants.LEFT);
        lblKpiCriticos = new JLabel("1", SwingConstants.LEFT);
        lblKpiDisponibles = new JLabel("6", SwingConstants.LEFT);
        lblKpiTasaOcupacion = new JLabel("33%", SwingConstants.LEFT);

        fila.add(crearCardKpi(lblKpiOcupados, "CANÍLES OCUPADOS", Ui.TURQUESA_SUAVE, COLOR_AZUL_PRIMARIO, Iconos.crearIconoCamaHospital(20, COLOR_AZUL_PRIMARIO)));
        fila.add(crearCardKpi(lblKpiCriticos, "PACIENTES EN ESTADO CRÍTICO", new Color(254, 226, 226), new Color(220, 38, 38), Iconos.crearIconoAlertaTriaje(20, new Color(220, 38, 38))));
        fila.add(crearCardKpi(lblKpiDisponibles, "CANÍLES DISPONIBLES", new Color(220, 252, 231), new Color(22, 163, 74), Iconos.crearIconoCheck(20, new Color(22, 163, 74))));
        fila.add(crearCardKpi(lblKpiTasaOcupacion, "OCUPACIÓN CLÍNICA", new Color(243, 232, 255), new Color(147, 51, 234), Iconos.crearIconoReportes(20, new Color(147, 51, 234))));

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

    private JPanel crearFilaMapaYAlertas() {
        JPanel fila = new JPanel(new BorderLayout(14, 0));
        fila.setOpaque(false);

        // Mapa de Caniles (3x3 boxes)
        fila.add(crearCardMapaCaniles(), BorderLayout.CENTER);

        // Panel de Alertas y Pacientes Críticos
        fila.add(crearCardAlertasCriticas(), BorderLayout.EAST);

        return fila;
    }

    private JPanel crearCardMapaCaniles() {
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

        JLabel tit = new JLabel("Mapa de Ocupación de Boxes Clínicos (Cunas / Cajas / Caniles)");
        tit.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tit.setForeground(COLOR_TEXTO_TITULO);
        top.add(tit, BorderLayout.WEST);

        JLabel lblLeyenda = new JLabel(" Verde: Libre  |  Azul: Ocupado  |  Rojo: Crítico/UCI  |  Amarillo: Aislamiento ");
        lblLeyenda.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblLeyenda.setForeground(COLOR_TEXTO_MUTED);
        top.add(lblLeyenda, BorderLayout.EAST);

        card.add(top, BorderLayout.NORTH);

        JPanel grilla = new JPanel(new GridLayout(3, 3, 8, 8));
        grilla.setOpaque(false);

        for (int i = 0; i < 9; i++) {
            final int idx = i;
            String boxName = nombresBoxes[i];
            JButton btnBox = new JButton() {
                private static final long serialVersionUID = 1L;
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    Color bg = (Color) getClientProperty("bgColor");
                    if (bg == null) bg = new Color(241, 245, 249);
                    g2.setColor(bg);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                    Color border = (Color) getClientProperty("borderColor");
                    if (border != null) {
                        g2.setColor(border);
                        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                    }
                    super.paintComponent(g2);
                    g2.dispose();
                }
            };
            btnBox.setFont(new Font("Segoe UI", Font.BOLD, 11));
            btnBox.setFocusPainted(false);
            btnBox.setContentAreaFilled(false);
            btnBox.setOpaque(false);
            btnBox.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btnBox.setPreferredSize(new Dimension(0, 48));
            btnBox.addActionListener(e -> seleccionarBoxDesdeMapa(boxName));

            botonesBoxes[i] = btnBox;
            grilla.add(btnBox);
        }

        card.add(grilla, BorderLayout.CENTER);
        return card;
    }

    private JPanel crearCardAlertasCriticas() {
        JPanel card = new JPanel(new BorderLayout(0, 6)) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(254, 242, 242));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(new Color(254, 202, 202));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(320, 0));
        card.setBorder(new EmptyBorder(12, 14, 12, 14));

        JLabel tit = new JLabel("🚨 Alertas / Pacientes Críticos (UCI)");
        tit.setFont(new Font("Segoe UI", Font.BOLD, 12));
        tit.setForeground(new Color(185, 28, 28));
        card.add(tit, BorderLayout.NORTH);

        JTextArea txtAlertas = new JTextArea();
        txtAlertas.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        txtAlertas.setEditable(false);
        txtAlertas.setOpaque(false);
        txtAlertas.setLineWrap(true);
        txtAlertas.setWrapStyleWord(true);
        txtAlertas.setText(
                "• Box 01 [UCI]: Zeus (Rottweiler) - Toxicosis por rodenticida. Monitorear diuresis y reflejos cada 2 horas.\n\n" +
                "• Box 05 [Aislamiento]: Bimba (Persa) - Gastroenteritis infecciosa. Verificar bomba de infusión a 15 ml/h.\n\n" +
                "• Box 02 [General]: Toby (Pug) - Alta postquirúrgica programada para mañana 09:00 hrs."
        );
        card.add(txtAlertas, BorderLayout.CENTER);

        return card;
    }

    private JPanel crearFilaExpedienteYTabla() {
        JPanel fila = new JPanel(new GridLayout(1, 2, 14, 0));
        fila.setOpaque(false);

        fila.add(crearCardExpedienteHospitalario());
        fila.add(crearCardTablaHospitalizados());

        return fila;
    }

    private JPanel crearCardExpedienteHospitalario() {
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
        card.setBorder(new EmptyBorder(12, 14, 12, 14));

        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));

        JLabel tit = new JLabel("Expediente Clínico de Hospitalización");
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
        form.add(crearFilaCampo("Paciente Internado *", cbPaciente));
        form.add(Box.createVerticalStrut(4));

        txtTutor = crearCampoTexto(false);
        form.add(crearFilaCampo("Propietario / Contacto de Emergencia", txtTutor));
        form.add(Box.createVerticalStrut(4));

        JPanel filaBox = new JPanel(new GridLayout(1, 2, 8, 0));
        filaBox.setOpaque(false);
        filaBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        filaBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        cbBoxAsignado = new JComboBox<>(nombresBoxes);
        cbBoxAsignado.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        cbBoxAsignado.setBackground(Color.WHITE);

        cbNivelAlerta = new JComboBox<>(new String[]{"ESTABLE", "OBSERVACIÓN", "CRÍTICO"});
        cbNivelAlerta.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        cbNivelAlerta.setBackground(Color.WHITE);

        filaBox.add(crearFilaCampo("Box Asignado *", cbBoxAsignado));
        filaBox.add(crearFilaCampo("Nivel de Alerta Clínica *", cbNivelAlerta));
        form.add(filaBox);
        form.add(Box.createVerticalStrut(4));

        txtDiagnostico = crearCampoTexto(true);
        txtDiagnostico.setText("Intoxicación aguda / Gastroenteritis / Postquirúrgico");
        txtVeterinario = crearCampoTexto(true);
        txtVeterinario.setText("Dr. Roberto Mendoza");
        form.add(crearFilaCampo("Diagnóstico de Ingreso *", txtDiagnostico));
        form.add(Box.createVerticalStrut(4));
        form.add(crearFilaCampo("Veterinario Tratante", txtVeterinario));
        form.add(Box.createVerticalStrut(4));

        JPanel filaConst = new JPanel(new GridLayout(1, 3, 6, 0));
        filaConst.setOpaque(false);
        filaConst.setAlignmentX(Component.LEFT_ALIGNMENT);
        filaConst.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        txtPeso = crearCampoTexto(true);
        txtPeso.setText("28.4");
        txtTemperatura = crearCampoTexto(true);
        txtTemperatura.setText("38.5");
        txtFC = crearCampoTexto(true);
        txtFC.setText("110");
        filaConst.add(crearFilaCampo("Peso (kg)", txtPeso));
        filaConst.add(crearFilaCampo("Temp (°C)", txtTemperatura));
        filaConst.add(crearFilaCampo("FC (lpm)", txtFC));
        form.add(filaConst);
        form.add(Box.createVerticalStrut(4));

        txtFluidoterapia = new JTextArea(2, 20);
        txtFluidoterapia.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        txtFluidoterapia.setLineWrap(true);
        txtFluidoterapia.setWrapStyleWord(true);
        txtFluidoterapia.setText("Ringer Lactato IV a 45 ml/h continuo en bomba de infusión.");
        txtFluidoterapia.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDE, 1),
                new EmptyBorder(3, 6, 3, 6)
        ));
        form.add(crearFilaArea("Fluidoterapia / Vía de Infusión", txtFluidoterapia, 38));
        form.add(Box.createVerticalStrut(4));

        txtMedicacion = new JTextArea(2, 20);
        txtMedicacion.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        txtMedicacion.setLineWrap(true);
        txtMedicacion.setWrapStyleWord(true);
        txtMedicacion.setText("Cefazolina 25mg/kg c/8h, Tramadol 2mg/kg c/8h, Omeprazol 1mg/kg c/24h.");
        txtMedicacion.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDE, 1),
                new EmptyBorder(3, 6, 3, 6)
        ));
        form.add(crearFilaArea("Medicamentos Intrahospitalarios Activos", txtMedicacion, 38));
        form.add(Box.createVerticalStrut(4));

        txtEvolucion = new JTextArea(2, 20);
        txtEvolucion.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        txtEvolucion.setLineWrap(true);
        txtEvolucion.setWrapStyleWord(true);
        txtEvolucion.setText("Paciente estable. Apetito moderado en dieta húmeda blanda. Micción positiva.");
        txtEvolucion.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDE, 1),
                new EmptyBorder(3, 6, 3, 6)
        ));
        form.add(crearFilaArea("Alimentación y Evolución Diaria (Enfermería)", txtEvolucion, 38));
        form.add(Box.createVerticalStrut(4));

        txtCostoDia = crearCampoTexto(true);
        txtCostoDia.setText("110.00");
        form.add(crearFilaCampo("Tarifa Diaria de Hospitalización (S/.)", txtCostoDia));
        form.add(Box.createVerticalStrut(8));

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnRow.setOpaque(false);
        btnRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        JButton btnIngresar = crearBotonWeb("🏥 Registrar Ingreso", true, () -> ingresarPacienteHospitalario());
        btnRow.add(btnIngresar);
        form.add(btnRow);

        card.add(form, BorderLayout.NORTH);
        return card;
    }

    private JPanel crearCardTablaHospitalizados() {
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

        JLabel tit = new JLabel("Pacientes Internados en Sala de Hospitalización");
        tit.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tit.setForeground(COLOR_TEXTO_TITULO);
        top.add(tit, BorderLayout.WEST);

        lblContadorHosp = new JLabel("Cargando...");
        lblContadorHosp.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblContadorHosp.setForeground(COLOR_TEXTO_MUTED);
        top.add(lblContadorHosp, BorderLayout.EAST);

        card.add(top, BorderLayout.NORTH);

        String[] columnas = {"Box", "Paciente", "Tutor", "Diagnóstico", "Ingreso", "Alerta", "Estado"};
        modeloHospitalizados = new DefaultTableModel(columnas, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        tablaHospitalizados = new JTable(modeloHospitalizados);
        Ui.formatearTabla(tablaHospitalizados, new int[]{0, 4, 5, 6}, new int[]{});

        tablaHospitalizados.getColumnModel().getColumn(0).setPreferredWidth(60);
        tablaHospitalizados.getColumnModel().getColumn(1).setPreferredWidth(85);
        tablaHospitalizados.getColumnModel().getColumn(3).setPreferredWidth(180);
        tablaHospitalizados.getColumnModel().getColumn(5).setCellRenderer(new BadgeAlertaHospRenderer());

        tablaHospitalizados.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                cargarExpedienteSeleccionado();
            }
        });

        JScrollPane sp = new JScrollPane(tablaHospitalizados);
        sp.setBorder(BorderFactory.createLineBorder(COLOR_BORDE, 1));
        sp.setPreferredSize(new Dimension(500, 360));
        card.add(sp, BorderLayout.CENTER);

        JPanel bot = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        bot.setOpaque(false);

        JButton btnAlta = crearBotonWeb("✓ Dar Alta Médica", false, () -> darAltaMedicaSeleccionada());
        JButton btnEpicrisis = crearBotonWeb("📄 Resumen de Epicrisis", false, () -> emitirEpicrisisHospitalaria());

        bot.add(btnAlta);
        bot.add(btnEpicrisis);
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

        listaActual = repo.getInternamientos();
        modeloHospitalizados.setRowCount(0);

        // Actualizar visual del mapa de boxes
        for (int i = 0; i < 9; i++) {
            String bName = nombresBoxes[i];
            Optional<InternamientoHospitalario> optH = listaActual.stream()
                    .filter(h -> h.getNumeroBox().equalsIgnoreCase(bName) && !"Alta Médica".equalsIgnoreCase(h.getEstado()))
                    .findFirst();

            JButton btn = botonesBoxes[i];
            if (optH.isPresent()) {
                InternamientoHospitalario h = optH.get();
                btn.setText("<html><center><b>" + bName + "</b><br><font size='2'>" + h.getNombreMascota() + " (" + h.getNivelAlerta() + ")</font></center></html>");
                if ("CRÍTICO".equalsIgnoreCase(h.getNivelAlerta())) {
                    btn.putClientProperty("bgColor", new Color(254, 226, 226));
                    btn.putClientProperty("borderColor", new Color(239, 68, 68));
                    btn.setForeground(new Color(185, 28, 28));
                } else if (h.getTipoBox().contains("Aislamiento")) {
                    btn.putClientProperty("bgColor", new Color(254, 243, 199));
                    btn.putClientProperty("borderColor", new Color(245, 158, 11));
                    btn.setForeground(new Color(180, 83, 9));
                } else {
                    btn.putClientProperty("bgColor", new Color(224, 242, 254));
                    btn.putClientProperty("borderColor", new Color(2, 132, 199));
                    btn.setForeground(COLOR_AZUL_PRIMARIO);
                }
            } else {
                btn.setText("<html><center><b>" + bName + "</b><br><font size='2' color='#16a34a'>[LIBRE]</font></center></html>");
                btn.putClientProperty("bgColor", new Color(240, 253, 244));
                btn.putClientProperty("borderColor", new Color(187, 247, 208));
                btn.setForeground(new Color(22, 163, 74));
            }
            btn.repaint();
        }

        long activos = 0;
        long criticos = 0;
        for (InternamientoHospitalario h : listaActual) {
            if (!"Alta Médica".equalsIgnoreCase(h.getEstado())) {
                activos++;
                if ("CRÍTICO".equalsIgnoreCase(h.getNivelAlerta())) criticos++;
            }
            modeloHospitalizados.addRow(new Object[]{
                    h.getNumeroBox(),
                    h.getNombreMascota(),
                    h.getNombreTutor(),
                    h.getDiagnosticoIngreso(),
                    h.getFechaIngresoFormateada(),
                    h.getNivelAlerta(),
                    h.getEstado()
            });
        }

        lblContadorHosp.setText(activos + " pacientes en internamiento");
        lblKpiOcupados.setText(activos + " / 9");
        lblKpiCriticos.setText(String.valueOf(criticos));
        lblKpiDisponibles.setText(String.valueOf(9 - activos));
        int porcentaje = (int) Math.round((activos / 9.0) * 100);
        lblKpiTasaOcupacion.setText(porcentaje + "%");
    }

    private void autocompletarTutor() {
        PacienteItem item = (PacienteItem) cbPaciente.getSelectedItem();
        if (item != null && item.mascota != null) {
            repo.getClienteDeMascota(item.mascota.getCodigo()).ifPresent(c -> {
                txtTutor.setText(c.getNombreCompleto() + " (" + c.getTelefonoPrincipal() + ")");
                txtPeso.setText(String.valueOf(item.mascota.getPesoActualKg()));
            });
        }
    }

    private void seleccionarBoxDesdeMapa(String boxName) {
        cbBoxAsignado.setSelectedItem(boxName);
        Optional<InternamientoHospitalario> opt = listaActual.stream()
                .filter(h -> h.getNumeroBox().equalsIgnoreCase(boxName) && !"Alta Médica".equalsIgnoreCase(h.getEstado()))
                .findFirst();

        if (opt.isPresent()) {
            InternamientoHospitalario h = opt.get();
            txtDiagnostico.setText(h.getDiagnosticoIngreso());
            txtVeterinario.setText(h.getVeterinarioTratante());
            txtFluidoterapia.setText(h.getFluidoterapia());
            txtMedicacion.setText(h.getMedicacionActual());
            txtEvolucion.setText(h.getEvolucionNotas());
            cbNivelAlerta.setSelectedItem(h.getNivelAlerta());
            txtCostoDia.setText(String.format("%.2f", h.getCostoDia()));
        } else {
            txtDiagnostico.setText("");
            txtFluidoterapia.setText("Ringer Lactato IV a 40 ml/h");
            txtMedicacion.setText("Analgesia y antibiótico según indicación médica");
            txtEvolucion.setText("Ingreso reciente. Paciente en adaptación.");
        }
    }

    private void ingresarPacienteHospitalario() {
        PacienteItem pi = (PacienteItem) cbPaciente.getSelectedItem();
        if (pi == null || pi.mascota == null) return;
        Mascota m = pi.mascota;
        Optional<Cliente> optC = repo.getClienteDeMascota(m.getCodigo());

        double costo = 100.0;
        try { costo = Double.parseDouble(txtCostoDia.getText().trim()); } catch (Exception ignored) {}

        String boxSel = (String) cbBoxAsignado.getSelectedItem();
        String tipoBox = boxSel.contains("01") ? "UCI / Cuidados Intensivos" : boxSel.contains("05") ? "Aislamiento Infeccioso" : "Hospitalización General";

        InternamientoHospitalario h = new InternamientoHospitalario(
                null,
                boxSel,
                tipoBox,
                m.getCodigo(),
                m.getNombre(),
                m.getEspecie() + " · " + m.getRaza(),
                optC.map(Cliente::getNombreCompleto).orElse("Tutor"),
                optC.map(Cliente::getTelefonoPrincipal).orElse(""),
                txtDiagnostico.getText().trim(),
                txtVeterinario.getText().trim(),
                LocalDate.now(),
                LocalTime.now(),
                LocalDate.now().plusDays(2),
                m.getPesoActualKg(),
                38.5,
                110,
                txtFluidoterapia.getText().trim(),
                txtMedicacion.getText().trim(),
                txtEvolucion.getText().trim(),
                (String) cbNivelAlerta.getSelectedItem(),
                costo,
                "Internado / En Tratamiento"
        );

        repo.guardarInternamiento(h);
        JOptionPane.showMessageDialog(this,
                "Paciente " + m.getNombre() + " ingresado satisfactoriamente al " + boxSel + " (" + tipoBox + ").",
                "Ingreso Hospitalario Exitoso", JOptionPane.INFORMATION_MESSAGE);

        recargarDatos();
    }

    private void cargarExpedienteSeleccionado() {
        int row = tablaHospitalizados.getSelectedRow();
        if (row < 0 || row >= listaActual.size()) return;
        InternamientoHospitalario h = listaActual.get(row);

        cbBoxAsignado.setSelectedItem(h.getNumeroBox());
        txtTutor.setText(h.getNombreTutor() + " (" + h.getTelefonoTutor() + ")");
        txtDiagnostico.setText(h.getDiagnosticoIngreso());
        txtVeterinario.setText(h.getVeterinarioTratante());
        txtPeso.setText(String.valueOf(h.getPesoActualKg()));
        txtTemperatura.setText(String.valueOf(h.getTemperaturaC()));
        txtFC.setText(String.valueOf(h.getFrecuenciaCardiaca()));
        cbNivelAlerta.setSelectedItem(h.getNivelAlerta());
        txtFluidoterapia.setText(h.getFluidoterapia());
        txtMedicacion.setText(h.getMedicacionActual());
        txtEvolucion.setText(h.getEvolucionNotas());
        txtCostoDia.setText(String.format("%.2f", h.getCostoDia()));
    }

    private void darAltaMedicaSeleccionada() {
        int row = tablaHospitalizados.getSelectedRow();
        if (row < 0 || row >= listaActual.size()) {
            JOptionPane.showMessageDialog(this, "Seleccione un paciente hospitalizado para dar de alta.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        InternamientoHospitalario h = listaActual.get(row);
        h.setEstado("Alta Médica");
        recargarDatos();

        JOptionPane.showMessageDialog(this,
                "Se ha concedido el Alta Médica para " + h.getNombreMascota() + ".\n" +
                "El canil " + h.getNumeroBox() + " ha quedado libre para sanitización y nuevo uso.",
                "Alta Médica Hospitalaria", JOptionPane.INFORMATION_MESSAGE);
    }

    private void emitirEpicrisisHospitalaria() {
        int row = tablaHospitalizados.getSelectedRow();
        if (row < 0 || row >= listaActual.size()) {
            JOptionPane.showMessageDialog(this, "Seleccione un paciente para emitir epicrisis.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        InternamientoHospitalario h = listaActual.get(row);

        String[][] datos = new String[][]{
            {"1", "Ingreso y Diagnóstico", h.getDiagnosticoIngreso(), "Fecha: " + h.getFechaIngresoFormateada() + " " + h.getHoraIngresoFormateada(), "Ingresado"},
            {"2", "Terapia y Medicación", h.getFluidoterapia() + " | " + h.getMedicacionActual(), "Veterinario: " + h.getVeterinarioTratante(), h.getNivelAlerta()},
            {"3", "Evolución y Alta", h.getEvolucionNotas(), "Canil Box: " + h.getNumeroBox(), h.getEstado()}
        };

        Ui.mostrarVisorReporte(
            (java.awt.Frame) javax.swing.SwingUtilities.getWindowAncestor(this),
            "EPICRISIS Y REPORTE DE HOSPITALIZACIÓN",
            "Paciente: " + h.getNombreMascota() + " (" + h.getEspecieRaza() + ") | Tutor: " + h.getNombreTutor() + " | Tel: " + h.getTelefonoTutor(),
            "Código Internamiento: " + h.getIdInternamiento() + " | Box: " + h.getNumeroBox() + " | Estado: " + h.getEstado(),
            new String[]{"Fase", "Etapa Clínica", "Protocolo / Medicación", "Responsable / Ubicación", "Estado"},
            datos,
            "UNIDAD DE CUIDADOS INTENSIVOS Y HOSPITALIZACIÓN · HAPPY PETS",
            "Epicrisis_" + h.getIdInternamiento()
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

    private static class BadgeAlertaHospRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            lbl.setHorizontalAlignment(SwingConstants.CENTER);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
            String al = String.valueOf(value);
            if (!isSelected) {
                if ("CRÍTICO".equalsIgnoreCase(al)) {
                    lbl.setForeground(new Color(220, 38, 38));
                } else if ("OBSERVACIÓN".equalsIgnoreCase(al)) {
                    lbl.setForeground(new Color(217, 119, 6));
                } else {
                    lbl.setForeground(new Color(22, 163, 74));
                }
            }
            return lbl;
        }
    }
}
