package happypets;

import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.plaf.basic.BasicButtonUI;

import happypets.ui.ClientesMascotasFrame;
import happypets.ui.ConstanciasCertificadosFrame;
import happypets.ui.HistorialClinicoFrame;
import happypets.ui.Ui;

/** Ventana principal con accesos independientes a los tres módulos. */
public class HappyPetsApp extends JFrame {
    private static final long serialVersionUID = 1L;

    public HappyPetsApp() {
        setTitle("Happy Pets - Módulos");
        setIconImage(Ui.icono());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 560, 390);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel contentPane = new JPanel();
        contentPane.setBackground(new Color(240, 240, 240));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JPanel header = new JPanel();
        header.setBounds(0, 0, 544, 80);
        header.setBackground(new Color(0, 188, 212));
        header.setLayout(null);
        contentPane.add(header);

        JLabel title = new JLabel("Happy Pets", SwingConstants.CENTER);
        title.setBounds(0, 15, 544, 50);
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        title.setForeground(Color.WHITE);
        header.add(title);

        JButton clientes = new JButton("Clientes y Mascotas");
        clientes.setBounds(44, 108, 456, 58);
        clientes.setUI(new BasicButtonUI());
        clientes.setBackground(new Color(0, 188, 212));
        clientes.setForeground(Color.WHITE);
        clientes.setFont(new Font("SansSerif", Font.BOLD, 13));
        clientes.setBorder(BorderFactory.createLineBorder(new Color(65, 65, 65)));
        clientes.addActionListener(e -> new ClientesMascotasFrame().setVisible(true));
        contentPane.add(clientes);

        JButton historial = new JButton("Historial Clínico");
        historial.setBounds(44, 188, 456, 58);
        historial.setUI(new BasicButtonUI());
        historial.setBackground(new Color(0, 188, 212));
        historial.setForeground(Color.WHITE);
        historial.setFont(new Font("SansSerif", Font.BOLD, 13));
        historial.setBorder(BorderFactory.createLineBorder(new Color(65, 65, 65)));
        historial.addActionListener(e -> new HistorialClinicoFrame().setVisible(true));
        contentPane.add(historial);

        JButton documentos = new JButton("Constancias y Certificados");
        documentos.setBounds(44, 268, 456, 58);
        documentos.setUI(new BasicButtonUI());
        documentos.setBackground(new Color(0, 188, 212));
        documentos.setForeground(Color.WHITE);
        documentos.setFont(new Font("SansSerif", Font.BOLD, 13));
        documentos.setBorder(BorderFactory.createLineBorder(new Color(65, 65, 65)));
        documentos.addActionListener(e -> new ConstanciasCertificadosFrame().setVisible(true));
        contentPane.add(documentos);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Ui.instalarApariencia();
            new HappyPetsApp().setVisible(true);
        });
    }
}
