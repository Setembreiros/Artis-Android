package com.setembreiros.artis.ui.post

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.setembreiros.artis.R
import com.setembreiros.artis.common.Constants
import com.setembreiros.artis.domain.model.post.Post
import com.setembreiros.artis.domain.model.post.PostMetadata
import com.setembreiros.artis.ui.commponents.BaseImagePost
import com.setembreiros.artis.ui.commponents.MediaPlayer
import com.setembreiros.artis.ui.commponents.PdfReader
import com.setembreiros.artis.ui.theme.ArtisTheme
import java.io.ByteArrayOutputStream
import java.io.InputStream

@Composable
fun PostDetailsScreen(postId: String) {
    val viewModel: PostDetailsViewModel = hiltViewModel()
    val posts = viewModel.getPosts()
    val listState = rememberLazyListState()

    val postIndex = posts.indexOfFirst { it.metadata.postId == postId }

    LaunchedEffect(postIndex) {
        if (postIndex >= 0) {
            listState.scrollToItem(postIndex)
        }
    }

    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        items(posts) { post ->
            PostDetailsView(post)
        }
    }
}

@Composable
fun PostDetailsView(post: Post) {
    Text(
        text = post.metadata.title,
        fontSize = 42.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top =16.dp),
        textAlign = TextAlign.Center
    )
    Spacer(modifier = Modifier.height(10.dp))
    when (post.metadata.type) {
        Constants.ContentType.IMAGE -> BaseImagePost(post)
        Constants.ContentType.TEXT -> PdfReader(post)
        Constants.ContentType.AUDIO -> MediaPlayer(post)
        Constants.ContentType.VIDEO -> MediaPlayer(post)
    }
    Spacer(modifier = Modifier.height(10.dp))
    Text(
        text = post.metadata.description,
        fontSize = 18.sp,
        color = Color.Gray,
        textAlign = TextAlign.Center
    )
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ImagePostDetailsPreview() {
    val imageResource = LocalContext.current.resources.openRawResource(R.raw.imaxe_de_proba)
    val content = imageResource.readBytes()

    val samplePost = Post(
        metadata = PostMetadata(
            "","",Constants.ContentType.IMAGE,
            title = "Sample Title",
            description = "This is a sample description for the post.", "", ""
        ),
        content = content,
        thumbnail = null
    )

    ArtisTheme {
        PostDetailsView(post = samplePost)
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun Image2PostDetailsPreview() {
    val imageResource = LocalContext.current.resources.openRawResource(R.raw.image_test_2)
    val content = imageResource.readBytes()

    val samplePost = Post(
        metadata = PostMetadata(
            "","",Constants.ContentType.IMAGE,
            title = "Sample Title",
            description = "This is a sample description for the post.", "", ""
        ),
        content = content,
        thumbnail = null
    )

    ArtisTheme {
        PostDetailsView(post = samplePost)
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun Video1PostDetailsPreview() {
    val videoResource = LocalContext.current.resources.openRawResource(R.raw.video_test_1)
    val content = videoResource.readBytes()

    val samplePost = Post(
        metadata = PostMetadata(
            "","",Constants.ContentType.VIDEO,
            title = "Sample Title",
            description = "This is a sample description for the post.", "", ""
        ),
        content = content,
        thumbnail = null
    )

    ArtisTheme {
        PostDetailsView(samplePost)
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun Video2PostDetailsPreview() {
    val videoResource = LocalContext.current.resources.openRawResource(R.raw.video_test_2)
    val content = videoResource.readBytes()

    val samplePost = Post(
        metadata = PostMetadata(
            "","",Constants.ContentType.VIDEO,
            title = "Sample Title",
            description = "This is a sample description for the post.", "", ""
        ),
        content = content,
        thumbnail = null
    )

    ArtisTheme {
        PostDetailsView(samplePost)
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PdfPostDetailsPreview() {
    val pdfResourceDescriptor = LocalContext.current.resources.openRawResourceFd(R.raw.pdf_test)
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
            "","",Constants.ContentType.TEXT,
            title = "Sample Title",
            description = "This is a sample description for the post.", "", ""
        ),
        content = content,
        thumbnail = null
    )

    ArtisTheme {
        PostDetailsView(samplePost)
    }
}