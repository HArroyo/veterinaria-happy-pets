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
import happypets.model.OrdenLaboratorio;
import happypets.ui.Iconos;
import happypets.ui.Ui;

/**
 * Submódulo 3.4: Laboratorio Clínico y Diagnóstico por Imágenes. Diseñado según
 * el wireframe oficial (Pág. 6): 1. Nueva Solicitud (Órdenes diagnósticas para
 * laboratorio e imágenes). 2. Registro de Resultados analíticos (hemograma,
 * bioquímica, citología). 3. Visor e Informe de Imágenes Diagnósticas
 * (Radiología y Ecografía). 4. Historial clínico de estudios con filtros y
 * exportación.
 */
public class VistaLaboratorioImagenesPanel extends happypets.ui.AssetsModulo {
	private static final long serialVersionUID = 1L;

	private static final Color COLOR_BORDE = new Color(226, 232, 240);
	private static final Color COLOR_AZUL_PRIMARIO = new Color(2, 132, 199);
	private static final Color COLOR_TEXTO_TITULO = new Color(30, 41, 59);
	private static final Color COLOR_TEXTO_MUTED = new Color(100, 116, 139);

	private final RepositorioVeterinaria repo = RepositorioVeterinaria.getInstancia();

	// KPIs
	private JLabel lblKpiTotalOrdenes;
	private JLabel lblKpiLaboratorio;
	private JLabel lblKpiImagenes;
	private JLabel lblKpiCompletados;

	// Formulario Solicitud
	private JComboBox<PacienteItem> cbPacienteSol;
	private JTextField txtTutorSol;
	private JComboBox<String> cbCategoriaSol;
	private JComboBox<String> cbTipoEstudioSol;
	private JComboBox<String> cbPrioridadSol;
	private JTextField txtVetSol;
	private JTextField txtMotivoSol;

	// Formulario Resultado
	private JTextField txtPacienteRes;
	private JTextField txtEstudioRes;
	private JTextField txtResponsableRes;
	private JTextArea txtValoresRes;
	private JTextArea txtInterpretacionRes;
	private JComboBox<String> cbEstadoRes;

	// Visor de Imagen Diagnóstica
	private JPanel panelPreviewRx;
	private JTextField txtEstudioImg;
	private JTextArea txtInformeImg;

	// Historial
	private JTable tablaHistorial;
	private DefaultTableModel modeloHistorial;
	private JTextField txtBuscarHistorial;
	private JLabel lblContadorHistorial;
	private List<OrdenLaboratorio> listaActual;

	public VistaLaboratorioImagenesPanel() {
		setLayout(new BorderLayout());
		setOpaque(false);

		JPanel contenido = new JPanel();
		contenido.setOpaque(false);
		contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
		contenido.setBorder(new EmptyBorder(12, 18, 14, 18));

		// 1. Cabecera
		contenido.add(crearCabeceraVista());
		contenido.add(Box.createVerticalStrut(8));

		// 2. Fila de KPIs
		contenido.add(crearFilaKpis());
		contenido.add(Box.createVerticalStrut(10));

		// 3. Fila Superior: Nueva Solicitud y Registrar Resultado
		contenido.add(crearFilaSolicitudYResultado());
		contenido.add(Box.createVerticalStrut(12));

		// 4. Fila Inferior: Visor de Imágenes e Historial de Estudios
		contenido.add(crearFilaImagenYHistorial());

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

		JLabel titulo = new JLabel("Laboratorio Clínico e Imágenes Diagnósticas");
		titulo.setFont(new Font("Segoe UI", Font.BOLD, 17));
		titulo.setForeground(COLOR_TEXTO_TITULO);

		JLabel subtitulo = new JLabel(
				"Módulo 3.4 · Órdenes diagnósticas, procesamiento de muestras, informes radiológicos y ecografías");
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
		                "Actualizar Laboratorio",
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

		lblKpiTotalOrdenes = new JLabel("4", SwingConstants.LEFT);
		lblKpiLaboratorio = new JLabel("2", SwingConstants.LEFT);
		lblKpiImagenes = new JLabel("2", SwingConstants.LEFT);
		lblKpiCompletados = new JLabel("4", SwingConstants.LEFT);

		fila.add(crearCardKpi(lblKpiTotalOrdenes, "ÓRDENES DIAGNÓSTICAS", new Color(224, 242, 254), COLOR_AZUL_PRIMARIO,
				Iconos.crearIconoFactura(20, COLOR_AZUL_PRIMARIO)));
		fila.add(crearCardKpi(lblKpiLaboratorio, "LABORATORIO CLÍNICO", new Color(243, 232, 255),
				new Color(147, 51, 234), Iconos.crearIconoMicroscopio(20, new Color(147, 51, 234))));
		fila.add(crearCardKpi(lblKpiImagenes, "RAYOS X Y ECOGRAFÍAS", new Color(254, 243, 199), new Color(217, 119, 6),
				Iconos.crearIconoRx(20, new Color(217, 119, 6))));
		fila.add(crearCardKpi(lblKpiCompletados, "RESULTADOS EMITIDOS", new Color(220, 252, 231),
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

	private JPanel crearFilaSolicitudYResultado() {
		JPanel fila = new JPanel(new GridLayout(1, 2, 14, 0));
		fila.setOpaque(false);

		fila.add(crearCardNuevaSolicitud());
		fila.add(crearCardRegistrarResultado());

		return fila;
	}

	private JPanel crearCardNuevaSolicitud() {
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

		JLabel tit = new JLabel("1) Nueva Solicitud (Orden Diagnóstica)");
		tit.setFont(new Font("Segoe UI", Font.BOLD, 13));
		tit.setForeground(COLOR_TEXTO_TITULO);
		form.add(tit);
		form.add(Box.createVerticalStrut(8));

		cbPacienteSol = new JComboBox<>();
		cbPacienteSol.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		cbPacienteSol.setBackground(Color.WHITE);
		cbPacienteSol.setPreferredSize(new Dimension(0, 26));
		cbPacienteSol.addActionListener(e -> autocompletarTutorSol());
		form.add(crearFilaCampo("Paciente (Nombre / Código) *", cbPacienteSol));
		form.add(Box.createVerticalStrut(4));

		txtTutorSol = crearCampoTexto(false);
		form.add(crearFilaCampo("Tutor / Contacto", txtTutorSol));
		form.add(Box.createVerticalStrut(4));

		JPanel filaTipo = new JPanel(new GridLayout(1, 2, 8, 0));
		filaTipo.setOpaque(false);
		cbCategoriaSol = new JComboBox<>(new String[] { "Laboratorio Clínico", "Diagnóstico por Imágenes" });
		cbCategoriaSol.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		cbCategoriaSol.setBackground(Color.WHITE);

		String[] estudios = { "Hemograma Completo Automatizado", "Perfil Bioquímico Hepato-Renal",
				"Radiografía de Tórax (LL / VD)", "Ecografía Abdominal Completa", "Urianálisis Completo + Sedimento",
				"Citología por Punción / Biopsia", "Raspado Cutáneo / Tricograma" };
		cbTipoEstudioSol = new JComboBox<>(estudios);
		cbTipoEstudioSol.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		cbTipoEstudioSol.setBackground(Color.WHITE);

		filaTipo.add(crearFilaCampo("Categoría *", cbCategoriaSol));
		filaTipo.add(crearFilaCampo("Tipo de Examen *", cbTipoEstudioSol));
		form.add(filaTipo);
		form.add(Box.createVerticalStrut(4));

		JPanel filaPrio = new JPanel(new GridLayout(1, 2, 8, 0));
		filaPrio.setOpaque(false);
		cbPrioridadSol = new JComboBox<>(new String[] { "Normal", "Urgente", "Emergencia" });
		cbPrioridadSol.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		cbPrioridadSol.setBackground(Color.WHITE);

		txtVetSol = crearCampoTexto(true);
		txtVetSol.setText("Dra. Camila Morales");

		filaPrio.add(crearFilaCampo("Prioridad Diagnóstica", cbPrioridadSol));
		filaPrio.add(crearFilaCampo("Veterinario Solicitante", txtVetSol));
		form.add(filaPrio);
		form.add(Box.createVerticalStrut(4));

		txtMotivoSol = crearCampoTexto(true);
		txtMotivoSol.setText("Decaimiento, vómitos y pérdida de apetito. Descartar proceso infeccioso.");
		form.add(crearFilaCampo("Motivo / Sospecha Clínica *", txtMotivoSol));
		form.add(Box.createVerticalStrut(10));

		JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
		btnRow.setOpaque(false);
		JButton btnSol =
		        crearBotonWeb(
		                "Generar Solicitud",
		                Iconos.crearIconoMicroscopio(
		                        15,
		                        Color.WHITE
		                ),
		                true,
		                () -> generarNuevaSolicitud()
		        );
		btnRow.add(btnSol);
		form.add(btnRow);

		card.add(form, BorderLayout.NORTH);
		return card;
	}

	private JPanel crearCardRegistrarResultado() {
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

		JLabel tit = new JLabel("2) Registrar Resultado y Procesamiento");
		tit.setFont(new Font("Segoe UI", Font.BOLD, 13));
		tit.setForeground(COLOR_TEXTO_TITULO);
		form.add(tit);
		form.add(Box.createVerticalStrut(8));

		JPanel fila1 = new JPanel(new GridLayout(1, 2, 8, 0));
		fila1.setOpaque(false);
		txtPacienteRes = crearCampoTexto(false);
		txtPacienteRes.setText("Rocky (Golden Retriever)");
		txtEstudioRes = crearCampoTexto(false);
		txtEstudioRes.setText("Hemograma Completo");
		fila1.add(crearFilaCampo("Paciente", txtPacienteRes));
		fila1.add(crearFilaCampo("Examen Clínico", txtEstudioRes));
		form.add(fila1);
		form.add(Box.createVerticalStrut(4));

		JPanel fila2 = new JPanel(new GridLayout(1, 2, 8, 0));
		fila2.setOpaque(false);
		txtResponsableRes = crearCampoTexto(true);
		txtResponsableRes.setText("Lab. Central - Lic. P. Torres");
		cbEstadoRes = new JComboBox<>(new String[] { "Completado", "En Proceso", "Muestra insuficiente" });
		cbEstadoRes.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		cbEstadoRes.setBackground(Color.WHITE);

		fila2.add(crearFilaCampo("Responsable del Procesamiento", txtResponsableRes));
		fila2.add(crearFilaCampo("Estado del Resultado", cbEstadoRes));
		form.add(fila2);
		form.add(Box.createVerticalStrut(4));

		txtValoresRes = new JTextArea(3, 20);
		txtValoresRes.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		txtValoresRes.setLineWrap(true);
		txtValoresRes.setWrapStyleWord(true);
		txtValoresRes.setText(
				"Leucocitos: 18.2 mil/uL (Ref: 6.0-17.0) [ELEVADO]\nHematocrito: 42% (Ref: 37-55%)\nPlaquetas: 245 mil/uL (Ref: 200-500 mil)\nNeutrófilos en banda: 4%");
		txtValoresRes.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(COLOR_BORDE, 1),
				new EmptyBorder(3, 6, 3, 6)));
		form.add(crearFilaArea("Resultados / Valores Analíticos Obtenidos", txtValoresRes, 55));
		form.add(Box.createVerticalStrut(4));

		txtInterpretacionRes = new JTextArea(2, 20);
		txtInterpretacionRes.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		txtInterpretacionRes.setLineWrap(true);
		txtInterpretacionRes.setWrapStyleWord(true);
		txtInterpretacionRes.setText(
				"Leucocitosis leve con desviación a la izquierda compatible con proceso inflamatorio/infeccioso agudo gastrointestinal.");
		txtInterpretacionRes.setBorder(BorderFactory
				.createCompoundBorder(BorderFactory.createLineBorder(COLOR_BORDE, 1), new EmptyBorder(3, 6, 3, 6)));
		form.add(crearFilaArea("Interpretación Médica y Observaciones", txtInterpretacionRes, 42));
		form.add(Box.createVerticalStrut(8));

		JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
		btnRow.setOpaque(false);
		JButton btnGuardar =
		        crearBotonWeb(
		                "Guardar Resultado",
		                Iconos.crearIconoGuardar(
		                        15,
		                        Color.WHITE
		                ),
		                true,
		                () -> guardarResultadoActual()
		        );
		btnRow.add(btnGuardar);
		form.add(btnRow);

		card.add(form, BorderLayout.NORTH);
		return card;
	}

	private JPanel crearFilaImagenYHistorial() {
		JPanel fila = new JPanel(new GridLayout(1, 2, 14, 0));
		fila.setOpaque(false);

		fila.add(crearCardVisorImagenes());
		fila.add(crearCardHistorialExamenes());

		return fila;
	}

	private JPanel crearCardVisorImagenes() {
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

		JLabel tit = new JLabel("3) Imágenes Diagnósticas (Radiología y Ecografía)");
		tit.setFont(new Font("Segoe UI", Font.BOLD, 13));
		tit.setForeground(COLOR_TEXTO_TITULO);
		card.add(tit, BorderLayout.NORTH);

		JPanel cuerpo = new JPanel(new BorderLayout(10, 0));
		cuerpo.setOpaque(false);

		// Visor simulado de placa radiográfica
		panelPreviewRx = new JPanel() {
			private static final long serialVersionUID = 1L;

			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				// Fondo oscuro de negativo radiológico
				g2.setColor(new Color(15, 23, 42));
				g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);

				// Silueta torácica / costillas estilizadas
				g2.setColor(new Color(148, 163, 184, 130));
				int cx = getWidth() / 2;
				int cy = getHeight() / 2;
				// Columna vertebral
				g2.fillRoundRect(cx - 3, 20, 6, getHeight() - 40, 4, 4);
				// Costillas
				for (int i = 35; i < getHeight() - 40; i += 18) {
					g2.drawArc(cx - 55, i, 50, 20, 0, 180);
					g2.drawArc(cx + 5, i, 50, 20, 0, 180);
				}
				// Silueta cardíaca
				g2.setColor(new Color(241, 245, 249, 160));
				g2.fillOval(cx - 16, cy - 10, 32, 42);

				// Marcador R / X
				g2.setColor(new Color(56, 189, 248));
				g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
				g2.drawString("RAYOS X DIGITAL - VET", 12, 22);

				g2.dispose();
			}
		};
		panelPreviewRx.setPreferredSize(new Dimension(170, 160));
		cuerpo.add(panelPreviewRx, BorderLayout.WEST);

		// Informe Radiológico a la derecha
		JPanel infoDer = new JPanel();
		infoDer.setOpaque(false);
		infoDer.setLayout(new BoxLayout(infoDer, BoxLayout.Y_AXIS));

		txtEstudioImg = crearCampoTexto(false);
		txtEstudioImg.setText("Luna · Radiografía de Tórax (LL / VD)");
		infoDer.add(crearFilaCampo("Estudio en Visor", txtEstudioImg));
		infoDer.add(Box.createVerticalStrut(4));

		txtInformeImg = new JTextArea(4, 20);
		txtInformeImg.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		txtInformeImg.setLineWrap(true);
		txtInformeImg.setWrapStyleWord(true);
		txtInformeImg.setText(
				"Silueta cardíaca conservada de tamaño normal (VHS: 7.4v). Patrón bronquial leve difuso en campos caudales. Sin evidencia de neumotórax ni derrame pleural. Sugiere bronquitis alérgica.");
		txtInformeImg.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(COLOR_BORDE, 1),
				new EmptyBorder(3, 6, 3, 6)));
		infoDer.add(crearFilaArea("Informe Radiológico / Ecográfico", txtInformeImg, 75));
		infoDer.add(Box.createVerticalStrut(6));

		JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
		btnRow.setOpaque(false);
		JButton btnGuardarInforme =
		        crearBotonWeb(
		                "Guardar Informe",
		                Iconos.crearIconoDocumento(
		                        15,
		                        new Color(51, 65, 85)
		                ),
		                false,
		                () -> {
		                    JOptionPane.showMessageDialog(
		                            this,
		                            "Informe de imagenología clínica actualizado correctamente.",
		                            "Informe Guardado",
		                            JOptionPane.INFORMATION_MESSAGE
		                    );
		                }
		        );
		btnRow.add(btnGuardarInforme);
		infoDer.add(btnRow);

		cuerpo.add(infoDer, BorderLayout.CENTER);
		card.add(cuerpo, BorderLayout.CENTER);

		return card;
	}

	private JPanel crearCardHistorialExamenes() {
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

		JLabel tit = new JLabel("4) Historial de Órdenes y Resultados");
		tit.setFont(new Font("Segoe UI", Font.BOLD, 13));
		tit.setForeground(COLOR_TEXTO_TITULO);
		top.add(tit, BorderLayout.WEST);

		lblContadorHistorial = new JLabel("Cargando órdenes...");
		lblContadorHistorial.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		lblContadorHistorial.setForeground(COLOR_TEXTO_MUTED);
		top.add(lblContadorHistorial, BorderLayout.EAST);

		card.add(top, BorderLayout.NORTH);

		String[] columnas = { "ID", "Fecha", "Paciente", "Examen", "Categoría", "Prioridad", "Estado" };
		modeloHistorial = new DefaultTableModel(columnas, 0) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int r, int c) {
				return false;
			}
		};

		tablaHistorial = new JTable(modeloHistorial);
		Ui.formatearTabla(tablaHistorial);
		tablaHistorial.setRowHeight(28);

		tablaHistorial.getColumnModel().getColumn(0).setPreferredWidth(75);
		tablaHistorial.getColumnModel().getColumn(1).setPreferredWidth(75);
		tablaHistorial.getColumnModel().getColumn(2).setPreferredWidth(85);
		tablaHistorial.getColumnModel().getColumn(3).setPreferredWidth(160);
		tablaHistorial.getColumnModel().getColumn(6).setCellRenderer(new BadgeEstadoLabRenderer());

		tablaHistorial.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				cargarOrdenSeleccionada();
			}
		});

		JScrollPane sp = new JScrollPane(tablaHistorial);
		sp.setBorder(BorderFactory.createLineBorder(COLOR_BORDE, 1));
		sp.setPreferredSize(new Dimension(500, 150));
		card.add(sp, BorderLayout.CENTER);

		JPanel bot = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
		bot.setOpaque(false);

		JButton btnVerReporte =
		        crearBotonWeb(
		                "Reporte Completo",
		                Iconos.crearIconoDocumento(
		                        15,
		                        new Color(37, 99, 235)
		                ),
		                false,
		                () -> verReporteDiagnosticoCompleto()
		        );
		bot.add(btnVerReporte);
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
		if (cbPacienteSol.getItemCount() == 0) {
			for (Mascota m : repo.todasLasMascotas()) {
				cbPacienteSol.addItem(new PacienteItem(m));
			}
			autocompletarTutorSol();
		}

		listaActual = repo.getOrdenesLaboratorio();
		modeloHistorial.setRowCount(0);

		for (OrdenLaboratorio o : listaActual) {
			modeloHistorial.addRow(new Object[] { o.getIdOrden(), o.getFechaSolicitudFormateada(), o.getNombreMascota(),
					o.getTipoEstudio(), o.getCategoria(), o.getPrioridad(), o.getEstado() });
		}

		lblContadorHistorial.setText(listaActual.size() + " órdenes procesadas");

		// KPIs
		long labs = listaActual.stream().filter(o -> o.getCategoria().contains("Laboratorio")).count();
		long imgs = listaActual.stream().filter(o -> o.getCategoria().contains("Imágenes")).count();
		long compl = listaActual.stream().filter(o -> "Completado".equalsIgnoreCase(o.getEstado())).count();

		lblKpiTotalOrdenes.setText(String.valueOf(listaActual.size()));
		lblKpiLaboratorio.setText(String.valueOf(labs));
		lblKpiImagenes.setText(String.valueOf(imgs));
		lblKpiCompletados.setText(String.valueOf(compl));
	}

	private void autocompletarTutorSol() {
		PacienteItem item = (PacienteItem) cbPacienteSol.getSelectedItem();
		if (item != null && item.mascota != null) {
			repo.getClienteDeMascota(item.mascota.getCodigo()).ifPresent(c -> {
				txtTutorSol.setText(c.getNombreCompleto() + " (" + c.getTelefonoPrincipal() + ")");
			});
		}
	}

	private void generarNuevaSolicitud() {
		PacienteItem pi = (PacienteItem) cbPacienteSol.getSelectedItem();
		if (pi == null || pi.mascota == null)
			return;
		Mascota m = pi.mascota;
		Optional<Cliente> optC = repo.getClienteDeMascota(m.getCodigo());

		OrdenLaboratorio o = new OrdenLaboratorio(null, (String) cbCategoriaSol.getSelectedItem(),
				(String) cbTipoEstudioSol.getSelectedItem(), m.getCodigo(), m.getNombre(),
				m.getEspecie() + " · " + m.getRaza(), optC.map(Cliente::getNombreCompleto).orElse("Tutor"),
				optC.map(Cliente::getTelefonoPrincipal).orElse(""), LocalDate.now(),
				(String) cbPrioridadSol.getSelectedItem(), txtVetSol.getText().trim(), txtMotivoSol.getText().trim(),
				null, "Lab. Central Veterinario",
				"Muestra biológica ingresada al analizador automatizado. Pendiente de emisión.",
				"Pendiente de informe por el especialista.", "En evaluación clínica.", "En Proceso");

		repo.guardarOrdenLaboratorio(o);
		JOptionPane
				.showMessageDialog(
						this, "Orden diagnóstica " + o.getIdOrden() + " generada exitosamente para " + m.getNombre()
								+ " (" + o.getTipoEstudio() + ").",
						"Solicitud Creada", JOptionPane.INFORMATION_MESSAGE);

		recargarDatos();
	}

	private void cargarOrdenSeleccionada() {
		int row = tablaHistorial.getSelectedRow();
		if (row < 0 || row >= listaActual.size())
			return;
		OrdenLaboratorio o = listaActual.get(row);

		txtPacienteRes.setText(o.getNombreMascota() + " (" + o.getEspecieRaza() + ")");
		txtEstudioRes.setText(o.getTipoEstudio());
		txtResponsableRes.setText(o.getResponsableProcesamiento());
		cbEstadoRes.setSelectedItem(o.getEstado());
		txtValoresRes.setText(o.getResultadoValores());
		txtInterpretacionRes.setText(o.getConclusionesRecomendaciones());

		if (o.getCategoria().contains("Imágenes")) {
			txtEstudioImg.setText(o.getNombreMascota() + " · " + o.getTipoEstudio());
			txtInformeImg.setText(
					o.getInformeDetallado() != null && !o.getInformeDetallado().isEmpty() ? o.getInformeDetallado()
							: o.getResultadoValores());
		}
	}

	private void guardarResultadoActual() {
		int row = tablaHistorial.getSelectedRow();
		if (row >= 0 && row < listaActual.size()) {
			OrdenLaboratorio o = listaActual.get(row);
			o.setResponsableProcesamiento(txtResponsableRes.getText().trim());
			o.setResultadoValores(txtValoresRes.getText().trim());
			o.setConclusionesRecomendaciones(txtInterpretacionRes.getText().trim());
			o.setEstado((String) cbEstadoRes.getSelectedItem());
			o.setFechaResultado(LocalDate.now());
			recargarDatos();
		}

		JOptionPane.showMessageDialog(this,
				"Resultado diagnóstico y valores analíticos almacenados correctamente en el expediente clínico.",
				"Resultado Guardado", JOptionPane.INFORMATION_MESSAGE);
	}

	private void verReporteDiagnosticoCompleto() {
		int row = tablaHistorial.getSelectedRow();
		if (row < 0 || row >= listaActual.size()) {
			JOptionPane.showMessageDialog(this,
					"Seleccione un estudio del historial para visualizar su reporte completo.", "Aviso",
					JOptionPane.WARNING_MESSAGE);
			return;
		}
		OrdenLaboratorio o = listaActual.get(row);

		String reporte = "╔════════════════════════════════════════════════════════════════╗\n"
				+ "║      VETERINARIA HAPPY PETS · INFORME DIAGNÓSTICO CLÍNICO      ║\n"
				+ "║           Laboratorio Veterinario & Imagenología               ║\n"
				+ "╠════════════════════════════════════════════════════════════════╣\n" + " Código Orden: "
				+ o.getIdOrden() + "  |  Categoría: " + o.getCategoria() + "\n" + " Fecha Solicitud: "
				+ o.getFechaSolicitudFormateada() + "  |  Fecha Emisión: " + o.getFechaResultadoFormateada() + "\n"
				+ " Paciente: " + o.getNombreMascota() + " (" + o.getEspecieRaza() + ")\n" + " Tutor: "
				+ o.getNombreTutor() + "  |  Tel: " + o.getTelefonoTutor() + "\n" + " Solicitante: "
				+ o.getVeterinarioSolicitante() + "  |  Prioridad: " + o.getPrioridad() + "\n" + " Examen: "
				+ o.getTipoEstudio() + "\n" + "────────────────────────────────────────────────────────────────\n"
				+ " RESULTADOS ANALÍTICOS / INFORME TÉCNICO:\n"
				+ (o.getInformeDetallado() != null && !o.getInformeDetallado().isEmpty()
						? o.getInformeDetallado() + "\n\n"
						: "")
				+ o.getResultadoValores() + "\n\n"
				+ "────────────────────────────────────────────────────────────────\n"
				+ " CONCLUSIONES E INTERPRETACIÓN MÉDICA:\n" + o.getConclusionesRecomendaciones() + "\n\n"
				+ " Especialista Responsable: " + o.getResponsableProcesamiento() + "\n" + " Estado Oficial: "
				+ o.getEstado() + "\n" + "╚════════════════════════════════════════════════════════════════╝";

		JOptionPane.showMessageDialog(this, reporte, "Reporte Diagnóstico Oficial - " + o.getIdOrden(),
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

	private static class BadgeEstadoLabRenderer extends DefaultTableCellRenderer {
		private static final long serialVersionUID = 1L;

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
			JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			lbl.setHorizontalAlignment(SwingConstants.CENTER);
			lbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
			String st = String.valueOf(value);
			if (!isSelected) {
				if ("Completado".equalsIgnoreCase(st)) {
					lbl.setForeground(new Color(22, 163, 74));
				} else if ("En Proceso".equalsIgnoreCase(st)) {
					lbl.setForeground(new Color(2, 132, 199));
				} else if ("Solicitado".equalsIgnoreCase(st)) {
					lbl.setForeground(new Color(217, 119, 6));
				} else if ("Muestra insuficiente".equalsIgnoreCase(st)) {
					lbl.setForeground(new Color(220, 38, 38));
				}
			}
			return lbl;
		}
	}
}
