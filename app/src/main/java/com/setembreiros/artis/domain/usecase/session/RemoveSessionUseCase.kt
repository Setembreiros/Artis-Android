package com.setembreiros.artis.domain.usecase.session

import com.setembreiros.artis.data.repository.SessionRepository
import com.setembreiros.artis.data.service.AuthenticationService
import javax.inject.Inject

class RemoveSessionUseCase @Inject constructor(
    private val refreshSessionUseCase: RefreshSessionUseCase,
    private val sessionRepository: SessionRepository,
    private val authService: AuthenticationService
) {
    suspend fun invoke(){
        val session = sessionRepository.getSession()
        if (session != null) {
            refreshSessionUseCase.stopRefreshing()
            authService.revokeAuthTokens(session)
            sessionRepository.removeSession()
        }
    }
}