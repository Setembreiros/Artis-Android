package com.setembreiros.artis.data

import com.setembreiros.artis.data.model.EmptyResponse
import com.setembreiros.artis.data.model.UserProfileApi
import com.setembreiros.artis.data.model.WrapperApi
import com.setembreiros.artis.data.model.comment.CreateCommentRequestApi
import com.setembreiros.artis.data.model.comment.GetCommentsResponseApi
import com.setembreiros.artis.data.model.like.CreateLikePostRequestApi
import com.setembreiros.artis.data.model.post.ConfirmPostRequestApi
import com.setembreiros.artis.data.model.post.CreatePostRequestApi
import com.setembreiros.artis.data.model.post.CreatePostResponseApi
import com.setembreiros.artis.data.model.post.GetPostMetadatasResponseApi
import com.setembreiros.artis.data.model.post.GetUrlPostsResponseApi
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiClient {

    @GET("readmodels/userprofile/{username}")
    suspend fun getProfile(@Header("Authorization") token: String, @Path("username") username : String) : WrapperApi<UserProfileApi>

    @POST("postservice/post")
    suspend fun createPost(@Header("Authorization") token: String, @Body postApi: CreatePostRequestApi) : WrapperApi<CreatePostResponseApi>

    @PUT("postservice/confirm-created-post")
    suspend fun confirmPost(@Header("Authorization") token: String, @Body confirmPostRequestApi: ConfirmPostRequestApi) : WrapperApi<EmptyResponse?>

    @GET("postservice/user-posts/{username}")
    suspend fun getUrlPosts(@Header("Authorization") token: String,
                            @Path("username") username: String,
                            @Query("limit") limit: Int = 9,
                            @Query("lastPostId") lastPostId: String,
                            @Query("lastPostCreatedAt") lastPostCreatedAt: String) : WrapperApi<GetUrlPostsResponseApi>

    @GET("readmodels/user-posts/{username}")
    suspend fun getPostMetadatas(@Header("Authorization") token: String,
                                 @Path("username") username: String,
                                 @Query("limit") limit: Int = 9,
                                 @Query("lastPostId") lastPostId: String,
                                 @Query("lastPostCreatedAt") lastPostCreatedAt: String) : WrapperApi<GetPostMetadatasResponseApi>

    @DELETE("postservice/posts")
    suspend fun deletePost(@Header("Authorization") token: String,
                                 @Query("postId") postId: String) : WrapperApi<EmptyResponse?>

    @POST("commentservice/comment")
    suspend fun createComment(@Header("Authorization") token: String, @Body commentApi: CreateCommentRequestApi) : WrapperApi<EmptyResponse?>

    @GET("readmodels/comments/{postId}")
    suspend fun getComments(@Header("Authorization") token: String,
                                 @Path("postId") postId: String,
                                 @Query("limit") limit: Int = 12,
                                 @Query("lastCommentId") lastCommentId: Long) : WrapperApi<GetCommentsResponseApi>

    @DELETE("commentservice/comment/{postId}/{commentId}")
    suspend fun deleteComment(@Header("Authorization") token: String,
                              @Path("postId") postId: String,
                              @Path("commentId") commentId: Long,) : WrapperApi<EmptyResponse?>

    @POST("reactionservice/likePost")
    suspend fun createLikePost(@Header("Authorization") token: String, @Body likePostApi: CreateLikePostRequestApi) : WrapperApi<EmptyResponse?>

    @DELETE("reactionservice/likePost/{postId}/{username}")
    suspend fun deleteLikePost(@Header("Authorization") token: String,
                              @Path("postId") postId: String,
                              @Path("username") username: String,) : WrapperApi<EmptyResponse?>
}