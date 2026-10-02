package gui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JSpinner;
import javax.swing.JScrollBar;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;

public class Seccion1 extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField txtPropietario;
	private JTextField txtNombreMascota;
	private JTextField txtDNI;
	private JTextField txtEspecie;
	private JTextField txtCalendario;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Seccion1 frame = new Seccion1();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Seccion1() {
		setTitle("Registrro de Citas");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Propietario:");
		lblNewLabel.setBounds(10, 11, 60, 14);
		contentPane.add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("Mascota:");
		lblNewLabel_1.setBounds(10, 36, 46, 14);
		contentPane.add(lblNewLabel_1);
		
		txtPropietario = new JTextField();
		txtPropietario.setBounds(77, 8, 86, 20);
		contentPane.add(txtPropietario);
		txtPropietario.setColumns(10);
		
		txtNombreMascota = new JTextField();
		txtNombreMascota.setBounds(77, 33, 86, 20);
		contentPane.add(txtNombreMascota);
		txtNombreMascota.setColumns(10);
		
		JLabel lblNewLabel_2 = new JLabel("DNI:");
		lblNewLabel_2.setBounds(173, 11, 46, 14);
		contentPane.add(lblNewLabel_2);
		
		JLabel lblNewLabel_2_1 = new JLabel("Especie:");
		lblNewLabel_2_1.setBounds(173, 36, 46, 14);
		contentPane.add(lblNewLabel_2_1);
		
		txtDNI = new JTextField();
		txtDNI.setColumns(10);
		txtDNI.setBounds(221, 8, 86, 20);
		contentPane.add(txtDNI);
		
		txtEspecie = new JTextField();
		txtEspecie.setColumns(10);
		txtEspecie.setBounds(221, 33, 86, 20);
		contentPane.add(txtEspecie);
		
		JButton btnNewButton = new JButton("Registrar");
		btnNewButton.setBounds(335, 7, 89, 23);
		contentPane.add(btnNewButton);
		
		txtCalendario = new JTextField();
		txtCalendario.setText("FECHA");
		txtCalendario.setBounds(338, 33, 86, 20);
		contentPane.add(txtCalendario);
		txtCalendario.setColumns(10);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 61, 414, 189);
		contentPane.add(scrollPane);
		
		JTextArea textArea = new JTextArea();
		scrollPane.setViewportView(textArea);

	}
}
