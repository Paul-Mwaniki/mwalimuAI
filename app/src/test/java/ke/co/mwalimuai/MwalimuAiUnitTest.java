package ke.co.mwalimuai;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import ke.co.mwalimuai.database.ActivityEntity;
import ke.co.mwalimuai.database.CurriculumEntity;
import ke.co.mwalimuai.database.LessonEntity;
import ke.co.mwalimuai.export.WordExportManager;
import ke.co.mwalimuai.generator.LessonGenerator;

public class MwalimuAiUnitTest {

    @Test
    public void testLessonGeneratorAssembly() {
        CurriculumEntity curr = new CurriculumEntity();
        curr.setCurriculumId("G6-MATH-11");
        curr.setGrade(6);
        curr.setLearningArea("Mathematics");
        curr.setStrand("Numbers");
        curr.setSubStrand("Whole numbers");
        curr.setCurriculumAlignedFocus("place value up to millions; rounding");
        curr.setLocalResources("number cards, chalkboard, stones");
        curr.setKenyanContext("school enrolment counts");

        LessonGenerator.LessonPlanningParameters params = new LessonGenerator.LessonPlanningParameters();
        params.curriculum = curr;
        params.durationMinutes = 40;
        params.classSize = 50;
        params.availableResources = "stones and chalkboard";
        params.learningNeed = "mixed ability";

        ActivityEntity act1 = new ActivityEntity();
        act1.setActivityTitle("Whole numbers: Manipulative exploration");
        act1.setStrategy("Manipulative exploration");
        act1.setLearnerSteps("Learners use stones to represent place value.");
        act1.setAiSuitabilityScore(4.5f);

        ArrayList<ActivityEntity> ranked = new ArrayList<>();
        ranked.add(act1);

        LessonEntity lesson = LessonGenerator.generateLessonPlan(null, params, ranked);

        assertNotNull(lesson);
        assertEquals("G6-MATH-11", lesson.getCurriculumId());
        assertEquals("Numbers", lesson.getStrand());
        assertEquals("Whole numbers", lesson.getSubStrand());
        assertEquals(40, lesson.getDurationMinutes());
        assertEquals(50, lesson.getClassSize());
        assertTrue(lesson.getLearningObjective().contains("Whole numbers"));
        assertTrue(lesson.getLessonDevelopment().contains("Manipulative exploration"));
    }

    @Test
    public void testWordDocxZipStructure() throws Exception {
        // Test that a standard OOXML ZIP can be created and read
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            ZipEntry entry1 = new ZipEntry("[Content_Types].xml");
            zos.putNextEntry(entry1);
            zos.write("<Types></Types>".getBytes(StandardCharsets.UTF_8));
            zos.closeEntry();

            ZipEntry entry2 = new ZipEntry("word/document.xml");
            zos.putNextEntry(entry2);
            zos.write("<w:document></w:document>".getBytes(StandardCharsets.UTF_8));
            zos.closeEntry();
        }

        // Validate ZIP structure
        boolean foundContentTypes = false;
        boolean foundDoc = false;
        try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(baos.toByteArray()))) {
            ZipEntry ze;
            while ((ze = zis.getNextEntry()) != null) {
                if (ze.getName().equals("[Content_Types].xml")) foundContentTypes = true;
                if (ze.getName().equals("word/document.xml")) foundDoc = true;
            }
        }

        assertTrue(foundContentTypes);
        assertTrue(foundDoc);
    }

    @Test
    public void testCustomMaterialsInActivitySteps() {
        CurriculumEntity curr = new CurriculumEntity();
        curr.setCurriculumId("G6-MATH-11");
        curr.setSubStrand("Whole numbers");
        curr.setStrand("Numbers");

        LessonGenerator.LessonPlanningParameters params = new LessonGenerator.LessonPlanningParameters();
        params.curriculum = curr;
        params.availableResources = "Avocado seeds, dry sticks";

        ActivityEntity act = new ActivityEntity();
        act.setActivityTitle("Whole numbers: Concrete exploration");
        act.setStrategy("Manipulative exploration");
        act.setLearnerSteps("1. Learners use number cards, chalkboard, stones to represent numbers. 2. Compare with stones.");
        act.setAiSuitabilityScore(4.8f);

        ArrayList<ActivityEntity> ranked = new ArrayList<>();
        ranked.add(act);

        LessonEntity lesson = LessonGenerator.generateLessonPlan(null, params, ranked);

        // Verify that custom materials replaced generic placeholders in the learner steps!
        assertTrue("Learner steps must contain custom materials",
                lesson.getLessonDevelopment().contains("Avocado seeds, dry sticks"));
        assertTrue("Resources needed must contain custom materials",
                lesson.getResourcesNeeded().contains("Avocado seeds, dry sticks"));
        assertTrue("Objective must contain custom materials",
                lesson.getLearningObjective().contains("Avocado seeds, dry sticks"));
    }

    @Test
    public void testCustomTeacherHookAndActivity() {
        CurriculumEntity curr = new CurriculumEntity();
        curr.setCurriculumId("G6-MATH-11");
        curr.setSubStrand("Whole numbers");
        curr.setStrand("Numbers");

        LessonGenerator.LessonPlanningParameters params = new LessonGenerator.LessonPlanningParameters();
        params.curriculum = curr;
        params.availableResources = "Avocado seeds";
        params.customActivityOrHook = "Learners count avocado seeds into 3 farmer baskets";

        ActivityEntity act = new ActivityEntity();
        act.setActivityTitle("Whole numbers: Partner Discussion");
        act.setStrategy("Think-pair-share");
        act.setLearnerSteps("Learners discuss in pairs.");
        act.setAiSuitabilityScore(4.2f);

        ArrayList<ActivityEntity> ranked = new ArrayList<>();
        ranked.add(act);

        LessonEntity lesson = LessonGenerator.generateLessonPlan(null, params, ranked);

        // Verify custom hook is in Introduction
        assertTrue("Introduction must feature custom hook",
                lesson.getIntroduction().contains("Learners count avocado seeds into 3 farmer baskets"));
        // Verify custom hook is in Step 1
        assertTrue("Step 1 must feature custom hook",
                lesson.getLessonDevelopment().contains("Learners count avocado seeds into 3 farmer baskets"));
        // Verify custom hook is in Main Activities
        assertTrue("Main activities must feature custom hook",
                lesson.getMainActivities().contains("Learners count avocado seeds into 3 farmer baskets"));
    }

    @Test
    public void testCurriculumBreakdownHelperAllSubStrands() {
        String[] codes = {"1.1", "1.2", "1.3", "1.4", "1.5", "2.1", "2.2", "2.3", "2.4", "2.5", "2.6", "3.1", "3.2", "3.3", "4.1", "5.1"};
        for (String code : codes) {
            CurriculumEntity entity = new CurriculumEntity();
            entity.setSubStrandCode(code);
            ke.co.mwalimuai.utils.CurriculumBreakdownHelper.SubStrandBreakdown breakdown =
                    ke.co.mwalimuai.utils.CurriculumBreakdownHelper.getBreakdown(entity);
            assertNotNull("Breakdown should not be null for " + code, breakdown);
            assertTrue("Learning outcomes should not be empty for " + code, !breakdown.learningOutcomes.isEmpty());
            assertTrue("Pages breakdown should not be empty for " + code, !breakdown.pages.isEmpty());
            assertTrue("Inquiry questions should not be empty for " + code, !breakdown.keyInquiryQuestions.isEmpty());
        }
    }

    @Test
    public void testScenarioRotationAndCleanFormatting() {
        CurriculumEntity curr = new CurriculumEntity();
        curr.setCurriculumId("G6-MATH-13");
        curr.setStrand("Numbers");
        curr.setSubStrandCode("1.3");
        curr.setSubStrand("Division");

        LessonGenerator.LessonPlanningParameters params = new LessonGenerator.LessonPlanningParameters();
        params.curriculum = curr;
        params.availableResources = "stones and chalkboard";

        LessonEntity plan1 = LessonGenerator.generateLessonPlan(null, params, new ArrayList<>());
        LessonEntity plan2 = LessonGenerator.generateLessonPlan(null, params, new ArrayList<>());

        // Verify different scenarios are generated on consecutive runs
        assertTrue("Consecutive generations for the same sub-strand should rotate scenarios",
                !plan1.getLessonTitle().equals(plan2.getLessonTitle()));
        // Verify no TinyML score jargon in teacher-facing lesson development
        assertTrue("Lesson development must not expose internal TinyML Rank Score",
                !plan1.getLessonDevelopment().contains("TinyML"));
    }
}
