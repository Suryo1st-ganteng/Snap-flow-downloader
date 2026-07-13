package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.PendingDownload
import com.example.ui.theme.AmberYellow
import com.example.ui.theme.CoralRed
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.LightGrayText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadOptionsSheet(
    visible: Boolean,
    pendingDownload: PendingDownload?,
    onDismiss: () -> Unit,
    onConfirmDownload: (resolution: String, isMp3: Boolean) -> Unit
) {
    if (!visible || pendingDownload == null) return

    val isTikTokOrFBOrInsta = pendingDownload.platform == "TikTok" || pendingDownload.platform == "Facebook" || pendingDownload.platform == "Instagram"

    var selectedResolution by remember { mutableStateOf("720p") }
    var selectedIsMp3 by remember { mutableStateOf(false) }

    LaunchedEffect(pendingDownload) {
        selectedResolution = if (isTikTokOrFBOrInsta) "720p (Tanpa Watermark)" else "720p"
        selectedIsMp3 = false
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.6f))
                .clickable(onClick = onDismiss),
            contentAlignment = Alignment.BottomCenter
        ) {
            // Sliding Container
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = false) {} // Prevent dismiss click propagation
                    .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Header Bar
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.align(Alignment.CenterStart).fillMaxWidth(0.85f)) {
                            Text(
                                text = "Pilih Format & Kualitas",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = pendingDownload.title,
                                style = MaterialTheme.typography.bodyMedium,
                                color = LightGrayText,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .testTag("close_sheet_button")
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Tutup", tint = LightGrayText)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Media Platform tag
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(
                                color = when (pendingDownload.platform) {
                                    "YouTube" -> CoralRed.copy(alpha = 0.2f)
                                    "TikTok" -> Color.Black
                                    "Facebook" -> Color(0xFF1877F2).copy(alpha = 0.2f)
                                    else -> MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                },
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = pendingDownload.platform,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = when (pendingDownload.platform) {
                                "YouTube" -> CoralRed
                                "TikTok" -> Color.White
                                "Facebook" -> Color(0xFF1877F2)
                                else -> MaterialTheme.colorScheme.primary
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    val isTikTokOrFBOrInsta = pendingDownload.platform == "TikTok" || pendingDownload.platform == "Facebook" || pendingDownload.platform == "Instagram"

                    if (isTikTokOrFBOrInsta) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, AmberYellow.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = AmberYellow,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Bypass Watermark Aktif",
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        fontSize = 12.sp
                                    )
                                    Text(
                                        text = "Ekstraksi langsung dari server CDN resmi ${pendingDownload.platform}. File yang diunduh bersih tanpa watermark, sama seperti hasil ekspor aplikasi ${pendingDownload.platform} bawaan Anda.",
                                        color = LightGrayText.copy(alpha = 0.7f),
                                        fontSize = 10.sp,
                                        lineHeight = 14.sp
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // MUSIC SECTION (Auto MP3 Conversion)
                    Text(
                        text = "UNDUH SEBAGAI AUDIO (MP3 Kualitas Tinggi)",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = AmberYellow
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    val audioOptions = listOf(
                        Triple("MP3 320kbps", "8.5 MB", "Kualitas Studio"),
                        Triple("MP3 192kbps", "5.2 MB", "Kualitas Tinggi"),
                        Triple("MP3 128kbps", "3.5 MB", "Kualitas Standar")
                    )

                    audioOptions.forEach { (res, size, desc) ->
                        val isSelected = selectedIsMp3 && selectedResolution == res
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .background(
                                    if (isSelected) AmberYellow.copy(alpha = 0.15f) else Color.Transparent,
                                    RoundedCornerShape(12.dp)
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) AmberYellow else DarkSurfaceElevated,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable {
                                    selectedIsMp3 = true
                                    selectedResolution = res
                                }
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.MusicNote,
                                contentDescription = null,
                                tint = if (isSelected) AmberYellow else LightGrayText,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = res,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isSelected) AmberYellow else Color.White
                                )
                                Text(
                                    text = desc,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = LightGrayText.copy(alpha = 0.7f)
                                )
                            }
                            Text(
                                text = size,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) AmberYellow else LightGrayText
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // VIDEO SECTION (144p - 1080p)
                    Text(
                        text = "UNDUH SEBAGAI VIDEO (MP4 Resolusi Lengkap)",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = AmberYellow
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    val videoOptions = if (isTikTokOrFBOrInsta) {
                        listOf(
                            Triple("1080p (Tanpa Watermark)", "48.0 MB", "Original Publisher Export - Bersih"),
                            Triple("720p (Tanpa Watermark)", "26.0 MB", "Official HD Export - Bersih"),
                            Triple("480p (Tanpa Watermark)", "14.5 MB", "SD Export - Bersih"),
                            Triple("360p (Tanpa Watermark)", "8.0 MB", "Kualitas Ringan - Bersih")
                        )
                    } else {
                        listOf(
                            Triple("1080p", "64.0 MB", "FHD - Sangat Jernih"),
                            Triple("720p", "35.0 MB", "HD - Jernih"),
                            Triple("480p", "18.0 MB", "SD - Kualitas Sedang"),
                            Triple("360p", "11.0 MB", "Hemat Kuota"),
                            Triple("240p", "6.0 MB", "Sangat Hemat"),
                            Triple("144p", "3.2 MB", "Paling Rendah")
                        )
                    }

                    videoOptions.forEach { (res, size, desc) ->
                        val isSelected = !selectedIsMp3 && selectedResolution == res
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .background(
                                    if (isSelected) AmberYellow.copy(alpha = 0.15f) else Color.Transparent,
                                    RoundedCornerShape(12.dp)
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) AmberYellow else DarkSurfaceElevated,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable {
                                    selectedIsMp3 = false
                                    selectedResolution = res
                                }
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = if (isSelected) AmberYellow else LightGrayText,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = res,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isSelected) AmberYellow else Color.White
                                )
                                Text(
                                    text = desc,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = LightGrayText.copy(alpha = 0.7f)
                                )
                            }
                            Text(
                                text = size,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) AmberYellow else LightGrayText
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // ACTION BUTTONS
                    Button(
                        onClick = {
                            onConfirmDownload(selectedResolution, selectedIsMp3)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AmberYellow,
                            contentColor = CoralRed
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .testTag("confirm_download_button"),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = if (selectedIsMp3) "Unduh & Konversi ke MP3" else "Unduh Video ($selectedResolution)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}
