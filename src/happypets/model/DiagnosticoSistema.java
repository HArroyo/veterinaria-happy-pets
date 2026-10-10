package happypets.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Modelo para el estado técnico, salud de la base de datos y telemetría (Módulo 10).
 */
public class DiagnosticoSistema implements Serializable {
    private static final long serialVersionUID = 1L;

    private String estadoBaseDatos;
    private String ultimoRespaldoCloud;
    private int ticketsPendientes;
    private String versionSoftware;
    private String estadoGeneral; // "Normal", "Atención", "Crítico"
    private double latenciaMs;
    private double memoriaUsadaMB;
    private double espacioDiscoLibreGB;
    private List<String> logsServidor;

    public DiagnosticoSistema() {
        this.estadoBaseDatos = "En línea (0.12 ms)";
        this.ultimoRespaldoCloud = "Hoy, 03:00 AM";
        this.ticketsPendientes = 0;
        this.versionSoftware = "HappyPets v2.4 LTS (Build 2026.10)";
        this.estadoGeneral = "Normal";
        this.latenciaMs = 0.12;
        this.memoriaUsadaMB = 284.6;
        this.espacioDiscoLibreGB = 112.4;
        this.logsServidor = new ArrayList<>();
        inicializarLogsPorDefecto();
    }

    private void inicializarLogsPorDefecto() {
        logsServidor.add("[INFO] 2026-10-09 03:00:02 - Backup Cloud automatizado completado en AWS S3 (happy-pets-backup-20261009.enc)");
        logsServidor.add("[INFO] 2026-10-09 06:00:15 - Conexión de pool HikariCP verificada: 10/10 conexiones activas y saludables");
        logsServidor.add("[INFO] 2026-10-09 08:30:00 - Sincronización con pasarela de pagos Wompi/Culqi activa (HTTP 200 OK)");
        logsServidor.add("[INFO] 2026-10-09 09:15:22 - API SUNAT/DIAN de facturación electrónica en línea y respondiendo en 84ms");
        logsServidor.add("[INFO] 2026-10-09 11:40:10 - WhatsApp Business Cloud webhook suscrito a mensajes entrantes y confirmaciones de citas");
        logsServidor.add("[INFO] 2026-10-09 13:00:00 - Motor HappyPet-Core-v1.8 de triaje inteligente con GPU en espera y listo");
    }

    public String getEstadoBaseDatos() {
        return estadoBaseDatos;
    }

    public void setEstadoBaseDatos(String estadoBaseDatos) {
        this.estadoBaseDatos = estadoBaseDatos;
    }

    public String getUltimoRespaldoCloud() {
        return ultimoRespaldoCloud;
    }

    public void setUltimoRespaldoCloud(String ultimoRespaldoCloud) {
        this.ultimoRespaldoCloud = ultimoRespaldoCloud;
    }

    public int getTicketsPendientes() {
        return ticketsPendientes;
    }

    public void setTicketsPendientes(int ticketsPendientes) {
        this.ticketsPendientes = ticketsPendientes;
    }

    public String getVersionSoftware() {
        return versionSoftware;
    }

    public void setVersionSoftware(String versionSoftware) {
        this.versionSoftware = versionSoftware;
    }

    public String getEstadoGeneral() {
        return estadoGeneral;
    }

    public void setEstadoGeneral(String estadoGeneral) {
        this.estadoGeneral = estadoGeneral;
    }

    public double getLatenciaMs() {
        return latenciaMs;
    }

    public void setLatenciaMs(double latenciaMs) {
        this.latenciaMs = latenciaMs;
    }

    public double getMemoriaUsadaMB() {
        return memoriaUsadaMB;
    }

    public void setMemoriaUsadaMB(double memoriaUsadaMB) {
        this.memoriaUsadaMB = memoriaUsadaMB;
    }

    public double getEspacioDiscoLibreGB() {
        return espacioDiscoLibreGB;
    }

    public void setEspacioDiscoLibreGB(double espacioDiscoLibreGB) {
        this.espacioDiscoLibreGB = espacioDiscoLibreGB;
    }

    public List<String> getLogsServidor() {
        return new ArrayList<>(logsServidor);
    }

    public void agregarLogServidor(String linea) {
        this.logsServidor.add(linea);
    }
}
