package com.setembreiros.artis.data

import com.setembreiros.artis.data.model.UserProfileApi
import com.setembreiros.artis.data.model.WrapperApi
import com.setembreiros.artis.data.model.post.ConfirmPostRequestApi
import com.setembreiros.artis.data.model.post.PostApi
import com.setembreiros.artis.data.model.post.PostResponseApi
import com.setembreiros.artis.domain.model.post.ConfirmPostRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiClient {

    @GET("readmodels/userprofile/{username}")
    suspend fun getProfile(@Header("Authorization") token: String, @Path("username") username : String) : WrapperApi<UserProfileApi>

    @POST("postservice/post")
    suspend fun createPost(@Header("Authorization") token: String, @Body postApi: PostApi) : WrapperApi<PostResponseApi>

    @PUT("postservice/confirm-created-post")
    suspend fun confirmPost(@Header("Authorization") token: String, @Body confirmPostRequestApi: ConfirmPostRequestApi) : WrapperApi<Boolean>
}