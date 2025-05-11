package com.setembreiros.artis.data.repository

import com.setembreiros.artis.data.ApiClient
import com.setembreiros.artis.data.base.BaseApiClient
import com.setembreiros.artis.data.mapper.fromdomain.CreateCommentRequestMapper
import com.setembreiros.artis.data.mapper.todomain.GetCommentsResponseMapperApi
import com.setembreiros.artis.domain.model.Comment
import com.setembreiros.artis.domain.usecase.session.GetSessionUseCase
import javax.inject.Inject

class CommentRepository @Inject constructor(private val apiClient: ApiClient, private val getSessionUseCase: GetSessionUseCase): BaseApiClient() {
    private fun getToken() = "Bearer " + getSessionUseCase.invoke()!!.idToken

    suspend fun createComment(comment: Comment) = safeApiCall{
        apiClient.createComment(getToken(), CreateCommentRequestMapper().map(comment))
    }

   suspend fun getComments(postId: String, lastCommentId: Long) = safeApiCall(GetCommentsResponseMapperApi()
   ){
        apiClient.getComments(getToken(), postId, 12, lastCommentId)
    }
}