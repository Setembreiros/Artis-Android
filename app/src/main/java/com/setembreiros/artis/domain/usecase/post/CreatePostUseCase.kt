package com.setembreiros.artis.domain.usecase.post

import android.content.Context
import android.net.Uri
import com.setembreiros.artis.BuildConfig
import com.setembreiros.artis.domain.model.post.CompletedPart
import com.setembreiros.artis.data.repository.PostRepository
import com.setembreiros.artis.data.service.S3Service
import com.setembreiros.artis.domain.base.Resource
import com.setembreiros.artis.domain.builder.ThumbnailBuilder
import com.setembreiros.artis.domain.model.post.ConfirmPostRequest
import com.setembreiros.artis.domain.model.post.Post
import com.setembreiros.artis.domain.model.post.PostResponse
import javax.inject.Inject

class CreatePostUseCase @Inject constructor(private val postRepository: PostRepository, private val s3Service: S3Service) {

    suspend fun invoke(post: Post, context: Context) : Boolean{
       return createMetaData(post, context)
    }

    private suspend fun createMetaData(post: Post, context: Context) : Boolean {
        return when(val response = postRepository.createPost(post)){
            is Resource.Success -> {
                val responseS3 = sendContentS3(post.content?.uriContent, ThumbnailBuilder.createThumbnail(context, post.content?.uriContent, post.metadata.type), response.value,context)
                if(responseS3.second)
                    if(response.value.presignedUrls.size > 1) {
                        confirmPost(true, response.value.postId, true, response.value.uploadId, responseS3.first)
                        return true
                    } else {
                        confirmPost(true, response.value.postId, false, response.value.uploadId, null)
                        return true
                    }
                else {
                    confirmPost(false, response.value.postId, false, response.value.uploadId, null)
                }
                false
            }
            is Resource.Failure -> return false
        }
    }

    private suspend fun sendContentS3(uriContent: Uri?, thumbnailContent: ByteArray?, metadata: PostResponse, context: Context) : Pair<List<CompletedPart>?, Boolean>{
        var urls = metadata.presignedUrls
        var thumbnailUrl = metadata.presignedThumbnailUrl
        if(BuildConfig.DEBUG) {
             url = getUrlDebug(metadata.presignedUrl)
             if(thumbnailUrl != "") {
                 thumbnailUrl = getUrlDebug(metadata.presignedThumbnailUrl)
             }
         }

        if(thumbnailUrl != "" && thumbnailContent != null) {
           s3Service.putContent(thumbnailUrl, thumbnailContent)
        }
        return s3Service.putContent(urls, uriContent, context)
    }

    private suspend fun confirmPost(isConfirmed: Boolean, postId: String, isMultipart: Boolean, uploadId: String, completedParts: List<CompletedPart>?): Boolean{
        val confirmPostRequest = ConfirmPostRequest(isConfirmed, postId, isMultipart, uploadId, completedParts)
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