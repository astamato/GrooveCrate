package com.example.myapplication.ui

import android.graphics.Bitmap
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.myapplication.data.DiscogsRepository
import com.example.myapplication.data.IdentifiedRecord
import com.example.myapplication.data.RecordIdentifier
import com.example.myapplication.util.CameraUtils
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutorService

@Composable
fun CameraContent(cameraExecutor: ExecutorService) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember { PreviewView(context) }
    val imageCapture = remember { ImageCapture.Builder().build() }
    var capturedImage by remember { mutableStateOf<Bitmap?>(null) }
    var identifiedRecord by remember { mutableStateOf<IdentifiedRecord?>(null) }
    var isIdentifying by remember { mutableStateOf(false) }
    var isAddingToDiscogs by remember { mutableStateOf(false) }
    var discogsResult by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()
    val recordIdentifier = remember { RecordIdentifier() }
    val discogsRepository = remember { DiscogsRepository() }

    LaunchedEffect(previewView) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview =
                Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    imageCapture,
                )
            } catch (exc: Exception) {
                Log.e("CameraContent", "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(context))
    }

    Column(modifier = Modifier.fillMaxSize()) {
        if (capturedImage == null) {
            Box(modifier = Modifier.weight(1f)) {
                AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize())
                Button(
                    onClick = {
                        CameraUtils.takePhoto(imageCapture, cameraExecutor) { bitmap ->
                            scope.launch {
                                capturedImage = bitmap
                                isIdentifying = true
                                identifiedRecord = recordIdentifier.identify(bitmap)
                                isIdentifying = false
                            }
                        }
                    },
                    modifier =
                        Modifier
                            .align(Alignment.BottomCenter)
                            .padding(16.dp),
                ) {
                    Text("Take Photo")
                }
            }
        } else {
            Column(
                modifier =
                    Modifier
                        .weight(1f)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Image(
                    bitmap = capturedImage!!.asImageBitmap(),
                    contentDescription = "Captured Image",
                    modifier =
                        Modifier
                            .height(300.dp)
                            .fillMaxWidth(),
                )
                Spacer(modifier = Modifier.height(16.dp))
                if (isIdentifying) {
                    CircularProgressIndicator()
                    Text("Identifying record using Gemini...")
                } else if (identifiedRecord != null) {
                    val record = identifiedRecord!!
                    Text(
                        text =
                            if (record.artist != null && record.album != null) {
                                "${record.artist} - ${record.album}"
                            } else {
                                record.rawResult
                            },
                        style = MaterialTheme.typography.headlineSmall,
                        color =
                            if (record.artist == null) {
                                MaterialTheme.colorScheme.error
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            },
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    if (isAddingToDiscogs) {
                        CircularProgressIndicator()
                        Text("Adding to Discogs...")
                    } else if (discogsResult != null) {
                        Text(discogsResult!!, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = {
                            capturedImage = null
                            identifiedRecord = null
                            discogsResult = null
                        }) {
                            Text("Done")
                        }
                    } else {
                        Row {
                            Button(onClick = {
                                capturedImage = null
                                identifiedRecord = null
                            }) {
                                Text("Retake")
                            }
                            Spacer(modifier = Modifier.width(8.dp))

                            if (record.artist != null && record.album != null) {
                                Button(onClick = {
                                    scope.launch {
                                        isAddingToDiscogs = true
                                        val result = discogsRepository.searchAndAdd(record.artist, record.album)
                                        discogsResult = result.getOrElse { it.message ?: "Unknown error" }
                                        isAddingToDiscogs = false
                                    }
                                }) {
                                    Text("Add to Collection")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
