package com.setembreiros.artis.ui.commponents

import android.content.Context
import android.net.Uri
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.ImageButton
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView

private const val MIN_BUFFER_MS = 5000
private const val MAX_BUFFER_MS = 10000
private const val BUFFER_FOR_PLAYBACK_MS = 1000
private const val BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS = 2000

@OptIn(UnstableApi::class)
@Composable
fun RememberExoPlayer(context: Context, uri: Uri, isMuted: Boolean) = remember {
    ExoPlayer.Builder(context)
        .setLoadControl(
            DefaultLoadControl.Builder()
            .setBufferDurationsMs(
                MIN_BUFFER_MS,
                MAX_BUFFER_MS,
                BUFFER_FOR_PLAYBACK_MS,
                BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS
            )
            .build())
        .build().apply {
        setMediaItem(MediaItem.fromUri(uri))
        prepare()
        playWhenReady = true
        volume = if (isMuted) 0f else 1f
    }
}

@Composable
fun EmbeddedPlayerView(
    context: Context,
    exoPlayer: ExoPlayer,
    isMuted: Boolean,
    onFullscreenToggle: () -> Unit
) {
    AndroidView(
        modifier = Modifier
            .padding(16.dp)
            .shadow(10.dp, RoundedCornerShape(16.dp), clip = true)
            .height(400.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.Black),
        factory = {
            playerViewWithControls(context, exoPlayer, isMuted, onFullscreenToggle)
        }
    )
}

@Composable
fun FullscreenMediaPlayerDialog(
    onDismiss: () -> Unit,
    context: Context,
    exoPlayer: ExoPlayer,
    isMuted: Boolean,
    onFullscreenToggle: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = {
                    playerViewWithControls(context, exoPlayer, isMuted, onFullscreenToggle)
                }
            )
        }
    }
}

@OptIn(UnstableApi::class)
private fun playerViewWithControls(
    context: Context,
    exoPlayer: ExoPlayer,
    isMuted: Boolean,
    onFullscreenToggle: () -> Unit
): PlayerView {
    return PlayerView(context).apply {
        player = exoPlayer
        useController = true
        resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT

        addFullscreenButton(context, onFullscreenToggle)
        addVolumeToggleButton(context, isMuted, exoPlayer)
    }
}

private fun PlayerView.addFullscreenButton(context: Context, onFullscreenToggle: () -> Unit) {
    val fullscreenButton = ImageButton(context).apply {
        setImageResource(android.R.drawable.ic_menu_view)
        setOnClickListener { onFullscreenToggle() }
    }
    addView(fullscreenButton, createLayoutParams(Gravity.END or Gravity.TOP))
}

private fun PlayerView.addVolumeToggleButton(context: Context, isMuted: Boolean, exoPlayer: ExoPlayer) {
    val volumeButton = ImageButton(context).apply {
        setImageResource(if (isMuted) android.R.drawable.ic_lock_silent_mode else android.R.drawable.ic_lock_silent_mode_off)
        setOnClickListener {
            exoPlayer.volume = if (exoPlayer.volume == 0f) 1f else 0f
            setImageResource(if (exoPlayer.volume == 0f) android.R.drawable.ic_lock_silent_mode else android.R.drawable.ic_lock_silent_mode_off)
        }
    }
    addView(volumeButton, createLayoutParams(Gravity.END or Gravity.BOTTOM))
}

private fun createLayoutParams(gravity: Int): FrameLayout.LayoutParams {
    return FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT).apply {
        this.gravity = gravity
    }
}