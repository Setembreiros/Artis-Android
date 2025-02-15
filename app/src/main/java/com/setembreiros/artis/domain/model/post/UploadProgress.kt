package com.setembreiros.artis.domain.model.post

sealed class UploadProgress {
    object Init : UploadProgress()
    object Loading : UploadProgress()  // estado para subidas non-multipart
    data class Progress(val percentage: Int) : UploadProgress()
    object Complete : UploadProgress()
    data class Error(val exception: Throwable) : UploadProgress()
}