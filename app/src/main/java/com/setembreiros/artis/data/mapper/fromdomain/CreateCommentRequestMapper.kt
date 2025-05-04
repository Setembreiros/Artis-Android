package com.setembreiros.artis.data.mapper.fromdomain

import com.setembreiros.artis.data.base.Mapper
import com.setembreiros.artis.data.model.comment.CreateCommentRequestApi
import com.setembreiros.artis.domain.model.Comment

class CreateCommentRequestMapper: Mapper<Comment, CreateCommentRequestApi> {
    override fun map(model: Comment): CreateCommentRequestApi {
        return CreateCommentRequestApi(username = model.username, postId = model.postId, content = model.content)
    }
}