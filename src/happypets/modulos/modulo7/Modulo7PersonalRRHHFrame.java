package happypets.modulos.modulo7;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import happypets.ui.Iconos;
import happypets.ui.Ui;

/**
 * Ventana independiente para el Módulo 7: Personal y Recursos Humanos.
 * Responsable Asignado: Loli Espinoza, Víctor Manuel
 * Contiene los 4 submódulos oficiales del software según los wireframes:
 *  1. Veterinarios (Directorio de especialistas clínicos colegiados)
 *  2. Personal de Apoyo (ATV auxiliares, recepción, estética, administración, mantenimiento)
 *  3. Horarios y Turnos (Cuadrante semanal de guardias y asignación de turnos médicos)
 *  4. Asistencias y Permisos (Pase de lista diario por turnos, retardos y solicitudes)
 */
public class Modulo7PersonalRRHHFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    private final VistaVeterinariosPanel vistaVeterinarios;
    private final VistaPersonalApoyoPanel vistaPersonalApoyo;
    private final VistaHorariosTurnosPanel vistaHorariosTurnos;
    private final VistaAsistenciasPermisosPanel vistaAsistenciasPermisos;
    private final JTabbedPane tabs;

    public Modulo7PersonalRRHHFrame() {
        this(0);
    }

    public Modulo7PersonalRRHHFrame(int pestanaInicial) {
        setTitle("Happy Pets - Módulo 7: Personal y Recursos Humanos (RRHH)");
        setIconImage(Ui.icono());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1340, 850);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(248, 250, 252));

        vistaVeterinarios = new VistaVeterinariosPanel();
        vistaPersonalApoyo = new VistaPersonalApoyoPanel();
        vistaHorariosTurnos = new VistaHorariosTurnosPanel();
        vistaAsistenciasPermisos = new VistaAsistenciasPermisosPanel();

        tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabs.setBackground(Color.WHITE);

        tabs.addTab("Nuestros Veterinarios",
                Iconos.crearIconoDoctor(16, Ui.TURQUESA_PROFUNDO),
                vistaVeterinarios);

        tabs.addTab("Personal de Apoyo",
                Iconos.crearIconoUsuario(16, Ui.TURQUESA),
                vistaPersonalApoyo);

        tabs.addTab("Horarios y Turnos Médicos",
                Iconos.crearIconoTurno(16, Ui.TURQUESA_OSCURO),
                vistaHorariosTurnos);

        tabs.addTab("Asistencias y Permisos",
                Iconos.crearIconoAsistencia(16, Ui.TURQUESA_PROFUNDO),
                vistaAsistenciasPermisos);

        if (pestanaInicial >= 0 && pestanaInicial < tabs.getTabCount()) {
            tabs.setSelectedIndex(pestanaInicial);
        }

        add(tabs, BorderLayout.CENTER);
    }

    public void seleccionarPestana(int idx) {
        if (idx >= 0 && idx < tabs.getTabCount()) {
            tabs.setSelectedIndex(idx);
        }
    }

    public void abrirSubmodulo(String nombreSub) {
        if (nombreSub == null) return;
        String s = nombreSub.toLowerCase();
        if (s.contains("vet") || s.contains("especialista") || s.contains("médico")) {
            seleccionarPestana(0);
        } else if (s.contains("apoyo") || s.contains("personal") || s.contains("auxiliar") || s.contains("recepci")) {
            seleccionarPestana(1);
        } else if (s.contains("horario") || s.contains("turno") || s.contains("cuadrante") || s.contains("guardia")) {
            seleccionarPestana(2);
        } else if (s.contains("asistencia") || s.contains("permiso") || s.contains("pase") || s.contains("lista")) {
            seleccionarPestana(3);
        }
        setVisible(true);
        toFront();
    }

    public VistaVeterinariosPanel getVistaVeterinarios() {
        return vistaVeterinarios;
    }

    public VistaPersonalApoyoPanel getVistaPersonalApoyo() {
        return vistaPersonalApoyo;
    }

    public VistaHorariosTurnosPanel getVistaHorariosTurnos() {
        return vistaHorariosTurnos;
    }

    public VistaAsistenciasPermisosPanel getVistaAsistenciasPermisos() {
        return vistaAsistenciasPermisos;
    }

    public static void main(String[] args) {
        Ui.instalarApariencia();
        javax.swing.SwingUtilities.invokeLater(() -> new Modulo7PersonalRRHHFrame().setVisible(true));
    }
}
