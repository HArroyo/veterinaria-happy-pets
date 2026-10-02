package happypets.modulos.modulo4;

import java.awt.Color;
import happypets.modulos.ModuloBaseFrame;
import happypets.ui.Iconos;

/**
 * Módulo 4: Servicios Estéticos y Hospedaje
 * Responsable Asignado: Quimi Valderrama, Francisco Raul
 * Submódulos:
 *  - Grooming y Peluquería
 *  - Hospitalización
 *  - Hotel / Guardería
 *  - Adopciones
 */
public class Modulo4EsteticosHospedajeFrame extends ModuloBaseFrame {
    private static final long serialVersionUID = 1L;

    public Modulo4EsteticosHospedajeFrame() {
        super(
                4,
                "Servicios Estéticos y Hospedaje",
                "Quimi Valderrama, Francisco Raul",
                new String[]{
                        "Grooming y Peluquería",
                        "Hospitalización",
                        "Hotel / Guardería",
                        "Adopciones"
                },
                new Color(230, 80, 130),
                Iconos.crearIconoHuella(22, new Color(230, 80, 130))
        );
    }
}
