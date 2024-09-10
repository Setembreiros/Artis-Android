package com.setembreiros.artis.data.repository

import com.setembreiros.artis.data.ApiClient
import com.setembreiros.artis.data.base.BaseApiClient
import com.setembreiros.artis.data.mapper.fromdomain.ConfirmPostRequestMapper
import com.setembreiros.artis.data.mapper.fromdomain.PostMapper
import com.setembreiros.artis.data.mapper.todomain.GenericBoolMapperApi
import com.setembreiros.artis.data.mapper.todomain.PostResponseMapperApi
import com.setembreiros.artis.domain.model.post.ConfirmPostRequest
import com.setembreiros.artis.domain.model.post.Post
import com.setembreiros.artis.domain.usecase.session.GetSessionUseCase
import javax.inject.Inject

class PostRepository @Inject constructor(private val apiClient: ApiClient, private val getSessionUseCase: GetSessionUseCase): BaseApiClient() {

    private fun getToken() = "Bearer " +getSessionUseCase.invoke()!!.idToken

    suspend fun createPost(post: Post) = safeApiCall(PostResponseMapperApi()){
        apiClient.createPost( getToken() ,PostMapper().map(post))
    }

    suspend fun confirmPost(confirmPostRequest: ConfirmPostRequest) = safeApiCall(GenericBoolMapperApi()){
        apiClient.confirmPost(getToken(), ConfirmPostRequestMapper().map(confirmPostRequest))
    }
}