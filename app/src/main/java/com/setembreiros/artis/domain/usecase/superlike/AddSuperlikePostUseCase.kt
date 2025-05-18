package com.setembreiros.artis.domain.usecase.superlike

import android.util.Log
import com.setembreiros.artis.data.repository.LikeRepository
import com.setembreiros.artis.data.repository.ProfileRepository
import com.setembreiros.artis.domain.base.Resource
import javax.inject.Inject

class AddSuperlikePostUseCase @Inject constructor(private val likeRepository: LikeRepository, private val profileRepository: ProfileRepository) {
    suspend fun invoke(username: String, postId: String): Boolean {
        return when(val result = likeRepository.createPostSuperlike(username, postId)){
            is Resource.Success -> {
                val post = profileRepository.getVisitPost(postId)
                post.metadata.superlikes += 1
                post.metadata.isSuperlikedByCurrentUser = true
                profileRepository.saveVisitPost(post)
                true
            }
            is Resource.Failure -> {
                Log.e("AddSuperlikePostUseCase", "Error adding superlike, type: ${result.type}, message: ${result.message}")
                false
            }
        }
    }
}