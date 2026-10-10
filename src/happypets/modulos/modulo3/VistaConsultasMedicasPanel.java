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
import happypets.model.AtencionMedica;
import happypets.model.Cliente;
import happypets.model.Mascota;
import happypets.ui.Iconos;
import happypets.ui.Ui;

/**
 * Submódulo 3.1: Consultas Médicas Veterinarias. Implementa el flujo clínico
 * estructurado del wireframe (Págs 3 y 4): 1. Búsqueda y selección de paciente
 * con carga de antecedentes. 2. Anamnesis y toma de constantes vitales (Peso,
 * Temp, FC). 3. Evaluación física detallada y notas de alergias. 4. Diagnóstico
 * presuntivo / definitivo y tratamiento clínico. 5. Prescripción de recetas con
 * posología e indicaciones para el tutor. 6. Historial de consultas con
 * visualizador e impresión de receta.
 */
public class VistaConsultasMedicasPanel extends happypets.ui.AssetsModulo {
	private static final long serialVersionUID = 1L;

	private static final Color COLOR_BORDE = new Color(226, 232, 240);
	private static final Color COLOR_AZUL_PRIMARIO = new Color(2, 132, 199);
	private static final Color COLOR_TEXTO_TITULO = new Color(30, 41, 59);
	private static final Color COLOR_TEXTO_MUTED = new Color(100, 116, 139);

	private final RepositorioVeterinaria repo = RepositorioVeterinaria.getInstancia();

	// KPIs
	private JLabel lblKpiConsultasHoy;
	private JLabel lblKpiUrgencias;
	private JLabel lblKpiRecetas;
	private JLabel lblKpiEnSeguimiento;

	// Formulario de Atención Clínica
	private JComboBox<PacienteItem> cbPaciente;
	private JTextField txtTutor;
	private JTextField txtTelefono;
	private JTextField txtVeterinario;
	private JTextField txtMotivo;
	private JTextField txtPeso;
	private JTextField txtTemperatura;
	private JTextField txtFC;
	private JTextArea txtEvaluacion;
	private JTextField txtAlergias;
	private JTextField txtDiagPresuntivo;
	private JTextField txtDiagDefinitivo;
	private JTextArea txtTratamiento;
	private JTextArea txtReceta;
	private JTextArea txtIndicaciones;
	private JTextField txtSeguimiento;
	private JTextField txtCosto;
	private JComboBox<String> cbEstado;

	// Tabla de Consultas
	private JTable tablaConsultas;
	private DefaultTableModel modeloConsultas;
	private JTextField txtBuscarConsulta;
	private JLabel lblContadorConsultas;
	private List<AtencionMedica> listaActual;

	public VistaConsultasMedicasPanel() {
		setLayout(new BorderLayout());
		setOpaque(false);

		JPanel contenido = new JPanel();
		contenido.setOpaque(false);
		contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
		contenido.setBorder(new EmptyBorder(12, 18, 14, 18));

		// 1. Cabecera con título e indicador de box
		contenido.add(crearCabeceraVista());
		contenido.add(Box.createVerticalStrut(8));

		// 2. Flujo de Atención Clínica (Step Tracker visual del wireframe)
		contenido.add(crearStepTrackerClinico());
		contenido.add(Box.createVerticalStrut(10));

		// 3. Fila de KPIs
		contenido.add(crearFilaKpis());
		contenido.add(Box.createVerticalStrut(10));

		// 4. Doble columna: Formulario Clínico Integrado y Tabla/Antecedentes
		contenido.add(crearDobleColumna());

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

		JLabel titulo = new JLabel("Consultas Médicas y Atenciones Veterinarias");
		titulo.setFont(new Font("Segoe UI", Font.BOLD, 17));
		titulo.setForeground(COLOR_TEXTO_TITULO);

		JLabel subtitulo = new JLabel(
				"Módulo 3.1 · Registro clínico, exploración física, constantes, diagnóstico y prescripción");
		subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		subtitulo.setForeground(COLOR_TEXTO_MUTED);

		izq.add(titulo);
		izq.add(Box.createVerticalStrut(2));
		izq.add(subtitulo);
		cab.add(izq, BorderLayout.CENTER);

		JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
		der.setOpaque(false);

		JLabel boxBadge = new JLabel(" Box de Consulta: #01 (Dr. Mendoza) ", SwingConstants.CENTER);
		boxBadge.setFont(new Font("Segoe UI", Font.BOLD, 11));
		boxBadge.setForeground(new Color(3, 105, 161));
		boxBadge.setOpaque(true);
		boxBadge.setBackground(new Color(224, 242, 254));
		boxBadge.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(new Color(186, 230, 253), 1, true), new EmptyBorder(4, 10, 4, 10)));
		der.add(boxBadge);

		JButton btnNueva = crearBotonWeb("Nueva Atención", Iconos.crearIconoCruzMedica(15, Color.WHITE), true,
				() -> limpiarFormulario());
		der.add(btnNueva);

		cab.add(der, BorderLayout.EAST);
		return cab;
	}

	private JPanel crearStepTrackerClinico() {
		JPanel tracker = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 6));
		tracker.setBackground(Color.WHITE);
		tracker.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(COLOR_BORDE, 1, true),
				new EmptyBorder(4, 12, 4, 12)));

		JLabel lblGuia = new JLabel("Flujo Operativo Clínico:");
		lblGuia.setFont(new Font("Segoe UI", Font.BOLD, 11));
		lblGuia.setForeground(COLOR_TEXTO_MUTED);
		tracker.add(lblGuia);

		String[] pasos = { "1. Seleccionar Paciente", "2. Revisar Antecedentes", "3. Evaluación Clínica",
				"4. Diagnóstico y Tratamiento", "5. Receta y Guardado" };

		for (int i = 0; i < pasos.length; i++) {
			JLabel step = new JLabel(" " + pasos[i] + " ");
			step.setFont(new Font("Segoe UI", i == 2 ? Font.BOLD : Font.PLAIN, 11));
			step.setForeground(i <= 2 ? COLOR_AZUL_PRIMARIO : COLOR_TEXTO_MUTED);
			step.setOpaque(i == 2);
			if (i == 2) {
				step.setBackground(new Color(224, 242, 254));
				step.setBorder(BorderFactory.createLineBorder(new Color(186, 230, 253), 1, true));
			}
			tracker.add(step);
			if (i < pasos.length - 1) {
				JLabel flecha = new JLabel("→");
				flecha.setFont(new Font("Segoe UI", Font.BOLD, 11));
				flecha.setForeground(new Color(203, 213, 225));
				tracker.add(flecha);
			}
		}
		return tracker;
	}

	private JPanel crearFilaKpis() {
		JPanel fila = new JPanel(new GridLayout(1, 4, 12, 0));
		fila.setOpaque(false);

		lblKpiConsultasHoy = new JLabel("3", SwingConstants.LEFT);
		lblKpiUrgencias = new JLabel("1", SwingConstants.LEFT);
		lblKpiRecetas = new JLabel("3", SwingConstants.LEFT);
		lblKpiEnSeguimiento = new JLabel("2", SwingConstants.LEFT);

		fila.add(crearCardKpi(lblKpiConsultasHoy, "CONSULTAS REGISTRADAS", new Color(224, 242, 254),
				COLOR_AZUL_PRIMARIO, Iconos.crearIconoEstetoscopio(20, COLOR_AZUL_PRIMARIO)));
		fila.add(crearCardKpi(lblKpiUrgencias, "CASOS DE URGENCIA", new Color(254, 226, 226), new Color(220, 38, 38),
				Iconos.crearIconoAlertaTriaje(20, new Color(220, 38, 38))));
		fila.add(crearCardKpi(lblKpiRecetas, "RECETAS PRESCRITAS", new Color(243, 232, 255), new Color(147, 51, 234),
				Iconos.crearIconoFactura(20, new Color(147, 51, 234))));
		fila.add(crearCardKpi(lblKpiEnSeguimiento, "CONTROLES PENDIENTES", new Color(254, 243, 199),
				new Color(217, 119, 6), Iconos.crearIconoReloj(20, new Color(217, 119, 6))));

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

	private JPanel crearDobleColumna() {
		JPanel panel = new JPanel(new BorderLayout(14, 0));
		panel.setOpaque(false);

		// Columna Izquierda: Formulario de Evaluación y Prescripción
		panel.add(crearCardFormularioClinico(), BorderLayout.WEST);

		// Columna Derecha: Antecedentes e Historial de Atenciones
		panel.add(crearCardTablaConsultas(), BorderLayout.CENTER);

		return panel;
	}

	private JPanel crearCardFormularioClinico() {
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

		JLabel titSec = new JLabel("1. Registro de Atención Clínica y Examen");
		titSec.setFont(new Font("Segoe UI", Font.BOLD, 13));
		titSec.setForeground(COLOR_TEXTO_TITULO);
		form.add(titSec);
		form.add(Box.createVerticalStrut(8));

		// Selector de paciente
		cbPaciente = new JComboBox<>();
		cbPaciente.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		cbPaciente.setBackground(Color.WHITE);
		cbPaciente.setPreferredSize(new Dimension(0, 26));
		cbPaciente.addActionListener(e -> autocompletarDatosPaciente());
		form.add(crearFilaCampo("Buscar Paciente (Nombre / Código) *", cbPaciente));
		form.add(Box.createVerticalStrut(4));

		// Tutor y teléfono
		JPanel filaTutor = new JPanel(new GridLayout(1, 2, 8, 0));
		filaTutor.setOpaque(false);
		txtTutor = crearCampoTexto(false);
		txtTelefono = crearCampoTexto(false);
		filaTutor.add(crearFilaCampo("Propietario / Tutor", txtTutor));
		filaTutor.add(crearFilaCampo("Teléfono Contacto", txtTelefono));
		form.add(filaTutor);
		form.add(Box.createVerticalStrut(4));

		// Veterinario a cargo y Motivo
		txtVeterinario = crearCampoTexto(true);
		txtVeterinario.setText("Dra. Camila Morales");
		txtMotivo = crearCampoTexto(true);
		txtMotivo.setText("Decaimiento general, letargia y vómito recurrente");
		form.add(crearFilaCampo("Veterinario Tratante", txtVeterinario));
		form.add(Box.createVerticalStrut(4));
		form.add(crearFilaCampo("Motivo de la Consulta *", txtMotivo));
		form.add(Box.createVerticalStrut(4));

		// Constantes Vitales (Triada clínica: Peso, Temp, FC)
		JPanel filaConstantes = new JPanel(new GridLayout(1, 3, 6, 0));
		filaConstantes.setOpaque(false);
		txtPeso = crearCampoTexto(true);
		txtPeso.setText("28.4");
		txtTemperatura = crearCampoTexto(true);
		txtTemperatura.setText("39.2");
		txtFC = crearCampoTexto(true);
		txtFC.setText("110");
		filaConstantes.add(crearFilaCampo("Peso (kg) *", txtPeso));
		filaConstantes.add(crearFilaCampo("Temp (°C) *", txtTemperatura));
		filaConstantes.add(crearFilaCampo("FC (lpm) *", txtFC));
		form.add(filaConstantes);
		form.add(Box.createVerticalStrut(4));

		// Evaluación clínica
		txtEvaluacion = new JTextArea(3, 20);
		txtEvaluacion.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		txtEvaluacion.setLineWrap(true);
		txtEvaluacion.setWrapStyleWord(true);
		txtEvaluacion.setText(
				"Paciente letárgico, mucosas ligeramente pálidas, dolor a la palpación abdominal media. Sonidos pulmonares limpios. Deshidratación leve ~5%.");
		txtEvaluacion.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(COLOR_BORDE, 1),
				new EmptyBorder(4, 6, 4, 6)));
		form.add(crearFilaArea("Evaluación Física / Anamnesis", txtEvaluacion, 55));
		form.add(Box.createVerticalStrut(4));

		// Alergias / Advertencias
		txtAlergias = crearCampoTexto(true);
		txtAlergias.setText("Sensibilidad a penicilinas reportada en libreta sanitaria");
		txtAlergias.setForeground(new Color(185, 28, 28));
		form.add(crearFilaCampo("Notas de Alergias / Alertas Clínicas", txtAlergias));
		form.add(Box.createVerticalStrut(8));

		JLabel titSec2 = new JLabel("2. Diagnóstico y Plan Terapéutico");
		titSec2.setFont(new Font("Segoe UI", Font.BOLD, 13));
		titSec2.setForeground(COLOR_TEXTO_TITULO);
		form.add(titSec2);
		form.add(Box.createVerticalStrut(6));

		// Diagnósticos
		txtDiagPresuntivo = crearCampoTexto(true);
		txtDiagPresuntivo.setText("Gastroenteritis aguda / Sospecha de cuerpo extraño");
		txtDiagDefinitivo = crearCampoTexto(true);
		txtDiagDefinitivo.setText("Gastroenteritis aguda de origen dietario");
		form.add(crearFilaCampo("Diagnóstico Presuntivo", txtDiagPresuntivo));
		form.add(Box.createVerticalStrut(4));
		form.add(crearFilaCampo("Diagnóstico Definitivo", txtDiagDefinitivo));

		form.add(Box.createVerticalStrut(4));

		// =====================================================
		// TRATAMIENTO / PLAN TERAPÉUTICO
		// =====================================================

		txtTratamiento = new JTextArea(2, 20);

		txtTratamiento.setFont(new Font("Segoe UI", Font.PLAIN, 11));

		txtTratamiento.setLineWrap(true);
		txtTratamiento.setWrapStyleWord(true);

		txtTratamiento
				.setText("Fluidoterapia de mantenimiento, control de náuseas " + "y protección gastrointestinal.");

		txtTratamiento.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(COLOR_BORDE, 1),
				new EmptyBorder(4, 6, 4, 6)));

		form.add(crearFilaArea("Tratamiento Aplicado / Plan Terapéutico", txtTratamiento, 48));

		form.add(Box.createVerticalStrut(4));

		// Receta médica y posología
		txtReceta = new JTextArea(3, 20);
		txtReceta.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		txtReceta.setLineWrap(true);
		txtReceta.setWrapStyleWord(true);
		txtReceta.setText(
				"1. Cerenia (Maropitant) 24mg: 1/2 comp c/24h x 3 días\n2. Sucralfato susp. 1g/5ml: 3ml c/8h antes de alimento x 5 días\n3. Probiótico Canino: 1 sobre c/24h x 7 días");
		txtReceta.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(COLOR_BORDE, 1),
				new EmptyBorder(4, 6, 4, 6)));
		form.add(crearFilaArea("Prescripción de Medicamentos y Posología", txtReceta, 60));
		form.add(Box.createVerticalStrut(4));

		// Indicaciones tutor y seguimiento
		txtIndicaciones = new JTextArea(2, 20);
		txtIndicaciones.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		txtIndicaciones.setLineWrap(true);
		txtIndicaciones.setWrapStyleWord(true);
		txtIndicaciones.setText(
				"Ayuno de sólidos por 12 horas. Ofrecer agua en pequeños sorbos. Dieta blanda (pollo hervido con arroz blanco en 4 tomas diarias).");
		txtIndicaciones.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(COLOR_BORDE, 1),
				new EmptyBorder(4, 6, 4, 6)));
		form.add(crearFilaArea("Indicaciones de Cuidado en Casa para el Tutor", txtIndicaciones, 46));
		form.add(Box.createVerticalStrut(4));

		JPanel filaFinal = new JPanel(new GridLayout(1, 3, 6, 0));
		filaFinal.setOpaque(false);
		txtSeguimiento = crearCampoTexto(true);
		txtSeguimiento.setText("Control en 48 horas");
		txtCosto = crearCampoTexto(true);
		txtCosto.setText("120.00");
		cbEstado = new JComboBox<>(
				new String[] { "Completada", "En Evaluación", "Derivada a Quirófano", "Hospitalizada" });
		cbEstado.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		cbEstado.setBackground(Color.WHITE);

		filaFinal.add(crearFilaCampo("Próximo Control", txtSeguimiento));
		filaFinal.add(crearFilaCampo("Costo (S/.)", txtCosto));
		filaFinal.add(crearFilaCampo("Estado Final", cbEstado));
		form.add(filaFinal);
		form.add(Box.createVerticalStrut(10));

		// Botones de acción
		JPanel filaBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
		filaBotones.setOpaque(false);

		JButton btnImprimir = crearBotonWeb("Imprimir Receta", Iconos.crearIconoImprimir(15, new Color(51, 65, 85)),
				false, () -> imprimirRecetaActual());
		JButton btnGuardar = crearBotonWeb("Guardar Consulta", Iconos.crearIconoGuardar(15, Color.WHITE), true,
				() -> guardarConsultaActual());
		filaBotones.add(btnImprimir);
		filaBotones.add(btnGuardar);
		form.add(filaBotones);

		card.add(form, BorderLayout.NORTH);
		return card;
	}

	private JPanel crearCardTablaConsultas() {
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

		JPanel titBox = new JPanel();
		titBox.setOpaque(false);
		titBox.setLayout(new BoxLayout(titBox, BoxLayout.Y_AXIS));

		JLabel tit = new JLabel("Historial de Consultas y Atenciones Clínicas");
		tit.setFont(new Font("Segoe UI", Font.BOLD, 13));
		tit.setForeground(COLOR_TEXTO_TITULO);

		lblContadorConsultas = new JLabel("Cargando atenciones...");
		lblContadorConsultas.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		lblContadorConsultas.setForeground(COLOR_TEXTO_MUTED);

		titBox.add(tit);
		titBox.add(lblContadorConsultas);
		top.add(titBox, BorderLayout.WEST);

		// Barra de búsqueda rápida
		JPanel searchBox = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
		searchBox.setOpaque(false);

		txtBuscarConsulta = new JTextField(16);
		txtBuscarConsulta.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		txtBuscarConsulta.setPreferredSize(new Dimension(180, 26));
		txtBuscarConsulta.putClientProperty("JTextField.placeholderText", "Buscar paciente o diagnóstico...");
		txtBuscarConsulta.addActionListener(e -> recargarDatos());

		JButton btnBuscar = crearBotonWeb("Buscar", Iconos.crearIconoBuscar(15, new Color(51, 65, 85)), false,
				() -> recargarDatos());
		searchBox.add(txtBuscarConsulta);
		searchBox.add(btnBuscar);
		top.add(searchBox, BorderLayout.EAST);

		card.add(top, BorderLayout.NORTH);

		// Tabla
		String[] columnas = { "Código", "Fecha", "Paciente", "Tutor", "Motivo", "Diagnóstico", "Veterinario",
				"Estado" };
		modeloConsultas = new DefaultTableModel(columnas, 0) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int r, int c) {
				return false;
			}
		};

		tablaConsultas = new JTable(modeloConsultas);
		Ui.formatearTabla(tablaConsultas);
		tablaConsultas.setRowHeight(30);

		tablaConsultas.getColumnModel().getColumn(0).setPreferredWidth(85);
		tablaConsultas.getColumnModel().getColumn(1).setPreferredWidth(75);
		tablaConsultas.getColumnModel().getColumn(2).setPreferredWidth(95);
		tablaConsultas.getColumnModel().getColumn(7).setCellRenderer(new BadgeEstadoConsultaRenderer());

		tablaConsultas.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					cargarConsultaSeleccionadaEnFormulario();
				}
			}
		});

		JScrollPane sp = new JScrollPane(tablaConsultas);
		sp.setBorder(BorderFactory.createLineBorder(COLOR_BORDE, 1));
		sp.setPreferredSize(new Dimension(650, 420));
		card.add(sp, BorderLayout.CENTER);

		// Botonera debajo de tabla
		JPanel bot = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
		bot.setOpaque(false);

		JButton btnVerFicha = crearBotonWeb("Ver Detalle Completo", Iconos.crearIconoOjo(15, new Color(37, 99, 235)),
				false, () -> verDetalleConsulta());
		JButton btnCargar = crearBotonWeb("Cargar en Formulario",
				Iconos.crearIconoFormulario(15, new Color(51, 65, 85)), false,
				() -> cargarConsultaSeleccionadaEnFormulario());
		bot.add(btnVerFicha);
		bot.add(btnCargar);
		card.add(bot, BorderLayout.SOUTH);

		return card;
	}

	private JPanel crearFilaCampo(String etiqueta, Component campo) {
		JPanel p = new JPanel(new BorderLayout(0, 2));
		p.setOpaque(false);
		JLabel lbl = new JLabel(etiqueta);
		lbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
		lbl.setForeground(new Color(71, 85, 105));
		p.add(lbl, BorderLayout.NORTH);
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

	private JButton crearBotonWeb(String texto, Icon icono, boolean primario, Runnable accion) {

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
		if (cbPaciente.getItemCount() == 0) {
			for (Mascota m : repo.todasLasMascotas()) {
				cbPaciente.addItem(new PacienteItem(m));
			}
			autocompletarDatosPaciente();
		}

		List<AtencionMedica> todas = repo.getAtencionesMedicas();
		String q = txtBuscarConsulta != null ? txtBuscarConsulta.getText().trim().toLowerCase() : "";

		listaActual = todas.stream().filter(a -> {
			if (q.isEmpty())
				return true;
			return a.getNombreMascota().toLowerCase().contains(q) || a.getNombreTutor().toLowerCase().contains(q)
					|| a.getDiagnosticoDefinitivo().toLowerCase().contains(q)
					|| a.getIdConsulta().toLowerCase().contains(q) || a.getMotivoConsulta().toLowerCase().contains(q);
		}).toList();

		modeloConsultas.setRowCount(0);
		for (AtencionMedica a : listaActual) {
			modeloConsultas.addRow(
					new Object[] { a.getIdConsulta(), a.getFechaFormateada(), a.getNombreMascota(), a.getNombreTutor(),
							a.getMotivoConsulta(), a.getDiagnosticoDefinitivo().isEmpty() ? a.getDiagnosticoPresuntivo()
									: a.getDiagnosticoDefinitivo(),
							a.getVeterinario(), a.getEstado() });
		}

		lblContadorConsultas.setText(listaActual.size() + " atenciones registradas en el sistema");

		// KPIs
		long hoy = todas.stream().filter(a -> a.getFecha().equals(LocalDate.now())).count();
		long urg = todas.stream().filter(a -> a.getMotivoConsulta().toLowerCase().contains("vómito")
				|| a.getMotivoConsulta().toLowerCase().contains("urgencia")).count();
		long rec = todas.stream().filter(a -> a.getRecetaMedicamentos() != null && !a.getRecetaMedicamentos().isEmpty())
				.count();

		lblKpiConsultasHoy.setText(String.valueOf(hoy > 0 ? hoy : todas.size()));
		lblKpiUrgencias.setText(String.valueOf(urg));
		lblKpiRecetas.setText(String.valueOf(rec));
		lblKpiEnSeguimiento.setText("2");
	}

	private void autocompletarDatosPaciente() {
		PacienteItem item = (PacienteItem) cbPaciente.getSelectedItem();
		if (item != null && item.mascota != null) {
			Mascota m = item.mascota;
			Optional<Cliente> optC = repo.getClienteDeMascota(m.getCodigo());
			optC.ifPresent(c -> {
				txtTutor.setText(c.getNombreCompleto());
				txtTelefono.setText(c.getTelefonoPrincipal());
			});
			txtPeso.setText(String.valueOf(m.getPesoActualKg()));
			if (!m.getAlergias().equalsIgnoreCase("Ninguna conocida")) {
				txtAlergias.setText("ALERTA: " + m.getAlergias());
				txtAlergias.setForeground(new Color(220, 38, 38));
			} else {
				txtAlergias.setText("Sin alergias conocidas");
				txtAlergias.setForeground(new Color(22, 163, 74));
			}
		}
	}

	private void guardarConsultaActual() {
		PacienteItem item = (PacienteItem) cbPaciente.getSelectedItem();
		if (item == null || item.mascota == null) {
			JOptionPane.showMessageDialog(this, "Debe seleccionar un paciente para registrar la consulta.",
					"Validación", JOptionPane.WARNING_MESSAGE);
			return;
		}

		Mascota m = item.mascota;
		String motivo = txtMotivo.getText().trim();
		if (motivo.isEmpty()) {
			JOptionPane.showMessageDialog(this, "El motivo de la consulta es obligatorio.", "Validación",
					JOptionPane.WARNING_MESSAGE);
			return;
		}

		double peso = 10.0;
		double temp = 38.5;
		int fc = 100;
		double costo = 80.0;
		try {
			peso = Double.parseDouble(txtPeso.getText().trim());
		} catch (Exception ignored) {
		}
		try {
			temp = Double.parseDouble(txtTemperatura.getText().trim());
		} catch (Exception ignored) {
		}
		try {
			fc = Integer.parseInt(txtFC.getText().trim());
		} catch (Exception ignored) {
		}
		try {
			costo = Double.parseDouble(txtCosto.getText().trim());
		} catch (Exception ignored) {
		}

		AtencionMedica nueva = new AtencionMedica(null, m.getCodigo(), m.getNombre(),
				m.getEspecie() + " · " + m.getRaza(), "", txtTutor.getText().trim(), txtTelefono.getText().trim(),
				LocalDate.now(), LocalTime.now(), txtVeterinario.getText().trim(), motivo, peso, temp, fc,
				txtEvaluacion.getText().trim(), txtDiagPresuntivo.getText().trim(), txtDiagDefinitivo.getText().trim(),
				txtTratamiento.getText().trim(), txtReceta.getText().trim(), txtIndicaciones.getText().trim(),
				txtSeguimiento.getText().trim(), (String) cbEstado.getSelectedItem(), costo);

		repo.guardarAtencionMedica(nueva);

		JOptionPane.showMessageDialog(this,
				"Consulta Médica " + nueva.getIdConsulta() + " registrada exitosamente para " + nueva.getNombreMascota()
						+ ".\n" + "Historia clínica actualizada con diagnóstico: " + nueva.getDiagnosticoDefinitivo(),
				"Atención Médica Guardada", JOptionPane.INFORMATION_MESSAGE);

		recargarDatos();
	}

	private void cargarConsultaSeleccionadaEnFormulario() {
		int row = tablaConsultas.getSelectedRow();
		if (row < 0 || row >= listaActual.size()) {
			JOptionPane.showMessageDialog(this, "Seleccione una consulta de la tabla para cargar sus datos.", "Aviso",
					JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		AtencionMedica a = listaActual.get(row);

		// Seleccionar paciente en combo
		for (int i = 0; i < cbPaciente.getItemCount(); i++) {
			PacienteItem pi = cbPaciente.getItemAt(i);
			if (pi != null && pi.mascota != null && pi.mascota.getCodigo().equalsIgnoreCase(a.getCodigoMascota())) {
				cbPaciente.setSelectedIndex(i);
				break;
			}
		}

		txtTutor.setText(a.getNombreTutor());
		txtTelefono.setText(a.getTelefonoTutor());
		txtVeterinario.setText(a.getVeterinario());
		txtMotivo.setText(a.getMotivoConsulta());
		txtPeso.setText(String.valueOf(a.getPesoKg()));
		txtTemperatura.setText(String.valueOf(a.getTemperaturaC()));
		txtFC.setText(String.valueOf(a.getFrecuenciaCardiacaLpm()));
		txtEvaluacion.setText(a.getEvaluacionClinica());
		txtDiagPresuntivo.setText(a.getDiagnosticoPresuntivo());
		txtDiagDefinitivo.setText(a.getDiagnosticoDefinitivo());
		txtTratamiento.setText(a.getTratamientoIndicado());
		txtReceta.setText(a.getRecetaMedicamentos());
		txtIndicaciones.setText(a.getIndicacionesTutor());
		txtSeguimiento.setText(a.getSeguimiento());
		txtCosto.setText(String.format("%.2f", a.getCosto()));
		cbEstado.setSelectedItem(a.getEstado());
	}

	private void verDetalleConsulta() {
		int row = tablaConsultas.getSelectedRow();
		if (row < 0 || row >= listaActual.size()) {
			JOptionPane.showMessageDialog(this, "Seleccione una consulta para ver su detalle.", "Aviso",
					JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		AtencionMedica a = listaActual.get(row);

		String msg = "HISTORIA CLÍNICA Y REPORTE DE ATENCIÓN VETERINARIA\n"
				+ "═════════════════════════════════════════════════════\n" + "ID Consulta: " + a.getIdConsulta()
				+ "  |  Fecha: " + a.getFechaFormateada() + " " + a.getHoraFormateada() + " hrs\n" + "Paciente: "
				+ a.getNombreMascota() + " (" + a.getEspecieRaza() + ")\n" + "Propietario: " + a.getNombreTutor()
				+ "  |  Teléfono: " + a.getTelefonoTutor() + "\n" + "Veterinario Tratante: " + a.getVeterinario() + "\n"
				+ "-----------------------------------------------------\n" + "TRIADA CLÍNICA Y CONSTANTES:\n"
				+ " • Peso: " + a.getPesoKg() + " kg   • Temp: " + a.getTemperaturaC() + " °C   • FC: "
				+ a.getFrecuenciaCardiacaLpm() + " lpm\n\n" + "MOTIVO:\n" + a.getMotivoConsulta() + "\n\n"
				+ "EVALUACIÓN FÍSICA:\n" + a.getEvaluacionClinica() + "\n\n" + "DIAGNÓSTICO DEFINITIVO:\n"
				+ (a.getDiagnosticoDefinitivo().isEmpty() ? a.getDiagnosticoPresuntivo() : a.getDiagnosticoDefinitivo())
				+ "\n\n" + "TRATAMIENTO APLICADO EN CLÍNICA:\n" + a.getTratamientoIndicado() + "\n\n"
				+ "RECETA PARA CASA:\n" + a.getRecetaMedicamentos() + "\n\n" + "INDICACIONES PARA EL TUTOR:\n"
				+ a.getIndicacionesTutor() + "\n\n" + "SEGUIMIENTO: " + a.getSeguimiento() + "  |  Estado: "
				+ a.getEstado();

		JOptionPane.showMessageDialog(this, msg, "Detalle de Consulta - " + a.getIdConsulta(),
				JOptionPane.INFORMATION_MESSAGE);
	}

	private void imprimirRecetaActual() {
		PacienteItem pi = (PacienteItem) cbPaciente.getSelectedItem();
		String mascota = pi != null && pi.mascota != null ? pi.mascota.getNombre() : "Paciente";
		String receta = txtReceta.getText().trim();
		String indic = txtIndicaciones.getText().trim();

		if (receta.isEmpty()) {
			JOptionPane.showMessageDialog(this, "No hay medicamentos prescritos en la receta para imprimir.", "Aviso",
					JOptionPane.WARNING_MESSAGE);
			return;
		}

		String doc = "╔════════════════════════════════════════════════════════════════╗\n"
				+ "║           VETERINARIA HAPPY PETS · RECETA MÉDICA               ║\n"
				+ "║            Atención Médica Veterinaria Colegiada               ║\n"
				+ "╠════════════════════════════════════════════════════════════════╣\n" + " Fecha: "
				+ LocalDate.now().format(AtencionMedica.FECHA_FORMATTER) + "               Hora: "
				+ LocalTime.now().format(AtencionMedica.HORA_FORMATTER) + "\n" + " Paciente: " + mascota
				+ "        Propietario: " + txtTutor.getText() + "\n" + " Veterinario Tratante: "
				+ txtVeterinario.getText() + "\n" + " Diagnóstico: " + txtDiagDefinitivo.getText() + "\n"
				+ "────────────────────────────────────────────────────────────────\n"
				+ " RP./ PRESCRIPCIÓN FARMACOLÓGICA Y POSOLOGÍA:\n" + receta + "\n\n"
				+ "────────────────────────────────────────────────────────────────\n"
				+ " INDICACIONES DE CUIDADO Y DIETA:\n" + indic + "\n\n" + " Próxima cita de control: "
				+ txtSeguimiento.getText() + "\n"
				+ "╚════════════════════════════════════════════════════════════════╝";

		JOptionPane.showMessageDialog(this, doc, "Vista Previa de Receta Impresa - Happy Pets",
				JOptionPane.INFORMATION_MESSAGE);
	}

	private void limpiarFormulario() {
		txtMotivo.setText("");
		txtEvaluacion.setText("");
		txtDiagPresuntivo.setText("");
		txtDiagDefinitivo.setText("");
		txtTratamiento.setText("");
		txtReceta.setText("");
		txtIndicaciones.setText("");
		txtSeguimiento.setText("Control en 48 horas");
		txtCosto.setText("80.00");
		cbEstado.setSelectedIndex(0);
	}

	public void cargarMascota(Mascota m) {
		if (m == null)
			return;
		recargarDatos();
		for (int i = 0; i < cbPaciente.getItemCount(); i++) {
			PacienteItem item = cbPaciente.getItemAt(i);
			if (item != null && item.mascota != null && item.mascota.getCodigo().equalsIgnoreCase(m.getCodigo())) {
				cbPaciente.setSelectedIndex(i);
				autocompletarDatosPaciente();
				break;
			}
		}
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

	private static class BadgeEstadoConsultaRenderer extends DefaultTableCellRenderer {
		private static final long serialVersionUID = 1L;

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
			JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			lbl.setHorizontalAlignment(SwingConstants.CENTER);
			lbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
			String st = String.valueOf(value);
			if (!isSelected) {
				if ("Completada".equalsIgnoreCase(st)) {
					lbl.setForeground(new Color(22, 163, 74));
				} else if ("En Evaluación".equalsIgnoreCase(st)) {
					lbl.setForeground(new Color(2, 132, 199));
				} else if ("Derivada a Quirófano".equalsIgnoreCase(st)) {
					lbl.setForeground(new Color(147, 51, 234));
				} else if ("Hospitalizada".equalsIgnoreCase(st)) {
					lbl.setForeground(new Color(220, 38, 38));
				}
			}
			return lbl;
		}
	}
}
