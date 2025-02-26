package com.setembreiros.artis.ui.profile

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.setembreiros.artis.R
import com.setembreiros.artis.domain.model.profile.UserProfile
import com.setembreiros.artis.ui.commponents.StandardButton
import com.setembreiros.artis.ui.commponents.StandardTextField
import com.setembreiros.artis.ui.theme.ArtisTheme
import com.setembreiros.artis.ui.theme.gray

@Composable
fun EditProfileScreen(onSavedProfile: () -> Unit) {
    val context = LocalContext.current
    val viewModel: EditProfileViewModel = hiltViewModel()
    val userProfile by viewModel.profile.collectAsStateWithLifecycle()
    val loading by viewModel.loading.collectAsStateWithLifecycle()

    EditProfileContentScreen(
        context,
        userProfile = userProfile,
        onSaveButtonClick = {
            viewModel.saveProfile { success ->
                if (success) {
                    onSavedProfile()
                } else {
                    Toast.makeText(
                        context,
                        "Error al guardar el perfil",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        },
        onProfileImageClick = {
            val byteArray = context.contentResolver.openInputStream(it)?.use { inputStream ->
                inputStream.readBytes() // Lee el contenido de la imagen como ByteArray
            }
            if (byteArray != null) {
                viewModel.setProfileImage(byteArray)
                viewModel.saveProfileImage { success ->
                    if (!success) {
                        Toast.makeText(
                            context,
                            "Error al guardar la imagen de perfil",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

        },
        loading = loading
    )
}

@Composable
fun EditProfileContentScreen(
    context: Context,
    userProfile: UserProfile?,
    onSaveButtonClick: () -> Unit,
    onProfileImageClick: (Uri) -> Unit,
    loading: Boolean
) {
    var uriContent by remember { mutableStateOf<Uri?>(null) }

    val contentPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uriContent = uri
        uri?.let{
            onProfileImageClick(it)
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(top = 32.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(id = R.string.hello, userProfile?.username ?: ""),
                    modifier = Modifier.weight(2f),
                    textAlign = TextAlign.Center,
                    color = Color.Black
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth()
            ) {
                AsyncImage(
                    model = "https://img.freepik.com/premium-vector/business-office-african-american-manager-usinessman-avatar-icon-head-portrait-occupation_805465-135.jpg",
                    placeholder = painterResource(id = R.drawable.male_avatar_placeholder),
                    contentDescription = stringResource(R.string.avatar_description),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .clickable {
                            contentPickerLauncher.launch("image/*")
                        }
                        .height(75.dp)
                        .width(75.dp)
                        .clip(CircleShape)
                )
            }
            Box(
                modifier = Modifier.padding(top = 8.dp)
            ) {
                StandardTextField(
                    hint = userProfile?.bio.toString(),
                    onChangeValue = {},
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    modifier = Modifier
                        .border(2.dp, Color.Black, RoundedCornerShape(16.dp))
                        .clip(RoundedCornerShape(16.dp))
                        .height(80.dp)
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.your_socials),
                    modifier = Modifier.weight(2f),
                    textAlign = TextAlign.Center,
                    color = Color.Black
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 16.dp)
            ) {
                // Place images of social networks here (?)
                Column {

                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 16.dp)
            ){
                StandardButton(
                    title = stringResource(id = R.string.save),
                    enabled = true,
                    loading = loading,
                    backgroundColor = gray
                ) {
                    onSaveButtonClick()
                }
            }

        }
    }
}

@Preview
@Composable
fun EditProfilePreview() {
    val context = LocalContext.current
    val imageResource = LocalContext.current.resources.openRawResource(R.raw.imaxe_de_proba)
    val imageContent = imageResource.readBytes()

    ArtisTheme {
        EditProfileContentScreen(
            context,
            UserProfile(
                "newHouses",
                "Sergio Simons es reconocido por su experiencia y liderazgo en telecomunicaciones, donde se le considera \"el puto amo\" por su maestría técnica y profesionalismo.",
                "Simons",
                "https://seoi.net/penint/",
                imageContent
            ),
            onSaveButtonClick = {},
            onProfileImageClick = {},
            false)
    }
}