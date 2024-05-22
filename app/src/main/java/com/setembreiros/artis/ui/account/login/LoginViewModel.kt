package com.setembreiros.artis.ui.account.login

import android.content.Context
import androidx.lifecycle.viewModelScope
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import aws.sdk.kotlin.services.cognitoidentityprovider.CognitoIdentityProviderClient
import aws.sdk.kotlin.services.cognitoidentityprovider.model.AuthFlowType
import aws.sdk.kotlin.services.cognitoidentityprovider.model.InitiateAuthRequest
import aws.sdk.kotlin.services.cognitoidentityprovider.model.NotAuthorizedException
import com.setembreiros.artis.BuildConfig
import com.setembreiros.artis.ui.account.calculateSecretHash
import com.setembreiros.artis.ui.account.getSessionToken
import com.setembreiros.artis.ui.base.BaseViewModel
import com.setembreiros.artis.ui.base.ResponseManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    @ApplicationContext private val context: Context
): BaseViewModel() {

    private val _userName = MutableStateFlow("")
    val userName = _userName

    private val _password = MutableStateFlow("")
    val password = _password

    fun setUserName(value: String){
        this._userName.update { value }
    }

    fun setPassword(value: String) {
        _password.update { value }
    }

    fun login(){
        viewModelScope.launch(Dispatchers.IO) {
            var authorized = signIn(BuildConfig.CLIENT_ID_UA, BuildConfig.SECRET_KEY_UA)
            if(!authorized)
                authorized = signIn(BuildConfig.CLIENT_ID_UE, BuildConfig.SECRET_KEY_UE)

            if(!authorized)
                responseManager.value = ResponseManager(show = true, false, message = "invalid_credentials")

            loading.update { false }
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
                    storeSessionToken(sessionToken)
                    responseManager.value = ResponseManager(show = true, false, message = "user_logged")
                    println(getSessionToken(context))
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

    private fun storeSessionToken(token: String?) {
        val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        val sharedPreferences = EncryptedSharedPreferences.create(
            context,
            "secret_shared_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        with(sharedPreferences.edit()) {
            putString("session_token", token)
            apply()
        }
    }
}