package happypets.modulos.modulo1;

import happypets.ui.Ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import happypets.model.Certificado;
import happypets.model.Cliente;
import happypets.model.DocumentoMascota;
import happypets.model.Mascota;

/**
 * Diálogo modal para visualización y descarga simulada de constancias y certificados.
 */
public class DetalleDocumentoDialog extends JDialog {
    private static final long serialVersionUID = 1L;

    public DetalleDocumentoDialog(JFrame parent, DocumentoMascota documento, Mascota mascota, Cliente cliente) {
        super(parent, "Visor de Documento Veterinario - " + (documento != null ? documento.getTipo() : ""), true);
        setSize(680, 560);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Ui.TURQUESA);
        header.setBorder(new EmptyBorder(14, 20, 14, 20));

        JLabel title = new JLabel(documento != null ? documento.getTipo() : "Documento");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(Color.WHITE);
        header.add(title, BorderLayout.WEST);

        JLabel statusLbl = new JLabel(documento != null && documento.isDisponible() ? "Documento Oficial Vigente" : "Inactivo");
        statusLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        statusLbl.setForeground(Color.WHITE);
        header.add(statusLbl, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        JPanel body = new JPanel(new GridBagLayout());
        body.setBackground(Color.WHITE);
        body.setBorder(new EmptyBorder(20, 24, 20, 24));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;
        agregarFila(body, gbc, row++, "Tipo de Documento:", documento != null ? documento.getTipo() : "-");
        agregarFila(body, gbc, row++, "Mascota / Paciente:", mascota != null ? mascota.getNombre() + " (" + mascota.getCodigo() + ") - " + mascota.getEspecie() + " " + mascota.getRaza() : "-");
        agregarFila(body, gbc, row++, "Propietario / Cliente:", cliente != null ? cliente.getNombreCompleto() + " (" + cliente.getNumeroDocumento() + ")" : "-");
        agregarFila(body, gbc, row++, "Última Actualización:", documento != null ? documento.getFechaActualizacionFormateada() : "-");
        agregarFila(body, gbc, row++, "Disponibilidad:", documento != null ? documento.getDisponibilidadTexto() : "-");

        if (documento instanceof Certificado cert) {
            agregarFila(body, gbc, row++, "N.º Certificado:", cert.getNumeroCertificado() != null ? cert.getNumeroCertificado() : "CERT-2024-HP");
            agregarFila(body, gbc, row++, "Motivo / Destino:", cert.getMotivoDestino() != null ? cert.getMotivoDestino() : "Nacional / Local");
            agregarFila(body, gbc, row++, "Estado Certificación:", cert.getEstado() != null ? cert.getEstado() : "Aprobado");
        }

        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.weightx = 0.0;
        JLabel lblDesc = new JLabel("Descripción Detallada:", SwingConstants.LEFT);
        lblDesc.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblDesc.setForeground(new Color(60, 60, 60));
        lblDesc.setHorizontalAlignment(SwingConstants.LEFT);
        body.add(lblDesc, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        JTextArea txtDesc = new JTextArea(documento != null ? documento.getDescripcion() : "");
        txtDesc.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtDesc.setLineWrap(true);
        txtDesc.setWrapStyleWord(true);
        txtDesc.setEditable(false);
        txtDesc.setBackground(new Color(248, 249, 250));
        txtDesc.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Ui.BORDE_SUAVE),
                new EmptyBorder(6, 6, 6, 6)
        ));
        JScrollPane scrollDesc = new JScrollPane(txtDesc);
        scrollDesc.setPreferredSize(new Dimension(360, 70));
        body.add(scrollDesc, gbc);

        add(new JScrollPane(body), BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 10));
        footer.setBackground(Ui.FONDO);

        JButton btnDescargar = Ui.boton("Visualizar / Exportar PDF", true);
        btnDescargar.setBackground(Ui.TURQUESA);
        btnDescargar.addActionListener(e -> {
            String tipo = documento != null ? documento.getTipo() : "Documento Veterinario";
            String[][] datos = new String[][]{
                {"1", tipo, mascota != null ? mascota.getNombre() : "N/D", cliente != null ? cliente.getNombreCompleto() : "N/D", documento != null ? documento.getFechaActualizacionFormateada() : "-"},
                {"2", "Dictamen Clínico", documento != null ? documento.getDescripcion() : "Conforme", "Dr. R. Mendoza (CMVP 8492)", "Vigente"}
            };
            Ui.mostrarVisorReporte(
                (JFrame) getParent(),
                tipo.toUpperCase(),
                "Paciente: " + (mascota != null ? mascota.getNombre() + " (" + mascota.getCodigo() + ")" : "N/D") + " | Propietario: " + (cliente != null ? cliente.getNombreCompleto() : "N/D"),
                "Fecha Emisión: " + (documento != null ? documento.getFechaActualizacionFormateada() : "-") + " | Estado: Certificación Oficial Aprobada",
                new String[]{"N°", "Concepto", "Paciente / Detalle", "Responsable", "Estado"},
                datos,
                "DOCUMENTO OFICIAL DIGITAL VETERINARIA HAPPY PETS S.A.C.",
                "Certificado_" + (mascota != null ? mascota.getNombre() : "Paciente")
            );
        });

        JButton btnCerrar = Ui.boton("Cerrar", false);
        btnCerrar.addActionListener(e -> dispose());

        footer.add(btnDescargar);
        footer.add(btnCerrar);
        add(footer, BorderLayout.SOUTH);
    }

    private void agregarFila(JPanel panel, GridBagConstraints gbc, int row, String etiqueta, String valor) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.0;
        JLabel lbl = new JLabel(etiqueta, SwingConstants.LEFT);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(new Color(60, 60, 60));
        lbl.setHorizontalAlignment(SwingConstants.LEFT);
        panel.add(lbl, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        JLabel val = new JLabel(valor != null ? valor : "-");
        val.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        val.setForeground(Color.BLACK);
        panel.add(val, gbc);
    }

    public static void descargarPdf(DocumentoMascota documento) {
        String nombreSugerido = (documento != null ? documento.getTipo().replace(" ", "_") : "Documento") + ".pdf";
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new java.io.File(nombreSugerido));
        int res = chooser.showSaveDialog(null);
        if (res == JFileChooser.APPROVE_OPTION) {
            java.io.File f = chooser.getSelectedFile();
            try {
                String[][] meta = new String[][]{
                    {"Tipo de Documento", documento != null ? documento.getTipo() : "Certificado Oficial"},
                    {"Institución", "Veterinaria Happy Pets S.A.C. · Lima Perú"},
                    {"Estado", "Vigente y Certificado"}
                };
                java.util.List<Object[]> filas = new java.util.ArrayList<>();
                filas.add(new Object[]{"1", documento != null ? documento.getTipo() : "Documento", "Oficial", "Vigente"});
                happypets.ui.GeneradorDocumentos.generarPDF(
                    documento != null ? documento.getTipo() : "Certificado",
                    "Certificado Oficial de Salud y Vacunación",
                    meta,
                    new String[]{"N°", "Tipo", "Categoría", "Estado"},
                    filas,
                    "DOCUMENTO VÁLIDO Y CERTIFICADO",
                    f
                );
                JOptionPane.showMessageDialog(null,
                        "Documento PDF oficial generado exitosamente en:\n" + f.getAbsolutePath(),
                        "Descarga Exitosa", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Error al generar archivo PDF: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
