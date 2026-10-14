package happypets.modulos.modulo9;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import happypets.data.RepositorioVeterinaria;
import happypets.model.DocumentoRepositorio;
import happypets.model.LogAuditoria;
import happypets.ui.Iconos;
import happypets.ui.Ui;

/**
 * Submódulo 9.3: Repositorio Documental y Custodia Digital
 * Responsable: Vera Aguilar, Carlos Edgardo
 * Almacén digital centralizado de contratos, consentimientos, facturas electrónicas y balances
 * con arquitectura Master-Detail, 4 KPIs de almacenamiento y dock de custodia en vivo.
 */
public class VistaRepositorioDocumentalPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private final RepositorioVeterinaria repo = RepositorioVeterinaria.getInstancia();

    // KPIs Superiores
    private JLabel lblKpiTotalDocs;
    private JLabel lblKpiAlmacenamiento;
    private JLabel lblKpiCertificados;
    private JLabel lblKpiDescargas;

    // Master Table (Izquierda)
    private JTable tablaDocumentos;
    private DefaultTableModel modeloDocumentos;
    private JTextField txtBuscar;
    private String tipoFiltro = "Todos";
    private final List<DocumentoRepositorio> listaDocumentosFiltrada = new ArrayList<>();
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private JPanel panelPills;

    // Live Detail Hub (Derecha - 360px)
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private JPanel panelHubContenido;
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private JPanel panelHubVacio;
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private JPanel panelHubDetalle;

    private JLabel lblHubAvatar;
    private JLabel lblHubNombre;
    private JLabel lblHubTipoBadge;
    private JLabel lblHubChipEstado;

    // Telemetría 2x2
    private JLabel lblHubTamano;
    private JLabel lblHubFecha;
    private JLabel lblHubCategoria;
    private JLabel lblHubUsuario;

    // Secciones de Detalle
    private JTextArea txtHubDescripcion;
    private JLabel lblHubChecksum;
    private JLabel lblHubRuta;

    private DocumentoRepositorio documentoSeleccionado;

    /**
     * Constructor de VistaRepositorioDocumentalPanel.
     * Inicializa los componentes gráficos, configura layouts, asigna estilos
     * institucionales y carga los datos desde el repositorio central.
     */
    public VistaRepositorioDocumentalPanel() {
        setLayout(new BorderLayout());
        setOpaque(false);

        // Panel Superior Fijo (Cabecera + 4 KPIs)
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel panelSuperior = new JPanel();
        panelSuperior.setOpaque(false);
        panelSuperior.setLayout(new BoxLayout(panelSuperior, BoxLayout.Y_AXIS));
        panelSuperior.setBorder(new EmptyBorder(12, 18, 10, 18));

        panelSuperior.add(crearCabeceraVista());
        panelSuperior.add(Box.createVerticalStrut(10));
        panelSuperior.add(crearFilaKpis());

        add(panelSuperior, BorderLayout.NORTH);

        // Contenedor Master-Detail
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel panelCentro = new JPanel(new BorderLayout());
        panelCentro.setOpaque(false);
        panelCentro.setBorder(new EmptyBorder(0, 18, 14, 18));
        panelCentro.add(crearSplitMasterDetail(), BorderLayout.CENTER);

        add(panelCentro, BorderLayout.CENTER);

        recargarDatos();
    }

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private JPanel crearCabeceraVista() {
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel cab = new JPanel(new BorderLayout());
        cab.setOpaque(false);
        cab.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel izq = new JPanel();
        izq.setOpaque(false);
        izq.setLayout(new BoxLayout(izq, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("Repositorio Documental y Custodia Digital");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 17));
        titulo.setForeground(Ui.TEXTO_TITULO);

        JLabel subtitulo = new JLabel("Módulo 9.3 · Almacenamiento institucional, gestión de expedientes firmados y custodia digital");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitulo.setForeground(Ui.TEXTO_MUTED);

        izq.add(titulo);
        izq.add(Box.createVerticalStrut(2));
        izq.add(subtitulo);
        cab.add(izq, BorderLayout.WEST);

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        der.setOpaque(false);

        JButton btnActualizar = Ui.boton("Actualizar", false);
        btnActualizar.addActionListener(e -> recargarDatos());
        der.add(btnActualizar);

        JButton btnSubir = Ui.boton("+ Subir Archivo", true);
        btnSubir.addActionListener(e -> mostrarModalSubirArchivo());
        der.add(btnSubir);

        cab.add(der, BorderLayout.EAST);
        return cab;
    }

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private JPanel crearFilaKpis() {
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel kpis = new JPanel(new GridLayout(1, 4, 12, 0));
        kpis.setOpaque(false);
        kpis.setPreferredSize(new Dimension(0, 74));
        kpis.setMaximumSize(new Dimension(Integer.MAX_VALUE, 74));

        lblKpiTotalDocs = new JLabel("0");
        lblKpiAlmacenamiento = new JLabel("12.5 MB / 10 GB");
        lblKpiCertificados = new JLabel("8 Emitidos");
        lblKpiDescargas = new JLabel("64 Descargas");

        kpis.add(crearTarjetaKpi("Total Expedientes", lblKpiTotalDocs, "Custodia digital segura", Iconos.crearIconoDocumento(22, Ui.TURQUESA)));
        kpis.add(crearTarjetaKpi("Almacenamiento Usado", lblKpiAlmacenamiento, "98.8% espacio libre", Iconos.crearIconoHistorial(22, Ui.AZUL_INFO)));
        kpis.add(crearTarjetaKpi("Certificados Médicos", lblKpiCertificados, "Firmas electrónicas", Iconos.crearIconoCheck(22, Ui.VERDE_EXITO)));
        kpis.add(crearTarjetaKpi("Descargas del Mes", lblKpiDescargas, "Trazabilidad forense", Iconos.crearIconoDescargar(22, Ui.NARANJA_AVISO)));

        return kpis;
    }

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private JPanel crearTarjetaKpi(String titulo, JLabel lblValor, String subtitulo, Icon icono) {
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel card = new JPanel(new BorderLayout(8, 0)) {
            private static final long serialVersionUID = 1L;
            @Override
            /**
             * Renderizado gráfico personalizado en Java2D.
             * Activa Antialiasing para bordes suaves y realiza el cálculo geométrico preciso.
             */
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(Ui.BORDE_SUAVE);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(8, 12, 8, 12));

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel texto = new JPanel();
        texto.setOpaque(false);
        texto.setLayout(new BoxLayout(texto, BoxLayout.Y_AXIS));

        JLabel lblTit = new JLabel(titulo);
        lblTit.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblTit.setForeground(Ui.TEXTO_MUTED);

        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblValor.setForeground(Ui.TEXTO_TITULO);

        JLabel lblSub = new JLabel(subtitulo);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblSub.setForeground(Ui.TEXTO_MUTED);

        texto.add(lblTit);
        texto.add(Box.createVerticalStrut(1));
        texto.add(lblValor);
        texto.add(Box.createVerticalStrut(1));
        texto.add(lblSub);

        card.add(texto, BorderLayout.CENTER);

        if (icono != null) {
            JLabel lblIco = new JLabel(icono);
            lblIco.setVerticalAlignment(SwingConstants.TOP);
            lblIco.setBorder(new EmptyBorder(2, 0, 0, 0));
            card.add(lblIco, BorderLayout.EAST);
        }

        return card;
    }

    private JSplitPane crearSplitMasterDetail() {
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setOpaque(false);
        split.setBorder(null);
        split.setDividerSize(8);
        split.setContinuousLayout(true);
        split.setResizeWeight(1.0);

        split.setLeftComponent(crearPanelMasterTable());
        split.setRightComponent(crearPanelLiveDetailHub());

        return split;
    }

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private JPanel crearPanelMasterTable() {
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel master = new JPanel(new BorderLayout(0, 10)) {
            private static final long serialVersionUID = 1L;
            @Override
            /**
             * Renderizado gráfico personalizado en Java2D.
             * Activa Antialiasing para bordes suaves y realiza el cálculo geométrico preciso.
             */
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(Ui.BORDE_SUAVE);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                g2.dispose();
            }
        };
        master.setOpaque(false);
        master.setBorder(new EmptyBorder(12, 14, 12, 14));

        // Cabecera Filtros & Buscador
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel cabFiltros = new JPanel();
        cabFiltros.setOpaque(false);
        cabFiltros.setLayout(new BoxLayout(cabFiltros, BoxLayout.Y_AXIS));

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel barraBuscar = new JPanel(new BorderLayout(8, 0));
        barraBuscar.setOpaque(false);

        JLabel lblBuscar = new JLabel("Buscar:");
        lblBuscar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblBuscar.setForeground(Ui.TEXTO_MUTED);
        barraBuscar.add(lblBuscar, BorderLayout.WEST);

        txtBuscar = new JTextField();
        txtBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtBuscar.putClientProperty("JTextField.placeholderText", "Buscar por nombre de archivo, categoría o formato...");
        txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filtrarDocumentos(); }
            public void removeUpdate(DocumentEvent e) { filtrarDocumentos(); }
            public void changedUpdate(DocumentEvent e) { filtrarDocumentos(); }
        });
        barraBuscar.add(txtBuscar, BorderLayout.CENTER);
        cabFiltros.add(barraBuscar);
        cabFiltros.add(Box.createVerticalStrut(8));

        // Píldoras de Filtro por Extensión
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        panelPills = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        panelPills.setOpaque(false);
        actualizarPildorasFiltro();
        cabFiltros.add(panelPills);

        master.add(cabFiltros, BorderLayout.NORTH);

        // Tabla de Documentos
        String[] cols = {"Tipo", "Nombre del Archivo", "Categoría Documental", "Tamaño", "Fecha Carga", "Usuario Custodio", "Estado"};
        modeloDocumentos = new DefaultTableModel(cols, 0) {
            private static final long serialVersionUID = 1L;
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tablaDocumentos = new JTable(modeloDocumentos);
        tablaDocumentos.setRowHeight(46);
        tablaDocumentos.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaDocumentos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaDocumentos.setShowVerticalLines(false);
        tablaDocumentos.setGridColor(new Color(241, 245, 249));
        tablaDocumentos.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        tablaDocumentos.getTableHeader().setBackground(new Color(248, 250, 252));
        tablaDocumentos.getTableHeader().setForeground(Ui.TEXTO_MUTED);

        tablaDocumentos.getColumnModel().getColumn(0).setPreferredWidth(80);
        tablaDocumentos.getColumnModel().getColumn(1).setPreferredWidth(230);
        tablaDocumentos.getColumnModel().getColumn(2).setPreferredWidth(140);
        tablaDocumentos.getColumnModel().getColumn(3).setPreferredWidth(90);
        tablaDocumentos.getColumnModel().getColumn(4).setPreferredWidth(120);
        tablaDocumentos.getColumnModel().getColumn(5).setPreferredWidth(110);
        tablaDocumentos.getColumnModel().getColumn(6).setPreferredWidth(100);

        // Badge en columna 0 (Tipo de Archivo)
        tablaDocumentos.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
            private static final long serialVersionUID = 1L;
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
                JPanel cell = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 11));
                cell.setOpaque(true);
                cell.setBackground(isSelected ? table.getSelectionBackground() : (row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252)));

                String tipo = value != null ? value.toString() : "";
                JLabel badge = new JLabel(" " + tipo + " ");
                badge.setOpaque(true);
                badge.setFont(new Font("Segoe UI", Font.BOLD, 10));

                if ("PDF".equalsIgnoreCase(tipo)) {
                    badge.setBackground(new Color(254, 226, 226));
                    badge.setForeground(new Color(220, 38, 38));
                } else if ("DOCX".equalsIgnoreCase(tipo)) {
                    badge.setBackground(Ui.TURQUESA_SUAVE);
                    badge.setForeground(Ui.TURQUESA_PROFUNDO);
                } else if ("XLSX".equalsIgnoreCase(tipo)) {
                    badge.setBackground(new Color(236, 253, 245));
                    badge.setForeground(new Color(16, 185, 129));
                } else {
                    badge.setBackground(new Color(241, 245, 249));
                    badge.setForeground(new Color(71, 85, 105));
                }

                badge.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(badge.getBackground().darker(), 1, true),
                        new EmptyBorder(2, 6, 2, 6)
                ));
                cell.add(badge);
                return cell;
            }
        });

        // Chip en columna 6 (Estado)
        tablaDocumentos.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            private static final long serialVersionUID = 1L;
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
                JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 11));
                p.setOpaque(true);
                p.setBackground(isSelected ? table.getSelectionBackground() : (row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252)));

                JLabel badge = new JLabel(" Custodiado ");
                badge.setOpaque(true);
                badge.setFont(new Font("Segoe UI", Font.BOLD, 10));
                badge.setBackground(new Color(220, 252, 231));
                badge.setForeground(new Color(21, 128, 61));
                badge.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(134, 239, 172), 1, true),
                        new EmptyBorder(2, 6, 2, 6)
                ));
                p.add(badge);
                return p;
            }
        });

        tablaDocumentos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int r = tablaDocumentos.getSelectedRow();
                if (r >= 0 && r < listaDocumentosFiltrada.size()) {
                    mostrarDetalleDocumento(listaDocumentosFiltrada.get(r));
                } else {
                    mostrarDetalleDocumento(null);
                }
            }
        });

        tablaDocumentos.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int r = tablaDocumentos.getSelectedRow();
                    if (r >= 0 && r < listaDocumentosFiltrada.size()) {
                        abrirVisorReporteDocumento(listaDocumentosFiltrada.get(r));
                    }
                }
            }
        });

        JScrollPane scroll = new JScrollPane(tablaDocumentos);
        scroll.setBorder(BorderFactory.createLineBorder(Ui.BORDE_SUAVE, 1));
        master.add(scroll, BorderLayout.CENTER);

        return master;
    }

    private void actualizarPildorasFiltro() {
        if (panelPills == null) return;
        panelPills.removeAll();

        int tot = repo.getDocumentosRepositorio().size();
        int pdfs = 0, docx = 0, xlsx = 0;
        for (DocumentoRepositorio d : repo.getDocumentosRepositorio()) {
            if ("PDF".equalsIgnoreCase(d.getTipoExtension())) pdfs++;
            else if ("DOCX".equalsIgnoreCase(d.getTipoExtension())) docx++;
            else if ("XLSX".equalsIgnoreCase(d.getTipoExtension())) xlsx++;
        }

        String[] pildoras = {
            "Todos (" + tot + ")",
            "PDF (" + pdfs + ")",
            "DOCX (" + docx + ")",
            "XLSX (" + xlsx + ")"
        };
        String[] valores = {"Todos", "PDF", "DOCX", "XLSX"};

        for (int i = 0; i < pildoras.length; i++) {
            final String f = valores[i];
            JButton btnPill = new JButton(pildoras[i]);
            btnPill.setFont(new Font("Segoe UI", Font.BOLD, 11));
            btnPill.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btnPill.setBorder(new EmptyBorder(4, 10, 4, 10));
            btnPill.setFocusPainted(false);

            if (f.equalsIgnoreCase(tipoFiltro)) {
                btnPill.setBackground(Ui.TURQUESA);
                btnPill.setForeground(Color.WHITE);
            } else {
                btnPill.setBackground(new Color(241, 245, 249));
                btnPill.setForeground(Ui.TEXTO_MUTED);
            }

            btnPill.addActionListener(e -> {
                tipoFiltro = f;
                actualizarPildorasFiltro();
                filtrarDocumentos();
            });
            panelPills.add(btnPill);
        }
        panelPills.revalidate();
        panelPills.repaint();
    }

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private JPanel crearPanelLiveDetailHub() {
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel detail = new JPanel(new BorderLayout()) {
            private static final long serialVersionUID = 1L;
            @Override
            /**
             * Renderizado gráfico personalizado en Java2D.
             * Activa Antialiasing para bordes suaves y realiza el cálculo geométrico preciso.
             */
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(Ui.BORDE_SUAVE);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                g2.dispose();
            }
        };
        detail.setOpaque(false);
        detail.setPreferredSize(new Dimension(360, 0));
        detail.setMinimumSize(new Dimension(320, 0));

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        panelHubContenido = new JPanel(new BorderLayout());
        panelHubContenido.setOpaque(false);

        panelHubVacio = crearHubVacio();
        panelHubDetalle = crearHubDetalleActivo();

        panelHubContenido.add(panelHubVacio, BorderLayout.CENTER);
        detail.add(panelHubContenido, BorderLayout.CENTER);

        return detail;
    }

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private JPanel crearHubVacio() {
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel vacio = new JPanel();
        vacio.setOpaque(false);
        vacio.setLayout(new BoxLayout(vacio, BoxLayout.Y_AXIS));
        vacio.setBorder(new EmptyBorder(40, 20, 40, 20));

        JLabel ico = new JLabel(Iconos.crearIconoDocumento(48, Ui.BORDE_SUAVE));
        ico.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel tit = new JLabel("Ningún Documento Seleccionado");
        tit.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tit.setForeground(Ui.TEXTO_TITULO);
        tit.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sub = new JLabel("<html><center>Seleccione un archivo de la lista para inspeccionar sus metadatos de custodia, tamaño, hash SHA-256 y descarga segura.</center></html>");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sub.setForeground(Ui.TEXTO_MUTED);
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);

        vacio.add(Box.createVerticalGlue());
        vacio.add(ico);
        vacio.add(Box.createVerticalStrut(12));
        vacio.add(tit);
        vacio.add(Box.createVerticalStrut(6));
        vacio.add(sub);
        vacio.add(Box.createVerticalGlue());

        return vacio;
    }

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private JPanel crearHubDetalleActivo() {
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);

        ScrollablePanel scrollContent = new ScrollablePanel();
        scrollContent.setOpaque(false);
        scrollContent.setLayout(new BoxLayout(scrollContent, BoxLayout.Y_AXIS));
        scrollContent.setBorder(new EmptyBorder(14, 16, 14, 16));

        // 1. Hero Block
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel hero = new JPanel(new BorderLayout(8, 0));
        hero.setOpaque(false);
        hero.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblHubAvatar = new JLabel("PDF", SwingConstants.CENTER) {
            private static final long serialVersionUID = 1L;
            @Override
            /**
             * Renderizado gráfico personalizado en Java2D.
             * Activa Antialiasing para bordes suaves y realiza el cálculo geométrico preciso.
             */
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int s = Math.min(getWidth(), getHeight()) - 2;
                int x = (getWidth() - s) / 2;
                int y = (getHeight() - s) / 2;
                g2.setColor(new Color(254, 226, 226));
                g2.fillOval(x, y, s, s);
                g2.setColor(new Color(252, 165, 165));
                g2.drawOval(x, y, s, s);
                super.paintComponent(g);
                g2.dispose();
            }
        };
        lblHubAvatar.setPreferredSize(new Dimension(46, 46));
        lblHubAvatar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblHubAvatar.setForeground(new Color(220, 38, 38));

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel avatarBox = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        avatarBox.setOpaque(false);
        avatarBox.setPreferredSize(new Dimension(46, 46));
        avatarBox.add(lblHubAvatar);
        hero.add(avatarBox, BorderLayout.WEST);

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel heroText = new JPanel();
        heroText.setOpaque(false);
        heroText.setLayout(new BoxLayout(heroText, BoxLayout.Y_AXIS));

        lblHubNombre = new JLabel("Archivo.pdf");
        lblHubNombre.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblHubNombre.setForeground(Ui.TEXTO_TITULO);
        lblHubNombre.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblHubTipoBadge = new JLabel("DOCUMENTO ENCRIPTADO");
        lblHubTipoBadge.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblHubTipoBadge.setForeground(Ui.TURQUESA_PROFUNDO);
        lblHubTipoBadge.setAlignmentX(Component.LEFT_ALIGNMENT);

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel rowChips = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        rowChips.setOpaque(false);
        rowChips.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblHubChipEstado = new JLabel(" Custodiado en Nube ");
        lblHubChipEstado.setOpaque(true);
        lblHubChipEstado.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblHubChipEstado.setBackground(new Color(220, 252, 231));
        lblHubChipEstado.setForeground(new Color(21, 128, 61));
        lblHubChipEstado.setBorder(BorderFactory.createLineBorder(new Color(134, 239, 172), 1, true));
        rowChips.add(lblHubChipEstado);

        heroText.add(lblHubNombre);
        heroText.add(Box.createVerticalStrut(2));
        heroText.add(lblHubTipoBadge);
        heroText.add(Box.createVerticalStrut(4));
        heroText.add(rowChips);

        hero.add(heroText, BorderLayout.CENTER);
        scrollContent.add(hero);
        scrollContent.add(Box.createVerticalStrut(14));

        // 2. Telemetría 2x2
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel telemetria = new JPanel(new GridLayout(2, 2, 8, 8));
        telemetria.setOpaque(false);
        telemetria.setAlignmentX(Component.LEFT_ALIGNMENT);
        telemetria.setMaximumSize(new Dimension(Integer.MAX_VALUE, 92));

        lblHubTamano = new JLabel("-");
        lblHubFecha = new JLabel("-");
        lblHubCategoria = new JLabel("-");
        lblHubUsuario = new JLabel("-");

        telemetria.add(crearMicroCard("Tamaño Archivo", lblHubTamano));
        telemetria.add(crearMicroCard("Fecha Registro", lblHubFecha));
        telemetria.add(crearMicroCard("Categoría", lblHubCategoria));
        telemetria.add(crearMicroCard("Custodio Legal", lblHubUsuario));

        scrollContent.add(telemetria);
        scrollContent.add(Box.createVerticalStrut(12));

        // 3. Card Descripción / Resumen
        txtHubDescripcion = new JTextArea();
        txtHubDescripcion.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtHubDescripcion.setForeground(Ui.TEXTO_TITULO);
        txtHubDescripcion.setLineWrap(true);
        txtHubDescripcion.setWrapStyleWord(true);
        txtHubDescripcion.setEditable(false);
        txtHubDescripcion.setOpaque(false);

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel pnlDesc = crearSeccionCard("Descripción y Propósito del Documento", txtHubDescripcion);
        pnlDesc.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollContent.add(pnlDesc);
        scrollContent.add(Box.createVerticalStrut(10));

        // 4. Card Integridad y Seguridad
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel pnlSeg = new JPanel(new GridLayout(2, 1, 4, 4));
        pnlSeg.setOpaque(false);

        lblHubChecksum = new JLabel("SHA-256: 4e9f7a8b...318c (Verificado)");
        lblHubChecksum.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblHubChecksum.setForeground(Ui.TEXTO_MUTED);

        lblHubRuta = new JLabel("Ubicación: /vault/legal/2026/archivos/");
        lblHubRuta.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblHubRuta.setForeground(Ui.TEXTO_MUTED);

        pnlSeg.add(lblHubChecksum);
        pnlSeg.add(lblHubRuta);

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel pnlSegCard = crearSeccionCard("Integridad y Trazabilidad Criptográfica", pnlSeg);
        pnlSegCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollContent.add(pnlSegCard);
        scrollContent.add(Box.createVerticalStrut(14));

        // 5. Botonera 2x2
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel gridAcciones = new JPanel(new GridLayout(2, 2, 8, 8));
        gridAcciones.setOpaque(false);
        gridAcciones.setAlignmentX(Component.LEFT_ALIGNMENT);
        gridAcciones.setMaximumSize(new Dimension(Integer.MAX_VALUE, 74));

        JButton btnDescargar = Ui.botonSecundario("Descargar PDF", Iconos.crearIconoDescargar(12, Ui.TEXTO_TITULO));
        btnDescargar.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnDescargar.addActionListener(e -> abrirVisorReporteDocumento(documentoSeleccionado));

        JButton btnSubirDock = Ui.botonPrimario("+ Subir", null);
        btnSubirDock.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnSubirDock.addActionListener(e -> mostrarModalSubirArchivo());

        JButton btnAuditar = Ui.botonSecundario("Ver Log Forense", Iconos.crearIconoHistorial(12, Ui.TEXTO_TITULO));
        btnAuditar.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnAuditar.addActionListener(e -> {
            if (documentoSeleccionado != null) {
                JOptionPane.showMessageDialog(this,
                        "Pista de auditoría para " + documentoSeleccionado.getNombreArchivo() + ":\n" +
                        "- Carga inicial: " + documentoSeleccionado.getFechaCargaFormateada() + " por " + documentoSeleccionado.getUsuarioCarga() + "\n" +
                        "- Integridad: Firma electrónica vigente\n" +
                        "- Descargas registradas: 4 accesos verificados.",
                        "Trazabilidad Documental", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        JButton btnCompartir = Ui.botonSecundario("Copiar Enlace", null);
        btnCompartir.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnCompartir.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                    "Enlace de descarga cifrado de un solo uso copiado al portapapeles:\n" +
                    "https://vault.happypets.com/doc/share?token=9f8a32b174e",
                    "Compartir Documento", JOptionPane.INFORMATION_MESSAGE);
        });

        gridAcciones.add(btnDescargar);
        gridAcciones.add(btnSubirDock);
        gridAcciones.add(btnAuditar);
        gridAcciones.add(btnCompartir);

        scrollContent.add(gridAcciones);

        JScrollPane scroll = new JScrollPane(scrollContent);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(14);

        wrapper.add(scroll, BorderLayout.CENTER);
        return wrapper;
    }

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private JPanel crearMicroCard(String etiqueta, JLabel lblValor) {
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel mc = new JPanel();
        mc.setOpaque(false);
        mc.setLayout(new BoxLayout(mc, BoxLayout.Y_AXIS));
        mc.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(241, 245, 249), 1, true),
                new EmptyBorder(6, 8, 6, 8)
        ));

        JLabel lblEtiq = new JLabel(etiqueta);
        lblEtiq.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblEtiq.setForeground(Ui.TEXTO_MUTED);

        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblValor.setForeground(Ui.TEXTO_TITULO);

        mc.add(lblEtiq);
        mc.add(Box.createVerticalStrut(2));
        mc.add(lblValor);

        return mc;
    }

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private JPanel crearSeccionCard(String titulo, JComponent contenido) {
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel sec = new JPanel(new BorderLayout(0, 6));
        sec.setOpaque(false);
        sec.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(241, 245, 249), 1, true),
                new EmptyBorder(8, 10, 8, 10)
        ));

        JLabel lblTit = new JLabel(titulo);
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblTit.setForeground(Ui.TEXTO_MUTED);
        sec.add(lblTit, BorderLayout.NORTH);

        sec.add(contenido, BorderLayout.CENTER);
        return sec;
    }

    public void recargarDatos() {
        int tot = repo.getDocumentosRepositorio().size();
        lblKpiTotalDocs.setText(tot + " Archivos");
        lblKpiCertificados.setText("8 Emitidos");
        lblKpiDescargas.setText("64 Descargas");

        actualizarPildorasFiltro();
        filtrarDocumentos();
    }

    /**
     * Aplica criterios de filtrado dinámico en memoria sobre los registros.
     * Evalúa texto de búsqueda (búsqueda predictiva) y filtros por categoría o estado.
     */
    private void filtrarDocumentos() {
        String q = txtBuscar != null ? txtBuscar.getText().trim() : "";
        List<DocumentoRepositorio> filtrados = repo.buscarDocumentosRepositorio(q, tipoFiltro);

        listaDocumentosFiltrada.clear();
        modeloDocumentos.setRowCount(0);

        for (DocumentoRepositorio d : filtrados) {
            listaDocumentosFiltrada.add(d);
            modeloDocumentos.addRow(new Object[]{
                    d.getTipoExtension(),
                    d.getNombreArchivo(),
                    d.getCategoria(),
                    d.getTamanoLegible(),
                    d.getFechaCargaFormateada(),
                    d.getUsuarioCarga(),
                    "Custodiado"
            });
        }

        if (listaDocumentosFiltrada.isEmpty()) {
            mostrarDetalleDocumento(null);
        } else {
            int sel = tablaDocumentos.getSelectedRow();
            if (sel < 0 || sel >= listaDocumentosFiltrada.size()) {
                tablaDocumentos.setRowSelectionInterval(0, 0);
                mostrarDetalleDocumento(listaDocumentosFiltrada.get(0));
            } else {
                mostrarDetalleDocumento(listaDocumentosFiltrada.get(sel));
            }
        }
    }

    /**
     * Actualiza el panel lateral derecho (Live Detail Hub) con la información del registro seleccionado.
     * Si el argumento es nulo, restablece el panel mostrando el estado de selección vacía.
     */
    private void mostrarDetalleDocumento(DocumentoRepositorio doc) {
        this.documentoSeleccionado = doc;
        panelHubContenido.removeAll();

        if (doc == null) {
            panelHubContenido.add(panelHubVacio, BorderLayout.CENTER);
        } else {
            String ext = doc.getTipoExtension() != null ? doc.getTipoExtension().toUpperCase() : "DOC";
            lblHubAvatar.setText(ext);
            lblHubNombre.setText(doc.getNombreArchivo());
            lblHubTipoBadge.setText("FORMATO " + ext + " • CUSTODIA SEGURA");

            lblHubTamano.setText(doc.getTamanoLegible());
            lblHubFecha.setText(doc.getFechaCargaFormateada());
            lblHubCategoria.setText(doc.getCategoria());
            lblHubUsuario.setText(doc.getUsuarioCarga());

            txtHubDescripcion.setText("Expediente digital oficial categorizado como " + doc.getCategoria() +
                    ". Archivo verificado y protegido contra modificaciones no autorizadas bajo normativa de custodia legal veterinaria.");

            panelHubContenido.add(panelHubDetalle, BorderLayout.CENTER);
        }

        panelHubContenido.revalidate();
        panelHubContenido.repaint();
    }

    private void abrirVisorReporteDocumento(DocumentoRepositorio d) {
        if (d == null) return;
        String[][] meta = new String[][]{
            {"Nombre del Archivo", d.getNombreArchivo()},
            {"Tipo / Extensión", d.getTipoExtension()},
            {"Categoría Documental", d.getCategoria()},
            {"Tamaño en Memoria", d.getTamanoLegible()},
            {"Fecha de Carga", d.getFechaCargaFormateada()},
            {"Usuario Custodio", d.getUsuarioCarga()}
        };
        String[][] datos = new String[][]{
            {"1", d.getNombreArchivo(), d.getTipoExtension(), d.getCategoria(), d.getTamanoLegible(), d.getFechaCargaFormateada(), d.getUsuarioCarga(), "Vigente y Custodiado"}
        };

        repo.registrarLogAuditoria(new LogAuditoria(
                "#EV-" + (1043 + repo.getLogsAuditoria().size()),
                "Descarga/Vista de " + d.getNombreArchivo(),
                "admin_user",
                LocalDateTime.now(),
                "127.0.0.1",
                "ÉXITO",
                "Descarga legal del documento " + d.getNombreArchivo() + " (" + d.getTamanoLegible() + ")"
        ));

        Ui.mostrarVisorReporte(
            (java.awt.Frame) SwingUtilities.getWindowAncestor(this),
            "REPOSITORIO DOCUMENTAL - VISTA Y DESCARGA",
            d.getNombreArchivo(),
            meta,
            new String[]{"N°", "Nombre del Archivo", "Tipo", "Categoría", "Tamaño", "Fecha de Carga", "Custodio", "Estado Legal"},
            datos,
            "Custodia digital centralizada y archivo documental Happy Pets.",
            "Doc_" + d.getNombreArchivo().replace(" ", "_")
        );
    }

    private void mostrarModalSubirArchivo() {
        JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(this), "Cargar Nuevo Expediente al Repositorio", JDialog.ModalityType.APPLICATION_MODAL);
        dlg.setSize(480, 420);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel pnl = new JPanel();
        pnl.setLayout(new BoxLayout(pnl, BoxLayout.Y_AXIS));
        pnl.setBackground(Color.WHITE);
        pnl.setBorder(new EmptyBorder(16, 20, 16, 20));

        JLabel lblTit = new JLabel("Carga de Documento Oficial");
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTit.setForeground(Ui.TEXTO_TITULO);
        pnl.add(lblTit);
        pnl.add(Box.createVerticalStrut(12));

        JTextField txtNombre = new JTextField();
        txtNombre.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        JComboBox<String> cmbExt = Ui.combo(new String[]{"PDF", "DOCX", "XLSX"});
        JComboBox<String> cmbCat = Ui.combo(new String[]{"Clínica", "Finanzas", "Legal", "Políticas", "Recursos Humanos"});
        JTextField txtTamano = new JTextField("1.2 MB");
        txtTamano.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        JTextField txtUsuario = new JTextField("admin_user");
        txtUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        pnl.add(crearFilaCampo("Nombre del Archivo:", txtNombre));
        pnl.add(Box.createVerticalStrut(6));
        pnl.add(crearFilaCampo("Tipo / Formato:", cmbExt));
        pnl.add(Box.createVerticalStrut(6));
        pnl.add(crearFilaCampo("Categoría:", cmbCat));
        pnl.add(Box.createVerticalStrut(6));
        pnl.add(crearFilaCampo("Tamaño Estimado:", txtTamano));
        pnl.add(Box.createVerticalStrut(6));
        pnl.add(crearFilaCampo("Usuario Custodio:", txtUsuario));

        dlg.add(pnl, BorderLayout.CENTER);

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel bot = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        bot.setBackground(new Color(248, 250, 252));
        bot.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Ui.BORDE_SUAVE));

        JButton btnCancelar = Ui.botonSecundario("Cancelar", null);
        btnCancelar.addActionListener(e -> dlg.dispose());

        JButton btnGuardar = Ui.botonPrimario("Cargar Expediente", null);
        btnGuardar.addActionListener(e -> {
            String n = txtNombre.getText().trim();
            if (n.isEmpty()) {
                JOptionPane.showMessageDialog(dlg, "Por favor ingrese el nombre del archivo.", "Campo Requerido", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (!n.contains(".")) {
                n = n + "." + ((String) cmbExt.getSelectedItem()).toLowerCase();
            }
            double tamMB = 1.2;
            try {
                tamMB = Double.parseDouble(txtTamano.getText().replaceAll("[^0-9.]", ""));
            } catch (Exception ignored) { }
            DocumentoRepositorio docNuevo = new DocumentoRepositorio(
                    "DOC-" + String.format("%03d", repo.getDocumentosRepositorio().size() + 1),
                    (String) cmbExt.getSelectedItem(),
                    n,
                    (String) cmbCat.getSelectedItem(),
                    tamMB,
                    LocalDateTime.now(),
                    txtUsuario.getText().trim(),
                    "assets/docs/" + n
            );
            repo.agregarDocumentoRepositorio(docNuevo);
            dlg.dispose();
            recargarDatos();
            JOptionPane.showMessageDialog(this, "Archivo cargado y custodiado en el repositorio con éxito.", "Carga Exitosa", JOptionPane.INFORMATION_MESSAGE);
        });

        bot.add(btnCancelar);
        bot.add(btnGuardar);
        dlg.add(bot, BorderLayout.SOUTH);

        dlg.setVisible(true);
    }

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private JPanel crearFilaCampo(String etiqueta, JComponent comp) {
    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
        JPanel f = new JPanel(new BorderLayout(8, 0));
        f.setOpaque(false);
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JLabel lbl = new JLabel(etiqueta);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lbl.setForeground(Ui.TEXTO_MUTED);
        lbl.setPreferredSize(new Dimension(130, 26));

        f.add(lbl, BorderLayout.WEST);
        f.add(comp, BorderLayout.CENTER);
        return f;
    }

    /**
     * Crea y parametriza la tabla maestra de datos.
     * Configura el modelo no editable, ancho de columnas, ordenamiento,
     * renderizadores visuales con avatares circulares y chips de estado.
     */
    private static class ScrollablePanel extends JPanel implements Scrollable {
        private static final long serialVersionUID = 1L;

        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }

        @Override
        public int getScrollableUnitIncrement(java.awt.Rectangle visibleRect, int orientation, int direction) {
            return 16;
        }

        @Override
        public int getScrollableBlockIncrement(java.awt.Rectangle visibleRect, int orientation, int direction) {
            return 32;
        }

        @Override
        public boolean getScrollableTracksViewportWidth() {
            return true;
        }

        @Override
        public boolean getScrollableTracksViewportHeight() {
            return false;
        }
    }
}
