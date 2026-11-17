package ke.co.mwalimuai.sne;

import java.util.Locale;

/**
 * Universal Design for Learning (UDL) & Special Needs Education (SNE) engine for Mwalimu SNE AI.
 * Generates evidence-based, adaptive mathematics lessons aligned with Kenya KICD SNE Curriculum Designs
 * and international accommodations for:
 * 1. Hearing Impairment / Deaf (KSL)
 * 2. Visual Impairment / Blind (Tactile Knots & Audio)
 * 3. Dyslexia (Visual Storyboards, Color Cues & Reading Windows)
 * 4. Dyscalculia (Concrete CPA, Desk Ten-Frames & Dot Math)
 * 5. ADHD & Executive Function (3-Min Micro-Sprints, Visual Timers & Kinesthetic Stations)
 * 6. Motor & Physical Challenges (20° Slant Boards, Adapted Grips & Pilot-Scribe Protocol)
 * 7. Universal Inclusive SNE (Multi-Tier Differentiated Scaffolding)
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
        } else if (lower.contains("dyscalculi") || lower.contains("number struggle") || lower.contains("math anxiety") || lower.contains("math difficult")) {
            return SNEProfile.DYSCALCULIA;
        } else if (lower.contains("adhd") || lower.contains("hyperactiv") || lower.contains("attention span") || lower.contains("focus challenge") || lower.contains("attention focus")) {
            return SNEProfile.ADHD_EXECUTIVE_FUNCTION;
        } else if (lower.contains("motor") || lower.contains("physical") || lower.contains("wheelchair") || lower.contains("cerebral") || lower.contains("grip")) {
            return SNEProfile.MOTOR_PHYSICAL;
        }
        return SNEProfile.UNIVERSAL_INCLUSIVE;
    }

    /**
     * Returns default SNE specialized resources according to the selected profile.
     */
    public static String getDefaultResources(String profileText) {
        return getDefaultResources(detectProfile(profileText));
    }

    public static String getDefaultResources(SNEProfile profile) {
        switch (profile) {
            case DEAF_HARD_OF_HEARING:
                return "KSL math posters, 3-color desk action cards, high-contrast chalkboard diagrams";
            case BLIND_LOW_VISION:
                return "Pre-knotted ropes (5cm knots), embossed cardboard boundaries, braille/audio guide";
            case DYSLEXIA:
                return "Visual math storyboards, single-line reading cut-out windows, color-coded operation charts";
            case DYSCALCULIA:
                return "Concrete CPA kit, desk Ten-Frames, double-sided counters, formula flip cards";
            case ADHD_EXECUTIVE_FUNCTION:
                return "Visual countdown timer, chunked single-problem task strips, kinesthetic floor tracks, focus fidgets";
            case MOTOR_PHYSICAL:
                return "20° wooden slant boards, triangular pencil grips, non-slip Dycem mats, rimmed organizer trays";
            default:
                return "Differentiated math manipulatives, visual task cards, multi-tier worksheets";
        }
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
                sb.append("   • Describe every chalkboard diagram verbally before writing (e.g. \"I am tracing a right-angled triangle starting from the bottom-left vertex...\").\n");
                sb.append("   • Pair with a supportive peer sighted-buddy who verbalizes intermediate calculations.\n\n");
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
                sb.append("   • Position the deaf learner in a semi-circular horseshoe layout with unobstructed direct sightlines to the teacher's face and hands.\n");
                sb.append("   • Flick light switch twice or tap desk firmly to signal stage transitions without auditory shouting.");
                break;

            case DYSLEXIA:
                sb.append("1. Symbolic & Iconic Storyboarding (Bypassing Text Density):\n");
                sb.append("   • Do NOT present math problems as dense multi-line paragraphs. Use symbolic picture cards and chalkboard icons:\n");
                sb.append("     [ 📦 Icon / Scene ] ➔ [ Operation Symbol ] ➔ [ Calculated Total = ? ]\n");
                sb.append("   • Provide designated peer reading buddies who read word problems aloud without revealing mathematical answers.\n\n");
                sb.append("2. Color-Coded Numerical Columns & Reading Windows:\n");
                sb.append("   • Write place values and formula terms in distinct chalk colors: Units in Green, Tens in Blue, Operation symbols in Yellow/Red.\n");
                sb.append("   • Provide a card reading window (a cardstock sheet with a rectangular cutout) so the learner focuses on one calculation line at a time without visual crowding.\n\n");
                sb.append("3. Low-Distraction Formatting & Assessment Accommodations:\n");
                sb.append("   • Zero penalty for spelling errors, digit reversals (6 vs 9), or letter mirroring.\n");
                sb.append("   • Reference: Kenya CBC SNE Guidelines for Specific Learning Difficulties (Dyslexia).");
                break;

            case DYSCALCULIA:
                sb.append("1. Concrete ➔ Pictorial ➔ Abstract (CPA) Strict Progression:\n");
                sb.append("   • FORBID jump-to-formula teaching. The learner must physically manipulate tactile counters and Ten-Frames before any abstract symbol is written.\n");
                sb.append("   • Provide permanent desk Ten-Frames (two rows of 5 squares) and dot arrays as external working memory anchors.\n\n");
                sb.append("2. Step-by-Step Formula Reference Cards & Sliders:\n");
                sb.append("   • Provide laminated flip cards showing single calculation sub-steps (e.g. Step 1: Count Base, Step 2: Count Height, Step 3: Divide by 2).\n");
                sb.append("   • Use tactile number line sliders (0–100) and multiplication reference tables without working-memory penalties.\n\n");
                sb.append("3. Anxiety De-escalation & Untimed Practice:\n");
                sb.append("   • Never demand rapid mental math drills; allow unhurried physical manipulation before requiring answers.\n");
                sb.append("   • Reference: KICD Teacher Guidelines for Mathematics Adaptations for Learners with Dyscalculia.");
                break;

            case ADHD_EXECUTIVE_FUNCTION:
                sb.append("1. High-Tempo 3-Minute Micro-Chunking:\n");
                sb.append("   • Break development stages into 3-minute action sprints: 3 Mins physical modeling ➔ 3 Mins partner verification ➔ 2 Mins book recording.\n");
                sb.append("   • Set a visible countdown timer or sand glass on the desk to anchor time perception.\n\n");
                sb.append("2. Kinesthetic Math Stations & Focus Fidgets:\n");
                sb.append("   • Integrate physical movement: floor number lines, measurement walking tracks, standing math stations.\n");
                sb.append("   • Provide silent tactile focus fidgets (sensory squeeze balls, textured desk grip strips) and under-bench bins for clutter-free desks.\n\n");
                sb.append("3. Active Leadership Roles & Executive Function Checklists:\n");
                sb.append("   • Appoint the learner as Squad Timekeeper or Material Captain to channel natural energy into responsible leadership.\n");
                sb.append("   • Use desk progress checklists with check-off stamps for every completed micro-task.");
                break;

            case MOTOR_PHYSICAL:
                sb.append("1. Ergonomic Slant Boards & Adapted Grips:\n");
                sb.append("   • Provide 20° wooden desktop slant boards to elevate paper to comfortable eye level and reduce postural/cervical fatigue.\n");
                sb.append("   • Equip writing tools with chunky triangular grips, weighted pens, or universal cuff hand holders.\n\n");
                sb.append("2. Non-Slip Stabilization & Rimmed Trays:\n");
                sb.append("   • Place non-slip rubber mats (Dycem / silicone mats) and shallow rimmed trays on desks to keep geometry tools and counters from sliding.\n");
                sb.append("   • Ensure minimum 1.2-meter wide, barrier-free classroom aisles for wheelchair clearance.\n\n");
                sb.append("3. 'Pilot and Scribe' Collaborative Partnership:\n");
                sb.append("   • Team the learner with a supportive peer partner: The learner with motor challenge acts as 'Lead Strategist / Pilot' (directing calculations), while the partner acts as hands-on 'Scribe'.\n");
                sb.append("   • Provide +30% extended time and accept oral defense, pointing, or voice recordings in lieu of handwritten calculations.\n");
                sb.append("   • Reference: KICD Curriculum Design for Learners with Physical Impairments.");
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
                sb.append("   • Assessed through Kenya Sign Language (KSL) mathematical signing, physical drawings, and visual exercise book diagrams. Spoken auditory language is NOT assessed.\n");
                break;
            case DYSLEXIA:
                sb.append("   • Assessed on mathematical conceptual reasoning and numerical problem solving; reading speed, spelling errors, and digit reversals are NOT penalized. Reading windows and peer reading support fully permitted.\n");
                break;
            case DYSCALCULIA:
                sb.append("   • Assessed on physical manipulative setup with Ten-Frames, concrete counters, and formula reference cards. Timed calculations and mental arithmetic drills are strictly avoided.\n");
                break;
            case ADHD_EXECUTIVE_FUNCTION:
                sb.append("   • Assessed on step-by-step problem completion during chunked micro-sprints and active station tasks. Visual checklists and physical movement options utilized without penalizing restlessness.\n");
                break;
            case MOTOR_PHYSICAL:
                sb.append("   • Assessed through oral explanation, pointing to large-print cards, or peer scribing. Fine-motor drawing precision and handwriting speed are NOT penalized; +30% time extension provided.\n");
                break;
            default:
                sb.append("   • Assessed through hands-on manipulation of ").append(localRes).append(", peer collaboration, and exercise book working.\n");
                break;
        }

        sb.append("────────────────────────────────────────\n");
        sb.append("▪ Inclusive 4-Tier CBC Competency Rubric:\n\n");

        sb.append("1. Exceeds Expectations (EE):\n");
        sb.append("   • Independently solves the ").append(contextDesc).append(" task accurately.\n");
        sb.append("   • Clearly explains the underlying mathematical principle (orally, in sign, through CPA models, or on slant boards) and mentors squad members.\n\n");

        sb.append("2. Meets Expectations (ME):\n");
        sb.append("   • Correctly models the ").append(subStrand).append(" problem using specialized SNE aids and arrives at the right answer with standard units.\n\n");

        sb.append("3. Approaching Expectations (AE):\n");
        sb.append("   • Correctly sets up the initial concrete representation or formula step but makes an arithmetic calculation error.\n\n");

        sb.append("4. Below Expectations (BE):\n");
        sb.append("   • Struggles to initiate material manipulation or decode task; requires direct 1-on-1 teacher coaching with adapted aids.\n\n");

        sb.append("────────────────────────────────────────\n");
        sb.append("▪ Automated Offline Remedial Prescription (For Learners at AE / BE):\n");
        sb.append("   ").append(getRemedialPrescription(profile, subStrand, localRes));

        return sb.toString();
    }

    private static String getRemedialPrescription(SNEProfile profile, String subStrand, String localRes) {
        switch (profile) {
            case BLIND_LOW_VISION:
                return "Provide 1-on-1 tactile guided touch: Guide the learner's hands over the pre-knotted knots while counting aloud together before attempting any independent problem.";
            case DEAF_HARD_OF_HEARING:
                return "Use desk action cards: Re-demonstrate the physical grouping silently at desk level, pointing to each stage of the worked chalkboard diagram in KSL.";
            case DYSLEXIA:
                return "Switch to color-coded number cards: Have the learner match physical counters directly onto large colored digit cards (Green for base, Blue for height) using a single-line reading window.";
            case DYSCALCULIA:
                return "Step back to Concrete CPA: Give the learner 10 double-sided counters on a desk Ten-Frame. Have them physically split or combine quantities before re-introducing any numerical symbols.";
            case ADHD_EXECUTIVE_FUNCTION:
                return "2-Minute Focus Sprint: Give a single short manipulative micro-task on a single card strip, provide immediate verbal praise upon completion, then advance to the next step.";
            case MOTOR_PHYSICAL:
                return "Verbal Pilot Protocol: Let the learner direct the teacher or peer assistant where to place items on the slant board (\"Place 6 units on the base line\"), affirming their conceptual mastery.";
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
                return "SNE (ADHD / Micro-Sprint Intervals) — Grade 6 " + baseName + " (pp. " + pages + ")";
            case MOTOR_PHYSICAL:
                return "SNE (Motor Adaptation / Pilot & Scribe) — Grade 6 " + baseName + " (pp. " + pages + ")";
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
                sb.append("1. Symbolic Storyboard & Visual Problem Decoding:\n")
                        .append("   Interpret and decode mathematical problems for ").append(subStrand)
                        .append(" through pictorial storyboards, icon cards, and color-coded operation indicators (+ in green, − in red, × in blue), bypassing dense reading passages.\n\n")
                        .append("2. Color-Coded Numerical Modeling & Calculation:\n")
                        .append("   Model mathematical procedures using reading cutout windows and align values in color-coded place-value columns (Units = Green, Tens = Blue, Hundreds = Orange) to eliminate visual tracking strain and digit reversals.\n\n")
                        .append("3. Peer Reader Collaboration & Formative Defense:\n")
                        .append("   Collaboratively verify and defend mathematical solutions with designated reading buddies without penalty for spelling errors or letter orientation.");
                break;

            case DYSCALCULIA:
                sb.append("1. Concrete CPA (Concrete-Pictorial-Abstract) Manipulation:\n")
                        .append("   Physically construct, group, and manipulate concrete objects (double-sided counters, base-10 blocks) to directly experience quantity, place value, and operations before viewing abstract formulas.\n\n")
                        .append("2. Desk Ten-Frame & Dot Array Representation:\n")
                        .append("   In hands-on CPA math stations of ").append(squadSize)
                        .append(" learners, map physical counters onto permanent desk Ten-Frames and grid paper to solve ").append(subStrand)
                        .append(" problems visually without taxing working memory.\n\n")
                        .append("3. Low-Anxiety Conceptual Verification:\n")
                        .append("   Explain and demonstrate why the mathematical rule works using step-by-step formula reference flip cards and physical arrays without timed mental math pressure.");
                break;

            case ADHD_EXECUTIVE_FUNCTION:
                sb.append("1. High-Tempo Micro-Chunked Problem Solving:\n")
                        .append("   Complete ").append(subStrand).append(" mathematical operations in focused 3-minute action sprints using visual countdown timers and chunked single-problem task strips.\n\n")
                        .append("2. Kinesthetic Station Modeling & Concrete Action:\n")
                        .append("   Participate actively in hands-on mathematical modeling through designated movement stations (floor number lines, spatial pacing) to channel physical energy into math comprehension.\n\n")
                        .append("3. Executive Function Self-Regulation & Team Leadership:\n")
                        .append("   Fulfill assigned squad leadership responsibilities (Timekeeper, Material Captain, or Step Presenter) using desk progress checklists and non-verbal check-in cues.");
                break;

            case MOTOR_PHYSICAL:
                sb.append("1. Ergonomic Tool & Slant Board Access:\n")
                        .append("   Engage with ").append(subStrand).append(" mathematical concepts using adapted writing aids (20° desktop slant boards, chunky triangular pencil grips, universal cuffs, and rimmed non-slip trays) to minimize physical fatigue.\n\n")
                        .append("2. Pilot-and-Scribe Cooperative Strategy:\n")
                        .append("   Serve as 'Lead Strategist / Pilot' in cooperative pairs of ").append(squadSize)
                        .append(" learners, directing calculation steps, measurement decisions, and formula applications while a designated peer partner acts as hands-on scribe.\n\n")
                        .append("3. Multi-Pathway Formative Defense:\n")
                        .append("   Defend and demonstrate mathematical solutions through alternative assessment pathways (oral explanation, pointing to enlarged option cards, or audio recording) with adjusted time pacing (+30%).");
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

            case DYSLEXIA:
                sb.append("▪ SNE Assistive Dyslexia & Visual Reading Accommodations:\n")
                        .append("   1. Visual Math Storyboard cards and pictorial problem icon strips (bypassing text-dense word problems).\n")
                        .append("   2. Coloured reading overlay strips and cut-out reading windows (yellow/blue tinted cardstock) to isolate single calculation lines.\n")
                        .append("   3. Color-coded chalkboard chalk/markers: Green (Units/Addition), Blue (Tens/Multiplication), Orange (Hundreds/Formulas), Red (Subtraction).\n")
                        .append("   4. Large-print, sans-serif step-by-step calculation reference cards with extra spacing and wide-ruled exercise books.\n\n")
                        .append("▪ SNE Classroom Seating & Peer Reader Setup for ").append(squadCount * squadSize).append(" Learners:\n")
                        .append("   1. Organize desks into ").append(squadCount).append(" paired reading pods of ").append(squadSize)
                        .append(" learners, pairing learners with dyslexia with supportive peer reading buddies who read scenarios aloud.\n")
                        .append("   2. Ensure clear sightlines to the board in glare-free lighting to minimize visual stress and perceptual crowding.\n")
                        .append("   3. Reference: KICD SNE Curriculum Guidelines for Specific Learning Difficulties (Dyslexia pp. ").append(pages).append(").");
                break;

            case DYSCALCULIA:
                sb.append("▪ SNE Concrete CPA & Working Memory Assistive Materials:\n")
                        .append("   1. Concrete CPA Kit: Permanent desk Ten-Frames, double-sided counters, base-10 counting blocks, and bead string racks.\n")
                        .append("   2. Tactile number line sliders (0–100) and laminated multiplication/arithmetic fact reference grids.\n")
                        .append("   3. Step-by-Step Formula & Operation Flip Cards (visual cue cards showing one sub-step at a time).\n")
                        .append("   4. Shallow desk-rim manipulative trays to keep counting materials securely organized and prevent drops.\n\n")
                        .append("▪ SNE Classroom Setup & Anxiety-Free Environment for ").append(squadCount * squadSize).append(" Learners:\n")
                        .append("   1. Organize into ").append(squadCount).append(" hands-on CPA math stations of ").append(squadSize)
                        .append(" learners, ensuring each learner has their own physical counter set within arm's reach.\n")
                        .append("   2. Implement a strictly unhurried, time-pressure-free learning environment (no rapid mental math call-outs).\n")
                        .append("   3. Reference: KICD SNE Teacher Guidelines for Mathematics Adaptations for Learners with Dyscalculia (pp. ").append(pages).append(").");
                break;

            case ADHD_EXECUTIVE_FUNCTION:
                sb.append("▪ SNE Focus, Regulation & Kinesthetic Materials:\n")
                        .append("   1. Visual Countdown Timers (large desktop sand timers or visible classroom digital countdowns for 3-minute micro-sprints).\n")
                        .append("   2. Tactile focus fidgets (silent sensory squeeze balls, textured desk grip strips, elastic desk-leg kick bands).\n")
                        .append("   3. Chunked task strips: single-problem calculation cards (only 1 problem visible at a time to prevent cognitive overload).\n")
                        .append("   4. Kinesthetic floor measurement track / active station cards taped on classroom pathways.\n")
                        .append("   5. Executive Function Progress Checklist cards with desk check-off stamps.\n\n")
                        .append("▪ SNE Classroom Movement & Workstation Setup for ").append(squadCount * squadSize).append(" Learners:\n")
                        .append("   1. Organize into ").append(squadCount).append(" low-distraction workstation pods of ").append(squadSize)
                        .append(" learners, arranged with clear perimeter walking tracks for movement-based math tasks.\n")
                        .append("   2. Keep desk surfaces 100% free of non-lesson clutter (bags, extra books placed in under-bench bins).\n")
                        .append("   3. Reference: Evidence-Based Executive Function and Attention Scaffolding Frameworks (pp. ").append(pages).append(").");
                break;

            case MOTOR_PHYSICAL:
                sb.append("▪ SNE Ergonomic, Adaptive & Assistive Tools:\n")
                        .append("   1. Ergonomic writing aids: 20-degree wooden desktop slant boards, chunky triangular pencil grips, weighted pens, and universal cuff holders.\n")
                        .append("   2. Non-slip rubber mats (Dycem / silicone desk pads) and rimmed organizing trays to keep geometric tools and counters firmly stabilized.\n")
                        .append("   3. Enlarged chunky geometric manipulatives, magnetic numbers on metallic desk boards, and large-key calculators.\n")
                        .append("   4. Multi-pathway response materials: large-print multiple-choice indicator cards and audio/voice recording tools.\n\n")
                        .append("▪ SNE Barrier-Free Classroom Ergonomics & Pod Setup for ").append(squadCount * squadSize).append(" Learners:\n")
                        .append("   1. Ensure wide (minimum 1.2-meter) barrier-free classroom aisles with zero floor obstructions for wheelchair mobility and assistive crutch navigation.\n")
                        .append("   2. Position adaptive workstations at comfortable wheelchair/postural heights with supportive seating.\n")
                        .append("   3. Organize into ").append(squadCount).append(" 'Pilot and Scribe' cooperative pairs/pods of ").append(squadSize)
                        .append(" learners with designated peer scribes.\n")
                        .append("   4. Reference: KICD Curriculum Design & Teacher's Guide for Learners with Physical Impairments (pp. ").append(pages).append(").");
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

            case DYSLEXIA:
                sb.append("Step 1 — Visual Storyboard & Iconic Hook (2 Mins):\n")
                        .append("   • Teacher reveals a 3-part pictorial storyboard on the chalkboard: [ 📦 Picture Scenario ] ➔ [ Operation Symbol ] ➔ [ Answer Box ].\n")
                        .append("   • Teacher reads the scenario aloud in clear, expressive tones, explicitly pointing to each chalkboard icon.\n");
                if (hasCustom) {
                    sb.append("   • Teacher Hook: \"").append(customHookText.trim()).append("\"\n");
                }
                sb.append("\nStep 2 — Color-Coded Operation Highlighting (2 Mins):\n")
                        .append("   • Highlight numbers and operations in distinct colors: Green for starting units, Blue for multipliers, Yellow for formula operators.\n")
                        .append("   • Learners place single-line reading cut-out windows over their task cards.\n\n")
                        .append("Step 3 — Low-Anxiety Inquiry Question (1 Min):\n")
                        .append("   • Display: \"").append(workedExample).append("\"\n")
                        .append("   • Ask: \"Looking at our color-coded storyboard icons, what mathematical action is happening to our numbers?\"");
                break;

            case DYSCALCULIA:
                sb.append("Step 1 — Concrete CPA Realia Immersion (2 Mins):\n")
                        .append("   • Place tactile counters and desk Ten-Frames directly into learners' hands.\n")
                        .append("   • Strictly forbid writing any numbers or formulas during these first 2 minutes; learners explore physical quantity first.\n");
                if (hasCustom) {
                    sb.append("   • Teacher Hook: \"").append(customHookText.trim()).append("\"\n");
                }
                sb.append("\nStep 2 — Dot Pattern & Ten-Frame Anchor (2 Mins):\n")
                        .append("   • Teacher models the problem visually on a large chalkboard Ten-Frame using magnetic dots: \"").append(workedExample).append("\"\n")
                        .append("   • Learners mirror the dot pattern on their desk Ten-Frames, physically touching each counter as they subitize the group.\n\n")
                        .append("Step 3 — Concrete Inquiry Question (1 Min):\n")
                        .append("   • Ask: \"When we group our counters in pairs on the Ten-Frame, what pattern do our fingers discover?\"");
                break;

            case ADHD_EXECUTIVE_FUNCTION:
                sb.append("Step 1 — High-Energy Kinesthetic Hook & Timer Launch (1 Min):\n")
                        .append("   • Set the 3-minute visual sand timer / digital countdown clock on the front demonstration desk.\n")
                        .append("   • Lead a 30-second rhythmic clapping or desk-tapping count pattern to focus classroom attention and synchronize physical energy.\n");
                if (hasCustom) {
                    sb.append("   • Teacher Hook: \"").append(customHookText.trim()).append("\"\n");
                }
                sb.append("\nStep 2 — Single-Problem Visual Reveal & Active Role Assignment (2 Mins):\n")
                        .append("   • Uncover a single, bold problem card on the board: \"").append(workedExample).append("\"\n")
                        .append("   • Assign active squad responsibilities: Squad Timekeeper, Material Captain, and Step Reporter.\n\n")
                        .append("Step 3 — Micro-Sprint Inquiry Challenge (2 Mins):\n")
                        .append("   • Announce the 2-minute action sprint: \"Squad Timekeepers, you have exactly 2 minutes to get your pod counters placed and identify Step 1!\"");
                break;

            case MOTOR_PHYSICAL:
                sb.append("Step 1 — Ergonomic Station Adjustment & Posture Check (2 Mins):\n")
                        .append("   • Ensure each learner's slant board is positioned at a 20-degree angle and non-slip rubber mats are stabilizing desk materials.\n")
                        .append("   • Provide adapted triangular grips or universal cuff holders to learners needing fine-motor assistance.\n");
                if (hasCustom) {
                    sb.append("   • Teacher Hook: \"").append(customHookText.trim()).append("\"\n");
                }
                sb.append("\nStep 2 — Pilot & Scribe Role Alignment (2 Mins):\n")
                        .append("   • Formally establish the cooperative dynamic: The learner with physical challenges is designated as 'Lead Strategist / Pilot', directing all calculations; partner is designated as 'Hands-on Scribe'.\n")
                        .append("   • Present the scenario clearly on the slanted reading stand: \"").append(workedExample).append("\"\n\n")
                        .append("Step 3 — Multi-Pathway Inquiry Question (1 Min):\n")
                        .append("   • Ask the Lead Strategist to formulate the opening hypothesis verbally or by pointing: \"What measurement strategy will guide our hands-on scribe?\"");
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

            case DYSLEXIA:
                // STAGE 1: Visual Storyboard & Color-Coded Numerical Modeling
                sb.append("STAGE 1: VISUAL STORYBOARD & COLOR-CODED NUMERICAL MODELING (").append(step1Mins).append(" MINS)\n");
                sb.append("▪ Focus: ").append(subStrand).append(" — Dyslexia Visual Storyboard & Low-Reading Scaffolding\n");
                sb.append("▪ Teaching Method: Pictorial Storyboarding & Color-Coded Place Value | Grouping: ").append(squadCount).append(" Paired Reading Pods of ").append(squadSize).append(" Learners\n");
                sb.append("▪ Assistive Aids: Cut-Out Reading Windows + Color-Coded Operation Task Cards\n\n");

                sb.append("Step 1 — Reading Window Setup & Peer Buddy Alignment (2 Mins):\n");
                sb.append("   • Distribute single-line reading cut-out windows (cardstock strips with central reading slot) and colored task cards to each pod.\n");
                sb.append("   • Designated peer reading buddies read the problem scenario aloud once in clear, unhurried tones.\n\n");

                sb.append("Step 2 — Chalkboard Pictorial Storyboard Modeling (3 Mins):\n");
                if (customHookText != null && !customHookText.trim().isEmpty()) {
                    sb.append("   • Teacher Note: \"").append(customHookText.trim()).append("\"\n");
                }
                sb.append("   • Sketch the problem on the board as a 3-stage visual icon storyboard rather than a dense paragraph: ").append(workedExample).append("\n");
                sb.append("   • Circle key numbers in distinct colors: Green for Base/Starting quantities, Blue for Heights/Multipliers, and Yellow for Operation signs.\n\n");

                sb.append("Step 3 — Step-by-Step Color-Coded Calculation (5 Mins):\n");
                if (slmGeneratedSteps != null && !slmGeneratedSteps.trim().isEmpty()) {
                    sb.append("   ▪ On-Device AI Adaptive Visual Steps (Tailored for Dyslexia Scaffolding):\n");
                    sb.append(formatSteps(slmGeneratedSteps)).append("\n");
                } else {
                    sb.append("   • Teacher models calculating the problem line-by-line using color-coded columns to eliminate digit reversals and line skipping.\n");
                    sb.append("   • Learners position their reading cutout window directly over the active step on their worksheet.\n");
                }
                sb.append("\n");

                sb.append("Step 4 — Color-Coded Exercise Book Recording (3 Mins):\n");
                sb.append("   • ").append(representationalTask).append("\n");
                sb.append("   • Learners use colored pens/pencils to record numbers in aligned place-value columns, bypassing dense prose.\n\n");

                sb.append("Step 5 — Visual Column Alignment Check (2 Mins):\n");
                sb.append("   • Teacher circulates checking that numbers are strictly aligned in vertical columns and that learners are using their reading windows.\n");

                // STAGE 2: Paired Reading & Step Strip Solving
                sb.append(divider);
                sb.append("STAGE 2: PAIRED READING & SINGLE-STEP STRIP PRACTICE (").append(step2Mins).append(" MINS)\n");
                sb.append("▪ Activity Focus: ").append(subStrand).append(" — Dyslexia Paired Practice\n");
                sb.append("▪ Teaching Method: Buddy Reading & Color-Coded Calculation\n\n");

                sb.append("Step 1 — Graphic Card Challenge Reveal (4 Mins):\n");
                sb.append("   • Present the follow-up task on an icon storyboard card: ").append(guidedPracticeProblem).append("\n");
                sb.append("   • Peer buddy reads the card aloud to ensure zero reading barrier.\n\n");

                sb.append("Step 2 — Step-by-Step Card Strip Solving (5 Mins):\n");
                sb.append("   • Learners isolate and solve each arithmetic step on their chunked card strip using reading windows.\n\n");

                sb.append("Step 3 — Color-Coded Peer Verification (3 Mins):\n");
                sb.append("   • Desk partners compare their color-coded columns; no penalty is assessed for letter/number orientation errors.\n\n");

                sb.append("Step 4 — Graphic Solution Demonstration (3 Mins):\n");
                sb.append("   • A learner with dyslexia displays their visual storyboard solution card to the class, orally explaining the steps.\n");

                // STAGE 3: Low-Reading Graphic Exit Ticket
                sb.append(divider);
                sb.append("STAGE 3: LOW-READING GRAPHIC EXIT TICKET & CLOSURE (").append(exitMins).append(" MINS)\n");
                sb.append("▪ Assessment Type: Pictorial & Color-Coded Individual Exit Ticket\n\n");

                sb.append("Step 1 — Storyboard Exit Ticket (3 Mins):\n");
                sb.append("   • ").append(exitTicketQuestion).append("\n");
                sb.append("   • Learners use reading windows and color-coded digits to solve the problem in their books.\n\n");

                sb.append("Step 2 — Expected Model Solution (Color-Coded Marking Key):\n");
                sb.append("   • ").append(exitTicketSolution).append("\n\n");

                sb.append("Step 3 — Encouraging Review & Material Storage (2 Mins):\n");
                sb.append("   • Teacher validates numerical reasoning; reading windows and colored cards are collected into pod pouches.");
                break;

            case DYSCALCULIA:
                // STAGE 1: 100% Concrete CPA Modeling & Desk Ten-Frames
                sb.append("STAGE 1: 100% CONCRETE CPA MODELING & DESK TEN-FRAMES (").append(step1Mins).append(" MINS)\n");
                sb.append("▪ Focus: ").append(subStrand).append(" — Dyscalculia Concrete CPA Immersion\n");
                sb.append("▪ Teaching Method: Concrete-to-Representational-to-Abstract (CPA) | Grouping: ").append(squadCount).append(" CPA Math Stations of ").append(squadSize).append(" Learners\n");
                sb.append("▪ Assistive Aids: Desk Ten-Frames + Double-Sided Counters + Step Flip Cards\n\n");

                sb.append("Step 1 — CPA Kit Distribution & Desk Ten-Frame Alignment (2 Mins):\n");
                sb.append("   • Material Captains distribute shallow trays with double-sided counters and step-by-step formula flip cards.\n");
                sb.append("   • Learners position permanent desk Ten-Frames within comfortable physical reach.\n\n");

                sb.append("Step 2 — Tactile Quantity Building (No Formulas) (3 Mins):\n");
                if (customHookText != null && !customHookText.trim().isEmpty()) {
                    sb.append("   • Teacher Note: \"").append(customHookText.trim()).append("\"\n");
                }
                sb.append("   • Present the challenge using physical arrays: ").append(workedExample).append("\n");
                sb.append("   • Teacher demonstrates grouping quantities physically on the demonstration Ten-Frame before writing any numerical digit.\n\n");

                sb.append("Step 3 — Hands-On Concrete CPA Manipulation (5 Mins):\n");
                if (slmGeneratedSteps != null && !slmGeneratedSteps.trim().isEmpty()) {
                    sb.append("   ▪ On-Device AI Adaptive Concrete Steps (Tailored for Dyscalculia CPA Progression):\n");
                    sb.append(formatSteps(slmGeneratedSteps)).append("\n");
                } else {
                    sb.append("   • Learners physically count and place counters onto their Ten-Frames to model the mathematical relationships.\n");
                    sb.append("   • Teacher guides learners to subitize (instantly see) groups of 5 and 10 to bypass mental counting overload.\n");
                }
                sb.append("\n");

                sb.append("Step 4 — Representational Dot Mapping (3 Mins):\n");
                sb.append("   • ").append(representationalTask).append("\n");
                sb.append("   • Learners draw dots inside printed 10-frame grids in their exercise books before writing abstract formulas.\n\n");

                sb.append("Step 5 — Untimed Concrete Check (2 Mins):\n");
                sb.append("   • Teacher checks that every learner has modeled the quantity physically with zero time-pressure or mental anxiety.\n");

                // STAGE 2: Representational Dot-to-Grid Pod Practice
                sb.append(divider);
                sb.append("STAGE 2: REPRESENTATIONAL DOT-TO-GRID POD PRACTICE (").append(step2Mins).append(" MINS)\n");
                sb.append("▪ Activity Focus: ").append(subStrand).append(" — Dyscalculia Station Practice\n");
                sb.append("▪ Teaching Method: Manipulative Modeling & Step Flip Card Scaffolding\n\n");

                sb.append("Step 1 — Follow-Up Manipulative Problem (4 Mins):\n");
                sb.append("   • Display the concrete task card: ").append(guidedPracticeProblem).append("\n");
                sb.append("   • Learners flip their step-card to Step 1 to anchor their action.\n\n");

                sb.append("Step 2 — Physical Ten-Frame Solving (5 Mins):\n");
                sb.append("   • Pod partners work collaboratively with counters and number line sliders, verifying each step physically.\n\n");

                sb.append("Step 3 — External Memory Aid Verification (3 Mins):\n");
                sb.append("   • Learners check calculations against laminated multiplication grids without penalty for using external reference tables.\n\n");

                sb.append("Step 4 — Concrete Demonstration at the Front (3 Mins):\n");
                sb.append("   • A learner demonstrates the solution using large magnetic Ten-Frame counters on the board.\n");

                // STAGE 3: Concrete Demonstrative Exit Ticket
                sb.append(divider);
                sb.append("STAGE 3: CONCRETE DEMONSTRATIVE EXIT TICKET & CLOSURE (").append(exitMins).append(" MINS)\n");
                sb.append("▪ Assessment Type: Manipulative-Supported Individual Exit Ticket\n\n");

                sb.append("Step 1 — Exit Challenge with Counters (3 Mins):\n");
                sb.append("   • ").append(exitTicketQuestion).append("\n");
                sb.append("   • Learners model the problem using their desk counters and record the solution on their grid sheet.\n\n");

                sb.append("Step 2 — Expected Model Solution (CPA Verification Key):\n");
                sb.append("   • ").append(exitTicketSolution).append("\n\n");

                sb.append("Step 3 — Calm Material Storage & Confidence Wrap-Up (2 Mins):\n");
                sb.append("   • Return counters to shallow trays; teacher celebrates concrete strategy execution.");
                break;

            case ADHD_EXECUTIVE_FUNCTION:
                // STAGE 1: High-Tempo 3-Minute Micro-Sprints & Kinesthetic Modeling
                sb.append("STAGE 1: HIGH-TEMPO 3-MINUTE MICRO-SPRINTS & KINESTHETIC MODELING (").append(step1Mins).append(" MINS)\n");
                sb.append("▪ Focus: ").append(subStrand).append(" — ADHD High-Tempo Engagement\n");
                sb.append("▪ Teaching Method: 3-Minute Action Sprints & Kinesthetic Modeling | Grouping: ").append(squadCount).append(" Action Pods of ").append(squadSize).append(" Learners\n");
                sb.append("▪ Assistive Aids: Visual Countdown Timer + Chunked Task Strips + Progress Checklist\n\n");

                sb.append("Step 1 — Desk Clearing & Role Activation (2 Mins):\n");
                sb.append("   • Verify desk surfaces are 100% free of distracting non-lesson items.\n");
                sb.append("   • Activate Squad Roles: Timekeeper (monitors timer), Material Captain (holds single task strip), Step Reporter.\n\n");

                sb.append("Step 2 — 3-Minute Focused Problem Sprint (3 Mins):\n");
                if (customHookText != null && !customHookText.trim().isEmpty()) {
                    sb.append("   • Teacher Note: \"").append(customHookText.trim()).append("\"\n");
                }
                sb.append("   • Start the 3-minute visual timer: Teacher demonstrates ").append(workedExample).append(" with bold, dynamic chalkboard movements.\n");
                sb.append("   • Timekeeper calls out \"1 minute remaining!\" to anchor time awareness.\n\n");

                sb.append("Step 3 — Kinesthetic Movement & Hands-On Action (5 Mins):\n");
                if (slmGeneratedSteps != null && !slmGeneratedSteps.trim().isEmpty()) {
                    sb.append("   ▪ On-Device AI Adaptive Steps (High-Energy Kinesthetic Format):\n");
                    sb.append(formatSteps(slmGeneratedSteps)).append("\n");
                } else {
                    sb.append("   • Learners physically pace out dimensions on the floor measurement line or sort physical cards at standing desks.\n");
                    sb.append("   • Tactile focus fidgets are permitted for learners during focused recording.\n");
                }
                sb.append("\n");

                sb.append("Step 4 — Single-Problem Task Strip Execution (3 Mins):\n");
                sb.append("   • ").append(representationalTask).append("\n");
                sb.append("   • Learners work on a folded sheet revealing strictly one problem at a time to prevent cognitive overload.\n\n");

                sb.append("Step 5 — Rapid Visual Stamp Check (2 Mins):\n");
                sb.append("   • Teacher moves rapidly across pods giving immediate progress stamps on desk checklists for completed steps.\n");

                // STAGE 2: Active Station Practice & Movement Pods
                sb.append(divider);
                sb.append("STAGE 2: ACTIVE STATION PRACTICE & MOVEMENT PODS (").append(step2Mins).append(" MINS)\n");
                sb.append("▪ Activity Focus: ").append(subStrand).append(" — Kinesthetic Station Practice\n");
                sb.append("▪ Teaching Method: Active Station Rotation & Peer Collaboration\n\n");

                sb.append("Step 1 — Rapid Challenge Reveal (4 Mins):\n");
                sb.append("   • Unveil the station challenge card: ").append(guidedPracticeProblem).append("\n");
                sb.append("   • Launch a fresh 4-minute visual timer countdown.\n\n");

                sb.append("Step 2 — Active Pod Solving (5 Mins):\n");
                sb.append("   • Learners solve the challenge standing or using station whiteboards, alternating recording roles.\n\n");

                sb.append("Step 3 — High-Five Peer Verification (3 Mins):\n");
                sb.append("   • Pod members verify each other's calculations and complete high-fives upon mutual accuracy.\n\n");

                sb.append("Step 4 — 60-Second Flash Demonstration (3 Mins):\n");
                sb.append("   • Step Reporters deliver a rapid 60-second summary of their pod's mathematical proof.\n");

                // STAGE 3: Chunked Rapid-Fire Exit Ticket
                sb.append(divider);
                sb.append("STAGE 3: CHUNKED RAPID-FIRE EXIT TICKET & PROGRESS REWARD (").append(exitMins).append(" MINS)\n");
                sb.append("▪ Assessment Type: Chunked Single-Problem Individual Exit Ticket\n\n");

                sb.append("Step 1 — Single-Problem Exit Strip (3 Mins):\n");
                sb.append("   • ").append(exitTicketQuestion).append("\n");
                sb.append("   • Learners complete the single task strip under a final 3-minute visual countdown timer.\n\n");

                sb.append("Step 2 — Expected Model Solution (Rapid Scoring Key):\n");
                sb.append("   • ").append(exitTicketSolution).append("\n\n");

                sb.append("Step 3 — Progress Reward & Transition Reset (2 Mins):\n");
                sb.append("   • Teacher stamps completed task strips; learners perform a 30-second classroom stretch reset.");
                break;

            case MOTOR_PHYSICAL:
                // STAGE 1: Ergonomic Slant Board & Manipulative Modeling
                sb.append("STAGE 1: ERGONOMIC SLANT BOARD & MANIPULATIVE MODELING (").append(step1Mins).append(" MINS)\n");
                sb.append("▪ Focus: ").append(subStrand).append(" — Ergonomic Physical Adaptation\n");
                sb.append("▪ Teaching Method: Adapted Grips & Stabilized Manipulatives | Grouping: ").append(squadCount).append(" Pilot-Scribe Pairs of ").append(squadSize).append(" Learners\n");
                sb.append("▪ Assistive Aids: 20° Desktop Slant Boards + Non-Slip Dycem Mats + Chunky Grips\n\n");

                sb.append("Step 1 — Ergonomic Station Adjustment (2 Mins):\n");
                sb.append("   • Verify 20° slant boards are positioned at comfortable postural heights and non-slip mats are placed under notebooks.\n");
                sb.append("   • Confirm chunky triangular grips or universal cuffs are fitted to writing instruments.\n\n");

                sb.append("Step 2 — Accessible Chalkboard Demonstration (3 Mins):\n");
                if (customHookText != null && !customHookText.trim().isEmpty()) {
                    sb.append("   • Teacher Note: \"").append(customHookText.trim()).append("\"\n");
                }
                sb.append("   • Present ").append(workedExample).append(" with large, high-visibility numerals positioned at comfortable eye-level.\n");
                sb.append("   • Provide large-print reference cards directly on learners' slant boards to avoid constant head-raising fatigue.\n\n");

                sb.append("Step 3 — Practical Modeling with Stabilized Manipulatives (5 Mins):\n");
                if (slmGeneratedSteps != null && !slmGeneratedSteps.trim().isEmpty()) {
                    sb.append("   ▪ On-Device AI Adaptive Steps (Ergonomically Stabilized Format):\n");
                    sb.append(formatSteps(slmGeneratedSteps)).append("\n");
                } else {
                    sb.append("   • Learners explore large chunky geometric solids and magnetic numbers inside rimmed desk trays.\n");
                    sb.append("   • Teacher demonstrates manipulation using minimal fine-motor grip force.\n");
                }
                sb.append("\n");

                sb.append("Step 4 — Pilot-and-Scribe Collaborative Recording (3 Mins):\n");
                sb.append("   • ").append(representationalTask).append("\n");
                sb.append("   • The 'Pilot' (learner with motor challenges) directs calculations verbally or via pointer; the peer 'Scribe' records in the notebook.\n\n");

                sb.append("Step 5 — Postural Comfort & Fatigue Check (2 Mins):\n");
                sb.append("   • Teacher checks learner posture, releasing hand tension and offering micro-rest breaks.\n");

                // STAGE 2: Pilot-and-Scribe Cooperative Pod Practice
                sb.append(divider);
                sb.append("STAGE 2: PILOT-AND-SCRIBE COOPERATIVE POD PRACTICE (").append(step2Mins).append(" MINS)\n");
                sb.append("▪ Activity Focus: ").append(subStrand).append(" — Cooperative Pilot & Scribe Practice\n");
                sb.append("▪ Teaching Method: Pilot Strategy & Peer Scribing\n\n");

                sb.append("Step 1 — Slant Board Challenge Presentation (4 Mins):\n");
                sb.append("   • Place the task card on the slant board: ").append(guidedPracticeProblem).append("\n");
                sb.append("   • Pilot articulates the overall solution strategy to the Scribe.\n\n");

                sb.append("Step 2 — Pilot-Directed Collaborative Problem Solving (5 Mins):\n");
                sb.append("   • The pair collaborates: Pilot calculates and instructs; Scribe executes geometric drawings and records working.\n\n");

                sb.append("Step 3 — Multi-Pathway Verification (3 Mins):\n");
                sb.append("   • Pilot uses large-format indicator cards or verbal explanation to verify intermediate steps.\n\n");

                sb.append("Step 4 — Adaptive Demonstration (3 Mins):\n");
                sb.append("   • Pilot and Scribe present their work together: Pilot explains reasoning; Scribe holds the slant board display.\n");

                // STAGE 3: Multi-Pathway (Oral / Pointing / Scribe) Exit Ticket
                sb.append(divider);
                sb.append("STAGE 3: MULTI-PATHWAY (ORAL / POINTING / SCRIBE) EXIT TICKET (").append(exitMins).append(" MINS)\n");
                sb.append("▪ Assessment Type: Multi-Pathway Accessible Exit Ticket (+30% Time Standard)\n\n");

                sb.append("Step 1 — Accessible Exit Challenge (3 Mins):\n");
                sb.append("   • ").append(exitTicketQuestion).append("\n");
                sb.append("   • Pilot provides oral response, uses eye-pointing to large cards, or dictates to peer scribe without writing fatigue.\n\n");

                sb.append("Step 2 — Expected Model Solution (Accessible Scoring Key):\n");
                sb.append("   • ").append(exitTicketSolution).append("\n\n");

                sb.append("Step 3 — Material Collection & Posture Rest (2 Mins):\n");
                sb.append("   • Store adapted grips and slant boards; allow 60 seconds of gentle wrist and shoulder relaxation.");
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
            case DYSCALCULIA: return "Dyscalculia (Math Learning Difficulties)";
            case ADHD_EXECUTIVE_FUNCTION: return "ADHD & Executive Function (Attention Focus Challenges)";
            case MOTOR_PHYSICAL: return "Motor / Physical Challenges (Physical Impairments)";
            default: return "Special Needs Education (SNE)";
        }
    }
}