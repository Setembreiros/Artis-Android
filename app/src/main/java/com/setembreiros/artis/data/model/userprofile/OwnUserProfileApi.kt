package com.setembreiros.artis.data.model.userprofile

data class OwnUserProfileApi(val username: String, val bio: String, val name: String, val link: String, val postsAmount: Int, val followersAmount : Int, val followeesAmount : Int, val isFollowedByCurrentUser: Boolean)