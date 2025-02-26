package com.setembreiros.artis.data.repository

import com.setembreiros.artis.data.ApiClient
import com.setembreiros.artis.data.base.BaseApiClient
import com.setembreiros.artis.data.mapper.fromdomain.ConfirmUserProfileImageMapper
import com.setembreiros.artis.data.mapper.fromdomain.UserProfileImageMapper
import com.setembreiros.artis.data.mapper.fromdomain.UserProfileMapper
import com.setembreiros.artis.data.mapper.todomain.GenericBoolMapperApi
import com.setembreiros.artis.data.mapper.todomain.GetUserProfileResponseMapperApi
import com.setembreiros.artis.data.mapper.todomain.UpdateUserProfileImageResponseMapperApi
import com.setembreiros.artis.data.mapper.todomain.UpdateUserProfileResponseMapperApi
import com.setembreiros.artis.domain.model.profile.ConfirmUserProfileImage
import com.setembreiros.artis.domain.model.profile.UserProfile
import javax.inject.Inject

class UserRepository @Inject constructor(private val apiClient: ApiClient, private val sessionRepository: SessionRepository): BaseApiClient() {

    private fun getToken() = "Bearer " +sessionRepository.getSession()!!.idToken

    suspend fun getUserProfile(username: String) = safeApiCall(GetUserProfileResponseMapperApi()){
        apiClient.getProfile(getToken(), username)
    }

    suspend fun updateUserProfile(userProfile: UserProfile) = safeApiCall(
        UpdateUserProfileResponseMapperApi()
    ){
        apiClient.updateProfile(getToken() , UserProfileMapper().map(userProfile))
    }

    suspend fun updateUserProfileImage(username: String) = safeApiCall(
        UpdateUserProfileImageResponseMapperApi()
    ){
        apiClient.updateProfileImage(getToken() , UserProfileImageMapper().map(username))
    }

    suspend fun confirmUserProfileImage(confirmUserProfileImage: ConfirmUserProfileImage) = safeApiCall(
        GenericBoolMapperApi()
    ){
        apiClient.confirmProfileImage(getToken() , ConfirmUserProfileImageMapper().map(confirmUserProfileImage))
    }
}