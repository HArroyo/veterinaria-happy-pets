# -*- coding: utf-8 -*-
"""
Generador del Manual Técnico y Funcional en formato PDF para el sistema HappyPets ERP.
Utiliza HTML5 + CSS3 Paged Media y Google Chrome Headless para producir un PDF vectorial
de alta calidad tipográfica y gráfica para estudio y sustentación académica.
"""

import os
import subprocess
import sys

def generar_html_manual():
    return """<!DOCTYPE html>
<html lang="es">
<head>
<meta charset="UTF-8">
<title>Manual Técnico y Funcional - HappyPets ERP</title>
<style>
  @page {
    size: A4;
    margin: 18mm 15mm 20mm 15mm;
    @bottom-right {
      content: "Página " counter(page);
      font-family: 'Segoe UI', Arial, sans-serif;
      font-size: 8pt;
      color: #64748b;
    }
    @bottom-left {
      content: "HappyPets ERP · Manual de Sustentación";
      font-family: 'Segoe UI', Arial, sans-serif;
      font-size: 8pt;
      color: #007987;
      font-weight: bold;
    }
  }

  body {
    font-family: 'Segoe UI', -apple-system, BlinkMacSystemFont, Roboto, Helvetica, Arial, sans-serif;
    color: #1e293b;
    background-color: #ffffff;
    line-height: 1.5;
    font-size: 9.5pt;
    margin: 0;
    padding: 0;
  }

  /* Portada */
  .cover-page {
    page-break-after: always;
    height: 100vh;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    text-align: center;
    padding: 40px 20px;
    box-sizing: border-box;
    background: linear-gradient(135deg, #f0fdfa 0%, #ffffff 50%, #e0f2fe 100%);
    border: 3px solid #00bcd4;
    border-radius: 12px;
  }
  .cover-badge {
    display: inline-block;
    background: #00bcd4;
    color: #ffffff;
    font-weight: 700;
    font-size: 11pt;
    padding: 6px 18px;
    border-radius: 20px;
    letter-spacing: 1.5px;
    text-transform: uppercase;
    margin-bottom: 25px;
  }
  .cover-title {
    font-size: 28pt;
    font-weight: 900;
    color: #006064;
    margin: 0 0 10px 0;
    line-height: 1.15;
    letter-spacing: -0.5px;
  }
  .cover-subtitle {
    font-size: 14pt;
    color: #007987;
    margin: 0 0 25px 0;
    font-weight: 600;
  }
  .cover-desc {
    font-size: 10pt;
    color: #475569;
    max-width: 550px;
    margin: 0 auto 35px auto;
    line-height: 1.6;
  }
  .cover-meta {
    background: #ffffff;
    border: 1px solid #cbd5e1;
    border-radius: 8px;
    padding: 16px 28px;
    text-align: left;
    display: inline-block;
    box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05);
  }
  .cover-meta table {
    border-collapse: collapse;
    font-size: 9pt;
  }
  .cover-meta td {
    padding: 4px 10px;
  }
  .cover-meta td.label {
    font-weight: bold;
    color: #0f172a;
  }
  .cover-meta td.val {
    color: #007987;
    font-weight: 600;
  }

  /* Encabezados y Secciones */
  h1 {
    color: #006064;
    font-size: 18pt;
    font-weight: 800;
    border-bottom: 2px solid #00bcd4;
    padding-bottom: 6px;
    margin-top: 28px;
    margin-bottom: 14px;
    page-break-before: always;
  }
  h1:first-of-type {
    page-break-before: auto;
  }
  h2 {
    color: #007987;
    font-size: 13pt;
    font-weight: 700;
    margin-top: 18px;
    margin-bottom: 8px;
    border-left: 4px solid #00bcd4;
    padding-left: 8px;
  }
  h3 {
    color: #0f172a;
    font-size: 11pt;
    font-weight: 700;
    margin-top: 14px;
    margin-bottom: 6px;
  }

  p {
    margin: 0 0 8px 0;
    text-align: justify;
  }

  /* Cajas de llamada y alertas */
  .callout {
    background: #f8fafc;
    border-left: 4px solid #00bcd4;
    padding: 10px 14px;
    margin: 12px 0;
    border-radius: 0 6px 6px 0;
    font-size: 9pt;
  }
  .callout-title {
    font-weight: bold;
    color: #006064;
    margin-bottom: 4px;
  }
  .callout-exam {
    background: #fffbeb;
    border-left: 4px solid #f59e0b;
    padding: 10px 14px;
    margin: 12px 0;
    border-radius: 0 6px 6px 0;
  }
  .callout-exam .callout-title {
    color: #b45309;
  }

  /* Tablas de contenido y técnicas */
  table.data-table {
    width: 100%;
    border-collapse: collapse;
    margin: 10px 0 16px 0;
    font-size: 8.5pt;
  }
  table.data-table th {
    background-color: #006064;
    color: #ffffff;
    text-align: left;
    padding: 6px 8px;
    font-weight: bold;
    border: 1px solid #004d40;
  }
  table.data-table td {
    padding: 5px 8px;
    border: 1px solid #cbd5e1;
    vertical-align: top;
  }
  table.data-table tr:nth-child(even) {
    background-color: #f8fafc;
  }

  /* Tarjetas de Módulos */
  .module-card {
    border: 1px solid #e2e8f0;
    background: #ffffff;
    border-radius: 8px;
    padding: 12px 14px;
    margin-bottom: 14px;
    box-shadow: 0 2px 4px rgba(0,0,0,0.03);
    page-break-inside: avoid;
  }
  .module-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    border-bottom: 1px solid #e2e8f0;
    padding-bottom: 6px;
    margin-bottom: 8px;
  }
  .module-name {
    font-size: 11pt;
    font-weight: bold;
    color: #006064;
  }
  .module-tag {
    background: #e0f2fe;
    color: #0369a1;
    font-size: 7.5pt;
    font-weight: bold;
    padding: 2px 8px;
    border-radius: 12px;
  }

  /* Bloques de código / nombres de clases */
  code {
    font-family: Consolas, 'Courier New', monospace;
    font-size: 8.5pt;
    background: #f1f5f9;
    color: #0f172a;
    padding: 1px 4px;
    border-radius: 3px;
    border: 1px solid #e2e8f0;
  }

  ul, ol {
    margin: 4px 0 10px 20px;
    padding: 0;
  }
  li {
    margin-bottom: 4px;
  }

  .badge-peru {
    background: #fee2e2;
    color: #991b1b;
    border: 1px solid #fca5a5;
    padding: 2px 6px;
    border-radius: 4px;
    font-size: 7.5pt;
    font-weight: bold;
  }
</style>
</head>
<body>

<!-- PORTADA -->
<div class="cover-page">
  <div class="cover-badge">DOCUMENTACIÓN TÉCNICA Y FUNCIONAL</div>
  <div class="cover-title">HAPPY PETS CLINIC ERP</div>
  <div class="cover-subtitle">Sistema Hospitalario y de Gestión Veterinaria 24 Horas</div>
  <div class="cover-desc">
    Manual integral de arquitectura de software, especificación funcional de los 10 módulos del sistema, guía de sustentación académica y banco de preguntas y respuestas para evaluación técnica.
  </div>
  <div class="cover-meta">
    <table>
      <tr><td class="label">Plataforma:</td><td class="val">Java Swing (Java SE 21 LTS)</td></tr>
      <tr><td class="label">Patrón de Arquitectura:</td><td class="val">MVC Desacoplado + Repository Singleton</td></tr>
      <tr><td class="label">Localización y Fiscalidad:</td><td class="val">Perú (RUC 20608421154, SUNAT CPE, PEN S/.)</td></tr>
      <tr><td class="label">Total Módulos ERP:</td><td class="val">10 Módulos Integrados (40 Submódulos)</td></tr>
      <tr><td class="label">Fecha de Compilación:</td><td class="val">Octubre 2026 · Versión Final</td></tr>
    </table>
  </div>
</div>

<!-- ÍNDICE GENERAL -->
<h1>1. Índice General y Resumen del Sistema</h1>
<p>
  El presente manual ha sido diseñado como herramienta de estudio rigurosa y material de apoyo para la exposición y sustentación del proyecto <strong>Veterinaria Happy Pets</strong>. Este documento contiene la explicación exhaustiva de los 10 módulos del ERP, la interacción entre capas de software, el manejo de eventos Swing, la persistencia en memoria y un banco de preguntas y respuestas frecuentes para resolver cualquier cuestionamiento técnico o funcional de los docentes o jurados examinadores.
</p>

<h2>1.1 Estructura del Documento</h2>
<ul>
  <li><strong>Capítulo 2:</strong> Arquitectura de Software, Patrones de Diseño y Sistema Visual.</li>
  <li><strong>Capítulo 3:</strong> Módulos 1 al 4 (Pacientes, Citas, Servicios Médicos, Estética y Hospedaje).</li>
  <li><strong>Capítulo 4:</strong> Módulos 5 al 7 (Inventario/Farmacia, Finanzas/POS, Personal/RRHH).</li>
  <li><strong>Capítulo 5:</strong> Módulos 8 al 10 (BI/Reportes, Notificaciones/Auditoría, Configuración/IA).</li>
  <li><strong>Capítulo 6:</strong> Banco de Preguntas y Respuestas para la Sustentación Oral.</li>
</ul>

<div class="callout">
  <div class="callout-title">Propósito Académico y Laboral</div>
  HappyPets ERP simula fielmente la operación de una clínica veterinaria de alta complejidad las 24 horas. Resuelve problemáticas reales de control de historias clínicas, triaje médico con IA, facturación electrónica peruana (SUNAT), trazabilidad de lotes farmacéuticos (Kardex), cuadrantes de guardias médicas y pistas inmutables de auditoría forense.
</div>

<!-- ARQUITECTURA -->
<h1>2. Arquitectura de Software y Patrones de Diseño</h1>

<h2>2.1 Paradigma Arquitectónico: MVC Adaptado a Swing</h2>
<p>
  El sistema adopta una arquitectura en tres capas desacopladas, lo que garantiza mantenibilidad, alta cohesión y bajo acoplamiento:
</p>
<ol>
  <li><strong>Capa de Dominio (Modelos):</strong> Paquete <code>happypets.model</code>. Clases POJO con encapsulamiento estricto que representan las entidades del negocio (e.g. <code>Mascota</code>, <code>Cliente</code>, <code>ConsultaMedica</code>, <code>VentaPOS</code>, <code>Usuario</code>).</li>
  <li><strong>Capa de Persistencia y Lógica (Repositorio):</strong> Paquete <code>happypets.data</code> con la clase central <code>RepositorioVeterinaria</code> bajo el patrón <strong>Singleton</strong>. Simula un motor de base de datos transaccional en memoria, garantizando atomicidad y centralización de consultas con Java Streams.</li>
  <li><strong>Capa de Presentación (Vistas y Controladores):</strong> Paquetes <code>happypets.ui</code> y <code>happypets.modulos.*</code>. Emplea componentes nativos Swing organizados mediante <code>BorderLayout</code>, <code>GridBagLayout</code> y <code>CardLayout</code>, combinados con renderizadores personalizados en <code>Graphics2D</code>.</li>
</ol>

<h2>2.2 Patrones de Diseño Clave Empleados en el Código</h2>
<table class="data-table">
  <thead>
    <tr>
      <th>Patrón</th>
      <th>Clase de Ejemplo</th>
      <th>Justificación Técnica / Sustentación</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td><strong>Singleton</strong></td>
      <td><code>RepositorioVeterinaria</code>, <code>ServicioAutenticacion</code></td>
      <td>Garantiza una única instancia compartida por todos los módulos del sistema, evitando discrepancias de datos o duplicación de memoria en tiempo de ejecución.</td>
    </tr>
    <tr>
      <td><strong>Master-Detail (Live Hub)</strong></td>
      <td>Todas las <code>Vista*Panel.java</code></td>
      <td>Permite visualizar la tabla o listado de registros a la izquierda (Master) y una ficha de inspección profunda y botones de acción a la derecha (Live Hub), optimizando el flujo de trabajo clínico.</td>
    </tr>
    <tr>
      <td><strong>Observer / Event Listener</strong></td>
      <td><code>ListSelectionListener</code>, <code>DocumentListener</code></td>
      <td>Reacciona de manera asíncrona ante la selección de filas en tablas o escritura en barras de búsqueda, actualizando los paneles secundarios en tiempo real sin recargar pantallas.</td>
    </tr>
    <tr>
      <td><strong>Custom Cell Renderer</strong></td>
      <td><code>TableCellRenderer</code> con <code>BorderLayout</code></td>
      <td>Garantiza el dibujo antialiased de avatares monograma, chips de estado tipo píldora e iconos vectoriales (como la estrella de calificación) evitando el recorte o deformación de textos.</td>
    </tr>
    <tr>
      <td><strong>Decoupled Modal Dialog</strong></td>
      <td>Todos los <code>Formulario*Dialog.java</code></td>
      <td>Diálogos modales independientes basados en <code>JDialog</code> que aíslan la captura, validación de reglas de negocio y retroalimentación visual antes de persistir en el repositorio.</td>
    </tr>
  </tbody>
</table>

<h2>2.3 Sistema Visual y Paleta Corporativa (Ui e Iconos)</h2>
<p>
  La plataforma implementa un sistema de diseño institucional formalizado en <code>happypets.ui.Ui</code> y <code>happypets.ui.Iconos</code>:
</p>
<ul>
  <li><strong>Color Primario Oficial:</strong> Turquesa Clínico (<code>#00BCD4</code>).</li>
  <li><strong>Color Secundario / Destacados:</strong> Turquesa Profundo (<code>#007987</code>) y Turquesa Oscuro (<code>#006064</code>).</li>
  <li><strong>Fondos y Superficies:</strong> Pizarra Suave (<code>#F8FAFC</code>) y Tarjetas en Blanco Puro (<code>#FFFFFF</code>).</li>
  <li><strong>Iconografía Vectorial:</strong> Dibujada a mano en <code>Graphics2D</code> con soporte HiDPI (estetoscopios, jeringas, tijeras, microscopios, huellas, estrellas).</li>
</ul>

<!-- MODULOS 1 AL 4 -->
<h1>3. Especificación: Módulos Clínicos y Asistenciales (1 al 4)</h1>

<h2>Módulo 1: Pacientes y Clientes</h2>
<div class="module-card">
  <div class="module-header">
    <span class="module-name">1. Gestión de Historias Clínicas y Propietarios</span>
    <span class="module-tag">Modulo 1 · Core Clínico</span>
  </div>
  <p><strong>Clases Principales:</strong> <code>VistaClientesMascotasPanel</code>, <code>VistaHistorialClinicoPanel</code>, <code>VistaConstanciasCertificadosPanel</code>.</p>
  <p><strong>Objetivo Funcional:</strong> Administrar el padrón de tutores responsables, las fichas médicas de las mascotas, su historial patológico previo, carnés de vacunación oficial y emisión de constancias de salud con código QR simulado.</p>
  <ul>
    <li><strong>Directorio Propietarios:</strong> Búsqueda por DNI, nombre o teléfono, selector dinámico de mascotas por tutor.</li>
    <li><strong>Historial Clínico:</strong> Registro evolutivo de consultas, signos vitales (temperatura, peso, frecuencia cardíaca), diagnósticos presuntivos y definitivos, y planes terapéuticos.</li>
    <li><strong>Constancias y Certificados:</strong> Generación interactiva de certificados de vacunación antirrábica, constancias de viaje y fichas clínicas exportables a HTML/impresión.</li>
  </ul>
</div>

<h2>Módulo 2: Agenda y Citas Médicas</h2>
<div class="module-card">
  <div class="module-header">
    <span class="module-name">2. Agendamiento, Triaje y Recordatorios</span>
    <span class="module-tag">Modulo 2 · Admisión</span>
  </div>
  <p><strong>Clases Principales:</strong> <code>VistaAgendamientoCitasPanel</code>, <code>VistaSalaEsperaTriajePanel</code>, <code>VistaRecordatoriosPanel</code>, <code>VistaCalendarioGlobalPanel</code>.</p>
  <p><strong>Objetivo Funcional:</strong> Controlar el flujo de admisión de pacientes, triaje con escala de colores de urgencia (Manchester veterinario), calendario de especialistas y recordatorios por WhatsApp/Email.</p>
  <ul>
    <li><strong>Sala de Triaje en Vivo:</strong> Monitor que categoriza pacientes en Emergencia Crítica (Rojo), Urgente (Naranja) y Control Rutinario (Verde), calculando tiempos de espera asistencial.</li>
    <li><strong>Recordatorios Omnicanal:</strong> Automatización de citas de refuerzo vacunal con copiado directo al portapapeles y plantillas oficiales.</li>
    <li><strong>Calendario Global:</strong> Visualización mensual y semanal de ocupación de boxes y turnos médicos.</li>
  </ul>
</div>

<h2>Módulo 3: Servicios Médicos Especializados</h2>
<div class="module-card">
  <div class="module-header">
    <span class="module-name">3. Consultas, Cirugías, Inmunizaciones y Laboratorio</span>
    <span class="module-tag">Modulo 3 · Hospitalario</span>
  </div>
  <p><strong>Clases Principales:</strong> <code>VistaConsultasMedicasPanel</code>, <code>VistaCirugiasQuirofanoPanel</code>, <code>VistaVacunacionDesparasitacionPanel</code>, <code>VistaLaboratorioImagenesPanel</code>.</p>
  <p><strong>Objetivo Funcional:</strong> Trazabilidad del acto médico veterinario, programación y consentimiento informado de cirugías de alta complejidad, control de inmunizaciones y órdenes de laboratorio e imagenología (Rayos X y Ecografías).</p>
  <ul>
    <li><strong>Control Quirúrgico:</strong> Registro de anestesia, cirujano titular, riesgo ASA y estado post-operatorio.</li>
    <li><strong>Laboratorio Clínico:</strong> Seguimiento de perfiles bioquímicos, hemogramas y muestras citológicas.</li>
  </ul>
</div>

<h2>Módulo 4: Estética, Hospedaje y Adopciones</h2>
<div class="module-card">
  <div class="module-header">
    <span class="module-name">4. Spa Canino, Hospitalización UCI, Hotel y Adopciones</span>
    <span class="module-tag">Modulo 4 · Bienestar</span>
  </div>
  <p><strong>Clases Principales:</strong> <code>VistaGroomingPeluqueriaPanel</code>, <code>VistaHospitalizacionPanel</code>, <code>VistaHotelGuarderiaPanel</code>, <code>VistaAdopcionesPanel</code>.</p>
  <p><strong>Objetivo Funcional:</strong> Gestión de servicios no asistenciales y de internamiento prolongado.</p>
  <ul>
    <li><strong>Grooming Spa:</strong> Gestión de turnos por estilista, tipo de corte de raza, baños medicados dermatológicos y avance de etapas (Espera &rarr; Baño &rarr; Corte/Secado &rarr; Listo &rarr; Entregado).</li>
    <li><strong>Hospitalización UCI:</strong> Monitoreo de boxes climatizados, fluidoterapia continua (ml/h), medicación intrahospitalaria activa y notas de enfermería 24/7.</li>
    <li><strong>Hotel y Guardería:</strong> Ocupación de suites caninas/felinas, control de noches de estadía, dietas supervisadas y paseos recreativos.</li>
    <li><strong>Adopciones Responsables:</strong> Catálogo de animales rescatados según Ley 30407, postulaciones, periodos de prueba y actas de compromiso legal.</li>
  </ul>
</div>

<!-- MODULOS 5 AL 7 -->
<h1>4. Especificación: Inventario, Finanzas y Personal (5 al 7)</h1>

<h2>Módulo 5: Inventario y Farmacia Veterinaria</h2>
<div class="module-card">
  <div class="module-header">
    <span class="module-name">5. Farmacopea, Kardex de Lotes y Proveedores</span>
    <span class="module-tag">Modulo 5 · Cadena de Suministro</span>
  </div>
  <p><strong>Clases Principales:</strong> <code>VistaCatalogoProductosPanel</code>, <code>VistaControlStockLotesPanel</code>, <code>VistaProveedoresOrdenesPanel</code>, <code>VistaAjustesMermasPanel</code>.</p>
  <ul>
    <li><strong>Kardex de Lotes:</strong> Trazabilidad estricta de medicamentos con fechas de vencimiento, alertas de stock mínimo y registro sanitario.</li>
    <li><strong>Proveedores y Órdenes de Compra:</strong> Homologación de laboratorios farmacéuticos (Zoetis, MSD, Boehringer) con RUC peruano y cálculo automático de IGV (18%).</li>
    <li><strong>Ajustes y Mermas:</strong> Registro de roturas, vencimientos o auditorías físicas de almacén con responsable y justificación técnica.</li>
  </ul>
</div>

<h2>Módulo 6: Finanzas, Ventas POS y Control de Caja</h2>
<div class="module-card">
  <div class="module-header">
    <span class="module-name">6. Punto de Venta POS, Cuentas por Cobrar/Pagar y Caja Chica</span>
    <span class="module-tag">Modulo 6 · Finanzas</span>
  </div>
  <p><strong>Clases Principales:</strong> <code>VistaPuntoVentaPOSPanel</code>, <code>VistaCuentasCobrarPagarPanel</code>, <code>VistaControlCajaChicaPanel</code>, <code>VistaEgresosOperativosPanel</code>.</p>
  <ul>
    <li><strong>POS y Facturación Electrónica:</strong> Emisión de Boletas de Venta y Facturas Electrónicas con desglose de Subtotal, IGV (18%) y Total en Soles peruanos (PEN S/.), con medios de pago Niubiz, Izipay, Yape y Plin.</li>
    <li><strong>Cuentas por Cobrar y Pagar:</strong> Monitoreo de créditos a clientes y deudas con proveedores, cálculo de días de mora y estado de cuenta individual.</li>
    <li><strong>Control de Caja Chica:</strong> Registro de ingresos, reembolsos y gastos menores con arqueo en vivo y saldo disponible.</li>
    <li><strong>Gestión de Egresos Operativos:</strong> Presupuestación de costos fijos (alquiler, energía, nómina, reactivos) con porcentaje de ejecución mensual.</li>
  </ul>
</div>

<h2>Módulo 7: Personal y Recursos Humanos</h2>
<div class="module-card">
  <div class="module-header">
    <span class="module-name">7. Veterinarios Colegiados, Personal de Apoyo y Guardias</span>
    <span class="module-tag">Modulo 7 · Talento Humano</span>
  </div>
  <p><strong>Clases Principales:</strong> <code>VistaVeterinariosPanel</code>, <code>VistaPersonalApoyoPanel</code>, <code>VistaHorariosTurnosPanel</code>, <code>VistaAsistenciasPermisosPanel</code>.</p>
  <ul>
    <li><strong>Directorio de Veterinarios:</strong> Registro de médicos con número de colegiatura (CMVP), especialidad (Cardiología, Cirugía, Felinos, Exóticos) y calificación promedio con icono de estrella dorada.</li>
    <li><strong>Personal de Apoyo (ATV):</strong> Registro de auxiliares técnicos veterinarios, estilistas caninos y recepcionistas.</li>
    <li><strong>Cuadrante de Turnos y Guardias:</strong> Planificación semanal de turnos diurnos, vespertinos y guardias nocturnas 24 horas.</li>
    <li><strong>Pase de Lista y Permisos:</strong> Control diario de asistencias, tardanzas (+10 min tolerancia) y gestión de permisos/licencias laborales.</li>
  </ul>
</div>

<!-- MODULOS 8 AL 10 -->
<h1>5. Especificación: Inteligencia, Auditoría y Configuración (8 al 10)</h1>

<h2>Módulo 8: Inteligencia de Negocio y Reportes (BI)</h2>
<div class="module-card">
  <div class="module-header">
    <span class="module-name">8. Tableros Ejecutivos, Gráficos y Exportador</span>
    <span class="module-tag">Modulo 8 · Analítica</span>
  </div>
  <p><strong>Clases Principales:</strong> <code>VistaTablerosMandoPanel</code>, <code>VistaReportesClinicosPanel</code>, <code>VistaReportesFinancierosPanel</code>, <code>VistaExportadorDatosPanel</code>.</p>
  <ul>
    <li><strong>Gráfico Mensual en Graphics2D:</strong> Renderizado de barras comparativas nativas de ingresos clínicos vs farmacia a 12 meses.</li>
    <li><strong>Métricas de Eficiencia Operativa:</strong> Barras con degradados turquesa corporativo que miden Ocupación de Quirófanos (78%), Puntualidad en Consultas (92%) y Pacientes Recurrentes (65%).</li>
    <li><strong>Exportador Universal:</strong> Generación interactiva de reportes en XLSX, CSV y PDF estructurado con visualizador integrado <code>VisorReporteDialog</code>.</li>
  </ul>
</div>

<h2>Módulo 9: Notificaciones, Documentos y Auditoría</h2>
<div class="module-card">
  <div class="module-header">
    <span class="module-name">9. Centro de Alertas, Canales, Archivo Digital y Logs</span>
    <span class="module-tag">Modulo 9 · Cumplimiento</span>
  </div>
  <p><strong>Clases Principales:</strong> <code>VistaCentroNotificacionesPanel</code>, <code>VistaConfiguracionCanalesPanel</code>, <code>VistaRepositorioDocumentalPanel</code>, <code>VistaLogsTrazabilidadPanel</code>.</p>
  <ul>
    <li><strong>Centro de Notificaciones:</strong> Recepción de alertas de sistema, avisos clínicos y notificaciones de inventario con alternancia de estado Leída / No leída.</li>
    <li><strong>Configuración de Canales:</strong> Gateway para WhatsApp Cloud API, servidor SMTP para correos y servicios de SMS.</li>
    <li><strong>Repositorio Documental Cifrado:</strong> Custodia de consentimientos quirúrgicos, pólizas y facturas con control de hash SHA-256 simulado.</li>
    <li><strong>Pistas de Auditoría Forense:</strong> Logs inmutables que registran cada acción: Usuario, Evento, Dirección IP, Timestamp y resultado ÉXITO/FALLO.</li>
  </ul>
</div>

<h2>Módulo 10: Configuración Global y Módulo de IA</h2>
<div class="module-card">
  <div class="module-header">
    <span class="module-name">10. Parámetros Perú, Roles RBAC, Integraciones e IA</span>
    <span class="module-tag">Modulo 10 · Administración</span>
  </div>
  <p><strong>Clases Principales:</strong> <code>VistaDashboardAdministracionPanel</code>, <code>VistaParametrosGeneralesPanel</code>, <code>VistaUsuariosRolesPanel</code>, <code>VistaIntegracionesExternasPanel</code>, <code>VistaModuloIASoportePanel</code>.</p>
  <ul>
    <li><strong>Parámetros Institucionales Perú:</strong> Configuración oficial de <code>HappyPets S.A.C.</code> con <strong>RUC 20608421154</strong>, domicilio en Miraflores / Lima, zona horaria <code>America/Lima</code> y moneda <code>PEN (S/.)</code>.</li>
    <li><strong>Control de Acceso Basado en Roles (RBAC):</strong> Matriz de permisos para Administrador, Veterinario Titular, Recepcionista, Auxiliar y Auditor Contable.</li>
    <li><strong>Integraciones Externas:</strong> Conexión con SUNAT CPE v2 (Facturación), WhatsApp Cloud API, Niubiz POS y AWS S3 Cloud.</li>
    <li><strong>Asistente Clínico de IA:</strong> Motor inteligente que ejecuta pre-triaje automático según síntomas, categoriza la gravedad y sugiere diagnósticos preliminares.</li>
  </ul>
</div>

<!-- BANCO DE PREGUNTAS Y RESPUESTAS -->
<h1>6. Guía Maestra de Sustentación y Preguntas de Evaluación</h1>
<p>
  A continuación se presenta un banco detallado de 15 preguntas y respuestas esenciales preparadas específicamente para que los integrantes del equipo puedan defender la arquitectura y el código del proyecto ante los jurados evaluadores:
</p>

<div class="callout-exam">
  <div class="callout-title">P1: ¿Por qué eligieron una arquitectura Swing desacoplada y no una aplicación web?</div>
  <p><strong>Respuesta Modelo:</strong> Porque en entornos hospitalarios de emergencia veterinaria 24 horas se requiere máxima velocidad de respuesta (latencia cero), operación fluida en hardware de escritorio sin depender de servidores web o caídas de internet, y acceso directo a periféricos como impresoras térmicas de tickets POS y lectores de códigos de barra para el Kardex.</p>
</div>

<div class="callout-exam">
  <div class="callout-title">P2: ¿Cómo garantizan que los datos no se corrompan entre módulos?</div>
  <p><strong>Respuesta Modelo:</strong> A través del patrón <strong>Singleton</strong> implementado en <code>RepositorioVeterinaria</code>. Existe un único punto de verdad para toda la aplicación. Todas las pantallas leen y escriben sobre la misma instancia en memoria, previniendo duplicidades y manteniendo la integridad referencial entre clientes, mascotas, citas y facturas.</p>
</div>

<div class="callout-exam">
  <div class="callout-title">P3: ¿Cómo funciona la reactividad de las tablas y el panel lateral derecho (Live Hub)?</div>
  <p><strong>Respuesta Modelo:</strong> Se utiliza el patrón <strong>Master-Detail</strong>. Al interactuar con la tabla maestra, un <code>ListSelectionListener</code> intercepta el cambio de fila seleccionada y llama al método <code>mostrarDetalle(...)</code>, el cual inyecta los datos de la entidad en el panel lateral. Si no hay selección, se muestra un panel vacío ilustrativo que orienta al usuario.</p>
</div>

<div class="callout-exam">
  <div class="callout-title">P4: ¿Por qué en varios renderizadores de tablas se utiliza BorderLayout en lugar de FlowLayout?</div>
  <p><strong>Respuesta Modelo:</strong> Porque <code>FlowLayout</code> provoca salto de línea automático cuando el texto excede el ancho disponible de la columna. En filas con altura fija de 48px, el texto se envolvía hacia abajo quedando oculto e invisible. Al usar <code>BorderLayout(8, 0)</code>, el avatar circular se ancla estrictamente al <code>WEST</code> y el texto se ubica en el <code>CENTER</code> con truncamiento elegante de Swing, garantizando legibilidad total.</p>
</div>

<div class="callout-exam">
  <div class="callout-title">P5: ¿Cómo resolvieron el problema de los círculos de avatares que se estiraban como óvalos?</div>
  <p><strong>Respuesta Modelo:</strong> Cuando un componente circular se coloca dentro de un contenedor con <code>BorderLayout</code>, este tiende a estirarlo verticalmente para empatar con los textos del centro. La solución fue doble: (1) envolver el avatar en un <code>avatarBox</code> con tamaño preferido rígido de 46x46 píxeles, y (2) en el método <code>paintComponent</code> calcular el diámetro como el mínimo entre ancho y alto (<code>int s = Math.min(w, h) - 2</code>) centrando el dibujo con coordenadas <code>x = (w - s)/2</code> e <code>y = (h - s)/2</code>. Así, matemáticamente es imposible que se deforme.</p>
</div>

<div class="callout-exam">
  <div class="callout-title">P6: ¿Cómo se implementó la localización tributaria a Perú en el Módulo 10?</div>
  <p><strong>Respuesta Modelo:</strong> Se adaptó toda la lógica de negocio a la normativa fiscal peruana: Identificador fiscal <strong>RUC de 11 dígitos</strong> (20608421154), razón social bajo la forma societaria <strong>S.A.C.</strong>, facturación electrónica integrada con la especificación <strong>SUNAT CPE v2</strong>, cálculo de <strong>IGV al 18%</strong>, moneda oficial en Soles (<strong>PEN S/.</strong>) y pasarelas de pago peruanas (Niubiz, Izipay y billeteras Yape/Plin).</p>
</div>

<div class="callout-exam">
  <div class="callout-title">P7: ¿Cómo se gestionan las barras de progreso personalizadas en el Módulo 8?</div>
  <p><strong>Respuesta Modelo:</strong> El componente estándar <code>JProgressBar</code> en Windows fuerza el color verde fluorescente del Look & Feel nativo del sistema operativo ignorando los estilos de la app. Para solucionarlo, creamos un componente derivado de <code>JComponent</code> con dibujo en <code>Graphics2D</code> y antialiasing, pintando una pista gris suave y una barra con degradado <code>GradientPaint</code> en la paleta turquesa clínica oficial de HappyPets.</p>
</div>

<div class="callout-exam">
  <div class="callout-title">P8: ¿Qué mecanismo se utiliza para el filtrado en tiempo real sin congelar la interfaz?</div>
  <p><strong>Respuesta Modelo:</strong> Se vincula un <code>DocumentListener</code> al campo de texto de búsqueda que reacciona a los eventos <code>insertUpdate</code>, <code>removeUpdate</code> y <code>changedUpdate</code>. El filtrado se ejecuta mediante Java Streams filtrando la lista completa original y reconstruyendo el <code>DefaultTableModel</code> en milisegundos, manteniendo la interfaz 100% fluida.</p>
</div>

<div class="callout-exam">
  <div class="callout-title">P9: ¿Cómo funciona el Módulo de Inteligencia Artificial para el Triaje?</div>
  <p><strong>Respuesta Modelo:</strong> La clase <code>RepositorioVeterinaria</code> incorpora el método <code>simularDiagnosticoTriajeIA(...)</code>. Este procesa la especie, raza, edad, signos vitales y motivo de consulta a través de un motor de reglas clínicas que categoriza el nivel de urgencia, detecta factores de riesgo patológico y genera una hipótesis diagnóstica para orientar al veterinario antes de que entre al tópico.</p>
</div>

<div class="callout-exam">
  <div class="callout-title">P10: ¿Cómo se garantiza la seguridad y trazabilidad en el ERP?</div>
  <p><strong>Respuesta Modelo:</strong> Mediante dos pilares: (1) El módulo de <strong>Roles y Permisos (RBAC)</strong> en el Módulo 10, que restringe las operaciones según el cargo del usuario autenticado; y (2) El módulo de <strong>Logs de Auditoría Forense</strong> en el Módulo 9, donde cada inicio de sesión, alta de mascota, cobro POS o cambio de parámetros queda grabado de forma inmutable con su timestamp exacto, usuario e IP.</p>
</div>

</body>
</html>
"""

def main():
    workspace = os.path.abspath(os.path.dirname(os.path.dirname(__file__)))
    html_path = os.path.join(workspace, "DOCUMENTACION_SISTEMA_HAPPYPETS.html")
    pdf_path = os.path.join(workspace, "DOCUMENTACION_SISTEMA_HAPPYPETS.pdf")

    print("[1] Generando archivo HTML de documentacion...")
    contenido_html = generar_html_manual()
    with open(html_path, "w", encoding="utf-8") as f:
        f.write(contenido_html)
    print(f" -> Guardado: {html_path} ({len(contenido_html)} bytes)")

    print("[2] Compilando a PDF vectorial de alta resolucion con Chrome Headless...")
    chrome_paths = [
        r"C:\Program Files\Google\Chrome\Application\chrome.exe",
        r"C:\Program Files (x86)\Microsoft\Edge\Application\msedge.exe"
    ]
    
    bin_path = None
    for p in chrome_paths:
        if os.path.exists(p):
            bin_path = p
            break
            
    if not bin_path:
        print("ERROR: No se encontro Google Chrome ni Microsoft Edge para renderizar el PDF.")
        sys.exit(1)
        
    print(f" -> Usando navegador: {bin_path}")
    cmd = [
        bin_path,
        "--headless=new",
        "--disable-gpu",
        "--run-all-compositor-stages-before-draw",
        f"--print-to-pdf={pdf_path}",
        html_path
    ]
    
    res = subprocess.run(cmd, capture_output=True)
    if os.path.exists(pdf_path):
        size_kb = os.path.getsize(pdf_path) / 1024.0
        print(f"[OK] Documento PDF generado exitosamente en la raiz:")
        print(f" -> Ruta: {pdf_path}")
        print(f" -> Tamano: {size_kb:.1f} KB")
    else:
        print(f"ERROR al generar PDF. Salida: {res.stderr.decode('utf-8', errors='ignore')}")
        sys.exit(1)

if __name__ == "__main__":
    main()
