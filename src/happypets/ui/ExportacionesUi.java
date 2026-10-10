package happypets.ui;
import java.awt.Component;
import java.util.List;
import javax.swing.*;
import happypets.data.*;

/** Exportar es opcional; los registros de la aplicación permanecen en memoria. */
public final class ExportacionesUi {
    private ExportacionesUi() {}
    public static void guardar(Component padre, List<List<String>> tabla, String formato, String nombre) {
        var cfg = RepositorioVeterinaria.getInstancia().getConfiguracionClinica();
        if ("PDF".equals(formato) && cfg != null) {
            tabla = new java.util.ArrayList<>(tabla);
            tabla.add(0, List.of(cfg.getNombreComercial(), cfg.getIdentificadorFiscal()));
            tabla.add(1, List.of(cfg.getDireccionSedePrincipal(), cfg.getTelefonoUrgencias()));
        }
        guardarBytes(padre, ExportadorTabla.generar(tabla, formato), nombre + "." + formato.toLowerCase(java.util.Locale.ROOT));
    }
    public static boolean guardarBytes(Component padre, byte[] contenido, String nombre) {
        if (contenido == null) {
            JOptionPane.showMessageDialog(padre, "Este registro de ejemplo no tiene un archivo disponible en memoria.", "Archivo no disponible", JOptionPane.INFORMATION_MESSAGE);
            return false;
        }
        JFileChooser selector = new JFileChooser();
        selector.setSelectedFile(new java.io.File(nombre));
        if (selector.showSaveDialog(padre) != JFileChooser.APPROVE_OPTION) return false;
        var destino = selector.getSelectedFile().toPath();
        if (java.nio.file.Files.exists(destino) && JOptionPane.showConfirmDialog(padre, "¿Reemplazar el archivo existente?", "Guardar", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return false;
        try {
            java.nio.file.Files.write(destino, contenido);
            JOptionPane.showMessageDialog(padre, "Archivo guardado: " + destino.toAbsolutePath());
            return true;
        } catch (java.io.IOException ex) {
            JOptionPane.showMessageDialog(padre, "No se pudo guardar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
}
