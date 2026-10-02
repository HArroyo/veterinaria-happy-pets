package happypets.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import happypets.data.RepositorioVeterinaria;
import happypets.model.Cliente;
import happypets.model.ConsultaClinica;
import happypets.model.Mascota;

/**
 * Pantalla 2: Historial Clínico de Mascotas.
 * Consulta de visitas y evolución clínica por paciente.
 * Diseñada en estricta fidelidad con el Wireframe oficial.
 */
public class HistorialClinicoFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    private final RepositorioVeterinaria repo = RepositorioVeterinaria.getInstancia();
    private Mascota mascotaActual;
    private Cliente clienteActual;
    private List<ConsultaClinica> listaConsultas;

    // Filtros
    private JTextField txtBuscarMascota;
    private JComboBox<String> cbPeriodo;

    // Resumen paciente
    private JLabel valMascota;
    private JLabel valCliente;
    private JLabel valEspecieRaza;
    private JLabel valUltimaConsulta;
    private JLabel valTotalConsultas;

    // Tabla de historial
    private JLabel lblContadorConsultas;
    private JTable tablaConsultas;
    private DefaultTableModel modeloConsultas;

    public HistorialClinicoFrame() {
        Ui.configurarVentana(this, "Historial Clínico");
        setLayout(new BorderLayout());

        // Cabecera institucional
        add(Ui.crearCabeceraSistema(), BorderLayout.NORTH);

        // Contenedor principal
        JPanel panelCuerpo = new JPanel(new BorderLayout(0, 14));
        panelCuerpo.setBackground(Ui.FONDO);
        panelCuerpo.setBorder(new EmptyBorder(16, 20, 16, 20));

        // Subcabecera del módulo
        panelCuerpo.add(Ui.crearCabeceraModulo("Historial Clínico de Mascotas", "Consulta de visitas y evolución por mascota"), BorderLayout.NORTH);

        // Panel Central: Filtros + Resumen Paciente + Tabla Historial
        JPanel panelCentral = new JPanel();
        panelCentral.setLayout(new BoxLayout(panelCentral, BoxLayout.Y_AXIS));
        panelCentral.setOpaque(false);

        panelCentral.add(crearBarraFiltros());
        panelCentral.add(Box.createVerticalStrut(14));
        panelCentral.add(crearFilaResumenPaciente());
        panelCentral.add(Box.createVerticalStrut(14));
        panelCentral.add(crearTarjetaHistorial());

        panelCuerpo.add(panelCentral, BorderLayout.CENTER);

        // Pie de página con botones Imprimir historial y Exportar PDF
        JButton btnImprimir = Ui.boton("Imprimir historial", false);
        JButton btnExportarPdf = Ui.boton("Exportar PDF", true);

        btnImprimir.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Enviando historial clínico de " + (mascotaActual != null ? mascotaActual.getNombre() : "") + " a la cola de impresión.",
                "Impresión", JOptionPane.INFORMATION_MESSAGE));

        btnExportarPdf.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Historial clínico generado y exportado exitosamente a PDF.",
                "Exportación Exitosa", JOptionPane.INFORMATION_MESSAGE));

        panelCuerpo.add(Ui.crearPieModulo("Historial Clínico", btnImprimir, btnExportarPdf), BorderLayout.SOUTH);

        add(panelCuerpo, BorderLayout.CENTER);

        // Cargar paciente por defecto del wireframe: Rocky (VET-0091)
        Optional<Mascota> optRocky = repo.buscarMascotaPorCodigoONombre("VET-0091");
        if (optRocky.isPresent()) {
            cargarMascota(optRocky.get());
        } else {
            List<Mascota> todas = repo.todasLasMascotas();
            if (!todas.isEmpty()) cargarMascota(todas.get(0));
        }
    }

    private JPanel crearBarraFiltros() {
        JPanel barra = new JPanel(new BorderLayout(14, 0));
        barra.setBackground(Color.WHITE);
        barra.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Ui.BORDE_SUAVE, 1),
                new EmptyBorder(8, 14, 8, 14)
        ));

        JPanel izq = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        izq.setOpaque(false);

        JLabel lblBuscar = new JLabel("Buscar mascota:");
        lblBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblBuscar.setForeground(new Color(60, 60, 60));
        izq.add(lblBuscar);

        txtBuscarMascota = Ui.campoTexto(20);
        txtBuscarMascota.setToolTipText("Nombre o código de mascota...");
        izq.add(txtBuscarMascota);

        JButton btnBuscar = Ui.boton("Buscar", true);
        btnBuscar.addActionListener(e -> buscarMascota());
        txtBuscarMascota.addActionListener(e -> buscarMascota());
        izq.add(btnBuscar);

        izq.add(Box.createHorizontalStrut(14));

        JLabel lblPeriodo = new JLabel("Período:");
        lblPeriodo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblPeriodo.setForeground(new Color(60, 60, 60));
        izq.add(lblPeriodo);

        cbPeriodo = new JComboBox<>(new String[] {"Todas las fechas", "Último mes", "Últimos 6 meses", "Último año", "Últimos 3 años"});
        cbPeriodo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cbPeriodo.addActionListener(e -> filtrarPorPeriodo());
        izq.add(cbPeriodo);

        barra.add(izq, BorderLayout.WEST);

        JLabel lblConsulta = new JLabel("Consulta del historial");
        lblConsulta.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblConsulta.setForeground(Ui.TURQUESA_OSCURO);
        barra.add(lblConsulta, BorderLayout.EAST);

        return barra;
    }

    private JPanel crearFilaResumenPaciente() {
        JPanel panel = new JPanel(new GridLayout(1, 5, 14, 0));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Ui.BORDE_SUAVE, 1),
                new EmptyBorder(12, 16, 12, 16)
        ));

        valMascota = new JLabel("Rocky (VET-0091)");
        valCliente = new JLabel("Carlos Eduardo Morales Soto");
        valEspecieRaza = new JLabel("Canino · Golden Retriever");
        valUltimaConsulta = new JLabel("14/06/2024");
        valTotalConsultas = new JLabel("3");

        panel.add(crearCajaInfo("Mascota seleccionada", valMascota));
        panel.add(crearCajaInfo("Cliente responsable", valCliente));
        panel.add(crearCajaInfo("Especie y raza", valEspecieRaza));
        panel.add(crearCajaInfo("Última consulta", valUltimaConsulta));
        panel.add(crearCajaInfo("Consultas registradas", valTotalConsultas));

        return panel;
    }

    private JPanel crearCajaInfo(String titulo, JLabel valor) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

        JLabel t = new JLabel(titulo);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        t.setForeground(new Color(110, 115, 120));

        valor.setFont(new Font("Segoe UI", Font.BOLD, 13));
        valor.setForeground(new Color(30, 30, 30));

        p.add(t);
        p.add(Box.createVerticalStrut(3));
        p.add(valor);
        return p;
    }

    private JPanel crearTarjetaHistorial() {
        JPanel tarjeta = Ui.crearTarjeta();
        tarjeta.setLayout(new BorderLayout());

        // Header: "Consultas de la mascota" (izq) y "3 registros" (der)
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Ui.TURQUESA);
        header.setBorder(new EmptyBorder(8, 14, 8, 14));

        JLabel title = new JLabel("Consultas de la mascota");
        title.setFont(new Font("Segoe UI", Font.BOLD, 13));
        title.setForeground(Color.WHITE);
        header.add(title, BorderLayout.WEST);

        lblContadorConsultas = new JLabel("3 registros");
        lblContadorConsultas.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblContadorConsultas.setForeground(Color.WHITE);
        header.add(lblContadorConsultas, BorderLayout.EAST);

        tarjeta.add(header, BorderLayout.NORTH);

        // Subheader informativo: "Consultas ordenadas por fecha de atención. Selecciona «Ver detalle» para revisar una visita."
        JPanel panelCuerpoTarjeta = new JPanel(new BorderLayout(0, 8));
        panelCuerpoTarjeta.setOpaque(false);
        panelCuerpoTarjeta.setBorder(new EmptyBorder(10, 14, 12, 14));

        JLabel lblInstruccion = new JLabel("Consultas ordenadas por fecha de atención. Selecciona «Ver detalle» para revisar una visita.");
        lblInstruccion.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblInstruccion.setForeground(new Color(100, 105, 110));
        panelCuerpoTarjeta.add(lblInstruccion, BorderLayout.NORTH);

        // Tabla de consultas
        String[] columnas = {"N.º", "Fecha", "Motivo", "Diagnóstico", "Tratamiento", "Veterinario", "Estado", "Acción"};
        modeloConsultas = new DefaultTableModel(columnas, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int row, int col) {
                return col == 7; // Solo la columna Acción es interactiva
            }
        };
        tablaConsultas = new JTable(modeloConsultas);
        Ui.formatearTabla(tablaConsultas);

        // Configuración de anchos de columna
        tablaConsultas.getColumnModel().getColumn(0).setPreferredWidth(80);
        tablaConsultas.getColumnModel().getColumn(1).setPreferredWidth(95);
        tablaConsultas.getColumnModel().getColumn(2).setPreferredWidth(140);
        tablaConsultas.getColumnModel().getColumn(3).setPreferredWidth(160);
        tablaConsultas.getColumnModel().getColumn(4).setPreferredWidth(180);
        tablaConsultas.getColumnModel().getColumn(5).setPreferredWidth(130);
        tablaConsultas.getColumnModel().getColumn(6).setPreferredWidth(90);
        tablaConsultas.getColumnModel().getColumn(7).setPreferredWidth(110);

        // Instalación del botón 'Ver detalle' en la columna 7
        Ui.instalarBotonColumna(tablaConsultas, 7, "Ver detalle", fila -> {
            if (listaConsultas != null && fila >= 0 && fila < listaConsultas.size()) {
                ConsultaClinica cc = listaConsultas.get(fila);
                DetalleConsultaDialog dlg = new DetalleConsultaDialog(this, cc, mascotaActual);
                dlg.setVisible(true);
            }
        });

        JScrollPane scroll = new JScrollPane(tablaConsultas);
        scroll.setBorder(BorderFactory.createLineBorder(Ui.BORDE_SUAVE, 1));
        panelCuerpoTarjeta.add(scroll, BorderLayout.CENTER);

        tarjeta.add(panelCuerpoTarjeta, BorderLayout.CENTER);
        return tarjeta;
    }

    public void cargarMascota(Mascota mascota) {
        this.mascotaActual = mascota;
        if (mascota == null) {
            valMascota.setText("-");
            valCliente.setText("-");
            valEspecieRaza.setText("-");
            valUltimaConsulta.setText("-");
            valTotalConsultas.setText("0");
            lblContadorConsultas.setText("0 registros");
            modeloConsultas.setRowCount(0);
            return;
        }

        // Buscar cliente responsable
        Optional<Cliente> optCliente = repo.getClienteDeMascota(mascota.getCodigo());
        this.clienteActual = optCliente.orElse(null);

        valMascota.setText(mascota.getNombre() + " (" + mascota.getCodigo() + ")");
        valCliente.setText(clienteActual != null ? clienteActual.getNombreCompleto() : "-");
        valEspecieRaza.setText(mascota.getEspecie() + " · " + mascota.getRaza());

        actualizarConsultas();
    }

    private void actualizarConsultas() {
        if (mascotaActual == null) return;
        listaConsultas = repo.getConsultasPorMascota(mascotaActual.getCodigo());

        modeloConsultas.setRowCount(0);
        for (ConsultaClinica cc : listaConsultas) {
            modeloConsultas.addRow(new Object[]{
                    cc.getCodigo(),
                    cc.getFechaFormateada(),
                    cc.getMotivo(),
                    cc.getDiagnostico(),
                    cc.getTratamiento(),
                    cc.getVeterinario(),
                    cc.getEstado(),
                    "Ver detalle"
            });
        }

        int total = listaConsultas.size();
        valTotalConsultas.setText(String.valueOf(total));
        lblContadorConsultas.setText(total + " registros");

        if (!listaConsultas.isEmpty()) {
            valUltimaConsulta.setText(listaConsultas.get(0).getFechaFormateada());
        } else {
            valUltimaConsulta.setText("-");
        }
    }

    private void buscarMascota() {
        String termino = txtBuscarMascota.getText().trim();
        if (termino.isEmpty()) {
            mostrarSelectorMascotas();
            return;
        }

        Optional<Mascota> opt = repo.buscarMascotaPorCodigoONombre(termino);
        if (opt.isPresent()) {
            cargarMascota(opt.get());
        } else {
            JOptionPane.showMessageDialog(this, "No se encontró mascota con: " + termino, "Sin resultados", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void mostrarSelectorMascotas() {
        List<Mascota> mascotas = repo.todasLasMascotas();
        String[] cols = {"Código", "Nombre", "Especie", "Raza", "Cliente Responsable"};
        Object[][] data = new Object[mascotas.size()][5];
        for (int i = 0; i < mascotas.size(); i++) {
            Mascota m = mascotas.get(i);
            Optional<Cliente> c = repo.getClienteDeMascota(m.getCodigo());
            data[i][0] = m.getCodigo();
            data[i][1] = m.getNombre();
            data[i][2] = m.getEspecie();
            data[i][3] = m.getRaza();
            data[i][4] = c.map(Cliente::getNombreCompleto).orElse("-");
        }

        JTable table = new JTable(new DefaultTableModel(data, cols));
        Ui.formatearTabla(table);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setPreferredSize(new Dimension(650, 240));

        int res = JOptionPane.showConfirmDialog(this, scroll, "Seleccionar Mascota para Historial", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res == JOptionPane.OK_OPTION) {
            int row = table.getSelectedRow();
            if (row >= 0) {
                cargarMascota(mascotas.get(row));
            }
        }
    }

    private void filtrarPorPeriodo() {
        if (mascotaActual == null) return;
        String sel = (String) cbPeriodo.getSelectedItem();
        List<ConsultaClinica> todas = repo.getConsultasPorMascota(mascotaActual.getCodigo());
        LocalDate ahora = LocalDate.now();

        listaConsultas = todas.stream().filter(cc -> {
            if ("Todas las fechas".equalsIgnoreCase(sel)) return true;
            if (cc.getFecha() == null) return false;
            if ("Último mes".equalsIgnoreCase(sel)) {
                return cc.getFecha().isAfter(ahora.minusMonths(1));
            } else if ("Últimos 6 meses".equalsIgnoreCase(sel)) {
                return cc.getFecha().isAfter(ahora.minusMonths(6));
            } else if ("Último año".equalsIgnoreCase(sel)) {
                return cc.getFecha().isAfter(ahora.minusYears(1));
            } else if ("Últimos 3 años".equalsIgnoreCase(sel)) {
                return cc.getFecha().isAfter(ahora.minusYears(3));
            }
            return true;
        }).toList();

        modeloConsultas.setRowCount(0);
        for (ConsultaClinica cc : listaConsultas) {
            modeloConsultas.addRow(new Object[]{
                    cc.getCodigo(),
                    cc.getFechaFormateada(),
                    cc.getMotivo(),
                    cc.getDiagnostico(),
                    cc.getTratamiento(),
                    cc.getVeterinario(),
                    cc.getEstado(),
                    "Ver detalle"
            });
        }
        lblContadorConsultas.setText(listaConsultas.size() + " registros");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Ui.instalarApariencia();
            new HistorialClinicoFrame().setVisible(true);
        });
    }
}
