package com.setembreiros.artis.domain.usecase.post

import com.setembreiros.artis.BuildConfig
import com.setembreiros.artis.data.repository.PostRepository
import com.setembreiros.artis.data.service.S3Service
import com.setembreiros.artis.domain.base.Resource
import com.setembreiros.artis.domain.model.post.Post
import com.setembreiros.artis.domain.model.post.PostMetadata
import com.setembreiros.artis.domain.model.post.PostUrl
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class GetPostsUseCase @Inject constructor(private val postRepository: PostRepository, private val s3Service: S3Service)  {
    suspend fun invoke(username: String) : Array<Post> = coroutineScope {
        val postMetadatasDeferred = async { getMetaData(username) }
        val contentsDeferred = async { getContent(username) }

        val postMetadatas = postMetadatasDeferred.await()
        val contents = contentsDeferred.await()

        val posts = ArrayList<Post>()
        for (postMetadata in postMetadatas) {
            val matchingContent = contents.find { it.first == postMetadata.postId }?.second
            val post = Post(postMetadata, matchingContent)
            println("Post: ${post.metadata.postId}, Content: ${post.content}")
            posts.add(post)
        }

        posts.toTypedArray()
    }

    private suspend fun getMetaData(username: String) : Array<PostMetadata> {
        return when(val response = postRepository.getPostMetadatas(username)){
            is Resource.Success -> {
                response.value
            }
            is Resource.Failure -> arrayOf()
        }
    }

    private suspend fun getContent(username: String) : List<Pair<String,ByteArray?>> {
        val postUrls = getUrls(username)
        if (postUrls.isNotEmpty())
            return getMultimediaContent(postUrls)

        return listOf()
    }

    private suspend fun getUrls(username: String) : Array<PostUrl>{
        return when(val response = postRepository.getUrlPosts(username)){
            is Resource.Success -> {
                response.value
            }
            is Resource.Failure -> return arrayOf()
        }
    }

    private suspend fun getMultimediaContent(postUrls: Array<PostUrl>): List<Pair<String,ByteArray?>> = coroutineScope {
        val deferredResponses = postUrls.map { postUrl ->
            async {
                var url = postUrl.url
                if(BuildConfig.DEBUG)
                    url = getUrlDebug(postUrl.url)

                val multimediaContent = s3Service.getContent(url)
                Pair(postUrl.postId, multimediaContent)
            }
        }

        deferredResponses.awaitAll()
    }

    private fun getUrlDebug(url: String) : String{
        val aux = url.split("4566")

        return BuildConfig.S3_URL +"/artis-bucket" + aux[1]
    }
}