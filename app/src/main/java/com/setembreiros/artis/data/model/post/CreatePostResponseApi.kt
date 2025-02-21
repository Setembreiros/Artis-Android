package com.setembreiros.artis.data.model.post

data class CreatePostResponseApi(val postId: String, val uploadId: String, val presignedUrls: Array<String>, val presignedThumbnailUrl: String)
