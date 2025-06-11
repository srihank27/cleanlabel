package com.example.cleanlabel.data

/**
 * Data class to hold the results of ingredient analysis
 */
data class IngredientAnalysis(
    val carcinogens: List<String>,
    val neurotoxins: List<String>,
    val concerningAdditives: Map<String, String>
) {
    val hasHarmfulSubstances: Boolean
        get() = carcinogens.isNotEmpty() || neurotoxins.isNotEmpty() || concerningAdditives.isNotEmpty()

    val summary: String
        get() = buildString {
            if (hasHarmfulSubstances) {
                append("Warning: Found ")
                val items = mutableListOf<String>()
                if (carcinogens.isNotEmpty()) items.add("${carcinogens.size} carcinogen(s)")
                if (neurotoxins.isNotEmpty()) items.add("${neurotoxins.size} neurotoxin(s)")
                if (concerningAdditives.isNotEmpty()) items.add("${concerningAdditives.size} concerning additive(s)")
                append(items.joinToString(", "))
            } else {
                append("No known harmful substances found")
            }
        }
} 