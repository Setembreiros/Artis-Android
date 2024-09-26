package com.setembreiros.artis.ui.commponents

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.setembreiros.artis.R
import com.setembreiros.artis.common.Constants

@Composable
fun PostThumbnail(thumbnail: ByteArray?, contentType: Constants.ContentType){
    Box(
        modifier = Modifier.fillMaxSize(),  // Ensures the box takes up full available space
        contentAlignment = Alignment.Center // Aligns the play button in the center of the Box
    ) {
        when (contentType) {
            Constants.ContentType.IMAGE -> BasePostThumbnail(thumbnail)
            Constants.ContentType.VIDEO -> {
                BasePostThumbnail(thumbnail)
                Image(
                    painter = painterResource(id = R.drawable.play_button),  // Play button icon
                    contentDescription = "Play Button",
                    modifier = Modifier.size(50.dp),  // Set the size of the play button
                    colorFilter = ColorFilter.tint(Color.White) // Optional: tint the play button if needed
                )
            }
            Constants.ContentType.AUDIO -> {
                if(thumbnail!!.isNotEmpty())
                    BasePostThumbnail(thumbnail)
                else {
                    Image(
                        painter = painterResource(id = R.drawable.audio_default_thumbnail),  // Play button icon
                        contentDescription = "Audio default thumbnail",
                        modifier = Modifier
                            .height(150.dp)
                            .width(100.dp)
                            .border(
                                2.dp,
                                Color.Black,
                                RoundedCornerShape(16.dp)
                            )
                            .clip(RoundedCornerShape(16.dp)),
                    )
                }
            }
            Constants.ContentType.TEXT -> BasePostThumbnail(thumbnail)
        }
    }
}

@Composable
fun BasePostThumbnail(thumbnail: ByteArray?){
    AsyncImage(
        model = thumbnail,
        modifier = Modifier
            .height(150.dp)
            .width(100.dp)
            .border(
                2.dp,
                Color.Black,
                RoundedCornerShape(16.dp)
            )
            .clip(RoundedCornerShape(16.dp)),
        contentScale = ContentScale.Crop,
        contentDescription = null,
        )
}