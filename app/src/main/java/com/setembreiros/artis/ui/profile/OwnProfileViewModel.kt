package com.setembreiros.artis.ui.profile

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.setembreiros.artis.R
import com.setembreiros.artis.data.repository.ProfileRepository
import com.setembreiros.artis.domain.base.Resource
import com.setembreiros.artis.domain.model.UserProfile
import com.setembreiros.artis.domain.model.UserProfileSnippet
import com.setembreiros.artis.domain.model.post.Post
import com.setembreiros.artis.domain.usecase.follow.GetFollowersUseCase
import com.setembreiros.artis.domain.usecase.post.GetPostsUseCase
import com.setembreiros.artis.domain.usecase.userprofile.GetUserProfileUseCase
import com.setembreiros.artis.domain.usecase.session.GetSessionUseCase
import com.setembreiros.artis.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OwnProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val getPostsUseCase: GetPostsUseCase,
    private val getSessionUseCase: GetSessionUseCase,
    private val getFollowersUseCase: GetFollowersUseCase,
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

    private val _isSecondLoading = MutableStateFlow(false)
    val isSecondLoading: StateFlow<Boolean> = _isSecondLoading

    private val _followers = MutableStateFlow<List<UserProfileSnippet>>(emptyList())
    val followers: StateFlow<List<UserProfileSnippet>> = _followers.asStateFlow()

    private val _thereAreMoreFollowers = MutableStateFlow(true)
    val thereAreMoreFollowers: StateFlow<Boolean> = _thereAreMoreFollowers

    private var periodicJob: Job? = null

    init {
        loadProfile()
        loadInitialPosts()
        rechargePostsPeriodically()
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    private fun loadProfile(){
        viewModelScope.launch(Dispatchers.IO) {
            getSessionUseCase.invoke()?.username?.let { username->
                when(val response = getUserProfileUseCase.invoke(username)){
                    is Resource.Success -> {
                        _profile.value = response.value
                    }
                    else -> {

                    }
                }
            }
        }
    }

    private fun loadInitialPosts() {
        _isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            getSessionUseCase.invoke()?.username?.let { username ->
                if (_posts.value.isEmpty()) {
                    profileRepository.removeAllVisitPosts()
                    val result = getPostsUseCase.invoke(username, username, "", "")
                    _posts.value = result.first.sortedBy { it.metadata.createdAt }
                    _posts.value.forEach { post ->
                        profileRepository.saveOwnPost(post)
                        profileRepository.saveVisitPost(post)
                    }
                    _thereAreMorePosts.value = result.second
                }
                _isLoading.value = false
            }
        }
    }

    fun loadMorePosts() {
        _isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            getSessionUseCase.invoke()?.username?.let { username ->
                val lastPost = _posts.value.last()
                val result = getPostsUseCase.invoke(username,username, lastPost.metadata.postId, lastPost.metadata.createdAt)
                val newPosts = result.first.sortedBy { it.metadata.createdAt }
                newPosts.forEach { post ->
                    profileRepository.saveOwnPost(post)
                    profileRepository.saveVisitPost(post)
                }
                _thereAreMorePosts.value = result.second
                _posts.value += newPosts
                _isLoading.value = false
            }
        }
    }

    fun loadInitialFollows() {
        _isSecondLoading.value = true
        viewModelScope.launch {
            getSessionUseCase.invoke()?.username?.let { username ->
                try {
                    val result = getFollowersUseCase.invoke(username, "")
                    _followers.value = result.first
                    _thereAreMoreFollowers.value = result.second
                } catch (e: Exception) {
                    Log.e("OtherUserProfileViewModel", "Error loading followers: ${e.message}")
                    _errorMessage.value = R.string.error_loading_followers
                }
                _isSecondLoading.value = false
            }
        }
    }

    fun loadMoreFollows() {
        _isSecondLoading.value = true
        viewModelScope.launch {
            getSessionUseCase.invoke()?.username?.let { username ->
                try {
                    val lastUsername = followers.value.last().username
                    val result = getFollowersUseCase.invoke(username, lastUsername)
                    _thereAreMoreFollowers.value = result.second
                    _followers.update { currentList ->
                        currentList + result.first
                    }
                } catch (e: Exception) {
                    Log.e("OtherUserProfileViewModel", "Error loading followers: ${e.message}")
                    _errorMessage.value = R.string.error_loading_followers
                }
                _isSecondLoading.value = false
            }
        }
    }

    private fun rechargePostsPeriodically() {
        periodicJob?.cancel() // Cancelar calquera operación previa
        periodicJob = viewModelScope.launch(Dispatchers.IO) {
            while (true) {
                _posts.value = profileRepository.getOwnPosts()
                delay(5000L)
            }
        }
    }
}