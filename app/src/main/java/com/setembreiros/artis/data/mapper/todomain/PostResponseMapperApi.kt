package com.setembreiros.artis.data.mapper.todomain

import com.setembreiros.artis.data.base.Mapper
import com.setembreiros.artis.data.model.post.PostResponseApi
import com.setembreiros.artis.domain.model.post.PostResponse

class PostResponseMapperApi: Mapper<PostResponseApi, PostResponse> {
    override fun map(model: PostResponseApi): PostResponse {
        return PostResponse(postId = model.post_id, presignedUrl = model.presigned_url)
    }
}