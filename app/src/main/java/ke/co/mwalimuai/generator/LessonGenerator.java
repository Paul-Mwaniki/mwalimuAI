package ke.co.mwalimuai.generator;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ke.co.mwalimuai.ai.OnDeviceSlmEngine;
import ke.co.mwalimuai.database.ActivityEntity;
import ke.co.mwalimuai.database.CurriculumEntity;
import ke.co.mwalimuai.database.LessonEntity;
import ke.co.mwalimuai.sne.InclusiveSNEPedagogyHelper;
import ke.co.mwalimuai.utils.CurriculumBreakdownHelper;

/**
 * Complete offline CBE lesson-generation engine in Java.
 * Combines On-Device Qwen2.5-0.5B SLM (via MediaPipe LlmInference), Local Curriculum RAG,
 * and Procedural Material-Physics Synthesis so every lesson tells one coherent story
 * and explains step-by-step how to use the teacher's exact materials.
 */
public class LessonGenerator {

    private static final String STAGE_DIVIDER = "\n────────────────────────────────────────\n\n";

    private static final Map<String, Integer> scenarioRotationMap = new HashMap<>();

    public static class LessonPlanningParameters {
        public CurriculumEntity curriculum;
        public int durationMinutes = 40;
        public int classSize = 45;
        public String availableResources = "paper and chalkboard";
        public String learningNeed = "mixed ability";
        public boolean electricity = false;
        public boolean internet = false;
        public String selectedActivityStrategy = "Recommended Best Fit (Auto-Select)";
        public String customActivityOrHook = "";
    }

    /**
     * Parses a teacher's free-form prompt (e.g. "Teach area of a triangle for 50 learners using strings and boxes")
     * into structured LessonPlanningParameters matched to the KICD Grade 6 Curriculum.
     */
    public static LessonPlanningParameters parseFreeFormPrompt(
            String freeFormPrompt,
            List<CurriculumEntity> allCurricula) {
        return parseFreeFormPrompt(freeFormPrompt, allCurricula, null);
    }

    public static LessonPlanningParameters parseFreeFormPrompt(
            String freeFormPrompt,
            List<CurriculumEntity> allCurricula,
            CurriculumEntity fallbackCurriculum) {

        LessonPlanningParameters params = new LessonPlanningParameters();
        String p = freeFormPrompt != null ? freeFormPrompt.trim() : "";
        String lower = p.toLowerCase(Locale.ROOT);

        // 1. Detect Sub-Strand Code from free-form text
        String targetCode = null;
        if (lower.contains("area") || (lower.contains("triangle") && !lower.contains("angle") && !lower.contains("prism"))) {
            targetCode = "2.2";
        } else if (lower.contains("fraction") || lower.contains("reciprocal") || lower.contains("lcm")) {
            targetCode = "1.4";
        } else if (lower.contains("decimal") || lower.contains("thousandth")) {
            targetCode = "1.5";
        } else if (lower.contains("divis") || lower.contains("divide") || lower.contains("bodmas")) {
            targetCode = "1.3";
        } else if (lower.contains("multipl") || lower.contains("product")) {
            targetCode = "1.2";
        } else if (lower.contains("length") || lower.contains("millimet") || lower.contains("centimet") || lower.contains("circumference") || lower.contains("perimeter")) {
            targetCode = "2.1";
        } else if (lower.contains("capacity") || lower.contains("volume") || lower.contains("litre") || lower.contains("millilitre")) {
            targetCode = "2.3";
        } else if (lower.contains("mass") || lower.contains("tonne") || lower.contains("kilogram") || lower.contains("weigh")) {
            targetCode = "2.4";
        } else if (lower.contains("time") || lower.contains("clock") || lower.contains("24-hour") || lower.contains("timetable") || lower.contains("duration")) {
            targetCode = "2.5";
        } else if (lower.contains("money") || lower.contains("budget") || lower.contains("profit") || lower.contains("loss") || lower.contains("tax") || lower.contains("shilling")) {
            targetCode = "2.6";
        } else if (lower.contains("parallel") || lower.contains("bisect") || lower.contains("perpendicular") || lower.contains("line")) {
            targetCode = "3.1";
        } else if (lower.contains("angle") || lower.contains("protractor") || lower.contains("degree")) {
            targetCode = "3.2";
        } else if (lower.contains("3-d") || lower.contains("3d") || lower.contains("cuboid") || lower.contains("cube") || lower.contains("pyramid") || lower.contains("prism") || lower.contains("vertices") || lower.contains("edges")) {
            targetCode = "3.3";
        } else if (lower.contains("data") || lower.contains("bar graph") || lower.contains("graph") || lower.contains("tally") || lower.contains("frequency")) {
            targetCode = "4.1";
        } else if (lower.contains("algebra") || lower.contains("inequalit") || lower.contains("equation")) {
            targetCode = "5.1";
        } else if (lower.contains("whole number") || lower.contains("place value") || lower.contains("total value") || lower.contains("million")) {
            targetCode = "1.1";
        }

        CurriculumEntity matched = null;
        if (targetCode == null && fallbackCurriculum != null) {
            matched = fallbackCurriculum;
        } else {
            if (targetCode == null) {
                // If user typed "strings and boxes" without a topic name, default to 2.2 Area
                if ((lower.contains("string") || lower.contains("rope")) && (lower.contains("box") || lower.contains("carton") || lower.contains("cardboard"))) {
                    targetCode = "2.2";
                } else {
                    targetCode = "1.1";
                }
            }
            if (allCurricula != null) {
                for (CurriculumEntity c : allCurricula) {
                    if (c.getSubStrandCode() != null && c.getSubStrandCode().trim().equals(targetCode)) {
                        matched = c;
                        break;
                    }
                }
                if (matched == null && !allCurricula.isEmpty()) {
                    matched = allCurricula.get(0);
                }
            }
            if (matched == null) {
                matched = createDefaultCurriculumForCode(targetCode);
            }
        }
        params.curriculum = matched;

        // 2. Extract class size if mentioned (e.g. "50 learners", "48 pupils", "class of 55")
        Matcher sizeMatcher = Pattern.compile("(\\d{2,3})\\s*(?:learners|pupils|students|kids)").matcher(lower);
        if (sizeMatcher.find()) {
            try {
                int sz = Integer.parseInt(sizeMatcher.group(1));
                if (sz >= 10 && sz <= 150) params.classSize = sz;
            } catch (Exception ignored) {}
        }

        // 3. Extract duration if mentioned (e.g. "35 minutes", "40 mins", "80 min")
        Matcher durMatcher = Pattern.compile("(\\d{2})\\s*(?:minutes|minute|mins|min)").matcher(lower);
        if (durMatcher.find()) {
            try {
                int d = Integer.parseInt(durMatcher.group(1));
                if (d >= 20 && d <= 90) params.durationMinutes = d;
            } catch (Exception ignored) {}
        }

        // 4. Extract custom materials (e.g. after "using ...", "with ...", or material keywords)
        String extractedMaterials = extractMaterialsFromPrompt(p);
        params.availableResources = extractedMaterials;
        params.customActivityOrHook = p;

        // 5. Detect SNE / Inclusive Learner Needs from prompt
        if (lower.contains("blind") || lower.contains("low vision") || lower.contains("braille") || lower.contains("visual impair")) {
            params.learningNeed = "Visual Impairment / Blind (Tactile Knotted Ropes & Audio Guide)";
        } else if (lower.contains("deaf") || lower.contains("hard of hearing") || lower.contains("sign language") || lower.contains("ksl")) {
            params.learningNeed = "Hearing Impairment / Deaf (KSL Sign Glosses & Visual Desk Cards)";
        } else if (lower.contains("dyslexi") || lower.contains("reading difficult")) {
            params.learningNeed = "Dyslexia & Reading Difficulties (Symbolic Storyboards & Color Cues)";
        } else if (lower.contains("dyscalculi") || lower.contains("number struggle") || lower.contains("math anxiety")) {
            params.learningNeed = "Dyscalculia (Concrete CPA, Dot Math & Ten-Frames)";
        } else if (lower.contains("adhd") || lower.contains("hyperactiv") || lower.contains("attention span") || lower.contains("focus")) {
            params.learningNeed = "ADHD & Attention Focus (3-Min Micro-Intervals & Kinesthetic Roles)";
        } else if (lower.contains("motor") || lower.contains("physical") || lower.contains("wheelchair") || lower.contains("cerebral")) {
            params.learningNeed = "Motor / Physical Challenges (Adapted Grips & Peer Scribing)";
        } else if (lower.contains("sne") || lower.contains("special need") || lower.contains("inclusiv")) {
            params.learningNeed = "Universal Inclusive (Mixed Abilities & Differentiated Scaffolding)";
        }

        return params;
    }

    private static String extractMaterialsFromPrompt(String prompt) {
        if (prompt == null || prompt.trim().isEmpty()) return "paper and chalkboard";
        String lower = prompt.toLowerCase(Locale.ROOT);

        // Look for "using <materials>" or "materials: <materials>"
        Matcher mUsing = Pattern.compile("(?i)(?:using|materials?:?|resources?:?|with)\\s+([a-zA-Z0-9,\\s/&\\-]+?)(?:\\.|\\s+for\\s+|\\s+in\\s+|\\s+to\\s+|$)").matcher(prompt);
        if (mUsing.find()) {
            String candidate = mUsing.group(1).trim();
            if (candidate.length() >= 3 && candidate.length() <= 60) {
                return candidate;
            }
        }

        List<String> detected = new ArrayList<>();
        String[] knownItems = {
                "strings", "string", "ropes", "sisal", "boxes", "cardboard boxes", "cartons",
                "avocado seeds", "maize seeds", "bean seeds", "seeds", "bottle tops", "bottle caps",
                "sticks", "twigs", "straws", "stones", "pebbles", "manila paper", "number cards", "abacus", "rulers"
        };
        for (String item : knownItems) {
            if (lower.contains(item) && !detected.contains(item)) {
                detected.add(item);
            }
        }
        if (!detected.isEmpty()) {
            return String.join(" and ", detected);
        }
        return "local classroom manipulatives (" + prompt.trim() + ")";
    }

    private static CurriculumEntity createDefaultCurriculumForCode(String code) {
        CurriculumEntity c = new CurriculumEntity();
        c.setSubStrandCode(code);
        c.setGrade(6);
        c.setLearningArea("Mathematics");
        switch (code) {
            case "1.2":
                c.setCurriculumId("G6-MATH-12"); c.setStrand("Numbers"); c.setSubStrand("Multiplication"); c.setSourcePdfPages("18-20"); break;
            case "1.3":
                c.setCurriculumId("G6-MATH-13"); c.setStrand("Numbers"); c.setSubStrand("Division"); c.setSourcePdfPages("21-22"); break;
            case "1.4":
                c.setCurriculumId("G6-MATH-14"); c.setStrand("Numbers"); c.setSubStrand("Fractions"); c.setSourcePdfPages("23-27"); break;
            case "1.5":
                c.setCurriculumId("G6-MATH-15"); c.setStrand("Numbers"); c.setSubStrand("Decimals"); c.setSourcePdfPages("28-31"); break;
            case "2.1":
                c.setCurriculumId("G6-MATH-21"); c.setStrand("Measurement"); c.setSubStrand("Length"); c.setSourcePdfPages("32-36"); break;
            case "2.2":
                c.setCurriculumId("G6-MATH-22"); c.setStrand("Measurement"); c.setSubStrand("Area"); c.setSourcePdfPages("37-39"); break;
            case "2.3":
                c.setCurriculumId("G6-MATH-23"); c.setStrand("Measurement"); c.setSubStrand("Capacity"); c.setSourcePdfPages("40-42"); break;
            case "2.4":
                c.setCurriculumId("G6-MATH-24"); c.setStrand("Measurement"); c.setSubStrand("Mass"); c.setSourcePdfPages("43-47"); break;
            case "2.5":
                c.setCurriculumId("G6-MATH-25"); c.setStrand("Measurement"); c.setSubStrand("Time"); c.setSourcePdfPages("48-52"); break;
            case "2.6":
                c.setCurriculumId("G6-MATH-26"); c.setStrand("Measurement"); c.setSubStrand("Money"); c.setSourcePdfPages("53-55"); break;
            case "3.1":
                c.setCurriculumId("G6-MATH-31"); c.setStrand("Geometry"); c.setSubStrand("Lines"); c.setSourcePdfPages("56-58"); break;
            case "3.2":
                c.setCurriculumId("G6-MATH-32"); c.setStrand("Geometry"); c.setSubStrand("Angles"); c.setSourcePdfPages("59-62"); break;
            case "3.3":
                c.setCurriculumId("G6-MATH-33"); c.setStrand("Geometry"); c.setSubStrand("3-D Objects"); c.setSourcePdfPages("63-65"); break;
            case "4.1":
                c.setCurriculumId("G6-MATH-41"); c.setStrand("Data Handling"); c.setSubStrand("Bar Graphs"); c.setSourcePdfPages("66-68"); break;
            case "5.1":
                c.setCurriculumId("G6-MATH-51"); c.setStrand("Algebra"); c.setSubStrand("Inequalities"); c.setSourcePdfPages("69-70"); break;
            default:
                c.setCurriculumId("G6-MATH-11"); c.setStrand("Numbers"); c.setSubStrand("Whole numbers"); c.setSourcePdfPages("13-17"); break;
        }
        return c;
    }

    /**
     * Formats any inline numbered steps ("1. ... 2. ... 3. ...") so every number starts on a new line.
     */
    public static String formatNumberedStepsNewLines(String text) {
        if (text == null || text.trim().isEmpty()) return "";
        String cleaned = text.trim()
                .replaceAll("\\s+(?=\\d+\\.\\s)", "\n   ");
        if (cleaned.startsWith("1.")) {
            cleaned = "   " + cleaned;
        }
        return cleaned;
    }

    public static synchronized LessonEntity generateLessonPlan(
            Context context,
            LessonPlanningParameters params,
            List<ActivityEntity> rankedActivities) {

        CurriculumEntity curr = params.curriculum;
        String strand = curr != null && curr.getStrand() != null ? curr.getStrand() : "Numbers";
        String subStrand = curr != null && curr.getSubStrand() != null ? curr.getSubStrand() : "Whole numbers";
        String subStrandCode = curr != null && curr.getSubStrandCode() != null ? curr.getSubStrandCode() : "1.1";
        String curriculumId = curr != null && curr.getCurriculumId() != null ? curr.getCurriculumId() : "G6-MATH-11";
        String sourcePages = (curr != null && curr.getSourcePdfPages() != null && !curr.getSourcePdfPages().isEmpty())
                ? "Pages " + curr.getSourcePdfPages() : "Pages 13-17";

        int currentScenarioIdx = scenarioRotationMap.containsKey(subStrandCode)
                ? scenarioRotationMap.get(subStrandCode) : 0;
        scenarioRotationMap.put(subStrandCode, currentScenarioIdx + 1);

        String localRes = (params.availableResources != null && !params.availableResources.trim().isEmpty())
                ? params.availableResources.trim()
                : (curr != null && curr.getLocalResources() != null ? curr.getLocalResources() : "paper and chalkboard");

        // 1. Dynamically synthesize a 100% coherent scenario + physical material instructions + worked math
        ProceduralMathSynthesizer.SynthesizedPedagogy synth =
                ProceduralMathSynthesizer.synthesize(subStrandCode, subStrand, localRes);

        String activeConcreteHowTo = synth.concreteMaterialHowTo;
        String contextDesc = synth.scenarioContext;

        // 2. Prompt the On-Device Qwen2.5-0.5B SLM (if downloaded on phone) with the material-connected RAG context
        String slmGeneratedSteps = null;
        if (context != null && OnDeviceSlmEngine.isModelDownloaded(context)) {
            slmGeneratedSteps = OnDeviceSlmEngine.getInstance(context)
                    .generateRagResponse(context, curr, params, contextDesc, synth.workedExample);
        }

        LessonEntity lesson = new LessonEntity();
        lesson.setLessonId("LP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        lesson.setCurriculumId(curriculumId);
        lesson.setGrade(6);
        lesson.setLearningArea("Mathematics");
        lesson.setStrand(strand);
        lesson.setSubStrand(subStrand);
        lesson.setDurationMinutes(params.durationMinutes);
        lesson.setClassSize(params.classSize);
        lesson.setCreatedTimestamp(System.currentTimeMillis());

        boolean hasCustomHook = params.customActivityOrHook != null && !params.customActivityOrHook.trim().isEmpty();
        String customHookText = hasCustomHook ? params.customActivityOrHook.trim() : "";

        int squadCount = Math.max(2, Math.round(params.classSize / 5.0f));
        int squadSize = Math.max(3, Math.round((float) params.classSize / squadCount));

        int introMins = Math.max(5, Math.round(params.durationMinutes * 0.125f));
        int exitMins = Math.max(5, Math.round(params.durationMinutes * 0.125f));
        int devTotalMins = Math.max(20, params.durationMinutes - introMins - exitMins);
        int step1Mins = devTotalMins / 2;
        int step2Mins = devTotalMins - step1Mins;

        // Detect SNE Profile (e.g. Hearing Impairment / KSL, Visual Impairment, etc.)
        InclusiveSNEPedagogyHelper.SNEProfile profile =
                InclusiveSNEPedagogyHelper.detectProfile(params.learningNeed);

        // 1. Lesson Title (100% SNE-focused and simple)
        lesson.setLessonTitle(InclusiveSNEPedagogyHelper.buildSNELessonTitle(
                profile, subStrand, sourcePages, capitalize(contextDesc)));

        // 2. Specific Learning Outcomes (100% SNE-aligned)
        lesson.setLearningObjective(InclusiveSNEPedagogyHelper.buildSNELearningOutcomes(
                profile, subStrand, localRes, contextDesc, params.durationMinutes, squadSize));

        // 3. Learning Resources & Teacher Preparation (100% SNE-aligned)
        lesson.setResourcesNeeded(InclusiveSNEPedagogyHelper.buildSNEResourcesNeeded(
                profile, localRes, contextDesc, squadCount, squadSize, sourcePages));

        // 4. Lesson Introduction / Hook (100% SNE-aligned, 0% auditory for HI)
        lesson.setIntroduction(InclusiveSNEPedagogyHelper.buildSNEIntroduction(
                profile, subStrand, localRes, contextDesc, synth.workedExample, introMins, customHookText));

        // 5. Lesson Development (Stages 1, 2, 3 fully SNE-native)
        String sneDevelopment = InclusiveSNEPedagogyHelper.buildSNELessonDevelopment(
                profile,
                subStrand,
                localRes,
                contextDesc,
                synth.workedExample,
                activeConcreteHowTo,
                synth.representationalTask,
                synth.guidedPracticeProblem,
                synth.exitTicketQuestion,
                synth.exitTicketSolution,
                slmGeneratedSteps,
                squadCount,
                squadSize,
                step1Mins,
                step2Mins,
                exitMins,
                customHookText
        );
        lesson.setLessonDevelopment(sneDevelopment);

        // Main Activities Summary
        String activitiesSummary;
        if (profile == InclusiveSNEPedagogyHelper.SNEProfile.DEAF_HARD_OF_HEARING) {
            activitiesSummary = "Silent Visual & KSL Concrete Modeling; KSL Peer Signing & Hands-On Pod Practice; Visual Exit Ticket & Signed Demonstration";
        } else if (profile == InclusiveSNEPedagogyHelper.SNEProfile.BLIND_LOW_VISION) {
            activitiesSummary = "Tactile Modeling & Kinesthetic Exploration; Tactile Pair Practice & Oral Verification; Tactile Exit Ticket";
        } else {
            activitiesSummary = "Adapted Concrete Modeling; Guided Pod Practice; Multi-Pathway Exit Ticket";
        }
        if (hasCustomHook) {
            activitiesSummary = customHookText + " — " + activitiesSummary;
        }
        lesson.setMainActivities(activitiesSummary);

        // 5. Formative Assessment & Multi-Pathway 4-Tier CBC Rubric (Offline SNE Adaptive)
        lesson.setAssessmentMethods(InclusiveSNEPedagogyHelper.buildInclusiveAssessmentSection(
                params.learningNeed,
                subStrand,
                capitalize(contextDesc),
                synth.exitTicketQuestion,
                synth.exitTicketSolution,
                localRes
        ));

        // 6. Large Class Adaptation & Universal Design for Learning (UDL) SNE Inclusivity
        String layoutDesc = (profile == InclusiveSNEPedagogyHelper.SNEProfile.DEAF_HARD_OF_HEARING)
                ? "Semi-circular horseshoe seating layout with direct unobstructed sightlines to the teacher's signing hands"
                : "Structured inclusive pods with clear classroom pathways and peer guide pairs";

        lesson.setLargeClassAdaptation(
                "▪ SNE Pod Organization for " + params.classSize + " Learners (" + squadCount + " Pods of " + squadSize + "):\n\n" +
                "1. Classroom Seating & Sightlines:\n" +
                "   " + layoutDesc + ".\n\n" +
                "2. Pod Leader / Facilitator:\n" +
                "   Directs peer interaction, confirms everyone has visual/tactile access to " + localRes + ".\n\n" +
                "3. Material Custodian:\n" +
                "   Collects, counts, and returns all " + localRes + " before and after the investigation.\n\n" +
                "4. Visual Scribe / Buddy Partner:\n" +
                "   Works alongside SNE peer assisting with diagram recording and peer card verification."
        );

        lesson.setInclusionNotes(InclusiveSNEPedagogyHelper.buildSNEInclusionSection(
                params.learningNeed,
                subStrand,
                localRes,
                capitalize(contextDesc)
        ));

        // 7. Teacher Reflection
        lesson.setReflectionQuestion(
                "1. Outcome Achievement:\n" +
                "   How many of the " + params.classSize + " learners achieved 'Meets' or 'Exceeds Expectations' on the " + contextDesc + " Exit Ticket?\n\n" +
                "2. Material Effectiveness:\n" +
                "   How effectively did manipulating " + localRes + " help learners grasp " + subStrand + "?\n\n" +
                "3. Follow-Up Action:\n" +
                "   Which learners require a 5-minute remedial practice with " + localRes + " before the next lesson?"
        );

        return lesson;
    }

    private static String capitalize(String s) {
        if (s == null || s.isEmpty()) return "";
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
}
