package com.setembreiros.artis.ui.post.item

import android.content.Context
import android.content.res.Configuration
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.imeNestedScroll
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import com.setembreiros.artis.domain.model.Comment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun PostDetailsView(context: Context, post: Post, onChange: () -> Unit) {
    val viewModel: PostDetailsViewModel = hiltViewModel()
    val commentsByPost by viewModel.commentsByPost.collectAsState()
    var showComments by remember { mutableStateOf(false) }
    val errorCode by viewModel.errorCode.collectAsState()

    // Mostrar Toast cando haxa un erro
    LaunchedEffect(errorCode) {
        errorCode?.let { code ->
            withContext(Dispatchers.Main) {
                Toast.makeText(context, context.getString(code), Toast.LENGTH_SHORT).show()
            }
            viewModel.clearErrorMessage()
        }
    }

    var showDeleteDialog by remember { mutableStateOf(false) }
    if (showDeleteDialog) {
        DeleteAlertDialog(
            onConfirm = {
                viewModel.deletePost(
                    post.metadata.postId,
                    onSuccess = {
                        onChange()
                        showDeleteDialog = false
                    }
                )
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
                .clickable { showComments = true }
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

    if (showComments) {
        viewModel.loadCommentsForPost(post.metadata.postId)

        CommentsSection(
            commentsByPost[post.metadata.postId] ?: emptyList(),
            onSend = {
                viewModel.addCommentAndUpdate(post.metadata.postId, it)
            },
            onDismiss = { showComments = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CommentsSection(
    comments: List<Comment>,
    onSend: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    var newComment by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    // Desprazar ao final cando se engade un novo comentario
    LaunchedEffect(comments.size) {
        if (comments.isNotEmpty()) {
            listState.animateScrollToItem(comments.size)
        }
    }

    ModalBottomSheet(
        onDismissRequest = {
            keyboardController?.hide()
            onDismiss()
        },
        sheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = true
        ),
        windowInsets = WindowInsets(0, 0, 0, 0),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .width(40.dp)
                    .height(4.dp)
                    .background(
                        color = Color.Gray.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(2.dp)
                    )
            )
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.95f)
                .imeNestedScroll() // Importante para o comportamento correcto
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 80.dp) // Espazo para o campo fixo
            ) {
                // Cabeceira
                Row(
                    modifier = Modifier
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(id = R.string.comment_section),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                HorizontalDivider(Modifier.padding(horizontal = 16.dp))

                // Lista de comentarios
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    items(comments) { comment ->
                        CommentItem(comment = comment)
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp))
                    }
                }
            }

            // Campo de comentario
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp, bottomStart = 8.dp, bottomEnd = 8.dp))
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 8.dp)
                    .navigationBarsPadding()
                    .imePadding() // Só este elemento reacciona ao teclado
            ) {
                Surface(
                    tonalElevation = 8.dp,
                    shadowElevation = 8.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = newComment,
                            onValueChange = { newComment = it },
                            modifier = Modifier.weight(1f),
                            placeholder = { Text(stringResource(id = R.string.add_comment), color = Color.LightGray) }
                        )
                        IconButton(
                            onClick = {
                                if (newComment.isNotBlank()) {
                                    onSend(newComment)
                                    newComment = ""
                                    keyboardController?.hide()
                                }
                            },
                            enabled = newComment.isNotBlank()
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Send",
                                tint = if (newComment.isNotBlank()) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CommentItem(comment: Comment) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Spacer(modifier = Modifier.width(8.dp))

        Column(modifier = Modifier.weight(1f)) {
            // Nome de usuario
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = comment.username,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    fontSize = 16.sp
                )
            }

            // Contido do comentario
            Text(
                text = comment.content,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
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
        PostDetailsView(context, post = samplePost, {})
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
        PostDetailsView(context, post = samplePost, {})
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
        PostDetailsView(context, samplePost, {})
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
        PostDetailsView(context, samplePost, {})
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
        PostDetailsView(context, samplePost, {})
    }
}