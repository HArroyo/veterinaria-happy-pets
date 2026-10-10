package happypets.ui;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Generador modular en memoria de documentos oficiales en formatos PDF y Excel (CSV / XLS)
 * para la clínica veterinaria Happy Pets.
 * Funciona de manera 100% nativa en Java sin requerir dependencias externas adicionales.
 */
public final class GeneradorDocumentos {

    private static final DateTimeFormatter FORMATO_FECHA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private GeneradorDocumentos() { }

    /**
     * Genera un archivo PDF oficial válido y estructurado en memoria y lo escribe en el archivo destino.
     */
    public static File generarPDF(String titulo, String subtitulo, String[][] metadatos,
                                   String[] columnas, List<Object[]> filas, String resumen, File archivoDestino) throws IOException {
        if (archivoDestino.getParentFile() != null && !archivoDestino.getParentFile().exists()) {
            archivoDestino.getParentFile().mkdirs();
        }

        // Construcción del flujo de contenido del PDF en memoria
        ByteArrayOutputStream streamContent = new ByteArrayOutputStream();
        PrintWriter pw = new PrintWriter(new OutputStreamWriter(streamContent, StandardCharsets.ISO_8859_1));

        // Dibujar encabezado institucional
        // Fondo turquesa superior: rectángulo de (30, 770) a (565, 820)
        pw.println("q");
        pw.println("0 0.737 0.831 rg"); // Turquesa (#00BCD4)
        pw.println("30 760 535 60 re f");
        pw.println("Q");

        // Texto de cabecera blanca
        pw.println("BT");
        pw.println("/F1 16 Tf"); // Helvetica-Bold 16
        pw.println("1 1 1 rg");  // Blanco
        pw.println("45 798 Td");
        pw.println("(" + escaparTextoPdf("CLINICA VETERINARIA HAPPY PETS 24H") + ") Tj");
        pw.println("ET");

        pw.println("BT");
        pw.println("/F2 9 Tf");  // Helvetica 9
        pw.println("1 1 1 rg");
        pw.println("45 776 Td");
        pw.println("(" + escaparTextoPdf("RUC/NIT: 900.842.115-4  |  Urgencias 24 Horas  |  Lima, Peru  |  Tel: +51 (01) 432-9980") + ") Tj");
        pw.println("ET");

        // Título del documento
        int y = 730;
        pw.println("BT");
        pw.println("/F1 14 Tf");
        pw.println("0.06 0.09 0.16 rg"); // Slate oscuro
        pw.println("45 " + y + " Td");
        pw.println("(" + escaparTextoPdf(titulo.toUpperCase()) + ") Tj");
        pw.println("ET");

        if (subtitulo != null && !subtitulo.trim().isEmpty()) {
            y -= 16;
            pw.println("BT");
            pw.println("/F2 10 Tf");
            pw.println("0.28 0.33 0.41 rg");
            pw.println("45 " + y + " Td");
            pw.println("(" + escaparTextoPdf(subtitulo) + ") Tj");
            pw.println("ET");
        }

        // Metadatos en dos columnas
        if (metadatos != null && metadatos.length > 0) {
            y -= 22;
            pw.println("q");
            pw.println("0.94 0.97 0.98 rg"); // Turquesa muy suave
            pw.println("40 " + (y - (metadatos.length * 14)) + " 515 " + ((metadatos.length * 14) + 10) + " re f");
            pw.println("0.85 0.90 0.94 RG 1 w");
            pw.println("40 " + (y - (metadatos.length * 14)) + " 515 " + ((metadatos.length * 14) + 10) + " re s");
            pw.println("Q");

            int yMeta = y;
            for (String[] par : metadatos) {
                if (par != null && par.length >= 2) {
                    pw.println("BT");
                    pw.println("/F1 9 Tf");
                    pw.println("0 0.47 0.53 rg"); // Turquesa oscuro
                    pw.println("50 " + yMeta + " Td");
                    pw.println("(" + escaparTextoPdf(par[0] + ":") + ") Tj");
                    pw.println("ET");

                    pw.println("BT");
                    pw.println("/F2 9 Tf");
                    pw.println("0.12 0.16 0.23 rg");
                    pw.println("170 " + yMeta + " Td");
                    pw.println("(" + escaparTextoPdf(par[1]) + ") Tj");
                    pw.println("ET");

                    yMeta -= 14;
                }
            }
            y = yMeta - 10;
        }

        // Tabla de datos
        if (columnas != null && columnas.length > 0) {
            y -= 15;
            int totalAncho = 515;
            int colAncho = totalAncho / columnas.length;

            // Encabezado de tabla
            pw.println("q");
            pw.println("0 0.59 0.66 rg"); // Turquesa oscuro
            pw.println("40 " + (y - 5) + " 515 20 re f");
            pw.println("Q");

            pw.println("BT");
            pw.println("/F1 9 Tf");
            pw.println("1 1 1 rg");
            for (int c = 0; c < columnas.length; c++) {
                int colX = 45 + (c * colAncho);
                pw.println(colX + " " + y + " Td");
                pw.println("(" + escaparTextoPdf(columnas[c]) + ") Tj");
                if (c < columnas.length - 1) {
                    pw.println("-" + colX + " 0 Td");
                }
            }
            pw.println("ET");

            y -= 22;

            // Filas de datos
            if (filas != null) {
                int contador = 0;
                for (Object[] fila : filas) {
                    if (y < 80) break; // Límite de página estándar
                    boolean par = (contador % 2 == 0);
                    if (par) {
                        pw.println("q");
                        pw.println("0.97 0.98 0.99 rg");
                        pw.println("40 " + (y - 4) + " 515 16 re f");
                        pw.println("Q");
                    }

                    pw.println("BT");
                    pw.println("/F2 8 Tf");
                    pw.println("0.12 0.16 0.23 rg");
                    for (int c = 0; c < columnas.length && c < fila.length; c++) {
                        int colX = 45 + (c * colAncho);
                        String val = fila[c] != null ? String.valueOf(fila[c]) : "";
                        if (val.length() > 24) val = val.substring(0, 21) + "...";
                        pw.println(colX + " " + y + " Td");
                        pw.println("(" + escaparTextoPdf(val) + ") Tj");
                        if (c < columnas.length - 1) {
                            pw.println("-" + colX + " 0 Td");
                        }
                    }
                    pw.println("ET");

                    y -= 16;
                    contador++;
                }
            }
        }

        // Resumen / Totales
        if (resumen != null && !resumen.trim().isEmpty() && y >= 70) {
            y -= 10;
            pw.println("q");
            pw.println("0.93 0.98 0.99 rg");
            pw.println("40 " + (y - 18) + " 515 28 re f");
            pw.println("0 0.737 0.831 RG 1 w");
            pw.println("40 " + (y - 18) + " 515 28 re s");
            pw.println("Q");

            pw.println("BT");
            pw.println("/F1 10 Tf");
            pw.println("0 0.47 0.53 rg");
            pw.println("50 " + (y - 8) + " Td");
            pw.println("(" + escaparTextoPdf(resumen) + ") Tj");
            pw.println("ET");
            y -= 30;
        }

        // Pie de página institucional y firma digital
        pw.println("BT");
        pw.println("/F2 8 Tf");
        pw.println("0.4 0.45 0.5 rg");
        pw.println("45 40 Td");
        pw.println("(" + escaparTextoPdf("Documento generado electronicamente por Happy Pets ERP · Fecha de emision: " + LocalDateTime.now().format(FORMATO_FECHA_HORA)) + ") Tj");
        pw.println("ET");

        pw.flush();
        byte[] contentBytes = streamContent.toByteArray();

        // Ensamble de la estructura PDF 1.4 con referencias cruzadas (XREF)
        List<Long> offsets = new ArrayList<>();
        ByteArrayOutputStream pdfFinal = new ByteArrayOutputStream();

        pdfFinal.write("%PDF-1.4\n".getBytes(StandardCharsets.ISO_8859_1));

        // Objeto 1: Catalog
        offsets.add((long) pdfFinal.size());
        pdfFinal.write("1 0 obj\n<< /Type /Catalog /Pages 2 0 R >>\nendobj\n".getBytes(StandardCharsets.ISO_8859_1));

        // Objeto 2: Pages
        offsets.add((long) pdfFinal.size());
        pdfFinal.write("2 0 obj\n<< /Type /Pages /Kids [3 0 R] /Count 1 >>\nendobj\n".getBytes(StandardCharsets.ISO_8859_1));

        // Objeto 3: Page
        offsets.add((long) pdfFinal.size());
        String objPage = "3 0 obj\n<< /Type /Page /Parent 2 0 R /MediaBox [0 0 595 842]\n"
                + "/Contents 4 0 R\n"
                + "/Resources << /Font << /F1 5 0 R /F2 6 0 R >> >>\n>>\nendobj\n";
        pdfFinal.write(objPage.getBytes(StandardCharsets.ISO_8859_1));

        // Objeto 4: Contents (Stream de dibujo y texto)
        offsets.add((long) pdfFinal.size());
        String streamHeader = "4 0 obj\n<< /Length " + contentBytes.length + " >>\nstream\n";
        pdfFinal.write(streamHeader.getBytes(StandardCharsets.ISO_8859_1));
        pdfFinal.write(contentBytes);
        pdfFinal.write("\nendstream\nendobj\n".getBytes(StandardCharsets.ISO_8859_1));

        // Objeto 5: Font F1 (Helvetica-Bold)
        offsets.add((long) pdfFinal.size());
        pdfFinal.write("5 0 obj\n<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica-Bold >>\nendobj\n".getBytes(StandardCharsets.ISO_8859_1));

        // Objeto 6: Font F2 (Helvetica)
        offsets.add((long) pdfFinal.size());
        pdfFinal.write("6 0 obj\n<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>\nendobj\n".getBytes(StandardCharsets.ISO_8859_1));

        // Tabla XREF
        long startXref = pdfFinal.size();
        pdfFinal.write("xref\n0 7\n0000000000 65535 f \n".getBytes(StandardCharsets.ISO_8859_1));
        for (Long off : offsets) {
            String entry = String.format("%010d 00000 n \n", off);
            pdfFinal.write(entry.getBytes(StandardCharsets.ISO_8859_1));
        }

        // Trailer
        String trailer = "trailer\n<< /Size 7 /Root 1 0 R >>\nstartxref\n" + startXref + "\n%%EOF\n";
        pdfFinal.write(trailer.getBytes(StandardCharsets.ISO_8859_1));

        try (FileOutputStream fos = new FileOutputStream(archivoDestino)) {
            fos.write(pdfFinal.toByteArray());
        }

        return archivoDestino;
    }

    /**
     * Genera un archivo Excel / CSV con UTF-8 BOM para apertura perfecta e inmediata en MS Excel y Google Sheets.
     */
    public static File generarExcel(String titulo, String[] columnas, List<Object[]> filas, File archivoDestino) throws IOException {
        if (archivoDestino.getParentFile() != null && !archivoDestino.getParentFile().exists()) {
            archivoDestino.getParentFile().mkdirs();
        }

        try (FileOutputStream fos = new FileOutputStream(archivoDestino)) {
            // Escribir Byte Order Mark (BOM) UTF-8 para que Excel detecte tildes y caracteres en español
            fos.write(new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF});

            PrintWriter pw = new PrintWriter(new OutputStreamWriter(fos, StandardCharsets.UTF_8));
            pw.println("CLÍNICA VETERINARIA HAPPY PETS 24H - REPORTE OFICIAL");
            pw.println("Título:;" + titulo);
            pw.println("Generado:;" + LocalDateTime.now().format(FORMATO_FECHA_HORA));
            pw.println();

            // Encabezados
            if (columnas != null) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < columnas.length; i++) {
                    sb.append(escaparCsv(columnas[i]));
                    if (i < columnas.length - 1) sb.append(";");
                }
                pw.println(sb.toString());
            }

            // Datos
            if (filas != null) {
                for (Object[] fila : filas) {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < fila.length; i++) {
                        sb.append(escaparCsv(fila[i] != null ? String.valueOf(fila[i]) : ""));
                        if (i < fila.length - 1) sb.append(";");
                    }
                    pw.println(sb.toString());
                }
            }

            pw.flush();
        }

        return archivoDestino;
    }

    private static String escaparTextoPdf(String texto) {
        if (texto == null) return "";
        // Normalizar caracteres en PDF Type 1 Standard Font
        String limpio = texto
                .replace("á", "a").replace("é", "e").replace("í", "i").replace("ó", "o").replace("ú", "u")
                .replace("Á", "A").replace("É", "E").replace("Í", "I").replace("Ó", "O").replace("Ú", "U")
                .replace("ñ", "n").replace("Ñ", "N")
                .replace("\\", "\\\\")
                .replace("(", "\\(")
                .replace(")", "\\)");
        return limpio;
    }

    private static String escaparCsv(String valor) {
        if (valor == null) return "\"\"";
        String v = valor.replace("\"", "\"\"");
        return "\"" + v + "\"";
    }
}
