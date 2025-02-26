package com.setembreiros.artis.data.mapper.fromdomain

import com.setembreiros.artis.data.base.Mapper
import com.setembreiros.artis.data.model.profile.UpdateUserProfileImageRequestApi

class UserProfileImageMapper: Mapper<String, UpdateUserProfileImageRequestApi> {
    override fun map(model: String): UpdateUserProfileImageRequestApi {
        return UpdateUserProfileImageRequestApi(username = model)
    }
}