package com.setembreiros.artis.data.repository

import com.setembreiros.artis.data.ApiClient
import com.setembreiros.artis.data.base.BaseApiClient
import com.setembreiros.artis.data.mapper.fromdomain.CreateReviewRequestMapper
import com.setembreiros.artis.data.mapper.todomain.GetReviewsResponseMapperApi
import com.setembreiros.artis.domain.model.Review
import com.setembreiros.artis.domain.usecase.session.GetSessionUseCase
import javax.inject.Inject

class ReviewRepository @Inject constructor(private val apiClient: ApiClient, private val getSessionUseCase: GetSessionUseCase): BaseApiClient() {
    private fun getToken() = "Bearer " + getSessionUseCase.invoke()!!.idToken

    suspend fun createReview(review: Review) = safeApiCall{
        apiClient.createReview(getToken(), CreateReviewRequestMapper().map(review))
    }

   suspend fun getReviews(postId: String, lastReviewId: Long) = safeApiCall(GetReviewsResponseMapperApi(getSessionUseCase)
   ){
        apiClient.getReviews(getToken(), postId, 12, lastReviewId)
    }

    suspend fun deleteReview(postId: String, reviewId: Long) = safeApiCall{
        apiClient.deleteReview(getToken(), postId, reviewId)
    }
}