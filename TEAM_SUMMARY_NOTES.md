# 📱 CBE Grade 6 Math — Offline AI Teacher Assistant (Quick Team Brief)

### 1. What It Is
An **offline-first Android AI app** for Kenyan **Grade 6 CBE Mathematics** teachers. It combines an **On-Device Small Language Model (SLM) & Curriculum RAG Engine**, a **Procedural Mathematical & Scenario Synthesizer**, and a **TinyML Pedagogical Strategy Ranker** to generate unlimited, non-repeating 40-minute lesson plans with zero internet required.

---

### 2. What It CAN Do
- **Unlimited Dynamic Lesson Synthesis (Never Repeats)**: Dynamically generates brand-new grade-appropriate numbers, solves the math step-by-step in real time (place value, partial products, long division, BODMAS, LCM fractions, decimals, geometry, bar graphs, inequalities), and pairs them with 47 Kenyan counties and community contexts on every single click.
- **Local KICD Curriculum RAG (Pages 13–70)**: Automatically retrieves the official KICD learning outcomes, page references, core competencies, values, and key inquiry questions for the selected sub-strand and injects them into the lesson plan.
- **On-Device MediaPipe GenAI SLM Support**: Includes `OnDeviceSlmEngine` (`com.google.mediapipe:tasks-genai`) which automatically loads any quantized `.task` / `.bin` Small Language Model (such as *Gemma-2-2B-IT-int4* or *Qwen-2.5-1.5B*) placed in `/sdcard/Android/data/ke.co.mwalimuai/files/` for hardware-accelerated free-text generation.
- **Adapt to Custom Materials & Classroom Inputs**: Teachers can select or type any local resource (*e.g., avocado seeds, mango leaves, bottle tops*) and any custom lesson hook, and the engine weaves them directly into the Concrete-Representational-Abstract (CRA) steps, squad sizes ($\text{Class Size} \div 5$), and 4-Tier CBE Rubric.
- **Export & Save Offline**: Saves plans in **Room SQLite** and exports directly to **Word (`.docx`)** and **PDF (`.pdf`)**.

---

### 3. Current Scope & Hardware Notes
- **Grade 6 Math Scope**: Currently grounded in the 5 Strands and 16 Sub-strands of the KICD Grade 6 Mathematics Curriculum Design (Pages 13–70).
- **Zero-Wait Instant Synthesis + Optional Heavy SLM Weights**: Works out-of-the-box in `<50 ms` using the built-in **Procedural Math & Curriculum RAG Synthesizer**, and automatically upgrades to full neural token generation whenever a `.task`/`.bin` SLM weight file is copied to the device.

---

### 4. How the Hybrid Offline AI Engine Works (4 Steps)
1. **Local Curriculum RAG Retrieval (`CurriculumBreakdownHelper`)**: Pulls the exact KICD pages, learning outcomes, competencies, and key inquiry questions for the chosen sub-strand.
2. **TinyML Pedagogical Strategy Ranking (`TinyMLInferenceManager`)**: Encodes classroom constraints (class size, duration, learner need, electricity/internet) into a **35-feature vector** and runs the `.tflite` neural network to select the best teaching methodology (*Manipulative Exploration, Think-Pair-Share, Station Rotation*, etc.).
3. **Procedural Math & Scenario Synthesis (`ProceduralMathSynthesizer`)**: Dynamically generates fresh numbers, computes the exact multi-step mathematical working and marking key, and contextualizes the problem in a Kenyan county using the teacher's exact materials.
4. **On-Device SLM Generation (`OnDeviceSlmEngine`)**: Constructs a structured RAG prompt and runs MediaPipe `LlmInference` (if `.task`/`.bin` weights are present on disk) or the procedural synthesizer to assemble the complete lesson plan.

---

### 5. Tech Stack
- **AI & ML**: **Google MediaPipe GenAI (`tasks-genai`)**, **LiteRT / TensorFlow Lite (`mwalimu_activity_ranker_optimized.tflite`)**, **Local Curriculum RAG**, and **Procedural Symbolic Math Synthesis**.
- **Android Architecture**: Java, Android Jetpack (`ViewModel`, `LiveData`, `Navigation`), Material 3, **Room SQLite**, native `PdfDocument` (PDF), and OOXML `ZipOutputStream` (`.docx`).
