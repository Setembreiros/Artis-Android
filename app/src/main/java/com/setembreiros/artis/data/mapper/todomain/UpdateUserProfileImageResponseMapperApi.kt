package com.setembreiros.artis.data.mapper.todomain

import com.setembreiros.artis.data.base.Mapper
import com.setembreiros.artis.data.model.profile.ConfirmUserProfileImageResponseApi
import com.setembreiros.artis.domain.model.profile.ConfirmUserProfileImageResponse

class UpdateUserProfileImageResponseMapperApi: Mapper<ConfirmUserProfileImageResponseApi, ConfirmUserProfileImageResponse> {
    override fun map(model: ConfirmUserProfileImageResponseApi): ConfirmUserProfileImageResponse {
        return ConfirmUserProfileImageResponse(presignedUrl = model.presignedUrl)
    }
}