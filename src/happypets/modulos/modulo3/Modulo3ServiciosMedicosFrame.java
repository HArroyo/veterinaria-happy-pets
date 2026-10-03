package happypets.modulos.modulo3;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import happypets.ui.Iconos;

/**
 * Ventana independiente para el Módulo 3: Servicios Médicos y Quirúrgicos.
 * Responsable Asignado: Martínez Gutiérrez, Gustavo Javier
 * Contiene los 4 submódulos clínicos oficiales:
 *  1. Consultas Médicas
 *  2. Vacunación y Desparasitación
 *  3. Cirugías y Quirófano
 *  4. Laboratorio e Imágenes
 */
public class Modulo3ServiciosMedicosFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    private final VistaConsultasMedicasPanel vistaConsultas;
    private final VistaVacunacionDesparasitacionPanel vistaVacunacion;
    private final VistaCirugiasQuirofanoPanel vistaCirugias;
    private final VistaLaboratorioImagenesPanel vistaLaboratorio;
    private final JTabbedPane tabs;

    public Modulo3ServiciosMedicosFrame() {
        this(0);
    }

    public Modulo3ServiciosMedicosFrame(int pestanaInicial) {
        setTitle("Happy Pets - Módulo 3: Servicios Médicos y Quirúrgicos");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1260, 800);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(248, 250, 252));

        vistaConsultas = new VistaConsultasMedicasPanel();
        vistaVacunacion = new VistaVacunacionDesparasitacionPanel();
        vistaCirugias = new VistaCirugiasQuirofanoPanel();
        vistaLaboratorio = new VistaLaboratorioImagenesPanel();

        tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabs.setBackground(Color.WHITE);

        tabs.addTab("Consultas Médicas", Iconos.crearIconoEstetoscopio(16, new Color(20, 184, 166)), vistaConsultas);
        tabs.addTab("Vacunación y Desparasitación", Iconos.crearIconoJeringa(16, new Color(2, 132, 199)), vistaVacunacion);
        tabs.addTab("Cirugías y Quirófano", Iconos.crearIconoBisturi(16, new Color(220, 38, 38)), vistaCirugias);
        tabs.addTab("Laboratorio e Imágenes", Iconos.crearIconoMicroscopio(16, new Color(147, 51, 234)), vistaLaboratorio);

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

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> new Modulo3ServiciosMedicosFrame().setVisible(true));
    }
}
