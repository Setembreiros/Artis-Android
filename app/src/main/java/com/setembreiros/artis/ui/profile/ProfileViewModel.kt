package com.setembreiros.artis.ui.profile

import androidx.lifecycle.viewModelScope
import com.setembreiros.artis.domain.base.Resource
import com.setembreiros.artis.domain.model.UserProfile
import com.setembreiros.artis.domain.usecase.GetUserProfileUseCase
import com.setembreiros.artis.domain.usecase.session.GetSessionUseCase
import com.setembreiros.artis.domain.usecase.session.RemoveSessionUseCase
import com.setembreiros.artis.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val removeSessionUseCase: RemoveSessionUseCase,
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val getSessionUseCase: GetSessionUseCase
): BaseViewModel() {

    private val _profile = MutableStateFlow<UserProfile?>(null)
    val profile = _profile

    init {
        getProfile()
    }

    fun closeSession(){
        viewModelScope.launch(Dispatchers.IO) {
            removeSessionUseCase.invoke()
        }
    }

    fun getProfile(){
        viewModelScope.launch(Dispatchers.IO) {
            getSessionUseCase.invoke()?.username?.let { username->
                when(val response = getUserProfileUseCase.invoke(username)){
                    is Resource.Success -> {
                        _profile.value = response.value
                    }
                    else -> {

                    }
                }
            }
        }
    }

}