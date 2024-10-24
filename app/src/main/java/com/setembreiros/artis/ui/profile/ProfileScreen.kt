package com.setembreiros.artis.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.setembreiros.artis.domain.model.UserProfile
import com.setembreiros.artis.domain.model.post.Post
import com.setembreiros.artis.ui.commponents.StandardTextField
import com.setembreiros.artis.ui.theme.ArtisTheme
import com.setembreiros.artis.ui.theme.greenBackground
import com.setembreiros.artis.ui.theme.pinkBackground
import com.setembreiros.artis.ui.theme.yellowBackground
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import com.setembreiros.artis.common.Constants
import com.setembreiros.artis.domain.model.post.PostMetadata
import com.setembreiros.artis.ui.commponents.PostThumbnail

@Composable
fun ProfileScreen(onImageClick: (postId: String) -> Unit) {
    val viewModel: ProfileViewModel = hiltViewModel()
    val userProfile by viewModel.profile.collectAsStateWithLifecycle()
    val posts by viewModel.posts.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    ContentScreen(
        userProfile = userProfile,
        posts = posts,
        onImageClick = onImageClick,
        isLoading = isLoading,
    )
}

@Composable
fun ContentScreen(
    userProfile: UserProfile?,
    posts: List<Post>,
    onImageClick: (postId: String) -> Unit,
    isLoading: Boolean
) {
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
                    color = Color.Black
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
                SmallFloatingActionButton(
                    onClick = { },
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.secondary,
                    shape = CircleShape,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = 10.dp, y = 10.dp)
                        .size(40.dp)
                        .border(2.dp, Color.Black, CircleShape)
                ) {
                    Icon(
                        Icons.Filled.Edit,
                        stringResource(id = R.string.edit_biography)
                    )
                }
            }
            HorizontalDivider(
                color = Color.Black,
                thickness = 2.dp,
                modifier = Modifier.padding(top = 16.dp)
            )
            Column(
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Box{
                    Text(
                        text = "31 Sep, A Coruña",
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(2.dp, Color.Black, RoundedCornerShape(16.dp))
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                color = yellowBackground
                            )
                            .height(40.dp)
                            .wrapContentHeight(),
                        color = Color.Black
                    )
                    Box(
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .align(Alignment.CenterStart)
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(Color.Black)
                    )
                    Box(
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .align(Alignment.CenterEnd)
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(Color.Black)
                    )

                }
                Row(
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.view_events),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .padding(end = 2.dp)
                            .weight(1f)
                            .border(2.dp, Color.Black, RoundedCornerShape(16.dp))
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                color = pinkBackground
                            )
                            .height(40.dp)
                            .wrapContentHeight(),
                        color = Color.Black
                    )
                    Text(
                        text = stringResource(id = R.string.edit_event),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .padding(start = 2.dp)
                            .weight(1f)
                            .border(2.dp, Color.Black, RoundedCornerShape(16.dp))
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                color = pinkBackground
                            )
                            .height(40.dp)
                            .wrapContentHeight(),
                        color = Color.Black
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(top = 16.dp)
        ) {
            Box(
                modifier = Modifier
                    .padding(top = 32.dp)
                    .border(2.dp, Color.Black, RoundedCornerShape(topEnd = 32.dp, topStart = 32.dp))
                    .clip(RoundedCornerShape(topEnd = 32.dp, topStart = 32.dp))
                    .background(
                        color = Color.White
                    )
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                Column {
                    Box(
                        modifier = Modifier.padding(top = 40.dp)
                    ) {

                        Text(
                            text = stringResource(id = R.string.photographies),
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth(),
                            color = Color.Black
                        )
                        Box(
                            modifier = Modifier
                                .padding(start = 16.dp)
                                .align(Alignment.CenterStart)
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(Color.Black)
                        )
                        Box(
                            modifier = Modifier
                                .padding(end = 16.dp)
                                .align(Alignment.CenterEnd)
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(Color.Black)
                        )
                    }
                    HorizontalDivider(color = Color.Black, thickness = 2.dp)
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        contentPadding = PaddingValues(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val sortedPosts = posts.sortedBy { it.metadata.createdAt }
                        items(sortedPosts) { post ->
                            PostThumbnail(post, onNavigateToImageDetails = {
                                onImageClick(post.metadata.postId)
                            })
                        }
                    }
                    if (isLoading) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .padding(horizontal = 32.dp)
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "${posts.size}\n Posts",
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .border(2.dp, Color.Black, RoundedCornerShape(16.dp))
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            color = greenBackground
                        )
                        .height(64.dp)
                        .width(64.dp)
                        .wrapContentHeight(),
                    color = Color.Black
                )
                AsyncImage(
                    model = "https://img.freepik.com/premium-vector/business-office-african-american-manager-usinessman-avatar-icon-head-portrait-occupation_805465-135.jpg",
                    placeholder = painterResource(id = R.drawable.male_avatar_placeholder),
                    contentDescription = stringResource(R.string.avatar_description),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .height(75.dp)
                        .width(75.dp)
                        .clip(CircleShape)
                )
                Text(
                    text = "65 Follow",
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .border(2.dp, Color.Black, RoundedCornerShape(16.dp))
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            color = greenBackground
                        )
                        .height(64.dp)
                        .width(64.dp)
                        .wrapContentHeight(),
                    color = Color.Black
                )
            }

        }
    }
}

@Preview
@Composable
fun ProfilePreview() {
    val imageResource = LocalContext.current.resources.openRawResource(R.raw.imaxe_de_proba)
    val imageContent = imageResource.readBytes()
    val imageResource2 = LocalContext.current.resources.openRawResource(R.raw.image_test_2)
    val imageContent2 = imageResource2.readBytes()
    val videoResource = LocalContext.current.resources.openRawResource(R.raw.video_test_1)
    val videoContent = videoResource.readBytes()
    val videoResource2 = LocalContext.current.resources.openRawResource(R.raw.video_test_2)
    val videoContent2 = videoResource2.readBytes()
    ArtisTheme {
        ContentScreen(
            UserProfile(
                "guillerial",
                "Guillermo Rial es reconocido por su experiencia y liderazgo en telecomunicaciones, donde se le considera \"el puto amo\" por su maestría técnica y profesionalismo.",
                "Guille",
                "https://fuckyou.com"
            ),
            listOf(Post(
                metadata = PostMetadata(
                    "","", Constants.ContentType.IMAGE,
                    title = "Sample Title",
                    description = "This is a sample description for the post.", "2024/01/03", ""
                ),
                content = imageContent2,
                thumbnail = imageContent2
            ), Post(
                metadata = PostMetadata(
                    "","", Constants.ContentType.VIDEO,
                    title = "Sample Title",
                    description = "This is a sample description for the post.", "2024/01/04", "",
                ),
                content = videoContent,
                thumbnail = null
            ), Post(
                metadata = PostMetadata(
                    "","", Constants.ContentType.VIDEO,
                    title = "Sample Title",
                    description = "This is a sample description for the post.", "2024/01/06", ""
                ),
                content = videoContent2,
                thumbnail = null
            ), Post(
                metadata = PostMetadata(
                    "","", Constants.ContentType.IMAGE,
                    title = "Sample Title",
                    description = "This is a sample description for the post.", "2024/01/01", ""
                ),
                content = imageContent,
                thumbnail = imageContent
            ), Post(
                metadata = PostMetadata(
                    "","", Constants.ContentType.IMAGE,
                    title = "Sample Title",
                    description = "This is a sample description for the post.", "2024/01/02", ""
                ),
                content = imageContent2,
                thumbnail = imageContent2
            ), Post(
                metadata = PostMetadata(
                    "","", Constants.ContentType.VIDEO,
                    title = "Sample Title",
                    description = "This is a sample description for the post.", "2024/01/05", ""
                ),
                content = videoContent,
                thumbnail = null
            )),
            onImageClick = {},
            false)
    }
}