package com.example.cleanlabel.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.cleanlabel.data.IngredientAnalysis
import com.example.cleanlabel.data.IngredientWithAmount
import com.example.cleanlabel.utils.IngredientParser

@Composable
fun IngredientAnalysisResults(
    analysis: IngredientAnalysis?,
    modifier: Modifier = Modifier
) {
    if (analysis == null) return

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        if (analysis.hasHarmfulSubstances) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Warning",
                    tint = MaterialTheme.colorScheme.error
                )
                Text(
                    text = "Potentially Harmful Substances Found",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.error
                )
            }
        } else {
            Text(
                text = "✅ No Known Harmful Substances Detected",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }

        // Carcinogens Section
        if (analysis.carcinogens.isNotEmpty()) {
            HarmfulSubstanceSection(
                title = "🚫 Potential Carcinogens",
                items = analysis.carcinogens.map { IngredientParser.parseIngredient(it) }
            )
        }

        // Neurotoxins Section
        if (analysis.neurotoxins.isNotEmpty()) {
            HarmfulSubstanceSection(
                title = "⚡ Potential Neurotoxins",
                items = analysis.neurotoxins.map { IngredientParser.parseIngredient(it) }
            )
        }

        // Concerning Additives Section
        if (analysis.concerningAdditives.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "⚠️ Concerning Additives",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    analysis.concerningAdditives.forEach { (additive, concern) ->
                        val parsedIngredient = IngredientParser.parseIngredient(additive)
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = parsedIngredient.name,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium
                                )
                                if (parsedIngredient.amount != null) {
                                    Text(
                                        text = parsedIngredient.amount,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                            Text(
                                text = concern,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HarmfulSubstanceSection(
    title: String,
    items: List<IngredientWithAmount>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            items.forEach { ingredient ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "• ${ingredient.name}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    if (ingredient.amount != null) {
                        Text(
                            text = ingredient.amount,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
} 