package com.farmacia.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

public class MainFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    public MainFrame() {
        setTitle("Sistema de Farmacia - Java 8 Swing");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1150, 720));
        setSize(1280, 800);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Pantalla 1: Catálogo de Productos y Fármacos", new CatalogoPanel());
        tabs.addTab("Pantalla 2: Control de Stock y Lotes", new StockPanel());
        tabs.addTab("Pantalla 3: Proveedores y Órdenes de Compra", new ProveedoresPanel());
        tabs.addTab("Pantalla 4: Ajustes y Mermas", new AjustesPanel());

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(tabs, BorderLayout.CENTER);
    }
}
