package com.setembreiros.artis.data

import com.setembreiros.artis.data.model.UserProfileApi
import com.setembreiros.artis.data.model.WrapperApi
import com.setembreiros.artis.data.model.post.PostApi
import com.setembreiros.artis.data.model.post.PostResponseApi
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiClient {

    @GET("readmodels/userprofile/{username}")
    suspend fun getProfile(@Path("username") username : String) : WrapperApi<UserProfileApi>

    @POST("postservice/post")
    suspend fun createPost(@Body postApi: PostApi) : WrapperApi<PostResponseApi>
}