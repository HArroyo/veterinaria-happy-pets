package happypets.model;

/** Reglas compartidas para formularios y operaciones del repositorio en memoria. */
public final class Validacion {
    private Validacion() {}
    public static double numero(String texto, String campo, boolean positivo) {
        try { return importe(Double.parseDouble(texto.trim()), campo, positivo); }
        catch (NumberFormatException ex) { throw new IllegalArgumentException(campo + ": ingrese un número válido."); }
    }
    public static double importe(double valor, String campo, boolean positivo) {
        if (!Double.isFinite(valor) || (positivo ? valor <= 0 : valor < 0))
            throw new IllegalArgumentException(campo + ": ingrese un valor " + (positivo ? "positivo." : "mayor o igual a cero."));
        return valor;
    }
    public static int entero(String texto, String campo, boolean positivo) {
        try {
            int valor = Integer.parseInt(texto.trim());
            importe(valor, campo, positivo);
            return valor;
        } catch (NumberFormatException ex) { throw new IllegalArgumentException(campo + ": ingrese un entero válido."); }
    }
}
