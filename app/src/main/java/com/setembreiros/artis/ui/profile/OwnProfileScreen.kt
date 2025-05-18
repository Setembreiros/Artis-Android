package com.setembreiros.artis.ui.profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun OwnProfileScreen(onImageClick: (postId: String) -> Unit) {
    val viewModel: ProfileViewModel = hiltViewModel()
    val userProfile by viewModel.profile.collectAsStateWithLifecycle()
    val posts by viewModel.posts.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val isThereMorePosts by viewModel.thereAreMorePosts.collectAsStateWithLifecycle()

    Profile(
        true,
        userProfile = userProfile,
        posts = posts,
        onImageClick = onImageClick,
        onLoadMore = { viewModel.loadMorePosts() },
        isLoading = isLoading,
        isThereMorePosts = isThereMorePosts,
    )
}