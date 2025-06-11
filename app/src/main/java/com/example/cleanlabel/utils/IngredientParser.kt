package com.example.cleanlabel.utils

import com.example.cleanlabel.data.IngredientWithAmount

object IngredientParser {
    private val amountPattern = Regex("""(\d+(?:\.\d+)?)\s*(g|mg|kg|ml|l|oz|%|mcg)""", RegexOption.IGNORE_CASE)

    fun parseIngredient(text: String): IngredientWithAmount {
        val match = amountPattern.find(text)
        return if (match != null) {
            val amount = match.groupValues[1] + match.groupValues[2]
            val name = text.replace(match.value, "").trim()
            IngredientWithAmount(
                name = name,
                amount = amount,
                originalText = text
            )
        } else {
            IngredientWithAmount(
                name = text.trim(),
                amount = null,
                originalText = text
            )
        }
    }

    fun parseIngredientList(text: String): List<IngredientWithAmount> {
        // Split by common delimiters
        return text.split(Regex("[,;]"))
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .map { parseIngredient(it) }
    }
} 