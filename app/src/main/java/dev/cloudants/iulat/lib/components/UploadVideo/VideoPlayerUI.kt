package dev.cloudants.iulat.lib.components.VideoPlayerUI

import android.net.Uri
import android.util.Base64
import android.util.Log
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import java.io.File
import java.io.FileOutputStream

@OptIn(UnstableApi::class)
@Composable
fun VideoPlayerUI(videoUrl: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var isProcessing by remember { mutableStateOf(true) }
    var fileUri by remember { mutableStateOf<Uri?>(null) }

    LaunchedEffect(videoUrl) {
        if (videoUrl.startsWith("http") || videoUrl.startsWith("content://")) {
            fileUri = Uri.parse(videoUrl)
        } else {
            try {
                val tempFile = File(context.cacheDir, "temp_report_video.mp4")
                val videoBytes = Base64.decode(videoUrl, Base64.DEFAULT)
                FileOutputStream(tempFile).use { fos ->
                    fos.write(videoBytes)
                }
                fileUri = Uri.fromFile(tempFile)
            } catch (e: Exception) {
                Log.e("VideoPlayerUI", "Error decoding video: ${e.message}")
            }
        }
        isProcessing = false
    }

    if (isProcessing) {
        Box(
            modifier = modifier.fillMaxWidth().height(250.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Color(0xFF2568EF))
        }
    } else if (fileUri != null) {
        val exoPlayer = remember(fileUri) {
            ExoPlayer.Builder(context).build().apply {
                setMediaItem(MediaItem.fromUri(fileUri!!))
                prepare()
            }
        }

        DisposableEffect(exoPlayer) {
            onDispose {
                exoPlayer.release()
            }
        }

        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                    useController = true
                }
            },
            modifier = modifier
                .fillMaxWidth()
                .height(250.dp)
        )
    }
}