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
import java.awt.Window;
import java.awt.image.BufferedImage;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.IntConsumer;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

/**
 * Constantes y componentes visuales reutilizables que aseguran
 * fidelidad visual y estandarización exacta con la paleta de color Turquesa Clínico Original
 * (#00BCD4 / #0096A9) de Happy Pets a través de todos los módulos del sistema (1 al 10).
 */
public final class Ui {
    // Paleta Corporativa Institucional Turquesa Clínico Original
    public static final Color TURQUESA = new Color(0, 188, 212);            // #00BCD4 Primario oficial
    public static final Color TURQUESA_MEDIO = new Color(77, 208, 225);     // #4DD0E1 Borde medio
    public static final Color TURQUESA_OSCURO = new Color(0, 150, 169);     // #0096A9 Hover y bordes primarios
    public static final Color TURQUESA_PROFUNDO = new Color(0, 121, 135);   // #007987 Textos destacados y headers
    public static final Color TURQUESA_SUAVE = new Color(224, 247, 250);    // #E0F7FA Pills, selección y fondos KPI

    // Colores de Estructura y Fondo
    public static final Color FONDO = new Color(248, 250, 252);             // #F8FAFC Fondo pizarra limpio
    public static final Color FONDO_CARD = Color.WHITE;
    public static final Color BORDE_SUAVE = new Color(226, 232, 240);       // #E2E8F0 Bordes contenedores
    public static final Color BORDE_INPUT = new Color(203, 213, 225);       // #CBD5E1 Bordes formularios

    // Jerarquía Tipográfica
    public static final Color TEXTO_TITULO = new Color(15, 23, 42);         // #0F172A Títulos de pantalla
    public static final Color TEXTO_SUBTITULO = new Color(71, 85, 105);     // #475569 Subtítulos
    public static final Color TEXTO_MUTED = new Color(100, 116, 139);       // #64748B Metadatos y notas
    public static final Color TEXTO_REGULAR = new Color(30, 41, 59);        // #1E293B Contenido

    // Colores de Estado Semántico
    public static final Color COLOR_EXITO = new Color(16, 185, 129);        // #10B981 Verde Esmeralda (Activo/Al día/Pagado)
    public static final Color COLOR_ADVERTENCIA = new Color(245, 158, 11);  // #F59E0B Ámbar (Pendiente/Alerta)
    public static final Color COLOR_PELIGRO = new Color(239, 68, 68);       // #EF4444 Rojo (Crítico/Vencido/Urgente)
    public static final Color ESTADO_ACTIVO = COLOR_EXITO;

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
     * "Happy Pets" y perfil de usuario a la derecha.
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
     * Botón estilizado según sea principal (turquesa) o secundario (blanco con borde).
     */
    public static JButton boton(String texto, boolean principal) {
        Color fondo = principal ? TURQUESA : Color.WHITE;
        Color textoColor = principal ? Color.WHITE : TEXTO_REGULAR;
        Color bordeColor = principal ? TURQUESA_OSCURO : BORDE_INPUT;

        JButton boton = new JButton(texto) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2.setColor(fondo.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(principal ? TURQUESA_OSCURO : new Color(241, 245, 249));
                } else {
                    g2.setColor(fondo);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
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
                new EmptyBorder(7, 14, 7, 14)
        ));
        return boton;
    }

    public static JButton botonPrimario(String texto) {
        return botonPrimario(texto, null);
    }

    public static JButton botonPrimario(String texto, Icon icono) {
        JButton b = boton(texto, true);
        if (icono != null) {
            b.setIcon(icono);
            b.setIconTextGap(6);
        }
        return b;
    }

    public static JButton botonSecundario(String texto) {
        return botonSecundario(texto, null);
    }

    public static JButton botonSecundario(String texto, Icon icono) {
        JButton b = boton(texto, false);
        if (icono != null) {
            b.setIcon(icono);
            b.setIconTextGap(6);
        }
        return b;
    }

    public static JButton botonPeligro(String texto) {
        return botonPeligro(texto, null);
    }

    public static JButton botonPeligro(String texto, Icon icono) {
        JButton b = new JButton(texto) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2.setColor(COLOR_PELIGRO.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(new Color(220, 38, 38));
                } else {
                    g2.setColor(COLOR_PELIGRO);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                super.paintComponent(g);
                g2.dispose();
            }
        };
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setContentAreaFilled(false);
        b.setOpaque(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 38, 38), 1),
                new EmptyBorder(7, 14, 7, 14)
        ));
        if (icono != null) {
            b.setIcon(icono);
            b.setIconTextGap(6);
        }
        return b;
    }

    public static JButton botonExito(String texto) {
        return botonExito(texto, null);
    }

    public static JButton botonExito(String texto, Icon icono) {
        JButton b = new JButton(texto) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2.setColor(COLOR_EXITO.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(new Color(5, 150, 105));
                } else {
                    g2.setColor(COLOR_EXITO);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                super.paintComponent(g);
                g2.dispose();
            }
        };
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setContentAreaFilled(false);
        b.setOpaque(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(5, 150, 105), 1),
                new EmptyBorder(7, 14, 7, 14)
        ));
        if (icono != null) {
            b.setIcon(icono);
            b.setIconTextGap(6);
        }
        return b;
    }

    /**
     * Label estandarizado para campos de formularios.
     */
    public static JLabel crearLabelFormulario(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lbl.setForeground(TEXTO_SUBTITULO);
        return lbl;
    }

    /**
     * Campo de texto con borde uniforme y altura estandarizada (34px).
     */
    public static JTextField campoTexto(int columnas) {
        JTextField tf = new JTextField(columnas);
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tf.setForeground(TEXTO_REGULAR);
        tf.setBackground(Color.WHITE);
        tf.setPreferredSize(new Dimension(tf.getPreferredSize().width, 34));
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE_INPUT, 1),
                new EmptyBorder(6, 10, 6, 10)
        ));
        return tf;
    }

    public static JTextField campoTexto(String texto, int columnas) {
        JTextField tf = campoTexto(columnas);
        if (texto != null && !texto.isEmpty()) {
            tf.setText(texto);
        }
        return tf;
    }

    public static JTextField campoTexto(String texto) {
        return campoTexto(texto, 15);
    }

    /**
     * JComboBox estandarizado con altura 34px y tipografía coherente.
     */
    public static <T> JComboBox<T> combo(T[] items) {
        JComboBox<T> cb = new JComboBox<>(items);
        cb.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cb.setBackground(Color.WHITE);
        cb.setPreferredSize(new Dimension(cb.getPreferredSize().width, 34));
        return cb;
    }

    /**
     * Panel tarjeta blanca contenedor con borde suave.
     */
    public static JPanel crearTarjeta() {
        JPanel p = new JPanel();
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE_SUAVE, 1),
                new EmptyBorder(14, 16, 14, 16)
        ));
        return p;
    }

    /**
     * Tarjeta KPI estandarizada para todos los módulos.
     */
    public static JPanel crearTarjetaKPI(String titulo, String valor, String subtitulo, Icon icono) {
        JPanel card = new JPanel(new BorderLayout(12, 0)) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                super.paintComponent(g);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE_SUAVE, 1),
                new EmptyBorder(14, 16, 14, 16)
        ));

        if (icono != null) {
            JPanel badgeIcon = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 8));
            badgeIcon.setOpaque(false);
            badgeIcon.setPreferredSize(new Dimension(44, 44));
            badgeIcon.add(new JLabel(icono));
            card.add(badgeIcon, BorderLayout.WEST);
        }

        JPanel centro = new JPanel();
        centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));
        centro.setOpaque(false);

        JLabel lblTit = new JLabel(titulo.toUpperCase());
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblTit.setForeground(TEXTO_MUTED);

        JLabel lblVal = new JLabel(valor);
        lblVal.setFont(new Font("Segoe UI", Font.BOLD, 19));
        lblVal.setForeground(TURQUESA_PROFUNDO);

        JLabel lblSub = new JLabel(subtitulo);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblSub.setForeground(TEXTO_SUBTITULO);

        centro.add(lblTit);
        centro.add(Box.createVerticalStrut(2));
        centro.add(lblVal);
        centro.add(Box.createVerticalStrut(2));
        centro.add(lblSub);

        card.add(centro, BorderLayout.CENTER);
        return card;
    }

    /**
     * Píldora de estado coloreada (badge).
     */
    public static JLabel crearBadgeEstado(String texto, Color fondo, Color textoColor) {
        JLabel badge = new JLabel("  " + texto + "  ", SwingConstants.CENTER) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(fondo);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        badge.setFont(new Font("Segoe UI", Font.BOLD, 10));
        badge.setForeground(textoColor);
        badge.setPreferredSize(new Dimension(badge.getPreferredSize().width + 8, 22));
        return badge;
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
     * Configuración estándar de tablas Swing:
     * - Altura de fila: 34px
     * - Encabezado: Altura 36px, fondo turquesa suave #E0F7FA o pizarra claro, fuente negrita
     * - Alternancia de filas (Zebra)
     * - Selección Turquesa Suave con texto Turquesa Profundo
     * - Alineaciones uniformes según el tipo de columna.
     */
    public static void formatearTabla(JTable tabla) {
        formatearTabla(tabla, new int[]{0}, new int[]{});
    }

    public static void formatearTabla(JTable tabla, int[] colsCentradas, int[] colsDerecha) {
        tabla.setRowHeight(34);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setGridColor(BORDE_SUAVE);
        tabla.setShowGrid(true);
        tabla.setFillsViewportHeight(true);
        tabla.setSelectionBackground(TURQUESA_SUAVE);
        tabla.setSelectionForeground(TURQUESA_PROFUNDO);

        // Header institucional
        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabla.getTableHeader().setBackground(new Color(241, 245, 249));
        tabla.getTableHeader().setForeground(TEXTO_TITULO);
        tabla.getTableHeader().setPreferredSize(new Dimension(tabla.getColumnModel().getTotalColumnWidth(), 36));
        tabla.getTableHeader().setReorderingAllowed(false);

        // Renderers con alineación estandarizada y alternancia de filas (Zebra)
        DefaultTableCellRenderer renderCentro = new CeldaAlternadaRenderer(SwingConstants.CENTER);
        DefaultTableCellRenderer renderDerecha = new CeldaAlternadaRenderer(SwingConstants.RIGHT);
        DefaultTableCellRenderer renderIzquierda = new CeldaAlternadaRenderer(SwingConstants.LEFT);

        Set<Integer> setCentro = new HashSet<>();
        if (colsCentradas != null) {
            for (int c : colsCentradas) setCentro.add(c);
        }

        Set<Integer> setDerecha = new HashSet<>();
        if (colsDerecha != null) {
            for (int c : colsDerecha) setDerecha.add(c);
        }

        for (int i = 0; i < tabla.getColumnCount(); i++) {
            if (setCentro.contains(i)) {
                tabla.getColumnModel().getColumn(i).setCellRenderer(renderCentro);
            } else if (setDerecha.contains(i)) {
                tabla.getColumnModel().getColumn(i).setCellRenderer(renderDerecha);
            } else {
                tabla.getColumnModel().getColumn(i).setCellRenderer(renderIzquierda);
            }
        }
    }

    private static class CeldaAlternadaRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;
        private final int alineacion;

        CeldaAlternadaRenderer(int alineacion) {
            this.alineacion = alineacion;
            setHorizontalAlignment(alineacion);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (isSelected) {
                c.setBackground(TURQUESA_SUAVE);
                c.setForeground(TURQUESA_PROFUNDO);
            } else {
                c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252));
                c.setForeground(TEXTO_REGULAR);
            }

            if (alineacion == SwingConstants.LEFT) {
                setBorder(new EmptyBorder(0, 10, 0, 4));
            } else if (alineacion == SwingConstants.RIGHT) {
                setBorder(new EmptyBorder(0, 4, 0, 10));
            } else {
                setBorder(new EmptyBorder(0, 4, 0, 4));
            }
            return c;
        }
    }

    /**
     * Muestra el visor de reportes formal en memoria con diseño institucional
     * y opciones para descargar en PDF y Excel (CSV).
     */
    public static void mostrarVisorReporte(Component parent, String titulo, String subtitulo,
                                           String[][] metadatos, String[] columnas,
                                           List<Object[]> filas, String resumen, String prefijoArchivo) {
        Window ventana = parent instanceof Window ? (Window) parent : SwingUtilities.getWindowAncestor(parent);
        VisorReporteDialog visor = new VisorReporteDialog(ventana, titulo, subtitulo, metadatos, columnas, filas, resumen, prefijoArchivo);
        visor.setVisible(true);
    }

    public static void mostrarVisorReporte(Component parent, String titulo, String subtitulo,
                                           String[][] metadatos, String[] columnas,
                                           Object[][] filas, String resumen, String prefijoArchivo) {
        List<Object[]> listaFilas = new java.util.ArrayList<>();
        if (filas != null) {
            for (Object[] row : filas) {
                listaFilas.add(row);
            }
        }
        mostrarVisorReporte(parent, titulo, subtitulo, metadatos, columnas, listaFilas, resumen, prefijoArchivo);
    }

    public static void mostrarVisorReporte(Component parent, String titulo, String subtitulo,
                                           String encabezadoExtra, String[] columnas,
                                           Object[][] filas, String resumen, String prefijoArchivo) {
        String[][] metadatos = new String[][]{
            {"Referencia", subtitulo != null ? subtitulo : ""},
            {"Detalles", encabezadoExtra != null ? encabezadoExtra : ""},
            {"Fecha de Emisión", LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))},
            {"Sistema ERP", "Happy Pets · Gestión Integral"}
        };
        mostrarVisorReporte(parent, titulo, subtitulo, metadatos, columnas, filas, resumen, prefijoArchivo);
    }

    public static void mostrarVisorReporte(Component parent, String titulo, String subtitulo,
                                           String encabezadoExtra, String[] columnas,
                                           List<Object[]> filas, String resumen, String prefijoArchivo) {
        String[][] metadatos = new String[][]{
            {"Referencia", subtitulo != null ? subtitulo : ""},
            {"Detalles", encabezadoExtra != null ? encabezadoExtra : ""},
            {"Fecha de Emisión", LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))},
            {"Sistema ERP", "Happy Pets · Gestión Integral"}
        };
        mostrarVisorReporte(parent, titulo, subtitulo, metadatos, columnas, filas, resumen, prefijoArchivo);
    }

    public static void mostrarVisorReporte(Component parent, String titulo, String subtitulo,
                                           String contenidoTexto, String prefijoArchivo) {
        String[][] metadatos = new String[][]{
            {"Documento", titulo},
            {"Subtítulo / Módulo", subtitulo != null ? subtitulo : "Happy Pets"},
            {"Fecha", LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))},
            {"Validez Legal", "Documento Oficial en Memoria"}
        };
        String[] lineas = contenidoTexto != null ? contenidoTexto.split("\\r?\\n") : new String[0];
        List<Object[]> filas = new java.util.ArrayList<>();
        int num = 1;
        for (String linea : lineas) {
            if (!linea.trim().isEmpty() && !linea.startsWith("===") && !linea.startsWith("---")) {
                filas.add(new Object[]{String.valueOf(num++), linea.trim()});
            }
        }
        mostrarVisorReporte(parent, titulo, subtitulo, metadatos, new String[]{"Línea", "Contenido del Documento"},
                           filas, "DOCUMENTO OFICIAL · VETERINARIA HAPPY PETS", prefijoArchivo);
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
     * Renderer y Editor para botones en una columna de JTable.
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
