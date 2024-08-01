package com.setembreiros.artis.data

import com.setembreiros.artis.data.model.UserProfileApi
import com.setembreiros.artis.data.model.WrapperApi
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiClient {

    @GET("readmodels/userprofile/{username}")
    suspend fun getProfile(@Path("username") username : String) : WrapperApi<UserProfileApi>
}