package com.setembreiros.artis.data.repository

import com.setembreiros.artis.data.ApiClient
import com.setembreiros.artis.data.base.BaseApiClient
import com.setembreiros.artis.data.model.like.CreateLikePostRequestApi
import com.setembreiros.artis.domain.usecase.session.GetSessionUseCase
import javax.inject.Inject

class LikeRepository @Inject constructor(private val apiClient: ApiClient, private val getSessionUseCase: GetSessionUseCase): BaseApiClient() {
    private fun getToken() = "Bearer " + getSessionUseCase.invoke()!!.idToken

    suspend fun createLikePost(username: String, postId: String) = safeApiCall{
        apiClient.createLikePost(getToken(), CreateLikePostRequestApi(username, postId))
    }

    suspend fun deleteLikePost(username: String, postId: String) = safeApiCall{
        apiClient.deleteLikePost(getToken(), postId, username)
    }
}