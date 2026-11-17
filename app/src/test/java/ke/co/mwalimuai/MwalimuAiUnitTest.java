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
        assertTrue(lesson.getLessonDevelopment().contains("STAGE 1: ADAPTED MULTISENSORY CONCRETE MODELING"));
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

        // Verify different math problems and exit tickets are dynamically synthesized on consecutive runs
        assertTrue("Consecutive generations for the same sub-strand must synthesize unique math problems",
                !plan1.getLessonDevelopment().equals(plan2.getLessonDevelopment()));
        // Verify no TinyML score jargon in teacher-facing lesson development
        assertTrue("Lesson development must not expose internal TinyML Rank Score",
                !plan1.getLessonDevelopment().contains("TinyML"));
    }

    @Test
    public void testProceduralMathSynthesizerAllSubStrandsUnique() {
        String[] codes = {"1.1", "1.2", "1.3", "1.4", "1.5", "2.1", "2.2", "2.3", "2.4", "2.5", "2.6", "3.1", "3.2", "3.3", "4.1", "5.1"};
        for (String code : codes) {
            ke.co.mwalimuai.generator.ProceduralMathSynthesizer.SynthesizedPedagogy s1 =
                    ke.co.mwalimuai.generator.ProceduralMathSynthesizer.synthesize(code, "SubStrand " + code, "mango seeds and sticks");
            assertNotNull("Synthesized pedagogy should not be null for " + code, s1);
            assertTrue("Concrete steps must weave in custom material for " + code,
                    s1.concreteMaterialHowTo.contains("mango seeds and sticks"));
            assertTrue("Worked example should not be empty for " + code, !s1.workedExample.isEmpty());
            assertTrue("Exit ticket solution should not be empty for " + code, !s1.exitTicketSolution.isEmpty());
        }
    }

    @Test
    public void testAreaWithStringsAndBoxesAndFreeFormPrompt() {
        LessonGenerator.LessonPlanningParameters params =
                LessonGenerator.parseFreeFormPrompt("Teach Area of a triangle for 48 learners using strings and boxes", null);
        assertEquals("2.2", params.curriculum.getSubStrandCode());
        assertEquals(48, params.classSize);
        assertTrue(params.availableResources.toLowerCase().contains("strings"));
        assertTrue(params.availableResources.toLowerCase().contains("boxes"));

        LessonEntity lesson = LessonGenerator.generateLessonPlan(null, params, new ArrayList<>());
        assertNotNull(lesson);
        assertTrue("Lesson development must explain flattening the box and stretching the string across the diagonal",
                lesson.getLessonDevelopment().toLowerCase().contains("diagonal"));
        assertTrue("Lesson development must not mention unrelated flowerbed when strings and boxes are used",
                !lesson.getLessonDevelopment().toLowerCase().contains("flowerbed"));
    }

    @Test
    public void testSNEInclusiveAdaptationsAndRubrics() {
        // 1. Blind SNE Test
        LessonGenerator.LessonPlanningParameters blindParams =
                LessonGenerator.parseFreeFormPrompt("Teach Area of a triangle using strings and boxes for a class with 2 blind learners", null);
        assertTrue(blindParams.learningNeed.toLowerCase().contains("blind"));
        LessonEntity blindLesson = LessonGenerator.generateLessonPlan(null, blindParams, new ArrayList<>());
        assertTrue("Inclusion notes must provide tactile string knots for blind learners",
                blindLesson.getInclusionNotes().toLowerCase().contains("knots"));
        assertTrue("Assessment must provide multi-pathway evidence and remedial prescription",
                blindLesson.getAssessmentMethods().contains("Automated Offline Remedial Prescription"));

        // 2. Deaf SNE Test
        LessonGenerator.LessonPlanningParameters deafParams =
                LessonGenerator.parseFreeFormPrompt("Teach Fractions using seeds and sticks with KSL sign language for deaf learners", null);
        assertTrue(deafParams.learningNeed.toLowerCase().contains("deaf"));
        LessonEntity deafLesson = LessonGenerator.generateLessonPlan(null, deafParams, new ArrayList<>());
        assertTrue("Inclusion notes must provide KSL sign glosses",
                deafLesson.getInclusionNotes().contains("KSL"));

        // 3. Dyscalculia SNE Test
        LessonGenerator.LessonPlanningParameters dyscParams =
                LessonGenerator.parseFreeFormPrompt("Teach Division using bottle tops for learners with dyscalculia", null);
        assertTrue(dyscParams.learningNeed.toLowerCase().contains("dyscalculia"));
        LessonEntity dyscLesson = LessonGenerator.generateLessonPlan(null, dyscParams, new ArrayList<>());
        assertTrue("Inclusion notes must provide CPA progression and Ten-Frames",
                dyscLesson.getInclusionNotes().contains("CPA"));
    }

    @Test
    public void testHearingImpairmentKslFullSNEFocus() {
        LessonGenerator.LessonPlanningParameters params = new LessonGenerator.LessonPlanningParameters();
        CurriculumEntity curr = new CurriculumEntity();
        curr.setCurriculumId("G6-MATH-22");
        curr.setStrand("Measurement");
        curr.setSubStrand("Area");
        curr.setSubStrandCode("2.2");
        curr.setSourcePdfPages("37-39");

        params.curriculum = curr;
        params.availableResources = "strings, boxes, cartons";
        params.durationMinutes = 40;
        params.classSize = 48;
        params.learningNeed = "Hearing Impairment / Deaf (KSL Sign Glosses & Visual Desk Cards)";

        LessonEntity lesson = LessonGenerator.generateLessonPlan(null, params, new ArrayList<>());

        assertNotNull(lesson);
        // 1. Simple, SNE-focused Title
        assertTrue("Title must be SNE focused", lesson.getLessonTitle().startsWith("SNE (Hearing Impairment / KSL)"));

        // 2. SNE Specific Learning Outcomes (KSL & visual arrangement)
        assertTrue("Outcomes must include KSL", lesson.getLearningObjective().contains("Kenya Sign Language (KSL)"));
        assertTrue("Outcomes must specify without auditory reliance",
                lesson.getLearningObjective().contains("without auditory reliance"));

        // 3. SNE Resources (desk action cards & horseshoe layout)
        assertTrue("Resources must include 3-color desk action cards",
                lesson.getResourcesNeeded().contains("3-Color Desk Action Indicator Cards"));
        assertTrue("Resources must specify horseshoe seating",
                lesson.getResourcesNeeded().contains("horseshoe"));

        // 4. SNE Introduction (visual attention signal, zero verbal shouting)
        assertTrue("Intro must feature visual attention signal",
                lesson.getIntroduction().contains("Visual Attention Signal"));
        assertTrue("Intro must avoid verbal shouting",
                lesson.getIntroduction().contains("Strictly avoid verbal/auditory shouting"));

        // 5. SNE Lesson Development (Stages 1, 2, 3)
        assertTrue("Stage 1 must be Silent Visual & Concrete Modeling",
                lesson.getLessonDevelopment().contains("STAGE 1: SILENT VISUAL & CONCRETE MODELING (KSL)"));
        assertTrue("Stage 1 must include 3-color desk cards check",
                lesson.getLessonDevelopment().contains("Desk-Level Visual Card Verification"));
        assertTrue("Stage 2 must be KSL Peer Signing",
                lesson.getLessonDevelopment().contains("STAGE 2: PEER SIGNING & HANDS-ON POD PRACTICE"));
        assertTrue("Stage 3 must be Visual Exit Ticket",
                lesson.getLessonDevelopment().contains("STAGE 3: VISUAL EXIT TICKET & SIGNED DEMONSTRATION"));

        // 6. Assessment must not test spoken language
        assertTrue("Assessment must note spoken auditory language is NOT assessed",
                lesson.getAssessmentMethods().contains("Spoken auditory language is NOT assessed"));

        // 7. No technical model names in user-facing lesson
        assertTrue("No Qwen reference in title", !lesson.getLessonTitle().toLowerCase().contains("qwen"));
        assertTrue("No Qwen reference in development", !lesson.getLessonDevelopment().toLowerCase().contains("qwen"));
    }
}
