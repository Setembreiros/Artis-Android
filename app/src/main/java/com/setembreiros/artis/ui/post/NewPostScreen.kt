package com.setembreiros.artis.ui.post

import android.content.Context
import android.content.res.Configuration
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.setembreiros.artis.R
import com.setembreiros.artis.common.Constants
import com.setembreiros.artis.domain.builder.ThumbnailBuilder
import com.setembreiros.artis.ui.commponents.StandardButton
import com.setembreiros.artis.ui.commponents.TextFieldPost
import com.setembreiros.artis.ui.theme.ArtisTheme
import com.setembreiros.artis.ui.theme.gray
import java.io.ByteArrayOutputStream
import java.io.InputStream

@Composable
fun NewPostScreen() {
    val context = LocalContext.current
    val viewModel: NewPostViewModel = hiltViewModel()
    val loading by viewModel.loading.collectAsStateWithLifecycle()


    DisposableEffect(context) {

        onDispose {

        }
    }

    Content(
        loading = loading,
        onTitle = {
            viewModel.setTitle(it)
        },
        onDescription = {
            viewModel.setDescription(it)
        },
        onResource = {
            viewModel.setResource(it)
        },
        onThumbnailResource = {
            viewModel.setThumbnailResource(it)
        },
        onType = {
            viewModel.setType(it)
        },
        onPublish = {viewModel.publish()}
        )

}

@Composable
fun Content(
    loading: Boolean,
    onTitle: (String) -> Unit,
    onDescription: (String) -> Unit,
    onResource: (ByteArray) -> Unit,
    onThumbnailResource: (ByteArray) -> Unit,
    onType: (Constants.ContentType) -> Unit,
    onPublish: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
            .padding(horizontal = 32.dp, vertical = 32.dp),


        ) {
        TextFieldPost(
            hint = stringResource(id = R.string.title),
            onChangeValue = { onTitle(it) },

            modifier = Modifier
                .wrapContentHeight()

        )
        Spacer(modifier = Modifier.size(16.dp))
        ImagePickerScreen(onResource, onThumbnailResource, onType)
        Spacer(modifier = Modifier.size(16.dp))
        TextFieldPost(
            hint = stringResource(id = R.string.caption),
            onChangeValue = {onDescription(it)},
            modifier = Modifier
                .wrapContentHeight()
                .heightIn(min = 100.dp)
        )
        Spacer(modifier = Modifier.weight(1f))
        StandardButton(
            title = stringResource(id = R.string.publish),
            enabled = true,
            loading = loading,
            backgroundColor = gray
        ) {
            onPublish()
        }
    }
}


@Composable
fun ImagePickerScreen(onResult: (ByteArray) -> Unit, onThumbnailResult: (ByteArray) -> Unit, onType: (Constants.ContentType) -> Unit) {
    var contentType by remember { mutableStateOf(Constants.ContentType.IMAGE) }
    var content by remember { mutableStateOf<ByteArray?>(null) }
    var thumbnailContent by remember { mutableStateOf<ByteArray?>(null) }
    val context = LocalContext.current

    val contentPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        content = getBytesFromUri(context, uri)
        content?.let{
            onResult(it)
        }
        thumbnailContent = ThumbnailBuilder.createThumbnail(content, contentType)
        thumbnailContent?.let{
            onThumbnailResult(it)
        }
        onType(contentType)
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
    ) {
        thumbnailContent?.let { thumbnailContent ->
            Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .clip(shape = RoundedCornerShape(8.dp))
                .background(gray),
            ) {
                var modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                if(contentType == Constants.ContentType.TEXT) {
                    modifier = Modifier
                        .height(300.dp)
                        .background(Color.White)
                }

                Image(
                    painter = rememberAsyncImagePainter(model = thumbnailContent),
                    contentDescription = null,
                    modifier = modifier,
                    contentScale = ContentScale.Fit
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            IconButton(modifier = Modifier.size(60.dp), onClick = {
                contentPickerLauncher.launch("image/*")
                contentType = Constants.ContentType.IMAGE
            }) {
                Icon(
                    imageVector = Icons.Default.Image,
                    contentDescription = "Image",
                    modifier = Modifier.size(60.dp),
                    tint = gray
                )
            }
            IconButton(modifier = Modifier.size(60.dp), onClick = {
                contentPickerLauncher.launch("application/pdf")
                contentType = Constants.ContentType.TEXT
            }) {
                Icon(
                    imageVector = Icons.Default.PictureAsPdf,
                    contentDescription = "PDF File",
                    modifier = Modifier.size(60.dp),
                    tint = gray
                )
            }
        }
    }
}

fun getBytesFromUri(context: Context, uri: Uri?): ByteArray? {
    uri?.let {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val byteBuffer = ByteArrayOutputStream()
            val buffer = ByteArray(1024)
            var len: Int

            while (inputStream?.read(buffer).also { len = it ?: -1 } != -1) {
                byteBuffer.write(buffer, 0, len)
            }

            inputStream?.close()

            byteBuffer.toByteArray()

        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    return null
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun NewPostPreview() {
    ArtisTheme {
        Content(loading = false, onTitle = {}, onDescription = {}, onResource = {}, onThumbnailResource = {}, onType = {},onPublish = {})
    }
}