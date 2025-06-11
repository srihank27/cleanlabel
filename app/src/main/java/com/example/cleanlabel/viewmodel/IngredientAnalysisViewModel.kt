package com.example.cleanlabel.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cleanlabel.data.IngredientAnalysis
import com.example.cleanlabel.data.ToxinDatabase
import com.example.cleanlabel.utils.IngredientParser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class IngredientAnalysisViewModel : ViewModel() {
    private val _analysisResult = MutableStateFlow<IngredientAnalysis?>(null)
    val analysisResult: StateFlow<IngredientAnalysis?> = _analysisResult.asStateFlow()

    private val _originalText = MutableStateFlow<String?>(null)
    val originalText: StateFlow<String?> = _originalText.asStateFlow()

    /**
     * Attempts to extract the ingredients section from the text
     */
    private fun extractIngredientsSection(text: String): String? {
        val ingredientsPattern = Regex(
            "(?i)(ingredients:|ingredients\\s*:?|contains:)\\s*([^.]*)",
            RegexOption.MULTILINE
        )
        return ingredientsPattern.find(text)?.groupValues?.get(2)?.trim()
    }

    /**
     * Analyzes the ingredients text and returns an analysis result
     */
    private fun analyzeIngredientsText(text: String): IngredientAnalysis {
        // Split ingredients by common delimiters and parse each ingredient
        val ingredients = text
            .split(Regex("[,;.]"))
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .map { IngredientParser.parseIngredient(it) }

        // Analyze ingredients using the toxin database
        return ToxinDatabase.analyzeIngredients(ingredients)
    }

    /**
     * Analyzes the given text for ingredients and checks them against the toxin database
     */
    fun analyzeIngredients(text: String) {
        viewModelScope.launch {
            _originalText.value = text
            try {
                // Extract ingredients section if possible
                val ingredientsText = extractIngredientsSection(text)
                
                // Analyze the ingredients
                val analysis = analyzeIngredientsText(ingredientsText ?: text)
                _analysisResult.value = analysis
            } catch (e: Exception) {
                _analysisResult.value = null
                Log.e("IngredientAnalysisViewModel", "Error analyzing ingredients", e)
            }
        }
    }

    /**
     * Clears the current analysis result
     */
    fun clearAnalysis() {
        _analysisResult.value = null
        _originalText.value = null
    }
} 