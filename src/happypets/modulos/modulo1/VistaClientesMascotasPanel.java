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
import java.util.List;
import java.util.Optional;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import happypets.data.RepositorioVeterinaria;
import happypets.model.Cliente;
import happypets.model.Mascota;
import happypets.ui.Iconos;
import happypets.ui.Ui;

/**
 * Vista 1 del Módulo 1: Mantenimiento de Clientes y Mascotas vinculadas.
 * Diseñada respetando la estética web moderna ERP:
 * - Cabecera con título, subtítulo y botones de acción rápida.
 * - Barra de herramientas de clientes (Listar, Nuevo, Modificar, Eliminar y Búsqueda).
 * - Ficha de cliente con campos reordenados en tarjeta limpia (Ficha Propietario).
 * - Sección de mascotas vinculadas con tabla interactiva y resumen clínico inferior.
 */
public class VistaClientesMascotasPanel extends happypets.ui.AssetsModulo {
    private static final long serialVersionUID = 1L;

    private static final Color COLOR_BORDE = new Color(226, 232, 240);
    private static final Color COLOR_AZUL_PRIMARIO = new Color(2, 132, 199);
    private static final Color COLOR_TEXTO_TITULO = new Color(30, 41, 59);
    private static final Color COLOR_TEXTO_MUTED = new Color(100, 116, 139);

    private final RepositorioVeterinaria repo = RepositorioVeterinaria.getInstancia();
    private Cliente clienteActual;
    private Mascota mascotaSeleccionada;

    // Campos del formulario del cliente
    private JTextField txtNombres;
    private JTextField txtDocumento;
    private JTextField txtTelefono;
    private JTextField txtTelefonoSecundario;
    private JTextField txtCorreo;
    private JTextField txtDireccion;
    private JTextField txtCiudad;
    private JTextArea txtNotas;
    private JTextField txtBusqueda;

    // Tabla de mascotas vinculadas
    private JLabel lblContadorMascotas;
    private JTable tablaMascotas;
    private DefaultTableModel modeloMascotas;

    // Resumen clínico de mascota seleccionada
    private JLabel valMascota;
    private JLabel valPeso;
    private JLabel valPlanVacunal;
    private JLabel valAlergias;

    public interface NavegacionListener {
        void irAHistorialClinico(Mascota mascota);
        void irAConstanciasCertificados(Mascota mascota);
    }

    private NavegacionListener navegacionListener;

    public void setNavegacionListener(NavegacionListener listener) {
        this.navegacionListener = listener;
    }

    public VistaClientesMascotasPanel() {
        setLayout(new BorderLayout());
        setOpaque(false);

        JPanel contenido = new JPanel();
        contenido.setOpaque(false);
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setBorder(new EmptyBorder(12, 18, 14, 18));

        // 1. Cabecera de la vista
        contenido.add(crearCabeceraVista());
        contenido.add(Box.createVerticalStrut(8));

        // 2. Barra de herramientas de clientes compacta
        contenido.add(crearBarraHerramientasClientes());
        contenido.add(Box.createVerticalStrut(10));

        // 3. Contenedor de doble columna: Ficha de Cliente y Mascotas vinculadas
        contenido.add(crearSeccionDobleColumna());

        // Wrapper con BorderLayout.NORTH para evitar estiramientos verticales en pantallas maximizadas
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(contenido, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(wrapper);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);

        // Cargar por defecto el cliente del wireframe (Carlos Morales / Mendoza DNI 45892134)
        Optional<Cliente> opt = repo.getClientes().stream().findFirst();
        opt.ifPresent(this::cargarClienteEnFormulario);
    }

    private JPanel crearCabeceraVista() {
        JPanel cab = new JPanel(new BorderLayout());
        cab.setOpaque(false);
        cab.setPreferredSize(new Dimension(0, 40));
        cab.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JPanel izq = new JPanel();
        izq.setOpaque(false);
        izq.setLayout(new BoxLayout(izq, BoxLayout.Y_AXIS));

        JLabel lblTit = new JLabel("Mantenimiento de Clientes y Mascotas");
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblTit.setForeground(COLOR_TEXTO_TITULO);

        JLabel lblSub = new JLabel("Módulo 1.1 · Registro y administración de propietarios responsables y pacientes asociados");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblSub.setForeground(COLOR_TEXTO_MUTED);

        izq.add(lblTit);
        izq.add(Box.createVerticalStrut(2));
        izq.add(lblSub);
        cab.add(izq, BorderLayout.CENTER);

        // Botones de acción derecha compactos
        JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 2));
        der.setOpaque(false);

        JButton btnExportar = crearBotonAccion("Exportar Datos", false);
        btnExportar.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Los datos de clientes y mascotas se exportaron exitosamente a formato CSV/Excel.",
                "Exportación", JOptionPane.INFORMATION_MESSAGE));

        JButton btnNuevo = crearBotonAccion("+ Nuevo Cliente", true);
        btnNuevo.addActionListener(e -> nuevoCliente());

        der.add(btnExportar);
        der.add(btnNuevo);
        cab.add(der, BorderLayout.EAST);

        return cab;
    }

    private JPanel crearBarraHerramientasClientes() {
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

        // Izquierda: Acciones compactas
        JPanel izq = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
        izq.setOpaque(false);

        JButton btnListar = crearBotonAccion("Listar Clientes", true);
        JButton btnModificar = crearBotonAccion("Modificar", false);
        JButton btnEliminar = crearBotonAccion("Eliminar", false);

        btnListar.addActionListener(e -> mostrarDialogoListaClientes());
        btnModificar.addActionListener(e -> {
            txtNombres.requestFocus();
            JOptionPane.showMessageDialog(this, "Edite los campos deseados en la ficha del propietario y presione 'Guardar Cambios'.", "Modificar Cliente", JOptionPane.INFORMATION_MESSAGE);
        });
        btnEliminar.addActionListener(e -> eliminarClienteActual());

        izq.add(btnListar);
        izq.add(btnModificar);
        izq.add(btnEliminar);
        barra.add(izq, BorderLayout.WEST);

        // Derecha: Buscador compacto
        JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 2));
        der.setOpaque(false);

        JLabel lblBuscar = new JLabel("Buscar por DNI o Apellido:");
        lblBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblBuscar.setForeground(COLOR_TEXTO_MUTED);
        der.add(lblBuscar);

        txtBusqueda = new JTextField(12);
        txtBusqueda.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        txtBusqueda.setPreferredSize(new Dimension(150, 26));
        txtBusqueda.setMaximumSize(new Dimension(180, 26));
        txtBusqueda.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225), 1),
                new EmptyBorder(2, 6, 2, 6)
        ));
        txtBusqueda.setToolTipText("Ingrese DNI o apellido del cliente");
        der.add(txtBusqueda);

        JButton btnBuscar = crearBotonAccion("Buscar", false);
        btnBuscar.setIcon(Iconos.crearIconoBuscar(12, COLOR_AZUL_PRIMARIO));
        btnBuscar.setIconTextGap(4);

        java.awt.event.ActionListener accionBuscar = e -> buscarCliente();
        btnBuscar.addActionListener(accionBuscar);
        txtBusqueda.addActionListener(accionBuscar);
        der.add(btnBuscar);

        barra.add(der, BorderLayout.EAST);
        return barra;
    }

    private JPanel crearSeccionDobleColumna() {
        JPanel grid = new JPanel(new GridLayout(1, 2, 14, 0));
        grid.setOpaque(false);

        // Columna Izquierda: Ficha de Registro de Cliente
        grid.add(crearTarjetaFichaCliente());

        // Columna Derecha: Mascotas vinculadas
        grid.add(crearTarjetaMascotasVinculadas());

        return grid;
    }

    private JPanel crearTarjetaFichaCliente() {
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

        // Cabecera de la ficha
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JLabel lblTit = new JLabel("Ficha de Registro de Cliente");
        lblTit.setIcon(Iconos.crearIconoUsuario(15, COLOR_AZUL_PRIMARIO));
        lblTit.setIconTextGap(6);
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTit.setForeground(COLOR_TEXTO_TITULO);
        top.add(lblTit, BorderLayout.WEST);

        JLabel lblReq = new JLabel("(*) Campos obligatorios");
        lblReq.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblReq.setForeground(new Color(239, 68, 68));
        top.add(lblReq, BorderLayout.EAST);
        card.add(top, BorderLayout.NORTH);

        // Formulario ordenado y compacto
        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));

        txtNombres = crearCampoTextoCompacto();
        txtDocumento = crearCampoTextoCompacto();
        txtTelefono = crearCampoTextoCompacto();
        txtTelefonoSecundario = crearCampoTextoCompacto();
        txtCorreo = crearCampoTextoCompacto();
        txtDireccion = crearCampoTextoCompacto();
        txtCiudad = crearCampoTextoCompacto();
        txtNotas = new JTextArea(2, 20);
        txtNotas.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        txtNotas.setLineWrap(true);
        txtNotas.setWrapStyleWord(true);
        txtNotas.setBorder(new EmptyBorder(3, 6, 3, 6));

        JScrollPane spNotas = new JScrollPane(txtNotas);
        spNotas.setPreferredSize(new Dimension(0, 46));
        spNotas.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        spNotas.setBorder(BorderFactory.createLineBorder(new Color(203, 213, 225), 1));

        form.add(crearFilaCampo("Nombres y Apellidos Completos *", txtNombres));
        form.add(Box.createVerticalStrut(6));

        JPanel fila2 = new JPanel(new GridLayout(1, 2, 10, 0));
        fila2.setOpaque(false);
        fila2.setAlignmentX(Component.LEFT_ALIGNMENT);
        fila2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        fila2.add(crearFilaCampo("Doc. Identidad (DNI) *", txtDocumento));
        fila2.add(crearFilaCampo("Distrito / Ciudad", txtCiudad));
        form.add(fila2);
        form.add(Box.createVerticalStrut(6));

        JPanel fila3 = new JPanel(new GridLayout(1, 2, 10, 0));
        fila3.setOpaque(false);
        fila3.setAlignmentX(Component.LEFT_ALIGNMENT);
        fila3.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        fila3.add(crearFilaCampo("Teléfono Principal *", txtTelefono));
        fila3.add(crearFilaCampo("Teléfono Secundario", txtTelefonoSecundario));
        form.add(fila3);
        form.add(Box.createVerticalStrut(6));

        form.add(crearFilaCampo("Correo Electrónico", txtCorreo));
        form.add(Box.createVerticalStrut(6));

        form.add(crearFilaCampo("Dirección Domiciliaria", txtDireccion));
        form.add(Box.createVerticalStrut(6));

        form.add(crearFilaCampo("Notas Adicionales / Referencias Médicas", spNotas));

        card.add(form, BorderLayout.CENTER);

        // Botones inferiores de la ficha
        JPanel bot = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        bot.setOpaque(false);

        JButton btnLimpiar = crearBotonAccion("Limpiar", false);
        btnLimpiar.addActionListener(e -> limpiarFormulario());

        JButton btnGuardar = crearBotonAccion("Guardar Cambios", true);
        btnGuardar.addActionListener(e -> guardarCambiosCliente());

        bot.add(btnLimpiar);
        bot.add(btnGuardar);
        card.add(bot, BorderLayout.SOUTH);

        return card;
    }

    private JTextField crearCampoTextoCompacto() {
        JTextField tf = new JTextField();
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        tf.setForeground(new Color(30, 41, 59));
        tf.setPreferredSize(new Dimension(0, 26));
        tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225), 1),
                new EmptyBorder(2, 6, 2, 6)
        ));
        return tf;
    }

    private JPanel crearFilaCampo(String etiqueta, java.awt.Component componente) {
        JPanel p = new JPanel(new BorderLayout(0, 2));
        p.setOpaque(false);
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));

        JLabel lbl = new JLabel(etiqueta, SwingConstants.LEFT);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lbl.setForeground(new Color(100, 116, 139));
        lbl.setHorizontalAlignment(SwingConstants.LEFT);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        p.add(lbl, BorderLayout.NORTH);
        p.add(componente, BorderLayout.CENTER);
        return p;
    }

    private JButton crearBotonAccion(String texto, boolean primario) {
        JButton btn = new happypets.ui.BotonAsset(texto) {
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
        btn.setBorder(new EmptyBorder(4, 10, 4, 10));
        btn.setPreferredSize(new Dimension(btn.getPreferredSize().width, 28));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        return btn;
    }

    private JPanel crearTarjetaMascotasVinculadas() {
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

        JLabel lblTit = new JLabel("Mascotas Vinculadas");
        lblTit.setIcon(Iconos.crearIconoHuella(15, COLOR_AZUL_PRIMARIO));
        lblTit.setIconTextGap(6);
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTit.setForeground(COLOR_TEXTO_TITULO);
        top.add(lblTit, BorderLayout.WEST);

        lblContadorMascotas = new JLabel("0 pacientes asociados");
        lblContadorMascotas.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblContadorMascotas.setForeground(COLOR_TEXTO_MUTED);
        top.add(lblContadorMascotas, BorderLayout.EAST);
        card.add(top, BorderLayout.NORTH);

        // Centro: Tabla y resumen
        JPanel centro = new JPanel(new BorderLayout(0, 8));
        centro.setOpaque(false);

        // Barra de botones de mascotas compacta
        JPanel barBtns = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        barBtns.setOpaque(false);

        JButton btnNuevaMascota = crearBotonAccion("+ Nueva Mascota", true);
        JButton btnVerHistorial = crearBotonAccion("Historial Clínico", false);
        JButton btnCertificado = crearBotonAccion("Certificados", false);
        JButton btnEliminarMascota = crearBotonAccion("Eliminar", false);

        btnNuevaMascota.addActionListener(e -> agregarNuevaMascota());
        btnVerHistorial.addActionListener(e -> abrirHistorialDeMascota());
        btnCertificado.addActionListener(e -> abrirCertificadosDeMascota());
        btnEliminarMascota.addActionListener(e -> eliminarMascotaSeleccionada());

        barBtns.add(btnNuevaMascota);
        barBtns.add(btnVerHistorial);
        barBtns.add(btnCertificado);
        barBtns.add(btnEliminarMascota);
        centro.add(barBtns, BorderLayout.NORTH);

        // Tabla de mascotas compacta
        String[] columnas = {"Código", "Mascota", "Especie / Raza", "Edad", "Sexo", "Estado"};
        modeloMascotas = new DefaultTableModel(columnas, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        tablaMascotas = new JTable(modeloMascotas);
        Ui.formatearTabla(tablaMascotas);
        tablaMascotas.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        tablaMascotas.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        tablaMascotas.setRowHeight(26);

        tablaMascotas.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                actualizarResumenMascotaSeleccionada();
            }
        });

        tablaMascotas.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    abrirHistorialDeMascota();
                }
            }
        });

        JScrollPane sp = new JScrollPane(tablaMascotas);
        sp.setPreferredSize(new Dimension(500, 155));
        sp.setBorder(BorderFactory.createLineBorder(COLOR_BORDE, 1));
        centro.add(sp, BorderLayout.CENTER);

        // Resumen médico inferior de la mascota seleccionada
        centro.add(crearPanelResumenMascota(), BorderLayout.SOUTH);

        card.add(centro, BorderLayout.CENTER);
        return card;
    }

    private JPanel crearPanelResumenMascota() {
        JPanel res = new JPanel(new GridLayout(2, 2, 8, 4)) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(248, 250, 252));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(COLOR_BORDE);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        res.setOpaque(false);
        res.setBorder(new EmptyBorder(6, 10, 6, 10));

        valMascota = new JLabel("Mascota: -");
        valPeso = new JLabel("Peso: -");
        valPlanVacunal = new JLabel("Plan Vacunal: -");
        valAlergias = new JLabel("Alergias: -");

        Font f = new Font("Segoe UI", Font.PLAIN, 10);
        valMascota.setFont(new Font("Segoe UI", Font.BOLD, 10));
        valPeso.setFont(f);
        valPlanVacunal.setFont(f);
        valAlergias.setFont(f);

        res.add(valMascota);
        res.add(valPeso);
        res.add(valPlanVacunal);
        res.add(valAlergias);

        return res;
    }

    public void cargarClienteEnFormulario(Cliente c) {
        this.clienteActual = c;
        if (c == null) {
            limpiarFormulario();
            return;
        }

        txtNombres.setText(c.getNombreCompleto());
        txtDocumento.setText(c.getNumeroDocumento());
        txtTelefono.setText(c.getTelefonoPrincipal());
        txtTelefonoSecundario.setText(c.getTelefonoSecundario());
        txtCorreo.setText(c.getCorreo());
        txtDireccion.setText(c.getDireccion());
        txtCiudad.setText(c.getDistritoCiudad());
        txtNotas.setText(c.getNotasContacto());

        recargarTablaMascotas();
    }

    private void recargarTablaMascotas() {
        modeloMascotas.setRowCount(0);
        if (clienteActual == null) {
            lblContadorMascotas.setText("0 pacientes asociados");
            actualizarResumenMascotaSeleccionada();
            return;
        }

        List<Mascota> mascotas = clienteActual.getMascotas();
        lblContadorMascotas.setText(mascotas.size() + " pacientes asociados");

        for (Mascota m : mascotas) {
            modeloMascotas.addRow(new Object[]{
                    m.getCodigo(),
                    m.getNombre(),
                    m.getEspecie() + " · " + m.getRaza(),
                    m.getEdadTexto(),
                    m.getSexo(),
                    m.getEstadoTexto()
            });
        }

        if (!mascotas.isEmpty()) {
            tablaMascotas.setRowSelectionInterval(0, 0);
        }
        actualizarResumenMascotaSeleccionada();
    }

    private void actualizarResumenMascotaSeleccionada() {
        int row = tablaMascotas.getSelectedRow();
        if (row >= 0 && clienteActual != null && row < clienteActual.getMascotas().size()) {
            mascotaSeleccionada = clienteActual.getMascotas().get(row);
            valMascota.setText("Mascota: " + mascotaSeleccionada.getNombre() + " (" + mascotaSeleccionada.getEspecie() + " - " + mascotaSeleccionada.getRaza() + ")");
            valPeso.setText("Peso Actual: " + mascotaSeleccionada.getPesoActualKg() + " kg");
            valPlanVacunal.setText("Plan Vacunal: " + mascotaSeleccionada.getPlanVacunal());
            valAlergias.setText("Alergias/Observ: " + mascotaSeleccionada.getAlergias());
        } else {
            mascotaSeleccionada = null;
            valMascota.setText("Mascota: Ninguna seleccionada");
            valPeso.setText("Peso Actual: -");
            valPlanVacunal.setText("Plan Vacunal: -");
            valAlergias.setText("Alergias/Observ: -");
        }
    }

    public void guardarCambiosCliente() {
        String nombres = txtNombres.getText().trim();
        String doc = txtDocumento.getText().trim();
        String tel = txtTelefono.getText().trim();

        if (nombres.isEmpty() || doc.isEmpty() || tel.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Por favor complete los campos obligatorios: Nombres, Documento de Identidad y Teléfono Principal.",
                    "Datos Incompletos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String[] partes = nombres.split(" ", 2);
        String nom = partes[0];
        String ape = partes.length > 1 ? partes[1] : "";

        if (clienteActual == null) {
            String id = "CLI-00" + (repo.getClientes().size() + 1);
            clienteActual = new Cliente(id, nom, ape, "DNI", doc, tel, txtTelefonoSecundario.getText().trim(),
                    txtCorreo.getText().trim(), txtDireccion.getText().trim(), txtCiudad.getText().trim(), txtNotas.getText().trim());
            repo.guardarCliente(clienteActual);
        } else {
            clienteActual.setNombres(nom);
            clienteActual.setApellidos(ape);
            clienteActual.setNumeroDocumento(doc);
            clienteActual.setTelefonoPrincipal(tel);
            clienteActual.setTelefonoSecundario(txtTelefonoSecundario.getText().trim());
            clienteActual.setCorreo(txtCorreo.getText().trim());
            clienteActual.setDireccion(txtDireccion.getText().trim());
            clienteActual.setDistritoCiudad(txtCiudad.getText().trim());
            clienteActual.setNotasContacto(txtNotas.getText().trim());
            repo.guardarCliente(clienteActual);
        }

        JOptionPane.showMessageDialog(this, "Datos del cliente guardados correctamente en la base de datos.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }

    public void nuevoCliente() {
        clienteActual = null;
        limpiarFormulario();
        txtNombres.requestFocus();
    }

    public void limpiarFormulario() {
        txtNombres.setText("");
        txtDocumento.setText("");
        txtTelefono.setText("");
        txtTelefonoSecundario.setText("");
        txtCorreo.setText("");
        txtDireccion.setText("");
        txtCiudad.setText("");
        txtNotas.setText("");
        modeloMascotas.setRowCount(0);
        lblContadorMascotas.setText("0 pacientes asociados");
        actualizarResumenMascotaSeleccionada();
    }

    private void buscarCliente() {
        String query = txtBusqueda.getText().trim();
        if (query.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese un DNI o apellido para buscar.", "Búsqueda", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Optional<Cliente> opt = repo.buscarClientePorDniOApellido(query);
        if (opt.isPresent()) {
            cargarClienteEnFormulario(opt.get());
        } else {
            JOptionPane.showMessageDialog(this, "No se encontró ningún cliente registrado con el criterio: " + query, "No Encontrado", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void eliminarClienteActual() {
        if (clienteActual == null) {
            JOptionPane.showMessageDialog(this, "No hay ningún cliente seleccionado para eliminar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int r = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de eliminar al cliente " + clienteActual.getNombreCompleto() + " y todas sus mascotas asociadas?",
                "Confirmar Eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (r == JOptionPane.YES_OPTION) {
            repo.eliminarCliente(clienteActual.getCodigo());
            nuevoCliente();
            JOptionPane.showMessageDialog(this, "Cliente eliminado exitosamente.", "Eliminado", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void agregarNuevaMascota() {
        if (clienteActual == null) {
            JOptionPane.showMessageDialog(this, "Primero registre o seleccione un cliente para vincularle una mascota.", "Cliente Requerido", JOptionPane.WARNING_MESSAGE);
            return;
        }
        java.awt.Window parent = SwingUtilities.getWindowAncestor(this);
        JFrame parentFrame = (parent instanceof JFrame) ? (JFrame) parent : null;
        FormularioMascotaDialog dlg = new FormularioMascotaDialog(parentFrame, null, nueva -> {
            clienteActual.agregarMascota(nueva);
            recargarTablaMascotas();
        });
        dlg.setVisible(true);
    }

    private void abrirHistorialDeMascota() {
        if (mascotaSeleccionada == null) {
            JOptionPane.showMessageDialog(this, "Seleccione una mascota en la tabla para consultar su historial.", "Selección Requerida", JOptionPane.INFORMATION_MESSAGE);
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
            JOptionPane.showMessageDialog(this, "Seleccione una mascota en la tabla para emitir o consultar sus certificados.", "Selección Requerida", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        if (navegacionListener != null) {
            navegacionListener.irAConstanciasCertificados(mascotaSeleccionada);
        } else {
            ConstanciasCertificadosFrame cf = new ConstanciasCertificadosFrame(mascotaSeleccionada);
            cf.setVisible(true);
        }
    }

    private void eliminarMascotaSeleccionada() {
        if (mascotaSeleccionada == null || clienteActual == null) {
            JOptionPane.showMessageDialog(this, "Seleccione una mascota de la tabla para eliminar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int r = JOptionPane.showConfirmDialog(this,
                "¿Desea eliminar a la mascota " + mascotaSeleccionada.getNombre() + " del cliente " + clienteActual.getNombreCompleto() + "?",
                "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);
        if (r == JOptionPane.YES_OPTION) {
            clienteActual.eliminarMascota(mascotaSeleccionada.getCodigo());
            recargarTablaMascotas();
        }
    }

    private void mostrarDialogoListaClientes() {
        List<Cliente> clientes = repo.getClientes();
        String[] cols = {"DNI / Doc", "Cliente", "Teléfono", "Ciudad", "Mascotas"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        for (Cliente c : clientes) {
            model.addRow(new Object[]{
                    c.getNumeroDocumento(),
                    c.getNombreCompleto(),
                    c.getTelefonoPrincipal(),
                    c.getDistritoCiudad(),
                    c.getMascotas().size() + " mascota(s)"
            });
        }

        JTable table = new JTable(model);
        Ui.formatearTabla(table);
        table.setRowHeight(32);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setPreferredSize(new Dimension(620, 260));

        int res = JOptionPane.showConfirmDialog(this, scroll, "Listado de Clientes Registrados", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res == JOptionPane.OK_OPTION) {
            int row = table.getSelectedRow();
            if (row >= 0 && row < clientes.size()) {
                cargarClienteEnFormulario(clientes.get(row));
            }
        }
    }
}
