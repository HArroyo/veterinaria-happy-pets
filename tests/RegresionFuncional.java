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
        System.out.println("Regresión funcional: " + comprobaciones + " comprobaciones correctas; datos de prueba solo en memoria.");
    }
    private static Cita cita(LocalDate fecha, LocalTime hora) {
        return new Cita(null, "TEST", "Paciente", "Canino", "TEST", "Tutor", "000", fecha, hora, 30,
                "Veterinario de prueba", "Consulta", "Programada", "Prueba", "Normal", "", 75);
    }
}
