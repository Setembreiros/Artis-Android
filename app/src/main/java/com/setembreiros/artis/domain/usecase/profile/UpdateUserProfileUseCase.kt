package com.setembreiros.artis.domain.usecase.profile

import com.setembreiros.artis.data.repository.UserRepository
import com.setembreiros.artis.domain.model.profile.UserProfile
import javax.inject.Inject

class UpdateUserProfileUseCase @Inject constructor(private val userRepository: UserRepository) {
    suspend fun invoke(userProfile: UserProfile) = userRepository.updateUserProfile(userProfile)
}