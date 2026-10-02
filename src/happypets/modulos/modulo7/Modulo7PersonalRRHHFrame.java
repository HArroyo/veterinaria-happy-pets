package happypets.modulos.modulo7;

import java.awt.Color;
import happypets.modulos.ModuloBaseFrame;
import happypets.ui.Iconos;

/**
 * Módulo 7: Personal y Recursos Humanos
 * Responsable Asignado: Loli Espinoza, Víctor Manuel
 * Submódulos:
 *  - Veterinarios
 *  - Personal de Apoyo
 *  - Horarios y Turnos
 *  - Asistencias y Permisos
 */
public class Modulo7PersonalRRHHFrame extends ModuloBaseFrame {
    private static final long serialVersionUID = 1L;

    public Modulo7PersonalRRHHFrame() {
        super(
                7,
                "Personal y Recursos Humanos",
                "Loli Espinoza, Víctor Manuel",
                new String[]{
                        "Veterinarios",
                        "Personal de Apoyo",
                        "Horarios y Turnos",
                        "Asistencias y Permisos"
                },
                new Color(111, 66, 193),
                Iconos.crearIconoUsuario(22, new Color(111, 66, 193))
        );
    }
}
