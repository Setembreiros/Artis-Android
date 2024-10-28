package com.setembreiros.artis.ui.commponents

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import coil.compose.AsyncImage
import com.setembreiros.artis.R
import com.setembreiros.artis.common.Constants
import com.setembreiros.artis.domain.builder.ThumbnailBuilder
import com.setembreiros.artis.domain.model.post.Post
import java.io.File

@Composable
fun PostThumbnail(context: Context, post: Post, onNavigateToImageDetails: () -> Unit) {
    ensureThumbnailContent(context, post)

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when (post.metadata.type) {
            Constants.ContentType.TEXT -> TextPostThumbnail(post, onNavigateToImageDetails)
            Constants.ContentType.IMAGE -> ImagePostThumbnail(post, onNavigateToImageDetails)
            Constants.ContentType.VIDEO -> VideoPostThumbnail(post, onNavigateToImageDetails)
            Constants.ContentType.AUDIO -> AudioPostThumbnail(post, onNavigateToImageDetails)
        }
    }
}

private fun ensureThumbnailContent(context: Context, post: Post) {
    if (post.thumbnail == null) {
        val tempFile = File.createTempFile("temp_file", "").apply {
            post.content?.let { writeBytes(it) }
        }
        post.thumbnail = ThumbnailBuilder.createThumbnail(context, tempFile.toUri(), post.metadata.type)
        tempFile.delete()
    }
}

@Composable
private fun TextPostThumbnail(post: Post, onClick: () -> Unit) {
    ThumbnailContainer(onClick = onClick) {
        BasePostThumbnail(post)
    }
}

@Composable
private fun ImagePostThumbnail(post: Post, onClick: () -> Unit) {
    ThumbnailContainer(onClick = onClick) {
        BasePostThumbnail(post)
    }
}

@Composable
private fun VideoPostThumbnail(post: Post, onClick: () -> Unit) {
    ThumbnailContainer(onClick = onClick) {
        BasePostThumbnail(post)
        PlayButtonOverlay()
    }
}

@Composable
private fun AudioPostThumbnail(post: Post, onClick: () -> Unit) {
    ThumbnailContainer(onClick = onClick) {
        if (post.thumbnail != null && post.thumbnail!!.isNotEmpty()) {
            BasePostThumbnail(post)
        } else {
            DefaultAudioThumbnail(onClick)
        }
    }
}

@Composable
private fun ThumbnailContainer(onClick: () -> Unit, content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .shadow(10.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

@Composable
private fun BasePostThumbnail(post: Post) {
    AsyncImage(
        model = post.thumbnail,
        modifier = Modifier
            .height(150.dp)
            .width(100.dp)
            .border(2.dp, Color.Black, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp)),
        contentScale = ContentScale.Crop,
        contentDescription = "Thumbnail post"
    )
}

@Composable
private fun PlayButtonOverlay() {
    Image(
        painter = painterResource(id = R.drawable.play_button),
        contentDescription = "Play Button",
        modifier = Modifier.size(50.dp),
        colorFilter = ColorFilter.tint(Color.White)
    )
}

@Composable
private fun DefaultAudioThumbnail(onClick: () -> Unit) {
    Image(
        painter = painterResource(id = R.drawable.audio_default_thumbnail),
        contentDescription = "Audio default thumbnail",
        modifier = Modifier
            .height(150.dp)
            .width(100.dp)
            .border(2.dp, Color.Black, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        contentScale = ContentScale.Crop
    )
}

@Composable
fun BaseImagePost(model: Any?) {
    var isFullScreen by rememberSaveable { mutableStateOf(false) }

    ImagePost(
        model = model,
        onClick = { isFullScreen = true }
    )

    if (isFullScreen) {
        FullScreenImageDialog(
            model = model,
            onDismiss = { isFullScreen = false }
        )
    }
}

@Composable
private fun ImagePost(model: Any?, onClick: () -> Unit) {
    AsyncImage(
        model = model,
        contentDescription = "Image post",
        modifier = Modifier
            .padding(16.dp)
            .shadow(10.dp, RoundedCornerShape(16.dp), clip = true)
            .height(400.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        contentScale = ContentScale.Crop
    )
}

@Composable
private fun FullScreenImageDialog(model: Any?, onDismiss: () -> Unit) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .width(800.dp)
                .background(Color.Black)
                .clickable { onDismiss() }
        ) {
            AsyncImage(
                model = model,
                contentDescription = "Full screen image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        }
    }
}

@Composable
fun MediaPlayer(uri: Uri?) {
    if (uri == null) return
    val context = LocalContext.current

    var isFullScreen by remember { mutableStateOf(false) }
    val isMuted by remember { mutableStateOf(true) }

    val exoPlayer = rememberExoPlayer(context, uri, isMuted)

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    if (isFullScreen) {
        FullscreenMediaPlayerDialog(
            onDismiss = { isFullScreen = false },
            context = context,
            exoPlayer = exoPlayer,
            isMuted = isMuted,
            onFullscreenToggle = { isFullScreen = false }
        )
    } else {
        EmbeddedPlayerView(
            context = context,
            exoPlayer = exoPlayer,
            isMuted = isMuted,
            onFullscreenToggle = { isFullScreen = true }
        )
    }
}

@Composable
private fun rememberExoPlayer(context: Context, uri: Uri, isMuted: Boolean) = remember {
    ExoPlayer.Builder(context).build().apply {
        setMediaItem(MediaItem.fromUri(uri))
        prepare()
        playWhenReady = true
        volume = if (isMuted) 0f else 1f
    }
}

@Composable
private fun FullscreenMediaPlayerDialog(
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

@Composable
private fun EmbeddedPlayerView(
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

@Composable
fun PdfReader(uri: Uri?) {
    if (uri == null) return
    val context = LocalContext.current

    val pdfRenderer = rememberPdfRenderer(context, uri) ?: return

    Box(
        modifier = Modifier
            .padding(16.dp)
            .shadow(10.dp, RoundedCornerShape(16.dp), clip = true)
            .height(400.dp)
            .background(Color.White)
            .clip(RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center
    ) {
        PdfPageList(pdfRenderer)
        OpenPdfButton(uri = uri, context = context, modifier = Modifier.align(Alignment.BottomCenter))
    }
}

@Composable
private fun rememberPdfRenderer(context: Context, uri: Uri): PdfRenderer? {
    val fileDescriptor = remember {
        context.contentResolver.openFileDescriptor(uri, "r")
    }
    val pdfRenderer = remember(fileDescriptor) { fileDescriptor?.let { PdfRenderer(it) } }

    DisposableEffect(Unit) {
        onDispose {
            pdfRenderer?.close()
            fileDescriptor?.close()
        }
    }

    return pdfRenderer
}

@Composable
private fun PdfPageList(pdfRenderer: PdfRenderer) {
    LazyRow(modifier = Modifier.padding(vertical = 16.dp)) {
        items(count = pdfRenderer.pageCount) { index ->
            PdfPage(pdfRenderer, index)
        }
    }
}

@Composable
private fun PdfPage(pdfRenderer: PdfRenderer, pageIndex: Int) {
    val bitmap = remember(pageIndex) {
        val page = pdfRenderer.openPage(pageIndex)
        Bitmap.createBitmap(page.width * 2, page.height * 2, Bitmap.Config.ARGB_8888).apply {
            page.render(this, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
        }.also {
            page.close()
        }
    }

    Image(
        bitmap = bitmap.asImageBitmap(),
        contentDescription = "PDF page number: $pageIndex",
        modifier = Modifier
            .padding(start = 10.dp)
            .shadow(10.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
    )
}

@Composable
private fun OpenPdfButton(uri: Uri, context: Context, modifier: Modifier = Modifier) {
    Button(onClick = { openPdfExternally(context, uri) }, modifier = modifier) {
        Text(text = stringResource(id = R.string.open_pdf))
    }
}

private fun openPdfExternally(context: Context, uri: Uri) {
    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(uri, "application/pdf")
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    try {
        context.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(context, R.string.no_external_pdf_app, Toast.LENGTH_SHORT).show()
    }
}
