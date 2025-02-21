package com.setembreiros.artis.ui.post

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.setembreiros.artis.domain.model.post.UploadProgress

@Composable
fun PublishPostScreen() {
    val progress by UploadProgressManager.progress.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        when (val current = progress) {
            is UploadProgress.Progress -> {
                LinearProgressIndicator(
                    progress = { current.percentage.toFloat() / 100 },
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xff4a90e2),
                    trackColor = Color(0xffd0eaff)
                )
            }
            UploadProgress.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.size(48.dp),
                    color = Color(0xff4a90e2),
                    strokeWidth = 4.dp
                )
            }
            is UploadProgress.Complete -> {
                Text("Subida completa!", style = MaterialTheme.typography.headlineSmall)
            }
            is UploadProgress.Error -> {
                Text("Erro: ${current.exception.message}", color = MaterialTheme.colorScheme.error)
            }
            else -> {}
        }
    }
}