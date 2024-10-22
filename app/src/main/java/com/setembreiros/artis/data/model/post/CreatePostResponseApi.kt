package com.setembreiros.artis.data.model.post

data class CreatePostResponseApi(val postId: String, val presignedUrl: String, val presignedThumbnailUrl: String)
