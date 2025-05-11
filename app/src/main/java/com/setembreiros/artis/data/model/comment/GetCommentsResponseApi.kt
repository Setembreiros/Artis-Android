package com.setembreiros.artis.data.model.comment

data class GetCommentsResponseApi(val comments: Array<GetCommentsDto>, val lastCommentId: Long)

data class GetCommentsDto(
    val commentId: Long,
    val postId: String,
    val username: String,
    val content: String,
    val createdAt: String,
    val updatedAt: String
)