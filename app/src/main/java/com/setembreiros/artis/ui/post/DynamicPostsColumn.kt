package com.setembreiros.artis.ui.post

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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

    // Detectar cando o usuario chega ao final
    val isAtBottom by remember {
        derivedStateOf {
            val totalItems = listState.layoutInfo.totalItemsCount
            val lastVisibleIndex = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1
            totalItems > 1 && lastVisibleIndex >= totalItems - 6
        }
    }
    LaunchedEffect(isAtBottom, isLoading) {
        if (isAtBottom && !isLoading && isThereMorePosts) {
            onLoadMore()
        }
    }

    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        items(posts) { post ->
            PostDetailsView(context, post)
        }
        item {
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}