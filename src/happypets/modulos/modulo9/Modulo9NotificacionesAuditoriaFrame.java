package happypets.modulos.modulo9;

import java.awt.Color;
import happypets.modulos.ModuloBaseFrame;
import happypets.ui.Iconos;

/**
 * Módulo 9: Notificaciones, Documentos y Auditoría
 * Responsable Asignado: Vera Aguilar, Carlos Edgardo
 * Submódulos:
 *  - Centro de Notificaciones
 *  - Configuración de Canales
 *  - Repositorio Documental
 *  - Logs y Trazabilidad
 */
public class Modulo9NotificacionesAuditoriaFrame extends ModuloBaseFrame {
    private static final long serialVersionUID = 1L;

    public Modulo9NotificacionesAuditoriaFrame() {
        super(
                9,
                "Notificaciones, Documentos y Auditoría",
                "Vera Aguilar, Carlos Edgardo",
                new String[]{
                        "Centro de Notificaciones",
                        "Configuración de Canales",
                        "Repositorio Documental",
                        "Logs y Trazabilidad"
                },
                new Color(100, 110, 120),
                Iconos.crearIconoHistorial(22, new Color(100, 110, 120))
        );
    }
}
