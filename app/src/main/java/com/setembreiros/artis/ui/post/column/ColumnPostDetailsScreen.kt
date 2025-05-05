package com.setembreiros.artis.ui.post.column

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun ColumnPostDetailsScreen(postId: String) {
    val context = LocalContext.current
    val viewModel: ColumnPostDetailsViewModel = hiltViewModel()
    val posts by viewModel.posts.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val isThereMorePosts by viewModel.isThereMorePosts.collectAsStateWithLifecycle()

    DynamicPostsColumn(context, postId, posts, { viewModel.loadMorePosts() }, isLoading, isThereMorePosts, { viewModel.updatePosts()})
}