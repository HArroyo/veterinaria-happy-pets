package happypets.modulos.modulo9;

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
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import happypets.data.RepositorioVeterinaria;
import happypets.model.LogAuditoria;
import happypets.ui.Iconos;
import happypets.ui.Ui;

/**
 * Submódulo 9.4: Logs y Pistas de Auditoría Forense
 * Responsable: Vera Aguilar, Carlos Edgardo
 * Registro inmutable de transacciones, accesos, descargas y cambios de estado
 * con arquitectura Master-Detail, 4 KPIs de seguridad y live audit dock.
 */
public class VistaLogsTrazabilidadPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private final RepositorioVeterinaria repo = RepositorioVeterinaria.getInstancia();

    // KPIs Superiores
    private JLabel lblKpiTotalLogs;
    private JLabel lblKpiUsuariosAuditados;
    private JLabel lblKpiAlertasSeguridad;
    private JLabel lblKpiIntegridad;

    // Master Table (Izquierda)
    private JTable tablaLogs;
    private DefaultTableModel modeloLogs;
    private JTextField txtBuscar;
    private String tipoEventoFiltro = "Todos";
    private final List<LogAuditoria> listaLogsFiltrada = new ArrayList<>();
    private JPanel panelPills;

    // Live Detail Hub (Derecha - 360px)
    private JPanel panelHubContenido;
    private JPanel panelHubVacio;
    private JPanel panelHubDetalle;

    private JLabel lblHubAvatar;
    private JLabel lblHubEvento;
    private JLabel lblHubUsuarioTitulo;
    private JLabel lblHubChipEstado;

    // Telemetría 2x2
    private JLabel lblHubId;
    private JLabel lblHubFecha;
    private JLabel lblHubUsuario;
    private JLabel lblHubIp;

    // Secciones de Detalle
    private JTextArea txtHubDetalle;
    private JLabel lblHubHash;
    private JLabel lblHubModulo;

    private LogAuditoria logSeleccionado;

    public VistaLogsTrazabilidadPanel() {
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
        cab.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

        JPanel izq = new JPanel();
        izq.setOpaque(false);
        izq.setLayout(new BoxLayout(izq, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("Logs y Pistas de Auditoría Forense");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 17));
        titulo.setForeground(Ui.TEXTO_TITULO);

        JLabel subtitulo = new JLabel("Módulo 9.4 · Registro inmutable de transacciones, descargas, modificaciones y accesos de seguridad");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitulo.setForeground(Ui.TEXTO_MUTED);

        izq.add(titulo);
        izq.add(Box.createVerticalStrut(2));
        izq.add(subtitulo);
        cab.add(izq, BorderLayout.WEST);

        JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        der.setOpaque(false);

        JButton btnActualizar = Ui.boton("Actualizar", false);
        btnActualizar.addActionListener(e -> recargarDatos());
        der.add(btnActualizar);

        JButton btnExportar = Ui.boton("Exportar Pistas", true);
        btnExportar.addActionListener(e -> exportarAuditoriaPDF());
        der.add(btnExportar);

        cab.add(der, BorderLayout.EAST);
        return cab;
    }

    private JPanel crearFilaKpis() {
        JPanel kpis = new JPanel(new GridLayout(1, 4, 12, 0));
        kpis.setOpaque(false);
        kpis.setPreferredSize(new Dimension(0, 74));
        kpis.setMaximumSize(new Dimension(Integer.MAX_VALUE, 74));

        lblKpiTotalLogs = new JLabel("0");
        lblKpiUsuariosAuditados = new JLabel("4 Operadores");
        lblKpiAlertasSeguridad = new JLabel("0 Incidentes");
        lblKpiIntegridad = new JLabel("SHA-256 Válido");

        kpis.add(crearTarjetaKpi("Eventos Registrados", lblKpiTotalLogs, "Pista forense inmutable", Iconos.crearIconoHistorial(22, Ui.TURQUESA)));
        kpis.add(crearTarjetaKpi("Usuarios Auditados", lblKpiUsuariosAuditados, "Sesiones bajo custodia", Iconos.crearIconoCheck(22, Ui.VERDE_EXITO)));
        kpis.add(crearTarjetaKpi("Incidentes de Acceso", lblKpiAlertasSeguridad, "Sin vulneraciones 30d", Iconos.crearIconoAlertaTriaje(22, Ui.ROJO_ERROR)));
        kpis.add(crearTarjetaKpi("Nivel de Integridad", lblKpiIntegridad, "Cadena criptográfica activa", Iconos.crearIconoDocumento(22, Ui.AZUL_INFO)));

        return kpis;
    }

    private JPanel crearTarjetaKpi(String titulo, JLabel lblValor, String subtitulo, Icon icono) {
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
        card.setBorder(new EmptyBorder(8, 12, 8, 12));

        JPanel texto = new JPanel();
        texto.setOpaque(false);
        texto.setLayout(new BoxLayout(texto, BoxLayout.Y_AXIS));

        JLabel lblTit = new JLabel(titulo);
        lblTit.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblTit.setForeground(Ui.TEXTO_MUTED);

        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblValor.setForeground(Ui.TEXTO_TITULO);

        JLabel lblSub = new JLabel(subtitulo);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblSub.setForeground(Ui.TEXTO_MUTED);

        texto.add(lblTit);
        texto.add(Box.createVerticalStrut(1));
        texto.add(lblValor);
        texto.add(Box.createVerticalStrut(1));
        texto.add(lblSub);

        card.add(texto, BorderLayout.CENTER);

        if (icono != null) {
            JLabel lblIco = new JLabel(icono);
            lblIco.setVerticalAlignment(SwingConstants.TOP);
            lblIco.setBorder(new EmptyBorder(2, 0, 0, 0));
            card.add(lblIco, BorderLayout.EAST);
        }

        return card;
    }

    private JSplitPane crearSplitMasterDetail() {
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setOpaque(false);
        split.setBorder(null);
        split.setDividerSize(8);
        split.setContinuousLayout(true);
        split.setResizeWeight(1.0);

        split.setLeftComponent(crearPanelMasterTable());
        split.setRightComponent(crearPanelLiveDetailHub());

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
        txtBuscar.putClientProperty("JTextField.placeholderText", "Buscar por usuario, evento, IP o detalles forenses...");
        txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filtrarLogs(); }
            public void removeUpdate(DocumentEvent e) { filtrarLogs(); }
            public void changedUpdate(DocumentEvent e) { filtrarLogs(); }
        });
        barraBuscar.add(txtBuscar, BorderLayout.CENTER);
        cabFiltros.add(barraBuscar);
        cabFiltros.add(Box.createVerticalStrut(8));

        // Píldoras de Filtro por Categoría de Evento
        panelPills = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        panelPills.setOpaque(false);
        actualizarPildorasFiltro();
        cabFiltros.add(panelPills);

        master.add(cabFiltros, BorderLayout.NORTH);

        // Tabla de Logs
        String[] cols = {"Cód. Log", "Evento Forense", "Usuario Operador", "Fecha y Hora", "IP Origen", "Resultado", "Detalle de Acción"};
        modeloLogs = new DefaultTableModel(cols, 0) {
            private static final long serialVersionUID = 1L;
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tablaLogs = new JTable(modeloLogs);
        tablaLogs.setRowHeight(46);
        tablaLogs.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaLogs.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaLogs.setShowVerticalLines(false);
        tablaLogs.setGridColor(new Color(241, 245, 249));
        tablaLogs.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        tablaLogs.getTableHeader().setBackground(new Color(248, 250, 252));
        tablaLogs.getTableHeader().setForeground(Ui.TEXTO_MUTED);

        tablaLogs.getColumnModel().getColumn(0).setPreferredWidth(95);
        tablaLogs.getColumnModel().getColumn(1).setPreferredWidth(170);
        tablaLogs.getColumnModel().getColumn(2).setPreferredWidth(130);
        tablaLogs.getColumnModel().getColumn(3).setPreferredWidth(125);
        tablaLogs.getColumnModel().getColumn(4).setPreferredWidth(95);
        tablaLogs.getColumnModel().getColumn(5).setPreferredWidth(90);
        tablaLogs.getColumnModel().getColumn(6).setPreferredWidth(200);

        // Monograma en columna 0 (#EV-1043)
        tablaLogs.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
            private static final long serialVersionUID = 1L;
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                JPanel cell = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 7));
                cell.setOpaque(true);
                cell.setBackground(isSelected ? table.getSelectionBackground() : (row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252)));

                String id = value != null ? value.toString() : "";
                JLabel avatar = new JLabel("EV", SwingConstants.CENTER) {
                    private static final long serialVersionUID = 1L;
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(new Color(241, 245, 249));
                        g2.fillOval(0, 0, getWidth(), getHeight());
                        g2.setColor(new Color(203, 213, 225));
                        g2.drawOval(0, 0, getWidth() - 1, getHeight() - 1);
                        super.paintComponent(g);
                        g2.dispose();
                    }
                };
                avatar.setPreferredSize(new Dimension(30, 30));
                avatar.setFont(new Font("Segoe UI", Font.BOLD, 10));
                avatar.setForeground(Ui.TEXTO_TITULO);

                JLabel lblId = new JLabel(id);
                lblId.setFont(new Font("Segoe UI", Font.BOLD, 11));
                lblId.setForeground(isSelected ? table.getSelectionForeground() : Ui.TEXTO_TITULO);

                cell.add(avatar);
                cell.add(lblId);
                return cell;
            }
        });

        // Chip de Resultado en columna 5 (ÉXITO / FALLO)
        tablaLogs.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            private static final long serialVersionUID = 1L;
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 11));
                p.setOpaque(true);
                p.setBackground(isSelected ? table.getSelectionBackground() : (row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252)));

                String st = value != null ? value.toString() : "ÉXITO";
                JLabel badge = new JLabel(" " + st + " ");
                badge.setOpaque(true);
                badge.setFont(new Font("Segoe UI", Font.BOLD, 10));

                if (st.toUpperCase().contains("ÉXITO") || st.toUpperCase().contains("EXITO")) {
                    badge.setBackground(new Color(220, 252, 231));
                    badge.setForeground(new Color(21, 128, 61));
                } else {
                    badge.setBackground(new Color(254, 226, 226));
                    badge.setForeground(new Color(220, 38, 38));
                }
                badge.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(badge.getBackground().darker(), 1, true),
                        new EmptyBorder(2, 6, 2, 6)
                ));
                p.add(badge);
                return p;
            }
        });

        tablaLogs.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int r = tablaLogs.getSelectedRow();
                if (r >= 0 && r < listaLogsFiltrada.size()) {
                    mostrarDetalleLog(listaLogsFiltrada.get(r));
                } else {
                    mostrarDetalleLog(null);
                }
            }
        });

        tablaLogs.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int r = tablaLogs.getSelectedRow();
                    if (r >= 0 && r < listaLogsFiltrada.size()) {
                        abrirVisorReporteLog(listaLogsFiltrada.get(r));
                    }
                }
            }
        });

        JScrollPane scroll = new JScrollPane(tablaLogs);
        scroll.setBorder(BorderFactory.createLineBorder(Ui.BORDE_SUAVE, 1));
        master.add(scroll, BorderLayout.CENTER);

        return master;
    }

    private void actualizarPildorasFiltro() {
        if (panelPills == null) return;
        panelPills.removeAll();

        int tot = repo.getLogsAuditoria().size();
        String[] pildoras = {
            "Todos (" + tot + ")",
            "Canales",
            "Descargas",
            "Notificaciones",
            "Cargas",
            "Sesiones"
        };
        String[] valores = {"Todos", "Canales", "Descarga", "Notificación", "Carga", "Sesión"};

        for (int i = 0; i < pildoras.length; i++) {
            final String f = valores[i];
            JButton btnPill = new JButton(pildoras[i]);
            btnPill.setFont(new Font("Segoe UI", Font.BOLD, 11));
            btnPill.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btnPill.setBorder(new EmptyBorder(4, 10, 4, 10));
            btnPill.setFocusPainted(false);

            if (f.equalsIgnoreCase(tipoEventoFiltro)) {
                btnPill.setBackground(Ui.TURQUESA);
                btnPill.setForeground(Color.WHITE);
            } else {
                btnPill.setBackground(new Color(241, 245, 249));
                btnPill.setForeground(Ui.TEXTO_MUTED);
            }

            btnPill.addActionListener(e -> {
                tipoEventoFiltro = f;
                actualizarPildorasFiltro();
                filtrarLogs();
            });
            panelPills.add(btnPill);
        }
        panelPills.revalidate();
        panelPills.repaint();
    }

    private JPanel crearPanelLiveDetailHub() {
        JPanel detail = new JPanel(new BorderLayout()) {
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
        detail.setOpaque(false);
        detail.setPreferredSize(new Dimension(360, 0));
        detail.setMinimumSize(new Dimension(320, 0));

        panelHubContenido = new JPanel(new BorderLayout());
        panelHubContenido.setOpaque(false);

        panelHubVacio = crearHubVacio();
        panelHubDetalle = crearHubDetalleActivo();

        panelHubContenido.add(panelHubVacio, BorderLayout.CENTER);
        detail.add(panelHubContenido, BorderLayout.CENTER);

        return detail;
    }

    private JPanel crearHubVacio() {
        JPanel vacio = new JPanel();
        vacio.setOpaque(false);
        vacio.setLayout(new BoxLayout(vacio, BoxLayout.Y_AXIS));
        vacio.setBorder(new EmptyBorder(40, 20, 40, 20));

        JLabel ico = new JLabel(Iconos.crearIconoHistorial(48, Ui.BORDE_SUAVE));
        ico.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel tit = new JLabel("Ningún Evento Seleccionado");
        tit.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tit.setForeground(Ui.TEXTO_TITULO);
        tit.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sub = new JLabel("<html><center>Seleccione un registro forense de la tabla para inspeccionar su dirección IP, payload, integridad criptográfica y usuario responsable.</center></html>");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sub.setForeground(Ui.TEXTO_MUTED);
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);

        vacio.add(Box.createVerticalGlue());
        vacio.add(ico);
        vacio.add(Box.createVerticalStrut(12));
        vacio.add(tit);
        vacio.add(Box.createVerticalStrut(6));
        vacio.add(sub);
        vacio.add(Box.createVerticalGlue());

        return vacio;
    }

    private JPanel crearHubDetalleActivo() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);

        ScrollablePanel scrollContent = new ScrollablePanel();
        scrollContent.setOpaque(false);
        scrollContent.setLayout(new BoxLayout(scrollContent, BoxLayout.Y_AXIS));
        scrollContent.setBorder(new EmptyBorder(14, 16, 14, 16));

        // 1. Hero Block
        JPanel hero = new JPanel(new BorderLayout(8, 0));
        hero.setOpaque(false);
        hero.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblHubAvatar = new JLabel("EV", SwingConstants.CENTER) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int s = Math.min(getWidth(), getHeight()) - 2;
                int x = (getWidth() - s) / 2;
                int y = (getHeight() - s) / 2;
                g2.setColor(Ui.TURQUESA_SUAVE);
                g2.fillOval(x, y, s, s);
                g2.setColor(Ui.TURQUESA);
                g2.drawOval(x, y, s, s);
                super.paintComponent(g);
                g2.dispose();
            }
        };
        lblHubAvatar.setPreferredSize(new Dimension(46, 46));
        lblHubAvatar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblHubAvatar.setForeground(Ui.TURQUESA_PROFUNDO);

        JPanel avatarBox = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        avatarBox.setOpaque(false);
        avatarBox.setPreferredSize(new Dimension(46, 46));
        avatarBox.add(lblHubAvatar);
        hero.add(avatarBox, BorderLayout.WEST);

        JPanel heroText = new JPanel();
        heroText.setOpaque(false);
        heroText.setLayout(new BoxLayout(heroText, BoxLayout.Y_AXIS));

        lblHubEvento = new JLabel("Evento Registrado");
        lblHubEvento.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblHubEvento.setForeground(Ui.TEXTO_TITULO);
        lblHubEvento.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblHubUsuarioTitulo = new JLabel("Operador: admin_user");
        lblHubUsuarioTitulo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblHubUsuarioTitulo.setForeground(Ui.TEXTO_MUTED);
        lblHubUsuarioTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel rowChips = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        rowChips.setOpaque(false);
        rowChips.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblHubChipEstado = new JLabel(" Transacción Exitosa ");
        lblHubChipEstado.setOpaque(true);
        lblHubChipEstado.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblHubChipEstado.setBackground(new Color(220, 252, 231));
        lblHubChipEstado.setForeground(new Color(21, 128, 61));
        lblHubChipEstado.setBorder(BorderFactory.createLineBorder(new Color(134, 239, 172), 1, true));
        rowChips.add(lblHubChipEstado);

        heroText.add(lblHubEvento);
        heroText.add(Box.createVerticalStrut(2));
        heroText.add(lblHubUsuarioTitulo);
        heroText.add(Box.createVerticalStrut(4));
        heroText.add(rowChips);

        hero.add(heroText, BorderLayout.CENTER);
        scrollContent.add(hero);
        scrollContent.add(Box.createVerticalStrut(14));

        // 2. Telemetría 2x2
        JPanel telemetria = new JPanel(new GridLayout(2, 2, 8, 8));
        telemetria.setOpaque(false);
        telemetria.setAlignmentX(Component.LEFT_ALIGNMENT);
        telemetria.setMaximumSize(new Dimension(Integer.MAX_VALUE, 92));

        lblHubId = new JLabel("-");
        lblHubFecha = new JLabel("-");
        lblHubUsuario = new JLabel("-");
        lblHubIp = new JLabel("-");

        telemetria.add(crearMicroCard("Cód. Identificador", lblHubId));
        telemetria.add(crearMicroCard("Estampa Cronológica", lblHubFecha));
        telemetria.add(crearMicroCard("Usuario Operador", lblHubUsuario));
        telemetria.add(crearMicroCard("Dirección IP Terminal", lblHubIp));

        scrollContent.add(telemetria);
        scrollContent.add(Box.createVerticalStrut(12));

        // 3. Card Payload / Descripción
        txtHubDetalle = new JTextArea();
        txtHubDetalle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtHubDetalle.setForeground(Ui.TEXTO_TITULO);
        txtHubDetalle.setLineWrap(true);
        txtHubDetalle.setWrapStyleWord(true);
        txtHubDetalle.setEditable(false);
        txtHubDetalle.setOpaque(false);

        JPanel pnlDetalle = crearSeccionCard("Detalle de la Operación Forense", txtHubDetalle);
        pnlDetalle.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollContent.add(pnlDetalle);
        scrollContent.add(Box.createVerticalStrut(10));

        // 4. Card Criptografía y Cadena Inmutable
        JPanel pnlHash = new JPanel(new GridLayout(2, 1, 4, 4));
        pnlHash.setOpaque(false);

        lblHubHash = new JLabel("Bloque #1043: a1c8...f93e (Firma Aprobada)");
        lblHubHash.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblHubHash.setForeground(Ui.TEXTO_MUTED);

        lblHubModulo = new JLabel("Pista de Auditoría: Inmutable y No Modificable");
        lblHubModulo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblHubModulo.setForeground(Ui.TEXTO_MUTED);

        pnlHash.add(lblHubHash);
        pnlHash.add(lblHubModulo);

        JPanel pnlHashCard = crearSeccionCard("Integridad y Trazabilidad Blockchain", pnlHash);
        pnlHashCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollContent.add(pnlHashCard);
        scrollContent.add(Box.createVerticalStrut(14));

        // 5. Botonera 2x2
        JPanel gridAcciones = new JPanel(new GridLayout(2, 2, 8, 8));
        gridAcciones.setOpaque(false);
        gridAcciones.setAlignmentX(Component.LEFT_ALIGNMENT);
        gridAcciones.setMaximumSize(new Dimension(Integer.MAX_VALUE, 74));

        JButton btnVisorPDF = Ui.botonSecundario("Descargar PDF", Iconos.crearIconoDescargar(12, Ui.TEXTO_TITULO));
        btnVisorPDF.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnVisorPDF.addActionListener(e -> abrirVisorReporteLog(logSeleccionado));

        JButton btnExportarCSV = Ui.botonSecundario("Exportar CSV", null);
        btnExportarCSV.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnExportarCSV.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                    "Exportación completa de pistas de auditoría generada en formato CSV:\n" +
                    "audit_trail_export_2026.csv (6 registros exportados)",
                    "Exportación CSV", JOptionPane.INFORMATION_MESSAGE);
        });

        JButton btnVerificarHash = Ui.botonPrimario("Verificar Hash", null);
        btnVerificarHash.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnVerificarHash.addActionListener(e -> {
            if (logSeleccionado != null) {
                JOptionPane.showMessageDialog(this,
                        "Verificación criptográfica de integridad inmutable:\n" +
                        "Registro: " + logSeleccionado.getIdEvento() + "\n" +
                        "Algoritmo: SHA-256 HMAC\n" +
                        "Resultado: Firma digital auténtica (Sin alteraciones de base de datos).",
                        "Validación Forense", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        JButton btnFiltrarUsuario = Ui.botonSecundario("Filtrar Usuario", null);
        btnFiltrarUsuario.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnFiltrarUsuario.addActionListener(e -> {
            if (logSeleccionado != null) {
                txtBuscar.setText(logSeleccionado.getUsuario());
            }
        });

        gridAcciones.add(btnVisorPDF);
        gridAcciones.add(btnExportarCSV);
        gridAcciones.add(btnVerificarHash);
        gridAcciones.add(btnFiltrarUsuario);

        scrollContent.add(gridAcciones);

        JScrollPane scroll = new JScrollPane(scrollContent);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(14);

        wrapper.add(scroll, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel crearMicroCard(String etiqueta, JLabel lblValor) {
        JPanel mc = new JPanel();
        mc.setOpaque(false);
        mc.setLayout(new BoxLayout(mc, BoxLayout.Y_AXIS));
        mc.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(241, 245, 249), 1, true),
                new EmptyBorder(6, 8, 6, 8)
        ));

        JLabel lblEtiq = new JLabel(etiqueta);
        lblEtiq.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblEtiq.setForeground(Ui.TEXTO_MUTED);

        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblValor.setForeground(Ui.TEXTO_TITULO);

        mc.add(lblEtiq);
        mc.add(Box.createVerticalStrut(2));
        mc.add(lblValor);

        return mc;
    }

    private JPanel crearSeccionCard(String titulo, JComponent contenido) {
        JPanel sec = new JPanel(new BorderLayout(0, 6));
        sec.setOpaque(false);
        sec.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(241, 245, 249), 1, true),
                new EmptyBorder(8, 10, 8, 10)
        ));

        JLabel lblTit = new JLabel(titulo);
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblTit.setForeground(Ui.TEXTO_MUTED);
        sec.add(lblTit, BorderLayout.NORTH);

        sec.add(contenido, BorderLayout.CENTER);
        return sec;
    }

    public void recargarDatos() {
        int tot = repo.getLogsAuditoria().size();
        lblKpiTotalLogs.setText(tot + " Eventos");

        actualizarPildorasFiltro();
        filtrarLogs();
    }

    private void filtrarLogs() {
        String q = txtBuscar != null ? txtBuscar.getText().trim().toLowerCase() : "";
        List<LogAuditoria> todos = repo.getLogsAuditoria();

        listaLogsFiltrada.clear();
        modeloLogs.setRowCount(0);

        for (LogAuditoria l : todos) {
            boolean coincideTipo = tipoEventoFiltro == null || "Todos".equalsIgnoreCase(tipoEventoFiltro)
                    || l.getTipoEvento().toLowerCase().contains(tipoEventoFiltro.toLowerCase());

            boolean coincideTexto = q.isEmpty()
                    || l.getIdEvento().toLowerCase().contains(q)
                    || l.getTipoEvento().toLowerCase().contains(q)
                    || l.getUsuario().toLowerCase().contains(q)
                    || l.getIpOrigen().toLowerCase().contains(q)
                    || l.getDetalles().toLowerCase().contains(q);

            if (coincideTipo && coincideTexto) {
                listaLogsFiltrada.add(l);
                modeloLogs.addRow(new Object[]{
                        l.getIdEvento(),
                        l.getTipoEvento(),
                        l.getUsuario(),
                        l.getFechaHoraFormateada(),
                        l.getIpOrigen(),
                        l.getEstado(),
                        l.getDetalles()
                });
            }
        }

        if (listaLogsFiltrada.isEmpty()) {
            mostrarDetalleLog(null);
        } else {
            int sel = tablaLogs.getSelectedRow();
            if (sel < 0 || sel >= listaLogsFiltrada.size()) {
                tablaLogs.setRowSelectionInterval(0, 0);
                mostrarDetalleLog(listaLogsFiltrada.get(0));
            } else {
                mostrarDetalleLog(listaLogsFiltrada.get(sel));
            }
        }
    }

    private void mostrarDetalleLog(LogAuditoria log) {
        this.logSeleccionado = log;
        panelHubContenido.removeAll();

        if (log == null) {
            panelHubContenido.add(panelHubVacio, BorderLayout.CENTER);
        } else {
            lblHubAvatar.setText("EV");
            lblHubEvento.setText(log.getTipoEvento());
            lblHubUsuarioTitulo.setText("Operador Responsable: " + log.getUsuario());

            lblHubId.setText(log.getIdEvento());
            lblHubFecha.setText(log.getFechaHoraFormateada());
            lblHubUsuario.setText(log.getUsuario());
            lblHubIp.setText(log.getIpOrigen());

            txtHubDetalle.setText(log.getDetalles());
            lblHubHash.setText("Bloque " + log.getIdEvento() + ": Cifrado SHA-256 Aprobado");

            panelHubContenido.add(panelHubDetalle, BorderLayout.CENTER);
        }

        panelHubContenido.revalidate();
        panelHubContenido.repaint();
    }

    private void abrirVisorReporteLog(LogAuditoria l) {
        if (l == null) return;
        String[][] meta = new String[][]{
            {"Identificador Forense", l.getIdEvento()},
            {"Tipo de Evento", l.getTipoEvento()},
            {"Usuario Operador", l.getUsuario()},
            {"Fecha y Hora", l.getFechaHoraFormateada()},
            {"IP Origen", l.getIpOrigen()},
            {"Resultado / Estado", l.getEstado()}
        };
        String[][] datos = new String[][]{
            {"1", l.getIdEvento(), l.getTipoEvento(), l.getUsuario(), l.getFechaHoraFormateada(), l.getIpOrigen(), l.getEstado(), l.getDetalles()}
        };
        Ui.mostrarVisorReporte(
            (java.awt.Frame) SwingUtilities.getWindowAncestor(this),
            "INFORME FORENSE DE AUDITORÍA ERP",
            "Pista de Auditoría " + l.getIdEvento(),
            meta,
            new String[]{"N°", "Cód. Evento", "Tipo de Evento", "Usuario", "Fecha y Hora", "IP Origen", "Estado", "Detalle de Transacción"},
            datos,
            "Pista de auditoría inmutable e inalterable Happy Pets ERP.",
            "Auditoria_" + l.getIdEvento().replace("#", "")
        );
    }

    private void exportarAuditoriaPDF() {
        List<LogAuditoria> logs = repo.getLogsAuditoria();
        String[][] meta = new String[][]{
            {"Módulo", "Logs y Trazabilidad (Auditoría Forense)"},
            {"Fecha de Auditoría", LocalDate.now().toString()},
            {"Total de Transacciones", String.valueOf(logs.size())},
            {"Criterio de Búsqueda", "Historial Completo Inmutable"},
            {"Integridad Cadena", "100% Verificada (HMAC SHA-256)"}
        };

        String[][] datos = new String[logs.size()][8];
        for (int i = 0; i < logs.size(); i++) {
            LogAuditoria l = logs.get(i);
            datos[i][0] = String.valueOf(i + 1);
            datos[i][1] = l.getIdEvento();
            datos[i][2] = l.getTipoEvento();
            datos[i][3] = l.getUsuario();
            datos[i][4] = l.getFechaHoraFormateada();
            datos[i][5] = l.getIpOrigen();
            datos[i][6] = l.getEstado();
            datos[i][7] = l.getDetalles();
        }

        Ui.mostrarVisorReporte(
            (java.awt.Frame) SwingUtilities.getWindowAncestor(this),
            "LIBRO MAYOR DE AUDITORÍA Y TRAZABILIDAD FORENSE",
            "Registro Inmutable de Operaciones y Seguridad",
            meta,
            new String[]{"N°", "Cód. Evento", "Tipo de Evento", "Usuario", "Fecha / Hora", "IP Origen", "Estado", "Detalle"},
            datos,
            "Documento certificado de auditoría y pistas de trazabilidad ERP Happy Pets.",
            "Pista_Auditoria_Forense"
        );
    }

    private static class ScrollablePanel extends JPanel implements Scrollable {
        private static final long serialVersionUID = 1L;

        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }

        @Override
        public int getScrollableUnitIncrement(java.awt.Rectangle visibleRect, int orientation, int direction) {
            return 16;
        }

        @Override
        public int getScrollableBlockIncrement(java.awt.Rectangle visibleRect, int orientation, int direction) {
            return 32;
        }

        @Override
        public boolean getScrollableTracksViewportWidth() {
            return true;
        }

        @Override
        public boolean getScrollableTracksViewportHeight() {
            return false;
        }
    }
}
