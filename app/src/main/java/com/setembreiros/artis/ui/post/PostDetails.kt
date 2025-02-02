package com.setembreiros.artis.ui.post

import android.content.Context
import android.content.res.Configuration
import android.net.Uri
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.setembreiros.artis.R
import com.setembreiros.artis.common.Constants
import com.setembreiros.artis.domain.model.post.Post
import com.setembreiros.artis.domain.model.post.PostMetadata
import com.setembreiros.artis.ui.commponents.AVPost
import com.setembreiros.artis.ui.commponents.ImagePost
import com.setembreiros.artis.ui.commponents.TextPost
import com.setembreiros.artis.ui.theme.ArtisTheme
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

@Composable
fun PostDetailsView(context: Context, post: Post) {
    Text(
        text = post.metadata.title,
        fontSize = 42.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top =16.dp),
        textAlign = TextAlign.Center
    )
    Spacer(modifier = Modifier.height(10.dp))
    when (post.metadata.type) {
        Constants.ContentType.TEXT -> TextPost(createUriTempFile(context, post.content))
        Constants.ContentType.IMAGE -> ImagePost(post.content)
        Constants.ContentType.AUDIO -> AVPost(createUriTempFile(context, post.content))
        Constants.ContentType.VIDEO -> AVPost(createUriTempFile(context, post.content))
    }
    Spacer(modifier = Modifier.height(10.dp))
    Text(
        text = post.metadata.description,
        fontSize = 18.sp,
        color = Color.Gray,
        textAlign = TextAlign.Center
    )
}

@Composable
private fun createUriTempFile(context: Context, content: ByteArray?): Uri? {
    content?.let {
        val tempFile = createTempFile(context, content)

        tempFile?.let {
            return remember {
                FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.provider",
                    tempFile
                )
            }
        }

        return null
    }

    return null
}

@Composable
private fun createTempFile(context: Context, content: ByteArray?): File? {
    content?.let {
        val tempFile = remember {
            val file = File.createTempFile("temp_pdf", "", context.cacheDir)
            val fos = FileOutputStream(file)
            fos.write(content)
            fos.close()
            file
        }

        return tempFile
    }

    return null
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ImagePostDetailsPreview() {
    val context = LocalContext.current
    val imageResource = context.resources.openRawResource(R.raw.imaxe_de_proba)
    val content = imageResource.readBytes()

    val samplePost = Post(
        metadata = PostMetadata(
            "","", Constants.ContentType.IMAGE,
            title = "Sample Title",
            description = "This is a sample description for the post.", 5, "", ""
        ),
        uriContent = null,
        content = content,
        thumbnail = null
    )

    ArtisTheme {
        PostDetailsView(context, post = samplePost)
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun Image2PostDetailsPreview() {
    val context = LocalContext.current
    val imageResource = context.resources.openRawResource(R.raw.image_test_2)
    val content = imageResource.readBytes()

    val samplePost = Post(
        metadata = PostMetadata(
            "","", Constants.ContentType.IMAGE,
            title = "Sample Title",
            description = "This is a sample description for the post.", 0, "", ""
        ),
        uriContent = null,
        content = content,
        thumbnail = null
    )

    ArtisTheme {
        PostDetailsView(context, post = samplePost)
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun Video1PostDetailsPreview() {
    val context = LocalContext.current
    val videoResource = context.resources.openRawResource(R.raw.video_test_1)
    val content = videoResource.readBytes()

    val samplePost = Post(
        metadata = PostMetadata(
            "","", Constants.ContentType.VIDEO,
            title = "Sample Title",
            description = "This is a sample description for the post.", 0, "", ""
        ),
        uriContent = null,
        content = content,
        thumbnail = null
    )

    ArtisTheme {
        PostDetailsView(context, samplePost)
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun Video2PostDetailsPreview() {
    val context = LocalContext.current
    val videoResource = context.resources.openRawResource(R.raw.video_test_2)
    val content = videoResource.readBytes()

    val samplePost = Post(
        metadata = PostMetadata(
            "","", Constants.ContentType.VIDEO,
            title = "Sample Title",
            description = "This is a sample description for the post.", 0, "", ""
        ),
        uriContent = null,
        content = content,
        thumbnail = null
    )

    ArtisTheme {
        PostDetailsView(context, samplePost)
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PdfPostDetailsPreview() {
    val context = LocalContext.current
    val pdfResourceDescriptor = context.resources.openRawResourceFd(R.raw.pdf_test)
    val inputStream: InputStream = pdfResourceDescriptor.createInputStream()
    val buffer = ByteArrayOutputStream()
    val data = ByteArray(1024)  // Buffer to read data in chunks
    var nRead: Int

    while (inputStream.read(data, 0, data.size).also { nRead = it } != -1) {
        buffer.write(data, 0, nRead)
    }

    buffer.flush()

    val content = buffer.toByteArray()

    val samplePost = Post(
        metadata = PostMetadata(
            "","", Constants.ContentType.TEXT,
            title = "Sample Title",
            description = "This is a sample description for the post.", 0, "", ""
        ),
        uriContent = null,
        content = content,
        thumbnail = null
    )

    ArtisTheme {
        PostDetailsView(context, samplePost)
    }
}