package com.example.myapplication.data

import android.util.Log
import com.example.myapplication.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DiscogsRepository {
    private val token = BuildConfig.DISCOGS_TOKEN
    private val username = BuildConfig.DISCOGS_USERNAME
    private val userAgent = "RecordInventoryApp/1.0"

    private val api: DiscogsApiService by lazy {
        val logging =
            HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

        val client =
            OkHttpClient
                .Builder()
                .addInterceptor(logging)
                .addInterceptor { chain ->
                    val request =
                        chain
                            .request()
                            .newBuilder()
                            .addHeader("User-Agent", userAgent)
                            .addHeader("Authorization", "Discogs token=$token")
                            .build()
                    chain.proceed(request)
                }.build()

        Retrofit
            .Builder()
            .baseUrl("https://api.discogs.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DiscogsApiService::class.java)
    }

    suspend fun searchAndAdd(
        artist: String?,
        title: String?,
    ): Result<String> {
        if (token.isEmpty() || token == "YOUR_DISCOGS_TOKEN_HERE") {
            return Result.failure(Exception("Discogs Token missing in local.properties"))
        }
        if (username.isEmpty() || username == "YOUR_DISCOGS_USERNAME_HERE") {
            return Result.failure(Exception("Discogs Username missing in local.properties"))
        }

        return try {
            val searchResponse = api.searchRelease(artist, title)
            if (!searchResponse.isSuccessful) {
                return Result.failure(Exception("Search failed: ${searchResponse.code()}"))
            }

            val result =
                searchResponse.body()?.results?.firstOrNull()
                    ?: return Result.failure(Exception("No matching record found on Discogs"))

            val addResponse = api.addReleaseToCollection(username, 1, result.id)
            if (addResponse.isSuccessful) {
                Result.success("Successfully added '${result.title}' to your collection!")
            } else {
                Result.failure(Exception("Failed to add to collection: ${addResponse.code()}"))
            }
        } catch (e: Exception) {
            Log.e("DiscogsRepository", "Error", e)
            Result.failure(e)
        }
    }
}
