package com.setembreiros.artis.data.model.post

data class GetUrlPostsResponseApi(val urlPosts: Array<GetUrlPostsDto>)

data class GetUrlPostsDto(val postId: String, val url: String, val thumbnailUrl: String)
