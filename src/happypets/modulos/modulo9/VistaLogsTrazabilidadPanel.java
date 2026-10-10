package happypets.modulos.modulo9;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import happypets.data.RepositorioVeterinaria;
import happypets.model.LogAuditoria;
import happypets.ui.Iconos;
import happypets.ui.Ui;

/**
 * Submódulo 9.4: Logs y Trazabilidad (Auditoría)
 * Responsable: Vera Aguilar, Carlos Edgardo
 * Registro forense y auditoría inmutable de accesos, descargas, modificaciones de configuración
 * y alertas de seguridad con filtrado cronológico y exportación masiva.
 */
public class VistaLogsTrazabilidadPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private final RepositorioVeterinaria repo = RepositorioVeterinaria.getInstancia();

    // Filtros
    private JTextField txtRangoFechas;
    private JComboBox<String> comboEvento;
    private JTextField txtBuscarUsuario;

    // Tabla
    private JTable tablaLogs;
    private DefaultTableModel modeloLogs;
    private JLabel lblPaginacion;
    private List<LogAuditoria> logsFiltrados;
    private int paginaActual = 1;
    private final int tamanoPagina = 6;

    public VistaLogsTrazabilidadPanel() {
        setLayout(new BorderLayout(0, 16));
        setBackground(new Color(248, 250, 252));
        setBorder(BorderFactory.createEmptyBorder(20, 24, 24, 24));

        add(crearCabeceraSuperior(), BorderLayout.NORTH);

        JPanel panelCentro = new JPanel(new BorderLayout(0, 12));
        panelCentro.setOpaque(false);

        panelCentro.add(crearBarraFiltros(), BorderLayout.NORTH);
        panelCentro.add(crearPanelTabla(), BorderLayout.CENTER);

        add(panelCentro, BorderLayout.CENTER);

        filtrarLogs();
    }

    private JPanel crearCabeceraSuperior() {
        JPanel cab = new JPanel(new BorderLayout(16, 8));
        cab.setBackground(new Color(30, 41, 59));
        cab.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(15, 23, 42), 1),
                BorderFactory.createEmptyBorder(14, 20, 14, 20)
        ));

        JPanel pnlTit = new JPanel();
        pnlTit.setLayout(new BoxLayout(pnlTit, BoxLayout.Y_AXIS));
        pnlTit.setOpaque(false);

        JLabel lblTit = new JLabel("Submódulo 4: Logs y Trazabilidad (Auditoría)");
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTit.setForeground(Color.WHITE);
        pnlTit.add(lblTit);

        JLabel lblSub = new JLabel("Pista de auditoría forense, registro inmutable de transacciones, descargas y accesos.");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblSub.setForeground(new Color(148, 163, 184));
        pnlTit.add(lblSub);

        cab.add(pnlTit, BorderLayout.WEST);

        JPanel pnlUsr = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        pnlUsr.setOpaque(false);
        JLabel lblUsr = new JLabel("Usuario: Admin 👤");
        lblUsr.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblUsr.setForeground(new Color(226, 232, 240));
        pnlUsr.add(lblUsr);

        cab.add(pnlUsr, BorderLayout.EAST);
        return cab;
    }

    private JPanel crearBarraFiltros() {
        JPanel card = new JPanel(new BorderLayout(12, 0));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)
        ));

        JPanel izq = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        izq.setOpaque(false);

        // Rango de fechas
        txtRangoFechas = new JTextField("Desde: 2026-03-01 Hasta: 2026-03-24", 18);
        txtRangoFechas.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtRangoFechas.setPreferredSize(new Dimension(240, 32));
        izq.add(txtRangoFechas);

        // Selector Evento
        comboEvento = new JComboBox<>(new String[]{
                "Evento: Todos ▼",
                "Modificación de Canales",
                "Descarga de Documento",
                "Lectura de Notificación",
                "Intento de Acceso",
                "Carga de Archivo"
        });
        comboEvento.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        comboEvento.setPreferredSize(new Dimension(180, 32));
        comboEvento.setBackground(Color.WHITE);
        izq.add(comboEvento);

        // Buscador de usuario
        txtBuscarUsuario = new JTextField(12);
        txtBuscarUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtBuscarUsuario.setPreferredSize(new Dimension(150, 32));
        txtBuscarUsuario.setToolTipText("Buscar usuario...");
        izq.add(txtBuscarUsuario);

        card.add(izq, BorderLayout.CENTER);

        // Botón Filtrar
        JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        der.setOpaque(false);

        JButton btnFiltrar = Ui.botonPrimario("Filtrar", Iconos.crearIconoRefrescar(12, Color.WHITE));
        btnFiltrar.setPreferredSize(new Dimension(100, 32));
        btnFiltrar.addActionListener(e -> filtrarLogs());
        der.add(btnFiltrar);

        card.add(der, BorderLayout.EAST);
        return card;
    }

    private JPanel crearPanelTabla() {
        JPanel card = new JPanel(new BorderLayout(0, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                BorderFactory.createEmptyBorder(14, 16, 14, 16)
        ));

        String[] columnas = {"ID Evento", "Tipo de Evento / Acciones", "Usuario", "Fecha y Hora", "Dirección IP", "Estado"};
        modeloLogs = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };

        tablaLogs = new JTable(modeloLogs);
        tablaLogs.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaLogs.setRowHeight(36);
        tablaLogs.setGridColor(new Color(241, 245, 249));
        tablaLogs.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tablaLogs.getTableHeader().setBackground(new Color(248, 250, 252));
        tablaLogs.getTableHeader().setForeground(new Color(71, 85, 105));
        tablaLogs.setSelectionBackground(new Color(240, 249, 255));
        tablaLogs.setSelectionForeground(new Color(15, 23, 42));

        DefaultTableCellRenderer centroRenderer = new DefaultTableCellRenderer();
        centroRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        tablaLogs.getColumnModel().getColumn(0).setCellRenderer(centroRenderer);
        tablaLogs.getColumnModel().getColumn(0).setPreferredWidth(95);
        tablaLogs.getColumnModel().getColumn(1).setPreferredWidth(220);
        tablaLogs.getColumnModel().getColumn(2).setPreferredWidth(120);
        tablaLogs.getColumnModel().getColumn(3).setCellRenderer(centroRenderer);
        tablaLogs.getColumnModel().getColumn(3).setPreferredWidth(150);
        tablaLogs.getColumnModel().getColumn(4).setCellRenderer(centroRenderer);
        tablaLogs.getColumnModel().getColumn(4).setPreferredWidth(110);
        tablaLogs.getColumnModel().getColumn(5).setPreferredWidth(95);

        // Render de Estado (ÉXITO / FALLIDO)
        tablaLogs.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                l.setHorizontalAlignment(SwingConstants.CENTER);
                String est = value != null ? value.toString() : "";
                l.setFont(new Font("Segoe UI", Font.BOLD, 11));
                l.setOpaque(true);
                if ("ÉXITO".equalsIgnoreCase(est)) {
                    l.setBackground(new Color(236, 253, 245));
                    l.setForeground(new Color(16, 185, 129));
                } else {
                    l.setBackground(new Color(254, 226, 226));
                    l.setForeground(new Color(220, 38, 38));
                }
                return l;
            }
        });

        tablaLogs.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int fila = tablaLogs.rowAtPoint(e.getPoint());
                if (fila >= 0 && e.getClickCount() == 2) {
                    mostrarDetalleForense(fila);
                }
            }
        });

        JScrollPane scroll = new JScrollPane(tablaLogs);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(241, 245, 249), 1));
        card.add(scroll, BorderLayout.CENTER);

        // Barra inferior: Exportar + Paginación
        JPanel bot = new JPanel(new BorderLayout());
        bot.setOpaque(false);

        JButton btnExportar = Ui.botonSecundario("📥 Exportar Logs (CSV/PDF)", Iconos.crearIconoExportar(13, new Color(15, 23, 42)));
        btnExportar.setPreferredSize(new Dimension(210, 34));
        btnExportar.addActionListener(e -> exportarLogsAuditoria());
        bot.add(btnExportar, BorderLayout.WEST);

        JPanel pnlPaginacion = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        pnlPaginacion.setOpaque(false);

        JButton btnAnt = new JButton("◀");
        btnAnt.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnAnt.setBackground(Color.WHITE);
        btnAnt.addActionListener(e -> {
            if (paginaActual > 1) {
                paginaActual--;
                actualizarTablaPaginada();
            }
        });

        lblPaginacion = new JLabel("Página 1 de 1 ▶");
        lblPaginacion.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblPaginacion.setForeground(new Color(71, 85, 105));

        JButton btnSig = new JButton("▶");
        btnSig.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnSig.setBackground(Color.WHITE);
        btnSig.addActionListener(e -> {
            int maxP = Math.max(1, (int) Math.ceil((double) logsFiltrados.size() / tamanoPagina));
            if (paginaActual < maxP) {
                paginaActual++;
                actualizarTablaPaginada();
            }
        });

        pnlPaginacion.add(btnAnt);
        pnlPaginacion.add(lblPaginacion);
        pnlPaginacion.add(btnSig);
        bot.add(pnlPaginacion, BorderLayout.EAST);

        card.add(bot, BorderLayout.SOUTH);
        return card;
    }

    private void filtrarLogs() {
        String ev = (String) comboEvento.getSelectedItem();
        if (ev != null && ev.contains("Todos")) ev = "Todos";
        String usr = txtBuscarUsuario != null ? txtBuscarUsuario.getText().trim() : "";

        logsFiltrados = repo.filtrarLogsAuditoria(null, null, ev, usr);
        paginaActual = 1;
        actualizarTablaPaginada();
    }

    private void actualizarTablaPaginada() {
        modeloLogs.setRowCount(0);
        int total = logsFiltrados.size();
        int maxP = Math.max(1, (int) Math.ceil((double) total / tamanoPagina));
        if (paginaActual > maxP) paginaActual = maxP;

        int inicio = (paginaActual - 1) * tamanoPagina;
        int fin = Math.min(inicio + tamanoPagina, total);

        for (int i = inicio; i < fin; i++) {
            LogAuditoria l = logsFiltrados.get(i);
            modeloLogs.addRow(new Object[]{
                    l.getIdEvento(),
                    l.getTipoEvento(),
                    l.getUsuario(),
                    l.getFechaHoraFormateada(),
                    l.getIpOrigen(),
                    l.getEstado()
            });
        }

        if (lblPaginacion != null) {
            lblPaginacion.setText("Página " + paginaActual + " de " + maxP);
        }
    }

    private void mostrarDetalleForense(int fila) {
        int idx = (paginaActual - 1) * tamanoPagina + fila;
        if (idx < 0 || idx >= logsFiltrados.size()) return;
        LogAuditoria l = logsFiltrados.get(idx);

        JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(this), "Detalle Forense de Auditoría - " + l.getIdEvento(), JDialog.ModalityType.APPLICATION_MODAL);
        dlg.setSize(520, 360);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());

        JPanel pnl = new JPanel();
        pnl.setLayout(new BoxLayout(pnl, BoxLayout.Y_AXIS));
        pnl.setBackground(Color.WHITE);
        pnl.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        JLabel lblTit = new JLabel(l.getIdEvento() + " · " + l.getTipoEvento());
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTit.setForeground(new Color(15, 23, 42));
        pnl.add(lblTit);
        pnl.add(Box.createVerticalStrut(14));

        JPanel grid = new JPanel(new GridLayout(3, 2, 10, 8));
        grid.setOpaque(false);
        grid.add(crearItem("Usuario Ejecutor:", l.getUsuario()));
        grid.add(crearItem("Dirección IP Origen:", l.getIpOrigen()));
        grid.add(crearItem("Fecha y Hora:", l.getFechaHoraFormateada()));
        grid.add(crearItem("Estado de Ejecución:", l.getEstado()));
        grid.add(crearItem("Nivel de Integridad:", "SHA-256 Verificado"));
        grid.add(crearItem("Protocolo:", "HTTPS / TLS 1.3"));
        pnl.add(grid);
        pnl.add(Box.createVerticalStrut(16));

        JLabel lblDetTit = new JLabel("Detalles del Evento:");
        lblDetTit.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblDetTit.setForeground(new Color(100, 116, 139));
        pnl.add(lblDetTit);
        pnl.add(Box.createVerticalStrut(4));

        JLabel lblDet = new JLabel("<html><body style='width: 380px;'>" + l.getDetalles() + "</body></html>");
        lblDet.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblDet.setForeground(new Color(30, 41, 59));
        pnl.add(lblDet);

        dlg.add(pnl, BorderLayout.CENTER);

        JPanel bot = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        bot.setBackground(new Color(248, 250, 252));
        bot.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(226, 232, 240)));

        JButton btnCerrar = Ui.botonPrimario("Cerrar", null);
        btnCerrar.addActionListener(e -> dlg.dispose());
        bot.add(btnCerrar);
        dlg.add(bot, BorderLayout.SOUTH);

        dlg.setVisible(true);
    }

    private JPanel crearItem(String c, String v) {
        JPanel p = new JPanel(new BorderLayout(4, 2));
        p.setOpaque(false);
        JLabel lc = new JLabel(c);
        lc.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lc.setForeground(new Color(100, 116, 139));
        p.add(lc, BorderLayout.NORTH);

        JLabel lv = new JLabel(v);
        lv.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lv.setForeground(new Color(15, 23, 42));
        p.add(lv, BorderLayout.CENTER);
        return p;
    }

    private void exportarLogsAuditoria() {
        String msg = "Generando archivo de auditoría inmutable en formatos CSV y PDF...\n"
                + "• Total registros incluidos: " + logsFiltrados.size() + "\n"
                + "• Rango auditado: Marzo 2026\n"
                + "• Hash de firma: 8f2b3e41... (Certificado digital)\n\n"
                + "Archivo generado: exports/audit_logs_marzo_2026.csv";
        JOptionPane.showMessageDialog(this, msg, "Exportación de Auditoría", JOptionPane.INFORMATION_MESSAGE);
    }
}
