package com.example.cleanlabel.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

enum class HealthConditionCategory(
    val displayName: String,
    val icon: ImageVector,
    val color: Color
) {
    ALLERGY(
        displayName = "Allergies",
        icon = Icons.Default.Warning,
        color = Color(0xFFFF6B6B) // Red
    ),
    DIETARY(
        displayName = "Dietary",
        icon = Icons.Default.Restaurant,
        color = Color(0xFF4CAF50) // Green
    ),
    CHRONIC(
        displayName = "Chronic",
        icon = Icons.Default.MedicalServices,
        color = Color(0xFFE91E63) // Pink
    ),
    OTHER(
        displayName = "Other",
        icon = Icons.Default.Info,
        color = Color(0xFF2196F3) // Blue
    )
}

data class HealthCondition(
    val id: String,
    val name: String,
    val category: HealthConditionCategory,
    val symbol: ImageVector,
    val color: Color
)

object HealthConditionsRepository {
    val conditions = listOf(
        // Allergies
        HealthCondition(
            id = "peanut_allergy",
            name = "Peanut Allergy",
            category = HealthConditionCategory.ALLERGY,
            symbol = Icons.Default.Warning,
            color = Color(0xFFFF6B6B) // Red
        ),
        HealthCondition(
            id = "tree_nut_allergy",
            name = "Tree Nut Allergy",
            category = HealthConditionCategory.ALLERGY,
            symbol = Icons.Default.Warning,
            color = Color(0xFFFF6B6B) // Red
        ),
        HealthCondition(
            id = "milk_allergy",
            name = "Milk Allergy",
            category = HealthConditionCategory.ALLERGY,
            symbol = Icons.Default.Warning,
            color = Color(0xFFFF6B6B) // Red
        ),
        HealthCondition(
            id = "egg_allergy",
            name = "Egg Allergy",
            category = HealthConditionCategory.ALLERGY,
            symbol = Icons.Default.Warning,
            color = Color(0xFFFF6B6B) // Red
        ),
        HealthCondition(
            id = "soy_allergy",
            name = "Soy Allergy",
            category = HealthConditionCategory.ALLERGY,
            symbol = Icons.Default.Warning,
            color = Color(0xFFFF6B6B) // Red
        ),
        HealthCondition(
            id = "wheat_allergy",
            name = "Wheat Allergy",
            category = HealthConditionCategory.ALLERGY,
            symbol = Icons.Default.Warning,
            color = Color(0xFFFF6B6B) // Red
        ),
        HealthCondition(
            id = "fish_allergy",
            name = "Fish Allergy",
            category = HealthConditionCategory.ALLERGY,
            symbol = Icons.Default.Warning,
            color = Color(0xFFFF6B6B) // Red
        ),
        HealthCondition(
            id = "shellfish_allergy",
            name = "Shellfish Allergy",
            category = HealthConditionCategory.ALLERGY,
            symbol = Icons.Default.Warning,
            color = Color(0xFFFF6B6B) // Red
        ),
        HealthCondition(
            id = "sesame_allergy",
            name = "Sesame Allergy",
            category = HealthConditionCategory.ALLERGY,
            symbol = Icons.Default.Warning,
            color = Color(0xFFFF6B6B) // Red
        ),

        // Dietary Restrictions
        HealthCondition(
            id = "vegetarian",
            name = "Vegetarian",
            category = HealthConditionCategory.DIETARY,
            symbol = Icons.Default.Eco,
            color = Color(0xFF4CAF50) // Green
        ),
        HealthCondition(
            id = "vegan",
            name = "Vegan",
            category = HealthConditionCategory.DIETARY,
            symbol = Icons.Default.Eco,
            color = Color(0xFF4CAF50) // Green
        ),
        HealthCondition(
            id = "gluten_free",
            name = "Gluten-Free",
            category = HealthConditionCategory.DIETARY,
            symbol = Icons.Default.Grain,
            color = Color(0xFF8D6E63) // Brown
        ),
        HealthCondition(
            id = "dairy_free",
            name = "Dairy-Free",
            category = HealthConditionCategory.DIETARY,
            symbol = Icons.Default.WaterDrop,
            color = Color(0xFF64B5F6) // Blue
        ),
        HealthCondition(
            id = "kosher",
            name = "Kosher",
            category = HealthConditionCategory.DIETARY,
            symbol = Icons.Default.Star,
            color = Color(0xFFFFD700) // Gold
        ),
        HealthCondition(
            id = "halal",
            name = "Halal",
            category = HealthConditionCategory.DIETARY,
            symbol = Icons.Default.Star,
            color = Color(0xFF4CAF50) // Green
        ),

        // Chronic Conditions
        HealthCondition(
            id = "diabetes",
            name = "Diabetes",
            category = HealthConditionCategory.CHRONIC,
            symbol = Icons.Default.MonitorHeart,
            color = Color(0xFFE91E63) // Pink
        ),
        HealthCondition(
            id = "celiac",
            name = "Celiac Disease",
            category = HealthConditionCategory.CHRONIC,
            symbol = Icons.Default.Grain,
            color = Color(0xFF8D6E63) // Brown
        ),
        HealthCondition(
            id = "lactose_intolerance",
            name = "Lactose Intolerance",
            category = HealthConditionCategory.CHRONIC,
            symbol = Icons.Default.WaterDrop,
            color = Color(0xFF64B5F6) // Blue
        ),
        HealthCondition(
            id = "high_blood_pressure",
            name = "High Blood Pressure",
            category = HealthConditionCategory.CHRONIC,
            symbol = Icons.Default.Favorite,
            color = Color(0xFFE91E63) // Pink
        ),
        HealthCondition(
            id = "high_cholesterol",
            name = "High Cholesterol",
            category = HealthConditionCategory.CHRONIC,
            symbol = Icons.Default.Favorite,
            color = Color(0xFFE91E63) // Pink
        ),
        HealthCondition(
            id = "asthma",
            name = "Asthma",
            category = HealthConditionCategory.CHRONIC,
            symbol = Icons.Default.Air,
            color = Color(0xFF64B5F6) // Blue
        ),
        HealthCondition(
            id = "migraines",
            name = "Migraines",
            category = HealthConditionCategory.CHRONIC,
            symbol = Icons.Default.Psychology,
            color = Color(0xFF9C27B0) // Purple
        ),
        HealthCondition(
            id = "osteoporosis",
            name = "Osteoporosis",
            category = HealthConditionCategory.CHRONIC,
            symbol = Icons.Default.MedicalServices,
            color = Color(0xFF795548) // Brown
        ),
        HealthCondition(
            id = "acid_reflux",
            name = "Acid Reflux (GERD)",
            category = HealthConditionCategory.CHRONIC,
            symbol = Icons.Default.LocalHospital,
            color = Color(0xFFE91E63) // Pink
        ),
        HealthCondition(
            id = "ibs",
            name = "Irritable Bowel Syndrome (IBS)",
            category = HealthConditionCategory.CHRONIC,
            symbol = Icons.Default.LocalHospital,
            color = Color(0xFFE91E63) // Pink
        ),
        HealthCondition(
            id = "heart_disease",
            name = "Heart Disease",
            category = HealthConditionCategory.CHRONIC,
            symbol = Icons.Default.Favorite,
            color = Color(0xFFE91E63) // Pink
        ),
        HealthCondition(
            id = "kidney_disease",
            name = "Kidney Disease",
            category = HealthConditionCategory.CHRONIC,
            symbol = Icons.Default.LocalHospital,
            color = Color(0xFFE91E63) // Pink
        ),

        // Other
        HealthCondition(
            id = "pregnancy",
            name = "Pregnancy",
            category = HealthConditionCategory.OTHER,
            symbol = Icons.Default.PregnantWoman,
            color = Color(0xFFE91E63) // Pink
        ),
        HealthCondition(
            id = "medication",
            name = "Medication",
            category = HealthConditionCategory.OTHER,
            symbol = Icons.Default.Medication,
            color = Color(0xFF2196F3) // Blue
        )
    )

    fun getConditionsByCategory(category: HealthConditionCategory): List<HealthCondition> {
        return conditions.filter { it.category == category }
    }

    fun searchConditions(query: String): List<HealthCondition> {
        return conditions.filter { 
            it.name.contains(query, ignoreCase = true) ||
            it.category.name.contains(query, ignoreCase = true)
        }
    }
} 