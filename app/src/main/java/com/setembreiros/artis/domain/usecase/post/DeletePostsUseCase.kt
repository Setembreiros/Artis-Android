package com.setembreiros.artis.domain.usecase.post

import com.setembreiros.artis.data.repository.PostRepository
import com.setembreiros.artis.data.repository.ProfileRepository
import com.setembreiros.artis.domain.base.Resource
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class DeletePostsUseCase @Inject constructor(private val postRepository: PostRepository, private val profileRepository: ProfileRepository)  {
    suspend fun invoke(postId: String) : Boolean = coroutineScope {
        return@coroutineScope when(postRepository.deletePost(postId)){
            is Resource.Success -> {
                profileRepository.removePost(postId)
                true
            }

            is Resource.Failure -> false
        }
    }
}