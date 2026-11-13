package ke.co.mwalimuai.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import ke.co.mwalimuai.database.CurriculumEntity;

/**
 * Breaks down the KICD Grade 6 Mathematics Curriculum Design (Pages 13-70)
 * into an easy-to-understand offline format with page-by-page summaries,
 * specific learning outcomes, key inquiry questions, and practical classroom guidance.
 */
public class CurriculumBreakdownHelper {

    public static final String OFFLINE_CURRICULUM_DRIVE_URL =
            "https://drive.google.com/file/d/1ki1N1YnslIpZomG-0IoYogkzek7CKx0j/view?pli=1";

    public static class PageBreakdown {
        public final String pageRange;
        public final String topicTitle;
        public final String simplifiedExplanation;
        public final String quickActivityIdea;

        public PageBreakdown(String pageRange, String topicTitle, String simplifiedExplanation, String quickActivityIdea) {
            this.pageRange = pageRange;
            this.topicTitle = topicTitle;
            this.simplifiedExplanation = simplifiedExplanation;
            this.quickActivityIdea = quickActivityIdea;
        }
    }

    public static class SubStrandBreakdown {
        public final String subStrandCode;
        public final List<String> learningOutcomes;
        public final List<String> keyInquiryQuestions;
        public final List<PageBreakdown> pages;
        public final String coreCompetencies;
        public final String valuesIntegrated;

        public SubStrandBreakdown(
                String subStrandCode,
                List<String> learningOutcomes,
                List<String> keyInquiryQuestions,
                List<PageBreakdown> pages,
                String coreCompetencies,
                String valuesIntegrated) {
            this.subStrandCode = subStrandCode;
            this.learningOutcomes = learningOutcomes;
            this.keyInquiryQuestions = keyInquiryQuestions;
            this.pages = pages;
            this.coreCompetencies = coreCompetencies;
            this.valuesIntegrated = valuesIntegrated;
        }
    }

    public static SubStrandBreakdown getBreakdown(CurriculumEntity entity) {
        String code = entity != null && entity.getSubStrandCode() != null ? entity.getSubStrandCode().trim() : "";

        switch (code) {
            case "1.1":
                return new SubStrandBreakdown(
                        "1.1",
                        Arrays.asList(
                                "Identify place value and total value of digits up to millions using place value charts.",
                                "Read and write numbers up to millions in symbols and in words.",
                                "Order and compare numbers up to 100,000 in real-life situations.",
                                "Round off numbers up to 100,000 to the nearest thousand and ten thousand.",
                                "Compute squares of whole numbers up to 100 and square roots up to 10,000."
                        ),
                        Arrays.asList(
                                "Where do we use large numbers up to millions in our county and country?",
                                "Why do we round off numbers when estimating school enrolment or seedling counts?"
                        ),
                        Arrays.asList(
                                new PageBreakdown("Page 13", "Place Value & Total Value up to Millions",
                                        "Learners group counters in bundles of 10s, 100s, and 1,000s and use a 7-column abacus or place value chart up to Millions.",
                                        "Draw a 7-column place value chart on the floor or desk and place stones/number cards in each column."),
                                new PageBreakdown("Page 14", "Reading & Writing Numbers in Symbols and Words",
                                        "Breaking large 6- and 7-digit numbers into periods (millions, thousands, ones) to read and write them accurately.",
                                        "Use county tree-planting or census cards; pairs read aloud the number on their partner's card."),
                                new PageBreakdown("Page 15", "Ordering & Comparing Numbers up to 100,000",
                                        "Arranging numbers in ascending (smallest to largest) and descending (largest to smallest) order.",
                                        "Give 5 learners number cards; have them physically line up from smallest to largest at the front."),
                                new PageBreakdown("Page 16", "Rounding Off to Nearest 1,000 and 10,000",
                                        "Using number lines to see which thousand or ten-thousand a number is closer to.",
                                        "Draw a chalk number line on the board/ground and hop to the nearest thousand."),
                                new PageBreakdown("Page 17", "Squares and Square Roots of Whole Numbers",
                                        "Representing square numbers as equal rows and columns (arrays) and finding the side length as the square root.",
                                        "Arrange 16, 25, or 36 bottle tops into equal square grids to discover squares and square roots.")
                        ),
                        "Critical thinking & problem solving; Communication & collaboration",
                        "Unity, Integrity, and Responsibility when sharing counting materials"
                );

            case "1.2":
                return new SubStrandBreakdown(
                        "1.2",
                        Arrays.asList(
                                "Multiply up to a 4-digit number by a 2-digit number accurately.",
                                "Estimate products by rounding off factors to the nearest ten or hundred.",
                                "Create and solve multiplication patterns in real-life contexts."
                        ),
                        Arrays.asList(
                                "How does estimating products help traders at the local market?",
                                "How can we break a 2-digit multiplier into tens and ones to multiply easily?"
                        ),
                        Arrays.asList(
                                new PageBreakdown("Page 18", "4-Digit by 2-Digit Multiplication",
                                        "Multiplying by expanding the 2-digit number into tens and ones (partial products) and using standard vertical multiplication.",
                                        "Calculate total seedlings in 24 crates if each crate holds 1,250 seedlings."),
                                new PageBreakdown("Page 19", "Estimating Products by Rounding Off",
                                        "Rounding each factor before multiplying to quickly check if an answer is reasonable.",
                                        "Market price estimation game: teams race to estimate the cost of 19 bags at KES 1,480 each."),
                                new PageBreakdown("Page 20", "Multiplication Number Patterns",
                                        "Identifying rules in growing number sequences and generating the next terms.",
                                        "Squads create a 4-number pattern on paper slips and challenge the next squad to find the 5th number.")
                        ),
                        "Critical thinking; Financial literacy",
                        "Honesty and fairness in market calculations"
                );

            case "1.3":
                return new SubStrandBreakdown(
                        "1.3",
                        Arrays.asList(
                                "Divide up to a 4-digit number by a 2-digit number with and without remainders.",
                                "Estimate quotients by rounding off the dividend and divisor.",
                                "Solve combined operations (BODMAS: Addition, Subtraction, Multiplication, Division)."
                        ),
                        Arrays.asList(
                                "How do we share harvest produce equally among community groups?",
                                "Why must we follow a specific order of operations when solving combined problems?"
                        ),
                        Arrays.asList(
                                new PageBreakdown("Page 21", "Multi-Digit Division (4-Digit by 2-Digit)",
                                        "Step-by-step long division relating multiplication facts to fair sharing.",
                                        "Divide 1,440 maize cobs equally among 12 village storage sacks using bundles."),
                                new PageBreakdown("Page 22", "Quotient Estimation",
                                        "Rounding the dividend and divisor to compatible numbers to estimate the quotient.",
                                        "Quick oral estimation drill before solving long division on the chalkboard."),
                                new PageBreakdown("Page 23", "Combined Operations (+, -, ×, ÷)",
                                        "Applying the correct order of operations (Division, Multiplication, Addition, Subtraction) in multi-step problems.",
                                        "Error Detective activity: learners spot and fix a calculation where addition was done before multiplication.")
                        ),
                        "Problem solving; Collaboration",
                        "Social justice and equity in fair distribution"
                );

            case "1.4":
                return new SubStrandBreakdown(
                        "1.4",
                        Arrays.asList(
                                "Find the Least Common Multiple (LCM) and add/subtract fractions using LCM.",
                                "Add and subtract mixed numbers in practical sharing situations.",
                                "Identify reciprocals of fractions and work out squares of fractions.",
                                "Convert fractions to percentages and percentages to fractions."
                        ),
                        Arrays.asList(
                                "How do we add or subtract fractions that have different denominators?",
                                "Where do we see percentages and fractions used together in daily life?"
                        ),
                        Arrays.asList(
                                new PageBreakdown("Page 24", "LCM & Adding/Subtracting Proper Fractions",
                                        "Finding a common denominator using LCM so fractions can be combined or compared.",
                                        "Fold paper circles into halves, thirds, and sixths to visually see why 1/2 + 1/3 = 5/6."),
                                new PageBreakdown("Page 25", "Adding & Subtracting Mixed Numbers",
                                        "Combining whole parts and fractional parts separately or converting to improper fractions.",
                                        "Model 2 ½ chapatis plus 1 ¼ chapatis using circular paper cutouts."),
                                new PageBreakdown("Page 26", "Reciprocals and Squares of Fractions",
                                        "Understanding reciprocals (multiplying two numbers to get 1) and squaring numerator and denominator.",
                                        "Fold a square paper in half twice to show that (1/2)² = 1/4 of the whole sheet."),
                                new PageBreakdown("Pages 27-28", "Fractions and Percentages (100-Grid)",
                                        "Expressing fractions with denominator 100 as percentages (%) and solving real-life discount/harvest problems.",
                                        "Shade 10×10 square paper grids with bottle tops to convert 1/4, 1/2, and 3/5 into percentages.")
                        ),
                        "Critical thinking; Creativity & imagination",
                        "Sharing, unity, and cooperation"
                );

            case "1.5":
                return new SubStrandBreakdown(
                        "1.5",
                        Arrays.asList(
                                "Identify place value of decimals up to ten-thousandths (4 decimal places).",
                                "Round off decimals up to 3 decimal places.",
                                "Convert decimals to fractions, fractions to decimals, and decimals to percentages.",
                                "Add and subtract decimals up to 4 decimal places."
                        ),
                        Arrays.asList(
                                "Why is accuracy in decimal places important when weighing goods or measuring medicine?",
                                "How are decimals, fractions, and percentages connected?"
                        ),
                        Arrays.asList(
                                new PageBreakdown("Page 29", "Decimal Place Value up to Ten-Thousandths",
                                        "Extending the place value chart to the right of the decimal point: tenths, hundredths, thousandths, ten-thousandths.",
                                        "Place value slider game on the chalkboard moving digits across the decimal point."),
                                new PageBreakdown("Page 30", "Rounding Off Decimals (1, 2, and 3 d.p.)",
                                        "Looking at the digit to the right to round decimals up or down in measurement contexts.",
                                        "Round off market weighing scale readings to 1 and 2 decimal places."),
                                new PageBreakdown("Pages 31-32", "Converting Between Decimals, Fractions & Percentages",
                                        "Writing decimals over 10, 100, or 1,000 and simplifying; multiplying by 100% for percentages.",
                                        "3-Way Matching Cards: squads match equivalent Fraction, Decimal, and Percentage cards (e.g., 3/4 = 0.75 = 75%)."),
                                new PageBreakdown("Page 33", "Addition and Subtraction of Decimals",
                                        "Aligning decimal points vertically before adding or subtracting up to 4 decimal places.",
                                        "Calculate combined mass of grain packets (e.g., 2.455 kg + 1.32 kg) keeping decimal points aligned.")
                        ),
                        "Digital/numerical literacy; Critical thinking",
                        "Precision, honesty, and integrity"
                );

            case "2.1":
                return new SubStrandBreakdown(
                        "2.1",
                        Arrays.asList(
                                "Use the millimetre (mm) as a unit of measuring length and relate cm to mm (1 cm = 10 mm).",
                                "Convert centimetres to millimetres and millimetres to centimetres.",
                                "Add, subtract, multiply, and divide lengths in cm and mm.",
                                "Establish the relationship between circumference and diameter (π) practically."
                        ),
                        Arrays.asList(
                                "When do we need millimetres instead of centimetres or metres?",
                                "What do you notice when you divide the circumference of any circle by its diameter?"
                        ),
                        Arrays.asList(
                                new PageBreakdown("Pages 34-36", "Millimetres (mm) & Converting cm and mm",
                                        "Observing the 10 small divisions in 1 cm on a ruler and converting lengths (e.g., 4.5 cm = 45 mm).",
                                        "Measure thickness of exercise books, seeds, and pencils in millimetres using a ruler."),
                                new PageBreakdown("Pages 37-38", "Operations on Length (cm and mm)",
                                        "Adding, subtracting, multiplying, and dividing lengths with regrouping (10 mm = 1 cm).",
                                        "Measure desk dimensions in cm and mm and calculate the total perimeter of 4 desks joined together."),
                                new PageBreakdown("Page 39", "Circumference and Diameter of Circular Objects",
                                        "Measuring around circular tins/tops with string (circumference) and across the center (diameter) to discover C ÷ D ≈ 3.14.",
                                        "Squads wrap sisal string around 3 different tins/cups, measure on a ruler, and compare Circumference ÷ Diameter.")
                        ),
                        "Learning to learn; Critical thinking",
                        "Responsibility in handling measuring instruments"
                );

            case "2.2":
                return new SubStrandBreakdown(
                        "2.2",
                        Arrays.asList(
                                "Work out the area of triangles as half the area of a rectangle (½ × base × height).",
                                "Calculate the area of combined shapes consisting of rectangles, squares, and triangles.",
                                "Apply area calculations to school garden plots and classroom spaces."
                        ),
                        Arrays.asList(
                                "How is the area of a right-angled triangle related to the area of a rectangle?",
                                "How can we split a complex plot of land into simple rectangles and triangles to find its total area?"
                        ),
                        Arrays.asList(
                                new PageBreakdown("Page 40", "Area of Triangles",
                                        "Cutting a rectangular paper along its diagonal to form two equal right-angled triangles, proving Area = ½ × base × height.",
                                        "Learners fold and cut rectangular scrap paper diagonally and count unit squares."),
                                new PageBreakdown("Pages 41-42", "Area of Combined Shapes",
                                        "Dividing L-shaped or house-shaped figures into known rectangles, squares, and triangles and summing their areas.",
                                        "Design a school vegetable garden with rectangular and triangular beds and compute total area.")
                        ),
                        "Creativity & imagination; Problem solving",
                        "Environmental stewardship (school gardening)"
                );

            case "2.3":
                return new SubStrandBreakdown(
                        "2.3",
                        Arrays.asList(
                                "Establish the relationship between cubic centimetres (cm³), millilitres (ml), and litres (L).",
                                "Identify the decilitre (dl) and convert between litres, decilitres, and millilitres.",
                                "Solve practical capacity problems involving water collection and storage."
                        ),
                        Arrays.asList(
                                "How many millilitres or cubic centimetres fill a 1-litre bottle?",
                                "How can accurate capacity measurement help us conserve water at school and home?"
                        ),
                        Arrays.asList(
                                new PageBreakdown("Page 43", "Relating cm³, Millilitres, and Litres",
                                        "Demonstrating that 1 cm³ = 1 ml and 1,000 cm³ = 1,000 ml = 1 Litre.",
                                        "Pour water from calibrated containers into a 1-litre bottle to verify 1,000 ml = 1 L."),
                                new PageBreakdown("Pages 44-45", "Converting Litres, Decilitres (dl), and Millilitres",
                                        "Using 1 Litre = 10 decilitres (dl) = 1,000 millilitres (ml) in everyday liquid measurements.",
                                        "Calibrate recycled plastic bottles using a cup and mark 250 ml, 500 ml, 750 ml, and 1 L levels.")
                        ),
                        "Citizenship; Problem solving",
                        "Responsibility in water conservation"
                );

            case "2.4":
                return new SubStrandBreakdown(
                        "2.4",
                        Arrays.asList(
                                "Identify the tonne (t) as a unit for measuring heavy masses (1 tonne = 1,000 kg).",
                                "Convert tonnes to kilogrammes and kilogrammes to tonnes.",
                                "Add, subtract, multiply, and divide masses in tonnes and kilogrammes."
                        ),
                        Arrays.asList(
                                "What kind of goods in Kenya are weighed in tonnes instead of kilogrammes?",
                                "How do we regroup kilogrammes into tonnes when adding harvest weights?"
                        ),
                        Arrays.asList(
                                new PageBreakdown("Pages 46-48", "The Tonne (t) & Conversions (t and kg)",
                                        "Understanding 1 t = 1,000 kg for lorries, maize harvests, and construction materials.",
                                        "Convert weights of 90 kg maize sacks to find how many sacks make 1.8 tonnes."),
                                new PageBreakdown("Pages 49-50", "Operations on Mass (Tonnes and Kilogrammes)",
                                        "Adding, subtracting, multiplying, and dividing in tonnes and kg with regrouping at 1,000 kg.",
                                        "Role-play a cereals depot weighing deliveries from 4 farmers in tonnes and kilogrammes.")
                        ),
                        "Critical thinking; Financial & agricultural literacy",
                        "Integrity and honesty in weighing agricultural produce"
                );

            case "2.5":
                return new SubStrandBreakdown(
                        "2.5",
                        Arrays.asList(
                                "Interpret and use a.m. and p.m. accurately in daily schedules.",
                                "Read and convert time between the 12-hour and 24-hour clock systems.",
                                "Calculate elapsed time and interpret bus, train (SGR), and flight timetables."
                        ),
                        Arrays.asList(
                                "Why do buses, hospitals, and railways use the 24-hour clock system?",
                                "How do you calculate the duration of a journey from a travel timetable?"
                        ),
                        Arrays.asList(
                                new PageBreakdown("Pages 51-53", "12-Hour (a.m./p.m.) & 24-Hour Clock Systems",
                                        "Converting afternoon/evening times by adding 12 hours (e.g., 2:30 p.m. = 1430 hrs) and writing 4-digit 24-hour time.",
                                        "Draw a dual-ring paper clock face showing 1-12 inside and 13-24 outside."),
                                new PageBreakdown("Page 54", "Travel Timetables & Elapsed Time",
                                        "Reading departure time, arrival time, and stoppage duration from SGR train and matatu schedules.",
                                        "Calculate travel time from Mombasa to Nairobi using a sample SGR timetable drawn on the chalkboard.")
                        ),
                        "Self-efficacy; Critical thinking",
                        "Respect and punctuality (time management)"
                );

            case "2.6":
                return new SubStrandBreakdown(
                        "2.6",
                        Arrays.asList(
                                "Prepare a simple budget and prioritize needs over wants.",
                                "Work out buying price, selling price, profit, and loss in business transactions.",
                                "Identify price lists, bills, and taxes in everyday Kenyan transactions."
                        ),
                        Arrays.asList(
                                "How do you know whether a trader has made a profit or a loss?",
                                "Why is preparing a budget important before going to the market?"
                        ),
                        Arrays.asList(
                                new PageBreakdown("Page 55", "Preparing Simple Budgets & Price Lists",
                                        "Listing items, quantities, unit costs, and total cost within a fixed amount of money.",
                                        "Give each squad a virtual budget of KES 1,000 to plan a class event using a local kiosk price list."),
                                new PageBreakdown("Pages 56-57", "Profit, Loss, and Taxes",
                                        "Profit = Selling Price - Buying Price; Loss = Buying Price - Selling Price; role of tax in public services.",
                                        "Classroom Kiosk Role-Play: teams buy and sell classroom items using paper money and compute their profit.")
                        ),
                        "Financial literacy; Citizenship",
                        "Integrity, patriotism (paying taxes), and responsibility"
                );

            case "3.1":
                return new SubStrandBreakdown(
                        "3.1",
                        Arrays.asList(
                                "Construct parallel lines using a ruler and a set square or paper folding.",
                                "Bisect a line and construct perpendicular lines accurately.",
                                "Identify parallel and perpendicular lines in the classroom and environment."
                        ),
                        Arrays.asList(
                                "Where can we see parallel and perpendicular lines in buildings, roads, and desks?",
                                "How can we fold a sheet of paper to create perpendicular and parallel creases?"
                        ),
                        Arrays.asList(
                                new PageBreakdown("Page 58", "Constructing Parallel Lines",
                                        "Sliding a set square along a straight ruler edge or folding paper twice in the same direction.",
                                        "Use straight sticks, string, and rulers to draw parallel garden rows."),
                                new PageBreakdown("Pages 59-60", "Bisecting Lines & Perpendicular Lines",
                                        "Folding a line segment end-to-end to find its midpoint and perpendicular bisector at 90°.",
                                        "Fold scrap paper strips in half to bisect a line segment and verify with a square corner.")
                        ),
                        "Creativity & imagination; Learning to learn",
                        "Orderliness and neatness in geometric work"
                );

            case "3.2":
                return new SubStrandBreakdown(
                        "3.2",
                        Arrays.asList(
                                "Identify angles on a straight line (sum = 180°) and angles at a point (sum = 360°).",
                                "Measure acute, right, obtuse, and reflex angles using a protractor.",
                                "Demonstrate practically that the sum of interior angles of a triangle is 180°."
                        ),
                        Arrays.asList(
                                "How can tearing the three corners of any triangle prove what its angles add up to?",
                                "Where are angles used by carpenters and roof builders in our community?"
                        ),
                        Arrays.asList(
                                new PageBreakdown("Pages 61-62", "Angles on a Straight Line & Measuring with a Protractor",
                                        "Aligning the center and baseline of a protractor to read inner/outer scales; finding missing angles on a straight line (180°).",
                                        "Use two sticks joined with string/rubber band to form acute, right, and obtuse angles and measure them."),
                                new PageBreakdown("Page 63", "Sum of Angles in a Triangle (180°)",
                                        "Cutting out the 3 vertices (corners) of a paper triangle and placing them side-by-side along a straight line.",
                                        "Every learner cuts a paper triangle, labels corners A, B, C, tears them off, and arranges them on a straight line!")
                        ),
                        "Critical thinking; Creativity",
                        "Cooperation in sharing geometrical sets"
                );

            case "3.3":
                return new SubStrandBreakdown(
                        "3.3",
                        Arrays.asList(
                                "Identify and count faces, edges, and vertices of cubes, cuboids, and cylinders.",
                                "Identify and count faces, edges, and vertices of pyramids and prisms.",
                                "Model 3-D objects using locally available cartons, sticks, and clay/plasticine."
                        ),
                        Arrays.asList(
                                "How do faces, edges, and vertices differ between a cuboid, a pyramid, and a prism?",
                                "How does unfolding a packaging box help us see the 2-D faces of a 3-D object?"
                        ),
                        Arrays.asList(
                                new PageBreakdown("Page 64", "Cubes, Cuboids, and Cylinders",
                                        "Inspecting real packets (chalk boxes, milk cartons, tins) to count flat/curved surfaces, edges, and corners.",
                                        "Touch-and-Count: squads mark each face with chalk as they count faces, edges, and vertices of a carton."),
                                new PageBreakdown("Page 65", "Pyramids and Triangular Prisms",
                                        "Building skeletons of square-based pyramids, triangular pyramids, and prisms using sticks and clay/dough.",
                                        "Join dry twigs/matchsticks with clay balls (vertices) to construct a 3-D pyramid and count its parts.")
                        ),
                        "Creativity & imagination; Problem solving",
                        "Environmental conservation (recycling cartons and tins)"
                );

            case "4.1":
                return new SubStrandBreakdown(
                        "4.1",
                        Arrays.asList(
                                "Collect data from the immediate environment and organize it in frequency tables using tally marks.",
                                "Represent data using pictographs and vertical or horizontal bar graphs.",
                                "Choose an appropriate scale and interpret information from bar graphs."
                        ),
                        Arrays.asList(
                                "Why is it easier to understand information from a bar graph than from a long list of numbers?",
                                "How do we choose a suitable vertical scale when drawing a bar graph?"
                        ),
                        Arrays.asList(
                                new PageBreakdown("Page 66", "Collecting Data & Frequency Tables",
                                        "Recording observations (e.g., shoe sizes, favourite crops, daily attendance) using tally bundles of 5.",
                                        "Survey the class on how they travel to school or their favourite fruit and make a tally table."),
                                new PageBreakdown("Pages 67-68", "Drawing & Interpreting Bar Graphs",
                                        "Labelling axes, choosing a uniform scale (e.g., 1 square = 2 or 5 learners), drawing bars of equal width, and answering questions.",
                                        "Stack bottle tops or seeds in columns on desk grids to form a physical 3-D bar graph before drawing it.")
                        ),
                        "Digital/data literacy; Communication",
                        "Honesty in recording and reporting data"
                );

            case "5.1":
            default:
                return new SubStrandBreakdown(
                        "5.1",
                        Arrays.asList(
                                "Form simple algebraic inequalities in one unknown from real-life situations (<, >, ≤, ≥).",
                                "Simplify algebraic inequalities in one unknown using addition, subtraction, multiplication, and division.",
                                "Solve and interpret simple inequalities in real-life contexts such as capacity limits and budgets."
                        ),
                        Arrays.asList(
                                "Where do we see 'less than' or 'greater than' limits in road safety or market shopping?",
                                "How is solving an inequality similar to balancing a weighing scale?"
                        ),
                        Arrays.asList(
                                new PageBreakdown("Page 69", "Forming Simple Inequalities in One Unknown",
                                        "Translating real-life statements (e.g., 'a matatu carries less than 14 passengers' -> p < 14) into mathematical symbols.",
                                        "Use a homemade beam balance with stones on two sides to show '>' (heavier) and '<' (lighter)."),
                                new PageBreakdown("Page 70", "Simplifying & Solving Inequalities",
                                        "Finding the range of whole numbers that make an inequality true (e.g., if x + 3 < 8, then x < 5, so whole numbers are 0, 1, 2, 3, 4).",
                                        "Number Card Mystery: learners test which number cards satisfy an inequality written on the chalkboard.")
                        ),
                        "Critical thinking & problem solving",
                        "Respect for rules and safety limits (e.g., vehicle capacity)"
                );
        }
    }
}

