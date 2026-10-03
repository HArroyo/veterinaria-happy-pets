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

public class StockPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private final DefaultTableModel model;
    private final JTable table;
    private final JTextField txtBuscar;
    private final JTextField txtProducto;
    private final JTextField txtLote;
    private final JTextField txtCantidad;
    private final JTextField txtVencimiento;
    private String movimiento = "INGRESO";

    public StockPanel() {
        setLayout(new BorderLayout(12, 12));
        setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        JPanel header = new JPanel(new BorderLayout());
        header.add(UIUtils.title("Control de Stock y Lotes"), BorderLayout.WEST);
        JPanel search = new JPanel(new BorderLayout(6, 4));
        search.add(UIUtils.fieldLabel("Buscar producto"), BorderLayout.NORTH);
        txtBuscar = UIUtils.textField();
        txtBuscar.setPreferredSize(new Dimension(290, 34));
        search.add(txtBuscar, BorderLayout.CENTER);
        header.add(search, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        model = new DefaultTableModel(new Object[] {"Producto", "Lote", "Vencimiento", "Stock"}, 0) {
            private static final long serialVersionUID = 1L;
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(model);
        table.setRowHeight(34);
        table.setFillsViewportHeight(true);
        JScrollPane scroll = new JScrollPane(table);

        JPanel center = new JPanel(new BorderLayout(0, 12));
        center.add(scroll, BorderLayout.CENTER);
        JPanel typeButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        JButton btnIngreso = UIUtils.darkButton("Ingreso");
        JButton btnSalida = UIUtils.lightButton("Salida");
        typeButtons.add(btnIngreso); typeButtons.add(btnSalida);
        center.add(typeButtons, BorderLayout.SOUTH);
        add(center, BorderLayout.CENTER);

        JPanel formWrapper = new JPanel(new BorderLayout(0, 10));
        formWrapper.setBorder(UIUtils.titledBorder("Registro de Movimientos de Stock (JPanel)"));
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 6, 6, 6); g.fill = GridBagConstraints.HORIZONTAL; g.weightx = 1;

        txtProducto = UIUtils.textField(); txtLote = UIUtils.textField();
        txtCantidad = UIUtils.textField(); txtVencimiento = UIUtils.textField();
        addField(form, g, 0, "Producto", txtProducto);
        addField(form, g, 1, "Lote", txtLote);
        addField(form, g, 2, "Cantidad", txtCantidad);
        addField(form, g, 3, "Fecha de vencimiento", txtVencimiento);
        formWrapper.add(form, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        JButton btnGuardar = UIUtils.darkButton("Guardar");
        JButton btnCancelar = UIUtils.lightButton("Cancelar");
        actions.add(btnGuardar); actions.add(btnCancelar);
        formWrapper.add(actions, BorderLayout.SOUTH);
        formWrapper.setPreferredSize(new Dimension(100, 190));
        add(formWrapper, BorderLayout.SOUTH);

        final TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<DefaultTableModel>(model);
        table.setRowSorter(sorter);
        txtBuscar.getDocument().addDocumentListener(new SimpleDocumentListener() {
            @Override public void update() {
                String text = txtBuscar.getText().trim();
                sorter.setRowFilter(text.isEmpty() ? null : RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(text)));
            }
        });

        btnIngreso.addActionListener(e -> movimiento = "INGRESO");
        btnSalida.addActionListener(e -> movimiento = "SALIDA");
        btnCancelar.addActionListener(e -> limpiar());
        btnGuardar.addActionListener(e -> guardarMovimiento());

        model.addRow(new Object[] {"Paracetamol 500 mg", "L-001", "12/2027", 50});
        model.addRow(new Object[] {"Alcohol 70%", "L-002", "06/2028", 30});
    }

    private void addField(JPanel panel, GridBagConstraints g, int col, String label, java.awt.Component field) {
        g.gridx = col; g.gridy = 0; panel.add(new JLabel(label), g);
        g.gridy = 1; panel.add(field, g);
    }

    private void guardarMovimiento() {
        String producto = txtProducto.getText().trim();
        String lote = txtLote.getText().trim();
        String cantidadText = txtCantidad.getText().trim();
        String vencimiento = txtVencimiento.getText().trim();
        if (producto.isEmpty() || lote.isEmpty() || cantidadText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Complete producto, lote y cantidad.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int cantidad;
        try { cantidad = Integer.parseInt(cantidadText); }
        catch (NumberFormatException ex) { JOptionPane.showMessageDialog(this, "Cantidad inválida."); return; }

        if ("SALIDA".equals(movimiento)) cantidad = -cantidad;
        model.addRow(new Object[] {producto, lote, vencimiento, cantidad});
        limpiar();
    }

    private void limpiar() {
        txtProducto.setText(""); txtLote.setText(""); txtCantidad.setText(""); txtVencimiento.setText("");
    }
}
