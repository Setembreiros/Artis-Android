package com.setembreiros.artis.data.model.review

data class CreateReviewRequestApi(val username: String, val postId: String, val content: String, val rating: Int)