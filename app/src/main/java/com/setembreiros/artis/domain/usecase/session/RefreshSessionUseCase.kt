package com.setembreiros.artis.domain.usecase.session

import com.setembreiros.artis.common.Constants.UserType
import com.setembreiros.artis.data.service.AuthenticationService
import com.setembreiros.artis.domain.model.Session
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

class RefreshSessionUseCase @Inject constructor(private val authService: AuthenticationService,
                                                private val saveSessionUseCase: SaveSessionUseCase) {
    fun invoke(session: Session) {
        scheduleSessionRefresh(session)
    }

    private fun scheduleSessionRefresh(session: Session) {
        val scope = CoroutineScope(Dispatchers.IO + Job())

        val delay = session.expiresIn - 60
        scope.launch {
            while (isActive) {
                println("Starting refreshing")

                val tokens = authService.refreshAuthTokens(session.userType, session.username, session.refreshToken)
                tokens?.let {
                    val idToken = it.idToken
                    val expiresIn = it.expiresIn.toLong()
                    idToken?.let { token ->
                        storeSessionToken(session.refreshToken, token, expiresIn, session.username, session.userType)
                        println("New Session stored with idToken: $idToken")
                    }
                }

                println("Waiting for refreshing: $delay seconds")
                delay(delay.seconds.inWholeMilliseconds)
            }
        }
    }

    private fun storeSessionToken(refreshToken: String, idToken: String, expiresIn: Long, username: String, userType: UserType) {
        val session = Session(refreshToken = refreshToken, idToken = idToken, expiresIn = expiresIn, userType = userType, username = username)
        saveSessionUseCase.invoke(session)
    }
}