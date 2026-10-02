package happypets.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
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
import happypets.model.DocumentoMascota;
import happypets.model.Mascota;

/**
 * Pantalla 3: Constancias y Certificados.
 * Consulta y descarga de documentos veterinarios disponibles por mascota.
 * Diseñada en estricta fidelidad con el Wireframe oficial.
 */
public class ConstanciasCertificadosFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    private final RepositorioVeterinaria repo = RepositorioVeterinaria.getInstancia();
    private Mascota mascotaActual;
    private Cliente clienteActual;
    private List<DocumentoMascota> listaDocumentos;

    // Filtros
    private JTextField txtBuscar;
    private JComboBox<String> cbTipoDocumento;

    // Resumen
    private JLabel valMascota;
    private JLabel valCliente;
    private JLabel valEspecieRaza;
    private JLabel valTotalDocumentos;

    // Tabla de documentos
    private JTable tablaDocumentos;
    private DefaultTableModel modeloDocumentos;

    public ConstanciasCertificadosFrame() {
        Ui.configurarVentana(this, "Constancias y Certificados");
        setLayout(new BorderLayout());

        // Cabecera institucional
        add(Ui.crearCabeceraSistema(), BorderLayout.NORTH);

        // Contenedor principal
        JPanel panelCuerpo = new JPanel(new BorderLayout(0, 14));
        panelCuerpo.setBackground(Ui.FONDO);
        panelCuerpo.setBorder(new EmptyBorder(16, 20, 16, 20));

        // Subcabecera del módulo
        panelCuerpo.add(Ui.crearCabeceraModulo("Constancias y Certificados", "Consulta de documentos disponibles por mascota"), BorderLayout.NORTH);

        // Panel Central: Filtros + Resumen + Tabla Documentos
        JPanel panelCentral = new JPanel();
        panelCentral.setLayout(new BoxLayout(panelCentral, BoxLayout.Y_AXIS));
        panelCentral.setOpaque(false);

        panelCentral.add(crearBarraFiltros());
        panelCentral.add(Box.createVerticalStrut(14));
        panelCentral.add(crearFilaResumen());
        panelCentral.add(Box.createVerticalStrut(14));
        panelCentral.add(crearTarjetaDocumentos());

        panelCuerpo.add(panelCentral, BorderLayout.CENTER);

        // Pie de página con botón 'Imprimir listado'
        JButton btnImprimirListado = Ui.boton("Imprimir listado", true);
        btnImprimirListado.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Enviando relación de constancias y certificados a la impresora predeterminada.",
                "Imprimir Listado", JOptionPane.INFORMATION_MESSAGE));

        panelCuerpo.add(Ui.crearPieModulo("Constancias y Certificados", btnImprimirListado), BorderLayout.SOUTH);

        add(panelCuerpo, BorderLayout.CENTER);

        // Cargar por defecto la mascota del wireframe: Rocky (VET-0091)
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

        JLabel lblBuscar = new JLabel("Buscar cliente o mascota:");
        lblBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblBuscar.setForeground(new Color(60, 60, 60));
        izq.add(lblBuscar);

        txtBuscar = Ui.campoTexto(20);
        txtBuscar.setToolTipText("Nombre o código...");
        izq.add(txtBuscar);

        JButton btnBuscar = Ui.boton("Buscar", true);
        btnBuscar.addActionListener(e -> buscarClienteOMascota());
        txtBuscar.addActionListener(e -> buscarClienteOMascota());
        izq.add(btnBuscar);

        izq.add(Box.createHorizontalStrut(14));

        JLabel lblTipo = new JLabel("Tipo de documento:");
        lblTipo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblTipo.setForeground(new Color(60, 60, 60));
        izq.add(lblTipo);

        cbTipoDocumento = new JComboBox<>(new String[] {
                "Todos",
                "Tarjeta de vacunación",
                "Historial de recetas médicas",
                "Historial clínico",
                "Certificado de vacunación"
        });
        cbTipoDocumento.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cbTipoDocumento.addActionListener(e -> filtrarPorTipo());
        izq.add(cbTipoDocumento);

        barra.add(izq, BorderLayout.WEST);

        JLabel lblDocDisp = new JLabel("Documentos disponibles");
        lblDocDisp.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblDocDisp.setForeground(Ui.TURQUESA_OSCURO);
        barra.add(lblDocDisp, BorderLayout.EAST);

        return barra;
    }

    private JPanel crearFilaResumen() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 16, 0));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Ui.BORDE_SUAVE, 1),
                new EmptyBorder(12, 16, 12, 16)
        ));

        valMascota = new JLabel("Rocky (VET-0091)");
        valCliente = new JLabel("Carlos Eduardo Morales Soto");
        valEspecieRaza = new JLabel("Canino · Golden Retriever");
        valTotalDocumentos = new JLabel("4");

        panel.add(crearCajaInfo("Mascota seleccionada", valMascota));
        panel.add(crearCajaInfo("Cliente responsable", valCliente));
        panel.add(crearCajaInfo("Especie y raza", valEspecieRaza));
        panel.add(crearCajaInfo("Documentos disponibles", valTotalDocumentos));

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

    private JPanel crearTarjetaDocumentos() {
        JPanel tarjeta = Ui.crearTarjeta();
        tarjeta.setLayout(new BorderLayout());

        // Header: "Documentos disponibles de la mascota" (izq) y "Consulta y descarga" (der)
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Ui.TURQUESA);
        header.setBorder(new EmptyBorder(8, 14, 8, 14));

        JLabel title = new JLabel("Documentos disponibles de la mascota");
        title.setFont(new Font("Segoe UI", Font.BOLD, 13));
        title.setForeground(Color.WHITE);
        header.add(title, BorderLayout.WEST);

        JLabel rightBadge = new JLabel("Consulta y descarga");
        rightBadge.setFont(new Font("Segoe UI", Font.BOLD, 12));
        rightBadge.setForeground(Color.WHITE);
        header.add(rightBadge, BorderLayout.EAST);

        tarjeta.add(header, BorderLayout.NORTH);

        // Subheader y tabla
        JPanel panelCuerpoTarjeta = new JPanel(new BorderLayout(0, 8));
        panelCuerpoTarjeta.setOpaque(false);
        panelCuerpoTarjeta.setBorder(new EmptyBorder(10, 14, 12, 14));

        JLabel lblInstruccion = new JLabel("Consulta los documentos disponibles para la mascota seleccionada.");
        lblInstruccion.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblInstruccion.setForeground(new Color(100, 105, 110));
        panelCuerpoTarjeta.add(lblInstruccion, BorderLayout.NORTH);

        // Columnas exactas del Wireframe:
        // Documento | Descripción | Última actualización | Disponibilidad | Acciones
        String[] columnas = {"Documento", "Descripción", "Última actualización", "Disponibilidad", "Acciones"};
        modeloDocumentos = new DefaultTableModel(columnas, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int row, int col) {
                return col == 4; // Solo la columna de Acciones es interactiva
            }
        };
        tablaDocumentos = new JTable(modeloDocumentos);
        Ui.formatearTabla(tablaDocumentos);

        tablaDocumentos.setRowHeight(36);
        tablaDocumentos.getColumnModel().getColumn(0).setPreferredWidth(210);
        tablaDocumentos.getColumnModel().getColumn(1).setPreferredWidth(340);
        tablaDocumentos.getColumnModel().getColumn(2).setPreferredWidth(140);
        tablaDocumentos.getColumnModel().getColumn(3).setPreferredWidth(120);
        tablaDocumentos.getColumnModel().getColumn(4).setPreferredWidth(200);

        // Instalar columna con dos botones: [Ver] y [Descargar PDF]
        Ui.instalarDobleBotonColumna(
                tablaDocumentos,
                4,
                fila -> { // Acción Ver
                    if (listaDocumentos != null && fila >= 0 && fila < listaDocumentos.size()) {
                        DocumentoMascota doc = listaDocumentos.get(fila);
                        DetalleDocumentoDialog dlg = new DetalleDocumentoDialog(this, doc, mascotaActual, clienteActual);
                        dlg.setVisible(true);
                    }
                },
                fila -> { // Acción Descargar PDF
                    if (listaDocumentos != null && fila >= 0 && fila < listaDocumentos.size()) {
                        DocumentoMascota doc = listaDocumentos.get(fila);
                        DetalleDocumentoDialog.descargarPdf(doc);
                    }
                }
        );

        JScrollPane scroll = new JScrollPane(tablaDocumentos);
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
            valTotalDocumentos.setText("0");
            modeloDocumentos.setRowCount(0);
            return;
        }

        Optional<Cliente> optCliente = repo.getClienteDeMascota(mascota.getCodigo());
        this.clienteActual = optCliente.orElse(null);

        valMascota.setText(mascota.getNombre() + " (" + mascota.getCodigo() + ")");
        valCliente.setText(clienteActual != null ? clienteActual.getNombreCompleto() : "-");
        valEspecieRaza.setText(mascota.getEspecie() + " · " + mascota.getRaza());

        actualizarDocumentos();
    }

    private void actualizarDocumentos() {
        if (mascotaActual == null) return;
        listaDocumentos = repo.getDocumentosPorMascota(mascotaActual.getCodigo());

        modeloDocumentos.setRowCount(0);
        for (DocumentoMascota doc : listaDocumentos) {
            modeloDocumentos.addRow(new Object[]{
                    doc.getTipo(),
                    doc.getDescripcion(),
                    doc.getFechaActualizacionFormateada(),
                    doc.getDisponibilidadTexto(),
                    ""
            });
        }

        valTotalDocumentos.setText(String.valueOf(listaDocumentos.size()));
    }

    private void buscarClienteOMascota() {
        String termino = txtBuscar.getText().trim();
        if (termino.isEmpty()) {
            mostrarSelectorMascotas();
            return;
        }

        // Buscar mascota por código o nombre
        Optional<Mascota> optMascota = repo.buscarMascotaPorCodigoONombre(termino);
        if (optMascota.isPresent()) {
            cargarMascota(optMascota.get());
            return;
        }

        // Buscar cliente y tomar su primera mascota
        Optional<Cliente> optCliente = repo.buscarClientePorDniOApellido(termino);
        if (optCliente.isPresent() && !optCliente.get().getMascotas().isEmpty()) {
            cargarMascota(optCliente.get().getMascotas().get(0));
            return;
        }

        JOptionPane.showMessageDialog(this, "No se encontró cliente o mascota con: " + termino, "Sin resultados", JOptionPane.WARNING_MESSAGE);
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

        int res = JOptionPane.showConfirmDialog(this, scroll, "Seleccionar Mascota para Documentos", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res == JOptionPane.OK_OPTION) {
            int row = table.getSelectedRow();
            if (row >= 0) {
                cargarMascota(mascotas.get(row));
            }
        }
    }

    private void filtrarPorTipo() {
        if (mascotaActual == null) return;
        String tipoSeleccionado = (String) cbTipoDocumento.getSelectedItem();
        List<DocumentoMascota> todos = repo.getDocumentosPorMascota(mascotaActual.getCodigo());

        if ("Todos".equalsIgnoreCase(tipoSeleccionado)) {
            listaDocumentos = todos;
        } else {
            listaDocumentos = todos.stream()
                    .filter(d -> d.getTipo().equalsIgnoreCase(tipoSeleccionado))
                    .toList();
        }

        modeloDocumentos.setRowCount(0);
        for (DocumentoMascota doc : listaDocumentos) {
            modeloDocumentos.addRow(new Object[]{
                    doc.getTipo(),
                    doc.getDescripcion(),
                    doc.getFechaActualizacionFormateada(),
                    doc.getDisponibilidadTexto(),
                    ""
            });
        }
        valTotalDocumentos.setText(String.valueOf(listaDocumentos.size()));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Ui.instalarApariencia();
            new ConstanciasCertificadosFrame().setVisible(true);
        });
    }
}
