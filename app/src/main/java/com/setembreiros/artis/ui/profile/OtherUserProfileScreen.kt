package com.setembreiros.artis.ui.profile

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.setembreiros.artis.ui.commponents.follower.FollowerSection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun OtherUserProfileScreen(username:String, onImageClick: (postId: String) -> Unit) {
    val context = LocalContext.current
    val viewModel: OtherUserProfileViewModel = hiltViewModel()
    val userProfile by viewModel.profile.collectAsStateWithLifecycle()
    val posts by viewModel.posts.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val isSecondLoading by viewModel.isSecondLoading.collectAsStateWithLifecycle()
    val isThereMorePosts by viewModel.thereAreMorePosts.collectAsStateWithLifecycle()
    var showFollows by remember { mutableStateOf(false) }
    val followers by viewModel.followers.collectAsState()
    val isThereMoreFollowers by viewModel.thereAreMoreFollowers.collectAsStateWithLifecycle()
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

    LaunchedEffect(username) {
        viewModel.loadProfile(username)
        viewModel.loadInitialPosts(username)
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        userProfile?.let {
            Profile(
                false,
                userProfile = it,
                posts = posts,
                onImageClick = onImageClick,
                onLoadMore = { viewModel.loadMorePosts(username) },
                isLoading = isLoading,
                isThereMorePosts = isThereMorePosts,
                onFollowClick = { viewModel.toggleFollow(username) },
                onShowFollows = {
                    viewModel.loadInitialFollows(username)
                    showFollows = true
                }
            )
        }

        if (showFollows) {
            FollowerSection(
                followers,
                onLoadMore = {
                    viewModel.loadMoreFollows(username)
                },
                isSecondLoading,
                isThereMoreFollowers,
                onDismiss = { showFollows = false }
            )
        }

        if(userProfile == null) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.surface
            )
        }
    }
}