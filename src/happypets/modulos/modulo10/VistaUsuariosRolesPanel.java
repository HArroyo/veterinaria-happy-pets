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

import happypets.data.RepositorioVeterinaria;
import happypets.model.RolPermiso;
import happypets.model.Usuario;
import happypets.ui.Iconos;

/**
 * Submódulo 10.2: Usuarios Activos, Roles y Permisos.
 * Basado fielmente en el wireframe 'USUARIO.pdf'.
 */
public class VistaUsuariosRolesPanel extends happypets.ui.AssetsModulo {
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
        setBackground(Color.WHITE);
        inicializarUI();
        recargarTabla();
    }

    private void inicializarUI() {
        JPanel contenedor = new JPanel(new BorderLayout(0, 16));
        contenedor.setBackground(Color.WHITE);
        contenedor.setOpaque(false);
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

        JLabel lblIcono = new JLabel(Iconos.crearIconoUsuario(24, new Color(30, 41, 59)));
        JLabel lblTitulo = new JLabel("Usuarios Activos y Asignación de Roles");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setForeground(new Color(15, 23, 42));

        izq.add(lblIcono);
        izq.add(lblTitulo);
        p.add(izq, BorderLayout.WEST);

        // Botones de Cabecera: [Gestionar Roles] y [+ Nuevo Usuario]
        JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        der.setBackground(Color.WHITE);

        JButton btnGestionarRoles = new happypets.ui.BotonAsset("Gestionar Roles");
        btnGestionarRoles.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnGestionarRoles.setForeground(new Color(51, 65, 85));
        btnGestionarRoles.setBackground(new Color(241, 245, 249));
        btnGestionarRoles.setFocusPainted(false);
        btnGestionarRoles.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnGestionarRoles.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225), 1),
                BorderFactory.createEmptyBorder(8, 16, 8, 16)
        ));
        btnGestionarRoles.addActionListener(e -> abrirDialogoGestionRoles());
        der.add(btnGestionarRoles);

        JButton btnNuevoUsuario = new happypets.ui.BotonAsset("+ Nuevo Usuario");
        btnNuevoUsuario.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnNuevoUsuario.setForeground(Color.WHITE);
        btnNuevoUsuario.setBackground(new Color(15, 23, 42));
        btnNuevoUsuario.setFocusPainted(false);
        btnNuevoUsuario.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnNuevoUsuario.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        btnNuevoUsuario.addActionListener(e -> abrirDialogoUsuario(null));
        der.add(btnNuevoUsuario);

        p.add(der, BorderLayout.EAST);
        return p;
    }

    private JPanel crearBarraFiltros() {
        JPanel bar = new JPanel(new BorderLayout(12, 0));
        bar.setBackground(Color.WHITE);
        bar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));

        // Buscador reactivo
        JPanel izq = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        izq.setBackground(Color.WHITE);
        JLabel lblLupa = new happypets.ui.EtiquetaAsset("🔍");
        txtBuscar = new JTextField(20);
        txtBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 13));
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
        der.setBackground(Color.WHITE);

        cbFiltroRol = new JComboBox<>(new String[]{"Rol: Todos", "Administrador", "Veterinario Titular", "Recepcionista", "Auxiliar Veterinario", "Contador / Auditor"});
        cbFiltroRol.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cbFiltroRol.setBackground(Color.WHITE);
        cbFiltroRol.addActionListener(e -> { paginaActual = 1; recargarTabla(); });

        cbFiltroEstado = new JComboBox<>(new String[]{"Estado: Todos", "Activo", "Inactivo"});
        cbFiltroEstado.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cbFiltroEstado.setBackground(Color.WHITE);
        cbFiltroEstado.addActionListener(e -> { paginaActual = 1; recargarTabla(); });

        JButton btnLimpiar = new happypets.ui.BotonAsset("Limpiar");
        btnLimpiar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnLimpiar.setBackground(Color.WHITE);
        btnLimpiar.setFocusPainted(false);
        btnLimpiar.setCursor(new Cursor(Cursor.HAND_CURSOR));
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
        tablaUsuarios.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tablaUsuarios.setRowHeight(52);
        tablaUsuarios.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaUsuarios.setShowVerticalLines(false);
        tablaUsuarios.setGridColor(new Color(241, 245, 249));
        tablaUsuarios.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tablaUsuarios.getTableHeader().setBackground(Color.WHITE);
        tablaUsuarios.getTableHeader().setForeground(new Color(71, 85, 105));
        tablaUsuarios.getTableHeader().setPreferredSize(new Dimension(10, 38));

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
        tablaUsuarios.getColumnModel().getColumn(4).setMinWidth(200);
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
                        if (x < tablaUsuarios.getCellRect(row, col, true).width / 2) {
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

        btnAnterior = new happypets.ui.BotonAsset("Anterior");
        estilizarBotonPaginacion(btnAnterior);
        btnAnterior.addActionListener(e -> {
            if (paginaActual > 1) {
                paginaActual--;
                recargarTabla();
            }
        });
        pnlPags.add(btnAnterior);

        btnPagina1 = new happypets.ui.BotonAsset("1");
        estilizarBotonPaginacion(btnPagina1);
        btnPagina1.addActionListener(e -> {
            paginaActual = 1;
            recargarTabla();
        });
        pnlPags.add(btnPagina1);

        btnPagina2 = new happypets.ui.BotonAsset("2");
        estilizarBotonPaginacion(btnPagina2);
        btnPagina2.addActionListener(e -> {
            paginaActual = 2;
            recargarTabla();
        });
        pnlPags.add(btnPagina2);

        btnSiguiente = new happypets.ui.BotonAsset("Siguiente");
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
        btnPagina1.setBackground(paginaActual == 1 ? new Color(15, 23, 42) : Color.WHITE);
        btnPagina1.setForeground(paginaActual == 1 ? Color.WHITE : new Color(71, 85, 105));
        btnPagina2.setBackground(paginaActual == 2 ? new Color(15, 23, 42) : Color.WHITE);
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
            p.setBackground(isSelected ? new Color(241, 245, 249) : Color.WHITE);
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
            p.setBackground(isSelected ? new Color(241, 245, 249) : Color.WHITE);

            String rol = value != null ? value.toString() : "";
            JLabel badge = new JLabel(rol);
            badge.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            badge.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(203, 213, 225), 1),
                    BorderFactory.createEmptyBorder(4, 10, 4, 10)
            ));
            badge.setOpaque(true);
            badge.setBackground(Color.WHITE);
            badge.setForeground(new Color(30, 41, 59));

            if ("Administrador".equalsIgnoreCase(rol)) {
                badge.setFont(new Font("Segoe UI", Font.BOLD, 12));
            }
            p.add(badge);
            return p;
        }
    }

    private static class EstadoCellRenderer extends DefaultTableCellRenderer {
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
            JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 16));
            p.setBackground(isSelected ? new Color(241, 245, 249) : Color.WHITE);

            String estado = value != null ? value.toString() : "Activo";
            boolean activo = "Activo".equalsIgnoreCase(estado);

            JLabel lbl = new JLabel("● " + estado);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
            lbl.setForeground(activo ? new Color(34, 197, 94) : new Color(148, 163, 184)); // Verde o Gris
            p.add(lbl);
            return p;
        }
    }

    private static class AccionesCellRenderer extends DefaultTableCellRenderer {
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
            JPanel p = new JPanel(new GridLayout(1, 2, 6, 0));
            p.setBorder(BorderFactory.createEmptyBorder(10, 4, 10, 4));
            p.setBackground(isSelected ? new Color(241, 245, 249) : Color.WHITE);

            JButton btnPermisos = new happypets.ui.BotonAsset("Permisos");
            btnPermisos.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            btnPermisos.setBackground(new Color(241, 245, 249));
            btnPermisos.setForeground(new Color(51, 65, 85));
            btnPermisos.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(203, 213, 225), 1),
                    BorderFactory.createEmptyBorder(3, 8, 3, 8)
            ));

            JButton btnEditar = new happypets.ui.BotonAsset("Editar");
            btnEditar.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            btnEditar.setBackground(new Color(241, 245, 249));
            btnEditar.setForeground(new Color(51, 65, 85));
            btnEditar.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(203, 213, 225), 1),
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

        JTextField txtNom = new JTextField(uExistente != null ? uExistente.getNombreCompleto() : "");
        JTextField txtUser = new JTextField(uExistente != null ? uExistente.getUsername() : "");
        if (uExistente != null) txtUser.setEditable(false);
        JTextField txtMail = new JTextField(uExistente != null ? uExistente.getCorreo() : "");
        JTextField txtPass = new JTextField(uExistente != null ? uExistente.getPassword() : "123456");

        JComboBox<String> cbRol = new JComboBox<>(new String[]{"Administrador", "Veterinario Titular", "Recepcionista", "Auxiliar Veterinario", "Contador / Auditor"});
        if (uExistente != null) cbRol.setSelectedItem(uExistente.getRol());

        JTextField txtArea = new JTextField(uExistente != null ? uExistente.getEspecialidadArea() : "Clínica General");
        JComboBox<String> cbEst = new JComboBox<>(new String[]{"Activo", "Inactivo"});
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
        bot.setBackground(Color.WHITE);
        JButton btnCan = new happypets.ui.BotonAsset("Cancelar");
        btnCan.addActionListener(e -> dlg.dispose());
        JButton btnOk = new happypets.ui.BotonAsset("Guardar Colaborador");
        btnOk.setBackground(new Color(15, 23, 42));
        btnOk.setForeground(Color.WHITE);
        btnOk.addActionListener(e -> {
            String nom = txtNom.getText().trim();
            String user = txtUser.getText().trim();
            String mail = txtMail.getText().trim();
            String pass = txtPass.getText().trim();
            if (nom.isEmpty() || user.isEmpty() || mail.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(dlg, "Por favor complete los campos obligatorios.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (uExistente == null && repo.getUsuariosSistema().stream().anyMatch(u -> u.getUsername().equalsIgnoreCase(user))) {
                JOptionPane.showMessageDialog(dlg, "Ese nombre de usuario ya existe.", "Validación", JOptionPane.WARNING_MESSAGE);
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

        java.util.List<happypets.model.RolPermiso> roles = repo.getRolesPermisos();
        JComboBox<String> cbRoles = new JComboBox<>(roles.stream().map(happypets.model.RolPermiso::getNombreRol).toArray(String[]::new));
        cbRoles.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JPanel pnlSel = new JPanel(new BorderLayout(10, 0));
        pnlSel.setBackground(Color.WHITE);
        pnlSel.add(new JLabel("Seleccione Rol: "), BorderLayout.WEST);
        pnlSel.add(cbRoles, BorderLayout.CENTER);

        // Matriz de permisos con checkboxes
        JPanel matriz = new JPanel(new GridLayout(5, 2, 12, 10));
        matriz.setBackground(Color.WHITE);
        matriz.setBorder(BorderFactory.createTitledBorder("Módulos y Privilegios Permitidos"));

        String[] modulos = happypets.auth.ServicioAutenticacion.MODULOS;

        JCheckBox[] checks = new JCheckBox[modulos.length];
        for (int i = 0; i < modulos.length; i++) {
            checks[i] = new JCheckBox(modulos[i], true);
            checks[i].setFont(new Font("Segoe UI", Font.PLAIN, 12));
            checks[i].setBackground(Color.WHITE);
            matriz.add(checks[i]);
        }

        Runnable cargarPermisos = () -> {
            happypets.model.RolPermiso rol = roles.get(cbRoles.getSelectedIndex());
            for (int i = 0; i < checks.length; i++) checks[i].setSelected(rol.tienePermiso(modulos[i]));
        };
        cbRoles.addActionListener(e -> cargarPermisos.run());
        cargarPermisos.run();

        JPanel centro = new JPanel(new BorderLayout(0, 14));
        centro.setBackground(Color.WHITE);
        centro.add(pnlSel, BorderLayout.NORTH);
        centro.add(matriz, BorderLayout.CENTER);
        pnl.add(centro, BorderLayout.CENTER);

        JPanel bot = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton btnCerrar = new happypets.ui.BotonAsset("Cerrar");
        btnCerrar.addActionListener(e -> dlg.dispose());
        JButton btnGuardar = new happypets.ui.BotonAsset("Guardar Privilegios");
        btnGuardar.setBackground(new Color(15, 23, 42));
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.addActionListener(e -> {
            happypets.model.RolPermiso rol = roles.get(cbRoles.getSelectedIndex());
            for (int i = 0; i < checks.length; i++) rol.asignarPermiso(modulos[i], checks[i].isSelected());
            repo.guardarRolPermiso(rol);
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
}
