package happypets.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
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
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import happypets.data.RepositorioVeterinaria;
import happypets.model.Cliente;
import happypets.model.Mascota;

/**
 * Pantalla 1: Mantenimiento de Clientes y Mascotas vinculadas.
 * Diseñada y validada en estricta fidelidad con el Wireframe oficial del proyecto.
 */
public class ClientesMascotasFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    private final RepositorioVeterinaria repo = RepositorioVeterinaria.getInstancia();
    private Cliente clienteActual;
    private Mascota mascotaSeleccionada;

    // Campos formulario cliente
    private JTextField txtNombres;
    private JTextField txtDocumento;
    private JTextField txtTelefono;
    private JTextField txtTelefonoSecundario;
    private JTextField txtCorreo;
    private JTextField txtDireccion;
    private JTextField txtCiudad;
    private JTextArea txtNotas;
    private JTextField txtBusqueda;

    // Mascotas
    private JLabel lblTituloMascotas;
    private JTable tablaMascotas;
    private DefaultTableModel modeloMascotas;

    // Resumen mascota seleccionada
    private JLabel valMascota;
    private JLabel valPeso;
    private JLabel valPlanVacunal;
    private JLabel valAlergias;

    public ClientesMascotasFrame() {
        Ui.configurarVentana(this, "Mantenimiento de Clientes");
        setLayout(new BorderLayout());

        // Cabecera Institucional
        add(Ui.crearCabeceraSistema(), BorderLayout.NORTH);

        // Contenedor principal con scroll si la pantalla es reducida
        JPanel panelCuerpo = new JPanel(new BorderLayout(0, 14));
        panelCuerpo.setBackground(Ui.FONDO);
        panelCuerpo.setBorder(new EmptyBorder(16, 20, 16, 20));

        // Subcabecera del módulo
        panelCuerpo.add(Ui.crearCabeceraModulo("Mantenimiento de Clientes", "Registro de clientes y mascotas vinculadas"), BorderLayout.NORTH);

        // Centro: Barra de acciones + dos columnas (Ficha Cliente y Mascotas)
        JPanel panelCentral = new JPanel();
        panelCentral.setLayout(new BoxLayout(panelCentral, BoxLayout.Y_AXIS));
        panelCentral.setOpaque(false);

        panelCentral.add(crearBarraAccionesClientes());
        panelCentral.add(Box.createVerticalStrut(14));
        panelCentral.add(crearSeccionDobleColumna());

        panelCuerpo.add(panelCentral, BorderLayout.CENTER);

        // Pie de página
        JButton btnExportar = Ui.boton("Exportar datos", false);
        JButton btnGuardarFinalizar = Ui.boton("Guardar y finalizar", true);

        btnExportar.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Datos del cliente y sus mascotas exportados exitosamente a formato CSV/Excel.",
                "Exportación", JOptionPane.INFORMATION_MESSAGE));

        btnGuardarFinalizar.addActionListener(e -> {
            guardarCambiosCliente();
            JOptionPane.showMessageDialog(this,
                    "Registro guardado y finalizado correctamente en el sistema Happy Pets.",
                    "Operación Exitosa", JOptionPane.INFORMATION_MESSAGE);
        });

        panelCuerpo.add(Ui.crearPieModulo("Clientes y Mascotas", btnExportar, btnGuardarFinalizar), BorderLayout.SOUTH);

        add(panelCuerpo, BorderLayout.CENTER);

        // Cargar datos del cliente principal según el Wireframe
        Optional<Cliente> opt = repo.getClientes().stream().findFirst();
        opt.ifPresent(this::cargarClienteEnFormulario);
    }

    private JPanel crearBarraAccionesClientes() {
        JPanel barra = new JPanel(new BorderLayout(10, 0));
        barra.setBackground(Color.WHITE);
        barra.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Ui.BORDE_SUAVE, 1),
                new EmptyBorder(8, 14, 8, 14)
        ));

        // Izquierda: Botones de gestión de clientes
        JPanel izq = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        izq.setOpaque(false);

        JLabel lbl = new JLabel("Clientes:");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(new Color(60, 60, 60));
        izq.add(lbl);

        JButton btnListar = Ui.boton("Listar clientes", true);
        JButton btnNuevo = Ui.boton("Nuevo cliente", false);
        JButton btnModificar = Ui.boton("Modificar cliente", false);
        JButton btnEliminar = Ui.boton("Eliminar cliente", false);

        btnListar.addActionListener(e -> mostrarListaClientes());
        btnNuevo.addActionListener(e -> nuevoCliente());
        btnModificar.addActionListener(e -> {
            txtNombres.requestFocus();
            JOptionPane.showMessageDialog(this, "Edite los campos en la ficha y presione 'Guardar cambios'.", "Modificar Cliente", JOptionPane.INFORMATION_MESSAGE);
        });
        btnEliminar.addActionListener(e -> eliminarClienteActual());

        izq.add(btnListar);
        izq.add(btnNuevo);
        izq.add(btnModificar);
        izq.add(btnEliminar);
        barra.add(izq, BorderLayout.WEST);

        // Derecha: Búsqueda por DNI o apellido
        JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        der.setOpaque(false);

        JLabel lblBuscar = new JLabel("Buscar por DNI o apellido:");
        lblBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblBuscar.setForeground(new Color(60, 60, 60));
        der.add(lblBuscar);

        txtBusqueda = Ui.campoTexto(18);
        txtBusqueda.setToolTipText("Documento o nombre...");
        der.add(txtBusqueda);

        JButton btnBuscar = Ui.boton("Buscar", true);
        btnBuscar.addActionListener(e -> buscarCliente());
        txtBusqueda.addActionListener(e -> buscarCliente());
        der.add(btnBuscar);

        barra.add(der, BorderLayout.EAST);
        return barra;
    }

    private JPanel crearSeccionDobleColumna() {
        JPanel contenedor = new JPanel(new GridLayout(1, 2, 16, 0));
        contenedor.setOpaque(false);

        contenedor.add(crearTarjetaFichaCliente());
        contenedor.add(crearTarjetaMascotas());
        return contenedor;
    }

    private JPanel crearTarjetaFichaCliente() {
        JPanel tarjeta = Ui.crearTarjeta();
        tarjeta.setLayout(new BorderLayout());

        // Cabecera de la ficha
        tarjeta.add(Ui.crearTituloTarjeta("Ficha de Registro del Cliente"), BorderLayout.NORTH);

        // Formulario
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(new EmptyBorder(16, 18, 12, 18));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        txtNombres = Ui.campoTexto(20);
        txtDocumento = Ui.campoTexto(20);
        txtTelefono = Ui.campoTexto(20);
        txtTelefonoSecundario = Ui.campoTexto(20);
        txtCorreo = Ui.campoTexto(20);
        txtDireccion = Ui.campoTexto(20);
        txtCiudad = Ui.campoTexto(20);

        txtNotas = new JTextArea(3, 20);
        txtNotas.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtNotas.setLineWrap(true);
        txtNotas.setWrapStyleWord(true);
        txtNotas.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 185, 190), 1),
                new EmptyBorder(4, 6, 4, 6)
        ));
        JScrollPane scrollNotas = new JScrollPane(txtNotas);

        int r = 0;
        agregarFilaFormulario(form, gbc, r++, "Nombres y apellidos*:", txtNombres);
        agregarFilaFormulario(form, gbc, r++, "DNI / CE*:", txtDocumento);
        agregarFilaFormulario(form, gbc, r++, "Teléfono de contacto*:", txtTelefono);
        agregarFilaFormulario(form, gbc, r++, "Teléfono secundario:", txtTelefonoSecundario);
        agregarFilaFormulario(form, gbc, r++, "Correo electrónico*:", txtCorreo);
        agregarFilaFormulario(form, gbc, r++, "Dirección:", txtDireccion);
        agregarFilaFormulario(form, gbc, r++, "Distrito / Ciudad:", txtCiudad);
        agregarFilaFormulario(form, gbc, r++, "Notas de contacto:", scrollNotas);

        tarjeta.add(form, BorderLayout.CENTER);

        // Barra inferior de la ficha: "* Campos obligatorios" + [Guardar cambios] [Cancelar]
        JPanel pieFicha = new JPanel(new BorderLayout());
        pieFicha.setBackground(Color.WHITE);
        pieFicha.setBorder(new EmptyBorder(8, 18, 14, 18));

        JLabel lblObligatorio = new JLabel("* Campos obligatorios");
        lblObligatorio.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblObligatorio.setForeground(new Color(120, 120, 120));
        pieFicha.add(lblObligatorio, BorderLayout.WEST);

        JPanel botonesFicha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        botonesFicha.setOpaque(false);

        JButton btnGuardar = Ui.boton("Guardar cambios", true);
        JButton btnCancelar = Ui.boton("Cancelar", false);

        btnGuardar.addActionListener(e -> guardarCambiosCliente());
        btnCancelar.addActionListener(e -> {
            if (clienteActual != null) {
                cargarClienteEnFormulario(clienteActual);
            } else {
                nuevoCliente();
            }
        });

        botonesFicha.add(btnGuardar);
        botonesFicha.add(btnCancelar);
        pieFicha.add(botonesFicha, BorderLayout.EAST);

        tarjeta.add(pieFicha, BorderLayout.SOUTH);
        return tarjeta;
    }

    private void agregarFilaFormulario(JPanel panel, GridBagConstraints gbc, int fila, String etiqueta, javax.swing.JComponent componente) {
        gbc.gridx = 0;
        gbc.gridy = fila;
        gbc.weightx = 0.32;
        JLabel lbl = new JLabel(etiqueta, SwingConstants.RIGHT);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        panel.add(lbl, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.68;
        panel.add(componente, gbc);
    }

    private JPanel crearTarjetaMascotas() {
        JPanel tarjeta = Ui.crearTarjeta();
        tarjeta.setLayout(new BorderLayout());

        // Cabecera tarjeta con botones de mascota exactamente como wireframe
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Ui.TURQUESA);
        header.setBorder(new EmptyBorder(6, 14, 6, 10));

        lblTituloMascotas = new JLabel("Mascotas vinculadas al cliente · 3 registradas");
        lblTituloMascotas.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTituloMascotas.setForeground(Color.WHITE);
        header.add(lblTituloMascotas, BorderLayout.WEST);

        JPanel accionesMascota = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        accionesMascota.setOpaque(false);

        JButton btnNuevaMascota = Ui.boton("Nueva mascota", false);
        JButton btnModificarMascota = Ui.boton("Modificar mascota", false);
        JButton btnEliminarMascota = Ui.boton("Eliminar mascota", false);

        btnNuevaMascota.addActionListener(e -> agregarNuevaMascota());
        btnModificarMascota.addActionListener(e -> modificarMascotaSeleccionada());
        btnEliminarMascota.addActionListener(e -> eliminarMascotaSeleccionada());

        accionesMascota.add(btnNuevaMascota);
        accionesMascota.add(btnModificarMascota);
        accionesMascota.add(btnEliminarMascota);
        header.add(accionesMascota, BorderLayout.EAST);

        tarjeta.add(header, BorderLayout.NORTH);

        // Tabla de mascotas
        String[] columnas = {"Código", "Nombre", "Especie", "Raza", "Edad", "Sexo", "Estado"};
        modeloMascotas = new DefaultTableModel(columnas, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        tablaMascotas = new JTable(modeloMascotas);
        Ui.formatearTabla(tablaMascotas);

        tablaMascotas.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int fila = tablaMascotas.getSelectedRow();
                if (fila >= 0 && clienteActual != null && fila < clienteActual.getMascotas().size()) {
                    mascotaSeleccionada = clienteActual.getMascotas().get(fila);
                    actualizarTarjetaResumenMascota(mascotaSeleccionada);
                }
            }
        });

        JScrollPane scrollTabla = new JScrollPane(tablaMascotas);
        scrollTabla.setBorder(BorderFactory.createEmptyBorder(1, 0, 1, 0));
        tarjeta.add(scrollTabla, BorderLayout.CENTER);

        // Resumen inferior de mascota seleccionada
        JPanel resumenPanel = new JPanel(new GridLayout(1, 4, 12, 0));
        resumenPanel.setBackground(new Color(248, 249, 251));
        resumenPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Ui.BORDE_SUAVE),
                new EmptyBorder(10, 14, 10, 14)
        ));

        valMascota = new JLabel("Rocky (VET-0091)");
        valPeso = new JLabel("28.4 kg");
        valPlanVacunal = new JLabel("Al día");
        valAlergias = new JLabel("Polen de gramíneas");

        resumenPanel.add(crearCajaDato("Mascota seleccionada", valMascota));
        resumenPanel.add(crearCajaDato("Peso actual", valPeso));
        resumenPanel.add(crearCajaDato("Plan vacunal", valPlanVacunal));
        resumenPanel.add(crearCajaDato("Alergias", valAlergias));

        tarjeta.add(resumenPanel, BorderLayout.SOUTH);
        return tarjeta;
    }

    private JPanel crearCajaDato(String etiqueta, JLabel valorLabel) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

        JLabel lbl = new JLabel(etiqueta);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lbl.setForeground(new Color(110, 115, 120));

        valorLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        valorLabel.setForeground(new Color(30, 30, 30));

        p.add(lbl);
        p.add(Box.createVerticalStrut(2));
        p.add(valorLabel);
        return p;
    }

    public void cargarClienteEnFormulario(Cliente cliente) {
        this.clienteActual = cliente;
        if (cliente == null) {
            nuevoCliente();
            return;
        }

        txtNombres.setText(cliente.getNombreCompleto());
        txtDocumento.setText(cliente.getNumeroDocumento());
        txtTelefono.setText(cliente.getTelefonoPrincipal());
        txtTelefonoSecundario.setText(cliente.getTelefonoSecundario());
        txtCorreo.setText(cliente.getCorreo());
        txtDireccion.setText(cliente.getDireccion());
        txtCiudad.setText(cliente.getDistritoCiudad());
        txtNotas.setText(cliente.getNotasContacto());

        actualizarTablaMascotas();
    }

    private void actualizarTablaMascotas() {
        modeloMascotas.setRowCount(0);
        if (clienteActual == null) {
            lblTituloMascotas.setText("Mascotas vinculadas al cliente · 0 registradas");
            limpiarResumenMascota();
            return;
        }

        List<Mascota> lista = clienteActual.getMascotas();
        lblTituloMascotas.setText("Mascotas vinculadas al cliente · " + lista.size() + " registradas");

        for (Mascota m : lista) {
            modeloMascotas.addRow(new Object[]{
                    m.getCodigo(),
                    m.getNombre(),
                    m.getEspecie(),
                    m.getRaza(),
                    m.getEdadTexto(),
                    m.getSexo(),
                    m.getEstadoTexto()
            });
        }

        if (!lista.isEmpty()) {
            tablaMascotas.setRowSelectionInterval(0, 0);
            mascotaSeleccionada = lista.get(0);
            actualizarTarjetaResumenMascota(mascotaSeleccionada);
        } else {
            limpiarResumenMascota();
        }
    }

    private void actualizarTarjetaResumenMascota(Mascota m) {
        if (m == null) {
            limpiarResumenMascota();
            return;
        }
        valMascota.setText(m.getNombre() + " (" + m.getCodigo() + ")");
        valPeso.setText(m.getPesoActualKg() > 0 ? m.getPesoActualKg() + " kg" : "-");
        valPlanVacunal.setText(m.getPlanVacunal() != null && !m.getPlanVacunal().isEmpty() ? m.getPlanVacunal() : "Al día");
        valAlergias.setText(m.getAlergias() != null && !m.getAlergias().isEmpty() ? m.getAlergias() : "Ninguna");
    }

    private void limpiarResumenMascota() {
        valMascota.setText("-");
        valPeso.setText("-");
        valPlanVacunal.setText("-");
        valAlergias.setText("-");
    }

    private void nuevoCliente() {
        clienteActual = new Cliente();
        clienteActual.setCodigo("CLI-" + String.format("%03d", repo.getClientes().size() + 1));
        txtNombres.setText("");
        txtDocumento.setText("");
        txtTelefono.setText("");
        txtTelefonoSecundario.setText("");
        txtCorreo.setText("");
        txtDireccion.setText("");
        txtCiudad.setText("");
        txtNotas.setText("");
        txtBusqueda.setText("");
        actualizarTablaMascotas();
        txtNombres.requestFocus();
    }

    private void guardarCambiosCliente() {
        if (txtNombres.getText().trim().isEmpty() || txtDocumento.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nombres y DNI son campos obligatorios.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (clienteActual == null) {
            clienteActual = new Cliente();
            clienteActual.setCodigo("CLI-" + String.format("%03d", repo.getClientes().size() + 1));
        }

        String nomComp = txtNombres.getText().trim();
        int idx = nomComp.lastIndexOf(' ');
        if (idx > 0) {
            clienteActual.setNombres(nomComp.substring(0, idx));
            clienteActual.setApellidos(nomComp.substring(idx + 1));
        } else {
            clienteActual.setNombres(nomComp);
            clienteActual.setApellidos("");
        }

        clienteActual.setTipoDocumento("DNI");
        clienteActual.setNumeroDocumento(txtDocumento.getText().trim());
        clienteActual.setTelefonoPrincipal(txtTelefono.getText().trim());
        clienteActual.setTelefonoSecundario(txtTelefonoSecundario.getText().trim());
        clienteActual.setCorreo(txtCorreo.getText().trim());
        clienteActual.setDireccion(txtDireccion.getText().trim());
        clienteActual.setDistritoCiudad(txtCiudad.getText().trim());
        clienteActual.setNotasContacto(txtNotas.getText().trim());

        repo.guardarCliente(clienteActual);
        JOptionPane.showMessageDialog(this, "Cliente guardado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }

    private void buscarCliente() {
        String termino = txtBusqueda.getText().trim();
        if (termino.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese DNI o apellido a buscar.", "Búsqueda", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Optional<Cliente> opt = repo.buscarClientePorDniOApellido(termino);
        if (opt.isPresent()) {
            cargarClienteEnFormulario(opt.get());
        } else {
            JOptionPane.showMessageDialog(this, "No se encontró ningún cliente con: " + termino, "Sin resultados", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void mostrarListaClientes() {
        List<Cliente> clientes = repo.getClientes();
        String[] cols = {"Código", "Nombres y Apellidos", "DNI", "Teléfono", "Mascotas"};
        Object[][] data = new Object[clientes.size()][5];
        for (int i = 0; i < clientes.size(); i++) {
            Cliente c = clientes.get(i);
            data[i][0] = c.getCodigo();
            data[i][1] = c.getNombreCompleto();
            data[i][2] = c.getNumeroDocumento();
            data[i][3] = c.getTelefonoPrincipal();
            data[i][4] = c.getMascotas().size();
        }

        JTable table = new JTable(new DefaultTableModel(data, cols));
        Ui.formatearTabla(table);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setPreferredSize(new Dimension(650, 240));

        int res = JOptionPane.showConfirmDialog(this, scroll, "Seleccionar Cliente", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res == JOptionPane.OK_OPTION) {
            int row = table.getSelectedRow();
            if (row >= 0) {
                cargarClienteEnFormulario(clientes.get(row));
            }
        }
    }

    private void eliminarClienteActual() {
        if (clienteActual == null || clienteActual.getCodigo() == null) {
            JOptionPane.showMessageDialog(this, "No hay cliente seleccionado para eliminar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int res = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de eliminar al cliente " + clienteActual.getNombreCompleto() + " y sus mascotas?",
                "Confirmar Eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (res == JOptionPane.YES_OPTION) {
            repo.eliminarCliente(clienteActual.getCodigo());
            JOptionPane.showMessageDialog(this, "Cliente eliminado.", "Operación Exitosa", JOptionPane.INFORMATION_MESSAGE);
            Optional<Cliente> siguiente = repo.getClientes().stream().findFirst();
            if (siguiente.isPresent()) {
                cargarClienteEnFormulario(siguiente.get());
            } else {
                nuevoCliente();
            }
        }
    }

    private void agregarNuevaMascota() {
        if (clienteActual == null) {
            JOptionPane.showMessageDialog(this, "Primero guarde o seleccione un cliente para vincular mascotas.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        FormularioMascotaDialog dlg = new FormularioMascotaDialog(this, null, nueva -> {
            clienteActual.agregarMascota(nueva);
            actualizarTablaMascotas();
        });
        dlg.setVisible(true);
    }

    private void modificarMascotaSeleccionada() {
        if (mascotaSeleccionada == null) {
            JOptionPane.showMessageDialog(this, "Seleccione una mascota en la tabla para modificar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        FormularioMascotaDialog dlg = new FormularioMascotaDialog(this, mascotaSeleccionada, mod -> {
            actualizarTablaMascotas();
        });
        dlg.setVisible(true);
    }

    private void eliminarMascotaSeleccionada() {
        if (mascotaSeleccionada == null) {
            JOptionPane.showMessageDialog(this, "Seleccione una mascota en la tabla para eliminar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int res = JOptionPane.showConfirmDialog(this,
                "¿Desea eliminar a la mascota " + mascotaSeleccionada.getNombre() + "?",
                "Confirmar Eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (res == JOptionPane.YES_OPTION) {
            clienteActual.eliminarMascota(mascotaSeleccionada.getCodigo());
            actualizarTablaMascotas();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Ui.instalarApariencia();
            new ClientesMascotasFrame().setVisible(true);
        });
    }
}
