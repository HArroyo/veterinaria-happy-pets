package gui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.JTextArea;
import javax.swing.JRadioButton;
import javax.swing.JButton;

public class Seccion3 extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField textField_1;
	private JTextField textField_2;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Seccion3 frame = new Seccion3();
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
	public Seccion3() {
		setTitle("Recordatorios");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 300, 365);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("CREAR RECORDATORIO");
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 20));
		lblNewLabel.setBounds(10, 11, 250, 25);
		contentPane.add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("Propietario:");
		lblNewLabel_1.setBounds(10, 47, 65, 14);
		contentPane.add(lblNewLabel_1);
		
		JLabel lblNewLabel_1_1 = new JLabel("Mascota:");
		lblNewLabel_1_1.setBounds(10, 72, 65, 14);
		contentPane.add(lblNewLabel_1_1);
		
		JLabel lblNewLabel_1_1_1 = new JLabel("Fecha:");
		lblNewLabel_1_1_1.setBounds(10, 97, 65, 14);
		contentPane.add(lblNewLabel_1_1_1);
		
		JLabel lblNewLabel_1_1_1_1 = new JLabel("Mensaje:");
		lblNewLabel_1_1_1_1.setBounds(10, 122, 65, 14);
		contentPane.add(lblNewLabel_1_1_1_1);
		
		textField_1 = new JTextField();
		textField_1.setBounds(85, 69, 189, 20);
		contentPane.add(textField_1);
		textField_1.setColumns(10);
		
		textField_2 = new JTextField();
		textField_2.setBounds(85, 94, 189, 20);
		contentPane.add(textField_2);
		textField_2.setColumns(10);
		
		JComboBox comboBox = new JComboBox();
		comboBox.setBounds(85, 43, 189, 22);
		contentPane.add(comboBox);
		
		JTextArea textArea = new JTextArea();
		textArea.setBounds(85, 117, 189, 100);
		contentPane.add(textArea);
		
		JLabel lblNewLabel_1_1_1_1_1 = new JLabel("Medio:");
		lblNewLabel_1_1_1_1_1.setBounds(10, 238, 65, 14);
		contentPane.add(lblNewLabel_1_1_1_1_1);
		
		JRadioButton rdbtnNewRadioButton = new JRadioButton("SMS");
		rdbtnNewRadioButton.setToolTipText("1\r\n2\r\n3\r\n4");
		rdbtnNewRadioButton.setBounds(85, 234, 45, 23);
		contentPane.add(rdbtnNewRadioButton);
		
		JRadioButton rdbtnNewRadioButton_1 = new JRadioButton("WhatsApp");
		rdbtnNewRadioButton_1.setToolTipText("1\r\n2\r\n3\r\n4");
		rdbtnNewRadioButton_1.setBounds(132, 234, 75, 23);
		contentPane.add(rdbtnNewRadioButton_1);
		
		JRadioButton rdbtnNewRadioButton_1_1 = new JRadioButton("E-Mail");
		rdbtnNewRadioButton_1_1.setToolTipText("1\r\n2\r\n3\r\n4");
		rdbtnNewRadioButton_1_1.setBounds(209, 234, 65, 23);
		contentPane.add(rdbtnNewRadioButton_1_1);
		
		JButton btnNewButton = new JButton("Guardar");
		btnNewButton.setBounds(25, 296, 89, 23);
		contentPane.add(btnNewButton);
		
		JButton btnBorrar = new JButton("Borrar");
		btnBorrar.setBounds(171, 296, 89, 23);
		contentPane.add(btnBorrar);

	}
}
