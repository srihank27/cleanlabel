package com.example.cleanlabel.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cleanlabel.viewmodel.NeurotoxinAnalysis
import com.example.cleanlabel.viewmodel.LabelAnalysisViewModel
import com.example.cleanlabel.viewmodel.HealthProfileViewModel
import com.example.cleanlabel.data.HealthCondition
import com.example.cleanlabel.data.HealthConditionCategory
import com.example.cleanlabel.data.HealthConditionsRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LabelAnalysisScreen(
    onBackClick: () -> Unit,
    text: String
) {
    val viewModel: LabelAnalysisViewModel = viewModel()
    val healthProfileViewModel: HealthProfileViewModel = viewModel()
    val neurotoxinAnalysis by viewModel.neurotoxinAnalysis.collectAsState(null)
    val isLoading by viewModel.isLoading.collectAsState(false)
    val error by viewModel.error.collectAsState(null)
    val currentProfile = healthProfileViewModel.currentProfile.collectAsState().value
    val conditions = currentProfile?.conditions ?: emptyList()
    
    // Trigger text analysis when the screen is loaded
    LaunchedEffect(text) {
        viewModel.analyzeText(text)
    }
    
    // Get the full HealthCondition objects for the current conditions
    val healthConditions = remember(conditions) {
        conditions.mapNotNull { conditionName ->
            HealthConditionsRepository.conditions.find { condition -> condition.name == conditionName }
        }
    }

    // Parse ingredients from the text
    val ingredients = remember(text) {
        // Look for common ingredient list indicators
        val ingredientSection = text.split(Regex("(?i)ingredients:|contains:|contains of:|made with:"))
            .getOrNull(1)?.trim() ?: text
        
        // Split by spaces and clean up
        ingredientSection.split(Regex("\\s+"))
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .filter { it.length > 2 } // Filter out very short words
            .filter { !it.contains(Regex("[0-9]")) } // Filter out words with numbers
    }

    // Analyze ingredients against health conditions
    val healthAnalysis = remember(ingredients, healthConditions) {
        healthConditions.mapNotNull { condition ->
            val concerns = when (condition.category) {
                HealthConditionCategory.ALLERGY -> {
                    ingredients.filter { ingredient ->
                        when (condition.name) {
                            "Peanut Allergy" -> ingredient.contains("peanut", ignoreCase = true)
                            "Tree Nut Allergy" -> ingredient.contains("almond", ignoreCase = true) || 
                                                ingredient.contains("walnut", ignoreCase = true) ||
                                                ingredient.contains("cashew", ignoreCase = true)
                            "Milk Allergy" -> ingredient.contains("milk", ignoreCase = true) ||
                                            ingredient.contains("whey", ignoreCase = true) ||
                                            ingredient.contains("casein", ignoreCase = true)
                            "Egg Allergy" -> ingredient.contains("egg", ignoreCase = true)
                            "Soy Allergy" -> ingredient.contains("soy", ignoreCase = true)
                            "Wheat Allergy" -> ingredient.contains("wheat", ignoreCase = true)
                            "Fish Allergy" -> ingredient.contains("fish", ignoreCase = true)
                            "Shellfish Allergy" -> ingredient.contains("shellfish", ignoreCase = true)
                            "Sesame Allergy" -> ingredient.contains("sesame", ignoreCase = true)
                            else -> false
                        }
                    }
                }
                HealthConditionCategory.DIETARY -> {
                    ingredients.filter { ingredient ->
                        when (condition.name) {
                            "Vegetarian" -> ingredient.contains("meat", ignoreCase = true) ||
                                         ingredient.contains("fish", ignoreCase = true) ||
                                         ingredient.contains("chicken", ignoreCase = true)
                            "Vegan" -> ingredient.contains("milk", ignoreCase = true) ||
                                     ingredient.contains("egg", ignoreCase = true) ||
                                     ingredient.contains("honey", ignoreCase = true)
                            "Gluten-Free" -> ingredient.contains("wheat", ignoreCase = true) ||
                                           ingredient.contains("barley", ignoreCase = true) ||
                                           ingredient.contains("rye", ignoreCase = true)
                            "Dairy-Free" -> ingredient.contains("milk", ignoreCase = true) ||
                                          ingredient.contains("cheese", ignoreCase = true) ||
                                          ingredient.contains("yogurt", ignoreCase = true)
                            else -> false
                        }
                    }
                }
                HealthConditionCategory.CHRONIC -> {
                    ingredients.filter { ingredient ->
                        when (condition.name) {
                            "Diabetes" -> ingredient.contains("sugar", ignoreCase = true) ||
                                        ingredient.contains("glucose", ignoreCase = true) ||
                                        ingredient.contains("fructose", ignoreCase = true)
                            "High Blood Pressure" -> ingredient.contains("sodium", ignoreCase = true) ||
                                                   ingredient.contains("salt", ignoreCase = true)
                            "High Cholesterol" -> ingredient.contains("cholesterol", ignoreCase = true) ||
                                                ingredient.contains("saturated fat", ignoreCase = true)
                            "Heart Disease" -> ingredient.contains("trans fat", ignoreCase = true) ||
                                             ingredient.contains("hydrogenated", ignoreCase = true)
                            else -> false
                        }
                    }
                }
                else -> emptyList()
            }
            if (concerns.isNotEmpty()) {
                HealthAnalysisResult(
                    condition = condition,
                    concerns = concerns.distinct() // Remove duplicates
                )
            } else null
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Label Analysis") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Loading indicator
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            
            // Error message
            error?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
            
            // Analysis Results
            if (!isLoading && error == null) {
                neurotoxinAnalysis?.let { analysis ->
                    IngredientAnalysisResults(analysis)
                    
                    // Health Analysis Results
                    if (healthConditions.isNotEmpty()) {
                        HealthAnalysisResults(healthAnalysis)
                    }
                }
            }
        }
    }
}

@Composable
fun HealthAnalysisResults(healthAnalysis: List<HealthAnalysisResult>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (healthAnalysis.isEmpty()) 
                MaterialTheme.colorScheme.primaryContainer 
            else 
                MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (healthAnalysis.isEmpty()) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Safe",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Warning",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = "Health Profile Analysis",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    color = if (healthAnalysis.isEmpty()) 
                        MaterialTheme.colorScheme.onPrimaryContainer 
                    else 
                        MaterialTheme.colorScheme.onErrorContainer
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Summary
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = if (healthAnalysis.isEmpty()) 
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) 
                        else 
                            MaterialTheme.colorScheme.error.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(12.dp)
            ) {
                Text(
                    text = if (healthAnalysis.isEmpty()) 
                        "Good news! This product is suitable for your health profile." 
                    else 
                        "This product may not be suitable for your health conditions.",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (healthAnalysis.isEmpty()) 
                        MaterialTheme.colorScheme.onPrimaryContainer 
                    else 
                        MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
            
            if (healthAnalysis.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                
                // Health Concerns by Category
                healthAnalysis.groupBy { it.condition.category }.forEach { (category, analyses) ->
                    // Category Header
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = category.icon,
                            contentDescription = null,
                            tint = category.color,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = category.displayName,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Conditions and Ingredients
                    analyses.forEach { analysis ->
                        // Condition Name
                        Text(
                            text = "• ${analysis.condition.name}:",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                        
                        // Ingredients
                        analysis.concerns.forEach { ingredient ->
                            Text(
                                text = "  - $ingredient",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.padding(start = 24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

data class HealthAnalysisResult(
    val condition: HealthCondition,
    val concerns: List<String>
)

@Composable
fun IngredientAnalysisResults(analysis: NeurotoxinAnalysis) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (analysis.foundNeurotoxins.isNotEmpty()) 
                MaterialTheme.colorScheme.errorContainer 
            else 
                MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (analysis.foundNeurotoxins.isNotEmpty()) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Warning",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                
                Text(
                    text = "Ingredients Analysis",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    color = if (analysis.foundNeurotoxins.isNotEmpty()) 
                        MaterialTheme.colorScheme.onErrorContainer 
                    else 
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Summary
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = if (analysis.foundNeurotoxins.isNotEmpty()) 
                            MaterialTheme.colorScheme.error.copy(alpha = 0.2f) 
                        else 
                            MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(12.dp)
            ) {
                Text(
                    text = if (analysis.foundNeurotoxins.isEmpty()) 
                        "Good news! No harmful ingredients were found in this product." 
                    else 
                        analysis.summary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (analysis.foundNeurotoxins.isNotEmpty()) 
                        MaterialTheme.colorScheme.onErrorContainer 
                    else 
                        MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
            
            if (analysis.foundNeurotoxins.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                
                // Categories
                Text(
                    text = "Categories Found:",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                analysis.categories.forEach { category ->
                    Text(
                        text = "• $category",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Found ingredients
                Text(
                    text = "Harmful Ingredients Found:",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                analysis.foundNeurotoxins.forEach { ingredient ->
                    Text(
                        text = "• $ingredient",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }
    }
} 