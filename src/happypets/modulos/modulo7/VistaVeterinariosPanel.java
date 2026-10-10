package happypets.modulos.modulo7;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import happypets.data.RepositorioVeterinaria;
import happypets.model.Veterinario;
import happypets.ui.Iconos;
import happypets.ui.Ui;

/**
 * Submódulo 7.1: Directorio de Especialistas Clínicos Colegiados ("Nuestros Veterinarios").
 * Diseñado según wireframe oficial (modulos/modulo-7/wireframe_page_3.png).
 */
public class VistaVeterinariosPanel extends happypets.ui.AssetsModulo {
    private static final long serialVersionUID = 1L;

    private static final Color COLOR_FONDO = new Color(248, 250, 252);
    private static final Color COLOR_CARD = Color.WHITE;
    private static final Color COLOR_BORDE = new Color(226, 232, 240);
    private static final Color COLOR_TEXTO_TITULO = new Color(15, 23, 42);
    private static final Color COLOR_TEXTO_MUTED = new Color(100, 116, 139);
    private static final Color COLOR_AZUL = new Color(2, 132, 199);
    private static final Color COLOR_MORADO = new Color(126, 34, 206);
    private static final Color COLOR_VERDE = new Color(16, 185, 129);
    private static final Color COLOR_AMBAR = new Color(245, 158, 11);

    private final RepositorioVeterinaria repo = RepositorioVeterinaria.getInstancia();

    private JTextField txtBuscar;
    private JComboBox<String> cmbEspecialidad;
    private JComboBox<String> cmbDisponibilidad;
    private JComboBox<String> cmbOrdenarPor;
    private JLabel lblContador;
    private JPanel panelTarjetasGrid;

    public VistaVeterinariosPanel() {
        setLayout(new BorderLayout());
        setBackground(COLOR_FONDO);
        setBorder(new EmptyBorder(20, 24, 20, 24));

        // 1. Cabecera con Título, Subtítulo y Botón Nuevo
        add(crearCabecera(), BorderLayout.NORTH);

        // 2. Centro: Barra de Filtros + Grid de Tarjetas con Scroll
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

        JLabel lblBreadcrumb = new JLabel("Equipo Médico · Cuadro Médico Colegiado");
        lblBreadcrumb.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblBreadcrumb.setForeground(COLOR_MORADO);

        JLabel lblTitulo = new JLabel("Nuestros Veterinarios");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(COLOR_TEXTO_TITULO);

        JLabel lblSub = new JLabel("Directorio de especialistas clínicos colegiados, especialidades quirúrgicas y disponibilidad en tiempo real.");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(COLOR_TEXTO_MUTED);

        textos.add(lblBreadcrumb);
        textos.add(Box.createVerticalStrut(2));
        textos.add(lblTitulo);
        textos.add(Box.createVerticalStrut(2));
        textos.add(lblSub);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelBotones.setOpaque(false);

        JButton btnNuevo = Ui.botonPrimario("+ Registrar Veterinario", Iconos.crearIconoDoctor(16, Color.WHITE));
        btnNuevo.setBackground(COLOR_MORADO);
        btnNuevo.addActionListener(e -> mostrarDialogoNuevoVeterinario());

        JButton btnActualizar = Ui.botonSecundario("Actualizar", Iconos.crearIconoBuscar(14, COLOR_TEXTO_MUTED));
        btnActualizar.addActionListener(e -> recargarDatos());

        panelBotones.add(btnActualizar);
        panelBotones.add(btnNuevo);

        for (java.awt.Component componente : textos.getComponents()) {
            if (componente instanceof javax.swing.JComponent jc) jc.setAlignmentX(LEFT_ALIGNMENT);
        }
        cab.add(textos, BorderLayout.CENTER);
        cab.add(panelBotones, BorderLayout.SOUTH);
        return cab;
    }

    private JPanel crearBarraFiltros() {
        JPanel panel = new JPanel(new BorderLayout(12, 10));
        panel.setBackground(COLOR_CARD);
        panel.setBorder(new CompoundBorder(
                new LineBorder(COLOR_BORDE, 1, true),
                new EmptyBorder(12, 16, 12, 16)
        ));

        JPanel filaFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        filaFiltros.setOpaque(false);

        // Buscador
        JLabel lblLupa = new JLabel(Iconos.crearIconoBuscar(16, COLOR_TEXTO_MUTED));
        txtBuscar = new JTextField(18);
        txtBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtBuscar.setToolTipText("Buscar por nombre, apellido o especialidad");
        txtBuscar.addActionListener(e -> recargarDatos());

        // Filtro Especialidad
        cmbEspecialidad = new JComboBox<>(new String[]{
                "Todas las especialidades",
                "Cirugía General",
                "Dermatología",
                "Animales Exóticos",
                "Cardiología",
                "Medicina Felina",
                "Oftalmología",
                "Urgencias",
                "Oncología"
        });
        cmbEspecialidad.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cmbEspecialidad.addActionListener(e -> recargarDatos());

        // Filtro Disponibilidad
        cmbDisponibilidad = new JComboBox<>(new String[]{
                "Cualquier horario",
                "Mañana",
                "Tarde",
                "Noche"
        });
        cmbDisponibilidad.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cmbDisponibilidad.addActionListener(e -> recargarDatos());

        // Ordenar por
        cmbOrdenarPor = new JComboBox<>(new String[]{
                "Ordenar por: Relevancia",
                "Ordenar por: Experiencia",
                "Ordenar por: Disponibilidad"
        });
        cmbOrdenarPor.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cmbOrdenarPor.addActionListener(e -> recargarDatos());

        JButton btnFiltrar = Ui.botonPrimario("Filtrar", Iconos.crearIconoFiltro(14, Color.WHITE));
        btnFiltrar.setBackground(COLOR_AZUL);
        btnFiltrar.addActionListener(e -> recargarDatos());

        JButton btnLimpiar = Ui.botonSecundario("Limpiar", null);
        btnLimpiar.addActionListener(e -> {
            txtBuscar.setText("");
            cmbEspecialidad.setSelectedIndex(0);
            cmbDisponibilidad.setSelectedIndex(0);
            cmbOrdenarPor.setSelectedIndex(0);
            recargarDatos();
        });

        filaFiltros.add(lblLupa);
        filaFiltros.add(txtBuscar);
        filaFiltros.add(new JLabel("Especialidad:"));
        filaFiltros.add(cmbEspecialidad);
        filaFiltros.add(new JLabel("Turno:"));
        filaFiltros.add(cmbDisponibilidad);
        filaFiltros.add(cmbOrdenarPor);
        filaFiltros.add(btnFiltrar);
        filaFiltros.add(btnLimpiar);

        lblContador = new JLabel("Mostrando 0 especialistas");
        lblContador.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblContador.setForeground(COLOR_TEXTO_MUTED);

        panel.add(filaFiltros, BorderLayout.CENTER);
        panel.add(lblContador, BorderLayout.EAST);
        return panel;
    }

    public void recargarDatos() {
        panelTarjetasGrid.removeAll();

        List<Veterinario> lista = repo.getVeterinarios();
        String busq = txtBuscar != null ? txtBuscar.getText().trim().toLowerCase() : "";
        String espFiltro = cmbEspecialidad != null ? (String) cmbEspecialidad.getSelectedItem() : "Todas las especialidades";
        String dispFiltro = cmbDisponibilidad != null ? (String) cmbDisponibilidad.getSelectedItem() : "Cualquier horario";
        String ordenFiltro = cmbOrdenarPor != null ? (String) cmbOrdenarPor.getSelectedItem() : "Ordenar por: Relevancia";

        List<Veterinario> filtrados = lista.stream().filter(v -> {
            if (!busq.isEmpty()) {
                boolean matchNom = v.getNombreCompleto().toLowerCase().contains(busq);
                boolean matchEsp = v.getEspecialidad().toLowerCase().contains(busq);
                boolean matchCol = v.getColegiaturaCMPV().toLowerCase().contains(busq);
                if (!matchNom && !matchEsp && !matchCol) return false;
            }
            if (espFiltro != null && !espFiltro.equals("Todas las especialidades")) {
                if (!v.getEspecialidad().toLowerCase().contains(espFiltro.toLowerCase())) {
                    return false;
                }
            }
            if (dispFiltro != null && !dispFiltro.equals("Cualquier horario")) {
                if (!v.getHorarioTurno().toLowerCase().contains(dispFiltro.toLowerCase()) &&
                        !v.getEstadoDisponibilidad().toLowerCase().contains(dispFiltro.toLowerCase())) {
                    return false;
                }
            }
            return true;
        }).collect(Collectors.toList());

        // Ordenamiento
        if (ordenFiltro.contains("Experiencia")) {
            filtrados.sort((a, b) -> Integer.compare(b.getAniosExperiencia(), a.getAniosExperiencia()));
        } else if (ordenFiltro.contains("Disponibilidad")) {
            filtrados.sort((a, b) -> a.getEstadoDisponibilidad().compareToIgnoreCase(b.getEstadoDisponibilidad()));
        }

        if (lblContador != null) {
            lblContador.setText("Mostrando " + filtrados.size() + " de " + lista.size() + " especialistas");
        }

        for (Veterinario v : filtrados) {
            panelTarjetasGrid.add(crearTarjetaVeterinario(v));
        }

        if (filtrados.isEmpty()) {
            JPanel vacio = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 40));
            vacio.setOpaque(false);
            JLabel lblVacio = new JLabel("No se encontraron especialistas veterinarios con los filtros seleccionados.");
            lblVacio.setFont(new Font("Segoe UI", Font.BOLD, 14));
            lblVacio.setForeground(COLOR_TEXTO_MUTED);
            vacio.add(lblVacio);
            panelTarjetasGrid.add(vacio);
        }

        panelTarjetasGrid.revalidate();
        panelTarjetasGrid.repaint();
    }

    private JPanel crearTarjetaVeterinario(Veterinario v) {
        JPanel card = new JPanel(new BorderLayout(14, 12));
        card.setBackground(COLOR_CARD);
        card.setBorder(new CompoundBorder(
                new LineBorder(COLOR_BORDE, 1, true),
                new EmptyBorder(16, 16, 16, 16)
        ));

        // Cabecera de la tarjeta: Avatar circular + Nombre + CMPV + Badge de Estado
        JPanel panelTop = new JPanel(new BorderLayout(12, 0));
        panelTop.setOpaque(false);

        // Avatar con iniciales
        String iniciales = obtenerIniciales(v.getNombreCompleto());
        JLabel avatar = new JLabel(iniciales, SwingConstants.CENTER);
        avatar.setPreferredSize(new Dimension(54, 54));
        avatar.setFont(new Font("Segoe UI", Font.BOLD, 18));
        avatar.setOpaque(true);
        avatar.setBackground(obtenerColorAvatar(v.getEspecialidad()));
        avatar.setForeground(Color.WHITE);
        avatar.setBorder(new LineBorder(Color.WHITE, 2, true));

        JPanel panelNombres = new JPanel();
        panelNombres.setLayout(new BoxLayout(panelNombres, BoxLayout.Y_AXIS));
        panelNombres.setOpaque(false);

        JLabel lblNombre = new JLabel(v.getNombreCompleto());
        lblNombre.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblNombre.setForeground(COLOR_TEXTO_TITULO);

        JLabel lblCol = new JLabel(v.getColegiaturaCMPV() + " · " + v.getAniosExperiencia() + " años exp.");
        lblCol.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblCol.setForeground(COLOR_TEXTO_MUTED);

        JLabel lblRating = new happypets.ui.EtiquetaAsset("★ " + String.format("%.1f", v.getCalificacionEstrellas()) +
                "  (" + v.getTotalAtencionesRealizadas() + " atenciones registradas)");
        lblRating.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblRating.setForeground(new Color(217, 119, 6)); // Ámbar oscuro

        panelNombres.add(lblNombre);
        panelNombres.add(Box.createVerticalStrut(2));
        panelNombres.add(lblCol);
        panelNombres.add(Box.createVerticalStrut(2));
        panelNombres.add(lblRating);

        // Badge de disponibilidad superior derecho
        JLabel badgeEstado = crearBadgeEstado(v.getEstadoDisponibilidad());

        panelTop.add(avatar, BorderLayout.WEST);
        panelTop.add(panelNombres, BorderLayout.CENTER);
        panelTop.add(badgeEstado, BorderLayout.EAST);

        card.add(panelTop, BorderLayout.NORTH);

        // Cuerpo de la tarjeta: Badge especialidad, descripción y horarios
        JPanel panelCuerpo = new JPanel();
        panelCuerpo.setLayout(new BoxLayout(panelCuerpo, BoxLayout.Y_AXIS));
        panelCuerpo.setOpaque(false);

        // Píldora de Especialidad
        JPanel panelEsp = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 4));
        panelEsp.setOpaque(false);
        JLabel badgeEsp = new JLabel("  " + v.getEspecialidad() + "  ");
        badgeEsp.setOpaque(true);
        badgeEsp.setFont(new Font("Segoe UI", Font.BOLD, 11));
        badgeEsp.setBackground(new Color(243, 232, 255)); // Morado suave
        badgeEsp.setForeground(COLOR_MORADO);
        badgeEsp.setBorder(new CompoundBorder(
                new LineBorder(new Color(216, 180, 254), 1, true),
                new EmptyBorder(3, 6, 3, 6)
        ));
        panelEsp.add(badgeEsp);

        // Descripción clínica
        JTextArea txtBio = new JTextArea(v.getDescripcionBio());
        txtBio.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtBio.setForeground(new Color(71, 85, 105));
        txtBio.setLineWrap(true);
        txtBio.setWrapStyleWord(true);
        txtBio.setEditable(false);
        txtBio.setOpaque(false);
        txtBio.setBorder(new EmptyBorder(4, 0, 6, 0));

        // Caja de Horario y Disponibilidad (Fondo gris tenue)
        JPanel boxHorario = new JPanel(new GridLayout(2, 2, 8, 4));
        boxHorario.setBackground(new Color(241, 245, 249));
        boxHorario.setBorder(new CompoundBorder(
                new LineBorder(COLOR_BORDE, 1, true),
                new EmptyBorder(8, 10, 8, 10)
        ));

        JLabel lblDias = new happypets.ui.EtiquetaAsset("📅 Días: " + v.getDiasAtencion());
        lblDias.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblDias.setForeground(COLOR_TEXTO_TITULO);

        JLabel lblHoras = new JLabel("⏰ Turno: " + v.getHorarioTurno());
        lblHoras.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblHoras.setForeground(COLOR_TEXTO_TITULO);

        JLabel lblLugar = new happypets.ui.EtiquetaAsset("📍 " + v.getConsultorioHabitual());
        lblLugar.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblLugar.setForeground(COLOR_TEXTO_TITULO);

        JLabel lblProx = new happypets.ui.EtiquetaAsset("⚡ Cita libre: " + v.getProximaCitaLibre());
        lblProx.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblProx.setForeground(COLOR_AZUL);

        boxHorario.add(lblDias);
        boxHorario.add(lblHoras);
        boxHorario.add(lblLugar);
        boxHorario.add(lblProx);

        panelCuerpo.add(panelEsp);
        panelCuerpo.add(txtBio);
        panelCuerpo.add(Box.createVerticalStrut(4));
        panelCuerpo.add(boxHorario);

        card.add(panelCuerpo, BorderLayout.CENTER);

        // Botones de acción inferiores
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        panelBotones.setOpaque(false);
        panelBotones.setBorder(new EmptyBorder(6, 0, 0, 0));

        JButton btnFicha = Ui.botonSecundario("Ver Perfil / Ficha", Iconos.crearIconoDocumento(14, COLOR_TEXTO_MUTED));
        btnFicha.addActionListener(e -> mostrarFichaVeterinario(v));

        JButton btnReservar = Ui.botonPrimario("Reservar Cita / Derivar", Iconos.crearIconoCalendario(14, Color.WHITE));
        btnReservar.setBackground(COLOR_AZUL);
        btnReservar.addActionListener(e -> mostrarDialogoReservarCita(v));

        panelBotones.add(btnFicha);
        panelBotones.add(btnReservar);

        card.add(panelBotones, BorderLayout.SOUTH);
        return card;
    }

    private JLabel crearBadgeEstado(String estado) {
        JLabel badge = new JLabel("  " + estado + "  ");
        badge.setOpaque(true);
        badge.setFont(new Font("Segoe UI", Font.BOLD, 11));

        Color bg;
        Color fg;

        if (estado.toLowerCase().contains("disponible")) {
            bg = new Color(220, 252, 231);
            fg = new Color(21, 128, 61);
        } else if (estado.toLowerCase().contains("consulta")) {
            bg = new Color(224, 242, 254);
            fg = new Color(3, 105, 161);
        } else if (estado.toLowerCase().contains("cirugía") || estado.toLowerCase().contains("cirugia")) {
            bg = new Color(254, 243, 199);
            fg = new Color(180, 83, 9);
        } else if (estado.toLowerCase().contains("tarde")) {
            bg = new Color(243, 232, 255);
            fg = new Color(126, 34, 206);
        } else {
            bg = new Color(241, 245, 249);
            fg = new Color(100, 116, 139);
        }

        badge.setBackground(bg);
        badge.setForeground(fg);
        badge.setBorder(new CompoundBorder(
                new LineBorder(bg.darker(), 1, true),
                new EmptyBorder(3, 8, 3, 8)
        ));
        return badge;
    }

    private Color obtenerColorAvatar(String especialidad) {
        String esp = especialidad.toLowerCase();
        if (esp.contains("cirug")) return new Color(225, 29, 72); // Rojo / Cirugía
        if (esp.contains("derm")) return new Color(13, 148, 136); // Teal / Derma
        if (esp.contains("exót") || esp.contains("exot")) return new Color(217, 119, 6); // Naranja / Exóticos
        if (esp.contains("cardio")) return new Color(239, 68, 68); // Rojo corazón
        if (esp.contains("felin")) return new Color(147, 51, 234); // Morado
        if (esp.contains("oftal")) return new Color(2, 132, 199); // Azul
        if (esp.contains("urgen")) return new Color(220, 38, 38); // Rojo intenso urgencias
        return COLOR_AZUL;
    }

    private String obtenerIniciales(String nombreCompleto) {
        if (nombreCompleto == null || nombreCompleto.isEmpty()) return "V";
        String limpio = nombreCompleto.replace("Dra.", "").replace("Dr.", "").trim();
        String[] partes = limpio.split("\\s+");
        if (partes.length >= 2) {
            return (partes[0].substring(0, 1) + partes[1].substring(0, 1)).toUpperCase();
        } else if (partes.length == 1 && partes[0].length() >= 2) {
            return partes[0].substring(0, 2).toUpperCase();
        }
        return "V";
    }

    // Modal de Ficha Completa del Veterinario
    private void mostrarFichaVeterinario(Veterinario v) {
        JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(this), "Ficha del Especialista: " + v.getNombreCompleto(), JDialog.ModalityType.APPLICATION_MODAL);
        dlg.setSize(640, 520);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());

        JPanel panel = new JPanel(new BorderLayout(16, 16));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 24, 20, 24));

        // Cabecera Ficha
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JLabel lblTit = new JLabel(v.getNombreCompleto());
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTit.setForeground(COLOR_TEXTO_TITULO);

        JLabel lblSub = new JLabel(v.getColegiaturaCMPV() + " · Especialista en " + v.getEspecialidad());
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(COLOR_TEXTO_MUTED);

        JPanel infoTop = new JPanel(new GridLayout(2, 1, 0, 4));
        infoTop.setOpaque(false);
        infoTop.add(lblTit);
        infoTop.add(lblSub);

        top.add(infoTop, BorderLayout.CENTER);
        top.add(crearBadgeEstado(v.getEstadoDisponibilidad()), BorderLayout.EAST);
        panel.add(top, BorderLayout.NORTH);

        // Contenido con campos
        JPanel campos = new JPanel(new GridLayout(6, 2, 12, 12));
        campos.setOpaque(false);
        campos.setBorder(new EmptyBorder(12, 0, 12, 0));

        campos.add(crearItemFicha("Años de Experiencia:", v.getAniosExperiencia() + " años colegiado"));
        campos.add(crearItemFicha("Calificación General:", "★ " + v.getCalificacionEstrellas() + " / 5.0"));
        campos.add(crearItemFicha("Consultorio Asignado:", v.getConsultorioHabitual()));
        campos.add(crearItemFicha("Atenciones / Cirugías:", v.getTotalAtencionesRealizadas() + " registradas"));
        campos.add(crearItemFicha("Días de Atención:", v.getDiasAtencion()));
        campos.add(crearItemFicha("Horario / Turno:", v.getHorarioTurno()));
        campos.add(crearItemFicha("Próxima Cita Libre:", v.getProximaCitaLibre()));
        campos.add(crearItemFicha("Teléfono Directo:", v.getTelefono()));
        campos.add(crearItemFicha("Correo Electrónico:", v.getEmail()));

        // Selector para cambiar disponibilidad en tiempo real
        JPanel boxCambio = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        boxCambio.setOpaque(false);
        JComboBox<String> cmbNuevoEstado = new JComboBox<>(new String[]{
                "Disponible hoy", "En consulta", "En cirugía", "Turno Tarde", "Guardia nocturna", "Descanso"
        });
        cmbNuevoEstado.setSelectedItem(v.getEstadoDisponibilidad());
        boxCambio.add(new JLabel("Estado actual:"));
        boxCambio.add(cmbNuevoEstado);
        campos.add(boxCambio);

        // Biografía completa abajo
        JPanel bioPanel = new JPanel(new BorderLayout(0, 6));
        bioPanel.setOpaque(false);
        JLabel lblBioTit = new JLabel("Perfil Profesional y Especialidades:");
        lblBioTit.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblBioTit.setForeground(COLOR_TEXTO_TITULO);

        JTextArea txtBioFull = new JTextArea(v.getDescripcionBio());
        txtBioFull.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtBioFull.setLineWrap(true);
        txtBioFull.setWrapStyleWord(true);
        txtBioFull.setEditable(false);
        txtBioFull.setBackground(Color.WHITE);
        txtBioFull.setBorder(new CompoundBorder(
                new LineBorder(COLOR_BORDE, 1, true),
                new EmptyBorder(8, 10, 8, 10)
        ));

        bioPanel.add(lblBioTit, BorderLayout.NORTH);
        bioPanel.add(txtBioFull, BorderLayout.CENTER);

        JPanel centro = new JPanel(new BorderLayout(0, 10));
        centro.setOpaque(false);
        centro.add(campos, BorderLayout.NORTH);
        centro.add(bioPanel, BorderLayout.CENTER);

        panel.add(centro, BorderLayout.CENTER);

        // Botones dialog
        JPanel bot = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        bot.setOpaque(false);

        JButton btnGuardarEstado = Ui.botonPrimario("Actualizar Disponibilidad", null);
        btnGuardarEstado.setBackground(COLOR_VERDE);
        btnGuardarEstado.addActionListener(e -> {
            String nuevo = (String) cmbNuevoEstado.getSelectedItem();
            repo.actualizarEstadoVeterinario(v.getId(), nuevo);
            JOptionPane.showMessageDialog(dlg, "Disponibilidad de " + v.getNombreCompleto() + " actualizada a: " + nuevo, "Estado Actualizado", JOptionPane.INFORMATION_MESSAGE);
            dlg.dispose();
            recargarDatos();
        });

        JButton btnCerrar = Ui.botonSecundario("Cerrar", null);
        btnCerrar.addActionListener(e -> dlg.dispose());

        bot.add(btnGuardarEstado);
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

    private void mostrarDialogoReservarCita(Veterinario v) {
        String msg = "Asignación rápida de consulta con " + v.getNombreCompleto() + "\n" +
                "Especialidad: " + v.getEspecialidad() + "\n" +
                "Consultorio: " + v.getConsultorioHabitual() + "\n" +
                "Próxima cita sugerida: " + v.getProximaCitaLibre() + "\n\n" +
                "¿Desea agendar una cita o derivar paciente a este especialista?";

        int resp = JOptionPane.showConfirmDialog(this, msg, "Reservar Cita Médica", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (resp == JOptionPane.YES_OPTION) {
            JOptionPane.showMessageDialog(this, "Redirigiendo a Agendamiento de Citas con el especialista " + v.getNombreCompleto() + " preseleccionado.", "Reserva en Proceso", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void mostrarDialogoNuevoVeterinario() {
        JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(this), "Registrar Nuevo Especialista Veterinario", JDialog.ModalityType.APPLICATION_MODAL);
        dlg.setSize(560, 580);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());

        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(new EmptyBorder(20, 24, 20, 24));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        JTextField txtNom = new JTextField(20);
        JTextField txtCol = new JTextField(15);
        JComboBox<String> cmbEsp = new JComboBox<>(new String[]{
                "Cirugía General y Tejidos Blandos", "Dermatología y Alergias",
                "Animales Exóticos y Aves", "Cardiología y Ecocardiografía",
                "Medicina Felina y Comportamiento", "Oftalmología Veterinaria",
                "Urgencias y Cuidados Críticos", "Oncología y Quimioterapia"
        });
        JTextField txtExp = new JTextField("5", 6);
        JTextField txtDias = new JTextField("Lun · Mié · Vie", 15);
        JTextField txtTurno = new JTextField("08:00 - 15:00", 15);
        JTextField txtCons = new JTextField("Consultorio 1", 15);
        JTextField txtTel = new JTextField("+51 987 654 321", 15);
        JTextField txtEmail = new JTextField("especialista@happypets.pe", 20);
        JTextArea txtBio = new JTextArea(3, 20);
        txtBio.setLineWrap(true);
        txtBio.setWrapStyleWord(true);
        txtBio.setBorder(new LineBorder(COLOR_BORDE, 1));

        p.add(new JLabel("Nombre Completo:"), gbc);
        gbc.gridx = 1; p.add(txtNom, gbc);
        gbc.gridx = 0; gbc.gridy++; p.add(new JLabel("Colegiatura CMPV:"), gbc);
        gbc.gridx = 1; p.add(txtCol, gbc);
        gbc.gridx = 0; gbc.gridy++; p.add(new JLabel("Especialidad:"), gbc);
        gbc.gridx = 1; p.add(cmbEsp, gbc);
        gbc.gridx = 0; gbc.gridy++; p.add(new JLabel("Años de Experiencia:"), gbc);
        gbc.gridx = 1; p.add(txtExp, gbc);
        gbc.gridx = 0; gbc.gridy++; p.add(new JLabel("Días de Atención:"), gbc);
        gbc.gridx = 1; p.add(txtDias, gbc);
        gbc.gridx = 0; gbc.gridy++; p.add(new JLabel("Horario habitual:"), gbc);
        gbc.gridx = 1; p.add(txtTurno, gbc);
        gbc.gridx = 0; gbc.gridy++; p.add(new JLabel("Consultorio / Quirófano:"), gbc);
        gbc.gridx = 1; p.add(txtCons, gbc);
        gbc.gridx = 0; gbc.gridy++; p.add(new JLabel("Teléfono Móvil:"), gbc);
        gbc.gridx = 1; p.add(txtTel, gbc);
        gbc.gridx = 0; gbc.gridy++; p.add(new JLabel("Correo Institucional:"), gbc);
        gbc.gridx = 1; p.add(txtEmail, gbc);
        gbc.gridx = 0; gbc.gridy++; p.add(new JLabel("Breve Perfil / Bio:"), gbc);
        gbc.gridx = 1; p.add(new JScrollPane(txtBio), gbc);

        JPanel panelBot = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        panelBot.setBackground(Color.WHITE);

        JButton btnGuardar = Ui.botonPrimario("Guardar Especialista", null);
        btnGuardar.setBackground(COLOR_MORADO);
        btnGuardar.addActionListener(e -> {
            String nom = txtNom.getText().trim();
            if (nom.isEmpty()) {
                JOptionPane.showMessageDialog(dlg, "Ingrese el nombre del especialista.", "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int exp = 5;
            try { exp = Integer.parseInt(txtExp.getText().trim()); } catch (Exception ignored) {}

            Veterinario nuevo = new Veterinario(
                    "VET-" + String.format("%02d", repo.getVeterinarios().size() + 1),
                    nom,
                    txtCol.getText().trim().isEmpty() ? "Col. N° 28/0000" : txtCol.getText().trim(),
                    (String) cmbEsp.getSelectedItem(),
                    exp,
                    txtBio.getText().trim().isEmpty() ? "Especialista clínico certificado." : txtBio.getText().trim(),
                    txtDias.getText().trim(),
                    txtTurno.getText().trim(),
                    "Hoy, 18:00 hrs",
                    "Disponible hoy",
                    txtTel.getText().trim(),
                    txtEmail.getText().trim(),
                    5.0,
                    0,
                    txtCons.getText().trim()
            );

            repo.guardarVeterinario(nuevo);
            JOptionPane.showMessageDialog(dlg, "Especialista " + nom + " registrado correctamente en el directorio.", "Registro Exitoso", JOptionPane.INFORMATION_MESSAGE);
            dlg.dispose();
            recargarDatos();
        });

        JButton btnCancelar = Ui.botonSecundario("Cancelar", null);
        btnCancelar.addActionListener(e -> dlg.dispose());

        panelBot.add(btnGuardar);
        panelBot.add(btnCancelar);

        dlg.add(p, BorderLayout.CENTER);
        dlg.add(panelBot, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }
}
