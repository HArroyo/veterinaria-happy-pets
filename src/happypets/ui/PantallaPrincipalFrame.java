package happypets.ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
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
import java.util.Optional;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import happypets.auth.ServicioAutenticacion;
import happypets.data.RepositorioVeterinaria;
import happypets.model.Cliente;
import happypets.model.Mascota;
import happypets.model.Usuario;
import happypets.model.Cita;
import happypets.modulos.modulo1.ClientesMascotasFrame;
import happypets.modulos.modulo1.ConstanciasCertificadosFrame;
import happypets.modulos.modulo1.HistorialClinicoFrame;
import happypets.modulos.modulo1.VistaClientesMascotasPanel;
import happypets.modulos.modulo1.VistaHistorialClinicoPanel;
import happypets.modulos.modulo1.VistaConstanciasCertificadosPanel;
import happypets.modulos.modulo2.Modulo2AgendaCitasFrame;
import happypets.modulos.modulo2.VistaAgendamientoCitasPanel;
import happypets.modulos.modulo2.VistaCalendarioGlobalPanel;
import happypets.modulos.modulo2.VistaRecordatoriosPanel;
import happypets.modulos.modulo2.VistaSalaEsperaTriajePanel;
import happypets.modulos.modulo3.Modulo3ServiciosMedicosFrame;
import happypets.modulos.modulo3.VistaConsultasMedicasPanel;
import happypets.modulos.modulo3.VistaVacunacionDesparasitacionPanel;
import happypets.modulos.modulo3.VistaCirugiasQuirofanoPanel;
import happypets.modulos.modulo3.VistaLaboratorioImagenesPanel;
import happypets.modulos.modulo4.Modulo4EsteticosHospedajeFrame;
import happypets.modulos.modulo4.VistaGroomingPeluqueriaPanel;
import happypets.modulos.modulo4.VistaHospitalizacionPanel;
import happypets.modulos.modulo4.VistaHotelGuarderiaPanel;
import happypets.modulos.modulo4.VistaAdopcionesPanel;
import happypets.modulos.modulo5.Modulo5InventarioFarmaciaFrame;
import happypets.modulos.modulo5.VistaCatalogoProductosPanel;
import happypets.modulos.modulo5.VistaControlStockLotesPanel;
import happypets.modulos.modulo5.VistaProveedoresOrdenesPanel;
import happypets.modulos.modulo5.VistaAjustesMermasPanel;
import happypets.modulos.modulo6.Modulo6FinanzasVentasFrame;
import happypets.modulos.modulo6.VistaPuntoVentaPOSPanel;
import happypets.modulos.modulo6.VistaCuentasCobrarPagarPanel;
import happypets.modulos.modulo6.VistaControlCajaChicaPanel;
import happypets.modulos.modulo6.VistaEgresosOperativosPanel;
import happypets.modulos.modulo7.Modulo7PersonalRRHHFrame;
import happypets.modulos.modulo7.VistaVeterinariosPanel;
import happypets.modulos.modulo7.VistaPersonalApoyoPanel;
import happypets.modulos.modulo7.VistaHorariosTurnosPanel;
import happypets.modulos.modulo7.VistaAsistenciasPermisosPanel;
import happypets.modulos.modulo8.Modulo8ReportesBIFrame;
import happypets.modulos.modulo8.VistaTablerosMandoPanel;
import happypets.modulos.modulo8.VistaReportesClinicosPanel;
import happypets.modulos.modulo8.VistaReportesFinancierosPanel;
import happypets.modulos.modulo8.VistaExportadorDatosPanel;
import happypets.modulos.modulo9.Modulo9NotificacionesAuditoriaFrame;
import happypets.modulos.modulo9.VistaCentroNotificacionesPanel;
import happypets.modulos.modulo9.VistaConfiguracionCanalesPanel;
import happypets.modulos.modulo9.VistaRepositorioDocumentalPanel;
import happypets.modulos.modulo9.VistaLogsTrazabilidadPanel;
import happypets.modulos.modulo10.Modulo10ConfiguracionSoporteFrame;
import happypets.modulos.modulo10.VistaDashboardAdministracionPanel;
import happypets.modulos.modulo10.VistaParametrosGeneralesPanel;
import happypets.modulos.modulo10.VistaUsuariosRolesPanel;
import happypets.modulos.modulo10.VistaIntegracionesExternasPanel;
import happypets.modulos.modulo10.VistaModuloIASoportePanel;

/**
 * Pantalla Principal (Dashboard Moderno Veterinario con Menú Lateral Izquierdo).
 * Diseñado según la arquitectura ERP web moderna solicitada por el usuario:
 * - Menú lateral a la izquierda (Sidebar) con Dashboard y los 10 módulos con submenús colapsables.
 * - Barra superior con buscador integrado, notificaciones y perfil de usuario (Harry Martin Arroyo Preciado).
 * - Área de contenido con Tarjetas KPI estilizadas, barra de filtros y tabla interactiva.
 */
public class PantallaPrincipalFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    // Colores del sistema basados en la referencia web
    private static final Color COLOR_FONDO_APP = Ui.FONDO;
    private static final Color COLOR_SIDEBAR = Color.WHITE;
    private static final Color COLOR_BORDE = Ui.BORDE_SUAVE;
    private static final Color COLOR_AZUL_PRIMARIO = Ui.TURQUESA;
    private static final Color COLOR_PILL_ACTIVO = Ui.TURQUESA_SUAVE;
    private static final Color COLOR_TEXTO_ACTIVO = Ui.TURQUESA_PROFUNDO;
    private static final Color COLOR_TEXTO_TITULO = Ui.TEXTO_TITULO;
    private static final Color COLOR_TEXTO_MUTED = Ui.TEXTO_MUTED;

    private final RepositorioVeterinaria repo = RepositorioVeterinaria.getInstancia();
    private final ServicioAutenticacion auth = ServicioAutenticacion.getInstancia();

    // Vistas integradas del Módulo 1 en CardLayout
    private final CardLayout layoutCards = new CardLayout();
    private final JPanel panelContenedorCards = new JPanel(layoutCards);
    private VistaClientesMascotasPanel vistaClientesMascotas;
    private VistaHistorialClinicoPanel vistaHistorialClinico;
    private VistaConstanciasCertificadosPanel vistaConstanciasCertificados;

    // Vistas integradas del Módulo 2 en CardLayout
    private VistaAgendamientoCitasPanel vistaAgendamiento;
    private VistaCalendarioGlobalPanel vistaCalendario;
    private VistaRecordatoriosPanel vistaRecordatorios;
    private VistaSalaEsperaTriajePanel vistaSalaEspera;

    // Vistas integradas del Módulo 3 en CardLayout
    private VistaConsultasMedicasPanel vistaConsultasMedicas;
    private VistaVacunacionDesparasitacionPanel vistaVacunacion;
    private VistaCirugiasQuirofanoPanel vistaCirugias;
    private VistaLaboratorioImagenesPanel vistaLaboratorio;

    // Vistas integradas del Módulo 4 en CardLayout
    private VistaGroomingPeluqueriaPanel vistaGrooming;
    private VistaHospitalizacionPanel vistaHospitalizacion;
    private VistaHotelGuarderiaPanel vistaHotel;
    private VistaAdopcionesPanel vistaAdopciones;

    // Vistas integradas del Módulo 5 en CardLayout
    private VistaCatalogoProductosPanel vistaCatalogo;
    private VistaControlStockLotesPanel vistaStockLotes;
    private VistaProveedoresOrdenesPanel vistaProveedoresOrdenes;
    private VistaAjustesMermasPanel vistaAjustesMermas;

    // Vistas integradas del Módulo 6 en CardLayout
    private VistaPuntoVentaPOSPanel vistaPOS;
    private VistaCuentasCobrarPagarPanel vistaCuentas;
    private VistaControlCajaChicaPanel vistaCajaChica;
    private VistaEgresosOperativosPanel vistaEgresos;

    // Vistas integradas del Módulo 7 en CardLayout
    private VistaVeterinariosPanel vistaVeterinarios;
    private VistaPersonalApoyoPanel vistaPersonalApoyo;
    private VistaHorariosTurnosPanel vistaHorariosTurnos;
    private VistaAsistenciasPermisosPanel vistaAsistenciasPermisos;

    // Vistas integradas del Módulo 8 en CardLayout
    private VistaTablerosMandoPanel vistaTablerosMando;
    private VistaReportesClinicosPanel vistaReportesClinicos;
    private VistaReportesFinancierosPanel vistaReportesFinancieros;
    private VistaExportadorDatosPanel vistaExportadorDatos;

    // Vistas integradas del Módulo 9 en CardLayout
    private VistaCentroNotificacionesPanel vistaCentroNotificaciones;
    private VistaConfiguracionCanalesPanel vistaConfiguracionCanales;
    private VistaRepositorioDocumentalPanel vistaRepositorioDocumental;
    private VistaLogsTrazabilidadPanel vistaLogsTrazabilidad;

    // Vistas integradas del Módulo 10 en CardLayout
    private VistaDashboardAdministracionPanel vistaDashboardAdmin;
    private VistaParametrosGeneralesPanel vistaParametrosGenerales;
    private VistaUsuariosRolesPanel vistaUsuariosRoles;
    private VistaIntegracionesExternasPanel vistaIntegracionesExternas;
    private VistaModuloIASoportePanel vistaModuloIASoporte;

    private JScrollPane panelDashboard;
    private JButton btnDashboard;

    // Estado del menú lateral
    private final List<ModuloMenuUI> listaModulosUI = new ArrayList<>();
    private JButton botonActivo = null;
    private JLabel lblTituloVista;
    private JLabel lblSubtituloVista;
    private JTable tablaPacientes;
    private DefaultTableModel modeloTabla;
    private JLabel lblContadorFiltro;

    public PantallaPrincipalFrame() {
        setTitle("Happy Pets - Panel de Gestión Veterinaria");
        setIconImage(Ui.icono());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1380, 880);
        setMinimumSize(new Dimension(1180, 740));
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(new BorderLayout());

        // 1. Menú Lateral Izquierdo (Sidebar moderno)
        add(crearBarraLateralIzquierda(), BorderLayout.WEST);

        // 2. Panel Principal (Top Bar + Contenedor de Vistas)
        JPanel panelCentro = new JPanel(new BorderLayout());
        panelCentro.setBackground(COLOR_FONDO_APP);

        // Barra superior con búsqueda y usuario
        panelCentro.add(crearBarraTopHeader(), BorderLayout.NORTH);

        // Contenedor dinámico de vistas en CardLayout
        panelCentro.add(crearContenedorCards(), BorderLayout.CENTER);

        add(panelCentro, BorderLayout.CENTER);

        // Iniciar en la vista de Clientes y Mascotas vinculadas del Módulo 1
        mostrarVista("MODULO1_CLIENTES");
    }

    /**
     * Construye la barra lateral izquierda (Sidebar) con logo, dashboard, 10 módulos con submenús y logout.
     */
    private JPanel crearBarraLateralIzquierda() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(270, 0));
        sidebar.setBackground(COLOR_SIDEBAR);
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, COLOR_BORDE));

        // Cabecera del Sidebar con Logotipo institucional
        JPanel headerLogo = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 16));
        headerLogo.setBackground(COLOR_SIDEBAR);
        headerLogo.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_BORDE));

        JLabel logoBadge = new JLabel("HP", SwingConstants.CENTER) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(COLOR_AZUL_PRIMARIO);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        logoBadge.setPreferredSize(new Dimension(38, 38));
        logoBadge.setForeground(Color.WHITE);
        logoBadge.setFont(new Font("Segoe UI", Font.BOLD, 17));
        headerLogo.add(logoBadge);

        JPanel titulosLogo = new JPanel();
        titulosLogo.setOpaque(false);
        titulosLogo.setLayout(new BoxLayout(titulosLogo, BoxLayout.Y_AXIS));

        JLabel lblHappy = new JLabel("Happy Pets");
        lblHappy.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblHappy.setForeground(new Color(15, 23, 42));

        JLabel lblVet = new JLabel("CLÍNICA VETERINARIA");
        lblVet.setFont(new Font("Segoe UI", Font.BOLD, 9));
        lblVet.setForeground(new Color(148, 163, 184));

        titulosLogo.add(lblHappy);
        titulosLogo.add(lblVet);
        headerLogo.add(titulosLogo);

        sidebar.add(headerLogo, BorderLayout.NORTH);

        // Contenedor scrolleable de los módulos y submenús
        JPanel menuLista = new JPanel();
        menuLista.setBackground(COLOR_SIDEBAR);
        menuLista.setLayout(new BoxLayout(menuLista, BoxLayout.Y_AXIS));
        menuLista.setBorder(new EmptyBorder(12, 12, 12, 12));

        // 1. Botón Dashboard
        btnDashboard = crearBotonNivel1("Dashboard", Iconos.crearIconoDashboard(18, new Color(71, 85, 105)), false, null);
        btnDashboard.addActionListener(e -> {
            seleccionarBoton(btnDashboard);
            mostrarVista("DASHBOARD");
            actualizarVistaPrincipal("Panel Principal (Dashboard)", "Vista general de pacientes, indicadores y estado clínico del sistema");
        });
        menuLista.add(btnDashboard);
        menuLista.add(Box.createVerticalStrut(6));

        // Definición de los 10 módulos oficiales con sus submódulos
        List<DefinicionModulo> modulos = obtenerDefinicionesModulos();

        for (int i = 0; i < modulos.size(); i++) {
            DefinicionModulo m = modulos.get(i);
            boolean esModulo1 = (m.numero == 1);
            ModuloMenuUI mui = new ModuloMenuUI(m, esModulo1);
            listaModulosUI.add(mui);

            menuLista.add(mui.panelContenedor);
            menuLista.add(Box.createVerticalStrut(4));

            // Si es el módulo 1, seleccionar su primer submódulo por defecto
            if (esModulo1 && mui.botonesSubmodulos.size() > 0) {
                JButton primerSub = mui.botonesSubmodulos.get(0);
                seleccionarBoton(primerSub);
            }
        }

        JScrollPane scrollMenu = new JScrollPane(menuLista);
        scrollMenu.setBorder(null);
        scrollMenu.setBackground(COLOR_SIDEBAR);
        scrollMenu.getViewport().setBackground(COLOR_SIDEBAR);
        scrollMenu.getVerticalScrollBar().setUnitIncrement(16);
        sidebar.add(scrollMenu, BorderLayout.CENTER);

        // Pie del sidebar con opción Cerrar Sesión (como en la captura)
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(COLOR_SIDEBAR);
        footer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, COLOR_BORDE),
                new EmptyBorder(12, 16, 14, 16)
        ));

        JButton btnLogout = new JButton(" Cerrar Sesión") {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                if (getModel().isRollover()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(254, 242, 242));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                    g2.dispose();
                }
                super.paintComponent(g);
            }
        };
        btnLogout.setIcon(Iconos.crearIconoSalir(16, new Color(220, 38, 38)));
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnLogout.setForeground(new Color(220, 38, 38));
        btnLogout.setFocusPainted(false);
        btnLogout.setContentAreaFilled(false);
        btnLogout.setOpaque(false);
        btnLogout.setHorizontalAlignment(SwingConstants.LEFT);
        btnLogout.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnLogout.setBorder(new EmptyBorder(8, 8, 8, 8));

        btnLogout.addActionListener(e -> {
            int r = JOptionPane.showConfirmDialog(this, "¿Está seguro de que desea cerrar la sesión actual?", "Cerrar Sesión", JOptionPane.YES_NO_OPTION);
            if (r == JOptionPane.YES_OPTION) {
                auth.cerrarSesion();
                dispose();
                SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
            }
        });

        footer.add(btnLogout, BorderLayout.CENTER);
        sidebar.add(footer, BorderLayout.SOUTH);

        return sidebar;
    }

    /**
     * Barra superior (Top Bar) con buscador, botón toggle, campana de notificaciones y bloque de usuario.
     */
    private JPanel crearBarraTopHeader() {
        JPanel top = new JPanel(new BorderLayout());
        top.setPreferredSize(new Dimension(0, 64));
        top.setBackground(Color.WHITE);
        top.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_BORDE));
        top.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_BORDE),
                new EmptyBorder(10, 20, 10, 24)
        ));

        // Izquierda: Solo Buscador redondeado (sin botón hamburguesa)
        JPanel izq = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 2));
        izq.setOpaque(false);

        // Barra de búsqueda redondeada con icono
        JPanel searchBox = new JPanel(new BorderLayout(8, 0)) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(241, 245, 249));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                g2.setColor(new Color(226, 232, 240));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 18, 18);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        searchBox.setOpaque(false);
        searchBox.setPreferredSize(new Dimension(320, 36));
        searchBox.setBorder(new EmptyBorder(4, 12, 4, 12));

        JLabel icoBuscar = new JLabel(Iconos.crearIconoBuscar(16, new Color(148, 163, 184)));
        searchBox.add(icoBuscar, BorderLayout.WEST);

        JTextField txtBuscar = new JTextField();
        txtBuscar.setBorder(null);
        txtBuscar.setOpaque(false);
        txtBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtBuscar.setForeground(new Color(51, 65, 85));
        txtBuscar.setText("Buscar cliente, paciente, historia...");

        txtBuscar.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if ("Buscar cliente, paciente, historia...".equals(txtBuscar.getText())) {
                    txtBuscar.setText("");
                }
            }
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (txtBuscar.getText().trim().isEmpty()) {
                    txtBuscar.setText("Buscar cliente, paciente, historia...");
                }
            }
        });

        txtBuscar.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                filtrarTabla(txtBuscar.getText());
            }
        });

        searchBox.add(txtBuscar, BorderLayout.CENTER);
        izq.add(searchBox);

        top.add(izq, BorderLayout.WEST);

        // Derecha: Notificaciones + Perfil de Usuario con avatar circular (igual a la captura)
        JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 2));
        der.setOpaque(false);

        // Campana de notificaciones
        JButton btnNotif = new JButton(Iconos.crearIconoCampana(18, new Color(100, 116, 139)));
        btnNotif.setPreferredSize(new Dimension(36, 36));
        btnNotif.setFocusPainted(false);
        btnNotif.setContentAreaFilled(false);
        btnNotif.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnNotif.setBorder(null);
        btnNotif.setToolTipText("Notificaciones del sistema");
        der.add(btnNotif);

        // Separador vertical sutil
        JLabel sep = new JLabel("│");
        sep.setForeground(new Color(226, 232, 240));
        der.add(sep);

        // Bloque de Usuario (Harry Martin Arroyo Preciado - Administrador)
        Usuario user = auth.getSesionActual();
        String nombreMostrar = "Harry Martin Arroyo Preciado"; // Representando al usuario en sesión según captura
        String rolMostrar = user != null ? user.getRol() : "Administrador";

        JPanel userText = new JPanel();
        userText.setOpaque(false);
        userText.setLayout(new BoxLayout(userText, BoxLayout.Y_AXIS));

        JLabel lblNombreUser = new JLabel(nombreMostrar);
        lblNombreUser.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblNombreUser.setForeground(new Color(30, 41, 59));
        lblNombreUser.setHorizontalAlignment(SwingConstants.RIGHT);

        JLabel lblRolUser = new JLabel(rolMostrar);
        lblRolUser.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblRolUser.setForeground(COLOR_TEXTO_MUTED);
        lblRolUser.setHorizontalAlignment(SwingConstants.RIGHT);

        userText.add(lblNombreUser);
        userText.add(lblRolUser);
        der.add(userText);

        // Avatar circular con iniciales "HM"
        JLabel avatar = new JLabel("HM", SwingConstants.CENTER) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(COLOR_AZUL_PRIMARIO);
                g2.fillOval(0, 0, getWidth(), getHeight());
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        avatar.setPreferredSize(new Dimension(38, 38));
        avatar.setForeground(Color.WHITE);
        avatar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        der.add(avatar);

        top.add(der, BorderLayout.EAST);
        return top;
    }

    /**
     * Contenedor central que gestiona las vistas del sistema mediante CardLayout.
     */
    private JPanel crearContenedorCards() {
        panelContenedorCards.setBackground(COLOR_FONDO_APP);

        panelDashboard = crearContenedorDashboard();
        vistaClientesMascotas = new VistaClientesMascotasPanel();
        vistaHistorialClinico = new VistaHistorialClinicoPanel();
        vistaConstanciasCertificados = new VistaConstanciasCertificadosPanel();

        // Enlazar navegación integrada entre las vistas del Módulo 1
        vistaClientesMascotas.setNavegacionListener(new VistaClientesMascotasPanel.NavegacionListener() {
            @Override
            public void irAHistorialClinico(Mascota mascota) {
                vistaHistorialClinico.cargarMascota(mascota);
                mostrarVista("MODULO1_HISTORIAL");
                activarBotonSubmodulo(1, 1);
                actualizarVistaPrincipal(
                        "Historial Clínico de Mascotas · " + mascota.getNombre(),
                        "Módulo 1.2 · Consulta cronológica de visitas veterinarias y evolución clínica"
                );
            }

            @Override
            public void irAConstanciasCertificados(Mascota mascota) {
                vistaConstanciasCertificados.cargarMascota(mascota);
                mostrarVista("MODULO1_CONSTANCIAS");
                activarBotonSubmodulo(1, 2);
                actualizarVistaPrincipal(
                        "Constancias y Certificados Médicos · " + mascota.getNombre(),
                        "Módulo 1.3 · Emisión, consulta y descarga de certificados sanitarios oficiales"
                );
            }
        });

        // Vistas del Módulo 2
        vistaAgendamiento = new VistaAgendamientoCitasPanel();
        vistaCalendario = new VistaCalendarioGlobalPanel();
        vistaRecordatorios = new VistaRecordatoriosPanel();
        vistaSalaEspera = new VistaSalaEsperaTriajePanel();

        vistaAgendamiento.setNavegacionListener(new VistaAgendamientoCitasPanel.AccionNavegacionAgendaListener() {
            @Override
            public void irASalaEspera(Cita cita) {
                vistaSalaEspera.registrarLlegadaDesdeCita(cita);
                mostrarVista("MODULO2_TRIAJE");
                activarBotonSubmodulo(2, 3);
                actualizarVistaPrincipal(
                        "Sala de Espera y Triaje · " + cita.getNombreMascota(),
                        "Módulo 2.4 · Monitor en vivo de turnos, signos vitales y escala de triaje de urgencias"
                );
            }

            @Override
            public void irACalendario(LocalDate fecha) {
                mostrarVista("MODULO2_CALENDARIO");
                activarBotonSubmodulo(2, 1);
                actualizarVistaPrincipal(
                        "Calendario Global de Recursos",
                        "Módulo 2.2 · Visualización de disponibilidad de consultorios, quirófano y grooming"
                );
            }

            @Override
            public void irARecordatorios(Cita cita) {
                vistaRecordatorios.prepararParaCita(cita);
                mostrarVista("MODULO2_RECORDATORIOS");
                activarBotonSubmodulo(2, 2);
                actualizarVistaPrincipal(
                        "Gestión de Recordatorios · " + cita.getNombreMascota(),
                        "Módulo 2.3 · Notificaciones multicanal automatizadas por WhatsApp, SMS y correo electrónico"
                );
            }
        });

        panelContenedorCards.add(panelDashboard, "DASHBOARD");
        panelContenedorCards.add(vistaClientesMascotas, "MODULO1_CLIENTES");
        panelContenedorCards.add(vistaHistorialClinico, "MODULO1_HISTORIAL");
        panelContenedorCards.add(vistaConstanciasCertificados, "MODULO1_CONSTANCIAS");

        panelContenedorCards.add(vistaAgendamiento, "MODULO2_AGENDAMIENTO");
        panelContenedorCards.add(vistaCalendario, "MODULO2_CALENDARIO");
        panelContenedorCards.add(vistaRecordatorios, "MODULO2_RECORDATORIOS");
        panelContenedorCards.add(vistaSalaEspera, "MODULO2_TRIAJE");

        // Vistas del Módulo 3
        vistaConsultasMedicas = new VistaConsultasMedicasPanel();
        vistaVacunacion = new VistaVacunacionDesparasitacionPanel();
        vistaCirugias = new VistaCirugiasQuirofanoPanel();
        vistaLaboratorio = new VistaLaboratorioImagenesPanel();

        panelContenedorCards.add(vistaConsultasMedicas, "MODULO3_CONSULTAS");
        panelContenedorCards.add(vistaVacunacion, "MODULO3_VACUNACION");
        panelContenedorCards.add(vistaCirugias, "MODULO3_CIRUGIAS");
        panelContenedorCards.add(vistaLaboratorio, "MODULO3_LABORATORIO");

        // Vistas del Módulo 4
        vistaGrooming = new VistaGroomingPeluqueriaPanel();
        vistaHospitalizacion = new VistaHospitalizacionPanel();
        vistaHotel = new VistaHotelGuarderiaPanel();
        vistaAdopciones = new VistaAdopcionesPanel();

        panelContenedorCards.add(vistaGrooming, "MODULO4_GROOMING");
        panelContenedorCards.add(vistaHospitalizacion, "MODULO4_HOSPITALIZACION");
        panelContenedorCards.add(vistaHotel, "MODULO4_HOTEL");
        panelContenedorCards.add(vistaAdopciones, "MODULO4_ADOPCIONES");

        // Vistas del Módulo 5
        vistaCatalogo = new VistaCatalogoProductosPanel();
        vistaStockLotes = new VistaControlStockLotesPanel();
        vistaProveedoresOrdenes = new VistaProveedoresOrdenesPanel();
        vistaAjustesMermas = new VistaAjustesMermasPanel();

        panelContenedorCards.add(vistaCatalogo, "MODULO5_CATALOGO");
        panelContenedorCards.add(vistaStockLotes, "MODULO5_STOCK_LOTES");
        panelContenedorCards.add(vistaProveedoresOrdenes, "MODULO5_PROVEEDORES");
        panelContenedorCards.add(vistaAjustesMermas, "MODULO5_AJUSTES");

        // Vistas del Módulo 6: Finanzas y Ventas
        vistaPOS = new VistaPuntoVentaPOSPanel();
        vistaCuentas = new VistaCuentasCobrarPagarPanel();
        vistaCajaChica = new VistaControlCajaChicaPanel();
        vistaEgresos = new VistaEgresosOperativosPanel();

        panelContenedorCards.add(vistaPOS, "MODULO6_POS");
        panelContenedorCards.add(vistaCuentas, "MODULO6_CUENTAS");
        panelContenedorCards.add(vistaCajaChica, "MODULO6_CAJA_CHICA");
        panelContenedorCards.add(vistaEgresos, "MODULO6_EGRESOS");

        // Vistas del Módulo 7: Personal y Recursos Humanos
        vistaVeterinarios = new VistaVeterinariosPanel();
        vistaPersonalApoyo = new VistaPersonalApoyoPanel();
        vistaHorariosTurnos = new VistaHorariosTurnosPanel();
        vistaAsistenciasPermisos = new VistaAsistenciasPermisosPanel();

        panelContenedorCards.add(vistaVeterinarios, "MODULO7_VETERINARIOS");
        panelContenedorCards.add(vistaPersonalApoyo, "MODULO7_APOYO");
        panelContenedorCards.add(vistaHorariosTurnos, "MODULO7_TURNOS");
        panelContenedorCards.add(vistaAsistenciasPermisos, "MODULO7_ASISTENCIAS");

        // Vistas del Módulo 8: Inteligencia de Negocios y Reportes
        vistaTablerosMando = new VistaTablerosMandoPanel();
        vistaReportesClinicos = new VistaReportesClinicosPanel();
        vistaReportesFinancieros = new VistaReportesFinancierosPanel(() -> {
            mostrarVista("MODULO8_EXPORTADOR");
            activarBotonSubmodulo(8, 3);
            actualizarVistaPrincipal("Exportador de Datos", "Módulo 8.4 · Extracción masiva y modular de conjuntos de datos clínicos, financieros e inventario");
        });
        vistaExportadorDatos = new VistaExportadorDatosPanel();

        panelContenedorCards.add(vistaTablerosMando, "MODULO8_DASHBOARD");
        panelContenedorCards.add(vistaReportesClinicos, "MODULO8_CLINICOS");
        panelContenedorCards.add(vistaReportesFinancieros, "MODULO8_FINANCIEROS");
        panelContenedorCards.add(vistaExportadorDatos, "MODULO8_EXPORTADOR");

        // Vistas del Módulo 9: Notificaciones, Documentos y Auditoría
        vistaCentroNotificaciones = new VistaCentroNotificacionesPanel();
        vistaConfiguracionCanales = new VistaConfiguracionCanalesPanel();
        vistaRepositorioDocumental = new VistaRepositorioDocumentalPanel();
        vistaLogsTrazabilidad = new VistaLogsTrazabilidadPanel();

        panelContenedorCards.add(vistaCentroNotificaciones, "MODULO9_NOTIFICACIONES");
        panelContenedorCards.add(vistaConfiguracionCanales, "MODULO9_CANALES");
        panelContenedorCards.add(vistaRepositorioDocumental, "MODULO9_DOCUMENTOS");
        panelContenedorCards.add(vistaLogsTrazabilidad, "MODULO9_LOGS");

        // Vistas del Módulo 10: Configuración, Integraciones y Soporte
        vistaDashboardAdmin = new VistaDashboardAdministracionPanel();
        vistaParametrosGenerales = new VistaParametrosGeneralesPanel();
        vistaUsuariosRoles = new VistaUsuariosRolesPanel();
        vistaIntegracionesExternas = new VistaIntegracionesExternasPanel();
        vistaModuloIASoporte = new VistaModuloIASoportePanel();

        panelContenedorCards.add(vistaDashboardAdmin, "MODULO10_DASHBOARD");
        panelContenedorCards.add(vistaParametrosGenerales, "MODULO10_PARAMETROS");
        panelContenedorCards.add(vistaUsuariosRoles, "MODULO10_USUARIOS");
        panelContenedorCards.add(vistaIntegracionesExternas, "MODULO10_INTEGRACIONES");
        panelContenedorCards.add(vistaModuloIASoporte, "MODULO10_IA_SOPORTE");

        return panelContenedorCards;
    }

    /**
     * Construye el contenido del Dashboard (Título, KPIs, Filtros y Tabla).
     */
    private JScrollPane crearContenedorDashboard() {
        JPanel contenido = new JPanel();
        contenido.setBackground(COLOR_FONDO_APP);
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setBorder(new EmptyBorder(22, 28, 26, 28));

        // 1. Título de página con subtítulo y botón de acción (como en la captura)
        contenido.add(crearCabeceraContenido());
        contenido.add(Box.createVerticalStrut(20));

        // 2. Fila de 4 Tarjetas KPI con diseño idéntico a la captura
        contenido.add(crearFilaKpiWeb());
        contenido.add(Box.createVerticalStrut(20));

        // 3. Barra de filtros con combobox y botón refresh
        contenido.add(crearBarraFiltros());
        contenido.add(Box.createVerticalStrut(18));

        // 4. Directorio de pacientes y clientes registrados
        contenido.add(crearTarjetaTablaPacientes());

        JScrollPane scroll = new JScrollPane(contenido);
        scroll.setBorder(null);
        scroll.setBackground(COLOR_FONDO_APP);
        scroll.getViewport().setBackground(COLOR_FONDO_APP);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        return scroll;
    }

    private JPanel crearCabeceraContenido() {
        JPanel cab = new JPanel(new BorderLayout());
        cab.setOpaque(false);

        JPanel izq = new JPanel();
        izq.setOpaque(false);
        izq.setLayout(new BoxLayout(izq, BoxLayout.Y_AXIS));

        lblTituloVista = new JLabel("Planilla y Directorio Clínico (Módulo 1: Pacientes y Clientes)");
        lblTituloVista.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTituloVista.setForeground(COLOR_TEXTO_TITULO);

        lblSubtituloVista = new JLabel("Gestión centralizada de propietarios, fichas de mascotas vinculadas e historiales médicos");
        lblSubtituloVista.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSubtituloVista.setForeground(COLOR_TEXTO_MUTED);

        izq.add(lblTituloVista);
        izq.add(Box.createVerticalStrut(4));
        izq.add(lblSubtituloVista);
        cab.add(izq, BorderLayout.CENTER);

        // Botón azul estilo "+ Generar Planilla por Empresa" de la captura
        JButton btnAccion = new JButton("+ Nuevo Registro / Paciente") {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? new Color(3, 105, 161) : COLOR_AZUL_PRIMARIO);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        btnAccion.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnAccion.setForeground(Color.WHITE);
        btnAccion.setFocusPainted(false);
        btnAccion.setContentAreaFilled(false);
        btnAccion.setOpaque(false);
        btnAccion.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnAccion.setBorder(new EmptyBorder(10, 18, 10, 18));
        btnAccion.addActionListener(e -> {
            mostrarVista("MODULO1_CLIENTES");
            vistaClientesMascotas.nuevoCliente();
            activarBotonSubmodulo(1, 0);
            actualizarVistaPrincipal(
                    "Mantenimiento de Clientes y Mascotas",
                    "Módulo 1.1 · Registro y administración de propietarios responsables y pacientes asociados"
            );
        });

        cab.add(btnAccion, BorderLayout.EAST);
        return cab;
    }

    /**
     * Crea las 4 tarjetas KPI con la misma estética de la captura web:
     * Fondo blanco, caja de icono suave en esquina izquierda, valor grande y etiqueta debajo.
     */
    private JPanel crearFilaKpiWeb() {
        JPanel fila = new JPanel(new GridLayout(1, 4, 16, 0));
        fila.setOpaque(false);

        int totalClientes = repo.getClientes().size();
        List<Mascota> totalMascotas = repo.todasLasMascotas();
        int totalConsultas = 4;
        int totalDocs = 5;

        fila.add(crearCardKpi(
                String.valueOf(totalClientes),
                "PROPIETARIOS REGISTRADOS",
                new Color(224, 242, 254),
                COLOR_AZUL_PRIMARIO,
                Iconos.crearIconoClientes(22, COLOR_AZUL_PRIMARIO)
        ));

        fila.add(crearCardKpi(
                String.valueOf(totalMascotas.size()),
                "PACIENTES ACTIVOS",
                new Color(254, 243, 199),
                new Color(217, 119, 6),
                Iconos.crearIconoHuella(22, new Color(217, 119, 6))
        ));

        fila.add(crearCardKpi(
                String.valueOf(totalConsultas),
                "CONSULTAS Y ATENCIONES",
                new Color(220, 252, 231),
                new Color(22, 163, 74),
                Iconos.crearIconoEstetoscopio(22, new Color(22, 163, 74))
        ));

        fila.add(crearCardKpi(
                String.valueOf(totalDocs),
                "CONSTANCIAS EMITIDAS",
                new Color(243, 232, 255),
                new Color(147, 51, 234),
                Iconos.crearIconoCertificado(22, new Color(147, 51, 234))
        ));

        return fila;
    }

    private JPanel crearCardKpi(String valor, String etiqueta, Color colorFondoIco, Color colorIcono, Icon icono) {
        JPanel card = new JPanel(new BorderLayout(14, 0)) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(COLOR_BORDE);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(16, 18, 16, 18));

        // Caja de icono redondeada a la izquierda
        JLabel badgeIcono = new JLabel(icono, SwingConstants.CENTER) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(colorFondoIco);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        badgeIcono.setPreferredSize(new Dimension(46, 46));
        card.add(badgeIcono, BorderLayout.WEST);

        // Bloque de valor y texto
        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));

        JLabel lblVal = new JLabel(valor);
        lblVal.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblVal.setForeground(COLOR_TEXTO_TITULO);

        JLabel lblEtq = new JLabel(etiqueta);
        lblEtq.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblEtq.setForeground(COLOR_TEXTO_MUTED);

        text.add(lblVal);
        text.add(Box.createVerticalStrut(2));
        text.add(lblEtq);
        card.add(text, BorderLayout.CENTER);

        return card;
    }

    /**
     * Barra de filtro horizontal con combobox y botón refresh idéntico a la captura.
     */
    private JPanel crearBarraFiltros() {
        JPanel bar = new JPanel(new BorderLayout()) {
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
        bar.setOpaque(false);
        bar.setBorder(new EmptyBorder(10, 18, 10, 18));

        JPanel izq = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        izq.setOpaque(false);

        JLabel lblFiltro = new JLabel("Filtrar por Especie / Categoría:");
        lblFiltro.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblFiltro.setForeground(new Color(51, 65, 85));
        izq.add(lblFiltro);

        JComboBox<String> cmbFiltro = new JComboBox<>(new String[]{
                "Todas las Mascotas (Caninos y Felinos)",
                "Solo Caninos",
                "Solo Felinos",
                "Pacientes Activos"
        });
        cmbFiltro.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cmbFiltro.setPreferredSize(new Dimension(280, 32));
        cmbFiltro.setBackground(Color.WHITE);

        cmbFiltro.addActionListener(e -> {
            String sel = (String) cmbFiltro.getSelectedItem();
            aplicarFiltroEspecie(sel);
        });

        izq.add(cmbFiltro);
        bar.add(izq, BorderLayout.WEST);

        JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        der.setOpaque(false);

        lblContadorFiltro = new JLabel(repo.todasLasMascotas().size() + " pacientes registrados");
        lblContadorFiltro.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblContadorFiltro.setForeground(COLOR_TEXTO_MUTED);
        der.add(lblContadorFiltro);

        JButton btnRefresh = new JButton("↻");
        btnRefresh.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnRefresh.setPreferredSize(new Dimension(32, 32));
        btnRefresh.setFocusPainted(false);
        btnRefresh.setBackground(Color.WHITE);
        btnRefresh.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnRefresh.setToolTipText("Actualizar tabla");
        btnRefresh.addActionListener(e -> recargarTabla());
        der.add(btnRefresh);

        bar.add(der, BorderLayout.EAST);
        return bar;
    }

    /**
     * Tabla moderna de pacientes registrados según la base de datos y wireframes.
     */
    private JPanel crearTarjetaTablaPacientes() {
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
        card.setBorder(new EmptyBorder(1, 1, 1, 1));

        String[] cols = {"Código", "Paciente", "Especie / Raza", "Propietario Responsable", "Teléfono Contacto", "Peso Actual", "Plan Vacunal", "Estado"};
        modeloTabla = new DefaultTableModel(cols, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        tablaPacientes = new JTable(modeloTabla);
        Ui.formatearTabla(tablaPacientes);
        tablaPacientes.setRowHeight(38);

        recargarTabla();

        tablaPacientes.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = tablaPacientes.getSelectedRow();
                    if (row >= 0) {
                        String cod = (String) modeloTabla.getValueAt(row, 0);
                        Optional<Mascota> optM = repo.todasLasMascotas().stream().filter(m -> m.getCodigo().equals(cod)).findFirst();
                        if (optM.isPresent()) {
                            vistaHistorialClinico.cargarMascota(optM.get());
                            mostrarVista("MODULO1_HISTORIAL");
                            activarBotonSubmodulo(1, 1);
                            actualizarVistaPrincipal(
                                    "Historial Clínico · " + optM.get().getNombre(),
                                    "Módulo 1.2 · Consulta cronológica de visitas veterinarias y diagnósticos"
                            );
                        }
                    }
                }
            }
        });

        JScrollPane sp = new JScrollPane(tablaPacientes);
        sp.setBorder(null);
        sp.setPreferredSize(new Dimension(1200, 240));
        card.add(sp, BorderLayout.CENTER);

        return card;
    }

    private void recargarTabla() {
        modeloTabla.setRowCount(0);
        List<Mascota> mascotas = repo.todasLasMascotas();
        for (Mascota m : mascotas) {
            Optional<Cliente> c = repo.getClienteDeMascota(m.getCodigo());
            modeloTabla.addRow(new Object[]{
                    m.getCodigo(),
                    m.getNombre(),
                    m.getEspecie() + " · " + m.getRaza(),
                    c.map(Cliente::getNombreCompleto).orElse("-"),
                    c.map(Cliente::getTelefonoPrincipal).orElse("-"),
                    m.getPesoActualKg() + " kg",
                    m.getPlanVacunal(),
                    m.getEstadoTexto()
            });
        }
        if (lblContadorFiltro != null) {
            lblContadorFiltro.setText(mascotas.size() + " pacientes registrados");
        }
    }

    private void aplicarFiltroEspecie(String filtro) {
        modeloTabla.setRowCount(0);
        List<Mascota> mascotas = repo.todasLasMascotas();
        int count = 0;
        for (Mascota m : mascotas) {
            boolean coincide = true;
            if (filtro.contains("Caninos") && !"Canino".equalsIgnoreCase(m.getEspecie())) coincide = false;
            if (filtro.contains("Felinos") && !"Felino".equalsIgnoreCase(m.getEspecie())) coincide = false;
            if (filtro.contains("Activos") && !m.isActivo()) coincide = false;

            if (coincide) {
                count++;
                Optional<Cliente> c = repo.getClienteDeMascota(m.getCodigo());
                modeloTabla.addRow(new Object[]{
                        m.getCodigo(),
                        m.getNombre(),
                        m.getEspecie() + " · " + m.getRaza(),
                        c.map(Cliente::getNombreCompleto).orElse("-"),
                        c.map(Cliente::getTelefonoPrincipal).orElse("-"),
                        m.getPesoActualKg() + " kg",
                        m.getPlanVacunal(),
                        m.getEstadoTexto()
                });
            }
        }
        lblContadorFiltro.setText(count + " pacientes encontrados");
    }

    private void filtrarTabla(String query) {
        if (query == null || query.trim().isEmpty() || "Buscar cliente, paciente, historia...".equals(query)) {
            recargarTabla();
            return;
        }
        String q = query.trim().toLowerCase();
        modeloTabla.setRowCount(0);
        int count = 0;
        for (Mascota m : repo.todasLasMascotas()) {
            Optional<Cliente> c = repo.getClienteDeMascota(m.getCodigo());
            String nombreCli = c.map(Cliente::getNombreCompleto).orElse("").toLowerCase();
            if (m.getNombre().toLowerCase().contains(q) ||
                    m.getCodigo().toLowerCase().contains(q) ||
                    m.getRaza().toLowerCase().contains(q) ||
                    nombreCli.contains(q)) {
                count++;
                modeloTabla.addRow(new Object[]{
                        m.getCodigo(),
                        m.getNombre(),
                        m.getEspecie() + " · " + m.getRaza(),
                        c.map(Cliente::getNombreCompleto).orElse("-"),
                        c.map(Cliente::getTelefonoPrincipal).orElse("-"),
                        m.getPesoActualKg() + " kg",
                        m.getPlanVacunal(),
                        m.getEstadoTexto()
                });
            }
        }
        lblContadorFiltro.setText(count + " coincidencias encontradas");
    }

    private void actualizarVistaPrincipal(String titulo, String subtitulo) {
        if (lblTituloVista != null) lblTituloVista.setText(titulo);
        if (lblSubtituloVista != null) lblSubtituloVista.setText(subtitulo);
    }

    public void mostrarVista(String nombreCard) {
        layoutCards.show(panelContenedorCards, nombreCard);
        panelContenedorCards.revalidate();
        panelContenedorCards.repaint();
    }

    public void activarBotonSubmodulo(int numModulo, int subIdx) {
        for (ModuloMenuUI mui : listaModulosUI) {
            if (mui.def.numero == numModulo) {
                if (!mui.expandido) {
                    mui.toggleExpansion();
                }
                if (subIdx >= 0 && subIdx < mui.botonesSubmodulos.size()) {
                    seleccionarBoton(mui.botonesSubmodulos.get(subIdx));
                }
                break;
            }
        }
    }

    private void seleccionarBoton(JButton btn) {
        if (botonActivo != null) {
            botonActivo.putClientProperty("activo", false);
            botonActivo.repaint();
        }
        botonActivo = btn;
        if (botonActivo != null) {
            botonActivo.putClientProperty("activo", true);
            botonActivo.repaint();
        }
    }

    /**
     * Componente UI para representar un Módulo en el Menú lateral con cabecera colapsable y submódulos.
     */
    private class ModuloMenuUI {
        final DefinicionModulo def;
        final JPanel panelContenedor;
        final JPanel panelSubmodulos;
        final JButton btnCabecera;
        final JLabel lblChevron;
        final List<JButton> botonesSubmodulos = new ArrayList<>();
        boolean expandido;

        ModuloMenuUI(DefinicionModulo def, boolean expandirInicialmente) {
            this.def = def;
            this.expandido = expandirInicialmente;

            panelContenedor = new JPanel();
            panelContenedor.setOpaque(false);
            panelContenedor.setLayout(new BoxLayout(panelContenedor, BoxLayout.Y_AXIS));

            // Cabecera del módulo con icono, título y flecha chevron
            lblChevron = new JLabel(Iconos.crearIconoChevron(14, new Color(148, 163, 184), expandido));

            btnCabecera = new JButton() {
                private static final long serialVersionUID = 1L;
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    if (expandido) {
                        g2.setColor(new Color(241, 245, 249));
                        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                    } else if (getModel().isRollover()) {
                        g2.setColor(new Color(248, 250, 252));
                        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                    }
                    super.paintComponent(g2);
                    g2.dispose();
                }
            };
            btnCabecera.setLayout(new BorderLayout(8, 0));
            btnCabecera.setOpaque(false);
            btnCabecera.setContentAreaFilled(false);
            btnCabecera.setFocusPainted(false);
            btnCabecera.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btnCabecera.setBorder(new EmptyBorder(8, 10, 8, 10));
            btnCabecera.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

            // Icono + Nombre del módulo a la izquierda
            JPanel izq = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
            izq.setOpaque(false);
            izq.add(new JLabel(def.icono));

            JLabel lblTitulo = new JLabel(def.nombre);
            lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 12));
            lblTitulo.setForeground(new Color(51, 65, 85));
            izq.add(lblTitulo);

            btnCabecera.add(izq, BorderLayout.CENTER);
            btnCabecera.add(lblChevron, BorderLayout.EAST);

            // Panel que contiene los submódulos indentados
            panelSubmodulos = new JPanel();
            panelSubmodulos.setOpaque(false);
            panelSubmodulos.setLayout(new BoxLayout(panelSubmodulos, BoxLayout.Y_AXIS));
            panelSubmodulos.setBorder(new EmptyBorder(2, 0, 4, 0));
            panelSubmodulos.setVisible(expandido);

            for (int i = 0; i < def.submodulos.length; i++) {
                String nomSub = def.submodulos[i];
                final int idx = i;
                JButton btnSub = crearBotonSubmodulo(nomSub, () -> {
                    def.accionesSubmodulos[idx].run();
                    actualizarVistaPrincipal(
                            def.nombre + " · " + nomSub,
                            "Responsable Asignado: " + def.responsable + "  |  Paquete: src/happypets/modulos/modulo" + def.numero + "/"
                    );
                });
                botonesSubmodulos.add(btnSub);
                panelSubmodulos.add(btnSub);
                panelSubmodulos.add(Box.createVerticalStrut(2));
            }

            btnCabecera.addActionListener(e -> toggleExpansion());

            panelContenedor.add(btnCabecera);
            panelContenedor.add(panelSubmodulos);
        }

        void toggleExpansion() {
            expandido = !expandido;
            panelSubmodulos.setVisible(expandido);
            lblChevron.setIcon(Iconos.crearIconoChevron(14, new Color(148, 163, 184), expandido));
            panelContenedor.revalidate();
            panelContenedor.repaint();
        }
    }

    private JButton crearBotonNivel1(String texto, Icon icono, boolean activo, Runnable accion) {
        JButton btn = new JButton(texto) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Boolean act = (Boolean) getClientProperty("activo");
                if (Boolean.TRUE.equals(act)) {
                    g2.setColor(COLOR_PILL_ACTIVO);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                } else if (getModel().isRollover()) {
                    g2.setColor(new Color(241, 245, 249));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                }
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        btn.setIcon(icono);
        btn.setIconTextGap(10);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(new Color(51, 65, 85));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 12, 8, 12));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btn.putClientProperty("activo", activo);

        if (accion != null) {
            btn.addActionListener(e -> accion.run());
        }
        return btn;
    }

    private JButton crearBotonSubmodulo(String texto, Runnable accion) {
        JButton btn = new JButton(texto) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Boolean act = (Boolean) getClientProperty("activo");
                if (Boolean.TRUE.equals(act)) {
                    // Píldora activa con fondo azul suave idéntica a "Planillas" en la captura
                    g2.setColor(COLOR_PILL_ACTIVO);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                    setForeground(COLOR_TEXTO_ACTIVO);
                } else if (getModel().isRollover()) {
                    g2.setColor(new Color(248, 250, 252));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                    setForeground(new Color(15, 23, 42));
                } else {
                    setForeground(new Color(100, 116, 139));
                }
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        // Indentación a la izquierda (34px)
        btn.setBorder(new EmptyBorder(7, 34, 7, 10));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        btn.putClientProperty("activo", false);

        btn.addActionListener(e -> {
            seleccionarBoton(btn);
            if (accion != null) accion.run();
        });
        return btn;
    }

    private static class DefinicionModulo {
        final int numero;
        final String nombre;
        final String responsable;
        final String[] submodulos;
        final Icon icono;
        final Runnable[] accionesSubmodulos;

        DefinicionModulo(int numero, String nombre, String responsable, String[] submodulos, Icon icono, Runnable[] accionesSubmodulos) {
            this.numero = numero;
            this.nombre = nombre;
            this.responsable = responsable;
            this.submodulos = submodulos;
            this.icono = icono;
            this.accionesSubmodulos = accionesSubmodulos;
        }
    }

    /**
     * Mapeo oficial de los 10 módulos con sus submódulos y compañeros responsables.
     */
    private List<DefinicionModulo> obtenerDefinicionesModulos() {
        return List.of(
                new DefinicionModulo(
                        1, "Pacientes y Clientes", "Linares Vielma, Rafael Alejandro",
                        new String[]{"Propietarios y Mascotas", "Historial Clínico", "Constancias y Certificados"},
                        Iconos.crearIconoHuella(16, COLOR_AZUL_PRIMARIO),
                        new Runnable[]{
                                () -> {
                                    mostrarVista("MODULO1_CLIENTES");
                                    actualizarVistaPrincipal("Mantenimiento de Clientes y Mascotas", "Módulo 1.1 · Registro y administración de propietarios responsables y pacientes asociados");
                                },
                                () -> {
                                    mostrarVista("MODULO1_HISTORIAL");
                                    actualizarVistaPrincipal("Historial Clínico de Mascotas", "Módulo 1.2 · Consulta cronológica de visitas veterinarias y diagnósticos");
                                },
                                () -> {
                                    mostrarVista("MODULO1_CONSTANCIAS");
                                    actualizarVistaPrincipal("Constancias y Certificados Médicos", "Módulo 1.3 · Emisión, consulta y descarga de certificados oficiales");
                                }
                        }
                ),
                new DefinicionModulo(
                        2, "Agenda y Citas", "Ayala Ornay, Antony Giovanny",
                        new String[]{"Agendamiento de Citas", "Calendario Global", "Gestión de Recordatorios", "Sala de Espera y Triaje"},
                        Iconos.crearIconoCalendario(16, new Color(14, 165, 233)),
                        new Runnable[]{
                                () -> {
                                    mostrarVista("MODULO2_AGENDAMIENTO");
                                    vistaAgendamiento.recargarDatos();
                                    actualizarVistaPrincipal("Agendamiento de Citas Médicas", "Módulo 2.1 · Reserva, reprogramación y control de citas clínicas y servicios");
                                },
                                () -> {
                                    mostrarVista("MODULO2_CALENDARIO");
                                    actualizarVistaPrincipal("Calendario Global de Recursos", "Módulo 2.2 · Visualización de disponibilidad de consultorios, quirófano y grooming");
                                },
                                () -> {
                                    mostrarVista("MODULO2_RECORDATORIOS");
                                    vistaRecordatorios.recargarDatos();
                                    actualizarVistaPrincipal("Gestión de Recordatorios Multicanal", "Módulo 2.3 · Notificaciones automatizadas por WhatsApp, SMS y correo electrónico");
                                },
                                () -> {
                                    mostrarVista("MODULO2_TRIAJE");
                                    vistaSalaEspera.recargarDatos();
                                    actualizarVistaPrincipal("Sala de Espera y Triaje Clínico", "Módulo 2.4 · Monitor de turnos, signos vitales y escala de triaje de urgencias");
                                }
                        }
                ),
                new DefinicionModulo(
                        3, "Servicios Médicos", "Martínez Gutiérrez, Gustavo Javier",
                        new String[]{"Consultas Médicas", "Vacunación y Desparasitación", "Cirugías y Quirófano", "Laboratorio e Imágenes"},
                        Iconos.crearIconoEstetoscopio(16, new Color(20, 184, 166)),
                        new Runnable[]{
                                () -> {
                                    mostrarVista("MODULO3_CONSULTAS");
                                    vistaConsultasMedicas.recargarDatos();
                                    actualizarVistaPrincipal("Consultas Médicas y Atenciones Veterinarias", "Módulo 3.1 · Registro clínico, exploración física, constantes, diagnóstico y prescripción");
                                },
                                () -> {
                                    mostrarVista("MODULO3_VACUNACION");
                                    vistaVacunacion.recargarDatos();
                                    actualizarVistaPrincipal("Vacunación y Desparasitación Preventiva", "Módulo 3.2 · Registro de biológicos, control de lotes, antiparasitarios y fechas de refuerzo");
                                },
                                () -> {
                                    mostrarVista("MODULO3_CIRUGIAS");
                                    vistaCirugias.recargarDatos();
                                    actualizarVistaPrincipal("Cirugías y Control de Quirófano", "Módulo 3.3 · Programación quirúrgica, protocolo intraoperatorio y seguimiento postoperatorio");
                                },
                                () -> {
                                    mostrarVista("MODULO3_LABORATORIO");
                                    vistaLaboratorio.recargarDatos();
                                    actualizarVistaPrincipal("Laboratorio Clínico e Imágenes Diagnósticas", "Módulo 3.4 · Órdenes diagnósticas, procesamiento de muestras, informes radiológicos y ecografías");
                                }
                        }
                ),
                new DefinicionModulo(
                        4, "Estética y Hospedaje", "Quimi Valderrama, Francisco Raul",
                        new String[]{"Grooming y Peluquería", "Hospitalización", "Hotel / Guardería", "Adopciones"},
                        Iconos.crearIconoHuella(16, new Color(244, 63, 94)),
                        new Runnable[]{
                                () -> {
                                    mostrarVista("MODULO4_GROOMING");
                                    vistaGrooming.recargarDatos();
                                    actualizarVistaPrincipal("Grooming y Peluquería Canina / Felina", "Módulo 4.1 · Spa estético, baños medicados, corte de raza, checklist dermatológico y entrega");
                                },
                                () -> {
                                    mostrarVista("MODULO4_HOSPITALIZACION");
                                    vistaHospitalizacion.recargarDatos();
                                    actualizarVistaPrincipal("Hospitalización y Cuidados Intensivos", "Módulo 4.2 · Monitor de boxes clínicos, alertas de gravedad, fluidoterapia, evolución y alta");
                                },
                                () -> {
                                    mostrarVista("MODULO4_HOTEL");
                                    vistaHotel.recargarDatos();
                                    actualizarVistaPrincipal("Hotel & Guardería Canina / Felina", "Módulo 4.3 · Suites climatizadas, estancias prolongadas, nutrición personalizada, paseos y check-out");
                                },
                                () -> {
                                    mostrarVista("MODULO4_ADOPCIONES");
                                    vistaAdopciones.recargarDatos();
                                    actualizarVistaPrincipal("Adopciones y Rescates Responsables", "Módulo 4.4 · Catálogo de rescatados, perfil sanitario, evaluación de postulantes y actas de compromiso");
                                }
                        }
                ),
                new DefinicionModulo(
                        5, "Inventario y Farmacia", "Castro Pairazaman, Craig Kem",
                        new String[]{"Catálogo de Productos y Fármacos", "Control de Stock y Lotes", "Proveedores y Órdenes de Compra", "Ajustes y Mermas"},
                        Iconos.crearIconoPildora(16, new Color(34, 197, 94)),
                        new Runnable[]{
                                () -> {
                                    mostrarVista("MODULO5_CATALOGO");
                                    vistaCatalogo.recargarDatos();
                                    actualizarVistaPrincipal("Catálogo de Productos y Fármacos", "Módulo 5.1 · Registro maestro de medicamentos, biológicos, alimentos clínicos, insumos y control de precios");
                                },
                                () -> {
                                    mostrarVista("MODULO5_STOCK_LOTES");
                                    vistaStockLotes.recargarDatos();
                                    actualizarVistaPrincipal("Control de Stock y Lotes (Kardex)", "Módulo 5.2 · Trazabilidad de lotes clínicos, control FEFO de caducidad, movimientos y reposición");
                                },
                                () -> {
                                    mostrarVista("MODULO5_PROVEEDORES");
                                    vistaProveedoresOrdenes.recargarDatos();
                                    actualizarVistaPrincipal("Proveedores y Órdenes de Compra", "Módulo 5.3 · Homologación de laboratorios, compras valorizadas, control de créditos y recepción de stock");
                                },
                                () -> {
                                    mostrarVista("MODULO5_AJUSTES");
                                    vistaAjustesMermas.recargarDatos();
                                    actualizarVistaPrincipal("Ajustes de Inventario y Mermas", "Módulo 5.4 · Registro de bajas por caducidad, roturas, descarte biológico y regularizaciones");
                                }
                        }
                ),
                new DefinicionModulo(
                        6, "Finanzas y Ventas", "Corrales Contreras, Joanna Andrea",
                        new String[]{"Punto de Venta (POS)", "Cuentas por Cobrar y Pagar", "Control de Caja Chica", "Gestión de Egresos Operativos"},
                        Iconos.crearIconoFactura(16, new Color(249, 115, 22)),
                        new Runnable[]{
                                () -> {
                                    mostrarVista("MODULO6_POS");
                                    vistaPOS.recargarDatos();
                                    actualizarVistaPrincipal("Punto de Venta (POS)", "Módulo 6.1 · Facturación electrónica rápida, catálogo interactivo, múltiples medios de pago y emisión de comprobantes");
                                },
                                () -> {
                                    mostrarVista("MODULO6_CUENTAS");
                                    vistaCuentas.recargarDatos();
                                    actualizarVistaPrincipal("Cuentas por Cobrar y Pagar", "Módulo 6.2 · Control de créditos otorgados a clientes, amortizaciones y obligaciones con proveedores");
                                },
                                () -> {
                                    mostrarVista("MODULO6_CAJA_CHICA");
                                    vistaCajaChica.recargarDatos();
                                    actualizarVistaPrincipal("Control de Caja Chica", "Módulo 6.3 · Fondo fijo, ingresos, egresos de urgencia, saldo físico y arqueo de efectivo");
                                },
                                () -> {
                                    mostrarVista("MODULO6_EGRESOS");
                                    vistaEgresos.recargarDatos();
                                    actualizarVistaPrincipal("Gestión de Egresos Operativos", "Módulo 6.4 · Control presupuestal de alquiler, servicios básicos, planillas y mantenimiento");
                                }
                        }
                ),
                new DefinicionModulo(
                        7, "Personal y RRHH", "Loli Espinoza, Víctor Manuel",
                        new String[]{"Veterinarios", "Personal de Apoyo", "Horarios y Turnos", "Asistencias y Permisos"},
                        Iconos.crearIconoUsuario(16, new Color(168, 85, 247)),
                        new Runnable[]{
                                () -> {
                                    mostrarVista("MODULO7_VETERINARIOS");
                                    vistaVeterinarios.recargarDatos();
                                    actualizarVistaPrincipal("Nuestros Veterinarios", "Módulo 7.1 · Directorio de especialistas clínicos colegiados, especialidades y disponibilidad en tiempo real");
                                },
                                () -> {
                                    mostrarVista("MODULO7_APOYO");
                                    vistaPersonalApoyo.recargarDatos();
                                    actualizarVistaPrincipal("Personal de Apoyo", "Módulo 7.2 · Directorio de auxiliares veterinarios ATV, recepción, estética y administración");
                                },
                                () -> {
                                    mostrarVista("MODULO7_TURNOS");
                                    vistaHorariosTurnos.recargarDatos();
                                    actualizarVistaPrincipal("Horarios y Turnos Médicos", "Módulo 7.3 · Cuadrante semanal de guardias, roles médicos y asignación de salas");
                                },
                                () -> {
                                    mostrarVista("MODULO7_ASISTENCIAS");
                                    vistaAsistenciasPermisos.recargarDatos();
                                    actualizarVistaPrincipal("Asistencias y Permisos", "Módulo 7.4 · Pase de lista diario por turnos, control de retardos y solicitudes de permisos");
                                }
                        }
                ),
                new DefinicionModulo(
                        8, "Inteligencia y Reportes", "Arroyo Preciado, Harry Martin",
                        new String[]{"Tableros de Mando (Dashboards)", "Reportes Clínicos", "Reportes Financieros", "Exportador de Datos"},
                        Iconos.crearIconoReportes(16, new Color(0, 115, 125)),
                        new Runnable[]{
                                () -> {
                                    mostrarVista("MODULO8_DASHBOARD");
                                    actualizarVistaPrincipal("Tableros de Mando (Dashboards)", "Módulo 8.1 · Monitoreo ejecutivo de indicadores de rendimiento, citas e ingresos en tiempo real");
                                },
                                () -> {
                                    mostrarVista("MODULO8_CLINICOS");
                                    actualizarVistaPrincipal("Reportes Clínicos", "Módulo 8.2 · Historial integral de consultas médicas, prevalencia patológica y carga asistencial");
                                },
                                () -> {
                                    mostrarVista("MODULO8_FINANCIEROS");
                                    actualizarVistaPrincipal("Reportes Financieros", "Módulo 8.3 · Balance consolidado de ingresos, costes asistenciales y rentabilidad por centro de coste");
                                },
                                () -> {
                                    mostrarVista("MODULO8_EXPORTADOR");
                                    actualizarVistaPrincipal("Exportador de Datos", "Módulo 8.4 · Extracción masiva y modular de conjuntos de datos clínicos, financieros e inventario");
                                }
                        }
                ),
                new DefinicionModulo(
                        9, "Notificaciones y Auditoría", "Vera Aguilar, Carlos Edgardo",
                        new String[]{"Centro de Notificaciones", "Configuración de Canales", "Repositorio Documental", "Logs y Trazabilidad"},
                        Iconos.crearIconoCampana(16, new Color(100, 116, 139)),
                        new Runnable[]{
                                () -> {
                                    mostrarVista("MODULO9_NOTIFICACIONES");
                                    actualizarVistaPrincipal("Centro de Notificaciones", "Módulo 9.1 · Bandeja unificada de alertas críticas, incidentes de seguridad y avisos en vivo");
                                },
                                () -> {
                                    mostrarVista("MODULO9_CANALES");
                                    actualizarVistaPrincipal("Configuración de Canales", "Módulo 9.2 · Parametrización de pasarelas de entrega (Email, SMS y Push) y políticas de envío");
                                },
                                () -> {
                                    mostrarVista("MODULO9_DOCUMENTOS");
                                    actualizarVistaPrincipal("Repositorio Documental", "Módulo 9.3 · Almacenamiento institucional, custodia digital y expediente de contratos y facturas");
                                },
                                () -> {
                                    mostrarVista("MODULO9_LOGS");
                                    actualizarVistaPrincipal("Logs y Trazabilidad (Auditoría)", "Módulo 9.4 · Pistas de auditoría inmutables, registro de transacciones, descargas y accesos");
                                }
                        }
                ),
                new DefinicionModulo(
                        10, "Configuración y Soporte", "Minaya Bravo, Almendra Lili",
                        new String[]{"Parámetros Generales", "Usuarios, Roles y Permisos", "Integraciones externas", "Módulo de IA y Soporte Técnico"},
                        Iconos.crearIconoConfiguracion(16, new Color(217, 119, 6)),
                        new Runnable[]{
                                () -> {
                                    mostrarVista("MODULO10_PARAMETROS");
                                    actualizarVistaPrincipal("Parámetros Generales", "Módulo 10.1 · Datos institucionales, logotipo oficial, moneda principal y zonas horarias");
                                },
                                () -> {
                                    mostrarVista("MODULO10_USUARIOS");
                                    actualizarVistaPrincipal("Usuarios, Roles y Permisos", "Módulo 10.2 · Nómina activa de colaboradores, asignación de áreas y matriz de privilegios");
                                },
                                () -> {
                                    mostrarVista("MODULO10_INTEGRACIONES");
                                    actualizarVistaPrincipal("Integraciones Externas", "Módulo 10.3 · Conexión con DIAN/SUNAT, WhatsApp Cloud, pasarelas de pago y laboratorio");
                                },
                                () -> {
                                    mostrarVista("MODULO10_IA_SOPORTE");
                                    actualizarVistaPrincipal("Módulo de IA y Soporte Técnico", "Módulo 10.4 · Asistentes de triaje inteligente, consola diagnóstica y telemetría");
                                }
                        }
                )
        );
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Ui.instalarApariencia();
            new PantallaPrincipalFrame().setVisible(true);
        });
    }
}
