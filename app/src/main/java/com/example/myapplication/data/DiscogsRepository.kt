package com.example.myapplication.data

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DiscogsRepository(private val authManager: AuthManager) {
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
                    val token = authManager.getToken() ?: ""
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

    suspend fun search(
        artist: String? = null,
        title: String? = null,
        barcode: String? = null,
    ): Result<SearchResult> {
        val token = authManager.getToken()
        if (token.isNullOrEmpty()) {
            return Result.failure(Exception("Discogs Token missing. Please set up your profile."))
        }

        return try {
            val searchResponse = api.searchRelease(artist, title, barcode, format = "vinyl")
            if (!searchResponse.isSuccessful) {
                return Result.failure(Exception("Search failed: ${searchResponse.code()}"))
            }

            val result =
                searchResponse.body()?.results?.firstOrNull()
                    ?: return Result.failure(Exception("No matching record found on Discogs"))
            Result.success(result)
        } catch (e: Exception) {
            Log.e("DiscogsRepository", "Search Error", e)
            Result.failure(e)
        }
    }

    suspend fun addToCollection(releaseId: Long): Result<String> {
        val username = authManager.getUsername()
        if (username.isNullOrEmpty()) {
            return Result.failure(Exception("Discogs Username missing. Please set up your profile."))
        }
        return try {
            val addResponse = api.addReleaseToCollection(username, 1, releaseId)
            if (addResponse.isSuccessful) {
                Result.success("Successfully added to your collection!")
            } else {
                Result.failure(Exception("Failed to add to collection: ${addResponse.code()}"))
            }
        } catch (e: Exception) {
            Log.e("DiscogsRepository", "Add Error", e)
            Result.failure(e)
        }
    }

    suspend fun getCollection(page: Int = 1): Result<DiscogsCollectionResponse> {
        val username = authManager.getUsername()
        if (username.isNullOrEmpty()) {
            return Result.failure(Exception("Discogs Username missing. Please set up your profile."))
        }

        return try {
            val response = api.getCollection(username = username, page = page)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) } ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Failed to fetch collection: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e("DiscogsRepository", "Get Collection Error", e)
            Result.failure(e)
        }
    }

    suspend fun searchAndAdd(
        artist: String? = null,
        title: String? = null,
        barcode: String? = null,
    ): Result<String> {
        val searchResult = search(artist, title, barcode)
        val release = searchResult.getOrElse { return Result.failure(it) }
        return addToCollection(release.id).map { "Successfully added '${release.title}' to your collection!" }
    }
}
