package com.setembreiros.artis.data.mapper.todomain

import com.setembreiros.artis.data.base.Mapper
import com.setembreiros.artis.data.model.post.GetPostMetadatasResponseApi
import com.setembreiros.artis.domain.model.post.PostMetadata

class GetPostMetadatasResponseMapperApi: Mapper<GetPostMetadatasResponseApi, Pair<Array<PostMetadata>, Boolean>> {
    override fun map(model: GetPostMetadatasResponseApi): Pair<Array<PostMetadata>, Boolean> {
        return Pair(model.posts.map { dto ->
            PostMetadata(
                postId = dto.post_id,
                username = dto.username,
                type = dto.type,
                title = dto.title,
                description = dto.description,
                createdAt = dto.created_at,
                lastUpdated = dto.last_updated
            )
        }.toTypedArray(), model.lastPostId != "")
    }
}