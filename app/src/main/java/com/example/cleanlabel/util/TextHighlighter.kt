package com.example.cleanlabel.util

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle

data class HighlightedMatch(
    val term: String,
    val startIndex: Int,
    val endIndex: Int,
    val category: HighlightCategory
)

enum class HighlightCategory(val color: Color) {
    CARCINOGEN(Color(0xFFE53935)),  // Red
    NEUROTOXIN(Color(0xFFFF6D00)),  // Orange
    CONCERNING_ADDITIVE(Color(0xFFFDD835)),  // Yellow
    ARTIFICIAL_SWEETENER(Color(0xFF7B1FA2)),  // Purple
    PRESERVATIVE(Color(0xFF0097A7))  // Cyan
}

object TextHighlighter {
    private val substanceCategories = mapOf(
        HighlightCategory.CARCINOGEN to setOf(
            "formaldehyde", "benzene", "asbestos", "arsenic", "cadmium",
            "chromium", "vinyl chloride", "ethylene oxide", "nickel compounds",
            "benzo[a]pyrene", "acrylamide"
        ),
        HighlightCategory.NEUROTOXIN to setOf(
            "lead", "mercury", "manganese", "aluminum", "toluene",
            "organophosphates", "ethanol", "methanol", "n-hexane",
            "tetrachloroethylene", "methylmercury"
        ),
        HighlightCategory.ARTIFICIAL_SWEETENER to setOf(
            "aspartame", "sucralose", "saccharin", "acesulfame", "neotame"
        ),
        HighlightCategory.PRESERVATIVE to setOf(
            "bha", "bht", "tbhq", "sodium benzoate", "potassium sorbate",
            "sodium nitrite", "sodium nitrate"
        ),
        HighlightCategory.CONCERNING_ADDITIVE to setOf(
            "msg", "monosodium glutamate", "carrageenan", "red 40", "yellow 5",
            "yellow 6", "blue 1", "blue 2", "titanium dioxide"
        )
    )

    fun findMatches(text: String): List<HighlightedMatch> {
        val matches = mutableListOf<HighlightedMatch>()
        val lowercaseText = text.lowercase()

        substanceCategories.forEach { (category, substances) ->
            substances.forEach { substance ->
                var startIndex = 0
                while (true) {
                    val index = lowercaseText.indexOf(substance, startIndex)
                    if (index == -1) break

                    matches.add(
                        HighlightedMatch(
                            term = text.substring(index, index + substance.length),
                            startIndex = index,
                            endIndex = index + substance.length,
                            category = category
                        )
                    )
                    startIndex = index + 1
                }
            }
        }

        return matches.sortedBy { it.startIndex }
    }

    fun highlightText(text: String, matches: List<HighlightedMatch>): AnnotatedString {
        return buildAnnotatedString {
            var currentIndex = 0

            matches.forEach { match ->
                // Add text before the match
                if (currentIndex < match.startIndex) {
                    append(text.substring(currentIndex, match.startIndex))
                }

                // Add the highlighted match
                withStyle(SpanStyle(
                    background = match.category.color.copy(alpha = 0.3f),
                    color = match.category.color
                )) {
                    append(match.term)
                }

                currentIndex = match.endIndex
            }

            // Add remaining text after last match
            if (currentIndex < text.length) {
                append(text.substring(currentIndex))
            }
        }
    }
} 