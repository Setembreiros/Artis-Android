package com.setembreiros.artis.domain.model

import com.setembreiros.artis.common.Constants.UserType

data class Session(var refreshToken: String, var idToken: String, var userType: UserType, var username: String)
