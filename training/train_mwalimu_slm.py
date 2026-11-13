#!/usr/bin/env python3
"""
Mwalimu AI - On-Device Small Language Model (SLM) Fine-Tuning Script
Fine-tunes Qwen2.5-0.5B-Instruct or SmolLM2-135M-Instruct on Kenyan CBC Grade 6 Mathematics datasets.
"""

import os
import torch
from unsloth import FastLanguageModel
from datasets import load_dataset
from trl import SFTTrainer
from transformers import TrainingArguments

def main():
    max_seq_length = 1024
    dtype = None
    load_in_4bit = True

    model_name = "Qwen/Qwen2.5-0.5B-Instruct"  # or "HuggingFaceTB/SmolLM2-135M-Instruct"
    print(f"Loading {model_name} with 4-bit quantization...")

    model, tokenizer = FastLanguageModel.from_pretrained(
        model_name = model_name,
        max_seq_length = max_seq_length,
        dtype = dtype,
        load_in_4bit = load_in_4bit,
    )

    print("Configuring LoRA adapters...")
    model = FastLanguageModel.get_peft_model(
        model,
        r = 16,
        target_modules = ["q_proj", "k_proj", "v_proj", "o_proj",
                          "gate_proj", "up_proj", "down_proj"],
        lora_alpha = 16,
        lora_dropout = 0,
        bias = "none",
        use_gradient_checkpointing = "unsloth",
        random_state = 3407,
    )

    dataset_path = os.path.join(os.path.dirname(__file__), "cbc_teacher_train.jsonl")
    if not os.path.exists(dataset_path):
        dataset_path = "cbc_teacher_train.jsonl"
    print(f"Loading dataset from {dataset_path}...")

    dataset = load_dataset("json", data_files={"train": dataset_path})["train"]
    print(f"Loaded {len(dataset)} training samples.")

    def format_chat(examples):
        convos = examples["messages"]
        texts = [tokenizer.apply_chat_template(convo, tokenize=False, add_generation_prompt=False) for convo in convos]
        return {"text": texts}

    dataset = dataset.map(format_chat, batched=True)

    print("Starting fine-tuning...")
    trainer = SFTTrainer(
        model = model,
        tokenizer = tokenizer,
        train_dataset = dataset,
        dataset_text_field = "text",
        max_seq_length = max_seq_length,
        args = TrainingArguments(
            per_device_train_batch_size = 4,
            gradient_accumulation_steps = 4,
            warmup_steps = 15,
            max_steps = 150,
            learning_rate = 2e-4,
            fp16 = not torch.cuda.is_bf16_supported(),
            bf16 = torch.cuda.is_bf16_supported(),
            logging_steps = 10,
            optim = "adamw_8bit",
            weight_decay = 0.01,
            lr_scheduler_type = "cosine",
            seed = 3407,
            output_dir = "mwalimu_checkpoints",
        ),
    )

    trainer.train()
    print("Fine-tuning completed successfully!")

    print("Exporting 4-bit GGUF model for Android deployment...")
    model.save_pretrained_gguf("mwalimu_slm_q4_k_m", tokenizer, quantization_method="q4_k_m")
    print("Export finished! Quantized model ready in 'mwalimu_slm_q4_k_m/'.")

if __name__ == "__main__":
    main()
