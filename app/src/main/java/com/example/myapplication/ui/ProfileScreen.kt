package com.example.myapplication.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.MyApplicationTheme

@Composable
fun ProfileScreen(
    currentUsername: String?,
    currentToken: String?,
    onSave: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var username by remember { mutableStateOf(currentUsername ?: "") }
    var token by remember { mutableStateOf(currentToken ?: "") }
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .imePadding()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))
            
            Text(
                text = "DISCOGS PROFILE",
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 2.sp,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Set up your credentials to sync with your collection",
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(48.dp))

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Discogs Username") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = token,
                onValueChange = { token = it },
                label = { Text("Personal Access Token") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)
                ),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            TextButton(
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.discogs.com/settings/developers"))
                    context.startActivity(intent)
                }
            ) {
                Text("Generate token on Discogs →", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }

        Button(
            onClick = { onSave(username, token) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp)
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            enabled = username.isNotBlank() && token.isNotBlank()
        ) {
            Text("SAVE PROFILE", fontWeight = FontWeight.Bold)
        }
    }
}

@PreviewLightDark
@Composable
fun ProfileScreenPreview() {
    MyApplicationTheme {
        ProfileScreen(
            currentUsername = "alejandra.stamato",
            currentToken = "token123",
            onSave = { _, _ -> }
        )
    }
}
