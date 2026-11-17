package ke.co.mwalimuai.sne;

import java.util.Locale;

/**
 * Universal Design for Learning (UDL) & Special Needs Education (SNE) engine for Mwalimu SNE AI.
 * Generates tailored multisensory (VAKT: Visual, Auditory, Kinesthetic, Tactile) adaptations,
 * KSL (Kenyan Sign Language) prompts, tactile manipulative recipes, and 4-tier CBC rubrics.
 */
public class InclusiveSNEPedagogyHelper {

    public enum SNEProfile {
        UNIVERSAL_INCLUSIVE,
        BLIND_LOW_VISION,
        DEAF_HARD_OF_HEARING,
        DYSLEXIA,
        DYSCALCULIA,
        ADHD_EXECUTIVE_FUNCTION,
        MOTOR_PHYSICAL
    }

    /**
     * Detects SNEProfile from user's selected dropdown string or free-form text.
     */
    public static SNEProfile detectProfile(String text) {
        if (text == null) return SNEProfile.UNIVERSAL_INCLUSIVE;
        String lower = text.toLowerCase(Locale.ROOT);

        if (lower.contains("blind") || lower.contains("low vision") || lower.contains("visual impairment") || lower.contains("braille")) {
            return SNEProfile.BLIND_LOW_VISION;
        } else if (lower.contains("deaf") || lower.contains("hard of hearing") || lower.contains("sign language") || lower.contains("ksl")) {
            return SNEProfile.DEAF_HARD_OF_HEARING;
        } else if (lower.contains("dyslexi") || lower.contains("reading difficult")) {
            return SNEProfile.DYSLEXIA;
        } else if (lower.contains("dyscalculi") || lower.contains("number struggle") || lower.contains("math anxiety")) {
            return SNEProfile.DYSCALCULIA;
        } else if (lower.contains("adhd") || lower.contains("hyperactiv") || lower.contains("attention span") || lower.contains("focus challenge")) {
            return SNEProfile.ADHD_EXECUTIVE_FUNCTION;
        } else if (lower.contains("motor") || lower.contains("physical") || lower.contains("wheelchair") || lower.contains("cerebral")) {
            return SNEProfile.MOTOR_PHYSICAL;
        }
        return SNEProfile.UNIVERSAL_INCLUSIVE;
    }

    /**
     * Builds comprehensive Universal Design for Learning (UDL) & SNE inclusion notes for the lesson plan.
     */
    public static String buildSNEInclusionSection(String need, String subStrand, String localRes, String contextDesc) {
        SNEProfile profile = detectProfile(need);
        StringBuilder sb = new StringBuilder();

        sb.append("────────────────────────────────────────\n");
        sb.append("▪ SNE & UNIVERSAL DESIGN FOR LEARNING (UDL) INCLUSION MATRIX\n");
        sb.append("▪ Target Need Focus: ").append(getProfileTitle(profile)).append("\n\n");

        switch (profile) {
            case BLIND_LOW_VISION:
                sb.append("1. Tactile & Kinesthetic Manipulative Adaptations (Using ").append(localRes).append("):\n");
                sb.append("   • Tactile String/Rope Knots: Tie firm knots at 5 cm intervals along ").append(localRes)
                        .append(" so the learner counts units and dimensions purely by finger touch.\n");
                sb.append("   • Embossed Cardboard Edges: Groove shape outlines with a blunt pencil or apply droplets of dried PVA/wood glue at each corner vertex to provide raised tactile landmarks.\n");
                sb.append("   • Perimeter & Area Touch: Paste dried beans or maize seeds inside boundary lines so the learner feels surface coverage.\n\n");
                sb.append("2. Teacher Audio-Description Protocol:\n");
                sb.append("   • Describe every chalkboard diagram verbally before writing (e.g. \"I am tracing a right-angled triangle starting from the bottom-left vertex, climbing up 8 finger-widths to the apex...\").\n");
                sb.append("   • Pair with a supportive peer sighted-buddy who verbalizes intermediate squad calculations.\n\n");
                sb.append("3. Assistive Technology & Low-Vision Scaffolding:\n");
                sb.append("   • Provide high-contrast yellow chalk on clean black chalkboard slate (minimum font size: 4 cm tall).\n");
                sb.append("   • Use the Mwalimu SNE AI Offline Audio Assistant (TTS) to listen to instructions hands-free.");
                break;

            case DEAF_HARD_OF_HEARING:
                sb.append("1. Kenyan Sign Language (KSL) Math Glosses & Visual Gestures:\n");
                sb.append("   • ").append(getKslMathGloss(subStrand)).append("\n");
                sb.append("   • Use standardized finger-spelling for math operation symbols (+, −, ×, ÷, =).\n\n");
                sb.append("2. Desk Action Cards & Visual Flowcharts:\n");
                sb.append("   • Provide 3 color-coded desk indicator cards: 🟢 Green (\"Squad is calculating\"), 🟡 Yellow (\"Need partner review\"), 🔴 Red (\"Mwalimu, please demonstrate this step physically\").\n");
                sb.append("   • Write all mathematical step numbers and numerical values in high-visibility boxes on the chalkboard before explaining.\n\n");
                sb.append("3. Vibration & Environmental Accommodation:\n");
                sb.append("   • Position the deaf learner with an unobstructed direct sightline to the teacher's face and hands for lip-reading and sign visual cues.\n");
                sb.append("   • Tap the desk firmly or use phone vibration to signal stage transitions (Concrete ➔ Guided Practice ➔ Exit Ticket).");
                break;

            case DYSLEXIA:
                sb.append("1. Symbolic & Iconic Storyboarding (Bypassing Text Density):\n");
                sb.append("   • Do NOT present math problems as dense multi-line paragraphs. Use symbolic picture cards and chalkboard icons:\n");
                sb.append("     [ 📦 Consignment Box ] ➔ [ ").append(localRes).append(" ] ➔ [ Calculated Total = ? ]\n\n");
                sb.append("2. Color-Coded Numerical Columns:\n");
                sb.append("   • Write place values and formula terms in distinct chalk colors: Base/Units in Green, Height/Tens in Blue, Operation symbols in Yellow/Red.\n");
                sb.append("   • This eliminates digit reversals (confusing 6 and 9) and sign disorientation.\n\n");
                sb.append("3. Low-Distraction Formatting:\n");
                sb.append("   • Provide a card window (a paper sheet with a rectangular cutout) so the learner focuses on one calculation line at a time without visual crowding.");
                break;

            case DYSCALCULIA:
                sb.append("1. Concrete ➔ Pictorial ➔ Abstract (CPA) Strict Progression:\n");
                sb.append("   • FORBID jump-to-formula teaching. The learner must physically manipulate ").append(localRes)
                        .append(" in their hands for 5 full minutes before any mathematical symbol is written.\n");
                sb.append("   • For fractions/area: Physically overlap 2 identical cardboard pieces before introducing ½ × b × h.\n\n");
                sb.append("2. Permanent Desk Anchors (Ten-Frames & Dot Math):\n");
                sb.append("   • Draw a permanent 10-frame (two rows of 5 squares) on the corner of the learner's desk to anchor number visualization.\n");
                sb.append("   • Allow the learner to use counters (bottle tops/seeds) as external working memory anchors without penalty.\n\n");
                sb.append("3. Anxiety De-escalation:\n");
                sb.append("   • Never demand rapid mental math drills; allow 30 seconds of quiet manipulation with ").append(localRes).append(" before answering.");
                break;

            case ADHD_EXECUTIVE_FUNCTION:
                sb.append("1. High-Tempo 3-Minute Micro-Chunking:\n");
                sb.append("   • Break development stages into 3-minute action sprints: 3 Mins physical handling of ").append(localRes)
                        .append(" ➔ 3 Mins peer verification ➔ 2 Mins book recording.\n");
                sb.append("   • Set a visible countdown timer or sand glass on the front table to anchor time perception.\n\n");
                sb.append("2. Active Kinesthetic Leadership Roles:\n");
                sb.append("   • Appoint the learner as the Squad's Material Custodian or Timekeeper to channel natural physical energy into responsible classroom leadership.\n\n");
                sb.append("3. Distraction-Free Desk Setup:\n");
                sb.append("   • Keep only the required ").append(localRes).append(" on the desk; store school bags and non-lesson items under the bench to prevent sensory overload.");
                break;

            case MOTOR_PHYSICAL:
                sb.append("1. Adapted Manipulative Grips & Stabilized Trays:\n");
                sb.append("   • Use thick sisal ropes instead of thin strings, and large carton panels instead of flimsy paper to support learners with limited fine motor grasp.\n");
                sb.append("   • Place manipulatives in a shallow wooden or cardboard rim tray to prevent pieces from rolling off desks.\n\n");
                sb.append("2. Scribe & Pilot Cooperative Learning:\n");
                sb.append("   • Team the learner with a supportive peer partner: The learner with motor challenge acts as the 'Lead Strategist' (deciding measurements and operations), while the partner acts as the 'Hands-on Scribe'.\n\n");
                sb.append("3. Non-Pen Assessment Alternatives:\n");
                sb.append("   • Accept verbal answers, pointing, or numbered card placement instead of requiring handwritten calculations in exercise books.");
                break;

            default:
                sb.append("1. Multi-Tier Differentiated Scaffolding:\n");
                sb.append("   • Level 1 (Struggling Learners): Provide pre-counted sets of ").append(localRes).append(" and whole-number calculations with visual step guides.\n");
                sb.append("   • Level 2 (On-Level Learners): Standard KICD curriculum calculations in the ").append(contextDesc).append(" scenario.\n");
                sb.append("   • Level 3 (Fast Finishers): Challenge to calculate inverted variations or reverse problems using ").append(localRes).append(".\n\n");
                sb.append("2. Cooperative Squad Peer Coaching:\n");
                sb.append("   • Group learners into heterogeneous squads of 4–5 with rotating roles: Facilitator, Material Custodian, Scribe, and Presenter.");
                break;
        }

        return sb.toString();
    }

    /**
     * Builds a multi-pathway 4-Tier CBC assessment rubric that assesses learners through touch, oral explanation,
     * concrete demonstration, and written work, plus automated offline remedial prescriptions.
     */
    public static String buildInclusiveAssessmentSection(
            String need,
            String subStrand,
            String contextDesc,
            String exitQ,
            String exitSol,
            String localRes) {

        SNEProfile profile = detectProfile(need);
        StringBuilder sb = new StringBuilder();

        sb.append("▪ Formative Assessment Task (").append(contextDesc).append("):\n");
        sb.append("   ").append(exitQ).append("\n\n");
        sb.append("▪ Expected Solution & Verification Key:\n");
        sb.append("   ").append(exitSol).append("\n\n");

        sb.append("▪ Multi-Pathway Observable Evidence of Learning:\n");
        switch (profile) {
            case BLIND_LOW_VISION:
                sb.append("   • Assessed through tactile model verification (feeling knotted strings or grooved cardboard edges) and oral mathematical defense.\n");
                break;
            case DEAF_HARD_OF_HEARING:
                sb.append("   • Assessed through physical manipulative setup and clear KSL sign communication/written board demonstration.\n");
                break;
            case DYSLEXIA:
                sb.append("   • Assessed on conceptual understanding and correct numerical outcome; spelling and handwriting neatness are NOT penalized.\n");
                break;
            case DYSCALCULIA:
                sb.append("   • Assessed on concrete manipulative demonstration with ").append(localRes).append(" and Ten-Frame representation.\n");
                break;
            default:
                sb.append("   • Assessed through hands-on manipulation of ").append(localRes).append(", peer collaboration, and exercise book working.\n");
                break;
        }

        sb.append("────────────────────────────────────────\n");
        sb.append("▪ Inclusive 4-Tier CBC Competency Rubric:\n\n");

        sb.append("1. Exceeds Expectations (EE):\n");
        sb.append("   • Independently solves the ").append(contextDesc).append(" task accurately.\n");
        sb.append("   • Clearly explains the underlying mathematical principle using ").append(localRes).append(" (orally, in sign, or in writing) and mentors squad members.\n\n");

        sb.append("2. Meets Expectations (ME):\n");
        sb.append("   • Correctly models the ").append(subStrand).append(" problem using ").append(localRes).append(" and arrives at the right answer with standard units.\n\n");

        sb.append("3. Approaching Expectations (AE):\n");
        sb.append("   • Correctly arranges ").append(localRes).append(" for the initial stage but makes an arithmetic calculation error.\n\n");

        sb.append("4. Below Expectations (BE):\n");
        sb.append("   • Struggles to initiate material manipulation; requires direct 1-on-1 teacher coaching with ").append(localRes).append(".\n\n");

        sb.append("────────────────────────────────────────\n");
        sb.append("▪ Automated Offline Remedial Prescription (For Learners at AE / BE):\n");
        sb.append("   ").append(getRemedialPrescription(profile, subStrand, localRes));

        return sb.toString();
    }

    private static String getRemedialPrescription(SNEProfile profile, String subStrand, String localRes) {
        switch (profile) {
            case BLIND_LOW_VISION:
                return "Provide 1-on-1 tactile guided touch: Guide the learner's hands over the pre-knotted knots on " + localRes + " while counting aloud together before attempting any independent problem.";
            case DEAF_HARD_OF_HEARING:
                return "Use desk action cards: Re-demonstrate the physical grouping with " + localRes + " silently at desk level, pointing to each stage of the worked chalkboard diagram.";
            case DYSLEXIA:
                return "Switch to colored chalk number cards: Have the learner match physical counters of " + localRes + " directly onto large colored digit cards (Green for base, Blue for height).";
            case DYSCALCULIA:
                return "Step back to Concrete CPA: Give the learner 12 bottle tops/counters on a 10-frame grid. Have them remove half physically before re-introducing the ½ multiplier.";
            case ADHD_EXECUTIVE_FUNCTION:
                return "2-Minute Focus Sprint: Give a single short manipulative micro-task with " + localRes + ", provide immediate verbal praise upon completion, then advance to the next step.";
            case MOTOR_PHYSICAL:
                return "Verbal Pilot Protocol: Let the learner direct the teacher or peer assistant where to place " + localRes + " (\"Place 6 units on the base line\"), affirming their conceptual mastery.";
            default:
                return "Pair with a peer mentor from an EE squad for 5 minutes of concrete manipulation practice using " + localRes + " before attempting tomorrow's lesson.";
        }
    }

    public static String getKslMathGloss(String subStrand) {
        String s = subStrand != null ? subStrand.toLowerCase(Locale.ROOT) : "";
        if (s.contains("area") || s.contains("triangle")) {
            return "KSL Math Gloss: Sign 'ENEO' (sweep flat horizontal palms across desk surface) + 'PEMBETATU' (touch index fingers and thumbs together to form a triangle).";
        } else if (s.contains("fraction")) {
            return "KSL Math Gloss: Sign 'SEHEMU' (hold flat non-dominant hand as denominator, place dominant hand above as numerator, chop edge down).";
        } else if (s.contains("decimal")) {
            return "KSL Math Gloss: Sign 'DESIMALI' (tap index finger firmly on desk surface to mark the decimal point, then fingerspell digits).";
        } else if (s.contains("multipl") || s.contains("product")) {
            return "KSL Math Gloss: Sign 'ZIDISHA' (cross index fingers in an X shape, then open palms outward).";
        } else if (s.contains("divis")) {
            return "KSL Math Gloss: Sign 'GAWA' (move both flat hands apart horizontally as if dividing items into equal piles).";
        } else if (s.contains("length") || s.contains("perimeter")) {
            return "KSL Math Gloss: Sign 'UREFU' (stretch both hands apart horizontally) + 'MZUNGUKO' (trace outer perimeter boundary).";
        } else if (s.contains("capacity") || s.contains("volume")) {
            return "KSL Math Gloss: Sign 'UJAZO' (cup both hands together and raise them upwards to indicate holding liquid).";
        } else if (s.contains("mass")) {
            return "KSL Math Gloss: Sign 'UZITO' (weigh imaginary heavy objects by dipping both palms up and down alternately).";
        } else if (s.contains("money")) {
            return "KSL Math Gloss: Sign 'FEDHA / PESA' (rub thumb and fingers together, then sign SHILINGI).";
        } else {
            return "KSL Math Gloss: Sign 'HISABATI' (interlock fingers and move hands in circular motion) + gesture to physical counters.";
        }
    }

    public static String getKiswahiliMathGloss(String subStrand) {
        String s = subStrand != null ? subStrand.toLowerCase(Locale.ROOT) : "";
        if (s.contains("area")) return "Hisabati ya KICD: Eneo la Pembetatu (Eneo = ½ × Tako × Kimo)";
        if (s.contains("fraction")) return "Hisabati ya KICD: Sehemu (Kizidishi, Kigawe na Sehemu Sahihi)";
        if (s.contains("decimal")) return "Hisabati ya KICD: Desimali (Thamani ya Nafasi ya Elfu Moja)";
        if (s.contains("multipl")) return "Hisabati ya KICD: Kuzidisha Nambari Kamili";
        if (s.contains("divis")) return "Hisabati ya KICD: Kugawa na Kanuni za Ugawaji (BODMAS)";
        if (s.contains("length")) return "Hisabati ya KICD: Urefu (Milimita, Sentimita na Mzingo)";
        if (s.contains("capacity")) return "Hisabati ya KICD: Ujazo na Uwezo (Lita na Mililita)";
        if (s.contains("mass")) return "Hisabati ya KICD: Misa na Uzito (Tani, Kilogramu na Gramu)";
        if (s.contains("time")) return "Hisabati ya KICD: Wakati (Saa 24 na Saa za Asubuhi/Alasiri)";
        if (s.contains("money")) return "Hisabati ya KICD: Fedha, Bajeti, Faida na Hasara";
        if (s.contains("line")) return "Hisabati ya KICD: Mistari Sambamba na Mistari Tasa";
        if (s.contains("angle")) return "Hisabati ya KICD: Pembe na Jumla ya Pembe katika Pembetatu (180°)";
        if (s.contains("3-d")) return "Hisabati ya KICD: Maumbo ya 3-D (Nyuso, Vipele na Pembe za Miundo)";
        if (s.contains("data")) return "Hisabati ya KICD: Takwimu, Michoro ya Pau na Majedwali";
        if (s.contains("algebra")) return "Hisabati ya KICD: Aljebra na Milinganyo Isiyo na Ulinganifu";
        return "Hisabati ya KICD: Nambari Kamili na Thamani ya Nafasi";
    }

    public static String getProfileTitle(SNEProfile p) {
        switch (p) {
            case BLIND_LOW_VISION: return "Visual Impairment / Blind (Tactile Knotted Ropes & Audio Guide)";
            case DEAF_HARD_OF_HEARING: return "Hearing Impairment / Deaf (KSL Sign Glosses & Visual Desk Cards)";
            case DYSLEXIA: return "Dyslexia & Reading Difficulties (Symbolic Storyboards & Color Cues)";
            case DYSCALCULIA: return "Dyscalculia (Concrete CPA, Dot Math & Ten-Frames)";
            case ADHD_EXECUTIVE_FUNCTION: return "ADHD & Attention Focus (3-Min Micro-Intervals & Kinesthetic Roles)";
            case MOTOR_PHYSICAL: return "Motor / Physical Challenges (Adapted Grips & Peer Scribing)";
            default: return "Universal Inclusive (Mixed Abilities & Differentiated Scaffolding)";
        }
    }
}
