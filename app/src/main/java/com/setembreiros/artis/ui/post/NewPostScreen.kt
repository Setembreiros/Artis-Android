package com.setembreiros.artis.ui.post

import android.content.res.Configuration
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.AudioFile
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.VideoCameraBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.setembreiros.artis.R
import com.setembreiros.artis.common.Constants
import com.setembreiros.artis.ui.commponents.BaseImagePost
import com.setembreiros.artis.ui.commponents.MediaPlayer
import com.setembreiros.artis.ui.commponents.PdfReader
import com.setembreiros.artis.ui.commponents.StandardButton
import com.setembreiros.artis.ui.commponents.TextFieldPost
import com.setembreiros.artis.ui.theme.ArtisTheme
import com.setembreiros.artis.ui.theme.gray

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
        onType = {
            viewModel.setType(it)
        },
        onPublish = {viewModel.publish(context)}
        )
}

@Composable
fun Content(
    loading: Boolean,
    onTitle: (String) -> Unit,
    onDescription: (String) -> Unit,
    onResource: (Uri) -> Unit,
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
        ImagePickerScreen(onResource, onType)
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
fun ImagePickerScreen(onResult: (Uri) -> Unit, onType: (Constants.ContentType) -> Unit) {
    var contentType by remember { mutableStateOf(Constants.ContentType.IMAGE) }
    var uriContent by remember { mutableStateOf<Uri?>(null) }
    var thereIsContent by remember { mutableStateOf(false) }

    val contentPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uriContent = uri
        uri?.let{
            thereIsContent = true
            onResult(it)
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
        if(thereIsContent) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .clip(shape = RoundedCornerShape(8.dp))
                    .background(gray),
            ) {
                when (contentType) {
                    Constants.ContentType.TEXT -> PdfReader(uriContent)
                    Constants.ContentType.IMAGE -> BaseImagePost(uriContent)
                    Constants.ContentType.AUDIO -> MediaPlayer(uriContent)
                    Constants.ContentType.VIDEO -> MediaPlayer(uriContent)
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            IconButton(modifier = Modifier.size(60.dp), onClick = {
                thereIsContent = false
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
            IconButton(modifier = Modifier.size(60.dp), onClick = {
                thereIsContent = false
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
                thereIsContent = false
                contentPickerLauncher.launch("audio/*")
                contentType = Constants.ContentType.AUDIO
            }) {
                Icon(
                    imageVector = Icons.Default.AudioFile,
                    contentDescription = "Audio",
                    modifier = Modifier.size(60.dp),
                    tint = gray
                )
            }
            IconButton(modifier = Modifier.size(60.dp), onClick = {
                thereIsContent = false
                contentPickerLauncher.launch("video/*")
                contentType = Constants.ContentType.VIDEO
            }) {
                Icon(
                    imageVector = Icons.Default.VideoCameraBack,
                    contentDescription = "Video",
                    modifier = Modifier.size(60.dp),
                    tint = gray
                )
            }
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun NewPostPreview() {
    ArtisTheme {
        Content(loading = false, onTitle = {}, onDescription = {}, onResource = {}, onType = {},onPublish = {})
    }
}