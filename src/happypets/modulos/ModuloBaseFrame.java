package happypets.modulos;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import happypets.ui.Iconos;
import happypets.ui.Ui;

/**
 * Clase base para la presentación y arquitectura de los módulos del sistema.
 * Permite renderizar de manera uniforme la información de cada módulo,
 * su responsable asignado y los submódulos correspondientes según el plan oficial del equipo.
 */
public class ModuloBaseFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    protected final int numeroModulo;
    protected final String nombreModulo;
    protected final String responsable;
    protected final String[] submodulos;
    protected final Color colorAcento;
    protected final Icon iconoModulo;

    public ModuloBaseFrame(int numeroModulo, String nombreModulo, String responsable, String[] submodulos, Color colorAcento, Icon iconoModulo) {
        this.numeroModulo = numeroModulo;
        this.nombreModulo = nombreModulo;
        this.responsable = responsable;
        this.submodulos = submodulos;
        this.colorAcento = colorAcento != null ? colorAcento : Ui.TURQUESA;
        this.iconoModulo = iconoModulo;

        setTitle("Módulo " + numeroModulo + ": " + nombreModulo + " - Happy Pets");
        setIconImage(Ui.icono());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1080, 680);
        setMinimumSize(new Dimension(920, 560));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Cabecera institucional superior
        add(Ui.crearCabeceraSistema(), BorderLayout.NORTH);

        // Contenedor principal con fondo gris suave
        JPanel contenedor = new JPanel(new BorderLayout(0, 16));
        contenedor.setBackground(Ui.FONDO);
        contenedor.setBorder(new EmptyBorder(18, 24, 18, 24));

        // Cabecera del módulo con información del responsable
        contenedor.add(crearCabeceraModuloConResponsable(), BorderLayout.NORTH);

        // Grid con los 4 submódulos
        JPanel gridSubmodulos = new JPanel(new GridLayout(2, 2, 16, 16));
        gridSubmodulos.setOpaque(false);

        for (int i = 0; i < submodulos.length; i++) {
            String sub = submodulos[i];
            String cod = numeroModulo + "." + (i + 1);
            gridSubmodulos.add(crearTarjetaSubmodulo(cod, sub));
        }

        JScrollPane scroll = new JScrollPane(gridSubmodulos);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(null);
        contenedor.add(scroll, BorderLayout.CENTER);

        // Pie informativo con botón de cerrar
        JButton btnCerrar = Ui.boton("Volver a Pantalla Principal", false);
        btnCerrar.addActionListener(e -> dispose());
        contenedor.add(Ui.crearPieModulo(nombreModulo + " · Resp: " + responsable, btnCerrar), BorderLayout.SOUTH);

        add(contenedor, BorderLayout.CENTER);
    }

    private JPanel crearCabeceraModuloConResponsable() {
        JPanel cab = new JPanel(new BorderLayout(14, 0)) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(colorAcento);
                g2.fillRoundRect(0, 0, 6, getHeight(), 6, 6);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        cab.setOpaque(false);
        cab.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Ui.BORDE_SUAVE, 1),
                new EmptyBorder(14, 18, 14, 18)
        ));

        JPanel izq = new JPanel();
        izq.setOpaque(false);
        izq.setLayout(new BoxLayout(izq, BoxLayout.Y_AXIS));

        JLabel lblTag = new JLabel("MÓDULO " + numeroModulo + " · PLATAFORMA VETERINARIA HAPPY PETS");
        lblTag.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblTag.setForeground(colorAcento);

        JLabel lblTit = new JLabel(nombreModulo);
        if (iconoModulo != null) {
            lblTit.setIcon(iconoModulo);
            lblTit.setIconTextGap(8);
        }
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTit.setForeground(Ui.TEXTO_TITULO);

        JLabel lblResp = new JLabel("Responsable Asignado: " + responsable + "   |   Ubicación: src/happypets/modulos/modulo" + numeroModulo + "/");
        lblResp.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblResp.setForeground(Ui.TEXTO_MUTED);

        izq.add(lblTag);
        izq.add(Box.createVerticalStrut(2));
        izq.add(lblTit);
        izq.add(Box.createVerticalStrut(4));
        izq.add(lblResp);

        cab.add(izq, BorderLayout.CENTER);

        // Estado a la derecha
        JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 8));
        der.setOpaque(false);
        JLabel badgeEstado = new JLabel("● Estructura Lista para Wireframe");
        badgeEstado.setFont(new Font("Segoe UI", Font.BOLD, 11));
        badgeEstado.setForeground(new Color(40, 167, 69));
        der.add(badgeEstado);
        cab.add(der, BorderLayout.EAST);

        return cab;
    }

    private JPanel crearTarjetaSubmodulo(String codigo, String nombreSubmodulo) {
        JPanel card = new JPanel(new BorderLayout(0, 10)) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Ui.BORDE_SUAVE, 1),
                new EmptyBorder(16, 18, 16, 18)
        ));

        // Top
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JLabel lblCod = new JLabel("SUBMÓDULO " + codigo);
        lblCod.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblCod.setForeground(colorAcento);

        JLabel lblTit = new JLabel(nombreSubmodulo);
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTit.setForeground(Ui.TEXTO_TITULO);

        top.add(lblCod, BorderLayout.NORTH);
        top.add(Box.createVerticalStrut(2), BorderLayout.CENTER);
        top.add(lblTit, BorderLayout.SOUTH);
        card.add(top, BorderLayout.NORTH);

        // Centro descripción
        JLabel lblDesc = new JLabel("<html><p style='color:#555; line-height:1.35;'>" +
                "Pantalla correspondiente al submódulo <b>" + nombreSubmodulo + "</b> asignado al compañero <b>" + responsable + "</b>. " +
                "Estructura organizada en el paquete <code>happypets.modulos.modulo" + numeroModulo + "</code> lista para vincular su wireframe oficial." +
                "</p></html>");
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        card.add(lblDesc, BorderLayout.CENTER);

        // Botón inferior
        JPanel bot = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        bot.setOpaque(false);
        JButton btn = Ui.boton("Verificar Submódulo " + codigo, true);
        btn.setPreferredSize(new Dimension(190, 34));
        btn.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                    "<html><body style='width: 360px; font-family: Segoe UI;'>" +
                            "<h3 style='color:#0096A9; margin-bottom:4px;'>Submódulo " + codigo + ": " + nombreSubmodulo + "</h3>" +
                            "<p><b>Módulo:</b> Módulo " + numeroModulo + " - " + nombreModulo + "<br>" +
                            "<b>Responsable Asignado:</b> " + responsable + "<br>" +
                            "<b>Ubicación del paquete:</b> src/happypets/modulos/modulo" + numeroModulo + "/<br>" +
                            "<b>Estado:</b> Pantalla vinculada y registrada en el Menú Principal.</p>" +
                            "<p style='color:#28a745;'><i>Lista para recibir el wireframe PDF y prototipo del compañero.</i></p>" +
                            "</body></html>",
                    "Detalle de Submódulo " + codigo,
                    JOptionPane.INFORMATION_MESSAGE);
        });
        bot.add(btn);
        card.add(bot, BorderLayout.SOUTH);

        return card;
    }

    public void abrirSubmodulo(String nombreSub) {
        setVisible(true);
        toFront();
    }
}
