package com.farmacia.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
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

public class ProveedoresPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private final DefaultTableModel model;
    private final JTable table;
    private final JTextField txtBuscar;
    private final JTextField txtRuc;
    private final JTextField txtRazonSocial;
    private final JTextField txtTelefono;

    public ProveedoresPanel() {
        setLayout(new BorderLayout(12, 12));
        setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        JPanel header = new JPanel(new BorderLayout());
        header.add(UIUtils.title("Proveedores y Órdenes de Compra"), BorderLayout.WEST);
        JPanel search = new JPanel(new BorderLayout(6, 4));
        search.add(UIUtils.fieldLabel("Buscar proveedor"), BorderLayout.NORTH);
        txtBuscar = UIUtils.textField(); txtBuscar.setPreferredSize(new Dimension(290, 34));
        search.add(txtBuscar, BorderLayout.CENTER);
        header.add(search, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        model = new DefaultTableModel(new Object[] {"RUC", "Proveedor", "Teléfono"}, 0) {
            private static final long serialVersionUID = 1L;
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(model); table.setRowHeight(34); table.setFillsViewportHeight(true);
        JScrollPane scroll = new JScrollPane(table);

        JPanel center = new JPanel(new BorderLayout(0, 12));
        center.add(scroll, BorderLayout.CENTER);
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        JButton btnNuevo = UIUtils.darkButton("Nuevo");
        JButton btnEditar = UIUtils.lightButton("Editar");
        JButton btnEliminar = UIUtils.lightButton("Eliminar");
        JButton btnOrdenes = UIUtils.lightButton("Órdenes de Compra");
        buttons.add(btnNuevo); buttons.add(btnEditar); buttons.add(btnEliminar); buttons.add(btnOrdenes);
        center.add(buttons, BorderLayout.SOUTH);
        add(center, BorderLayout.CENTER);

        JPanel formWrapper = new JPanel(new BorderLayout(0, 10));
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 6, 6, 6); g.fill = GridBagConstraints.HORIZONTAL; g.weightx = 1;
        txtRuc = UIUtils.textField(); txtRazonSocial = UIUtils.textField(); txtTelefono = UIUtils.textField();
        addField(form, g, 0, "RUC", txtRuc);
        addField(form, g, 1, "Razón social", txtRazonSocial);
        addField(form, g, 2, "Teléfono", txtTelefono);
        formWrapper.add(form, BorderLayout.CENTER);
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        JButton btnGuardar = UIUtils.darkButton("Guardar");
        JButton btnCancelar = UIUtils.lightButton("Cancelar");
        actions.add(btnGuardar); actions.add(btnCancelar);
        formWrapper.add(actions, BorderLayout.SOUTH);
        formWrapper.setPreferredSize(new Dimension(100, 150));
        add(formWrapper, BorderLayout.SOUTH);

        final TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<DefaultTableModel>(model);
        table.setRowSorter(sorter);
        txtBuscar.getDocument().addDocumentListener(new SimpleDocumentListener() {
            @Override public void update() {
                String text = txtBuscar.getText().trim();
                sorter.setRowFilter(text.isEmpty() ? null : RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(text)));
            }
        });

        btnNuevo.addActionListener(e -> limpiar());
        btnCancelar.addActionListener(e -> limpiar());
        btnGuardar.addActionListener(e -> guardar());
        btnEditar.addActionListener(e -> cargarSeleccion());
        btnEliminar.addActionListener(e -> eliminar());
        btnOrdenes.addActionListener(e -> JOptionPane.showMessageDialog(this, "Módulo de Órdenes de Compra preparado para integrarse."));

        model.addRow(new Object[] {"20123456789", "Distribuidora Salud SAC", "987654321"});
        model.addRow(new Object[] {"20555544441", "Farma Proveedor EIRL", "912345678"});
    }

    private void addField(JPanel panel, GridBagConstraints g, int col, String label, java.awt.Component field) {
        g.gridx = col; g.gridy = 0; panel.add(new JLabel(label), g);
        g.gridy = 1; panel.add(field, g);
    }

    private void guardar() {
        String ruc = txtRuc.getText().trim();
        String razon = txtRazonSocial.getText().trim();
        String telefono = txtTelefono.getText().trim();
        if (ruc.isEmpty() || razon.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Complete RUC y razón social.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int selected = table.getSelectedRow();
        if (selected >= 0) {
            int row = table.convertRowIndexToModel(selected);
            model.setValueAt(ruc, row, 0); model.setValueAt(razon, row, 1); model.setValueAt(telefono, row, 2);
        } else {
            model.addRow(new Object[] {ruc, razon, telefono});
        }
        limpiar();
    }

    private void cargarSeleccion() {
        int selected = table.getSelectedRow();
        if (selected < 0) { JOptionPane.showMessageDialog(this, "Seleccione un proveedor."); return; }
        int row = table.convertRowIndexToModel(selected);
        txtRuc.setText(String.valueOf(model.getValueAt(row, 0)));
        txtRazonSocial.setText(String.valueOf(model.getValueAt(row, 1)));
        txtTelefono.setText(String.valueOf(model.getValueAt(row, 2)));
    }

    private void eliminar() {
        int selected = table.getSelectedRow();
        if (selected < 0) { JOptionPane.showMessageDialog(this, "Seleccione un proveedor."); return; }
        model.removeRow(table.convertRowIndexToModel(selected));
        limpiar();
    }

    private void limpiar() {
        txtRuc.setText(""); txtRazonSocial.setText(""); txtTelefono.setText(""); table.clearSelection();
    }
}
