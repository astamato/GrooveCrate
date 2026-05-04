package com.example.myapplication.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.AuthManager
import com.example.myapplication.data.DiscogsRepository
import com.example.myapplication.data.ScannedRecord
import kotlinx.coroutines.launch

class MainViewModel(
    private val discogsRepository: DiscogsRepository,
    private val authManager: AuthManager
) : ViewModel() {

    var scannedRecords by mutableStateOf(listOf<ScannedRecord>())
        private set
        
    var isUploading by mutableStateOf(false)
        private set

    // Auth State
    var currentUsername by mutableStateOf(authManager.getUsername())
        private set
    var currentToken by mutableStateOf(authManager.getToken())
        private set
    val isProfileSetUp: Boolean get() = authManager.hasCredentials()

    // Remote Library State
    var remoteRecords by mutableStateOf(listOf<com.example.myapplication.data.CollectionRelease>())
        private set
    var remoteReleaseIds by mutableStateOf(setOf<Long>())
        private set
    var isLoadingLibrary by mutableStateOf(false)
        private set
    var libraryTotalCount by mutableIntStateOf(0)
        private set
    private var currentLibraryPage = 1
    private var totalPages = 1
    var hasMorePages by mutableStateOf(true)
        private set

    var userMessage by mutableStateOf<String?>(null)
        private set

    fun clearUserMessage() {
        userMessage = null
    }

    fun saveProfile(username: String, token: String) {
        authManager.saveCredentials(username, token)
        currentUsername = username
        currentToken = token
        fetchRemoteLibrary(refresh = true)
    }

    fun addRecord(record: ScannedRecord): String? {
        if (scannedRecords.any { it.discogsId == record.discogsId }) {
            return "Already in temporary list"
        }
        if (remoteReleaseIds.contains(record.discogsId)) {
            return "Already in your Discogs collection"
        }
        scannedRecords = scannedRecords + record
        return null
    }

    fun removeRecord(record: ScannedRecord) {
        if (record.isUploaded && record.instanceId != null) {
            viewModelScope.launch {
                val result = discogsRepository.removeFromCollection(record.discogsId, record.instanceId!!)
                result.onSuccess {
                    remoteReleaseIds = remoteReleaseIds - record.discogsId
                    userMessage = "Removed from Discogs"
                }.onFailure {
                    userMessage = "Failed to remove from Discogs: ${it.message}"
                }
            }
        }
        scannedRecords = scannedRecords.filter { it.id != record.id }
    }

    fun clearAll() {
        scannedRecords = emptyList()
    }

    fun clearCompleted() {
        scannedRecords = scannedRecords.filter { !it.isUploaded }
    }

    fun uploadAll() {
        viewModelScope.launch {
            isUploading = true
            scannedRecords.forEach { record ->
                if (!record.isUploaded) {
                    val result = discogsRepository.addToCollection(record.discogsId)
                    result.onSuccess { instanceId ->
                        updateRecord(record.copy(isUploaded = true, instanceId = instanceId, isError = false))
                        remoteReleaseIds = remoteReleaseIds + record.discogsId
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
        if (!isProfileSetUp) return

        if (refresh) {
            currentLibraryPage = 1
            remoteRecords = emptyList()
            remoteReleaseIds = emptySet()
            hasMorePages = true
        }
        
        if (isLoadingLibrary || !hasMorePages) return
        
        viewModelScope.launch {
            isLoadingLibrary = true
            val result = discogsRepository.getCollection(currentLibraryPage)
            result.onSuccess { response ->
                remoteRecords = if (refresh) response.releases else remoteRecords + response.releases
                remoteReleaseIds = remoteReleaseIds + response.releases.map { it.id }
                libraryTotalCount = response.pagination.items
                totalPages = response.pagination.pages
                currentLibraryPage++
                hasMorePages = currentLibraryPage <= totalPages
                
                // If we are refreshing and there are more pages, let's pre-fetch more IDs in background
                if (refresh && hasMorePages) {
                    fetchAllRemoteIds()
                }
            }.onFailure {
                hasMorePages = false
            }
            isLoadingLibrary = false
        }
    }

    fun removeReleaseFromRemote(record: com.example.myapplication.data.CollectionRelease) {
        viewModelScope.launch {
            isLoadingLibrary = true
            val result = discogsRepository.removeFromCollection(record.id, record.instance_id, record.folder_id)
            result.onSuccess {
                remoteRecords = remoteRecords.filter { it.instance_id != record.instance_id }
                remoteReleaseIds = remoteReleaseIds - record.id
                libraryTotalCount--
                userMessage = "Successfully removed"
            }.onFailure {
                userMessage = "Failed to remove: ${it.message}"
            }
            isLoadingLibrary = false
        }
    }

    private fun fetchAllRemoteIds() {
        viewModelScope.launch {
            var page = 2
            while (page <= totalPages) {
                val result = discogsRepository.getCollection(page)
                result.onSuccess { response ->
                    remoteReleaseIds = remoteReleaseIds + response.releases.map { it.id }
                    page++
                }.onFailure {
                    return@launch
                }
            }
        }
    }

    private fun updateRecord(record: ScannedRecord) {
        scannedRecords = scannedRecords.map {
            if (it.id == record.id) record else it
        }
    }
}
