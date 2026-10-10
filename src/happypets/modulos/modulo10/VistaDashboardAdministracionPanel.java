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
import java.io.File;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import happypets.data.RepositorioVeterinaria;
import happypets.model.ConfiguracionClinica;
import happypets.model.ConfiguracionModuloIA;
import happypets.model.DiagnosticoSistema;
import happypets.model.IntegracionExterna;
import happypets.model.Usuario;
import happypets.ui.Iconos;

/**
 * Vista Consolidada 'PÁGINA COMPLETA.pdf' del Módulo 10: Configuración, Integraciones y Soporte.
 * Muestra el dashboard integral en 2 columnas:
 *  - Columna Izquierda: Parámetros Generales + Tabla de Usuarios Activos
 *  - Columna Derecha: Módulo de IA + Integraciones Externas + Soporte Técnico
 */
public class VistaDashboardAdministracionPanel extends happypets.ui.AssetsModulo {
    private static final long serialVersionUID = 1L;

    private final RepositorioVeterinaria repo = RepositorioVeterinaria.getInstancia();

    // Campos de Parámetros Generales
    private JTextField txtRazonSocial;
    private JTextField txtNombreComercial;
    private JTextField txtIdentificadorFiscal;
    private JTextField txtCorreo;
    private JTextField txtTelefono;
    private JTextField txtDireccion;
    private JComboBox<String> cbMoneda;
    private JComboBox<String> cbZonaHoraria;
    private JComboBox<String> cbSedeTop;

    // Tabla de Usuarios
    private JTable tablaUsuarios;
    private DefaultTableModel modeloUsuarios;
    private JLabel lblPaginacionUsuarios;

    // Toggles de IA
    private JCheckBox chkPreTriaje;
    private JCheckBox chkDiagnostico;
    private JCheckBox chkVacunas;

    // Panel de Integraciones
    private JPanel panelIntegraciones;
    private JLabel lblContadorIntegraciones;

    // Métricas de Soporte
    private JLabel lblEstadoBD;
    private JLabel lblUltimoRespaldo;
    private JLabel lblTicketsPendientes;

    public VistaDashboardAdministracionPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        inicializarUI();
        cargarDatos();
    }

    private void inicializarUI() {
        JPanel contenedor = new JPanel();
        contenedor.setLayout(new BoxLayout(contenedor, BoxLayout.Y_AXIS));
        contenedor.setBackground(Color.WHITE);
        contenedor.setOpaque(false);
        contenedor.setBorder(BorderFactory.createEmptyBorder(16, 24, 24, 24));

        // 1. Barra Superior con Buscador, Selector de Sede y Perfil de Usuario
        contenedor.add(crearTopBar());
        contenedor.add(Box.createVerticalStrut(16));

        // 2. Encabezado Institucional (Breadcrumb + Título + Botones Guardar/Restablecer)
        contenedor.add(crearBarraEncabezado());
        contenedor.add(Box.createVerticalStrut(14));

        // 3. Barra de Pestañas / Píldoras Rápidas
        contenedor.add(crearBarraPestañasPildoras());
        contenedor.add(Box.createVerticalStrut(18));

        // 4. Panel de Dos Columnas (Dashboard Maestro)
        contenedor.add(crearGridDosColumnas());

        JScrollPane scroll = new JScrollPane(contenedor);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.getViewport().setBackground(Color.WHITE);
        add(scroll, BorderLayout.CENTER);
    }

    private JPanel crearTopBar() {
        JPanel top = new JPanel(new BorderLayout(16, 0));
        top.setBackground(Color.WHITE);
        top.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                BorderFactory.createEmptyBorder(10, 16, 10, 16)
        ));

        // Buscador
        JPanel izq = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        izq.setBackground(Color.WHITE);
        JTextField txtBuscarTop = new JTextField("Buscar parámetro, usuario o configuración...", 26);
        txtBuscarTop.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtBuscarTop.setForeground(new Color(100, 116, 139));
        txtBuscarTop.setPreferredSize(new Dimension(300, 32));
        izq.add(txtBuscarTop);
        izq.add(new happypets.ui.EtiquetaAsset("🔍"));
        top.add(izq, BorderLayout.WEST);

        // Selector de Sede y Usuario Administrador
        JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 0));
        der.setBackground(Color.WHITE);

        JLabel lblSedeT = new JLabel("Sede:");
        lblSedeT.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSedeT.setForeground(new Color(100, 116, 139));

        cbSedeTop = new JComboBox<>(new String[]{"Sede Norte - Principal", "Sede Sur - Miraflores", "Sede Este - La Molina"});
        cbSedeTop.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cbSedeTop.setBackground(Color.WHITE);

        der.add(lblSedeT);
        der.add(cbSedeTop);

        // Perfil
        JPanel perfil = new JPanel(new BorderLayout(8, 0));
        perfil.setBackground(Color.WHITE);
        JLabel lblAvatar = new JLabel(Iconos.crearIconoDoctor(28, new Color(59, 130, 246)));

        JPanel textosUser = new JPanel();
        textosUser.setLayout(new BoxLayout(textosUser, BoxLayout.Y_AXIS));
        textosUser.setBackground(Color.WHITE);
        JLabel lblNomU = new JLabel("Dr. Roberto Torres");
        lblNomU.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblNomU.setForeground(new Color(15, 23, 42));
        JLabel lblRolU = new JLabel("Administrador General");
        lblRolU.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblRolU.setForeground(new Color(100, 116, 139));
        textosUser.add(lblNomU);
        textosUser.add(lblRolU);

        perfil.add(lblAvatar, BorderLayout.WEST);
        perfil.add(textosUser, BorderLayout.CENTER);
        der.add(perfil);

        top.add(der, BorderLayout.EAST);
        return top;
    }

    private JPanel crearBarraEncabezado() {
        JPanel header = new JPanel(new BorderLayout(15, 6));
        header.setBackground(Color.WHITE);

        JPanel izq = new JPanel();
        izq.setLayout(new BoxLayout(izq, BoxLayout.Y_AXIS));
        izq.setBackground(Color.WHITE);

        JLabel lblBreadcrumb = new JLabel("Ajustes > Configuración Empresarial");
        lblBreadcrumb.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblBreadcrumb.setForeground(new Color(100, 116, 139));
        izq.add(lblBreadcrumb);
        izq.add(Box.createVerticalStrut(2));

        JLabel lblTitulo = new JLabel("Administración de HappyPets");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(new Color(15, 23, 42));
        izq.add(lblTitulo);
        izq.add(Box.createVerticalStrut(2));

        JLabel lblSub = new JLabel("Configuración centralizada de parámetros, control de acceso, IA y servicios conectados de la clínica.");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(new Color(100, 116, 139));
        izq.add(lblSub);

        header.add(izq, BorderLayout.CENTER);

        JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        der.setBackground(Color.WHITE);

        JButton btnRestablecer = new happypets.ui.BotonAsset("Restablecer Valores");
        btnRestablecer.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnRestablecer.setForeground(new Color(51, 65, 85));
        btnRestablecer.setBackground(Color.WHITE);
        btnRestablecer.setFocusPainted(false);
        btnRestablecer.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRestablecer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225), 1),
                BorderFactory.createEmptyBorder(8, 16, 8, 16)
        ));
        btnRestablecer.addActionListener(e -> cargarDatos());
        der.add(btnRestablecer);

        JButton btnGuardar = new happypets.ui.BotonAsset("💾  Guardar Todos los Cambios");
        btnGuardar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setBackground(new Color(15, 23, 42));
        btnGuardar.setFocusPainted(false);
        btnGuardar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnGuardar.setBorder(BorderFactory.createEmptyBorder(9, 18, 9, 18));
        btnGuardar.addActionListener(e -> guardarTodo());
        der.add(btnGuardar);

        header.add(der, BorderLayout.EAST);
        return header;
    }

    private JPanel crearBarraPestañasPildoras() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        p.setBackground(Color.WHITE);

        p.add(crearPildora("Parámetros Generales", true, null));
        p.add(crearPildora("Usuarios (14)", false, null));
        p.add(crearPildora("Roles y Permisos", false, null));
        p.add(crearPildora("Integraciones Externas (5)", false, null));
        p.add(crearPildora("Módulo de IA", false, "Activo"));
        p.add(crearPildora("Soporte Técnico", false, null));

        return p;
    }

    private JButton crearPildora(String texto, boolean activo, String badge) {
        String label = texto + (badge != null ? " [" + badge + "]" : "");
        JButton b = new happypets.ui.BotonAsset(label);
        b.setFont(new Font("Segoe UI", activo ? Font.BOLD : Font.PLAIN, 13));
        b.setForeground(activo ? new Color(15, 23, 42) : new Color(100, 116, 139));
        b.setBackground(Color.WHITE);
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(activo ? new Color(15, 23, 42) : new Color(226, 232, 240), activo ? 2 : 1),
                BorderFactory.createEmptyBorder(6, 14, 6, 14)
        ));
        return b;
    }

    private JPanel crearGridDosColumnas() {
        JPanel grid = new JPanel(new GridBagLayout());
        grid.setBackground(Color.WHITE);
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(0, 0, 0, 16);
        g.fill = GridBagConstraints.BOTH;

        // Columna Izquierda (65% del ancho)
        JPanel colIzquierda = new JPanel();
        colIzquierda.setLayout(new BoxLayout(colIzquierda, BoxLayout.Y_AXIS));
        colIzquierda.setBackground(Color.WHITE);

        colIzquierda.add(crearCardDatosGenerales());
        colIzquierda.add(Box.createVerticalStrut(18));
        colIzquierda.add(crearCardUsuarios());

        g.gridx = 0; g.gridy = 0; g.weightx = 0.65; g.weighty = 1.0;
        grid.add(colIzquierda, g);

        // Columna Derecha (35% del ancho)
        JPanel colDerecha = new JPanel();
        colDerecha.setLayout(new BoxLayout(colDerecha, BoxLayout.Y_AXIS));
        colDerecha.setBackground(Color.WHITE);

        colDerecha.add(crearCardModuloIA());
        colDerecha.add(Box.createVerticalStrut(18));
        colDerecha.add(crearCardIntegraciones());
        colDerecha.add(Box.createVerticalStrut(18));
        colDerecha.add(crearCardSoporteTecnico());

        g.gridx = 1; g.gridy = 0; g.weightx = 0.35; g.weighty = 1.0;
        g.insets = new Insets(0, 0, 0, 0);
        grid.add(colDerecha, g);

        return grid;
    }

    // Columna Izquierda - Tarjeta 1: Datos Generales
    private JPanel crearCardDatosGenerales() {
        JPanel card = new JPanel(new BorderLayout(0, 14));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                BorderFactory.createEmptyBorder(18, 22, 18, 22)
        ));

        // Cabecera
        JPanel head = new JPanel(new BorderLayout());
        head.setBackground(Color.WHITE);
        JLabel lblTit = new JLabel("Datos Generales de la Clínica Veterinaria");
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTit.setForeground(new Color(15, 23, 42));

        txtIdentificadorFiscal = new JTextField(12);
        txtIdentificadorFiscal.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        JPanel pId = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        pId.setBackground(Color.WHITE);
        pId.add(new JLabel("Identificador Fiscal:"));
        pId.add(txtIdentificadorFiscal);

        head.add(lblTit, BorderLayout.WEST);
        head.add(pId, BorderLayout.EAST);
        card.add(head, BorderLayout.NORTH);

        // Formulario
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 6, 6, 6);
        g.fill = GridBagConstraints.HORIZONTAL;

        txtRazonSocial = new JTextField();
        txtNombreComercial = new JTextField();
        txtCorreo = new JTextField();
        txtTelefono = new JTextField();
        txtDireccion = new JTextField();

        cbMoneda = new JComboBox<>(new String[]{"COP ($) - Peso Colombiano", "PEN (S/) - Sol Peruano", "USD ($) - Dólar"});
        cbMoneda.setBackground(Color.WHITE);
        cbZonaHoraria = new JComboBox<>(new String[]{"America/Bogota (UTC -05:00)", "America/Lima (UTC -05:00)"});
        cbZonaHoraria.setBackground(Color.WHITE);

        g.gridx = 0; g.gridy = 0; g.weightx = 0.5;
        form.add(crearCampo("Razón Social / Nombre Legal", txtRazonSocial), g);
        g.gridx = 1; g.gridy = 0; g.weightx = 0.5;
        form.add(crearCampo("Nombre Comercial", txtNombreComercial), g);

        g.gridx = 0; g.gridy = 1;
        form.add(crearCampo("Correo Electrónico Institucional", txtCorreo), g);
        g.gridx = 1; g.gridy = 1;
        form.add(crearCampo("Línea Telefónica de Urgencias", txtTelefono), g);

        g.gridx = 0; g.gridy = 2; g.gridwidth = 2;
        form.add(crearCampo("Dirección Sede Principal", txtDireccion), g);
        g.gridwidth = 1;

        g.gridx = 0; g.gridy = 3;
        form.add(crearCampo("Moneda Principal", cbMoneda), g);
        g.gridx = 1; g.gridy = 3;
        form.add(crearCampo("Zona Horaria", cbZonaHoraria), g);

        // Logo row
        JPanel pLogo = new JPanel(new BorderLayout(14, 0));
        pLogo.setBackground(Color.WHITE);
        pLogo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)
        ));

        JLabel lblBox = new JLabel(Iconos.crearIconoHuella(26, new Color(59, 130, 246)));
        JPanel txts = new JPanel();
        txts.setLayout(new BoxLayout(txts, BoxLayout.Y_AXIS));
        txts.setBackground(Color.WHITE);
        JLabel l1 = new JLabel("Logotipo Oficial para Recetas y Facturas");
        l1.setFont(new Font("Segoe UI", Font.BOLD, 12));
        JLabel l2 = new JLabel("Formatos aceptados: PNG, JPG, SVG. Máx 2MB");
        l2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        l2.setForeground(new Color(100, 116, 139));
        txts.add(l1);
        txts.add(l2);

        JButton btnLogo = new happypets.ui.BotonAsset("Actualizar Imagen");
        btnLogo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnLogo.setBackground(Color.WHITE);
        btnLogo.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogo.addActionListener(e -> seleccionarLogo());

        pLogo.add(lblBox, BorderLayout.WEST);
        pLogo.add(txts, BorderLayout.CENTER);
        pLogo.add(btnLogo, BorderLayout.EAST);

        g.gridx = 0; g.gridy = 4; g.gridwidth = 2;
        form.add(pLogo, g);

        card.add(form, BorderLayout.CENTER);
        return card;
    }

    private JPanel crearCampo(String tit, Component c) {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setBackground(Color.WHITE);
        JLabel l = new JLabel(tit);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setForeground(new Color(71, 85, 105));
        p.add(l, BorderLayout.NORTH);
        p.add(c, BorderLayout.CENTER);
        return p;
    }

    // Columna Izquierda - Tarjeta 2: Usuarios Activos
    private JPanel crearCardUsuarios() {
        JPanel card = new JPanel(new BorderLayout(0, 12));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                BorderFactory.createEmptyBorder(18, 22, 18, 22)
        ));

        // Cabecera con Botones
        JPanel head = new JPanel(new BorderLayout());
        head.setBackground(Color.WHITE);

        JPanel izq = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        izq.setBackground(Color.WHITE);
        izq.add(new JLabel(Iconos.crearIconoUsuario(20, new Color(15, 23, 42))));
        JLabel lblTit = new JLabel("Usuarios Activos y Asignación de Roles");
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 15));
        izq.add(lblTit);

        JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        der.setBackground(Color.WHITE);

        JButton btnGest = new happypets.ui.BotonAsset("Gestionar Roles");
        btnGest.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnGest.setBackground(new Color(241, 245, 249));
        btnGest.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnGest.addActionListener(e -> JOptionPane.showMessageDialog(this, "Acceso a matriz de roles y permisos.", "Roles", JOptionPane.INFORMATION_MESSAGE));

        JButton btnNew = new happypets.ui.BotonAsset("+ Nuevo Usuario");
        btnNew.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnNew.setBackground(new Color(15, 23, 42));
        btnNew.setForeground(Color.WHITE);
        btnNew.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnNew.addActionListener(e -> JOptionPane.showMessageDialog(this, "Formulario de alta de usuario.", "Nuevo Usuario", JOptionPane.INFORMATION_MESSAGE));

        der.add(btnGest);
        der.add(btnNew);

        head.add(izq, BorderLayout.WEST);
        head.add(der, BorderLayout.EAST);
        card.add(head, BorderLayout.NORTH);

        // Tabla
        String[] cols = {"Usuario / Nombre", "Rol Asignado", "Especialidad / Área", "Estado", "Acciones"};
        modeloUsuarios = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaUsuarios = new JTable(modeloUsuarios);
        tablaUsuarios.setRowHeight(48);
        tablaUsuarios.setShowVerticalLines(false);
        tablaUsuarios.setGridColor(new Color(241, 245, 249));
        tablaUsuarios.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tablaUsuarios.getTableHeader().setBackground(Color.WHITE);

        tablaUsuarios.getColumnModel().getColumn(0).setPreferredWidth(200);
        tablaUsuarios.getColumnModel().getColumn(1).setPreferredWidth(140);
        tablaUsuarios.getColumnModel().getColumn(2).setPreferredWidth(140);
        tablaUsuarios.getColumnModel().getColumn(3).setPreferredWidth(90);
        tablaUsuarios.getColumnModel().getColumn(4).setPreferredWidth(130);

        tablaUsuarios.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
                super.getTableCellRendererComponent(t, v, s, f, r, c);
                String est = v != null ? v.toString() : "Activo";
                setText("● " + est);
                setForeground("Activo".equalsIgnoreCase(est) ? new Color(34, 197, 94) : new Color(148, 163, 184));
                setFont(new Font("Segoe UI", Font.BOLD, 12));
                return this;
            }
        });

        JScrollPane sc = new JScrollPane(tablaUsuarios);
        sc.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240), 1));
        sc.setPreferredSize(new Dimension(500, 240));
        card.add(sc, BorderLayout.CENTER);

        // Pie de paginación
        JPanel foot = new JPanel(new BorderLayout());
        foot.setBackground(Color.WHITE);
        foot.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));

        lblPaginacionUsuarios = new JLabel("Mostrando 4 de 14 colaboradores");
        lblPaginacionUsuarios.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblPaginacionUsuarios.setForeground(new Color(100, 116, 139));

        JPanel pPags = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
        pPags.setBackground(Color.WHITE);
        JButton bAnt = new happypets.ui.BotonAsset("Anterior");
        bAnt.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        JButton b1 = new happypets.ui.BotonAsset("1");
        b1.setBackground(new Color(15, 23, 42));
        b1.setForeground(Color.WHITE);
        JButton b2 = new happypets.ui.BotonAsset("2");
        b2.setBackground(Color.WHITE);
        JButton bSig = new happypets.ui.BotonAsset("Siguiente");
        bSig.setFont(new Font("Segoe UI", Font.PLAIN, 11));

        pPags.add(bAnt);
        pPags.add(b1);
        pPags.add(b2);
        pPags.add(bSig);

        foot.add(lblPaginacionUsuarios, BorderLayout.WEST);
        foot.add(pPags, BorderLayout.EAST);
        card.add(foot, BorderLayout.SOUTH);

        return card;
    }

    // Columna Derecha - Tarjeta 1: Módulo de IA
    private JPanel crearCardModuloIA() {
        JPanel card = new JPanel(new BorderLayout(0, 12));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                BorderFactory.createEmptyBorder(18, 20, 18, 20)
        ));

        JPanel head = new JPanel(new BorderLayout());
        head.setBackground(Color.WHITE);
        JLabel lblTit = new JLabel("Módulo de IA HappyPets");
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 15));

        JLabel badgeH = new JLabel("Habilitado");
        badgeH.setFont(new Font("Segoe UI", Font.BOLD, 11));
        badgeH.setBackground(new Color(241, 245, 249));
        badgeH.setOpaque(true);
        badgeH.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));

        head.add(lblTit, BorderLayout.WEST);
        head.add(badgeH, BorderLayout.EAST);
        card.add(head, BorderLayout.NORTH);

        JPanel cuerpo = new JPanel();
        cuerpo.setLayout(new BoxLayout(cuerpo, BoxLayout.Y_AXIS));
        cuerpo.setBackground(Color.WHITE);

        JLabel lblSub = new JLabel("Configura los asistentes inteligentes para triaje clínico, diagnóstico sugerido y respuesta rápida al cliente.");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(new Color(100, 116, 139));
        cuerpo.add(lblSub);
        cuerpo.add(Box.createVerticalStrut(10));

        chkPreTriaje = new JCheckBox("Pre-Triaje de Mascotas con IA", true);
        chkDiagnostico = new JCheckBox("Asistente Diagnóstico Preliminar", true);
        chkVacunas = new JCheckBox("Recordatorios Predictivos de Vacunas", false);

        cuerpo.add(crearMiniToggle(chkPreTriaje, "Categoriza urgencias al registrar ingreso."));
        cuerpo.add(Box.createVerticalStrut(8));
        cuerpo.add(crearMiniToggle(chkDiagnostico, "Sugiere posibles patologías por síntomas."));
        cuerpo.add(Box.createVerticalStrut(8));
        cuerpo.add(crearMiniToggle(chkVacunas, "Calcula fechas óptimas por raza/edad."));
        cuerpo.add(Box.createVerticalStrut(12));

        JPanel pie = new JPanel(new BorderLayout());
        pie.setBackground(Color.WHITE);
        JLabel lMod = new JLabel("Modelo actual: HappyPet-Core-v1.8");
        lMod.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lMod.setForeground(new Color(71, 85, 105));

        JButton btnPrompts = new happypets.ui.BotonAsset("Ajustes de Prompts");
        btnPrompts.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btnPrompts.setBackground(new Color(241, 245, 249));
        btnPrompts.addActionListener(e -> JOptionPane.showMessageDialog(this, "System prompt configurado para triaje veterinario.", "Prompts", JOptionPane.INFORMATION_MESSAGE));

        pie.add(lMod, BorderLayout.WEST);
        pie.add(btnPrompts, BorderLayout.EAST);
        cuerpo.add(pie);

        card.add(cuerpo, BorderLayout.CENTER);
        return card;
    }

    private JPanel crearMiniToggle(JCheckBox chk, String desc) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));

        JPanel txt = new JPanel();
        txt.setLayout(new BoxLayout(txt, BoxLayout.Y_AXIS));
        txt.setBackground(Color.WHITE);
        JLabel t = new JLabel(chk.getText());
        t.setFont(new Font("Segoe UI", Font.BOLD, 12));
        JLabel d = new JLabel(desc);
        d.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        d.setForeground(new Color(100, 116, 139));
        txt.add(t);
        txt.add(d);

        chk.setText("");
        chk.setBackground(Color.WHITE);
        p.add(txt, BorderLayout.CENTER);
        p.add(chk, BorderLayout.EAST);
        return p;
    }

    // Columna Derecha - Tarjeta 2: Integraciones Externas
    private JPanel crearCardIntegraciones() {
        JPanel card = new JPanel(new BorderLayout(0, 12));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                BorderFactory.createEmptyBorder(18, 20, 18, 20)
        ));

        JPanel head = new JPanel(new BorderLayout());
        head.setBackground(Color.WHITE);
        JLabel lblTit = new JLabel("Integraciones Externas");
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 15));

        lblContadorIntegraciones = new JLabel("3 de 5 activas");
        lblContadorIntegraciones.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblContadorIntegraciones.setForeground(new Color(100, 116, 139));

        head.add(lblTit, BorderLayout.WEST);
        head.add(lblContadorIntegraciones, BorderLayout.EAST);
        card.add(head, BorderLayout.NORTH);

        panelIntegraciones = new JPanel();
        panelIntegraciones.setLayout(new BoxLayout(panelIntegraciones, BoxLayout.Y_AXIS));
        panelIntegraciones.setBackground(Color.WHITE);

        card.add(panelIntegraciones, BorderLayout.CENTER);
        return card;
    }

    // Columna Derecha - Tarjeta 3: Soporte Técnico
    private JPanel crearCardSoporteTecnico() {
        JPanel card = new JPanel(new BorderLayout(0, 12));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                BorderFactory.createEmptyBorder(18, 20, 18, 20)
        ));

        JPanel head = new JPanel(new BorderLayout());
        head.setBackground(Color.WHITE);
        JPanel izq = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        izq.setBackground(Color.WHITE);
        izq.add(new JLabel(Iconos.crearIconoAuriculares(18, new Color(15, 23, 42))));
        JLabel lblTit = new JLabel("Soporte Técnico y Diagnóstico");
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 15));
        izq.add(lblTit);

        JLabel lblNorm = new JLabel("Estado: Normal");
        lblNorm.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblNorm.setForeground(new Color(30, 41, 59));

        head.add(izq, BorderLayout.WEST);
        head.add(lblNorm, BorderLayout.EAST);
        card.add(head, BorderLayout.NORTH);

        JPanel metricas = new JPanel(new GridLayout(3, 2, 8, 6));
        metricas.setBackground(Color.WHITE);
        metricas.setBorder(BorderFactory.createEmptyBorder(4, 0, 8, 0));

        lblEstadoBD = new JLabel("En línea (0.12 ms)");
        lblEstadoBD.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblEstadoBD.setForeground(new Color(34, 197, 94));

        lblUltimoRespaldo = new JLabel("Hoy, 03:00 AM");
        lblUltimoRespaldo.setFont(new Font("Segoe UI", Font.BOLD, 12));

        lblTicketsPendientes = new JLabel("0 pendientes");
        lblTicketsPendientes.setFont(new Font("Segoe UI", Font.BOLD, 12));

        metricas.add(new JLabel("Estado de Base de Datos:"));
        metricas.add(lblEstadoBD);
        metricas.add(new JLabel("Último Respaldo Cloud:"));
        metricas.add(lblUltimoRespaldo);
        metricas.add(new JLabel("Tickets de Soporte Abiertos:"));
        metricas.add(lblTicketsPendientes);

        card.add(metricas, BorderLayout.CENTER);

        JPanel bot = new JPanel(new GridLayout(1, 2, 8, 0));
        bot.setBackground(Color.WHITE);

        JButton btnLogs = new happypets.ui.BotonAsset("Ver Logs Sistema");
        btnLogs.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnLogs.setBackground(new Color(241, 245, 249));
        btnLogs.addActionListener(e -> JOptionPane.showMessageDialog(this, "Logs del servidor: Estado óptimo, sin anomalías.", "Logs", JOptionPane.INFORMATION_MESSAGE));

        JButton btnTicket = new happypets.ui.BotonAsset("🎫  Crear Ticket");
        btnTicket.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnTicket.setBackground(new Color(241, 245, 249));
        btnTicket.addActionListener(e -> JOptionPane.showMessageDialog(this, "Formulario para reportar incidencia técnica.", "Nuevo Ticket", JOptionPane.INFORMATION_MESSAGE));

        bot.add(btnLogs);
        bot.add(btnTicket);
        card.add(bot, BorderLayout.SOUTH);

        return card;
    }

    private void cargarDatos() {
        ConfiguracionClinica cfg = repo.getConfiguracionClinica();
        if (cfg != null) {
            txtRazonSocial.setText(cfg.getRazonSocial());
            txtNombreComercial.setText(cfg.getNombreComercial());
            txtIdentificadorFiscal.setText(cfg.getIdentificadorFiscal());
            txtCorreo.setText(cfg.getCorreoInstitucional());
            txtTelefono.setText(cfg.getTelefonoUrgencias());
            txtDireccion.setText(cfg.getDireccionSedePrincipal());
            cbMoneda.setSelectedItem(cfg.getMonedaPrincipal());
            cbZonaHoraria.setSelectedItem(cfg.getZonaHoraria());
            cbSedeTop.setSelectedItem(cfg.getSedeActiva());
        }

        // Cargar 4 primeros usuarios como en el wireframe
        modeloUsuarios.setRowCount(0);
        List<Usuario> us = repo.getUsuariosSistema();
        int max = Math.min(4, us.size());
        for (int i = 0; i < max; i++) {
            Usuario u = us.get(i);
            modeloUsuarios.addRow(new Object[]{
                    u.getNombreCompleto() + " (" + u.getCorreo() + ")",
                    u.getRol(),
                    u.getEspecialidadArea(),
                    u.getEstado(),
                    "Permisos | Editar"
            });
        }
        lblPaginacionUsuarios.setText("Mostrando " + max + " de " + us.size() + " colaboradores");

        // Cargar Integraciones
        panelIntegraciones.removeAll();
        List<IntegracionExterna> ints = repo.getIntegracionesExternas();
        long act = ints.stream().filter(IntegracionExterna::isActiva).count();
        lblContadorIntegraciones.setText(act + " de " + ints.size() + " activas");

        for (int i = 0; i < Math.min(4, ints.size()); i++) {
            IntegracionExterna in = ints.get(i);
            JPanel r = new JPanel(new BorderLayout(8, 0));
            r.setBackground(Color.WHITE);
            r.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                    BorderFactory.createEmptyBorder(6, 10, 6, 10)
            ));

            JPanel t = new JPanel();
            t.setLayout(new BoxLayout(t, BoxLayout.Y_AXIS));
            t.setBackground(Color.WHITE);
            JLabel lN = new JLabel(in.getNombre() + " " + (in.isActiva() ? "●" : "○"));
            lN.setFont(new Font("Segoe UI", Font.BOLD, 12));
            lN.setForeground(new Color(15, 23, 42));
            JLabel lS = new JLabel(in.getSubtitulo());
            lS.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            lS.setForeground(new Color(100, 116, 139));
            t.add(lN);
            t.add(lS);

            JButton b = new happypets.ui.BotonAsset(in.isActiva() ? "Configurar" : "Conectar");
            b.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            b.setBackground(Color.WHITE);
            b.addActionListener(e -> {
                repo.conmutarEstadoIntegracion(in.getId());
                cargarDatos();
            });

            r.add(t, BorderLayout.CENTER);
            r.add(b, BorderLayout.EAST);
            panelIntegraciones.add(r);
            panelIntegraciones.add(Box.createVerticalStrut(6));
        }

        // Diagnóstico
        DiagnosticoSistema diag = repo.getDiagnosticoSistema();
        if (diag != null) {
            lblEstadoBD.setText(diag.getEstadoBaseDatos());
            lblUltimoRespaldo.setText(diag.getUltimoRespaldoCloud());
            lblTicketsPendientes.setText(diag.getTicketsPendientes() + " pendientes");
        }

        panelIntegraciones.revalidate();
        panelIntegraciones.repaint();
    }

    private void guardarTodo() {
        ConfiguracionClinica cfg = new ConfiguracionClinica(
                txtRazonSocial.getText().trim(),
                txtNombreComercial.getText().trim(),
                txtIdentificadorFiscal.getText().trim(),
                txtCorreo.getText().trim(),
                txtTelefono.getText().trim(),
                txtDireccion.getText().trim(),
                (String) cbMoneda.getSelectedItem(),
                (String) cbZonaHoraria.getSelectedItem(),
                "assets/logo_happypets.png",
                (String) cbSedeTop.getSelectedItem()
        );
        repo.guardarConfiguracionClinica(cfg);

        ConfiguracionModuloIA ia = repo.getConfiguracionModuloIA();
        if (ia != null) {
            ia.setPreTriajeMascotas(chkPreTriaje.isSelected());
            ia.setAsistenteDiagnosticoPreliminar(chkDiagnostico.isSelected());
            ia.setRecordatoriosPredictivosVacunas(chkVacunas.isSelected());
            repo.guardarConfiguracionModuloIA(ia);
        }

        JOptionPane.showMessageDialog(
                this,
                "✓ Configuración de HappyPets guardada exitosamente.\n"
                        + "Todos los módulos del ERP se encuentran sincronizados con los parámetros actuales.",
                "Cambios Guardados",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void seleccionarLogo() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File f = chooser.getSelectedFile();
            JOptionPane.showMessageDialog(this, "✓ Logotipo cargado: " + f.getName(), "Logotipo", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
