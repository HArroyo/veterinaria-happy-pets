package happypets.modulos.modulo10;

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
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
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
import happypets.model.RolPermiso;
import happypets.model.Usuario;
import happypets.ui.Iconos;
import happypets.ui.Ui;

/**
 * Submódulo 10.2: Gestión de Usuarios, Roles y Control de Acceso
 * Responsable: Minaya Bravo, Almendra Lili
 * Matriz de privilegios, credenciales institucionales y seguridad por perfil
 * con arquitectura Master-Detail, 4 KPIs de cuentas y dock de seguridad en tiempo real.
 */
public class VistaUsuariosRolesPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private final RepositorioVeterinaria repo = RepositorioVeterinaria.getInstancia();

    // KPIs Superiores
    private JLabel lblKpiTotalUsuarios;
    private JLabel lblKpiUsuariosActivos;
    private JLabel lblKpiRolesDefinidos;
    private JLabel lblKpiSesionesActivas;

    // Master Table (Izquierda)
    private JTable tablaUsuarios;
    private DefaultTableModel modeloUsuarios;
    private JTextField txtBuscar;
    private String filtroRol = "TODOS";
    private final List<Usuario> listaUsuariosFiltrada = new ArrayList<>();
    private JPanel panelPills;

    // Live Detail Hub (Derecha - 360px)
    private JPanel panelHubContenido;
    private JPanel panelHubVacio;
    private JPanel panelHubDetalle;

    private JLabel lblHubAvatar;
    private JLabel lblHubNombre;
    private JLabel lblHubUsername;
    private JLabel lblHubChipEstado;

    // Telemetría 2x2
    private JLabel lblHubRol;
    private JLabel lblHubDepto;
    private JLabel lblHubCorreo;
    private JLabel lblHubEstado;

    // Permisos y Detalles
    private JTextArea txtHubPermisosResumen;
    private JLabel lblHubSeguridadNota;

    private Usuario usuarioSeleccionado;

    public VistaUsuariosRolesPanel() {
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

        JLabel titulo = new JLabel("Usuarios Activos, Roles y Permisos");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 17));
        titulo.setForeground(Ui.TEXTO_TITULO);

        JLabel subtitulo = new JLabel("Módulo 10.2 · Control de acceso, asignación de perfiles, credenciales y matriz de seguridad");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitulo.setForeground(Ui.TEXTO_MUTED);

        izq.add(titulo);
        izq.add(Box.createVerticalStrut(2));
        izq.add(subtitulo);
        cab.add(izq, BorderLayout.WEST);

        JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        der.setOpaque(false);

        JButton btnExportar = Ui.boton("Exportar Directorio", false);
        btnExportar.addActionListener(e -> exportarDirectorioUsuarios());
        der.add(btnExportar);

        JButton btnGestionarRoles = Ui.boton("Gestionar Roles", false);
        btnGestionarRoles.addActionListener(e -> abrirDialogoGestionRoles());
        der.add(btnGestionarRoles);

        JButton btnNuevoUsuario = Ui.boton("+ Nuevo Usuario", true);
        btnNuevoUsuario.addActionListener(e -> abrirDialogoUsuario(null));
        der.add(btnNuevoUsuario);

        cab.add(der, BorderLayout.EAST);
        return cab;
    }

    private JPanel crearFilaKpis() {
        JPanel kpis = new JPanel(new GridLayout(1, 4, 12, 0));
        kpis.setOpaque(false);
        kpis.setPreferredSize(new Dimension(0, 74));
        kpis.setMaximumSize(new Dimension(Integer.MAX_VALUE, 74));

        lblKpiTotalUsuarios = new JLabel("0");
        lblKpiUsuariosActivos = new JLabel("0");
        lblKpiRolesDefinidos = new JLabel("0");
        lblKpiSesionesActivas = new JLabel("3 Conectados");

        kpis.add(crearTarjetaKpi("Usuarios Totales", lblKpiTotalUsuarios, "Cuentas en el sistema", Iconos.crearIconoUsuario(22, Ui.TURQUESA)));
        kpis.add(crearTarjetaKpi("Personal en Servicio", lblKpiUsuariosActivos, "Cuentas activas habilitadas", Iconos.crearIconoCheck(22, Ui.VERDE_EXITO)));
        kpis.add(crearTarjetaKpi("Perfiles y Roles", lblKpiRolesDefinidos, "Matriz de privilegios RBAC", Iconos.crearIconoHistorial(22, Ui.AZUL_INFO)));
        kpis.add(crearTarjetaKpi("Sesiones en Vivo", lblKpiSesionesActivas, "Operadores en línea ahora", Iconos.crearIconoAlertaTriaje(22, Ui.NARANJA_AVISO)));

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
        txtBuscar.putClientProperty("JTextField.placeholderText", "Buscar por nombre, usuario, correo o departamento...");
        txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filtrarUsuarios(); }
            public void removeUpdate(DocumentEvent e) { filtrarUsuarios(); }
            public void changedUpdate(DocumentEvent e) { filtrarUsuarios(); }
        });
        barraBuscar.add(txtBuscar, BorderLayout.CENTER);
        cabFiltros.add(barraBuscar);
        cabFiltros.add(Box.createVerticalStrut(8));

        // Píldoras de Filtro por Rol
        panelPills = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        panelPills.setOpaque(false);
        actualizarPildorasFiltro();
        cabFiltros.add(panelPills);

        master.add(cabFiltros, BorderLayout.NORTH);

        // Tabla de Usuarios
        String[] cols = {"Colaborador", "Usuario @", "Rol Asignado", "Área / Departamento", "Correo Electrónico", "Estado"};
        modeloUsuarios = new DefaultTableModel(cols, 0) {
            private static final long serialVersionUID = 1L;
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tablaUsuarios = new JTable(modeloUsuarios);
        tablaUsuarios.setRowHeight(46);
        tablaUsuarios.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaUsuarios.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaUsuarios.setShowVerticalLines(false);
        tablaUsuarios.setGridColor(new Color(241, 245, 249));
        tablaUsuarios.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        tablaUsuarios.getTableHeader().setBackground(new Color(248, 250, 252));
        tablaUsuarios.getTableHeader().setForeground(Ui.TEXTO_MUTED);

        tablaUsuarios.getColumnModel().getColumn(0).setPreferredWidth(190);
        tablaUsuarios.getColumnModel().getColumn(1).setPreferredWidth(100);
        tablaUsuarios.getColumnModel().getColumn(2).setPreferredWidth(130);
        tablaUsuarios.getColumnModel().getColumn(3).setPreferredWidth(140);
        tablaUsuarios.getColumnModel().getColumn(4).setPreferredWidth(170);
        tablaUsuarios.getColumnModel().getColumn(5).setPreferredWidth(95);

        // Avatar monograma en columna 0
        tablaUsuarios.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
            private static final long serialVersionUID = 1L;
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                JPanel cell = new JPanel(new BorderLayout(8, 0));
                cell.setOpaque(true);
                cell.setBackground(isSelected ? table.getSelectionBackground() : (row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252)));
                cell.setBorder(new EmptyBorder(0, 8, 0, 8));

                String nombre = value != null ? value.toString() : "";
                String iniciales = obtenerIniciales(nombre);

                JLabel avatar = new JLabel(iniciales, SwingConstants.CENTER) {
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
                        g2.setColor(Ui.TURQUESA_MEDIO);
                        g2.drawOval(x, y, s, s);
                        super.paintComponent(g);
                        g2.dispose();
                    }
                };
                avatar.setPreferredSize(new Dimension(30, 30));
                avatar.setFont(new Font("Segoe UI", Font.BOLD, 10));
                avatar.setForeground(Ui.TURQUESA_PROFUNDO);

                JLabel lblNombre = new JLabel(nombre);
                lblNombre.setFont(new Font("Segoe UI", Font.BOLD, 12));
                lblNombre.setForeground(isSelected ? table.getSelectionForeground() : Ui.TEXTO_TITULO);

                cell.add(avatar, BorderLayout.WEST);
                cell.add(lblNombre, BorderLayout.CENTER);
                return cell;
            }
        });

        // Chip de Rol en Columna 2
        tablaUsuarios.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            private static final long serialVersionUID = 1L;
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 11));
                p.setOpaque(true);
                p.setBackground(isSelected ? table.getSelectionBackground() : (row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252)));

                String rol = value != null ? value.toString() : "";
                JLabel badge = new JLabel(" " + rol + " ");
                badge.setOpaque(true);
                badge.setFont(new Font("Segoe UI", Font.BOLD, 10));

                if (rol.contains("Admin")) {
                    badge.setBackground(new Color(254, 242, 242));
                    badge.setForeground(new Color(220, 38, 38));
                } else if (rol.contains("Veterinario")) {
                    badge.setBackground(Ui.TURQUESA_SUAVE);
                    badge.setForeground(Ui.TURQUESA_PROFUNDO);
                } else if (rol.contains("Contador")) {
                    badge.setBackground(new Color(236, 253, 245));
                    badge.setForeground(new Color(16, 185, 129));
                } else {
                    badge.setBackground(new Color(241, 245, 249));
                    badge.setForeground(new Color(71, 85, 105));
                }

                badge.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(badge.getBackground().darker(), 1, true),
                        new EmptyBorder(2, 6, 2, 6)
                ));
                p.add(badge);
                return p;
            }
        });

        // Chip en columna 5 (Estado: Activo / Inactivo)
        tablaUsuarios.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            private static final long serialVersionUID = 1L;
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 11));
                p.setOpaque(true);
                p.setBackground(isSelected ? table.getSelectionBackground() : (row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252)));

                String st = value != null ? value.toString() : "Activo";
                JLabel badge = new JLabel(" " + st + " ");
                badge.setOpaque(true);
                badge.setFont(new Font("Segoe UI", Font.BOLD, 10));

                if (st.equalsIgnoreCase("Activo")) {
                    badge.setBackground(new Color(220, 252, 231));
                    badge.setForeground(new Color(21, 128, 61));
                } else {
                    badge.setBackground(new Color(241, 245, 249));
                    badge.setForeground(new Color(148, 163, 184));
                }
                badge.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(badge.getBackground().darker(), 1, true),
                        new EmptyBorder(2, 6, 2, 6)
                ));
                p.add(badge);
                return p;
            }
        });

        tablaUsuarios.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int r = tablaUsuarios.getSelectedRow();
                if (r >= 0 && r < listaUsuariosFiltrada.size()) {
                    mostrarDetalleUsuario(listaUsuariosFiltrada.get(r));
                } else {
                    mostrarDetalleUsuario(null);
                }
            }
        });

        tablaUsuarios.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int r = tablaUsuarios.getSelectedRow();
                    if (r >= 0 && r < listaUsuariosFiltrada.size()) {
                        abrirDialogoUsuario(listaUsuariosFiltrada.get(r));
                    }
                }
            }
        });

        JScrollPane scroll = new JScrollPane(tablaUsuarios);
        scroll.setBorder(BorderFactory.createLineBorder(Ui.BORDE_SUAVE, 1));
        master.add(scroll, BorderLayout.CENTER);

        return master;
    }

    private void actualizarPildorasFiltro() {
        if (panelPills == null) return;
        panelPills.removeAll();

        String[] roles = {"TODOS", "Administrador", "Veterinario", "Recepcionista", "Auxiliar", "Contador"};

        for (String r : roles) {
            JButton btnPill = new JButton(r);
            btnPill.setFont(new Font("Segoe UI", Font.BOLD, 11));
            btnPill.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btnPill.setBorder(new EmptyBorder(4, 10, 4, 10));
            btnPill.setFocusPainted(false);

            if (r.equalsIgnoreCase(filtroRol)) {
                btnPill.setBackground(Ui.TURQUESA);
                btnPill.setForeground(Color.WHITE);
            } else {
                btnPill.setBackground(new Color(241, 245, 249));
                btnPill.setForeground(Ui.TEXTO_MUTED);
            }

            btnPill.addActionListener(e -> {
                filtroRol = r;
                actualizarPildorasFiltro();
                filtrarUsuarios();
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

        JLabel ico = new JLabel(Iconos.crearIconoUsuario(48, Ui.BORDE_SUAVE));
        ico.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel tit = new JLabel("Ningún Colaborador Seleccionado");
        tit.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tit.setForeground(Ui.TEXTO_TITULO);
        tit.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sub = new JLabel("<html><center>Seleccione un usuario de la tabla para visualizar su perfil, roles asignados, matriz de privilegios y gestión de claves.</center></html>");
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

        lblHubAvatar = new JLabel("US", SwingConstants.CENTER) {
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

        lblHubNombre = new JLabel("Nombre Colaborador");
        lblHubNombre.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblHubNombre.setForeground(Ui.TEXTO_TITULO);
        lblHubNombre.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblHubUsername = new JLabel("@usuario");
        lblHubUsername.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblHubUsername.setForeground(Ui.TEXTO_MUTED);
        lblHubUsername.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel rowChips = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        rowChips.setOpaque(false);
        rowChips.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblHubChipEstado = new JLabel(" Activo en Turno ");
        lblHubChipEstado.setOpaque(true);
        lblHubChipEstado.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblHubChipEstado.setBackground(new Color(220, 252, 231));
        lblHubChipEstado.setForeground(new Color(21, 128, 61));
        lblHubChipEstado.setBorder(BorderFactory.createLineBorder(new Color(134, 239, 172), 1, true));
        rowChips.add(lblHubChipEstado);

        heroText.add(lblHubNombre);
        heroText.add(Box.createVerticalStrut(2));
        heroText.add(lblHubUsername);
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

        lblHubRol = new JLabel("-");
        lblHubDepto = new JLabel("-");
        lblHubCorreo = new JLabel("-");
        lblHubEstado = new JLabel("-");

        telemetria.add(crearMicroCard("Rol de Acceso", lblHubRol));
        telemetria.add(crearMicroCard("Departamento", lblHubDepto));
        telemetria.add(crearMicroCard("Correo Corporativo", lblHubCorreo));
        telemetria.add(crearMicroCard("Estado de Cuenta", lblHubEstado));

        scrollContent.add(telemetria);
        scrollContent.add(Box.createVerticalStrut(12));

        // 3. Card Matriz de Privilegios
        txtHubPermisosResumen = new JTextArea();
        txtHubPermisosResumen.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        txtHubPermisosResumen.setForeground(Ui.TEXTO_TITULO);
        txtHubPermisosResumen.setLineWrap(true);
        txtHubPermisosResumen.setWrapStyleWord(true);
        txtHubPermisosResumen.setEditable(false);
        txtHubPermisosResumen.setOpaque(false);

        JPanel pnlPermisos = crearSeccionCard("Matriz de Permisos del Rol", txtHubPermisosResumen);
        pnlPermisos.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollContent.add(pnlPermisos);
        scrollContent.add(Box.createVerticalStrut(10));

        // 4. Card Seguridad y Claves
        JPanel pnlSeg = new JPanel(new GridLayout(2, 1, 4, 4));
        pnlSeg.setOpaque(false);

        lblHubSeguridadNota = new JLabel("Autenticación: 2FA Opcional Habilitada");
        lblHubSeguridadNota.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblHubSeguridadNota.setForeground(Ui.TEXTO_MUTED);

        JLabel lblPassPolicy = new JLabel("Expiración de Contraseña: Cada 90 días");
        lblPassPolicy.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblPassPolicy.setForeground(Ui.TEXTO_MUTED);

        pnlSeg.add(lblHubSeguridadNota);
        pnlSeg.add(lblPassPolicy);

        JPanel pnlSegCard = crearSeccionCard("Políticas de Seguridad y Sesión", pnlSeg);
        pnlSegCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollContent.add(pnlSegCard);
        scrollContent.add(Box.createVerticalStrut(14));

        // 5. Botonera 2x2
        JPanel gridAcciones = new JPanel(new GridLayout(2, 2, 8, 8));
        gridAcciones.setOpaque(false);
        gridAcciones.setAlignmentX(Component.LEFT_ALIGNMENT);
        gridAcciones.setMaximumSize(new Dimension(Integer.MAX_VALUE, 74));

        JButton btnEditar = Ui.botonSecundario("Editar Usuario", Iconos.crearIconoUsuario(12, Ui.TEXTO_TITULO));
        btnEditar.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnEditar.addActionListener(e -> {
            if (usuarioSeleccionado != null) abrirDialogoUsuario(usuarioSeleccionado);
        });

        JButton btnResetPass = Ui.botonSecundario("Restablecer Clave", null);
        btnResetPass.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnResetPass.addActionListener(e -> {
            if (usuarioSeleccionado != null) {
                String nueva = JOptionPane.showInputDialog(this, "Ingrese la nueva contraseña provisional para @" + usuarioSeleccionado.getUsername() + ":", "Reset de Contraseña", JOptionPane.QUESTION_MESSAGE);
                if (nueva != null && !nueva.trim().isEmpty()) {
                    usuarioSeleccionado.setPassword(nueva.trim());
                    JOptionPane.showMessageDialog(this, "Contraseña actualizada exitosamente para @" + usuarioSeleccionado.getUsername(), "Éxito", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        JButton btnRoles = Ui.botonSecundario("Gestionar Roles", null);
        btnRoles.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnRoles.addActionListener(e -> abrirDialogoGestionRoles());

        JButton btnToggleEstado = Ui.botonPrimario("Alternar Estado", null);
        btnToggleEstado.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnToggleEstado.addActionListener(e -> {
            if (usuarioSeleccionado != null) {
                String nuevoSt = "Activo".equalsIgnoreCase(usuarioSeleccionado.getEstado()) ? "Inactivo" : "Activo";
                usuarioSeleccionado.setEstado(nuevoSt);
                recargarDatos();
            }
        });

        gridAcciones.add(btnEditar);
        gridAcciones.add(btnResetPass);
        gridAcciones.add(btnRoles);
        gridAcciones.add(btnToggleEstado);

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

    private String obtenerIniciales(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) return "US";
        String[] partes = nombre.trim().split("\\s+");
        if (partes.length == 1) return partes[0].substring(0, Math.min(2, partes[0].length())).toUpperCase();
        return ("" + partes[0].charAt(0) + partes[1].charAt(0)).toUpperCase();
    }

    public void recargarDatos() {
        int tot = repo.getUsuariosSistema().size();
        int act = 0;
        for (Usuario u : repo.getUsuariosSistema()) {
            if ("Activo".equalsIgnoreCase(u.getEstado())) act++;
        }
        int roles = repo.getRolesPermisos().size();

        lblKpiTotalUsuarios.setText(tot + " Cuentas");
        lblKpiUsuariosActivos.setText(act + " En Servicio");
        lblKpiRolesDefinidos.setText(roles + " Perfiles");

        actualizarPildorasFiltro();
        filtrarUsuarios();
    }

    private void filtrarUsuarios() {
        String q = txtBuscar != null ? txtBuscar.getText().trim().toLowerCase() : "";
        List<Usuario> todos = repo.getUsuariosSistema();

        listaUsuariosFiltrada.clear();
        modeloUsuarios.setRowCount(0);

        for (Usuario u : todos) {
            boolean coincideRol = filtroRol == null || "TODOS".equalsIgnoreCase(filtroRol)
                    || u.getRol().toLowerCase().contains(filtroRol.toLowerCase());

            boolean coincideTexto = q.isEmpty()
                    || u.getNombreCompleto().toLowerCase().contains(q)
                    || u.getUsername().toLowerCase().contains(q)
                    || u.getCorreo().toLowerCase().contains(q)
                    || u.getEspecialidadArea().toLowerCase().contains(q);

            if (coincideRol && coincideTexto) {
                listaUsuariosFiltrada.add(u);
                modeloUsuarios.addRow(new Object[]{
                        u.getNombreCompleto(),
                        "@" + u.getUsername(),
                        u.getRol(),
                        u.getEspecialidadArea(),
                        u.getCorreo(),
                        u.getEstado()
                });
            }
        }

        if (listaUsuariosFiltrada.isEmpty()) {
            mostrarDetalleUsuario(null);
        } else {
            int sel = tablaUsuarios.getSelectedRow();
            if (sel < 0 || sel >= listaUsuariosFiltrada.size()) {
                tablaUsuarios.setRowSelectionInterval(0, 0);
                mostrarDetalleUsuario(listaUsuariosFiltrada.get(0));
            } else {
                mostrarDetalleUsuario(listaUsuariosFiltrada.get(sel));
            }
        }
    }

    private void mostrarDetalleUsuario(Usuario u) {
        this.usuarioSeleccionado = u;
        panelHubContenido.removeAll();

        if (u == null) {
            panelHubContenido.add(panelHubVacio, BorderLayout.CENTER);
        } else {
            lblHubAvatar.setText(obtenerIniciales(u.getNombreCompleto()));
            lblHubNombre.setText(u.getNombreCompleto());
            lblHubUsername.setText("@" + u.getUsername());

            if ("Activo".equalsIgnoreCase(u.getEstado())) {
                lblHubChipEstado.setText(" Cuenta Habilitada ");
                lblHubChipEstado.setBackground(new Color(220, 252, 231));
                lblHubChipEstado.setForeground(new Color(21, 128, 61));
                lblHubChipEstado.setBorder(BorderFactory.createLineBorder(new Color(134, 239, 172), 1, true));
            } else {
                lblHubChipEstado.setText(" Cuenta Desactivada ");
                lblHubChipEstado.setBackground(new Color(241, 245, 249));
                lblHubChipEstado.setForeground(new Color(148, 163, 184));
                lblHubChipEstado.setBorder(BorderFactory.createLineBorder(new Color(203, 213, 225), 1, true));
            }

            lblHubRol.setText(u.getRol());
            lblHubDepto.setText(u.getEspecialidadArea());
            lblHubCorreo.setText(u.getCorreo());
            lblHubEstado.setText(u.getEstado());

            // Buscar permisos del rol
            RolPermiso rolPerm = null;
            for (RolPermiso r : repo.getRolesPermisos()) {
                if (r.getNombreRol().equalsIgnoreCase(u.getRol())) {
                    rolPerm = r;
                    break;
                }
            }

            if (rolPerm != null) {
                StringBuilder sb = new StringBuilder();
                sb.append("Nivel de Acceso: ").append(rolPerm.getNivelAcceso()).append("\n");
                sb.append("Módulos Habilitados:\n");
                rolPerm.getPermisosModulos().forEach((mod, val) -> {
                    if (val) sb.append("  ✔ ").append(mod).append("\n");
                });
                txtHubPermisosResumen.setText(sb.toString().trim());
            } else {
                txtHubPermisosResumen.setText("Privilegios estándar de perfil operativo asignados para " + u.getRol() + ".");
            }

            panelHubContenido.add(panelHubDetalle, BorderLayout.CENTER);
        }

        panelHubContenido.revalidate();
        panelHubContenido.repaint();
    }

    private void abrirDialogoUsuario(Usuario u) {
        boolean esNuevo = (u == null);
        JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(this), esNuevo ? "Registrar Nuevo Usuario" : "Editar Usuario @" + u.getUsername(), JDialog.ModalityType.APPLICATION_MODAL);
        dlg.setSize(480, 440);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());

        JPanel pnl = new JPanel();
        pnl.setLayout(new BoxLayout(pnl, BoxLayout.Y_AXIS));
        pnl.setBackground(Color.WHITE);
        pnl.setBorder(new EmptyBorder(16, 20, 16, 20));

        JTextField txtNombre = new JTextField(esNuevo ? "" : u.getNombreCompleto());
        JTextField txtUser = new JTextField(esNuevo ? "" : u.getUsername());
        JTextField txtPass = new JTextField(esNuevo ? "vet123" : u.getPassword());
        JTextField txtEmail = new JTextField(esNuevo ? "" : u.getCorreo());
        JTextField txtDepto = new JTextField(esNuevo ? "Atención Clínica" : u.getEspecialidadArea());

        JComboBox<String> cmbRol = Ui.combo(new String[]{"Administrador", "Veterinario Titular", "Veterinario Especialista", "Recepcionista", "Auxiliar Veterinario", "Contador / Auditor"});
        if (!esNuevo) cmbRol.setSelectedItem(u.getRol());

        JComboBox<String> cmbEstado = Ui.combo(new String[]{"Activo", "Inactivo"});
        if (!esNuevo) cmbEstado.setSelectedItem(u.getEstado());

        pnl.add(crearFilaCampo("Nombre Completo:", txtNombre));
        pnl.add(Box.createVerticalStrut(6));
        pnl.add(crearFilaCampo("Usuario / Login:", txtUser));
        pnl.add(Box.createVerticalStrut(6));
        pnl.add(crearFilaCampo("Contraseña Inicial:", txtPass));
        pnl.add(Box.createVerticalStrut(6));
        pnl.add(crearFilaCampo("Correo Corporativo:", txtEmail));
        pnl.add(Box.createVerticalStrut(6));
        pnl.add(crearFilaCampo("Área / Departamento:", txtDepto));
        pnl.add(Box.createVerticalStrut(6));
        pnl.add(crearFilaCampo("Rol Asignado:", cmbRol));
        pnl.add(Box.createVerticalStrut(6));
        pnl.add(crearFilaCampo("Estado de Cuenta:", cmbEstado));

        dlg.add(pnl, BorderLayout.CENTER);

        JPanel bot = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        bot.setBackground(new Color(248, 250, 252));
        bot.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Ui.BORDE_SUAVE));

        JButton btnCancelar = Ui.botonSecundario("Cancelar", null);
        btnCancelar.addActionListener(e -> dlg.dispose());

        JButton btnGuardar = Ui.botonPrimario("Guardar Usuario", null);
        btnGuardar.addActionListener(e -> {
            String nom = txtNombre.getText().trim();
            String usr = txtUser.getText().trim();
            if (nom.isEmpty() || usr.isEmpty()) {
                JOptionPane.showMessageDialog(dlg, "Nombre y Usuario son obligatorios.", "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (esNuevo) {
                Usuario nuevo = new Usuario(usr, txtPass.getText().trim(), nom, (String) cmbRol.getSelectedItem(), txtEmail.getText().trim(), txtDepto.getText().trim(), (String) cmbEstado.getSelectedItem());
                repo.getUsuariosSistema().add(nuevo);
            } else {
                u.setNombreCompleto(nom);
                u.setUsername(usr);
                u.setPassword(txtPass.getText().trim());
                u.setCorreo(txtEmail.getText().trim());
                u.setEspecialidadArea(txtDepto.getText().trim());
                u.setRol((String) cmbRol.getSelectedItem());
                u.setEstado((String) cmbEstado.getSelectedItem());
            }
            dlg.dispose();
            recargarDatos();
            JOptionPane.showMessageDialog(this, "Usuario guardado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        });

        bot.add(btnCancelar);
        bot.add(btnGuardar);
        dlg.add(bot, BorderLayout.SOUTH);

        dlg.setVisible(true);
    }

    private void abrirDialogoGestionRoles() {
        JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(this), "Matriz de Roles y Privilegios (RBAC)", JDialog.ModalityType.APPLICATION_MODAL);
        dlg.setSize(620, 480);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());

        JPanel pnl = new JPanel(new BorderLayout(10, 10));
        pnl.setBackground(Color.WHITE);
        pnl.setBorder(new EmptyBorder(16, 20, 16, 20));

        JLabel lblTit = new JLabel("Perfiles y Matriz de Control de Acceso");
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTit.setForeground(Ui.TEXTO_TITULO);
        pnl.add(lblTit, BorderLayout.NORTH);

        String[] cols = {"Cód.", "Nombre del Rol", "Nivel de Acceso", "Estado", "Descripción General"};
        DefaultTableModel mod = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        for (RolPermiso r : repo.getRolesPermisos()) {
            mod.addRow(new Object[]{r.getIdRol(), r.getNombreRol(), r.getNivelAcceso(), r.isActivo() ? "Activo" : "Inactivo", r.getDescripcion()});
        }

        JTable tbl = new JTable(mod);
        tbl.setRowHeight(38);
        tbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tbl.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        tbl.getTableHeader().setBackground(new Color(248, 250, 252));

        pnl.add(new JScrollPane(tbl), BorderLayout.CENTER);
        dlg.add(pnl, BorderLayout.CENTER);

        JPanel bot = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        bot.setBackground(new Color(248, 250, 252));
        bot.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Ui.BORDE_SUAVE));

        JButton btnCerrar = Ui.botonPrimario("Cerrar", null);
        btnCerrar.addActionListener(e -> dlg.dispose());
        bot.add(btnCerrar);
        dlg.add(bot, BorderLayout.SOUTH);

        dlg.setVisible(true);
    }

    private void exportarDirectorioUsuarios() {
        List<Usuario> list = repo.getUsuariosSistema();
        String[][] meta = new String[][]{
            {"Módulo", "Gestión de Usuarios y Roles"},
            {"Fecha de Emisión", LocalDate.now().toString()},
            {"Total Colaboradores", String.valueOf(list.size())},
            {"Seguridad", "Políticas RBAC Activas"}
        };
        String[][] datos = new String[list.size()][6];
        for (int i = 0; i < list.size(); i++) {
            Usuario u = list.get(i);
            datos[i][0] = String.valueOf(i + 1);
            datos[i][1] = u.getNombreCompleto();
            datos[i][2] = "@" + u.getUsername();
            datos[i][3] = u.getRol();
            datos[i][4] = u.getEspecialidadArea();
            datos[i][5] = u.getEstado();
        }

        Ui.mostrarVisorReporte(
            (java.awt.Frame) SwingUtilities.getWindowAncestor(this),
            "DIRECTORIO OFICIAL DE USUARIOS Y ROLES",
            "Personal Clínico y Administrativo con Credenciales ERP",
            meta,
            new String[]{"N°", "Colaborador", "Usuario", "Rol Asignado", "Departamento", "Estado"},
            datos,
            "Documento oficial de control de acceso corporativo Happy Pets.",
            "Directorio_Usuarios_Roles"
        );
    }

    private JPanel crearFilaCampo(String etiqueta, JComponent comp) {
        JPanel f = new JPanel(new BorderLayout(8, 0));
        f.setOpaque(false);
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JLabel lbl = new JLabel(etiqueta);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lbl.setForeground(Ui.TEXTO_MUTED);
        lbl.setPreferredSize(new Dimension(140, 26));

        f.add(lbl, BorderLayout.WEST);
        f.add(comp, BorderLayout.CENTER);
        return f;
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
