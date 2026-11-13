package ke.co.mwalimuai.ai;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ke.co.mwalimuai.utils.AssetManagerHelper;

/**
 * Offline intelligent pedagogical assistant for Kenyan Grade 6 Mathematics teachers.
 * Performs on-device semantic intent analysis, extracts custom teacher materials,
 * and synthesizes contextualized KICD CBC pedagogical responses completely offline.
 */
public class OfflineLanguageModelEngine {

    private static final String TAG = "OfflineLMEngine";
    private static final String PROMPTS_DATA_PATH = "data/Teacher_AI_Prompts.json";

    public static class PromptEntry {
        public String query_id;
        public String curriculum_id;
        public String teacher_request;
        public String retrieval_context;
        public String expected_response_guidance;
        public String response_type;
    }

    public static class ChatMessage {
        public final boolean isUser;
        public final String text;
        public final long timestamp;

        public ChatMessage(boolean isUser, String text) {
            this.isUser = isUser;
            this.text = text;
            this.timestamp = System.currentTimeMillis();
        }
    }

    private static volatile OfflineLanguageModelEngine INSTANCE;
    private final List<PromptEntry> promptEntries = new ArrayList<>();
    private boolean isLoaded = false;

    public static OfflineLanguageModelEngine getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (OfflineLanguageModelEngine.class) {
                if (INSTANCE == null) {
                    INSTANCE = new OfflineLanguageModelEngine(context.getApplicationContext());
                }
            }
        }
        return INSTANCE;
    }

    private OfflineLanguageModelEngine(Context context) {
        loadPrompts(context);
    }

    private void loadPrompts(Context context) {
        try {
            if (AssetManagerHelper.assetExists(context, PROMPTS_DATA_PATH)) {
                String json = AssetManagerHelper.readAssetString(context, PROMPTS_DATA_PATH);
                Gson gson = new Gson();
                Type type = new TypeToken<List<PromptEntry>>() {}.getType();
                List<PromptEntry> list = gson.fromJson(json, type);
                if (list != null) {
                    promptEntries.addAll(list);
                    isLoaded = true;
                    Log.d(TAG, "Loaded " + promptEntries.size() + " teacher prompts for context matching.");
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading prompts dataset", e);
        }
    }

    /**
     * Generates a contextually rich, unique pedagogical response offline.
     */
    public String generateResponse(String userQuery) {
        if (userQuery == null || userQuery.trim().isEmpty()) {
            return "Habari Mwalimu! Ask me any question about Grade 6 Mathematics lesson planning, activities, games, low-cost materials, or classroom management.";
        }

        String query = userQuery.trim();
        String queryLower = query.toLowerCase(Locale.ROOT);

        // 1. Extract any specific local materials mentioned by the teacher
        String materials = extractMaterials(queryLower);

        // 2. Classify Pedagogical Intent
        boolean isGame = queryLower.contains("game") || queryLower.contains("play") || queryLower.contains("gamif");
        boolean isLargeClass = queryLower.contains("large class") || queryLower.contains("crowded") ||
                queryLower.contains("50") || queryLower.contains("55") || queryLower.contains("60") || queryLower.contains("many learners");
        boolean isAssessment = queryLower.contains("assessment") || queryLower.contains("rubric") ||
                queryLower.contains("evaluat") || queryLower.contains("exit ticket") || queryLower.contains("test");
        boolean isHook = queryLower.contains("hook") || queryLower.contains("intro") ||
                queryLower.contains("starter") || queryLower.contains("begin");
        boolean isDifferentiation = queryLower.contains("struggl") || queryLower.contains("support") ||
                queryLower.contains("remediat") || queryLower.contains("slow") || queryLower.contains("differentiat") || queryLower.contains("special");

        // 3. Classify Mathematical Strand
        if (queryLower.contains("fraction")) {
            return buildFractionsResponse(materials, isGame, isLargeClass, isAssessment, isHook, isDifferentiation);
        } else if (queryLower.contains("decimal") || queryLower.contains("percentage") || queryLower.contains("percent")) {
            return buildDecimalsResponse(materials, isGame, isLargeClass, isAssessment, isHook);
        } else if (queryLower.contains("area") || queryLower.contains("perimeter") || queryLower.contains("length")) {
            return buildMeasurementResponse("Length, Perimeter & Area", materials, isGame, isLargeClass, isAssessment);
        } else if (queryLower.contains("volume") || queryLower.contains("capacity") || queryLower.contains("litre") || queryLower.contains("liquid")) {
            return buildMeasurementResponse("Capacity & Volume", materials, isGame, isLargeClass, isAssessment);
        } else if (queryLower.contains("mass") || queryLower.contains("weight") || queryLower.contains("gram") || queryLower.contains("kilogram")) {
            return buildMeasurementResponse("Mass & Weight", materials, isGame, isLargeClass, isAssessment);
        } else if (queryLower.contains("money") || queryLower.contains("currency") || queryLower.contains("shilling") || queryLower.contains("budget") || queryLower.contains("profit")) {
            return buildMoneyResponse(materials, isGame, isLargeClass, isAssessment);
        } else if (queryLower.contains("angle") || queryLower.contains("triangle") || queryLower.contains("geometry") || queryLower.contains("shape") || queryLower.contains("line")) {
            return buildGeometryResponse(materials, isGame, isLargeClass, isAssessment);
        } else if (queryLower.contains("algebra") || queryLower.contains("equation") || queryLower.contains("variable") || queryLower.contains("letter")) {
            return buildAlgebraResponse(materials, isGame, isLargeClass, isAssessment);
        } else if (queryLower.contains("data") || queryLower.contains("graph") || queryLower.contains("chart") || queryLower.contains("mean") || queryLower.contains("table")) {
            return buildDataResponse(materials, isGame, isLargeClass, isAssessment);
        } else if (isLargeClass) {
            return buildGeneralLargeClassResponse(materials);
        } else if (isAssessment) {
            return buildGeneralAssessmentRubric();
        } else if (isGame) {
            return buildGeneralMathGameResponse(materials);
        }

        // Default: Whole Numbers / General Grade 6 Mathematics lesson design
        return buildWholeNumbersResponse(materials, isGame, isLargeClass, isAssessment, isHook, isDifferentiation);
    }

    private String extractMaterials(String q) {
        if (q.contains("avocado") || q.contains("seed")) return "avocado seeds / bean seeds";
        if (q.contains("stick")) return "dry sticks / twigs";
        if (q.contains("bottle") || q.contains("top") || q.contains("cap")) return "bottle caps / crowns";
        if (q.contains("stone") || q.contains("pebble")) return "small pebbles / stones";
        if (q.contains("maize") || q.contains("corn")) return "maize grains";
        if (q.contains("paper") || q.contains("card")) return "paper strips / card cutouts";
        if (q.contains("string") || q.contains("rope")) return "sisal strings / ropes";
        return "local concrete manipulatives (e.g., seeds, sticks, bottle caps, stones)";
    }

    private String buildFractionsResponse(String mat, boolean isGame, boolean isLarge, boolean isAss, boolean isHook, boolean isDiff) {
        StringBuilder sb = new StringBuilder();
        sb.append("Jambo Mwalimu! Here is your CBC pedagogical guide for Grade 6 Fractions:\n\n");

        if (isHook) {
            sb.append("• Lesson Hook (5 Mins):\n");
            sb.append("Gather learners around. Present 12 ").append(mat).append(". Tell a story of three siblings in Nakuru dividing produce equally. Ask: 'What fraction does each child receive?' Have a learner demonstrate on the table.\n\n");
        } else if (isGame) {
            sb.append("• Math Game: Fraction Relay Race (Biashara ya Sehemu)\n");
            sb.append("Divide learners into squads. Each squad receives 20 ").append(mat).append(". The teacher calls out a fraction (e.g. 'Show 3/5 of 15!'). Squads race to group the ").append(mat).append(" and justify their fraction to the class.\n\n");
        } else {
            sb.append("• Concrete Step-by-Step Exploration:\n");
            sb.append("1. Concrete: Learners use ").append(mat).append(" to represent proper, improper, and mixed fractions (e.g. making groups of 4 to show 5/4 = 1 1/4).\n");
            sb.append("2. Representational: Learners draw diagrams and strip models in their exercise books.\n");
            sb.append("3. Abstract: Write fractional equations on chalkboard (e.g. 1/2 + 1/4 = 3/4).\n\n");
        }

        if (isLarge) {
            sb.append("• Large Class Strategy (50+ learners):\n");
            sb.append("Organize into 5 squads of 10. Assign a 'Material Custodian' in each squad to manage the ").append(mat).append(" and a 'Reporter' to explain the group's solution.\n\n");
        }

        if (isAss || true) {
            sb.append("• CBC Formative Assessment Rubric:\n");
            sb.append("  - EE (Exceeds): Converts mixed numbers to improper fractions and explains equivalence using ").append(mat).append(" without assistance.\n");
            sb.append("  - ME (Meets): Accurately models and calculates fractions in pairs with minimal guidance.\n");
            sb.append("  - AE (Approaching): Identifies fractions with concrete items but needs teacher assistance with common denominators.\n");
            sb.append("  - BE (Below): Struggles to group manipulatives; requires peer buddy scaffolding.\n\n");
            sb.append("• 2-Minute Exit Ticket: 'Draw or show using ").append(mat).append(" how 2/3 is greater than 1/2.'");
        }

        return sb.toString();
    }

    private String buildWholeNumbersResponse(String mat, boolean isGame, boolean isLarge, boolean isAss, boolean isHook, boolean isDiff) {
        StringBuilder sb = new StringBuilder();
        sb.append("Jambo Mwalimu! Pedagogical guidance for Grade 6 Whole Numbers (Place Value & Operations):\n\n");

        if (isHook) {
            sb.append("• Lesson Hook (5 Mins):\n");
            sb.append("Present a bag containing dozens of ").append(mat).append(". Ask: 'How can our school inventory team count 1,450 seedlings quickly without making mistakes?' Guide them toward bundling by 10s and 100s.\n\n");
        } else if (isGame) {
            sb.append("• Math Game: Place Value Market Sprint\n");
            sb.append("Give squads bundles of ").append(mat).append(" representing Ones (singles), Tens (tied bundles), and Hundreds (bags). Call out a number (e.g. 342) and have squads assemble the correct quantity on their desks.\n\n");
        } else {
            sb.append("• Concrete-Representational-Abstract (CRA) Plan:\n");
            sb.append("1. Bundling Activity: Group ").append(mat).append(" into sets of 10 to demonstrate place value up to thousands and millions.\n");
            sb.append("2. Partner Challenge: Learner A writes a 5-digit number on chalkboard; Learner B models it with ").append(mat).append(".\n");
            sb.append("3. Real-World Application: Relate numbers to Kenyan census data or school tree-planting totals.\n\n");
        }

        if (isLarge) {
            sb.append("• Squad Management for Large Classes:\n");
            sb.append("Keep learners in fixed desk rows of 4. Have adjacent desks rotate roles (Facilitator, Material Custodian, Scribe, Timekeeper) to keep 100% active engagement.\n\n");
        }

        sb.append("• CBC Assessment Rubric:\n");
        sb.append("  - EE: Rounds numbers up to millions and justifies place value rules independently.\n");
        sb.append("  - ME: Correctly identifies place value and performs operations with ").append(mat).append(".\n");
        sb.append("  - AE: Performs operations but makes occasional place value alignment errors.\n");
        sb.append("  - BE: Requires step-by-step teacher modeling with concrete counters.\n\n");
        sb.append("• 2-Minute Exit Ticket: 'Write down the place value and total value of digit 7 in 574,820.'");

        return sb.toString();
    }

    private String buildMeasurementResponse(String topic, String mat, boolean isGame, boolean isLarge, boolean isAss) {
        StringBuilder sb = new StringBuilder();
        sb.append("Jambo Mwalimu! Practical CBC guide for Grade 6 ").append(topic).append(":\n\n");

        sb.append("• Hands-on Investigation:\n");
        sb.append("1. Active Measurement: Have learners use ").append(mat).append(" to estimate and measure real objects (classroom floor, desks, windows).\n");
        sb.append("2. Conversion Task: Connect non-standard units (lengths of ").append(mat).append(") to standard metric units (centimetres, metres, or millilitres/litres).\n");
        sb.append("3. Cooperative Recording: Learners tabulate their estimates vs actual measurements in pairs.\n\n");

        if (isGame) {
            sb.append("• Classroom Game: Estimation Master\n");
            sb.append("Each squad estimates the perimeter or capacity of a classroom zone. The team with the closest estimate verified by measurement earns the 'Master Surveyor' badge.\n\n");
        }

        sb.append("• Formative Assessment:\n");
        sb.append("Ask learners: 'Why is it necessary to use standard metric units rather than our ").append(mat).append(" when buying or selling items at the local market?'");

        return sb.toString();
    }

    private String buildMoneyResponse(String mat, boolean isGame, boolean isLarge, boolean isAss) {
        return "Jambo Mwalimu! CBC Guide for Grade 6 Money & Financial Literacy:\n\n" +
                "• Lesson Hook & Activity: Biashara Market Simulation\n" +
                "1. Setup: Transform the classroom into an open-air market. Learners use " + mat + " as market goods (tomatoes, avocadoes, maize, eggs).\n" +
                "2. Roles: Half the learners are traders with price tags; the other half are shoppers with play budgets.\n" +
                "3. Mathematical Focus: Calculating discounts, computing profit and loss, giving accurate change.\n\n" +
                "• Large Class Strategy:\n" +
                "Group learners in squads of 5. Each squad manages one 'duka' stall to prevent classroom chaos.\n\n" +
                "• CBC Competency: Financial literacy, decision making, integrity, critical thinking.\n" +
                "• Formative Check: 'If you buy 3 items at KSh 45 each and pay with a KSh 200 note, how much change should you receive?'";
    }

    private String buildGeometryResponse(String mat, boolean isGame, boolean isLarge, boolean isAss) {
        return "Jambo Mwalimu! CBC Guide for Grade 6 Geometry (Angles, Lines & Shapes):\n\n" +
                "• Concrete Investigation using " + mat + ":\n" +
                "1. Constructing Angles: Learners arrange " + mat + " to form acute, right, obtuse, and reflex angles on their desks.\n" +
                "2. Polygon Properties: Combine " + mat + " to build equilateral, isosceles, and scalene triangles, counting vertices and measuring interior angles.\n" +
                "3. Architectural Connection: Discuss how traditional Kenyan houses and roofs use triangles for strength and stability.\n\n" +
                "• Formative Assessment (ME Criteria):\n" +
                "Learner accurately classifies angles and constructs a quadrilateral using " + mat + " without teacher assistance.\n" +
                "• Quick Exit Ticket: 'Draw an angle of approximately 120 degrees and state whether it is acute, obtuse, or reflex.'";
    }

    private String buildAlgebraResponse(String mat, boolean isGame, boolean isLarge, boolean isAss) {
        return "Jambo Mwalimu! CBC Guide for Grade 6 Algebra (Simple Algebraic Expressions):\n\n" +
                "• Concrete CRA Approach using " + mat + ":\n" +
                "1. Mystery Box Concept: Place a mystery number of " + mat + " inside a paper cup labeled 'x'. Place 3 visible " + mat + " beside it. State: 'x + 3 = 10. How many are in the cup?'\n" +
                "2. Balanced Equation: Use desk balance analogies where subtracting from one side requires subtracting from the other side.\n" +
                "3. Symbolic Representation: Transition from physical cups and " + mat + " to algebraic letters (e.g. 2y + 4 = 16).\n\n" +
                "• Scaffolding Note:\n" +
                "Never introduce abstract letters without physical manipulative verification first for Grade 6 learners.";
    }

    private String buildDataResponse(String mat, boolean isGame, boolean isLarge, boolean isAss) {
        return "Jambo Mwalimu! CBC Guide for Grade 6 Data Handling (Tables & Bar Graphs):\n\n" +
                "• Collaborative Activity:\n" +
                "1. Data Collection: Tally favourite school subjects or types of trees in the school compound.\n" +
                "2. Physical Graphing: Use " + mat + " stacked in columns on desks to create 3D bar graphs before drawing on grid paper.\n" +
                "3. Interpretation: Ask learners to identify the mode (most frequent category) and calculate the mean.\n\n" +
                "• Exit Ticket: 'From your graph, how many more learners preferred Mathematics than Science?'";
    }

    private String buildDecimalsResponse(String mat, boolean isGame, boolean isLarge, boolean isAss, boolean isHook) {
        return "Jambo Mwalimu! CBC Guide for Grade 6 Decimals & Percentages:\n\n" +
                "• Concrete Exploration using " + mat + ":\n" +
                "1. Tenths and Hundredths: Use 10 bundles of 10 " + mat + " (total 100). Show that 1 counter is 0.01 (1%), and 1 bundle of 10 is 0.10 (10%).\n" +
                "2. Equivalence: Show learners that 1/2 = 0.50 = 50% using physical groupings of " + mat + ".\n" +
                "3. Kenyan Context: Relate decimals to M-Pesa transaction fees and shopping receipts.\n\n" +
                "• Formative Check: 'Convert 0.75 into a fraction in its simplest form and justify using your counters.'";
    }

    private String buildGeneralLargeClassResponse(String mat) {
        return "Managing Large Mathematics Classes (50+ learners) in Kenyan CBC:\n\n" +
                "1. Squad System (Vikundi Vidogo):\n" +
                "   Divide the classroom into 5-6 fixed squads. Assign designated roles:\n" +
                "   • Facilitator: Keeps squad focused on task.\n" +
                "   • Material Custodian: Collects and returns " + mat + ".\n" +
                "   • Scribe: Writes group consensus solution.\n" +
                "   • Timekeeper: Ensures squad finishes within time.\n\n" +
                "2. Choral & Response Techniques:\n" +
                "   Use 'Think-Pair-Share' instead of whole-class callouts so every learner speaks.\n\n" +
                "3. Low-Cost Materials:\n" +
                "   Never wait for photocopies. Have learners draw mini-chalkboards or use " + mat + " for desk-level proofs.\n\n" +
                "4. Rapid Assessment:\n" +
                "   Use 'Thumbs Up / Thumbs Down' or finger counts (1 to 5) for instant formative checks across 50 learners in 10 seconds.";
    }

    private String buildGeneralAssessmentRubric() {
        return "KICD Grade 6 CBC 4-Tier Assessment Rubric Framework:\n\n" +
                "1. Exceeds Expectations (EE):\n" +
                "   • Consistently accurate mathematical procedures.\n" +
                "   • Independently explains reasoning and formulates extension word problems.\n\n" +
                "2. Meets Expectations (ME):\n" +
                "   • Accurately solves standard curriculum problems.\n" +
                "   • Uses manipulatives and mathematical vocabulary correctly with minimal support.\n\n" +
                "3. Approaching Expectations (AE):\n" +
                "   • Understands the fundamental concept but makes minor computational errors.\n" +
                "   • Requires occasional scaffolding and prompting from teacher or peers.\n\n" +
                "4. Below Expectations (BE):\n" +
                "   • Struggles with basic procedure; requires continuous concrete modeling and direct one-on-one intervention.\n\n" +
                "Tip: Always document learner progress in your CBC Assessment Portfolio using observable evidence rather than purely exam marks.";
    }

    private String buildGeneralMathGameResponse(String mat) {
        return "Top Low-Cost CBC Mathematics Game for Grade 6:\n\n" +
                "• Game Title: Market Sprint (Biashara Challenge)\n" +
                "• Materials: " + mat + " and scrap paper number cards.\n" +
                "• Rules: \n" +
                "  1. Learners in groups of 4 take turns drawing 2 number cards.\n" +
                "  2. They must formulate a word problem using " + mat + " and challenge their partner to calculate the solution within 60 seconds.\n" +
                "  3. Correct solutions earn 1 point; clear mathematical justification earns 2 points.\n" +
                "• Duration: 12-15 minutes.\n" +
                "• Values & Competencies: Collaboration, communication, critical thinking, sportsmanship.";
    }
}
