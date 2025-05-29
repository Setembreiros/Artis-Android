package com.setembreiros.artis.domain.model

data class UserProfile(val username: String, val bio: String, val name: String, val link: String, val postsAmount: Int, var followersAmount: Int, var isFollowedByCurrentUser: Boolean)
