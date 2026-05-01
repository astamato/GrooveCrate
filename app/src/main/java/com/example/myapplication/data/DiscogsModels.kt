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
