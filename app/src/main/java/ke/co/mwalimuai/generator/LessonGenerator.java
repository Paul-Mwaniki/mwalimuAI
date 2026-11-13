package ke.co.mwalimuai.generator;

import android.content.Context;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import ke.co.mwalimuai.database.ActivityEntity;
import ke.co.mwalimuai.database.CurriculumEntity;
import ke.co.mwalimuai.database.LessonEntity;

/**
 * Complete offline CBE lesson-generation engine in Java.
 * Rotates through diverse Kenyan real-world scenarios every time a lesson is generated,
 * expounds concrete material implementation step-by-step, and formats all sections
 * with clean visual dividers and multi-line steps (without exposing internal AI scores).
 */
public class LessonGenerator {

    private static final String STAGE_DIVIDER = "\n────────────────────────────────────────\n\n";

    // Tracks scenario rotation per sub-strand so clicking "Plan Another" always produces a fresh scenario
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

    private static class TopicPedagogyDetail {
        final String scenarioContext;
        final String workedExample;
        final String concreteMaterialHowTo;
        final String representationalTask;
        final String guidedPracticeProblem;
        final String exitTicketQuestion;
        final String exitTicketSolution;

        TopicPedagogyDetail(
                String scenarioContext,
                String workedExample,
                String concreteMaterialHowTo,
                String representationalTask,
                String guidedPracticeProblem,
                String exitTicketQuestion,
                String exitTicketSolution) {
            this.scenarioContext = scenarioContext;
            this.workedExample = workedExample;
            this.concreteMaterialHowTo = concreteMaterialHowTo;
            this.representationalTask = representationalTask;
            this.guidedPracticeProblem = guidedPracticeProblem;
            this.exitTicketQuestion = exitTicketQuestion;
            this.exitTicketSolution = exitTicketSolution;
        }
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

    /**
     * Replaces generic placeholder materials inside activity steps with the teacher's selected materials.
     */
    private static String adaptTextWithResources(String text, String localRes) {
        if (text == null) return "";
        if (localRes == null || localRes.trim().isEmpty()) return text;

        String adapted = text;
        String[] genericPlaceholders = {
                "number cards, chalkboard, stones",
                "bottle tops and paper",
                "ruler and paper",
                "stones and chalkboard",
                "number cards",
                "bottle tops",
                "pebbles",
                "stones"
        };

        for (String placeholder : genericPlaceholders) {
            adapted = adapted.replaceAll("(?i)" + java.util.regex.Pattern.quote(placeholder), localRes);
        }
        return adapted;
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

        // Advance scenario index for this sub-strand so every generation produces a different real-life scenario
        int currentScenarioIdx = scenarioRotationMap.containsKey(subStrandCode)
                ? scenarioRotationMap.get(subStrandCode) : 0;
        scenarioRotationMap.put(subStrandCode, currentScenarioIdx + 1);

        String localRes = (params.availableResources != null && !params.availableResources.trim().isEmpty())
                ? params.availableResources.trim()
                : (curr != null && curr.getLocalResources() != null ? curr.getLocalResources() : "paper and chalkboard");

        List<TopicPedagogyDetail> variants = getTopicScenarioVariants(subStrandCode, subStrand, localRes);
        TopicPedagogyDetail topicDetail = variants.get(currentScenarioIdx % variants.size());
        String contextDesc = topicDetail.scenarioContext;

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

        // Lesson Title (includes the active scenario theme)
        lesson.setLessonTitle(subStrand + " (" + sourcePages + ") — " + capitalize(contextDesc));

        // 1. Specific Learning Outcomes (Multi-line numbered format)
        String focus = curr != null && curr.getCurriculumAlignedFocus() != null
                ? curr.getCurriculumAlignedFocus() : "mathematical reasoning and problem solving";
        lesson.setLearningObjective(
                "By the end of the " + params.durationMinutes + "-minute lesson, the learner should be able to:\n\n" +
                "1. Knowledge & Concept:\n" +
                "   Explain and demonstrate " + subStrand + " concepts (" + focus + ") using " + localRes + ".\n\n" +
                "2. Practical Application:\n" +
                "   Manipulate " + localRes + " in squads of " + squadSize + " to model and solve problems based on " + contextDesc + ".\n\n" +
                "3. Values & Competencies:\n" +
                "   Collaborate fairly with peers, share " + localRes + " responsibly, and justify each mathematical step."
        );

        // 2. Learning Resources & Material Preparation
        lesson.setResourcesNeeded(
                "▪ Classroom Materials:\n" +
                "   " + localRes + ", chalkboard, and learner exercise books.\n\n" +
                "▪ Teacher Preparation Before Lesson:\n" +
                "   1. Organize " + localRes + " into " + squadCount + " equal sets (1 set per squad of " + squadSize + " learners).\n" +
                "   2. Write the opening worked example (" + contextDesc + ") clearly on the chalkboard.\n" +
                "   3. Curriculum Reference: KICD Grade 6 Mathematics (" + sourcePages + ")."
        );

        // 3. Lesson Introduction / Hook (Every step on a new line)
        if (hasCustomHook) {
            lesson.setIntroduction(
                    "⏱ Time Allocation: " + introMins + " Minutes\n" +
                    "────────────────────────────────────────\n" +
                    "Step 1 — Teacher Lesson Hook (2 Mins):\n" +
                    "   • " + customHookText + "\n\n" +
                    "Step 2 — Concrete Material Demonstration (2 Mins):\n" +
                    "   • Display " + localRes + " at the front table.\n" +
                    "   • Invite 2 learners to model the opening problem using " + localRes + ".\n\n" +
                    "Step 3 — Linking Prior Knowledge (1 Min):\n" +
                    "   • Ask the class: \"How does grouping or arranging " + localRes + " help us solve today's " + subStrand + " challenge?\""
            );
        } else {
            lesson.setIntroduction(
                    "⏱ Time Allocation: " + introMins + " Minutes\n" +
                    "────────────────────────────────────────\n" +
                    "Step 1 — Real-Life Scenario Hook (2 Mins):\n" +
                    "   • Introduce today's scenario on " + contextDesc + ".\n" +
                    "   • Board Problem: " + topicDetail.workedExample + "\n\n" +
                    "Step 2 — Concrete Demonstration with Materials (2 Mins):\n" +
                    "   • Place " + localRes + " on the front demonstration table.\n" +
                    "   • Call 2 learners forward to physically arrange " + localRes + " while the class observes and counts aloud.\n\n" +
                    "Step 3 — Prior Knowledge Check (1 Min):\n" +
                    "   • Ask desk partners to discuss for 30 seconds: \"What mathematical rule connects our arrangement of " + localRes + " to " + subStrand + "?\""
            );
        }

        // 4. Lesson Development (Clean visual stage dividers, zero AI/TinyML jargon)
        StringBuilder devBuilder = new StringBuilder();
        StringBuilder activitiesSummary = new StringBuilder();

        ActivityEntity primaryAct = null;
        ActivityEntity secondaryAct = null;
        if (rankedActivities != null && !rankedActivities.isEmpty()) {
            int pIdx = currentScenarioIdx % rankedActivities.size();
            int sIdx = (currentScenarioIdx + 1) % rankedActivities.size();
            primaryAct = rankedActivities.get(pIdx);
            secondaryAct = rankedActivities.get(sIdx);
        }

        String strat1 = primaryAct != null ? primaryAct.getStrategy() : "Manipulative exploration";
        String title1 = hasCustomHook ? (subStrand + ": " + customHookText)
                : (subStrand + " — Hands-On Exploration (" + capitalize(contextDesc) + ")");

        devBuilder.append("STAGE 1: CONCRETE HANDS-ON EXPLORATION (").append(step1Mins).append(" MINS)\n");
        devBuilder.append("▪ Activity Focus: ").append(title1).append("\n");
        devBuilder.append("▪ Teaching Method: ").append(strat1).append(" | Grouping: ").append(squadCount).append(" Squads of ").append(squadSize).append("\n");
        devBuilder.append("▪ Materials in Use: ").append(localRes).append("\n\n");
        devBuilder.append("Step 1 — Squad & Material Distribution (2 Mins):\n");
        devBuilder.append("   • Divide the ").append(params.classSize).append(" learners into ").append(squadCount)
                .append(" squads of ").append(squadSize).append(".\n");
        devBuilder.append("   • Each squad's Material Custodian collects their set of ").append(localRes).append(".\n\n");

        if (hasCustomHook) {
            devBuilder.append("Step 2 — Teacher Modelling (3 Mins):\n");
            devBuilder.append("   • Guide squads through the custom challenge: \"").append(customHookText)
                    .append("\" using ").append(localRes).append(".\n\n");
        } else {
            devBuilder.append("Step 2 — Teacher Worked Example (3 Mins):\n");
            devBuilder.append("   • Model on the chalkboard: ").append(topicDetail.workedExample).append("\n\n");
        }

        devBuilder.append("Step 3 — Concrete Material Manipulation (5 Mins):\n");
        devBuilder.append("   • ").append(topicDetail.concreteMaterialHowTo).append("\n\n");

        devBuilder.append("Step 4 — Representational Recording in Books (3 Mins):\n");
        devBuilder.append("   • ").append(topicDetail.representationalTask).append("\n\n");

        devBuilder.append("Step 5 — Teacher Observation Check (2 Mins):\n");
        devBuilder.append("   • Walk around desks inspecting each squad's arrangement of ").append(localRes)
                .append(" and ask: \"How does your physical model prove your written answer?\"");

        devBuilder.append(STAGE_DIVIDER);
        activitiesSummary.append(title1);

        // Stage 2: Guided Practice & Peer Application
        String strat2 = secondaryAct != null ? secondaryAct.getStrategy() : "Think-pair-share";
        String title2 = subStrand + " — Guided Practice & Peer Verification";

        devBuilder.append("STAGE 2: GUIDED PRACTICE & PEER APPLICATION (").append(step2Mins).append(" MINS)\n");
        devBuilder.append("▪ Activity Focus: ").append(title2).append("\n");
        devBuilder.append("▪ Teaching Method: ").append(strat2).append(" | Grouping: Desk Pairs\n\n");

        devBuilder.append("Step 1 — Guided Practice Problem (4 Mins):\n");
        devBuilder.append("   • Write on the chalkboard: ").append(topicDetail.guidedPracticeProblem).append("\n\n");

        devBuilder.append("Step 2 — Concrete-to-Abstract Verification (5 Mins):\n");
        devBuilder.append("   • Learners work out the mathematical solution in their exercise books AND verify each step using ")
                .append(localRes).append(".\n\n");

        devBuilder.append("Step 3 — Partner Error Check (3 Mins):\n");
        devBuilder.append("   • Desk partners exchange books, compare their arrangements of ").append(localRes)
                .append(", and check that units and working steps are complete.\n\n");

        devBuilder.append("Step 4 — Class Presentation (3 Mins):\n");
        devBuilder.append("   • Invite 2 learners from different squads to demonstrate their working on the chalkboard.");

        devBuilder.append(STAGE_DIVIDER);
        activitiesSummary.append("; ").append(title2);

        // Stage 3: Structured Exit Ticket & Closure
        devBuilder.append("STAGE 3: STRUCTURED EXIT TICKET & CLOSURE (").append(exitMins).append(" MINS)\n");
        devBuilder.append("▪ Assessment Type: Individual Written Exit Ticket\n\n");
        devBuilder.append("Step 1 — Chalkboard Exit Ticket Problem (3 Mins):\n");
        devBuilder.append("   • ").append(topicDetail.exitTicketQuestion).append("\n\n");
        devBuilder.append("Step 2 — Expected Model Solution (Teacher Marking Key):\n");
        devBuilder.append("   • ").append(topicDetail.exitTicketSolution).append("\n\n");
        devBuilder.append("Step 3 — Collection & Wrap-Up (2 Mins):\n");
        devBuilder.append("   • Learners hold up their written solution for a quick visual scan.\n");
        devBuilder.append("   • Material Custodians count and return all ").append(localRes).append(" to the classroom container.");

        lesson.setMainActivities(activitiesSummary.toString());
        lesson.setLessonDevelopment(devBuilder.toString());

        // 5. Formative Assessment & 4-Tier CBE Rubric (Formatted with clear dividers)
        AssessmentGenerator.AssessmentItem rubric = AssessmentGenerator.getAssessmentForCurriculum(context, curriculumId);
        lesson.setAssessmentMethods(
                "▪ Assessment Task (" + capitalize(contextDesc) + "):\n" +
                "   " + topicDetail.exitTicketQuestion + "\n\n" +
                "▪ Observable Evidence of Learning:\n" +
                "   " + rubric.evidenceOfLearning + " (Verified through manipulation of " + localRes + " and written steps).\n" +
                "────────────────────────────────────────\n" +
                "▪ 4-Tier CBE Assessment Rubric:\n\n" +
                "1. Exceeds Expectations (EE):\n" +
                "   • Solves the problem and exit ticket accurately without support.\n" +
                "   • Clearly explains the mathematical principle using " + localRes + ".\n\n" +
                "2. Meets Expectations (ME):\n" +
                "   • Correctly models " + subStrand + " using " + localRes + " and arrives at the right answer.\n\n" +
                "3. Approaching Expectations (AE):\n" +
                "   • Groups " + localRes + " correctly for the first step but makes a minor calculation slip.\n\n" +
                "4. Below Expectations (BE):\n" +
                "   • Struggles to begin the task; requires one-on-one coaching with smaller quantities of " + localRes + "."
        );

        // 6. Large Class Adaptation & Inclusion Notes
        lesson.setLargeClassAdaptation(
                "▪ Classroom Management for " + params.classSize + " Learners (" + squadCount + " Squads of " + squadSize + "):\n\n" +
                "1. Squad Leader / Facilitator:\n" +
                "   Reads the problem aloud and ensures every member touches the " + localRes + ".\n\n" +
                "2. Material Custodian:\n" +
                "   Collects, distributes, and counts all " + localRes + " before and after the activity.\n\n" +
                "3. Scribe:\n" +
                "   Records the squad's consensus diagram and working.\n\n" +
                "4. Timekeeper & Reporter:\n" +
                "   Monitors the time allocation and presents the squad's findings."
        );

        lesson.setInclusionNotes(
                "────────────────────────────────────────\n" +
                "▪ Differentiation & Inclusion (" + capitalize(params.learningNeed) + " Focus):\n\n" +
                "1. Learner Support (Scaffolding):\n" +
                "   Give struggling learners smaller numbers first and guide them to physically group " + localRes + " before writing.\n\n" +
                "2. Extension Challenge (Fast Finishers):\n" +
                "   Ask fast finishers to formulate their own word problem on " + contextDesc + " and test a neighbouring squad.\n\n" +
                "3. Accessibility:\n" +
                "   Use large, clear chalkboard writing and pair learners needing extra support with a peer buddy."
        );

        // 7. Teacher Reflection
        lesson.setReflectionQuestion(
                "1. Outcome Achievement:\n" +
                "   How many of the " + params.classSize + " learners achieved 'Meets' or 'Exceeds Expectations' on the Exit Ticket?\n\n" +
                "2. Material Effectiveness:\n" +
                "   How effectively did using " + localRes + " help learners understand " + subStrand + " in the context of " + contextDesc + "?\n\n" +
                "3. Follow-Up Action:\n" +
                "   Which learners require a 5-minute remedial practice with " + localRes + " before the next lesson?"
        );

        return lesson;
    }

    private static String capitalize(String s) {
        if (s == null || s.isEmpty()) return "";
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    /**
     * Provides 4 distinct Kenyan real-world scenarios, worked examples, concrete material steps,
     * guided problems, and exit tickets for EVERY sub-strand so repeated generation never shows just one example.
     */
    private static List<TopicPedagogyDetail> getTopicScenarioVariants(String code, String subStrand, String mat) {
        String c = code != null ? code.trim() : "1.1";
        switch (c) {
            case "1.1": // Whole numbers (4 distinct scenarios)
                return Arrays.asList(
                        new TopicPedagogyDetail(
                                "county tree-planting nursery records",
                                "A county tree nursery recorded 478,625 seedlings. What is the place value and total value of digit 7, and what is 478,625 rounded to the nearest ten thousand?",
                                "Each squad draws a 6-column place value chart (Hundred Thousands to Ones) and places " + mat + " in each column to represent 478,625 (4 in HT, 7 in TTh, 8 in Th, 6 in H, 2 in T, 5 in O).",
                                "Learners copy the place value table into their books, write the total value of 7 (70,000), and draw a number line showing 478,625 rounds to 480,000.",
                                "Using your " + mat + ", represent 634,290, state the total value of digit 3, and round 634,290 to the nearest thousand.",
                                "In the number 852,741: (a) state the place value and total value of digit 5, and (b) round 852,741 to the nearest ten thousand.",
                                "(a) Place value = Ten Thousands; Total value = 50,000. (b) Nearest ten thousand = 850,000."
                        ),
                        new TopicPedagogyDetail(
                                "national primary school book census",
                                "Three sub-counties received 345,890, 354,120, and 345,190 textbooks. Arrange these numbers in ascending order and find the square of 15.",
                                "Squads represent the digits of the three numbers using " + mat + " on place value strips, comparing from the Hundred Thousands column down to the Hundreds column, then arrange 15 rows of 15 in a grid sketch.",
                                "Learners write the ordered sequence (345,190 < 345,890 < 354,120) in words and symbols, and compute 15² = 15 × 15 = 225.",
                                "Order 512,400, 521,040, and 512,040 from largest to smallest (descending), and work out √196.",
                                "(a) Write 609,045 in words. (b) Find the value of 14² and √144.",
                                "(a) Six hundred and nine thousand and forty-five. (b) 14² = 196; √144 = 12."
                        ),
                        new TopicPedagogyDetail(
                                "dairy cooperative milk delivery records",
                                "A dairy cooperative processed 763,480 litres of milk in a month. Round this off to the nearest hundred thousand and find the square root of 225.",
                                "Squads use " + mat + " to mark 763,480 on a desk number line between 700,000 and 800,000, seeing it is past the midpoint 750,000, and group " + mat + " into prime factor pairs of 225.",
                                "Learners record 763,480 ≈ 800,000 (nearest hundred thousand) and factorize 225 = 3 × 3 × 5 × 5 so √225 = 3 × 5 = 15.",
                                "Round 244,950 to the nearest ten thousand, and work out 18².",
                                "(a) Round 936,410 to the nearest ten thousand. (b) Work out √256.",
                                "(a) 940,000 (thousands digit is 6, so round up). (b) √256 = 16."
                        ),
                        new TopicPedagogyDetail(
                                "county stadium attendance and voter registration",
                                "In a county registration drive, 594,308 citizens registered. What is the difference between the total value of digit 9 and the total value of digit 3?",
                                "Squads place 9 items of " + mat + " in the Ten Thousands column (90,000) and 3 items in the Hundreds column (300) and model subtracting 300 from 90,000.",
                                "Learners write 90,000 - 300 = 89,700 in their exercise books and write 594,308 in words.",
                                "Find the sum of the total value of digit 6 and digit 4 in the number 468,912.",
                                "In the number 748,265, find the difference between the total value of digit 4 and the total value of digit 2.",
                                "Total value of 4 = 40,000; Total value of 2 = 200. Difference = 40,000 - 200 = 39,800."
                        )
                );

            case "1.2": // Multiplication (4 distinct scenarios)
                return Arrays.asList(
                        new TopicPedagogyDetail(
                                "agricultural harvest crates & lorry loads",
                                "A farmers' cooperative packed 1,234 avocadoes per crate into 15 crates. Estimate by rounding and calculate the exact total.",
                                "Squads use " + mat + " to split the multiplier 15 into (10 + 5): first modelling 1,234 × 10 = 12,340, then 1,234 × 5 = 6,170 using grouped bundles of " + mat + ".",
                                "Learners write the vertical multiplication in their books: 12,340 + 6,170 = 18,510, and compare with the estimate 1,200 × 20 = 24,000.",
                                "A school bookstore receives 1,425 exercise books in each of 16 cartons. Compute the exact total.",
                                "Calculate 1,325 × 12 showing both partial products, and estimate by rounding to the nearest hundred and ten.",
                                "Exact: (1,325 × 10) + (1,325 × 2) = 13,250 + 2,650 = 15,900. Estimate: 1,300 × 10 = 13,000."
                        ),
                        new TopicPedagogyDetail(
                                "solar lantern distribution across sub-counties",
                                "Each rural dispensary solar kit costs KES 2,315. What is the cost of 24 solar kits?",
                                "Squads partition 24 into 2 tens (20) and 4 ones (4) using " + mat + ", computing 2,315 × 20 = 46,300 and 2,315 × 4 = 9,260.",
                                "Learners sum the partial products (46,300 + 9,260 = 55,560) in their exercise books and verify by rounding (2,300 × 20 = 46,000).",
                                "Find the next two numbers in the multiplication pattern: 15, 45, 135, 405, ___, ___.",
                                "Work out 2,140 × 23 and find the next term in the pattern: 12, 24, 48, 96, ___.",
                                "2,140 × 23 = (2,140 × 20) + (2,140 × 3) = 42,800 + 6,420 = 49,220. Next pattern term = 96 × 2 = 192."
                        ),
                        new TopicPedagogyDetail(
                                "poultry farm egg tray production",
                                "A poultry farm collects 1,650 eggs daily. How many eggs are collected in 18 days?",
                                "Squads arrange " + mat + " in an area-model box (1,000 + 600 + 50) × (10 + 8) and calculate the 6 sub-boxes before summing.",
                                "Learners add the partial products in their books: (1,650 × 10) + (1,650 × 8) = 16,500 + 13,200 = 29,700 eggs.",
                                "Estimate the product of 2,890 × 31 by rounding off both numbers, then find the exact product.",
                                "Abakery bakes 1,450 loaves of bread per day. How many loaves does it bake in 14 days?",
                                "(1,450 × 10) + (1,450 × 4) = 14,500 + 5,800 = 20,300 loaves."
                        ),
                        new TopicPedagogyDetail(
                                "tree seedling planting rows in a forest reserve",
                                "Volunteers planted 35 rows of indigenous trees with 1,125 seedlings in each row. How many seedlings were planted altogether?",
                                "Squads use " + mat + " to represent bundles of 1,000, 100, 20, and 5 multiplied by 30 and by 5.",
                                "Learners compute (1,125 × 30) + (1,125 × 5) = 33,750 + 5,625 = 39,375 seedlings in their books.",
                                "Create a multiplication pattern starting at 7 with a rule of 'multiply by 3' up to the 5th term.",
                                "Work out 1,520 × 25 showing clear vertical working.",
                                "(1,520 × 20) + (1,520 × 5) = 30,400 + 7,600 = 38,000."
                        )
                );

            case "1.3": // Division (4 distinct scenarios — no longer only maize!)
                return Arrays.asList(
                        new TopicPedagogyDetail(
                                "county school textbook & exercise book distribution",
                                "The Ministry of Education supplied 2,160 Mathematics textbooks to be shared equally among 18 primary schools. How many textbooks does each school receive?",
                                "Squads group " + mat + " into thousands, hundreds, and tens bundles, dividing 2,160 into 18 equal shares step-by-step (1 hundred = 1,800; remainder 360 ÷ 18 = 20).",
                                "Learners write the long division steps (Divide, Multiply, Subtract, Bring down) for 2,160 ÷ 18 = 120 textbooks in their exercise books.",
                                "A stationery store packs 1,920 pencils equally into 16 boxes. How many pencils are in each box? Also solve: 45 - 15 ÷ 3 × 2.",
                                "(a) Divide 1,728 by 12. (b) Work out the combined operation: 36 - 12 ÷ 4 × 2.",
                                "(a) 1,728 ÷ 12 = 144. (b) Division first: 12 ÷ 4 = 3; Multiplication next: 3 × 2 = 6; Subtraction last: 36 - 6 = 30."
                        ),
                        new TopicPedagogyDetail(
                                "greening Kenya tree seedling allocation",
                                "A forestry station has 3,360 grevillea seedlings to distribute equally among 24 schools. Estimate the quotient and calculate the exact number per school.",
                                "Squads use " + mat + " to model quotient estimation (3,400 ÷ 20 = 170) and then perform step-by-step division of 3,360 into 24 groups (100 per group uses 2,400; remaining 960 ÷ 24 = 40).",
                                "Learners record 3,360 ÷ 24 = 140 seedlings per school in their books and verify by multiplying 140 × 24 = 3,360.",
                                "Divide 2,925 seedlings among 15 youth groups, and solve: 60 ÷ 12 + 8 × 4 - 5.",
                                "(a) Work out 2,520 ÷ 14. (b) Solve: 24 + 18 ÷ 6 × 3.",
                                "(a) 2,520 ÷ 14 = 180. (b) 18 ÷ 6 = 3; 3 × 3 = 9; 24 + 9 = 33."
                        ),
                        new TopicPedagogyDetail(
                                "dairy cooperative milk crate packing",
                                "A dairy plant packed 4,320 packets of milk into crates holding 24 packets each. How many crates were filled?",
                                "Squads use " + mat + " to represent crates of 24, subtracting multiples of 24 (24 × 100 = 2,400; 24 × 80 = 1,920) to see how many crates make 4,320.",
                                "Learners complete the long division 4,320 ÷ 24 = 180 crates in their books.",
                                "If 1,850 eggs are packed into trays of 30, how many full trays are filled and how many eggs remain?",
                                "A factory packs 2,700 bars of soap equally into 15 cartons. How many bars are in each carton?",
                                "2,700 ÷ 15 = 180 bars of soap per carton."
                        ),
                        new TopicPedagogyDetail(
                                "community water harvest & solar kit sharing",
                                "A community borehole pumped 3,840 litres of water equally into 16 storage drums. How many litres are in each drum?",
                                "Squads arrange 16 groups on their desk chart and distribute bundles of " + mat + " (200 per drum = 3,200; remaining 640 ÷ 16 = 40).",
                                "Learners write the long division 3,840 ÷ 16 = 240 litres per drum and check using multiplication (240 × 16 = 3,840).",
                                "Work out: 100 - 48 ÷ 8 × 5 + 12.",
                                "(a) Divide 3,120 by 13. (b) Work out: 50 - 20 ÷ 5 × 4.",
                                "(a) 3,120 ÷ 13 = 240. (b) 20 ÷ 5 = 4; 4 × 4 = 16; 50 - 16 = 34."
                        )
                );

            case "1.4": // Fractions (4 distinct scenarios)
                return Arrays.asList(
                        new TopicPedagogyDetail(
                                "school shamba crop allocation",
                                "Grade 6 learners planted sukuma wiki on 1/2 of the school plot and spinach on 1/3 of it. What total fraction is planted, and what percentage is 3/4?",
                                "Squads count out 6 equal groups of " + mat + " (LCM of 2 and 3 is 6). They take 1/2 of 6 (3 items) and 1/3 of 6 (2 items) and combine them to show 3/6 + 2/6 = 5/6.",
                                "Learners draw a rectangular plot divided into 6 equal strips, shade 5/6, and convert 3/4 to a percentage (3/4 × 100% = 75%).",
                                "Work out 2 1/3 + 1 1/4 using the LCM, and find the reciprocal of 1 2/3.",
                                "(a) Work out 3/4 - 1/3 using the LCM. (b) Convert 4/5 into a percentage.",
                                "(a) LCM of 4 and 3 is 12: 9/12 - 4/12 = 5/12. (b) 4/5 × 100% = 80%."
                        ),
                        new TopicPedagogyDetail(
                                "sharing chapati and fruit harvest fairly",
                                "A family used 2 1/2 cups of wheat flour for chapati and 1 3/4 cups for mandazi. How many cups of flour did they use altogether?",
                                "Squads use " + mat + " in groups of 4 (quarters) to model 2 wholes + 2 quarters (2 2/4) plus 1 whole + 3 quarters (1 3/4), regrouping 5 quarters into 1 whole and 1 quarter.",
                                "Learners write 2 1/2 + 1 3/4 = 3 + (2/4 + 3/4) = 3 + 5/4 = 4 1/4 cups in their books.",
                                "Work out 4 1/2 - 1 2/3, and calculate the square of 2/5.",
                                "(a) Work out 1 1/2 + 2 1/4. (b) Find the square of 3/4 and the reciprocal of 5/8.",
                                "(a) 3 3/4. (b) (3/4)² = 9/16; Reciprocal of 5/8 = 8/5 (or 1 3/5)."
                        ),
                        new TopicPedagogyDetail(
                                "water tank levels and irrigation usage",
                                "A school tank was 7/8 full in the morning. By evening, 1/4 of the tank had been used. What fraction of water remained, and what percentage is that?",
                                "Squads arrange 8 equal piles of " + mat + " to represent eighths, start with 7 piles (7/8), and remove 2 piles (since 1/4 = 2/8) to leave 5 piles (5/8).",
                                "Learners write 7/8 - 1/4 = 7/8 - 2/8 = 5/8 in their books, and convert 7/10 to a percentage (70%).",
                                "Convert 65% into a simplified fraction, and work out (1 1/3)².",
                                "(a) Work out 5/6 - 1/2. (b) Convert 35% into a fraction in its simplest form.",
                                "(a) 5/6 - 3/6 = 2/6 = 1/3. (b) 35/100 = 7/20."
                        ),
                        new TopicPedagogyDetail(
                                "athletics relay track distances",
                                "Two runners covered 1 3/5 km and 2 1/2 km in a training relay. What total distance did they cover?",
                                "Squads use 10 groups of " + mat + " (LCM of 5 and 2 is 10) to convert 3/5 = 6/10 and 1/2 = 5/10, combining them to make 11/10 = 1 1/10.",
                                "Learners record 1 3/5 + 2 1/2 = 3 + (6/10 + 5/10) = 4 1/10 km in their exercise books.",
                                "Find the reciprocal of 2 1/4, and convert 17/20 to a percentage.",
                                "(a) Find the reciprocal of 1 3/4. (b) Work out (2/3)².",
                                "(a) 1 3/4 = 7/4, so the reciprocal is 4/7. (b) (2/3)² = 4/9."
                        )
                );

            case "1.5": // Decimals (4 distinct scenarios)
                return Arrays.asList(
                        new TopicPedagogyDetail(
                                "cereals market digital weighing scales",
                                "Two bags of beans weigh 14.375 kg and 12.45 kg on a digital scale. What is the place value of 5 in 14.375, and what is the combined mass?",
                                "Squads draw a decimal place value chart (Tens, Ones, . , Tenths, Hundredths, Thousandths) and place " + mat + " in each column keeping the decimal points vertically aligned.",
                                "Learners write 14.375 + 12.450 = 26.825 kg in their books and round the result to 2 decimal places (26.83 kg).",
                                "Convert 0.375 to a simplified fraction, and work out 25.6 - 8.475.",
                                "(a) Round 18.4682 to 2 decimal places. (b) Work out 6.45 + 3.125.",
                                "(a) 18.47. (b) 6.450 + 3.125 = 9.575."
                        ),
                        new TopicPedagogyDetail(
                                "tailoring & school uniform fabric measurements",
                                "A tailor had 9.5 metres of cloth and used 3.625 metres to sew a dress. How much cloth remained?",
                                "Squads use " + mat + " on a decimal chart to model adding placeholder zeros (9.500 - 3.625) and regrouping across tenths, hundredths, and thousandths.",
                                "Learners compute 9.500 - 3.625 = 5.875 metres in their books and convert 0.85 to a percentage (85%).",
                                "Round 4.9536 to 1, 2, and 3 decimal places.",
                                "(a) Work out 12.4 - 5.175. (b) Convert 0.65 into a percentage and a simplified fraction.",
                                "(a) 12.400 - 5.175 = 7.225. (b) 65% and 65/100 = 13/20."
                        ),
                        new TopicPedagogyDetail(
                                "laboratory & dispensary medicine measurements",
                                "A bottle contains 0.485 litres of solution. State the place value of each digit after the decimal point and round to 2 decimal places.",
                                "Squads place 4 items of " + mat + " in Tenths, 8 in Hundredths, and 5 in Thousandths on their desk chart.",
                                "Learners write: 4 = Tenths, 8 = Hundredths, 5 = Thousandths, and round 0.485 to 0.49.",
                                "Convert 3/8 into a decimal (up to 3 decimal places) and a percentage.",
                                "State the place value of digit 9 in 3.1492, and round 3.1492 to 3 decimal places.",
                                "Place value of 9 = Thousandths; 3.1492 rounded to 3 decimal places = 3.149."
                        ),
                        new TopicPedagogyDetail(
                                "athletics sprint timing & fuel pump readings",
                                "A boda-boda rider bought 4.25 litres of petrol on Monday and 3.875 litres on Wednesday. How many litres did he buy in total?",
                                "Squads align 4.250 and 3.875 using " + mat + " on place value strips and regroup 10 hundredths into 1 tenth.",
                                "Learners calculate 4.250 + 3.875 = 8.125 litres and express 0.125 as 1/8.",
                                "Work out: 15.75 + 4.325 - 6.5.",
                                "Convert 45% to a decimal, and work out 8.6 - 2.345.",
                                "45% = 0.45. 8.600 - 2.345 = 6.255."
                        )
                );

            case "2.1": // Length (4 distinct scenarios)
                return Arrays.asList(
                        new TopicPedagogyDetail(
                                "carpentry & classroom desk making",
                                "A carpenter joins two pieces of timber measuring 14 cm 8 mm and 9 cm 6 mm. What is their combined length in cm and mm?",
                                "Squads use " + mat + " and rulers to measure objects in cm and mm, regrouping every 10 mm into 1 cm.",
                                "Learners add 8 mm + 6 mm = 14 mm (1 cm 4 mm) and 14 cm + 9 cm + 1 cm = 24 cm 4 mm in their books.",
                                "Convert 84 mm into cm and mm, and subtract: 18 cm 3 mm - 6 cm 7 mm.",
                                "Work out 12 cm 7 mm + 8 cm 8 mm, giving your answer in cm and mm.",
                                "7 mm + 8 mm = 15 mm = 1 cm 5 mm; 12 + 8 + 1 = 21 cm. Answer: 21 cm 5 mm."
                        ),
                        new TopicPedagogyDetail(
                                "measuring circular water tanks & wheel rims (Circumference ÷ Diameter)",
                                "A circular drum top has a diameter of 14 cm and a circumference of 44 cm. Find the ratio Circumference ÷ Diameter and multiply 6 cm 4 mm by 5.",
                                "Squads wrap string/strips of " + mat + " around a circular lid to measure the circumference and across the middle for the diameter, verifying C ≈ 3.14 × D.",
                                "Learners record C ÷ D = 44 ÷ 14 = 22/7 (≈ 3.14) and calculate 6 cm 4 mm × 5 = 32 cm 0 mm.",
                                "Divide 25 cm 2 mm by 6, giving the answer in cm and mm.",
                                "(a) Convert 9 cm 4 mm into millimetres. (b) Multiply 4 cm 6 mm by 4.",
                                "(a) 9 × 10 + 4 = 94 mm. (b) 4 cm × 4 = 16 cm; 6 mm × 4 = 24 mm (2 cm 4 mm); Total = 18 cm 4 mm."
                        ),
                        new TopicPedagogyDetail(
                                "school garden fencing & seedling spacing",
                                "A garden path is 45 cm 5 mm wide. If it is widened by 16 cm 8 mm, what is the new width?",
                                "Squads model adding millimetres using " + mat + " (5 mm + 8 mm = 13 mm = 1 cm 3 mm).",
                                "Learners write 45 cm 5 mm + 16 cm 8 mm = 62 cm 3 mm in their exercise books.",
                                "A wire of length 36 cm 8 mm is cut into 8 equal pieces. Find the length of each piece in cm and mm.",
                                "Work out: 20 cm 2 mm - 11 cm 7 mm.",
                                "Borrow 1 cm (10 mm): 12 mm - 7 mm = 5 mm; 19 cm - 11 cm = 8 cm. Answer: 8 cm 5 mm."
                        ),
                        new TopicPedagogyDetail(
                                "bookbinding & spine thickness measurement",
                                "One textbook is 1 cm 6 mm thick. What is the height of a stack of 7 identical textbooks?",
                                "Squads stack 7 groups of (1 cm and 6 mm) using " + mat + " and regroup 42 mm into 4 cm 2 mm.",
                                "Learners calculate 1 cm 6 mm × 7 = 7 cm + 42 mm = 11 cm 2 mm.",
                                "Convert 135 mm to cm and mm, and divide 19 cm 5 mm by 5.",
                                "Divide 16 cm 8 mm by 4.",
                                "16 cm ÷ 4 = 4 cm; 8 mm ÷ 4 = 2 mm. Answer: 4 cm 2 mm."
                        )
                );

            case "2.2": // Area (4 distinct scenarios)
                return Arrays.asList(
                        new TopicPedagogyDetail(
                                "school vegetable garden triangular beds",
                                "A triangular school flower bed has a base of 12 m and a perpendicular height of 8 m. Calculate its area.",
                                "Squads arrange " + mat + " to outline a 12 by 8 rectangle (96 square units) and split it diagonally to prove a right-angled triangle has half the area (48 square units).",
                                "Learners sketch the triangle and compute Area = 1/2 × base × height = 1/2 × 12 m × 8 m = 48 m².",
                                "Find the total area of a combined plot made of a 10 m by 6 m rectangle joined to a triangle of base 6 m and height 4 m.",
                                "Calculate the area of a triangle with base 14 cm and perpendicular height 9 cm.",
                                "Area = 1/2 × 14 cm × 9 cm = 7 × 9 = 63 cm²."
                        ),
                        new TopicPedagogyDetail(
                                "gabion & house gable wall painting",
                                "The front wall of a store consists of a square of side 6 m topped by a triangular roof gable of base 6 m and height 3 m. Find the total area of the wall.",
                                "Squads model the combined shape on desk grids using " + mat + ": a 6×6 square at the bottom and a triangle on top.",
                                "Learners compute Area of square = 6 × 6 = 36 m², Area of triangle = 1/2 × 6 × 3 = 9 m², Total Area = 36 + 9 = 45 m².",
                                "A combined shape is made of a rectangle 12 cm by 5 cm and a right-angled triangle of base 8 cm and height 5 cm. Find the total area.",
                                "Find the total area of a figure made of a square of side 8 cm and a triangle of base 8 cm and height 6 cm.",
                                "Square area = 8 × 8 = 64 cm²; Triangle area = 1/2 × 8 × 6 = 24 cm²; Total = 88 cm²."
                        ),
                        new TopicPedagogyDetail(
                                "tree nursery shade net design",
                                "A rectangular shade net measures 15 m by 10 m. A triangular corner of base 6 m and height 4 m is left open for a water tank. Find the area covered by the net.",
                                "Squads model the full rectangle with " + mat + " and remove the triangular corner section.",
                                "Learners calculate Rectangle Area = 15 × 10 = 150 m², Triangle Area = 1/2 × 6 × 4 = 12 m², Shaded Area = 150 - 12 = 138 m².",
                                "Calculate the area of a right-angled triangle with base 20 cm and height 13 cm.",
                                "A triangular road sign has a base of 30 cm and a height of 24 cm. Find its area.",
                                "Area = 1/2 × 30 cm × 24 cm = 15 × 24 = 360 cm²."
                        ),
                        new TopicPedagogyDetail(
                                "classroom floor & poultry house layout",
                                "A trapezium-shaped poultry run is split into a rectangle measuring 8 m by 5 m and a triangle of base 4 m and height 5 m. What is the total floor area?",
                                "Squads use " + mat + " to divide the combined shape into Part A (rectangle) and Part B (triangle).",
                                "Learners record Part A = 8 × 5 = 40 m², Part B = 1/2 × 4 × 5 = 10 m², Total Area = 50 m².",
                                "If a triangle has an area of 36 cm² and a base of 9 cm, what is its height?",
                                "Find the area of a combined shape consisting of two identical triangles each of base 10 cm and height 7 cm.",
                                "Area of 1 triangle = 1/2 × 10 × 7 = 35 cm²; Total area of 2 triangles = 70 cm²."
                        )
                );

            case "2.3": // Capacity (4 distinct scenarios)
                return Arrays.asList(
                        new TopicPedagogyDetail(
                                "school rainwater harvesting & jerrycans",
                                "A school handwashing station tank holds 45 litres of water. Convert this capacity into millilitres (ml) and decilitres (dl).",
                                "Squads use " + mat + " to model the conversion rules: 1 Litre = 10 decilitres (dl) = 1,000 millilitres (ml) = 1,000 cm³.",
                                "Learners write 45 L × 1,000 = 45,000 ml and 45 L × 10 = 450 dl in their exercise books.",
                                "A 20-litre jerrycan is full of water. If 12,500 ml are used, how many litres and millilitres remain?",
                                "Convert 3,500 millilitres into: (a) litres and millilitres, and (b) decilitres.",
                                "(a) 3 litres 500 millilitres. (b) 3,500 ÷ 100 = 35 decilitres (dl)."
                        ),
                        new TopicPedagogyDetail(
                                "dairy cooperative milk packaging in decilitres & millilitres",
                                "A dairy processes 80 litres of yoghurt and packs it into 5-decilitre (5 dl) cups. How many cups are filled?",
                                "Squads group " + mat + " in tens to convert 80 litres into 800 decilitres (80 × 10 = 800 dl), then divide by 5 dl.",
                                "Learners compute 800 dl ÷ 5 dl = 160 cups in their exercise books.",
                                "Convert 7,200 cm³ into litres and millilitres.",
                                "How many 250 ml packets of milk can be packed from a 15-litre container?",
                                "15 L = 15,000 ml. Number of packets = 15,000 ÷ 250 = 60 packets."
                        ),
                        new TopicPedagogyDetail(
                                "tree nursery drip irrigation calibration",
                                "Each seedling receives 4 decilitres (4 dl) of water. How many litres of water are needed for 50 seedlings?",
                                "Squads use " + mat + " to multiply 50 × 4 dl = 200 dl and regroup every 10 dl into 1 litre.",
                                "Learners write 200 dl ÷ 10 = 20 litres in their exercise books.",
                                "Add 4 L 650 ml and 3 L 750 ml, giving the answer in litres and millilitres.",
                                "Convert 180 decilitres (dl) into: (a) litres, and (b) millilitres.",
                                "(a) 180 ÷ 10 = 18 litres. (b) 18 × 1,000 = 18,000 millilitres."
                        ),
                        new TopicPedagogyDetail(
                                "dispensary liquid medicine & sanitizer mixing",
                                "A rectangular container measures 20 cm by 10 cm by 15 cm. Find its volume in cm³ and its capacity in litres.",
                                "Squads model Volume = 20 × 10 × 15 = 3,000 cm³ using " + mat + " and apply 1,000 cm³ = 1 Litre.",
                                "Learners write 3,000 cm³ ÷ 1,000 = 3 litres in their books.",
                                "Convert 4.5 litres into cm³ and decilitres.",
                                "A container holds 6,400 cm³ of liquid. Express this capacity in litres and millilitres.",
                                "Since 1 cm³ = 1 ml, 6,400 cm³ = 6,400 ml = 6 litres 400 millilitres."
                        )
                );

            case "2.4": // Mass (4 distinct scenarios)
                return Arrays.asList(
                        new TopicPedagogyDetail(
                                "cereals board maize harvest weighbridge",
                                "Two lorries deliver 4 tonnes 650 kg and 3 tonnes 500 kg of grain. Find the total mass in tonnes and kilogrammes.",
                                "Squads group " + mat + " where 1 large bundle = 1 tonne (1,000 kg) and single items = 100 kg, regrouping 1,000 kg into 1 tonne.",
                                "Learners add 650 kg + 500 kg = 1,150 kg (1 t 150 kg) and 4 t + 3 t + 1 t = 8 t 150 kg.",
                                "Convert 6,250 kg into tonnes and kg, and subtract: 9 t 200 kg - 3 t 750 kg.",
                                "Work out 5 tonnes 400 kg + 2 tonnes 850 kg in tonnes and kilogrammes.",
                                "400 kg + 850 kg = 1,250 kg = 1 t 250 kg; 5 + 2 + 1 = 8 tonnes 250 kg."
                        ),
                        new TopicPedagogyDetail(
                                "tea factory green leaf collection trucks",
                                "A tea factory truck weighs 3 tonnes 400 kg when empty and 7 tonnes 150 kg when loaded with tea leaves. What is the mass of the tea leaves?",
                                "Squads use " + mat + " to model borrowing 1 tonne (1,000 kg) from 7 tonnes so 1,150 kg - 400 kg = 750 kg.",
                                "Learners compute 7 t 150 kg - 3 t 400 kg = 3 tonnes 750 kg in their books.",
                                "Multiply 2 tonnes 350 kg by 4.",
                                "Work out: 8 tonnes 300 kg - 4 tonnes 650 kg.",
                                "Borrow 1 tonne (1,000 kg): 1,300 kg - 650 kg = 650 kg; 7 t - 4 t = 3 t. Answer: 3 tonnes 650 kg."
                        ),
                        new TopicPedagogyDetail(
                                "hardware cement & sand construction delivery",
                                "A builder ordered 60 bags of cement, each weighing 50 kg. What is the total mass in tonnes?",
                                "Squads use " + mat + " to multiply 60 × 50 kg = 3,000 kg and group by 1,000 kg to convert to tonnes.",
                                "Learners write 3,000 kg ÷ 1,000 = 3 tonnes in their exercise books.",
                                "Share 12 tonnes 600 kg of relief fertiliser equally among 6 cooperatives.",
                                "(a) Convert 4.75 tonnes into kilogrammes. (b) Multiply 1 tonne 450 kg by 3.",
                                "(a) 4.75 × 1,000 = 4,750 kg. (b) 3 t + 1,350 kg = 4 tonnes 350 kg."
                        ),
                        new TopicPedagogyDetail(
                                "bridge weight limit & produce transport",
                                "A rural bridge has a maximum mass limit of 10 tonnes. An empty lorry weighs 3 tonnes 200 kg. How many tonnes and kg of cargo can it safely carry?",
                                "Squads model 10 tonnes as 9 tonnes 1,000 kg using " + mat + " and subtract 3 tonnes 200 kg.",
                                "Learners record 10 t 000 kg - 3 t 200 kg = 6 tonnes 800 kg.",
                                "Divide 15 tonnes 200 kg by 4.",
                                "Divide 9 tonnes 600 kg equally by 3.",
                                "9 t ÷ 3 = 3 tonnes; 600 kg ÷ 3 = 200 kg. Answer: 3 tonnes 200 kg."
                        )
                );

            case "2.5": // Time (4 distinct scenarios)
                return Arrays.asList(
                        new TopicPedagogyDetail(
                                "Madaraka Express (SGR) train timetable",
                                "The SGR train departs Mombasa at 8:00 a.m. and arrives in Nairobi at 1:15 p.m. Write the arrival time in 24-hour clock format and find the duration of the journey.",
                                "Squads arrange " + mat + " around a 24-hour clock diagram drawn on paper to count hours from 0800 hrs to 1315 hrs.",
                                "Learners convert 1:15 p.m. -> 1315 hrs and subtract 1315 hrs - 0800 hrs = 5 hours 15 minutes.",
                                "A bus leaves Nakuru at 1445 hrs and reaches Kisumu at 1810 hrs. How long did the journey take?",
                                "(a) Convert 4:45 p.m. to the 24-hour clock system. (b) Find the time taken from 0920 hrs to 1305 hrs.",
                                "(a) 1645 hrs. (b) 3 hours 45 minutes."
                        ),
                        new TopicPedagogyDetail(
                                "school inter-house sports day schedule",
                                "A school football match started at 3:40 p.m. and lasted 1 hour 35 minutes. At what time did it end in both 24-hour and 12-hour clock systems?",
                                "Squads convert 3:40 p.m. to 1540 hrs using " + mat + " and add 1 hr 35 mins (40 + 35 = 75 mins = 1 hr 15 mins).",
                                "Learners write 1540 hrs + 1 hr 35 mins = 1715 hrs (which is 5:15 p.m.).",
                                "Convert 2135 hrs and 0045 hrs into 12-hour (a.m./p.m.) time.",
                                "A choir rehearsal lasted 2 hours 25 minutes and ended at 1610 hrs. At what time did it start?",
                                "1610 hrs - 2 hrs 25 mins: borrow 1 hr (60 mins) -> 70 - 25 = 45 mins; 15 - 2 = 13 -> 1345 hrs (or 1:45 p.m.)."
                        ),
                        new TopicPedagogyDetail(
                                "upcountry matatu & bus stage departures",
                                "A matatu left Nairobi at 0650 hrs and arrived in Nyeri at 0930 hrs after a 15-minute stopover. What was the total travel time?",
                                "Squads count the minutes from 0650 hrs to 0700 hrs (10 mins) and from 0700 hrs to 0930 hrs (2 hrs 30 mins) using " + mat + ".",
                                "Learners sum 10 mins + 2 hrs 30 mins = 2 hours 40 minutes total journey time (and 2 hrs 25 mins moving time).",
                                "Write 11:20 p.m. and 7:05 a.m. in the 24-hour clock system.",
                                "(a) Convert 1950 hrs to 12-hour clock time. (b) Find the duration from 10:30 a.m. to 2:15 p.m.",
                                "(a) 1950 - 1200 = 7:50 p.m. (b) From 10:30 a.m. to 12:00 noon = 1 hr 30 mins; plus 2 hrs 15 mins = 3 hours 45 minutes."
                        ),
                        new TopicPedagogyDetail(
                                "hospital shift & flight arrival schedules",
                                "A nurse starts her duty at 2000 hrs and finishes at 0400 hrs the next morning. Convert both times to 12-hour format and find her shift duration.",
                                "Squads use a 24-hour clock chart and " + mat + " to count from 2000 hrs (8:00 p.m.) to 2400 hrs (4 hrs) plus 0000 hrs to 0400 hrs (4 hrs).",
                                "Learners write 2000 hrs = 8:00 p.m., 0400 hrs = 4:00 a.m., Total shift = 4 + 4 = 8 hours.",
                                "A flight departs Eldoret at 1140 hrs and takes 55 minutes to reach Nairobi. Find the arrival time.",
                                "Convert (a) 12:15 a.m. and (b) 12:15 p.m. into 24-hour clock format.",
                                "(a) 0015 hrs. (b) 1215 hrs."
                        )
                );

            case "2.6": // Money (4 distinct scenarios)
                return Arrays.asList(
                        new TopicPedagogyDetail(
                                "local market mama mboga profit & loss",
                                "A trader bought a crate of tomatoes for KES 1,800 and sold all of them for KES 2,350. Did she make a profit or loss, and how much?",
                                "Squads use " + mat + " as currency counters to represent Buying Price (1,800) and Selling Price (2,350) and count the difference.",
                                "Learners write Profit = Selling Price - Buying Price = 2,350 - 1,800 = KES 550 in their books.",
                                "Prepare a household budget for KES 500: 2 kg unga at KES 160/kg, 1 L milk at KES 70, cabbage at KES 60. Find total spent and balance.",
                                "A shopkeeper bought a bag of sugar for KES 6,200 and sold it for KES 5,850. State whether he made a profit or loss and calculate the amount.",
                                "Since Selling Price (5,850) < Buying Price (6,200), it is a Loss. Loss = 6,200 - 5,850 = KES 350."
                        ),
                        new TopicPedagogyDetail(
                                "school agriculture club poultry project budget",
                                "The Grade 6 Agriculture Club bought 20 chicks for KES 2,000, spent KES 1,500 on feeds, and sold the grown birds for KES 5,200. Find their net profit.",
                                "Squads model Total Buying/Production Cost (2,000 + 1,500 = 3,500) using " + mat + " and compare with Selling Price (5,200).",
                                "Learners record Total Cost = KES 3,500; Profit = 5,200 - 3,500 = KES 1,700.",
                                "Why do citizens pay Value Added Tax (VAT) when buying goods in a supermarket? Name two public services funded by taxes.",
                                "A carpenter bought timber for KES 3,400, varnish for KES 400, and sold the finished table for KES 4,750. Calculate his profit.",
                                "Total Buying Cost = 3,400 + 400 = KES 3,800. Profit = 4,750 - 3,800 = KES 950."
                        ),
                        new TopicPedagogyDetail(
                                "back-to-school stationery shopping bill",
                                "A parent has KES 1,000 and buys: 6 exercise books at KES 60 each, a mathematical set at KES 250, and a ruler at KES 40. Prepare the shopping bill and find the change.",
                                "Squads use " + mat + " to group 6 × 60 = 360, then add 250 + 40 = 650, and subtract from 1,000.",
                                "Learners draw a 4-column bill table (Item, Qty, Unit Price, Total) summing to KES 650, and Balance = 1,000 - 650 = KES 350.",
                                "If a trader buys 10 pineapples at KES 80 each and 2 get spoilt, how much must she sell the remaining 8 pineapples for to make a profit of KES 160?",
                                "Calculate the total bill for: 3 packets of milk at KES 65 each and 2 loaves of bread at KES 60 each.",
                                "Milk: 3 × 65 = KES 195; Bread: 2 × 60 = KES 120; Total Bill = 195 + 120 = KES 315."
                        ),
                        new TopicPedagogyDetail(
                                "kiosk mobile money & savings plan",
                                "A youth group saves KES 450 every week for 8 weeks to buy a wheelbarrow costing KES 3,200. Do they have enough money, and what is the surplus?",
                                "Squads model 450 × 8 = 3,600 using bundles of " + mat + " and subtract the price 3,200.",
                                "Learners compute Total Savings = KES 3,600; Surplus = 3,600 - 3,200 = KES 400.",
                                "Differentiate between Income Tax and Value Added Tax (VAT) in Kenya.",
                                "A trader bought a radio for KES 1,650 and sold it at a profit of KES 280. What was the selling price?",
                                "Selling Price = Buying Price + Profit = 1,650 + 280 = KES 1,930."
                        )
                );

            case "3.1": // Lines (4 distinct scenarios)
                return Arrays.asList(
                        new TopicPedagogyDetail(
                                "railway tracks & school fence construction",
                                "How do we construct a line parallel to line AB and a perpendicular bisector of line segment CD = 8 cm?",
                                "Squads arrange straight edges/items of " + mat + " on their desks to model parallel lines (constant distance apart) and perpendicular bisectors (meeting at 90° at the midpoint).",
                                "Learners draw CD = 8 cm in their books, bisect it at midpoint M (4 cm), and mark the 90° right angle.",
                                "Draw two parallel horizontal lines 4 cm apart and a perpendicular line cutting across both.",
                                "Draw a line segment PQ = 6 cm and construct its perpendicular bisector, stating the length of PM.",
                                "The perpendicular bisector cuts PQ at 90° into two equal halves: PM = 3 cm and MQ = 3 cm."
                        ),
                        new TopicPedagogyDetail(
                                "vegetable garden furrow rows & irrigation pipes",
                                "A farmer wants to dig 3 parallel planting furrows 3 cm apart on a garden plan and a main water pipe perpendicular to all 3 furrows.",
                                "Squads slide a ruler/set-square along their desk using " + mat + " as spacing markers to keep the lines equidistant.",
                                "Learners construct 3 parallel lines 3 cm apart and a perpendicular line at 90° in their exercise books.",
                                "Construct a perpendicular line from an external point P to a given line XY.",
                                "State two real-life examples of parallel lines and two examples of perpendicular lines in the classroom.",
                                "Parallel: opposite edges of a desk/blackboard, window bars. Perpendicular: corner of a book/door where vertical and horizontal edges meet at 90°."
                        ),
                        new TopicPedagogyDetail(
                                "football pitch markings & court lines",
                                "On a scale drawing of a football pitch, the touchline is 10 cm long. Bisect the touchline to draw the halfway line.",
                                "Squads fold a 10 cm strip or use " + mat + " to locate the exact midpoint at 5 cm and form a 90° angle.",
                                "Learners construct a 10 cm line segment and draw its perpendicular bisector at the 5 cm mark.",
                                "Draw a vertical line AB = 7 cm and construct its perpendicular bisector.",
                                "If line XY = 9 cm is bisected at point K, what is the length of XK and what angle does the bisector make with XY?",
                                "XK = 9 ÷ 2 = 4.5 cm; Angle = 90° (right angle)."
                        ),
                        new TopicPedagogyDetail(
                                "carpentry window frame & roof beam layout",
                                "A window frame has two vertical bars parallel to each other and a horizontal crossbar perpendicular to both.",
                                "Squads arrange " + mat + " to form a rectangular frame, checking each corner with a square paper corner (90°).",
                                "Learners construct parallel lines using a ruler and set-square in their exercise books.",
                                "Draw line MN = 8.4 cm and divide it into 4 equal parts by bisecting it twice.",
                                "Line segment AB = 12 cm is bisected at M, and AM is bisected at N. Find the length of AN.",
                                "AM = 12 ÷ 2 = 6 cm; AN = 6 ÷ 2 = 3 cm."
                        )
                );

            case "3.2": // Angles (4 distinct scenarios)
                return Arrays.asList(
                        new TopicPedagogyDetail(
                                "roof truss angles & carpentry joints",
                                "Two interior angles of a triangular roof truss measure 65° and 45°. What is the size of the third angle?",
                                "Squads use " + mat + " and paper triangles: they tear off the 3 corners of a triangle and place them side-by-side along a straight line to prove they add up to 180°.",
                                "Learners write 65° + 45° + x = 180° -> 110° + x = 180° -> x = 70° in their exercise books.",
                                "Two angles on a straight line are 125° and y. Find angle y.",
                                "A right-angled triangle has one angle of 90° and another angle of 35°. Calculate the third angle.",
                                "Third angle = 180° - (90° + 35°) = 180° - 125° = 55°."
                        ),
                        new TopicPedagogyDetail(
                                "crossroads & road junction angles on a straight line",
                                "A feeder road meets a straight highway at an angle of 118°. What is the size of the adjacent angle on the straight highway?",
                                "Squads arrange " + mat + " along a straight ruler edge and a branching line, measuring both angles with a protractor to confirm they sum to 180°.",
                                "Learners draw the straight line junction and calculate 180° - 118° = 62°.",
                                "An isosceles triangle has two equal base angles of 50° each. Find the top vertex angle.",
                                "Three angles on a straight line are 50°, 75°, and w. Find the size of angle w.",
                                "Angles on a straight line add up to 180°: w = 180° - (50° + 75°) = 180° - 125° = 55°."
                        ),
                        new TopicPedagogyDetail(
                                "bicycle wheel spokes & angles at a point",
                                "Three paths meet at a central monument forming angles of 110°, 130°, and z around the point. Find angle z.",
                                "Squads arrange " + mat + " radiating from a central point to show a full turn equals two straight lines (180° + 180° = 360°).",
                                "Learners write 110° + 130° + z = 360° -> 240° + z = 360° -> z = 120°.",
                                "In an equilateral triangle, all three angles are equal. Calculate the size of each angle.",
                                "In triangle ABC, angle A = 48° and angle B = 62°. Find angle C and state whether the triangle is acute, right, or obtuse.",
                                "Angle C = 180° - (48° + 62°) = 180° - 110° = 70°. Since all angles are less than 90°, it is an acute-angled triangle."
                        ),
                        new TopicPedagogyDetail(
                                "door openings & protractor measurement",
                                "Construct and measure an acute angle of 60° and an obtuse angle of 135° using a protractor.",
                                "Squads use two straight sticks/strips of " + mat + " hinged at one vertex to form acute (<90°), right (90°), and obtuse (90°–180°) angles.",
                                "Learners draw and label a 60° angle and a 135° angle in their exercise books.",
                                "If one angle of a triangle is 110° and the other two angles are equal, find the size of each equal angle.",
                                "An isosceles triangle has a vertex angle of 40° and two equal base angles. Find the size of each base angle.",
                                "Sum of the two equal base angles = 180° - 40° = 140°. Each base angle = 140° ÷ 2 = 70°."
                        )
                );

            case "3.3": // 3-D Objects (4 distinct scenarios)
                return Arrays.asList(
                        new TopicPedagogyDetail(
                                "packaging cartons, milk packets & chalk boxes",
                                "Compare the number of faces, edges, and vertices of a rectangular cuboid (chalk box) and a square-based pyramid.",
                                "Squads inspect 3-D shapes modelled or marked with " + mat + ", numbering each flat Face, tracing each Edge, and touching each corner (Vertex).",
                                "Learners draw a comparison table in their books: Cuboid (6 Faces, 12 Edges, 8 Vertices) vs Square-Based Pyramid (5 Faces, 8 Edges, 5 Vertices).",
                                "Count and record the faces, edges, and vertices of a triangular prism (tent/roof shape).",
                                "Complete the properties of a Square-Based Pyramid: Faces = ___, Edges = ___, Vertices = ___.",
                                "Faces = 5 (1 square base + 4 triangular faces); Edges = 8; Vertices = 5."
                        ),
                        new TopicPedagogyDetail(
                                "traditional hut roofs & pyramid structures",
                                "How many faces, edges, and vertices does a triangular-based pyramid (tetrahedron) have?",
                                "Squads join 6 straight pieces/sticks and 4 corner markers from " + mat + " to build a 3-D triangular pyramid skeleton.",
                                "Learners sketch the triangular pyramid and record: 4 triangular Faces, 6 Edges, and 4 Vertices.",
                                "Verify Euler's relationship (Faces + Vertices - Edges = 2) for both a cube and a triangular pyramid.",
                                "State the number of faces, edges, and vertices of a Triangular Prism.",
                                "Faces = 5 (2 triangular ends + 3 rectangular sides); Edges = 9; Vertices = 6."
                        ),
                        new TopicPedagogyDetail(
                                "water drums, tins & cylindrical containers",
                                "Identify the surfaces and edges of a closed cylindrical tin vs an open cylindrical cup.",
                                "Squads examine cylindrical containers and flat paper nets using " + mat + ", noting 2 flat circular faces and 1 curved surface on a closed cylinder.",
                                "Learners draw the net of a closed cylinder (1 rectangle + 2 circles) and a cube (6 squares) in their books.",
                                "Which 3-D object has 6 congruent square faces, 12 equal edges, and 8 vertices?",
                                "Find the total number of edges and vertices on two separate cuboids.",
                                "One cuboid has 12 edges and 8 vertices; two cuboids have 24 edges and 16 vertices."
                        ),
                        new TopicPedagogyDetail(
                                "greenhouse & house roof triangular prisms",
                                "A greenhouse roof is shaped like a triangular prism. Describe the shapes of its 5 faces.",
                                "Squads unfold or model a triangular prism using " + mat + " to see 3 rectangular faces and 2 triangular end faces.",
                                "Learners draw the net of a triangular prism in their books and label all 5 faces, 9 edges, and 6 vertices.",
                                "What is the difference between the number of edges of a cuboid and a square-based pyramid?",
                                "Which 3-D solid has 1 square face and 4 triangular faces? State its number of edges.",
                                "A Square-Based Pyramid; it has 8 edges."
                        )
                );

            case "4.1": // Bar Graphs (4 distinct scenarios)
                return Arrays.asList(
                        new TopicPedagogyDetail(
                                "weekly school attendance records",
                                "Grade 6 attendance from Monday to Friday was: Mon=40, Tue=45, Wed=35, Thu=50, Fri=45. Represent this on a bar graph using a vertical scale of 1 unit = 5 learners.",
                                "Squads stack " + mat + " in 5 vertical columns on their desks (where 1 item = 5 learners: Mon=8, Tue=9, Wed=7, Thu=10, Fri=9) to build a physical bar graph.",
                                "Learners draw labelled axes (Days vs Number of Learners), plot the 5 bars of equal width, and identify Thursday as the highest attendance day.",
                                "From the attendance graph, find the difference between Thursday and Wednesday attendance and the average daily attendance.",
                                "On a bar graph with vertical scale 1 cm = 10 bags of maize, how tall should the bar be to represent 45 bags?",
                                "Bar height = 45 ÷ 10 = 4.5 cm."
                        ),
                        new TopicPedagogyDetail(
                                "school tree-planting species tally & graph",
                                "Learners planted 4 types of trees: Cypress=30, Grevillea=50, Mango=20, Avocado=40. Make a frequency table and draw a bar graph (1 square = 10 trees).",
                                "Squads group " + mat + " in tally bundles of 5 to verify the frequency table, then build 4 bars of heights 3, 5, 2, and 4 units.",
                                "Learners draw the frequency table and bar graph in their exercise books and calculate the total trees planted (140 trees).",
                                "How many more Grevillea trees were planted than Mango trees? What percentage of all trees were Avocado?",
                                "In a bar graph where 1 square represents 4 learners, a bar is 7 squares high. How many learners does it represent?",
                                "7 × 4 = 28 learners."
                        ),
                        new TopicPedagogyDetail(
                                "county rainfall distribution across months",
                                "Monthly rainfall (in mm) from Jan to May was: Jan=20, Feb=30, Mar=80, Apr=100, May=60. Represent this on a horizontal or vertical bar graph (1 unit = 20 mm).",
                                "Squads arrange " + mat + " in rows/columns of 1, 1.5, 4, 5, and 3 units to compare dry and wet months.",
                                "Learners draw the bar graph in their books and find the total rainfall for the 5 months (290 mm).",
                                "Between which two consecutive months was there the greatest increase in rainfall?",
                                "Five squads scored 15, 25, 20, 30, and 10 points in a math quiz. Find the range (highest minus lowest) and total points.",
                                "Range = 30 - 10 = 20 points; Total = 15 + 25 + 20 + 30 + 10 = 100 points."
                        ),
                        new TopicPedagogyDetail(
                                "market fruit sales survey",
                                "A vendor sold Oranges=60, Bananas=90, Mangoes=45, Pineapples=30. Using a scale of 1 cm = 15 fruits, find the bar height for each fruit and draw the graph.",
                                "Squads divide each frequency by 15 using " + mat + ": Oranges=4 cm, Bananas=6 cm, Mangoes=3 cm, Pineapples=2 cm.",
                                "Learners construct the bar graph accurately in their books with uniform spacing between bars.",
                                "If each pineapple was sold for KES 50, how much money did the vendor collect from pineapples?",
                                "Using a scale of 1 cm = 15 fruits, how many fruits are represented by a bar of height 5 cm?",
                                "5 × 15 = 75 fruits."
                        )
                );

            case "5.1": // Inequalities (4 distinct scenarios)
            default:
                return Arrays.asList(
                        new TopicPedagogyDetail(
                                "matatu passenger safety capacity limits",
                                "A 14-seater matatu already has 9 passengers inside. If x more passengers board so that the total does not exceed 14, form and solve the inequality.",
                                "Squads place 9 items of " + mat + " on the left side of a desk line and 14 on the right side, testing how many more items (x) can be added without exceeding 14.",
                                "Learners write 9 + x ≤ 14, subtract 9 from both sides to get x ≤ 5, and list the whole number solutions (0, 1, 2, 3, 4, 5).",
                                "Form and solve the inequality: 'Twice a number n plus 3 is less than 15' (2n + 3 < 15).",
                                "Solve the inequality x + 6 < 11 and list all the whole number values of x.",
                                "x < 11 - 6 -> x < 5. Whole number values: 0, 1, 2, 3, 4."
                        ),
                        new TopicPedagogyDetail(
                                "school shopping budget limits",
                                "A learner has KES 100 to buy exercise books costing KES 20 each. Form an inequality for the number of books (b) she can buy if she must keep at least KES 15 for bus fare.",
                                "Squads use " + mat + " (where 1 item = KES 10) to model 20b + 15 ≤ 100, removing 15 first to leave 85.",
                                "Learners solve 20b ≤ 85 -> b ≤ 4.25, so the maximum whole number of books is 4 (0, 1, 2, 3, or 4 books).",
                                "Solve the inequality: 3y - 4 > 11.",
                                "Solve the inequality 4m ≤ 20 and list all whole number values of m greater than 0.",
                                "Divide both sides by 4: m ≤ 5. Whole numbers greater than 0: 1, 2, 3, 4, 5."
                        ),
                        new TopicPedagogyDetail(
                                "bridge mass limit & carton loading",
                                "A handcart can carry less than 18 cartons of books. It already has 7 cartons loaded. Form and solve an inequality for the additional cartons (c) that can be loaded.",
                                "Squads place 7 items of " + mat + " on one side and 18 on the other to model 7 + c < 18.",
                                "Learners write 7 + c < 18 -> c < 11 in their exercise books.",
                                "Solve the inequality: p ÷ 3 + 2 ≤ 6.",
                                "Form an inequality for: 'When 5 is subtracted from a number k, the result is greater than 9', and solve it.",
                                "Inequality: k - 5 > 9. Add 5 to both sides: k > 14."
                        ),
                        new TopicPedagogyDetail(
                                "classroom desk seating & group sizes",
                                "Three equal squads of learners plus 4 prefects in a hall are fewer than 22 learners in total. Form and solve an inequality for the number of learners (s) in each squad.",
                                "Squads arrange 3 equal groups of " + mat + " plus 4 single items and compare against 22 items.",
                                "Learners write 3s + 4 < 22 -> 3s < 18 -> s < 6 (so each squad has fewer than 6 learners).",
                                "List the whole numbers that satisfy both x > 2 and x ≤ 6.",
                                "Solve the inequality 2x + 3 < 13 and list the whole number solutions.",
                                "2x < 13 - 3 -> 2x < 10 -> x < 5. Whole numbers: 0, 1, 2, 3, 4."
                        )
                );
        }
    }
}
