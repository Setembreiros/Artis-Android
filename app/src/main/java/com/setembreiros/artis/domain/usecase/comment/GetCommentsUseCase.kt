package com.setembreiros.artis.domain.usecase.comment

import com.setembreiros.artis.data.repository.CommentRepository
import com.setembreiros.artis.domain.base.Resource
import com.setembreiros.artis.domain.model.Comment
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class GetCommentsUseCase @Inject constructor(private val commentRepository: CommentRepository)  {
    suspend fun invoke(postId: String, lastCommentId: Long) : Pair<List<Comment>,Boolean> = coroutineScope {
        getComments(postId, lastCommentId)
    }


    private suspend fun getComments(postId: String, lastCommentId: Long) : Pair<List<Comment>,Boolean> {
        return when(val response = commentRepository.getComments(postId, lastCommentId)){
            is Resource.Success -> {
                response.value
            }
            is Resource.Failure -> Pair(listOf(), false)
        }
    }
}