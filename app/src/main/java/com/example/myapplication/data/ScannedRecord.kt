package com.example.myapplication.data

import android.graphics.Bitmap

data class ScannedRecord(
    val id: String =
        java.util.UUID
            .randomUUID()
            .toString(),
    val title: String,
    val year: String?,
    val discogsId: Long,
    val thumbnail: Bitmap? = null,
    var isUploaded: Boolean = false,
    var instanceId: Long? = null,
    var isError: Boolean = false,
    var errorMessage: String? = null,
)
