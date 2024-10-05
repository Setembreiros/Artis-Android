package com.setembreiros.artis.ui.commponents

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
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
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import coil.compose.AsyncImage
import com.setembreiros.artis.R
import com.setembreiros.artis.common.Constants
import com.setembreiros.artis.domain.model.post.Post
import java.io.File
import java.io.FileOutputStream

@Composable
fun PostThumbnail(post: Post, onNavigateToImageDetails: () -> Unit,){
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when (post.metadata.type) {
            Constants.ContentType.IMAGE -> BasePostThumbnail(
                post,
                onImageClick = { onNavigateToImageDetails() }
            )
            Constants.ContentType.VIDEO -> {
                BasePostThumbnail(
                    post,
                    onImageClick = { onNavigateToImageDetails() }
                )
                Image(
                    painter = painterResource(id = R.drawable.play_button),
                    contentDescription = "Play Button",
                    modifier = Modifier.size(50.dp),
                    colorFilter = ColorFilter.tint(Color.White)
                )
            }
            Constants.ContentType.AUDIO -> {
                if(post.thumbnail != null)
                    BasePostThumbnail(
                        post,
                        onImageClick = { onNavigateToImageDetails() }
                    )
                else {
                    Image(
                        painter = painterResource(id = R.drawable.audio_default_thumbnail),
                        contentDescription = "Audio default thumbnail",
                        modifier = Modifier
                            .shadow(10.dp, RoundedCornerShape(16.dp), clip = true)
                            .height(150.dp)
                            .width(100.dp)
                            .border(
                                2.dp,
                                Color.Black,
                                RoundedCornerShape(16.dp)
                            )
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { onNavigateToImageDetails() },
                        contentScale = ContentScale.Crop,
                    )
                }
            }
            Constants.ContentType.TEXT -> BasePostThumbnail(
                post,
                onImageClick = { onNavigateToImageDetails() }
            )
        }
    }
}

@Composable
fun BasePostThumbnail(post: Post, onImageClick: () -> Unit){
    AsyncImage(
        model = post.thumbnail,
        modifier = Modifier
            .shadow(10.dp, RoundedCornerShape(16.dp), clip = true)
            .height(150.dp)
            .width(100.dp)
            .border(
                2.dp,
                Color.Black,
                RoundedCornerShape(16.dp)
            )
            .clip(RoundedCornerShape(16.dp))
            .clickable { onImageClick() },
        contentScale = ContentScale.Crop,
        contentDescription = null,
        )
}

@Composable
fun BaseImagePost(post: Post){
    var isFullScreen by rememberSaveable { mutableStateOf(false) }

    AsyncImage(
        model = post.content,
        contentDescription = "Image",
        modifier = Modifier
            .padding(16.dp)
            .shadow(10.dp, RoundedCornerShape(16.dp), clip = true)
            .height(400.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable {
                isFullScreen = true
            },
        contentScale = ContentScale.Crop,
    )

    if (isFullScreen) {
        Dialog(onDismissRequest = { isFullScreen = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)) {
            Box(
                modifier = Modifier
                    .width(800.dp)
                    .background(Color.Black)
                    .clickable { isFullScreen = false }
            ) {
                AsyncImage(
                    model = post.content,
                    contentDescription = "Full screen image",
                    modifier = Modifier
                        .fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }
        }
    }
}

@OptIn(UnstableApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MediaPlayer(post: Post) {
    val context = LocalContext.current
    val videoFile = remember {
        writeByteArrayToFile(context, post.content!!, "temp_media." + post.metadata.fileType)
    }

    var isFullScreen by remember { mutableStateOf(false) }

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            val mediaItem = MediaItem.Builder()
                .setUri(videoFile.toUri())
                .build()
            setMediaItem(mediaItem)
            prepare()
            playWhenReady = true
            volume = 0f
        }
    }

    if (isFullScreen) {
        Dialog(onDismissRequest = { isFullScreen = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            ) {
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = {
                        PlayerView(context).apply {
                            player = exoPlayer
                            useController = true
                            resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL

                            val fullscreenButton = ImageButton(context).apply {
                                setImageResource(android.R.drawable.ic_menu_close_clear_cancel)
                                setOnClickListener {
                                    isFullScreen = false
                                }
                            }

                            this.addView(fullscreenButton)
                            val params = FrameLayout.LayoutParams(
                                FrameLayout.LayoutParams.WRAP_CONTENT,
                                FrameLayout.LayoutParams.WRAP_CONTENT
                            ).apply {
                                gravity = Gravity.END or Gravity.TOP
                            }
                            fullscreenButton.layoutParams = params
                        }
                    }
                )
            }
        }
    } else {
        AndroidView(
            modifier = Modifier
                .padding(16.dp)
                .shadow(10.dp, RoundedCornerShape(16.dp), clip = true)
                .height(400.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.Black),
            factory = {
                PlayerView(context).apply {
                    player = exoPlayer
                    useController = true
                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT

                    val fullscreenButton = ImageButton(context).apply {
                        setImageResource(android.R.drawable.ic_menu_view)
                        setOnClickListener {
                            isFullScreen = true
                        }
                    }

                    this.addView(fullscreenButton)
                    val params = FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        gravity = Gravity.END or Gravity.TOP
                    }
                    fullscreenButton.layoutParams = params
                }
            }
        )
    }
}
@Composable
fun PdfReader(post: Post) {
    val context = LocalContext.current

    val fileDescriptor: ParcelFileDescriptor?
    val pdfRenderer: PdfRenderer?

    val tempFile = remember {
        val file = File.createTempFile("temp_pdf", post.metadata.fileType, context.cacheDir)
        val fos = FileOutputStream(file)
        fos.write(post.content)
        fos.close()
        file
    }

    fileDescriptor = ParcelFileDescriptor.open(tempFile, ParcelFileDescriptor.MODE_READ_ONLY)
    pdfRenderer = PdfRenderer(fileDescriptor)

    fun openPdfExternally() {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",  // Nome do provider
            tempFile
        )

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) // Conceder permiso de lectura
        }

        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, "Non hai aplicacións para ler PDFs", Toast.LENGTH_SHORT).show()
        }
    }

    Box(
        modifier = Modifier
            .padding(16.dp)
            .shadow(10.dp, RoundedCornerShape(16.dp), clip = true)
            .height(400.dp)
            .background(Color.White)
            .clip(RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center
    ) {
        LazyRow(modifier = Modifier.padding(vertical = 16.dp)) {
            items(count = pdfRenderer.pageCount) { index ->
                val page = pdfRenderer.openPage(index)
                val bitmap = Bitmap.createBitmap(page.width * 2, page.height * 2, Bitmap.Config.ARGB_8888)
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "PDF page number: $index",
                    modifier = Modifier
                        .padding(start = 10.dp)
                        .shadow(10.dp, RoundedCornerShape(16.dp))
                        .clip(RoundedCornerShape(16.dp))
                )

                page.close()
            }
        }

        Button(
            onClick = { openPdfExternally() },
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Text(text = stringResource(id = R.string.open_pdf),)
        }
    }
}




fun writeByteArrayToFile(context: Context, byteArray: ByteArray, fileName: String): File {
    val file = File(context.cacheDir, fileName)
    FileOutputStream(file).use { fos ->
        fos.write(byteArray)
    }
    return file
}
