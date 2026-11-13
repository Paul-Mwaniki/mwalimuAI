# 🇰🇪 Mwalimu AI: Small Language Model (SLM) Training Guide

This folder contains the complete training dataset and Google Colab pipeline to train an ultra-compact, high-performance on-device AI model for Kenyan Grade 6 Competency-Based Curriculum (CBC) teachers.

---

## 📁 Files Included

| File | Description |
|---|---|
| [`cbc_teacher_train.jsonl`](file:///c:/Users/USER/AndroidStudioProjects/OfflineApp1/training/cbc_teacher_train.jsonl) | **720 comprehensive CBC training samples** (1.7 MB) covering all Grade 6 math strands, custom materials (avocado seeds, sticks, bottle caps, stones, beans), custom activity hooks, and KICD 4-tier rubrics. |
| [`cbc_teacher_val.jsonl`](file:///c:/Users/USER/AndroidStudioProjects/OfflineApp1/training/cbc_teacher_val.jsonl) | **80 validation samples** to evaluate generalization. |
| [`MwalimuAI_Colab_FineTuning.ipynb`](file:///c:/Users/USER/AndroidStudioProjects/OfflineApp1/training/MwalimuAI_Colab_FineTuning.ipynb) | **Google Colab Notebook** ready to upload and run on a free T4 GPU. |
| [`DatasetGenerator.java`](file:///c:/Users/USER/AndroidStudioProjects/OfflineApp1/training/DatasetGenerator.java) | Standalone Java generator used to generate the dataset. |
| [`train_mwalimu_slm.py`](file:///c:/Users/USER/AndroidStudioProjects/OfflineApp1/training/train_mwalimu_slm.py) | Standalone Python training script. |

---

## 🚀 How to Run in Google Colab (Step-by-Step)

### Step 1: Open Google Colab
1. Go to [colab.research.google.com](https://colab.research.google.com).
2. Click **Upload** and select [`MwalimuAI_Colab_FineTuning.ipynb`](file:///c:/Users/USER/AndroidStudioProjects/OfflineApp1/training/MwalimuAI_Colab_FineTuning.ipynb).

### Step 2: Enable Free GPU
1. In the Colab menu, go to **Runtime** > **Change runtime type**.
2. Select **T4 GPU** as the Hardware accelerator.
3. Click **Save**.

### Step 3: Upload the Dataset
1. Click the 📁 **Files** icon on the left sidebar.
2. Drag and drop [`cbc_teacher_train.jsonl`](file:///c:/Users/USER/AndroidStudioProjects/OfflineApp1/training/cbc_teacher_train.jsonl) into the files area.

### Step 4: Run All Cells
1. Click **Runtime** > **Run all** (or press `Ctrl + F9`).
2. Training takes **~15 to 25 minutes**.
3. At the end, the notebook automatically:
   - Evaluates the model on test prompts.
   - Quantizes the model to 4-bit INT4 (**~290 MB** for Qwen2.5-0.5B, or **~75 MB** for SmolLM2-135M).
   - Prompts you to download the `.gguf` file.

---

## 📱 How to Deploy to Your Redmi 14C

Once downloaded, push the file to your connected phone using ADB:

```bash
adb push mwalimu_slm-0.5b-q4_k_m.gguf /sdcard/Android/data/ke.co.mwalimuai/files/
```

Mwalimu AI will load this model completely offline with 0 internet connection required!
