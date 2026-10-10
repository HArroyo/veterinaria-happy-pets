package happypets.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Modelo de datos para el Submódulo 4.4: Adopciones y Rescates Responsables.
 * Registra el catálogo de rescatados, estado sanitario, perfil de adopción y postulaciones de tutores.
 */
public class MascotaAdopcion {
    public static final DateTimeFormatter FECHA_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private String idMascotaAdopcion;
    private String nombre;
    private String especie; // "Canino", "Felino"
    private String raza; // "Mestizo", "Cruza Poodle", etc.
    private String edadEstimada;
    private String sexo;
    private String tamano; // "Pequeño", "Mediano", "Grande"
    private String temperamento;
    private String historiaRescate;
    private boolean esterilizado;
    private boolean vacunado;
    private boolean desparasitado;
    private String adoptanteNombre;
    private String adoptanteDni;
    private String adoptanteTelefono;
    private String adoptanteDireccion;
    private LocalDate fechaSolicitud;
    private double cuotaDonacion;
    private String estado; // "Disponible", "En Evaluación", "En Adaptación (Prueba)", "Adoptado con Éxito"

    public MascotaAdopcion(String idMascotaAdopcion, String nombre, String especie, String raza,
                           String edadEstimada, String sexo, String tamano, String temperamento,
                           String historiaRescate, boolean esterilizado, boolean vacunado, boolean desparasitado,
                           String adoptanteNombre, String adoptanteDni, String adoptanteTelefono,
                           String adoptanteDireccion, LocalDate fechaSolicitud, double cuotaDonacion, String estado) {
        this.idMascotaAdopcion = idMascotaAdopcion;
        this.nombre = nombre;
        this.especie = especie;
        this.raza = raza;
        this.edadEstimada = edadEstimada;
        this.sexo = sexo;
        this.tamano = tamano;
        this.temperamento = temperamento;
        this.historiaRescate = historiaRescate;
        this.esterilizado = esterilizado;
        this.vacunado = vacunado;
        this.desparasitado = desparasitado;
        this.adoptanteNombre = adoptanteNombre;
        this.adoptanteDni = adoptanteDni;
        this.adoptanteTelefono = adoptanteTelefono;
        this.adoptanteDireccion = adoptanteDireccion;
        this.fechaSolicitud = fechaSolicitud;
        this.cuotaDonacion = cuotaDonacion;
        this.estado = estado != null ? estado : "Disponible";
    }

    public String getIdMascotaAdopcion() { return idMascotaAdopcion; }
    public void setIdMascotaAdopcion(String idMascotaAdopcion) { this.idMascotaAdopcion = idMascotaAdopcion; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEspecie() { return especie; }
    public void setEspecie(String especie) { this.especie = especie; }

    public String getRaza() { return raza; }
    public void setRaza(String raza) { this.raza = raza; }

    public String getEdadEstimada() { return edadEstimada; }
    public void setEdadEstimada(String edadEstimada) { this.edadEstimada = edadEstimada; }

    public String getSexo() { return sexo; }
    public void setSexo(String sexo) { this.sexo = sexo; }

    public String getTamano() { return tamano; }
    public void setTamano(String tamano) { this.tamano = tamano; }

    public String getTemperamento() { return temperamento; }
    public void setTemperamento(String temperamento) { this.temperamento = temperamento; }

    public String getHistoriaRescate() { return historiaRescate; }
    public void setHistoriaRescate(String historiaRescate) { this.historiaRescate = historiaRescate; }

    public boolean isEsterilizado() { return esterilizado; }
    public void setEsterilizado(boolean esterilizado) { this.esterilizado = esterilizado; }

    public boolean isVacunado() { return vacunado; }
    public void setVacunado(boolean vacunado) { this.vacunado = vacunado; }

    public boolean isDesparasitado() { return desparasitado; }
    public void setDesparasitado(boolean desparasitado) { this.desparasitado = desparasitado; }

    public String getAdoptanteNombre() { return adoptanteNombre; }
    public void setAdoptanteNombre(String adoptanteNombre) { this.adoptanteNombre = adoptanteNombre; }

    public String getAdoptanteDni() { return adoptanteDni; }
    public void setAdoptanteDni(String adoptanteDni) { this.adoptanteDni = adoptanteDni; }

    public String getAdoptanteTelefono() { return adoptanteTelefono; }
    public void setAdoptanteTelefono(String adoptanteTelefono) { this.adoptanteTelefono = adoptanteTelefono; }

    public String getAdoptanteDireccion() { return adoptanteDireccion; }
    public void setAdoptanteDireccion(String adoptanteDireccion) { this.adoptanteDireccion = adoptanteDireccion; }

    public LocalDate getFechaSolicitud() { return fechaSolicitud; }
    public void setFechaSolicitud(LocalDate fechaSolicitud) { this.fechaSolicitud = fechaSolicitud; }
    public String getFechaSolicitudFormateada() { return fechaSolicitud != null ? fechaSolicitud.format(FECHA_FORMATTER) : "-"; }

    public double getCuotaDonacion() { return cuotaDonacion; }
    public void setCuotaDonacion(double cuotaDonacion) { this.cuotaDonacion = cuotaDonacion; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
