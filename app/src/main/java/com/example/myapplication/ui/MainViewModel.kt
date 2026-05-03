package com.example.myapplication.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.DiscogsRepository
import com.example.myapplication.data.ScannedRecord
import kotlinx.coroutines.launch

class MainViewModel(
    private val discogsRepository: DiscogsRepository
) : ViewModel() {

    var scannedRecords by mutableStateOf(listOf<ScannedRecord>())
        private set
        
    var isUploading by mutableStateOf(false)
        private set

    // Remote Library State
    var remoteRecords by mutableStateOf(listOf<com.example.myapplication.data.CollectionRelease>())
        private set
    var isLoadingLibrary by mutableStateOf(false)
        private set
    var libraryTotalCount by mutableIntStateOf(0)
        private set
    private var currentLibraryPage = 1
    private var totalPages = 1
    var hasMorePages by mutableStateOf(true)
        private set

    fun addRecord(record: ScannedRecord) {
        scannedRecords = scannedRecords + record
    }

    fun removeRecord(record: ScannedRecord) {
        scannedRecords = scannedRecords.filter { it.id != record.id }
    }

    fun clearAll() {
        scannedRecords = emptyList()
    }

    fun uploadAll() {
        viewModelScope.launch {
            isUploading = true
            scannedRecords.forEach { record ->
                if (!record.isUploaded) {
                    val result = discogsRepository.addToCollection(record.discogsId)
                    result.onSuccess {
                        updateRecord(record.copy(isUploaded = true, isError = false))
                    }.onFailure {
                        updateRecord(record.copy(isError = true, errorMessage = it.message))
                    }
                }
            }
            isUploading = false
            // Refresh library after successful upload
            fetchRemoteLibrary(refresh = true)
        }
    }

    fun fetchRemoteLibrary(refresh: Boolean = false) {
        if (refresh) {
            currentLibraryPage = 1
            remoteRecords = emptyList()
            hasMorePages = true
        }
        
        if (isLoadingLibrary || !hasMorePages) return
        
        viewModelScope.launch {
            isLoadingLibrary = true
            val result = discogsRepository.getCollection(currentLibraryPage)
            result.onSuccess { response ->
                remoteRecords = if (refresh) response.releases else remoteRecords + response.releases
                libraryTotalCount = response.pagination.items
                totalPages = response.pagination.pages
                currentLibraryPage++
                hasMorePages = currentLibraryPage <= totalPages
            }.onFailure {
                hasMorePages = false
            }
            isLoadingLibrary = false
        }
    }

    private fun updateRecord(record: ScannedRecord) {
        scannedRecords = scannedRecords.map {
            if (it.id == record.id) record else it
        }
    }
}
