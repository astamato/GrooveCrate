package com.example.myapplication.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.myapplication.data.BasicInformation
import com.example.myapplication.data.CollectionRelease
import com.example.myapplication.ui.theme.MyApplicationTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemoteLibraryScreen(
    records: List<CollectionRelease>,
    isLoading: Boolean,
    hasMore: Boolean,
    onBack: () -> Unit,
    onLoadMore: () -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pullToRefreshState = rememberPullToRefreshState()
    
    LaunchedEffect(Unit) {
        if (records.isEmpty()) {
            onLoadMore()
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("DISCOGS LIBRARY", letterSpacing = 2.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                    navigationIconContentColor = Color.White
                )
            )
        },
        containerColor = Color.Black
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = isLoading && records.isNotEmpty(),
            onRefresh = onRefresh,
            state = pullToRefreshState,
            modifier = Modifier.padding(padding)
        ) {
            if (records.isEmpty() && isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            } else if (records.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No records found in your collection", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    itemsIndexed(records) { index, record ->
                        if (index >= records.size - 5 && !isLoading && hasMore) {
                            onLoadMore()
                        }
                        RemoteInventoryItem(record = record)
                    }
                    
                    if (isLoading && hasMore) {
                        item {
                            Box(
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(modifier = Modifier.size(32.dp), color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RemoteInventoryItem(record: CollectionRelease, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val painter = rememberAsyncImagePainter(model = record.basic_information.thumb)
            Image(
                painter = painter,
                contentDescription = null,
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = record.basic_information.title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                val artistName = record.basic_information.artists.firstOrNull()?.name ?: "Unknown Artist"
                Text(
                    text = artistName,
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodySmall
                )
                if (record.basic_information.year != null && record.basic_information.year != 0) {
                    Text(
                        text = "Year: ${record.basic_information.year}",
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
fun RemoteLibraryScreenPreview() {
    MyApplicationTheme {
        RemoteLibraryScreen(
            records = listOf(
                CollectionRelease(
                    id = 1,
                    instance_id = 1,
                    folder_id = 1,
                    rating = 5,
                    basic_information = BasicInformation(
                        id = 1,
                        title = "Rumours",
                        year = 1977,
                        thumb = null,
                        cover_image = null,
                        artists = listOf()
                    )
                )
            ),
            isLoading = false,
            hasMore = false,
            onBack = {},
            onLoadMore = {},
            onRefresh = {}
        )
    }
}
