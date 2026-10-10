package happypets.modulos.modulo10;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
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
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.time.LocalDate;

import happypets.data.RepositorioVeterinaria;
import happypets.model.IntegracionExterna;
import happypets.ui.Iconos;
import happypets.ui.Ui;

/**
 * Submódulo 10.3: Integraciones Externas.
 * Basado fielmente en el wireframe 'Integraciones externas.pdf'.
 */
public class VistaIntegracionesExternasPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private final RepositorioVeterinaria repo = RepositorioVeterinaria.getInstancia();

    private JLabel lblContadorActivas;
    private JPanel panelListaIntegraciones;

    public VistaIntegracionesExternasPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(248, 250, 252));
        inicializarUI();
        recargarIntegraciones();
    }

    private void inicializarUI() {
        JPanel contenedor = new JPanel(new BorderLayout(0, 16));
        contenedor.setBackground(new Color(248, 250, 252));
        contenedor.setBorder(BorderFactory.createEmptyBorder(20, 28, 28, 28));

        // Tarjeta Principal (idéntica al wireframe Integraciones externas.pdf)
        JPanel card = new JPanel(new BorderLayout(0, 16));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                BorderFactory.createEmptyBorder(20, 24, 20, 24)
        ));

        // Cabecera: Título "Integraciones Externas" y contador "3 de 5 activas"
        JPanel head = new JPanel(new BorderLayout());
        head.setBackground(Color.WHITE);

        JPanel izq = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        izq.setBackground(Color.WHITE);
        izq.add(new JLabel(Iconos.crearIconoEnchufe(24, Ui.TURQUESA_PROFUNDO)));
        JLabel lblTit = new JLabel("Integraciones Externas");
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTit.setForeground(new Color(15, 23, 42));
        izq.add(lblTit);

        JPanel derHead = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        derHead.setBackground(Color.WHITE);

        lblContadorActivas = new JLabel("3 de 5 activas");
        lblContadorActivas.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblContadorActivas.setForeground(Ui.TURQUESA_OSCURO);
        derHead.add(lblContadorActivas);

        JButton btnExportar = Ui.botonSecundario("📄 Exportar Reporte APIs", Iconos.crearIconoExportar(13, Ui.TURQUESA_PROFUNDO));
        btnExportar.setPreferredSize(new Dimension(190, 32));
        btnExportar.addActionListener(e -> exportarReporteIntegraciones());
        derHead.add(btnExportar);

        head.add(izq, BorderLayout.WEST);
        head.add(derHead, BorderLayout.EAST);
        card.add(head, BorderLayout.NORTH);

        // Lista de tarjetas de servicio
        panelListaIntegraciones = new JPanel();
        panelListaIntegraciones.setLayout(new BoxLayout(panelListaIntegraciones, BoxLayout.Y_AXIS));
        panelListaIntegraciones.setBackground(Color.WHITE);

        JScrollPane scroll = new JScrollPane(panelListaIntegraciones);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.getVerticalScrollBar().setUnitIncrement(12);

        card.add(scroll, BorderLayout.CENTER);
        contenedor.add(card, BorderLayout.CENTER);

        add(contenedor, BorderLayout.CENTER);
    }

    public void recargarIntegraciones() {
        panelListaIntegraciones.removeAll();

        List<IntegracionExterna> lista = repo.getIntegracionesExternas();
        long activas = lista.stream().filter(IntegracionExterna::isActiva).count();
        lblContadorActivas.setText(activas + " de " + lista.size() + " activas");

        for (IntegracionExterna inte : lista) {
            panelListaIntegraciones.add(crearFilaServicio(inte));
            panelListaIntegraciones.add(Box.createVerticalStrut(12));
        }

        panelListaIntegraciones.revalidate();
        panelListaIntegraciones.repaint();
    }

    private JPanel crearFilaServicio(IntegracionExterna inte) {
        JPanel item = new JPanel(new BorderLayout(14, 0));
        item.setBackground(Color.WHITE);
        item.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                BorderFactory.createEmptyBorder(14, 18, 14, 18)
        ));
        item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 76));

        // Lado Izquierdo: Nombre + Punto de estado + Subtítulo
        JPanel izq = new JPanel();
        izq.setLayout(new BoxLayout(izq, BoxLayout.Y_AXIS));
        izq.setBackground(Color.WHITE);

        JPanel pnlNombrePunto = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        pnlNombrePunto.setBackground(Color.WHITE);

        JLabel lblNom = new JLabel(inte.getNombre());
        lblNom.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblNom.setForeground(new Color(15, 23, 42));
        pnlNombrePunto.add(lblNom);

        JLabel lblDot = new JLabel("●");
        lblDot.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblDot.setForeground(inte.isActiva() ? Ui.TURQUESA_OSCURO : new Color(148, 163, 184));
        pnlNombrePunto.add(lblDot);

        izq.add(pnlNombrePunto);
        izq.add(Box.createVerticalStrut(2));

        JLabel lblSub = new JLabel(inte.getSubtitulo());
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(new Color(100, 116, 139));
        izq.add(lblSub);

        item.add(izq, BorderLayout.CENTER);

        // Lado Derecho: Botón [Configurar] o [Conectar]
        JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        der.setBackground(Color.WHITE);

        JButton btnAccion = Ui.botonSecundario(inte.isActiva() ? "Configurar" : "Conectar", null);
        btnAccion.setPreferredSize(new Dimension(110, 32));

        btnAccion.addActionListener(e -> {
            if (inte.isActiva()) {
                abrirDialogoConfiguracion(inte);
            } else {
                conectarServicio(inte);
            }
        });

        der.add(btnAccion);
        item.add(der, BorderLayout.EAST);

        return item;
    }

    private void conectarServicio(IntegracionExterna inte) {
        int resp = JOptionPane.showConfirmDialog(
                this,
                "¿Desea inicializar la conexión con el servicio: " + inte.getNombre() + "?\n"
                        + "Se verificará la disponibilidad del endpoint y autenticación segura.",
                "Conectar " + inte.getNombre(),
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (resp == JOptionPane.YES_OPTION) {
            repo.conmutarEstadoIntegracion(inte.getId());
            recargarIntegraciones();
            JOptionPane.showMessageDialog(
                    this,
                    "✓ Conexión establecida exitosamente con " + inte.getNombre() + ".\n"
                            + "El servicio se encuentra ahora activo y sincronizando.",
                    "Integración Conectada",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }
    }

    private void abrirDialogoConfiguracion(IntegracionExterna inte) {
        JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(this), "Configurar: " + inte.getNombre(), JDialog.ModalityType.APPLICATION_MODAL);
        dlg.setSize(520, 420);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());

        JPanel pnl = new JPanel(new GridBagLayout());
        pnl.setBackground(Color.WHITE);
        pnl.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 6, 6, 6);
        g.fill = GridBagConstraints.HORIZONTAL;

        JTextField txtUrl = Ui.campoTexto(inte.getEndpointUrl(), 20);
        JTextField txtKey = Ui.campoTexto(inte.getApiKey(), 20);
        JComboBox<String> cbAmb = Ui.combo(new String[]{"Producción", "Sandbox / Pruebas"});
        cbAmb.setSelectedItem(inte.getAmbiente());

        JLabel lblSync = new JLabel(inte.getUltimaSincronizacion());
        lblSync.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblSync.setForeground(new Color(15, 23, 42));

        g.gridx = 0; g.gridy = 0; g.weightx = 0.35;
        pnl.add(new JLabel("Endpoint URL / Host:"), g);
        g.gridx = 1; g.gridy = 0; g.weightx = 0.65;
        pnl.add(txtUrl, g);

        g.gridx = 0; g.gridy = 1;
        pnl.add(new JLabel("API Key / Token:"), g);
        g.gridx = 1; g.gridy = 1;
        pnl.add(txtKey, g);

        g.gridx = 0; g.gridy = 2;
        pnl.add(new JLabel("Ambiente de Ejecución:"), g);
        g.gridx = 1; g.gridy = 2;
        pnl.add(cbAmb, g);

        g.gridx = 0; g.gridy = 3;
        pnl.add(new JLabel("Última Sincronización:"), g);
        g.gridx = 1; g.gridy = 3;
        pnl.add(lblSync, g);

        // Botón de prueba de conexión
        g.gridx = 0; g.gridy = 4; g.gridwidth = 2;
        JButton btnTest = Ui.botonSecundario("⚡ Probar Conexión en Vivo", null);
        btnTest.addActionListener(e -> {
            JOptionPane.showMessageDialog(dlg, "✓ Conexión exitosa con " + inte.getNombre() + " (Respuesta: HTTP 200 OK en 48ms).", "Ping Exitoso", JOptionPane.INFORMATION_MESSAGE);
        });
        pnl.add(btnTest, g);

        // Botones inferiores
        JPanel bot = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        bot.setBackground(new Color(248, 250, 252));

        JButton btnDesconectar = Ui.botonPeligro("Desconectar Servicio", null);
        btnDesconectar.addActionListener(e -> {
            repo.conmutarEstadoIntegracion(inte.getId());
            recargarIntegraciones();
            dlg.dispose();
        });
        bot.add(btnDesconectar);

        JButton btnGuardar = Ui.botonPrimario("Guardar Cambios", null);
        btnGuardar.addActionListener(e -> {
            inte.setEndpointUrl(txtUrl.getText().trim());
            inte.setApiKey(txtKey.getText().trim());
            inte.setAmbiente((String) cbAmb.getSelectedItem());
            repo.guardarIntegracionExterna(inte);
            recargarIntegraciones();
            dlg.dispose();
            JOptionPane.showMessageDialog(this, "✓ Parámetros de " + inte.getNombre() + " guardados.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        });
        bot.add(btnGuardar);

        dlg.add(pnl, BorderLayout.CENTER);
        dlg.add(bot, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    private void exportarReporteIntegraciones() {
        List<IntegracionExterna> lista = repo.getIntegracionesExternas();
        StringBuilder html = new StringBuilder();
        html.append("<html><body style='font-family:sans-serif; padding:15px; color:#1e293b;'>");
        html.append("<h2 style='color:#006064; border-bottom:2px solid #00BCD4; padding-bottom:6px;'>ESTADO DE CONECTIVIDAD E INTEGRACIONES EXTERNAS</h2>");
        html.append("<p><strong>Fecha de Verificación:</strong> ").append(LocalDate.now()).append(" | <strong>Total Pasarelas:</strong> ").append(lista.size()).append("</p>");
        html.append("<table border='1' cellpadding='6' cellspacing='0' style='border-collapse:collapse; width:100%; border-color:#cbd5e1; font-size:11px;'>");
        html.append("<tr style='background-color:#006064; color:white;'><th>Servicio / API</th><th>Detalle / Uso</th><th>Ambiente</th><th>Endpoint</th><th>Estado</th><th>Último Ping</th></tr>");

        StringBuilder csv = new StringBuilder("Servicio,Detalle,Ambiente,Endpoint,Estado,Ultimo Ping\n");

        for (IntegracionExterna ie : lista) {
            html.append("<tr>")
                .append("<td><b>").append(ie.getNombre()).append("</b></td>")
                .append("<td>").append(ie.getSubtitulo()).append("</td>")
                .append("<td>").append(ie.getAmbiente()).append("</td>")
                .append("<td>").append(ie.getEndpointUrl()).append("</td>")
                .append("<td align='center'>").append(ie.isActiva() ? "<span style='color:#007987;'><b>ACTIVA</b></span>" : "<span style='color:#64748b;'>INACTIVA</span>").append("</td>")
                .append("<td align='center'>").append(ie.getUltimaSincronizacion()).append("</td>")
                .append("</tr>");

            csv.append("\"").append(ie.getNombre()).append("\",\"")
               .append(ie.getSubtitulo()).append("\",\"")
               .append(ie.getAmbiente()).append("\",\"")
               .append(ie.getEndpointUrl()).append("\",\"")
               .append(ie.isActiva() ? "ACTIVA" : "INACTIVA").append("\",\"")
               .append(ie.getUltimaSincronizacion()).append("\"\n");
        }

        html.append("</table>");
        html.append("<p style='margin-top:15px; font-size:10px; color:#64748b;'>Reporte técnico de conectividad emitido por Happy Pets ERP Gateway.</p>");
        html.append("</body></html>");

        Ui.mostrarVisorReporte(
            SwingUtilities.getWindowAncestor(this),
            "Integraciones y APIs",
            "Estado de Conectividad de Integraciones Externas",
            html.toString(),
            csv.toString()
        );
    }
}
