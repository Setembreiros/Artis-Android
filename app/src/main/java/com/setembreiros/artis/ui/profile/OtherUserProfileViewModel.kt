package com.setembreiros.artis.ui.profile

import androidx.lifecycle.viewModelScope
import com.setembreiros.artis.data.repository.ProfileRepository
import com.setembreiros.artis.domain.base.Resource
import com.setembreiros.artis.domain.model.UserProfile
import com.setembreiros.artis.domain.model.post.Post
import com.setembreiros.artis.domain.usecase.post.GetPostsUseCase
import com.setembreiros.artis.domain.usecase.userprofile.GetUserProfileUseCase
import com.setembreiros.artis.domain.usecase.session.GetSessionUseCase
import com.setembreiros.artis.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OtherUserProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val getPostsUseCase: GetPostsUseCase,
    private val getSessionUseCase: GetSessionUseCase,
): BaseViewModel() {
    private val _profile = MutableStateFlow<UserProfile?>(null)
    val profile = _profile

    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> get() = _posts

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _thereAreMorePosts = MutableStateFlow(true)
    val thereAreMorePosts: StateFlow<Boolean> = _thereAreMorePosts

    fun loadProfile(username: String){
        viewModelScope.launch(Dispatchers.IO) {
            when(val response = getUserProfileUseCase.invoke(username)){
                is Resource.Success -> {
                    _profile.value = response.value
                }
                else -> {
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
}