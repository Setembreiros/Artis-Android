package com.setembreiros.artis.data.model.superlike

data class GetPostSuperlikesResponseApi(val postSuperlikes: Array<GetPostSuperlikesDto>, val lastUsername: String)

data class GetPostSuperlikesDto(
    val username: String,
    val name: String,
)