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

/**
 * Constantes y componentes visuales reutilizables que aseguran
 * fidelidad visual exacta con los wireframes del proyecto Happy Pets.
 */
public final class Ui {
    public static final Color TURQUESA = new Color(0, 188, 212);
    public static final Color TURQUESA_OSCURO = new Color(0, 150, 169);
    public static final Color FONDO = new Color(242, 244, 247);
    public static final Color BORDE_SUAVE = new Color(205, 212, 218);
    public static final Color TEXTO_TITULO = new Color(33, 37, 41);
    public static final Color TEXTO_MUTED = new Color(108, 117, 125);
    public static final Color ESTADO_ACTIVO = new Color(40, 167, 69);
    
    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private Ui() { }

    public static void instalarApariencia() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) { }
    }

    /**
     * Barra superior institucional exactamente como en los wireframes:
     * "Sistema de Gestión Veterinaria" a la izquierda,
     * "Happy Pets" y "Usuario: Dr. R. Mendoza (Admin)" a la derecha.
     */
    public static JPanel crearCabeceraSistema() {
        JPanel barra = new JPanel(new BorderLayout());
        barra.setBackground(TURQUESA);
        barra.setPreferredSize(new Dimension(1400, 54));
        barra.setBorder(new EmptyBorder(14, 28, 14, 28));

        JLabel titulo = new JLabel("Sistema de Gestión Veterinaria");
        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 17));
        barra.add(titulo, BorderLayout.WEST);

        JPanel derecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 28, 0));
        derecha.setOpaque(false);

        JLabel marca = new JLabel("Happy Pets");
        marca.setForeground(Color.WHITE);
        marca.setFont(new Font("Segoe UI", Font.BOLD, 12));

        JLabel usuario = new JLabel("Usuario: Dr. R. Mendoza (Admin)");
        usuario.setForeground(Color.WHITE);
        usuario.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        derecha.add(marca);
        derecha.add(usuario);
        barra.add(derecha, BorderLayout.EAST);
        return barra;
    }

    /**
     * Cabecera del módulo con insignia 'HP', título, subtítulo y badge 'Happy Pets'.
     */
    public static JPanel crearCabeceraModulo(String tituloModulo, String subtituloModulo) {
        JPanel panel = new JPanel(new BorderLayout(16, 0));
        panel.setBackground(TURQUESA);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(TURQUESA_OSCURO, 1),
                new EmptyBorder(14, 18, 14, 18)
        ));

        // Insignia HP
        JLabel badge = new JLabel("HP", SwingConstants.CENTER) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        badge.setPreferredSize(new Dimension(48, 48));
        badge.setForeground(TURQUESA);
        badge.setFont(new Font("Segoe UI", Font.BOLD, 20));
        panel.add(badge, BorderLayout.WEST);

        // Textos del Módulo
        JPanel centro = new JPanel();
        centro.setOpaque(false);
        centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));

        JLabel lblTitulo = new JLabel(tituloModulo);
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));

        JLabel lblSub = new JLabel(subtituloModulo);
        lblSub.setForeground(new Color(240, 255, 255));
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        centro.add(lblTitulo);
        centro.add(Box.createVerticalStrut(3));
        centro.add(lblSub);
        panel.add(centro, BorderLayout.CENTER);

        // Badge lateral Happy Pets
        JLabel pill = new JLabel("Happy Pets", SwingConstants.CENTER) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        pill.setPreferredSize(new Dimension(170, 30));
        pill.setForeground(new Color(60, 60, 60));
        pill.setFont(new Font("Segoe UI", Font.BOLD, 12));
        panel.add(pill, BorderLayout.EAST);

        return panel;
    }

    /**
     * Botón estilizado según sea principal (cian) o secundario (blanco con borde).
     */
    public static JButton boton(String texto, boolean principal) {
        Color fondo = principal ? TURQUESA : Color.WHITE;
        Color textoColor = principal ? Color.WHITE : new Color(40, 40, 40);
        Color bordeColor = principal ? TURQUESA_OSCURO : new Color(170, 175, 180);

        JButton boton = new JButton(texto) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2.setColor(fondo.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(principal ? TURQUESA_OSCURO : new Color(245, 247, 250));
                } else {
                    g2.setColor(fondo);
                }
                g2.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
                g2.dispose();
            }
        };
        boton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        boton.setForeground(textoColor);
        boton.setFocusPainted(false);
        boton.setContentAreaFilled(false);
        boton.setOpaque(false);
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        boton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bordeColor, 1),
                new EmptyBorder(6, 14, 6, 14)
        ));
        return boton;
    }

    public static JButton botonPrimario(String texto, javax.swing.Icon icono) {
        JButton b = boton(texto, true);
        if (icono != null) {
            b.setIcon(icono);
            b.setIconTextGap(6);
        }
        return b;
    }

    public static JButton botonSecundario(String texto, javax.swing.Icon icono) {
        JButton b = boton(texto, false);
        if (icono != null) {
            b.setIcon(icono);
            b.setIconTextGap(6);
        }
        return b;
    }

    /**
     * Campo de texto con borde elegante y tamaño uniforme.
     */
    public static JTextField campoTexto(int columnas) {
        JTextField tf = new JTextField(columnas);
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 185, 190), 1),
                new EmptyBorder(5, 8, 5, 8)
        ));
        return tf;
    }

    /**
     * Panel tarjeta blanca contenedor con borde suave.
     */
    public static JPanel crearTarjeta() {
        JPanel p = new JPanel();
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createLineBorder(BORDE_SUAVE, 1));
        return p;
    }

    /**
     * Barra de título para tarjetas internas (turquesa con texto blanco).
     */
    public static JPanel crearTituloTarjeta(String titulo) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(TURQUESA);
        p.setBorder(new EmptyBorder(8, 14, 8, 14));
        JLabel lbl = new JLabel(titulo);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(Color.WHITE);
        p.add(lbl, BorderLayout.WEST);
        return p;
    }

    /**
     * Pie de página del módulo exactamente como el wireframe.
     */
    public static JPanel crearPieModulo(String modulo, Component... botonesDerecha) {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(Color.WHITE);
        footer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE_SUAVE, 1),
                new EmptyBorder(10, 16, 10, 16)
        ));

        JLabel lbl = new JLabel("Módulo: " + modulo);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setForeground(new Color(70, 70, 70));
        footer.add(lbl, BorderLayout.WEST);

        if (botonesDerecha != null && botonesDerecha.length > 0) {
            JPanel derecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
            derecha.setOpaque(false);
            for (Component c : botonesDerecha) {
                derecha.add(c);
            }
            footer.add(derecha, BorderLayout.EAST);
        }
        return footer;
    }

    /**
     * Configuración estándar de tablas Swing para alinearse con los wireframes.
     */
    public static void formatearTabla(JTable tabla) {
        tabla.setRowHeight(32);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setGridColor(new Color(225, 230, 235));
        tabla.setShowGrid(true);
        tabla.setFillsViewportHeight(true);

        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabla.getTableHeader().setBackground(new Color(245, 247, 250));
        tabla.getTableHeader().setForeground(new Color(50, 50, 50));
        tabla.getTableHeader().setPreferredSize(new Dimension(tabla.getColumnModel().getTotalColumnWidth(), 34));

        DefaultTableCellRenderer renderCentro = new DefaultTableCellRenderer();
        renderCentro.setHorizontalAlignment(SwingConstants.CENTER);

        // Alineación central de columnas estándar si aplican
        if (tabla.getColumnCount() > 0) {
            tabla.getColumnModel().getColumn(0).setCellRenderer(renderCentro);
        }
    }

    public static BufferedImage icono() {
        BufferedImage imagen = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = imagen.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(TURQUESA);
        g.fillRoundRect(0, 0, 31, 31, 8, 8);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Segoe UI", Font.BOLD, 17));
        g.drawString("HP", 3, 23);
        g.dispose();
        return imagen;
    }

    public static void configurarVentana(JFrame frame, String titulo) {
        frame.setTitle("Happy Pets - " + titulo);
        frame.setIconImage(icono());
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(1440, 860);
        frame.setMinimumSize(new Dimension(1200, 750));
        frame.setLocationRelativeTo(null);
    }

    public static String fecha(LocalDate valor) {
        return valor == null ? "" : FECHA.format(valor);
    }

    /**
     * Renderer y Editor para botones en una columna de JTable (ej: 'Ver detalle').
     */
    public static void instalarBotonColumna(JTable tabla, int columna, String textoBoton, IntConsumer accion) {
        BotonColumnaRendererEditor botonHandler = new BotonColumnaRendererEditor(tabla, textoBoton, accion);
        tabla.getColumnModel().getColumn(columna).setCellRenderer(botonHandler);
        tabla.getColumnModel().getColumn(columna).setCellEditor(botonHandler);
    }

    private static final class BotonColumnaRendererEditor extends AbstractCellEditor
            implements TableCellRenderer, TableCellEditor {
        private static final long serialVersionUID = 1L;
        private final JButton visual;
        private final JButton editor;
        private final JTable tabla;
        private final IntConsumer accion;
        private int filaSeleccionada;

        BotonColumnaRendererEditor(JTable tabla, String texto, IntConsumer accion) {
            this.tabla = tabla;
            this.accion = accion;
            this.visual = boton(texto, false);
            this.editor = boton(texto, false);

            this.editor.addActionListener(e -> {
                fireEditingStopped();
                accion.accept(filaSeleccionada);
            });
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            return visual;
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            this.filaSeleccionada = table.convertRowIndexToModel(row);
            return editor;
        }

        @Override
        public Object getCellEditorValue() {
            return visual.getText();
        }
    }

    /**
     * Renderer y Editor para columna con dos botones: [Ver] y [Descargar PDF]
     */
    public static void instalarDobleBotonColumna(JTable tabla, int columna,
                                                IntConsumer accionVer, IntConsumer accionDescargar) {
        DobleBotonRendererEditor handler = new DobleBotonRendererEditor(tabla, accionVer, accionDescargar);
        tabla.getColumnModel().getColumn(columna).setCellRenderer(handler);
        tabla.getColumnModel().getColumn(columna).setCellEditor(handler);
    }

    private static final class DobleBotonRendererEditor extends AbstractCellEditor
            implements TableCellRenderer, TableCellEditor {
        private static final long serialVersionUID = 1L;
        private final JPanel panelVisual = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 2));
        private final JPanel panelEditor = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 2));
        private final JButton btnVerVisual = boton("Ver", false);
        private final JButton btnDescargarVisual = boton("Descargar PDF", false);
        private final JButton btnVerEditor = boton("Ver", false);
        private final JButton btnDescargarEditor = boton("Descargar PDF", false);
        private final JTable tabla;
        private int filaSeleccionada;

        DobleBotonRendererEditor(JTable tabla, IntConsumer onVer, IntConsumer onDescargar) {
            this.tabla = tabla;
            panelVisual.setOpaque(false);
            panelEditor.setOpaque(false);

            panelVisual.add(btnVerVisual);
            panelVisual.add(btnDescargarVisual);

            panelEditor.add(btnVerEditor);
            panelEditor.add(btnDescargarEditor);

            btnVerEditor.addActionListener(e -> {
                fireEditingStopped();
                onVer.accept(filaSeleccionada);
            });

            btnDescargarEditor.addActionListener(e -> {
                fireEditingStopped();
                onDescargar.accept(filaSeleccionada);
            });
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            return panelVisual;
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            this.filaSeleccionada = table.convertRowIndexToModel(row);
            return panelEditor;
        }

        @Override
        public Object getCellEditorValue() {
            return "";
        }
    }
}
