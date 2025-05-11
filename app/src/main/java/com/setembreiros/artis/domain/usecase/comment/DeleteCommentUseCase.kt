package com.setembreiros.artis.domain.usecase.comment

import android.util.Log
import com.setembreiros.artis.data.repository.CommentRepository
import com.setembreiros.artis.domain.base.Resource
import javax.inject.Inject

class DeleteCommentUseCase @Inject constructor(private val commentRepository: CommentRepository) {
    suspend fun invoke(postId: String, commentId: Long): Boolean {
        val result = commentRepository.deleteComment(postId, commentId)
        return when(result){
            is Resource.Success -> true
            is Resource.Failure -> {
                Log.e("DeleteCommentUseCase", "Error deleting comment, type: ${result.type}, message: ${result.message}")
                false
            }
        }
    }
}