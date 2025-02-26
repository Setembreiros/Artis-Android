package com.setembreiros.artis.ui.post

import androidx.lifecycle.viewModelScope
import com.setembreiros.artis.data.repository.ProfileRepository
import com.setembreiros.artis.domain.model.post.Post
import com.setembreiros.artis.domain.usecase.post.DeletePostsUseCase
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
class PostDetailsViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val getPostsUseCase: GetPostsUseCase,
    private val getSessionUseCase: GetSessionUseCase,
    private val deletePostsUseCase: DeletePostsUseCase
): BaseViewModel() {
    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts = _posts
    private val _post = MutableStateFlow<Post?>(null)
    val post = _post
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    private val _isThereMorePosts = MutableStateFlow(true)
    val isThereMorePosts: StateFlow<Boolean> = _isThereMorePosts

    init {
        getPosts()
    }

    private fun getPosts() {
        _posts.value = profileRepository.getPosts()
    }

    fun loadMorePosts() {
        viewModelScope.launch(Dispatchers.IO) {
            getSessionUseCase.invoke()?.username?.let { username ->
                _isLoading.value = true
                val lastPost = _posts.value.last()
                val result = getPostsUseCase.invoke(username, lastPost.metadata.postId, lastPost.metadata.createdAt)
                val newPosts = result.first.sortedBy { it.metadata.createdAt }
                _isThereMorePosts.value = result.second
                _posts.value += newPosts
                _isLoading.value = false
            }
        }
    }

    fun deletePost(postId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            deletePostsUseCase.invoke(postId)
            profileRepository.removePost(postId)
            _posts.value = profileRepository.getPosts()
        }
    }
}