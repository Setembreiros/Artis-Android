package com.setembreiros.artis.data.mapper.fromdomain

import com.setembreiros.artis.data.base.Mapper
import com.setembreiros.artis.data.model.profile.UpdateUserProfileRequestApi
import com.setembreiros.artis.data.model.profile.UpdateUserProfileResponseApi
import com.setembreiros.artis.domain.model.profile.UserProfile

class UserProfileMapper: Mapper<UserProfile, UpdateUserProfileRequestApi> {
    override fun map(model: UserProfile): UpdateUserProfileRequestApi {
        return UpdateUserProfileRequestApi(username = model.username, bio = model.bio, name = model.name, link = model.link)
    }
}