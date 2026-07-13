package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class DownloadStatus {
    PENDING,
    DOWNLOADING,
    PAUSED,
    COMPLETED,
    FAILED,
    CONVERTING // Auto-conversion to MP3
}

@Entity(tableName = "download_items")
data class DownloadItem(
    @PrimaryKey val id: String, // Unique download ID, typically hashed URL or UUID
    val title: String,
    val sourceUrl: String,
    val thumbnailUrl: String,
    val filePath: String,
    val status: DownloadStatus = DownloadStatus.PENDING,
    val progress: Float = 0f, // 0.0 to 1.0 (or 0 to 100)
    val downloadedBytes: Long = 0L,
    val totalBytes: Long = 0L,
    val speed: String = "0 KB/s", // Speed text format e.g., "1.2 MB/s"
    val resolution: String = "720p", // e.g. "1080p", "720p", "480p", "360p", "MP3 320kbps"
    val isMp3: Boolean = false,
    val timestamp: Long = System.currentTimeMillis(),
    val platform: String = "Web" // "YouTube", "TikTok", "Facebook", etc.
)
