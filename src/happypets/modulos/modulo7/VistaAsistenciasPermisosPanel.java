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
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
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
import happypets.model.RegistroAsistencia;
import happypets.model.SolicitudPermiso;
import happypets.ui.Iconos;
import happypets.ui.Ui;

/**
 * Submódulo 7.4: Asistencias y Permisos - Pase de lista diario por turnos.
 * Diseñado según wireframe oficial (modulos/modulo-7/WhatsApp Image 2026-09-24 at 10.39-pdfa-3u.pdf).
 */
public class VistaAsistenciasPermisosPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private static final Color COLOR_FONDO = new Color(248, 250, 252);
    private static final Color COLOR_CARD = Color.WHITE;
    private static final Color COLOR_BORDE = new Color(226, 232, 240);
    private static final Color COLOR_TEXTO_TITULO = new Color(15, 23, 42);
    private static final Color COLOR_TEXTO_MUTED = new Color(100, 116, 139);
    private static final Color COLOR_AZUL = new Color(2, 132, 199);
    private static final Color COLOR_VERDE = new Color(16, 185, 129);
    private static final Color COLOR_AMBAR = new Color(245, 158, 11);
    private static final Color COLOR_ROJO = new Color(239, 68, 68);
    private static final Color COLOR_MORADO = new Color(126, 34, 206);

    private final RepositorioVeterinaria repo = RepositorioVeterinaria.getInstancia();

    // KPIs Superiores
    private JLabel lblKpiPresentes;
    private JLabel lblKpiRetardos;
    private JLabel lblKpiAusentes;
    private JLabel lblKpiPermisos;

    // Filtros
    private String turnoFiltroPildora = "Todos";
    private JPanel panelPildorasTurnos;
    private JTextField txtBuscar;
    private JComboBox<String> cmbEstado;
    private JComboBox<String> cmbCategoria;

    // Tabla de Asistencias
    private JTable tablaAsistencias;
    private DefaultTableModel modeloTabla;
    private List<RegistroAsistencia> listaAsistenciasFiltradas;

    // Panel Lateral Derecho
    private JLabel lblSelNombre;
    private JLabel lblSelCargo;
    private JLabel lblSelEstadoBadge;
    private JLabel lblSelTurno;
    private JLabel lblSelEntrada;
    private JLabel lblSelSalida;
    private JLabel lblSelNotaAlerta;
    private RegistroAsistencia registroSeleccionado;
    private JPanel panelPermisosHoyLista;

    public VistaAsistenciasPermisosPanel() {
        setLayout(new BorderLayout());
        setBackground(COLOR_FONDO);
        setBorder(new EmptyBorder(18, 22, 18, 22));

        // 1. Cabecera (Título, fecha, botones)
        add(crearCabecera(), BorderLayout.NORTH);

        // 2. Centro: Fila de KPIs + Filtros + Split de Tabla y Panel Lateral
        JPanel panelCentro = new JPanel(new BorderLayout(0, 12));
        panelCentro.setOpaque(false);

        JPanel panelSuperior = new JPanel(new BorderLayout(0, 12));
        panelSuperior.setOpaque(false);
        panelSuperior.add(crearFilaKpis(), BorderLayout.NORTH);
        panelSuperior.add(crearBarraFiltros(), BorderLayout.SOUTH);
        panelCentro.add(panelSuperior, BorderLayout.NORTH);

        // Cuerpo: Tabla a la izquierda (65%) y Panel Lateral a la derecha (35%)
        JPanel panelCuerpo = new JPanel(new BorderLayout(16, 0));
        panelCuerpo.setOpaque(false);
        panelCuerpo.add(crearPanelTabla(), BorderLayout.CENTER);
        panelCuerpo.add(crearPanelLateralControl(), BorderLayout.EAST);

        panelCentro.add(panelCuerpo, BorderLayout.CENTER);
        add(panelCentro, BorderLayout.CENTER);

        recargarDatos();
    }

    private JPanel crearCabecera() {
        JPanel cab = new JPanel(new BorderLayout(16, 8));
        cab.setOpaque(false);
        cab.setBorder(new EmptyBorder(0, 0, 12, 0));

        JPanel textos = new JPanel();
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        textos.setOpaque(false);

        JLabel lblBreadcrumb = new JLabel("Control de Personal · Pase de Lista Diario por Turnos");
        lblBreadcrumb.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblBreadcrumb.setForeground(COLOR_MORADO);

        JLabel lblTitulo = new JLabel("Asistencias y Permisos");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(COLOR_TEXTO_TITULO);

        LocalDate hoy = LocalDate.now();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM 'de' yyyy", new Locale("es", "ES"));
        String fechaStr = hoy.format(dtf);
        fechaStr = fechaStr.substring(0, 1).toUpperCase() + fechaStr.substring(1);

        JLabel lblSub = new JLabel(fechaStr + " · 24 Colaboradores Programados Hoy");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(COLOR_TEXTO_MUTED);

        textos.add(lblBreadcrumb);
        textos.add(Box.createVerticalStrut(2));
        textos.add(lblTitulo);
        textos.add(Box.createVerticalStrut(2));
        textos.add(lblSub);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelBotones.setOpaque(false);

        JButton btnExportar = Ui.botonSecundario("Exportar Reporte", Iconos.crearIconoDocumento(14, COLOR_TEXTO_MUTED));
        btnExportar.addActionListener(e -> JOptionPane.showMessageDialog(this, "Exportando reporte de asistencias del día en formato Excel / PDF.", "Reporte Diario", JOptionPane.INFORMATION_MESSAGE));

        JButton btnPaseLista = Ui.botonPrimario("+ Pase de Lista Rápido", Iconos.crearIconoAsistencia(16, Color.WHITE));
        btnPaseLista.setBackground(COLOR_MORADO);
        btnPaseLista.addActionListener(e -> mostrarDialogoPaseListaRapido());

        panelBotones.add(btnExportar);
        panelBotones.add(btnPaseLista);

        cab.add(textos, BorderLayout.CENTER);
        cab.add(panelBotones, BorderLayout.EAST);
        return cab;
    }

    private JPanel crearFilaKpis() {
        JPanel fila = new JPanel(new GridLayout(1, 4, 14, 0));
        fila.setOpaque(false);

        lblKpiPresentes = new JLabel("18");
        lblKpiRetardos = new JLabel("3");
        lblKpiAusentes = new JLabel("2");
        lblKpiPermisos = new JLabel("1");

        fila.add(crearTarjetaKpi("Presentes Hoy", lblKpiPresentes, "En servicio puntual", COLOR_VERDE, new Color(240, 253, 244)));
        fila.add(crearTarjetaKpi("Retardos", lblKpiRetardos, "+10 min tolerancia", COLOR_AMBAR, new Color(254, 252, 232)));
        fila.add(crearTarjetaKpi("Ausentes", lblKpiAusentes, "Inasistencias registradas", COLOR_ROJO, new Color(254, 242, 242)));
        fila.add(crearTarjetaKpi("Permisos Hoy", lblKpiPermisos, "Licencias y pases médicos", COLOR_MORADO, new Color(250, 245, 255)));

        return fila;
    }

    private JPanel crearTarjetaKpi(String titulo, JLabel lblValor, String subtitulo, Color colorAcento, Color fondo) {
        JPanel card = new JPanel(new BorderLayout(10, 4));
        card.setBackground(COLOR_CARD);
        card.setBorder(new CompoundBorder(
                new LineBorder(COLOR_BORDE, 1, true),
                new EmptyBorder(12, 14, 12, 14)
        ));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JLabel lblTit = new JLabel(titulo);
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTit.setForeground(COLOR_TEXTO_MUTED);

        JLabel badgeIcono = new JLabel(" ● ");
        badgeIcono.setFont(new Font("Segoe UI", Font.BOLD, 14));
        badgeIcono.setForeground(colorAcento);

        top.add(lblTit, BorderLayout.WEST);
        top.add(badgeIcono, BorderLayout.EAST);

        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblValor.setForeground(COLOR_TEXTO_TITULO);

        JLabel lblSub = new JLabel(subtitulo);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblSub.setForeground(COLOR_TEXTO_MUTED);

        card.add(top, BorderLayout.NORTH);
        card.add(lblValor, BorderLayout.CENTER);
        card.add(lblSub, BorderLayout.SOUTH);
        return card;
    }

    private JPanel crearBarraFiltros() {
        JPanel contenedor = new JPanel(new BorderLayout(12, 8));
        contenedor.setOpaque(false);

        // Fila de píldoras de Turnos
        panelPildorasTurnos = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        panelPildorasTurnos.setOpaque(false);

        // Fila de Búsqueda y Dropdowns
        JPanel panelBusqueda = new JPanel(new BorderLayout(12, 8));
        panelBusqueda.setBackground(COLOR_CARD);
        panelBusqueda.setBorder(new CompoundBorder(
                new LineBorder(COLOR_BORDE, 1, true),
                new EmptyBorder(10, 14, 10, 14)
        ));

        JPanel filaIzq = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filaIzq.setOpaque(false);

        JLabel lblLupa = new JLabel(Iconos.crearIconoBuscar(16, COLOR_TEXTO_MUTED));
        txtBuscar = new JTextField(18);
        txtBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtBuscar.setToolTipText("Buscar colaborador por nombre o rol");
        txtBuscar.addActionListener(e -> recargarDatos());

        cmbEstado = new JComboBox<>(new String[]{
                "Todos los estados", "Presente", "Retardo", "Ausente", "Permiso", "Pendiente"
        });
        cmbEstado.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cmbEstado.addActionListener(e -> recargarDatos());

        cmbCategoria = new JComboBox<>(new String[]{
                "Todas las áreas", "Veterinarios", "Personal de Apoyo"
        });
        cmbCategoria.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cmbCategoria.addActionListener(e -> recargarDatos());

        JButton btnFiltrar = Ui.botonPrimario("Filtrar", Iconos.crearIconoFiltro(14, Color.WHITE));
        btnFiltrar.setBackground(COLOR_MORADO);
        btnFiltrar.addActionListener(e -> recargarDatos());

        JButton btnLimpiar = Ui.botonSecundario("Limpiar", null);
        btnLimpiar.addActionListener(e -> {
            txtBuscar.setText("");
            turnoFiltroPildora = "Todos";
            cmbEstado.setSelectedIndex(0);
            cmbCategoria.setSelectedIndex(0);
            recargarDatos();
        });

        filaIzq.add(lblLupa);
        filaIzq.add(txtBuscar);
        filaIzq.add(new JLabel("Estado:"));
        filaIzq.add(cmbEstado);
        filaIzq.add(new JLabel("Rol:"));
        filaIzq.add(cmbCategoria);
        filaIzq.add(btnFiltrar);
        filaIzq.add(btnLimpiar);

        panelBusqueda.add(filaIzq, BorderLayout.CENTER);

        contenedor.add(panelPildorasTurnos, BorderLayout.NORTH);
        contenedor.add(panelBusqueda, BorderLayout.SOUTH);
        return contenedor;
    }

    private void actualizarPildorasTurnos(List<RegistroAsistencia> lista) {
        panelPildorasTurnos.removeAll();

        long cTotal = lista.size();
        long cMan = lista.stream().filter(a -> a.getTurno().toLowerCase().contains("mañana")).count();
        long cTar = lista.stream().filter(a -> a.getTurno().toLowerCase().contains("tarde")).count();
        long cNoc = lista.stream().filter(a -> a.getTurno().toLowerCase().contains("noche")).count();

        panelPildorasTurnos.add(crearBotonPildoraTurno("Todos", "Todos · " + cTotal));
        panelPildorasTurnos.add(crearBotonPildoraTurno("Mañana", "Mañana · " + cMan));
        panelPildorasTurnos.add(crearBotonPildoraTurno("Tarde", "Tarde · " + cTar));
        panelPildorasTurnos.add(crearBotonPildoraTurno("Noche", "Noche · " + cNoc));

        panelPildorasTurnos.revalidate();
        panelPildorasTurnos.repaint();
    }

    private JButton crearBotonPildoraTurno(String turnoClave, String texto) {
        boolean activo = turnoFiltroPildora.equalsIgnoreCase(turnoClave);
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        if (activo) {
            btn.setBackground(COLOR_MORADO);
            btn.setForeground(Color.WHITE);
            btn.setBorder(new CompoundBorder(
                    new LineBorder(COLOR_MORADO.darker(), 1, true),
                    new EmptyBorder(5, 14, 5, 14)
            ));
        } else {
            btn.setBackground(Color.WHITE);
            btn.setForeground(COLOR_TEXTO_TITULO);
            btn.setBorder(new CompoundBorder(
                    new LineBorder(COLOR_BORDE, 1, true),
                    new EmptyBorder(5, 14, 5, 14)
            ));
        }

        btn.addActionListener(e -> {
            turnoFiltroPildora = turnoClave;
            recargarDatos();
        });
        return btn;
    }

    private JPanel crearPanelTabla() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_CARD);
        panel.setBorder(new CompoundBorder(
                new LineBorder(COLOR_BORDE, 1, true),
                new EmptyBorder(4, 4, 4, 4)
        ));

        String[] cols = {
                "Colaborador / Rol", "Turno", "Entrada", "Salida", "Estado", "Permiso / Nota", "Acción"
        };

        modeloTabla = new DefaultTableModel(cols, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        tablaAsistencias = new JTable(modeloTabla);
        tablaAsistencias.setRowHeight(48);
        tablaAsistencias.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaAsistencias.setShowGrid(true);
        tablaAsistencias.setGridColor(new Color(241, 245, 249));
        tablaAsistencias.setSelectionBackground(new Color(243, 232, 255));
        tablaAsistencias.setSelectionForeground(COLOR_TEXTO_TITULO);

        JTableHeader th = tablaAsistencias.getTableHeader();
        th.setFont(new Font("Segoe UI", Font.BOLD, 12));
        th.setBackground(new Color(248, 250, 252));
        th.setForeground(COLOR_TEXTO_TITULO);
        th.setPreferredSize(new Dimension(0, 36));

        // Renderer Columna 0: Avatar + Nombre + Rol
        tablaAsistencias.getColumnModel().getColumn(0).setPreferredWidth(220);
        tablaAsistencias.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
            private static final long serialVersionUID = 1L;
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                JPanel p = new JPanel(new BorderLayout(8, 0));
                p.setOpaque(true);
                p.setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
                p.setBorder(new EmptyBorder(4, 8, 4, 8));

                if (value instanceof RegistroAsistencia) {
                    RegistroAsistencia a = (RegistroAsistencia) value;
                    JLabel av = new JLabel(obtenerIniciales(a.getNombreColaborador()), SwingConstants.CENTER);
                    av.setPreferredSize(new Dimension(32, 32));
                    av.setFont(new Font("Segoe UI", Font.BOLD, 12));
                    av.setOpaque(true);
                    av.setBackground("Veterinario".equals(a.getCategoria()) ? COLOR_MORADO : COLOR_AZUL);
                    av.setForeground(Color.WHITE);
                    av.setBorder(new LineBorder(Color.WHITE, 1, true));

                    JPanel txtP = new JPanel(new GridLayout(2, 1, 0, 2));
                    txtP.setOpaque(false);

                    JLabel lblNom = new JLabel(a.getNombreColaborador());
                    lblNom.setFont(new Font("Segoe UI", Font.BOLD, 12));
                    lblNom.setForeground(COLOR_TEXTO_TITULO);

                    JLabel lblRol = new JLabel(a.getRol() + " (" + a.getCategoria() + ")");
                    lblRol.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                    lblRol.setForeground(COLOR_TEXTO_MUTED);

                    txtP.add(lblNom);
                    txtP.add(lblRol);

                    p.add(av, BorderLayout.WEST);
                    p.add(txtP, BorderLayout.CENTER);
                } else {
                    JLabel l = new JLabel(String.valueOf(value));
                    p.add(l, BorderLayout.CENTER);
                }
                return p;
            }
        });

        // Renderer Columna 1: Turno
        tablaAsistencias.getColumnModel().getColumn(1).setPreferredWidth(140);
        // Renderer Columna 2: Entrada
        tablaAsistencias.getColumnModel().getColumn(2).setPreferredWidth(75);
        tablaAsistencias.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            private static final long serialVersionUID = 1L;
            @Override
            public Component getTableCellRendererComponent(JTable table, Object val, boolean sel, boolean foc, int row, int col) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(table, val, sel, foc, row, col);
                l.setHorizontalAlignment(SwingConstants.CENTER);
                l.setFont(new Font("Segoe UI", Font.BOLD, 12));
                return l;
            }
        });

        // Renderer Columna 3: Salida
        tablaAsistencias.getColumnModel().getColumn(3).setPreferredWidth(75);
        tablaAsistencias.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            private static final long serialVersionUID = 1L;
            @Override
            public Component getTableCellRendererComponent(JTable table, Object val, boolean sel, boolean foc, int row, int col) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(table, val, sel, foc, row, col);
                l.setHorizontalAlignment(SwingConstants.CENTER);
                l.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                return l;
            }
        });

        // Renderer Columna 4: Estado (Badge)
        tablaAsistencias.getColumnModel().getColumn(4).setPreferredWidth(100);
        tablaAsistencias.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            private static final long serialVersionUID = 1L;
            @Override
            public Component getTableCellRendererComponent(JTable table, Object val, boolean sel, boolean foc, int row, int col) {
                JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 8));
                p.setOpaque(true);
                p.setBackground(sel ? table.getSelectionBackground() : Color.WHITE);

                String st = String.valueOf(val);
                p.add(crearBadgeEstadoAsistencia(st));
                return p;
            }
        });

        // Renderer Columna 5: Permiso / Nota
        tablaAsistencias.getColumnModel().getColumn(5).setPreferredWidth(160);

        // Renderer Columna 6: Botón [Marcar]
        tablaAsistencias.getColumnModel().getColumn(6).setPreferredWidth(90);
        tablaAsistencias.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            private static final long serialVersionUID = 1L;
            @Override
            public Component getTableCellRendererComponent(JTable table, Object val, boolean sel, boolean foc, int row, int col) {
                JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 8));
                p.setOpaque(true);
                p.setBackground(sel ? table.getSelectionBackground() : Color.WHITE);
                JButton btn = new JButton("Marcar");
                btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
                btn.setBackground(Color.WHITE);
                btn.setForeground(COLOR_MORADO);
                btn.setFocusPainted(false);
                btn.setBorder(new CompoundBorder(new LineBorder(new Color(216, 180, 254), 1, true), new EmptyBorder(3, 8, 3, 8)));
                p.add(btn);
                return p;
            }
        });

        // Selección de fila para sincronizar con Panel Lateral
        tablaAsistencias.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = tablaAsistencias.getSelectedRow();
                if (row >= 0 && listaAsistenciasFiltradas != null && row < listaAsistenciasFiltradas.size()) {
                    seleccionarColaborador(listaAsistenciasFiltradas.get(row));
                }
            }
        });

        tablaAsistencias.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int row = tablaAsistencias.getSelectedRow();
                int col = tablaAsistencias.getSelectedColumn();
                if (row >= 0 && col == 6) {
                    if (listaAsistenciasFiltradas != null && row < listaAsistenciasFiltradas.size()) {
                        mostrarDialogoMarcar(listaAsistenciasFiltradas.get(row));
                    }
                }
            }
        });

        JScrollPane scroll = new JScrollPane(tablaAsistencias);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(Color.WHITE);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPanelLateralControl() {
        JPanel lateral = new JPanel(new BorderLayout(0, 14));
        lateral.setPreferredSize(new Dimension(340, 0));
        lateral.setOpaque(false);

        // Tarjeta Superior: Detalle del colaborador seleccionado
        JPanel cardDetalle = new JPanel(new BorderLayout(10, 10));
        cardDetalle.setBackground(COLOR_CARD);
        cardDetalle.setBorder(new CompoundBorder(
                new LineBorder(COLOR_BORDE, 1, true),
                new EmptyBorder(14, 16, 14, 16)
        ));

        JLabel lblTitDetalle = new JLabel("Detalle del Colaborador");
        lblTitDetalle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTitDetalle.setForeground(COLOR_TEXTO_TITULO);

        JPanel infoPersona = new JPanel(new BorderLayout(8, 0));
        infoPersona.setOpaque(false);

        lblSelNombre = new JLabel("Dra. Elena Ruiz Salazar");
        lblSelNombre.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblSelNombre.setForeground(COLOR_TEXTO_TITULO);

        lblSelCargo = new JLabel("Medicina Felina · CMPV 29/3310");
        lblSelCargo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSelCargo.setForeground(COLOR_TEXTO_MUTED);

        JPanel nombresPanel = new JPanel(new GridLayout(2, 1, 0, 2));
        nombresPanel.setOpaque(false);
        nombresPanel.add(lblSelNombre);
        nombresPanel.add(lblSelCargo);

        lblSelEstadoBadge = crearBadgeEstadoAsistencia("Presente");

        infoPersona.add(nombresPanel, BorderLayout.CENTER);
        infoPersona.add(lblSelEstadoBadge, BorderLayout.EAST);

        // Campos de horario y alerta
        JPanel panelCampos = new JPanel(new GridLayout(4, 1, 0, 6));
        panelCampos.setOpaque(false);
        panelCampos.setBorder(new CompoundBorder(
                new LineBorder(new Color(241, 245, 249), 1, true),
                new EmptyBorder(8, 10, 8, 10)
        ));
        panelCampos.setBackground(new Color(248, 250, 252));

        lblSelTurno = new JLabel("⏰ Turno: Mañana (08:00 - 15:00)");
        lblSelTurno.setFont(new Font("Segoe UI", Font.PLAIN, 11));

        lblSelEntrada = new JLabel("📥 Entrada: 07:55  (Esperada: 08:00)");
        lblSelEntrada.setFont(new Font("Segoe UI", Font.PLAIN, 11));

        lblSelSalida = new JLabel("📤 Salida: --:--  (Esperada: 15:00)");
        lblSelSalida.setFont(new Font("Segoe UI", Font.PLAIN, 11));

        lblSelNotaAlerta = new JLabel("Nota: Puntual · En consulta felina");
        lblSelNotaAlerta.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblSelNotaAlerta.setForeground(COLOR_VERDE);

        panelCampos.add(lblSelTurno);
        panelCampos.add(lblSelEntrada);
        panelCampos.add(lblSelSalida);
        panelCampos.add(lblSelNotaAlerta);

        // Botones de acción del seleccionado
        JPanel panelBotonesAccion = new JPanel(new GridLayout(2, 1, 0, 8));
        panelBotonesAccion.setOpaque(false);

        JButton btnRegistrarMarcacion = Ui.botonPrimario("Registrar Asistencia / Salida", Iconos.crearIconoAsistencia(14, Color.WHITE));
        btnRegistrarMarcacion.setBackground(COLOR_MORADO);
        btnRegistrarMarcacion.addActionListener(e -> {
            if (registroSeleccionado != null) {
                mostrarDialogoMarcar(registroSeleccionado);
            }
        });

        JButton btnCrearPermiso = Ui.botonSecundario("+ Crear Permiso para Colaborador", Iconos.crearIconoPermiso(14, COLOR_TEXTO_MUTED));
        btnCrearPermiso.addActionListener(e -> mostrarDialogoCrearPermiso());

        panelBotonesAccion.add(btnRegistrarMarcacion);
        panelBotonesAccion.add(btnCrearPermiso);

        cardDetalle.add(lblTitDetalle, BorderLayout.NORTH);
        JPanel centroCard = new JPanel(new BorderLayout(0, 10));
        centroCard.setOpaque(false);
        centroCard.add(infoPersona, BorderLayout.NORTH);
        centroCard.add(panelCampos, BorderLayout.CENTER);
        centroCard.add(panelBotonesAccion, BorderLayout.SOUTH);
        cardDetalle.add(centroCard, BorderLayout.CENTER);

        // Tarjeta Inferior: Permisos de hoy & Cobertura noche
        JPanel cardPermisosYNoche = new JPanel(new BorderLayout(0, 10));
        cardPermisosYNoche.setBackground(COLOR_CARD);
        cardPermisosYNoche.setBorder(new CompoundBorder(
                new LineBorder(COLOR_BORDE, 1, true),
                new EmptyBorder(14, 16, 14, 16)
        ));

        JLabel lblTitPermisos = new JLabel("📋 Permisos de Hoy");
        lblTitPermisos.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTitPermisos.setForeground(COLOR_TEXTO_TITULO);

        panelPermisosHoyLista = new JPanel();
        panelPermisosHoyLista.setLayout(new BoxLayout(panelPermisosHoyLista, BoxLayout.Y_AXIS));
        panelPermisosHoyLista.setOpaque(false);

        // Widget Cobertura noche abajo
        JPanel boxNoche = new JPanel(new BorderLayout(6, 6));
        boxNoche.setBackground(new Color(248, 250, 252));
        boxNoche.setBorder(new CompoundBorder(
                new LineBorder(COLOR_BORDE, 1, true),
                new EmptyBorder(8, 10, 8, 10)
        ));

        JLabel lblTitNoche = new JLabel("🌙 Cobertura Noche (Hoy 22:00)");
        lblTitNoche.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblTitNoche.setForeground(COLOR_TEXTO_TITULO);

        JLabel lblNocheDoc = new JLabel("Dr. Carlos Méndez · Urgencias 24h");
        lblNocheDoc.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblNocheDoc.setForeground(COLOR_TEXTO_MUTED);

        JButton btnReasignarGuardia = new JButton("Reasignar Guardia");
        btnReasignarGuardia.setFont(new Font("Segoe UI", Font.BOLD, 10));
        btnReasignarGuardia.setBackground(Color.WHITE);
        btnReasignarGuardia.setForeground(COLOR_AZUL);
        btnReasignarGuardia.setBorder(new LineBorder(COLOR_BORDE, 1, true));
        btnReasignarGuardia.addActionListener(e -> JOptionPane.showMessageDialog(this, "Apertura de reasignación de guardia nocturna de emergencias.", "Guardia Nocturna", JOptionPane.INFORMATION_MESSAGE));

        boxNoche.add(lblTitNoche, BorderLayout.NORTH);
        boxNoche.add(lblNocheDoc, BorderLayout.CENTER);
        boxNoche.add(btnReasignarGuardia, BorderLayout.EAST);

        cardPermisosYNoche.add(lblTitPermisos, BorderLayout.NORTH);
        cardPermisosYNoche.add(panelPermisosHoyLista, BorderLayout.CENTER);
        cardPermisosYNoche.add(boxNoche, BorderLayout.SOUTH);

        lateral.add(cardDetalle, BorderLayout.NORTH);
        lateral.add(cardPermisosYNoche, BorderLayout.CENTER);
        return lateral;
    }

    private void actualizarListaPermisosHoy() {
        panelPermisosHoyLista.removeAll();
        List<SolicitudPermiso> permisos = repo.getSolicitudesPermisos();

        for (SolicitudPermiso sp : permisos) {
            JPanel item = new JPanel(new BorderLayout(6, 4));
            item.setBackground(new Color(248, 250, 252));
            item.setBorder(new CompoundBorder(
                    new LineBorder(COLOR_BORDE, 1, true),
                    new EmptyBorder(6, 8, 6, 8)
            ));

            JLabel lblSol = new JLabel("<html><b>" + sp.getId() + " · " + sp.getNombreSolicitante() + "</b> (" + sp.getTipoPermiso() + ")<br>" +
                    "<font color='#64748b'>Cubre: " + sp.getReemplazoPropuesto() + " · " + sp.getTurnoAfectado() + "</font></html>");
            lblSol.setFont(new Font("Segoe UI", Font.PLAIN, 11));

            JPanel botItem = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
            botItem.setOpaque(false);

            if ("Pendiente".equalsIgnoreCase(sp.getEstado())) {
                JButton btnOk = new JButton("Aprobar");
                btnOk.setFont(new Font("Segoe UI", Font.BOLD, 10));
                btnOk.setBackground(COLOR_VERDE);
                btnOk.setForeground(Color.WHITE);
                btnOk.setBorder(new EmptyBorder(3, 6, 3, 6));
                btnOk.addActionListener(e -> {
                    repo.aprobarPermiso(sp.getId());
                    JOptionPane.showMessageDialog(this, "Permiso " + sp.getId() + " aprobado exitosamente.", "Permiso Aprobado", JOptionPane.INFORMATION_MESSAGE);
                    recargarDatos();
                });

                JButton btnNo = new JButton("Rechazar");
                btnNo.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                btnNo.setBackground(Color.WHITE);
                btnNo.setForeground(COLOR_ROJO);
                btnNo.setBorder(new LineBorder(COLOR_BORDE, 1, true));
                btnNo.addActionListener(e -> {
                    repo.rechazarPermiso(sp.getId(), "No procede por falta de cobertura");
                    JOptionPane.showMessageDialog(this, "Permiso " + sp.getId() + " rechazado.", "Permiso Rechazado", JOptionPane.INFORMATION_MESSAGE);
                    recargarDatos();
                });

                botItem.add(btnOk);
                botItem.add(btnNo);
            } else {
                JLabel lblEstado = new JLabel(" " + sp.getEstado() + " ");
                lblEstado.setFont(new Font("Segoe UI", Font.BOLD, 10));
                lblEstado.setForeground("Aprobado".equalsIgnoreCase(sp.getEstado()) ? COLOR_VERDE : COLOR_ROJO);
                botItem.add(lblEstado);
            }

            item.add(lblSol, BorderLayout.CENTER);
            item.add(botItem, BorderLayout.SOUTH);

            panelPermisosHoyLista.add(item);
            panelPermisosHoyLista.add(Box.createVerticalStrut(6));
        }

        panelPermisosHoyLista.revalidate();
        panelPermisosHoyLista.repaint();
    }

    private JLabel crearBadgeEstadoAsistencia(String estado) {
        JLabel b = new JLabel("  " + estado + "  ");
        b.setOpaque(true);
        b.setFont(new Font("Segoe UI", Font.BOLD, 11));

        Color bg;
        Color fg;

        if ("Presente".equalsIgnoreCase(estado)) {
            bg = new Color(220, 252, 231);
            fg = new Color(21, 128, 61);
        } else if ("Retardo".equalsIgnoreCase(estado)) {
            bg = new Color(254, 243, 199);
            fg = new Color(180, 83, 9);
        } else if ("Ausente".equalsIgnoreCase(estado)) {
            bg = new Color(254, 226, 226);
            fg = new Color(185, 28, 28);
        } else if ("Permiso".equalsIgnoreCase(estado)) {
            bg = new Color(243, 232, 255);
            fg = new Color(126, 34, 206);
        } else { // Pendiente
            bg = new Color(241, 245, 249);
            fg = new Color(100, 116, 139);
        }

        b.setBackground(bg);
        b.setForeground(fg);
        b.setBorder(new CompoundBorder(
                new LineBorder(bg.darker(), 1, true),
                new EmptyBorder(2, 6, 2, 6)
        ));
        return b;
    }

    private void seleccionarColaborador(RegistroAsistencia a) {
        this.registroSeleccionado = a;
        lblSelNombre.setText(a.getNombreColaborador());
        lblSelCargo.setText(a.getRol() + " · " + a.getCategoria());

        lblSelEstadoBadge.setText("  " + a.getEstado() + "  ");
        JLabel nuevoBadge = crearBadgeEstadoAsistencia(a.getEstado());
        lblSelEstadoBadge.setBackground(nuevoBadge.getBackground());
        lblSelEstadoBadge.setForeground(nuevoBadge.getForeground());
        lblSelEstadoBadge.setBorder(nuevoBadge.getBorder());

        lblSelTurno.setText("⏰ Turno: " + a.getTurno());
        lblSelEntrada.setText("📥 Entrada: " + a.getHoraEntradaReal() + "  (Esperada: " + a.getHoraEntradaEsperada() + ")");
        lblSelSalida.setText("📤 Salida: " + a.getHoraSalidaReal() + "  (Esperada: " + a.getHoraSalidaEsperada() + ")");

        if ("Retardo".equalsIgnoreCase(a.getEstado())) {
            lblSelNotaAlerta.setText("⚠️ " + a.getNotaJustificacion());
            lblSelNotaAlerta.setForeground(COLOR_AMBAR);
        } else if ("Ausente".equalsIgnoreCase(a.getEstado())) {
            lblSelNotaAlerta.setText("❌ " + a.getNotaJustificacion());
            lblSelNotaAlerta.setForeground(COLOR_ROJO);
        } else if ("Permiso".equalsIgnoreCase(a.getEstado())) {
            lblSelNotaAlerta.setText("📋 " + a.getNotaJustificacion());
            lblSelNotaAlerta.setForeground(COLOR_MORADO);
        } else {
            lblSelNotaAlerta.setText("✓ " + (a.getNotaJustificacion() != null ? a.getNotaJustificacion() : "Puntual"));
            lblSelNotaAlerta.setForeground(COLOR_VERDE);
        }
    }

    public void recargarDatos() {
        modeloTabla.setRowCount(0);
        List<RegistroAsistencia> todos = repo.getAsistencias();

        // Actualizar KPIs superiores
        lblKpiPresentes.setText(String.valueOf(repo.contarPresentesHoy()));
        lblKpiRetardos.setText(String.valueOf(repo.contarRetardosHoy()));
        lblKpiAusentes.setText(String.valueOf(repo.contarAusentesHoy()));
        lblKpiPermisos.setText(String.valueOf(repo.contarPermisosHoy()));

        actualizarPildorasTurnos(todos);
        actualizarListaPermisosHoy();

        String busq = txtBuscar != null ? txtBuscar.getText().trim().toLowerCase() : "";
        String estFiltro = cmbEstado != null ? (String) cmbEstado.getSelectedItem() : "Todos los estados";
        String catFiltro = cmbCategoria != null ? (String) cmbCategoria.getSelectedItem() : "Todas las áreas";

        listaAsistenciasFiltradas = todos.stream().filter(a -> {
            if (!busq.isEmpty()) {
                boolean mNom = a.getNombreColaborador().toLowerCase().contains(busq);
                boolean mRol = a.getRol().toLowerCase().contains(busq);
                if (!mNom && !mRol) return false;
            }
            if (!turnoFiltroPildora.equalsIgnoreCase("Todos")) {
                if (!a.getTurno().toLowerCase().contains(turnoFiltroPildora.toLowerCase())) {
                    return false;
                }
            }
            if (estFiltro != null && !estFiltro.equals("Todos los estados")) {
                if (!a.getEstado().equalsIgnoreCase(estFiltro)) {
                    return false;
                }
            }
            if (catFiltro != null && !catFiltro.equals("Todas las áreas")) {
                if (catFiltro.contains("Veterinario") && !a.getCategoria().equalsIgnoreCase("Veterinario")) {
                    return false;
                }
                if (catFiltro.contains("Apoyo") && !a.getCategoria().equalsIgnoreCase("Apoyo")) {
                    return false;
                }
            }
            return true;
        }).collect(Collectors.toList());

        for (RegistroAsistencia a : listaAsistenciasFiltradas) {
            modeloTabla.addRow(new Object[]{
                    a,
                    a.getTurno(),
                    a.getHoraEntradaReal(),
                    a.getHoraSalidaReal(),
                    a.getEstado(),
                    a.getNotaJustificacion() != null ? a.getNotaJustificacion() : "-",
                    "Marcar"
            });
        }

        if (!listaAsistenciasFiltradas.isEmpty()) {
            tablaAsistencias.setRowSelectionInterval(0, 0);
            seleccionarColaborador(listaAsistenciasFiltradas.get(0));
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

    private void mostrarDialogoMarcar(RegistroAsistencia a) {
        JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(this), "Registrar Marcación: " + a.getNombreColaborador(), JDialog.ModalityType.APPLICATION_MODAL);
        dlg.setSize(480, 420);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());

        JPanel p = new JPanel(new GridLayout(6, 2, 8, 8));
        p.setBackground(Color.WHITE);
        p.setBorder(new EmptyBorder(20, 24, 20, 24));

        LocalTime ahora = LocalTime.now();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");
        String horaActualStr = ahora.format(dtf);

        JTextField txtEntrada = new JTextField(a.getHoraEntradaReal().equals("--:--") ? horaActualStr : a.getHoraEntradaReal(), 10);
        JTextField txtSalida = new JTextField(a.getHoraSalidaReal().equals("--:--") ? horaActualStr : a.getHoraSalidaReal(), 10);
        JComboBox<String> cmbNuevoEst = new JComboBox<>(new String[]{
                "Presente", "Retardo", "Ausente", "Permiso", "Pendiente"
        });
        cmbNuevoEst.setSelectedItem(a.getEstado());
        JTextField txtRetardoMin = new JTextField(String.valueOf(a.getMinutosRetardo()), 6);
        JTextField txtNota = new JTextField(a.getNotaJustificacion() != null ? a.getNotaJustificacion() : "", 15);

        p.add(new JLabel("Colaborador:"));
        p.add(new JLabel("<html><b>" + a.getNombreColaborador() + "</b></html>"));
        p.add(new JLabel("Hora Entrada:"));
        p.add(txtEntrada);
        p.add(new JLabel("Hora Salida:"));
        p.add(txtSalida);
        p.add(new JLabel("Estado:"));
        p.add(cmbNuevoEst);
        p.add(new JLabel("Minutos de retardo:"));
        p.add(txtRetardoMin);
        p.add(new JLabel("Justificación / Nota:"));
        p.add(txtNota);

        JPanel bot = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        bot.setBackground(Color.WHITE);

        JButton btnGuardar = Ui.botonPrimario("Guardar Marcación", null);
        btnGuardar.setBackground(COLOR_MORADO);
        btnGuardar.addActionListener(e -> {
            int retardo = 0;
            try { retardo = Integer.parseInt(txtRetardoMin.getText().trim()); } catch (Exception ignored) {}
            String nuevoEst = (String) cmbNuevoEst.getSelectedItem();

            repo.marcarEntrada(a.getId(), txtEntrada.getText().trim(), nuevoEst, retardo, txtNota.getText().trim());
            repo.marcarSalida(a.getId(), txtSalida.getText().trim());

            JOptionPane.showMessageDialog(dlg, "Marcación de asistencia guardada para " + a.getNombreColaborador(), "Registro Guardado", JOptionPane.INFORMATION_MESSAGE);
            dlg.dispose();
            recargarDatos();
        });

        JButton btnCancelar = Ui.botonSecundario("Cancelar", null);
        btnCancelar.addActionListener(e -> dlg.dispose());

        bot.add(btnGuardar);
        bot.add(btnCancelar);

        dlg.add(p, BorderLayout.CENTER);
        dlg.add(bot, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    private void mostrarDialogoPaseListaRapido() {
        int resp = JOptionPane.showConfirmDialog(
                this,
                "¿Desea marcar como 'Presentes' a todos los colaboradores del turno actual que aún estén pendientes?",
                "Pase de Lista Rápido",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );
        if (resp == JOptionPane.YES_OPTION) {
            String hora = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
            for (RegistroAsistencia a : repo.getAsistencias()) {
                if ("Pendiente".equalsIgnoreCase(a.getEstado()) && a.getTurno().toLowerCase().contains("mañana")) {
                    repo.marcarEntrada(a.getId(), hora, "Presente", 0, "Pase de lista masivo");
                }
            }
            JOptionPane.showMessageDialog(this, "Pase de lista rápido completado para el personal de turno mañana.", "Completado", JOptionPane.INFORMATION_MESSAGE);
            recargarDatos();
        }
    }

    private void mostrarDialogoCrearPermiso() {
        JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(this), "Crear Solicitud de Permiso / Licencia", JDialog.ModalityType.APPLICATION_MODAL);
        dlg.setSize(520, 460);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());

        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(new EmptyBorder(18, 20, 18, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = 0;

        JTextField txtSol = new JTextField(registroSeleccionado != null ? registroSeleccionado.getNombreColaborador() : "Dra. Laura Morales", 20);
        JComboBox<String> cmbTipo = new JComboBox<>(new String[]{
                "Permiso médico", "Capacitación / Congreso", "Asunto personal", "Compensación de guardia", "Vacaciones"
        });
        JTextField txtTurnoAfect = new JTextField("Tarde (15:00 - 22:00)", 20);
        JTextField txtReemp = new JTextField("Dr. Carlos Méndez", 20);
        JTextField txtMotivo = new JTextField("Asistencia a congreso veterinario y capacitación", 20);

        p.add(new JLabel("Colaborador Solicitante:"), gbc);
        gbc.gridx = 1; p.add(txtSol, gbc);
        gbc.gridx = 0; gbc.gridy++; p.add(new JLabel("Tipo de Permiso:"), gbc);
        gbc.gridx = 1; p.add(cmbTipo, gbc);
        gbc.gridx = 0; gbc.gridy++; p.add(new JLabel("Turno que Cubrirá:"), gbc);
        gbc.gridx = 1; p.add(txtTurnoAfect, gbc);
        gbc.gridx = 0; gbc.gridy++; p.add(new JLabel("Colaborador Reemplazo:"), gbc);
        gbc.gridx = 1; p.add(txtReemp, gbc);
        gbc.gridx = 0; gbc.gridy++; p.add(new JLabel("Motivo Detallado:"), gbc);
        gbc.gridx = 1; p.add(txtMotivo, gbc);

        JPanel bot = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        bot.setBackground(Color.WHITE);

        JButton btnGuardar = Ui.botonPrimario("Registrar Solicitud", null);
        btnGuardar.setBackground(COLOR_MORADO);
        btnGuardar.addActionListener(e -> {
            SolicitudPermiso sp = new SolicitudPermiso(
                    "P-" + (185 + repo.getSolicitudesPermisos().size()),
                    registroSeleccionado != null ? registroSeleccionado.getPersonalId() : "VET-01",
                    txtSol.getText().trim(),
                    registroSeleccionado != null ? registroSeleccionado.getRol() : "Especialista Clínico",
                    (String) cmbTipo.getSelectedItem(),
                    LocalDate.now(),
                    txtTurnoAfect.getText().trim(),
                    txtReemp.getText().trim(),
                    txtMotivo.getText().trim(),
                    "Pendiente"
            );

            repo.guardarSolicitudPermiso(sp);
            JOptionPane.showMessageDialog(dlg, "Solicitud de permiso " + sp.getId() + " registrada y enviada para aprobación.", "Solicitud Registrada", JOptionPane.INFORMATION_MESSAGE);
            dlg.dispose();
            recargarDatos();
        });

        JButton btnCancelar = Ui.botonSecundario("Cancelar", null);
        btnCancelar.addActionListener(e -> dlg.dispose());

        bot.add(btnGuardar);
        bot.add(btnCancelar);

        dlg.add(p, BorderLayout.CENTER);
        dlg.add(bot, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }
}
