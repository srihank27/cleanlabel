package com.example.cleanlabel.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.cleanlabel.util.HighlightCategory
import com.example.cleanlabel.util.TextHighlighter

@Composable
fun HighlightedIngredientText(
    text: String,
    modifier: Modifier = Modifier
) {
    val matches = remember(text) {
        TextHighlighter.findMatches(text)
    }
    
    val highlightedText = remember(text, matches) {
        TextHighlighter.highlightText(text, matches)
    }
    
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Original text with highlights
        Text(
            text = highlightedText,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    MaterialTheme.colorScheme.surface,
                    RoundedCornerShape(8.dp)
                )
                .padding(16.dp)
        )
        
        // Legend
        if (matches.isNotEmpty()) {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Ingredient Categories",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    val categories = matches.map { it.category }.distinct()
                    categories.forEach { category ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                modifier = Modifier.size(24.dp),
                                shape = RoundedCornerShape(4.dp),
                                color = category.color.copy(alpha = 0.3f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(category.color.copy(alpha = 0.3f))
                                )
                            }
                            
                            Text(
                                text = when (category) {
                                    HighlightCategory.CARCINOGEN -> "Carcinogenic Substances"
                                    HighlightCategory.NEUROTOXIN -> "Neurotoxic Substances"
                                    HighlightCategory.CONCERNING_ADDITIVE -> "Concerning Additives"
                                    HighlightCategory.ARTIFICIAL_SWEETENER -> "Artificial Sweeteners"
                                    HighlightCategory.PRESERVATIVE -> "Preservatives"
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                color = category.color
                            )
                        }
                    }
                }
            }
        }
    }
} 