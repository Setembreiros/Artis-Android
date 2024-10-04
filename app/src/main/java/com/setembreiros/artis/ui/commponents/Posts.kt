package com.setembreiros.artis.ui.commponents

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
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
        modifier = Modifier.fillMaxSize(),  // Ensures the box takes up full available space
        contentAlignment = Alignment.Center // Aligns the play button in the center of the Box
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
                    painter = painterResource(id = R.drawable.play_button),  // Play button icon
                    contentDescription = "Play Button",
                    modifier = Modifier.size(50.dp),  // Set the size of the play button
                    colorFilter = ColorFilter.tint(Color.White) // Optional: tint the play button if needed
                )
            }
            Constants.ContentType.AUDIO -> {
                if(post.thumbnail!!.isNotEmpty())
                    BasePostThumbnail(
                        post,
                        onImageClick = { onNavigateToImageDetails() }
                    )
                else {
                    Image(
                        painter = painterResource(id = R.drawable.audio_default_thumbnail),  // Play button icon
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
    AsyncImage(
        model = post.content,
        contentDescription = "Image",
        modifier = Modifier
            .padding(16.dp)
            .shadow(10.dp, RoundedCornerShape(16.dp), clip = true)
            .height(400.dp)
            .clip(RoundedCornerShape(16.dp)),
        contentScale = ContentScale.Crop,
    )
}

@OptIn(UnstableApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MediaPlayer(post: Post) {
    val context = LocalContext.current
    val videoFile = remember {
        writeByteArrayToFile(context, post.content!!, "temp_media." + post.metadata.fileType)
    }
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
            }
        },
    )
}

@Composable
fun PdfReader(post: Post){
    val fileDescriptor: ParcelFileDescriptor?
    val pdfRenderer: PdfRenderer?
    val tempFile: File? = File.createTempFile("temp_pdf", post.metadata.fileType)
    val fos = FileOutputStream(tempFile)
    fos.write(post.content)
    fos.close()

    fileDescriptor = ParcelFileDescriptor.open(tempFile, ParcelFileDescriptor.MODE_READ_ONLY)
    pdfRenderer = PdfRenderer(fileDescriptor)

    Box(modifier = Modifier
        .padding(16.dp)
        .shadow(10.dp, RoundedCornerShape(16.dp), clip = true)
        .height(400.dp)
        .background(Color.White)
        .clip(RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center) {
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
    }
}


fun writeByteArrayToFile(context: Context, byteArray: ByteArray, fileName: String): File {
    val file = File(context.cacheDir, fileName)
    FileOutputStream(file).use { fos ->
        fos.write(byteArray)
    }
    return file
}
