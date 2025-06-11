package com.example.cleanlabel.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject

class LabelAnalysisViewModel(application: Application) : AndroidViewModel(application) {
    
    companion object {
        private const val TAG = "LabelAnalysisViewModel"
    }
    
    private val _nutritionAnalysis = MutableStateFlow<NutritionAnalysis?>(null)
    val nutritionAnalysis: StateFlow<NutritionAnalysis?> = _nutritionAnalysis
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error
    
    private val _ocrText = MutableStateFlow<String?>(null)
    val ocrText: StateFlow<String?> = _ocrText
    
    private val _neurotoxinAnalysis = MutableStateFlow<NeurotoxinAnalysis?>(null)
    val neurotoxinAnalysis: StateFlow<NeurotoxinAnalysis?> = _neurotoxinAnalysis

    /**
     * Analyze nutrition data
     */
    fun analyzeNutrition(calories: Float, protein: Float, carbs: Float, fat: Float) {
        _isLoading.value = true
        _error.value = null
        
        Log.d(TAG, "Analyzing nutrition - Calories: $calories, Protein: $protein, Carbs: $carbs, Fat: $fat")
        
        viewModelScope.launch {
            try {
                val analysis = withContext(Dispatchers.Default) {
                    analyzeNutritionData(calories, protein, carbs, fat)
                }
                _nutritionAnalysis.value = analysis
                Log.d(TAG, "Analysis successful: $analysis")
            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
                Log.e(TAG, "Analysis error", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Analyze text from OCR
     */
    fun analyzeText(text: String) {
        _isLoading.value = true
        _error.value = null
        _ocrText.value = text
        
        Log.d(TAG, "Processing OCR text (first 100 chars): ${text.take(100)}...")
        
        viewModelScope.launch {
            try {
                // Analyze for harmful ingredients
                val analysis = withContext(Dispatchers.Default) {
                    analyzeHarmfulIngredients(text)
                }
                _neurotoxinAnalysis.value = analysis
                
            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
                Log.e(TAG, "Analysis error", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    private fun analyzeNutritionData(calories: Float, protein: Float, carbs: Float, fat: Float): NutritionAnalysis {
        val total = protein + carbs + fat
        val proteinPercentage = if (total > 0) (protein / total) * 100 else 0f
        val carbsPercentage = if (total > 0) (carbs / total) * 100 else 0f
        val fatPercentage = if (total > 0) (fat / total) * 100 else 0f
        
        return NutritionAnalysis(
            proteinPercentage = proteinPercentage,
            carbsPercentage = carbsPercentage,
            fatPercentage = fatPercentage,
            classification = classifyNutrition(proteinPercentage, carbsPercentage, fatPercentage),
            isHighProtein = proteinPercentage > 30,
            isLowCarb = carbsPercentage < 30,
            isLowFat = fatPercentage < 20
        )
    }
    
    private fun classifyNutrition(protein: Float, carbs: Float, fat: Float): String {
        return when {
            protein > 30 && carbs < 30 -> "High Protein, Low Carb"
            carbs > 50 -> "High Carb"
            fat > 35 -> "High Fat"
            protein > 20 && carbs > 40 && fat < 30 -> "Balanced"
            else -> "Standard"
        }
    }
    
    private fun analyzeHarmfulIngredients(text: String): NeurotoxinAnalysis {
        val harmfulIngredients = listOf(
            // Neurotoxins
            "MSG", "Aspartame", "Monosodium Glutamate",
            
            // Synthetic Food Dyes
            "Red 40", "Red 3", "Red 2", "Red 1",
            "Yellow 5", "Yellow 6", "Yellow 1", "Yellow 2",
            "Blue 1", "Blue 2",
            "Green 3",
            "Citrus Red 2",
            "Orange B",
            "Brilliant Black BN",
            "Brown HT",
            "Tartrazine", "Allura Red", "Brilliant Blue FCF",
            "Erythrosine", "Fast Green FCF", "Indigotine",
            "Sunset Yellow FCF", "Artificial Colors",
            
            // Preservatives
            "BHA", "BHT", "TBHQ",
            "Sodium Benzoate", "Potassium Benzoate",
            "Sodium Nitrite", "Sodium Nitrate",
            "Potassium Bromate", "Propylene Glycol",
            
            // Other Harmful Additives
            "Carrageenan", "High Fructose Corn Syrup"
        )
        
        val foundIngredients = harmfulIngredients.filter { 
            text.contains(it, ignoreCase = true) 
        }
        
        val categories = mutableListOf<String>()
        if (foundIngredients.any { it in listOf("MSG", "Aspartame", "Monosodium Glutamate") }) {
            categories.add("Neurotoxins")
        }
        if (foundIngredients.any { it.contains("Red", ignoreCase = true) || 
            it.contains("Yellow", ignoreCase = true) ||
            it.contains("Blue", ignoreCase = true) ||
            it.contains("Green", ignoreCase = true) ||
            it.contains("Orange", ignoreCase = true) ||
            it.contains("Brown", ignoreCase = true) ||
            it.contains("Black", ignoreCase = true) ||
            it in listOf("Tartrazine", "Allura Red", "Brilliant Blue FCF",
                        "Erythrosine", "Fast Green FCF", "Indigotine",
                        "Sunset Yellow FCF", "Artificial Colors") }) {
            categories.add("Synthetic Food Dyes")
        }
        if (foundIngredients.any { it in listOf("BHA", "BHT", "TBHQ", 
            "Sodium Benzoate", "Potassium Benzoate",
            "Sodium Nitrite", "Sodium Nitrate",
            "Potassium Bromate", "Propylene Glycol") }) {
            categories.add("Preservatives")
        }
        if (foundIngredients.any { it in listOf("Carrageenan", "High Fructose Corn Syrup") }) {
            categories.add("Other Harmful Additives")
        }
        
        return NeurotoxinAnalysis(
            foundNeurotoxins = foundIngredients,
            categories = categories,
            summary = if (foundIngredients.isEmpty()) "No harmful ingredients found" 
                     else "Found ${foundIngredients.size} potentially harmful ingredients",
            ingredientsText = text
        )
    }
    
    /**
     * Clear the current analysis
     */
    fun clearAnalysis() {
        _nutritionAnalysis.value = null
        _neurotoxinAnalysis.value = null
        _error.value = null
        _ocrText.value = null
    }
    
    /**
     * Get the current OCR text for debugging
     */
    fun getOcrText(): String? {
        return _ocrText.value
    }
}

/**
 * Data class to hold nutrition values
 */
private data class NutritionValues(
    val calories: Float,
    val protein: Float,
    val carbs: Float,
    val fat: Float
)

/**
 * Data class to hold nutrition analysis results
 */
data class NutritionAnalysis(
    val proteinPercentage: Float,
    val carbsPercentage: Float,
    val fatPercentage: Float,
    val classification: String,
    val isHighProtein: Boolean,
    val isLowCarb: Boolean,
    val isLowFat: Boolean,
    var calories: Float = 0f,
    var protein: Float = 0f,
    var carbs: Float = 0f,
    var fat: Float = 0f
)

/**
 * Data class to hold neurotoxin analysis results
 */
data class NeurotoxinAnalysis(
    val foundNeurotoxins: List<String>,
    val categories: List<String>,
    val summary: String,
    val ingredientsText: String
) 