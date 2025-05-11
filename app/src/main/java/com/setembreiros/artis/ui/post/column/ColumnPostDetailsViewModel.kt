package com.setembreiros.artis.ui.post.column

import androidx.lifecycle.viewModelScope
import com.setembreiros.artis.data.repository.ProfileRepository
import com.setembreiros.artis.domain.model.post.Post
import com.setembreiros.artis.domain.usecase.post.GetPostsUseCase
import com.setembreiros.artis.domain.usecase.session.GetSessionUseCase
import com.setembreiros.artis.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ColumnPostDetailsViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val getPostsUseCase: GetPostsUseCase,
    private val getSessionUseCase: GetSessionUseCase,
): BaseViewModel() {
    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts = _posts
    private val _post = MutableStateFlow<Post?>(null)
    val post = _post
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    private val _thereAreMorePosts = MutableStateFlow(true)
    val thereAreMorePosts: StateFlow<Boolean> = _thereAreMorePosts

    init {
        getPosts()
    }

    private fun getPosts() {
        _posts.value = profileRepository.getPosts()
    }

    fun loadMorePosts() {
        _isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            getSessionUseCase.invoke()?.username?.let { username ->
                val lastPost = _posts.value.last()
                val result = getPostsUseCase.invoke(username, username, lastPost.metadata.postId, lastPost.metadata.createdAt)
                val newPosts = result.first.sortedBy { it.metadata.createdAt }
                _thereAreMorePosts.value = result.second
                _posts.value += newPosts
                _isLoading.value = false
            }
        }
    }

    fun updatePosts() {
        viewModelScope.launch(Dispatchers.IO) {
            _posts.value = profileRepository.getPosts()
        }
    }
}