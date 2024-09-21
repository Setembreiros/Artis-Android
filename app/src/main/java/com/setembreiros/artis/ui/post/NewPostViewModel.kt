package com.setembreiros.artis.ui.post

import androidx.lifecycle.viewModelScope
import com.setembreiros.artis.common.Constants
import com.setembreiros.artis.domain.model.post.Post
import com.setembreiros.artis.domain.model.post.PostMetadata
import com.setembreiros.artis.domain.usecase.post.CreatePostUseCase
import com.setembreiros.artis.domain.usecase.session.GetSessionUseCase
import com.setembreiros.artis.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewPostViewModel @Inject constructor(
    private val createPostUseCase: CreatePostUseCase,
    getSessionUseCase: GetSessionUseCase
): BaseViewModel() {

    private val _session = MutableStateFlow(getSessionUseCase.invoke())
    val session = _session

    private val _title = MutableStateFlow("")
    val title = _title

    private val _description = MutableStateFlow("")
    val description = _description

    private val _resource = MutableStateFlow<ByteArray?>(null)
    val resource = _resource

    private val _type = MutableStateFlow(Constants.ContentType.IMAGE)
    val type = _type

    private val _fileType = MutableStateFlow("png")
    val fileType = _fileType

    fun publish(){
        _resource.value?.let {
            loading.update { true }
            val post = Post(
                metadata = PostMetadata(
                    postId = "",
                    username = session.value!!.username,
                    title = _title.value,
                    description = _description.value,
                    type = _type.value,
                    fileType = _fileType.value
                ),
                content = ByteArray(0)
            )
            viewModelScope.launch(Dispatchers.IO) {
                createPostUseCase.invoke(post, it)
                loading.update { false }
            }
        }
    }

    fun setTitle(value: String){
        _title.value = value
    }

    fun setDescription(value: String){
        _description.value = value
    }

    fun setResource(value: ByteArray){
        _resource.value = value
    }
}


