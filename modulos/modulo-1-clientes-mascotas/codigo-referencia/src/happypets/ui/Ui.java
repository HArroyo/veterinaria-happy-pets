package happypets.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.function.IntConsumer;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

/** Componentes visuales compartidos por las tres ventanas. */
public final class Ui {
    public static final Color TURQUESA = new Color(0, 188, 212);
    public static final Color FONDO = new Color(240, 240, 240);
    public static final Color BORDE = new Color(120, 120, 120);
    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private Ui() { }

    public static void instalarApariencia() {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception ignored) { /* Swing conserva su apariencia predeterminada. */ }
    }

    public static void prepararVentana(JFrame frame, String titulo) {
        frame.setTitle("Happy Pets - " + titulo);
        frame.setIconImage(icono());
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setMinimumSize(new Dimension(1080, 680));
        frame.setSize(1440, 850);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());
        frame.add(cabeceraSistema(), BorderLayout.NORTH);
    }

    private static JPanel cabeceraSistema() {
        JPanel barra = new JPanel(new BorderLayout(24, 0));
        barra.setBackground(TURQUESA);
        barra.setBorder(new EmptyBorder(17, 28, 17, 28));
        JLabel titulo = new JLabel("Sistema de Gestión Veterinaria");
        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 18));
        barra.add(titulo, BorderLayout.WEST);
        JPanel derecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 32, 0));
        derecha.setOpaque(false);
        derecha.add(textoBlanco("Happy Pets"));
        derecha.add(textoBlanco("Usuario:"));
        barra.add(derecha, BorderLayout.EAST);
        return barra;
    }

    private static JLabel textoBlanco(String texto) {
        JLabel label = new JLabel(texto);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("SansSerif", Font.BOLD, 11));
        return label;
    }

    public static JPanel contenido() {
        JPanel panel = new JPanel(new BorderLayout(0, 16));
        panel.setBackground(FONDO);
        panel.setBorder(new EmptyBorder(20, 22, 20, 22));
        return panel;
    }

    public static JScrollPane desplazable(Component componente) {
        JScrollPane scroll = new JScrollPane(componente);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(18);
        return scroll;
    }

    public static JPanel cabeceraModulo(String titulo, String subtitulo) {
        JPanel panel = new JPanel(new BorderLayout(18, 0));
        panel.setBackground(TURQUESA);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE), new EmptyBorder(14, 18, 14, 18)));
        JLabel logo = new JLabel("HP", SwingConstants.CENTER);
        logo.setOpaque(true);
        logo.setBackground(Color.WHITE);
        logo.setForeground(TURQUESA.darker());
        logo.setFont(new Font("SansSerif", Font.BOLD, 18));
        logo.setPreferredSize(new Dimension(44, 44));
        logo.setBorder(BorderFactory.createLineBorder(BORDE));
        panel.add(logo, BorderLayout.WEST);
        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        JLabel nombre = new JLabel(titulo);
        nombre.setForeground(Color.WHITE);
        nombre.setFont(new Font("SansSerif", Font.BOLD, 22));
        JLabel descripcion = new JLabel(subtitulo);
        descripcion.setForeground(Color.WHITE);
        descripcion.setFont(new Font("SansSerif", Font.PLAIN, 11));
        textos.add(nombre);
        textos.add(Box.createVerticalStrut(4));
        textos.add(descripcion);
        panel.add(textos, BorderLayout.CENTER);
        JLabel sucursal = new JLabel("Happy Pets", SwingConstants.CENTER);
        sucursal.setOpaque(true);
        sucursal.setBackground(Color.WHITE);
        sucursal.setForeground(Color.DARK_GRAY);
        sucursal.setPreferredSize(new Dimension(180, 28));
        sucursal.setBorder(BorderFactory.createLineBorder(BORDE));
        panel.add(sucursal, BorderLayout.EAST);
        return panel;
    }

    public static JPanel vertical(Component... componentes) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        for (int i = 0; i < componentes.length; i++) {
            if (i > 0) panel.add(Box.createVerticalStrut(16));
            if (componentes[i] instanceof JComponent componente) {
                componente.setAlignmentX(Component.LEFT_ALIGNMENT);
            }
            panel.add(componentes[i]);
        }
        return panel;
    }

    public static JPanel tarjeta() {
        JPanel panel = new JPanel(new BorderLayout(0, 14));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE), new EmptyBorder(16, 16, 16, 16)));
        return panel;
    }

    public static JLabel tituloSeccion(String texto) {
        JLabel label = new JLabel(texto);
        label.setOpaque(true);
        label.setBackground(TURQUESA);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("SansSerif", Font.BOLD, 13));
        label.setBorder(new EmptyBorder(11, 14, 11, 14));
        return label;
    }

    public static JButton boton(String texto, boolean principal) {
        Color fondo = principal ? TURQUESA : Color.WHITE;
        JButton boton = new JButton(texto) {
            private static final long serialVersionUID = 1L;

            @Override protected void paintComponent(Graphics g) {
                g.setColor(getModel().isPressed() ? fondo.darker() : fondo);
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        boton.setFont(new Font("SansSerif", Font.BOLD, 11));
        boton.setMargin(new Insets(8, 14, 8, 14));
        boton.setFocusPainted(false);
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        boton.setBackground(fondo);
        boton.setForeground(principal ? Color.WHITE : Color.BLACK);
        boton.setContentAreaFilled(false);
        boton.setOpaque(false);
        boton.setBorder(BorderFactory.createLineBorder(new Color(65, 65, 65)));
        boton.setPreferredSize(new Dimension(Math.max(86, texto.length() * 6 + 24), 32));
        return boton;
    }

    public static JTextField campo(int columnas) {
        JTextField campo = new JTextField(columnas);
        campo.setFont(new Font("SansSerif", Font.PLAIN, 12));
        campo.setMargin(new Insets(6, 7, 6, 7));
        return campo;
    }

    public static JPanel fila(FlowLayout layout) {
        JPanel panel = new JPanel(layout);
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE), new EmptyBorder(9, 10, 9, 10)));
        return panel;
    }

    public static JPanel pie(String modulo) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE), new EmptyBorder(9, 14, 9, 14)));
        panel.add(new JLabel("Módulo: " + modulo), BorderLayout.WEST);
        return panel;
    }

    public static JTable tabla(javax.swing.table.TableModel modelo) {
        JTable tabla = new JTable(modelo);
        tabla.setRowHeight(38);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setFillsViewportHeight(true);
        tabla.setAutoCreateRowSorter(true);
        tabla.setFont(new Font("SansSerif", Font.PLAIN, 12));
        tabla.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        tabla.getTableHeader().setBackground(new Color(230, 234, 238));
        tabla.setGridColor(new Color(170, 170, 170));
        tabla.setShowGrid(true);
        return tabla;
    }

    public static void instalarBotonTabla(JTable tabla, int columna, IntConsumer accion) {
        BotonTabla boton = new BotonTabla(tabla, accion);
        tabla.getColumnModel().getColumn(columna).setCellRenderer(boton);
        tabla.getColumnModel().getColumn(columna).setCellEditor(boton);
    }

    public static String fecha(LocalDate valor) { return valor == null ? "" : FECHA.format(valor); }

    public static BufferedImage icono() {
        BufferedImage imagen = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = imagen.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(TURQUESA);
        g.fillRoundRect(0, 0, 31, 31, 7, 7);
        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 16));
        g.drawString("HP", 3, 22);
        g.dispose();
        return imagen;
    }

    public static void pendiente(Component padre, String accion) {
        JOptionPane.showMessageDialog(padre,
                accion + " se conectará a la lógica del sistema en la siguiente etapa.",
                "Evento preparado", JOptionPane.INFORMATION_MESSAGE);
    }

    private static final class BotonTabla extends AbstractCellEditor
            implements TableCellRenderer, TableCellEditor {
        private static final long serialVersionUID = 1L;
        private final JButton visual = boton("", false);
        private final JButton editor = boton("", false);
        private final JTable tabla;
        private final IntConsumer accion;
        private int filaModelo;

        BotonTabla(JTable tabla, IntConsumer accion) {
            this.tabla = tabla;
            this.accion = accion;
            editor.addActionListener(evento -> {
                fireEditingStopped();
                accion.accept(filaModelo);
            });
        }

        @Override public Component getTableCellRendererComponent(JTable tabla, Object valor,
                boolean seleccionado, boolean foco, int fila, int columna) {
            visual.setText(String.valueOf(valor));
            return visual;
        }

        @Override public Component getTableCellEditorComponent(JTable tabla, Object valor,
                boolean seleccionado, int fila, int columna) {
            filaModelo = this.tabla.convertRowIndexToModel(fila);
            editor.setText(String.valueOf(valor));
            return editor;
        }

        @Override public Object getCellEditorValue() { return editor.getText(); }
    }
}
