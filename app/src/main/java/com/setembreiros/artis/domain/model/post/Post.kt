package com.setembreiros.artis.domain.model.post

import android.net.Uri
import com.setembreiros.artis.common.Constants.ContentType

data class Post(val metadata : PostMetadata, var content: PostContent?)

data class PostMetadata(
    val postId : String,
    val username: String,
    val type: ContentType,
    val title: String,
    val description: String,
    val size: Long,
    val comments: Long,
    val createdAt: String,
    val lastUpdated: String
)

data class PostContent(
    var uriContent: Uri?,
    var content: ByteArray?,
    var thumbnail:  String?
)