package com.setembreiros.artis.data.repository

import com.setembreiros.artis.data.ApiClient
import com.setembreiros.artis.data.base.BaseApiClient
import com.setembreiros.artis.data.mapper.UserProfileMapperApi
import javax.inject.Inject

class UserRepository @Inject constructor(private val apiClient: ApiClient, private val sessionRepository: SessionRepository): BaseApiClient() {

    private fun getToken() = "Bearer " +sessionRepository.getSession()!!.idToken

    suspend fun getUserProfile(username: String) = safeApiCall(UserProfileMapperApi()){
        apiClient.getProfile(getToken(), username)
    }
}