package com.setembreiros.artis.domain.model

data class Comment(val commentId: Long, val username: String, val postId: String, val content: String, val isOwner: Boolean = false)