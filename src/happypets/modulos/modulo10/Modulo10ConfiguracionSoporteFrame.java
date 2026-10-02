package happypets.modulos.modulo10;

import java.awt.Color;
import happypets.modulos.ModuloBaseFrame;
import happypets.ui.Iconos;

/**
 * Módulo 10: Configuración, Integraciones y Soporte
 * Responsable Asignado: Minaya Bravo, Almendra Lili
 * Submódulos:
 *  - Parámetros Generales
 *  - Usuarios, Roles y Permisos
 *  - Integraciones externas
 *  - Módulo de IA y Soporte Técnico
 */
public class Modulo10ConfiguracionSoporteFrame extends ModuloBaseFrame {
    private static final long serialVersionUID = 1L;

    public Modulo10ConfiguracionSoporteFrame() {
        super(
                10,
                "Configuración, Integraciones y Soporte",
                "Minaya Bravo, Almendra Lili",
                new String[]{
                        "Parámetros Generales",
                        "Usuarios, Roles y Permisos",
                        "Integraciones externas",
                        "Módulo de IA y Soporte Técnico"
                },
                new Color(200, 150, 0),
                Iconos.crearIconoCandado(22, new Color(200, 150, 0))
        );
    }
}
