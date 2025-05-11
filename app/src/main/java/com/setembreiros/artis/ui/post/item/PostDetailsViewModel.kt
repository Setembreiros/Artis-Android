package com.setembreiros.artis.ui.post.item

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.setembreiros.artis.R
import com.setembreiros.artis.data.repository.ProfileRepository
import com.setembreiros.artis.domain.model.Comment
import com.setembreiros.artis.domain.model.Like
import com.setembreiros.artis.domain.model.Superlike
import com.setembreiros.artis.domain.usecase.comment.AddCommentUseCase
import com.setembreiros.artis.domain.usecase.comment.DeleteCommentUseCase
import com.setembreiros.artis.domain.usecase.comment.GetCommentsUseCase
import com.setembreiros.artis.domain.usecase.like.AddLikePostUseCase
import com.setembreiros.artis.domain.usecase.like.DeleteLikePostUseCase
import com.setembreiros.artis.domain.usecase.like.GetLikesUseCase
import com.setembreiros.artis.domain.usecase.post.DeletePostsUseCase
import com.setembreiros.artis.domain.usecase.session.GetSessionUseCase
import com.setembreiros.artis.domain.usecase.superlike.AddSuperlikePostUseCase
import com.setembreiros.artis.domain.usecase.superlike.DeleteSuperlikePostUseCase
import com.setembreiros.artis.domain.usecase.superlike.GetSuperlikesUseCase
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
    private val addLikePostUseCase: AddLikePostUseCase,
    private val getLikesUseCase: GetLikesUseCase,
    private val deleteLikePostUseCase: DeleteLikePostUseCase,
    private val addSuperlikePostUseCase: AddSuperlikePostUseCase,
    private val getSuperlikesUseCase: GetSuperlikesUseCase,
    private val deleteSuperlikePostUseCase: DeleteSuperlikePostUseCase,
): BaseViewModel() {
    private val _amountOfCommentsByPost = MutableStateFlow<Map<String, Long>>(emptyMap())
    val amountOfCommentsByPost: StateFlow<Map<String, Long>> = _amountOfCommentsByPost.asStateFlow()
    private val _postComments = MutableStateFlow<List<Comment>>(emptyList())
    val postComments: StateFlow<List<Comment>> = _postComments.asStateFlow()
    private val _amountOfLikesByPost = MutableStateFlow<Map<String, Long>>(emptyMap())
    val amountOfLikesByPost: StateFlow<Map<String, Long>> = _amountOfLikesByPost
    private val _likedByUser = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    val likedByUser: StateFlow<Map<String, Boolean>> = _likedByUser
    private val _postLikes = MutableStateFlow<List<Like>>(emptyList())
    val postLikes: StateFlow<List<Like>> = _postLikes.asStateFlow()
    private val _amountOfSuperlikesByPost = MutableStateFlow<Map<String, Long>>(emptyMap())
    val amountOfSuperlikesByPost: StateFlow<Map<String, Long>> = _amountOfSuperlikesByPost
    private val _superlikedByUser = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    val superlikedByUser: StateFlow<Map<String, Boolean>> = _superlikedByUser
    private val _postSuperlikes = MutableStateFlow<List<Superlike>>(emptyList())
    val postSuperlikes: StateFlow<List<Superlike>> = _postSuperlikes.asStateFlow()
    private val _errorMessage = MutableStateFlow<Int?>(null)
    val errorCode: StateFlow<Int?> = _errorMessage.asStateFlow()
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    private val _thereAreMoreComments = MutableStateFlow(true)
    val thereAreMoreComments: StateFlow<Boolean> = _thereAreMoreComments
    private val _thereAreMoreLikes = MutableStateFlow(true)
    val thereAreMoreLikes: StateFlow<Boolean> = _thereAreMoreLikes
    private val _thereAreMoreSuperlikes = MutableStateFlow(true)
    val thereAreMoreSuperlikes: StateFlow<Boolean> = _thereAreMoreSuperlikes

    fun setAmountOfComments(postId: String, amountOfComments: Long) {
        _amountOfCommentsByPost.update { currentMap ->
            currentMap.toMutableMap().apply {
                if(!this.containsKey(postId)) {
                    this[postId] = amountOfComments
                }
            }
        }
    }

    fun initializeLikes(postId: String, likes: Long, isLikedByCurrentUser: Boolean) {
        _amountOfLikesByPost.update { it + (postId to likes) }
        _likedByUser.update { it + (postId to isLikedByCurrentUser) }
    }

    fun initializeSuperlikes(postId: String, superlikes: Long, isSuperlikedByCurrentUser: Boolean) {
        _amountOfSuperlikesByPost.update { it + (postId to superlikes) }
        _superlikedByUser.update { it + (postId to isSuperlikedByCurrentUser) }
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

    fun loadInitialLikes(postId: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val result = getLikesUseCase.invoke(postId, "")
                _postLikes.value = result.first
                _thereAreMoreLikes.value = result.second
            } catch (e: Exception) {
                Log.e("PostDetailsViewModel", "Error loading likes: ${e.message}")
                _errorMessage.value = R.string.error_loading_likes
            }
            _isLoading.value = false
        }
    }

    fun loadMoreLikes(postId: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val lastUsername = _postLikes.value.last().username
                val result = getLikesUseCase.invoke(postId, lastUsername)
                _thereAreMoreLikes.value = result.second
                _postLikes.update { currentList ->
                    currentList + result.first
                }
            } catch (e: Exception) {
                Log.e("PostDetailsViewModel", "Error loading likes: ${e.message}")
                _errorMessage.value = R.string.error_loading_likes
            }
            _isLoading.value = false
        }
    }

    fun toggleLikePost(postId: String) {
        val isLiked = _likedByUser.value[postId] ?: false
        viewModelScope.launch {
            try {
                if (isLiked) {
                    deleteLikeAndUpdate(postId)
                } else {
                    addLikeAndUpdate(postId)
                }
                _likedByUser.update { it + (postId to !isLiked) }
            } catch (e: Exception) {
                _errorMessage.value = R.string.error_updating_like
            }
        }
    }

    private suspend fun addLikeAndUpdate(postId: String) {
        return withContext(Dispatchers.IO) {
            try {
                val result = addLike(postId)
                if (!result) {
                    _errorMessage.value = R.string.error_adding_like
                } else {
                    increaseAmountOfLikesByOne(postId)
                }
            } catch (e: Exception) {
                Log.e("PostDetailsViewModel", "Error adding like: ${e.message}")
                _errorMessage.value = R.string.error_adding_like
            }
        }
    }

    private suspend fun deleteLikeAndUpdate(postId: String)  {
        return withContext(Dispatchers.IO) {
            try {
                val result = deleteLike(postId)
                if (!result) {
                    _errorMessage.value = R.string.error_deleting_like
                } else {
                    decreaseAmountOfLikesByOne(postId)
                }
            } catch (e: Exception) {
                Log.e("PostDetailsViewModel", "Error deleting like: ${e.message}")
                _errorMessage.value = R.string.error_deleting_like
            }
        }
    }

    fun loadInitialSuperlikes(postId: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val result = getSuperlikesUseCase.invoke(postId, "")
                _postSuperlikes.value = result.first
                _thereAreMoreSuperlikes.value = result.second
            } catch (e: Exception) {
                Log.e("PostDetailsViewModel", "Error loading superlikes: ${e.message}")
                _errorMessage.value = R.string.error_loading_superlikes
            }
            _isLoading.value = false
        }
    }

    fun loadMoreSuperlikes(postId: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val lastUsername = _postSuperlikes.value.last().username
                val result = getSuperlikesUseCase.invoke(postId, lastUsername)
                _thereAreMoreLikes.value = result.second
                _postSuperlikes.update { currentList ->
                    currentList + result.first
                }
            } catch (e: Exception) {
                Log.e("PostDetailsViewModel", "Error loading superlikes: ${e.message}")
                _errorMessage.value = R.string.error_loading_superlikes
            }
            _isLoading.value = false
        }
    }

    fun toggleSuperlikePost(postId: String) {
        val isSuperliked = _superlikedByUser.value[postId] ?: false
        viewModelScope.launch {
            try {
                if (isSuperliked) {
                    deleteSuperlikeAndUpdate(postId)
                } else {
                    addSuperlikeAndUpdate(postId)
                }
                _superlikedByUser.update { it + (postId to !isSuperliked) }
            } catch (e: Exception) {
                _errorMessage.value = R.string.error_updating_superlike
            }
        }
    }

    private suspend fun addSuperlikeAndUpdate(postId: String) {
        return withContext(Dispatchers.IO) {
            try {
                val result = addSuperlike(postId)
                if (!result) {
                    _errorMessage.value = R.string.error_adding_superlike
                } else {
                    increaseAmountOfSuperlikesByOne(postId)
                }
            } catch (e: Exception) {
                Log.e("PostDetailsViewModel", "Error adding superlike: ${e.message}")
                _errorMessage.value = R.string.error_adding_superlike
            }
        }
    }

    private suspend fun deleteSuperlikeAndUpdate(postId: String)  {
        return withContext(Dispatchers.IO) {
            try {
                val result = deleteSuperlike(postId)
                if (!result) {
                    _errorMessage.value = R.string.error_deleting_superlike
                } else {
                    decreaseAmountOfSuperlikesByOne(postId)
                }
            } catch (e: Exception) {
                Log.e("PostDetailsViewModel", "Error deleting superlike: ${e.message}")
                _errorMessage.value = R.string.error_deleting_superlike
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

    private suspend fun addLike(postId: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                var result = false
                getSessionUseCase.invoke()?.username?.let { username ->
                    result = addLikePostUseCase.invoke(username, postId)
                }
                result
            } catch (e: Exception) {
                Log.e("PostDetailsViewModel", "Error adding like: ${e.message}")
                false
            }
        }
    }

    private suspend fun deleteLike(postId: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                var result = false
                getSessionUseCase.invoke()?.username?.let { username ->
                    result = deleteLikePostUseCase.invoke(username, postId)
                }
                result
            } catch (e: Exception) {
                Log.e("PostDetailsViewModel", "Error deleting like: ${e.message}")
                false
            }
        }
    }

    private fun increaseAmountOfLikesByOne(postId: String) {
        _amountOfLikesByPost.update { currentLikes ->
            currentLikes.toMutableMap().apply {
                this[postId] = (this[postId] ?: 0) + 1
            }
        }
    }

    private fun decreaseAmountOfLikesByOne(postId: String) {
        _amountOfLikesByPost.update { currentMap ->
            currentMap.toMutableMap().apply {
                this[postId] = (this[postId] ?: 0) - 1
            }
        }
    }

    private suspend fun addSuperlike(postId: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                var result = false
                getSessionUseCase.invoke()?.username?.let { username ->
                    result = addSuperlikePostUseCase.invoke(username, postId)
                }
                result
            } catch (e: Exception) {
                Log.e("PostDetailsViewModel", "Error adding superlike: ${e.message}")
                false
            }
        }
    }

    private suspend fun deleteSuperlike(postId: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                var result = false
                getSessionUseCase.invoke()?.username?.let { username ->
                    result = deleteSuperlikePostUseCase.invoke(username, postId)
                }
                result
            } catch (e: Exception) {
                Log.e("PostDetailsViewModel", "Error deleting superlike: ${e.message}")
                false
            }
        }
    }

    private fun increaseAmountOfSuperlikesByOne(postId: String) {
        _amountOfSuperlikesByPost.update { currentSuperlikes ->
            currentSuperlikes.toMutableMap().apply {
                this[postId] = (this[postId] ?: 0) + 1
            }
        }
    }

    private fun decreaseAmountOfSuperlikesByOne(postId: String) {
        _amountOfSuperlikesByPost.update { currentMap ->
            currentMap.toMutableMap().apply {
                this[postId] = (this[postId] ?: 0) - 1
            }
        }
    }
}