package happypets.modulos.modulo8;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import happypets.ui.Iconos;
import happypets.ui.Ui;

/**
 * Ventana independiente para el Módulo 8: Inteligencia de Negocios y Reportes.
 * Responsable Asignado: Arroyo Preciado, Harry Martin
 * Contiene los 4 submódulos oficiales del software según los wireframes:
 *  1. Tableros de Mando (Dashboards ejecutivos, KPIs en vivo y citas de hoy)
 *  2. Reportes Clínicos (Diagnósticos más comunes, carga médica y fichas clínicas)
 *  3. Reportes Financieros (EBITDA, rendimiento por prestación y balance contable)
 *  4. Exportador de Datos (Extractor modular, segmentación e historial de descargas)
 */
public class Modulo8ReportesBIFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    private final VistaTablerosMandoPanel vistaTablerosMando;
    private final VistaReportesClinicosPanel vistaReportesClinicos;
    private final VistaReportesFinancierosPanel vistaReportesFinancieros;
    private final VistaExportadorDatosPanel vistaExportadorDatos;
    private final JTabbedPane tabs;

    public Modulo8ReportesBIFrame() {
        this(0);
    }

    public Modulo8ReportesBIFrame(int pestanaInicial) {
        setTitle("Happy Pets - Módulo 8: Inteligencia de Negocios y Reportes (BI)");
        setIconImage(Ui.icono());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1360, 860);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(248, 250, 252));

        vistaTablerosMando = new VistaTablerosMandoPanel();
        vistaReportesClinicos = new VistaReportesClinicosPanel();
        vistaReportesFinancieros = new VistaReportesFinancierosPanel(() -> seleccionarPestana(3));
        vistaExportadorDatos = new VistaExportadorDatosPanel();

        tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabs.setBackground(Color.WHITE);

        tabs.addTab("Tableros de Mando (Dashboards)",
                Iconos.crearIconoReportes(16, new Color(0, 115, 125)),
                vistaTablerosMando);

        tabs.addTab("Reportes Clínicos",
                Iconos.crearIconoDoctor(16, new Color(13, 148, 136)),
                vistaReportesClinicos);

        tabs.addTab("Reportes Financieros",
                Iconos.crearIconoPOS(16, new Color(16, 185, 129)),
                vistaReportesFinancieros);

        tabs.addTab("Exportador de Datos",
                Iconos.crearIconoExportar(16, new Color(2, 132, 199)),
                vistaExportadorDatos);

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
        if (s.contains("tablero") || s.contains("dashboard") || s.contains("mando") || s.contains("kpi")) {
            seleccionarPestana(0);
        } else if (s.contains("clínic") || s.contains("clinic") || s.contains("diagnóstic") || s.contains("atenci")) {
            seleccionarPestana(1);
        } else if (s.contains("financ") || s.contains("ebitda") || s.contains("ingreso") || s.contains("balance")) {
            seleccionarPestana(2);
        } else if (s.contains("export") || s.contains("dato") || s.contains("excel") || s.contains("csv")) {
            seleccionarPestana(3);
        }
        setVisible(true);
        toFront();
    }

    public VistaTablerosMandoPanel getVistaTablerosMando() {
        return vistaTablerosMando;
    }

    public VistaReportesClinicosPanel getVistaReportesClinicos() {
        return vistaReportesClinicos;
    }

    public VistaReportesFinancierosPanel getVistaReportesFinancieros() {
        return vistaReportesFinancieros;
    }

    public VistaExportadorDatosPanel getVistaExportadorDatos() {
        return vistaExportadorDatos;
    }

    public static void main(String[] args) {
        Ui.instalarApariencia();
        javax.swing.SwingUtilities.invokeLater(() -> new Modulo8ReportesBIFrame().setVisible(true));
    }
}
