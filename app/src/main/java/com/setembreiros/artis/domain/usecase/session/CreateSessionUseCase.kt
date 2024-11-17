package com.setembreiros.artis.domain.usecase.session

import com.setembreiros.artis.common.Constants.UserType
import com.setembreiros.artis.data.service.AuthenticationService
import com.setembreiros.artis.domain.model.Session
import javax.inject.Inject

class CreateSessionUseCase @Inject constructor(private val authService: AuthenticationService,
                                               private val saveSessionUseCase: SaveSessionUseCase,
                                               private val refreshSessionUseCase: RefreshSessionUseCase) {
    suspend fun invoke(username: String, password: String, userType: UserType): Boolean {
        val tokens = authService.createAuthTokens(userType, username, password)
        tokens?.let {
            val idToken = it.idToken
            val refreshToken = it.refreshToken
            val expiresIn = it.expiresIn.toLong()
            idToken?.let {
                refreshToken?.let {
                    val session = Session(refreshToken, idToken = idToken, expiresIn= expiresIn, userType = userType, username = username)
                    saveSessionUseCase.invoke(session)
                    refreshSessionUseCase.invoke(session)
                }
            }?: return false
        }

        return true
    }
}