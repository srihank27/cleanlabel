package com.example.cleanlabel.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cleanlabel.R
import com.example.cleanlabel.data.HealthCondition
import com.example.cleanlabel.data.HealthConditionCategory
import com.example.cleanlabel.data.HealthConditionsRepository
import com.example.cleanlabel.viewmodel.HealthProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onScanClick: () -> Unit,
    onProfileClick: () -> Unit,
    viewModel: HealthProfileViewModel = viewModel()
) {
    val currentProfile by viewModel.currentProfile.collectAsState()
    val profiles by viewModel.profiles.collectAsState()
    val conditions = currentProfile?.conditions ?: emptyList()
    
    // If currentProfile is null but we have profiles, set the first one as current
    LaunchedEffect(profiles) {
        if (currentProfile == null && profiles.isNotEmpty()) {
            viewModel.setCurrentProfile(profiles.first())
        }
    }
    
    // Get the full HealthCondition objects for the current conditions
    val healthConditions = remember(conditions) {
        conditions.mapNotNull { conditionName ->
            HealthConditionsRepository.conditions.find { it.name == conditionName }
        }
    }

    // Force recomposition when profiles change
    LaunchedEffect(profiles) {
        if (currentProfile != null) {
            val updatedProfile = profiles.find { it.id == currentProfile?.id }
            if (updatedProfile != null && updatedProfile != currentProfile) {
                viewModel.setCurrentProfile(updatedProfile)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Clean Label") },
                actions = {
                    IconButton(onClick = onProfileClick) {
                        Icon(Icons.Default.Person, contentDescription = "Health Profile")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Welcome to Clean Label",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            // Health Conditions Section
            if (healthConditions.isNotEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "Your Health Conditions",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            // Group conditions by category
                            val conditionsByCategory = healthConditions.groupBy { it.category }
                            
                            conditionsByCategory.forEach { (category, conditionsList) ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = category.icon,
                                        contentDescription = null,
                                        tint = category.color,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Text(
                                        text = category.displayName,
                                        style = MaterialTheme.typography.titleSmall,
                                        color = category.color,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(100.dp)
                                ) {
                                    items(conditionsList.chunked(3)) { rowConditions ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            rowConditions.forEach { condition ->
                                                AssistChip(
                                                    onClick = { /* Handle click if needed */ },
                                                    label = {
                                                        Row(
                                                            verticalAlignment = Alignment.CenterVertically,
                                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                                        ) {
                                                            Icon(
                                                                imageVector = condition.symbol,
                                                                contentDescription = null,
                                                                modifier = Modifier.size(16.dp),
                                                                tint = condition.color
                                                            )
                                                            Text(condition.name)
                                                        }
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No Health Profile Set",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Set up your health profile to get personalized recommendations",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = onProfileClick) {
                                Icon(
                                    Icons.Default.Person,
                                    contentDescription = null,
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                                Text("Set Up Health Profile")
                            }
                        }
                    }
                }
            }

            // Scan Button
            item {
                Button(
                    onClick = onScanClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Icon(
                        Icons.Default.QrCodeScanner,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text("Scan Product Label")
                }
            }
        }
    }
} 