package com.setembreiros.artis.domain.usecase.follow

import com.setembreiros.artis.data.repository.FollowRepository
import com.setembreiros.artis.domain.base.Resource
import com.setembreiros.artis.domain.model.UserProfileSnippet
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class GetFollowersUseCase @Inject constructor(private val followRepository: FollowRepository)  {
    suspend fun invoke(username: String, lastUsername: String) : Pair<List<UserProfileSnippet>,Boolean> = coroutineScope {
        getFollowers(username, lastUsername)
    }

    private suspend fun getFollowers(username: String, lastUsername: String) : Pair<List<UserProfileSnippet>,Boolean> {
        return when(val response = followRepository.getFollowers(username, lastUsername)){
            is Resource.Success -> {
                response.value
            }
            is Resource.Failure -> Pair(listOf(), false)
        }
    }
}