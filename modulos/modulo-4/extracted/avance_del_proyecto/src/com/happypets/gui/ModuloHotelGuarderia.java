package com.happypets.gui;

import javax.swing.*;
import java.awt.*;

public class ModuloHotelGuarderia extends JDialog {
    public ModuloHotelGuarderia(JFrame padre) {
        super(padre, "Hotel / Guardería", true);
        setSize(850, 650);
        setLocationRelativeTo(padre);
        setLayout(new GridBagLayout());
        getContentPane().setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;

        JLabel lblTitulo = new JLabel("HOTEL / GUARDERÍA", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        add(lblTitulo, gbc);

        // --- Panel Izquierdo: Ocupación ---
        JPanel panelOcupacion = new JPanel(new GridLayout(3, 3, 10, 10));
        panelOcupacion.setBorder(BorderFactory.createTitledBorder("ESTADO DE OCUPACIÓN"));
        panelOcupacion.setBackground(Color.WHITE);
        for (int i = 1; i <= 9; i++) {
            String estado = (i == 1 || i == 2 || i == 6) ? "OCUPADO" : "LIBRE";
            JPanel box = new JPanel(new BorderLayout());
            box.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            box.add(new JLabel("Box 0" + i + " - " + estado, SwingConstants.CENTER), BorderLayout.NORTH);
            box.add(new JCheckBox("Check de control"), BorderLayout.CENTER);
            panelOcupacion.add(box);
        }
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1; gbc.weightx = 0.6; gbc.weighty = 0.5;
        add(panelOcupacion, gbc);

        // --- Panel Derecho: Calendario ---
        JPanel panelReserva = new JPanel(new BorderLayout());
        panelReserva.setBorder(BorderFactory.createTitledBorder("RESERVAR (PERIODO DE ESTADÍA)"));
        panelReserva.setBackground(Color.WHITE);
        panelReserva.add(new JLabel("Simulación de Calendario (Enero 2017)", SwingConstants.CENTER), BorderLayout.CENTER);
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 0.4;
        add(panelReserva, gbc);

        // --- Panel Inferior: Datos del Ocupante ---
        JPanel panelDatos = new JPanel(new GridLayout(4, 4, 5, 5));
        panelDatos.setBorder(BorderFactory.createTitledBorder("DATOS DEL OCUPANTE"));
        panelDatos.setBackground(Color.WHITE);
        
        panelDatos.add(new JLabel("Propietario:")); panelDatos.add(new JTextField());
        panelDatos.add(new JLabel("Peso:")); panelDatos.add(new JTextField());
        panelDatos.add(new JLabel("Teléfono:")); panelDatos.add(new JTextField());
        panelDatos.add(new JLabel("Año:")); panelDatos.add(new JTextField());
        panelDatos.add(new JLabel("Correo:")); panelDatos.add(new JTextField());
        panelDatos.add(new JLabel("Raza:")); panelDatos.add(new JTextField());
        panelDatos.add(new JLabel("Ingreso:")); panelDatos.add(new JTextField());
        panelDatos.add(new JLabel("Salida:")); panelDatos.add(new JTextField());

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; gbc.weighty = 0.3;
        add(panelDatos, gbc);
    }
}
