package com.happypets.gui;

import javax.swing.*;
import java.awt.*;

public class ModuloHospitalizacion extends JDialog {
    public ModuloHospitalizacion(JFrame padre) {
        super(padre, "Hospitalización", true);
        setSize(850, 650);
        setLocationRelativeTo(padre);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(Color.WHITE);

        JLabel lblTitulo = new JLabel("HOSPITALIZACIÓN", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        add(lblTitulo, BorderLayout.NORTH);

        JPanel panelCuerpo = new JPanel(new GridLayout(2, 1, 10, 10));
        panelCuerpo.setBackground(Color.WHITE);

        JPanel panelCunas = new JPanel(new GridLayout(2, 5, 5, 5));
        panelCunas.setBorder(BorderFactory.createTitledBorder("MAPA DE OCUPACIÓN (Cunas / Cajas / Caniles)"));
        panelCunas.setBackground(Color.WHITE);
        for(int i=1; i<=10; i++) {
            panelCunas.add(new JButton("Box 0" + i + (i%3==0 ? " [OCUPADO]":" [LIBRE]")));
        }
        panelCuerpo.add(panelCunas);

        JPanel panelPaciente = new JPanel(new BorderLayout());
        panelPaciente.setBorder(BorderFactory.createTitledBorder("EXPEDIENTE DE HOSPITALIZACIÓN: Toby (Canino - Golden Retriever)"));
        panelPaciente.setBackground(Color.WHITE);
        
        JTextArea txtEvolucion = new JTextArea("Tratamientos y Medicación Actual:\n- Fluidoterapia cada 4 horas\n- Monitoreo de temperatura constante\n- Alimentación blanda");
        panelPaciente.add(new JScrollPane(txtEvolucion), BorderLayout.CENTER);
        
        panelCuerpo.add(panelPaciente);
        add(panelCuerpo, BorderLayout.CENTER);
    }
}
