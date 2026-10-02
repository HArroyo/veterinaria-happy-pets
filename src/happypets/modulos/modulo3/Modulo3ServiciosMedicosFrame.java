package happypets.modulos.modulo3;

import java.awt.Color;
import happypets.modulos.ModuloBaseFrame;
import happypets.ui.Iconos;

/**
 * Módulo 3: Servicios Médicos y Quirúrgicos
 * Responsable Asignado: Martínez Gutiérrez, Gustavo Javier
 * Submódulos:
 *  - Consultas Médicas
 *  - Vacunación y Desparasitación
 *  - Cirugías y Quirófano
 *  - Laboratorio e Imágenes
 */
public class Modulo3ServiciosMedicosFrame extends ModuloBaseFrame {
    private static final long serialVersionUID = 1L;

    public Modulo3ServiciosMedicosFrame() {
        super(
                3,
                "Servicios Médicos y Quirúrgicos",
                "Martínez Gutiérrez, Gustavo Javier",
                new String[]{
                        "Consultas Médicas",
                        "Vacunación y Desparasitación",
                        "Cirugías y Quirófano",
                        "Laboratorio e Imágenes"
                },
                new Color(0, 150, 169),
                Iconos.crearIconoEstetoscopio(22, new Color(0, 150, 169))
        );
    }
}
