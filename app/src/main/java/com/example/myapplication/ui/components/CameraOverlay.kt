package com.example.myapplication.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.MyApplicationTheme

@Composable
fun CameraOverlay(
    itemCount: Int,
    onClose: () -> Unit,
    onTakePhoto: () -> Unit,
    onViewInventory: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 48.dp, start = 24.dp, end = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onClose) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White, modifier = Modifier.size(28.dp))
            }
            Text(
                "SCAN MODE",
                color = Color.White,
                letterSpacing = 2.sp,
                fontWeight = FontWeight.Light,
                fontSize = 14.sp,
            )
            Box {
                Icon(
                    Icons.AutoMirrored.Filled.List,
                    contentDescription = "Inventory",
                    tint = Color.White,
                    modifier = Modifier
                        .size(28.dp)
                        .clickable { onViewInventory() },
                )
                if (itemCount > 0) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = 8.dp, y = (-8).dp)
                            .size(18.dp)
                            .background(MaterialTheme.colorScheme.primary, CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            itemCount.toString(),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                        )
                    }
                }
            }
        }

        // Scanning Box Corners
        Box(
            modifier = Modifier
                .size(280.dp)
                .align(Alignment.Center),
        ) {
            val cornerSize = 40.dp
            val strokeWidth = 3.dp
            val color = MaterialTheme.colorScheme.primary

            // Corners drawing
            Modifier.drawBehindCorner(topLeft = true, color = color, stroke = strokeWidth).let { m ->
                Box(Modifier.align(Alignment.TopStart).size(cornerSize).then(m))
            }
            Modifier.drawBehindCorner(topRight = true, color = color, stroke = strokeWidth).let { m ->
                Box(Modifier.align(Alignment.TopEnd).size(cornerSize).then(m))
            }
            Modifier.drawBehindCorner(bottomLeft = true, color = color, stroke = strokeWidth).let { m ->
                Box(Modifier.align(Alignment.BottomStart).size(cornerSize).then(m))
            }
            Modifier.drawBehindCorner(bottomRight = true, color = color, stroke = strokeWidth).let { m ->
                Box(Modifier.align(Alignment.BottomEnd).size(cornerSize).then(m))
            }
        }

        // Mode Selector
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 140.dp)
                .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            ModeButton("SINGLE", active = false)
            ModeButton("BULK", active = true)
            ModeButton("STACK", active = false)
        }

        // Capture Button
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 40.dp)
                .size(80.dp)
                .border(4.dp, Color.White, CircleShape)
                .padding(6.dp)
                .clip(CircleShape)
                .background(Color.White)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                ) { onTakePhoto() },
        )
    }
}

@Composable
fun ModeButton(
    text: String,
    active: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (active) Color.White else Color.Transparent)
            .padding(horizontal = 16.dp, vertical = 6.dp),
    ) {
        Text(
            text,
            color = if (active) Color.Black else Color.Gray,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@PreviewLightDark
@Composable
fun CameraOverlayPreview() {
    MyApplicationTheme {
        Box(Modifier.background(Color.Black).fillMaxSize()) {
            CameraOverlay(
                itemCount = 5,
                onClose = {},
                onTakePhoto = {},
                onViewInventory = {}
            )
        }
    }
}
