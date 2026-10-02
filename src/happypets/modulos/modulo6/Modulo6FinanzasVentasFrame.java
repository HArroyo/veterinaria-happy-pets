package happypets.modulos.modulo6;

import java.awt.Color;
import happypets.modulos.ModuloBaseFrame;
import happypets.ui.Iconos;

/**
 * Módulo 6: Finanzas y Ventas
 * Responsable Asignado: Corrales Contreras, Joanna Andrea
 * Submódulos:
 *  - Punto de Venta (POS)
 *  - Cuentas por Cobrar y Pagar
 *  - Control de Caja Chica
 *  - Gestión de Egresos Operativos
 */
public class Modulo6FinanzasVentasFrame extends ModuloBaseFrame {
    private static final long serialVersionUID = 1L;

    public Modulo6FinanzasVentasFrame() {
        super(
                6,
                "Finanzas y Ventas",
                "Corrales Contreras, Joanna Andrea",
                new String[]{
                        "Punto de Venta (POS)",
                        "Cuentas por Cobrar y Pagar",
                        "Control de Caja Chica",
                        "Gestión de Egresos Operativos"
                },
                new Color(255, 140, 0),
                Iconos.crearIconoFactura(22, new Color(255, 140, 0))
        );
    }
}
