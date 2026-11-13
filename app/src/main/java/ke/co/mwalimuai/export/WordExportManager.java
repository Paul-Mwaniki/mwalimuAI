package ke.co.mwalimuai.export;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import ke.co.mwalimuai.database.LessonEntity;

/**
 * Generates genuine Microsoft Word (.docx) documents offline using the Office Open XML (ECMA-376) standard.
 */
public class WordExportManager {

    private static final String TAG = "WordExportManager";

    public static File exportLessonToDocx(Context context, LessonEntity lesson) throws IOException {
        File exportDir = new File(context.getCacheDir(), "exported_lessons");
        if (!exportDir.exists()) {
            exportDir.mkdirs();
        }

        String safeTitle = lesson.getSubStrand().replaceAll("[^a-zA-Z0-9_-]", "_");
        String filename = "Lesson_" + safeTitle + "_" + System.currentTimeMillis() + ".docx";
        File docxFile = new File(exportDir, filename);

        try (FileOutputStream fos = new FileOutputStream(docxFile);
             ZipOutputStream zos = new ZipOutputStream(fos)) {

            // 1. [Content_Types].xml
            addZipEntry(zos, "[Content_Types].xml", getContentTypesXml());

            // 2. _rels/.rels
            addZipEntry(zos, "_rels/.rels", getRootRelsXml());

            // 3. word/_rels/document.xml.rels
            addZipEntry(zos, "word/_rels/document.xml.rels", getDocRelsXml());

            // 4. word/styles.xml
            addZipEntry(zos, "word/styles.xml", getStylesXml());

            // 5. word/document.xml
            addZipEntry(zos, "word/document.xml", buildDocumentXml(lesson));
        }

        Log.i(TAG, "Successfully exported DOCX to: " + docxFile.getAbsolutePath());
        return docxFile;
    }

    private static void addZipEntry(ZipOutputStream zos, String name, String content) throws IOException {
        ZipEntry entry = new ZipEntry(name);
        zos.putNextEntry(entry);
        zos.write(content.getBytes(StandardCharsets.UTF_8));
        zos.closeEntry();
    }

    private static String getContentTypesXml() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<Types xmlns=\"http://schemas.openxmlformats.org/package/2006/content-types\">\n" +
                "  <Default Extension=\"rels\" ContentType=\"application/vnd.openxmlformats-package.relationships+xml\"/>\n" +
                "  <Default Extension=\"xml\" ContentType=\"application/xml\"/>\n" +
                "  <Override PartName=\"/word/document.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.wordprocessingml.document.main+xml\"/>\n" +
                "  <Override PartName=\"/word/styles.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.wordprocessingml.styles+xml\"/>\n" +
                "</Types>";
    }

    private static String getRootRelsXml() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\">\n" +
                "  <Relationship Id=\"rId1\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument\" Target=\"word/document.xml\"/>\n" +
                "</Relationships>";
    }

    private static String getDocRelsXml() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\">\n" +
                "  <Relationship Id=\"rId1\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/styles\" Target=\"styles.xml\"/>\n" +
                "</Relationships>";
    }

    private static String getStylesXml() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<w:styles xmlns:w=\"http://schemas.openxmlformats.org/wordprocessingml/2006/main\">\n" +
                "  <w:docDefaults>\n" +
                "    <w:rPrDefault><w:rPr><w:rFonts w:ascii=\"Calibri\" w:hAnsi=\"Calibri\"/><w:sz w:val=\"22\"/></w:rPr></w:rPrDefault>\n" +
                "  </w:docDefaults>\n" +
                "</w:styles>";
    }

    private static String buildDocumentXml(LessonEntity lesson) {
        String dateStr = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(new Date(lesson.getCreatedTimestamp()));

        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n");
        sb.append("<w:document xmlns:w=\"http://schemas.openxmlformats.org/wordprocessingml/2006/main\">\n");
        sb.append("<w:body>\n");

        // Header Title
        sb.append(paragraph("CBE — LESSON PLAN", true, "1B5E20", 32, true));
        sb.append(paragraph("Kenyan Competency-Based Education (CBE) • Grade 6 Mathematics", false, "475569", 20, true));
        sb.append(emptyLine());

        // Metadata Table
        sb.append("<w:tbl>\n");
        sb.append("  <w:tblPr><w:tblW w:w=\"9500\" w:type=\"dxa\"/><w:tblBorders><w:top w:val=\"single\" w:sz=\"4\" w:space=\"0\" w:color=\"CCCCCC\"/><w:bottom w:val=\"single\" w:sz=\"4\" w:space=\"0\" w:color=\"CCCCCC\"/><w:insideH w:val=\"single\" w:sz=\"4\" w:space=\"0\" w:color=\"EEEEEE\"/></w:tblBorders></w:tblPr>\n");
        sb.append(tableRow("Grade &amp; Learning Area", "Grade 6 — Mathematics", "Date", dateStr));
        sb.append(tableRow("Strand", lesson.getStrand(), "Sub-strand", lesson.getSubStrand()));
        sb.append(tableRow("Duration", lesson.getDurationMinutes() + " Minutes", "Class Size", lesson.getClassSize() + " Learners"));
        sb.append(tableRow("Curriculum Code", lesson.getCurriculumId(), "Lesson Plan ID", lesson.getLessonId()));
        sb.append("</w:tbl>\n");
        sb.append(emptyLine());

        // Section 1: Title & Objective
        sb.append(sectionHeading("1. Lesson Title &amp; Specific Learning Outcomes"));
        sb.append(paragraph("Lesson Title: " + escapeXml(lesson.getLessonTitle()), true, "000000", 22, false));
        sb.append(paragraph(escapeXml(lesson.getLearningObjective()), false, "000000", 22, false));
        sb.append(emptyLine());

        // Section 2: Resources
        sb.append(sectionHeading("2. Suggested Learning Resources"));
        sb.append(paragraph(escapeXml(lesson.getResourcesNeeded()), false, "000000", 22, false));
        sb.append(emptyLine());

        // Section 3: Introduction
        sb.append(sectionHeading("3. Lesson Introduction (Hook &amp; Prior Knowledge — 5 Mins)"));
        sb.append(paragraph(escapeXml(lesson.getIntroduction()), false, "000000", 22, false));
        sb.append(emptyLine());

        // Section 4: Lesson Development
        sb.append(sectionHeading("4. Lesson Development &amp; Exit Ticket"));
        sb.append(paragraph(escapeXml(lesson.getLessonDevelopment()), false, "000000", 22, false));
        sb.append(emptyLine());

        // Section 5: Assessment
        sb.append(sectionHeading("5. Formative Assessment &amp; 4-Tier CBE Rubric"));
        sb.append(paragraph(escapeXml(lesson.getAssessmentMethods()), false, "000000", 22, false));
        sb.append(emptyLine());

        // Section 6: Large Class & Inclusion
        sb.append(sectionHeading("6. Large Class Adaptation &amp; Inclusion"));
        sb.append(paragraph(escapeXml(lesson.getLargeClassAdaptation()), false, "000000", 22, false));
        sb.append(paragraph(escapeXml(lesson.getInclusionNotes()), false, "000000", 22, false));
        sb.append(emptyLine());

        // Section 7: Teacher Reflection
        sb.append(sectionHeading("7. Teacher Reflection &amp; Next Steps"));
        sb.append(paragraph(escapeXml(lesson.getReflectionQuestion()), false, "000000", 22, false));

        // Footer note
        sb.append(emptyLine());
        sb.append(paragraph("Generated offline by CBE Teacher Assistant.", false, "94A3B8", 18, true));

        sb.append("</w:body>\n");
        sb.append("</w:document>");
        return sb.toString();
    }

    private static String sectionHeading(String title) {
        return "<w:p><w:pPr><w:spacing w:before=\"180\" w:after=\"80\"/><w:shd w:val=\"clear\" w:color=\"auto\" w:fill=\"DCFCE7\"/></w:pPr>" +
                "<w:r><w:rPr><w:b/><w:color w:val=\"1B5E20\"/><w:sz w:val=\"24\"/></w:rPr>" +
                "<w:t>  " + escapeXml(title) + "</w:t></w:r></w:p>\n";
    }

    private static String paragraph(String text, boolean bold, String hexColor, int halfPtSize, boolean center) {
        String jc = center ? "<w:jc w:val=\"center\"/>" : "";
        String[] lines = text.split("\n");
        StringBuilder sb = new StringBuilder();
        for (String line : lines) {
            sb.append("<w:p><w:pPr>").append(jc).append("<w:spacing w:after=\"60\"/></w:pPr>");
            sb.append("<w:r><w:rPr>");
            if (bold) sb.append("<w:b/>");
            if (hexColor != null) sb.append("<w:color w:val=\"").append(hexColor).append("\"/>");
            sb.append("<w:sz w:val=\"").append(halfPtSize).append("\"/>");
            sb.append("</w:rPr><w:t>").append(escapeXml(line)).append("</w:t></w:r></w:p>\n");
        }
        return sb.toString();
    }

    private static String emptyLine() {
        return "<w:p><w:pPr><w:spacing w:after=\"80\"/></w:pPr></w:p>\n";
    }

    private static String tableRow(String label1, String val1, String label2, String val2) {
        return "<w:tr>\n" +
                "  <w:tc><w:tcPr><w:tcW w:w=\"2200\" w:type=\"dxa\"/><w:shd w:val=\"clear\" w:color=\"auto\" w:fill=\"F1F5F9\"/></w:tcPr><w:p><w:r><w:rPr><w:b/><w:sz w:val=\"20\"/></w:rPr><w:t>" + escapeXml(label1) + "</w:t></w:r></w:p></w:tc>\n" +
                "  <w:tc><w:tcPr><w:tcW w:w=\"2550\" w:type=\"dxa\"/></w:tcPr><w:p><w:r><w:rPr><w:sz w:val=\"20\"/></w:rPr><w:t>" + escapeXml(val1) + "</w:t></w:r></w:p></w:tc>\n" +
                "  <w:tc><w:tcPr><w:tcW w:w=\"2200\" w:type=\"dxa\"/><w:shd w:val=\"clear\" w:color=\"auto\" w:fill=\"F1F5F9\"/></w:tcPr><w:p><w:r><w:rPr><w:b/><w:sz w:val=\"20\"/></w:rPr><w:t>" + escapeXml(label2) + "</w:t></w:r></w:p></w:tc>\n" +
                "  <w:tc><w:tcPr><w:tcW w:w=\"2550\" w:type=\"dxa\"/></w:tcPr><w:p><w:r><w:rPr><w:sz w:val=\"20\"/></w:rPr><w:t>" + escapeXml(val2) + "</w:t></w:r></w:p></w:tc>\n" +
                "</w:tr>\n";
    }

    private static String escapeXml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }
}
