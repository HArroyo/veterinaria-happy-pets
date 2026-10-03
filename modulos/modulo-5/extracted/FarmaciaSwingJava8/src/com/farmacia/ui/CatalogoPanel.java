package com.farmacia.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import com.farmacia.util.UIUtils;

public class CatalogoPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private final DefaultTableModel model;
    private final JTable table;
    private final JTextField txtBuscar;
    private final JTextField txtCodigo;
    private final JTextField txtNombre;
    private final JComboBox<String> cmbCategoria;
    private final JTextField txtPrecio;

    public CatalogoPanel() {
        setLayout(new BorderLayout(12, 12));
        setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        JPanel header = new JPanel(new BorderLayout());
        header.add(UIUtils.title("Catálogo de Productos y Fármacos"), BorderLayout.WEST);

        JPanel search = new JPanel(new BorderLayout(6, 4));
        search.add(UIUtils.fieldLabel("Buscar producto"), BorderLayout.NORTH);
        txtBuscar = UIUtils.textField();
        txtBuscar.setPreferredSize(new Dimension(290, 34));
        search.add(txtBuscar, BorderLayout.CENTER);
        header.add(search, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        model = new DefaultTableModel(new Object[] {"Código", "Producto", "Categoría", "Precio"}, 0) {
            private static final long serialVersionUID = 1L;
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(model);
        table.setRowHeight(34);
        table.setFillsViewportHeight(true);
        JScrollPane scroll = new JScrollPane(table);

        JPanel center = new JPanel(new BorderLayout(0, 12));
        center.add(scroll, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        JButton btnNuevo = UIUtils.darkButton("Nuevo");
        JButton btnEditar = UIUtils.lightButton("Editar");
        JButton btnEliminar = UIUtils.lightButton("Eliminar");
        buttons.add(btnNuevo);
        buttons.add(btnEditar);
        buttons.add(btnEliminar);
        center.add(buttons, BorderLayout.SOUTH);
        add(center, BorderLayout.CENTER);

        JPanel formWrapper = new JPanel(new BorderLayout(0, 10));
        formWrapper.setBorder(UIUtils.titledBorder("Formulario de Producto / Fármaco (JPanel)"));

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 6, 6, 6);
        g.fill = GridBagConstraints.HORIZONTAL;
        g.weightx = 1;

        txtCodigo = UIUtils.textField();
        txtNombre = UIUtils.textField();
        cmbCategoria = new JComboBox<String>(new String[] {"Seleccione...", "Medicamento", "Cuidado personal", "Suplemento", "Otro"});
        txtPrecio = UIUtils.textField();

        addField(form, g, 0, "Código", txtCodigo);
        addField(form, g, 1, "Nombre", txtNombre);
        addField(form, g, 2, "Categoría", cmbCategoria);
        addField(form, g, 3, "Precio", txtPrecio);

        formWrapper.add(form, BorderLayout.CENTER);
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        JButton btnGuardar = UIUtils.darkButton("Guardar");
        JButton btnCancelar = UIUtils.lightButton("Cancelar");
        actions.add(btnGuardar);
        actions.add(btnCancelar);
        formWrapper.add(actions, BorderLayout.SOUTH);
        formWrapper.setPreferredSize(new Dimension(100, 210));
        add(formWrapper, BorderLayout.SOUTH);

        final TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<DefaultTableModel>(model);
        table.setRowSorter(sorter);
        txtBuscar.getDocument().addDocumentListener(new SimpleDocumentListener() {
            @Override public void update() {
                String text = txtBuscar.getText().trim();
                sorter.setRowFilter(text.isEmpty() ? null : RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(text)));
            }
        });

        btnGuardar.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) { guardar(); }
        });
        btnEliminar.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) { eliminar(); }
        });
        btnEditar.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) { cargarSeleccion(); }
        });
        btnNuevo.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) { limpiar(); txtCodigo.requestFocus(); }
        });
        btnCancelar.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) { limpiar(); }
        });

        model.addRow(new Object[] {"P001", "Paracetamol 500 mg", "Medicamento", "3.50"});
        model.addRow(new Object[] {"P002", "Alcohol 70%", "Cuidado personal", "8.90"});
    }

    private void addField(JPanel panel, GridBagConstraints g, int col, String label, java.awt.Component field) {
        g.gridx = col; g.gridy = 0;
        panel.add(new JLabel(label), g);
        g.gridy = 1;
        panel.add(field, g);
    }

    private void guardar() {
        String codigo = txtCodigo.getText().trim();
        String nombre = txtNombre.getText().trim();
        String categoria = String.valueOf(cmbCategoria.getSelectedItem());
        String precio = txtPrecio.getText().trim();
        if (codigo.isEmpty() || nombre.isEmpty() || precio.isEmpty() || cmbCategoria.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(this, "Complete todos los campos.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int selected = table.getSelectedRow();
        if (selected >= 0) {
            int row = table.convertRowIndexToModel(selected);
            model.setValueAt(codigo, row, 0);
            model.setValueAt(nombre, row, 1);
            model.setValueAt(categoria, row, 2);
            model.setValueAt(precio, row, 3);
        } else {
            model.addRow(new Object[] {codigo, nombre, categoria, precio});
        }
        limpiar();
    }

    private void eliminar() {
        int selected = table.getSelectedRow();
        if (selected < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto.");
            return;
        }
        model.removeRow(table.convertRowIndexToModel(selected));
        limpiar();
    }

    private void cargarSeleccion() {
        int selected = table.getSelectedRow();
        if (selected < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto.");
            return;
        }
        int row = table.convertRowIndexToModel(selected);
        txtCodigo.setText(String.valueOf(model.getValueAt(row, 0)));
        txtNombre.setText(String.valueOf(model.getValueAt(row, 1)));
        cmbCategoria.setSelectedItem(String.valueOf(model.getValueAt(row, 2)));
        txtPrecio.setText(String.valueOf(model.getValueAt(row, 3)));
    }

    private void limpiar() {
        txtCodigo.setText(""); txtNombre.setText(""); txtPrecio.setText("");
        cmbCategoria.setSelectedIndex(0); table.clearSelection();
    }
}
