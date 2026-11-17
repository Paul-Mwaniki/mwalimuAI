package ke.co.mwalimuai.generator;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Random;

/**
 * Procedural Generative Math, Coherent Scenario & Physical Material Synthesizer
 * for KICD Grade 6 Mathematics (Pages 13-70).
 *
 * Key Capabilities:
 * 1. Single Coherent Storyline: Title, Hook, Worked Example, Stage 1, Stage 2, and Exit Ticket
 *    always stay within the exact same real-world Kenyan story.
 * 2. Physical Material Semantics: Understands what the teacher's custom materials physically are
 *    (e.g., strings/ropes = stretched for diagonals, boundaries, and circumference;
 *    boxes/cartons = unfolded into rectangular sheets or 3D nets and cut along diagonals;
 *    sticks/twigs = joined at vertices for angles and polygons;
 *    seeds/stones/bottle tops = bundled in 10s/100s or arranged in arrays)
 *    and explains step-by-step HOW to use them.
 * 3. Real-Time Symbolic Math Solver: Dynamically generates fresh numbers and solves every step.
 */
public class ProceduralMathSynthesizer {

    public static class SynthesizedPedagogy {
        public final String scenarioContext;
        public final String workedExample;
        public final String concreteMaterialHowTo;
        public final String representationalTask;
        public final String guidedPracticeProblem;
        public final String exitTicketQuestion;
        public final String exitTicketSolution;

        public SynthesizedPedagogy(
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

    private static final String[] KENYAN_COUNTIES = {
            "Nakuru", "Kisumu", "Machakos", "Uasin Gishu", "Nyeri", "Mombasa", "Kakamega",
            "Kiambu", "Meru", "Kisii", "Kilifi", "Kericho", "Embu", "Bungoma", "Trans Nzoia",
            "Laikipia", "Kajiado", "Narok", "Kitui", "Makueni", "Murang'a", "Kirinyaga",
            "Nandi", "Bomet", "Homa Bay", "Migori", "Siaya", "Busia", "Vihiga", "Taita Taveta",
            "Kwale", "Tharaka-Nithi", "Nyandarua", "Elgeyo-Marakwet", "Baringo", "Nyamira", "Garissa"
    };

    private static final Random RNG = new Random();

    private static String pickCounty() {
        return KENYAN_COUNTIES[RNG.nextInt(KENYAN_COUNTIES.length)];
    }

    private static String fmt(long n) {
        return NumberFormat.getNumberInstance(Locale.US).format(n);
    }

    private static int gcd(int a, int b) {
        a = Math.abs(a);
        b = Math.abs(b);
        while (b != 0) {
            int t = b;
            b = a % b;
            a = t;
        }
        return a == 0 ? 1 : a;
    }

    private static int lcm(int a, int b) {
        return (a / gcd(a, b)) * b;
    }

    // Physical material property detectors
    private static boolean hasBoxOrCardboard(String m) {
        String s = m.toLowerCase(Locale.ROOT);
        return s.contains("box") || s.contains("carton") || s.contains("cardboard") || s.contains("packet") || s.contains("packaging");
    }

    private static boolean hasStringOrRope(String m) {
        String s = m.toLowerCase(Locale.ROOT);
        return s.contains("string") || s.contains("rope") || s.contains("thread") || s.contains("sisal")
                || s.contains("yarn") || s.contains("wire") || s.contains("tape") || s.contains("ribbon") || s.contains("fibre") || s.contains("fiber");
    }

    private static boolean hasStickOrStraw(String m) {
        String s = m.toLowerCase(Locale.ROOT);
        return s.contains("stick") || s.contains("twig") || s.contains("straw") || s.contains("rod") || s.contains("toothpick") || s.contains("broom");
    }

    private static boolean hasPaperOrManila(String m) {
        String s = m.toLowerCase(Locale.ROOT);
        return s.contains("paper") || s.contains("manila") || s.contains("card") || s.contains("sheet") || s.contains("cutout") || s.contains("chart");
    }

    private static class MaterialStoryProfile {
        final String county;
        final String context;
        final String unitItem;
        final String containerName;

        MaterialStoryProfile(String county, String context, String unitItem, String containerName) {
            this.county = county;
            this.context = context;
            this.unitItem = unitItem;
            this.containerName = containerName;
        }
    }

    /**
     * Builds a real-world Kenyan scenario directly connected to the teacher's exact materials in use
     * so there is NEVER a disconnect between the materials and the scenario story.
     */
    private static MaterialStoryProfile buildMaterialStory(String mat) {
        String county = pickCounty();
        String m = (mat != null && !mat.trim().isEmpty()) ? mat.trim() : "counters and number cards";
        String lower = m.toLowerCase(Locale.ROOT);

        if (hasBoxOrCardboard(m) && hasStringOrRope(m)) {
            return new MaterialStoryProfile(
                    county,
                    county + " Packaging & String-Binding Box Workshop (Using " + capitalize(m) + ")",
                    "metres of binding string packed in boxes",
                    "cardboard storage boxes"
            );
        } else if (hasBoxOrCardboard(m)) {
            return new MaterialStoryProfile(
                    county,
                    county + " Carton & Box Packaging Store (Using " + capitalize(m) + ")",
                    "packaged box units",
                    "cardboard cartons"
            );
        } else if (hasStringOrRope(m)) {
            return new MaterialStoryProfile(
                    county,
                    county + " Sisal & String Weaving Cooperative (Using " + capitalize(m) + ")",
                    "metres of woven string",
                    "string spools"
            );
        } else if (hasStickOrStraw(m)) {
            return new MaterialStoryProfile(
                    county,
                    county + " Woodcraft & Counting-Stick Workshop (Using " + capitalize(m) + ")",
                    "bundled counting sticks",
                    "stick bundles"
            );
        } else if (lower.contains("top") || lower.contains("cap") || lower.contains("crown")) {
            return new MaterialStoryProfile(
                    county,
                    county + " Recycling & Bottle-Top Sorting Centre (Using " + capitalize(m) + ")",
                    "sorted bottle tops",
                    "sorting trays"
            );
        } else if (lower.contains("stone") || lower.contains("pebble")) {
            return new MaterialStoryProfile(
                    county,
                    county + " Construction & Graded Stone Tallying Yard (Using " + capitalize(m) + ")",
                    "graded counting stones",
                    "stone piles"
            );
        } else if (lower.contains("seed") || lower.contains("bean") || lower.contains("maize") || lower.contains("grain")) {
            return new MaterialStoryProfile(
                    county,
                    county + " Agricultural Seed Sorting Centre (Using " + capitalize(m) + ")",
                    m,
                    "sorting baskets"
            );
        } else if (hasPaperOrManila(m)) {
            return new MaterialStoryProfile(
                    county,
                    county + " School Printing & Number-Card Centre (Using " + capitalize(m) + ")",
                    "printed number cards and sheets",
                    "card packets"
            );
        } else {
            return new MaterialStoryProfile(
                    county,
                    county + " Classroom Practical Investigation (Using " + capitalize(m) + ")",
                    m,
                    "classroom squad sets"
            );
        }
    }

    /**
     * Dynamically synthesizes a unique, mathematically verified, and physically coherent lesson scenario
     * and problem set for any KICD Grade 6 sub-strand (1.1 through 5.1).
     */
    public static SynthesizedPedagogy synthesize(String subStrandCode, String subStrand, String mat) {
        String code = subStrandCode != null ? subStrandCode.trim() : "1.1";
        String material = (mat != null && !mat.trim().isEmpty()) ? mat.trim() : "counters and number cards";

        switch (code) {
            case "1.1":
                return synthesizeWholeNumbers(material);
            case "1.2":
                return synthesizeMultiplication(material);
            case "1.3":
                return synthesizeDivision(material);
            case "1.4":
                return synthesizeFractions(material);
            case "1.5":
                return synthesizeDecimals(material);
            case "2.1":
                return synthesizeLength(material);
            case "2.2":
                return synthesizeArea(material);
            case "2.3":
                return synthesizeCapacity(material);
            case "2.4":
                return synthesizeMass(material);
            case "2.5":
                return synthesizeTime(material);
            case "2.6":
                return synthesizeMoney(material);
            case "3.1":
                return synthesizeLines(material);
            case "3.2":
                return synthesizeAngles(material);
            case "3.3":
                return synthesize3DObjects(material);
            case "4.1":
                return synthesizeDataHandling(material);
            case "5.1":
                return synthesizeAlgebraInequalities(material);
            default:
                return synthesizeWholeNumbers(material);
        }
    }

    // =========================================================================================
    // 1.1 WHOLE NUMBERS (Place value up to millions, rounding, squares & square roots)
    // =========================================================================================
    private static SynthesizedPedagogy synthesizeWholeNumbers(String mat) {
        MaterialStoryProfile story = buildMaterialStory(mat);
        String context = story.context;
        String unitItem = story.unitItem;

        int ht = 2 + RNG.nextInt(7);
        int tth = 1 + RNG.nextInt(8);
        if (tth == ht) tth = (tth % 8) + 1;
        int th = 1 + RNG.nextInt(8);
        int h = 1 + RNG.nextInt(8);
        int t = 1 + RNG.nextInt(8);
        int o = 1 + RNG.nextInt(8);
        int num1 = ht * 100000 + tth * 10000 + th * 1000 + h * 100 + t * 10 + o;
        int totalValTth = tth * 10000;
        int roundedNearestTenTh = Math.round(num1 / 10000.0f) * 10000;

        int sqBase = 12 + RNG.nextInt(14);
        int sqVal = sqBase * sqBase;

        int gHt = 3 + RNG.nextInt(6);
        int gTth = 2 + RNG.nextInt(7);
        int gTh = 1 + RNG.nextInt(8);
        int gNum = gHt * 100000 + gTth * 10000 + gTh * 1000 + (2 + RNG.nextInt(7)) * 100 + 45;
        int gRoundedTh = Math.round(gNum / 1000.0f) * 1000;

        int eHt = 4 + RNG.nextInt(5);
        int eTth = 2 + RNG.nextInt(7);
        int eTh = 5 + RNG.nextInt(4);
        int eH = 1 + RNG.nextInt(7);
        int eNum = eHt * 100000 + eTth * 10000 + eTh * 1000 + eH * 100 + 36;
        int eTotalVal = eTth * 10000;
        int eRoundedTenTh = Math.round(eNum / 10000.0f) * 10000;
        int rootBase = 13 + RNG.nextInt(13);
        int rootSquare = rootBase * rootBase;

        String howTo;
        if (hasBoxOrCardboard(mat) && hasStringOrRope(mat)) {
            howTo = "1. Squads lay out 6 labelled boxes/containers (Hundred Thousands down to Ones) using " + mat + " and stretch strings between them to form a 6-column place value abacus.\n" +
                    "   2. Learners place markers into each box to model " + fmt(num1) + " (" + ht + " in HT, " + tth + " in TTh, " + th + " in Th, " + h + " in H, " + t + " in T, " + o + " in O).\n" +
                    "   3. Squads stretch a string across their desk as a number line from " + fmt((num1 / 10000) * 10000) + " to " + fmt(((num1 / 10000) + 1) * 10000) + " to visually prove why " + fmt(num1) + " rounds to " + fmt(roundedNearestTenTh) + ".";
        } else if (hasStringOrRope(mat)) {
            howTo = "1. Squads use " + mat + " to lay out 6 vertical place-value columns on their desks (Hundred Thousands to Ones) for " + fmt(num1) + ".\n" +
                    "   2. Learners tie knots or place markers along the string in each column (" + tth + " in the Ten Thousands column = " + fmt(totalValTth) + ").\n" +
                    "   3. Squads stretch a straight piece of " + mat + " as a number line to locate " + fmt(num1) + " and round it to " + fmt(roundedNearestTenTh) + ".";
        } else {
            howTo = "1. Each squad draws a 6-column place value chart (Hundred Thousands to Ones) and places " + mat + " in each column to model " + fmt(num1) + " " + unitItem + " (" + ht + " in HT, " + tth + " in TTh, " + th + " in Th, " + h + " in H, " + t + " in T, " + o + " in O).\n" +
                    "   2. Learners isolate the " + tth + " items in the Ten Thousands column to prove its total value is " + tth + " × 10,000 = " + fmt(totalValTth) + ".\n" +
                    "   3. Learners arrange " + mat + " into a square grid of " + sqBase + " rows by " + sqBase + " columns to model " + sqBase + "² = " + fmt(sqVal) + ".";
        }

        return new SynthesizedPedagogy(
                context,
                "At the " + context + ", " + fmt(num1) + " " + unitItem + " were recorded. " +
                        "(a) State the place value and total value of digit " + tth + " in " + fmt(num1) + ". " +
                        "(b) Round " + fmt(num1) + " to the nearest ten thousand, and calculate " + sqBase + "².",
                howTo,
                "Learners copy the 6-column place value table for the " + context + " into their exercise books, write the total value of digit " + tth + " as " + fmt(totalValTth) +
                        ", round " + fmt(num1) + " to " + fmt(roundedNearestTenTh) + ", and compute " + sqBase + "² = " + sqBase + " × " + sqBase + " = " + fmt(sqVal) + ".",
                "In a second record from the " + context + ", " + fmt(gNum) + " " + unitItem + " were counted. Using " + mat + ", state the total value of digit " + gTth + " (" + fmt(gTth * 10000) + ") and round " + fmt(gNum) + " to the nearest thousand (" + fmt(gRoundedTh) + ").",
                "From the " + context + " records: (a) In " + fmt(eNum) + " " + unitItem + ", state the place value and total value of digit " + eTth + ", and round " + fmt(eNum) + " to the nearest ten thousand. (b) Work out √" + fmt(rootSquare) + ".",
                "(a) Place value = Ten Thousands; Total value = " + fmt(eTotalVal) + "; Nearest ten thousand = " + fmt(eRoundedTenTh) + ". (b) √" + fmt(rootSquare) + " = " + rootBase + " (since " + rootBase + " × " + rootBase + " = " + fmt(rootSquare) + ")."
        );
    }

    // =========================================================================================
    // 1.2 MULTIPLICATION (4-digit by 2-digit partial products, estimation, patterns)
    // =========================================================================================
    private static SynthesizedPedagogy synthesizeMultiplication(String mat) {
        MaterialStoryProfile story = buildMaterialStory(mat);
        String context = story.context;
        String unitItem = story.unitItem;
        String containerName = story.containerName;

        int multiplicand = 1120 + RNG.nextInt(1750);
        int tens = (1 + RNG.nextInt(2)) * 10;
        int ones = 2 + RNG.nextInt(7);
        int multiplier = tens + ones;
        int partialTens = multiplicand * tens;
        int partialOnes = multiplicand * ones;
        int exactProduct = partialTens + partialOnes;

        int estMultiplicand = Math.round(multiplicand / 100.0f) * 100;
        int estMultiplier = Math.round(multiplier / 10.0f) * 10;
        int estProduct = estMultiplicand * estMultiplier;

        int gA = 1210 + RNG.nextInt(1400);
        int gTens = (1 + RNG.nextInt(2)) * 10;
        int gOnes = 3 + RNG.nextInt(6);
        int gB = gTens + gOnes;
        int gProd = gA * gB;

        int eA = 1150 + RNG.nextInt(1500);
        int eTens = (1 + RNG.nextInt(2)) * 10;
        int eOnes = 2 + RNG.nextInt(7);
        int eB = eTens + eOnes;
        int ePart1 = eA * eTens;
        int ePart2 = eA * eOnes;
        int eProd = ePart1 + ePart2;

        String howTo;
        if (hasBoxOrCardboard(mat)) {
            howTo = "1. Squads use " + mat + " to draw or build a 2-section area-model box representing the multiplier " + multiplier + " split into (" + tens + " + " + ones + ").\n" +
                    "   2. In the first box section, learners calculate " + fmt(multiplicand) + " × " + tens + " = " + fmt(partialTens) + " " + unitItem + ".\n" +
                    "   3. In the second box section, learners calculate " + fmt(multiplicand) + " × " + ones + " = " + fmt(partialOnes) + " " + unitItem + ", then combine both sections.";
        } else {
            howTo = "1. Squads use " + mat + " to partition the 2-digit number of " + containerName + " (" + multiplier + ") into (" + tens + " tens + " + ones + " ones).\n" +
                    "   2. Learners model the first partial product: " + fmt(multiplicand) + " × " + tens + " = " + fmt(partialTens) + " " + unitItem + " using grouped bundles of " + mat + ".\n" +
                    "   3. Learners model the second partial product: " + fmt(multiplicand) + " × " + ones + " = " + fmt(partialOnes) + " " + unitItem + ", and combine both groups.";
        }

        return new SynthesizedPedagogy(
                context,
                "In the " + context + ", each of the " + multiplier + " " + containerName + " holds " + fmt(multiplicand) + " " + unitItem +
                        ". Estimate the total by rounding off, then calculate the exact number of " + unitItem + ".",
                howTo,
                "Learners write the vertical partial-product multiplication for the " + context + " in their books: " + fmt(partialTens) + " + " + fmt(partialOnes) + " = " + fmt(exactProduct) +
                        " " + unitItem + ", and compare it with the rounded estimate (" + fmt(estMultiplicand) + " × " + estMultiplier + " = " + fmt(estProduct) + ").",
                "A second lorry in the " + context + " carries " + gB + " " + containerName + " with " + fmt(gA) + " " + unitItem + " in each. Use " + mat + " to compute the total (" + fmt(gA * gTens) + " + " + fmt(gA * gOnes) + " = " + fmt(gProd) + " " + unitItem + ").",
                "For the " + context + ": (a) Work out " + fmt(eA) + " " + unitItem + " × " + eB + " " + containerName + " showing both partial products. (b) Estimate the answer by rounding " + fmt(eA) + " to the nearest hundred and " + eB + " to the nearest ten.",
                "(a) (" + fmt(eA) + " × " + eTens + ") + (" + fmt(eA) + " × " + eOnes + ") = " + fmt(ePart1) + " + " + fmt(ePart2) + " = " + fmt(eProd) + " " + unitItem + ". (b) Estimate: " + fmt(Math.round(eA / 100.0f) * 100) + " × " + (Math.round(eB / 10.0f) * 10) + " = " + fmt((long) (Math.round(eA / 100.0f) * 100) * (Math.round(eB / 10.0f) * 10)) + "."
        );
    }

    // =========================================================================================
    // 1.3 DIVISION (4-digit by 2-digit exact long division + BODMAS combined operations)
    // =========================================================================================
    private static SynthesizedPedagogy synthesizeDivision(String mat) {
        MaterialStoryProfile story = buildMaterialStory(mat);
        String context = story.context;
        String unitItem = story.unitItem;
        String groupName = story.containerName;

        int divisor = 12 + RNG.nextInt(14);
        int quotient = 115 + RNG.nextInt(155);
        int dividend = divisor * quotient;
        int hundredsPart = (quotient / 100) * 100;
        int remAfterHundreds = dividend - (divisor * hundredsPart);

        int gDivisor = 12 + RNG.nextInt(13);
        int gQuotient = 110 + RNG.nextInt(140);
        int gDividend = gDivisor * gQuotient;

        int eDivisor = 11 + RNG.nextInt(14);
        int eQuotient = 108 + RNG.nextInt(130);
        int eDividend = eDivisor * eQuotient;

        int bodB = 3 + RNG.nextInt(5);
        int bodQuot = 3 + RNG.nextInt(5);
        int bodA = bodB * bodQuot;
        int bodMult = 2 + RNG.nextInt(4);
        int bodProd = bodQuot * bodMult;
        int bodStart = bodProd + 15 + RNG.nextInt(25);
        int bodAns = bodStart - bodProd;

        String howTo = "1. Squads use " + mat + " to represent " + fmt(dividend) + " " + unitItem + " being shared equally among " + divisor + " " + groupName + ".\n" +
                "   2. First, squads allocate " + hundredsPart + " " + unitItem + " to each of the " + divisor + " " + groupName + " (using " + fmt(divisor * hundredsPart) + " " + unitItem + ").\n" +
                "   3. Next, squads divide the remaining " + fmt(remAfterHundreds) + " " + unitItem + " equally among the " + divisor + " " + groupName + " (" + (quotient - hundredsPart) + " each) to reach " + quotient + " " + unitItem + " per group.";

        return new SynthesizedPedagogy(
                context,
                "In the " + context + ", " + fmt(dividend) + " " + unitItem + " are shared equally among " + divisor + " " + groupName + ". How many " + unitItem + " does each receive?",
                howTo,
                "Learners write the long division steps (Divide, Multiply, Subtract, Bring down) in their books showing " + fmt(dividend) + " ÷ " + divisor + " = " + quotient + " " + unitItem +
                        ", and verify by multiplying " + quotient + " × " + divisor + " = " + fmt(dividend) + ".",
                "In a second batch of the " + context + ", divide " + fmt(gDividend) + " " + unitItem + " equally among " + gDivisor + " " + groupName + " using " + mat + " (Answer: " + gQuotient + " " + unitItem + ").",
                "From the " + context + ": (a) Divide " + fmt(eDividend) + " " + unitItem + " equally among " + eDivisor + " " + groupName + ". (b) Work out the combined BODMAS operation: " + bodStart + " - " + bodA + " ÷ " + bodB + " × " + bodMult + ".",
                "(a) " + fmt(eDividend) + " ÷ " + eDivisor + " = " + eQuotient + " " + unitItem + ". (b) Division first: " + bodA + " ÷ " + bodB + " = " + bodQuot +
                        "; Multiplication next: " + bodQuot + " × " + bodMult + " = " + bodProd + "; Subtraction last: " + bodStart + " - " + bodProd + " = " + bodAns + "."
        );
    }

    // =========================================================================================
    // 1.4 FRACTIONS (LCM addition/subtraction, mixed numbers, squares, reciprocals, percentages)
    // =========================================================================================
    private static SynthesizedPedagogy synthesizeFractions(String mat) {
        MaterialStoryProfile story = buildMaterialStory(mat);
        String context = story.context;
        String unitItem = story.unitItem;
        String containerName = story.containerName;

        int[][] denomPairs = {{2, 3}, {3, 4}, {2, 5}, {3, 5}, {4, 5}};
        int[] pair = denomPairs[RNG.nextInt(denomPairs.length)];
        int d1 = pair[0];
        int d2 = pair[1];
        int n1 = 1;
        int n2 = 1 + RNG.nextInt(d2 - 1);
        int commonDenom = lcm(d1, d2);
        int eq1 = n1 * (commonDenom / d1);
        int eq2 = n2 * (commonDenom / d2);
        int sumNum = eq1 + eq2;

        int[] pctDenoms = {4, 5, 10, 20, 25};
        int pDen = pctDenoms[RNG.nextInt(pctDenoms.length)];
        int pNum = 1 + RNG.nextInt(pDen - 1);
        int pctVal = (pNum * 100) / pDen;

        int[] ePair = denomPairs[RNG.nextInt(denomPairs.length)];
        int ed1 = ePair[0];
        int ed2 = ePair[1];
        int eLcm = lcm(ed1, ed2);
        int en1 = ed1 - 1;
        int en2 = 1;
        int eEq1 = en1 * (eLcm / ed1);
        int eEq2 = en2 * (eLcm / ed2);
        int eDiff = eEq1 - eEq2;

        int sqN = 2 + RNG.nextInt(3);
        int sqD = sqN + 1 + RNG.nextInt(3);

        String howTo;
        if (hasStringOrRope(mat) || hasStickOrStraw(mat)) {
            howTo = "1. Squads stretch a piece of " + mat + " across their desk and mark it into " + commonDenom + " equal segments (since the LCM of " + d1 + " and " + d2 + " is " + commonDenom + ").\n" +
                    "   2. Learners measure " + n1 + "/" + d1 + " of the length as " + eq1 + "/" + commonDenom + " (first batch of " + containerName + ") and " + n2 + "/" + d2 + " of the length as " + eq2 + "/" + commonDenom + " (second batch).\n" +
                    "   3. Squads join the two measured sections along the " + mat + " to show the total fraction used is " + eq1 + "/" + commonDenom + " + " + eq2 + "/" + commonDenom + " = " + sumNum + "/" + commonDenom + ".";
        } else if (hasBoxOrCardboard(mat) || hasPaperOrManila(mat)) {
            howTo = "1. Squads flatten or divide a panel from their " + mat + " into " + commonDenom + " equal strips (LCM of " + d1 + " and " + d2 + " = " + commonDenom + ").\n" +
                    "   2. Learners mark " + eq1 + " strips for " + n1 + "/" + d1 + " and " + eq2 + " strips for " + n2 + "/" + d2 + ".\n" +
                    "   3. Counting the marked strips on the " + mat + " proves " + n1 + "/" + d1 + " + " + n2 + "/" + d2 + " = " + sumNum + "/" + commonDenom + ".";
        } else {
            howTo = "1. Squads count out " + commonDenom + " items of " + mat + " to represent 1 whole set (since the LCM of " + d1 + " and " + d2 + " is " + commonDenom + ").\n" +
                    "   2. Learners separate " + n1 + "/" + d1 + " of the " + commonDenom + " items (" + eq1 + " items) and " + n2 + "/" + d2 + " of the " + commonDenom + " items (" + eq2 + " items).\n" +
                    "   3. Combining both groups of " + mat + " gives " + eq1 + " + " + eq2 + " = " + sumNum + " out of " + commonDenom + " (" + sumNum + "/" + commonDenom + ").";
        }

        return new SynthesizedPedagogy(
                context,
                "In the " + context + ", learners used " + n1 + "/" + d1 + " of their " + unitItem + " for Group A and " + n2 + "/" + d2 +
                        " for Group B. Find the combined fraction used via the LCM, and convert " + pNum + "/" + pDen + " into a percentage.",
                howTo,
                "Learners draw a fraction bar of " + commonDenom + " equal parts for the " + context + " in their books, write " + eq1 + "/" + commonDenom + " + " + eq2 + "/" + commonDenom + " = " + sumNum + "/" + commonDenom +
                        ", and calculate " + pNum + "/" + pDen + " × 100% = " + pctVal + "%.",
                "In the " + context + ", work out 1 " + n1 + "/" + d1 + " + 2 " + n2 + "/" + d2 + " using the LCM " + commonDenom + " and " + mat + ", and state the reciprocal of " + sqN + "/" + sqD + " (" + sqD + "/" + sqN + ").",
                "For the " + context + ": (a) Work out " + en1 + "/" + ed1 + " - " + en2 + "/" + ed2 + " using the LCM. (b) Find (" + sqN + "/" + sqD + ")² and convert " + pNum + "/" + pDen + " to a percentage.",
                "(a) LCM of " + ed1 + " and " + ed2 + " is " + eLcm + ": " + eEq1 + "/" + eLcm + " - " + eEq2 + "/" + eLcm + " = " + eDiff + "/" + eLcm +
                        ". (b) (" + sqN + "/" + sqD + ")² = " + (sqN * sqN) + "/" + (sqD * sqD) + "; " + pNum + "/" + pDen + " = " + pctVal + "%."
        );
    }

    // =========================================================================================
    // 1.5 DECIMALS (Ten-thousandths place value, rounding up to 3 d.p., addition/subtraction)
    // =========================================================================================
    private static SynthesizedPedagogy synthesizeDecimals(String mat) {
        MaterialStoryProfile story = buildMaterialStory(mat);
        String context = story.context;
        String containerName = story.containerName;

        int w1 = 11 + RNG.nextInt(18);
        int t1 = 1 + RNG.nextInt(8);
        int h1 = 1 + RNG.nextInt(8);
        int th1 = 2 + RNG.nextInt(7);
        double dec1 = w1 + t1 * 0.1 + h1 * 0.01 + th1 * 0.001;

        int w2 = 5 + RNG.nextInt(12);
        int t2 = 1 + RNG.nextInt(8);
        int h2 = 1 + RNG.nextInt(8);
        double dec2 = w2 + t2 * 0.1 + h2 * 0.01;

        double sum = dec1 + dec2;
        String dec1Str = String.format(Locale.US, "%.3f", dec1);
        String dec2Str = String.format(Locale.US, "%.2f", dec2);
        String dec2Padded = String.format(Locale.US, "%.3f", dec2);
        String sumStr = String.format(Locale.US, "%.3f", sum);
        String sumRounded2Dp = String.format(Locale.US, "%.2f", sum);

        double eA = (14 + RNG.nextInt(15)) + (2 + RNG.nextInt(7)) * 0.1;
        double eB = (4 + RNG.nextInt(8)) + (1 + RNG.nextInt(8)) * 0.1 + (2 + RNG.nextInt(7)) * 0.01 + 5 * 0.001;
        double eDiff = eA - eB;

        String howTo = "1. Squads draw a 6-column decimal place-value chart (Tens, Ones, Decimal Point, Tenths, Hundredths, Thousandths) and use " + mat + " to model " + dec1Str + " and " + dec2Padded + ".\n" +
                "   2. Learners align the decimal points vertically and place a 0 placeholder in the Thousandths column of " + dec2Padded + ".\n" +
                "   3. Squads combine the " + mat + " column-by-column from Thousandths to Tens to obtain " + sumStr + ".";

        return new SynthesizedPedagogy(
                context,
                "At the " + context + ", two " + containerName + " measure " + dec1Str + " units and " + dec2Str + " units. State the place value of digit " + th1 + " in " + dec1Str + ", find the combined total, and round it to 2 decimal places.",
                howTo,
                "Learners write the vertically aligned addition for the " + context + " in their books: " + dec1Str + " + " + dec2Padded + " = " + sumStr + ", identify digit " + th1 + " as Thousandths, and round " + sumStr + " to " + sumRounded2Dp + ".",
                "At the " + context + ", subtract " + dec2Padded + " from the combined total " + sumStr + " using " + mat + " (" + sumStr + " - " + dec2Padded + " = " + dec1Str + ").",
                "For the " + context + ": (a) Work out " + String.format(Locale.US, "%.1f", eA) + " - " + String.format(Locale.US, "%.3f", eB) + ". (b) Round " + dec1Str + " to 2 decimal places.",
                "(a) Pad with zeros: " + String.format(Locale.US, "%.3f", eA) + " - " + String.format(Locale.US, "%.3f", eB) + " = " + String.format(Locale.US, "%.3f", eDiff) + ". (b) " + String.format(Locale.US, "%.2f", dec1) + "."
        );
    }

    // =========================================================================================
    // 2.1 LENGTH (Millimetres & centimetres conversions, operations, circumference/diameter)
    // =========================================================================================
    private static SynthesizedPedagogy synthesizeLength(String mat) {
        MaterialStoryProfile story = buildMaterialStory(mat);
        String context = story.context;

        int cm1 = 12 + RNG.nextInt(18);
        int mm1 = 5 + RNG.nextInt(5);
        int cm2 = 7 + RNG.nextInt(12);
        int mm2 = 4 + RNG.nextInt(6);
        int totalMm = mm1 + mm2;
        int carryCm = totalMm / 10;
        int remMm = totalMm % 10;
        int totalCm = cm1 + cm2 + carryCm;

        int multCm = 4 + RNG.nextInt(6);
        int multMm = 3 + RNG.nextInt(6);
        int factor = 3 + RNG.nextInt(4);
        int prodTotalMm = (multCm * 10 + multMm) * factor;
        int prodCm = prodTotalMm / 10;
        int prodMm = prodTotalMm % 10;

        String howTo;
        if (hasStringOrRope(mat) || hasBoxOrCardboard(mat)) {
            howTo = "1. Squads stretch " + mat + " along two edges measuring " + cm1 + " cm " + mm1 + " mm and " + cm2 + " cm " + mm2 + " mm and lay them end-to-end against a ruler.\n" +
                    "   2. Learners combine the millimetre portions (" + mm1 + " mm + " + mm2 + " mm = " + totalMm + " mm) and regroup 10 mm into 1 full cm, leaving " + remMm + " mm.\n" +
                    "   3. Squads also wrap a string from " + mat + " around a circular lid (Circumference) and across its centre (Diameter) to verify Circumference ÷ Diameter ≈ 3.14.";
        } else {
            howTo = "1. Squads use rulers and " + mat + " to measure two pieces of " + cm1 + " cm " + mm1 + " mm and " + cm2 + " cm " + mm2 + " mm.\n" +
                    "   2. Learners group every 10 mm (" + mm1 + " mm + " + mm2 + " mm = " + totalMm + " mm) into " + carryCm + " cm and " + remMm + " mm.\n" +
                    "   3. Adding the centimetres gives " + cm1 + " + " + cm2 + " + " + carryCm + " = " + totalCm + " cm " + remMm + " mm.";
        }

        return new SynthesizedPedagogy(
                context,
                "At the " + context + ", learners join two pieces of " + mat + " measuring " + cm1 + " cm " + mm1 + " mm and " + cm2 + " cm " + mm2 + " mm. What is their combined length in cm and mm?",
                howTo,
                "Learners write the two-column addition (cm | mm) for the " + context + " in their books: " + cm1 + " cm " + mm1 + " mm + " + cm2 + " cm " + mm2 + " mm = " + totalCm + " cm " + remMm + " mm.",
                "In the " + context + ", convert the total length of " + totalCm + " cm " + remMm + " mm of " + mat + " into millimetres (" + (totalCm * 10 + remMm) + " mm), and check that Circumference ÷ Diameter ≈ 22/7 (3.14).",
                "For the " + context + ": (a) Convert " + cm1 + " cm " + mm1 + " mm of " + mat + " into millimetres. (b) Multiply a piece of " + multCm + " cm " + multMm + " mm by " + factor + ".",
                "(a) (" + cm1 + " × 10) + " + mm1 + " = " + (cm1 * 10 + mm1) + " mm. (b) " + multCm + " cm × " + factor + " = " + (multCm * factor) + " cm; " +
                        multMm + " mm × " + factor + " = " + (multMm * factor) + " mm (" + ((multMm * factor) / 10) + " cm " + ((multMm * factor) % 10) + " mm); Total = " + prodCm + " cm " + prodMm + " mm."
        );
    }

    // =========================================================================================
    // 2.2 AREA (Area of triangles = 1/2 x b x h and combined shapes — Material-Physics Aware!)
    // =========================================================================================
    private static SynthesizedPedagogy synthesizeArea(String mat) {
        String county = pickCounty();
        boolean usesBox = hasBoxOrCardboard(mat);
        boolean usesString = hasStringOrRope(mat);
        boolean usesStick = hasStickOrStraw(mat);

        int base = (3 + RNG.nextInt(7)) * 2; // even 6..18 cm
        int height = 5 + RNG.nextInt(11); // 5..15 cm
        int rectArea = base * height;
        int triArea = rectArea / 2;

        int sqSide = base;
        int combTriHeight = (2 + RNG.nextInt(5)) * 2;
        int sqArea = sqSide * sqSide;
        int combTriArea = (sqSide * combTriHeight) / 2;
        int totalCombArea = sqArea + combTriArea;

        int eBase = (4 + RNG.nextInt(6)) * 2;
        int eHeight = 7 + RNG.nextInt(9);
        int eArea = (eBase * eHeight) / 2;

        String context;
        String workedExample;
        String howTo;
        String repTask;
        String guidedProb;
        String exitQ;

        if (usesBox && usesString) {
            context = county + " Carpentry & Cardboard Box Gusset Design (Using " + capitalize(mat) + ")";
            workedExample = "At the " + county + " Carpentry Workshop, learners flatten a rectangular cardboard box panel of base " + base +
                    " cm and perpendicular height " + height + " cm, then stretch a string tightly across its diagonal to cut two equal triangular corner braces. What is the area of the full rectangular box panel, and what is the area of one triangular brace?";
            howTo = "1. Flatten the Box into a Rectangle: Each squad unfolds a cardboard box from their " + mat + " flat on the desk and measures a rectangular panel of base " + base + " cm and height " + height + " cm (Rectangle Area = " + base + " × " + height + " = " + rectArea + " cm²).\n" +
                    "   2. Stretch the String Along the Diagonal: Learners take a piece of string from their " + mat + ", anchor it at one corner vertex of the box panel, and stretch it tightly to the opposite diagonal corner.\n" +
                    "   3. Cut/Fold Along the String to Prove ½ × b × h: Squads cut or fold the cardboard box along the stretched string line, placing the two resulting triangles on top of each other to prove they are identical—so each triangle's area is half the rectangular box face: ½ × " + base + " cm × " + height + " cm = " + triArea + " cm².";
            repTask = "Learners trace the flattened rectangular box face (" + base + " cm by " + height + " cm) and the diagonal string line in their exercise books, writing: Rectangle Area = " + rectArea + " cm² → Triangle Area = ½ × base × height = ½ × " + base + " × " + height + " = " + triArea + " cm².";
            guidedProb = "Squads join a square cardboard box flap of side " + sqSide + " cm to a triangular roof flap (outlined with string) of base " + sqSide + " cm and height " + combTriHeight + " cm. Calculate the combined area (" + sqArea + " cm² + " + combTriArea + " cm² = " + totalCombArea + " cm²).";
            exitQ = "In the " + county + " Box & String Workshop, a rectangular box side measures base " + eBase + " cm by height " + eHeight + " cm, and a string is stretched diagonally across it to form a right-angled triangle. Find the area of the triangle.";
        } else if (usesBox) {
            context = county + " Packaging Box & Triangular Flap Design (Using " + capitalize(mat) + ")";
            workedExample = "In " + county + " County, learners unfold a rectangular box panel from " + mat + " measuring " + base + " cm at the base and " + height + " cm in height, and cut it diagonally from corner to corner into two equal triangular flaps. Find the area of one triangular flap.";
            howTo = "1. Squads unfold a rectangular face of their " + mat + " measuring base " + base + " cm by height " + height + " cm (Area = " + rectArea + " cm²).\n" +
                    "   2. Learners draw a diagonal line from one corner of the box panel to the opposite corner and fold/cut along the diagonal.\n" +
                    "   3. By overlapping the two triangular pieces of the box, squads prove that one triangle is exactly half the rectangle: Area = ½ × " + base + " × " + height + " = " + triArea + " cm².";
            repTask = "Learners sketch the rectangular box panel divided diagonally into 2 triangles in their books and compute Area = ½ × base × height = ½ × " + base + " × " + height + " = " + triArea + " cm².";
            guidedProb = "A combined packaging template made from " + mat + " has a square base of side " + sqSide + " cm and a triangular top flap of base " + sqSide + " cm and height " + combTriHeight + " cm. Find the total area (" + sqArea + " + " + combTriArea + " = " + totalCombArea + " cm²).";
            exitQ = "A triangular flap cut diagonally from a rectangular box panel has a base of " + eBase + " cm and a perpendicular height of " + eHeight + " cm. Calculate its area.";
        } else if (usesString || usesStick) {
            context = county + " School Farm Triangular Plot Layout (Using " + capitalize(mat) + ")";
            workedExample = "In " + county + " County, learners peg out a rectangular plot of base " + base + " cm and height " + height + " cm on their desk chart using " + mat + ", then run a diagonal line across it to form two equal triangular plots. What is the area of one triangular plot?";
            howTo = "1. Squads use " + mat + " to outline a rectangle of base " + base + " cm and perpendicular height " + height + " cm on their desk grid (Area = " + base + " × " + height + " = " + rectArea + " cm²).\n" +
                    "   2. Learners stretch a piece of " + mat + " diagonally from one corner to the opposite corner, dividing the rectangle into 2 equal right-angled triangles.\n" +
                    "   3. Squads verify that each triangle occupies half the rectangular space: Area = ½ × " + base + " cm × " + height + " cm = " + triArea + " cm².";
            repTask = "Learners draw the rectangle and diagonal formed by " + mat + " in their books and calculate Area = ½ × base × height = ½ × " + base + " × " + height + " = " + triArea + " cm².";
            guidedProb = "Using " + mat + ", squads outline a combined shape made of a " + sqSide + " cm by " + sqSide + " cm square joined to a triangle of base " + sqSide + " cm and height " + combTriHeight + " cm. Find the combined area (" + sqArea + " + " + combTriArea + " = " + totalCombArea + " cm²).";
            exitQ = "A triangular plot outlined with " + mat + " has a base of " + eBase + " cm and a perpendicular height of " + eHeight + " cm. Find its area.";
        } else {
            context = county + " Classroom Triangular Grid Design (Using " + capitalize(mat) + ")";
            workedExample = "At a school in " + county + " County, learners arrange " + mat + " into a rectangular grid of base " + base + " units and height " + height + " units, and divide it diagonally into two equal right-angled triangles. What is the area of one triangle?";
            howTo = "1. Squads use " + mat + " to model a rectangular grid of base " + base + " units and height " + height + " units (Total rectangle area = " + base + " × " + height + " = " + rectArea + " square units).\n" +
                    "   2. Learners divide the rectangular arrangement of " + mat + " along the diagonal from corner to corner into 2 equal right-angled triangles.\n" +
                    "   3. Squads count and verify that one triangle equals half of the rectangle: Area = ½ × base × height = ½ × " + base + " × " + height + " = " + triArea + " square units.";
            repTask = "Learners draw the rectangular grid of " + mat + " split diagonally into two triangles in their exercise books and write Area = ½ × base × height = ½ × " + base + " × " + height + " = " + triArea + " square units.";
            guidedProb = "In the " + context + ", a combined shape modelled with " + mat + " consists of a square of side " + sqSide + " units joined to a triangle of base " + sqSide + " units and height " + combTriHeight + " units. Find the total area (" + sqArea + " + " + combTriArea + " = " + totalCombArea + " square units).";
            exitQ = "A triangle modelled in the " + context + " has a base of " + eBase + " units and a perpendicular height of " + eHeight + " units. Calculate its area.";
        }

        return new SynthesizedPedagogy(
                context,
                workedExample,
                howTo,
                repTask,
                guidedProb,
                exitQ,
                "Area of a triangle = ½ × base × height = ½ × " + eBase + " × " + eHeight + " = " + eArea + " square units."
        );
    }

    // =========================================================================================
    // 2.3 CAPACITY (cm³, millilitres, litres, and conversions)
    // =========================================================================================
    private static SynthesizedPedagogy synthesizeCapacity(String mat) {
        MaterialStoryProfile story = buildMaterialStory(mat);
        String context = story.context;

        int l = (2 + RNG.nextInt(4)) * 10;
        int w = 20 + RNG.nextInt(21);
        int h = 10 + RNG.nextInt(21);
        int volCm3 = l * w * h;
        double capLitres = volCm3 / 1000.0;

        int eLitres = 3 + RNG.nextInt(7);
        int eMl = (1 + RNG.nextInt(8)) * 100 + 50;
        int eTotalMl = eLitres * 1000 + eMl;

        String howTo;
        if (hasBoxOrCardboard(mat)) {
            howTo = "1. Squads measure the internal length (" + l + " cm), width (" + w + " cm), and height (" + h + " cm) of a rectangular carton/box from their " + mat + ".\n" +
                    "   2. Learners calculate how many 1 cm³ unit cubes fit inside the box: Volume = " + l + " × " + w + " × " + h + " = " + fmt(volCm3) + " cm³.\n" +
                    "   3. Using the rule 1,000 cm³ = 1,000 ml = 1 litre, squads convert the box volume into " + capLitres + " litres.";
        } else {
            howTo = "1. Squads use " + mat + " to model a rectangular container measuring " + l + " cm by " + w + " cm by " + h + " cm.\n" +
                    "   2. Learners multiply Length × Width × Height = " + fmt(volCm3) + " cm³ and group every 1,000 cm³ into 1 litre.\n" +
                    "   3. Squads verify that " + fmt(volCm3) + " cm³ ÷ 1,000 = " + capLitres + " litres.";
        }

        return new SynthesizedPedagogy(
                context,
                "In the " + context + ", a rectangular container from " + story.containerName + " measures " + l + " cm long, " + w + " cm wide, and " + h + " cm high. Find its internal volume in cm³ and its capacity in litres.",
                howTo,
                "Learners write the formula in their exercise books: Volume = L × W × H = " + l + " × " + w + " × " + h + " = " + fmt(volCm3) + " cm³; Capacity = " + fmt(volCm3) + " ÷ 1,000 = " + capLitres + " litres.",
                "In the same " + context + ", convert " + eLitres + " litres " + eMl + " ml into millilitres (" + fmt(eTotalMl) + " ml) using " + mat + ".",
                "For the " + context + ": (a) Convert " + fmt(eTotalMl) + " ml into litres and millilitres. (b) Find the capacity in litres of a rectangular container from " + story.containerName + " measuring 50 cm by 40 cm by 30 cm.",
                "(a) " + eLitres + " litres " + eMl + " ml. (b) Volume = 50 × 40 × 30 = 60,000 cm³; Capacity = 60,000 ÷ 1,000 = 60 litres."
        );
    }

    // =========================================================================================
    // 2.4 MASS (Tonnes, kilograms, grams conversions & operations)
    // =========================================================================================
    private static SynthesizedPedagogy synthesizeMass(String mat) {
        MaterialStoryProfile story = buildMaterialStory(mat);
        String context = story.context;

        int t1 = 2 + RNG.nextInt(5);
        int kg1 = 450 + RNG.nextInt(450);
        int t2 = 1 + RNG.nextInt(4);
        int kg2 = 350 + RNG.nextInt(500);
        int sumKg = kg1 + kg2;
        int carryT = sumKg / 1000;
        int remKg = sumKg % 1000;
        int totalT = t1 + t2 + carryT;

        int divT = 3 + RNG.nextInt(4);
        int divFactor = 4;
        int eachKg = (divT * 1000) / divFactor;

        return new SynthesizedPedagogy(
                context,
                "At the " + context + ", two bulk consignments of " + story.containerName + " weigh " + t1 + " t " + kg1 + " kg and " + t2 + " t " + kg2 + " kg. What is their combined mass in tonnes and kilograms?",
                "1. Squads draw a two-column Weighbridge Chart (Tonnes | Kilograms) and use " + mat + " to represent " + t1 + " t " + kg1 + " kg and " + t2 + " t " + kg2 + " kg.\n" +
                        "   2. Learners add the kilograms column first: " + kg1 + " kg + " + kg2 + " kg = " + fmt(sumKg) + " kg.\n" +
                        "   3. Since 1,000 kg = 1 tonne, squads regroup 1,000 kg to the Tonnes column (" + t1 + " + " + t2 + " + " + carryT + " = " + totalT + " t) with " + remKg + " kg remaining.",
                "Learners record the vertical addition for the " + context + " in their books: " + t1 + " t " + kg1 + " kg + " + t2 + " t " + kg2 + " kg = " + totalT + " t " + remKg + " kg.",
                "At the " + context + ", " + divT + " tonnes of supplies are shared equally among " + divFactor + " " + story.containerName + ". Use " + mat + " to convert " + divT + " t to " + fmt(divT * 1000) + " kg and divide by " + divFactor + " (" + eachKg + " kg each).",
                "From the " + context + ": (a) Convert " + t1 + " tonnes " + kg1 + " kg into kilograms. (b) Subtract " + t2 + " t " + kg2 + " kg from the combined mass of " + totalT + " t " + remKg + " kg.",
                "(a) (" + t1 + " × 1,000) + " + kg1 + " = " + fmt(t1 * 1000 + kg1) + " kg. (b) " + t1 + " t " + kg1 + " kg."
        );
    }

    // =========================================================================================
    // 2.5 TIME (a.m./p.m. to 24-hour clock conversions & SGR/bus travel durations)
    // =========================================================================================
    private static SynthesizedPedagogy synthesizeTime(String mat) {
        MaterialStoryProfile story = buildMaterialStory(mat);
        String context = story.context;

        int depHour = 7 + RNG.nextInt(4);
        int depMin = (1 + RNG.nextInt(5)) * 10;
        int durHours = 3 + RNG.nextInt(4);
        int durMins = (1 + RNG.nextInt(5)) * 10;

        int totalMins = depMin + durMins;
        int arrHour24 = depHour + durHours + (totalMins / 60);
        int arrMin = totalMins % 60;
        int arrHour12 = arrHour24 > 12 ? arrHour24 - 12 : arrHour24;
        String amPm = arrHour24 >= 12 ? "p.m." : "a.m.";

        String dep24 = String.format(Locale.US, "%02d%02d hrs", depHour, depMin);
        String arr24 = String.format(Locale.US, "%02d%02d hrs", arrHour24, arrMin);
        String arr12 = String.format(Locale.US, "%d:%02d %s", arrHour12, arrMin, amPm);

        return new SynthesizedPedagogy(
                context,
                "In the " + context + ", a delivery van carrying " + mat + " departs at " + dep24 + " and travels for " + durHours + " hours " + durMins + " minutes. Find its arrival time in 24-hour and 12-hour clock notation.",
                "1. Squads use " + mat + " to build a 24-hour clock face and a linear journey timeline on their desks starting at " + dep24 + ".\n" +
                        "   2. Learners advance by " + durHours + " full hours and " + durMins + " minutes along the timeline using " + mat + ", regrouping 60 minutes into 1 hour where total minutes (" + totalMins + ") reach 60 or more.\n" +
                        "   3. Squads read the final arrival time as " + arr24 + " and subtract 1200 hrs to express afternoon times in p.m. (" + arr12 + ").",
                "Learners copy the " + context + " schedule into their books and calculate: " + dep24 + " + " + durHours + " h " + durMins + " min = " + arr24 + " (" + arr12 + ").",
                "Using the " + context + " and " + mat + ", convert 3:45 p.m. (1545 hrs) and 9:15 p.m. (2115 hrs) to 24-hour clock notation and find the duration between them (5 hours 30 minutes).",
                "For the " + context + ": (a) Convert 4:20 p.m. into 24-hour clock notation. (b) A work shift using " + mat + " starts at 0830 hrs and ends at 1315 hrs. How long did the shift take?",
                "(a) 0420 + 1200 = 1620 hrs. (b) 1315 hrs - 0830 hrs = 4 hours 45 minutes."
        );
    }

    // =========================================================================================
    // 2.6 MONEY (Household/school budgets, price lists, profit/loss, and VAT/tax)
    // =========================================================================================
    private static SynthesizedPedagogy synthesizeMoney(String mat) {
        MaterialStoryProfile story = buildMaterialStory(mat);
        String context = story.context;

        int p1 = 160 + RNG.nextInt(50);
        int p2 = 320 + RNG.nextInt(80);
        int p3 = 110 + RNG.nextInt(40);
        int q1 = 2;
        int q2 = 1;
        int q3 = 3;
        int totalBill = (p1 * q1) + (p2 * q2) + (p3 * q3);
        int notePaid = ((totalBill / 1000) + 1) * 1000;
        int balance = notePaid - totalBill;

        int buyPrice = 1200 + RNG.nextInt(600);
        int profit = 250 + RNG.nextInt(200);
        int sellPrice = buyPrice + profit;

        return new SynthesizedPedagogy(
                context,
                "In the " + context + ", a workshop prepares a budget using " + mat + " for " + q1 + " " + story.containerName + " at KES " + p1 + " each, " +
                        q2 + " master bundle at KES " + p2 + ", and " + q3 + " packs of " + story.unitItem + " at KES " + p3 + " each. Calculate the total bill and the balance from KES " + fmt(notePaid) + ".",
                "1. Squads set up a classroom store for the " + context + " using " + mat + " as labelled items and currency tokens.\n" +
                        "   2. Learners calculate the cost of each group: " + q1 + " × " + p1 + " = KES " + (p1 * q1) + "; 1 × " + p2 + " = KES " + p2 + "; " + q3 + " × " + p3 + " = KES " + (p3 * q3) + ".\n" +
                        "   3. Squads sum the subtotals (KES " + fmt(totalBill) + ") and count out the change from KES " + fmt(notePaid) + " (KES " + fmt(balance) + ").",
                "Learners draw a 4-column budget table (Item | Quantity | Unit Price | Total Cost) in their books showing Total = KES " + fmt(totalBill) + " and Balance = KES " + fmt(balance) + ".",
                "At the same " + context + ", a trader bought a batch of " + mat + " for KES " + fmt(buyPrice) + " and sold it for KES " + fmt(sellPrice) + ". Use " + mat + " to model the profit made (KES " + fmt(profit) + ").",
                "From the " + context + ": A workshop buys materials (" + mat + ") at KES " + fmt(buyPrice) + " and sells the finished product at KES " + fmt(sellPrice) + ". (a) Calculate the profit. (b) State one reason why citizens pay tax.",
                "(a) Profit = Selling Price - Buying Price = KES " + fmt(sellPrice) + " - KES " + fmt(buyPrice) + " = KES " + fmt(profit) + ". (b) Taxes fund public services such as schools, hospitals, and roads."
        );
    }

    // =========================================================================================
    // 3.1 LINES (Parallel lines and perpendicular bisectors)
    // =========================================================================================
    private static SynthesizedPedagogy synthesizeLines(String mat) {
        MaterialStoryProfile story = buildMaterialStory(mat);
        String context = story.context;
        int segLen = 6 + RNG.nextInt(5) * 2;
        int halfLen = segLen / 2;

        String howTo;
        if (hasStringOrRope(mat) || hasStickOrStraw(mat)) {
            howTo = "1. Squads stretch a piece of " + mat + " straight across their desk to form horizontal line AB of length " + segLen + " cm.\n" +
                    "   2. Learners fold the piece of " + mat + " in half to locate the exact midpoint M at " + halfLen + " cm from both A and B.\n" +
                    "   3. Squads lay a second piece of " + mat + " across point M at a 90° right angle (verified with a set square or square box corner) to form the perpendicular bisector CD.";
        } else {
            howTo = "1. Squads use " + mat + " and a ruler to mark straight line segment AB of length " + segLen + " cm on their desks.\n" +
                    "   2. Learners locate the midpoint M at " + halfLen + " cm and construct line CD crossing AB at 90°.\n" +
                    "   3. Squads also arrange two parallel rows of " + mat + " a constant 4 cm apart to demonstrate parallel lines.";
        }

        return new SynthesizedPedagogy(
                context,
                "In the " + context + ", learners use " + mat + " to lay out a straight line AB of length " + segLen + " cm and construct a perpendicular bisector CD across its midpoint M. What is the length of AM and the angle at M?",
                howTo,
                "Learners construct line segment AB = " + segLen + " cm in their exercise books using a ruler and pair of compasses, bisecting it perpendicularly at M (" + halfLen + " cm) at 90°.",
                "For the " + context + ", use " + mat + " and a ruler/set square to construct two parallel lines 4 cm apart that never meet.",
                "For the " + context + ": Draw a straight line PQ = " + segLen + " cm and construct its perpendicular bisector at point M. State the length of PM and the angle formed at M.",
                "Length PM = " + segLen + " ÷ 2 = " + halfLen + " cm; Angle at M = 90° (right angle)."
        );
    }

    // =========================================================================================
    // 3.2 ANGLES (Angles on a straight line = 180°, sum of angles in a triangle = 180°)
    // =========================================================================================
    private static SynthesizedPedagogy synthesizeAngles(String mat) {
        MaterialStoryProfile story = buildMaterialStory(mat);
        String context = story.context;

        int angA = 45 + RNG.nextInt(35);
        int angB = 50 + RNG.nextInt(35);
        int angC = 180 - (angA + angB);

        int lineKnown = 65 + RNG.nextInt(60);
        int lineUnknown = 180 - lineKnown;

        String howTo;
        if (hasStringOrRope(mat) || hasStickOrStraw(mat) || hasBoxOrCardboard(mat)) {
            howTo = "1. Squads use " + mat + " to construct a triangular frame ABC on their desks where two base angles measure " + angA + "° and " + angB + "°.\n" +
                    "   2. Learners cut or mark the 3 corner angles from " + mat + " and place them side-by-side along a straight stretched line to prove the 3 angles form a straight line (180°).\n" +
                    "   3. Subtracting (" + angA + "° + " + angB + "° = " + (angA + angB) + "°) from 180° gives the third angle: " + angC + "°.";
        } else {
            howTo = "1. Squads arrange " + mat + " to model triangular frame ABC with angles " + angA + "° and " + angB + "°.\n" +
                    "   2. Learners place the three interior angles side-by-side along a straight ruler edge to demonstrate that angles in a triangle add up to 180°.\n" +
                    "   3. Squads calculate the missing angle: 180° - (" + angA + "° + " + angB + "°) = " + angC + "°.";
        }

        return new SynthesizedPedagogy(
                context,
                "In the " + context + ", learners build a triangular frame using " + mat + " whose two interior angles measure " + angA + "° and " + angB + "°. Calculate the third angle.",
                howTo,
                "Learners draw the triangular frame in their books and solve: " + angA + "° + " + angB + "° + x = 180° → " + (angA + angB) + "° + x = 180° → x = " + angC + "°.",
                "In the same " + context + ", a piece of " + mat + " meets a straight edge at " + lineKnown + "°. Find the adjacent angle on the straight line (180° - " + lineKnown + "° = " + lineUnknown + "°).",
                "For the " + context + ": (a) Find the missing angle x on a straight line if the adjacent angle formed by " + mat + " is " + lineKnown + "°. (b) In triangle PQR, angle P = " + angA + "° and angle Q = " + angB + "°. Find angle R.",
                "(a) x = 180° - " + lineKnown + "° = " + lineUnknown + "°. (b) Angle R = 180° - (" + angA + "° + " + angB + "°) = 180° - " + (angA + angB) + "° = " + angC + "°."
        );
    }

    // =========================================================================================
    // 3.3 3-D OBJECTS (Vertices, faces, edges & nets of cuboids, cubes, cylinders, pyramids)
    // =========================================================================================
    private static SynthesizedPedagogy synthesize3DObjects(String mat) {
        MaterialStoryProfile story = buildMaterialStory(mat);
        String context = story.context;

        boolean useSquarePyramid = RNG.nextBoolean();
        String solidName = useSquarePyramid ? "square-based pyramid" : "triangular prism";
        int faces = 5;
        int vertices = useSquarePyramid ? 5 : 6;
        int edges = useSquarePyramid ? 8 : 9;

        String howTo;
        if (hasBoxOrCardboard(mat) || hasStringOrRope(mat)) {
            howTo = "1. Squads inspect a closed rectangular box/carton from their " + mat + ", counting its 6 rectangular faces, 8 corner vertices, and 12 edges.\n" +
                    "   2. Learners carefully unfold the box flat onto the desk to reveal its 2-D net, then use " + mat + " to fold it back into a 3-D cuboid.\n" +
                    "   3. Next, squads construct a " + solidName + " model using " + mat + " and count its " + faces + " faces, " + vertices + " vertices, and " + edges + " edges.";
        } else {
            howTo = "1. Squads build 3-D skeleton models of a cuboid and a " + solidName + " using " + mat + ".\n" +
                    "   2. Learners touch and count each flat face, each corner vertex, and each straight edge on both solids.\n" +
                    "   3. Squads unfold a paper/cardboard net flat on the desk to see how 2-D faces fold into a 3-D solid.";
        }

        return new SynthesizedPedagogy(
                context,
                "At the " + context + ", learners use " + mat + " to inspect a rectangular cuboid and a " + solidName + ". State the number of faces, vertices, and edges of each 3-D solid.",
                howTo,
                "Learners draw a 4-column table (3-D Solid | Faces | Vertices | Edges) in their exercise books: Cuboid = 6 Faces, 8 Vertices, 12 Edges; " +
                        capitalize(solidName) + " = " + faces + " Faces, " + vertices + " Vertices, " + edges + " Edges.",
                "In the " + context + ", trace the 2-D net of an open cube (5 equal squares) and a closed cuboid (6 rectangles) in your exercise book using " + mat + ".",
                "From the " + context + ": (a) State the number of faces, vertices, and edges of a cuboid made from " + mat + ". (b) State the number of faces, vertices, and edges of a " + solidName + ".",
                "(a) Cuboid: 6 faces, 8 vertices, 12 edges. (b) " + capitalize(solidName) + ": " + faces + " faces, " + vertices + " vertices, " + edges + " edges."
        );
    }

    // =========================================================================================
    // 4.1 DATA HANDLING (Frequency tables, tally marks, and bar graphs)
    // =========================================================================================
    private static SynthesizedPedagogy synthesizeDataHandling(String mat) {
        MaterialStoryProfile story = buildMaterialStory(mat);
        String context = story.context;

        int scale = (1 + RNG.nextInt(2)) * 5;
        int c1 = (3 + RNG.nextInt(5)) * scale;
        int c2 = (4 + RNG.nextInt(5)) * scale;
        int c3 = (2 + RNG.nextInt(4)) * scale;
        int c4 = (5 + RNG.nextInt(4)) * scale;
        int total = c1 + c2 + c3 + c4;
        int diff = Math.abs(c4 - c3);

        return new SynthesizedPedagogy(
                context,
                "In the " + context + ", four squads counted their sorted " + mat + ": Squad A = " + c1 + ", Squad B = " + c2 + ", Squad C = " + c3 + ", and Squad D = " + c4 +
                        ". Represent this data in a frequency table and bar graph using a vertical scale of 1 square = " + scale + " items.",
                "1. Squads group their " + mat + " in bundles of 5 to represent tally marks for each squad in the " + context + ".\n" +
                        "   2. Using the scale 1 unit = " + scale + " items, learners build 4 vertical bar columns on their desks using " + mat + " of heights " +
                        (c1 / scale) + ", " + (c2 / scale) + ", " + (c3 / scale) + ", and " + (c4 / scale) + " units.\n" +
                        "   3. Squads compare the bar heights to identify the highest bar and total count (" + total + " items of " + mat + ").",
                "Learners draw a frequency table and a labelled bar graph (1 square = " + scale + " items) in their exercise books for Squad A (" + c1 + "), Squad B (" + c2 + "), Squad C (" + c3 + "), and Squad D (" + c4 + ").",
                "From your " + context + " bar graph, find the difference between Squad D (" + c4 + ") and Squad C (" + c3 + ") using " + mat + " (Answer: " + diff + ").",
                "Using the " + context + " data (Squad A = " + c1 + ", Squad B = " + c2 + ", Squad C = " + c3 + ", Squad D = " + c4 + "): (a) Find the total number of items. (b) At a scale of 1 square = " + scale + " items, how many squares high is Squad A's bar?",
                "(a) Total = " + c1 + " + " + c2 + " + " + c3 + " + " + c4 + " = " + total + " items. (b) " + c1 + " ÷ " + scale + " = " + (c1 / scale) + " squares high."
        );
    }

    // =========================================================================================
    // 5.1 ALGEBRA (Forming and solving one-step linear inequalities)
    // =========================================================================================
    private static SynthesizedPedagogy synthesizeAlgebraInequalities(String mat) {
        MaterialStoryProfile story = buildMaterialStory(mat);
        String context = story.context;

        int coeff = 2 + RNG.nextInt(4);
        int solBound = 4 + RNG.nextInt(8);
        int addend = 3 + RNG.nextInt(9);
        int rhs = coeff * solBound + addend;

        int eAdd = 4 + RNG.nextInt(8);
        int eBound = 6 + RNG.nextInt(10);
        int eRhs = eBound + eAdd;

        return new SynthesizedPedagogy(
                context,
                "In the " + context + ", " + coeff + " identical " + story.containerName + " of " + story.unitItem + " plus " + addend + " loose " + story.unitItem + " must total less than " + rhs +
                        ". Form and solve the inequality " + coeff + "x + " + addend + " < " + rhs + " for whole numbers.",
                "1. Squads model the inequality on their desks using " + mat + ": they place " + coeff + " mystery " + story.containerName + " (x) plus " + addend + " single items on the left, and " + rhs + " items on the right.\n" +
                        "   2. Learners remove " + addend + " single items of " + mat + " from both sides, leaving " + coeff + "x < " + (rhs - addend) + ".\n" +
                        "   3. Dividing the remaining " + (rhs - addend) + " items equally into " + coeff + " " + story.containerName + " shows that each x must hold fewer than " + solBound + " items (x < " + solBound + ").",
                "Learners write the algebraic steps in their exercise books: " + coeff + "x + " + addend + " < " + rhs + " → " + coeff + "x < " + (rhs - addend) +
                        " → x < " + solBound + " (Whole numbers: 0, 1, 2, ..., " + (solBound - 1) + ").",
                "In the " + context + ", when " + eAdd + " items are added to one of the " + story.containerName + " holding p items, the total is greater than " + eRhs + ". Use " + mat + " to form and solve p + " + eAdd + " > " + eRhs + " (p > " + eBound + ").",
                "For the " + context + ": Solve the inequality x + " + eAdd + " ≤ " + eRhs + " and list the whole number values of x that are greater than " + (eBound - 3) + ".",
                "Subtract " + eAdd + " from both sides: x ≤ " + eRhs + " - " + eAdd + " → x ≤ " + eBound + ". Whole numbers greater than " + (eBound - 3) + " satisfying x ≤ " + eBound + " are: " + (eBound - 2) + ", " + (eBound - 1) + ", " + eBound + "."
        );
    }

    private static String capitalize(String s) {
        if (s == null || s.isEmpty()) return "";
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
}
