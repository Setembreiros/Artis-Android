package com.setembreiros.artis.data.model.userprofile

data class SearchUserResponseApi(val users: List<UserProfileSnippet>)

data class UserProfileSnippet(val username: String, val name: String)
