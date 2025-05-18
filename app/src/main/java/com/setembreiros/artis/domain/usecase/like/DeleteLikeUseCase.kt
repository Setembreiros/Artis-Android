package com.setembreiros.artis.domain.usecase.like

import android.util.Log
import com.setembreiros.artis.data.repository.LikeRepository
import com.setembreiros.artis.data.repository.ProfileRepository
import com.setembreiros.artis.domain.base.Resource
import javax.inject.Inject

class DeleteLikePostUseCase @Inject constructor(private val likeRepository: LikeRepository, private val profileRepository: ProfileRepository) {
    suspend fun invoke(username: String, postId: String): Boolean {
        val result = likeRepository.deletePostLike(username, postId)
        return when(result){
            is Resource.Success -> {
                val post = profileRepository.getVisitPost(postId)
                post.metadata.likes -= 1
                post.metadata.isLikedByCurrentUser = false
                profileRepository.saveVisitPost(post)
                true
            }
            is Resource.Failure -> {
                Log.e("DeleteLikePostUseCase", "Error deleting like, type: ${result.type}, message: ${result.message}")
                false
            }
        }
    }
}