package com.setembreiros.artis.ui.post

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.lifecycle.viewModelScope
import com.setembreiros.artis.common.Constants
import com.setembreiros.artis.domain.model.post.Post
import com.setembreiros.artis.domain.model.post.PostContent
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

    private val _resource = MutableStateFlow<Uri?>(null)
    val resource = _resource

    private val _type = MutableStateFlow(Constants.ContentType.IMAGE)
    val type = _type

    fun publish(context: Context){
        _resource.value?.let {
            loading.update { true }
            val size = getFileSizeFromUri(context, it)
            val post = Post(
                metadata = PostMetadata(
                    postId = "",
                    username = session.value!!.username,
                    title = _title.value,
                    description = _description.value,
                    type = _type.value,
                    size = size,
                    createdAt = "",
                    lastUpdated = ""
                ),
                content = PostContent(
                    uriContent = it,
                    content = null,
                    thumbnail = null
                )
            )
            viewModelScope.launch(Dispatchers.IO) {
                createPostUseCase.invoke(post, context) { progress ->
                    UploadProgressManager.updateProgress(progress)
                }
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

    fun setResource(value: Uri?){
        _resource.value = value
    }

    fun getResource() : Uri?{
        return _resource.value
    }

    fun setType(value: Constants.ContentType){
        _type.value = value
    }

    private fun getFileSizeFromUri(context: Context, uri: Uri): Long {
        var fileSize: Long = 0
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            val sizeIndex = it.getColumnIndex(OpenableColumns.SIZE)
            if (sizeIndex != -1) {
                it.moveToFirst()
                fileSize = it.getLong(sizeIndex)
            }
        }
        return fileSize / 1024 / 1024 // return MB
    }
}


