package com.setembreiros.artis.data.model.post

import com.setembreiros.artis.common.Constants.ContentType

data class GetPostMetadatasResponseApi(val posts: Array<GetPostMetadataDto>)

data class GetPostMetadataDto(
    val post_id: String,
    val username: String,
    val type: ContentType,
    val file_type: String,
    val title: String,
    val description: String,
    val created_at: String,
    val last_updated: String
)