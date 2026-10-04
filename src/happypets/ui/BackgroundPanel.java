package happypets.ui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class BackgroundPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private final Image backgroundImage;

	public BackgroundPanel(String resourcePath) {

		java.net.URL url = getClass().getResource(resourcePath);

		if (url == null) {
			throw new IllegalArgumentException("No se encontró el recurso: " + resourcePath);
		}

		backgroundImage = new ImageIcon(url).getImage();

		setOpaque(false);
	}

	@Override
	protected void paintComponent(Graphics g) {

		super.paintComponent(g);

		if (backgroundImage == null) {
			return;
		}

		int panelWidth = getWidth();
		int panelHeight = getHeight();

		int imageWidth = backgroundImage.getWidth(this);

		int imageHeight = backgroundImage.getHeight(this);

		if (imageWidth <= 0 || imageHeight <= 0) {
			return;
		}

		/*
		 * Escala tipo CSS background-size: cover.
		 *
		 * Mantiene la proporción original de la imagen y llena completamente el panel.
		 */
		double scaleX = (double) panelWidth / imageWidth;

		double scaleY = (double) panelHeight / imageHeight;

		double scale = Math.max(scaleX, scaleY);

		int scaledWidth = (int) Math.ceil(imageWidth * scale);

		int scaledHeight = (int) Math.ceil(imageHeight * scale);

		/*
		 * Centrar la imagen.
		 */
		int x = (panelWidth - scaledWidth) / 2;

		int y = (panelHeight - scaledHeight) / 2;

		Graphics2D g2 = (Graphics2D) g.create();

		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

		g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		g2.drawImage(backgroundImage, x, y, scaledWidth, scaledHeight, this);

		g2.dispose();
	}
}
