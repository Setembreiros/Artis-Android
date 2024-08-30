package com.setembreiros.artis.ui.post

import android.content.Context
import android.content.res.Configuration
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.setembreiros.artis.R
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
        onPublish = {viewModel.publish()}
        )

}

@Composable
fun Content(
    loading: Boolean,
    onTitle: (String) -> Unit,
    onDescription: (String) -> Unit,
    onResource: (ByteArray) -> Unit,
    onPublish: () -> Unit
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
        ImagePickerScreen {
            onResource(it)
        }
        Spacer(modifier = Modifier.size(16.dp))
        TextFieldPost(
            hint = stringResource(id = R.string.caption),
            onChangeValue = {onDescription(it)},
            modifier = Modifier
                .wrapContentHeight()
                .heightIn(min = 150.dp)
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
fun ImagePickerScreen(onResult: (ByteArray) -> Unit) {
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
        val result = getBytesFromUri(context, uri)
        result?.let{
            onResult(it)
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
    ) {


        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(shape = RoundedCornerShape(8.dp))
                .background(gray)
                .clickable { imagePickerLauncher.launch("image/*") },
        ) {

            imageUri?.let { uri ->
                Image(
                    painter = rememberAsyncImagePainter(uri),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)

                        .clickable { imagePickerLauncher.launch("image/*") },
                    contentScale = ContentScale.Fit
                )
            }?: Text(text = stringResource(id = R.string.add_post), color = MaterialTheme.colorScheme.onSecondary)

        }

        Spacer(modifier = Modifier.height(16.dp))
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
        Content(loading = false, onTitle = {}, onDescription = {}, onResource = {}, onPublish = {})
    }
}