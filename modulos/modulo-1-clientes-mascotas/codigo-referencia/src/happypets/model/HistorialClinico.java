package happypets.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/** Agrupa cronológicamente las consultas de una mascota. */
public class HistorialClinico {
    private final String codigoMascota;
    private final List<ConsultaClinica> consultas = new ArrayList<>();

    public HistorialClinico(String codigoMascota) { this.codigoMascota = codigoMascota; }
    public String getCodigoMascota() { return codigoMascota; }

    public void agregarConsulta(ConsultaClinica consulta) {
        if (consulta == null || !codigoMascota.equals(consulta.getCodigoMascota())) {
            throw new IllegalArgumentException("La consulta debe pertenecer a la mascota");
        }
        consultas.add(consulta);
        consultas.sort(Comparator.comparing(ConsultaClinica::getFecha).reversed());
    }

    public List<ConsultaClinica> getConsultas() { return Collections.unmodifiableList(consultas); }
}
