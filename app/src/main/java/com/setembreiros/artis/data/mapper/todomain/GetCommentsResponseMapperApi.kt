package com.setembreiros.artis.data.mapper.todomain

import android.util.Log
import com.setembreiros.artis.data.base.Mapper
import com.setembreiros.artis.data.model.comment.GetCommentsResponseApi
import com.setembreiros.artis.domain.model.Comment
import com.setembreiros.artis.domain.usecase.session.GetSessionUseCase
import javax.inject.Inject

class GetCommentsResponseMapperApi @Inject constructor(private val getSessionUseCase: GetSessionUseCase): Mapper<GetCommentsResponseApi, Pair<List<Comment>, Boolean>> {
    override fun map(model: GetCommentsResponseApi): Pair<List<Comment>, Boolean> {
        try {
            getSessionUseCase.invoke()?.username?.let { currentUser ->
                return Pair(model.comments.map { dto ->
                    Comment(
                        commentId = dto.commentId,
                        username = dto.username,
                        postId = dto.postId,
                        content = dto.content,
                        isOwner = dto.username == currentUser
                    )
                }.toList(), model.lastCommentId != 0.toLong())
            }
        }catch (e: Exception) {
            Log.e("GetCommentsResponseMapperApi", "Error mapping comment: ${e.message}")
        }
        return Pair(emptyList(), false)
    }
}