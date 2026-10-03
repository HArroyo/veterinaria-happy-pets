package happypets.modulos.modulo5;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import happypets.ui.Iconos;

/**
 * Ventana independiente para el Módulo 5: Inventario y Farmacia.
 * Responsable Asignado: Castro Pairazaman, Craig Kem
 * Contiene los 4 submódulos oficiales:
 *  1. Catálogo de Productos y Fármacos
 *  2. Control de Stock y Lotes
 *  3. Proveedores y Órdenes de Compra
 *  4. Ajustes y Mermas
 */
public class Modulo5InventarioFarmaciaFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    private final VistaCatalogoProductosPanel vistaCatalogo;
    private final VistaControlStockLotesPanel vistaStock;
    private final VistaProveedoresOrdenesPanel vistaProveedores;
    private final VistaAjustesMermasPanel vistaAjustes;
    private final JTabbedPane tabs;

    public Modulo5InventarioFarmaciaFrame() {
        this(0);
    }

    public Modulo5InventarioFarmaciaFrame(int pestanaInicial) {
        setTitle("Happy Pets - Módulo 5: Inventario y Farmacia");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1280, 820);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(248, 250, 252));

        vistaCatalogo = new VistaCatalogoProductosPanel();
        vistaStock = new VistaControlStockLotesPanel();
        vistaProveedores = new VistaProveedoresOrdenesPanel();
        vistaAjustes = new VistaAjustesMermasPanel();

        tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabs.setBackground(Color.WHITE);

        tabs.addTab("Catálogo de Productos y Fármacos", Iconos.crearIconoPildora(16, new Color(34, 197, 94)), vistaCatalogo);
        tabs.addTab("Control de Stock y Lotes", Iconos.crearIconoCajaAlmacen(16, new Color(14, 165, 233)), vistaStock);
        tabs.addTab("Proveedores y Órdenes de Compra", Iconos.crearIconoCamionProveedor(16, new Color(245, 158, 11)), vistaProveedores);
        tabs.addTab("Ajustes y Mermas", Iconos.crearIconoAlertaMerma(16, new Color(220, 38, 38)), vistaAjustes);

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
        javax.swing.SwingUtilities.invokeLater(() -> new Modulo5InventarioFarmaciaFrame().setVisible(true));
    }
}
