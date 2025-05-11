package com.setembreiros.artis.domain.usecase.like

import android.util.Log
import com.setembreiros.artis.data.repository.LikeRepository
import com.setembreiros.artis.domain.base.Resource
import javax.inject.Inject

class DeleteLikePostUseCase @Inject constructor(private val likeRepository: LikeRepository) {
    suspend fun invoke(username: String, postId: String): Boolean {
        val result = likeRepository.deleteLikePost(username, postId)
        return when(result){
            is Resource.Success -> true
            is Resource.Failure -> {
                Log.e("DeleteCommentUseCase", "Error deleting like, type: ${result.type}, message: ${result.message}")
                false
            }
        }
    }
}