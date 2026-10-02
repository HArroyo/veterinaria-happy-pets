package happypets.modulos.modulo2;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import happypets.ui.Iconos;

/**
 * Ventana independiente para el Módulo 2: Agenda y Citas.
 * Contiene los 4 submódulos oficiales:
 *  1. Agendamiento de Citas
 *  2. Calendario Global
 *  3. Gestión de Recordatorios
 *  4. Sala de Espera y Triaje
 */
public class Modulo2AgendaCitasFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    private final VistaAgendamientoCitasPanel vistaAgendamiento;
    private final VistaCalendarioGlobalPanel vistaCalendario;
    private final VistaRecordatoriosPanel vistaRecordatorios;
    private final VistaSalaEsperaTriajePanel vistaSalaEspera;

    public Modulo2AgendaCitasFrame() {
        this(0);
    }

    public Modulo2AgendaCitasFrame(int pestanaInicial) {
        setTitle("Happy Pets - Módulo 2: Agenda y Citas");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1240, 780);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(248, 250, 252));

        vistaAgendamiento = new VistaAgendamientoCitasPanel();
        vistaCalendario = new VistaCalendarioGlobalPanel();
        vistaRecordatorios = new VistaRecordatoriosPanel();
        vistaSalaEspera = new VistaSalaEsperaTriajePanel();

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabs.setBackground(Color.WHITE);

        tabs.addTab("Agendamiento de Citas", Iconos.crearIconoCalendario(16, new Color(2, 132, 199)), vistaAgendamiento);
        tabs.addTab("Calendario Global", Iconos.crearIconoReloj(16, new Color(2, 132, 199)), vistaCalendario);
        tabs.addTab("Gestión de Recordatorios", Iconos.crearIconoWhatsApp(16, new Color(22, 163, 74)), vistaRecordatorios);
        tabs.addTab("Sala de Espera y Triaje", Iconos.crearIconoAlertaTriaje(16, new Color(220, 38, 38)), vistaSalaEspera);

        if (pestanaInicial >= 0 && pestanaInicial < tabs.getTabCount()) {
            tabs.setSelectedIndex(pestanaInicial);
        }

        add(tabs, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> new Modulo2AgendaCitasFrame().setVisible(true));
    }
}
