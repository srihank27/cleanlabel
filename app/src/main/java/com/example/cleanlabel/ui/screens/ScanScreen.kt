package com.example.cleanlabel.ui.screens

import android.Manifest
import android.content.Context
import android.util.Log
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.example.cleanlabel.viewmodel.HealthProfileViewModel
import java.util.concurrent.Executors
import java.util.concurrent.ExecutorService

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ScanScreen(
    onScanResult: (String) -> Unit,
    onBackClick: () -> Unit,
    viewModel: HealthProfileViewModel = viewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    
    // Camera permission state
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
    
    // Scanning state
    var isScanning by remember { mutableStateOf(true) }
    var recognizedText by remember { mutableStateOf("") }
    var lensFacing by remember { mutableStateOf(CameraSelector.LENS_FACING_BACK) }
    var lastRecognizedText by remember { mutableStateOf("") }
    var showHealthForm by remember { mutableStateOf(false) }
    var newCondition by remember { mutableStateOf("") }
    var showHealthConditions by remember { mutableStateOf(false) }
    var showAddConditionDialog by remember { mutableStateOf(false) }
    
    val currentProfile by viewModel.currentProfile.collectAsState()
    val conditions = currentProfile?.conditions ?: emptyList()
    
    // Create a fixed thread pool with 2 threads
    val imageAnalysisExecutor = Executors.newFixedThreadPool(2)
    
    // Process recognized text
    fun processAndPassText(text: String) {
        if (text.isNotEmpty() && isScanning) {
            isScanning = false
            onScanResult(text)
        }
    }
    
    // Request camera permission if not granted
    LaunchedEffect(Unit) {
        if (!cameraPermissionState.status.isGranted) {
            cameraPermissionState.launchPermissionRequest()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Health Scanner") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showHealthForm = !showHealthForm }) {
                        Icon(
                            imageVector = Icons.Default.HealthAndSafety,
                            contentDescription = "Health Conditions"
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
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (cameraPermissionState.status.isGranted) {
                // Camera preview
                CameraPreview(
                    context = context,
                    lifecycleOwner = lifecycleOwner,
                    lensFacing = lensFacing,
                    onTextRecognized = { text ->
                        lastRecognizedText = text
                    },
                    imageAnalysisExecutor = imageAnalysisExecutor
                )
                
                // Scanning overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Scan area indicator
                    Box(
                        modifier = Modifier
                            .size(280.dp)
                            .border(
                                width = 2.dp,
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(24.dp)
                            )
                    )
                }
                
                // Health conditions form
                if (showHealthForm) {
                    Card(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .fillMaxWidth()
                            .padding(16.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "Health Conditions",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            // Health conditions list
                            if (conditions.isEmpty()) {
                                Text(
                                    text = "No health conditions added",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            } else {
                                conditions.forEach { condition ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = condition,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        IconButton(
                                            onClick = {
                                                val updatedConditions = conditions.filter { it != condition }
                                                viewModel.updateCurrentProfile(conditions = updatedConditions)
                                            }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Remove,
                                                contentDescription = "Remove Condition",
                                                tint = MaterialTheme.colorScheme.error
                                            )
                                        }
                                    }
                                }
                            }
                            
                            // Add new condition
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = newCondition,
                                    onValueChange = { newCondition = it },
                                    modifier = Modifier.weight(1f),
                                    placeholder = { Text("Add health condition") },
                                    singleLine = true
                                )
                                IconButton(
                                    onClick = {
                                        if (newCondition.isNotBlank()) {
                                            val updatedConditions = conditions + newCondition
                                            viewModel.updateCurrentProfile(conditions = updatedConditions)
                                            newCondition = ""
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Add",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }
                
                // Camera controls
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Switch camera button
                        IconButton(
                            onClick = {
                                lensFacing = if (lensFacing == CameraSelector.LENS_FACING_BACK) {
                                    CameraSelector.LENS_FACING_FRONT
                                } else {
                                    CameraSelector.LENS_FACING_BACK
                                }
                            },
                            modifier = Modifier
                                .size(48.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = CircleShape
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Cameraswitch,
                                contentDescription = "Switch Camera",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                        
                        // Capture button
                        Button(
                            onClick = {
                                if (lastRecognizedText.isNotEmpty()) {
                                    onScanResult(lastRecognizedText)
                                }
                            },
                            shape = CircleShape,
                            modifier = Modifier.size(64.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.PhotoCamera,
                                contentDescription = "Capture",
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }
            } else {
                // Show permission request UI
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Camera Permission Required",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "This app needs camera permission to scan product labels",
                        fontSize = 16.sp,
                        modifier = Modifier.padding(horizontal = 32.dp)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = { cameraPermissionState.launchPermissionRequest() },
                        modifier = Modifier.fillMaxWidth(0.7f)
                    ) {
                        Text("Grant Permission")
                    }
                }
            }
        }
    }

    // Add Condition Dialog
    if (showAddConditionDialog) {
        AlertDialog(
            onDismissRequest = { showAddConditionDialog = false },
            title = { Text("Add Health Condition") },
            text = {
                OutlinedTextField(
                    value = newCondition,
                    onValueChange = { newCondition = it },
                    label = { Text("Condition") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newCondition.isNotBlank()) {
                            val updatedConditions = conditions + newCondition
                            viewModel.updateCurrentProfile(conditions = updatedConditions)
                            newCondition = ""
                            showAddConditionDialog = false
                        }
                    }
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddConditionDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun CameraPreview(
    context: Context,
    lifecycleOwner: androidx.lifecycle.LifecycleOwner,
    lensFacing: Int,
    onTextRecognized: (String) -> Unit,
    imageAnalysisExecutor: ExecutorService
) {
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val previewView = remember { PreviewView(context) }
    val textRecognizer = remember { TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS) }
    
    AndroidView(
        factory = { previewView },
        modifier = Modifier.fillMaxSize()
    ) {
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            
            // Preview use case
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }
            
            // Image analysis use case
            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                
            imageAnalysis.setAnalyzer(imageAnalysisExecutor) { imageProxy ->
                // Safely handle the imageProxy
                val mediaImage = imageProxy.image
                if (mediaImage != null) {
                    val inputImage = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                    // Process the image
                    textRecognizer.process(inputImage)
                        .addOnSuccessListener { visionText ->
                            // Handle recognized text
                            onTextRecognized(visionText.text)
                        }
                        .addOnFailureListener { e ->
                            // Handle failure
                            Log.e("ScanScreen", "Text recognition failed", e)
                        }
                        .addOnCompleteListener {
                            // Ensure imageProxy is closed
                            imageProxy.close()
                        }
                } else {
                    // Handle the case where mediaImage is null
                    Log.e("ScanScreen", "Media image is null")
                    imageProxy.close() // Ensure imageProxy is closed even if mediaImage is null
                }
            }
            
            try {
                // Unbind all use cases before rebinding
                cameraProvider.unbindAll()
                
                // Select front or back camera
                val cameraSelector = CameraSelector.Builder()
                    .requireLensFacing(lensFacing)
                    .build()
                
                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageAnalysis
                )
            } catch (e: Exception) {
                Log.e("CameraPreview", "Use case binding failed", e)
            }
        }, ContextCompat.getMainExecutor(context))
    }
} 