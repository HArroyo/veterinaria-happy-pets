package happypets.modulos.modulo1;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import happypets.model.Mascota;
import happypets.ui.Ui;

/**
 * Pantalla 2: Historial Clínico de Mascotas.
 * Aloja el panel moderno VistaHistorialClinicoPanel garantizando la nueva
 * arquitectura visual ERP y permitiendo ejecución autónoma y verificación.
 */
public class HistorialClinicoFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    private final VistaHistorialClinicoPanel panelVista;

    public HistorialClinicoFrame() {
        this(null);
    }

    public HistorialClinicoFrame(Mascota mascota) {
        setTitle("Happy Pets - Historial Clínico de Mascotas");
        setIconImage(Ui.icono());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1320, 840);
        setMinimumSize(new Dimension(1100, 700));
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(248, 250, 252));
        setLayout(new BorderLayout());

        panelVista = new VistaHistorialClinicoPanel();
        if (mascota != null) {
            panelVista.cargarMascota(mascota);
        }
        add(panelVista, BorderLayout.CENTER);
    }

    public void cargarMascota(Mascota mascota) {
        if (panelVista != null && mascota != null) {
            panelVista.cargarMascota(mascota);
        }
    }

    public VistaHistorialClinicoPanel getPanelVista() {
        return panelVista;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Ui.instalarApariencia();
            new HistorialClinicoFrame().setVisible(true);
        });
    }
}
