package com.example.cleanlabel.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cleanlabel.data.HealthCondition
import com.example.cleanlabel.data.HealthConditionCategory
import com.example.cleanlabel.data.HealthConditionsRepository
import com.example.cleanlabel.viewmodel.HealthProfileViewModel

// Define data classes and enums needed
data class Product(
    val name: String,
    val ingredients: List<String>,
    val allergens: List<String>
)

data class CustomerProfile(
    val healthConditions: List<HealthCondition>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    scanResult: String,
    onBackClick: () -> Unit,
    onHomeClick: () -> Unit,
    onAnalyzeClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Scan Results") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* Share functionality */ }) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share"
                        )
                    }
                    IconButton(onClick = onHomeClick) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "Home"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        onClick = { onAnalyzeClick(scanResult) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Analyze Label")
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Scanned Text Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Scanned Text",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = scanResult,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
fun ProductResultDisplay(isSuitable: Boolean) {
    if (isSuitable) {
        Text(
            text = "This product is suitable for you!",
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(16.dp)
        )
    } else {
        Text(
            text = "This product is not suitable for you.",
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(16.dp)
        )
    }
}

fun isProductSuitable(product: Product, profile: CustomerProfile): Boolean {
    profile.healthConditions.forEach { condition ->
        when (condition.name) {
            "High Blood Pressure" -> {
                if (product.ingredients.any { it.contains("sodium", ignoreCase = true) }) {
                    return false
                }
            }
            "Irritable Bowel Syndrome (IBS)" -> {
                if (product.ingredients.any { it.contains("spicy", ignoreCase = true) }) {
                    return false
                }
            }
            "Diabetes" -> {
                if (product.ingredients.any { it.contains("sugar", ignoreCase = true) }) {
                    return false
                }
            }
            else -> {
                if (product.allergens.isNotEmpty()) {
                    return false
                }
            }
        }
    }
    return true
}

fun parseProductFromText(text: String): Product {
    // Simple parsing logic - you should implement more sophisticated parsing
    val ingredients = text.split(",").map { it.trim() }
    val allergens = ingredients.filter { it.contains("contains", ignoreCase = true) }
    return Product(
        name = text.split("\n").firstOrNull() ?: "",
        ingredients = ingredients,
        allergens = allergens
    )
}

@Composable
fun NutritionalItem(name: String, value: String, isHighlighted: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .then(
                if (isHighlighted) {
                    Modifier
                        .background(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                } else {
                    Modifier
                }
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = name,
            fontSize = 16.sp,
            fontWeight = if (isHighlighted) FontWeight.Medium else FontWeight.Normal
        )
        
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = if (isHighlighted) FontWeight.Bold else FontWeight.Normal,
            color = if (isHighlighted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun HealthHistoryInput(onProfileUpdated: (CustomerProfile) -> Unit) {
    val healthConditions = HealthConditionsRepository.conditions
    val selectedConditions = remember { mutableStateListOf<HealthCondition>() }

    Column {
        Text("Select your health conditions:")
        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            items(
                items = healthConditions,
                key = { it.id }
            ) { condition ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = selectedConditions.contains(condition),
                        onCheckedChange = {
                            if (it) {
                                selectedConditions.add(condition)
                            } else {
                                selectedConditions.remove(condition)
                            }
                        }
                    )
                    Text(condition.name)
                }
            }
        }
        Button(onClick = {
            onProfileUpdated(CustomerProfile(selectedConditions))
        }) {
            Text("Save Health History")
        }
    }
} 