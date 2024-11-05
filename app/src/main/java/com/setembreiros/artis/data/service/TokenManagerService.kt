package com.setembreiros.artis.data.service

import aws.sdk.kotlin.services.cognitoidentityprovider.CognitoIdentityProviderClient
import aws.sdk.kotlin.services.cognitoidentityprovider.model.AuthFlowType
import aws.sdk.kotlin.services.cognitoidentityprovider.model.AuthenticationResultType
import aws.sdk.kotlin.services.cognitoidentityprovider.model.InitiateAuthRequest
import aws.sdk.kotlin.services.cognitoidentityprovider.model.NotAuthorizedException
import com.setembreiros.artis.BuildConfig
import com.setembreiros.artis.common.Constants.UserType
import com.setembreiros.artis.ui.account.calculateSecretHash
import javax.inject.Inject

class AuthenticationService @Inject constructor() {
    suspend fun createAuthTokens(userType: UserType, username: String, password: String): AuthenticationResultType? {
        val clientIdVal = getClientId(userType)
        val secretKey = getSecretKey(userType)
        val secretHash = calculateSecretHash(clientIdVal, secretKey, username)

        val authParas = mutableMapOf<String, String>()
        authParas["USERNAME"] = username
        authParas["PASSWORD"] = password
        authParas["SECRET_HASH"] = secretHash

        val request = InitiateAuthRequest {
            clientId = clientIdVal
            authFlow = AuthFlowType.UserPasswordAuth
            authParameters  = authParas
        }

        return getCognitoTokens(request)
    }

    suspend fun refreshAuthTokens(userType: UserType, username: String, refreshToken: String): AuthenticationResultType? {
        val clientIdVal = getClientId(userType)
        val secretKey = getSecretKey(userType)
        val secretHash = calculateSecretHash(clientIdVal, secretKey, username)

        val authParas = mutableMapOf<String, String>().apply {
            this["REFRESH_TOKEN"] = refreshToken
            this["SECRET_HASH"] = secretHash
        }

        val request = InitiateAuthRequest {
            clientId = clientIdVal
            authFlow = AuthFlowType.RefreshTokenAuth
            authParameters = authParas
        }

        return getCognitoTokens(request)
    }

    private fun getClientId(userType: UserType): String {
        return if(userType == UserType.UA) {
            BuildConfig.CLIENT_ID_UA
        } else {
            BuildConfig.CLIENT_ID_UE
        }
    }

    private fun getSecretKey(userType: UserType): String {
        return if(userType == UserType.UA) {
            BuildConfig.SECRET_KEY_UA
        } else {
            BuildConfig.SECRET_KEY_UE
        }
    }

    private suspend fun getCognitoTokens(request: InitiateAuthRequest): AuthenticationResultType? {
        CognitoIdentityProviderClient {
            region = "eu-west-3"
        }.use { identityProviderClient ->
            try {
                val result = identityProviderClient.initiateAuth(request)
                return result.authenticationResult
            } catch (e: NotAuthorizedException) {
                println("Not Authorized Error: $e")
            } catch (e: Exception) {
                println("Error occurred: $e")
            }
        }

        return null
    }
}