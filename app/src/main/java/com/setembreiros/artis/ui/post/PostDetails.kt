package com.setembreiros.artis.ui.post

import android.content.Context
import android.content.res.Configuration
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.setembreiros.artis.R
import com.setembreiros.artis.common.Constants
import com.setembreiros.artis.domain.model.post.Post
import com.setembreiros.artis.domain.model.post.PostContent
import com.setembreiros.artis.domain.model.post.PostMetadata
import com.setembreiros.artis.ui.commponents.AVPost
import com.setembreiros.artis.ui.commponents.DeleteAlertDialog
import com.setembreiros.artis.ui.commponents.ImagePost
import com.setembreiros.artis.ui.commponents.MenuOption
import com.setembreiros.artis.ui.commponents.TextPost
import com.setembreiros.artis.ui.commponents.ThreeDotsMenuButton
import com.setembreiros.artis.ui.theme.ArtisTheme
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import androidx.compose.foundation.clickable
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment

@Composable
fun PostDetailsView(context: Context, post: Post) {
    val viewModel: PostDetailsViewModel = hiltViewModel()

    var showDeleteDialog by remember { mutableStateOf(false) }
    if (showDeleteDialog) {
        DeleteAlertDialog(
            onConfirm = {
                viewModel.deletePost(post.metadata.postId)
            },
            onDismiss = { showDeleteDialog = false }
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = "https://img.freepik.com/premium-vector/business-office-african-american-manager-usinessman-avatar-icon-head-portrait-occupation_805465-135.jpg",
                placeholder = painterResource(id = R.drawable.male_avatar_placeholder),
                contentDescription = stringResource(R.string.avatar_description),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = post.metadata.username,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
        val profileOptions = listOf(
            MenuOption(
                text = "Delete",
                color = Color.Red,
                icon = Icons.Default.Delete,
                onClick = { showDeleteDialog = true }
            ),
        )
        ThreeDotsMenuButton(profileOptions)
    }
    Text(
        text = post.metadata.title,
        fontSize = 42.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top =16.dp),
        textAlign = TextAlign.Center
    )
    Spacer(modifier = Modifier.height(10.dp))
    if(post.content?.content != null && post.content?.content!!.isNotEmpty()) {
        post.content!!.uriContent = createUriTempFile(context, post.metadata.postId, post.content?.content)
        post.content!!.content = null
    }
    when (post.metadata.type) {
        Constants.ContentType.TEXT -> TextPost(post.content!!.uriContent)
        Constants.ContentType.IMAGE -> ImagePost(post.content!!.uriContent)
        Constants.ContentType.AUDIO -> AVPost(post.content!!.uriContent)
        Constants.ContentType.VIDEO -> AVPost(post.content!!.uriContent)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clickable { /* TODO: Add logic */ }
                .padding(end = 16.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Comment,
                contentDescription = "Comments",
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "${post.metadata.comments}",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }
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
private fun createUriTempFile(context: Context, postId: String, content: ByteArray?): Uri? {
    content?.let {
        val tempFile = createTempFile(context, postId, content)

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
private fun createTempFile(context: Context, postId: String, content: ByteArray?): File? {
    content?.let {
        val tempFile = remember {
            val file = File.createTempFile("temp_$postId", "", context.cacheDir)
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
            description = "This is a sample description for the post.", 5, 0, "", ""
        ),
        content = PostContent(
            uriContent = null,
            content = content,
            thumbnail = null
        )
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
            description = "This is a sample description for the post.", 0, 0, "", ""
        ),
        content = PostContent(
            uriContent = null,
            content = content,
            thumbnail = null
        )
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
            description = "This is a sample description for the post.", 0, 0, "", ""
        ),
        content = PostContent(
            uriContent = null,
            content = content,
            thumbnail = null
        )
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
            description = "This is a sample description for the post.", 0, 0, "", ""
        ),
        content = PostContent(
            uriContent = null,
            content = content,
            thumbnail = null
        )
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
            description = "This is a sample description for the post.", 0, 0, "", ""
        ),
        content = PostContent(
            uriContent = null,
            content = content,
            thumbnail = null
        )
    )

    ArtisTheme {
        PostDetailsView(context, samplePost)
    }
}