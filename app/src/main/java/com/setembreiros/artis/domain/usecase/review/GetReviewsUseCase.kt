package com.setembreiros.artis.domain.usecase.review

import com.setembreiros.artis.data.repository.ReviewRepository
import com.setembreiros.artis.domain.base.Resource
import com.setembreiros.artis.domain.model.Review
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class GetReviewsUseCase @Inject constructor(private val reviewRepository: ReviewRepository)  {
    suspend fun invoke(postId: String, lastReviewId: Long) : Pair<List<Review>,Boolean> = coroutineScope {
        getReviews(postId, lastReviewId)
    }


    private suspend fun getReviews(postId: String, lastReviewId: Long) : Pair<List<Review>,Boolean> {
        return when(val response = reviewRepository.getReviews(postId, lastReviewId)){
            is Resource.Success -> {
                response.value
            }
            is Resource.Failure -> Pair(listOf(), false)
        }
    }
}