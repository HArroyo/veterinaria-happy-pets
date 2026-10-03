package com.happypets.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class ModuloPrincipal extends JFrame {

    public ModuloPrincipal() {
        setTitle("Happy Pets - Servicios Estéticos y Hospedaje");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- 1. MENÚ LATERAL IZQUIERDO (Panel Gris) ---
        JPanel panelLateral = new JPanel();
        panelLateral.setBackground(new Color(110, 110, 110));
        panelLateral.setPreferredSize(new Dimension(200, 700));
        panelLateral.setLayout(new GridLayout(7, 1, 5, 5));

        JLabel lblLogo = new JLabel(" Happy Pets", SwingConstants.CENTER);
        lblLogo.setForeground(Color.WHITE);
        lblLogo.setFont(new Font("Arial", Font.BOLD, 16));
        panelLateral.add(lblLogo);

        String[] botonesLaterales = {"Consultas", "Grooming", "Hospitalización", "Hotel y Guardería", "Adopciones", "Configuración"};
        for (String texto : botonesLaterales) {
            JButton btn = new JButton(texto);
            btn.setFocusable(false);
            panelLateral.add(btn);
        }
        add(panelLateral, BorderLayout.WEST);

        // --- 2. CONTENIDO PRINCIPAL ---
        JPanel panelContenido = new JPanel(new BorderLayout());
        panelContenido.setBackground(Color.WHITE);

        // Encabezado Superior
        JPanel panelHeader = new JPanel(new BorderLayout());
        panelHeader.setBackground(Color.WHITE);
        panelHeader.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        JLabel lblTitulo = new JLabel("Servicios Estéticos y Hospedaje", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 22));
        panelHeader.add(lblTitulo, BorderLayout.CENTER);
        panelContenido.add(panelHeader, BorderLayout.NORTH);

        // Cuadrícula de Tarjetas/Módulos (2x2)
        JPanel panelGrid = new JPanel(new GridLayout(2, 2, 20, 20));
        panelGrid.setBackground(Color.WHITE);
        panelGrid.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Creación de las 4 Tarjetas vinculadas a sus respectivos JDialogs
        panelGrid.add(crearTarjeta("Adopciones Responsables", "Programa de rescate y adopción de caninos y felinos.", "Conocer Mascotas", e -> abrirModulo(new ModuloAdopciones(this))));
        panelGrid.add(crearTarjeta("Grooming y Peluquería", "Corte según raza, baño medicado y limpieza otológica.", "Reservar Turno", e -> abrirModulo(new ModuloGrooming(this))));
        panelGrid.add(crearTarjeta("Hospitalización", "Módulos climatizados, fluidoterapia y monitoreo 24/7.", "Consultar Estado", e -> abrirModulo(new ModuloHospitalizacion(this))));
        panelGrid.add(crearTarjeta("Hotel y Guardería", "Estadías en suites individuales con zonas de juego.", "Reservar Estadía", e -> abrirModulo(new ModuloHotelGuarderia(this))));

        panelContenido.add(panelGrid, BorderLayout.CENTER);
        add(panelContenido, BorderLayout.CENTER);
    }

    private JPanel crearTarjeta(String titulo, String desc, String textoBoton, ActionListener accion) {
        JPanel tarjeta = new JPanel();
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));
        tarjeta.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true));
        tarjeta.setBackground(Color.WHITE);

        JLabel lblT = new JLabel(titulo);
        lblT.setFont(new Font("Arial", Font.BOLD, 16));
        lblT.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblD = new JLabel("<html><body style='width: 250px;'>" + desc + "</body></html>");
        lblD.setFont(new Font("Arial", Font.PLAIN, 12));
        lblD.setForeground(Color.GRAY);
        lblD.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton btn = new JButton(textoBoton);
        btn.setBackground(new Color(80, 80, 80));
        btn.setForeground(Color.WHITE);
        btn.setFocusable(false);
        btn.addActionListener(accion);
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);

        tarjeta.add(Box.createVerticalStrut(15));
        tarjeta.add(lblT);
        tarjeta.add(Box.createVerticalStrut(10));
        tarjeta.add(lblD);
        tarjeta.add(Box.createVerticalGlue());
        tarjeta.add(btn);
        tarjeta.add(Box.createVerticalStrut(15));

        JPanel contenedorMargen = new JPanel(new BorderLayout());
        contenedorMargen.setBackground(Color.WHITE);
        contenedorMargen.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        contenedorMargen.add(tarjeta);

        return contenedorMargen;
    }

    private void abrirModulo(JDialog modulo) {
        modulo.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ModuloPrincipal().setVisible(true));
    }
}
