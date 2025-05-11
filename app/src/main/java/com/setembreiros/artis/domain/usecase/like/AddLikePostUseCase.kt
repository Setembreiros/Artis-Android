package com.setembreiros.artis.domain.usecase.like

import android.util.Log
import com.setembreiros.artis.data.repository.LikeRepository
import com.setembreiros.artis.domain.base.Resource
import javax.inject.Inject

class AddLikePostUseCase @Inject constructor(private val likeRepository: LikeRepository) {
    suspend fun invoke(username: String, postId: String): Boolean {
        return when(val result = likeRepository.createPostLike(username, postId)){
            is Resource.Success -> true
            is Resource.Failure -> {
                Log.e("DeleteCommentUseCase", "Error adding like, type: ${result.type}, message: ${result.message}")
                false
            }
        }
    }
}