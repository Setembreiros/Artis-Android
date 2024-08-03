package com.setembreiros.artis.data.mapper

import com.setembreiros.artis.data.model.UserProfileApi
import com.setembreiros.artis.domain.model.UserProfile
import com.setembreiros.artis.data.base.Mapper

class UserProfileMapperApi: Mapper<UserProfileApi, UserProfile> {
    override fun map(model: UserProfileApi): UserProfile {
        return UserProfile(username = model.username, name = model.name, bio = model.bio, link = model.link)
    }
}