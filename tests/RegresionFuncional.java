package happypets;

import happypets.auth.ServicioAutenticacion;
import happypets.data.RepositorioVeterinaria;
import happypets.model.*;
import java.time.*;
import java.util.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.zip.*;

/** Ejecutar con -ea en un proceso nuevo para aislar los datos de prueba en memoria. */
public final class RegresionFuncional {
    private static int comprobaciones;
    private static void verificar(boolean condicion, String mensaje) {
        comprobaciones++;
        if (!condicion) throw new AssertionError(mensaje);
    }
    private static void rechazar(Runnable accion, String mensaje) {
        try { accion.run(); } catch (IllegalArgumentException ex) { comprobaciones++; return; }
        throw new AssertionError(mensaje);
    }
    private static VentaPOS venta(List<ItemVentaPOS> items, String estado) {
        return new VentaPOS(null, null, "Ticket POS", LocalDateTime.now(), "Prueba", "-", "-", items,
                0, "Tarjeta", 100000, estado, "Prueba");
    }
    public static void main(String[] args) throws Exception {
        var repo = RepositorioVeterinaria.getInstancia();
        var auth = ServicioAutenticacion.getInstancia();
        verificar(repo.getUsuariosSistema().size() == 14, "Conservar usuarios precargados");
        verificar(auth.autenticar("admin", "admin").isPresent(), "Compatibilidad admin/admin");
        verificar(auth.puedeAccederModulo(10), "Administrador autorizado");
        auth.cerrarSesion();
        verificar(auth.getSesionActual() == null && !auth.puedeAccederModulo(1), "Logout sin sesión implícita");
        verificar(auth.autenticar("lgomez", "aux123").isEmpty(), "Usuario inactivo rechazado");
        repo.guardarUsuarioSistema(new Usuario("prueba", "clave", "Prueba", "Recepcionista", "prueba@example.invalid"));
        verificar(auth.autenticar("prueba", "clave").isPresent(), "Usuario creado en memoria puede entrar");
        verificar(auth.puedeAccederModulo(2) && !auth.puedeAccederModulo(10), "Permisos por rol");
        var rol = repo.getRolesPermisos().stream().filter(r -> r.getNombreRol().equals("Recepcionista")).findFirst().orElseThrow();
        rol.asignarPermiso(ServicioAutenticacion.MODULOS[1], false);
        repo.guardarRolPermiso(rol);
        verificar(!auth.puedeAccederModulo(2), "Cambios de permisos efectivos");
        repo.guardarUsuarioSistema(new Usuario("prueba", "nueva", "Prueba", "Recepcionista", "prueba@example.invalid"));
        auth.cerrarSesion();
        verificar(auth.autenticar("prueba", "clave").isEmpty() && auth.autenticar("prueba", "nueva").isPresent(), "Cambio de contraseña efectivo");

        var producto = repo.getProductosFarmacia().get(0);
        int stock = producto.getStockActual();
        int ventas = repo.getVentasPOS().size();
        var exceso = venta(List.of(new ItemVentaPOS(producto.getCodigo(), "Producto", "Producto", stock + 1, 1)), "Pagada");
        rechazar(() -> repo.guardarVentaPOS(exceso), "Rechazar sobreventa");
        verificar(producto.getStockActual() == stock && repo.getVentasPOS().size() == ventas && exceso.getIdVenta() == null, "Sobreventa sin mutaciones");
        var duplicados = venta(List.of(new ItemVentaPOS(producto.getCodigo(), "Producto", "Producto", stock, 1),
                new ItemVentaPOS(producto.getCodigo().toLowerCase(), "Producto", "Producto", 1, 1)), "Pagada");
        rechazar(() -> repo.guardarVentaPOS(duplicados), "Agregar cantidades del mismo SKU");
        var item = new ItemVentaPOS(producto.getCodigo(), "Producto", "Producto", 1, 118);
        var pagada = venta(List.of(item), "Pagada");
        verificar(Math.abs(pagada.getIgv() - 18) < 1e-9 && pagada.getTotal() == 118, "IGV incluido");
        item.setCantidad(500);
        verificar(pagada.getItems().get(0).getCantidad() == 1, "Detalle independiente del carrito");
        double ingresos = repo.getMetricasOperativas().stream().mapToDouble(MetricaMensualIngreso::getTotalIngresos).sum();
        repo.guardarVentaPOS(pagada);
        verificar(producto.getStockActual() == stock - 1, "Descontar stock");
        verificar(Math.abs(repo.getMetricasOperativas().stream().mapToDouble(MetricaMensualIngreso::getTotalIngresos).sum() - ingresos - 118) < 1e-8, "Reporte refleja venta");
        rechazar(() -> repo.guardarVentaPOS(pagada), "No registrar venta dos veces");
        repo.guardarMovimientoCajaChica(new MovimientoCajaChica(null, LocalDate.now(), "Ingreso", "Cobro POS " + pagada.getNumeroComprobante(), 118, "Prueba", pagada.getNumeroComprobante()));
        int movimientos = repo.getMovimientosCajaChica().size();
        repo.anularVentaPOS(pagada.getIdVenta());
        repo.anularVentaPOS(pagada.getIdVenta());
        verificar(producto.getStockActual() == stock, "Anulación reintegra una sola vez");
        verificar(repo.getMovimientosCajaChica().size() == movimientos + 1, "Reversión de caja una sola vez");
        verificar(Math.abs(repo.getMetricasOperativas().stream().mapToDouble(MetricaMensualIngreso::getTotalIngresos).sum() - ingresos) < 1e-8, "Reporte excluye anuladas");
        var cotizacion = venta(List.of(new ItemVentaPOS(producto.getCodigo(), "Producto", "Producto", 1, 1)), "Cotización");
        repo.guardarVentaPOS(cotizacion); repo.anularVentaPOS(cotizacion.getIdVenta());
        verificar(producto.getStockActual() == stock, "Cotización no altera stock al anular");
        rechazar(() -> repo.guardarVentaPOS(venta(List.of(new ItemVentaPOS("SERV", "Servicio", "Servicio", -1, 10)), "Pagada")), "Rechazar cantidad negativa");
        rechazar(() -> repo.guardarVentaPOS(venta(List.of(new ItemVentaPOS("SERV", "Servicio", "Servicio", 1, Double.NaN)), "Pagada")), "Rechazar NaN");
        int kardex = repo.getMovimientosStock().size();
        repo.registrarMovimientoStock(new LoteMovimientoStock(null, producto.getCodigo(), producto.getNombre(), "PRUEBA",
                "Ajuste Positivo (+ Sobrante)", 2, stock, stock + 2, LocalDate.now(), LocalDate.now().plusMonths(1), "Prueba", ""));
        verificar(producto.getStockActual() == stock + 2, "Ajuste positivo incrementa stock");
        rechazar(() -> repo.registrarMovimientoStock(new LoteMovimientoStock(null, producto.getCodigo(), producto.getNombre(), "PRUEBA",
                "Salida", stock + 3, stock + 2, -1, LocalDate.now(), LocalDate.now().plusMonths(1), "Prueba", "")), "Salida excesiva rechazada");
        verificar(repo.getMovimientosStock().size() == kardex + 1 && producto.getStockActual() == stock + 2, "Salida rechazada sin movimiento ni cambio de stock");

        LocalDate fecha = LocalDate.of(2035, 1, 1);
        Cita primera = cita(fecha, LocalTime.of(9, 0)); repo.guardarCita(primera);
        rechazar(() -> repo.guardarCita(cita(fecha, LocalTime.of(9, 15))), "Rechazar citas solapadas");
        repo.guardarCita(cita(fecha, LocalTime.of(9, 30)));
        verificar(repo.getCitas().stream().filter(c -> c.getFecha().equals(fecha)).count() == 2, "Horarios consecutivos permitidos");

        for (String formato : List.of("CSV", "XLSX", "PDF")) {
            var exp = repo.generarExportacion("Inventario y Farmacia", formato, "Todos", List.of());
            byte[] contenido = repo.obtenerContenidoExportacion(exp.getIdExportacion());
            verificar(contenido != null && contenido.length > 100 && exp.getTotalFilas() == repo.getProductosFarmacia().size(), "Exportación real " + formato);
            verificar(!java.nio.file.Files.exists(java.nio.file.Path.of(exp.getRutaArchivo())), "Generación solo en memoria " + formato);
            if (formato.equals("CSV")) verificar(new String(contenido, StandardCharsets.UTF_8).contains(producto.getCodigo()), "CSV contiene datos");
            if (formato.equals("XLSX")) {
                boolean hoja = false;
                try (ZipInputStream zip = new ZipInputStream(new ByteArrayInputStream(contenido))) {
                    for (ZipEntry entrada; (entrada = zip.getNextEntry()) != null;) {
                        if (entrada.getName().equals("xl/worksheets/sheet1.xml")) hoja = new String(zip.readAllBytes(), StandardCharsets.UTF_8).contains(producto.getCodigo());
                    }
                }
                verificar(hoja, "XLSX contiene hoja y datos");
            }
            if (formato.equals("PDF")) verificar(new String(contenido, StandardCharsets.ISO_8859_1).startsWith("%PDF-1.4"), "Cabecera PDF");
        }
        auth.cerrarSesion();
        verificar(!repo.getUsuariosSistema().get(0).getPassword().equals("admin123"), "Contraseñas derivadas en memoria");
        verificar(repo.getUsuariosSistema().get(0).validarPassword("admin123"), "Credenciales conservadas");
        var ocupado = repo.getInternamientos().stream().filter(h -> !"Alta Médica".equals(h.getEstado())).findFirst().orElseThrow();
        int hospitalizados = repo.getInternamientos().size();
        rechazar(() -> repo.guardarInternamiento(internamiento(ocupado.getNumeroBox(), "TEST-HOSP")), "Box ocupado rechazado");
        verificar(repo.getInternamientos().size() == hospitalizados, "Rechazo sin duplicar internamiento");
        var nuevoIngreso = internamiento("Box 09", "TEST-HOSP");
        repo.guardarInternamiento(nuevoIngreso);
        verificar(nuevoIngreso.getPesoActualKg() == 12 && nuevoIngreso.getTemperaturaC() == 39 && nuevoIngreso.getFrecuenciaCardiaca() == 120, "Signos vitales conservados");
        rechazar(() -> repo.guardarInternamiento(internamiento("Box 08", "TEST-HOSP")), "Paciente internado no se duplica");
        repo.actualizarEstadoInternamiento(nuevoIngreso.getIdInternamiento(), "Alta Médica");
        repo.guardarInternamiento(internamiento("Box 09", "TEST-HOSP-2"));
        var orden = repo.getOrdenesCompra().stream().filter(o -> !"Recibida en Almacén".equals(o.getEstado()) && !"Cancelada".equals(o.getEstado())).findFirst().orElseThrow();
        int stockCompra = producto.getStockActual(), movimientosCompra = repo.getMovimientosStock().size();
        rechazar(() -> repo.recibirOrdenCompra(orden.getIdOrden(), Map.of("NO-EXISTE", 1)), "Recepción inválida rechazada");
        verificar(producto.getStockActual() == stockCompra && repo.getMovimientosStock().size() == movimientosCompra, "Recepción inválida atómica");
        repo.recibirOrdenCompra(orden.getIdOrden(), Map.of(producto.getCodigo(), 3));
        verificar(producto.getStockActual() == stockCompra + 3 && repo.getMovimientosStock().size() == movimientosCompra + 1, "Recepción actualiza stock y Kardex");
        rechazar(() -> repo.recibirOrdenCompra(orden.getIdOrden(), Map.of(producto.getCodigo(), 3)), "Recepción única");
        var cobrar = repo.getCuentasPorCobrar().stream().filter(c -> c.getSaldo() > 1).findFirst().orElseThrow();
        double saldoAnterior = cobrar.getSaldo();
        int cajaAnterior = repo.getMovimientosCajaChica().size();
        rechazar(() -> repo.registrarAbonoCuentaPorCobrar(cobrar.getIdCuenta(), Double.NaN), "Abono NaN rechazado");
        rechazar(() -> repo.registrarAbonoCuentaPorCobrar(cobrar.getIdCuenta(), saldoAnterior + 1), "Abono superior al saldo rechazado");
        verificar(cobrar.getSaldo() == saldoAnterior && repo.getMovimientosCajaChica().size() == cajaAnterior, "Abonos inválidos sin mutación");
        repo.registrarAbonoCuentaPorCobrar(cobrar.getIdCuenta(), 1);
        verificar(cobrar.getSaldo() == saldoAnterior - 1 && repo.getMovimientosCajaChica().size() == cajaAnterior + 1, "Abono reflejado en caja");
        var pagar = repo.getCuentasPorPagar().stream().filter(c -> c.getSaldo() > 1).findFirst().orElseThrow();
        double saldoPago = pagar.getSaldo();
        repo.registrarPagoCuentaPorPagar(pagar.getIdCuenta(), 1);
        verificar(pagar.getSaldo() == saldoPago - 1 && repo.getMovimientosCajaChica().size() == cajaAnterior + 2, "Pago reflejado en caja");
        var mario = repo.getCuadranteTurnos().stream().filter(t -> t.getNombreProfesional().contains("Mario Silva")).findFirst().orElseThrow();
        var laura = repo.getCuadranteTurnos().stream().filter(t -> t.getNombreProfesional().contains("Laura Morales")).findFirst().orElseThrow();
        String marioAntes = mario.getHorarioDia(5), lauraAntes = laura.getHorarioDia(5);
        repo.resolverPermuta(true);
        verificar(mario.getHorarioDia(5).equals(lauraAntes) && laura.getHorarioDia(5).equals(marioAntes), "Permuta modifica ambos horarios");
        rechazar(() -> repo.resolverPermuta(true), "Permuta no se aplica dos veces");
        rechazar(() -> TurnoSemanal.validarHorario("25:00 - 30:00", "Mañana"), "Horas inválidas rechazadas");
        rechazar(() -> TurnoSemanal.validarHorario("15:00 - 08:00", "Mañana"), "Intervalo inválido rechazado");
        TurnoSemanal.validarHorario("20:00 - 08:00", "Guardia");
        var documento = repo.getDocumentosRepositorio().get(0);
        byte[] original = new byte[]{1, 2, 3}; documento.setContenido(original); original[0] = 9;
        verificar(documento.getContenido()[0] == 1, "Archivo almacenado por copia en memoria");
        byte[] copia = documento.getContenido(); copia[1] = 9;
        verificar(documento.getContenido()[1] == 2, "Descarga no muta el archivo en memoria");
        verificar(auth.autenticar("admin", "admin").isPresent(), "Sesión para auditoría");
        var log = new LogAuditoria(null, "Prueba", "admin_user", LocalDateTime.now(), "127.0.0.1", "ÉXITO", "Prueba");
        repo.registrarLogAuditoria(log);
        verificar(log.getUsuario().equals(auth.getSesionActual().getUsername()) && log.getIdEvento() != null, "Auditoría usa sesión e ID real");
        auth.cerrarSesion();
        var reserva = new ReservaHospedaje(null, "SUITE-PRUEBA", "TEST", "Paciente", "Canino", "Tutor", "000", "000",
                LocalDate.of(2035, 1, 1), LocalDate.of(2035, 1, 3), 2, "Dieta", "Paseos", false, "", 50, 100, "Confirmada");
        repo.guardarReservaHospedaje(reserva);
        var solapada = new ReservaHospedaje(null, "SUITE-PRUEBA", "OTRO", "Otro", "Canino", "Tutor", "000", "000",
                LocalDate.of(2035, 1, 2), LocalDate.of(2035, 1, 4), 2, "Dieta", "Paseos", false, "", 50, 100, "Confirmada");
        rechazar(() -> repo.guardarReservaHospedaje(solapada), "Suite reservada no se duplica");
        repo.actualizarEstadoHospedaje(reserva.getIdReserva(), "Finalizada / Check-out");
        repo.guardarReservaHospedaje(solapada);
        verificar(solapada.getIdReserva() != null, "Check-out libera suite");
        rechazar(() -> Validacion.numero("NaN", "Precio", false), "NaN en formulario rechazado");
        rechazar(() -> Validacion.numero("Infinity", "Precio", false), "Infinito en formulario rechazado");
        rechazar(() -> Validacion.entero("-1", "Stock", false), "Stock negativo rechazado");
        verificar(icono("Hospitalización") != icono("Documento"), "Hospitalización tiene icono propio");
        verificar(icono("Hotel / Guardería") != icono("Documento"), "Hotel tiene icono propio");
        verificar(icono("Adopciones") != icono("Documento"), "Adopciones tiene icono propio");
        verificar(icono("Farmacia") != icono("IA"), "Farmacia no se confunde con IA");
        verificar(icono("Historial Clínico") != icono("IA"), "Historial no se confunde con IA");
        javax.swing.SwingUtilities.invokeAndWait(() -> {
            try {
                var panel = new happypets.modulos.modulo4.VistaHospitalizacionPanel();
                ((javax.swing.JComboBox<?>) campo(panel, "cbBoxAsignado")).setSelectedItem("Box 08");
                ((javax.swing.JTextField) campo(panel, "txtPeso")).setText("13.5");
                ((javax.swing.JTextField) campo(panel, "txtTemperatura")).setText("37.9");
                ((javax.swing.JTextField) campo(panel, "txtFC")).setText("95");
                var ingreso = panel.getClass().getDeclaredMethod("ingresarPacienteHospitalario"); ingreso.setAccessible(true);
                try { ingreso.invoke(panel); }
                catch (java.lang.reflect.InvocationTargetException ex) { if (!(ex.getCause() instanceof java.awt.HeadlessException)) throw ex; }
                var guardado = repo.getInternamientos().stream().filter(h -> "Box 08".equals(h.getNumeroBox())).findFirst().orElseThrow();
                verificar(guardado.getPesoActualKg() == 13.5 && guardado.getTemperaturaC() == 37.9 && guardado.getFrecuenciaCardiaca() == 95, "Formulario hospitalario guarda los signos escritos");
                var financiero = new happypets.modulos.modulo8.VistaReportesFinancierosPanel();
                ((javax.swing.JTextField) campo(financiero, "txtRangoFechas")).setText("01/01/2035 - 31/01/2035");
                try { boton(financiero, "Aplicar Filtro").doClick(); } catch (java.awt.HeadlessException ex) { /* Mensaje final sin pantalla. */ }
                var metricas = financiero.getClass().getDeclaredMethod("metricasFiltradas"); metricas.setAccessible(true);
                verificar(((List<?>) metricas.invoke(financiero)).isEmpty(), "Filtro financiero excluye operaciones fuera del rango");
            } catch (ReflectiveOperationException ex) { throw new AssertionError(ex); }
        });
        System.out.println("Regresión funcional: " + comprobaciones + " comprobaciones correctas; datos de prueba solo en memoria.");
    }
    private static Cita cita(LocalDate fecha, LocalTime hora) {
        return new Cita(null, "TEST", "Paciente", "Canino", "TEST", "Tutor", "000", fecha, hora, 30,
                "Veterinario de prueba", "Consulta", "Programada", "Prueba", "Normal", "", 75);
    }
    private static InternamientoHospitalario internamiento(String box, String mascota) {
        return new InternamientoHospitalario(null, box, "General", mascota, "Paciente", "Canino", "Tutor", "000", "Prueba", "Veterinario", LocalDate.now(), LocalTime.now(), LocalDate.now().plusDays(1), 12, 39, 120, "", "", "", "ESTABLE", 100, "Internado / En Tratamiento");
    }
    private static Object campo(Object objeto, String nombre) throws ReflectiveOperationException {
        var campo = objeto.getClass().getDeclaredField(nombre); campo.setAccessible(true); return campo.get(objeto);
    }
    private static javax.swing.JButton boton(java.awt.Container panel, String texto) {
        for (var componente : panel.getComponents()) {
            if (componente instanceof javax.swing.JButton b && texto.equals(b.getText())) return b;
            if (componente instanceof java.awt.Container c) { var resultado = boton(c, texto); if (resultado != null) return resultado; }
        }
        return null;
    }
    private static int icono(String texto) {
        var icono = (javax.swing.ImageIcon) happypets.ui.Iconos.paraTexto(texto, 16, java.awt.Color.BLACK);
        var imagen = (java.awt.image.BufferedImage) icono.getImage();
        return java.util.Arrays.hashCode(imagen.getRGB(0, 0, 16, 16, null, 0, 16));
    }
}
