package com.setembreiros.artis.domain.usecase.superlike

import android.util.Log
import com.setembreiros.artis.data.repository.LikeRepository
import com.setembreiros.artis.domain.base.Resource
import javax.inject.Inject

class DeleteSuperlikePostUseCase @Inject constructor(private val likeRepository: LikeRepository) {
    suspend fun invoke(username: String, postId: String): Boolean {
        val result = likeRepository.deletePostSuperlike(username, postId)
        return when(result){
            is Resource.Success -> true
            is Resource.Failure -> {
                Log.e("DeleteSuperlikePostUseCase", "Error deleting superlike, type: ${result.type}, message: ${result.message}")
                false
            }
        }
    }
}