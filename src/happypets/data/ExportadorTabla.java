package happypets.data;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/** Genera documentos en memoria; guardar un archivo es una acción independiente del usuario. */
public final class ExportadorTabla {
    private ExportadorTabla() {}

    public static byte[] generar(List<List<String>> filas, String formato) {
        try {
            return switch (formato.toUpperCase(Locale.ROOT)) {
                case "CSV" -> csv(filas);
                case "XLSX" -> excel(filas);
                case "PDF" -> pdf(filas);
                default -> throw new IllegalArgumentException("Formato de exportación no admitido.");
            };
        } catch (IOException ex) {
            throw new IllegalStateException("No se pudo generar la exportación.", ex);
        }
    }

    private static byte[] csv(List<List<String>> filas) {
        StringBuilder texto = new StringBuilder("\ufeff");
        for (List<String> fila : filas) {
            texto.append(fila.stream().map(v -> "\"" + seguroCsv(v).replace("\"", "\"\"") + "\"")
                    .collect(java.util.stream.Collectors.joining(";"))).append("\r\n");
        }
        return texto.toString().getBytes(StandardCharsets.UTF_8);
    }

    private static String seguroCsv(String valor) {
        String v = valor == null ? "" : valor;
        return v.matches("^[=+@\\-].*") ? "'" + v : v;
    }

    private static String xml(String v) {
        return (v == null ? "" : v).replaceAll("[\\x00-\\x08\\x0B\\x0C\\x0E-\\x1F]", "")
                .replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }

    private static byte[] excel(List<List<String>> filas) throws IOException {
        ByteArrayOutputStream salida = new ByteArrayOutputStream();
        try (ZipOutputStream zip = new ZipOutputStream(salida, StandardCharsets.UTF_8)) {
            entrada(zip, "[Content_Types].xml", "<Types xmlns=\"http://schemas.openxmlformats.org/package/2006/content-types\"><Default Extension=\"rels\" ContentType=\"application/vnd.openxmlformats-package.relationships+xml\"/><Default Extension=\"xml\" ContentType=\"application/xml\"/><Override PartName=\"/xl/workbook.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet.main+xml\"/><Override PartName=\"/xl/worksheets/sheet1.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml\"/></Types>");
            entrada(zip, "_rels/.rels", "<Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\"><Relationship Id=\"rId1\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument\" Target=\"xl/workbook.xml\"/></Relationships>");
            entrada(zip, "xl/workbook.xml", "<workbook xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\" xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\"><sheets><sheet name=\"Datos\" sheetId=\"1\" r:id=\"rId1\"/></sheets></workbook>");
            entrada(zip, "xl/_rels/workbook.xml.rels", "<Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\"><Relationship Id=\"rId1\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet\" Target=\"worksheets/sheet1.xml\"/></Relationships>");
            StringBuilder hoja = new StringBuilder("<worksheet xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\"><sheetData>");
            for (int i = 0; i < filas.size(); i++) {
                hoja.append("<row r=\"").append(i + 1).append("\">");
                for (String valor : filas.get(i)) hoja.append("<c t=\"inlineStr\"><is><t xml:space=\"preserve\">").append(xml(valor)).append("</t></is></c>");
                hoja.append("</row>");
            }
            entrada(zip, "xl/worksheets/sheet1.xml", hoja.append("</sheetData></worksheet>").toString());
        }
        return salida.toByteArray();
    }

    private static void entrada(ZipOutputStream zip, String nombre, String contenido) throws IOException {
        zip.putNextEntry(new ZipEntry(nombre));
        zip.write(("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + contenido).getBytes(StandardCharsets.UTF_8));
        zip.closeEntry();
    }

    private static byte[] pdf(List<List<String>> filas) throws IOException {
        List<String> lineas = new ArrayList<>();
        for (List<String> fila : filas) {
            String linea = String.join(" | ", fila);
            for (int inicio = 0; inicio < Math.max(1, linea.length()); inicio += 110)
                lineas.add(linea.substring(inicio, Math.min(inicio + 110, linea.length())));
        }
        int paginas = Math.max(1, (lineas.size() + 45) / 46);
        List<String> objetos = new ArrayList<>();
        objetos.add("<< /Type /Catalog /Pages 2 0 R >>");
        StringBuilder hijos = new StringBuilder();
        for (int i = 0; i < paginas; i++) hijos.append(4 + i * 2).append(" 0 R ");
        objetos.add("<< /Type /Pages /Count " + paginas + " /Kids [" + hijos + "] >>");
        objetos.add("<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica /Encoding /WinAnsiEncoding >>");
        for (int i = 0; i < paginas; i++) {
            objetos.add("<< /Type /Page /Parent 2 0 R /MediaBox [0 0 842 595] /Resources << /Font << /F1 3 0 R >> >> /Contents " + (5 + i * 2) + " 0 R >>");
            StringBuilder contenido = new StringBuilder("BT /F1 10 Tf 12 TL 30 560 Td ");
            for (int j = i * 46; j < Math.min(lineas.size(), (i + 1) * 46); j++)
                contenido.append('(').append(lineas.get(j).replace("\\", "\\\\").replace("(", "\\(").replace(")", "\\)").replaceAll("[\\r\\n]", " ")).append(") Tj T* ");
            contenido.append("ET");
            objetos.add("<< /Length " + bytesPdf(contenido.toString()).length + " >>\nstream\n" + contenido + "\nendstream");
        }
        ByteArrayOutputStream salida = new ByteArrayOutputStream();
        salida.write(bytesPdf("%PDF-1.4\n"));
        List<Integer> posiciones = new ArrayList<>();
        for (int i = 0; i < objetos.size(); i++) {
            posiciones.add(salida.size());
            salida.write(bytesPdf((i + 1) + " 0 obj\n" + objetos.get(i) + "\nendobj\n"));
        }
        int xref = salida.size();
        salida.write(bytesPdf("xref\n0 " + (objetos.size() + 1) + "\n0000000000 65535 f \n"));
        for (int posicion : posiciones) salida.write(bytesPdf(String.format(Locale.ROOT, "%010d 00000 n \n", posicion)));
        salida.write(bytesPdf("trailer\n<< /Size " + (objetos.size() + 1) + " /Root 1 0 R >>\nstartxref\n" + xref + "\n%%EOF\n"));
        return salida.toByteArray();
    }

    private static byte[] bytesPdf(String texto) { return texto.getBytes(java.nio.charset.Charset.forName("windows-1252")); }
}
