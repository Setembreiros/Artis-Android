package com.setembreiros.artis.ui.profile

import androidx.lifecycle.viewModelScope
import com.setembreiros.artis.domain.usecase.GetUserProfileUseCase
import com.setembreiros.artis.domain.usecase.session.RemoveSessionUseCase
import com.setembreiros.artis.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val removeSessionUseCase: RemoveSessionUseCase,
    private val getUserProfileUseCase: GetUserProfileUseCase
): BaseViewModel() {


    fun closeSession(){
        viewModelScope.launch(Dispatchers.IO) {
            removeSessionUseCase.invoke()
        }
    }

    fun getProfile(){
        viewModelScope.launch(Dispatchers.IO) {
            getUserProfileUseCase.invoke("samuel")
        }
    }
}