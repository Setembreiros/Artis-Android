package com.setembreiros.artis.ui.post

import com.setembreiros.artis.domain.model.post.UploadProgress
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object UploadProgressManager {
    private var _progress = MutableStateFlow<UploadProgress>(UploadProgress.Init)
    val progress: StateFlow<UploadProgress> = _progress

    fun updateProgress(newProgress: UploadProgress) {
        _progress.value = newProgress
    }
}