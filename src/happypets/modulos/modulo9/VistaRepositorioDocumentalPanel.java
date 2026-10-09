package happypets.modulos.modulo9;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDateTime;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import happypets.data.RepositorioVeterinaria;
import happypets.model.DocumentoRepositorio;
import happypets.ui.Iconos;
import happypets.ui.Ui;

/**
 * Submódulo 9.3: Repositorio Documental
 * Responsable: Vera Aguilar, Carlos Edgardo
 * Almacén digital centralizado de contratos, informes, facturas electrónicas y balances
 * con filtrado por extensión, carga de archivos, visor interactivo y trazabilidad.
 */
public class VistaRepositorioDocumentalPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private final RepositorioVeterinaria repo = RepositorioVeterinaria.getInstancia();

    private JTextField txtBuscar;
    private JComboBox<String> comboFiltroTipo;
    private JTable tablaDocumentos;
    private DefaultTableModel modeloDocumentos;
    private JLabel lblContadorDocs;
    private List<DocumentoRepositorio> documentosActuales;

    public VistaRepositorioDocumentalPanel() {
        setLayout(new BorderLayout(0, 16));
        setBackground(new Color(248, 250, 252));
        setBorder(BorderFactory.createEmptyBorder(20, 24, 24, 24));

        add(crearCabeceraSuperior(), BorderLayout.NORTH);

        JPanel panelCentro = new JPanel(new BorderLayout(0, 12));
        panelCentro.setOpaque(false);

        panelCentro.add(crearBarraHerramientas(), BorderLayout.NORTH);
        panelCentro.add(crearPanelTabla(), BorderLayout.CENTER);

        add(panelCentro, BorderLayout.CENTER);

        cargarDatosDocumentos();
    }

    private JPanel crearCabeceraSuperior() {
        JPanel cab = new JPanel(new BorderLayout(16, 8));
        cab.setBackground(new Color(30, 41, 59));
        cab.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(15, 23, 42), 1),
                BorderFactory.createEmptyBorder(14, 20, 14, 20)
        ));

        JPanel pnlTit = new JPanel();
        pnlTit.setLayout(new BoxLayout(pnlTit, BoxLayout.Y_AXIS));
        pnlTit.setOpaque(false);

        JLabel lblTit = new JLabel("Submódulo 3: Repositorio Documental");
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTit.setForeground(Color.WHITE);
        pnlTit.add(lblTit);

        JLabel lblSub = new JLabel("Almacenamiento institucional, gestión de expedientes firmados y custodia digital.");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblSub.setForeground(new Color(148, 163, 184));
        pnlTit.add(lblSub);

        cab.add(pnlTit, BorderLayout.WEST);

        JPanel pnlUsr = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        pnlUsr.setOpaque(false);
        JLabel lblUsr = new JLabel("Usuario: Admin 👤");
        lblUsr.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblUsr.setForeground(new Color(226, 232, 240));
        pnlUsr.add(lblUsr);

        cab.add(pnlUsr, BorderLayout.EAST);
        return cab;
    }

    private JPanel crearBarraHerramientas() {
        JPanel card = new JPanel(new BorderLayout(12, 0));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)
        ));

        // Izquierda: Buscador + Filtro tipo
        JPanel izq = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        izq.setOpaque(false);

        txtBuscar = new JTextField(18);
        txtBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtBuscar.setPreferredSize(new Dimension(230, 32));
        txtBuscar.setToolTipText("Buscar archivo por nombre o categoría...");
        txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { cargarDatosDocumentos(); }
            @Override public void removeUpdate(DocumentEvent e) { cargarDatosDocumentos(); }
            @Override public void changedUpdate(DocumentEvent e) { cargarDatosDocumentos(); }
        });
        izq.add(new JLabel(Iconos.crearIconoBuscar(14, new Color(100, 116, 139))));
        izq.add(txtBuscar);

        comboFiltroTipo = new JComboBox<>(new String[]{
                "Filtrar: Todos ▼",
                "PDF",
                "DOCX",
                "XLSX"
        });
        comboFiltroTipo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        comboFiltroTipo.setPreferredSize(new Dimension(140, 32));
        comboFiltroTipo.setBackground(Color.WHITE);
        comboFiltroTipo.addActionListener(e -> cargarDatosDocumentos());
        izq.add(comboFiltroTipo);

        card.add(izq, BorderLayout.CENTER);

        // Derecha: Botón + Subir Archivo
        JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        der.setOpaque(false);

        JButton btnSubir = Ui.botonPrimario("+ Subir Archivo", Iconos.crearIconoDocumento(13, Color.WHITE));
        btnSubir.setPreferredSize(new Dimension(150, 32));
        btnSubir.addActionListener(e -> mostrarModalSubirArchivo());
        der.add(btnSubir);

        card.add(der, BorderLayout.EAST);
        return card;
    }

    private JPanel crearPanelTabla() {
        JPanel card = new JPanel(new BorderLayout(0, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                BorderFactory.createEmptyBorder(14, 16, 14, 16)
        ));

        String[] columnas = {"Tipo", "Nombre del Archivo", "Categoría", "Tamaño", "Fecha de Carga", "Acciones"};
        modeloDocumentos = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };

        tablaDocumentos = new JTable(modeloDocumentos);
        tablaDocumentos.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaDocumentos.setRowHeight(38);
        tablaDocumentos.setGridColor(new Color(241, 245, 249));
        tablaDocumentos.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tablaDocumentos.getTableHeader().setBackground(new Color(248, 250, 252));
        tablaDocumentos.getTableHeader().setForeground(new Color(71, 85, 105));
        tablaDocumentos.setSelectionBackground(new Color(240, 249, 255));
        tablaDocumentos.setSelectionForeground(new Color(15, 23, 42));

        DefaultTableCellRenderer centroRenderer = new DefaultTableCellRenderer();
        centroRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        tablaDocumentos.getColumnModel().getColumn(0).setCellRenderer(centroRenderer);
        tablaDocumentos.getColumnModel().getColumn(0).setPreferredWidth(80);
        tablaDocumentos.getColumnModel().getColumn(1).setPreferredWidth(210);
        tablaDocumentos.getColumnModel().getColumn(2).setPreferredWidth(140);
        tablaDocumentos.getColumnModel().getColumn(3).setCellRenderer(centroRenderer);
        tablaDocumentos.getColumnModel().getColumn(3).setPreferredWidth(90);
        tablaDocumentos.getColumnModel().getColumn(4).setCellRenderer(centroRenderer);
        tablaDocumentos.getColumnModel().getColumn(4).setPreferredWidth(140);
        tablaDocumentos.getColumnModel().getColumn(5).setPreferredWidth(130);

        // Render para el Badge de Tipo
        tablaDocumentos.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                l.setHorizontalAlignment(SwingConstants.CENTER);
                String tipo = value != null ? value.toString() : "";
                l.setFont(new Font("Segoe UI", Font.BOLD, 11));
                l.setOpaque(true);
                if ("PDF".equalsIgnoreCase(tipo)) {
                    l.setBackground(new Color(254, 226, 226));
                    l.setForeground(new Color(220, 38, 38));
                } else if ("DOCX".equalsIgnoreCase(tipo)) {
                    l.setBackground(new Color(224, 242, 254));
                    l.setForeground(new Color(3, 105, 161));
                } else if ("XLSX".equalsIgnoreCase(tipo)) {
                    l.setBackground(new Color(236, 253, 245));
                    l.setForeground(new Color(16, 185, 129));
                } else {
                    l.setBackground(new Color(241, 245, 249));
                    l.setForeground(new Color(71, 85, 105));
                }
                return l;
            }
        });

        // Render de Botón Acción
        tablaDocumentos.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(table, "Descargar / Ver", isSelected, hasFocus, row, col);
                l.setHorizontalAlignment(SwingConstants.CENTER);
                l.setFont(new Font("Segoe UI", Font.BOLD, 11));
                l.setForeground(new Color(2, 132, 199));
                l.setCursor(new Cursor(Cursor.HAND_CURSOR));
                return l;
            }
        });

        tablaDocumentos.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int col = tablaDocumentos.columnAtPoint(e.getPoint());
                int fila = tablaDocumentos.rowAtPoint(e.getPoint());
                if (fila >= 0 && (col == 5 || e.getClickCount() == 2)) {
                    abrirDetalleDocumento(fila);
                }
            }
        });

        JScrollPane scroll = new JScrollPane(tablaDocumentos);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(241, 245, 249), 1));
        card.add(scroll, BorderLayout.CENTER);

        // Pie de tabla
        JPanel pie = new JPanel(new BorderLayout());
        pie.setOpaque(false);

        lblContadorDocs = new JLabel("Mostrando 1-4 de 4 documentos");
        lblContadorDocs.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblContadorDocs.setForeground(new Color(100, 116, 139));
        pie.add(lblContadorDocs, BorderLayout.WEST);

        JLabel lblCap = new JLabel("Almacenamiento ocupado: 12.5 MB / 10 GB");
        lblCap.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblCap.setForeground(new Color(71, 85, 105));
        pie.add(lblCap, BorderLayout.EAST);

        card.add(pie, BorderLayout.SOUTH);
        return card;
    }

    private void cargarDatosDocumentos() {
        String q = txtBuscar != null ? txtBuscar.getText().trim() : "";
        String tipoFiltro = comboFiltroTipo != null ? (String) comboFiltroTipo.getSelectedItem() : "";
        if (tipoFiltro != null && tipoFiltro.contains("Todos")) tipoFiltro = "Todos";

        documentosActuales = repo.buscarDocumentosRepositorio(q, tipoFiltro);
        modeloDocumentos.setRowCount(0);

        for (DocumentoRepositorio d : documentosActuales) {
            modeloDocumentos.addRow(new Object[]{
                    d.getTipoExtension(),
                    d.getNombreArchivo(),
                    d.getCategoria(),
                    d.getTamanoLegible(),
                    d.getFechaCargaFormateada(),
                    "Descargar / Ver"
            });
        }

        if (lblContadorDocs != null) {
            int tot = repo.getDocumentosRepositorio().size();
            lblContadorDocs.setText("Mostrando 1-" + documentosActuales.size() + " de " + tot + " documentos");
        }
    }

    private void abrirDetalleDocumento(int fila) {
        if (fila < 0 || fila >= documentosActuales.size()) return;
        DocumentoRepositorio d = documentosActuales.get(fila);

        String msg = "EXPEDIENTE DIGITAL DEL REPOSITORIO\n\n"
                + "• Archivo: " + d.getNombreArchivo() + "\n"
                + "• Tipo de Formato: " + d.getTipoExtension() + "\n"
                + "• Categoría: " + d.getCategoria() + "\n"
                + "• Peso: " + d.getTamanoLegible() + "\n"
                + "• Fecha y Hora de Carga: " + d.getFechaCargaFormateada() + "\n"
                + "• Usuario Custodio: " + d.getUsuarioCarga() + "\n"
                + "• Ubicación en Servidor: " + d.getRutaArchivo() + "\n\n"
                + "¿Desea descargar una copia o previsualizar el documento?";

        int opt = JOptionPane.showOptionDialog(this, msg, "Documento - " + d.getNombreArchivo(),
                JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE,
                null, new String[]{"Descargar Copia", "Cerrar"}, "Descargar Copia");

        if (opt == 0) {
            repo.registrarLogAuditoria(new happypets.model.LogAuditoria(
                    "#EV-" + (1043 + repo.getLogsAuditoria().size()),
                    "Descarga de " + d.getNombreArchivo(),
                    "admin_user", LocalDateTime.now(), "127.0.0.1", "ÉXITO",
                    "Descarga completada del documento digital desde el Repositorio Documental"
            ));
            JOptionPane.showMessageDialog(this,
                    "Archivo " + d.getNombreArchivo() + " descargado exitosamente.\nGuardado temporalmente en carpeta de descargas de usuario.",
                    "Descarga Exitosa", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void mostrarModalSubirArchivo() {
        JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(this), "Subir Archivo al Repositorio Documental", JDialog.ModalityType.APPLICATION_MODAL);
        dlg.setSize(480, 340);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());

        JPanel pnl = new JPanel();
        pnl.setLayout(new BoxLayout(pnl, BoxLayout.Y_AXIS));
        pnl.setBackground(Color.WHITE);
        pnl.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        JLabel lblTit = new JLabel("Cargar Nuevo Documento Digital");
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTit.setForeground(new Color(15, 23, 42));
        pnl.add(lblTit);
        pnl.add(Box.createVerticalStrut(14));

        JTextField txtNombre = new JTextField("Convenio_Institucional_2026.pdf");
        txtNombre.setPreferredSize(new Dimension(300, 32));

        JComboBox<String> comboCat = new JComboBox<>(new String[]{"Legal / RRHH", "Facturación", "Auditoría", "Contabilidad", "Clínico"});
        comboCat.setPreferredSize(new Dimension(300, 32));
        comboCat.setBackground(Color.WHITE);

        JComboBox<String> comboTipo = new JComboBox<>(new String[]{"PDF", "DOCX", "XLSX"});
        comboTipo.setPreferredSize(new Dimension(300, 32));
        comboTipo.setBackground(Color.WHITE);

        JPanel grid = new JPanel(new GridLayout(3, 2, 8, 10));
        grid.setOpaque(false);
        grid.add(new JLabel("Nombre de Archivo:"));
        grid.add(txtNombre);
        grid.add(new JLabel("Categoría:"));
        grid.add(comboCat);
        grid.add(new JLabel("Formato / Extensión:"));
        grid.add(comboTipo);
        pnl.add(grid);

        dlg.add(pnl, BorderLayout.CENTER);

        JPanel bot = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        bot.setBackground(new Color(248, 250, 252));
        bot.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(226, 232, 240)));

        JButton btnCancelar = Ui.botonSecundario("Cancelar", null);
        btnCancelar.addActionListener(e -> dlg.dispose());

        JButton btnGuardar = Ui.botonPrimario("Confirmar Carga", null);
        btnGuardar.addActionListener(e -> {
            String nom = txtNombre.getText().trim();
            if (nom.isEmpty()) {
                JOptionPane.showMessageDialog(dlg, "Ingrese el nombre del archivo.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String tipo = (String) comboTipo.getSelectedItem();
            String cat = (String) comboCat.getSelectedItem();

            DocumentoRepositorio doc = new DocumentoRepositorio(
                    null, tipo, nom, cat, 2.1, LocalDateTime.now(), "admin_user", "docs/" + nom
            );
            repo.agregarDocumentoRepositorio(doc);
            cargarDatosDocumentos();
            dlg.dispose();
            JOptionPane.showMessageDialog(this, "Documento digital '" + nom + "' indexado con éxito.", "Carga Exitosa", JOptionPane.INFORMATION_MESSAGE);
        });

        bot.add(btnCancelar);
        bot.add(btnGuardar);
        dlg.add(bot, BorderLayout.SOUTH);

        dlg.setVisible(true);
    }
}
