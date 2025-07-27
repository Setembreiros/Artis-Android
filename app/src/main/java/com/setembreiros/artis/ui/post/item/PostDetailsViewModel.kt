package com.setembreiros.artis.ui.post.item

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.setembreiros.artis.R
import com.setembreiros.artis.data.repository.ProfileRepository
import com.setembreiros.artis.domain.model.Comment
import com.setembreiros.artis.domain.model.Like
import com.setembreiros.artis.domain.model.Review
import com.setembreiros.artis.domain.model.Superlike
import com.setembreiros.artis.domain.usecase.comment.AddCommentUseCase
import com.setembreiros.artis.domain.usecase.comment.DeleteCommentUseCase
import com.setembreiros.artis.domain.usecase.comment.GetCommentsUseCase
import com.setembreiros.artis.domain.usecase.like.AddLikePostUseCase
import com.setembreiros.artis.domain.usecase.like.DeleteLikePostUseCase
import com.setembreiros.artis.domain.usecase.like.GetLikesUseCase
import com.setembreiros.artis.domain.usecase.post.DeletePostsUseCase
import com.setembreiros.artis.domain.usecase.review.AddReviewUseCase
import com.setembreiros.artis.domain.usecase.review.DeleteReviewUseCase
import com.setembreiros.artis.domain.usecase.review.GetReviewsUseCase
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
    private val getReviewsUseCase: GetReviewsUseCase,
    private val deleteReviewUseCase: DeleteReviewUseCase,
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
    private val _currentUsername = MutableStateFlow(getSessionUseCase.invoke()?.username)
    val currentUsername = _currentUsername
    private val _amountOfReviewsByPost = MutableStateFlow<Map<String, Long>>(emptyMap())
    val amountOfReviewsByPost: StateFlow<Map<String, Long>> = _amountOfReviewsByPost.asStateFlow()
    private val _postReviews = MutableStateFlow<List<Review>>(emptyList())
    val postReviews: StateFlow<List<Review>> = _postReviews.asStateFlow()
    private val _reviewedByUser = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    val reviewedByUser: StateFlow<Map<String, Boolean>> = _reviewedByUser
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
    private val _thereAreMoreReviews = MutableStateFlow(true)
    val thereAreMoreReviews: StateFlow<Boolean> = _thereAreMoreReviews
    private val _thereAreMoreComments = MutableStateFlow(true)
    val thereAreMoreComments: StateFlow<Boolean> = _thereAreMoreComments
    private val _thereAreMoreLikes = MutableStateFlow(true)
    val thereAreMoreLikes: StateFlow<Boolean> = _thereAreMoreLikes
    private val _thereAreMoreSuperlikes = MutableStateFlow(true)
    val thereAreMoreSuperlikes: StateFlow<Boolean> = _thereAreMoreSuperlikes

    fun initializeReviews(postId: String) {
        setAmountOfReviews(postId)
        _reviewedByUser.update { it + (postId to profileRepository.getVisitPost(postId).metadata.isReviewedByCurrentUser) }
    }

    fun setAmountOfReviews(postId: String) {
        _amountOfReviewsByPost.update { currentMap ->
            currentMap.toMutableMap().apply {
                this[postId] = profileRepository.getVisitPost(postId).metadata.reviews
            }
        }
    }

    fun initializeComments(postId: String) {
        _amountOfCommentsByPost.update { currentMap ->
            currentMap.toMutableMap().apply {
                if(!this.containsKey(postId)) {
                    this[postId] = profileRepository.getVisitPost(postId).metadata.comments
                }
            }
        }
    }

    fun setAmountOfComments(postId: String) {
        _amountOfCommentsByPost.update { currentMap ->
            currentMap.toMutableMap().apply {
                this[postId] = profileRepository.getVisitPost(postId).metadata.comments
            }
        }
    }

    fun initializeLikes(postId: String) {
        _amountOfLikesByPost.update { it + (postId to profileRepository.getVisitPost(postId).metadata.likes) }
        _likedByUser.update { it + (postId to profileRepository.getVisitPost(postId).metadata.isLikedByCurrentUser) }
    }

    fun initializeSuperlikes(postId: String) {
        _amountOfSuperlikesByPost.update { it + (postId to profileRepository.getVisitPost(postId).metadata.superlikes) }
        _superlikedByUser.update { it + (postId to profileRepository.getVisitPost(postId).metadata.isSuperlikedByCurrentUser) }
    }

    fun deletePost(postId: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    getSessionUseCase.invoke()?.username?.let { username ->
                        deletePostsUseCase.invoke(username, postId)
                        profileRepository.removePost(postId)
                    }
                }
                onSuccess() // Chamar só despois de completar
            } catch (e: Exception) {
                Log.e("PostDetailsViewModel", "Error deleting post: ${e.message}")
                _errorMessage.value = R.string.error_deleting_post
            }
        }
    }

    fun loadInitialReviews(postId: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val result = getReviewsUseCase.invoke(postId, 0)
                _postReviews.value = result.first
                _thereAreMoreReviews.value = result.second
            } catch (e: Exception) {
                Log.e("PostDetailsViewModel", "Error loading reviews: ${e.message}")
                _errorMessage.value = R.string.error_loading_reviews
            }
            _isLoading.value = false
        }
    }

    fun loadMoreReviews(postId: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val lastReviewId = _postReviews.value.last().reviewId
                val result = getReviewsUseCase.invoke(postId, lastReviewId)
                _thereAreMoreReviews.value = result.second
                _postReviews.update { currentList ->
                    currentList + result.first
                }
            } catch (e: Exception) {
                Log.e("PostDetailsViewModel", "Error loading reviews: ${e.message}")
                _errorMessage.value = R.string.error_loading_reviews
            }
            _isLoading.value = false
        }
    }

    fun deleteReviewAndUpdate(postId: String, reviewId: Long)  {
        viewModelScope.launch {
            try {
                val result = deleteReview(postId, reviewId)
                if (!result) {
                    _errorMessage.value = R.string.error_deleting_review
                } else {
                    _postReviews.update { currentList ->
                        currentList.filterNot { it.reviewId == reviewId }// Eliminao da lista
                    }
                    setAmountOfReviews(postId)
                }
            } catch (e: Exception) {
                Log.e("PostDetailsViewModel", "Error deleting comment: ${e.message}")
                _errorMessage.value = R.string.error_deleting_comment
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
                    setAmountOfComments(postId)
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
                    setAmountOfComments(postId)
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
                    initializeLikes(postId)
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
                    initializeLikes(postId)
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
                    initializeSuperlikes(postId)
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
                    initializeSuperlikes(postId)
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

    private suspend fun deleteReview(postId: String, reviewId: Long): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                deleteReviewUseCase.invoke(postId, reviewId)
            } catch (e: Exception) {
                Log.e("PostDetailsViewModel", "Error deleting review: ${e.message}")
                false
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
}