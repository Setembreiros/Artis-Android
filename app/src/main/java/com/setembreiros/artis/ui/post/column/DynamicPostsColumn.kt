package com.setembreiros.artis.ui.post.column

import android.content.Context
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.setembreiros.artis.domain.model.post.Post
import com.setembreiros.artis.ui.commponents.DynamicColumn
import com.setembreiros.artis.ui.post.item.PostDetailsView

@Composable
fun DynamicPostsColumn(
    context: Context,
    postId: String,
    posts: List<Post>,
    onLoadMore: () -> Unit,
    isLoading: Boolean,
    thereAreMorePosts: Boolean,
    onChange: () -> Unit,
    onAddReview: (postId: String) -> Unit
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
            PostDetailsView(context, post, onChange, onAddReview)
        },
        onLoadMore = onLoadMore,
        isLoading = isLoading,
        thereAreMoreItems = thereAreMorePosts,
        modifier = Modifier.padding(8.dp),
        contentPadding = PaddingValues(bottom = 56.dp),
        listState = listState
    )
}