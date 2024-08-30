package com.setembreiros.artis.domain.usecase.post

import android.util.Log
import com.setembreiros.artis.BuildConfig
import com.setembreiros.artis.data.repository.PostRepository
import com.setembreiros.artis.data.service.S3Service
import com.setembreiros.artis.domain.base.Resource
import com.setembreiros.artis.domain.model.post.Post
import com.setembreiros.artis.domain.model.post.PostResponse
import javax.inject.Inject

class CreatePostUseCase @Inject constructor(private val postRepository: PostRepository, private val s3Service: S3Service) {

    suspend fun invoke(post: Post, content: ByteArray) : Boolean{
       return createMetaData(post, content)
    }

    private suspend fun createMetaData(post: Post, content: ByteArray) : Boolean{
        return when(val response = postRepository.createPost(post)){
            is Resource.Success -> {
                sendContentS3(content, response.value)
            }
            is Resource.Failure -> return false
        }
    }

    private suspend fun sendContentS3(content: ByteArray, metadata: PostResponse) : Boolean{
        var url = metadata.presignedUrl
        if(BuildConfig.DEBUG)
            url = getUrlDebug(metadata.presignedUrl)
        return s3Service.putContent(url, content, "PUT")
    }

    private fun getUrlDebug(url: String) : String{
        val aux = url.split("4566")

        return BuildConfig.S3_URL +"/artis-bucket" + aux[1]
    }
}