package com.setembreiros.artis.ui.commponents

import android.content.Context
import android.net.Uri
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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.net.toUri
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

@Composable
fun TextPost(uri: Uri?) {
    if (uri == null) return
    val context = LocalContext.current

    val pdfRenderer = RememberPdfRenderer(context, uri) ?: return

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
fun ImagePost(model: Any?) {
    var isFullScreen by rememberSaveable { mutableStateOf(false) }

    CroppedImagePost(
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
fun AVPost(uri: Uri?) {
    if (uri == null) return
    val context = LocalContext.current

    var isFullScreen by remember { mutableStateOf(false) }
    val isMuted by remember { mutableStateOf(true) }

    val exoPlayer = RememberExoPlayer(context, uri, isMuted)

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
        PlayImageOverlay()
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
private fun PlayImageOverlay() {
    Image(
        painter = painterResource(id = R.drawable.play_button),
        contentDescription = "Play Button",
        modifier = Modifier.size(50.dp),
        colorFilter = ColorFilter.tint(Color.White)
    )
}

@Composable
private fun CroppedImagePost(model: Any?, onClick: () -> Unit) {
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

private fun ensureThumbnailContent(context: Context, post: Post) {
    if (post.thumbnail == null) {
        val tempFile = File.createTempFile("temp_file", "").apply {
            post.content?.let { writeBytes(it) }
        }
        post.thumbnail = ThumbnailBuilder.createThumbnail(context, tempFile.toUri(), post.metadata.type)
        tempFile.delete()
    }
}
