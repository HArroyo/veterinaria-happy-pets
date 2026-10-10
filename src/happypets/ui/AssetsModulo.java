package happypets.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.Icon;
import javax.swing.JPanel;

/** Fondos vectoriales: blancos, con detalles celestes e identidad de cada módulo. */
public class AssetsModulo extends JPanel {
    private static final long serialVersionUID = 1L;

    public static Icon icono(int modulo, int size, Color color) {
        return switch (modulo) {
            case 1 -> Iconos.crearIconoHuella(size, color);
            case 2 -> Iconos.crearIconoCalendario(size, color);
            case 3 -> Iconos.crearIconoEstetoscopio(size, color);
            case 4 -> Iconos.crearIconoCasaMascota(size, color);
            case 5 -> Iconos.crearIconoPildora(size, color);
            case 6 -> Iconos.crearIconoPOS(size, color);
            case 7 -> Iconos.crearIconoClientes(size, color);
            case 8 -> Iconos.crearIconoGraficoBarras(size, color);
            case 9 -> Iconos.crearIconoCampana(size, color);
            case 10 -> Iconos.crearIconoConfiguracion(size, color);
            default -> Iconos.crearIconoHuella(size, color);
        };
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(new Color(240, 249, 255));
        g.fillOval(getWidth() - 210, -160, 360, 360);
        g.fillOval(-150, getHeight() - 150, 280, 280);
        String paquete = getClass().getPackageName();
        int modulo = paquete.matches(".*modulo\\d+$")
                ? Integer.parseInt(paquete.substring(paquete.lastIndexOf("modulo") + 6)) : 1;
        icono(modulo, 60, new Color(186, 230, 253)).paintIcon(this, g, getWidth() - 75, 12);
        g.dispose();
    }
}
