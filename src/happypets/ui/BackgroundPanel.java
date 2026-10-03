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
	        if (url != null) {
	            backgroundImage = new ImageIcon(url).getImage();
	        } else {
	            throw new IllegalArgumentException("No se encontró el recurso: " + resourcePath);
	        }
	        setOpaque(false);
	    }

	    @Override
	    protected void paintComponent(Graphics g) {
	        super.paintComponent(g);

	        if (backgroundImage != null) {
	            Graphics2D g2 = (Graphics2D) g.create();
	            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
	            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

	            int panelWidth = getWidth();
	            int panelHeight = getHeight();

	            int imgWidth = backgroundImage.getWidth(this);
	            int imgHeight = backgroundImage.getHeight(this);

	            double scaleX = (double) panelWidth / imgWidth;
	            double scaleY = (double) panelHeight / imgHeight;
	            double scale = Math.max(scaleX, scaleY);

	            int drawWidth = (int) (imgWidth * scale);
	            int drawHeight = (int) (imgHeight * scale);

	            int x = (panelWidth - drawWidth) / 2;
	            int y = (panelHeight - drawHeight) / 2;

	            g2.drawImage(backgroundImage, x, y, drawWidth, drawHeight, this);
	            g2.dispose();
	        }
	    }
}
