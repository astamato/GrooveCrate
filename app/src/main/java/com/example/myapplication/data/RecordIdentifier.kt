package com.example.myapplication.data

import android.graphics.Bitmap
import android.util.Log
import com.example.myapplication.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class IdentifiedRecord(
    val artist: String?,
    val album: String?,
    val rawResult: String,
)

class RecordIdentifier {
    private val apiKey = BuildConfig.GEMINI_API_KEY

    suspend fun identify(bitmap: Bitmap): IdentifiedRecord {
        if (apiKey.isEmpty() || apiKey == "YOUR_API_KEY_HERE") {
            return IdentifiedRecord(null, null, "Gemini API Key missing. Please provide one in local.properties")
        }

        return withContext(Dispatchers.IO) {
            val modelsToTry = listOf("gemini-flash-latest")
            var lastError = ""

            for (modelName in modelsToTry) {
                try {
                    val generativeModel =
                        GenerativeModel(
                            modelName = modelName,
                            apiKey = apiKey,
                        )

                    val inputContent =
                        content {
                            image(bitmap)
                            text(
                                "Identify this vinyl record cover. Return the Artist and Album title. " +
                                        "Format the output as JSON with 'artist' and 'album' keys. " +
                                        "If you are not sure, give your best guess.",
                            )
                        }

                    val response = generativeModel.generateContent(inputContent)
                    val text = response.text
                    if (text != null) {
                        // Very simple JSON extraction
                        val artist = extractJsonField(text, "artist")
                        val album = extractJsonField(text, "album")
                        return@withContext IdentifiedRecord(artist, album, text)
                    }
                } catch (e: Exception) {
                    lastError = e.message ?: "Unknown error"
                    Log.w("RecordIdentifier", "Failed with $modelName: $lastError")
                    if (!lastError.contains("404") &&
                        !lastError.contains("not found") &&
                        !lastError.contains("available")
                    ) {
                        break
                    }
                }
            }
            IdentifiedRecord(null, null, "Error: $lastError")
        }
    }

    private fun extractJsonField(
        json: String,
        field: String,
    ): String? {
        val pattern = "\"$field\"\\s*:\\s*\"([^\"]+)\"".toRegex()
        return pattern.find(json)?.groupValues?.get(1)
    }
}
