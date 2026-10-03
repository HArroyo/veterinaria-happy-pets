package com.happypets.gui;

import javax.swing.*;
import java.awt.*;

public class ModuloAdopciones extends JDialog {
    public ModuloAdopciones(JFrame padre) {
        super(padre, "Adopciones y Rescates", true);
        setSize(800, 650);
        setLocationRelativeTo(padre);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(Color.WHITE);

        JLabel lblTitulo = new JLabel("ADOPCIONES Y RESCATES", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        add(lblTitulo, BorderLayout.NORTH);

        JPanel panelCentro = new JPanel(new GridLayout(2, 1, 10, 10));
        panelCentro.setBackground(Color.WHITE);

        JPanel panelMascotas = new JPanel(new BorderLayout());
        panelMascotas.setBorder(BorderFactory.createTitledBorder("MASCOTAS EN ADOPCIÓN"));
        panelMascotas.setBackground(Color.WHITE);
        
        String[] columnas = {"Mascotas Disponibles (Lista)"};
        String[][] datos = {{"Item 1"}, {"Item 2"}, {"Item 3"}, {"Item 4"}};
        JTable tabla = new JTable(datos, columnas);
        panelMascotas.add(new JTextField(" Buscar mascota..."), BorderLayout.NORTH);
        panelMascotas.add(new JScrollPane(tabla), BorderLayout.CENTER);
        
        panelCentro.add(panelMascotas);

        JPanel panelAdoptante = new JPanel(new GridLayout(3, 4, 5, 5));
        panelAdoptante.setBorder(BorderFactory.createTitledBorder("DATOS DEL ADOPTANTE"));
        panelAdoptante.setBackground(Color.WHITE);
        panelAdoptante.add(new JLabel("Propietario:")); panelAdoptante.add(new JTextField());
        panelAdoptante.add(new JLabel("Peso:")); panelAdoptante.add(new JTextField());
        panelAdoptante.add(new JLabel("Teléfono:")); panelAdoptante.add(new JTextField());
        panelAdoptante.add(new JLabel("Año:")); panelAdoptante.add(new JTextField());
        panelAdoptante.add(new JLabel("Correo:")); panelAdoptante.add(new JTextField());
        panelAdoptante.add(new JLabel("Raza:")); panelAdoptante.add(new JTextField());

        panelCentro.add(panelAdoptante);
        add(panelCentro, BorderLayout.CENTER);

        JLabel lblSeleccionada = new JLabel("MASCOTA SELECCIONADA: ID 101 - Rocky (Perro, Mestizo, Mediano, 1 año)", SwingConstants.CENTER);
        lblSeleccionada.setForeground(new Color(30, 70, 120));
        lblSeleccionada.setFont(new Font("Arial", Font.BOLD, 13));
        add(lblSeleccionada, BorderLayout.SOUTH);
    }
}
