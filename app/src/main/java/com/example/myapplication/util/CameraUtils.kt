package com.example.myapplication.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import java.util.concurrent.ExecutorService

object CameraUtils {
    fun takePhoto(
        imageCapture: ImageCapture,
        executor: ExecutorService,
        onImageCaptured: (Bitmap) -> Unit
    ) {
        imageCapture.takePicture(
            executor,
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(image: ImageProxy) {
                    val buffer = image.planes[0].buffer
                    val bytes = ByteArray(buffer.remaining())
                    buffer.get(bytes)
                    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    image.close()
                    onImageCaptured(bitmap)
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e("CameraUtils", "Photo capture failed: ${exception.message}", exception)
                }
            }
        )
    }
}
