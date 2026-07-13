package com.example.ui.screens

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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DownloadItem
import com.example.data.model.DownloadStatus
import com.example.ui.DownloadViewModel
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: DownloadViewModel,
    onNavigateToBrowser: (url: String) -> Unit
) {
    val inputUrl by viewModel.inputUrl.collectAsState()
    val downloads by viewModel.downloads.collectAsState()
    val activeDownloads = downloads.filter { 
        it.status == DownloadStatus.DOWNLOADING || 
        it.status == DownloadStatus.PENDING ||
        it.status == DownloadStatus.CONVERTING
    }.take(3)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // High-Fidelity Elegant Dark Header
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(AmberYellow, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = null,
                            tint = CoralRed,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "SnapFlow",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .background(StatusGreen, CircleShape)
                            )
                            Text(
                                text = "BACKGROUND SERVICE ACTIVE",
                                fontSize = 9.sp,
                                color = DarkGrayText,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(
                        onClick = {},
                        modifier = Modifier
                            .size(42.dp)
                            .background(DarkSurface, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Akun",
                            tint = LightGrayText
                        )
                    }
                    IconButton(
                        onClick = {},
                        modifier = Modifier
                            .size(42.dp)
                            .background(DarkSurface, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Pengaturan",
                            tint = LightGrayText
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Paste URL Section
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, DarkSurfaceElevated, RoundedCornerShape(20.dp))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Tempel Link Video",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = AmberYellow
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = inputUrl,
                        onValueChange = { viewModel.setInputUrl(it) },
                        placeholder = { Text("Masukkan URL video atau audio di sini...", color = DarkGrayText) },
                        leadingIcon = { Icon(Icons.Default.Link, contentDescription = null, tint = DarkGrayText) },
                        trailingIcon = {
                            if (inputUrl.isNotEmpty()) {
                                IconButton(onClick = { viewModel.setInputUrl("") }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear", tint = DarkGrayText)
                                }
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AmberYellow,
                            unfocusedBorderColor = DarkSurfaceElevated,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("url_input_field"),
                        shape = RoundedCornerShape(16.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Premium Ready to Download Layout Button
                    Button(
                        onClick = {
                            if (inputUrl.isNotBlank()) {
                                viewModel.openDownloadSheetForUrl(inputUrl)
                            }
                        },
                        enabled = inputUrl.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AmberYellow,
                            disabledContainerColor = DarkSurfaceElevated.copy(alpha = 0.5f),
                            contentColor = CoralRed,
                            disabledContentColor = DarkGrayText
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .testTag("download_button"),
                        shape = RoundedCornerShape(24.dp),
                        contentPadding = PaddingValues(horizontal = 20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.FileDownload,
                                    contentDescription = null,
                                    tint = CoralRed,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = if (inputUrl.isNotBlank()) "Siap Mengunduh? Klik di sini" else "Menunggu Link Video...",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.ArrowForwardIos,
                                contentDescription = null,
                                tint = CoralRed,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // Supported Platforms Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Platform Didukung",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = AmberYellow
                )
                Text(
                    text = "Cepat & Stabil",
                    fontSize = 11.sp,
                    color = DarkGrayText
                )
            }
            Spacer(modifier = Modifier.height(12.dp))

            val platforms = listOf(
                PlatformItem("YouTube", "https://m.youtube.com", Color(0xFFEF4444)),
                PlatformItem("Facebook", "https://m.facebook.com", Color(0xFF3B82F6)),
                PlatformItem("TikTok", "https://www.tiktok.com", Color(0xFFEC4899)),
                PlatformItem("Instagram", "https://www.instagram.com", Color(0xFFE1306C)),
                PlatformItem("Twitter / X", "https://mobile.twitter.com", Color(0xFF938F99))
            )

            // 4 Column Grid Configuration matching the HTML
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    platforms.take(4).forEach { platform ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(DarkSurface, RoundedCornerShape(16.dp))
                                .border(1.dp, DarkSurfaceElevated, RoundedCornerShape(16.dp))
                                .clickable { onNavigateToBrowser(platform.url) }
                                .padding(vertical = 16.dp, horizontal = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = when (platform.name) {
                                        "YouTube" -> Icons.Default.VideoLibrary
                                        "Facebook" -> Icons.Default.Facebook
                                        "TikTok" -> Icons.Default.Camera
                                        else -> Icons.Default.PhotoCamera
                                    },
                                    contentDescription = null,
                                    tint = platform.color,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = platform.name,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = LightGrayText,
                                    textAlign = TextAlign.Center,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Twitter/X Card
                    val twitter = platforms[4]
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(DarkSurface, RoundedCornerShape(16.dp))
                            .border(1.dp, DarkSurfaceElevated, RoundedCornerShape(16.dp))
                            .clickable { onNavigateToBrowser(twitter.url) }
                            .padding(vertical = 16.dp, horizontal = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Language,
                                contentDescription = null,
                                tint = twitter.color,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = twitter.name,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = LightGrayText,
                                textAlign = TextAlign.Center,
                                maxLines = 1
                            )
                        }
                    }

                    // More Gradient Card matching the mockup
                    Box(
                        modifier = Modifier
                            .weight(3f)
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(DarkSurface, CoralRed)
                                ),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .border(1.dp, DarkSurfaceElevated, RoundedCornerShape(16.dp))
                            .clickable { onNavigateToBrowser("https://www.google.com") }
                            .padding(vertical = 16.dp, horizontal = 16.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null,
                                    tint = AmberYellow,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "Eksplor Lebih Banyak",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // Active downloads list (if any)
        if (activeDownloads.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Sedang Mengunduh (${downloads.filter { it.status == DownloadStatus.DOWNLOADING || it.status == DownloadStatus.PENDING || it.status == DownloadStatus.CONVERTING }.size})",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = AmberYellow
                    )
                    TextButton(onClick = { viewModel.selectTab(2) }) {
                        Text("Lihat Semua", color = AmberYellow, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(activeDownloads) { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .border(1.dp, DarkSurfaceElevated, RoundedCornerShape(24.dp)),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.Top) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .background(DarkSurfaceElevated, RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (item.isMp3) Icons.Default.MusicNote else Icons.Default.PlayCircleFilled,
                                    contentDescription = null,
                                    tint = AmberYellow,
                                    modifier = Modifier.size(24.dp)
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
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Box(
                                        modifier = Modifier
                                            .background(DarkBackground, RoundedCornerShape(4.dp))
                                            .border(1.dp, DarkSurfaceElevated, RoundedCornerShape(4.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = item.resolution,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = AmberYellow
                                        )
                                    }
                                    Box(
                                        modifier = Modifier
                                            .background(DarkBackground, RoundedCornerShape(4.dp))
                                            .border(1.dp, DarkSurfaceElevated, RoundedCornerShape(4.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = if (item.isMp3) "MP3" else "MP4",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = AmberYellow
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "${formatBytes(item.downloadedBytes)} / ${formatBytes(item.totalBytes)}",
                                    fontSize = 11.sp,
                                    color = DarkGrayText,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${(item.progress * 100).toInt()}% • ${item.speed}",
                                    fontSize = 11.sp,
                                    color = AmberYellow,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            LinearProgressIndicator(
                                progress = { item.progress },
                                color = AmberYellow,
                                trackColor = DarkSurfaceElevated,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(CircleShape)
                            )
                        }
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }
        }

        // Guide / Tips Section
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, DarkSurfaceElevated, RoundedCornerShape(20.dp))
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = AmberYellow,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "Petunjuk Penggunaan",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "1. Tempelkan link video di atas atau pilih salah satu platform didukung.\n" +
                                    "2. Klik tombol download atau putar video di browser bawaan.\n" +
                                    "3. Klik tombol download melayang untuk memicu deteksi resolusi video & audio.",
                            fontSize = 11.sp,
                            color = DarkGrayText,
                            lineHeight = 17.sp
                        )
                    }
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

data class PlatformItem(
    val name: String,
    val url: String,
    val color: Color
)

