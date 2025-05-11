package com.setembreiros.artis.ui.post

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.setembreiros.artis.R
import com.setembreiros.artis.data.repository.ProfileRepository
import com.setembreiros.artis.domain.model.Comment
import com.setembreiros.artis.domain.model.post.Post
import com.setembreiros.artis.domain.usecase.comment.AddCommentUseCase
import com.setembreiros.artis.domain.usecase.post.DeletePostsUseCase
import com.setembreiros.artis.domain.usecase.post.GetPostsUseCase
import com.setembreiros.artis.domain.usecase.session.GetSessionUseCase
import com.setembreiros.artis.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class PostDetailsViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val getPostsUseCase: GetPostsUseCase,
    private val getSessionUseCase: GetSessionUseCase,
    private val deletePostsUseCase: DeletePostsUseCase,
    private val addCommentUseCase: AddCommentUseCase,
): BaseViewModel() {
    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts = _posts
    private val _post = MutableStateFlow<Post?>(null)
    val post = _post
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    private val _isThereMorePosts = MutableStateFlow(true)
    val isThereMorePosts: StateFlow<Boolean> = _isThereMorePosts
    private val _commentsByPost = MutableStateFlow<Map<String, List<Comment>>>(emptyMap())
    val commentsByPost: StateFlow<Map<String, List<Comment>>> = _commentsByPost.asStateFlow()
    private val _errorMessage = MutableStateFlow<Int?>(null)
    val errorCode: StateFlow<Int?> = _errorMessage.asStateFlow()

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

    fun loadCommentsForPost(postId: String) {
        viewModelScope.launch {
            try {
                val comments = loadComments(postId)
                _commentsByPost.update { currentMap ->
                    currentMap + (postId to comments)
                }
            } catch (e: Exception) {
                Log.e("PostDetailsViewModel", "Error loading comments: ${e.message}")
                _errorMessage.value = R.string.error_loading_comments
            }
        }
    }

    private suspend fun loadComments(postId: String): List<Comment> {
        return withContext(Dispatchers.IO) {
            _commentsByPost.value[postId] ?: emptyList()
        }
    }

    fun addCommentAndUpdate(postId: String, content: String) {
        viewModelScope.launch {
            try {
                val newComment = addComment(postId, content)
                newComment?.let { comment ->
                    _commentsByPost.update { currentMap ->
                        val currentComments = currentMap[postId] ?: emptyList()
                        currentMap + (postId to (currentComments + comment))
                    }
                } ?: run {
                    _errorMessage.value = R.string.comment_failed
                }
            } catch (e: Exception) {
                Log.e("PostDetailsViewModel", "Error adding comment: ${e.message}")
                _errorMessage.value = R.string.error_adding_comment
            }
        }
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    private suspend fun addComment(postId: String, content: String): Comment? {
        return withContext(Dispatchers.IO) {
            try {
                getSessionUseCase.invoke()?.username?.let { username ->
                    addCommentUseCase.invoke(username, postId, content)
                }
            } catch (e: Exception) {
                null
            }
        }
    }
}