package com.setembreiros.artis.domain.usecase.superlike

import android.util.Log
import com.setembreiros.artis.data.repository.LikeRepository
import com.setembreiros.artis.domain.base.Resource
import javax.inject.Inject

class AddSuperlikePostUseCase @Inject constructor(private val likeRepository: LikeRepository) {
    suspend fun invoke(username: String, postId: String): Boolean {
        return when(val result = likeRepository.createPostSuperlike(username, postId)){
            is Resource.Success -> true
            is Resource.Failure -> {
                Log.e("AddSuperlikePostUseCase", "Error adding superlike, type: ${result.type}, message: ${result.message}")
                false
            }
        }
    }
}