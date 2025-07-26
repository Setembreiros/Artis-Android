package com.setembreiros.artis.domain.usecase.review

import android.util.Log
import com.setembreiros.artis.data.repository.ReviewRepository
import com.setembreiros.artis.data.repository.ProfileRepository
import com.setembreiros.artis.domain.base.Resource
import javax.inject.Inject

class DeleteReviewUseCase @Inject constructor(private val reviewRepository: ReviewRepository, private val profileRepository: ProfileRepository) {
    suspend fun invoke(postId: String, reviewId: Long): Boolean {
        val result = reviewRepository.deleteReview(postId, reviewId)
        return when(result){
            is Resource.Success -> {
                val post = profileRepository.getVisitPost(postId)
                post.metadata.reviews -= 1
                profileRepository.saveVisitPost(post)
                true
            }
            is Resource.Failure -> {
                Log.e("DeleteReviewUseCase", "Error deleting review, type: ${result.type}, message: ${result.message}")
                false
            }
        }
    }
}