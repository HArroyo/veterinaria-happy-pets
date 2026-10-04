package happypets.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.util.Optional;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import happypets.auth.ServicioAutenticacion;
import happypets.model.Usuario;

/**
 * Pantalla de inicio de sesión con diseño moderno y estética veterinaria.
 * Proporciona acceso mediante las credenciales por defecto (admin / admin).
 */
public class LoginFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    private JTextField txtUsuario;
    private JPasswordField txtPassword;
    private JLabel lblError;
    private final ServicioAutenticacion auth = ServicioAutenticacion.getInstancia();

    public LoginFrame() {
        setTitle("Happy Pets - Iniciar Sesión");
        setIconImage(Ui.icono());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1150, 720);
        setMinimumSize(new Dimension(880, 560));
        setLocationRelativeTo(null);
        setResizable(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        JPanel panelPrincipal = new JPanel(new GridLayout(1, 2));
        panelPrincipal.add(crearPanelBannerVeterinario());
        panelPrincipal.add(crearPanelFormularioLogin());

        setContentPane(panelPrincipal);
    }

    /**
     * Panel izquierdo: Banner visual veterinario con degradado e identidad Happy Pets.
     */
    private JPanel crearPanelBannerVeterinario() {
        JPanel banner = new JPanel() {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Degradado turquesa veterinario profundo
                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(0, 150, 169),
                        getWidth(), getHeight(), new Color(0, 96, 100)
                );
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());

                // Huellas decorativas de fondo con baja opacidad
                g2.setColor(new Color(255, 255, 255, 18));
                dibujarHuella(g2, 60, 80, 50);
                dibujarHuella(g2, 320, 180, 70);
                dibujarHuella(g2, 80, 390, 60);
                dibujarHuella(g2, 340, 440, 80);

                g2.dispose();
            }

            private void dibujarHuella(Graphics2D g2, int x, int y, int size) {
                int pad = size / 2;
                g2.fillOval(x + pad / 4, y + pad / 2, pad, (int)(pad * 0.8));
                int toe = pad / 3;
                g2.fillOval(x + pad / 8, y + pad / 4, toe, toe);
                g2.fillOval(x + pad / 2 - toe / 2, y + pad / 8, toe, toe);
                g2.fillOval(x + pad - pad / 3, y + pad / 8, toe, toe);
                g2.fillOval(x + pad, y + pad / 4, toe, toe);
            }
        };

        banner.setLayout(new BorderLayout());
        banner.setBorder(new EmptyBorder(40, 40, 40, 40));

        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);

        JPanel contenido = new JPanel();
        contenido.setOpaque(false);
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));

        // Insignia HP
        JLabel badgeHp = new JLabel("HP", SwingConstants.CENTER) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        badgeHp.setPreferredSize(new Dimension(54, 54));
        badgeHp.setMaximumSize(new Dimension(54, 54));
        badgeHp.setForeground(new Color(0, 150, 169));
        badgeHp.setFont(new Font("Segoe UI", Font.BOLD, 22));
        badgeHp.setAlignmentX(LEFT_ALIGNMENT);

        JLabel lblMarca = new JLabel("Happy Pets");
        lblMarca.setFont(new Font("Segoe UI", Font.BOLD, 30));
        lblMarca.setForeground(Color.WHITE);
        lblMarca.setAlignmentX(LEFT_ALIGNMENT);
        lblMarca.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JLabel lblTag = new JLabel("Sistema de Gestión Veterinaria");
        lblTag.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblTag.setForeground(new Color(225, 245, 250));
        lblTag.setAlignmentX(LEFT_ALIGNMENT);
        lblTag.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));

        contenido.add(badgeHp);
        contenido.add(Box.createVerticalStrut(14));
        contenido.add(lblMarca);
        contenido.add(Box.createVerticalStrut(4));
        contenido.add(lblTag);
        contenido.add(Box.createVerticalStrut(28));

        // Puntos clave de valor agrupados con espaciado consistente
        contenido.add(crearItemCaracteristica("Control Integral de Clientes y Pacientes", Iconos.crearIconoHuella(18, Color.WHITE)));
        contenido.add(Box.createVerticalStrut(14));
        contenido.add(crearItemCaracteristica("Historial Clínico Digital y Diagnósticos", Iconos.crearIconoEstetoscopio(18, Color.WHITE)));
        contenido.add(Box.createVerticalStrut(14));
        contenido.add(crearItemCaracteristica("Certificados de Vacunación y Descargas", Iconos.crearIconoCertificado(18, Color.WHITE)));
        contenido.add(Box.createVerticalStrut(14));
        contenido.add(crearItemCaracteristica("Acceso Seguro y Roles de Personal", Iconos.crearIconoCandado(18, Color.WHITE)));

        wrapper.add(contenido);
        banner.add(wrapper, BorderLayout.CENTER);

        JLabel lblVersion = new JLabel("Happy Pets ERP · Versión 1.0 Oficial (2026)");
        lblVersion.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblVersion.setForeground(new Color(200, 230, 235));
        banner.add(lblVersion, BorderLayout.SOUTH);

        return banner;
    }

    private JPanel crearItemCaracteristica(String texto, javax.swing.Icon icono) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        p.setOpaque(false);
        p.setAlignmentX(LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        p.setPreferredSize(new Dimension(380, 28));

        JLabel lblIcono = new JLabel(icono);
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lbl.setForeground(new Color(240, 255, 255));

        p.add(lblIcono);
        p.add(lbl);
        return p;
    }

    /**
     * Panel derecho: Formulario de inicio de sesión con inputs estilizados y ayuda por defecto.
     */
    private JPanel crearPanelFormularioLogin() {
        JPanel form = new JPanel(new BorderLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(new EmptyBorder(40, 48, 40, 48));

        JPanel cuerpo = new JPanel();
        cuerpo.setOpaque(false);
        cuerpo.setLayout(new BoxLayout(cuerpo, BoxLayout.Y_AXIS));

        JLabel lblBienvenido = new JLabel("Iniciar Sesión", SwingConstants.LEFT);
        lblBienvenido.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblBienvenido.setForeground(Ui.TEXTO_TITULO);
        lblBienvenido.setHorizontalAlignment(SwingConstants.LEFT);
        lblBienvenido.setAlignmentX(LEFT_ALIGNMENT);

        JLabel lblSub = new JLabel("Ingresa con tu cuenta para acceder a la clínica", SwingConstants.LEFT);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(Ui.TEXTO_MUTED);
        lblSub.setHorizontalAlignment(SwingConstants.LEFT);
        lblSub.setAlignmentX(LEFT_ALIGNMENT);

        cuerpo.add(lblBienvenido);
        cuerpo.add(Box.createVerticalStrut(4));
        cuerpo.add(lblSub);
        cuerpo.add(Box.createVerticalStrut(24));

        // Caja de credenciales por defecto (usuario y clave admin)
        JPanel boxCredenciales = new JPanel(new BorderLayout(8, 0));
        boxCredenciales.setBackground(new Color(236, 250, 252));
        boxCredenciales.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(178, 235, 242), 1),
                new EmptyBorder(8, 12, 8, 12)
        ));
        JLabel lblInfoIcon = new JLabel(" Acceso predeterminado:  Usuario: admin  |  Clave: admin");
        lblInfoIcon.setIcon(Iconos.crearIconoInfo(16, new Color(0, 105, 115)));
        lblInfoIcon.setIconTextGap(6);
        lblInfoIcon.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblInfoIcon.setForeground(new Color(0, 105, 115));
        boxCredenciales.add(lblInfoIcon, BorderLayout.CENTER);
        boxCredenciales.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        boxCredenciales.setAlignmentX(LEFT_ALIGNMENT);
        cuerpo.add(boxCredenciales);
        cuerpo.add(Box.createVerticalStrut(20));

        // Campo Usuario
        JLabel lblUsu = new JLabel("Usuario o Correo", SwingConstants.LEFT);
        lblUsu.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblUsu.setForeground(new Color(60, 65, 70));
        lblUsu.setHorizontalAlignment(SwingConstants.LEFT);
        lblUsu.setAlignmentX(LEFT_ALIGNMENT);
        cuerpo.add(lblUsu);
        cuerpo.add(Box.createVerticalStrut(6));

        txtUsuario = new JTextField("admin");
        estilizarCampo(txtUsuario);
        txtUsuario.setAlignmentX(LEFT_ALIGNMENT);
        cuerpo.add(txtUsuario);
        cuerpo.add(Box.createVerticalStrut(14));

        // Campo Contraseña
        JLabel lblPass = new JLabel("Contraseña", SwingConstants.LEFT);
        lblPass.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblPass.setForeground(new Color(60, 65, 70));
        lblPass.setHorizontalAlignment(SwingConstants.LEFT);
        lblPass.setAlignmentX(LEFT_ALIGNMENT);
        cuerpo.add(lblPass);
        cuerpo.add(Box.createVerticalStrut(6));

        txtPassword = new JPasswordField("admin");
        estilizarCampo(txtPassword);
        txtPassword.setAlignmentX(LEFT_ALIGNMENT);
        cuerpo.add(txtPassword);
        cuerpo.add(Box.createVerticalStrut(8));

        // Checkbox Mostrar contraseña
        JCheckBox chkMostrar = new JCheckBox("Mostrar contraseña");
        chkMostrar.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        chkMostrar.setForeground(Ui.TEXTO_MUTED);
        chkMostrar.setOpaque(false);
        chkMostrar.setFocusPainted(false);
        chkMostrar.addActionListener(e -> {
            if (chkMostrar.isSelected()) {
                txtPassword.setEchoChar((char) 0);
            } else {
                txtPassword.setEchoChar('•');
            }
        });
        chkMostrar.setAlignmentX(LEFT_ALIGNMENT);
        cuerpo.add(chkMostrar);
        cuerpo.add(Box.createVerticalStrut(14));

        // Mensaje de Error
        lblError = new JLabel(" ");
        lblError.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblError.setForeground(new Color(220, 53, 69));
        lblError.setAlignmentX(LEFT_ALIGNMENT);
        cuerpo.add(lblError);
        cuerpo.add(Box.createVerticalStrut(10));

        // Botón de Ingreso
        JButton btnIngresar = new JButton("INGRESAR AL SISTEMA") {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color fondo = getModel().isPressed() ? Ui.TURQUESA_OSCURO : (getModel().isRollover() ? new Color(0, 172, 193) : Ui.TURQUESA);
                g2.setColor(fondo);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        btnIngresar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnIngresar.setForeground(Color.WHITE);
        btnIngresar.setFocusPainted(false);
        btnIngresar.setContentAreaFilled(false);
        btnIngresar.setOpaque(false);
        btnIngresar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnIngresar.setPreferredSize(new Dimension(Integer.MAX_VALUE, 42));
        btnIngresar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btnIngresar.setBorder(new EmptyBorder(8, 14, 8, 14));
        btnIngresar.setAlignmentX(LEFT_ALIGNMENT);

        btnIngresar.addActionListener(e -> intentarLogin());
        txtUsuario.addActionListener(e -> intentarLogin());
        txtPassword.addActionListener(e -> intentarLogin());

        cuerpo.add(btnIngresar);

        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);
        wrapper.add(cuerpo);

        form.add(wrapper, BorderLayout.CENTER);

        // Pie
        JLabel footer = new JLabel("Happy Pets © 2026 · Todos los derechos reservados", SwingConstants.CENTER);
        footer.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        footer.setForeground(new Color(150, 155, 160));
        form.add(footer, BorderLayout.SOUTH);

        return form;
    }

    private void estilizarCampo(JTextField campo) {
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        campo.setPreferredSize(new Dimension(Integer.MAX_VALUE, 36));
        campo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(190, 195, 200), 1),
                new EmptyBorder(6, 10, 6, 10)
        ));
    }

    private void intentarLogin() {
        String u = txtUsuario.getText().trim();
        String p = new String(txtPassword.getPassword()).trim();

        if (u.isEmpty() || p.isEmpty()) {
            lblError.setText("⚠️ Ingrese usuario y contraseña.");
            return;
        }

        Optional<Usuario> usuario = auth.autenticar(u, p);
        if (usuario.isPresent()) {
            lblError.setText(" ");
            dispose(); // Cierra el login
            SwingUtilities.invokeLater(() -> {
                // Abre la pantalla principal moderna
                PantallaPrincipalFrame principal = new PantallaPrincipalFrame();
                principal.setVisible(true);
            });
        } else {
            lblError.setText("❌ Credenciales incorrectas. Verifique usuario y clave.");
            txtPassword.setText("");
            txtPassword.requestFocus();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Ui.instalarApariencia();
            new LoginFrame().setVisible(true);
        });
    }
}
