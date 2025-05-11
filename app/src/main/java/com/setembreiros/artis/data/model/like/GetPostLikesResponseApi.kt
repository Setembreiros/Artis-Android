package com.setembreiros.artis.data.model.like

data class GetPostLikesResponseApi(val postLikes: Array<GetPostLikesDto>, val lastUsername: String)

data class GetPostLikesDto(
    val username: String,
    val name: String,
)