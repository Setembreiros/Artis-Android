package com.setembreiros.artis.ui.post

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.setembreiros.artis.domain.model.post.Post
import com.setembreiros.artis.ui.commponents.DynamicColumn

@Composable
fun DynamicPostsColumn(
    context: Context,
    postId: String,
    posts: List<Post>,
    onLoadMore: () -> Unit,
    isLoading: Boolean,
    isThereMorePosts: Boolean
) {
    val listState = rememberLazyListState()

    val postIndex = posts.indexOfFirst { it.metadata.postId == postId }
    LaunchedEffect(postIndex) {
        if (postIndex >= 0) {
            listState.scrollToItem(postIndex)
        }
    }

    DynamicColumn(
        items = posts,
        itemView = { post ->
            PostDetailsView(context, post)
        },
        onLoadMore = onLoadMore,
        isLoading = isLoading,
        isThereMorePosts = isThereMorePosts,
        modifier = Modifier.padding(8.dp),
        contentPadding = PaddingValues(bottom = 56.dp)
    )
}