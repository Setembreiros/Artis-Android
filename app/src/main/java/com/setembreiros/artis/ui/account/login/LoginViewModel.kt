package com.setembreiros.artis.ui.account.login

import androidx.lifecycle.viewModelScope

import aws.sdk.kotlin.services.cognitoidentityprovider.CognitoIdentityProviderClient
import aws.sdk.kotlin.services.cognitoidentityprovider.model.AuthFlowType
import aws.sdk.kotlin.services.cognitoidentityprovider.model.InitiateAuthRequest
import aws.sdk.kotlin.services.cognitoidentityprovider.model.NotAuthorizedException
import com.setembreiros.artis.BuildConfig
import com.setembreiros.artis.common.UserType
import com.setembreiros.artis.domain.model.Session
import com.setembreiros.artis.domain.usecase.session.GetSessionUseCase
import com.setembreiros.artis.domain.usecase.session.SaveSessionUseCase
import com.setembreiros.artis.ui.account.calculateSecretHash
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
    private val  saveSessionUseCase: SaveSessionUseCase,
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
        val clientIdVal: String
        val secretKey: String

        if(userType == UserType.UA) {
            clientIdVal = BuildConfig.CLIENT_ID_UA
            secretKey = BuildConfig.SECRET_KEY_UA
        } else {
            clientIdVal = BuildConfig.CLIENT_ID_UE
            secretKey = BuildConfig.SECRET_KEY_UE
        }

        loading.update { true }
        val authParas = mutableMapOf<String, String>()
        authParas["USERNAME"] = _userName.value
        authParas["PASSWORD"] = _password.value
        authParas["SECRET_HASH"] = calculateSecretHash(clientIdVal, secretKey, _userName.value)

        val request = InitiateAuthRequest {
            clientId = clientIdVal
            authFlow = AuthFlowType.UserPasswordAuth
            authParameters  = authParas
        }

        CognitoIdentityProviderClient { region = "eu-west-3" }.use { identityProviderClient ->
            try {
                val result = identityProviderClient.initiateAuth(request)
                result.authenticationResult?.let {
                    val idToken = it.idToken
                    val refreshToken = it.refreshToken
                    idToken?.let {
                        refreshToken?.let {
                            storeSessionToken(refreshToken, idToken, userType)
                        _loginSuccess.update { true }
                        }
                    }?: return false
                }
                loading.update { false }
            } catch (e: NotAuthorizedException) {
                return false
            }catch (e: Exception) {
                println("Error occurred: $e")
                return false
            }
        }
        return true
    }

    private fun storeSessionToken(refreshToken: String, idToken: String, userType: UserType) {
            val session = Session(refreshToken, idToken = idToken, userType = userType)
            saveSessionUseCase.invoke(session)

    }
}