package com.example.cleanlabel.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.cleanlabel.data.IngredientAnalysis
import com.example.cleanlabel.viewmodel.IngredientAnalysisViewModel
import android.content.Intent

// Extension properties for warning colors
val ColorScheme.warning: Color
    get() = Color(0xFFFFA000) // Amber 700

val ColorScheme.warningContainer: Color
    get() = Color(0xFFFFECB3) // Amber 100

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToxinNotificationScreen(
    viewModel: IngredientAnalysisViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val analysisResult by viewModel.analysisResult.collectAsState()
    val context = LocalContext.current
    var showFilters by remember { mutableStateOf(false) }
    var selectedFilters by remember { mutableStateOf(setOf("carcinogens", "neurotoxins", "additives")) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ingredient Analysis") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // Filter button
                    IconButton(onClick = { showFilters = true }) {
                        Icon(Icons.Default.FilterList, contentDescription = "Filter results")
                    }
                    // Share button
                    IconButton(
                        onClick = {
                            analysisResult?.let { analysis ->
                                val shareIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_TEXT, analysis.summary)
                                }
                                context.startActivity(Intent.createChooser(shareIntent, "Share Analysis"))
                            }
                        }
                    ) {
                        Icon(Icons.Default.Share, contentDescription = "Share results")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = modifier.padding(paddingValues)) {
            ToxinNotificationContent(
                analysis = analysisResult,
                selectedFilters = selectedFilters,
                modifier = Modifier.fillMaxSize()
            )

            // Filter Dialog
            if (showFilters) {
                AlertDialog(
                    onDismissRequest = { showFilters = false },
                    title = { Text("Filter Results") },
                    text = {
                        Column {
                            CheckboxItem(
                                text = "Show Carcinogens",
                                checked = selectedFilters.contains("carcinogens"),
                                onCheckedChange = { checked ->
                                    selectedFilters = if (checked) {
                                        selectedFilters + "carcinogens"
                                    } else {
                                        selectedFilters - "carcinogens"
                                    }
                                }
                            )
                            CheckboxItem(
                                text = "Show Neurotoxins",
                                checked = selectedFilters.contains("neurotoxins"),
                                onCheckedChange = { checked ->
                                    selectedFilters = if (checked) {
                                        selectedFilters + "neurotoxins"
                                    } else {
                                        selectedFilters - "neurotoxins"
                                    }
                                }
                            )
                            CheckboxItem(
                                text = "Show Concerning Additives",
                                checked = selectedFilters.contains("additives"),
                                onCheckedChange = { checked ->
                                    selectedFilters = if (checked) {
                                        selectedFilters + "additives"
                                    } else {
                                        selectedFilters - "additives"
                                    }
                                }
                            )
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = { showFilters = false }) {
                            Text("Done")
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun CheckboxItem(
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
        Text(text)
    }
}

@Composable
private fun ToxinNotificationContent(
    analysis: IngredientAnalysis?,
    selectedFilters: Set<String>,
    modifier: Modifier = Modifier
) {
    if (analysis == null) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Summary Card
        NotificationCard(
            analysis = analysis,
            modifier = Modifier.fillMaxWidth()
        )

        // Detailed Sections
        if (analysis.hasHarmfulSubstances) {
            // Carcinogens Alert
            if (analysis.carcinogens.isNotEmpty() && selectedFilters.contains("carcinogens")) {
                AlertSection(
                    title = "Carcinogen Alert",
                    icon = Icons.Default.Warning,
                    iconTint = MaterialTheme.colorScheme.error,
                    items = analysis.carcinogens,
                    description = "These ingredients have been identified as potential carcinogens by authoritative sources.",
                    severityLevel = "High Risk",
                    sourceInfo = "Source: International Agency for Research on Cancer (IARC)",
                    sourceUrl = "https://www.iarc.who.int/",
                    detailedInfo = "Carcinogens are substances capable of causing cancer in living tissue. " +
                            "The presence of these ingredients has been linked to increased cancer risk in scientific studies."
                )
            }

            // Neurotoxins Alert
            if (analysis.neurotoxins.isNotEmpty() && selectedFilters.contains("neurotoxins")) {
                AlertSection(
                    title = "Neurotoxin Alert",
                    icon = Icons.Default.PriorityHigh,
                    iconTint = MaterialTheme.colorScheme.error,
                    items = analysis.neurotoxins,
                    description = "These ingredients have been identified as potential neurotoxins that may affect the nervous system.",
                    severityLevel = "High Risk",
                    sourceInfo = "Source: Environmental Protection Agency (EPA)",
                    sourceUrl = "https://www.epa.gov/",
                    detailedInfo = "Neurotoxins are substances that can damage the nervous system. " +
                            "These compounds may affect brain function and neural development."
                )
            }

            // Additives Alert
            if (analysis.concerningAdditives.isNotEmpty() && selectedFilters.contains("additives")) {
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary
                            )
                            Text(
                                text = "Concerning Additives",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        
                        Text(
                            text = "The following additives have been flagged for potential health concerns:",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        
                        analysis.concerningAdditives.forEach { (additive, concern) ->
                            AdditiveCard(
                                additive = additive,
                                concern = concern,
                                severityLevel = when {
                                    concern.contains("carcinogen", ignoreCase = true) -> "High Risk"
                                    concern.contains("inflammatory", ignoreCase = true) -> "Medium Risk"
                                    else -> "Low Risk"
                                }
                            )
                        }

                        // Source information
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                        Text(
                            text = "Source: FDA Food Additive Listings",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AdditiveCard(
    additive: String,
    concern: String,
    severityLevel: String,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = additive,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                SeverityChip(severityLevel)
            }
            Text(
                text = concern,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SeverityChip(
    severityLevel: String,
    modifier: Modifier = Modifier
) {
    val (color, containerColor) = when (severityLevel) {
        "High Risk" -> MaterialTheme.colorScheme.error to MaterialTheme.colorScheme.errorContainer
        "Medium Risk" -> MaterialTheme.colorScheme.warning to MaterialTheme.colorScheme.warningContainer
        else -> MaterialTheme.colorScheme.secondary to MaterialTheme.colorScheme.secondaryContainer
    }

    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.small,
        color = containerColor
    ) {
        Text(
            text = severityLevel,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = color
        )
    }
}

@Composable
private fun AlertSection(
    title: String,
    icon: ImageVector,
    iconTint: Color,
    items: List<String>,
    description: String,
    severityLevel: String,
    sourceInfo: String,
    sourceUrl: String,
    detailedInfo: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    
    ElevatedCard(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint
                    )
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                SeverityChip(severityLevel)
            }
            
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = detailedInfo,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            items.forEach { item ->
                ListItem(
                    headlineContent = { Text(item) },
                    leadingContent = {
                        Icon(
                            Icons.Default.Circle,
                            contentDescription = null,
                            modifier = Modifier.size(8.dp)
                        )
                    }
                )
            }

            // Source information with link
            TextButton(
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse(sourceUrl))
                    context.startActivity(intent)
                }
            ) {
                Text(sourceInfo)
                Icon(
                    Icons.Default.OpenInNew,
                    contentDescription = "Open source website",
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
private fun NotificationCard(
    analysis: IngredientAnalysis,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = if (analysis.hasHarmfulSubstances)
                MaterialTheme.colorScheme.errorContainer
            else
                MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (analysis.hasHarmfulSubstances)
                        Icons.Default.Warning
                    else
                        Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = if (analysis.hasHarmfulSubstances)
                        MaterialTheme.colorScheme.error
                    else
                        MaterialTheme.colorScheme.primary
                )
                Text(
                    text = if (analysis.hasHarmfulSubstances)
                        "Warning: Harmful Substances Detected"
                    else
                        "No Known Harmful Substances Found",
                    style = MaterialTheme.typography.titleLarge,
                    color = if (analysis.hasHarmfulSubstances)
                        MaterialTheme.colorScheme.onErrorContainer
                    else
                        MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            if (analysis.hasHarmfulSubstances) {
                Text(
                    text = buildString {
                        append("Found: ")
                        val items = mutableListOf<String>()
                        if (analysis.carcinogens.isNotEmpty()) {
                            items.add("${analysis.carcinogens.size} carcinogen(s)")
                        }
                        if (analysis.neurotoxins.isNotEmpty()) {
                            items.add("${analysis.neurotoxins.size} neurotoxin(s)")
                        }
                        if (analysis.concerningAdditives.isNotEmpty()) {
                            items.add("${analysis.concerningAdditives.size} concerning additive(s)")
                        }
                        append(items.joinToString(", "))
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
    }
} 