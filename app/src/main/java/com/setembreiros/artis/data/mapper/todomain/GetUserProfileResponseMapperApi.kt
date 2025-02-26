package com.setembreiros.artis.data.mapper.todomain

import com.setembreiros.artis.data.model.profile.GetUserProfileResponseApi
import com.setembreiros.artis.domain.model.profile.UserProfile
import com.setembreiros.artis.data.base.Mapper

class GetUserProfileResponseMapperApi: Mapper<GetUserProfileResponseApi, UserProfile> {
    override fun map(model: GetUserProfileResponseApi): UserProfile {
        return UserProfile(username = model.username, name = model.name, bio = model.bio, link = model.link, image = null)
    }
}