package com.setembreiros.artis.ui.profile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun OwnProfileScreen(onImageClick: (postId: String) -> Unit) {
    val viewModel: ProfileViewModel = hiltViewModel()
    val userProfile by viewModel.profile.collectAsStateWithLifecycle()
    val posts by viewModel.posts.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val isThereMorePosts by viewModel.thereAreMorePosts.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        userProfile?.let {
            Profile(
                true,
                userProfile = it,
                posts = posts,
                onImageClick = onImageClick,
                onLoadMore = { viewModel.loadMorePosts() },
                isLoading = isLoading,
                isThereMorePosts = isThereMorePosts,
                onFollowClick = { })
        }

        if (userProfile == null) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.surface
            )
        }
    }
}