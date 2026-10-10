package happypets.modulos.modulo10;

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
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.time.LocalDate;

import happypets.data.RepositorioVeterinaria;
import happypets.model.RolPermiso;
import happypets.model.Usuario;
import happypets.ui.Iconos;
import happypets.ui.Ui;

/**
 * Submódulo 10.2: Usuarios Activos, Roles y Permisos.
 * Basado fielmente en el wireframe 'USUARIO.pdf'.
 */
public class VistaUsuariosRolesPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private final RepositorioVeterinaria repo = RepositorioVeterinaria.getInstancia();

    private JTextField txtBuscar;
    private JComboBox<String> cbFiltroRol;
    private JComboBox<String> cbFiltroEstado;
    private JTable tablaUsuarios;
    private DefaultTableModel modeloTabla;
    private JLabel lblContadorPaginacion;
    private JButton btnAnterior;
    private JButton btnSiguiente;
    private JButton btnPagina1;
    private JButton btnPagina2;

    private int paginaActual = 1;
    private final int itemsPorPagina = 7;
    private List<Usuario> usuariosFiltrados;

    public VistaUsuariosRolesPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(248, 250, 252));
        inicializarUI();
        recargarTabla();
    }

    private void inicializarUI() {
        JPanel contenedor = new JPanel(new BorderLayout(0, 16));
        contenedor.setBackground(new Color(248, 250, 252));
        contenedor.setBorder(BorderFactory.createEmptyBorder(20, 28, 28, 28));

        // 1. Tarjeta Contenedora Principal (como en USUARIO.pdf)
        JPanel card = new JPanel(new BorderLayout(0, 16));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                BorderFactory.createEmptyBorder(20, 24, 20, 24)
        ));

        // Cabecera de la Tarjeta con Botones [Gestionar Roles] y [+ Nuevo Usuario]
        card.add(crearCabeceraTarjeta(), BorderLayout.NORTH);

        // Barra de Búsqueda y Filtros
        card.add(crearBarraFiltros(), BorderLayout.AFTER_LINE_ENDS); // O justo antes de la tabla

        // Panel Central con Filtros y Tabla
        JPanel centro = new JPanel(new BorderLayout(0, 12));
        centro.setBackground(Color.WHITE);
        centro.add(crearBarraFiltros(), BorderLayout.NORTH);
        centro.add(crearTablaScroll(), BorderLayout.CENTER);
        centro.add(crearPiePaginacion(), BorderLayout.SOUTH);

        card.add(centro, BorderLayout.CENTER);
        contenedor.add(card, BorderLayout.CENTER);

        add(contenedor, BorderLayout.CENTER);
    }

    private JPanel crearCabeceraTarjeta() {
        JPanel p = new JPanel(new BorderLayout(15, 0));
        p.setBackground(Color.WHITE);

        // Título con Icono de Grupo de Usuarios
        JPanel izq = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        izq.setBackground(Color.WHITE);

        JLabel lblIcono = new JLabel(Iconos.crearIconoUsuario(24, Ui.TURQUESA_PROFUNDO));
        JLabel lblTitulo = new JLabel("Usuarios Activos y Asignación de Roles");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setForeground(new Color(15, 23, 42));

        izq.add(lblIcono);
        izq.add(lblTitulo);
        p.add(izq, BorderLayout.WEST);

        // Botones de Cabecera: [Exportar Lista], [Gestionar Roles] y [+ Nuevo Usuario]
        JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        der.setBackground(Color.WHITE);

        JButton btnExportar = Ui.botonSecundario("Exportar Lista", Iconos.crearIconoExportar(13, Ui.TURQUESA_PROFUNDO));
        btnExportar.setPreferredSize(new Dimension(140, 34));
        btnExportar.addActionListener(e -> exportarDirectorioUsuarios());
        der.add(btnExportar);

        JButton btnGestionarRoles = Ui.botonSecundario("Gestionar Roles", null);
        btnGestionarRoles.setPreferredSize(new Dimension(135, 34));
        btnGestionarRoles.addActionListener(e -> abrirDialogoGestionRoles());
        der.add(btnGestionarRoles);

        JButton btnNuevoUsuario = Ui.botonPrimario("+ Nuevo Usuario", null);
        btnNuevoUsuario.setPreferredSize(new Dimension(145, 34));
        btnNuevoUsuario.addActionListener(e -> abrirDialogoUsuario(null));
        der.add(btnNuevoUsuario);

        p.add(der, BorderLayout.EAST);
        return p;
    }

    private JPanel crearBarraFiltros() {
        JPanel bar = new JPanel(new BorderLayout(12, 0));
        bar.setBackground(new Color(248, 250, 252));
        bar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));

        // Buscador reactivo
        JPanel izq = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        izq.setBackground(new Color(248, 250, 252));
        JLabel lblLupa = new JLabel("🔍");
        txtBuscar = Ui.campoTexto("", 20);
        txtBuscar.setPreferredSize(new Dimension(240, 32));
        txtBuscar.setToolTipText("Buscar por nombre, usuario, correo o especialidad...");
        txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { paginaActual = 1; recargarTabla(); }
            public void removeUpdate(DocumentEvent e) { paginaActual = 1; recargarTabla(); }
            public void changedUpdate(DocumentEvent e) { paginaActual = 1; recargarTabla(); }
        });
        izq.add(lblLupa);
        izq.add(txtBuscar);

        // Combos de filtro
        JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        der.setBackground(new Color(248, 250, 252));

        cbFiltroRol = Ui.combo(new String[]{"Rol: Todos", "Administrador", "Veterinario Titular", "Recepcionista", "Auxiliar Veterinario", "Contador / Auditor"});
        cbFiltroRol.setPreferredSize(new Dimension(160, 32));
        cbFiltroRol.addActionListener(e -> { paginaActual = 1; recargarTabla(); });

        cbFiltroEstado = Ui.combo(new String[]{"Estado: Todos", "Activo", "Inactivo"});
        cbFiltroEstado.setPreferredSize(new Dimension(130, 32));
        cbFiltroEstado.addActionListener(e -> { paginaActual = 1; recargarTabla(); });

        JButton btnLimpiar = Ui.botonSecundario("Limpiar", null);
        btnLimpiar.setPreferredSize(new Dimension(85, 32));
        btnLimpiar.addActionListener(e -> {
            txtBuscar.setText("");
            cbFiltroRol.setSelectedIndex(0);
            cbFiltroEstado.setSelectedIndex(0);
            paginaActual = 1;
            recargarTabla();
        });

        der.add(new JLabel("Filtros:"));
        der.add(cbFiltroRol);
        der.add(cbFiltroEstado);
        der.add(btnLimpiar);

        bar.add(izq, BorderLayout.WEST);
        bar.add(der, BorderLayout.EAST);
        return bar;
    }

    private JScrollPane crearTablaScroll() {
        String[] columnas = {"Usuario / Nombre", "Rol Asignado", "Especialidad / Área", "Estado", "Acciones"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            private static final long serialVersionUID = 1L;
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        tablaUsuarios = new JTable(modeloTabla);
        Ui.formatearTabla(tablaUsuarios, new int[]{3}, new int[]{});

        // Renderers estilizados
        tablaUsuarios.getColumnModel().getColumn(0).setPreferredWidth(260);
        tablaUsuarios.getColumnModel().getColumn(0).setCellRenderer(new UsuarioCellRenderer());

        tablaUsuarios.getColumnModel().getColumn(1).setPreferredWidth(160);
        tablaUsuarios.getColumnModel().getColumn(1).setCellRenderer(new RolCellRenderer());

        tablaUsuarios.getColumnModel().getColumn(2).setPreferredWidth(180);
        tablaUsuarios.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                setForeground(new Color(71, 85, 105));
                setFont(new Font("Segoe UI", Font.PLAIN, 13));
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                return this;
            }
        });

        tablaUsuarios.getColumnModel().getColumn(3).setPreferredWidth(110);
        tablaUsuarios.getColumnModel().getColumn(3).setCellRenderer(new EstadoCellRenderer());

        tablaUsuarios.getColumnModel().getColumn(4).setPreferredWidth(170);
        tablaUsuarios.getColumnModel().getColumn(4).setCellRenderer(new AccionesCellRenderer());

        // Evento de clic en acciones
        tablaUsuarios.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int row = tablaUsuarios.getSelectedRow();
                int col = tablaUsuarios.getSelectedColumn();
                if (row >= 0 && col == 4) {
                    Usuario u = obtenerUsuarioDeFila(row);
                    if (u != null) {
                        int x = e.getX() - tablaUsuarios.getCellRect(row, col, true).x;
                        if (x < 80) {
                            abrirDialogoPermisosUsuario(u);
                        } else {
                            abrirDialogoUsuario(u);
                        }
                    }
                }
            }
        });

        JScrollPane scroll = new JScrollPane(tablaUsuarios);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240), 1));
        scroll.getViewport().setBackground(Color.WHITE);
        return scroll;
    }

    private JPanel crearPiePaginacion() {
        JPanel foot = new JPanel(new BorderLayout());
        foot.setBackground(Color.WHITE);
        foot.setBorder(BorderFactory.createEmptyBorder(12, 4, 4, 4));

        lblContadorPaginacion = new JLabel("Mostrando 0 de 0 colaboradores");
        lblContadorPaginacion.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblContadorPaginacion.setForeground(new Color(100, 116, 139));
        foot.add(lblContadorPaginacion, BorderLayout.WEST);

        JPanel pnlPags = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        pnlPags.setBackground(Color.WHITE);

        btnAnterior = new JButton("Anterior");
        estilizarBotonPaginacion(btnAnterior);
        btnAnterior.addActionListener(e -> {
            if (paginaActual > 1) {
                paginaActual--;
                recargarTabla();
            }
        });
        pnlPags.add(btnAnterior);

        btnPagina1 = new JButton("1");
        estilizarBotonPaginacion(btnPagina1);
        btnPagina1.addActionListener(e -> {
            paginaActual = 1;
            recargarTabla();
        });
        pnlPags.add(btnPagina1);

        btnPagina2 = new JButton("2");
        estilizarBotonPaginacion(btnPagina2);
        btnPagina2.addActionListener(e -> {
            paginaActual = 2;
            recargarTabla();
        });
        pnlPags.add(btnPagina2);

        btnSiguiente = new JButton("Siguiente");
        estilizarBotonPaginacion(btnSiguiente);
        btnSiguiente.addActionListener(e -> {
            int total = usuariosFiltrados != null ? usuariosFiltrados.size() : 0;
            int maxPags = (int) Math.ceil((double) total / itemsPorPagina);
            if (paginaActual < maxPags) {
                paginaActual++;
                recargarTabla();
            }
        });
        pnlPags.add(btnSiguiente);

        foot.add(pnlPags, BorderLayout.EAST);
        return foot;
    }

    private void estilizarBotonPaginacion(JButton b) {
        b.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        b.setBackground(Color.WHITE);
        b.setForeground(new Color(71, 85, 105));
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                BorderFactory.createEmptyBorder(5, 11, 5, 11)
        ));
    }

    private void recargarTabla() {
        String termino = txtBuscar != null ? txtBuscar.getText().trim() : "";
        String fRol = (cbFiltroRol != null && cbFiltroRol.getSelectedIndex() > 0) ? (String) cbFiltroRol.getSelectedItem() : "Todos";
        String fEst = (cbFiltroEstado != null && cbFiltroEstado.getSelectedIndex() > 0) ? (String) cbFiltroEstado.getSelectedItem() : "Todos";

        usuariosFiltrados = repo.buscarUsuariosSistema(termino, fRol, fEst);
        int total = usuariosFiltrados.size();
        int maxPags = Math.max(1, (int) Math.ceil((double) total / itemsPorPagina));

        if (paginaActual > maxPags) paginaActual = maxPags;
        if (paginaActual < 1) paginaActual = 1;

        int inicio = (paginaActual - 1) * itemsPorPagina;
        int fin = Math.min(inicio + itemsPorPagina, total);

        modeloTabla.setRowCount(0);
        for (int i = inicio; i < fin; i++) {
            Usuario u = usuariosFiltrados.get(i);
            modeloTabla.addRow(new Object[]{
                    u, // Objeto completo para renderizar nombre + email
                    u.getRol(),
                    u.getEspecialidadArea(),
                    u.getEstado(),
                    "Permisos | Editar"
            });
        }

        // Actualizar textos y botones de paginación
        int mostrados = fin - inicio;
        lblContadorPaginacion.setText("Mostrando " + mostrados + " de " + total + " colaboradores");
        btnAnterior.setEnabled(paginaActual > 1);
        btnSiguiente.setEnabled(paginaActual < maxPags);

        // Estilo activo para el botón de página actual
        btnPagina1.setBackground(paginaActual == 1 ? Ui.TURQUESA_PROFUNDO : Color.WHITE);
        btnPagina1.setForeground(paginaActual == 1 ? Color.WHITE : new Color(71, 85, 105));
        btnPagina2.setBackground(paginaActual == 2 ? Ui.TURQUESA_PROFUNDO : Color.WHITE);
        btnPagina2.setForeground(paginaActual == 2 ? Color.WHITE : new Color(71, 85, 105));
    }

    private Usuario obtenerUsuarioDeFila(int fila) {
        if (fila >= 0 && fila < modeloTabla.getRowCount()) {
            Object obj = modeloTabla.getValueAt(fila, 0);
            if (obj instanceof Usuario) {
                return (Usuario) obj;
            }
        }
        return null;
    }

    // Renderers visuales
    private static class UsuarioCellRenderer extends DefaultTableCellRenderer {
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
            JPanel p = new JPanel();
            p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
            p.setBackground(isSelected ? Ui.TURQUESA_SUAVE : Color.WHITE);
            p.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));

            if (value instanceof Usuario) {
                Usuario u = (Usuario) value;
                JLabel lblNom = new JLabel(u.getNombreCompleto());
                lblNom.setFont(new Font("Segoe UI", Font.BOLD, 13));
                lblNom.setForeground(new Color(15, 23, 42));

                JLabel lblMail = new JLabel(u.getCorreo());
                lblMail.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                lblMail.setForeground(new Color(100, 116, 139));

                p.add(lblNom);
                p.add(Box.createVerticalStrut(2));
                p.add(lblMail);
            }
            return p;
        }
    }

    private static class RolCellRenderer extends DefaultTableCellRenderer {
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
            JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 12));
            p.setBackground(isSelected ? Ui.TURQUESA_SUAVE : Color.WHITE);

            String rol = value != null ? value.toString() : "";
            JLabel badge = new JLabel(rol);
            badge.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            badge.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Ui.BORDE_SUAVE, 1),
                    BorderFactory.createEmptyBorder(4, 10, 4, 10)
            ));
            badge.setOpaque(true);

            if ("Administrador".equalsIgnoreCase(rol)) {
                badge.setBackground(Ui.TURQUESA_SUAVE);
                badge.setForeground(Ui.TURQUESA_PROFUNDO);
                badge.setFont(new Font("Segoe UI", Font.BOLD, 12));
            } else {
                badge.setBackground(new Color(248, 250, 252));
                badge.setForeground(new Color(30, 41, 59));
            }
            p.add(badge);
            return p;
        }
    }

    private static class EstadoCellRenderer extends DefaultTableCellRenderer {
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
            JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 16));
            p.setBackground(isSelected ? Ui.TURQUESA_SUAVE : Color.WHITE);

            String estado = value != null ? value.toString() : "Activo";
            boolean activo = "Activo".equalsIgnoreCase(estado);

            JLabel lbl = new JLabel("● " + estado);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
            lbl.setForeground(activo ? Ui.TURQUESA_OSCURO : new Color(148, 163, 184));
            p.add(lbl);
            return p;
        }
    }

    private static class AccionesCellRenderer extends DefaultTableCellRenderer {
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
            JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 10));
            p.setBackground(isSelected ? Ui.TURQUESA_SUAVE : Color.WHITE);

            JButton btnPermisos = new JButton("Permisos");
            btnPermisos.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            btnPermisos.setBackground(new Color(241, 245, 249));
            btnPermisos.setForeground(Ui.TURQUESA_PROFUNDO);
            btnPermisos.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Ui.BORDE_SUAVE, 1),
                    BorderFactory.createEmptyBorder(3, 8, 3, 8)
            ));

            JButton btnEditar = new JButton("Editar");
            btnEditar.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            btnEditar.setBackground(new Color(241, 245, 249));
            btnEditar.setForeground(Ui.TURQUESA_PROFUNDO);
            btnEditar.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Ui.BORDE_SUAVE, 1),
                    BorderFactory.createEmptyBorder(3, 8, 3, 8)
            ));

            p.add(btnPermisos);
            p.add(btnEditar);
            return p;
        }
    }

    // Diálogos modales interactivos
    private void abrirDialogoUsuario(Usuario uExistente) {
        JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(this), uExistente == null ? "Nuevo Colaborador" : "Editar Colaborador", JDialog.ModalityType.APPLICATION_MODAL);
        dlg.setSize(480, 480);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());

        JPanel pnl = new JPanel(new GridBagLayout());
        pnl.setBackground(Color.WHITE);
        pnl.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 6, 6, 6);
        g.fill = GridBagConstraints.HORIZONTAL;

        JTextField txtNom = Ui.campoTexto(uExistente != null ? uExistente.getNombreCompleto() : "", 15);
        JTextField txtUser = Ui.campoTexto(uExistente != null ? uExistente.getUsername() : "", 15);
        if (uExistente != null) txtUser.setEditable(false);
        JTextField txtMail = Ui.campoTexto(uExistente != null ? uExistente.getCorreo() : "", 15);
        JTextField txtPass = Ui.campoTexto(uExistente != null ? uExistente.getPassword() : "123456", 15);

        JComboBox<String> cbRol = Ui.combo(new String[]{"Administrador", "Veterinario Titular", "Recepcionista", "Auxiliar Veterinario", "Contador / Auditor"});
        if (uExistente != null) cbRol.setSelectedItem(uExistente.getRol());

        JTextField txtArea = Ui.campoTexto(uExistente != null ? uExistente.getEspecialidadArea() : "Clínica General", 15);
        JComboBox<String> cbEst = Ui.combo(new String[]{"Activo", "Inactivo"});
        if (uExistente != null) cbEst.setSelectedItem(uExistente.getEstado());

        int fila = 0;
        agregarFilaForm(pnl, g, fila++, "Nombre Completo:", txtNom);
        agregarFilaForm(pnl, g, fila++, "Nombre de Usuario:", txtUser);
        agregarFilaForm(pnl, g, fila++, "Correo Electrónico:", txtMail);
        agregarFilaForm(pnl, g, fila++, "Contraseña de Acceso:", txtPass);
        agregarFilaForm(pnl, g, fila++, "Rol en el Sistema:", cbRol);
        agregarFilaForm(pnl, g, fila++, "Especialidad / Área:", txtArea);
        agregarFilaForm(pnl, g, fila++, "Estado:", cbEst);

        JPanel bot = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        bot.setBackground(new Color(248, 250, 252));
        JButton btnCan = Ui.botonSecundario("Cancelar", null);
        btnCan.addActionListener(e -> dlg.dispose());
        JButton btnOk = Ui.botonPrimario("Guardar Colaborador", null);
        btnOk.addActionListener(e -> {
            String nom = txtNom.getText().trim();
            String user = txtUser.getText().trim();
            String mail = txtMail.getText().trim();
            String pass = txtPass.getText().trim();
            if (nom.isEmpty() || user.isEmpty() || mail.isEmpty()) {
                JOptionPane.showMessageDialog(dlg, "Por favor complete los campos obligatorios.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Usuario nuevo = new Usuario(user, pass, nom, (String) cbRol.getSelectedItem(), mail, txtArea.getText().trim(), (String) cbEst.getSelectedItem());
            repo.guardarUsuarioSistema(nuevo);
            recargarTabla();
            dlg.dispose();
            JOptionPane.showMessageDialog(this, "✓ Usuario " + user + " guardado satisfactoriamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        });
        bot.add(btnCan);
        bot.add(btnOk);

        dlg.add(pnl, BorderLayout.CENTER);
        dlg.add(bot, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    private void agregarFilaForm(JPanel p, GridBagConstraints g, int fila, String label, Component c) {
        g.gridx = 0; g.gridy = fila; g.weightx = 0.35;
        JLabel l = new JLabel(label);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        p.add(l, g);
        g.gridx = 1; g.gridy = fila; g.weightx = 0.65;
        p.add(c, g);
    }

    private void abrirDialogoGestionRoles() {
        JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(this), "Gestión de Roles y Matriz de Permisos", JDialog.ModalityType.APPLICATION_MODAL);
        dlg.setSize(620, 520);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());

        JPanel pnl = new JPanel(new BorderLayout(0, 16));
        pnl.setBackground(Color.WHITE);
        pnl.setBorder(BorderFactory.createEmptyBorder(18, 22, 18, 22));

        JLabel lblTit = new JLabel("Configuración de Privilegios por Rol");
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTit.setForeground(new Color(15, 23, 42));
        pnl.add(lblTit, BorderLayout.NORTH);

        JComboBox<String> cbRoles = Ui.combo(new String[]{
                "Administrador (Nivel Total)",
                "Veterinario Titular (Nivel Médico)",
                "Recepcionista (Nivel Operativo)",
                "Auxiliar Veterinario (Nivel Asistencial)",
                "Contador / Auditor (Nivel Financiero)"
        });

        JPanel pnlSel = new JPanel(new BorderLayout(10, 0));
        pnlSel.setBackground(Color.WHITE);
        pnlSel.add(new JLabel("Seleccione Rol: "), BorderLayout.WEST);
        pnlSel.add(cbRoles, BorderLayout.CENTER);

        // Matriz de permisos con checkboxes
        JPanel matriz = new JPanel(new GridLayout(5, 2, 12, 10));
        matriz.setBackground(Color.WHITE);
        matriz.setBorder(BorderFactory.createTitledBorder("Módulos y Privilegios Permitidos"));

        String[] modulos = {
                "Pacientes e Historias Clínicas",
                "Agenda y Citas",
                "Servicios Médicos y Quirófanos",
                "Estética y Hospedaje Canino/Felino",
                "Farmacia e Inventario",
                "Finanzas, Ventas POS y Caja",
                "Personal y Cuadrante RRHH",
                "Reportes y Métricas BI",
                "Notificaciones, Repositorio y Auditoría",
                "Configuración, Integraciones e IA"
        };

        JCheckBox[] checks = new JCheckBox[modulos.length];
        for (int i = 0; i < modulos.length; i++) {
            checks[i] = new JCheckBox(modulos[i], true);
            checks[i].setFont(new Font("Segoe UI", Font.PLAIN, 12));
            checks[i].setBackground(Color.WHITE);
            matriz.add(checks[i]);
        }

        cbRoles.addActionListener(e -> {
            int sel = cbRoles.getSelectedIndex();
            for (int i = 0; i < checks.length; i++) {
                if (sel == 0) checks[i].setSelected(true);
                else if (sel == 1) checks[i].setSelected(i <= 4 || i == 7);
                else if (sel == 2) checks[i].setSelected(i == 0 || i == 1 || i == 5);
                else if (sel == 3) checks[i].setSelected(i == 0 || i == 3 || i == 4);
                else if (sel == 4) checks[i].setSelected(i == 5 || i == 7 || i == 8);
            }
        });

        JPanel centro = new JPanel(new BorderLayout(0, 14));
        centro.setBackground(Color.WHITE);
        centro.add(pnlSel, BorderLayout.NORTH);
        centro.add(matriz, BorderLayout.CENTER);
        pnl.add(centro, BorderLayout.CENTER);

        JPanel bot = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton btnCerrar = Ui.botonSecundario("Cerrar", null);
        btnCerrar.addActionListener(e -> dlg.dispose());
        JButton btnGuardar = Ui.botonPrimario("Guardar Privilegios", null);
        btnGuardar.addActionListener(e -> {
            JOptionPane.showMessageDialog(dlg, "✓ Matriz de privilegios para '" + cbRoles.getSelectedItem() + "' actualizada correctamente.", "Roles Guardados", JOptionPane.INFORMATION_MESSAGE);
            dlg.dispose();
        });
        bot.add(btnCerrar);
        bot.add(btnGuardar);

        dlg.add(pnl, BorderLayout.CENTER);
        dlg.add(bot, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    private void abrirDialogoPermisosUsuario(Usuario u) {
        JOptionPane.showMessageDialog(
                this,
                "Privilegios activos para: " + u.getNombreCompleto() + "\n"
                        + "• Rol: " + u.getRol() + "\n"
                        + "• Área Asignada: " + u.getEspecialidadArea() + "\n"
                        + "• Estado de Cuenta: " + u.getEstado() + "\n\n"
                        + "Los permisos heredan las políticas del rol '" + u.getRol() + "'.\n"
                        + "Para personalizar módulos individuales, use el botón [Gestionar Roles].",
                "Permisos de " + u.getUsername(),
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void exportarDirectorioUsuarios() {
        List<Usuario> us = repo.getUsuariosSistema();
        StringBuilder html = new StringBuilder();
        html.append("<html><body style='font-family:sans-serif; padding:15px; color:#1e293b;'>");
        html.append("<h2 style='color:#006064; border-bottom:2px solid #00BCD4; padding-bottom:6px;'>DIRECTORIO DE USUARIOS Y ROLES ERP</h2>");
        html.append("<p><strong>Fecha:</strong> ").append(LocalDate.now()).append(" | <strong>Total Colaboradores:</strong> ").append(us.size()).append("</p>");
        html.append("<table border='1' cellpadding='6' cellspacing='0' style='border-collapse:collapse; width:100%; border-color:#cbd5e1; font-size:11px;'>");
        html.append("<tr style='background-color:#006064; color:white;'><th>Nombre Completo</th><th>Usuario</th><th>Correo</th><th>Rol</th><th>Área</th><th>Estado</th></tr>");

        StringBuilder csv = new StringBuilder();
        csv.append("Nombre Completo,Usuario,Correo,Rol,Area,Estado\n");

        for (Usuario u : us) {
            html.append("<tr>")
                .append("<td><b>").append(u.getNombreCompleto()).append("</b></td>")
                .append("<td>").append(u.getUsername()).append("</td>")
                .append("<td>").append(u.getCorreo()).append("</td>")
                .append("<td>").append(u.getRol()).append("</td>")
                .append("<td>").append(u.getEspecialidadArea()).append("</td>")
                .append("<td align='center'>").append(u.getEstado()).append("</td>")
                .append("</tr>");

            csv.append("\"").append(u.getNombreCompleto()).append("\",\"")
               .append(u.getUsername()).append("\",\"")
               .append(u.getCorreo()).append("\",\"")
               .append(u.getRol()).append("\",\"")
               .append(u.getEspecialidadArea()).append("\",\"")
               .append(u.getEstado()).append("\"\n");
        }

        html.append("</table>");
        html.append("<p style='margin-top:15px; font-size:10px; color:#64748b;'>Documento de nómina institucional expedido por Veterinaria Happy Pets ERP.</p>");
        html.append("</body></html>");

        Ui.mostrarVisorReporte(
            SwingUtilities.getWindowAncestor(this),
            "Directorio de Usuarios",
            "Directorio Oficial de Colaboradores y Roles",
            html.toString(),
            csv.toString()
        );
    }
}
