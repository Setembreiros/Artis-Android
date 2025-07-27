package com.setembreiros.artis.ui.review

import androidx.lifecycle.viewModelScope
import com.setembreiros.artis.domain.usecase.review.AddReviewUseCase
import com.setembreiros.artis.domain.usecase.session.GetSessionUseCase
import com.setembreiros.artis.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class CreateReviewViewModel @Inject constructor(
    private val addReviewUseCase: AddReviewUseCase,
    private val getSessionUseCase: GetSessionUseCase
): BaseViewModel() {
    private val _postId = MutableStateFlow("")
    val postId = _postId

    private val _title = MutableStateFlow("")
    val title = _title

    private val _description = MutableStateFlow("")
    val description = _description

    private val _rating = MutableStateFlow(0)
    val rating = _rating

    fun publish(onReviewSubmitted: () -> Unit){
        viewModelScope.launch(Dispatchers.IO) {
            loading.update { true }
            getSessionUseCase.invoke()?.username?.let { username ->
                addReviewUseCase.invoke(
                    username,
                    _postId.value,
                    _title.value,
                    _description.value,
                    _rating.value
                )
                withContext(Dispatchers.Main) {
                    onReviewSubmitted()
                }
            }
            loading.update { false }
        }
    }

    fun setPostId(value: String){
        postId.value = value
    }

    fun setTitle(value: String){
        _title.value = value
    }

    fun setDescription(value: String){
        _description.value = value
    }

    fun setRating(value: Int){
        rating.value = value
    }
}


