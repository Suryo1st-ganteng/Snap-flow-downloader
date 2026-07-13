package com.example

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.example.data.database.AppDatabase
import com.example.data.repository.DownloadRepository
import com.example.ui.DownloadViewModel
import com.example.ui.DownloadViewModelFactory
import com.example.ui.components.DownloadOptionsSheet
import com.example.ui.screens.BrowserScreen
import com.example.ui.screens.DownloadsScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.theme.AmberYellow
import com.example.ui.theme.NavigationBarColor
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: DownloadViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Core Database and Repository Initialization
        val database = AppDatabase.getDatabase(this)
        val repository = DownloadRepository(database.downloadDao())
        
        // Setup View Model via Factory (Simple Constructor Injection)
        val factory = DownloadViewModelFactory(applicationContext, repository)
        viewModel = ViewModelProvider(this, factory)[DownloadViewModel::class.java]

        // Handle initial incoming Share Intent
        handleShareIntent(intent)

        setContent {
            MyApplicationTheme {
                val currentTab by viewModel.currentTab.collectAsState()
                val showDownloadSheet by viewModel.showDownloadSheet.collectAsState()
                val targetDownloadItem by viewModel.targetDownloadItem.collectAsState()
                val context = LocalContext.current

                // Runtime Notification Permission Request (Android 13+)
                val notificationPermissionLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission()
                ) { isGranted ->
                    // Logs or silent acknowledgement
                }

                LaunchedEffect(Unit) {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                        notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                    }
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        NavigationBar(
                            containerColor = NavigationBarColor,
                            tonalElevation = 8.dp
                        ) {
                            NavigationBarItem(
                                selected = currentTab == 0,
                                onClick = { viewModel.selectTab(0) },
                                icon = { Icon(Icons.Default.Home, contentDescription = null, modifier = Modifier.size(24.dp)) },
                                label = { Text("Beranda", fontWeight = FontWeight.Bold) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MaterialTheme.colorScheme.background,
                                    selectedTextColor = AmberYellow,
                                    indicatorColor = AmberYellow,
                                    unselectedIconColor = MaterialTheme.colorScheme.tertiary,
                                    unselectedTextColor = MaterialTheme.colorScheme.tertiary
                                ),
                                modifier = Modifier.testTag("nav_beranda")
                            )

                            NavigationBarItem(
                                selected = currentTab == 1,
                                onClick = { viewModel.selectTab(1) },
                                icon = { Icon(Icons.Default.Language, contentDescription = null, modifier = Modifier.size(24.dp)) },
                                label = { Text("Browser", fontWeight = FontWeight.Bold) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MaterialTheme.colorScheme.background,
                                    selectedTextColor = AmberYellow,
                                    indicatorColor = AmberYellow,
                                    unselectedIconColor = MaterialTheme.colorScheme.tertiary,
                                    unselectedTextColor = MaterialTheme.colorScheme.tertiary
                                ),
                                modifier = Modifier.testTag("nav_browser")
                            )

                            NavigationBarItem(
                                selected = currentTab == 2,
                                onClick = { viewModel.selectTab(2) },
                                icon = { Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(24.dp)) },
                                label = { Text("Unduhan", fontWeight = FontWeight.Bold) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MaterialTheme.colorScheme.background,
                                    selectedTextColor = AmberYellow,
                                    indicatorColor = AmberYellow,
                                    unselectedIconColor = MaterialTheme.colorScheme.tertiary,
                                    unselectedTextColor = MaterialTheme.colorScheme.tertiary
                                ),
                                modifier = Modifier.testTag("nav_unduhan")
                            )
                        }
                    }
                ) { innerPadding ->
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        when (currentTab) {
                            0 -> HomeScreen(
                                viewModel = viewModel,
                                onNavigateToBrowser = { url ->
                                    viewModel.setBrowserUrl(url)
                                    viewModel.selectTab(1)
                                }
                            )
                            1 -> BrowserScreen(viewModel = viewModel)
                            2 -> DownloadsScreen(viewModel = viewModel)
                        }
                    }

                    // Root Overlapping Resolution Sheet / Bottom Selection Dialog
                    DownloadOptionsSheet(
                        visible = showDownloadSheet,
                        pendingDownload = targetDownloadItem,
                        onDismiss = { viewModel.closeDownloadSheet() },
                        onConfirmDownload = { resolution, isMp3 ->
                            viewModel.startDownload(resolution, isMp3)
                        }
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleShareIntent(intent)
    }

    private fun handleShareIntent(intent: Intent?) {
        if (intent == null) return
        val action = intent.action
        val type = intent.type

        if (Intent.ACTION_SEND == action && type != null) {
            if ("text/plain" == type) {
                val sharedText = intent.getStringExtra(Intent.EXTRA_TEXT)
                if (!sharedText.isNullOrBlank()) {
                    // Try to isolate the URL from text
                    val url = extractUrl(sharedText)
                    if (url.isNotEmpty()) {
                        viewModel.setInputUrl(url)
                        viewModel.openDownloadSheetForUrl(url)
                    }
                }
            }
        }
    }

    private fun extractUrl(text: String): String {
        // Simple regex to extract URL from a shared message body
        val regex = "https?://[\\w\\-_]+(\\.[\\w\\-_]+)+([\\w\\-\\.,@?^=%&:/~\\+#]*[\\w\\-\\_@?^=%&/~\\+#])?".toRegex()
        val match = regex.find(text)
        return match?.value ?: ""
    }
}
