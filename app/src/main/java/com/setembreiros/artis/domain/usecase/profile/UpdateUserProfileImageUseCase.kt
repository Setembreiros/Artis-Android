package com.setembreiros.artis.domain.usecase.profile

import com.setembreiros.artis.BuildConfig
import com.setembreiros.artis.data.repository.UserRepository
import com.setembreiros.artis.data.service.S3Service
import com.setembreiros.artis.domain.base.Resource
import com.setembreiros.artis.domain.model.post.ConfirmPostRequest
import com.setembreiros.artis.domain.model.post.Post
import com.setembreiros.artis.domain.model.post.PostResponse
import com.setembreiros.artis.domain.model.profile.ConfirmUserProfileImage
import com.setembreiros.artis.domain.model.profile.UserProfile
import javax.inject.Inject

class UpdateUserProfileImageUseCase @Inject constructor(private val userRepository: UserRepository, private val s3Service: S3Service) {
    suspend fun invoke(username: String, image: ByteArray) : Boolean {
        return updateUserProfileImage(username, image)
    }

    private suspend fun updateUserProfileImage(username: String, image: ByteArray) : Boolean {
        return when(val response = userRepository.updateUserProfileImage(username)){
            is Resource.Success -> {
                val responseS3 = sendContentS3(image, response.value.presignedUrl)
                if(responseS3) {
                    confirmUserProfileImage(true, username)
                } else {
                    confirmUserProfileImage(false, username)
                    false
                }
            }
            is Resource.Failure -> false
        }
    }

    private suspend fun sendContentS3(content: ByteArray?, presignedUrl: String) : Boolean{
        var url = presignedUrl
        if(BuildConfig.DEBUG) {
            url = getUrlDebug(presignedUrl)
        }
        return s3Service.putContent(url, content)
    }


    private suspend fun confirmUserProfileImage(isConfirmed: Boolean, username: String): Boolean{
        val confirmUserProfileImage = ConfirmUserProfileImage(isConfirmed, username)
        return when(userRepository.confirmUserProfileImage(confirmUserProfileImage)){
            is Resource.Success -> true
            is Resource.Failure -> false
        }

    }

    private fun getUrlDebug(url: String) : String{
        val aux = url.split("4566")

        return BuildConfig.S3_URL +"/artis-bucket" + aux[1]
    }
}