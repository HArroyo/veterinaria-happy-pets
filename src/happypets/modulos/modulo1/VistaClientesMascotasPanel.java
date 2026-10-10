package happypets.modulos.modulo1;

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
import java.awt.Window;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

import happypets.data.RepositorioVeterinaria;
import happypets.model.Cliente;
import happypets.model.ConsultaClinica;
import happypets.model.Mascota;
import happypets.ui.Iconos;
import happypets.ui.Ui;

/**
 * Vista 1 del Modulo 1: Directorio de Clientes y Ficha Medica Integrada (Opcion A).
 * Arquitectura Master-Detail Clinical Split:
 * - Columna Izquierda (~320px): Directorio interactivo en vivo con filtro en tiempo real.
 * - Columna Derecha (~68%): Ficha clinica ejecutiva integral:
 *     1. Ficha Ejecutiva del Propietario (datos, contacto, acciones rapidas).
 *     2. Selector de Pacientes (tarjetas conmutables para alternar mascotas).
 *     3. Sub-tarjeta Inferior Izquierda: Ficha Tecnica y Metricas del Paciente.
 *     4. Sub-tarjeta Inferior Derecha: Atenciones Medicas y Visitas Recientes (timeline).
 */
public class VistaClientesMascotasPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private static final Color COLOR_BORDE = Ui.BORDE_SUAVE;
    private static final Color COLOR_TURQUESA = Ui.TURQUESA;
    private static final Color COLOR_TITULO = Ui.TEXTO_TITULO;
    private static final Color COLOR_MUTED = Ui.TEXTO_MUTED;
    private static final Color COLOR_CARD_BG = Color.WHITE;
    private static final Color COLOR_SELECCION_BG = new Color(240, 253, 250); // cyan/teal tint

    private final RepositorioVeterinaria repo = RepositorioVeterinaria.getInstancia();
    private Cliente clienteActual;
    private Mascota mascotaSeleccionada;

    // Directorio izquierdo
    private JTextField txtBuscarDirectorio;
    private JLabel lblContadorDirectorio;
    private DefaultListModel<Cliente> modeloListaClientes;
    private JList<Cliente> listaClientes;
    private List<Cliente> listaClientesCompleta = new ArrayList<>();

    // Ficha Propietario (derecha arriba)
    private JLabel lblOwnerCodigo;
    private JLabel lblOwnerNombre;
    private JLabel lblOwnerDocumento;
    private JLabel lblOwnerTelefono;
    private JLabel lblOwnerCorreo;
    private JLabel lblOwnerDireccion;
    private JLabel lblOwnerCiudad;
    private JLabel lblOwnerNotas;
    private JButton btnEditarPropietario;
    private JButton btnEliminarPropietario;
    private JButton btnRegistrarMascota;

    // Selector de mascotas
    private JLabel lblContadorMascotas;
    private JPanel panelBotonesMascotas;
    private JScrollPane scrollSelectorMascotas;

    // Sub-tarjeta: Perfil clinico de la mascota
    private JLabel lblPetTitulo;
    private JLabel lblPetCodigoBadge;
    private JLabel lblPetEstadoBadge;
    private JLabel lblPetSexoBadge;
    private JLabel lblPetEspecieRaza;
    private JLabel lblPetEdad;
    private JLabel lblPetPeso;
    private JLabel lblPetEsterilizado;
    private JLabel lblPetMicrochip;
    private JLabel lblPetVacunasBadge;
    private JPanel panelAlertaAlergias;
    private JLabel lblPetAlergias;
    private JButton btnHistorialClinico;
    private JButton btnCertificados;
    private JButton btnEditarMascota;
    private JButton btnEliminarMascota;

    // Sub-tarjeta: Historial reciente de consultas
    private JTable tablaConsultasRecientes;
    private DefaultTableModel modeloConsultasRecientes;
    private JLabel lblConsultasVacias;
    private JScrollPane scrollTablaConsultas;
    private JButton btnNuevaConsulta;

    // Listener de navegacion
    public interface NavegacionListener {
        void irAHistorialClinico(Mascota mascota);
        void irAConstanciasCertificados(Mascota mascota);
    }

    private NavegacionListener navegacionListener;

    public void setNavegacionListener(NavegacionListener listener) {
        this.navegacionListener = listener;
    }

    public NavegacionListener getNavegacionListener() {
        return navegacionListener;
    }

    public VistaClientesMascotasPanel() {
        setLayout(new BorderLayout(0, 10));
        setOpaque(false);
        setBorder(new EmptyBorder(12, 16, 14, 16));

        // 1. Cabecera superior
        add(crearCabeceraVista(), BorderLayout.NORTH);

        // 2. Panel central dividido (Master-Detail)
        JPanel centralSplit = new JPanel(new BorderLayout(14, 0));
        centralSplit.setOpaque(false);

        // Columna Izquierda: Directorio de Propietarios (~330px)
        centralSplit.add(crearPanelDirectorioIzquierdo(), BorderLayout.WEST);

        // Columna Derecha: Ficha Clinica Integral (~68%)
        centralSplit.add(crearPanelDossierDerecho(), BorderLayout.CENTER);

        add(centralSplit, BorderLayout.CENTER);

        // Cargar datos iniciales
        recargarDirectorioClientes();
        if (!listaClientesCompleta.isEmpty()) {
            listaClientes.setSelectedIndex(0);
        }
    }

    // =========================================================================
    // 1. CABECERA DE LA VISTA
    // =========================================================================
    private JPanel crearCabeceraVista() {
        JPanel cab = new JPanel(new BorderLayout());
        cab.setOpaque(false);
        cab.setPreferredSize(new Dimension(0, 42));

        JPanel izq = new JPanel();
        izq.setOpaque(false);
        izq.setLayout(new BoxLayout(izq, BoxLayout.Y_AXIS));

        JLabel lblTit = new JLabel("Directorio de Clientes y Ficha Clinica");
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblTit.setForeground(COLOR_TITULO);

        JLabel lblSub = new JLabel("Modulo 1.1 · Gestion integral de propietarios, fichas de pacientes y atenciones veterinarias");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblSub.setForeground(COLOR_MUTED);

        izq.add(lblTit);
        izq.add(Box.createVerticalStrut(2));
        izq.add(lblSub);
        cab.add(izq, BorderLayout.CENTER);

        JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 2));
        der.setOpaque(false);

        JButton btnExportar = crearBotonAccion("Exportar Datos", false);
        btnExportar.setIcon(Iconos.crearIconoExportar(13, COLOR_TURQUESA));
        btnExportar.setIconTextGap(6);
        btnExportar.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Los datos de clientes y mascotas se exportaron exitosamente a formato CSV/Excel.",
                "Exportacion", JOptionPane.INFORMATION_MESSAGE));

        JButton btnNuevo = crearBotonAccion("Nuevo Propietario", true);
        btnNuevo.setIcon(Iconos.crearIconoMas(12, Color.WHITE));
        btnNuevo.setIconTextGap(6);
        btnNuevo.addActionListener(e -> nuevoCliente());

        der.add(btnExportar);
        der.add(btnNuevo);
        cab.add(der, BorderLayout.EAST);

        return cab;
    }

    // =========================================================================
    // 2. COLUMNA IZQUIERDA: DIRECTORIO EN VIVO
    // =========================================================================
    private JPanel crearPanelDirectorioIzquierdo() {
        JPanel panel = new JPanel(new BorderLayout(0, 8)) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(COLOR_CARD_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(COLOR_BORDE);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(320, 0));
        panel.setMinimumSize(new Dimension(280, 0));
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));

        // Cabecera del directorio
        JPanel top = new JPanel(new BorderLayout(0, 6));
        top.setOpaque(false);

        JPanel titFila = new JPanel(new BorderLayout());
        titFila.setOpaque(false);

        JLabel lblTit = new JLabel("Directorio de Clientes");
        lblTit.setIcon(Iconos.crearIconoClientes(15, COLOR_TURQUESA));
        lblTit.setIconTextGap(6);
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTit.setForeground(COLOR_TITULO);
        titFila.add(lblTit, BorderLayout.WEST);

        lblContadorDirectorio = new JLabel("0 clientes");
        lblContadorDirectorio.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblContadorDirectorio.setForeground(COLOR_TURQUESA);
        lblContadorDirectorio.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(204, 251, 241), 1),
                new EmptyBorder(2, 6, 2, 6)
        ));
        titFila.add(lblContadorDirectorio, BorderLayout.EAST);
        top.add(titFila, BorderLayout.NORTH);

        // Barra de busqueda reactiva
        JPanel searchBox = new JPanel(new BorderLayout(4, 0));
        searchBox.setOpaque(false);

        txtBuscarDirectorio = new JTextField();
        txtBuscarDirectorio.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        txtBuscarDirectorio.setPreferredSize(new Dimension(0, 28));
        txtBuscarDirectorio.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225), 1),
                new EmptyBorder(2, 8, 2, 8)
        ));
        txtBuscarDirectorio.setToolTipText("Filtrar por nombre, DNI, telefono o mascota");

        JLabel lblIconBuscar = new JLabel(Iconos.crearIconoBuscar(13, COLOR_MUTED));
        lblIconBuscar.setBorder(new EmptyBorder(0, 4, 0, 4));

        JButton btnLimpiar = new JButton("x") {
            private static final long serialVersionUID = 1L;
            @Override
            public Dimension getPreferredSize() { return new Dimension(22, 22); }
        };
        btnLimpiar.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnLimpiar.setForeground(COLOR_MUTED);
        btnLimpiar.setContentAreaFilled(false);
        btnLimpiar.setBorderPainted(false);
        btnLimpiar.setFocusPainted(false);
        btnLimpiar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnLimpiar.addActionListener(e -> txtBuscarDirectorio.setText(""));

        searchBox.add(lblIconBuscar, BorderLayout.WEST);
        searchBox.add(txtBuscarDirectorio, BorderLayout.CENTER);
        searchBox.add(btnLimpiar, BorderLayout.EAST);
        top.add(searchBox, BorderLayout.SOUTH);

        panel.add(top, BorderLayout.NORTH);

        // Lista reactiva de clientes
        modeloListaClientes = new DefaultListModel<>();
        listaClientes = new JList<>(modeloListaClientes);
        listaClientes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaClientes.setCellRenderer(new ClienteListCellRenderer());
        listaClientes.setOpaque(false);

        listaClientes.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Cliente sel = listaClientes.getSelectedValue();
                if (sel != null) {
                    cargarCliente(sel);
                }
            }
        });

        // Filtrado en tiempo real al escribir
        txtBuscarDirectorio.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { filtrarDirectorio(); }
            @Override
            public void removeUpdate(DocumentEvent e) { filtrarDirectorio(); }
            @Override
            public void changedUpdate(DocumentEvent e) { filtrarDirectorio(); }
        });

        JScrollPane scroll = new JScrollPane(listaClientes);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(241, 245, 249), 1));
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    // =========================================================================
    // 3. COLUMNA DERECHA: DOSSIER CLINICO COMPLETO
    // =========================================================================
    private JPanel crearPanelDossierDerecho() {
        JPanel dossier = new JPanel(new BorderLayout(0, 10));
        dossier.setOpaque(false);

        // Parte Superior del Dossier: Ficha del Propietario + Selector de Mascotas
        JPanel superiorDossier = new JPanel();
        superiorDossier.setOpaque(false);
        superiorDossier.setLayout(new BoxLayout(superiorDossier, BoxLayout.Y_AXIS));

        superiorDossier.add(crearTarjetaFichaPropietario());
        superiorDossier.add(Box.createVerticalStrut(10));
        superiorDossier.add(crearTarjetaSelectorMascotas());

        dossier.add(superiorDossier, BorderLayout.NORTH);

        // Parte Inferior del Dossier: Doble Sub-Tarjeta (Perfil Medico + Visitas Recientes)
        dossier.add(crearSubTarjetasInferiores(), BorderLayout.CENTER);

        return dossier;
    }

    // 3.1. Ficha Ejecutiva del Propietario
    private JPanel crearTarjetaFichaPropietario() {
        JPanel card = new JPanel(new BorderLayout(0, 8)) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(COLOR_CARD_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(COLOR_BORDE);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(10, 14, 10, 14));

        // Cabecera de la ficha
        JPanel cab = new JPanel(new BorderLayout());
        cab.setOpaque(false);

        JPanel titIzq = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        titIzq.setOpaque(false);

        JLabel lblTit = new JLabel("Ficha Ejecutiva del Propietario");
        lblTit.setIcon(Iconos.crearIconoUsuario(15, COLOR_TURQUESA));
        lblTit.setIconTextGap(6);
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTit.setForeground(COLOR_TITULO);
        titIzq.add(lblTit);

        lblOwnerCodigo = crearBadge("CLI-001", new Color(241, 245, 249), new Color(71, 85, 105));
        titIzq.add(lblOwnerCodigo);
        cab.add(titIzq, BorderLayout.WEST);

        // Botones de accion rapida del propietario
        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        acciones.setOpaque(false);

        btnEditarPropietario = crearBotonAccion("Editar Ficha", false);
        btnEditarPropietario.setIcon(Iconos.crearIconoEditar(12, new Color(71, 85, 105)));
        btnEditarPropietario.setIconTextGap(5);
        btnEditarPropietario.addActionListener(e -> abrirEditarCliente());

        btnEliminarPropietario = crearBotonAccion("Eliminar", false);
        btnEliminarPropietario.setIcon(Iconos.crearIconoEliminar(12, new Color(239, 68, 68)));
        btnEliminarPropietario.setForeground(new Color(239, 68, 68));
        btnEliminarPropietario.setIconTextGap(5);
        btnEliminarPropietario.addActionListener(e -> eliminarClienteActual());

        btnRegistrarMascota = crearBotonAccion("Registrar Mascota", true);
        btnRegistrarMascota.setIcon(Iconos.crearIconoMas(11, Color.WHITE));
        btnRegistrarMascota.setIconTextGap(5);
        btnRegistrarMascota.addActionListener(e -> agregarNuevaMascota());

        acciones.add(btnEditarPropietario);
        acciones.add(btnEliminarPropietario);
        acciones.add(btnRegistrarMascota);
        cab.add(acciones, BorderLayout.EAST);
        card.add(cab, BorderLayout.NORTH);

        // Grid de datos del cliente
        JPanel grid = new JPanel(new GridLayout(2, 3, 12, 6));
        grid.setOpaque(false);

        lblOwnerNombre = new JLabel("-");
        lblOwnerDocumento = new JLabel("-");
        lblOwnerTelefono = new JLabel("-");
        lblOwnerCorreo = new JLabel("-");
        lblOwnerDireccion = new JLabel("-");
        lblOwnerCiudad = new JLabel("-");

        grid.add(crearItemDato("Titular Responsable:", lblOwnerNombre, true));
        grid.add(crearItemDato("Doc. Identidad:", lblOwnerDocumento, false));
        grid.add(crearItemDato("Telefono(s):", lblOwnerTelefono, false));
        grid.add(crearItemDato("Correo Electronico:", lblOwnerCorreo, false));
        grid.add(crearItemDato("Direccion Domiciliaria:", lblOwnerDireccion, false));
        grid.add(crearItemDato("Distrito / Ciudad:", lblOwnerCiudad, false));
        card.add(grid, BorderLayout.CENTER);

        // Fila inferior: Notas de contacto
        JPanel pNotas = new JPanel(new BorderLayout(6, 0));
        pNotas.setOpaque(false);
        pNotas.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(241, 245, 249)),
                new EmptyBorder(6, 2, 2, 2)
        ));

        JLabel lblTitNotas = new JLabel("Notas / Observaciones:");
        lblTitNotas.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblTitNotas.setForeground(COLOR_MUTED);

        lblOwnerNotas = new JLabel("Ninguna observacion registrada.");
        lblOwnerNotas.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblOwnerNotas.setForeground(new Color(71, 85, 105));

        pNotas.add(lblTitNotas, BorderLayout.WEST);
        pNotas.add(lblOwnerNotas, BorderLayout.CENTER);
        card.add(pNotas, BorderLayout.SOUTH);

        return card;
    }

    // 3.2. Selector de Pacientes / Tarjetas de Mascotas
    private JPanel crearTarjetaSelectorMascotas() {
        JPanel card = new JPanel(new BorderLayout(0, 6)) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(COLOR_CARD_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(COLOR_BORDE);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(8, 14, 8, 14));

        // Cabecera del selector
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JLabel lblTit = new JLabel("Pacientes Asociados al Titular");
        lblTit.setIcon(Iconos.crearIconoHuella(14, COLOR_TURQUESA));
        lblTit.setIconTextGap(6);
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTit.setForeground(COLOR_TITULO);
        top.add(lblTit, BorderLayout.WEST);

        lblContadorMascotas = new JLabel("0 pacientes");
        lblContadorMascotas.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblContadorMascotas.setForeground(COLOR_MUTED);
        top.add(lblContadorMascotas, BorderLayout.EAST);
        card.add(top, BorderLayout.NORTH);

        // Contenedor horizontal de botones de mascotas
        panelBotonesMascotas = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        panelBotonesMascotas.setOpaque(false);

        scrollSelectorMascotas = new JScrollPane(panelBotonesMascotas);
        scrollSelectorMascotas.setBorder(null);
        scrollSelectorMascotas.setOpaque(false);
        scrollSelectorMascotas.getViewport().setOpaque(false);
        scrollSelectorMascotas.setPreferredSize(new Dimension(0, 48));
        scrollSelectorMascotas.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        card.add(scrollSelectorMascotas, BorderLayout.CENTER);

        return card;
    }

    // 3.3. Doble Sub-Tarjeta Inferior (Perfil Medico y Visitas Recientes)
    private JPanel crearSubTarjetasInferiores() {
        JPanel split = new JPanel(new GridLayout(1, 2, 12, 0));
        split.setOpaque(false);

        // Sub-Tarjeta 1: Ficha Tecnica del Paciente
        split.add(crearSubTarjetaPerfilMascota());

        // Sub-Tarjeta 2: Atenciones Medicas Recientes
        split.add(crearSubTarjetaVisitasRecientes());

        return split;
    }

    // Sub-Tarjeta 1: Perfil Tecnico de la Mascota
    private JPanel crearSubTarjetaPerfilMascota() {
        JPanel card = new JPanel(new BorderLayout(0, 8)) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(COLOR_CARD_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(COLOR_BORDE);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(10, 14, 10, 14));

        // Cabecera de la sub-tarjeta
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JPanel titIzq = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        titIzq.setOpaque(false);

        lblPetTitulo = new JLabel("Perfil Medico del Paciente");
        lblPetTitulo.setIcon(Iconos.crearIconoHuella(15, COLOR_TURQUESA));
        lblPetTitulo.setIconTextGap(6);
        lblPetTitulo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblPetTitulo.setForeground(COLOR_TITULO);
        titIzq.add(lblPetTitulo);

        lblPetCodigoBadge = crearBadge("VET-0000", new Color(241, 245, 249), new Color(71, 85, 105));
        titIzq.add(lblPetCodigoBadge);
        top.add(titIzq, BorderLayout.WEST);

        JPanel badgesDer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
        badgesDer.setOpaque(false);
        lblPetSexoBadge = crearBadge("Macho", new Color(238, 242, 255), new Color(79, 70, 229));
        lblPetEstadoBadge = crearBadge("Activo", new Color(240, 253, 244), new Color(22, 101, 52));
        badgesDer.add(lblPetSexoBadge);
        badgesDer.add(lblPetEstadoBadge);
        top.add(badgesDer, BorderLayout.EAST);
        card.add(top, BorderLayout.NORTH);

        // Centro: Metricas clinicas y datos del paciente
        JPanel centro = new JPanel();
        centro.setOpaque(false);
        centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));

        JPanel gridSpecs = new JPanel(new GridLayout(3, 2, 10, 6));
        gridSpecs.setOpaque(false);

        lblPetEspecieRaza = new JLabel("-");
        lblPetEdad = new JLabel("-");
        lblPetPeso = new JLabel("-");
        lblPetEsterilizado = new JLabel("-");
        lblPetMicrochip = new JLabel("-");
        lblPetVacunasBadge = new JLabel("-");

        gridSpecs.add(crearItemDato("Especie y Raza:", lblPetEspecieRaza, true));
        gridSpecs.add(crearItemDato("Edad Cronologica:", lblPetEdad, false));
        gridSpecs.add(crearItemDato("Peso Actual:", lblPetPeso, true));
        gridSpecs.add(crearItemDato("Esterilizacion:", lblPetEsterilizado, false));
        gridSpecs.add(crearItemDato("Microchip:", lblPetMicrochip, false));
        gridSpecs.add(crearItemDato("Plan Vacunal:", lblPetVacunasBadge, false));
        centro.add(gridSpecs);
        centro.add(Box.createVerticalStrut(8));

        // Alerta de alergias y cuidados
        panelAlertaAlergias = new JPanel(new BorderLayout(6, 0)) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        panelAlertaAlergias.setOpaque(false);
        panelAlertaAlergias.setBackground(new Color(254, 242, 242));
        panelAlertaAlergias.setBorder(new EmptyBorder(6, 10, 6, 10));

        panelAlertaAlergias.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        panelAlertaAlergias.setPreferredSize(new Dimension(0, 32));

        JLabel lblAlertaIcon = new JLabel("!");
        lblAlertaIcon.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblAlertaIcon.setForeground(new Color(185, 28, 28));
        panelAlertaAlergias.add(lblAlertaIcon, BorderLayout.WEST);

        lblPetAlergias = new JLabel("Alergias / Alertas: Ninguna conocida");
        lblPetAlergias.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblPetAlergias.setForeground(new Color(153, 27, 27));
        panelAlertaAlergias.add(lblPetAlergias, BorderLayout.CENTER);

        centro.add(panelAlertaAlergias);
        centro.add(Box.createVerticalGlue());
        card.add(centro, BorderLayout.CENTER);

        // Barra inferior: Acciones clinicas directas
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
        footer.setOpaque(false);

        btnHistorialClinico = crearBotonAccion("Historial Clinico", true);
        btnHistorialClinico.setIcon(Iconos.crearIconoHistorial(13, Color.WHITE));
        btnHistorialClinico.setIconTextGap(5);
        btnHistorialClinico.addActionListener(e -> abrirHistorialDeMascota());

        btnCertificados = crearBotonAccion("Certificados", false);
        btnCertificados.setIcon(Iconos.crearIconoCertificado(13, COLOR_TURQUESA));
        btnCertificados.setIconTextGap(5);
        btnCertificados.addActionListener(e -> abrirCertificadosDeMascota());

        btnEditarMascota = crearBotonAccion("Editar", false);
        btnEditarMascota.setIcon(Iconos.crearIconoEditar(12, new Color(71, 85, 105)));
        btnEditarMascota.setIconTextGap(4);
        btnEditarMascota.addActionListener(e -> editarMascotaSeleccionada());

        btnEliminarMascota = crearBotonAccion("Eliminar", false);
        btnEliminarMascota.setIcon(Iconos.crearIconoEliminar(12, new Color(239, 68, 68)));
        btnEliminarMascota.setForeground(new Color(239, 68, 68));
        btnEliminarMascota.setIconTextGap(4);
        btnEliminarMascota.addActionListener(e -> eliminarMascotaSeleccionada());

        footer.add(btnHistorialClinico);
        footer.add(btnCertificados);
        footer.add(btnEditarMascota);
        footer.add(btnEliminarMascota);
        card.add(footer, BorderLayout.SOUTH);

        return card;
    }

    // Sub-Tarjeta 2: Atenciones Medicas Recientes (Timeline)
    private JPanel crearSubTarjetaVisitasRecientes() {
        JPanel card = new JPanel(new BorderLayout(0, 8)) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(COLOR_CARD_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(COLOR_BORDE);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(10, 14, 10, 14));

        // Cabecera
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JLabel lblTit = new JLabel("Atenciones Medicas Recientes");
        lblTit.setIcon(Iconos.crearIconoEstetoscopio(15, COLOR_TURQUESA));
        lblTit.setIconTextGap(6);
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTit.setForeground(COLOR_TITULO);
        top.add(lblTit, BorderLayout.WEST);

        btnNuevaConsulta = crearBotonAccion("Nueva Consulta", false);
        btnNuevaConsulta.setIcon(Iconos.crearIconoMas(11, COLOR_TURQUESA));
        btnNuevaConsulta.setIconTextGap(4);
        btnNuevaConsulta.addActionListener(e -> abrirNuevaConsulta());
        top.add(btnNuevaConsulta, BorderLayout.EAST);
        card.add(top, BorderLayout.NORTH);

        // Tabla de consultas / visitas recientes
        String[] columnas = {"Fecha", "Motivo", "Diagnostico / Tratamiento", "Veterinario", "Estado"};
        modeloConsultasRecientes = new DefaultTableModel(columnas, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        tablaConsultasRecientes = new JTable(modeloConsultasRecientes);
        Ui.formatearTabla(tablaConsultasRecientes, new int[]{0, 4}, new int[]{});
        tablaConsultasRecientes.setRowHeight(28);

        tablaConsultasRecientes.getColumnModel().getColumn(0).setPreferredWidth(85);
        tablaConsultasRecientes.getColumnModel().getColumn(0).setMaxWidth(95);
        tablaConsultasRecientes.getColumnModel().getColumn(1).setPreferredWidth(110);
        tablaConsultasRecientes.getColumnModel().getColumn(2).setPreferredWidth(160);
        tablaConsultasRecientes.getColumnModel().getColumn(3).setPreferredWidth(105);
        tablaConsultasRecientes.getColumnModel().getColumn(4).setPreferredWidth(75);
        tablaConsultasRecientes.getColumnModel().getColumn(4).setMaxWidth(85);

        tablaConsultasRecientes.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    abrirDetalleConsultaSeleccionada();
                }
            }
        });

        scrollTablaConsultas = new JScrollPane(tablaConsultasRecientes);
        scrollTablaConsultas.setBorder(BorderFactory.createLineBorder(COLOR_BORDE, 1));
        scrollTablaConsultas.setOpaque(false);
        scrollTablaConsultas.getViewport().setOpaque(false);

        lblConsultasVacias = new JLabel("No se registran visitas previas para esta mascota.", SwingConstants.CENTER);
        lblConsultasVacias.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblConsultasVacias.setForeground(COLOR_MUTED);

        card.add(scrollTablaConsultas, BorderLayout.CENTER);

        return card;
    }

    // =========================================================================
    // 4. CONTROL DE DATOS Y RENDERIZADO
    // =========================================================================

    public void recargarDirectorioClientes() {
        listaClientesCompleta = repo.getClientes();
        filtrarDirectorio();
    }

    private void filtrarDirectorio() {
        String q = (txtBuscarDirectorio != null) ? txtBuscarDirectorio.getText().trim().toLowerCase() : "";
        modeloListaClientes.clear();

        for (Cliente c : listaClientesCompleta) {
            boolean coincide = q.isEmpty();
            if (!coincide) {
                if (c.getNombreCompleto().toLowerCase().contains(q) ||
                        (c.getNumeroDocumento() != null && c.getNumeroDocumento().toLowerCase().contains(q)) ||
                        (c.getTelefonoPrincipal() != null && c.getTelefonoPrincipal().toLowerCase().contains(q)) ||
                        (c.getDistritoCiudad() != null && c.getDistritoCiudad().toLowerCase().contains(q))) {
                    coincide = true;
                } else {
                    for (Mascota m : c.getMascotas()) {
                        if (m.getNombre().toLowerCase().contains(q) || m.getCodigo().toLowerCase().contains(q)) {
                            coincide = true;
                            break;
                        }
                    }
                }
            }
            if (coincide) {
                modeloListaClientes.addElement(c);
            }
        }

        if (lblContadorDirectorio != null) {
            lblContadorDirectorio.setText(modeloListaClientes.size() + " clientes");
        }

        if (!modeloListaClientes.isEmpty() && listaClientes.getSelectedValue() == null) {
            listaClientes.setSelectedIndex(0);
        }
    }

    public void cargarCliente(Cliente c) {
        this.clienteActual = c;
        if (c == null) {
            limpiarFormulario();
            return;
        }

        // Ficha Ejecutiva
        lblOwnerCodigo.setText(c.getCodigo());
        lblOwnerNombre.setText(c.getNombreCompleto());
        lblOwnerDocumento.setText(c.getTipoDocumento() + " " + c.getNumeroDocumento());
        String tel = c.getTelefonoPrincipal();
        if (c.getTelefonoSecundario() != null && !c.getTelefonoSecundario().trim().isEmpty()) {
            tel += " / " + c.getTelefonoSecundario();
        }
        lblOwnerTelefono.setText(tel);
        lblOwnerCorreo.setText((c.getCorreo() != null && !c.getCorreo().trim().isEmpty()) ? c.getCorreo() : "No registrado");
        lblOwnerDireccion.setText((c.getDireccion() != null && !c.getDireccion().trim().isEmpty()) ? c.getDireccion() : "No registrada");
        lblOwnerCiudad.setText((c.getDistritoCiudad() != null && !c.getDistritoCiudad().trim().isEmpty()) ? c.getDistritoCiudad() : "Lima");
        lblOwnerNotas.setText((c.getNotasContacto() != null && !c.getNotasContacto().trim().isEmpty()) ? c.getNotasContacto() : "Sin notas especiales.");

        // Refrescar selector de mascotas
        recargarSelectorMascotas();
    }

    public void cargarClienteEnFormulario(Cliente c) {
        cargarCliente(c);
        // Sincronizar seleccion en lista si existe
        if (c != null && listaClientes != null) {
            for (int i = 0; i < modeloListaClientes.size(); i++) {
                if (modeloListaClientes.get(i).getCodigo().equalsIgnoreCase(c.getCodigo())) {
                    listaClientes.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    private void recargarSelectorMascotas() {
        panelBotonesMascotas.removeAll();

        if (clienteActual == null || clienteActual.getMascotas().isEmpty()) {
            lblContadorMascotas.setText("0 pacientes");
            JLabel empty = new JLabel("Este propietario no tiene pacientes vinculados.", SwingConstants.LEFT);
            empty.setFont(new Font("Segoe UI", Font.ITALIC, 11));
            empty.setForeground(COLOR_MUTED);
            panelBotonesMascotas.add(empty);

            JButton btnAdd = crearBotonAccion("+ Registrar Primera Mascota", true);
            btnAdd.addActionListener(e -> agregarNuevaMascota());
            panelBotonesMascotas.add(btnAdd);

            cargarMascotaEnDossier(null);
        } else {
            List<Mascota> mascotas = clienteActual.getMascotas();
            lblContadorMascotas.setText(mascotas.size() + " paciente" + (mascotas.size() > 1 ? "s" : ""));

            Mascota petToSelect = null;
            if (mascotaSeleccionada != null) {
                for (Mascota m : mascotas) {
                    if (m.getCodigo().equalsIgnoreCase(mascotaSeleccionada.getCodigo())) {
                        petToSelect = m;
                        break;
                    }
                }
            }
            if (petToSelect == null) {
                petToSelect = mascotas.get(0);
            }

            for (Mascota m : mascotas) {
                boolean activa = (m == petToSelect);
                panelBotonesMascotas.add(crearBotonPetTab(m, activa));
            }

            cargarMascotaEnDossier(petToSelect);
        }

        panelBotonesMascotas.revalidate();
        panelBotonesMascotas.repaint();
    }

    private JButton crearBotonPetTab(Mascota m, boolean activa) {
        String texto = m.getNombre() + " (" + m.getEspecie() + " · " + m.getRaza() + ")";
        JButton btn = new JButton(texto) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (activa) {
                    g2.setColor(COLOR_SELECCION_BG);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                    g2.setColor(COLOR_TURQUESA);
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                } else {
                    g2.setColor(getModel().isRollover() ? new Color(248, 250, 252) : Color.WHITE);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                    g2.setColor(COLOR_BORDE);
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                }
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        btn.setIcon(Iconos.crearIconoHuella(13, activa ? COLOR_TURQUESA : COLOR_MUTED));
        btn.setIconTextGap(6);
        btn.setFont(new Font("Segoe UI", activa ? Font.BOLD : Font.PLAIN, 11));
        btn.setForeground(activa ? COLOR_TURQUESA : new Color(51, 65, 85));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(5, 12, 5, 12));

        btn.addActionListener(e -> {
            cargarMascotaEnDossier(m);
            recargarSelectorMascotas();
        });

        return btn;
    }

    private void cargarMascotaEnDossier(Mascota m) {
        this.mascotaSeleccionada = m;

        if (m == null) {
            lblPetTitulo.setText("Perfil Medico del Paciente");
            lblPetCodigoBadge.setText("VET-0000");
            lblPetSexoBadge.setText("-");
            lblPetEstadoBadge.setText("-");
            lblPetEspecieRaza.setText("-");
            lblPetEdad.setText("-");
            lblPetPeso.setText("-");
            lblPetEsterilizado.setText("-");
            lblPetMicrochip.setText("-");
            lblPetVacunasBadge.setText("-");
            lblPetAlergias.setText("Alergias / Alertas: Ninguna conocida");
            panelAlertaAlergias.setBackground(new Color(241, 245, 249));

            modeloConsultasRecientes.setRowCount(0);
            return;
        }

        // Metricas superiores
        lblPetTitulo.setText(m.getNombre() + " (" + m.getEspecie() + ")");
        lblPetCodigoBadge.setText(m.getCodigo());
        lblPetSexoBadge.setText(m.getSexo() != null ? m.getSexo() : "Macho");
        lblPetEstadoBadge.setText(m.isActivo() ? "Activo" : "Inactivo");

        // Ficha tecnica
        lblPetEspecieRaza.setText(m.getEspecieRaza());
        lblPetEdad.setText(m.getEdadTexto());
        lblPetPeso.setText(m.getPesoActualKg() + " kg");
        lblPetEsterilizado.setText(m.isEsterilizado() ? "Si (Quirurgico)" : "No esterilizado");
        lblPetMicrochip.setText((m.getMicrochip() != null && !m.getMicrochip().trim().isEmpty()) ? m.getMicrochip() : "No registrado");

        String vac = m.getPlanVacunal() != null ? m.getPlanVacunal() : "Al dia";
        lblPetVacunasBadge.setText(vac);
        if (vac.toLowerCase().contains("dia")) {
            lblPetVacunasBadge.setForeground(new Color(22, 101, 52));
        } else {
            lblPetVacunasBadge.setForeground(new Color(180, 83, 9));
        }

        // Alergias
        String alergias = m.getAlergias();
        if (alergias != null && !alergias.trim().isEmpty() && !alergias.equalsIgnoreCase("ninguna") && !alergias.equalsIgnoreCase("ninguna conocida")) {
            lblPetAlergias.setText("Alergias / Alertas: " + alergias);
            lblPetAlergias.setForeground(new Color(185, 28, 28));
            panelAlertaAlergias.setBackground(new Color(254, 242, 242));
        } else {
            lblPetAlergias.setText("Alergias / Alertas: Ninguna conocida (Apto sin restricciones)");
            lblPetAlergias.setForeground(new Color(22, 101, 52));
            panelAlertaAlergias.setBackground(new Color(240, 253, 244));
        }

        // Consultas recientes del paciente
        recargarConsultasRecientes(m.getCodigo());
    }

    private void recargarConsultasRecientes(String codigoMascota) {
        modeloConsultasRecientes.setRowCount(0);
        List<ConsultaClinica> consultas = repo.getConsultasPorMascota(codigoMascota);

        for (ConsultaClinica cc : consultas) {
            modeloConsultasRecientes.addRow(new Object[]{
                    cc.getFechaFormateada(),
                    cc.getMotivo(),
                    cc.getDiagnostico() + " · " + cc.getTratamiento(),
                    cc.getVeterinario(),
                    cc.getEstado()
            });
        }
    }

    // =========================================================================
    // 5. ACCIONES DE DIALOGOS Y NAVEGACION
    // =========================================================================

    public void nuevoCliente() {
        Window parent = SwingUtilities.getWindowAncestor(this);
        FormularioClienteDialog dlg = new FormularioClienteDialog(parent, null, nuevo -> {
            repo.guardarCliente(nuevo);
            recargarDirectorioClientes();
            cargarClienteEnFormulario(nuevo);
            JOptionPane.showMessageDialog(this,
                    "Cliente " + nuevo.getNombreCompleto() + " registrado exitosamente.",
                    "Registro Exitoso", JOptionPane.INFORMATION_MESSAGE);
        });
        dlg.setVisible(true);
    }

    private void abrirEditarCliente() {
        if (clienteActual == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un cliente para modificar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Window parent = SwingUtilities.getWindowAncestor(this);
        FormularioClienteDialog dlg = new FormularioClienteDialog(parent, clienteActual, editado -> {
            repo.guardarCliente(editado);
            recargarDirectorioClientes();
            cargarCliente(editado);
            JOptionPane.showMessageDialog(this,
                    "Ficha del cliente actualizada exitosamente.",
                    "Actualizacion Exitosa", JOptionPane.INFORMATION_MESSAGE);
        });
        dlg.setVisible(true);
    }

    public void guardarCambiosCliente() {
        abrirEditarCliente();
    }

    private void eliminarClienteActual() {
        if (clienteActual == null) {
            JOptionPane.showMessageDialog(this, "No hay ningun cliente seleccionado para eliminar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int r = JOptionPane.showConfirmDialog(this,
                "¿Esta seguro de eliminar al propietario " + clienteActual.getNombreCompleto() + " y todas sus mascotas asociadas?",
                "Confirmar Eliminacion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (r == JOptionPane.YES_OPTION) {
            repo.eliminarCliente(clienteActual.getCodigo());
            recargarDirectorioClientes();
            if (!modeloListaClientes.isEmpty()) {
                listaClientes.setSelectedIndex(0);
            } else {
                limpiarFormulario();
            }
            JOptionPane.showMessageDialog(this, "Cliente eliminado del sistema.", "Eliminado", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void agregarNuevaMascota() {
        if (clienteActual == null) {
            JOptionPane.showMessageDialog(this, "Primero seleccione o registre un propietario para asociarle una mascota.", "Propietario Requerido", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Window parent = SwingUtilities.getWindowAncestor(this);
        JFrame parentFrame = (parent instanceof JFrame) ? (JFrame) parent : null;
        FormularioMascotaDialog dlg = new FormularioMascotaDialog(parentFrame, null, nueva -> {
            clienteActual.agregarMascota(nueva);
            recargarSelectorMascotas();
            cargarMascotaEnDossier(nueva);
            recargarDirectorioClientes();
        });
        dlg.setVisible(true);
    }

    private void editarMascotaSeleccionada() {
        if (mascotaSeleccionada == null || clienteActual == null) {
            JOptionPane.showMessageDialog(this, "Seleccione una mascota para editar sus datos.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Window parent = SwingUtilities.getWindowAncestor(this);
        JFrame parentFrame = (parent instanceof JFrame) ? (JFrame) parent : null;
        FormularioMascotaDialog dlg = new FormularioMascotaDialog(parentFrame, mascotaSeleccionada, editada -> {
            recargarSelectorMascotas();
            cargarMascotaEnDossier(editada);
            recargarDirectorioClientes();
        });
        dlg.setVisible(true);
    }

    private void eliminarMascotaSeleccionada() {
        if (mascotaSeleccionada == null || clienteActual == null) {
            JOptionPane.showMessageDialog(this, "Seleccione una mascota para eliminar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int r = JOptionPane.showConfirmDialog(this,
                "¿Desea eliminar la mascota " + mascotaSeleccionada.getNombre() + " de la ficha de " + clienteActual.getNombreCompleto() + "?",
                "Confirmar Eliminacion", JOptionPane.YES_NO_OPTION);
        if (r == JOptionPane.YES_OPTION) {
            clienteActual.eliminarMascota(mascotaSeleccionada.getCodigo());
            mascotaSeleccionada = null;
            recargarSelectorMascotas();
            recargarDirectorioClientes();
        }
    }

    private void abrirHistorialDeMascota() {
        if (mascotaSeleccionada == null) {
            JOptionPane.showMessageDialog(this, "Seleccione una mascota para consultar su historial.", "Seleccion Requerida", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        if (navegacionListener != null) {
            navegacionListener.irAHistorialClinico(mascotaSeleccionada);
        } else {
            HistorialClinicoFrame hf = new HistorialClinicoFrame(mascotaSeleccionada);
            hf.setVisible(true);
        }
    }

    private void abrirCertificadosDeMascota() {
        if (mascotaSeleccionada == null) {
            JOptionPane.showMessageDialog(this, "Seleccione una mascota para emitir o consultar sus certificados.", "Seleccion Requerida", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        if (navegacionListener != null) {
            navegacionListener.irAConstanciasCertificados(mascotaSeleccionada);
        } else {
            ConstanciasCertificadosFrame cf = new ConstanciasCertificadosFrame(mascotaSeleccionada);
            cf.setVisible(true);
        }
    }

    private void abrirNuevaConsulta() {
        if (mascotaSeleccionada == null) {
            JOptionPane.showMessageDialog(this, "Seleccione una mascota para registrar una atencion medica.", "Seleccion Requerida", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        // Navegar directamente a historial clinico para registrar consulta completa
        if (navegacionListener != null) {
            navegacionListener.irAHistorialClinico(mascotaSeleccionada);
        } else {
            HistorialClinicoFrame hf = new HistorialClinicoFrame(mascotaSeleccionada);
            hf.setVisible(true);
        }
    }

    private void abrirDetalleConsultaSeleccionada() {
        int r = tablaConsultasRecientes.getSelectedRow();
        if (r >= 0 && mascotaSeleccionada != null) {
            List<ConsultaClinica> consultas = repo.getConsultasPorMascota(mascotaSeleccionada.getCodigo());
            if (r < consultas.size()) {
                ConsultaClinica cc = consultas.get(r);
                Window win = SwingUtilities.getWindowAncestor(this);
                JFrame frame = (win instanceof JFrame) ? (JFrame) win : null;
                DetalleConsultaDialog dlg = new DetalleConsultaDialog(frame, cc, mascotaSeleccionada);
                dlg.setVisible(true);
            }
        }
    }

    public void limpiarFormulario() {
        clienteActual = null;
        mascotaSeleccionada = null;
        if (lblOwnerCodigo != null) lblOwnerCodigo.setText("CLI-000");
        if (lblOwnerNombre != null) lblOwnerNombre.setText("-");
        if (lblOwnerDocumento != null) lblOwnerDocumento.setText("-");
        if (lblOwnerTelefono != null) lblOwnerTelefono.setText("-");
        if (lblOwnerCorreo != null) lblOwnerCorreo.setText("-");
        if (lblOwnerDireccion != null) lblOwnerDireccion.setText("-");
        if (lblOwnerCiudad != null) lblOwnerCiudad.setText("-");
        if (lblOwnerNotas != null) lblOwnerNotas.setText("Ninguna observacion registrada.");
        if (panelBotonesMascotas != null) panelBotonesMascotas.removeAll();
        cargarMascotaEnDossier(null);
    }

    public Cliente getClienteActual() {
        return clienteActual;
    }

    public Mascota getMascotaSeleccionada() {
        return mascotaSeleccionada;
    }

    // =========================================================================
    // 6. COMPONENTES AUXILIARES DE ESTILO
    // =========================================================================

    private JPanel crearItemDato(String etiqueta, JLabel valLabel, boolean destacado) {
        JPanel p = new JPanel(new BorderLayout(0, 2));
        p.setOpaque(false);

        JLabel lbl = new JLabel(etiqueta);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lbl.setForeground(COLOR_MUTED);

        valLabel.setFont(new Font("Segoe UI", destacado ? Font.BOLD : Font.PLAIN, 11));
        valLabel.setForeground(COLOR_TITULO);

        p.add(lbl, BorderLayout.NORTH);
        p.add(valLabel, BorderLayout.CENTER);
        return p;
    }

    private JLabel crearBadge(String texto, Color bg, Color fg) {
        JLabel badge = new JLabel(texto) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        badge.setFont(new Font("Segoe UI", Font.BOLD, 10));
        badge.setForeground(fg);
        badge.setBackground(bg);
        badge.setOpaque(false);
        badge.setBorder(new EmptyBorder(2, 6, 2, 6));
        return badge;
    }

    private JButton crearBotonAccion(String texto, boolean primario) {
        JButton btn = new JButton(texto) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                boolean esPrimario = Boolean.TRUE.equals(getClientProperty("primario"));
                if (esPrimario) {
                    g2.setColor(getModel().isRollover() ? Ui.TURQUESA_OSCURO : COLOR_TURQUESA);
                } else {
                    g2.setColor(getModel().isRollover() ? new Color(241, 245, 249) : Color.WHITE);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                if (!esPrimario) {
                    g2.setColor(COLOR_BORDE);
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 6, 6);
                }
                super.paintComponent(g2);
                g2.dispose();
            }

            @Override
            public Dimension getPreferredSize() {
                Dimension d = super.getPreferredSize();
                return new Dimension(d.width + 8, 26);
            }
        };
        btn.putClientProperty("primario", primario);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 10));
        btn.setForeground(primario ? Color.WHITE : new Color(51, 65, 85));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(3, 8, 3, 8));
        return btn;
    }

    // =========================================================================
    // 7. RENDERIZADOR PERSONALIZADO DEL DIRECTORIO DE CLIENTES
    // =========================================================================
    private static class ClienteListCellRenderer extends JPanel implements ListCellRenderer<Cliente> {
        private static final long serialVersionUID = 1L;

        private final JLabel lblAvatar;
        private final JLabel lblNombre;
        private final JLabel lblDocCiudad;
        private final JLabel lblTelefono;
        private final JLabel lblBadgeMascotas;
        private boolean isSelected = false;

        public ClienteListCellRenderer() {
            setLayout(new BorderLayout(8, 0));
            setOpaque(true);
            setBorder(new EmptyBorder(7, 8, 7, 8));

            // Avatar circular con iniciales
            lblAvatar = new JLabel("", SwingConstants.CENTER) {
                private static final long serialVersionUID = 1L;
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(isSelected ? COLOR_TURQUESA : new Color(203, 213, 225));
                    g2.fillOval(0, 0, getWidth(), getHeight());
                    super.paintComponent(g2);
                    g2.dispose();
                }
            };
            lblAvatar.setPreferredSize(new Dimension(34, 34));
            lblAvatar.setFont(new Font("Segoe UI", Font.BOLD, 11));
            lblAvatar.setForeground(Color.WHITE);
            add(lblAvatar, BorderLayout.WEST);

            // Centro: Nombre y datos
            JPanel mid = new JPanel();
            mid.setOpaque(false);
            mid.setLayout(new BoxLayout(mid, BoxLayout.Y_AXIS));

            lblNombre = new JLabel();
            lblNombre.setFont(new Font("Segoe UI", Font.BOLD, 11));
            lblNombre.setForeground(COLOR_TITULO);

            lblDocCiudad = new JLabel();
            lblDocCiudad.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            lblDocCiudad.setForeground(COLOR_MUTED);

            lblTelefono = new JLabel();
            lblTelefono.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            lblTelefono.setForeground(new Color(148, 163, 184));

            mid.add(lblNombre);
            mid.add(lblDocCiudad);
            mid.add(lblTelefono);
            add(mid, BorderLayout.CENTER);

            // Derecha: Badge de mascotas
            lblBadgeMascotas = new JLabel() {
                private static final long serialVersionUID = 1L;
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(getBackground());
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                    super.paintComponent(g2);
                    g2.dispose();
                }
            };
            lblBadgeMascotas.setFont(new Font("Segoe UI", Font.BOLD, 9));
            lblBadgeMascotas.setForeground(new Color(15, 118, 110));
            lblBadgeMascotas.setBackground(new Color(240, 253, 250));
            lblBadgeMascotas.setBorder(new EmptyBorder(2, 6, 2, 6));

            JPanel pDer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 8));
            pDer.setOpaque(false);
            pDer.add(lblBadgeMascotas);
            add(pDer, BorderLayout.EAST);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends Cliente> list, Cliente cliente,
                                                      int index, boolean isSelected, boolean cellHasFocus) {
            this.isSelected = isSelected;

            if (cliente != null) {
                // Generar iniciales
                String n = cliente.getNombres() != null && !cliente.getNombres().isEmpty() ? cliente.getNombres().substring(0, 1) : "";
                String a = cliente.getApellidos() != null && !cliente.getApellidos().isEmpty() ? cliente.getApellidos().substring(0, 1) : "";
                lblAvatar.setText((n + a).toUpperCase());

                lblNombre.setText(cliente.getNombreCompleto());
                lblDocCiudad.setText(cliente.getTipoDocumento() + " " + cliente.getNumeroDocumento() + " · " + cliente.getDistritoCiudad());
                lblTelefono.setText("Tel: " + cliente.getTelefonoPrincipal());

                int totalMascotas = cliente.getMascotas().size();
                lblBadgeMascotas.setText(totalMascotas + " m.");
            }

            if (isSelected) {
                setBackground(COLOR_SELECCION_BG);
                setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 3, 0, 0, COLOR_TURQUESA),
                        new EmptyBorder(7, 5, 7, 8)
                ));
            } else {
                setBackground(Color.WHITE);
                setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(241, 245, 249)),
                        new EmptyBorder(7, 8, 7, 8)
                ));
            }

            return this;
        }
    }
}
