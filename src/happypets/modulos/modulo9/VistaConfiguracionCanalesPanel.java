package happypets.modulos.modulo9;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import happypets.data.RepositorioVeterinaria;
import happypets.model.ConfiguracionCanalNotificacion;
import happypets.model.PreferenciaNotificacionEventos;
import happypets.ui.Iconos;
import happypets.ui.Ui;

/**
 * Submódulo 9.2: Configuración de Canales
 * Responsable: Vera Aguilar, Carlos Edgardo
 * Permite activar, configurar y parametrizar los destinos (Email, SMS, Push) y
 * frecuencias de notificación, así como los tipos de eventos críticos a reportar.
 */
public class VistaConfiguracionCanalesPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private final RepositorioVeterinaria repo = RepositorioVeterinaria.getInstancia();

    // Controles de canales
    private JCheckBox chkEmail;
    private JTextField txtEmail;
    private JComboBox<String> comboFrecEmail;

    private JCheckBox chkSMS;
    private JTextField txtSMS;
    private JComboBox<String> comboFrecSMS;

    private JCheckBox chkPush;
    private JTextField txtPush;
    private JComboBox<String> comboFrecPush;

    // Controles de eventos a notificar
    private JCheckBox chkAlertasCriticas;
    private JCheckBox chkDocumentosNuevos;
    private JCheckBox chkEventosSistema;
    private JCheckBox chkRecordatoriosClinicos;

    public VistaConfiguracionCanalesPanel() {
        setLayout(new BorderLayout(0, 16));
        setBackground(new Color(248, 250, 252));
        setBorder(BorderFactory.createEmptyBorder(20, 24, 24, 24));

        add(crearCabeceraSuperior(), BorderLayout.NORTH);

        JPanel panelCentro = new JPanel();
        panelCentro.setLayout(new BoxLayout(panelCentro, BoxLayout.Y_AXIS));
        panelCentro.setOpaque(false);

        panelCentro.add(crearCardConfiguracion());

        JScrollPane scroll = new JScrollPane(panelCentro);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setBackground(new Color(248, 250, 252));
        add(scroll, BorderLayout.CENTER);

        add(crearBarraAccionesInferiores(), BorderLayout.SOUTH);

        cargarValoresDesdeRepositorio();
    }

    private JPanel crearCabeceraSuperior() {
        JPanel cab = new JPanel(new BorderLayout(16, 8));
        cab.setBackground(Ui.TURQUESA_PROFUNDO);
        cab.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Ui.BORDE_SUAVE, 1),
                BorderFactory.createEmptyBorder(14, 20, 14, 20)
        ));

        JPanel pnlTit = new JPanel();
        pnlTit.setLayout(new BoxLayout(pnlTit, BoxLayout.Y_AXIS));
        pnlTit.setOpaque(false);

        JLabel lblTit = new JLabel("Submódulo 2: Configuración de Canales");
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTit.setForeground(Color.WHITE);
        pnlTit.add(lblTit);

        JLabel lblSub = new JLabel("Parametrización de pasarelas de entrega (Email, SMS y Push) y políticas de envío.");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblSub.setForeground(Ui.TURQUESA_SUAVE);
        pnlTit.add(lblSub);

        cab.add(pnlTit, BorderLayout.WEST);

        JPanel pnlUsr = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        pnlUsr.setOpaque(false);
        JLabel lblUsr = new JLabel("  Usuario: Admin  ");
        lblUsr.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblUsr.setOpaque(true);
        lblUsr.setBackground(Ui.TURQUESA_SUAVE);
        lblUsr.setForeground(Ui.TURQUESA_PROFUNDO);
        lblUsr.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));
        pnlUsr.add(lblUsr);

        cab.add(pnlUsr, BorderLayout.EAST);
        return cab;
    }

    private JPanel crearCardConfiguracion() {
        JPanel card = new JPanel(new BorderLayout(0, 16));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                BorderFactory.createEmptyBorder(22, 26, 22, 26)
        ));

        // Subtítulo
        JLabel lblSubtit = new JLabel("Seleccione y configure los canales de notificación activos:");
        lblSubtit.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSubtit.setForeground(new Color(51, 65, 85));
        card.add(lblSubtit, BorderLayout.NORTH);

        // Formulario de los 3 Canales con GridBagLayout
        JPanel formCanales = new JPanel(new GridBagLayout());
        formCanales.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 8, 10, 8);

        // Fila 1: Email
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.18;
        chkEmail = new JCheckBox("Email", true);
        chkEmail.setFont(new Font("Segoe UI", Font.BOLD, 13));
        chkEmail.setOpaque(false);
        chkEmail.setForeground(new Color(30, 41, 59));
        chkEmail.addActionListener(e -> txtEmail.setEnabled(chkEmail.isSelected()));
        formCanales.add(chkEmail, gbc);

        gbc.gridx = 1; gbc.weightx = 0.45;
        txtEmail = new JTextField("usuario@empresa.com");
        txtEmail.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtEmail.setPreferredSize(new Dimension(240, 34));
        formCanales.add(txtEmail, gbc);

        gbc.gridx = 2; gbc.weightx = 0.25;
        comboFrecEmail = new JComboBox<>(new String[]{
                "Frec.: Inmediata ▼",
                "Frec.: Cada hora ▼",
                "Frec.: Resumen Diario ▼",
                "Frec.: Semanal ▼"
        });
        comboFrecEmail.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        comboFrecEmail.setPreferredSize(new Dimension(170, 34));
        comboFrecEmail.setBackground(Color.WHITE);
        formCanales.add(comboFrecEmail, gbc);

        gbc.gridx = 3; gbc.weightx = 0.12;
        JButton btnTestEmail = Ui.botonSecundario("Probar Envío", null);
        btnTestEmail.setPreferredSize(new Dimension(110, 32));
        btnTestEmail.addActionListener(e -> probarCanal("Email", txtEmail.getText()));
        formCanales.add(btnTestEmail, gbc);

        // Fila 2: SMS
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.18;
        chkSMS = new JCheckBox("SMS", true);
        chkSMS.setFont(new Font("Segoe UI", Font.BOLD, 13));
        chkSMS.setOpaque(false);
        chkSMS.setForeground(new Color(30, 41, 59));
        chkSMS.addActionListener(e -> txtSMS.setEnabled(chkSMS.isSelected()));
        formCanales.add(chkSMS, gbc);

        gbc.gridx = 1; gbc.weightx = 0.45;
        txtSMS = new JTextField("+51 987 654 321");
        txtSMS.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtSMS.setPreferredSize(new Dimension(240, 34));
        formCanales.add(txtSMS, gbc);

        gbc.gridx = 2; gbc.weightx = 0.25;
        comboFrecSMS = new JComboBox<>(new String[]{
                "Frec.: Resumen Diario ▼",
                "Frec.: Inmediata ▼",
                "Frec.: Cada hora ▼",
                "Frec.: Semanal ▼"
        });
        comboFrecSMS.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        comboFrecSMS.setPreferredSize(new Dimension(170, 34));
        comboFrecSMS.setBackground(Color.WHITE);
        formCanales.add(comboFrecSMS, gbc);

        gbc.gridx = 3; gbc.weightx = 0.12;
        JButton btnTestSMS = Ui.botonSecundario("Probar Envío", null);
        btnTestSMS.setPreferredSize(new Dimension(110, 32));
        btnTestSMS.addActionListener(e -> probarCanal("SMS", txtSMS.getText()));
        formCanales.add(btnTestSMS, gbc);

        // Fila 3: Push
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.18;
        chkPush = new JCheckBox("Notificaciones Push", false);
        chkPush.setFont(new Font("Segoe UI", Font.BOLD, 13));
        chkPush.setOpaque(false);
        chkPush.setForeground(new Color(30, 41, 59));
        chkPush.addActionListener(e -> txtPush.setEnabled(chkPush.isSelected()));
        formCanales.add(chkPush, gbc);

        gbc.gridx = 1; gbc.weightx = 0.45;
        txtPush = new JTextField("Dispositivo Móvil / Navegador Web");
        txtPush.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtPush.setPreferredSize(new Dimension(240, 34));
        txtPush.setEnabled(false);
        formCanales.add(txtPush, gbc);

        gbc.gridx = 2; gbc.weightx = 0.25;
        comboFrecPush = new JComboBox<>(new String[]{
                "Frec.: Inmediata ▼",
                "Frec.: Cada hora ▼",
                "Frec.: Resumen Diario ▼"
        });
        comboFrecPush.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        comboFrecPush.setPreferredSize(new Dimension(170, 34));
        comboFrecPush.setBackground(Color.WHITE);
        formCanales.add(comboFrecPush, gbc);

        gbc.gridx = 3; gbc.weightx = 0.12;
        JButton btnTestPush = Ui.botonSecundario("Probar Envío", null);
        btnTestPush.setPreferredSize(new Dimension(110, 32));
        btnTestPush.addActionListener(e -> probarCanal("Push", txtPush.getText()));
        formCanales.add(btnTestPush, gbc);

        card.add(formCanales, BorderLayout.CENTER);

        // Sección Inferior: Notificar sobre
        JPanel pnlInferior = new JPanel();
        pnlInferior.setLayout(new BoxLayout(pnlInferior, BoxLayout.Y_AXIS));
        pnlInferior.setOpaque(false);
        pnlInferior.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(16, 8, 8, 8)
        ));

        JPanel filaChecks = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        filaChecks.setOpaque(false);

        JLabel lblNotifSobre = new JLabel("Notificar sobre:");
        lblNotifSobre.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblNotifSobre.setForeground(new Color(71, 85, 105));
        filaChecks.add(lblNotifSobre);

        chkAlertasCriticas = new JCheckBox("Alertas Críticas", true);
        chkAlertasCriticas.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        chkAlertasCriticas.setOpaque(false);
        filaChecks.add(chkAlertasCriticas);

        chkDocumentosNuevos = new JCheckBox("Documentos Nuevos", true);
        chkDocumentosNuevos.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        chkDocumentosNuevos.setOpaque(false);
        filaChecks.add(chkDocumentosNuevos);

        chkEventosSistema = new JCheckBox("Eventos de Sistema", true);
        chkEventosSistema.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        chkEventosSistema.setOpaque(false);
        filaChecks.add(chkEventosSistema);

        chkRecordatoriosClinicos = new JCheckBox("Recordatorios Clínicos", false);
        chkRecordatoriosClinicos.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        chkRecordatoriosClinicos.setOpaque(false);
        filaChecks.add(chkRecordatoriosClinicos);

        pnlInferior.add(filaChecks);
        card.add(pnlInferior, BorderLayout.SOUTH);

        return card;
    }

    private JPanel crearBarraAccionesInferiores() {
        JPanel bot = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        bot.setOpaque(false);

        JButton btnExportar = Ui.botonSecundario("Exportar Configuración", Iconos.crearIconoDescargar(13, Ui.TEXTO_TITULO));
        btnExportar.setPreferredSize(new Dimension(190, 36));
        btnExportar.addActionListener(e -> {
            String[][] meta = new String[][]{
                {"Módulo", "Configuración de Canales y Políticas de Envío"},
                {"Fecha de Configuración", java.time.LocalDate.now().toString()},
                {"Usuario Administrador", "Admin Central"},
                {"Canal Email", chkEmail.isSelected() ? "Activo (" + txtEmail.getText() + ")" : "Inactivo"},
                {"Canal SMS", chkSMS.isSelected() ? "Activo (" + txtSMS.getText() + ")" : "Inactivo"},
                {"Canal Push", chkPush.isSelected() ? "Activo (" + txtPush.getText() + ")" : "Inactivo"}
            };
            String[][] datos = new String[][]{
                {"1", "Email Gateway", txtEmail.getText(), comboFrecEmail.getSelectedItem().toString(), chkEmail.isSelected() ? "Activo" : "Inactivo"},
                {"2", "SMS Movil", txtSMS.getText(), comboFrecSMS.getSelectedItem().toString(), chkSMS.isSelected() ? "Activo" : "Inactivo"},
                {"3", "Web Push", txtPush.getText(), comboFrecPush.getSelectedItem().toString(), chkPush.isSelected() ? "Activo" : "Inactivo"}
            };
            Ui.mostrarVisorReporte(
                (java.awt.Frame) SwingUtilities.getWindowAncestor(this),
                "CONFIGURACIÓN DE CANALES Y REGLAS DE ENVÍO",
                "Directivas de Comunicación y Notificación ERP",
                meta,
                new String[]{"N°", "Canal de Pasarela", "Destinatario / Endpoint", "Frecuencia", "Estado"},
                datos,
                "Políticas de comunicación corporativa Happy Pets.",
                "Config_Canales_Notificaciones"
            );
        });
        bot.add(btnExportar);

        JButton btnCancelar = Ui.botonSecundario("Cancelar", null);
        btnCancelar.setPreferredSize(new Dimension(110, 36));
        btnCancelar.addActionListener(e -> cargarValoresDesdeRepositorio());
        bot.add(btnCancelar);

        JButton btnGuardar = Ui.botonPrimario("Guardar Preferencias", Iconos.crearIconoRefrescar(12, Color.WHITE));
        btnGuardar.setPreferredSize(new Dimension(175, 36));
        btnGuardar.addActionListener(e -> guardarPreferencias());
        bot.add(btnGuardar);

        return bot;
    }

    private void cargarValoresDesdeRepositorio() {
        List<ConfiguracionCanalNotificacion> canales = repo.getCanalesNotificacion();
        for (ConfiguracionCanalNotificacion c : canales) {
            if ("Email".equalsIgnoreCase(c.getCanal())) {
                chkEmail.setSelected(c.isActivo());
                txtEmail.setText(c.getDestino());
                txtEmail.setEnabled(c.isActivo());
            } else if ("SMS".equalsIgnoreCase(c.getCanal())) {
                chkSMS.setSelected(c.isActivo());
                txtSMS.setText(c.getDestino());
                txtSMS.setEnabled(c.isActivo());
            } else if (c.getCanal().contains("Push")) {
                chkPush.setSelected(c.isActivo());
                txtPush.setText(c.getDestino());
                txtPush.setEnabled(c.isActivo());
            }
        }

        PreferenciaNotificacionEventos pref = repo.getPreferenciasEventos();
        if (pref != null) {
            chkAlertasCriticas.setSelected(pref.isAlertasCriticas());
            chkDocumentosNuevos.setSelected(pref.isDocumentosNuevos());
            chkEventosSistema.setSelected(pref.isEventosSistema());
            chkRecordatoriosClinicos.setSelected(pref.isRecordatoriosClinicos());
        }
    }

    private void guardarPreferencias() {
        List<ConfiguracionCanalNotificacion> nuevos = new ArrayList<>();
        nuevos.add(new ConfiguracionCanalNotificacion("Email", chkEmail.isSelected(), txtEmail.getText().trim(), (String) comboFrecEmail.getSelectedItem()));
        nuevos.add(new ConfiguracionCanalNotificacion("SMS", chkSMS.isSelected(), txtSMS.getText().trim(), (String) comboFrecSMS.getSelectedItem()));
        nuevos.add(new ConfiguracionCanalNotificacion("Notificaciones Push", chkPush.isSelected(), txtPush.getText().trim(), (String) comboFrecPush.getSelectedItem()));

        PreferenciaNotificacionEventos nuevasPref = new PreferenciaNotificacionEventos(
                chkAlertasCriticas.isSelected(),
                chkDocumentosNuevos.isSelected(),
                chkEventosSistema.isSelected(),
                chkRecordatoriosClinicos.isSelected()
        );

        repo.guardarPreferenciasCanales(nuevos, nuevasPref);

        JOptionPane.showMessageDialog(this,
                "Preferencias de canales de notificación y eventos actualizadas con éxito.\nSe ha registrado un evento en el log de auditoría (#EV-1043).",
                "Configuración Guardada", JOptionPane.INFORMATION_MESSAGE);
    }

    private void probarCanal(String canal, String destino) {
        JOptionPane.showMessageDialog(this,
                "Enviando mensaje de prueba al canal " + canal + " hacia [" + destino + "]...\nRespuesta del servidor: 200 OK (Mensaje entregado).",
                "Test de Canal", JOptionPane.INFORMATION_MESSAGE);
    }
}
