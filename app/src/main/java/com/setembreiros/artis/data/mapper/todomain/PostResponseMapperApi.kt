package com.setembreiros.artis.data.mapper.todomain

import com.setembreiros.artis.data.base.Mapper
import com.setembreiros.artis.data.model.post.CreatePostResponseApi
import com.setembreiros.artis.domain.model.post.PostResponse

class PostResponseMapperApi: Mapper<CreatePostResponseApi, PostResponse> {
    override fun map(model: CreatePostResponseApi): PostResponse {
        return PostResponse(postId = model.post_id, presignedUrl = model.presigned_url)
    }
}