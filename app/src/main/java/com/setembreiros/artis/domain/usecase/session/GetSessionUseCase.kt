package com.setembreiros.artis.domain.usecase.session

import com.setembreiros.artis.data.repository.SessionRepository
import javax.inject.Inject

class GetSessionUseCase @Inject constructor(private val sessionRepository: SessionRepository) {
    fun invoke() = sessionRepository.getSession()
}