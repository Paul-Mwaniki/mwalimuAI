package training;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class DatasetGenerator {

    static class StrandInfo {
        String strand;
        String subStrand;
        String focus;
        String kenyanContext;

        StrandInfo(String strand, String subStrand, String focus, String kenyanContext) {
            this.strand = strand;
            this.subStrand = subStrand;
            this.focus = focus;
            this.kenyanContext = kenyanContext;
        }
    }

    static class MaterialPair {
        String primary;
        String manipulativeDesc;

        MaterialPair(String primary, String manipulativeDesc) {
            this.primary = primary;
            this.manipulativeDesc = manipulativeDesc;
        }
    }

    private static final String SYSTEM_PROMPT =
            "You are Mwalimu AI, an intelligent, empathetic on-device pedagogical assistant for Kenyan Grade 6 Competency-Based Curriculum (CBC) Mathematics teachers. " +
            "You specialize in low-cost resource adaptations, integrating teacher-specified materials (such as avocado seeds, dry sticks, bottle tops, stones, beans, and strings), " +
            "large classroom squad management (45-65 learners), hands-on CRA (Concrete-Representational-Abstract) inquiry, and KICD 4-tier formative assessment rubrics (EE, ME, AE, BE). " +
            "Always weave the teacher's exact materials and lesson ideas directly into the activities, steps, and questions.";

    public static void main(String[] args) throws Exception {
        File dir = new File("training");
        if (!dir.exists()) dir.mkdirs();

        List<StrandInfo> strands = getStrandList();
        List<MaterialPair> materials = getMaterialList();

        List<String> records = new ArrayList<>();

        // Generate diverse training entries
        for (int i = 0; i < strands.size(); i++) {
            StrandInfo s = strands.get(i);

            for (int j = 0; j < materials.size(); j++) {
                MaterialPair m = materials.get(j);

                // Scenario 1: Full Structured Lesson Plan with Custom Materials & Custom Hook
                records.add(buildLessonPlanSample(s, m, (i * materials.size() + j) % 3));

                // Scenario 2: Conversational Teacher Pedagogical Request
                records.add(buildChatPedagogySample(s, m));

                // Scenario 3: Classroom Game with specific materials
                records.add(buildMathGameSample(s, m));

                // Scenario 4: Formative Assessment & CBC Rubric
                records.add(buildAssessmentSample(s, m));

                // Scenario 5: Misconception & Remediation Scaffolding
                records.add(buildRemediationSample(s, m));
            }
        }

        System.out.println("Generated " + records.size() + " total high-quality CBC training samples.");

        // Split 90% train, 10% validation
        int trainSize = (int) (records.size() * 0.9);
        File trainFile = new File(dir, "cbc_teacher_train.jsonl");
        File valFile = new File(dir, "cbc_teacher_val.jsonl");

        try (BufferedWriter trainWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(trainFile), StandardCharsets.UTF_8));
             BufferedWriter valWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(valFile), StandardCharsets.UTF_8))) {

            for (int i = 0; i < records.size(); i++) {
                if (i < trainSize) {
                    trainWriter.write(records.get(i));
                    trainWriter.newLine();
                } else {
                    valWriter.write(records.get(i));
                    valWriter.newLine();
                }
            }
        }

        System.out.println("Wrote " + trainSize + " records to " + trainFile.getAbsolutePath());
        System.out.println("Wrote " + (records.size() - trainSize) + " records to " + valFile.getAbsolutePath());
    }

    private static String escapeJson(String s) {
        if (s == null) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '"': sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\b': sb.append("\\b"); break;
                case '\f': sb.append("\\f"); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                case '\t': sb.append("\\t"); break;
                default:
                    if (c < ' ') {
                        String t = "000" + Integer.toHexString(c);
                        sb.append("\\u").append(t.substring(t.length() - 4));
                    } else {
                        sb.append(c);
                    }
            }
        }
        return sb.toString();
    }

    private static String formatMessages(String userMsg, String assistantMsg) {
        return "{\"messages\": [" +
                "{\"role\": \"system\", \"content\": \"" + escapeJson(SYSTEM_PROMPT) + "\"}, " +
                "{\"role\": \"user\", \"content\": \"" + escapeJson(userMsg) + "\"}, " +
                "{\"role\": \"assistant\", \"content\": \"" + escapeJson(assistantMsg) + "\"}" +
                "]}";
    }

    private static String buildLessonPlanSample(StrandInfo s, MaterialPair m, int variant) {
        String hookIdea = getCustomHook(s.subStrand, m.primary, variant);
        int classSize = 48 + (variant * 5);

        String userPrompt = "Create a 40-minute CBC Lesson Plan for Grade 6 Mathematics.\n" +
                "• Strand: " + s.strand + "\n" +
                "• Sub-strand: " + s.subStrand + " (" + s.focus + ")\n" +
                "• Available Local Resources: " + m.primary + "\n" +
                "• Teacher Activity Idea / Hook: " + hookIdea + "\n" +
                "• Class Size: " + classSize + " learners (Mixed ability)\n" +
                "• Environment: Low-resource Kenyan classroom, no electricity, no internet.";

        StringBuilder asst = new StringBuilder();
        asst.append("### Grade 6 CBC Lesson Plan: ").append(s.subStrand).append(" — Practical Hands-On Investigation\n\n");
        asst.append("**1. Administrative Details:**\n");
        asst.append("• Learning Area: Mathematics | Grade: 6 | Duration: 40 Mins | Class Size: ").append(classSize).append(" Learners\n");
        asst.append("• Strand: ").append(s.strand).append(" | Sub-strand: ").append(s.subStrand).append("\n\n");

        asst.append("**2. Specific Learning Objective:**\n");
        asst.append("By the end of the lesson, the learner should be able to apply ").append(s.subStrand)
            .append(" concepts (").append(s.focus).append(") to solve real-world problems in cooperative squads using ")
            .append(m.primary).append(".\n\n");

        asst.append("**3. Lesson Hook & Introduction (5 Mins):**\n");
        asst.append("• Teacher Lesson Hook: ").append(hookIdea).append("\n");
        asst.append("• Prior Knowledge: Quick 2-minute call-and-response review connecting ").append(s.kenyanContext)
            .append(" to today's challenge.\n\n");

        asst.append("**4. Lesson Development & Hands-On Activities (30 Mins):**\n");
        asst.append("• Step 1: Concrete Exploration (15 Mins) — Teacher-Selected Focus\n");
        asst.append("  - Hands-on Materials: ").append(m.primary).append("\n");
        asst.append("  - Learner Steps: 1. Introduce the challenge (").append(hookIdea).append("). ")
            .append("2. In squads of 5-6, learners manipulate ").append(m.primary)
            .append(" to represent and model the problem concretely. ")
            .append("3. Scribe records the group model on mini-chalkboard or paper slate. ")
            .append("4. Teacher circulates to guide squads needing scaffolding.\n\n");

        asst.append("• Step 2: Guided Application & Peer Discussion (15 Mins) — Think-Pair-Share\n");
        asst.append("  - Strategy: Mathematical Reasoning & Communication\n");
        asst.append("  - Group Practice: Pairs compare their results using ").append(m.primary)
            .append(" and explain their mathematical justification. Learners transfer concrete models into numerical representation in their exercise books.\n\n");

        asst.append("**5. Large Class Management (").append(classSize).append(" Learners):**\n");
        asst.append("• Squad Roles: Divide class into fixed squads of 5-6. Assign designated roles:\n");
        asst.append("  - Facilitator: Keeps squad on track.\n");
        asst.append("  - Material Custodian: Distributes and collects ").append(m.primary).append(".\n");
        asst.append("  - Scribe: Records consensus answer.\n");
        asst.append("  - Timekeeper: Ensures completion within 15 minutes.\n\n");

        asst.append("**6. Inclusion & Differentiation:**\n");
        asst.append("• Scaffolding: Provide tactile guidance using ").append(m.primary).append(" and smaller numerical values for learners experiencing difficulties.\n");
        asst.append("• Extension: Fast finishers formulate an open-ended word problem using ").append(m.primary).append(" for a neighbouring squad to solve.\n\n");

        asst.append("**7. KICD CBC Assessment Rubric:**\n");
        asst.append("• Exceeds Expectations (EE): Independently solves and explains the abstract principle using ").append(m.primary).append(".\n");
        asst.append("• Meets Expectations (ME): Correctly models and computes ").append(s.subStrand).append(" with ").append(m.primary).append(" with minimal guidance.\n");
        asst.append("• Approaching Expectations (AE): Understands concept partially but requires peer assistance on calculation.\n");
        asst.append("• Below Expectations (BE): Needs continuous teacher modeling with physical manipulatives.\n\n");

        asst.append("**8. 2-Minute Exit Ticket:**\n");
        asst.append("Before leaving, each learner writes on a slip of paper: 'How did using ").append(m.primary)
            .append(" help you solve today's ").append(s.subStrand).append(" problem?'");

        return formatMessages(userPrompt, asst.toString());
    }

    private static String buildChatPedagogySample(StrandInfo s, MaterialPair m) {
        String userPrompt = "Habari Mwalimu! How can I teach " + s.subStrand + " (" + s.focus + ") using " + m.primary + " in a Grade 6 Kenyan classroom?";

        StringBuilder asst = new StringBuilder();
        asst.append("Jambo Mwalimu! Here is an actionable, step-by-step CBC pedagogical approach for Grade 6 ")
            .append(s.subStrand).append(" using ").append(m.primary).append(":\n\n");

        asst.append("1. Concrete Hook (3-5 Mins):\n");
        asst.append("Connect to ").append(s.kenyanContext).append(". Present a relatable local scenario and have 2 learners come to the front to manipulate ").append(m.primary).append(".\n\n");

        asst.append("2. CRA (Concrete-Representational-Abstract) Progression:\n");
        asst.append("• Concrete: In squads, learners arrange ").append(m.primary).append(" into physical groups or models.\n");
        asst.append("• Representational: Learners draw the physical arrangement in their exercise books as visual diagrams.\n");
        asst.append("• Abstract: Connect the drawings to standard mathematical symbols and equations on the chalkboard.\n\n");

        asst.append("3. Formative Check:\n");
        asst.append("Walk around desks and ask an oral prompt: 'Explain to your desk partner why your arrangement of ")
            .append(m.primary).append(" proves this solution.'");

        return formatMessages(userPrompt, asst.toString());
    }

    private static String buildMathGameSample(StrandInfo s, MaterialPair m) {
        String userPrompt = "Suggest an active, low-cost classroom game for Grade 6 " + s.subStrand + " using " + m.primary + ".";

        StringBuilder asst = new StringBuilder();
        asst.append("Here is an engaging, zero-cost CBC Mathematics game:\n\n");
        asst.append("• Game Title: ").append(s.subStrand).append(" Sprint (Mbio za Hisabati)\n");
        asst.append("• Materials: ").append(m.primary).append(" and chalkboard / paper slips.\n");
        asst.append("• Setup: Divide learners into squads of 5. Place a pile of ").append(m.primary).append(" in the center of each desk cluster.\n");
        asst.append("• How to Play:\n");
        asst.append("  1. The teacher writes a ").append(s.subStrand).append(" challenge on the chalkboard related to ").append(s.focus).append(".\n");
        asst.append("  2. Squads race to model the answer using their ").append(m.primary).append(" within 60 seconds.\n");
        asst.append("  3. When ready, the squad raises hands and the 'Reporter' explains their reasoning.\n");
        asst.append("  4. Accurate explanation earns 2 points; clean team collaboration earns 1 bonus point.\n");
        asst.append("• Core CBC Competencies: Critical thinking, collaboration, communication, and financial/numerical literacy.");

        return formatMessages(userPrompt, asst.toString());
    }

    private static String buildAssessmentSample(StrandInfo s, MaterialPair m) {
        String userPrompt = "What is the KICD CBC assessment rubric and formative exit ticket for Grade 6 " + s.subStrand + " when using " + m.primary + "?";

        StringBuilder asst = new StringBuilder();
        asst.append("### KICD CBC Formative Assessment Framework for Grade 6 ").append(s.subStrand).append(":\n\n");
        asst.append("• Assessment Criteria Focus: ").append(s.focus).append("\n");
        asst.append("• Concrete Evidence: Observable manipulation of ").append(m.primary).append(" and mathematical justification.\n\n");

        asst.append("4-Tier Assessment Rubric:\n");
        asst.append("1. Exceeds Expectations (EE):\n");
        asst.append("   - Solves accurately and explains abstract relationships independently.\n");
        asst.append("   - Creates an original real-world scenario involving ").append(m.primary).append(".\n\n");
        asst.append("2. Meets Expectations (ME):\n");
        asst.append("   - Accurately demonstrates and calculates ").append(s.subStrand).append(" using ").append(m.primary).append(".\n");
        asst.append("   - Works collaboratively in squads with minimal teacher prompting.\n\n");
        asst.append("3. Approaching Expectations (AE):\n");
        asst.append("   - Models the concept with ").append(m.primary).append(" but makes minor arithmetic or alignment errors.\n");
        asst.append("   - Requires occasional peer scaffolding.\n\n");
        asst.append("4. Below Expectations (BE):\n");
        asst.append("   - Struggles to initiate grouping or representation with ").append(m.primary).append(".\n");
        asst.append("   - Requires direct teacher modeling and simplified concrete tasks.\n\n");

        asst.append("• 2-Minute Chalkboard Exit Ticket:\n");
        asst.append("'Draw one sketch showing how ").append(m.primary).append(" can be used to solve a ").append(s.subStrand).append(" problem in your community.'");

        return formatMessages(userPrompt, asst.toString());
    }

    private static String buildRemediationSample(StrandInfo s, MaterialPair m) {
        String userPrompt = "Some of my Grade 6 learners are struggling with " + s.subStrand + " (" + s.focus + "). How do I differentiate and scaffold using " + m.primary + "?";

        StringBuilder asst = new StringBuilder();
        asst.append("Differentiating Grade 6 ").append(s.subStrand).append(" with Concrete Scaffolding:\n\n");
        asst.append("1. Diagnosing the Misconception:\n");
        asst.append("Learners often jump to abstract rules before understanding the physical quantity. When learners struggle with ")
            .append(s.focus).append(", do not repeat verbal explanations—switch immediately to tactile manipulatives.\n\n");

        asst.append("2. Scaffolding Routine with ").append(m.primary).append(":\n");
        asst.append("• Step 1: Give the struggling learner a smaller quantity of ").append(m.primary).append(".\n");
        asst.append("• Step 2: Guide them to physically group, measure, or count step-by-step.\n");
        asst.append("• Step 3: Have them narrate what they are doing aloud: 'I am taking these ").append(m.primary).append(" and...'\n");
        asst.append("• Step 4: Pair with an empathetic peer buddy who achieved 'Meets Expectations' (Think-Pair-Share).\n\n");

        asst.append("3. Extension for Fast Finishers:\n");
        asst.append("Challenge advanced learners: 'Can you formulate an inequality or multi-step word problem based on our ")
            .append(m.primary).append(" and test your classmates?'");

        return formatMessages(userPrompt, asst.toString());
    }

    private static String getCustomHook(String subStrand, String materials, int variant) {
        if (subStrand.contains("Whole") || subStrand.contains("Multiplication") || subStrand.contains("Division")) {
            if (variant == 0) return "Learners simulate a community tree-planting inventory by grouping " + materials + " into bundles of 10s and 100s.";
            if (variant == 1) return "Learners act as village market traders dividing harvest produce equally into 3 storage baskets using " + materials + ".";
            return "Learners model school lunch distribution by calculating food portions with " + materials + ".";
        } else if (subStrand.contains("Fraction") || subStrand.contains("Decimal")) {
            if (variant == 0) return "Learners fold paper and divide " + materials + " to show sharing chapati and harvest portions fairly.";
            if (variant == 1) return "Learners represent mixed numbers by making complete bundles and fractional remainders with " + materials + ".";
            return "Learners model M-Pesa transaction rates and percentage discounts using " + materials + ".";
        } else if (subStrand.contains("Length") || subStrand.contains("Perimeter") || subStrand.contains("Area")) {
            if (variant == 0) return "Learners measure the boundaries of the school vegetable plot using " + materials + " as non-standard and standard units.";
            if (variant == 1) return "Learners arrange " + materials + " along desk perimeters to discover the difference between length around and area inside.";
            return "Learners design a model classroom floor plan calculating tiles needed using " + materials + ".";
        } else if (subStrand.contains("Capacity") || subStrand.contains("Volume") || subStrand.contains("Mass")) {
            if (variant == 0) return "Learners estimate and calibrate market water containers using " + materials + " and standard measuring cups.";
            if (variant == 1) return "Learners build a makeshift balance to compare the weight of local crops using " + materials + ".";
            return "Learners solve a village drought rationing challenge by measuring daily water quotas using " + materials + ".";
        } else if (subStrand.contains("Money") || subStrand.contains("Time")) {
            if (variant == 0) return "Learners set up a kiosk stall using " + materials + " as commodities to compute total sales, profit, and change.";
            if (variant == 1) return "Learners plan a school transport journey timetable calculating departure, travel time, and arrival using " + materials + ".";
            return "Learners calculate household weekly budget limits and compare savings options using " + materials + ".";
        } else if (subStrand.contains("Lines") || subStrand.contains("Angles") || subStrand.contains("3-D")) {
            if (variant == 0) return "Learners construct architectural roof trusses and door hinges using " + materials + " to classify right, acute, and obtuse angles.";
            if (variant == 1) return "Learners build 3D geometric packaging boxes using " + materials + " counting faces, vertices, and edges.";
            return "Learners map railway lines and school fences to identify parallel and perpendicular lines using " + materials + ".";
        } else {
            if (variant == 0) return "Learners collect daily school attendance or rainfall counts with " + materials + " to construct a 3D bar graph.";
            if (variant == 1) return "Learners solve a matatu passenger loading limit inequality using " + materials + " as passenger counts.";
            return "Learners investigate mode and mean scores in school sports using " + materials + ".";
        }
    }

    private static List<StrandInfo> getStrandList() {
        List<StrandInfo> list = new ArrayList<>();
        list.add(new StrandInfo("Numbers", "Whole numbers", "place value up to millions; rounding; squares and square roots", "school enrolment and seedling counts"));
        list.add(new StrandInfo("Numbers", "Multiplication", "four-digit by two-digit multiplication; product estimation", "harvest crates and market stock"));
        list.add(new StrandInfo("Numbers", "Division", "division with multi-digit numbers; quotient estimation", "fair distribution of maize and food parcels"));
        list.add(new StrandInfo("Numbers", "Fractions", "least common multiples; addition and subtraction of fractions and mixed numbers; reciprocals; percentages", "sharing chapati and produce"));
        list.add(new StrandInfo("Numbers", "Decimals", "decimal place value to ten-thousandths; rounding; fraction and percentage conversions", "market weights and measurements"));
        list.add(new StrandInfo("Measurement", "Length", "measuring and converting lengths; practical length problems", "school garden and walking routes"));
        list.add(new StrandInfo("Measurement", "Area", "area calculations and comparisons using familiar shapes", "school garden plots"));
        list.add(new StrandInfo("Measurement", "Capacity", "capacity measurement and conversions in everyday settings", "water collection and storage"));
        list.add(new StrandInfo("Measurement", "Mass", "mass measurement, conversions and practical problems", "grain and vegetable markets"));
        list.add(new StrandInfo("Measurement", "Time", "reading time and solving elapsed-time problems", "school transport and daily schedules"));
        list.add(new StrandInfo("Measurement", "Money", "money calculations, budgeting, profit and loss, transactions", "kiosk purchases and budgeting"));
        list.add(new StrandInfo("Geometry", "Lines", "recognising and constructing parallel and perpendicular lines", "roads, fences and classroom objects"));
        list.add(new StrandInfo("Geometry", "Angles", "recognising, measuring and working with acute, right, obtuse, and reflex angles", "roof designs and door openings"));
        list.add(new StrandInfo("Geometry", "3-D Objects", "identifying faces, edges and vertices of three-dimensional objects", "packaging and local construction"));
        list.add(new StrandInfo("Data Handling", "Bar Graphs", "collecting and tabulating data; constructing and interpreting bar graphs", "school attendance and rainfall counts"));
        list.add(new StrandInfo("Algebra", "Inequalities", "forming and simplifying inequalities in one unknown", "budget limits and transport capacity"));
        return list;
    }

    private static List<MaterialPair> getMaterialList() {
        List<MaterialPair> list = new ArrayList<>();
        list.add(new MaterialPair("Avocado seeds and dry sticks", "large organic seeds and twigs"));
        list.add(new MaterialPair("Bottle caps and scrap cardboard", "metal soda crowns and recycled packaging"));
        list.add(new MaterialPair("Maize grains and bean counters", "dry agricultural food grains"));
        list.add(new MaterialPair("Small pebbles and sisal string", "smooth stones and local rope fiber"));
        list.add(new MaterialPair("Empty plastic bottles and calibrated cups", "recycled water bottles and tea cups"));
        list.add(new MaterialPair("Paper strips and carton cutouts", "scrap exercise book paper and card"));
        list.add(new MaterialPair("Matchsticks and chalk pieces", "wooden splints and colored chalk"));
        list.add(new MaterialPair("Dried tea leaves containers and balance tins", "empty metal tins and homemade balance"));
        list.add(new MaterialPair("Bamboo twigs and banana fiber strings", "locally cut bamboo and plant fibers"));
        list.add(new MaterialPair("Old newspaper grids and bottle tops", "printed paper squares and plastic caps"));
        return list;
    }
}
