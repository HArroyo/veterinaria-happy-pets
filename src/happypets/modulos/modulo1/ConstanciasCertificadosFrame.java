package happypets.modulos.modulo1;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import happypets.model.Mascota;
import happypets.ui.Ui;

/**
 * Pantalla 3: Constancias y Certificados Médicos.
 * Aloja el panel moderno VistaConstanciasCertificadosPanel garantizando la nueva
 * arquitectura visual ERP y permitiendo ejecución autónoma y verificación.
 */
public class ConstanciasCertificadosFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    private final VistaConstanciasCertificadosPanel panelVista;

    public ConstanciasCertificadosFrame() {
        this(null);
    }

    public ConstanciasCertificadosFrame(Mascota mascota) {
        setTitle("Happy Pets - Constancias y Certificados Médicos");
        setIconImage(Ui.icono());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1320, 840);
        setMinimumSize(new Dimension(1100, 700));
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(248, 250, 252));
        setLayout(new BorderLayout());

        panelVista = new VistaConstanciasCertificadosPanel();
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

    public VistaConstanciasCertificadosPanel getPanelVista() {
        return panelVista;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Ui.instalarApariencia();
            new ConstanciasCertificadosFrame().setVisible(true);
        });
    }
}
