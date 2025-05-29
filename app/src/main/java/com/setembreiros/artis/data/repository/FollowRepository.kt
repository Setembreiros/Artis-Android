package com.setembreiros.artis.data.repository

import com.setembreiros.artis.data.ApiClient
import com.setembreiros.artis.data.base.BaseApiClient
import com.setembreiros.artis.data.model.follow.AddFollowerRequestApi
import com.setembreiros.artis.domain.usecase.session.GetSessionUseCase
import javax.inject.Inject

class FollowRepository @Inject constructor(private val apiClient: ApiClient, private val getSessionUseCase: GetSessionUseCase): BaseApiClient() {
    private fun getToken() = "Bearer " + getSessionUseCase.invoke()!!.idToken

    suspend fun addFollower(followerId: String, followeeId: String) = safeApiCall{
        apiClient.addFollower(getToken(), AddFollowerRequestApi(followerId, followeeId))
    }

    suspend fun removeFollower(followerId: String, followeeId: String) = safeApiCall{
        apiClient.removeFollower(getToken(), followerId, followeeId)
    }
}