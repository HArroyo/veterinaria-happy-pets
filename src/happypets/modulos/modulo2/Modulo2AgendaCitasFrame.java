package happypets.modulos.modulo2;

import java.awt.Color;
import happypets.modulos.ModuloBaseFrame;
import happypets.ui.Iconos;
import happypets.ui.Ui;

/**
 * Módulo 2: Agenda y Citas
 * Responsable Asignado: Ayala Ornay, Antony Giovanny
 * Submódulos:
 *  - Agendamiento de Citas
 *  - Calendario Global
 *  - Gestión de Recordatorios
 *  - Sala de Espera y Triaje
 */
public class Modulo2AgendaCitasFrame extends ModuloBaseFrame {
    private static final long serialVersionUID = 1L;

    public Modulo2AgendaCitasFrame() {
        super(
                2,
                "Agenda y Citas",
                "Ayala Ornay, Antony Giovanny",
                new String[]{
                        "Agendamiento de Citas",
                        "Calendario Global",
                        "Gestión de Recordatorios",
                        "Sala de Espera y Triaje"
                },
                Ui.TURQUESA_OSCURO,
                Iconos.crearIconoCalendario(22, Ui.TURQUESA_OSCURO)
        );
    }
}
