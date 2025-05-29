package com.setembreiros.artis.domain.usecase.follow

import android.util.Log
import com.setembreiros.artis.data.repository.FollowRepository
import com.setembreiros.artis.domain.base.Resource
import javax.inject.Inject

class UnfollowUserUseCase @Inject constructor(private val followRepository: FollowRepository) {
    suspend fun invoke(followerId: String, followeeId: String): Boolean {
        return when(val result = followRepository.removeFollower(followerId, followeeId)){
            is Resource.Success -> {
                true
            }
            is Resource.Failure -> {
                Log.e("UnfollowUserUseCase", "Error removing follower, type: ${result.type}, message: ${result.message}")
                false
            }
        }
    }
}