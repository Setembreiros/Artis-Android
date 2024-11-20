package com.setembreiros.artis.ui.account.login

import androidx.lifecycle.viewModelScope

import com.setembreiros.artis.common.Constants.UserType
import com.setembreiros.artis.domain.usecase.session.CreateSessionUseCase
import com.setembreiros.artis.domain.usecase.session.GetSessionUseCase
import com.setembreiros.artis.ui.base.BaseViewModel
import com.setembreiros.artis.ui.base.ResponseManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val  createSessionUseCase: CreateSessionUseCase,
    private val getSessionUseCase: GetSessionUseCase
): BaseViewModel() {

    private val _userName = MutableStateFlow("")
    val userName = _userName

    private val _password = MutableStateFlow("")
    val password = _password

    private val _loginSuccess = MutableStateFlow(false)
    val loginSuccess = _loginSuccess

    init {
        println(getSessionUseCase.invoke())
    }

    fun setUserName(value: String){
        this._userName.update { value }
    }

    fun setPassword(value: String) {
        _password.update { value }
    }

    fun login(){
        viewModelScope.launch(Dispatchers.IO) {
            var authorized = signIn(UserType.UA)
            if(!authorized)
                authorized = signIn(UserType.UE)

            if(!authorized)
                responseManager.value = ResponseManager(show = true, false, message = "invalid_credentials")

            loading.update { false }
        }
    }

    private suspend fun signIn(userType: UserType): Boolean {
        loading.update { true }
        val isLogged = createSessionUseCase.invoke(_userName.value, _password.value, userType)
        if (isLogged) _loginSuccess.update { true }
        loading.update { false }

        return isLogged
    }
}