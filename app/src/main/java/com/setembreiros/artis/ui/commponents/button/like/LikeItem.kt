package com.setembreiros.artis.ui.commponents.button.like

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.setembreiros.artis.domain.model.Like

@Composable
fun LikeItem(like: Like) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                // Nome de usuario
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = like.username,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray,
                        fontSize = 16.sp
                    )
                }
                // Nome real e completo
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = like.fullname,
                        fontWeight = FontWeight.Bold,
                        color = Color.LightGray,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}