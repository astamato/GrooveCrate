package com.example.myapplication.ui

import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.myapplication.data.AuthManager
import com.example.myapplication.data.DiscogsRepository
import com.example.myapplication.data.RecordIdentifier
import com.example.myapplication.data.ScannedRecord
import com.example.myapplication.ui.components.CameraOverlay
import com.example.myapplication.ui.components.IdentificationFeedback
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.util.BarcodeAnalyzer
import com.example.myapplication.util.CameraUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Composable
fun CameraContent(
    cameraExecutor: ExecutorService,
    viewModel: MainViewModel,
    onBack: () -> Unit,
    onViewInventory: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember { PreviewView(context) }
    val imageCapture = remember { ImageCapture.Builder().build() }

    var detectedBarcode by remember { mutableStateOf<String?>(null) }
    var isIdentifying by remember { mutableStateOf(false) }
    var lastAddedMessage by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()
    val recordIdentifier = koinInject<RecordIdentifier>()
    val discogsRepository = koinInject<DiscogsRepository>()

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
                                if (viewModel.addRecord(newRecord)) {
                                    lastAddedMessage = "Added: ${release.title}"
                                } else {
                                    lastAddedMessage = "Already in list: ${release.title}"
                                }
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
        modifier = modifier
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
                                if (viewModel.addRecord(newRecord)) {
                                    lastAddedMessage = "Added: ${release.title}"
                                } else {
                                    lastAddedMessage = "Already in list: ${release.title}"
                                }
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

        IdentificationFeedback(
            isIdentifying = isIdentifying,
            lastAddedMessage = lastAddedMessage
        )
    }
}

@PreviewLightDark
@Composable
fun CameraContentPreview() {
    val context = LocalContext.current
    val authManager = AuthManager(context)
    val viewModel = MainViewModel(DiscogsRepository(authManager), authManager)
    MyApplicationTheme {
        CameraContent(
            cameraExecutor = Executors.newSingleThreadExecutor(),
            viewModel = viewModel,
            onBack = {},
            onViewInventory = {}
        )
    }
}
