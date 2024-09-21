package com.setembreiros.artis.data.mapper.todomain

import com.setembreiros.artis.data.base.Mapper
import com.setembreiros.artis.data.model.post.GetUrlPostsResponseApi
import com.setembreiros.artis.domain.model.post.PostUrl

class GetUrlPostsResponseMapperApi: Mapper<GetUrlPostsResponseApi, Array<PostUrl>> {
    override fun map(model: GetUrlPostsResponseApi): Array<PostUrl> {
        return model.urlPosts.map { dto ->
            PostUrl(
                postId = dto.postId,
                url = dto.url,
            )
        }.toTypedArray()
    }
}