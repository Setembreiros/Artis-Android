package com.setembreiros.artis.ui


import android.util.Log
import androidx.lifecycle.viewModelScope
import aws.sdk.kotlin.services.cognitoidentityprovider.CognitoIdentityProviderClient
import aws.sdk.kotlin.services.cognitoidentityprovider.model.AttributeType
import aws.sdk.kotlin.services.cognitoidentityprovider.model.ConfirmSignUpRequest
import aws.sdk.kotlin.services.cognitoidentityprovider.model.SignUpRequest
import com.setembreiros.artis.BuildConfig

import com.setembreiros.artis.common.UserType
import com.setembreiros.artis.common.isValidEmail
import com.setembreiros.artis.common.regionList
import com.setembreiros.artis.ui.base.BaseViewModel
import com.setembreiros.artis.ui.base.ResponseManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.UnsupportedEncodingException
import java.nio.charset.StandardCharsets
import java.util.Base64
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
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

    fun register(){
        if(validateData()){
            viewModelScope.launch(Dispatchers.IO) {
                if(_userType.value == UserType.UA)
                    signUp(BuildConfig.CLIENT_ID_UA,BuildConfig.SECRET_KEY_UA)
                else signUp(BuildConfig.CLIENT_ID_UE,BuildConfig.SECRET_KEY_UE)
            }
        }
    }

    fun confirmSignUp(code: String){
        viewModelScope.launch(Dispatchers.IO) {
            if(_userType.value == UserType.UA)
                confirmSignUp(BuildConfig.CLIENT_ID_UA, code)
            else confirmSignUp(BuildConfig.CLIENT_ID_UE,code)
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

                identityProviderClient.signUp(request)
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

    private suspend fun confirmSignUp(clientIdVal: String?, codeVal: String?) {
        val signUpRequest = ConfirmSignUpRequest {
            clientId = clientIdVal
            confirmationCode = codeVal
            username = _userName.value
        }

        CognitoIdentityProviderClient { region = "us-east-1" }.use { identityProviderClient ->
            identityProviderClient.confirmSignUp(signUpRequest)
            println("${_userName.value}  was confirmed")
        }
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

    private fun calculateSecretHash(userPoolClientId: String, userPoolClientSecret: String, userName: String): String {
        val macSha256Algorithm = "HmacSHA256"
        val signingKey = SecretKeySpec(
            userPoolClientSecret.toByteArray(StandardCharsets.UTF_8),
            macSha256Algorithm
        )
        try {
            val mac = Mac.getInstance(macSha256Algorithm)
            mac.init(signingKey)
            mac.update(userName.toByteArray(StandardCharsets.UTF_8))
            val rawHmac = mac.doFinal(userPoolClientId.toByteArray(StandardCharsets.UTF_8))
            return Base64.getEncoder().encodeToString(rawHmac)
        } catch (e: UnsupportedEncodingException) {
            println(e.message)
        }
        return ""
    }
}