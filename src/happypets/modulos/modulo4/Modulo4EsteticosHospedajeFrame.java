package happypets.modulos.modulo4;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import happypets.ui.Iconos;

/**
 * Ventana independiente para el Módulo 4: Servicios Estéticos y Hospedaje.
 * Responsable Asignado: Quimi Valderrama, Francisco Raul
 * Contiene los 4 submódulos oficiales:
 *  1. Grooming y Peluquería
 *  2. Hospitalización
 *  3. Hotel / Guardería
 *  4. Adopciones y Rescates
 */
public class Modulo4EsteticosHospedajeFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    private final VistaGroomingPeluqueriaPanel vistaGrooming;
    private final VistaHospitalizacionPanel vistaHospitalizacion;
    private final VistaHotelGuarderiaPanel vistaHotel;
    private final VistaAdopcionesPanel vistaAdopciones;
    private final JTabbedPane tabs;

    public Modulo4EsteticosHospedajeFrame() {
        this(0);
    }

    public Modulo4EsteticosHospedajeFrame(int pestanaInicial) {
        setTitle("Happy Pets - Módulo 4: Servicios Estéticos y Hospedaje");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1260, 800);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(248, 250, 252));

        vistaGrooming = new VistaGroomingPeluqueriaPanel();
        vistaHospitalizacion = new VistaHospitalizacionPanel();
        vistaHotel = new VistaHotelGuarderiaPanel();
        vistaAdopciones = new VistaAdopcionesPanel();

        tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabs.setBackground(Color.WHITE);

        setIconImage(happypets.ui.Ui.icono());
        tabs.addTab("Grooming y Peluquería", Iconos.crearIconoTijeras(16, happypets.ui.Ui.TURQUESA), vistaGrooming);
        tabs.addTab("Hospitalización", Iconos.crearIconoCamaHospital(16, happypets.ui.Ui.COLOR_PELIGRO), vistaHospitalizacion);
        tabs.addTab("Hotel / Guardería", Iconos.crearIconoCasaMascota(16, happypets.ui.Ui.TURQUESA_OSCURO), vistaHotel);
        tabs.addTab("Adopciones y Rescates", Iconos.crearIconoCorazonMascota(16, happypets.ui.Ui.TURQUESA_PROFUNDO), vistaAdopciones);

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
        javax.swing.SwingUtilities.invokeLater(() -> new Modulo4EsteticosHospedajeFrame().setVisible(true));
    }
}
