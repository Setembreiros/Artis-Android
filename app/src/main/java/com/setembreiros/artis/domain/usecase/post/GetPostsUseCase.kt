package com.setembreiros.artis.domain.usecase.post

import android.net.Uri
import com.setembreiros.artis.BuildConfig
import com.setembreiros.artis.common.Constants
import com.setembreiros.artis.data.repository.PostRepository
import com.setembreiros.artis.data.repository.ProfileRepository
import com.setembreiros.artis.data.service.S3Service
import com.setembreiros.artis.domain.base.Resource
import com.setembreiros.artis.domain.model.post.Post
import com.setembreiros.artis.domain.model.post.PostContent
import com.setembreiros.artis.domain.model.post.PostMetadata
import com.setembreiros.artis.domain.model.post.PostUrl
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class GetPostsUseCase @Inject constructor(private val postRepository: PostRepository,
                                          private val profileRepository: ProfileRepository,
                                          private val s3Service: S3Service)  {
    suspend fun invoke(username: String, currentUsername: String, lastPostId: String, lastPostCreatedAt: String) : Pair<Array<Post>,Boolean> = coroutineScope {
        val postMetadatasDeferred = async { getMetaData(username, currentUsername, lastPostId, lastPostCreatedAt) }
        val postUrlsDeferred = async { getUrls(username, lastPostId, lastPostCreatedAt) }

        val postMetadatas = postMetadatasDeferred.await()
        val postUrls = postUrlsDeferred.await()

        val posts = ArrayList<Post>()
        for (postMetadata in postMetadatas.first) {
            val matchingContent = postUrls.find { it.postId == postMetadata.postId }?.let {
                getMultimediaContent(it, postMetadata.type)
            }
            val post = Post(postMetadata, matchingContent)
            posts.add(post)
        }

        Pair(posts.toTypedArray(), postMetadatas.second)
    }

    private suspend fun getMetaData(username: String, currentUsername: String, lastPostId: String, lastPostCreatedAt: String) : Pair<Array<PostMetadata>, Boolean> {
        return when(val response = postRepository.getPostMetadatas(username, currentUsername, lastPostId, lastPostCreatedAt)){
            is Resource.Success -> {
                response.value
            }
            is Resource.Failure -> Pair(arrayOf(), false)
        }
    }

    private suspend fun getUrls(username: String, lastPostId: String, lastPostCreatedAt: String) : Array<PostUrl>{
        return when(val response = postRepository.getUrlPosts(username, lastPostId, lastPostCreatedAt)){
            is Resource.Success -> {
                response.value
            }
            is Resource.Failure -> return arrayOf()
        }
    }

    private suspend fun getMultimediaContent(postUrl: PostUrl, postType: Constants.ContentType): PostContent = coroutineScope {
        var url = postUrl.url
        var thumbnailUrl = postUrl.thumbnailUrl
        var content: ByteArray? = null
        if(BuildConfig.DEBUG) {
            url = getUrlDebug(postUrl.url)
            if(thumbnailUrl != "") {
                thumbnailUrl = getUrlDebug(postUrl.thumbnailUrl)
            }
        }

        if (postType == Constants.ContentType.TEXT) {
            content = s3Service.getContent(url)
        }

        PostContent(Uri.parse(url), content, thumbnailUrl)
    }

    private fun getUrlDebug(url: String) : String{
        val aux = url.split("4566")

        return BuildConfig.S3_URL +"/artis-bucket" + aux[1]
    }
}