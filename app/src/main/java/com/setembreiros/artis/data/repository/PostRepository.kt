package com.setembreiros.artis.data.repository

import com.setembreiros.artis.data.ApiClient
import com.setembreiros.artis.data.base.BaseApiClient
import com.setembreiros.artis.data.mapper.fromdomain.PostMapper
import com.setembreiros.artis.data.mapper.todomain.PostResponseMapperApi
import com.setembreiros.artis.domain.model.post.Post
import javax.inject.Inject

class PostRepository @Inject constructor(private val apiClient: ApiClient): BaseApiClient() {

    suspend fun createPost(post: Post) = safeApiCall(PostResponseMapperApi()){
        apiClient.createPost(PostMapper().map(post))
    }
}