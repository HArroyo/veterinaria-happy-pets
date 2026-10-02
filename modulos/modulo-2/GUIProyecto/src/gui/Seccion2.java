package gui;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.BoxLayout;
import javax.swing.SwingConstants;
import java.util.Calendar;

public class Seccion2 extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	
	// Componentes calendario izquierdo
	private JLabel lblMesAnio;
	private JPanel panelCalendario;
	private int mesActual, anioActual;
	private final String[] MESES = {
		"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
		"Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
	};
	private final String[] DIAS_CORTO = {"Mo", "Tu", "We", "Th", "Fr", "Sa", "Su"};
	
	// Tabla horarios
	private JPanel panelTabla;
	private final String[] HORAS = {"9am","10am","11am","12pm","1pm","2pm","3pm","4pm","5pm","6pm","7pm","8pm"};
	private final String[] COLUMNAS = {"Veterinario1","Veterinario2","Quirofano","Grooming Area"};

	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			try {
				Seccion2 frame = new Seccion2();
				frame.setVisible(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	public Seccion2() {
		setTitle("Calendario Global");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 900, 550);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		// --- TITULO ---
		JLabel lblTitulo = new JLabel("VISTA [TIPO] PROMEDIO");
		lblTitulo.setFont(new Font("Tahoma", Font.PLAIN, 20));
		lblTitulo.setBounds(280, 5, 350, 30);
		contentPane.add(lblTitulo);
		
		// --- BOTONES ARRIBA DERECHA ---
		JPanel pnlBotonesVista = new JPanel();
		pnlBotonesVista.setBounds(680, 10, 190, 30);
		pnlBotonesVista.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 0));
		JButton btnDia = new JButton("Día");
		JButton btnSemana = new JButton("Semana");
		JButton btnMes = new JButton("Mes");
		btnDia.setBackground(new Color(220, 220, 220));
		btnSemana.setBackground(new Color(220, 220, 220));
		btnMes.setBackground(new Color(220, 220, 220));
		pnlBotonesVista.add(btnDia);
		pnlBotonesVista.add(btnSemana);
		pnlBotonesVista.add(btnMes);
		contentPane.add(pnlBotonesVista);
		
		// --- PANEL IZQUIERDO: CALENDARIO + FILTROS ---
		JPanel pnlIzquierda = new JPanel();
		pnlIzquierda.setBounds(10, 50, 220, 470);
		pnlIzquierda.setLayout(null);
		contentPane.add(pnlIzquierda);
		
		// Navegación mes/año
		JPanel pnlNavegacion = new JPanel();
		pnlNavegacion.setBounds(0, 0, 220, 40);
		pnlNavegacion.setLayout(new BorderLayout());
		JButton btnAnt = new JButton("<");
		lblMesAnio = new JLabel("", SwingConstants.CENTER);
		lblMesAnio.setFont(new Font("Tahoma", Font.BOLD, 14));
		JButton btnSig = new JButton(">");
		pnlNavegacion.add(btnAnt, BorderLayout.WEST);
		pnlNavegacion.add(lblMesAnio, BorderLayout.CENTER);
		pnlNavegacion.add(btnSig, BorderLayout.EAST);
		pnlIzquierda.add(pnlNavegacion);
		
		// Panel cuadrícula calendario
		panelCalendario = new JPanel();
		panelCalendario.setBounds(0, 45, 220, 200);
		panelCalendario.setLayout(new GridLayout(7, 7, 2, 2));
		pnlIzquierda.add(panelCalendario);
		
		// Filtros
		JLabel lblFiltros = new JLabel("Filtros");
		lblFiltros.setBounds(0, 260, 220, 25);
		lblFiltros.setFont(new Font("Tahoma", Font.BOLD, 13));
		pnlIzquierda.add(lblFiltros);
		
		JComboBox<String> cbVet = new JComboBox<>(new String[]{"Veterinario", "Veterinario 1", "Veterinario 2"});
		cbVet.setBounds(10, 290, 200, 30);
		pnlIzquierda.add(cbVet);
		
		JComboBox<String> cbEstado = new JComboBox<>(new String[]{"Estado", "Disponible", "Ocupado"});
		cbEstado.setBounds(10, 335, 200, 30);
		pnlIzquierda.add(cbEstado);
		
		// Área dibujo/estado abajo
		JPanel pnlCuadro = new JPanel();
		pnlCuadro.setBounds(10, 380, 200, 120);
		pnlCuadro.setBorder(new EmptyBorder(5,5,5,5));
		pnlCuadro.setLayout(new BorderLayout());
		pnlIzquierda.add(pnlCuadro);
		
		// --- PANEL DERECHA: TABLA HORARIOS ---
		panelTabla = new JPanel();
		panelTabla.setBounds(240, 50, 630, 470);
		panelTabla.setLayout(new BorderLayout());
		contentPane.add(panelTabla);
		
		// Navegación meses
		btnAnt.addActionListener(e -> cambiarMes(-1));
		btnSig.addActionListener(e -> cambiarMes(1));
		
		// Fecha actual
		Calendar hoy = Calendar.getInstance();
		mesActual = hoy.get(Calendar.MONTH);
		anioActual = hoy.get(Calendar.YEAR);
		actualizarCalendario();
		construirTabla();
	}
	
	// Cambiar mes
	private void cambiarMes(int delta) {
		mesActual += delta;
		if (mesActual > 11) { mesActual = 0; anioActual++; }
		if (mesActual < 0) { mesActual = 11; anioActual--; }
		actualizarCalendario();
	}
	
	// Dibujar calendario pequeño
	private void actualizarCalendario() {
		panelCalendario.removeAll();
		lblMesAnio.setText(MESES[mesActual] + " " + anioActual);
		
		// Cabecera días
		for (String d : DIAS_CORTO) {
			JLabel l = new JLabel(d, SwingConstants.CENTER);
			l.setFont(new Font("Tahoma", Font.PLAIN, 10));
			panelCalendario.add(l);
		}
		
		Calendar cal = Calendar.getInstance();
		cal.set(anioActual, mesActual, 1);
		int inicio = cal.get(Calendar.DAY_OF_WEEK) - 1;
		int totalDias = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		
		// Espacios vacíos
		for (int i = 0; i < inicio; i++) panelCalendario.add(new JLabel(""));
		
		// Días
		for (int d = 1; d <= totalDias; d++) {
			JLabel lblDia = new JLabel(String.valueOf(d), SwingConstants.CENTER);
			lblDia.setFont(new Font("Tahoma", Font.PLAIN, 11));
			lblDia.setOpaque(true);
			lblDia.setBackground(Color.WHITE);
			panelCalendario.add(lblDia);
		}
		
		panelCalendario.revalidate();
		panelCalendario.repaint();
	}
	
	// Construir tabla de horarios
	private void construirTabla() {
		panelTabla.removeAll();
		
		// Cabecera
		JPanel pnlCabecera = new JPanel(new GridLayout(1, COLUMNAS.length + 1, 1, 1));
		pnlCabecera.add(new JLabel(""));
		for (String col : COLUMNAS) {
			JLabel l = new JLabel(col, SwingConstants.CENTER);
			l.setBackground(new Color(150, 150, 150));
			l.setForeground(Color.WHITE);
			l.setOpaque(true);
			l.setFont(new Font("Tahoma", Font.BOLD, 12));
			pnlCabecera.add(l);
		}
		panelTabla.add(pnlCabecera, BorderLayout.NORTH);
		
		// Cuerpo tabla
		JPanel pnlCuerpo = new JPanel(new GridLayout(HORAS.length, COLUMNAS.length + 1, 1, 1));
		for (String hora : HORAS) {
			// Columna hora
			JLabel lHora = new JLabel(hora, SwingConstants.CENTER);
			lHora.setBackground(new Color(230, 230, 230));
			lHora.setOpaque(true);
			pnlCuerpo.add(lHora);
			// Columnas espacios
			for (int c = 0; c < COLUMNAS.length; c++) {
				JLabel celda = new JLabel("", SwingConstants.CENTER);
				celda.setBackground(Color.WHITE);
				celda.setOpaque(true);
				pnlCuerpo.add(celda);
			}
		}
		panelTabla.add(pnlCuerpo, BorderLayout.CENTER);
		
		panelTabla.revalidate();
		panelTabla.repaint();
	}
}
