package com.setembreiros.artis.domain.usecase.comment

import android.util.Log
import com.setembreiros.artis.data.repository.CommentRepository
import com.setembreiros.artis.data.repository.ProfileRepository
import com.setembreiros.artis.domain.base.Resource
import javax.inject.Inject

class DeleteCommentUseCase @Inject constructor(private val commentRepository: CommentRepository, private val profileRepository: ProfileRepository) {
    suspend fun invoke(postId: String, commentId: Long): Boolean {
        val result = commentRepository.deleteComment(postId, commentId)
        return when(result){
            is Resource.Success -> {
                val post = profileRepository.getVisitPost(postId)
                post.metadata.comments -= 1
                profileRepository.saveVisitPost(post)
                true
            }
            is Resource.Failure -> {
                Log.e("DeleteCommentUseCase", "Error deleting comment, type: ${result.type}, message: ${result.message}")
                false
            }
        }
    }
}