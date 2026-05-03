package com.example.myapplication.ui

import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.myapplication.data.DiscogsRepository
import com.example.myapplication.data.RecordIdentifier
import com.example.myapplication.data.ScannedRecord
import com.example.myapplication.util.BarcodeAnalyzer
import com.example.myapplication.util.CameraUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutorService

@Composable
fun CameraContent(
    cameraExecutor: ExecutorService,
    viewModel: MainViewModel,
    onBack: () -> Unit,
    onViewInventory: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember { PreviewView(context) }
    val imageCapture = remember { ImageCapture.Builder().build() }

    var detectedBarcode by remember { mutableStateOf<String?>(null) }
    var isIdentifying by remember { mutableStateOf(false) }
    var lastAddedMessage by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()
    val recordIdentifier = remember { RecordIdentifier() }
    val discogsRepository = remember { DiscogsRepository() }

    val imageAnalysis = remember {
        ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
            .also {
                it.setAnalyzer(cameraExecutor, BarcodeAnalyzer { barcode ->
                    if (detectedBarcode == null && !isIdentifying) {
                        detectedBarcode = barcode
                        isIdentifying = true
                        scope.launch {
                            val result = discogsRepository.search(barcode = barcode)
                            result.onSuccess { release ->
                                val newRecord = ScannedRecord(
                                    title = release.title,
                                    year = release.year,
                                    discogsId = release.id
                                )
                                viewModel.addRecord(newRecord)
                                lastAddedMessage = "Added: ${release.title}"
                                delay(2000)
                                lastAddedMessage = null
                            }.onFailure {
                                lastAddedMessage = "Error: Barcode not found"
                                delay(2000)
                                lastAddedMessage = null
                            }
                            detectedBarcode = null
                            isIdentifying = false
                        }
                    }
                })
            }
    }

    LaunchedEffect(previewView) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    imageCapture,
                    imageAnalysis,
                )
            } catch (exc: Exception) {
                Log.e("CameraContent", "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(context))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
    ) {
        // Camera Preview
        AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize())

        // Elegant Overlays
        CameraOverlay(
            itemCount = viewModel.scannedRecords.size,
            onClose = onBack,
            onTakePhoto = {
                CameraUtils.takePhoto(imageCapture, cameraExecutor) { bitmap ->
                    scope.launch {
                        isIdentifying = true
                        val identified = recordIdentifier.identify(bitmap)
                        if (identified.artist != null && identified.album != null) {
                            val result = discogsRepository.search(
                                artist = identified.artist,
                                title = identified.album,
                            )
                            result.onSuccess { release ->
                                val newRecord = ScannedRecord(
                                    title = release.title,
                                    year = release.year,
                                    discogsId = release.id,
                                    thumbnail = bitmap,
                                )
                                viewModel.addRecord(newRecord)
                                lastAddedMessage = "Added: ${release.title}"
                                delay(2000)
                                lastAddedMessage = null
                            }.onFailure {
                                lastAddedMessage = "Error: Record not found"
                                delay(2000)
                                lastAddedMessage = null
                            }
                        } else {
                            lastAddedMessage = "Error: Could not identify"
                            delay(2000)
                            lastAddedMessage = null
                        }
                        isIdentifying = false
                    }
                }
            },
            onViewInventory = onViewInventory
        )

        // Identification Status / Feedback
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 200.dp),
            contentAlignment = Alignment.Center,
        ) {
            if (isIdentifying) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("IDENTIFYING...", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }

            AnimatedVisibility(
                visible = lastAddedMessage != null,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(8.dp),
                ) {
                    Text(
                        lastAddedMessage ?: "",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}

@Composable
private fun CameraOverlay(
    itemCount: Int,
    onClose: () -> Unit,
    onTakePhoto: () -> Unit,
    onViewInventory: () -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        // Top Bar
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(top = 48.dp, start = 24.dp, end = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onClose) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White, modifier = Modifier.size(28.dp))
            }
            Text(
                "SCAN MODE",
                color = Color.White,
                letterSpacing = 2.sp,
                fontWeight = FontWeight.Light,
                fontSize = 14.sp,
            )
            Box {
                Icon(
                    Icons.AutoMirrored.Filled.List,
                    contentDescription = "Inventory",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp).clickable { onViewInventory() },
                )
                if (itemCount > 0) {
                    Box(
                        modifier =
                            Modifier
                                .align(Alignment.TopEnd)
                                .offset(x = 8.dp, y = (-8).dp)
                                .size(18.dp)
                                .background(MaterialTheme.colorScheme.primary, CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            itemCount.toString(),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                        )
                    }
                }
            }
        }

        // Scanning Box Corners
        Box(
            modifier =
                Modifier
                    .size(280.dp)
                    .align(Alignment.Center),
        ) {
            val cornerSize = 40.dp
            val strokeWidth = 3.dp
            val color = MaterialTheme.colorScheme.primary

            // Corners drawing
            Modifier.drawBehindCorner(topLeft = true, color = color, stroke = strokeWidth).let { m ->
                Box(Modifier.align(Alignment.TopStart).size(cornerSize).then(m))
            }
            Modifier.drawBehindCorner(topRight = true, color = color, stroke = strokeWidth).let { m ->
                Box(Modifier.align(Alignment.TopEnd).size(cornerSize).then(m))
            }
            Modifier.drawBehindCorner(bottomLeft = true, color = color, stroke = strokeWidth).let { m ->
                Box(Modifier.align(Alignment.BottomStart).size(cornerSize).then(m))
            }
            Modifier.drawBehindCorner(bottomRight = true, color = color, stroke = strokeWidth).let { m ->
                Box(Modifier.align(Alignment.BottomEnd).size(cornerSize).then(m))
            }
        }

        // Mode Selector
        Row(
            modifier =
                Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 140.dp)
                    .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            ModeButton("SINGLE", active = false)
            ModeButton("BULK", active = true)
            ModeButton("STACK", active = false)
        }

        // Capture Button
        Box(
            modifier =
                Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 40.dp)
                    .size(80.dp)
                    .border(4.dp, Color.White, CircleShape)
                    .padding(6.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) { onTakePhoto() },
        )
    }
}

@Composable
private fun ModeButton(
    text: String,
    active: Boolean,
) {
    Box(
        modifier =
            Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(if (active) Color.White else Color.Transparent)
                .padding(horizontal = 16.dp, vertical = 6.dp),
    ) {
        Text(
            text,
            color = if (active) Color.Black else Color.Gray,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

private fun Modifier.drawBehindCorner(
    topLeft: Boolean = false,
    topRight: Boolean = false,
    bottomLeft: Boolean = false,
    bottomRight: Boolean = false,
    color: Color,
    stroke: Dp,
) = this.drawBehind {
    val s = stroke.toPx()
    if (topLeft) {
        drawLine(color, Offset(0f, 0f), Offset(size.width, 0f), s)
        drawLine(color, Offset(0f, 0f), Offset(0f, size.height), s)
    }
    if (topRight) {
        drawLine(color, Offset(size.width, 0f), Offset(0f, 0f), s)
        drawLine(color, Offset(size.width, 0f), Offset(size.width, size.height), s)
    }
    if (bottomLeft) {
        drawLine(color, Offset(0f, size.height), Offset(size.width, size.height), s)
        drawLine(color, Offset(0f, size.height), Offset(0f, 0f), s)
    }
    if (bottomRight) {
        drawLine(color, Offset(size.width, size.height), Offset(0f, size.height), s)
        drawLine(color, Offset(size.width, size.height), Offset(size.width, 0f), s)
    }
}
