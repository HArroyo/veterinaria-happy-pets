package happypets.modulos.modulo8;

import java.awt.Color;
import happypets.modulos.ModuloBaseFrame;
import happypets.ui.Iconos;

/**
 * Módulo 8: Inteligencia de Negocios y Reportes
 * Responsable Asignado: Arroyo Preciado, Harry Martin
 * Submódulos:
 *  - Tableros de Mando (Dashboards)
 *  - Reportes Clínicos
 *  - Reportes Financieros
 *  - Exportador de Datos
 */
public class Modulo8ReportesBIFrame extends ModuloBaseFrame {
    private static final long serialVersionUID = 1L;

    public Modulo8ReportesBIFrame() {
        super(
                8,
                "Inteligencia de Negocios y Reportes",
                "Arroyo Preciado, Harry Martin",
                new String[]{
                        "Tableros de Mando (Dashboards)",
                        "Reportes Clínicos",
                        "Reportes Financieros",
                        "Exportador de Datos"
                },
                new Color(0, 115, 125),
                Iconos.crearIconoReportes(22, new Color(0, 115, 125))
        );
    }
}
