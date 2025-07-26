package com.setembreiros.artis.data.mapper.todomain

import android.util.Log
import com.setembreiros.artis.data.base.Mapper
import com.setembreiros.artis.data.model.review.GetReviewsResponseApi
import com.setembreiros.artis.domain.model.Review
import com.setembreiros.artis.domain.usecase.session.GetSessionUseCase
import javax.inject.Inject

class GetReviewsResponseMapperApi @Inject constructor(private val getSessionUseCase: GetSessionUseCase): Mapper<GetReviewsResponseApi, Pair<List<Review>, Boolean>> {
    override fun map(model: GetReviewsResponseApi): Pair<List<Review>, Boolean> {
        try {
            getSessionUseCase.invoke()?.username?.let { currentUser ->
                return Pair(model.reviews.map { dto ->
                    Review(
                        reviewId = dto.reviewId,
                        username = dto.username,
                        postId = dto.postId,
                        content = dto.content,
                        rating = dto.rating,
                        isOwner = dto.username == currentUser
                    )
                }.toList(), model.lastReviewId != 0.toLong())
            }
        }catch (e: Exception) {
            Log.e("GetReviewsResponseMapperApi", "Error mapping review: ${e.message}")
        }
        return Pair(emptyList(), false)
    }
}