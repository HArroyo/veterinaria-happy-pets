package happypets.modulos.modulo10;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import happypets.ui.Iconos;
import happypets.ui.Ui;

/**
 * Ventana independiente para el Módulo 10: Configuración, Integraciones y Soporte.
 * Responsable Asignado: Minaya Bravo, Almendra Lili
 * Contiene los 4 submódulos oficiales del software según los wireframes:
 *  1. Parámetros Generales (Datos de la clínica, logos, monedas, sedes)
 *  2. Usuarios, Roles y Permisos (Colaboradores activos, control de acceso y matriz de privilegios)
 *  3. Integraciones Externas (APIs DIAN/SUNAT, WhatsApp Cloud, Wompi, IDEXX, AWS S3)
 *  4. Módulo de IA y Soporte Técnico (Triaje veterinario, diagnósticos sugeridos y telemetría)
 *  + Vista Completa (Dashboard Maestro según PÁGINA COMPLETA.pdf)
 */
public class Modulo10ConfiguracionSoporteFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    private final VistaDashboardAdministracionPanel vistaDashboard;
    private final VistaParametrosGeneralesPanel vistaParametros;
    private final VistaUsuariosRolesPanel vistaUsuarios;
    private final VistaIntegracionesExternasPanel vistaIntegraciones;
    private final VistaModuloIASoportePanel vistaIASoporte;
    private final JTabbedPane tabs;

    public Modulo10ConfiguracionSoporteFrame() {
        this(0);
    }

    public Modulo10ConfiguracionSoporteFrame(int pestanaInicial) {
        setTitle("Happy Pets - Módulo 10: Configuración, Integraciones y Soporte");
        setIconImage(Ui.icono());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1360, 880);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(248, 250, 252));

        vistaDashboard = new VistaDashboardAdministracionPanel();
        vistaParametros = new VistaParametrosGeneralesPanel();
        vistaUsuarios = new VistaUsuariosRolesPanel();
        vistaIntegraciones = new VistaIntegracionesExternasPanel();
        vistaIASoporte = new VistaModuloIASoportePanel();

        tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabs.setBackground(Color.WHITE);

        tabs.addTab("Vista Completa (Dashboard)",
                Iconos.crearIconoDashboard(16, new Color(15, 23, 42)),
                vistaDashboard);

        tabs.addTab("Parámetros Generales",
                Iconos.crearIconoConfiguracion(16, new Color(59, 130, 246)),
                vistaParametros);

        tabs.addTab("Usuarios, Roles y Permisos",
                Iconos.crearIconoUsuario(16, new Color(16, 185, 129)),
                vistaUsuarios);

        tabs.addTab("Integraciones Externas",
                Iconos.crearIconoEnchufe(16, new Color(245, 158, 11)),
                vistaIntegraciones);

        tabs.addTab("Módulo de IA y Soporte",
                Iconos.crearIconoRobot(16, new Color(139, 92, 246)),
                vistaIASoporte);

        if (pestanaInicial >= 0 && pestanaInicial < tabs.getTabCount()) {
            tabs.setSelectedIndex(pestanaInicial);
        }

        add(tabs, BorderLayout.CENTER);
    }

    public void seleccionarPestana(int idx) {
        if (idx >= 0 && idx < tabs.getTabCount()) {
            tabs.setSelectedIndex(idx);
        }
    }

    public void abrirSubmodulo(String nombreSub) {
        if (nombreSub == null) return;
        String s = nombreSub.toLowerCase();
        if (s.contains("completa") || s.contains("dashboard") || s.contains("general")) {
            if (s.contains("parámetro") || s.contains("parametro") || s.contains("empresa") || s.contains("clínica")) {
                seleccionarPestana(1);
            } else {
                seleccionarPestana(0);
            }
        } else if (s.contains("usuario") || s.contains("rol") || s.contains("permiso") || s.contains("acceso")) {
            seleccionarPestana(2);
        } else if (s.contains("integraci") || s.contains("extern") || s.contains("api") || s.contains("whatsapp") || s.contains("dian")) {
            seleccionarPestana(3);
        } else if (s.contains("ia") || s.contains("inteligencia") || s.contains("soporte") || s.contains("ticket") || s.contains("diagnostico")) {
            seleccionarPestana(4);
        }
        setVisible(true);
        toFront();
    }

    public VistaDashboardAdministracionPanel getVistaDashboard() {
        return vistaDashboard;
    }

    public VistaParametrosGeneralesPanel getVistaParametros() {
        return vistaParametros;
    }

    public VistaUsuariosRolesPanel getVistaUsuarios() {
        return vistaUsuarios;
    }

    public VistaIntegracionesExternasPanel getVistaIntegraciones() {
        return vistaIntegraciones;
    }

    public VistaModuloIASoportePanel getVistaIASoporte() {
        return vistaIASoporte;
    }

    public static void main(String[] args) {
        Ui.instalarApariencia();
        javax.swing.SwingUtilities.invokeLater(() -> new Modulo10ConfiguracionSoporteFrame().setVisible(true));
    }
}
