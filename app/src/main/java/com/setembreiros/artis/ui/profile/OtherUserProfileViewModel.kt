package com.setembreiros.artis.ui.profile

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.setembreiros.artis.R
import com.setembreiros.artis.data.repository.ProfileRepository
import com.setembreiros.artis.domain.base.Resource
import com.setembreiros.artis.domain.model.UserProfile
import com.setembreiros.artis.domain.model.post.Post
import com.setembreiros.artis.domain.usecase.follow.FollowUserUseCase
import com.setembreiros.artis.domain.usecase.follow.UnfollowUserUseCase
import com.setembreiros.artis.domain.usecase.post.GetPostsUseCase
import com.setembreiros.artis.domain.usecase.session.GetSessionUseCase
import com.setembreiros.artis.domain.usecase.userprofile.GetOtherUserProfileUseCase
import com.setembreiros.artis.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class OtherUserProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val getOtherUserProfileUseCase: GetOtherUserProfileUseCase,
    private val getPostsUseCase: GetPostsUseCase,
    private val getSessionUseCase: GetSessionUseCase,
    private val followUserUseCase: FollowUserUseCase,
    private val unfollowUserUseCase: UnfollowUserUseCase,
): BaseViewModel() {
    private val _errorMessage = MutableStateFlow<Int?>(null)
    val errorCode: StateFlow<Int?> = _errorMessage.asStateFlow()

    private val _profile = MutableStateFlow<UserProfile?>(null)
    val profile = _profile

    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> get() = _posts

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _thereAreMorePosts = MutableStateFlow(true)
    val thereAreMorePosts: StateFlow<Boolean> = _thereAreMorePosts

    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    fun loadProfile(username: String){
        viewModelScope.launch(Dispatchers.IO) {
            getSessionUseCase.invoke()?.username?.let { currentUsername ->
                when (val response = getOtherUserProfileUseCase.invoke(username, currentUsername)) {
                    is Resource.Success -> {
                        _profile.value = response.value
                    }
                    else -> {
                        _errorMessage.value = R.string.error_loading_user_profile
                    }
                }
            }
        }
    }

    fun loadInitialPosts(username: String) {
        _isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            getSessionUseCase.invoke()?.username?.let { currentUsername ->
                if (_posts.value.isEmpty()) {
                    profileRepository.removeAllVisitPosts()
                    val result = getPostsUseCase.invoke(username, currentUsername, "", "")
                    _posts.value = result.first.sortedBy { it.metadata.createdAt }
                    _posts.value.forEach { post ->  profileRepository.saveVisitPost(post) }
                    _thereAreMorePosts.value = result.second
                }
                _isLoading.value = false
            }
        }
    }

    fun loadMorePosts(username: String) {
        _isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            getSessionUseCase.invoke()?.username?.let { currentUsername ->
                val lastPost = _posts.value.last()
                val result = getPostsUseCase.invoke(username, currentUsername, lastPost.metadata.postId, lastPost.metadata.createdAt)
                val newPosts = result.first.sortedBy { it.metadata.createdAt }
                newPosts.forEach { post ->  profileRepository.saveVisitPost(post) }
                _thereAreMorePosts.value = result.second
                _posts.value += newPosts
                _isLoading.value = false
            }
        }
    }

    fun toggleFollow(username: String) {
        viewModelScope.launch {
            _profile.value?.let { currentProfile ->
                val updatedProfile = if (!currentProfile.isFollowedByCurrentUser) {
                    follow(username)
                    currentProfile.copy(
                        isFollowedByCurrentUser = true,
                        followersAmount = currentProfile.followersAmount + 1
                    )
                } else {
                    unfollow(username)
                    currentProfile.copy(
                        isFollowedByCurrentUser = false,
                        followersAmount = currentProfile.followersAmount - 1
                    )
                }
                _profile.value = updatedProfile
            }
        }
    }

    private suspend fun follow(username: String) {
        return withContext(Dispatchers.IO) {
            try {
                var result = false
                getSessionUseCase.invoke()?.username?.let { currentUsername ->
                    result = followUserUseCase.invoke(currentUsername, username)
                }

                if (!result) {
                    _errorMessage.value = R.string.error_following_user
                }
            } catch (e: Exception) {
                Log.e("OtherUserProfileViewModel", "Error following user: ${e.message}")
                _errorMessage.value = R.string.error_following_user
            }
        }
    }

    private suspend fun unfollow(username: String) {
        return withContext(Dispatchers.IO) {
            try {
                var result = false
                getSessionUseCase.invoke()?.username?.let { currentUsername ->
                    result = unfollowUserUseCase.invoke(currentUsername, username)
                }

                if (!result) {
                    _errorMessage.value = R.string.error_unfollowing_user
                }
            } catch (e: Exception) {
                Log.e("OtherUserProfileViewModel", "Error unfollowing user: ${e.message}")
                _errorMessage.value = R.string.error_unfollowing_user
            }
        }
    }
}