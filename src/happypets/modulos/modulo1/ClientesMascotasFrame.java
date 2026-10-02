package happypets.modulos.modulo1;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import happypets.model.Cliente;
import happypets.ui.Ui;

/**
 * Pantalla 1: Mantenimiento de Clientes y Mascotas vinculadas.
 * Aloja el panel moderno VistaClientesMascotasPanel garantizando la nueva
 * arquitectura visual ERP y permitiendo ejecución autónoma y verificación.
 */
public class ClientesMascotasFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    private final VistaClientesMascotasPanel panelVista;

    public ClientesMascotasFrame() {
        setTitle("Happy Pets - Mantenimiento de Clientes y Mascotas");
        setIconImage(Ui.icono());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1320, 840);
        setMinimumSize(new Dimension(1100, 700));
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(248, 250, 252));
        setLayout(new BorderLayout());

        panelVista = new VistaClientesMascotasPanel();
        add(panelVista, BorderLayout.CENTER);
    }

    public void cargarCliente(Cliente cliente) {
        if (panelVista != null && cliente != null) {
            panelVista.cargarClienteEnFormulario(cliente);
        }
    }

    public VistaClientesMascotasPanel getPanelVista() {
        return panelVista;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Ui.instalarApariencia();
            new ClientesMascotasFrame().setVisible(true);
        });
    }
}
