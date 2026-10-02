package happypets;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import happypets.data.RepositorioVeterinaria;
import happypets.ui.ClientesMascotasFrame;
import happypets.ui.ConstanciasCertificadosFrame;
import happypets.ui.HistorialClinicoFrame;
import happypets.ui.Ui;

/**
 * Punto de entrada principal del sistema de gestión veterinaria Happy Pets.
 * Permite acceder de forma centralizada e intuitiva a los módulos del sistema.
 */
public class HappyPetsApp extends JFrame {
    private static final long serialVersionUID = 1L;

    public HappyPetsApp() {
        setTitle("Sistema de Gestión Veterinaria - Happy Pets");
        setIconImage(Ui.icono());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(780, 520);
        setMinimumSize(new Dimension(680, 480));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Cabecera institucional
        add(Ui.crearCabeceraSistema(), BorderLayout.NORTH);

        // Contenedor Central
        JPanel contenedor = new JPanel(new BorderLayout(0, 18));
        contenedor.setBackground(Ui.FONDO);
        contenedor.setBorder(new EmptyBorder(22, 28, 22, 28));

        // Bienvenida
        JPanel bienvenida = new JPanel();
        bienvenida.setOpaque(false);
        bienvenida.setLayout(new BoxLayout(bienvenida, BoxLayout.Y_AXIS));

        JLabel lblBienvenida = new JLabel("Bienvenido al Panel de Gestión Veterinaria");
        lblBienvenida.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblBienvenida.setForeground(Ui.TEXTO_TITULO);

        JLabel lblSub = new JLabel("Seleccione el módulo que desea consultar o administrar:");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(Ui.TEXTO_MUTED);

        bienvenida.add(lblBienvenida);
        bienvenida.add(Box.createVerticalStrut(4));
        bienvenida.add(lblSub);
        contenedor.add(bienvenida, BorderLayout.NORTH);

        // Grid de los 3 Módulos
        JPanel gridModulos = new JPanel(new GridLayout(1, 3, 16, 0));
        gridModulos.setOpaque(false);

        gridModulos.add(crearBotonModulo(
                "Módulo 1.1",
                "Clientes y Mascotas",
                "Ficha de clientes, registro de mascotas vinculadas y datos de contacto.",
                e -> new ClientesMascotasFrame().setVisible(true)
        ));

        gridModulos.add(crearBotonModulo(
                "Módulo 1.2",
                "Historial Clínico",
                "Visitas de evolución, diagnósticos, recetas, vacunas y detalles clínicos.",
                e -> new HistorialClinicoFrame().setVisible(true)
        ));

        gridModulos.add(crearBotonModulo(
                "Módulo 1.3",
                "Constancias y Certificados",
                "Tarjetas de vacunación, recetas emitidas y certificados veterinarios.",
                e -> new ConstanciasCertificadosFrame().setVisible(true)
        ));

        contenedor.add(gridModulos, BorderLayout.CENTER);

        // Pie informativo con estadísticas del repositorio
        RepositorioVeterinaria repo = RepositorioVeterinaria.getInstancia();
        int clientes = repo.getClientes().size();
        int mascotas = repo.todasLasMascotas().size();

        JPanel pieInfo = new JPanel(new BorderLayout());
        pieInfo.setBackground(Color.WHITE);
        pieInfo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Ui.BORDE_SUAVE, 1),
                new EmptyBorder(10, 16, 10, 16)
        ));

        JLabel infoRepo = new JLabel("Base de datos activa · " + clientes + " clientes registrados · " + mascotas + " mascotas asociadas");
        infoRepo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        infoRepo.setForeground(new Color(90, 95, 100));
        pieInfo.add(infoRepo, BorderLayout.WEST);

        JLabel version = new JLabel("Happy Pets v1.0");
        version.setFont(new Font("Segoe UI", Font.BOLD, 12));
        version.setForeground(Ui.TURQUESA_OSCURO);
        pieInfo.add(version, BorderLayout.EAST);

        contenedor.add(pieInfo, BorderLayout.SOUTH);

        add(contenedor, BorderLayout.CENTER);
    }

    private JPanel crearBotonModulo(String codigo, String titulo, String descripcion, java.awt.event.ActionListener accion) {
        JPanel card = new JPanel(new BorderLayout(0, 10)) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Ui.BORDE_SUAVE, 1),
                new EmptyBorder(16, 16, 16, 16)
        ));

        // Cabecera de la tarjeta
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JLabel lblCodigo = new JLabel(codigo);
        lblCodigo.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblCodigo.setForeground(Ui.TURQUESA_OSCURO);
        top.add(lblCodigo, BorderLayout.NORTH);

        JLabel lblTit = new JLabel("<html><b>" + titulo + "</b></html>");
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTit.setForeground(new Color(30, 30, 30));
        top.add(lblTit, BorderLayout.CENTER);
        card.add(top, BorderLayout.NORTH);

        // Descripción
        JLabel lblDesc = new JLabel("<html><p style='color:#6c757d; line-height:1.3;'>" + descripcion + "</p></html>");
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblDesc.setVerticalAlignment(SwingConstants.TOP);
        card.add(lblDesc, BorderLayout.CENTER);

        // Botón de ingreso
        JButton btnAbrir = Ui.boton("Abrir Módulo", true);
        btnAbrir.setPreferredSize(new Dimension(140, 34));
        btnAbrir.addActionListener(accion);
        card.add(btnAbrir, BorderLayout.SOUTH);

        return card;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Ui.instalarApariencia();
            new HappyPetsApp().setVisible(true);
        });
    }
}
