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
        super.setForeground(color);
        if (automaticIcon != null && getIcon() == automaticIcon && getText() != null && !getText().isBlank()) {
            automaticIcon = Iconos.paraTexto(getText(), 16, color);
            setIcon(automaticIcon);
        }
    }
}
