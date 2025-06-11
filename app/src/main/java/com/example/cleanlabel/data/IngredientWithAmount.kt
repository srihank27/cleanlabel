package com.example.cleanlabel.data

data class IngredientWithAmount(
    val name: String,
    val amount: String? = null,  // The amount with unit (e.g., "100g", "50mg")
    val originalText: String  // The original ingredient text
) 