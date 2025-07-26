package com.setembreiros.artis.domain.usecase.review

import com.setembreiros.artis.data.repository.ReviewRepository
import com.setembreiros.artis.data.repository.ProfileRepository
import com.setembreiros.artis.domain.base.Resource
import com.setembreiros.artis.domain.model.Review
import javax.inject.Inject

class AddReviewUseCase @Inject constructor(private val reviewRepository: ReviewRepository, private val profileRepository: ProfileRepository) {
    suspend fun invoke(username: String, postId: String, title: String, content: String, rating: Int) : Review? {
        val review = Review(0, username, postId, title, content, rating)
        return when(reviewRepository.createReview(review)){
            is Resource.Success -> {
                val post = profileRepository.getVisitPost(postId)
                post.metadata.reviews += 1
                profileRepository.saveVisitPost(post)
                review
            }
            is Resource.Failure -> null
        }
    }
}