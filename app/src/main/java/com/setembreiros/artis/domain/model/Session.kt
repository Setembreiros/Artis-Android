package com.setembreiros.artis.domain.model

import com.setembreiros.artis.common.UserType

data class Session(var refreshToken: String, var idToken: String, var userType: UserType)
