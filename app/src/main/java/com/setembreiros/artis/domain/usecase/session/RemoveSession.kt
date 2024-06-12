package com.setembreiros.artis.domain.usecase.session

import com.setembreiros.artis.data.repository.SessionRepository
import javax.inject.Inject

class RemoveSession @Inject constructor(private val sessionRepository: SessionRepository) {
    fun invoke(){
        sessionRepository.removeSession()
    }
}