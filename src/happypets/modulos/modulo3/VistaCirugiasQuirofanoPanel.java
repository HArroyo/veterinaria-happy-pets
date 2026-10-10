package happypets.modulos.modulo3;

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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import happypets.data.RepositorioVeterinaria;
import happypets.model.Cliente;
import happypets.model.Mascota;
import happypets.model.RegistroCirugia;
import happypets.ui.Iconos;
import happypets.ui.Ui;

/**
 * Submódulo 3.3: Cirugías y Quirófano Veterinario. Diseñado según el wireframe
 * oficial (Pág. 5 Derecha): - Panel de control de quirófanos en tiempo real
 * (Q-01 Cirugía Mayor, Q-02 Procedimientos). - Programación de intervenciones
 * quirúrgicas. - Registro operatorio intraquirúrgico (anestesia, medicamentos,
 * personal de apoyo, tiempos). - Seguimiento postoperatorio clínico (escala de
 * dolor, alta, curación, retiro de puntos).
 */
public class VistaCirugiasQuirofanoPanel extends happypets.ui.AssetsModulo {
	private static final long serialVersionUID = 1L;

	private static final Color COLOR_BORDE = new Color(226, 232, 240);
	private static final Color COLOR_AZUL_PRIMARIO = new Color(2, 132, 199);
	private static final Color COLOR_TEXTO_TITULO = new Color(30, 41, 59);
	private static final Color COLOR_TEXTO_MUTED = new Color(100, 116, 139);

	private final RepositorioVeterinaria repo = RepositorioVeterinaria.getInstancia();

	// KPIs
	private JLabel lblKpiQuirofanosActivos;
	private JLabel lblKpiCirugiasHoy;
	private JLabel lblKpiEnProcedimiento;
	private JLabel lblKpiEnRecuperacion;

	// Tabla de Quirófano en Vivo
	private JTable tablaQuirofano;
	private DefaultTableModel modeloQuirofano;
	private JLabel lblContadorCirugias;
	private List<RegistroCirugia> listaActual;

	// Formulario de Programación
	private JComboBox<PacienteItem> cbPacienteProg;
	private JTextField txtTutorProg;
	private JComboBox<String> cbProcedimientoProg;
	private JComboBox<String> cbQuirofanoProg;
	private JTextField txtCirujanoProg;
	private JTextField txtHoraProg;

	// Formulario Registro Intraoperatorio
	private JTextField txtPacienteActa;
	private JTextField txtProcedimientoActa;
	private JTextField txtCirujanoActa;
	private JTextField txtAnestesistaActa;
	private JTextField txtHorariosReal;
	private JComboBox<String> cbAnestesiaActa;
	private JTextArea txtMedicamentosActa;
	private JTextArea txtObservacionesActa;

	// Formulario Postoperatorio
	private JComboBox<String> cbEstadoAlta;
	private JComboBox<String> cbEscalaDolor;
	private JTextArea txtMedicosCasa;
	private JTextArea txtIndicacionesTutor;
	private JTextField txtFechaPuntos;

	public VistaCirugiasQuirofanoPanel() {
		setLayout(new BorderLayout());
		setOpaque(false);

		JPanel contenido = new JPanel();
		contenido.setOpaque(false);
		contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
		contenido.setBorder(new EmptyBorder(12, 18, 14, 18));

		// 1. Cabecera
		contenido.add(crearCabeceraVista());
		contenido.add(Box.createVerticalStrut(8));

		// 2. Fila de 4 KPIs
		contenido.add(crearFilaKpis());
		contenido.add(Box.createVerticalStrut(10));

		// 3. Monitor de Quirófano en Tiempo Real (Tabla Superior)
		contenido.add(crearCardMonitorQuirofano());
		contenido.add(Box.createVerticalStrut(12));

		// 4. Doble columna: Programación Quirúrgica y Acta Operatoria / Postoperatorio
		contenido.add(crearDobleColumnaDetalle());

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

		recargarDatos();
	}

	private JPanel crearCabeceraVista() {
		JPanel cab = new JPanel(new BorderLayout());
		cab.setOpaque(false);

		JPanel izq = new JPanel();
		izq.setOpaque(false);
		izq.setLayout(new BoxLayout(izq, BoxLayout.Y_AXIS));

		JLabel titulo = new JLabel("Cirugías y Control de Quirófano");
		titulo.setFont(new Font("Segoe UI", Font.BOLD, 17));
		titulo.setForeground(COLOR_TEXTO_TITULO);

		JLabel subtitulo = new JLabel(
				"Módulo 3.3 · Programación quirúrgica, protocolo intraoperatorio y seguimiento postoperatorio");
		subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		subtitulo.setForeground(COLOR_TEXTO_MUTED);

		izq.add(titulo);
		izq.add(Box.createVerticalStrut(2));
		izq.add(subtitulo);
		cab.add(izq, BorderLayout.CENTER);

		JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
		der.setOpaque(false);

		JButton btnRefrescar =
		        crearBotonWeb(
		                "Actualizar Estado",
		                Iconos.crearIconoActualizar(
		                        15,
		                        new Color(51, 65, 85)
		                ),
		                false,
		                () -> recargarDatos()
		        );
		der.add(btnRefrescar);

		cab.add(der, BorderLayout.EAST);
		return cab;
	}

	private JPanel crearFilaKpis() {
		JPanel fila = new JPanel(new GridLayout(1, 4, 12, 0));
		fila.setOpaque(false);

		lblKpiQuirofanosActivos = new JLabel("2 / 2", SwingConstants.LEFT);
		lblKpiCirugiasHoy = new JLabel("3", SwingConstants.LEFT);
		lblKpiEnProcedimiento = new JLabel("1", SwingConstants.LEFT);
		lblKpiEnRecuperacion = new JLabel("1", SwingConstants.LEFT);

		fila.add(crearCardKpi(lblKpiQuirofanosActivos, "QUIRÓFANOS HABILITADOS", new Color(224, 242, 254),
				COLOR_AZUL_PRIMARIO, Iconos.crearIconoBisturi(20, COLOR_AZUL_PRIMARIO)));
		fila.add(crearCardKpi(lblKpiCirugiasHoy, "CIRUGÍAS PROGRAMADAS", new Color(243, 232, 255),
				new Color(147, 51, 234), Iconos.crearIconoCalendario(20, new Color(147, 51, 234))));
		fila.add(crearCardKpi(lblKpiEnProcedimiento, "EN INTERVENCIÓN ACTIVA", new Color(254, 243, 199),
				new Color(217, 119, 6), Iconos.crearIconoReloj(20, new Color(217, 119, 6))));
		fila.add(crearCardKpi(lblKpiEnRecuperacion, "EN RECUPERACIÓN / ALTA", new Color(220, 252, 231),
				new Color(22, 163, 74), Iconos.crearIconoCheck(20, new Color(22, 163, 74))));

		return fila;
	}

	private JPanel crearCardKpi(JLabel lblValor, String etiqueta, Color colorFondoIco, Color colorIco, Icon icono) {
		JPanel card = new JPanel(new BorderLayout(10, 0)) {
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
		card.setBorder(new EmptyBorder(8, 12, 8, 12));

		JLabel iconBox = new JLabel(icono, SwingConstants.CENTER) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setColor(colorFondoIco);
				g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
				super.paintComponent(g2);
				g2.dispose();
			}
		};
		iconBox.setPreferredSize(new Dimension(36, 36));
		card.add(iconBox, BorderLayout.WEST);

		JPanel centro = new JPanel();
		centro.setOpaque(false);
		centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));

		lblValor.setFont(new Font("Segoe UI", Font.BOLD, 17));
		lblValor.setForeground(COLOR_TEXTO_TITULO);

		JLabel lblSub = new JLabel(etiqueta);
		lblSub.setFont(new Font("Segoe UI", Font.BOLD, 9));
		lblSub.setForeground(COLOR_TEXTO_MUTED);

		centro.add(lblValor);
		centro.add(lblSub);
		card.add(centro, BorderLayout.CENTER);

		return card;
	}

	private JPanel crearCardMonitorQuirofano() {
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

		JPanel top = new JPanel(new BorderLayout());
		top.setOpaque(false);

		JLabel tit = new JLabel("Control de Quirófano en Vivo (Tabla Operatoria)");
		tit.setFont(new Font("Segoe UI", Font.BOLD, 13));
		tit.setForeground(COLOR_TEXTO_TITULO);
		top.add(tit, BorderLayout.WEST);

		lblContadorCirugias = new JLabel("Cargando procedimientos...");
		lblContadorCirugias.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		lblContadorCirugias.setForeground(COLOR_TEXTO_MUTED);
		top.add(lblContadorCirugias, BorderLayout.EAST);

		card.add(top, BorderLayout.NORTH);

		String[] columnas = { "ID", "Quirófano", "Paciente", "Tutor", "Procedimiento Quirúrgico", "Hora Prog.",
				"Cirujano Principal", "Anestesia", "Estado" };
		modeloQuirofano = new DefaultTableModel(columnas, 0) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int r, int c) {
				return false;
			}
		};

		tablaQuirofano = new JTable(modeloQuirofano);
		Ui.formatearTabla(tablaQuirofano);
		tablaQuirofano.setRowHeight(32);

		tablaQuirofano.getColumnModel().getColumn(0).setPreferredWidth(75);
		tablaQuirofano.getColumnModel().getColumn(1).setPreferredWidth(130);
		tablaQuirofano.getColumnModel().getColumn(2).setPreferredWidth(85);
		tablaQuirofano.getColumnModel().getColumn(4).setPreferredWidth(210);
		tablaQuirofano.getColumnModel().getColumn(8).setCellRenderer(new BadgeEstadoCirugiaRenderer());

		tablaQuirofano.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				cargarCirugiaSeleccionada();
			}
		});

		JScrollPane sp = new JScrollPane(tablaQuirofano);
		sp.setBorder(BorderFactory.createLineBorder(COLOR_BORDE, 1));
		sp.setPreferredSize(new Dimension(1000, 150));
		card.add(sp, BorderLayout.CENTER);

		JPanel bot = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
		bot.setOpaque(false);

		JButton btnIniciar =
		        crearBotonWeb(
		                "Iniciar Cirugía",
		                Iconos.crearIconoBisturi(
		                        15,
		                        new Color(51, 65, 85)
		                ),
		                false,
		                () -> cambiarEstadoSeleccionado(
		                        "En Proceso"
		                )
		        );
		JButton btnRecup =
		        crearBotonWeb(
		                "Pasar a Recuperación",
		                Iconos.crearIconoRecuperacion(
		                        15,
		                        new Color(51, 65, 85)
		                ),
		                false,
		                () -> cambiarEstadoSeleccionado(
		                        "En Recuperación"
		                )
		        );
		JButton btnAlta =
		        crearBotonWeb(
		                "Dar Alta Quirúrgica",
		                Iconos.crearIconoCheck(
		                        15,
		                        new Color(22, 163, 74)
		                ),
		                false,
		                () -> cambiarEstadoSeleccionado(
		                        "Alta Quirúrgica"
		                )
		        );
		bot.add(btnIniciar);
		bot.add(btnRecup);
		bot.add(btnAlta);

		card.add(bot, BorderLayout.SOUTH);
		return card;
	}

	private JPanel crearDobleColumnaDetalle() {
		JPanel doble = new JPanel(new GridLayout(1, 2, 14, 0));
		doble.setOpaque(false);

		doble.add(crearCardProgramarCirugia());
		doble.add(crearCardActaPostoperatorio());

		return doble;
	}

	private JPanel crearCardProgramarCirugia() {
		JPanel card = new JPanel(new BorderLayout()) {
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

		JPanel form = new JPanel();
		form.setOpaque(false);
		form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));

		JLabel tit = new JLabel("1) Programar Intervención Quirúrgica");
		tit.setFont(new Font("Segoe UI", Font.BOLD, 13));
		tit.setForeground(COLOR_TEXTO_TITULO);
		form.add(tit);
		form.add(Box.createVerticalStrut(8));

		cbPacienteProg = new JComboBox<>();
		cbPacienteProg.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		cbPacienteProg.setBackground(Color.WHITE);
		cbPacienteProg.setPreferredSize(new Dimension(0, 26));
		cbPacienteProg.addActionListener(e -> autocompletarTutorProg());
		form.add(crearFilaCampo("Paciente a Intervenir *", cbPacienteProg));
		form.add(Box.createVerticalStrut(4));

		txtTutorProg = crearCampoTexto(false);
		form.add(crearFilaCampo("Tutor / Responsable", txtTutorProg));
		form.add(Box.createVerticalStrut(4));

		String[] proceds = { "Ovariohisterectomía preventiva (Esterilización)", "Castración / Orquiectomía",
				"Profilaxis Dental + Extracción de piezas", "Excisión de Tumor Cutáneo / Biopsia",
				"Cirugía Traumatológica / Osteosíntesis", "Cesárea de Urgencia",
				"Limpieza de Herida Profunda y Sutura" };
		cbProcedimientoProg = new JComboBox<>(proceds);
		cbProcedimientoProg.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		cbProcedimientoProg.setBackground(Color.WHITE);
		form.add(crearFilaCampo("Procedimiento Quirúrgico *", cbProcedimientoProg));
		form.add(Box.createVerticalStrut(4));

		JPanel filaQuiro = new JPanel(new GridLayout(1, 2, 8, 0));
		filaQuiro.setOpaque(false);
		cbQuirofanoProg = new JComboBox<>(
				new String[] { "Quirófano 1 (Cirugía Mayor)", "Quirófano 2 (Procedimientos)" });
		cbQuirofanoProg.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		cbQuirofanoProg.setBackground(Color.WHITE);

		txtHoraProg = crearCampoTexto(true);
		txtHoraProg.setText("09:00");
		filaQuiro.add(crearFilaCampo("Sala de Quirófano *", cbQuirofanoProg));
		filaQuiro.add(crearFilaCampo("Hora Programada *", txtHoraProg));
		form.add(filaQuiro);
		form.add(Box.createVerticalStrut(4));

		txtCirujanoProg = crearCampoTexto(true);
		txtCirujanoProg.setText("Dr. Carlos Méndez (Cirujano)");
		form.add(crearFilaCampo("Cirujano Principal *", txtCirujanoProg));
		form.add(Box.createVerticalStrut(10));

		JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
		btnRow.setOpaque(false);
		JButton btnProg =
		        crearBotonWeb(
		                "Programar Cirugía",
		                Iconos.crearIconoCalendario(
		                        15,
		                        Color.WHITE
		                ),
		                true,
		                () -> programarNuevaCirugia()
		        );
		btnRow.add(btnProg);
		form.add(btnRow);

		card.add(form, BorderLayout.NORTH);
		return card;
	}

	private JPanel crearCardActaPostoperatorio() {
		JPanel card = new JPanel(new BorderLayout()) {
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

		JPanel form = new JPanel();
		form.setOpaque(false);
		form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));

		JLabel tit = new JLabel("2) Registro Operatorio y Protocolo Postquirúrgico");
		tit.setFont(new Font("Segoe UI", Font.BOLD, 13));
		tit.setForeground(COLOR_TEXTO_TITULO);
		form.add(tit);
		form.add(Box.createVerticalStrut(8));

		JPanel fila1 = new JPanel(new GridLayout(1, 2, 8, 0));
		fila1.setOpaque(false);
		txtPacienteActa = crearCampoTexto(false);
		txtPacienteActa.setText("Toby (Canino · Pug)");
		txtProcedimientoActa = crearCampoTexto(false);
		txtProcedimientoActa.setText("Profilaxis Dental + Extracción");
		fila1.add(crearFilaCampo("Paciente en Quirófano", txtPacienteActa));
		fila1.add(crearFilaCampo("Procedimiento Realizado", txtProcedimientoActa));
		form.add(fila1);
		form.add(Box.createVerticalStrut(4));

		JPanel fila2 = new JPanel(new GridLayout(1, 2, 8, 0));
		fila2.setOpaque(false);
		txtCirujanoActa = crearCampoTexto(true);
		txtCirujanoActa.setText("Dra. Ana Silva");
		txtAnestesistaActa = crearCampoTexto(true);
		txtAnestesistaActa.setText("Lic. Marta Ferrer (Anestesista)");
		fila2.add(crearFilaCampo("Cirujano Principal", txtCirujanoActa));
		fila2.add(crearFilaCampo("Personal de Apoyo / Anestesia", txtAnestesistaActa));
		form.add(fila2);
		form.add(Box.createVerticalStrut(4));

		JPanel fila3 = new JPanel(new GridLayout(1, 2, 8, 0));
		fila3.setOpaque(false);
		cbAnestesiaActa = new JComboBox<>(
				new String[] { "Inhalatoria Isoflurano", "Intravenosa TIVA (Propofol)", "Sedación Profunda" });
		cbAnestesiaActa.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		cbAnestesiaActa.setBackground(Color.WHITE);
		txtHorariosReal = crearCampoTexto(true);
		txtHorariosReal.setText("10:15 - 11:40 (85 min)");
		fila3.add(crearFilaCampo("Tipo de Anestesia", cbAnestesiaActa));
		fila3.add(crearFilaCampo("Tiempos Quirúrgicos (Inicio - Fin)", txtHorariosReal));
		form.add(fila3);
		form.add(Box.createVerticalStrut(4));

		txtMedicamentosActa = new JTextArea(2, 20);
		txtMedicamentosActa.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		txtMedicamentosActa.setLineWrap(true);
		txtMedicamentosActa.setWrapStyleWord(true);
		txtMedicamentosActa.setText("Propofol 2mg/kg IV, Isoflurano 1.5%, Meloxicam 0.2mg/kg SC, Cefalexina 20mg/kg.");
		txtMedicamentosActa.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(COLOR_BORDE, 1),
				new EmptyBorder(3, 6, 3, 6)));
		form.add(crearFilaArea("Fármacos, Anestésicos e Insumos Quirúrgicos", txtMedicamentosActa, 42));
		form.add(Box.createVerticalStrut(4));

		JPanel filaPost = new JPanel(new GridLayout(1, 3, 6, 0));
		filaPost.setOpaque(false);
		cbEstadoAlta = new JComboBox<>(
				new String[] { "Alerta y estable", "En recuperación anestésica", "Hospitalizado en observación" });
		cbEstadoAlta.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		cbEstadoAlta.setBackground(Color.WHITE);

		cbEscalaDolor = new JComboBox<>(
				new String[] { "1 (Mínimo)", "2 (Leve)", "3 (Moderado bajo)", "4 (Moderado)", "5 (Severo)" });
		cbEscalaDolor.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		cbEscalaDolor.setBackground(Color.WHITE);

		txtFechaPuntos = crearCampoTexto(true);
		txtFechaPuntos.setText(LocalDate.now().plusDays(10).format(RegistroCirugia.FECHA_FORMATTER));

		filaPost.add(crearFilaCampo("Estado al Alta", cbEstadoAlta));
		filaPost.add(crearFilaCampo("Escala Dolor (1-10)", cbEscalaDolor));
		filaPost.add(crearFilaCampo("Retiro de Puntos", txtFechaPuntos));
		form.add(filaPost);
		form.add(Box.createVerticalStrut(4));

		txtIndicacionesTutor = new JTextArea(2, 20);
		txtIndicacionesTutor.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		txtIndicacionesTutor.setLineWrap(true);
		txtIndicacionesTutor.setWrapStyleWord(true);
		txtIndicacionesTutor.setText(
				"Uso permanente de collar isabelino por 10 días. Limpieza diaria de la herida con solución antiséptica. Reposo absoluto.");
		txtIndicacionesTutor.setBorder(BorderFactory
				.createCompoundBorder(BorderFactory.createLineBorder(COLOR_BORDE, 1), new EmptyBorder(3, 6, 3, 6)));
		form.add(crearFilaArea("Indicaciones Postoperatorias para el Tutor", txtIndicacionesTutor, 42));
		form.add(Box.createVerticalStrut(8));

		JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
		btnRow.setOpaque(false);

		JButton btnImprimir =
		        crearBotonWeb(
		                "Protocolo Quirúrgico",
		                Iconos.crearIconoImprimir(
		                        15,
		                        new Color(51, 65, 85)
		                ),
		                false,
		                () -> imprimirProtocoloQuirurgico()
		        );
		JButton btnGuardarActa =
		        crearBotonWeb(
		                "Guardar y Finalizar",
		                Iconos.crearIconoGuardar(
		                        15,
		                        Color.WHITE
		                ),
		                true,
		                () -> guardarProtocoloActual()
		        );
		btnRow.add(btnImprimir);
		btnRow.add(btnGuardarActa);
		form.add(btnRow);

		card.add(form, BorderLayout.NORTH);
		return card;
	}

	private JPanel crearFilaCampo(String etiqueta, Component campo) {
		JPanel p = new JPanel(new BorderLayout(0, 2));
		p.setOpaque(false);
		JLabel lbl = new JLabel(etiqueta);
		lbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
		lbl.setForeground(new Color(71, 85, 105));
		p.add(lbl, BorderLayout.NORTH);
		happypets.ui.Ui.ajustarAlturaCampo(campo);
		p.add(campo, BorderLayout.CENTER);
		return p;
	}

	private JPanel crearFilaArea(String etiqueta, JTextArea area, int altura) {
		JPanel p = new JPanel(new BorderLayout(0, 2));
		p.setOpaque(false);
		JLabel lbl = new JLabel(etiqueta);
		lbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
		lbl.setForeground(new Color(71, 85, 105));
		p.add(lbl, BorderLayout.NORTH);

		JScrollPane sp = new JScrollPane(area);
		sp.setPreferredSize(new Dimension(0, altura));
		sp.setBorder(null);
		p.add(sp, BorderLayout.CENTER);
		return p;
	}

	private JTextField crearCampoTexto(boolean editable) {
		JTextField tf = new JTextField();
		tf.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		tf.setPreferredSize(new Dimension(0, 26));
		tf.setEditable(editable);
		tf.setBackground(editable ? Color.WHITE : new Color(241, 245, 249));
		tf.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(COLOR_BORDE, 1),
				new EmptyBorder(2, 6, 2, 6)));
		return tf;
	}

	private JButton crearBotonWeb(String texto, javax.swing.Icon icono, boolean primario, Runnable accion) {

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

				g2.dispose();

				super.paintComponent(g);
			}
		};

		btn.setIcon(icono);

		btn.setIconTextGap(7);

		btn.setFont(new Font("Segoe UI", Font.BOLD, 11));

		btn.setForeground(primario ? Color.WHITE : new Color(51, 65, 85));

		btn.setFocusPainted(false);
		btn.setContentAreaFilled(false);
		btn.setOpaque(false);

		btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		btn.setBorder(new EmptyBorder(5, 12, 5, 12));

		btn.setPreferredSize(new Dimension(btn.getPreferredSize().width, 30));

		btn.addActionListener(e -> accion.run());

		return btn;
	}

	public void recargarDatos() {
		if (cbPacienteProg.getItemCount() == 0) {
			for (Mascota m : repo.todasLasMascotas()) {
				cbPacienteProg.addItem(new PacienteItem(m));
			}
			autocompletarTutorProg();
		}

		listaActual = repo.getCirugias();
		modeloQuirofano.setRowCount(0);

		for (RegistroCirugia c : listaActual) {
			modeloQuirofano.addRow(new Object[] { c.getIdCirugia(), c.getQuirofano(), c.getNombreMascota(),
					c.getNombreTutor(), c.getProcedimiento(), c.getHoraProgramadaFormateada() + " hrs",
					c.getCirujanoPrincipal(), c.getTipoAnestesia(), c.getEstado() });
		}

		lblContadorCirugias.setText(listaActual.size() + " cirugías en control quirúrgico");

		// KPIs
		long proc = listaActual.stream().filter(c -> "En Proceso".equalsIgnoreCase(c.getEstado())).count();
		long recup = listaActual.stream().filter(c -> "En Recuperación".equalsIgnoreCase(c.getEstado())
				|| "Alta Quirúrgica".equalsIgnoreCase(c.getEstado())).count();
		lblKpiCirugiasHoy.setText(String.valueOf(listaActual.size()));
		lblKpiEnProcedimiento.setText(String.valueOf(proc));
		lblKpiEnRecuperacion.setText(String.valueOf(recup));
	}

	private void autocompletarTutorProg() {
		PacienteItem item = (PacienteItem) cbPacienteProg.getSelectedItem();
		if (item != null && item.mascota != null) {
			repo.getClienteDeMascota(item.mascota.getCodigo()).ifPresent(c -> {
				txtTutorProg.setText(c.getNombreCompleto() + " (" + c.getTelefonoPrincipal() + ")");
			});
		}
	}

	private void programarNuevaCirugia() {
		PacienteItem pi = (PacienteItem) cbPacienteProg.getSelectedItem();
		if (pi == null || pi.mascota == null)
			return;
		Mascota m = pi.mascota;
		Optional<Cliente> optC = repo.getClienteDeMascota(m.getCodigo());

		LocalTime hora = LocalTime.of(9, 0);
		try {
			String[] partes = txtHoraProg.getText().trim().split(":");
			hora = LocalTime.of(Integer.parseInt(partes[0].trim()), Integer.parseInt(partes[1].trim()));
		} catch (Exception ex) { JOptionPane.showMessageDialog(this, "Revise los datos numéricos, fechas y horas: " + ex.getMessage(), "Validación", JOptionPane.WARNING_MESSAGE); return; }

		RegistroCirugia c = new RegistroCirugia(null, m.getCodigo(), m.getNombre(),
				m.getEspecie() + " · " + m.getRaza(), optC.map(Cliente::getNombreCompleto).orElse("Tutor"),
				optC.map(Cliente::getTelefonoPrincipal).orElse(""), (String) cbProcedimientoProg.getSelectedItem(),
				(String) cbQuirofanoProg.getSelectedItem(), txtCirujanoProg.getText().trim(),
				"Lic. Marta Ferrer (Anestesista)", LocalDate.now(), hora, null, null, "Inhalatoria Isoflurano",
				"Protocolo anestésico estándar prequirúrgico",
				"Cirugía programada conforme a evaluación preanestésica.", 0, "Pendiente",
				"Ayuno de 12 horas previo a cirugía.", LocalDate.now().plusDays(10), "Programada");

		repo.guardarCirugia(c);
		JOptionPane.showMessageDialog(this,
				"Cirugía " + c.getIdCirugia() + " programada con éxito para " + m.getNombre() + " en "
						+ c.getQuirofano() + " a las " + c.getHoraProgramadaFormateada() + " hrs.",
				"Cirugía Programada", JOptionPane.INFORMATION_MESSAGE);

		recargarDatos();
	}

	private void cargarCirugiaSeleccionada() {
		int row = tablaQuirofano.getSelectedRow();
		if (row < 0 || row >= listaActual.size())
			return;
		RegistroCirugia c = listaActual.get(row);

		txtPacienteActa.setText(c.getNombreMascota() + " (" + c.getEspecieRaza() + ")");
		txtProcedimientoActa.setText(c.getProcedimiento());
		txtCirujanoActa.setText(c.getCirujanoPrincipal());
		txtAnestesistaActa.setText(c.getPersonalApoyo());
		txtHorariosReal.setText(c.getHoraInicioFormateada() + " - " + c.getHoraFinFormateada());
		cbAnestesiaActa.setSelectedItem(c.getTipoAnestesia());
		txtMedicamentosActa.setText(c.getMedicamentosInsumos());
		cbEstadoAlta.setSelectedItem(c.getEstadoGeneralAlta());
		txtIndicacionesTutor.setText(c.getIndicacionesPostop());
		txtFechaPuntos.setText(c.getFechaRetiroPuntosFormateada());
	}

	private void cambiarEstadoSeleccionado(String nuevoEstado) {
		int row = tablaQuirofano.getSelectedRow();
		if (row < 0 || row >= listaActual.size()) {
			JOptionPane.showMessageDialog(this, "Seleccione una cirugía de la tabla para cambiar su estado.", "Aviso",
					JOptionPane.WARNING_MESSAGE);
			return;
		}
		RegistroCirugia c = listaActual.get(row);
		c.setEstado(nuevoEstado);
		if ("En Proceso".equalsIgnoreCase(nuevoEstado) && c.getHoraInicioReal() == null) {
			c.setHoraInicioReal(LocalTime.now());
		}
		if ("En Recuperación".equalsIgnoreCase(nuevoEstado) && c.getHoraFinReal() == null) {
			c.setHoraFinReal(LocalTime.now());
		}
		recargarDatos();
		cargarCirugiaSeleccionada();

		JOptionPane
				.showMessageDialog(this,
						"La cirugía de " + c.getNombreMascota() + " (" + c.getProcedimiento()
								+ ") ahora está en estado: " + nuevoEstado,
						"Estado Quirúrgico Actualizado", JOptionPane.INFORMATION_MESSAGE);
	}

	private void guardarProtocoloActual() {
		int row = tablaQuirofano.getSelectedRow();
		if (row >= 0 && row < listaActual.size()) {
			RegistroCirugia c = listaActual.get(row);
			c.setCirujanoPrincipal(txtCirujanoActa.getText().trim());
			c.setPersonalApoyo(txtAnestesistaActa.getText().trim());
			c.setTipoAnestesia((String) cbAnestesiaActa.getSelectedItem());
			c.setMedicamentosInsumos(txtMedicamentosActa.getText().trim());
			c.setEstadoGeneralAlta((String) cbEstadoAlta.getSelectedItem());
			c.setIndicacionesPostop(txtIndicacionesTutor.getText().trim());
			recargarDatos();
		}

		JOptionPane.showMessageDialog(this,
				"Protocolo operatorio y seguimiento postquirúrgico guardado correctamente en la historia clínica del paciente.",
				"Acta Quirúrgica Guardada", JOptionPane.INFORMATION_MESSAGE);
	}

	private void imprimirProtocoloQuirurgico() {
		int row = tablaQuirofano.getSelectedRow();
		if (row < 0 || row >= listaActual.size()) {
			JOptionPane.showMessageDialog(this, "Seleccione una cirugía para imprimir su protocolo.", "Aviso",
					JOptionPane.WARNING_MESSAGE);
			return;
		}
		RegistroCirugia c = listaActual.get(row);

		String acta = "╔════════════════════════════════════════════════════════════════╗\n"
				+ "║       VETERINARIA HAPPY PETS · PROTOCOLO QUIRÚRGICO            ║\n"
				+ "║            Centro Quirúrgico Veterinario Avanzado              ║\n"
				+ "╠════════════════════════════════════════════════════════════════╣\n" + " Código Cirugía: "
				+ c.getIdCirugia() + "    Fecha: " + c.getFechaFormateada() + "\n" + " Paciente: "
				+ c.getNombreMascota() + " (" + c.getEspecieRaza() + ")\n" + " Tutor: " + c.getNombreTutor()
				+ "  |  Tel: " + c.getTelefonoTutor() + "\n" + " Procedimiento: " + c.getProcedimiento() + "\n"
				+ " Quirófano: " + c.getQuirofano() + "\n"
				+ "────────────────────────────────────────────────────────────────\n"
				+ " EQUIPO QUIRÚRGICO Y ANESTESIA:\n" + " • Cirujano: " + c.getCirujanoPrincipal() + "\n"
				+ " • Anestesista: " + c.getPersonalApoyo() + "\n" + " • Protocolo Anestésico: " + c.getTipoAnestesia()
				+ "\n" + " • Insumos / Fármacos: " + c.getMedicamentosInsumos() + "\n" + " • Horario Operatorio: "
				+ c.getHoraInicioFormateada() + " a " + c.getHoraFinFormateada() + "\n"
				+ "────────────────────────────────────────────────────────────────\n"
				+ " SEGUIMIENTO POSTOPERATORIO:\n" + " • Estado al alta: " + c.getEstadoGeneralAlta() + "\n"
				+ " • Cuidados tutor: " + c.getIndicacionesPostop() + "\n" + " • Fecha de retiro de puntos: "
				+ c.getFechaRetiroPuntosFormateada() + "\n"
				+ "╚════════════════════════════════════════════════════════════════╝";

		JOptionPane.showMessageDialog(this, acta, "Protocolo Quirúrgico Impreso - Happy Pets",
				JOptionPane.INFORMATION_MESSAGE);
	}

	private static class PacienteItem {
		final Mascota mascota;

		PacienteItem(Mascota mascota) {
			this.mascota = mascota;
		}

		@Override
		public String toString() {
			return mascota != null
					? mascota.getCodigo() + " - " + mascota.getNombre() + " (" + mascota.getEspecie() + " · "
							+ mascota.getRaza() + ")"
					: "-";
		}
	}

	private static class BadgeEstadoCirugiaRenderer extends DefaultTableCellRenderer {
		private static final long serialVersionUID = 1L;

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
			JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			lbl.setHorizontalAlignment(SwingConstants.CENTER);
			lbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
			String st = String.valueOf(value);
			if (!isSelected) {
				if ("Programada".equalsIgnoreCase(st)) {
					lbl.setForeground(new Color(2, 132, 199));
				} else if ("En Proceso".equalsIgnoreCase(st)) {
					lbl.setForeground(new Color(217, 119, 6));
				} else if ("En Recuperación".equalsIgnoreCase(st)) {
					lbl.setForeground(new Color(147, 51, 234));
				} else if ("Alta Quirúrgica".equalsIgnoreCase(st)) {
					lbl.setForeground(new Color(22, 163, 74));
				}
			}
			return lbl;
		}
	}
}
