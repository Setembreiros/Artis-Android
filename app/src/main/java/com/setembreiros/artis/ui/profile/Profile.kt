package com.setembreiros.artis.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.setembreiros.artis.R
import com.setembreiros.artis.domain.model.UserProfile
import com.setembreiros.artis.domain.model.post.Post
import com.setembreiros.artis.ui.commponents.StandardTextField
import com.setembreiros.artis.ui.post.DynamicPostsVerticalGrid
import com.setembreiros.artis.ui.theme.greenBackground
import com.setembreiros.artis.ui.theme.pinkBackground
import com.setembreiros.artis.ui.theme.yellowBackground

@Composable
fun Profile(
    isOwnProfile: Boolean,
    userProfile: UserProfile,
    posts: List<Post>,
    onImageClick: (postId: String) -> Unit,
    onLoadMore: () -> Unit,
    isLoading: Boolean,
    isThereMorePosts: Boolean,
    onFollowClick: () -> Unit,
    onShowFollows: () -> Unit
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
                if (isOwnProfile) {
                    HelloHeader(userProfile.username)
                } else {
                    OtherUserHeader(
                        userProfile.username,
                        isFollowing = userProfile.isFollowedByCurrentUser,
                        onFollowClick = onFollowClick
                    )
                }
                BioSection(userProfile.bio, isOwnProfile)
                HorizontalDivider(
                    color = Color.Black,
                    thickness = 2.dp,
                    modifier = Modifier.padding(top = 16.dp)
                )
                EventSection(isOwnProfile)
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(top = 16.dp)
            ) {
                PostsSection(
                    posts,
                    onLoadMore,
                    onImageClick,
                    isLoading,
                    isThereMorePosts
                )
                UserInfoHeader(
                    Modifier.align(Alignment.TopCenter),
                    userProfile = userProfile,
                    onShowFollows
                )
            }
        }
}

@Composable
fun HelloHeader(username: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = stringResource(id = R.string.hello, username),
            modifier = Modifier.weight(2f),
            color = Color.Black
        )
    }
}

@Composable
fun OtherUserHeader(username: String,
                    isFollowing: Boolean,
                    onFollowClick: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {// Mostrar o botón só se non é o propio perfil
        FollowButton(
            username = username,
            isFollowing = isFollowing,
            onFollowClick = onFollowClick,
        )
    }
}

@Composable
fun BioSection(bio: String, isOwner: Boolean) {
    Box(
        modifier = Modifier.padding(top = 8.dp)
    ) {
        BioBox(bio)
        if (isOwner) {
            EditFloatingButton(
                modifier = Modifier.align(Alignment.BottomEnd)
            )
        }
    }
}

@Composable
fun BioBox(bio: String) {
    StandardTextField(
        hint = bio,
        onChangeValue = {},
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        modifier = Modifier
            .border(2.dp, Color.Black, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .height(80.dp)
    )
}

@Composable
fun EditFloatingButton(modifier: Modifier) {
    SmallFloatingActionButton(
        onClick = { },
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.secondary,
        shape = CircleShape,
        modifier = modifier
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

@Composable
fun EventSection(isOwner: Boolean) {
    Column(
        modifier = Modifier.padding(top = 16.dp)
    ) {
        EventHeader()
        EventBox(isOwner)
    }
}

@Composable
fun EventHeader() {
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
}

@Composable
fun EventBox(isOwner: Boolean) {
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
        if (isOwner) {
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

@Composable
fun UserInfoHeader(
    modifier: Modifier,
    userProfile: UserProfile?,
    onShowFollows: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = modifier
            .padding(horizontal = 32.dp)
            .fillMaxWidth()
    ) {
        PostAmountBox(userProfile?.postsAmount ?: 0)
        ImageProfile()
        FollowAmountBox(userProfile?.followersAmount ?: 0, onShowFollows)
    }
}

@Composable
fun FollowButton(
    username: String,
    modifier: Modifier = Modifier,
    isFollowing: Boolean = false, // You can manage this state
    onFollowClick: () -> Unit = {} // Add your follow logic here
) {
    val buttonText = (if (isFollowing) stringResource(R.string.following) else stringResource(R.string.follow)) + " " + username
    val gradientColors = if (isFollowing) {
        listOf(Color.LightGray, Color.Gray) // Subtle gradient for "Following" state
    } else {
        listOf(
            Color(0xFFB2F2BB),
            Color(0xFFFFEC99)
        )
    }

    val gradientBrush = Brush.horizontalGradient(
        colors = gradientColors,
        startX = 0f,
        endX = Float.POSITIVE_INFINITY
    )

    val textColor = if (isFollowing) Color.DarkGray else Color(0xFF1E1E1E)

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(gradientBrush)
            .border(2.dp, Color.Black, RoundedCornerShape(20.dp))
            .clickable { onFollowClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = buttonText,
            color = textColor,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

@Composable
fun PostAmountBox(postAmount: Int) {
    Text(
        text = "${postAmount}\nPosts",
        textAlign = TextAlign.Center,
        modifier = Modifier
            .border(2.dp, Color.Black, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(
                color = greenBackground
            )
            .height(68.dp)
            .width(68.dp)
            .wrapContentHeight(),
        color = Color.Black
    )
}

@Composable
fun ImageProfile() {
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
}

@Composable
fun FollowAmountBox(followAmount: Int, onShow: () -> Unit) {
    Text(
        text = "${followAmount}\nFollows",
        textAlign = TextAlign.Center,
        modifier = Modifier
            .border(2.dp, Color.Black, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(
                color = greenBackground
            )
            .height(68.dp)
            .width(68.dp)
            .wrapContentHeight()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null // elimina o efecto visual ao premer
            ) {
                onShow()
            },
        color = Color.Black
    )
}

@Composable
fun PostsSection(
    posts: List<Post>,
    onLoadMore: () -> Unit,
    onImageClick: (postId: String) -> Unit,
    isLoading: Boolean,
    isThereMorePosts: Boolean
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
            PostSectionHeader()
            HorizontalDivider(color = Color.Black, thickness = 2.dp)
            DynamicPostsVerticalGrid(posts, onLoadMore, onImageClick, isLoading, isThereMorePosts)
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun PostSectionHeader() {
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
}