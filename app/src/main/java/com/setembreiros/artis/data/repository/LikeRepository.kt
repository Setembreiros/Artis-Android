package com.setembreiros.artis.data.repository

import com.setembreiros.artis.data.ApiClient
import com.setembreiros.artis.data.base.BaseApiClient
import com.setembreiros.artis.data.mapper.todomain.GetPostLikesResponseMapperApi
import com.setembreiros.artis.data.model.like.CreateLikePostRequestApi
import com.setembreiros.artis.domain.usecase.session.GetSessionUseCase
import javax.inject.Inject

class LikeRepository @Inject constructor(private val apiClient: ApiClient, private val getSessionUseCase: GetSessionUseCase): BaseApiClient() {
    private fun getToken() = "Bearer " + getSessionUseCase.invoke()!!.idToken

    suspend fun createPostLike(username: String, postId: String) = safeApiCall{
        apiClient.createPostLike(getToken(), CreateLikePostRequestApi(username, postId))
    }

    suspend fun getPostLikes(postId: String, lastUsername: String) = safeApiCall(
        GetPostLikesResponseMapperApi()
    ){
        apiClient.getPostLikes(getToken(), postId, 12, lastUsername)
    }

    suspend fun deletePostLike(username: String, postId: String) = safeApiCall{
        apiClient.deletePostLike(getToken(), postId, username)
    }
}