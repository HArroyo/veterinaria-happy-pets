package com.happypets.gui;

import javax.swing.*;
import java.awt.*;

public class ModuloGrooming extends JDialog {
    public ModuloGrooming(JFrame padre) {
        super(padre, "Grooming y Peluquería", true);
        setSize(800, 600);
        setLocationRelativeTo(padre);
        setLayout(new BorderLayout(15, 15));
        getContentPane().setBackground(Color.WHITE);

        JLabel lblTitulo = new JLabel("GROOMING Y PELUQUERÍA", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        add(lblTitulo, BorderLayout.NORTH);

        JPanel panelCentral = new JPanel(new GridLayout(1, 2, 15, 15));
        panelCentral.setBackground(Color.WHITE);

        JPanel panelIzquierdo = new JPanel();
        panelIzquierdo.setLayout(new BoxLayout(panelIzquierdo, BoxLayout.Y_AXIS));
        panelIzquierdo.setBorder(BorderFactory.createTitledBorder("CHECKLIST DE ENTRADA"));
        panelIzquierdo.setBackground(Color.WHITE);

        panelIzquierdo.add(new JCheckBox("Control de pulgas y garrapatas"));
        panelIzquierdo.add(new JCheckBox("Revisión de piel (sin heridas)"));
        panelIzquierdo.add(new JCheckBox("Pelaje libre de nudos graves"));
        panelIzquierdo.add(Box.createVerticalStrut(20));
        panelIzquierdo.add(new JLabel("Observaciones adicionales:"));
        panelIzquierdo.add(new JScrollPane(new JTextArea(5, 20)));

        JPanel panelDerecho = new JPanel(new BorderLayout());
        panelDerecho.setBorder(BorderFactory.createTitledBorder("AGENDA DE TURNOS Y ESTILISTAS"));
        panelDerecho.setBackground(Color.WHITE);
        
        String[] estilistas = {"Seleccionar Estilista...", "Carlos Mendoza", "Ana Martínez", "Luis Peña"};
        JComboBox<String> cbEstilistas = new JComboBox<>(estilistas);
        panelDerecho.add(cbEstilistas, BorderLayout.NORTH);
        panelDerecho.add(new JLabel("Simulación Agenda Diaria - Vista de Turnos", SwingConstants.CENTER), BorderLayout.CENTER);

        panelCentral.add(panelIzquierdo);
        panelCentral.add(panelDerecho);
        add(panelCentral, BorderLayout.CENTER);

        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelInferior.setBackground(new Color(245, 245, 245));
        panelInferior.add(new JLabel("Ficha actual del servicio: Luna - Gato - Persa - 2 años"));
        add(panelInferior, BorderLayout.SOUTH);
    }
}
