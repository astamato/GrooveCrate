package com.example.myapplication.data

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface DiscogsApiService {
    @GET("database/search")
    suspend fun searchRelease(
        @Query("artist") artist: String?,
        @Query("release_title") title: String?,
        @Query("type") type: String = "release"
    ): Response<DiscogsSearchResponse>

    @POST("users/{username}/collection/folders/{folderId}/releases/{releaseId}")
    suspend fun addReleaseToCollection(
        @Path("username") username: String,
        @Path("folderId") folderId: Int,
        @Path("releaseId") releaseId: Long
    ): Response<AddToCollectionResponse>
}
