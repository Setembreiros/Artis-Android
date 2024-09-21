package com.setembreiros.artis.data

import com.setembreiros.artis.data.model.UserProfileApi
import com.setembreiros.artis.data.model.WrapperApi
import com.setembreiros.artis.data.model.post.ConfirmPostRequestApi
import com.setembreiros.artis.data.model.post.CreatePostRequestApi
import com.setembreiros.artis.data.model.post.CreatePostResponseApi
import com.setembreiros.artis.data.model.post.GetPostMetadatasResponseApi
import com.setembreiros.artis.data.model.post.GetUrlPostsResponseApi
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
    suspend fun createPost(@Header("Authorization") token: String, @Body postApi: CreatePostRequestApi) : WrapperApi<CreatePostResponseApi>

    @PUT("postservice/confirm-created-post")
    suspend fun confirmPost(@Header("Authorization") token: String, @Body confirmPostRequestApi: ConfirmPostRequestApi) : WrapperApi<Boolean>

    @GET("postservice/user-posts/{username}")
    suspend fun getUrlPosts(@Header("Authorization") token: String, @Path("username") username : String) : WrapperApi<GetUrlPostsResponseApi>

    @GET("readmodels/user-posts/{username}")
    suspend fun getPostMetadatas(@Header("Authorization") token: String, @Path("username") username : String) : WrapperApi<GetPostMetadatasResponseApi>
}