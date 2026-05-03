package com.example.myapplication.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.data.ScannedRecord

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryScreen(
    records: List<ScannedRecord>,
    isUploading: Boolean,
    onDelete: (ScannedRecord) -> Unit,
    onBack: () -> Unit,
    onUploadAll: () -> Unit,
    onClearAll: () -> Unit
) {
    var showSuccessFeedback by remember { mutableStateOf(false) }
    
    // Check if everything was just uploaded successfully
    val allUploaded = records.isNotEmpty() && records.all { it.isUploaded }
    
    LaunchedEffect(allUploaded) {
        if (allUploaded) {
            showSuccessFeedback = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("MY INVENTORY", letterSpacing = 2.sp, fontWeight = FontWeight.Bold) },
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
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black)
                    .padding(16.dp)
            ) {
                if (showSuccessFeedback) {
                    Button(
                        onClick = {
                            onClearAll()
                            showSuccessFeedback = false
                            onBack()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("DONE", fontWeight = FontWeight.Bold)
                    }
                } else if (records.isNotEmpty() && !allUploaded) {
                    Button(
                        onClick = onUploadAll,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !isUploading
                    ) {
                        if (isUploading) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.Black)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("UPLOADING...")
                        } else {
                            Text("UPLOAD ALL TO DISCOGS", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        },
        containerColor = Color.Black
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (showSuccessFeedback) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .background(MaterialTheme.colorScheme.primary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(60.dp),
                            tint = Color.Black
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        "SYNC COMPLETE",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "All ${records.size} records have been successfully added to your Discogs collection.",
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            } else if (records.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Your inventory is empty", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(records) { record ->
                        InventoryItem(record = record, onDelete = { onDelete(record) })
                    }
                }
            }
        }
    }
}

@Composable
fun InventoryItem(record: ScannedRecord, onDelete: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (record.thumbnail != null) {
                Image(
                    bitmap = record.thumbnail.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(Color.DarkGray, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("LP", color = Color.Gray)
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = record.title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                if (record.year != null) {
                    Text(
                        text = "Year: ${record.year}",
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                if (record.isUploaded) {
                    Text(
                        text = "✓ Added to Discogs",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodySmall
                    )
                } else if (record.isError) {
                    Text(
                        text = record.errorMessage ?: "Upload failed",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            
            if (!record.isUploaded) {
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Gray)
                }
            }
        }
    }
}
