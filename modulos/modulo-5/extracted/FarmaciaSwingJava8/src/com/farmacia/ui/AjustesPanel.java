package com.farmacia.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import com.farmacia.util.UIUtils;

public class AjustesPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private final DefaultTableModel model;
	private final JTable table;
	private final JTextField txtProducto;
	private final JComboBox<String> cmbTipo;
	private final JTextField txtCantidad;
	private final JTextArea txtMotivo;

	public AjustesPanel() {
		setLayout(new BorderLayout(12, 12));
		setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

		JPanel header = new JPanel(new BorderLayout());
		header.add(UIUtils.title("Ajustes y Mermas"), BorderLayout.WEST);
		JButton btnNuevo = UIUtils.darkButton("Nuevo ajuste");
		header.add(btnNuevo, BorderLayout.EAST);
		add(header, BorderLayout.NORTH);

		model = new DefaultTableModel(new Object[] { "Fecha", "Producto", "Tipo", "Cantidad" }, 0) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		table = new JTable(model);
		table.setRowHeight(34);
		table.setFillsViewportHeight(true);
		add(new JScrollPane(table), BorderLayout.CENTER);

		JPanel bottom = new JPanel(new BorderLayout(0, 10));
		JPanel form = new JPanel(new GridBagLayout());
		GridBagConstraints g = new GridBagConstraints();
		g.insets = new Insets(5, 6, 5, 6);
		g.fill = GridBagConstraints.HORIZONTAL;
		g.weightx = 1;
		txtProducto = UIUtils.textField();
		cmbTipo = new JComboBox<String>(
				new String[] { "Seleccione...", "Merma", "Ajuste positivo", "Ajuste negativo", "Vencimiento" });
		txtCantidad = UIUtils.textField();

		addField(form, g, 0, 0, "Producto", txtProducto);
		addField(form, g, 1, 0, "Tipo", cmbTipo);
		addField(form, g, 2, 0, "Cantidad", txtCantidad);

		g.gridx = 0;
		g.gridy = 2;
		g.gridwidth = 3;
		form.add(new JLabel("Motivo"), g);
		txtMotivo = new JTextArea(4, 20);
		txtMotivo.setLineWrap(true);
		txtMotivo.setWrapStyleWord(true);
		g.gridy = 3;
		form.add(new JScrollPane(txtMotivo), g);
		bottom.add(form, BorderLayout.CENTER);

		JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
		JButton btnGuardar = UIUtils.darkButton("Guardar");
		JButton btnCancelar = UIUtils.lightButton("Cancelar");
		actions.add(btnGuardar);
		actions.add(btnCancelar);
		bottom.add(actions, BorderLayout.SOUTH);
		bottom.setPreferredSize(new Dimension(100, 250));
		add(bottom, BorderLayout.SOUTH);

		btnNuevo.addActionListener(e -> {
			limpiar();
			txtProducto.requestFocus();
		});
		btnCancelar.addActionListener(e -> limpiar());
		btnGuardar.addActionListener(e -> guardar());
	}

	private void addField(JPanel panel, GridBagConstraints g, int col, int row, String label,
			java.awt.Component field) {
		g.gridx = col;
		g.gridy = row;
		g.gridwidth = 1;
		panel.add(new JLabel(label), g);
		g.gridy = row + 1;
		panel.add(field, g);
	}

	private void guardar() {
		String producto = txtProducto.getText().trim();
		String cantidad = txtCantidad.getText().trim();
		if (producto.isEmpty() || cantidad.isEmpty() || cmbTipo.getSelectedIndex() == 0) {
			JOptionPane.showMessageDialog(this, "Complete producto, tipo y cantidad.", "Validación",
					JOptionPane.WARNING_MESSAGE);
			return;
		}
		try {
			Integer.parseInt(cantidad);
		} catch (NumberFormatException ex) {
			JOptionPane.showMessageDialog(this, "La cantidad debe ser numérica.");
			return;
		}
		String fecha = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
		model.addRow(new Object[] { fecha, producto, String.valueOf(cmbTipo.getSelectedItem()), cantidad });
		limpiar();
	}

	private void limpiar() {
		txtProducto.setText("");
		txtCantidad.setText("");
		txtMotivo.setText("");
		cmbTipo.setSelectedIndex(0);
		table.clearSelection();
	}
}
