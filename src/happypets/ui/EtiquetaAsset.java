package happypets.ui;

import javax.swing.JLabel;

/** Etiqueta que sustituye símbolos decorativos por iconos independientes de fuentes. */
public class EtiquetaAsset extends JLabel {
    private static final long serialVersionUID = 1L;
    public EtiquetaAsset(String texto) { super(texto); }
    public EtiquetaAsset(String texto, int alineacion) { super(texto, alineacion); }

    @Override
    public void setText(String texto) {
        super.setText(Iconos.textoSinEmoji(texto));
        if (texto != null && !texto.equals(Iconos.textoSinEmoji(texto))) {
            setIcon(Iconos.paraTexto(texto, 18, Ui.TURQUESA_OSCURO));
            setIconTextGap(6);
        }
    }
}
