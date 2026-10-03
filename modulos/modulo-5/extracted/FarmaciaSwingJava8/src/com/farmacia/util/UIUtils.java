package com.farmacia.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

public final class UIUtils {
    public static final Color DARK = new Color(31, 41, 55);
    public static final Color LIGHT = new Color(245, 247, 250);
    public static final Color BORDER = new Color(126, 136, 148);

    private UIUtils() { }

    public static JLabel title(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 18));
        return label;
    }

    public static JLabel fieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.PLAIN, 12));
        return label;
    }

    public static JButton darkButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setBackground(DARK);
        button.setForeground(Color.WHITE);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setFont(new Font("SansSerif", Font.BOLD, 12));
        button.setPreferredSize(new Dimension(Math.max(85, text.length() * 8 + 24), 36));
        return button;
    }

    public static JButton lightButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setFont(new Font("SansSerif", Font.PLAIN, 12));
        button.setPreferredSize(new Dimension(Math.max(85, text.length() * 8 + 24), 36));
        return button;
    }

    public static JTextField textField() {
        JTextField field = new JTextField();
        field.setPreferredSize(new Dimension(180, 34));
        return field;
    }

    public static Border titledBorder(String title) {
        TitledBorder border = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(BORDER), title);
        border.setTitleFont(new Font("SansSerif", Font.BOLD, 12));
        return border;
    }

    public static void addPadding(JComponent component, int top, int left, int bottom, int right) {
        component.setBorder(BorderFactory.createCompoundBorder(
                component.getBorder(),
                BorderFactory.createEmptyBorder(top, left, bottom, right)));
    }

    public static void setBackgroundRecursive(Component component, Color color) {
        if (component instanceof JPanel) {
            component.setBackground(color);
        }
        if (component instanceof java.awt.Container) {
            Component[] children = ((java.awt.Container) component).getComponents();
            for (Component child : children) {
                setBackgroundRecursive(child, color);
            }
        }
    }
}
