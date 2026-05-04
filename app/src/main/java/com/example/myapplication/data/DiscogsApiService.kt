package com.example.myapplication.data

import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface DiscogsApiService {
    @GET("database/search")
    suspend fun searchRelease(
        @Query("artist") artist: String?,
        @Query("release_title") title: String?,
        @Query("barcode") barcode: String? = null,
        @Query("format") format: String? = "vinyl",
        @Query("type") type: String = "release",
    ): Response<DiscogsSearchResponse>

    @POST("users/{username}/collection/folders/{folderId}/releases/{releaseId}")
    suspend fun addReleaseToCollection(
        @Path("username") username: String,
        @Path("folderId") folderId: Int,
        @Path("releaseId") releaseId: Long,
    ): Response<AddToCollectionResponse>

    @GET("users/{username}/collection/folders/{folderId}/releases")
    suspend fun getCollection(
        @Path("username") username: String,
        @Path("folderId") folderId: Int = 0, // 0 is 'All'
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 100,
        @Query("sort") sort: String = "added",
        @Query("sort_order") sortOrder: String = "desc"
    ): Response<DiscogsCollectionResponse>

    @DELETE("users/{username}/collection/folders/{folderId}/releases/{releaseId}/instances/{instanceId}")
    suspend fun deleteReleaseFromCollection(
        @Path("username") username: String,
        @Path("folderId") folderId: Int,
        @Path("releaseId") releaseId: Long,
        @Path("instanceId") instanceId: Long
    ): Response<Unit>
}
