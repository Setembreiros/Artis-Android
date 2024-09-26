package com.setembreiros.artis.domain.usecase.post

import com.setembreiros.artis.BuildConfig
import com.setembreiros.artis.data.repository.PostRepository
import com.setembreiros.artis.data.service.S3Service
import com.setembreiros.artis.domain.base.Resource
import com.setembreiros.artis.domain.model.post.ConfirmPostRequest
import com.setembreiros.artis.domain.model.post.Post
import com.setembreiros.artis.domain.model.post.PostResponse
import javax.inject.Inject

class CreatePostUseCase @Inject constructor(private val postRepository: PostRepository, private val s3Service: S3Service) {

    suspend fun invoke(post: Post, content: ByteArray) : Boolean{
       return createMetaData(post, content)
    }

    private suspend fun createMetaData(post: Post, content: ByteArray) : Boolean {
        return when(val response = postRepository.createPost(post)){
            is Resource.Success -> {
                val responseS3 = sendContentS3(content, response.value)
                if(responseS3)
                    confirmPost(true, response.value.postId)
                else {
                    confirmPost(false, response.value.postId)
                    false
                }
            }
            is Resource.Failure -> return false
        }
    }

    private suspend fun sendContentS3(content: ByteArray, metadata: PostResponse) : Boolean{
        var url = metadata.presignedUrl
        if(BuildConfig.DEBUG)
            url = getUrlDebug(metadata.presignedUrl)
        return s3Service.putContent(url, content)
    }

    private suspend fun confirmPost(isConfirmed: Boolean, posId: String): Boolean{
        val confirmPostRequest = ConfirmPostRequest(isConfirmed, posId)
        return when(postRepository.confirmPost(confirmPostRequest)){
            is Resource.Success -> true
            is Resource.Failure -> false
        }

    }

    private fun getUrlDebug(url: String) : String{
        val aux = url.split("4566")

        return BuildConfig.S3_URL +"/artis-bucket" + aux[1]
    }
}