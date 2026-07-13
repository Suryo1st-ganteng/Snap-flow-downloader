package com.example.ui.screens

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DownloadItem
import com.example.data.model.DownloadStatus
import com.example.ui.DownloadViewModel
import com.example.ui.theme.*

@Composable
fun DownloadsScreen(viewModel: DownloadViewModel) {
    val downloads by viewModel.downloads.collectAsState()
    val context = LocalContext.current

    var selectedSection by remember { mutableStateOf(0) } // 0 = Sedang Berjalan, 1 = Selesai

    val activeDownloads = downloads.filter {
        it.status == DownloadStatus.PENDING ||
        it.status == DownloadStatus.DOWNLOADING ||
        it.status == DownloadStatus.CONVERTING
    }

    val completedDownloads = downloads.filter {
        it.status == DownloadStatus.COMPLETED ||
        it.status == DownloadStatus.FAILED
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Toggle Tab Selector
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(DarkSurface, RoundedCornerShape(12.dp))
                .padding(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(
                        if (selectedSection == 0) AmberYellow else Color.Transparent,
                        RoundedCornerShape(10.dp)
                    )
                    .clickable { selectedSection = 0 }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Sedang Berjalan (${activeDownloads.size})",
                    color = if (selectedSection == 0) Color.Black else LightGrayText,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(
                        if (selectedSection == 1) AmberYellow else Color.Transparent,
                        RoundedCornerShape(10.dp)
                    )
                    .clickable { selectedSection = 1 }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Selesai (${completedDownloads.size})",
                    color = if (selectedSection == 1) Color.Black else LightGrayText,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }

        if (selectedSection == 0) {
            // ACTIVE QUEUE
            if (activeDownloads.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Queue,
                            contentDescription = null,
                            tint = DarkGrayText,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Tidak ada unduhan aktif",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Unduhan yang sedang berjalan atau mengantre akan muncul di sini.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = DarkGrayText,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(activeDownloads, key = { it.id }) { item ->
                        ActiveDownloadCard(item, viewModel)
                    }
                }
            }
        } else {
            // HISTORY / COMPLETED
            if (completedDownloads.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.DownloadDone,
                            contentDescription = null,
                            tint = DarkGrayText,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Belum ada file selesai",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Video atau MP3 yang selesai diunduh akan tersimpan di riwayat ini.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = DarkGrayText,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(completedDownloads, key = { it.id }) { item ->
                        CompletedDownloadCard(item, viewModel, context)
                    }
                }
            }
        }
    }
}

@Composable
fun ActiveDownloadCard(item: DownloadItem, viewModel: DownloadViewModel) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, DarkSurfaceElevated, RoundedCornerShape(16.dp))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Platform indicator badge
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(
                            when (item.platform) {
                                "YouTube" -> CoralRed.copy(alpha = 0.2f)
                                "TikTok" -> Color.Black
                                "Facebook" -> Color(0xFF1877F2).copy(alpha = 0.2f)
                                else -> AmberYellow.copy(alpha = 0.2f)
                            },
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (item.isMp3) Icons.Default.MusicNote else Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = when (item.platform) {
                            "YouTube" -> CoralRed
                            "TikTok" -> Color.White
                            "Facebook" -> Color(0xFF1877F2)
                            else -> AmberYellow
                        },
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.title,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 1
                    )
                    Text(
                        text = "${item.resolution} • ${item.platform}",
                        style = MaterialTheme.typography.bodySmall,
                        color = DarkGrayText
                    )
                }

                // Controls: Play / Pause / Cancel
                Row {
                    if (item.status == DownloadStatus.DOWNLOADING) {
                        IconButton(onClick = { viewModel.pauseDownload(item.id) }) {
                            Icon(Icons.Default.Pause, contentDescription = "Pause", tint = AmberYellow)
                        }
                    } else if (item.status == DownloadStatus.PAUSED || item.status == DownloadStatus.PENDING) {
                        IconButton(onClick = { viewModel.resumeDownload(item.id) }) {
                            Icon(Icons.Default.PlayArrow, contentDescription = "Resume", tint = AmberYellow)
                        }
                    }

                    IconButton(onClick = { viewModel.cancelDownload(item.id) }) {
                        Icon(Icons.Default.Close, contentDescription = "Batal", tint = CoralRed)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Progress Bar
            LinearProgressIndicator(
                progress = { item.progress },
                color = if (item.status == DownloadStatus.CONVERTING) CoralRed else AmberYellow,
                trackColor = DarkSurfaceElevated,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Speeds & bytes written
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (item.status == DownloadStatus.CONVERTING) "Mengonversi ke MP3..." else item.speed,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (item.status == DownloadStatus.CONVERTING) CoralRed else AmberYellow
                )

                Text(
                    text = "${formatBytes(item.downloadedBytes)} / ${formatBytes(item.totalBytes)}",
                    fontSize = 12.sp,
                    color = DarkGrayText
                )
            }
        }
    }
}

@Composable
fun CompletedDownloadCard(item: DownloadItem, viewModel: DownloadViewModel, context: Context) {
    val isSuccess = item.status == DownloadStatus.COMPLETED

    Card(
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = if (isSuccess) DarkSurfaceElevated else CoralRed.copy(alpha = 0.3f),
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        if (isSuccess) AmberYellow.copy(alpha = 0.1f) else CoralRed.copy(alpha = 0.1f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (!isSuccess) {
                        Icons.Default.ErrorOutline
                    } else if (item.isMp3) {
                        Icons.Default.MusicNote
                    } else {
                        Icons.Default.PlayCircleFilled
                    },
                    contentDescription = null,
                    tint = if (isSuccess) AmberYellow else CoralRed,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (isSuccess) "Sukses" else "Gagal",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSuccess) AmberYellow else CoralRed
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "•  ${item.resolution}  •  ${item.platform}",
                        fontSize = 12.sp,
                        color = DarkGrayText
                    )
                }

                val cleanBadgeText = when (item.platform) {
                    "TikTok" -> "TikTok Native (Tanpa Watermark)"
                    "Facebook" -> "FB Clean Direct HD"
                    "Instagram" -> "Insta Clean Reel"
                    else -> null
                }

                if (cleanBadgeText != null && isSuccess && !item.isMp3) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .background(
                                color = when (item.platform) {
                                    "TikTok" -> Color.White.copy(alpha = 0.12f)
                                    "Facebook" -> Color(0xFF1877F2).copy(alpha = 0.15f)
                                    "Instagram" -> Color(0xFFE1306C).copy(alpha = 0.15f)
                                    else -> AmberYellow.copy(alpha = 0.15f)
                                },
                                shape = RoundedCornerShape(6.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = when (item.platform) {
                                    "TikTok" -> Color.White
                                    "Facebook" -> Color(0xFF3B82F6)
                                    "Instagram" -> Color(0xFFE1306C)
                                    else -> AmberYellow
                                },
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = cleanBadgeText,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            // Options: Open/Share (if Success), Retry (if Failed), Delete
            Row {
                if (isSuccess) {
                    IconButton(onClick = {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = if (item.isMp3) "audio/*" else "video/*"
                            putExtra(Intent.EXTRA_SUBJECT, item.title)
                            putExtra(Intent.EXTRA_TEXT, "Lihat video/audio hasil unduhan SnapFlow: ${item.sourceUrl}")
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Bagikan"))
                    }) {
                        Icon(Icons.Default.Share, contentDescription = "Bagikan", tint = LightGrayText)
                    }
                } else {
                    IconButton(onClick = {
                        viewModel.openDownloadSheetForUrl(item.sourceUrl)
                    }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Coba Lagi", tint = AmberYellow)
                    }
                }

                IconButton(onClick = { viewModel.deleteHistoryItem(item) }) {
                    Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = Color.Gray)
                }
            }
        }
    }
}

private fun formatBytes(bytes: Long): String {
    if (bytes <= 0) return "0 B"
    val units = arrayOf("B", "KB", "MB", "GB")
    val digitGroups = (Math.log10(bytes.toDouble()) / Math.log10(1024.0)).toInt()
    return String.format("%.1f %s", bytes / Math.pow(1024.0, digitGroups.toDouble()), units[digitGroups])
}
