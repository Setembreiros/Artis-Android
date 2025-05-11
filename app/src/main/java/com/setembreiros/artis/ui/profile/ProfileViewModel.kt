package com.setembreiros.artis.ui.profile

import androidx.lifecycle.viewModelScope
import com.setembreiros.artis.data.repository.ProfileRepository
import com.setembreiros.artis.domain.base.Resource
import com.setembreiros.artis.domain.model.UserProfile
import com.setembreiros.artis.domain.model.post.Post
import com.setembreiros.artis.domain.usecase.post.GetPostsUseCase
import com.setembreiros.artis.domain.usecase.GetUserProfileUseCase
import com.setembreiros.artis.domain.usecase.session.GetSessionUseCase
import com.setembreiros.artis.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val getPostsUseCase: GetPostsUseCase,
    private val getSessionUseCase: GetSessionUseCase
): BaseViewModel() {

    private val _profile = MutableStateFlow<UserProfile?>(null)
    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val profile = _profile
    val posts: StateFlow<List<Post>> get() = _posts
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    private val _thereAreMorePosts = MutableStateFlow(true)
    val thereAreMorePosts: StateFlow<Boolean> = _thereAreMorePosts
    private var periodicJob: Job? = null

    init {
        loadProfile()
        loadInitialPosts()
        rechargePostsPeriodically()
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
                    val result = getPostsUseCase.invoke(username, username, "", "")
                    _posts.value = result.first.sortedBy { it.metadata.createdAt }
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
                _thereAreMorePosts.value = result.second
                _posts.value += newPosts
                _isLoading.value = false
            }
        }
    }

    private fun rechargePostsPeriodically() {
        periodicJob?.cancel() // Cancelar calquera operación previa
        periodicJob = viewModelScope.launch(Dispatchers.IO) {
            while (true) {
                _posts.value = profileRepository.getPosts()
                delay(5000L)
            }
        }
    }
}