package com.setembreiros.artis.data.mapper.todomain

import com.setembreiros.artis.data.base.Mapper
import com.setembreiros.artis.data.model.comment.GetCommentsResponseApi
import com.setembreiros.artis.domain.model.Comment

class GetCommentsResponseMapperApi: Mapper<GetCommentsResponseApi, Pair<List<Comment>, Boolean>> {
    override fun map(model: GetCommentsResponseApi): Pair<List<Comment>, Boolean> {
        return Pair(model.comments.map { dto ->
            Comment(
                commentId = dto.commentId,
                username = dto.username,
                postId = dto.postId,
                content = dto.content,
            )
        }.toList(), model.lastCommentId != 0.toLong())
    }
}