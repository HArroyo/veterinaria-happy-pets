package com.farmacia.app;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.farmacia.ui.MainFrame;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception e) {
                    // Si falla el look & feel del sistema, Swing usa el predeterminado.
                }
                new MainFrame().setVisible(true);
            }
        });
    }
}
