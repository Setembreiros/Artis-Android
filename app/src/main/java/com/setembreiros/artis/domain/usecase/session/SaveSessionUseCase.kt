package com.setembreiros.artis.domain.usecase.session

import com.setembreiros.artis.data.repository.SessionRepository
import com.setembreiros.artis.domain.model.Session
import javax.inject.Inject

class SaveSessionUseCase @Inject constructor(private val sessionRepository: SessionRepository) {

    fun invoke(session: Session){
        sessionRepository.saveSession(session)
    }
}