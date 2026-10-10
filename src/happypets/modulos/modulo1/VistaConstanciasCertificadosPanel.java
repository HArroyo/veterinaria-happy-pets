package happypets.modulos.modulo1;

import java.awt.BorderLayout;
import java.awt.Color;
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
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import happypets.data.RepositorioVeterinaria;
import happypets.model.Cliente;
import happypets.model.DocumentoMascota;
import happypets.model.Mascota;
import happypets.ui.Iconos;
import happypets.ui.Ui;

/**
 * Vista 3 del Módulo 1: Constancias y Certificados Médicos.
 * Adaptada a la nueva arquitectura web moderna ERP con:
 * - Cabecera con título, subtítulo y botón de emisión.
 * - Barra de búsqueda y selector de tipo de certificado.
 * - 4 Tarjetas KPI de resumen documental del paciente activo.
 * - Tabla con los certificados y constancias oficiales con vista previa e impresión.
 */
public class VistaConstanciasCertificadosPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private static final Color COLOR_BORDE = Ui.BORDE_SUAVE;
    private static final Color COLOR_AZUL_PRIMARIO = Ui.TURQUESA;
    private static final Color COLOR_TEXTO_TITULO = Ui.TEXTO_TITULO;
    private static final Color COLOR_TEXTO_MUTED = Ui.TEXTO_MUTED;

    private final RepositorioVeterinaria repo = RepositorioVeterinaria.getInstancia();
    private Mascota mascotaActual;
    private Cliente clienteActual;
    private List<DocumentoMascota> listaDocumentos;

    // Filtros
    private JTextField txtBuscar;
    private JComboBox<String> cbTipoDocumento;

    // Labels KPI
    private JLabel lblKpiMascotaVal;
    private JLabel lblKpiMascotaSub;
    private JLabel lblKpiClienteVal;
    private JLabel lblKpiClienteSub;
    private JLabel lblKpiTotalVal;
    private JLabel lblKpiTotalSub;
    private JLabel lblKpiEstadoVal;
    private JLabel lblKpiEstadoSub;

    // Tabla de documentos
    private JLabel lblContadorDocs;
    private JTable tablaDocumentos;
    private DefaultTableModel modeloDocumentos;

    public VistaConstanciasCertificadosPanel() {
        setLayout(new BorderLayout());
        setOpaque(false);

        JPanel contenido = new JPanel();
        contenido.setOpaque(false);
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setBorder(new EmptyBorder(12, 18, 14, 18));

        // 1. Cabecera
        contenido.add(crearCabeceraVista());
        contenido.add(Box.createVerticalStrut(8));

        // 2. Barra de filtros de búsqueda y tipo de documento
        contenido.add(crearBarraFiltros());
        contenido.add(Box.createVerticalStrut(10));

        // 3. 4 Tarjetas KPI de resumen documental
        contenido.add(crearFilaKpiDocumentos());
        contenido.add(Box.createVerticalStrut(10));

        // 4. Tabla de documentos y certificados emitidos
        contenido.add(crearTarjetaTablaDocumentos());

        // Wrapper con BorderLayout.NORTH para evitar estiramientos verticales en pantallas maximizadas
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(contenido, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(wrapper);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);

        // Cargar paciente por defecto: Rocky (VET-0091)
        Optional<Mascota> optRocky = repo.buscarMascotaPorCodigoONombre("VET-0091");
        if (optRocky.isPresent()) {
            cargarMascota(optRocky.get());
        } else {
            List<Mascota> todas = repo.todasLasMascotas();
            if (!todas.isEmpty()) cargarMascota(todas.get(0));
        }
    }

    private JPanel crearCabeceraVista() {
        JPanel cab = new JPanel(new BorderLayout());
        cab.setOpaque(false);
        cab.setPreferredSize(new Dimension(0, 40));
        cab.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JPanel izq = new JPanel();
        izq.setOpaque(false);
        izq.setLayout(new BoxLayout(izq, BoxLayout.Y_AXIS));

        JLabel lblTit = new JLabel("Constancias y Certificados Médicos");
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblTit.setForeground(COLOR_TEXTO_TITULO);

        JLabel lblSub = new JLabel("Módulo 1.3 · Emisión, consulta y descarga de constancias sanitarias y certificados oficiales");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblSub.setForeground(COLOR_TEXTO_MUTED);

        izq.add(lblTit);
        izq.add(Box.createVerticalStrut(2));
        izq.add(lblSub);
        cab.add(izq, BorderLayout.CENTER);

        JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 2));
        der.setOpaque(false);

        JButton btnImprimir = Ui.boton("Imprimir Listado", false);
        btnImprimir.setIcon(Iconos.crearIconoImprimir(12, COLOR_AZUL_PRIMARIO));
        btnImprimir.setIconTextGap(4);
        btnImprimir.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnImprimir.setPreferredSize(new Dimension(btnImprimir.getPreferredSize().width, 28));
        btnImprimir.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Enviando listado de constancias y certificados a la impresora predeterminada.",
                "Impresión", JOptionPane.INFORMATION_MESSAGE));

        JButton btnEmitir = Ui.boton("+ Emitir Nuevo Certificado", true);
        btnEmitir.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnEmitir.setPreferredSize(new Dimension(btnEmitir.getPreferredSize().width, 28));
        btnEmitir.addActionListener(e -> emitirNuevoCertificado());

        der.add(btnImprimir);
        der.add(btnEmitir);
        cab.add(der, BorderLayout.EAST);

        return cab;
    }

    private JPanel crearBarraFiltros() {
        JPanel barra = new JPanel(new BorderLayout(10, 0)) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(COLOR_BORDE);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        barra.setOpaque(false);
        barra.setBorder(new EmptyBorder(3, 10, 3, 10));
        barra.setPreferredSize(new Dimension(0, 38));
        barra.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

        JPanel izq = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
        izq.setOpaque(false);

        JLabel lblBuscar = new JLabel("Buscar cliente o mascota:");
        lblBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblBuscar.setForeground(COLOR_TEXTO_MUTED);
        izq.add(lblBuscar);

        txtBuscar = new JTextField(12);
        txtBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        txtBuscar.setPreferredSize(new Dimension(140, 26));
        txtBuscar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225), 1),
                new EmptyBorder(2, 6, 2, 6)
        ));
        txtBuscar.setToolTipText("Nombre o código...");
        izq.add(txtBuscar);

        JButton btnBuscar = Ui.boton("Buscar", false);
        btnBuscar.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnBuscar.setPreferredSize(new Dimension(btnBuscar.getPreferredSize().width, 26));
        btnBuscar.setIcon(Iconos.crearIconoBuscar(12, COLOR_AZUL_PRIMARIO));
        btnBuscar.setIconTextGap(4);

        java.awt.event.ActionListener accionBuscar = e -> buscarClienteOMascota();
        btnBuscar.addActionListener(accionBuscar);
        txtBuscar.addActionListener(accionBuscar);
        izq.add(btnBuscar);

        izq.add(Box.createHorizontalStrut(10));

        JLabel lblTipo = new JLabel("Tipo de documento:");
        lblTipo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblTipo.setForeground(COLOR_TEXTO_MUTED);
        izq.add(lblTipo);

        cbTipoDocumento = new JComboBox<>(new String[]{
                "Todos los documentos",
                "Tarjeta de vacunación",
                "Historial de recetas médicas",
                "Historial clínico",
                "Certificado de vacunación",
                "Certificado de salud",
                "Certificado de desparasitación",
                "Autorización quirúrgica"
        });
        cbTipoDocumento.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        cbTipoDocumento.setPreferredSize(new Dimension(180, 26));
        cbTipoDocumento.setBackground(Color.WHITE);
        cbTipoDocumento.addActionListener(e -> filtrarDocumentos());
        izq.add(cbTipoDocumento);

        barra.add(izq, BorderLayout.WEST);

        // Selector rápido a la derecha
        JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 2));
        der.setOpaque(false);

        JLabel lblAtajos = new JLabel("Pacientes:");
        lblAtajos.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblAtajos.setForeground(COLOR_TEXTO_MUTED);
        der.add(lblAtajos);

        JButton bRocky = Ui.boton("Rocky", false);
        bRocky.setFont(new Font("Segoe UI", Font.BOLD, 10));
        bRocky.setPreferredSize(new Dimension(bRocky.getPreferredSize().width, 24));
        bRocky.addActionListener(e -> repo.buscarMascotaPorCodigoONombre("VET-0091").ifPresent(this::cargarMascota));

        JButton bLuna = Ui.boton("Luna", false);
        bLuna.setFont(new Font("Segoe UI", Font.BOLD, 10));
        bLuna.setPreferredSize(new Dimension(bLuna.getPreferredSize().width, 24));
        bLuna.addActionListener(e -> repo.buscarMascotaPorCodigoONombre("VET-0144").ifPresent(this::cargarMascota));

        JButton bToby = Ui.boton("Toby", false);
        bToby.setFont(new Font("Segoe UI", Font.BOLD, 10));
        bToby.setPreferredSize(new Dimension(bToby.getPreferredSize().width, 24));
        bToby.addActionListener(e -> repo.buscarMascotaPorCodigoONombre("VET-0238").ifPresent(this::cargarMascota));

        der.add(bRocky);
        der.add(bLuna);
        der.add(bToby);

        barra.add(der, BorderLayout.EAST);
        return barra;
    }

    private JPanel crearFilaKpiDocumentos() {
        JPanel fila = new JPanel(new GridLayout(1, 4, 16, 0));
        fila.setOpaque(false);

        lblKpiMascotaVal = new JLabel("Rocky");
        lblKpiMascotaSub = new JLabel("Canino · Golden Retriever");
        fila.add(crearCardKpi(lblKpiMascotaVal, lblKpiMascotaSub, "PACIENTE ASOCIADO", Ui.TURQUESA_SUAVE, Iconos.crearIconoHuella(22, COLOR_AZUL_PRIMARIO)));

        lblKpiClienteVal = new JLabel("Carlos Morales");
        lblKpiClienteSub = new JLabel("DNI: 45892134");
        fila.add(crearCardKpi(lblKpiClienteVal, lblKpiClienteSub, "PROPIETARIO", new Color(254, 243, 199), Iconos.crearIconoClientes(22, new Color(217, 119, 6))));

        lblKpiTotalVal = new JLabel("4");
        lblKpiTotalSub = new JLabel("Constancias vigentes");
        fila.add(crearCardKpi(lblKpiTotalVal, lblKpiTotalSub, "DOCUMENTOS DISPONIBLES", new Color(220, 252, 231), Iconos.crearIconoCertificado(22, new Color(22, 163, 74))));

        lblKpiEstadoVal = new JLabel("VIGENTE");
        lblKpiEstadoSub = new JLabel("Plan sanitario al día");
        fila.add(crearCardKpi(lblKpiEstadoVal, lblKpiEstadoSub, "ESTADO SANITARIO", new Color(243, 232, 255), Iconos.crearIconoCandado(22, new Color(147, 51, 234))));

        return fila;
    }

    private JPanel crearCardKpi(JLabel lblPrincipal, JLabel lblSecundario, String etiqueta, Color colorFondoIco, Icon icono) {
        JPanel card = new JPanel(new BorderLayout(10, 0)) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(COLOR_BORDE);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(10, 12, 10, 12));

        JLabel badgeIcono = new JLabel(icono, SwingConstants.CENTER) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(colorFondoIco);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        badgeIcono.setPreferredSize(new Dimension(38, 38));
        card.add(badgeIcono, BorderLayout.WEST);

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));

        lblPrincipal.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblPrincipal.setForeground(COLOR_TEXTO_TITULO);

        JLabel lblEtq = new JLabel(etiqueta);
        lblEtq.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblEtq.setForeground(COLOR_TEXTO_MUTED);

        lblSecundario.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblSecundario.setForeground(COLOR_TEXTO_MUTED);

        text.add(lblEtq);
        text.add(Box.createVerticalStrut(2));
        text.add(lblPrincipal);
        text.add(Box.createVerticalStrut(1));
        text.add(lblSecundario);
        card.add(text, BorderLayout.CENTER);

        return card;
    }

    private JPanel crearTarjetaTablaDocumentos() {
        JPanel card = new JPanel(new BorderLayout(0, 8)) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(COLOR_BORDE);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(12, 14, 12, 14));

        // Cabecera
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JLabel lblTit = new JLabel("Documentos y Certificados Sanitarios Emitidos");
        lblTit.setIcon(Iconos.crearIconoCertificado(15, COLOR_AZUL_PRIMARIO));
        lblTit.setIconTextGap(6);
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTit.setForeground(COLOR_TEXTO_TITULO);
        top.add(lblTit, BorderLayout.WEST);

        lblContadorDocs = new JLabel("4 documentos disponibles");
        lblContadorDocs.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblContadorDocs.setForeground(COLOR_TEXTO_MUTED);
        top.add(lblContadorDocs, BorderLayout.EAST);
        card.add(top, BorderLayout.NORTH);

        // Tabla
        String[] columnas = {"Tipo de Documento", "Descripción / Contenido", "Fecha Actualización", "Disponibilidad", "Estado"};
        modeloDocumentos = new DefaultTableModel(columnas, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        tablaDocumentos = new JTable(modeloDocumentos);
        Ui.formatearTabla(tablaDocumentos, new int[]{0, 2, 3, 4}, new int[]{});

        tablaDocumentos.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    verDetalleDocumentoSeleccionado();
                }
            }
        });

        JScrollPane sp = new JScrollPane(tablaDocumentos);
        sp.setPreferredSize(new Dimension(1100, 210));
        sp.setBorder(BorderFactory.createLineBorder(COLOR_BORDE, 1));
        card.add(sp, BorderLayout.CENTER);

        // Pie
        JPanel bot = new JPanel(new BorderLayout());
        bot.setOpaque(false);

        JLabel lblTip = new JLabel("Doble clic sobre cualquier documento para visualizar la vista oficial de impresión");
        lblTip.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblTip.setForeground(COLOR_TEXTO_MUTED);
        bot.add(lblTip, BorderLayout.WEST);

        JPanel botBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 2));
        botBtns.setOpaque(false);

        JButton btnVer = Ui.boton("Ver / Imprimir Certificado", true);
        btnVer.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnVer.setPreferredSize(new Dimension(btnVer.getPreferredSize().width, 28));
        btnVer.addActionListener(e -> verDetalleDocumentoSeleccionado());

        botBtns.add(btnVer);
        bot.add(botBtns, BorderLayout.EAST);

        card.add(bot, BorderLayout.SOUTH);
        return card;
    }

    public void cargarMascota(Mascota m) {
        this.mascotaActual = m;
        if (m == null) return;

        clienteActual = repo.getClienteDeMascota(m.getCodigo()).orElse(null);

        lblKpiMascotaVal.setText(m.getNombre());
        lblKpiMascotaSub.setText(m.getEspecie() + " · " + m.getRaza() + " (" + m.getCodigo() + ")");

        if (clienteActual != null) {
            lblKpiClienteVal.setText(clienteActual.getNombreCompleto());
            lblKpiClienteSub.setText("DNI: " + clienteActual.getNumeroDocumento() + " · Tel: " + clienteActual.getTelefonoPrincipal());
        } else {
            lblKpiClienteVal.setText("Sin propietario");
            lblKpiClienteSub.setText("-");
        }

        listaDocumentos = repo.getDocumentosPorMascota(m.getCodigo());
        lblKpiTotalVal.setText(String.valueOf(listaDocumentos.size()));
        lblKpiTotalSub.setText("Documentos vigentes para emisión");

        lblKpiEstadoVal.setText("VIGENTE");
        lblKpiEstadoSub.setText("Plan vacunal: " + m.getPlanVacunal());

        filtrarDocumentos();
    }

    private void filtrarDocumentos() {
        modeloDocumentos.setRowCount(0);
        if (mascotaActual == null || listaDocumentos == null) return;

        String filtroTipo = (String) cbTipoDocumento.getSelectedItem();

        int count = 0;
        for (DocumentoMascota d : listaDocumentos) {
            if ("Todos los documentos".equals(filtroTipo) || d.getTipo().equalsIgnoreCase(filtroTipo)) {
                count++;
                modeloDocumentos.addRow(new Object[]{
                        d.getTipo(),
                        d.getDescripcion(),
                        d.getFechaActualizacionFormateada(),
                        d.getDisponibilidadTexto(),
                        "Oficial · Válido"
                });
            }
        }
        lblContadorDocs.setText(count + " documento(s) disponible(s)");
    }

    private void buscarClienteOMascota() {
        String query = txtBuscar.getText().trim();
        if (query.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese el nombre o código de la mascota a consultar.", "Búsqueda", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Optional<Mascota> optM = repo.buscarMascotaPorCodigoONombre(query);
        if (optM.isPresent()) {
            cargarMascota(optM.get());
            return;
        }
        Optional<Cliente> optC = repo.buscarClientePorDniOApellido(query);
        if (optC.isPresent() && !optC.get().getMascotas().isEmpty()) {
            cargarMascota(optC.get().getMascotas().get(0));
            return;
        }
        JOptionPane.showMessageDialog(this, "No se encontró ningún registro coincidente con: " + query, "No Encontrado", JOptionPane.WARNING_MESSAGE);
    }

    private void verDetalleDocumentoSeleccionado() {
        int row = tablaDocumentos.getSelectedRow();
        if (row < 0 || listaDocumentos == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un documento en la tabla para visualizarlo.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String tipo = (String) modeloDocumentos.getValueAt(row, 0);
        DocumentoMascota doc = listaDocumentos.stream().filter(d -> d.getTipo().equals(tipo)).findFirst().orElse(null);
        if (doc != null) {
            DetalleDocumentoDialog dlg = new DetalleDocumentoDialog(null, doc, mascotaActual, clienteActual);
            dlg.setVisible(true);
        }
    }

    private void emitirNuevoCertificado() {
        if (mascotaActual == null) {
            JOptionPane.showMessageDialog(this, "Seleccione primero una mascota para emitirle una constancia o certificado.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String[] opciones = {
                "Certificado de Salud General",
                "Constancia de Vacunación",
                "Certificado de Desparasitación",
                "Autorización Quirúrgica"
        };
        String tipo = (String) JOptionPane.showInputDialog(this, "Seleccione el tipo de documento a emitir:", "Emitir Documento Oficial", JOptionPane.QUESTION_MESSAGE, null, opciones, opciones[0]);
        if (tipo != null) {
            String desc = JOptionPane.showInputDialog(this, "Observaciones / Detalles clínicos para el documento:", "Observaciones", JOptionPane.QUESTION_MESSAGE);
            String docId = "DOC-00" + (listaDocumentos != null ? (listaDocumentos.size() + 1) : 1);
            DocumentoMascota nuevo = new DocumentoMascota(
                    docId,
                    mascotaActual.getCodigo(),
                    tipo,
                    desc != null && !desc.trim().isEmpty() ? desc : "Emitido conforme a revisión médica",
                    LocalDate.now(),
                    true
            );
            repo.agregarDocumento(nuevo);
            cargarMascota(mascotaActual);
            JOptionPane.showMessageDialog(this, "Documento '" + tipo + "' emitido exitosamente para " + mascotaActual.getNombre() + ".", "Emisión Exitosa", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
