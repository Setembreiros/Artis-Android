package com.setembreiros.artis.ui.profile

import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.setembreiros.artis.domain.base.Resource
import com.setembreiros.artis.domain.model.profile.UserProfile
import com.setembreiros.artis.domain.usecase.post.GetPostsUseCase
import com.setembreiros.artis.domain.usecase.profile.GetUserProfileUseCase
import com.setembreiros.artis.domain.usecase.profile.UpdateUserProfileImageUseCase
import com.setembreiros.artis.domain.usecase.profile.UpdateUserProfileUseCase
import com.setembreiros.artis.domain.usecase.session.GetSessionUseCase
import com.setembreiros.artis.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val getPostsUseCase: GetPostsUseCase,
    private val getSessionUseCase: GetSessionUseCase,
    private val updateUserProfileUseCase: UpdateUserProfileUseCase,
    private val updateUserProfileImageUseCase: UpdateUserProfileImageUseCase
): BaseViewModel() {

    private var _profile = MutableStateFlow<UserProfile?>(null)
    var profile = _profile

    private val _image = MutableStateFlow<ByteArray?>(null)
    val image = _image

    init {
        loadProfile()
    }

    private fun loadProfile(){
        viewModelScope.launch(Dispatchers.IO) {
            getSessionUseCase.invoke()?.username?.let { username->
                when(val response = getUserProfileUseCase.invoke(username)){
                    is Resource.Success -> {
                        profile.value = response.value
                    }
                    else -> {

                    }
                }
            }
        }
    }

    fun saveProfile(onResult: (Boolean) -> Unit) {
        loading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            profile.value?.let {
                val result = updateUserProfileUseCase.invoke(it)
                loading.value = false
                withContext(Dispatchers.Main) {
                    onResult(result is Resource.Success)
                }
            } ?: run {
                loading.value = false
                withContext(Dispatchers.Main) {
                    onResult(false)
                }
            }
        }
    }

    fun setProfileImage(image: ByteArray) {
        _image.value = image
    }

    fun saveProfileImage( onResult: (Boolean) -> Unit) {
        loading.value = true

        viewModelScope.launch(Dispatchers.IO) {
            Pair(profile.value, image.value).takeIf { it.first != null && it.second != null }?.let { (profileValue, imageValue) ->
                val result = updateUserProfileImageUseCase.invoke(
                    profileValue!!.username,
                    imageValue!!
                )
                loading.value = false
                withContext(Dispatchers.Main) {
                    onResult(result)
                }
            } ?: run {
                loading.value = false
                withContext(Dispatchers.Main) {
                    onResult(false)
                }
            }
        }
    }
}