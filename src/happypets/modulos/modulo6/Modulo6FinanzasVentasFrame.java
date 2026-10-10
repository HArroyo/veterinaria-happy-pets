package happypets.modulos.modulo6;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import happypets.ui.Iconos;
import happypets.ui.Ui;

/**
 * Ventana independiente para el Módulo 6: Finanzas y Ventas.
 * Responsable Asignada: Corrales Contreras, Joanna Andrea
 * Contiene los 4 submódulos oficiales del software según los wireframes:
 *  1. Punto de Venta (POS)
 *  2. Cuentas por Cobrar y Pagar
 *  3. Control de Caja Chica
 *  4. Gestión de Egresos Operativos
 */
public class Modulo6FinanzasVentasFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    private final VistaPuntoVentaPOSPanel vistaPOS;
    private final VistaCuentasCobrarPagarPanel vistaCuentas;
    private final VistaControlCajaChicaPanel vistaCajaChica;
    private final VistaEgresosOperativosPanel vistaEgresos;
    private final JTabbedPane tabs;

    public Modulo6FinanzasVentasFrame() {
        this(0);
    }

    public Modulo6FinanzasVentasFrame(int pestanaInicial) {
        setTitle("Happy Pets - Módulo 6: Finanzas y Ventas");
        setIconImage(Ui.icono());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1320, 840);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(248, 250, 252));

        vistaPOS = new VistaPuntoVentaPOSPanel();
        vistaCuentas = new VistaCuentasCobrarPagarPanel();
        vistaCajaChica = new VistaControlCajaChicaPanel();
        vistaEgresos = new VistaEgresosOperativosPanel();

        tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabs.setBackground(Color.WHITE);

        tabs.addTab("Punto de Venta (POS)",
                Iconos.crearIconoPOS(16, Ui.TURQUESA),
                vistaPOS);

        tabs.addTab("Cuentas por Cobrar y Pagar",
                Iconos.crearIconoCuentas(16, Ui.TURQUESA_OSCURO),
                vistaCuentas);

        tabs.addTab("Control de Caja Chica",
                Iconos.crearIconoCajaChica(16, Ui.TURQUESA),
                vistaCajaChica);

        tabs.addTab("Gestión de Egresos Operativos",
                Iconos.crearIconoEgresos(16, Ui.TURQUESA_OSCURO),
                vistaEgresos);

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
        if (s.contains("pos") || s.contains("punto")) {
            seleccionarPestana(0);
        } else if (s.contains("cobrar") || s.contains("pagar") || s.contains("cuentas")) {
            seleccionarPestana(1);
        } else if (s.contains("caja") || s.contains("chica")) {
            seleccionarPestana(2);
        } else if (s.contains("egreso") || s.contains("operativo")) {
            seleccionarPestana(3);
        }
        setVisible(true);
        toFront();
    }

    public static void main(String[] args) {
        Ui.instalarApariencia();
        javax.swing.SwingUtilities.invokeLater(() -> new Modulo6FinanzasVentasFrame().setVisible(true));
    }
}
