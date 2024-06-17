package com.setembreiros.artis.domain.usecase.session

import com.setembreiros.artis.data.repository.SessionRepository
import javax.inject.Inject

class RemoveSessionUseCase @Inject constructor(private val sessionRepository: SessionRepository) {
    suspend fun invoke(){
        sessionRepository.removeSession()
    }
}