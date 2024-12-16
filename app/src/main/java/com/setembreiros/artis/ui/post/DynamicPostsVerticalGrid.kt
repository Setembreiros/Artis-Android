package com.setembreiros.artis.ui.post

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.setembreiros.artis.domain.model.post.Post
import com.setembreiros.artis.ui.commponents.PostThumbnail


@Composable
fun DynamicPostsVerticalGrid(
    context: Context,
    posts: List<Post>,
    onLoadMore: () -> Unit,
    onImageClick: (postId: String) -> Unit,
    isLoading: Boolean,
    isThereMorePosts: Boolean
) {
    val listState = rememberLazyGridState()

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

    val sortedPosts = remember(posts) {
        posts.distinctBy { it.metadata.postId }.sortedBy { it.metadata.createdAt }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyVerticalGrid(
            state = listState,
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(sortedPosts) { post ->
                PostThumbnail(context, post, onNavigateToImageDetails = {
                    onImageClick(post.metadata.postId)
                })
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
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
}
