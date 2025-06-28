package com.setembreiros.artis.data.model.follow

data class GetFollowersResponseApi(val followers: Array<GetFollowersDto>, val lastFollowerId: String)

data class GetFollowersDto(
    val username: String,
    val fullname: String,
)