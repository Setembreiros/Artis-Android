package com.setembreiros.artis.data.model.post

data class ConfirmPostRequestApi(val isConfirmed: Boolean, val postId: String, val isMultipart: Boolean, val uploadId: String, val completedParts: List<CompletedPart>?)

data class CompletedPart(val partNumber: Int, val eTag: String)

