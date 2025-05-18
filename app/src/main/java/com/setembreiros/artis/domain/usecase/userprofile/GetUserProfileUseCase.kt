package com.setembreiros.artis.domain.usecase.userprofile

import com.setembreiros.artis.data.repository.UserRepository
import javax.inject.Inject

class GetUserProfileUseCase @Inject constructor(private val userRepository: UserRepository) {
    suspend fun invoke(username: String) = userRepository.getUserProfile(username)
}