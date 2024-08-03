package com.setembreiros.artis.domain.usecase.session

import aws.sdk.kotlin.services.cognitoidentityprovider.CognitoIdentityProviderClient
import aws.sdk.kotlin.services.cognitoidentityprovider.revokeToken
import com.setembreiros.artis.BuildConfig
import com.setembreiros.artis.common.Constants.UserType
import com.setembreiros.artis.data.repository.SessionRepository
import javax.inject.Inject

class RemoveSessionUseCase @Inject constructor(private val sessionRepository: SessionRepository) {
    suspend fun invoke(){
        val session = sessionRepository.getSession()
        val clientIdVal: String
        val secretKey: String

        if(session?.userType  == UserType.UA) {
            clientIdVal = BuildConfig.CLIENT_ID_UA
            secretKey = BuildConfig.SECRET_KEY_UA
        } else {
            clientIdVal = BuildConfig.CLIENT_ID_UE
            secretKey = BuildConfig.SECRET_KEY_UE
        }

        CognitoIdentityProviderClient { region = "eu-west-3" }.use { identityProviderClient ->
            try {
                sessionRepository.getSession()
                identityProviderClient.revokeToken {
                    clientId = clientIdVal
                    clientSecret = secretKey
                    token = session?.refreshToken
                }
            } catch (e: Exception) {
                println("Error occurred: $e")
            }
        }

        sessionRepository.removeSession()
    }
}