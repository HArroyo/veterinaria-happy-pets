package happypets.modulos.modulo5;

import java.awt.Color;
import happypets.modulos.ModuloBaseFrame;
import happypets.ui.Iconos;

/**
 * Módulo 5: Inventario y Farmacia
 * Responsable Asignado: Castro Pairazaman, Craig Kem
 * Submódulos:
 *  - Catálogo de Productos y Fármacos
 *  - Control de Stock y Lotes
 *  - Proveedores y Órdenes de Compra
 *  - Ajustes y Mermas
 */
public class Modulo5InventarioFarmaciaFrame extends ModuloBaseFrame {
    private static final long serialVersionUID = 1L;

    public Modulo5InventarioFarmaciaFrame() {
        super(
                5,
                "Inventario y Farmacia",
                "Castro Pairazaman, Craig Kem",
                new String[]{
                        "Catálogo de Productos y Fármacos",
                        "Control de Stock y Lotes",
                        "Proveedores y Órdenes de Compra",
                        "Ajustes y Mermas"
                },
                new Color(40, 167, 69),
                Iconos.crearIconoPildora(22, new Color(40, 167, 69))
        );
    }
}
