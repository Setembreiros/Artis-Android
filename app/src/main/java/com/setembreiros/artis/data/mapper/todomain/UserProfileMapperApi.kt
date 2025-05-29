package com.setembreiros.artis.data.mapper.todomain

import com.setembreiros.artis.data.model.userprofile.OwnUserProfileApi
import com.setembreiros.artis.domain.model.UserProfile
import com.setembreiros.artis.data.base.Mapper

class UserProfileMapperApi: Mapper<OwnUserProfileApi, UserProfile> {
    override fun map(model: OwnUserProfileApi): UserProfile {
        return UserProfile(username = model.username, name = model.name, bio = model.bio, link = model.link, postsAmount = model.postsAmount, followersAmount = model.followersAmount, isFollowedByCurrentUser = model.isFollowedByCurrentUser)
    }
}