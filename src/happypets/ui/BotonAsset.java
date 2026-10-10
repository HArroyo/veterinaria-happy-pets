package happypets.ui;

import javax.swing.Icon;
import javax.swing.JButton;

/** Botón con icono Java2D que conserva los listeners y el pintado de cada vista. */
public class BotonAsset extends JButton {
    private static final long serialVersionUID = 1L;
    private Icon automaticIcon;
    public BotonAsset() { super(); }
    public BotonAsset(String texto) { super(texto); }
    public BotonAsset(Icon icono) { super(icono); }
    public BotonAsset(String texto, Icon icono) { super(texto, icono); }

    @Override
    public void updateUI() {
        // El tema de Windows ignora el fondo personalizado y deja texto blanco sobre blanco.
        setUI(new javax.swing.plaf.basic.BasicButtonUI());
    }

    @Override
    public java.awt.Dimension getPreferredSize() {
        java.awt.Dimension size = super.getPreferredSize();
        if (getFont() == null) return size;
        java.awt.Insets padding = getInsets();
        int anchoIcono = getIcon() == null ? 0 : getIcon().getIconWidth();
        int anchoTexto = getText() == null || getText().startsWith("<html>") ? 0
                : getFontMetrics(getFont()).stringWidth(getText());
        int gap = anchoIcono > 0 && anchoTexto > 0 ? getIconTextGap() : 0;
        return new java.awt.Dimension(Math.max(size.width, anchoTexto + anchoIcono + gap + padding.left + padding.right),
                Math.max(size.height, Math.max(24, getFontMetrics(getFont()).getHeight() + padding.top + padding.bottom)));
    }

    @Override
    public void setText(String texto) {
        super.setText(Iconos.textoSinEmoji(texto));
        if (texto != null && !texto.isBlank()) {
            automaticIcon = Iconos.paraTexto(texto, 16, Ui.TURQUESA_OSCURO);
            setIcon(automaticIcon);
            setIconTextGap(6);
        }
    }

    @Override
    public void setForeground(java.awt.Color color) {
        if (java.util.Objects.equals(color, getForeground())) return;
        super.setForeground(color);
        if (automaticIcon != null && getIcon() == automaticIcon && getText() != null && !getText().isBlank()) {
            automaticIcon = Iconos.paraTexto(getText(), 16, color);
            setIcon(automaticIcon);
        }
    }
}
