package com.setembreiros.artis.data.mapper.fromdomain

import com.setembreiros.artis.data.base.Mapper
import com.setembreiros.artis.data.model.profile.ConfirmUserProfileImageRequestApi
import com.setembreiros.artis.domain.model.profile.ConfirmUserProfileImage

class ConfirmUserProfileImageMapper: Mapper<ConfirmUserProfileImage, ConfirmUserProfileImageRequestApi> {
    override fun map(model: ConfirmUserProfileImage): ConfirmUserProfileImageRequestApi {
        return ConfirmUserProfileImageRequestApi(is_confirmed = model.isConfirmed, username = model.username)
    }
}