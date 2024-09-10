package com.setembreiros.artis.data.mapper.fromdomain

import com.setembreiros.artis.data.base.Mapper
import com.setembreiros.artis.data.model.post.PostApi
import com.setembreiros.artis.domain.model.post.Post

class PostMapper: Mapper<Post, PostApi> {
    override fun map(model: Post): PostApi {
        return PostApi(username = model.username, title = model.title, description = model.description, file_Type = model.fileType, type = model.type)
    }
}