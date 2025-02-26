package com.setembreiros.artis.data.mapper.todomain

import com.setembreiros.artis.data.base.Mapper
import com.setembreiros.artis.data.model.profile.UpdateUserProfileResponseApi
import com.setembreiros.artis.domain.model.profile.UserProfile

class UpdateUserProfileResponseMapperApi: Mapper<UpdateUserProfileResponseApi, UserProfile> {
    override fun map(model: UpdateUserProfileResponseApi): UserProfile {
        return UserProfile(username = model.username, name = model.name, bio = model.bio, link = model.link, image = null)
    }
}