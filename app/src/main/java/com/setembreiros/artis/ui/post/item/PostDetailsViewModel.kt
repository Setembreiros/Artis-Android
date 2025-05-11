package com.setembreiros.artis.ui.post.item

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.setembreiros.artis.R
import com.setembreiros.artis.data.repository.ProfileRepository
import com.setembreiros.artis.domain.model.Comment
import com.setembreiros.artis.domain.usecase.comment.AddCommentUseCase
import com.setembreiros.artis.domain.usecase.comment.GetCommentsUseCase
import com.setembreiros.artis.domain.usecase.post.DeletePostsUseCase
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
    private val getSessionUseCase: GetSessionUseCase,
    private val deletePostsUseCase: DeletePostsUseCase,
    private val addCommentUseCase: AddCommentUseCase,
    private val getCommentsUseCase: GetCommentsUseCase,
): BaseViewModel() {
    private val _postComments = MutableStateFlow<List<Comment>>(emptyList())
    val postComments: StateFlow<List<Comment>> = _postComments.asStateFlow()
    private val _errorMessage = MutableStateFlow<Int?>(null)
    val errorCode: StateFlow<Int?> = _errorMessage.asStateFlow()
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    private val _thereAreMoreComments = MutableStateFlow(true)
    val thereAreMoreComments: StateFlow<Boolean> = _thereAreMoreComments

    fun deletePost(postId: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    deletePostsUseCase.invoke(postId)
                    profileRepository.removePost(postId)
                }
                onSuccess() // Chamar só despois de completar
            } catch (e: Exception) {
                Log.e("PostDetailsViewModel", "Error deleting post: ${e.message}")
                _errorMessage.value = R.string.error_deleting_post
            }
        }
    }

    fun loadInitialComments(postId: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val result = getCommentsUseCase.invoke(postId, 0)
                _postComments.value = result.first
                _thereAreMoreComments.value = result.second
            } catch (e: Exception) {
                Log.e("PostDetailsViewModel", "Error loading comments: ${e.message}")
                _errorMessage.value = R.string.error_loading_comments
            }
            _isLoading.value = false
        }
    }

    fun loadMoreComments(postId: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val lastCommentId = _postComments.value.last().commentId
                val result = getCommentsUseCase.invoke(postId, lastCommentId)
                _thereAreMoreComments.value = result.second
                _postComments.update { currentList ->
                    currentList + result.first
                }
            } catch (e: Exception) {
                Log.e("PostDetailsViewModel", "Error loading comments: ${e.message}")
                _errorMessage.value = R.string.error_loading_comments
            }
            _isLoading.value = false
        }
    }

    fun addCommentAndUpdate(postId: String, content: String) {
        viewModelScope.launch {
            try {
                val newComment = addComment(postId, content)
                newComment?.let { comment ->
                    _postComments.update { currentList ->
                        currentList + comment
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