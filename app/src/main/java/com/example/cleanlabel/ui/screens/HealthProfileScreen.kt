package com.example.cleanlabel.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cleanlabel.data.HealthCondition
import com.example.cleanlabel.data.HealthConditionCategory
import com.example.cleanlabel.data.HealthConditionsRepository
import com.example.cleanlabel.data.HealthProfile
import com.example.cleanlabel.viewmodel.HealthProfileViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun HealthProfileScreen(
    onBackClick: () -> Unit,
    viewModel: HealthProfileViewModel = viewModel()
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var selectedProfile by remember { mutableStateOf<HealthProfile?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<HealthConditionCategory?>(null) }
    var expanded by remember { mutableStateOf(false) }
    
    val profiles by viewModel.profiles.collectAsState()
    val currentProfile by viewModel.currentProfile.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Health Profiles") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Profile")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            items(profiles) { profile ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    onClick = {
                        selectedProfile = profile
                        showEditDialog = true
                    }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = profile.name,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Age: ${profile.age}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        if (profile.conditions.isNotEmpty()) {
                            Text(
                                text = "Conditions: ${profile.conditions.joinToString(", ")}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            IconButton(onClick = {
                                viewModel.setCurrentProfile(profile)
                            }) {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = "Select",
                                    tint = if (profile == currentProfile) 
                                        MaterialTheme.colorScheme.primary 
                                    else 
                                        MaterialTheme.colorScheme.onSurface
                                )
                            }
                            IconButton(onClick = {
                                viewModel.deleteProfile(profile)
                            }) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        HealthProfileDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { name, age, conditions ->
                viewModel.addProfile(
                    name = name,
                    age = age,
                    conditions = conditions
                )
                showAddDialog = false
            },
            isEdit = false
        )
    }

    if (showEditDialog && selectedProfile != null) {
        HealthProfileDialog(
            onDismiss = { showEditDialog = false },
            onConfirm = { name, age, conditions ->
                viewModel.updateProfile(
                    selectedProfile!!.copy(
                        name = name,
                        age = age,
                        conditions = conditions
                    )
                )
                if (selectedProfile == currentProfile) {
                    viewModel.updateCurrentProfile(
                        name = name,
                        age = age,
                        conditions = conditions
                    )
                }
                showEditDialog = false
            },
            isEdit = true,
            initialName = selectedProfile!!.name,
            initialAge = selectedProfile!!.age,
            initialConditions = selectedProfile!!.conditions
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthProfileDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, Int, List<String>) -> Unit,
    isEdit: Boolean = false,
    initialName: String = "",
    initialAge: Int = 0,
    initialConditions: List<String> = emptyList()
) {
    var name by remember { mutableStateOf(initialName) }
    var age by remember { mutableStateOf(initialAge.toString()) }
    var conditions by remember { mutableStateOf(initialConditions) }
    var selectedCategory by remember { mutableStateOf<HealthConditionCategory?>(null) }
    var categoryExpanded by remember { mutableStateOf(false) }
    var conditionExpanded by remember { mutableStateOf(false) }
    var selectedCondition by remember { mutableStateOf<HealthCondition?>(null) }

    val filteredConditions = remember(selectedCategory) {
        if (selectedCategory != null) {
            HealthConditionsRepository.conditions.filter { it.category == selectedCategory }
        } else {
            HealthConditionsRepository.conditions
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isEdit) "Edit Profile" else "Add Profile") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = age,
                    onValueChange = { age = it },
                    label = { Text("Age") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Category Selection Dropdown
                ExposedDropdownMenuBox(
                    expanded = categoryExpanded,
                    onExpandedChange = { categoryExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedCategory?.displayName ?: "Select Category",
                        onValueChange = {},
                        readOnly = true,
                        leadingIcon = {
                            Icon(
                                imageVector = selectedCategory?.icon ?: Icons.Default.Category,
                                contentDescription = null,
                                tint = selectedCategory?.color ?: MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = categoryExpanded,
                        onDismissRequest = { categoryExpanded = false }
                    ) {
                        HealthConditionCategory.values().forEach { category ->
                            DropdownMenuItem(
                                text = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            imageVector = category.icon,
                                            contentDescription = null,
                                            tint = category.color,
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Text(category.displayName)
                                    }
                                },
                                onClick = {
                                    selectedCategory = category
                                    categoryExpanded = false
                                }
                            )
                        }
                    }
                }

                // Condition Selection Dropdown
                ExposedDropdownMenuBox(
                    expanded = conditionExpanded,
                    onExpandedChange = { conditionExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedCondition?.name ?: "Select Condition",
                        onValueChange = {},
                        readOnly = true,
                        leadingIcon = {
                            Icon(
                                imageVector = selectedCondition?.symbol ?: Icons.Default.Info,
                                contentDescription = null,
                                tint = selectedCondition?.color ?: MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = conditionExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = conditionExpanded,
                        onDismissRequest = { conditionExpanded = false }
                    ) {
                        filteredConditions.forEach { condition ->
                            DropdownMenuItem(
                                text = { 
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            imageVector = condition.symbol,
                                            contentDescription = null,
                                            tint = condition.color,
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Text(condition.name)
                                    }
                                },
                                onClick = {
                                    if (!conditions.contains(condition.name)) {
                                        conditions = conditions + condition.name
                                    }
                                    selectedCondition = null
                                    conditionExpanded = false
                                }
                            )
                        }
                    }
                }

                // Selected Conditions
                if (conditions.isNotEmpty()) {
                    Text(
                        text = "Selected Conditions:",
                        style = MaterialTheme.typography.titleSmall
                    )
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                    ) {
                        items(conditions.chunked(3)) { rowConditions ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                rowConditions.forEach { conditionName ->
                                    val condition = HealthConditionsRepository.conditions.find { it.name == conditionName }
                                    if (condition != null) {
                                        AssistChip(
                                            onClick = {
                                                conditions = conditions - conditionName
                                            },
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
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(
                        name,
                        age.toIntOrNull() ?: 0,
                        conditions
                    )
                }
            ) {
                Text(if (isEdit) "Update" else "Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
} 