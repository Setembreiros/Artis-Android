package com.setembreiros.artis.domain.usecase.like

import com.setembreiros.artis.data.repository.LikeRepository
import com.setembreiros.artis.domain.base.Resource
import com.setembreiros.artis.domain.model.Like
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class GetLikesUseCase @Inject constructor(private val likeRepository: LikeRepository)  {
    suspend fun invoke(postId: String, lastUsername: String) : Pair<List<Like>,Boolean> = coroutineScope {
        getLikes(postId, lastUsername)
    }

    private suspend fun getLikes(postId: String, lastUsername: String) : Pair<List<Like>,Boolean> {
        return when(val response = likeRepository.getPostLikes(postId, lastUsername)){
            is Resource.Success -> {
                response.value
            }
            is Resource.Failure -> Pair(listOf(), false)
        }
    }
}