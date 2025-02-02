package com.setembreiros.artis.domain.model.post

data class PostResponse(val postId: String, val uploadId: String, val presignedUrls: Array<String>, val presignedThumbnailUrl: String)
