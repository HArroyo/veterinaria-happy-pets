package happypets.modulos.modulo8;

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
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import happypets.data.RepositorioVeterinaria;
import happypets.model.HistorialExportacion;
import happypets.ui.Iconos;
import happypets.ui.Ui;

/**
 * Submódulo 8.4: Exportador de Datos
 * Responsable: Arroyo Preciado, Harry Martin
 * Extractor modular de datos con selección por origen (Clínica, Finanzas, Inventario),
 * segmentación por filtros y campos, formato (Excel, CSV, PDF), cálculo de volumen estimado,
 * ejecución de exportación y tabla de historial con widget de cuota de almacenamiento.
 */
public class VistaExportadorDatosPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private final RepositorioVeterinaria repo = RepositorioVeterinaria.getInstancia();

    // Estado de selección
    private String origenSeleccionado = "Pacientes y Fichas Clínicas";
    private String formatoSeleccionado = "XLSX";

    // Componentes interactivos Paso 1
    private JPanel cardOrigen1;
    private JPanel cardOrigen2;
    private JPanel cardOrigen3;

    // Componentes interactivos Paso 2
    private JComboBox<String> comboRango;
    private JComboBox<String> comboEspecie;
    private JComboBox<String> comboCliente;
    private JComboBox<String> comboSede;
    private final List<JCheckBox> checkCampos = new ArrayList<>();

    // Componentes interactivos Paso 3
    private JButton btnFmtExcel;
    private JButton btnFmtCSV;
    private JButton btnFmtPDF;
    private JLabel lblEstimacion;
    private JButton btnExportar;

    // Historial
    private JTable tablaHistorial;
    private DefaultTableModel modeloHistorial;
    private JTextField txtBuscarHistorial;
    private JLabel lblContadorHistorial;
    private List<HistorialExportacion> historialActual;

    public VistaExportadorDatosPanel() {
        setLayout(new BorderLayout(0, 16));
        setBackground(new Color(248, 250, 252));
        setBorder(BorderFactory.createEmptyBorder(20, 24, 24, 24));

        add(crearCabeceraSuperior(), BorderLayout.NORTH);

        JPanel panelCuerpo = new JPanel();
        panelCuerpo.setLayout(new BoxLayout(panelCuerpo, BoxLayout.Y_AXIS));
        panelCuerpo.setBackground(new Color(248, 250, 252));

        panelCuerpo.add(crearPaso1OrigenDatos());
        panelCuerpo.add(Box.createVerticalStrut(16));
        panelCuerpo.add(crearPaso2SegmentacionYCampos());
        panelCuerpo.add(Box.createVerticalStrut(16));
        panelCuerpo.add(crearPaso3FormatoYEjecucion());
        panelCuerpo.add(Box.createVerticalStrut(16));
        panelCuerpo.add(crearPanelHistorialYAlmacenamiento());

        JScrollPane scrollGeneral = new JScrollPane(panelCuerpo);
        scrollGeneral.setBorder(null);
        scrollGeneral.getVerticalScrollBar().setUnitIncrement(16);
        scrollGeneral.setBackground(new Color(248, 250, 252));
        add(scrollGeneral, BorderLayout.CENTER);

        actualizarEstimacion();
        cargarHistorial();
    }

    private JPanel crearCabeceraSuperior() {
        JPanel cab = new JPanel(new BorderLayout(16, 8));
        cab.setOpaque(false);

        JPanel pnlTit = new JPanel();
        pnlTit.setLayout(new BoxLayout(pnlTit, BoxLayout.Y_AXIS));
        pnlTit.setOpaque(false);

        JPanel filaT = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filaT.setOpaque(false);

        JLabel lblTit = new JLabel("Exportador de Datos");
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTit.setForeground(new Color(15, 23, 42));
        filaT.add(lblTit);

        JLabel badgeSede = new JLabel("  Sede Central y Filiales  ");
        badgeSede.setFont(new Font("Segoe UI", Font.BOLD, 11));
        badgeSede.setOpaque(true);
        badgeSede.setBackground(new Color(224, 242, 254));
        badgeSede.setForeground(new Color(3, 105, 161));
        badgeSede.setBorder(BorderFactory.createEmptyBorder(3, 6, 3, 6));
        filaT.add(badgeSede);
        pnlTit.add(filaT);
        pnlTit.add(Box.createVerticalStrut(3));

        JLabel lblSub = new JLabel("Extracción masiva y modular de conjuntos de datos clínicos, financieros e inventario.");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(new Color(100, 116, 139));
        pnlTit.add(lblSub);

        cab.add(pnlTit, BorderLayout.WEST);

        JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        der.setOpaque(false);

        JLabel badgeSync = new JLabel("● Última sinc.: Hoy, 09:30 AM");
        badgeSync.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        badgeSync.setForeground(new Color(100, 116, 139));
        der.add(badgeSync);

        JButton btnActualizarTodo = Ui.botonSecundario("Sincronizar Fuentes", Iconos.crearIconoRefrescar(13, new Color(15, 23, 42)));
        btnActualizarTodo.setPreferredSize(new Dimension(175, 34));
        btnActualizarTodo.addActionListener(e -> {
            cargarHistorial();
            JOptionPane.showMessageDialog(this,
                    "Todas las fuentes de datos (clínica, finanzas, inventario) se encuentran sincronizadas.",
                    "Sincronización Exitosa", JOptionPane.INFORMATION_MESSAGE);
        });
        der.add(btnActualizarTodo);

        cab.add(der, BorderLayout.EAST);
        return cab;
    }

    private JPanel crearPaso1OrigenDatos() {
        JPanel seccion = new JPanel(new BorderLayout(0, 10));
        seccion.setOpaque(false);

        JLabel lblPaso = new JLabel("PASO 1: SELECCIONE EL ORIGEN DE DATOS (DATA SOURCE)");
        lblPaso.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblPaso.setForeground(new Color(71, 85, 105));
        seccion.add(lblPaso, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(1, 3, 14, 0));
        grid.setOpaque(false);

        cardOrigen1 = crearTarjetaOrigen(
                "Pacientes y Fichas Clínicas",
                "3,892 registros",
                "Datos demográficos, especies, triaje, anamnesis, tratamientos y diagnósticos confirmados.",
                Iconos.crearIconoMascota(22, new Color(2, 132, 199)),
                true
        );

        cardOrigen2 = crearTarjetaOrigen(
                "Finanzas y Facturación",
                "14,210 tickets",
                "Transacciones POS, boletas, facturas, cuentas por cobrar/pagar, márgenes y caja chica.",
                Iconos.crearIconoPOS(22, new Color(13, 148, 136)),
                false
        );

        cardOrigen3 = crearTarjetaOrigen(
                "Inventario y Farmacia",
                "842 productos",
                "Catálogo de medicamentos, lotes activos, stocks mínimos, compras y mermas.",
                Iconos.crearIconoStock(22, new Color(217, 119, 6)),
                false
        );

        cardOrigen1.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { seleccionarOrigen("Pacientes y Fichas Clínicas"); }
        });
        cardOrigen2.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { seleccionarOrigen("Finanzas y Facturación"); }
        });
        cardOrigen3.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { seleccionarOrigen("Inventario y Farmacia"); }
        });

        grid.add(cardOrigen1);
        grid.add(cardOrigen2);
        grid.add(cardOrigen3);
        seccion.add(grid, BorderLayout.CENTER);

        return seccion;
    }

    private JPanel crearTarjetaOrigen(String titulo, String badgeTexto, String descripcion,
                                      javax.swing.Icon icono, boolean seleccionada) {
        JPanel card = new JPanel(new BorderLayout(8, 6));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        actualizarEstiloTarjetaOrigen(card, seleccionada);

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JLabel lblTit = new JLabel(titulo);
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTit.setForeground(new Color(15, 23, 42));
        top.add(lblTit, BorderLayout.WEST);

        JLabel lblIcono = new JLabel(icono);
        top.add(lblIcono, BorderLayout.EAST);
        card.add(top, BorderLayout.NORTH);

        JPanel centro = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 4));
        centro.setOpaque(false);

        JLabel badge = new JLabel("  " + badgeTexto + "  ");
        badge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        badge.setOpaque(true);
        badge.setBackground(new Color(241, 245, 249));
        badge.setForeground(new Color(3, 105, 161));
        badge.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));
        centro.add(badge);
        card.add(centro, BorderLayout.CENTER);

        JLabel lblDesc = new JLabel("<html><body style='width: 170px;'>" + descripcion + "</body></html>");
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblDesc.setForeground(new Color(100, 116, 139));
        card.add(lblDesc, BorderLayout.SOUTH);

        return card;
    }

    private void actualizarEstiloTarjetaOrigen(JPanel card, boolean seleccionada) {
        if (seleccionada) {
            card.setBackground(new Color(240, 249, 255));
            card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(2, 132, 199), 2),
                    BorderFactory.createEmptyBorder(12, 14, 12, 14)
            ));
        } else {
            card.setBackground(Color.WHITE);
            card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                    BorderFactory.createEmptyBorder(13, 15, 13, 15)
            ));
        }
    }

    private void seleccionarOrigen(String nuevoOrigen) {
        this.origenSeleccionado = nuevoOrigen;
        actualizarEstiloTarjetaOrigen(cardOrigen1, "Pacientes y Fichas Clínicas".equals(nuevoOrigen));
        actualizarEstiloTarjetaOrigen(cardOrigen2, "Finanzas y Facturación".equals(nuevoOrigen));
        actualizarEstiloTarjetaOrigen(cardOrigen3, "Inventario y Farmacia".equals(nuevoOrigen));
        cardOrigen1.repaint();
        cardOrigen2.repaint();
        cardOrigen3.repaint();
        actualizarEstimacion();
    }

    private JPanel crearPaso2SegmentacionYCampos() {
        JPanel card = new JPanel(new BorderLayout(0, 12));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                BorderFactory.createEmptyBorder(14, 16, 14, 16)
        ));

        JLabel lblPaso = new JLabel("PASO 2: FILTRADO, SEGMENTACIÓN Y CAMPOS ADICIONALES");
        lblPaso.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblPaso.setForeground(new Color(71, 85, 105));
        card.add(lblPaso, BorderLayout.NORTH);

        JPanel cuerpo = new JPanel();
        cuerpo.setLayout(new BoxLayout(cuerpo, BoxLayout.Y_AXIS));
        cuerpo.setOpaque(false);

        // Fila de 4 Selectores
        JPanel filaSelectores = new JPanel(new GridLayout(1, 4, 12, 0));
        filaSelectores.setOpaque(false);

        comboRango = new JComboBox<>(new String[]{
                "Último mes (Mayo 2024)",
                "Primer Semestre (Q1 - Q2 2024)",
                "Año en curso 2024",
                "Histórico Completo (Todo)"
        });
        comboRango.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        comboRango.setBackground(Color.WHITE);
        comboRango.addActionListener(e -> actualizarEstimacion());
        filaSelectores.add(crearCajaSelector("Rango Temporal", comboRango));

        comboEspecie = new JComboBox<>(new String[]{
                "Todas las Especies",
                "Sólo Caninos",
                "Sólo Felinos",
                "Animales Exóticos"
        });
        comboEspecie.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        comboEspecie.setBackground(Color.WHITE);
        comboEspecie.addActionListener(e -> actualizarEstimacion());
        filaSelectores.add(crearCajaSelector("Especie / Mascota", comboEspecie));

        comboCliente = new JComboBox<>(new String[]{
                "Todos los Clientes",
                "Clientes Regulares",
                "Nuevos Pacientes (2024)",
                "Clientes VIP / Convenios"
        });
        comboCliente.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        comboCliente.setBackground(Color.WHITE);
        comboCliente.addActionListener(e -> actualizarEstimacion());
        filaSelectores.add(crearCajaSelector("Tipo de Cliente / Tutor", comboCliente));

        comboSede = new JComboBox<>(new String[]{
                "Sede Central y Filiales",
                "Sede Central (Miraflores)",
                "Sede Filial (San Borja)"
        });
        comboSede.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        comboSede.setBackground(Color.WHITE);
        comboSede.addActionListener(e -> actualizarEstimacion());
        filaSelectores.add(crearCajaSelector("Sede de Origen", comboSede));

        cuerpo.add(filaSelectores);
        cuerpo.add(Box.createVerticalStrut(14));

        // Cuadrícula de 6 Checkboxes de campos
        JLabel lblCheckTit = new JLabel("Campos y Metadatos a Incluir en la Exportación:");
        lblCheckTit.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblCheckTit.setForeground(new Color(30, 41, 59));
        cuerpo.add(lblCheckTit);
        cuerpo.add(Box.createVerticalStrut(8));

        JPanel gridChecks = new JPanel(new GridLayout(2, 3, 12, 6));
        gridChecks.setOpaque(false);

        checkCampos.clear();
        checkCampos.add(new JCheckBox("Incluir Chip / Tatuaje y Registro Identificatorio", true));
        checkCampos.add(new JCheckBox("Incluir Datos del Tutor (DNI, Teléfono, Correo)", true));
        checkCampos.add(new JCheckBox("Incluir Médico Veterinario y N° Colegiatura", true));
        checkCampos.add(new JCheckBox("Incluir Costos Directos, Márgenes e Importes", false));
        checkCampos.add(new JCheckBox("Incluir Observaciones de Triaje y Anamnesis", true));
        checkCampos.add(new JCheckBox("Incluir Código CIE-10 / Diagnóstico Patológico", false));

        for (JCheckBox cb : checkCampos) {
            cb.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            cb.setBackground(Color.WHITE);
            cb.setForeground(new Color(51, 65, 85));
            cb.addActionListener(e -> actualizarEstimacion());
            gridChecks.add(cb);
        }

        cuerpo.add(gridChecks);
        card.add(cuerpo, BorderLayout.CENTER);

        return card;
    }

    private JPanel crearCajaSelector(String etiqueta, JComboBox<String> combo) {
        JPanel p = new JPanel(new BorderLayout(4, 3));
        p.setOpaque(false);
        JLabel l = new JLabel(etiqueta);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        l.setForeground(new Color(100, 116, 139));
        p.add(l, BorderLayout.NORTH);
        p.add(combo, BorderLayout.CENTER);
        return p;
    }

    private JPanel crearPaso3FormatoYEjecucion() {
        JPanel card = new JPanel(new BorderLayout(14, 0));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                BorderFactory.createEmptyBorder(14, 16, 14, 16)
        ));

        JPanel izq = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        izq.setOpaque(false);

        JLabel lblPaso = new JLabel("PASO 3: FORMATO: ");
        lblPaso.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblPaso.setForeground(new Color(71, 85, 105));
        izq.add(lblPaso);

        btnFmtExcel = new JButton("Microsoft Excel (.xlsx)");
        btnFmtExcel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnFmtExcel.setFocusPainted(false);
        btnFmtExcel.addActionListener(e -> seleccionarFormato("XLSX"));
        izq.add(btnFmtExcel);

        btnFmtCSV = new JButton("Valores CSV (.csv)");
        btnFmtCSV.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btnFmtCSV.setFocusPainted(false);
        btnFmtCSV.addActionListener(e -> seleccionarFormato("CSV"));
        izq.add(btnFmtCSV);

        btnFmtPDF = new JButton("Documento PDF (.pdf)");
        btnFmtPDF.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btnFmtPDF.setFocusPainted(false);
        btnFmtPDF.addActionListener(e -> seleccionarFormato("PDF"));
        izq.add(btnFmtPDF);

        actualizarBotonesFormato();
        card.add(izq, BorderLayout.WEST);

        // Estimación y botón de exportación
        JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 0));
        der.setOpaque(false);

        lblEstimacion = new JLabel("3,120 filas estimadas · Aprox. 2.4 MB");
        lblEstimacion.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblEstimacion.setForeground(new Color(15, 118, 110));
        der.add(lblEstimacion);

        btnExportar = Ui.botonPrimario("Exportar Conjunto de Datos", Iconos.crearIconoDocumento(14, Color.WHITE));
        btnExportar.setPreferredSize(new Dimension(230, 36));
        btnExportar.addActionListener(e -> ejecutarExportacion());
        der.add(btnExportar);

        card.add(der, BorderLayout.EAST);
        return card;
    }

    private void seleccionarFormato(String fmt) {
        this.formatoSeleccionado = fmt;
        actualizarBotonesFormato();
        actualizarEstimacion();
    }

    private void actualizarBotonesFormato() {
        estilizarBotonFormato(btnFmtExcel, "XLSX".equals(formatoSeleccionado));
        estilizarBotonFormato(btnFmtCSV, "CSV".equals(formatoSeleccionado));
        estilizarBotonFormato(btnFmtPDF, "PDF".equals(formatoSeleccionado));
    }

    private void estilizarBotonFormato(JButton btn, boolean seleccionado) {
        if (btn == null) return;
        if (seleccionado) {
            btn.setBackground(new Color(2, 132, 199));
            btn.setForeground(Color.WHITE);
            btn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(3, 105, 161), 1),
                    BorderFactory.createEmptyBorder(6, 12, 6, 12)
            ));
        } else {
            btn.setBackground(Color.WHITE);
            btn.setForeground(new Color(51, 65, 85));
            btn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(203, 213, 225), 1),
                    BorderFactory.createEmptyBorder(6, 12, 6, 12)
            ));
        }
    }

    private void actualizarEstimacion() {
        int filas;
        double mb;
        if ("Inventario y Farmacia".equals(origenSeleccionado)) {
            filas = 842;
            mb = "CSV".equals(formatoSeleccionado) ? 0.3 : ("PDF".equals(formatoSeleccionado) ? 1.2 : 0.6);
        } else if ("Finanzas y Facturación".equals(origenSeleccionado)) {
            filas = 14210;
            mb = "CSV".equals(formatoSeleccionado) ? 4.5 : ("PDF".equals(formatoSeleccionado) ? 9.8 : 8.7);
        } else {
            filas = 3120;
            mb = "CSV".equals(formatoSeleccionado) ? 1.1 : ("PDF".equals(formatoSeleccionado) ? 4.6 : 2.4);
        }

        if (lblEstimacion != null) {
            lblEstimacion.setText(String.format("%,d filas estimadas · Aprox. %.1f MB", filas, mb));
        }
    }

    private void ejecutarExportacion() {
        List<String> campos = new ArrayList<>();
        for (JCheckBox cb : checkCampos) {
            if (cb.isSelected()) campos.add(cb.getText());
        }

        String filtros = (String) comboEspecie.getSelectedItem() + " · " + comboSede.getSelectedItem();

        HistorialExportacion exp = repo.generarExportacion(origenSeleccionado, formatoSeleccionado, filtros, campos);
        cargarHistorial();

        String msg = "¡EXPORTACIÓN COMPLETADA CON ÉXITO!\n\n"
                + "• Identificador: " + exp.getIdExportacion() + "\n"
                + "• Origen: " + exp.getOrigenDatos() + "\n"
                + "• Formato: " + exp.getFormato() + "\n"
                + "• Filas procesadas: " + String.format("%,d", exp.getTotalFilas()) + "\n"
                + "• Tamaño generado: " + exp.getTamanoLegible() + "\n"
                + "• Archivo: " + exp.getRutaArchivo() + "\n\n"
                + "El archivo se ha indexado en el historial de exportaciones recientes.";

        JOptionPane.showMessageDialog(this, msg, "Descarga Finalizada", JOptionPane.INFORMATION_MESSAGE);
    }

    private JPanel crearPanelHistorialYAlmacenamiento() {
        JPanel card = new JPanel(new BorderLayout(0, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                BorderFactory.createEmptyBorder(14, 16, 14, 16)
        ));

        // Cabecera del Historial
        JPanel top = new JPanel(new BorderLayout(12, 0));
        top.setOpaque(false);

        JPanel titIzq = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        titIzq.setOpaque(false);

        JLabel lblTit = new JLabel("Historial de Exportaciones Recientes");
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTit.setForeground(new Color(15, 23, 42));
        titIzq.add(lblTit);

        lblContadorHistorial = new JLabel("  5 descargas  ");
        lblContadorHistorial.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblContadorHistorial.setOpaque(true);
        lblContadorHistorial.setBackground(new Color(241, 245, 249));
        lblContadorHistorial.setForeground(new Color(71, 85, 105));
        lblContadorHistorial.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));
        titIzq.add(lblContadorHistorial);
        top.add(titIzq, BorderLayout.WEST);

        JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        der.setOpaque(false);

        txtBuscarHistorial = new JTextField(16);
        txtBuscarHistorial.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtBuscarHistorial.setPreferredSize(new Dimension(190, 30));
        txtBuscarHistorial.setToolTipText("Buscar en historial...");
        txtBuscarHistorial.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { cargarHistorial(); }
            @Override public void removeUpdate(DocumentEvent e) { cargarHistorial(); }
            @Override public void changedUpdate(DocumentEvent e) { cargarHistorial(); }
        });
        der.add(new JLabel(Iconos.crearIconoLupa(14, new Color(100, 116, 139))));
        der.add(txtBuscarHistorial);

        JButton btnActualizarHist = Ui.botonSecundario("Actualizar", Iconos.crearIconoRefrescar(12, new Color(15, 23, 42)));
        btnActualizarHist.setPreferredSize(new Dimension(110, 30));
        btnActualizarHist.addActionListener(e -> cargarHistorial());
        der.add(btnActualizarHist);

        top.add(der, BorderLayout.EAST);
        card.add(top, BorderLayout.NORTH);

        // Tabla de Historial
        String[] columnas = {"Cód. Exportación", "Origen de Datos", "Fecha y Hora", "Filtros Aplicados", "Formato", "Filas", "Tamaño", "Estado", "Acción"};
        modeloHistorial = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };

        tablaHistorial = new JTable(modeloHistorial);
        tablaHistorial.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaHistorial.setRowHeight(36);
        tablaHistorial.setGridColor(new Color(241, 245, 249));
        tablaHistorial.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tablaHistorial.getTableHeader().setBackground(new Color(248, 250, 252));
        tablaHistorial.getTableHeader().setForeground(new Color(71, 85, 105));
        tablaHistorial.setSelectionBackground(new Color(240, 249, 255));
        tablaHistorial.setSelectionForeground(new Color(15, 23, 42));

        DefaultTableCellRenderer centroRenderer = new DefaultTableCellRenderer();
        centroRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        DefaultTableCellRenderer derRenderer = new DefaultTableCellRenderer();
        derRenderer.setHorizontalAlignment(SwingConstants.RIGHT);

        tablaHistorial.getColumnModel().getColumn(0).setCellRenderer(centroRenderer);
        tablaHistorial.getColumnModel().getColumn(0).setPreferredWidth(105);
        tablaHistorial.getColumnModel().getColumn(1).setPreferredWidth(170);
        tablaHistorial.getColumnModel().getColumn(2).setCellRenderer(centroRenderer);
        tablaHistorial.getColumnModel().getColumn(2).setPreferredWidth(120);
        tablaHistorial.getColumnModel().getColumn(3).setPreferredWidth(190);
        tablaHistorial.getColumnModel().getColumn(4).setCellRenderer(centroRenderer);
        tablaHistorial.getColumnModel().getColumn(4).setPreferredWidth(75);
        tablaHistorial.getColumnModel().getColumn(5).setCellRenderer(derRenderer);
        tablaHistorial.getColumnModel().getColumn(5).setPreferredWidth(85);
        tablaHistorial.getColumnModel().getColumn(6).setCellRenderer(centroRenderer);
        tablaHistorial.getColumnModel().getColumn(6).setPreferredWidth(75);
        tablaHistorial.getColumnModel().getColumn(7).setPreferredWidth(100);
        tablaHistorial.getColumnModel().getColumn(8).setPreferredWidth(85);

        // Render de Estado (Completado)
        tablaHistorial.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                l.setHorizontalAlignment(SwingConstants.CENTER);
                l.setFont(new Font("Segoe UI", Font.BOLD, 11));
                l.setOpaque(true);
                l.setBackground(new Color(236, 253, 245));
                l.setForeground(new Color(16, 185, 129));
                return l;
            }
        });

        // Render de Acción Descargar
        tablaHistorial.getColumnModel().getColumn(8).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(table, "Descargar", isSelected, hasFocus, row, col);
                l.setHorizontalAlignment(SwingConstants.CENTER);
                l.setFont(new Font("Segoe UI", Font.BOLD, 11));
                l.setForeground(new Color(2, 132, 199));
                l.setCursor(new Cursor(Cursor.HAND_CURSOR));
                return l;
            }
        });

        tablaHistorial.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int col = tablaHistorial.columnAtPoint(e.getPoint());
                int fila = tablaHistorial.rowAtPoint(e.getPoint());
                if (fila >= 0 && col == 8) {
                    descargarArchivoHistorial(fila);
                }
            }
        });

        JScrollPane scroll = new JScrollPane(tablaHistorial);
        scroll.setPreferredSize(new Dimension(800, 190));
        scroll.setBorder(BorderFactory.createLineBorder(new Color(241, 245, 249), 1));
        card.add(scroll, BorderLayout.CENTER);

        // Barra inferior de Cuota de Almacenamiento en Nube
        JPanel pnlAlmacenamiento = new JPanel(new BorderLayout(14, 4));
        pnlAlmacenamiento.setBackground(new Color(248, 250, 252));
        pnlAlmacenamiento.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)
        ));

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);

        JLabel lblCap = new JLabel("Cuota de Almacenamiento para Exportaciones: 6.4 GB / 10 GB (64% utilizado)");
        lblCap.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblCap.setForeground(new Color(30, 41, 59));
        topBar.add(lblCap, BorderLayout.WEST);

        JLabel lblLibre = new JLabel("Espacio libre disponible: 3.6 GB");
        lblLibre.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblLibre.setForeground(new Color(100, 116, 139));
        topBar.add(lblLibre, BorderLayout.EAST);
        pnlAlmacenamiento.add(topBar, BorderLayout.NORTH);

        JProgressBar bar = new JProgressBar(0, 100);
        bar.setValue(64);
        bar.setPreferredSize(new Dimension(100, 7));
        bar.setForeground(new Color(2, 132, 199));
        bar.setBackground(new Color(226, 232, 240));
        bar.setBorderPainted(false);
        pnlAlmacenamiento.add(bar, BorderLayout.CENTER);

        card.add(pnlAlmacenamiento, BorderLayout.SOUTH);
        return card;
    }

    private void cargarHistorial() {
        String q = txtBuscarHistorial != null ? txtBuscarHistorial.getText().trim().toLowerCase() : "";
        List<HistorialExportacion> todos = repo.getHistorialExportaciones();
        historialActual = new ArrayList<>();

        modeloHistorial.setRowCount(0);
        for (HistorialExportacion h : todos) {
            if (q.isEmpty() || h.getIdExportacion().toLowerCase().contains(q)
                    || h.getOrigenDatos().toLowerCase().contains(q)
                    || h.getFiltrosAplicados().toLowerCase().contains(q)
                    || h.getFormato().toLowerCase().contains(q)) {
                historialActual.add(h);
                modeloHistorial.addRow(new Object[]{
                        h.getIdExportacion(),
                        h.getOrigenDatos(),
                        h.getFechaCreacionFormateada(),
                        h.getFiltrosAplicados(),
                        h.getFormato(),
                        String.format("%,d", h.getTotalFilas()),
                        h.getTamanoLegible(),
                        h.getEstado(),
                        "Descargar"
                });
            }
        }

        if (lblContadorHistorial != null) {
            lblContadorHistorial.setText("  " + historialActual.size() + " descargas listadas  ");
        }
    }

    private void descargarArchivoHistorial(int fila) {
        if (fila < 0 || fila >= historialActual.size()) return;
        HistorialExportacion h = historialActual.get(fila);

        JOptionPane.showMessageDialog(this,
                "Descargando archivo: " + h.getIdExportacion() + " (" + h.getFormato() + ")\n"
                        + "Ubicación en disco: " + h.getRutaArchivo() + "\n"
                        + "Tamaño: " + h.getTamanoLegible(),
                "Descarga de Archivo", JOptionPane.INFORMATION_MESSAGE);
    }
}
