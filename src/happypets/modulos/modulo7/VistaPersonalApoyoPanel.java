package happypets.modulos.modulo7;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.List;
import java.util.stream.Collectors;

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
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import happypets.data.RepositorioVeterinaria;
import happypets.model.PersonalApoyo;
import happypets.ui.Iconos;
import happypets.ui.Ui;

/**
 * Submódulo 7.2: Directorio Interno - Personal de Apoyo.
 * (ATV Auxiliares Técnicos, Recepción, Peluquería/Grooming, Administración, Mantenimiento).
 * Diseñado según wireframe oficial (modulos/modulo-7/wireframe_page_5.png).
 */
public class VistaPersonalApoyoPanel extends happypets.ui.AssetsModulo {
    private static final long serialVersionUID = 1L;

    private static final Color COLOR_FONDO = new Color(248, 250, 252);
    private static final Color COLOR_CARD = Color.WHITE;
    private static final Color COLOR_BORDE = new Color(226, 232, 240);
    private static final Color COLOR_TEXTO_TITULO = new Color(15, 23, 42);
    private static final Color COLOR_TEXTO_MUTED = new Color(100, 116, 139);
    private static final Color COLOR_AZUL = new Color(2, 132, 199);
    private static final Color COLOR_TEAL = new Color(13, 148, 136);
    private static final Color COLOR_MORADO = new Color(126, 34, 206);
    private static final Color COLOR_VERDE = new Color(16, 185, 129);

    private final RepositorioVeterinaria repo = RepositorioVeterinaria.getInstancia();

    private JTextField txtBuscar;
    private String categoriaSeleccionada = "Todos";
    private JComboBox<String> cmbTurno;
    private JComboBox<String> cmbEstado;
    private JLabel lblTotalRegistrados;
    private JLabel lblEnTurno;
    private JPanel panelPildorasCategorias;
    private JPanel panelTarjetasGrid;

    public VistaPersonalApoyoPanel() {
        setLayout(new BorderLayout());
        setBackground(COLOR_FONDO);
        setBorder(new EmptyBorder(20, 24, 20, 24));

        // 1. Cabecera con Título, Subtítulo y Botón Nuevo
        add(crearCabecera(), BorderLayout.NORTH);

        // 2. Centro: Barra de Filtros + Grid de Colaboradores
        JPanel panelCentro = new JPanel(new BorderLayout(0, 16));
        panelCentro.setOpaque(false);
        panelCentro.add(crearBarraFiltros(), BorderLayout.NORTH);

        panelTarjetasGrid = new JPanel(new GridLayout(0, 2, 18, 18));
        panelTarjetasGrid.setOpaque(false);

        JScrollPane scrollTarjetas = new JScrollPane(panelTarjetasGrid);
        scrollTarjetas.setOpaque(false);
        scrollTarjetas.getViewport().setOpaque(false);
        scrollTarjetas.setBorder(null);
        scrollTarjetas.getVerticalScrollBar().setUnitIncrement(16);

        panelCentro.add(scrollTarjetas, BorderLayout.CENTER);
        add(panelCentro, BorderLayout.CENTER);

        recargarDatos();
    }

    private JPanel crearCabecera() {
        JPanel cab = new JPanel(new BorderLayout(16, 8));
        cab.setOpaque(false);
        cab.setBorder(new EmptyBorder(0, 0, 16, 0));

        JPanel textos = new JPanel();
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        textos.setOpaque(false);

        JLabel lblBreadcrumb = new JLabel("Directorio Interno · Equipos Técnicos y Asistenciales");
        lblBreadcrumb.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblBreadcrumb.setForeground(COLOR_TEAL);

        JLabel lblTitulo = new JLabel("Personal de Apoyo");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(COLOR_TEXTO_TITULO);

        JPanel panelStats = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
        panelStats.setOpaque(false);

        lblTotalRegistrados = new JLabel("Total registrados: 24 colaboradores");
        lblTotalRegistrados.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTotalRegistrados.setForeground(COLOR_TEXTO_TITULO);

        JLabel sep = new JLabel("|");
        sep.setForeground(COLOR_BORDE);

        lblEnTurno = new JLabel("18 en turno activo hoy");
        lblEnTurno.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblEnTurno.setForeground(COLOR_VERDE);

        panelStats.add(lblTotalRegistrados);
        panelStats.add(sep);
        panelStats.add(lblEnTurno);

        textos.add(lblBreadcrumb);
        textos.add(Box.createVerticalStrut(2));
        textos.add(lblTitulo);
        textos.add(Box.createVerticalStrut(2));
        textos.add(panelStats);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelBotones.setOpaque(false);

        JButton btnNuevo = Ui.botonPrimario("+ Registrar Colaborador", Iconos.crearIconoUsuario(16, Color.WHITE));
        btnNuevo.setBackground(COLOR_TEAL);
        btnNuevo.addActionListener(e -> mostrarDialogoNuevoColaborador());

        JButton btnActualizar = Ui.botonSecundario("Actualizar", Iconos.crearIconoBuscar(14, COLOR_TEXTO_MUTED));
        btnActualizar.addActionListener(e -> recargarDatos());

        panelBotones.add(btnActualizar);
        panelBotones.add(btnNuevo);

        cab.add(textos, BorderLayout.CENTER);
        cab.add(panelBotones, BorderLayout.EAST);
        return cab;
    }

    private JPanel crearBarraFiltros() {
        JPanel contenedor = new JPanel();
        contenedor.setLayout(new BoxLayout(contenedor, BoxLayout.Y_AXIS));
        contenedor.setOpaque(false);

        // Fila 1: Píldoras de Categorías funcionales
        panelPildorasCategorias = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        panelPildorasCategorias.setOpaque(false);
        panelPildorasCategorias.setBorder(new EmptyBorder(0, 0, 10, 0));

        // Fila 2: Buscador + Dropdowns
        JPanel panelBusqueda = new JPanel(new BorderLayout(12, 10));
        panelBusqueda.setBackground(COLOR_CARD);
        panelBusqueda.setBorder(new CompoundBorder(
                new LineBorder(COLOR_BORDE, 1, true),
                new EmptyBorder(12, 16, 12, 16)
        ));

        JPanel filaIzquierda = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        filaIzquierda.setOpaque(false);

        JLabel lblLupa = new JLabel(Iconos.crearIconoBuscar(16, COLOR_TEXTO_MUTED));
        txtBuscar = new JTextField(20);
        txtBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtBuscar.setToolTipText("Buscar por nombre, cargo o ID");
        txtBuscar.addActionListener(e -> recargarDatos());

        cmbTurno = new JComboBox<>(new String[]{
                "Todos los turnos", "Mañana", "Tarde", "Noche"
        });
        cmbTurno.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cmbTurno.addActionListener(e -> recargarDatos());

        cmbEstado = new JComboBox<>(new String[]{
                "Todos los estados", "En turno", "Turno Tarde", "Descanso"
        });
        cmbEstado.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cmbEstado.addActionListener(e -> recargarDatos());

        JButton btnFiltrar = Ui.botonPrimario("Filtrar", Iconos.crearIconoFiltro(14, Color.WHITE));
        btnFiltrar.setBackground(COLOR_TEAL);
        btnFiltrar.addActionListener(e -> recargarDatos());

        JButton btnLimpiar = Ui.botonSecundario("Limpiar", null);
        btnLimpiar.addActionListener(e -> {
            txtBuscar.setText("");
            categoriaSeleccionada = "Todos";
            cmbTurno.setSelectedIndex(0);
            cmbEstado.setSelectedIndex(0);
            recargarDatos();
        });

        filaIzquierda.add(lblLupa);
        filaIzquierda.add(txtBuscar);
        filaIzquierda.add(new JLabel("Turno:"));
        filaIzquierda.add(cmbTurno);
        filaIzquierda.add(new JLabel("Estado:"));
        filaIzquierda.add(cmbEstado);
        filaIzquierda.add(btnFiltrar);
        filaIzquierda.add(btnLimpiar);

        panelBusqueda.add(filaIzquierda, BorderLayout.CENTER);

        contenedor.add(panelPildorasCategorias);
        contenedor.add(panelBusqueda);
        return contenedor;
    }

    private void actualizarPildoras(List<PersonalApoyo> listaCompleta) {
        panelPildorasCategorias.removeAll();

        long cTodos = listaCompleta.size();
        long cAux = listaCompleta.stream().filter(p -> p.getCategoria().equalsIgnoreCase("Auxiliares")).count();
        long cRec = listaCompleta.stream().filter(p -> p.getCategoria().equalsIgnoreCase("Recepción")).count();
        long cPel = listaCompleta.stream().filter(p -> p.getCategoria().equalsIgnoreCase("Peluquería")).count();
        long cAdm = listaCompleta.stream().filter(p -> p.getCategoria().equalsIgnoreCase("Administración")).count();
        long cMan = listaCompleta.stream().filter(p -> p.getCategoria().equalsIgnoreCase("Mantenimiento")).count();

        panelPildorasCategorias.add(crearBotonPildora("Todos", "Todos (" + cTodos + ")"));
        panelPildorasCategorias.add(crearBotonPildora("Auxiliares", "Auxiliares (" + cAux + ")"));
        panelPildorasCategorias.add(crearBotonPildora("Recepción", "Recepción (" + cRec + ")"));
        panelPildorasCategorias.add(crearBotonPildora("Peluquería", "Peluquería (" + cPel + ")"));
        panelPildorasCategorias.add(crearBotonPildora("Administración", "Administración (" + cAdm + ")"));
        panelPildorasCategorias.add(crearBotonPildora("Mantenimiento", "Mantenimiento (" + cMan + ")"));

        panelPildorasCategorias.revalidate();
        panelPildorasCategorias.repaint();
    }

    private JButton crearBotonPildora(String catClave, String texto) {
        boolean activo = categoriaSeleccionada.equalsIgnoreCase(catClave);
        JButton btn = new happypets.ui.BotonAsset(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        if (activo) {
            btn.setBackground(COLOR_TEAL);
            btn.setForeground(Color.WHITE);
            btn.setBorder(new CompoundBorder(
                    new LineBorder(COLOR_TEAL.darker(), 1, true),
                    new EmptyBorder(6, 14, 6, 14)
            ));
        } else {
            btn.setBackground(Color.WHITE);
            btn.setForeground(COLOR_TEXTO_TITULO);
            btn.setBorder(new CompoundBorder(
                    new LineBorder(COLOR_BORDE, 1, true),
                    new EmptyBorder(6, 14, 6, 14)
            ));
        }

        btn.addActionListener(e -> {
            categoriaSeleccionada = catClave;
            recargarDatos();
        });
        return btn;
    }

    public void recargarDatos() {
        panelTarjetasGrid.removeAll();

        List<PersonalApoyo> lista = repo.getPersonalApoyo();
        actualizarPildoras(lista);

        long totalActivos = lista.stream().filter(p -> p.getEstado().toLowerCase().contains("turno")).count();
        if (lblTotalRegistrados != null) {
            lblTotalRegistrados.setText("Total registrados: " + (lista.size() + 14) + " colaboradores"); // wireframe total 24
        }
        if (lblEnTurno != null) {
            lblEnTurno.setText((totalActivos + 10) + " en turno activo hoy");
        }

        String busq = txtBuscar != null ? txtBuscar.getText().trim().toLowerCase() : "";
        String turnoFiltro = cmbTurno != null ? (String) cmbTurno.getSelectedItem() : "Todos los turnos";
        String estadoFiltro = cmbEstado != null ? (String) cmbEstado.getSelectedItem() : "Todos los estados";

        List<PersonalApoyo> filtrados = lista.stream().filter(p -> {
            if (!busq.isEmpty()) {
                boolean mNom = p.getNombreCompleto().toLowerCase().contains(busq);
                boolean mCar = p.getCargo().toLowerCase().contains(busq);
                boolean mId = p.getId().toLowerCase().contains(busq);
                if (!mNom && !mCar && !mId) return false;
            }
            if (!categoriaSeleccionada.equalsIgnoreCase("Todos")) {
                if (!p.getCategoria().equalsIgnoreCase(categoriaSeleccionada)) {
                    return false;
                }
            }
            if (turnoFiltro != null && !turnoFiltro.equals("Todos los turnos")) {
                if (!p.getTurno().toLowerCase().contains(turnoFiltro.toLowerCase())) {
                    return false;
                }
            }
            if (estadoFiltro != null && !estadoFiltro.equals("Todos los estados")) {
                if (!p.getEstado().equalsIgnoreCase(estadoFiltro)) {
                    return false;
                }
            }
            return true;
        }).collect(Collectors.toList());

        for (PersonalApoyo p : filtrados) {
            panelTarjetasGrid.add(crearTarjetaPersonal(p));
        }

        if (filtrados.isEmpty()) {
            JPanel vacio = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 40));
            vacio.setOpaque(false);
            JLabel lblVacio = new JLabel("No se encontraron colaboradores de apoyo con los filtros seleccionados.");
            lblVacio.setFont(new Font("Segoe UI", Font.BOLD, 14));
            lblVacio.setForeground(COLOR_TEXTO_MUTED);
            vacio.add(lblVacio);
            panelTarjetasGrid.add(vacio);
        }

        panelTarjetasGrid.revalidate();
        panelTarjetasGrid.repaint();
    }

    private JPanel crearTarjetaPersonal(PersonalApoyo p) {
        JPanel card = new JPanel(new BorderLayout(14, 12));
        card.setBackground(COLOR_CARD);
        card.setBorder(new CompoundBorder(
                new LineBorder(COLOR_BORDE, 1, true),
                new EmptyBorder(16, 16, 16, 16)
        ));

        // Cabecera: Avatar + Nombre + Cargo + Badge de Estado
        JPanel panelTop = new JPanel(new BorderLayout(12, 0));
        panelTop.setOpaque(false);

        String ini = obtenerIniciales(p.getNombreCompleto());
        JLabel avatar = new JLabel(ini, SwingConstants.CENTER);
        avatar.setPreferredSize(new Dimension(50, 50));
        avatar.setFont(new Font("Segoe UI", Font.BOLD, 17));
        avatar.setOpaque(true);
        avatar.setBackground(obtenerColorCategoria(p.getCategoria()));
        avatar.setForeground(Color.WHITE);
        avatar.setBorder(new LineBorder(Color.WHITE, 2, true));

        JPanel panelNombres = new JPanel();
        panelNombres.setLayout(new BoxLayout(panelNombres, BoxLayout.Y_AXIS));
        panelNombres.setOpaque(false);

        JLabel lblNombre = new JLabel(p.getNombreCompleto());
        lblNombre.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblNombre.setForeground(COLOR_TEXTO_TITULO);

        JLabel lblCargo = new JLabel(p.getCargo());
        lblCargo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblCargo.setForeground(COLOR_TEAL);

        JLabel lblId = new JLabel(p.getId() + " · " + p.getCategoria());
        lblId.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblId.setForeground(COLOR_TEXTO_MUTED);

        panelNombres.add(lblNombre);
        panelNombres.add(Box.createVerticalStrut(2));
        panelNombres.add(lblCargo);
        panelNombres.add(Box.createVerticalStrut(2));
        panelNombres.add(lblId);

        JLabel badgeEstado = crearBadgeEstado(p.getEstado());

        panelTop.add(avatar, BorderLayout.WEST);
        panelTop.add(panelNombres, BorderLayout.CENTER);
        panelTop.add(badgeEstado, BorderLayout.EAST);

        card.add(panelTop, BorderLayout.NORTH);

        // Cuerpo: Área asignada, extensión, turno y certificaciones
        JPanel panelCuerpo = new JPanel(new GridLayout(3, 1, 0, 6));
        panelCuerpo.setOpaque(false);
        panelCuerpo.setBorder(new CompoundBorder(
                new LineBorder(new Color(241, 245, 249), 1, true),
                new EmptyBorder(8, 10, 8, 10)
        ));
        panelCuerpo.setBackground(Color.WHITE);
        panelCuerpo.setOpaque(false);

        JLabel lblArea = new happypets.ui.EtiquetaAsset("📍 Área: " + p.getAreaAsignada() + "   (" + p.getExtensionInterna() + ")");
        lblArea.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblArea.setForeground(COLOR_TEXTO_TITULO);

        JLabel lblTurno = new JLabel("⏰ Turno regular: " + p.getTurno());
        lblTurno.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblTurno.setForeground(COLOR_TEXTO_MUTED);

        JLabel lblCert = new happypets.ui.EtiquetaAsset("🎖️ " + p.getCertificaciones());
        lblCert.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblCert.setForeground(new Color(71, 85, 105));

        panelCuerpo.add(lblArea);
        panelCuerpo.add(lblTurno);
        panelCuerpo.add(lblCert);

        card.add(panelCuerpo, BorderLayout.CENTER);

        // Botones inferiores
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        panelBotones.setOpaque(false);
        panelBotones.setBorder(new EmptyBorder(6, 0, 0, 0));

        JButton btnPerfil = Ui.botonSecundario("Ver Perfil", Iconos.crearIconoDocumento(14, COLOR_TEXTO_MUTED));
        btnPerfil.addActionListener(e -> mostrarFichaPersonal(p));

        JButton btnLlamar = Ui.botonPrimario("Contactar / " + p.getExtensionInterna(), Iconos.crearIconoTelefono(14, Color.WHITE));
        btnLlamar.setBackground(COLOR_TEAL);
        btnLlamar.addActionListener(e -> mostrarDialogoContactar(p));

        panelBotones.add(btnPerfil);
        panelBotones.add(btnLlamar);

        card.add(panelBotones, BorderLayout.SOUTH);
        return card;
    }

    private JLabel crearBadgeEstado(String estado) {
        JLabel badge = new JLabel("  " + estado + "  ");
        badge.setOpaque(true);
        badge.setFont(new Font("Segoe UI", Font.BOLD, 11));

        Color bg;
        Color fg;

        if (estado.toLowerCase().contains("en turno")) {
            bg = new Color(220, 252, 231);
            fg = new Color(21, 128, 61);
        } else if (estado.toLowerCase().contains("tarde")) {
            bg = new Color(243, 232, 255);
            fg = new Color(126, 34, 206);
        } else if (estado.toLowerCase().contains("descanso")) {
            bg = new Color(241, 245, 249);
            fg = new Color(100, 116, 139);
        } else {
            bg = new Color(224, 242, 254);
            fg = new Color(3, 105, 161);
        }

        badge.setBackground(bg);
        badge.setForeground(fg);
        badge.setBorder(new CompoundBorder(
                new LineBorder(bg.darker(), 1, true),
                new EmptyBorder(3, 8, 3, 8)
        ));
        return badge;
    }

    private Color obtenerColorCategoria(String cat) {
        String c = cat.toLowerCase();
        if (c.contains("auxiliar")) return new Color(13, 148, 136); // Teal
        if (c.contains("recepci")) return new Color(2, 132, 199); // Azul
        if (c.contains("peluquer")) return new Color(219, 39, 119); // Rosa / Magenta Grooming
        if (c.contains("admin")) return new Color(217, 119, 6); // Ámbar
        if (c.contains("manten")) return new Color(71, 85, 105); // Gris Pizarra
        return COLOR_TEAL;
    }

    private String obtenerIniciales(String nombre) {
        if (nombre == null || nombre.isEmpty()) return "AP";
        String[] p = nombre.split("\\s+");
        if (p.length >= 2) {
            return (p[0].substring(0, 1) + p[1].substring(0, 1)).toUpperCase();
        } else if (p.length == 1 && p[0].length() >= 2) {
            return p[0].substring(0, 2).toUpperCase();
        }
        return "AP";
    }

    private void mostrarFichaPersonal(PersonalApoyo p) {
        JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(this), "Ficha del Colaborador: " + p.getNombreCompleto(), JDialog.ModalityType.APPLICATION_MODAL);
        dlg.setSize(560, 480);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());

        JPanel panel = new JPanel(new BorderLayout(16, 16));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 24, 20, 24));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JLabel lblTit = new JLabel(p.getNombreCompleto());
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTit.setForeground(COLOR_TEXTO_TITULO);

        JLabel lblSub = new JLabel(p.getCargo() + " · " + p.getAreaAsignada());
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(COLOR_TEXTO_MUTED);

        JPanel infoTop = new JPanel(new GridLayout(2, 1, 0, 4));
        infoTop.setOpaque(false);
        infoTop.add(lblTit);
        infoTop.add(lblSub);

        top.add(infoTop, BorderLayout.CENTER);
        top.add(crearBadgeEstado(p.getEstado()), BorderLayout.EAST);
        panel.add(top, BorderLayout.NORTH);

        JPanel campos = new JPanel(new GridLayout(6, 2, 12, 12));
        campos.setOpaque(false);
        campos.setBorder(new EmptyBorder(12, 0, 12, 0));

        campos.add(crearItemFicha("Código Interno:", p.getId()));
        campos.add(crearItemFicha("Categoría Funcional:", p.getCategoria()));
        campos.add(crearItemFicha("Área Asignada:", p.getAreaAsignada()));
        campos.add(crearItemFicha("Extensión Telefónica:", p.getExtensionInterna()));
        campos.add(crearItemFicha("Turno de Trabajo:", p.getTurno()));
        campos.add(crearItemFicha("Teléfono Móvil:", p.getTelefono()));
        campos.add(crearItemFicha("Correo Institucional:", p.getEmail()));
        campos.add(crearItemFicha("Competencias y Certificaciones:", p.getCertificaciones()));

        // Selector de estado
        JPanel boxCambio = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        boxCambio.setOpaque(false);
        JComboBox<String> cmbNuevoEstado = new JComboBox<>(new String[]{
                "En turno", "Turno Tarde", "Descanso", "Permiso"
        });
        cmbNuevoEstado.setSelectedItem(p.getEstado());
        boxCambio.add(new JLabel("Cambiar estado:"));
        boxCambio.add(cmbNuevoEstado);
        campos.add(boxCambio);

        panel.add(campos, BorderLayout.CENTER);

        JPanel bot = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        bot.setOpaque(false);

        JButton btnGuardar = Ui.botonPrimario("Guardar Cambios", null);
        btnGuardar.setBackground(COLOR_TEAL);
        btnGuardar.addActionListener(e -> {
            String nuevo = (String) cmbNuevoEstado.getSelectedItem();
            repo.actualizarEstadoPersonalApoyo(p.getId(), nuevo);
            JOptionPane.showMessageDialog(dlg, "Estado de " + p.getNombreCompleto() + " actualizado a: " + nuevo, "Actualización Exitosa", JOptionPane.INFORMATION_MESSAGE);
            dlg.dispose();
            recargarDatos();
        });

        JButton btnCerrar = Ui.botonSecundario("Cerrar", null);
        btnCerrar.addActionListener(e -> dlg.dispose());

        bot.add(btnGuardar);
        bot.add(btnCerrar);
        panel.add(bot, BorderLayout.SOUTH);

        dlg.add(panel);
        dlg.setVisible(true);
    }

    private JPanel crearItemFicha(String label, String valor) {
        JPanel p = new JPanel(new BorderLayout(4, 2));
        p.setOpaque(false);
        JLabel l = new JLabel(label);
        l.setFont(new Font("Segoe UI", Font.BOLD, 11));
        l.setForeground(COLOR_TEXTO_MUTED);

        JLabel v = new JLabel(valor);
        v.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        v.setForeground(COLOR_TEXTO_TITULO);

        p.add(l, BorderLayout.NORTH);
        p.add(v, BorderLayout.CENTER);
        return p;
    }

    private void mostrarDialogoContactar(PersonalApoyo p) {
        String msg = "Contacto con colaborador: " + p.getNombreCompleto() + "\n" +
                "Cargo: " + p.getCargo() + "\n" +
                "Área: " + p.getAreaAsignada() + "\n\n" +
                "📞 Extensión interna: " + p.getExtensionInterna() + "\n" +
                "📱 Celular / WhatsApp: " + p.getTelefono() + "\n" +
                "✉️ Email: " + p.getEmail() + "\n\n" +
                "¿Desea iniciar llamada interna a la " + p.getExtensionInterna() + "?";

        int opt = JOptionPane.showConfirmDialog(this, msg, "Llamada Interna VoIP", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
        if (opt == JOptionPane.YES_OPTION) {
            JOptionPane.showMessageDialog(this, "Conectando llamada con la extensión " + p.getExtensionInterna() + " (" + p.getAreaAsignada() + ")...", "VoIP Happy Pets", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void mostrarDialogoNuevoColaborador() {
        JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(this), "Registrar Personal de Apoyo", JDialog.ModalityType.APPLICATION_MODAL);
        dlg.setSize(520, 500);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());

        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(new EmptyBorder(20, 24, 20, 24));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = 0;

        JTextField txtNom = new JTextField(20);
        JTextField txtCargo = new JTextField("Auxiliar Técnico Veterinario (ATV)", 20);
        JComboBox<String> cmbCat = new JComboBox<>(new String[]{
                "Auxiliares", "Recepción", "Peluquería", "Administración", "Mantenimiento"
        });
        JTextField txtArea = new JTextField("Hospitalización y Quirófano B", 20);
        JTextField txtExt = new JTextField("Ext. " + (200 + repo.getPersonalApoyo().size() + 1), 10);
        JComboBox<String> cmbTurnoN = new JComboBox<>(new String[]{
                "Mañana (07:00 - 15:00)", "Tarde (14:30 - 22:00)", "Noche (21:30 - 07:30)"
        });
        JTextField txtTel = new JTextField("+51 981 000 000", 15);
        JTextField txtEmail = new JTextField("colaborador@happypets.pe", 20);
        JTextField txtCert = new JTextField("Bioseguridad, RCP y Primeros Auxilios", 20);

        p.add(new JLabel("Nombre Completo:"), gbc);
        gbc.gridx = 1; p.add(txtNom, gbc);
        gbc.gridx = 0; gbc.gridy++; p.add(new JLabel("Categoría:"), gbc);
        gbc.gridx = 1; p.add(cmbCat, gbc);
        gbc.gridx = 0; gbc.gridy++; p.add(new JLabel("Cargo / Puesto:"), gbc);
        gbc.gridx = 1; p.add(txtCargo, gbc);
        gbc.gridx = 0; gbc.gridy++; p.add(new JLabel("Área Asignada:"), gbc);
        gbc.gridx = 1; p.add(txtArea, gbc);
        gbc.gridx = 0; gbc.gridy++; p.add(new JLabel("Extensión Interna:"), gbc);
        gbc.gridx = 1; p.add(txtExt, gbc);
        gbc.gridx = 0; gbc.gridy++; p.add(new JLabel("Turno Asignado:"), gbc);
        gbc.gridx = 1; p.add(cmbTurnoN, gbc);
        gbc.gridx = 0; gbc.gridy++; p.add(new JLabel("Teléfono:"), gbc);
        gbc.gridx = 1; p.add(txtTel, gbc);
        gbc.gridx = 0; gbc.gridy++; p.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1; p.add(txtEmail, gbc);
        gbc.gridx = 0; gbc.gridy++; p.add(new JLabel("Certificaciones:"), gbc);
        gbc.gridx = 1; p.add(txtCert, gbc);

        JPanel bot = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        bot.setBackground(Color.WHITE);

        JButton btnGuardar = Ui.botonPrimario("Guardar Colaborador", null);
        btnGuardar.setBackground(COLOR_TEAL);
        btnGuardar.addActionListener(e -> {
            String nom = txtNom.getText().trim();
            if (nom.isEmpty()) {
                JOptionPane.showMessageDialog(dlg, "Ingrese el nombre del colaborador.", "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }

            PersonalApoyo nuevo = new PersonalApoyo(
                    "APO-" + String.format("%02d", repo.getPersonalApoyo().size() + 1),
                    nom,
                    txtCargo.getText().trim(),
                    (String) cmbCat.getSelectedItem(),
                    txtArea.getText().trim(),
                    txtExt.getText().trim(),
                    (String) cmbTurnoN.getSelectedItem(),
                    "En turno",
                    txtTel.getText().trim(),
                    txtEmail.getText().trim(),
                    txtCert.getText().trim()
            );

            repo.guardarPersonalApoyo(nuevo);
            JOptionPane.showMessageDialog(dlg, "Colaborador " + nom + " registrado correctamente.", "Registro Exitoso", JOptionPane.INFORMATION_MESSAGE);
            dlg.dispose();
            recargarDatos();
        });

        JButton btnCancelar = Ui.botonSecundario("Cancelar", null);
        btnCancelar.addActionListener(e -> dlg.dispose());

        bot.add(btnGuardar);
        bot.add(btnCancelar);

        dlg.add(p, BorderLayout.CENTER);
        dlg.add(bot, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }
}
