package ke.co.mwalimuai.export;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.pdf.PdfDocument;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import ke.co.mwalimuai.database.LessonEntity;

/**
 * Generates professionally formatted PDF lesson plans offline using Android PdfDocument.
 */
public class PdfExportManager {

    private static final String TAG = "PdfExportManager";

    public static File exportLessonToPdf(Context context, LessonEntity lesson) throws IOException {
        File exportDir = new File(context.getCacheDir(), "exported_lessons");
        if (!exportDir.exists()) {
            exportDir.mkdirs();
        }

        String safeTitle = lesson.getSubStrand().replaceAll("[^a-zA-Z0-9_-]", "_");
        String filename = "Lesson_" + safeTitle + "_" + System.currentTimeMillis() + ".pdf";
        File pdfFile = new File(exportDir, filename);

        final int PAGE_WIDTH = 595; // A4 standard width in points at 72 dpi
        final int PAGE_HEIGHT = 842; // A4 standard height
        final int MARGIN = 40;
        final int CONTENT_WIDTH = PAGE_WIDTH - 2 * MARGIN;

        PdfDocument document = new PdfDocument();
        int pageNumber = 1;

        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNumber).create();
        PdfDocument.Page page = document.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);

        int y = MARGIN;

        // Top Accent Banner
        paint.setColor(Color.parseColor("#1B5E20"));
        canvas.drawRect(0, 0, PAGE_WIDTH, 70, paint);

        // Header Title
        paint.setColor(Color.WHITE);
        paint.setTextSize(18f);
        paint.setFakeBoldText(true);
        canvas.drawText("CBE — LESSON PLAN", MARGIN, 36, paint);

        paint.setTextSize(10f);
        paint.setFakeBoldText(false);
        canvas.drawText("Kenyan Grade 6 Mathematics • Competency-Based Education (CBE)", MARGIN, 54, paint);

        y = 95;

        // Metadata Box
        paint.setColor(Color.parseColor("#F1F5F9"));
        canvas.drawRect(MARGIN, y, PAGE_WIDTH - MARGIN, y + 65, paint);

        paint.setColor(Color.parseColor("#CBD5E1"));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(1f);
        canvas.drawRect(MARGIN, y, PAGE_WIDTH - MARGIN, y + 65, paint);
        paint.setStyle(Paint.Style.FILL);

        paint.setColor(Color.parseColor("#0F172A"));
        paint.setTextSize(9f);
        paint.setFakeBoldText(true);
        canvas.drawText("Strand: " + lesson.getStrand(), MARGIN + 12, y + 18, paint);
        canvas.drawText("Sub-strand: " + lesson.getSubStrand(), MARGIN + 12, y + 34, paint);
        canvas.drawText("Duration: " + lesson.getDurationMinutes() + " Mins", MARGIN + 12, y + 50, paint);

        String dateStr = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(new Date(lesson.getCreatedTimestamp()));
        canvas.drawText("Class Size: " + lesson.getClassSize() + " Learners", MARGIN + 260, y + 18, paint);
        canvas.drawText("Curriculum ID: " + lesson.getCurriculumId(), MARGIN + 260, y + 34, paint);
        canvas.drawText("Date: " + dateStr, MARGIN + 260, y + 50, paint);

        y += 85;

        // Sections with automatic multi-page pagination
        String[][] sections = new String[][]{
                {"1. Lesson Title & Learning Outcomes", lesson.getLessonTitle() + "\n" + lesson.getLearningObjective()},
                {"2. Learning Resources & Preparation", lesson.getResourcesNeeded()},
                {"3. Lesson Introduction (Hook & Demonstration)", lesson.getIntroduction()},
                {"4. Lesson Development & Exit Ticket", lesson.getLessonDevelopment()},
                {"5. Formative Assessment & 4-Tier CBE Rubric", lesson.getAssessmentMethods()},
                {"6. Large Class Squad Roles & Inclusion", lesson.getLargeClassAdaptation() + "\n\n" + lesson.getInclusionNotes()},
                {"7. Teacher Reflection", lesson.getReflectionQuestion()}
        };

        for (String[] sec : sections) {
            String secTitle = sec[0];
            String secBody = sec[1] != null ? sec[1] : "";

            textPaint.setColor(Color.parseColor("#1E293B"));
            textPaint.setTextSize(9f);
            StaticLayout measureLayout = StaticLayout.Builder.obtain(secBody, 0, secBody.length(), textPaint, CONTENT_WIDTH)
                    .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                    .setLineSpacing(2f, 1f)
                    .setIncludePad(false)
                    .build();

            int neededHeight = 26 + measureLayout.getHeight() + 14;
            if (y + neededHeight > PAGE_HEIGHT - 50 && y > MARGIN + 30) {
                drawFooter(canvas, pageNumber, PAGE_WIDTH, PAGE_HEIGHT, paint);
                document.finishPage(page);

                pageNumber++;
                pageInfo = new PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNumber).create();
                page = document.startPage(pageInfo);
                canvas = page.getCanvas();
                y = MARGIN;
            }

            y = drawSection(canvas, secTitle, secBody, MARGIN, y, CONTENT_WIDTH, textPaint, paint);
        }

        drawFooter(canvas, pageNumber, PAGE_WIDTH, PAGE_HEIGHT, paint);
        document.finishPage(page);

        try (FileOutputStream fos = new FileOutputStream(pdfFile)) {
            document.writeTo(fos);
        } finally {
            document.close();
        }

        Log.i(TAG, "Successfully exported PDF to: " + pdfFile.getAbsolutePath());
        return pdfFile;
    }

    private static int drawSection(Canvas canvas, String sectionTitle, String body, int x, int y, int width, TextPaint textPaint, Paint bgPaint) {
        // Section Title Pill
        bgPaint.setColor(Color.parseColor("#DCFCE7"));
        bgPaint.setStyle(Paint.Style.FILL);
        canvas.drawRoundRect(x, y, x + width, y + 20, 4f, 4f, bgPaint);

        bgPaint.setColor(Color.parseColor("#1B5E20"));
        bgPaint.setTextSize(10f);
        bgPaint.setFakeBoldText(true);
        canvas.drawText(sectionTitle, x + 8, y + 14, bgPaint);

        y += 26;

        // Section Body
        textPaint.setColor(Color.parseColor("#1E293B"));
        textPaint.setTextSize(9f);

        StaticLayout layout = StaticLayout.Builder.obtain(body != null ? body : "", 0, (body != null ? body.length() : 0), textPaint, width)
                .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                .setLineSpacing(2f, 1f)
                .setIncludePad(false)
                .build();

        canvas.save();
        canvas.translate(x, y);
        layout.draw(canvas);
        canvas.restore();

        return y + layout.getHeight() + 14;
    }

    private static void drawFooter(Canvas canvas, int pageNumber, int width, int height, Paint paint) {
        paint.setColor(Color.parseColor("#94A3B8"));
        paint.setTextSize(8f);
        paint.setFakeBoldText(false);
        canvas.drawText("CBE • Offline Lesson Planner for Kenya", 40, height - 20, paint);
        canvas.drawText("Page " + pageNumber, width - 80, height - 20, paint);
    }
}
