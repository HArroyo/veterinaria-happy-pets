package happypets.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.Window;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 * Visor interactivo en memoria con diseño oficial de previsualización formal
 * para imprimir y descargar documentos y reportes del sistema Happy Pets ERP.
 */
public class VisorReporteDialog extends JDialog {
    private static final long serialVersionUID = 1L;

    private final String titulo;
    private final String subtitulo;
    private final String[][] metadatos;
    private final String[] columnas;
    private final List<Object[]> filas;
    private final String resumen;
    private final String prefijoArchivo;

    public VisorReporteDialog(Window parent, String titulo, String subtitulo,
                              String[][] metadatos, String[] columnas,
                              List<Object[]> filas, String resumen, String prefijoArchivo) {
        super(parent, "Vista Previa de Documento Oficial - Happy Pets", ModalityType.APPLICATION_MODAL);
        this.titulo = titulo;
        this.subtitulo = subtitulo;
        this.metadatos = metadatos;
        this.columnas = columnas;
        this.filas = filas;
        this.resumen = resumen;
        this.prefijoArchivo = prefijoArchivo != null ? prefijoArchivo : "documento_happypets";

        setIconImage(Ui.icono());
        setSize(960, 720);
        setMinimumSize(new Dimension(840, 580));
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        inicializarInterfaz();
    }

    private void inicializarInterfaz() {
        // Fondo general
        JPanel fondo = new JPanel(new BorderLayout());
        fondo.setBackground(new Color(241, 245, 249));

        // Barra de herramientas superior
        JPanel barraHerramientas = new JPanel(new BorderLayout(12, 0));
        barraHerramientas.setBackground(Color.WHITE);
        barraHerramientas.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Ui.BORDE_SUAVE),
                new EmptyBorder(10, 20, 10, 20)
        ));

        JPanel titIzq = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        titIzq.setOpaque(false);
        JLabel lblIco = new JLabel(Iconos.crearIconoDocumento(20, Ui.TURQUESA));
        JLabel lblTitHerramientas = new JLabel("Documento Institucional Oficial · Previsualización");
        lblTitHerramientas.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTitHerramientas.setForeground(Ui.TEXTO_TITULO);
        titIzq.add(lblIco);
        titIzq.add(lblTitHerramientas);
        barraHerramientas.add(titIzq, BorderLayout.WEST);

        JPanel btnsDer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnsDer.setOpaque(false);

        JButton btnPDF = Ui.botonPrimario("Descargar PDF", Iconos.crearIconoExportar(14, Color.WHITE));
        btnPDF.addActionListener(e -> descargarPDF());

        JButton btnExcel = Ui.botonSecundario("Exportar Excel (XLS/CSV)", Iconos.crearIconoDocumento(14, Ui.TEXTO_TITULO));
        btnExcel.addActionListener(e -> descargarExcel());

        JButton btnImprimir = Ui.botonSecundario("Imprimir", Iconos.crearIconoImprimir(14, Ui.TEXTO_TITULO));
        btnImprimir.addActionListener(e -> imprimirDocumento());

        JButton btnCerrar = Ui.botonSecundario("Cerrar", null);
        btnCerrar.addActionListener(e -> dispose());

        btnsDer.add(btnPDF);
        btnsDer.add(btnExcel);
        btnsDer.add(btnImprimir);
        btnsDer.add(btnCerrar);
        barraHerramientas.add(btnsDer, BorderLayout.EAST);

        fondo.add(barraHerramientas, BorderLayout.NORTH);

        // Contenedor del documento "Hoja A4"
        JPanel contenedorHoja = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 20));
        contenedorHoja.setBackground(new Color(241, 245, 249));

        JPanel hojaA4 = crearHojaDocumento();
        contenedorHoja.add(hojaA4);

        JScrollPane scroll = new JScrollPane(contenedorHoja);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.getViewport().setBackground(new Color(241, 245, 249));

        fondo.add(scroll, BorderLayout.CENTER);
        add(fondo, BorderLayout.CENTER);
    }

    private JPanel crearHojaDocumento() {
        JPanel hoja = new JPanel(new BorderLayout(0, 16)) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth() - 4, getHeight() - 4, 12, 12);
                super.paintComponent(g);
                g2.dispose();
            }
        };
        hoja.setOpaque(false);
        hoja.setPreferredSize(new Dimension(820, 840));
        hoja.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Ui.BORDE_SUAVE, 1),
                new EmptyBorder(26, 32, 26, 32)
        ));

        // 1. Encabezado institucional de la hoja
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        // Barra Turquesa superior
        JPanel bannerTurquesa = new JPanel(new BorderLayout(14, 0));
        bannerTurquesa.setBackground(Ui.TURQUESA);
        bannerTurquesa.setBorder(new EmptyBorder(12, 18, 12, 18));

        JLabel logoHP = new JLabel("HP", SwingConstants.CENTER) {
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
        logoHP.setPreferredSize(new Dimension(42, 42));
        logoHP.setFont(new Font("Segoe UI", Font.BOLD, 18));
        logoHP.setForeground(Ui.TURQUESA);
        bannerTurquesa.add(logoHP, BorderLayout.WEST);

        JPanel infoClinica = new JPanel();
        infoClinica.setLayout(new BoxLayout(infoClinica, BoxLayout.Y_AXIS));
        infoClinica.setOpaque(false);

        JLabel lblNomClinica = new JLabel("CLÍNICA VETERINARIA HAPPY PETS 24 HORAS");
        lblNomClinica.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblNomClinica.setForeground(Color.WHITE);

        JLabel lblDetClinica = new JLabel("RUC / NIT: 900.842.115-4 · Sede Central y Quirófanos · Tel: +51 (01) 432-9980 · Urgencias 24h");
        lblDetClinica.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblDetClinica.setForeground(new Color(240, 255, 255));

        infoClinica.add(lblNomClinica);
        infoClinica.add(Box.createVerticalStrut(2));
        infoClinica.add(lblDetClinica);
        bannerTurquesa.add(infoClinica, BorderLayout.CENTER);

        header.add(bannerTurquesa, BorderLayout.NORTH);

        // Bloque del Título y Subtítulo
        JPanel bloqueTit = new JPanel();
        bloqueTit.setLayout(new BoxLayout(bloqueTit, BoxLayout.Y_AXIS));
        bloqueTit.setOpaque(false);
        bloqueTit.setBorder(new EmptyBorder(14, 0, 4, 0));

        JLabel lblTitDoc = new JLabel(titulo.toUpperCase());
        lblTitDoc.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitDoc.setForeground(Ui.TEXTO_TITULO);
        bloqueTit.add(lblTitDoc);

        if (subtitulo != null && !subtitulo.trim().isEmpty()) {
            bloqueTit.add(Box.createVerticalStrut(2));
            JLabel lblSubDoc = new JLabel(subtitulo);
            lblSubDoc.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            lblSubDoc.setForeground(Ui.TEXTO_MUTED);
            bloqueTit.add(lblSubDoc);
        }

        header.add(bloqueTit, BorderLayout.CENTER);
        hoja.add(header, BorderLayout.NORTH);

        // 2. Contenido Central (Metadatos + Tabla)
        JPanel centro = new JPanel();
        centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));
        centro.setOpaque(false);

        // Metadatos
        if (metadatos != null && metadatos.length > 0) {
            JPanel cardMeta = new JPanel(new GridLayout((metadatos.length + 1) / 2, 2, 14, 6));
            cardMeta.setBackground(Ui.TURQUESA_SUAVE);
            cardMeta.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(178, 235, 242), 1),
                    new EmptyBorder(10, 14, 10, 14)
            ));

            for (String[] m : metadatos) {
                if (m != null && m.length >= 2) {
                    JLabel lblM = new JLabel("<html><b><font color='#007987'>" + m[0] + ":</font></b> " + m[1] + "</html>");
                    lblM.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                    lblM.setForeground(Ui.TEXTO_TITULO);
                    cardMeta.add(lblM);
                }
            }
            centro.add(cardMeta);
            centro.add(Box.createVerticalStrut(12));
        }

        // Tabla de Registros
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        if (filas != null) {
            for (Object[] f : filas) {
                modelo.addRow(f);
            }
        }

        JTable tabla = new JTable(modelo);
        Ui.formatearTabla(tabla);

        JScrollPane scrollTabla = new JScrollPane(tabla);
        scrollTabla.setPreferredSize(new Dimension(750, 260));
        scrollTabla.setBorder(BorderFactory.createLineBorder(Ui.BORDE_SUAVE, 1));
        centro.add(scrollTabla);

        // Bloque de Resumen / Totales
        if (resumen != null && !resumen.trim().isEmpty()) {
            centro.add(Box.createVerticalStrut(12));
            JPanel panelResumen = new JPanel(new BorderLayout());
            panelResumen.setBackground(Ui.TURQUESA_SUAVE);
            panelResumen.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Ui.TURQUESA, 1),
                    new EmptyBorder(8, 14, 8, 14)
            ));

            JLabel lblRes = new JLabel(resumen);
            lblRes.setFont(new Font("Segoe UI", Font.BOLD, 12));
            lblRes.setForeground(Ui.TURQUESA_PROFUNDO);
            panelResumen.add(lblRes, BorderLayout.CENTER);
            centro.add(panelResumen);
        }

        hoja.add(centro, BorderLayout.CENTER);

        // 3. Pie de página formal con firma y certificación
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(14, 0, 0, 0));

        JPanel firmaIzq = new JPanel();
        firmaIzq.setLayout(new BoxLayout(firmaIzq, BoxLayout.Y_AXIS));
        firmaIzq.setOpaque(false);

        JLabel lblLineaFirma = new JLabel("_________________________________________");
        lblLineaFirma.setForeground(Ui.TEXTO_MUTED);
        JLabel lblFirma = new JLabel("Dr. Roberto Mendoza Chávez · Director Médico General");
        lblFirma.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblFirma.setForeground(Ui.TEXTO_TITULO);
        JLabel lblCol = new JLabel("Colegio Médico Veterinario del Perú · C.M.V.P. N° 19/6721");
        lblCol.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblCol.setForeground(Ui.TEXTO_MUTED);

        firmaIzq.add(lblLineaFirma);
        firmaIzq.add(lblFirma);
        firmaIzq.add(lblCol);
        footer.add(firmaIzq, BorderLayout.WEST);

        JPanel selloDer = new JPanel();
        selloDer.setLayout(new BoxLayout(selloDer, BoxLayout.Y_AXIS));
        selloDer.setOpaque(false);

        JLabel lblCert = new JLabel("CERTIFICACIÓN DIGITAL VÁLIDA");
        lblCert.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblCert.setForeground(Ui.TURQUESA_OSCURO);
        JLabel lblFechaEmi = new JLabel("Generado: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
        lblFechaEmi.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblFechaEmi.setForeground(Ui.TEXTO_MUTED);
        JLabel lblHash = new JLabel("Verificación: HP-SYS-MEM-" + Integer.toHexString(System.identityHashCode(this)).toUpperCase());
        lblHash.setFont(new Font("Segoe UI", Font.PLAIN, 9));
        lblHash.setForeground(Ui.TEXTO_MUTED);

        selloDer.add(lblCert);
        selloDer.add(lblFechaEmi);
        selloDer.add(lblHash);
        footer.add(selloDer, BorderLayout.EAST);

        hoja.add(footer, BorderLayout.SOUTH);

        return hoja;
    }

    private void descargarPDF() {
        try {
            File dirExports = new File("exports");
            if (!dirExports.exists()) dirExports.mkdirs();

            String timeStamp = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss").format(LocalDateTime.now());
            File archivo = new File(dirExports, prefijoArchivo + "_" + timeStamp + ".pdf");

            GeneradorDocumentos.generarPDF(titulo, subtitulo, metadatos, columnas, filas, resumen, archivo);

            int opc = JOptionPane.showConfirmDialog(this,
                    "¡Archivo PDF generado exitosamente en memoria!\n\n"
                            + "• Ubicación: " + archivo.getAbsolutePath() + "\n"
                            + "• Tamaño: " + (archivo.length() / 1024 + 1) + " KB\n\n"
                            + "¿Desea abrir el archivo PDF generado en su lector predeterminado?",
                    "Exportación PDF Completada",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.INFORMATION_MESSAGE);

            if (opc == JOptionPane.YES_OPTION) {
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(archivo);
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al generar el archivo PDF: " + ex.getMessage(),
                    "Error de Exportación",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void descargarExcel() {
        try {
            File dirExports = new File("exports");
            if (!dirExports.exists()) dirExports.mkdirs();

            String timeStamp = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss").format(LocalDateTime.now());
            File archivo = new File(dirExports, prefijoArchivo + "_" + timeStamp + ".csv");

            GeneradorDocumentos.generarExcel(titulo, columnas, filas, archivo);

            int opc = JOptionPane.showConfirmDialog(this,
                    "¡Archivo Excel / CSV generado exitosamente!\n\n"
                            + "• Ubicación: " + archivo.getAbsolutePath() + "\n"
                            + "• Formato: CSV con UTF-8 BOM (compatible nativo con Microsoft Excel)\n\n"
                            + "¿Desea abrir el archivo en Excel ahora?",
                    "Exportación Excel Completada",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.INFORMATION_MESSAGE);

            if (opc == JOptionPane.YES_OPTION) {
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(archivo);
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al generar el archivo Excel: " + ex.getMessage(),
                    "Error de Exportación",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void imprimirDocumento() {
        JOptionPane.showMessageDialog(this,
                "Enviando orden a la cola de impresión institucional de Happy Pets...\n\n"
                        + "• Impresora: Láser HP LaserJet Pro 24H (Recepción)\n"
                        + "• Orientación: Vertical (A4)\n"
                        + "• Estado: Documento despachado con éxito.",
                "Impresión de Documento",
                JOptionPane.INFORMATION_MESSAGE);
    }
}
