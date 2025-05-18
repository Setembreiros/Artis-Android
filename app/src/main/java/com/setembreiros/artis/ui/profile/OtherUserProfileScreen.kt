package com.setembreiros.artis.ui.profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun OtherUserProfileScreen(username:String, onImageClick: (postId: String) -> Unit) {
    val viewModel: OtherUserProfileViewModel = hiltViewModel()
    val userProfile by viewModel.profile.collectAsStateWithLifecycle()
    val posts by viewModel.posts.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val isThereMorePosts by viewModel.thereAreMorePosts.collectAsStateWithLifecycle()

    LaunchedEffect(username) {
        viewModel.loadProfile(username)
        viewModel.loadInitialPosts(username)
    }

    Profile(
        false,
        userProfile = userProfile,
        posts = posts,
        onImageClick = onImageClick,
        onLoadMore = { viewModel.loadMorePosts(username) },
        isLoading = isLoading,
        isThereMorePosts = isThereMorePosts,
    )
}