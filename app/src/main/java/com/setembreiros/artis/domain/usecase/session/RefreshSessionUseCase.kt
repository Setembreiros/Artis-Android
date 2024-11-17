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
import javax.inject.Singleton
import kotlin.time.Duration.Companion.seconds

@Singleton
class RefreshSessionUseCase @Inject constructor(private val authService: AuthenticationService,
                                                private val saveSessionUseCase: SaveSessionUseCase) {
    private var refreshJob: Job? = null

    fun invoke(session: Session) {
        scheduleSessionRefresh(session)
    }

    private fun scheduleSessionRefresh(session: Session) {
        // Cancel any existing job to avoid multiple refresh loops
        refreshJob?.cancel()

        val scope = CoroutineScope(Dispatchers.IO + Job())

        val delay = session.expiresIn - 300
        refreshJob = scope.launch {
            while (isActive) {
                println("Waiting for refreshing: $delay seconds")
                delay(delay.seconds.inWholeMilliseconds)
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
            }
        }
    }

    fun stopRefreshing() {
        refreshJob?.cancel()
        println("Session refreshing stopped.")
    }

    private fun storeSessionToken(refreshToken: String, idToken: String, expiresIn: Long, username: String, userType: UserType) {
        val session = Session(refreshToken = refreshToken, idToken = idToken, expiresIn = expiresIn, userType = userType, username = username)
        saveSessionUseCase.invoke(session)
    }
}