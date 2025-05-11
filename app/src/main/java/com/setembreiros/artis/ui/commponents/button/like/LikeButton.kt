package com.setembreiros.artis.ui.commponents.button.like

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LikeButton(
    isLiked: Boolean,
    likesCount: Long,
    onLike: () -> Unit,
    onShow: () -> Unit,
) {
    var currentIsLiked by remember { mutableStateOf(isLiked) }
    val scale by animateFloatAsState(
        targetValue = if (currentIsLiked) 1.2f else 1f,
        animationSpec = spring(
            dampingRatio = 0.4f,
            stiffness = 200f
        ), label = ""
    )

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Outlined.Favorite,
            contentDescription = "Like",
            tint = if (isLiked) Color.Red else MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null // elimina o efecto visual ao premer
                ) {
                    currentIsLiked = !currentIsLiked
                    onLike()
                }
                .scale(scale)
                .graphicsLayer {
                    rotationZ = if (currentIsLiked) 0f else 0f
                }
                .animateContentSize()
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "$likesCount",
            fontSize = 16.sp,
            color = if (isLiked) Color.Red else MaterialTheme.colorScheme.primary,
            modifier = Modifier.animateContentSize()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null // elimina o efecto visual ao premer
                ) {
                    onShow()
                }
        )
    }
}