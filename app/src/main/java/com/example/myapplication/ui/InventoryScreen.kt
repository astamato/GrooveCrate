package com.example.myapplication.ui

import android.graphics.Bitmap
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.data.ScannedRecord
import com.example.myapplication.ui.theme.MyApplicationTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryScreen(
    records: List<ScannedRecord>,
    isUploading: Boolean,
    totalCount: Int,
    onDelete: (ScannedRecord) -> Unit,
    onBack: () -> Unit,
    onUploadAll: () -> Unit,
    onClearAll: () -> Unit,
    onClearCompleted: () -> Unit,
    modifier: Modifier = Modifier,
    username: String? = null
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
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("MY INVENTORY", letterSpacing = 2.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground
                ),
                actions = {
                    if (records.isNotEmpty() && !showSuccessFeedback) {
                        if (records.any { it.isUploaded }) {
                            TextButton(onClick = onClearCompleted) {
                                Text("FLUSH SUCCESS", color = MaterialTheme.colorScheme.primary)
                            }
                        }
                        TextButton(onClick = onClearAll) {
                            Text("CLEAR ALL", color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
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
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (showSuccessFeedback) {
                SuccessContent(
                    count = records.size,
                    totalCount = totalCount,
                    thumbnails = records.mapNotNull { it.thumbnail },
                    onDone = {
                        onClearAll()
                        showSuccessFeedback = false
                        onBack()
                    },
                    username = username
                )
            } else if (records.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Your inventory is empty", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
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
fun InventoryItem(record: ScannedRecord, onDelete: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("LP", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = record.title,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                if (record.year != null) {
                    Text(
                        text = "Year: ${record.year}",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
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
            
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            }
        }
    }
}

@Composable
fun SuccessContent(
    count: Int,
    totalCount: Int,
    thumbnails: List<Bitmap>,
    onDone: () -> Unit,
    username: String? = null
) {
    val uriHandler = LocalUriHandler.current
    val backgroundBrush = Brush.radialGradient(
        colors = listOf(Color(0xFF32281D), Color(0xFF121212)),
        center = Offset(x = 540f, y = 500f), // Adjusted for typical screen center
        radius = 1200f
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(Color(0xFFE5B154), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Check,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = "Filed away.",
            color = Color.White,
            style = MaterialTheme.typography.displayMedium.copy(
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily.Serif
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        val description = buildAnnotatedString {
            append("$count records added to your\nDiscogs collection. Total now\nreads ")
            withStyle(style = SpanStyle(color = Color.White, fontWeight = FontWeight.Bold)) {
                append("$totalCount")
            }
            append(".")
        }

        Text(
            text = description,
            color = Color(0xFFB0B0B0),
            textAlign = TextAlign.Center,
            lineHeight = 24.sp,
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Thumbnails
        if (thumbnails.isNotEmpty()) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    thumbnails.take(4).forEach { bitmap ->
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = null,
                            modifier = Modifier
                                .size(60.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
                
                if (thumbnails.size > 4) {
                     Spacer(modifier = Modifier.height(8.dp))
                     Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        thumbnails.drop(4).take(4).forEach { bitmap ->
                            Image(
                                bitmap = bitmap.asImageBitmap(),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(RoundedCornerShape(12.dp)),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(64.dp))

        Button(
            onClick = onDone,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEBDBC6)),
            shape = RoundedCornerShape(50),
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp) // Taller button
                .padding(horizontal = 32.dp)
        ) {
            Text("Scan another shelf", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))

        TextButton(onClick = {
            val url = if (username != null) {
                "https://www.discogs.com/users/$username/collection"
            } else {
                "https://www.discogs.com"
            }
            uriHandler.openUri(url)
        }) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("View on Discogs", color = Color.Gray)
                Spacer(modifier = Modifier.width(4.dp))
                Text("↗", color = Color.Gray, fontSize = 14.sp)
            }
        }
    }
}

@PreviewLightDark
@Composable
fun SuccessContentPreview() {
    MyApplicationTheme {
        SuccessContent(
            count = 5,
            totalCount = 312,
            thumbnails = emptyList(), // Can't easily provide Bitmaps here
            onDone = {}
        )
    }
}

@PreviewLightDark
@Composable
fun InventoryScreenPreview() {
    MyApplicationTheme {
        InventoryScreen(
            records = listOf(
                ScannedRecord(title = "The Dark Side of the Moon", year = "1973", discogsId = 1, isUploaded = true),
                ScannedRecord(title = "Nevermind", year = "1991", discogsId = 2, isUploaded = true),
                ScannedRecord(title = "Kind of Blue", year = "1959", discogsId = 3, isUploaded = true)
            ),
            isUploading = false,
            totalCount = 312,
            onDelete = {},
            onBack = {},
            onUploadAll = {},
            onClearAll = {},
            onClearCompleted = {}
        )
    }
}
