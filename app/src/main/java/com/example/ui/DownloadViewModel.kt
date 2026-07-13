package com.example.ui

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.DownloadItem
import com.example.data.model.DownloadStatus
import com.example.data.repository.DownloadRepository
import com.example.service.DownloadService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.security.MessageDigest
import java.util.UUID

class DownloadViewModel(
    private val context: Context,
    private val repository: DownloadRepository
) : ViewModel() {

    // Tab indexing: 0 = Beranda, 1 = Browser, 2 = Unduhan
    private val _currentTab = MutableStateFlow(0)
    val currentTab: StateFlow<Int> = _currentTab.asStateFlow()

    // Search bar/URL input field
    private val _inputUrl = MutableStateFlow("")
    val inputUrl: StateFlow<String> = _inputUrl.asStateFlow()

    // Interactive browser URL
    private val _browserUrl = MutableStateFlow("https://www.youtube.com")
    val browserUrl: StateFlow<String> = _browserUrl.asStateFlow()

    // Trigger showing the download options sheet
    private val _showDownloadSheet = MutableStateFlow(false)
    val showDownloadSheet: StateFlow<Boolean> = _showDownloadSheet.asStateFlow()

    // Selected video information for download
    private val _targetDownloadItem = MutableStateFlow<PendingDownload?>(null)
    val targetDownloadItem: StateFlow<PendingDownload?> = _targetDownloadItem.asStateFlow()

    // Live list of all downloads from Room Database
    val downloads: StateFlow<List<DownloadItem>> = repository.allDownloads
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun selectTab(tab: Int) {
        _currentTab.value = tab
    }

    fun setInputUrl(url: String) {
        _inputUrl.value = url
    }

    fun setBrowserUrl(url: String) {
        _browserUrl.value = url
    }

    fun openDownloadSheetForUrl(url: String) {
        val cleanUrl = url.trim()
        if (cleanUrl.isEmpty()) return

        val title = guessTitle(cleanUrl)
        val platform = detectPlatform(cleanUrl)

        _targetDownloadItem.value = PendingDownload(
            url = cleanUrl,
            title = title,
            platform = platform,
            thumbnailUrl = getPlaceholderThumbnail(platform)
        )
        _showDownloadSheet.value = true
    }

    fun closeDownloadSheet() {
        _showDownloadSheet.value = false
        _targetDownloadItem.value = null
    }

    fun startDownload(resolution: String, isMp3: Boolean) {
        val target = _targetDownloadItem.value ?: return
        val id = generateMd5(target.url + resolution + isMp3)

        val item = DownloadItem(
            id = id,
            title = target.title,
            sourceUrl = target.url,
            thumbnailUrl = target.thumbnailUrl,
            filePath = "",
            status = DownloadStatus.PENDING,
            progress = 0f,
            downloadedBytes = 0,
            totalBytes = calculateEstimatedSize(resolution, isMp3),
            speed = "Menunggu",
            resolution = resolution,
            isMp3 = isMp3,
            platform = target.platform
        )

        viewModelScope.launch {
            repository.insert(item)
            
            // Trigger background service
            val intent = Intent(context, DownloadService::class.java).apply {
                action = DownloadService.ACTION_START_DOWNLOAD
                putExtra(DownloadService.EXTRA_DOWNLOAD_ID, id)
            }
            context.startService(intent)
        }

        closeDownloadSheet()
        // Switch to Downloads Tab to see the progress!
        selectTab(2)
    }

    fun pauseDownload(id: String) {
        val intent = Intent(context, DownloadService::class.java).apply {
            action = DownloadService.ACTION_PAUSE_DOWNLOAD
            putExtra(DownloadService.EXTRA_DOWNLOAD_ID, id)
        }
        context.startService(intent)
    }

    fun resumeDownload(id: String) {
        val intent = Intent(context, DownloadService::class.java).apply {
            action = DownloadService.ACTION_RESUME_DOWNLOAD
            putExtra(DownloadService.EXTRA_DOWNLOAD_ID, id)
        }
        context.startService(intent)
    }

    fun cancelDownload(id: String) {
        val intent = Intent(context, DownloadService::class.java).apply {
            action = DownloadService.ACTION_CANCEL_DOWNLOAD
            putExtra(DownloadService.EXTRA_DOWNLOAD_ID, id)
        }
        context.startService(intent)
    }

    fun deleteHistoryItem(item: DownloadItem) {
        viewModelScope.launch {
            repository.delete(item)
        }
    }

    private fun detectPlatform(url: String): String {
        val lower = url.lowercase()
        return when {
            lower.contains("youtube.com") || lower.contains("youtu.be") -> "YouTube"
            lower.contains("tiktok.com") -> "TikTok"
            lower.contains("facebook.com") || lower.contains("fb.watch") -> "Facebook"
            lower.contains("instagram.com") -> "Instagram"
            lower.contains("twitter.com") || lower.contains("x.com") -> "Twitter"
            else -> "Web"
        }
    }

    private fun guessTitle(url: String): String {
        val platform = detectPlatform(url)
        val randId = UUID.randomUUID().toString().take(4).uppercase()
        return when (platform) {
            "YouTube" -> "Video YouTube - $randId"
            "TikTok" -> "TikTok Video - $randId"
            "Facebook" -> "FB Video - $randId"
            "Instagram" -> "Insta Reel - $randId"
            "Twitter" -> "Twitter Media - $randId"
            else -> "Media Unduhan - $randId"
        }
    }

    private fun getPlaceholderThumbnail(platform: String): String {
        // Return a color or visual placeholder code we can map in the UI
        return platform
    }

    private fun calculateEstimatedSize(resolution: String, isMp3: Boolean): Long {
        if (isMp3) {
            return when {
                resolution.contains("320kbps") -> 8_500_000L // 8.5MB
                resolution.contains("192kbps") -> 5_200_000L // 5.2MB
                else -> 3_500_000L // 128kbps -> 3.5MB
            }
        } else {
            return when {
                resolution.contains("1080p") -> 64_000_000L // 64MB
                resolution.contains("720p") -> 35_000_000L // 35MB
                resolution.contains("480p") -> 18_000_000L // 18MB
                resolution.contains("360p") -> 11_000_000L // 11MB
                resolution.contains("240p") -> 6_000_000L  // 6MB
                else -> 3_200_000L    // 144p -> 3.2MB
            }
        }
    }

    private fun generateMd5(input: String): String {
        return MessageDigest.getInstance("MD5")
            .digest(input.toByteArray())
            .joinToString("") { "%02x".format(it) }
    }
}

data class PendingDownload(
    val url: String,
    val title: String,
    val platform: String,
    val thumbnailUrl: String
)

class DownloadViewModelFactory(
    private val context: Context,
    private val repository: DownloadRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DownloadViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DownloadViewModel(context, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
