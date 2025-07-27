package com.setembreiros.artis.ui.review

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.setembreiros.artis.R
import com.setembreiros.artis.ui.commponents.StandardButton
import com.setembreiros.artis.ui.commponents.TextFieldPost
import com.setembreiros.artis.ui.theme.gray

@Composable
fun CreateReviewScreen(postId: String) {
    val context = LocalContext.current
    val viewModel: CreateReviewViewModel = hiltViewModel()
    val loading by viewModel.loading.collectAsStateWithLifecycle()
    var selectedRating by remember { mutableIntStateOf(0) }
    viewModel.setPostId(postId)

    DisposableEffect(context) {
        onDispose {

        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
            .padding(horizontal = 32.dp, vertical = 32.dp),
        verticalArrangement = Arrangement.Top
    ) {

        StarRatingSelector(
          rating = selectedRating,
          onRatingSelected = {
              selectedRating = it
              viewModel.setRating(it)
          }
        )
        Spacer(modifier = Modifier.size(16.dp))
        TextFieldPost(
            placeholder = stringResource(id = R.string.title),
            onChangeValue = { viewModel.setTitle(it) },
            modifier = Modifier
                .wrapContentHeight()
                .heightIn(max = 50.dp)
        )
        Spacer(modifier = Modifier.size(16.dp))
        TextFieldPost(
            placeholder = stringResource(id = R.string.write_review),
            onChangeValue = { viewModel.setDescription(it) },
            modifier = Modifier
                .wrapContentHeight()
                .heightIn(min = 400.dp)
                .weight(1f)
        )
        Spacer(modifier = Modifier.size(16.dp))
        StandardButton(
            title = stringResource(id = R.string.publish),
            enabled = true,
            loading = loading,
            backgroundColor = gray
        ) {
            viewModel.publish()
        }
    }
}

@Composable
fun StarRatingSelector(
    rating: Int,
    onRatingSelected: (Int) -> Unit,
    maxStars: Int = 5,
    starSize: Dp = 40.dp
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        repeat(maxStars) { index ->
            val starNumber = index + 1
            IconButton(
                onClick = { onRatingSelected(starNumber) },
                modifier = Modifier.size(starSize)
            ) {
                Icon(
                    imageVector = if (starNumber <= rating) Icons.Filled.Star else Icons.Outlined.Star,
                    contentDescription = "Rate $starNumber stars",
                    tint = if (starNumber <= rating) Color(0xFFFFEC99)
                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
