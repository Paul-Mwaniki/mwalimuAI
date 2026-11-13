# 📱 CBE Grade 6 Math — Offline AI Teacher Assistant (Quick Team Brief)

### 1. What It Is
An **offline-first Android app** for Kenyan **Grade 6 CBE Mathematics** teachers. It uses an **on-device TinyML neural network** and a **local SQLite curriculum bank** to break down the KICD curriculum and generate 40-minute lesson plans with zero internet required.

---

### 2. What It CAN Do
- **Generate Complete 40-Min CBE Lesson Plans**: Produces structured plans with Learning Outcomes, Material Prep, Hook, 3-Stage Development (*Concrete Exploration $\rightarrow$ Guided Practice $\rightarrow$ Exit Ticket + Marking Key*), 4-Tier CBE Rubric (EE, ME, AE, BE), and Large-Class Squad Roles.
- **Adapt to Classroom Inputs**: Dynamically adjusts group sizes, time splits, and activities based on **Class Size**, **Duration**, **Learner Need**, and **Electricity/Internet** availability.
- **Support Custom Local Materials**: Teachers can pick preset resources (*stones, bottle tops, sticks*) or type custom materials (*e.g., avocado seeds*), which are mapped into the AI model and injected into every activity step.
- **Rotate Across 64 Kenyan Scenarios**: Includes **4 worked real-life scenarios per sub-strand** (across all 16 sub-strands), so clicking **"Plan Another"** generates a new scenario, worked example, and exit ticket.
- **Offline Curriculum Guide (Pages 13–70)**: Breaks down all 5 Strands and 16 Sub-strands into simplified page-by-page summaries, outcomes, inquiry questions, and offline activities.
- **Export & Save Offline**: Saves plans in **Room SQLite** and exports directly to **Word (`.docx`)** and **PDF (`.pdf`)**.

---

### 3. What It CANNOT Do & Limitations
- **Not a Generative Chatbot (LLM)**: The AI model is a **suitability ranker**, not a free-text generator. It scores and selects from a verified offline bank rather than inventing new text word-by-word.
- **Fixed Scenario Bank (64 Worked Scenarios / 160 Activities)**: Each sub-strand rotates through 4 pre-verified scenarios; after 4 generations on the same topic, it cycles back.
- **Grade 6 Math Only**: Currently scoped strictly to the 16 KICD Grade 6 Mathematics sub-strands.
- **Synthetic Training Data**: Trained on 800 heuristic-labeled classroom scenarios rather than field-collected teacher ratings.

---

### 4. How the AI & Algorithm Work (4 Steps)
1. **Rule-Based Filtering**: Pulls candidate activities for the selected topic from SQLite and filters out any that violate hard constraints (time, electricity, internet).
2. **Feature Encoding (35 Inputs)**: Encodes the teacher's classroom inputs and activity strategy into a **35-feature vector** (6 Z-score normalized numbers + 29 one-hot categorical flags).
3. **TinyML Neural Scoring**: Runs a **Feedforward Neural Network (`.tflite`)** on-device via **TensorFlow Lite (LiteRT)** to score each candidate activity from `1.0` to `5.0` and rank the best teaching strategies.
4. **Dynamic Assembly**: Combines the top-ranked strategies with the next scenario in rotation, calculates squad counts ($\text{Class Size} \div 5$), and injects the teacher's exact materials into the final plan.

---

### 5. How It Was Trained & Built
- **Model Training**: Trained a regression neural network in **Google Colab** on **800 classroom-activity scenarios** (`Recommendation_Labels.json`) aligned to KICD Pages 13–70, then quantized it to **TensorFlow Lite** (`mwalimu_activity_ranker_optimized.tflite`).
- **Tech Stack**: Java, Android Jetpack (`ViewModel`, `LiveData`, `Navigation`), Material 3, **Room SQLite**, **LiteRT (TFLite)**, native `PdfDocument` (PDF), and OOXML `ZipOutputStream` (`.docx`).
