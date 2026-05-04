package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import org.koin.androidx.compose.koinViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.ui.*
import com.example.myapplication.ui.theme.MyApplicationTheme
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : ComponentActivity() {
    private lateinit var cameraExecutor: ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        cameraExecutor = Executors.newSingleThreadExecutor()

        setContent {
            MyApplicationTheme {
                MainApp(cameraExecutor)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}

@Composable
fun MainApp(cameraExecutor: ExecutorService) {
    val navController = rememberNavController()
    val viewModel: MainViewModel = koinViewModel()

    val startDestination = if (viewModel.isProfileSetUp) "home" else "profile"

    NavHost(navController = navController, startDestination = startDestination) {
        composable("home") {
            HomeScreen(
                recordCount = viewModel.libraryTotalCount,
                pendingCount = viewModel.scannedRecords.count { !it.isUploaded },
                onScanClick = { navController.navigate("camera") },
                onPendingClick = { navController.navigate("inventory") },
                onRemoteLibraryClick = { navController.navigate("remote_library") },
                onProfileClick = { navController.navigate("profile") }
            )
            
            // Auto-fetch library count on home
            LaunchedEffect(Unit) {
                if (viewModel.libraryTotalCount == 0) {
                    viewModel.fetchRemoteLibrary(refresh = true)
                }
            }
        }
        composable("profile") {
            ProfileScreen(
                currentUsername = viewModel.currentUsername,
                currentToken = viewModel.currentToken,
                onSave = { username, token ->
                    viewModel.saveProfile(username, token)
                    navController.navigate("home") {
                        popUpTo("profile") { inclusive = true }
                    }
                }
            )
        }
        composable("camera") {
            MainScreen(
                cameraExecutor = cameraExecutor,
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onViewInventory = { navController.navigate("inventory") }
            )
        }
        composable("inventory") {
            InventoryScreen(
                records = viewModel.scannedRecords,
                isUploading = viewModel.isUploading,
                onDelete = { viewModel.removeRecord(it) },
                onBack = { navController.popBackStack() },
                onUploadAll = { viewModel.uploadAll() },
                onClearAll = { viewModel.clearAll() }
            )
        }
        composable("remote_library") {
            RemoteLibraryScreen(
                records = viewModel.remoteRecords,
                isLoading = viewModel.isLoadingLibrary,
                hasMore = viewModel.hasMorePages,
                onBack = { navController.popBackStack() },
                onLoadMore = { viewModel.fetchRemoteLibrary() },
                onRefresh = { viewModel.fetchRemoteLibrary(refresh = true) }
            )
        }
    }
}
