package com.setembreiros.artis.ui.post.item

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.setembreiros.artis.R
import com.setembreiros.artis.data.repository.ProfileRepository
import com.setembreiros.artis.domain.model.Comment
import com.setembreiros.artis.domain.usecase.comment.AddCommentUseCase
import com.setembreiros.artis.domain.usecase.comment.DeleteCommentUseCase
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
    private val deleteCommentUseCase: DeleteCommentUseCase,
): BaseViewModel() {
    private val _amountOfCommentsByPost = MutableStateFlow<Map<String, Long>>(emptyMap())
    val amountOfCommentsByPost: StateFlow<Map<String, Long>> = _amountOfCommentsByPost.asStateFlow()
    private val _postComments = MutableStateFlow<List<Comment>>(emptyList())
    val postComments: StateFlow<List<Comment>> = _postComments.asStateFlow()
    private val _errorMessage = MutableStateFlow<Int?>(null)
    val errorCode: StateFlow<Int?> = _errorMessage.asStateFlow()
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    private val _thereAreMoreComments = MutableStateFlow(true)
    val thereAreMoreComments: StateFlow<Boolean> = _thereAreMoreComments

    fun setAmountOfComments(postId: String, amountOfComments: Long) {
        _amountOfCommentsByPost.update { currentMap ->
            currentMap.toMutableMap().apply {
                if(!this.containsKey(postId)) {
                    this[postId] = amountOfComments
                }
            }
        }
    }

    private fun increaseAmountOfCommentsByOne(postId: String) {
        _amountOfCommentsByPost.update { currentMap ->
            currentMap.toMutableMap().apply {
                this[postId] = (this[postId] ?: 0) + 1
            }
        }
    }

    private fun decreaseAmountOfCommentsByOne(postId: String) {
        _amountOfCommentsByPost.update { currentMap ->
            currentMap.toMutableMap().apply {
                this[postId] = (this[postId] ?: 0) - 1
            }
        }
    }

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
                        listOf(comment) + currentList // Engade ao comezo
                    }
                    increaseAmountOfCommentsByOne(postId)
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

    fun deleteCommentAndUpdate(postId: String, commentId: Long)  {
        viewModelScope.launch {
            try {
                val result = deleteComment(postId, commentId)
                if (!result) {
                    _errorMessage.value = R.string.error_deleting_comment
                } else {
                    _postComments.update { currentList ->
                        currentList.filterNot { it.commentId == commentId }// Eliminao da lista
                    }
                    decreaseAmountOfCommentsByOne(postId)
                }
            } catch (e: Exception) {
                Log.e("PostDetailsViewModel", "Error deleting comment: ${e.message}")
                _errorMessage.value = R.string.error_deleting_comment
            }
        }
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

    private suspend fun deleteComment(postId: String, commentId: Long): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                deleteCommentUseCase.invoke(postId, commentId)
            } catch (e: Exception) {
                Log.e("PostDetailsViewModel", "Error deleting comment: ${e.message}")
                false
            }
        }
    }
}