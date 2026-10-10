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
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import happypets.data.RepositorioVeterinaria;
import happypets.model.Cliente;
import happypets.model.ConsultaClinica;
import happypets.model.Mascota;
import happypets.ui.Iconos;
import happypets.ui.Ui;

/**
 * Vista 2 del Módulo 1: Historial Clínico de Mascotas.
 * Adaptada a la nueva arquitectura web moderna ERP con:
 * - Cabecera con título, subtítulo y botones de acción.
 * - Barra de búsqueda y selector de período.
 * - 4 Tarjetas KPI de resumen del paciente activo (Paciente, Propietario, Última Atención y Total Consultas).
 * - Tabla del historial con consultas clínicas y botón de apertura de detalle.
 */
public class VistaHistorialClinicoPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private static final Color COLOR_BORDE = Ui.BORDE_SUAVE;
    private static final Color COLOR_AZUL_PRIMARIO = Ui.TURQUESA;
    private static final Color COLOR_TEXTO_TITULO = Ui.TEXTO_TITULO;
    private static final Color COLOR_TEXTO_MUTED = Ui.TEXTO_MUTED;

    private final RepositorioVeterinaria repo = RepositorioVeterinaria.getInstancia();
    private Mascota mascotaActual;
    private Cliente clienteActual;
    private List<ConsultaClinica> listaConsultas;

    // Filtros
    private JTextField txtBuscarMascota;
    private JComboBox<String> cbPeriodo;

    // Labels de las tarjetas KPI del paciente
    private JLabel lblKpiMascotaVal;
    private JLabel lblKpiMascotaSub;
    private JLabel lblKpiClienteVal;
    private JLabel lblKpiClienteSub;
    private JLabel lblKpiUltimaVal;
    private JLabel lblKpiUltimaSub;
    private JLabel lblKpiTotalVal;
    private JLabel lblKpiTotalSub;

    // Tabla de historial
    private JLabel lblContadorConsultas;
    private JTable tablaConsultas;
    private DefaultTableModel modeloConsultas;

    public VistaHistorialClinicoPanel() {
        setLayout(new BorderLayout());
        setOpaque(false);

        JPanel contenido = new JPanel();
        contenido.setOpaque(false);
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setBorder(new EmptyBorder(12, 18, 14, 18));

        // 1. Cabecera con título y acciones
        contenido.add(crearCabeceraVista());
        contenido.add(Box.createVerticalStrut(8));

        // 2. Barra de filtros de búsqueda y período
        contenido.add(crearBarraFiltros());
        contenido.add(Box.createVerticalStrut(10));

        // 3. Fila de 4 Tarjetas KPI con información del paciente activo
        contenido.add(crearFilaKpiPaciente());
        contenido.add(Box.createVerticalStrut(10));

        // 4. Tarjeta con la tabla de consultas del historial clínico
        contenido.add(crearTarjetaTablaHistorial());

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

        // Cargar paciente por defecto del wireframe: Rocky (VET-0091)
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

        JLabel lblTit = new JLabel("Historial Clínico de Mascotas");
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblTit.setForeground(COLOR_TEXTO_TITULO);

        JLabel lblSub = new JLabel("Módulo 1.2 · Consulta cronológica de visitas veterinarias, diagnósticos y plan de tratamiento");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblSub.setForeground(COLOR_TEXTO_MUTED);

        izq.add(lblTit);
        izq.add(Box.createVerticalStrut(2));
        izq.add(lblSub);
        cab.add(izq, BorderLayout.CENTER);

        JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 2));
        der.setOpaque(false);

        JButton btnImprimir = Ui.boton("Imprimir Historial", false);
        btnImprimir.setIcon(Iconos.crearIconoImprimir(12, COLOR_AZUL_PRIMARIO));
        btnImprimir.setIconTextGap(4);
        btnImprimir.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnImprimir.setPreferredSize(new Dimension(btnImprimir.getPreferredSize().width, 28));
        btnImprimir.addActionListener(e -> exportarHistorialDocumento());

        JButton btnExportarPdf = Ui.boton("Exportar PDF / Excel", true);
        btnExportarPdf.setIcon(Iconos.crearIconoDescargar(12, Color.WHITE));
        btnExportarPdf.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnExportarPdf.setPreferredSize(new Dimension(btnExportarPdf.getPreferredSize().width, 28));
        btnExportarPdf.addActionListener(e -> exportarHistorialDocumento());

        JButton btnNuevaConsulta = Ui.boton("+ Nueva Consulta", false);
        btnNuevaConsulta.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnNuevaConsulta.setPreferredSize(new Dimension(btnNuevaConsulta.getPreferredSize().width, 28));
        btnNuevaConsulta.addActionListener(e -> registrarNuevaConsulta());

        der.add(btnImprimir);
        der.add(btnExportarPdf);
        der.add(btnNuevaConsulta);
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

        JLabel lblBuscar = new JLabel("Buscar mascota:");
        lblBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblBuscar.setForeground(COLOR_TEXTO_MUTED);
        izq.add(lblBuscar);

        txtBuscarMascota = new JTextField(12);
        txtBuscarMascota.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        txtBuscarMascota.setPreferredSize(new Dimension(140, 28));
        txtBuscarMascota.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225), 1),
                new EmptyBorder(2, 6, 2, 6)
        ));
        txtBuscarMascota.setToolTipText("Nombre o código de la mascota");
        izq.add(txtBuscarMascota);

        JButton btnBuscar = Ui.boton("Buscar", false);
        btnBuscar.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnBuscar.setIcon(Iconos.crearIconoBuscar(13, COLOR_AZUL_PRIMARIO));
        btnBuscar.setIconTextGap(6);
        btnBuscar.setPreferredSize(new Dimension(92, 28));

        java.awt.event.ActionListener accionBuscar = e -> buscarMascota();
        btnBuscar.addActionListener(accionBuscar);
        txtBuscarMascota.addActionListener(accionBuscar);
        izq.add(btnBuscar);

        izq.add(Box.createHorizontalStrut(10));

        JLabel lblPeriodo = new JLabel("Período:");
        lblPeriodo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblPeriodo.setForeground(COLOR_TEXTO_MUTED);
        izq.add(lblPeriodo);

        cbPeriodo = new JComboBox<>(new String[]{
                "Todas las fechas",
                "Último mes",
                "Últimos 3 meses",
                "Últimos 6 meses",
                "Último año"
        });
        cbPeriodo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        cbPeriodo.setPreferredSize(new Dimension(140, 26));
        cbPeriodo.setBackground(Color.WHITE);
        cbPeriodo.addActionListener(e -> filtrarConsultasPorPeriodo());
        izq.add(cbPeriodo);

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

    /**
     * 4 Tarjetas KPI superiores idénticas a la captura web adaptadas al historial.
     */
    private JPanel crearFilaKpiPaciente() {
        JPanel fila = new JPanel(new GridLayout(1, 4, 16, 0));
        fila.setOpaque(false);

        // 1. Paciente Mascota
        lblKpiMascotaVal = new JLabel("Rocky");
        lblKpiMascotaSub = new JLabel("Canino · Golden Retriever");
        fila.add(crearCardKpi(lblKpiMascotaVal, lblKpiMascotaSub, "PACIENTE SELECCIONADO", Ui.TURQUESA_SUAVE, Iconos.crearIconoHuella(22, COLOR_AZUL_PRIMARIO)));

        // 2. Propietario Responsable
        lblKpiClienteVal = new JLabel("Carlos Morales");
        lblKpiClienteSub = new JLabel("DNI: 45892134");
        fila.add(crearCardKpi(lblKpiClienteVal, lblKpiClienteSub, "PROPIETARIO RESPONSABLE", new Color(254, 243, 199), Iconos.crearIconoClientes(22, new Color(217, 119, 6))));

        // 3. Última Atención
        lblKpiUltimaVal = new JLabel("14/06/2024");
        lblKpiUltimaSub = new JLabel("Dr. R. Mendoza");
        fila.add(crearCardKpi(lblKpiUltimaVal, lblKpiUltimaSub, "ÚLTIMA ATENCIÓN MÉDICA", new Color(220, 252, 231), Iconos.crearIconoEstetoscopio(22, new Color(22, 163, 74))));

        // 4. Total Consultas
        lblKpiTotalVal = new JLabel("3");
        lblKpiTotalSub = new JLabel("Visitas registradas");
        fila.add(crearCardKpi(lblKpiTotalVal, lblKpiTotalSub, "CONSULTAS EN HISTORIAL", new Color(243, 232, 255), Iconos.crearIconoHistorial(22, new Color(147, 51, 234))));

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

    private JPanel crearTarjetaTablaHistorial() {
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

        // Cabecera de la tabla
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JLabel lblTit = new JLabel("Registro Cronológico de Atenciones Clínicas");
        lblTit.setIcon(Iconos.crearIconoHistorial(15, COLOR_AZUL_PRIMARIO));
        lblTit.setIconTextGap(6);
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTit.setForeground(COLOR_TEXTO_TITULO);
        top.add(lblTit, BorderLayout.WEST);

        lblContadorConsultas = new JLabel("3 atenciones médicas");
        lblContadorConsultas.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblContadorConsultas.setForeground(COLOR_TEXTO_MUTED);
        top.add(lblContadorConsultas, BorderLayout.EAST);
        card.add(top, BorderLayout.NORTH);

        // Tabla
        String[] columnas = {"Código", "Fecha", "Motivo Consulta", "Diagnóstico Clínico", "Tratamiento / Receta", "Veterinario", "Estado"};
        modeloConsultas = new DefaultTableModel(columnas, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        tablaConsultas = new JTable(modeloConsultas);
        Ui.formatearTabla(tablaConsultas, new int[]{0, 1, 6}, new int[]{});

        tablaConsultas.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    verDetalleConsultaSeleccionada();
                }
            }
        });

        JScrollPane sp = new JScrollPane(tablaConsultas);
        sp.setPreferredSize(new Dimension(1100, 210));
        sp.setBorder(BorderFactory.createLineBorder(COLOR_BORDE, 1));
        card.add(sp, BorderLayout.CENTER);

        // Pie de la tarjeta con botones de acción
        JPanel bot = new JPanel(new BorderLayout());
        bot.setOpaque(false);

        JLabel lblTip = new JLabel("Doble clic sobre cualquier consulta para ver su diagnóstico y receta detallada");
        lblTip.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblTip.setForeground(COLOR_TEXTO_MUTED);
        bot.add(lblTip, BorderLayout.WEST);

        JPanel botBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 2));
        botBtns.setOpaque(false);

        JButton btnVerDetalle = Ui.boton("Ver Detalle de Consulta", true);
        btnVerDetalle.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnVerDetalle.setPreferredSize(new Dimension(btnVerDetalle.getPreferredSize().width, 28));
        btnVerDetalle.addActionListener(e -> verDetalleConsultaSeleccionada());

        botBtns.add(btnVerDetalle);
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

        listaConsultas = repo.getConsultasPorMascota(m.getCodigo());
        lblKpiTotalVal.setText(String.valueOf(listaConsultas.size()));
        lblKpiTotalSub.setText("Consultas clínicas en historial");

        if (!listaConsultas.isEmpty()) {
            ConsultaClinica ultima = listaConsultas.get(0);
            lblKpiUltimaVal.setText(ultima.getFechaFormateada());
            lblKpiUltimaSub.setText(ultima.getVeterinario() + " · " + ultima.getMotivo());
        } else {
            lblKpiUltimaVal.setText("Sin consultas");
            lblKpiUltimaSub.setText("-");
        }

        filtrarConsultasPorPeriodo();
    }

    private void filtrarConsultasPorPeriodo() {
        modeloConsultas.setRowCount(0);
        if (mascotaActual == null || listaConsultas == null) return;

        String periodo = (String) cbPeriodo.getSelectedItem();
        LocalDate limite = null;
        LocalDate hoy = LocalDate.now();

        if ("Último mes".equals(periodo)) limite = hoy.minusMonths(1);
        else if ("Últimos 3 meses".equals(periodo)) limite = hoy.minusMonths(3);
        else if ("Últimos 6 meses".equals(periodo)) limite = hoy.minusMonths(6);
        else if ("Último año".equals(periodo)) limite = hoy.minusYears(1);

        int count = 0;
        for (ConsultaClinica c : listaConsultas) {
            if (limite == null || !c.getFecha().isBefore(limite)) {
                count++;
                modeloConsultas.addRow(new Object[]{
                        c.getCodigo(),
                        c.getFechaFormateada(),
                        c.getMotivo(),
                        c.getDiagnostico(),
                        c.getTratamiento(),
                        c.getVeterinario(),
                        c.getEstado()
                });
            }
        }
        lblContadorConsultas.setText(count + " consulta(s) encontrada(s)");
    }

    private void buscarMascota() {
        String query = txtBuscarMascota.getText().trim();
        if (query.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese el nombre o código de la mascota a consultar.", "Búsqueda", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Optional<Mascota> opt = repo.buscarMascotaPorCodigoONombre(query);
        if (opt.isPresent()) {
            cargarMascota(opt.get());
        } else {
            JOptionPane.showMessageDialog(this, "No se encontró ninguna mascota con el término: " + query, "No Encontrado", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void verDetalleConsultaSeleccionada() {
        int row = tablaConsultas.getSelectedRow();
        if (row < 0 || listaConsultas == null) {
            JOptionPane.showMessageDialog(this, "Seleccione una consulta en la tabla para ver su detalle.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String cod = (String) modeloConsultas.getValueAt(row, 0);
        ConsultaClinica c = listaConsultas.stream().filter(con -> con.getCodigo().equals(cod)).findFirst().orElse(null);
        if (c != null) {
            DetalleConsultaDialog dlg = new DetalleConsultaDialog(null, c, mascotaActual);
            dlg.setVisible(true);
        }
    }

    private void registrarNuevaConsulta() {
        if (mascotaActual == null) {
            JOptionPane.showMessageDialog(this, "Seleccione primero una mascota para registrarle una consulta.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String motivo = JOptionPane.showInputDialog(this, "Ingrese el motivo de la consulta médica para " + mascotaActual.getNombre() + ":", "Nueva Consulta", JOptionPane.QUESTION_MESSAGE);
        if (motivo != null && !motivo.trim().isEmpty()) {
            String diag = JOptionPane.showInputDialog(this, "Ingrese el diagnóstico preliminar:", "Diagnóstico", JOptionPane.QUESTION_MESSAGE);
            String trat = JOptionPane.showInputDialog(this, "Ingrese el tratamiento o prescripción:", "Tratamiento", JOptionPane.QUESTION_MESSAGE);

            String cod = "HC-00" + (listaConsultas != null ? (listaConsultas.size() + 1) : 1);
            ConsultaClinica nueva = new ConsultaClinica(
                    cod,
                    mascotaActual.getCodigo(),
                    LocalDate.now(),
                    motivo,
                    "Revisión médica general",
                    diag != null ? diag : "En evaluación",
                    trat != null ? trat : "Sin tratamiento",
                    "Dr. R. Mendoza",
                    mascotaActual.getPesoActualKg(),
                    38.5,
                    "Consulta ingresada en el sistema",
                    "Cerrada"
            );
            repo.agregarConsulta(nueva);
            cargarMascota(mascotaActual);
            JOptionPane.showMessageDialog(this, "Consulta médica registrada correctamente en el historial.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void exportarHistorialDocumento() {
        if (mascotaActual == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un paciente para generar el reporte de historial clínico.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int rows = modeloConsultas.getRowCount();
        String[][] datos = new String[rows][5];
        for (int i = 0; i < rows; i++) {
            datos[i][0] = String.valueOf(modeloConsultas.getValueAt(i, 0));
            datos[i][1] = String.valueOf(modeloConsultas.getValueAt(i, 1));
            datos[i][2] = String.valueOf(modeloConsultas.getValueAt(i, 2));
            datos[i][3] = String.valueOf(modeloConsultas.getValueAt(i, 3));
            datos[i][4] = String.valueOf(modeloConsultas.getValueAt(i, 5));
        }

        Ui.mostrarVisorReporte(
                (java.awt.Frame) SwingUtilities.getWindowAncestor(this),
                "HISTORIAL CLÍNICO VETERINARIO",
                "Paciente: " + mascotaActual.getNombre() + " (" + mascotaActual.getEspecieRaza() + ") | Código: " + mascotaActual.getCodigo(),
                "Propietario: " + (clienteActual != null ? clienteActual.getNombreCompleto() : "N/D") + " | Total Consultas: " + rows,
                new String[]{"Código", "Fecha", "Motivo", "Diagnóstico", "Veterinario"},
                datos,
                "ESTADO: PACIENTE CONTROLADO · HISTORIAL COMPLETO",
                "HistorialClinico_" + mascotaActual.getNombre()
        );
    }
}
