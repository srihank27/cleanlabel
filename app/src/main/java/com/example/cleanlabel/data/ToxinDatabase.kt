package com.example.cleanlabel.data

import com.example.cleanlabel.data.IngredientAnalysis
import com.example.cleanlabel.data.IngredientWithAmount

/**
 * Database of known harmful substances compiled from authoritative sources:
 * - National Toxicology Program (NTP)
 * - International Agency for Research on Cancer (IARC)
 * - Environmental Protection Agency (EPA)
 * - Agency for Toxic Substances and Disease Registry (ATSDR)
 */
object ToxinDatabase {
    // Known carcinogenic substances
    private val knownCarcinogens = setOf(
        "formaldehyde",
        "benzene",
        "asbestos",
        "arsenic",
        "cadmium",
        "chromium",
        "vinyl chloride",
        "ethylene oxide",
        "nickel compounds",
        "polycyclic aromatic hydrocarbons",
        "acrylamide",
        "benzo[a]pyrene"
    )
    
    // Known neurotoxic substances
    private val knownNeurotoxins = setOf(
        "lead",
        "mercury",
        "manganese",
        "aluminum",
        "toluene",
        "organophosphates",
        "ethanol",
        "methanol",
        "n-hexane",
        "tetrachloroethylene",
        "methylmercury",
        "acrylamide"
    )
    
    // Common food additives with potential health concerns
    private val concerningAdditives = setOf(
        "artificial colors" to "Potential behavioral effects and hyperactivity",
        "aspartame" to "Controversial artificial sweetener with mixed evidence on safety",
        "bha" to "Possible carcinogen",
        "bht" to "Possible carcinogen",
        "carrageenan" to "Potential inflammatory effects",
        "high fructose corn syrup" to "Associated with metabolic disorders",
        "monosodium glutamate" to "May cause adverse reactions in sensitive individuals",
        "nitrates" to "Can form carcinogenic compounds",
        "nitrites" to "Can form carcinogenic compounds",
        "potassium bromate" to "Possible carcinogen",
        "sodium benzoate" to "May form benzene when combined with vitamin C",
        "titanium dioxide" to "Possible carcinogen"
    ).toMap()

    /**
     * Analyzes a list of ingredients for potential harmful substances
     * @param ingredients List of IngredientWithAmount objects to analyze
     * @return Analysis results containing found carcinogens, neurotoxins, and concerning additives
     */
    fun analyzeIngredients(ingredients: List<IngredientWithAmount>): IngredientAnalysis {
        val foundCarcinogens = mutableListOf<String>()
        val foundNeurotoxins = mutableListOf<String>()
        val foundAdditives = mutableMapOf<String, String>()

        ingredients.forEach { ingredient ->
            val normalizedName = ingredient.name.lowercase()
            
            // Check for carcinogens
            if (knownCarcinogens.any { carcinogen -> 
                normalizedName.contains(carcinogen) 
            }) {
                foundCarcinogens.add(ingredient.originalText)
            }

            // Check for neurotoxins
            if (knownNeurotoxins.any { neurotoxin -> 
                normalizedName.contains(neurotoxin) 
            }) {
                foundNeurotoxins.add(ingredient.originalText)
            }

            // Check for concerning additives
            concerningAdditives.forEach { (additive, concern) ->
                if (normalizedName.contains(additive)) {
                    foundAdditives[ingredient.originalText] = concern
                }
            }
        }

        return IngredientAnalysis(
            carcinogens = foundCarcinogens,
            neurotoxins = foundNeurotoxins,
            concerningAdditives = foundAdditives
        )
    }
} 