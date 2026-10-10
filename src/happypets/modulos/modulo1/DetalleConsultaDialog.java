package happypets.modulos.modulo1;

import happypets.ui.Ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import happypets.model.ConsultaClinica;
import happypets.model.Mascota;

/**
 * Diálogo modal para visualizar el detalle completo de una consulta clínica veterinaria.
 */
public class DetalleConsultaDialog extends JDialog {
    private static final long serialVersionUID = 1L;

    public DetalleConsultaDialog(JFrame parent, ConsultaClinica consulta, Mascota mascota) {
        super(parent, "Detalle de Consulta Clínica - " + (consulta != null ? consulta.getCodigo() : ""), true);
        setSize(650, 580);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Ui.TURQUESA);
        header.setBorder(new EmptyBorder(14, 20, 14, 20));

        JLabel title = new JLabel("Consulta Clínica " + (consulta != null ? consulta.getCodigo() : ""));
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(Color.WHITE);
        header.add(title, BorderLayout.WEST);

        JLabel dateLbl = new JLabel(consulta != null ? consulta.getFechaFormateada() : "");
        dateLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        dateLbl.setForeground(Color.WHITE);
        header.add(dateLbl, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        JPanel body = new JPanel(new GridBagLayout());
        body.setBackground(Color.WHITE);
        body.setBorder(new EmptyBorder(18, 22, 18, 22));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;
        agregarFila(body, gbc, row++, "Paciente / Mascota:", mascota != null ? mascota.getNombre() + " (" + mascota.getCodigo() + ") - " + mascota.getEspecie() + " " + mascota.getRaza() : "-");
        agregarFila(body, gbc, row++, "Veterinario Tratante:", consulta != null ? consulta.getVeterinario() : "-");
        agregarFila(body, gbc, row++, "Estado de Atención:", consulta != null ? consulta.getEstado() : "-");
        agregarFila(body, gbc, row++, "Signos Vitales:", consulta != null ? "Peso: " + consulta.getPesoKg() + " kg   |   Temperatura: " + consulta.getTemperaturaC() + " °C" : "-");
        agregarFila(body, gbc, row++, "Motivo de Consulta:", consulta != null ? consulta.getMotivo() : "-");
        agregarFila(body, gbc, row++, "Síntomas Reportados:", consulta != null ? consulta.getSintomas() : "-");
        agregarFila(body, gbc, row++, "Diagnóstico Clínico:", consulta != null ? consulta.getDiagnostico() : "-");
        agregarFila(body, gbc, row++, "Tratamiento / Plan:", consulta != null ? consulta.getTratamiento() : "-");

        // Observaciones en JTextArea
        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.weightx = 0.0;
        JLabel lblObs = new JLabel("Observaciones:", SwingConstants.LEFT);
        lblObs.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblObs.setForeground(new Color(60, 60, 60));
        lblObs.setHorizontalAlignment(SwingConstants.LEFT);
        body.add(lblObs, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        JTextArea txtObs = new JTextArea(consulta != null ? consulta.getObservaciones() : "");
        txtObs.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtObs.setLineWrap(true);
        txtObs.setWrapStyleWord(true);
        txtObs.setEditable(false);
        txtObs.setBackground(new Color(248, 249, 250));
        txtObs.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Ui.BORDE_SUAVE),
                new EmptyBorder(6, 6, 6, 6)
        ));
        JScrollPane scrollObs = new JScrollPane(txtObs);
        scrollObs.setPreferredSize(new Dimension(360, 70));
        body.add(scrollObs, gbc);

        add(new JScrollPane(body), BorderLayout.CENTER);

        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(Ui.FONDO);
        footer.setBorder(new EmptyBorder(10, 20, 10, 20));

        JButton btnCerrar = Ui.boton("Cerrar", true);
        btnCerrar.addActionListener(e -> dispose());
        footer.add(btnCerrar, BorderLayout.EAST);
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
}
