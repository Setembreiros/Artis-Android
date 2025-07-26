package com.setembreiros.artis.data.model.review

data class GetReviewsResponseApi(val reviews: Array<GetReviewsDto>, val lastReviewId: Long)

data class GetReviewsDto(
    val reviewId: Long,
    val postId: String,
    val username: String,
    val content: String,
    val rating: Int,
    val createdAt: String,
    val updatedAt: String
)