package happypets.modulos.modulo9;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import happypets.ui.Iconos;
import happypets.ui.Ui;

/**
 * Ventana independiente para el Módulo 9: Notificaciones, Documentos y Auditoría.
 * Responsable Asignado: Vera Aguilar, Carlos Edgardo
 * Contiene los 4 submódulos oficiales del software según los wireframes:
 *  1. Centro de Notificaciones (Alertas, mensajes y eventos de auditoría)
 *  2. Configuración de Canales (Pasarelas Email, SMS, Push y políticas de envío)
 *  3. Repositorio Documental (Custodia digital de contratos, informes y facturas)
 *  4. Logs y Trazabilidad (Pistas de auditoría inmutables y trazabilidad forense)
 */
public class Modulo9NotificacionesAuditoriaFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    private final VistaCentroNotificacionesPanel vistaCentroNotificaciones;
    private final VistaConfiguracionCanalesPanel vistaConfiguracionCanales;
    private final VistaRepositorioDocumentalPanel vistaRepositorioDocumental;
    private final VistaLogsTrazabilidadPanel vistaLogsTrazabilidad;
    private final JTabbedPane tabs;

    public Modulo9NotificacionesAuditoriaFrame() {
        this(0);
    }

    public Modulo9NotificacionesAuditoriaFrame(int pestanaInicial) {
        setTitle("Happy Pets - Módulo 9: Notificaciones, Documentos y Auditoría");
        setIconImage(Ui.icono());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1340, 850);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(248, 250, 252));

        vistaCentroNotificaciones = new VistaCentroNotificacionesPanel();
        vistaConfiguracionCanales = new VistaConfiguracionCanalesPanel();
        vistaRepositorioDocumental = new VistaRepositorioDocumentalPanel();
        vistaLogsTrazabilidad = new VistaLogsTrazabilidadPanel();

        tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabs.setBackground(Color.WHITE);

        tabs.addTab("Centro de Notificaciones",
                Iconos.crearIconoCampana(16, Ui.TURQUESA),
                vistaCentroNotificaciones);

        tabs.addTab("Configuración de Canales",
                Iconos.crearIconoCanales(16, Ui.TURQUESA_MEDIO),
                vistaConfiguracionCanales);

        tabs.addTab("Repositorio Documental",
                Iconos.crearIconoDocumento(16, Ui.TURQUESA_OSCURO),
                vistaRepositorioDocumental);

        tabs.addTab("Logs y Trazabilidad",
                Iconos.crearIconoHistorial(16, Ui.TURQUESA_PROFUNDO),
                vistaLogsTrazabilidad);

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
        if (s.contains("notificaci") || s.contains("alerta") || s.contains("aviso")) {
            seleccionarPestana(0);
        } else if (s.contains("canal") || s.contains("configuraci") || s.contains("email") || s.contains("sms")) {
            seleccionarPestana(1);
        } else if (s.contains("document") || s.contains("repositorio") || s.contains("archivo")) {
            seleccionarPestana(2);
        } else if (s.contains("log") || s.contains("trazabilidad") || s.contains("auditor") || s.contains("evento")) {
            seleccionarPestana(3);
        }
        setVisible(true);
        toFront();
    }

    public VistaCentroNotificacionesPanel getVistaCentroNotificaciones() {
        return vistaCentroNotificaciones;
    }

    public VistaConfiguracionCanalesPanel getVistaConfiguracionCanales() {
        return vistaConfiguracionCanales;
    }

    public VistaRepositorioDocumentalPanel getVistaRepositorioDocumental() {
        return vistaRepositorioDocumental;
    }

    public VistaLogsTrazabilidadPanel getVistaLogsTrazabilidad() {
        return vistaLogsTrazabilidad;
    }

    public static void main(String[] args) {
        Ui.instalarApariencia();
        javax.swing.SwingUtilities.invokeLater(() -> new Modulo9NotificacionesAuditoriaFrame().setVisible(true));
    }
}
