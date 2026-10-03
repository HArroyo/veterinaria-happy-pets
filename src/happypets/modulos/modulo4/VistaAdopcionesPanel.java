package happypets.modulos.modulo4;

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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import happypets.data.RepositorioVeterinaria;
import happypets.model.MascotaAdopcion;
import happypets.ui.Iconos;
import happypets.ui.Ui;

/**
 * Submódulo 4.4: Adopciones y Rescates Responsables.
 * Diseñado según el wireframe oficial y requerimientos ERP Happy Pets:
 * - Catálogo clínico de animales rescatados y rehabilitados.
 * - Validación sanitaria estricta (esterilización, vacunación y desparasitación).
 * - Recepción y evaluación de postulantes adoptantes.
 * - Emisión formal de Acta Legal de Adopción Responsable y difusión social.
 */
public class VistaAdopcionesPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private static final Color COLOR_BORDE = new Color(226, 232, 240);
    private static final Color COLOR_AZUL_PRIMARIO = new Color(2, 132, 199);
    private static final Color COLOR_TEXTO_TITULO = new Color(30, 41, 59);
    private static final Color COLOR_TEXTO_MUTED = new Color(100, 116, 139);

    private final RepositorioVeterinaria repo = RepositorioVeterinaria.getInstancia();

    // KPIs
    private JLabel lblKpiDisponibles;
    private JLabel lblKpiEvaluacion;
    private JLabel lblKpiAdaptacion;
    private JLabel lblKpiAdoptados;

    // Formulario de Postulación / Rescatado
    private JComboBox<MascotaItem> cbMascotasRefugio;
    private JTextField txtNombreRescatado;
    private JComboBox<String> cbEspecie;
    private JTextField txtRaza;
    private JTextField txtEdad;
    private JComboBox<String> cbSexo;
    private JComboBox<String> cbTamano;
    private JCheckBox chkEsterilizado;
    private JCheckBox chkVacunado;
    private JCheckBox chkDesparasitado;
    private JTextField txtTemperamento;

    // Datos del Adoptante
    private JTextField txtAdoptanteNombre;
    private JTextField txtAdoptanteDni;
    private JTextField txtAdoptanteTelefono;
    private JTextField txtAdoptanteDireccion;
    private JTextField txtDonacion;

    // Tabla de Catálogo y Adopciones
    private JTable tablaAdopciones;
    private DefaultTableModel modeloAdopciones;
    private JLabel lblContadorAdopciones;
    private JComboBox<String> cbFiltroEstado;
    private List<MascotaAdopcion> listaActual;

    public VistaAdopcionesPanel() {
        setLayout(new BorderLayout());
        setOpaque(false);

        JPanel contenido = new JPanel();
        contenido.setOpaque(false);
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setBorder(new EmptyBorder(12, 18, 14, 18));

        // 1. Cabecera
        contenido.add(crearCabeceraVista());
        contenido.add(Box.createVerticalStrut(8));

        // 2. Fila de KPIs
        contenido.add(crearFilaKpis());
        contenido.add(Box.createVerticalStrut(10));

        // 3. Doble Columna: Formulario y Catálogo
        contenido.add(crearDobleColumna());

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(contenido, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(wrapper);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);

        recargarDatos();
    }

    private JPanel crearCabeceraVista() {
        JPanel cab = new JPanel(new BorderLayout());
        cab.setOpaque(false);

        JPanel izq = new JPanel();
        izq.setOpaque(false);
        izq.setLayout(new BoxLayout(izq, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("Adopciones y Rescates Responsables");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 17));
        titulo.setForeground(COLOR_TEXTO_TITULO);

        JLabel subtitulo = new JLabel("Módulo 4.4 · Catálogo de rescatados, perfil sanitario, evaluación de postulantes y actas de compromiso");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitulo.setForeground(COLOR_TEXTO_MUTED);

        izq.add(titulo);
        izq.add(Box.createVerticalStrut(2));
        izq.add(subtitulo);
        cab.add(izq, BorderLayout.CENTER);

        JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        der.setOpaque(false);

        JLabel badgeRefugio = new JLabel(" Programa de Tenencia Responsable · Rescate Happy Pets ", SwingConstants.CENTER);
        badgeRefugio.setFont(new Font("Segoe UI", Font.BOLD, 11));
        badgeRefugio.setForeground(new Color(2, 132, 199));
        badgeRefugio.setOpaque(true);
        badgeRefugio.setBackground(new Color(224, 242, 254));
        badgeRefugio.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(186, 230, 253), 1, true),
                new EmptyBorder(4, 10, 4, 10)
        ));
        der.add(badgeRefugio);

        cab.add(der, BorderLayout.EAST);
        return cab;
    }

    private JPanel crearFilaKpis() {
        JPanel kpis = new JPanel(new GridLayout(1, 4, 10, 0));
        kpis.setOpaque(false);

        lblKpiDisponibles = new JLabel("0", SwingConstants.LEFT);
        lblKpiEvaluacion = new JLabel("0", SwingConstants.LEFT);
        lblKpiAdaptacion = new JLabel("0", SwingConstants.LEFT);
        lblKpiAdoptados = new JLabel("0", SwingConstants.LEFT);

        kpis.add(crearTarjetaKpi("Disponibles para Adopción", lblKpiDisponibles, "Listos para un hogar", new Color(14, 165, 233), Iconos.crearIconoCorazonMascota(18, new Color(14, 165, 233))));
        kpis.add(crearTarjetaKpi("Postulaciones en Revisión", lblKpiEvaluacion, "Entrevistas a tutores", new Color(245, 158, 11), Iconos.crearIconoDocumento(16, new Color(245, 158, 11))));
        kpis.add(crearTarjetaKpi("En Período de Prueba", lblKpiAdaptacion, "Adaptación 15 días", new Color(99, 102, 241), Iconos.crearIconoReloj(16, new Color(99, 102, 241))));
        kpis.add(crearTarjetaKpi("Adopciones Exitosas", lblKpiAdoptados, "Hogares definitivos", new Color(16, 185, 129), Iconos.crearIconoCheck(16, new Color(16, 185, 129))));

        return kpis;
    }

    private JPanel crearTarjetaKpi(String titulo, JLabel lblValor, String subtitulo, Color colorAcento, Icon icono) {
        JPanel card = new JPanel(new BorderLayout(8, 4)) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(COLOR_BORDE);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(8, 12, 8, 12));

        JPanel izq = new JPanel();
        izq.setOpaque(false);
        izq.setLayout(new BoxLayout(izq, BoxLayout.Y_AXIS));

        JLabel t = new JLabel(titulo);
        t.setFont(new Font("Segoe UI", Font.BOLD, 10));
        t.setForeground(COLOR_TEXTO_MUTED);

        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblValor.setForeground(COLOR_TEXTO_TITULO);

        JLabel sub = new JLabel(subtitulo);
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        sub.setForeground(COLOR_TEXTO_MUTED);

        izq.add(t);
        izq.add(Box.createVerticalStrut(2));
        izq.add(lblValor);
        izq.add(Box.createVerticalStrut(1));
        izq.add(sub);

        card.add(izq, BorderLayout.CENTER);

        if (icono != null) {
            JLabel lblIcono = new JLabel(icono);
            lblIcono.setVerticalAlignment(SwingConstants.TOP);
            card.add(lblIcono, BorderLayout.EAST);
        }

        return card;
    }

    private JPanel crearDobleColumna() {
        JPanel fila = new JPanel(new BorderLayout(14, 0));
        fila.setOpaque(false);

        // Columna Izquierda: Formulario (ancho 420px aprox)
        JPanel colIzquierda = crearPanelFormularioAdopcion();
        colIzquierda.setPreferredSize(new Dimension(420, 580));
        fila.add(colIzquierda, BorderLayout.WEST);

        // Columna Derecha: Catálogo y Gestión
        JPanel colDerecha = crearPanelCatalogoAdopciones();
        fila.add(colDerecha, BorderLayout.CENTER);

        return fila;
    }

    private JPanel crearPanelFormularioAdopcion() {
        JPanel card = new JPanel(new BorderLayout()) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(COLOR_BORDE);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(12, 14, 12, 14));

        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));

        // Título de la sección
        JLabel lblTitulo = new JLabel("Postulación de Adoptante / Rescatado");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTitulo.setForeground(COLOR_TEXTO_TITULO);
        form.add(lblTitulo);

        JLabel lblSub = new JLabel("Vincule un postulante evaluado o agregue un nuevo rescatado");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblSub.setForeground(COLOR_TEXTO_MUTED);
        form.add(lblSub);
        form.add(Box.createVerticalStrut(8));

        // 1. Selector de Rescatado
        form.add(crearEtiquetaCampo("Seleccionar Rescatado del Catálogo:"));
        cbMascotasRefugio = new JComboBox<>();
        estilizarControl(cbMascotasRefugio);
        cbMascotasRefugio.addActionListener(e -> cargarDatosMascotaSeleccionada());
        form.add(cbMascotasRefugio);
        form.add(Box.createVerticalStrut(6));

        // 2. Ficha del Rescatado (Grid)
        JPanel gridDatos = new JPanel(new GridLayout(1, 2, 8, 0));
        gridDatos.setOpaque(false);

        JPanel pNom = new JPanel();
        pNom.setOpaque(false);
        pNom.setLayout(new BoxLayout(pNom, BoxLayout.Y_AXIS));
        pNom.add(crearEtiquetaCampo("Nombre Rescatado:"));
        txtNombreRescatado = new JTextField();
        estilizarControl(txtNombreRescatado);
        pNom.add(txtNombreRescatado);
        gridDatos.add(pNom);

        JPanel pEsp = new JPanel();
        pEsp.setOpaque(false);
        pEsp.setLayout(new BoxLayout(pEsp, BoxLayout.Y_AXIS));
        pEsp.add(crearEtiquetaCampo("Especie:"));
        cbEspecie = new JComboBox<>(new String[]{"Canino", "Felino"});
        estilizarControl(cbEspecie);
        pEsp.add(cbEspecie);
        gridDatos.add(pEsp);

        form.add(gridDatos);
        form.add(Box.createVerticalStrut(6));

        // Raza, Edad y Sexo
        JPanel gridDetalles = new JPanel(new GridLayout(1, 3, 6, 0));
        gridDetalles.setOpaque(false);

        JPanel pRaza = new JPanel();
        pRaza.setOpaque(false);
        pRaza.setLayout(new BoxLayout(pRaza, BoxLayout.Y_AXIS));
        pRaza.add(crearEtiquetaCampo("Raza / Tipo:"));
        txtRaza = new JTextField();
        estilizarControl(txtRaza);
        pRaza.add(txtRaza);
        gridDetalles.add(pRaza);

        JPanel pEdad = new JPanel();
        pEdad.setOpaque(false);
        pEdad.setLayout(new BoxLayout(pEdad, BoxLayout.Y_AXIS));
        pEdad.add(crearEtiquetaCampo("Edad Estim.:"));
        txtEdad = new JTextField();
        estilizarControl(txtEdad);
        pEdad.add(txtEdad);
        gridDetalles.add(pEdad);

        JPanel pSexo = new JPanel();
        pSexo.setOpaque(false);
        pSexo.setLayout(new BoxLayout(pSexo, BoxLayout.Y_AXIS));
        pSexo.add(crearEtiquetaCampo("Sexo:"));
        cbSexo = new JComboBox<>(new String[]{"Macho", "Hembra"});
        estilizarControl(cbSexo);
        pSexo.add(cbSexo);
        gridDetalles.add(pSexo);

        form.add(gridDetalles);
        form.add(Box.createVerticalStrut(6));

        // Checklist Sanitario
        form.add(crearEtiquetaCampo("Estado Sanitario Obligatorio:"));
        JPanel pChecklist = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        pChecklist.setOpaque(false);

        chkEsterilizado = new JCheckBox("Esterilizado/a");
        chkEsterilizado.setFont(new Font("Segoe UI", Font.BOLD, 10));
        chkEsterilizado.setForeground(new Color(16, 185, 129));
        chkEsterilizado.setOpaque(false);
        chkEsterilizado.setSelected(true);

        chkVacunado = new JCheckBox("Vacunación al día");
        chkVacunado.setFont(new Font("Segoe UI", Font.BOLD, 10));
        chkVacunado.setForeground(new Color(16, 185, 129));
        chkVacunado.setOpaque(false);
        chkVacunado.setSelected(true);

        chkDesparasitado = new JCheckBox("Desparasitado/a");
        chkDesparasitado.setFont(new Font("Segoe UI", Font.BOLD, 10));
        chkDesparasitado.setForeground(new Color(16, 185, 129));
        chkDesparasitado.setOpaque(false);
        chkDesparasitado.setSelected(true);

        pChecklist.add(chkEsterilizado);
        pChecklist.add(chkVacunado);
        pChecklist.add(chkDesparasitado);
        form.add(pChecklist);
        form.add(Box.createVerticalStrut(6));

        // Temperamento
        form.add(crearEtiquetaCampo("Temperamento / Compatibilidad:"));
        txtTemperamento = new JTextField("Sociable con otros perros y niños");
        estilizarControl(txtTemperamento);
        form.add(txtTemperamento);
        form.add(Box.createVerticalStrut(8));

        // Separador con título de Adoptante
        JLabel lblSeccAdoptante = new JLabel("Datos del Postulante / Adoptante");
        lblSeccAdoptante.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblSeccAdoptante.setForeground(COLOR_AZUL_PRIMARIO);
        form.add(lblSeccAdoptante);
        form.add(Box.createVerticalStrut(4));

        // Nombre y DNI
        JPanel gridPostulante = new JPanel(new GridLayout(1, 2, 8, 0));
        gridPostulante.setOpaque(false);

        JPanel pAdNom = new JPanel();
        pAdNom.setOpaque(false);
        pAdNom.setLayout(new BoxLayout(pAdNom, BoxLayout.Y_AXIS));
        pAdNom.add(crearEtiquetaCampo("Nombre Postulante:"));
        txtAdoptanteNombre = new JTextField();
        estilizarControl(txtAdoptanteNombre);
        pAdNom.add(txtAdoptanteNombre);
        gridPostulante.add(pAdNom);

        JPanel pAdDni = new JPanel();
        pAdDni.setOpaque(false);
        pAdDni.setLayout(new BoxLayout(pAdDni, BoxLayout.Y_AXIS));
        pAdDni.add(crearEtiquetaCampo("DNI:"));
        txtAdoptanteDni = new JTextField();
        estilizarControl(txtAdoptanteDni);
        pAdDni.add(txtAdoptanteDni);
        gridPostulante.add(pAdDni);

        form.add(gridPostulante);
        form.add(Box.createVerticalStrut(6));

        // Teléfono y Dirección
        JPanel gridContacto = new JPanel(new GridLayout(1, 2, 8, 0));
        gridContacto.setOpaque(false);

        JPanel pAdTel = new JPanel();
        pAdTel.setOpaque(false);
        pAdTel.setLayout(new BoxLayout(pAdTel, BoxLayout.Y_AXIS));
        pAdTel.add(crearEtiquetaCampo("Teléfono WhatsApp:"));
        txtAdoptanteTelefono = new JTextField();
        estilizarControl(txtAdoptanteTelefono);
        pAdTel.add(txtAdoptanteTelefono);
        gridContacto.add(pAdTel);

        JPanel pAdDon = new JPanel();
        pAdDon.setOpaque(false);
        pAdDon.setLayout(new BoxLayout(pAdDon, BoxLayout.Y_AXIS));
        pAdDon.add(crearEtiquetaCampo("Donación Fondo S/.:"));
        txtDonacion = new JTextField("50.00");
        estilizarControl(txtDonacion);
        pAdDon.add(txtDonacion);
        gridContacto.add(pAdDon);

        form.add(gridContacto);
        form.add(Box.createVerticalStrut(6));

        form.add(crearEtiquetaCampo("Dirección y Tipo de Vivienda:"));
        txtAdoptanteDireccion = new JTextField("Casa propia con jardín cercado");
        estilizarControl(txtAdoptanteDireccion);
        form.add(txtAdoptanteDireccion);
        form.add(Box.createVerticalStrut(10));

        // Botones de acción
        JPanel pBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        pBotones.setOpaque(false);

        JButton btnNuevoRescatado = crearBoton("+ Nuevo Rescatado", false, this::guardarNuevoRescatado);
        JButton btnRegistrarPost = crearBoton("Registrar Postulación", true, this::registrarPostulacion);

        pBotones.add(btnNuevoRescatado);
        pBotones.add(btnRegistrarPost);
        form.add(pBotones);

        card.add(form, BorderLayout.CENTER);
        return card;
    }

    private JPanel crearPanelCatalogoAdopciones() {
        JPanel card = new JPanel(new BorderLayout(0, 8)) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(COLOR_BORDE);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(12, 14, 12, 14));

        // Cabecera de la tabla con filtro
        JPanel top = new JPanel(new BorderLayout(8, 0));
        top.setOpaque(false);

        JPanel izq = new JPanel();
        izq.setOpaque(false);
        izq.setLayout(new BoxLayout(izq, BoxLayout.Y_AXIS));

        JLabel lblTit = new JLabel("Catálogo de Rescatados & Seguimiento de Solicitudes");
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTit.setForeground(COLOR_TEXTO_TITULO);

        lblContadorAdopciones = new JLabel("0 mascotas registradas");
        lblContadorAdopciones.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblContadorAdopciones.setForeground(COLOR_TEXTO_MUTED);

        izq.add(lblTit);
        izq.add(Box.createVerticalStrut(1));
        izq.add(lblContadorAdopciones);
        top.add(izq, BorderLayout.CENTER);

        JPanel derFiltro = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        derFiltro.setOpaque(false);

        JLabel lblFiltro = new JLabel("Estado:");
        lblFiltro.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblFiltro.setForeground(COLOR_TEXTO_MUTED);
        derFiltro.add(lblFiltro);

        cbFiltroEstado = new JComboBox<>(new String[]{"Todos", "Disponible", "En Evaluación", "En Adaptación (Prueba)", "Adoptado con Éxito"});
        estilizarControl(cbFiltroEstado);
        cbFiltroEstado.addActionListener(e -> filtrarTabla());
        derFiltro.add(cbFiltroEstado);

        top.add(derFiltro, BorderLayout.EAST);
        card.add(top, BorderLayout.NORTH);

        // Tabla
        String[] columnas = {"ID", "Nombre", "Especie / Raza", "Edad", "Sanidad", "Postulante", "Donación", "Estado"};
        modeloAdopciones = new DefaultTableModel(columnas, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        tablaAdopciones = new JTable(modeloAdopciones);
        tablaAdopciones.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        tablaAdopciones.setRowHeight(26);
        tablaAdopciones.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        tablaAdopciones.getTableHeader().setBackground(new Color(248, 250, 252));
        tablaAdopciones.getTableHeader().setForeground(new Color(71, 85, 105));
        tablaAdopciones.setSelectionBackground(new Color(224, 242, 254));
        tablaAdopciones.setSelectionForeground(new Color(3, 105, 161));
        tablaAdopciones.setShowGrid(false);
        tablaAdopciones.setIntercellSpacing(new Dimension(0, 0));

        tablaAdopciones.getColumnModel().getColumn(0).setPreferredWidth(65);
        tablaAdopciones.getColumnModel().getColumn(1).setPreferredWidth(85);
        tablaAdopciones.getColumnModel().getColumn(2).setPreferredWidth(110);
        tablaAdopciones.getColumnModel().getColumn(3).setPreferredWidth(65);
        tablaAdopciones.getColumnModel().getColumn(4).setPreferredWidth(100);
        tablaAdopciones.getColumnModel().getColumn(5).setPreferredWidth(110);
        tablaAdopciones.getColumnModel().getColumn(6).setPreferredWidth(65);
        tablaAdopciones.getColumnModel().getColumn(7).setPreferredWidth(125);

        // Renderizador de Estado
        tablaAdopciones.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
            private static final long serialVersionUID = 1L;
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                l.setHorizontalAlignment(SwingConstants.CENTER);
                String st = value != null ? value.toString() : "";
                if (st.contains("Disponible")) {
                    l.setForeground(new Color(2, 132, 199));
                    l.setBackground(new Color(224, 242, 254));
                } else if (st.contains("En Evaluación")) {
                    l.setForeground(new Color(245, 158, 11));
                    l.setBackground(new Color(254, 243, 199));
                } else if (st.contains("Adaptación")) {
                    l.setForeground(new Color(99, 102, 241));
                    l.setBackground(new Color(238, 242, 255));
                } else {
                    l.setForeground(new Color(16, 185, 129));
                    l.setBackground(new Color(209, 250, 229));
                }
                l.setOpaque(true);
                return l;
            }
        });

        JScrollPane scrollTabla = new JScrollPane(tablaAdopciones);
        scrollTabla.setBorder(BorderFactory.createLineBorder(COLOR_BORDE, 1));
        scrollTabla.getViewport().setBackground(Color.WHITE);
        card.add(scrollTabla, BorderLayout.CENTER);

        // Barra inferior de acciones
        JPanel pAcciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        pAcciones.setOpaque(false);

        JButton btnDifusion = crearBoton("Difusión WhatsApp", false, this::generarDifusionWhatsapp);
        btnDifusion.setIcon(Iconos.crearIconoMensaje(14, new Color(16, 185, 129)));

        JButton btnAprobarPrueba = crearBoton("Pasar a Adaptación", false, this::pasarAAdaptacion);
        btnAprobarPrueba.setIcon(Iconos.crearIconoReloj(14, new Color(99, 102, 241)));

        JButton btnFinalizarActa = crearBoton("Finalizar y Emitir Acta", true, this::finalizarActaAdopcion);
        btnFinalizarActa.setIcon(Iconos.crearIconoCheck(14, Color.WHITE));

        pAcciones.add(btnDifusion);
        pAcciones.add(btnAprobarPrueba);
        pAcciones.add(btnFinalizarActa);

        card.add(pAcciones, BorderLayout.SOUTH);
        return card;
    }

    private JLabel crearEtiquetaCampo(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(new Font("Segoe UI", Font.BOLD, 10));
        l.setForeground(new Color(71, 85, 105));
        l.setBorder(new EmptyBorder(0, 0, 2, 0));
        return l;
    }

    private void estilizarControl(Component c) {
        c.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        c.setPreferredSize(new Dimension(c.getPreferredSize().width, 27));
        c.setMaximumSize(new Dimension(Integer.MAX_VALUE, 27));
        if (c instanceof JTextField) {
            ((JTextField) c).setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(COLOR_BORDE, 1, true),
                    new EmptyBorder(2, 6, 2, 6)
            ));
        } else if (c instanceof JComboBox) {
            c.setBackground(Color.WHITE);
        }
    }

    private JButton crearBoton(String texto, boolean primario, Runnable accion) {
        JButton btn = new JButton(texto) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (primario) {
                    g2.setColor(getModel().isRollover() ? new Color(3, 105, 161) : COLOR_AZUL_PRIMARIO);
                } else {
                    g2.setColor(getModel().isRollover() ? new Color(241, 245, 249) : Color.WHITE);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                if (!primario) {
                    g2.setColor(COLOR_BORDE);
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 6, 6);
                }
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btn.setForeground(primario ? Color.WHITE : new Color(51, 65, 85));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(5, 12, 5, 12));
        btn.setPreferredSize(new Dimension(btn.getPreferredSize().width, 28));
        btn.addActionListener(e -> accion.run());
        return btn;
    }

    public void recargarDatos() {
        listaActual = repo.getMascotasAdopcion();

        cbMascotasRefugio.removeAllItems();
        cbMascotasRefugio.addItem(new MascotaItem(null, "--- Seleccionar Rescatado para Postulación ---"));
        for (MascotaAdopcion ma : listaActual) {
            cbMascotasRefugio.addItem(new MascotaItem(ma, ma.getIdMascotaAdopcion() + " · " + ma.getNombre() + " (" + ma.getEstado() + ")"));
        }

        filtrarTabla();

        // KPIs
        long disp = listaActual.stream().filter(m -> "Disponible".equalsIgnoreCase(m.getEstado())).count();
        long eval = listaActual.stream().filter(m -> "En Evaluación".equalsIgnoreCase(m.getEstado())).count();
        long adap = listaActual.stream().filter(m -> m.getEstado().contains("Adaptación")).count();
        long adop = listaActual.stream().filter(m -> m.getEstado().contains("Adoptado")).count();

        lblKpiDisponibles.setText(String.valueOf(disp));
        lblKpiEvaluacion.setText(String.valueOf(eval));
        lblKpiAdaptacion.setText(String.valueOf(adap));
        lblKpiAdoptados.setText(String.valueOf(adop));
    }

    private void filtrarTabla() {
        if (listaActual == null) return;
        modeloAdopciones.setRowCount(0);
        String filtro = cbFiltroEstado != null ? (String) cbFiltroEstado.getSelectedItem() : "Todos";

        int cont = 0;
        for (MascotaAdopcion m : listaActual) {
            if (!"Todos".equalsIgnoreCase(filtro)) {
                if (!m.getEstado().equalsIgnoreCase(filtro)) continue;
            }
            cont++;

            String sanidad = (m.isEsterilizado() ? "✓ Est." : "No Est.") + " | " +
                    (m.isVacunado() ? "✓ Vac." : "Pend.") + " | " +
                    (m.isDesparasitado() ? "✓ Desp." : "Pend.");

            modeloAdopciones.addRow(new Object[]{
                    m.getIdMascotaAdopcion(),
                    m.getNombre(),
                    m.getEspecie() + " · " + m.getRaza(),
                    m.getEdadEstimada() + " (" + m.getSexo() + ")",
                    sanidad,
                    m.getAdoptanteNombre() != null && !m.getAdoptanteNombre().isEmpty() ? m.getAdoptanteNombre() : "Sin postulante",
                    "S/. " + String.format("%.2f", m.getCuotaDonacion()),
                    m.getEstado()
            });
        }
        lblContadorAdopciones.setText(cont + " rescatados listados");
    }

    private void cargarDatosMascotaSeleccionada() {
        MascotaItem item = (MascotaItem) cbMascotasRefugio.getSelectedItem();
        if (item != null && item.mascota != null) {
            MascotaAdopcion m = item.mascota;
            txtNombreRescatado.setText(m.getNombre());
            cbEspecie.setSelectedItem(m.getEspecie());
            txtRaza.setText(m.getRaza());
            txtEdad.setText(m.getEdadEstimada());
            cbSexo.setSelectedItem(m.getSexo());
            chkEsterilizado.setSelected(m.isEsterilizado());
            chkVacunado.setSelected(m.isVacunado());
            chkDesparasitado.setSelected(m.isDesparasitado());
            txtTemperamento.setText(m.getTemperamento());

            if (m.getAdoptanteNombre() != null) txtAdoptanteNombre.setText(m.getAdoptanteNombre());
            if (m.getAdoptanteDni() != null) txtAdoptanteDni.setText(m.getAdoptanteDni());
            if (m.getAdoptanteTelefono() != null) txtAdoptanteTelefono.setText(m.getAdoptanteTelefono());
            if (m.getAdoptanteDireccion() != null) txtAdoptanteDireccion.setText(m.getAdoptanteDireccion());
        }
    }

    private void guardarNuevoRescatado() {
        String nom = txtNombreRescatado.getText().trim();
        if (nom.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese el nombre del rescatado para ingresarlo al catálogo.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        MascotaAdopcion nuevo = new MascotaAdopcion(
                null,
                nom,
                (String) cbEspecie.getSelectedItem(),
                txtRaza.getText().trim(),
                txtEdad.getText().trim(),
                (String) cbSexo.getSelectedItem(),
                "Mediano",
                txtTemperamento.getText().trim(),
                "Rescatado en operativo y rehabilitado clínicamente en Happy Pets",
                chkEsterilizado.isSelected(),
                chkVacunado.isSelected(),
                chkDesparasitado.isSelected(),
                "",
                "",
                "",
                "",
                null,
                50.0,
                "Disponible"
        );

        repo.guardarMascotaAdopcion(nuevo);
        recargarDatos();
        JOptionPane.showMessageDialog(this, "¡Rescatado " + nom + " registrado en el catálogo oficial de adopciones!", "Registro Exitoso", JOptionPane.INFORMATION_MESSAGE);
    }

    private void registrarPostulacion() {
        MascotaItem item = (MascotaItem) cbMascotasRefugio.getSelectedItem();
        if (item == null || item.mascota == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un rescatado del catálogo para vincular la postulación.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String postNombre = txtAdoptanteNombre.getText().trim();
        String postDni = txtAdoptanteDni.getText().trim();
        String postTel = txtAdoptanteTelefono.getText().trim();
        String postDir = txtAdoptanteDireccion.getText().trim();

        if (postNombre.isEmpty() || postDni.isEmpty() || postTel.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Complete el nombre, DNI y teléfono del postulante.", "Datos Incompletos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        repo.actualizarEstadoAdopcion(item.mascota.getIdMascotaAdopcion(), "En Evaluación", postNombre, postDni, postTel, postDir);
        recargarDatos();

        JOptionPane.showMessageDialog(this,
                "Postulación registrada correctamente para " + item.mascota.getNombre() + ".\n\n" +
                        "Adoptante: " + postNombre + " (DNI: " + postDni + ")\n" +
                        "Contacto WhatsApp: " + postTel + "\n" +
                        "Estado: En Evaluación (Entrevista domiciliaria pendiente)",
                "Postulación Registrada", JOptionPane.INFORMATION_MESSAGE);
    }

    private void pasarAAdaptacion() {
        int fila = tablaAdopciones.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un rescatado de la tabla para iniciar el período de prueba.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String idMascota = (String) modeloAdopciones.getValueAt(fila, 0);
        Optional<MascotaAdopcion> opt = repo.getMascotasAdopcion().stream().filter(m -> m.getIdMascotaAdopcion().equalsIgnoreCase(idMascota)).findFirst();
        if (!opt.isPresent()) return;

        MascotaAdopcion m = opt.get();
        if (m.getAdoptanteNombre() == null || m.getAdoptanteNombre().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe registrar un postulante antes de pasar a la etapa de adaptación.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        repo.actualizarEstadoAdopcion(m.getIdMascotaAdopcion(), "En Adaptación (Prueba)", null, null, null, null);
        recargarDatos();

        JOptionPane.showMessageDialog(this,
                "¡Período de adaptación de 15 días iniciado!\n\n" +
                        "Mascota: " + m.getNombre() + "\n" +
                        "Tutor Temporal: " + m.getAdoptanteNombre() + " (" + m.getAdoptanteTelefono() + ")\n" +
                        "Se programará un seguimiento veterinario telefónico en 7 días.",
                "Período de Prueba Iniciado", JOptionPane.INFORMATION_MESSAGE);
    }

    private void finalizarActaAdopcion() {
        int fila = tablaAdopciones.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un adoptado para emitir el Acta de Compromiso Legal.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String idMascota = (String) modeloAdopciones.getValueAt(fila, 0);
        Optional<MascotaAdopcion> opt = repo.getMascotasAdopcion().stream().filter(m -> m.getIdMascotaAdopcion().equalsIgnoreCase(idMascota)).findFirst();
        if (!opt.isPresent()) return;

        MascotaAdopcion m = opt.get();
        if (m.getAdoptanteNombre() == null || m.getAdoptanteNombre().isEmpty()) {
            JOptionPane.showMessageDialog(this, "No se puede emitir el acta sin un adoptante registrado.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        repo.actualizarEstadoAdopcion(m.getIdMascotaAdopcion(), "Adoptado con Éxito", null, null, null, null);
        recargarDatos();

        String acta = "ACTA OFICIAL DE ADOPCIÓN RESPONSABLE Y COMPROMISO ÉTICO-LEGAL\n" +
                "CLÍNICA VETERINARIA HAPPY PETS · PROGRAMA RESCATE & VIDA\n" +
                "=========================================================================\n\n" +
                "NÚMERO DE EXPEDIENTE: " + m.getIdMascotaAdopcion() + "\n" +
                "FECHA DE ADOPCIÓN   : " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + "\n\n" +
                "DATOS DEL ANIMAL ADOPTADO:\n" +
                "  • Nombre Asignado : " + m.getNombre() + "\n" +
                "  • Especie / Raza  : " + m.getEspecie() + " · " + m.getRaza() + "\n" +
                "  • Edad Estimada   : " + m.getEdadEstimada() + "  |  Sexo: " + m.getSexo() + "\n" +
                "  • Estado Clínico  : " + (m.isEsterilizado() ? "Esterilizado/a ✓" : "Pendiente") + " | " +
                (m.isVacunado() ? "Vacunado/a ✓" : "Pendiente") + " | " + (m.isDesparasitado() ? "Desparasitado/a ✓" : "Pendiente") + "\n\n" +
                "DATOS DEL TUTOR ADOPTANTE:\n" +
                "  • Nombre Completo : " + m.getAdoptanteNombre() + "\n" +
                "  • DNI / Documento : " + m.getAdoptanteDni() + "\n" +
                "  • Teléfono Móvil  : " + m.getAdoptanteTelefono() + "\n" +
                "  • Domicilio       : " + m.getAdoptanteDireccion() + "\n" +
                "  • Aporte al Fondo : S/. " + String.format("%.2f", m.getCuotaDonacion()) + " (Para rescate de otros animales)\n\n" +
                "COMPROMISOS DE TENENCIA RESPONSABLE (LEY 30407):\n" +
                "1. Proporcionar alimento de calidad, agua fresca, abrigo y atención médica preventiva periódica.\n" +
                "2. NO mantener al animal atado, confinado ni en azoteas desprotegidas.\n" +
                "3. En caso fortuito de no poder conservarlo, comunicarlo inmediatamente a Happy Pets sin abandonarlo.\n" +
                "4. Permitir visitas inopinadas de seguimiento durante los primeros 6 meses.\n\n" +
                "__________________________             __________________________\n" +
                "     Firma del Adoptante                    Dirección Médica Happy Pets\n" +
                "  DNI: " + m.getAdoptanteDni() + "                     Programa de Adopciones\n";

        JTextArea ta = new JTextArea(acta);
        ta.setFont(new Font("Consolas", Font.PLAIN, 11));
        ta.setEditable(false);
        JScrollPane sp = new JScrollPane(ta);
        sp.setPreferredSize(new Dimension(540, 340));

        JOptionPane.showMessageDialog(this, sp, "Acta Oficial de Adopción Responsable", JOptionPane.INFORMATION_MESSAGE);
    }

    private void generarDifusionWhatsapp() {
        int fila = tablaAdopciones.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una mascota para generar el mensaje de difusión.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String idMascota = (String) modeloAdopciones.getValueAt(fila, 0);
        Optional<MascotaAdopcion> opt = repo.getMascotasAdopcion().stream().filter(m -> m.getIdMascotaAdopcion().equalsIgnoreCase(idMascota)).findFirst();
        if (!opt.isPresent()) return;

        MascotaAdopcion m = opt.get();
        String mensaje = "🐾 ¡HOLA AMIGOS! BUSCO UN HOGAR DEFINITIVO LLENO DE AMOR 🏡✨\n" +
                "====================================================\n\n" +
                "🐶 Mi nombre es *" + m.getNombre() + "* y soy un consentido rescatado por el equipo de *Happy Pets*.\n\n" +
                "📌 *Datos sobre mí:*\n" +
                "• Especie / Raza: " + m.getEspecie() + " · " + m.getRaza() + "\n" +
                "• Edad: " + m.getEdadEstimada() + " | Sexo: " + m.getSexo() + "\n" +
                "• Personalidad: " + m.getTemperamento() + "\n" +
                "• Salud: " + (m.isEsterilizado() ? "Esterilizado/a ✓ " : "") + (m.isVacunado() ? "Vacunas al día ✓ " : "") + (m.isDesparasitado() ? "Desparasitado/a ✓" : "") + "\n\n" +
                "❤️ Si quieres darme una oportunidad y abrirme las puertas de tu corazón y tu familia, contáctanos hoy mismo.\n" +
                "📞 WhatsApp Adopciones Happy Pets: +51 984 552 110\n" +
                "📍 Av. Primavera 1230, Santiago de Surco\n" +
                "====================================================\n" +
                "¡Por favor comparte esta publicación y ayúdame a encontrar a mi familia soñada! 🙏🐶🐱";

        JTextArea ta = new JTextArea(mensaje);
        ta.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        ta.setEditable(false);
        ta.setLineWrap(true);
        ta.setWrapStyleWord(true);
        JScrollPane sp = new JScrollPane(ta);
        sp.setPreferredSize(new Dimension(460, 260));

        JOptionPane.showMessageDialog(this, sp, "Mensaje de Difusión para Redes / WhatsApp", JOptionPane.INFORMATION_MESSAGE);
    }

    // Helper para Combo
    private static class MascotaItem {
        final MascotaAdopcion mascota;
        final String etiqueta;

        MascotaItem(MascotaAdopcion m, String et) {
            this.mascota = m;
            this.etiqueta = et;
        }

        @Override
        public String toString() {
            return etiqueta;
        }
    }
}
