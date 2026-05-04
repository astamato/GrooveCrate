package com.example.myapplication.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.MyApplicationTheme
import java.util.Calendar

@Composable
fun HomeScreen(
    recordCount: Int,
    pendingCount: Int,
    onScanClick: () -> Unit,
    onPendingClick: () -> Unit,
    onRemoteLibraryClick: () -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val greeting = remember {
        when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
            in 0..11 -> "GOOD MORNING"
            in 12..16 -> "GOOD AFTERNOON"
            else -> "GOOD EVENING"
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .padding(24.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = greeting,
                    color = Color.Gray,
                    style = MaterialTheme.typography.labelMedium,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Your crate",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color(0xFF8B1A1A), CircleShape)
                    .clickable { onProfileClick() },
                contentAlignment = Alignment.Center
            ) {
                Text("A", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Stats Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(modifier = Modifier.weight(1f), label = "Records", value = recordCount.toString())
            StatCard(modifier = Modifier.weight(1f), label = "Artists", value = (recordCount / 2).toString())
            StatCard(modifier = Modifier.weight(1f), label = "Value", value = "£${recordCount * 25}")
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Main Actions
        Text(
            text = "ACTIONS",
            color = Color.Gray,
            style = MaterialTheme.typography.labelLarge,
            letterSpacing = 1.sp
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        ActionTile(
            title = "Scan a shelf",
            subtitle = "Identify multiple records at once",
            icon = Icons.Default.CameraAlt,
            color = MaterialTheme.colorScheme.primary,
            onClick = onScanClick
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        ActionTile(
            title = "Pending Uploads",
            subtitle = "$pendingCount items ready for Discogs",
            icon = Icons.Default.PendingActions,
            color = if (pendingCount > 0) Color(0xFFFFA500) else Color.Gray,
            onClick = onPendingClick
        )

        Spacer(modifier = Modifier.height(12.dp))

        ActionTile(
            title = "Remote Library",
            subtitle = "Browse your Discogs collection",
            icon = Icons.Default.LibraryMusic,
            color = Color.Cyan,
            onClick = onRemoteLibraryClick
        )
    }
}

@Composable
fun StatCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.height(100.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = value, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Text(text = label, color = Color.Gray, fontSize = 12.sp)
        }
    }
}

@Composable
fun ActionTile(
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(color.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color)
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column {
                Text(text = title, color = Color.White, fontWeight = FontWeight.Bold)
                Text(text = subtitle, color = Color.Gray, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@PreviewLightDark
@Composable
fun HomeScreenPreview() {
    MyApplicationTheme {
        HomeScreen(
            recordCount = 312,
            pendingCount = 5,
            onScanClick = {},
            onPendingClick = {},
            onRemoteLibraryClick = {},
            onProfileClick = {}
        )
    }
}
