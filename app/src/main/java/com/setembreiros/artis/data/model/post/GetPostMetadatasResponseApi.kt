package com.setembreiros.artis.data.model.post

import com.setembreiros.artis.common.Constants.ContentType

data class GetPostMetadatasResponseApi(val posts: Array<GetPostMetadataDto>, val lastPostId: String, val lastPostCreatedAt: String)

data class GetPostMetadataDto(
    val post_id: String,
    val username: String,
    val type: ContentType,
    val file_type: String,
    val title: String,
    val description: String,
    val reviews: Long,
    val isReviewedByCurrentUser: Boolean,
    val comments: Long,
    val likes: Long,
    val isLikedByCurrentUser: Boolean,
    val superlikes: Long,
    val isSuperlikedByCurrentUser: Boolean,
    val size: Long,
    val created_at: String,
    val last_updated: String
)