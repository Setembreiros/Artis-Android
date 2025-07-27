package com.setembreiros.artis.domain.model

data class Review(val reviewId: Long, val username: String, val postId: String, val title: String, val content: String, val rating: Int, val isOwner: Boolean = false)