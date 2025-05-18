package com.setembreiros.artis.domain.usecase.userprofile

import com.setembreiros.artis.data.repository.UserRepository
import com.setembreiros.artis.domain.base.Resource
import com.setembreiros.artis.domain.model.UserProfileSnippet
import javax.inject.Inject

class SearchUserUseCase @Inject constructor(private val userRepository: UserRepository) {
    suspend fun invoke(query: String) = searchUser(query)

    private suspend fun searchUser(query: String) : List<UserProfileSnippet> {
        return when(val response = userRepository.searchUser(query)){
            is Resource.Success -> {
                response.value
            }
            is Resource.Failure -> listOf()
        }
    }
}