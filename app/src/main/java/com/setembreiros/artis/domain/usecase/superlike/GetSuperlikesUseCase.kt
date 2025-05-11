package com.setembreiros.artis.domain.usecase.superlike

import com.setembreiros.artis.data.repository.LikeRepository
import com.setembreiros.artis.domain.base.Resource
import com.setembreiros.artis.domain.model.Superlike
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class GetSuperlikesUseCase @Inject constructor(private val likeRepository: LikeRepository)  {
    suspend fun invoke(postId: String, lastUsername: String) : Pair<List<Superlike>,Boolean> = coroutineScope {
        getSuperlikes(postId, lastUsername)
    }

    private suspend fun getSuperlikes(postId: String, lastUsername: String) : Pair<List<Superlike>,Boolean> {
        return when(val response = likeRepository.getPostSuperlikes(postId, lastUsername)){
            is Resource.Success -> {
                response.value
            }
            is Resource.Failure -> Pair(listOf(), false)
        }
    }
}