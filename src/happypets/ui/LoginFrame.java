package happypets.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.util.Optional;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
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

		setTitle("Happy Pets Clínica Veterinaria · Iniciar Sesión");

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
	 * Panel izquierdo: Banner visual veterinario con degradado e identidad Happy
	 * Pets.
	 */
	private JPanel crearPanelBannerVeterinario() {

		java.net.URL fondoUrl = getClass().getResource("/happypets/assets/images/bg-login-happypets.png");

		Image imagenFondo = null;

		if (fondoUrl != null) {
			imagenFondo = new ImageIcon(fondoUrl).getImage();
		}

		final Image fondoLogin = imagenFondo;

		JPanel banner = new JPanel(new BorderLayout()) {

			private static final long serialVersionUID = 1L;

			@Override
			protected void paintComponent(Graphics g) {

				super.paintComponent(g);

				Graphics2D g2 = (Graphics2D) g.create();

				g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

				g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

				if (fondoLogin != null) {

					int panelW = getWidth();

					int panelH = getHeight();

					int imgW = fondoLogin.getWidth(this);

					int imgH = fondoLogin.getHeight(this);

					if (imgW > 0 && imgH > 0) {

						/*
						 * Equivalente a CSS:
						 *
						 * background-size: cover; background-position: center;
						 */

						double escalaX = (double) panelW / imgW;

						double escalaY = (double) panelH / imgH;

						double escala = Math.max(escalaX, escalaY);

						int ancho = (int) Math.ceil(imgW * escala);

						int alto = (int) Math.ceil(imgH * escala);

						int x = (panelW - ancho) / 2;

						int y = (panelH - alto) / 2;

						g2.drawImage(fondoLogin, x, y, ancho, alto, this);
					}

				} else {

					// Fallback por si no encuentra la imagen
					g2.setColor(new Color(235, 247, 255));

					g2.fillRect(0, 0, getWidth(), getHeight());
				}

				g2.dispose();
			}
		};

		banner.setOpaque(false);

		banner.setBorder(new EmptyBorder(40, 50, 40, 50));

		// =========================================================
		// CONTENIDO CENTRAL
		// =========================================================

		JPanel wrapper = new JPanel(new GridBagLayout());

		wrapper.setOpaque(false);

		JPanel contenido = new JPanel();

		contenido.setOpaque(false);

		contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));

		// =========================================================
		// LOGO
		// =========================================================

		ImageIcon logo = Ui.logoHorizontal(300, 120);

		JLabel lblLogo = new JLabel();

		if (logo != null) {

			lblLogo.setIcon(logo);

		} else {

			lblLogo.setText("Happy Pets");

			lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 30));

			lblLogo.setForeground(new Color(15, 67, 110));
		}

		lblLogo.setAlignmentX(LEFT_ALIGNMENT);

		lblLogo.setMaximumSize(new Dimension(320, 120));

		contenido.add(lblLogo);

		contenido.add(Box.createVerticalStrut(12));

		// =========================================================
		// SUBTÍTULO
		// =========================================================

		JLabel lblTag = new JLabel("Sistema de Gestión Veterinaria");

		lblTag.setFont(new Font("Segoe UI", Font.BOLD, 16));

		lblTag.setForeground(new Color(51, 94, 133));

		lblTag.setAlignmentX(LEFT_ALIGNMENT);

		contenido.add(lblTag);

		contenido.add(Box.createVerticalStrut(8));

		JLabel lblDescripcion = new JLabel("<html>" + "Gestión clínica moderna para el cuidado "
				+ "y seguimiento de nuestros pacientes." + "</html>");

		lblDescripcion.setFont(new Font("Segoe UI", Font.PLAIN, 13));

		lblDescripcion.setForeground(new Color(100, 116, 139));

		lblDescripcion.setAlignmentX(LEFT_ALIGNMENT);

		lblDescripcion.setMaximumSize(new Dimension(420, 50));

		contenido.add(lblDescripcion);

		contenido.add(Box.createVerticalStrut(32));

		// =========================================================
		// CARACTERÍSTICAS
		// =========================================================

		contenido.add(crearItemCaracteristica("Control Integral de Clientes y Pacientes",
				Iconos.crearIconoHuella(18, new Color(2, 132, 199))));

		contenido.add(Box.createVerticalStrut(14));

		contenido.add(crearItemCaracteristica("Historial Clínico Digital y Diagnósticos",
				Iconos.crearIconoEstetoscopio(18, new Color(14, 116, 144))));

		contenido.add(Box.createVerticalStrut(14));

		contenido.add(crearItemCaracteristica("Certificados de Vacunación y Descargas",
				Iconos.crearIconoCertificado(18, new Color(37, 99, 235))));

		contenido.add(Box.createVerticalStrut(14));

		contenido.add(crearItemCaracteristica("Acceso Seguro y Roles de Personal",
				Iconos.crearIconoCandado(18, new Color(3, 105, 161))));

		GridBagConstraints gbc = new GridBagConstraints();

		gbc.gridx = 0;
		gbc.gridy = 0;

		gbc.weightx = 1.0;
		gbc.weighty = 1.0;

		gbc.anchor = GridBagConstraints.CENTER;

		gbc.insets = new Insets(0, 0, 100, 0);

		wrapper.add(contenido, gbc);

		banner.add(wrapper, BorderLayout.CENTER);

		// =========================================================
		// VERSIÓN
		// =========================================================

		JLabel lblVersion = new JLabel("Happy Pets Clínica Veterinaria · Versión 1.0 (2026)");

		lblVersion.setFont(new Font("Segoe UI", Font.PLAIN, 11));

		lblVersion.setForeground(new Color(100, 116, 139));

		banner.add(lblVersion, BorderLayout.SOUTH);

		return banner;
	}

	private JPanel crearItemCaracteristica(String texto, javax.swing.Icon icono) {

		JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));

		p.setOpaque(false);

		p.setAlignmentX(LEFT_ALIGNMENT);

		p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

		JLabel lblIcono = new JLabel(icono);

		JLabel lbl = new JLabel(texto);

		lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));

		lbl.setForeground(new Color(51, 65, 85));

		p.add(lblIcono);
		p.add(lbl);

		return p;
	}

	/**
	 * Panel derecho: Formulario de inicio de sesión con inputs estilizados y ayuda
	 * por defecto.
	 */
	private JPanel crearPanelFormularioLogin() {
		JPanel form = new JPanel(new BorderLayout());
		form.setBackground(Color.WHITE);
		form.setBorder(new EmptyBorder(40, 48, 40, 48));

		JPanel cuerpo = new JPanel();
		cuerpo.setOpaque(false);
		cuerpo.setLayout(new BoxLayout(cuerpo, BoxLayout.Y_AXIS));

		JLabel lblBienvenido = new JLabel("Iniciar Sesión");
		lblBienvenido.setFont(new Font("Segoe UI", Font.BOLD, 26));
		lblBienvenido.setForeground(Ui.TEXTO_TITULO);

		JLabel lblSub = new JLabel("Ingresa con tu cuenta para acceder a la clínica");
		lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		lblSub.setForeground(Ui.TEXTO_MUTED);

		cuerpo.add(lblBienvenido);
		cuerpo.add(Box.createVerticalStrut(4));
		cuerpo.add(lblSub);
		cuerpo.add(Box.createVerticalStrut(24));

		// Caja de credenciales por defecto (usuario y clave admin)
		JPanel boxCredenciales = new JPanel(new BorderLayout(8, 0));
		boxCredenciales.setBackground(new Color(236, 250, 252));
		boxCredenciales.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(new Color(178, 235, 242), 1), new EmptyBorder(8, 12, 8, 12)));
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
		JLabel lblUsu = new JLabel("Usuario o Correo");
		lblUsu.setFont(new Font("Segoe UI", Font.BOLD, 12));
		lblUsu.setForeground(new Color(60, 65, 70));
		lblUsu.setAlignmentX(LEFT_ALIGNMENT);
		cuerpo.add(lblUsu);
		cuerpo.add(Box.createVerticalStrut(6));

		txtUsuario = new JTextField("admin");
		estilizarCampo(txtUsuario);
		txtUsuario.setAlignmentX(LEFT_ALIGNMENT);
		cuerpo.add(txtUsuario);
		cuerpo.add(Box.createVerticalStrut(14));

		// Campo Contraseña
		JLabel lblPass = new JLabel("Contraseña");
		lblPass.setFont(new Font("Segoe UI", Font.BOLD, 12));
		lblPass.setForeground(new Color(60, 65, 70));
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
				Color fondo = getModel().isPressed() ? Ui.TURQUESA_OSCURO
						: (getModel().isRollover() ? new Color(0, 172, 193) : Ui.TURQUESA);
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
		campo.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(190, 195, 200), 1),
				new EmptyBorder(6, 10, 6, 10)));
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
