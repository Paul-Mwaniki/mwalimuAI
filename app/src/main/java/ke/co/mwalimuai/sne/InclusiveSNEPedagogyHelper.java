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
                sb.append("   • Assessed through Kenya Sign Language (KSL) mathematical signing, physical manipulative setup with ")
                        .append(localRes).append(", and visual exercise book diagrams. Spoken auditory language is NOT assessed.\n");
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

    public static String getKslShortLabel(String subStrand) {
        String s = subStrand != null ? subStrand.toLowerCase(Locale.ROOT) : "";
        if (s.contains("area")) return "ENEO PEMBETATU";
        if (s.contains("fraction")) return "SEHEMU";
        if (s.contains("decimal")) return "DESIMALI";
        if (s.contains("multipl")) return "ZIDISHA";
        if (s.contains("divis")) return "GAWA";
        if (s.contains("length")) return "UREFU NA MZUNGUKO";
        if (s.contains("capacity")) return "UJAZO";
        if (s.contains("mass")) return "UZITO";
        if (s.contains("money")) return "FEDHA";
        return "HISABATI";
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

    public static String buildSNELessonTitle(SNEProfile profile, String subStrand, String pages, String contextDesc) {
        String baseName = capitalize(subStrand);
        switch (profile) {
            case DEAF_HARD_OF_HEARING:
                return "SNE (Hearing Impairment / KSL) — Grade 6 " + baseName + " (pp. " + pages + ")";
            case BLIND_LOW_VISION:
                return "SNE (Visual Impairment / Tactile) — Grade 6 " + baseName + " (pp. " + pages + ")";
            case DYSLEXIA:
                return "SNE (Dyslexia / Visual Storyboard) — Grade 6 " + baseName + " (pp. " + pages + ")";
            case DYSCALCULIA:
                return "SNE (Dyscalculia / CPA Concrete) — Grade 6 " + baseName + " (pp. " + pages + ")";
            case ADHD_EXECUTIVE_FUNCTION:
                return "SNE (ADHD / Micro-Interval Focus) — Grade 6 " + baseName + " (pp. " + pages + ")";
            case MOTOR_PHYSICAL:
                return "SNE (Motor Adaptation / Peer Scribe) — Grade 6 " + baseName + " (pp. " + pages + ")";
            default:
                return "SNE Lesson Plan: Grade 6 " + baseName + " (pp. " + pages + ")";
        }
    }

    public static String buildSNELearningOutcomes(
            SNEProfile profile,
            String subStrand,
            String localRes,
            String contextDesc,
            int durationMinutes,
            int squadSize) {

        StringBuilder sb = new StringBuilder();
        sb.append("By the end of the ").append(durationMinutes).append("-minute SNE lesson, the learner with ")
                .append(getProfileTargetName(profile)).append(" should be able to:\n\n");

        switch (profile) {
            case DEAF_HARD_OF_HEARING:
                sb.append("1. Kenya Sign Language (KSL) & Mathematical Vocabulary:\n")
                        .append("   Identify, understand, and sign mathematical concepts for ").append(subStrand)
                        .append(" in KSL (").append(getKslMathGloss(subStrand)).append(").\n\n")
                        .append("2. Visual & Concrete Spatial Modeling:\n")
                        .append("   In visual pods of ").append(squadSize)
                        .append(" learners, model and solve ").append(subStrand)
                        .append(" mathematical problems using high-contrast chalkboard diagrams, visual drawings, and geometric representations without auditory reliance.\n\n")
                        .append("3. Peer Signing & Formative Verification:\n")
                        .append("   Communicate, verify, and defend mathematical solutions with desk partners in KSL using 3-color desk action cards.");
                break;

            case BLIND_LOW_VISION:
                sb.append("1. Tactile Concept Recognition:\n")
                        .append("   Identify boundaries, dimensions, and operations of ").append(subStrand)
                        .append(" by touch using pre-knotted ropes and embossed edges.\n\n")
                        .append("2. Kinesthetic Manipulation:\n")
                        .append("   Count knots and feel vertex landmarks to calculate values without needing chalkboard sight.\n\n")
                        .append("3. Oral Mathematical Defense:\n")
                        .append("   Explain the mathematical rule verbally or in braille to squad members.");
                break;

            case DYSLEXIA:
                sb.append("1. Symbolic & Iconic Comprehension:\n")
                        .append("   Interpret ").append(subStrand).append(" through color-coded number lines and picture storyboards, bypassing dense text paragraphs.\n\n")
                        .append("2. Concrete Representation:\n")
                        .append("   Model the problem and record numerical values in color-coded columns.\n\n")
                        .append("3. Functional Application:\n")
                        .append("   Apply mathematical calculation accurately without penalty for spelling or letter-reversal.");
                break;

            case DYSCALCULIA:
                sb.append("1. Concrete CPA Number Experience:\n")
                        .append("   Physically manipulate visual counters to experience quantity and area before any abstract formula is shown.\n\n")
                        .append("2. Ten-Frame / Dot Array Calculation:\n")
                        .append("   Use permanent desk ten-frames and dot arrays to solve ").append(subStrand).append(" without working-memory strain.\n\n")
                        .append("3. Conceptual Confidence:\n")
                        .append("   Demonstrate why the mathematical rule works using concrete items.");
                break;

            default:
                sb.append("1. Adapted SNE Concept Exploration:\n")
                        .append("   Demonstrate ").append(subStrand).append(" using adapted multisensory pathways (VAKT).\n\n")
                        .append("2. Hands-on Manipulation with ").append(capitalize(localRes)).append(":\n")
                        .append("   Work collaboratively in peer pods using ").append(localRes).append(".\n\n")
                        .append("3. Functional Competency:\n")
                        .append("   Demonstrate core CBC competencies and mutual support.");
                break;
        }

        return sb.toString();
    }

    public static String buildSNEResourcesNeeded(
            SNEProfile profile,
            String subStrand,
            String localRes,
            String contextDesc,
            int squadCount,
            int squadSize,
            String pages) {

        StringBuilder sb = new StringBuilder();

        switch (profile) {
            case DEAF_HARD_OF_HEARING:
                sb.append("▪ SNE Assistive & Visual Teaching Materials:\n")
                        .append("   1. KSL Mathematical Dictionary & Wall Posters (Signs for ").append(getKslShortLabel(subStrand)).append(").\n")
                        .append("   2. 3-Color Desk Action Indicator Cards (🟢 Green: Calculating, 🟡 Yellow: Peer Check, 🔴 Red: Teacher Demo).\n")
                        .append("   3. High-contrast chalkboard markers/chalk (Yellow, White, Green) for boxed visual steps.\n")
                        .append("   4. Geometric drawing tools, rulers, and learner mathematics exercise books.\n\n")
                        .append("▪ SNE Classroom Seating & Sightline Setup for ").append(squadCount * squadSize).append(" Learners:\n")
                        .append("   1. Arrange desks in a semi-circular horseshoe layout facing the teacher with ample natural front-lighting to allow clear lip-reading and unobstructed sightlines for signing.\n")
                        .append("   2. Organize into ").append(squadCount).append(" visual pods of ").append(squadSize)
                        .append(" learners, pairing each deaf learner with a KSL-proficient signing buddy.\n")
                        .append("   3. Reference: KICD Grade 6 Mathematics Curriculum Design for Learners with Hearing Impairment (pp. ").append(pages).append(").");
                break;

            case BLIND_LOW_VISION:
                sb.append("▪ SNE Tactile & Audio Materials:\n")
                        .append("   1. Pre-knotted ropes/strings (knots tied at 5 cm intervals) and embossed cardboard cartons with dried PVA glue vertices.\n")
                        .append("   2. Braille geometric shapes, wooden ruler with raised tactile notches.\n")
                        .append("   3. Mwalimu SNE AI Offline Audio Assistant (Text-to-Speech) for hands-free audio review.\n\n")
                        .append("▪ SNE Classroom Safety & Orientation Setup:\n")
                        .append("   1. Keep classroom pathways completely clear of bags and clutter to ensure safe navigation.\n")
                        .append("   2. Pair each learner with a consistent sighted peer guide.");
                break;

            default:
                sb.append("▪ SNE Specialized Classroom Materials:\n")
                        .append("   1. Concrete manipulatives: ").append(localRes != null && !localRes.trim().isEmpty() ? localRes : "visual counters").append(" in shallow desk-rim organizer trays.\n")
                        .append("   2. Visual storyboards, color-coded calculation sheets, and Ten-Frame desk anchors.\n\n")
                        .append("▪ SNE Pod Arrangement:\n")
                        .append("   Divide into ").append(squadCount).append(" structured pods of ").append(squadSize)
                        .append(" learners with explicit peer-support roles (Strategist, Material Custodian, Scribe).");
                break;
        }

        return sb.toString();
    }

    public static String buildSNEIntroduction(
            SNEProfile profile,
            String subStrand,
            String localRes,
            String contextDesc,
            String workedExample,
            int introMins,
            String customHookText) {

        StringBuilder sb = new StringBuilder();
        sb.append("⏱ Time Allocation: ").append(introMins).append(" Minutes (Targeted SNE Methodology)\n");
        sb.append("────────────────────────────────────────\n");

        boolean hasCustom = customHookText != null && !customHookText.trim().isEmpty();

        switch (profile) {
            case DEAF_HARD_OF_HEARING:
                sb.append("Step 1 — Visual Attention Signal (1 Min):\n")
                        .append("   • Flick the classroom light switch twice or raise your hand with the standard KSL attention wave.\n")
                        .append("   • Strictly avoid verbal/auditory shouting. Ensure 100% of learners have direct sightlines to the teacher.\n");
                if (hasCustom) {
                    sb.append("   • Teacher Note: \"").append(customHookText.trim()).append("\"\n");
                }
                sb.append("\nStep 2 — KSL Vocabulary Demonstration (2 Mins):\n")
                        .append("   • Write the mathematical term inside a high-contrast chalkboard box.\n")
                        .append("   • Simultaneously sign and point: ").append(getKslMathGloss(subStrand)).append(". Learners mirror the signs with both hands.\n\n")
                        .append("Step 3 — Visual Key Inquiry Question (2 Mins):\n")
                        .append("   • Display the chalkboard visual problem diagram:\n")
                        .append("     \"").append(workedExample).append("\"\n")
                        .append("   • Sign the Key Inquiry Question: \"How do visual diagrams and mathematical formulas help us solve this accurately?\"");
                break;

            case BLIND_LOW_VISION:
                sb.append("Step 1 — Tactile Realia Exploration (2 Mins):\n")
                        .append("   • Hand each learner a tactile sample to explore by touch.\n");
                if (hasCustom) {
                    sb.append("   • Teacher Hook: \"").append(customHookText.trim()).append("\"\n");
                }
                sb.append("\nStep 2 — Audio-Described Scenario Hook (2 Mins):\n")
                        .append("   • Audio-describe the ").append(subStrand).append(" scenario clearly: \"").append(workedExample).append("\"\n\n")
                        .append("Step 3 — Tactile Inquiry Question (1 Min):\n")
                        .append("   • Ask learners to feel the edges: \"How can counting the knots give us the exact measurement?\"");
                break;

            default:
                sb.append("Step 1 — Concrete Hook & Visual Connection (2 Mins):\n");
                if (hasCustom) {
                    sb.append("   • Teacher Hook Focus: \"").append(customHookText.trim()).append("\"\n");
                }
                sb.append("   • Introduce ").append(subStrand).append(" using concrete desk representations.\n\n")
                        .append("Step 2 — Multisensory Demonstration (2 Mins):\n")
                        .append("   • Display: \"").append(workedExample).append("\"\n\n")
                        .append("Step 3 — Key Inquiry Question (1 Min):\n")
                        .append("   • Engage learners in observing how physical visual manipulation solves the challenge.");
                break;
        }

        return sb.toString();
    }

    public static String buildSNELessonDevelopment(
            SNEProfile profile,
            String subStrand,
            String localRes,
            String contextDesc,
            String workedExample,
            String concreteHowTo,
            String representationalTask,
            String guidedPracticeProblem,
            String exitTicketQuestion,
            String exitTicketSolution,
            String slmGeneratedSteps,
            int squadCount,
            int squadSize,
            int step1Mins,
            int step2Mins,
            int exitMins,
            String customHookText) {

        StringBuilder sb = new StringBuilder();
        String divider = "\n────────────────────────────────────────\n";

        switch (profile) {
            case DEAF_HARD_OF_HEARING:
                // STAGE 1: Silent Visual & Concrete Modeling (KSL)
                sb.append("STAGE 1: SILENT VISUAL & CONCRETE MODELING (KSL) (").append(step1Mins).append(" MINS)\n");
                sb.append("▪ Focus: ").append(subStrand).append(" Visual & KSL Modeling\n");
                sb.append("▪ Teaching Method: Silent Modeling & KSL Signing | Grouping: ").append(squadCount).append(" Visual Pods of ").append(squadSize).append(" Learners\n");
                sb.append("▪ Assistive Aids: High-Contrast Chalkboard + 3-Color Desk Action Cards\n\n");

                sb.append("Step 1 — Horseshoe Layout & Pod Sightlines Check (2 Mins):\n");
                sb.append("   • Verify that all ").append(squadCount).append(" visual pods in the semi-circular horseshoe layout have unobstructed sightlines to the teacher's signing hands and facial expressions.\n");
                sb.append("   • Pod Material Custodians collect drawing rulers and exercise books silently from the front tray.\n\n");

                sb.append("Step 2 — High-Contrast Board Diagram & KSL Concept Demonstration (3 Mins):\n");
                if (customHookText != null && !customHookText.trim().isEmpty()) {
                    sb.append("   • Teacher Note: \"").append(customHookText.trim()).append("\"\n");
                }
                sb.append("   • Write the scenario problem inside a high-contrast chalkboard box: ").append(workedExample).append("\n");
                sb.append("   • Demonstrate and sign the mathematical concept in Kenya Sign Language (KSL): ").append(getKslMathGloss(subStrand)).append(".\n");
                sb.append("   • Learners mirror the signs with both hands to reinforce mathematical vocabulary.\n\n");

                sb.append("Step 3 — Practical Visual Modeling & Spatial Demonstration (5 Mins):\n");
                if (slmGeneratedSteps != null && !slmGeneratedSteps.trim().isEmpty()) {
                    sb.append("   ▪ On-Device AI Adaptive Visual Steps (Adapted for ").append(squadCount * squadSize).append(" Learners):\n");
                    sb.append(formatSteps(slmGeneratedSteps)).append("\n");
                } else {
                    sb.append("   • Teacher silently demonstrates step-by-step visual calculations and spatial modeling at the board, signing key mathematical transitions in KSL.\n");
                    sb.append("   • Pod buddies mirror the visual steps on their desk drawing sheets.\n");
                }
                sb.append("\n");

                sb.append("Step 4 — Silent Diagrammatic Recording in Exercise Books (3 Mins):\n");
                sb.append("   • ").append(representationalTask).append("\n");
                sb.append("   • Learners draw the visual diagram and record numerical working without auditory distraction.\n\n");

                sb.append("Step 5 — Desk-Level Visual Card Verification (2 Mins):\n");
                sb.append("   • Teacher walks through the horseshoe pods observing 3-color desk action cards:\n");
                sb.append("     🟢 Green = Calculating independently | 🟡 Yellow = Peer checking | 🔴 Red = Requests visual demo.\n");
                sb.append("   • Teacher provides silent desk-level visual assistance to pods displaying 🔴 Red cards.\n");

                // STAGE 2: Peer Signing & Hands-On Pod Practice
                sb.append(divider);
                sb.append("STAGE 2: PEER SIGNING & HANDS-ON POD PRACTICE (").append(step2Mins).append(" MINS)\n");
                sb.append("▪ Activity Focus: ").append(subStrand).append(" — KSL Peer Practice\n");
                sb.append("▪ Teaching Method: KSL Peer Signing & Collaborative Modeling | Grouping: Visual Desk Pairs\n\n");

                sb.append("Step 1 — Chalkboard Visual Follow-Up Challenge (4 Mins):\n");
                sb.append("   • Write the follow-up problem inside a chalkboard box: ").append(guidedPracticeProblem).append("\n");
                sb.append("   • Sign the challenge in KSL to verify clarity across all pods.\n\n");

                sb.append("Step 2 — Concrete Visual Pod Modeling (5 Mins):\n");
                sb.append("   • Desk pairs display 🟢 Green cards and collaboratively solve the challenge in their exercise books.\n\n");

                sb.append("Step 3 — KSL Partner Verification & Peer Signing (3 Mins):\n");
                sb.append("   • Learners switch cards to 🟡 Yellow, exchange exercise books, and sign their mathematical reasoning to each other in KSL.\n");
                sb.append("   • Partners visually verify each other's written calculations and diagrams.\n\n");

                sb.append("Step 4 — Signed Class Demonstration (3 Mins):\n");
                sb.append("   • Invite 2 deaf learners to the front demonstration area to sign their mathematical proof in KSL for the class.\n");

                // STAGE 3: Visual Exit Ticket & Signed Demonstration
                sb.append(divider);
                sb.append("STAGE 3: VISUAL EXIT TICKET & SIGNED DEMONSTRATION (").append(exitMins).append(" MINS)\n");
                sb.append("▪ Assessment Type: Silent Individual Visual & Signed Exit Ticket\n\n");

                sb.append("Step 1 — Chalkboard Visual Exit Ticket (3 Mins):\n");
                sb.append("   • ").append(exitTicketQuestion).append("\n");
                sb.append("   • Learners independently solve and record their step-by-step mathematical working and diagrams in their books.\n\n");

                sb.append("Step 2 — Expected Model Solution (Visual Marking Key):\n");
                sb.append("   • ").append(exitTicketSolution).append("\n\n");

                sb.append("Step 3 — Silent Visual Confirmation & Wrap-Up (2 Mins):\n");
                sb.append("   • Learners raise their exercise books for a silent visual confirmation scan by the teacher.\n");
                sb.append("   • Teacher signs the KSL closing encouragement.");
                break;

            case BLIND_LOW_VISION:
                sb.append("STAGE 1: TACTILE MODELING & KINESTHETIC EXPLORATION (").append(step1Mins).append(" MINS)\n");
                sb.append("▪ Focus: ").append(subStrand).append(" Tactile Discovery (").append(capitalize(contextDesc)).append(")\n");
                sb.append("▪ Teaching Method: Tactile Demonstration & Sighted Guide Buddy | Grouping: ").append(squadCount).append(" Tactile Pods of ").append(squadSize).append("\n");
                sb.append("▪ Materials in Use: Pre-Knotted ").append(localRes).append(" & Embossed Diagrams\n\n");

                sb.append("Step 1 — Tactile Station Setup (2 Mins):\n");
                sb.append("   • Keep classroom walkways completely clear. Sighted peer guides assist blind learners to tactile pod stations.\n");
                sb.append("   • Hand out ").append(localRes).append(" with tactile notches/knots at 5 cm intervals.\n\n");

                sb.append("Step 2 — Audio-Described Scenario Hook (3 Mins):\n");
                sb.append("   • Audio-describe the problem clearly: \"").append(workedExample).append("\"\n");
                sb.append("   • Guide learners' hands along the embossed perimeter edges.\n\n");

                sb.append("Step 3 — Tactile Hands-On Modeling with ").append(capitalize(localRes)).append(" (5 Mins):\n");
                if (slmGeneratedSteps != null && !slmGeneratedSteps.trim().isEmpty()) {
                    sb.append("   ▪ On-Device AI Adaptive Tactile Steps (Freely Adapted for ").append(capitalize(localRes)).append("):\n");
                    sb.append(formatSteps(slmGeneratedSteps)).append("\n");
                } else {
                    sb.append("   ").append(concreteHowTo).append("\n");
                    sb.append("   • Blind learners count knots along ").append(localRes).append(" to measure lengths and calculate areas by touch.\n");
                }
                sb.append("\n");

                sb.append("Step 4 — Braille / Tactile Recording (3 Mins):\n");
                sb.append("   • Record values using braille slates or audio voice recording in squads.\n\n");

                sb.append("Step 5 — 1-on-1 Guided Touch Check (2 Mins):\n");
                sb.append("   • Teacher moves between pods guiding learners' fingertips over their knot counts.\n");

                sb.append(divider);
                sb.append("STAGE 2: TACTILE PAIR PRACTICE & ORAL VERIFICATION (").append(step2Mins).append(" MINS)\n");
                sb.append("▪ Focus: ").append(subStrand).append(" Tactile Peer Verification (").append(capitalize(contextDesc)).append(")\n");
                sb.append("▪ Teaching Method: Sighted Guide / Tactile Peer Collaboration\n\n");
                sb.append("Step 1 — Audio-Described Follow-Up Problem: ").append(guidedPracticeProblem).append("\n\n");
                sb.append("Step 2 — Tactile Measurement: Learners count knots on ").append(localRes).append(" to solve the problem.\n\n");
                sb.append("Step 3 — Oral Defense: Partner pairs orally explain how their tactile count verifies the calculation.\n");

                sb.append(divider);
                sb.append("STAGE 3: TACTILE EXIT TICKET & AUDITORY CLOSURE (").append(exitMins).append(" MINS)\n");
                sb.append("▪ Assessment Type: Tactile / Oral Individual Exit Ticket\n\n");
                sb.append("Step 1 — Exit Problem: ").append(exitTicketQuestion).append("\n\n");
                sb.append("Step 2 — Expected Model Solution: ").append(exitTicketSolution).append("\n\n");
                sb.append("Step 3 — Material Storage: Sighted guides and blind partners count knots and store all ").append(localRes).append(".");
                break;

            default:
                sb.append("STAGE 1: ADAPTED MULTISENSORY CONCRETE MODELING (").append(step1Mins).append(" MINS)\n");
                sb.append("▪ Focus: ").append(subStrand).append(" Adapted Hands-On Exploration (").append(capitalize(contextDesc)).append(")\n");
                sb.append("▪ Teaching Method: Concrete CPA Progression | Grouping: ").append(squadCount).append(" Pods of ").append(squadSize).append("\n");
                sb.append("▪ Materials in Use: ").append(localRes).append(" + Visual Scaffolds\n\n");

                sb.append("Step 1 — Inclusive Pod Organization & Material Distribution (2 Mins):\n");
                if (customHookText != null && !customHookText.trim().isEmpty()) {
                    sb.append("   • Teacher Focus: \"").append(customHookText.trim()).append("\"\n");
                }
                sb.append("   • Pods arranged with defined peer roles (Facilitator, Material Custodian, Scribe).\n");
                sb.append("   • Material Custodians distribute ").append(localRes).append(".\n\n");

                sb.append("Step 2 — High-Contrast Chalkboard Problem Hook (3 Mins):\n");
                sb.append("   • Problem: ").append(workedExample).append("\n");
                sb.append("   • Use color-coded chalk/markers to separate numbers and operations.\n\n");

                sb.append("Step 3 — Concrete Modeling with ").append(capitalize(localRes)).append(" (5 Mins):\n");
                if (slmGeneratedSteps != null && !slmGeneratedSteps.trim().isEmpty()) {
                    sb.append("   ▪ On-Device AI Adaptive Steps (Freely Adapted for ").append(capitalize(localRes)).append("):\n");
                    sb.append(formatSteps(slmGeneratedSteps)).append("\n");
                } else {
                    sb.append("   ").append(concreteHowTo).append("\n");
                }
                sb.append("\n");

                sb.append("Step 4 — Differentiated Representational Recording (3 Mins):\n");
                sb.append("   • ").append(representationalTask).append("\n\n");

                sb.append("Step 5 — Teacher Differentiated Check (2 Mins):\n");
                sb.append("   • Teacher circulates providing targeted prompts and praise.\n");

                sb.append(divider);
                sb.append("STAGE 2: GUIDED POD PRACTICE & PEER APPLICATION (").append(step2Mins).append(" MINS)\n");
                sb.append("▪ Focus: ").append(subStrand).append(" Guided Practice (").append(capitalize(contextDesc)).append(")\n");
                sb.append("▪ Teaching Method: Peer Collaborative Problem Solving\n\n");
                sb.append("Step 1 — Follow-Up Challenge: ").append(guidedPracticeProblem).append("\n\n");
                sb.append("Step 2 — Concrete Verification: Pods model the problem using ").append(localRes).append(".\n\n");
                sb.append("Step 3 — Peer Verification: Desk partners check each other's work.\n\n");
                sb.append("Step 4 — Pod Demonstration: Learners share solutions with the class.\n");

                sb.append(divider);
                sb.append("STAGE 3: STRUCTURED EXIT TICKET & MULTI-PATHWAY CLOSURE (").append(exitMins).append(" MINS)\n");
                sb.append("▪ Assessment Type: Multi-Pathway Individual Exit Ticket\n\n");
                sb.append("Step 1 — Exit Ticket Challenge: ").append(exitTicketQuestion).append("\n\n");
                sb.append("Step 2 — Expected Model Solution: ").append(exitTicketSolution).append("\n\n");
                sb.append("Step 3 — Collection & Wrap-Up: Return all ").append(localRes).append(" to storage trays.");
                break;
        }

        return sb.toString();
    }

    private static String formatSteps(String text) {
        if (text == null || text.trim().isEmpty()) return "";
        String cleaned = text.trim().replaceAll("\\s+(?=\\d+\\.\\s)", "\n   ");
        if (cleaned.startsWith("1.")) {
            cleaned = "   " + cleaned;
        }
        return cleaned;
    }

    private static String capitalize(String s) {
        if (s == null || s.isEmpty()) return "";
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    public static String getProfileTargetName(SNEProfile p) {
        switch (p) {
            case DEAF_HARD_OF_HEARING: return "Hearing Impairment (Deaf / Hard of Hearing)";
            case BLIND_LOW_VISION: return "Visual Impairment (Blind / Low Vision)";
            case DYSLEXIA: return "Dyslexia (Reading Difficulties)";
            case DYSCALCULIA: return "Dyscalculia (Math Difficulties)";
            case ADHD_EXECUTIVE_FUNCTION: return "ADHD (Attention & Focus Challenges)";
            case MOTOR_PHYSICAL: return "Motor / Physical Challenges";
            default: return "Special Needs Education (SNE)";
        }
    }
}
