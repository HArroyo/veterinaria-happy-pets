package gui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class Seccion4 extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField textField;
	private JTable table;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Seccion4 frame = new Seccion4();
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
	public Seccion4() {
		setTitle("Sala de espera y triaje");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 650, 450);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("PACIENTES EN SALAS DE ESPERA (ORDEN DE TRIAJE)");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 20));
		lblNewLabel.setToolTipText("(");
		lblNewLabel.setBounds(10, 11, 490, 25);
		contentPane.add(lblNewLabel);
		
		textField = new JTextField();
		textField.setBounds(10, 47, 200, 20);
		contentPane.add(textField);
		textField.setColumns(10);
		
		table = new JTable();
		table.setModel(new DefaultTableModel(
			    new Object[][] {},
			    new String[] {
			        "H. Llegada", "Mascota", "Dueño", "Motivo Cita", "Estado"
			    }
			) {
			    boolean[] columnEditables = new boolean[] {
			        false, false, true, true, true
			    };
			    public boolean isCellEditable(int row, int column) {
			        return columnEditables[column];
			    }
			});
			table.getColumnModel().getColumn(0).setResizable(false);
			table.getColumnModel().getColumn(1).setResizable(false);
			JScrollPane scrollPane = new JScrollPane(table);
			scrollPane.setBounds(10, 78, 614, 340);
			contentPane.add(scrollPane);
	}
}
