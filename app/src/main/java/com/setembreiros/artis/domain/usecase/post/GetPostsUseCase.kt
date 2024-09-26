package com.setembreiros.artis.domain.usecase.post

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.media.MediaMetadataRetriever
import android.os.ParcelFileDescriptor
import com.setembreiros.artis.BuildConfig
import com.setembreiros.artis.common.Constants
import com.setembreiros.artis.data.repository.PostRepository
import com.setembreiros.artis.data.service.S3Service
import com.setembreiros.artis.domain.base.Resource
import com.setembreiros.artis.domain.model.post.Post
import com.setembreiros.artis.domain.model.post.PostMetadata
import com.setembreiros.artis.domain.model.post.PostUrl
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class GetPostsUseCase @Inject constructor(private val postRepository: PostRepository, private val s3Service: S3Service)  {
    suspend fun invoke(username: String) : Array<Post> = coroutineScope {
        val postMetadatasDeferred = async { getMetaData(username) }
        val contentsDeferred = async { getContent(username) }

        val postMetadatas = postMetadatasDeferred.await()
        val contents = contentsDeferred.await()

        val posts = ArrayList<Post>()
        for (postMetadata in postMetadatas) {
            val matchingContent = contents.find { it.first == postMetadata.postId }?.let {
                Pair(it.second, it.third)
            }
            val post = Post(postMetadata, matchingContent!!.first, matchingContent.second)
            ensureThumbnailContent(post)
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

    private suspend fun getContent(username: String) : List<Triple<String,ByteArray, ByteArray?>> {
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

    private suspend fun getMultimediaContent(postUrls: Array<PostUrl>): List<Triple<String,ByteArray,ByteArray?>> = coroutineScope {
        val deferredResponses = postUrls.map { postUrl ->
            async {
                var url = postUrl.url
                var thumbnailUrl = postUrl.thumbnailUrl
                var thumbnailContent: ByteArray? = null
                if(BuildConfig.DEBUG) {
                    url = getUrlDebug(postUrl.url)
                    if(thumbnailUrl != "") {
                        thumbnailUrl = getUrlDebug(postUrl.thumbnailUrl)
                    }
                }

                val multimediaContent = s3Service.getContent(url)
                if(thumbnailUrl != "")
                    thumbnailContent = s3Service.getContent(thumbnailUrl)

                Triple(postUrl.postId, multimediaContent, thumbnailContent)
            }
        }

        deferredResponses.awaitAll()
    }

    private fun ensureThumbnailContent(post: Post) {
        if(post.thumbnail!!.isEmpty()) {
            when (post.metadata.type) {
                Constants.ContentType.IMAGE -> {
                    post.thumbnail = post.content
                }
                Constants.ContentType.TEXT -> {
                    var tempFile: File? = null
                    try {
                        tempFile = File.createTempFile("temp_pdf", post.metadata.fileType)
                        val fos = FileOutputStream(tempFile)
                        fos.write(post.content)
                        fos.close()

                        val fileDescriptor: ParcelFileDescriptor = ParcelFileDescriptor.open(tempFile, ParcelFileDescriptor.MODE_READ_ONLY)
                        val pdfRenderer = PdfRenderer(fileDescriptor)

                        val page = pdfRenderer.openPage(0)

                        val bitmap = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)

                        // Render the page onto the bitmap
                        page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                        page.close()
                        pdfRenderer.close()
                        fileDescriptor.close()

                        val byteArrayOutputStream = ByteArrayOutputStream()
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream) // Compress the bitmap to PNG
                        post.thumbnail = byteArrayOutputStream.toByteArray()
                        byteArrayOutputStream.close()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    } finally {
                        tempFile?.delete()
                    }
                }
                Constants.ContentType.AUDIO -> return
                Constants.ContentType.VIDEO -> {
                    val retriever = MediaMetadataRetriever()
                    var tempFile: File? = null
                    try {
                        tempFile = File.createTempFile("temp_video", post.metadata.fileType)
                        val fos = FileOutputStream(tempFile)
                        fos.write(post.content)
                        fos.close()

                        retriever.setDataSource(tempFile!!.absolutePath)

                        val bitmap = retriever.getFrameAtTime(1 * 1000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
                        val byteArrayOutputStream = ByteArrayOutputStream()
                        bitmap!!.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream) // Compress the bitmap to PNG
                        post.thumbnail = byteArrayOutputStream.toByteArray()
                        byteArrayOutputStream.close()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    finally {
                        // Release the retriever and delete the temp file
                        retriever.release()
                        tempFile?.delete()
                    }
                }
            }
        }
    }

    private fun getUrlDebug(url: String) : String{
        val aux = url.split("4566")

        return BuildConfig.S3_URL +"/artis-bucket" + aux[1]
    }
}