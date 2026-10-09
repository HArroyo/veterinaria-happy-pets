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
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import happypets.data.RepositorioVeterinaria;
import happypets.model.NotificacionSistema;
import happypets.ui.Iconos;
import happypets.ui.Ui;

/**
 * Submódulo 9.1: Centro de Notificaciones
 * Responsable: Vera Aguilar, Carlos Edgardo
 * Bandeja de entrada unificada de alertas críticas, mensajes y eventos de auditoría,
 * con filtrado por categoría, búsqueda reactiva, visualización detallada y gestión de lectura.
 */
public class VistaCentroNotificacionesPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private final RepositorioVeterinaria repo = RepositorioVeterinaria.getInstancia();

    // Filtros
    private String categoriaSeleccionada = "Todas";
    private JButton btnFiltroAlertas;
    private JButton btnFiltroMensajes;
    private JButton btnFiltroEventos;
    private JButton btnFiltroTodas;
    private JTextField txtBuscar;

    // Contenedor de notificaciones
    private JPanel panelListaNotificaciones;
    private final List<TarjetaNotificacionUI> tarjetasUI = new ArrayList<>();
    private NotificacionSistema notificacionSeleccionada = null;

    public VistaCentroNotificacionesPanel() {
        setLayout(new BorderLayout(0, 16));
        setBackground(new Color(248, 250, 252));
        setBorder(BorderFactory.createEmptyBorder(20, 24, 24, 24));

        add(crearCabeceraSuperior(), BorderLayout.NORTH);

        JPanel panelCentro = new JPanel(new BorderLayout(0, 14));
        panelCentro.setOpaque(false);
        panelCentro.add(crearBarraFiltrosYBuscador(), BorderLayout.NORTH);

        panelListaNotificaciones = new JPanel();
        panelListaNotificaciones.setLayout(new BoxLayout(panelListaNotificaciones, BoxLayout.Y_AXIS));
        panelListaNotificaciones.setBackground(new Color(248, 250, 252));

        JScrollPane scroll = new JScrollPane(panelListaNotificaciones);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setBackground(new Color(248, 250, 252));
        panelCentro.add(scroll, BorderLayout.CENTER);

        panelCentro.add(crearBarraAccionesInferiores(), BorderLayout.SOUTH);

        add(panelCentro, BorderLayout.CENTER);

        recargarLista();
    }

    private JPanel crearCabeceraSuperior() {
        JPanel cab = new JPanel(new BorderLayout(16, 8));
        cab.setBackground(new Color(30, 41, 59)); // Cabecera oscura elegante
        cab.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(15, 23, 42), 1),
                BorderFactory.createEmptyBorder(14, 20, 14, 20)
        ));

        // Título
        JPanel pnlTit = new JPanel();
        pnlTit.setLayout(new BoxLayout(pnlTit, BoxLayout.Y_AXIS));
        pnlTit.setOpaque(false);

        JLabel lblTit = new JLabel("Submódulo 1: Centro de Notificaciones");
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTit.setForeground(Color.WHITE);
        pnlTit.add(lblTit);

        JLabel lblSub = new JLabel("Monitoreo en vivo de incidentes de seguridad, alertas de capacidad y mensajes del sistema.");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblSub.setForeground(new Color(148, 163, 184));
        pnlTit.add(lblSub);

        cab.add(pnlTit, BorderLayout.WEST);

        // Bloque de usuario
        JPanel pnlUsr = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        pnlUsr.setOpaque(false);

        JLabel lblUsr = new JLabel("Usuario: Admin 👤");
        lblUsr.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblUsr.setForeground(new Color(226, 232, 240));
        pnlUsr.add(lblUsr);

        cab.add(pnlUsr, BorderLayout.EAST);
        return cab;
    }

    private JPanel crearBarraFiltrosYBuscador() {
        JPanel pnl = new JPanel(new BorderLayout(12, 0));
        pnl.setOpaque(false);

        // Botones de filtro de categorías
        JPanel pnlPestanas = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        pnlPestanas.setOpaque(false);

        btnFiltroTodas = crearBotonPestana("Todas (" + repo.getNotificaciones().size() + ")", "Todas");
        btnFiltroAlertas = crearBotonPestana("Alertas (" + repo.contarNotificacionesPorCategoria("Alerta") + ")", "Alerta");
        btnFiltroMensajes = crearBotonPestana("Mensajes (" + repo.contarNotificacionesPorCategoria("Mensaje") + ")", "Mensaje");
        btnFiltroEventos = crearBotonPestana("Eventos (" + repo.contarNotificacionesPorCategoria("Evento") + ")", "Evento");

        pnlPestanas.add(btnFiltroTodas);
        pnlPestanas.add(btnFiltroAlertas);
        pnlPestanas.add(btnFiltroMensajes);
        pnlPestanas.add(btnFiltroEventos);

        actualizarEstiloPestanas();
        pnl.add(pnlPestanas, BorderLayout.WEST);

        // Buscador
        JPanel pnlBuscar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        pnlBuscar.setOpaque(false);

        txtBuscar = new JTextField(18);
        txtBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtBuscar.setPreferredSize(new Dimension(220, 32));
        txtBuscar.setToolTipText("Buscar en notificaciones...");
        txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { recargarLista(); }
            @Override public void removeUpdate(DocumentEvent e) { recargarLista(); }
            @Override public void changedUpdate(DocumentEvent e) { recargarLista(); }
        });

        pnlBuscar.add(new JLabel(Iconos.crearIconoBuscar(14, new Color(100, 116, 139))));
        pnlBuscar.add(txtBuscar);

        pnl.add(pnlBuscar, BorderLayout.EAST);
        return pnl;
    }

    private JButton crearBotonPestana(String texto, String cat) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> {
            categoriaSeleccionada = cat;
            actualizarEstiloPestanas();
            recargarLista();
        });
        return btn;
    }

    private void actualizarEstiloPestanas() {
        estilizarPestana(btnFiltroTodas, "Todas".equals(categoriaSeleccionada));
        estilizarPestana(btnFiltroAlertas, "Alerta".equals(categoriaSeleccionada));
        estilizarPestana(btnFiltroMensajes, "Mensaje".equals(categoriaSeleccionada));
        estilizarPestana(btnFiltroEventos, "Evento".equals(categoriaSeleccionada));
    }

    private void estilizarPestana(JButton btn, boolean activa) {
        if (btn == null) return;
        if (activa) {
            btn.setBackground(new Color(2, 132, 199));
            btn.setForeground(Color.WHITE);
            btn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(3, 105, 161), 1),
                    BorderFactory.createEmptyBorder(6, 16, 6, 16)
            ));
        } else {
            btn.setBackground(Color.WHITE);
            btn.setForeground(new Color(71, 85, 105));
            btn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(203, 213, 225), 1),
                    BorderFactory.createEmptyBorder(6, 16, 6, 16)
            ));
        }
    }

    private JPanel crearBarraAccionesInferiores() {
        JPanel bot = new JPanel(new BorderLayout(14, 0));
        bot.setOpaque(false);
        bot.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));

        JPanel izq = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        izq.setOpaque(false);

        JButton btnDetalle = Ui.botonSecundario("Ver Detalle", Iconos.crearIconoDocumento(13, new Color(15, 23, 42)));
        btnDetalle.setPreferredSize(new Dimension(135, 34));
        btnDetalle.addActionListener(e -> mostrarDetalleNotificacion());
        izq.add(btnDetalle);

        JButton btnMarcarLeida = Ui.botonSecundario("Marcar como Leída", Iconos.crearIconoRefrescar(12, new Color(15, 23, 42)));
        btnMarcarLeida.setPreferredSize(new Dimension(165, 34));
        btnMarcarLeida.addActionListener(e -> marcarSeleccionadaLeida());
        izq.add(btnMarcarLeida);

        bot.add(izq, BorderLayout.WEST);

        JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        der.setOpaque(false);

        JButton btnLimpiar = Ui.botonSecundario("Limpiar Alertas Leídas", null);
        btnLimpiar.setPreferredSize(new Dimension(175, 34));
        btnLimpiar.addActionListener(e -> {
            repo.limpiarAlertasLeidas();
            recargarLista();
            JOptionPane.showMessageDialog(this,
                    "Se han depurado las notificaciones marcadas como leídas.",
                    "Limpieza de Alertas", JOptionPane.INFORMATION_MESSAGE);
        });
        der.add(btnLimpiar);

        bot.add(der, BorderLayout.EAST);
        return bot;
    }

    private void recargarLista() {
        panelListaNotificaciones.removeAll();
        tarjetasUI.clear();

        String q = txtBuscar != null ? txtBuscar.getText().trim().toLowerCase() : "";
        List<NotificacionSistema> todas = repo.getNotificacionesPorCategoria(categoriaSeleccionada);

        List<NotificacionSistema> filtradas = new ArrayList<>();
        for (NotificacionSistema n : todas) {
            if (q.isEmpty() || n.getTitulo().toLowerCase().contains(q)
                    || n.getMensaje().toLowerCase().contains(q)
                    || n.getTipo().toLowerCase().contains(q)) {
                filtradas.add(n);
            }
        }

        if (filtradas.isEmpty()) {
            JPanel pnlVacio = new JPanel();
            pnlVacio.setBackground(Color.WHITE);
            pnlVacio.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                    BorderFactory.createEmptyBorder(30, 20, 30, 20)
            ));
            JLabel lblV = new JLabel("No se encontraron notificaciones en esta categoría.");
            lblV.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            lblV.setForeground(new Color(100, 116, 139));
            pnlVacio.add(lblV);
            panelListaNotificaciones.add(pnlVacio);
        } else {
            for (NotificacionSistema notif : filtradas) {
                TarjetaNotificacionUI t = new TarjetaNotificacionUI(notif);
                tarjetasUI.add(t);
                panelListaNotificaciones.add(t);
                panelListaNotificaciones.add(Box.createVerticalStrut(10));
            }
        }

        // Actualizar contadores en botones
        if (btnFiltroTodas != null) btnFiltroTodas.setText("Todas (" + repo.getNotificaciones().size() + ")");
        if (btnFiltroAlertas != null) btnFiltroAlertas.setText("Alertas (" + repo.contarNotificacionesPorCategoria("Alerta") + ")");
        if (btnFiltroMensajes != null) btnFiltroMensajes.setText("Mensajes (" + repo.contarNotificacionesPorCategoria("Mensaje") + ")");
        if (btnFiltroEventos != null) btnFiltroEventos.setText("Eventos (" + repo.contarNotificacionesPorCategoria("Evento") + ")");

        panelListaNotificaciones.revalidate();
        panelListaNotificaciones.repaint();
    }

    private void seleccionarTarjeta(TarjetaNotificacionUI tarjeta) {
        for (TarjetaNotificacionUI t : tarjetasUI) {
            t.setSeleccionada(t == tarjeta);
        }
        notificacionSeleccionada = tarjeta != null ? tarjeta.notificacion : null;
    }

    private void mostrarDetalleNotificacion() {
        if (notificacionSeleccionada == null) {
            JOptionPane.showMessageDialog(this,
                    "Por favor, seleccione una notificación de la lista para ver su detalle.",
                    "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(this), "Detalle de Notificación - " + notificacionSeleccionada.getId(), JDialog.ModalityType.APPLICATION_MODAL);
        dlg.setSize(520, 380);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());

        JPanel pnl = new JPanel();
        pnl.setLayout(new BoxLayout(pnl, BoxLayout.Y_AXIS));
        pnl.setBackground(Color.WHITE);
        pnl.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        JLabel lblTit = new JLabel(notificacionSeleccionada.getTitulo());
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTit.setForeground(new Color(15, 23, 42));
        pnl.add(lblTit);
        pnl.add(Box.createVerticalStrut(12));

        JPanel grid = new JPanel(new GridLayout(3, 2, 10, 8));
        grid.setOpaque(false);
        grid.add(crearItemInfo("Identificador:", notificacionSeleccionada.getId()));
        grid.add(crearItemInfo("Tipo / Etiqueta:", notificacionSeleccionada.getTipo()));
        grid.add(crearItemInfo("Categoría:", notificacionSeleccionada.getCategoria()));
        grid.add(crearItemInfo("Fecha y Hora:", notificacionSeleccionada.getFechaFormateada()));
        grid.add(crearItemInfo("Módulo de Origen:", notificacionSeleccionada.getOrigen()));
        grid.add(crearItemInfo("Estado de Lectura:", notificacionSeleccionada.isLeida() ? "Leída" : "No leída"));
        pnl.add(grid);
        pnl.add(Box.createVerticalStrut(16));

        JLabel lblDescTit = new JLabel("Mensaje Completo:");
        lblDescTit.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblDescTit.setForeground(new Color(100, 116, 139));
        pnl.add(lblDescTit);
        pnl.add(Box.createVerticalStrut(4));

        JLabel lblMsg = new JLabel("<html><body style='width: 380px;'>" + notificacionSeleccionada.getMensaje() + "</body></html>");
        lblMsg.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblMsg.setForeground(new Color(30, 41, 59));
        pnl.add(lblMsg);

        dlg.add(pnl, BorderLayout.CENTER);

        JPanel bot = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        bot.setBackground(new Color(248, 250, 252));
        bot.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(226, 232, 240)));

        JButton btnMarcar = Ui.botonSecundario("Marcar Leída", null);
        btnMarcar.addActionListener(e -> {
            repo.marcarNotificacionComoLeida(notificacionSeleccionada.getId());
            dlg.dispose();
            recargarLista();
        });

        JButton btnCerrar = Ui.botonPrimario("Cerrar", null);
        btnCerrar.addActionListener(e -> dlg.dispose());

        bot.add(btnMarcar);
        bot.add(btnCerrar);
        dlg.add(bot, BorderLayout.SOUTH);

        dlg.setVisible(true);
    }

    private JPanel crearItemInfo(String campo, String val) {
        JPanel p = new JPanel(new BorderLayout(4, 2));
        p.setOpaque(false);
        JLabel c = new JLabel(campo);
        c.setFont(new Font("Segoe UI", Font.BOLD, 11));
        c.setForeground(new Color(100, 116, 139));
        p.add(c, BorderLayout.NORTH);

        JLabel v = new JLabel(val);
        v.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        v.setForeground(new Color(15, 23, 42));
        p.add(v, BorderLayout.CENTER);
        return p;
    }

    private void marcarSeleccionadaLeida() {
        if (notificacionSeleccionada == null) {
            JOptionPane.showMessageDialog(this,
                    "Por favor, seleccione una notificación para marcar como leída.",
                    "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }
        repo.marcarNotificacionComoLeida(notificacionSeleccionada.getId());
        recargarLista();
        JOptionPane.showMessageDialog(this,
                "Notificación marcada como leída.",
                "Actualización", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Tarjeta visual para cada fila de notificación del wireframe.
     */
    private class TarjetaNotificacionUI extends JPanel {
        private static final long serialVersionUID = 1L;
        private final NotificacionSistema notificacion;
        private boolean seleccionada = false;

        TarjetaNotificacionUI(NotificacionSistema notificacion) {
            this.notificacion = notificacion;
            setLayout(new BorderLayout(12, 6));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            actualizarEstilo();

            // Fila superior: Badge de tipo + tiempo relativo
            JPanel top = new JPanel(new BorderLayout());
            top.setOpaque(false);

            JPanel izqBadge = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            izqBadge.setOpaque(false);

            JLabel badgeTipo = new JLabel("  " + notificacion.getTipo() + "  ");
            badgeTipo.setFont(new Font("Segoe UI", Font.BOLD, 10));
            badgeTipo.setOpaque(true);

            Color colorBadge = obtenerColorTipo(notificacion.getTipo());
            badgeTipo.setBackground(new Color(colorBadge.getRed(), colorBadge.getGreen(), colorBadge.getBlue(), 30));
            badgeTipo.setForeground(colorBadge);
            badgeTipo.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));
            izqBadge.add(badgeTipo);
            top.add(izqBadge, BorderLayout.WEST);

            JLabel lblTiempo = new JLabel(notificacion.getTiempoRelativo());
            lblTiempo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            lblTiempo.setForeground(new Color(148, 163, 184));
            top.add(lblTiempo, BorderLayout.EAST);
            add(top, BorderLayout.NORTH);

            // Mensaje principal
            JLabel lblMensaje = new JLabel(notificacion.getMensaje());
            lblMensaje.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            lblMensaje.setForeground(new Color(30, 41, 59));
            add(lblMensaje, BorderLayout.CENTER);

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    seleccionarTarjeta(TarjetaNotificacionUI.this);
                    if (e.getClickCount() == 2) {
                        mostrarDetalleNotificacion();
                    }
                }
            });
        }

        void setSeleccionada(boolean sel) {
            this.seleccionada = sel;
            actualizarEstilo();
            repaint();
        }

        private void actualizarEstilo() {
            if (seleccionada) {
                setBackground(new Color(240, 249, 255));
                setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(2, 132, 199), 2),
                        BorderFactory.createEmptyBorder(10, 14, 10, 14)
                ));
            } else {
                setBackground(notificacion.isLeida() ? new Color(248, 250, 252) : Color.WHITE);
                setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                        BorderFactory.createEmptyBorder(11, 15, 11, 15)
                ));
            }
        }

        private Color obtenerColorTipo(String tipo) {
            if (tipo == null) return new Color(100, 116, 139);
            if (tipo.contains("ALERTA")) return new Color(220, 38, 38);       // Rojo
            if (tipo.contains("IMPORTANTE")) return new Color(79, 70, 229);    // Índigo
            if (tipo.contains("MENSAJE")) return new Color(2, 132, 199);       // Azul cielo
            if (tipo.contains("CLÍNICO")) return new Color(16, 185, 129);      // Verde esmeralda
            return new Color(100, 116, 139);                                   // Slate
        }
    }
}
