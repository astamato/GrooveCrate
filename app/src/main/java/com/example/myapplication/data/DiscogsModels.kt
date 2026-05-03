package com.example.myapplication.data

data class DiscogsSearchResponse(
    val results: List<SearchResult>,
)

data class SearchResult(
    val id: Long,
    val title: String,
    val year: String?,
    val thumb: String?,
    val resource_url: String,
    val type: String,
)

data class AddToCollectionResponse(
    val instance_id: Long,
    val resource_url: String,
)

data class DiscogsCollectionResponse(
    val pagination: DiscogsPagination,
    val releases: List<CollectionRelease>
)

data class DiscogsPagination(
    val items: Int,
    val page: Int,
    val pages: Int,
    val per_page: Int
)

data class CollectionRelease(
    val id: Long,
    val instance_id: Long,
    val folder_id: Int,
    val rating: Int,
    val basic_information: BasicInformation
)

data class BasicInformation(
    val id: Long,
    val title: String,
    val year: Int?,
    val thumb: String?,
    val cover_image: String?,
    val artists: List<ArtistInfo>
)

data class ArtistInfo(
    val name: String,
    val id: Long
)
