package com.setembreiros.artis.data.mapper.fromdomain

import com.setembreiros.artis.data.base.Mapper
import com.setembreiros.artis.data.model.post.CreatePostRequestApi
import com.setembreiros.artis.domain.model.post.Post

class PostMapper: Mapper<Post, CreatePostRequestApi> {
    override fun map(model: Post): CreatePostRequestApi {
        return CreatePostRequestApi(username = model.metadata.username, title = model.metadata.title, description = model.metadata.description, type = model.metadata.type, hasThumbnail = model.thumbnail != null)
    }
}