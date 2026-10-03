package happypets.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * Generador de iconos vectoriales nativos de alta resolución para Java Swing.
 * Garantiza renderizado nítido e independiente de fuentes o soporte de emojis del sistema operativo.
 */
public final class Iconos {

    private Iconos() { }

    /**
     * Huella de mascota (perro / gato).
     */
    public static Icon crearIconoHuella(int size, Color color) {
        BufferedImage img = crearImagenBase(size);
        Graphics2D g = configG2(img);
        g.setColor(color);

        double s = size;
        // Almohadilla central
        double padW = s * 0.48;
        double padH = s * 0.40;
        double padX = (s - padW) / 2.0;
        double padY = s * 0.48;
        g.fill(new RoundRectangle2D.Double(padX, padY, padW, padH, padW * 0.6, padH * 0.6));

        // 4 dedos / deditos en arco
        double toeW = s * 0.17;
        double toeH = s * 0.24;

        g.fill(new Ellipse2D.Double(s * 0.14, s * 0.32, toeW, toeH));
        g.fill(new Ellipse2D.Double(s * 0.33, s * 0.14, toeW, toeH));
        g.fill(new Ellipse2D.Double(s * 0.50, s * 0.14, toeW, toeH));
        g.fill(new Ellipse2D.Double(s * 0.69, s * 0.32, toeW, toeH));

        g.dispose();
        return new ImageIcon(img);
    }

    /**
     * Estetoscopio o Cruz Médica Veterinaria.
     */
    public static Icon crearIconoEstetoscopio(int size, Color color) {
        BufferedImage img = crearImagenBase(size);
        Graphics2D g = configG2(img);
        g.setColor(color);
        g.setStroke(new BasicStroke((float) Math.max(1.8, size * 0.1), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        double s = size;
        // Tubos del estetoscopio (forma de U)
        double uX = s * 0.22;
        double uY = s * 0.15;
        double uW = s * 0.46;
        double uH = s * 0.48;
        g.drawArc((int) uX, (int) uY, (int) uW, (int) uH, 180, 180);

        // Auriculares arriba
        g.drawLine((int) uX, (int) (uY + uH / 2), (int) uX, (int) (s * 0.16));
        g.drawLine((int) (uX + uW), (int) (uY + uH / 2), (int) (uX + uW), (int) (s * 0.16));

        // Tubo hacia la campana
        double stemX = uX + uW / 2.0;
        double stemY = uY + uH;
        g.drawLine((int) stemX, (int) stemY, (int) stemX, (int) (s * 0.74));
        g.drawArc((int) (stemX - s * 0.15), (int) (s * 0.65), (int) (s * 0.3), (int) (s * 0.2), 0, 180);

        // Campana del estetoscopio
        g.fillOval((int) (stemX - s * 0.16), (int) (s * 0.68), (int) (s * 0.26), (int) (s * 0.26));

        g.dispose();
        return new ImageIcon(img);
    }

    /**
     * Historial Clínico / Portapapeles con líneas.
     */
    public static Icon crearIconoHistorial(int size, Color color) {
        BufferedImage img = crearImagenBase(size);
        Graphics2D g = configG2(img);
        g.setColor(color);

        double s = size;
        float stroke = (float) Math.max(1.5, s * 0.08);
        g.setStroke(new BasicStroke(stroke, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        // Hoja o tabla
        double docX = s * 0.20;
        double docY = s * 0.16;
        double docW = s * 0.60;
        double docH = s * 0.72;
        g.draw(new RoundRectangle2D.Double(docX, docY, docW, docH, s * 0.12, s * 0.12));

        // Clip superior
        double clipW = s * 0.30;
        double clipH = s * 0.12;
        double clipX = (s - clipW) / 2.0;
        g.fill(new RoundRectangle2D.Double(clipX, s * 0.08, clipW, clipH, s * 0.06, s * 0.06));

        // Líneas de texto clínicas
        double lineX1 = docX + s * 0.12;
        double lineX2 = docX + docW - s * 0.12;
        g.drawLine((int) lineX1, (int) (s * 0.38), (int) lineX2, (int) (s * 0.38));
        g.drawLine((int) lineX1, (int) (s * 0.52), (int) lineX2, (int) (s * 0.52));
        g.drawLine((int) lineX1, (int) (s * 0.66), (int) (lineX1 + (lineX2 - lineX1) * 0.65), (int) (s * 0.66));

        g.dispose();
        return new ImageIcon(img);
    }

    /**
     * Certificado Oficial / Documento con Sello de Garantía.
     */
    public static Icon crearIconoCertificado(int size, Color color) {
        BufferedImage img = crearImagenBase(size);
        Graphics2D g = configG2(img);
        g.setColor(color);

        double s = size;
        float stroke = (float) Math.max(1.5, s * 0.08);
        g.setStroke(new BasicStroke(stroke, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        // Documento
        double w = s * 0.65;
        double h = s * 0.75;
        double x = s * 0.12;
        double y = s * 0.10;
        g.draw(new RoundRectangle2D.Double(x, y, w, h, s * 0.1, s * 0.1));

        // Líneas superiores
        g.drawLine((int) (x + s * 0.10), (int) (y + s * 0.16), (int) (x + w - s * 0.10), (int) (y + s * 0.16));
        g.drawLine((int) (x + s * 0.10), (int) (y + s * 0.30), (int) (x + w - s * 0.10), (int) (y + s * 0.30));
        g.drawLine((int) (x + s * 0.10), (int) (y + s * 0.44), (int) (x + w * 0.60), (int) (y + s * 0.44));

        // Medalla / Sello circular con cinta en la esquina
        double sealR = s * 0.30;
        double sealX = s * 0.58;
        double sealY = s * 0.56;
        g.fill(new Ellipse2D.Double(sealX, sealY, sealR, sealR));

        // Cinta colgante
        int[] rx = {(int) (sealX + sealR * 0.3), (int) (sealX + sealR * 0.1), (int) (sealX + sealR * 0.5)};
        int[] ry = {(int) (sealY + sealR * 0.8), (int) (s * 0.96), (int) (sealY + sealR * 0.9)};
        g.fillPolygon(rx, ry, 3);

        int[] rx2 = {(int) (sealX + sealR * 0.7), (int) (sealX + sealR * 0.9), (int) (sealX + sealR * 0.5)};
        int[] ry2 = {(int) (sealY + sealR * 0.8), (int) (s * 0.96), (int) (sealY + sealR * 0.9)};
        g.fillPolygon(rx2, ry2, 3);

        g.dispose();
        return new ImageIcon(img);
    }

    /**
     * Usuario / Perfil de Médico Veterinario.
     */
    public static Icon crearIconoUsuario(int size, Color color) {
        BufferedImage img = crearImagenBase(size);
        Graphics2D g = configG2(img);
        g.setColor(color);

        double s = size;
        // Cabeza
        double headD = s * 0.38;
        double headX = (s - headD) / 2.0;
        double headY = s * 0.12;
        g.fill(new Ellipse2D.Double(headX, headY, headD, headD));

        // Hombros / Torso
        double bodyW = s * 0.72;
        double bodyH = s * 0.42;
        double bodyX = (s - bodyW) / 2.0;
        double bodyY = s * 0.52;
        g.fill(new RoundRectangle2D.Double(bodyX, bodyY, bodyW, bodyH, bodyW * 0.5, bodyH * 0.5));

        g.dispose();
        return new ImageIcon(img);
    }

    /**
     * Grupo de Clientes / Propietarios.
     */
    public static Icon crearIconoClientes(int size, Color color) {
        BufferedImage img = crearImagenBase(size);
        Graphics2D g = configG2(img);
        g.setColor(color);

        double s = size;
        // Persona principal (frente derecha)
        double head1 = s * 0.32;
        double h1X = s * 0.42;
        double h1Y = s * 0.16;
        g.fill(new Ellipse2D.Double(h1X, h1Y, head1, head1));
        g.fill(new RoundRectangle2D.Double(s * 0.32, s * 0.52, s * 0.54, s * 0.38, s * 0.25, s * 0.25));

        // Persona secundaria (fondo izquierda)
        Color semi = new Color(color.getRed(), color.getGreen(), color.getBlue(), (int) (color.getAlpha() * 0.75));
        g.setColor(semi);
        double head2 = s * 0.26;
        g.fill(new Ellipse2D.Double(s * 0.16, s * 0.24, head2, head2));
        g.fill(new RoundRectangle2D.Double(s * 0.08, s * 0.54, s * 0.40, s * 0.34, s * 0.20, s * 0.20));

        g.dispose();
        return new ImageIcon(img);
    }

    /**
     * Candado / Seguridad.
     */
    public static Icon crearIconoCandado(int size, Color color) {
        BufferedImage img = crearImagenBase(size);
        Graphics2D g = configG2(img);
        g.setColor(color);

        double s = size;
        float stroke = (float) Math.max(1.6, s * 0.1);
        g.setStroke(new BasicStroke(stroke, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        // Arco del candado
        double arcW = s * 0.40;
        double arcH = s * 0.42;
        double arcX = (s - arcW) / 2.0;
        double arcY = s * 0.14;
        g.draw(new RoundRectangle2D.Double(arcX, arcY, arcW, arcH, arcW, arcW));

        // Cuerpo del candado
        double bodyW = s * 0.62;
        double bodyH = s * 0.48;
        double bodyX = (s - bodyW) / 2.0;
        double bodyY = s * 0.42;
        g.fill(new RoundRectangle2D.Double(bodyX, bodyY, bodyW, bodyH, s * 0.15, s * 0.15));

        // Ojo de la cerradura en blanco
        g.setColor(Color.WHITE);
        double holeR = s * 0.12;
        g.fill(new Ellipse2D.Double((s - holeR) / 2.0, bodyY + bodyH * 0.28, holeR, holeR));

        g.dispose();
        return new ImageIcon(img);
    }

    /**
     * Info / Bombilla / Ayuda.
     */
    public static Icon crearIconoInfo(int size, Color color) {
        BufferedImage img = crearImagenBase(size);
        Graphics2D g = configG2(img);
        g.setColor(color);

        double s = size;
        float stroke = (float) Math.max(1.6, s * 0.09);
        g.setStroke(new BasicStroke(stroke, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        // Círculo exterior
        double pad = s * 0.08;
        double d = s - pad * 2;
        g.draw(new Ellipse2D.Double(pad, pad, d, d));

        // Punto de la 'i'
        double dotR = s * 0.10;
        g.fill(new Ellipse2D.Double((s - dotR) / 2.0, s * 0.26, dotR, dotR));

        // Barra de la 'i'
        g.drawLine((int) (s * 0.5), (int) (s * 0.44), (int) (s * 0.5), (int) (s * 0.72));

        g.dispose();
        return new ImageIcon(img);
    }

    /**
     * Cruz Médica.
     */
    public static Icon crearIconoCruzMedica(int size, Color color) {
        BufferedImage img = crearImagenBase(size);
        Graphics2D g = configG2(img);
        g.setColor(color);

        double s = size;
        double barW = s * 0.28;
        double barL = s * 0.74;

        // Barra vertical
        g.fill(new RoundRectangle2D.Double((s - barW) / 2.0, (s - barL) / 2.0, barW, barL, s * 0.1, s * 0.1));
        // Barra horizontal
        g.fill(new RoundRectangle2D.Double((s - barL) / 2.0, (s - barW) / 2.0, barL, barW, s * 0.1, s * 0.1));

        g.dispose();
        return new ImageIcon(img);
    }

    /**
     * Lupa de Búsqueda.
     */
    public static Icon crearIconoBuscar(int size, Color color) {
        BufferedImage img = crearImagenBase(size);
        Graphics2D g = configG2(img);
        g.setColor(color);

        double s = size;
        float stroke = (float) Math.max(1.6, s * 0.1);
        g.setStroke(new BasicStroke(stroke, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        // Lente circular
        double r = s * 0.28;
        double cx = s * 0.40;
        double cy = s * 0.40;
        g.draw(new Ellipse2D.Double(cx - r, cy - r, r * 2, r * 2));

        // Mango de la lupa
        double handleStart = r * 0.707;
        g.drawLine((int) (cx + handleStart), (int) (cy + handleStart), (int) (s * 0.85), (int) (s * 0.85));

        g.dispose();
        return new ImageIcon(img);
    }

    /**
     * Impresora.
     */
    public static Icon crearIconoImprimir(int size, Color color) {
        BufferedImage img = crearImagenBase(size);
        Graphics2D g = configG2(img);
        g.setColor(color);

        double s = size;
        float stroke = (float) Math.max(1.5, s * 0.08);
        g.setStroke(new BasicStroke(stroke, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        // Cuerpo impresora
        g.draw(new RoundRectangle2D.Double(s * 0.12, s * 0.35, s * 0.76, s * 0.38, s * 0.1, s * 0.1));

        // Hoja superior
        g.draw(new RoundRectangle2D.Double(s * 0.26, s * 0.12, s * 0.48, s * 0.23, s * 0.06, s * 0.06));

        // Hoja inferior que sale
        g.draw(new RoundRectangle2D.Double(s * 0.24, s * 0.60, s * 0.52, s * 0.26, s * 0.06, s * 0.06));

        g.dispose();
        return new ImageIcon(img);
    }

    /**
     * Menú / Barras de navegación.
     */
    public static Icon crearIconoMenu(int size, Color color) {
        BufferedImage img = crearImagenBase(size);
        Graphics2D g = configG2(img);
        g.setColor(color);
        double s = size;
        float stroke = (float) Math.max(1.8, s * 0.12);
        g.setStroke(new BasicStroke(stroke, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        double x1 = s * 0.18;
        double x2 = s * 0.82;
        g.drawLine((int) x1, (int) (s * 0.28), (int) x2, (int) (s * 0.28));
        g.drawLine((int) x1, (int) (s * 0.50), (int) x2, (int) (s * 0.50));
        g.drawLine((int) x1, (int) (s * 0.72), (int) x2, (int) (s * 0.72));

        g.dispose();
        return new ImageIcon(img);
    }

    /**
     * Calendario / Citas veterinarias.
     */
    public static Icon crearIconoCalendario(int size, Color color) {
        BufferedImage img = crearImagenBase(size);
        Graphics2D g = configG2(img);
        g.setColor(color);
        double s = size;
        float stroke = (float) Math.max(1.5, s * 0.08);
        g.setStroke(new BasicStroke(stroke, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        // Marco del calendario
        double w = s * 0.74;
        double h = s * 0.68;
        double x = (s - w) / 2.0;
        double y = s * 0.20;
        g.draw(new RoundRectangle2D.Double(x, y, w, h, s * 0.1, s * 0.1));

        // Barra superior
        g.drawLine((int) x, (int) (y + h * 0.28), (int) (x + w), (int) (y + h * 0.28));

        // Anillas superiores
        g.drawLine((int) (x + w * 0.26), (int) (s * 0.10), (int) (x + w * 0.26), (int) (y + h * 0.18));
        g.drawLine((int) (x + w * 0.74), (int) (s * 0.10), (int) (x + w * 0.74), (int) (y + h * 0.18));

        // Puntos/días del calendario
        double dotSize = s * 0.08;
        g.fill(new Ellipse2D.Double(x + w * 0.24, y + h * 0.46, dotSize, dotSize));
        g.fill(new Ellipse2D.Double(x + w * 0.50, y + h * 0.46, dotSize, dotSize));
        g.fill(new Ellipse2D.Double(x + w * 0.76, y + h * 0.46, dotSize, dotSize));
        g.fill(new Ellipse2D.Double(x + w * 0.24, y + h * 0.72, dotSize, dotSize));
        g.fill(new Ellipse2D.Double(x + w * 0.50, y + h * 0.72, dotSize, dotSize));
        g.fill(new Ellipse2D.Double(x + w * 0.76, y + h * 0.72, dotSize, dotSize));

        g.dispose();
        return new ImageIcon(img);
    }

    /**
     * Factura / Comprobante de pago.
     */
    public static Icon crearIconoFactura(int size, Color color) {
        BufferedImage img = crearImagenBase(size);
        Graphics2D g = configG2(img);
        g.setColor(color);
        double s = size;
        float stroke = (float) Math.max(1.5, s * 0.08);
        g.setStroke(new BasicStroke(stroke, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        double w = s * 0.62;
        double h = s * 0.78;
        double x = (s - w) / 2.0;
        double y = s * 0.11;
        g.draw(new RoundRectangle2D.Double(x, y, w, h, s * 0.08, s * 0.08));

        // Líneas de texto y monto
        g.drawLine((int) (x + s * 0.10), (int) (y + s * 0.18), (int) (x + w - s * 0.10), (int) (y + s * 0.18));
        g.drawLine((int) (x + s * 0.10), (int) (y + s * 0.32), (int) (x + w - s * 0.10), (int) (y + s * 0.32));
        g.drawLine((int) (x + s * 0.10), (int) (y + s * 0.46), (int) (x + w - s * 0.22), (int) (y + s * 0.46));
        g.drawLine((int) (x + s * 0.10), (int) (y + s * 0.60), (int) (x + w - s * 0.10), (int) (y + s * 0.60));

        g.dispose();
        return new ImageIcon(img);
    }

    /**
     * Píldora / Farmacia veterinaria.
     */
    public static Icon crearIconoPildora(int size, Color color) {
        BufferedImage img = crearImagenBase(size);
        Graphics2D g = configG2(img);
        g.setColor(color);
        double s = size;
        float stroke = (float) Math.max(1.5, s * 0.08);
        g.setStroke(new BasicStroke(stroke, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        // Cápsula inclinada 45 grados
        g.rotate(Math.toRadians(-40), s / 2.0, s / 2.0);
        double w = s * 0.36;
        double h = s * 0.76;
        double x = (s - w) / 2.0;
        double y = (s - h) / 2.0;
        g.draw(new RoundRectangle2D.Double(x, y, w, h, w, w));
        // Línea divisoria central de la cápsula
        g.drawLine((int) x, (int) (s / 2.0), (int) (x + w), (int) (s / 2.0));

        g.dispose();
        return new ImageIcon(img);
    }

    /**
     * Cerrar sesión / Salir del sistema.
     */
    public static Icon crearIconoSalir(int size, Color color) {
        BufferedImage img = crearImagenBase(size);
        Graphics2D g = configG2(img);
        g.setColor(color);
        double s = size;
        float stroke = (float) Math.max(1.6, s * 0.09);
        g.setStroke(new BasicStroke(stroke, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        // Marco de puerta
        double x = s * 0.18;
        double y = s * 0.14;
        double w = s * 0.40;
        double h = s * 0.72;
        g.draw(new RoundRectangle2D.Double(x, y, w, h, s * 0.06, s * 0.06));

        // Flecha de salida apuntando a la derecha
        int midY = (int) (s * 0.50);
        int arrStart = (int) (s * 0.42);
        int arrEnd = (int) (s * 0.84);
        g.drawLine(arrStart, midY, arrEnd, midY);
        g.drawLine((int) (arrEnd - s * 0.14), (int) (midY - s * 0.14), arrEnd, midY);
        g.drawLine((int) (arrEnd - s * 0.14), (int) (midY + s * 0.14), arrEnd, midY);

        g.dispose();
        return new ImageIcon(img);
    }

    /**
     * Gráfico de reportes y estadísticas.
     */
    public static Icon crearIconoReportes(int size, Color color) {
        BufferedImage img = crearImagenBase(size);
        Graphics2D g = configG2(img);
        g.setColor(color);
        double s = size;
        float stroke = (float) Math.max(1.5, s * 0.08);
        g.setStroke(new BasicStroke(stroke, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        // Ejes
        g.drawLine((int) (s * 0.16), (int) (s * 0.82), (int) (s * 0.86), (int) (s * 0.82));
        g.drawLine((int) (s * 0.16), (int) (s * 0.16), (int) (s * 0.16), (int) (s * 0.82));

        // Barras
        double barW = s * 0.14;
        g.fillRect((int) (s * 0.26), (int) (s * 0.52), (int) barW, (int) (s * 0.30));
        g.fillRect((int) (s * 0.46), (int) (s * 0.32), (int) barW, (int) (s * 0.50));
        g.fillRect((int) (s * 0.66), (int) (s * 0.20), (int) barW, (int) (s * 0.62));

        g.dispose();
        return new ImageIcon(img);
    }

    /**
     * Campana de notificaciones.
     */
    public static Icon crearIconoCampana(int size, Color color) {
        BufferedImage img = crearImagenBase(size);
        Graphics2D g = configG2(img);
        g.setColor(color);
        double s = size;
        float stroke = (float) Math.max(1.5, s * 0.08);
        g.setStroke(new BasicStroke(stroke, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        // Aro superior
        double ringD = s * 0.16;
        g.draw(new Ellipse2D.Double((s - ringD) / 2.0, s * 0.10, ringD, ringD));

        // Cuerpo campana
        int[] xPoints = {
                (int) (s * 0.32), (int) (s * 0.32), (int) (s * 0.18), (int) (s * 0.82), (int) (s * 0.68), (int) (s * 0.68)
        };
        int[] yPoints = {
                (int) (s * 0.48), (int) (s * 0.32), (int) (s * 0.72), (int) (s * 0.72), (int) (s * 0.32), (int) (s * 0.48)
        };
        g.drawPolyline(xPoints, yPoints, 6);
        g.drawLine((int) (s * 0.14), (int) (s * 0.72), (int) (s * 0.86), (int) (s * 0.72));

        // Badajo de la campana
        g.fill(new Ellipse2D.Double(s * 0.42, s * 0.76, s * 0.16, s * 0.14));

        g.dispose();
        return new ImageIcon(img);
    }

    /**
     * Flecha Chevron (arriba o abajo).
     */
    public static Icon crearIconoChevron(int size, Color color, boolean up) {
        BufferedImage img = crearImagenBase(size);
        Graphics2D g = configG2(img);
        g.setColor(color);
        double s = size;
        float stroke = (float) Math.max(1.8, s * 0.14);
        g.setStroke(new BasicStroke(stroke, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        double x1 = s * 0.22;
        double xMid = s * 0.50;
        double x2 = s * 0.78;

        if (up) {
            double yLow = s * 0.62;
            double yHigh = s * 0.38;
            g.drawLine((int) x1, (int) yLow, (int) xMid, (int) yHigh);
            g.drawLine((int) xMid, (int) yHigh, (int) x2, (int) yLow);
        } else {
            double yHigh = s * 0.38;
            double yLow = s * 0.62;
            g.drawLine((int) x1, (int) yHigh, (int) xMid, (int) yLow);
            g.drawLine((int) xMid, (int) yLow, (int) x2, (int) yHigh);
        }

        g.dispose();
        return new ImageIcon(img);
    }

    /**
     * Dashboard / Inicio.
     */
    public static Icon crearIconoDashboard(int size, Color color) {
        BufferedImage img = crearImagenBase(size);
        Graphics2D g = configG2(img);
        g.setColor(color);
        double s = size;
        float stroke = (float) Math.max(1.5, s * 0.08);
        g.setStroke(new BasicStroke(stroke, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        // Techo
        int[] rx = {(int) (s * 0.15), (int) (s * 0.50), (int) (s * 0.85)};
        int[] ry = {(int) (s * 0.46), (int) (s * 0.18), (int) (s * 0.46)};
        g.drawPolyline(rx, ry, 3);

        // Paredes
        g.draw(new RoundRectangle2D.Double(s * 0.24, s * 0.46, s * 0.52, s * 0.42, s * 0.06, s * 0.06));

        // Puerta
        g.draw(new RoundRectangle2D.Double(s * 0.42, s * 0.58, s * 0.16, s * 0.30, s * 0.04, s * 0.04));

        g.dispose();
        return new ImageIcon(img);
    }

    /**
     * Reloj / Horario de cita médica.
     */
    public static Icon crearIconoReloj(int size, Color color) {
        BufferedImage img = crearImagenBase(size);
        Graphics2D g = configG2(img);
        g.setColor(color);
        double s = size;
        float stroke = (float) Math.max(1.5, s * 0.08);
        g.setStroke(new BasicStroke(stroke, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        // Esfera del reloj
        double pad = s * 0.10;
        double d = s - pad * 2.0;
        g.draw(new Ellipse2D.Double(pad, pad, d, d));

        // Manecillas del reloj (centro a las 12 y a las 3)
        double cx = s / 2.0;
        double cy = s / 2.0;
        g.drawLine((int) cx, (int) cy, (int) cx, (int) (cy - s * 0.28));
        g.drawLine((int) cx, (int) cy, (int) (cx + s * 0.22), (int) cy);

        // Punto central
        double pr = s * 0.06;
        g.fill(new Ellipse2D.Double(cx - pr / 2.0, cy - pr / 2.0, pr, pr));

        g.dispose();
        return new ImageIcon(img);
    }

    /**
     * WhatsApp / Mensajería directa.
     */
    public static Icon crearIconoWhatsApp(int size, Color color) {
        BufferedImage img = crearImagenBase(size);
        Graphics2D g = configG2(img);
        g.setColor(color);
        double s = size;
        float stroke = (float) Math.max(1.5, s * 0.08);
        g.setStroke(new BasicStroke(stroke, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        // Bocadillo de diálogo redondeado
        double bw = s * 0.74;
        double bh = s * 0.62;
        double bx = (s - bw) / 2.0;
        double by = s * 0.12;
        g.draw(new RoundRectangle2D.Double(bx, by, bw, bh, s * 0.25, s * 0.25));

        // Cola del bocadillo abajo a la izquierda
        int[] tx = {(int) (bx + s * 0.12), (int) (bx + s * 0.02), (int) (bx + s * 0.26)};
        int[] ty = {(int) (by + bh - s * 0.04), (int) (s * 0.88), (int) (by + bh - s * 0.02)};
        g.fillPolygon(tx, ty, 3);

        // Teléfono auricular estilizado dentro
        double phW = s * 0.36;
        double phH = s * 0.24;
        g.drawArc((int) (bx + (bw - phW) / 2.0), (int) (by + (bh - phH) / 2.0), (int) phW, (int) phH, 30, 200);

        g.dispose();
        return new ImageIcon(img);
    }

    /**
     * Sobre de correo / SMS.
     */
    public static Icon crearIconoMensaje(int size, Color color) {
        BufferedImage img = crearImagenBase(size);
        Graphics2D g = configG2(img);
        g.setColor(color);
        double s = size;
        float stroke = (float) Math.max(1.5, s * 0.08);
        g.setStroke(new BasicStroke(stroke, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        // Sobre
        double w = s * 0.76;
        double h = s * 0.54;
        double x = (s - w) / 2.0;
        double y = (s - h) / 2.0;
        g.draw(new RoundRectangle2D.Double(x, y, w, h, s * 0.08, s * 0.08));

        // Solapa del sobre
        g.drawLine((int) x, (int) y, (int) (s / 2.0), (int) (y + h * 0.58));
        g.drawLine((int) (s / 2.0), (int) (y + h * 0.58), (int) (x + w), (int) y);

        g.dispose();
        return new ImageIcon(img);
    }

    /**
     * Alerta de Triaje / Urgencias clínicas (pulso cardíaco).
     */
    public static Icon crearIconoAlertaTriaje(int size, Color color) {
        BufferedImage img = crearImagenBase(size);
        Graphics2D g = configG2(img);
        g.setColor(color);
        double s = size;
        float stroke = (float) Math.max(1.8, s * 0.09);
        g.setStroke(new BasicStroke(stroke, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        // Línea de electrocardiograma / pulso vital
        double midY = s * 0.50;
        int[] px = {
                (int) (s * 0.12), (int) (s * 0.30), (int) (s * 0.40),
                (int) (s * 0.50), (int) (s * 0.60), (int) (s * 0.70), (int) (s * 0.88)
        };
        int[] py = {
                (int) midY, (int) midY, (int) (s * 0.24),
                (int) (s * 0.76), (int) (s * 0.34), (int) midY, (int) midY
        };
        g.drawPolyline(px, py, 7);

        g.dispose();
        return new ImageIcon(img);
    }

    /**
     * Check / Confirmación de asistencia.
     */
    public static Icon crearIconoCheck(int size, Color color) {
        BufferedImage img = crearImagenBase(size);
        Graphics2D g = configG2(img);
        g.setColor(color);
        double s = size;
        float stroke = (float) Math.max(1.8, s * 0.12);
        g.setStroke(new BasicStroke(stroke, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        double x1 = s * 0.20;
        double y1 = s * 0.52;
        double x2 = s * 0.42;
        double y2 = s * 0.74;
        double x3 = s * 0.82;
        double y3 = s * 0.26;

        g.drawLine((int) x1, (int) y1, (int) x2, (int) y2);
        g.drawLine((int) x2, (int) y2, (int) x3, (int) y3);

        g.dispose();
        return new ImageIcon(img);
    }

    /**
     * Jeringa médica para Vacunación y Desparasitación.
     */
    public static Icon crearIconoJeringa(int size, Color color) {
        BufferedImage img = crearImagenBase(size);
        Graphics2D g = configG2(img);
        g.setColor(color);
        double s = size;
        float stroke = (float) Math.max(1.6, s * 0.08);
        g.setStroke(new BasicStroke(stroke, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        // Cuerpo cilíndrico diagonal de la jeringa
        int[] px = {(int) (s * 0.35), (int) (s * 0.65), (int) (s * 0.50), (int) (s * 0.20)};
        int[] py = {(int) (s * 0.20), (int) (s * 0.50), (int) (s * 0.65), (int) (s * 0.35)};
        g.drawPolygon(px, py, 4);

        // Aguja saliente abajo a la izquierda
        g.drawLine((int) (s * 0.20), (int) (s * 0.65), (int) (s * 0.08), (int) (s * 0.88));

        // Émbolo / pulsador arriba a la derecha
        g.drawLine((int) (s * 0.65), (int) (s * 0.20), (int) (s * 0.82), (int) (s * 0.08));
        g.drawLine((int) (s * 0.74), (int) (s * 0.05), (int) (s * 0.90), (int) (s * 0.21));

        // Marcas de medición
        g.drawLine((int) (s * 0.34), (int) (s * 0.36), (int) (s * 0.40), (int) (s * 0.42));
        g.drawLine((int) (s * 0.42), (int) (s * 0.28), (int) (s * 0.48), (int) (s * 0.34));

        g.dispose();
        return new ImageIcon(img);
    }

    /**
     * Bisturí quirúrgico para Cirugías y Quirófano.
     */
    public static Icon crearIconoBisturi(int size, Color color) {
        BufferedImage img = crearImagenBase(size);
        Graphics2D g = configG2(img);
        g.setColor(color);
        double s = size;
        float stroke = (float) Math.max(1.6, s * 0.08);
        g.setStroke(new BasicStroke(stroke, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        // Mango estilizado
        g.drawLine((int) (s * 0.75), (int) (s * 0.15), (int) (s * 0.40), (int) (s * 0.55));

        // Hoja afilada curva del bisturí
        int[] bx = {(int) (s * 0.40), (int) (s * 0.18), (int) (s * 0.25), (int) (s * 0.44)};
        int[] by = {(int) (s * 0.55), (int) (s * 0.82), (int) (s * 0.86), (int) (s * 0.60)};
        g.fillPolygon(bx, by, 4);

        g.dispose();
        return new ImageIcon(img);
    }

    /**
     * Microscopio / Tubo para Laboratorio Clínico.
     */
    public static Icon crearIconoMicroscopio(int size, Color color) {
        BufferedImage img = crearImagenBase(size);
        Graphics2D g = configG2(img);
        g.setColor(color);
        double s = size;
        float stroke = (float) Math.max(1.6, s * 0.08);
        g.setStroke(new BasicStroke(stroke, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        // Base plana
        g.drawLine((int) (s * 0.20), (int) (s * 0.86), (int) (s * 0.80), (int) (s * 0.86));
        // Brazo curvado
        g.drawArc((int) (s * 0.35), (int) (s * 0.35), (int) (s * 0.45), (int) (s * 0.45), 270, 180);
        // Tubo ocular inclinado
        g.drawLine((int) (s * 0.42), (int) (s * 0.18), (int) (s * 0.30), (int) (s * 0.45));
        // Platina portaobjetos
        g.drawLine((int) (s * 0.22), (int) (s * 0.60), (int) (s * 0.50), (int) (s * 0.60));

        g.dispose();
        return new ImageIcon(img);
    }

    /**
     * Placa de Rayos X / Imagen Diagnóstica.
     */
    public static Icon crearIconoRx(int size, Color color) {
        BufferedImage img = crearImagenBase(size);
        Graphics2D g = configG2(img);
        g.setColor(color);
        double s = size;
        float stroke = (float) Math.max(1.6, s * 0.08);
        g.setStroke(new BasicStroke(stroke, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        // Marco de la placa radiográfica
        g.drawRoundRect((int) (s * 0.15), (int) (s * 0.15), (int) (s * 0.70), (int) (s * 0.70), (int) (s * 0.15), (int) (s * 0.15));

        // Letras 'R' y 'X' dentro de la placa
        g.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, (int) (s * 0.38)));
        java.awt.FontMetrics fm = g.getFontMetrics();
        String txt = "RX";
        int tx = (int) ((s - fm.stringWidth(txt)) / 2.0);
        int ty = (int) ((s + fm.getAscent() - fm.getDescent()) / 2.0);
        g.drawString(txt, tx, ty);

        g.dispose();
        return new ImageIcon(img);
    }

    /**
     * Tijeras de peluquería y estética / Grooming.
     */
    public static Icon crearIconoTijeras(int size, Color color) {
        BufferedImage img = crearImagenBase(size);
        Graphics2D g = configG2(img);
        g.setColor(color);
        double s = size;
        float stroke = (float) Math.max(1.6, s * 0.08);
        g.setStroke(new BasicStroke(stroke, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        // Dos anillas de mango abajo
        g.drawOval((int) (s * 0.15), (int) (s * 0.65), (int) (s * 0.28), (int) (s * 0.28));
        g.drawOval((int) (s * 0.57), (int) (s * 0.65), (int) (s * 0.28), (int) (s * 0.28));

        // Hojas cruzadas hacia arriba
        g.drawLine((int) (s * 0.35), (int) (s * 0.68), (int) (s * 0.75), (int) (s * 0.12));
        g.drawLine((int) (s * 0.65), (int) (s * 0.68), (int) (s * 0.25), (int) (s * 0.12));

        // Remache central
        g.fillOval((int) (s * 0.45), (int) (s * 0.43), (int) (s * 0.10), (int) (s * 0.10));

        g.dispose();
        return new ImageIcon(img);
    }

    /**
     * Cama de hospitalización / Canil de internamiento clínico.
     */
    public static Icon crearIconoCamaHospital(int size, Color color) {
        BufferedImage img = crearImagenBase(size);
        Graphics2D g = configG2(img);
        g.setColor(color);
        double s = size;
        float stroke = (float) Math.max(1.6, s * 0.08);
        g.setStroke(new BasicStroke(stroke, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        // Cabecera y pies
        g.drawLine((int) (s * 0.15), (int) (s * 0.35), (int) (s * 0.15), (int) (s * 0.85));
        g.drawLine((int) (s * 0.85), (int) (s * 0.45), (int) (s * 0.85), (int) (s * 0.85));

        // Colchón horizontal
        g.drawLine((int) (s * 0.15), (int) (s * 0.65), (int) (s * 0.85), (int) (s * 0.65));

        // Almohada
        g.fillRoundRect((int) (s * 0.18), (int) (s * 0.54), (int) (s * 0.22), (int) (s * 0.10), 4, 4);

        // Cruz de internamiento médico arriba a la derecha
        g.drawLine((int) (s * 0.65), (int) (s * 0.20), (int) (s * 0.65), (int) (s * 0.38));
        g.drawLine((int) (s * 0.56), (int) (s * 0.29), (int) (s * 0.74), (int) (s * 0.29));

        g.dispose();
        return new ImageIcon(img);
    }

    /**
     * Casita / Hotel y Guardería de mascotas.
     */
    public static Icon crearIconoCasaMascota(int size, Color color) {
        BufferedImage img = crearImagenBase(size);
        Graphics2D g = configG2(img);
        g.setColor(color);
        double s = size;
        float stroke = (float) Math.max(1.6, s * 0.08);
        g.setStroke(new BasicStroke(stroke, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        // Techo en triángulo
        int[] tx = {(int) (s * 0.10), (int) (s * 0.50), (int) (s * 0.90)};
        int[] ty = {(int) (s * 0.45), (int) (s * 0.15), (int) (s * 0.45)};
        g.drawPolygon(tx, ty, 3);

        // Paredes cuadradas
        g.drawRect((int) (s * 0.20), (int) (s * 0.45), (int) (s * 0.60), (int) (s * 0.42));

        // Puerta en arco
        g.drawArc((int) (s * 0.38), (int) (s * 0.58), (int) (s * 0.24), (int) (s * 0.28), 0, 180);
        g.drawLine((int) (s * 0.38), (int) (s * 0.72), (int) (s * 0.38), (int) (s * 0.87));
        g.drawLine((int) (s * 0.62), (int) (s * 0.72), (int) (s * 0.62), (int) (s * 0.87));

        g.dispose();
        return new ImageIcon(img);
    }

    /**
     * Corazón con huella / Adopciones y Rescates.
     */
    public static Icon crearIconoCorazonMascota(int size, Color color) {
        BufferedImage img = crearImagenBase(size);
        Graphics2D g = configG2(img);
        g.setColor(color);
        double s = size;

        // Corazón contorno
        float stroke = (float) Math.max(1.6, s * 0.08);
        g.setStroke(new BasicStroke(stroke, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        // Trazado de dos arcos de corazón
        g.drawArc((int) (s * 0.15), (int) (s * 0.15), (int) (s * 0.36), (int) (s * 0.36), 0, 180);
        g.drawArc((int) (s * 0.49), (int) (s * 0.15), (int) (s * 0.36), (int) (s * 0.36), 0, 180);
        g.drawLine((int) (s * 0.15), (int) (s * 0.33), (int) (s * 0.50), (int) (s * 0.85));
        g.drawLine((int) (s * 0.85), (int) (s * 0.33), (int) (s * 0.50), (int) (s * 0.85));

        // Almohadilla interna en el centro
        g.fillOval((int) (s * 0.44), (int) (s * 0.42), (int) (s * 0.12), (int) (s * 0.12));
        g.fillOval((int) (s * 0.36), (int) (s * 0.34), (int) (s * 0.07), (int) (s * 0.07));
        g.fillOval((int) (s * 0.45), (int) (s * 0.28), (int) (s * 0.07), (int) (s * 0.07));
        g.fillOval((int) (s * 0.55), (int) (s * 0.34), (int) (s * 0.07), (int) (s * 0.07));
        g.dispose();
        return new ImageIcon(img);
    }

    /**
     * Documento / Contrato / Ficha clínica.
     */
    public static Icon crearIconoDocumento(int size, Color color) {
        BufferedImage img = crearImagenBase(size);
        Graphics2D g = configG2(img);
        g.setColor(color);
        double s = size;
        float stroke = (float) Math.max(1.6, s * 0.08);
        g.setStroke(new BasicStroke(stroke, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        // Hoja rectangular con esquina redondeada
        int x = (int) (s * 0.20);
        int y = (int) (s * 0.12);
        int w = (int) (s * 0.60);
        int h = (int) (s * 0.76);
        g.drawRoundRect(x, y, w, h, 4, 4);

        // Líneas horizontales de texto
        g.drawLine((int) (s * 0.32), (int) (s * 0.32), (int) (s * 0.68), (int) (s * 0.32));
        g.drawLine((int) (s * 0.32), (int) (s * 0.50), (int) (s * 0.68), (int) (s * 0.50));
        g.drawLine((int) (s * 0.32), (int) (s * 0.68), (int) (s * 0.55), (int) (s * 0.68));

        g.dispose();
        return new ImageIcon(img);
    }

    /**
     * Caja de almacén / Control de Stock y Lotes.
     */
    public static Icon crearIconoCajaAlmacen(int size, Color color) {
        BufferedImage img = crearImagenBase(size);
        Graphics2D g = configG2(img);
        g.setColor(color);
        double s = size;
        float stroke = (float) Math.max(1.6, s * 0.08);
        g.setStroke(new BasicStroke(stroke, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        // Cubo / Caja isométrica
        int cx = (int) (s * 0.50);
        int topY = (int) (s * 0.15);
        int midY = (int) (s * 0.42);
        int botY = (int) (s * 0.82);
        int leftX = (int) (s * 0.15);
        int rightX = (int) (s * 0.85);

        // Tapa superior
        g.drawLine(cx, topY, rightX, (int) (s * 0.28));
        g.drawLine(rightX, (int) (s * 0.28), cx, midY);
        g.drawLine(cx, midY, leftX, (int) (s * 0.28));
        g.drawLine(leftX, (int) (s * 0.28), cx, topY);

        // Aristas verticales
        g.drawLine(leftX, (int) (s * 0.28), leftX, (int) (s * 0.68));
        g.drawLine(cx, midY, cx, botY);
        g.drawLine(rightX, (int) (s * 0.28), rightX, (int) (s * 0.68));

        // Aristas inferiores
        g.drawLine(leftX, (int) (s * 0.68), cx, botY);
        g.drawLine(cx, botY, rightX, (int) (s * 0.68));

        g.dispose();
        return new ImageIcon(img);
    }

    /**
     * Camión de distribución / Proveedores y Órdenes de Compra.
     */
    public static Icon crearIconoCamionProveedor(int size, Color color) {
        BufferedImage img = crearImagenBase(size);
        Graphics2D g = configG2(img);
        g.setColor(color);
        double s = size;
        float stroke = (float) Math.max(1.6, s * 0.08);
        g.setStroke(new BasicStroke(stroke, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        // Furgón / Caja de carga
        g.drawRoundRect((int) (s * 0.12), (int) (s * 0.25), (int) (s * 0.50), (int) (s * 0.45), 4, 4);

        // Cabina
        g.drawLine((int) (s * 0.62), (int) (s * 0.40), (int) (s * 0.78), (int) (s * 0.40));
        g.drawLine((int) (s * 0.78), (int) (s * 0.40), (int) (s * 0.88), (int) (s * 0.54));
        g.drawLine((int) (s * 0.88), (int) (s * 0.54), (int) (s * 0.88), (int) (s * 0.70));
        g.drawLine((int) (s * 0.88), (int) (s * 0.70), (int) (s * 0.62), (int) (s * 0.70));

        // Ventanilla cabina
        g.drawRect((int) (s * 0.66), (int) (s * 0.44), (int) (s * 0.14), (int) (s * 0.12));

        // Ruedas
        g.drawOval((int) (s * 0.24), (int) (s * 0.64), (int) (s * 0.18), (int) (s * 0.18));
        g.drawOval((int) (s * 0.70), (int) (s * 0.64), (int) (s * 0.18), (int) (s * 0.18));

        g.dispose();
        return new ImageIcon(img);
    }

    /**
     * Triángulo de advertencia / Mermas y Ajustes.
     */
    public static Icon crearIconoAlertaMerma(int size, Color color) {
        BufferedImage img = crearImagenBase(size);
        Graphics2D g = configG2(img);
        g.setColor(color);
        double s = size;
        float stroke = (float) Math.max(1.6, s * 0.08);
        g.setStroke(new BasicStroke(stroke, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        // Triángulo de advertencia
        int[] x = {(int) (s * 0.50), (int) (s * 0.12), (int) (s * 0.88)};
        int[] y = {(int) (s * 0.15), (int) (s * 0.82), (int) (s * 0.82)};
        g.drawPolygon(x, y, 3);

        // Signo de exclamación
        g.drawLine((int) (s * 0.50), (int) (s * 0.36), (int) (s * 0.50), (int) (s * 0.58));
        g.fillOval((int) (s * 0.46), (int) (s * 0.67), (int) (s * 0.08), (int) (s * 0.08));

        g.dispose();
        return new ImageIcon(img);
    }

    /**
     * Lápiz / Edición de registros.
     */
    public static Icon crearIconoLapiz(int size, Color color) {
        BufferedImage img = crearImagenBase(size);
        Graphics2D g = configG2(img);
        g.setColor(color);
        double s = size;
        float stroke = (float) Math.max(1.6, s * 0.08);
        g.setStroke(new BasicStroke(stroke, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        // Rotar 45 grados para orientar el lápiz
        g.rotate(Math.toRadians(45), s * 0.5, s * 0.5);

        // Cuerpo rectangular del lápiz
        int w = (int) (s * 0.22);
        int h = (int) (s * 0.50);
        int x = (int) (s * 0.39);
        int y = (int) (s * 0.20);
        g.drawRect(x, y, w, h);

        // Punta triangular
        int[] tx = {x, x + w / 2, x + w};
        int[] ty = {y, (int) (s * 0.06), y};
        g.drawPolygon(tx, ty, 3);

        // Borrador arriba/atrás
        g.drawLine(x, y + h - (int) (s * 0.08), x + w, y + h - (int) (s * 0.08));

        g.dispose();
        return new ImageIcon(img);
    }

    private static BufferedImage crearImagenBase(int size) {
        return new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
    }

    private static Graphics2D configG2(BufferedImage img) {
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        return g;
    }
}
