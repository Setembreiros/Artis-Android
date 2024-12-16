package com.setembreiros.artis.ui.post

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun PostDetailsScreen(postId: String) {
    val context = LocalContext.current
    val viewModel: PostDetailsViewModel = hiltViewModel()
    val posts by viewModel.posts.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val isThereMorePosts by viewModel.isThereMorePosts.collectAsStateWithLifecycle()

    DynamicPostsColumn(context, postId, posts, { viewModel.loadMorePosts() }, isLoading, isThereMorePosts)
}