package com.example.ui.screens

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.ViewGroup
import android.webkit.*
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.ui.DownloadViewModel
import com.example.ui.theme.AmberYellow
import com.example.ui.theme.CoralRed
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.LightGrayText
import com.example.ui.theme.NavigationBarColor

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun BrowserScreen(viewModel: DownloadViewModel) {
    val browserUrl by viewModel.browserUrl.collectAsState()
    val context = LocalContext.current

    var webViewInstance by remember { mutableStateOf<WebView?>(null) }
    var currentWebUrl by remember { mutableStateOf(browserUrl) }
    var pageTitle by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var detectedStreamUrl by remember { mutableStateOf<String?>(null) }

    // Intercept back button to navigate WebView back
    BackHandler(enabled = webViewInstance?.canGoBack() == true) {
        webViewInstance?.goBack()
    }

    // Reset detected video when URL changes
    LaunchedEffect(currentWebUrl) {
        detectedStreamUrl = null
    }

    // Sync ViewModel browserUrl changes with the WebView
    LaunchedEffect(browserUrl) {
        if (browserUrl != currentWebUrl) {
            webViewInstance?.loadUrl(browserUrl)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Address Bar & Controls
        Card(
            colors = CardDefaults.cardColors(containerColor = NavigationBarColor),
            shape = RoundedCornerShape(0.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Navigation controls
                    IconButton(
                        onClick = { webViewInstance?.goBack() },
                        enabled = webViewInstance?.canGoBack() == true
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Kembali",
                            tint = if (webViewInstance?.canGoBack() == true) Color.White else Color.Gray
                        )
                    }

                    IconButton(
                        onClick = { webViewInstance?.goForward() },
                        enabled = webViewInstance?.canGoForward() == true
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "Maju",
                            tint = if (webViewInstance?.canGoForward() == true) Color.White else Color.Gray
                        )
                    }

                    IconButton(onClick = { webViewInstance?.reload() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Muat Ulang", tint = Color.White)
                    }

                    // Search / URL input bar
                    OutlinedTextField(
                        value = currentWebUrl,
                        onValueChange = { currentWebUrl = it },
                        singleLine = true,
                        textStyle = MaterialTheme.typography.bodySmall.copy(color = Color.White),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AmberYellow,
                            unfocusedBorderColor = DarkSurfaceElevated,
                            focusedContainerColor = DarkSurfaceElevated,
                            unfocusedContainerColor = DarkSurfaceElevated
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        shape = RoundedCornerShape(22.dp),
                        trailingIcon = {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    color = AmberYellow,
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                IconButton(onClick = {
                                    var targetUrl = currentWebUrl.trim()
                                    if (targetUrl.isNotEmpty()) {
                                        if (!targetUrl.startsWith("http://") && !targetUrl.startsWith("https://")) {
                                            targetUrl = if (targetUrl.contains(".")) {
                                                "https://$targetUrl"
                                            } else {
                                                "https://www.google.com/search?q=$targetUrl"
                                            }
                                        }
                                        webViewInstance?.loadUrl(targetUrl)
                                    }
                                }) {
                                    Icon(Icons.Default.Search, contentDescription = "Cari", tint = AmberYellow)
                                }
                            }
                        }
                    )

                    IconButton(onClick = {
                        val homeUrl = "https://www.youtube.com"
                        webViewInstance?.loadUrl(homeUrl)
                    }) {
                        Icon(Icons.Default.Home, contentDescription = "Beranda", tint = Color.White)
                    }
                }
            }
        }

        // WebView Container
        Box(modifier = Modifier.weight(1f)) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    WebView(ctx).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        
                        settings.apply {
                            javaScriptEnabled = true
                            domStorageEnabled = true
                            databaseEnabled = true
                            useWideViewPort = true
                            loadWithOverviewMode = true
                            mediaPlaybackRequiresUserGesture = false
                            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                            userAgentString = "Mozilla/5.0 (Linux; Android 10; K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36"
                        }

                        // Enable session cookie synchronization for logins
                        CookieManager.getInstance().setAcceptCookie(true)
                        CookieManager.getInstance().setAcceptThirdPartyCookies(this, true)

                        // Sniff video elements on play / load via JavaScript interface
                        addJavascriptInterface(object {
                            @JavascriptInterface
                            fun onVideoFound(url: String) {
                                if (url.isNotEmpty() && !url.startsWith("blob:") && url != detectedStreamUrl) {
                                    detectedStreamUrl = url
                                }
                            }
                        }, "AndroidVideoExtractor")

                        webViewClient = object : WebViewClient() {
                            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                                super.onPageStarted(view, url, favicon)
                                isLoading = true
                                url?.let {
                                    currentWebUrl = it
                                    viewModel.setBrowserUrl(it)
                                }
                            }

                            override fun onPageFinished(view: WebView?, url: String?) {
                                super.onPageFinished(view, url)
                                isLoading = false
                                pageTitle = view?.title ?: ""
                                url?.let {
                                    currentWebUrl = it
                                    viewModel.setBrowserUrl(it)
                                }

                                // Inject JS snippet to monitor video tags periodically
                                view?.evaluateJavascript(
                                    """
                                    (function() {
                                        function checkForVideos() {
                                            var videos = document.getElementsByTagName('video');
                                            if (videos.length > 0) {
                                                for (var i = 0; i < videos.length; i++) {
                                                    var src = videos[i].src || '';
                                                    if (src) {
                                                        AndroidVideoExtractor.onVideoFound(src);
                                                    }
                                                    var sources = videos[i].getElementsByTagName('source');
                                                    for (var j = 0; j < sources.length; j++) {
                                                        var sSrc = sources[j].src || '';
                                                        if (sSrc) {
                                                            AndroidVideoExtractor.onVideoFound(sSrc);
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        // Monitor clicks and video play triggers
                                        document.addEventListener('play', checkForVideos, true);
                                        setInterval(checkForVideos, 2500);
                                    })();
                                    """.trimIndent(),
                                    null
                                )
                            }

                            override fun shouldInterceptRequest(
                                view: WebView?,
                                request: WebResourceRequest?
                            ): WebResourceResponse? {
                                val reqUrl = request?.url?.toString() ?: ""
                                // Sniff stream patterns in real-time request streams
                                if (reqUrl.contains(".mp4") || reqUrl.contains(".m3u8") || reqUrl.contains("googlevideo.com/videoplayback")) {
                                    if (!reqUrl.startsWith("blob:")) {
                                        detectedStreamUrl = reqUrl
                                    }
                                }
                                return super.shouldInterceptRequest(view, request)
                            }
                        }

                        webChromeClient = object : WebChromeClient() {
                            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                                super.onProgressChanged(view, newProgress)
                                if (newProgress == 100) {
                                    isLoading = false
                                }
                            }
                        }

                        loadUrl(currentWebUrl)
                        webViewInstance = this
                    }
                },
                update = { webView ->
                    webViewInstance = webView
                }
            )

            // Animated Floating Download Button
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                // If stream is detected, we glow amber. Otherwise, we still support manual page analysis when clicked!
                val isSniffed = detectedStreamUrl != null
                
                LargeFloatingActionButton(
                    onClick = {
                        val downloadUrl = detectedStreamUrl ?: currentWebUrl
                        viewModel.openDownloadSheetForUrl(downloadUrl)
                    },
                    containerColor = if (isSniffed) AmberYellow else CoralRed,
                    contentColor = Color.Black,
                    shape = CircleShape,
                    modifier = Modifier.testTag("floating_download_fab")
                ) {
                    Icon(
                        imageVector = if (isSniffed) Icons.Default.Download else Icons.Default.VideoCall,
                        contentDescription = "Unduh Video Sekarang",
                        modifier = Modifier.size(36.dp)
                    )
                }

                // Small badge showing detected status
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(bottom = 60.dp, end = 4.dp)
                        .background(
                            if (isSniffed) Color(0xFF4CAF50) else Color.DarkGray,
                            RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (isSniffed) "Video Terdeteksi!" else "Siap Download",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}
