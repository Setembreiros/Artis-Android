package com.setembreiros.artis.ui.account.register


import android.util.Log
import androidx.lifecycle.viewModelScope
import aws.sdk.kotlin.services.cognitoidentityprovider.CognitoIdentityProviderClient
import aws.sdk.kotlin.services.cognitoidentityprovider.model.AttributeType
import aws.sdk.kotlin.services.cognitoidentityprovider.model.AuthFlowType
import aws.sdk.kotlin.services.cognitoidentityprovider.model.ConfirmSignUpRequest
import aws.sdk.kotlin.services.cognitoidentityprovider.model.InitiateAuthRequest
import aws.sdk.kotlin.services.cognitoidentityprovider.model.NotAuthorizedException
import aws.sdk.kotlin.services.cognitoidentityprovider.model.SignUpRequest
import com.setembreiros.artis.BuildConfig

import com.setembreiros.artis.common.Constants.UserType
import com.setembreiros.artis.common.isValidEmail
import com.setembreiros.artis.common.Constants.regionList
import com.setembreiros.artis.domain.model.Session
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
class RegisterViewModel @Inject constructor(
    private val saveSessionUseCase: SaveSessionUseCase
): BaseViewModel() {

    private val _userName = MutableStateFlow("")
    val userName = _userName

    private val _email = MutableStateFlow("")
    val email = _email

    private val _password = MutableStateFlow("")
    val password = _password

    private val _name = MutableStateFlow("")
    val name = _name

    private val _lastName = MutableStateFlow("")
    val lastName = _lastName

    private val _region = MutableStateFlow(regionList[0])
    val region = _region

    private val _registerView = MutableStateFlow(true)
    val registerView = _registerView

    private val _code = MutableStateFlow("")
    val code = _code

    private val _registerSuccess = MutableStateFlow(false)
    val registerSuccess = _registerSuccess

    private val _userType = MutableStateFlow(UserType.UE)

    fun setUserName(value: String){
        this._userName.update { value }
    }

    fun setEmail(value: String) {
        _email.update { value }
    }

    fun setPassword(value: String) {
        _password.update { value }
    }

    fun setName(value: String) {
        _name.update { value }
    }

    fun setLastName(value: String) {
        _lastName.update { value }
    }

    fun setRegin(value: String) {
        region.update { value }
    }

    fun setUserType(value: UserType) {
        _userType.update { value }
    }

    fun setCode(value: String) {
        _code.update { value }
    }

    fun register(){
        if(validateData()){
            viewModelScope.launch(Dispatchers.IO) {
                if(_userType.value == UserType.UA)
                    signUp(BuildConfig.CLIENT_ID_UA, BuildConfig.SECRET_KEY_UA)
                else signUp(BuildConfig.CLIENT_ID_UE, BuildConfig.SECRET_KEY_UE)
            }
        }
    }

    fun confirmSignUp(){
        viewModelScope.launch(Dispatchers.IO) {
            if(_userType.value == UserType.UA)
                confirmSignUp(BuildConfig.CLIENT_ID_UA, _code.value, BuildConfig.SECRET_KEY_UA)
            else confirmSignUp(BuildConfig.CLIENT_ID_UE, _code.value, BuildConfig.SECRET_KEY_UE)
        }
    }

    private fun validateData(): Boolean{
        val pattern = Regex("^(?=.*[A-Z])(?=.*[\\W_])(?=.*[0-9]).{8,}$")
        responseManager.update { it.copy(show = false) }
        if(!_email.value.isValidEmail()){
            responseManager.value = ResponseManager(show = true, false, message = "invalid_email")
            return false
        }
        if(!pattern.matches(_password.value)){
            responseManager.value = ResponseManager(show = true, false, message = "invalid_pass")
            return false
        }
        return true
    }

    private suspend fun signUp(clientIdVal: String, secretKey: String) {
        loading.update { true }
        val attrs = mutableListOf<AttributeType>()

        val attributeTypeEmail = AttributeType {
            this.name = "email"
            this.value = _email.value
        }
        attrs.add(attributeTypeEmail)

        val attributeTypeRegion = AttributeType {
            this.name = "custom:region"
            this.value = _region.value
        }
        attrs.add(attributeTypeRegion)

        if(_userType.value == UserType.UA){
            val attributeTypeName = AttributeType {
                this.name = "name"
                this.value = _name.value+" "+lastName.value

            }
            attrs.add(attributeTypeName)
        }

        val secretVal = calculateSecretHash(clientIdVal, secretKey, _userName.value)

        val request = SignUpRequest {
            userAttributes = attrs
            username = this@RegisterViewModel._userName.value
            clientId = clientIdVal
            password = _password.value
            secretHash = secretVal
        }
        CognitoIdentityProviderClient { region = "eu-west-3" }.use { identityProviderClient ->

            try {
                val response = identityProviderClient.signUp(request)
                Log.d("DOG",response.toString())

               _registerView.update { false }
                responseManager.value = ResponseManager(show = true, false, message = "account_created")
                loading.update { false }

            } catch (e: Exception) {
                loading.update { false }
                println("Error occurred: $e")
                e.message?.let {
                    getErrorMessage(it)
                }
            }
        }
    }

    private suspend fun confirmSignUp(clientIdVal: String, codeVal: String, secretKey: String) {
        loading.update { true }
        val secretVal = calculateSecretHash(clientIdVal, secretKey, _userName.value)

        val signUpRequest = ConfirmSignUpRequest {
            clientId = clientIdVal
            confirmationCode = codeVal
            username = _userName.value
            secretHash = secretVal
        }

        CognitoIdentityProviderClient { region = "eu-west-3" }.use { identityProviderClient ->
                    try {
                        identityProviderClient.confirmSignUp(signUpRequest)
                        loading.update { false }
                        signIn(clientIdVal, secretKey)
                        println("${_userName.value}  was confirmed")
                    }catch (e : Exception){
                        loading.update { false }
                        println(e.toString())
                    }

                }
    }

    private suspend fun signIn(clientIdVal: String, secretKey: String): Boolean {
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
                    val sessionToken = it.idToken
                    sessionToken?.let {
                        _registerSuccess.update { true }
                        storeSessionToken(sessionToken)
                        _registerSuccess.update { true }
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

    private fun storeSessionToken(token: String) {
        val session = Session(token = token)
        saveSessionUseCase.invoke(session)

    }

    private fun getErrorMessage(message: String){
        if(message.contains("EXISTING_USERNAME")){
            responseManager.value = ResponseManager(show = true, false, message = "EXISTING_USERNAME")
            return
        }
        if(message.contains("EXISTING_EMAIL")){
            responseManager.value = ResponseManager(show = true, false, message = "EXISTING_EMAIL")
            return
        }

        responseManager.value = ResponseManager(show = true, true, message = message)
    }
}