package com.setembreiros.artis.data.mapper.todomain

import com.setembreiros.artis.data.base.Mapper
import com.setembreiros.artis.data.model.post.GetPostMetadatasResponseApi
import com.setembreiros.artis.domain.model.post.PostMetadata

class GetPostMetadatasResponseMapperApi: Mapper<GetPostMetadatasResponseApi, Array<PostMetadata>> {
    override fun map(model: GetPostMetadatasResponseApi): Array<PostMetadata> {
        return model.posts.map { dto ->
            PostMetadata(
                postId = dto.post_id,
                username = dto.username,
                type = dto.type,
                fileType = dto.file_type,
                title = dto.title,
                description = dto.description,
                createdAt = dto.created_at,
                lastUpdated = dto.last_updated
            )
        }.toTypedArray()
    }
}