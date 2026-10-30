package com.example.model

data class PatternDraft(
    val id: String = "",
    val name: String = "Sculpted Corset Sloper",
    val category: String = "Bodice",
    val bustSize: Float = 88f,
    val waistSize: Float = 66f,
    val hipSize: Float = 94f,
    val easeAllowance: Float = 2.0f,
    val dartPosition: String = "Princess Seam",
    val seamAllowance: Float = 1.5f,
    val grainline: String = "Straight Grain",
    val notes: String = "Engineered with 4-piece boning channels and 1.5cm seam allowances.",
    val updatedAt: Long = System.currentTimeMillis()
)
